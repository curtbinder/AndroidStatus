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

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import info.curtbinder.reefangel.service.MessageCommands;

public class SettingsActivity extends AppCompatActivity
implements PrefLoadFragListener, PrefSetTitleListener {

    private static final String TAG = SettingsActivity.class.getSimpleName();

    private RAApplication raApp;
    private PrefsReceiver receiver;
    private IntentFilter filter;
    private Toolbar mToolbar;

    private String[] devicesArray;
    private String[] profilesArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        raApp = (RAApplication) getApplication();
        // Set the Theme before the layout is instantiated
        //Utils.onActivityCreateSetTheme(this, raApp.raprefs.getSelectedTheme());
        setContentView(R.layout.activity_settings);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        // Enable the Back/Up button on the toolbar to allow for the user to press the button
        // to exit this activity. Otherwise, they must press the actual back button on the device.
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateBackwards();
            }
        });

        devicesArray = raApp.getResources().getStringArray(R.array.devices);
        profilesArray = raApp.getResources().getStringArray(R.array.profileLabels);

        receiver = new PrefsReceiver();
        filter = new IntentFilter(MessageCommands.LABEL_RESPONSE_INTENT);


        loadFragment(0);
    }

    public String getDevicesArrayValue(int index) {
        return devicesArray[index];
    }

    public String getProfilesArrayValue(int index) {
        return profilesArray[index];
    }

    public String getDisplayValue(int v, int arrayValuesId, int arrayDisplayId) {
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

    public String getDisplayValueLong(long v, int arrayValuesId, int arrayDisplayId) {
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

    public void updateDownloadLabelUserId(Preference p) {
        CharSequence cs = raApp.getString(R.string.prefControllerLabelsDownloadSummary)
                + " " + raApp.raprefs.getUserId();
        p.setSummary(cs);
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

    class PrefsReceiver extends BroadcastReceiver {

        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "Warn about labels");
            AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this, R.style.AlertDialogStyle);
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

    public void loadFragment(int id) {
        PreferenceFragment pf = null;
        String title = getFragTitle(id);
        switch(id){
            default:
            case PrefLoadFragListener.PREF_HEADERS:
                pf = new PrefHeadersFragment();
                break;
            case PrefLoadFragListener.PREF_PROFILE:
                pf = new PrefProfileFragment();
                break;
            case PrefLoadFragListener.PREF_CONTROLLER:
                pf = new PrefControllerFragment();
                break;
            case PrefLoadFragListener.PREF_AUTOUPDATE:
                pf = new PrefAutoUpdateFragment();
                break;
            case PrefLoadFragListener.PREF_ADVANCED:
                pf = new PrefAdvancedFragment();
                break;
            case PrefLoadFragListener.PREF_NOTIFICATIONS:
                pf = new PrefNotificationsFragment();
                break;
            case PrefLoadFragListener.PREF_LOGGING:
                pf = new PrefLoggingFragment();
                break;
            case PrefLoadFragListener.PREF_APP:
                pf = new PrefAppFragment();
                break;

        }
        getFragmentManager().beginTransaction()
                .replace(R.id.container, pf)
                .addToBackStack(title)
                .commit();
    }

    public void setToolbarTitle(String title) {
        if (mToolbar == null)
            return;
        mToolbar.setTitle(title);
    }

    public void setToolbarTitle(int id) {
        setToolbarTitle(getFragTitle(id));
    }

    protected String getFragTitle(int id) {
        String title = "";
        switch(id){
            default:
            case PrefLoadFragListener.PREF_HEADERS:
                title = getString(R.string.menuMainSettings);
                break;
            case PrefLoadFragListener.PREF_PROFILE:
                title = getString(R.string.prefsCategoryProfiles);
                break;
            case PrefLoadFragListener.PREF_CONTROLLER:
                title = getString(R.string.prefsCategoryController);
                break;
            case PrefLoadFragListener.PREF_AUTOUPDATE:
                title = getString(R.string.prefAutoUpdateCategory);
                break;
            case PrefLoadFragListener.PREF_ADVANCED:
                title = getString(R.string.prefsCategoryAdvanced);
                break;
            case PrefLoadFragListener.PREF_NOTIFICATIONS:
                title = getString(R.string.prefNotificationCategory);
                break;
            case PrefLoadFragListener.PREF_LOGGING:
                title = getString(R.string.prefLoggingCategory);
                break;
            case PrefLoadFragListener.PREF_APP:
                title = getString(R.string.prefsCategoryApp);
                break;
        }
        return title;
    }

    @Override
    public void onBackPressed() {
        navigateBackwards();
    }

    protected void navigateBackwards() {
        FragmentManager fm = getFragmentManager();
        int count = fm.getBackStackEntryCount();
        if (count > 1) {
            // pop the backstack
            fm.popBackStack();
            // Get the second to last item on the backstack
            FragmentManager.BackStackEntry be = fm.getBackStackEntryAt(count-2);
            // set the toolbar title based on the name added to the backstack
            Log.d(TAG, "Back to:  " + be.getName());
            setToolbarTitle(be.getName());
        } else {
            finish();
        }
    }
}
