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
 *
 * Status page for the I/O expansion module
 */
public class PageIOFragment extends Fragment
    implements PageRefreshInterface {
    private static final String TAG = PageIOFragment.class.getSimpleName();

    private TextView[] ioText = new TextView[Controller.MAX_IO_CHANNELS];
    private TableRow[] ioRow = new TableRow[Controller.MAX_IO_CHANNELS];

    public static PageIOFragment newInstance() {
        return new PageIOFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.page_io, container, false);
        findViews(rootView);
        return rootView;
    }

    private void findViews(View root) {
        ioRow[0] = (TableRow) root.findViewById(R.id.rowIO0);
        ioRow[1] = (TableRow) root.findViewById(R.id.rowIO1);
        ioRow[2] = (TableRow) root.findViewById(R.id.rowIO2);
        ioRow[3] = (TableRow) root.findViewById(R.id.rowIO3);
        ioRow[4] = (TableRow) root.findViewById(R.id.rowIO4);
        ioRow[5] = (TableRow) root.findViewById(R.id.rowIO5);

        for ( int i = 0; i < Controller.MAX_IO_CHANNELS; i++ ) {
            ioText[i] = (TextView) ioRow[i].findViewById(R.id.rowValue);
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
        ((TextView) ioRow[channel].findViewById(R.id.rowTitle)).setText(label);
        String s = String.format(Locale.getDefault(),
                "%s %d", getResources().getString(R.string.labelIO), channel);
        ((TextView) ioRow[channel].findViewById(R.id.rowSubTitle)).setText(s);
    }

    private void updateLabelsAndVisibility() {
        Log.d(TAG, "updateLabelsAndVisibility");
        RAApplication raApp = (RAApplication)getActivity().getApplication();
        RAPreferences raPrefs = raApp.raprefs;
        for(int i = 0; i < Controller.MAX_IO_CHANNELS; i++) {
            setLabel(i, raPrefs.getDimmingModuleChannelLabel(i));
            // TODO add in visibility functions
            //setVisibility(i, true);
        }
    }

    private String[] getValues(Cursor c) {
        String sa[] = new String[Controller.MAX_IO_CHANNELS];
        short io = c.getShort(c.getColumnIndex(StatusTable.COL_IO));
        String s;
        for ( byte i = 0; i < Controller.MAX_IO_CHANNELS; i++ ) {
            if ( Controller.getIOChannel(io, i) ) {
                s = getString(R.string.labelOFF);
            } else {
                s = getString(R.string.labelON);
            }
            sa[i] = s;
        }
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
            updateStatus = Utils.getDisplayDate(c.getString(c.getColumnIndex(StatusTable.COL_LOGDATE)));
            v = getValues(c);
        } else {
            updateStatus = getString(R.string.messageNever);
            v = f.getNeverValues(Controller.MAX_IO_CHANNELS);
        }
        c.close();

        f.updateDisplayText(updateStatus);
        for (int i = 0; i < Controller.MAX_IO_CHANNELS; i++ ) {
            ioText[i].setText(v[i]);
        }
    }
}
