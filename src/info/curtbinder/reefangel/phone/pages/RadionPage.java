/*
 * Copyright (c) 2011-13 by Curt Binder (http://curtbinder.info)
 * 
 * This work is made available under the terms of the Creative Commons
 * Attribution-NonCommercial-ShareAlike 3.0 Unported License
 * http://creativecommons.org/licenses/by-nc-sa/3.0/
 */

package info.curtbinder.reefangel.phone.pages;

import info.curtbinder.reefangel.controller.Controller;
import info.curtbinder.reefangel.phone.Globals;
import info.curtbinder.reefangel.phone.R;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.widget.TableRow;
import android.widget.TextView;

public class RadionPage extends RAPage
	implements OnLongClickListener {
	// private static final String TAG = RadionWidget.class.getSimpleName();

	Context ctx; // saved context from parent
	private TextView[] radionText =
			new TextView[Controller.MAX_RADION_LIGHT_CHANNELS];
	private short[] radionValues = new short[Controller.MAX_RADION_LIGHT_CHANNELS];

	public RadionPage ( Context context ) {
		super( context );
		ctx = context;
		addViewsFromLayout( context );
	}

	public RadionPage ( Context context, AttributeSet attrs ) {
		super( context, attrs );
		ctx = context;
		addViewsFromLayout( context );
	}

	private void addViewsFromLayout ( Context context ) {
		LayoutInflater layoutInflater =
				(LayoutInflater) context
						.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
		layoutInflater.inflate( R.layout.radion, this );
		findViews();
	}

	private void findViews ( ) {
		TableRow tr;
		TextView tv;
		tr = (TableRow) findViewById( R.id.rowWhite );
		radionText[0] = (TextView) tr.findViewById( R.id.rowValue );
		tv = (TextView) tr.findViewById( R.id.rowTitle );
		tv.setText( R.string.labelWhite );
		tr = (TableRow) findViewById( R.id.rowRoyalBlue );
		radionText[1] = (TextView) tr.findViewById( R.id.rowValue );
		radionText[1].setTextColor( ctx.getResources()
				.getColor( R.color.royalblue ) );
		tv = (TextView) tr.findViewById( R.id.rowTitle );
		tv.setTextColor( ctx.getResources().getColor( R.color.royalblue ) );
		tv.setText( R.string.labelRoyalBlue );
		tr = (TableRow) findViewById( R.id.rowRed );
		radionText[2] = (TextView) tr.findViewById( R.id.rowValue );
		radionText[2].setTextColor( ctx.getResources().getColor( R.color.red ) );
		tv = (TextView) tr.findViewById( R.id.rowTitle );
		tv.setTextColor( ctx.getResources().getColor( R.color.red ) );
		tv.setText( R.string.labelRed );
		tr = (TableRow) findViewById( R.id.rowGreen );
		radionText[3] = (TextView) tr.findViewById( R.id.rowValue );
		radionText[3]
				.setTextColor( ctx.getResources().getColor( R.color.green ) );
		tv = (TextView) tr.findViewById( R.id.rowTitle );
		tv.setTextColor( ctx.getResources().getColor( R.color.green ) );
		tv.setText( R.string.labelGreen );
		tr = (TableRow) findViewById( R.id.rowBlue );
		radionText[4] = (TextView) tr.findViewById( R.id.rowValue );
		radionText[4]
				.setTextColor( ctx.getResources().getColor( R.color.blue ) );
		tv = (TextView) tr.findViewById( R.id.rowTitle );
		tv.setTextColor( ctx.getResources().getColor( R.color.blue ) );
		tv.setText( R.string.labelBlue );
		tr = (TableRow) findViewById( R.id.rowIntensity );
		radionText[5] = (TextView) tr.findViewById( R.id.rowValue );
		tv = (TextView) tr.findViewById( R.id.rowTitle );
		tv.setText( R.string.labelIntensity );

		for ( int i = 0; i < Controller.MAX_RADION_LIGHT_CHANNELS; i++ ) {
			radionText[i].setLongClickable( true );
			radionText[i].setOnLongClickListener( this );
		}
	}

	public void setLabel ( int channel, String label ) {
		TableRow tr = (TableRow) radionText[channel].getParent();
		((TextView) tr.findViewById( R.id.rowTitle )).setText( label );
	}

	public void updateDisplay ( String[] v ) {
		for ( int i = 0; i < Controller.MAX_RADION_LIGHT_CHANNELS; i++ ) {
			radionText[i].setText( v[i] );
		}
	}
	
	public void updatePWMValues ( short[] v ) {
		for ( int i = 0; i < Controller.MAX_RADION_LIGHT_CHANNELS; i++ ) {
			radionValues[i] = v[i];
		}
	}

	@Override
	public String getPageTitle ( ) {
		return ctx.getString( R.string.labelRadion );
	}

	@Override
	public boolean onLongClick ( View v ) {
		View parent = (View) v.getParent();
		String msg;
		switch ( parent.getId() ) {
			default:
				return false;
			case R.id.rowWhite:
				msg = getPopupMessage( R.string.labelWhite );
				displayOverridePopup(Globals.OVERRIDE_RF_WHITE, radionValues[0], msg);
				break;
			case R.id.rowRoyalBlue:
				msg = getPopupMessage( R.string.labelRoyalBlue );
				displayOverridePopup(Globals.OVERRIDE_RF_ROYALBLUE, radionValues[1], msg);
				break;
			case R.id.rowRed:
				msg = getPopupMessage( R.string.labelRed );
				displayOverridePopup(Globals.OVERRIDE_RF_RED, radionValues[2], msg);
				break;
			case R.id.rowGreen:
				msg = getPopupMessage( R.string.labelGreen );
				displayOverridePopup(Globals.OVERRIDE_RF_GREEN, radionValues[3], msg);
				break;
			case R.id.rowBlue:
				msg = getPopupMessage( R.string.labelBlue );
				displayOverridePopup(Globals.OVERRIDE_RF_BLUE, radionValues[4], msg);
				break;
			case R.id.rowIntensity:
				msg = getPopupMessage( R.string.labelIntensity );
				displayOverridePopup(Globals.OVERRIDE_RF_INTENSITY, radionValues[5], msg);
				break;
		}
		return true;
	}
	
	private String getPopupMessage ( int stringId ) {
		// TODO consider getting the text from textview widget like the dimming page
		String label = ctx.getString( R.string.labelRadion );
		String channel = ctx.getString( R.string.labelChannel );
		String msg = label + " " + ctx.getString( stringId ) + " " + channel;
		return msg;
	}

}
