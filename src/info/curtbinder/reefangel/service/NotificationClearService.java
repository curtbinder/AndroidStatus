/*
 * Copyright (c) 2011-2013 by Curt Binder (http://curtbinder.info)
 * 
 * This work is made available under the terms of the Creative Commons
 * Attribution-NonCommercial-ShareAlike 3.0 Unported License
 * http://creativecommons.org/licenses/by-nc-sa/3.0/
 */

package info.curtbinder.reefangel.service;

import info.curtbinder.reefangel.db.ErrorTable;
import info.curtbinder.reefangel.db.StatusProvider;
import info.curtbinder.reefangel.phone.StatusActivity;
import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

public class NotificationClearService extends IntentService {

	private static final String TAG = NotificationClearService.class
			.getSimpleName();

	public NotificationClearService () {
		super( TAG );
	}

	@Override
	protected void onHandleIntent ( Intent intent ) {
		clearNotifications();

		if ( intent.getAction()
				.equals( MessageCommands.NOTIFICATION_LAUNCH_INTENT ) ) {
			Log.d( TAG, "Launching status activity" );
			Intent i = getStatusActivity();
			getApplication().startActivity( i );
		}
	}

	private Intent getStatusActivity ( ) {
		Intent si = new Intent( this, StatusActivity.class );
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
