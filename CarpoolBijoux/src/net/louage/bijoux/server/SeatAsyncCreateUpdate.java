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
import net.louage.bijoux.model.Seat;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

public class SeatAsyncCreateUpdate extends AsyncTask<String[], Integer, Seat>{

	public static final String RES_UPD_OK = "Seat was successfully saved";
	public static final String RES_UPD_NOK = "Seat wasn't saved";
	public static final String RES_UPD_NULL = "Could not connect to the server";
	private static final String TAG_CALL_SUCCESFULL = "result";
	private static final String TAG_UPD_SUCCESFULL = "result_message";

	JSONParser jParser = new JSONParser();
	private ProgressDialog mProgressDialog;
	public static final int DIALOG_GET_PROGRESS = 0;
	private Context context;
	private String[] parameters;
	private AsTskObjectCompleteListener<Seat> listener;

	public SeatAsyncCreateUpdate(Context context,
			AsTskObjectCompleteListener<Seat> listener, String[] params) {
		super();
		this.context = context;
		this.listener = listener;
		parameters = params;
		mProgressDialog = new ProgressDialog(context);
		mProgressDialog.setMessage("processing seat...");
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
	protected Seat doInBackground(String[]... params) {
		String tag = "UpdateSeats doInBackground";
		// Building Parameters
		List<NameValuePair> params1 = new ArrayList<NameValuePair>();
		// Set the method name
		params1.add(new BasicNameValuePair("method", "createOrUpdateSeat"));
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
		params1.add(new BasicNameValuePair("seat_id", parameters[1]));
		params1.add(new BasicNameValuePair("created_by_user_id", parameters[2]));
		params1.add(new BasicNameValuePair("device_id", parameters[3]));
		params1.add(new BasicNameValuePair("tour_id", parameters[4]));
		//Here we must convert the field to passenger_id 
		//It could be mistaken by the parameter user_id we provide in all http calls
		//that identifies the caller.
		params1.add(new BasicNameValuePair("passenger_id", parameters[5]));
		params1.add(new BasicNameValuePair("status", parameters[6]));
		params1.add(new BasicNameValuePair("paid", parameters[7]));
		params1.add(new BasicNameValuePair("pickupAddress", parameters[8]));
		params1.add(new BasicNameValuePair("dropoffAddress", parameters[9]));
		// getting JSON string from URL
		JSONObject json = jParser.makeHttpRequest(Constants.SERVICE_URL, "GET",	params1);
		// Check your log cat for JSON response
		// Log.d("Check login: ", json.toString());
		if (!(json == null)) {
			try {
				// Check if getMySeats was successfully
				int success = json.getInt(TAG_CALL_SUCCESFULL);
				if (success == 1) {
					JSONObject json_seat = json.getJSONObject(TAG_UPD_SUCCESFULL);
					if (json_seat!=null) {
						Log.d("Try Catch: ", RES_UPD_OK);
						Seat st = new Seat();
						st=JSONParser.getSeat(json_seat);
						return st;
					} else {
						Log.d("Try Catch: ", RES_UPD_NOK);
						return null;
					}
				} else {
				}
			} catch (JSONException e) {
				Log.d("Try Catch: ", RES_UPD_NOK);
				return null;
			}
		} else {
			Log.d("json is null", RES_UPD_NULL);
			return null;
		}

		return null;
	}

	@Override
	protected void onPostExecute(Seat seat) {
		super.onPostExecute(seat);
		mProgressDialog.dismiss();
		Log.d("onPostExecute: ", "OK");
		listener.onTaskComplete(seat);
	}

	protected void onProgressUpdate(String... progress) {
		Log.d("ANDRO_ASYNC", progress[0]);
		mProgressDialog.setProgress(Integer.parseInt(progress[0]));
	}
}
