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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;

public class VortechPopupActivity extends Activity 
	implements OnClickListener, SeekBar.OnSeekBarChangeListener {

	private static final String TAG = VortechPopupActivity.class
			.getSimpleName();

	public static final String TYPE = "vtType";
	public static final String VALUE = "vtValue";
	public static final int MODE = 0;
	public static final int SPEED = 1;
	public static final int DURATION = 2;
	public static final int LOCATION_OFFSET = 55;

	private int popupType;
	private boolean preLocations;
	private int currentValue;

	private Spinner sp;
	private SeekBar seek;
	private TextView tvValue;

	@Override
	protected void onCreate ( Bundle savedInstanceState ) {
		super.onCreate( savedInstanceState );
		Bundle b = getIntent().getExtras();
		if ( b != null ) {
			popupType = b.getInt( TYPE );
			preLocations = b.getBoolean( Globals.PRE10_LOCATIONS );
			currentValue = b.getInt( VALUE );
		} else {
			preLocations = false;
			currentValue = 0;
		}

		switch ( popupType ) {
			default:
				// invalid type, so set to MODE
				popupType = MODE;
			case MODE:
				setContentView( R.layout.vortechpopup );
				sp = (Spinner) findViewById( R.id.vtSpinner );
				setupModeSpinner();
				((TextView)findViewById( R.id.vtDescription )).setText( R.string.descriptionMode );
				break;
			case SPEED:
				setContentView( R.layout.vortechpopup2 );
				setupSeekBar(true);
				((TextView)findViewById( R.id.vtDescription )).setText( R.string.descriptionSpeed );
				break;
			case DURATION:
				setContentView( R.layout.vortechpopup2 );
				setupSeekBar(false);
				((TextView)findViewById( R.id.vtDescription )).setText( R.string.descriptionDuration );
				break;
		}

		Button btn = (Button) findViewById( R.id.vtBtnCancel );
		btn.setOnClickListener( this );
		btn = (Button) findViewById( R.id.vtBtnUpdate );
		btn.setOnClickListener( this );
	}

	private void setupModeSpinner ( ) {
		List<Map<String, String>> data = new ArrayList<Map<String, String>>();
		String[] from = new String[] { "data" };
		int[] to = new int[] { android.R.id.text1 };
		String[] labels =
				getResources()
						.getStringArray( R.array.vortechModeLabels );
		// only modes 0 - 11
		for ( int i = 0; i < 12; i++ ) {
			data.add( addData( labels[i] ) );
		}
		SimpleAdapter simpleAdapter =
				new SimpleAdapter( this, data,
					android.R.layout.simple_spinner_item, from, to );
		simpleAdapter
				.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item );
		sp.setAdapter( simpleAdapter );
		sp.setSelection( currentValue );
	}
	
	private void setupSeekBar ( Boolean fSpeed ) {
		int max;
		if ( fSpeed ) {
			max = 100;
		} else {
			max = 255;
		}
		seek = (SeekBar) findViewById(R.id.vtSeek);
		seek.setMax( max );
		seek.setProgress( currentValue );
		seek.setOnSeekBarChangeListener( this );
		tvValue = (TextView) findViewById(R.id.vtSeekValue);
		updateProgressText( currentValue );
	}
	
	private Map<String, String> addData ( String value ) {
		Map<String, String> mapList = new HashMap<String, String>();
		mapList.put( "data", value );
		return mapList;
	}

	@Override
	public void onClick ( View v ) {
		switch ( v.getId() ) {
			case R.id.vtBtnCancel:
				finish();
				break;
			case R.id.vtBtnUpdate:
				updateSettings();
				finish();
				break;
		}
	}

	private void updateSettings ( ) {
		// grab the value
		// send the memory command
		Intent i = new Intent( this, UpdateService.class );
		i.setAction( MessageCommands.MEMORY_SEND_INTENT );
		int location;
		if ( preLocations ) {
			location = MemoryActivity.LOCATION_START_OLD;
		} else {
			location = MemoryActivity.LOCATION_START;
		}
		location += LOCATION_OFFSET + popupType;

		int value;
		if ( popupType == MODE ) {
			value = sp.getSelectedItemPosition();
		} else {
			value = seek.getProgress();
		}
		i.putExtra( MessageCommands.MEMORY_SEND_TYPE_STRING,
					RequestCommands.MemoryByte );
		i.putExtra( MessageCommands.MEMORY_SEND_LOCATION_INT, location );
		i.putExtra( MessageCommands.MEMORY_SEND_VALUE_INT, value );
		Log.d( TAG, "Update Vortech:  " + RequestCommands.MemoryByte + location
					+ "," + value );
		startService( i );
	}

	@SuppressLint("DefaultLocale")
	private void updateProgressText ( int value ) {
		String s;
		if ( popupType == SPEED ) {
			s = String.format(Locale.getDefault(), "%d%%", value);
		} else {
			s = String.format(Locale.getDefault(), "%d", value);
		}
		tvValue.setText( s );
	}
	
	@Override
	public void onProgressChanged (
			SeekBar seekBar,
			int progress,
			boolean fromUser ) {
		updateProgressText(progress);
	}

	@Override
	public void onStartTrackingTouch ( SeekBar seekBar ) {	
	}

	@Override
	public void onStopTrackingTouch ( SeekBar seekBar ) {
	}
}
