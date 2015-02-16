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

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.util.Log;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import info.curtbinder.reefangel.service.MessageCommands;

public class SettingsActivity extends PreferenceActivity {

    private static final String TAG = SettingsActivity.class.getSimpleName();

    protected Method mLoadHeaders = null;
    protected Method mHasHeaders = null;

    private RAApplication raApp;
    //private RAPreferences raPrefs;
    private PrefsReceiver receiver;
    private IntentFilter filter;

    private String[] devicesArray;
    private String[] profilesArray;

    /**
     * Checks to see if using the new v11+ way of handling PrefsFragments.
     *
     * @return Returns false pre-v11, else checks to see if using headers
     */
    public boolean isNewV11Prefs() {
        if (mHasHeaders != null && mLoadHeaders != null) {
            try {
                return (Boolean) mHasHeaders.invoke(this);
            } catch (IllegalArgumentException e) {

            } catch (IllegalAccessException e) {

            } catch (InvocationTargetException e) {

            }
        }
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // onBuildHeaders() will be called during super.onCreate()
        try {
            mLoadHeaders = getClass().getMethod("loadHeadersFromResource", int.class, List.class);
            mHasHeaders = getClass().getMethod("hasHeaders");
        } catch (NoSuchMethodException e) {

        }
        super.onCreate(savedInstanceState);

        raApp = (RAApplication) getApplication();
        //raPrefs = raApp.raprefs;

        devicesArray = raApp.getResources().getStringArray(R.array.devices);
        profilesArray = raApp.getResources().getStringArray(R.array.profileLabels);

        receiver = new PrefsReceiver();
        filter = new IntentFilter(MessageCommands.LABEL_RESPONSE_INTENT);

        if (!isNewV11Prefs()) {
            addPreferencesFromResource(R.xml.pref_profiles);
            addPreferencesFromResource(R.xml.pref_controller);
            addPreferencesFromResource(R.xml.pref_advanced);
            addPreferencesFromResource(R.xml.pref_notifications);
            addPreferencesFromResource(R.xml.pref_logging);
            addPreferencesFromResource(R.xml.pref_appinfo);
            Preference changelog = findPreference(raApp.getString(R.string.prefChangelogKey));
//            changelog.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
//                public boolean onPreferenceClick(Preference preference) {
//                    raApp.displayChangeLog(getParent());
//                    return true;
//                }
//            });
            changelog.setEnabled(false);
        }
    }

    public String getDevicesArrayValue(int index) {
        return devicesArray[index];
    }

    public String getProfilesArrayValue(int index) {
        return profilesArray[index];
    }

    public String getDisplayValue(
            int v,
            int arrayValuesId,
            int arrayDisplayId) {
        int pos = 0;
        String[] values = raApp.getResources().getStringArray(arrayValuesId);
        String[] display = raApp.getResources().getStringArray(arrayDisplayId);
        for (int i = 0; i < values.length; i++) {
            if (Integer.parseInt(values[i]) == v) {
                // found value
                pos = i;
                break;
            }
        }
        return display[pos];
    }

    public String getDisplayValueLong(
            long v,
            int arrayValuesId,
            int arrayDisplayId) {
        int pos = 0;
        String[] values = raApp.getResources().getStringArray(arrayValuesId);
        String[] display = raApp.getResources().getStringArray(arrayDisplayId);
        for (int i = 0; i < values.length; i++) {
            if (Long.parseLong(values[i]) == v) {
                // found value
                pos = i;
                break;
            }
        }
        return display[pos];
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(receiver, filter, Permissions.SEND_COMMAND, null);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
    }

    @Override
    public void onBuildHeaders(List<Header> target) {
        try {
            mLoadHeaders.invoke(this, new Object[]{R.xml.pref_headers, target});
        } catch (IllegalArgumentException e) {

        } catch (IllegalAccessException e) {

        } catch (InvocationTargetException e) {

        }
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    protected boolean isValidFragment(String fragmentName) {
        return PrefProfileFragment.class.getName().equals(fragmentName) ||
                PrefControllerFragment.class.getName().equals(fragmentName) ||
                PrefAutoUpdateFragment.class.getName().equals(fragmentName) ||
                PrefAdvancedFragment.class.getName().equals(fragmentName) ||
                PrefNotificationsFragment.class.getName().equals(fragmentName) ||
                PrefLoggingFragment.class.getName().equals(fragmentName) ||
                PrefAppFragment.class.getName().equals(fragmentName) ||
                super.isValidFragment(fragmentName);
    }

    class PrefsReceiver extends BroadcastReceiver {

        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "Warn about labels");
            AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this);
            builder.setMessage(raApp.getString(R.string.messageDownloadMessage))
                    .setCancelable(false)
                    .setPositiveButton(raApp.getString(R.string.buttonOk),
                            new DialogInterface.OnClickListener() {
                                public void onClick(
                                        DialogInterface dialog,
                                        int id) {
                                    dialog.dismiss();
                                }
                            });

            AlertDialog alert = builder.create();
            alert.show();
        }
    }
}
