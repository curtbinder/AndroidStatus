/*
 * Copyright (c) 2011-2013 by Curt Binder (http://curtbinder.info)
 * 
 * This work is made available under the terms of the Creative Commons
 * Attribution-NonCommercial-ShareAlike 3.0 Unported License
 * http://creativecommons.org/licenses/by-nc-sa/3.0/
 */

package info.curtbinder.reefangel.service;

import info.curtbinder.reefangel.controller.Controller;
import info.curtbinder.reefangel.db.NotificationTable;
import info.curtbinder.reefangel.db.StatusProvider;
import info.curtbinder.reefangel.db.StatusTable;
import info.curtbinder.reefangel.phone.Globals;
import info.curtbinder.reefangel.phone.R;
import info.curtbinder.reefangel.phone.RAApplication;

import java.util.Locale;

import android.app.IntentService;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

public class ParameterNotificationService extends IntentService {

	public static final String LATEST = "latest-data";
	private static final String TAG = ParameterNotificationService.class
			.getSimpleName();

	private static RAApplication rapp;
	private String errorMessage;
	private String[] parameters;

	public ParameterNotificationService () {
		super( TAG );

	}

	@Override
	protected void onHandleIntent ( Intent intent ) {
		rapp = (RAApplication) getApplication();
		processNotifications();
	}

	// check the notifications
	// create notifications for each "error" by calling
	// rapp.insertErrorMessage( msg )

	// get a list of the notifications
	// loop through them and compare the most recent values to the
	// notifications

	private void processNotifications ( ) {
		Uri uri =
				Uri.parse( StatusProvider.CONTENT_URI + "/"
							+ StatusProvider.PATH_NOTIFICATION );
		// get all notifications
		Cursor c =
				getContentResolver().query( uri, null, null, null,
											NotificationTable.COL_ID + " ASC" );
		int notifyCount = 0;
		if ( c.moveToFirst() ) {
			int count = c.getCount();
			int param = 0, cond = 0;
			float value = (float) 0.0;
			parameters = rapp.getResources().getStringArray( R.array.deviceParameters );
			Log.d( TAG, "Have " + count + " notifications to check" );
			// grab the latest parameters to compare against
			Uri uriLatest =
					Uri.parse( StatusProvider.CONTENT_URI + "/"
								+ StatusProvider.PATH_LATEST );
			// get all notifications
			Cursor l =
					getContentResolver().query( uriLatest, null, null, null,
												StatusTable.COL_ID + " DESC" );
			l.moveToFirst();
			do {
				param =
						c.getInt( c
								.getColumnIndex( NotificationTable.COL_PARAM ) );
				cond =
						c.getInt( c
								.getColumnIndex( NotificationTable.COL_CONDITION ) );
				value =
						c.getFloat( c
								.getColumnIndex( NotificationTable.COL_VALUE ) );
				errorMessage = "";
				if ( isNotifyTriggered( param, cond, value, l ) ) {
					// notification triggered
					// add to list of notifications
					rapp.insertErrorMessage( errorMessage );
					notifyCount++;
				}
			} while ( c.moveToNext() );
			l.close();
		} // else no notifications
		c.close();

		// launch the notification after we process the messages
		if ( notifyCount > 0 ) {
			rapp.notifyUser();
		}
	}

