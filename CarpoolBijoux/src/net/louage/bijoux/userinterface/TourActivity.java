package net.louage.bijoux.userinterface;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.google.gson.Gson;

import net.louage.bijoux.R;
import net.louage.bijoux.constants.DateTime;
import net.louage.bijoux.constants.Installation;
import net.louage.bijoux.constants.NoticeDialogFragment;
import net.louage.bijoux.constants.NoticeDialogFragment.NoticeDialogListener;
import net.louage.bijoux.constants.SharedPreferences;
import net.louage.bijoux.model.Country;
import net.louage.bijoux.model.Seat;
import net.louage.bijoux.model.Team;
import net.louage.bijoux.model.Tour;
import net.louage.bijoux.model.User;
import net.louage.bijoux.model.Vehicle;
import net.louage.bijoux.server.AddressAsyncGetCoordinates;
import net.louage.bijoux.server.AsTskArrayListCompleteListener;
import net.louage.bijoux.server.AsTskObjectCompleteListener;
import net.louage.bijoux.server.SeatAsyncCreateUpdate;
import net.louage.bijoux.server.SeatAsyncDelete;
import net.louage.bijoux.server.TourAsyncCreateUpdate;
import net.louage.bijoux.server.TourAsyncDelete;
import net.louage.bijoux.service.SyncTrackingDataService;
import net.louage.bijoux.service.TourTrackingDataService;
import net.louage.bijoux.sqlite.SchemaHelper;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.location.Address;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

