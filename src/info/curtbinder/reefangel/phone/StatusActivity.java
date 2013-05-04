/*
 * Copyright (c) 2011-13 by Curt Binder (http://curtbinder.info)
 *
 * This work is made available under the terms of the 
 * Creative Commons Attribution-NonCommercial-ShareAlike 3.0 Unported License
 * http://creativecommons.org/licenses/by-nc-sa/3.0/
 */

package info.curtbinder.reefangel.phone;

import info.curtbinder.reefangel.controller.Controller;
import info.curtbinder.reefangel.controller.Relay;
import info.curtbinder.reefangel.db.RAData;
import info.curtbinder.reefangel.phone.pages.AIPage;
import info.curtbinder.reefangel.phone.pages.CommandsPage;
import info.curtbinder.reefangel.phone.pages.ControllerPage;
import info.curtbinder.reefangel.phone.pages.CustomPage;
import info.curtbinder.reefangel.phone.pages.DimmingPage;
import info.curtbinder.reefangel.phone.pages.IOPage;
import info.curtbinder.reefangel.phone.pages.RAPage;
import info.curtbinder.reefangel.phone.pages.RadionPage;
import info.curtbinder.reefangel.phone.pages.RelayBoxPage;
import info.curtbinder.reefangel.phone.pages.VortechPage;
import info.curtbinder.reefangel.service.MessageCommands;
import info.curtbinder.reefangel.service.XMLTags;

import java.util.Locale;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.database.SQLException;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.viewpagerindicator.TitlePageIndicator;

public class StatusActivity extends BaseActivity implements ActionBar.OnNavigationListener {
	private static final String TAG = StatusActivity.class.getSimpleName();
	
	// do we reload the pages or not?
	private boolean fReloadPages = false;

	// Display views
	private TextView updateTime;
	private ViewPager pager;
	private CustomPagerAdapter pagerAdapter;
	private TitlePageIndicator titleIndicator;
	private String[] vortechModes;
	private View[] appPages;
	// minimum number of pages: status, main relay
	private static final int MIN_PAGES = 3;
	// TODO change all these to be updated based on configuration
	private static final int POS_START = 0;

	private static final int POS_COMMANDS = POS_START;
	private static final int POS_CONTROLLER = POS_START + 1;

	private static final int POS_MODULES = POS_CONTROLLER + 10;
	private static final int POS_DIMMING = POS_MODULES;
	private static final int POS_RADION = POS_MODULES + 1;
	private static final int POS_VORTECH = POS_MODULES + 2;
	private static final int POS_AI = POS_MODULES + 3;
	private static final int POS_IO = POS_MODULES + 4;
	private static final int POS_CUSTOM = POS_MODULES + 5;

	private static final int POS_MAIN_RELAY = POS_CONTROLLER + 1;
	private static final int POS_EXP1_RELAY = POS_MAIN_RELAY + 1;
	private static final int POS_EXP2_RELAY = POS_MAIN_RELAY + 2;
	private static final int POS_EXP3_RELAY = POS_MAIN_RELAY + 3;
	private static final int POS_EXP4_RELAY = POS_MAIN_RELAY + 4;
	private static final int POS_EXP5_RELAY = POS_MAIN_RELAY + 5;
	private static final int POS_EXP6_RELAY = POS_MAIN_RELAY + 6;
	private static final int POS_EXP7_RELAY = POS_MAIN_RELAY + 7;
	private static final int POS_EXP8_RELAY = POS_MAIN_RELAY + 8;

	private static final int POS_END = POS_CUSTOM + 1;

	private CommandsPage pageCommands;
	private ControllerPage pageController;
	private DimmingPage pageDimming;
	private RadionPage pageRadion;
	private VortechPage pageVortech;
	private AIPage pageAI;
	private IOPage pageIO;
	private CustomPage pageCustom;
	private RelayBoxPage pageMain;
	private RelayBoxPage[] pageExpRelays =
			new RelayBoxPage[Controller.MAX_EXPANSION_RELAYS];

	// Message Receivers
	StatusReceiver receiver;
	IntentFilter filter;

	/** Called when the activity is first created. */

