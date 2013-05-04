/*
 * Copyright (c) 2011-13 by Curt Binder (http://curtbinder.info)
 *
 * This work is made available under the terms of the 
 * Creative Commons Attribution-NonCommercial-ShareAlike 3.0 Unported License
 * http://creativecommons.org/licenses/by-nc-sa/3.0/
 */

package info.curtbinder.reefangel.phone;

import info.curtbinder.reefangel.service.MessageCommands;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.util.Log;
import android.widget.Toast;

public class DownloadLabelsPreferenceListener implements OnPreferenceClickListener {

	private static final String TAG = DownloadLabelsPreferenceListener.class.getSimpleName();
	
	private Context ctx;
	private RAApplication rapp;
	
	public DownloadLabelsPreferenceListener ( Context context, RAApplication ra ) {
		this.ctx = context;
		this.rapp = ra;
	}
	
	@Override
	public boolean onPreferenceClick ( Preference preference ) {
		AlertDialog.Builder builder =
				new AlertDialog.Builder( ctx );
		builder.setMessage( rapp.getString( R.string.messageDownloadLabelsPrompt )
									+ " "
									+ rapp.getPrefUserId()
									+ "?" )
				.setCancelable( false )
				.setPositiveButton( rapp.getString( R.string.buttonYes ),
									new DialogInterface.OnClickListener() {
										public void onClick (
												DialogInterface dialog,
												int id ) {
											// launch download
											Log.d(	TAG,
													"Download labels" );
											Intent i =
													new Intent(
														MessageCommands.LABEL_QUERY_INTENT );
											rapp.sendBroadcast( i,
																Permissions.SEND_COMMAND );
											dialog.dismiss();
											Toast.makeText( ctx,
															rapp.getString( R.string.messageDownloadLabels ),
															Toast.LENGTH_SHORT )
													.show();
										}
									} )
				.setNegativeButton( rapp.getString( R.string.buttonNo ),
									new DialogInterface.OnClickListener() {
										public void onClick (
												DialogInterface dialog,
												int id ) {
											Log.d(	TAG,
													"Cancel download" );
											dialog.cancel();
										}
									} );

		AlertDialog alert = builder.create();
		alert.show();
		return true;
	}

}
