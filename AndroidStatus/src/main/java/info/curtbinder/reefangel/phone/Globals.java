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

package info.curtbinder.reefangel.phone;

public final class Globals {
    public static final String PACKAGE = buildPackage();
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
    //public static final int OVERRIDE_CHANNELS = 17;

    // calibrate locations
    public static final int CALIBRATE_PH = 0;
    public static final int CALIBRATE_SALINITY = 1;
    public static final int CALIBRATE_ORP = 2;
    public static final int CALIBRATE_PHE = 3;
    public static final int CALIBRATE_WATERLEVEL = 4;

    // Controller Indices
    public static final int T1_INDEX = 0;
    public static final int T2_INDEX = 1;
    public static final int T3_INDEX = 2;
    public static final int PH_INDEX = 3;
    public static final int DP_INDEX = 4;
    public static final int AP_INDEX = 5;
    public static final int ATOLO_INDEX = 6;
    public static final int ATOHI_INDEX = 7;
    public static final int SALINITY_INDEX = 8;
    public static final int ORP_INDEX = 9;
    public static final int PHE_INDEX = 10;
    public static final int WL_INDEX = 11;
    public static final int WL1_INDEX = 12;
    public static final int WL2_INDEX = 13;
    public static final int WL3_INDEX = 14;
    public static final int WL4_INDEX = 15;
    public static final int HUMIDITY_INDEX = 16;


    private static String buildPackage() {
        String p = Globals.class.getPackage().getName();
        if (BuildConfig.DEBUG) {
            p += ".debug";
        }
        return p;
    }
}
