package net.louage.bijoux.userinterface;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.google.gson.Gson;

import net.louage.bijoux.R;
import net.louage.bijoux.constants.NoticeDialogFragmentMultiChoice;
import net.louage.bijoux.constants.SharedPreferences;
import net.louage.bijoux.model.Tour;
import net.louage.bijoux.model.User;
import net.louage.bijoux.server.AsTskObjectCompleteListener;
import net.louage.bijoux.server.UserTeamAsyncDelete;
import net.louage.bijoux.sqlite.SchemaHelper;
import android.app.Activity;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ListFragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

public class MyTeamFragment extends ListFragment implements OnItemClickListener {
	public static final String ARG_NAVDRAWER_NUMBER = "number";
	CustomIconAdapter adapter;
	private List<RowIconItem> rowIconItems;
	private ArrayList<User> users = new ArrayList<User>();
	String[]teamnames;
	private User appUser;
	MainActivity ma;
	User userToRemove;
	int mStackLevel = 0;
	public static final int DIALOG_FRAGMENT = 1;
	
	public MyTeamFragment() {
		// Empty constructor required for fragment subclasses
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (savedInstanceState != null) {
	        mStackLevel = savedInstanceState.getInt("level");
	    }
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
	    super.onSaveInstanceState(outState);
	    outState.putInt("level", mStackLevel);
	}
	
	void showDialog(int type) {
	    mStackLevel++;

	    FragmentTransaction ft = getActivity().getFragmentManager().beginTransaction();
	    Fragment prev = getActivity().getFragmentManager().findFragmentByTag("dialog");
	    if (prev != null) {
	        ft.remove(prev);
	    }
	    ft.addToBackStack(null);

	    switch (type) {

	        case DIALOG_FRAGMENT:

	            DialogFragment dialogFrag = NoticeDialogFragmentMultiChoice.newInstance(123);
	            dialogFrag.setTargetFragment(this, DIALOG_FRAGMENT);
	            dialogFrag.show(getFragmentManager().beginTransaction(), "dialog");

	            break;
	    }
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		int i = getArguments().getInt(ARG_NAVDRAWER_NUMBER);
		ma = (MainActivity) getActivity();
		String selNavDrawItem = ma.getmNavDrawerTitles()[i];
		View myTeamView = inflater.inflate(R.layout.fragment_my_tours,
				container, false);
		getActivity().setTitle(selNavDrawItem);
		rowIconItems = new ArrayList<RowIconItem>();
		appUser = SharedPreferences.getUser(ma);
		return myTeamView;
	}

	private ArrayList<User> getTeamMembers() {
		String tag = "MyTeamFragment getTeamMembers";
		SchemaHelper sh = new SchemaHelper(ma);
		ArrayList<User> sqLiteUsers = new ArrayList<User>();
		sqLiteUsers = sh.getUsers();
		Log.d(tag, "tempUser.size()" + sqLiteUsers.size());
		sh.close();
		int removeIndex = -1;
		for (int i = 0; i < sqLiteUsers.size(); i++) {
			User checUser = sqLiteUsers.get(i);
			if (appUser.getUser_id() == checUser.getUser_id()) {
				removeIndex = i;
			}
		}

		if (removeIndex >= 0) {
			sqLiteUsers.remove(removeIndex);
		}
		return sqLiteUsers;
	}

