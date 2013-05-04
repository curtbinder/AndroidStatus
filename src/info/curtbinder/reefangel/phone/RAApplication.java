/*
 * Copyright (c) 2011-2013 by Curt Binder (http://curtbinder.info)
 * 
 * This work is made available under the terms of the Creative Commons
 * Attribution-NonCommercial-ShareAlike 3.0 Unported License
 * http://creativecommons.org/licenses/by-nc-sa/3.0/
 */

package info.curtbinder.reefangel.phone;

import info.curtbinder.reefangel.db.RAData;
import info.curtbinder.reefangel.service.ControllerService;
import info.curtbinder.reefangel.service.MessageCommands;
import info.curtbinder.reefangel.service.UpdateService;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Locale;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

public class RAApplication extends Application {

	private static final String TAG = RAApplication.class.getSimpleName();
	private static final String NUMBER_PATTERN = "\\d+";
	private static final String HOST_PATTERN =
			"^(?i:[[0-9][a-z]]+)(?i:[\\w\\.\\-]*)(?i:[[0-9][a-z]]+)$";
	private static final String USERID_PATTERN = "[\\w\\-\\.]+";

	// Preferences
	public RAPreferences raprefs;

	// Error code stuff
	private String[] errorCodes;
	private String[] errorCodesStrings;
	public int errorCode;
	public int errorCount;

	// Controller Data
	public RAData data;

	// Service Stuff
	public boolean isServiceRunning;

	public void onCreate ( ) {
		// prefs = PreferenceManager.getDefaultSharedPreferences( this );
		errorCodes = getResources().getStringArray( R.array.errorCodes );
		errorCodesStrings =
				getResources().getStringArray( R.array.errorCodesStrings );
		errorCode = 0; // set to no error initially
		data = new RAData( this );
		raprefs = new RAPreferences( this );
		isServiceRunning = false;

		// initialize the error count
		errorCount = 0;

		checkServiceRunning();

	}

	public void checkServiceRunning ( ) {
		// Check if the service is running, if not start it
		if ( !isServiceRunning && !isFirstRun() )
			startService( new Intent( this, ControllerService.class ) );
	}

	public void onTerminate ( ) {
		super.onTerminate();
		data.close();

		if ( isServiceRunning )
			stopService( new Intent( this, ControllerService.class ) );
	}

	public void restartAutoUpdateService ( ) {
		Log.d( TAG, "restarting auto update service" );
		cancelAutoUpdateService();
		startAutoUpdateService();
	}

	public void cancelAutoUpdateService ( ) {
		Log.d( TAG, "cancel auto update" );
		PendingIntent pi = getUpdateIntent();
		AlarmManager am =
				(AlarmManager) getSystemService( Context.ALARM_SERVICE );
		// cancel the repeating service
		am.cancel( pi );
	}

	public void startAutoUpdateService ( ) {
		// check to see if we need to start the repeating update service
		// grab the service interval, make sure it's greater than 0
		long interval = raprefs.getUpdateInterval();
		if ( interval == 0 ) {
			Log.d( TAG, "disabled autoupdate" );
			return;
		}

		int up = raprefs.getUpdateProfile();
		if ( raprefs.isCommunicateController() ) {
			int p = getSelectedProfile();
			Log.d( TAG, "UP: " + up + " P: " + p );
			if ( isAwayProfileEnabled() ) {
				Log.d( TAG, "profiles enabled, checking proper profile" );
				if ( (up == Globals.profileOnlyAway)
						&& (p != Globals.profileAway) ) {
					// only run on away profile and we are not on away profile
					Log.d( TAG, "only run on away, not away" );
					return;
				} else if ( (up == Globals.profileOnlyHome)
							&& (p != Globals.profileHome) ) {
					// only run on home profile and we are not on home profile
					Log.d( TAG, "only run on home, not home" );
					return;
				}
			}
		}

		// create a status query message
		PendingIntent pi = getUpdateIntent();
		// setup alarm service to wake up and start the service periodically
		AlarmManager am =
				(AlarmManager) getSystemService( Context.ALARM_SERVICE );
		am.setInexactRepeating( AlarmManager.RTC, System.currentTimeMillis(),
								interval, pi );
		// Profile, interval, wakeup
		// String profile;
		// switch ( up ) {
		// default:
		// case Globals.profileAlways:
		// profile = "always";
		// break;
		// case Globals.profileOnlyAway:
		// profile = "only away";
		// break;
		// case Globals.profileOnlyHome:
		// profile = "only home";
		// break;
		// }
		// String s =
		// String.format( "%s, %s m", profile,
		// raprefs.getUpdateIntervalDisplay() );
		// Log.d( TAG, "started auto update: " + s );
	}

