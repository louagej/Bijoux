package net.louage.bijoux.userinterface;

import java.util.ArrayList;

import com.google.gson.Gson;

import net.louage.bijoux.R;
import net.louage.bijoux.constants.SharedPreferences;
import net.louage.bijoux.model.User;
import net.louage.bijoux.server.AsTskObjectCompleteListener;
import net.louage.bijoux.server.UserAsyncCreateUpdate;
import net.louage.bijoux.sqlite.SchemaHelper;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
//import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

public class UserActivity extends Activity implements OnClickListener{
	TextView txt_user_lastname;
	TextView txt_user_firstname;
	TextView txt_user_email;
	TextView txt_user_phone;
	TextView txt_user_info;
	TextView txt_user_driverlicense;
	CheckBox chb_user_approved;
	Button btn_user_update;
	User user;
	User appUser;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user);
		txt_user_lastname = (TextView) findViewById(R.id.txt_user_lastname);
		txt_user_firstname = (TextView) findViewById(R.id.txt_user_firstname);
		txt_user_email = (TextView) findViewById(R.id.txt_user_email);
		txt_user_phone = (TextView) findViewById(R.id.txt_user_phone);
		txt_user_info = (TextView) findViewById(R.id.txt_user_info);
		txt_user_driverlicense = (TextView) findViewById(R.id.txt_user_driverlicense);
		chb_user_approved = (CheckBox) findViewById(R.id.chb_user_approved);
		chb_user_approved.setOnClickListener(this);
		btn_user_update = (Button) findViewById(R.id.btn_user_update);
		btn_user_update.setOnClickListener(this);
		appUser=SharedPreferences.getUser(this);
		// Get User from intent
		String jsonUser = getIntent().getStringExtra("jsonUser");
		Gson gson = new Gson();
		user = gson.fromJson(jsonUser, User.class);
		if (user!=null) {
			txt_user_lastname.setText(user.getLastname());
			txt_user_firstname.setText(user.getFirstname());
			txt_user_email.setText(user.getEmail());
			txt_user_phone.setText(user.getPhone());
			txt_user_info.setText(user.getInfo());
			txt_user_driverlicense.setText(user.getDriverlicense());
			chb_user_approved.setChecked(user.getApproved());

		} else {
			txt_user_lastname.setText("no user information was found");
		}
		
		setTitle(R.string.act_user_title);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.user, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onClick(View v) {
		//String tag = "UserActivity onClick";
		switch (v.getId()) {
		case R.id.btn_user_update:
			String[] paramsupdateOrCreateUser = getUserParams();
			new UserAsyncCreateUpdate(this, new UserCreateUpdateTaskCompleteListener(),paramsupdateOrCreateUser).execute();
			break;
		case R.id.chb_user_approved:
			user.setApproved(chb_user_approved.isChecked());
			//Log.d(tag, "user is "+ Boolean.toString(user.getApproved()));
		break;
		default:
			break;
		}
		
	}
	
	class UserCreateUpdateTaskCompleteListener implements
	AsTskObjectCompleteListener<User> {
		@Override
		public void onTaskComplete(User usr) {
			//Log.d("onTaskComplete: ",	"UserCreateUpdateTaskCompleteListener Started");
			ArrayList<User> users = new ArrayList<User>();
			if (usr!=null) {
				user=usr;
				users.add(usr);
				SchemaHelper sh = new SchemaHelper(getApplicationContext());
				sh.userCreateOrUpdate(users);
				sh.close();
				users.clear();
				onBackPressed();
			}else{
				Toast.makeText(getApplicationContext(), "update wasn't succesfully", Toast.LENGTH_LONG).show();
			}
		}
	}

	private String[] getUserParams() {
		String user_id = Integer.toString(appUser.getUser_id());
		String user_id_to_cr_upd = Integer.toString(user.getUser_id());
		String lastname = user.getLastname();
		String firstname = user.getFirstname();
		String email = user.getEmail();
		String phone = user.getPhone();
		String info = user.getInfo();
		String driverlicense = user.getDriverlicense();
		String approved = Boolean.toString((user.getApproved()));
		String[] params = {user_id, user_id_to_cr_upd, lastname, firstname, email, phone, info, driverlicense, approved};
		return params;
	}

	@Override
	public void onBackPressed() {
		//String tag = "UserActivity onBackPressed";
		Intent intent = new Intent();
		Gson gson = new Gson();
		String jsonUser = gson.toJson(user);
		//Log.d(tag + " jsonUser:", jsonUser);
		intent.putExtra("jsonUser", jsonUser);
		// Log.d(tag + " intent.getStringExtra:",
		setResult(RESULT_OK, intent);
		onActivityResult(MyTeamFragment.GET_USER_INFO, Activity.RESULT_OK, intent);
		// Toast.makeText(this, "onBackPressed was processed",
		// Toast.LENGTH_SHORT).show();
		finish();
	}

}
