/*
 * Copyright (c) 2011-2013 by Curt Binder (http://curtbinder.info)
 * 
 * This work is made available under the terms of the Creative Commons
 * Attribution-NonCommercial-ShareAlike 3.0 Unported License
 * http://creativecommons.org/licenses/by-nc-sa/3.0/
 */

package info.curtbinder.reefangel.service;

import info.curtbinder.reefangel.phone.Globals;
import info.curtbinder.reefangel.phone.R;
import info.curtbinder.reefangel.phone.RAApplication;
import info.curtbinder.reefangel.phone.RAPreferences;
import android.app.IntentService;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

public class UpdateService extends IntentService {

	private static final String TAG = UpdateService.class.getSimpleName();
	private static RAApplication rapp;

	public UpdateService () {
		super( TAG );
	}

	private void notControllerMessage ( ) {
		// TODO update this for portal
		Toast.makeText( rapp.getBaseContext(), R.string.messageNotController,
						Toast.LENGTH_LONG ).show();
	}

	private boolean isNetworkAvailable ( ) {
		boolean fAvailable = false;
		ConnectivityManager con =
				(ConnectivityManager) getApplication()
						.getSystemService( CONNECTIVITY_SERVICE );
		NetworkInfo n = con.getActiveNetworkInfo();
		if ( n != null ) {
			if ( n.isConnected() ) {
				fAvailable = true;
			}
		}
		return fAvailable;
	}

	protected void onHandleIntent ( Intent intent ) {
		// create new ControllerTask based on values received
		// run the task
		rapp = (RAApplication) getApplication();
		String action = intent.getAction();
		int profile_update =
				intent.getIntExtra( MessageCommands.AUTO_UPDATE_PROFILE_INT, -1 );
		if ( action.equals( MessageCommands.QUERY_STATUS_INTENT )
				&& (profile_update > -1) ) {
			processAutoUpdate( profile_update );
			return;
		}
		processRACommand( intent );
	}

	private void processAutoUpdate ( int profile_update ) {
		final RAPreferences raprefs = rapp.raprefs;
		Host h =
				new Host( raprefs.getConnectionTimeout(),
					raprefs.getReadTimeout() );
		if ( raprefs.isCommunicateController() ) {
			// controller
			String host, port;
			if ( rapp.isAwayProfileEnabled() ) {
				// only check if the away profile is enabled
				switch ( profile_update ) {
					default:
					case Globals.profileAlways:
						host = raprefs.getHost();
						port = raprefs.getPort();
						break;
					case Globals.profileOnlyAway:
						host = raprefs.getAwayHost();
						port = raprefs.getAwayPort();
						break;
					case Globals.profileOnlyHome:
						host = raprefs.getHomeHost();
						port = raprefs.getHomePort();
						break;
				}
			} else {
				host = raprefs.getHost();
				port = raprefs.getPort();
			}
			h.setHost( host );
			h.setPort( port );
			h.setCommand( RequestCommands.Status );
		} else {
			// reeefangel.com / portal
			h.setUserId( raprefs.getUserId() );
			h.setCommand( RequestCommands.ReefAngel );
		}
		Log.d( TAG, "AutoUpdate: " + h.toString() );
		runTask( h );
	}

	private void processRACommand ( Intent intent ) {
		String action = intent.getAction();
		String command = RequestCommands.None;
		final RAPreferences raprefs = rapp.raprefs;
		boolean isController = raprefs.isCommunicateController();
		Host h =
				new Host( raprefs.getConnectionTimeout(),
					raprefs.getReadTimeout() );

		// setup the basics for the host first
		if ( isController ) {
			// controller
			h.setHost( raprefs.getHost() );
			h.setPort( raprefs.getPort() );
		} else {
			// reeefangel.com
			h.setUserId( raprefs.getUserId() );
		}

		if ( action.equals( MessageCommands.QUERY_STATUS_INTENT ) ) {
			if ( isController )
				command = RequestCommands.Status;
			else
				command = RequestCommands.ReefAngel;

			h.setCommand( command );
		} else if ( action.equals( MessageCommands.TOGGLE_RELAY_INTENT ) ) {
			if ( isController )
				command =
						new String(
							String.format(	"%s%d%d",
											RequestCommands.Relay,
											intent.getIntExtra( MessageCommands.TOGGLE_RELAY_PORT_INT,
																Globals.defaultPort ),
											intent.getIntExtra( MessageCommands.TOGGLE_RELAY_MODE_INT,
																Globals.defaultPort ) ) );
			else
				command = RequestCommands.ReefAngel;

			h.setCommand( command );
		} else if ( action.equals( MessageCommands.MEMORY_SEND_INTENT ) ) {
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
			// set the userid
			h.setUserId( raprefs.getUserId() );
			h.setGetLabelsOnly( true );
		} else if ( action.equals( MessageCommands.COMMAND_SEND_INTENT ) ) {
			if ( !isController ) {
				notControllerMessage();
				return;
			}
			h.setCommand( intent
					.getStringExtra( MessageCommands.COMMAND_SEND_STRING ) );
		} else if ( action.equals( MessageCommands.VERSION_QUERY_INTENT ) ) {
			if ( !isController ) {
				notControllerMessage();
				return;
			}
			h.setCommand( RequestCommands.Version );
		} else if ( action.equals( MessageCommands.DATE_QUERY_INTENT ) ) {
			if ( !isController ) {
				notControllerMessage();
				return;
			}
			h.setCommand( RequestCommands.DateTime );
		} else if ( action.equals( MessageCommands.DATE_SEND_INTENT ) ) {
			if ( !isController ) {
				notControllerMessage();
				return;
			}
			h.setCommand( intent
					.getStringExtra( MessageCommands.DATE_SEND_STRING ) );
		} else {
			Log.d( TAG, "Unknown command: " + action);
			return;
		}
		Log.d( TAG, "Task Host: " + h.toString() );
		runTask( h );
	}

	private void runTask ( Host h ) {
		// run the task
		if ( isNetworkAvailable() ) {
			ControllerTask ct = new ControllerTask( rapp, h );
			ct.run();
		} else {
			// TODO remove Toast
			Toast.makeText( rapp.getBaseContext(),
							R.string.messageNetworkOffline, Toast.LENGTH_LONG )
					.show();
		}
	}
}