	private PendingIntent getUpdateIntent ( ) {
		Intent i = new Intent( this, UpdateService.class );
		i.setAction( MessageCommands.QUERY_STATUS_INTENT );
		i.putExtra( MessageCommands.AUTO_UPDATE_PROFILE_INT,
					raprefs.getUpdateProfile() );
		PendingIntent pi =
				PendingIntent.getService(	this, -1, i,
											PendingIntent.FLAG_CANCEL_CURRENT );
		return pi;
	}

	// Data handling
	public void insertData ( Intent i ) {
		ContentValues v = new ContentValues();
		v.put( RAData.PCOL_T1, i.getStringExtra( RAData.PCOL_T1 ) );
		v.put( RAData.PCOL_T2, i.getStringExtra( RAData.PCOL_T2 ) );
		v.put( RAData.PCOL_T3, i.getStringExtra( RAData.PCOL_T3 ) );
		v.put( RAData.PCOL_PH, i.getStringExtra( RAData.PCOL_PH ) );
		v.put( RAData.PCOL_DP, i.getShortExtra( RAData.PCOL_DP, (short) 0 ) );
		v.put( RAData.PCOL_AP, i.getShortExtra( RAData.PCOL_AP, (short) 0 ) );
		v.put( RAData.PCOL_SAL, i.getStringExtra( RAData.PCOL_SAL ) );
		v.put( RAData.PCOL_ORP, i.getStringExtra( RAData.PCOL_ORP ) );
		v.put( RAData.PCOL_ATOHI, i.getBooleanExtra( RAData.PCOL_ATOHI, false ) );
		v.put( RAData.PCOL_ATOLO, i.getBooleanExtra( RAData.PCOL_ATOLO, false ) );
		v.put( RAData.PCOL_LOGDATE, i.getStringExtra( RAData.PCOL_LOGDATE ) );
		v.put(	RAData.PCOL_RDATA,
				i.getShortExtra( RAData.PCOL_RDATA, (short) 0 ) );
		v.put(	RAData.PCOL_RONMASK,
				i.getShortExtra( RAData.PCOL_RONMASK, (short) 0 ) );
		v.put(	RAData.PCOL_ROFFMASK,
				i.getShortExtra( RAData.PCOL_ROFFMASK, (short) 0 ) );
		v.put(	RAData.PCOL_R1DATA,
				i.getShortExtra( RAData.PCOL_R1DATA, (short) 0 ) );
		v.put(	RAData.PCOL_R1ONMASK,
				i.getShortExtra( RAData.PCOL_R1ONMASK, (short) 0 ) );
		v.put(	RAData.PCOL_R1OFFMASK,
				i.getShortExtra( RAData.PCOL_R1OFFMASK, (short) 0 ) );
		v.put(	RAData.PCOL_R2DATA,
				i.getShortExtra( RAData.PCOL_R2DATA, (short) 0 ) );
		v.put(	RAData.PCOL_R2ONMASK,
				i.getShortExtra( RAData.PCOL_R2ONMASK, (short) 0 ) );
		v.put(	RAData.PCOL_R2OFFMASK,
				i.getShortExtra( RAData.PCOL_R2OFFMASK, (short) 0 ) );
		v.put(	RAData.PCOL_R3DATA,
				i.getShortExtra( RAData.PCOL_R3DATA, (short) 0 ) );
		v.put(	RAData.PCOL_R3ONMASK,
				i.getShortExtra( RAData.PCOL_R3ONMASK, (short) 0 ) );
		v.put(	RAData.PCOL_R3OFFMASK,
				i.getShortExtra( RAData.PCOL_R3OFFMASK, (short) 0 ) );
		v.put(	RAData.PCOL_R4DATA,
				i.getShortExtra( RAData.PCOL_R4DATA, (short) 0 ) );
		v.put(	RAData.PCOL_R4ONMASK,
				i.getShortExtra( RAData.PCOL_R4ONMASK, (short) 0 ) );
		v.put(	RAData.PCOL_R4OFFMASK,
				i.getShortExtra( RAData.PCOL_R4OFFMASK, (short) 0 ) );
		v.put(	RAData.PCOL_R5DATA,
				i.getShortExtra( RAData.PCOL_R5DATA, (short) 0 ) );
		v.put(	RAData.PCOL_R5ONMASK,
				i.getShortExtra( RAData.PCOL_R5ONMASK, (short) 0 ) );
		v.put(	RAData.PCOL_R5OFFMASK,
				i.getShortExtra( RAData.PCOL_R5OFFMASK, (short) 0 ) );
		v.put(	RAData.PCOL_R6DATA,
				i.getShortExtra( RAData.PCOL_R6DATA, (short) 0 ) );
		v.put(	RAData.PCOL_R6ONMASK,
				i.getShortExtra( RAData.PCOL_R6ONMASK, (short) 0 ) );
		v.put(	RAData.PCOL_R6OFFMASK,
				i.getShortExtra( RAData.PCOL_R6OFFMASK, (short) 0 ) );
		v.put(	RAData.PCOL_R7DATA,
				i.getShortExtra( RAData.PCOL_R7DATA, (short) 0 ) );
		v.put(	RAData.PCOL_R7ONMASK,
				i.getShortExtra( RAData.PCOL_R7ONMASK, (short) 0 ) );
		v.put(	RAData.PCOL_R7OFFMASK,
				i.getShortExtra( RAData.PCOL_R7OFFMASK, (short) 0 ) );
		v.put(	RAData.PCOL_R8DATA,
				i.getShortExtra( RAData.PCOL_R8DATA, (short) 0 ) );
		v.put(	RAData.PCOL_R8ONMASK,
				i.getShortExtra( RAData.PCOL_R8ONMASK, (short) 0 ) );
		v.put(	RAData.PCOL_R8OFFMASK,
				i.getShortExtra( RAData.PCOL_R8OFFMASK, (short) 0 ) );
		v.put(	RAData.PCOL_PWME0,
				i.getShortExtra( RAData.PCOL_PWME0, (short) 0 ) );
		v.put(	RAData.PCOL_PWME1,
				i.getShortExtra( RAData.PCOL_PWME1, (short) 0 ) );
		v.put(	RAData.PCOL_PWME2,
				i.getShortExtra( RAData.PCOL_PWME2, (short) 0 ) );
		v.put(	RAData.PCOL_PWME3,
				i.getShortExtra( RAData.PCOL_PWME3, (short) 0 ) );
		v.put(	RAData.PCOL_PWME4,
				i.getShortExtra( RAData.PCOL_PWME4, (short) 0 ) );
		v.put(	RAData.PCOL_PWME5,
				i.getShortExtra( RAData.PCOL_PWME5, (short) 0 ) );
		v.put( RAData.PCOL_AIW, i.getShortExtra( RAData.PCOL_AIW, (short) 0 ) );
		v.put( RAData.PCOL_AIB, i.getShortExtra( RAData.PCOL_AIB, (short) 0 ) );
		v.put( RAData.PCOL_AIRB, i.getShortExtra( RAData.PCOL_AIRB, (short) 0 ) );
		v.put( RAData.PCOL_RFM, i.getShortExtra( RAData.PCOL_RFM, (short) 0 ) );
		v.put( RAData.PCOL_RFS, i.getShortExtra( RAData.PCOL_RFS, (short) 0 ) );
		v.put( RAData.PCOL_RFD, i.getShortExtra( RAData.PCOL_RFD, (short) 0 ) );
		v.put( RAData.PCOL_RFW, i.getShortExtra( RAData.PCOL_RFW, (short) 0 ) );
		v.put( RAData.PCOL_RFRB, i.getShortExtra( RAData.PCOL_RFRB, (short) 0 ) );
		v.put( RAData.PCOL_RFR, i.getShortExtra( RAData.PCOL_RFR, (short) 0 ) );
		v.put( RAData.PCOL_RFG, i.getShortExtra( RAData.PCOL_RFG, (short) 0 ) );
		v.put( RAData.PCOL_RFB, i.getShortExtra( RAData.PCOL_RFB, (short) 0 ) );
		v.put( RAData.PCOL_RFI, i.getShortExtra( RAData.PCOL_RFI, (short) 0 ) );
		v.put( RAData.PCOL_IO, i.getShortExtra( RAData.PCOL_IO, (short) 0 ) );
		v.put( RAData.PCOL_C0, i.getShortExtra( RAData.PCOL_C0, (short) 0 ) );
		v.put( RAData.PCOL_C1, i.getShortExtra( RAData.PCOL_C1, (short) 0 ) );
		v.put( RAData.PCOL_C2, i.getShortExtra( RAData.PCOL_C2, (short) 0 ) );
		v.put( RAData.PCOL_C3, i.getShortExtra( RAData.PCOL_C3, (short) 0 ) );
		v.put( RAData.PCOL_C4, i.getShortExtra( RAData.PCOL_C4, (short) 0 ) );
		v.put( RAData.PCOL_C5, i.getShortExtra( RAData.PCOL_C5, (short) 0 ) );
		v.put( RAData.PCOL_C6, i.getShortExtra( RAData.PCOL_C6, (short) 0 ) );
		v.put( RAData.PCOL_C7, i.getShortExtra( RAData.PCOL_C7, (short) 0 ) );
		v.put( RAData.PCOL_EM, i.getShortExtra( RAData.PCOL_EM, (short) 0 ) );
		v.put( RAData.PCOL_REM, i.getShortExtra( RAData.PCOL_REM, (short) 0 ) );
		v.put( RAData.PCOL_PHE, i.getStringExtra( RAData.PCOL_PHE ) );
		v.put( RAData.PCOL_WL, i.getShortExtra( RAData.PCOL_WL, (short) 0 ) );
		data.insert( v );
	}

