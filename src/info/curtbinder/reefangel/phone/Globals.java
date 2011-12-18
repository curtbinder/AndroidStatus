package info.curtbinder.reefangel.phone;

import android.net.Uri;

public class Globals {
	// Database constants
	public static final String PTABLE_MAX_COUNT = "30";
	public static final String PTABLE_NAME = "params";
	// columns in params table
	public static final String PCOL_ID = "_id";
	public static final String PCOL_T1 = "t1";
	public static final String PCOL_T2 = "t2";
	public static final String PCOL_T3 = "t3";
	public static final String PCOL_PH = "ph";
	public static final String PCOL_DP = "dp";
	public static final String PCOL_AP = "ap";
	public static final String PCOL_ATOHI = "atohi";
	public static final String PCOL_ATOLO = "atolow";
	public static final String PCOL_SAL = "sal";
	public static final String PCOL_LOGDATE = "logdate";
	public static final String PCOL_RDATA = "rdata";
	public static final String PCOL_RONMASK = "ronmask";
	public static final String PCOL_ROFFMASK = "roffmask";
	public static final String PCOL_R1DATA = "r1data";
	public static final String PCOL_R1ONMASK = "r1onmask";
	public static final String PCOL_R1OFFMASK = "r1offmask";
	public static final String PCOL_R2DATA = "r2data";
	public static final String PCOL_R2ONMASK = "r2onmask";
	public static final String PCOL_R2OFFMASK = "r2offmask";
	public static final String PCOL_R3DATA = "r3data";
	public static final String PCOL_R3ONMASK = "r3onmask";
	public static final String PCOL_R3OFFMASK = "r3offmask";
	public static final String PCOL_R4DATA = "r4data";
	public static final String PCOL_R4ONMASK = "r4onmask";
	public static final String PCOL_R4OFFMASK = "r4offmask";
	public static final String PCOL_R5DATA = "r5data";
	public static final String PCOL_R5ONMASK = "r5onmask";
	public static final String PCOL_R5OFFMASK = "r5offmask";
	public static final String PCOL_R6DATA = "r6data";
	public static final String PCOL_R6ONMASK = "r6onmask";
	public static final String PCOL_R6OFFMASK = "r6offmask";
	public static final String PCOL_R7DATA = "r7data";
	public static final String PCOL_R7ONMASK = "r7onmask";
	public static final String PCOL_R7OFFMASK = "r7offmask";
	public static final String PCOL_R8DATA = "r8data";
	public static final String PCOL_R8ONMASK = "r8onmask";
	public static final String PCOL_R8OFFMASK = "r8offmask";

	// Content Provider
	public static final String AUTHORITY = "info.curtbinder.reefangel.phone";
	public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + 
			"/" + PTABLE_NAME);
	
	// Requests
	public static final String requestMemoryByte = "/mb";
	public static final String requestMemoryInt = "/mi";
	public static final String requestStatus = "/r99";
	//public static final String requestStatus = "/sr";
	public static final String requestDateTime = "/d";
	public static final String requestVersion = "/v";
	public static final String requestFeedingMode = "/mf";
	public static final String requestWaterMode = "/mw";
	public static final String requestExitMode = "/bp";
	public static final String requestRelay = "/r";
	public static final String requestNone = "";
	public static final String requestReefAngel = "ra";
	
	// XML tags
	public static final String xmlStatus = "RA";
	public static final String xmlMemory = "MEM";
	public static final String xmlMemorySingle = "M";
	public static final String xmlDateTime = "D";
	public static final String xmlVersion = "V";
	public static final String xmlMode = "MODE";
	public static final String xmlT1 = "T1";
	public static final String xmlT2 = "T2";
	public static final String xmlT3 = "T3";
	public static final String xmlPH = "PH";
	public static final String xmlATOLow = "ATOLOW";
	public static final String xmlATOHigh = "ATOHIGH";
	public static final String xmlSalinity = "SAL";
	public static final String xmlPWMDaylight = "PWMD";
	public static final String xmlPWMActinic = "PWMA";
	public static final String xmlRelay = "R";
	public static final String xmlRelayMaskOn = "RON";
	public static final String xmlRelayMaskOff = "ROFF";
	public static final String xmlLogDate = "LOGDATE";
}
