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
public class PrefProfileFragment extends PreferenceFragment
        implements Preference.OnPreferenceChangeListener,
        SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String TAG = PrefProfileFragment.class.getSimpleName();

    private RAApplication raApp;
    private RAPreferences raPrefs;
    private Preference profilekey;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        PrefSetTitleListener prefSetTitleListener = (PrefSetTitleListener) activity;
        prefSetTitleListener.setToolbarTitle(PrefLoadFragListener.PREF_PROFILE);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        raApp = (RAApplication) getActivity().getApplication();
        raPrefs = raApp.raprefs;

        // load the preferences from an XML file
        addPreferencesFromResource(R.xml.pref_profiles);

        // set the device label
        updateDeviceKeySummary();

        findPreference(raApp.getString(R.string.prefPortKey)).setOnPreferenceChangeListener(this);
        findPreference(raApp.getString(R.string.prefHostKey)).setOnPreferenceChangeListener(this);
        findPreference(raApp.getString(R.string.prefPortAwayKey)).setOnPreferenceChangeListener(this);
        findPreference(raApp.getString(R.string.prefHostAwayKey)).setOnPreferenceChangeListener(this);
        findPreference(raApp.getString(R.string.prefUserIdKey)).setOnPreferenceChangeListener(this);
        profilekey = findPreference(raApp.getString(R.string.prefProfileSelectedKey));

        updateSelectedProfileVisibility();
        updateSelectedProfileSummary();
        updateHostsSummary();
        updateUserIdSummary(raPrefs.getUserId());

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

    private void updateDeviceKeySummary() {
        String s;
        if (raPrefs.isCommunicateController()) {
            // 0 index is controller
            s = ((SettingsActivity) getActivity()).getDevicesArrayValue(0);
        } else {
            // 1 index is Portal
            s = ((SettingsActivity) getActivity()).getDevicesArrayValue(1);
        }
        findPreference(raApp.getString(R.string.prefDeviceKey)).setSummary(s);
    }

    private void updateSelectedProfileVisibility() {
        boolean fEnable = false;
        if (raApp.isAwayProfileEnabled() && raPrefs.isCommunicateController()) {
            fEnable = true;
        }
        profilekey.setEnabled(fEnable);
        profilekey.setSelectable(fEnable);
    }

    private void updateSelectedProfileSummary() {
        profilekey.setSummary(((SettingsActivity) getActivity()).getProfilesArrayValue(raApp.getSelectedProfile()));
    }

    private void updateHostsSummary() {
        updateHomeHostSummary();
        updateHomePortSummary();
        updateAwayHostSummary();
        updateAwayPortSummary();
    }

    private void updateHomeHostSummary() {
        findPreference(raApp.getString(R.string.prefHostKey)).setSummary(raPrefs.getHomeHost());
    }

    private void updateHomePortSummary() {
        findPreference(raApp.getString(R.string.prefPortKey)).setSummary(raPrefs.getHomePort());
    }

    private void updateAwayHostSummary() {
        findPreference(raApp.getString(R.string.prefHostAwayKey)).setSummary(raPrefs.getAwayHost());
    }

    private void updateAwayPortSummary() {
        findPreference(raApp.getString(R.string.prefPortAwayKey)).setSummary(raPrefs.getAwayPort());
    }

    private void updateUserIdSummary(String s) {
        // on this preference screen
        findPreference(raApp.getString(R.string.prefUserIdKey)).setSummary(s);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        Log.d(TAG, "onPreferenceChange");

        // return true to change, false to not
        if (preference.getKey().equals(raApp.getString(R.string.prefPortKey))) {
            return raApp.validatePort(newValue);
        } else if (preference.getKey().equals(raApp.getString(R.string.prefHostKey))) {
            return raApp.validateHost(newValue);
        } else if (preference.getKey().equals(raApp.getString(R.string.prefPortAwayKey))) {
            return raApp.validatePort(newValue);
        } else if (preference.getKey().equals(raApp.getString(R.string.prefHostAwayKey))) {
            Log.d(TAG, "Change away host: " + newValue.toString());
            // Away Host can be empty
            if (newValue.toString().equals("")) {
                return true;
            }
            return raApp.validateHost(newValue);
        } else if (preference.getKey().equals(raApp.getString(R.string.prefUserIdKey))) {
            if (!raApp.validateUser(newValue))
                return false;

        }
        return true;
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Log.d(TAG, "onSharedPreferenceChanged");

        if (key.equals(raApp.getString(R.string.prefHostKey)) || key.equals(raApp.getString(R.string.prefPortKey))) {
            homeHostChanged();
        } else if (key.equals(raApp.getString(R.string.prefPortAwayKey))
                || key.equals(raApp.getString(R.string.prefHostAwayKey))) {
            awayHostChanged(key);
        } else if (key.equals(raApp.getString(R.string.prefUserIdKey))) {
            userIdChanged();
        } else if (key.equals(raApp.getString(R.string.prefProfileSelectedKey))) {
            updateProfileChanged();
        } else if (key.equals(raApp.getString(R.string.prefDeviceKey))) {
            deviceChanged();
        }
    }

    private void homeHostChanged() {
        updateHomeHostSummary();
        updateHomePortSummary();
        // only restart service if:
        // - enabled (interval > 0)
        // -- away profile enabled AND the profile is not only away
        // -- away profile disabled
        if (raPrefs.getUpdateInterval() > 0
                && raPrefs.isCommunicateController()) {
            if (raApp.isAwayProfileEnabled()) {
                if (raPrefs.getUpdateProfile() != Globals.profileOnlyAway) {
                    Log.d(TAG,
                            "away enabled: restart based on home host & port");
                    raApp.restartAutoUpdateService();
                }
            } else {
                Log.d(TAG,
                        "away disabled: restart based on home host & port");
                raApp.restartAutoUpdateService();
            }
        }
    }

    private void awayHostChanged(String key) {
        updateAwayHostSummary();
        updateAwayPortSummary();
        // only restart service if:
        // - away profile enabled
        // - enabled (interval > 0) AND the profile is not only home
        boolean f = false;
        if (raApp.isAwayProfileEnabled()) {
            // only restart service if it's enabled AND
            // the profile is not only home
            if (raPrefs.isCommunicateController()) {
                if ((raPrefs.getUpdateInterval() > 0)
                        && (raPrefs.getUpdateProfile() != Globals.profileOnlyHome)) {
                    Log.d(TAG, "restart based on away host & port");
                    raApp.restartAutoUpdateService();
                }
            }
            f = true;
        } else {
            // user cleared the away host, disabling profiles
            if (key.equals(raApp.getString(R.string.prefHostAwayKey))) {
                // clear out the profiles
                raPrefs.setSelectedProfile(Globals.profileHome);
                if (raPrefs.isCommunicateController())
                    // only restart if a controller
                    raApp.restartAutoUpdateService();
            }
        }
//        updateAutoUpdateProfileVisibility(f);
        updateSelectedProfileVisibility();
    }

    private void userIdChanged() {
        updateUserIdSummary(raPrefs.getUserId());
        if ((raPrefs.getUpdateInterval() > 0)
                && (!raPrefs.isCommunicateController())) {
            Log.d(TAG, "restart based on userid changing");
            raApp.restartAutoUpdateService();
        }
    }

    private void updateProfileChanged() {
        if (raPrefs.getUpdateInterval() > 0) {
            Log.d(TAG, "profile changed, restart");
            raApp.restartAutoUpdateService();
        }
        updateSelectedProfileSummary();
    }

    private void deviceChanged() {
        // device changes
        boolean f = false;
        if (raPrefs.isCommunicateController()
                && raApp.isAwayProfileEnabled())
            f = true;
        // only restart if there is an interval
        if (raPrefs.getUpdateInterval() > 0)
            raApp.restartAutoUpdateService();
//        updateAutoUpdateProfileVisibility(f);
        updateSelectedProfileVisibility();
        updateDeviceKeySummary();
    }
}
