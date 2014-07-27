/*
 * Copyright 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.louage.bijoux.userinterface;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ListFragment;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * This example illustrates a common usage of the DrawerLayout widget in the
 * Android support library.
 * <p/>
 * <p>
 * When a navigation (left) drawer is present, the host activity should detect
 * presses of the action bar's Up affordance as a signal to open and close the
 * navigation drawer. The ActionBarDrawerToggle facilitates this behavior. Items
 * within the drawer should fall into one of two categories:
 * </p>
 * <p/>
 * <ul>
 * <li><strong>View switches</strong>. A view switch follows the same basic
 * policies as list or tab navigation in that a view switch does not create
 * navigation history. This pattern should only be used at the root activity of
 * a task, leaving some form of Up navigation active for activities further down
 * the navigation hierarchy.</li>
 * <li><strong>Selective Up</strong>. The drawer allows the user to choose an
 * alternate parent for Up navigation. This allows a user to jump across an
 * app's navigation hierarchy at will. The application should treat this as it
 * treats Up navigation from a different task, replacing the current task stack
 * using TaskStackBuilder or similar. This is the only form of navigation drawer
 * that should be used outside of the root activity of a task.</li>
 * </ul>
 * <p/>
 * <p>
 * Right side drawers should be used for actions, not navigation. This follows
 * the pattern established by the Action Bar that navigation should be to the
 * left and actions to the right. An action should be an operation performed on
 * the current contents of the window, for example enabling or disabling a data
 * overlay on top of the current content.
 * </p>
 */
public class MainActivity extends Activity {
	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;
	private ActionBarDrawerToggle mDrawerToggle;

	private CharSequence mDrawerTitle;
	private CharSequence mTitle;
	private String[] mNavDrawerTitles;
	String navDrawerArray;
	User appUser;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mTitle = mDrawerTitle = getTitle();

		// temporary overule login until php script works fine for login
		// user_id = 1;
		// net.louage.bijoux.constants.SharedPreferences.setUserId(this,
		// user_id);
		// net.louage.bijoux.constants.SharedPreferences.setUserName(this,
		// "job");
		// net.louage.bijoux.constants.SharedPreferences.setPassword(this,
		// "job");
		// net.louage.bijoux.constants.SharedPreferences.setRole(this, "admin");

		if (!net.louage.bijoux.constants.SharedPreferences
				.checkConnectionData(this)) {
			mNavDrawerTitles = getResources().getStringArray(
					R.array.unknown_array);
		} else {
			net.louage.bijoux.constants.SharedPreferences.getUserId(this);
			appUser = net.louage.bijoux.constants.SharedPreferences
					.getUser(this);
			Boolean member = false;
			Boolean teamleader = false;
			boolean admin = false;
			ArrayList<Role> appUserRoles = new ArrayList<Role>();
			try {
				appUserRoles = appUser.getRoles();

				for (int i = 0; i < appUserRoles.size(); i++) {
					Role appUserRole = appUserRoles.get(i);
					String appUserRoleName = appUserRole.getRoleName();
					switch (appUserRoleName) {
					case "member":
						member = true;
						break;
					case "teamleader":
						teamleader = true;
						break;
					case "admin":
						admin = true;
						break;
					default:
						break;
					}
				}
			} catch (Exception e) {
				// No roles known for this user... go back to login
			}

			if (admin) {
				mNavDrawerTitles = getResources().getStringArray(
						R.array.admin_array);
			} else if (teamleader) {
				mNavDrawerTitles = getResources().getStringArray(
						R.array.team_array);
			} else if (member) {
				mNavDrawerTitles = getResources().getStringArray(
						R.array.member_array);
			} else {
				mNavDrawerTitles = getResources().getStringArray(
						R.array.unknown_array);
			}

			/*
			 * switch (net.louage.bijoux.constants.SharedPreferences
			 * .getroleName(this)) { case "admin": mNavDrawerTitles =
			 * getResources().getStringArray( R.array.admin_array); break; case
			 * "teamleader": mNavDrawerTitles = getResources().getStringArray(
			 * R.array.team_array); break; case "member": mNavDrawerTitles =
			 * getResources().getStringArray( R.array.member_array); break; case
			 * "unknown": mNavDrawerTitles = getResources().getStringArray(
			 * R.array.unknown_array); break; default: break; }
			 */
		}
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerList = (ListView) findViewById(R.id.left_drawer);

