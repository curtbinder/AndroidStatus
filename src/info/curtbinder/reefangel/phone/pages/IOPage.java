/*
 * Copyright (c) 2011-13 by Curt Binder (http://curtbinder.info)
 * 
 * This work is made available under the terms of the Creative Commons
 * Attribution-NonCommercial-ShareAlike 3.0 Unported License
 * http://creativecommons.org/licenses/by-nc-sa/3.0/
 */

package info.curtbinder.reefangel.phone.pages;

import info.curtbinder.reefangel.controller.Controller;
import info.curtbinder.reefangel.phone.R;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TableRow;
import android.widget.TextView;

public class IOPage extends RAPage {
	private static final String TAG = IOPage.class.getSimpleName();

	Context ctx; // saved context from parent
	private TextView[] ioText = new TextView[Controller.MAX_IO_CHANNELS];
	private TableRow[] ioRow = new TableRow[Controller.MAX_IO_CHANNELS];

	public IOPage ( Context context ) {
		super( context );
		ctx = context;
		addViewsFromLayout( context );
	}

	public IOPage ( Context context, AttributeSet attrs ) {
		super( context, attrs );
		ctx = context;
		addViewsFromLayout( context );
	}

	private void addViewsFromLayout ( Context context ) {
		LayoutInflater layoutInflater =
				(LayoutInflater) context
						.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
		layoutInflater.inflate( R.layout.io, this );
		findViews();
	}

	private void findViews ( ) {
		ioRow[0] = (TableRow) findViewById( R.id.rowIO0 );
		ioText[0] = (TextView) ioRow[0].findViewById( R.id.rowValue );
		ioRow[1] = (TableRow) findViewById( R.id.rowIO1 );
		ioText[1] = (TextView) ioRow[1].findViewById( R.id.rowValue );
		ioRow[2] = (TableRow) findViewById( R.id.rowIO2 );
		ioText[2] = (TextView) ioRow[2].findViewById( R.id.rowValue );
		ioRow[3] = (TableRow) findViewById( R.id.rowIO3 );
		ioText[3] = (TextView) ioRow[3].findViewById( R.id.rowValue );
		ioRow[4] = (TableRow) findViewById( R.id.rowIO4 );
		ioText[4] = (TextView) ioRow[4].findViewById( R.id.rowValue );
		ioRow[5] = (TableRow) findViewById( R.id.rowIO5 );
		ioText[5] = (TextView) ioRow[5].findViewById( R.id.rowValue );
	}

	public void setLabel ( int channel, String label ) {
		((TextView) ioRow[channel].findViewById( R.id.rowTitle ))
				.setText( label );
		String s =
				new String( String.format( "%s %d", ctx.getResources()
						.getString( R.string.labelIO ), channel ) );
		((TextView) ioRow[channel].findViewById( R.id.rowSubTitle ))
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
		ioRow[channel].setVisibility( v );
	}

	public void updateDisplay ( String[] v ) {
		for ( int i = 0; i < Controller.MAX_IO_CHANNELS; i++ ) {
			ioText[i].setText( v[i] );
		}
	}

	@Override
	public String getPageTitle ( ) {
		return ctx.getResources().getString( R.string.labelIO );
	}

}
