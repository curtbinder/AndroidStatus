/*
 * Copyright (c) 2011-2013 by Curt Binder (http://curtbinder.info)
 * 
 * This work is made available under the terms of the Creative Commons
 * Attribution-NonCommercial-ShareAlike 3.0 Unported License
 * http://creativecommons.org/licenses/by-nc-sa/3.0/
 */

package info.curtbinder.reefangel.phone;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.util.Log;

public class DeleteLogPreferenceListener implements OnPreferenceClickListener {
	private static final String TAG = DeleteLogPreferenceListener.class
			.getSimpleName();

	private Context ctx;
	private RAApplication rapp;

	public DeleteLogPreferenceListener ( Context context, RAApplication ra ) {
		this.ctx = context;
		this.rapp = ra;
	}

	@Override
	public boolean onPreferenceClick ( Preference preference ) {
		AlertDialog.Builder builder = new AlertDialog.Builder( ctx );
		builder.setMessage( rapp.getString( R.string.messageDeleteLogPrompt ) )
				.setCancelable( false )
				.setPositiveButton( rapp.getString( R.string.buttonYes ),
									new DialogInterface.OnClickListener() {
										public void onClick (
												DialogInterface dialog,
												int id ) {
											Log.d( TAG, "Delete log file" );
											dialog.dismiss();
											deleteLogFile();
										}
									} )
				.setNegativeButton( rapp.getString( R.string.buttonNo ),
									new DialogInterface.OnClickListener() {
										public void onClick (
												DialogInterface dialog,
												int id ) {
											Log.d( TAG, "Delete log cancelled" );
											dialog.cancel();
										}
									} );

		AlertDialog alert = builder.create();
		alert.show();
		return true;
	}

	@SuppressWarnings("deprecation")
	private void deleteLogFile ( ) {
		rapp.deleteLoggingFile();
		// disable deleting and sending of the log file if not present
		if ( !rapp.isLoggingFilePresent() ) {
			((PrefsActivity) ctx)
					.findPreference(	rapp.getString( R.string.prefLoggingDeleteKey ) )
					.setEnabled( false );
			((PrefsActivity) ctx)
					.findPreference(	rapp.getString( R.string.prefLoggingSendKey ) )
					.setEnabled( false );
		}
	}

}
