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
import android.view.LayoutInflater;
import android.widget.ScrollView;
import android.widget.TextView;

public class VortechWidget extends ScrollView {
	//private static final String TAG = VortechWidget.class.getSimpleName();

	Context ctx; // saved context from parent
	private TextView[] vortechText;
	private TextView[] vortechLabels;

	public VortechWidget ( Context context ) {
		super( context );
		addViewsFromLayout( context );
		ctx = context;
	}

	public VortechWidget ( Context context, AttributeSet attrs ) {
		super( context, attrs );
		addViewsFromLayout( context );
		ctx = context;
	}

	private void addViewsFromLayout ( Context context ) {
		LayoutInflater layoutInflater =
				(LayoutInflater) context
						.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
		layoutInflater.inflate( R.layout.vortech, this );
		vortechText = new TextView[Controller.MAX_VORTECH_VALUES];
		vortechLabels = new TextView[Controller.MAX_VORTECH_VALUES];
		for ( int i = 0; i < Controller.MAX_VORTECH_VALUES; i++ ) {
			vortechText[i] = new TextView( context );
			vortechLabels[i] = new TextView( context );
		}
		findViews();
	}

	private void findViews ( ) {
		vortechText[0] = (TextView) findViewById( R.id.vortechMode );
		vortechText[1] = (TextView) findViewById( R.id.vortechSpeed );
		vortechText[2] = (TextView) findViewById( R.id.vortechDuration );

		vortechLabels[0] = (TextView) findViewById( R.id.vortech_mode_label );
		vortechLabels[1] = (TextView) findViewById( R.id.vortech_speed_label );
		vortechLabels[2] = (TextView) findViewById( R.id.vortech_duration_label );
	}

	public void setLabel ( int channel, String label ) {
		vortechLabels[channel].setText( label );
	}
	
	public void updateDisplay ( String[] v ) {
		for ( int i = 0; i < Controller.MAX_VORTECH_VALUES; i++ ) {
			vortechText[i].setText( v[i] );
		}
	}

}
