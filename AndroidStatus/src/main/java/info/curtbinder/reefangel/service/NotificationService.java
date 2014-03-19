/*
 * Copyright (c) 2011-2013 by Curt Binder (http://curtbinder.info)
 * 
 * This work is made available under the terms of the Creative Commons
 * Attribution-NonCommercial-ShareAlike 3.0 Unported License
 * http://creativecommons.org/licenses/by-nc-sa/3.0/
 */

package info.curtbinder.reefangel.service;

import info.curtbinder.reefangel.controller.Controller;
import info.curtbinder.reefangel.db.ErrorTable;
import info.curtbinder.reefangel.db.NotificationTable;
import info.curtbinder.reefangel.db.StatusProvider;
import info.curtbinder.reefangel.db.StatusTable;
import info.curtbinder.reefangel.phone.Globals;
import info.curtbinder.reefangel.phone.Permissions;
import info.curtbinder.reefangel.phone.R;
import info.curtbinder.reefangel.phone.RAApplication;
import info.curtbinder.reefangel.phone.MainActivity;

import java.util.Locale;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

// TODO update notification service
public class NotificationService extends IntentService {

	private static final String TAG = NotificationService.class.getSimpleName();

	private static RAApplication rapp;
	private String errorMessage;
	private String paramPrecision;
	private String[] parameters;

	public NotificationService () {
		super( TAG );
	}

	@Override
	protected void onHandleIntent ( Intent intent ) {
		rapp = (RAApplication) getApplication();
		String action = intent.getAction();
		if ( action.equals( MessageCommands.NOTIFICATION_CLEAR_INTENT ) ) {
			clearNotifications();

		} else if ( action.equals( MessageCommands.NOTIFICATION_LAUNCH_INTENT ) ) {
			clearNotifications();
			Intent i = getStatusActivity();
			getApplication().startActivity( i );
		} else if ( action.equals( MessageCommands.NOTIFICATION_INTENT ) ) {
			processNotifications();
		} else if ( action.equals( MessageCommands.NOTIFICATION_ERROR_INTENT ) ) {
			processError();
		}
	}

	private void processError ( ) {
		boolean fDisplayUpdate = true;
		if ( rapp.raprefs.isNotificationEnabled() ) {
			// Only proceed if notifications are enabled

			// check if can retry on errors
			if ( rapp.raprefs.isErrorRetryEnabled() ) {
				// increase error count as soon as we know it's an error
				rapp.increaseErrorCount();

				// we are to retry connection before displaying an error
				if ( rapp.canErrorRetry() ) {
					// if error count is less than the max,
					// we need to retry the communication
					String s =
							rapp.getString( R.string.messageErrorRetry,
											rapp.errorCount );
					broadcastUpdateStatus( s );
					fDisplayUpdate = false;
					try {
						Thread.sleep( rapp.raprefs
								.getNotificationErrorRetryInterval() );
					} catch ( InterruptedException e ) {
					}
					Intent i = new Intent( rapp, UpdateService.class );
					i.setAction( MessageCommands.QUERY_STATUS_INTENT );
					startService( i );
				}
				// otherwise if we have exceeded the max count, then we
				// display the error
			}

		}
		if ( fDisplayUpdate ) {
			// log the error in the error table
			String errorMessage = rapp.getErrorMessage();
			insertErrorMessage( errorMessage );

			// notify the user
			notifyUser();

			// update the app text
			broadcastUpdateStatus( errorMessage );
		}
	}

