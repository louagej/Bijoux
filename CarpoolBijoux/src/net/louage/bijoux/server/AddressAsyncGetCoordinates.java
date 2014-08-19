package net.louage.bijoux.server;

import java.util.ArrayList;
import java.util.List;

import net.louage.bijoux.constants.Constants;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.location.Address;
import android.os.AsyncTask;
import android.util.Log;

public class AddressAsyncGetCoordinates extends AsyncTask<String[], Integer, ArrayList<Address>> {
	private static final String TAG_ADDRESS_COMPONENTS = "address_components";
	public static final String RES_GET_OK = "Get was successfully";
	public static final String RES_GET_NOK = "Get was unsuccessfully";
	public static final String RES_GET_NULL = "Could not connect to the server";
	private static final String TAG_GET_RESULTS = "results";
	private static final String TAG_LOCATION = "location";
	private static final String TAG_TYPES = "types";
	private static final String TAG_LONG_NAME = "long_name";
	private static final String TAG_SHORT_NAME = "short_name";
	JSONParser jParser = new JSONParser();
	private ProgressDialog mProgressDialog;
	public static final int DIALOG_GET_PROGRESS = 0;
	private static final String TAG_GEOMETRY = "geometry";
	private String[] parameters;
	
	private AsTskArrayListCompleteListener<Address> listener;