	public void onCreate ( Bundle savedInstanceState ) {
		super.onCreate( savedInstanceState );
		setContentView( R.layout.status );

		// Message Receiver stuff
		receiver = new StatusReceiver();
		filter = new IntentFilter( MessageCommands.UPDATE_DISPLAY_DATA_INTENT );
		filter.addAction( MessageCommands.UPDATE_STATUS_INTENT );
		filter.addAction( MessageCommands.ERROR_MESSAGE_INTENT );
		filter.addAction( MessageCommands.VORTECH_UPDATE_INTENT );
		filter.addAction( MessageCommands.MEMORY_RESPONSE_INTENT );
		filter.addAction( MessageCommands.COMMAND_RESPONSE_INTENT );

		vortechModes =
				getResources().getStringArray( R.array.vortechModeLabels );

		createViews();
		findViews();
		
		// update actionbar 
		final ActionBar ab = getSupportActionBar();
		ab.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
		ab.setDisplayShowTitleEnabled(false);

		// set the max number of pages that we can have
		appPages = new View[POS_END];
		updatePageOrder();
		setPagerPrefs();

		// TODO possibly move to onresume
		disableRelayButtons();
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
        
		// Scroll to controller page
		pager.setCurrentItem( POS_CONTROLLER );
	}

	protected void onPause ( ) {
		super.onPause();
		unregisterReceiver( receiver );
	}

	protected void onResume ( ) {
		super.onResume();
		registerReceiver( receiver, filter, Permissions.QUERY_STATUS, null );
		registerReceiver( receiver, filter, Permissions.SEND_COMMAND, null );

		setNavigationList();
		
		// this forces all the pages to be redrawn when the app is restored
		if ( fReloadPages ) {
			redrawPages();
		}
		
		updateViewsVisibility();
		updateDisplay();

		// the last thing we do is display the changelog if necessary
		rapp.displayChangeLog( this );
	}
	
	private void setNavigationList ( ) {
		// set list navigation items
		final ActionBar ab = getSupportActionBar();
        Context context = ab.getThemedContext();
        int arrayID;
        if ( rapp.isAwayProfileEnabled() ) {
        	arrayID = R.array.profileLabels;
        } else {
        	arrayID = R.array.profileLabelsHomeOnly;
        }
        ArrayAdapter<CharSequence> list = 
        		ArrayAdapter.createFromResource(context, arrayID, 
        		                                R.layout.sherlock_spinner_item); 
        list.setDropDownViewResource(R.layout.sherlock_spinner_dropdown_item);
        ab.setListNavigationCallbacks(list, this);
        ab.setSelectedNavigationItem( rapp.getSelectedProfile() );
	}

	private void createViews ( ) {
		Context ctx = rapp.getBaseContext();
		pageCommands = new CommandsPage( ctx );
		pageController = new ControllerPage( ctx );
		pageDimming = new DimmingPage( ctx );
		pageRadion = new RadionPage( ctx );
		pageVortech = new VortechPage( ctx );
		pageAI = new AIPage( ctx );
		pageIO = new IOPage( ctx );
		pageCustom = new CustomPage( ctx );
		pageMain = new RelayBoxPage( ctx );
		for ( int i = 0; i < Controller.MAX_EXPANSION_RELAYS; i++ ) {
			pageExpRelays[i] = new RelayBoxPage( ctx );
			pageExpRelays[i].setRelayBoxNumber( i + 1 );
		}
	}

	private void findViews ( ) {
		updateTime = (TextView) findViewById( R.id.updated );
		pager = (ViewPager) findViewById( R.id.pager );
		titleIndicator = (TitlePageIndicator) findViewById( R.id.indicator );
	}

	private void disableRelayButtons ( ) {
		pageMain.refreshButtonEnablement();

		for ( int i = 0; i < Controller.MAX_EXPANSION_RELAYS; i++ ) {
			pageExpRelays[i].refreshButtonEnablement();
		}
	}

	private void setOnClickListeners ( ) {
//		refreshButton.setOnClickListener( this );
//		refreshButton.setOnLongClickListener( this );
		// TODO consider clearing click listeners and updating clickable always
		int i;
		if ( rapp.isCommunicateController() ) {
			pageMain.setOnClickListeners();

			for ( i = 0; i < Controller.MAX_EXPANSION_RELAYS; i++ )
				pageExpRelays[i].setOnClickListeners();

		} else {
			pageMain.setClickable( false );

			for ( i = 0; i < Controller.MAX_EXPANSION_RELAYS; i++ )
				pageExpRelays[i].setClickable( false );

		}
	}

