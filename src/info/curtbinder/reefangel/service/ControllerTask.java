/*
 * Copyright (c) 2011-2013 by Curt Binder (http://curtbinder.info)
 * 
 * This work is made available under the terms of the Creative Commons
 * Attribution-NonCommercial-ShareAlike 3.0 Unported License
 * http://creativecommons.org/licenses/by-nc-sa/3.0/
 */

package info.curtbinder.reefangel.service;

import info.curtbinder.reefangel.controller.Controller;
import info.curtbinder.reefangel.controller.Relay;
import info.curtbinder.reefangel.db.StatusProvider;
import info.curtbinder.reefangel.db.StatusTable;
import info.curtbinder.reefangel.phone.Globals;
import info.curtbinder.reefangel.phone.Permissions;
import info.curtbinder.reefangel.phone.R;
import info.curtbinder.reefangel.phone.RAApplication;
import info.curtbinder.reefangel.phone.RAPreferences;

import java.io.IOException;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.SocketTimeoutException;
import java.net.URL;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

public class ControllerTask implements Runnable {

	private static final String TAG = ControllerTask.class.getSimpleName();
	private final Host host;
	private final RAApplication rapp;
	private final RAPreferences raprefs;

	ControllerTask ( RAApplication rapp, Host host ) {
		this.rapp = rapp;
		this.host = host;
		this.raprefs = rapp.raprefs;
	}

	public void run ( ) {
		// Communicate with controller

		// clear out the error code on run
		rapp.errorCode = 0;
		HttpURLConnection con = null;
		String res = "";
		broadcastUpdateStatus( R.string.statusStart );
		try {
			URL url = new URL( host.toString() );
			con = setupConnection(url);
			if ( host.isDeviceAuthenticationEnabled() ) {
				String basicAuth = "Basic " +
					Base64.encodeBytes( host.getDeviceAuthenticationString().getBytes() );
				Log.d(TAG, "Auth: " + basicAuth);
				con.setRequestProperty( "Authorization", basicAuth );
			}
			broadcastUpdateStatus( R.string.statusConnect );
			con.connect();

			if ( Thread.interrupted() )
				throw new InterruptedException();

		} catch ( MalformedURLException e ) {
			rapp.error( 1, e, "MalformedURLException" );
		} catch ( ProtocolException e ) {
			rapp.error( 1, e, "ProtocolException" );
		} catch ( SocketTimeoutException e ) {
			rapp.error( 5, e, "SocketTimeoutException" );
		} catch ( ConnectException e ) {
			rapp.error( 3, e, "ConnectException" );
		} catch ( IOException e ) {
			rapp.error( 1, e, "IOException" );
		} catch ( InterruptedException e ) {
			res =
					(String) rapp.getResources()
							.getText( R.string.messageCancelled );
		}

		// check if there was an error
		if ( rapp.errorCode > 0 ) {
			// encountered an error, display an error on screen
			if ( (host.getCommand().equals( RequestCommands.Reboot ))
					&& (rapp.errorCode == 15) ) {
				// if we get a timeout after sending this command, the
				// controller does not support the command
				broadcastUpdateStatus( R.string.messageUnsupported );
			} else {
				broadcastErrorMessage();
			}
		} else if ( res.equals( (String) rapp.getResources()
				.getText( R.string.messageCancelled ) ) ) {
			// Interrupted
			broadcastUpdateStatus( R.string.messageCancelled );
		} else {
			XMLHandler xml = new XMLHandler();
			if ( raprefs.useOld085xExpansionRelays() ) {
				xml.setOld085xExpansion( true );
			}
			if ( !parseXML( xml, con ) ) {
				// error parsing
				broadcastErrorMessage();
				return;
			}
			broadcastUpdateStatus( R.string.statusUpdatingDisplay );
			broadcastResponses( xml );
		}
	}
	
