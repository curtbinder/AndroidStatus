package info.curtbinder.reefangel.phone;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
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
	
	ControllerTask(ReefAngelStatusActivity ra, Host h, boolean statusScreen) {
		this.ra = ra;
		this.h = h;
		this.status = statusScreen;
	}
	
	@Override
	public void run() {
		// Communicate with controller
		
		// Parse XML response
		String res = "";
		String sendCmdErrorMessage = "";
		long start = System.currentTimeMillis();
		try {
			res = sendCommand( new URL( h.toString() ) );
		} catch ( MalformedURLException e ) {
			sendCmdErrorMessage = "Error sending command";
			Log.d(TAG, "MalformedURLException", e);
		}
		long end = System.currentTimeMillis();
		String out = new String(String.format("Took %d ms to send command\n", end - start ));
		Log.d(TAG, out);

		// check if there was an error
		if ( res.equals( Globals.messageError ) ) {
			// encountered an error, display an error on screen
			Log.d(TAG, sendCmdErrorMessage);
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
			BufferedReader bin =
					new BufferedReader( new InputStreamReader( u.openStream() ) );
			String line;
			while ( (line = bin.readLine()) != null ) {
				s += line;
			}
		} catch ( Exception e ) {
			Log.d(TAG, "Error with sendCommand", e);
			s = Globals.messageError;
		}
		return s;
	}
	
	private boolean parseXML ( XMLHandler h, String res ) {
		SAXParserFactory spf = SAXParserFactory.newInstance();
		SAXParser sp = null;
		XMLReader xr = null;
		long start = 0, end = 0;
		try {
			sp = spf.newSAXParser();
		} catch (ParserConfigurationException e) {
			Log.d(TAG, "ParserConfigurationException", e);
			e.printStackTrace();
			return false;
		} catch (SAXException e) {
			Log.d(TAG, "SAXException", e);
			e.printStackTrace();
			return false;
		}
		
		Log.d(TAG, "Parsing" );
		try {
			//xr = XMLReaderFactory.createXMLReader();
			xr = sp.getXMLReader();
		} catch ( SAXException e ) {
			Log.d(TAG, "SAXException", e);
			e.printStackTrace();
			return false;
		}
		xr.setContentHandler( h );
		xr.setErrorHandler( h );
		start = System.currentTimeMillis();
		try {
			xr.parse( new InputSource( new StringReader( res ) ) );
		} catch ( IOException e ) {
			Log.d(TAG, "parseXML: IOException", e);
			e.printStackTrace();
			return false;
		} catch ( SAXException e ) {
			Log.d(TAG, "parseXML: SAXException", e);
			e.printStackTrace();
			return false;
		}
		end = System.currentTimeMillis();
		String out;
		out = new String(String.format("Took %d ms to parse\n", end - start ));
		Log.d(TAG, out);
		Log.d(TAG, "Parsed" );
		return true;
	}
}