	public AddressAsyncGetCoordinates(Context context, AsTskArrayListCompleteListener<Address> listener,String[] params) {
		this.listener = listener;
		parameters = params;
		mProgressDialog = new ProgressDialog(context);
		mProgressDialog.setMessage("Getting address list...");
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
	protected ArrayList<Address> doInBackground(String[]... params) {
		String tag = "AddressAsyncGetCoordinates doInBackground";
		// Building Parameters
		List<NameValuePair> params1 = new ArrayList<NameValuePair>();
		params1.add(new BasicNameValuePair("address", parameters[0]));
		params1.add(new BasicNameValuePair("sensor", "false"));
		Log.d("Check params1: ", params1.toString());
		ArrayList<Address> addresses = new ArrayList<Address>();
		// getting JSON string from URL
		JSONObject json = jParser.makeHttpRequest(Constants.GEOCODING_URL, "GET", params1);
		// Check your log cat for JSON response		
		// Check if getAddresses was successfully
		if (!(json == null)) {
			try {				
				JSONArray json_address_list = json.getJSONArray(TAG_GET_RESULTS);
				// Check JSONArray results size > 0
				if (json_address_list.length() > 0) {
					//for each json_address_list... get JSONArray address_components
					for (int i = 0; i < json_address_list.length(); i++) {
						Address adr = new Address(null);
						JSONObject json_address = json_address_list.getJSONObject(i);
						JSONArray json_component_list = json_address.getJSONArray(TAG_ADDRESS_COMPONENTS);
						String componentNumber=null;
						String componentAddress=null;
						String componentRoute=null;
						String addressLineZero = null;
						for (int j = 0; j < json_component_list.length(); j++) {														
							JSONObject jsonComponent = json_component_list.getJSONObject(j);
							JSONArray jsonTypes = jsonComponent.getJSONArray(TAG_TYPES);
							String[] types = new String[jsonTypes.length()];
							for (int k = 0; k < types.length; k++) {
								types[k]=jsonTypes.getString(k);								
							}
							String type = types[0];
							switch (type) {
							case "street_address":
								//indicates a precise street address
								if (componentAddress==null) {
									componentAddress = jsonComponent.getString(TAG_LONG_NAME);
								} else {
									componentAddress = componentAddress+", "+jsonComponent.getString(TAG_LONG_NAME);
								}																
								break;
								
							case "street_number":
								//indicates the precise street number.
								if (componentNumber==null) {
									componentNumber = jsonComponent.getString(TAG_LONG_NAME);
								} else {
									componentNumber = componentNumber+"\\"+jsonComponent.getString(TAG_LONG_NAME);
								}								
								break;
								
							case "route":
								//indicates a named route (such as "US 101")
								if (componentRoute==null) {
									componentRoute = jsonComponent.getString(TAG_LONG_NAME);
								} else {
									componentRoute = componentRoute+", "+jsonComponent.getString(TAG_LONG_NAME);
								}																
								break;
								
							case "locality":
								//indicates an incorporated city or town political entity.
								adr.setLocality(jsonComponent.getString(TAG_LONG_NAME));
								break;
							case "sublocality":
								//indicates an incorporated city or town political entity.
								adr.setSubLocality(jsonComponent.getString(TAG_LONG_NAME));
								break;
							case "political":
								//indicates a political entity. Usually, this type indicates a polygon of some civil administration.
								break;
							case "administrative_area_level_1":
								// indicates a first-order civil entity below the country level.
								//Within the United States, these administrative levels are states.
								//Not all nations exhibit these administrative levels.
								adr.setAdminArea(jsonComponent.getString(TAG_LONG_NAME));
								break;
							case "administrative_area_level_2":
								//Indicates a second-order civil entity below the country level.
								//Within the United States, these administrative levels are counties.
								//Not all nations exhibit these administrative levels.								
								adr.setSubAdminArea(jsonComponent.getString(TAG_LONG_NAME));
								break;
							case "administrative_area_level_3":
								//Indicates a third-order civil entity below the country level.
								//This type indicates a minor civil division.
								//Not all nations exhibit these administrative levels.								
								break;
							case "colloquial_area":
								//Indicates a commonly-used alternative name for the entity.
								adr.setThoroughfare(jsonComponent.getString(TAG_LONG_NAME));
								break;
							case "country":
								adr.setCountryCode(jsonComponent.getString(TAG_SHORT_NAME));
								adr.setCountryName(jsonComponent.getString(TAG_LONG_NAME));
								break;
							case "postal_code":
								adr.setPostalCode(jsonComponent.getString(TAG_LONG_NAME));
								break;								
							default:
								Log.d(tag, "unknown address_component type");
								break;
							}
							if (componentRoute==null) {
								if (componentNumber==null) {
									if (componentAddress==null) {
										//no address was found
									} else {
										addressLineZero=componentAddress;
									}									
									adr.setAddressLine(0, addressLineZero);
								} else {
									if (componentAddress==null) {
										//only a number was found
										addressLineZero=componentNumber;
									} else {
										//only componentAddress and componentNumber contain information
										addressLineZero=componentAddress+" "+componentNumber;										
									}									
									adr.setAddressLine(0, addressLineZero);
								}
							} else {
								if (componentNumber==null) {
									if (componentAddress==null) {
										//only componentRoute was found
										addressLineZero=componentRoute;
									} else {
										//componentRoute and componentAddress are found
										addressLineZero=componentAddress+", "+componentRoute;
									}									
									adr.setAddressLine(0, addressLineZero);
								} else {
									if (componentAddress==null) {
										//componentNumber and componentRoute are found
										addressLineZero=componentRoute+" "+componentNumber;
									} else {
										//only componentAddress, componentNumber and componentRoute are found
										addressLineZero=componentAddress+" "+componentNumber +", " + componentRoute;										
									}									
									adr.setAddressLine(0, addressLineZero);
								}
							}

						}
						//Analyze geometry
						try {
							JSONObject jsonGeometry = json_address.getJSONObject(TAG_GEOMETRY);
							JSONObject jsonLocation = jsonGeometry.getJSONObject(TAG_LOCATION);	
							double lng = jsonLocation.getDouble("lng");
							double lat = jsonLocation.getDouble("lat");
							adr.setLongitude(lng);
							adr.setLatitude(lat);
							Log.d("latitude", "" + lat);
							Log.d("longitude", "" + lng);
						} catch (JSONException e) {
							e.printStackTrace();
						}
						// formatted_address is now completed and can be added to the ArrayList Addresses
						addresses.add(adr);
					}
					Log.d("Try Catch: ", "OK building ArrayList<Address> addresses");
				} else {
					Log.d("Try Catch: ", "Address formation wasn't successfully");
				}
				return addresses;
			} catch (JSONException e) {
				Log.d("Try Catch: ", "Address formation wasn't successfully");
				e.printStackTrace();
				return addresses;
			}
		} else {
			Log.d("JSON Null: ", "No connection to server could be made");
			return addresses;
		}

	}

	protected void onProgressUpdate(String... progress) {
		Log.d("ANDRO_ASYNC", progress[0]);
		mProgressDialog.setProgress(Integer.parseInt(progress[0]));
	}

	protected void onPostExecute(ArrayList<Address> addresses) {
		super.onPostExecute(addresses);
		mProgressDialog.dismiss();
		Log.d("onPostExecute: ", "OK");
		listener.onTaskComplete(addresses);
	}

}
