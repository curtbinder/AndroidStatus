package info.curtbinder.reefangel.phone.pages;

/*
 * Copyright (c) 2011-12 by Curt Binder (http://curtbinder.info)
 * 
 * This work is made available under the terms of the Creative Commons
 * Attribution-NonCommercial-ShareAlike 3.0 Unported License
 * http://creativecommons.org/licenses/by-nc-sa/3.0/
 */

import info.curtbinder.reefangel.controller.Controller;
import info.curtbinder.reefangel.phone.R;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TableRow;
import android.widget.TextView;

public class ControllerPage extends ScrollView {
	private static final String TAG = ControllerPage.class.getSimpleName();

	public static final int T1_INDEX = 0;
	public static final int T2_INDEX = 1;
	public static final int T3_INDEX = 2;
	public static final int PH_INDEX = 3;
	public static final int DP_INDEX = 4;
	public static final int AP_INDEX = 5;
	public static final int SALINITY_INDEX = 6;
	public static final int ORP_INDEX = 7;
	public static final int PHE_INDEX = 8;
	public static final int WL_INDEX = 9;

	Context ctx; // saved context from parent
	private TextView[] deviceText;
	private TextView[] deviceLabel;
	private TableRow[] deviceRow;

	public ControllerPage ( Context context ) {
		super( context );
		addViewsFromLayout( context );
		ctx = context;
		// setDefaults();
	}

	public ControllerPage ( Context context, AttributeSet attrs ) {
		super( context, attrs );
		addViewsFromLayout( context );
		ctx = context;
		// setDefaults();
	}

	private void addViewsFromLayout ( Context context ) {
		LayoutInflater layoutInflater =
				(LayoutInflater) context
						.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
		layoutInflater.inflate( R.layout.controller, this );
		deviceText = new TextView[Controller.MAX_CONTROLLER_VALUES];
		deviceLabel = new TextView[Controller.MAX_CONTROLLER_VALUES];
		deviceRow = new TableRow[Controller.MAX_CONTROLLER_VALUES];
		for ( int i = 0; i < Controller.MAX_CONTROLLER_VALUES; i++ ) {
			deviceText[i] = new TextView( context );
			deviceLabel[i] = new TextView( context );
			deviceRow[i] = new TableRow( context );
		}
		findViews();
	}

	private void findViews ( ) {
		deviceText[T1_INDEX] = (TextView) findViewById( R.id.temp1 );
		deviceText[T2_INDEX] = (TextView) findViewById( R.id.temp2 );
		deviceText[T3_INDEX] = (TextView) findViewById( R.id.temp3 );
		deviceText[PH_INDEX] = (TextView) findViewById( R.id.ph );
		deviceText[DP_INDEX] = (TextView) findViewById( R.id.dp );
		deviceText[AP_INDEX] = (TextView) findViewById( R.id.ap );
		deviceText[SALINITY_INDEX] = (TextView) findViewById( R.id.salinity );
		deviceText[ORP_INDEX] = (TextView) findViewById( R.id.orp );
		deviceText[PHE_INDEX] = (TextView) findViewById( R.id.phe );
		deviceText[WL_INDEX] = (TextView) findViewById( R.id.water );

		deviceLabel[T1_INDEX] = (TextView) findViewById( R.id.t1_label );
		deviceLabel[T2_INDEX] = (TextView) findViewById( R.id.t2_label );
		deviceLabel[T3_INDEX] = (TextView) findViewById( R.id.t3_label );
		deviceLabel[PH_INDEX] = (TextView) findViewById( R.id.ph_label );
		deviceLabel[DP_INDEX] = (TextView) findViewById( R.id.dp_label );
		deviceLabel[AP_INDEX] = (TextView) findViewById( R.id.ap_label );
		deviceLabel[SALINITY_INDEX] =
				(TextView) findViewById( R.id.salinity_label );
		deviceLabel[ORP_INDEX] = (TextView) findViewById( R.id.orp_label );
		deviceLabel[PHE_INDEX] = (TextView) findViewById( R.id.phe_label );
		deviceLabel[WL_INDEX] = (TextView) findViewById( R.id.water_label );

//		deviceRow[T1_INDEX] = (TableRow) findViewById( R.id.t1_row );
//		deviceRow[T2_INDEX] = (TableRow) findViewById( R.id.t2_row );
//		deviceRow[T3_INDEX] = (TableRow) findViewById( R.id.t3_row );
//		deviceRow[PH_INDEX] = (TableRow) findViewById( R.id.ph_row );
//		deviceRow[DP_INDEX] = (TableRow) findViewById( R.id.dp_row );
//		deviceRow[AP_INDEX] = (TableRow) findViewById( R.id.ap_row );
//		deviceRow[SALINITY_INDEX] = (TableRow) findViewById( R.id.salinity_row );
//		deviceRow[ORP_INDEX] = (TableRow) findViewById( R.id.orp_row );
//		deviceRow[PHE_INDEX] = (TableRow) findViewById( R.id.phe_row );
//		deviceRow[WL_INDEX] = (TableRow) findViewById( R.id.water_row );

	}

	public void setLabel ( int device, String label ) {
		deviceLabel[device].setText( label );
	}

	public void setVisibility ( int device, boolean fVisible ) {
		int v;
		if ( fVisible ) {
			Log.d( TAG, device + " visible" );
			v = View.VISIBLE;
		} else {
			Log.d( TAG, device + " gone" );
			v = View.GONE;
		}
		deviceText[device].setVisibility( v );
		deviceLabel[device].setVisibility( v );
		//deviceRow[device].setVisibility( v );
	}

	public void updateDisplay ( String[] v ) {
		deviceText[T1_INDEX].setText( v[T1_INDEX] );
		deviceText[T2_INDEX].setText( v[T2_INDEX] );
		deviceText[T3_INDEX].setText( v[T3_INDEX] );
		deviceText[PH_INDEX].setText( v[PH_INDEX] );
		deviceText[DP_INDEX].setText( v[DP_INDEX] );
		deviceText[AP_INDEX].setText( v[AP_INDEX] );
		deviceText[SALINITY_INDEX].setText( v[SALINITY_INDEX] );
		deviceText[ORP_INDEX].setText( v[ORP_INDEX] );
		deviceText[PHE_INDEX].setText( v[PHE_INDEX] );
		deviceText[WL_INDEX].setText( v[WL_INDEX] );
	}

}
