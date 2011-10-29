package info.curtbinder.reefangel.phone;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.ConnectException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import android.util.Log;

public class ControllerTask implements Runnable {
	private static final String TAG = "RAControllerTask";
	private final ReefAngelStatusActivity ra;
	private final Host host;
	private boolean status;
	
	// TODO add in error codes to be associated with error messages
	// and locations in the code
	private int errorCode;
	
	ControllerTask(ReefAngelStatusActivity ra, Host host, boolean statusScreen) {
		this.ra = ra;
		this.host = host;
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
			res = sendCommand( new URL( host.toString() ) );
		} catch ( MalformedURLException e ) {
			sendCmdErrorMessage = "Error sending command";
			errorCode = Globals.errorSendCmdBadUrl;
			Log.e(TAG, "MalformedURLException", e);
		}
		long end = System.currentTimeMillis();
		Log.d(TAG, new String(String.format("sendCommand (%d ms)", end - start )));

		// check if there was an error
		if ( res.equals( (String) ra.getResources().getText(R.string.messageError) ) ) {
			// TODO log the actual error message
			// encountered an error, display an error on screen
			Log.d(TAG, sendCmdErrorMessage);
			String er = new String(String.format("%s: %d", (String) ra.getResources().getText(R.string.messageError), errorCode));
			Log.d(TAG, er);
			ra.guiUpdateTimeText(er);
		} else if ( res.equals( (String) ra.getResources().getText(R.string.messageCancelled) ) ) {
			// Interrupted 
			Log.d(TAG, "sendCommand Interrupted");
			ra.guiUpdateTimeText((String) ra.getResources().getText(R.string.messageCancelled));
		} else {
			XMLHandler xml = new XMLHandler();
			if ( !parseXML( xml, res ) ) {
				// error parsing
				return;
			}
			
			if ( status )
				ra.guiUpdateDisplay(xml.getRa());
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
			s = (String) ra.getResources().getText(R.string.messageCancelled);
		} catch ( ConnectException e ) {
			Log.e(TAG, "sendCommand: ConnectException", e);
			errorCode = Globals.errorSendCmdConnect;
		} catch ( UnknownHostException e ) {
			Log.e(TAG, "sendCommand: UnknownHostException", e);
			errorCode = Globals.errorSendCmdUnknownHost;			
		} catch ( Exception e ) {
			Log.e(TAG, "sendCommand: Exception", e);
			errorCode = Globals.errorSendCmdException;
		}
		
		// if we encountered an error, set the error text
		if ( errorCode > 0 )
			s = (String) ra.getResources().getText(R.string.messageError);
		
		return s;
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
			
			Log.d(TAG, "Parsing" );
			sp = spf.newSAXParser();
			xr = sp.getXMLReader();
			xr.setContentHandler( xml );
			xr.setErrorHandler( xml );
			
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
