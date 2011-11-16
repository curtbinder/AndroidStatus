package info.curtbinder.reefangel.phone;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

public class Prefs extends PreferenceActivity implements OnPreferenceChangeListener {

	private static final String TAG = "RAPrefs";
	private static final String NUMBER_PATTERN = "\\d+";
	private static final String HOST_PATTERN = "^(?i:[[0-9][a-z]]+)(?i:[\\w\\.\\-]*)(?i:[[0-9][a-z]]+)$";
	private static final String USERID_PATTERN = "[\\w\\-\\.]+";
	
	private Preference portkey;
	private Preference hostkey;
	private Preference useridkey;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.settings);
		
		portkey = getPreferenceScreen().findPreference(getBaseContext().getString(R.string.prefPortKey));
		portkey.setOnPreferenceChangeListener(this);
		hostkey = getPreferenceScreen().findPreference(getBaseContext().getString(R.string.prefHostKey));
		hostkey.setOnPreferenceChangeListener(this);
		useridkey = getPreferenceScreen().findPreference(getBaseContext().getString(R.string.prefUserIdKey));
		useridkey.setOnPreferenceChangeListener(this);
		Preference devicekey = getPreferenceScreen().findPreference(getBaseContext().getString(R.string.prefDeviceKey));
		devicekey.setOnPreferenceChangeListener(this);
		Preference raWebsite = getPreferenceScreen().findPreference(getBaseContext().getString(R.string.prefReefAngelKey));
		raWebsite.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			
			@Override
			public boolean onPreferenceClick(Preference preference) {
				Intent browserIntent = new Intent(Intent.ACTION_VIEW, 
						Uri.parse(getBaseContext().getString(R.string.websiteReefangel)));
				startActivity(browserIntent);
				return true;
			}
		});
		Preference license = getPreferenceScreen().findPreference(getBaseContext().getString(R.string.prefLicenseKey));
		license.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			
			@Override
			public boolean onPreferenceClick(Preference preference) {
				startActivity(new Intent(getBaseContext(), License.class));
				return true;
			}
		});

		// TODO add in handler for Download All Labels click
		Preference download = getPreferenceScreen().findPreference(getBaseContext().getString(R.string.prefControllerLabelsDownloadKey));
		download.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(Preference preference) {
				
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
				return true;
			}
		});
		CharSequence cs = download.getSummary() + " " + getUserId(getBaseContext());
		download.setSummary(cs);
		
		// toggle the visibility of preferences based on device selection
		toggleDevicePrefVisibility(Integer.parseInt(getDevice(getBaseContext())));
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		Log.d(TAG, "Prefs Pause / Restart App");
		Intent i = new Intent(this, ReefAngelStatusActivity.class);
		i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(i);
		finish();
	}
		
	@Override
	public boolean onPreferenceChange(Preference preference, Object newValue) {
		// return true to change, false to not
		if ( preference.getKey().equals(preference.getContext().getString(R.string.prefPortKey))) {
			Log.d(TAG, "Validate entered port");
			if ( ! isNumber(newValue) ) {
				// not a number
				Log.d(TAG, "Invalid port");
				Toast.makeText(preference.getContext(), getResources().getString(R.string.messageNotNumber) + ": " + newValue.toString(), Toast.LENGTH_SHORT).show();				
				return false;
			} else {
				// it's a number, verify it's within range
				int min = Integer.parseInt((String)preference.getContext().getString(R.string.prefPortMin));
				int max = Integer.parseInt((String)preference.getContext().getString(R.string.prefPortMax));
				int v = Integer.parseInt((String)newValue.toString());
				
				// check if it's less than the min value or if it's greater than the max value
				if ( (v < min) || (v > max) ) {
					Log.d(TAG, "Invalid port range");
					Toast.makeText(preference.getContext(), getResources().getString(R.string.prefPortInvalidPort) + ": " + newValue.toString(), Toast.LENGTH_SHORT).show();
					return false;
				}
			}
		} else if ( preference.getKey().equals(preference.getContext().getString(R.string.prefHostKey))) {
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
				Toast.makeText(preference.getContext(), 
						getResources().getString(R.string.prefHostInvalidHost) + ": " + newValue.toString(), 
						Toast.LENGTH_SHORT).show();
				return false;
			}
		} else if ( preference.getKey().equals(preference.getContext().getString(R.string.prefDeviceKey)) ) {
			// enable / disable categories based on what the user selected
			Log.d(TAG, "Update enabled prefs");
			toggleDevicePrefVisibility(Integer.parseInt(newValue.toString()));
		} else if ( preference.getKey().equals(preference.getContext().getString(R.string.prefUserIdKey)) ) {
			String u = newValue.toString();
			if ( ! u.matches(USERID_PATTERN) ) {
				// invalid userid
				Log.d(TAG, "Invalid userid");
				Toast.makeText(preference.getContext(), 
						getResources().getString(R.string.prefUserIdInvalid) + ": " + newValue.toString(), 
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
		String[] devicesArray = getBaseContext().getResources().getStringArray(R.array.devicesValues);
		int devReefAngel = Integer.parseInt(devicesArray[1]);
		boolean boolEnableReefAngel = false;
		if ( d == devReefAngel ) {
			boolEnableReefAngel = true;
		}
		useridkey.setEnabled(boolEnableReefAngel);
		portkey.setEnabled(!boolEnableReefAngel);
		hostkey.setEnabled(!boolEnableReefAngel);
	}
	
	public static String getHost(Context context) {
		return PreferenceManager.getDefaultSharedPreferences(context).getString(
				context.getString(R.string.prefHostKey), 
				context.getString(R.string.prefHostDefault));
	}
	
	public static String getPort(Context context) {
		return PreferenceManager.getDefaultSharedPreferences(context).getString(
				context.getString(R.string.prefPortKey), 
				context.getString(R.string.prefPortDefault));
	}
	
	public static boolean getT2Visibility(Context context) {
		return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(
				context.getString(R.string.prefT2VisibilityKey), 
				true);
	}
	
	public static boolean getT3Visibility(Context context) {
		return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(
				context.getString(R.string.prefT3VisibilityKey), 
				true);
	}
	
	public static boolean getDPVisibility(Context context) {
		return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(
				context.getString(R.string.prefDPVisibilityKey),
				true);
	}
	
	public static boolean getAPVisibility(Context context) {
		return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(
				context.getString(R.string.prefAPVisibilityKey),
				true);
	}
	
	public static boolean getSalinityVisibility(Context context) {
		return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(
				context.getString(R.string.prefSalinityVisibilityKey),
				false);
	}
	
	public static String getT1Label(Context context) {
		return PreferenceManager.getDefaultSharedPreferences(context).getString(
				context.getString(R.string.prefT1LabelKey), 
				context.getString(R.string.temp1_label));
	}
	
	public static String getT2Label(Context context) {
		return PreferenceManager.getDefaultSharedPreferences(context).getString(
				context.getString(R.string.prefT2LabelKey), 
				context.getString(R.string.temp2_label));
	}
	
	public static String getT3Label(Context context) {
		return PreferenceManager.getDefaultSharedPreferences(context).getString(
				context.getString(R.string.prefT3LabelKey), 
				context.getString(R.string.temp3_label));
	}
	
	public static String getDPLabel(Context context) {
		return PreferenceManager.getDefaultSharedPreferences(context).getString(
				context.getString(R.string.prefDPLabelKey), 
				context.getString(R.string.dp_label));
	}
	
	public static String getAPLabel(Context context) {
		return PreferenceManager.getDefaultSharedPreferences(context).getString(
				context.getString(R.string.prefAPLabelKey), 
				context.getString(R.string.ap_label));
	}
	
	public static String getMainRelayLabel(Context context, int port) {
		String key;
		String defaultValue;
		switch ( port ) {
		default:
		case 1:
			key = context.getString(R.string.prefMainPort1LabelKey);
			defaultValue = context.getString(R.string.port1_label);
			break;
		case 2:
			key = context.getString(R.string.prefMainPort2LabelKey);
			defaultValue = context.getString(R.string.port2_label);
			break;
		case 3:
			key = context.getString(R.string.prefMainPort3LabelKey);
			defaultValue = context.getString(R.string.port3_label);
			break;
		case 4:
			key = context.getString(R.string.prefMainPort4LabelKey);
			defaultValue = context.getString(R.string.port4_label);
			break;
		case 5:
			key = context.getString(R.string.prefMainPort5LabelKey);
			defaultValue = context.getString(R.string.port5_label);
			break;
		case 6:
			key = context.getString(R.string.prefMainPort6LabelKey);
			defaultValue = context.getString(R.string.port6_label);
			break;
		case 7:
			key = context.getString(R.string.prefMainPort7LabelKey);
			defaultValue = context.getString(R.string.port7_label);
			break;
		case 8:
			key = context.getString(R.string.prefMainPort8LabelKey);
			defaultValue = context.getString(R.string.port8_label);
			break;
		}
		return PreferenceManager.getDefaultSharedPreferences(context).getString(
				key, defaultValue);
	}
	
	public static String getDevice(Context context) {
		return PreferenceManager.getDefaultSharedPreferences(context).getString(
				context.getString(R.string.prefDeviceKey),
				context.getString(R.string.prefDeviceDefault));
	}
	
	public static String getUserId(Context context) {
		return PreferenceManager.getDefaultSharedPreferences(context).getString(
				context.getString(R.string.prefUserIdKey),
				context.getString(R.string.prefUserIdDefault));
	}
}