	private void updateViewsVisibility ( ) {
		// updates all the views visibility based on user settings
		// get values from Preferences

		// TODO split into multiple functions
		// Labels
		//updateRefreshButtonLabel();
		setControllerLabels();

		setRelayLabels( );

		if ( rapp.getDimmingModuleEnabled() ) {
			for ( int i = 0; i < Controller.MAX_PWM_EXPANSION_PORTS; i++ )
				pageDimming
						.setLabel( i, rapp.getDimmingModuleChannelLabel( i ) );
		}

		if ( rapp.getIOModuleEnabled() ) {
			for ( int i = 0; i < Controller.MAX_IO_CHANNELS; i++ ) {
				pageIO.setLabel( i, rapp.getIOModuleChannelLabel( i ) );
			}
		}

		if ( rapp.getCustomModuleEnabled() ) {
			for ( int i = 0; i < Controller.MAX_CUSTOM_VARIABLES; i++ )
				pageCustom.setLabel( i, rapp.getCustomModuleChannelLabel( i ) );
		}

		setControllerVisibility();
		// TODO update control visibility here
		// TODO consider hiding dimming channels not in use
		// TODO consider hiding custom variables not in use
		// TODO consider hiding io channels not in use
	}
	
	private void setControllerLabels ( ) {
		pageController.setLabel(	ControllerPage.T1_INDEX,
									rapp.getPrefT1Label(),
									getString( R.string.labelTemp1 ) );
		pageController.setLabel(	ControllerPage.T2_INDEX,
									rapp.getPrefT2Label(),
									getString( R.string.labelTemp2 ) );
		pageController.setLabel(	ControllerPage.T3_INDEX,
									rapp.getPrefT3Label(),
									getString( R.string.labelTemp3 ) );
		pageController.setLabel(	ControllerPage.PH_INDEX,
									rapp.getPrefPHLabel(),
									getString( R.string.labelPH ) );
		pageController.setLabel(	ControllerPage.DP_INDEX,
									rapp.getPrefDPLabel(),
									getString( R.string.labelDP ) );
		pageController.setLabel(	ControllerPage.AP_INDEX,
									rapp.getPrefAPLabel(),
									getString( R.string.labelAP ) );
		pageController.setLabel(	ControllerPage.SALINITY_INDEX,
									rapp.getPrefSalinityLabel(),
									getString( R.string.labelSalinity ) );
		pageController.setLabel(	ControllerPage.ORP_INDEX,
									rapp.getPrefORPLabel(),
									getString( R.string.labelORP ) );
		pageController.setLabel(	ControllerPage.PHE_INDEX,
									rapp.getPrefPHExpLabel(),
									getString( R.string.labelPHExp ) );
		pageController.setLabel(	ControllerPage.WL_INDEX,
									rapp.getPrefWaterLevelLabel(),
									getString( R.string.labelWaterLevel ) );
	}
	
	private void setRelayLabels ( ) {
		int qty = rapp.getPrefExpansionRelayQuantity();
		
		String defaultPort = getString( R.string.defaultPortName );
		for ( int i = 0; i < Controller.MAX_RELAY_PORTS; i++ ) {
			pageMain.setPortLabel(	i, rapp.getPrefMainRelayLabel( i ),
									defaultPort + (i + 1) );
			boolean enabled = rapp.getPrefMainRelayControlEnabled( i );
			pageMain.setControlEnabled( i, enabled );

			for ( int j = 0; j < Controller.MAX_EXPANSION_RELAYS; j++ ) {
				// skip over the relays that are not installed
				if ( (j + 1) > qty )
					break;
				pageExpRelays[j].setPortLabel( i, rapp
						.getPrefRelayLabel( j + 1, i ), defaultPort + (i + 1) );
				pageExpRelays[j].setControlEnabled( i, rapp
						.getPrefRelayControlEnabled( j + 1, i ) );
			}

		}
	}
	
	private void setControllerVisibility ( ) {
		// Visibility
		pageController.setVisibility(	ControllerPage.T2_INDEX,
										rapp.getPrefT2Visibility() );
		pageController.setVisibility(	ControllerPage.T3_INDEX,
										rapp.getPrefT3Visibility() );
		pageController.setVisibility(	ControllerPage.DP_INDEX,
										rapp.getPrefDPVisibility() );
		pageController.setVisibility(	ControllerPage.AP_INDEX,
										rapp.getPrefAPVisibility() );
		pageController.setVisibility(	ControllerPage.PH_INDEX,
										rapp.getPrefPHVisibility() );
		pageController.setVisibility(	ControllerPage.SALINITY_INDEX,
										rapp.getPrefSalinityVisibility() );
		pageController.setVisibility(	ControllerPage.ORP_INDEX,
										rapp.getPrefORPVisibility() );
		pageController.setVisibility(	ControllerPage.PHE_INDEX,
										rapp.getPrefPHExpVisibility() );
		pageController.setVisibility(	ControllerPage.WL_INDEX,
										rapp.getPrefWaterLevelVisibility() );
	}
	
