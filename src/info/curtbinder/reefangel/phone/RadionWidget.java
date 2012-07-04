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

public class RadionWidget extends ScrollView {
	//private static final String TAG = RadionWidget.class.getSimpleName();

	Context ctx; // saved context from parent
	private TextView[] radionText;
	private TextView[] radionLabels;

	public RadionWidget ( Context context ) {
		super( context );
		addViewsFromLayout( context );
		ctx = context;
	}

	public RadionWidget ( Context context, AttributeSet attrs ) {
		super( context, attrs );
		addViewsFromLayout( context );
		ctx = context;
	}

	private void addViewsFromLayout ( Context context ) {
		LayoutInflater layoutInflater =
				(LayoutInflater) context
						.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
		layoutInflater.inflate( R.layout.radion, this );
		radionText = new TextView[Controller.MAX_RADION_LIGHT_CHANNELS];
		radionLabels = new TextView[Controller.MAX_RADION_LIGHT_CHANNELS];
		for ( int i = 0; i < Controller.MAX_RADION_LIGHT_CHANNELS; i++ ) {
			radionText[i] = new TextView( context );
			radionLabels[i] = new TextView( context );
		}
		findViews();
	}

	private void findViews ( ) {
		radionText[0] = (TextView) findViewById( R.id.radionWhite );
		radionText[1] = (TextView) findViewById( R.id.radionRoyalBlue );
		radionText[2] = (TextView) findViewById( R.id.radionRed );
		radionText[3] = (TextView) findViewById( R.id.radionGreen );
		radionText[4] = (TextView) findViewById( R.id.radionBlue );
		radionText[5] = (TextView) findViewById( R.id.radionIntensity );

		radionLabels[0] = (TextView) findViewById( R.id.radion_white_label );
		radionLabels[1] = (TextView) findViewById( R.id.radion_royalblue_label );
		radionLabels[2] = (TextView) findViewById( R.id.radion_red_label );
		radionLabels[3] = (TextView) findViewById( R.id.radion_green_label );
		radionLabels[4] = (TextView) findViewById( R.id.radion_blue_label );
		radionLabels[5] = (TextView) findViewById( R.id.radion_intensity_label );
	}

	public void setLabel ( int channel, String label ) {
		radionLabels[channel].setText( label );
	}
	
	public void updateDisplay ( String[] v ) {
		for ( int i = 0; i < Controller.MAX_RADION_LIGHT_CHANNELS; i++ ) {
			radionText[i].setText( v[i] );
		}
	}

}
