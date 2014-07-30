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
import net.louage.bijoux.R;
import net.louage.bijoux.model.Role;
import net.louage.bijoux.model.User;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.SearchManager;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
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

		if (!net.louage.bijoux.constants.SharedPreferences
				.checkConnectionData(this)) {
			setmNavDrawerTitles(getResources().getStringArray(
					R.array.unknown_array));
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
				setmNavDrawerTitles(getResources().getStringArray(
						R.array.admin_array));
			} else if (teamleader) {
				setmNavDrawerTitles(getResources().getStringArray(
						R.array.team_array));
			} else if (member) {
				setmNavDrawerTitles(getResources().getStringArray(
						R.array.member_array));
			} else {
				setmNavDrawerTitles(getResources().getStringArray(
						R.array.unknown_array));
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
				R.layout.drawer_list_item, getmNavDrawerTitles()));
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

	/* The click listener for ListView in the navigation drawer */
	private class DrawerItemClickListener implements
			ListView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			selectItem(position);
		}
	}

	/** Swaps fragments in the main content view
	 * The method selectItem(int) must be visible from other external fragments
	 * of the same package. Therefore, visibility is set to default
	 */
	void selectItem(int position) {
		switch (getmNavDrawerTitles()[position]) {
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
			buildMyProfile(position);
			break;
		case "Login":
			buildLogin(position);
			break;
		case "Register":
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
			buildUnknowAccount(position);
			break;
		default:
			break;
		}

		// update selected item and title, then close the drawer
		mDrawerList.setItemChecked(position, true);
		setTitle(getmNavDrawerTitles()[position]);
		mDrawerLayout.closeDrawer(mDrawerList);
	}

	private void buildMyProfile(int position) {
		// update the main content by replacing fragments
		Fragment fragment = new MyProfileFragment();
		Bundle args = new Bundle();

		// - Inserts an int value into the mapping of this Bundle,
		// replacing any existing value for the given key.
		args.putInt(RegisterFragment.ARG_NAVDRAWER_NUMBER, position);
		fragment.setArguments(args);

		FragmentManager fragmentManager = getFragmentManager();
		fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
		// update selected item and title, then close the drawer
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

	public String[] getmNavDrawerTitles() {
		return mNavDrawerTitles;
	}

	public void setmNavDrawerTitles(String[] mNavDrawerTitles) {
		this.mNavDrawerTitles = mNavDrawerTitles;
	}
}