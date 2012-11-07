package info.curtbinder.reefangel.phone.pages;

/*
 * Copyright (c) 2011-12 by Curt Binder (http://curtbinder.info)
 * 
 * This work is made available under the terms of the Creative Commons
 * Attribution-NonCommercial-ShareAlike 3.0 Unported License
 * http://creativecommons.org/licenses/by-nc-sa/3.0/
 */

import info.curtbinder.reefangel.controller.Controller;
import info.curtbinder.reefangel.controller.Relay;
import info.curtbinder.reefangel.phone.Permissions;
import info.curtbinder.reefangel.phone.R;
import info.curtbinder.reefangel.service.MessageCommands;
import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.ToggleButton;

public class RelayBoxPage extends ScrollView implements OnClickListener {
	private static final String TAG = RelayBoxPage.class.getSimpleName();

	Context ctx; // saved context from parent
	private int relayNumber; // 0 : Main Relay, 1 - 8 : Expansion Relay
	private TextView titleText;
	private TextView[] portLabels = new TextView[Controller.MAX_RELAY_PORTS];
	private ToggleButton[] portBtns =
			new ToggleButton[Controller.MAX_RELAY_PORTS];
	private View[] portMaskBtns = new View[Controller.MAX_RELAY_PORTS];

	public RelayBoxPage ( Context context ) {
		super( context );
		addViewsFromLayout( context );
		ctx = context;
		setDefaults();
	}

	public RelayBoxPage ( Context context, AttributeSet attrs ) {
		super( context, attrs );
		addViewsFromLayout( context );
		ctx = context;
		setDefaults();
	}

	private void setDefaults ( ) {
		setAsMainRelay();
	}

	public void setRelayBoxNumber ( int i ) {
		relayNumber = i;
	}

	public void setAsMainRelay ( ) {
		relayNumber = 0;
	}

	public void setAsExpansionRelay ( int relayNumber ) {
		this.relayNumber = relayNumber;
	}

	public int getRelayNumber ( ) {
		return relayNumber;
	}

	public boolean isMainRelay ( ) {
		return relayNumber == 0;
	}

	public boolean isExpansionRelay ( ) {
		return relayNumber > 0;
	}

	private int getBoxNumber ( ) {
		return relayNumber * 10;
	}

	private void addViewsFromLayout ( Context context ) {
		LayoutInflater layoutInflater =
				(LayoutInflater) context
						.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
		layoutInflater.inflate( R.layout.relaybox, this );
		findViews();
	}

	private void findViews ( ) {
		titleText = (TextView) findViewById( R.id.relayTitle );

		portLabels[0] = (TextView) findViewById( R.id.relayPort1Label );
		portLabels[1] = (TextView) findViewById( R.id.relayPort2Label );
		portLabels[2] = (TextView) findViewById( R.id.relayPort3Label );
		portLabels[3] = (TextView) findViewById( R.id.relayPort4Label );
		portLabels[4] = (TextView) findViewById( R.id.relayPort5Label );
		portLabels[5] = (TextView) findViewById( R.id.relayPort6Label );
		portLabels[6] = (TextView) findViewById( R.id.relayPort7Label );
		portLabels[7] = (TextView) findViewById( R.id.relayPort8Label );

		portBtns[0] = (ToggleButton) findViewById( R.id.relayPort1 );
		portBtns[1] = (ToggleButton) findViewById( R.id.relayPort2 );
		portBtns[2] = (ToggleButton) findViewById( R.id.relayPort3 );
		portBtns[3] = (ToggleButton) findViewById( R.id.relayPort4 );
		portBtns[4] = (ToggleButton) findViewById( R.id.relayPort5 );
		portBtns[5] = (ToggleButton) findViewById( R.id.relayPort6 );
		portBtns[6] = (ToggleButton) findViewById( R.id.relayPort7 );
		portBtns[7] = (ToggleButton) findViewById( R.id.relayPort8 );

		portMaskBtns[0] = findViewById( R.id.relayPort1Mask );
		portMaskBtns[1] = findViewById( R.id.relayPort2Mask );
		portMaskBtns[2] = findViewById( R.id.relayPort3Mask );
		portMaskBtns[3] = findViewById( R.id.relayPort4Mask );
		portMaskBtns[4] = findViewById( R.id.relayPort5Mask );
		portMaskBtns[5] = findViewById( R.id.relayPort6Mask );
		portMaskBtns[6] = findViewById( R.id.relayPort7Mask );
		portMaskBtns[7] = findViewById( R.id.relayPort8Mask );
	}

	public void setClickable ( boolean clickable ) {
		for ( int i = 0; i < Controller.MAX_RELAY_PORTS; i++ ) {
			portBtns[i].setClickable( false );
			portMaskBtns[i].setClickable( false );
		}
	}

	public void setOnClickListeners ( ) {
		for ( int i = 0; i < Controller.MAX_RELAY_PORTS; i++ ) {
			portBtns[i].setOnClickListener( this );
			portMaskBtns[i].setOnClickListener( this );
		}
	}

	public void setPortLabel ( int port, String label ) {
		// relay is 0 based
		// label is text to set
		Log.d( TAG, relayNumber + " Label: " + port + ", " + label );
		portLabels[port].setText( label );
	}

	public void setRelayTitle ( String s ) {
		Log.d( TAG, relayNumber + " Title: " + s );
		titleText.setText( s );
	}

