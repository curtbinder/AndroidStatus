package info.curtbinder.reefangel.phone;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import android.util.Log;

public class Host {
	private static final String TAG = "RAHost";
	private String host;
	private int port;
	private String command;
	private int timeoutConnect;
	private int timeoutRead;
	private String raUserid;
	private final String RAHOST = "http://www.reefangel.com/status/xml.aspx?id=";
	
	// for memory reading/writing
	private int location;
	private int value;
	private boolean write;
	
	Host() {
		setDefaults("", 80, Globals.requestNone);
	}
	
	Host(String userid) {
		setDefaults("", 80, Globals.requestReefAngel);
		raUserid = userid;
	}
	
	Host(String host, int port, String command) {
		setDefaults(host, port, command);
	}
	
	Host(String host, String port, String command) {
		setDefaults(host, Integer.parseInt(port), command);
	}
	
	private void setDefaults(String host, int port, String command) {
		this.host = host;
		this.port = port;
		this.command = command;
		timeoutConnect = 15000;  // milliseconds
		timeoutRead = 10000;  // milliseconds
		location = 0;
		value = 0;
		write = false;
		raUserid = "";
	}
	
	public void setHost(String host) {
		this.host = host;
	}
	
	public void setPort(String port) {
		this.port = Integer.parseInt(port);
	}
	
	public void setCommand(String command) {
		this.command = command;
	}
	
	public void setUserId(String userid) {
		raUserid = userid;
		setCommand(Globals.requestReefAngel);
	}
	
	public int getConnectTimeout() {
		return timeoutConnect;
	}
	
	public int getReadTimeout() {
		return timeoutRead;
	}
	
	public void setReadLocation(int location) {
		this.location = location;
		this.value = 0;
		this.write = false;
	}
	
	public void setWriteLocation(int location, int value) {
		this.location = location;
		this.value = value;
		this.write = true;
	}
	
	public String toString() {
		String s = "";
		if ( (command.equals( Globals.requestStatus )) || 
			 (command.equals( Globals.requestVersion)) ) {
			s = new String(String.format("http://%s:%d%s", host, port, command));
		} else if ( (command.equals( Globals.requestMemoryInt )) ||
					(command.equals( Globals.requestMemoryByte)) ) {
			if ( write ) {
				s = new String(String.format("http://%s:%d%s%d,%d", 
						host, port, command, location, value));
			} else {
				s = new String(String.format("http://%s:%d%s%d", 
						host, port, command, location));
			}
		} else if ( command.equals( Globals.requestReefAngel ) ) {
			String encodedId;
			try {
				encodedId = URLEncoder.encode(raUserid, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				Log.e(TAG, "Failed URL encoder");
				encodedId = "";
			}
			s = RAHOST + encodedId;
		}
		return s;
	}
}
