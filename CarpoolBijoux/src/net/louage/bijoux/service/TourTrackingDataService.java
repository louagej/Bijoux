package net.louage.bijoux.service;

import net.louage.bijoux.model.Tour;
import net.louage.bijoux.model.Tracking;
import net.louage.bijoux.sqlite.SchemaHelper;

import com.google.gson.Gson;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.ConditionVariable;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;

public class TourTrackingDataService extends Service{
	//Copied>>
	private LocationManager mLocationManager = null;
	private static final int LOCATION_INTERVAL = 1000;
	private static final float LOCATION_DISTANCE = 10f;
	//Copied<<
	
	Intent intent=null;
	private String tag="TourTrackingDataService";
	private ConditionVariable mCondition;
	Tour tr;
	// This is the object that receives interactions from clients.
	private final IBinder mBinder = new Binder() {
		@Override
		protected boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
			return super.onTransact(code, data, reply, flags);
		}
	};


	// Create Runnable object
	private Runnable mTask = new Runnable() {
		public void run() {
			//Intent intent = new Intent(getApplicationContext(), TrackingActivity.class);
			//Gson gson = new Gson();
			//String jsonTour = gson.toJson(tr);
			//intent.putExtra("jsonTour", jsonTour);
			//intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			//startActivity(intent);
			//TODO Start tracking Activity
		}
	};

	@Override
	public IBinder onBind(Intent intent) {
		Log.d(tag, "onBind was called");
		Toast.makeText(this, tag , Toast.LENGTH_LONG).show();
		return mBinder;
	}

	@Override
	public void onCreate() {
		/*super.onCreate();
		Log.d(tag, "onCreate was called");
		/// Start up the thread running the service. Note that we create a
		/// separate thread because the service normally runs in the process's
		/// main thread, which we don't want to block.
		Thread notifyingThread = new Thread(
				null, 					// Thread group
				mTask, 					// Runnable object
				"NotifyingService");	// Thread name
		mCondition = new ConditionVariable(false);
		Log.d(tag, "condition: " + mCondition);
		notifyingThread.start();
		Log.d(tag, "notifyingThread was started");
		Toast.makeText(this, tag , Toast.LENGTH_LONG).show();*/
	    Log.d(tag, "onCreate");
	    initializeLocationManager();
	    try {
	        mLocationManager.requestLocationUpdates(
	                LocationManager.NETWORK_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
	                mLocationListeners[1]);
	    } catch (java.lang.SecurityException ex) {
	        Log.i(tag, "fail to request location update, ignore", ex);
	    } catch (IllegalArgumentException ex) {
	        Log.d(tag, "network provider does not exist, " + ex.getMessage());
	    }
	    try {
	        mLocationManager.requestLocationUpdates(
	                LocationManager.GPS_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
	                mLocationListeners[0]);
	    } catch (java.lang.SecurityException ex) {
	        Log.i(tag, "fail to request location update, ignore", ex);
	    } catch (IllegalArgumentException ex) {
	        Log.d(tag, "gps provider does not exist " + ex.getMessage());
	    }
		
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.d(tag, "onStartCommand was called");
		Toast.makeText(this, tag , Toast.LENGTH_LONG).show();
		this.intent=intent;
		// Get Tour from intent 
		String jsonTour = (String) intent.getExtras().get("jsonTour");
		Gson gson = new Gson();
		tr = gson.fromJson(jsonTour, Tour.class);
		return START_NOT_STICKY;
	}

	@Override
	public void onDestroy() {
		/*// Stop the thread from generating further notifications
		mCondition.open();
		super.onDestroy();*/
		   Log.d(tag, "onDestroy");
		    super.onDestroy();
		    if (mLocationManager != null) {
		        for (int i = 0; i < mLocationListeners.length; i++) {
		            try {
		                mLocationManager.removeUpdates(mLocationListeners[i]);
		            } catch (Exception ex) {
		                Log.i(tag, "fail to remove location listners, ignore", ex);
		            }
		        }
		    }
	}

	private class LocationListener implements android.location.LocationListener{
		Location mLastLocation;
		public LocationListener(String provider)
		{
			Log.d(tag, "LocationListener " + provider);
			mLastLocation = new Location(provider);
		}
		@Override
		public void onLocationChanged(Location location)
		{
			Log.d(tag, "onLocationChanged: " + location);
			mLastLocation.set(location);
			String tag = "TrackingActivity onLocationChanged";
			if (tr != null) {
				// Build tracking object
				Tracking tracking = new Tracking();
				tracking.setTour_id(tr.getTour_id());
				tracking.setLatitude(location.getLatitude());
				tracking.setLongitude(location.getLongitude());
				tracking.setAccuracy(location.getAccuracy());
				tracking.setAltitude(location.getAltitude());
				tracking.setSpeed(location.getSpeed());
				SchemaHelper sh = new SchemaHelper(getBaseContext());
				int _id = (int) sh.addTracking(tracking);
				tracking.setTracking_id(_id);
				sh.close();
				Log.d(tag, "Tracking" + tracking.getLatitude() + " "+tracking.getLongitude());
			}
		}
		@Override
		public void onProviderDisabled(String provider)
		{
			Log.d(tag, "onProviderDisabled: " + provider);            
		}
		@Override
		public void onProviderEnabled(String provider)
		{
			Log.d(tag, "onProviderEnabled: " + provider);
		}
		@Override
		public void onStatusChanged(String provider, int status, Bundle extras)
		{
			Log.d(tag, "onStatusChanged: " + provider);
		}
	}
	
	LocationListener[] mLocationListeners = new LocationListener[] {
			new LocationListener(LocationManager.GPS_PROVIDER),
			new LocationListener(LocationManager.NETWORK_PROVIDER)
	};
	
	private void initializeLocationManager() {
	    Log.d(tag, "initializeLocationManager");
	    if (mLocationManager == null) {
	        mLocationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
	    }
	}
	
}