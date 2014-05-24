/*
 * Copyright (c) 2011-2013 by Curt Binder (http://curtbinder.info)
 * 
 * This work is made available under the terms of the Creative Commons
 * Attribution-NonCommercial-ShareAlike 3.0 Unported License
 * http://creativecommons.org/licenses/by-nc-sa/3.0/
 */

package info.curtbinder.reefangel.service;

import info.curtbinder.reefangel.controller.Controller;
import info.curtbinder.reefangel.controller.DateTime;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import android.util.Log;

public class XMLHandler extends DefaultHandler {

	private static final String TAG = XMLHandler.class.getSimpleName();
	private String currentElementText = "";
	private String requestType = "";
	private Controller ra;
	private String version = "";
	private String memoryResponse = "";
	private String modeResponse = "";
	// expansion relays
	// for 0.8.5.x use 0 based data
	// for 0.9.x and later use 1 based data
	private boolean fUse085XRelays = false;

	private DateTime dt;

	public Controller getRa ( ) {
		return ra;
	}

	public String getVersion ( ) {
		return version;
	}

	public String getDateTime ( ) {
		return dt.getDateTimeString();
	}

	public String getDateTimeUpdateStatus ( ) {
		return dt.getUpdateStatus();
	}

	public String getMemoryResponse ( ) {
		return memoryResponse;
	}

	public String getModeResponse ( ) {
		return modeResponse;
	}

	public String getRequestType ( ) {
		return requestType;
	}

	public XMLHandler () {
		super();
		this.ra = new Controller();
		this.dt = new DateTime();
	}

	public void setOld085xExpansion ( boolean f ) {
		this.fUse085XRelays = f;
	}

	public void endDocument ( ) throws SAXException {
		if ( ra.getLogDate().equals( "" ) ) {
			// No log date found, set the date to be the current date/time
			DateFormat dft =
					DateFormat.getDateTimeInstance( DateFormat.DEFAULT,
													DateFormat.DEFAULT,
													Locale.getDefault() );
			ra.setLogDate( dft.format( new Date() ) );
		}
	}

	public void characters ( char[] ch, int start, int length )
			throws SAXException {
		String s = new String( ch, start, length );
		currentElementText += s;
	}

	public void endElement ( String uri, String localName, String qName )
			throws SAXException {
		String tag;
		if ( !qName.equals( "" ) ) {
			// Log.d(TAG, "end xml: qName");
			tag = qName;
		} else {
			// Log.d(TAG, "end xml: localName");
			tag = localName;
		}
		// Log.d(TAG, "End: r'" + requestType + "', t'" + tag + "', '" +
		// currentElementText + "'");
		// if ( (requestType.equals( RequestCommands.Status )) ||
		// (requestType.startsWith( RequestCommands.Relay )) ) {
		if ( requestType.equals( RequestCommands.Status ) ) {
			if ( tag.equals( XMLTags.Status ) ) {
				return;
			} else {
				// Parameter status and Labels are sent using the same XML outer
				// tag
				if ( tag.endsWith( XMLTags.LabelEnd )
						&& !tag.equals( XMLTags.RelayMaskOn ) ) {
					processLabelXml( tag );
				} else {
					processStatusXml( tag );
				}
			}
		} else if ( requestType.equals( RequestCommands.MemoryByte ) ) {
			if ( tag.equals( XMLTags.Memory ) ) {
				return;
			} else {
				processMemoryXml( tag );
			}

		} else if ( requestType.equals( RequestCommands.DateTime ) ) {
			if ( tag.equals( XMLTags.DateTime ) ) {
				if ( !currentElementText.equals( "" ) ) {
					// not empty meaning we have a status to report
					// either OK or ERR
					dt.setStatus( currentElementText );
				}
				return;
			} else {
				processDateTimeXml( tag );
			}

		} else if ( requestType.equals( RequestCommands.Version ) ) {
			processVersionXml( tag );
		} else if ( requestType.equals( RequestCommands.ExitMode ) ) {
			processModeXml( tag );
		} else {
			// TODO request none, set an error?
			Log.d( TAG, "Unknown Request: " + requestType );
		}
		currentElementText = "";
	}

	//
	public void startElement (
			String uri,
			String localName,
			String qName,
			Attributes attributes ) throws SAXException {
		String tag;
		if ( !qName.equals( "" ) ) {
			// Log.d(TAG, "start xml: qName");
			tag = qName;
		} else {
			// Log.d(TAG, "start xml: localName");
			tag = localName;
		}
		// Log.d(TAG, "start: r'" + requestType + "', t'" + tag + "'");
		if ( requestType.equals( "" ) ) {
			// no request type, so set it based on the first element we process
			if ( tag.equals( XMLTags.Status ) ) {
				requestType = RequestCommands.Status;
			} else if ( qName.equals( XMLTags.DateTime ) ) {
				requestType = RequestCommands.DateTime;
			} else if ( tag.equals( XMLTags.Version ) ) {
				requestType = RequestCommands.Version;
			} else if ( tag.equals( XMLTags.Mode ) ) {
				// all modes return the same response, just chose to use Exit
				// Mode
				requestType = RequestCommands.ExitMode;
			} else if ( tag.startsWith( XMLTags.MemorySingle ) ) {
				// can be either type, just chose to use Bytes
				requestType = RequestCommands.MemoryByte;
			} else {
				requestType = RequestCommands.None;
			}
		}
	}

