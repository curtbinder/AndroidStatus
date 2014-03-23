package info.curtbinder.reefangel.phone;

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

public class PageRelayFragment extends Fragment
        implements PageRefreshInterface {

    private static final String TAG = PageRelayFragment.class.getSimpleName();
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
    public View onCreateView(
            LayoutInflater inflater,
            ViewGroup container,
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
//        for ( int i = 0; i < Controller.MAX_RELAY_PORTS; i++ ) {
//            portBtns[i].setOnClickListener( this );
//            portMaskBtns[i].setOnClickListener( this );
//        }
    }

    private void setPortLabels() {
        Resources r = getActivity().getResources();
        setPortLabel(0, "Skimmer", r.getString(R.string.prefPort1LabelTitle));
        setPortLabel(1, "WM Left", r.getString(R.string.prefPort2LabelTitle));
        setPortLabel(2, "Heater", r.getString(R.string.prefPort3LabelTitle));
        setPortLabel(3, "Return", r.getString(R.string.prefPort4LabelTitle));
        setPortLabel(4, "N/A", r.getString(R.string.prefPort5LabelTitle));
        setPortLabel(5, "WM Right", r.getString(R.string.prefPort6LabelTitle));
        setPortLabel(6, "Dim", r.getString(R.string.prefPort7LabelTitle));
        setPortLabel(7, "Non Dim", r.getString(R.string.prefPort8LabelTitle));
        RAApplication raApp = (RAApplication) getActivity().getApplication();
        RAPreferences raPrefs = raApp.raprefs;
        boolean enabled;
        for (int i = 0; i < Controller.MAX_RELAY_PORTS; i++) {
            enabled = raPrefs.getMainRelayControlEnabled(i);
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
            r = c.getShort(c.getColumnIndex(StatusTable.COL_RDATA));
            ron = c.getShort(c.getColumnIndex(StatusTable.COL_RONMASK));
            roff = c.getShort(c.getColumnIndex(StatusTable.COL_ROFFMASK));
        } else {
            updateStatus = getString(R.string.messageNever);
            r = ron = roff = 0;
        }
        c.close();

        ((StatusFragment) getParentFragment()).updateDisplayText(updateStatus);
        updateRelayValues(new Relay(r, ron, roff),
                ((RAApplication) getActivity().getApplication()).raprefs.isCommunicateController());
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
        updateData();
    }
}
