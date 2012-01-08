package info.curtbinder.reefangel.phone;

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
	private static final String NUMBER_PATTERN = "\\d+";
	private static final String HOST_PATTERN =
			"^(?i:[[0-9][a-z]]+)(?i:[\\w\\.\\-]*)(?i:[[0-9][a-z]]+)$";
	private static final String USERID_PATTERN = "[\\w\\-\\.]+";

	private Preference portkey;
	private Preference hostkey;
	private Preference useridkey;
	private Preference downloadkey;

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
															rapp.sendBroadcast( i );
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
		registerReceiver( receiver, filter );
	}

	private void updateDownloadLabelUserId ( String userId ) {
		CharSequence cs =
				rapp.getString( R.string.prefControllerLabelsDownloadSummary )
						+ " " + userId;
		downloadkey.setSummary( cs );
	}

	@Override
	public boolean onPreferenceChange ( Preference preference, Object newValue ) {
		// return true to change, false to not
		if ( preference.getKey()
				.equals( rapp.getString( R.string.prefPortKey ) ) ) {
			Log.d( TAG, "Validate entered port" );
			if ( !isNumber( newValue ) ) {
				// not a number
				Log.d( TAG, "Invalid port" );
				Toast.makeText( rapp,
								rapp.getString( R.string.messageNotNumber )
										+ ": " + newValue.toString(),
								Toast.LENGTH_SHORT ).show();
				return false;
			} else {
				// it's a number, verify it's within range
				int min =
						Integer.parseInt( rapp.getString( R.string.prefPortMin ) );
				int max =
						Integer.parseInt( rapp.getString( R.string.prefPortMax ) );
				int v = Integer.parseInt( (String) newValue.toString() );

				// check if it's less than the min value or if it's greater than
				// the max value
				if ( (v < min) || (v > max) ) {
					Log.d( TAG, "Invalid port range" );
					Toast.makeText( rapp,
									rapp.getString( R.string.prefPortInvalidPort )
											+ ": " + newValue.toString(),
									Toast.LENGTH_SHORT ).show();
					return false;
				}
			}
		} else if ( preference.getKey()
				.equals( rapp.getString( R.string.prefHostKey ) ) ) {
			// host validation here
			Log.d( TAG, "Validate entered host" );
			String h = newValue.toString();

			// Hosts must:
			// - not start with 'http://'
			// - only contain: alpha, number, _, -, .
			// - end with: alpha or number

			if ( !h.matches( HOST_PATTERN ) ) {
				// invalid host
				Log.d( TAG, "Invalid host" );
				Toast.makeText( rapp,
								rapp.getString( R.string.prefHostInvalidHost )
										+ ": " + newValue.toString(),
								Toast.LENGTH_SHORT ).show();
				return false;
			}
		} else if ( preference.getKey()
				.equals( rapp.getString( R.string.prefUserIdKey ) ) ) {
			String u = newValue.toString();
			if ( !u.matches( USERID_PATTERN ) ) {
				// invalid userid
				Log.d( TAG, "Invalid userid" );
				Toast.makeText( rapp,
								rapp.getString( R.string.prefUserIdInvalid )
										+ ": " + newValue.toString(),
								Toast.LENGTH_SHORT ).show();
				return false;
			}
			updateDownloadLabelUserId( u );
		}
		return true;
	}

	private boolean isNumber ( Object value ) {
		if ( (!value.toString().equals( "" ))
				&& (value.toString().matches( NUMBER_PATTERN )) ) {
			return true;
		}
		return false;
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
