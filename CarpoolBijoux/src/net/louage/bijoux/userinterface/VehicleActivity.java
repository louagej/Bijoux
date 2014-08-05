package net.louage.bijoux.userinterface;

import net.louage.bijoux.R;
import net.louage.bijoux.constants.NoticeDialogFragment;
import net.louage.bijoux.constants.NoticeDialogFragment.NoticeDialogListener;
import net.louage.bijoux.model.Vehicle;
import net.louage.bijoux.server.AsTskObjectCompleteListener;
import net.louage.bijoux.server.DeleteVehicle;
import net.louage.bijoux.sqlite.CountryTable;
import net.louage.bijoux.sqlite.SchemaHelper;
import android.app.Activity;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class VehicleActivity extends Activity implements View.OnClickListener, NoticeDialogListener{
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
	ArrayAdapter<String> adapter2;
	Vehicle vh;
	//private static final String[] COUNTRIES = new String[] {
	//	"Belgium", "France", "Italy", "Germany", "Spain"
	//};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_vehicle);
		sh = new SchemaHelper(this);
		
		//Get Vehicle from intent
		vh = (Vehicle) getIntent().getSerializableExtra("Vehicle");
		txtActVehAutonumberId = (TextView) findViewById(R.id.txtActVehAutonumberId);
		txtActVehAutonumberId.setText(getResources().getString(R.string.act_vehicle_id) + ": " + vh.getVehicle_id());
		eTxtActVehLicenseplate = (EditText)findViewById(R.id.eTxtActVehLicenseplate);
		eTxtActVehLicenseplate.setText(vh.getLicenseplate());
		autComplActVehCountry = (AutoCompleteTextView)findViewById(R.id.autComplActVehCountry);
		//Build AutoCompleteTextView for selecting countries retrieved by string array
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
		
		//Build AutoCompleteTextView for selecting vehicle brands retrieved by string array
		autComplActVehBrand = (AutoCompleteTextView)findViewById(R.id.autComplActVehBrand);
		//Link the String array with a new ArrayAdapter
		String[] brands = getResources().getStringArray(R.array.brand_array);
		adapter2 = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, brands);
		autComplActVehBrand.setAdapter(adapter2);
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
			//Blank out EditText fields and change caption of update button Create Vehicle
			txtActVehAutonumberId.setText(getResources().getString(R.string.act_vehicle_id) + ": *");
			eTxtActVehLicenseplate.setText("");
			autComplActVehCountry.setText("");
			eTxtActVehNoOfPassengers.setText("");
			autComplActVehBrand.setText("");
			eTxtActVehVehicleType.setText("");
			btnActVehUpdate.setText("Create");
						
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

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnActVehUpdate:
			//TODO Start Async task to update or create vehicle depending cation of btnActVehUpdate 
			break;
		default:
			break;
		}		
	}

	// The dialog fragment receives a reference to this Activity through the
    // Fragment.onAttach() callback, which it uses to call the following methods
    // defined by the NoticeDialogFragment.NoticeDialogListener interface
	@Override
	public void onDialogPositiveClick(DialogFragment dialog) {
		String[] params = { Integer.toString(vh.getUser_id()), Integer.toString(vh.getVehicle_id()) };
		new DeleteVehicle(this, new DeleteVehicleTaskCompleteListener(), params).execute();
	}

	@Override
	public void onDialogNegativeClick(DialogFragment dialog) {
		Toast.makeText(this, R.string.act_vehicle_actb_del_vehicle_canceled, Toast.LENGTH_SHORT).show();		
	}
	
	
	class DeleteVehicleTaskCompleteListener implements	AsTskObjectCompleteListener<Vehicle> {
		@Override
		public void onTaskComplete(Vehicle vh) {
			Log.d("onTaskComplete: ", "DeleteVehicleTaskCompleteListener Started");
			VehicleActivity.this.onTaskComplete(vh);			
			}
		}

	public void onTaskComplete(Vehicle vh) {
		if (vh!=null) {
			Toast.makeText(this, "Vehicle with _id: "+ vh.getVehicle_id()+" was deleted", Toast.LENGTH_LONG).show();
			finish();
		} else {
			Toast.makeText(this, "Vehicle wasn't deleted, try again later", Toast.LENGTH_LONG).show();
		}
		
	}

}
