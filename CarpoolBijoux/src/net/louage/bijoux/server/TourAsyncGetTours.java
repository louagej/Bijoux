package net.louage.bijoux.server;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import net.louage.bijoux.constants.Constants;
import net.louage.bijoux.constants.DateTime;
import net.louage.bijoux.constants.Installation;
import net.louage.bijoux.constants.SharedPreferences;
import net.louage.bijoux.model.Country;
import net.louage.bijoux.model.Seat;
import net.louage.bijoux.model.Team;
import net.louage.bijoux.model.Tour;
import net.louage.bijoux.model.User;
import net.louage.bijoux.model.Vehicle;
import net.louage.bijoux.model.VehicleType;
import net.louage.bijoux.sqlite.SchemaHelper;
import android.app.ProgressDialog;
import android.content.Context;
import android.location.Address;
import android.os.AsyncTask;
import android.util.Log;

public class TourAsyncGetTours extends
		AsyncTask<String[], Integer, ArrayList<Tour>> {
	public static final String RES_GET_OK = "Get was successfully";
	public static final String RES_GET_NOK = "Get was unsuccessfully";
	public static final String RES_GET_NULL = "Could not connect to the server";
	private static final String TAG_GET_SUCCESFULL = "result";
	private static final String TAG_TOUR_INFO_RESULT = "tour_list";
	private static final String TAG_USER_TOURS = "tours";
	private static final String TAG_TOUR_SEATS = "tour_seats";
	private static final String TAG_SEATS = "seats";
	private static final String TAG_USER = "user";
	private static final String TAG_VEHICLE = "tour_vehicle";

	JSONParser jParser = new JSONParser();
	private ProgressDialog mProgressDialog;
	public static final int DIALOG_GET_PROGRESS = 0;
	private Context context;
	private String[] parameters;

	private AsTskArrayListCompleteListener<Tour> listener;

	public TourAsyncGetTours(Context context,
			AsTskArrayListCompleteListener<Tour> listener, String[] params) {
		this.context = context;
		this.listener = listener;
		parameters = params;
		mProgressDialog = new ProgressDialog(context);
		mProgressDialog.setMessage("Getting tour list...");
		mProgressDialog.setIndeterminate(false);
		mProgressDialog.setMax(100);
		mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		mProgressDialog.setCancelable(true);
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		mProgressDialog.show();
	}

	@Override
	protected ArrayList<Tour> doInBackground(String[]... params) {
		String tag = "GetTours doInBackground";
		// Building Parameters
		List<NameValuePair> params1 = new ArrayList<NameValuePair>();
		// Set the method name
		params1.add(new BasicNameValuePair("method", "getTours"));
		// Please make sure the spellings of the keys are correct
		params1.add(new BasicNameValuePair("user_id", parameters[0]));

		// Check if installation was already done by getting Universally
		// unique identifier
		String instalId = Installation.id(context);
		Log.d(tag + " Installation.id uuid: ", instalId);
		UUID uid = UUID.fromString(instalId);
		Long lng = uid.getMostSignificantBits();
		Log.d(tag, "getMostSignificantBits(): " + lng);
		instalId = Long.toString(lng);
		Log.d(tag, "Long.toString(lng): " + instalId);
		params1.add(new BasicNameValuePair("uuid", instalId));
		User appUser = SharedPreferences.getUser(context);
		ArrayList<Team> teams = appUser.getMemberOf();
		String tm = "";
		// ArrayList<String> teamNames = new ArrayList<String>();
		for (int i = 0; i < teams.size(); i++) {
			Team team = new Team();
			team = teams.get(i);
			tm = tm + team.getTeam();
			if (i < teams.size() - 1) {
				tm = tm + ",";
			}
			// teamNames.add(team.getTeam());
		}
		// String teamParam=teamNames.toString();
		// String teamParam=tm.toString();
		params1.add(new BasicNameValuePair("teams", tm));
		Log.d("Check params1: ", "teams: " + tm);
		// getting JSON string from URL
		JSONObject json = jParser.makeHttpRequest(Constants.SERVICE_URL, "GET",
				params1);
		// Check your log cat for JSON response
		// Log.d("Check login: ", json.toString());
		if (!(json == null)) {
			try {
				// Check if GetTours was successfully
				int success = json.getInt(TAG_GET_SUCCESFULL);
				if (success == 1) {
					//Log.d("success GetTours: ", json.toString());
					JSONObject json_tour_list = json
							.getJSONObject(TAG_TOUR_INFO_RESULT);
					//Log.d("JSON tour_list: ", json_tour_list.toString());
					JSONArray json_tours = json_tour_list
							.getJSONArray(TAG_USER_TOURS);
					//Log.d("JSON tours: ", json_tours.toString());
					ArrayList<Tour> tours = new ArrayList<Tour>();
					for (int i = 0; i < json_tours.length(); i++) {
						JSONObject json_tour = json_tours.getJSONObject(i);
						Tour tr = new Tour();
						tr.setTour_id(json_tour.getInt(Tour.TAG_ID));
						Date tourDate = DateTime.getDateSQLiteString(json_tour.getString(Tour.TAG_DATE));
						tr.setDate(tourDate);
						Date tourTime = DateTime.getTimeSQLiteString(json_tour.getString(Tour.TAG_TIME));
						tr.setTime(tourTime);
						JSONObject json_user = json_tour.getJSONObject(TAG_USER);
						User tourUser = getUserFromJson(json_user);
						tr.setUser(tourUser);
						JSONObject json_vehicle = json_tour.getJSONObject(TAG_VEHICLE);
						Vehicle vh = getVehicleFromJson(json_vehicle);
						tr.setVehicle(vh);
						tr.setSeat_price(Double.parseDouble(json_tour.getString(Tour.TAG_SEAT_PRICE)));
						JSONObject json_team = json_tour.getJSONObject(Tour.TAG_TOUR_TEAM);
						tr.setTeam(JSONParser.getTeamfromJson(json_team));
						//Log.d(tag, "Team: "+ tr.getTeam().getTeam());
						//Log.d(tag, "Team id: "+tr.getTeam().getTeam_id());
						Address fromAddress = getFromAddress(json_tour);
						tr.setFromAddress(fromAddress);
						Address toAddress = getToAddress(json_tour);
						tr.setToAddress(toAddress);
						JSONObject json_tour_seats = json_tour.getJSONObject(TAG_TOUR_SEATS);
						JSONArray json_seats = json_tour_seats.getJSONArray(TAG_SEATS);
						ArrayList<Seat>seats=new ArrayList<Seat>();
						for (int j = 0; j < json_seats.length(); j++) {
							JSONObject json_seat = json_seats.getJSONObject(j);
							Seat seat = getSeat(json_seat);
							seats.add(seat);
						}
						tr.setSeats(seats);
						tours.add(tr);
					}
					Log.d("Try Catch: ", "OK building Object tours");
					return tours;
				} else {
					Log.d("Try Catch: ", "NOK building Object tours");
					return null;
				}
			} catch (JSONException e) {
				Log.d("JSON Null: ", "No connection to server could be made");
				e.printStackTrace();
				return null;
			}
		} else {
			return null;
		}
	}

	private Seat getSeat(JSONObject json_seat) throws JSONException {
		Seat seat = new Seat();
		seat.setSeat_id(json_seat.getInt(Seat.TAG_ID));
		seat.setCreated_by_user_id(json_seat.getInt(Seat.TAG_CREATED_BY_USER_ID));
		seat.setDevice_id(json_seat.getString(Seat.TAG_DEVICE_ID));
		seat.setTour_id(json_seat.getInt(Seat.TAG_TOUR_ID));
		seat.setUser_id(json_seat.getInt(Seat.TAG_USER_ID));
		seat.setStatus(json_seat.getString(Seat.TAG_STATUS));
		int paid = json_seat.getInt(Seat.TAG_PAID);
		if (paid==1) {
			seat.setPaid(true);
		} else {
			seat.setPaid(false);
		}		
		return seat;
	}

	private Address getFromAddress(JSONObject json_tour) throws JSONException {
		Address fromAddress = new Address(null);
		fromAddress.setAddressLine(1,
				json_tour.getString(Tour.TAG_FROM_ADDRESS));
		fromAddress.setPostalCode(json_tour.getString(Tour.TAG_FROM_POST_CODE));
		fromAddress.setLocality(json_tour.getString(Tour.TAG_FROM_CITY));
		fromAddress.setCountryCode(json_tour.getString(Tour.TAG_FROM_COUNTRY));
		SchemaHelper sh = new SchemaHelper(context);
		Country ct = sh.getCountryFromIso3166(json_tour
				.getString(Tour.TAG_FROM_COUNTRY));
		sh.close();
		fromAddress.setCountryName(ct.getDescription());
		return fromAddress;
	}

	private Address getToAddress(JSONObject json_tour) throws JSONException {
		Address toAddress = new Address(null);
		toAddress.setAddressLine(1, json_tour.getString(Tour.TAG_TO_ADDRESS));
		toAddress.setPostalCode(json_tour.getString(Tour.TAG_TO_POST_CODE));
		toAddress.setLocality(json_tour.getString(Tour.TAG_TO_CITY));
		toAddress.setCountryCode(json_tour.getString(Tour.TAG_TO_COUNTRY));
		SchemaHelper sh = new SchemaHelper(context);
		Country ct = sh.getCountryFromIso3166(json_tour
				.getString(Tour.TAG_FROM_COUNTRY));
		sh.close();
		toAddress.setCountryName(ct.getDescription());
		return toAddress;
	}

	private User getUserFromJson(JSONObject json_user) throws JSONException {
		User tourUser = new User();
		tourUser.setUser_id(Integer.parseInt(json_user.getString(User.TAG_ID)));
		tourUser.setUsername(json_user.getString(User.TAG_USERNAME));
		Date activationDate = DateTime.getDateSQLiteString(json_user
				.getString(User.TAG_ACTIVATION));
		tourUser.setActivation(activationDate);
		tourUser.setLastname(json_user.getString(User.TAG_LASTNAME));
		tourUser.setFirstname(json_user.getString(User.TAG_FIRSTNAME));
		tourUser.setEmail(json_user.getString(User.TAG_EMAIL));
		tourUser.setPhone(json_user.getString(User.TAG_PHONE));
		tourUser.setInfo(json_user.getString(User.TAG_INFO));
		tourUser.setDriverlicense(json_user.getString(User.TAG_DRIVERLICENSE));
		return tourUser;
	}

	private Vehicle getVehicleFromJson(JSONObject json_vehicle)
			throws JSONException {
		Vehicle vh = new Vehicle();
		vh.setVehicle_id(Integer.parseInt(json_vehicle
				.getString(Vehicle.TAG_ID)));
		vh.setLicenseplate(json_vehicle.getString(Vehicle.TAG_LICENSEPLATE));
		SchemaHelper sh = new SchemaHelper(context);
		sh.getCountryDescription(json_vehicle.getString(Vehicle.TAG_COUNTRY));
		Country ct = new Country(json_vehicle.getString(Vehicle.TAG_COUNTRY),
				json_vehicle.getString(Vehicle.TAG_COUNTRY));
		vh.setCountry(ct);
		vh.setNumberOfPassengers(json_vehicle
				.getInt(Vehicle.TAG_NUMBER_OF_PASS));
		vh.setBrand(json_vehicle.getString(Vehicle.TAG_BRAND));
		VehicleType vt = new VehicleType(
				json_vehicle.getString(Vehicle.TAG_VEHICLE_TYPE));
		vh.setType(vt);
		vh.setUser_id(json_vehicle.getInt(Vehicle.TAG_USER_ID));
		Date vehicleUpdatedAt = DateTime.getDateTimeSQLiteString(json_vehicle
				.getString(Vehicle.TAG_UPDATED_AT));
		vh.setUpdate_at(vehicleUpdatedAt);
		return vh;
	}

	protected void onProgressUpdate(String... progress) {
		Log.d("ANDRO_ASYNC", progress[0]);
		mProgressDialog.setProgress(Integer.parseInt(progress[0]));
	}

	@Override
	protected void onPostExecute(ArrayList<Tour> tours) {
		super.onPostExecute(tours);
		mProgressDialog.dismiss();
		Log.d("onPostExecute: ", "OK");
		listener.onTaskComplete(tours);
	}

}
