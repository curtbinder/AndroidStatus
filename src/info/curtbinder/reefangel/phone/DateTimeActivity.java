/*
 * Copyright (c) 2011-13 by Curt Binder (http://curtbinder.info)
 *
 * This work is made available under the terms of the 
 * Creative Commons Attribution-NonCommercial-ShareAlike 3.0 Unported License
 * http://creativecommons.org/licenses/by-nc-sa/3.0/
 */

package info.curtbinder.reefangel.phone;

import info.curtbinder.reefangel.controller.DateTime;
import info.curtbinder.reefangel.service.MessageCommands;

import java.util.Calendar;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

public class DateTimeActivity extends Activity implements OnClickListener {

	private static final String TAG = DateTimeActivity.class.getSimpleName();

	private Button getControllerTimeButton;
	private Button setCurrentTimeButton;
	private Button changeDateButton;
	private Button changeTimeButton;
	private Button setCustomTimeButton;
	private TextView controllerTimeText;
	private TextView customTimeText;
	private TextView customDateText;
	private DateTime dt = new DateTime();
	private boolean fCustomDateSet = false;
	private boolean fCustomTimeSet = false;

	CommandsReceiver receiver;
	IntentFilter filter;

	protected void onCreate ( Bundle savedInstanceState ) {
		super.onCreate( savedInstanceState );
		setContentView( R.layout.datetime );

		findViews();
		receiver = new CommandsReceiver();
		filter = new IntentFilter( MessageCommands.DATE_QUERY_RESPONSE_INTENT );
		filter.addAction( MessageCommands.DATE_SEND_RESPONSE_INTENT );

		setOnClickListeners();
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
		getControllerTimeButton =
				(Button) findViewById( R.id.timeButtonGetControllerTime );
		setCurrentTimeButton =
				(Button) findViewById( R.id.timeButtonSetCurrentTime );
		changeDateButton = (Button) findViewById( R.id.timeButtonChangeDate );
		changeTimeButton = (Button) findViewById( R.id.timeButtonChangeTime );
		setCustomTimeButton =
				(Button) findViewById( R.id.timeButtonSetCustomTime );
		controllerTimeText =
				(TextView) findViewById( R.id.timeTextControllerTime );
		customTimeText = (TextView) findViewById( R.id.timeTextCustomTime );
		customDateText = (TextView) findViewById( R.id.timeTextCustomDate );
	}

	private void setOnClickListeners ( ) {
		getControllerTimeButton.setOnClickListener( this );
		setCurrentTimeButton.setOnClickListener( this );
		changeDateButton.setOnClickListener( this );
		changeTimeButton.setOnClickListener( this );
		setCustomTimeButton.setOnClickListener( this );
	}

	private void displayChangeDateDialog ( ) {
		Log.d( TAG, "Change Date" );
		final Calendar c = Calendar.getInstance();
		DatePickerDialog t;
		t =
				new DatePickerDialog( this,
					new DatePickerDialog.OnDateSetListener() {

						public void onDateSet (
								DatePicker view,
								int year,
								int monthOfYear,
								int dayOfMonth ) {
							fCustomDateSet = true;
							dt.setYear( year );
							dt.setMonth( monthOfYear );
							dt.setDay( dayOfMonth );
							customDateText.setText( dt.getDateString() );
						}

					}, c.get( Calendar.YEAR ), c.get( Calendar.MONTH ),
					c.get( Calendar.DAY_OF_MONTH ) );
		t.show();
	}

	private void displayChangeTimeDialog ( ) {
		Log.d( TAG, "Change Time" );
		final Calendar c = Calendar.getInstance();
		TimePickerDialog t;
		t =
				new TimePickerDialog( this,
					new TimePickerDialog.OnTimeSetListener() {

						public void onTimeSet (
								TimePicker view,
								int hourOfDay,
								int minute ) {
							fCustomTimeSet = true;
							dt.setHour( hourOfDay );
							dt.setMinute( minute );
							Log.d(	TAG,
									dt.getTimeString() + " - " + dt.getHour()
											+ ":" + dt.getMinute() );
							customTimeText.setText( dt.getTimeString() );
						}
					}, c.get( Calendar.HOUR_OF_DAY ), c.get( Calendar.MINUTE ),
					true );
		t.show();
	}

	private boolean canSetCustomTime ( ) {
		Log.d( TAG, "Can Set Custom Time" );
		if ( !fCustomTimeSet && !fCustomDateSet ) {
			Log.d( TAG, "cannot set custom date & time" );
			return false;
		}
		return true;
	}

	public void onClick ( View v ) {
		Intent i = new Intent();
		boolean fSend = false;
		switch ( v.getId() ) {
			case R.id.timeButtonChangeDate:
				displayChangeDateDialog();
				break;
			case R.id.timeButtonChangeTime:
				displayChangeTimeDialog();
				break;
			case R.id.timeButtonGetControllerTime:
				Log.d( TAG, "Get Time" );
				i.setAction( MessageCommands.DATE_QUERY_INTENT );
				fSend = true;
				break;
			case R.id.timeButtonSetCurrentTime:
				Log.d( TAG, "Set Current Time" );
				dt.setWithCurrentDateTime();
				Log.d( TAG, "DT: " + dt.getDateTimeString() );
				// clear the current time on the screen
				controllerTimeText.setText( "" );
				i.setAction( MessageCommands.DATE_SEND_INTENT );
				i.putExtra( MessageCommands.DATE_SEND_STRING,
							dt.getSetCommand() );
				fSend = true;
				break;
			case R.id.timeButtonSetCustomTime:
				if ( !canSetCustomTime() ) {
					return;
				}
				Log.d( TAG, "Custom DT: " + dt.getDateTimeString() );
				controllerTimeText.setText( "" );
				i.setAction( MessageCommands.DATE_SEND_INTENT );
				i.putExtra( MessageCommands.DATE_SEND_STRING,
							dt.getSetCommand() );
				fSend = true;
				break;
			default:
				return;
		}
		if ( fSend ) {
			sendBroadcast( i, Permissions.SEND_COMMAND );
		}
	}

	class CommandsReceiver extends BroadcastReceiver {

		public void onReceive ( Context context, Intent intent ) {
			if ( intent.getAction()
					.equals( MessageCommands.DATE_QUERY_RESPONSE_INTENT ) ) {
				controllerTimeText
						.setText( intent
								.getStringExtra( MessageCommands.DATE_QUERY_RESPONSE_STRING ) );
			} else if ( intent.getAction()
					.equals( MessageCommands.DATE_SEND_RESPONSE_INTENT ) ) {
				Toast.makeText( DateTimeActivity.this,
								intent.getStringExtra( MessageCommands.DATE_SEND_RESPONSE_STRING ),
								Toast.LENGTH_SHORT ).show();
			}
		}

	}
}
