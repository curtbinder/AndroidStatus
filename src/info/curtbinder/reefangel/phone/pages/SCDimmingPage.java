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

import java.util.Locale;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.widget.TableRow;
import android.widget.TextView;

public class SCDimmingPage extends RAPage
	implements OnLongClickListener {
	private static final String TAG = SCDimmingPage.class.getSimpleName();

	Context ctx; // saved context from parent
	private TextView[] pwmeText =
			new TextView[Controller.MAX_SCPWM_EXPANSION_PORTS];
	private TableRow[] pwmeRow =
			new TableRow[Controller.MAX_SCPWM_EXPANSION_PORTS];
	private short[] pwmeValues = new short[Controller.MAX_SCPWM_EXPANSION_PORTS];

	public SCDimmingPage ( Context context ) {
		super( context );
		ctx = context;
		addViewsFromLayout( context );
	}

	public SCDimmingPage ( Context context, AttributeSet attrs ) {
		super( context, attrs );
		ctx = context;
		addViewsFromLayout( context );
	}

	private void addViewsFromLayout ( Context context ) {
		LayoutInflater layoutInflater =
				(LayoutInflater) context
						.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
		layoutInflater.inflate( R.layout.scdimming, this );
		findViews();
		
	}

	private void findViews ( ) {
		pwmeRow[0] = (TableRow) findViewById( R.id.rowSCPWME0 );
		pwmeText[0] = (TextView) pwmeRow[0].findViewById( R.id.rowValue );
		pwmeRow[1] = (TableRow) findViewById( R.id.rowSCPWME1 );
		pwmeText[1] = (TextView) pwmeRow[1].findViewById( R.id.rowValue );
		pwmeRow[2] = (TableRow) findViewById( R.id.rowSCPWME2 );
		pwmeText[2] = (TextView) pwmeRow[2].findViewById( R.id.rowValue );
		pwmeRow[3] = (TableRow) findViewById( R.id.rowSCPWME3 );
		pwmeText[3] = (TextView) pwmeRow[3].findViewById( R.id.rowValue );
		pwmeRow[4] = (TableRow) findViewById( R.id.rowSCPWME4 );
		pwmeText[4] = (TextView) pwmeRow[4].findViewById( R.id.rowValue );
		pwmeRow[5] = (TableRow) findViewById( R.id.rowSCPWME5 );
		pwmeText[5] = (TextView) pwmeRow[5].findViewById( R.id.rowValue );
		pwmeRow[6] = (TableRow) findViewById( R.id.rowSCPWME6 );
		pwmeText[6] = (TextView) pwmeRow[6].findViewById( R.id.rowValue );
		pwmeRow[7] = (TableRow) findViewById( R.id.rowSCPWME7 );
		pwmeText[7] = (TextView) pwmeRow[7].findViewById( R.id.rowValue );
		pwmeRow[8] = (TableRow) findViewById( R.id.rowSCPWME8 );
		pwmeText[8] = (TextView) pwmeRow[8].findViewById( R.id.rowValue );
		pwmeRow[9] = (TableRow) findViewById( R.id.rowSCPWME9 );
		pwmeText[9] = (TextView) pwmeRow[9].findViewById( R.id.rowValue );
		pwmeRow[10] = (TableRow) findViewById( R.id.rowSCPWME10 );
		pwmeText[10] = (TextView) pwmeRow[10].findViewById( R.id.rowValue );
		pwmeRow[11] = (TableRow) findViewById( R.id.rowSCPWME11 );
		pwmeText[11] = (TextView) pwmeRow[11].findViewById( R.id.rowValue );
		pwmeRow[12] = (TableRow) findViewById( R.id.rowSCPWME12 );
		pwmeText[12] = (TextView) pwmeRow[12].findViewById( R.id.rowValue );
		pwmeRow[13] = (TableRow) findViewById( R.id.rowSCPWME13 );
		pwmeText[13] = (TextView) pwmeRow[13].findViewById( R.id.rowValue );
		pwmeRow[14] = (TableRow) findViewById( R.id.rowSCPWME14 );
		pwmeText[14] = (TextView) pwmeRow[14].findViewById( R.id.rowValue );
		pwmeRow[15] = (TableRow) findViewById( R.id.rowSCPWME15 );
		pwmeText[15] = (TextView) pwmeRow[15].findViewById( R.id.rowValue );
		for ( int i = 0; i < Controller.MAX_SCPWM_EXPANSION_PORTS; i++ ) {
			pwmeText[i].setLongClickable( true );
			pwmeText[i].setOnLongClickListener( this );
		}

	}

	public void setLabel ( int channel, String label ) {
		((TextView) pwmeRow[channel].findViewById( R.id.rowTitle ))
				.setText( label );
		String s = String.format( Locale.getDefault(), 
		                          "%s %d", ctx.getString( R.string.labelChannel ), channel );
		((TextView) pwmeRow[channel].findViewById( R.id.rowSubTitle ))
				.setText( s );
	}

	public void setVisibility ( int channel, boolean fVisible ) {
		// this function is most likely unnecessary
		int v;
		if ( fVisible ) {
			Log.d( TAG, channel + " visible" );
			v = View.VISIBLE;
		} else {
			Log.d( TAG, channel + " gone" );
			v = View.GONE;
		}
		pwmeRow[channel].setVisibility( v );
	}

	public void updateDisplay ( String[] v ) {
		for ( int i = 0; i < Controller.MAX_SCPWM_EXPANSION_PORTS; i++ ) {
			pwmeText[i].setText( v[i] );
		}
	}
	
	public void updatePWMValues ( short[] v ) {
		for ( int i = 0; i < Controller.MAX_SCPWM_EXPANSION_PORTS; i++ ) {
			pwmeValues[i] = v[i];
		}
	}

	@Override
	public String getPageTitle ( ) {
		return ctx.getString( R.string.labelSCDimming );
	}

	@Override
	public boolean onLongClick ( View v ) {
		View parent = (View) v.getParent();
		switch ( parent.getId() ) {
			default:
				return false;
			case R.id.rowSCPWME0:
				displayOverridePopup(Globals.OVERRIDE_16CH_CHANNEL0, pwmeValues[0]);
				break;
			case R.id.rowSCPWME1:
				displayOverridePopup(Globals.OVERRIDE_16CH_CHANNEL1, pwmeValues[1]);
				break;
			case R.id.rowSCPWME2:
				displayOverridePopup(Globals.OVERRIDE_16CH_CHANNEL2, pwmeValues[2]);
				break;
			case R.id.rowSCPWME3:
				displayOverridePopup(Globals.OVERRIDE_16CH_CHANNEL3, pwmeValues[3]);
				break;
			case R.id.rowSCPWME4:
				displayOverridePopup(Globals.OVERRIDE_16CH_CHANNEL4, pwmeValues[4]);
				break;
			case R.id.rowSCPWME5:
				displayOverridePopup(Globals.OVERRIDE_16CH_CHANNEL5, pwmeValues[5]);
				break;
			case R.id.rowSCPWME6:
				displayOverridePopup(Globals.OVERRIDE_16CH_CHANNEL6, pwmeValues[6]);
				break;
			case R.id.rowSCPWME7:
				displayOverridePopup(Globals.OVERRIDE_16CH_CHANNEL7, pwmeValues[7]);
				break;
			case R.id.rowSCPWME8:
				displayOverridePopup(Globals.OVERRIDE_16CH_CHANNEL8, pwmeValues[8]);
				break;
			case R.id.rowSCPWME9:
				displayOverridePopup(Globals.OVERRIDE_16CH_CHANNEL9, pwmeValues[9]);
				break;
			case R.id.rowSCPWME10:
				displayOverridePopup(Globals.OVERRIDE_16CH_CHANNEL10, pwmeValues[10]);
				break;
			case R.id.rowSCPWME11:
				displayOverridePopup(Globals.OVERRIDE_16CH_CHANNEL11, pwmeValues[11]);
				break;
			case R.id.rowSCPWME12:
				displayOverridePopup(Globals.OVERRIDE_16CH_CHANNEL12, pwmeValues[12]);
				break;
			case R.id.rowSCPWME13:
				displayOverridePopup(Globals.OVERRIDE_16CH_CHANNEL13, pwmeValues[13]);
				break;
			case R.id.rowSCPWME14:
				displayOverridePopup(Globals.OVERRIDE_16CH_CHANNEL14, pwmeValues[14]);
				break;
			case R.id.rowSCPWME15:
				displayOverridePopup(Globals.OVERRIDE_16CH_CHANNEL15, pwmeValues[15]);
				break;
		}
		return true;
	}
}
