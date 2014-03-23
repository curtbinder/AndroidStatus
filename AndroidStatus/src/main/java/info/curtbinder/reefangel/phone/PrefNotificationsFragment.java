package info.curtbinder.reefangel.phone;

import android.annotation.TargetApi;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;

/**
 * Created by binder on 3/22/14.
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class PrefNotificationsFragment extends PreferenceFragment
        implements SharedPreferences.OnSharedPreferenceChangeListener {

    private RAApplication raApp;
    private RAPreferences raPrefs;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        raApp = (RAApplication) getActivity().getApplication();
        raPrefs = raApp.raprefs;

        // load the preferences from an XML file
        addPreferencesFromResource(R.xml.pref_notifications);

        updateErrorRetryCountSummary();
        updateErrorRetryIntervalSummary();
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    private void updateErrorRetryCountSummary() {
        String[] sa =
                raApp.getResources().getStringArray(R.array.errorRetryCount);
        int count = raPrefs.getNotificationErrorRetryMax();
        Preference pe =
                findPreference(raApp
                        .getString(R.string.prefNotificationErrorRetryKey));
        pe.setSummary(sa[count]);

        // only enable the retry interval if we are supposed to retry
        // AND if we are enabled
        boolean fEnable = false;
        if ((pe.isEnabled()) && (count > Globals.errorRetryNone))
            fEnable = true;
        Preference p =
                findPreference(raApp.getString(R.string.prefNotificationErrorRetryIntervalKey));
        p.setEnabled(fEnable);
        p.setSelectable(fEnable);
    }

    private void updateErrorRetryIntervalSummary() {
        String s = ((SettingsActivity) getActivity()).getDisplayValueLong(
                raPrefs.getNotificationErrorRetryInterval(),
                R.array.errorRetryIntervalValues,
                R.array.errorRetryInterval);
        findPreference(
                raApp.getString(R.string.prefNotificationErrorRetryIntervalKey))
                .setSummary(s);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(raApp
                .getString(R.string.prefNotificationErrorRetryKey))) {
            // error retry count changed, update the summary and
            // visibility of the retry interval
            updateErrorRetryCountSummary();
        } else if (key.equals(raApp
                .getString(R.string.prefNotificationErrorRetryIntervalKey))) {
            // interval changed
            updateErrorRetryIntervalSummary();
        }
    }
}
