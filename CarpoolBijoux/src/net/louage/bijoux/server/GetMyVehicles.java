package net.louage.bijoux.server;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import net.louage.bijoux.constants.Constants;
import net.louage.bijoux.constants.Installation;
import net.louage.bijoux.model.Vehicle;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
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
	JSONParser jParser = new JSONParser();
	//private ProgressDialog mProgressDialog;
	public static final int DIALOG_GET_PROGRESS = 0;
	private Context context;
	private String[] parameters;
	
	private AsyncTaskCompleteListener<Vehicle> listener;

	public GetMyVehicles(Context context, AsyncTaskCompleteListener<Vehicle> listener,String[] params) {
		this.context = context;
		this.listener = listener;
		/*mProgressDialog = new ProgressDialog(context);
		mProgressDialog.setMessage("Getting vehicle list...");
		mProgressDialog.setIndeterminate(false);
		mProgressDialog.setMax(100);
		mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		mProgressDialog.setCancelable(true);*/
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		//mProgressDialog.show();
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
					//TODO build ArraList from retrieve JSON with Vehicle objects
					return null;
				} else {
					return null;
				}
			} catch (JSONException e) {
				e.printStackTrace();
				return null;
			}
		} else {
			return null;
		}

	}

	protected void onProgressUpdate(String... progress) {
		Log.d("ANDRO_ASYNC", progress[0]);
		//mProgressDialog.setProgress(Integer.parseInt(progress[0]));
	}

	protected void onPostExecute(ArrayList<Vehicle> vehicles) {
		super.onPostExecute(vehicles);
		//mProgressDialog.dismiss();
		listener.onTaskComplete(vehicles);
	}

}
