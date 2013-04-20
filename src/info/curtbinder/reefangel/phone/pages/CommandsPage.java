package info.curtbinder.reefangel.phone.pages;

/*
 * Copyright (c) 2011-13 by Curt Binder (http://curtbinder.info)
 * 
 * This work is made available under the terms of the Creative Commons
 * Attribution-NonCommercial-ShareAlike 3.0 Unported License
 * http://creativecommons.org/licenses/by-nc-sa/3.0/
 */

import info.curtbinder.reefangel.phone.Permissions;
import info.curtbinder.reefangel.phone.R;
import info.curtbinder.reefangel.service.MessageCommands;
import info.curtbinder.reefangel.service.RequestCommands;
import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ScrollView;

public class CommandsPage extends ScrollView implements OnClickListener {
	//private static final String TAG = CommandsPage.class.getSimpleName();

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
		Button b = (Button)findViewById(R.id.command_button_feed);
		b.setOnClickListener( this );
		b = (Button)findViewById(R.id.command_button_water);
		b.setOnClickListener( this );
		b = (Button)findViewById(R.id.command_button_exit);
		b.setOnClickListener( this );
		b = (Button)findViewById(R.id.command_button_ato_clear);
		b.setOnClickListener( this );
		b = (Button)findViewById(R.id.command_button_overheat_clear);
		b.setOnClickListener( this );
	}

	@Override
	public void onClick ( View v ) {
		Intent i = new Intent();
		switch ( v.getId() ) {
			case R.id.command_button_feed:
				i.setAction( MessageCommands.COMMAND_SEND_INTENT );
				i.putExtra( MessageCommands.COMMAND_SEND_STRING,
							RequestCommands.FeedingMode );
				break;
			case R.id.command_button_water:
				i.setAction( MessageCommands.COMMAND_SEND_INTENT );
				i.putExtra( MessageCommands.COMMAND_SEND_STRING,
							RequestCommands.WaterMode );
				break;
			case R.id.command_button_ato_clear:
				i.setAction( MessageCommands.COMMAND_SEND_INTENT );
				i.putExtra( MessageCommands.COMMAND_SEND_STRING,
							RequestCommands.AtoClear );
				break;
			case R.id.command_button_overheat_clear:
				i.setAction( MessageCommands.COMMAND_SEND_INTENT );
				i.putExtra( MessageCommands.COMMAND_SEND_STRING,
							RequestCommands.OverheatClear );
				break;
			default:
			case R.id.command_button_exit:
				i.setAction( MessageCommands.COMMAND_SEND_INTENT );
				i.putExtra( MessageCommands.COMMAND_SEND_STRING,
							RequestCommands.ExitMode );
				break;
		}
		ctx.sendBroadcast( i, Permissions.SEND_COMMAND );
	}

}
