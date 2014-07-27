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
import android.content.Intent;
import android.content.res.Resources;
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
import android.widget.ToggleButton;

import info.curtbinder.reefangel.controller.Controller;
import info.curtbinder.reefangel.controller.Relay;
import info.curtbinder.reefangel.db.StatusProvider;
import info.curtbinder.reefangel.db.StatusTable;
import info.curtbinder.reefangel.service.MessageCommands;
import info.curtbinder.reefangel.service.UpdateService;

public class PageRelayFragment extends Fragment
        implements PageRefreshInterface, View.OnClickListener {

    private static final String TAG = PageRelayFragment.class.getSimpleName();
    private static final int COL_R = 0;
    private static final int COL_RON = 1;
    private static final int COL_ROFF = 2;
    private int relayNumber;
    private ToggleButton[] portBtns =
            new ToggleButton[Controller.MAX_RELAY_PORTS];
    private View[] portMaskBtns = new View[Controller.MAX_RELAY_PORTS];

    private boolean[] controlsEnabled = new boolean[Controller.MAX_RELAY_PORTS];

    public PageRelayFragment() {
        relayNumber = 0;
    }

    public PageRelayFragment(int position) {
        relayNumber = position;
    }

    public static PageRelayFragment newInstance(int position) {
        // pass in values to construct a new instance
        PageRelayFragment p = new PageRelayFragment(position);
        return p;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.page_relaybox, container, false);
        findViews(rootView);
        return rootView;
    }

    private void findViews(View root) {
        TableRow tr;
        tr = (TableRow) root.findViewById(R.id.rowPort1);
        portBtns[0] = (ToggleButton) tr.findViewById(R.id.rowToggle);
        portMaskBtns[0] = tr.findViewById(R.id.rowOverrideToggle);
        tr = (TableRow) root.findViewById(R.id.rowPort2);
        portBtns[1] = (ToggleButton) tr.findViewById(R.id.rowToggle);
        portMaskBtns[1] = tr.findViewById(R.id.rowOverrideToggle);
        tr = (TableRow) root.findViewById(R.id.rowPort3);
        portBtns[2] = (ToggleButton) tr.findViewById(R.id.rowToggle);
        portMaskBtns[2] = tr.findViewById(R.id.rowOverrideToggle);
        tr = (TableRow) root.findViewById(R.id.rowPort4);
        portBtns[3] = (ToggleButton) tr.findViewById(R.id.rowToggle);
        portMaskBtns[3] = tr.findViewById(R.id.rowOverrideToggle);
        tr = (TableRow) root.findViewById(R.id.rowPort5);
        portBtns[4] = (ToggleButton) tr.findViewById(R.id.rowToggle);
        portMaskBtns[4] = tr.findViewById(R.id.rowOverrideToggle);
        tr = (TableRow) root.findViewById(R.id.rowPort6);
        portBtns[5] = (ToggleButton) tr.findViewById(R.id.rowToggle);
        portMaskBtns[5] = tr.findViewById(R.id.rowOverrideToggle);
        tr = (TableRow) root.findViewById(R.id.rowPort7);
        portBtns[6] = (ToggleButton) tr.findViewById(R.id.rowToggle);
        portMaskBtns[6] = tr.findViewById(R.id.rowOverrideToggle);
        tr = (TableRow) root.findViewById(R.id.rowPort8);
        portBtns[7] = (ToggleButton) tr.findViewById(R.id.rowToggle);
        portMaskBtns[7] = tr.findViewById(R.id.rowOverrideToggle);
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshButtonEnablement();
        setOnClickListeners();
        setPortLabels();
        refreshData();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    private void refreshButtonEnablement() {
        for (int i = 0; i < Controller.MAX_RELAY_PORTS; i++) {
            boolean enabled = isControlEnabled(i);

            portBtns[i].setEnabled(enabled);
            portMaskBtns[i].setClickable(enabled);
        }
    }

    private boolean isControlEnabled(int port) {
        return controlsEnabled[port];
    }

    private void setControlEnabled(int port, boolean enabled) {
        controlsEnabled[port] = enabled;
        refreshButtonEnablement();
    }

    private void setClickable(boolean clickable) {
        for (int i = 0; i < Controller.MAX_RELAY_PORTS; i++) {
            portBtns[i].setClickable(false);
            portMaskBtns[i].setClickable(false);
        }
    }

    private void setOnClickListeners() {
        for ( int i = 0; i < Controller.MAX_RELAY_PORTS; i++ ) {
            portBtns[i].setOnClickListener(this);
            portMaskBtns[i].setOnClickListener(this);
        }
    }

    private void setPortLabels() {
        Resources r = getActivity().getResources();
        RAApplication raApp = (RAApplication) getActivity().getApplication();
        RAPreferences raPrefs = raApp.raprefs;
        boolean enabled;
        String defaultPort = r.getString(R.string.defaultPortName);
        for (int i = 0; i < Controller.MAX_RELAY_PORTS; i++) {
            setPortLabel(i, raPrefs.getRelayLabel(relayNumber, i), defaultPort + (i+1));
            enabled = raPrefs.getRelayControlEnabled(relayNumber, i);
            setControlEnabled(i, enabled);
        }
    }

    private void setPortLabel(int port, String title, String subtitle) {
        // relay is 0 based
        // label is text to set
        Log.d(TAG, relayNumber + " Label: " + port + ", " + title);
        int id;
        switch (port) {
            default:
            case 0:
                id = R.id.rowPort1;
                break;
            case 1:
                id = R.id.rowPort2;
                break;
            case 2:
                id = R.id.rowPort3;
                break;
            case 3:
                id = R.id.rowPort4;
                break;
            case 4:
                id = R.id.rowPort5;
                break;
            case 5:
                id = R.id.rowPort6;
                break;
            case 6:
                id = R.id.rowPort7;
                break;
            case 7:
                id = R.id.rowPort8;
                break;
        }
        TableRow tr;
        tr = (TableRow) getView().findViewById(id);
        ((TextView) tr.findViewById(R.id.rowTitle)).setText(title);
        ((TextView) tr.findViewById(R.id.rowSubTitle)).setText(subtitle);
    }

    private void updateData() {
        Log.d(TAG, "updateData");
        Uri uri = Uri.parse(StatusProvider.CONTENT_URI + "/" + StatusProvider.PATH_LATEST);
        Cursor c = getActivity().getContentResolver().query(uri, null, null, null,
                StatusTable.COL_ID + " DESC");
        String updateStatus;
        short r, ron, roff;
        if (c.moveToFirst()) {
            updateStatus = c.getString(c.getColumnIndex(StatusTable.COL_LOGDATE));
            r = c.getShort(c.getColumnIndex(getColumnName(COL_R)));
            ron = c.getShort(c.getColumnIndex(getColumnName(COL_RON)));
            roff = c.getShort(c.getColumnIndex(getColumnName(COL_ROFF)));
        } else {
            updateStatus = getString(R.string.messageNever);
            r = ron = roff = 0;
        }
        c.close();

        ((StatusFragment) getParentFragment()).updateDisplayText(updateStatus);
        updateRelayValues(new Relay(r, ron, roff),
                ((RAApplication) getActivity().getApplication()).raprefs.isCommunicateController());
    }

    private String getColumnName(int type) {
        String column = "r";
        if (relayNumber > 0) {
            column += Integer.toString(relayNumber);
        }
        switch(type) {
            case COL_R:
                column += "data";
                break;
            case COL_RON:
                column += "onmask";
                break;
            case COL_ROFF:
                column += "offmask";
                break;
        }
        return column;
    }

    private void updateRelayValues(Relay r, boolean fUseMask) {
        short status;
        for (int i = 0; i < Controller.MAX_RELAY_PORTS; i++) {
            status = r.getPortStatus(i + 1);
            portBtns[i].setChecked(r.isPortOn(i + 1, fUseMask));
            if (((status == Relay.PORT_ON) || (status == Relay.PORT_STATE_OFF))
                    && fUseMask) {
                // masked on or off, show button
                portMaskBtns[i].setVisibility(View.VISIBLE);
            } else {
                portMaskBtns[i].setVisibility(View.INVISIBLE);
            }
        }
    }

    @Override
    public void refreshData() {
        Activity a = getActivity();
        if (a == null) {
            return;
        }
        updateData();
    }

    @Override
    public void onClick(View v) {
        int box = getBoxNumber();
        // inside Log.d, the + is string concatenation
        // so relayNumber + NUM is actually like doing 1 + 1 == 11
        // however, when you get into arithmetic 1 + 1 = 2 and not 11

        // The buttons are nested inside a LinearLayout and then inside a
        // TableRow
        // The TableRow is the View that contains the row id
        int port = 1;
        View parent = (View) v.getParent().getParent();
        switch ( parent.getId() ) {
            default:
            case R.id.rowPort1:
                port = 1;
                break;
            case R.id.rowPort2:
                port = 2;
                break;
            case R.id.rowPort3:
                port = 3;
                break;
            case R.id.rowPort4:
                port = 4;
                break;
            case R.id.rowPort5:
                port = 5;
                break;
            case R.id.rowPort6:
                port = 6;
                break;
            case R.id.rowPort7:
                port = 7;
                break;
            case R.id.rowPort8:
                port = 8;
                break;
        }
        if ( v.getId() == R.id.rowOverrideToggle ) {
            sendRelayClearMaskTask( box + port );
        } else if ( v.getId() == R.id.rowToggle ) {
            sendRelayToggleTask( box + port );
        }
    }

    private int getBoxNumber ( ) {
        return relayNumber * 10;
    }

    private void sendRelayToggleTask ( int port ) {
        // port is 1 based
        Log.d( TAG, "sendRelayToggleTask" );
        int p = port - getBoxNumber();
        int status = Relay.PORT_STATE_OFF;
        if ( portBtns[p - 1].isChecked() ) {
            status = Relay.PORT_STATE_ON;
        }
        launchRelayToggleTask( port, status );
    }

    private void sendRelayClearMaskTask ( int port ) {
        // port is 1 based
        Log.d( TAG, "sendRelayClearMaskTask" );
        // hide ourself and clear the mask
        int p = port - getBoxNumber();
        portMaskBtns[p - 1].setVisibility( View.INVISIBLE );
        launchRelayToggleTask( port, Relay.PORT_STATE_AUTO );
    }

    private void launchRelayToggleTask ( int relay, int status ) {
        // port is 1 based
        Intent i = new Intent( getActivity(), UpdateService.class );
        i.setAction( MessageCommands.TOGGLE_RELAY_INTENT );
        i.putExtra( MessageCommands.TOGGLE_RELAY_PORT_INT, relay );
        i.putExtra( MessageCommands.TOGGLE_RELAY_MODE_INT, status );
        getActivity().startService(i);
    }
}
