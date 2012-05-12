package info.curtbinder.reefangel.phone;

/*
 * Copyright (c) 2011-12 by Curt Binder (http://curtbinder.info)
 *
 * This work is made available under the terms of the 
 * Creative Commons Attribution-NonCommercial-ShareAlike 3.0 Unported License
 * http://creativecommons.org/licenses/by-nc-sa/3.0/
 */

//import java.text.DecimalFormatSymbols;

public class Controller {
	public static final byte MAX_EXPANSION_RELAYS = 8;
	public static final byte MAX_RELAY_PORTS = 8;
	public static final byte MAX_TEMP_SENSORS = 3;

	private String updateLogDate;
	private TempSensor[] tempSensors;
	private Number pH;
	private boolean atoLow;
	private boolean atoHigh;
	private byte pwmA;
	private byte pwmD;
	private Number salinity;
	private Number orp;
	private Relay main;
	private byte qtyExpansionRelays;
	private Relay[] expansionRelays;

	public Controller () {
		init();
	}

	public Controller ( byte numExpRelays ) {
		init();
		this.qtyExpansionRelays = numExpRelays;
	}

	private void init ( ) {
		updateLogDate = "";
		tempSensors = new TempSensor[MAX_TEMP_SENSORS];
		int i;
		for ( i = 0; i < MAX_TEMP_SENSORS; i++ ) {
			tempSensors[i] = new TempSensor();
		}
		pH = new Number( (byte) 2 );
		atoLow = false;
		atoHigh = false;
		pwmA = 0;
		pwmD = 0;
		salinity = new Number( (byte) 1 );
		orp = new Number( (byte) 1 );
		main = new Relay();
		expansionRelays = new Relay[MAX_EXPANSION_RELAYS];
		for ( i = 0; i < MAX_EXPANSION_RELAYS; i++ ) {
			expansionRelays[i] = new Relay();
		}
		qtyExpansionRelays = 0;
	}

	public void setNumExpansionRelays ( byte relays ) {
		qtyExpansionRelays = relays;
	}

	public byte getNumExpansionRelays ( ) {
		return qtyExpansionRelays;
	}

	public void setLogDate ( String date ) {
		updateLogDate = date;
	}

	public String getLogDate ( ) {
		return updateLogDate;
	}

	// public void setTempValue(int sensor, int value) {
	// tempSensors[sensor-1].setTemp(value);
	// }

	public void setTempLabel ( int sensor, String label ) {
		tempSensors[sensor - 1].setLabel( label );
	}

	public String getTempLabel ( int sensor ) {
		return tempSensors[sensor - 1].getLabel();
	}

//	public String[] getTempLabels ( ) {
//		return new String[] {	tempSensors[0].getLabel(),
//								tempSensors[1].getLabel(),
//								tempSensors[2].getLabel() };
//	}

	public void setTemp1 ( int value ) {
		tempSensors[0].setTemp( value );
	}

	public String getTemp1 ( ) {
		return tempSensors[0].getTemp();
	}

	public void setTemp2 ( int value ) {
		tempSensors[1].setTemp( value );
	}

	public String getTemp2 ( ) {
		return tempSensors[1].getTemp();
	}

	public void setTemp3 ( int value ) {
		tempSensors[2].setTemp( value );
	}

	public String getTemp3 ( ) {
		return tempSensors[2].getTemp();
	}

	public void setPH ( int value ) {
		pH.setValue( value );
	}

	public String getPH ( ) {
		return pH.toString();
	}

	public void setAtoLow ( boolean v ) {
		atoLow = v;
	}

	public boolean getAtoLow ( ) {
		return atoLow;
	}

	public String getAtoLowText ( ) {
		return getAtoText( atoLow );
	}

	private String getAtoText ( boolean active ) {
		// TODO use strings.xml instead of hard code
		if ( active )
			return "ON";
		else
			return "OFF";
	}

	public void setAtoHigh ( boolean v ) {
		atoHigh = v;
	}

	public boolean getAtoHigh ( ) {
		return atoHigh;
	}

	public String getAtoHighText ( ) {
		return getAtoText( atoHigh );
	}

	public void setPwmA ( byte v ) {
		pwmA = v;
	}

	public String getPwmA ( ) {
		// TODO change to be locale independent
		return new String( String.format( "%d%c", pwmA, '%' ) );
	}

	public void setPwmD ( byte v ) {
		pwmD = v;
	}

	public String getPwmD ( ) {
		// TODO change to be locale independent
		return new String( String.format( "%d%c", pwmD, '%' ) );
	}

	public void setSalinity ( int value ) {
		salinity.setValue( value );
	}

	public String getSalinity ( ) {
		return salinity.toString() + " ppt";
	}

	public void setORP ( int value ) {
		orp.setValue( value );
	}
	
	public String getORP ( ) {
		// TODO get label for ORP
		return orp.toString();
	}
	
	public void setMainRelayData ( short data, short maskOn, short maskOff ) {
		main.setRelayData( data, maskOn, maskOff );
	}

	public void setMainRelayData ( short data ) {
		main.setRelayData( data );
	}

	public void setMainRelayOnMask ( short maskOn ) {
		main.setRelayOnMask( maskOn );
	}

	public void setMainRelayOffMask ( short maskOff ) {
		main.setRelayOffMask( maskOff );
	}

	public Relay getMainRelay ( ) {
		return main;
	}
/*
	public void setExpRelayData (
			int relay,
			short data,
			short maskOn,
			short maskOff ) {
		expansionRelays[relay].setRelayData( data, maskOn, maskOff );
	}
*/
	public void setExpRelayData ( int relay, short data ) {
		// Pass in the 1 based index for relay
		expansionRelays[relay - 1].setRelayData( data );
	}

	public void setExpRelayOnMask ( int relay, short maskOn ) {
		// Pass in the 1 based index for relay
		expansionRelays[relay - 1].setRelayOnMask( maskOn );
	}

	public void setExpRelayOffMask ( int relay, short maskOff ) {
		// Pass in the 1 based index for relay
		expansionRelays[relay - 1].setRelayOffMask( maskOff );
	}

	public Relay getExpRelay ( int relay ) {
		return expansionRelays[relay - 1];
	}
}
