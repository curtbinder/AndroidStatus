package info.curtbinder.reefangel.phone;

import android.annotation.TargetApi;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.util.Log;

/**
 * Created by binder on 3/22/14.
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class PrefAutoUpdateFragment extends PreferenceFragment
        implements SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String TAG = PrefAutoUpdateFragment.class.getSimpleName();

    private RAApplication raApp;
    private RAPreferences raPrefs;
    private Preference updateprofilekey;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        raApp = (RAApplication) getActivity().getApplication();
        raPrefs = raApp.raprefs;

        // load the preferences from an XML file
        addPreferencesFromResource(R.xml.pref_autoupdate);

        updateprofilekey =
                findPreference(raApp
                        .getString(R.string.prefAutoUpdateProfileKey));
        updateprofilekey.setSummary(getUpdateProfileDisplay());
        updateAutoUpdateProfileVisibility(raApp.isAwayProfileEnabled());
        updateAutoUpdateIntervalSummary();
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

    private void updateAutoUpdateIntervalSummary() {
        findPreference(raApp.getString(R.string.prefAutoUpdateIntervalKey))
                .setSummary(getUpdateIntervalDisplay());
    }

    private void updateAutoUpdateProfileVisibility(boolean fVisible) {
        updateprofilekey.setEnabled(fVisible);
        updateprofilekey.setSelectable(fVisible);
    }

    private String getUpdateIntervalDisplay() {
        return ((SettingsActivity)getActivity()).getDisplayValueLong(
                raPrefs.getUpdateInterval(),
                R.array.updateIntervalValues,
                R.array.updateInterval);
    }

    public String getUpdateProfileDisplay() {
        return ((SettingsActivity) getActivity()).getDisplayValue(raPrefs.getUpdateProfile(),
                R.array.updateProfileValues, R.array.updateProfile);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(raApp
                .getString(R.string.prefAutoUpdateIntervalKey))) {
            // when interval changes, update the repeat service
            updateAutoUpdateIntervalSummary();
            raApp.restartAutoUpdateService();
            boolean fVisible = false;
            if (raApp.isAwayProfileEnabled()
                    && (raPrefs.getUpdateInterval() > 0)) {
                Log.d(TAG, "enable update profile");
                fVisible = true;
            }
            updateAutoUpdateProfileVisibility(fVisible);
        } else if (key.equals(raApp
                .getString(R.string.prefAutoUpdateProfileKey))) {
            // restart the update service if we change the update profile
            updateprofilekey.setSummary(getUpdateProfileDisplay());
            raApp.restartAutoUpdateService();
        }
    }
}
