package info.curtbinder.reefangel.phone;

public class Relay {
	static final byte PORT_OFF = 0;
	static final byte PORT_ON = 1;

	static final byte PORT_STATE_ON = 0;
	static final byte PORT_STATE_OFF = 1;
	static final byte PORT_STATE_AUTO = 2;

	static final short PORT_1 = 1 << 0;
	static final short PORT_2 = 1 << 1;
	static final short PORT_3 = 1 << 2;
	static final short PORT_4 = 1 << 3;
	static final short PORT_5 = 1 << 4;
	static final short PORT_6 = 1 << 5;
	static final short PORT_7 = 1 << 6;
	static final short PORT_8 = 1 << 7;

	private short data;
	private short maskOn;
	private short maskOff;

	public Relay () {
		data = 0;
		maskOn = 0;
		maskOff = 0;
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

	// Get port statuses
	/*
	 * maskOn = 1 & maskOff = 1 == PORT_ON 
	 * maskOn = 0 & maskOff = 1 == PORT_AUTO
	 * maskOn = 0 & maskOff = 0 == PORT_OFF
	 */
	public short getPort1Status ( ) {
		return getPortStatus(1);
	}

	public short getPort2Status ( ) {
		return getPortStatus(2);
	}

	public short getPort3Status ( ) {
		return getPortStatus(3);
	}

	public short getPort4Status ( ) {
		return getPortStatus(4);
	}

	public short getPort5Status ( ) {
		return getPortStatus(5);
	}

	public short getPort6Status ( ) {
		return getPortStatus(6);
	}

	public short getPort7Status ( ) {
		return getPortStatus(7);
	}

	public short getPort8Status ( ) {
		return getPortStatus(8);
	}
	
	public short getPortStatus ( int port ) {
		short status = PORT_STATE_OFF;
		short portmask = getPortMask(port);
		if ( ((maskOn & portmask) == PORT_ON) && ((maskOff & portmask) == PORT_ON) ) {
			status = PORT_STATE_ON;
		} else if ( ((maskOn & portmask) == PORT_OFF)
				&& ((maskOff & portmask) == PORT_ON) ) {
			status = PORT_STATE_AUTO;
		}
		return status;		
	}

	// ports
	public boolean isPort1On ( ) {
		return isPortOn(1);
	}

	public boolean isPort2On ( ) {
		return isPortOn(2);
	}

	public boolean isPort3On ( ) {
		return isPortOn(3);
	}

	public boolean isPort4On ( ) {
		return isPortOn(4);
	}

	public boolean isPort5On ( ) {
		return isPortOn(5);
	}

	public boolean isPort6On ( ) {
		return isPortOn(6);
	}

	public boolean isPort7On ( ) {
		return isPortOn(7);
	}

	public boolean isPort8On ( ) {
		return isPortOn(8);
	}

	public boolean isPortOn ( int port ) {
		boolean b = false;
		short portmask = getPortMask(port);
		/*
		short status = getPortStatus(port);
		if ( status == PORT_STATE_ON ) {
			// masked on
			b = true;
		} else if ( status == PORT_STATE_AUTO ) {
		*/
			// auto - based on controller settings
			b = ((data & portmask) != PORT_OFF);
		//} // else masked off
		return b;
	}
	
	public short getPortValue( int port ) {
		return (short) (data & getPortMask(port));
	}
	
	private short getPortMask ( int port ) {
		short portmask;
		switch ( port ) {
		default:
		case 1:
			portmask = PORT_1;
			break;
		case 2:
			portmask = PORT_2;
			break;
		case 3:
			portmask = PORT_3;
			break;
		case 4:
			portmask = PORT_4;
			break;
		case 5:
			portmask = PORT_5;
			break;
		case 6:
			portmask = PORT_6;
			break;
		case 7:
			portmask = PORT_7;
			break;
		case 8:
			portmask = PORT_8;
			break;
		}
		return portmask;
	}
}
