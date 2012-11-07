package info.curtbinder.reefangel.service;

/*
 * Copyright (c) 2011-12 by Curt Binder (http://curtbinder.info)
 * 
 * This work is made available under the terms of the Creative Commons
 * Attribution-NonCommercial-ShareAlike 3.0 Unported License
 * http://creativecommons.org/licenses/by-nc-sa/3.0/
 */

public final class RequestCommands {
	// Request commands sent to the controller
	public static final String MemoryByte = "/mb";
	public static final String MemoryInt = "/mi";
	public static final String Status = "/r99";
	// public static final String Status = "/sr";
	public static final String DateTime = "/d";
	public static final String Version = "/v";
	public static final String FeedingMode = "/mf";
	public static final String WaterMode = "/mw";
	public static final String AtoClear = "/mt";
	public static final String OverheatClear = "/mo";
	public static final String ExitMode = "/bp";
	public static final String Relay = "/r";
	public static final String None = "";
	public static final String ReefAngel = "ra";
}
