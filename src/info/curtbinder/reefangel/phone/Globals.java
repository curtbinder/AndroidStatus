package info.curtbinder.reefangel.phone;

/*
 * Copyright (c) 2011-12 by Curt Binder (http://curtbinder.info)
 *
 * This work is made available under the terms of the 
 * Creative Commons Attribution-NonCommercial-ShareAlike 3.0 Unported License
 * http://creativecommons.org/licenses/by-nc-sa/3.0/
 */


public final class Globals {
	public static final String loggingFile = "ra_log.txt";
	public static final int logReplace = 0;
	public static final int logAppend = 1;
	
	public static final int memoryReadOnly = -1;
	public static final int defaultPort = 9;
	
	// profile updating
	public static final int profileAlways = 0;
	public static final int profileOnlyAway = 1;
	public static final int profileOnlyHome = 2;
	public static final int profileHome = 0;
	public static final int profileAway = 1;
}
