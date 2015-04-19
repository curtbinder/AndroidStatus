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
    private String wifiUsername;
    private String wifiPassword;

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

	private void setDefaults ( String host, int port, String command ) {
		this.host = host;
		this.port = port;
		this.command = command;
		location = 0;
		value = 0;
		write = false;
		raUserid = "";
		labels = false;
        wifiUsername = "";
        wifiPassword = "";
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

    public void setWifiUsername ( String username ) {
        this.wifiUsername = username;
    }

    public String getWifiUsername ( ) {
        return this.wifiUsername;
    }

    public void setWifiPassword ( String password ) {
        this.wifiPassword = password;
    }

    public String getWifiPassword ( ) {
        return this.wifiPassword;
    }

    public String getDeviceAuthenticationString ( ) {
        return wifiUsername + ":" + wifiPassword;
    }

    public boolean isDeviceAuthenticationEnabled ( ) {
        // if either the password or username are empty,
        // device authentication is not enabled
        if ( wifiPassword.equals( "" ) || wifiUsername.equals( "" ) ) {
            return false;
        }
        return true;
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

    public void setOverrideChannel ( int port, int value ) {
        this.location = port;
        this.value = value;
    }

    public int getOverrideChannel ( ) {
        return this.location;
    }

    public void setCalibrateType ( int type ) {
        this.location = type;
    }

    public int getCalibrateType ( ) {
        return this.location;
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
            s = String.format(	"http://%s:%d%s", host, port, command );
        } else if ( (command.equals( RequestCommands.MemoryInt ))
                || (command.equals( RequestCommands.MemoryByte )) ) {
            if ( write ) {
                s = String.format(	"http://%s:%d%s%d,%d",
                        host,
                        port, command, location,
                        value );
            } else {
                s = String.format(	"http://%s:%d%s%d", host, port, command, location );
            }
        } else if ( command.equals( RequestCommands.Calibrate ) ) {
            s = String.format( "http://%s:%d%s%d", host, port, command, location);
        } else if ((command.equals(RequestCommands.PwmOverride))
                || (command.equals(RequestCommands.CustomVar))) {
            s = String.format( "http://%s:%d%s%d,%d",
                    host, port, command, location, value);
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
