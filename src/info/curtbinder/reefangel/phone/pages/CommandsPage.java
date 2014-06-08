/*
 * Copyright (c) 2011-2013 by Curt Binder (http://curtbinder.info)
 * 
 * This work is made available under the terms of the Creative Commons
 * Attribution-NonCommercial-ShareAlike 3.0 Unported License
 * http://creativecommons.org/licenses/by-nc-sa/3.0/
 */

package info.curtbinder.reefangel.phone.pages;

import info.curtbinder.reefangel.phone.Globals;
import info.curtbinder.reefangel.phone.R;
import info.curtbinder.reefangel.service.MessageCommands;
import info.curtbinder.reefangel.service.RequestCommands;
import info.curtbinder.reefangel.service.UpdateService;
import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class CommandsPage extends RAPage implements OnClickListener {
	// private static final String TAG = CommandsPage.class.getSimpleName();

	Context ctx; // saved context from parent

	public CommandsPage ( Context context ) {
		super( context );
		addViewsFromLayout( context );
		ctx = context;
	}

	public CommandsPage ( Context context, AttributeSet attrs ) {
		super( context, attrs );
		addViewsFromLayout( context );
		ctx = context;
	}

	private void addViewsFromLayout ( Context context ) {
		LayoutInflater layoutInflater =
				(LayoutInflater) context
						.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
		layoutInflater.inflate( R.layout.commandspage, this );
		findViews();
	}

	private void findViews ( ) {
		Button b = (Button) findViewById( R.id.command_button_feed );
		b.setOnClickListener( this );
		b = (Button) findViewById( R.id.command_button_water );
		b.setOnClickListener( this );
		b = (Button) findViewById( R.id.command_button_exit );
		b.setOnClickListener( this );
		b = (Button) findViewById( R.id.command_button_reboot );
		b.setOnClickListener( this );
		b = (Button) findViewById( R.id.command_button_lights_on );
		b.setOnClickListener( this );
		b = (Button) findViewById( R.id.command_button_lights_off );
		b.setOnClickListener( this );
		b = (Button) findViewById( R.id.command_button_ato_clear );
		b.setOnClickListener( this );
		b = (Button) findViewById( R.id.command_button_overheat_clear );
		b.setOnClickListener( this );
		b = (Button) findViewById( R.id.command_button_calibrate_ph );
		b.setOnClickListener( this );
		b = (Button) findViewById( R.id.command_button_calibrate_salinity );
		b.setOnClickListener( this );
		b = (Button) findViewById( R.id.command_button_calibrate_water );
		b.setOnClickListener( this );
		b = (Button) findViewById( R.id.command_button_calibrate_orp );
		b.setOnClickListener( this );
		b = (Button) findViewById( R.id.command_button_calibrate_phe );
		b.setOnClickListener( this );
		b = (Button) findViewById( R.id.command_button_version );
		b.setOnClickListener( this );
	}

	@Override
	public void onClick ( View v ) {
		Intent i = new Intent( ctx, UpdateService.class );
		String s = RequestCommands.ExitMode;
		String action = MessageCommands.COMMAND_SEND_INTENT;
		String command = MessageCommands.COMMAND_SEND_STRING;
		int location = -1;
		switch ( v.getId() ) {
			case R.id.command_button_feed:
				s = RequestCommands.FeedingMode;
				break;
			case R.id.command_button_water:
				s = RequestCommands.WaterMode;
				break;
			case R.id.command_button_lights_on:
				s = RequestCommands.LightsOn;
				break;
			case R.id.command_button_lights_off:
				s = RequestCommands.LightsOff;
				break;
			case R.id.command_button_ato_clear:
				s = RequestCommands.AtoClear;
				break;
			case R.id.command_button_overheat_clear:
				s = RequestCommands.OverheatClear;
				break;
			case R.id.command_button_reboot:
				s = RequestCommands.Reboot;
				break;
			case R.id.command_button_calibrate_ph:
				location = Globals.CALIBRATE_PH;
				break;
			case R.id.command_button_calibrate_phe:
				location = Globals.CALIBRATE_PHE;
				break;
			case R.id.command_button_calibrate_orp:
				location = Globals.CALIBRATE_ORP;
				break;
			case R.id.command_button_calibrate_salinity:
				location = Globals.CALIBRATE_SALINITY;
				break;
			case R.id.command_button_calibrate_water:
				location = Globals.CALIBRATE_WATERLEVEL;
				break;
			case R.id.command_button_version:
				action = MessageCommands.VERSION_QUERY_INTENT;
				s = RequestCommands.Version;
		}
		if ( location > -1 ) {
			// location is greater than -1, which means we have a calibration command
			// command & s are ignored by the service since the command is the same
			// for all of the calibration modes
			i.putExtra(MessageCommands.CALIBRATE_SEND_LOCATION_INT, location);
			action = MessageCommands.CALIBRATE_SEND_INTENT;
		}
		i.setAction( action );
		i.putExtra( command, s );
		ctx.startService( i );
	}

	@Override
	public String getPageTitle ( ) {
		return ctx.getString( R.string.titleCommands );
	}

}