	private void processStatusXml ( String tag ) {
		if ( tag.equals( XMLTags.T1 ) ) {
			ra.setTemp1( Integer.parseInt( currentElementText ) );
		} else if ( tag.equals( XMLTags.T2 ) ) {
			ra.setTemp2( Integer.parseInt( currentElementText ) );
		} else if ( tag.equals( XMLTags.T3 ) ) {
			ra.setTemp3( Integer.parseInt( currentElementText ) );
		} else if ( tag.equals( XMLTags.PH ) ) {
			ra.setPH( Integer.parseInt( currentElementText ) );
		} else if ( tag.equals( XMLTags.PHExpansion ) ) {
			ra.setPHExp( Integer.parseInt( currentElementText ) );
		} else if ( tag.equals( XMLTags.ATOLow ) ) {
			boolean f = false;
			if ( Short.parseShort( currentElementText ) == 1 ) {
				f = true;
			}
			ra.setAtoLow( f );
		} else if ( tag.equals( XMLTags.ATOHigh ) ) {
			boolean f = false;
			if ( Short.parseShort( currentElementText ) == 1 ) {
				f = true;
			}
			ra.setAtoHigh( f );
		} else if ( tag.equals( XMLTags.PWMActinic ) ) {
			short v = Short.parseShort( currentElementText );
			ra.setPwmA( v );
		} else if ( tag.equals( XMLTags.PWMDaylight ) ) {
			short v = Short.parseShort( currentElementText );
			ra.setPwmD( v );
		} else if ( tag.startsWith( XMLTags.PWMExpansion ) && 
				!tag.endsWith( XMLTags.Override )) {
			short channel =
					Short.parseShort( tag.substring( XMLTags.PWMExpansion
							.length() ) );
			short v = Short.parseShort( currentElementText );
			ra.setPwmExpansion( channel, v );
		} else if ( tag.equals( XMLTags.Salinity ) ) {
			ra.setSalinity( Integer.parseInt( currentElementText ) );
		} else if ( tag.equals( XMLTags.ORP ) ) {
			ra.setORP( Integer.parseInt( currentElementText ) );
		} else if ( tag.equals( XMLTags.WaterLevel ) ) {
			// 1 channel water level, tag is WL
			short v = Short.parseShort( currentElementText );
			ra.setWaterLevel( (short) 0, v );
		} else if ( tag.equals( XMLTags.Relay ) ) {
			ra.setMainRelayData( Short.parseShort( currentElementText ) );
		} else if ( tag.equals( XMLTags.RelayMaskOn ) ) {
			ra.setMainRelayOnMask( Short.parseShort( currentElementText ) );
		} else if ( tag.equals( XMLTags.RelayMaskOff ) ) {
			ra.setMainRelayOffMask( Short.parseShort( currentElementText ) );
		} else if ( tag.equals( XMLTags.LogDate ) ) {
			ra.setLogDate( currentElementText );
		} else if ( tag.equals( XMLTags.RelayExpansionModules ) ) {
			short v = Short.parseShort( currentElementText );
			ra.setRelayExpansionModules( v );
		} else if ( tag.equals( XMLTags.ExpansionModules ) ) {
			short v = Short.parseShort( currentElementText );
			ra.setExpansionModules( v );
		} else if ( tag.equals( XMLTags.ExpansionModules1 ) ) {
			short v = Short.parseShort( currentElementText );
			ra.setExpansionModules1( v );
		} else if ( tag.equals( XMLTags.AIBlue ) ) {
			ra.setAIChannel(	Controller.AI_BLUE,
								Short.parseShort( currentElementText ) );
		} else if ( tag.equals( XMLTags.AIRoyalBlue ) ) {
			ra.setAIChannel(	Controller.AI_ROYALBLUE,
								Short.parseShort( currentElementText ) );
		} else if ( tag.equals( XMLTags.AIWhite ) ) {
			ra.setAIChannel(	Controller.AI_WHITE,
								Short.parseShort( currentElementText ) );
		} else if ( tag.equals( XMLTags.RFMode ) ) {
			ra.setVortechValue( Controller.VORTECH_MODE,
								Short.parseShort( currentElementText ) );
		} else if ( tag.equals( XMLTags.RFSpeed ) ) {
			ra.setVortechValue( Controller.VORTECH_SPEED,
								Short.parseShort( currentElementText ) );
		} else if ( tag.equals( XMLTags.RFDuration ) ) {
			ra.setVortechValue( Controller.VORTECH_DURATION,
								Short.parseShort( currentElementText ) );
		} else if ( tag.equals( XMLTags.RFWhite ) ) {
			ra.setRadionChannel(	Controller.RADION_WHITE,
									Short.parseShort( currentElementText ) );
		} else if ( tag.equals( XMLTags.RFBlue ) ) {
			ra.setRadionChannel(	Controller.RADION_BLUE,
									Short.parseShort( currentElementText ) );
		} else if ( tag.equals( XMLTags.RFGreen ) ) {
			ra.setRadionChannel(	Controller.RADION_GREEN,
									Short.parseShort( currentElementText ) );
		} else if ( tag.equals( XMLTags.RFRed ) ) {
			ra.setRadionChannel(	Controller.RADION_RED,
									Short.parseShort( currentElementText ) );
		} else if ( tag.equals( XMLTags.RFRoyalBlue ) ) {
			ra.setRadionChannel(	Controller.RADION_ROYALBLUE,
									Short.parseShort( currentElementText ) );
		} else if ( tag.equals( XMLTags.RFIntensity ) ) {
			ra.setRadionChannel(	Controller.RADION_INTENSITY,
									Short.parseShort( currentElementText ) );
		} else if ( tag.equals( XMLTags.IO ) ) {
			ra.setIOChannels( Short.parseShort( currentElementText ) );
		} else if ( tag.endsWith( XMLTags.Override ) ) {
			// FIXME Handle Override Tags
			Log.d( TAG, "Unhandled Override tag (" + tag + ") with data: "
					+ currentElementText );
		} else if ( tag.startsWith( XMLTags.Custom ) ) {
			short v =
					Short.parseShort( tag.substring( XMLTags.Custom.length() ) );
			short c = Short.parseShort( currentElementText );
			ra.setCustomVariable( v, c );
		} else if ( tag.startsWith( XMLTags.WaterLevel ) ) {
			// 4 channel water level, tags start with WL
			short p = Short.parseShort( tag.substring( XMLTags.WaterLevel.length() ) );
			short v = Short.parseShort( currentElementText );
			ra.setWaterLevel( p, v );
		} else if ( tag.startsWith( XMLTags.RelayMaskOn ) ) {
			int relay =
					Integer.parseInt( tag.substring( XMLTags.RelayMaskOn
							.length() ) );
			if ( fUse085XRelays )
				relay += 1;
			ra.setExpRelayOnMask( relay, Short.parseShort( currentElementText ) );
		} else if ( tag.startsWith( XMLTags.RelayMaskOff ) ) {
			int relay =
					Integer.parseInt( tag.substring( XMLTags.RelayMaskOff
							.length() ) );
			if ( fUse085XRelays )
				relay += 1;
			ra.setExpRelayOffMask( relay, Short.parseShort( currentElementText ) );
		} else if ( tag.startsWith( XMLTags.Relay ) ) {
			try {
				int relay =
						Integer.parseInt( tag.substring( XMLTags.Relay.length() ) );
				if ( fUse085XRelays )
					relay += 1;
				ra.setExpRelayData( relay, Short.parseShort( currentElementText ) );
			} catch ( NumberFormatException e ) {
				Log.e( TAG, "Invalid XML tag: " + tag );
			}
		} else if ( tag.equals( XMLTags.MyReefAngelID ) ) {
			Log.d( TAG, "Reefangel ID: " + currentElementText );
		} else {
			Log.d( TAG, "Unhandled XML tag (" + tag + ") with data: "
						+ currentElementText );
		}
	}