	private HttpURLConnection setupConnection ( URL url ) throws ProtocolException, IOException {
		HttpURLConnection con = (HttpURLConnection) url.openConnection();
		con.setReadTimeout( host.getReadTimeout() );
		con.setConnectTimeout( host.getConnectTimeout() );
		con.setRequestMethod( "GET" );
		con.setDoInput( true );
		return con;
	}
	
	private boolean parseXML ( XMLHandler xml, HttpURLConnection con ) {
		SAXParserFactory spf = SAXParserFactory.newInstance();
		XMLReader xr = null;
		boolean result = false;
		try {
			// Check for an interruption
			if ( Thread.interrupted() )
				throw new InterruptedException();

			broadcastUpdateStatus( R.string.statusInitParser );
			xr = spf.newSAXParser().getXMLReader();
			xr.setContentHandler( xml );
			xr.setErrorHandler( xml );

			// Check for an interruption
			if ( Thread.interrupted() )
				throw new InterruptedException();

			broadcastUpdateStatus( R.string.statusParsing );
			xr.parse( new InputSource(con.getInputStream()) );
			broadcastUpdateStatus( R.string.statusFinished );
			result = true;
		} catch ( ParserConfigurationException e ) {
			rapp.error( 7, e, "parseXML: ParserConfigurationException" );
		} catch ( IOException e ) {
			rapp.error( 8, e, "parseXML: IOException" );
		} catch ( SAXException e ) {
			rapp.error( 9, e, "parseXML: SAXException" );
		} catch ( InterruptedException e ) {
			// Not a true error, so only for debugging
			Log.d( TAG, "parseXML: InterruptedException", e );
		}
		return result;
	}

	// Broadcast Stuff
	private void broadcastResponses ( XMLHandler xml ) {
		if ( host.isRequestForLabels() ) {
			broadcastLabelsResponse( xml.getRa() );
		} else if ( host.getCommand().startsWith( RequestCommands.Relay )
					|| host.getCommand().equals( RequestCommands.ReefAngel ) ) {
			broadcastUpdateDisplayData( xml.getRa() );
		} else if ( host.getCommand().equals( RequestCommands.MemoryByte )
					|| host.getCommand().equals( RequestCommands.MemoryInt ) ) {
			broadcastMemoryResponse( xml.getMemoryResponse(), host.isWrite() );
		} else if ( host.getCommand().equals( RequestCommands.FeedingMode ) ) {
			broadcastCommandResponse(	R.string.labelFeedingMode,
										xml.getModeResponse() );
		} else if ( host.getCommand().equals( RequestCommands.WaterMode ) ) {
			broadcastCommandResponse(	R.string.labelWaterMode,
										xml.getModeResponse() );
		} else if ( host.getCommand().equals( RequestCommands.ExitMode ) ) {
			broadcastCommandResponse(	R.string.labelExitMode,
										xml.getModeResponse() );
		} else if ( host.getCommand().equals( RequestCommands.AtoClear ) ) {
			broadcastCommandResponse(	R.string.labelAtoClear,
										xml.getModeResponse() );
		} else if ( host.getCommand().equals( RequestCommands.OverheatClear ) ) {
			broadcastCommandResponse(	R.string.labelOverheatClear,
										xml.getModeResponse() );
		} else if ( host.getCommand().equals( RequestCommands.LightsOn ) ) {
			broadcastCommandResponse(	R.string.labelLightsOn,
										xml.getModeResponse() );
		} else if ( host.getCommand().equals( RequestCommands.LightsOff ) ) {
			broadcastCommandResponse(	R.string.labelLightsOff,
										xml.getModeResponse() );
		} else if ( host.getCommand().equals( RequestCommands.Reboot ) ) {
			broadcastCommandResponse(	R.string.labelReboot,
										xml.getModeResponse() );
		} else if ( host.getCommand().equals( RequestCommands.Calibrate ) ) {
			broadcastCalibrateResponse(getCalibrateResponseMessage(host.getCalibrateType()),
			                         xml.getModeResponse());
		} else if ( host.getCommand().equals( RequestCommands.PwmOverride ) ) {
			broadcastOverrideResponse( host.getOverrideChannel(), 
			                          xml.getModeResponse() );
		} else if ( host.getCommand().equals( RequestCommands.Version ) ) {
			Intent i = new Intent( MessageCommands.VERSION_RESPONSE_INTENT );
			i.putExtra( MessageCommands.VERSION_RESPONSE_STRING,
						xml.getVersion() );
			rapp.sendBroadcast( i, Permissions.SEND_COMMAND );
		} else if ( host.getCommand().equals( RequestCommands.DateTime ) ) {
			Intent i = new Intent( MessageCommands.DATE_QUERY_RESPONSE_INTENT );
			i.putExtra( MessageCommands.DATE_QUERY_RESPONSE_STRING,
						xml.getDateTime() );
			rapp.sendBroadcast( i, Permissions.SEND_COMMAND );
		} else if ( host.getCommand().startsWith( RequestCommands.DateTime ) ) {
			Intent i = new Intent( MessageCommands.DATE_SEND_RESPONSE_INTENT );
			i.putExtra( MessageCommands.DATE_SEND_RESPONSE_STRING,
						xml.getDateTimeUpdateStatus() );
			rapp.sendBroadcast( i, Permissions.SEND_COMMAND );
		}
	}

