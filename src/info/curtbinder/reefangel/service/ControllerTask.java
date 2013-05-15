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
import info.curtbinder.reefangel.phone.Permissions;
import info.curtbinder.reefangel.phone.R;
import info.curtbinder.reefangel.phone.RAApplication;
import info.curtbinder.reefangel.phone.RAPreferences;
import info.curtbinder.reefangel.phone.StatusActivity;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.util.Date;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
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
		long start = System.currentTimeMillis();
		Log.i(	TAG,
				"Update: "
						+ DateFormat.getDateTimeInstance().format( new Date() ) );
		try {
			URL url = new URL( host.toString() );
			con = (HttpURLConnection) url.openConnection();
			con.setReadTimeout( host.getReadTimeout() );
			con.setConnectTimeout( host.getConnectTimeout() );
			con.setRequestMethod( "GET" );
			con.setDoInput( true );

			broadcastUpdateStatus( R.string.statusConnect );
			con.connect();

			if ( Thread.interrupted() )
				throw new InterruptedException();

			res = sendCommand( con.getInputStream() );
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
			Log.d( TAG, "InterruptedException", e );
			res =
					(String) rapp.getResources()
							.getText( R.string.messageCancelled );
		}

		if ( con != null ) {
			con.disconnect();
			broadcastUpdateStatus( R.string.statusDisconnected );
		}

		long end = System.currentTimeMillis();
		Log.d(	TAG,
				new String( String.format( "sendCommand (%d ms)", end - start ) ) );
		broadcastUpdateStatus( R.string.statusReadResponse );

		// check if there was an error
		if ( rapp.errorCode > 0 ) {
			// encountered an error, display an error on screen
			broadcastErrorMessage();
		} else if ( res.equals( (String) rapp.getResources()
				.getText( R.string.messageCancelled ) ) ) {
			// Interrupted
			Log.d( TAG, "sendCommand Interrupted" );
			broadcastUpdateStatus( R.string.messageCancelled );
		} else {
			XMLHandler xml = new XMLHandler();
			if ( raprefs.useOld085xExpansionRelays() )
				xml.setOld085xExpansion( true );
			if ( !parseXML( xml, res ) ) {
				// error parsing
				broadcastErrorMessage();
				return;
			}
			broadcastUpdateStatus( R.string.statusUpdatingDisplay );
			broadcastResponses( xml );
		}
	}

	private String sendCommand ( InputStream i ) {
		StringBuilder s = new StringBuilder( 8192 );
		try {
			// Check for an interruption
			if ( Thread.interrupted() )
				throw new InterruptedException();

			broadcastUpdateStatus( R.string.statusSendingCommand );
			int available;
			byte[] b;
			int nRead = 0;
			// int count = 1;
			while ( (available = i.available()) > 0 ) {
				// Check for an interruption
				// Log.d(TAG, "Count: " + count++ + ", size: " + available);
				if ( Thread.interrupted() )
					throw new InterruptedException();

				b = new byte[available];
				nRead = i.read( b, 0, available );
				s.append( new String( b, 0, nRead ) );
			}
			broadcastUpdateStatus( R.string.statusReadResponse );
		} catch ( InterruptedException e ) {
			Log.d( TAG, "sendCommand: InterruptedException", e );
			s =
					new StringBuilder( (String) rapp.getResources()
							.getText( R.string.messageCancelled ) );
		} catch ( ConnectException e ) {
			rapp.error( 3, e, "sendCommand: ConnectException" );
		} catch ( UnknownHostException e ) {
			rapp.error( 4, e, "sendCommand: UnknownHostException" );
		} catch ( Exception e ) {
			rapp.error( 2, e, "sendCommand: Exception" );
		}

		// if we encountered an error, set the error text
		if ( rapp.errorCode > 0 )
			s =
					new StringBuilder( (String) rapp.getResources()
							.getText( R.string.messageError ) );

		return s.toString();
	}

	private boolean parseXML ( XMLHandler xml, String res ) {
		SAXParserFactory spf = SAXParserFactory.newInstance();
		SAXParser sp = null;
		XMLReader xr = null;
		long start = 0, end = 0;
		boolean result = false;
		try {
			// Check for an interruption
			if ( Thread.interrupted() )
				throw new InterruptedException();

			broadcastUpdateStatus( R.string.statusInitParser );
			Log.d( TAG, "Parsing" );
			sp = spf.newSAXParser();
			xr = sp.getXMLReader();
			xr.setContentHandler( xml );
			xr.setErrorHandler( xml );

			// Check for an interruption
			if ( Thread.interrupted() )
				throw new InterruptedException();

			start = System.currentTimeMillis();
			broadcastUpdateStatus( R.string.statusParsing );
			xr.parse( new InputSource( new StringReader( res ) ) );
			end = System.currentTimeMillis();
			Log.d(	TAG,
					new String( String.format( "Parsed (%d ms)", end - start ) ) );
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

	private void broadcastCommandResponse ( int id, String response ) {
		Log.d(	TAG,
				rapp.getString( id ) + rapp.getString( R.string.labelSeparator )
						+ " " + response );
		Intent i = new Intent( MessageCommands.COMMAND_RESPONSE_INTENT );
		i.putExtra( MessageCommands.COMMAND_RESPONSE_STRING,
					rapp.getString( id )
							+ rapp.getString( R.string.labelSeparator ) + " "
							+ response );
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
		if ( !ra.getWaterLevelLabel().equals( "" ) ) {
			raprefs.set(	R.string.prefWaterLevelLabelKey,
							ra.getWaterLevelLabel() );
		}
		for ( i = 0; i < Controller.MAX_PWM_EXPANSION_PORTS; i++ ) {
			if ( !ra.getPwmExpansionLabel( (short) i ).equals( "" ) )
				raprefs.setDimmingModuleChannelLabel( i, ra
						.getPwmExpansionLabel( (short) i ) );
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
		v.put( StatusTable.COL_REM, ra.getRelayExpansionModules() );
		v.put( StatusTable.COL_PHE, ra.getPHExp() );
		v.put( StatusTable.COL_WL, ra.getWaterLevel() );
		rapp.getContentResolver()
				.insert(	Uri.parse( StatusProvider.CONTENT_URI + "/"
										+ StatusProvider.PATH_STATUS ), v );

		Intent u = new Intent( MessageCommands.UPDATE_DISPLAY_DATA_INTENT );
		rapp.sendBroadcast( u, Permissions.QUERY_STATUS );
	}

	private void broadcastUpdateStatus ( int msgid ) {
		Intent i = new Intent( MessageCommands.UPDATE_STATUS_INTENT );
		i.putExtra( MessageCommands.UPDATE_STATUS_ID, msgid );
		rapp.sendBroadcast( i, Permissions.QUERY_STATUS );
	}

	@SuppressWarnings("deprecation")
	private void broadcastErrorMessage ( ) {
		// Log.d(TAG, "broadcastErrorMessage");
		String er = rapp.getErrorMessage();

		if ( raprefs.isNotificationEnabled() ) {
			// create intent to launch status activity when notification
			// selected
			Intent si = new Intent( rapp, StatusActivity.class );
			si.addFlags( Intent.FLAG_ACTIVITY_SINGLE_TOP );
			si.addFlags( Intent.FLAG_ACTIVITY_REORDER_TO_FRONT );
			PendingIntent pi =
					PendingIntent
							.getActivity(	rapp, -1, si,
											PendingIntent.FLAG_UPDATE_CURRENT );

			// if error notification is enabled, increase the error count
			// as soon as we know it's an error
			if ( raprefs.isErrorRetryEnabled() ) {
				rapp.errorCount++;
			}

			boolean fCanNotify = true;
			// if error retry is enabled, don't notify unless we fail the error
			// retries
			if ( raprefs.isErrorRetryEnabled() && rapp.canErrorRetry() ) {
				fCanNotify = false;
			}

			// send notification
			if ( fCanNotify ) {
				// notification
				NotificationManager nm =
						(NotificationManager) rapp
								.getSystemService( Context.NOTIFICATION_SERVICE );
				Notification n =
						new Notification( R.drawable.st_notify,
							rapp.getString( R.string.app_name ) + " " + er,
							System.currentTimeMillis() );
				n.flags |= Notification.FLAG_AUTO_CANCEL;
				n.sound = raprefs.getNotificationSound();
				n.setLatestEventInfo(	rapp,
										rapp.getString( R.string.app_name ),
										er, pi );
				nm.notify( 0, n );
			}
		}

		// broadcast
		Intent i = new Intent( MessageCommands.ERROR_MESSAGE_INTENT );
		i.putExtra( MessageCommands.ERROR_MESSAGE_STRING, er );

		// send broadcast
		rapp.sendBroadcast( i, Permissions.QUERY_STATUS );
	}
}