		// set a custom shadow that overlays the main content when the drawer
		// opens
		mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow,
				GravityCompat.START);
		// set up the drawer's list view with items and click listener
		mDrawerList.setAdapter(new ArrayAdapter<String>(this,
				R.layout.drawer_list_item, mNavDrawerTitles));
		mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

		// enable ActionBar app icon to behave as action to toggle nav drawer
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);

		// ActionBarDrawerToggle ties together the the proper interactions
		// between the sliding drawer and the action bar app icon
		mDrawerToggle = new ActionBarDrawerToggle(this, /* host Activity */
		mDrawerLayout, /* DrawerLayout object */
		R.drawable.ic_drawer, /* nav drawer image to replace 'Up' caret */
		R.string.drawer_open, /* "open drawer" description for accessibility */
		R.string.drawer_close /* "close drawer" description for accessibility */
		) {
			public void onDrawerClosed(View view) {
				getActionBar().setTitle(mTitle);
				invalidateOptionsMenu(); // creates call to
											// onPrepareOptionsMenu()
			}

			public void onDrawerOpened(View drawerView) {
				getActionBar().setTitle(mDrawerTitle);
				invalidateOptionsMenu(); // creates call to
											// onPrepareOptionsMenu()
			}
		};
		mDrawerLayout.setDrawerListener(mDrawerToggle);

		if (savedInstanceState == null) {
			selectItem(0);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);
		return super.onCreateOptionsMenu(menu);
	}

	/* Called whenever we call invalidateOptionsMenu() */
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		// If the nav drawer is open, hide action items related to the content
		// view
		boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
		menu.findItem(R.id.action_websearch).setVisible(!drawerOpen);
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// The action bar home/up action should open or close the drawer.
		// ActionBarDrawerToggle will take care of this.
		if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}
		// Handle action buttons
		switch (item.getItemId()) {
		case R.id.action_websearch:
			// create intent to perform web search for this planet
			Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
			intent.putExtra(SearchManager.QUERY, getActionBar().getTitle());
			// catch event that there's no activity to handle intent
			if (intent.resolveActivity(getPackageManager()) != null) {
				startActivity(intent);
			} else {
				Toast.makeText(this, R.string.app_not_available,
						Toast.LENGTH_LONG).show();
			}
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	/* The click listner for ListView in the navigation drawer */
	private class DrawerItemClickListener implements
			ListView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			selectItem(position);
		}
	}

	/** Swaps fragments in the main content view */
	private void selectItem(int position) {
		switch (mNavDrawerTitles[position]) {
		case "Coming Tours":
			buildComingTours(position);
			break;
		case "My Tours":
			buildMyTours(position);
			break;
		case "My Seats":
			// TODO Build listeners for My Seats
			buildUnknowAccount(position);
			break;
		case "My Profile":
			// TODO Build listeners for My Profile
			buildUnknowAccount(position);
			break;
		case "Login":
			buildLogin(position);
			break;
		case "Register":
			// TODO Build listeners for Register
			buildRegister(position);
			break;
		case "My Team":
			// TODO Build listeners for My Team
			buildUnknowAccount(position);
			break;
		case "Approve Member":
			// TODO Build listeners for Approve Member
			buildUnknowAccount(position);
			break;
		case "Admin":
			// TODO Build listeners for Admin
			buildUnknowAccount(position);
			break;
		case "Unknown Account":
			// TODO Build listeners for Unknown Account
			buildUnknowAccount(position);
			break;
		default:
			break;
		}

		// update selected item and title, then close the drawer
		mDrawerList.setItemChecked(position, true);
		setTitle(mNavDrawerTitles[position]);
		mDrawerLayout.closeDrawer(mDrawerList);
	}

	private void buildComingTours(int position) {
		// update the main content by replacing fragments
		Fragment fragment = new ComingToursFragment();
		Bundle args = new Bundle();

		// - Inserts an int value into the mapping of this Bundle,
		// replacing any existing value for the given key.
		args.putInt(RegisterFragment.ARG_NAVDRAWER_NUMBER, position);
		fragment.setArguments(args);

		FragmentManager fragmentManager = getFragmentManager();
		fragmentManager.beginTransaction()
				.replace(R.id.content_frame, fragment).commit();
		// update selected item and title, then close the drawer
	}

	private void buildRegister(int position) {
		// update the main content by replacing fragments
		Fragment fragment = new RegisterFragment();
		Bundle args = new Bundle();

		// - Inserts an int value into the mapping of this Bundle,
		// replacing any existing value for the given key.
		args.putInt(RegisterFragment.ARG_NAVDRAWER_NUMBER, position);
		fragment.setArguments(args);

		FragmentManager fragmentManager = getFragmentManager();
		fragmentManager.beginTransaction()
				.replace(R.id.content_frame, fragment).commit();
		// update selected item and title, then close the drawer
	}

	private void buildUnknowAccount(int position) {
		// update the main content by replacing fragments
		Fragment fragment = new UnknowAccountFragment();
		Bundle args = new Bundle();

		// - Inserts an int value into the mapping of this Bundle,
		// replacing any existing value for the given key.
		args.putInt(UnknowAccountFragment.ARG_NAVDRAWER_NUMBER, position);
		fragment.setArguments(args);

		FragmentManager fragmentManager = getFragmentManager();
		fragmentManager.beginTransaction()
				.replace(R.id.content_frame, fragment).commit();
		// update selected item and title, then close the drawer
	}

	private void buildLogin(int position) {
		// update the main content by replacing fragments
		Fragment fragment = new LoginFragment();
		Bundle args = new Bundle();

		// - Inserts an int value into the mapping of this Bundle,
		// replacing any existing value for the given key.
		args.putInt(LoginFragment.ARG_NAVDRAWER_NUMBER, position);
		fragment.setArguments(args);

		FragmentManager fragmentManager = getFragmentManager();
		fragmentManager.beginTransaction()
				.replace(R.id.content_frame, fragment).commit();
		// update selected item and title, then close the drawer
	}

	private void buildMyTours(int position) {
		// TODO Build listeners for My Tours
		// update the main content by replacing fragments
		Fragment fragment = new MainFragment();
		Bundle args = new Bundle();

		// - Inserts an int value into the mapping of this Bundle,
		// replacing any existing value for the given key.
		args.putInt(MainFragment.ARG_NAVDRAWER_NUMBER, position);
		fragment.setArguments(args);

		FragmentManager fragmentManager = getFragmentManager();
		fragmentManager.beginTransaction()
				.replace(R.id.content_frame, fragment).commit();
		// update selected item and title, then close the drawer
	}

	@Override
	public void setTitle(CharSequence title) {
		if (appUser!=null){
			mTitle = title + " - " + appUser.getFirstname();
		}else{
			mTitle = title;			
		}
		
		getActionBar().setTitle(mTitle);
	}

	/**
	 * When using the ActionBarDrawerToggle, you must call it during
	 * onPostCreate() and onConfigurationChanged()...
	 */

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
		mDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		// Pass any configuration change to the drawer toggles
		mDrawerToggle.onConfigurationChanged(newConfig);
	}

	public static class MainFragment extends Fragment {
		public static final String ARG_NAVDRAWER_NUMBER = "number";

		public MainFragment() {
			// Empty constructor required for fragment subclasses
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			int i = getArguments().getInt(ARG_NAVDRAWER_NUMBER);
			MainActivity ma = (MainActivity) getActivity();
			String selNavDrawItem = ma.mNavDrawerTitles[i];
			switch (selNavDrawItem) {
			case "Coming Tours":
				// TODO Build fragment for Coming Tours
				View commingToursView = inflater.inflate(
						R.layout.fragment_main, container, false);
				getActivity().setTitle(selNavDrawItem);
				return commingToursView;
			case "My Tours":
				// TODO Build fragment for My Tours
				View myToursView = inflater.inflate(R.layout.fragment_main,
						container, false);
				getActivity().setTitle(selNavDrawItem);
				return myToursView;
			case "My Seats":
				// TODO Build fragment for My Seats
				View mySeatsView = inflater.inflate(R.layout.fragment_main,
						container, false);
				getActivity().setTitle(selNavDrawItem);
				return mySeatsView;
			case "My Profile":
				// TODO Build fragment for My Profile
				View myProfileView = inflater.inflate(R.layout.fragment_main,
						container, false);
				getActivity().setTitle(selNavDrawItem);
				return myProfileView;
				/*
				 * case "Login": // TODO Build fragment for Login View loginView
				 * = inflater.inflate(R.layout.fragment_login, container,
				 * false); getActivity().setTitle(selNavDrawItem); return
				 * loginView;
				 */
			case "Register":
				View registerView = inflater.inflate(R.layout.fragment_main,
						container, false);
				getActivity().setTitle(selNavDrawItem);
				return registerView;
			case "My Team":
				// TODO Build fragment for My Team
				View myTeamView = inflater.inflate(R.layout.fragment_main,
						container, false);
				getActivity().setTitle(selNavDrawItem);
				return myTeamView;
			case "Approve Member":
				// TODO Build fragment for Approve Member
				View approveMemberView = inflater.inflate(
						R.layout.fragment_main, container, false);
				getActivity().setTitle(selNavDrawItem);
				return approveMemberView;
			case "Admin":
				// TODO Build fragment for Admin
				View adminView = inflater.inflate(R.layout.fragment_main,
						container, false);
				getActivity().setTitle(selNavDrawItem);
				return adminView;
				/*
				 * case "Unknown Account": // TODO Build fragment for Unknown
				 * Account View unknownView = inflater.inflate(
				 * R.layout.fragment_unknown_account, container, false);
				 * getActivity().setTitle(selNavDrawItem); return unknownView;
				 */
			default:
				View defaultView = inflater.inflate(
						R.layout.fragment_unknown_account, container, false);
				getActivity().setTitle(selNavDrawItem);
				return defaultView;
			}
		}

	}

	public static class LoginFragment extends Fragment implements
			View.OnClickListener {
		public static final String ARG_NAVDRAWER_NUMBER = "number";
		public static final String RES_LOGIN_OK = "Login was succesfull";
		public static final String RES_LOGIN_NOK = "Login was unsuccesfull";
		public static final String RES_LOGIN_NULL = "Could not connect to the server";
		EditText txtUsername;
		EditText txtPassword;
		Button btnLogin;
		TextView txtLoginResult;

		public LoginFragment() {
			// Empty constructor required for fragment subclasses
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			int i = getArguments().getInt(ARG_NAVDRAWER_NUMBER);
			MainActivity ma = (MainActivity) getActivity();
			String selNavDrawItem = ma.mNavDrawerTitles[i];
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
			public static final int DIALOG_DOWNLOAD_PROGRESS = 0;
			private static final String TAG_LOGIN_SUCCESFULL = "result";
			private static final String TAG_USER_INFO_RESULT = "user_info";
			private static final String TAG_USER = "user";
			private static final String TAG_USER_ROLES = "user_roles";
			private static final String TAG_USER_VEHICLES = "user_vehicles";
			private static final String TAG_USER_MEMBER_OF = "user_member_of";
			private static final String TAG_USER_TEAMLEADER_OF = "user_teamleader_of";

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
				String tag = "CheckLogin doInBackground";
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
							Log.d("success login: ", json.toString());
							JSONObject json_user_info = json
									.getJSONObject(TAG_USER_INFO_RESULT);
							JSONObject json_user = json_user_info
									.getJSONObject(TAG_USER);
							Log.d("success login: ", json_user.toString());
							User user = new User();
							user.setUser_id(json_user.getInt(User.TAG_ID));
							Date date = DateTime.getDateSQLiteString(json_user
									.getString(User.TAG_ACTIVATION));
							user.setActivation(date);
							user.setDriverlicense(json_user
									.getString(User.TAG_DRIVERLICENSE));
							user.setEmail(json_user.getString(User.TAG_EMAIL));
							user.setFirstname(json_user
									.getString(User.TAG_FIRSTNAME));
							user.setInfo(json_user.getString(User.TAG_INFO));
							user.setLastname(json_user
									.getString(User.TAG_LASTNAME));
							user.setPassword(txtPassword.getText().toString());
							user.setPhone(json_user.getString(User.TAG_PHONE));
							Date updatedDate = DateTime
									.getDateTimeSQLiteString(json_user
											.getString(User.TAG_UPDATED_AT));
							user.setUpdate_at(updatedDate);
							user.setUsername(json_user
									.getString(User.TAG_USERNAME));
							JSONObject json_user_roles = json_user_info
									.getJSONObject(TAG_USER_ROLES);
							JSONArray json_roles = json_user_roles
									.getJSONArray(User.TAG_ROLES);
							ArrayList<Role> roles = new ArrayList<Role>();
							for (int i = 0; i < json_roles.length(); i++) {
								JSONObject json_role = json_roles
										.getJSONObject(i);
								String rolename = json_role
										.getString(Role.TAG_USER_ROLENAME);
								Role role = new Role(rolename);
								roles.add(role);
							}
							user.setRoles(roles);

							JSONObject json_user_vehicles = json_user_info
									.getJSONObject(TAG_USER_VEHICLES);
							JSONArray json_vehicles = json_user_vehicles
									.getJSONArray(User.TAG_VEHICLES);
							ArrayList<Vehicle> vehicles = new ArrayList<Vehicle>();
							for (int i = 0; i < json_vehicles.length(); i++) {
								JSONObject json_vehicle = json_vehicles
										.getJSONObject(i);
								Vehicle vhc = new Vehicle();
								vhc.setVehicle_id(json_vehicle
										.getInt(Vehicle.TAG_ID));
								vhc.setLicenseplate(json_vehicle
										.getString(Vehicle.TAG_LICENSEPLATE));
								Country ct = new Country(
										json_vehicle
												.getString(Vehicle.TAG_COUNTRY),
										json_vehicle
												.getString(Vehicle.TAG_COUNTRY));
								vhc.setCountry(ct);
								vhc.setNumberOfPassengers(json_vehicle
										.getInt(Vehicle.TAG_NUMBER_OF_PASS));
								vhc.setBrand(json_vehicle
										.getString(Vehicle.TAG_BRAND));
								VehicleType vt = new VehicleType(
										json_vehicle
												.getString(Vehicle.TAG_VEHICLE_TYPE));
								vhc.setType(vt);
								vhc.setUser_id(json_vehicle
										.getInt(Vehicle.TAG_USER_ID));
								Date vehicleUpdatedAt = DateTime
										.getDateTimeSQLiteString(json_user
												.getString(Vehicle.TAG_UPDATED_AT));
								vhc.setUpdate_at(vehicleUpdatedAt);
								vehicles.add(vhc);
							}
							user.setVehicles(vehicles);

							JSONObject json_user_member_of = json_user_info
									.getJSONObject(TAG_USER_MEMBER_OF);
							JSONArray json_teams = json_user_member_of
									.getJSONArray(User.TAG_MEMBER_OF);
							ArrayList<Team> teams = new ArrayList<Team>();
							for (int i = 0; i < json_teams.length(); i++) {
								JSONObject json_team = json_teams
										.getJSONObject(i);
								String teamname = json_team
										.getString(Team.TAG_USER_TEAMNAME);
								Team team = new Team(teamname);
								teams.add(team);
							}
							user.setMemberOf(teams);

							JSONObject json_user_teamleder_of = json_user_info
									.getJSONObject(TAG_USER_TEAMLEADER_OF);
							JSONArray json_teamleaderteams = json_user_teamleder_of
									.getJSONArray(User.TAG_MANAGER_OF);
							ArrayList<Team> managedTeams = new ArrayList<Team>();
							for (int i = 0; i < json_teamleaderteams.length(); i++) {
								JSONObject json_team = json_teams
										.getJSONObject(i);
								String teamname = json_team
										.getString(Team.TAG_USER_TEAMNAME);
								Team team = new Team(teamname);
								managedTeams.add(team);
							}
							user.setManagerOf(managedTeams);

							net.louage.bijoux.constants.SharedPreferences
									.setUserId(context, user.getUser_id());
							net.louage.bijoux.constants.SharedPreferences
									.setUserName(context, parameters[0]);
							net.louage.bijoux.constants.SharedPreferences
									.setPassword(context, parameters[1]);
							net.louage.bijoux.constants.SharedPreferences
									.setUser(context, user);

							return RES_LOGIN_OK;
						} else {
							Log.d("failure login: ", json.toString());
							return RES_LOGIN_NOK;
						}
					} catch (JSONException e) {
						e.printStackTrace();
						return RES_LOGIN_NOK;
					}
				} else {
					return RES_LOGIN_NULL;
				}

			}

			protected void onProgressUpdate(String... progress) {
				Log.d("ANDRO_ASYNC", progress[0]);
				mProgressDialog.setProgress(Integer.parseInt(progress[0]));
			}

			protected void onPostExecute(String result) {
				super.onPostExecute(result);
				txtLoginResult.setText(result);
				btnLogin.setText(R.string.frg_login_logout);
				mProgressDialog.dismiss();
				getActivity().finish();
				startActivity(getActivity().getIntent());
			}

		}

	}

	public static class UnknowAccountFragment extends Fragment implements
			View.OnClickListener {
		public static final String ARG_NAVDRAWER_NUMBER = "number";
		TextView txt_unknown_account;
		Button btnLoginFromUnknown;
		Button btnRegisterFromUnknown;

		public UnknowAccountFragment() {
			// Empty constructor required for fragment subclasses
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			int i = getArguments().getInt(ARG_NAVDRAWER_NUMBER);
			MainActivity ma = (MainActivity) getActivity();
			String selNavDrawItem = ma.mNavDrawerTitles[i];
			View loginView = inflater.inflate(
					R.layout.fragment_unknown_account, container, false);
			getActivity().setTitle(selNavDrawItem);
			btnLoginFromUnknown = (Button) loginView
					.findViewById(R.id.btnLoginFromUnknown);
			btnLoginFromUnknown.setOnClickListener(this);
			btnRegisterFromUnknown = (Button) loginView
					.findViewById(R.id.btnRegisterFromUnknown);
			btnRegisterFromUnknown.setOnClickListener(this);
			txt_unknown_account = (TextView) loginView
					.findViewById(R.id.txt_unknown_account);
			return loginView;
		}

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.btnLoginFromUnknown:
				MainActivity ma = (MainActivity) getActivity();
				// Toast.makeText(ma, "Login from Unknown Account was clicked",
				// Toast.LENGTH_LONG).show();
				String[] nd = ma.mNavDrawerTitles;
				int selectLogin = 0;
				for (int i = 0; i < nd.length; i++) {
					if (nd[i].equals("Login")) {
						selectLogin = i;
					}
				}
				ma.selectItem(selectLogin);
				break;
			case R.id.btnRegisterFromUnknown:
				MainActivity mar = (MainActivity) getActivity();
				// Toast.makeText(ma,
				// "Register from Unknown Account was clicked",
				// Toast.LENGTH_LONG).show();
				String[] ndr = mar.mNavDrawerTitles;
				int selectRegister = 0;
				for (int i = 0; i < ndr.length; i++) {
					if (ndr[i].equals("Register")) {
						selectRegister = i;
					}
				}
				mar.selectItem(selectRegister);
				break;
			default:
				break;
			}

		}

	}

	public static class RegisterFragment extends Fragment implements
			View.OnClickListener {
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
			String selNavDrawItem = ma.mNavDrawerTitles[i];
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
				MainActivity ma = (MainActivity) getActivity();
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

	public static class ComingToursFragment extends ListFragment implements
			OnItemClickListener {
		public static final String ARG_NAVDRAWER_NUMBER = "number";
		// Example array until php script can return array of coming tours
		// TODO get Array from MySQL
		String[] menutitles = { "Sept 15 2014, 18:00, Anoka, Minneapolis",
				"Sept 15 2014, 15:30, Maple Grove, Lake Elmo",
				"Sept 18 2014, 08:30, Bloomington, Shorewood",
				"Sept 24 2014, 20:15, Minnetonka, Cottage Grove",
				"Sept 25 2014, 18:00, Burnsville, Lake Elmo" };;
		// TypedArray menuIcons;
		CustomIconAdapter adapter;
		private List<RowIconItem> rowIconItems;

		public ComingToursFragment() {
			// Empty constructor required for fragment subclasses
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			int i = getArguments().getInt(ARG_NAVDRAWER_NUMBER);
			MainActivity ma = (MainActivity) getActivity();
			String selNavDrawItem = ma.mNavDrawerTitles[i];
			View comingToursView = inflater.inflate(
					R.layout.fragment_coming_tours, container, false);
			getActivity().setTitle(selNavDrawItem);
			return comingToursView;
		}

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			Toast.makeText(getActivity(), menutitles[position],
					Toast.LENGTH_SHORT).show();

		}

		@Override
		public void onViewCreated(View view, Bundle savedInstanceState) {
			// TODO Auto-generated method stub
			super.onViewCreated(view, savedInstanceState);
			// menuIcons =
			// getResources().obtainTypedArray(R.array.list_item_img_icons);
			rowIconItems = new ArrayList<RowIconItem>();

			for (int i = 0; i < menutitles.length; i++) {
				// RowIconItem items = new RowIconItem(menutitles[i],
				// menuIcons.getResourceId(i, -1));
				RowIconItem items = new RowIconItem(menutitles[i],
						R.drawable.calender);
				rowIconItems.add(items);
			}

			adapter = new CustomIconAdapter(getActivity(), rowIconItems);
			setListAdapter(adapter);
			getListView().setOnItemClickListener(this);
		}
	}
}