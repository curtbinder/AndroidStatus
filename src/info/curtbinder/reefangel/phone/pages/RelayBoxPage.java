package info.curtbinder.reefangel.phone.pages;

/*
 * Copyright (c) 2011-13 by Curt Binder (http://curtbinder.info)
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
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.ToggleButton;

public class RelayBoxPage extends ScrollView implements OnClickListener {
	private static final String TAG = RelayBoxPage.class.getSimpleName();

	Context ctx; // saved context from parent
	private int relayNumber; // 0 : Main Relay, 1 - 8 : Expansion Relay
	private TextView titleText;
	private ToggleButton[] portBtns =
			new ToggleButton[Controller.MAX_RELAY_PORTS];
	private View[] portMaskBtns = new View[Controller.MAX_RELAY_PORTS];

	private boolean[] controlsEnabled = new boolean[Controller.MAX_RELAY_PORTS];
	
	public RelayBoxPage ( Context context ) {
		super( context );
		ctx = context;
		addViewsFromLayout( context );
		setDefaults();
	}

	public RelayBoxPage ( Context context, AttributeSet attrs ) {
		super( context, attrs );
		ctx = context;
		addViewsFromLayout( context );
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
		TableRow tr;
		tr = (TableRow)findViewById(R.id.rowPort1);
		portBtns[0] = (ToggleButton)tr.findViewById( R.id.rowToggle );
		portMaskBtns[0] = tr.findViewById( R.id.rowOverrideToggle );
		tr = (TableRow)findViewById(R.id.rowPort2);
		portBtns[1] = (ToggleButton)tr.findViewById( R.id.rowToggle );
		portMaskBtns[1] = tr.findViewById( R.id.rowOverrideToggle );
		tr = (TableRow)findViewById(R.id.rowPort3);
		portBtns[2] = (ToggleButton)tr.findViewById( R.id.rowToggle );
		portMaskBtns[2] = tr.findViewById( R.id.rowOverrideToggle );
		tr = (TableRow)findViewById(R.id.rowPort4);
		portBtns[3] = (ToggleButton)tr.findViewById( R.id.rowToggle );
		portMaskBtns[3] = tr.findViewById( R.id.rowOverrideToggle );
		tr = (TableRow)findViewById(R.id.rowPort5);
		portBtns[4] = (ToggleButton)tr.findViewById( R.id.rowToggle );
		portMaskBtns[4] = tr.findViewById( R.id.rowOverrideToggle );
		tr = (TableRow)findViewById(R.id.rowPort6);
		portBtns[5] = (ToggleButton)tr.findViewById( R.id.rowToggle );
		portMaskBtns[5] = tr.findViewById( R.id.rowOverrideToggle );
		tr = (TableRow)findViewById(R.id.rowPort7);
		portBtns[6] = (ToggleButton)tr.findViewById( R.id.rowToggle );
		portMaskBtns[6] = tr.findViewById( R.id.rowOverrideToggle );
		tr = (TableRow)findViewById(R.id.rowPort8);
		portBtns[7] = (ToggleButton)tr.findViewById( R.id.rowToggle );
		portMaskBtns[7] = tr.findViewById( R.id.rowOverrideToggle );
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

	public void setPortLabel ( int port, String title, String subtitle ) {
		// relay is 0 based
		// label is text to set
		Log.d( TAG, relayNumber + " Label: " + port + ", " + title );
		int id;
		switch ( port ) {
			default:
			case 0:
				id = R.id.rowPort1;
				break;
			case 1:
				id = R.id.rowPort2;
				break;
			case 2:
				id = R.id.rowPort3;
				break;
			case 3:
				id = R.id.rowPort4;
				break;
			case 4:
				id = R.id.rowPort5;
				break;
			case 5:
				id = R.id.rowPort6;
				break;
			case 6:
				id = R.id.rowPort7;
				break;
			case 7:
				id = R.id.rowPort8;
				break;
		}
		TableRow tr;
		tr = (TableRow)findViewById(id);
		((TextView)tr.findViewById( R.id.rowTitle )).setText( title );
		((TextView)tr.findViewById( R.id.rowSubTitle )).setText( subtitle);
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
		
		// The buttons are nested inside a LinearLayout and then inside a TableRow
		// The TableRow is the View that contains the row id
		int port = 1;
		View parent = (View) v.getParent().getParent();
		switch ( parent.getId() ) {
			default:
			case R.id.rowPort1:
				port = 1;
				break;
			case R.id.rowPort2:
				port = 2;
				break;
			case R.id.rowPort3:
				port = 3;
				break;
			case R.id.rowPort4:
				port = 4;
				break;
			case R.id.rowPort5:
				port = 5;
				break;
			case R.id.rowPort6:
				port = 6;
				break;
			case R.id.rowPort7:
				port = 7;
				break;
			case R.id.rowPort8:
				port = 8;
				break;
		}
		if ( v.getId() == R.id.rowOverrideToggle ) {
			Log.d( TAG, "clear mask " + relayNumber + port );
			sendRelayClearMaskTask( box + port );
		} else if ( v.getId() == R.id.rowToggle ) {
			Log.d( TAG, "toggle port " + relayNumber + port );
			sendRelayToggleTask( box + port );
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

	public void refreshButtonEnablement() {
		for ( int i = 0; i < Controller.MAX_RELAY_PORTS; i++ ) {
			boolean enabled = isControlEnabled(i);
			
			portBtns[i].setEnabled(enabled);
			portMaskBtns[i].setClickable(enabled);
		}
	}
	
	public void setControlEnabled ( int port, boolean enabled ) {
		Log.d( TAG, relayNumber + " Enable: " + port + ", " + enabled );
		controlsEnabled[port] = enabled;
		refreshButtonEnablement();
	}

	private boolean isControlEnabled(int port) {
		return controlsEnabled[port];
	}
}
