package info.curtbinder.reefangel.phone;

import android.content.Context;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

public class Prefs extends PreferenceActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.settings);
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
}
