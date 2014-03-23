/*
 * Copyright (c) 2011-13 by Curt Binder (http://curtbinder.info)
 *
 * This work is made available under the terms of the 
 * Creative Commons Attribution-NonCommercial-ShareAlike 3.0 Unported License
 * http://creativecommons.org/licenses/by-nc-sa/3.0/
 */

package info.curtbinder.reefangel.phone;

public final class Globals {
	public static final String PACKAGE = buildPackage();

    private static String buildPackage() {
        String p = Globals.class.getPackage().getName();
        if (BuildConfig.DEBUG) {
            p += ".debug";
        }
        return p;
    }

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
}
