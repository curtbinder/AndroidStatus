/*
 * Copyright (c) 2011-2013 by Curt Binder (http://curtbinder.info)
 * 
 * This work is made available under the terms of the Creative Commons
 * Attribution-NonCommercial-ShareAlike 3.0 Unported License
 * http://creativecommons.org/licenses/by-nc-sa/3.0/
 */

package info.curtbinder.reefangel.phone;

import info.curtbinder.reefangel.db.RADbHelper;
import info.curtbinder.reefangel.service.MessageCommands;
import info.curtbinder.reefangel.service.UpdateService;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
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
	private static final String USERID_PATTERN = "[\\w\\-\\.\\x20]+";
	@SuppressWarnings("unused")
	private static final String WIFI_LOOKUP =
			"http://forum.reefangel.com/getwifi.php?id=%1$s&pwd=%2$s";

	// Preferences
	public RAPreferences raprefs;

	// Error code stuff
	private String[] errorCodes;
	private String[] errorCodesStrings;
	public int errorCode;
	public int errorCount;

	// Controller Data
	public RADbHelper data;

	public void onCreate ( ) {
		errorCodes = getResources().getStringArray( R.array.errorCodes );
		errorCodesStrings =
				getResources().getStringArray( R.array.errorCodesStrings );
		errorCode = 0; // set to no error initially
		data = new RADbHelper( this );
		raprefs = new RAPreferences( this );

		// initialize the error count
		errorCount = 0;
	}

	public void onTerminate ( ) {
		super.onTerminate();
		data.close();
	}

	public void restartAutoUpdateService ( ) {
		cancelAutoUpdateService();
		startAutoUpdateService();
	}

	public void cancelAutoUpdateService ( ) {
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

	// Error Logging
	public void error ( int errorCodeIndex, Throwable t, String msg ) {
		errorCode = Integer.parseInt( errorCodes[errorCodeIndex] );

		// if logging enabled, save the log
		if ( raprefs.isLoggingEnabled() ) {
			if ( !hasExternalStorage() ) {
				// doesn't have external storage
				Toast.makeText( this,
								getString( R.string.messageNoExternalStorage ),
								Toast.LENGTH_LONG ).show();
				return;
			}
			boolean keepFile = raprefs.isLoggingAppendFile();
			try {
				String sFile = getLoggingFile();
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
						String.format(	Locale.US,
										"%s %d: %s",
										getResources()
												.getText( R.string.messageError ),
										errorCode,
										errorCodesStrings[i] );
				break;
			}
		}
		return s;
	}

	public static String getFancyDate ( long when ) {
		DateFormat fmt =
				DateFormat.getDateTimeInstance( DateFormat.SHORT,
												DateFormat.SHORT,
												Locale.getDefault() );
		return fmt.format( new Date( when ) );
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

	public void increaseErrorCount ( ) {
		errorCount++;
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
		String h = host.toString();

		// Hosts must:
		// - not start with 'http://'
		// - only contain: alpha, number, _, -, .
		// - end with: alpha or number

		if ( !h.matches( HOST_PATTERN ) ) {
			// invalid host
			Toast.makeText( this,
							this.getString( R.string.prefHostInvalidHost )
									+ ": " + host.toString(),
							Toast.LENGTH_SHORT ).show();
			return false;
		}
		return true;
	}

	public boolean validatePort ( Object port ) {
		if ( !isNumber( port ) ) {
			// not a number
			Toast.makeText( this,
							getString( R.string.messageNotNumber ) + ": "
									+ port.toString(), Toast.LENGTH_SHORT )
					.show();
			return false;
		} else {
			// it's a number, verify it's within range
			// TODO: convert min & max ports to int value defines
			int min = Integer.parseInt( getString( R.string.prefPortMin ) );
			int max = Integer.parseInt( getString( R.string.prefPortMax ) );
			int v = Integer.parseInt( (String) port.toString() );

			// check if it's less than the min value or if it's greater than
			// the max value
			if ( (v < min) || (v > max) ) {
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
