package info.curtbinder.reefangel.controller;

import java.util.Locale;

import info.curtbinder.reefangel.phone.Globals;
import android.util.Log;


/*
 * Copyright (c) 2011-13 by Curt Binder (http://curtbinder.info)
 *
 * This work is made available under the terms of the 
 * Creative Commons Attribution-NonCommercial-ShareAlike 3.0 Unported License
 * http://creativecommons.org/licenses/by-nc-sa/3.0/
 */

public class Controller {
	public static final byte MAX_CONTROLLER_VALUES = 17;
	public static final byte MAX_EXPANSION_RELAYS = 8;
	public static final byte MAX_RELAY_PORTS = 8;
	public static final byte MAX_TEMP_SENSORS = 3;
	public static final byte MAX_PWM_EXPANSION_PORTS = 6;
	public static final byte MAX_AI_CHANNELS = 3;
	public static final byte MAX_CUSTOM_VARIABLES = 8;
	public static final byte MAX_IO_CHANNELS = 6;
	public static final byte MAX_RADION_LIGHT_CHANNELS = 6;
	public static final byte MAX_VORTECH_VALUES = 3;
	public static final byte MAX_WATERLEVEL_PORTS = 5;

	// First set of expansion modules - EM
	public static final short MODULE_DIMMING = 1 << 0;
	public static final short MODULE_RF = 1 << 1;
	public static final short MODULE_AI = 1 << 2;
	public static final short MODULE_SALINITY = 1 << 3;
	public static final short MODULE_ORP = 1 << 4;
	public static final short MODULE_IO = 1 << 5;
	public static final short MODULE_PHEXPANSION = 1 << 6;
	public static final short MODULE_WATERLEVEL = 1 << 7;
	
	// Second set of expansion modules - EM1
	public static final short MODULE_HUMIDITY = 1 << 0;
	public static final short MODULE_DCPUMP = 1 << 1;
	public static final short MODULE_LEAKDETECTOR = 1 << 2;

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
	private NumberWithLabel[] tempSensors;
	private NumberWithLabel pH;
	private NumberWithLabel pHExp;
	private boolean atoLow;
	private boolean atoHigh;
	private ShortWithLabelOverride pwmA;
	private ShortWithLabelOverride pwmD;
	private ShortWithLabel[] waterlevel;
	private ShortWithLabel humidity;
	private NumberWithLabel salinity;
	private NumberWithLabel orp;
	private Relay main;
	private byte qtyExpansionRelays;
	private Relay[] expansionRelays;
	private short expansionModules;
	private short expansionModules1;
	private short relayExpansionModules;
	private short ioChannels;
	private String[] ioChannelsLabels;
	private ShortWithLabelOverride[] pwmExpansion;
	private ShortWithLabelOverride[] aiChannels;
	private ShortWithLabelOverride[] radionChannels;
	private ShortWithLabel[] customVariables;
	private short[] vortechValues;

	public Controller () {
		init();
	}

	public Controller ( byte numExpRelays ) {
		init();
		this.qtyExpansionRelays = numExpRelays;
	}

