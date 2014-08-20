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
 */
public class PageFlagsFragment extends Fragment
implements PageRefreshInterface {
    private static final String TAG = PageFlagsFragment.class.getSimpleName();

    private TextView[] flagsStatusText = new TextView[Controller.MAX_STATUS_FLAGS];
    private TextView[] flagsAlertText = new TextView[Controller.MAX_ALERT_FLAGS];

    public static PageFlagsFragment newInstance() {
        return new PageFlagsFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.page_flags, container, false);
        findViews(rootView);
        return rootView;
    }

    private void findViews(View root) {
        TableRow tr;
        tr = (TableRow) root.findViewById(R.id.rowAto);
        flagsAlertText[0] = (TextView) tr.findViewById(R.id.rowValue);
        setRowTitle(tr, R.string.labelAtoTimeout);
        tr = (TableRow) root.findViewById(R.id.rowOverheat);
        flagsAlertText[1] = (TextView) tr.findViewById(R.id.rowValue);
        setRowTitle(tr, R.string.labelOverheatTimeout);
        tr = (TableRow) root.findViewById(R.id.rowBusLock);
        flagsAlertText[2] = (TextView) tr.findViewById(R.id.rowValue);
        setRowTitle(tr, R.string.labelBusLock);
        tr = (TableRow) root.findViewById(R.id.rowLeak);
        flagsAlertText[3] = (TextView) tr.findViewById(R.id.rowValue);
        setRowTitle(tr, R.string.labelLeak);

        tr = (TableRow) root.findViewById(R.id.rowLightsOn);
        flagsStatusText[0] = (TextView) tr.findViewById(R.id.rowValue);
        setRowTitle(tr, R.string.labelLightsOn);
        tr = (TableRow) root.findViewById(R.id.rowFeeding);
        flagsStatusText[1] = (TextView) tr.findViewById(R.id.rowValue);
        setRowTitle(tr, R.string.labelFeedingMode);
        tr = (TableRow) root.findViewById(R.id.rowWaterChange);
        flagsStatusText[2] = (TextView) tr.findViewById(R.id.rowValue);
        setRowTitle(tr, R.string.labelWaterMode);
    }

    private void setRowTitle(TableRow row, int labelId) {
        ((TextView) row.findViewById(R.id.rowTitle)).setText(labelId);
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshData();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    private void updateStatusFlags(short flags) {
        int id;
        int color;
        if ( Controller.isLightsOnFlagSet(flags) ) {
            id = R.string.labelON;
            color = getResources().getColor(R.color.red);
        } else {
            id = R.string.labelOFF;
            color = getResources().getColor(R.color.green);
        }
        flagsStatusText[0].setText(id);
        flagsStatusText[0].setTextColor(color);

        if ( Controller.isFeedingFlagSet(flags) ) {
            id = R.string.labelON;
            color = getResources().getColor(R.color.red);
        } else {
            id = R.string.labelOFF;
            color = getResources().getColor(R.color.green);
        }
        flagsStatusText[1].setText(id);
        flagsStatusText[1].setTextColor(color);

        if ( Controller.isWaterChangeFlagSet(flags) ) {
            id = R.string.labelON;
            color = getResources().getColor(R.color.red);
        } else {
            id = R.string.labelOFF;
            color = getResources().getColor(R.color.green);
        }
        flagsStatusText[2].setText(id);
        flagsStatusText[2].setTextColor(color);
    }

    private void updateAlertFlags(short flags) {
        int id;
        int color;
        if ( Controller.isATOTimeoutFlagSet(flags) ) {
            id = R.string.labelON;
            color = getResources().getColor(R.color.red);
        } else {
            id = R.string.labelOFF;
            color = getResources().getColor(R.color.green);
        }
        flagsAlertText[0].setText(id);
        flagsAlertText[0].setTextColor(color);

        if ( Controller.isOverheatFlagSet(flags) ) {
            id = R.string.labelON;
            color = getResources().getColor(R.color.red);
        } else {
            id = R.string.labelOFF;
            color = getResources().getColor(R.color.green);
        }
        flagsAlertText[1].setText(id);
        flagsAlertText[1].setTextColor(color);

        if ( Controller.isBusLockFlagSet(flags) ) {
            id = R.string.labelON;
            color = getResources().getColor(R.color.red);
        } else {
            id = R.string.labelOFF;
            color = getResources().getColor(R.color.green);
        }
        flagsAlertText[2].setText(id);
        flagsAlertText[2].setTextColor(color);

        if ( Controller.isLeakFlagSet(flags) ) {
            id = R.string.labelON;
            color = getResources().getColor(R.color.red);
        } else {
            id = R.string.labelOFF;
            color = getResources().getColor(R.color.green);
        }
        flagsAlertText[3].setText(id);
        flagsAlertText[3].setTextColor(color);
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
        short sf, af;
        if (c.moveToFirst()) {
            updateStatus = c.getString(c.getColumnIndex(StatusTable.COL_LOGDATE));
            sf = c.getShort(c.getColumnIndex(StatusTable.COL_SF));
            af = c.getShort(c.getColumnIndex(StatusTable.COL_AF));
        } else {
            updateStatus = getString(R.string.messageNever);
            sf = af = 0;
        }
        c.close();

        f.updateDisplayText(updateStatus);
        updateStatusFlags(sf);
        updateAlertFlags(af);
    }
}