	private void setUserList(ArrayList<User> usrs) {
		String tag = "MyTeamFragment setUserList";
		Log.d(tag, "usrs size: " + usrs.size());
		rowIconItems.clear();
		users = usrs;
		if (users != null) {
			// Sort Arraylist before displaying it to the user
			Collections.sort(users, new Comparator<User>() {
				@Override
				public int compare(User u1, User u2) {
					return u1.getFirstname().compareTo(u2.getFirstname());
				}
			});
			for (int i = 0; i < users.size(); i++) {
				User usr = users.get(i);
				String trData = usr.getFirstname() + " " + usr.getLastname()
						+ "\nTel: " + usr.getPhone();
				String logo = "ic_action_person";
				int id = getActivity().getResources().getIdentifier(logo,
						"drawable", getActivity().getPackageName());
				RowIconItem items = new RowIconItem(trData, id);
				rowIconItems.add(items);
			}

			adapter = new CustomIconAdapter(getActivity(), rowIconItems);
			setListAdapter(adapter);
			getListView().setOnItemClickListener(this);
			registerForContextMenu(getListView());
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		User usr = (User) users.get(position);
		// TODO Start content menu to remove user from team
		// String user = "From: "+usr.getFromAddress().getLocality() + "\nTo: "+
		// usr.getToAddress().getLocality();
		Toast.makeText(getActivity(), usr.getFirstname() + " was clicked",
				Toast.LENGTH_SHORT).show();
		// Intent intent = new Intent(getActivity(), TourActivity.class);
		// Gson gson = new Gson();
		// String jsonTour = gson.toJson(usr);
		// intent.putExtra("jsonTour", jsonTour);
		// startActivity(intent);
		// startActivityForResult(intent, GET_TOUR_INFO);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		ArrayList<User> tempUsers = new ArrayList<User>();
		tempUsers = getTeamMembers();
		setUserList(tempUsers);
		super.onViewCreated(view, savedInstanceState);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		ma.getMenuInflater().inflate(R.menu.teammember_status, menu);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		userToRemove = users.get(info.position);
		switch (item.getItemId()) {
		case R.id.remove_team_member:
			//Ask team leader what teams the user must be removed from
			showDialog(DIALOG_FRAGMENT);
			break;
		default:
			break;

		}
		return super.onContextItemSelected(item);
	}

	class UserTeamDeleteTaskCompleteListener implements
			AsTskObjectCompleteListener<User> {
		@Override
		public void onTaskComplete(User user) {
			Log.d("DeleteUserTaskCompleteListener: ", "onTaskComplete(User user) Started");
			SchemaHelper sh = new SchemaHelper(getActivity());
			int userDeleted = sh.userDelete(userToRemove.getUser_id());
			if (userDeleted>0) {
				int userArrayListIndex=-1;
				ArrayList<User> tempTeamMembers = new ArrayList<User>();
				tempTeamMembers=users;
				for (int i = 0; i < tempTeamMembers.size(); i++) {
					User usr = new User();
					usr=tempTeamMembers.get(i);
					if (usr.getUser_id()==userDeleted) {
						userArrayListIndex=i;
					}
				}
				if (userArrayListIndex>=0) {
					tempTeamMembers.remove(userArrayListIndex);
					setUserList(tempTeamMembers);
					userToRemove=null;
					Toast.makeText(ma, "User was removed on ArrayList", Toast.LENGTH_LONG).show();
				} else {
					Toast.makeText(ma, "User wasn't removed on ArrayList", Toast.LENGTH_LONG).show();
				}
			} else {
				//SQLite record wasn't deleted
				Toast.makeText(ma, "SQLite record wasn't deleted", Toast.LENGTH_LONG).show();
				
			}
			
			// Loop over list and remove user if he was deleted from all teams
			//setUserList(tempUSers);
		}
	}
	
	
	private String[] getUserParams(User usr) {
		String user_id = Integer.toString(appUser.getUser_id());
		String user_id_to_remove = Integer.toString(usr.getUser_id());
		String teams_to_remove_member = "";
		for (int i = 0; i < teamnames.length; i++) {
			teams_to_remove_member=teams_to_remove_member+","+teamnames[i];
		}
		String[] params = new String[] { user_id, user_id_to_remove,teams_to_remove_member };
		return params;
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
	        switch(requestCode) {
	            case DIALOG_FRAGMENT:
	                if (resultCode == Activity.RESULT_OK) {
	                	Bundle b = data.getExtras();
	    				if (b != null) {
	    					//Retrieve tour from intent
	    					String jsonResultSelection = data.getStringExtra("jsonResultSelection");
	    					Gson gson = new Gson();
	    					String[] resultSelection=gson.fromJson(jsonResultSelection, String[].class);
	    					teamnames=resultSelection;
		            		String[] paramsRemoveFromTeam = getUserParams(userToRemove);
		            		new UserTeamAsyncDelete(ma, new UserTeamDeleteTaskCompleteListener(),paramsRemoveFromTeam).execute();
	    					}else{
	    						Toast.makeText(ma, "Positive clicked Bundle was empty", Toast.LENGTH_LONG).show();
	    					}
	            		//userToRemove=null;
	            		//Toast.makeText(ma, "Positive clicked", Toast.LENGTH_LONG).show();
	                } else if (resultCode == Activity.RESULT_CANCELED){
	                    // After Cancel code.
	                	userToRemove=null;
	            		Toast.makeText(ma, R.string.frg_myteam_menu_delete_canceled, Toast.LENGTH_LONG).show();
	                }

	                break;
	        }
	    }

}