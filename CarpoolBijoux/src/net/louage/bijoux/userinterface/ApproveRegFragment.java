package net.louage.bijoux.userinterface;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import com.google.gson.Gson;
import net.louage.bijoux.R;
import net.louage.bijoux.constants.NoticeDialogFragmentMultiChoice;
import net.louage.bijoux.constants.SharedPreferences;
import net.louage.bijoux.model.User;
import net.louage.bijoux.server.AsTskObjectCompleteListener;
import net.louage.bijoux.server.UserApproveRegAsyncUpdate;
import net.louage.bijoux.sqlite.SchemaHelper;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ListFragment;
import android.content.Intent;
import android.os.Bundle;
//import android.util.Log;
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

public class ApproveRegFragment extends ListFragment implements OnItemClickListener {
	public static final String ARG_NAVDRAWER_NUMBER = "number";
	CustomIconAdapter adapter;
	private List<RowIconItem> rowIconItems;
	private ArrayList<User> approveRegUsers = new ArrayList<User>();
	String[]teamnames;
	private User appUser;
	MainActivity ma;
	User userToApprove;
	int mStackLevel = 0;
	Boolean approvalStatus=false; //This parameter will be added to the async task
	public static final int DIALOG_FRAGMENT = 1;
	
	public ApproveRegFragment() {
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
	
	@SuppressLint("CommitTransaction")
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
		View approveRegView = inflater.inflate(R.layout.fragment_my_tours, container, false);
		getActivity().setTitle(selNavDrawItem);
		rowIconItems = new ArrayList<RowIconItem>();
		appUser = SharedPreferences.getUser(ma);
		return approveRegView;
	}

	private ArrayList<User> getMembersToApproveReg() {
		//String tag = "ApproveRegFragment getApproveRegMembers";
		SchemaHelper sh = new SchemaHelper(ma);
		ArrayList<User> sqLiteUsers = new ArrayList<User>();
		sqLiteUsers = sh.getUsersToApprove();
		//Log.d(tag, "tempUser.size()" + sqLiteUsers.size());
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
		//String tag = "ApproveRegFragment setUserList";
		//Log.d(tag, "usrs size: " + usrs.size());
		rowIconItems.clear();
		approveRegUsers = usrs;
		if (approveRegUsers != null) {
			// Sort Arraylist before displaying it to the user
			Collections.sort(approveRegUsers, new Comparator<User>() {
				@Override
				public int compare(User u1, User u2) {
					return u1.getFirstname().compareTo(u2.getFirstname());
				}
			});
			for (int i = 0; i < approveRegUsers.size(); i++) {
				User usr = approveRegUsers.get(i);
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
		User usr = (User) approveRegUsers.get(position);
		Toast.makeText(getActivity(), usr.getFirstname() + " was clicked", Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		ArrayList<User> tempUsers = new ArrayList<User>();
		tempUsers = getMembersToApproveReg();
		setUserList(tempUsers);
		super.onViewCreated(view, savedInstanceState);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		ma.getMenuInflater().inflate(R.menu.approve_registration, menu);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		userToApprove = approveRegUsers.get(info.position);
		switch (item.getItemId()) {
		case R.id.approve_registration:
			//Ask administrator what teams the registration is for must be removed from
			approvalStatus=true;
			showDialog(DIALOG_FRAGMENT);
			break;
		case R.id.decline_registration:
			//Ask administrator what teams the registration is for must be removed from
			approvalStatus=false;
			teamnames=new String[]{};
			String[] paramsApproveReg = getApproveRegParams(userToApprove);
    		new UserApproveRegAsyncUpdate(ma, new UserRegistrationUpdateTaskCompleteListener(),paramsApproveReg).execute();
			break;
		default:
			break;

		}
		return super.onContextItemSelected(item);
	}

	class UserRegistrationUpdateTaskCompleteListener implements
			AsTskObjectCompleteListener<User> {
		@Override
		public void onTaskComplete(User user) {
			//Log.d("UserRegistrationUpdateTaskCompleteListener: ", "onTaskComplete(User user) Started");
			SchemaHelper sh = new SchemaHelper(getActivity());
			if (user!=null) {
				if (approvalStatus==false) {
					//Delete the user from sqLite db in case registration wasn't approved
					sh.userDelete(userToApprove.getUser_id());	
				} else {
					//Update user status in sqLite db in case registration was approved
					ArrayList<User> users = new ArrayList<User>();
					users.add(userToApprove);
					sh.userCreateOrUpdate(users);
				}	
				sh.close();
				//Update ListView in UI
				ArrayList<User> tempApproveRegUsers = new ArrayList<User>();
				tempApproveRegUsers=getMembersToApproveReg();
				setUserList(tempApproveRegUsers);
				userToApprove=null;
			} else {
				Toast.makeText(ma, "Somthing went wrong, please try the registration over again!", Toast.LENGTH_LONG).show();
			}
			
		}
	}
	
	
	private String[] getApproveRegParams(User usr) {
		String user_id = Integer.toString(appUser.getUser_id());
		String user_id_to_remove = Integer.toString(usr.getUser_id());
		String teams_to_approve_member = "";
		for (int i = 0; i < teamnames.length; i++) {
			teams_to_approve_member=teams_to_approve_member+teamnames[i];
			if (i+1<teamnames.length) {
				teams_to_approve_member=teams_to_approve_member+",";
			}
			
		}
		String as = Boolean.toString(approvalStatus);
		String[] params = new String[] { user_id, user_id_to_remove,teams_to_approve_member, as};
		return params;
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
	        switch(requestCode) {
	            case DIALOG_FRAGMENT:
	                if (resultCode == Activity.RESULT_OK) {
	                	Bundle b = data.getExtras();
	    				if (b != null) {
	    					userToApprove.setApproved(true);
	    					//Retrieve tour from intent
	    					String jsonResultSelection = data.getStringExtra("jsonResultSelection");
	    					Gson gson = new Gson();
	    					String[] resultSelection=gson.fromJson(jsonResultSelection, String[].class);
	    					teamnames=resultSelection;
		            		String[] paramsApproveReg = getApproveRegParams(userToApprove);
		            		new UserApproveRegAsyncUpdate(ma, new UserRegistrationUpdateTaskCompleteListener(),paramsApproveReg).execute();
	    					}else{
	    						Toast.makeText(ma, "Positive clicked Bundle was empty", Toast.LENGTH_LONG).show();
	    					}
	            		//Toast.makeText(ma, "Positive clicked", Toast.LENGTH_LONG).show();
	                } else if (resultCode == Activity.RESULT_CANCELED){
	                    // After Cancel code.
	                	userToApprove=null;
	            		Toast.makeText(ma, R.string.frg_myteam_menu_delete_canceled, Toast.LENGTH_LONG).show();
	                }

	                break;
	        }
	    }

}