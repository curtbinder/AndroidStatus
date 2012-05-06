package info.curtbinder.reefangel.phone;

/*
 * Copyright (c) 2011-12 by Curt Binder (http://curtbinder.info)
 *
 * This work is made available under the terms of the 
 * Creative Commons Attribution-NonCommercial-ShareAlike 3.0 Unported License
 * http://creativecommons.org/licenses/by-nc-sa/3.0/
 */

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class DateTimeActivity extends BaseActivity implements OnClickListener {

	private static final String TAG = DateTimeActivity.class.getSimpleName();

	private Button getControllerTimeButton;
	private Button setCurrentTimeButton;
	private Button changeDateButton;
	private Button changeTimeButton;
	private Button setCustomTimeButton;
	private TextView controllerTimeText;
	private TextView customTimeText;
	private TextView customDateText;
	

	CommandsReceiver receiver;
	IntentFilter filter;

	@Override
	protected void onCreate ( Bundle savedInstanceState ) {
		super.onCreate( savedInstanceState );
		setContentView( R.layout.datetime );

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
		getControllerTimeButton = (Button)findViewById(R.id.timeButtonGetControllerTime);
		setCurrentTimeButton = (Button)findViewById(R.id.timeButtonSetCurrentTime);
		changeDateButton = (Button)findViewById(R.id.timeButtonChangeDate);
		changeTimeButton = (Button)findViewById(R.id.timeButtonChangeTime);
		setCustomTimeButton = (Button)findViewById(R.id.timeButtonSetCustomTime);
		controllerTimeText = (TextView)findViewById(R.id.timeTextControllerTime);
		customTimeText = (TextView)findViewById(R.id.timeTextCustomTime);
		customDateText = (TextView)findViewById(R.id.timeTextCustomDate);
	}

	private void setOnClickListeners ( ) {
		getControllerTimeButton.setOnClickListener(this);
		setCurrentTimeButton.setOnClickListener(this);
		changeDateButton.setOnClickListener(this);
		changeTimeButton.setOnClickListener(this);
		setCustomTimeButton.setOnClickListener(this);
	}

	private void displayChangeDateDialog() {
		// TODO launch dialog to pick date, then save date and update display
		Log.d(TAG, "Change Date");
		customDateText.setText("MM/DD/YYYY");
	}
	
	private void displayChangeTimeDialog() {
		// TODO launch dialog to pick time, then save time and update display
		Log.d(TAG, "Change Time");
		customTimeText.setText("HH:MM");
	}
	
	@Override
	public void onClick ( View v ) {
		Intent i = new Intent();
		switch ( v.getId() ) {
		case R.id.timeButtonChangeDate:
			displayChangeDateDialog();
			break;
		case R.id.timeButtonChangeTime:
			displayChangeTimeDialog();
			break;
		case R.id.timeButtonGetControllerTime:
			// TODO intent to get current date & time from controller
			Log.d(TAG, "Get Time");
			break;
		case R.id.timeButtonSetCurrentTime:
			// TODO get system time and date, send intent to controller
			Log.d(TAG, "Set Current Time");
			break;
		case R.id.timeButtonSetCustomTime:
			// TODO get saved time & date, send intent to controller
			Log.d(TAG, "Set Custom Time");
			break;
		default:
			return;
		/*
			case R.id.command_button_exit:
				i.setAction( MessageCommands.COMMAND_SEND_INTENT );
				i.putExtra( MessageCommands.COMMAND_SEND_STRING,
							Globals.requestExitMode );
				break;
		*/
		}
		sendBroadcast( i, Permissions.SEND_COMMAND );
	}

	class CommandsReceiver extends BroadcastReceiver {

		@Override
		public void onReceive ( Context context, Intent intent ) {
			/*
			if ( intent.getAction()
					.equals( MessageCommands.COMMAND_RESPONSE_INTENT ) ) {
				Toast.makeText( DateTimeActivity.this,
								intent.getStringExtra( MessageCommands.COMMAND_RESPONSE_STRING ),
								Toast.LENGTH_LONG ).show();
			}
			*/
			/*
			 * TODO 
			 *   Get controller time - update display on screen with
			 *   	date & time in users locale
			 *   Set time response - display toast that was successful 
			 */
		}

	}
}
