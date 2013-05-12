/*
 * Copyright (c) 2011-2013 by Curt Binder (http://curtbinder.info)
 * 
 * This work is made available under the terms of the Creative Commons
 * Attribution-NonCommercial-ShareAlike 3.0 Unported License
 * http://creativecommons.org/licenses/by-nc-sa/3.0/
 */

package info.curtbinder.reefangel.phone;

import info.curtbinder.reefangel.controller.Controller;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;

public class RAPreferences {

	private RAApplication rapp;
	private SharedPreferences prefs;
	// Devices stuff
	private String[] devicesArray;
	// Relay labels
	private int[][] relayLabels;
	private int[] relayDefaultLabels;

	public RAPreferences ( RAApplication ra ) {
		this.rapp = ra;
		prefs = PreferenceManager.getDefaultSharedPreferences( rapp );
		devicesArray =
				rapp.getResources().getStringArray( R.array.devicesValues );
		fillRelayLabels();
	}

	// Generic interface
	public void set ( String key, String value ) {
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString( key, value );
		editor.commit();
	}

	public void set ( int keyid, String value ) {
		set( rapp.getString( keyid ), value );
	}

	public void set ( int keyid, boolean value ) {
		SharedPreferences.Editor editor = prefs.edit();
		editor.putBoolean( rapp.getString( keyid ), value ).commit();
	}

	public void set ( String key, int value ) {
		SharedPreferences.Editor editor = prefs.edit();
		editor.putInt( key, value );
		editor.commit();
	}

	public void set ( int keyid, int value ) {
		set( rapp.getString( keyid ), value );
	}

	public int getInt ( int keyid, int defValue ) {
		return prefs.getInt( rapp.getString( keyid ), defValue );
	}

	public boolean getBoolean ( int keyid, boolean defValue ) {
		return prefs.getBoolean( rapp.getString( keyid ), defValue );
	}

	public boolean getBoolean ( String key, boolean defValue ) {
		return prefs.getBoolean( key, defValue );
	}

	public String getString ( int keyid, String defValue ) {
		return prefs.getString( rapp.getString( keyid ), defValue );
	}

	public String getString ( int keyid, int defValueId ) {
		return prefs.getString( rapp.getString( keyid ),
								rapp.getString( defValueId ) );
	}

	public void deletePref ( int keyid ) {
		deletePref( rapp.getString( keyid ) );
	}

	public void deletePref ( String key ) {
		SharedPreferences.Editor editor = prefs.edit();
		editor.remove( key );
		editor.commit();
	}

	// App Preferences
	public int getPreviousCodeVersion ( ) {
		return getInt( R.string.prefPreviousCodeVersionKey, 0 );
	}

	public void setPreviousCodeVersion ( int version ) {
		set( R.string.prefPreviousCodeVersionKey, version );
	}

	public boolean isFirstRun ( ) {
		return getBoolean( R.string.prefFirstRunKey, true );
	}

	public void disableFirstRun ( ) {
		set( R.string.prefFirstRunKey, false );
	}

	public void clearFirstRun ( ) {
		deletePref( R.string.prefFirstRunKey );
	}

	public boolean useOld085xExpansionRelays ( ) {
		return getBoolean( R.string.prefExp085xKey, false );
	}

	public boolean useOldPre10MemoryLocations ( ) {
		return getBoolean( R.string.prefPre10MemoryKey, true );
	}

	// Automatic Updating
	public long getUpdateInterval ( ) {
		return Long
				.parseLong( getString(	R.string.prefAutoUpdateIntervalKey,
										R.string.prefAutoUpdateIntervalDefault ) );
	}

	public int getUpdateProfile ( ) {
		return Integer
				.parseInt( getString(	R.string.prefAutoUpdateProfileKey,
										R.string.prefAutoUpdateProfileDefault ) );
	}

	// Logging
	public boolean isLoggingEnabled ( ) {
		return getBoolean( R.string.prefLoggingEnableKey, false );
	}

