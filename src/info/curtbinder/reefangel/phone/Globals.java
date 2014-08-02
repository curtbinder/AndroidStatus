/*
 * Copyright (c) 2011-13 by Curt Binder (http://curtbinder.info)
 *
 * This work is made available under the terms of the 
 * Creative Commons Attribution-NonCommercial-ShareAlike 3.0 Unported License
 * http://creativecommons.org/licenses/by-nc-sa/3.0/
 */

package info.curtbinder.reefangel.phone;

public final class Globals {
	public static final String PACKAGE = "info.curtbinder.reefangel.phone";
	public static final String PRE10_LOCATIONS = "PreLocations";

	public static final String loggingFile = "ra_log.txt";
	public static final int logReplace = 0;
	public static final int logAppend = 1;

	public static final int errorRetryNone = 0;
	
	public static final int memoryReadOnly = -1;
	public static final int defaultPort = 9;

	// profile updating
	public static final int profileAlways = 0;
	public static final int profileOnlyAway = 1;
	public static final int profileOnlyHome = 2;
	public static final int profileHome = 0;
	public static final int profileAway = 1;
	
	// notification conditions
	public static final int condGreaterThan = 0;
	public static final int condGreaterThanOrEqualTo = 1;
	public static final int condEqual = 2;
	public static final int condLessThan = 3;
	public static final int condLessThanOrEqualTo = 4;
	public static final int condNotEqual = 5;
	
	// notification parameters
	public static final int paramT1 = 0;
	public static final int paramT2 = 1;
	public static final int paramT3 = 2;
	public static final int paramPH = 3;
	public static final int paramPHExpansion = 4;
	public static final int paramDaylightPWM = 5;
	public static final int paramActinicPWM = 6;
	public static final int paramSalinity = 7;
	public static final int paramORP = 8;
	public static final int paramWaterLevel = 9;
	public static final int paramATOHigh = 10;
	public static final int paramATOLow = 11;
	public static final int paramPWMExp0 = 12;
	public static final int paramPWMExp1 = 13;
	public static final int paramPWMExp2 = 14;
	public static final int paramPWMExp3 = 15;
	public static final int paramPWMExp4 = 16;
	public static final int paramPWMExp5 = 17;
	public static final int paramAIWhite = 18;
	public static final int paramAIBlue = 19;
	public static final int paramAIRoyalBlue = 20;
	public static final int paramVortechMode = 21;
	public static final int paramVortechSpeed = 22;
	public static final int paramVortechDuration = 23;
	public static final int paramRadionWhite = 24;
	public static final int paramRadionRoyalBlue = 25;
	public static final int paramRadionRed = 26;
	public static final int paramRadionGreen = 27;
	public static final int paramRadionBlue = 28;
	public static final int paramRadionIntensity = 29;
	public static final int paramIOCh0 = 30;
	public static final int paramIOCh1 = 31;
	public static final int paramIOCh2 = 32;
	public static final int paramIOCh3 = 33;
	public static final int paramIOCh4 = 34;
	public static final int paramIOCh5 = 35;
	public static final int paramCustom0 = 36;
	public static final int paramCustom1 = 37;
	public static final int paramCustom2 = 38;
	public static final int paramCustom3 = 39;
	public static final int paramCustom4 = 40;
	public static final int paramCustom5 = 41;
	public static final int paramCustom6 = 42;
	public static final int paramCustom7 = 43;
	public static final int paramWaterLevel1 = 44;
	public static final int paramWaterLevel2 = 45;
	public static final int paramWaterLevel3 = 46;
	public static final int paramWaterLevel4 = 47;
	public static final int paramHumidity = 48;
	public static final int paramSCPWMExp0 = 49;
	public static final int paramSCPWMExp1 = 50;
	public static final int paramSCPWMExp2 = 51;
	public static final int paramSCPWMExp3 = 52;
	public static final int paramSCPWMExp4 = 53;
	public static final int paramSCPWMExp5 = 54;
	public static final int paramSCPWMExp6 = 55;
	public static final int paramSCPWMExp7 = 56;
	public static final int paramSCPWMExp8 = 57;
	public static final int paramSCPWMExp9 = 58;
	public static final int paramSCPWMExp10 = 59;
	public static final int paramSCPWMExp11 = 60;
	public static final int paramSCPWMExp12 = 61;
	public static final int paramSCPWMExp13 = 62;
	public static final int paramSCPWMExp14 = 63;
	public static final int paramSCPWMExp15 = 64;
	
	
	// override locations
	public static final int OVERRIDE_DISABLE = 255;
	public static final int OVERRIDE_MAX_VALUE = 100;
	public static final int OVERRIDE_DAYLIGHT = 0;
	public static final int OVERRIDE_ACTINIC = 1;
	public static final int OVERRIDE_CHANNEL0 = 2;
	public static final int OVERRIDE_CHANNEL1 = 3;
	public static final int OVERRIDE_CHANNEL2 = 4;
	public static final int OVERRIDE_CHANNEL3 = 5;
	public static final int OVERRIDE_CHANNEL4 = 6;
	public static final int OVERRIDE_CHANNEL5 = 7;
	public static final int OVERRIDE_AI_WHITE = 8;
	public static final int OVERRIDE_AI_ROYALBLUE = 9;
	public static final int OVERRIDE_AI_BLUE = 10;
	public static final int OVERRIDE_RF_WHITE = 11;
	public static final int OVERRIDE_RF_ROYALBLUE = 12;
	public static final int OVERRIDE_RF_RED = 13;
	public static final int OVERRIDE_RF_GREEN = 14;
	public static final int OVERRIDE_RF_BLUE = 15;
	public static final int OVERRIDE_RF_INTENSITY = 16;
	public static final int OVERRIDE_DAYLIGHT2 = 17;
	public static final int OVERRIDE_ACTINIC2 = 18;
	public static final int OVERRIDE_16CH_CHANNEL0 = 19;
	public static final int OVERRIDE_16CH_CHANNEL1 = 20;
	public static final int OVERRIDE_16CH_CHANNEL2 = 21;
	public static final int OVERRIDE_16CH_CHANNEL3 = 22;
	public static final int OVERRIDE_16CH_CHANNEL4 = 23;
	public static final int OVERRIDE_16CH_CHANNEL5 = 24;
	public static final int OVERRIDE_16CH_CHANNEL6 = 25;
	public static final int OVERRIDE_16CH_CHANNEL7 = 26;
	public static final int OVERRIDE_16CH_CHANNEL8 = 27;
	public static final int OVERRIDE_16CH_CHANNEL9 = 28;
	public static final int OVERRIDE_16CH_CHANNEL10 = 29;
	public static final int OVERRIDE_16CH_CHANNEL11 = 30;
	public static final int OVERRIDE_16CH_CHANNEL12 = 31;
	public static final int OVERRIDE_16CH_CHANNEL13 = 32;
	public static final int OVERRIDE_16CH_CHANNEL14 = 33;
	public static final int OVERRIDE_16CH_CHANNEL15 = 34;
	//public static final int OVERRIDE_CHANNELS = 35;
	
	// calibrate locations
	public static final int CALIBRATE_PH = 0;
	public static final int CALIBRATE_SALINITY = 1;
	public static final int CALIBRATE_ORP = 2;
	public static final int CALIBRATE_PHE = 3;
	public static final int CALIBRATE_WATERLEVEL = 4;
}
