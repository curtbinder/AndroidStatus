package info.curtbinder.reefangel.phone;

/*
 * Copyright (c) 2011-13 by Curt Binder (http://curtbinder.info)
 *
 * This work is made available under the terms of the 
 * Creative Commons Attribution-NonCommercial-ShareAlike 3.0 Unported License
 * http://creativecommons.org/licenses/by-nc-sa/3.0/
 */

import info.curtbinder.reefangel.service.MessageCommands;
import info.curtbinder.reefangel.service.RequestCommands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;

public class VortechPopupActivity extends BaseActivity implements
		OnClickListener {

	private static final String TAG = VortechPopupActivity.class
			.getSimpleName();

	public static final String TYPE = "vtType";
	public static final int MODE = 0;
	public static final int SPEED = 1;
	public static final int DURATION = 2;
	public static final int LOCATION_OFFSET = 55;

	private int popupType;

	private TextView desc;
	private Spinner sp;

	@Override
	protected void onCreate ( Bundle savedInstanceState ) {
		super.onCreate( savedInstanceState );
		setContentView( R.layout.vortechpopup );
		Bundle b = getIntent().getExtras();
		if ( b != null ) {
			popupType = b.getInt( "vtType" );
		}
		desc = (TextView) findViewById( R.id.vtDescription );
		sp = (Spinner) findViewById( R.id.vtSpinner );
		List<Map<String, String>> data = new ArrayList<Map<String, String>>();
		String[] from = new String[] { "data" };
		int[] to = new int[] { android.R.id.text1 };

		switch ( popupType ) {
			default:
				// invalid type, so set to MODE
				popupType = MODE;
			case MODE:
				desc.setText( R.string.descriptionMode );
				String[] labels =
						getResources()
								.getStringArray( R.array.vortechModeLabels );
				// only modes 0 - 11
				for ( int i = 0; i < 12; i++ ) {
					data.add( addData( labels[i] ) );
				}
				break;
			case SPEED:
				desc.setText( R.string.descriptionSpeed );
				for ( int i = 0; i < 100; i++ ) {
					data.add( addData( String.format( "%d%%", i ) ) );
				}
				break;
			case DURATION:
				desc.setText( R.string.descriptionDuration );
				for ( int i = 0; i < 255; i++ ) {
					data.add( addData( String.format( "%d", i ) ) );
				}
				break;
		}
		SimpleAdapter simpleAdapter =
				new SimpleAdapter( this, data,
					android.R.layout.simple_spinner_item, from, to );
		simpleAdapter
				.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item );
		sp.setAdapter( simpleAdapter );

		Button btn = (Button) findViewById( R.id.vtBtnCancel );
		btn.setOnClickListener( this );
		btn = (Button) findViewById( R.id.vtBtnUpdate );
		btn.setOnClickListener( this );
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
		Intent i = new Intent( MessageCommands.MEMORY_SEND_INTENT );
		int location;
		if ( rapp.useOldPre10MemoryLocations() ) {
			location = MemoryActivity.LOCATION_START_OLD;
		} else {
			location = MemoryActivity.LOCATION_START;
		}
		location += LOCATION_OFFSET + popupType;

		int value = sp.getSelectedItemPosition();
		i.putExtra( MessageCommands.MEMORY_SEND_TYPE_STRING,
					RequestCommands.MemoryByte );
		i.putExtra( MessageCommands.MEMORY_SEND_LOCATION_INT, location );
		i.putExtra( MessageCommands.MEMORY_SEND_VALUE_INT, value );
		Log.d( TAG, "Update Vortech:  " + RequestCommands.MemoryByte + location
					+ "," + value );
		sendBroadcast( i, Permissions.SEND_COMMAND );
	}
}
