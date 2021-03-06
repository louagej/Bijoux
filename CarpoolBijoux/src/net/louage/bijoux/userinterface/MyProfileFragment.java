package net.louage.bijoux.userinterface;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import net.louage.bijoux.R;
import net.louage.bijoux.constants.Constants;
import net.louage.bijoux.constants.Installation;
import net.louage.bijoux.constants.SharedPreferences;
import net.louage.bijoux.model.User;
import net.louage.bijoux.model.Vehicle;
import net.louage.bijoux.server.AsTskArrayListCompleteListener;
import net.louage.bijoux.server.VehiclesAsyncGetMyVehicles;
import net.louage.bijoux.server.JSONParser;
import android.annotation.SuppressLint;
import android.app.ListFragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.os.AsyncTask;
import android.os.Bundle;
//import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MyProfileFragment extends ListFragment implements
		OnItemClickListener, OnClickListener,
		AsTskArrayListCompleteListener<Vehicle> {
	public static final String ARG_NAVDRAWER_NUMBER = "number";
	public static final String RES_UPDATE_OK = "Update was successfully";
	public static final String RES_UPDATE_NOK = "Update was unsuccessfully";
	public static final String RES_UPDATE_NULL = "Could not connect to the server";
	Button btn_my_profile_update;
	Button btn_create_new_vehicle;
	EditText etxt_my_profile_lastname;
	EditText etxt_my_profile_firstname;
	EditText etxt_my_profile_email;
	EditText etxt_my_profile_phone;
	EditText etxt_my_profile_info;
	EditText etxt_my_profile_driverlicense;
	ArrayList<Vehicle> vehicles = new ArrayList<Vehicle>();
	TypedArray menuIcons;
	CustomIconAdapter adapter;
	private List<RowIconItem> rowIconItems;
	User appUser;
	MainActivity ma;

	public MyProfileFragment() {
		// Empty constructor required for fragment subclasses
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		int i = getArguments().getInt(ARG_NAVDRAWER_NUMBER);
		ma = (MainActivity) getActivity();
		String selNavDrawItem = ma.getmNavDrawerTitles()[i];
		View myProfileView = inflater.inflate(R.layout.fragment_my_profile,
				container, false);
		getActivity().setTitle(selNavDrawItem);
		etxt_my_profile_lastname = (EditText) myProfileView
				.findViewById(R.id.etxt_my_profile_lastname);
		etxt_my_profile_firstname = (EditText) myProfileView
				.findViewById(R.id.etxt_my_profile_firstname);
		etxt_my_profile_email = (EditText) myProfileView
				.findViewById(R.id.etxt_my_profile_email);
		etxt_my_profile_phone = (EditText) myProfileView
				.findViewById(R.id.etxt_my_profile_phone);
		etxt_my_profile_info = (EditText) myProfileView
				.findViewById(R.id.etxt_my_profile_info);
		etxt_my_profile_driverlicense = (EditText) myProfileView
				.findViewById(R.id.etxt_my_profile_driverlicense);

		btn_my_profile_update = (Button) myProfileView.findViewById(R.id.btn_my_profile_update);
		btn_my_profile_update.setOnClickListener(this);
		
		btn_create_new_vehicle = (Button) myProfileView.findViewById(R.id.btn_create_new_vehicle);
		btn_create_new_vehicle.setOnClickListener(this);
		
		appUser = SharedPreferences.getUser(ma);

		String[] params = { Integer.toString(appUser.getUser_id()) };
		new VehiclesAsyncGetMyVehicles(ma, new GetMyVehiclesTaskCompleteListener(), params)
				.execute();
		return myProfileView;
	}
	
	

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Vehicle vh = vehicles.get(position);
		//String message = vh.getBrand() + " - " + vh.getType().getType() + ": " + vh.getLicenseplate() + " was clicked.";
		//Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
		Intent intent = new Intent(ma, VehicleActivity.class);
		intent.putExtra("Vehicle", vh);
		startActivity(intent);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_my_profile_update:
			// Toast.makeText(ma,
			// "Register button was clicked",Toast.LENGTH_LONG).show();
			String user_id = Integer.toString(appUser.getUser_id());
			String lastname = etxt_my_profile_lastname.getText().toString();
			appUser.setLastname(lastname);
			String firstname = etxt_my_profile_firstname.getText().toString();
			appUser.setFirstname(firstname);
			String email = etxt_my_profile_email.getText().toString();
			appUser.setEmail(email);
			String phone = etxt_my_profile_phone.getText().toString();
			appUser.setPhone(phone);
			//String username = appUser.getUsername();
			String username = SharedPreferences.getUsername(ma);
			//String password = appUser.getPassword();
			String password = SharedPreferences.getPassword(ma);
			String info = etxt_my_profile_info.getText().toString();
			appUser.setInfo(info);
			String driverlicense = etxt_my_profile_driverlicense.getText()
					.toString();
			appUser.setDriverlicense(driverlicense);
			String[] params = { lastname, firstname, email, phone, username,
					password, info, driverlicense, user_id };
			UpdateMyProfile updateMyProfile = new UpdateMyProfile(
					getActivity(), params);
			updateMyProfile.execute(params);
			break;
		case R.id.btn_create_new_vehicle:
			Intent intent = new Intent(ma, VehicleActivity.class);
			startActivity(intent);
			break;
		default:
			break;
		}

	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		etxt_my_profile_lastname.setText(appUser.getLastname());
		etxt_my_profile_firstname.setText(appUser.getFirstname());
		etxt_my_profile_email.setText(appUser.getEmail());
		etxt_my_profile_phone.setText(appUser.getPhone());
		etxt_my_profile_info.setText(appUser.getInfo());
		etxt_my_profile_driverlicense.setText(appUser.getDriverlicense());
	}

	class UpdateMyProfile extends AsyncTask<String[], Integer, String> {
		public static final int DIALOG_UPDATE_PROGRESS = 0;
		private static final String TAG_UDPATE_SUCCESFULL = "result";

		private ProgressDialog mProgressDialog;
		JSONParser jParser = new JSONParser();

		private Context context;
		private String[] parameters;

		public UpdateMyProfile(Context context, String[] params) {
			this.context = context;
			parameters = params;
			mProgressDialog = new ProgressDialog(context);
			mProgressDialog.setMessage("Updating your profile...");
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
		protected String doInBackground(String[]... params) {
			//String tag = "UpdateMyProfile doInBackground";
			// Building Parameters
			List<NameValuePair> params1 = new ArrayList<NameValuePair>();
			// Set the method name
			params1.add(new BasicNameValuePair("method", "updateMyProfile"));
			// Please make sure the spellings of the keys are correct
			params1.add(new BasicNameValuePair("lastname", parameters[0]));
			params1.add(new BasicNameValuePair("firstname", parameters[1]));
			params1.add(new BasicNameValuePair("email", parameters[2]));
			params1.add(new BasicNameValuePair("phone", parameters[3]));
			params1.add(new BasicNameValuePair("username", parameters[4]));
			params1.add(new BasicNameValuePair("password", parameters[5]));
			params1.add(new BasicNameValuePair("info", parameters[6]));
			params1.add(new BasicNameValuePair("driverlicense", parameters[7]));
			params1.add(new BasicNameValuePair("user_id", parameters[8]));

			// Check if installation was already done by getting Universally
			// unique identifier
			String instalId = Installation.id(context);
			//Log.d(tag + " Installation.id uuid: ", instalId);
			// instalId = instalId.replace("-", "");
			// Long lng = Long.parseLong(instalId, 16);
			UUID uid = UUID.fromString(instalId);
			Long lng = uid.getMostSignificantBits();
			//Log.d(tag, "getMostSignificantBits(): " + lng);
			instalId = Long.toString(lng);
			//Log.d(tag, "Long.toString(lng): " + instalId);
			params1.add(new BasicNameValuePair("uuid", instalId));

			//Log.d("Check params1: ", params1.toString());
			// getting JSON string from URL
			JSONObject json = jParser.makeHttpRequest(Constants.SERVICE_URL,
					"GET", params1);
			// Check your log cat for JSON reponse
			// Log.d("Check login: ", json.toString());
			if (!(json == null)) {
				try {
					// Check if login was successfull
					int success = json.getInt(TAG_UDPATE_SUCCESFULL);
					if (success == 1) {
						return RES_UPDATE_OK;
					} else {
						return RES_UPDATE_NOK;
					}
				} catch (JSONException e) {
					e.printStackTrace();
					return RES_UPDATE_NOK;
				}
			} else {
				return RES_UPDATE_NULL;
			}

		}

		protected void onProgressUpdate(String... progress) {
			//Log.d("ANDRO_ASYNC", progress[0]);
			mProgressDialog.setProgress(Integer.parseInt(progress[0]));
		}

		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			mProgressDialog.dismiss();
			if (result.equals(RES_UPDATE_OK)) {
				getActivity().finish();
				startActivity(getActivity().getIntent());
				Toast.makeText(getActivity(), result, Toast.LENGTH_LONG).show();
				SharedPreferences.setUser(context, appUser);
			} else {
				Toast.makeText(getActivity(), result, Toast.LENGTH_LONG).show();
			}

		}

	}

	@SuppressLint("DefaultLocale")
	@Override
	public void onTaskComplete(ArrayList<Vehicle> vhs) {
		//Log.d("onTaskComplete: ", "MyProfileFragment Started");
		vehicles.clear();
		vehicles = vhs;
		rowIconItems = new ArrayList<RowIconItem>();
		for (int i = 0; i < vehicles.size(); i++) {
			Vehicle vh = vehicles.get(i);
			String vhData = vh.getBrand() + " - " + vh.getType().getType() + ": " + vh.getLicenseplate();
			String logo = vh.getBrand().replaceAll(" ", "_")
					.toLowerCase(Locale.getDefault());
			int id = ma.getResources().getIdentifier(logo, "drawable",
					ma.getPackageName());
			RowIconItem items = new RowIconItem(vhData, id);
			rowIconItems.add(items);
		}

		adapter = new CustomIconAdapter(getActivity(), rowIconItems);
		//Log.d("setListAdapter: ", "MyProfileFragment Started");
		setListAdapter(adapter);
		getListView().setOnItemClickListener(this);
	}

	class GetMyVehiclesTaskCompleteListener implements
			AsTskArrayListCompleteListener<Vehicle> {
		@Override
		public void onTaskComplete(ArrayList<Vehicle> vhs) {
			//Log.d("onTaskComplete: ", "FetchMyVehicleTaskCompleteListener Started");
			MyProfileFragment.this.onTaskComplete(vhs);			
		}
	}

	@Override
	public void onResume() {
		String[] params = { Integer.toString(appUser.getUser_id()) };
		new VehiclesAsyncGetMyVehicles(ma, new GetMyVehiclesTaskCompleteListener(), params).execute();
		super.onResume();
	}

}
