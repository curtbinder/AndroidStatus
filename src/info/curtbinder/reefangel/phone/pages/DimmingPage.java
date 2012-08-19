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
import android.widget.TextView;

public class DimmingPage extends ScrollView {
	private static final String TAG = DimmingPage.class.getSimpleName();

	Context ctx; // saved context from parent
	private TextView[] pwmeText;
	private TextView[] pwmeLabels;

	public DimmingPage ( Context context ) {
		super( context );
		addViewsFromLayout( context );
		ctx = context;
	}

	public DimmingPage ( Context context, AttributeSet attrs ) {
		super( context, attrs );
		addViewsFromLayout( context );
		ctx = context;
	}

	private void addViewsFromLayout ( Context context ) {
		LayoutInflater layoutInflater =
				(LayoutInflater) context
						.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
		layoutInflater.inflate( R.layout.dimming, this );
		pwmeText = new TextView[Controller.MAX_PWM_EXPANSION_PORTS];
		pwmeLabels = new TextView[Controller.MAX_PWM_EXPANSION_PORTS];
		for ( int i = 0; i < Controller.MAX_PWM_EXPANSION_PORTS; i++ ) {
			pwmeText[i] = new TextView( context );
			pwmeLabels[i] = new TextView( context );
		}
		findViews();
	}

	private void findViews ( ) {
		pwmeText[0] = (TextView) findViewById( R.id.pwme0 );
		pwmeText[1] = (TextView) findViewById( R.id.pwme1 );
		pwmeText[2] = (TextView) findViewById( R.id.pwme2 );
		pwmeText[3] = (TextView) findViewById( R.id.pwme3 );
		pwmeText[4] = (TextView) findViewById( R.id.pwme4 );
		pwmeText[5] = (TextView) findViewById( R.id.pwme5 );

		pwmeLabels[0] = (TextView) findViewById( R.id.pwme0_label );
		pwmeLabels[1] = (TextView) findViewById( R.id.pwme1_label );
		pwmeLabels[2] = (TextView) findViewById( R.id.pwme2_label );
		pwmeLabels[3] = (TextView) findViewById( R.id.pwme3_label );
		pwmeLabels[4] = (TextView) findViewById( R.id.pwme4_label );
		pwmeLabels[5] = (TextView) findViewById( R.id.pwme5_label );
	}

	public void setLabel ( int channel, String label ) {
		pwmeLabels[channel].setText( label );
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
		pwmeText[channel].setVisibility( v );
		pwmeLabels[channel].setVisibility( v );
	}

	public void updateDisplay ( String[] v ) {
		for ( int i = 0; i < Controller.MAX_PWM_EXPANSION_PORTS; i++ ) {
			pwmeText[i].setText( v[i] );
		}
	}

}
