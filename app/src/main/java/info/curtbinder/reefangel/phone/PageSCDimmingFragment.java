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
public class PageSCDimmingFragment extends Fragment
    implements PageRefreshInterface, View.OnLongClickListener {

    private static final String TAG = PageSCDimmingFragment.class.getSimpleName();

    private TextView[] pwmeText = new TextView[Controller.MAX_SCPWM_EXPANSION_PORTS];
    private TableRow[] pwmeRow = new TableRow[Controller.MAX_SCPWM_EXPANSION_PORTS];
    private short[] pwmeValues = new short[Controller.MAX_SCPWM_EXPANSION_PORTS];

    public static PageSCDimmingFragment newInstance() {
        return new PageSCDimmingFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.page_scdimming, container, false);
        findViews(rootView);
        return rootView;
    }

    private void findViews(View root) {
        pwmeRow[0] = (TableRow) root.findViewById(R.id.rowSCPWME0);
        pwmeRow[1] = (TableRow) root.findViewById(R.id.rowSCPWME1);
        pwmeRow[2] = (TableRow) root.findViewById(R.id.rowSCPWME2);
        pwmeRow[3] = (TableRow) root.findViewById(R.id.rowSCPWME3);
        pwmeRow[4] = (TableRow) root.findViewById(R.id.rowSCPWME4);
        pwmeRow[5] = (TableRow) root.findViewById(R.id.rowSCPWME5);
        pwmeRow[6] = (TableRow) root.findViewById(R.id.rowSCPWME6);
        pwmeRow[7] = (TableRow) root.findViewById(R.id.rowSCPWME7);
        pwmeRow[8] = (TableRow) root.findViewById(R.id.rowSCPWME8);
        pwmeRow[9] = (TableRow) root.findViewById(R.id.rowSCPWME9);
        pwmeRow[10] = (TableRow) root.findViewById(R.id.rowSCPWME10);
        pwmeRow[11] = (TableRow) root.findViewById(R.id.rowSCPWME11);
        pwmeRow[12] = (TableRow) root.findViewById(R.id.rowSCPWME12);
        pwmeRow[13] = (TableRow) root.findViewById(R.id.rowSCPWME13);
        pwmeRow[14] = (TableRow) root.findViewById(R.id.rowSCPWME14);
        pwmeRow[15] = (TableRow) root.findViewById(R.id.rowSCPWME15);

        for ( int i = 0; i < Controller.MAX_SCPWM_EXPANSION_PORTS; i++ ) {
            pwmeText[i] = (TextView) pwmeRow[i].findViewById(R.id.rowValue);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        updateLabelsAndVisibility();
        updateClickable();
        refreshData();
    }

    private void updateClickable() {
        boolean fClickable = ((RAApplication) getActivity().getApplication()).raprefs.isCommunicateController();
        View.OnLongClickListener l = null;
        // Update the long clickablility and longclick listener based on the device we communicate
        if (fClickable) {
            l = this;
        }
        for ( int i = 0; i < Controller.MAX_SCPWM_EXPANSION_PORTS; i++ ) {
            pwmeText[i].setLongClickable(fClickable);
            pwmeText[i].setOnLongClickListener(l);
        }
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
            case R.id.rowSCPWME0:
                f.displayOverrideDialog(Globals.OVERRIDE_16CH_CHANNEL0, pwmeValues[0]);
                break;
            case R.id.rowSCPWME1:
                f.displayOverrideDialog(Globals.OVERRIDE_16CH_CHANNEL1, pwmeValues[1]);
                break;
            case R.id.rowSCPWME2:
                f.displayOverrideDialog(Globals.OVERRIDE_16CH_CHANNEL2, pwmeValues[2]);
                break;
            case R.id.rowSCPWME3:
                f.displayOverrideDialog(Globals.OVERRIDE_16CH_CHANNEL3, pwmeValues[3]);
                break;
            case R.id.rowSCPWME4:
                f.displayOverrideDialog(Globals.OVERRIDE_16CH_CHANNEL4, pwmeValues[4]);
                break;
            case R.id.rowSCPWME5:
                f.displayOverrideDialog(Globals.OVERRIDE_16CH_CHANNEL5, pwmeValues[5]);
                break;
            case R.id.rowSCPWME6:
                f.displayOverrideDialog(Globals.OVERRIDE_16CH_CHANNEL6, pwmeValues[6]);
                break;
            case R.id.rowSCPWME7:
                f.displayOverrideDialog(Globals.OVERRIDE_16CH_CHANNEL7, pwmeValues[7]);
                break;
            case R.id.rowSCPWME8:
                f.displayOverrideDialog(Globals.OVERRIDE_16CH_CHANNEL8, pwmeValues[8]);
                break;
            case R.id.rowSCPWME9:
                f.displayOverrideDialog(Globals.OVERRIDE_16CH_CHANNEL9, pwmeValues[9]);
                break;
            case R.id.rowSCPWME10:
                f.displayOverrideDialog(Globals.OVERRIDE_16CH_CHANNEL10, pwmeValues[10]);
                break;
            case R.id.rowSCPWME11:
                f.displayOverrideDialog(Globals.OVERRIDE_16CH_CHANNEL11, pwmeValues[11]);
                break;
            case R.id.rowSCPWME12:
                f.displayOverrideDialog(Globals.OVERRIDE_16CH_CHANNEL12, pwmeValues[12]);
                break;
            case R.id.rowSCPWME13:
                f.displayOverrideDialog(Globals.OVERRIDE_16CH_CHANNEL13, pwmeValues[13]);
                break;
            case R.id.rowSCPWME14:
                f.displayOverrideDialog(Globals.OVERRIDE_16CH_CHANNEL14, pwmeValues[14]);
                break;
            case R.id.rowSCPWME15:
                f.displayOverrideDialog(Globals.OVERRIDE_16CH_CHANNEL15, pwmeValues[15]);
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
        for(int i = 0; i < Controller.MAX_SCPWM_EXPANSION_PORTS; i++) {
            setLabel(i, raPrefs.getSCDimmingModuleChannelLabel(i));
            // TODO add in visibility functions
            //setVisibility(i, true);
        }
    }

    private String[] getValues(Cursor c) {
        String sa[] = new String[Controller.MAX_SCPWM_EXPANSION_PORTS];
        pwmeValues[0] = c.getShort(c.getColumnIndex(StatusTable.COL_SCPWME0));
        sa[0] = Controller.getPWMDisplayValue(pwmeValues[0],
                        c.getShort(c.getColumnIndex(StatusTable.COL_SCPWME0O)));
        pwmeValues[1] = c.getShort(c.getColumnIndex(StatusTable.COL_SCPWME1));
        sa[1] = Controller.getPWMDisplayValue(pwmeValues[1],
                        c.getShort(c.getColumnIndex(StatusTable.COL_SCPWME1O)));
        pwmeValues[2] = c.getShort(c.getColumnIndex(StatusTable.COL_SCPWME2));
        sa[2] = Controller.getPWMDisplayValue(pwmeValues[2],
                        c.getShort(c.getColumnIndex(StatusTable.COL_SCPWME2O)));
        pwmeValues[3] = c.getShort(c.getColumnIndex(StatusTable.COL_SCPWME3));
        sa[3] = Controller.getPWMDisplayValue(pwmeValues[3],
                        c.getShort(c.getColumnIndex(StatusTable.COL_SCPWME3O)));
        pwmeValues[4] = c.getShort(c.getColumnIndex(StatusTable.COL_SCPWME4));
        sa[4] = Controller.getPWMDisplayValue(pwmeValues[4],
                        c.getShort(c.getColumnIndex(StatusTable.COL_SCPWME4O)));
        pwmeValues[5] = c.getShort(c.getColumnIndex(StatusTable.COL_SCPWME5));
        sa[5] = Controller.getPWMDisplayValue(pwmeValues[5],
                        c.getShort(c.getColumnIndex(StatusTable.COL_SCPWME5O)));
        pwmeValues[6] = c.getShort(c.getColumnIndex(StatusTable.COL_SCPWME6));
        sa[6] = Controller.getPWMDisplayValue(pwmeValues[6],
                        c.getShort(c.getColumnIndex(StatusTable.COL_SCPWME6O)));
        pwmeValues[7] = c.getShort(c.getColumnIndex(StatusTable.COL_SCPWME7));
        sa[7] = Controller.getPWMDisplayValue(pwmeValues[7],
                        c.getShort(c.getColumnIndex(StatusTable.COL_SCPWME7O)));
        pwmeValues[8] = c.getShort(c.getColumnIndex(StatusTable.COL_SCPWME8));
        sa[8] = Controller.getPWMDisplayValue(pwmeValues[8],
                        c.getShort(c.getColumnIndex(StatusTable.COL_SCPWME8O)));
        pwmeValues[9] = c.getShort(c.getColumnIndex(StatusTable.COL_SCPWME9));
        sa[9] = Controller.getPWMDisplayValue(pwmeValues[9],
                        c.getShort(c.getColumnIndex(StatusTable.COL_SCPWME9O)));
        pwmeValues[10] = c.getShort(c.getColumnIndex(StatusTable.COL_SCPWME10));
        sa[10] = Controller.getPWMDisplayValue(pwmeValues[10],
                        c.getShort(c.getColumnIndex(StatusTable.COL_SCPWME10O)));
        pwmeValues[11] = c.getShort(c.getColumnIndex(StatusTable.COL_SCPWME11));
        sa[11] = Controller.getPWMDisplayValue(pwmeValues[11],
                        c.getShort(c.getColumnIndex(StatusTable.COL_SCPWME11O)));
        pwmeValues[12] = c.getShort(c.getColumnIndex(StatusTable.COL_SCPWME12));
        sa[12] = Controller.getPWMDisplayValue(pwmeValues[12],
                        c.getShort(c.getColumnIndex(StatusTable.COL_SCPWME12O)));
        pwmeValues[13] = c.getShort(c.getColumnIndex(StatusTable.COL_SCPWME13));
        sa[13] = Controller.getPWMDisplayValue(pwmeValues[13],
                        c.getShort(c.getColumnIndex(StatusTable.COL_SCPWME13O)));
        pwmeValues[14] = c.getShort(c.getColumnIndex(StatusTable.COL_SCPWME14));
        sa[14] = Controller.getPWMDisplayValue(pwmeValues[14],
                        c.getShort(c.getColumnIndex(StatusTable.COL_SCPWME14O)));
        pwmeValues[15] = c.getShort(c.getColumnIndex(StatusTable.COL_SCPWME15));
        sa[15] = Controller.getPWMDisplayValue(pwmeValues[15],
                        c.getShort(c.getColumnIndex(StatusTable.COL_SCPWME15O)));
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
            v = ((StatusFragment) getParentFragment()).getNeverValues(Controller.MAX_SCPWM_EXPANSION_PORTS);
        }
        c.close();

        f.updateDisplayText(updateStatus);
        for (int i = 0; i < Controller.MAX_SCPWM_EXPANSION_PORTS; i++ ) {
            pwmeText[i].setText(v[i]);
        }
    }
}
