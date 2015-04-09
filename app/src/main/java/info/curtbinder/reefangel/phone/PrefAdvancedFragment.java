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
import android.preference.PreferenceFragment;


/**
 * Created by binder on 3/22/14.
 */
public class PrefAdvancedFragment extends PreferenceFragment
        implements SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String TAG = PrefAdvancedFragment.class.getSimpleName();

    private RAApplication raApp;
    private RAPreferences raPrefs;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        PrefSetTitleListener prefSetTitleListener = (PrefSetTitleListener) activity;
        prefSetTitleListener.setToolbarTitle(getString(R.string.prefsCategoryAdvanced));
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        raApp = (RAApplication) getActivity().getApplication();
        raPrefs = raApp.raprefs;

        // load the preferences from an XML file
        addPreferencesFromResource(R.xml.pref_advanced);

        updateConnectionTimeoutSummary();
        updateReadTimeoutSummary();

//        Preference p = findPreference(raApp.getString(R.string.prefResetAllPrefsKey));
//        p.setOnPreferenceClickListener(new ResetAllPreferenceListener());
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

    // This code is not working. I believe it may be too deep to work properly and additional
    // calls must be made
//    class ResetAllPreferenceListener implements Preference.OnPreferenceClickListener {
//
//        @Override
//        public boolean onPreferenceClick(Preference preference) {
//            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//            builder.setMessage(raApp.getString(R.string.messageResetAllPrefsPrompt))
//                    .setCancelable(false)
//                    .setPositiveButton(raApp.getString(R.string.buttonYes),
//                            new DialogInterface.OnClickListener() {
//                                public void onClick(
//                                        DialogInterface dialog,
//                                        int id) {
//                                    Log.d(TAG, "Delete All Prefs");
//                                    raApp.raprefs.clearAllAppPreferences();
//                                    Log.d(TAG, "finish activity with good exit");
//                                    getActivity().finishActivity(MainActivity.RESULT_EXIT);
//                                    Log.d(TAG, "dismiss dialog");
//                                    dialog.dismiss();
//                                }
//                            })
//                    .setNegativeButton(raApp.getString(R.string.buttonNo),
//                            new DialogInterface.OnClickListener() {
//                                public void onClick(
//                                        DialogInterface dialog,
//                                        int id) {
//                                    Log.d(TAG, "Cancel");
//                                    dialog.cancel();
//                                }
//                            });
//
//            AlertDialog alert = builder.create();
//            alert.show();
//            return true;
//        }
//    }
}
