package info.curtbinder.reefangel.phone;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableRow;
import android.widget.TextView;

import info.curtbinder.reefangel.controller.Controller;

public class PageControllerFragment extends Fragment
    implements PageRefreshInterface {

    private static final String TAG = PageControllerFragment.class.getSimpleName();

    public static final int T1_INDEX = 0;
    public static final int T2_INDEX = 1;
    public static final int T3_INDEX = 2;
    public static final int PH_INDEX = 3;
    public static final int DP_INDEX = 4;
    public static final int AP_INDEX = 5;
    public static final int ATOLO_INDEX = 6;
    public static final int ATOHI_INDEX = 7;
    public static final int SALINITY_INDEX = 8;
    public static final int ORP_INDEX = 9;
    public static final int PHE_INDEX = 10;
    public static final int WL_INDEX = 11;

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
	public View onCreateView (
			LayoutInflater inflater,
			ViewGroup container,
			Bundle savedInstanceState ) {
		View rootView = inflater.inflate( R.layout.page_controller, container, false );
        // do something with the rootView
        findViews(rootView);
        return rootView;
	}

    private void findViews(View root) {
        deviceRow[T1_INDEX] = (TableRow) root.findViewById( R.id.t1_row );
        deviceRow[T2_INDEX] = (TableRow) root.findViewById( R.id.t2_row );
        deviceRow[T3_INDEX] = (TableRow) root.findViewById( R.id.t3_row );
        deviceRow[PH_INDEX] = (TableRow) root.findViewById( R.id.ph_row );
        deviceRow[DP_INDEX] = (TableRow) root.findViewById( R.id.dp_row );
        deviceRow[AP_INDEX] = (TableRow) root.findViewById( R.id.ap_row );
        deviceRow[ATOLO_INDEX] = (TableRow) root.findViewById( R.id.atolow_row );
        deviceRow[ATOHI_INDEX] = (TableRow) root.findViewById( R.id.atohi_row );
        deviceRow[SALINITY_INDEX] = (TableRow) root.findViewById( R.id.salinity_row );
        deviceRow[ORP_INDEX] = (TableRow) root.findViewById( R.id.orp_row );
        deviceRow[PHE_INDEX] = (TableRow) root.findViewById( R.id.phe_row );
        deviceRow[WL_INDEX] = (TableRow) root.findViewById( R.id.water_row );

        for ( int i = 0; i < Controller.MAX_CONTROLLER_VALUES; i++ ) {
            deviceText[i] =
                    (TextView) deviceRow[i].findViewById( R.id.rowValue );
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
        // todo load labels from memory or maybe a database
        setLabel(T1_INDEX, "Tank", "T1");
        setLabel(T2_INDEX, "Room", "T2");
        setLabel(T3_INDEX, "Lights", "T3");
        setLabel(PH_INDEX, "pH", "PH");
        setLabel(DP_INDEX, "Daylight", "DP");
        setLabel(AP_INDEX, "Actinic", "AP");
        setLabel(ATOLO_INDEX, "ATO Low", "ATOLOW");
        setLabel(ATOHI_INDEX, "ATO Hight", "ATOHIGH");
        setLabel(SALINITY_INDEX, "Salinity", "Salinity");
        setLabel(ORP_INDEX, "ORP", "ORP");
        setLabel(PHE_INDEX, "PH Expansion", "PHE");
        setLabel(WL_INDEX, "Water", "Water");
    }

    private void setLabel ( int device, String title, String subtitle ) {
        TextView tv;
        tv = (TextView) deviceRow[device].findViewById( R.id.rowTitle );
        tv.setText( title );
        tv = (TextView) deviceRow[device].findViewById( R.id.rowSubTitle );
        tv.setText( subtitle );
    }

    private void setVisibility ( int device, boolean fVisible ) {
        int v;
        if ( fVisible ) {
            Log.d( TAG, device + " visible" );
            v = View.VISIBLE;
        } else {
            Log.d( TAG, device + " gone" );
            v = View.GONE;
        }
        deviceRow[device].setVisibility( v );
    }

    private void updateData() {
        Log.d(TAG, "updateData");
        // todo load from database
        ((StatusFragment)getParentFragment()).updateDisplayText("2 days ago");

        deviceText[T1_INDEX].setText( "77.0" );
        deviceText[T2_INDEX].setText( "65.0" );
        deviceText[T3_INDEX].setText( "100.0" );
        deviceText[PH_INDEX].setText( "8.20" );
        deviceText[DP_INDEX].setText( "100%" );
        deviceText[AP_INDEX].setText( "91%" );
        // FIXME instead of setting text, we need to change icon
        deviceText[ATOLO_INDEX].setText( "OFF" );
        deviceText[ATOHI_INDEX].setText( "ON" );
        deviceText[SALINITY_INDEX].setText( "35.0 ppt" );
        deviceText[ORP_INDEX].setText( "357 mV" );
        deviceText[PHE_INDEX].setText( "8.31" );
        deviceText[WL_INDEX].setText( "76%" );
//        deviceText[T1_INDEX].setText( v[T1_INDEX] );
//        deviceText[T2_INDEX].setText( v[T2_INDEX] );
//        deviceText[T3_INDEX].setText( v[T3_INDEX] );
//        deviceText[PH_INDEX].setText( v[PH_INDEX] );
//        deviceText[DP_INDEX].setText( v[DP_INDEX] );
//        deviceText[AP_INDEX].setText( v[AP_INDEX] );
//        // FIXME instead of setting text, we need to change icon
//        deviceText[ATOLO_INDEX].setText( v[ATOLO_INDEX] );
//        deviceText[ATOHI_INDEX].setText( v[ATOHI_INDEX] );
//        deviceText[SALINITY_INDEX].setText( v[SALINITY_INDEX] );
//        deviceText[ORP_INDEX].setText( v[ORP_INDEX] );
//        deviceText[PHE_INDEX].setText( v[PHE_INDEX] );
//        deviceText[WL_INDEX].setText( v[WL_INDEX] );
    }

    @Override
    public void refreshData() {
        updateData();
    }
}
