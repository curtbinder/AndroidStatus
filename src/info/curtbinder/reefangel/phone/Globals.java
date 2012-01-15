package info.curtbinder.reefangel.phone;


public final class Globals {
	public static final String PACKAGE_BASE = "info.curtbinder.reefangel.phone";
	
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
	
	public static final int memoryReadOnly = -1;
	public static final int defaultPort = 9;
	
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
	public static final String xmlPWMExpansion = "PWME";
	public static final String xmlRelay = "R";
	public static final String xmlRelayMaskOn = "RON";
	public static final String xmlRelayMaskOff = "ROFF";
	public static final String xmlLogDate = "LOGDATE";
	public static final String xmlLabelTempBegin = "T";
	public static final String xmlLabelRelayBegin = "R";
	public static final String xmlLabelEnd = "N";
	public static final String xmlRelayExpansionModules = "REM";
	public static final String xmlExpansionModules = "EM";
	public static final String xmlAIWhite = "AIW";
	public static final String xmlAIBlue = "AIB";
	public static final String xmlAIRoyalBlue = "AIRB";
	public static final String xmlRFMode = "RFM";
	public static final String xmlRFSpeed = "RFS";
	public static final String xmlRFDuration = "RFD";
	public static final String xmlRFWhite = "RFW";
	public static final String xmlRFRoyalBlue = "RFRB";
	public static final String xmlRFRed = "RFR";
	public static final String xmlRFGreen = "RFG";
	public static final String xmlRFBlue = "RFB";
	public static final String xmlRFIntensity = "RFI";
}
