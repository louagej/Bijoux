package net.louage.bijoux.userinterface;

import net.louage.bijoux.R;
import net.louage.bijoux.model.Country;
import net.louage.bijoux.model.Vehicle;
import net.louage.bijoux.sqlite.CountryTable;
import net.louage.bijoux.sqlite.SchemaHelper;
import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class VehicleActivity extends Activity {
	private TextView txtActVehAutonumberId;
	private EditText eTxtActVehLicenseplate;
	private AutoCompleteTextView autComplActVehCountry;
	private EditText eTxtActVehNoOfPassengers;
	private AutoCompleteTextView autComplActVehBrand;
	private EditText eTxtActVehVehicleType;
	//private String[] countryArray = getResources().getStringArray(R.array.country_array);;
	private Button btnActVehUpdate;
	private SchemaHelper sh;
	ArrayAdapter<String> adapter;
	//private static final String[] COUNTRIES = new String[] {
	//	"Belgium", "France", "Italy", "Germany", "Spain"
	//};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_vehicle);
		sh = new SchemaHelper(this);
		
		//Get Vehicle from intent
		Vehicle vh = (Vehicle) getIntent().getSerializableExtra("Vehicle");
		txtActVehAutonumberId = (TextView) findViewById(R.id.txtActVehAutonumberId);
		txtActVehAutonumberId.setText(getResources().getString(R.string.act_vehicle_id) + ": " + vh.getVehicle_id());
		eTxtActVehLicenseplate = (EditText)findViewById(R.id.eTxtActVehLicenseplate);
		eTxtActVehLicenseplate.setText(vh.getLicenseplate());
		autComplActVehCountry = (AutoCompleteTextView)findViewById(R.id.autComplActVehCountry);
		//Build AutoCompleteTextView for selecting countries retrieved by the SQLite database table country
		autComplActVehCountry.setText(getCountryDescription(vh.getCountry().getIso3166()));
		//Fill String array with values from SQLite database
		//buildCountriesArray();
		//Link the String array with a new ArrayAdapter
		String[] countries = getResources().getStringArray(R.array.country_array);
		adapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, countries);
		//adapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, COUNTRIES);
		//Link the adapter to the AutoCompleteTextView in the layout
		autComplActVehCountry.setAdapter(adapter);
		
		eTxtActVehNoOfPassengers = (EditText)findViewById(R.id.eTxtActVehNoOfPassengers);
		eTxtActVehNoOfPassengers.setText(Integer.toString(vh.getNumberOfPassengers()));
		autComplActVehBrand = (AutoCompleteTextView)findViewById(R.id.autComplActVehBrand);
		autComplActVehBrand.setText(vh.getBrand());
		eTxtActVehVehicleType = (EditText)findViewById(R.id.eTxtActVehVehicleType);
		eTxtActVehVehicleType.setText(vh.getType().getType());
		btnActVehUpdate = (Button)findViewById(R.id.btnActVehUpdate);
	}

	private String getCountryDescription(String iso3166) {
		Log.d("iso3166", iso3166);
		Cursor c = sh.getCountryDescription(iso3166);
		String countryDescription="";
		while (c.moveToNext()) {
			countryDescription = c.getString(c.getColumnIndex(CountryTable.DESCRIPTION));			
		}
		return countryDescription;
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
			//startActivity(new Intent(this, NewUserActivity.class));
			//TODO Blank out EditText fields and change caption of update button Create Vehicle
			break;
		case R.id.delete_vehicle:
			//TODO Ask user to delete vehicle and if confirmed, start async task delete vehicle
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

}
