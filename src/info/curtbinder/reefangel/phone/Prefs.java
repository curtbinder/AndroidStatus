package info.curtbinder.reefangel.phone;

import android.content.Context;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

public class Prefs extends PreferenceActivity {
	private static final String OPT_HOST = "host";
	private static final String OPT_HOST_DEF = "curtbinder.dyndns.info";
	private static final String OPT_PORT = "port";
	private static final String OPT_PORT_DEF = "2000";
	private static final String OPT_T2_VIS = "t2_visibility";
	private static final String OPT_T3_VIS = "t3_visibility";
	private static final String OPT_DP_VIS = "dp_visibility";
	private static final String OPT_AP_VIS = "ap_visibility";
	private static final String OPT_SALINITY_VIS = "salinity_visibility";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.settings);
	}
	
	public static String getHost(Context context) {
		return PreferenceManager.getDefaultSharedPreferences(context).getString(OPT_HOST, OPT_HOST_DEF);
	}
	
	public static String getPort(Context context) {
		return PreferenceManager.getDefaultSharedPreferences(context).getString(OPT_PORT, OPT_PORT_DEF);
	}
	
	public static boolean getT2Visibility(Context context) {
		return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(OPT_T2_VIS, true);
	}
	
	public static boolean getT3Visibility(Context context) {
		return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(OPT_T3_VIS, true);
	}
	
	public static boolean getDPVisibility(Context context) {
		return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(OPT_DP_VIS, true);
	}
	
	public static boolean getAPVisibility(Context context) {
		return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(OPT_AP_VIS, true);
	}
	
	public static boolean getSalinityVisibility(Context context) {
		return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(OPT_SALINITY_VIS, false);
	}
}
