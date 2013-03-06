package info.curtbinder.reefangel.phone;

/*
 * Copyright (c) 2011-12 by Curt Binder (http://curtbinder.info)
 *
 * This work is made available under the terms of the 
 * Creative Commons Attribution-NonCommercial-ShareAlike 3.0 Unported License
 * http://creativecommons.org/licenses/by-nc-sa/3.0/
 */

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
import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

public class RAApplication extends Application {

	private static final String TAG = RAApplication.class.getSimpleName();
	private static final String NUMBER_PATTERN = "\\d+";
	private static final String HOST_PATTERN =
			"^(?i:[[0-9][a-z]]+)(?i:[\\w\\.\\-]*)(?i:[[0-9][a-z]]+)$";
	private static final String USERID_PATTERN = "[\\w\\-\\.]+";
	private SharedPreferences prefs;
	// Error code stuff
	private String[] errorCodes;
	private String[] errorCodesStrings;
	public int errorCode;
	public int errorCount;

	// Devices stuff
	private String[] devicesArray;

	// Controller Data
	public RAData data;

	// Relay labels
	private int[][] relayLabels;
	private int[] relayDefaultLabels;

	// Service Stuff
	public boolean isServiceRunning;

	public void onCreate ( ) {
		prefs = PreferenceManager.getDefaultSharedPreferences( this );
		errorCodes = getResources().getStringArray( R.array.errorCodes );
		errorCodesStrings =
				getResources().getStringArray( R.array.errorCodesStrings );
		errorCode = 0; // set to no error initially
		data = new RAData( this );
		devicesArray = getResources().getStringArray( R.array.devicesValues );
		isServiceRunning = false;

		// initialize the error count
		errorCount = 0;

		fillRelayLabels();

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
		long interval = getUpdateInterval();
		if ( interval == 0 ) {
			Log.d( TAG, "disabled autoupdate" );
			return;
		}

		int up = getUpdateProfile();
		if ( isCommunicateController() ) {
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
		int type;
		if ( isWakeupEnabled() )
			type = AlarmManager.RTC_WAKEUP;
		else
			type = AlarmManager.RTC;
		am.setInexactRepeating( type, System.currentTimeMillis(), interval, pi );
		// Profile, interval, wakeup
		String profile;
		switch ( up ) {
			default:
			case Globals.profileAlways:
				profile = "always";
				break;
			case Globals.profileOnlyAway:
				profile = "only away";
				break;
			case Globals.profileOnlyHome:
				profile = "only home";
				break;
		}
		String s =
				String.format(	"%s, %s m, %s", profile,
								getUpdateIntervalDisplay(),
								(type == AlarmManager.RTC_WAKEUP)	? "wakeup"
																	: "none" );
		Log.d( TAG, "started auto update: " + s );
	}

	private PendingIntent getUpdateIntent ( ) {
		Intent i = new Intent( this, UpdateService.class );
		i.setAction( MessageCommands.QUERY_STATUS_INTENT );
		i.putExtra( MessageCommands.AUTO_UPDATE_PROFILE_INT, getUpdateProfile() );
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
		if ( isLoggingEnabled() ) {
			if ( !hasExternalStorage() ) {
				// doesn't have external storage
				Log.d( TAG, "No external storage" );
				Toast.makeText( this,
								getString( R.string.messageNoExternalStorage ),
								Toast.LENGTH_LONG ).show();
				return;
			}
			boolean keepFile = isLoggingAppendFile();
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
										getPrefHost(), getPrefPort(),
										getPrefUserId() );
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
						String.format(	"%s %d: %s",
										getResources()
												.getText( R.string.messageError ),
										errorCode, errorCodesStrings[i] );
				break;
			}
		}
		return s;
	}

	public int getErrorRetryMax ( ) {
		return Integer
				.parseInt( prefs
						.getString( getString( R.string.prefNotificationErrorRetryKey ),
									"0" ) );
	}

	public boolean canErrorRetry ( ) {
		boolean f = false;
		if ( errorCount <= getErrorRetryMax() ) {
			f = true;
		}
		return f;
	}

	public boolean isErrorRetryEnabled ( ) {
		return (getErrorRetryMax() > Globals.errorRetryNone);
	}

	public void clearErrorRetryCount ( ) {
		errorCount = Globals.errorRetryNone;
	}

	public long getErrorRetryInterval ( ) {
		// time between error retries
		return Long
				.parseLong( prefs
						.getString( getString( R.string.prefNotificationErrorRetryIntervalKey ),
									getString( R.string.prefNotificationErrorRetryIntervalDefault ) ) );

	}

	private boolean isLoggingEnabled ( ) {
		return prefs.getBoolean(	getString( R.string.prefLoggingEnableKey ),
									false );
	}

	private boolean isLoggingAppendFile ( ) {
		int i = getLoggingUpdateValue();
		boolean f = false;
		if ( i == Globals.logAppend )
			f = true;
		return f;
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
	protected void fillRelayLabels ( ) {
		relayLabels =
				new int[][] {	{	R.string.prefMainPort1LabelKey,
									R.string.prefMainPort2LabelKey,
									R.string.prefMainPort3LabelKey,
									R.string.prefMainPort4LabelKey,
									R.string.prefMainPort5LabelKey,
									R.string.prefMainPort6LabelKey,
									R.string.prefMainPort7LabelKey,
									R.string.prefMainPort8LabelKey },
								{	R.string.prefExp1Port1LabelKey,
									R.string.prefExp1Port2LabelKey,
									R.string.prefExp1Port3LabelKey,
									R.string.prefExp1Port4LabelKey,
									R.string.prefExp1Port5LabelKey,
									R.string.prefExp1Port6LabelKey,
									R.string.prefExp1Port7LabelKey,
									R.string.prefExp1Port8LabelKey },
								{	R.string.prefExp2Port1LabelKey,
									R.string.prefExp2Port2LabelKey,
									R.string.prefExp2Port3LabelKey,
									R.string.prefExp2Port4LabelKey,
									R.string.prefExp2Port5LabelKey,
									R.string.prefExp2Port6LabelKey,
									R.string.prefExp2Port7LabelKey,
									R.string.prefExp2Port8LabelKey },
								{	R.string.prefExp3Port1LabelKey,
									R.string.prefExp3Port2LabelKey,
									R.string.prefExp3Port3LabelKey,
									R.string.prefExp3Port4LabelKey,
									R.string.prefExp3Port5LabelKey,
									R.string.prefExp3Port6LabelKey,
									R.string.prefExp3Port7LabelKey,
									R.string.prefExp3Port8LabelKey },
								{	R.string.prefExp4Port1LabelKey,
									R.string.prefExp4Port2LabelKey,
									R.string.prefExp4Port3LabelKey,
									R.string.prefExp4Port4LabelKey,
									R.string.prefExp4Port5LabelKey,
									R.string.prefExp4Port6LabelKey,
									R.string.prefExp4Port7LabelKey,
									R.string.prefExp4Port8LabelKey },
								{	R.string.prefExp5Port1LabelKey,
									R.string.prefExp5Port2LabelKey,
									R.string.prefExp5Port3LabelKey,
									R.string.prefExp5Port4LabelKey,
									R.string.prefExp5Port5LabelKey,
									R.string.prefExp5Port6LabelKey,
									R.string.prefExp5Port7LabelKey,
									R.string.prefExp5Port8LabelKey },
								{	R.string.prefExp6Port1LabelKey,
									R.string.prefExp6Port2LabelKey,
									R.string.prefExp6Port3LabelKey,
									R.string.prefExp6Port4LabelKey,
									R.string.prefExp6Port5LabelKey,
									R.string.prefExp6Port6LabelKey,
									R.string.prefExp6Port7LabelKey,
									R.string.prefExp6Port8LabelKey },
								{	R.string.prefExp7Port1LabelKey,
									R.string.prefExp7Port2LabelKey,
									R.string.prefExp7Port3LabelKey,
									R.string.prefExp7Port4LabelKey,
									R.string.prefExp7Port5LabelKey,
									R.string.prefExp7Port6LabelKey,
									R.string.prefExp7Port7LabelKey,
									R.string.prefExp7Port8LabelKey },
								{	R.string.prefExp8Port1LabelKey,
									R.string.prefExp8Port2LabelKey,
									R.string.prefExp8Port3LabelKey,
									R.string.prefExp8Port4LabelKey,
									R.string.prefExp8Port5LabelKey,
									R.string.prefExp8Port6LabelKey,
									R.string.prefExp8Port7LabelKey,
									R.string.prefExp8Port8LabelKey } };
		relayDefaultLabels =
				new int[] { R.string.labelPort1,
							R.string.labelPort2,
							R.string.labelPort3,
							R.string.labelPort4,
							R.string.labelPort5,
							R.string.labelPort6,
							R.string.labelPort7,
							R.string.labelPort8 };
	}

	public boolean isCommunicateController ( ) {
		boolean b = false;
		if ( getPrefDevice().equals( devicesArray[0] ) ) {
			b = true;
		}
		return b;
	}

	public boolean useOld085xExpansionRelays ( ) {
		return prefs.getBoolean( getString( R.string.prefExp085xKey ), false );
	}

	public boolean useOldPre10MemoryLocations ( ) {
		return prefs
				.getBoolean( getString( R.string.prefPre10MemoryKey ), true );
	}

	public long getUpdateInterval ( ) {
		String s =
				prefs.getString(	getString( R.string.prefAutoUpdateIntervalKey ),
									getString( R.string.prefAutoUpdateIntervalDefault ) );
		long i = Long.parseLong( s );
		return i;
	}

	public String getUpdateIntervalDisplay ( ) {
		int pos = 0;
		long value = getUpdateInterval();
		String[] interval =
				getResources().getStringArray( R.array.updateIntervalValues );
		String[] intervaldisplay =
				getResources().getStringArray( R.array.updateInterval );
		for ( int i = 0; i < interval.length; i++ ) {
			if ( Long.parseLong( interval[i] ) == value ) {
				// found value
				pos = i;
				break;
			}
		}
		return intervaldisplay[pos];
	}

	public boolean isWakeupEnabled ( ) {
		// TODO do we wake up the device to run the auto status updates or not?
		return false;
	}

	public int getUpdateProfile ( ) {
		String s =
				prefs.getString(	getString( R.string.prefAutoUpdateProfileKey ),
									getString( R.string.prefAutoUpdateProfileDefault ) );
		int i = Integer.parseInt( s );
		return i;
	}

	public String getUpdateProfileDisplay ( ) {
		int pos = 0;
		int value = getUpdateProfile();
		String[] profile =
				getResources().getStringArray( R.array.updateProfileValues );
		String[] profiledisplay =
				getResources().getStringArray( R.array.updateProfile );
		for ( int i = 0; i < profile.length; i++ ) {
			if ( Integer.parseInt( profile[i] ) == value ) {
				// found value
				pos = i;
				break;
			}
		}
		return profiledisplay[pos];
	}

	public int getLoggingUpdateValue ( ) {
		return Integer.parseInt( prefs
				.getString( getString( R.string.prefLoggingUpdateKey ), "0" ) );
	}

	public String getLoggingUpdateDisplay ( ) {
		int pos = 0;
		int value = getLoggingUpdateValue();

		String[] logging =
				getResources().getStringArray( R.array.loggingUpdateValues );
		String[] loggingdisplay =
				getResources().getStringArray( R.array.loggingUpdate );
		for ( int i = 0; i < logging.length; i++ ) {
			if ( Integer.parseInt( logging[i] ) == value ) {
				// found value
				pos = i;
				break;
			}
		}
		return loggingdisplay[pos];
	}

	public boolean isFirstRun ( ) {
		// First run will be determined by:
		// if the first run key is NOT set AND
		// if the host key is NOT set OR if it's the same as the default
		boolean fFirst =
				prefs.getBoolean( getString( R.string.prefFirstRunKey ), true );
		// if it's already set, no need to compare the hosts
		if ( !fFirst ) {
			Log.w( TAG, "First run already set" );
			return false;
		}

		// if it's not set (as in existing installations), check the host
		// the host should be set and it should not be the same as the default
		boolean fHost = true;
		String host = prefs.getString( getString( R.string.prefHostKey ), "" );
		if ( host.equals( "" ) )
			fHost = false;
		Log.w( TAG, "Host:  '" + host + "',  host set: " + fHost );
		if ( !fHost )
			return true;

		// if we have made it here, then it's an existing install where the user
		// has the host set to something other than the default
		// so we will go ahead and clear the first run prompt for them
		disableFirstRun();
		return false;
	}

	public void disableFirstRun ( ) {
		SharedPreferences.Editor editor = prefs.edit();
		editor.putBoolean( getString( R.string.prefFirstRunKey ), false );
		editor.commit();
	}

	protected void clearFirstRun ( ) {
		// TODO remove this function, not needed to clear first run key
		deletePref( R.string.prefFirstRunKey );
	}

	public void displayChangeLog ( Activity a ) {
		// check version code stored in preferences vs the version stored in
		// running code
		// display the changelog if the values are different
		Log.d( TAG, "display changelog" );
		int previous =
				prefs.getInt( getString( R.string.prefPreviousCodeVersion ), 0 );
		int current = 0;
		try {
			current =
					getPackageManager().getPackageInfo( getPackageName(), 0 ).versionCode;
		} catch ( NameNotFoundException e ) {
		}
		Log.d( TAG, "Compare: " + current + " == " + previous );
		if ( current > previous ) {
			// save code version in preferences
			prefs.edit()
					.putInt( getString( R.string.prefPreviousCodeVersion ),
								current ).commit();
			// newer version, display changelog
			Log.d( TAG, "Showing changelog" );
			Changelog.displayChangelog( a );
		}
		// deletePref( R.string.prefPreviousCodeVersion );
	}

	public int getSelectedProfile ( ) {
		return Integer.parseInt( prefs
				.getString( getString( R.string.prefProfileSelectedKey ),
							getString( R.string.prefProfileSelectedDefault ) ) );
	}

	public void setSelectedProfile ( int profile ) {
		if ( profile > Globals.profileAway )
			return;
		String s = "" + profile;
		Log.d( TAG, "Changed Profile: " + s );
		setPref( R.string.prefProfileSelectedKey, s );
		restartAutoUpdateService();
	}

	public boolean isAwayProfileEnabled ( ) {
		// get away host, compare to empty host
		// if host is set, then the profile is enabled
		// if port is not set, that implies default port
		String host = getPrefAwayHost();
		Log.d( TAG, "isAwayProfileEnabled: " + host );
		if ( host.equals( getString( R.string.prefHostAwayDefault ) ) ) {
			return false;
		}
		return true;
	}

	public int getPreviousEM ( ) {
		int previous = prefs.getInt( getString( R.string.prefPreviousEM ), -1 );
		return previous;
	}

	public void setPreviousEM ( short em ) {
		prefs.edit().putInt( getString( R.string.prefPreviousEM ), em )
				.commit();
	}

	public boolean isNotificationEnabled ( ) {
		return prefs
				.getBoolean(	getString( R.string.prefNotificationEnableKey ),
								true );
	}

	public Uri getNotificationSound ( ) {
		String s =
				prefs.getString(	getString( R.string.prefNotificationSoundKey ),
									"content://settings/system/notification_sound" );
		return Uri.parse( s );
	}

	public String getPrefHost ( ) {
		int profile = getSelectedProfile();
		if ( profile == 1 ) {
			// Away profile
			if ( isAwayProfileEnabled() ) {
				// away profile is filled in and enabled
				// return away profile
				return getPrefAwayHost();
			}
		}
		return getPrefHomeHost();
	}

	public String getPrefPort ( ) {
		int profile = getSelectedProfile();
		if ( profile == Globals.profileAway ) {
			// Away profile
			if ( isAwayProfileEnabled() ) {
				// away profile is filled in and enabled
				// return away profile
				return getPrefAwayPort();
			}
		}
		return getPrefHomePort();
	}

	public String getPrefHomeHost ( ) {
		return prefs.getString( getString( R.string.prefHostKey ),
								getString( R.string.prefHostHomeDefault ) );
	}

	public String getPrefHomePort ( ) {
		return prefs.getString( getString( R.string.prefPortKey ),
								getString( R.string.prefPortDefault ) );
	}

	public String getPrefAwayHost ( ) {
		return prefs.getString( getString( R.string.prefHostAwayKey ),
								getString( R.string.prefHostAwayDefault ) );
	}

	public String getPrefAwayPort ( ) {
		return prefs.getString( getString( R.string.prefPortAwayKey ),
								getString( R.string.prefPortDefault ) );
	}

	public boolean getPrefT2Visibility ( ) {
		return prefs.getBoolean(	getString( R.string.prefT2VisibilityKey ),
									true );
	}

	public boolean getPrefT3Visibility ( ) {
		return prefs.getBoolean(	getString( R.string.prefT3VisibilityKey ),
									true );
	}

	public boolean getPrefDPVisibility ( ) {
		return prefs.getBoolean(	getString( R.string.prefDPVisibilityKey ),
									true );
	}

	public boolean getPrefAPVisibility ( ) {
		return prefs.getBoolean(	getString( R.string.prefAPVisibilityKey ),
									true );
	}

	public boolean getPrefPHVisibility ( ) {
		return prefs.getBoolean(	getString( R.string.prefPHVisibilityKey ),
									true );
	}

	public boolean getPrefSalinityVisibility ( ) {
		return prefs
				.getBoolean(	getString( R.string.prefSalinityVisibilityKey ),
								false );
	}

	public boolean getPrefORPVisibility ( ) {
		return prefs.getBoolean(	getString( R.string.prefORPVisibilityKey ),
									false );
	}

	public boolean getPrefPHExpVisibility ( ) {
		return prefs
				.getBoolean(	getString( R.string.prefPHExpVisibilityKey ),
								false );
	}

	public boolean getPrefWaterLevelVisibility ( ) {
		return prefs
				.getBoolean(	getString( R.string.prefWaterLevelVisibilityKey ),
								false );
	}

	public String getPrefT1Label ( ) {
		return prefs.getString( getString( R.string.prefT1LabelKey ),
								getString( R.string.labelTemp1 ) );
	}

	public String getPrefT2Label ( ) {
		return prefs.getString( getString( R.string.prefT2LabelKey ),
								getString( R.string.labelTemp2 ) );
	}

	public String getPrefT3Label ( ) {
		return prefs.getString( getString( R.string.prefT3LabelKey ),
								getString( R.string.labelTemp3 ) );
	}

	public String getPrefPHLabel ( ) {
		return prefs.getString( getString( R.string.prefPHLabelKey ),
								getString( R.string.labelPH ) );
	}

	public String getPrefDPLabel ( ) {
		return prefs.getString( getString( R.string.prefDPLabelKey ),
								getString( R.string.labelDP ) );
	}

	public String getPrefAPLabel ( ) {
		return prefs.getString( getString( R.string.prefAPLabelKey ),
								getString( R.string.labelAP ) );
	}

	public String getPrefSalinityLabel ( ) {
		return prefs.getString( getString( R.string.prefSalinityLabelKey ),
								getString( R.string.labelSalinity ) );
	}

	public String getPrefORPLabel ( ) {
		return prefs.getString( getString( R.string.prefORPLabelKey ),
								getString( R.string.labelORP ) );
	}

	public String getPrefPHExpLabel ( ) {
		return prefs.getString( getString( R.string.prefPHExpLabelKey ),
								getString( R.string.labelPHExp ) );
	}

	public String getPrefWaterLevelLabel ( ) {
		return prefs.getString( getString( R.string.prefWaterLevelLabelKey ),
								getString( R.string.labelWaterLevel ) );
	}

	public String getPrefMainRelayLabel ( int port ) {
		return getPrefRelayLabel( 0, port );
	}

	public String getPrefRelayLabel ( int relay, int port ) {
		return prefs.getString( getString( relayLabels[relay][port] ),
								getString( relayDefaultLabels[port] ) );
	}

	public void setPrefRelayLabel ( int relay, int port, String label ) {
		setPref( getString( relayLabels[relay][port] ), label );
	}

	public int getPrefRelayKey ( int relay, int port ) {
		return relayLabels[relay][port];
	}

	public void setPref ( String key, String value ) {
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString( key, value );
		editor.commit();
	}

	public void setPref ( int keyid, String value ) {
		setPref( getString( keyid ), value );
	}

	public void setPref ( int keyid, boolean value ) {
		SharedPreferences.Editor editor = prefs.edit();
		editor.putBoolean( getString( keyid ), value ).commit();
	}

	public void deletePref ( int keyid ) {
		SharedPreferences.Editor editor = prefs.edit();
		editor.remove( getString( keyid ) );
		editor.commit();
	}

	public String getPrefDevice ( ) {
		return prefs.getString( getString( R.string.prefDeviceKey ),
								getString( R.string.prefDeviceDefault ) );
	}

	public String getPrefUserId ( ) {
		return prefs.getString( getString( R.string.prefUserIdKey ),
								getString( R.string.prefUserIdDefault ) );
	}

	public boolean isAutoUpdateModulesEnabled ( ) {
		return prefs
				.getBoolean(	getString( R.string.prefAutoUpdateModulesKey ),
								true );
	}

	public int getPrefExpansionRelayQuantity ( ) {
		return Integer.parseInt( prefs
				.getString( getString( R.string.prefExpQtyKey ), "0" ) );
	}

	public int getTotalInstalledModuleQuantity ( ) {
		// this function gets all the installed modules for the controller
		// that are displayed on their own separate pages
		// the modules include:
		// expansion relays, dimming, vortech, radion, ai, custom, io
		int total = 0;
		total += getPrefExpansionRelayQuantity();
		total += getInstalledModuleQuantity();
		return total;
	}

	public int getInstalledModuleQuantity ( ) {
		// returns the total installed modules
		int total = 0;
		if ( getDimmingModuleEnabled() )
			total++;
		if ( getRadionModuleEnabled() )
			total++;
		if ( getVortechModuleEnabled() )
			total++;
		if ( getAIModuleEnabled() )
			total++;
		if ( getIOModuleEnabled() )
			total++;
		if ( getCustomModuleEnabled() )
			total++;
		return total;
	}

	public boolean getDimmingModuleEnabled ( ) {
		return prefs
				.getBoolean(	getString( R.string.prefExpDimmingEnableKey ),
								false );
	}

	public String getDimmingModuleChannelLabel ( int channel ) {
		int k, v;
		switch ( channel ) {
			default:
			case 0:
				k = R.string.prefExpDimmingCh0LabelKey;
				v = R.string.prefExpDimmingCh0LabelTitle;
				break;
			case 1:
				k = R.string.prefExpDimmingCh1LabelKey;
				v = R.string.prefExpDimmingCh1LabelTitle;
				break;
			case 2:
				k = R.string.prefExpDimmingCh2LabelKey;
				v = R.string.prefExpDimmingCh2LabelTitle;
				break;
			case 3:
				k = R.string.prefExpDimmingCh3LabelKey;
				v = R.string.prefExpDimmingCh3LabelTitle;
				break;
			case 4:
				k = R.string.prefExpDimmingCh4LabelKey;
				v = R.string.prefExpDimmingCh4LabelTitle;
				break;
			case 5:
				k = R.string.prefExpDimmingCh5LabelKey;
				v = R.string.prefExpDimmingCh5LabelTitle;
				break;
		}
		return prefs.getString( getString( k ), getString( v ) );
	}

	public void setDimmingModuleChannelLabel ( int channel, String label ) {
		int k;
		switch ( channel ) {
			default:
			case 0:
				k = R.string.prefExpDimmingCh0LabelKey;
				break;
			case 1:
				k = R.string.prefExpDimmingCh1LabelKey;
				break;
			case 2:
				k = R.string.prefExpDimmingCh2LabelKey;
				break;
			case 3:
				k = R.string.prefExpDimmingCh3LabelKey;
				break;
			case 4:
				k = R.string.prefExpDimmingCh4LabelKey;
				break;
			case 5:
				k = R.string.prefExpDimmingCh5LabelKey;
				break;
		}
		setPref( k, label );
	}

	public boolean getRadionModuleEnabled ( ) {
		return prefs
				.getBoolean(	getString( R.string.prefExpRadionEnableKey ),
								false );
	}

	public boolean getVortechModuleEnabled ( ) {
		return prefs
				.getBoolean(	getString( R.string.prefExpVortechEnableKey ),
								false );
	}

	public boolean getAIModuleEnabled ( ) {
		return prefs.getBoolean(	getString( R.string.prefExpAIEnableKey ),
									false );
	}

	public boolean getIOModuleEnabled ( ) {
		return prefs.getBoolean(	getString( R.string.prefExpIOEnableKey ),
									false );
	}

	public String getIOModuleChannelLabel ( int channel ) {
		int k, v;
		switch ( channel ) {
			default:
			case 0:
				k = R.string.prefExpIO0LabelKey;
				v = R.string.prefExpIO0LabelTitle;
				break;
			case 1:
				k = R.string.prefExpIO1LabelKey;
				v = R.string.prefExpIO1LabelTitle;
				break;
			case 2:
				k = R.string.prefExpIO2LabelKey;
				v = R.string.prefExpIO2LabelTitle;
				break;
			case 3:
				k = R.string.prefExpIO3LabelKey;
				v = R.string.prefExpIO3LabelTitle;
				break;
			case 4:
				k = R.string.prefExpIO4LabelKey;
				v = R.string.prefExpIO4LabelTitle;
				break;
			case 5:
				k = R.string.prefExpIO5LabelKey;
				v = R.string.prefExpIO5LabelTitle;
				break;
		}
		return prefs.getString( getString( k ), getString( v ) );
	}

	public void setIOModuleChannelLabel ( int channel, String label ) {
		int k;
		switch ( channel ) {
			default:
			case 0:
				k = R.string.prefExpIO0LabelKey;
				break;
			case 1:
				k = R.string.prefExpIO1LabelKey;
				break;
			case 2:
				k = R.string.prefExpIO2LabelKey;
				break;
			case 3:
				k = R.string.prefExpIO3LabelKey;
				break;
			case 4:
				k = R.string.prefExpIO4LabelKey;
				break;
			case 5:
				k = R.string.prefExpIO5LabelKey;
				break;
		}
		setPref( k, label );
	}

	public boolean getCustomModuleEnabled ( ) {
		return prefs
				.getBoolean(	getString( R.string.prefExpCustomEnableKey ),
								false );
	}

	public String getCustomModuleChannelLabel ( int channel ) {
		int k, d;
		switch ( channel ) {
			default:
			case 0:
				k = R.string.prefExpCustom0LabelKey;
				d = R.string.prefExpCustom0LabelTitle;
				break;
			case 1:
				k = R.string.prefExpCustom1LabelKey;
				d = R.string.prefExpCustom1LabelTitle;
				break;
			case 2:
				k = R.string.prefExpCustom2LabelKey;
				d = R.string.prefExpCustom2LabelTitle;
				break;
			case 3:
				k = R.string.prefExpCustom3LabelKey;
				d = R.string.prefExpCustom3LabelTitle;
				break;
			case 4:
				k = R.string.prefExpCustom4LabelKey;
				d = R.string.prefExpCustom4LabelTitle;
				break;
			case 5:
				k = R.string.prefExpCustom5LabelKey;
				d = R.string.prefExpCustom5LabelTitle;
				break;
			case 6:
				k = R.string.prefExpCustom6LabelKey;
				d = R.string.prefExpCustom6LabelTitle;
				break;
			case 7:
				k = R.string.prefExpCustom7LabelKey;
				d = R.string.prefExpCustom7LabelTitle;
				break;
		}
		return prefs.getString( getString( k ), getString( d ) );
	}

	public void setCustomModuleChannelLabel ( int channel, String label ) {
		int k;
		switch ( channel ) {
			default:
			case 0:
				k = R.string.prefExpCustom0LabelKey;
				break;
			case 1:
				k = R.string.prefExpCustom1LabelKey;
				break;
			case 2:
				k = R.string.prefExpCustom2LabelKey;
				break;
			case 3:
				k = R.string.prefExpCustom3LabelKey;
				break;
			case 4:
				k = R.string.prefExpCustom4LabelKey;
				break;
			case 5:
				k = R.string.prefExpCustom5LabelKey;
				break;
			case 6:
				k = R.string.prefExpCustom6LabelKey;
				break;
			case 7:
				k = R.string.prefExpCustom7LabelKey;
				break;
		}
		setPref( k, label );
	}

	private String getRelayControlEnabledKey ( int relay, int port ) {
		String s;
		if ( relay == 0 ) {
			s = "prefMainPort";
		} else {
			s = "prefExp" + relay + "Port";
		}
		return s + (port + 1) + "EnabledKey";
	}

	public boolean getPrefRelayControlEnabled ( int relay, int port ) {
		return prefs
				.getBoolean( getRelayControlEnabledKey( relay, port ), true );
	}

	public boolean getPrefMainRelayControlEnabled ( int port ) {
		return getPrefRelayControlEnabled( 0, port );
	}

}
