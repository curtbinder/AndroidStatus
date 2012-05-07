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
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.database.SQLException;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.ToggleButton;

public class StatusActivity extends BaseActivity implements OnClickListener {
	private static final String TAG = StatusActivity.class.getSimpleName();

	// Display views
	private View refreshButton;
	private TextView updateTime;
	// private TextView messageText;
	private TextView t1Text;
	private TextView t2Text;
	private TextView t3Text;
	private TextView phText;
	private TextView dpText;
	private TextView apText;
	private TextView salinityText;
	private TextView t1Label;
	private TextView t2Label;
	private TextView t3Label;
	private TextView phLabel;
	private TextView dpLabel;
	private TextView apLabel;
	private TextView salinityLabel;

	private TextView[] mainPortLabels =
			new TextView[Controller.MAX_RELAY_PORTS];
	private ToggleButton[] mainPortBtns =
			new ToggleButton[Controller.MAX_RELAY_PORTS];
	private View[] mainPortMaskBtns = new View[Controller.MAX_RELAY_PORTS];

	// private TextView[] exp1PortLabels =
	// new TextView[Controller.MAX_RELAY_PORTS];
	// private ToggleButton[] exp1PortBtns =
	// new ToggleButton[Controller.MAX_RELAY_PORTS];
	// private View[] exp1PortMaskBtns = new View[Controller.MAX_RELAY_PORTS];

	// Message Receivers
	StatusReceiver receiver;
	IntentFilter filter;

	// View visibility
	// private boolean showMessageText;

	/** Called when the activity is first created. */
	@Override
	public void onCreate ( Bundle savedInstanceState ) {
		super.onCreate( savedInstanceState );
		setContentView( R.layout.status );

		// Message Receiver stuff
		receiver = new StatusReceiver();
		filter = new IntentFilter( MessageCommands.UPDATE_DISPLAY_DATA_INTENT );
		filter.addAction( MessageCommands.UPDATE_STATUS_INTENT );
		filter.addAction( MessageCommands.ERROR_MESSAGE_INTENT );

		findViews();

		setOnClickListeners();

		// Check if this is the first run, if so we need to prompt the user
		// to configure before we start the service and proceed
		// this should be the last thing done in OnCreate()
		if ( rapp.isFirstRun() ) {
			Log.w( TAG, "First Run of app" );
			Intent i = new Intent( this, FirstRunActivity.class );
			i.addFlags( Intent.FLAG_ACTIVITY_SINGLE_TOP );
			startActivity( i );
			finish();
		}
	}

	@Override
	protected void onPause ( ) {
		super.onPause();
		unregisterReceiver( receiver );
	}

	@Override
	protected void onResume ( ) {
		super.onResume();
		registerReceiver( receiver, filter, Permissions.QUERY_STATUS, null );
		updateViewsVisibility();
		updateDisplay();

		// TODO either put the displaying of the changelog here or in OnStart
	}