	public boolean isLoggingAppendFile ( ) {
		int i = getLoggingUpdateValue();
		boolean f = false;
		if ( i == Globals.logAppend )
			f = true;
		return f;
	}

	public int getLoggingUpdateValue ( ) {
		return Integer
				.parseInt( getString( R.string.prefLoggingUpdateKey, "0" ) );
	}

	// Notifications
	public int getNotificationErrorRetryMax ( ) {
		return Integer
				.parseInt( getString(	R.string.prefNotificationErrorRetryKey,
										"0" ) );
	}

	public long getNotificationErrorRetryInterval ( ) {
		return Long
				.parseLong( getString(	R.string.prefNotificationErrorRetryIntervalKey,
										R.string.prefNotificationErrorRetryIntervalDefault ) );
	}

	public boolean isErrorRetryEnabled ( ) {
		return (getNotificationErrorRetryMax() > Globals.errorRetryNone);
	}

	public boolean isNotificationEnabled ( ) {
		return getBoolean( R.string.prefNotificationEnableKey, true );
	}

	public Uri getNotificationSound ( ) {
		String s =
				getString(	R.string.prefNotificationSoundKey,
							"content://settings/system/notification_sound" );
		return Uri.parse( s );
	}

	// Profiles
	public int getSelectedProfile ( ) {
		return Integer
				.parseInt( getString(	R.string.prefProfileSelectedKey,
										R.string.prefProfileSelectedDefault ) );
	}

	public void setSelectedProfile ( int profile ) {
		String s = "" + profile;
		// Log.d( TAG, "Changed Profile: " + s );
		set( R.string.prefProfileSelectedKey, s );
	}

	// Host

	public boolean isMainHostSet ( ) {
		boolean fHost = true;
		String host = getString( R.string.prefHostKey, "" );
		if ( host.equals( "" ) )
			fHost = false;
		return fHost;
	}

	public boolean isAwayHostSet ( ) {
		// get away host, compare to empty host
		// default away host is empty host
		boolean fHost = true;
		String host = getAwayHost();
		if ( host.equals( "" ) )
			fHost = false;
		return fHost;
	}

	// TODO convert Host & Port functions to allow for multiple hosts
	public String getHost ( ) {
		int profile = getSelectedProfile();
		if ( profile == 1 ) {
			// Away profile
			if ( isAwayHostSet() ) {
				// away profile is filled in and enabled
				// return away profile
				return getAwayHost();
			}
		}
		return getHomeHost();
	}

	public void setHost ( String host ) {
		set( R.string.prefHostKey, host );
	}

	public String getPort ( ) {
		int profile = getSelectedProfile();
		if ( profile == Globals.profileAway ) {
			// Away profile
			if ( isAwayHostSet() ) {
				// away profile is filled in and enabled
				// return away profile
				return getAwayPort();
			}
		}
		return getHomePort();
	}

	public void setPort ( String port ) {
		set( R.string.prefPortKey, port );
	}

	public String getHomeHost ( ) {
		return getString( R.string.prefHostKey, R.string.prefHostHomeDefault );
	}

	public String getHomePort ( ) {
		return getString( R.string.prefPortKey, R.string.prefPortDefault );
	}

	public String getAwayHost ( ) {
		return getString(	R.string.prefHostAwayKey,
							R.string.prefHostAwayDefault );
	}

	public String getAwayPort ( ) {
		return getString( R.string.prefPortAwayKey, R.string.prefPortDefault );
	}

	public boolean isCommunicateController ( ) {
		boolean b = false;
		if ( getDevice().equals( devicesArray[0] ) ) {
			b = true;
		}
		return b;
	}

	public String getDevice ( ) {
		return getString( R.string.prefDeviceKey, R.string.prefDeviceDefault );
	}

	public String getUserId ( ) {
		return getString( R.string.prefUserIdKey, R.string.prefUserIdDefault );
	}

	public void setUserId ( String userId ) {
		set( R.string.prefUserIdKey, userId );
	}

	// Controller Information
	public boolean getT2Visibility ( ) {
		return getBoolean( R.string.prefT2VisibilityKey, true );
	}

