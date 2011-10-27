package info.curtbinder.reefangel.phone;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.ConnectException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
//import org.xml.sax.helpers.XMLReaderFactory;

import android.util.Log;

public class ControllerTask implements Runnable {
	private static final String TAG = "ControllerTask";
	private final ReefAngelStatusActivity ra;
	private final Host h;
	private boolean status;
	
	// TODO add in error codes to be associated with error messages
	// and locations in the code
	private int errorCode;
	
	ControllerTask(ReefAngelStatusActivity ra, Host h, boolean statusScreen) {
		this.ra = ra;
		this.h = h;
		this.status = statusScreen;
		this.errorCode = 0;
	}
	
	@Override
	public void run() {
		// Communicate with controller
		
		// clear out the error code on run
		errorCode = 0;
		String res = "";
		String sendCmdErrorMessage = "";
		long start = System.currentTimeMillis();
		try {
			res = sendCommand( new URL( h.toString() ) );
		} catch ( MalformedURLException e ) {
			sendCmdErrorMessage = "Error sending command";
			errorCode = Globals.errorSendCmdBadUrl;
			Log.e(TAG, "MalformedURLException", e);
		}
		long end = System.currentTimeMillis();
		Log.d(TAG, new String(String.format("sendCommand (%d ms)", end - start )));

		// check if there was an error
		if ( res.equals( Globals.messageError ) ) {
			// TODO log the actual error message
			// encountered an error, display an error on screen
			Log.d(TAG, sendCmdErrorMessage);
			String er = new String(String.format("%s: %d", Globals.messageError, errorCode));
			Log.d(TAG, er);
			ra.guiUpdateTimeText(er);
		} else if ( res.equals( Globals.messageInterrupted ) ) {
			// Interrupted 
			Log.d(TAG, "sendCommand Interrupted");
			ra.guiUpdateTimeText(Globals.messageInterrupted);
		} else {
			XMLHandler h = new XMLHandler();
			if ( !parseXML( h, res ) ) {
				// error parsing
				return;
			}
			
			if ( status )
				ra.guiUpdateDisplay(h.getRa());
			// else handle updating memory display 
		}
	}

	private String sendCommand ( URL u ) {
		String s = "";
		try {
			// Check for an interruption
			if ( Thread.interrupted() )
				throw new InterruptedException();
			
			BufferedReader bin =
					new BufferedReader( new InputStreamReader( u.openStream() ) );
			String line;
			while ( (line = bin.readLine()) != null ) {
				// Check for an interruption
				if ( Thread.interrupted() )
					throw new InterruptedException();
				
				s += line;
			}
		} catch ( InterruptedException e ) {
			Log.d(TAG, "sendCommand: InterruptedException", e);
			s = Globals.messageInterrupted;
		} catch ( ConnectException e ) {
			Log.e(TAG, "sendCommand: ConnectException", e);
			s = Globals.messageError;
			errorCode = Globals.errorSendCmdConnect;
		} catch ( Exception e ) {
			Log.e(TAG, "sendCommand: Exception", e);
			s = Globals.messageError;
			errorCode = Globals.errorSendCmdException;
		}
		return s;
	}
	
	private boolean parseXML ( XMLHandler h, String res ) {
		SAXParserFactory spf = SAXParserFactory.newInstance();
		SAXParser sp = null;
		XMLReader xr = null;
		long start = 0, end = 0;
		boolean result = false;
		try {
			// Check for an interruption
			if ( Thread.interrupted() )
				throw new InterruptedException();
			
			Log.d(TAG, "Parsing" );
			sp = spf.newSAXParser();
			xr = sp.getXMLReader();
			xr.setContentHandler( h );
			xr.setErrorHandler( h );
			
			// Check for an interruption
			if ( Thread.interrupted() )
				throw new InterruptedException();
			
			start = System.currentTimeMillis();
			xr.parse( new InputSource( new StringReader( res ) ) );
			end = System.currentTimeMillis();
			Log.d(TAG, new String(String.format("Parsed (%d ms)", end - start )));
			result = true;
		} catch (ParserConfigurationException e) {
			Log.e(TAG, "parseXML: ParserConfigurationException", e);
			errorCode = Globals.errorParseXmlParseConfig;
		} catch ( IOException e ) {
			Log.e(TAG, "parseXML: IOException", e);
			errorCode = Globals.errorParseXmlIO;
		} catch (SAXException e) {
			Log.e(TAG, "parseXML: SAXException", e);
			errorCode = Globals.errorParseXmlSAX;
		} catch (InterruptedException e) {
			// Not a true error, so only for debugging
			Log.d(TAG, "parseXML: InterruptedException", e);
		}
		return result;
	}
}
