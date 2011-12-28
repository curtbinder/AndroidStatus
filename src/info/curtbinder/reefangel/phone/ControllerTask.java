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

import android.content.Intent;
import android.util.Log;

public class ControllerTask implements Runnable {
	public static final String UPDATE_DISPLAY_DATA_INTENT = Globals.PACKAGE_BASE + ".UPDATE_DISPLAY_DATA";
	public static final String UPDATE_STATUS_INTENT = Globals.PACKAGE_BASE + ".UPDATE_STATUS";
	public static final String UPDATE_STATUS_ID = "STATUS_MSG_ID";
	public static final String ERROR_MESSAGE_INTENT = Globals.PACKAGE_BASE + ".ERROR_MESSAGE";
	public static final String ERROR_MESSAGE_STRING = "ERROR_MESSAGE_STRING";
	
	private static final String TAG = "RAControllerTask";
	private final Host host;
	private boolean status;
	RAApplication rapp;

	private int errorCode;

	ControllerTask(RAApplication rapp, Host host, boolean statusScreen) {
		this.rapp = rapp;
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
		broadcastUpdateStatus(R.string.statusStart);
		long start = System.currentTimeMillis();
		try {
			URL url = new URL(host.toString());
			con = (HttpURLConnection) url.openConnection();
			con.setReadTimeout(host.getReadTimeout());
			con.setConnectTimeout(host.getConnectTimeout());
			con.setRequestMethod("GET");
			con.setDoInput(true);

			broadcastUpdateStatus(R.string.statusConnect);
			con.connect();

			if (Thread.interrupted())
				throw new InterruptedException();

			res = sendCommand(con.getInputStream());
		} catch (MalformedURLException e) {
			rapp.error(1, e, "MalformedURLException");
		} catch (ProtocolException e) {
			rapp.error(1, e, "ProtocolException");
		} catch (SocketTimeoutException e) {
			rapp.error(5, e, "SocketTimeoutException");
		} catch (ConnectException e) {
			rapp.error(3, e, "ConnectException");
		} catch (IOException e) {
			rapp.error(1, e, "IOException");
		} catch (InterruptedException e) {
			Log.d(TAG, "InterruptedException", e);
			res = (String) rapp.getResources().getText(
					R.string.messageCancelled);
		} finally {
			if (con != null) {
				con.disconnect();
				broadcastUpdateStatus(R.string.statusDisconnected);
			}
		}
		long end = System.currentTimeMillis();
		Log.d(TAG,
				new String(String.format("sendCommand (%d ms)", end - start)));
		broadcastUpdateStatus(R.string.statusReadResponse);

		// check if there was an error
		if (errorCode > 0) {
			// encountered an error, display an error on screen
			broadcastErrorMessage();
		} else if (res.equals((String) rapp.getResources().getText(
				R.string.messageCancelled))) {
			// Interrupted
			Log.d(TAG, "sendCommand Interrupted");
			broadcastUpdateStatus(R.string.messageCancelled);
		} else {
			XMLHandler xml = new XMLHandler();
			if (!parseXML(xml, res)) {
				// error parsing
				broadcastErrorMessage();
				return;
			}
			broadcastUpdateStatus(R.string.statusUpdatingDisplay);
			if (status) {
				broadcastUpdateDisplayData(xml.getRa());
			}
			// else handle updating memory display
			// else handle updating the labels from reefangel.com
		}
	}

	private String sendCommand(InputStream i) {
		StringBuilder s = new StringBuilder(8192);
		try {
			// Check for an interruption
			if (Thread.interrupted())
				throw new InterruptedException();

			broadcastUpdateStatus(R.string.statusSendingCommand);
			BufferedReader bin = new BufferedReader(new InputStreamReader(i),
					8192);
			String line;
			broadcastUpdateStatus(R.string.statusReadResponse);
			while ((line = bin.readLine()) != null) {
				// Check for an interruption
				if (Thread.interrupted())
					throw new InterruptedException();

				s.append(line);
			}
		} catch (InterruptedException e) {
			Log.d(TAG, "sendCommand: InterruptedException", e);
			s = new StringBuilder((String) rapp.getResources().getText(
					R.string.messageCancelled));
		} catch (ConnectException e) {
			rapp.error(3, e, "sendCommand: ConnectException");
		} catch (UnknownHostException e) {
			rapp.error(4, e, "sendCommand: UnknownHostException");
		} catch (Exception e) {
			rapp.error(2, e, "sendCommand: Exception");
		}

		// if we encountered an error, set the error text
		if (errorCode > 0)
			s = new StringBuilder((String) rapp.getResources().getText(
					R.string.messageError));

		return s.toString();
	}

