package net.louage.bijoux.userinterface;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import net.louage.bijoux.R;
import net.louage.bijoux.constants.Constants;
import net.louage.bijoux.constants.DateTime;
import net.louage.bijoux.constants.Installation;
import net.louage.bijoux.constants.SharedPreferences;
import net.louage.bijoux.model.Country;
import net.louage.bijoux.model.Role;
import net.louage.bijoux.model.Team;
import net.louage.bijoux.model.User;
import net.louage.bijoux.model.Vehicle;
import net.louage.bijoux.model.VehicleType;
import net.louage.bijoux.server.JSONParser;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
//import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class LoginFragment extends Fragment implements View.OnClickListener{
	public static final String ARG_NAVDRAWER_NUMBER = "number";
	public static final String RES_LOGIN_OK = "Login was succesfull";
	public static final String RES_LOGIN_NOK = "Login was unsuccesfull";
	public static final String RES_LOGIN_NULL = "Could not connect to the server";
	EditText txtUsername;
	EditText txtPassword;
	Button btnLogin;
	TextView txtLoginResult;
	Boolean startApplication=false;

	public LoginFragment() {
		// Empty constructor required for fragment subclasses
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		int i = getArguments().getInt(ARG_NAVDRAWER_NUMBER);
		MainActivity ma = (MainActivity) getActivity();
		String selNavDrawItem = ma.getmNavDrawerTitles()[i];
		View loginView = inflater.inflate(R.layout.fragment_login,
				container, false);
		getActivity().setTitle(selNavDrawItem);
		txtUsername = (EditText) loginView.findViewById(R.id.txtUsername);
		txtPassword = (EditText) loginView.findViewById(R.id.txtPassword);
		btnLogin = (Button) loginView.findViewById(R.id.btnLogin);
		btnLogin.setOnClickListener(this);
		txtLoginResult = (TextView) loginView.findViewById(R.id.txtLoginResult);
		if (SharedPreferences.getUser(getActivity()) != null) {
			// login already exist
			String usrnm = SharedPreferences.getUsername(getActivity());
			if (usrnm != null) {
				txtUsername.setText(usrnm);
			}
			String pswrd = SharedPreferences.getPassword(getActivity());
			if (pswrd != null) {
				txtPassword.setText(pswrd);
			}
			btnLogin.setText(R.string.frg_login_logout);
		} else {
			//The EditText fields txtUsername and txtPassword doesn't need to be filled
			//The user must fill these fields with his login and password
		}
		return loginView;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnLogin:
			if (SharedPreferences.getUser(getActivity()) != null) {
				SharedPreferences.deleteSharedPreferences(getActivity());
				txtUsername.setText("");
				txtPassword.setText("");
				btnLogin.setText(R.string.frg_login_login);
				getActivity().finish();
				startActivity(getActivity().getIntent());
			} else {
				String usrn = txtUsername.getText().toString();
				String passw = txtPassword.getText().toString();
				String[] params = { usrn, passw };
				CheckLogin checkLogin = new CheckLogin(getActivity(),
						params);
				checkLogin.execute(params);
			}

			break;
		default:
			break;
		}

	}

	class CheckLogin extends AsyncTask<String[], Integer, String> {
		private static final String TAG_ACCOUNT_WITH_TEAM = "We're almost there...your account will soon be matched with your team!";
		private static final String NO_OF_TEAMS = "no_of_teams";
		private static final String ACCOUNT_WAITING_FOR_APPROVAL = "Your account is still waiting for approval";
		private static final String NO_OF_ROLES = "no_of_roles";
		public static final int DIALOG_DOWNLOAD_PROGRESS = 0;
		private static final String TAG_LOGIN_SUCCESFULL = "result";
		private static final String TAG_RESULT_MESSAGE = "result_message";
		private static final String TAG_USER_INFO_RESULT = "user_info";
		private static final String TAG_USER_APPROVED = "approved";
		private static final String TAG_USER = "user";
		private static final String TAG_USER_ROLES = "user_roles";
		private static final String TAG_USER_VEHICLES = "user_vehicles";
		private static final String TAG_USER_MEMBER_OF = "user_member_of";
		private static final String TAG_USER_TEAMLEADER_OF = "user_teamleader_of";
		private String serverResult="";

		private ProgressDialog mProgressDialog;
		JSONParser jParser = new JSONParser();

		private Context context;
		private String[] parameters;

		public CheckLogin(Context context, String[] params) {
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
			//String tag = "CheckLogin doInBackground";
			// Building Parameters
			List<NameValuePair> params1 = new ArrayList<NameValuePair>();
			// Set the method name
			params1.add(new BasicNameValuePair("method", "verifyLogin"));
			// Please make sure the spellings of the keys are correct
			// Set the username and password
			params1.add(new BasicNameValuePair("username", parameters[0]));
			params1.add(new BasicNameValuePair("password", parameters[1]));
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
			JSONObject json = jParser.makeHttpRequest(
					Constants.SERVICE_URL, "GET", params1);
			// Check your log cat for JSON reponse
			// Log.d("Check login: ", json.toString());
			if (!(json == null)) {
				try {
					// Check if login was successfull
					int success_user_id = json.getInt(TAG_LOGIN_SUCCESFULL);
					if (success_user_id != 0) {
						// login OK
						//Log.d("success login: ", json.toString());
						JSONObject json_user_info = json.getJSONObject(TAG_USER_INFO_RESULT);
						int approved=json_user_info.getInt(TAG_USER_APPROVED);
						if (approved==0) {
							serverResult=ACCOUNT_WAITING_FOR_APPROVAL;
						} else {
							JSONObject json_user = json_user_info.getJSONObject(TAG_USER);
							//Log.d("success login: ", json_user.toString());
							User user = new User();
							user.setUser_id(json_user.getInt(User.TAG_ID));
							Date date = DateTime.getDateFormString(json_user.getString(User.TAG_ACTIVATION));
							user.setActivation(date);
							user.setDriverlicense(json_user.getString(User.TAG_DRIVERLICENSE));
							user.setEmail(json_user.getString(User.TAG_EMAIL));
							user.setFirstname(json_user.getString(User.TAG_FIRSTNAME));
							user.setInfo(json_user.getString(User.TAG_INFO));
							user.setLastname(json_user.getString(User.TAG_LASTNAME));
							user.setPhone(json_user.getString(User.TAG_PHONE));
							Date updatedDate = DateTime.getDateTimeString(json_user.getString(User.TAG_UPDATED_AT));
							user.setUpdate_at(updatedDate);
							JSONObject json_user_roles = json_user_info.getJSONObject(TAG_USER_ROLES);
							int noOfRoles=json_user_roles.getInt(NO_OF_ROLES);
							if (noOfRoles==0) {
								serverResult=ACCOUNT_WAITING_FOR_APPROVAL;
							} else {
								JSONArray json_roles = json_user_roles.getJSONArray(User.TAG_ROLES);
								ArrayList<Role> roles = new ArrayList<Role>();
								
								for (int i = 0; i < json_roles.length(); i++) {
									JSONObject json_role = json_roles.getJSONObject(i);
									String rolename = json_role.getString(Role.TAG_USER_ROLENAME);
									Role role = new Role(rolename);
									roles.add(role);
								}
								user.setRoles(roles);

								JSONObject json_user_vehicles = json_user_info.getJSONObject(TAG_USER_VEHICLES);
								JSONArray json_vehicles = json_user_vehicles.getJSONArray(User.TAG_VEHICLES);
								ArrayList<Vehicle> vehicles = new ArrayList<Vehicle>();
								for (int i = 0; i < json_vehicles.length(); i++) {
									JSONObject json_vehicle = json_vehicles.getJSONObject(i);
									Vehicle vhc = new Vehicle();
									vhc.setVehicle_id(json_vehicle.getInt(Vehicle.TAG_ID));
									vhc.setLicenseplate(json_vehicle.getString(Vehicle.TAG_LICENSEPLATE));
									Country ct = new Country(json_vehicle.getString(Vehicle.TAG_COUNTRY), json_vehicle.getString(Vehicle.TAG_COUNTRY));
									vhc.setCountry(ct);
									vhc.setNumberOfPassengers(json_vehicle.getInt(Vehicle.TAG_NUMBER_OF_PASS));
									vhc.setBrand(json_vehicle.getString(Vehicle.TAG_BRAND));
									VehicleType vt = new VehicleType( json_vehicle.getString(Vehicle.TAG_VEHICLE_TYPE));
									vhc.setType(vt);
									vhc.setUser_id(json_vehicle.getInt(Vehicle.TAG_USER_ID));
									Date vehicleUpdatedAt = DateTime.getDateTimeString(json_user.getString(Vehicle.TAG_UPDATED_AT));
									vhc.setUpdate_at(vehicleUpdatedAt);
									vehicles.add(vhc);
								}
								user.setVehicles(vehicles);

								JSONObject json_user_member_of = json_user_info.getJSONObject(TAG_USER_MEMBER_OF);
								int noOfTeams = json_user_member_of.getInt(NO_OF_TEAMS);
								if (noOfTeams==0) {
									serverResult=TAG_ACCOUNT_WITH_TEAM;
								} else {
									JSONArray json_teams = json_user_member_of.getJSONArray(User.TAG_MEMBER_OF);
									ArrayList<Team> teams = new ArrayList<Team>();
									for (int i = 0; i < json_teams.length(); i++) {
										JSONObject json_team = json_teams.getJSONObject(i);
										String teamname = json_team.getString(Team.TAG_USER_TEAMNAME);
										int teamid = Integer.parseInt(json_team.getString(Team.TAG_ID));
										Team team = new Team(teamname, teamid);
										teams.add(team);
									}
									user.setMemberOf(teams);

									JSONObject json_user_teamleder_of = json_user_info.getJSONObject(TAG_USER_TEAMLEADER_OF);
									JSONArray json_teamleaderteams = json_user_teamleder_of.getJSONArray(User.TAG_MANAGER_OF);
									//Log.d(tag, "json_teamleaderteams.length(): "+json_teamleaderteams.length());
									ArrayList<Team> managedTeams = new ArrayList<Team>();
									for (int i = 0; i < json_teamleaderteams.length(); i++) {
										JSONObject json_team = json_teamleaderteams.getJSONObject(i);
										String teamname = json_team.getString(Team.TAG_USER_TEAMNAME);
										int teamid = Integer.parseInt(json_team.getString(Team.TAG_ID));
										Team team = new Team(teamname, teamid);
										managedTeams.add(team);
										//Log.d(tag, "teamname "+i+": "+team.getTeamname());
									}
									user.setManagerOf(managedTeams);

									net.louage.bijoux.constants.SharedPreferences.setUserId(context, user.getUser_id());
									net.louage.bijoux.constants.SharedPreferences.setUserName(context, parameters[0]);
									net.louage.bijoux.constants.SharedPreferences.setPassword(context, parameters[1]);
									net.louage.bijoux.constants.SharedPreferences.setUser(context, user);
									startApplication=true;
								}				
							}
						}
						
						return serverResult;
					} else {
						//Log.d("failure login: ", json.toString());
						serverResult=json.getString(TAG_RESULT_MESSAGE);
						return serverResult;
					}
				} catch (JSONException e) {
					e.printStackTrace();
					serverResult=RES_LOGIN_NOK;
					return serverResult;
				}
			} else {
				serverResult=RES_LOGIN_NULL;
				return serverResult;
			}

		}

		protected void onProgressUpdate(String... progress) {
			//Log.d("ANDRO_ASYNC", progress[0]);
			mProgressDialog.setProgress(Integer.parseInt(progress[0]));
		}

		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			mProgressDialog.dismiss();
			if (startApplication==false) {
				txtLoginResult.setText(result);
			} else {
				btnLogin.setText(R.string.frg_login_logout);
				getActivity().finish();
				startActivity(getActivity().getIntent());
			}
		}
	}
}
