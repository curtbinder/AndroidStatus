/*
 * Copyright (c) 2011-2013 by Curt Binder (http://curtbinder.info)
 * 
 * This work is made available under the terms of the Creative Commons
 * Attribution-NonCommercial-ShareAlike 3.0 Unported License
 * http://creativecommons.org/licenses/by-nc-sa/3.0/
 */

package info.curtbinder.reefangel.phone;

import info.curtbinder.reefangel.controller.Controller;
import info.curtbinder.reefangel.controller.Relay;
import info.curtbinder.reefangel.db.StatusProvider;
import info.curtbinder.reefangel.db.StatusTable;
import info.curtbinder.reefangel.phone.pages.AIPage;
import info.curtbinder.reefangel.phone.pages.CommandsPage;
import info.curtbinder.reefangel.phone.pages.ControllerPage;
import info.curtbinder.reefangel.phone.pages.CustomPage;
import info.curtbinder.reefangel.phone.pages.DimmingPage;
import info.curtbinder.reefangel.phone.pages.FlagsPage;
import info.curtbinder.reefangel.phone.pages.IOPage;
import info.curtbinder.reefangel.phone.pages.RAPage;
import info.curtbinder.reefangel.phone.pages.RadionPage;
import info.curtbinder.reefangel.phone.pages.RelayBoxPage;
import info.curtbinder.reefangel.phone.pages.SCDimmingPage;
import info.curtbinder.reefangel.phone.pages.VortechPage;
import info.curtbinder.reefangel.service.MessageCommands;
import info.curtbinder.reefangel.service.UpdateService;
import info.curtbinder.reefangel.service.XMLTags;

import java.util.Locale;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

