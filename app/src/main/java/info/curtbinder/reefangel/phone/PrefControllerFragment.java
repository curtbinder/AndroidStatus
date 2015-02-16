/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Curt Binder
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
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.util.Log;
import android.widget.Toast;

import info.curtbinder.reefangel.service.MessageCommands;
import info.curtbinder.reefangel.service.UpdateService;

/**
 * Created by binder on 2/15/15.
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class PrefControllerFragment extends PreferenceFragment {

    private static final String TAG = PrefControllerFragment.class.getSimpleName();

    private RAApplication raApp;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        raApp = (RAApplication) getActivity().getApplication();

        // load the preferences from an XML file
        addPreferencesFromResource(R.xml.pref_controller);

        // set onclicklistener for downloading the labels
        Preference p = findPreference(raApp.getString(R.string.prefControllerLabelsDownloadKey));
        p.setOnPreferenceClickListener(new DownloadLabelsPreferenceListener());

        // update the download label summary
        updateDownloadLabelUserId(raApp.raprefs.getUserId());
    }

    private void updateDownloadLabelUserId(String userId) {
        CharSequence cs = raApp.getString(R.string.prefControllerLabelsDownloadSummary)
                + " " + userId;
        findPreference(raApp.getString(R.string.prefControllerLabelsDownloadKey)).setSummary(cs);
    }

    class DownloadLabelsPreferenceListener implements Preference.OnPreferenceClickListener {

        @Override
        public boolean onPreferenceClick(Preference preference) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage(raApp.getString(R.string.messageDownloadLabelsPrompt)
                    + " " + raApp.raprefs.getUserId() + "?")
                    .setCancelable(false)
                    .setPositiveButton(raApp.getString(R.string.buttonYes),
                            new DialogInterface.OnClickListener() {
                                public void onClick(
                                        DialogInterface dialog,
                                        int id) {
                                    // launch download
                                    Log.d(TAG, "Download labels");
                                    Intent i = new Intent(raApp, UpdateService.class);
                                    i.setAction(MessageCommands.LABEL_QUERY_INTENT);
                                    raApp.startService(i);
                                    dialog.dismiss();
                                    Toast.makeText(getActivity(),
                                            raApp.getString(R.string.messageDownloadLabels),
                                            Toast.LENGTH_SHORT)
                                            .show();
                                }
                            })
                    .setNegativeButton(raApp.getString(R.string.buttonNo),
                            new DialogInterface.OnClickListener() {
                                public void onClick(
                                        DialogInterface dialog,
                                        int id) {
                                    Log.d(TAG, "Cancel download");
                                    dialog.cancel();
                                }
                            });

            AlertDialog alert = builder.create();
            alert.show();
            return true;
        }

    }
}
