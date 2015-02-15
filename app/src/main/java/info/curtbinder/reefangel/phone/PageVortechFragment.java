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
public class PageVortechFragment extends Fragment
implements PageRefreshInterface, PagePWMRefreshInterface, View.OnLongClickListener {

    private static final String TAG = PageVortechFragment.class.getSimpleName();
    private TextView[] vortechText = new TextView[Controller.MAX_VORTECH_VALUES];
    private int[] vortechValues = new int[Controller.MAX_VORTECH_VALUES];
    private String[] vortechModes;

    public static PageVortechFragment newInstance() {
        return new PageVortechFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.page_vortech, container, false);
        findViews(rootView);
        vortechModes = getResources().getStringArray(R.array.vortechModeLabels);
        return rootView;
    }

    private void findViews(View root) {
        TableRow tr;
        tr = (TableRow) root.findViewById(R.id.rowMode);
        vortechText[0] = (TextView) tr.findViewById(R.id.rowValue);
        setRowTitle(tr, R.string.labelMode);
        tr = (TableRow) root.findViewById(R.id.rowSpeed);
        vortechText[1] = (TextView) tr.findViewById(R.id.rowValue);
        setRowTitle(tr, R.string.labelSpeed);
        tr = (TableRow) root.findViewById(R.id.rowDuration);
        vortechText[2] = (TextView) tr.findViewById(R.id.rowValue);
        setRowTitle(tr, R.string.labelDuration);

        for ( int i = 0; i < Controller.MAX_VORTECH_VALUES; i++ ) {
            vortechText[i].setLongClickable(true);
            vortechText[i].setOnLongClickListener(this);
        }
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

    @Override
    public boolean onLongClick(View v) {
        View parent = (View) v.getParent();
        StatusFragment f = (StatusFragment) getParentFragment();
        switch (parent.getId()) {
            default:
                return false;
            case R.id.rowMode:
                Log.d(TAG, "longclick MODE");
                f.displayVortechDialog(Controller.VORTECH_MODE,
                        vortechValues[Controller.VORTECH_MODE]);
                break;
            case R.id.rowSpeed:
                Log.d(TAG, "longclick SPEED");
                f.displayVortechDialog(Controller.VORTECH_SPEED,
                        vortechValues[Controller.VORTECH_SPEED]);
                break;
            case R.id.rowDuration:
                Log.d(TAG, "longclick DURATION");
                f.displayVortechDialog(Controller.VORTECH_DURATION,
                        vortechValues[Controller.VORTECH_DURATION]);
                break;
        }
        return true;
    }

    @Override
    public void updatePWMValues(short[] v) {
        // TODO may need to function exactly like refreshData()
        for ( int i = 0; i < Controller.MAX_VORTECH_VALUES; i++ ) {
            vortechValues[i] = v[i];
        }
    }

//    public short[] getVortechValues(Cursor c) {
//        // TODO is getVortechValues needed?? not currently used.
//        return new short[] {
//                c.getShort(c.getColumnIndex(StatusTable.COL_RFM)),
//                c.getShort(c.getColumnIndex(StatusTable.COL_RFS)),
//                c.getShort(c.getColumnIndex(StatusTable.COL_RFD))
//        };
//    }

    private String getMode(int v) {
        String s;
        if ( v >= 0 && v <= 11 ) {
            // use the index value
            s = vortechModes[v];
        } else if ( v >= 97 && v <= 100 ) {
            // use index 12
            s = vortechModes[v - 85];
        } else {
            // unknown, so use default status
            s = getString(R.string.defaultStatusText);
        }
        return s;
    }

    private String getDuration(int v, int mode) {
        String s = "";
        switch (mode) {
            case 3:
            case 5:
                // value is in 100 milliseconds
                s = String.format(Locale.US, "%d %s", v, "ms");
                break;
            case 4:
                // value is in seconds
                s = String.format(Locale.US, "%d %c", v, 's');
                break;
            default:
                break;
        }
        return s;
    }

    private String[] getValues(Cursor c) {
        // Updates the display text and the values
        String sa[] = new String[Controller.MAX_VORTECH_VALUES];
        // mode
        vortechValues[Controller.VORTECH_MODE] = c.getInt(c.getColumnIndex(StatusTable.COL_RFM));
        sa[Controller.VORTECH_MODE] = getMode(vortechValues[Controller.VORTECH_MODE]);
        // speed
        vortechValues[Controller.VORTECH_SPEED] = c.getInt(c.getColumnIndex(StatusTable.COL_RFS));
        sa[Controller.VORTECH_SPEED] = String.format(Locale.US, "%d%c",
                vortechValues[Controller.VORTECH_SPEED], '%');
        // duration
        vortechValues[Controller.VORTECH_DURATION] = c.getInt(c.getColumnIndex(StatusTable.COL_RFD));
        sa[Controller.VORTECH_DURATION] = getDuration(vortechValues[Controller.VORTECH_DURATION],
                vortechValues[Controller.VORTECH_MODE]);
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
            v = ((StatusFragment) getParentFragment()).getNeverValues(Controller.MAX_VORTECH_VALUES);
        }
        c.close();

        f.updateDisplayText(updateStatus);
        for ( int i = 0; i < Controller.MAX_VORTECH_VALUES; i++ ) {
            vortechText[i].setText(v[i]);
        }
    }
}