	public boolean getT3Visibility ( ) {
		return getBoolean( R.string.prefT3VisibilityKey, true );
	}

	public boolean getDPVisibility ( ) {
		return getBoolean( R.string.prefDPVisibilityKey, true );
	}

	public boolean getAPVisibility ( ) {
		return getBoolean( R.string.prefAPVisibilityKey, true );
	}

	public boolean getPHVisibility ( ) {
		return getBoolean( R.string.prefPHVisibilityKey, true );
	}
	
	public boolean getAtoLowVisibility ( ) {
		return getBoolean( R.string.prefATOLoVisibilityKey, true );
	}
	
	public boolean getAtoHighVisibility ( ) {
		return getBoolean( R.string.prefATOHiVisibilityKey, true );
	}

	public boolean getSalinityVisibility ( ) {
		return getBoolean( R.string.prefSalinityVisibilityKey, false );
	}

	public boolean getORPVisibility ( ) {
		return getBoolean( R.string.prefORPVisibilityKey, false );
	}

	public boolean getPHExpVisibility ( ) {
		return getBoolean( R.string.prefPHExpVisibilityKey, false );
	}

	public boolean getWaterLevelVisibility ( ) {
		return getBoolean( R.string.prefWaterLevelVisibilityKey, false );
	}

	public String getT1Label ( ) {
		return getString( R.string.prefT1LabelKey, R.string.labelTemp1 );
	}

	public String getT2Label ( ) {
		return getString( R.string.prefT2LabelKey, R.string.labelTemp2 );
	}

	public String getT3Label ( ) {
		return getString( R.string.prefT3LabelKey, R.string.labelTemp3 );
	}

	public String getPHLabel ( ) {
		return getString( R.string.prefPHLabelKey, R.string.labelPH );
	}

	public String getDPLabel ( ) {
		return getString( R.string.prefDPLabelKey, R.string.labelDP );
	}

	public String getAPLabel ( ) {
		return getString( R.string.prefAPLabelKey, R.string.labelAP );
	}

	public String getAtoLowLabel ( ) {
		return getString( R.string.prefATOLoLabelKey, R.string.labelAtoLow );
	}
	
	public String getAtoHighLabel ( ) {
		return getString( R.string.prefATOHiLabelKey, R.string.labelAtoHigh );
	}
	
	public String getSalinityLabel ( ) {
		return getString( R.string.prefSalinityLabelKey, R.string.labelSalinity );
	}

	public String getORPLabel ( ) {
		return getString( R.string.prefORPLabelKey, R.string.labelORP );
	}

	public String getPHExpLabel ( ) {
		return getString( R.string.prefPHExpLabelKey, R.string.labelPHExp );
	}

	public String getWaterLevelLabel ( ) {
		return getString(	R.string.prefWaterLevelLabelKey,
							R.string.labelWaterLevel );
	}

	// Relay Labels
	public String getMainRelayLabel ( int port ) {
		return getRelayLabel( 0, port );
	}

	public String getRelayLabel ( int relay, int port ) {
		return getString( relayLabels[relay][port], relayDefaultLabels[port] );
	}

	public void setRelayLabel ( int relay, int port, String label ) {
		set( relayLabels[relay][port], label );
	}

	// TODO getRelayKey used in PrefsActivity
	public int getRelayKey ( int relay, int port ) {
		return relayLabels[relay][port];
	}

	public int getExpansionRelayQuantity ( ) {
		return Integer.parseInt( getString( R.string.prefExpQtyKey, "0" ) );
	}

	// Expansion Modules
	public int getPreviousEM ( ) {
		return getInt( R.string.prefPreviousEMKey, -1 );
	}

	public void setPreviousEM ( short em ) {
		set( rapp.getString( R.string.prefPreviousEMKey ), em );
	}

	public boolean isAutoUpdateModulesEnabled ( ) {
		return getBoolean( R.string.prefAutoUpdateModulesKey, true );
	}

