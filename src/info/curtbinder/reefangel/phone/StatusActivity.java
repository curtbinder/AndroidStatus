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

public class StatusActivity extends BaseActivity implements OnClickListener {
	private static final String TAG = StatusActivity.class.getSimpleName();

	// Display views
	private View refreshButton;
	private TextView updateTime;
	// private TextView messageText;
	private ControllerWidget controller;

	private RelayBoxWidget main;
	private RelayBoxWidget[] exprelays =
			new RelayBoxWidget[Controller.MAX_EXPANSION_RELAYS];

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
		controller = (ControllerWidget) findViewById( R.id.controller );

		main = (RelayBoxWidget) findViewById( R.id.mainrelay );
		exprelays[0] = (RelayBoxWidget) findViewById( R.id.exprelay1 );
		exprelays[0].setRelayBoxNumber( 1 );
		exprelays[1] = (RelayBoxWidget) findViewById( R.id.exprelay2 );
		exprelays[1].setRelayBoxNumber( 2 );
		exprelays[2] = (RelayBoxWidget) findViewById( R.id.exprelay3 );
		exprelays[2].setRelayBoxNumber( 3 );
		exprelays[3] = (RelayBoxWidget) findViewById( R.id.exprelay4 );
		exprelays[3].setRelayBoxNumber( 4 );
		exprelays[4] = (RelayBoxWidget) findViewById( R.id.exprelay5 );
		exprelays[4].setRelayBoxNumber( 5 );
		exprelays[5] = (RelayBoxWidget) findViewById( R.id.exprelay6 );
		exprelays[5].setRelayBoxNumber( 6 );
		exprelays[6] = (RelayBoxWidget) findViewById( R.id.exprelay7 );
		exprelays[6].setRelayBoxNumber( 7 );
		exprelays[7] = (RelayBoxWidget) findViewById( R.id.exprelay8 );
		exprelays[7].setRelayBoxNumber( 8 );
	}

	private void setOnClickListeners ( ) {
		refreshButton.setOnClickListener( this );
		if ( rapp.isCommunicateController() ) {
			main.setOnClickListeners();
			for ( int i = 0; i < Controller.MAX_EXPANSION_RELAYS; i++ )
				exprelays[i].setOnClickListeners();
		} else {
			main.setClickable( false );
			for ( int i = 0; i < Controller.MAX_EXPANSION_RELAYS; i++ )
				exprelays[i].setClickable( false );
		}
	}

	private void updateViewsVisibility ( ) {
		// updates all the views visibility based on user settings
		// get values from Preferences
		// showMessageText = false;

		// Labels
		String separator = getString( R.string.labelSeparator );
		controller.setT1Label( rapp.getPrefT1Label() + separator );
		controller.setT2Label( rapp.getPrefT2Label() + separator );
		controller.setT3Label( rapp.getPrefT3Label() + separator );
		controller.setPHLabel( rapp.getPrefPHLabel() + separator );
		controller.setDPLabel( rapp.getPrefDPLabel() + separator );
		controller.setAPLabel( rapp.getPrefAPLabel() + separator );
		controller.setSalinityLabel( rapp.getPrefSalinityLabel() + separator );
		
		int qty = rapp.getPrefExpansionRelayQuantity();
		Log.d( TAG, "Expansion Relays: " + qty );
		main.setRelayTitle( getString( R.string.prefMainRelayTitle ) );
		// set the labels
		switch ( qty ) {
			case 8:
				exprelays[7]
						.setRelayTitle( getString( R.string.prefExp8RelayTitle ) );
			case 7:
				exprelays[6]
						.setRelayTitle( getString( R.string.prefExp7RelayTitle ) );
			case 6:
				exprelays[5]
						.setRelayTitle( getString( R.string.prefExp6RelayTitle ) );
			case 5:
				exprelays[4]
						.setRelayTitle( getString( R.string.prefExp5RelayTitle ) );
			case 4:
				exprelays[3]
						.setRelayTitle( getString( R.string.prefExp4RelayTitle ) );
			case 3:
				exprelays[2]
						.setRelayTitle( getString( R.string.prefExp3RelayTitle ) );
			case 2:
				exprelays[1]
						.setRelayTitle( getString( R.string.prefExp2RelayTitle ) );
			case 1:
				exprelays[0]
						.setRelayTitle( getString( R.string.prefExp1RelayTitle ) );
			default:
				break;
		}

		int i, j;
		for ( i = 0; i < Controller.MAX_RELAY_PORTS; i++ ) {
			main.setPortLabel( i, rapp.getPrefMainRelayLabel( i ) + separator );
			for ( j = 0; j < Controller.MAX_EXPANSION_RELAYS; j++ ) {
				// skip over the relays that are not installed
				if ( (j+1) > qty )
					break;
				exprelays[j].setPortLabel( i, rapp.getPrefRelayLabel( j + 1, i )
												+ separator );
			}
		}
		// show/hide the relays
		int iVisible;
		for ( i = 1; i <= Controller.MAX_EXPANSION_RELAYS; i++ ) {
			if ( i > rapp.getPrefExpansionRelayQuantity() ) {
				Log.d( TAG, "Relay " + i + " gone" );
				iVisible = View.GONE;
			} else {
				Log.d( TAG, "Relay " + i + " visible" );
				iVisible = View.VISIBLE;
			}
			exprelays[i - 1].setVisibility( iVisible );
		}

		// Visibility
		controller.setT2Visibility( rapp.getPrefT2Visibility() );
		controller.setT3Visibility( rapp.getPrefT3Visibility() );
		controller.setDPVisibility( rapp.getPrefDPVisibility() );
		controller.setAPVisibility( rapp.getPrefAPVisibility() );
		controller.setSalinityVisibility( rapp.getPrefSalinityVisibility() );

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
		}
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

	public void updateDisplay ( ) {
		Log.d( TAG, "updateDisplay" );
		try {
			Cursor c = rapp.data.getLatestData();
			String updateStatus;
			String[] values;
			short r, ron, roff;
			short[] expr = new short[Controller.MAX_EXPANSION_RELAYS];
			short[] expron = new short[Controller.MAX_EXPANSION_RELAYS];
			short[] exproff = new short[Controller.MAX_EXPANSION_RELAYS];

			if ( c.moveToFirst() ) {
				updateStatus =
						c.getString( c.getColumnIndex( RAData.PCOL_LOGDATE ) );
				values =
						new String[] {	c.getString( c
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

				expr[0] = c.getShort( c.getColumnIndex( RAData.PCOL_R1DATA ) );
				expron[0] =
						c.getShort( c.getColumnIndex( RAData.PCOL_R1ONMASK ) );
				exproff[0] =
						c.getShort( c.getColumnIndex( RAData.PCOL_R1OFFMASK ) );
				expr[1] = c.getShort( c.getColumnIndex( RAData.PCOL_R2DATA ) );
				expron[1] =
						c.getShort( c.getColumnIndex( RAData.PCOL_R2ONMASK ) );
				exproff[1] =
						c.getShort( c.getColumnIndex( RAData.PCOL_R2OFFMASK ) );
				expr[2] = c.getShort( c.getColumnIndex( RAData.PCOL_R3DATA ) );
				expron[2] =
						c.getShort( c.getColumnIndex( RAData.PCOL_R3ONMASK ) );
				exproff[2] =
						c.getShort( c.getColumnIndex( RAData.PCOL_R3OFFMASK ) );
				expr[3] = c.getShort( c.getColumnIndex( RAData.PCOL_R4DATA ) );
				expron[3] =
						c.getShort( c.getColumnIndex( RAData.PCOL_R4ONMASK ) );
				exproff[3] =
						c.getShort( c.getColumnIndex( RAData.PCOL_R4OFFMASK ) );
				expr[4] = c.getShort( c.getColumnIndex( RAData.PCOL_R1DATA ) );
				expron[4] =
						c.getShort( c.getColumnIndex( RAData.PCOL_R1ONMASK ) );
				exproff[4] =
						c.getShort( c.getColumnIndex( RAData.PCOL_R1OFFMASK ) );
				expr[5] = c.getShort( c.getColumnIndex( RAData.PCOL_R2DATA ) );
				expron[5] =
						c.getShort( c.getColumnIndex( RAData.PCOL_R2ONMASK ) );
				exproff[5] =
						c.getShort( c.getColumnIndex( RAData.PCOL_R2OFFMASK ) );
				expr[6] = c.getShort( c.getColumnIndex( RAData.PCOL_R3DATA ) );
				expron[6] =
						c.getShort( c.getColumnIndex( RAData.PCOL_R3ONMASK ) );
				exproff[6] =
						c.getShort( c.getColumnIndex( RAData.PCOL_R3OFFMASK ) );
				expr[7] = c.getShort( c.getColumnIndex( RAData.PCOL_R4DATA ) );
				expron[7] =
						c.getShort( c.getColumnIndex( RAData.PCOL_R4ONMASK ) );
				exproff[7] =
						c.getShort( c.getColumnIndex( RAData.PCOL_R4OFFMASK ) );
			} else {
				updateStatus = getString( R.string.messageNever );
				values = getNeverValues();
				r = ron = roff = 0;
				for ( int i = 0; i < Controller.MAX_EXPANSION_RELAYS; i++ ) {
					expr[i] = expron[i] = exproff[i] = 0;
				}
			}
			c.close();
			updateTime.setText( updateStatus );
			controller.updateDisplay( values );
			boolean fUseMask = rapp.isCommunicateController();
			main.updateRelayValues( new Relay( r, ron, roff ), fUseMask );
			for ( int i = 0; i < rapp.getPrefExpansionRelayQuantity(); i++ ) {
				exprelays[i].updateRelayValues( new Relay( expr[i], expron[i],
					exproff[i] ), fUseMask );
			}
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

	private String[] getNeverValues ( ) {
		return new String[] {	getString( R.string.defaultStatusText ),
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
