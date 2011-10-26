package info.curtbinder.reefangel.phone;


import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class XMLHandler extends DefaultHandler {

	private String currentElementText = "";
	private String requestType = "";
	private Controller ra;
	private String version = "";
	private String memoryResponse = "";
	//private DateTime dt;

	public Controller getRa ( ) {
		return ra;
	}

	public String getVersion ( ) {
		return version;
	}

	/*
	public String getDateTime ( ) {
		return dt.getDateTimeString();
	}

	public String getDateTimeUpdateStatus ( ) {
		return dt.getUpdateStatus();
	}
	*/

	public String getMemoryResponse ( ) {
		return memoryResponse;
	}

	public String getRequestType ( ) {
		return requestType;
	}

	public XMLHandler () {
		super();
		this.ra = new Controller();
		//this.dt = new DateTime();
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
		if ( requestType.equals( Globals.requestStatus ) ) {
			if ( qName.equals( Globals.xmlStatus ) ) {
				return;
			} else {
				processStatusXml( qName );
			}
		} else if ( requestType.equals( Globals.requestMemoryByte ) ) {
			if ( qName.equals( Globals.xmlMemory ) ) {
				return;
			} else {
				processMemoryXml( qName );
			}
/*
		} else if ( requestType.equals( Globals.requestDateTime ) ) {
			if ( qName.equals( Globals.xmlDateTime ) ) {
				if ( !currentElementText.isEmpty() ) {
					// not empty meaning we have a status to report
					// either OK or ERR
					dt.setStatus( currentElementText );
				}
				return;
			} else {
				processDateTimeXml( qName );
			}
*/
		} else if ( requestType.equals( Globals.requestVersion ) ) {
			processVersionXml( qName );
		}
		currentElementText = "";
	}

	// @Override
	public void startElement (
			String uri,
			String localName,
			String qName,
			Attributes attributes ) throws SAXException {
		if ( requestType.equals("") ) {
			// no request type, so set it based on the first element we process
			if ( qName.equals( Globals.xmlStatus ) ) {
				requestType = Globals.requestStatus;
//			} else if ( qName.equals( Globals.xmlDateTime ) ) {
//				requestType = Globals.requestDateTime;
			} else if ( qName.equals( Globals.xmlVersion ) ) {
				requestType = Globals.requestVersion;
			} else if ( qName.startsWith( Globals.xmlMemorySingle ) ) {
				// can be either type, just chose to use Bytes
				requestType = Globals.requestMemoryByte;
			}
		}
	}

	private void processStatusXml ( String tag ) {
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
		} else if ( tag.equals( Globals.xmlSalinity ) ) {
			ra.setSalinity( Byte.parseByte( currentElementText ) );
		} else if ( tag.equals( Globals.xmlRelay ) ) {
			ra.setMainRelayData( Short.parseShort( currentElementText ) );
		} else if ( tag.equals( Globals.xmlRelayMaskOn ) ) {
			ra.setMainRelayDataMaskOn( Short.parseShort( currentElementText ) );
		} else if ( tag.equals( Globals.xmlRelayMaskOff ) ) {
			ra.setMainRelayDataMaskOff( Short.parseShort( currentElementText ) );
		}
		// TODO process expansion relays
	}

/*
	private void processDateTimeXml ( String tag ) {
		//
		//  Response will be more XML data or OK
		// 
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
*/
	private void processVersionXml ( String tag ) {
		/*
		 * Response will be the Version
		 */
		if ( tag.equals( Globals.xmlVersion ) ) {
			version = currentElementText;
		}
	}

	private void processMemoryXml ( String tag ) {
		/*
		 * Responses will be either: OK, value, ERR
		 */
		if ( tag.startsWith( Globals.xmlMemorySingle ) ) {
			memoryResponse = currentElementText;
		}
	}
}
