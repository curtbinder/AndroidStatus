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

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;

/**
 * Created by binder on 3/22/14.
 */
public class PrefLoggingFragment extends PreferenceFragment
        implements SharedPreferences.OnSharedPreferenceChangeListener {

    private RAApplication raApp;
    private RAPreferences raPrefs;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        raApp = (RAApplication) getActivity().getApplication();
        raPrefs = raApp.raprefs;

        // load the preferences from an XML file
        addPreferencesFromResource(R.xml.pref_logging);

        updateLoggingDisplay();

        Preference deletelog = findPreference(raApp.getString(R.string.prefLoggingDeleteKey));
        deletelog.setOnPreferenceClickListener(new DeleteLogPreferenceListener());

        Preference sendemail =
                findPreference(raApp.getString(R.string.prefLoggingSendKey));
        sendemail.setOnPreferenceClickListener(new SendEmailPreferenceListener());

        // disable deleting and sending of the log file if not present
        if (!raApp.isLoggingFilePresent()) {
            deletelog.setEnabled(false);
            sendemail.setEnabled(false);
        }
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
        getPreferenceScreen().getSharedPreferences();
    }

    private void updateLoggingDisplay() {
        findPreference(raApp.getString(R.string.prefLoggingUpdateKey)).setSummary(
                ((SettingsActivity) getActivity()).getDisplayValue(raPrefs.getLoggingUpdateValue(),
                        R.array.loggingUpdateValues,
                        R.array.loggingUpdate)
        );
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(raApp.getString(R.string.prefLoggingUpdateKey))) {
            updateLoggingDisplay();
        }
    }

    class DeleteLogPreferenceListener implements Preference.OnPreferenceClickListener {

        @Override
        public boolean onPreferenceClick(Preference preference) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage(raApp.getString(R.string.messageDeleteLogPrompt))
                    .setCancelable(false)
                    .setPositiveButton(raApp.getString(R.string.buttonYes),
                            new DialogInterface.OnClickListener() {
                                public void onClick(
                                        DialogInterface dialog,
                                        int id) {
//                                    Log.d( TAG, "Delete log file" );
                                    dialog.dismiss();
                                    deleteLogFile();
                                }
                            }
                    )
                    .setNegativeButton(raApp.getString(R.string.buttonNo),
                            new DialogInterface.OnClickListener() {
                                public void onClick(
                                        DialogInterface dialog,
                                        int id) {
//                                    Log.d(TAG, "Delete log cancelled");
                                    dialog.cancel();
                                }
                            }
                    );

            AlertDialog alert = builder.create();
            alert.show();
            return true;
        }

        private void deleteLogFile() {
            raApp.deleteLoggingFile();
            // disable deleting and sending of the log file if not present
            if (!raApp.isLoggingFilePresent()) {
                findPreference(raApp.getString(R.string.prefLoggingDeleteKey))
                        .setEnabled(false);
                findPreference(raApp.getString(R.string.prefLoggingSendKey))
                        .setEnabled(false);
            }
        }

    }


    class SendEmailPreferenceListener implements Preference.OnPreferenceClickListener {

        @Override
        public boolean onPreferenceClick(Preference preference) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage(raApp.getString(R.string.messageSendLogPrompt))
                    .setCancelable(false)
                    .setPositiveButton(raApp.getString(R.string.buttonYes),
                            new DialogInterface.OnClickListener() {
                                public void onClick(
                                        DialogInterface dialog,
                                        int id) {
//                                    Log.d( TAG, "Send file" );
                                    dialog.dismiss();
                                    sendEmail();
                                }
                            }
                    )
                    .setNegativeButton(raApp.getString(R.string.buttonNo),
                            new DialogInterface.OnClickListener() {
                                public void onClick(
                                        DialogInterface dialog,
                                        int id) {
//                                    Log.d( TAG, "Send cancelled" );
                                    dialog.cancel();
                                }
                            }
                    );

            AlertDialog alert = builder.create();
            alert.show();
            return true;
        }

        private void sendEmail() {
            Intent email = new Intent(Intent.ACTION_SEND);
            email.putExtra(Intent.EXTRA_EMAIL,
                    new String[]{"android@curtbinder.info"});
            email.putExtra(Intent.EXTRA_SUBJECT, "Status Logfile");
            email.setType("text/plain");
            email.putExtra(Intent.EXTRA_TEXT, "Logfile from my session.");
//            Log.d(TAG, "Logfile: " + Uri.parse( "file://" + raApp.getLoggingFile() ) );
            email.putExtra(Intent.EXTRA_STREAM,
                    Uri.parse("file://" + raApp.getLoggingFile()));
            getActivity().startActivity(Intent
                    .createChooser(email, "Send email..."));
        }
    }

}