	private void findViews ( ) {
		refreshButton = findViewById( R.id.refresh_button );
		updateTime = (TextView) findViewById( R.id.updated );
		t1Text = (TextView) findViewById( R.id.temp1 );
		t2Text = (TextView) findViewById( R.id.temp2 );
		t3Text = (TextView) findViewById( R.id.temp3 );
		phText = (TextView) findViewById( R.id.ph );
		dpText = (TextView) findViewById( R.id.dp );
		apText = (TextView) findViewById( R.id.ap );
		salinityText = (TextView) findViewById( R.id.salinity );
		t1Label = (TextView) findViewById( R.id.t1_label );
		t2Label = (TextView) findViewById( R.id.t2_label );
		t3Label = (TextView) findViewById( R.id.t3_label );
		phLabel = (TextView) findViewById( R.id.ph_label );
		dpLabel = (TextView) findViewById( R.id.dp_label );
		apLabel = (TextView) findViewById( R.id.ap_label );
		salinityLabel = (TextView) findViewById( R.id.salinity_label );

		mainPortLabels[0] = (TextView) findViewById( R.id.main_port1_label );
		mainPortLabels[1] = (TextView) findViewById( R.id.main_port2_label );
		mainPortLabels[2] = (TextView) findViewById( R.id.main_port3_label );
		mainPortLabels[3] = (TextView) findViewById( R.id.main_port4_label );
		mainPortLabels[4] = (TextView) findViewById( R.id.main_port5_label );
		mainPortLabels[5] = (TextView) findViewById( R.id.main_port6_label );
		mainPortLabels[6] = (TextView) findViewById( R.id.main_port7_label );
		mainPortLabels[7] = (TextView) findViewById( R.id.main_port8_label );
		mainPortBtns[0] = (ToggleButton) findViewById( R.id.main_port1 );
		mainPortBtns[1] = (ToggleButton) findViewById( R.id.main_port2 );
		mainPortBtns[2] = (ToggleButton) findViewById( R.id.main_port3 );
		mainPortBtns[3] = (ToggleButton) findViewById( R.id.main_port4 );
		mainPortBtns[4] = (ToggleButton) findViewById( R.id.main_port5 );
		mainPortBtns[5] = (ToggleButton) findViewById( R.id.main_port6 );
		mainPortBtns[6] = (ToggleButton) findViewById( R.id.main_port7 );
		mainPortBtns[7] = (ToggleButton) findViewById( R.id.main_port8 );

		mainPortMaskBtns[0] = findViewById( R.id.main_port1mask );
		mainPortMaskBtns[1] = findViewById( R.id.main_port2mask );
		mainPortMaskBtns[2] = findViewById( R.id.main_port3mask );
		mainPortMaskBtns[3] = findViewById( R.id.main_port4mask );
		mainPortMaskBtns[4] = findViewById( R.id.main_port5mask );
		mainPortMaskBtns[5] = findViewById( R.id.main_port6mask );
		mainPortMaskBtns[6] = findViewById( R.id.main_port7mask );
		mainPortMaskBtns[7] = findViewById( R.id.main_port8mask );
	}

	private void setOnClickListeners ( ) {
		refreshButton.setOnClickListener( this );
		for ( int i = 0; i < 8; i++ ) {
			if ( rapp.isCommunicateController() ) {
				mainPortBtns[i].setOnClickListener( this );
				mainPortMaskBtns[i].setOnClickListener( this );
			} else {
				mainPortBtns[i].setClickable( false );
				mainPortMaskBtns[i].setClickable( false );
			}
		}
	}

