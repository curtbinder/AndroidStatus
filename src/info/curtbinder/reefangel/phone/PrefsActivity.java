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
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.util.Log;
import android.widget.Toast;

public class PrefsActivity extends PreferenceActivity implements
		OnPreferenceChangeListener, OnSharedPreferenceChangeListener {

	private static final String TAG = PrefsActivity.class.getSimpleName();

	private Preference profilekey;
	private Preference downloadkey;
	private Preference explabelkey;
	private Preference exp085xkey;
	private Preference[] explabels =
			new Preference[Controller.MAX_EXPANSION_RELAYS];
	private Preference updateprofilekey;
	private String[] devicesArray;
	private String[] profilesArray;
	private String[] expRelayQtyArray;

	RAApplication rapp;
	PrefsReceiver receiver;
	IntentFilter filter;

	protected void onCreate ( Bundle savedInstanceState ) {
		super.onCreate( savedInstanceState );
		addPreferencesFromResource( R.xml.preferences );
		rapp = (RAApplication) getApplication();
		devicesArray = rapp.getResources().getStringArray( R.array.devices );
		profilesArray =
				rapp.getResources().getStringArray( R.array.profileLabels );
		expRelayQtyArray =
				rapp.getResources().getStringArray( R.array.expansionRelays );

		receiver = new PrefsReceiver();
		filter = new IntentFilter( MessageCommands.LABEL_RESPONSE_INTENT );

		// set the device label
		updateDeviceKeySummary();

		Preference porthomekey =
				findPreference( rapp.getString( R.string.prefPortKey ) );
		porthomekey.setOnPreferenceChangeListener( this );
		Preference hosthomekey =
				findPreference( rapp.getString( R.string.prefHostKey ) );
		hosthomekey.setOnPreferenceChangeListener( this );
		Preference portawaykey =
				findPreference( rapp.getString( R.string.prefPortAwayKey ) );
		portawaykey.setOnPreferenceChangeListener( this );
		Preference hostawaykey =
				findPreference( rapp.getString( R.string.prefHostAwayKey ) );
		hostawaykey.setOnPreferenceChangeListener( this );
		Preference useridkey =
				findPreference( rapp.getString( R.string.prefUserIdKey ) );
		useridkey.setOnPreferenceChangeListener( this );
		explabelkey =
				findPreference( rapp.getString( R.string.prefExpLabelsKey ) );
		exp085xkey = findPreference( rapp.getString( R.string.prefExp085xKey ) );
		explabels[0] =
				findPreference( rapp.getString( R.string.prefExp1RelayKey ) );
		explabels[1] =
				findPreference( rapp.getString( R.string.prefExp2RelayKey ) );
		explabels[2] =
				findPreference( rapp.getString( R.string.prefExp3RelayKey ) );
		explabels[3] =
				findPreference( rapp.getString( R.string.prefExp4RelayKey ) );
		explabels[4] =
				findPreference( rapp.getString( R.string.prefExp5RelayKey ) );
		explabels[5] =
				findPreference( rapp.getString( R.string.prefExp6RelayKey ) );
		explabels[6] =
				findPreference( rapp.getString( R.string.prefExp7RelayKey ) );
		explabels[7] =
				findPreference( rapp.getString( R.string.prefExp8RelayKey ) );
		updateExpansionLabelsVisibility( rapp.getPrefExpansionRelayQuantity() );

		profilekey =
				findPreference( rapp
						.getString( R.string.prefProfileSelectedKey ) );
		updateSelectedProfileVisibility();
		updateSelectedProfileSummary();
		updateHostsSummary();
		updateExpRelayQuantitySummary();

		updateprofilekey =
				findPreference( rapp
						.getString( R.string.prefAutoUpdateProfileKey ) );
		updateprofilekey.setOnPreferenceChangeListener( this );
		if ( rapp.isAwayProfileEnabled() ) {
			updateAutoUpdateProfileVisibility( true );
		} else {
			updateAutoUpdateProfileVisibility( false );
		}
		updateprofilekey.setSummary( rapp.getUpdateProfileDisplay() );
		findPreference( rapp.getString( R.string.prefAutoUpdateIntervalKey ) )
				.setSummary( rapp.getUpdateIntervalDisplay() );
		findPreference( rapp.getString( R.string.prefLoggingUpdateKey ) )
				.setSummary( rapp.getLoggingUpdateDisplay() );

		Preference changelog =
				findPreference( rapp.getString( R.string.prefChangelogKey ) );
		changelog
				.setOnPreferenceClickListener( new OnPreferenceClickListener() {

					public boolean onPreferenceClick ( Preference preference ) {
						Changelog.displayChangelog( PrefsActivity.this );
						return true;
					}
				} );

		downloadkey =
				findPreference( rapp
						.getString( R.string.prefControllerLabelsDownloadKey ) );
		downloadkey
				.setOnPreferenceClickListener( new OnPreferenceClickListener() {

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
		updateUserIds();

		Preference resetkey =
				findPreference( rapp.getString( R.string.prefResetLabelsKey ) );
		resetkey.setOnPreferenceClickListener( new OnPreferenceClickListener() {

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

		Preference deletelog =
				findPreference( rapp.getString( R.string.prefLoggingDeleteKey ) );
		deletelog
				.setOnPreferenceClickListener( new OnPreferenceClickListener() {

					public boolean onPreferenceClick ( Preference preference ) {

						AlertDialog.Builder builder =
								new AlertDialog.Builder( PrefsActivity.this );
						builder.setMessage( rapp.getString( R.string.messageDeleteLogPrompt ) )
								.setCancelable( false )
								.setPositiveButton( rapp.getString( R.string.buttonYes ),
													new DialogInterface.OnClickListener() {
														public void onClick (
																DialogInterface dialog,
																int id ) {
															Log.d(	TAG,
																	"Delete log file" );
															dialog.dismiss();
															deleteLogFile();
														}
													} )
								.setNegativeButton( rapp.getString( R.string.buttonNo ),
													new DialogInterface.OnClickListener() {
														public void onClick (
																DialogInterface dialog,
																int id ) {
															Log.d(	TAG,
																	"Delete log cancelled" );
															dialog.cancel();
														}
													} );

						AlertDialog alert = builder.create();
						alert.show();
						return true;
					}
				} );

		Preference sendemail =
				findPreference( rapp.getString( R.string.prefLoggingSendKey ) );
		sendemail
				.setOnPreferenceClickListener( new OnPreferenceClickListener() {

					public boolean onPreferenceClick ( Preference preference ) {

						AlertDialog.Builder builder =
								new AlertDialog.Builder( PrefsActivity.this );
						builder.setMessage( rapp.getString( R.string.messageSendLogPrompt ) )
								.setCancelable( false )
								.setPositiveButton( rapp.getString( R.string.buttonYes ),
													new DialogInterface.OnClickListener() {
														public void onClick (
																DialogInterface dialog,
																int id ) {
															Log.d(	TAG,
																	"Send file" );
															dialog.dismiss();
															sendEmail();
														}
													} )
								.setNegativeButton( rapp.getString( R.string.buttonNo ),
													new DialogInterface.OnClickListener() {
														public void onClick (
																DialogInterface dialog,
																int id ) {
															Log.d(	TAG,
																	"Send cancelled" );
															dialog.cancel();
														}
													} );

						AlertDialog alert = builder.create();
						alert.show();
						return true;
					}
				} );

		// disable deleting and sending of the log file if not present
		if ( !rapp.isLoggingFilePresent() ) {
			deletelog.setEnabled( false );
			sendemail.setEnabled( false );
		}
	}

	protected void onPause ( ) {
		super.onPause();
		unregisterReceiver( receiver );
		getPreferenceScreen().getSharedPreferences()
				.unregisterOnSharedPreferenceChangeListener( this );
	}

	protected void onResume ( ) {
		super.onResume();
		registerReceiver( receiver, filter, Permissions.SEND_COMMAND, null );
		getPreferenceScreen().getSharedPreferences()
				.registerOnSharedPreferenceChangeListener( this );
	}

	private void updateSelectedProfileVisibility ( ) {
		boolean fEnable = false;
		if ( rapp.isAwayProfileEnabled() && rapp.isCommunicateController() ) {
			fEnable = true;
		}
		profilekey.setEnabled( fEnable );
		profilekey.setSelectable( fEnable );
	}

	private void updateDownloadLabelUserId ( String userId ) {
		CharSequence cs =
				rapp.getString( R.string.prefControllerLabelsDownloadSummary )
						+ " " + userId;
		downloadkey.setSummary( cs );
	}

	private void updateUserIdSummary ( String s ) {
		findPreference( rapp.getString( R.string.prefUserIdKey ) )
				.setSummary( s );
	}

	private void updateUserIds ( ) {
		updateUserIdSummary( rapp.getPrefUserId() );
		updateDownloadLabelUserId( rapp.getPrefUserId() );
	}

	private void updateSelectedProfileSummary ( ) {
		profilekey.setSummary( profilesArray[rapp.getSelectedProfile()] );
	}

	private void updateHostsSummary ( ) {
		updateHomeHostSummary();
		updateHomePortSummary();
		updateAwayHostSummary();
		updateAwayPortSummary();
	}

	private void updateHomeHostSummary ( ) {
		findPreference( rapp.getString( R.string.prefHostKey ) )
				.setSummary( rapp.getPrefHomeHost() );
	}

	private void updateHomePortSummary ( ) {
		findPreference( rapp.getString( R.string.prefPortKey ) )
				.setSummary( rapp.getPrefHomePort() );
	}

	private void updateAwayHostSummary ( ) {
		findPreference( rapp.getString( R.string.prefHostAwayKey ) )
				.setSummary( rapp.getPrefAwayHost() );
	}

	private void updateAwayPortSummary ( ) {
		findPreference( rapp.getString( R.string.prefPortAwayKey ) )
				.setSummary( rapp.getPrefAwayPort() );
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

	private void updateDeviceKeySummary ( ) {
		String s;
		if ( rapp.isCommunicateController() ) {
			// 0 index is controller
			s = devicesArray[0];
		} else {
			// 1 index is Portal
			s = devicesArray[1];
		}
		findPreference( rapp.getString( R.string.prefDeviceKey ) )
				.setSummary( s );
	}

	private void updateExpRelayQuantitySummary ( ) {
		// change to use array list instead
		int value = rapp.getPrefExpansionRelayQuantity();
		findPreference( rapp.getString( R.string.prefExpQtyKey ) )
				.setSummary( expRelayQtyArray[value] );
	}

	private void updateSummary ( int key, String s ) {
		findPreference( rapp.getString( key ) ).setSummary( s );
	}

	private void updateControllerLabelVisibilitySummary ( ) {
		updateSummary( R.string.prefT2LabelKey, "" );
	}

	private void updateSummaryFormat (
			int key,
			int formatString,
			int enabledString,
			int disabledString,
			boolean cond ) {
		String f = rapp.getString( formatString );
		String value;
		if ( cond ) {
			value = rapp.getString( enabledString );
		} else {
			value = rapp.getString( disabledString );
		}
		findPreference( rapp.getString( key ) )
				.setSummary( String.format( f, value ) );
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
		startActivity( Intent.createChooser( email, "Send email..." ) );
	}

	private void deleteLogFile ( ) {
		rapp.deleteLoggingFile();
		// disable deleting and sending of the log file if not present
		if ( !rapp.isLoggingFilePresent() ) {
			findPreference( rapp.getString( R.string.prefLoggingDeleteKey ) )
					.setEnabled( false );
			findPreference( rapp.getString( R.string.prefLoggingSendKey ) )
					.setEnabled( false );
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

	public boolean onPreferenceChange ( Preference preference, Object newValue ) {
		// return true to change, false to not
		if ( preference.getKey()
				.equals( rapp.getString( R.string.prefPortKey ) ) ) {
			return rapp.validatePort( newValue );
		} else if ( preference.getKey()
				.equals( rapp.getString( R.string.prefHostKey ) ) ) {
			return rapp.validateHost( newValue );
		} else if ( preference.getKey()
				.equals( rapp.getString( R.string.prefPortAwayKey ) ) ) {
			return rapp.validatePort( newValue );
		} else if ( preference.getKey()
				.equals( rapp.getString( R.string.prefHostAwayKey ) ) ) {
			Log.d( TAG, "Change away host: " + newValue.toString() );
			// Away Host can be empty
			if ( newValue.toString().equals( "" ) ) {
				return true;
			}
			return rapp.validateHost( newValue );
		} else if ( preference.getKey()
				.equals( rapp.getString( R.string.prefUserIdKey ) ) ) {
			if ( !rapp.validateUser( newValue ) )
				return false;

		}
		return true;
	}

	private void updateAutoUpdateProfileVisibility ( boolean fVisible ) {
		updateprofilekey.setEnabled( fVisible );
		updateprofilekey.setSelectable( fVisible );
	}

	public void onSharedPreferenceChanged (
			SharedPreferences sharedPreferences,
			String key ) {
		if ( key.equals( rapp.getString( R.string.prefHostKey ) )
				|| key.equals( rapp.getString( R.string.prefPortKey ) ) ) {
			updateHomeHostSummary();
			updateHomePortSummary();
			// only restart service if:
			// - enabled (interval > 0)
			// -- away profile enabled AND the profile is not only away
			// -- away profile disabled
			if ( rapp.getUpdateInterval() > 0 && rapp.isCommunicateController() ) {
				if ( rapp.isAwayProfileEnabled() ) {
					if ( rapp.getUpdateProfile() != Globals.profileOnlyAway ) {
						Log.d(	TAG,
								"away enabled: restart based on home host & port" );
						rapp.restartAutoUpdateService();
					}
				} else {
					Log.d(	TAG,
							"away disabled: restart based on home host & port" );
					rapp.restartAutoUpdateService();
				}
			}
		} else if ( key.equals( rapp.getString( R.string.prefPortAwayKey ) )
					|| key.equals( rapp.getString( R.string.prefHostAwayKey ) ) ) {
			updateAwayHostSummary();
			updateAwayPortSummary();
			// only restart service if:
			// - away profile enabled
			// - enabled (interval > 0) AND the profile is not only home
			boolean f = false;
			if ( rapp.isAwayProfileEnabled() ) {
				// only restart service if it's enabled AND
				// the profile is not only home
				if ( rapp.isCommunicateController() ) {
					if ( (rapp.getUpdateInterval() > 0)
							&& (rapp.getUpdateProfile() != Globals.profileOnlyHome) ) {
						Log.d( TAG, "restart based on away host & port" );
						rapp.restartAutoUpdateService();
					}
				}
				f = true;
			} else {
				// user cleared the away host, disabling profiles
				if ( key.equals( rapp.getString( R.string.prefHostAwayKey ) ) ) {
					// clear out the profiles
					rapp.setPref(	R.string.prefProfileSelectedKey,
									"" + Globals.profileHome );
					if ( rapp.isCommunicateController() )
						// only restart if a controller
						rapp.restartAutoUpdateService();
				}
			}
			updateAutoUpdateProfileVisibility( f );
			updateSelectedProfileVisibility();
		} else if ( key.equals( rapp.getString( R.string.prefUserIdKey ) ) ) {
			updateUserIds();
			if ( (rapp.getUpdateInterval() > 0)
					&& (!rapp.isCommunicateController()) ) {
				Log.d( TAG, "restart based on userid changing" );
				rapp.restartAutoUpdateService();
			}
		} else if ( key.equals( rapp.getString( R.string.prefExpQtyKey ) ) ) {
			// enable / disable the Expansion Labels based on how
			// many expansion relays selected
			updateExpansionLabelsVisibility( rapp
					.getPrefExpansionRelayQuantity() );
			updateExpRelayQuantitySummary();
		} else if ( key.equals( rapp
				.getString( R.string.prefAutoUpdateIntervalKey ) ) ) {
			// when interval changes, update the repeat service
			// updateprofilekey.setSummary( rapp.getUpdateIntervalDisplay() );
			findPreference( rapp.getString( R.string.prefAutoUpdateIntervalKey ) )
					.setSummary( rapp.getUpdateIntervalDisplay() );
			rapp.restartAutoUpdateService();
			boolean fVisible = false;
			if ( rapp.isAwayProfileEnabled() && (rapp.getUpdateInterval() > 0) ) {
				Log.d( TAG, "enable update profile" );
				fVisible = true;
			}
			updateAutoUpdateProfileVisibility( fVisible );
		} else if ( key.equals( rapp
				.getString( R.string.prefAutoUpdateProfileKey ) ) ) {
			// restart the update service if we change the update profile
			updateprofilekey.setSummary( rapp.getUpdateProfileDisplay() );
			rapp.restartAutoUpdateService();
		} else if ( key.equals( rapp.getString( R.string.prefDeviceKey ) ) ) {
			// device changes
			boolean f = false;
			if ( rapp.isCommunicateController() && rapp.isAwayProfileEnabled() )
				f = true;
			// only restart if there is an interval
			if ( rapp.getUpdateInterval() > 0 )
				rapp.restartAutoUpdateService();
			updateAutoUpdateProfileVisibility( f );
			updateSelectedProfileVisibility();
			updateDeviceKeySummary();
		} else if ( key.equals( rapp
				.getString( R.string.prefProfileSelectedKey ) ) ) {
			if ( rapp.getUpdateInterval() > 0 ) {
				Log.d( TAG, "profile changed, restart" );
				rapp.restartAutoUpdateService();
			}
			updateSelectedProfileSummary();
		} else if ( key
				.equals( rapp.getString( R.string.prefLoggingUpdateKey ) ) ) {
			findPreference( rapp.getString( R.string.prefLoggingUpdateKey ) )
					.setSummary( rapp.getLoggingUpdateDisplay() );
		}
	}

	class PrefsReceiver extends BroadcastReceiver {

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