	private void broadcastUpdateStatus ( String status ) {
		Intent i = new Intent( MessageCommands.UPDATE_STATUS_INTENT );
		i.putExtra( MessageCommands.UPDATE_STATUS_ID, -1 );
		i.putExtra( MessageCommands.UPDATE_STATUS_STRING, status );
		rapp.sendBroadcast( i, Permissions.QUERY_STATUS );
	}

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
			int param = 0, cond = 0;
			float value = (float) 0.0;
			parameters =
					rapp.getResources()
							.getStringArray( R.array.deviceParameters );
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
					insertErrorMessage( errorMessage );
					notifyCount++;
				}
			} while ( c.moveToNext() );
			l.close();
		} // else no notifications
		c.close();

		// launch the notification after we process the messages
		if ( notifyCount > 0 ) {
			notifyUser();
		}
	}

	private boolean isNotifyTriggered (
			int param,
			int cond,
			float rvalue,
			Cursor latest ) {
		boolean fRet = false;
		float lvalue = getLeftValue( param, latest );
		String condLabel = "";
		switch ( cond ) {
			case Globals.condEqual: {
				if ( lvalue == rvalue ) {
					condLabel = "=";
					fRet = true;
				}
				break;
			}
			case Globals.condGreaterThan: {
				if ( lvalue > rvalue ) {
					condLabel = ">";
					fRet = true;
				}
				break;
			}
			case Globals.condGreaterThanOrEqualTo: {
				if ( lvalue >= rvalue ) {
					condLabel = ">=";
					fRet = true;
				}
				break;
			}
			case Globals.condLessThan: {
				if ( lvalue < rvalue ) {
					condLabel = "<";
					fRet = true;
				}
				break;
			}
			case Globals.condLessThanOrEqualTo: {
				if ( lvalue <= rvalue ) {
					condLabel = "<=";
					fRet = true;
				}
				break;
			}
			case Globals.condNotEqual: {
				if ( lvalue != rvalue ) {
					condLabel = "!=";
					fRet = true;
				}
				break;
			}
		}
		if ( fRet ) {
			String formatString =
					String.format(	Locale.US, "%%s: %s %s %s", paramPrecision,
									condLabel, paramPrecision );
			errorMessage =
					String.format(	Locale.US, formatString, parameters[param],
									lvalue, rvalue );
		}
		return fRet;
	}

	private float getLeftValue ( int id, Cursor l ) {
		float f;
		paramPrecision = "%.0f";
		switch ( id ) {
			case Globals.paramT1: {
				f = l.getFloat( l.getColumnIndex( StatusTable.COL_T1 ) );
				paramPrecision = "%.1f";
				break;
			}
			case Globals.paramT2: {
				f = l.getFloat( l.getColumnIndex( StatusTable.COL_T2 ) );
				paramPrecision = "%.1f";
				break;
			}
			case Globals.paramT3: {
				f = l.getFloat( l.getColumnIndex( StatusTable.COL_T3 ) );
				paramPrecision = "%.1f";
				break;
			}
			case Globals.paramPH: {
				f = l.getFloat( l.getColumnIndex( StatusTable.COL_PH ) );
				paramPrecision = "%.2f";
				break;
			}
			case Globals.paramPHExpansion: {
				f = l.getFloat( l.getColumnIndex( StatusTable.COL_PHE ) );
				paramPrecision = "%.2f";
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
				paramPrecision = "%.1f";
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

	private PendingIntent getNotificationLaunchIntent ( boolean fClearOnly ) {
		// Create notification intent
		// Will launch the service to clear the notifications
		// and launch the main activity unless clear only is set
		Intent i = new Intent( this, NotificationService.class );
		if ( fClearOnly ) {
			i.setAction( MessageCommands.NOTIFICATION_CLEAR_INTENT );
		} else {
			i.setAction( MessageCommands.NOTIFICATION_LAUNCH_INTENT );
		}
		PendingIntent pi = PendingIntent.getService( this, -1, i, 0 );
		return pi;
	}

	private NotificationCompat.Builder buildNormalNotification (
			String msg,
			long when,
			int count ) {
		Bitmap icon =
				BitmapFactory.decodeResource(	getResources(),
												R.drawable.ic_launcher );
        // TODO update the small notification icon
		NotificationCompat.Builder b =
				new NotificationCompat.Builder( this )
						.setAutoCancel( true )
						.setSmallIcon( R.drawable.st_notify )
						.setLargeIcon( icon )
						.setContentTitle( getString( R.string.app_name ) )
						.setContentText( msg )
						.setTicker( msg )
						.setWhen( when )
						.setSound( rapp.raprefs.getNotificationSound() )
						.setDeleteIntent( getNotificationLaunchIntent( true ) )
						.setContentIntent( getNotificationLaunchIntent( false ) );
		if ( count > 1 ) {
			b.setNumber( count );
			if ( Build.VERSION.SDK_INT <= Build.VERSION_CODES.GINGERBREAD_MR1 ) {
				String msgGB =
						String.format(	Locale.US,
										getString( R.string.messageGBMoreErrorss ),
										msg, count );
				b.setContentText( msgGB );
			}
		}
		return b;
	}

	private String getInboxStyleMessage ( String msg, long when ) {
		String extraMessage =
				String.format(	Locale.getDefault(), "%s - %s", msg,
								RAApplication.getFancyDate( when ) );
		return extraMessage;
	}

	public void notifyUser ( ) {
		Uri uri =
				Uri.parse( StatusProvider.CONTENT_URI + "/"
							+ StatusProvider.PATH_ERROR );
		Cursor c =
				getContentResolver().query( uri, null,
											ErrorTable.COL_READ + "=?",
											new String[] { "0" },
											ErrorTable.COL_ID + " DESC" );

		String firstMessage = null;
		long firstWhen = 0;
		int numCount = 0;
		String[] summaryLines = new String[5];
		String summaryText = "";
		if ( c.moveToFirst() ) {
			int msgIndex = c.getColumnIndex( ErrorTable.COL_MESSAGE );
			int whenIndex = c.getColumnIndex( ErrorTable.COL_TIME );
			// grab the most recent error first
			firstMessage = c.getString( msgIndex );
			firstWhen = c.getLong( whenIndex );
			numCount = c.getCount();
			// handle looping through the rest of the messages
			// in order to create the big notification
			// InboxStyle only allows for up to 5 lines
			int extraCount = 1;
			summaryLines[0] = getInboxStyleMessage( firstMessage, firstWhen );
			while ( c.moveToNext() && extraCount < 5 ) {
				summaryLines[extraCount] =
						getInboxStyleMessage(	c.getString( msgIndex ),
												c.getLong( whenIndex ) );
				extraCount++;
			}
			// when multiple items shown, the first item is the content title
			// so a max of items can be shown (content title plus 5 extra
			// lines)
			if ( extraCount < numCount ) {
				summaryText =
						String.format(	Locale.US,
										getString( R.string.messageMoreErrors ),
										numCount - extraCount );
			}
		}
		c.close();

		NotificationManager nm =
				(NotificationManager) getSystemService( Context.NOTIFICATION_SERVICE );
		int mNotificationId = 001;

		NotificationCompat.Builder normal =
				buildNormalNotification( firstMessage, firstWhen, numCount );
		if ( numCount > 1 ) {
			NotificationCompat.InboxStyle inbox =
					new NotificationCompat.InboxStyle( normal );
			// inbox.setBigContentTitle( firstMessage );
			for ( String s : summaryLines ) {
				inbox.addLine( s );
			}
			inbox.setSummaryText( summaryText );
			nm.notify( mNotificationId, inbox.build() );
		} else {
			nm.notify( mNotificationId, normal.build() );
		}
	}

	public void insertErrorMessage ( String msg ) {
		// inserts the given error message into the database
		// message is the parameter for expandability with notifications
		ContentValues v = new ContentValues();
		v.put( ErrorTable.COL_TIME, System.currentTimeMillis() );
		v.put( ErrorTable.COL_MESSAGE, msg );
		v.put( ErrorTable.COL_READ, false );
		getContentResolver()
				.insert(	Uri.parse( StatusProvider.CONTENT_URI + "/"
										+ StatusProvider.PATH_ERROR ), v );
	}

	private Intent getStatusActivity ( ) {
		Intent si = new Intent( this, MainActivity.class );
		si.addFlags( Intent.FLAG_ACTIVITY_NEW_TASK );
		si.addFlags( Intent.FLAG_ACTIVITY_SINGLE_TOP );
		si.addFlags( Intent.FLAG_ACTIVITY_REORDER_TO_FRONT );
		return si;
	}

	private void clearNotifications ( ) {
		Uri uri =
				Uri.parse( StatusProvider.CONTENT_URI + "/"
							+ StatusProvider.PATH_ERROR );
		ContentValues v = new ContentValues();
		v.put( ErrorTable.COL_READ, true );
		// ignore the number of rows updated
		getContentResolver().update( uri, v, ErrorTable.COL_READ + "=?",
										new String[] { "0" } );
	}
}