	private boolean parseXML(XMLHandler xml, String res) {
		SAXParserFactory spf = SAXParserFactory.newInstance();
		SAXParser sp = null;
		XMLReader xr = null;
		long start = 0, end = 0;
		boolean result = false;
		try {
			// Check for an interruption
			if (Thread.interrupted())
				throw new InterruptedException();

			broadcastUpdateStatus(R.string.statusInitParser);
			Log.d(TAG, "Parsing");
			sp = spf.newSAXParser();
			xr = sp.getXMLReader();
			xr.setContentHandler(xml);
			xr.setErrorHandler(xml);

			// Check for an interruption
			if (Thread.interrupted())
				throw new InterruptedException();

			start = System.currentTimeMillis();
			broadcastUpdateStatus(R.string.statusParsing);
			xr.parse(new InputSource(new StringReader(res)));
			end = System.currentTimeMillis();
			Log.d(TAG, new String(String.format("Parsed (%d ms)", end - start)));
			broadcastUpdateStatus(R.string.statusFinished);
			result = true;
		} catch (ParserConfigurationException e) {
			rapp.error(7, e, "parseXML: ParserConfigurationException");
		} catch (IOException e) {
			rapp.error(8, e, "parseXML: IOException");
		} catch (SAXException e) {
			rapp.error(9, e, "parseXML: SAXException");
		} catch (InterruptedException e) {
			// Not a true error, so only for debugging
			Log.d(TAG, "parseXML: InterruptedException", e);
		}
		return result;
	}
	
	/////// Broadcast Stuff
	private void broadcastUpdateDisplayData(Controller ra) {
		Log.d(TAG, "broadcastUpdateDisplayData");
		Intent i = new Intent(UPDATE_DISPLAY_DATA_INTENT);
		i.putExtra(RAData.PCOL_T1, ra.getTemp1());
		i.putExtra(RAData.PCOL_T2, ra.getTemp2());
		i.putExtra(RAData.PCOL_T3, ra.getTemp3());
		i.putExtra(RAData.PCOL_PH, ra.getPH());
		i.putExtra(RAData.PCOL_DP, ra.getPwmD());
		i.putExtra(RAData.PCOL_AP, ra.getPwmA());
		i.putExtra(RAData.PCOL_SAL, ra.getSalinity());
		i.putExtra(RAData.PCOL_ATOHI, ra.getAtoHigh());
		i.putExtra(RAData.PCOL_ATOLO, ra.getAtoLow());
		i.putExtra(RAData.PCOL_LOGDATE, ra.getLogDate());
		i.putExtra(RAData.PCOL_RDATA, ra.getMainRelay().getRelayData());
		i.putExtra(RAData.PCOL_RONMASK, ra.getMainRelay().getRelayOnMask());
		i.putExtra(RAData.PCOL_ROFFMASK, ra.getMainRelay().getRelayOffMask());
		rapp.sendBroadcast(i);
	}
	
	private void broadcastUpdateStatus(int msgid) {
		Log.d(TAG, "broadcastUpdateStatus");
		Intent i = new Intent(UPDATE_STATUS_INTENT);
		i.putExtra(UPDATE_STATUS_ID, msgid);
		rapp.sendBroadcast(i);
	}
	
	private void broadcastErrorMessage() {
		// TODO maybe a notification message or something
		Log.d(TAG, "broadcastErrorMessage");
		Intent i = new Intent(ERROR_MESSAGE_INTENT);
		i.putExtra(ERROR_MESSAGE_STRING, rapp.getErrorMessage());
		rapp.sendBroadcast(i);
	}
}
