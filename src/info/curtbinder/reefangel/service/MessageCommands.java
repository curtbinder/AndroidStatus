/*
 * Copyright (c) 2011-2013 by Curt Binder (http://curtbinder.info)
 * 
 * This work is made available under the terms of the Creative Commons
 * Attribution-NonCommercial-ShareAlike 3.0 Unported License
 * http://creativecommons.org/licenses/by-nc-sa/3.0/
 */

package info.curtbinder.reefangel.service;

public final class MessageCommands {
	public static final String PACKAGE_BASE =
			"info.curtbinder.reefangel.service";

	public static final String AUTO_UPDATE_PROFILE_INT =
			PACKAGE_BASE + ".AUTO_UPDATE_PROFILE_INT";
	public static final String COMMAND_RESPONSE_INTENT =
			PACKAGE_BASE + ".COMMAND_RESPOSNE";
	public static final String COMMAND_RESPONSE_STRING =
			"COMMAND_RESPONSE_STRING";
	public static final String COMMAND_SEND_INTENT = PACKAGE_BASE
														+ ".COMMAND_SEND";
	public static final String COMMAND_SEND_STRING = "COMMAND_SEND_STRING";

	public static final String DATE_QUERY_INTENT = PACKAGE_BASE + ".DATE_QUERY";
	public static final String DATE_QUERY_RESPONSE_INTENT =
			PACKAGE_BASE + ".DATE_QUERY_RESPONSE";
	public static final String DATE_QUERY_RESPONSE_STRING =
			"DATE_QUERY_RESPONSE_STRING";

	public static final String DATE_SEND_INTENT = PACKAGE_BASE + ".DATE_SEND";
	public static final String DATE_SEND_STRING = "DATE_SEND_STRING";
	public static final String DATE_SEND_RESPONSE_INTENT =
			PACKAGE_BASE + ".DATE_SEND_RESPONSE";
	public static final String DATE_SEND_RESPONSE_STRING =
			"DATE_SEND_RESPONSE_STRING";

	public static final String ERROR_MESSAGE_INTENT = PACKAGE_BASE
														+ ".ERROR_MESSAGE";
	public static final String ERROR_MESSAGE_STRING = "ERROR_MESSAGE_STRING";

	public static final String LABEL_QUERY_INTENT = PACKAGE_BASE
													+ ".LABEL_QUERY";
	public static final String LABEL_RESPONSE_INTENT = PACKAGE_BASE
														+ ".LABEL_RESPONSE";

	public static final String MEMORY_SEND_INTENT = PACKAGE_BASE
													+ ".MEMORY_SEND";
	public static final String MEMORY_SEND_TYPE_STRING =
			"MEMORY_SEND_TYPE_STRING";
	public static final String MEMORY_SEND_LOCATION_INT =
			"MEMORY_SEND_LOCATION_INT";
	public static final String MEMORY_SEND_VALUE_INT = "MEMORY_SEND_VALUE_INT";

	public static final String MEMORY_RESPONSE_INTENT = PACKAGE_BASE
														+ ".MEMORY_RESPONSE";
	public static final String MEMORY_RESPONSE_STRING =
			"MEMORY_RESPONSE_STRING";
	public static final String MEMORY_RESPONSE_WRITE_BOOLEAN =
			"MEMORY_RESPONSE_WRITE_BOOLEAN";

	public static final String NOTIFICATION_INTENT = 
			PACKAGE_BASE + ".NOTIFICATION";
	public static final String NOTIFICATION_CLEAR_INTENT =
			PACKAGE_BASE + ".NOTIFICATION_CLEAR";
	public static final String NOTIFICATION_ERROR_INTENT =
			PACKAGE_BASE + ".NOTIFICATION_ERROR";
	public static final String NOTIFICATION_LAUNCH_INTENT =
			PACKAGE_BASE + ".NOTIFICATION_LAUNCH";
	
	public static final String OVERRIDE_SEND_INTENT = PACKAGE_BASE + ".OVERRIDE_SEND";
	public static final String OVERRIDE_SEND_LOCATION_INT = "OVERRIDE_SEND_LOCATION_INT";
	public static final String OVERRIDE_SEND_VALUE_INT = "OVERRIDE_SEND_VALUE_INT";
	public static final String OVERRIDE_RESPONSE_INTENT = PACKAGE_BASE + ".OVERRIDE_RESPONSE";
	public static final String OVERRIDE_RESPONSE_STRING = "OVERRIDE_RESPONSE_STRING";

	public static final String QUERY_STATUS_INTENT = PACKAGE_BASE
														+ ".QUERY_STATUS";

	public static final String TOGGLE_RELAY_INTENT = PACKAGE_BASE
														+ ".TOGGLE_RELAY";
	public static final String TOGGLE_RELAY_PORT_INT = "TOGGLE_RELAY_PORT_INT";
	public static final String TOGGLE_RELAY_MODE_INT = "TOGGLE_RELAY_MODE_INT";

	public static final String UPDATE_DISPLAY_DATA_INTENT =
			PACKAGE_BASE + ".UPDATE_DISPLAY_DATA";

	public static final String UPDATE_STATUS_INTENT = PACKAGE_BASE
														+ ".UPDATE_STATUS";
	public static final String UPDATE_STATUS_ID = "UPDATE_STATUS_ID";
	public static final String UPDATE_STATUS_STRING = "UPDATE_STATUS_STRING";

	public static final String VERSION_QUERY_INTENT = PACKAGE_BASE
														+ ".VERSION_QUERY";
	public static final String VERSION_RESPONSE_INTENT =
			PACKAGE_BASE + ".VERSION_RESPONSE";
	public static final String VERSION_RESPONSE_STRING =
			"VERSION_RESPONSE_STRING";
	public static final String VORTECH_UPDATE_INTENT = PACKAGE_BASE
														+ ".VORTECH_UPDATE";
	public static final String VORTECH_UPDATE_TYPE = "VORTECH_UPDATE_TYPE";

}
