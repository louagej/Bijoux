package net.louage.bijoux.server;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import net.louage.bijoux.constants.Constants;
import net.louage.bijoux.constants.Installation;
import net.louage.bijoux.model.Vehicle;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

public class DeleteVehicle extends AsyncTask<String[], Integer, Vehicle> {

	public static final String RES_DEL_OK = "Get was successfully";
	public static final String RES_DEL_NOK = "Get was unsuccessfully";
	public static final String RES_DEL_NULL = "Could not connect to the server";
	private static final String TAG_CALL_SUCCESFULL = "result";
	private static final String TAG_DEL_SUCCESFULL = "result_message";

	JSONParser jParser = new JSONParser();
	private ProgressDialog mProgressDialog;
	public static final int DIALOG_GET_PROGRESS = 0;
	private Context context;
	private String[] parameters;
	private AsTskObjectCompleteListener<Vehicle> listener;

	public DeleteVehicle(Context context,
			AsTskObjectCompleteListener<Vehicle> listener, String[] params) {
		super();
		this.context = context;
		this.listener = listener;
		parameters = params;
		mProgressDialog = new ProgressDialog(context);
		mProgressDialog.setMessage("Deleting vehicle...");
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
	protected Vehicle doInBackground(String[]... params) {
		String tag = "GetMyVehicles doInBackground";
		// Building Parameters
		List<NameValuePair> params1 = new ArrayList<NameValuePair>();
		// Set the method name
		params1.add(new BasicNameValuePair("method", "deleteVehicle"));
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
		params1.add(new BasicNameValuePair("vehicle_id", parameters[1]));

		Log.d("Check params1: ", params1.toString());
		// getting JSON string from URL
		JSONObject json = jParser.makeHttpRequest(Constants.SERVICE_URL, "GET",
				params1);
		// Check your log cat for JSON response
		// Log.d("Check login: ", json.toString());
		if (!(json == null)) {
			try {
				// Check if getMyVehicles was successfully
				int success = json.getInt(TAG_CALL_SUCCESFULL);
				if (success == 1) {
					boolean deleted = json.getBoolean(TAG_DEL_SUCCESFULL);
					if (deleted) {
						Log.d("Try Catch: ", "Vehicle was deleted");
						Vehicle vh = new Vehicle();
						vh.setVehicle_id(Integer.parseInt(parameters[1]));
						return vh;
					} else {
						Log.d("Try Catch: ", "Server didn't deleted vehicle");
						return null;
					}
				} else {
				}
			} catch (JSONException e) {
				Log.d("Try Catch: ", "Vehicle wasn't deleted");
				return null;
			}
		} else {
			Log.d("json is null", "Server couldn't be called");
			return null;
		}

		return null;
	}

	@Override
	protected void onPostExecute(Vehicle vehicle) {
		super.onPostExecute(vehicle);
		mProgressDialog.dismiss();
		Log.d("onPostExecute: ", "OK");
		listener.onTaskComplete(vehicle);
	}

	protected void onProgressUpdate(String... progress) {
		Log.d("ANDRO_ASYNC", progress[0]);
		mProgressDialog.setProgress(Integer.parseInt(progress[0]));
	}
	
	

}
