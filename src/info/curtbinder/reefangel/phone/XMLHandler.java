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

	private DateTime dt;

	public Controller getRa ( ) {
		return ra;
	}

	public String getVersion ( ) {
		return version;
	}

	public String getDateTime ( ) {
		return dt.getDateTimeString();
		// return "";
	}

	public String getDateTimeUpdateStatus ( ) {
		return dt.getUpdateStatus();
		// return "";
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
				processStatusXml( tag );
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
			if ( Integer.parseInt( currentElementText ) == 1 ) {
				f = true;
			}
			ra.setAtoLow( f );
		} else if ( tag.equals( Globals.xmlATOHigh ) ) {
			boolean f = false;
			if ( Integer.parseInt( currentElementText ) == 1 ) {
				f = true;
			}
			ra.setAtoHigh( f );
		} else if ( tag.equals( Globals.xmlPWMActinic ) ) {
			ra.setPwmA( Byte.parseByte( currentElementText ) );
		} else if ( tag.equals( Globals.xmlPWMDaylight ) ) {
			ra.setPwmD( Byte.parseByte( currentElementText ) );
		} else if ( tag.startsWith( Globals.xmlPWMExpansion ) ) {
			Log.d( TAG, "PWME (" + tag + ") " + currentElementText );
		} else if ( tag.equals( Globals.xmlSalinity ) ) {
			ra.setSalinity( Integer.parseInt( currentElementText ) );
		} else if ( tag.equals( Globals.xmlRelay ) ) {
			ra.setMainRelayData( Short.parseShort( currentElementText ) );
		} else if ( tag.equals( Globals.xmlRelayMaskOn ) ) {
			ra.setMainRelayOnMask( Short.parseShort( currentElementText ) );
		} else if ( tag.equals( Globals.xmlRelayMaskOff ) ) {
			ra.setMainRelayOffMask( Short.parseShort( currentElementText ) );
		} else if ( tag.equals( Globals.xmlLogDate ) ) {
			ra.setLogDate( currentElementText );
		} else if ( tag.equals( Globals.xmlRelayExpansionModules ) ) {
			Log.d( TAG, "REM " + currentElementText );
		} else if ( tag.equals( Globals.xmlExpansionModules ) ) {
			Log.d( TAG, "EM " + currentElementText );
		} else if ( tag.equals( Globals.xmlPWMExpansion ) ) {
			Log.d( TAG, "PWME " + currentElementText );
		} else if ( tag.equals( Globals.xmlAIBlue ) ) {
			Log.d( TAG, "AIB " + currentElementText );
		} else if ( tag.equals( Globals.xmlAIRoyalBlue ) ) {
			Log.d( TAG, "AIRB " + currentElementText );
		} else if ( tag.equals( Globals.xmlAIWhite ) ) {
			Log.d( TAG, "AIW " + currentElementText );
		} else if ( tag.equals( Globals.xmlRFMode ) ) {
			Log.d( TAG, "RFM " + currentElementText );
		} else if ( tag.equals( Globals.xmlRFSpeed ) ) {
			Log.d( TAG, "RFS " + currentElementText );
		} else if ( tag.equals( Globals.xmlRFDuration ) ) {
			Log.d( TAG, "RFD " + currentElementText );
		} else if ( tag.equals( Globals.xmlRFWhite ) ) {
			Log.d( TAG, "RFW " + currentElementText );
		} else if ( tag.equals( Globals.xmlRFBlue ) ) {
			Log.d( TAG, "RFB " + currentElementText );
		} else if ( tag.equals( Globals.xmlRFGreen ) ) {
			Log.d( TAG, "RFG " + currentElementText );
		} else if ( tag.equals( Globals.xmlRFRed ) ) {
			Log.d( TAG, "RFR " + currentElementText );
		} else if ( tag.equals( Globals.xmlRFRoyalBlue ) ) {
			Log.d( TAG, "RFRB " + currentElementText );
		} else if ( tag.equals( Globals.xmlRFIntensity ) ) {
			Log.d( TAG, "RFI " + currentElementText );
		} else if ( tag.startsWith( Globals.xmlLabelTempBegin )
					&& tag.endsWith( Globals.xmlLabelEnd ) ) {
			// handle temp sensor labels
			int sensor =
					Integer.parseInt( getTagNumber( tag,
													Globals.xmlLabelTempBegin,
													Globals.xmlLabelEnd ) );
			if ( sensor < 0 || sensor > Controller.MAX_TEMP_SENSORS )
				Log.e( TAG, "Incorrect sensor number: " + tag );
			ra.setTempLabel( sensor, currentElementText );
		} else if ( tag.startsWith( Globals.xmlLabelRelayBegin )
					&& tag.endsWith( Globals.xmlLabelEnd ) ) {
			// handle relay labels
			int relay =
					Integer.parseInt( getTagNumber( tag,
													Globals.xmlLabelRelayBegin,
													Globals.xmlLabelEnd ) );
			if ( relay < 10 ) {
				// main relay
				ra.getMainRelay().setPortLabel( relay, currentElementText );
			} else {
				// expansion relays, so split the port from the relay box
				int box = relay / 10;
				int port = relay % 10;
				ra.getExpRelay( box ).setPortLabel( port, currentElementText );
			}
		} else if ( tag.startsWith( Globals.xmlRelayMaskOn ) ) {
			/*
			 * int relay = Integer.parseInt(tag.substring(Globals.xmlRelayMaskOn
			 * .length())); ra.setExpRelayOnMask(relay,
			 * Short.parseShort(currentElementText));
			 */
		} else if ( tag.startsWith( Globals.xmlRelayMaskOff ) ) {
			/*
			 * int relay =
			 * Integer.parseInt(tag.substring(Globals.xmlRelayMaskOff
			 * .length())); ra.setExpRelayOffMask(relay,
			 * Short.parseShort(currentElementText));
			 */
		} else if ( tag.startsWith( Globals.xmlRelay ) ) {
			/*
			 * int relay = Integer.parseInt(tag.substring(Globals.xmlRelay
			 * .length())); ra.setExpRelayData(relay,
			 * Short.parseShort(currentElementText));
			 */
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