	private void updateViewsVisibility ( ) {
		// updates all the views visibility based on user settings
		// get values from Preferences
		// showMessageText = false;

		// Labels
		String separator = getString( R.string.labelSeparator );
		t1Label.setText( rapp.getPrefT1Label() + separator );
		t2Label.setText( rapp.getPrefT2Label() + separator );
		t3Label.setText( rapp.getPrefT3Label() + separator );
		phLabel.setText( rapp.getPrefPHLabel() + separator );
		dpLabel.setText( rapp.getPrefDPLabel() + separator );
		apLabel.setText( rapp.getPrefAPLabel() + separator );
		salinityLabel.setText( rapp.getPrefSalinityLabel() + separator );

		for ( int i = 0; i < 8; i++ ) {
			mainPortLabels[i].setText( rapp.getPrefMainRelayLabel( i + 1 )
										+ separator );
		}

		// Visibility
		if ( rapp.getPrefT2Visibility() ) {
			Log.d( TAG, "T2 visible" );
			t2Text.setVisibility( View.VISIBLE );
			t2Label.setVisibility( View.VISIBLE );
		} else {
			Log.d( TAG, "T2 gone" );
			t2Text.setVisibility( View.GONE );
			t2Label.setVisibility( View.GONE );
		}
		if ( rapp.getPrefT3Visibility() ) {
			Log.d( TAG, "T3 visible" );
			t3Text.setVisibility( View.VISIBLE );
			t3Label.setVisibility( View.VISIBLE );
		} else {
			Log.d( TAG, "T3 gone" );
			t3Text.setVisibility( View.GONE );
			t3Label.setVisibility( View.GONE );
		}
		if ( rapp.getPrefDPVisibility() ) {
			Log.d( TAG, "DP visible" );
			dpText.setVisibility( View.VISIBLE );
			dpLabel.setVisibility( View.VISIBLE );
		} else {
			Log.d( TAG, "DP gone" );
			dpText.setVisibility( View.GONE );
			dpLabel.setVisibility( View.GONE );
		}
		if ( rapp.getPrefAPVisibility() ) {
			Log.d( TAG, "AP visible" );
			apText.setVisibility( View.VISIBLE );
			apLabel.setVisibility( View.VISIBLE );
		} else {
			Log.d( TAG, "AP gone" );
			apText.setVisibility( View.GONE );
			apLabel.setVisibility( View.GONE );
		}
		if ( rapp.getPrefSalinityVisibility() ) {
			Log.d( TAG, "Salinity visible" );
			salinityText.setVisibility( View.VISIBLE );
			salinityLabel.setVisibility( View.VISIBLE );
		} else {
			Log.d( TAG, "Salinity gone" );
			salinityText.setVisibility( View.GONE );
			salinityLabel.setVisibility( View.GONE );
		}
		// if ( ! showMessageText )
		// messageText.setVisibility(View.GONE);
	}

	@Override
	public void onClick ( View v ) {
		switch ( v.getId() ) {
			case R.id.refresh_button:
				// launch the update
				Log.d( TAG, "onClick Refresh button" );
				launchStatusTask();
				break;
			case R.id.main_port1:
				Log.d( TAG, "toggle port 1" );
				sendRelayToggleTask( 1 );
				break;
			case R.id.main_port2:
				Log.d( TAG, "toggle port 2" );
				sendRelayToggleTask( 2 );
				break;
			case R.id.main_port3:
				Log.d( TAG, "toggle port 3" );
				sendRelayToggleTask( 3 );
				break;
			case R.id.main_port4:
				Log.d( TAG, "toggle port 4" );
				sendRelayToggleTask( 4 );
				break;
			case R.id.main_port5:
				Log.d( TAG, "toggle port 5" );
				sendRelayToggleTask( 5 );
				break;
			case R.id.main_port6:
				Log.d( TAG, "toggle port 6" );
				sendRelayToggleTask( 6 );
				break;
			case R.id.main_port7:
				Log.d( TAG, "toggle port 7" );
				sendRelayToggleTask( 7 );
				break;
			case R.id.main_port8:
				Log.d( TAG, "toggle port 8" );
				sendRelayToggleTask( 8 );
				break;
			case R.id.main_port1mask:
				Log.d( TAG, "clear mask 1" );
				sendRelayClearMaskTask( 1 );
				break;
			case R.id.main_port2mask:
				Log.d( TAG, "clear mask 2" );
				sendRelayClearMaskTask( 2 );
				break;
			case R.id.main_port3mask:
				Log.d( TAG, "clear mask 3" );
				sendRelayClearMaskTask( 3 );
				break;
			case R.id.main_port4mask:
				Log.d( TAG, "clear mask 4" );
				sendRelayClearMaskTask( 4 );
				break;
			case R.id.main_port5mask:
				Log.d( TAG, "clear mask 5" );
				sendRelayClearMaskTask( 5 );
				break;
			case R.id.main_port6mask:
				Log.d( TAG, "clear mask 6" );
				sendRelayClearMaskTask( 6 );
				break;
			case R.id.main_port7mask:
				Log.d( TAG, "clear mask 7" );
				sendRelayClearMaskTask( 7 );
				break;
			case R.id.main_port8mask:
				Log.d( TAG, "clear mask 8" );
				sendRelayClearMaskTask( 8 );
				break;
		}
	}