	public int getTotalInstalledModuleQuantity ( ) {
		// this function gets all the installed modules for the controller
		// that are displayed on their own separate pages
		// the modules include:
		// expansion relays, dimming, vortech, radion, ai, custom, io
		int total = 0;
		total += getExpansionRelayQuantity();
		total += getInstalledModuleQuantity();
		return total;
	}

	public int getInstalledModuleQuantity ( ) {
		// returns the total installed modules
		int total = 0;
		if ( getDimmingModuleEnabled() )
			total++;
		if ( getRadionModuleEnabled() )
			total++;
		if ( getVortechModuleEnabled() )
			total++;
		if ( getAIModuleEnabled() )
			total++;
		if ( getIOModuleEnabled() )
			total++;
		if ( getCustomModuleEnabled() )
			total++;
		return total;
	}

	public boolean getDimmingModuleEnabled ( ) {
		return getBoolean( R.string.prefExpDimmingEnableKey, false );
	}

	// FIXME improve function
	public String getDimmingModuleChannelLabel ( int channel ) {
		int k, v;
		switch ( channel ) {
			default:
			case 0:
				k = R.string.prefExpDimmingCh0LabelKey;
				v = R.string.prefExpDimmingCh0LabelTitle;
				break;
			case 1:
				k = R.string.prefExpDimmingCh1LabelKey;
				v = R.string.prefExpDimmingCh1LabelTitle;
				break;
			case 2:
				k = R.string.prefExpDimmingCh2LabelKey;
				v = R.string.prefExpDimmingCh2LabelTitle;
				break;
			case 3:
				k = R.string.prefExpDimmingCh3LabelKey;
				v = R.string.prefExpDimmingCh3LabelTitle;
				break;
			case 4:
				k = R.string.prefExpDimmingCh4LabelKey;
				v = R.string.prefExpDimmingCh4LabelTitle;
				break;
			case 5:
				k = R.string.prefExpDimmingCh5LabelKey;
				v = R.string.prefExpDimmingCh5LabelTitle;
				break;
		}
		return getString( k, v );
	}

	// FIXME improve function
	public void setDimmingModuleChannelLabel ( int channel, String label ) {
		int k;
		switch ( channel ) {
			default:
			case 0:
				k = R.string.prefExpDimmingCh0LabelKey;
				break;
			case 1:
				k = R.string.prefExpDimmingCh1LabelKey;
				break;
			case 2:
				k = R.string.prefExpDimmingCh2LabelKey;
				break;
			case 3:
				k = R.string.prefExpDimmingCh3LabelKey;
				break;
			case 4:
				k = R.string.prefExpDimmingCh4LabelKey;
				break;
			case 5:
				k = R.string.prefExpDimmingCh5LabelKey;
				break;
		}
		set( k, label );
	}

	public boolean getRadionModuleEnabled ( ) {
		return getBoolean( R.string.prefExpRadionEnableKey, false );
	}

	public boolean getVortechModuleEnabled ( ) {
		return getBoolean( R.string.prefExpVortechEnableKey, false );
	}

	public boolean getAIModuleEnabled ( ) {
		return getBoolean( R.string.prefExpAIEnableKey, false );
	}

	public boolean getIOModuleEnabled ( ) {
		return getBoolean( R.string.prefExpIOEnableKey, false );
	}

	// FIXME improve function
	public String getIOModuleChannelLabel ( int channel ) {
		int k, v;
		switch ( channel ) {
			default:
			case 0:
				k = R.string.prefExpIO0LabelKey;
				v = R.string.prefExpIO0LabelTitle;
				break;
			case 1:
				k = R.string.prefExpIO1LabelKey;
				v = R.string.prefExpIO1LabelTitle;
				break;
			case 2:
				k = R.string.prefExpIO2LabelKey;
				v = R.string.prefExpIO2LabelTitle;
				break;
			case 3:
				k = R.string.prefExpIO3LabelKey;
				v = R.string.prefExpIO3LabelTitle;
				break;
			case 4:
				k = R.string.prefExpIO4LabelKey;
				v = R.string.prefExpIO4LabelTitle;
				break;
			case 5:
				k = R.string.prefExpIO5LabelKey;
				v = R.string.prefExpIO5LabelTitle;
				break;
		}
		return getString( k, v );
	}

