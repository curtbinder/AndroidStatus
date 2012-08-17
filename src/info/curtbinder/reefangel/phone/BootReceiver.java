package info.curtbinder.reefangel.phone;

/*
 * Copyright (c) 2011-12 by Curt Binder (http://curtbinder.info)
 *
 * This work is made available under the terms of the 
 * Creative Commons Attribution-NonCommercial-ShareAlike 3.0 Unported License
 * http://creativecommons.org/licenses/by-nc-sa/3.0/
 */

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BootReceiver extends BroadcastReceiver {
	private static final String TAG = BootReceiver.class.getSimpleName();

	@Override
	public void onReceive ( Context context, Intent intent ) {
		Log.d( TAG, "onReceive Boot" );
		// Start the controller service
		context.startService( new Intent( context, ControllerService.class ) );

		// TODO check to see if we need to start the repeating update service
		// grab the service interval, make sure it's greater than 0
		// create a status query message
		long interval = 0;
		if ( interval == 0 ) 
			return;
		
		Intent i =
				((RAApplication) context.getApplicationContext())
						.getUpdateIntent();
		PendingIntent pi =
				PendingIntent.getService(	context, -1, i,
											PendingIntent.FLAG_UPDATE_CURRENT );
		// setup alarm service to wake up and start the service periodically
		AlarmManager am =
				(AlarmManager) context.getSystemService( Context.ALARM_SERVICE );
		am.setInexactRepeating( AlarmManager.RTC, System.currentTimeMillis(), interval, pi );
		Log.d( TAG, "started auto update" );
	}

}