	private String getCalibrateResponseMessage ( int location ) {
		int id;
		switch ( location ) {
			default:
			case Globals.CALIBRATE_PH:
				id = R.string.labelCalibratePH;
				break;
			case Globals.CALIBRATE_PHE:
				id = R.string.labelCalibratePHExp;
				break;
			case Globals.CALIBRATE_ORP:
				id = R.string.labelCalibrateORP;
				break;
			case Globals.CALIBRATE_SALINITY:
				id = R.string.labelCalibrateSalinity;
				break;
			case Globals.CALIBRATE_WATERLEVEL:
				id = R.string.labelCalibrateWaterLevel;
				break;
		}
		return rapp.getString(id);
	}
	
	private void broadcastCalibrateResponse ( String msg, String response ) {
		msg += rapp.getString( R.string.labelSeparator );
		Log.d(	TAG, msg + " " + response );
		Intent i = new Intent( MessageCommands.CALIBRATE_RESPONSE_INTENT );
		i.putExtra( MessageCommands.CALIBRATE_RESPONSE_STRING,
					msg + " " + response );
		rapp.sendBroadcast( i, Permissions.SEND_COMMAND );	
	}
	
	private void broadcastOverrideResponse ( int channel, String response ) {
		// get channel name
		// create response -  channel: MESSAGE
		String msg = rapp.getPWMOverrideChannelName( channel ) 
				+ rapp.getString( R.string.labelSeparator );
		Log.d( TAG, msg + " " + response );
		Intent i = new Intent( MessageCommands.OVERRIDE_RESPONSE_INTENT );
		i.putExtra( MessageCommands.OVERRIDE_RESPONSE_STRING, 
		            msg + " " + response );
		rapp.sendBroadcast( i, Permissions.SEND_COMMAND );
	}
	
	private void broadcastCommandResponse ( int id, String response ) {
		String msg = rapp.getString( id ) + rapp.getString( R.string.labelSeparator );
		Log.d(	TAG, msg + " " + response );
		Intent i = new Intent( MessageCommands.COMMAND_RESPONSE_INTENT );
		i.putExtra( MessageCommands.COMMAND_RESPONSE_STRING,
					msg + " " + response );
		rapp.sendBroadcast( i, Permissions.SEND_COMMAND );	
	}

