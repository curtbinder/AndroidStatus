/*
 * Copyright (c) 2011-2013 by Curt Binder (http://curtbinder.info)
 * 
 * This work is made available under the terms of the Creative Commons
 * Attribution-NonCommercial-ShareAlike 3.0 Unported License
 * http://creativecommons.org/licenses/by-nc-sa/3.0/
 */

package info.curtbinder.reefangel.phone.pages;

import info.curtbinder.reefangel.controller.Controller;
import info.curtbinder.reefangel.phone.R;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TableRow;
import android.widget.TextView;

public class ControllerPage extends RAPage {
	private static final String TAG = ControllerPage.class.getSimpleName();

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
	public static final int WL1_INDEX = 12;
	public static final int WL2_INDEX = 13;
	public static final int WL3_INDEX = 14;
	public static final int WL4_INDEX = 15;
	public static final int HUMIDITY_INDEX = 16;

	Context ctx; // saved context from parent
	private TextView[] deviceText =
			new TextView[Controller.MAX_CONTROLLER_VALUES];
	private TableRow[] deviceRow =
			new TableRow[Controller.MAX_CONTROLLER_VALUES];
	private int[] colors = new int[Controller.MAX_CONTROLLER_VALUES];

	public ControllerPage ( Context context ) {
		super( context );
		ctx = context;
		addViewsFromLayout( context );
	}

	public ControllerPage ( Context context, AttributeSet attrs ) {
		super( context, attrs );
		ctx = context;
		addViewsFromLayout( context );
	}

	private void addViewsFromLayout ( Context context ) {
		LayoutInflater layoutInflater =
				(LayoutInflater) context
						.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
		layoutInflater.inflate( R.layout.controller, this );
		findViews();
	}

	private void findViews ( ) {
		colors[T1_INDEX] = ctx.getResources().getColor( R.color.t1 );
		colors[T2_INDEX] = ctx.getResources().getColor( R.color.t2 );
		colors[T3_INDEX] = ctx.getResources().getColor( R.color.t3 );
		colors[PH_INDEX] = ctx.getResources().getColor( R.color.ph );
		colors[DP_INDEX] = ctx.getResources().getColor( R.color.dp );
		colors[AP_INDEX] = ctx.getResources().getColor( R.color.ap );
		colors[ATOLO_INDEX] = ctx.getResources().getColor( R.color.white );
		colors[ATOHI_INDEX] = ctx.getResources().getColor( R.color.white );
		colors[SALINITY_INDEX] = ctx.getResources().getColor( R.color.white );
		colors[ORP_INDEX] = ctx.getResources().getColor( R.color.white );
		colors[PHE_INDEX] = ctx.getResources().getColor( R.color.ph );
		colors[WL_INDEX] = ctx.getResources().getColor( R.color.white );
		colors[WL1_INDEX] = ctx.getResources().getColor( R.color.white );
		colors[WL2_INDEX] = ctx.getResources().getColor( R.color.white );
		colors[WL3_INDEX] = ctx.getResources().getColor( R.color.white );
		colors[WL4_INDEX] = ctx.getResources().getColor( R.color.white );
		colors[HUMIDITY_INDEX] = ctx.getResources().getColor( R.color.white );

		deviceRow[T1_INDEX] = (TableRow) findViewById( R.id.t1_row );
		deviceRow[T2_INDEX] = (TableRow) findViewById( R.id.t2_row );
		deviceRow[T3_INDEX] = (TableRow) findViewById( R.id.t3_row );
		deviceRow[PH_INDEX] = (TableRow) findViewById( R.id.ph_row );
		deviceRow[DP_INDEX] = (TableRow) findViewById( R.id.dp_row );
		deviceRow[AP_INDEX] = (TableRow) findViewById( R.id.ap_row );
		deviceRow[ATOLO_INDEX] = (TableRow) findViewById( R.id.atolow_row );
		deviceRow[ATOHI_INDEX] = (TableRow) findViewById( R.id.atohi_row );
		deviceRow[SALINITY_INDEX] = (TableRow) findViewById( R.id.salinity_row );
		deviceRow[ORP_INDEX] = (TableRow) findViewById( R.id.orp_row );
		deviceRow[PHE_INDEX] = (TableRow) findViewById( R.id.phe_row );
		deviceRow[WL_INDEX] = (TableRow) findViewById( R.id.water_row );
		deviceRow[WL1_INDEX] = (TableRow) findViewById( R.id.water1_row );
		deviceRow[WL2_INDEX] = (TableRow) findViewById( R.id.water2_row );
		deviceRow[WL3_INDEX] = (TableRow) findViewById( R.id.water3_row );
		deviceRow[WL4_INDEX] = (TableRow) findViewById( R.id.water4_row );
		deviceRow[HUMIDITY_INDEX] = (TableRow) findViewById( R.id.humidity_row );

		for ( int i = 0; i < Controller.MAX_CONTROLLER_VALUES; i++ ) {
			deviceText[i] =
					(TextView) deviceRow[i].findViewById( R.id.rowValue );
			deviceText[i].setTextColor( colors[i] );
		}

	}

	public void setLabel ( int device, String title, String subtitle ) {
		TextView tv;
		tv = (TextView) deviceRow[device].findViewById( R.id.rowTitle );
		tv.setText( title );
		tv.setTextColor( colors[device] );
		tv = (TextView) deviceRow[device].findViewById( R.id.rowSubTitle );
		tv.setTextColor( colors[device] );
		tv.setText( subtitle );
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
		deviceRow[device].setVisibility( v );
	}

	public void updateDisplay ( String[] v ) {
		deviceText[T1_INDEX].setText( v[T1_INDEX] );
		deviceText[T2_INDEX].setText( v[T2_INDEX] );
		deviceText[T3_INDEX].setText( v[T3_INDEX] );
		deviceText[PH_INDEX].setText( v[PH_INDEX] );
		deviceText[DP_INDEX].setText( v[DP_INDEX] );
		deviceText[AP_INDEX].setText( v[AP_INDEX] );
		// FIXME instead of setting text, we need to change icon
		deviceText[ATOLO_INDEX].setText( v[ATOLO_INDEX] );
		deviceText[ATOHI_INDEX].setText( v[ATOHI_INDEX] );
		deviceText[SALINITY_INDEX].setText( v[SALINITY_INDEX] );
		deviceText[ORP_INDEX].setText( v[ORP_INDEX] );
		deviceText[PHE_INDEX].setText( v[PHE_INDEX] );
		deviceText[WL_INDEX].setText( v[WL_INDEX] );
		deviceText[WL1_INDEX].setText( v[WL1_INDEX] );
		deviceText[WL2_INDEX].setText( v[WL2_INDEX] );
		deviceText[WL3_INDEX].setText( v[WL3_INDEX] );
		deviceText[WL4_INDEX].setText( v[WL4_INDEX] );
		deviceText[HUMIDITY_INDEX].setText( v[HUMIDITY_INDEX] );
	}

	@Override
	public String getPageTitle ( ) {
		return ctx.getResources().getString( R.string.labelController );
	}

}
