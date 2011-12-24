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
import java.net.SocketTimeoutException;
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
	private final StatusActivity ra;
	private final Host host;
	private boolean status;
	
	private int errorCode;
	
	ControllerTask(StatusActivity ra, Host host, boolean statusScreen) {
		this.ra = ra;
		this.host = host;
		this.status = statusScreen;
		this.errorCode = 0;
	}
	
	private void Error(int errorCodeIndex, Throwable t, String msg) {
		String[] errorCodes = ra.getResources().getStringArray(R.array.errorCodes);
		this.errorCode = Integer.parseInt(errorCodes[errorCodeIndex]);
		Log.e(TAG, msg, t);
	}
	
	private String getErrorMessage() {
		String[] errorCodesStrings = ra.getResources().getStringArray(R.array.errorCodesStrings);
		String[] errorCodes = ra.getResources().getStringArray(R.array.errorCodes);
		String s = "Unknown error";
		
		// loop through array of errorcodes and match with the current code
		for ( int i = 0; i < errorCodes.length; i++ ){
			if ( Integer.parseInt(errorCodes[i]) == errorCode ) {
				// found code
				s = String.format("%s %d: %s", 
						(String) ra.getResources().getText(R.string.messageError), 
						errorCode, 
						errorCodesStrings[i]);
				break;
			}
		}
		return s;
	}
	
	@Override
	public void run() {
		// Communicate with controller
		
		// clear out the error code on run
		errorCode = 0;
		HttpURLConnection con = null;
		String res = "";
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
			Error(1, e, "MalformedURLException");
		} catch ( ProtocolException e ) {
			Error(1, e, "ProtocolException");
		} catch ( SocketTimeoutException e ) {
			Error(5, e, "SocketTimeoutException");
		} catch ( ConnectException e ) {
			Error(3, e, "ConnectException");
		} catch ( IOException e ) {
			Error(1, e, "IOException");
		} catch ( InterruptedException e ) {
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
		if ( errorCode > 0 ) {
			// encountered an error, display an error on screen
			String er = getErrorMessage();
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
				String er = getErrorMessage();
				Log.d(TAG, er);
				ra.guiUpdateTimeText(er);
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
			Error(3, e, "sendCommand: ConnectException");
		} catch ( UnknownHostException e ) {
			Error(4, e, "sendCommand: UnknownHostException");
		} catch ( Exception e ) {
			Error(2, e, "sendCommand: Exception");
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
			Error(7, e, "parseXML: ParserConfigurationException");
		} catch ( IOException e ) {
			Error(8, e, "parseXML: IOException");
		} catch (SAXException e) {
			Error(9, e, "parseXML: SAXException");
		} catch (InterruptedException e) {
			// Not a true error, so only for debugging
			Log.d(TAG, "parseXML: InterruptedException", e);
		}
		return result;
	}
}
