/*
 * Copyright (c) 2011-2013 by Curt Binder (http://curtbinder.info)
 * 
 * This work is made available under the terms of the Creative Commons
 * Attribution-NonCommercial-ShareAlike 3.0 Unported License
 * http://creativecommons.org/licenses/by-nc-sa/3.0/
 */

package info.curtbinder.reefangel.service;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import android.util.Log;

public class Host {
	private static final String TAG = Host.class.getSimpleName();
	private String host;
	private int port;
	private String command;
	private int timeoutConnect;
	private int timeoutRead;
	private String raUserid;

	private final String RAPARAMS =
			"http://forum.reefangel.com/status/params.aspx?id=";
	private final String RALABELS =
			"http://forum.reefangel.com/status/labels.aspx?id=";

	// for memory reading/writing
	private int location;
	private int value;
	private boolean write;

	// for labels only
	private boolean labels;

	Host ( int timeoutConnect, int timeoutRead ) {
		setDefaults( "", 80, RequestCommands.None );
		this.timeoutConnect = timeoutConnect;
		this.timeoutRead = timeoutRead;
	}
/*
	Host ( String userid ) {
		setDefaults( "", 80, RequestCommands.ReefAngel );
		raUserid = userid;
	}

	Host ( String host, int port, String command ) {
		setDefaults( host, port, command );
	}

	Host ( String host, String port, String command ) {
		setDefaults( host, Integer.parseInt( port ), command );
	}
*/
	private void setDefaults ( String host, int port, String command ) {
		this.host = host;
		this.port = port;
		this.command = command;
		//timeoutConnect = 15000; // milliseconds
		//timeoutRead = 10000; // milliseconds
		location = 0;
		value = 0;
		write = false;
		raUserid = "";
		labels = false;
	}

	public void setHost ( String host ) {
		this.host = host;
	}

	public void setPort ( String port ) {
		this.port = Integer.parseInt( port );
	}

	public void setCommand ( String command ) {
		this.command = command;
	}

	public String getCommand ( ) {
		return this.command;
	}

	public void setUserId ( String userid ) {
		raUserid = userid;
		setCommand( RequestCommands.ReefAngel );
	}

	public void setGetLabelsOnly ( boolean getLabels ) {
		// TODO consider putting a check for a valid raUserid
		this.labels = true;
		setCommand( RequestCommands.ReefAngel );
	}

	public boolean isRequestForLabels ( ) {
		return this.labels;
	}

	public int getConnectTimeout ( ) {
		return timeoutConnect;
	}
	
	public void setConnectTimeout ( int timeout ) {
		timeoutConnect = timeout;
	}

	public int getReadTimeout ( ) {
		return timeoutRead;
	}
	
	public void setReadTimout ( int timeout ) {
		timeoutRead = timeout;
	}

	public void setReadLocation ( int location ) {
		this.location = location;
		this.value = 0;
		this.write = false;
	}

	public void setWriteLocation ( int location, int value ) {
		this.location = location;
		this.value = value;
		this.write = true;
	}

	public boolean isWrite ( ) {
		return this.write;
	}
	
	public void setOverrideLocation ( int port, int value ) {
		this.location = port;
		this.value = value;
	}

	public String toString ( ) {
		// TODO improve error message with a null host string
		String s = "";
		if ( (command.startsWith( RequestCommands.Relay ))
				|| (command.equals( RequestCommands.Status ))
				|| (command.equals( RequestCommands.Version ))
				|| (command.equals( RequestCommands.FeedingMode ))
				|| (command.equals( RequestCommands.ExitMode ))
				|| (command.equals( RequestCommands.WaterMode ))
				|| (command.equals( RequestCommands.AtoClear ))
				|| (command.equals( RequestCommands.OverheatClear ))
				|| (command.startsWith( RequestCommands.DateTime ))
				|| (command.equals( RequestCommands.LightsOn ))
				|| (command.equals( RequestCommands.LightsOff ))
				|| (command.equals( RequestCommands.Reboot )) ) {
			s =
					new String( String.format(	"http://%s:%d%s", host, port,
												command ) );
		} else if ( (command.equals( RequestCommands.MemoryInt ))
					|| (command.equals( RequestCommands.MemoryByte )) ) {
			if ( write ) {
				s =
						new String( String.format(	"http://%s:%d%s%d,%d",
													host,
													port, command, location,
													value ) );
			} else {
				s =
						new String( String.format(	"http://%s:%d%s%d", host,
													port, command, location ) );
			}
		} else if ( command.equals( RequestCommands.PwmOverride ) ) {
			s = new String ( String.format( "http://%s:%d%s%d,%d",
			                                host, port,
			                                command, location, value) );
		} else if ( command.equals( RequestCommands.ReefAngel ) ) {
			String encodedId;
			try {
				encodedId = URLEncoder.encode( raUserid, "UTF-8" );
			} catch ( UnsupportedEncodingException e ) {
				Log.e( TAG, "Failed URL encoder" );
				encodedId = "";
			}

			if ( labels ) {
				s = RALABELS + encodedId;
			} else {
				s = RAPARAMS + encodedId;
			}
		}
		return s;
	}
}
