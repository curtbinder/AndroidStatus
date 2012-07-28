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

public class AIPage extends ScrollView {
	// private static final String TAG = AIWidget.class.getSimpleName();

	Context ctx; // saved context from parent
	private TextView[] aiText;
	private TextView[] aiLabels;

	public AIPage ( Context context ) {
		super( context );
		addViewsFromLayout( context );
		ctx = context;
	}

	public AIPage ( Context context, AttributeSet attrs ) {
		super( context, attrs );
		addViewsFromLayout( context );
		ctx = context;
	}

	private void addViewsFromLayout ( Context context ) {
		LayoutInflater layoutInflater =
				(LayoutInflater) context
						.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
		layoutInflater.inflate( R.layout.ai, this );
		aiText = new TextView[Controller.MAX_AI_CHANNELS];
		aiLabels = new TextView[Controller.MAX_AI_CHANNELS];
		for ( int i = 0; i < Controller.MAX_AI_CHANNELS; i++ ) {
			aiText[i] = new TextView( context );
			aiLabels[i] = new TextView( context );
		}
		findViews();
	}

	private void findViews ( ) {
		aiText[0] = (TextView) findViewById( R.id.aiWhite );
		aiText[1] = (TextView) findViewById( R.id.aiBlue );
		aiText[2] = (TextView) findViewById( R.id.aiRoyalBlue );

		aiLabels[0] = (TextView) findViewById( R.id.ai_white_label );
		aiLabels[1] = (TextView) findViewById( R.id.ai_blue_label );
		aiLabels[2] = (TextView) findViewById( R.id.ai_royalblue_label );
	}

	public void setLabel ( int channel, String label ) {
		aiLabels[channel].setText( label );
	}

	public void updateDisplay ( String[] v ) {
		for ( int i = 0; i < Controller.MAX_AI_CHANNELS; i++ ) {
			aiText[i].setText( v[i] );
		}
	}

}