	private void sendRelayToggleTask ( int port ) {
		Log.d( TAG, "sendRelayToggleTask" );
		int status = Relay.PORT_STATE_OFF;
		if ( mainPortBtns[port - 1].isChecked() ) {
			status = Relay.PORT_STATE_ON;
		}
		launchRelayToggleTask( port, status );
	}

	private void sendRelayClearMaskTask ( int port ) {
		Log.d( TAG, "sendRelayClearMaskTask" );
		// hide ourself and clear the mask
		mainPortMaskBtns[port - 1].setVisibility( View.INVISIBLE );
		launchRelayToggleTask( port, Relay.PORT_STATE_AUTO );
	}

	@Override
	public boolean onKeyDown ( int keyCode, KeyEvent event ) {
		switch ( keyCode ) {
			case KeyEvent.KEYCODE_R:
				// launch the update
				Log.d( TAG, "onKeyDown R" );
				launchStatusTask();
				break;
			default:
				return super.onKeyDown( keyCode, event );
		}
		return true;
	}

	private void launchStatusTask ( ) {
		Log.d( TAG, "launchStatusTask" );
		Intent i = new Intent( MessageCommands.QUERY_STATUS_INTENT );
		sendBroadcast( i, Permissions.QUERY_STATUS );
	}

	private void launchRelayToggleTask ( int relay, int status ) {
		Log.d( TAG, "launchRelayToggleTask" );
		Intent i = new Intent( MessageCommands.TOGGLE_RELAY_INTENT );
		i.putExtra( MessageCommands.TOGGLE_RELAY_PORT_INT, relay );
		i.putExtra( MessageCommands.TOGGLE_RELAY_MODE_INT, status );
		sendBroadcast( i, Permissions.SEND_COMMAND );
	}

	public void updateDisplay ( ) {
		Log.d( TAG, "updateDisplay" );
		try {
			// TODO add in expansion relay data
			Cursor c = rapp.data.getLatestData();
			String[] values;
			short r, ron, roff;
			Relay relay = new Relay();

			if ( c.moveToFirst() ) {
				values =
						new String[] {	c.getString( c
												.getColumnIndex( RAData.PCOL_LOGDATE ) ),
										c.getString( c
												.getColumnIndex( RAData.PCOL_T1 ) ),
										c.getString( c
												.getColumnIndex( RAData.PCOL_T2 ) ),
										c.getString( c
												.getColumnIndex( RAData.PCOL_T3 ) ),
										c.getString( c
												.getColumnIndex( RAData.PCOL_PH ) ),
										c.getString( c
												.getColumnIndex( RAData.PCOL_DP ) ),
										c.getString( c
												.getColumnIndex( RAData.PCOL_AP ) ),
										c.getString( c
												.getColumnIndex( RAData.PCOL_SAL ) ) };
				r = c.getShort( c.getColumnIndex( RAData.PCOL_RDATA ) );
				ron = c.getShort( c.getColumnIndex( RAData.PCOL_RONMASK ) );
				roff = c.getShort( c.getColumnIndex( RAData.PCOL_ROFFMASK ) );
			} else {
				values = getNeverValues();
				r = ron = roff = 0;
			}
			c.close();
			loadDisplayedControllerValues( values );
			relay.setRelayData( r, ron, roff );
			updateMainRelayValues( relay );
		} catch ( SQLException e ) {
			Log.d( TAG, "SQLException: " + e.getMessage() );
		} catch ( CursorIndexOutOfBoundsException e ) {
			Log.d( TAG, "CursorIndex out of bounds: " + e.getMessage() );
		}
	}

	class StatusReceiver extends BroadcastReceiver {
		private final String TAG = StatusReceiver.class.getSimpleName();

