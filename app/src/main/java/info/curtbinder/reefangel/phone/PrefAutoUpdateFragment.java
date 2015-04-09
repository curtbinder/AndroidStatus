/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Curt Binder
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package info.curtbinder.reefangel.phone;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.util.Log;

/**
 * Created by binder on 3/22/14.
 */
public class PrefAutoUpdateFragment extends PreferenceFragment
        implements SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String TAG = PrefAutoUpdateFragment.class.getSimpleName();

    private RAApplication raApp;
    private RAPreferences raPrefs;
    private Preference updateprofilekey;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        PrefSetTitleListener prefSetTitleListener = (PrefSetTitleListener) activity;
        prefSetTitleListener.setToolbarTitle(getString(R.string.prefAutoUpdateCategory));
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        raApp = (RAApplication) getActivity().getApplication();
        raPrefs = raApp.raprefs;

        // load the preferences from an XML file
        addPreferencesFromResource(R.xml.pref_autoupdate);

        updateprofilekey = findPreference(raApp.getString(R.string.prefAutoUpdateProfileKey));
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
        return ((SettingsActivity) getActivity()).getDisplayValueLong(
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