public class StatusActivity extends BaseActivity implements
		ActionBar.OnNavigationListener {
	private static final String TAG = StatusActivity.class.getSimpleName();

	// do we reload the pages or not?
	private boolean fReloadPages = false;
	// do not switch selected profile when restoring the application state
	private static boolean fRestoreState = false;

	// Display views
	private TextView updateTime;
	private ViewPager pager;
	private CustomPagerAdapter pagerAdapter;
	private String[] vortechModes;
	private View[] appPages;
	// minimum number of pages: commands, flags, status, main relay
	private static final int MIN_PAGES = 4;

	private static final int POS_START = 0;

	private static final int POS_FLAGS = POS_START;
	private static final int POS_COMMANDS = POS_START + 1;
	private static final int POS_CONTROLLER = POS_START + 2;

	private static final int POS_MODULES = POS_CONTROLLER + 10;
	private static final int POS_DIMMING = POS_MODULES;
	private static final int POS_SC_DIMMING = POS_MODULES + 1;
	private static final int POS_RADION = POS_MODULES + 2;
	private static final int POS_VORTECH = POS_MODULES + 3;
	private static final int POS_AI = POS_MODULES + 4;
	private static final int POS_IO = POS_MODULES + 5;
	private static final int POS_CUSTOM = POS_MODULES + 6;

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
	private FlagsPage pageFlags;
	private ControllerPage pageController;
	private DimmingPage pageDimming;
	private SCDimmingPage pageSCDimming;
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
		filter.addAction( MessageCommands.VORTECH_POPUP_INTENT );
		filter.addAction( MessageCommands.MEMORY_RESPONSE_INTENT );
		filter.addAction( MessageCommands.COMMAND_RESPONSE_INTENT );
		filter.addAction( MessageCommands.VERSION_RESPONSE_INTENT );
		filter.addAction( MessageCommands.OVERRIDE_RESPONSE_INTENT );
		filter.addAction( MessageCommands.OVERRIDE_POPUP_INTENT );
		filter.addAction( MessageCommands.CALIBRATE_RESPONSE_INTENT );

		vortechModes =
				getResources().getStringArray( R.array.vortechModeLabels );

		createViews();
		findViews();

		// update actionbar
		final ActionBar ab = getSupportActionBar();
		ab.setNavigationMode( ActionBar.NAVIGATION_MODE_LIST );
		ab.setDisplayShowTitleEnabled( false );

		// set the max number of pages that we can have
		appPages = new View[POS_END];
		updatePageOrder();
		setPagerPrefs();

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

		fRestoreState = true;
		setNavigationList();

		// this forces all the pages to be redrawn when the app is restored
		if ( fReloadPages ) {
			redrawPages();
		}

		disableRelayButtons();
		setOnClickListeners();

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
				ArrayAdapter
						.createFromResource(	context, arrayID,
												R.layout.sherlock_spinner_item );
		list.setDropDownViewResource( R.layout.sherlock_spinner_dropdown_item );
		ab.setListNavigationCallbacks( list, this );
		ab.setSelectedNavigationItem( rapp.getSelectedProfile() );
	}

	private void createViews ( ) {
		Context ctx = rapp.getBaseContext();
		pageCommands = new CommandsPage( ctx );
		pageFlags = new FlagsPage( ctx );
		pageController = new ControllerPage( ctx );
		pageDimming = new DimmingPage( ctx );
		pageSCDimming = new SCDimmingPage( ctx );
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
	}

	private void disableRelayButtons ( ) {
		pageMain.refreshButtonEnablement();

		for ( int i = 0; i < Controller.MAX_EXPANSION_RELAYS; i++ ) {
			pageExpRelays[i].refreshButtonEnablement();
		}
	}

	private void setOnClickListeners ( ) {
		int i;
		if ( rapp.raprefs.isCommunicateController() ) {
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

		// Labels
		// updateRefreshButtonLabel();
		setControllerLabels();

		setRelayLabels();
		int i;
		if ( rapp.raprefs.getDimmingModuleEnabled() ) {
			for ( i = 0; i < Controller.MAX_PWM_EXPANSION_PORTS; i++ )
				pageDimming.setLabel( i, rapp.raprefs
						.getDimmingModuleChannelLabel( i ) );
		}

		if ( rapp.raprefs.getSCDimmingModuleEnabled() ) {
			for ( i = 0; i < Controller.MAX_SCPWM_EXPANSION_PORTS; i++ )
				pageSCDimming.setLabel( i, rapp.raprefs
				                        .getSCDimmingModuleChannelLabel(i) );
		}
		if ( rapp.raprefs.getIOModuleEnabled() ) {
			for ( i = 0; i < Controller.MAX_IO_CHANNELS; i++ ) {
				pageIO.setLabel( i, rapp.raprefs.getIOModuleChannelLabel( i ) );
			}
		}

		if ( rapp.raprefs.getCustomModuleEnabled() ) {
			for ( i = 0; i < Controller.MAX_CUSTOM_VARIABLES; i++ )
				pageCustom.setLabel( i, rapp.raprefs
						.getCustomModuleChannelLabel( i ) );
		}

		setControllerVisibility();
		// TODO update control visibility here
		// TODO consider hiding dimming channels not in use
		// TODO consider hiding custom variables not in use
		// TODO consider hiding io channels not in use
	}

	private void setControllerLabels ( ) {
		pageController.setLabel(	ControllerPage.T1_INDEX,
									rapp.raprefs.getT1Label(),
									getString( R.string.labelTemp1 ) );
		pageController.setLabel(	ControllerPage.T2_INDEX,
									rapp.raprefs.getT2Label(),
									getString( R.string.labelTemp2 ) );
		pageController.setLabel(	ControllerPage.T3_INDEX,
									rapp.raprefs.getT3Label(),
									getString( R.string.labelTemp3 ) );
		pageController.setLabel(	ControllerPage.PH_INDEX,
									rapp.raprefs.getPHLabel(),
									getString( R.string.labelPH ) );
		pageController.setLabel(	ControllerPage.DP_INDEX,
									rapp.raprefs.getDPLabel(),
									getString( R.string.labelDP ) );
		pageController.setLabel(	ControllerPage.AP_INDEX,
									rapp.raprefs.getAPLabel(),
									getString( R.string.labelAP ) );
		pageController.setLabel(	ControllerPage.ATOLO_INDEX,
									rapp.raprefs.getAtoLowLabel(),
									getString( R.string.labelAtoLow ) );
		pageController.setLabel(	ControllerPage.ATOHI_INDEX,
									rapp.raprefs.getAtoHighLabel(),
									getString( R.string.labelAtoHigh ) );
		pageController.setLabel(	ControllerPage.SALINITY_INDEX,
									rapp.raprefs.getSalinityLabel(),
									getString( R.string.labelSalinity ) );
		pageController.setLabel(	ControllerPage.ORP_INDEX,
									rapp.raprefs.getORPLabel(),
									getString( R.string.labelORP ) );
		pageController.setLabel(	ControllerPage.PHE_INDEX,
									rapp.raprefs.getPHExpLabel(),
									getString( R.string.labelPHExp ) );
		pageController.setLabel(	ControllerPage.WL_INDEX,
									rapp.raprefs.getWaterLevelLabel(0),
									rapp.raprefs.getWaterLevelDefaultLabel(0) );
		pageController.setLabel(	ControllerPage.WL1_INDEX,
									rapp.raprefs.getWaterLevelLabel(1),
									rapp.raprefs.getWaterLevelDefaultLabel(1) );
		pageController.setLabel(	ControllerPage.WL2_INDEX,
									rapp.raprefs.getWaterLevelLabel(2),
									rapp.raprefs.getWaterLevelDefaultLabel(2) );
		pageController.setLabel(	ControllerPage.WL3_INDEX,
									rapp.raprefs.getWaterLevelLabel(3),
									rapp.raprefs.getWaterLevelDefaultLabel(3) );
		pageController.setLabel(	ControllerPage.WL4_INDEX,
									rapp.raprefs.getWaterLevelLabel(4),
									rapp.raprefs.getWaterLevelDefaultLabel(4) );
		pageController.setLabel( ControllerPage.HUMIDITY_INDEX, 
		                         rapp.raprefs.getHumidityLabel(), 
		                         getString(R.string.labelHumidity) );
	}

	private void setRelayLabels ( ) {
		int qty = rapp.raprefs.getExpansionRelayQuantity();

		String defaultPort = getString( R.string.defaultPortName );
		for ( int i = 0; i < Controller.MAX_RELAY_PORTS; i++ ) {
			pageMain.setPortLabel(	i, rapp.raprefs.getMainRelayLabel( i ),
									defaultPort + (i + 1) );
			boolean enabled = rapp.raprefs.getMainRelayControlEnabled( i );
			pageMain.setControlEnabled( i, enabled );

			for ( int j = 0; j < Controller.MAX_EXPANSION_RELAYS; j++ ) {
				// skip over the relays that are not installed
				if ( (j + 1) > qty )
					break;
				pageExpRelays[j].setPortLabel( i, rapp.raprefs
						.getRelayLabel( j + 1, i ), defaultPort + (i + 1) );
				pageExpRelays[j].setControlEnabled( i, rapp.raprefs
						.getRelayControlEnabled( j + 1, i ) );
			}

		}
	}

	private void setControllerVisibility ( ) {
		// Visibility
		pageController.setVisibility(	ControllerPage.T2_INDEX,
										rapp.raprefs.getT2Visibility() );
		pageController.setVisibility(	ControllerPage.T3_INDEX,
										rapp.raprefs.getT3Visibility() );
		pageController.setVisibility(	ControllerPage.DP_INDEX,
										rapp.raprefs.getDPVisibility() );
		pageController.setVisibility(	ControllerPage.AP_INDEX,
										rapp.raprefs.getAPVisibility() );
		pageController.setVisibility(	ControllerPage.PH_INDEX,
										rapp.raprefs.getPHVisibility() );
		pageController.setVisibility(	ControllerPage.ATOLO_INDEX,
										rapp.raprefs.getAtoLowVisibility() );
		pageController.setVisibility(	ControllerPage.ATOHI_INDEX,
										rapp.raprefs.getAtoHighVisibility() );
		pageController.setVisibility(	ControllerPage.SALINITY_INDEX,
										rapp.raprefs.getSalinityVisibility() );
		pageController.setVisibility(	ControllerPage.ORP_INDEX,
										rapp.raprefs.getORPVisibility() );
		pageController.setVisibility(	ControllerPage.PHE_INDEX,
										rapp.raprefs.getPHExpVisibility() );
		pageController.setVisibility(	ControllerPage.WL_INDEX,
										rapp.raprefs.getWaterLevelVisibility(0) );
		pageController.setVisibility(	ControllerPage.WL1_INDEX,
										rapp.raprefs.getWaterLevelVisibility(1) );
		pageController.setVisibility(	ControllerPage.WL2_INDEX,
										rapp.raprefs.getWaterLevelVisibility(2) );
		pageController.setVisibility(	ControllerPage.WL3_INDEX,
										rapp.raprefs.getWaterLevelVisibility(3) );
		pageController.setVisibility(	ControllerPage.WL4_INDEX,
										rapp.raprefs.getWaterLevelVisibility(4) );
		pageController.setVisibility( ControllerPage.HUMIDITY_INDEX, 
		                              rapp.raprefs.getHumidityVisibility() );
	}

	@Override
	public boolean onNavigationItemSelected ( int itemPosition, long itemId ) {
		// only switch profiles when the user changes the navigation item,
		// not when the navigation list state is restored
		if ( !fRestoreState ) {
			switchProfiles( itemPosition );
		} else {
			fRestoreState = false;
		}
		return true;
	}

	private void switchProfiles ( int id ) {
		rapp.setSelectedProfile( id );
	}

	private void launchStatusTask ( ) {
		Intent i = new Intent( this, UpdateService.class );
		i.setAction( MessageCommands.QUERY_STATUS_INTENT );
		startService( i );
	}

	public void updateDisplay ( ) {
		Uri uri =
				Uri.parse( StatusProvider.CONTENT_URI + "/"
							+ StatusProvider.PATH_LATEST );
		Cursor c =
				getContentResolver().query( uri, null, null, null,
											StatusTable.COL_ID + " DESC" );
		String updateStatus;
		String[] values;
		String[] pwme;
		String[] rf;
		String[] vt;
		String[] ai;
		String[] io;
		String[] custom;
		String[] scpwme;
		short r, ron, roff, newEM, newEM1, newREM, apValue, dpValue, af, sf;
		short[] pwmeValues = new short[Controller.MAX_PWM_EXPANSION_PORTS];
		short[] radionValues = new short[Controller.MAX_RADION_LIGHT_CHANNELS];
		short[] aiValues = new short[Controller.MAX_AI_CHANNELS];
		short[] expr = new short[Controller.MAX_EXPANSION_RELAYS];
		short[] expron = new short[Controller.MAX_EXPANSION_RELAYS];
		short[] exproff = new short[Controller.MAX_EXPANSION_RELAYS];
		short[] vtValues = new short[Controller.MAX_VORTECH_VALUES];
		short[] scpwmeValues = new short[Controller.MAX_SCPWM_EXPANSION_PORTS];

		if ( c.moveToFirst() ) {
			updateStatus =
					c.getString( c.getColumnIndex( StatusTable.COL_LOGDATE ) );
			values = getControllerValues( c );
			apValue = c.getShort( c.getColumnIndex( StatusTable.COL_AP ) );
			dpValue = c.getShort( c.getColumnIndex(  StatusTable.COL_DP ) );
			pwme = getPWMETextValues( c );
			pwmeValues = getPWMEValues( c );
			scpwme = getSCPWMETextValues( c );
			scpwmeValues = getSCPWMEValues( c );
			rf = getRadionTextValues( c );
			radionValues = getRadionValues( c );
			vt = getVortechTextValues( c );
			vtValues = getVortechValues( c );
			ai = getAITextValues( c );
			aiValues = getAIValues( c );
			io = getIOValues( c );
			custom = getCustomValues( c );
			r = c.getShort( c.getColumnIndex( StatusTable.COL_RDATA ) );
			ron = c.getShort( c.getColumnIndex( StatusTable.COL_RONMASK ) );
			roff = c.getShort( c.getColumnIndex( StatusTable.COL_ROFFMASK ) );

			expr[0] = c.getShort( c.getColumnIndex( StatusTable.COL_R1DATA ) );
			expron[0] =
					c.getShort( c.getColumnIndex( StatusTable.COL_R1ONMASK ) );
			exproff[0] =
					c.getShort( c.getColumnIndex( StatusTable.COL_R1OFFMASK ) );
			expr[1] = c.getShort( c.getColumnIndex( StatusTable.COL_R2DATA ) );
			expron[1] =
					c.getShort( c.getColumnIndex( StatusTable.COL_R2ONMASK ) );
			exproff[1] =
					c.getShort( c.getColumnIndex( StatusTable.COL_R2OFFMASK ) );
			expr[2] = c.getShort( c.getColumnIndex( StatusTable.COL_R3DATA ) );
			expron[2] =
					c.getShort( c.getColumnIndex( StatusTable.COL_R3ONMASK ) );
			exproff[2] =
					c.getShort( c.getColumnIndex( StatusTable.COL_R3OFFMASK ) );
			expr[3] = c.getShort( c.getColumnIndex( StatusTable.COL_R4DATA ) );
			expron[3] =
					c.getShort( c.getColumnIndex( StatusTable.COL_R4ONMASK ) );
			exproff[3] =
					c.getShort( c.getColumnIndex( StatusTable.COL_R4OFFMASK ) );
			expr[4] = c.getShort( c.getColumnIndex( StatusTable.COL_R5DATA ) );
			expron[4] =
					c.getShort( c.getColumnIndex( StatusTable.COL_R5ONMASK ) );
			exproff[4] =
					c.getShort( c.getColumnIndex( StatusTable.COL_R5OFFMASK ) );
			expr[5] = c.getShort( c.getColumnIndex( StatusTable.COL_R6DATA ) );
			expron[5] =
					c.getShort( c.getColumnIndex( StatusTable.COL_R6ONMASK ) );
			exproff[5] =
					c.getShort( c.getColumnIndex( StatusTable.COL_R6OFFMASK ) );
			expr[6] = c.getShort( c.getColumnIndex( StatusTable.COL_R7DATA ) );
			expron[6] =
					c.getShort( c.getColumnIndex( StatusTable.COL_R7ONMASK ) );
			exproff[6] =
					c.getShort( c.getColumnIndex( StatusTable.COL_R7OFFMASK ) );
			expr[7] = c.getShort( c.getColumnIndex( StatusTable.COL_R8DATA ) );
			expron[7] =
					c.getShort( c.getColumnIndex( StatusTable.COL_R8ONMASK ) );
			exproff[7] =
					c.getShort( c.getColumnIndex( StatusTable.COL_R8OFFMASK ) );
			newEM = c.getShort( c.getColumnIndex( StatusTable.COL_EM ) );
			newEM1 = c.getShort( c.getColumnIndex( StatusTable.COL_EM1 ) );
			newREM = c.getShort( c.getColumnIndex( StatusTable.COL_REM ) );
			sf = c.getShort( c.getColumnIndex( StatusTable.COL_SF ) );
			af = c.getShort( c.getColumnIndex( StatusTable.COL_AF ) );
		} else {
			updateStatus = getString( R.string.messageNever );
			values = getNeverValues( Controller.MAX_CONTROLLER_VALUES );
			pwme = getNeverValues( Controller.MAX_PWM_EXPANSION_PORTS );
			scpwme = getNeverValues( Controller.MAX_SCPWM_EXPANSION_PORTS );
			rf = getNeverValues( Controller.MAX_RADION_LIGHT_CHANNELS );
			vt = getNeverValues( Controller.MAX_VORTECH_VALUES );
			ai = getNeverValues( Controller.MAX_AI_CHANNELS );
			io = getNeverValues( Controller.MAX_IO_CHANNELS );
			custom = getNeverValues( Controller.MAX_CUSTOM_VARIABLES );
			r = ron = roff = newEM = newEM1 = newREM = apValue = dpValue = sf = af = 0;
			int i;
			for ( i = 0; i < Controller.MAX_EXPANSION_RELAYS; i++ ) {
				expr[i] = expron[i] = exproff[i] = 0;
			}
			for ( i = 0; i < Controller.MAX_PWM_EXPANSION_PORTS; i++ ) {
				pwmeValues[i] = 0;
			}
			for ( i = 0; i < Controller.MAX_SCPWM_EXPANSION_PORTS; i++ ) {
				scpwmeValues[i] = 0;
			}
			for ( i = 0; i < Controller.MAX_RADION_LIGHT_CHANNELS; i++ ) {
				radionValues[i] = 0;
			}
			for ( i = 0; i < Controller.MAX_AI_CHANNELS; i++ ) {
				aiValues[i] = 0;
			}
			for ( i = 0; i < Controller.MAX_VORTECH_VALUES; i++ ) {
				vtValues[i] = 0;
			}
		}
		c.close();
		
		updateTime.setText( updateStatus );
		pageController.updateDisplay( values );
		pageController.updatePWMValues( apValue, dpValue );
		pageFlags.updateStatus( sf );
		pageFlags.updateAlert( af );
		pageDimming.updateDisplay( pwme );
		pageDimming.updatePWMValues( pwmeValues );
		pageSCDimming.updateDisplay( scpwme );
		pageSCDimming.updatePWMValues( scpwmeValues );
		pageRadion.updateDisplay( rf );
		pageRadion.updatePWMValues( radionValues );
		pageVortech.updateDisplay( vt );
		pageVortech.updateValues( vtValues );
		pageAI.updateDisplay( ai );
		pageAI.updatePWMValues( aiValues );
		pageIO.updateDisplay( io );
		pageCustom.updateDisplay( custom );
		boolean fUseMask = rapp.raprefs.isCommunicateController();
		pageMain.updateRelayValues( new Relay( r, ron, roff ), fUseMask );
		for ( int i = 0; i < rapp.raprefs.getExpansionRelayQuantity(); i++ ) {
			pageExpRelays[i].updateRelayValues( new Relay( expr[i], expron[i],
				exproff[i] ), fUseMask );
		}
		
		if ( rapp.raprefs.isAutoUpdateModulesEnabled() ) {
			// update the screen / pages if necessary
			checkDeviceModules( newEM, newEM1, newREM );
		}
	}

	class StatusReceiver extends BroadcastReceiver {
		// private final String TAG = StatusReceiver.class.getSimpleName();

		public void onReceive ( Context context, Intent intent ) {
			String action = intent.getAction();
			if ( action.equals( MessageCommands.UPDATE_STATUS_INTENT ) ) {
				int id =
						intent.getIntExtra( MessageCommands.UPDATE_STATUS_ID,
											R.string.defaultStatusText );
				if ( id > -1 ) {
					updateTime.setText( id );
				} else {
					// we are updating with a string being sent to us
					updateTime
							.setText( intent
									.getStringExtra( MessageCommands.UPDATE_STATUS_STRING ) );
				}
			} else if ( action
					.equals( MessageCommands.UPDATE_DISPLAY_DATA_INTENT ) ) {
				updateDisplay();
			} else if ( action.equals( MessageCommands.VORTECH_POPUP_INTENT ) ) {
				int type = intent.getIntExtra( VortechPopupActivity.TYPE, 0 );
				int value = intent.getIntExtra( VortechPopupActivity.VALUE, 0 );
				Intent i = new Intent( StatusActivity.this,
							VortechPopupActivity.class );
				i.putExtra( VortechPopupActivity.TYPE, type );
				i.putExtra( VortechPopupActivity.VALUE, value );
				i.putExtra( Globals.PRE10_LOCATIONS,
							rapp.raprefs.useOldPre10MemoryLocations() );
				startActivity( i );
			} else if ( action.equals( MessageCommands.MEMORY_RESPONSE_INTENT ) ) {
				// for vortech responses
				String response =
						intent.getStringExtra( MessageCommands.MEMORY_RESPONSE_STRING );
				displayResponse(response, -1, false);
			} else if ( action.equals( MessageCommands.OVERRIDE_RESPONSE_INTENT ) ) {
				String response = intent.getStringExtra(MessageCommands.OVERRIDE_RESPONSE_STRING);
				displayResponse(response, -1, false);
			} else if ( action.equals( MessageCommands.OVERRIDE_POPUP_INTENT ) ) {
				// message to display the popup
				int channel = intent.getIntExtra( OverridePopupActivity.CHANNEL_KEY, 0);
				String msg = rapp.getPWMOverrideMessageDisplay( channel );
				Intent i = new Intent(StatusActivity.this, OverridePopupActivity.class);
				i.putExtra( OverridePopupActivity.MESSAGE_KEY, msg ); 
				i.putExtra( OverridePopupActivity.CHANNEL_KEY, channel);
				i.putExtra( OverridePopupActivity.VALUE_KEY, 
				            intent.getShortExtra( OverridePopupActivity.VALUE_KEY, (short) 0) );
				startActivity(i);
			} else if ( action.equals( MessageCommands.COMMAND_RESPONSE_INTENT ) ) {
				String response =
						intent.getStringExtra( MessageCommands.COMMAND_RESPONSE_STRING );
				displayResponse(response, -1, false);
			} else if ( action.equals( MessageCommands.CALIBRATE_RESPONSE_INTENT ) ) {
				String response =
						intent.getStringExtra( MessageCommands.CALIBRATE_RESPONSE_STRING );
				displayResponse(response, R.string.statusFinished, true );				
			} else if ( action.equals( MessageCommands.VERSION_RESPONSE_INTENT ) ) {
				// set the version button's text to the version of the software
				((Button) findViewById( R.id.command_button_version ))
				.setText( intent.getStringExtra( MessageCommands.VERSION_RESPONSE_STRING ) );
				// TODO set the time to be the last updated time from the database
				updateTime.setText( R.string.statusFinished );
			}
		}
	}
	
	private void displayResponse ( String response, int stringId, boolean fAlwaysToast ) {
		int msgId;
		boolean fShowToast = false;
		if ( stringId == -1 ) {
			msgId = R.string.statusRefreshNeeded;
		} else {
			msgId = stringId;
		}
		if ( response.contains( XMLTags.Ok ) ) {
			updateTime.setText( msgId );
			if ( rapp.raprefs.isAutoRefreshAfterUpdate() ) {
				updateTime.setText( R.string.statusWaiting );
				Log.d(TAG, "AutoRefreshAfterUpdate");
				Handler h = new Handler();
				Runnable r = new Runnable() {
					public void run() {
						launchStatusTask();
					}
				};
				// pause for a second before we proceed
				h.postDelayed( r, 1000 );
			}	
		} else {
			fShowToast = true;
		}
		
		if ( fAlwaysToast || fShowToast ) {
			Toast.makeText( StatusActivity.this, response,
							Toast.LENGTH_LONG ).show();
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
		// FIXME switch to only setting the string to 1 or 0
		// FIXME so the controllerpage can easily update the images
		String l, h;
		if ( c.getShort( c.getColumnIndex( StatusTable.COL_ATOLO ) ) == 1 )
			l = getString( R.string.labelON ); // ACTIVE, GREEN, ON
		else
			l = getString( R.string.labelOFF ); // INACTIVE, RED, OFF
		if ( c.getShort( c.getColumnIndex( StatusTable.COL_ATOHI ) ) == 1 )
			h = getString( R.string.labelON ); // ACTIVE, GREEN, ON
		else
			h = getString( R.string.labelOFF ); // INACTIVE, RED, OFF
		String apText = Controller.getPWMDisplayValue( c.getShort( c.getColumnIndex(StatusTable.COL_AP) ),
		                                               c.getShort( c.getColumnIndex(StatusTable.COL_PWMAO)));
		String dpText = Controller.getPWMDisplayValue( c.getShort( c.getColumnIndex(StatusTable.COL_DP) ),
		                                               c.getShort( c.getColumnIndex(StatusTable.COL_PWMDO)));
		return new String[] {	c.getString( c
										.getColumnIndex( StatusTable.COL_T1 ) ),
								c.getString( c
										.getColumnIndex( StatusTable.COL_T2 ) ),
								c.getString( c
										.getColumnIndex( StatusTable.COL_T3 ) ),
								c.getString( c
										.getColumnIndex( StatusTable.COL_PH ) ),
								dpText,
								apText,
								l,
								h,
								c.getString( c
										.getColumnIndex( StatusTable.COL_SAL ) )
										+ " ppt",
								c.getString( c
										.getColumnIndex( StatusTable.COL_ORP ) )
										+ " mV",
								c.getString( c
										.getColumnIndex( StatusTable.COL_PHE ) ),
								c.getString( c
										.getColumnIndex( StatusTable.COL_WL ) )
										+ "%",
								c.getString( c
										.getColumnIndex( StatusTable.COL_WL1 ) )
										+ "%",
								c.getString( c
								        .getColumnIndex( StatusTable.COL_WL2 ) )
										+ "%",
								c.getString( c
										.getColumnIndex( StatusTable.COL_WL3 ) )
										+ "%",
								c.getString( c
								        .getColumnIndex( StatusTable.COL_WL4 ) )
										+ "%",
								c.getString( c.getColumnIndex( StatusTable.COL_HUM ) )
								        + "%" };
	}

	private String[] getPWMETextValues ( Cursor c ) {
		String[] sa = new String[Controller.MAX_PWM_EXPANSION_PORTS];
		sa[0] = Controller.getPWMDisplayValue( c.getShort( c.getColumnIndex(StatusTable.COL_PWME0) ),
		                                       c.getShort( c.getColumnIndex(StatusTable.COL_PWME0O)));
		sa[1] = Controller.getPWMDisplayValue( c.getShort( c.getColumnIndex(StatusTable.COL_PWME1) ),
		                                       c.getShort( c.getColumnIndex(StatusTable.COL_PWME1O)));
		sa[2] = Controller.getPWMDisplayValue( c.getShort( c.getColumnIndex(StatusTable.COL_PWME2) ),
		                                       c.getShort( c.getColumnIndex(StatusTable.COL_PWME2O)));
		sa[3] = Controller.getPWMDisplayValue( c.getShort( c.getColumnIndex(StatusTable.COL_PWME3) ),
		                                       c.getShort( c.getColumnIndex(StatusTable.COL_PWME3O)));
		sa[4] = Controller.getPWMDisplayValue( c.getShort( c.getColumnIndex(StatusTable.COL_PWME4) ),
		                                       c.getShort( c.getColumnIndex(StatusTable.COL_PWME4O)));
		sa[5] = Controller.getPWMDisplayValue( c.getShort( c.getColumnIndex(StatusTable.COL_PWME5) ),
	                                           c.getShort( c.getColumnIndex(StatusTable.COL_PWME5O)));
		return sa;
	}
	
	private short[] getPWMEValues ( Cursor c ) {
		short[] v = new short[Controller.MAX_PWM_EXPANSION_PORTS];
		v[0] = c.getShort( c.getColumnIndex(StatusTable.COL_PWME0) );
		v[1] = c.getShort( c.getColumnIndex(StatusTable.COL_PWME1) );
		v[2] = c.getShort( c.getColumnIndex(StatusTable.COL_PWME2) );
		v[3] = c.getShort( c.getColumnIndex(StatusTable.COL_PWME3) );
		v[4] = c.getShort( c.getColumnIndex(StatusTable.COL_PWME4) );
		v[5] = c.getShort( c.getColumnIndex(StatusTable.COL_PWME5) );
		return v;
	}

	private String[] getSCPWMETextValues ( Cursor c ) {
		String[] sa = new String[Controller.MAX_SCPWM_EXPANSION_PORTS];
		sa[0] = Controller.getPWMDisplayValue( c.getShort( c.getColumnIndex(StatusTable.COL_SCPWME0) ),
		                                       c.getShort( c.getColumnIndex(StatusTable.COL_SCPWME0O)));
		sa[1] = Controller.getPWMDisplayValue( c.getShort( c.getColumnIndex(StatusTable.COL_SCPWME1) ),
		                                       c.getShort( c.getColumnIndex(StatusTable.COL_SCPWME1O)));
		sa[2] = Controller.getPWMDisplayValue( c.getShort( c.getColumnIndex(StatusTable.COL_SCPWME2) ),
		                                       c.getShort( c.getColumnIndex(StatusTable.COL_SCPWME2O)));
		sa[3] = Controller.getPWMDisplayValue( c.getShort( c.getColumnIndex(StatusTable.COL_SCPWME3) ),
		                                       c.getShort( c.getColumnIndex(StatusTable.COL_SCPWME3O)));
		sa[4] = Controller.getPWMDisplayValue( c.getShort( c.getColumnIndex(StatusTable.COL_SCPWME4) ),
		                                       c.getShort( c.getColumnIndex(StatusTable.COL_SCPWME4O)));
		sa[5] = Controller.getPWMDisplayValue( c.getShort( c.getColumnIndex(StatusTable.COL_SCPWME5) ),
	                                           c.getShort( c.getColumnIndex(StatusTable.COL_SCPWME5O)));
		sa[6] = Controller.getPWMDisplayValue( c.getShort( c.getColumnIndex(StatusTable.COL_SCPWME6) ),
		                                       c.getShort( c.getColumnIndex(StatusTable.COL_SCPWME6O)));
		sa[7] = Controller.getPWMDisplayValue( c.getShort( c.getColumnIndex(StatusTable.COL_SCPWME7) ),
		                                       c.getShort( c.getColumnIndex(StatusTable.COL_SCPWME7O)));
		sa[8] = Controller.getPWMDisplayValue( c.getShort( c.getColumnIndex(StatusTable.COL_SCPWME8) ),
		                                       c.getShort( c.getColumnIndex(StatusTable.COL_SCPWME8O)));
		sa[9] = Controller.getPWMDisplayValue( c.getShort( c.getColumnIndex(StatusTable.COL_SCPWME9) ),
		                                       c.getShort( c.getColumnIndex(StatusTable.COL_SCPWME9O)));
		sa[10] = Controller.getPWMDisplayValue( c.getShort( c.getColumnIndex(StatusTable.COL_SCPWME10) ),
		                                       c.getShort( c.getColumnIndex(StatusTable.COL_SCPWME10O)));
		sa[11] = Controller.getPWMDisplayValue( c.getShort( c.getColumnIndex(StatusTable.COL_SCPWME11) ),
	                                           c.getShort( c.getColumnIndex(StatusTable.COL_SCPWME11O)));
		sa[12] = Controller.getPWMDisplayValue( c.getShort( c.getColumnIndex(StatusTable.COL_SCPWME12) ),
		                                       c.getShort( c.getColumnIndex(StatusTable.COL_SCPWME12O)));
		sa[13] = Controller.getPWMDisplayValue( c.getShort( c.getColumnIndex(StatusTable.COL_SCPWME13) ),
		                                       c.getShort( c.getColumnIndex(StatusTable.COL_SCPWME13O)));
		sa[14] = Controller.getPWMDisplayValue( c.getShort( c.getColumnIndex(StatusTable.COL_SCPWME14) ),
		                                       c.getShort( c.getColumnIndex(StatusTable.COL_SCPWME14O)));
		sa[15] = Controller.getPWMDisplayValue( c.getShort( c.getColumnIndex(StatusTable.COL_SCPWME15) ),
		                                       c.getShort( c.getColumnIndex(StatusTable.COL_SCPWME15O)));
		return sa;
	}
	
	private short[] getSCPWMEValues ( Cursor c ) {
		short[] v = new short[Controller.MAX_SCPWM_EXPANSION_PORTS];
		v[0] = c.getShort( c.getColumnIndex(StatusTable.COL_SCPWME0) );
		v[1] = c.getShort( c.getColumnIndex(StatusTable.COL_SCPWME1) );
		v[2] = c.getShort( c.getColumnIndex(StatusTable.COL_SCPWME2) );
		v[3] = c.getShort( c.getColumnIndex(StatusTable.COL_SCPWME3) );
		v[4] = c.getShort( c.getColumnIndex(StatusTable.COL_SCPWME4) );
		v[5] = c.getShort( c.getColumnIndex(StatusTable.COL_SCPWME5) );
		v[6] = c.getShort( c.getColumnIndex(StatusTable.COL_SCPWME6) );
		v[7] = c.getShort( c.getColumnIndex(StatusTable.COL_SCPWME7) );
		v[8] = c.getShort( c.getColumnIndex(StatusTable.COL_SCPWME8) );
		v[9] = c.getShort( c.getColumnIndex(StatusTable.COL_SCPWME9) );
		v[10] = c.getShort( c.getColumnIndex(StatusTable.COL_SCPWME10) );
		v[11] = c.getShort( c.getColumnIndex(StatusTable.COL_SCPWME11) );
		v[12] = c.getShort( c.getColumnIndex(StatusTable.COL_SCPWME12) );
		v[13] = c.getShort( c.getColumnIndex(StatusTable.COL_SCPWME13) );
		v[14] = c.getShort( c.getColumnIndex(StatusTable.COL_SCPWME14) );
		v[15] = c.getShort( c.getColumnIndex(StatusTable.COL_SCPWME15) );
		return v;
	}
	
	private String[] getRadionTextValues ( Cursor c ) {
		String[] sa = new String[Controller.MAX_RADION_LIGHT_CHANNELS];
		sa[0] = Controller.getPWMDisplayValue( c.getShort( c.getColumnIndex(StatusTable.COL_RFW) ),
		                                       c.getShort( c.getColumnIndex(StatusTable.COL_RFWO)));
		sa[1] = Controller.getPWMDisplayValue( c.getShort( c.getColumnIndex(StatusTable.COL_RFRB) ),
		                                       c.getShort( c.getColumnIndex(StatusTable.COL_RFRBO)));
		sa[2] = Controller.getPWMDisplayValue( c.getShort( c.getColumnIndex(StatusTable.COL_RFR) ),
		                                       c.getShort( c.getColumnIndex(StatusTable.COL_RFRO)));
		sa[3] = Controller.getPWMDisplayValue( c.getShort( c.getColumnIndex(StatusTable.COL_RFG) ),
		                                       c.getShort( c.getColumnIndex(StatusTable.COL_RFGO)));
		sa[4] = Controller.getPWMDisplayValue( c.getShort( c.getColumnIndex(StatusTable.COL_RFB) ),
		                                       c.getShort( c.getColumnIndex(StatusTable.COL_RFBO)));
		sa[5] = Controller.getPWMDisplayValue( c.getShort( c.getColumnIndex(StatusTable.COL_RFI) ),
	                                           c.getShort( c.getColumnIndex(StatusTable.COL_RFIO)));
		return sa;
	}
	
	private short[] getRadionValues ( Cursor c ) {
		short[] v = new short[Controller.MAX_RADION_LIGHT_CHANNELS];
		v[0] = c.getShort( c.getColumnIndex(StatusTable.COL_RFW) );
		v[1] = c.getShort( c.getColumnIndex(StatusTable.COL_RFRB) );
		v[2] = c.getShort( c.getColumnIndex(StatusTable.COL_RFR) );
		v[3] = c.getShort( c.getColumnIndex(StatusTable.COL_RFG) );
		v[4] = c.getShort( c.getColumnIndex(StatusTable.COL_RFB) );
		v[5] = c.getShort( c.getColumnIndex(StatusTable.COL_RFI) );
		return v;
	}

	private String[] getVortechTextValues ( Cursor c ) {
		String[] sa = new String[Controller.MAX_VORTECH_VALUES];
		String s = "";
		int v, mode;
		// mode
		v = c.getInt( c.getColumnIndex( StatusTable.COL_RFM ) );
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
		v = c.getInt( c.getColumnIndex( StatusTable.COL_RFS ) );
		s = String.format( Locale.US, "%d%c", v, '%' );
		sa[Controller.VORTECH_SPEED] = s;
		// duration
		v = c.getInt( c.getColumnIndex( StatusTable.COL_RFD ) );
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
	
	private short[] getVortechValues ( Cursor c ) {
		short[] v = new short[Controller.MAX_VORTECH_VALUES];
		v[Controller.VORTECH_MODE] = c.getShort( c.getColumnIndex( StatusTable.COL_RFM ) );
		v[Controller.VORTECH_SPEED] = c.getShort( c.getColumnIndex( StatusTable.COL_RFS ) );
		v[Controller.VORTECH_DURATION] = c.getShort( c.getColumnIndex( StatusTable.COL_RFD ) );
		return v;
	}

	private String[] getAITextValues ( Cursor c ) {
		String[] sa = new String[Controller.MAX_AI_CHANNELS];
		sa[Controller.AI_WHITE] = Controller.getPWMDisplayValue( c.getShort( c.getColumnIndex(StatusTable.COL_AIW) ),
		                                       c.getShort( c.getColumnIndex(StatusTable.COL_AIWO)));
		sa[Controller.AI_BLUE] = Controller.getPWMDisplayValue( c.getShort( c.getColumnIndex(StatusTable.COL_AIB) ),
		                                       c.getShort( c.getColumnIndex(StatusTable.COL_AIBO)));
		sa[Controller.AI_ROYALBLUE] = Controller.getPWMDisplayValue( c.getShort( c.getColumnIndex(StatusTable.COL_AIRB) ),
		                                       c.getShort( c.getColumnIndex(StatusTable.COL_AIRBO)));
		return sa;
	}
	
	private short[] getAIValues ( Cursor c ) {
		short[] v = new short[Controller.MAX_AI_CHANNELS];
		v[0] = c.getShort( c.getColumnIndex(StatusTable.COL_AIW) );
		v[1] = c.getShort( c.getColumnIndex(StatusTable.COL_AIB) );
		v[2] = c.getShort( c.getColumnIndex(StatusTable.COL_AIRB) );
		return v;
	}

	private String[] getIOValues ( Cursor c ) {
		String[] sa = new String[Controller.MAX_IO_CHANNELS];
		short io = c.getShort( c.getColumnIndex( StatusTable.COL_IO ) );
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
		return new String[] {	c.getString( c
										.getColumnIndex( StatusTable.COL_C0 ) ),
								c.getString( c
										.getColumnIndex( StatusTable.COL_C1 ) ),
								c.getString( c
										.getColumnIndex( StatusTable.COL_C2 ) ),
								c.getString( c
										.getColumnIndex( StatusTable.COL_C3 ) ),
								c.getString( c
										.getColumnIndex( StatusTable.COL_C4 ) ),
								c.getString( c
										.getColumnIndex( StatusTable.COL_C5 ) ),
								c.getString( c
										.getColumnIndex( StatusTable.COL_C6 ) ),
								c.getString( c
										.getColumnIndex( StatusTable.COL_C7 ) ) };
	}

	private void checkDeviceModules ( short newEM, short newEM1, short newREM ) {
		// FIXME fix preference setting functions
		boolean fReload = false;
		short oldEM = (short) rapp.raprefs.getPreviousEM();
		Log.d( TAG, "EM: Old: " + oldEM + " New: " + newEM );
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
			rapp.raprefs.set( R.string.prefExpAIEnableKey, f );
			if ( Controller.isDimmingModuleInstalled( newEM ) )
				f = true;
			else
				f = false;
			Log.d( TAG, "Dimming: " + f );
			rapp.raprefs.set( R.string.prefExpDimmingEnableKey, f );
			if ( Controller.isIOModuleInstalled( newEM ) )
				f = true;
			else
				f = false;
			Log.d( TAG, "IO: " + f );
			rapp.raprefs.set( R.string.prefExpIOEnableKey, f );
			if ( Controller.isORPModuleInstalled( newEM ) )
				f = true;
			else
				f = false;
			Log.d( TAG, "ORP: " + f );
			rapp.raprefs.set( R.string.prefORPVisibilityKey, f );
			if ( Controller.isPHExpansionModuleInstalled( newEM ) )
				f = true;
			else
				f = false;
			Log.d( TAG, "PHE: " + f );
			rapp.raprefs.set( R.string.prefPHExpVisibilityKey, f );
			if ( Controller.isRFModuleInstalled( newEM ) )
				f = true;
			else
				f = false;
			Log.d( TAG, "RF: " + f );
			rapp.raprefs.set( R.string.prefExpRadionEnableKey, f );
			rapp.raprefs.set( R.string.prefExpVortechEnableKey, f );
			if ( Controller.isSalinityModuleInstalled( newEM ) )
				f = true;
			else
				f = false;
			Log.d( TAG, "Salinity: " + f );
			rapp.raprefs.set( R.string.prefSalinityVisibilityKey, f );
			if ( Controller.isWaterLevelModuleInstalled( newEM ) )
				f = true;
			else
				f = false;
			Log.d( TAG, "WATER: " + f );
			String key;
			for ( int i = 0; i < Controller.MAX_WATERLEVEL_PORTS; i++ ) {
				key = "wl";
				if ( i > 0 ) key += i;
				key += "_visibility";
				rapp.raprefs.set( key, f );
			}

			// update the previous settings to the new ones after we change
			rapp.raprefs.setPreviousEM( newEM );
		}

		short oldEM1 = (short) rapp.raprefs.getPreviousEM1();
		Log.d( TAG, "EM1: Old: " + oldEM1 + " New: " + newEM1 );
		if ( oldEM1 != newEM1 ) {
			boolean f = false;
			if ( Controller.isHumidityModuleInstalled( newEM1 ) )
				f = true;
			else
				f = false;
			Log.d(TAG, "Humidity: " + f);
			// TODO finish setting EM1 modules
			rapp.raprefs.set( R.string.prefHumidityVisibilityKey, f );
			
			if ( Controller.isDCPumpControlModuleInstalled( newEM1 ) )
				f = true;
			else
				f = false;
			Log.d(TAG, "DCPump: " + f);
			//rapp.raprefs.set( R.string.prefExpDCPumpEnableKey, f );
			
			if ( Controller.isLeakDetectorModuleInstalled( newEM1 ) ) 
				f = true;
			else
				f = false;
			Log.d(TAG, "Leak Detector: " + f);
			//rapp.raprefs.set( R.string.prefExpLeakDetectorEnableKey, f );
			
			rapp.raprefs.setPreviousEM1( newEM1 );
		}
		
		int newRQty = Controller.getRelayExpansionModulesInstalled( newREM );
		int oldRQty = rapp.raprefs.getExpansionRelayQuantity();
		Log.d( TAG, "Old Qty: " + oldRQty + " New Qty: " + newRQty );
		if ( oldRQty != newRQty ) {
			// expansion relay modules different
			// set flag to reload the pages
			fReload = true;
			// set the installed relays in the preferences
			Log.d( TAG, "Relays: " + newRQty );
			rapp.raprefs.set(	R.string.prefExpQtyKey,
								Integer.toString( newRQty ) );
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
		Intent i = null;
		switch ( item.getItemId() ) {
			case R.id.refresh:
				// launch the update
				launchStatusTask();
				return true;
			case R.id.settings:
				// launch settings
				reloadPages();
				i = new Intent( this, PrefsActivity.class );
				break;
			case R.id.about:
				// launch about box
				i = new Intent( this, AboutActivity.class );
				break;
			case R.id.history:
				i = new Intent( this, FragmentListActivity.class );
				i.putExtra( FragmentListActivity.FRAG_TYPE,
							FragmentListActivity.HISTORY );
				break;
			case R.id.errors:
				i = new Intent( this, FragmentListActivity.class );
				i.putExtra( FragmentListActivity.FRAG_TYPE,
							FragmentListActivity.ERRORS );
				break;
			case R.id.notifications:
				i = new Intent( this, FragmentListActivity.class );
				i.putExtra( FragmentListActivity.FRAG_TYPE,
							FragmentListActivity.NOTIFICATIONS );
				break;
			case R.id.memory:
				// launch memory
				i = new Intent( this, MemoryTabsActivity.class );
				i.putExtra( Globals.PRE10_LOCATIONS,
							rapp.raprefs.useOldPre10MemoryLocations() );
				break;
			case R.id.datetime:
				// launch date & time
				i = new Intent( this, DateTimeActivity.class );
				break;
			default:
				return super.onOptionsItemSelected( item );
		}
		if ( i != null ) {
			startActivity( i );
		}
		return true;
	}

	private void setPagerPrefs ( ) {
		pagerAdapter = new CustomPagerAdapter();
		pager.setAdapter( pagerAdapter );
		// Set the minimum pages to keep loaded
		// will set to minimum pages since the pages are not complex
		pager.setOffscreenPageLimit( MIN_PAGES );
	}

	private void updatePageOrder ( ) {
		// updates the order of the pages for display
		int i, j;
		int qty = rapp.raprefs.getExpansionRelayQuantity();
		// loop through all the possible pages
		// keep track of the pages installed compared to total pages
		// if the module is enabled, add it to the available pages list
		// then increment the installed pages counter
		for ( i = POS_START, j = POS_START; i <= POS_END; i++ ) {
			switch ( i ) {
				case POS_COMMANDS:
					// Log.d( TAG, j + ": Commands" );
					appPages[j] = pageCommands;
					j++;
					break;
				case POS_FLAGS:
					appPages[j] = pageFlags;
					j++;
					break;
				case POS_CONTROLLER:
					// Log.d( TAG, j + ": Controller" );
					appPages[j] = pageController;
					j++;
					break;
				case POS_DIMMING:
					if ( rapp.raprefs.getDimmingModuleEnabled() ) {
						// Log.d( TAG, j + ": Dimming" );
						appPages[j] = pageDimming;
						j++;
					}
					break;
				case POS_SC_DIMMING:
					if ( rapp.raprefs.getSCDimmingModuleEnabled() ) {
						// Log.d( TAG, j + ": SCDimming" );
						appPages[j] = pageSCDimming;
						j++;
					}
					break;
				case POS_RADION:
					if ( rapp.raprefs.getRadionModuleEnabled() ) {
						// Log.d( TAG, j + ": Radion" );
						appPages[j] = pageRadion;
						j++;
					}
					break;
				case POS_VORTECH:
					if ( rapp.raprefs.getVortechModuleEnabled() ) {
						// Log.d( TAG, j + ": Vortech" );
						appPages[j] = pageVortech;
						j++;
					}
					break;
				case POS_AI:
					if ( rapp.raprefs.getAIModuleEnabled() ) {
						// Log.d( TAG, j + ": AI" );
						appPages[j] = pageAI;
						j++;
					}
					break;
				case POS_IO:
					if ( rapp.raprefs.getIOModuleEnabled() ) {
						// Log.d( TAG, j + ": IO" );
						appPages[j] = pageIO;
						j++;
					}
					break;
				case POS_CUSTOM:
					if ( rapp.raprefs.getCustomModuleEnabled() ) {
						// Log.d( TAG, j + ": Custom" );
						appPages[j] = pageCustom;
						j++;
					}
					break;
				case POS_MAIN_RELAY:
					// Log.d( TAG, j + ": Main Relay" );
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
							// Log.d( TAG, j + ": Exp Relay " + relay );
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
		// private final String TAG = CustomPagerAdapter.class.getSimpleName();

		public int getCount ( ) {
			int qty =
					MIN_PAGES + rapp.raprefs.getTotalInstalledModuleQuantity();
			return qty;
		}

		public CharSequence getPageTitle ( int position ) {
			return ((RAPage) appPages[position]).getPageTitle();
		}

		public void destroyItem (
				ViewGroup container,
				int position,
				Object object ) {
			((ViewPager) container).removeView( (View) object );
		}

		public Object instantiateItem ( ViewGroup container, int position ) {
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
