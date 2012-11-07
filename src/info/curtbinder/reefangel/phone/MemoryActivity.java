package info.curtbinder.reefangel.phone;

/*
 * Copyright (c) 2011-12 by Curt Binder (http://curtbinder.info)
 *
 * This work is made available under the terms of the 
 * Creative Commons Attribution-NonCommercial-ShareAlike 3.0 Unported License
 * http://creativecommons.org/licenses/by-nc-sa/3.0/
 */

import info.curtbinder.reefangel.service.MessageCommands;
import info.curtbinder.reefangel.service.RequestCommands;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

public class MemoryActivity extends BaseActivity {

	private final static String TAG = MemoryActivity.class.getSimpleName();

	final static int LOCATION_MIN = 0;
	final static int LOCATION_MAX = 1023;
	final static int LOCATION_START_OLD = 800;
	final static int LOCATION_START = 200;
	final static int TYPE_BYTE = 0;
	final static int TYPE_INT = 1;

	final static int PWM_MIN = 0;
	final static int PWM_MAX = 100;
	final static int HR_MIN = 0;
	final static int HR_MAX = 23;
	final static int MIN_MIN = 0;
	final static int MIN_MAX = 59;
	final static int BYTE_MIN = 0;
	final static int BYTE_MAX = 255;
	final static int INT_MIN = 0;
	final static int INT_MAX = 32767;
	final static int WM_MIN = 0;
	final static int WM_MAX = 21600;
	final static int PH_MIN = 0;
	final static int PH_MAX = 1024;
	final static int TIMEOUTS_MIN = 0;
	final static int TIMEOUTS_MAX = 3600;

	private Spinner locationSpinner;
	private EditText locationText;
	private EditText valueText;
	private Button readButton;
	private Button writeButton;
	private RadioButton byteButton;
	private RadioButton intButton;
	private int[] memoryLocations;
	private int[] memoryLocationsTypes;

	MemoryReceiver receiver;
	IntentFilter filter;

	protected void onCreate ( Bundle savedInstanceState ) {
		super.onCreate( savedInstanceState );
		setContentView( R.layout.memory );

		// Message receiver
		receiver = new MemoryReceiver();
		filter = new IntentFilter( MessageCommands.MEMORY_RESPONSE_INTENT );
		// filter.addAction( MessageCommands.UPDATE_STATUS_INTENT );

		findViews();
		setAdapters();
		setOnClickListeners();

		memoryLocations = getResources().getIntArray( R.array.memoryLocations );
		memoryLocationsTypes =
				getResources().getIntArray( R.array.memoryLocationsTypes );
		setInitialValues();
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
		locationSpinner = (Spinner) findViewById( R.id.spinMemoryLocation );
		locationText = (EditText) findViewById( R.id.locationText );
		valueText = (EditText) findViewById( R.id.valueText );
		readButton = (Button) findViewById( R.id.buttonRead );
		writeButton = (Button) findViewById( R.id.buttonWrite );
		byteButton = (RadioButton) findViewById( R.id.radioButtonByte );
		intButton = (RadioButton) findViewById( R.id.radioButtonInt );
	}