	public void updateRelayValues ( Relay r, boolean fUseMask ) {
		short status;
		String s;
		String s1;
		for ( int i = 0; i < Controller.MAX_RELAY_PORTS; i++ ) {
			status = r.getPortStatus( i + 1 );
			if ( status == Relay.PORT_STATE_ON ) {
				s1 = "ON";
			} else if ( status == Relay.PORT_STATE_AUTO ) {
				s1 = "AUTO";
			} else {
				s1 = "OFF";
			}
			s =
					new String(
						String.format(	"Port %d%d: %s(%s)",
										relayNumber,
										i + 1,
										r.isPortOn( i + 1, fUseMask )	? "ON"
																		: "OFF",
										s1 ) );
			Log.d( TAG, s );

			portBtns[i].setChecked( r.isPortOn( i + 1, fUseMask ) );
			if ( ((status == Relay.PORT_ON) || (status == Relay.PORT_STATE_OFF))
					&& fUseMask ) {
				// masked on or off, show button
				portMaskBtns[i].setVisibility( View.VISIBLE );
			} else {
				portMaskBtns[i].setVisibility( View.INVISIBLE );
			}
		}
	}

	public void onClick ( View v ) {
		int box = getBoxNumber();
		// inside Log.d, the + is string concatenation
		// so relayNumber + NUM is actually like doing 1 + 1 == 11
		// however, when you get into arithmetic 1 + 1 = 2 and not 11
		switch ( v.getId() ) {
			case R.id.relayPort1:
				Log.d( TAG, "toggle port " + relayNumber + 1 );
				sendRelayToggleTask( box + 1 );
				break;
			case R.id.relayPort2:
				Log.d( TAG, "toggle port " + relayNumber + 2 );
				sendRelayToggleTask( box + 2 );
				break;
			case R.id.relayPort3:
				Log.d( TAG, "toggle port " + relayNumber + 3 );
				sendRelayToggleTask( box + 3 );
				break;
			case R.id.relayPort4:
				Log.d( TAG, "toggle port " + relayNumber + 4 );
				sendRelayToggleTask( box + 4 );
				break;
			case R.id.relayPort5:
				Log.d( TAG, "toggle port " + relayNumber + 5 );
				sendRelayToggleTask( box + 5 );
				break;
			case R.id.relayPort6:
				Log.d( TAG, "toggle port " + relayNumber + 6 );
				sendRelayToggleTask( box + 6 );
				break;
			case R.id.relayPort7:
				Log.d( TAG, "toggle port " + relayNumber + 7 );
				sendRelayToggleTask( box + 7 );
				break;
			case R.id.relayPort8:
				Log.d( TAG, "toggle port " + relayNumber + 8 );
				sendRelayToggleTask( box + 8 );
				break;
			case R.id.relayPort1Mask:
				Log.d( TAG, "clear mask " + relayNumber + 1 );
				sendRelayClearMaskTask( box + 1 );
				break;
			case R.id.relayPort2Mask:
				Log.d( TAG, "clear mask " + relayNumber + 2 );
				sendRelayClearMaskTask( box + 2 );
				break;
			case R.id.relayPort3Mask:
				Log.d( TAG, "clear mask " + relayNumber + 3 );
				sendRelayClearMaskTask( box + 3 );
				break;
			case R.id.relayPort4Mask:
				Log.d( TAG, "clear mask " + relayNumber + 4 );
				sendRelayClearMaskTask( box + 4 );
				break;
			case R.id.relayPort5Mask:
				Log.d( TAG, "clear mask " + relayNumber + 5 );
				sendRelayClearMaskTask( box + 5 );
				break;
			case R.id.relayPort6Mask:
				Log.d( TAG, "clear mask " + relayNumber + 6 );
				sendRelayClearMaskTask( box + 6 );
				break;
			case R.id.relayPort7Mask:
				Log.d( TAG, "clear mask " + relayNumber + 7 );
				sendRelayClearMaskTask( box + 7 );
				break;
			case R.id.relayPort8Mask:
				Log.d( TAG, "clear mask " + relayNumber + 8 );
				sendRelayClearMaskTask( box + 8 );
				break;
		}
	}

	private void sendRelayToggleTask ( int port ) {
		// port is 1 based
		Log.d( TAG, "sendRelayToggleTask" );
		int p = port - getBoxNumber();
		int status = Relay.PORT_STATE_OFF;
		if ( portBtns[p - 1].isChecked() ) {
			status = Relay.PORT_STATE_ON;
		}
		launchRelayToggleTask( port, status );
	}

	private void sendRelayClearMaskTask ( int port ) {
		// port is 1 based
		Log.d( TAG, "sendRelayClearMaskTask" );
		// hide ourself and clear the mask
		int p = port - getBoxNumber();
		portMaskBtns[p - 1].setVisibility( View.INVISIBLE );
		launchRelayToggleTask( port, Relay.PORT_STATE_AUTO );
	}

	private void launchRelayToggleTask ( int relay, int status ) {
		// port is 1 based
		Log.d( TAG, "launchRelayToggleTask" );
		Intent i = new Intent( MessageCommands.TOGGLE_RELAY_INTENT );
		i.putExtra( MessageCommands.TOGGLE_RELAY_PORT_INT, relay );
		i.putExtra( MessageCommands.TOGGLE_RELAY_MODE_INT, status );
		ctx.sendBroadcast( i, Permissions.SEND_COMMAND );
	}
}
