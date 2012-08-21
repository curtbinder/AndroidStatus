package info.curtbinder.reefangel.phone;

/*
 * Copyright (c) 2011-12 by Curt Binder (http://curtbinder.info)
 *
 * This work is made available under the terms of the 
 * Creative Commons Attribution-NonCommercial-ShareAlike 3.0 Unported License
 * http://creativecommons.org/licenses/by-nc-sa/3.0/
 */


public final class Globals {	
	// Requests
	public static final String requestMemoryByte = "/mb";
	public static final String requestMemoryInt = "/mi";
	public static final String requestStatus = "/r99";
	//public static final String requestStatus = "/sr";
	public static final String requestDateTime = "/d";
	public static final String requestVersion = "/v";
	public static final String requestFeedingMode = "/mf";
	public static final String requestWaterMode = "/mw";
	public static final String requestAtoClear = "/mt";
	public static final String requestOverheatClear = "/mo";
	public static final String requestExitMode = "/bp";
	public static final String requestRelay = "/r";
	public static final String requestNone = "";
	public static final String requestReefAngel = "ra";
	
	public static final int memoryReadOnly = -1;
	public static final int defaultPort = 9;
	
	// profile updating
	public static final int profileAlways = 0;
	public static final int profileOnlyAway = 1;
	public static final int profileOnlyHome = 2;
	public static final int profileHome = 0;
	public static final int profileAway = 1;
}
