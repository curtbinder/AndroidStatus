/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2012 Curt Binder
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package info.curtbinder.reefangel.service;

import info.curtbinder.reefangel.controller.Controller;
import info.curtbinder.reefangel.controller.DateTime;
import info.curtbinder.reefangel.phone.RAApplication;
import info.curtbinder.reefangel.phone.Utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
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
            SimpleDateFormat dft = Utils.getDefaultDateFormat();
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
        } else if ( requestType.equals( RequestCommands.PwmOverride ) ) {
            processPWMOverrideResponseXml(tag);
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
            } else if ( tag.startsWith( XMLTags.PWMOverrideResponse ) ) {
                requestType = RequestCommands.PwmOverride;
            } else {
                Log.d(TAG, "startElement: (Unknown): " + tag );
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
            short v = Short.parseShort(currentElementText);
            ra.setPwmD(v);
        } else if ( tag.equals( XMLTags.PWMActinic2 ) ) {
            short v = Short.parseShort(currentElementText);
            ra.setPwmA2(v);
        } else if ( tag.equals( XMLTags.PWMDaylight2 ) ) {
            short v = Short.parseShort(currentElementText);
            ra.setPwmD2(v);
        } else if ( tag.startsWith( XMLTags.PWMExpansion ) &&
                !tag.endsWith( XMLTags.Override )) {
            short channel =
                    Short.parseShort( tag.substring( XMLTags.PWMExpansion
                            .length() ) );
            short v = Short.parseShort( currentElementText );
            ra.setPwmExpansion( channel, v );
        } else if ( tag.startsWith( XMLTags.PWMExpansion16) &&
                !tag.endsWith( XMLTags.Override ) ) {
            short channel = Short.parseShort( tag.substring(XMLTags.PWMExpansion16.length()) );
            short v = Short.parseShort( currentElementText );
            ra.setSCPwmExpansion( channel, v );
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
        } else if ( tag.equals( XMLTags.Humidity ) ) {
            short v = Short.parseShort( currentElementText );
            ra.setHumidity( v );
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
            ra.setIOChannels(Short.parseShort(currentElementText));
        } else if ( tag.equals( XMLTags.DCPumpMode ) ) {
            ra.setDCPumpValue( Controller.DCPUMP_MODE, Short.parseShort(currentElementText));
        } else if ( tag.equals( XMLTags.DCPumpSpeed ) ) {
            ra.setDCPumpValue( Controller.DCPUMP_SPEED, Short.parseShort(currentElementText));
        } else if ( tag.equals( XMLTags.DCPumpDuration ) ) {
            ra.setDCPumpValue( Controller.DCPUMP_DURATION, Short.parseShort(currentElementText));
        } else if ( tag.equals( XMLTags.DCPumpThreshold ) ) {
            ra.setDCPumpValue( Controller.DCPUMP_THRESHOLD, Short.parseShort(currentElementText));
        } else if ( tag.endsWith( XMLTags.Override ) ) {
            processPwmOverride(tag, currentElementText);
        } else if ( tag.startsWith( XMLTags.Custom ) ) {
            short v = Short.parseShort( tag.substring( XMLTags.Custom.length() ) );
            short c = Short.parseShort( currentElementText );
            ra.setCustomVariable( v, c );
        } else if ( tag.startsWith( XMLTags.WaterLevel ) ) {
            // 4 channel water level, tags start with WL
            short p = Short.parseShort(tag.substring(XMLTags.WaterLevel.length()));
            short v = Short.parseShort(currentElementText);
            ra.setWaterLevel(p, v);
        } else if ( tag.equals( XMLTags.Par ) ) {
            Log.d(TAG, "PAR Value: " + currentElementText);
        } else if ( tag.equals( XMLTags.StatusFlags ) ) {
            ra.setStatusFlags( Short.parseShort(currentElementText) );
        } else if ( tag.equals( XMLTags.AlertFlags ) ) {
            ra.setAlertFlags( Short.parseShort(currentElementText) );
        } else if ( tag.startsWith( XMLTags.RelayMaskOn ) ) {
            int relay = Integer.parseInt( tag.substring( XMLTags.RelayMaskOn.length() ) );
            if ( fUse085XRelays )
                relay += 1;
            ra.setExpRelayOnMask( relay, Short.parseShort( currentElementText ) );
        } else if ( tag.startsWith( XMLTags.RelayMaskOff ) ) {
            int relay = Integer.parseInt( tag.substring( XMLTags.RelayMaskOff.length() ) );
            if ( fUse085XRelays )
                relay += 1;
            ra.setExpRelayOffMask( relay, Short.parseShort( currentElementText ) );
        } else if ( tag.startsWith( XMLTags.Relay ) ) {
            try {
                int relay = Integer.parseInt(tag.substring(XMLTags.Relay.length()));
                if (fUse085XRelays)
                    relay += 1;
                ra.setExpRelayData(relay, Short.parseShort(currentElementText));
            } catch (NumberFormatException e) {
                Log.e(TAG, "Invalid XML tag: " + tag);
            }
        } else if ( tag.equals( XMLTags.BoardID ) ) {
            //Log.d( TAG, "Board ID: " + currentElementText );
            ra.setBoard(Byte.parseByte(currentElementText));
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

    private void processPwmOverride ( String tag, String element ) {
//		Log.d( TAG, "Override tag (" + tag + ") with data: " + element );
        Short value = Short.parseShort( element );
        if ( tag.startsWith( XMLTags.PWMActinic2) ) {
            ra.setPwmA2Override( value );
        } else if ( tag.startsWith( XMLTags.PWMDaylight2) ) {
            ra.setPwmD2Override( value );
        } else if ( tag.startsWith( XMLTags.PWMActinic ) ) {
            ra.setPwmAOverride( value );
        } else if ( tag.startsWith( XMLTags.PWMDaylight ) ) {
            ra.setPwmDOverride( value );
        } else if ( tag.startsWith( XMLTags.PWMExpansion ) ) {
            // Get the channel from the tag. The last char is an O.
            Short channel = Short.parseShort( tag.substring(
                    XMLTags.PWMExpansion.length(), tag.length()-1) );
            ra.setPwmExpansionOverride( channel, value );
        } else if ( tag.startsWith( XMLTags.PWMExpansion16 ) ) {
            // Get the channel from the tag. The last char is an O.
            Short channel = Short.parseShort( tag.substring(
                    XMLTags.PWMExpansion16.length(), tag.length()-1) );
            ra.setSCPwmExpansionOverride( channel, value );
        } else if ( tag.startsWith( XMLTags.AIWhite ) ) {
            ra.setAIChannelOverride( Controller.AI_WHITE, value );
        } else if ( tag.startsWith( XMLTags.AIBlue ) ) {
            ra.setAIChannelOverride( Controller.AI_BLUE, value );
        } else if ( tag.startsWith( XMLTags.AIRoyalBlue ) ) {
            ra.setAIChannelOverride( Controller.AI_ROYALBLUE, value );
        } else if ( tag.startsWith( XMLTags.RFWhite ) ) {
            ra.setRadionChannelOverride( Controller.RADION_WHITE, value );
        } else if ( tag.startsWith( XMLTags.RFRoyalBlue ) ) {
            ra.setRadionChannelOverride( Controller.RADION_ROYALBLUE, value );
        } else if ( tag.startsWith( XMLTags.RFRed ) ) {
            ra.setRadionChannelOverride( Controller.RADION_RED, value );
        } else if ( tag.startsWith( XMLTags.RFGreen ) ) {
            ra.setRadionChannelOverride( Controller.RADION_GREEN, value );
        } else if ( tag.startsWith( XMLTags.RFBlue ) ) {
            ra.setRadionChannelOverride( Controller.RADION_BLUE, value );
        } else if ( tag.startsWith( XMLTags.RFIntensity ) ) {
            ra.setRadionChannelOverride( Controller.RADION_INTENSITY, value );
        }
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
            short channel = Short.parseShort( getTagNumber( tag, XMLTags.PWMExpansion,
                            XMLTags.LabelEnd ) );
            if ( channel < 0 || channel > Controller.MAX_PWM_EXPANSION_PORTS )
                Log.e(TAG, "Incorrect PWM Expansion port: " + tag);
            ra.setPwmExpansionLabel( channel, currentElementText );
        } else if ( tag.startsWith( XMLTags.PWMExpansion16 ) ) {
            // SCPWME
            short channel = Short.parseShort( getTagNumber( tag, XMLTags.PWMExpansion16,
                            XMLTags.LabelEnd ) );
            if ( channel < 0 || channel > Controller.MAX_SCPWM_EXPANSION_PORTS )
                Log.e(TAG, "Incorrect SCPWM Expansion port: " + tag);
            ra.setSCPwmExpansionLabel( channel, currentElementText );
        } else if ( tag.startsWith( XMLTags.PWMActinic2 + "1" ) ) {
            // PWMA2
            ra.setPwmA2Label( currentElementText );
        } else if ( tag.startsWith( XMLTags.PWMDaylight2 + "1" ) ) {
            // PWMD2
            ra.setPwmD2Label( currentElementText );
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
        } else if ( tag.startsWith( XMLTags.Humidity ) ) {
            // HUM
            ra.setHumidityLabel( currentElementText );
        } else if ( tag.equals( XMLTags.WaterLevel + XMLTags.LabelEnd ) ) {
            // 1 channel Water Level
            ra.setWaterLevelLabel( (short) 0, currentElementText );
        } else if ( tag.startsWith( XMLTags.WaterLevel ) ) {
            // 4 channel Water Level
            short p = Short.parseShort( getTagNumber(tag, XMLTags.WaterLevel, XMLTags.LabelEnd) );
            if ( p < 0 || p > Controller.MAX_WATERLEVEL_PORTS )
                Log.e(TAG, "Incorrect Water Level Port: " + tag);
            ra.setWaterLevelLabel( p, currentElementText );;
        } else if ( tag.startsWith( XMLTags.Custom ) ) {
            // C
            short v =
                    Short.parseShort( getTagNumber( tag, XMLTags.Custom,
                            XMLTags.LabelEnd ) );
            if ( v < 0 || v > Controller.MAX_CUSTOM_VARIABLES )
                Log.e(TAG, "Incorrect Custom Variable: " + tag);
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

    private void processPWMOverrideResponseXml ( String tag ) {
        // Responses will be either: OK or ERR
        if ( tag.startsWith( XMLTags.PWMOverrideResponse ) ) {
            modeResponse = currentElementText;
        }
    }

	private void processModeXml ( String tag ) {
		// Response will be either: OK or ERR
		if ( tag.startsWith( XMLTags.Mode ) ) {
			modeResponse = currentElementText;
		}
	}
}
