package info.curtbinder.reefangel.phone;

/*
 * Copyright (c) 2011-12 by Curt Binder (http://curtbinder.info)
 *
 * This work is made available under the terms of the 
 * Creative Commons Attribution-NonCommercial-ShareAlike 3.0 Unported License
 * http://creativecommons.org/licenses/by-nc-sa/3.0/
 */

import info.curtbinder.reefangel.controller.Controller;
import info.curtbinder.reefangel.service.MessageCommands;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
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

	private Preference downloadkey;
	private Preference explabelkey;
	private Preference exp085xkey;
	private Preference[] explabels =
			new Preference[Controller.MAX_EXPANSION_RELAYS];
	private Preference updateprofilekey;

	RAApplication rapp;
	PrefsReceiver receiver;
	IntentFilter filter;

	@Override
	protected void onCreate ( Bundle savedInstanceState ) {
		super.onCreate( savedInstanceState );
		addPreferencesFromResource( R.xml.preferences );
		rapp = (RAApplication) getApplication();

		receiver = new PrefsReceiver();
		filter = new IntentFilter( MessageCommands.LABEL_RESPONSE_INTENT );

		Preference porthomekey =
				getPreferenceScreen()
						.findPreference( rapp.getString( R.string.prefPortKey ) );
		porthomekey.setOnPreferenceChangeListener( this );
		Preference hosthomekey =
				getPreferenceScreen()
						.findPreference( rapp.getString( R.string.prefHostKey ) );
		hosthomekey.setOnPreferenceChangeListener( this );
		Preference portawaykey =
				getPreferenceScreen()
						.findPreference(	rapp.getString( R.string.prefPortAwayKey ) );
		portawaykey.setOnPreferenceChangeListener( this );
		Preference hostawaykey =
				getPreferenceScreen()
						.findPreference(	rapp.getString( R.string.prefHostAwayKey ) );
		hostawaykey.setOnPreferenceChangeListener( this );
		Preference useridkey =
				getPreferenceScreen()
						.findPreference(	rapp.getString( R.string.prefUserIdKey ) );
		useridkey.setOnPreferenceChangeListener( this );
		Preference devicekey =
				getPreferenceScreen()
						.findPreference(	rapp.getString( R.string.prefDeviceKey ) );
		devicekey.setOnPreferenceChangeListener( this );
		Preference updatekey =
				getPreferenceScreen()
						.findPreference(	rapp.getString( R.string.prefAutoUpdateIntervalKey ) );
		updatekey.setOnPreferenceChangeListener( this );

		Preference expqtykey =
				getPreferenceScreen()
						.findPreference(	rapp.getString( R.string.prefExpQtyKey ) );
		expqtykey.setOnPreferenceChangeListener( this );
		explabelkey =
				getPreferenceScreen()
						.findPreference(	rapp.getString( R.string.prefExpLabelsKey ) );
		exp085xkey =
				getPreferenceScreen()
						.findPreference(	rapp.getString( R.string.prefExp085xKey ) );
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

		updateprofilekey =
				getPreferenceScreen()
						.findPreference(	rapp.getString( R.string.prefAutoUpdateProfileKey ) );
		updateprofilekey.setOnPreferenceChangeListener( this );
		if ( rapp.isAwayProfileEnabled() ) {
			updateAutoUpdateProfileVisibility( true );
		} else {
			updateAutoUpdateProfileVisibility( false );
		}

		Preference changelog =
				getPreferenceScreen()
						.findPreference(	rapp.getString( R.string.prefChangelogKey ) );
		changelog
				.setOnPreferenceClickListener( new OnPreferenceClickListener() {

					@Override
					public boolean onPreferenceClick ( Preference preference ) {
						Changelog.displayChangelog( PrefsActivity.this );
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

		Preference resetkey =
				getPreferenceScreen()
						.findPreference(	rapp.getString( R.string.prefResetLabelsKey ) );
		resetkey.setOnPreferenceClickListener( new OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick ( Preference preference ) {

				AlertDialog.Builder builder =
						new AlertDialog.Builder( PrefsActivity.this );
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
		} );

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
			exp085xkey.setEnabled( true );
			exp085xkey.setSelectable( true );
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
			exp085xkey.setEnabled( false );
			exp085xkey.setSelectable( false );
		}
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

		Toast.makeText( PrefsActivity.this,
						rapp.getString( R.string.messageResetLabelsComplete ),
						Toast.LENGTH_SHORT ).show();
	}

	@Override
	public boolean onPreferenceChange ( Preference preference, Object newValue ) {
		// return true to change, false to not
		if ( preference.getKey()
				.equals( rapp.getString( R.string.prefPortKey ) ) ) {
			boolean f = rapp.validatePort( newValue );
			if ( f ) {
				rapp.setPref( R.string.prefPortKey, newValue.toString() );
				// only restart service if it's enabled AND
				// the profile is not only away
				if ( (rapp.getUpdateInterval() > 0)
						&& (rapp.getUpdateProfile() != Globals.profileOnlyAway) ) {
					rapp.restartAutoUpdateService();
				}
			}
			return f;
		} else if ( preference.getKey()
				.equals( rapp.getString( R.string.prefHostKey ) ) ) {
			boolean f = rapp.validateHost( newValue );
			if ( f ) {
				rapp.setPref( R.string.prefHostKey, newValue.toString() );
				// only restart service if it's enabled AND
				// the profile is not only away
				if ( (rapp.getUpdateInterval() > 0)
						&& (rapp.getUpdateProfile() != Globals.profileOnlyAway) ) {
					rapp.restartAutoUpdateService();
				}
			}
			return f;
		} else if ( preference.getKey()
				.equals( rapp.getString( R.string.prefPortAwayKey ) ) ) {
			boolean f = rapp.validatePort( newValue );
			if ( f ) {
				rapp.setPref( R.string.prefPortAwayKey, newValue.toString() );
				// only restart if away is enabled
				if ( rapp.isAwayProfileEnabled() ) {
					// only restart service if it's enabled AND
					// the profile is not only home
					if ( (rapp.getUpdateInterval() > 0)
							&& (rapp.getUpdateProfile() != Globals.profileOnlyHome) ) {
						rapp.restartAutoUpdateService();
					}
				}
			}
			return f;
		} else if ( preference.getKey()
				.equals( rapp.getString( R.string.prefHostAwayKey ) ) ) {
			Log.d( TAG, "Change away host: " + newValue.toString() );
			// Away Host can be empty
			if ( newValue.toString().equals( "" ) ) {
				// set the selected profile to be the home profile
				// service gets restarted with setSelectedProfile call
				rapp.setPref( R.string.prefHostAwayKey, "" );
				rapp.setSelectedProfile( Globals.profileHome );
				// disable the selecting of the AutoUpdateProfiles
				// when away is disabled
				updateAutoUpdateProfileVisibility( false );
				return true;
			}
			// If it's not empty, validate the host
			boolean f = rapp.validateHost( newValue );
			if ( f ) {
				rapp.setPref( R.string.prefHostAwayKey, newValue.toString() );
				// only restart service if it's enabled AND
				// the profile is not only home
				if ( (rapp.getUpdateInterval() > 0)
						&& (rapp.getUpdateProfile() != Globals.profileOnlyHome) ) {
					rapp.restartAutoUpdateService();
				}
				updateAutoUpdateProfileVisibility( true );
			}
			return f;
		} else if ( preference.getKey()
				.equals( rapp.getString( R.string.prefUserIdKey ) ) ) {
			if ( !rapp.validateUser( newValue ) )
				return false;
			updateDownloadLabelUserId( newValue.toString() );
			if ( (rapp.getUpdateInterval() > 0)
					&& (rapp.isCommunicateController()) ) {
				rapp.setPref( R.string.prefUserIdKey, newValue.toString() );
				rapp.restartAutoUpdateService();
			}
		} else if ( preference.getKey()
				.equals( rapp.getString( R.string.prefExpQtyKey ) ) ) {
			// enable / disable the Expansion Labels based on how
			// many expansion relays selected
			updateExpansionLabelsVisibility( Integer.parseInt( newValue
					.toString() ) );
		} else if ( preference.getKey()
				.equals( rapp.getString( R.string.prefAutoUpdateIntervalKey ) ) ) {
			// when interval changes, update the repeat service
			long o = rapp.getUpdateInterval();
			long n = Long.parseLong( newValue.toString() );
			Log.d( TAG, "Change Interval:  " + o + " - " + n );
			if ( n != o ) {
				// Old and new values differ, restart the service
				rapp.setPref(	R.string.prefAutoUpdateIntervalKey,
								newValue.toString() );
				// TODO check if parameter needed with previous value storing
				rapp.restartAutoUpdateService();
			}
			boolean fVisible = false;
			// TODO check this, update visibility appropriately
			if ( rapp.isAwayProfileEnabled() && (n > 0) ) {
				Log.d( TAG, "enable update profile" );
				fVisible = true;
			}
			updateAutoUpdateProfileVisibility( fVisible );
		} else if ( preference.getKey()
				.equals( rapp.getString( R.string.prefAutoUpdateProfileKey ) ) ) {
			// restart the update service if we change the update profile
			rapp.setPref(	R.string.prefAutoUpdateProfileKey,
							newValue.toString() );
			rapp.restartAutoUpdateService();
		} else if ( preference.getKey()
				.equals( rapp.getString( R.string.prefDeviceKey ) ) ) {
			// device changes
			rapp.setPref( R.string.prefDeviceKey, newValue.toString() );
			boolean f = false;
			if ( rapp.isCommunicateController() ) {
				if ( rapp.isAwayProfileEnabled() )
					f = true;
			}
			rapp.restartAutoUpdateService();
			updateAutoUpdateProfileVisibility( f );
		}
		return true;
	}

	private void updateAutoUpdateProfileVisibility ( boolean fVisible ) {
		updateprofilekey.setEnabled( fVisible );
		updateprofilekey.setSelectable( fVisible );
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
