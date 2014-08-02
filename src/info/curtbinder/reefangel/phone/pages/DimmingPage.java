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

public class DimmingPage extends RAPage
	implements OnLongClickListener {
	private static final String TAG = DimmingPage.class.getSimpleName();

	Context ctx; // saved context from parent
	private TextView[] pwmeText =
			new TextView[Controller.MAX_PWM_EXPANSION_PORTS];
	private TableRow[] pwmeRow =
			new TableRow[Controller.MAX_PWM_EXPANSION_PORTS];
	private short[] pwmeValues = new short[Controller.MAX_PWM_EXPANSION_PORTS];

	public DimmingPage ( Context context ) {
		super( context );
		ctx = context;
		addViewsFromLayout( context );
	}

	public DimmingPage ( Context context, AttributeSet attrs ) {
		super( context, attrs );
		ctx = context;
		addViewsFromLayout( context );
	}

	private void addViewsFromLayout ( Context context ) {
		LayoutInflater layoutInflater =
				(LayoutInflater) context
						.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
		layoutInflater.inflate( R.layout.dimming, this );
		findViews();
		
	}

	private void findViews ( ) {
		pwmeRow[0] = (TableRow) findViewById( R.id.rowPWME0 );
		pwmeText[0] = (TextView) pwmeRow[0].findViewById( R.id.rowValue );
		pwmeRow[1] = (TableRow) findViewById( R.id.rowPWME1 );
		pwmeText[1] = (TextView) pwmeRow[1].findViewById( R.id.rowValue );
		pwmeRow[2] = (TableRow) findViewById( R.id.rowPWME2 );
		pwmeText[2] = (TextView) pwmeRow[2].findViewById( R.id.rowValue );
		pwmeRow[3] = (TableRow) findViewById( R.id.rowPWME3 );
		pwmeText[3] = (TextView) pwmeRow[3].findViewById( R.id.rowValue );
		pwmeRow[4] = (TableRow) findViewById( R.id.rowPWME4 );
		pwmeText[4] = (TextView) pwmeRow[4].findViewById( R.id.rowValue );
		pwmeRow[5] = (TableRow) findViewById( R.id.rowPWME5 );
		pwmeText[5] = (TextView) pwmeRow[5].findViewById( R.id.rowValue );
		for ( int i = 0; i < Controller.MAX_PWM_EXPANSION_PORTS; i++ ) {
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
		for ( int i = 0; i < Controller.MAX_PWM_EXPANSION_PORTS; i++ ) {
			pwmeText[i].setText( v[i] );
		}
	}
	
	public void updatePWMValues ( short[] v ) {
		for ( int i = 0; i < Controller.MAX_PWM_EXPANSION_PORTS; i++ ) {
			pwmeValues[i] = v[i];
		}
	}

	@Override
	public String getPageTitle ( ) {
		return ctx.getString( R.string.labelDimming );
	}

	@Override
	public boolean onLongClick ( View v ) {
		View parent = (View) v.getParent();
		switch ( parent.getId() ) {
			default:
				return false;
			case R.id.rowPWME0:
				displayOverridePopup(Globals.OVERRIDE_CHANNEL0, pwmeValues[0]);
				break;
			case R.id.rowPWME1:
				displayOverridePopup(Globals.OVERRIDE_CHANNEL1, pwmeValues[1]);
				break;
			case R.id.rowPWME2:
				displayOverridePopup(Globals.OVERRIDE_CHANNEL2, pwmeValues[2]);
				break;
			case R.id.rowPWME3:
				displayOverridePopup(Globals.OVERRIDE_CHANNEL3, pwmeValues[3]);
				break;
			case R.id.rowPWME4:
				displayOverridePopup(Globals.OVERRIDE_CHANNEL4, pwmeValues[4]);
				break;
			case R.id.rowPWME5:
				displayOverridePopup(Globals.OVERRIDE_CHANNEL5, pwmeValues[5]);
				break;
		}
		return true;
	}
}
