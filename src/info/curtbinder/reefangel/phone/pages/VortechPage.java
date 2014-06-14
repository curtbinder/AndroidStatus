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
import info.curtbinder.reefangel.phone.VortechPopupActivity;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.widget.TableRow;
import android.widget.TextView;

public class VortechPage extends RAPage implements OnLongClickListener {
	private static final String TAG = VortechPage.class.getSimpleName();

	Context ctx; // saved context from parent
	private TextView[] vortechText =
			new TextView[Controller.MAX_VORTECH_VALUES];
	private int[] vortechValues = new int[Controller.MAX_VORTECH_VALUES];

	public VortechPage ( Context context ) {
		super( context );
		ctx = context;
		addViewsFromLayout( context );
	}

	public VortechPage ( Context context, AttributeSet attrs ) {
		super( context, attrs );
		ctx = context;
		addViewsFromLayout( context );
	}

	private void addViewsFromLayout ( Context context ) {
		LayoutInflater layoutInflater =
				(LayoutInflater) context
						.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
		layoutInflater.inflate( R.layout.vortech, this );
		findViews();
	}

	private void findViews ( ) {
		TableRow tr;
		TextView tv;
		tr = (TableRow) findViewById( R.id.rowMode );
		vortechText[0] = (TextView) tr.findViewById( R.id.rowValue );
		tv = (TextView) tr.findViewById( R.id.rowTitle );
		tv.setText( R.string.labelMode );
		tr = (TableRow) findViewById( R.id.rowSpeed );
		vortechText[1] = (TextView) tr.findViewById( R.id.rowValue );
		tv = (TextView) tr.findViewById( R.id.rowTitle );
		tv.setText( R.string.labelSpeed );
		tr = (TableRow) findViewById( R.id.rowDuration );
		vortechText[2] = (TextView) tr.findViewById( R.id.rowValue );
		tv = (TextView) tr.findViewById( R.id.rowTitle );
		tv.setText( R.string.labelDuration );
		vortechText[0].setOnLongClickListener( this );
		vortechText[0].setLongClickable( true );
		vortechText[1].setOnLongClickListener( this );
		vortechText[1].setLongClickable( true );
		vortechText[2].setOnLongClickListener( this );
		vortechText[2].setLongClickable( true );
	}

	public void setLabel ( int channel, String label ) {
		TableRow tr = (TableRow) vortechText[channel].getParent();
		((TextView) tr.findViewById( R.id.rowTitle )).setText( label );
	}

	public void updateDisplay ( String[] v ) {
		for ( int i = 0; i < Controller.MAX_VORTECH_VALUES; i++ ) {
			vortechText[i].setText( v[i] );
		}
	}
	
	public void updateValues ( short[] v) {
		for (int i = 0; i < Controller.MAX_VORTECH_VALUES; i++ ) {
			vortechValues[i] = v[i];
		}
	}

	@Override
	public boolean onLongClick ( View v ) {
		// Display popup that allows for memory setting
		// view for long click is embedded in a TableRow
		// get the parent to determine what item was long clicked
		View parent = (View) v.getParent();
		switch ( parent.getId() ) {
			case R.id.rowMode:
				// display mode choices
				Log.d( TAG, "Vortech Mode change" );
				displayVortechPopup( VortechPopupActivity.MODE, 0 );
				break;
			case R.id.rowSpeed:
				// display speed choices
				// Speed 0-100
				Log.d( TAG, "Vortech Speed change" );
				displayVortechPopup( VortechPopupActivity.SPEED, 0 );
				break;
			case R.id.rowDuration:
				// display duration choices
				// Duration 0-255
				Log.d( TAG, "Vortech Duration change" );
				displayVortechPopup( VortechPopupActivity.DURATION, 0 );
				break;
			default:
				return false;
		}
		return true;
	}

	@Override
	public String getPageTitle ( ) {
		return ctx.getString( R.string.labelVortech );
	}
}
