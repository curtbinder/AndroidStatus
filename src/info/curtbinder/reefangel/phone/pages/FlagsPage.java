/*
 * Copyright (c) 2011-2014 by Curt Binder (http://curtbinder.info)
 *
 * This work is made available under the terms of the 
 * Creative Commons Attribution-NonCommercial-ShareAlike 3.0 Unported License
 * http://creativecommons.org/licenses/by-nc-sa/3.0/
 */

package info.curtbinder.reefangel.phone.pages;

import info.curtbinder.reefangel.controller.Controller;
import info.curtbinder.reefangel.phone.R;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.TableRow;
import android.widget.TextView;

public class FlagsPage extends RAPage {

	Context ctx; // saved context from parent
	private TextView[] flagsStatusText = new TextView[Controller.MAX_STATUS_FLAGS];
	private TextView[] flagsAlertText = new TextView[Controller.MAX_ALERT_FLAGS];

	public FlagsPage ( Context context ) {
		super( context );
		addViewsFromLayout( context );
		ctx = context;
	}

	public FlagsPage ( Context context, AttributeSet attrs ) {
		super( context, attrs );
		addViewsFromLayout( context );
		ctx = context;
	}

	private void addViewsFromLayout ( Context context ) {
		LayoutInflater layoutInflater =
				(LayoutInflater) context
						.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
		layoutInflater.inflate( R.layout.flagspage, this );
		findViews();
	}
	
	private void findViews ( ) {
		TableRow tr;
		TextView tv;
		tr = (TableRow) findViewById( R.id.rowAto );
		flagsAlertText[0] = (TextView) tr.findViewById( R.id.rowValue );
		tv = (TextView) tr.findViewById( R.id.rowTitle );
		tv.setText( R.string.labelAtoTimeout );

		tr = (TableRow) findViewById( R.id.rowOverheat );
		flagsAlertText[1] = (TextView) tr.findViewById( R.id.rowValue );
		tv = (TextView) tr.findViewById( R.id.rowTitle );
		tv.setText( R.string.labelOverheatTimeout );
		
		tr = (TableRow) findViewById( R.id.rowBusLock );
		flagsAlertText[2] = (TextView) tr.findViewById( R.id.rowValue );
		tv = (TextView) tr.findViewById( R.id.rowTitle );
		tv.setText( R.string.labelBusLock );
		
		tr = (TableRow) findViewById( R.id.rowLeak );
		flagsAlertText[3] = (TextView) tr.findViewById( R.id.rowValue );
		tv = (TextView) tr.findViewById( R.id.rowTitle );
		tv.setText( R.string.labelLeak );
		
		tr = (TableRow) findViewById( R.id.rowLightsOn );
		flagsStatusText[0] = (TextView) tr.findViewById( R.id.rowValue );
		tv = (TextView) tr.findViewById( R.id.rowTitle );
		tv.setText( R.string.labelLightsOn );
		
		tr = (TableRow) findViewById( R.id.rowFeeding );
		flagsStatusText[1] = (TextView) tr.findViewById( R.id.rowValue );
		tv = (TextView) tr.findViewById( R.id.rowTitle );
		tv.setText( R.string.labelFeedingMode );
		
		tr = (TableRow) findViewById( R.id.rowWaterChange );
		flagsStatusText[2] = (TextView) tr.findViewById( R.id.rowValue );
		tv = (TextView) tr.findViewById( R.id.rowTitle );
		tv.setText( R.string.labelWaterMode );
	}
	
	@Override
	public String getPageTitle ( ) {
		return ctx.getString( R.string.titleFlags );
	}

	public void updateStatus ( short flags ) {
		Log.d("FlagsPage", "UpdateStatus: " + flags);
		int id;
		if ( Controller.isLightsOnFlagSet( flags ) ) {
			id = R.string.labelON;
		} else {
			id = R.string.labelOFF;
		}
		flagsStatusText[0].setText( id );
		if ( Controller.isFeedingFlagSet( flags ) ) {
			id = R.string.labelON;
		} else {
			id = R.string.labelOFF;
		}
		flagsStatusText[1].setText( id );
		if ( Controller.isWaterChangeFlagSet( flags ) ) {
			id = R.string.labelON;
		} else {
			id = R.string.labelOFF;
		}
		flagsStatusText[2].setText( id );
	}
	
	public void updateAlert ( short flags ) {
		Log.d("FlagsPage", "UpdateAlert: " + flags);
		int id;
		if ( Controller.isATOTimeoutFlagSet( flags ) ) {
			id = R.string.labelON;
		} else {
			id = R.string.labelOFF;
		}
		flagsAlertText[0].setText( id );
		if ( Controller.isOverheatFlagSet( flags ) ) {
			id = R.string.labelON;
		} else {
			id = R.string.labelOFF;
		}
		flagsAlertText[1].setText( id );
		if ( Controller.isBusLockFlagSet( flags ) ) {
			id = R.string.labelON;
		} else {
			id = R.string.labelOFF;
		}
		flagsAlertText[2].setText( id );
		if ( Controller.isLeakFlagSet( flags ) ) {
			id = R.string.labelON;
		} else {
			id = R.string.labelOFF;
		}
		flagsAlertText[3].setText( id );
	}
}
