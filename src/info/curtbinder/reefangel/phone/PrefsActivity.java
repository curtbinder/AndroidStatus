package info.curtbinder.reefangel.phone;

//import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.util.Log;
import android.widget.Toast;

public class PrefsActivity extends PreferenceActivity implements OnPreferenceChangeListener {

	private static final String TAG = "RAPrefs";
	private static final String NUMBER_PATTERN = "\\d+";
	private static final String HOST_PATTERN = "^(?i:[[0-9][a-z]]+)(?i:[\\w\\.\\-]*)(?i:[[0-9][a-z]]+)$";
	private static final String USERID_PATTERN = "[\\w\\-\\.]+";
	
	private boolean fRestart;
	private Preference portkey;
	private Preference hostkey;
	private Preference useridkey;
	
	RAApplication rapp;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.settings);
		rapp = (RAApplication)getApplication();
		
		fRestart = true;
		portkey = getPreferenceScreen().findPreference(rapp.getString(R.string.prefPortKey));
		portkey.setOnPreferenceChangeListener(this);
		hostkey = getPreferenceScreen().findPreference(rapp.getString(R.string.prefHostKey));
		hostkey.setOnPreferenceChangeListener(this);
		useridkey = getPreferenceScreen().findPreference(rapp.getString(R.string.prefUserIdKey));
		useridkey.setOnPreferenceChangeListener(this);
		Preference devicekey = getPreferenceScreen().findPreference(rapp.getString(R.string.prefDeviceKey));
		devicekey.setOnPreferenceChangeListener(this);
		Preference raWebsite = getPreferenceScreen().findPreference(rapp.getString(R.string.prefReefAngelKey));
		raWebsite.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			
			@Override
			public boolean onPreferenceClick(Preference preference) {
				fRestart = false;
				Intent browserIntent = new Intent(Intent.ACTION_VIEW, 
						Uri.parse(rapp.getString(R.string.websiteReefangel)));
				startActivity(browserIntent);
				return true;
			}
		});
		Preference license = getPreferenceScreen().findPreference(rapp.getString(R.string.prefLicenseKey));
		license.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			
			@Override
			public boolean onPreferenceClick(Preference preference) {
				fRestart = false;
				startActivity(new Intent(rapp, LicenseActivity.class));
				return true;
			}
		});

		// TODO add in handler for Download All Labels click
		Preference download = getPreferenceScreen().findPreference(rapp.getString(R.string.prefControllerLabelsDownloadKey));
		download.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(Preference preference) {
				/*
				AlertDialog.Builder builder = new AlertDialog.Builder(Prefs.this);
				builder.setMessage("Download all labels for " + getUserId(getBaseContext()) + "?")
				       .setCancelable(false)
				       .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
				           public void onClick(DialogInterface dialog, int id) {
				        	   // launch download
				        	   Log.d(TAG, "Download labels");
				        	   dialog.dismiss();
				           }
				       })
				       .setNegativeButton("No", new DialogInterface.OnClickListener() {
				           public void onClick(DialogInterface dialog, int id) {
				        	   Log.d(TAG, "Cancel download");
				        	   dialog.cancel();
				           }
				       });

				AlertDialog alert = builder.create();
				alert.show();
				*/
				Toast.makeText(PrefsActivity.this, "Not implemented yet", Toast.LENGTH_SHORT).show();
				return true;
			}
		});
		CharSequence cs = download.getSummary() + " " + rapp.getPrefUserId();
		download.setSummary(cs);
		
		// toggle the visibility of preferences based on device selection
		toggleDevicePrefVisibility(Integer.parseInt(rapp.getPrefDevice()));
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		if ( fRestart ) {
			Log.d(TAG, "Prefs Pause / Restart App");
			Intent i = new Intent(this, StatusActivity.class);
			i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(i);
			finish();
		} else {
			Log.d(TAG, "Prefs Pause");
		}
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		fRestart = true;
	}
	
	@Override
	public boolean onPreferenceChange(Preference preference, Object newValue) {
		// return true to change, false to not
		if ( preference.getKey().equals(rapp.getString(R.string.prefPortKey))) {
			Log.d(TAG, "Validate entered port");
			if ( ! isNumber(newValue) ) {
				// not a number
				Log.d(TAG, "Invalid port");
				Toast.makeText(rapp, rapp.getString(R.string.messageNotNumber) + ": " + newValue.toString(), Toast.LENGTH_SHORT).show();				
				return false;
			} else {
				// it's a number, verify it's within range
				int min = Integer.parseInt(rapp.getString(R.string.prefPortMin));
				int max = Integer.parseInt(rapp.getString(R.string.prefPortMax));
				int v = Integer.parseInt((String)newValue.toString());
				
				// check if it's less than the min value or if it's greater than the max value
				if ( (v < min) || (v > max) ) {
					Log.d(TAG, "Invalid port range");
					Toast.makeText(rapp, rapp.getString(R.string.prefPortInvalidPort) + ": " + newValue.toString(), Toast.LENGTH_SHORT).show();
					return false;
				}
			}
		} else if ( preference.getKey().equals(rapp.getString(R.string.prefHostKey))) {
			// host validation here
			Log.d(TAG, "Validate entered host");
			String h = newValue.toString();
			/**
			 * Hosts must:
			 *  - not start with 'http://'
			 *  - only contain:  alpha, number, _, -, .
			 *  - end with: alpha or number
			 */
			if ( ! h.matches(HOST_PATTERN) ) {
				// invalid host
				Log.d(TAG, "Invalid host");
				Toast.makeText(rapp, 
						rapp.getString(R.string.prefHostInvalidHost) + ": " + newValue.toString(), 
						Toast.LENGTH_SHORT).show();
				return false;
			}
		} else if ( preference.getKey().equals(rapp.getString(R.string.prefDeviceKey)) ) {
			// enable / disable categories based on what the user selected
			Log.d(TAG, "Update enabled prefs");
			toggleDevicePrefVisibility(Integer.parseInt(newValue.toString()));
		} else if ( preference.getKey().equals(rapp.getString(R.string.prefUserIdKey)) ) {
			String u = newValue.toString();
			if ( ! u.matches(USERID_PATTERN) ) {
				// invalid userid
				Log.d(TAG, "Invalid userid");
				Toast.makeText(rapp, 
						rapp.getString(R.string.prefUserIdInvalid) + ": " + newValue.toString(), 
						Toast.LENGTH_SHORT).show();	
				return false;
			}
		}
		return true;
	}
	
	private boolean isNumber(Object value) {
		if ( (! value.toString().equals("")) &&
			 ( value.toString().matches(NUMBER_PATTERN)) )
		{
			return true;
		}
		return false;
	}
	
	private void toggleDevicePrefVisibility(int d) {
		String[] devicesArray = rapp.getResources().getStringArray(R.array.devicesValues);
		int devReefAngel = Integer.parseInt(devicesArray[1]);
		boolean boolEnableReefAngel = false;
		if ( d == devReefAngel ) {
			boolEnableReefAngel = true;
		}
		useridkey.setEnabled(boolEnableReefAngel);
		portkey.setEnabled(!boolEnableReefAngel);
		hostkey.setEnabled(!boolEnableReefAngel);
	}
}
