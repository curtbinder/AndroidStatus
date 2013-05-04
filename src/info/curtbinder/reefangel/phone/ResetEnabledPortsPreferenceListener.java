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
import android.widget.Toast;

public class ResetEnabledPortsPreferenceListener implements
		OnPreferenceClickListener {
	private static final String TAG = ResetEnabledPortsPreferenceListener.class
			.getSimpleName();

	private Context ctx;
	private RAApplication rapp;

	public ResetEnabledPortsPreferenceListener ( Context context,
													RAApplication ra ) {
		this.ctx = context;
		this.rapp = ra;
	}

	@Override
	public boolean onPreferenceClick ( Preference preference ) {
		AlertDialog.Builder builder = new AlertDialog.Builder( ctx );
		builder.setMessage( rapp.getString( R.string.messageResetEnabledPortsPrompt ) )
				.setCancelable( false )
				.setPositiveButton( rapp.getString( R.string.buttonYes ),
									new DialogInterface.OnClickListener() {
										public void onClick (
												DialogInterface dialog,
												int id ) {
											Log.d( TAG, "Reset enabled ports" );
											dialog.dismiss();
											resetEnabledPorts();
										}
									} )
				.setNegativeButton( rapp.getString( R.string.buttonNo ),
									new DialogInterface.OnClickListener() {
										public void onClick (
												DialogInterface dialog,
												int id ) {
											Log.d(	TAG,
													"Cancel reset enabled ports" );
											dialog.cancel();
										}
									} );

		AlertDialog alert = builder.create();
		alert.show();
		return true;
	}

	private void resetEnabledPorts ( ) {
		rapp.deleteRelayControlEnabledPorts();

		Toast.makeText( ctx,
						rapp.getString( R.string.messageResetEanbledPortsComplete ),
						Toast.LENGTH_SHORT ).show();
	}

}