	private String getTagNumber ( String tag, String start, String end ) {
		int ep = tag.indexOf( end );
		int bp = start.length();
		return tag.substring( bp, ep );
	}

	private void processLabelXml ( String tag ) {
		// Handle all labels here
		if ( currentElementText.equals( "null" ) ) {
			Log.d( TAG, tag + " is null, skipping" );
			return;
		}

		if ( tag.startsWith( XMLTags.LabelTempBegin ) ) {
			// handle temp sensor labels
			int sensor =
					Integer.parseInt( getTagNumber( tag,
													XMLTags.LabelTempBegin,
													XMLTags.LabelEnd ) );
			if ( sensor < 0 || sensor > Controller.MAX_TEMP_SENSORS )
				Log.e( TAG, "Incorrect sensor number: " + tag );
			ra.setTempLabel( sensor, currentElementText );
		} else if ( tag.startsWith( XMLTags.PWMExpansion ) ) {
			// PWME
			short channel =
					Short.parseShort( getTagNumber( tag, XMLTags.PWMExpansion,
													XMLTags.LabelEnd ) );
			ra.setPwmExpansionLabel( channel, currentElementText );
		} else if ( tag.startsWith( XMLTags.PWMActinic + "1" ) ) {
			// PWMA
			ra.setPwmALabel( currentElementText );
		} else if ( tag.startsWith( XMLTags.PWMDaylight + "1" ) ) {
			// PWMD
			ra.setPwmDLabel( currentElementText );
		} else if ( tag.equals( XMLTags.PHExpansion + XMLTags.LabelEnd ) ) {
			// PHE
			ra.setPHExpLabel( currentElementText );
		} else if ( tag.equals( XMLTags.PH + XMLTags.LabelEnd ) ) {
			// PH
			ra.setPHLabel( currentElementText );
		} else if ( tag.startsWith( XMLTags.Salinity ) ) {
			// SAL
			ra.setSalinityLabel( currentElementText );
		} else if ( tag.startsWith( XMLTags.ORP ) ) {
			// ORP
			ra.setORPLabel( currentElementText );
		} else if ( tag.equals( XMLTags.WaterLevel + XMLTags.LabelEnd ) ) {
			// 1 channel Water Level
			ra.setWaterLevelLabel( (short) 0, currentElementText );
		} else if ( tag.startsWith( XMLTags.WaterLevel ) ) {
			// 4 channel Water Level
			short p = Short.parseShort( getTagNumber(tag, XMLTags.WaterLevel, XMLTags.LabelEnd) );
			ra.setWaterLevelLabel( p, currentElementText );;
		} else if ( tag.startsWith( XMLTags.Custom ) ) {
			// C
			short v =
					Short.parseShort( getTagNumber( tag, XMLTags.Custom,
													XMLTags.LabelEnd ) );
			ra.setCustomVariableLabel( v, currentElementText );
		} else if ( tag.startsWith( XMLTags.IO ) ) {
			// IO
			short v =
					Short.parseShort( getTagNumber( tag, XMLTags.IO,
													XMLTags.LabelEnd ) );
			ra.setIOChannelLabel( v, currentElementText );
		} else if ( tag.startsWith( XMLTags.RFBlue )
					|| tag.startsWith( XMLTags.RFGreen )
					|| tag.startsWith( XMLTags.RFIntensity )
					|| tag.startsWith( XMLTags.RFRed )
					|| tag.startsWith( XMLTags.RFRoyalBlue )
					|| tag.startsWith( XMLTags.RFWhite ) ) {
			// RF labels, not stored only handled to prevent
			// errors due to processing like a Relay label because
			// they start with R
			Log.d( TAG, "RF Label" );
		} else if ( tag.startsWith( XMLTags.Relay ) ) {
			// handle relay labels
			int relay =
					Integer.parseInt( getTagNumber( tag, XMLTags.Relay,
													XMLTags.LabelEnd ) );
			if ( relay < 10 ) {
				// main relay
				ra.getMainRelay().setPortLabel( relay, currentElementText );
			} else {
				// expansion relays, so split the port from the relay box
				int box = relay / 10;
				int port = relay % 10;
				ra.getExpRelay( box ).setPortLabel( port, currentElementText );
			}
		} else {
			Log.d( TAG, "Unknown label: (" + tag + ") = " + currentElementText );
		}
	}