	// FIXME improve function
	public void setIOModuleChannelLabel ( int channel, String label ) {
		int k;
		switch ( channel ) {
			default:
			case 0:
				k = R.string.prefExpIO0LabelKey;
				break;
			case 1:
				k = R.string.prefExpIO1LabelKey;
				break;
			case 2:
				k = R.string.prefExpIO2LabelKey;
				break;
			case 3:
				k = R.string.prefExpIO3LabelKey;
				break;
			case 4:
				k = R.string.prefExpIO4LabelKey;
				break;
			case 5:
				k = R.string.prefExpIO5LabelKey;
				break;
		}
		set( k, label );
	}

	public boolean getCustomModuleEnabled ( ) {
		return getBoolean( R.string.prefExpCustomEnableKey, false );
	}

	// FIXME improve function
	public String getCustomModuleChannelLabel ( int channel ) {
		int k, d;
		switch ( channel ) {
			default:
			case 0:
				k = R.string.prefExpCustom0LabelKey;
				d = R.string.prefExpCustom0LabelTitle;
				break;
			case 1:
				k = R.string.prefExpCustom1LabelKey;
				d = R.string.prefExpCustom1LabelTitle;
				break;
			case 2:
				k = R.string.prefExpCustom2LabelKey;
				d = R.string.prefExpCustom2LabelTitle;
				break;
			case 3:
				k = R.string.prefExpCustom3LabelKey;
				d = R.string.prefExpCustom3LabelTitle;
				break;
			case 4:
				k = R.string.prefExpCustom4LabelKey;
				d = R.string.prefExpCustom4LabelTitle;
				break;
			case 5:
				k = R.string.prefExpCustom5LabelKey;
				d = R.string.prefExpCustom5LabelTitle;
				break;
			case 6:
				k = R.string.prefExpCustom6LabelKey;
				d = R.string.prefExpCustom6LabelTitle;
				break;
			case 7:
				k = R.string.prefExpCustom7LabelKey;
				d = R.string.prefExpCustom7LabelTitle;
				break;
		}
		return getString( k, d );
	}

	// FIXME improve function
	public void setCustomModuleChannelLabel ( int channel, String label ) {
		int k;
		switch ( channel ) {
			default:
			case 0:
				k = R.string.prefExpCustom0LabelKey;
				break;
			case 1:
				k = R.string.prefExpCustom1LabelKey;
				break;
			case 2:
				k = R.string.prefExpCustom2LabelKey;
				break;
			case 3:
				k = R.string.prefExpCustom3LabelKey;
				break;
			case 4:
				k = R.string.prefExpCustom4LabelKey;
				break;
			case 5:
				k = R.string.prefExpCustom5LabelKey;
				break;
			case 6:
				k = R.string.prefExpCustom6LabelKey;
				break;
			case 7:
				k = R.string.prefExpCustom7LabelKey;
				break;
		}
		set( k, label );
	}

	private String getRelayControlEnabledKey ( int relay, int port ) {
		String s;
		if ( relay == 0 ) {
			s = "prefMainPort";
		} else {
			s = "prefExp" + relay + "Port";
		}
		return s + (port + 1) + "EnabledKey";
	}

	public boolean getRelayControlEnabled ( int relay, int port ) {
		return getBoolean( getRelayControlEnabledKey( relay, port ), true );
	}

	public boolean getMainRelayControlEnabled ( int port ) {
		return getRelayControlEnabled( 0, port );
	}

	public void deleteRelayControlEnabledPorts ( ) {
		for ( int i = 0; i <= Controller.MAX_EXPANSION_RELAYS; i++ ) {
			for ( int j = 0; j < Controller.MAX_RELAY_PORTS; j++ ) {
				deletePref( getRelayControlEnabledKey( i, j ) );
			}
		}
	}

