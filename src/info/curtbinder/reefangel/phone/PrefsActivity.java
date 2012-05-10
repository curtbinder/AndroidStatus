package info.curtbinder.reefangel.phone;

/*
 * Copyright (c) 2011-12 by Curt Binder (http://curtbinder.info)
 *
 * This work is made available under the terms of the 
 * Creative Commons Attribution-NonCommercial-ShareAlike 3.0 Unported License
 * http://creativecommons.org/licenses/by-nc-sa/3.0/
 */

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.util.Log;
import android.widget.Toast;

public class PrefsActivity extends PreferenceActivity implements
		OnPreferenceChangeListener {

	private static final String TAG = PrefsActivity.class.getSimpleName();

	private Preference portkey;
	private Preference hostkey;
	private Preference useridkey;
	private Preference downloadkey;
	private Preference explabelkey;
	private Preference[] explabels =
			new Preference[Controller.MAX_EXPANSION_RELAYS];

	RAApplication rapp;
	PrefsReceiver receiver;
	IntentFilter filter;

	@Override
	protected void onCreate ( Bundle savedInstanceState ) {
		super.onCreate( savedInstanceState );
		addPreferencesFromResource( R.xml.settings );
		rapp = (RAApplication) getApplication();

		receiver = new PrefsReceiver();
		filter = new IntentFilter( MessageCommands.LABEL_RESPONSE_INTENT );

		portkey =
				getPreferenceScreen()
						.findPreference( rapp.getString( R.string.prefPortKey ) );
		portkey.setOnPreferenceChangeListener( this );
		hostkey =
				getPreferenceScreen()
						.findPreference( rapp.getString( R.string.prefHostKey ) );
		hostkey.setOnPreferenceChangeListener( this );
		useridkey =
				getPreferenceScreen()
						.findPreference(	rapp.getString( R.string.prefUserIdKey ) );
		useridkey.setOnPreferenceChangeListener( this );
		Preference devicekey =
				getPreferenceScreen()
						.findPreference(	rapp.getString( R.string.prefDeviceKey ) );
		devicekey.setOnPreferenceChangeListener( this );

		Preference expqtykey =
				getPreferenceScreen()
						.findPreference(	rapp.getString( R.string.prefExpQtyKey ) );
		expqtykey.setOnPreferenceChangeListener( this );
		explabelkey =
				getPreferenceScreen()
						.findPreference(	rapp.getString( R.string.prefExpLabelsKey ) );
		explabels[0] =
				getPreferenceScreen()
						.findPreference(	rapp.getString( R.string.prefExp1RelayKey ) );
		explabels[1] =
				getPreferenceScreen()
						.findPreference(	rapp.getString( R.string.prefExp2RelayKey ) );
		explabels[2] =
				getPreferenceScreen()
						.findPreference(	rapp.getString( R.string.prefExp3RelayKey ) );
		explabels[3] =
				getPreferenceScreen()
						.findPreference(	rapp.getString( R.string.prefExp4RelayKey ) );
		explabels[4] =
				getPreferenceScreen()
						.findPreference(	rapp.getString( R.string.prefExp5RelayKey ) );
		explabels[5] =
				getPreferenceScreen()
						.findPreference(	rapp.getString( R.string.prefExp6RelayKey ) );
		explabels[6] =
				getPreferenceScreen()
						.findPreference(	rapp.getString( R.string.prefExp7RelayKey ) );
		explabels[7] =
				getPreferenceScreen()
						.findPreference(	rapp.getString( R.string.prefExp8RelayKey ) );
		updateExpansionLabelsVisibility( rapp.getPrefExpansionRelayQuantity() );

		Preference raWebsite =
				getPreferenceScreen()
						.findPreference(	rapp.getString( R.string.prefReefAngelKey ) );
		raWebsite
				.setOnPreferenceClickListener( new OnPreferenceClickListener() {

					@Override
					public boolean onPreferenceClick ( Preference preference ) {
						Intent browserIntent =
								new Intent(
									Intent.ACTION_VIEW,
									Uri.parse( rapp
											.getString( R.string.websiteReefangel ) ) );
						startActivity( browserIntent );
						return true;
					}
				} );
		Preference raForum =
				getPreferenceScreen()
						.findPreference( rapp.getString( R.string.prefForumKey ) );
		raForum.setOnPreferenceClickListener( new OnPreferenceClickListener() {

			@Override
			public boolean onPreferenceClick ( Preference preference ) {
				Intent browserIntent =
						new Intent( Intent.ACTION_VIEW, Uri.parse( rapp
								.getString( R.string.forumReefangel ) ) );
				startActivity( browserIntent );
				return true;
			}
		} );
		Preference license =
				getPreferenceScreen()
						.findPreference(	rapp.getString( R.string.prefLicenseKey ) );
		license.setOnPreferenceClickListener( new OnPreferenceClickListener() {

			@Override
			public boolean onPreferenceClick ( Preference preference ) {
				startActivity( new Intent( rapp, LicenseActivity.class ) );
				return true;
			}
		} );

		downloadkey =
				getPreferenceScreen()
						.findPreference(	rapp.getString( R.string.prefControllerLabelsDownloadKey ) );
		downloadkey
				.setOnPreferenceClickListener( new OnPreferenceClickListener() {
					@Override
					public boolean onPreferenceClick ( Preference preference ) {

						AlertDialog.Builder builder =
								new AlertDialog.Builder( PrefsActivity.this );
						builder.setMessage( "Download all labels for "
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
															Toast.makeText( PrefsActivity.this,
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
				} );
		updateDownloadLabelUserId( rapp.getPrefUserId() );
	}

	@Override
	protected void onPause ( ) {
		super.onPause();
		unregisterReceiver( receiver );
	}

	@Override
	protected void onResume ( ) {
		super.onResume();
		registerReceiver( receiver, filter, Permissions.SEND_COMMAND, null );
	}

	private void updateDownloadLabelUserId ( String userId ) {
		CharSequence cs =
				rapp.getString( R.string.prefControllerLabelsDownloadSummary )
						+ " " + userId;
		downloadkey.setSummary( cs );
	}

	private void updateExpansionLabelsVisibility ( int qty ) {
		boolean fEnable;
		if ( qty > 0 ) {
			explabelkey.setEnabled( true );
			explabelkey.setSelectable( true );
			for ( int i = 0; i < Controller.MAX_EXPANSION_RELAYS; i++ ) {
				if ( (i + 1) <= qty )
					fEnable = true;
				else
					fEnable = false;
				explabels[i].setEnabled( fEnable );
			}
		} else {
			// no expansion relays, disable the menu
			explabelkey.setEnabled( false );
			explabelkey.setSelectable( false );
		}
	}

	@Override
	public boolean onPreferenceChange ( Preference preference, Object newValue ) {
		// return true to change, false to not
		if ( preference.getKey()
				.equals( rapp.getString( R.string.prefPortKey ) ) ) {
			return rapp.validatePort( newValue );
		} else if ( preference.getKey()
				.equals( rapp.getString( R.string.prefHostKey ) ) ) {
			return rapp.validateHost( newValue );
		} else if ( preference.getKey()
				.equals( rapp.getString( R.string.prefUserIdKey ) ) ) {
			if ( !rapp.validateUser( newValue ) )
				return false;
			updateDownloadLabelUserId( newValue.toString() );
		} else if ( preference.getKey()
				.equals( rapp.getString( R.string.prefExpQtyKey ) ) ) {
			// enable / disable the Expansion Labels based on how
			// many expansion relays selected
			updateExpansionLabelsVisibility( Integer.parseInt( newValue
					.toString() ) );
		}
		return true;
	}

	class PrefsReceiver extends BroadcastReceiver {
		@Override
		public void onReceive ( Context context, Intent intent ) {
			Log.d( TAG, "Warn about labels" );
			// Toast.makeText( PrefsActivity.this,
			// rapp.getString( R.string.messageDownloadLabelsComplete ),
			// Toast.LENGTH_LONG ).show();
			AlertDialog.Builder builder =
					new AlertDialog.Builder( PrefsActivity.this );
			builder.setMessage( rapp.getString( R.string.messageDownloadMessage ) )
					.setCancelable( false )
					.setPositiveButton( rapp.getString( R.string.buttonOk ),
										new DialogInterface.OnClickListener() {
											public void onClick (
													DialogInterface dialog,
													int id ) {
												dialog.dismiss();
											}
										} );

			AlertDialog alert = builder.create();
			alert.show();
		}
	}
}
