package info.curtbinder.reefangel.phone;

public class Globals {
	// Requests
	public static final String requestMemoryByte = "/mb";
	public static final String requestMemoryInt = "/mi";
	public static final String requestStatusOld = "/r99";
	public static final String requestStatus = "/sr";
	public static final String requestDateTime = "/d";
	public static final String requestVersion = "/v";
	public static final String requestFeedingMode = "/mf";
	public static final String requestWaterMode = "/mw";
	public static final String requestExitMode = "/bp";
	public static final String requestNone = "";
	
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
	
	// Error Codes for ControllerTask
	public static final int errorSendCmd = 10;
	public static final int errorSendCmdBadUrl = 11;
	public static final int errorSendCmdException = 12;
	public static final int errorSendCmdConnect = 13;
	public static final int errorSendCmdUnknownHost = 14;
	public static final int errorParseXml = 20;
	public static final int errorParseXmlParseConfig = 21;
	public static final int errorParseXmlIO = 22;
	public static final int errorParseXmlSAX = 23;
}
