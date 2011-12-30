package info.curtbinder.reefangel.phone;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

public class RAApplication extends Application {

	private static final String TAG = RAApplication.class.getSimpleName();
	private SharedPreferences prefs;
	// Error code stuff
	private String[] errorCodes;
	private String[] errorCodesStrings;
	private int errorCode;
	// Devices stuff
	private String[] devicesArray;

	// Controller Data
	private RAData data;

	// Service Stuff
	public boolean isServiceRunning;

	@Override
	public void onCreate() {
		prefs = PreferenceManager.getDefaultSharedPreferences(this);
		errorCodes = getResources().getStringArray(R.array.errorCodes);
		errorCodesStrings = getResources().getStringArray(
				R.array.errorCodesStrings);
		errorCode = 0; // set to no error initially
		data = new RAData(this);
		devicesArray = getResources().getStringArray(R.array.devicesValues);
		isServiceRunning = false;
	}

	@Override
	public void onTerminate() {
		super.onTerminate();
		data.close();
	}

	// Data handling
	public RAData getRAData() {
		return data;
	}

	// Error Logging
	public void error(int errorCodeIndex, Throwable t, String msg) {
		errorCode = Integer.parseInt(errorCodes[errorCodeIndex]);
		Log.e(TAG, msg, t);
	}

	public String getErrorMessage() {
		String s = (String) getResources()
				.getText(R.string.messageUnknownError);
		// loop through array of error codes and match with the current code
		for (int i = 0; i < errorCodes.length; i++) {
			if (Integer.parseInt(errorCodes[i]) == errorCode) {
				// found code
				s = String.format("%s %d: %s",
						getResources().getText(R.string.messageError),
						errorCode, errorCodesStrings[i]);
				break;
			}
		}
		return s;
	}

	// Preferences
	public boolean isCommunicateController() {
		boolean b = false;
		if (getPrefDevice().equals(devicesArray[0])) {
			b = true;
		}
		return b;
	}

	public String getPrefHost() {
		return prefs.getString(getString(R.string.prefHostKey),
				getString(R.string.prefHostDefault));
	}

	public String getPrefPort() {
		return prefs.getString(getString(R.string.prefPortKey),
				getString(R.string.prefPortDefault));
	}

	public boolean getPrefT2Visibility() {
		return prefs.getBoolean(getString(R.string.prefT2VisibilityKey), true);
	}

	public boolean getPrefT3Visibility() {
		return prefs.getBoolean(getString(R.string.prefT3VisibilityKey), true);
	}

	public boolean getPrefDPVisibility() {
		return prefs.getBoolean(getString(R.string.prefDPVisibilityKey), true);
	}

	public boolean getPrefAPVisibility() {
		return prefs.getBoolean(getString(R.string.prefAPVisibilityKey), true);
	}

	public boolean getPrefSalinityVisibility() {
		return prefs.getBoolean(getString(R.string.prefSalinityVisibilityKey),
				false);
	}

	public String getPrefT1Label() {
		return prefs.getString(getString(R.string.prefT1LabelKey),
				getString(R.string.temp1_label));
	}

	public String getPrefT2Label() {
		return prefs.getString(getString(R.string.prefT2LabelKey),
				getString(R.string.temp2_label));
	}

	public String getPrefT3Label() {
		return prefs.getString(getString(R.string.prefT3LabelKey),
				getString(R.string.temp3_label));
	}

	public CharSequence getPrefPHLabel() {
		return prefs.getString(getString(R.string.prefPHLabelKey),
				getString(R.string.ph_label));
	}

	public String getPrefDPLabel() {
		return prefs.getString(getString(R.string.prefDPLabelKey),
				getString(R.string.dp_label));
	}

	public String getPrefAPLabel() {
		return prefs.getString(getString(R.string.prefAPLabelKey),
				getString(R.string.ap_label));
	}

	public String getPrefMainRelayLabel(int port) {
		String key;
		String defaultValue;
		switch (port) {
		default:
		case 1:
			key = getString(R.string.prefMainPort1LabelKey);
			defaultValue = getString(R.string.port1_label);
			break;
		case 2:
			key = getString(R.string.prefMainPort2LabelKey);
			defaultValue = getString(R.string.port2_label);
			break;
		case 3:
			key = getString(R.string.prefMainPort3LabelKey);
			defaultValue = getString(R.string.port3_label);
			break;
		case 4:
			key = getString(R.string.prefMainPort4LabelKey);
			defaultValue = getString(R.string.port4_label);
			break;
		case 5:
			key = getString(R.string.prefMainPort5LabelKey);
			defaultValue = getString(R.string.port5_label);
			break;
		case 6:
			key = getString(R.string.prefMainPort6LabelKey);
			defaultValue = getString(R.string.port6_label);
			break;
		case 7:
			key = getString(R.string.prefMainPort7LabelKey);
			defaultValue = getString(R.string.port7_label);
			break;
		case 8:
			key = getString(R.string.prefMainPort8LabelKey);
			defaultValue = getString(R.string.port8_label);
			break;
		}
		return prefs.getString(key, defaultValue);
	}

	public String getPrefDevice() {
		return prefs.getString(getString(R.string.prefDeviceKey),
				getString(R.string.prefDeviceDefault));
	}

	public String getPrefUserId() {
		return prefs.getString(getString(R.string.prefUserIdKey),
				getString(R.string.prefUserIdDefault));
	}
}