	private boolean isNotifyTriggered (
			int param,
			int cond,
			float rvalue,
			Cursor latest ) {
		boolean fRet = false;
		// Log.d(TAG, param + ", " + cond + ", " + value );
		float lvalue = getLeftValue( param, latest );
		switch ( cond ) {
			case Globals.condEqual: {
				Log.d( TAG, lvalue + " == " + rvalue );
				if ( lvalue == rvalue ) {
					errorMessage =
							String.format(	Locale.US, "%s: %.2f == %.2f", parameters[param], lvalue,
											rvalue );
					fRet = true;
				}
				break;
			}
			case Globals.condGreaterThan: {
				Log.d( TAG, lvalue + " > " + rvalue );
				if ( lvalue > rvalue ) {
					errorMessage =
							String.format(	Locale.US, "%s: %.2f > %.2f", parameters[param], lvalue,
											rvalue );
					fRet = true;
				}
				break;
			}
			case Globals.condGreaterThanOrEqualTo: {
				Log.d( TAG, lvalue + " >= " + rvalue );
				if ( lvalue >= rvalue ) {
					errorMessage =
							String.format(	Locale.US, "%s: %.2f >= %.2f", parameters[param], lvalue,
											rvalue );
					fRet = true;
				}
				break;
			}
			case Globals.condLessThan: {
				Log.d( TAG, lvalue + " < " + rvalue );
				if ( lvalue < rvalue ) {
					errorMessage =
							String.format(	Locale.US, "%s: %.2f < %.2f", parameters[param], lvalue,
											rvalue );
					fRet = true;
				}
				break;
			}
			case Globals.condLessThanOrEqualTo: {
				Log.d( TAG, lvalue + " <= " + rvalue );
				if ( lvalue <= rvalue ) {
					errorMessage =
							String.format(	Locale.US, "%s: %.2f <= %.2f", parameters[param], lvalue,
											rvalue );
					fRet = true;
				}
				break;
			}
			case Globals.condNotEqual: {
				Log.d( TAG, lvalue + " != " + rvalue );
				if ( lvalue != rvalue ) {
					errorMessage =
							String.format(	Locale.US, "%s: %.2f != %.2f", parameters[param], lvalue,
											rvalue );
					fRet = true;
				}
				break;
			}
		}
		return fRet;
	}

