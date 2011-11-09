package info.curtbinder.reefangel.phone;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
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
		HttpURLConnection con = null;
		String res = "";
		String sendCmdErrorMessage = "";
		ra.guiUpdateTimeText((String) ra.getResources().getText(R.string.statusStart));
		long start = System.currentTimeMillis();
		try {
			URL url = new URL( host.toString() );
			con = (HttpURLConnection) url.openConnection();
			con.setReadTimeout(host.getReadTimeout());
			con.setConnectTimeout(host.getConnectTimeout());
			con.setRequestMethod("GET");
			con.setDoInput(true);
			
			ra.guiUpdateTimeText((String) ra.getResources().getText(R.string.statusConnect));
			con.connect();
			
			if ( Thread.interrupted() )
				throw new InterruptedException();
			
			res = sendCommand( con.getInputStream() );
		} catch ( MalformedURLException e ) {
			sendCmdErrorMessage = "Error sending command";
			errorCode = Globals.errorSendCmdBadUrl;
			Log.e(TAG, "MalformedURLException", e);
		} catch (ProtocolException e) {
			sendCmdErrorMessage = "Error sending command";
			errorCode = Globals.errorSendCmdBadUrl;
			Log.e(TAG, "ProtocolException", e);
		} catch (IOException e) {
			sendCmdErrorMessage = "Error sending command";
			errorCode = Globals.errorSendCmdBadUrl;
			Log.e(TAG, "IOException", e);
		} catch (InterruptedException e) {
			Log.d(TAG, "InterruptedException", e);
			res = (String) ra.getResources().getText(R.string.messageCancelled);
		} finally {
			if ( con != null ) {
				con.disconnect();
				ra.guiUpdateTimeText((String) ra.getResources().getText(R.string.statusDisconnected));
			}
		}
		long end = System.currentTimeMillis();
		Log.d(TAG, new String(String.format("sendCommand (%d ms)", end - start )));
		ra.guiUpdateTimeText((String) ra.getResources().getText(R.string.statusReadResponse));

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
			ra.guiUpdateTimeText((String) ra.getResources().getText(R.string.statusUpdatingDisplay));
			if ( status ) {
				ra.guiAddParamsEntry(xml.getRa());
				ra.guiUpdateDisplay();
			}
			// else handle updating memory display 
			// else handle updating the labels from reefangel.com
		}
	}

	private String sendCommand ( InputStream i ) {
		StringBuilder s = new StringBuilder(8192);
		try {
			// Check for an interruption
			if ( Thread.interrupted() )
				throw new InterruptedException();
			
			ra.guiUpdateTimeText((String) ra.getResources().getText(R.string.statusSendingCommand));
			BufferedReader bin =
					new BufferedReader( new InputStreamReader( i ) , 8192 );
			String line;
			ra.guiUpdateTimeText((String) ra.getResources().getText(R.string.statusReadResponse));
			while ( (line = bin.readLine()) != null ) {
				// Check for an interruption
				if ( Thread.interrupted() )
					throw new InterruptedException();
				
				s.append(line);
			}
		} catch ( InterruptedException e ) {
			Log.d(TAG, "sendCommand: InterruptedException", e);
			s = new StringBuilder((String) ra.getResources().getText(R.string.messageCancelled));
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
			s = new StringBuilder((String) ra.getResources().getText(R.string.messageError));
		
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
			
			ra.guiUpdateTimeText((String) ra.getResources().getText(R.string.statusInitParser));
			Log.d(TAG, "Parsing" );
			sp = spf.newSAXParser();
			xr = sp.getXMLReader();
			xr.setContentHandler( xml );
			xr.setErrorHandler( xml );
			
			// Check for an interruption
			if ( Thread.interrupted() )
				throw new InterruptedException();
			
			start = System.currentTimeMillis();
			ra.guiUpdateTimeText((String) ra.getResources().getText(R.string.statusParsing));
			xr.parse( new InputSource( new StringReader( res ) ) );
			end = System.currentTimeMillis();
			Log.d(TAG, new String(String.format("Parsed (%d ms)", end - start )));
			ra.guiUpdateTimeText((String) ra.getResources().getText(R.string.statusFinished));
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
