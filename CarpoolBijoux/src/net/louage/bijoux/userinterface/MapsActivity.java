package net.louage.bijoux.userinterface;

import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.Gson;

import net.louage.bijoux.R;
import net.louage.bijoux.model.Tour;
import net.louage.bijoux.model.Tracking;
import net.louage.bijoux.R.id;
import net.louage.bijoux.R.layout;
import net.louage.bijoux.R.menu;
import net.louage.bijoux.locationtracking.GMapV2GetRouteDirection;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.location.Address;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;

public class MapsActivity extends Activity {
	//TODO fields private!!
	// Google Map
	private GoogleMap googleMap;
	// private int user_id;
	private int tour_id;
	private int type; // Triggers if the map should show a position (1) or a route (2)
	private MarkerOptions markerOptions;
	private Tracking tracking;
	private String mode = "driving";
	public ProgressDialog pDialog;
	private ArrayList<Tracking> trackings = new ArrayList<Tracking>();
	private Document document;
	private LatLng startPosition;
	private LatLng middlePosition;
	private LatLng endPosition;
	private Address address;
	private GMapV2GetRouteDirection v2GetRouteDirection;
	@SuppressLint("SimpleDateFormat")
	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_maps);
		Intent intent = getIntent();
		Bundle extras = intent.getExtras();
		if (extras != null) {
			// user_id = extras.getInt("user_id");
			//tour_id = extras.getInt("tour_id");
			type = extras.getInt("type");
			String jsonAddress = getIntent().getStringExtra("jsonAddress");
			Gson gson = new Gson();
			address = gson.fromJson(jsonAddress, Address.class);
			
		} else {
			Toast.makeText(this, "Couldn't find tour details. Please try another time!", Toast.LENGTH_LONG).show();
			finish();
		}
		try {
			// Loading map
			initilizeMap();

		} catch (Exception e) {
			e.printStackTrace();
		}

		switch (type) {
		case 1:
			tracking = (Tracking) intent.getSerializableExtra("tracking");
			goTo();
			break;
		case 2:
			/*trackings = (ArrayList<Tracking>) intent.getSerializableExtra("trackings");
			for (int i = 0; i < trackings.size(); i++) {
				// Create markers for every trace of the tracking
				Tracking tracking = trackings.get(i);
				DateFormat dateTimeFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
				String trackingTitle = "Tracking: " + tracking.getTracking_id() + " - "+ dateTimeFormat.format(tracking.getTrack_date_time());
				String trackingSnippet = "Altitude: " + tracking.getAltitude()+ " - Speed: " + tracking.getSpeed() * 3.6 + "km/h";
				LatLng myTracking = new LatLng(tracking.getLatitude(), tracking.getLongitude());
				// Put markers on the map
				googleMap.addMarker(new MarkerOptions().position(myTracking).title(trackingTitle).snippet(trackingSnippet));
			}

			// Calculate 3 LatLng positions to compose route on map
			int trcQty = trackings.size();
			double trcQtyHalf = Math.floor(trackings.size() / 2);
			Tracking trcStart = trackings.get(0);
			startPosition = new LatLng(trcStart.getLatitude(), trcStart.getLongitude());

			Tracking trcMiddle = trackings.get((int) trcQtyHalf);
			middlePosition = new LatLng(trcMiddle.getLatitude(), trcMiddle.getLongitude());

			Tracking trcEnd = trackings.get(trcQty - 1);
			endPosition = new LatLng(trcEnd.getLatitude(), trcEnd.getLongitude());

			new addDirectionLines().execute();

			CameraPosition cameraPostition = new CameraPosition.Builder().target(middlePosition).zoom(13).bearing(90).tilt(30).build();
			googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPostition));*/
			Toast.makeText(this, "Tracking not ready yet", Toast.LENGTH_LONG).show();
			finish();
			break;
		default:
			break;
		}
	}

	/**
	 * function to load map. If map is not created it will create it for you
	 * */
	private void initilizeMap() {
		if (googleMap == null) {
			googleMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
			googleMap.getUiSettings().setZoomControlsEnabled(true);
			googleMap.getUiSettings().setCompassEnabled(true);
			googleMap.getUiSettings().setMyLocationButtonEnabled(true);
			googleMap.getUiSettings().setAllGesturesEnabled(true);
			googleMap.setTrafficEnabled(true);
			// check if map is created successfully or not
			if (googleMap == null) {
				Toast.makeText(getApplicationContext(),
						"Sorry! unable to create maps", Toast.LENGTH_SHORT).show();
			}
		}
	}

	@SuppressLint("SimpleDateFormat")
	public void goTo() {
		String trackingTitle = "Location";
		//Double lat = address.getLatitude();
		//Double lng = address.getLongitude();
		
		String trackingSnippet = address.getAddressLine(0) + "\n" +address.getPostalCode()+" "+ address.getLocality();
		/*if((lat !=null) && (lng!=null)){*/
			LatLng myTracking = new LatLng(address.getLatitude(), address.getLongitude());			
			CameraPosition cameraPostition = new CameraPosition.Builder().target(myTracking).zoom(17).bearing(90).tilt(30).build();
			googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPostition));
			googleMap.addMarker(new MarkerOptions().position(myTracking).title(trackingTitle).snippet(trackingSnippet));
		/*}else {
			Toast.makeText(getApplicationContext(), "Sorry! unable to create maps for " + trackingSnippet, Toast.LENGTH_SHORT).show();
			finish();
		}*/
		/*LatLng myTracking = new LatLng(address.getLatitude(), address.getLongitude());
		
		CameraPosition cameraPostition = new CameraPosition.Builder().target(myTracking).zoom(17).bearing(90).tilt(30).build();
		googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPostition));
		googleMap.addMarker(new MarkerOptions().position(myTracking).title(trackingTitle).snippet(trackingSnippet));*/
	}

	private class addDirectionLines extends AsyncTask<String, Void, String> {
        //private ProgressDialog Dialog;
        String response = "";
        @Override
        protected void onPreExecute() {
              //Dialog = new ProgressDialog(MapsActivity.this);
              //Dialog.setMessage("Loading route...");
              //Dialog.show();
        }

        @Override
        protected String doInBackground(String... urls) {
              //Get All Route values
                    document = getDocument(startPosition, endPosition, GMapV2GetRouteDirection.MODE_DRIVING);
                    response = "Success";
              return response;

        }

        @Override
        protected void onPostExecute(String result) {
        	//googleMap.clear();
              if(response.equalsIgnoreCase("Success")){
              //ArrayList<LatLng> directionPoint = v2GetRouteDirection.getDirection(document);
              ArrayList<LatLng> directionPoint = getDirection(document);
              PolylineOptions rectLine = new PolylineOptions().width(10).color(Color.RED);

              for (int i = 0; i < directionPoint.size(); i++) {
                    rectLine.add(directionPoint.get(i));
              }
              // Adding route on the map
              googleMap.addPolyline(rectLine);
              //googleMap.position(endPosition);
              //googleMap.draggable(true);
              //googleMap.addMarker(markerOptions);

              }
             
              //Dialog.dismiss();
        }
  }
  @Override
  protected void onStop() {
        super.onStop();
        finish();
  }

  public Document getDocument(LatLng start, LatLng end, String mode) {
      String url = "http://maps.googleapis.com/maps/api/directions/xml?"
              + "origin=" + start.latitude + "," + start.longitude 
              + "&destination=" + end.latitude + "," + end.longitude
              + "&sensor=false&units=metric&mode="+mode;

      try {
          HttpClient httpClient = new DefaultHttpClient();
          HttpContext localContext = new BasicHttpContext();
          HttpPost httpPost = new HttpPost(url);
          HttpResponse response = httpClient.execute(httpPost, localContext);
          InputStream in = response.getEntity().getContent();
          DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
          Document doc = builder.parse(in);
          return doc;
      } catch (Exception e) {
          e.printStackTrace();
      }
      return null;
  }
  
  public ArrayList<LatLng> getDirection (Document doc) {
      NodeList nl1, nl2, nl3;
      ArrayList<LatLng> listGeopoints = new ArrayList<LatLng>();
      nl1 = doc.getElementsByTagName("step");
      if (nl1.getLength() > 0) {
          for (int i = 0; i < nl1.getLength(); i++) {
              Node node1 = nl1.item(i);
              nl2 = node1.getChildNodes();

              Node locationNode = nl2.item(getNodeIndex(nl2, "start_location"));
              nl3 = locationNode.getChildNodes();
              Node latNode = nl3.item(getNodeIndex(nl3, "lat"));
              double lat = Double.parseDouble(latNode.getTextContent());
              Node lngNode = nl3.item(getNodeIndex(nl3, "lng"));
              double lng = Double.parseDouble(lngNode.getTextContent());
              listGeopoints.add(new LatLng(lat, lng));

              locationNode = nl2.item(getNodeIndex(nl2, "polyline"));
              nl3 = locationNode.getChildNodes();
              latNode = nl3.item(getNodeIndex(nl3, "points"));
              ArrayList<LatLng> arr = decodePoly(latNode.getTextContent());
              for(int j = 0 ; j < arr.size() ; j++) {
                  listGeopoints.add(new LatLng(arr.get(j).latitude, arr.get(j).longitude));
              }

              locationNode = nl2.item(getNodeIndex(nl2, "end_location"));
              nl3 = locationNode.getChildNodes();
              latNode = nl3.item(getNodeIndex(nl3, "lat"));
              lat = Double.parseDouble(latNode.getTextContent());
              lngNode = nl3.item(getNodeIndex(nl3, "lng"));
              lng = Double.parseDouble(lngNode.getTextContent());
              listGeopoints.add(new LatLng(lat, lng));
          }
      }

      return listGeopoints;
  }

  private int getNodeIndex(NodeList nl, String nodename) {
      for(int i = 0 ; i < nl.getLength() ; i++) {
          if(nl.item(i).getNodeName().equals(nodename))
              return i;
      }
      return -1;
  }

  private ArrayList<LatLng> decodePoly(String encoded) {
      ArrayList<LatLng> poly = new ArrayList<LatLng>();
      int index = 0, len = encoded.length();
      int lat = 0, lng = 0;
      while (index < len) {
          int b, shift = 0, result = 0;
          do {
              b = encoded.charAt(index++) - 63;
              result |= (b & 0x1f) << shift;
              shift += 5;
          } while (b >= 0x20);
          int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
          lat += dlat;
          shift = 0;
          result = 0;
          do {
              b = encoded.charAt(index++) - 63;
              result |= (b & 0x1f) << shift;
              shift += 5;
          } while (b >= 0x20);
          int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
          lng += dlng;

          LatLng position = new LatLng((double) lat / 1E5, (double) lng / 1E5);
          poly.add(position);
      }
      return poly;
  }
  
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.maps, menu);
		return true;
	}

	@Override
	protected void onResume() {
		initilizeMap();
		super.onResume();
	}

	public void myClickHandler(View target) {
		switch (target.getId()) {
		case R.id.btnMaps:
			googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
			break;
		case R.id.btnSat:
			googleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
			googleMap.setTrafficEnabled(true);
			break;
		case R.id.btnHybrid:
			googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
			googleMap.setTrafficEnabled(true);
			break;
		case R.id.btnTerrain:
			googleMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
			break;
		default:
			break;
		}
	}

}
