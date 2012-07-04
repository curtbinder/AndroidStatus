package info.curtbinder.reefangel.phone;

/*
 * Copyright (c) 2011-12 by Curt Binder (http://curtbinder.info)
 *
 * This work is made available under the terms of the 
 * Creative Commons Attribution-NonCommercial-ShareAlike 3.0 Unported License
 * http://creativecommons.org/licenses/by-nc-sa/3.0/
 */

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.database.SQLException;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class StatusActivity extends BaseActivity implements OnClickListener,
		OnLongClickListener {
	private static final String TAG = StatusActivity.class.getSimpleName();

	// do we reload the pages or not?
	private boolean fReloadPages = false;
	
	// Display views
	private Button refreshButton;
	private TextView updateTime;
	// private TextView messageText;
	private ViewPager pager;
	private CustomPagerAdapter pagerAdapter;
	private String[] profiles;
	// minimum number of pages: status, main relay
	private static final int MIN_PAGES = 2;
	// TODO change all these to be updated based on configuration
	private static final int POS_START = 0;

	private static final int POS_CONTROLLER = POS_START;
	private static final int POS_DIMMING = POS_CONTROLLER + 1;
	private static final int POS_RADION = POS_CONTROLLER + 2;
	private static final int POS_VORTECH = POS_CONTROLLER + 3;
	private static final int POS_AI = POS_CONTROLLER + 4;
	private static final int POS_IO = POS_CONTROLLER + 5;
	private static final int POS_CUSTOM = POS_CONTROLLER + 6;

	private static final int POS_MAIN_RELAY = POS_CONTROLLER + 7;
	private static final int POS_EXP1_RELAY = POS_MAIN_RELAY + 1;
	private static final int POS_EXP2_RELAY = POS_MAIN_RELAY + 2;
	private static final int POS_EXP3_RELAY = POS_MAIN_RELAY + 3;
	private static final int POS_EXP4_RELAY = POS_MAIN_RELAY + 4;
	private static final int POS_EXP5_RELAY = POS_MAIN_RELAY + 5;
	private static final int POS_EXP6_RELAY = POS_MAIN_RELAY + 6;
	private static final int POS_EXP7_RELAY = POS_MAIN_RELAY + 7;
	private static final int POS_EXP8_RELAY = POS_MAIN_RELAY + 8;

	private ControllerWidget controller;
	private DimmingWidget dimming;
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

		profiles = getResources().getStringArray( R.array.profileLabels );

		createViews();
		findViews();

		setPagerPrefs();

		// TODO possibly move to onresume
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
		
		// this forces all the pages to be redrawn when the app is restored
		if ( fReloadPages ) {
			Log.d( TAG, "Redraw the pages" );
			pagerAdapter.notifyDataSetChanged();
			fReloadPages = false;
		}
		
		updateViewsVisibility();
		updateDisplay();

		// TODO either put the displaying of the changelog here or in OnStart
	}

	private void createViews ( ) {
		Context ctx = rapp.getBaseContext();
		controller = new ControllerWidget( ctx );
		dimming = new DimmingWidget( ctx );
		main = new RelayBoxWidget( ctx );
		for ( int i = 0; i < Controller.MAX_EXPANSION_RELAYS; i++ ) {
			exprelays[i] = new RelayBoxWidget( ctx );
			exprelays[i].setRelayBoxNumber( i + 1 );
		}
		// TODO create additional wigdets for main screen in app
	}

	private void findViews ( ) {
		refreshButton = (Button) findViewById( R.id.refresh_button );
		updateTime = (TextView) findViewById( R.id.updated );
		pager = (ViewPager) findViewById( R.id.pager );
	}

	private void setOnClickListeners ( ) {
		refreshButton.setOnClickListener( this );
		refreshButton.setOnLongClickListener( this );
		// TODO consider clearing click listeners and updating clickable always
		int i;
		if ( rapp.isCommunicateController() ) {
			main.setOnClickListeners();

			for ( i = 0; i < Controller.MAX_EXPANSION_RELAYS; i++ )
				exprelays[i].setOnClickListeners();

		} else {
			main.setClickable( false );

			for ( i = 0; i < Controller.MAX_EXPANSION_RELAYS; i++ )
				exprelays[i].setClickable( false );

		}
	}

	private void updateViewsVisibility ( ) {
		// updates all the views visibility based on user settings
		// get values from Preferences
		// showMessageText = false;

		// Labels
		updateRefreshButtonLabel();
		String separator = getString( R.string.labelSeparator );
		controller.setT1Label( rapp.getPrefT1Label() + separator );
		controller.setT2Label( rapp.getPrefT2Label() + separator );
		controller.setT3Label( rapp.getPrefT3Label() + separator );
		controller.setPHLabel( rapp.getPrefPHLabel() + separator );
		controller.setDPLabel( rapp.getPrefDPLabel() + separator );
		controller.setAPLabel( rapp.getPrefAPLabel() + separator );
		controller.setSalinityLabel( rapp.getPrefSalinityLabel() + separator );
		controller.setORPLabel( rapp.getPrefORPLabel() + separator );

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
				if ( (j + 1) > qty )
					break;
				exprelays[j].setPortLabel( i, rapp.getPrefRelayLabel( j + 1, i )
												+ separator );
			}

		}

		// TODO set other control labels here
		if ( rapp.getDimmingModuleEnabled() ) {
			for ( i = 0; i < Controller.MAX_PWM_EXPANSION_PORTS; i++ )
				dimming.setLabel( i, rapp.getDimmingModuleChannelLabel( i )
										+ separator );
		}

		// Visibility
		controller.setT2Visibility( rapp.getPrefT2Visibility() );
		controller.setT3Visibility( rapp.getPrefT3Visibility() );
		controller.setDPVisibility( rapp.getPrefDPVisibility() );
		controller.setAPVisibility( rapp.getPrefAPVisibility() );
		controller.setPHVisibility( rapp.getPrefPHVisibility() );
		controller.setSalinityVisibility( rapp.getPrefSalinityVisibility() );
		controller.setORPVisibility( rapp.getPrefORPVisibility() );

		// TODO update control visibility here

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
	public boolean onLongClick ( View v ) {
		// if it's not a controller, don't even bother processing
		// the long clicks
		if ( !rapp.isCommunicateController() )
			return true;

		switch ( v.getId() ) {
			case R.id.refresh_button:
				// launch the profile selector
				Log.d( TAG, "onLongClick Refresh button" );
				if ( !rapp.isAwayProfileEnabled() ) {
					Log.d( TAG, "Away profile not enabled, cancelling" );
					return true;
				}
				DialogInterface.OnClickListener ocl =
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick (
									DialogInterface dialog,
									int item ) {
								switchProfiles( item );
								dialog.dismiss();
							}
						};
				AlertDialog.Builder builder = new AlertDialog.Builder( this );
				builder.setTitle( R.string.titleSelectProfile );
				builder.setSingleChoiceItems(	profiles,
												rapp.getSelectedProfile(), ocl );
				AlertDialog dlg = builder.create();
				dlg.show();
				return true;
		}
		return false;
	}

	private void switchProfiles ( int id ) {
		String s = "Switched to profile: " + profiles[id];
		Log.d( TAG, s );
		Toast.makeText( getApplicationContext(), s, Toast.LENGTH_SHORT ).show();
		s = String.format( "%d", id );
		rapp.setPref( R.string.prefProfileSelectedKey, s );
		updateRefreshButtonLabel();
	}

	private void updateRefreshButtonLabel ( ) {
		// button label will be: Refresh - PROFILE
		// only allow for the changing of the label IF it's a controller
		// AND if the away profile is enabled
		String s;
		if ( rapp.isAwayProfileEnabled() && rapp.isCommunicateController() )
			s =
					String.format(	"%s - %s",
									getString( R.string.buttonRefresh ),
									profiles[rapp.getSelectedProfile()] );
		else
			s = getString( R.string.buttonRefresh );
		refreshButton.setText( s );
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
			// TODO get all the values here to be displayed
			String updateStatus;
			String[] values;
			String[] pwme;
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
												.getColumnIndex( RAData.PCOL_SAL ) ),
										c.getString( c
												.getColumnIndex( RAData.PCOL_ORP ) ) };
				pwme =
						new String[] {	c.getString( c
												.getColumnIndex( RAData.PCOL_PWME0 ) ),
										c.getString( c
												.getColumnIndex( RAData.PCOL_PWME1 ) ),
										c.getString( c
												.getColumnIndex( RAData.PCOL_PWME2 ) ),
										c.getString( c
												.getColumnIndex( RAData.PCOL_PWME3 ) ),
										c.getString( c
												.getColumnIndex( RAData.PCOL_PWME4 ) ),
										c.getString( c
												.getColumnIndex( RAData.PCOL_PWME5 ) ) };
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
				expr[4] = c.getShort( c.getColumnIndex( RAData.PCOL_R5DATA ) );
				expron[4] =
						c.getShort( c.getColumnIndex( RAData.PCOL_R5ONMASK ) );
				exproff[4] =
						c.getShort( c.getColumnIndex( RAData.PCOL_R5OFFMASK ) );
				expr[5] = c.getShort( c.getColumnIndex( RAData.PCOL_R6DATA ) );
				expron[5] =
						c.getShort( c.getColumnIndex( RAData.PCOL_R6ONMASK ) );
				exproff[5] =
						c.getShort( c.getColumnIndex( RAData.PCOL_R6OFFMASK ) );
				expr[6] = c.getShort( c.getColumnIndex( RAData.PCOL_R7DATA ) );
				expron[6] =
						c.getShort( c.getColumnIndex( RAData.PCOL_R7ONMASK ) );
				exproff[6] =
						c.getShort( c.getColumnIndex( RAData.PCOL_R7OFFMASK ) );
				expr[7] = c.getShort( c.getColumnIndex( RAData.PCOL_R8DATA ) );
				expron[7] =
						c.getShort( c.getColumnIndex( RAData.PCOL_R8ONMASK ) );
				exproff[7] =
						c.getShort( c.getColumnIndex( RAData.PCOL_R8OFFMASK ) );
			} else {
				updateStatus = getString( R.string.messageNever );
				values = getNeverValues( 8 );
				pwme = getNeverValues( Controller.MAX_PWM_EXPANSION_PORTS );
				r = ron = roff = 0;
				for ( int i = 0; i < Controller.MAX_EXPANSION_RELAYS; i++ ) {
					expr[i] = expron[i] = exproff[i] = 0;
				}
			}
			c.close();
			updateTime.setText( updateStatus );
			controller.updateDisplay( values );
			dimming.updateDisplay( pwme );
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
				// TODO have insert be done by the task and only updateDisplay
				// here
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

	private String[] getNeverValues ( int qty ) {
		String[] s = new String[qty];
		for ( int i = 0; i < qty; i++ ) {
			s[i] = getString( R.string.defaultStatusText );
		}
		return s;
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
				Log.d( TAG, "Settings clicked" );
				// scroll to first page on entering settings
				pager.setCurrentItem( POS_CONTROLLER );
				// force the pages to be redrawn if we enter settings
				fReloadPages = true;
				startActivity( new Intent( this, PrefsActivity.class ) );
				break;
			case R.id.about:
				// launch about box
				Log.d( TAG, "About clicked" );
				startActivity( new Intent( this, AboutActivity.class ) );
				break;
			case R.id.params:
				Log.d( TAG, "Parameters clicked" );
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

	private void setPagerPrefs ( ) {
		Log.d( TAG, "Create pager adapter" );
		pagerAdapter = new CustomPagerAdapter();
		pager.setAdapter( pagerAdapter );
		// Set the minimum pages to keep loaded
		// will set to minimum pages since the pages are not complex
		pager.setOffscreenPageLimit( MIN_PAGES );
	}

	private class CustomPagerAdapter extends PagerAdapter {
		private final String TAG = CustomPagerAdapter.class.getSimpleName();

		@Override
		public int getCount ( ) {
			int qty = MIN_PAGES + rapp.getTotalInstalledModuleQuantity();
			return qty;
		}

		@Override
		public void destroyItem (
				ViewGroup container,
				int position,
				Object object ) {
			Log.d( TAG, "destroyItem " + position );
			((ViewPager) container).removeView( (View) object );
		}

		@Override
		public Object instantiateItem ( ViewGroup container, int position ) {
			View v;
			int p = position;
			int qty = rapp.getInstalledModuleQuantity();
			if ( qty == 0 ) {
				Log.d( TAG, "No installed modules, skipping to main relay" );
				p += POS_CUSTOM;
			} else if ( (p > qty) && (p < POS_MAIN_RELAY) ) {
				// if it's between the last installed module AND the
				// main relay, jump to main relay
				Log.d( TAG, "Between last module and main relay, skip to main" );
				p = POS_MAIN_RELAY;
			}
			switch ( p ) {
				default:
				case POS_CONTROLLER: // Controller Status
					Log.d( TAG, "Create controller" );
					v = controller;
					break;
				case POS_DIMMING: // Dimming
					Log.d( TAG, "Create dimming" );
					v = dimming;
					break;
				case POS_RADION:
					v = controller;
					break;
				case POS_VORTECH:
					v = controller;
					break;
				case POS_AI:
					v = controller;
					break;
				case POS_IO:
					v = controller;
					break;
				case POS_CUSTOM:
					v = controller;
					break;
				case POS_MAIN_RELAY: // Main Relay
					Log.d( TAG, "Create main relay" );
					v = main;
					break;
				case POS_EXP1_RELAY: // Expansion Relay 1
				case POS_EXP2_RELAY: // Expansion Relay 2
				case POS_EXP3_RELAY: // Expansion Relay 3
				case POS_EXP4_RELAY: // Expansion Relay 4
				case POS_EXP5_RELAY: // Expansion Relay 5
				case POS_EXP6_RELAY: // Expansion Relay 6
				case POS_EXP7_RELAY: // Expansion Relay 7
				case POS_EXP8_RELAY: // Expansion Relay 8
					int relay = p - POS_EXP1_RELAY;
					Log.d( TAG, "Create exp relay " + relay + " (" + p + ")" );
					v = exprelays[relay];
					break;
			}
			((ViewPager) container).addView( v );
			return v;
		}

		@Override
		public boolean isViewFromObject ( View view, Object object ) {
			return view == object;
		}

		@Override
		public int getItemPosition ( Object object ) {
			return POSITION_NONE;
		}

	}

}
