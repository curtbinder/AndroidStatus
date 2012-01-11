package info.curtbinder.reefangel.phone;

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

public class CommandsActivity extends BaseActivity implements OnClickListener {

	//private static final String TAG = CommandsActivity.class.getSimpleName();

	private Button feedingButton;
	private Button waterButton;
	private Button exitButton;
	private Button versionButton;
	private TextView versionText;

	CommandsReceiver receiver;
	IntentFilter filter;

	@Override
	protected void onCreate ( Bundle savedInstanceState ) {
		super.onCreate( savedInstanceState );
		setContentView( R.layout.commands );

		findViews();
		receiver = new CommandsReceiver();
		filter = new IntentFilter( MessageCommands.COMMAND_RESPONSE_INTENT );
		filter.addAction( MessageCommands.VERSION_RESPONSE_INTENT );

		setOnClickListeners();
	}

	@Override
	protected void onPause ( ) {
		super.onPause();
		unregisterReceiver( receiver );
	}

	@Override
	protected void onResume ( ) {
		super.onResume();
		registerReceiver( receiver, filter, Permissions.SEND_COMMAND, null );
	}

	private void findViews ( ) {
		feedingButton = (Button) findViewById( R.id.command_button_feed );
		waterButton = (Button) findViewById( R.id.command_button_water );
		exitButton = (Button) findViewById( R.id.command_button_exit );
		versionButton = (Button) findViewById( R.id.command_button_version );
		versionText = (TextView) findViewById( R.id.textInstalledVersion );
	}

	private void setOnClickListeners ( ) {
		feedingButton.setOnClickListener( this );
		waterButton.setOnClickListener( this );
		exitButton.setOnClickListener( this );
		versionButton.setOnClickListener( this );
	}

	@Override
	public void onClick ( View v ) {
		Intent i = new Intent();
		switch ( v.getId() ) {
			case R.id.command_button_feed:
				i.setAction( MessageCommands.COMMAND_SEND_INTENT );
				i.putExtra( MessageCommands.COMMAND_SEND_STRING,
							Globals.requestFeedingMode );
				break;
			case R.id.command_button_water:
				i.setAction( MessageCommands.COMMAND_SEND_INTENT );
				i.putExtra( MessageCommands.COMMAND_SEND_STRING,
							Globals.requestWaterMode );
				break;
			case R.id.command_button_version:
				i.setAction( MessageCommands.VERSION_QUERY_INTENT );
				break;
			default:
			case R.id.command_button_exit:
				i.setAction( MessageCommands.COMMAND_SEND_INTENT );
				i.putExtra( MessageCommands.COMMAND_SEND_STRING,
							Globals.requestExitMode );
				break;
		}
		sendBroadcast( i, Permissions.SEND_COMMAND );
	}

	class CommandsReceiver extends BroadcastReceiver {

		@Override
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
