/*
 * Copyright (c) 2011-2014 by Curt Binder (http://curtbinder.info)
 * 
 * This work is made available under the terms of the Creative Commons
 * Attribution-NonCommercial-ShareAlike 3.0 Unported License
 * http://creativecommons.org/licenses/by-nc-sa/3.0/
 */

package info.curtbinder.reefangel.phone;

import info.curtbinder.reefangel.service.MessageCommands;
import info.curtbinder.reefangel.service.UpdateService;

import java.util.Locale;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

public class OverridePopupActivity extends Activity 
	implements OnClickListener, SeekBar.OnSeekBarChangeListener {

	public static final String TAG = OverridePopupActivity.class.getSimpleName();
	public static final String MESSAGE_KEY = "message_key";
	public static final String CHANNEL_KEY = "channel_key";
	public static final String VALUE_KEY = "value_key";
	
	private TextView tvTitle;
	private TextView tvValue;
	private int pwmChannel;
	private short currentValue;
	private SeekBar seek;
	
	@Override
	protected void onCreate ( Bundle savedInstanceState ) {
		super.onCreate( savedInstanceState );
		setContentView(R.layout.overridepopup);
		pwmChannel = Globals.OVERRIDE_DAYLIGHT;
		currentValue = 0;
		
		findViews();
		setOnClickListeners();
		
		Bundle extras = getIntent().getExtras();
		if ( extras != null ) {
			String msg = extras.getString( MESSAGE_KEY );
			pwmChannel = extras.getInt( CHANNEL_KEY, 0 );
			currentValue = extras.getShort( VALUE_KEY, (short) 0 );
			tvTitle.setText( msg );
		}
		// TODO Set progress bar to update value on change
		seek.setMax( Globals.OVERRIDE_MAX_VALUE );
		seek.setProgress( currentValue );
		seek.setOnSeekBarChangeListener( this );
		updateProgressText();
	}

	@SuppressLint("DefaultLocale")
	private void updateProgressText ( ) {
		String s = String.format(Locale.getDefault(), "%d%%", currentValue);
		tvValue.setText( s );
	}
	
	private void findViews ( ) {
		tvTitle = (TextView) findViewById(R.id.textOverrideTitle);
		tvValue = (TextView) findViewById(R.id.textOverrideValue);
		seek = (SeekBar) findViewById(R.id.seekOverride);
	}

	private void setOnClickListeners ( ) {
		Button b = (Button) findViewById(R.id.buttonCancel);
		b.setOnClickListener( this );
		b = (Button) findViewById(R.id.buttonClearOverride);
		b.setOnClickListener( this );
		b = (Button) findViewById(R.id.buttonSetOverride);
		b.setOnClickListener( this );
	}

	@Override
	public void onClick ( View v ) {
		int id = v.getId();
		switch ( id ) {
			case R.id.buttonCancel:
				break;
			case R.id.buttonClearOverride:
				updateOverride(Globals.OVERRIDE_DISABLE);
				break;
			case R.id.buttonSetOverride:
				updateOverride(currentValue);
				break;
		}
		finish();
	}
	
	private void updateOverride ( int value ) {
		Intent i = new Intent( this, UpdateService.class );
		i.setAction( MessageCommands.OVERRIDE_SEND_INTENT );
		i.putExtra( MessageCommands.OVERRIDE_SEND_LOCATION_INT, pwmChannel );
		i.putExtra( MessageCommands.OVERRIDE_SEND_VALUE_INT, value );
		Log.d(TAG, "updateOverride: channel: " + pwmChannel + ", value: " + value);
		startService(i);
	}

	@Override
	public void onProgressChanged (
			SeekBar seekBar,
			int progress,
			boolean fromUser ) {
		currentValue = (short) progress;
		updateProgressText();
	}

	@Override
	public void onStartTrackingTouch ( SeekBar seekBar ) {
	}

	@Override
	public void onStopTrackingTouch ( SeekBar seekBar ) {
	}
}
