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

import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.Toast;

import info.curtbinder.reefangel.controller.Controller;

/**
 * Created by binder on 3/1/15.
 */
public class PrefDialogResetLabels extends DialogPreference {

    private static final String TAG = PrefDialogResetLabels.class.getSimpleName();

    protected Context ctx;

    public PrefDialogResetLabels(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.ctx = context;
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        super.onClick(dialog, which);

        if (which == DialogInterface.BUTTON_POSITIVE) {
            dialog.dismiss();
            Handler h = new Handler();
            final Runnable r = new Runnable() {
                public void run() {
                    resetLabels();
                }
            };
            h.post(r);

            // TODO consider reloading all default values
            // TODO consider setting onPreferenceChange listeners to update the settings when they change
            // call this to trigger the execution of the setOnPreferenceChangeListener method
            // at the SettingsActivity
            //getOnPreferenceChangeListener().onPreferenceChange(this, true);
        }
    }

    private void resetLabels() {
        Log.d(TAG, "Deleting all labels");
        SettingsActivity settings = (SettingsActivity) ctx;
        RAApplication raApp = (RAApplication) settings.getApplication();
        RAPreferences raprefs = raApp.raprefs;
        Toast.makeText(ctx,
                raApp.getString(R.string.messageResetLabelsBegin),
                Toast.LENGTH_SHORT).show();

        // delete all controller labels
        raprefs.deletePref(R.string.prefT1LabelKey);
        raprefs.deletePref(R.string.prefT2LabelKey);
        raprefs.deletePref(R.string.prefT3LabelKey);
        raprefs.deletePref(R.string.prefAPLabelKey);
        raprefs.deletePref(R.string.prefDPLabelKey);
        raprefs.deletePref(R.string.prefPHLabelKey);
        raprefs.deletePref(R.string.prefSalinityLabelKey);
        raprefs.deletePref(R.string.prefORPLabelKey);
        raprefs.deletePref(R.string.prefPHExpLabelKey);
        int i;
        for (i = 0; i <= Controller.MAX_EXPANSION_RELAYS; i++) {
            for (int j = 0; j < Controller.MAX_RELAY_PORTS; j++) {
                raprefs.deletePref(raprefs.getRelayKey(i, j));
            }
        }
        raprefs.deletePref(R.string.prefATOHiLabelKey);
        raprefs.deletePref(R.string.prefATOLoLabelKey);
        raprefs.deletePref(R.string.prefPHExpLabelKey);
        for (i = 0; i <= Controller.MAX_WATERLEVEL_PORTS; i++) {
            raprefs.deletePref(raprefs.getWaterLevelLabelKey(i));
        }
        raprefs.deletePref(R.string.prefHumidityLabelKey);
        raprefs.deletePref(R.string.prefExpDimmingCh0LabelKey);
        raprefs.deletePref(R.string.prefExpDimmingCh1LabelKey);
        raprefs.deletePref(R.string.prefExpDimmingCh2LabelKey);
        raprefs.deletePref(R.string.prefExpDimmingCh3LabelKey);
        raprefs.deletePref(R.string.prefExpDimmingCh4LabelKey);
        raprefs.deletePref(R.string.prefExpDimmingCh5LabelKey);
        raprefs.deletePref(R.string.prefExpSCDimmingCh0LabelKey);
        raprefs.deletePref(R.string.prefExpSCDimmingCh1LabelKey);
        raprefs.deletePref(R.string.prefExpSCDimmingCh2LabelKey);
        raprefs.deletePref(R.string.prefExpSCDimmingCh3LabelKey);
        raprefs.deletePref(R.string.prefExpSCDimmingCh4LabelKey);
        raprefs.deletePref(R.string.prefExpSCDimmingCh5LabelKey);
        raprefs.deletePref(R.string.prefExpSCDimmingCh6LabelKey);
        raprefs.deletePref(R.string.prefExpSCDimmingCh7LabelKey);
        raprefs.deletePref(R.string.prefExpSCDimmingCh8LabelKey);
        raprefs.deletePref(R.string.prefExpSCDimmingCh9LabelKey);
        raprefs.deletePref(R.string.prefExpSCDimmingCh10LabelKey);
        raprefs.deletePref(R.string.prefExpSCDimmingCh11LabelKey);
        raprefs.deletePref(R.string.prefExpSCDimmingCh12LabelKey);
        raprefs.deletePref(R.string.prefExpSCDimmingCh13LabelKey);
        raprefs.deletePref(R.string.prefExpSCDimmingCh14LabelKey);
        raprefs.deletePref(R.string.prefExpSCDimmingCh15LabelKey);
        raprefs.deletePref(R.string.prefExpIO0LabelKey);
        raprefs.deletePref(R.string.prefExpIO1LabelKey);
        raprefs.deletePref(R.string.prefExpIO2LabelKey);
        raprefs.deletePref(R.string.prefExpIO3LabelKey);
        raprefs.deletePref(R.string.prefExpIO4LabelKey);
        raprefs.deletePref(R.string.prefExpIO5LabelKey);
        raprefs.deletePref(R.string.prefExpCustom0LabelKey);
        raprefs.deletePref(R.string.prefExpCustom1LabelKey);
        raprefs.deletePref(R.string.prefExpCustom2LabelKey);
        raprefs.deletePref(R.string.prefExpCustom3LabelKey);
        raprefs.deletePref(R.string.prefExpCustom4LabelKey);
        raprefs.deletePref(R.string.prefExpCustom5LabelKey);
        raprefs.deletePref(R.string.prefExpCustom6LabelKey);
        raprefs.deletePref(R.string.prefExpCustom7LabelKey);

        Toast.makeText(ctx,
                raApp.getString(R.string.messageResetLabelsComplete),
                Toast.LENGTH_SHORT).show();
    }
}
