package net.louage.bijoux.service;

import java.util.ArrayList;

import net.louage.bijoux.R;
import net.louage.bijoux.constants.DateTime;
import net.louage.bijoux.constants.SharedPreferences;
import net.louage.bijoux.model.Tracking;
import net.louage.bijoux.sqlite.SchemaHelper;
import net.louage.bijoux.server.AsTskObjectCompleteListener;
import net.louage.bijoux.server.TrackingAsyncCreateUpdate;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.ConditionVariable;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
//import android.util.Log;
import android.widget.Toast;

public class SyncTrackingDataService extends Service {
	String tag="SyncTrackingDataService";
	private static int TRACKING_STATUS=R.drawable.ic_action_refresh;
	private ConditionVariable mCondition;
	private NotificationManager mNM;
	
	// This is the object that receives interactions from clients. See
	// RemoteService for a more complete example.
	private final IBinder mBinder = new Binder() {
		@Override
		protected boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
			return super.onTransact(code, data, reply, flags);
		}
	};
	
	// Create Runnable object
	private Runnable mTask = new Runnable() {
		public void run() {
			//Log.d("SyncTrackingDataService Runnable: ", "Runnable() Started");
			try {
				ArrayList<Tracking> trackings = new ArrayList<Tracking>();
				SchemaHelper sh = new SchemaHelper(getBaseContext());
				trackings=sh.trackingSelectForCloud();
				sh.close();			
				//Log.d("SyncTrackingDataService trackings: ", "trackings.size()"+trackings.size());				
				for (int i = 0; i < trackings.size(); i++) {
					Tracking tr = new Tracking();
					tr=trackings.get(i);
					//Log.d("SyncTrackingDataService trackingSelectForCloud: ", tr.getTracking_id()+" - lat: "+tr.getLatitude());
					String[] paramsTracking = getMapsParamsTrack(tr);
					new TrackingAsyncCreateUpdate(getBaseContext(), new TrackingAsyncCreateUpdateCompleteListener(), paramsTracking).execute();
				}
				showNotification(R.drawable.ic_action_refresh, "Syncronisation was completed");
			} catch (Exception e) {
				e.printStackTrace();
				showNotification(R.drawable.ic_action_refresh, "Syncronisation not completed");
			}
			
			// Done with our work... stop the service!
			SyncTrackingDataService.this.stopSelf();
		}

		private String[] getMapsParamsTrack(Tracking tr) {
			String user_id = String.valueOf(SharedPreferences.getUser(getBaseContext()).getUser_id());
			//The tracking_id must be zero to create a new tracking record online
			//The return _id from the MySQL will be save in the field 'cloud_id'
			String virtual_id = String.valueOf(0);
			String track_date_time = DateTime.getStrDateTimeStamp(tr.getTrack_date_time());
			String tour_id = String.valueOf(tr.getTour_id());
			String latitude = String.valueOf(tr.getLatitude());
			String longitude = String.valueOf(tr.getLongitude());
			String accuracy = String.valueOf(tr.getAccuracy());
			String altitude = String.valueOf(tr.getAltitude());
			String speed = String.valueOf(tr.getSpeed());
			String tracking_id = String.valueOf(tr.getTracking_id());
			String[] params = new String[] { user_id,
					virtual_id,
					track_date_time,
					tour_id,
					latitude,
					longitude,
					accuracy,
					altitude,
					speed,
					tracking_id};
			return params;
		}
		
		class TrackingAsyncCreateUpdateCompleteListener implements
		AsTskObjectCompleteListener<Tracking> {
			@Override
			public void onTaskComplete(Tracking track) {
				//Log.d("TrackingAsyncCreateUpdateCompleteListener: ", "onTaskComplete(Tracking track) Started");
				Tracking tr = new Tracking();
				tr = track;
				//Log.d("onTaskComplete (Tracking track): ", "Tracking_id: "+tr.getTracking_id());
				//Log.d("onTaskComplete (Tracking track): ", "Accuracy: "+tr.getAccuracy());
				//Log.d("onTaskComplete (Tracking track): ", "Altitude: "+tr.getAltitude());
				//Log.d("onTaskComplete (Tracking track): ", "Cloud_id: "+tr.getCloud_id());
				//Log.d("onTaskComplete (Tracking track): ", "Latitude: "+tr.getLatitude());
				//Log.d("onTaskComplete (Tracking track): ", "Longitude: "+tr.getLongitude());
				//Log.d("onTaskComplete (Tracking track): ", "Speed: "+tr.getSpeed());
				//Log.d("onTaskComplete (Tracking track): ", "Tour_id: "+tr.getTour_id());
				//Log.d("onTaskComplete (Tracking track): ", "Track_date_time(): "+tr.getTrack_date_time());
				SchemaHelper sh = new SchemaHelper(getBaseContext());
				sh.trackingUpdate(tr);				
				sh.close();
			}
}
	};

	@Override
	public IBinder onBind(Intent intent) {
		//Log.d(tag, "onBind was called");
		Toast.makeText(this, tag , Toast.LENGTH_LONG).show();
		
		//TODO sync tracking data from sqLite to MysQL online
		return mBinder;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		//Log.d(tag, "onCreate was called");
		mNM = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		/// Start up the thread running the service. Note that we create a
		/// separate thread because the service normally runs in the process's
		/// main thread, which we don't want to block.
		Thread notifyingThread = new Thread(
				null, 					// Thread group
				mTask, 					// Runnable object
				"NotifyingService");	// Thread name
		mCondition = new ConditionVariable(false);
		//Log.d(tag, "condition: " + mCondition);
		notifyingThread.start();
		//Log.d(tag, "notifyingThread was started");
		Toast.makeText(this, tag , Toast.LENGTH_LONG).show();
	}
	
	

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		//Log.d(tag, "onStartCommand was called");
		Toast.makeText(this, tag , Toast.LENGTH_LONG).show();
		return START_NOT_STICKY;
	}
	
	@Override
	public void onDestroy() {
		// Cancel the persistent notification.
		//mNM.cancel(TRACKING_STATUS);
		// Stop the thread from generating further notifications
		mCondition.open();
		super.onDestroy();
	}

	@SuppressWarnings("deprecation")
	private void showNotification(int moodId, String message) {
		// In this sample, we'll use the same text for the ticker and the
		// expanded notification
		CharSequence text = message;

		// Set the icon, scrolling text and timestamp.
		// Note that in this example, we pass null for tickerText. We update the
		// icon enough that
		// it is distracting to show the ticker text every time it changes. We
		// strongly suggest
		// that you do this as well. (Think of of the "New hardware found" or
		// "Network connection
		// changed" messages that always pop up)
		Notification notification = new Notification(moodId, null, System.currentTimeMillis());

		// The PendingIntent to launch our activity if the user selects this notification
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0, new Intent(this, SyncTrackingDataService.class), 0);

		// Set the info for the views that show in the notification panel.
		notification.setLatestEventInfo(this,"Tracking Status", text, contentIntent);

		// Send the notification.
		// We use a layout id because it is a unique number. We use it later to cancel.
		mNM.notify(TRACKING_STATUS, notification);
	}
	
}