	@Override
	public boolean onNavigationItemSelected ( int itemPosition, long itemId ) {
		switchProfiles(itemPosition);
		return true;
	}
	
	private void switchProfiles ( int id ) {
		rapp.setSelectedProfile( id );
	}

	private void launchStatusTask ( ) {
		Intent i = new Intent( MessageCommands.QUERY_STATUS_INTENT );
		sendBroadcast( i, Permissions.QUERY_STATUS );
	}

	public void updateDisplay ( ) {
		try {
			Cursor c = rapp.data.getLatestData();
			String updateStatus;
			String[] values;
			String[] pwme;
			String[] rf;
			String[] vt;
			String[] ai;
			String[] io;
			String[] custom;
			short r, ron, roff, newEM, newREM;
			short[] expr = new short[Controller.MAX_EXPANSION_RELAYS];
			short[] expron = new short[Controller.MAX_EXPANSION_RELAYS];
			short[] exproff = new short[Controller.MAX_EXPANSION_RELAYS];

			if ( c.moveToFirst() ) {
				updateStatus =
						c.getString( c.getColumnIndex( RAData.PCOL_LOGDATE ) );
				values = getControllerValues( c );
				pwme = getPWMEValues( c );
				rf = getRadionValues( c );
				vt = getVortechValues( c );
				ai = getAIValues( c );
				io = getIOValues( c );
				custom = getCustomValues( c );
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
				newEM = c.getShort( c.getColumnIndex( RAData.PCOL_EM ) );
				newREM = c.getShort( c.getColumnIndex( RAData.PCOL_REM ) );
			} else {
				updateStatus = getString( R.string.messageNever );
				values = getNeverValues( Controller.MAX_CONTROLLER_VALUES );
				pwme = getNeverValues( Controller.MAX_PWM_EXPANSION_PORTS );
				rf = getNeverValues( Controller.MAX_RADION_LIGHT_CHANNELS );
				vt = getNeverValues( Controller.MAX_VORTECH_VALUES );
				ai = getNeverValues( Controller.MAX_AI_CHANNELS );
				io = getNeverValues( Controller.MAX_IO_CHANNELS );
				custom = getNeverValues( Controller.MAX_CUSTOM_VARIABLES );
				r = ron = roff = newEM = newREM = 0;
				for ( int i = 0; i < Controller.MAX_EXPANSION_RELAYS; i++ ) {
					expr[i] = expron[i] = exproff[i] = 0;
				}
			}
			c.close();
			updateTime.setText( updateStatus );
			pageController.updateDisplay( values );
			pageDimming.updateDisplay( pwme );
			pageRadion.updateDisplay( rf );
			pageVortech.updateDisplay( vt );
			pageAI.updateDisplay( ai );
			pageIO.updateDisplay( io );
			pageCustom.updateDisplay( custom );
			boolean fUseMask = rapp.isCommunicateController();
			pageMain.updateRelayValues( new Relay( r, ron, roff ), fUseMask );
			for ( int i = 0; i < rapp.getPrefExpansionRelayQuantity(); i++ ) {
				pageExpRelays[i].updateRelayValues( new Relay( expr[i],
					expron[i], exproff[i] ), fUseMask );
			}

			if ( rapp.isAutoUpdateModulesEnabled() ) {
				// update the screen / pages if necessary
				checkDeviceModules( newEM, newREM );
			}

		} catch ( SQLException e ) {
			Log.d( TAG, "SQLException: " + e.getMessage() );
		} catch ( CursorIndexOutOfBoundsException e ) {
			Log.d( TAG, "CursorIndex out of bounds: " + e.getMessage() );
		}
	}

	class StatusReceiver extends BroadcastReceiver {
		private final String TAG = StatusReceiver.class.getSimpleName();

