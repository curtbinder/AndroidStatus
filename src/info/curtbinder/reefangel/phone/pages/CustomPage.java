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

public class CustomPage extends ScrollView {
	private static final String TAG = CustomPage.class.getSimpleName();

	Context ctx; // saved context from parent
	private TextView[] customText =
			new TextView[Controller.MAX_CUSTOM_VARIABLES];
	private TableRow[] customRow =
			new TableRow[Controller.MAX_CUSTOM_VARIABLES];

	public CustomPage ( Context context ) {
		super( context );
		ctx = context;
		addViewsFromLayout( context );
	}

	public CustomPage ( Context context, AttributeSet attrs ) {
		super( context, attrs );
		ctx = context;
		addViewsFromLayout( context );
	}

	private void addViewsFromLayout ( Context context ) {
		LayoutInflater layoutInflater =
				(LayoutInflater) context
						.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
		layoutInflater.inflate( R.layout.custom, this );
		findViews();
	}

	private void findViews ( ) {
		customRow[0] = (TableRow) findViewById( R.id.rowCustom0 );
		customText[0] = (TextView) customRow[0].findViewById( R.id.rowValue );
		customRow[1] = (TableRow) findViewById( R.id.rowCustom1 );
		customText[1] = (TextView) customRow[1].findViewById( R.id.rowValue );
		customRow[2] = (TableRow) findViewById( R.id.rowCustom2 );
		customText[2] = (TextView) customRow[2].findViewById( R.id.rowValue );
		customRow[3] = (TableRow) findViewById( R.id.rowCustom3 );
		customText[3] = (TextView) customRow[3].findViewById( R.id.rowValue );
		customRow[4] = (TableRow) findViewById( R.id.rowCustom4 );
		customText[4] = (TextView) customRow[4].findViewById( R.id.rowValue );
		customRow[5] = (TableRow) findViewById( R.id.rowCustom5 );
		customText[5] = (TextView) customRow[5].findViewById( R.id.rowValue );
		customRow[6] = (TableRow) findViewById( R.id.rowCustom6 );
		customText[6] = (TextView) customRow[6].findViewById( R.id.rowValue );
		customRow[7] = (TableRow) findViewById( R.id.rowCustom7 );
		customText[7] = (TextView) customRow[7].findViewById( R.id.rowValue );
	}

	public void setLabel ( int channel, String label ) {
		((TextView) customRow[channel].findViewById( R.id.rowTitle ))
				.setText( label );
		String s =
				new String( String.format( "%s %d", ctx.getResources()
						.getString( R.string.labelCustom ), channel ) );
		((TextView) customRow[channel].findViewById( R.id.rowSubTitle ))
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
		customRow[channel].setVisibility( v );
	}

	public void updateDisplay ( String[] v ) {
		for ( int i = 0; i < Controller.MAX_CUSTOM_VARIABLES; i++ ) {
			customText[i].setText( v[i] );
		}
	}

}
