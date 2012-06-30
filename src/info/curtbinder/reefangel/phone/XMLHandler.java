package info.curtbinder.reefangel.phone;

/*
 * Copyright (c) 2011-12 by Curt Binder (http://curtbinder.info)
 *
 * This work is made available under the terms of the 
 * Creative Commons Attribution-NonCommercial-ShareAlike 3.0 Unported License
 * http://creativecommons.org/licenses/by-nc-sa/3.0/
 */

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
		// if ( (requestType.equals( Globals.requestStatus )) ||
		// (requestType.startsWith( Globals.requestRelay )) ) {
		if ( requestType.equals( Globals.requestStatus ) ) {
			if ( tag.equals( Globals.xmlStatus ) ) {
				return;
			} else {
				// Parameter status and Labels are sent using the same XML outer
				// tag
				if ( tag.endsWith( Globals.xmlLabelEnd ) ) {
					processLabelXml( tag );
				} else {
					processStatusXml( tag );
				}
			}
		} else if ( requestType.equals( Globals.requestMemoryByte ) ) {
			if ( tag.equals( Globals.xmlMemory ) ) {
				return;
			} else {
				processMemoryXml( tag );
			}

		} else if ( requestType.equals( Globals.requestDateTime ) ) {
			if ( tag.equals( Globals.xmlDateTime ) ) {
				if ( !currentElementText.equals( "" ) ) {
					// not empty meaning we have a status to report
					// either OK or ERR
					dt.setStatus( currentElementText );
				}
				return;
			} else {
				processDateTimeXml( tag );
			}

		} else if ( requestType.equals( Globals.requestVersion ) ) {
			processVersionXml( tag );
		} else if ( requestType.equals( Globals.requestExitMode ) ) {
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
			if ( tag.equals( Globals.xmlStatus ) ) {
				requestType = Globals.requestStatus;
			} else if ( qName.equals( Globals.xmlDateTime ) ) {
				requestType = Globals.requestDateTime;
			} else if ( tag.equals( Globals.xmlVersion ) ) {
				requestType = Globals.requestVersion;
			} else if ( tag.equals( Globals.xmlMode ) ) {
				// all modes return the same response, just chose to use Exit
				// Mode
				requestType = Globals.requestExitMode;
			} else if ( tag.startsWith( Globals.xmlMemorySingle ) ) {
				// can be either type, just chose to use Bytes
				requestType = Globals.requestMemoryByte;
			} else {
				requestType = Globals.requestNone;
			}
		}
	}

	private void processStatusXml ( String tag ) {
		Log.d( TAG, "statusXML: " + tag );
		if ( tag.equals( Globals.xmlT1 ) ) {
			ra.setTemp1( Integer.parseInt( currentElementText ) );
		} else if ( tag.equals( Globals.xmlT2 ) ) {
			ra.setTemp2( Integer.parseInt( currentElementText ) );
		} else if ( tag.equals( Globals.xmlT3 ) ) {
			ra.setTemp3( Integer.parseInt( currentElementText ) );
		} else if ( tag.equals( Globals.xmlPH ) ) {
			ra.setPH( Integer.parseInt( currentElementText ) );
		} else if ( tag.equals( Globals.xmlATOLow ) ) {
			boolean f = false;
			if ( Short.parseShort( currentElementText ) == 1 ) {
				f = true;
			}
			ra.setAtoLow( f );
		} else if ( tag.equals( Globals.xmlATOHigh ) ) {
			boolean f = false;
			if ( Short.parseShort( currentElementText ) == 1 ) {
				f = true;
			}
			ra.setAtoHigh( f );
		} else if ( tag.equals( Globals.xmlPWMActinic ) ) {
			short v = Short.parseShort( currentElementText );
			ra.setPwmA( v );
		} else if ( tag.equals( Globals.xmlPWMDaylight ) ) {
			short v = Short.parseShort( currentElementText );
			ra.setPwmD( v );
		} else if ( tag.startsWith( Globals.xmlPWMExpansion ) ) {
			short channel =
					Short.parseShort( tag.substring( Globals.xmlPWMExpansion
							.length() ) );
			short v = Short.parseShort( currentElementText );
			ra.setPwmExpansion( channel, v );
		} else if ( tag.equals( Globals.xmlSalinity ) ) {
			ra.setSalinity( Integer.parseInt( currentElementText ) );
		} else if ( tag.equals( Globals.xmlORP ) ) {
			ra.setORP( Integer.parseInt( currentElementText ) );
		} else if ( tag.equals( Globals.xmlRelay ) ) {
			ra.setMainRelayData( Short.parseShort( currentElementText ) );
		} else if ( tag.equals( Globals.xmlRelayMaskOn ) ) {
			ra.setMainRelayOnMask( Short.parseShort( currentElementText ) );
		} else if ( tag.equals( Globals.xmlRelayMaskOff ) ) {
			ra.setMainRelayOffMask( Short.parseShort( currentElementText ) );
		} else if ( tag.equals( Globals.xmlLogDate ) ) {
			ra.setLogDate( currentElementText );
		} else if ( tag.equals( Globals.xmlRelayExpansionModules ) ) {
			short v = Short.parseShort( currentElementText );
			ra.setRelayExpansionModules( v );
		} else if ( tag.equals( Globals.xmlExpansionModules ) ) {
			short v = Short.parseShort( currentElementText );
			ra.setExpansionModules( v );
		} else if ( tag.equals( Globals.xmlAIBlue ) ) {
			ra.setAIChannel(	Controller.AI_BLUE,
								Short.parseShort( currentElementText ) );
		} else if ( tag.equals( Globals.xmlAIRoyalBlue ) ) {
			ra.setAIChannel(	Controller.AI_ROYALBLUE,
								Short.parseShort( currentElementText ) );
		} else if ( tag.equals( Globals.xmlAIWhite ) ) {
			ra.setAIChannel(	Controller.AI_WHITE,
								Short.parseShort( currentElementText ) );
		} else if ( tag.equals( Globals.xmlRFMode ) ) {
			ra.setVortechValue( Controller.VORTECH_MODE,
								Short.parseShort( currentElementText ) );
		} else if ( tag.equals( Globals.xmlRFSpeed ) ) {
			ra.setVortechValue( Controller.VORTECH_SPEED,
								Short.parseShort( currentElementText ) );
		} else if ( tag.equals( Globals.xmlRFDuration ) ) {
			ra.setVortechValue( Controller.VORTECH_DURATION,
								Short.parseShort( currentElementText ) );
		} else if ( tag.equals( Globals.xmlRFWhite ) ) {
			ra.setRadionChannel(	Controller.RADION_WHITE,
									Short.parseShort( currentElementText ) );
		} else if ( tag.equals( Globals.xmlRFBlue ) ) {
			ra.setRadionChannel(	Controller.RADION_BLUE,
									Short.parseShort( currentElementText ) );
		} else if ( tag.equals( Globals.xmlRFGreen ) ) {
			ra.setRadionChannel(	Controller.RADION_GREEN,
									Short.parseShort( currentElementText ) );
		} else if ( tag.equals( Globals.xmlRFRed ) ) {
			ra.setRadionChannel(	Controller.RADION_RED,
									Short.parseShort( currentElementText ) );
		} else if ( tag.equals( Globals.xmlRFRoyalBlue ) ) {
			ra.setRadionChannel(	Controller.RADION_ROYALBLUE,
									Short.parseShort( currentElementText ) );
		} else if ( tag.equals( Globals.xmlRFIntensity ) ) {
			ra.setRadionChannel(	Controller.RADION_INTENSITY,
									Short.parseShort( currentElementText ) );
		} else if ( tag.equals( Globals.xmlIO ) ) {
			ra.setIOChannels( Short.parseShort( currentElementText ) );
		} else if ( tag.startsWith( Globals.xmlCustom ) ) {
			short v =
					Short.parseShort( tag.substring( Globals.xmlCustom.length() ) );
			short c = Short.parseShort( currentElementText );
			ra.setCustomVariable( v, c );
		} else if ( tag.startsWith( Globals.xmlRelayMaskOn ) ) {
			int relay =
					Integer.parseInt( tag.substring( Globals.xmlRelayMaskOn
							.length() ) );
			if ( fUse085XRelays )
				relay += 1;
			ra.setExpRelayOnMask( relay, Short.parseShort( currentElementText ) );
		} else if ( tag.startsWith( Globals.xmlRelayMaskOff ) ) {
			int relay =
					Integer.parseInt( tag.substring( Globals.xmlRelayMaskOff
							.length() ) );
			if ( fUse085XRelays )
				relay += 1;
			ra.setExpRelayOffMask( relay, Short.parseShort( currentElementText ) );
		} else if ( tag.startsWith( Globals.xmlRelay ) ) {
			int relay =
					Integer.parseInt( tag.substring( Globals.xmlRelay.length() ) );
			if ( fUse085XRelays )
				relay += 1;
			ra.setExpRelayData( relay, Short.parseShort( currentElementText ) );
		} else if ( tag.equals( Globals.xmlMyReefAngelID ) ) {
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
			// TODO skip if null
			Log.d(TAG, tag + " is null, skipping");
			return;
		}
		
		if ( tag.startsWith( Globals.xmlLabelTempBegin ) ) {
			// handle temp sensor labels
			int sensor =
					Integer.parseInt( getTagNumber( tag,
													Globals.xmlLabelTempBegin,
													Globals.xmlLabelEnd ) );
			if ( sensor < 0 || sensor > Controller.MAX_TEMP_SENSORS )
				Log.e( TAG, "Incorrect sensor number: " + tag );
			ra.setTempLabel( sensor, currentElementText );
		} else if ( tag.startsWith( Globals.xmlRelay ) ) {
			// handle relay labels
			int relay =
					Integer.parseInt( getTagNumber( tag, Globals.xmlRelay,
													Globals.xmlLabelEnd ) );
			if ( relay < 10 ) {
				// main relay
				ra.getMainRelay().setPortLabel( relay, currentElementText );
			} else {
				// expansion relays, so split the port from the relay box
				int box = relay / 10;
				int port = relay % 10;
				// portal sends null if there's no value stored for a port
				// so don't save null
				if ( !currentElementText.equals( "null" ) )
					ra.getExpRelay( box ).setPortLabel( port,
														currentElementText );
			}
		} else if ( tag.startsWith( Globals.xmlPWMExpansion ) ) {
			// PWME
			// TODO save the label
			short channel =
					Short.parseShort( getTagNumber( tag,
													Globals.xmlPWMExpansion,
													Globals.xmlLabelEnd ) );
			Log.d( TAG, "PWM #" + channel + ": " + currentElementText );
		} else if ( tag.startsWith( Globals.xmlPHExpansion ) ) {
			// PHE, PHE before PH because PH will match both PH and PHE
			Log.d( TAG, "PHExp Label: " + currentElementText );
		} else if ( tag.startsWith( Globals.xmlPH ) ) {
			// PH
			Log.d( TAG, "PH Label: " + currentElementText );
		} else if ( tag.startsWith( Globals.xmlSalinity ) ) {
			// SAL
			Log.d( TAG, "Salinity Label: " + currentElementText );
		} else if ( tag.startsWith( Globals.xmlORP ) ) {
			// } else if ( tag.equals( Globals.xmlORP + Globals.xmlLabelEnd ) )
			// {
			// ORP
			Log.d( TAG, "ORP Label: " + currentElementText );
		} else if ( tag.startsWith( Globals.xmlCustom ) ) {
			// C
			short v =
					Short.parseShort( getTagNumber( tag, Globals.xmlCustom,
													Globals.xmlLabelEnd ) );
			Log.d( TAG, "Custom #" + v + ": " + currentElementText );
		} else if ( tag.startsWith( Globals.xmlIO ) ) {
			// IO
			short v =
					Short.parseShort( getTagNumber( tag, Globals.xmlIO,
													Globals.xmlLabelEnd ) );
			Log.d( TAG, "IO #" + v + ": " + currentElementText );
		} else {
			Log.d( TAG, "Unknown label: (" + tag + ") = " + currentElementText );
		}
	}

	private void processDateTimeXml ( String tag ) {
		// Response will be more XML data or OK
		if ( tag.equals( "HR" ) ) {
			dt.setHour( Integer.parseInt( currentElementText ) );
		} else if ( tag.equals( "MIN" ) ) {
			dt.setMinute( Integer.parseInt( currentElementText ) );
		} else if ( tag.equals( "MON" ) ) {
			// controller uses 1 based for month
			// java uses 0 based for month
			dt.setMonth( Integer.parseInt( currentElementText ) - 1 );
		} else if ( tag.equals( "DAY" ) ) {
			dt.setDay( Integer.parseInt( currentElementText ) );
		} else if ( tag.equals( "YR" ) ) {
			dt.setYear( Integer.parseInt( currentElementText ) );
		}
	}

	private void processVersionXml ( String tag ) {
		// Response will be the Version
		if ( tag.equals( Globals.xmlVersion ) ) {
			version = currentElementText;
		}
	}

	private void processMemoryXml ( String tag ) {
		// Responses will be either: OK, value, ERR
		if ( tag.startsWith( Globals.xmlMemorySingle ) ) {
			memoryResponse = currentElementText;
		}
	}

	private void processModeXml ( String tag ) {
		// Response will be either: OK or ERR
		if ( tag.startsWith( Globals.xmlMode ) ) {
			modeResponse = currentElementText;
		}
	}
}
