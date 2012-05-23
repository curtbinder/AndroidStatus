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
	public static final byte MAX_PWM_EXPANSION_PORTS = 6;
	public static final byte MAX_AI_CHANNELS = 3;
	public static final byte MAX_CUSTOM_VARIABLES = 8;
	public static final byte MAX_RADION_LIGHT_CHANNELS = 6;
	public static final byte MAX_VORTECH_VALUES = 3;

	public static final byte MODULE_DIMMING = 1 << 0;
	public static final byte MODULE_RF = 1 << 1;
	public static final byte MODULE_AI = 1 << 2;

	// AI channels
	public static final byte AI_WHITE = 0;
	public static final byte AI_BLUE = 1;
	public static final byte AI_ROYALBLUE = 2;

	// Radion channels
	public static final byte RADION_WHITE = 0;
	public static final byte RADION_ROYALBLUE = 1;
	public static final byte RADION_RED = 2;
	public static final byte RADION_GREEN = 3;
	public static final byte RADION_BLUE = 4;
	public static final byte RADION_INTENSITY = 5;

	// Vortech modes
	public static final byte VORTECH_MODE = 0;
	public static final byte VORTECH_SPEED = 1;
	public static final byte VORTECH_DURATION = 2;

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
	private byte expansionModules;
	private byte relayExpansionModules;
	private byte ioChannels;
	private byte[] pwmExpansion;
	private byte[] aiChannels;
	private byte[] radionChannels;
	private byte[] customVariables;
	private byte[] vortechValues;

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
		pwmExpansion = new byte[MAX_PWM_EXPANSION_PORTS];
		for ( i = 0; i < MAX_PWM_EXPANSION_PORTS; i++ ) {
			pwmExpansion[i] = 0;
		}
		salinity = new Number( (byte) 1 );
		orp = new Number();
		main = new Relay();
		expansionRelays = new Relay[MAX_EXPANSION_RELAYS];
		for ( i = 0; i < MAX_EXPANSION_RELAYS; i++ ) {
			expansionRelays[i] = new Relay();
		}
		qtyExpansionRelays = 0;
		expansionModules = 0;
		relayExpansionModules = 0;
		ioChannels = 0;
		aiChannels = new byte[MAX_AI_CHANNELS];
		for ( i = 0; i < MAX_AI_CHANNELS; i++ ) {
			aiChannels[i] = 0;
		}
		radionChannels = new byte[MAX_RADION_LIGHT_CHANNELS];
		for ( i = 0; i < MAX_RADION_LIGHT_CHANNELS; i++ ) {
			radionChannels[i] = 0;
		}
		customVariables = new byte[MAX_CUSTOM_VARIABLES];
		for ( i = 0; i < MAX_CUSTOM_VARIABLES; i++ ) {
			customVariables[i] = 0;
		}
		vortechValues = new byte[MAX_VORTECH_VALUES];
		for ( i = 0; i < MAX_VORTECH_VALUES; i++ ) {
			vortechValues[i] = 0;
		}
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

	// public String[] getTempLabels ( ) {
	// return new String[] { tempSensors[0].getLabel(),
	// tempSensors[1].getLabel(),
	// tempSensors[2].getLabel() };
	// }

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

	public void setPwmExpansion ( int channel, byte v ) {
		pwmExpansion[channel] = v;
	}

	public String getPwmExpansion ( int channel ) {
		// TODO change to be locale independent
		return new String( String.format( "%d%c", pwmExpansion[channel], '%' ) );
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
		return orp.toString() + " mV";
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
	 * public void setExpRelayData ( int relay, short data, short maskOn, short
	 * maskOff ) { expansionRelays[relay].setRelayData( data, maskOn, maskOff );
	 * }
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

	public byte getCustomVariable ( byte var ) {
		return customVariables[var];
	}

	public void setCustomVariable ( byte var, byte value ) {
		customVariables[var] = value;
	}

	public byte getAIChannel ( byte channel ) {
		return aiChannels[channel];
	}

	public void setAIChannel ( byte channel, byte value ) {
		aiChannels[channel] = value;
	}

	public byte getRadionChannel ( byte channel ) {
		return radionChannels[channel];
	}

	public void setRadionChannel ( byte channel, byte value ) {
		radionChannels[channel] = value;
	}

	public byte getVortechValue ( byte type ) {
		return vortechValues[type];
	}

	public void setVortechValue ( byte type, byte value ) {
		vortechValues[type] = value;
	}

	public byte getExpansionModules ( ) {
		return expansionModules;
	}
	
	public void setExpansionModules ( byte em ) {
		expansionModules = em;
	}

	public static boolean isDimmingModuleInstalled ( byte expansionModules ) {
		return (expansionModules & MODULE_DIMMING) == 1;
	}

	public static boolean isRFModuleInstalled ( byte expansionModules ) {
		return (expansionModules & MODULE_RF) == 1;
	}

	public static boolean isAIModuleInstalled ( byte expansionModules ) {
		return (expansionModules & MODULE_AI) == 1;
	}

	public byte getRelayExpansionModules ( ) {
		return relayExpansionModules;
	}
	
	public void setRelayExpansionModules ( byte rem ) {
		relayExpansionModules = rem;
	}
	
	public static int getRelayExpansionModulesInstalled ( byte rem ) {
		int qty = 0;
		for ( int i = 7; i >= 0; i-- ) {
			if ( (rem & (1<<i)) == 1 ) {
				qty = i+1;
				break;
			}
		}
		return qty;
	}
	
	public byte getIOChannels ( ) {
		return ioChannels;
	}
	
	public void setIOChannels ( byte ioChannels ) {
		this.ioChannels = ioChannels;
	}
	
	public static boolean getIOChannel ( byte ioChannels, byte channel ) {
		// channel is 0 based
		return (ioChannels & (1 << channel)) == 1;
	}
}
