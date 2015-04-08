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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableRow;
import android.widget.TextView;

import info.curtbinder.reefangel.controller.Controller;
import info.curtbinder.reefangel.db.StatusTable;

/**
 * Created by binder on 7/26/14.
 *
 * Status page for the Radion device
 */
public class PageRadionFragment extends Fragment
    implements PageRefreshInterface, View.OnLongClickListener {

    private static final String TAG = PageRadionFragment.class.getSimpleName();
    private TextView[] radionText = new TextView[Controller.MAX_RADION_LIGHT_CHANNELS];
    private short[] radionValues = new short[Controller.MAX_RADION_LIGHT_CHANNELS];

    public static PageRadionFragment newInstance() {
        return new PageRadionFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.page_radion, container, false);
        findViews(rootView);
        return rootView;
    }

    private void findViews(View root) {
        TableRow tr;
        tr = (TableRow) root.findViewById(R.id.rowWhite);
        radionText[0] = (TextView) tr.findViewById(R.id.rowValue);
        setRowTitle(tr, R.string.labelWhite);
        tr = (TableRow) root.findViewById(R.id.rowRoyalBlue);
        radionText[1] = (TextView) tr.findViewById(R.id.rowValue);
        setRowTitle(tr, R.string.labelRoyalBlue);
        tr = (TableRow) root.findViewById(R.id.rowRed);
        radionText[2] = (TextView) tr.findViewById(R.id.rowValue);
        setRowTitle(tr, R.string.labelRed);
        tr = (TableRow) root.findViewById(R.id.rowGreen);
        radionText[3] = (TextView) tr.findViewById(R.id.rowValue);
        setRowTitle(tr, R.string.labelGreen);
        tr = (TableRow) root.findViewById(R.id.rowBlue);
        radionText[4] = (TextView) tr.findViewById(R.id.rowValue);
        setRowTitle(tr, R.string.labelBlue);
        tr = (TableRow) root.findViewById(R.id.rowIntensity);
        radionText[5] = (TextView) tr.findViewById(R.id.rowValue);
        setRowTitle(tr, R.string.labelIntensity);
    }

    private void setRowTitle(TableRow row, int labelId) {
        ((TextView) row.findViewById(R.id.rowTitle)).setText(labelId);
    }

    @Override
    public void onResume() {
        super.onResume();
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
        for ( int i = 0; i < Controller.MAX_RADION_LIGHT_CHANNELS; i++ ) {
            radionText[i].setLongClickable(fClickable);
            radionText[i].setOnLongClickListener(l);
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
            case R.id.rowWhite:
                f.displayOverrideDialog(Globals.OVERRIDE_RF_WHITE,
                        radionValues[Controller.RADION_WHITE]);
                break;
            case R.id.rowRoyalBlue:
                f.displayOverrideDialog(Globals.OVERRIDE_RF_ROYALBLUE,
                        radionValues[Controller.RADION_ROYALBLUE]);
                break;
            case R.id.rowRed:
                f.displayOverrideDialog(Globals.OVERRIDE_RF_RED,
                        radionValues[Controller.RADION_RED]);
                break;
            case R.id.rowGreen:
                f.displayOverrideDialog(Globals.OVERRIDE_RF_GREEN,
                        radionValues[Controller.RADION_GREEN]);
                break;
            case R.id.rowBlue:
                f.displayOverrideDialog(Globals.OVERRIDE_RF_BLUE,
                        radionValues[Controller.RADION_BLUE]);
                break;
            case R.id.rowIntensity:
                f.displayOverrideDialog(Globals.OVERRIDE_RF_INTENSITY,
                        radionValues[Controller.RADION_INTENSITY]);
                break;
        }
        return true;
    }

    private String[] getValues(Cursor c) {
        String sa[] = new String[Controller.MAX_RADION_LIGHT_CHANNELS];
        radionValues[Controller.RADION_WHITE] = c.getShort(c.getColumnIndex(StatusTable.COL_RFW));
        sa[Controller.RADION_WHITE] = Controller.getPWMDisplayValue(
                radionValues[Controller.RADION_WHITE],
                c.getShort(c.getColumnIndex(StatusTable.COL_RFWO)));
        radionValues[Controller.RADION_ROYALBLUE] = c.getShort(c.getColumnIndex(StatusTable.COL_RFRB));
        sa[Controller.RADION_ROYALBLUE] = Controller.getPWMDisplayValue(
                radionValues[Controller.RADION_ROYALBLUE],
                c.getShort(c.getColumnIndex(StatusTable.COL_RFRBO)));
        radionValues[Controller.RADION_RED] = c.getShort(c.getColumnIndex(StatusTable.COL_RFR));
        sa[Controller.RADION_RED] = Controller.getPWMDisplayValue(
                radionValues[Controller.RADION_RED],
                c.getShort(c.getColumnIndex(StatusTable.COL_RFRO)));
        radionValues[Controller.RADION_GREEN] = c.getShort(c.getColumnIndex(StatusTable.COL_RFG));
        sa[Controller.RADION_GREEN] = Controller.getPWMDisplayValue(
                radionValues[Controller.RADION_GREEN],
                c.getShort(c.getColumnIndex(StatusTable.COL_RFGO)));
        radionValues[Controller.RADION_BLUE] = c.getShort(c.getColumnIndex(StatusTable.COL_RFB));
        sa[Controller.RADION_BLUE] = Controller.getPWMDisplayValue(
                radionValues[Controller.RADION_BLUE],
                c.getShort(c.getColumnIndex(StatusTable.COL_RFBO)));
        radionValues[Controller.RADION_INTENSITY] = c.getShort(c.getColumnIndex(StatusTable.COL_RFI));
        sa[Controller.RADION_INTENSITY] = Controller.getPWMDisplayValue(
                radionValues[Controller.RADION_INTENSITY],
                c.getShort(c.getColumnIndex(StatusTable.COL_RFIO)));
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
            v = ((StatusFragment) getParentFragment()).getNeverValues(Controller.MAX_RADION_LIGHT_CHANNELS);
        }
        c.close();

        f.updateDisplayText(updateStatus);
        for (int i = 0; i < Controller.MAX_RADION_LIGHT_CHANNELS; i++ ) {
            radionText[i].setText(v[i]);
        }
    }
}