	// FIXME improve preference saving
	private void broadcastLabelsResponse ( Controller ra ) {
		// Save the preferences to memory
		raprefs.set( R.string.prefT1LabelKey, ra.getTempLabel( 1 ) );
		raprefs.set( R.string.prefT2LabelKey, ra.getTempLabel( 2 ) );
		raprefs.set( R.string.prefT3LabelKey, ra.getTempLabel( 3 ) );
		int i, j;
		Log.d( TAG, "saving main labels" );
		for ( i = 0; i < Controller.MAX_RELAY_PORTS; i++ ) {
			raprefs.setRelayLabel( 0, i, ra.getMainRelay().getPortLabel( i + 1 ) );
		}
		Relay r;
		for ( i = 0; i < Controller.MAX_EXPANSION_RELAYS; i++ ) {
			// use i+1 because it uses 1 based referencing
			r = ra.getExpRelay( i + 1 );
			for ( j = 0; j < Controller.MAX_RELAY_PORTS; j++ ) {
				// use i+1 because the expansion relays start at 1
				raprefs.setRelayLabel( i + 1, j, r.getPortLabel( j + 1 ) );
			}
		}
		if ( !ra.getPHLabel().equals( "" ) ) {
			raprefs.set( R.string.prefPHLabelKey, ra.getPHLabel() );
		}
		// FIXME add getting ato low and high labels from portal
		// FIXME labels require updating Controller class to store them
		if ( !ra.getSalinityLabel().equals( "" ) ) {
			raprefs.set( R.string.prefSalinityLabelKey, ra.getSalinityLabel() );
		}
		if ( !ra.getORPLabel().equals( "" ) ) {
			raprefs.set( R.string.prefORPLabelKey, ra.getORPLabel() );
		}
		if ( !ra.getPHExpLabel().equals( "" ) ) {
			raprefs.set( R.string.prefPHExpLabelKey, ra.getPHExpLabel() );
		}
		if ( !ra.getPwmALabel().equals( "" ) ) {
			raprefs.set( R.string.prefAPLabelKey, ra.getPwmALabel() );
		}
		if ( !ra.getPwmDLabel().equals( "" ) ) {
			raprefs.set( R.string.prefDPLabelKey, ra.getPwmDLabel() );
		}
		for ( i = 0; i < Controller.MAX_WATERLEVEL_PORTS; i++ ) {
			if ( !ra.getWaterLevelLabel( (short)i ).equals("") ) {
				raprefs.set( raprefs.getWaterLevelLabelKey( i ), 
				             ra.getWaterLevelLabel((short)i) );
			}
		}
		for ( i = 0; i < Controller.MAX_PWM_EXPANSION_PORTS; i++ ) {
			if ( !ra.getPwmExpansionLabel( (short) i ).equals( "" ) )
				raprefs.setDimmingModuleChannelLabel( i, ra
						.getPwmExpansionLabel( (short) i ) );
		}
		for ( i = 0; i < Controller.MAX_SCPWM_EXPANSION_PORTS; i++ ) {
			if ( !ra.getSCPwmExpansionLabel( (short) i ).equals( "" ) )
				raprefs.setSCDimmingModuleChannelLabel( i, ra
						.getSCPwmExpansionLabel( (short) i ) );
		}
		for ( i = 0; i < Controller.MAX_CUSTOM_VARIABLES; i++ ) {
			if ( !ra.getCustomVariableLabel( (short) i ).equals( "" ) )
				raprefs.setCustomModuleChannelLabel( i, ra
						.getCustomVariableLabel( (short) i ) );
		}
		for ( i = 0; i < Controller.MAX_IO_CHANNELS; i++ ) {
			if ( !ra.getIOChannelLabel( (short) i ).equals( "" ) )
				raprefs.setIOModuleChannelLabel( i, ra
						.getIOChannelLabel( (short) i ) );
		}
		if ( !ra.getHumidityLabel().equals( "" ) ) {
			raprefs.set( R.string.prefHumidityLabelKey, ra.getHumidityLabel() );
		}

		// Tell the activity we updated the labels
		Intent intent = new Intent( MessageCommands.LABEL_RESPONSE_INTENT );
		rapp.sendBroadcast( intent, Permissions.SEND_COMMAND );
	}

