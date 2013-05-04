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
import android.content.Intent;
import android.net.Uri;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.util.Log;

public class SendEmailPreferenceListener implements OnPreferenceClickListener {
	private static final String TAG = SendEmailPreferenceListener.class
			.getSimpleName();

	private Context ctx;
	private RAApplication rapp;

	public SendEmailPreferenceListener ( Context context, RAApplication ra ) {
		this.ctx = context;
		this.rapp = ra;
	}

	@Override
	public boolean onPreferenceClick ( Preference preference ) {
		AlertDialog.Builder builder = new AlertDialog.Builder( ctx );
		builder.setMessage( rapp.getString( R.string.messageSendLogPrompt ) )
				.setCancelable( false )
				.setPositiveButton( rapp.getString( R.string.buttonYes ),
									new DialogInterface.OnClickListener() {
										public void onClick (
												DialogInterface dialog,
												int id ) {
											Log.d( TAG, "Send file" );
											dialog.dismiss();
											sendEmail();
										}
									} )
				.setNegativeButton( rapp.getString( R.string.buttonNo ),
									new DialogInterface.OnClickListener() {
										public void onClick (
												DialogInterface dialog,
												int id ) {
											Log.d( TAG, "Send cancelled" );
											dialog.cancel();
										}
									} );

		AlertDialog alert = builder.create();
		alert.show();
		return true;
	}

	private void sendEmail ( ) {
		Intent email = new Intent( Intent.ACTION_SEND );
		email.putExtra( Intent.EXTRA_EMAIL,
						new String[] { "android@curtbinder.info" } );
		email.putExtra( Intent.EXTRA_SUBJECT, "Status Logfile" );
		email.setType( "text/plain" );
		email.putExtra( Intent.EXTRA_TEXT, "Logfile from my session." );
		Log.d( TAG, "Logfile: " + Uri.parse( "file://" + rapp.getLoggingFile() ) );
		email.putExtra( Intent.EXTRA_STREAM,
						Uri.parse( "file://" + rapp.getLoggingFile() ) );
		((PrefsActivity) ctx).startActivity( Intent
				.createChooser( email, "Send email..." ) );
	}
}
