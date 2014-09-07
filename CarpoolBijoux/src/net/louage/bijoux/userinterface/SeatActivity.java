package net.louage.bijoux.userinterface;

import net.louage.bijoux.R;
import net.louage.bijoux.constants.SharedPreferences;
import net.louage.bijoux.model.Seat;
import net.louage.bijoux.model.User;
import net.louage.bijoux.server.AsTskObjectCompleteListener;
import net.louage.bijoux.server.SeatAsyncCreateUpdate;

import com.google.gson.Gson;

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
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class SeatActivity extends Activity implements OnClickListener{
	private TextView txt_seat_status;
	private CheckBox chb_seat_paid;
	private EditText etxt_seat_from;
	private EditText etxt_seat_to;
	Button btn_seat_save;
	private Seat st;
	private User appUser;
	private static final String TRANS_SUCCESS = "Completed transaction successfully";
	private static final String TRANS_NO_SUCCESS = "Transaction wasn't successfully ";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_seat);
		
		// Get Seat from intent
		String jsonSeat = getIntent().getStringExtra("jsonSeat");
		Gson gson = new Gson();
		st = gson.fromJson(jsonSeat, Seat.class);
		appUser = SharedPreferences.getUser(this);
		txt_seat_status= (TextView)findViewById(R.id.txt_seat_status);
		chb_seat_paid= (CheckBox)findViewById(R.id.chb_seat_paid);
		chb_seat_paid.setOnClickListener(this);
		checkboxNotEditable();
		etxt_seat_from= (EditText)findViewById(R.id.etxt_seat_from);
		etxt_seat_to= (EditText)findViewById(R.id.etxt_seat_to);
		btn_seat_save=(Button)findViewById(R.id.btn_seat_save);
		btn_seat_save.setOnClickListener(this);
		txt_seat_status.setText(st.getStatus());
		chb_seat_paid.setChecked(st.isPaid());
		etxt_seat_from.setText(st.getPickupAddress());
		etxt_seat_to.setText(st.getDropoffAddress());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.seat, menu);
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
		switch (v.getId()) {
		case R.id.chb_seat_paid:
			st.setPaid(chb_seat_paid.isChecked());
			break;
		case R.id.btn_seat_save:
			//If pickup address changes, the status must be approved again by the driver
			st.setStatus("Pending");
			st.setPickupAddress(etxt_seat_from.getText().toString());
			st.setDropoffAddress(etxt_seat_to.getText().toString());
			String[] paramsPaid = getSeatParams(st);
			new SeatAsyncCreateUpdate(this,	new CreateOrUpdateSeatTaskCompleteListener(), paramsPaid).execute();
			break;
		default:
			break;

		}
	}
	
	
	class CreateOrUpdateSeatTaskCompleteListener implements
	AsTskObjectCompleteListener<Seat> {
		@Override
		public void onTaskComplete(Seat st) {
			//Log.d("CreateOrUpdateSeatTaskCompleteListener: ", "onTaskComplete(Seat st) Started");
			if (st!=null) {
				Toast.makeText(getApplicationContext(), TRANS_SUCCESS, Toast.LENGTH_LONG).show();
				onBackPressed();
			} else {
				Toast.makeText(getApplicationContext(), TRANS_NO_SUCCESS, Toast.LENGTH_LONG).show();
			}
			
		}
	}
	
	private String[] getSeatParams(Seat st) {
		String[] params = { Integer.toString(appUser.getUser_id()),
				Integer.toString(st.getSeat_id()),
				Integer.toString(st.getCreated_by_user_id()),
				st.getDevice_id(), Integer.toString(st.getTour_id()),
				Integer.toString(st.getUser_id()), st.getStatus(),
				Boolean.toString(st.isPaid()),
				st.getPickupAddress(),st.getDropoffAddress() };
		return params;
	}
	
	private void checkboxNotEditable() {
		chb_seat_paid.setFocusable(false);
		// user touches widget on phone with touch screen
		chb_seat_paid.setFocusableInTouchMode(false);
		// user navigates with wheel and selects widget
		chb_seat_paid.setClickable(false);

	}

	@Override
	public void onBackPressed() {
		Intent intent = new Intent();
		Gson gson = new Gson();
		String jsonSeat = gson.toJson(st);
		intent.putExtra("jsonSeat", jsonSeat);
		setResult(RESULT_OK, intent);
		finish();
	}
}
