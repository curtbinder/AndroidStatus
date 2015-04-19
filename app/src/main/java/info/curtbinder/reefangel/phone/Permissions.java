/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2012 Curt Binder
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
