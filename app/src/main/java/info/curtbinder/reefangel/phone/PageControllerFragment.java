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

public class PageControllerFragment extends Fragment
        implements PageRefreshInterface,
        View.OnLongClickListener {

    private static final String TAG = PageControllerFragment.class.getSimpleName();
    //Context ctx; // saved context from parent
    private TextView[] deviceText =
            new TextView[Controller.MAX_CONTROLLER_VALUES];
    private TableRow[] deviceRow =
            new TableRow[Controller.MAX_CONTROLLER_VALUES];
    private short dpValue;
    private short apValue;
    private short dp2Value;
    private short ap2Value;

    public static PageControllerFragment newInstance() {
        return new PageControllerFragment();
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
        deviceRow[Globals.DP2_INDEX] = (TableRow) root.findViewById(R.id.dp2_row);
        deviceRow[Globals.AP2_INDEX] = (TableRow) root.findViewById(R.id.ap2_row);
        deviceRow[Globals.ATOLO_INDEX] = (TableRow) root.findViewById(R.id.atolow_row);
        deviceRow[Globals.ATOHI_INDEX] = (TableRow) root.findViewById(R.id.atohi_row);
        deviceRow[Globals.SALINITY_INDEX] = (TableRow) root.findViewById(R.id.salinity_row);
        deviceRow[Globals.ORP_INDEX] = (TableRow) root.findViewById(R.id.orp_row);
        deviceRow[Globals.PHE_INDEX] = (TableRow) root.findViewById(R.id.phe_row);
        deviceRow[Globals.WL_INDEX] = (TableRow) root.findViewById(R.id.water_row);
        deviceRow[Globals.WL1_INDEX] = (TableRow) root.findViewById(R.id.water1_row);
        deviceRow[Globals.WL2_INDEX] = (TableRow) root.findViewById(R.id.water2_row);
        deviceRow[Globals.WL3_INDEX] = (TableRow) root.findViewById(R.id.water3_row);
        deviceRow[Globals.WL4_INDEX] = (TableRow) root.findViewById(R.id.water4_row);
        deviceRow[Globals.HUMIDITY_INDEX] = (TableRow) root.findViewById(R.id.humidity_row);
        deviceRow[Globals.PAR_INDEX] = (TableRow) root.findViewById(R.id.par_row);

        for (int i = 0; i < Controller.MAX_CONTROLLER_VALUES; i++) {
            deviceText[i] = (TextView) deviceRow[i].findViewById(R.id.rowValue);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        updateLabelsAndVisibility();
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
        deviceText[Globals.AP_INDEX].setLongClickable(fClickable);
        deviceText[Globals.AP_INDEX].setOnLongClickListener(l);
        deviceText[Globals.DP_INDEX].setLongClickable(fClickable);
        deviceText[Globals.DP_INDEX].setOnLongClickListener(l);
        deviceText[Globals.AP2_INDEX].setLongClickable(fClickable);
        deviceText[Globals.AP2_INDEX].setOnLongClickListener(l);
        deviceText[Globals.DP2_INDEX].setLongClickable(fClickable);
        deviceText[Globals.DP2_INDEX].setOnLongClickListener(l);
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
            case R.id.dp_row:
                f.displayOverrideDialog(Globals.OVERRIDE_DAYLIGHT, dpValue);
                break;
            case R.id.ap_row:
                f.displayOverrideDialog(Globals.OVERRIDE_ACTINIC, apValue);
                break;
            case R.id.dp2_row:
                f.displayOverrideDialog(Globals.OVERRIDE_DAYLIGHT2, dp2Value);
                break;
            case R.id.ap2_row:
                f.displayOverrideDialog(Globals.OVERRIDE_ACTINIC2, ap2Value);
                break;
        }
        return true;
    }

    private void updateLabelsAndVisibility() {
        Log.d(TAG, "updateLabelsAndVisibility");
        RAApplication raApp = (RAApplication)getActivity().getApplication();
        RAPreferences raPrefs = raApp.raprefs;
        // TODO update for new water level and humidity labels
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
            case Globals.DP2_INDEX:
                resId = R.string.labelDP2;
                break;
            case Globals.AP2_INDEX:
                resId = R.string.labelAP2;
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
            case Globals.HUMIDITY_INDEX:
                resId = R.string.labelHumidity;
                break;
            case Globals.PAR_INDEX:
                resId = R.string.labelPar;
                break;
            case Globals.WL_INDEX:
            case Globals.WL1_INDEX:
            case Globals.WL2_INDEX:
            case Globals.WL3_INDEX:
            case Globals.WL4_INDEX:
                RAApplication raApp = (RAApplication)getActivity().getApplication();
                return raApp.raprefs.getWaterLevelDefaultLabel(index - Globals.WL_INDEX);
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
            //Log.d(TAG, device + " visible");
            v = View.VISIBLE;
        } else {
            //Log.d(TAG, device + " gone");
            v = View.GONE;
        }
        deviceRow[device].setVisibility(v);
    }

    private String[] getValues(Cursor c) {
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
        // update the ap & dp values
        dpValue = c.getShort(c.getColumnIndex(StatusTable.COL_DP));
        apValue = c.getShort(c.getColumnIndex(StatusTable.COL_AP));
        dp2Value = c.getShort(c.getColumnIndex(StatusTable.COL_PWMD2));
        ap2Value = c.getShort(c.getColumnIndex(StatusTable.COL_PWMA2));
        return new String[]{c.getString(c.getColumnIndex(StatusTable.COL_T1)),
                c.getString(c.getColumnIndex(StatusTable.COL_T2)),
                c.getString(c.getColumnIndex(StatusTable.COL_T3)),
                c.getString(c.getColumnIndex(StatusTable.COL_PH)),
                Controller.getPWMDisplayValue(dpValue,
                        c.getShort(c.getColumnIndex(StatusTable.COL_PWMDO))),
                Controller.getPWMDisplayValue(apValue,
                        c.getShort(c.getColumnIndex(StatusTable.COL_PWMAO))),
                Controller.getPWMDisplayValue(dp2Value,
                        c.getShort(c.getColumnIndex(StatusTable.COL_PWMD2O))),
                Controller.getPWMDisplayValue(ap2Value,
                        c.getShort(c.getColumnIndex(StatusTable.COL_PWMA2O))),
                l, h,
                c.getString(c.getColumnIndex(StatusTable.COL_SAL)) + " ppt",
                c.getString(c.getColumnIndex(StatusTable.COL_ORP)) + " mV",
                c.getString(c.getColumnIndex(StatusTable.COL_PHE)),
                c.getString(c.getColumnIndex(StatusTable.COL_WL)) + "%",
                c.getString(c.getColumnIndex(StatusTable.COL_WL1)) + "%",
                c.getString(c.getColumnIndex(StatusTable.COL_WL2)) + "%",
                c.getString(c.getColumnIndex(StatusTable.COL_WL3)) + "%",
                c.getString(c.getColumnIndex(StatusTable.COL_WL4)) + "%",
                c.getString(c.getColumnIndex(StatusTable.COL_HUM)) + "%",
                c.getString(c.getColumnIndex(StatusTable.COL_PAR))};
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
            v = f.getNeverValues(Controller.MAX_CONTROLLER_VALUES);
        }
        c.close();

        f.updateDisplayText(updateStatus);
        // set ATO LO and HI to icons instead of text
        for (int i = 0; i < Controller.MAX_CONTROLLER_VALUES; i++ ) {
            deviceText[i].setText(v[i]);
        }
    }
}