		public void onReceive ( Context context, Intent intent ) {
			// Log.d(TAG, "onReceive");
			String action = intent.getAction();
			if ( action.equals( MessageCommands.UPDATE_STATUS_INTENT ) ) {
				int id =
						intent.getIntExtra( MessageCommands.UPDATE_STATUS_ID,
											R.string.defaultStatusText );
				Log.d( TAG, getResources().getString( id ) );
				updateTime.setText( id );
			} else if ( action
					.equals( MessageCommands.UPDATE_DISPLAY_DATA_INTENT ) ) {
				Log.d( TAG, "update data intent" );
				updateDisplay();
				rapp.clearErrorRetryCount();
			} else if ( action.equals( MessageCommands.ERROR_MESSAGE_INTENT ) ) {
				boolean fDisplayUpdate = true;
				if ( rapp.isNotificationEnabled() && rapp.isErrorRetryEnabled() ) {
					// Only proceed if notifications are enabled
					// we are to retry connection before displaying an error
					// increment the error count
					String s =
							rapp.getString( R.string.messageErrorRetry,
											rapp.errorCount );
					Log.d( TAG, s );
					if ( rapp.canErrorRetry() ) {
						// if error count is less than the max,
						// we need to retry the communication
						Log.d( TAG, "Error Re-launch" );
						updateTime.setText( s );
						fDisplayUpdate = false;
						Runnable r = new Runnable() {
							public void run ( ) {
								launchStatusTask();
							}
						};
						Handler h = new Handler();
						h.postDelayed( r, rapp.getErrorRetryInterval() );
					}
					// otherwise if we have exceeded the max count, then we
					// display the error
				}
				if ( fDisplayUpdate ) {
					Log.d(	TAG,
							intent.getStringExtra( MessageCommands.ERROR_MESSAGE_STRING ) );
					updateTime
							.setText( intent
									.getStringExtra( MessageCommands.ERROR_MESSAGE_STRING ) );
				}
			} else if ( action.equals( MessageCommands.VORTECH_UPDATE_INTENT ) ) {
				int type =
						intent.getIntExtra( MessageCommands.VORTECH_UPDATE_TYPE,
											0 );
				Intent i =
						new Intent( StatusActivity.this,
							VortechPopupActivity.class );
				i.putExtra( VortechPopupActivity.TYPE, type );
				i.putExtra( Globals.PRE10_LOCATIONS,
							rapp.useOldPre10MemoryLocations() );
				startActivity( i );
			} else if ( action.equals( MessageCommands.MEMORY_RESPONSE_INTENT ) ) {
				String response =
						intent.getStringExtra( MessageCommands.MEMORY_RESPONSE_STRING );
				if ( response.equals( XMLTags.Ok ) ) {
					updateTime.setText( R.string.statusRefreshNeeded );
				} else {
					Toast.makeText( StatusActivity.this, response,
									Toast.LENGTH_LONG ).show();
				}
			} else if ( action.equals( MessageCommands.COMMAND_RESPONSE_INTENT ) ) {
				String response =
						intent.getStringExtra( MessageCommands.COMMAND_RESPONSE_STRING );
				if ( response.contains( XMLTags.Ok ) ) {
					updateTime.setText( R.string.statusRefreshNeeded );
				} else {
					Toast.makeText( StatusActivity.this, response,
									Toast.LENGTH_LONG ).show();
				}
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

	private String[] getControllerValues ( Cursor c ) {
		return new String[] {	c.getString( c.getColumnIndex( RAData.PCOL_T1 ) ),
								c.getString( c.getColumnIndex( RAData.PCOL_T2 ) ),
								c.getString( c.getColumnIndex( RAData.PCOL_T3 ) ),
								c.getString( c.getColumnIndex( RAData.PCOL_PH ) ),
								c.getString( c.getColumnIndex( RAData.PCOL_DP ) )
										+ "%",
								c.getString( c.getColumnIndex( RAData.PCOL_AP ) )
										+ "%",
								c.getString( c.getColumnIndex( RAData.PCOL_SAL ) )
										+ " ppt",
								c.getString( c.getColumnIndex( RAData.PCOL_ORP ) )
										+ " mV",
								c.getString( c.getColumnIndex( RAData.PCOL_PHE ) ),
								c.getString( c.getColumnIndex( RAData.PCOL_WL ) )
										+ "%" };
	}

	private String[] getPWMEValues ( Cursor c ) {
		String[] sa = new String[Controller.MAX_PWM_EXPANSION_PORTS];
		sa[0] = c.getString( c.getColumnIndex( RAData.PCOL_PWME0 ) ) + "%";
		sa[1] = c.getString( c.getColumnIndex( RAData.PCOL_PWME1 ) ) + "%";
		sa[2] = c.getString( c.getColumnIndex( RAData.PCOL_PWME2 ) ) + "%";
		sa[3] = c.getString( c.getColumnIndex( RAData.PCOL_PWME3 ) ) + "%";
		sa[4] = c.getString( c.getColumnIndex( RAData.PCOL_PWME4 ) ) + "%";
		sa[5] = c.getString( c.getColumnIndex( RAData.PCOL_PWME5 ) ) + "%";
		return sa;
	}

	private String[] getRadionValues ( Cursor c ) {
		String[] sa = new String[Controller.MAX_RADION_LIGHT_CHANNELS];
		sa[0] = c.getString( c.getColumnIndex( RAData.PCOL_RFW ) ) + "%";
		sa[1] = c.getString( c.getColumnIndex( RAData.PCOL_RFRB ) ) + "%";
		sa[2] = c.getString( c.getColumnIndex( RAData.PCOL_RFR ) ) + "%";
		sa[3] = c.getString( c.getColumnIndex( RAData.PCOL_RFG ) ) + "%";
		sa[4] = c.getString( c.getColumnIndex( RAData.PCOL_RFB ) ) + "%";
		sa[5] = c.getString( c.getColumnIndex( RAData.PCOL_RFI ) ) + "%";
		return sa;
	}

	private String[] getVortechValues ( Cursor c ) {
		String[] sa = new String[Controller.MAX_VORTECH_VALUES];
		String s = "";
		int v, mode;
		// mode
		v = c.getInt( c.getColumnIndex( RAData.PCOL_RFM ) );
		mode = v;
		if ( v >= 0 && v <= 11 ) {
			// use the index value
			s = vortechModes[v];
		} else if ( v >= 97 && v <= 100 ) {
			// use index 12
			s = vortechModes[v - 85];
		} else {
			// unknown, so use default status
			s = getString( R.string.defaultStatusText );
		}
		sa[Controller.VORTECH_MODE] = s;
		// speed
		v = c.getInt( c.getColumnIndex( RAData.PCOL_RFS ) );
		s = String.format( Locale.US, "%d%c", v, '%' );
		sa[Controller.VORTECH_SPEED] = s;
		// duration
		v = c.getInt( c.getColumnIndex( RAData.PCOL_RFD ) );
		switch ( mode ) {
			case 3:
			case 5:
				// value is in 100 milliseconds
				s = String.format( Locale.US, "%d %s", v, "ms" );
				break;
			case 4:
				// value is in seconds
				s = String.format( Locale.US, "%d %c", v, 's' );
				break;
			default:
				break;
		}
		sa[Controller.VORTECH_DURATION] = s;
		return sa;
	}

	private String[] getAIValues ( Cursor c ) {
		String[] sa = new String[Controller.MAX_AI_CHANNELS];
		sa[Controller.AI_WHITE] =
				c.getString( c.getColumnIndex( RAData.PCOL_AIW ) ) + "%";
		sa[Controller.AI_BLUE] =
				c.getString( c.getColumnIndex( RAData.PCOL_AIB ) ) + "%";
		sa[Controller.AI_ROYALBLUE] =
				c.getString( c.getColumnIndex( RAData.PCOL_AIRB ) ) + "%";
		return sa;
	}

	private String[] getIOValues ( Cursor c ) {
		String[] sa = new String[Controller.MAX_IO_CHANNELS];
		short io = c.getShort( c.getColumnIndex( RAData.PCOL_IO ) );
		String s;
		for ( byte i = 0; i < Controller.MAX_IO_CHANNELS; i++ ) {
			if ( Controller.getIOChannel( io, i ) ) {
				s = getString( R.string.labelOFF );
			} else {
				s = getString( R.string.labelON );
			}
			sa[i] = s;
		}
		return sa;
	}

	private String[] getCustomValues ( Cursor c ) {
		return new String[] {	c.getString( c.getColumnIndex( RAData.PCOL_C0 ) ),
								c.getString( c.getColumnIndex( RAData.PCOL_C1 ) ),
								c.getString( c.getColumnIndex( RAData.PCOL_C2 ) ),
								c.getString( c.getColumnIndex( RAData.PCOL_C3 ) ),
								c.getString( c.getColumnIndex( RAData.PCOL_C4 ) ),
								c.getString( c.getColumnIndex( RAData.PCOL_C5 ) ),
								c.getString( c.getColumnIndex( RAData.PCOL_C6 ) ),
								c.getString( c.getColumnIndex( RAData.PCOL_C7 ) ) };
	}

	private void checkDeviceModules ( short newEM, short newREM ) {
		boolean fReload = false;
		short oldEM = (short) rapp.getPreviousEM();
		Log.d( TAG, "Old: " + oldEM + " New: " + newEM );
		if ( oldEM != newEM ) {
			// expansion modules different
			// set flag to reload the pages
			fReload = true;
			// check which expansion modules are installed
			// set the installed modules in the preferences
			boolean f = false;
			if ( Controller.isAIModuleInstalled( newEM ) )
				f = true;
			else
				f = false;
			Log.d( TAG, "AI: " + f );
			rapp.setPref( R.string.prefExpAIEnableKey, f );
			if ( Controller.isDimmingModuleInstalled( newEM ) )
				f = true;
			else
				f = false;
			Log.d( TAG, "Dimming: " + f );
			rapp.setPref( R.string.prefExpDimmingEnableKey, f );
			if ( Controller.isIOModuleInstalled( newEM ) )
				f = true;
			else
				f = false;
			Log.d( TAG, "IO: " + f );
			rapp.setPref( R.string.prefExpIOEnableKey, f );
			if ( Controller.isORPModuleInstalled( newEM ) )
				f = true;
			else
				f = false;
			Log.d( TAG, "ORP: " + f );
			rapp.setPref( R.string.prefORPVisibilityKey, f );
			if ( Controller.isPHExpansionModuleInstalled( newEM ) )
				f = true;
			else
				f = false;
			Log.d( TAG, "PHE: " + f );
			rapp.setPref( R.string.prefPHExpVisibilityKey, f );
			if ( Controller.isRFModuleInstalled( newEM ) )
				f = true;
			else
				f = false;
			Log.d( TAG, "RF: " + f );
			rapp.setPref( R.string.prefExpRadionEnableKey, f );
			rapp.setPref( R.string.prefExpVortechEnableKey, f );
			if ( Controller.isSalinityModuleInstalled( newEM ) )
				f = true;
			else
				f = false;
			Log.d( TAG, "Salinity: " + f );
			rapp.setPref( R.string.prefSalinityVisibilityKey, f );
			if ( Controller.isWaterLevelModuleInstalled( newEM ) )
				f = true;
			else
				f = false;
			Log.d( TAG, "WATER: " + f );
			rapp.setPref( R.string.prefWaterLevelVisibilityKey, f );

			// update the previous settings to the new ones after we change
			rapp.setPreviousEM( newEM );
		}

		int newRQty = Controller.getRelayExpansionModulesInstalled( newREM );
		int oldRQty = rapp.getPrefExpansionRelayQuantity();
		Log.d( TAG, "Old Qty: " + oldRQty + " New Qty: " + newRQty );
		if ( oldRQty != newRQty ) {
			// expansion relay modules different
			// set flag to reload the pages
			fReload = true;
			// set the installed relays in the preferences
			Log.d( TAG, "Relays: " + newRQty );
			rapp.setPref( R.string.prefExpQtyKey, Integer.toString( newRQty ) );
		}

		if ( fReload ) {
			reloadPages();
			updateViewsVisibility();
			redrawPages();
		}
	}

	private void reloadPages ( ) {
		// scroll to first page on entering settings
		pager.setCurrentItem( POS_CONTROLLER );
		// force the pages to be redrawn if we enter settings
		fReloadPages = true;
	}

	private void redrawPages ( ) {
		// redraw the pages
		updatePageOrder();
		pagerAdapter.notifyDataSetChanged();
		fReloadPages = false;
	}

	@Override
	public boolean onCreateOptionsMenu ( Menu menu ) {
		MenuInflater inflater = getSupportMenuInflater();
		inflater.inflate( R.menu.status_menu, menu );
		return true;
	}

	@Override
	public boolean onOptionsItemSelected ( MenuItem item ) {
		// Handle item selection
		switch ( item.getItemId() ) {
			case R.id.refresh:
				// launch the update
				launchStatusTask();
				break;
			case R.id.settings:
				// launch settings
				reloadPages();
				startActivity( new Intent( this, PrefsActivity.class ) );
				break;
			case R.id.about:
				// launch about box
				startActivity( new Intent( this, AboutActivity.class ) );
				break;
			case R.id.params:
				startActivity( new Intent( this, ParamsListActivity.class ) );
				break;
			case R.id.memory:
				// launch memory
				Intent i = new Intent( this, MemoryTabsActivity.class );
				i.putExtra( Globals.PRE10_LOCATIONS,
							rapp.useOldPre10MemoryLocations() );
				startActivity( i );
				break;
			case R.id.commands:
				// launch commands
				startActivity( new Intent( this, CommandTabsActivity.class ) );
				break;
			default:
				return super.onOptionsItemSelected( item );
		}
		return true;
	}

	private void setPagerPrefs ( ) {
		pagerAdapter = new CustomPagerAdapter();
		pager.setAdapter( pagerAdapter );
		// Set the minimum pages to keep loaded
		// will set to minimum pages since the pages are not complex
		pager.setOffscreenPageLimit( MIN_PAGES );
		titleIndicator.setViewPager( pager );
	}

	private void updatePageOrder ( ) {
		// updates the order of the pages for display
		int i, j;
		int qty = rapp.getPrefExpansionRelayQuantity();
		// loop through all the possible pages
		// keep track of the pages installed compared to total pages
		// if the module is enabled, add it to the available pages list
		// then increment the installed pages counter
		for ( i = POS_START, j = POS_START; i <= POS_END; i++ ) {
			switch ( i ) {
				case POS_COMMANDS:
					//Log.d( TAG, j + ": Commands" );
					appPages[j] = pageCommands;
					j++;
					break;
				case POS_CONTROLLER:
					//Log.d( TAG, j + ": Controller" );
					appPages[j] = pageController;
					j++;
					break;
				case POS_DIMMING:
					if ( rapp.getDimmingModuleEnabled() ) {
						//Log.d( TAG, j + ": Dimming" );
						appPages[j] = pageDimming;
						j++;
					}
					break;
				case POS_RADION:
					if ( rapp.getRadionModuleEnabled() ) {
						//Log.d( TAG, j + ": Radion" );
						appPages[j] = pageRadion;
						j++;
					}
					break;
				case POS_VORTECH:
					if ( rapp.getVortechModuleEnabled() ) {
						//Log.d( TAG, j + ": Vortech" );
						appPages[j] = pageVortech;
						j++;
					}
					break;
				case POS_AI:
					if ( rapp.getAIModuleEnabled() ) {
						//Log.d( TAG, j + ": AI" );
						appPages[j] = pageAI;
						j++;
					}
					break;
				case POS_IO:
					if ( rapp.getIOModuleEnabled() ) {
						//Log.d( TAG, j + ": IO" );
						appPages[j] = pageIO;
						j++;
					}
					break;
				case POS_CUSTOM:
					if ( rapp.getCustomModuleEnabled() ) {
						//Log.d( TAG, j + ": Custom" );
						appPages[j] = pageCustom;
						j++;
					}
					break;
				case POS_MAIN_RELAY:
					//Log.d( TAG, j + ": Main Relay" );
					appPages[j] = pageMain;
					j++;
					break;
				case POS_EXP1_RELAY:
				case POS_EXP2_RELAY:
				case POS_EXP3_RELAY:
				case POS_EXP4_RELAY:
				case POS_EXP5_RELAY:
				case POS_EXP6_RELAY:
				case POS_EXP7_RELAY:
				case POS_EXP8_RELAY:
					if ( qty > 0 ) {
						int relay = i - POS_EXP1_RELAY;
						if ( relay < qty ) {
							//Log.d( TAG, j + ": Exp Relay " + relay );
							appPages[j] = pageExpRelays[relay];
							j++;
						}
					}
					break;
			}
		}
		if ( j < POS_END ) {
			for ( ; j < POS_END; j++ ) {
				appPages[j] = null;
			}
		}
	}

	private class CustomPagerAdapter extends PagerAdapter {
		private final String TAG = CustomPagerAdapter.class.getSimpleName();

		public int getCount ( ) {
			int qty = MIN_PAGES + rapp.getTotalInstalledModuleQuantity();
			return qty;
		}
		
		public CharSequence getPageTitle ( int position ) {
			return ((RAPage)appPages[position]).getPageTitle();
		}

		public void destroyItem (
				ViewGroup container,
				int position,
				Object object ) {
			Log.d( TAG, "destroyItem " + position );
			((ViewPager) container).removeView( (View) object );
		}

		public Object instantiateItem ( ViewGroup container, int position ) {
			Log.d( TAG, "Position: " + position );
			((ViewPager) container).addView( appPages[position] );
			return appPages[position];
		}

		public boolean isViewFromObject ( View view, Object object ) {
			return view == object;
		}

		public int getItemPosition ( Object object ) {
			return POSITION_NONE;
		}

	}
}
