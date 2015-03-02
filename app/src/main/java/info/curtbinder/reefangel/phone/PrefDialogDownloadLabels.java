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
import android.content.Intent;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.Toast;

import info.curtbinder.reefangel.service.MessageCommands;
import info.curtbinder.reefangel.service.UpdateService;

/**
 * Created by binder on 3/1/15.
 */
public class PrefDialogDownloadLabels extends DialogPreference {

    private static final String TAG = PrefDialogDownloadLabels.class.getSimpleName();

    protected Context ctx;

    public PrefDialogDownloadLabels(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.ctx = context;
        SettingsActivity settings = (SettingsActivity) ctx;
        RAApplication raApp = (RAApplication) settings.getApplication();
        CharSequence cs = ctx.getString(R.string.prefControllerLabelsDownloadSummary)
                + " " + raApp.raprefs.getUserId();
        Log.d(TAG, "" + cs);
        setDialogMessage(cs);
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        super.onClick(dialog, which);

        if (which == DialogInterface.BUTTON_POSITIVE) {
            dialog.dismiss();
            downloadLabels();

            // TODO consider reloading all default values
            // TODO consider setting onPreferenceChange listeners to update the settings when they change
            // call this to trigger the execution of the setOnPreferenceChangeListener method
            // at the SettingsActivity
            //getOnPreferenceChangeListener().onPreferenceChange(this, true);
        }
    }

    private void downloadLabels() {
        Intent i = new Intent(ctx, UpdateService.class);
        i.setAction(MessageCommands.LABEL_QUERY_INTENT);
        ctx.startService(i);
        Toast.makeText(ctx, ctx.getString(R.string.messageDownloadLabels),
                Toast.LENGTH_SHORT).show();
    }
}
