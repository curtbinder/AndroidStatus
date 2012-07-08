package info.curtbinder.reefangel.phone;

/*
 * Copyright (c) 2011-12 by Curt Binder (http://curtbinder.info)
 * 
 * This work is made available under the terms of the Creative Commons
 * Attribution-NonCommercial-ShareAlike 3.0 Unported License
 * http://creativecommons.org/licenses/by-nc-sa/3.0/
 */

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;

public class CustomWidget extends ScrollView {
	private static final String TAG = CustomWidget.class.getSimpleName();

	Context ctx; // saved context from parent
	private TextView[] customText;
	private TextView[] customLabels;

	public CustomWidget ( Context context ) {
		super( context );
		addViewsFromLayout( context );
		ctx = context;
	}

	public CustomWidget ( Context context, AttributeSet attrs ) {
		super( context, attrs );
		addViewsFromLayout( context );
		ctx = context;
	}

	private void addViewsFromLayout ( Context context ) {
		LayoutInflater layoutInflater =
				(LayoutInflater) context
						.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
		layoutInflater.inflate( R.layout.custom, this );
		customText = new TextView[Controller.MAX_CUSTOM_VARIABLES];
		customLabels = new TextView[Controller.MAX_CUSTOM_VARIABLES];
		for ( int i = 0; i < Controller.MAX_CUSTOM_VARIABLES; i++ ) {
			customText[i] = new TextView( context );
			customLabels[i] = new TextView( context );
		}
		findViews();
	}

	private void findViews ( ) {
		customText[0] = (TextView) findViewById( R.id.custom0 );
		customText[1] = (TextView) findViewById( R.id.custom1 );
		customText[2] = (TextView) findViewById( R.id.custom2 );
		customText[3] = (TextView) findViewById( R.id.custom3 );
		customText[4] = (TextView) findViewById( R.id.custom4 );
		customText[5] = (TextView) findViewById( R.id.custom5 );
		customText[6] = (TextView) findViewById( R.id.custom6 );
		customText[7] = (TextView) findViewById( R.id.custom7 );

		customLabels[0] = (TextView) findViewById( R.id.custom0_label );
		customLabels[1] = (TextView) findViewById( R.id.custom1_label );
		customLabels[2] = (TextView) findViewById( R.id.custom2_label );
		customLabels[3] = (TextView) findViewById( R.id.custom3_label );
		customLabels[4] = (TextView) findViewById( R.id.custom4_label );
		customLabels[5] = (TextView) findViewById( R.id.custom5_label );
		customLabels[6] = (TextView) findViewById( R.id.custom6_label );
		customLabels[7] = (TextView) findViewById( R.id.custom7_label );
	}

	public void setLabel ( int channel, String label ) {
		customLabels[channel].setText( label );
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
		customText[channel].setVisibility( v );
		customLabels[channel].setVisibility( v );
	}

	public void updateDisplay ( String[] v ) {
		for ( int i = 0; i < Controller.MAX_CUSTOM_VARIABLES; i++ ) {
			customText[i].setText( v[i] );
		}
	}

}