		@Override
		public void onReceive ( Context context, Intent intent ) {
			// Log.d(TAG, "onReceive");
			String action = intent.getAction();
			if ( action.equals( MessageCommands.UPDATE_STATUS_INTENT ) ) {
				int id =
						intent.getIntExtra( MessageCommands.UPDATE_STATUS_ID,
											R.string.defaultStatusText );
				Log.d( TAG, getResources().getString( id ) );
				updateTime.setText( getResources().getString( id ) );
			} else if ( action
					.equals( MessageCommands.UPDATE_DISPLAY_DATA_INTENT ) ) {
				Log.d( TAG, "update data intent" );
				rapp.insertData( intent );
				updateDisplay();
			} else if ( action.equals( MessageCommands.ERROR_MESSAGE_INTENT ) ) {
				Log.d( TAG, intent
						.getStringExtra( MessageCommands.ERROR_MESSAGE_STRING ) );
				updateTime
						.setText( intent
								.getStringExtra( MessageCommands.ERROR_MESSAGE_STRING ) );
			}
		}
	}

	private void updateMainRelayValues ( Relay r ) {
		short status;
		String s;
		String s1;
		boolean useMask = rapp.isCommunicateController();
		for ( int i = 0; i < 8; i++ ) {
			status = r.getPortStatus( i + 1 );
			if ( status == Relay.PORT_STATE_ON ) {
				s1 = "ON";
			} else if ( status == Relay.PORT_STATE_AUTO ) {
				s1 = "AUTO";
			} else {
				s1 = "OFF";
			}
			s =
					new String( String.format( "Port %d: %s(%s)", i + 1, r
							.isPortOn( i + 1, useMask ) ? "ON" : "OFF", s1 ) );
			Log.d( TAG, s );

			mainPortBtns[i].setChecked( r.isPortOn( i + 1, useMask ) );
			if ( ((status == Relay.PORT_ON) || (status == Relay.PORT_STATE_OFF))
					&& useMask ) {
				// masked on or off, show button
				mainPortMaskBtns[i].setVisibility( View.VISIBLE );
			} else {
				mainPortMaskBtns[i].setVisibility( View.INVISIBLE );
			}
		}
	}

	private void loadDisplayedControllerValues ( String[] v ) {
		// The order must match with the order in getDisplayedControllerValues
		updateTime.setText( v[0] );
		t1Text.setText( v[1] );
		t2Text.setText( v[2] );
		t3Text.setText( v[3] );
		phText.setText( v[4] );
		dpText.setText( v[5] );
		apText.setText( v[6] );
		salinityText.setText( v[7] );
	}

	private String[] getNeverValues ( ) {
		return new String[] {	getString( R.string.messageNever ),
								getString( R.string.defaultStatusText ),
								getString( R.string.defaultStatusText ),
								getString( R.string.defaultStatusText ),
								getString( R.string.defaultStatusText ),
								getString( R.string.defaultStatusText ),
								getString( R.string.defaultStatusText ),
								getString( R.string.defaultStatusText ) };
	}

	@Override
	public boolean onCreateOptionsMenu ( Menu menu ) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate( R.menu.status_menu, menu );
		return true;
	}

	@Override
	public boolean onOptionsItemSelected ( MenuItem item ) {
		// Handle item selection
		switch ( item.getItemId() ) {
			case R.id.settings:
				// launch settings
				Log.d( TAG, "Menu Settings clicked" );
				startActivity( new Intent( this, PrefsActivity.class ) );
				break;
			case R.id.about:
				// launch about box
				Log.d( TAG, "Menu About clicked" );
				startActivity( new Intent( this, AboutActivity.class ) );
				break;
			case R.id.params:
				Log.d( TAG, "Menu Parameters clicked" );
				startActivity( new Intent( this, ParamsListActivity.class ) );
				break;
			case R.id.memory:
				// launch memory
				Log.d( TAG, "Memory clicked" );
				startActivity( new Intent( this, MemoryTabsActivity.class ) );
				break;
			case R.id.commands:
				// launch commands
				Log.d( TAG, "Commands clicked" );
				startActivity( new Intent( this, CommandTabsActivity.class ) );
				break;
			default:
				return super.onOptionsItemSelected( item );
		}
		return true;
	}
}
