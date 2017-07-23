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

public class ControllerProbesVisibilityTable {

    /*
    Controller Probes Visibility Table

    Contains a list of the probes attached to the main controller that are shown in the app.
    This allows the probes that are unused to be hidden from the user.
    This database only contains the values and the app handles displaying or not displaying
    the specified probes.

    Probes include:
    T1, T2, T3 - temp sensors
    PH, PHe - ph probes
    Salinity
    Humidity
    ORP
    Water Levels
    ATO Ports
    PWM AP and DP ports

    Added in DB Version 13
     */


    private static final String TAG = ControllerProbesVisibilityTable.class.getSimpleName();

    // Database constants
    public static final String TABLE_NAME = "controller_probes_visibility";
    // columns
    public static final String COL_ID = "_id";
    public static final String COL_CONTROLLER_ID = "controller_id";
    public static final String COL_T1 = "t1";
    public static final String COL_T2 = "t2";
    public static final String COL_T3 = "t3";
    public static final String COL_PH = "ph";
    public static final String COL_AP = "ap";
    public static final String COL_DP = "dp";
    public static final String COL_ATOLOW = "atolow";
    public static final String COL_ATOHIGH = "atohigh";
    public static final String COL_SALINITY = "salinity";
    public static final String COL_ORP = "orp";
    public static final String COL_PHE = "phe";
    // water level labels, 0 is main/single, 1-4 are 4channel expansion
    public static final String COL_W0 = "w0";
    public static final String COL_W1 = "w1";
    public static final String COL_W2 = "w2";
    public static final String COL_W3 = "w3";
    public static final String COL_W4 = "w4";
    public static final String COL_HUMIDITY = "humidity";

    private static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME
            + " (" + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COL_CONTROLLER_ID + " INTEGER, "
            + COL_T1 + " INTEGER DEFAULT 1, "
            + COL_T2 + " INTEGER DEFAULT 1, "
            + COL_T3 + " INTEGER DEFAULT 1, "
            + COL_PH + " INTEGER DEFAULT 1, "
            + COL_AP + " INTEGER DEFAULT 1, "
            + COL_DP + " INTEGER DEFAULT 1, "
            + COL_ATOLOW + " INTEGER DEFAULT 1, "
            + COL_ATOHIGH + " INTEGER DEFAULT 1, "
            + COL_SALINITY + " INTEGER DEFAULT 0, "
            + COL_ORP + " INTEGER DEFAULT 0, "
            + COL_PHE + " INTEGER DEFAULT 0, "
            + COL_W0 + " INTEGER DEFAULT 0, "
            + COL_W1 + " INTEGER DEFAULT 0, "
            + COL_W2 + " INTEGER DEFAULT 0, "
            + COL_W3 + " INTEGER DEFAULT 0, "
            + COL_W4 + " INTEGER DEFAULT 0, "
            + COL_HUMIDITY + " INTEGER DEFAULT 0, "
            + "FOREIGN KEY (" + COL_CONTROLLER_ID + ") REFERENCES "
            + ControllersTable.TABLE_NAME + "(" + ControllersTable.COL_CONTROLLER_ID + ")"
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