	private void setAdapters ( ) {
		ArrayAdapter<CharSequence> adapter =
				ArrayAdapter
						.createFromResource(	this,
												R.array.memoryLocationsNames,
												android.R.layout.simple_spinner_item );
		adapter.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item );
		locationSpinner.setAdapter( adapter );
	}

	private boolean checkValueRange ( ) {
		boolean fRet = true;
		String s = valueText.getText().toString();
		Log.d( TAG, "Value: '" + s + "'" );
		if ( s.equals( "" ) ) {
			// Empty string
			Toast.makeText( MemoryActivity.this,
							getResources()
									.getString( R.string.messageEmptyValue ),
							Toast.LENGTH_SHORT ).show();
			return false;
		}
		int v = Integer.parseInt( s );
		int sel = locationSpinner.getSelectedItemPosition();
		Log.d( TAG, "Selection: " + sel );
		if ( intButton.isChecked() ) {
			if ( isSpecialLocation( sel, R.array.wavemakersIndex ) ) {
				if ( (v < WM_MIN) || (v > WM_MAX) ) {
					Toast.makeText( MemoryActivity.this,
									getResources()
											.getString( R.string.messageInvalidRangeFormat,
														WM_MIN, WM_MAX ),
									Toast.LENGTH_SHORT ).show();
					fRet = false;
				}
			} else if ( isSpecialLocation( sel, R.array.phIndex ) ) {
				if ( (v < PH_MIN) || (v > PH_MAX) ) {
					Toast.makeText( MemoryActivity.this,
									getResources()
											.getString( R.string.messageInvalidRangeFormat,
														PH_MIN, PH_MAX ),
									Toast.LENGTH_SHORT ).show();
					fRet = false;
				}
			} else if ( isSpecialLocation( sel, R.array.timeoutIndex ) ) {
				if ( (v < TIMEOUTS_MIN) || (v > TIMEOUTS_MAX) ) {
					Toast.makeText( MemoryActivity.this,
									getResources()
											.getString( R.string.messageInvalidRangeFormat,
														TIMEOUTS_MIN,
														TIMEOUTS_MAX ),
									Toast.LENGTH_SHORT ).show();
					fRet = false;
				}
			} else {
				if ( (v < INT_MIN) || (v > INT_MAX) ) {
					Toast.makeText( MemoryActivity.this,
									getResources()
											.getString( R.string.messageInvalidRangeFormat,
														INT_MIN, INT_MAX ),
									Toast.LENGTH_SHORT ).show();
					fRet = false;
				}
			}
		} else if ( byteButton.isChecked() ) {
			if ( isSpecialLocation( sel, R.array.hourIndex ) ) {
				if ( (v < HR_MIN) || (v > HR_MAX) ) {
					Toast.makeText( MemoryActivity.this,
									getResources()
											.getString( R.string.messageInvalidRangeFormat,
														HR_MIN, HR_MAX ),
									Toast.LENGTH_SHORT ).show();
					fRet = false;
				}
			} else if ( isSpecialLocation( sel, R.array.minuteIndex ) ) {
				if ( (v < MIN_MIN) || (v > MIN_MAX) ) {
					Toast.makeText( MemoryActivity.this,
									getResources()
											.getString( R.string.messageInvalidRangeFormat,
														MIN_MIN, MIN_MAX ),
									Toast.LENGTH_SHORT ).show();
					fRet = false;
				}
			} else if ( isSpecialLocation( sel, R.array.pwmIndex ) ) {
				if ( (v < PWM_MIN) || (v > PWM_MAX) ) {
					Toast.makeText( MemoryActivity.this,
									getResources()
											.getString( R.string.messageInvalidRangeFormat,
														PWM_MIN, PWM_MAX ),
									Toast.LENGTH_SHORT ).show();
					fRet = false;
				}
			} else {
				if ( (v < BYTE_MIN) || (v > BYTE_MAX) ) {
					Toast.makeText( MemoryActivity.this,
									getResources()
											.getString( R.string.messageInvalidRangeFormat,
														BYTE_MIN, BYTE_MAX ),
									Toast.LENGTH_SHORT ).show();
					fRet = false;
				}
			}
		}
		return fRet;
	}

	private boolean isSpecialLocation ( int pos, int arrayID ) {
		int[] loc = getResources().getIntArray( arrayID );
		boolean fRet = false;
		for ( int i = 0; i < loc.length; i++ ) {
			if ( loc[i] == pos ) {
				fRet = true;
				break;
			}
		}
		return fRet;
	}

	private boolean checkLocationValue ( ) {
		boolean fRet = true;
		String s = locationText.getText().toString();
		Log.d( TAG, "Location: '" + s + "'" );
		if ( s.equals( "" ) ) {
			// Empty string
			Toast.makeText( MemoryActivity.this,
							getResources()
									.getString( R.string.messageEmptyLocation ),
							Toast.LENGTH_SHORT ).show();
			return false;
		}
		int v = Integer.parseInt( s );
		if ( (v < LOCATION_MIN) || (v > LOCATION_MAX) ) {
			Toast.makeText( MemoryActivity.this,
							getResources()
									.getString( R.string.messageInvalidLocation,
												LOCATION_MIN, LOCATION_MAX ),
							Toast.LENGTH_SHORT ).show();
			fRet = false;
		}
		return fRet;
	}

	private void setOnClickListeners ( ) {
		// create on click listeners
		readButton.setOnClickListener( new OnClickListener() {
			public void onClick ( View v ) {
				if ( !checkLocationValue() ) {
					return;
				}
				// good location, proceed
				sendMessage( false );
			}
		} );
		writeButton.setOnClickListener( new OnClickListener() {
			public void onClick ( View v ) {
				if ( !checkLocationValue() ) {
					return;
				}
				if ( !checkValueRange() ) {
					return;
				}
				// good location and value, proceed
				sendMessage( true );
			}
		} );
		locationSpinner
				.setOnItemSelectedListener( new OnItemSelectedListener() {

					public void onItemSelected (
							AdapterView<?> parent,
							View v,
							int position,
							long id ) {
						setItemSelected( (int) id );
					}

					public void onNothingSelected ( AdapterView<?> arg0 ) {
					}
				} );
	}

	private void setItemSelected ( int id ) {
		boolean enable = false;
		int start;
		if ( rapp.useOldPre10MemoryLocations() ) {
			start = LOCATION_START_OLD;
		} else {
			start = LOCATION_START;
		}

		if ( id > 0 ) {
			String s =
					new String( String.format( "%d", start
														+ memoryLocations[id] ) );
			locationText.setText( s );
		} else {
			locationText.setText( "" );
		}
		// update the radio button
		if ( memoryLocationsTypes[id] == TYPE_BYTE ) {
			byteButton.setChecked( true );
			intButton.setChecked( false );
		} else {
			byteButton.setChecked( false );
			intButton.setChecked( true );
		}

		if ( id == 0 ) {
			enable = true;
		}
		updateLocationEditability( enable );
	}

	private void setInitialValues ( ) {
		locationSpinner.setSelection( 1 );
		setItemSelected( 1 );
	}

	private void updateLocationEditability ( boolean enable ) {
		// updates the enabling/disabling of the location & radio buttons based
		// on the location drop down menu
		byteButton.setEnabled( enable );
		intButton.setEnabled( enable );
		locationText.setEnabled( enable );
		if ( enable ) {
			locationText.requestFocus();
		} else {
			valueText.requestFocus();
		}
	}

	public void updateValue ( String value ) {
		valueText.setText( value );
	}

	private void sendMessage ( boolean write ) {
		Log.d( TAG, "sendMessage" );
		Intent i = new Intent( MessageCommands.MEMORY_SEND_INTENT );
		String type = RequestCommands.MemoryByte;
		int value = Globals.memoryReadOnly;
		int id = R.string.messageReadingMemory;

		if ( write ) {
			value = (int) Integer.parseInt( valueText.getText().toString() );
			id = R.string.messageWritingMemory;
		}

		if ( intButton.isChecked() )
			type = RequestCommands.MemoryInt;

		i.putExtra( MessageCommands.MEMORY_SEND_TYPE_STRING, type );
		i.putExtra( MessageCommands.MEMORY_SEND_LOCATION_INT,
					(int) Integer.parseInt( locationText.getText().toString() ) );
		i.putExtra( MessageCommands.MEMORY_SEND_VALUE_INT, value );
		sendBroadcast( i, Permissions.SEND_COMMAND );
		Toast.makeText( MemoryActivity.this, rapp.getString( id ),
						Toast.LENGTH_LONG ).show();
	}

	class MemoryReceiver extends BroadcastReceiver {

		public void onReceive ( Context context, Intent intent ) {
			String action = intent.getAction();
			if ( action.equals( MessageCommands.MEMORY_RESPONSE_INTENT ) ) {
				boolean wasWrite =
						intent.getBooleanExtra( MessageCommands.MEMORY_RESPONSE_WRITE_BOOLEAN,
												false );
				String response =
						intent.getStringExtra( MessageCommands.MEMORY_RESPONSE_STRING );
				if ( wasWrite ) {
					// do something since we wrote
					Toast.makeText( MemoryActivity.this, response,
									Toast.LENGTH_LONG ).show();
				} else {
					// do something for read
					updateValue( response );
				}
				// } else if ( action.equals(
				// MessageCommands.UPDATE_STATUS_INTENT ) ) {
				// TextView s = (TextView) findViewById( R.id.statusText );
				// int id =
				// intent.getIntExtra( MessageCommands.UPDATE_STATUS_ID,
				// -1 );
				// if ( id > -1 )
				// s.setText( id );
			}
		}
	}
}
