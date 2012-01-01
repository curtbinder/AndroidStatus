package info.curtbinder.reefangel.phone;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

public class ControllerService extends Service {

	// Messages
	public static final String QUERY_STATUS_INTENT = Globals.PACKAGE_BASE
														+ ".QUERY_STATUS";
	public static final String TOGGLE_RELAY_INTENT = Globals.PACKAGE_BASE
														+ ".TOGGLE_RELAY";
	public static final String TOGGLE_RELAY_PORT_INT = "TOGGLE_RELAY_PORT_INT";
	public static final String TOGGLE_RELAY_MODE_INT = "TOGGLE_RELAY_MODE_INT";
	public static final String MEMORY_INTENT = Globals.PACKAGE_BASE + ".MEMORY";
	public static final String MEMORY_TYPE_STRING = "MEMORY_TYPE_STRING";
	public static final String MEMORY_LOCATION_INT = "MEMORY_LOCATION_INT";
	public static final String MEMORY_VALUE_INT = "MEMORY_VALUE_INT";
	public static final String LABEL_QUERY_INTENT = Globals.PACKAGE_BASE
													+ ".LABEL_QUERY";

	private static final String TAG = ControllerService.class.getSimpleName();

	private static RAApplication rapp;
	private ServiceReceiver receiver;
	private IntentFilter filter;

	private ExecutorService serviceThread;

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
		filter = new IntentFilter( QUERY_STATUS_INTENT );
		filter.addAction( TOGGLE_RELAY_INTENT );
		filter.addAction( MEMORY_INTENT );
		filter.addAction( LABEL_QUERY_INTENT );
		filter.addAction( ControllerTask.COMMAND_SEND_INTENT );
		filter.addAction( ControllerTask.VERSION_QUERY_INTENT );
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
	public synchronized void onStart ( Intent intent, int startId ) {
		super.onStart( intent, startId );

		Log.d( TAG, "onStart" );

		if ( !rapp.isServiceRunning ) {
			Log.d( TAG, "start Service" );
			// register the receiver
			registerReceiver( receiver, filter );

			// create the thread executor
			serviceThread = Executors.newSingleThreadExecutor();

			rapp.isServiceRunning = true;
		}
	}

	class ServiceReceiver extends BroadcastReceiver {
		@Override
		public void onReceive ( Context context, Intent intent ) {
			// Log.d(TAG, "onReceive");
			// receive messages
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

			if ( action.equals( QUERY_STATUS_INTENT ) ) {
				Log.d( TAG, "Query status" );
				if ( isController )
					command = Globals.requestStatus;
				else
					command = Globals.requestReefAngel;

				h.setCommand( command );
			} else if ( action.equals( TOGGLE_RELAY_INTENT ) ) {
				Log.d( TAG, "Toggle Relay" );
				if ( isController )
					command =
							new String(
								String.format(	"%s%d%d",
												Globals.requestRelay,
												intent.getIntExtra( TOGGLE_RELAY_PORT_INT,
																	Globals.defaultPort ),
												intent.getIntExtra( TOGGLE_RELAY_MODE_INT,
																	Globals.defaultPort ) ) );
				else
					command = Globals.requestReefAngel;

				h.setCommand( command );
			} else if ( action.equals( MEMORY_INTENT ) ) {
				Log.d( TAG, "Memory" );
				int value =
						intent.getIntExtra( MEMORY_VALUE_INT,
											Globals.memoryReadOnly );
				int location =
						intent.getIntExtra( MEMORY_LOCATION_INT,
											Globals.memoryReadOnly );
				String type = intent.getStringExtra( MEMORY_TYPE_STRING );
				if ( type.equals( null )
						|| (location == Globals.memoryReadOnly) ) {
					Log.d( TAG, "No memory specified" );
					return;
				}

				if ( !isController ) {
					// TODO update this for portal
					Log.d( TAG, "Not a controller" );
					return;
				}

				h.setCommand( type );
				if ( value == Globals.memoryReadOnly )
					h.setReadLocation( location );
				else
					h.setWriteLocation( location, value );
			} else if ( action.equals( LABEL_QUERY_INTENT ) ) {
				Log.d( TAG, "Query labels" );
				// set the userid
				h.setUserId( rapp.getPrefUserId() );
				h.setGetLabelsOnly( true );
			} else if ( action.equals( ControllerTask.COMMAND_SEND_INTENT )) {
				Log.d(TAG, "Command Send");
				h.setCommand( intent.getStringExtra( ControllerTask.COMMAND_SEND_STRING ) );
			} else if ( action.equals(ControllerTask.VERSION_QUERY_INTENT)) {
				Log.d(TAG, "Query version");
				h.setCommand( Globals.requestVersion );
			} else {
				Log.d( TAG, "Unknown command" );
				return;
			}
			Log.d( TAG, "Task Host: " + h.toString() );
			// submit to thread for execution
			serviceThread.submit( new ControllerTask( rapp, h ) );
		}
	}
}
