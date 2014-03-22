package info.curtbinder.reefangel.phone;

import android.annotation.TargetApi;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceFragment;

/**
 * Created by binder on 3/22/14.
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class PrefAdvancedFragment extends PreferenceFragment
        implements SharedPreferences.OnSharedPreferenceChangeListener {

    private RAApplication raApp;
    private RAPreferences raPrefs;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        raApp = (RAApplication) getActivity().getApplication();
        raPrefs = raApp.raprefs;

        // load the preferences from an XML file
        addPreferencesFromResource(R.xml.pref_advanced);

        updateConnectionTimeoutSummary();
        updateReadTimeoutSummary();
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

    private void updateConnectionTimeoutSummary() {
        findPreference(raApp.getString(R.string.prefConnectionTimeoutKey))
                .setSummary(((SettingsActivity) getActivity())
                        .getDisplayValue(raPrefs.getConnectionTimeout(),
                                R.array.networkTimeoutValues,
                                R.array.networkTimeout));
    }

    private void updateReadTimeoutSummary() {
        findPreference(raApp.getString(R.string.prefReadTimeoutKey))
                .setSummary(((SettingsActivity) getActivity())
                        .getDisplayValue(raPrefs.getReadTimeout(),
                                R.array.networkTimeoutValues,
                                R.array.networkTimeout));
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(raApp.getString(R.string.prefConnectionTimeoutKey))) {
            updateConnectionTimeoutSummary();
        } else if (key.equals(raApp.getString(R.string.prefReadTimeoutKey))) {
            updateReadTimeoutSummary();
        }
    }
}
