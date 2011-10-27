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
}
