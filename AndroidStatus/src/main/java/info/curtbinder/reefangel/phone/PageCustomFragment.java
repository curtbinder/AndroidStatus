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

import info.curtbinder.reefangel.controller.Controller;
import info.curtbinder.reefangel.db.StatusTable;

/**
 * Created by binder on 7/26/14.
 */
public class PageCustomFragment extends Fragment
    implements PageRefreshInterface {

    private static final String TAG = PageCustomFragment.class.getSimpleName();
    private TextView[] customText = new TextView[Controller.MAX_CUSTOM_VARIABLES];
    private TableRow[] customRow = new TableRow[Controller.MAX_CUSTOM_VARIABLES];

    public static PageCustomFragment newInstance() {
        return new PageCustomFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.page_custom, container, false);
        // do something with rootView
        findViews(rootView);
        return rootView;
    }

    private void findViews(View root) {
        customRow[0] = (TableRow) root.findViewById(R.id.rowCustom0);
        customRow[1] = (TableRow) root.findViewById(R.id.rowCustom1);
        customRow[2] = (TableRow) root.findViewById(R.id.rowCustom2);
        customRow[3] = (TableRow) root.findViewById(R.id.rowCustom3);
        customRow[4] = (TableRow) root.findViewById(R.id.rowCustom4);
        customRow[5] = (TableRow) root.findViewById(R.id.rowCustom5);
        customRow[6] = (TableRow) root.findViewById(R.id.rowCustom6);
        customRow[7] = (TableRow) root.findViewById(R.id.rowCustom7);

        for (int i = 0; i < Controller.MAX_CUSTOM_VARIABLES; i++ ) {
            customText[i] = (TextView) customRow[i].findViewById(R.id.rowValue);
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

    private void setLabel(int channel, String label) {
        ((TextView) customRow[channel].findViewById(R.id.rowTitle)).setText(label);
        String s = String.format("%s %d", getResources().getString(R.string.labelCustom), channel);
        ((TextView) customRow[channel].findViewById(R.id.rowSubTitle)).setText(s);
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
        customRow[device].setVisibility(v);
    }

    private void updateLabelsAndVisibility() {
        Log.d(TAG, "updateLabelsAndVisibility");
        RAApplication raApp = (RAApplication)getActivity().getApplication();
        RAPreferences raPrefs = raApp.raprefs;
        for(int i = 0; i < Controller.MAX_CUSTOM_VARIABLES; i++) {
            setLabel(i, raPrefs.getCustomModuleChannelLabel(i));
            // TODO add in visibility functions
            //setVisibility(i, true);
        }
    }

    private String[] getValues(Cursor c) {
        return new String[] {
                c.getString(c.getColumnIndex(StatusTable.COL_C0)),
                c.getString(c.getColumnIndex(StatusTable.COL_C1)),
                c.getString(c.getColumnIndex(StatusTable.COL_C2)),
                c.getString(c.getColumnIndex(StatusTable.COL_C3)),
                c.getString(c.getColumnIndex(StatusTable.COL_C4)),
                c.getString(c.getColumnIndex(StatusTable.COL_C5)),
                c.getString(c.getColumnIndex(StatusTable.COL_C6)),
                c.getString(c.getColumnIndex(StatusTable.COL_C7))
        };
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
            v = ((StatusFragment) getParentFragment()).getNeverValues(Controller.MAX_CUSTOM_VARIABLES);
        }
        c.close();

        f.updateDisplayText(updateStatus);
        for (int i = 0; i < Controller.MAX_CUSTOM_VARIABLES; i++ ) {
            customText[i].setText(v[i]);
        }
    }
}
