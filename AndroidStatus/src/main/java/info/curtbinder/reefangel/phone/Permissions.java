package info.curtbinder.reefangel.phone;

/*
 * Copyright (c) 2011-12 by Curt Binder (http://curtbinder.info)
 *
 * This work is made available under the terms of the 
 * Creative Commons Attribution-NonCommercial-ShareAlike 3.0 Unported License
 * http://creativecommons.org/licenses/by-nc-sa/3.0/
 */

public final class Permissions {
    public static final String PERMISSION_BASE =
            "info.curtbinder.reefangel.permission";

    /*
     * QUERY_STATUS permission handles sending / receiving the following
     * messages
     *
     * QUERY_STATUS UPDATE_STATUS UPDATE_DISPLAY_DATA ERROR_MESSAGE
     */
    public static final String QUERY_STATUS = PERMISSION_BASE + ".QUERY_STATUS";
    /*
     * SEND_COMMAND permission handles sending / receiving the following
     * messages
     *
     * COMMAND_SEND LABEL_QUERY MEMORY_SEND TOGGLE_RELAY VERSION_QUERY
     *
     * COMMAND_RESPONSE LABEL_RESPONSE MEMORY_RESPONSE VERSION_RESPONSE
     */
    public static final String SEND_COMMAND = PERMISSION_BASE + ".SEND_COMMAND";
}
