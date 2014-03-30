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

package info.curtbinder.reefangel.service;

import info.curtbinder.reefangel.phone.BuildConfig;

public final class MessageCommands {
	public static final String PACKAGE_BASE = buildBase();

    private static String buildBase() {
        String s = MessageCommands.class.getPackage().getName();
        if (BuildConfig.DEBUG) {
            s += ".debug";
        }
        return s;
    }

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
