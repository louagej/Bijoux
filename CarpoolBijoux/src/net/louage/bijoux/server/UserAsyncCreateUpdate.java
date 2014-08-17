package net.louage.bijoux.server;

import java.util.ArrayList;
import java.util.List;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import net.louage.bijoux.constants.Constants;
import net.louage.bijoux.constants.Installation;
import net.louage.bijoux.model.User;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

public class UserAsyncCreateUpdate extends AsyncTask<String[], Integer, User> {

	public static final String RES_UPD_OK = "User was successfully saved";
	public static final String RES_UPD_NOK = "User wasn't saved";
	public static final String RES_UPD_NULL = "Could not connect to the server";
	private static final String TAG_CALL_SUCCESFULL = "result";
	private static final String TAG_RES_MESSAGE = "result_message";
	private String serverResult;

	JSONParser jParser = new JSONParser();
	private ProgressDialog mProgressDialog;
	public static final int DIALOG_GET_PROGRESS = 0;
	private Context context;
	private String[] parameters;
	private AsTskObjectCompleteListener<User> listener;

	public UserAsyncCreateUpdate(Context context, AsTskObjectCompleteListener<User> listener, String[] params) {
		super();
		this.context = context;
		this.listener = listener;
		parameters = params;
		mProgressDialog = new ProgressDialog(context);
		mProgressDialog.setMessage("processing user information...");
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
	protected User doInBackground(String[]... params) {
		//String tag = "UpdateUsers doInBackground";
		// Building Parameters
		// Please make sure the spellings of the keys are correct
		List<NameValuePair> params1 = new ArrayList<NameValuePair>();
		params1.add(new BasicNameValuePair("method", "createOrUpdateUser"));
		params1.add(new BasicNameValuePair("user_id", parameters[0]));
		params1.add(new BasicNameValuePair("uuid", Installation.getInstallationID(context)));
		params1.add(new BasicNameValuePair("user_id_to_cr_upd", parameters[1]));
		params1.add(new BasicNameValuePair("lastname", parameters[2]));
		params1.add(new BasicNameValuePair("firstname", parameters[3]));
		params1.add(new BasicNameValuePair("email", parameters[4]));
		params1.add(new BasicNameValuePair("phone", parameters[5]));
		params1.add(new BasicNameValuePair("info", parameters[6]));
		params1.add(new BasicNameValuePair("driverlicense", parameters[7]));
		params1.add(new BasicNameValuePair("approved", parameters[8]));
		// getting JSON string from URL
		JSONObject json = jParser.makeHttpRequest(Constants.SERVICE_URL, "GET",	params1);
		// Check your log cat for JSON response
		// Log.d("Check login: ", json.toString());
		if (!(json == null)) {
			try {
				// Check if getMyUsers was successfully
				int success = json.getInt(TAG_CALL_SUCCESFULL);
				serverResult = json.getString(TAG_RES_MESSAGE);
				if (success == 1) {
					//boolean deleted = json.getBoolean(TAG_UPD_SUCCESFULL);
					JSONObject json_user = json.getJSONObject(User.USER);
					User user = new User();
					user=JSONParser.getUserfromJson(json_user);
					return user;
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
	protected void onPostExecute(User user) {
		super.onPostExecute(user);
		mProgressDialog.dismiss();
		Log.d("onPostExecute: ", serverResult);
		Toast.makeText(context, serverResult, Toast.LENGTH_LONG).show();
		listener.onTaskComplete(user);
	}

	protected void onProgressUpdate(String... progress) {
		Log.d("ANDRO_ASYNC", progress[0]);
		mProgressDialog.setProgress(Integer.parseInt(progress[0]));
	}

}
