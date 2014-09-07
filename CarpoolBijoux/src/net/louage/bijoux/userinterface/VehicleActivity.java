package net.louage.bijoux.userinterface;

import net.louage.bijoux.R;
import net.louage.bijoux.constants.NoticeDialogFragment;
import net.louage.bijoux.constants.NoticeDialogFragment.NoticeDialogListener;
import net.louage.bijoux.model.Country;
import net.louage.bijoux.model.Vehicle;
import net.louage.bijoux.model.VehicleType;
import net.louage.bijoux.server.AsTskObjectCompleteListener;
import net.louage.bijoux.server.VehicleAsyncDelete;
import net.louage.bijoux.server.VehicleAsyncCreateUpdate;
import net.louage.bijoux.sqlite.CountryTable;
import net.louage.bijoux.sqlite.SchemaHelper;
import android.app.Activity;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.database.Cursor;
import android.os.Bundle;
//import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class VehicleActivity extends Activity implements View.OnClickListener,
		NoticeDialogListener {
	private static final String CREATE = "Create";
	private static final String VEHICLE_WASN_T_DELETED_TRY_AGAIN_LATER = "Vehicle wasn't deleted, try again later";
	private static final String COMPLETED_TRANSACTION_SUCCESFULL = "Completed transaction succesfull";
	private TextView txtActVehAutonumberId;
	private EditText eTxtActVehLicenseplate;
	private AutoCompleteTextView autComplActVehCountry;
	private EditText eTxtActVehNoOfPassengers;
	private AutoCompleteTextView autComplActVehBrand;
	private EditText eTxtActVehVehicleType;
	private Button btnActVehUpdate;
	private Button btnActVehCreate;
	private SchemaHelper sh;
	ArrayAdapter<String> adapter;
	ArrayAdapter<String> adapter2;
	Vehicle vh;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_vehicle);
		txtActVehAutonumberId = (TextView) findViewById(R.id.txtActVehAutonumberId);
		eTxtActVehLicenseplate = (EditText) findViewById(R.id.eTxtActVehLicenseplate);
		autComplActVehCountry = (AutoCompleteTextView) findViewById(R.id.autComplActVehCountry);
		// Link the String array with a new ArrayAdapter
		String[] countries = getResources().getStringArray(R.array.country_array);
		adapter = new ArrayAdapter<String>(this,android.R.layout.simple_dropdown_item_1line, countries);
		// Link the adapter to the AutoCompleteTextView in the layout
		autComplActVehCountry.setAdapter(adapter);
		eTxtActVehNoOfPassengers = (EditText) findViewById(R.id.eTxtActVehNoOfPassengers);
		// Build AutoCompleteTextView for selecting vehicle brands retrieved by string array
		autComplActVehBrand = (AutoCompleteTextView) findViewById(R.id.autComplActVehBrand);
		// Link the String array with a new ArrayAdapter
		String[] brands = getResources().getStringArray(R.array.brand_array);
		adapter2 = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, brands);
		autComplActVehBrand.setAdapter(adapter2);
		eTxtActVehVehicleType = (EditText) findViewById(R.id.eTxtActVehVehicleType);
		btnActVehUpdate = (Button) findViewById(R.id.btnActVehUpdate);
		btnActVehUpdate.setOnClickListener(this);
		btnActVehCreate = (Button) findViewById(R.id.btnActVehCreate);
		btnActVehCreate.setOnClickListener(this);
		
		// Get Vehicle from intent
		vh = (Vehicle) getIntent().getSerializableExtra("Vehicle");
		if (vh!=null) {
			btnActVehCreate.setVisibility(View.INVISIBLE);
			btnActVehUpdate.setVisibility(View.VISIBLE);
			txtActVehAutonumberId.setText(getResources().getString(R.string.act_vehicle_id)	+ ": " + vh.getVehicle_id());
			eTxtActVehLicenseplate.setText(vh.getLicenseplate());
			// Build AutoCompleteTextView for selecting countries retrieved by string array
			autComplActVehCountry.setText(getCountry(vh.getCountry().getIso3166()).getDescription());
			eTxtActVehNoOfPassengers.setText(Integer.toString(vh.getNumberOfPassengers()));
			autComplActVehBrand.setText(vh.getBrand());
			eTxtActVehVehicleType.setText(vh.getType().getType());
		} else {
			// Activity was started to create a new vehicle
			prepareForNewVehicle();
		}
		
		setTitle(R.string.act_vehicle_title);
		
	}

	private Country getCountry(String iso3166) {
		sh = new SchemaHelper(this);
		sh.openDataBase();
		//Log.d("iso3166", iso3166);
		Country ct = new Country();
		Cursor c = sh.getCountryDescription(iso3166);
		String countryDescription = "";
		int countryId = 0;
		while (c.moveToNext()) {
			countryDescription = c.getString(c
					.getColumnIndex(CountryTable.DESCRIPTION));
			countryId = c.getInt(c.getColumnIndex(CountryTable.ID));
		}
		ct.setId(countryId);
		ct.setDescription(countryDescription);
		ct.setIso3166(iso3166);
		sh.close();
		return ct;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.vehicle, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		switch (id) {
		case R.id.create_new_vehicle:
			prepareForNewVehicle();
			break;
		case R.id.delete_vehicle:
			// Create an instance of the dialog fragment and show it
			DialogFragment dialog = new NoticeDialogFragment();
			FragmentManager fragmentManager = getFragmentManager();
			dialog.show(fragmentManager, "Notice Dialog Fragment First test!");
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	private void prepareForNewVehicle() {
		// Blank out EditText fields and change caption of update button Create Vehicle
		btnActVehUpdate.setVisibility(View.INVISIBLE);
		btnActVehCreate.setVisibility(View.VISIBLE);
		txtActVehAutonumberId.setText(getResources().getString(R.string.act_vehicle_id) + ": *");
		eTxtActVehLicenseplate.setText("");
		autComplActVehCountry.setText("");
		eTxtActVehNoOfPassengers.setText("");
		autComplActVehBrand.setText("");
		eTxtActVehVehicleType.setText("");
		btnActVehUpdate.setText(CREATE);
		vh = null;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnActVehUpdate:
			updateOrCreateVehicleObject();
			String[] params = getVehicleParams();
			new VehicleAsyncCreateUpdate(this, new CreateOrUpdateVehicleTaskCompleteListener(),
					params).execute();
			break;
		case R.id.btnActVehCreate:
			updateOrCreateVehicleObject();
			String[] paramsCreate = getVehicleParams();
			new VehicleAsyncCreateUpdate(this, new CreateOrUpdateVehicleTaskCompleteListener(),
					paramsCreate).execute();
			break;

		default:
			break;
		}
	}

	private String[] getVehicleParams() {
		String[] params = { Integer.toString(vh.getUser_id()),
				Integer.toString(vh.getVehicle_id()),
				vh.getLicenseplate(),
				vh.getCountry().getIso3166(),
				Integer.toString(vh.getNumberOfPassengers()),
				vh.getBrand(),
				vh.getType().getType() };
		return params;
	}

	private Vehicle updateOrCreateVehicleObject() {
		if (vh == null) {
			// In this case we need to create a new vehicle
			vh = new Vehicle();
			vh.setVehicle_id(0);
		}
		vh.setUser_id(net.louage.bijoux.constants.SharedPreferences
				.getUserId(this));
		vh.setLicenseplate(eTxtActVehLicenseplate.getText().toString());
		String countryDescription = autComplActVehCountry.getText().toString();
		SchemaHelper sh = new SchemaHelper(this);
		Country country = sh.getCountry(countryDescription);
		sh.close();
		vh.setCountry(country);
		vh.setNumberOfPassengers(Integer.parseInt(eTxtActVehNoOfPassengers
				.getText().toString()));
		vh.setBrand(autComplActVehBrand.getText().toString());
		VehicleType vt = new VehicleType(eTxtActVehVehicleType.getText()
				.toString());
		vh.setType(vt);
		return vh;

	}

	class CreateOrUpdateVehicleTaskCompleteListener implements
			AsTskObjectCompleteListener<Vehicle> {
		@Override
		public void onTaskComplete(Vehicle vh) {
			//Log.d("onTaskComplete: ",
			//		"UpdateVehicleTaskCompleteListener Started");
			VehicleActivity.this.onTaskCompleteCreateOrUpdate(vh);
		}
	}

	public void onTaskCompleteCreateOrUpdate(Vehicle vh2) {
		Toast.makeText(this, COMPLETED_TRANSACTION_SUCCESFULL, Toast.LENGTH_LONG).show();
		finish();

	}

	// The dialog fragment receives a reference to this Activity through the
	// Fragment.onAttach() callback, which it uses to call the following methods
	// defined by the NoticeDialogFragment.NoticeDialogListener interface
	@Override
	public void onDialogPositiveClick(DialogFragment dialog) {
		String[] params = { Integer.toString(vh.getUser_id()),
				Integer.toString(vh.getVehicle_id()) };
		new VehicleAsyncDelete(this, new DeleteVehicleTaskCompleteListener(), params)
				.execute();
	}

	@Override
	public void onDialogNegativeClick(DialogFragment dialog) {
		Toast.makeText(this, R.string.act_vehicle_actb_del_vehicle_canceled,
				Toast.LENGTH_SHORT).show();
	}

	class DeleteVehicleTaskCompleteListener implements
			AsTskObjectCompleteListener<Vehicle> {
		@Override
		public void onTaskComplete(Vehicle vh) {
			//Log.d("onTaskComplete: ",
			//		"DeleteVehicleTaskCompleteListener Started");
			VehicleActivity.this.onTaskCompleteDelete(vh);
		}
	}

	public void onTaskCompleteDelete(Vehicle vh) {
		if (vh != null) {
			Toast.makeText(this,
					"Vehicle: " + vh.getVehicle_id() + " was deleted",
					Toast.LENGTH_LONG).show();
			finish();
		} else {
			Toast.makeText(this, VEHICLE_WASN_T_DELETED_TRY_AGAIN_LATER,
					Toast.LENGTH_LONG).show();
		}

	}

}
