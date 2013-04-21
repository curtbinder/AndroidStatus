package info.curtbinder.reefangel.phone.pages;

/*
 * Copyright (c) 2011-13 by Curt Binder (http://curtbinder.info)
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
import android.widget.TableRow;
import android.widget.TextView;

public class DimmingPage extends ScrollView {
	private static final String TAG = DimmingPage.class.getSimpleName();

	Context ctx; // saved context from parent
	private TextView[] pwmeText =
			new TextView[Controller.MAX_PWM_EXPANSION_PORTS];
	private TableRow[] pwmeRow =
			new TableRow[Controller.MAX_PWM_EXPANSION_PORTS];

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

	}

	public void setLabel ( int channel, String label ) {
		((TextView) pwmeRow[channel].findViewById( R.id.rowTitle ))
				.setText( label );
		String s =
				new String( String.format( "%s %d", ctx.getResources()
						.getString( R.string.labelChannel ), channel ) );
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

}
