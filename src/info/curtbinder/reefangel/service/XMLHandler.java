package info.curtbinder.reefangel.service;

/*
 * Copyright (c) 2011-12 by Curt Binder (http://curtbinder.info)
 *
 * This work is made available under the terms of the 
 * Creative Commons Attribution-NonCommercial-ShareAlike 3.0 Unported License
 * http://creativecommons.org/licenses/by-nc-sa/3.0/
 */

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

	@Override
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

	@Override
	public void characters ( char[] ch, int start, int length )
			throws SAXException {
		String s = new String( ch, start, length );
		currentElementText += s;
	}

	@Override
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
			if ( tag.equals( XMLTags.xmlStatus ) ) {
				return;
			} else {
				// Parameter status and Labels are sent using the same XML outer
				// tag
				if ( tag.endsWith( XMLTags.xmlLabelEnd )
						&& !tag.equals( XMLTags.xmlRelayMaskOn ) ) {
					processLabelXml( tag );
				} else {
					processStatusXml( tag );
				}
			}
		} else if ( requestType.equals( RequestCommands.MemoryByte ) ) {
			if ( tag.equals( XMLTags.xmlMemory ) ) {
				return;
			} else {
				processMemoryXml( tag );
			}

		} else if ( requestType.equals( RequestCommands.DateTime ) ) {
			if ( tag.equals( XMLTags.xmlDateTime ) ) {
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

	// @Override
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
			if ( tag.equals( XMLTags.xmlStatus ) ) {
				requestType = RequestCommands.Status;
			} else if ( qName.equals( XMLTags.xmlDateTime ) ) {
				requestType = RequestCommands.DateTime;
			} else if ( tag.equals( XMLTags.xmlVersion ) ) {
				requestType = RequestCommands.Version;
			} else if ( tag.equals( XMLTags.xmlMode ) ) {
				// all modes return the same response, just chose to use Exit
				// Mode
				requestType = RequestCommands.ExitMode;
			} else if ( tag.startsWith( XMLTags.xmlMemorySingle ) ) {
				// can be either type, just chose to use Bytes
				requestType = RequestCommands.MemoryByte;
			} else {
				requestType = RequestCommands.None;
			}
		}
	}

	private void processStatusXml ( String tag ) {
		Log.d( TAG, "statusXML: " + tag );
		if ( tag.equals( XMLTags.xmlT1 ) ) {
			ra.setTemp1( Integer.parseInt( currentElementText ) );
		} else if ( tag.equals( XMLTags.xmlT2 ) ) {
			ra.setTemp2( Integer.parseInt( currentElementText ) );
		} else if ( tag.equals( XMLTags.xmlT3 ) ) {
			ra.setTemp3( Integer.parseInt( currentElementText ) );
		} else if ( tag.equals( XMLTags.xmlPH ) ) {
			ra.setPH( Integer.parseInt( currentElementText ) );
		} else if ( tag.equals( XMLTags.xmlPHExpansion ) ) {
			ra.setPHExp( Integer.parseInt( currentElementText ) );
		} else if ( tag.equals( XMLTags.xmlATOLow ) ) {
			boolean f = false;
			if ( Short.parseShort( currentElementText ) == 1 ) {
				f = true;
			}
			ra.setAtoLow( f );
		} else if ( tag.equals( XMLTags.xmlATOHigh ) ) {
			boolean f = false;
			if ( Short.parseShort( currentElementText ) == 1 ) {
				f = true;
			}
			ra.setAtoHigh( f );
		} else if ( tag.equals( XMLTags.xmlPWMActinic ) ) {
			short v = Short.parseShort( currentElementText );
			ra.setPwmA( v );
		} else if ( tag.equals( XMLTags.xmlPWMDaylight ) ) {
			short v = Short.parseShort( currentElementText );
			ra.setPwmD( v );
		} else if ( tag.startsWith( XMLTags.xmlPWMExpansion ) ) {
			short channel =
					Short.parseShort( tag.substring( XMLTags.xmlPWMExpansion
							.length() ) );
			short v = Short.parseShort( currentElementText );
			ra.setPwmExpansion( channel, v );
		} else if ( tag.equals( XMLTags.xmlSalinity ) ) {
			ra.setSalinity( Integer.parseInt( currentElementText ) );
		} else if ( tag.equals( XMLTags.xmlORP ) ) {
			ra.setORP( Integer.parseInt( currentElementText ) );
		} else if ( tag.equals( XMLTags.xmlWaterLevel ) ) {
			short v = Short.parseShort( currentElementText );
			ra.setWaterLevel( v );
		} else if ( tag.equals( XMLTags.xmlRelay ) ) {
			ra.setMainRelayData( Short.parseShort( currentElementText ) );
		} else if ( tag.equals( XMLTags.xmlRelayMaskOn ) ) {
			ra.setMainRelayOnMask( Short.parseShort( currentElementText ) );
		} else if ( tag.equals( XMLTags.xmlRelayMaskOff ) ) {
			ra.setMainRelayOffMask( Short.parseShort( currentElementText ) );
		} else if ( tag.equals( XMLTags.xmlLogDate ) ) {
			ra.setLogDate( currentElementText );
		} else if ( tag.equals( XMLTags.xmlRelayExpansionModules ) ) {
			short v = Short.parseShort( currentElementText );
			ra.setRelayExpansionModules( v );
		} else if ( tag.equals( XMLTags.xmlExpansionModules ) ) {
			short v = Short.parseShort( currentElementText );
			ra.setExpansionModules( v );
		} else if ( tag.equals( XMLTags.xmlAIBlue ) ) {
			ra.setAIChannel(	Controller.AI_BLUE,
								Short.parseShort( currentElementText ) );
		} else if ( tag.equals( XMLTags.xmlAIRoyalBlue ) ) {
			ra.setAIChannel(	Controller.AI_ROYALBLUE,
								Short.parseShort( currentElementText ) );
		} else if ( tag.equals( XMLTags.xmlAIWhite ) ) {
			ra.setAIChannel(	Controller.AI_WHITE,
								Short.parseShort( currentElementText ) );
		} else if ( tag.equals( XMLTags.xmlRFMode ) ) {
			ra.setVortechValue( Controller.VORTECH_MODE,
								Short.parseShort( currentElementText ) );
		} else if ( tag.equals( XMLTags.xmlRFSpeed ) ) {
			ra.setVortechValue( Controller.VORTECH_SPEED,
								Short.parseShort( currentElementText ) );
		} else if ( tag.equals( XMLTags.xmlRFDuration ) ) {
			ra.setVortechValue( Controller.VORTECH_DURATION,
								Short.parseShort( currentElementText ) );
		} else if ( tag.equals( XMLTags.xmlRFWhite ) ) {
			ra.setRadionChannel(	Controller.RADION_WHITE,
									Short.parseShort( currentElementText ) );
		} else if ( tag.equals( XMLTags.xmlRFBlue ) ) {
			ra.setRadionChannel(	Controller.RADION_BLUE,
									Short.parseShort( currentElementText ) );
		} else if ( tag.equals( XMLTags.xmlRFGreen ) ) {
			ra.setRadionChannel(	Controller.RADION_GREEN,
									Short.parseShort( currentElementText ) );
		} else if ( tag.equals( XMLTags.xmlRFRed ) ) {
			ra.setRadionChannel(	Controller.RADION_RED,
									Short.parseShort( currentElementText ) );
		} else if ( tag.equals( XMLTags.xmlRFRoyalBlue ) ) {
			ra.setRadionChannel(	Controller.RADION_ROYALBLUE,
									Short.parseShort( currentElementText ) );
		} else if ( tag.equals( XMLTags.xmlRFIntensity ) ) {
			ra.setRadionChannel(	Controller.RADION_INTENSITY,
									Short.parseShort( currentElementText ) );
		} else if ( tag.equals( XMLTags.xmlIO ) ) {
			ra.setIOChannels( Short.parseShort( currentElementText ) );
		} else if ( tag.startsWith( XMLTags.xmlCustom ) ) {
			short v =
					Short.parseShort( tag.substring( XMLTags.xmlCustom.length() ) );
			short c = Short.parseShort( currentElementText );
			ra.setCustomVariable( v, c );
		} else if ( tag.startsWith( XMLTags.xmlRelayMaskOn ) ) {
			int relay =
					Integer.parseInt( tag.substring( XMLTags.xmlRelayMaskOn
							.length() ) );
			if ( fUse085XRelays )
				relay += 1;
			ra.setExpRelayOnMask( relay, Short.parseShort( currentElementText ) );
		} else if ( tag.startsWith( XMLTags.xmlRelayMaskOff ) ) {
			int relay =
					Integer.parseInt( tag.substring( XMLTags.xmlRelayMaskOff
							.length() ) );
			if ( fUse085XRelays )
				relay += 1;
			ra.setExpRelayOffMask( relay, Short.parseShort( currentElementText ) );
		} else if ( tag.startsWith( XMLTags.xmlRelay ) ) {
			int relay =
					Integer.parseInt( tag.substring( XMLTags.xmlRelay.length() ) );
			if ( fUse085XRelays )
				relay += 1;
			ra.setExpRelayData( relay, Short.parseShort( currentElementText ) );
		} else if ( tag.equals( XMLTags.xmlMyReefAngelID ) ) {
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

		if ( tag.startsWith( XMLTags.xmlLabelTempBegin ) ) {
			// handle temp sensor labels
			int sensor =
					Integer.parseInt( getTagNumber( tag,
													XMLTags.xmlLabelTempBegin,
													XMLTags.xmlLabelEnd ) );
			if ( sensor < 0 || sensor > Controller.MAX_TEMP_SENSORS )
				Log.e( TAG, "Incorrect sensor number: " + tag );
			ra.setTempLabel( sensor, currentElementText );
		} else if ( tag.startsWith( XMLTags.xmlRelay ) ) {
			// handle relay labels
			int relay =
					Integer.parseInt( getTagNumber( tag, XMLTags.xmlRelay,
													XMLTags.xmlLabelEnd ) );
			if ( relay < 10 ) {
				// main relay
				ra.getMainRelay().setPortLabel( relay, currentElementText );
			} else {
				// expansion relays, so split the port from the relay box
				int box = relay / 10;
				int port = relay % 10;
				ra.getExpRelay( box ).setPortLabel( port, currentElementText );
			}
		} else if ( tag.startsWith( XMLTags.xmlPWMExpansion ) ) {
			// PWME
			short channel =
					Short.parseShort( getTagNumber( tag,
													XMLTags.xmlPWMExpansion,
													XMLTags.xmlLabelEnd ) );
			Log.d( TAG, "PWM #" + channel + ": " + currentElementText );
			ra.setPwmExpansionLabel( channel, currentElementText );
		} else if ( tag.equals( XMLTags.xmlPHExpansion + XMLTags.xmlLabelEnd ) ) {
			// PHE
			Log.d( TAG, "PHExp Label: " + currentElementText );
			ra.setPHExpLabel( currentElementText );
		} else if ( tag.equals( XMLTags.xmlPH + XMLTags.xmlLabelEnd ) ) {
			// PH
			Log.d( TAG, "PH Label: " + currentElementText );
			ra.setPHLabel( currentElementText );
		} else if ( tag.equals( XMLTags.xmlSalinity + XMLTags.xmlLabelEnd ) ) {
			// SAL
			Log.d( TAG, "Salinity Label: " + currentElementText );
			ra.setSalinityLabel( currentElementText );
		} else if ( tag.equals( XMLTags.xmlORP + XMLTags.xmlLabelEnd ) ) {
			// ORP
			Log.d( TAG, "ORP Label: " + currentElementText );
			ra.setORPLabel( currentElementText );
		} else if ( tag.startsWith( XMLTags.xmlCustom ) ) {
			// C
			short v =
					Short.parseShort( getTagNumber( tag, XMLTags.xmlCustom,
													XMLTags.xmlLabelEnd ) );
			Log.d( TAG, "Custom #" + v + ": " + currentElementText );
			ra.setCustomVariableLabel( v, currentElementText );
		} else if ( tag.startsWith( XMLTags.xmlIO ) ) {
			// IO
			short v =
					Short.parseShort( getTagNumber( tag, XMLTags.xmlIO,
													XMLTags.xmlLabelEnd ) );
			Log.d( TAG, "IO #" + v + ": " + currentElementText );
			ra.setIOChannelLabel( v, currentElementText );
		} else {
			Log.d( TAG, "Unknown label: (" + tag + ") = " + currentElementText );
		}
	}

	private void processDateTimeXml ( String tag ) {
		// Response will be more XML data or OK
		if ( tag.equals( XMLTags.xmlHour ) ) {
			dt.setHour( Integer.parseInt( currentElementText ) );
		} else if ( tag.equals( XMLTags.xmlMinute ) ) {
			dt.setMinute( Integer.parseInt( currentElementText ) );
		} else if ( tag.equals( XMLTags.xmlMonth ) ) {
			// controller uses 1 based for month
			// java uses 0 based for month
			dt.setMonth( Integer.parseInt( currentElementText ) - 1 );
		} else if ( tag.equals( XMLTags.xmlDay ) ) {
			dt.setDay( Integer.parseInt( currentElementText ) );
		} else if ( tag.equals( XMLTags.xmlYear ) ) {
			dt.setYear( Integer.parseInt( currentElementText ) );
		}
	}

	private void processVersionXml ( String tag ) {
		// Response will be the Version
		if ( tag.equals( XMLTags.xmlVersion ) ) {
			version = currentElementText;
		}
	}

	private void processMemoryXml ( String tag ) {
		// Responses will be either: OK, value, ERR
		if ( tag.startsWith( XMLTags.xmlMemorySingle ) ) {
			memoryResponse = currentElementText;
		}
	}

	private void processModeXml ( String tag ) {
		// Response will be either: OK or ERR
		if ( tag.startsWith( XMLTags.xmlMode ) ) {
			modeResponse = currentElementText;
		}
	}
}
