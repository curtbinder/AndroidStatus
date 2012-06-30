package info.curtbinder.reefangel.phone;

/*
 * Copyright (c) 2011-12 by Curt Binder (http://curtbinder.info)
 *
 * This work is made available under the terms of the 
 * Creative Commons Attribution-NonCommercial-ShareAlike 3.0 Unported License
 * http://creativecommons.org/licenses/by-nc-sa/3.0/
 */

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

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import android.content.Intent;
import android.util.Log;

public class ControllerTask implements Runnable {
	// Messages broadcast from the task

	private static final String TAG = ControllerTask.class.getSimpleName();
	private final Host host;
	private final RAApplication rapp;

	ControllerTask ( RAApplication rapp, Host host ) {
		this.rapp = rapp;
		this.host = host;
	}

	@Override
	public void run ( ) {
		// Communicate with controller

		// clear out the error code on run
		rapp.errorCode = 0;
		HttpURLConnection con = null;
		String res = "";
		broadcastUpdateStatus( R.string.statusStart );
		long start = System.currentTimeMillis();
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
		} finally {
			if ( con != null ) {
				con.disconnect();
				broadcastUpdateStatus( R.string.statusDisconnected );
			}
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
			if ( rapp.useOld085xExpansionRelays() )
				xml.setOld085xExpansion( true );
			if ( !parseXML( xml, res ) ) {
				// error parsing
				broadcastErrorMessage();
				return;
			}
			broadcastUpdateStatus( R.string.statusUpdatingDisplay );
			if ( host.isRequestForLabels() ) {
				broadcastLabelsResponse( xml.getRa() );
			} else if ( host.getCommand().startsWith( Globals.requestRelay )
						|| host.getCommand().equals( Globals.requestReefAngel ) ) {
				broadcastUpdateDisplayData( xml.getRa() );
			} else if ( host.getCommand().equals( Globals.requestMemoryByte )
						|| host.getCommand().equals( Globals.requestMemoryInt ) ) {
				broadcastMemoryResponse(	xml.getMemoryResponse(),
											host.isWrite() );
			} else if ( host.getCommand().equals( Globals.requestFeedingMode ) ) {
				broadcastCommandResponse(	R.string.labelFeedingMode,
											xml.getModeResponse() );
			} else if ( host.getCommand().equals( Globals.requestWaterMode ) ) {
				broadcastCommandResponse(	R.string.labelWaterMode,
											xml.getModeResponse() );
			} else if ( host.getCommand().equals( Globals.requestExitMode ) ) {
				broadcastCommandResponse(	R.string.labelExitMode,
											xml.getModeResponse() );
			} else if ( host.getCommand().equals( Globals.requestAtoClear ) ) {
				broadcastCommandResponse(	R.string.labelAtoClear,
											xml.getModeResponse() );
			} else if ( host.getCommand().equals( Globals.requestOverheatClear ) ) {
				broadcastCommandResponse(	R.string.labelOverheatClear,
											xml.getModeResponse() );
			} else if ( host.getCommand().equals( Globals.requestVersion ) ) {
				Intent i = new Intent( MessageCommands.VERSION_RESPONSE_INTENT );
				i.putExtra( MessageCommands.VERSION_RESPONSE_STRING,
							xml.getVersion() );
				rapp.sendBroadcast( i, Permissions.SEND_COMMAND );
			} else if ( host.getCommand().equals( Globals.requestDateTime ) ) {
				Intent i =
						new Intent( MessageCommands.DATE_QUERY_RESPONSE_INTENT );
				i.putExtra( MessageCommands.DATE_QUERY_RESPONSE_STRING,
							xml.getDateTime() );
				rapp.sendBroadcast( i, Permissions.SEND_COMMAND );
			} else if ( host.getCommand().startsWith( Globals.requestDateTime ) ) {
				Intent i =
						new Intent( MessageCommands.DATE_SEND_RESPONSE_INTENT );
				i.putExtra( MessageCommands.DATE_SEND_RESPONSE_STRING,
							xml.getDateTimeUpdateStatus() );
				rapp.sendBroadcast( i, Permissions.SEND_COMMAND );
			}
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

	private void broadcastLabelsResponse ( Controller ra ) {
		// Save the preferences to memory
		rapp.setPref( R.string.prefT1LabelKey, ra.getTempLabel( 1 ) );
		rapp.setPref( R.string.prefT2LabelKey, ra.getTempLabel( 2 ) );
		rapp.setPref( R.string.prefT3LabelKey, ra.getTempLabel( 3 ) );
		int i, j;
		Log.d( TAG, "saving main labels" );
		for ( i = 0; i < Controller.MAX_RELAY_PORTS; i++ ) {
			rapp.setPrefRelayLabel( 0, i, ra.getMainRelay()
					.getPortLabel( i + 1 ) );
		}
		Relay r;
		for ( i = 0; i < Controller.MAX_EXPANSION_RELAYS; i++ ) {
			// use i+1 because it uses 1 based referencing
			r = ra.getExpRelay( i + 1 );
			for ( j = 0; j < Controller.MAX_RELAY_PORTS; j++ ) {
				// use i+1 because the expansion relays start at 1
				rapp.setPrefRelayLabel( i + 1, j, r.getPortLabel( j + 1 ) );
			}
		}
		if ( !ra.getPHLabel().equals( "" ) ) {
			rapp.setPref( R.string.prefPHLabelKey, ra.getPHLabel() );
		}
		if ( !ra.getSalinityLabel().equals( "" ) ) {
			rapp.setPref( R.string.prefSalinityLabelKey, ra.getSalinityLabel() );
		}
		if ( !ra.getORPLabel().equals( "" ) ) {
			rapp.setPref( R.string.prefORPLabelKey, ra.getORPLabel() );
		}
		// TODO add other label downloading and setting here (PHE, Custom, IO, PWME)

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
		Intent i = new Intent( MessageCommands.UPDATE_DISPLAY_DATA_INTENT );
		i.putExtra( RAData.PCOL_T1, ra.getTemp1() );
		i.putExtra( RAData.PCOL_T2, ra.getTemp2() );
		i.putExtra( RAData.PCOL_T3, ra.getTemp3() );
		i.putExtra( RAData.PCOL_PH, ra.getPH() );
		i.putExtra( RAData.PCOL_DP, ra.getPwmD() );
		i.putExtra( RAData.PCOL_AP, ra.getPwmA() );
		i.putExtra( RAData.PCOL_SAL, ra.getSalinity() );
		i.putExtra( RAData.PCOL_ORP, ra.getORP() );
		i.putExtra( RAData.PCOL_ATOHI, ra.getAtoHigh() );
		i.putExtra( RAData.PCOL_ATOLO, ra.getAtoLow() );
		i.putExtra( RAData.PCOL_LOGDATE, ra.getLogDate() );
		i.putExtra( RAData.PCOL_RDATA, ra.getMainRelay().getRelayData() );
		i.putExtra( RAData.PCOL_RONMASK, ra.getMainRelay().getRelayOnMask() );
		i.putExtra( RAData.PCOL_ROFFMASK, ra.getMainRelay().getRelayOffMask() );
		i.putExtra( RAData.PCOL_R1DATA, ra.getExpRelay( 1 ).getRelayData() );
		i.putExtra( RAData.PCOL_R1ONMASK, ra.getExpRelay( 1 ).getRelayOnMask() );
		i.putExtra( RAData.PCOL_R1OFFMASK, ra.getExpRelay( 1 )
				.getRelayOffMask() );
		i.putExtra( RAData.PCOL_R2DATA, ra.getExpRelay( 2 ).getRelayData() );
		i.putExtra( RAData.PCOL_R2ONMASK, ra.getExpRelay( 2 ).getRelayOnMask() );
		i.putExtra( RAData.PCOL_R2OFFMASK, ra.getExpRelay( 2 )
				.getRelayOffMask() );
		i.putExtra( RAData.PCOL_R3DATA, ra.getExpRelay( 3 ).getRelayData() );
		i.putExtra( RAData.PCOL_R3ONMASK, ra.getExpRelay( 3 ).getRelayOnMask() );
		i.putExtra( RAData.PCOL_R3OFFMASK, ra.getExpRelay( 3 )
				.getRelayOffMask() );
		i.putExtra( RAData.PCOL_R4DATA, ra.getExpRelay( 4 ).getRelayData() );
		i.putExtra( RAData.PCOL_R4ONMASK, ra.getExpRelay( 4 ).getRelayOnMask() );
		i.putExtra( RAData.PCOL_R4OFFMASK, ra.getExpRelay( 4 )
				.getRelayOffMask() );
		i.putExtra( RAData.PCOL_R5DATA, ra.getExpRelay( 5 ).getRelayData() );
		i.putExtra( RAData.PCOL_R5ONMASK, ra.getExpRelay( 5 ).getRelayOnMask() );
		i.putExtra( RAData.PCOL_R5OFFMASK, ra.getExpRelay( 5 )
				.getRelayOffMask() );
		i.putExtra( RAData.PCOL_R6DATA, ra.getExpRelay( 6 ).getRelayData() );
		i.putExtra( RAData.PCOL_R6ONMASK, ra.getExpRelay( 6 ).getRelayOnMask() );
		i.putExtra( RAData.PCOL_R6OFFMASK, ra.getExpRelay( 6 )
				.getRelayOffMask() );
		i.putExtra( RAData.PCOL_R7DATA, ra.getExpRelay( 7 ).getRelayData() );
		i.putExtra( RAData.PCOL_R7ONMASK, ra.getExpRelay( 7 ).getRelayOnMask() );
		i.putExtra( RAData.PCOL_R7OFFMASK, ra.getExpRelay( 7 )
				.getRelayOffMask() );
		i.putExtra( RAData.PCOL_R8DATA, ra.getExpRelay( 8 ).getRelayData() );
		i.putExtra( RAData.PCOL_R8ONMASK, ra.getExpRelay( 8 ).getRelayOnMask() );
		i.putExtra( RAData.PCOL_R8OFFMASK, ra.getExpRelay( 8 )
				.getRelayOffMask() );
		i.putExtra( RAData.PCOL_PWME0, ra.getPwmExpansion( (short) 0 ) );
		i.putExtra( RAData.PCOL_PWME1, ra.getPwmExpansion( (short) 1 ) );
		i.putExtra( RAData.PCOL_PWME2, ra.getPwmExpansion( (short) 2 ) );
		i.putExtra( RAData.PCOL_PWME3, ra.getPwmExpansion( (short) 3 ) );
		i.putExtra( RAData.PCOL_PWME4, ra.getPwmExpansion( (short) 4 ) );
		i.putExtra( RAData.PCOL_PWME5, ra.getPwmExpansion( (short) 5 ) );
		i.putExtra( RAData.PCOL_AIW, ra.getAIChannel( Controller.AI_WHITE ) );
		i.putExtra( RAData.PCOL_AIB, ra.getAIChannel( Controller.AI_BLUE ) );
		i.putExtra( RAData.PCOL_AIRB, ra.getAIChannel( Controller.AI_ROYALBLUE ) );
		i.putExtra( RAData.PCOL_RFM,
					ra.getVortechValue( Controller.VORTECH_MODE ) );
		i.putExtra( RAData.PCOL_RFS,
					ra.getVortechValue( Controller.VORTECH_SPEED ) );
		i.putExtra( RAData.PCOL_RFD,
					ra.getVortechValue( Controller.VORTECH_DURATION ) );
		i.putExtra( RAData.PCOL_RFW,
					ra.getRadionChannel( Controller.RADION_WHITE ) );
		i.putExtra( RAData.PCOL_RFRB,
					ra.getRadionChannel( Controller.RADION_ROYALBLUE ) );
		i.putExtra( RAData.PCOL_RFR,
					ra.getRadionChannel( Controller.RADION_RED ) );
		i.putExtra( RAData.PCOL_RFG,
					ra.getRadionChannel( Controller.RADION_GREEN ) );
		i.putExtra( RAData.PCOL_RFB,
					ra.getRadionChannel( Controller.RADION_BLUE ) );
		i.putExtra( RAData.PCOL_RFI,
					ra.getRadionChannel( Controller.RADION_INTENSITY ) );
		i.putExtra( RAData.PCOL_IO, ra.getIOChannels() );
		i.putExtra( RAData.PCOL_C0, ra.getCustomVariable( (byte) 0 ) );
		i.putExtra( RAData.PCOL_C1, ra.getCustomVariable( (byte) 1 ) );
		i.putExtra( RAData.PCOL_C2, ra.getCustomVariable( (byte) 2 ) );
		i.putExtra( RAData.PCOL_C3, ra.getCustomVariable( (byte) 3 ) );
		i.putExtra( RAData.PCOL_C4, ra.getCustomVariable( (byte) 4 ) );
		i.putExtra( RAData.PCOL_C5, ra.getCustomVariable( (byte) 5 ) );
		i.putExtra( RAData.PCOL_C6, ra.getCustomVariable( (byte) 6 ) );
		i.putExtra( RAData.PCOL_C7, ra.getCustomVariable( (byte) 7 ) );
		i.putExtra( RAData.PCOL_EM, ra.getExpansionModules() );
		i.putExtra( RAData.PCOL_REM, ra.getRelayExpansionModules() );
		i.putExtra( RAData.PCOL_PHE, ra.getPHExp() );
		i.putExtra( RAData.PCOL_WL, ra.getWaterLevel() );
		rapp.sendBroadcast( i, Permissions.QUERY_STATUS );
	}

	private void broadcastUpdateStatus ( int msgid ) {
		// Log.d(TAG, "broadcastUpdateStatus");
		Intent i = new Intent( MessageCommands.UPDATE_STATUS_INTENT );
		i.putExtra( MessageCommands.UPDATE_STATUS_ID, msgid );
		rapp.sendBroadcast( i, Permissions.QUERY_STATUS );
	}

	private void broadcastErrorMessage ( ) {
		// TODO maybe a notification message or something
		// Log.d(TAG, "broadcastErrorMessage");
		Intent i = new Intent( MessageCommands.ERROR_MESSAGE_INTENT );
		i.putExtra( MessageCommands.ERROR_MESSAGE_STRING,
					rapp.getErrorMessage() );
		rapp.sendBroadcast( i, Permissions.QUERY_STATUS );
	}
}