	// Error Logging
	public void error ( int errorCodeIndex, Throwable t, String msg ) {
		errorCode = Integer.parseInt( errorCodes[errorCodeIndex] );
		Log.e( TAG, msg, t );

		// if logging enabled, save the log
		if ( raprefs.isLoggingEnabled() ) {
			if ( !hasExternalStorage() ) {
				// doesn't have external storage
				Log.d( TAG, "No external storage" );
				Toast.makeText( this,
								getString( R.string.messageNoExternalStorage ),
								Toast.LENGTH_LONG ).show();
				return;
			}
			boolean keepFile = raprefs.isLoggingAppendFile();
			try {
				String sFile = getLoggingFile();
				Log.d( TAG, "File: " + sFile );
				FileWriter fw = new FileWriter( sFile, keepFile );
				PrintWriter pw = new PrintWriter( fw );
				DateFormat dft =
						DateFormat.getDateTimeInstance( DateFormat.DEFAULT,
														DateFormat.DEFAULT,
														Locale.getDefault() );
				pw.println( dft.format( Calendar.getInstance().getTime() ) );
				String s =
						String.format(	"Profile: %s\nHost: %s:%s\nUser ID: %s",
										(getSelectedProfile() == 1)	? "Away"
																	: "Home",
										raprefs.getHost(), raprefs.getPort(),
										raprefs.getUserId() );
				pw.println( s );
				pw.println( msg );
				pw.println( t.toString() );
				pw.println( "Stack Trace:" );
				pw.flush();
				t.printStackTrace( pw );
				pw.println( "----" );
				pw.flush();
				pw.close();
			} catch ( IOException e ) {
				e.printStackTrace();
			}
		}
	}