	private void broadcastMemoryResponse ( String response, boolean wasWrite ) {
		// Log.d(TAG, "broadcastMemoryResponse");
		Intent i = new Intent( MessageCommands.MEMORY_RESPONSE_INTENT );
		i.putExtra( MessageCommands.MEMORY_RESPONSE_STRING, response );
		i.putExtra( MessageCommands.MEMORY_RESPONSE_WRITE_BOOLEAN, wasWrite );
		rapp.sendBroadcast( i, Permissions.SEND_COMMAND );
	}

	private void broadcastUpdateDisplayData ( Controller ra ) {
		ContentValues v = new ContentValues();
		v.put( StatusTable.COL_T1, ra.getTemp1() );
		v.put( StatusTable.COL_T2, ra.getTemp2() );
		v.put( StatusTable.COL_T3, ra.getTemp3() );
		v.put( StatusTable.COL_PH, ra.getPH() );
		v.put( StatusTable.COL_DP, ra.getPwmD() );
		v.put( StatusTable.COL_AP, ra.getPwmA() );
		v.put( StatusTable.COL_SAL, ra.getSalinity() );
		v.put( StatusTable.COL_ORP, ra.getORP() );
		v.put( StatusTable.COL_ATOHI, ra.getAtoHigh() );
		v.put( StatusTable.COL_ATOLO, ra.getAtoLow() );
		v.put( StatusTable.COL_LOGDATE, ra.getLogDate() );
		v.put( StatusTable.COL_RDATA, ra.getMainRelay().getRelayData() );
		v.put( StatusTable.COL_RONMASK, ra.getMainRelay().getRelayOnMask() );
		v.put( StatusTable.COL_ROFFMASK, ra.getMainRelay().getRelayOffMask() );
		v.put( StatusTable.COL_R1DATA, ra.getExpRelay( 1 ).getRelayData() );
		v.put( StatusTable.COL_R1ONMASK, ra.getExpRelay( 1 ).getRelayOnMask() );
		v.put( StatusTable.COL_R1OFFMASK, ra.getExpRelay( 1 ).getRelayOffMask() );
		v.put( StatusTable.COL_R2DATA, ra.getExpRelay( 2 ).getRelayData() );
		v.put( StatusTable.COL_R2ONMASK, ra.getExpRelay( 2 ).getRelayOnMask() );
		v.put( StatusTable.COL_R2OFFMASK, ra.getExpRelay( 2 ).getRelayOffMask() );
		v.put( StatusTable.COL_R3DATA, ra.getExpRelay( 3 ).getRelayData() );
		v.put( StatusTable.COL_R3ONMASK, ra.getExpRelay( 3 ).getRelayOnMask() );
		v.put( StatusTable.COL_R3OFFMASK, ra.getExpRelay( 3 ).getRelayOffMask() );
		v.put( StatusTable.COL_R4DATA, ra.getExpRelay( 4 ).getRelayData() );
		v.put( StatusTable.COL_R4ONMASK, ra.getExpRelay( 4 ).getRelayOnMask() );
		v.put( StatusTable.COL_R4OFFMASK, ra.getExpRelay( 4 ).getRelayOffMask() );
		v.put( StatusTable.COL_R5DATA, ra.getExpRelay( 5 ).getRelayData() );
		v.put( StatusTable.COL_R5ONMASK, ra.getExpRelay( 5 ).getRelayOnMask() );
		v.put( StatusTable.COL_R5OFFMASK, ra.getExpRelay( 5 ).getRelayOffMask() );
		v.put( StatusTable.COL_R6DATA, ra.getExpRelay( 6 ).getRelayData() );
		v.put( StatusTable.COL_R6ONMASK, ra.getExpRelay( 6 ).getRelayOnMask() );
		v.put( StatusTable.COL_R6OFFMASK, ra.getExpRelay( 6 ).getRelayOffMask() );
		v.put( StatusTable.COL_R7DATA, ra.getExpRelay( 7 ).getRelayData() );
		v.put( StatusTable.COL_R7ONMASK, ra.getExpRelay( 7 ).getRelayOnMask() );
		v.put( StatusTable.COL_R7OFFMASK, ra.getExpRelay( 7 ).getRelayOffMask() );
		v.put( StatusTable.COL_R8DATA, ra.getExpRelay( 8 ).getRelayData() );
		v.put( StatusTable.COL_R8ONMASK, ra.getExpRelay( 8 ).getRelayOnMask() );
		v.put( StatusTable.COL_R8OFFMASK, ra.getExpRelay( 8 ).getRelayOffMask() );
		v.put( StatusTable.COL_PWME0, ra.getPwmExpansion( (short) 0 ) );
		v.put( StatusTable.COL_PWME1, ra.getPwmExpansion( (short) 1 ) );
		v.put( StatusTable.COL_PWME2, ra.getPwmExpansion( (short) 2 ) );
		v.put( StatusTable.COL_PWME3, ra.getPwmExpansion( (short) 3 ) );
		v.put( StatusTable.COL_PWME4, ra.getPwmExpansion( (short) 4 ) );
		v.put( StatusTable.COL_PWME5, ra.getPwmExpansion( (short) 5 ) );
		v.put( StatusTable.COL_AIW, ra.getAIChannel( Controller.AI_WHITE ) );
		v.put( StatusTable.COL_AIB, ra.getAIChannel( Controller.AI_BLUE ) );
		v.put( StatusTable.COL_AIRB, ra.getAIChannel( Controller.AI_ROYALBLUE ) );
		v.put(	StatusTable.COL_RFM,
				ra.getVortechValue( Controller.VORTECH_MODE ) );
		v.put(	StatusTable.COL_RFS,
				ra.getVortechValue( Controller.VORTECH_SPEED ) );
		v.put(	StatusTable.COL_RFD,
				ra.getVortechValue( Controller.VORTECH_DURATION ) );
		v.put(	StatusTable.COL_RFW,
				ra.getRadionChannel( Controller.RADION_WHITE ) );
		v.put(	StatusTable.COL_RFRB,
				ra.getRadionChannel( Controller.RADION_ROYALBLUE ) );
		v.put( StatusTable.COL_RFR, ra.getRadionChannel( Controller.RADION_RED ) );
		v.put(	StatusTable.COL_RFG,
				ra.getRadionChannel( Controller.RADION_GREEN ) );
		v.put(	StatusTable.COL_RFB,
				ra.getRadionChannel( Controller.RADION_BLUE ) );
		v.put(	StatusTable.COL_RFI,
				ra.getRadionChannel( Controller.RADION_INTENSITY ) );
		v.put( StatusTable.COL_IO, ra.getIOChannels() );
		v.put( StatusTable.COL_C0, ra.getCustomVariable( (byte) 0 ) );
		v.put( StatusTable.COL_C1, ra.getCustomVariable( (byte) 1 ) );
		v.put( StatusTable.COL_C2, ra.getCustomVariable( (byte) 2 ) );
		v.put( StatusTable.COL_C3, ra.getCustomVariable( (byte) 3 ) );
		v.put( StatusTable.COL_C4, ra.getCustomVariable( (byte) 4 ) );
		v.put( StatusTable.COL_C5, ra.getCustomVariable( (byte) 5 ) );
		v.put( StatusTable.COL_C6, ra.getCustomVariable( (byte) 6 ) );
		v.put( StatusTable.COL_C7, ra.getCustomVariable( (byte) 7 ) );
		v.put( StatusTable.COL_EM, ra.getExpansionModules() );
		v.put( StatusTable.COL_EM1, ra.getExpansionModules1() );
		v.put( StatusTable.COL_REM, ra.getRelayExpansionModules() );
		v.put( StatusTable.COL_PHE, ra.getPHExp() );
		v.put( StatusTable.COL_WL, ra.getWaterLevel( (short) 0) );
		v.put( StatusTable.COL_WL1, ra.getWaterLevel( (short) 1) );
		v.put( StatusTable.COL_WL2, ra.getWaterLevel( (short) 2) );
		v.put( StatusTable.COL_WL3, ra.getWaterLevel( (short) 3) );
		v.put( StatusTable.COL_WL4, ra.getWaterLevel( (short) 4) );
		v.put( StatusTable.COL_HUM, ra.getHumidity() );
		v.put( StatusTable.COL_PWMAO, ra.getPwmAOverride() );
		v.put( StatusTable.COL_PWMDO, ra.getPwmDOverride() );
		v.put( StatusTable.COL_PWME0O, ra.getPwmExpansionOverride( (short) 0 ) );
		v.put( StatusTable.COL_PWME1O, ra.getPwmExpansionOverride( (short) 1 ) );
		v.put( StatusTable.COL_PWME2O, ra.getPwmExpansionOverride( (short) 2 ) );
		v.put( StatusTable.COL_PWME3O, ra.getPwmExpansionOverride( (short) 3 ) );
		v.put( StatusTable.COL_PWME4O, ra.getPwmExpansionOverride( (short) 4 ) );
		v.put( StatusTable.COL_PWME5O, ra.getPwmExpansionOverride( (short) 5 ) );
		v.put( StatusTable.COL_AIWO, ra.getAIChannelOverride( Controller.AI_WHITE ) );
		v.put( StatusTable.COL_AIBO, ra.getAIChannelOverride( Controller.AI_BLUE ) );
		v.put( StatusTable.COL_AIRBO, ra.getAIChannelOverride( Controller.AI_ROYALBLUE ) );
		v.put( StatusTable.COL_RFWO, ra.getRadionChannelOverride( Controller.RADION_WHITE ) );
		v.put( StatusTable.COL_RFRBO, ra.getRadionChannelOverride( Controller.RADION_ROYALBLUE ) );
		v.put( StatusTable.COL_RFRO, ra.getRadionChannelOverride( Controller.RADION_RED ) );
		v.put( StatusTable.COL_RFGO, ra.getRadionChannelOverride( Controller.RADION_GREEN ) );
		v.put( StatusTable.COL_RFBO, ra.getRadionChannelOverride( Controller.RADION_BLUE ) );
		v.put( StatusTable.COL_RFIO, ra.getRadionChannelOverride( Controller.RADION_INTENSITY ) );
		v.put( StatusTable.COL_SF, ra.getStatusFlags() );
		v.put( StatusTable.COL_AF, ra.getAlertFlags() );
		v.put( StatusTable.COL_SCPWME0, ra.getSCPwmExpansion( (short) 0));
		v.put( StatusTable.COL_SCPWME0O, ra.getSCPwmExpansionOverride( (short) 0));
		v.put( StatusTable.COL_SCPWME1, ra.getSCPwmExpansion( (short) 1));
		v.put( StatusTable.COL_SCPWME1O, ra.getSCPwmExpansionOverride( (short) 1));
		v.put( StatusTable.COL_SCPWME2, ra.getSCPwmExpansion( (short) 2));
		v.put( StatusTable.COL_SCPWME2O, ra.getSCPwmExpansionOverride( (short) 2));
		v.put( StatusTable.COL_SCPWME3, ra.getSCPwmExpansion( (short) 3));
		v.put( StatusTable.COL_SCPWME3O, ra.getSCPwmExpansionOverride( (short) 3));
		v.put( StatusTable.COL_SCPWME4, ra.getSCPwmExpansion( (short) 4));
		v.put( StatusTable.COL_SCPWME4O, ra.getSCPwmExpansionOverride( (short) 4));
		v.put( StatusTable.COL_SCPWME5, ra.getSCPwmExpansion( (short) 5));
		v.put( StatusTable.COL_SCPWME5O, ra.getSCPwmExpansionOverride( (short) 5));
		v.put( StatusTable.COL_SCPWME6, ra.getSCPwmExpansion( (short) 6));
		v.put( StatusTable.COL_SCPWME6O, ra.getSCPwmExpansionOverride( (short) 6));
		v.put( StatusTable.COL_SCPWME7, ra.getSCPwmExpansion( (short) 7));
		v.put( StatusTable.COL_SCPWME7O, ra.getSCPwmExpansionOverride( (short) 7));
		v.put( StatusTable.COL_SCPWME8, ra.getSCPwmExpansion( (short) 8));
		v.put( StatusTable.COL_SCPWME8O, ra.getSCPwmExpansionOverride( (short) 8));
		v.put( StatusTable.COL_SCPWME9, ra.getSCPwmExpansion( (short) 9));
		v.put( StatusTable.COL_SCPWME9O, ra.getSCPwmExpansionOverride( (short) 9));
		v.put( StatusTable.COL_SCPWME10, ra.getSCPwmExpansion( (short) 10));
		v.put( StatusTable.COL_SCPWME10O, ra.getSCPwmExpansionOverride( (short) 10));
		v.put( StatusTable.COL_SCPWME11, ra.getSCPwmExpansion( (short) 11));
		v.put( StatusTable.COL_SCPWME11O, ra.getSCPwmExpansionOverride( (short) 11));
		v.put( StatusTable.COL_SCPWME12, ra.getSCPwmExpansion( (short) 12));
		v.put( StatusTable.COL_SCPWME12O, ra.getSCPwmExpansionOverride( (short) 12));
		v.put( StatusTable.COL_SCPWME13, ra.getSCPwmExpansion( (short) 13));
		v.put( StatusTable.COL_SCPWME13O, ra.getSCPwmExpansionOverride( (short) 13));
		v.put( StatusTable.COL_SCPWME14, ra.getSCPwmExpansion( (short) 14));
		v.put( StatusTable.COL_SCPWME14O, ra.getSCPwmExpansionOverride( (short) 14));
		v.put( StatusTable.COL_SCPWME15, ra.getSCPwmExpansion( (short) 15));
		v.put( StatusTable.COL_SCPWME15O, ra.getSCPwmExpansionOverride( (short) 15));
		rapp.getContentResolver()
				.insert(	Uri.parse( StatusProvider.CONTENT_URI + "/"
										+ StatusProvider.PATH_STATUS ), v );
		// Clear the error retry count on successful insertion of data
		rapp.clearErrorRetryCount();

		if ( raprefs.isNotificationEnabled() ) {
			// launch the notification check service
			Intent n = new Intent( rapp, NotificationService.class );
			n.setAction( MessageCommands.NOTIFICATION_INTENT );
			rapp.startService( n );
		}

		Intent u = new Intent( MessageCommands.UPDATE_DISPLAY_DATA_INTENT );
		rapp.sendBroadcast( u, Permissions.QUERY_STATUS );
	}

	private void broadcastUpdateStatus ( int msgid ) {
		Intent i = new Intent( MessageCommands.UPDATE_STATUS_INTENT );
		i.putExtra( MessageCommands.UPDATE_STATUS_ID, msgid );
		rapp.sendBroadcast( i, Permissions.QUERY_STATUS );
	}

	private void broadcastErrorMessage ( ) {
		Intent i = new Intent( rapp, NotificationService.class );
		i.setAction( MessageCommands.NOTIFICATION_ERROR_INTENT );
		rapp.startService( i );
	}
}
