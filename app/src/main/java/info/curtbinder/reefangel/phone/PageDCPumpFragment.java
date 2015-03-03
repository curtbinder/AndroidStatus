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
 * Status page for the DC Pump
 */
public class PageDCPumpFragment extends Fragment
    implements PageRefreshInterface, View.OnLongClickListener {

    private static final String TAG = PageDCPumpFragment.class.getSimpleName();
    private TextView[] dcpumpText = new TextView[Controller.MAX_DCPUMP_VALUES];
    private short[] dcpumpValues = new short[Controller.MAX_DCPUMP_VALUES];
    private String[] dcpumpModes;

    public static PageDCPumpFragment newInstance() {
        return new PageDCPumpFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.page_dcpump, container, false);
        findViews(rootView);
        dcpumpModes = getResources().getStringArray(R.array.dcPumpModeLabels);
        return rootView;
    }

    private void findViews(View root) {
        TableRow tr;
        tr = (TableRow) root.findViewById(R.id.rowMode);
        dcpumpText[0] = (TextView) tr.findViewById(R.id.rowValue);
        setRowTitle(tr, R.string.labelMode);
        tr = (TableRow) root.findViewById(R.id.rowSpeed);
        dcpumpText[1] = (TextView) tr.findViewById(R.id.rowValue);
        setRowTitle(tr, R.string.labelSpeed);
        tr = (TableRow) root.findViewById(R.id.rowDuration);
        dcpumpText[2] = (TextView) tr.findViewById(R.id.rowValue);
        setRowTitle(tr, R.string.labelDuration);
        tr = (TableRow) root.findViewById(R.id.rowThreshold);
        dcpumpText[3] = (TextView) tr.findViewById(R.id.rowValue);
        setRowTitle(tr, R.string.labelThreshold);
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
        for ( int i = 0; i < Controller.MAX_DCPUMP_VALUES; i++ ) {
            dcpumpText[i].setLongClickable(fClickable);
            dcpumpText[i].setOnLongClickListener(l);
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
            case R.id.rowMode:
                f.displayDCPumpDialog(Controller.DCPUMP_MODE, dcpumpValues[Controller.DCPUMP_MODE]);
                break;
            case R.id.rowSpeed:
                f.displayDCPumpDialog(Controller.DCPUMP_SPEED, dcpumpValues[Controller.DCPUMP_SPEED]);
                break;
            case R.id.rowDuration:
                f.displayDCPumpDialog(Controller.DCPUMP_DURATION, dcpumpValues[Controller.DCPUMP_DURATION]);
                break;
            case R.id.rowThreshold:
                f.displayDCPumpDialog(Controller.DCPUMP_THRESHOLD, dcpumpValues[Controller.DCPUMP_THRESHOLD]);
                break;
        }
        return true;
    }

    private String getMode(int v) {
        // array is from 0-9 indices
        // indices 7-9 actually correspond to values 12-14
        int index = 0;
        if ((v >= 0) && (v <= 6)) {
            index = v;
        } else if ((v >= 12) && (v <= 14)) {
            index = v - 5;
        }
        return dcpumpModes[index];
    }

    private String[] getValues(Cursor c) {
        String sa[] = new String[Controller.MAX_DCPUMP_VALUES];
        dcpumpValues[Controller.DCPUMP_MODE] = c.getShort(c.getColumnIndex(StatusTable.COL_DCM));
        sa[Controller.DCPUMP_MODE] = getMode(dcpumpValues[Controller.DCPUMP_MODE]);
        dcpumpValues[Controller.DCPUMP_SPEED] = c.getShort(c.getColumnIndex(StatusTable.COL_DCS));
        sa[Controller.DCPUMP_SPEED] = dcpumpValues[Controller.DCPUMP_SPEED] + "%";
        dcpumpValues[Controller.DCPUMP_DURATION] = c.getShort(c.getColumnIndex(StatusTable.COL_DCD));
        sa[Controller.DCPUMP_DURATION] = dcpumpValues[Controller.DCPUMP_DURATION] + "";
        dcpumpValues[Controller.DCPUMP_THRESHOLD] = c.getShort(c.getColumnIndex(StatusTable.COL_DCT));
        sa[Controller.DCPUMP_THRESHOLD] = dcpumpValues[Controller.DCPUMP_THRESHOLD] + "";
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
            v = f.getNeverValues(Controller.MAX_DCPUMP_VALUES);
        }
        c.close();

        f.updateDisplayText(updateStatus);
        for (int i = 0; i < Controller.MAX_DCPUMP_VALUES; i++ ) {
            dcpumpText[i].setText(v[i]);
        }
    }
}
