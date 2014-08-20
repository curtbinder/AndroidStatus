/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2012 Curt Binder
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
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
            h.setWifiUsername( raprefs.getDeviceWifiUsername() );
            h.setWifiPassword( raprefs.getDeviceWifiPassword() );
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
            h.setWifiUsername( raprefs.getDeviceWifiUsername() );
            h.setWifiPassword( raprefs.getDeviceWifiPassword() );
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
                command = String.format( "%s%d%d",
                            RequestCommands.Relay,
                            intent.getIntExtra(MessageCommands.TOGGLE_RELAY_PORT_INT, Globals.defaultPort),
                            intent.getIntExtra(MessageCommands.TOGGLE_RELAY_MODE_INT, Globals.defaultPort) );
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
        } else if ( action.equals( MessageCommands.OVERRIDE_SEND_INTENT ) ) {
            if ( !isController ) {
                notControllerMessage();
                return;
            }
            int value = intent.getIntExtra(MessageCommands.OVERRIDE_SEND_VALUE_INT, 0);
            int location = intent.getIntExtra( MessageCommands.OVERRIDE_SEND_LOCATION_INT, Globals.OVERRIDE_DISABLE );
            h.setCommand( RequestCommands.PwmOverride );
            h.setOverrideChannel( location, value );
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
        } else if ( action.equals( MessageCommands.CALIBRATE_SEND_INTENT ) ) {
            if ( !isController ) {
                notControllerMessage();
                return;
            }
            h.setCommand( RequestCommands.Calibrate );
            h.setCalibrateType( intent.getIntExtra(MessageCommands.CALIBRATE_SEND_LOCATION_INT,
                    Globals.CALIBRATE_PH) );
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
            Log.d( TAG, "Unknown command" );
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
