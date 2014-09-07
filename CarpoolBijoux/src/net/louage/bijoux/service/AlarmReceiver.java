package net.louage.bijoux.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
//import android.util.Log;
import android.widget.Toast;

public class AlarmReceiver extends BroadcastReceiver{
	public static String ACTION_ALARM = "startSyncTrackingDataService";

	@Override
	public void onReceive(Context context, Intent intent) {
		//Log.d("Alarm Receiver", "Entered");
		Toast.makeText(context, "Entered", Toast.LENGTH_SHORT).show();

		Bundle bundle = intent.getExtras();
		String action = bundle.getString(ACTION_ALARM);
		if (action.equals(ACTION_ALARM)) {
			//Log.d("Alarm Receiver", "If loop");
			Intent inService = new Intent(context,SyncTrackingDataService.class);
			context.startService(inService);
		}
		else
		{
			//Log.d("Alarm Receiver", "Else loop");
			Toast.makeText(context, "Else loop", Toast.LENGTH_SHORT).show();
		}
	}
}