	protected void fillRelayLabels ( ) {
		relayLabels =
				new int[][] {	{	R.string.prefMainPort1LabelKey,
									R.string.prefMainPort2LabelKey,
									R.string.prefMainPort3LabelKey,
									R.string.prefMainPort4LabelKey,
									R.string.prefMainPort5LabelKey,
									R.string.prefMainPort6LabelKey,
									R.string.prefMainPort7LabelKey,
									R.string.prefMainPort8LabelKey },
								{	R.string.prefExp1Port1LabelKey,
									R.string.prefExp1Port2LabelKey,
									R.string.prefExp1Port3LabelKey,
									R.string.prefExp1Port4LabelKey,
									R.string.prefExp1Port5LabelKey,
									R.string.prefExp1Port6LabelKey,
									R.string.prefExp1Port7LabelKey,
									R.string.prefExp1Port8LabelKey },
								{	R.string.prefExp2Port1LabelKey,
									R.string.prefExp2Port2LabelKey,
									R.string.prefExp2Port3LabelKey,
									R.string.prefExp2Port4LabelKey,
									R.string.prefExp2Port5LabelKey,
									R.string.prefExp2Port6LabelKey,
									R.string.prefExp2Port7LabelKey,
									R.string.prefExp2Port8LabelKey },
								{	R.string.prefExp3Port1LabelKey,
									R.string.prefExp3Port2LabelKey,
									R.string.prefExp3Port3LabelKey,
									R.string.prefExp3Port4LabelKey,
									R.string.prefExp3Port5LabelKey,
									R.string.prefExp3Port6LabelKey,
									R.string.prefExp3Port7LabelKey,
									R.string.prefExp3Port8LabelKey },
								{	R.string.prefExp4Port1LabelKey,
									R.string.prefExp4Port2LabelKey,
									R.string.prefExp4Port3LabelKey,
									R.string.prefExp4Port4LabelKey,
									R.string.prefExp4Port5LabelKey,
									R.string.prefExp4Port6LabelKey,
									R.string.prefExp4Port7LabelKey,
									R.string.prefExp4Port8LabelKey },
								{	R.string.prefExp5Port1LabelKey,
									R.string.prefExp5Port2LabelKey,
									R.string.prefExp5Port3LabelKey,
									R.string.prefExp5Port4LabelKey,
									R.string.prefExp5Port5LabelKey,
									R.string.prefExp5Port6LabelKey,
									R.string.prefExp5Port7LabelKey,
									R.string.prefExp5Port8LabelKey },
								{	R.string.prefExp6Port1LabelKey,
									R.string.prefExp6Port2LabelKey,
									R.string.prefExp6Port3LabelKey,
									R.string.prefExp6Port4LabelKey,
									R.string.prefExp6Port5LabelKey,
									R.string.prefExp6Port6LabelKey,
									R.string.prefExp6Port7LabelKey,
									R.string.prefExp6Port8LabelKey },
								{	R.string.prefExp7Port1LabelKey,
									R.string.prefExp7Port2LabelKey,
									R.string.prefExp7Port3LabelKey,
									R.string.prefExp7Port4LabelKey,
									R.string.prefExp7Port5LabelKey,
									R.string.prefExp7Port6LabelKey,
									R.string.prefExp7Port7LabelKey,
									R.string.prefExp7Port8LabelKey },
								{	R.string.prefExp8Port1LabelKey,
									R.string.prefExp8Port2LabelKey,
									R.string.prefExp8Port3LabelKey,
									R.string.prefExp8Port4LabelKey,
									R.string.prefExp8Port5LabelKey,
									R.string.prefExp8Port6LabelKey,
									R.string.prefExp8Port7LabelKey,
									R.string.prefExp8Port8LabelKey } };
		relayDefaultLabels =
				new int[] { R.string.labelPort1,
							R.string.labelPort2,
							R.string.labelPort3,
							R.string.labelPort4,
							R.string.labelPort5,
							R.string.labelPort6,
							R.string.labelPort7,
							R.string.labelPort8 };
	}
}
