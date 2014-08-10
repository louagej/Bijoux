package net.louage.bijoux.userinterface;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.google.gson.Gson;

import net.louage.bijoux.R;
import net.louage.bijoux.constants.DateTime;
import net.louage.bijoux.constants.SharedPreferences;
import net.louage.bijoux.model.Seat;
import net.louage.bijoux.model.Tour;
import net.louage.bijoux.model.User;
import net.louage.bijoux.model.Vehicle;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;

public class TourActivity extends Activity implements OnItemSelectedListener,
		OnItemClickListener, OnClickListener {
	private EditText eTxtActTourTime;
	private Spinner spnnActTourTour;
	private EditText eTxtActTourSeatPrice;
	private EditText eTxtActTourFromAddress;
	private EditText eTxtActTourToAddress;
	private Button btnActTourUpdate;
	private Button btnActTourSeatRequest;
	private EditText eTxtActTourDate;
	private Tour tr;
	User appUser;
	ArrayList<Vehicle> vehicles;
	ArrayList<Seat> seats;
	private List<RowIconItem> rowIconItems;
	CustomIconAdapter adapter;
	List<String> list;
	int spinnerSelection;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tour);
		// Get Tour from intent
		String jsonTour = getIntent().getStringExtra("jsonTour");
		Gson gson = new Gson();
		tr = gson.fromJson(jsonTour, Tour.class);
		// Get appUser Vehicle List;
		appUser = SharedPreferences.getUser(this);
		vehicles = new ArrayList<Vehicle>();
		vehicles = appUser.getVehicles();
		// tr = (Tour) getIntent().getSerializableExtra("Tour");
		eTxtActTourDate = (EditText) findViewById(R.id.eTxtActTourDate);
		eTxtActTourTime = (EditText) findViewById(R.id.eTxtActTourTime);
		spnnActTourTour = (Spinner) findViewById(R.id.spnnActTourVehicle);
		eTxtActTourSeatPrice = (EditText) findViewById(R.id.eTxtActTourSeatPrice);
		eTxtActTourFromAddress = (EditText) findViewById(R.id.eTxtActTourFromAddress);
		eTxtActTourToAddress = (EditText) findViewById(R.id.eTxtActTourToAddress);
		btnActTourUpdate = (Button) findViewById(R.id.btnActTourUpdate);
		btnActTourSeatRequest = (Button) findViewById(R.id.btnActTourSeatRequest);
		// Set text of tour object in view EditText place holders
		list = new ArrayList<String>();
		for (int i = 0; i < vehicles.size(); i++) {
			Vehicle vh = new Vehicle();
			vh = vehicles.get(i);
			list.add(vh.getBrand() + " - " + vh.getLicenseplate());
			if (vh.getVehicle_id() == tr.getVehicle().getVehicle_id()) {
				spinnerSelection = i;
			}
		}

		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, list);
		dataAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spnnActTourTour.setAdapter(dataAdapter);
		// Spinner item selection Listener
		spnnActTourTour.setOnItemSelectedListener(this);
		String tourDate = DateTime.getDateMediumFormat(tr.getDate());
		String tourtime = DateTime.getLocaleTimeDefaultFormat(tr.getTime());
		eTxtActTourDate.setText(tourDate);
		eTxtActTourTime.setText(tourtime);

		// Set current vehicle for the tour as selected spinner item
		spnnActTourTour.setSelection(spinnerSelection);
		eTxtActTourSeatPrice.setText(Double.toString(tr.getSeat_price()));
		eTxtActTourFromAddress.setText(tr.getFromAddress().getAddressLine(1)
				+ "\n" + tr.getFromAddress().getPostalCode() + ", "
				+ tr.getFromAddress().getLocality() + "\n"
				+ tr.getFromAddress().getCountryName());
		eTxtActTourToAddress.setText(tr.getToAddress().getAddressLine(1) + "\n"
				+ tr.getToAddress().getPostalCode() + " - "
				+ tr.getToAddress().getLocality() + "\n"
				+ tr.getToAddress().getCountryName());

		// get passengers for tour
		String[] params = { Integer.toString(appUser.getUser_id()),
				Integer.toString(tr.getTour_id()) };
		// new SeatsAsyncGetSeats(ma, new
		// SeatsAsyncGetSeatsTaskCompleteListener(), params).execute();
		seats = new ArrayList<Seat>();
		setSeatList(tr.getSeats());
	}

	private void setSeatList(ArrayList<Seat> sts) {
		seats.clear();
		seats = sts;
		rowIconItems = new ArrayList<RowIconItem>();
		for (int i = 0; i < seats.size(); i++) {
			Seat st = seats.get(i);
			String stData = "Seat id: " + st.getSeat_id() + " "
					+ st.getStatus() + "\nUser id: " + st.getUser_id();
			String logo = st.getStatus().toLowerCase(Locale.getDefault());
			int id = getResources().getIdentifier(logo, "drawable",
					getPackageName());
			RowIconItem items = new RowIconItem(stData, id);
			rowIconItems.add(items);
		}

		adapter = new CustomIconAdapter(this, rowIconItems);
		Log.d("setListAdapter: ", "MyProfileFragment Started");
		ListView list = (ListView) findViewById(android.R.id.list);
		list.setAdapter(adapter);
		list.setOnItemClickListener(this);
		// Only in case the the appUser is the user who's executing the tour,
		// he/she can approve or decline a seatrequest
		if (appUser.getUser_id() == tr.getUser().getUser_id()) {
			registerForContextMenu(list);
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.tour, menu);
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
	public void onItemSelected(AdapterView<?> parent, View view, int position,
			long id) {
		// Set the selected item from the spinner as item displayed in the
		// spinner
		spnnActTourTour.setSelection(position);
		// Save selected vehicle in the tour object
		tr.setVehicle(vehicles.get(position));
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {

	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO update seat approved when selecting list long

	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		getMenuInflater().inflate(R.menu.seatstatus, menu);
		super.onCreateContextMenu(menu, v, menuInfo);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case R.id.item1:
			// TODO Change seat status in approved
			break;
		case R.id.item2:
			// TODO Change seat status in canceled
			break;
		case R.id.item3:
			// TODO Change seat status in declined
			break;
		case R.id.item4:
			// TODO Change seat status in pending
			break;
		default:
			break;
		}
		return super.onContextItemSelected(item);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnActTourUpdate:
			//TODO Update or Create tour
			break;
		case R.id.btnActTourSeatRequest:
			//Send Seat Request
			break;
		default:
			break;
		}

	}

}
