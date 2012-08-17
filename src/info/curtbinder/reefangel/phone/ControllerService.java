package info.curtbinder.reefangel.phone;

/*
 * Copyright (c) 2011-12 by Curt Binder (http://curtbinder.info)
 *
 * This work is made available under the terms of the 
 * Creative Commons Attribution-NonCommercial-ShareAlike 3.0 Unported License
 * http://creativecommons.org/licenses/by-nc-sa/3.0/
 */

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

	@Override
	public IBinder onBind ( Intent intent ) {
		return null;
	}

	@Override
	public synchronized void onCreate ( ) {
		super.onCreate();

		Log.d( TAG, "onCreate" );

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

	@Override
	public synchronized void onDestroy ( ) {
		super.onDestroy();

		Log.d( TAG, "onDestroy" );

		if ( rapp.isServiceRunning ) {
			unregisterReceiver( receiver );
			rapp.isServiceRunning = false;
		}
	}

	@Override
	public synchronized int onStartCommand (
			Intent intent,
			int flags,
			int startId ) {
		super.onStartCommand( intent, flags, startId );

		Log.d( TAG, "onStartCommand" );
		if ( rapp.isFirstRun() ) {
			Log.d( TAG, "first run, not starting service until configured" );
			return START_STICKY;
		}

		if ( !rapp.isServiceRunning ) {
			Log.d( TAG, "start Service" );
			// register the receiver
			registerReceiver( receiver, filter, Permissions.SEND_COMMAND, null );
			registerReceiver( receiver, filter, Permissions.QUERY_STATUS, null );

			/*
			long interval = rapp.getUpdateInterval();
			if ( interval > 0 ) {
				Log.d( TAG, "auto update interval " + interval / 60
							+ " minutes" );
				// createScheduledUpdate( interval );
			}
			*/

			rapp.isServiceRunning = true;
		}
		return START_STICKY;
	}

	/*
	 * private void createScheduledUpdate ( long interval ) { // repeating
	 * update is only for the status // create a host and configure it Host h =
	 * new Host(); if ( rapp.isCommunicateController() ) { // controller
	 * h.setHost( rapp.getPrefHost() ); h.setPort( rapp.getPrefPort() );
	 * h.setCommand( Globals.requestStatus ); } else { // reeefangel.com
	 * h.setUserId( rapp.getPrefUserId() ); h.setCommand(
	 * Globals.requestReefAngel ); } Log.d( TAG, "AutoUpdate: " + h.toString()
	 * ); serviceThread.scheduleAtFixedRate( new ControllerTask( rapp, h ), 0L,
	 * interval, TimeUnit.SECONDS ); }
	 */

	class ServiceReceiver extends BroadcastReceiver {
		@Override
		public void onReceive ( Context context, Intent intent ) {
			// Log.d(TAG, "onReceive");
			// receive messages and pass on to updater service
			Intent i = new Intent(context, UpdateService.class);
			i.setAction( intent.getAction() );
			i.putExtras( intent );
			context.startService( i );
		}
	}

}
