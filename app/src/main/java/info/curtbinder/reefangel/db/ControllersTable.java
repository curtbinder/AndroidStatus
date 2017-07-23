/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2017 Curt Binder
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

package info.curtbinder.reefangel.db;

import android.database.sqlite.SQLiteDatabase;

/**
 * Created by binder on 1/15/17.
 */

public class ControllersTable {

    /*

    Controllers Table

    This table contains a list of all the controllers that the app is communicating with.
    This table stores tha main information related to the controller.
    Such as:
        host/ip, port, type of device (controller or portal)
        portal id associated with it, quantity of expansion relays, etc

    Added in DB Version 13
     */


    private static final String TAG = ControllersTable.class.getSimpleName();

    // Database constants
    public static final String TABLE_NAME = "controllers";
    // columns
    public static final String COL_CONTROLLER_ID = "_id";
    public static final String COL_NAME = "name";
    public static final String COL_HOST = "host";
    public static final String COL_PORT = "port";
    // device authentication settings used with v1.1.1 libraries and later
    public static final String COL_USERNAME = "username";
    public static final String COL_PASSWORD = "password";
    // Controller or Portal - int value
    public static final String COL_DEVICE_TYPE = "device_type";
    public static final String COL_PORTAL_ID = "portal_id";
    public static final String COL_PORTAL_URL = "portal_url";
    // integer value for quantity
    public static final String COL_RELAY_EXPANSION_QTY = "relay_expansion_qty";
    // boolean to auto update this controllers values
    public static final String COL_AUTOUPDATE = "autoupdate";
    // boolean to use the pre v1.0 memory locations
    public static final String COL_PRE10_MEMORY = "pre10_memory";
    // boolean to use the v0.8.5.x library expansion code
    public static final String COL_OLD_EXPANSION = "old_expansion";

    private static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME
            + " (" + COL_CONTROLLER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COL_NAME + " TEXT, "
            + COL_HOST + " TEXT, "
            + COL_PORT + " INTEGER DEFAULT 2000, "
            + COL_USERNAME + " TEXT, "
            + COL_PASSWORD + " TEXT, "
            + COL_DEVICE_TYPE + " INTEGER DEFAULT 0, "
            + COL_PORTAL_ID + " TEXT, "
            + COL_PORTAL_URL + " TEXT, "
            + COL_RELAY_EXPANSION_QTY + " INTEGER DEFAULT 0, "
            + COL_AUTOUPDATE + " INTEGER DEFAULT 1, "
            + COL_PRE10_MEMORY + " INTEGER DEFAULT 0, "
            + COL_OLD_EXPANSION + " INTEGER DEFAULT 0"
            + ");";
    private static final String DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;

    public static void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    public static void onUpgrade(
            SQLiteDatabase db,
            int oldVersion,
            int newVersion) {
        int curVer = oldVersion;
        while (curVer < newVersion) {
            curVer++;
            switch (curVer) {
                default:
                    break;
            }
        }
    }

    private static void dropTable(SQLiteDatabase db) {
        db.execSQL(DROP_TABLE);
    }

    public static void onDowngrade(
            SQLiteDatabase db,
            int oldVersion,
            int newVersion) {
        int curVer = oldVersion;
        while (curVer > newVersion) {
            curVer--;
            switch (curVer) {
                default:
                    break;
                case 12:
                    // drop the table if the downgraded version is less than 13
                    dropTable(db);
                    break;
            }
        }
    }

}
