package net.louage.bijoux.server;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import net.louage.bijoux.constants.Constants;
import net.louage.bijoux.constants.DateTime;
import net.louage.bijoux.constants.Installation;
import net.louage.bijoux.model.Seat;
import net.louage.bijoux.model.Tour;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
//import android.util.Log;
import android.widget.Toast;

public class TourAsyncCreateUpdate extends AsyncTask<String[], Integer, Tour>{

	public static final String RES_UPD_OK = "Tour was successfully saved";
	public static final String RES_UPD_NOK = "Tour wasn't saved";
	public static final String RES_UPD_NULL = "Could not connect to the server";
	private static final String TAG_CALL_SUCCESFULL = "result";
	private static final String TAG_UPD_SUCCESFULL = "result_message";

	JSONParser jParser = new JSONParser();
	private ProgressDialog mProgressDialog;
	public static final int DIALOG_GET_PROGRESS = 0;
	private Context context;
	private String[] parameters;
	private AsTskObjectCompleteListener<Tour> listener;
	private String resultmessage="";

	public TourAsyncCreateUpdate(Context context,
			AsTskObjectCompleteListener<Tour> listener, String[] params) {
		super();
		this.context = context;
		this.listener = listener;
		parameters = params;
		mProgressDialog = new ProgressDialog(context);
		mProgressDialog.setMessage("processing tour...");
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
	protected Tour doInBackground(String[]... params) {
		//String tag = "UpdateTours doInBackground";
		// Building Parameters
		List<NameValuePair> params1 = new ArrayList<NameValuePair>();
		// Please make sure the spellings of the keys are correct
		params1.add(new BasicNameValuePair("method", "createOrUpdateTour"));		
		params1.add(new BasicNameValuePair("user_id", parameters[0]));
		params1.add(new BasicNameValuePair("uuid", Installation.getInstallationID(context)));
		params1.add(new BasicNameValuePair("tour_id", parameters[1]));
		params1.add(new BasicNameValuePair("date", parameters[2]));
		params1.add(new BasicNameValuePair("time", parameters[3]));
		params1.add(new BasicNameValuePair("tour_user_id", parameters[4]));
		params1.add(new BasicNameValuePair("vehicle_id", parameters[5]));
		params1.add(new BasicNameValuePair("team_id", parameters[6]));
		params1.add(new BasicNameValuePair("from_address", parameters[7]));
		params1.add(new BasicNameValuePair("from_post_code", parameters[8]));
		params1.add(new BasicNameValuePair("from_city", parameters[9]));
		params1.add(new BasicNameValuePair("from_country", parameters[10]));
		params1.add(new BasicNameValuePair("to_address", parameters[11]));
		params1.add(new BasicNameValuePair("to_post_code", parameters[12]));
		params1.add(new BasicNameValuePair("to_city", parameters[13]));
		params1.add(new BasicNameValuePair("to_country", parameters[14]));
		params1.add(new BasicNameValuePair("seat_price", parameters[15]));
		// getting JSON trring from URL
		JSONObject json = jParser.makeHttpRequest(Constants.SERVICE_URL, "GET",	params1);
		// Check your log cat for JSON response
		// Log.d("Check login: ", json.toString());
		if (!(json == null)) {
			try {
				// Check if getMyTours was successfully
				int success = json.getInt(TAG_CALL_SUCCESFULL);
				if (success == 1) {
					//boolean deleted = json.getBoolean(TAG_UPD_SUCCESFULL);
					//int result = json.getInt(TAG_UPD_SUCCESFULL);
					JSONObject json_tour = json.getJSONObject(TAG_UPD_SUCCESFULL);
					if (json_tour!=null) {
						//String tag="TourAsyncCreateUpdate json_tour!=null: ";
						//Log.d(tag, "true");
						Tour tr = new Tour();
						tr.setTour_id(json_tour.getInt(Tour.TAG_ID));
						Date tourDate = DateTime.getDateFormString(json_tour.getString(Tour.TAG_DATE));
						tr.setDate(tourDate);
						//Log.d(tag, "tourDate: "+tourDate);
						Date tourTime = DateTime.getTimeFormString(json_tour.getString(Tour.TAG_TIME));
						tr.setTime(tourTime);
						//Log.d(tag, "tourTime: "+tourTime);
						//Build user
						JSONObject json_user = json_tour.getJSONObject(Tour.TAG_USER);
						//Log.d(tag, json_user.toString());
						tr.setUser(JSONParser.getUserfromJson(json_user));
						//Build Vehicle
						JSONObject json_vehicle = json_tour.getJSONObject(Tour.TAG_TOUR_VEHICLE);
						tr.setVehicle(JSONParser.getVehiclefromJson(json_vehicle, context));
						//Log.d(tag, json_vehicle.toString());
						tr.setSeat_price(json_tour.getDouble(Tour.TAG_SEAT_PRICE));
						//Build Teamobject						
						JSONObject json_team = json_tour.getJSONObject(Tour.TAG_TOUR_TEAM);
						tr.setTeam(JSONParser.getTeamfromJson(json_team));
						//Log.d(tag, json_team.toString());
						//Build From Address
						tr.setFromAddress(JSONParser.getFromAddress(json_tour, context));
						//Build To Address
						tr.setToAddress(JSONParser.getToAddress(json_tour, context));
						//Get ArrayList of seats registered for this tour
						JSONObject json_tour_seats = json_tour.getJSONObject(Tour.TAG_TOUR_SEATS);
						JSONArray json_seats = json_tour_seats.getJSONArray(Tour.TAG_SEATS);
						ArrayList<Seat>seats=new ArrayList<Seat>();
						for (int j = 0; j < json_seats.length(); j++) {
							JSONObject json_seat = json_seats.getJSONObject(j);
							seats.add(JSONParser.getSeat(json_seat));
						}
						tr.setSeats(seats);
						resultmessage=RES_UPD_OK;
						return tr;
					} else {
						//Log.d("if (json_tour!=null): ", "json_tour=null");
						resultmessage=RES_UPD_NOK;
						return null;
					}
				} else {
					//Log.d("if (json succes tag: ", "0");
					resultmessage=RES_UPD_NOK;
				}
			} catch (JSONException e) {
				//Log.d("Try Catch: ", RES_UPD_NOK);
				resultmessage=RES_UPD_NOK;
				return null;
			}
		} else {
			//Log.d("json is null", RES_UPD_NULL);
			resultmessage=RES_UPD_NOK;
			return null;
		}

		return null;
	}

	@Override
	protected void onPostExecute(Tour tour) {
		//String tag="TourAsyncCreateUpdate onPostExecute";
		super.onPostExecute(tour);
		mProgressDialog.dismiss();
		//Log.d(tag, tour.toString());
		Toast.makeText(context, resultmessage, Toast.LENGTH_LONG).show();
		listener.onTaskComplete(tour);
	}

	protected void onProgressUpdate(String... progress) {
		//Log.d("ANDRO_ASYNC", progress[0]);
		mProgressDialog.setProgress(Integer.parseInt(progress[0]));
	}
}
