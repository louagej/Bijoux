package net.louage.bijoux.server;

import java.util.ArrayList;
import java.util.List;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import net.louage.bijoux.constants.Constants;
import net.louage.bijoux.constants.Installation;
import net.louage.bijoux.model.Tracking;
//import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

public class TrackingAsyncCreateUpdate extends AsyncTask<String[], Integer, Tracking> {

	public static final String RES_UPD_OK = "Tracking was successfully saved";
	public static final String RES_UPD_NOK = "Tracking wasn't saved";
	public static final String RES_UPD_NULL = "Could not connect to the server";
	private static final String TAG_CALL_SUCCESFULL = "result";
	private static final String TAG_RES_MESSAGE = "result_message";
	private String serverResult;

	JSONParser jParser = new JSONParser();
	//private ProgressDialog mProgressDialog;
	public static final int DIALOG_GET_PROGRESS = 0;
	private Context context;
	private String[] parameters;
	private AsTskObjectCompleteListener<Tracking> listener;
	private int tracking_id=-1;

	public TrackingAsyncCreateUpdate(Context context, AsTskObjectCompleteListener<Tracking> listener, String[] params) {
		super();
		this.context = context;
		this.listener = listener;
		parameters = params;
		/*mProgressDialog = new ProgressDialog(context);
		mProgressDialog.setMessage("processing tracking information...");
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
	protected Tracking doInBackground(String[]... params) {
		//String tag = "UpdateTrackings doInBackground";
		// Building Parameters
		// Please make sure the spellings of the keys are correct
		List<NameValuePair> params1 = new ArrayList<NameValuePair>();
		params1.add(new BasicNameValuePair("method", "createOrUpdateTracking"));
		params1.add(new BasicNameValuePair("user_id", parameters[0]));
		params1.add(new BasicNameValuePair("uuid", Installation.getInstallationID(context)));
		params1.add(new BasicNameValuePair("tracking_id", parameters[1]));
		params1.add(new BasicNameValuePair("track_date_time", parameters[2]));
		params1.add(new BasicNameValuePair("tour_id", parameters[3]));
		params1.add(new BasicNameValuePair("latitude", parameters[4]));
		params1.add(new BasicNameValuePair("longitude", parameters[5]));
		params1.add(new BasicNameValuePair("accuracy", parameters[6]));
		params1.add(new BasicNameValuePair("altitude", parameters[7]));
		params1.add(new BasicNameValuePair("speed", parameters[8]));
		Log.d("tracking_id before makeHttpRequest: ", ""+tracking_id);
		tracking_id=Integer.parseInt(parameters[9]);
		// getting JSON string from URL
		JSONObject json = jParser.makeHttpRequest(Constants.SERVICE_URL, "GET",	params1);
		// Check your log cat for JSON response
		// Log.d("Check login: ", json.toString());
		if (!(json == null)) {
			try {
				// Check if getMyTrackings was successfully
				int success = json.getInt(TAG_CALL_SUCCESFULL);
				serverResult = json.getString(TAG_RES_MESSAGE);
				if (success == 1) {
					//boolean deleted = json.getBoolean(TAG_UPD_SUCCESFULL);
					JSONObject json_tracking = json.getJSONObject(Tracking.TRACKING);
					Tracking tracking = new Tracking();
					tracking=JSONParser.getTrackingfromJson(json_tracking);
					if (tracking!=null) {
						tracking.setTracking_id(tracking_id);
						Log.d("tracking_id after makeHttpRequest: ", ""+tracking_id);
					}
					return tracking;
				} else {
					serverResult= serverResult + ": " + RES_UPD_NOK;
					return null;
				}
			} catch (JSONException e) {
				serverResult= serverResult + ": " + RES_UPD_NOK;
				Log.d("Try Catch: ", RES_UPD_NOK);
				return null;
			}
		} else {
			serverResult= serverResult + ": " + RES_UPD_NULL;
			Log.d("json is null", RES_UPD_NULL);
			return null;
		}
	}

	@Override
	protected void onPostExecute(Tracking tracking) {
		super.onPostExecute(tracking);
		//mProgressDialog.dismiss();
		Log.d("onPostExecute: ", serverResult);
		//Toast.makeText(context, serverResult, Toast.LENGTH_LONG).show();
		listener.onTaskComplete(tracking);
	}

	protected void onProgressUpdate(String... progress) {
		Log.d("ANDRO_ASYNC", progress[0]);
		//mProgressDialog.setProgress(Integer.parseInt(progress[0]));
	}

}
