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
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableRow;
import android.widget.TextView;

import info.curtbinder.reefangel.controller.Controller;
import info.curtbinder.reefangel.db.StatusProvider;
import info.curtbinder.reefangel.db.StatusTable;

/**
 * Created by binder on 7/26/14.
 */
public class PageAIFragment extends Fragment
    implements PageRefreshInterface, View.OnLongClickListener {

    private static final String TAG = PageAIFragment.class.getSimpleName();
    private TextView[] aiText = new TextView[Controller.MAX_AI_CHANNELS];
    private short[] aiValues = new short[Controller.MAX_AI_CHANNELS];

    public static PageAIFragment newInstance() {
        PageAIFragment p = new PageAIFragment();
        return p;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.page_ai, container, false);
        // do something with the rootView
        findViews(rootView);
        return rootView;
    }

    private void findViews(View root) {
        TableRow tr;
        tr = (TableRow) root.findViewById(R.id.rowAIWhite);
        aiText[0] = (TextView) tr.findViewById(R.id.rowValue);
        setRowTitle(tr, R.string.labelWhite);
        tr = (TableRow) root.findViewById(R.id.rowAIBlue);
        aiText[1] = (TextView) tr.findViewById(R.id.rowValue);
        setRowTitle(tr, R.string.labelBlue);
        tr = (TableRow) root.findViewById(R.id.rowAIRoyalBlue);
        aiText[2] = (TextView) tr.findViewById(R.id.rowValue);
        setRowTitle(tr, R.string.labelRoyalBlue);

        for (int i = 0; i < Controller.MAX_AI_CHANNELS; i++ ) {
            aiText[i].setLongClickable(true);
            aiText[i].setOnLongClickListener(this);
        }
    }

    private void setRowTitle(TableRow row, int labelId) {
        ((TextView) row.findViewById(R.id.rowTitle)).setText(labelId);
    }

    private void setRowTitle(TableRow row, String label) {
        ((TextView) row.findViewById(R.id.rowTitle)).setText(label);
    }

    public void setLabel(int channel, String label) {
        TableRow tr = (TableRow) aiText[channel].getParent();
        setRowTitle(tr, label);
    }

    public void updatePWMValues(short[] v) {
        for ( int i = 0; i < Controller.MAX_AI_CHANNELS; i++ ) {
            aiValues[i] = v[i];
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshData();
    }

    @Override
    public boolean onLongClick(View v) {
        View parent = (View) v.getParent();
        StatusFragment f = (StatusFragment) getParentFragment();
        // TODO call displayOverridePopup, add function to base fragment
        switch (parent.getId()) {
            default:
                return false;
            case R.id.rowAIWhite:
                f.testFunction("White");
                break;
            case R.id.rowAIBlue:
                f.testFunction("Blue");
                break;
            case R.id.rowAIRoyalBlue:
                f.testFunction("RoyalBlue");
                break;
        }
        return true;
    }

    public short[] getAIValues(Cursor c) {
        // TODO how is this function used??
        short[] v = new short[Controller.MAX_AI_CHANNELS];
        v[0] = c.getShort(c.getColumnIndex(StatusTable.COL_AIW));
        v[1] = c.getShort(c.getColumnIndex(StatusTable.COL_AIB));
        v[2] = c.getShort(c.getColumnIndex(StatusTable.COL_AIRB));
        return v;
    }

    private String[] getValues(Cursor c) {
        String[] sa = new String[Controller.MAX_AI_CHANNELS];
        sa[Controller.AI_WHITE] = Controller.getPWMDisplayValue(
                c.getShort(c.getColumnIndex(StatusTable.COL_AIW)),
                c.getShort(c.getColumnIndex(StatusTable.COL_AIWO)));
        sa[Controller.AI_BLUE] = Controller.getPWMDisplayValue(
                c.getShort(c.getColumnIndex(StatusTable.COL_AIB)),
                c.getShort(c.getColumnIndex(StatusTable.COL_AIBO)));
        sa[Controller.AI_ROYALBLUE] = Controller.getPWMDisplayValue(
                c.getShort(c.getColumnIndex(StatusTable.COL_AIRB)),
                c.getShort(c.getColumnIndex(StatusTable.COL_AIRBO)));
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
            v = ((StatusFragment) getParentFragment()).getNeverValues(Controller.MAX_AI_CHANNELS);
        }
        c.close();

        f.updateDisplayText(updateStatus);
        for ( int i = 0; i < Controller.MAX_AI_CHANNELS; i++ ) {
            aiText[i].setText(v[i]);
        }
    }

    @Override
    public String getPageTitle() {
        return getString(R.string.labelAI);
    }
}
