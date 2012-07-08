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

public class IOWidget extends ScrollView {
	private static final String TAG = IOWidget.class.getSimpleName();

	Context ctx; // saved context from parent
	private TextView[] ioText;
	private TextView[] ioLabels;

	public IOWidget ( Context context ) {
		super( context );
		addViewsFromLayout( context );
		ctx = context;
	}

	public IOWidget ( Context context, AttributeSet attrs ) {
		super( context, attrs );
		addViewsFromLayout( context );
		ctx = context;
	}

	private void addViewsFromLayout ( Context context ) {
		LayoutInflater layoutInflater =
				(LayoutInflater) context
						.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
		layoutInflater.inflate( R.layout.io, this );
		ioText = new TextView[Controller.MAX_IO_CHANNELS];
		ioLabels = new TextView[Controller.MAX_IO_CHANNELS];
		for ( int i = 0; i < Controller.MAX_IO_CHANNELS; i++ ) {
			ioText[i] = new TextView( context );
			ioLabels[i] = new TextView( context );
		}
		findViews();
	}

	private void findViews ( ) {
		ioText[0] = (TextView) findViewById( R.id.io0 );
		ioText[1] = (TextView) findViewById( R.id.io1 );
		ioText[2] = (TextView) findViewById( R.id.io2 );
		ioText[3] = (TextView) findViewById( R.id.io3 );
		ioText[4] = (TextView) findViewById( R.id.io4 );
		ioText[5] = (TextView) findViewById( R.id.io5 );
		ioText[6] = (TextView) findViewById( R.id.io6 );

		ioLabels[0] = (TextView) findViewById( R.id.io0_label );
		ioLabels[1] = (TextView) findViewById( R.id.io1_label );
		ioLabels[2] = (TextView) findViewById( R.id.io2_label );
		ioLabels[3] = (TextView) findViewById( R.id.io3_label );
		ioLabels[4] = (TextView) findViewById( R.id.io4_label );
		ioLabels[5] = (TextView) findViewById( R.id.io5_label );
		ioLabels[6] = (TextView) findViewById( R.id.io6_label );
	}

	public void setLabel ( int channel, String label ) {
		ioLabels[channel].setText( label );
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
		ioText[channel].setVisibility( v );
		ioLabels[channel].setVisibility( v );
	}

	public void updateDisplay ( String[] v ) {
		for ( int i = 0; i < Controller.MAX_IO_CHANNELS; i++ ) {
			ioText[i].setText( v[i] );
		}
	}

}
