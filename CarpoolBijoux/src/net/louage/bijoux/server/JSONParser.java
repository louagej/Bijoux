package net.louage.bijoux.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.List;

import net.louage.bijoux.constants.DateTime;
import net.louage.bijoux.model.Country;
import net.louage.bijoux.model.Seat;
import net.louage.bijoux.model.Team;
import net.louage.bijoux.model.Tour;
import net.louage.bijoux.model.Tracking;
import net.louage.bijoux.model.User;
import net.louage.bijoux.model.Vehicle;
import net.louage.bijoux.model.VehicleType;
import net.louage.bijoux.sqlite.SchemaHelper;
import net.louage.bijoux.sqlite.TrackingTable;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.location.Address;
import android.util.Log;

public class JSONParser {

	static InputStream is = null;
	static JSONObject jObj = null;
	static String json = "";

	// constructor
	public JSONParser() {

	}

	// function get json from url
	// by making HTTP POST or GET mehtod
	public JSONObject makeHttpRequest(String url, String method,
			List<NameValuePair> params) {

		// Making HTTP request
		try {

			// check for request method
			if (method == "POST") {
				// request method is POST
				// defaultHttpClient
				DefaultHttpClient httpClient = new DefaultHttpClient();
				HttpPost httpPost = new HttpPost(url);

				// webservice aanroepen om nieuw item weg te schrijven
				httpPost.setEntity(new UrlEncodedFormEntity(params));

				HttpResponse httpResponse = httpClient.execute(httpPost);
				// we krijgen iets van de webservice terug 1 en wat tekst

				HttpEntity httpEntity = httpResponse.getEntity();
				is = httpEntity.getContent();

			} else if (method == "GET") {
				// request method is GET
				//Log.d("JSONParser method GET params: ", params.toString());
				DefaultHttpClient httpClient = new DefaultHttpClient();
				String paramString = URLEncodedUtils.format(params, "utf-8");
				url += "?" + paramString;
				HttpGet httpGet = new HttpGet(url);
				Log.d("JSONParser method GET: ", httpGet.getURI().toString());

				HttpResponse httpResponse = httpClient.execute(httpGet);
				HttpEntity httpEntity = httpResponse.getEntity();
				is = httpEntity.getContent();
			}

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					is, "iso-8859-1"), 8);
			StringBuilder sb = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
			is.close();
			json = sb.toString();
		} catch (Exception e) {
			Log.e("Buffer Error", "Error converting result " + e.toString());
		}

		// try parse the string to a JSON object
		try {
			jObj = new JSONObject(json);
		} catch (JSONException e) {
			Log.e("JSON Parser", "Error parsing data " + e.toString());
		}

		// return JSON String
		return jObj;

	}

	public static User getUserfromJson(JSONObject json_user) throws JSONException {
		User user = new User();
		user.setUser_id(Integer.parseInt(json_user.getString(User.TAG_ID)));
		Date activationDate = DateTime.getDateFormString(json_user.getString(User.TAG_ACTIVATION));
		user.setActivation(activationDate);
		user.setLastname(json_user.getString(User.TAG_LASTNAME));
		user.setFirstname(json_user.getString(User.TAG_FIRSTNAME));
		user.setEmail(json_user.getString(User.TAG_EMAIL));
		user.setPhone(json_user.getString(User.TAG_PHONE));
		user.setInfo(json_user.getString(User.TAG_INFO));
		Date updateAtDate=DateTime.getDateTimeString(json_user.getString(User.TAG_UPDATED_AT));
		user.setUpdate_at(updateAtDate);
		user.setDriverlicense(json_user.getString(User.TAG_DRIVERLICENSE));
		int approved = json_user.getInt(User.TAG_APPROVED);
		if (approved==1) {
			user.setApproved(true);
		} else {
			user.setApproved(false);
		}
		return user;

	}

	public static Vehicle getVehiclefromJson(JSONObject json_vehicle, Context context) throws JSONException {
		Vehicle vh = new Vehicle();
		vh.setVehicle_id(Integer.parseInt(json_vehicle.getString(Vehicle.TAG_ID)));
		vh.setLicenseplate(json_vehicle.getString(Vehicle.TAG_LICENSEPLATE));
		SchemaHelper sh = new SchemaHelper(context);
		Country ct = sh.getCountryFromIso3166(json_vehicle.getString(Vehicle.TAG_COUNTRY));
		sh.close();
		//Country ct = new Country(json_vehicle.getString(Vehicle.TAG_COUNTRY),json_vehicle.getString(Vehicle.TAG_COUNTRY));
		vh.setCountry(ct);
		vh.setNumberOfPassengers(json_vehicle.getInt(Vehicle.TAG_NUMBER_OF_PASS));
		vh.setBrand(json_vehicle.getString(Vehicle.TAG_BRAND));
		VehicleType vt = new VehicleType(json_vehicle.getString(Vehicle.TAG_VEHICLE_TYPE));
		vh.setType(vt);
		vh.setUser_id(json_vehicle.getInt(Vehicle.TAG_USER_ID));
		Date vehicleUpdatedAt = DateTime.getDateTimeString(json_vehicle.getString(Vehicle.TAG_UPDATED_AT));
		vh.setUpdate_at(vehicleUpdatedAt);
		return vh;
	}
	
	public static Team getTeamfromJson(JSONObject json_team) throws JSONException {
		Team tm = new Team();
		tm.setTeam_id(json_team.getInt(Team.TAG_ID));
		tm.setTeamname(json_team.getString(Team.TAG_USER_TEAMNAME));
		return tm;
	}
	
	public static Address getFromAddress(JSONObject json_tour, Context context) throws JSONException {
		Address fromAddress = new Address(null);
		fromAddress.setAddressLine(0,
				json_tour.getString(Tour.TAG_FROM_ADDRESS));
		fromAddress.setPostalCode(json_tour.getString(Tour.TAG_FROM_POST_CODE));
		fromAddress.setLocality(json_tour.getString(Tour.TAG_FROM_CITY));
		fromAddress.setCountryCode(json_tour.getString(Tour.TAG_FROM_COUNTRY));
		SchemaHelper sh = new SchemaHelper(context);
		Country ct = sh.getCountryFromIso3166(json_tour.getString(Tour.TAG_FROM_COUNTRY));
		sh.close();
		fromAddress.setCountryName(ct.getDescription());
		return fromAddress;
	}
	
	public static Address getToAddress(JSONObject json_tour, Context context) throws JSONException {
		Address toAddress = new Address(null);
		toAddress.setAddressLine(0, json_tour.getString(Tour.TAG_TO_ADDRESS));
		toAddress.setPostalCode(json_tour.getString(Tour.TAG_TO_POST_CODE));
		toAddress.setLocality(json_tour.getString(Tour.TAG_TO_CITY));
		toAddress.setCountryCode(json_tour.getString(Tour.TAG_TO_COUNTRY));
		SchemaHelper sh = new SchemaHelper(context);
		Country ct = sh.getCountryFromIso3166(json_tour.getString(Tour.TAG_FROM_COUNTRY));
		sh.close();
		toAddress.setCountryName(ct.getDescription());
		return toAddress;
	}
	
	public static Seat getSeat(JSONObject json_seat) throws JSONException {
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
		seat.setPickupAddress(json_seat.getString(Seat.TAG_PICKUP));
		seat.setDropoffAddress(json_seat.getString(Seat.TAG_DROPOFF));
		return seat;
	}

	public static Tracking getTrackingfromJson(JSONObject json_tracking) throws JSONException{
		Tracking track = new Tracking();
		track.setCloud_id(json_tracking.getInt(TrackingTable.ID));
		Date trackDateTime = DateTime.getDateFormString(json_tracking.getString(TrackingTable.TRACK_DATE_TIME));
		track.setTrack_date_time(trackDateTime);
		track.setTour_id(json_tracking.getInt(TrackingTable.TOUR_ID));
		track.setLatitude(json_tracking.getDouble(TrackingTable.LATITUDE));
		track.setLongitude(json_tracking.getDouble(TrackingTable.LONGITUDE));
		track.setAccuracy(json_tracking.getDouble(TrackingTable.ACCURACY));
		track.setAltitude(json_tracking.getDouble(TrackingTable.ALTITUDE));
		track.setSpeed(json_tracking.getDouble(TrackingTable.SPEED));
		return track;
	}
}