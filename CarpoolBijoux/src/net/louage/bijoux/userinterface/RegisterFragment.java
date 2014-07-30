package net.louage.bijoux.userinterface;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import net.louage.bijoux.R;
import net.louage.bijoux.constants.Constants;
import net.louage.bijoux.constants.Installation;
import net.louage.bijoux.server.JSONParser;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class RegisterFragment extends Fragment implements View.OnClickListener{
	public static final String ARG_NAVDRAWER_NUMBER = "number";
	public static final String RES_REGISTRATION_OK = "Login was succesfull";
	public static final String RES_REGISTRATION_NOK = "Login was unsuccesfull";
	public static final String RES_REGISTRATION_NULL = "Could not connect to the server";
	TextView txt_unknown_account;
	EditText txtLastName;
	EditText txtFirstName;
	EditText txtEmail;
	EditText txtPhone;
	EditText txtRegUsername;
	EditText txtRegPassword;
	EditText txtInfo;
	EditText txtDriverLicense;
	Button btnRegister;

	public RegisterFragment() {
		// Empty constructor required for fragment subclasses
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		int i = getArguments().getInt(ARG_NAVDRAWER_NUMBER);
		MainActivity ma = (MainActivity) getActivity();
		String selNavDrawItem = ma.getmNavDrawerTitles()[i];
		View registerView = inflater.inflate(R.layout.fragment_register,
				container, false);
		getActivity().setTitle(selNavDrawItem);
		
		txtLastName = (EditText) registerView.findViewById(R.id.txtLastName);
		txtFirstName = (EditText) registerView.findViewById(R.id.txtFirstName);
		txtEmail = (EditText) registerView.findViewById(R.id.txtEmail);
		txtPhone = (EditText) registerView.findViewById(R.id.txtPhone);
		txtRegUsername = (EditText) registerView.findViewById(R.id.txtRegUsername);
		txtRegPassword = (EditText) registerView.findViewById(R.id.txtRegPassword);
		txtInfo = (EditText) registerView.findViewById(R.id.txtInfo);
		txtDriverLicense = (EditText) registerView.findViewById(R.id.txtDriverLicense);
		
		btnRegister = (Button) registerView.findViewById(R.id.btnRegister);
		btnRegister.setOnClickListener(this);
		return registerView;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnRegister:
			//MainActivity ma = (MainActivity) getActivity();
			//Toast.makeText(ma, "Register button was clicked",Toast.LENGTH_LONG).show();
			String lastname = txtLastName.getText().toString();
			String firstname = txtFirstName.getText().toString();
			String email = txtEmail.getText().toString();
			String phone = txtPhone.getText().toString();
			String username = txtRegUsername.getText().toString();
			String password = txtRegPassword.getText().toString();
			String info = txtInfo.getText().toString();
			String driverlicense = txtDriverLicense.getText().toString();
			String[] params = { lastname, firstname, email, phone, username, password, info, driverlicense};
			RegisterLogin registerLogin = new RegisterLogin(getActivity(), params);
			registerLogin.execute(params);
			break;
		default:
			break;
		}

	}
	
	class RegisterLogin extends AsyncTask<String[], Integer, String> {
		public static final int DIALOG_DOWNLOAD_PROGRESS = 0;
		private static final String TAG_REGISTER_SUCCESFULL = "result";

		private ProgressDialog mProgressDialog;
		JSONParser jParser = new JSONParser();

		private Context context;
		private String[] parameters;

		public RegisterLogin(Context context, String[] params) {
			this.context = context;
			parameters = params;
			mProgressDialog = new ProgressDialog(context);
			mProgressDialog.setMessage("Checking login info...");
			mProgressDialog.setIndeterminate(false);
			mProgressDialog.setMax(100);
			mProgressDialog
					.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			mProgressDialog.setCancelable(true);

		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			mProgressDialog.show();
		}

		@Override
		protected String doInBackground(String[]... params) {
			String tag = "RegisterLogin doInBackground";
			// Building Parameters
			List<NameValuePair> params1 = new ArrayList<NameValuePair>();
			// Set the method name
			params1.add(new BasicNameValuePair("method", "registerLogin"));
			// Please make sure the spellings of the keys are correct
			params1.add(new BasicNameValuePair("lastname", parameters[0]));
			params1.add(new BasicNameValuePair("firstname", parameters[1]));
			params1.add(new BasicNameValuePair("email", parameters[2]));
			params1.add(new BasicNameValuePair("phone", parameters[3]));
			params1.add(new BasicNameValuePair("username", parameters[4]));
			params1.add(new BasicNameValuePair("password", parameters[5]));
			params1.add(new BasicNameValuePair("info", parameters[6]));
			params1.add(new BasicNameValuePair("driverlicense", parameters[7]));
			
			// Check if installation was already done by getting Universally
			// unique identifier
			String instalId = Installation.id(context);
			Log.d(tag + " Installation.id uuid: ", instalId);
			// instalId = instalId.replace("-", "");
			// Long lng = Long.parseLong(instalId, 16);
			UUID uid = UUID.fromString(instalId);
			Long lng = uid.getMostSignificantBits();
			Log.d(tag, "getMostSignificantBits(): " + lng);
			instalId = Long.toString(lng);
			Log.d(tag, "Long.toString(lng): " + instalId);
			params1.add(new BasicNameValuePair("uuid", instalId));

			Log.d("Check params1: ", params1.toString());
			// getting JSON string from URL
			JSONObject json = jParser.makeHttpRequest(Constants.SERVICE_URL, "GET", params1);
			// Check your log cat for JSON reponse
			// Log.d("Check login: ", json.toString());
			if (!(json == null)) {
				try {
					// Check if login was successfull
					int success = json.getInt(TAG_REGISTER_SUCCESFULL);
					if (success == 1) {
						return RES_REGISTRATION_OK;
					} else {
						return RES_REGISTRATION_NOK;
					}
				} catch (JSONException e) {
					e.printStackTrace();
					return RES_REGISTRATION_NOK;
				}
			} else {
				return RES_REGISTRATION_NULL;
			}

		}

		protected void onProgressUpdate(String... progress) {
			Log.d("ANDRO_ASYNC", progress[0]);
			mProgressDialog.setProgress(Integer.parseInt(progress[0]));
		}

		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			mProgressDialog.dismiss();
			getActivity().finish();
			startActivity(getActivity().getIntent());
			Toast.makeText(getActivity(), result,Toast.LENGTH_LONG).show();	
		}
	}
}