	private float getLeftValue ( int id, Cursor l ) {
		float f;
		switch ( id ) {
			case Globals.paramT1: {
				f = l.getFloat( l.getColumnIndex( StatusTable.COL_T1 ) );
				break;
			}
			case Globals.paramT2: {
				f = l.getFloat( l.getColumnIndex( StatusTable.COL_T2 ) );
				break;
			}
			case Globals.paramT3: {
				f = l.getFloat( l.getColumnIndex( StatusTable.COL_T3 ) );
				break;
			}
			case Globals.paramPH: {
				f = l.getFloat( l.getColumnIndex( StatusTable.COL_PH ) );
				break;
			}
			case Globals.paramPHExpansion: {
				f = l.getFloat( l.getColumnIndex( StatusTable.COL_PHE ) );
				break;
			}
			case Globals.paramDaylightPWM: {
				f = l.getFloat( l.getColumnIndex( StatusTable.COL_DP ) );
				break;
			}
			case Globals.paramActinicPWM: {
				f = l.getFloat( l.getColumnIndex( StatusTable.COL_AP ) );
				break;
			}
			case Globals.paramSalinity: {
				f = l.getFloat( l.getColumnIndex( StatusTable.COL_SAL ) );
				break;
			}
			case Globals.paramORP: {
				f = l.getFloat( l.getColumnIndex( StatusTable.COL_ORP ) );
				break;
			}
			case Globals.paramWaterLevel: {
				f = l.getFloat( l.getColumnIndex( StatusTable.COL_WL ) );
				break;
			}
			case Globals.paramATOHigh: {
				f = l.getFloat( l.getColumnIndex( StatusTable.COL_ATOHI ) );
				break;
			}
			case Globals.paramATOLow: {
				f = l.getFloat( l.getColumnIndex( StatusTable.COL_ATOLO ) );
				break;
			}
			case Globals.paramPWMExp0: {
				f = l.getFloat( l.getColumnIndex( StatusTable.COL_PWME0 ) );
				break;
			}
			case Globals.paramPWMExp1: {
				f = l.getFloat( l.getColumnIndex( StatusTable.COL_PWME1 ) );
				break;
			}
			case Globals.paramPWMExp2: {
				f = l.getFloat( l.getColumnIndex( StatusTable.COL_PWME2 ) );
				break;
			}
			case Globals.paramPWMExp3: {
				f = l.getFloat( l.getColumnIndex( StatusTable.COL_PWME3 ) );
				break;
			}
			case Globals.paramPWMExp4: {
				f = l.getFloat( l.getColumnIndex( StatusTable.COL_PWME4 ) );
				break;
			}
			case Globals.paramPWMExp5: {
				f = l.getFloat( l.getColumnIndex( StatusTable.COL_PWME5 ) );
				break;
			}
			case Globals.paramAIWhite: {
				f = l.getFloat( l.getColumnIndex( StatusTable.COL_AIW ) );
				break;
			}
			case Globals.paramAIBlue: {
				f = l.getFloat( l.getColumnIndex( StatusTable.COL_AIB ) );
				break;
			}
			case Globals.paramAIRoyalBlue: {
				f = l.getFloat( l.getColumnIndex( StatusTable.COL_AIRB ) );
				break;
			}
			case Globals.paramVortechMode: {
				f = l.getFloat( l.getColumnIndex( StatusTable.COL_RFM ) );
				break;
			}
			case Globals.paramVortechSpeed: {
				f = l.getFloat( l.getColumnIndex( StatusTable.COL_RFS ) );
				break;
			}
			case Globals.paramVortechDuration: {
				f = l.getFloat( l.getColumnIndex( StatusTable.COL_RFD ) );
				break;
			}
			case Globals.paramRadionWhite: {
				f = l.getFloat( l.getColumnIndex( StatusTable.COL_RFW ) );
				break;
			}
			case Globals.paramRadionRoyalBlue: {
				f = l.getFloat( l.getColumnIndex( StatusTable.COL_RFRB ) );
				break;
			}
			case Globals.paramRadionRed: {
				f = l.getFloat( l.getColumnIndex( StatusTable.COL_RFR ) );
				break;
			}
			case Globals.paramRadionGreen: {
				f = l.getFloat( l.getColumnIndex( StatusTable.COL_RFG ) );
				break;
			}
			case Globals.paramRadionBlue: {
				f = l.getFloat( l.getColumnIndex( StatusTable.COL_RFB ) );
				break;
			}
			case Globals.paramRadionIntensity: {
				f = l.getFloat( l.getColumnIndex( StatusTable.COL_RFI ) );
				break;
			}
			case Globals.paramIOCh0:
			case Globals.paramIOCh1:
			case Globals.paramIOCh2:
			case Globals.paramIOCh3:
			case Globals.paramIOCh4:
			case Globals.paramIOCh5: {
				short io = l.getShort( l.getColumnIndex( StatusTable.COL_IO ) );
				byte ch = (byte) (id - Globals.paramIOCh0);
				// getIOChannel returns TRUE if the value is 1
				// and FALSE if the value is 0
				if ( Controller.getIOChannel( io, ch ) ) {
					f = 1;
				} else {
					f = 0;
				}
				break;
			}
			case Globals.paramCustom0: {
				f = l.getFloat( l.getColumnIndex( StatusTable.COL_C0 ) );
				break;
			}
			case Globals.paramCustom1: {
				f = l.getFloat( l.getColumnIndex( StatusTable.COL_C1 ) );
				break;
			}
			case Globals.paramCustom2: {
				f = l.getFloat( l.getColumnIndex( StatusTable.COL_C2 ) );
				break;
			}
			case Globals.paramCustom3: {
				f = l.getFloat( l.getColumnIndex( StatusTable.COL_C3 ) );
				break;
			}
			case Globals.paramCustom4: {
				f = l.getFloat( l.getColumnIndex( StatusTable.COL_C4 ) );
				break;
			}
			case Globals.paramCustom5: {
				f = l.getFloat( l.getColumnIndex( StatusTable.COL_C5 ) );
				break;
			}
			case Globals.paramCustom6: {
				f = l.getFloat( l.getColumnIndex( StatusTable.COL_C6 ) );
				break;
			}
			case Globals.paramCustom7: {
				f = l.getFloat( l.getColumnIndex( StatusTable.COL_C7 ) );
				break;
			}
			default: {
				f = 0;
				break;
			}
		}
		return f;
	}

}
