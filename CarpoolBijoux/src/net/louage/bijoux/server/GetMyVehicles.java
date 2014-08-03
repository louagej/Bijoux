package net.louage.bijoux.server;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import net.louage.bijoux.constants.Constants;
import net.louage.bijoux.constants.DateTime;
import net.louage.bijoux.constants.Installation;
import net.louage.bijoux.model.Country;
import net.louage.bijoux.model.Vehicle;
import net.louage.bijoux.model.VehicleType;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

public class GetMyVehicles extends AsyncTask<String[], Integer, ArrayList<Vehicle>> {
	public static final String RES_GET_OK = "Get was successfully";
	public static final String RES_GET_NOK = "Get was unsuccessfully";
	public static final String RES_GET_NULL = "Could not connect to the server";
	private static final String TAG_GET_SUCCESFULL = "result";
	private static final String TAG_VEHICLE_INFO_RESULT = "vehicle_list";
	private static final String TAG_USER_VEHICLES = "vehicles";
	
	JSONParser jParser = new JSONParser();
	private ProgressDialog mProgressDialog;
	public static final int DIALOG_GET_PROGRESS = 0;
	private Context context;
	private String[] parameters;
	
	private AsTskArrayListCompleteListener<Vehicle> listener;

	public GetMyVehicles(Context context, AsTskArrayListCompleteListener<Vehicle> listener,String[] params) {
		this.context = context;
		this.listener = listener;
		parameters = params;
		mProgressDialog = new ProgressDialog(context);
		mProgressDialog.setMessage("Getting vehicle list...");
		mProgressDialog.setIndeterminate(false);
		mProgressDialog.setMax(100);
		mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		mProgressDialog.setCancelable(true);
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		mProgressDialog.show();
	}

	@Override
	protected ArrayList<Vehicle> doInBackground(String[]... params) {
		String tag = "GetMyVehicles doInBackground";
		// Building Parameters
		List<NameValuePair> params1 = new ArrayList<NameValuePair>();
		// Set the method name
		params1.add(new BasicNameValuePair("method", "getMyVehicles"));
		// Please make sure the spellings of the keys are correct
		params1.add(new BasicNameValuePair("user_id", parameters[0]));

		// Check if installation was already done by getting Universally
		// unique identifier
		String instalId = Installation.id(context);
		Log.d(tag + " Installation.id uuid: ", instalId);
		UUID uid = UUID.fromString(instalId);
		Long lng = uid.getMostSignificantBits();
		Log.d(tag, "getMostSignificantBits(): " + lng);
		instalId = Long.toString(lng);
		Log.d(tag, "Long.toString(lng): " + instalId);
		params1.add(new BasicNameValuePair("uuid", instalId));

		Log.d("Check params1: ", params1.toString());
		// getting JSON string from URL
		JSONObject json = jParser.makeHttpRequest(Constants.SERVICE_URL,
				"GET", params1);
		// Check your log cat for JSON response
		// Log.d("Check login: ", json.toString());
		if (!(json == null)) {
			try {
				// Check if getMyVehicles was successfully
				int success = json.getInt(TAG_GET_SUCCESFULL);
				if (success == 1) {
					Log.d("success getMyVehicles: ", json.toString());
					JSONObject json_vehicle_list = json.getJSONObject(TAG_VEHICLE_INFO_RESULT);
					Log.d("JSON vehicle_list: ", json_vehicle_list.toString());
					JSONArray json_vehicles = json_vehicle_list.getJSONArray(TAG_USER_VEHICLES);
					Log.d("JSON vehicles: ", json_vehicles.toString());
					ArrayList<Vehicle> vehicles = new ArrayList<Vehicle>();
					for (int i = 0; i < json_vehicles.length(); i++) {
						JSONObject json_vehicle = json_vehicles.getJSONObject(i);
						Vehicle vhc = new Vehicle();
						vhc.setVehicle_id(json_vehicle.getInt(Vehicle.TAG_ID));
						vhc.setLicenseplate(json_vehicle.getString(Vehicle.TAG_LICENSEPLATE));
						Country ct = new Country(json_vehicle.getString(Vehicle.TAG_COUNTRY),json_vehicle.getString(Vehicle.TAG_COUNTRY));
						vhc.setCountry(ct);
						vhc.setNumberOfPassengers(json_vehicle.getInt(Vehicle.TAG_NUMBER_OF_PASS));
						vhc.setBrand(json_vehicle.getString(Vehicle.TAG_BRAND));
						VehicleType vt = new VehicleType(json_vehicle.getString(Vehicle.TAG_VEHICLE_TYPE));
						vhc.setType(vt);
						vhc.setUser_id(json_vehicle.getInt(Vehicle.TAG_USER_ID));
						Date vehicleUpdatedAt = DateTime.getDateTimeSQLiteString(json_vehicle.getString(Vehicle.TAG_UPDATED_AT));
						vhc.setUpdate_at(vehicleUpdatedAt);
						vehicles.add(vhc);
					}
					Log.d("Try Catch: ", "OK building Object vehciles");
					return vehicles;
				} else {
					Log.d("Try Catch: ", "NOK building Object vehciles");
					return null;
				}
			} catch (JSONException e) {
				Log.d("JSON Null: ", "No connection to server could be made");
				e.printStackTrace();
				return null;
			}
		} else {
			return null;
		}

	}

	protected void onProgressUpdate(String... progress) {
		Log.d("ANDRO_ASYNC", progress[0]);
		mProgressDialog.setProgress(Integer.parseInt(progress[0]));
	}

	protected void onPostExecute(ArrayList<Vehicle> vehicles) {
		super.onPostExecute(vehicles);
		mProgressDialog.dismiss();
		Log.d("onPostExecute: ", "OK");
		listener.onTaskComplete(vehicles);
	}

}
