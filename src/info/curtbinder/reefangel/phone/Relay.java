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

	public void setRelayDataMaskOn ( short maskOn ) {
		this.maskOn = maskOn;
	}

	public void setRelayDataMaskOff ( short maskOff ) {
		this.maskOff = maskOff;
	}

	// Get port statuses
	/*
	 * maskOn = 1 & maskOff = 1 == PORT_ON 
	 * maskOn = 0 & maskOff = 1 == PORT_AUTO
	 * maskOn = 0 & maskOff = 0 == PORT_OFF
	 */
	public short getPort1Status ( ) {
		short status = PORT_STATE_OFF;
		if ( ((maskOn & PORT_1) == PORT_ON) && ((maskOff & PORT_1) == PORT_ON) ) {
			status = PORT_STATE_ON;
		} else if ( ((maskOn & PORT_1) == PORT_OFF)
				&& ((maskOff & PORT_1) == PORT_ON) ) {
			status = PORT_STATE_AUTO;
		}
		return status;
	}

	public short getPort2Status ( ) {
		short status = PORT_STATE_OFF;
		if ( ((maskOn & PORT_2) == PORT_ON) && ((maskOff & PORT_2) == PORT_ON) ) {
			status = PORT_STATE_ON;
		} else if ( ((maskOn & PORT_2) == PORT_OFF)
				&& ((maskOff & PORT_2) == PORT_ON) ) {
			status = PORT_STATE_AUTO;
		}
		return status;
	}

	public short getPort3Status ( ) {
		short status = PORT_STATE_OFF;
		if ( ((maskOn & PORT_3) == PORT_ON) && ((maskOff & PORT_3) == PORT_ON) ) {
			status = PORT_STATE_ON;
		} else if ( ((maskOn & PORT_3) == PORT_OFF)
				&& ((maskOff & PORT_3) == PORT_ON) ) {
			status = PORT_STATE_AUTO;
		}
		return status;
	}

	public short getPort4Status ( ) {
		short status = PORT_STATE_OFF;
		if ( ((maskOn & PORT_4) == PORT_ON) && ((maskOff & PORT_4) == PORT_ON) ) {
			status = PORT_STATE_ON;
		} else if ( ((maskOn & PORT_4) == PORT_OFF)
				&& ((maskOff & PORT_4) == PORT_ON) ) {
			status = PORT_STATE_AUTO;
		}
		return status;
	}

	public short getPort5Status ( ) {
		short status = PORT_STATE_OFF;
		if ( ((maskOn & PORT_5) == PORT_ON) && ((maskOff & PORT_5) == PORT_ON) ) {
			status = PORT_STATE_ON;
		} else if ( ((maskOn & PORT_5) == PORT_OFF)
				&& ((maskOff & PORT_5) == PORT_ON) ) {
			status = PORT_STATE_AUTO;
		}
		return status;
	}

	public short getPort6Status ( ) {
		short status = PORT_STATE_OFF;
		if ( ((maskOn & PORT_6) == PORT_ON) && ((maskOff & PORT_6) == PORT_ON) ) {
			status = PORT_STATE_ON;
		} else if ( ((maskOn & PORT_6) == PORT_OFF)
				&& ((maskOff & PORT_6) == PORT_ON) ) {
			status = PORT_STATE_AUTO;
		}
		return status;
	}

	public short getPort7Status ( ) {
		short status = PORT_STATE_OFF;
		if ( ((maskOn & PORT_7) == PORT_ON) && ((maskOff & PORT_7) == PORT_ON) ) {
			status = PORT_STATE_ON;
		} else if ( ((maskOn & PORT_7) == PORT_OFF)
				&& ((maskOff & PORT_7) == PORT_ON) ) {
			status = PORT_STATE_AUTO;
		}
		return status;
	}

	public short getPort8Status ( ) {
		short status = PORT_STATE_OFF;
		if ( ((maskOn & PORT_8) == PORT_ON) && ((maskOff & PORT_8) == PORT_ON) ) {
			status = PORT_STATE_ON;
		} else if ( ((maskOn & PORT_8) == PORT_OFF)
				&& ((maskOff & PORT_8) == PORT_ON) ) {
			status = PORT_STATE_AUTO;
		}
		return status;
	}

	// ports
	public boolean isPort1On ( ) {
		return ((data & PORT_1) != PORT_OFF);
	}

	public boolean isPort2On ( ) {
		return ((data & PORT_2) != PORT_OFF);
	}

	public boolean isPort3On ( ) {
		return ((data & PORT_3) != PORT_OFF);
	}

	public boolean isPort4On ( ) {
		return ((data & PORT_4) != PORT_OFF);
	}

	public boolean isPort5On ( ) {
		return ((data & PORT_5) != PORT_OFF);
	}

	public boolean isPort6On ( ) {
		return ((data & PORT_6) != PORT_OFF);
	}

	public boolean isPort7On ( ) {
		return ((data & PORT_7) != PORT_OFF);
	}

	public boolean isPort8On ( ) {
		return ((data & PORT_8) != PORT_OFF);
	}
}
