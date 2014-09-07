package net.louage.bijoux.server;

import java.util.ArrayList;
import java.util.List;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import net.louage.bijoux.constants.Constants;
import net.louage.bijoux.constants.Installation;
import net.louage.bijoux.model.Tour;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
//import android.util.Log;

public class TourAsyncDelete extends AsyncTask<String[], Integer, Tour> {

	public static final String RES_DEL_OK = "Delete was successfully";
	public static final String RES_DEL_NOK = "Delete was unsuccessfully";
	public static final String RES_DEL_NULL = "Could not connect to the server";
	private static final String TAG_CALL_SUCCESFULL = "result";
	private static final String TAG_DEL_SUCCESFULL = "result_message";
	private String resultAsyncTask="";
	JSONParser jParser = new JSONParser();
	private ProgressDialog mProgressDialog;
	public static final int DIALOG_GET_PROGRESS = 0;
	private Context context;
	private String[] parameters;
	private AsTskObjectCompleteListener<Tour> listener;

	public TourAsyncDelete(Context context,
			AsTskObjectCompleteListener<Tour> listener, String[] params) {
		super();
		this.context = context;
		this.listener = listener;
		parameters = params;
		mProgressDialog = new ProgressDialog(context);
		mProgressDialog.setMessage("Deleting tour...");
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
		//String tag = "TourAsyncDelete doInBackground";
		// Building Parameters
		// Please make sure the spellings of the keys are correct
		List<NameValuePair> params1 = new ArrayList<NameValuePair>();
		params1.add(new BasicNameValuePair("method", "deleteTour"));
		params1.add(new BasicNameValuePair("user_id", parameters[0]));
		params1.add(new BasicNameValuePair("uuid", Installation.getInstallationID(context)));
		params1.add(new BasicNameValuePair("tour_id", parameters[1]));
		//getting JSON string from URL
		JSONObject json = jParser.makeHttpRequest(Constants.SERVICE_URL, "GET",	params1);
		// Check your log cat for JSON response
		if (!(json == null)) {
			try {
				// Check if getMyTours was successfully
				int success = json.getInt(TAG_CALL_SUCCESFULL);
				if (success == 1) {
					boolean deleted = json.getBoolean(TAG_DEL_SUCCESFULL);
					if (deleted) {
						//Log.d("Try Catch: ", RES_DEL_OK);
						resultAsyncTask = RES_DEL_OK;
						Tour tr = new Tour();
						tr.setTour_id(Integer.parseInt(parameters[1]));
						return tr;
					} else {
						//Log.d("boolean deleted = false: ", RES_DEL_NOK);
						resultAsyncTask = RES_DEL_NOK;
						return null;
					}
				} else {
				}
			} catch (JSONException e) {
				//Log.d("Try Catch: ", RES_DEL_NOK);
				resultAsyncTask = RES_DEL_NOK;
				return null;
			}
		} else {
			//Log.d("!(json == null)", RES_DEL_NULL);
			resultAsyncTask = RES_DEL_NULL;
			return null;
		}

		return null;
	}

	@Override
	protected void onPostExecute(Tour tour) {
		super.onPostExecute(tour);
		mProgressDialog.dismiss();
		//Log.d("TourAsyncDelete onPostExecute: ", resultAsyncTask);
		listener.onTaskComplete(tour);
	}

	protected void onProgressUpdate(String... progress) {
		//Log.d("ANDRO_ASYNC", progress[0]);
		mProgressDialog.setProgress(Integer.parseInt(progress[0]));
	}
	
	

}
