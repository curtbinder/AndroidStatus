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
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.Locale;

import info.curtbinder.reefangel.controller.Controller;
import info.curtbinder.reefangel.db.StatusTable;

/**
 * Created by binder on 7/26/14.
 */
public class PageDimmingFragment extends Fragment
    implements PageRefreshInterface, View.OnLongClickListener {

    private static final String TAG = PageDimmingFragment.class.getSimpleName();

    private TextView[] pwmeText = new TextView[Controller.MAX_PWM_EXPANSION_PORTS];
    private TableRow[] pwmeRow = new TableRow[Controller.MAX_PWM_EXPANSION_PORTS];
    private short[] pwmeValues = new short[Controller.MAX_PWM_EXPANSION_PORTS];

    public static PageDimmingFragment newInstance() {
        return new PageDimmingFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.page_dimming, container, false);
        findViews(rootView);
        return rootView;
    }

    private void findViews(View root) {
        pwmeRow[0] = (TableRow) root.findViewById(R.id.rowPWME0);
        pwmeRow[1] = (TableRow) root.findViewById(R.id.rowPWME1);
        pwmeRow[2] = (TableRow) root.findViewById(R.id.rowPWME2);
        pwmeRow[3] = (TableRow) root.findViewById(R.id.rowPWME3);
        pwmeRow[4] = (TableRow) root.findViewById(R.id.rowPWME4);
        pwmeRow[5] = (TableRow) root.findViewById(R.id.rowPWME5);

        for ( int i = 0; i < Controller.MAX_PWM_EXPANSION_PORTS; i++ ) {
            pwmeText[i] = (TextView) pwmeRow[i].findViewById(R.id.rowValue);
            pwmeText[i].setLongClickable(true);
            pwmeText[i].setOnLongClickListener(this);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        updateLabelsAndVisibility();
        refreshData();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public boolean onLongClick(View v) {
        View parent = (View) v.getParent();
        StatusFragment f = (StatusFragment) getParentFragment();
        switch (parent.getId()) {
            default:
                return false;
            case R.id.rowPWME0:
                f.displayOverrideDialog(Globals.OVERRIDE_CHANNEL0, pwmeValues[0]);
                break;
            case R.id.rowPWME1:
                f.displayOverrideDialog(Globals.OVERRIDE_CHANNEL1, pwmeValues[1]);
                break;
            case R.id.rowPWME2:
                f.displayOverrideDialog(Globals.OVERRIDE_CHANNEL2, pwmeValues[2]);
                break;
            case R.id.rowPWME3:
                f.displayOverrideDialog(Globals.OVERRIDE_CHANNEL3, pwmeValues[3]);
                break;
            case R.id.rowPWME4:
                f.displayOverrideDialog(Globals.OVERRIDE_CHANNEL4, pwmeValues[4]);
                break;
            case R.id.rowPWME5:
                f.displayOverrideDialog(Globals.OVERRIDE_CHANNEL5, pwmeValues[5]);
                break;
        }
        return true;
    }

    private void setLabel(int channel, String label) {
        ((TextView) pwmeRow[channel].findViewById(R.id.rowTitle)).setText(label);
        String s = String.format(Locale.getDefault(),
                "%s %d", getResources().getString(R.string.labelChannel), channel);
        ((TextView) pwmeRow[channel].findViewById(R.id.rowSubTitle)).setText(s);
    }

    private void setVisibility(int device, boolean fVisible) {
        int v;
        if (fVisible) {
            Log.d(TAG, device + " visible");
            v = View.VISIBLE;
        } else {
            Log.d(TAG, device + " gone");
            v = View.GONE;
        }
        pwmeRow[device].setVisibility(v);
    }

    private void updateLabelsAndVisibility() {
        Log.d(TAG, "updateLabelsAndVisibility");
        RAApplication raApp = (RAApplication)getActivity().getApplication();
        RAPreferences raPrefs = raApp.raprefs;
        for(int i = 0; i < Controller.MAX_PWM_EXPANSION_PORTS; i++) {
            setLabel(i, raPrefs.getDimmingModuleChannelLabel(i));
            // TODO add in visibility functions
            //setVisibility(i, true);
        }
    }

    public void updatePWMValues(short[] v) {
        for ( int i = 0; i < Controller.MAX_PWM_EXPANSION_PORTS; i++ ) {
            pwmeValues[i] = v[i];
        }
    }

//    public short[] getPWMEValues(Cursor c) {
//        return new short[] {
//            c.getShort(c.getColumnIndex(StatusTable.COL_PWME0)),
//            c.getShort(c.getColumnIndex(StatusTable.COL_PWME1)),
//            c.getShort(c.getColumnIndex(StatusTable.COL_PWME2)),
//            c.getShort(c.getColumnIndex(StatusTable.COL_PWME3)),
//            c.getShort(c.getColumnIndex(StatusTable.COL_PWME4)),
//            c.getShort(c.getColumnIndex(StatusTable.COL_PWME5))};
//    }

    private String[] getValues(Cursor c) {
        String sa[] = new String[Controller.MAX_PWM_EXPANSION_PORTS];
        pwmeValues[0] = c.getShort(c.getColumnIndex(StatusTable.COL_PWME0));
        sa[0] = Controller.getPWMDisplayValue(pwmeValues[0],
                        c.getShort(c.getColumnIndex(StatusTable.COL_PWME0O)));
        pwmeValues[1] = c.getShort(c.getColumnIndex(StatusTable.COL_PWME1));
        sa[1] = Controller.getPWMDisplayValue(pwmeValues[1],
                        c.getShort(c.getColumnIndex(StatusTable.COL_PWME1O)));
        pwmeValues[2] = c.getShort(c.getColumnIndex(StatusTable.COL_PWME2));
        sa[2] = Controller.getPWMDisplayValue(pwmeValues[2],
                        c.getShort(c.getColumnIndex(StatusTable.COL_PWME2O)));
        pwmeValues[3] = c.getShort(c.getColumnIndex(StatusTable.COL_PWME3));
        sa[3] = Controller.getPWMDisplayValue(pwmeValues[3],
                        c.getShort(c.getColumnIndex(StatusTable.COL_PWME3O)));
        pwmeValues[4] = c.getShort(c.getColumnIndex(StatusTable.COL_PWME4));
        sa[4] = Controller.getPWMDisplayValue(pwmeValues[4],
                        c.getShort(c.getColumnIndex(StatusTable.COL_PWME4O)));
        pwmeValues[5] = c.getShort(c.getColumnIndex(StatusTable.COL_PWME5));
        sa[5] = Controller.getPWMDisplayValue(pwmeValues[5],
                        c.getShort(c.getColumnIndex(StatusTable.COL_PWME5O)));
        return sa;
    }

    @Override
    public void refreshData() {
        Activity a = getActivity();
        if ( a == null ) {
            return;
        }
        // only update if the activity exists
        StatusFragment f = ((StatusFragment) getParentFragment());
        Cursor c = f.getLatestDataCursor();
        String updateStatus;
        String[] v;
        if (c.moveToFirst()) {
            updateStatus = c.getString(c.getColumnIndex(StatusTable.COL_LOGDATE));
            v = getValues(c);
        } else {
            updateStatus = getString(R.string.messageNever);
            v = ((StatusFragment) getParentFragment()).getNeverValues(Controller.MAX_PWM_EXPANSION_PORTS);
        }
        c.close();

        f.updateDisplayText(updateStatus);
        for (int i = 0; i < Controller.MAX_PWM_EXPANSION_PORTS; i++ ) {
            pwmeText[i].setText(v[i]);
        }
    }
}
