package info.curtbinder.reefangel.phone.pages;

/*
 * Copyright (c) 2011-13 by Curt Binder (http://curtbinder.info)
 * 
 * This work is made available under the terms of the Creative Commons
 * Attribution-NonCommercial-ShareAlike 3.0 Unported License
 * http://creativecommons.org/licenses/by-nc-sa/3.0/
 */

import info.curtbinder.reefangel.controller.Controller;
import info.curtbinder.reefangel.phone.Permissions;
import info.curtbinder.reefangel.phone.R;
import info.curtbinder.reefangel.phone.VortechPopupActivity;
import info.curtbinder.reefangel.service.MessageCommands;
import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.widget.ScrollView;
import android.widget.TextView;

public class VortechPage extends ScrollView implements OnLongClickListener {
	private static final String TAG = VortechPage.class.getSimpleName();

	Context ctx; // saved context from parent
	private TextView[] vortechText;
	private TextView[] vortechLabels;

	public VortechPage ( Context context ) {
		super( context );
		addViewsFromLayout( context );
		ctx = context;
	}

	public VortechPage ( Context context, AttributeSet attrs ) {
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
		vortechText[0].setOnLongClickListener( this );
		vortechText[0].setLongClickable( true );
		vortechText[1].setOnLongClickListener( this );
		vortechText[1].setLongClickable( true );
		vortechText[2].setOnLongClickListener( this );
		vortechText[2].setLongClickable( true );

		vortechLabels[0] = (TextView) findViewById( R.id.vortech_mode_label );
		vortechLabels[1] = (TextView) findViewById( R.id.vortech_speed_label );
		vortechLabels[2] =
				(TextView) findViewById( R.id.vortech_duration_label );
	}

	public void setLabel ( int channel, String label ) {
		vortechLabels[channel].setText( label );
	}

	public void updateDisplay ( String[] v ) {
		for ( int i = 0; i < Controller.MAX_VORTECH_VALUES; i++ ) {
			vortechText[i].setText( v[i] );
		}
	}

	@Override
	public boolean onLongClick ( View v ) {
		// Display popup that allows for memory setting
		switch ( v.getId() ) {
			case R.id.vortechMode:
				// display mode choices
				Log.d( TAG, "Vortech Mode change" );
				displayPopup( VortechPopupActivity.MODE );
				break;
			case R.id.vortechSpeed:
				// display speed choices
				// Speed 0-100
				Log.d( TAG, "Vortech Speed change" );
				displayPopup( VortechPopupActivity.SPEED );
				break;
			case R.id.vortechDuration:
				// display duration choices
				// Duration 0-255
				Log.d( TAG, "Vortech Duration change" );
				displayPopup( VortechPopupActivity.DURATION );
				break;
			default:
				return false;
		}
		return true;
	}

	private void displayPopup ( int type ) {
		Intent i = new Intent( MessageCommands.VORTECH_UPDATE_INTENT );
		i.putExtra( MessageCommands.VORTECH_UPDATE_TYPE, type );
		ctx.sendBroadcast( i, Permissions.SEND_COMMAND );
	}
}
