package info.curtbinder.reefangel.phone;

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

public class PageControllerFragment extends Fragment
        implements PageRefreshInterface {

    private static final String TAG = PageControllerFragment.class.getSimpleName();
    //Context ctx; // saved context from parent
    private TextView[] deviceText =
            new TextView[Controller.MAX_CONTROLLER_VALUES];
    private TableRow[] deviceRow =
            new TableRow[Controller.MAX_CONTROLLER_VALUES];

    public static PageControllerFragment newInstance() {
        PageControllerFragment p = new PageControllerFragment();
        return p;
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.page_controller, container, false);
        // do something with the rootView
        findViews(rootView);
        return rootView;
    }

    private void findViews(View root) {
        deviceRow[Globals.T1_INDEX] = (TableRow) root.findViewById(R.id.t1_row);
        deviceRow[Globals.T2_INDEX] = (TableRow) root.findViewById(R.id.t2_row);
        deviceRow[Globals.T3_INDEX] = (TableRow) root.findViewById(R.id.t3_row);
        deviceRow[Globals.PH_INDEX] = (TableRow) root.findViewById(R.id.ph_row);
        deviceRow[Globals.DP_INDEX] = (TableRow) root.findViewById(R.id.dp_row);
        deviceRow[Globals.AP_INDEX] = (TableRow) root.findViewById(R.id.ap_row);
        deviceRow[Globals.ATOLO_INDEX] = (TableRow) root.findViewById(R.id.atolow_row);
        deviceRow[Globals.ATOHI_INDEX] = (TableRow) root.findViewById(R.id.atohi_row);
        deviceRow[Globals.SALINITY_INDEX] = (TableRow) root.findViewById(R.id.salinity_row);
        deviceRow[Globals.ORP_INDEX] = (TableRow) root.findViewById(R.id.orp_row);
        deviceRow[Globals.PHE_INDEX] = (TableRow) root.findViewById(R.id.phe_row);
        deviceRow[Globals.WL_INDEX] = (TableRow) root.findViewById(R.id.water_row);

        for (int i = 0; i < Controller.MAX_CONTROLLER_VALUES; i++) {
            deviceText[i] =
                    (TextView) deviceRow[i].findViewById(R.id.rowValue);
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

    private void updateLabelsAndVisibility() {
        Log.d(TAG, "updateLabelsAndVisibility");
        RAApplication raApp = (RAApplication)getActivity().getApplication();
        RAPreferences raPrefs = raApp.raprefs;
        for(int i = 0; i < Controller.MAX_CONTROLLER_VALUES; i++) {
            setLabel(i, raPrefs.getControllerLabel(i), getDeviceSubtitle(i));
            setVisibility(i, raPrefs.getControllerVisibility(i));
        }
    }

    private String getDeviceSubtitle(int index) {
        int resId;
        switch(index){
            default:
            case Globals.T1_INDEX:
                resId = R.string.labelTemp1;
                break;
            case Globals.T2_INDEX:
                resId = R.string.labelTemp2;
                break;
            case Globals.T3_INDEX:
                resId = R.string.labelTemp3;
                break;
            case Globals.PH_INDEX:
                resId = R.string.labelPH;
                break;
            case Globals.DP_INDEX:
                resId = R.string.labelDP;
                break;
            case Globals.AP_INDEX:
                resId = R.string.labelAP;
                break;
            case Globals.ATOLO_INDEX:
                resId = R.string.labelAtoLow;
                break;
            case Globals.ATOHI_INDEX:
                resId = R.string.labelAtoHigh;
                break;
            case Globals.SALINITY_INDEX:
                resId = R.string.labelSalinity;
                break;
            case Globals.ORP_INDEX:
                resId = R.string.labelORP;
                break;
            case Globals.PHE_INDEX:
                resId = R.string.labelPHExp;
                break;
            case Globals.WL_INDEX:
                resId = R.string.labelWaterLevel;
                break;
        }
        return getString(resId);
    }

    private void setLabel(int device, String title, String subtitle) {
        TextView tv;
        tv = (TextView) deviceRow[device].findViewById(R.id.rowTitle);
        tv.setText(title);
        tv = (TextView) deviceRow[device].findViewById(R.id.rowSubTitle);
        tv.setText(subtitle);
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
        deviceRow[device].setVisibility(v);
    }

    private void updateData() {
        Log.d(TAG, "updateData");
        Uri uri = Uri.parse(StatusProvider.CONTENT_URI + "/" + StatusProvider.PATH_LATEST);
        Cursor c = getActivity().getContentResolver().query(uri, null, null, null,
                StatusTable.COL_ID + " DESC");
        String updateStatus;
        String[] v = new String[Controller.MAX_CONTROLLER_VALUES];
        if (c.moveToFirst()) {
            updateStatus = c.getString(c.getColumnIndex(StatusTable.COL_LOGDATE));
            v = getControllerValues(c);
        } else {
            updateStatus = getString(R.string.messageNever);
            v = ((StatusFragment) getParentFragment()).getNeverValues(Controller.MAX_CONTROLLER_VALUES);
        }
        c.close();

        ((StatusFragment) getParentFragment()).updateDisplayText(updateStatus);
        // set ATO LO and HI to icons instead of text
        for (int i = 0; i < Controller.MAX_CONTROLLER_VALUES; i++ ) {
            deviceText[i].setText(v[i]);
        }
    }

    private String[] getControllerValues(Cursor c) {
        // FIXME switch to only setting the string to 1 or 0
        // FIXME so the controllerpage can easily update the images
        String l, h;
        if (c.getShort(c.getColumnIndex(StatusTable.COL_ATOLO)) == 1)
            l = getString(R.string.labelON); // ACTIVE, GREEN, ON
        else
            l = getString(R.string.labelOFF); // INACTIVE, RED, OFF
        if (c.getShort(c.getColumnIndex(StatusTable.COL_ATOHI)) == 1)
            h = getString(R.string.labelON); // ACTIVE, GREEN, ON
        else
            h = getString(R.string.labelOFF); // INACTIVE, RED, OFF
        return new String[]{c.getString(c.getColumnIndex(StatusTable.COL_T1)),
                c.getString(c.getColumnIndex(StatusTable.COL_T2)),
                c.getString(c.getColumnIndex(StatusTable.COL_T3)),
                c.getString(c.getColumnIndex(StatusTable.COL_PH)),
                c.getString(c.getColumnIndex(StatusTable.COL_DP)) + "%",
                c.getString(c.getColumnIndex(StatusTable.COL_AP)) + "%",
                l, h,
                c.getString(c.getColumnIndex(StatusTable.COL_SAL)) + " ppt",
                c.getString(c.getColumnIndex(StatusTable.COL_ORP)) + " mV",
                c.getString(c.getColumnIndex(StatusTable.COL_PHE)),
                c.getString(c.getColumnIndex(StatusTable.COL_WL)) + "%"};
    }


    @Override
    public void refreshData() {
        updateData();
    }
}
