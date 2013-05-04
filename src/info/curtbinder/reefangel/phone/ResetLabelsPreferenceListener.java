/*
 * Copyright (c) 2011-2013 by Curt Binder (http://curtbinder.info)
 * 
 * This work is made available under the terms of the Creative Commons
 * Attribution-NonCommercial-ShareAlike 3.0 Unported License
 * http://creativecommons.org/licenses/by-nc-sa/3.0/
 */

package info.curtbinder.reefangel.phone;

import info.curtbinder.reefangel.controller.Controller;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.util.Log;
import android.widget.Toast;

public class ResetLabelsPreferenceListener implements OnPreferenceClickListener {
	private static final String TAG = ResetLabelsPreferenceListener.class.getSimpleName();
	
	private Context ctx;
	private RAApplication rapp;
	
	public ResetLabelsPreferenceListener ( Context context, RAApplication ra ) {
		this.ctx = context;
		this.rapp = ra;
	}

	@Override
	public boolean onPreferenceClick ( Preference preference ) {
		AlertDialog.Builder builder =
				new AlertDialog.Builder( ctx );
		builder.setMessage( rapp.getString( R.string.messageResetLabelsPrompt ) )
				.setCancelable( false )
				.setPositiveButton( rapp.getString( R.string.buttonYes ),
									new DialogInterface.OnClickListener() {
										public void onClick (
												DialogInterface dialog,
												int id ) {
											Log.d( TAG, "Reset labels" );
											dialog.dismiss();
											resetLabels();
										}
									} )
				.setNegativeButton( rapp.getString( R.string.buttonNo ),
									new DialogInterface.OnClickListener() {
										public void onClick (
												DialogInterface dialog,
												int id ) {
											Log.d( TAG, "Cancel reset" );
											dialog.cancel();
										}
									} );

		AlertDialog alert = builder.create();
		alert.show();
		return true;
	}

	private void resetLabels ( ) {
		Log.d( TAG, "Deleting all labels" );
		// delete all controller labels
		rapp.deletePref( R.string.prefT1LabelKey );
		rapp.deletePref( R.string.prefT2LabelKey );
		rapp.deletePref( R.string.prefT3LabelKey );
		rapp.deletePref( R.string.prefAPLabelKey );
		rapp.deletePref( R.string.prefDPLabelKey );
		rapp.deletePref( R.string.prefPHLabelKey );
		rapp.deletePref( R.string.prefSalinityLabelKey );
		rapp.deletePref( R.string.prefORPLabelKey );
		rapp.deletePref( R.string.prefPHExpLabelKey );
		for ( int i = 0; i <= Controller.MAX_EXPANSION_RELAYS; i++ ) {
			for ( int j = 0; j < Controller.MAX_RELAY_PORTS; j++ ) {
				rapp.deletePref( rapp.getPrefRelayKey( i, j ) );
			}
		}
		rapp.deletePref( R.string.prefExpDimmingCh0LabelKey );
		rapp.deletePref( R.string.prefExpDimmingCh1LabelKey );
		rapp.deletePref( R.string.prefExpDimmingCh2LabelKey );
		rapp.deletePref( R.string.prefExpDimmingCh3LabelKey );
		rapp.deletePref( R.string.prefExpDimmingCh4LabelKey );
		rapp.deletePref( R.string.prefExpDimmingCh5LabelKey );
		rapp.deletePref( R.string.prefExpIO0LabelKey );
		rapp.deletePref( R.string.prefExpIO1LabelKey );
		rapp.deletePref( R.string.prefExpIO2LabelKey );
		rapp.deletePref( R.string.prefExpIO3LabelKey );
		rapp.deletePref( R.string.prefExpIO4LabelKey );
		rapp.deletePref( R.string.prefExpIO5LabelKey );
		rapp.deletePref( R.string.prefExpCustom0LabelKey );
		rapp.deletePref( R.string.prefExpCustom1LabelKey );
		rapp.deletePref( R.string.prefExpCustom2LabelKey );
		rapp.deletePref( R.string.prefExpCustom3LabelKey );
		rapp.deletePref( R.string.prefExpCustom4LabelKey );
		rapp.deletePref( R.string.prefExpCustom5LabelKey );
		rapp.deletePref( R.string.prefExpCustom6LabelKey );
		rapp.deletePref( R.string.prefExpCustom7LabelKey );

		Toast.makeText( ctx,
						rapp.getString( R.string.messageResetLabelsComplete ),
						Toast.LENGTH_SHORT ).show();
	}
	
}
