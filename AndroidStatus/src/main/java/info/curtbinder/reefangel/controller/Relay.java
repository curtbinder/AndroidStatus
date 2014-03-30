/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Curt Binder
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

package info.curtbinder.reefangel.controller;

public class Relay {
	// All Port Referencing functions are 1 based
	public static final byte PORT_OFF = 0;
	public static final byte PORT_ON = 1;

	public static final byte PORT_STATE_OFF = 0;
	public static final byte PORT_STATE_ON = 1;
	public static final byte PORT_STATE_AUTO = 2;

	private short data;
	private short maskOn;
	private short maskOff;

	private String[] labels;

	public Relay () {
		data = 0;
		maskOn = 0;
		maskOff = 0;
		initLabels();
	}

	public Relay ( short data, short maskOn, short maskOff ) {
		this.data = data;
		this.maskOn = maskOn;
		this.maskOff = maskOff;
		initLabels();
	}

	private void initLabels ( ) {
		labels = new String[Controller.MAX_RELAY_PORTS];
		for ( int i = 0; i < Controller.MAX_RELAY_PORTS; i++ ) {
			// TODO use strings.xml instead of hard code
			labels[i] = String.format( "Port %d", i + 1 );
		}
	}

	public void setRelayData ( short data, short maskOn, short maskOff ) {
		this.data = data;
		this.maskOn = maskOn;
		this.maskOff = maskOff;
	}

	public void setRelayData ( short data ) {
		this.data = data;
	}

	public void setRelayOnMask ( short maskOn ) {
		this.maskOn = maskOn;
	}

	public void setRelayOffMask ( short maskOff ) {
		this.maskOff = maskOff;
	}

	public short getRelayData ( ) {
		return this.data;
	}

	public short getRelayOnMask ( ) {
		return this.maskOn;
	}

	public short getRelayOffMask ( ) {
		return this.maskOff;
	}

	// public String[] getPortLabels() {
	// return labels;
	// }

	public String getPortLabel ( int port ) {
		return labels[port - 1];
	}

	// public void setPortLabels(String[] labels) {
	// this.labels = labels;
	// }

	public void setPortLabel ( int port, String label ) {
		labels[port - 1] = label;
	}

	// Get port statuses
	/*
	 * maskOn = 1 & maskOff = 1 == PORT_ON maskOn = 0 & maskOff = 1 == PORT_AUTO
	 * maskOn = 0 & maskOff = 0 == PORT_OFF
	 */
	public short getPort1Status ( ) {
		return getPortStatus( 1 );
	}

	public short getPort2Status ( ) {
		return getPortStatus( 2 );
	}

	public short getPort3Status ( ) {
		return getPortStatus( 3 );
	}

	public short getPort4Status ( ) {
		return getPortStatus( 4 );
	}

	public short getPort5Status ( ) {
		return getPortStatus( 5 );
	}

	public short getPort6Status ( ) {
		return getPortStatus( 6 );
	}

	public short getPort7Status ( ) {
		return getPortStatus( 7 );
	}

	public short getPort8Status ( ) {
		return getPortStatus( 8 );
	}

	public short getPortStatus ( int port ) {
		short status = PORT_STATE_OFF;
		if ( (getPortMaskOnValue( port ) == PORT_ON)
				&& (getPortMaskOffValue( port ) == PORT_ON) ) {
			status = PORT_STATE_ON;
		} else if ( (getPortMaskOnValue( port ) == PORT_OFF)
					&& (getPortMaskOffValue( port ) == PORT_ON) ) {
			status = PORT_STATE_AUTO;
		}
		return status;
	}

	public short getPortMaskOnValue ( int port ) {
		return (short) ((maskOn & (1 << (port - 1))) >> (port - 1));
	}

	public short getPortMaskOffValue ( int port ) {
		return (short) ((maskOff & (1 << (port - 1))) >> (port - 1));
	}

	public short getPortValue ( int port ) {
		return (short) (data & (1 << (port - 1)));
	}

	// ports
	public boolean isPort1On ( boolean usemask ) {
		return isPortOn( 1, usemask );
	}

	public boolean isPort2On ( boolean usemask ) {
		return isPortOn( 2, usemask );
	}

	public boolean isPort3On ( boolean usemask ) {
		return isPortOn( 3, usemask );
	}

	public boolean isPort4On ( boolean usemask ) {
		return isPortOn( 4, usemask );
	}

	public boolean isPort5On ( boolean usemask ) {
		return isPortOn( 5, usemask );
	}

	public boolean isPort6On ( boolean usemask ) {
		return isPortOn( 6, usemask );
	}

	public boolean isPort7On ( boolean usemask ) {
		return isPortOn( 7, usemask );
	}

	public boolean isPort8On ( boolean usemask ) {
		return isPortOn( 8, usemask );
	}

	public boolean isPortOn ( int port, boolean usemask ) {
		boolean b = false;
		if ( usemask ) {
			short status = getPortStatus( port );
			if ( status == PORT_STATE_ON ) {
				// masked on
				b = true;
			} else if ( status == PORT_STATE_AUTO ) {
				// auto - based on controller settings
				b = (getPortValue( port ) != PORT_OFF);
			} // else masked off
		} else {
			b = (getPortValue( port ) != PORT_OFF);
		}
		return b;
	}
}