	public String getErrorMessage ( ) {
		String s =
				(String) getResources().getText( R.string.messageUnknownError );
		// loop through array of error codes and match with the current code
		for ( int i = 0; i < errorCodes.length; i++ ) {
			if ( Integer.parseInt( errorCodes[i] ) == errorCode ) {
				// found code
				s =
						String.format(	Locale.getDefault(),
										"%s %d: %s",
										getResources()
												.getText( R.string.messageError ),
										errorCode, errorCodesStrings[i] );
				break;
			}
		}
		return s;
	}

	public boolean canErrorRetry ( ) {
		boolean f = false;
		if ( errorCount <= raprefs.getNotificationErrorRetryMax() ) {
			f = true;
		}
		return f;
	}

	public void clearErrorRetryCount ( ) {
		errorCount = Globals.errorRetryNone;
	}

	public String getLoggingDirectory ( ) {
		String s =
				"" + Environment.getExternalStorageDirectory()
						+ Environment.getDataDirectory() + "/"
						+ Globals.PACKAGE + "/";
		return s;
	}

	public String getLoggingFile ( ) {
		return getLoggingDirectory() + Globals.loggingFile;
	}

	public boolean isLoggingFilePresent ( ) {
		boolean f = false;
		File l = new File( getLoggingFile() );
		if ( (l != null) && (l.exists()) )
			f = true;
		return f;
	}

	public void deleteLoggingFile ( ) {
		File l = new File( getLoggingFile() );
		if ( l != null && l.exists() )
			l.delete();
	}