public class TourActivity extends FragmentActivity implements OnItemSelectedListener,
		OnItemClickListener, OnClickListener, NoticeDialogListener {
	private EditText eTxtActTourTime;
	private Spinner spnnActTourVehicle;
	private Spinner spnnActTourTeam;
	private EditText eTxtActTourSeatPrice;
	private EditText eTxtActTourFromAddress;
	private EditText eTxtActTourFromPostCode;
	private EditText eTxtActTourFromCity;
	private AutoCompleteTextView eTxtActTourFromCountry;
	private EditText eTxtActTourToAddress;
	private EditText eTxtActTourToPostCode;
	private EditText eTxtActTourToCity;
	private AutoCompleteTextView eTxtActTourToCountry;
	private Button btnActTourUpdate;
	private Button btnActTourSeatRequest;
	private Button btnActTourStartTracking;
	private Button btnFromAddress;
	private Button btnToAddress;
	private EditText eTxtActTourDate;
	private ArrayAdapter<String> adapterCountries;
	private Tour tr;
	User appUser;
	ArrayList<Vehicle> vehicles;
	ArrayList<Team> teams;
	ArrayList<Seat> seats;
	private List<RowIconItem> rowIconItems;
	CustomIconAdapter adapter;
	List<String> list;
	List<String> listTeams;
	int spinnerSelectionVehicle;
	int spinnerSelectionTeam;
	private static final String COMPLETED_TRANSACTION_SUCCESFULL = "Completed transaction succesfull";
	private static final String COMPLETED_TRANSACTION_UNSUCCESFULL = "Transaction not completed";
	private static final int GET_SEAT_INFO = 1;
	private static final int DATE_DIALOG_ID = 100;
	private static final int TIME_DIALOG_ID = 200;
	Boolean deleted = false;
	Boolean newTour = false;
	private boolean syncStarted=false;
	private boolean trackingStarted=false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// String tag = "TourActivity onCreate";
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tour);
		// Get Tour from intent
		String jsonTour = getIntent().getStringExtra("jsonTour");
		Gson gson = new Gson();
		tr = gson.fromJson(jsonTour, Tour.class);
		// Log.d(tag, "Team: "+ tr.getTeam().getTeam());
		// Log.d(tag, "Team id: "+tr.getTeam().getTeam_id());
		// Get appUser Vehicle List;
		appUser = SharedPreferences.getUser(this);
		vehicles = new ArrayList<Vehicle>();
		vehicles = appUser.getVehicles();
		teams = new ArrayList<Team>();
		teams = appUser.getMemberOf();
		eTxtActTourDate = (EditText) findViewById(R.id.eTxtActTourDate);
		
		eTxtActTourTime = (EditText) findViewById(R.id.eTxtActTourTime);
		spnnActTourVehicle = (Spinner) findViewById(R.id.spnnActTourVehicle);
		spnnActTourTeam = (Spinner) findViewById(R.id.spnnActTourTeam);
		eTxtActTourSeatPrice = (EditText) findViewById(R.id.eTxtActTourSeatPrice);
		eTxtActTourFromAddress = (EditText) findViewById(R.id.eTxtActTourFromAddress);
		eTxtActTourFromPostCode = (EditText) findViewById(R.id.eTxtActTourFromPostCode);
		eTxtActTourFromCity = (EditText) findViewById(R.id.eTxtActTourFromCity);
		eTxtActTourFromCountry = (AutoCompleteTextView) findViewById(R.id.eTxtActTourFromCountry);
		eTxtActTourToAddress = (EditText) findViewById(R.id.eTxtActTourToAddress);
		eTxtActTourToPostCode = (EditText) findViewById(R.id.eTxtActTourToPostCode);
		eTxtActTourToCity = (EditText) findViewById(R.id.eTxtActTourToCity);
		eTxtActTourToCountry = (AutoCompleteTextView) findViewById(R.id.eTxtActTourToCountry);
		btnActTourUpdate = (Button) findViewById(R.id.btnActTourUpdate);
		btnActTourSeatRequest = (Button) findViewById(R.id.btnActTourSeatRequest);
		btnActTourStartTracking = (Button) findViewById(R.id.btnActTourStartTracking);
		btnFromAddress = (Button)findViewById(R.id.btnFromAddress);
		btnToAddress = (Button)findViewById(R.id.btnToAddress);
		btnFromAddress.setOnClickListener(this);
		btnToAddress.setOnClickListener(this);
		
		if (tr.getTour_id()==0) {
			prepareNewTour();
		}
		
		// When the Tour was initiated by the appUser, request seat button shouldn't be visible
		// Only the update tour should and tracking should be visible
		if (appUser.getUser_id() == tr.getUser().getUser_id()) {
			btnActTourSeatRequest.setVisibility(View.INVISIBLE);
			btnActTourStartTracking.setOnClickListener(this);
			btnActTourUpdate.setOnClickListener(this);
			// If the appUser is the user that offers a tour, the vehicle
			// spinner must be build from vehicles of the appUser
			setArrayAdapterVehicleList();
			setEditTextEditable();
		} else {
			// Remove update tour, delete option and put EditText fields not editable
			btnActTourUpdate.setVisibility(View.INVISIBLE);
			btnActTourStartTracking.setVisibility(View.INVISIBLE);
			btnActTourSeatRequest.setOnClickListener(this);
			setEditTextNotEditable();
			// If the appUser isn't the user that offers a tour, the vehicle
			// spinner must be build from the vehicle in the tour
			list = new ArrayList<String>();
			list.add(tr.getVehicle().getBrand() + " - "
					+ tr.getVehicle().getLicenseplate());
			spinnerSelectionVehicle = 0;

		}
		
		

		// Link spinner Vehicle to adapter
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, list);
		dataAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spnnActTourVehicle.setAdapter(dataAdapter);
		// Spinner item selection Listener
		spnnActTourVehicle.setOnItemSelectedListener(this);
		// Set text of Teams
		listTeams = new ArrayList<String>();
		for (int i = 0; i < teams.size(); i++) {
			Team tm = new Team();
			tm = teams.get(i);
			listTeams.add("Team " + tm.getTeamname());
			if (tm.getTeam_id() == tr.getTeam().getTeam_id()) {
				spinnerSelectionTeam = i;
			}
		}
		// Link spinner Team to adapter
		ArrayAdapter<String> dataAdapterTeam = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, listTeams);
		dataAdapterTeam.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spnnActTourTeam.setAdapter(dataAdapterTeam);
		// Spinner item selection Listener
		spnnActTourTeam.setOnItemSelectedListener(this);
		seats = new ArrayList<Seat>();
		rowIconItems = new ArrayList<RowIconItem>();
		
		if (tr.getTour_id()>0) {
			String tourDate = DateTime.getStrDateStamp(tr.getDate());
			String tourtime = DateTime.getStrTimeStampShort(tr.getTime());
			eTxtActTourDate.setText(tourDate);
			eTxtActTourTime.setText(tourtime);

			// Set current vehicle for the tour as selected spinner item
			spnnActTourVehicle.setSelection(spinnerSelectionVehicle);
			spnnActTourTeam.setSelection(spinnerSelectionTeam);
			eTxtActTourSeatPrice.setText(Double.toString(tr.getSeat_price()));
			// Show from-address in UI
			eTxtActTourFromAddress.setText(tr.getFromAddress().getAddressLine(0));
			eTxtActTourFromPostCode.setText(tr.getFromAddress().getPostalCode());
			eTxtActTourFromCity.setText(tr.getFromAddress().getLocality());
			eTxtActTourFromCountry.setText(tr.getFromAddress().getCountryName());
			// Show to-address in UI
			eTxtActTourToAddress.setText(tr.getToAddress().getAddressLine(0));
			eTxtActTourToPostCode.setText(tr.getToAddress().getPostalCode());
			eTxtActTourToCity.setText(tr.getToAddress().getLocality());
			eTxtActTourToCountry.setText(tr.getToAddress().getCountryName());
			setSeatList(tr.getSeats());
			
		}

		// buildCountriesArray();
		// 1. Get the array from the Android String Resources
		String[] countries = getResources().getStringArray(R.array.country_array);
		// 2. Link the String array with a new ArrayAdapter
		adapterCountries = new ArrayAdapter<String>(this,android.R.layout.simple_dropdown_item_1line, countries);
		// 3. Link the adapter to the AutoCompleteTextView in the layout
		eTxtActTourFromCountry.setAdapter(adapterCountries);
		eTxtActTourToCountry.setAdapter(adapterCountries);
		setTitle(tr.getUser().getFirstname()+" "+tr.getUser().getLastname());
	}

	private void setArrayAdapterVehicleList() {
		list = new ArrayList<String>();
		for (int i = 0; i < vehicles.size(); i++) {
			Vehicle vh = new Vehicle();
			vh = vehicles.get(i);
			list.add(vh.getBrand() + " - " + vh.getLicenseplate());
			if (vh.getVehicle_id() == tr.getVehicle().getVehicle_id()) {
				spinnerSelectionVehicle = i;
			}
		}
	}

	private void setEditTextNotEditable() {
		eTxtActTourDate.setOnClickListener(null);
		eTxtActTourTime.setOnClickListener(null);
		eTxtActTourDate.setFocusable(false);
		// user touches widget on phone with touch screen
		eTxtActTourDate.setFocusableInTouchMode(false);
		// user navigates with wheel and selects widget
		eTxtActTourDate.setClickable(false);

		eTxtActTourTime.setFocusable(false);
		eTxtActTourTime.setFocusableInTouchMode(false);
		eTxtActTourTime.setClickable(false);

		spnnActTourVehicle.setActivated(false);
		spnnActTourVehicle.setFocusableInTouchMode(false);
		spnnActTourVehicle.setClickable(false);

		spnnActTourTeam.setActivated(false);
		spnnActTourTeam.setFocusableInTouchMode(false);
		spnnActTourTeam.setClickable(false);
		
		eTxtActTourSeatPrice.setFocusable(false);
		eTxtActTourSeatPrice.setFocusableInTouchMode(false);
		eTxtActTourSeatPrice.setClickable(false);

		eTxtActTourFromAddress.setFocusable(false);
		eTxtActTourFromAddress.setFocusableInTouchMode(false);
		eTxtActTourFromAddress.setClickable(false);
		
		eTxtActTourFromPostCode.setFocusable(false);
		eTxtActTourFromPostCode.setFocusableInTouchMode(false);
		eTxtActTourFromPostCode.setClickable(false);

		eTxtActTourFromCity.setFocusable(false);
		eTxtActTourFromCity.setFocusableInTouchMode(false);
		eTxtActTourFromCity.setClickable(false);

		eTxtActTourFromCountry.setFocusable(false);
		eTxtActTourFromCountry.setFocusableInTouchMode(false);
		eTxtActTourFromCountry.setClickable(false);

		eTxtActTourToAddress.setFocusable(false);
		eTxtActTourToAddress.setFocusableInTouchMode(false);
		eTxtActTourToAddress.setClickable(false);
		
		eTxtActTourToPostCode.setFocusable(false);
		eTxtActTourToPostCode.setFocusableInTouchMode(false);
		eTxtActTourToPostCode.setClickable(false);
		
		eTxtActTourToCity.setFocusable(false);
		eTxtActTourToCity.setFocusableInTouchMode(false);
		eTxtActTourToCity.setClickable(false);
		
		eTxtActTourToCountry.setFocusable(false);
		eTxtActTourToCountry.setFocusableInTouchMode(false);
		eTxtActTourToCountry.setClickable(false);
	}

	private void setEditTextEditable() {
		//Date and Time are default not editable because they will be changed by a Date- and TimePicker
		eTxtActTourDate.setFocusable(false);
		eTxtActTourDate.setFocusableInTouchMode(false);
		eTxtActTourDate.setClickable(false);
		eTxtActTourTime.setFocusable(false);
		eTxtActTourTime.setFocusableInTouchMode(false);
		eTxtActTourTime.setClickable(false);
		eTxtActTourDate.setOnClickListener(this);
		eTxtActTourTime.setOnClickListener(this);
		
		spnnActTourVehicle.setActivated(true);
		// user touches widget on phone with touch screen
		spnnActTourVehicle.setFocusableInTouchMode(true);
		// user navigates with wheel and selects widget
		spnnActTourVehicle.setClickable(true);
		
		spnnActTourTeam.setActivated(true);
		spnnActTourTeam.setFocusableInTouchMode(true);
		spnnActTourTeam.setClickable(true);
		
		eTxtActTourSeatPrice.setFocusable(true);
		eTxtActTourSeatPrice.setFocusableInTouchMode(true);
		eTxtActTourSeatPrice.setClickable(true);
		
		eTxtActTourFromAddress.setFocusable(true);
		eTxtActTourFromAddress.setFocusableInTouchMode(true);
		eTxtActTourFromAddress.setClickable(true);
		
		eTxtActTourFromPostCode.setFocusable(true);
		eTxtActTourFromPostCode.setFocusableInTouchMode(true);
		eTxtActTourFromPostCode.setClickable(true);
		
		eTxtActTourFromCity.setFocusable(true);
		eTxtActTourFromCity.setFocusableInTouchMode(true);
		eTxtActTourFromCity.setClickable(true);
		
		eTxtActTourFromCountry.setFocusable(true);
		eTxtActTourFromCountry.setFocusableInTouchMode(true);
		eTxtActTourFromCountry.setClickable(true);
		
		eTxtActTourToAddress.setFocusable(true);
		eTxtActTourToAddress.setFocusableInTouchMode(true);
		eTxtActTourToAddress.setClickable(true);
		
		eTxtActTourToPostCode.setFocusable(true);
		eTxtActTourToPostCode.setFocusableInTouchMode(true);
		eTxtActTourToPostCode.setClickable(true);
		
		eTxtActTourToCity.setFocusable(true);
		eTxtActTourToCity.setFocusableInTouchMode(true);
		eTxtActTourToCity.setClickable(true);
		
		eTxtActTourToCountry.setFocusable(true);
		eTxtActTourToCountry.setFocusableInTouchMode(true);
		eTxtActTourToCountry.setClickable(true);		
	}

	/*
	 * @param ArrayList<Seat> This method refreshes the list with seats Once a
	 * request for a seat has been added or changed, this method should be
	 * called
	 */
	private void setSeatList(ArrayList<Seat> sts) {
		rowIconItems.clear();
		seats = sts;
		Boolean requestDone=false;
		int removeIndex=-1;
		for (int i = 0; i < seats.size(); i++) {
			Seat st = seats.get(i);
			if (tr.getUser().getUser_id()==st.getUser_id()) {
				removeIndex=i;
			}else{
				if (st.isPaid()) {
				} else {
				}
				if (st.getUser_id()==appUser.getUser_id()) {
					requestDone=true;
				}
				User seatUser = new User();
				SchemaHelper sh = new SchemaHelper(this);
				seatUser=sh.userSelectId(st.getUser_id());
				sh.close();
				String stData = null;
				if (seatUser!=null) {
					stData = seatUser.getFirstname() + " "+ seatUser.getLastname();
				} else {
					stData = "User id: " + st.getUser_id();
				}
				if (tr.getUser().getUser_id()==appUser.getUser_id()) {
					//stData = stData +"\nPayment: "+ payment;
					stData = stData +"\nFrom: "+ st.getPickupAddress();
					stData = stData +"\nTo: "+ st.getDropoffAddress();
				} else {
					//stData = stData +"\nStatus: "+ st.getStatus();
					stData = stData +"\nFrom: "+ st.getPickupAddress();
					stData = stData +"\nTo: "+ st.getDropoffAddress();
				}
				String logo = st.getStatus().toLowerCase(Locale.getDefault());
				int id = getResources().getIdentifier(logo, "drawable",
						getPackageName());
				RowIconItem items = new RowIconItem(stData, id);
				rowIconItems.add(items);
			}
		}
		
		if (removeIndex>=0) {
			seats.remove(removeIndex);
		}
		
		if (requestDone==true) {
			btnActTourSeatRequest.setText(R.string.act_tour_btnActTourSeatCancel);
		}else {
			btnActTourSeatRequest.setText(R.string.act_tour_btnActTourSeatRequest);
		}
		adapter = new CustomIconAdapter(this, rowIconItems);
		ListView list = (ListView) findViewById(android.R.id.list);
		list.setAdapter(adapter);

		list.setOnItemClickListener(this);
		// Only in case the the appUser is the user who's executing the tour,
		// he/she can approve or decline a seat request
		if (appUser.getUser_id() == tr.getUser().getUser_id()) {
			registerForContextMenu(list);
		}
		tr.setSeats(seats);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.tour, menu);
		// If the appUser isn't the user that initiated this tour, he/she
		// shouldn't be able to delete this tour
		if (appUser.getUser_id() != tr.getUser().getUser_id()) {
			menu.removeItem(R.id.delete_tour);
			menu.removeItem(R.id.sync_tracking_data);
		}
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		String tag = "TourActivity onOptionsItemSelected";
		int id = item.getItemId();
		switch (id) {
		case R.id.create_new_tour:
			// Blank out EditText fields and change caption of update button
			// Change caption of update button to create_new_tour
			prepareNewTour();
			break;
		
		case R.id.delete_tour:
			// Create an instance of the dialog fragment and show it
			DialogFragment dialog = new NoticeDialogFragment();
			FragmentManager fragmentManager = getFragmentManager();
			dialog.show(fragmentManager, "Delete Tour");
			break;
		
		case R.id.sync_tracking_data:
			if (syncStarted) {
				stopService(new Intent(this, SyncTrackingDataService.class));
				syncStarted=false;
				Log.d(tag, "tracking Stopped");
			} else {
				Intent intent = new Intent(this, SyncTrackingDataService.class);
				startService(intent);
				syncStarted=true;
				Log.d(tag, "tracking Started");
			}
			break;
		
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	private void prepareNewTour() {
		String tag = "TourActivity prepareNewTour";
		eTxtActTourDate.setText(DateTime.getStrDateStamp(new Date()));
		eTxtActTourTime.setText(DateTime.getStrTimeStampShort(new Date()));
		spnnActTourVehicle.setSelection(0);
		spnnActTourTeam.setSelection(0);
		eTxtActTourSeatPrice.setText("0.00");
		eTxtActTourFromAddress.setText("");
		eTxtActTourFromPostCode.setText("");
		eTxtActTourFromCity.setText("");
		eTxtActTourFromCountry.setText("");
		eTxtActTourToAddress.setText("");
		eTxtActTourToPostCode.setText("");
		eTxtActTourToCity.setText("");
		eTxtActTourToCountry.setText("");
		// blank out tour object
		tr = null;
		// Build a new tour object with id=0
		tr = new Tour();
		tr.setTour_id(0);
		tr.setUser(net.louage.bijoux.constants.SharedPreferences
				.getUser(this));
		tr.setVehicle(vehicles.get(0));
		tr.setTeam(teams.get(0));
		Log.d(tag, "Team: " + tr.getTeam().getTeamname());
		Log.d(tag, "Team id: " + tr.getTeam().getTeam_id());
		btnActTourSeatRequest.setVisibility(View.INVISIBLE);
		btnActTourUpdate.setVisibility(View.VISIBLE);
		btnActTourUpdate.setText(R.string.act_tour_btnActTourCreate);
		btnActTourUpdate.setOnClickListener(this);
		if (seats!=null) {
			seats.clear();
			setSeatList(seats);
		}
		
		setArrayAdapterVehicleList();
		setEditTextEditable();
		setTitle(getResources().getString(R.string.act_tour_new)+" "+appUser.getFirstname());
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position,
			long id) {
		switch (parent.getId()) {
		case R.id.spnnActTourVehicle:
			// Set the selected item from the spinner as item displayed in the
			// spinner
			spnnActTourVehicle.setSelection(position);
			// Save selected vehicle in the tour object
			tr.setVehicle(vehicles.get(position));
			break;
		case R.id.spnnActTourTeam:
			// Set the selected item from the spinner as item displayed in the
			// spinner
			spnnActTourTeam.setSelection(position);
			// Save selected vehicle in the tour object
			tr.setTeam(teams.get(position));
			break;
		default:
			break;
		}

	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {
		
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		//Toast.makeText(this, "Item in List was clicked", Toast.LENGTH_SHORT).show();
		Seat st = new Seat();
		st=seats.get(position);
		if (appUser.getUser_id()==st.getUser_id()) {
			Intent intent = new Intent(this, SeatActivity.class);
			Gson gson = new Gson();
			String jsonSeat = gson.toJson(st);
			intent.putExtra("jsonSeat", jsonSeat);
			//startActivity(intent);
			startActivityForResult(intent, GET_SEAT_INFO);
		}
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		getMenuInflater().inflate(R.menu.seatstatus, menu);
		/*
		 * ListView lv = (ListView) v; AdapterView.AdapterContextMenuInfo acmi =
		 * (AdapterContextMenuInfo) menuInfo; Seat st =
		 * (Seat)lv.getItemAtPosition(acmi.position);
		 */
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		Seat st = tr.getSeats().get(info.position);
		switch (item.getItemId()) {
		case R.id.seat_status_approved:
			// Change seat object
			st.setStatus(getResources().getString(
					R.string.act_tour_seat_choice_approved));
			// Change seat in mysql
			String[] paramsApproved = getSeatParams(st);
			new SeatAsyncCreateUpdate(this,
					new CreateOrUpdateSeatTaskCompleteListener(),
					paramsApproved).execute();
			break;
		case R.id.seat_status_canceled:
			// Change seat object
			st.setStatus(getResources().getString(
					R.string.act_tour_seat_choice_canceled));
			// Change seat in mysql
			String[] paramsCanceled = getSeatParams(st);
			new SeatAsyncCreateUpdate(this,
					new CreateOrUpdateSeatTaskCompleteListener(),
					paramsCanceled).execute();
			break;
		case R.id.seat_status_delete:
			// Change seat object
			st.setStatus(getResources().getString(R.string.act_tour_seat_choice_delete));
			// Change seat in mysql
			String[] paramsDeclined = getSeatParams(st);
			new SeatAsyncDelete(this, new DeleteSeatTaskCompleteListener(),	paramsDeclined).execute();
			break;
		case R.id.seat_status_pending:
			// Change seat object
			st.setStatus(getResources().getString(
					R.string.act_tour_seat_choice_pending));
			// Change seat in mysql
			String[] paramsPending = getSeatParams(st);
			new SeatAsyncCreateUpdate(this,
					new CreateOrUpdateSeatTaskCompleteListener(), paramsPending)
					.execute();
			break;
		case R.id.seat_status_not_paid:
			// Change seat object
			st.setPaid(false);
			// Change seat in mysql
			String[] paramsNotPaid = getSeatParams(st);
			new SeatAsyncCreateUpdate(this,
					new CreateOrUpdateSeatTaskCompleteListener(), paramsNotPaid)
					.execute();
			break;
		case R.id.seat_status_paid:
			// Change seat object
			st.setPaid(true);
			// Change seat in mysql
			String[] paramsPaid = getSeatParams(st);
			new SeatAsyncCreateUpdate(this,	new CreateOrUpdateSeatTaskCompleteListener(), paramsPaid)
					.execute();
			break;
		default:
			break;
		}
		return super.onContextItemSelected(item);
	}

	class CreateOrUpdateSeatTaskCompleteListener implements
			AsTskObjectCompleteListener<Seat> {
		@Override
		public void onTaskComplete(Seat st) {
			Log.d("CreateOrUpdateSeatTaskCompleteListener: ",
					"onTaskComplete(Seat st) Started");
			ArrayList<Seat> seatsUpdate = new ArrayList<Seat>();
			seatsUpdate = seats;
			Boolean update = false;
			// TourActivity.this.onTaskCompleteCreateOrUpdate(st);
			for (int i = 0; i < seatsUpdate.size(); i++) {
				Seat seat = seats.get(i);
				Log.d("onTaskComplete", "Seat id: " + seat.getSeat_id());
				if (st.getSeat_id() == seat.getSeat_id()) {
					// Replace the old seat object with the updated object
					seatsUpdate.set(i, st);
					update = true;
				}
			}
			// Check if the Seat st already exists in the ArrayList
			if (update == false) {
				// New Seat request
				seatsUpdate.add(st);
			}
			// tr.setSeats(seatsUpdate);
			// Update seat list in user interface
			setSeatList(seatsUpdate);
			Toast.makeText(getApplicationContext(),
					COMPLETED_TRANSACTION_SUCCESFULL, Toast.LENGTH_LONG).show();
		}
	}

	class DeleteSeatTaskCompleteListener implements
			AsTskObjectCompleteListener<Seat> {
		@Override
		public void onTaskComplete(Seat st) {
			Log.d("DeleteSeatTaskCompleteListener: ",
					"onTaskComplete(Seat st) Started");
			// ArrayList<Seat> seatsAfterDelete = new ArrayList<Seat>();
			// seatsAfterDelete = seats;
			Boolean delete = false;
			// TourActivity.this.onTaskCompleteCreateOrUpdate(st);
			Log.d("onTaskComplete", "Seat id: " + st.getSeat_id());
			User cancelSeatUser=new User();
			for (int i = 0; i < seats.size(); i++) {
				Seat seat = seats.get(i);
				// Log.d("onTaskComplete", "Seat id: " + seat.getSeat_id());
				if (st.getSeat_id() == seat.getSeat_id()) {
					//Get User information about the canceled seat
					SchemaHelper sh= new SchemaHelper(getApplicationContext());
					cancelSeatUser=sh.userSelectId(seat.getUser_id());
					sh.close();
					// Replace the old seat object with the deleted object
					seats.remove(i);
					i--;
					delete = true;
				}
			}
			// Check if the Seat is deleted
			if (delete == false) {
				Toast.makeText(getApplicationContext(),	COMPLETED_TRANSACTION_UNSUCCESFULL, Toast.LENGTH_LONG).show();
			} else{
				
				informTourOperatorCancelSeat(cancelSeatUser);
			}
			setSeatList(seats);
		}

	}
	
	private void informTourOperatorCancelSeat(User cancelSeatUser) {
		//Get user phone number for sending a text message about canceled seat
		String phone=tr.getUser().getPhone();
		String textBody=null;
		if (cancelSeatUser!=null) {
			textBody=getResources().getString(R.string.act_tour_sms_text_body)
					+" "+DateTime.getStrDateTimeStamp(tr.getDate())
					+" ("+tr.getFromAddress().getLocality()+" - "+tr.getToAddress().getLocality()
					+") "+getResources().getString(R.string.act_tour_sms_text_body2)
					+", "+cancelSeatUser.getFirstname();
		} else {
			textBody=getResources().getString(R.string.act_tour_sms_text_body)
					+" "+DateTime.getStrDateTimeStamp(tr.getDate())
					+" ("+tr.getFromAddress().getLocality()+" - "+tr.getToAddress().getLocality()
					+") "+getResources().getString(R.string.act_tour_sms_text_body2);
		}
		sendSMS(phone, textBody);
	}
	
	/**
	 * @param phone
	 * @param textMessage
	 * Extracted from <a href="http://developer.android.com/reference/android/telephony/SmsManager.html">Android documentation</a><br>
	 * The SmsManager manages SMS operations such as sending data to the given mobile device.<br>
	 * You can create this object by calling the static method SmsManager.getDefault()
	 */
	private void sendSMS(String phone, String textMessage) {
		//Log.d("sms message: ", message);
		//Log.d("sms phone: ", phoneNumber);
		SmsManager sms = SmsManager.getDefault();
		/*Divide a message text into several fragments, none bigger than the maximum SMS message size.
		 * the original message. Must not be null.
		 */
		ArrayList<String> parts=sms.divideMessage(textMessage);
		/*Send a multi-part text based SMS.
		 * The callee should have already divided the message into correctly sized parts by calling divideMessage.
		 */
		sms.sendMultipartTextMessage(phone, null, parts, null, null);
	}
	
	private String[] getSeatParams(Seat st) {
		String[] params = { Integer.toString(appUser.getUser_id()),
				Integer.toString(st.getSeat_id()),
				Integer.toString(st.getCreated_by_user_id()),
				st.getDevice_id(), Integer.toString(st.getTour_id()),
				Integer.toString(st.getUser_id()), st.getStatus(),
				Boolean.toString(st.isPaid()),
				st.getPickupAddress(),st.getDropoffAddress()};
		return params;
	}

	private String[] getTourParams(Tour tour) {
		if (tour.getTour_id() == 0) {
			newTour = true;
		}
		String tag = "TourActivity getTourParams";
		Log.d(tag, "Started");
		String appUserId = Integer.toString(appUser.getUser_id());
		String tourDate = DateTime.getStrDateStamp(tour.getDate());
		String tourTime = DateTime.getStrTimeStampShort(tour.getTime());
		String tourId = Integer.toString(tr.getTour_id());
		String userId = Integer.toString(tr.getUser().getUser_id());
		String vehicleId = Integer.toString(tr.getVehicle().getVehicle_id());
		Log.d(tag, "Teamname: " + tr.getTeam().getTeamname());
		Log.d(tag, "Team_id: " + tr.getTeam().getTeam_id());
		String teamId = Integer.toString(tr.getTeam().getTeam_id());
		String fromAddressLine0 = tr.getFromAddress().getAddressLine(0);
		String fromPostalCode = tr.getFromAddress().getPostalCode();
		String fromLocality = tr.getFromAddress().getLocality();
		String fromCountryCode = tr.getFromAddress().getCountryCode();
		String toAddressLine0 = tr.getToAddress().getAddressLine(0);
		String toPostalCode = tr.getToAddress().getPostalCode();
		String toLocality = tr.getToAddress().getLocality();
		String toCountryCode = tr.getToAddress().getCountryCode();
		String seatPrice = Double.toString(tr.getSeat_price());
		String[] params = { appUserId, tourId, tourDate, tourTime, userId,
				vehicleId, teamId, fromAddressLine0, fromPostalCode,
				fromLocality, fromCountryCode, toAddressLine0, toPostalCode,
				toLocality, toCountryCode, seatPrice };
		return params;
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnActTourUpdate:
			try {
				buildTourObjectfromUI();
			} catch (ParseException e) {
				e.printStackTrace();
			}
			String[] paramsTour = getTourParams(tr);
			new TourAsyncCreateUpdate(this,	new CreateOrUpdateTourTaskCompleteListener(), paramsTour).execute();
			break;
		case R.id.btnActTourSeatRequest:
			String btnSeatRequest=getResources().getString(R.string.act_tour_btnActTourSeatRequest);
			if (btnActTourSeatRequest.getText().toString()==btnSeatRequest) {
				// appUser sends a seat request for this tour
				Seat st = new Seat();
				st.setSeat_id(0);
				st.setCreated_by_user_id(appUser.getUser_id());
				st.setDevice_id(Installation.getInstallationID(this));
				st.setPaid(false);
				st.setStatus("Pending");
				st.setTour_id(tr.getTour_id());
				st.setUser_id(appUser.getUser_id());
				String[] paramsPaid = getSeatParams(st);
				new SeatAsyncCreateUpdate(this,	new CreateOrUpdateSeatTaskCompleteListener(), paramsPaid).execute();
			} else {
				//User wants to remove the seat request
				int deleteSeatAtIndex=-1;
				for (int i = 0; i < seats.size(); i++) {
					Seat cancelSeat = seats.get(i);
					if (appUser.getUser_id()==cancelSeat.getUser_id()) {
						cancelSeat.setStatus(getResources().getString(R.string.act_tour_seat_choice_delete));
						deleteSeatAtIndex=i;
					}
				}
				/*Check if the deleteSeatAtIndex has been changed
				 *This means the the object to be deleted was found in the ArrayList seats 
				 */
				if (deleteSeatAtIndex>=0) {
					/*Get the Seat object from the ArrayList
					 *and Start Async task for deleting record in MySQL database
					 */
					Seat st = seats.get(deleteSeatAtIndex);
					// Change seat in mysql
					String[] paramsDeclined = getSeatParams(st);
					new SeatAsyncDelete(this, new DeleteSeatTaskCompleteListener(),	paramsDeclined).execute();
				}
			}

			break;
			
		case R.id.btnActTourStartTracking:
			if (trackingStarted) {
				stopService(new Intent(this, TourTrackingDataService.class));
				Toast.makeText(this, "Tracking stopped", Toast.LENGTH_LONG).show();
				//Set Text on button back to initial text
				btnActTourStartTracking.setText(getText(R.string.act_tour_btnActTourStartTracking));
				trackingStarted=false;
				Intent intentsync = new Intent(this, SyncTrackingDataService.class);
				startService(intentsync);
			} else {
				Intent intent = new Intent(this, TourTrackingDataService.class);
				Gson gson = new Gson();
				String jsonTour = gson.toJson(tr);
				intent.putExtra("jsonTour", jsonTour);
				startService(intent);
				Toast.makeText(this, "Tracking started", Toast.LENGTH_LONG).show();
				//Set Text on button to stop text
				btnActTourStartTracking.setText(getText(R.string.act_tour_btnActTourStopTracking));
				trackingStarted=true;
			}
			
			break;
			
		case R.id.btnFromAddress:
			//Toast.makeText(this, "btnFromAddress was clicked", Toast.LENGTH_LONG).show();
			String[] paramsAddressFrom = getMapsParams(tr.getFromAddress());
			//Log.d("btnFromAddress Clicked with parameters", paramsAddressFrom.toString());
			new AddressAsyncGetCoordinates(this, new GetCoordinatesTaskCompleteListener(), paramsAddressFrom).execute();
			//Intent intentFrom = new Intent(this,MapsActivity.class);
			//intentFrom.putExtra("type", 1);
			//Gson gsonFrom = new Gson();
			//String jsonFromAddress = gsonFrom.toJson(tr.getFromAddress());
			//intentFrom.putExtra("jsonAddress", jsonFromAddress);
			//startActivity(intentFrom);
			break;
			
		case R.id.btnToAddress:
			//Toast.makeText(this, "btnToAddress was clicked", Toast.LENGTH_LONG).show();
			String[] paramsAddressTo = getMapsParams(tr.getToAddress());
			//Log.d("btnFromAddress Clicked with parameters", paramsAddressTo.toString());
			new AddressAsyncGetCoordinates(this, new GetCoordinatesTaskCompleteListener(), paramsAddressTo).execute();
			//Intent intentTo = new Intent(this,MapsActivity.class);
			//intentTo.putExtra("type", 1);
			//Gson gsonTo = new Gson();
			//String jsonToAddress = gsonTo.toJson(tr.getFromAddress());
			//intentTo.putExtra("jsonAddress", jsonToAddress);
			//startActivity(intentTo);
			break;
		case R.id.eTxtActTourDate:
			showDialog(DATE_DIALOG_ID);
			break;
		case R.id.eTxtActTourTime:
			showDialog(TIME_DIALOG_ID);
			break;
		default:
			
			break;
		}
	}
	
	
	
	class GetCoordinatesTaskCompleteListener implements
	AsTskArrayListCompleteListener<Address> {
		@Override
		public void onTaskComplete(ArrayList<Address> addresses) {
			Log.d("onTaskComplete: ", "FetchMyVehicleTaskCompleteListener Started");
			if (addresses.size()>0) {
				Address adr = new Address(null);
				adr=addresses.get(0);
				Log.d("onTaskComplete Address info:", adr.getAddressLine(0));
				Log.d("onTaskComplete Coördinates info:", "lat: "+ String.valueOf(adr.getLatitude())+" - lng: "+String.valueOf(adr.getLongitude()));
				Intent intentTo = new Intent(getApplicationContext(),MapsActivity.class);
				intentTo.putExtra("type", 1);
				Gson gsonTo = new Gson();
				String jsonToAddress = gsonTo.toJson(adr);
				intentTo.putExtra("jsonAddress", jsonToAddress);
				startActivity(intentTo);
			} else {
				Toast.makeText(getApplicationContext(), "No map location could be found for this address, please enter a valid address", Toast.LENGTH_LONG).show();
			}		
		}
	}

	private String[] getMapsParams(Address address) {
		String addressLine0 = address.getAddressLine(0);
		String postalCode = address.getPostalCode();
		String locality = address.getLocality();
		String country=null;
		String countryName = address.getCountryName();
		if (countryName==null) {
			country = address.getCountryCode();
		} else {
			country = countryName;
		}
		
		String geoAdress = addressLine0+", "+postalCode+" "+locality+" "+country;
		String[] params = { geoAdress };
		return params;
	}

	private void buildTourObjectfromUI() throws ParseException {
		//Date tourDate = DateTime.stringToDateMediumFormat(eTxtActTourDate.getText().toString());
		Date tourDate = DateTime.getDateFormString(eTxtActTourDate.getText().toString());
		tr.setDate(tourDate);
		Date tourTime = DateTime.getTimeFormStringShort(eTxtActTourTime.getText()
				.toString());
		tr.setTime(tourTime);
		// The spinner immediate sets the right vehicle in the tour object, so
		// this doesn't need to be saved
		tr.setSeat_price(Double.parseDouble(eTxtActTourSeatPrice.getText()
				.toString()));
		createFromAddressFromUI();
		createToAddressFromUI();
	}

	private void createFromAddressFromUI() {
		Address fromAddress = new Address(Locale.getDefault());
		fromAddress.setAddressLine(0, eTxtActTourFromAddress.getText()
				.toString());
		fromAddress.setPostalCode(eTxtActTourFromPostCode.getText().toString());
		fromAddress.setLocality(eTxtActTourFromCity.getText().toString());
		fromAddress.setCountryName(eTxtActTourFromCountry.getText().toString());
		SchemaHelper sh = new SchemaHelper(this);
		Country ct = sh.getCountry(eTxtActTourFromCountry.getText().toString());
		sh.close();
		if (ct != null) {
			fromAddress.setCountryCode(ct.getIso3166());
		}
		tr.setFromAddress(fromAddress);
	}

	private void createToAddressFromUI() {
		Address toAddress = new Address(Locale.getDefault());
		toAddress.setAddressLine(0, eTxtActTourToAddress.getText().toString());
		toAddress.setPostalCode(eTxtActTourToPostCode.getText().toString());
		toAddress.setLocality(eTxtActTourToCity.getText().toString());
		toAddress.setCountryName(eTxtActTourToCountry.getText().toString());
		SchemaHelper sh = new SchemaHelper(this);
		Country ct = sh.getCountry(eTxtActTourToCountry.getText().toString());
		sh.close();
		if (ct != null) {
			toAddress.setCountryCode(ct.getIso3166());
		}
		tr.setToAddress(toAddress);
	}

	class CreateOrUpdateTourTaskCompleteListener implements
			AsTskObjectCompleteListener<Tour> {
		@Override
		public void onTaskComplete(Tour tour) {
			Log.d("CreateOrUpdateTourTaskCompleteListener: ",
					"onTaskComplete(Tour tour) Started");
			tr = tour;
			if (tr.getSeats()!=null) {
				setSeatList(tr.getSeats());
			}
			btnActTourUpdate.setText(R.string.act_tour_btnActTourUpdate);
			onBackPressed();
		}
	}

	// The dialog fragment receives a reference to this Activity through the
	// Fragment.onAttach() callback, which it uses to call the following methods
	// defined by the NoticeDialogFragment.NoticeDialogListener interface
	public void onDialogPositiveClick(DialogFragment dialog) {
		String[] params = { Integer.toString(tr.getUser().getUser_id()),
				Integer.toString(tr.getTour_id()) };
		new TourAsyncDelete(this, new DeleteTourTaskCompleteListener(), params)
				.execute();
		Toast.makeText(this, R.string.act_tour_actb_del_tour,
				Toast.LENGTH_SHORT).show();
	}

	public void onDialogNegativeClick(DialogFragment dialog) {
		Toast.makeText(this, R.string.act_tour_actb_del_tour_canceled,
				Toast.LENGTH_SHORT).show();
	}

	class DeleteTourTaskCompleteListener implements
			AsTskObjectCompleteListener<Tour> {
		@Override
		public void onTaskComplete(Tour tour) {
			Log.d("DeleteTourTaskCompleteListener: ",
					"onTaskComplete(Tour tour) Started");
			for (int i = 0; i < seats.size(); i++) {
				Seat st = new Seat();
				st=seats.get(i);
				User informSeatUser=new User();
				SchemaHelper sh = new SchemaHelper(getApplicationContext());
				informSeatUser=sh.userSelectId(st.getUser_id());
				sh.close();
				informSeatUserCancelTour(informSeatUser);
			}
			tr = tour;
			deleted = true;
			onBackPressed();
		}
	}

	private void informSeatUserCancelTour(User informSeatUser) {
		//Get user phone number for sending a text message about canceled seat
		String phone=informSeatUser.getPhone();
		String textBody=null;
		textBody=getResources().getString(R.string.act_tour_sms_text_cancel_tour_body)
				+" "+DateTime.getStrDateTimeStamp(tr.getDate())
				+" ("+tr.getFromAddress().getLocality()+" - "+tr.getToAddress().getLocality()
				+") "+getResources().getString(R.string.act_tour_sms_text_cancel_tour_body2)
				+", "+tr.getUser().getFirstname();
		sendSMS(phone, textBody);
	}

	@Override
	public void onBackPressed() {
		String tag = "TourActivity onBackPressed";
		// super.onBackPressed();
		// String tag="TourActivity onBackPressed()";
		Intent intent = new Intent();
		Gson gson = new Gson();
		String jsonTour = gson.toJson(tr);
		Log.d(tag + " jsonTour:", jsonTour);

		intent.putExtra("jsonTour", jsonTour);
		intent.putExtra("tourDeleted", deleted);
		intent.putExtra("newTour", newTour);
		// Log.d(tag + " intent.getStringExtra:",
		// intent.getStringExtra("jsonTour"));

		setResult(RESULT_OK, intent);
		// setResult(RESULT_OK, getIntent().putExtra("jsonTour", jsonTour));

		// Toast.makeText(this, "onBackPressed was processed",
		// Toast.LENGTH_SHORT).show();
		finish();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (data==null) {
			Toast.makeText(this, "Intent data is null", Toast.LENGTH_SHORT).show();
		}
		if (requestCode == GET_SEAT_INFO) {
			ArrayList<Seat> seatsUpdate = new ArrayList<Seat>();
			if (resultCode == Activity.RESULT_OK) {
				Bundle b = data.getExtras();
				if (b != null) {
					//Retrieve seat from intent
					String jsonSeat = data.getStringExtra("jsonSeat");
					Gson gson = new Gson();
					Seat st = gson.fromJson(jsonSeat, Seat.class);
					seatsUpdate = seats;
					//Log.d(tag, "seatsUpdate size(): " + seatsUpdate.size());
					//Update the existing ArrayList seats with returned Seat tr
					for (int i = 0; i < seatsUpdate.size(); i++) {
						Seat seat = seats.get(i);							
						if (st.getSeat_id() == seat.getSeat_id()) {
							// Replace the old seat object with the updated object in the original ArrayList seats
							seatsUpdate.set(i, st);
							//Toast.makeText(getActivity(), "seat "+tr.getSeat_id()+" is updated.",Toast.LENGTH_SHORT).show();
							Log.d("onTaskComplete", "Updated seat: " + st.getSeat_id());
						}
					}
				} else {
					//Toast.makeText(getActivity(), "Bundle b was = null",
					//		Toast.LENGTH_SHORT).show();
				}
				//Log.d("onTaskComplete", "seats size(): " + seatsUpdate.size());
				setSeatList(seatsUpdate);
			}
		}

	}

	@Override
	protected Dialog onCreateDialog(int id, Bundle args) {
		
		switch (id) {
		case DATE_DIALOG_ID:
			Calendar c = Calendar.getInstance();
			if (tr.getDate()!=null) {
				c.setTime(tr.getDate());
			} else {
				int weekday = c.get(Calendar.DAY_OF_WEEK);  
				if (weekday != Calendar.SATURDAY){
					// calculate how days to add for next Saturday  
				    int days = (Calendar.SATURDAY - weekday);  
				    c.add(Calendar.DAY_OF_YEAR, days);
				}
			}
			
			int cyear = c.get(Calendar.YEAR);
			int cmonth = c.get(Calendar.MONTH);
			int cday = c.get(Calendar.DAY_OF_MONTH);
			return new DatePickerDialog(this,  mDateSetListener,  cyear, cmonth, cday);
		case TIME_DIALOG_ID:
			Calendar cTime = Calendar.getInstance();
			if (tr.getTime()!=null) {
				cTime.setTime(tr.getTime());
			} else {
				cTime.set(1970, 1, 1, 18, 0, 0);
			}
			
			int mHour = cTime.get(Calendar.HOUR_OF_DAY);
			int mMin = cTime.get(Calendar.MINUTE);
			return new TimePickerDialog(this,  TimePickerDialog.THEME_TRADITIONAL, mTimeSetListener,  mHour, mMin, true);
			
		}
		return null;
		}

		private DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {
			// onDateSet method
			public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
				Calendar cSelected = Calendar.getInstance();
				cSelected.set(year, monthOfYear, dayOfMonth);
				Date selectedDate=new Date();
				selectedDate=cSelected.getTime();
				//Toast.makeText(TourActivity.this, "Selected Date is ="+DateTime.getStrDateStamp(selectedDate), Toast.LENGTH_SHORT).show();
				TourActivity.this.tr.setDate(selectedDate);
				TourActivity.this.eTxtActTourDate.setText(DateTime.getStrDateStamp(selectedDate));
						
			}
		};
		
		private TimePickerDialog.OnTimeSetListener mTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
			// onTimeSet method
			@Override
			public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
				Calendar cSelected = Calendar.getInstance();
				cSelected.set(1970, 1, 1, hourOfDay, minute, 0);
				Date selectedTime=new Date();
				selectedTime=cSelected.getTime();
				//Toast.makeText(TourActivity.this, "Selected Time is ="+DateTime.getStrTimeStamp(selectedTime), Toast.LENGTH_SHORT).show();
				TourActivity.this.tr.setTime(selectedTime);
				TourActivity.this.eTxtActTourTime.setText(DateTime.getStrTimeStampShort(selectedTime));
			}
		};

		
}