	private void init ( ) {
		updateLogDate = "";
		tempSensors = new NumberWithLabel[MAX_TEMP_SENSORS];
		int i;
		for ( i = 0; i < MAX_TEMP_SENSORS; i++ ) {
			tempSensors[i] = new NumberWithLabel( (byte) 1 );
		}
		pH = new NumberWithLabel( (byte) 2 );
		pHExp = new NumberWithLabel( (byte) 2 );
		atoLow = false;
		atoHigh = false;
		pwmA = new ShortWithLabelOverride();
		pwmD = new ShortWithLabelOverride();
		pwmExpansion = new ShortWithLabelOverride[MAX_PWM_EXPANSION_PORTS];
		for ( i = 0; i < MAX_PWM_EXPANSION_PORTS; i++ ) {
			pwmExpansion[i] = new ShortWithLabelOverride();
		}
		waterlevel = new ShortWithLabel[MAX_WATERLEVEL_PORTS];
		for ( i = 0; i < MAX_WATERLEVEL_PORTS; i++ ) {
			waterlevel[i] = new ShortWithLabel();
		}
		humidity = new ShortWithLabel();
		salinity = new NumberWithLabel( (byte) 1 );
		orp = new NumberWithLabel();
		main = new Relay();
		expansionRelays = new Relay[MAX_EXPANSION_RELAYS];
		for ( i = 0; i < MAX_EXPANSION_RELAYS; i++ ) {
			expansionRelays[i] = new Relay();
		}
		qtyExpansionRelays = 0;
		expansionModules = 0;
		expansionModules1 = 0;
		relayExpansionModules = 0;
		ioChannels = 0;
		ioChannelsLabels = new String[MAX_IO_CHANNELS];
		for ( i = 0; i < MAX_IO_CHANNELS; i++ ) {
			ioChannelsLabels[i] = "";
		}
		aiChannels = new ShortWithLabelOverride[MAX_AI_CHANNELS];
		for ( i = 0; i < MAX_AI_CHANNELS; i++ ) {
			aiChannels[i] = new ShortWithLabelOverride();
		}
		radionChannels = new ShortWithLabelOverride[MAX_RADION_LIGHT_CHANNELS];
		for ( i = 0; i < MAX_RADION_LIGHT_CHANNELS; i++ ) {
			radionChannels[i] = new ShortWithLabelOverride();
		}
		customVariables = new ShortWithLabel[MAX_CUSTOM_VARIABLES];
		for ( i = 0; i < MAX_CUSTOM_VARIABLES; i++ ) {
			customVariables[i] = new ShortWithLabel();
		}
		vortechValues = new short[MAX_VORTECH_VALUES];
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

	public void setTempLabel ( int sensor, String label ) {
		tempSensors[sensor - 1].setLabel( label );
	}

	public String getTempLabel ( int sensor ) {
		return tempSensors[sensor - 1].getLabel();
	}

	public void setTemp1 ( int value ) {
		tempSensors[0].setData( value );
	}

	public String getTemp1 ( ) {
		return tempSensors[0].getData();
	}

	public void setTemp2 ( int value ) {
		tempSensors[1].setData( value );
	}

	public String getTemp2 ( ) {
		return tempSensors[1].getData();
	}

	public void setTemp3 ( int value ) {
		tempSensors[2].setData( value );
	}

	public String getTemp3 ( ) {
		return tempSensors[2].getData();
	}

	public void setPH ( int value ) {
		pH.setData( value );
	}

	public String getPH ( ) {
		return pH.getData();
	}

	public void setPHLabel ( String label ) {
		pH.setLabel( label );
	}

	public String getPHLabel ( ) {
		return pH.getLabel();
	}

	public void setPHExp ( int value ) {
		pHExp.setData( value );
	}

	public String getPHExp ( ) {
		return pHExp.getData();
	}

	public void setPHExpLabel ( String label ) {
		pHExp.setLabel( label );
	}

	public String getPHExpLabel ( ) {
		return pHExp.getLabel();
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

	public void setPwmA ( short v ) {
		pwmA.setData( v );
	}

	public short getPwmA ( ) {
		return pwmA.getData();
	}
	
	public short getPwmAOverride ( ) {
		return pwmA.getOverrideValue();
	}
	
	public void setPwmAOverride ( short v ) {
		pwmA.setOverride( v );
	}

	public void setPwmALabel ( String label ) {
		pwmA.setLabel( label );
	}

	public String getPwmALabel ( ) {
		return pwmA.getLabel();
	}

	public void setPwmD ( short v ) {
		pwmD.setData( v );
	}

	public short getPwmD ( ) {
		return pwmD.getData();
	}
	
	public short getPwmDOverride ( ) {
		return pwmD.getOverrideValue();
	}
	
	public void setPwmDOverride ( short v ) {
		pwmD.setOverride( v );
	}

	public void setPwmDLabel ( String label ) {
		pwmD.setLabel( label );
	}

	public String getPwmDLabel ( ) {
		return pwmD.getLabel();
	}

	public void setPwmExpansion ( short channel, short v ) {
		pwmExpansion[channel].setData( v );
	}

	public short getPwmExpansion ( short channel ) {
		return pwmExpansion[channel].getData();
	}
	
	
	public short getPwmExpansionOverride ( short channel ) {
		return pwmExpansion[channel].getOverrideValue();
	}
	
	public void setPwmExpansionOverride ( short channel, short v ) {
		pwmExpansion[channel].setOverride( v );
	}

	public void setPwmExpansionLabel ( short channel, String label ) {
		pwmExpansion[channel].setLabel( label );
	}

	public String getPwmExpansionLabel ( short channel ) {
		return pwmExpansion[channel].getLabel();
	}

	public void setWaterLevel ( short port, short value ) {
		waterlevel[port].setData( value );;
	}
	
	public short getWaterLevel ( short port ) {
		return waterlevel[port].getData();
	}
	
	public void setWaterLevelLabel ( short port, String label ) {
		waterlevel[port].setLabel( label );
	}
	
	public String getWaterLevelLabel ( short port ) {
		return waterlevel[port].getLabel();
	}

	public void setHumidity ( short value ) {
		humidity.setData( value );
	}
	
	public short getHumidity ( ) {
		return humidity.getData();
	}
	
	public void setHumidityLabel ( String label ) {
		humidity.setLabel( label );
	}
	
	public String getHumidityLabel ( ) {
		return humidity.getLabel();
	}
	
	public void setSalinity ( int value ) {
		salinity.setData( value );
	}

	public String getSalinity ( ) {
		return salinity.getData();
	}

	public void setSalinityLabel ( String label ) {
		salinity.setLabel( label );
	}

	public String getSalinityLabel ( ) {
		return salinity.getLabel();
	}

	public void setORP ( int value ) {
		orp.setData( value );
	}

	public String getORP ( ) {
		return orp.getData();
	}

	public void setORPLabel ( String label ) {
		orp.setLabel( label );
	}

	public String getORPLabel ( ) {
		return orp.getLabel();
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

	public short getCustomVariable ( short var ) {
		return customVariables[var].getData();
	}

	public void setCustomVariable ( short var, short value ) {
		customVariables[var].setData( value );
	}

	public String getCustomVariableLabel ( short var ) {
		return customVariables[var].getLabel();
	}

	public void setCustomVariableLabel ( short var, String label ) {
		customVariables[var].setLabel( label );
	}

	public short getAIChannel ( byte channel ) {
		return aiChannels[channel].getData();
	}

	public void setAIChannel ( byte channel, short value ) {
		aiChannels[channel].setData( value );
	}
	
	public short getAIChannelOverride ( byte channel ) {
		return aiChannels[channel].getOverrideValue();
	}
	
	public void setAIChannelOverride ( byte channel, short value ) {
		aiChannels[channel].setOverride( value );
	}

	public short getRadionChannel ( byte channel ) {
		return radionChannels[channel].getData();
	}

	public void setRadionChannel ( byte channel, short value ) {
		radionChannels[channel].setData( value );
	}
	
	public short getRadionChannelOverride ( byte channel ) {
		return radionChannels[channel].getOverrideValue();
	}
	
	public void setRadionChannelOverride ( byte channel, short value ) {
		radionChannels[channel].setOverride( value );;
	}

	public short getVortechValue ( byte type ) {
		return vortechValues[type];
	}

	public void setVortechValue ( byte type, short value ) {
		vortechValues[type] = value;
	}

	public short getExpansionModules ( ) {
		return expansionModules;
	}

	public void setExpansionModules ( short em ) {
		expansionModules = em;
	}

	public short getExpansionModules1 ( ) {
		return expansionModules1;
	}
	
	public void setExpansionModules1 ( short em1 ) {
		expansionModules1 = em1;
	}
	
	public static boolean isDimmingModuleInstalled ( short expansionModules ) {
		return (expansionModules & MODULE_DIMMING) == MODULE_DIMMING;
	}

	public static boolean isRFModuleInstalled ( short expansionModules ) {
		return (expansionModules & MODULE_RF) == MODULE_RF;
	}

	public static boolean isAIModuleInstalled ( short expansionModules ) {
		return (expansionModules & MODULE_AI) == MODULE_AI;
	}

	public static boolean isSalinityModuleInstalled ( short expansionModules ) {
		return (expansionModules & MODULE_SALINITY) == MODULE_SALINITY;
	}

	public static boolean isORPModuleInstalled ( short expansionModules ) {
		return (expansionModules & MODULE_ORP) == MODULE_ORP;
	}

	public static boolean isIOModuleInstalled ( short expansionModules ) {
		return (expansionModules & MODULE_IO) == MODULE_IO;
	}

	public static boolean isPHExpansionModuleInstalled ( short expansionModules ) {
		return (expansionModules & MODULE_PHEXPANSION) == MODULE_PHEXPANSION;
	}

	public static boolean isWaterLevelModuleInstalled ( short expansionModules ) {
		return (expansionModules & MODULE_WATERLEVEL) == MODULE_WATERLEVEL;
	}
	
	public static boolean isHumidityModuleInstalled ( short expansionModules1 ) {
		return (expansionModules1 & MODULE_HUMIDITY) == MODULE_HUMIDITY;
	}
	
	public static boolean isDCPumpControlModuleInstalled ( short expansionModules1 ) {
		return (expansionModules1 & MODULE_DCPUMP) == MODULE_DCPUMP;
	}
	
	public static boolean isLeakDetectorModuleInstalled ( short expansionModules1 ) {
		return (expansionModules1 & MODULE_LEAKDETECTOR) == MODULE_LEAKDETECTOR;
	}

	public short getRelayExpansionModules ( ) {
		return relayExpansionModules;
	}

	public void setRelayExpansionModules ( short rem ) {
		relayExpansionModules = rem;
	}

	public static int getRelayExpansionModulesInstalled ( short rem ) {
		int qty = 0;
		for ( int i = 7; i >= 0; i-- ) {
			if ( (rem & (1 << i)) == (1 << i) ) {
				qty = i + 1;
				break;
			}
		}
		return qty;
	}

	public short getIOChannels ( ) {
		return ioChannels;
	}

	public void setIOChannels ( short ioChannels ) {
		this.ioChannels = ioChannels;
	}

	public String getIOChannelLabel ( short channel ) {
		return ioChannelsLabels[channel];
	}

	public void setIOChannelLabel ( short channel, String label ) {
		ioChannelsLabels[channel] = label;
	}

	public static boolean getIOChannel ( short ioChannels, byte channel ) {
		// channel is 0 based
		int v = (1 << channel);
		int w = (ioChannels & v);
		boolean f = w == v;
		return f;
	}
	
	public static boolean isPWMOverrideEnabled( short value, short override ) {
		// above 100% (max value), so the override is disabled
		if ( override > Globals.OVERRIDE_MAX_VALUE ) {
			return false;
		}
		return true;
	}
	
	public static short getPWMValueFromOverride ( short data, short override ) {
		short v;
		if ( isPWMOverrideEnabled(data, override) ) {
			v = data;
		} else {
			v = override;
		}
		Log.d("getPWMValueFromOverride", "data: " + data + " override: " + override + " return: " + v);
		return v;
	}
	
	public static String getPWMDisplayValue( short data, short override ) {
		String s = String.format(Locale.getDefault(), "%d%%", getPWMValueFromOverride(data, override));
		if ( isPWMOverrideEnabled(data, override) ) {
			s = "**" + s;
		}
		return s;
	}
}