	public boolean hasExternalStorage ( ) {
		boolean f = false;
		File path = new File( getLoggingDirectory() );
		path.mkdirs();
		File file = new File( path, "test.txt" );
		file.mkdirs();
		if ( file != null ) {
			if ( file.exists() ) {
				f = true;
				file.delete();
			}
		}
		return f;
	}

	private boolean isNumber ( Object value ) {
		if ( (!value.toString().equals( "" ))
				&& (value.toString().matches( NUMBER_PATTERN )) ) {
			return true;
		}
		return false;
	}

	public boolean validateHost ( Object host ) {
		// host validation here
		Log.d( TAG, "Validate entered host" );
		String h = host.toString();

		// Hosts must:
		// - not start with 'http://'
		// - only contain: alpha, number, _, -, .
		// - end with: alpha or number

		if ( !h.matches( HOST_PATTERN ) ) {
			// invalid host
			Log.d( TAG, "Invalid host" );
			Toast.makeText( this,
							this.getString( R.string.prefHostInvalidHost )
									+ ": " + host.toString(),
							Toast.LENGTH_SHORT ).show();
			return false;
		}
		return true;
	}

	public boolean validatePort ( Object port ) {
		Log.d( TAG, "Validate entered port" );
		if ( !isNumber( port ) ) {
			// not a number
			Log.d( TAG, "Invalid port" );
			Toast.makeText( this,
							getString( R.string.messageNotNumber ) + ": "
									+ port.toString(), Toast.LENGTH_SHORT )
					.show();
			return false;
		} else {
			// it's a number, verify it's within range
			int min = Integer.parseInt( getString( R.string.prefPortMin ) );
			int max = Integer.parseInt( getString( R.string.prefPortMax ) );
			int v = Integer.parseInt( (String) port.toString() );

			// check if it's less than the min value or if it's greater than
			// the max value
			if ( (v < min) || (v > max) ) {
				Log.d( TAG, "Invalid port range" );
				Toast.makeText( this,
								getString( R.string.prefPortInvalidPort )
										+ ": " + port.toString(),
								Toast.LENGTH_SHORT ).show();
				return false;
			}
		}
		return true;
	}

	public boolean validateUser ( Object user ) {
		String u = user.toString();
		if ( !u.matches( USERID_PATTERN ) ) {
			// invalid userid
			Log.d( TAG, "Invalid userid" );
			Toast.makeText( this,
							getString( R.string.prefUserIdInvalid ) + ": "
									+ user.toString(), Toast.LENGTH_SHORT )
					.show();
			return false;
		}
		return true;
	}

	// Preferences

	public boolean isFirstRun ( ) {
		// First run will be determined by:
		// if the first run key is NOT set AND
		// if the host key is NOT set OR if it's the same as the default
		boolean fFirst = raprefs.isFirstRun();

		// if it's already set, no need to compare the hosts
		if ( !fFirst ) {
			// Log.w( TAG, "First run already set" );
			return false;
		}

		// if it's not set (as in existing installations), check the host
		// the host should be set and it should not be the same as the default
		if ( !raprefs.isMainHostSet() )
			return true;

		// if we have made it here, then it's an existing install where the user
		// has the host set to something other than the default
		// so we will go ahead and clear the first run prompt for them
		raprefs.disableFirstRun();
		return false;
	}

	public void displayChangeLog ( Activity a ) {
		// check version code stored in preferences vs the version stored in
		// running code
		// display the changelog if the values are different
		Log.d( TAG, "display changelog" );
		int previous = raprefs.getPreviousCodeVersion();

		int current = 0;
		try {
			current =
					getPackageManager().getPackageInfo( getPackageName(), 0 ).versionCode;
		} catch ( NameNotFoundException e ) {
		}
		if ( current > previous ) {
			// save code version in preferences
			raprefs.setPreviousCodeVersion( current );
			// newer version, display changelog
			Changelog.displayChangelog( a );
		}
		// deletePref( R.string.prefPreviousCodeVersion );
	}

	// Profiles
	public int getSelectedProfile ( ) {
		return raprefs.getSelectedProfile();
	}

	public void setSelectedProfile ( int profile ) {
		if ( profile > Globals.profileAway )
			return;
		raprefs.setSelectedProfile( profile );
		restartAutoUpdateService();
	}

	public boolean isAwayProfileEnabled ( ) {
		// String host = raprefs.getAwayHost();
		// Log.d( TAG, "isAwayProfileEnabled: " + host );
		// get away host, compare to empty host
		// if host is set, then the profile is enabled
		// if port is not set, that implies default port
		return raprefs.isAwayHostSet();
	}
}