	private void processDateTimeXml ( String tag ) {
		// Response will be more XML data or OK
		if ( tag.equals( XMLTags.Hour ) ) {
			dt.setHour( Integer.parseInt( currentElementText ) );
		} else if ( tag.equals( XMLTags.Minute ) ) {
			dt.setMinute( Integer.parseInt( currentElementText ) );
		} else if ( tag.equals( XMLTags.Month ) ) {
			// controller uses 1 based for month
			// java uses 0 based for month
			dt.setMonth( Integer.parseInt( currentElementText ) - 1 );
		} else if ( tag.equals( XMLTags.Day ) ) {
			dt.setDay( Integer.parseInt( currentElementText ) );
		} else if ( tag.equals( XMLTags.Year ) ) {
			dt.setYear( Integer.parseInt( currentElementText ) );
		}
	}

	private void processVersionXml ( String tag ) {
		// Response will be the Version
		if ( tag.equals( XMLTags.Version ) ) {
			version = currentElementText;
		}
	}

	private void processMemoryXml ( String tag ) {
		// Responses will be either: OK, value, ERR
		if ( tag.startsWith( XMLTags.MemorySingle ) ) {
			memoryResponse = currentElementText;
		}
	}

	private void processModeXml ( String tag ) {
		// Response will be either: OK or ERR
		if ( tag.startsWith( XMLTags.Mode ) ) {
			modeResponse = currentElementText;
		}
	}
}
