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

/**
 * Created by binder on 3/22/14.
 */
public class PrefNotificationsFragment extends PreferenceFragment
        implements SharedPreferences.OnSharedPreferenceChangeListener {

    private RAApplication raApp;
    private RAPreferences raPrefs;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        PrefSetTitleListener prefSetTitleListener = (PrefSetTitleListener) activity;
        prefSetTitleListener.setToolbarTitle(getString(R.string.prefNotificationCategory));
    }

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
