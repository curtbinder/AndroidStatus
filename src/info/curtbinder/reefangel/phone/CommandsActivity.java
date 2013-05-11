/*
 * Copyright (c) 2011-2013 by Curt Binder (http://curtbinder.info)
 *
 * This work is made available under the terms of the 
 * Creative Commons Attribution-NonCommercial-ShareAlike 3.0 Unported License
 * http://creativecommons.org/licenses/by-nc-sa/3.0/
 */

package info.curtbinder.reefangel.phone;

import info.curtbinder.reefangel.service.MessageCommands;
import info.curtbinder.reefangel.service.RequestCommands;
import info.curtbinder.reefangel.service.UpdateService;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class CommandsActivity extends Activity implements OnClickListener {

	private TextView versionText;

	CommandsReceiver receiver;
	IntentFilter filter;

	protected void onCreate ( Bundle savedInstanceState ) {
		super.onCreate( savedInstanceState );
		setContentView( R.layout.commands );

		findViews();
		receiver = new CommandsReceiver();
		filter = new IntentFilter( MessageCommands.COMMAND_RESPONSE_INTENT );
		filter.addAction( MessageCommands.VERSION_RESPONSE_INTENT );
	}

	protected void onPause ( ) {
		super.onPause();
		unregisterReceiver( receiver );
	}

	protected void onResume ( ) {
		super.onResume();
		registerReceiver( receiver, filter, Permissions.SEND_COMMAND, null );
	}

	private void findViews ( ) {
		versionText = (TextView) findViewById( R.id.textInstalledVersion );
		Button b = (Button) findViewById( R.id.command_button_feed );
		b.setOnClickListener( this );
		b = (Button) findViewById( R.id.command_button_water );
		b.setOnClickListener( this );
		b = (Button) findViewById( R.id.command_button_exit );
		b.setOnClickListener( this );
		b = (Button) findViewById( R.id.command_button_lights_on );
		b.setOnClickListener( this );
		b = (Button) findViewById( R.id.command_button_lights_off );
		b.setOnClickListener( this );
		b = (Button) findViewById( R.id.command_button_ato_clear );
		b.setOnClickListener( this );
		b = (Button) findViewById( R.id.command_button_overheat_clear );
		b.setOnClickListener( this );
		b = (Button) findViewById( R.id.command_button_version );
		b.setOnClickListener( this );
	}

	@Override
	public void onClick ( View v ) {
		Intent i = new Intent( this, UpdateService.class );
		String s = RequestCommands.ExitMode;
		String action = MessageCommands.COMMAND_SEND_INTENT;
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
			case R.id.command_button_version:
				action = MessageCommands.VERSION_QUERY_INTENT;
				// Next line is not needed because this is handled by the service
				// added only for completeness
				s = RequestCommands.Version;
				break;
		}
		i.setAction( action );
		i.putExtra( MessageCommands.COMMAND_SEND_STRING, s );
		startService( i );
	}

	class CommandsReceiver extends BroadcastReceiver {

		public void onReceive ( Context context, Intent intent ) {
			if ( intent.getAction()
					.equals( MessageCommands.COMMAND_RESPONSE_INTENT ) ) {
				Toast.makeText( CommandsActivity.this,
								intent.getStringExtra( MessageCommands.COMMAND_RESPONSE_STRING ),
								Toast.LENGTH_LONG ).show();
			} else if ( intent.getAction()
					.equals( MessageCommands.VERSION_RESPONSE_INTENT ) ) {
				versionText
						.setText( intent
								.getStringExtra( MessageCommands.VERSION_RESPONSE_STRING ) );
			}
		}

	}
}
