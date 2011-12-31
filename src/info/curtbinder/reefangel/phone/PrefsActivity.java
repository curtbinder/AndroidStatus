package info.curtbinder.reefangel.phone;

//import android.app.AlertDialog;
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

	private static final String TAG = "RAPrefs";
	private static final String NUMBER_PATTERN = "\\d+";
	private static final String HOST_PATTERN =
			"^(?i:[[0-9][a-z]]+)(?i:[\\w\\.\\-]*)(?i:[[0-9][a-z]]+)$";
	private static final String USERID_PATTERN = "[\\w\\-\\.]+";

	private boolean fRestart;
	private Preference portkey;
	private Preference hostkey;
	private Preference useridkey;

	RAApplication rapp;
	PrefsReceiver receiver;
	IntentFilter filter;

	@Override
	protected void onCreate ( Bundle savedInstanceState ) {
		super.onCreate( savedInstanceState );
		addPreferencesFromResource( R.xml.settings );
		rapp = (RAApplication) getApplication();

		receiver = new PrefsReceiver();
		filter = new IntentFilter( ControllerTask.LABEL_RESPONSE_INTENT );

		fRestart = true;
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
						fRestart = false;
						Intent browserIntent =
								new Intent(
									Intent.ACTION_VIEW,
									Uri.parse( rapp
											.getString( R.string.websiteReefangel ) ) );
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
				fRestart = false;
				startActivity( new Intent( rapp, LicenseActivity.class ) );
				return true;
			}
		} );

		Preference download =
				getPreferenceScreen()
						.findPreference(	rapp.getString( R.string.prefControllerLabelsDownloadKey ) );
		download.setOnPreferenceClickListener( new OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick ( Preference preference ) {

				AlertDialog.Builder builder =
						new AlertDialog.Builder( PrefsActivity.this );
				builder.setMessage( "Download all labels for "
											+ rapp.getPrefUserId() + "?" )
						.setCancelable( false )
						.setPositiveButton( rapp.getString( R.string.yesButton ),
											new DialogInterface.OnClickListener() {
												public void onClick (
														DialogInterface dialog,
														int id ) {
													// launch download
													Log.d(	TAG,
															"Download labels" );
													Intent i =
															new Intent(
																ControllerService.LABEL_QUERY_INTENT );
													rapp.sendBroadcast( i );
													dialog.dismiss();
													Toast.makeText( PrefsActivity.this,
																	rapp.getString( R.string.messageDownloadLabels ),
																	Toast.LENGTH_LONG )
															.show();
												}
											} )
						.setNegativeButton( rapp.getString( R.string.noButton ),
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
		CharSequence cs = download.getSummary() + " " + rapp.getPrefUserId();
		download.setSummary( cs );

		// toggle the visibility of preferences based on device selection
		toggleDevicePrefVisibility( Integer.parseInt( rapp.getPrefDevice() ) );
	}

	@Override
	protected void onPause ( ) {
		super.onPause();
		unregisterReceiver( receiver );
		if ( fRestart ) {
			Log.d( TAG, "Prefs Pause / Restart App" );
			Intent i = new Intent( this, StatusActivity.class );
			i.addFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP );
			startActivity( i );
			finish();
		} else {
			Log.d( TAG, "Prefs Pause" );
		}
	}

	@Override
	protected void onResume ( ) {
		super.onResume();
		fRestart = true;
		registerReceiver( receiver, filter );
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
			/*
			 * Hosts must: - not start with 'http://' - only contain: alpha,
			 * number, _, -, . - end with: alpha or number
			 */
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
				.equals( rapp.getString( R.string.prefDeviceKey ) ) ) {
			// enable / disable categories based on what the user selected
			Log.d( TAG, "Update enabled prefs" );
			toggleDevicePrefVisibility( Integer.parseInt( newValue.toString() ) );
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

	private void toggleDevicePrefVisibility ( int d ) {
		String[] devicesArray =
				rapp.getResources().getStringArray( R.array.devicesValues );
		int devReefAngel = Integer.parseInt( devicesArray[1] );
		boolean boolEnableReefAngel = false;
		if ( d == devReefAngel ) {
			boolEnableReefAngel = true;
		}
		useridkey.setEnabled( boolEnableReefAngel );
		portkey.setEnabled( !boolEnableReefAngel );
		hostkey.setEnabled( !boolEnableReefAngel );
	}

	protected void updateLabels ( String[] temps, String[] main, String[][] exp ) {
		// set the labels
		rapp.setPref( R.string.prefT1LabelKey, temps[0] );
		rapp.setPref( R.string.prefT2LabelKey, temps[1] );
		rapp.setPref( R.string.prefT3LabelKey, temps[2] );
		int i, j;
		for ( i = 0; i < Controller.MAX_RELAY_PORTS; i++ ) {
			rapp.setPrefRelayLabel( 0, i, main[i] );
		}
		for ( i = 0; i < Controller.MAX_EXPANSION_RELAYS; i++ ) {
			for ( j = 0; j < Controller.MAX_RELAY_PORTS; j++ ) {
				// use i+1 because the expansion relays start at 1
				rapp.setPrefRelayLabel( i + 1, j, exp[i][j] );
			}
		}
		Toast.makeText( PrefsActivity.this,
						rapp.getString( R.string.messageDownloadLabelsComplete ),
						Toast.LENGTH_LONG ).show();
		AlertDialog.Builder builder =
				new AlertDialog.Builder( PrefsActivity.this );
		builder.setMessage( rapp.getString( R.string.messageDownloadMessage ) )
				.setCancelable( false )
				.setPositiveButton( rapp.getString( R.string.okButton ),
									new DialogInterface.OnClickListener() {
										public void onClick (
												DialogInterface dialog,
												int id ) {
											Log.d( TAG, "Warn about labels" );
											dialog.dismiss();
										}
									} );

		AlertDialog alert = builder.create();
		alert.show();
	}

	class PrefsReceiver extends BroadcastReceiver {
		@Override
		public void onReceive ( Context context, Intent intent ) {
			// only receives the label response message
			// grab all the labels
			String[] temps = new String[Controller.MAX_TEMP_SENSORS];
			String[] main = new String[Controller.MAX_RELAY_PORTS];
			String[][] exp =
					new String[Controller.MAX_EXPANSION_RELAYS][Controller.MAX_RELAY_PORTS];
			temps =
					intent.getStringArrayExtra( ControllerTask.LABEL_RESPONSE_TEMP_ARRAY );
			main =
					intent.getStringArrayExtra( ControllerTask.LABEL_RESPONSE_MAIN_ARRAY );
			exp[0] =
					intent.getStringArrayExtra( ControllerTask.LABEL_RESPONSE_EXP1_ARRAY );
			exp[1] =
					intent.getStringArrayExtra( ControllerTask.LABEL_RESPONSE_EXP2_ARRAY );
			exp[2] =
					intent.getStringArrayExtra( ControllerTask.LABEL_RESPONSE_EXP3_ARRAY );
			exp[3] =
					intent.getStringArrayExtra( ControllerTask.LABEL_RESPONSE_EXP4_ARRAY );
			exp[4] =
					intent.getStringArrayExtra( ControllerTask.LABEL_RESPONSE_EXP5_ARRAY );
			exp[5] =
					intent.getStringArrayExtra( ControllerTask.LABEL_RESPONSE_EXP6_ARRAY );
			exp[6] =
					intent.getStringArrayExtra( ControllerTask.LABEL_RESPONSE_EXP7_ARRAY );
			exp[7] =
					intent.getStringArrayExtra( ControllerTask.LABEL_RESPONSE_EXP8_ARRAY );
			updateLabels( temps, main, exp );
		}
	}
}
