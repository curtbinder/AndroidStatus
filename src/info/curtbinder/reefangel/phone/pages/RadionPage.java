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
import android.view.LayoutInflater;
import android.widget.ScrollView;
import android.widget.TableRow;
import android.widget.TextView;

public class RadionPage extends ScrollView {
	// private static final String TAG = RadionWidget.class.getSimpleName();

	Context ctx; // saved context from parent
	private TextView[] radionText =
			new TextView[Controller.MAX_RADION_LIGHT_CHANNELS];

	public RadionPage ( Context context ) {
		super( context );
		ctx = context;
		addViewsFromLayout( context );
	}

	public RadionPage ( Context context, AttributeSet attrs ) {
		super( context, attrs );
		ctx = context;
		addViewsFromLayout( context );
	}

	private void addViewsFromLayout ( Context context ) {
		LayoutInflater layoutInflater =
				(LayoutInflater) context
						.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
		layoutInflater.inflate( R.layout.radion, this );
		findViews();
	}

	private void findViews ( ) {
		TableRow tr;
		tr = (TableRow) findViewById( R.id.rowWhite );
		radionText[0] = (TextView) tr.findViewById( R.id.rowValue );
		tr = (TableRow) findViewById( R.id.rowRoyalBlue );
		radionText[1] = (TextView) tr.findViewById( R.id.rowValue );
		radionText[1].setTextColor( ctx.getResources()
				.getColor( R.color.royalblue ) );
		((TextView) tr.findViewById( R.id.rowTitle )).setTextColor( ctx
				.getResources().getColor( R.color.royalblue ) );
		tr = (TableRow) findViewById( R.id.rowRed );
		radionText[2] = (TextView) tr.findViewById( R.id.rowValue );
		radionText[2].setTextColor( ctx.getResources().getColor( R.color.red ) );
		((TextView) tr.findViewById( R.id.rowTitle )).setTextColor( ctx
				.getResources().getColor( R.color.red ) );
		tr = (TableRow) findViewById( R.id.rowGreen );
		radionText[3] = (TextView) tr.findViewById( R.id.rowValue );
		radionText[3]
				.setTextColor( ctx.getResources().getColor( R.color.green ) );
		((TextView) tr.findViewById( R.id.rowTitle )).setTextColor( ctx
				.getResources().getColor( R.color.green ) );
		tr = (TableRow) findViewById( R.id.rowBlue );
		radionText[4] = (TextView) tr.findViewById( R.id.rowValue );
		radionText[4]
				.setTextColor( ctx.getResources().getColor( R.color.blue ) );
		((TextView) tr.findViewById( R.id.rowTitle )).setTextColor( ctx
				.getResources().getColor( R.color.blue ) );
		tr = (TableRow) findViewById( R.id.rowIntensity );
		radionText[5] = (TextView) tr.findViewById( R.id.rowValue );

	}

	public void setLabel ( int channel, String label ) {
		TableRow tr = (TableRow) radionText[channel].getParent();
		((TextView) tr.findViewById( R.id.rowTitle )).setText( label );
	}

	public void updateDisplay ( String[] v ) {
		for ( int i = 0; i < Controller.MAX_RADION_LIGHT_CHANNELS; i++ ) {
			radionText[i].setText( v[i] );
		}
	}

}
