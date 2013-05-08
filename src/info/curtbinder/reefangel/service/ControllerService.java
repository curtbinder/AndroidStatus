/*
 * Copyright (c) 2011-2013 by Curt Binder (http://curtbinder.info)
 * 
 * This work is made available under the terms of the Creative Commons
 * Attribution-NonCommercial-ShareAlike 3.0 Unported License
 * http://creativecommons.org/licenses/by-nc-sa/3.0/
 */

package info.curtbinder.reefangel.service;

import info.curtbinder.reefangel.phone.Permissions;
import info.curtbinder.reefangel.phone.RAApplication;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

public class ControllerService extends Service {
	private static final String TAG = ControllerService.class.getSimpleName();

	private static RAApplication rapp;
	private ServiceReceiver receiver;
	private IntentFilter filter;

	public IBinder onBind ( Intent intent ) {
		return null;
	}

	public synchronized void onCreate ( ) {
		super.onCreate();

		rapp = (RAApplication) getApplication();
		receiver = new ServiceReceiver();
		filter = new IntentFilter( MessageCommands.QUERY_STATUS_INTENT );
		filter.addAction( MessageCommands.TOGGLE_RELAY_INTENT );
		filter.addAction( MessageCommands.MEMORY_SEND_INTENT );
		filter.addAction( MessageCommands.LABEL_QUERY_INTENT );
		filter.addAction( MessageCommands.COMMAND_SEND_INTENT );
		filter.addAction( MessageCommands.VERSION_QUERY_INTENT );
		filter.addAction( MessageCommands.DATE_QUERY_INTENT );
		filter.addAction( MessageCommands.DATE_SEND_INTENT );
	}

	public synchronized void onDestroy ( ) {
		super.onDestroy();

		if ( rapp.isServiceRunning ) {
			unregisterReceiver( receiver );
			rapp.isServiceRunning = false;
		}
	}

	public synchronized int onStartCommand (
			Intent intent,
			int flags,
			int startId ) {
		super.onStartCommand( intent, flags, startId );

		if ( rapp.isFirstRun() ) {
			Log.d( TAG, "first run, not starting service until configured" );
			return START_STICKY;
		}

		if ( !rapp.isServiceRunning ) {
			Log.d( TAG, "start Service" );
			// register the receiver
			registerReceiver( receiver, filter, Permissions.SEND_COMMAND, null );
			registerReceiver( receiver, filter, Permissions.QUERY_STATUS, null );

			rapp.isServiceRunning = true;
		}
		return START_STICKY;
	}

	class ServiceReceiver extends BroadcastReceiver {

		public void onReceive ( Context context, Intent intent ) {
			// Log.d(TAG, "onReceive");
			// receive messages and pass on to updater service
			Intent i = new Intent( context, UpdateService.class );
			i.setAction( intent.getAction() );
			i.putExtras( intent );
			context.startService( i );
		}
	}

}
