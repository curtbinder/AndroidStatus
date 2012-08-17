package info.curtbinder.reefangel.phone;

/*
 * Copyright (c) 2011-12 by Curt Binder (http://curtbinder.info)
 *
 * This work is made available under the terms of the 
 * Creative Commons Attribution-NonCommercial-ShareAlike 3.0 Unported License
 * http://creativecommons.org/licenses/by-nc-sa/3.0/
 */

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class ControllerService extends Service {
	private static final String TAG = ControllerService.class.getSimpleName();

	private static RAApplication rapp;
	private ServiceReceiver receiver;
	private IntentFilter filter;

	private ScheduledExecutorService serviceThread;

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

			// create the thread executor
			serviceThread = Executors.newSingleThreadScheduledExecutor();

			long interval = rapp.getUpdateInterval();
			if ( interval > 0 ) {
				Log.d( TAG, "auto update interval " + interval/60 + " minutes" );
				createScheduledUpdate( interval );
			}

			rapp.isServiceRunning = true;
		}
		return START_STICKY;
	}

	private boolean isNetworkAvailable ( ) {
		boolean fAvailable = false;
		ConnectivityManager con =
				(ConnectivityManager) rapp
						.getSystemService( CONNECTIVITY_SERVICE );
		NetworkInfo n = con.getActiveNetworkInfo();
		if ( n != null ) {
			if ( n.isConnected() ) {
				fAvailable = true;
			}
		}
		return fAvailable;
	}

	private void createScheduledUpdate ( long interval ) {
		// repeating update is only for the status
		// create a host and configure it
		Host h = new Host();
		if ( rapp.isCommunicateController() ) {
			// controller
			h.setHost( rapp.getPrefHost() );
			h.setPort( rapp.getPrefPort() );
			h.setCommand( Globals.requestStatus );
		} else {
			// reeefangel.com
			h.setUserId( rapp.getPrefUserId() );
			h.setCommand( Globals.requestReefAngel );
		}
		serviceThread.scheduleAtFixedRate(	new ControllerTask( rapp, h ), 0L,
											interval, TimeUnit.SECONDS );
	}

	class ServiceReceiver extends BroadcastReceiver {
		@Override
		public void onReceive ( Context context, Intent intent ) {
			// Log.d(TAG, "onReceive");
			// receive messages

			processRACommand( intent );
		}
	}

	private void notControllerMessage ( ) {
		// TODO update this for portal
		Log.d( TAG, "Not a controller" );
		Toast.makeText( rapp.getBaseContext(), R.string.messageNotController,
						Toast.LENGTH_LONG ).show();
	}

	private void processRACommand ( Intent intent ) {
		// create new ControllerTask based on values received
		// post ControllerTask to serviceThread
		String action = intent.getAction();
		String command = Globals.requestNone;
		boolean isController = rapp.isCommunicateController();
		Host h = new Host();

		// setup the basics for the host first
		if ( isController ) {
			// controller
			h.setHost( rapp.getPrefHost() );
			h.setPort( rapp.getPrefPort() );
		} else {
			// reeefangel.com
			h.setUserId( rapp.getPrefUserId() );
		}

		if ( action.equals( MessageCommands.QUERY_STATUS_INTENT ) ) {
			Log.d( TAG, "Query status" );
			if ( isController )
				command = Globals.requestStatus;
			else
				command = Globals.requestReefAngel;

			h.setCommand( command );
		} else if ( action.equals( MessageCommands.TOGGLE_RELAY_INTENT ) ) {
			Log.d( TAG, "Toggle Relay" );
			if ( isController )
				command =
						new String(
							String.format(	"%s%d%d",
											Globals.requestRelay,
											intent.getIntExtra( MessageCommands.TOGGLE_RELAY_PORT_INT,
																Globals.defaultPort ),
											intent.getIntExtra( MessageCommands.TOGGLE_RELAY_MODE_INT,
																Globals.defaultPort ) ) );
			else
				command = Globals.requestReefAngel;

			h.setCommand( command );
		} else if ( action.equals( MessageCommands.MEMORY_SEND_INTENT ) ) {
			Log.d( TAG, "Memory" );
			int value =
					intent.getIntExtra( MessageCommands.MEMORY_SEND_VALUE_INT,
										Globals.memoryReadOnly );
			int location =
					intent.getIntExtra( MessageCommands.MEMORY_SEND_LOCATION_INT,
										Globals.memoryReadOnly );
			String type =
					intent.getStringExtra( MessageCommands.MEMORY_SEND_TYPE_STRING );
			if ( type.equals( null ) || (location == Globals.memoryReadOnly) ) {
				Log.d( TAG, "No memory specified" );
				return;
			}

			if ( !isController ) {
				notControllerMessage();
				return;
			}

			h.setCommand( type );
			if ( value == Globals.memoryReadOnly )
				h.setReadLocation( location );
			else
				h.setWriteLocation( location, value );
		} else if ( action.equals( MessageCommands.LABEL_QUERY_INTENT ) ) {
			Log.d( TAG, "Query labels" );
			// set the userid
			h.setUserId( rapp.getPrefUserId() );
			h.setGetLabelsOnly( true );
		} else if ( action.equals( MessageCommands.COMMAND_SEND_INTENT ) ) {
			Log.d( TAG, "Command Send" );
			if ( !isController ) {
				notControllerMessage();
				return;
			}
			h.setCommand( intent
					.getStringExtra( MessageCommands.COMMAND_SEND_STRING ) );
		} else if ( action.equals( MessageCommands.VERSION_QUERY_INTENT ) ) {
			Log.d( TAG, "Query version" );
			if ( !isController ) {
				notControllerMessage();
				return;
			}
			h.setCommand( Globals.requestVersion );
		} else if ( action.equals( MessageCommands.DATE_QUERY_INTENT ) ) {
			Log.d( TAG, "Query Date" );
			if ( !isController ) {
				notControllerMessage();
				return;
			}
			h.setCommand( Globals.requestDateTime );
		} else if ( action.equals( MessageCommands.DATE_SEND_INTENT ) ) {
			Log.d( TAG, "Set Date" );
			if ( !isController ) {
				notControllerMessage();
				return;
			}
			h.setCommand( intent
					.getStringExtra( MessageCommands.DATE_SEND_STRING ) );
		} else {
			Log.d( TAG, "Unknown command" );
			return;
		}
		Log.d( TAG, "Task Host: " + h.toString() );
		// schedule the task to execute after 0 second delay (aka, immediately)
		if ( isNetworkAvailable() )
			serviceThread.schedule( new ControllerTask( rapp, h ), 0L,
									TimeUnit.SECONDS );
		else
			// TODO remove Toast
			Toast.makeText( rapp.getBaseContext(),
							R.string.messageNetworkOffline, Toast.LENGTH_LONG )
					.show();
	}

}
