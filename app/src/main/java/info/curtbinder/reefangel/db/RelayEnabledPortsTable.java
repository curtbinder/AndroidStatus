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

public class RelayEnabledPortsTable {

    /*
    Relay Box Enabled Ports table

    Contains a list of the relay box ports that are enabled / disabled.
    Disabled ports cannot have their value changed via the app, only displayed.
    The app limits the control based on the values stored in the database.

    Added in DB Version 13
     */


    private static final String TAG = RelayEnabledPortsTable.class.getSimpleName();

    // Database constants
    public static final String TABLE_NAME = "relay_enabled_ports";
    // columns
    public static final String COL_ID = "_id";
    public static final String COL_CONTROLLER_ID = "controller_id";
    public static final String COL_MAIN_PORT1 = "main_port1";
    public static final String COL_MAIN_PORT2 = "main_port2";
    public static final String COL_MAIN_PORT3 = "main_port3";
    public static final String COL_MAIN_PORT4 = "main_port4";
    public static final String COL_MAIN_PORT5 = "main_port5";
    public static final String COL_MAIN_PORT6 = "main_port6";
    public static final String COL_MAIN_PORT7 = "main_port7";
    public static final String COL_MAIN_PORT8 = "main_port8";
    public static final String COL_EXP1_PORT1 = "exp1_port1";
    public static final String COL_EXP1_PORT2 = "exp1_port2";
    public static final String COL_EXP1_PORT3 = "exp1_port3";
    public static final String COL_EXP1_PORT4 = "exp1_port4";
    public static final String COL_EXP1_PORT5 = "exp1_port5";
    public static final String COL_EXP1_PORT6 = "exp1_port6";
    public static final String COL_EXP1_PORT7 = "exp1_port7";
    public static final String COL_EXP1_PORT8 = "exp1_port8";
    public static final String COL_EXP2_PORT1 = "exp2_port1";
    public static final String COL_EXP2_PORT2 = "exp2_port2";
    public static final String COL_EXP2_PORT3 = "exp2_port3";
    public static final String COL_EXP2_PORT4 = "exp2_port4";
    public static final String COL_EXP2_PORT5 = "exp2_port5";
    public static final String COL_EXP2_PORT6 = "exp2_port6";
    public static final String COL_EXP2_PORT7 = "exp2_port7";
    public static final String COL_EXP2_PORT8 = "exp2_port8";
    public static final String COL_EXP3_PORT1 = "exp3_port1";
    public static final String COL_EXP3_PORT2 = "exp3_port2";
    public static final String COL_EXP3_PORT3 = "exp3_port3";
    public static final String COL_EXP3_PORT4 = "exp3_port4";
    public static final String COL_EXP3_PORT5 = "exp3_port5";
    public static final String COL_EXP3_PORT6 = "exp3_port6";
    public static final String COL_EXP3_PORT7 = "exp3_port7";
    public static final String COL_EXP3_PORT8 = "exp3_port8";
    public static final String COL_EXP4_PORT1 = "exp4_port1";
    public static final String COL_EXP4_PORT2 = "exp4_port2";
    public static final String COL_EXP4_PORT3 = "exp4_port3";
    public static final String COL_EXP4_PORT4 = "exp4_port4";
    public static final String COL_EXP4_PORT5 = "exp4_port5";
    public static final String COL_EXP4_PORT6 = "exp4_port6";
    public static final String COL_EXP4_PORT7 = "exp4_port7";
    public static final String COL_EXP4_PORT8 = "exp4_port8";
    public static final String COL_EXP5_PORT1 = "exp5_port1";
    public static final String COL_EXP5_PORT2 = "exp5_port2";
    public static final String COL_EXP5_PORT3 = "exp5_port3";
    public static final String COL_EXP5_PORT4 = "exp5_port4";
    public static final String COL_EXP5_PORT5 = "exp5_port5";
    public static final String COL_EXP5_PORT6 = "exp5_port6";
    public static final String COL_EXP5_PORT7 = "exp5_port7";
    public static final String COL_EXP5_PORT8 = "exp5_port8";
    public static final String COL_EXP6_PORT1 = "exp6_port1";
    public static final String COL_EXP6_PORT2 = "exp6_port2";
    public static final String COL_EXP6_PORT3 = "exp6_port3";
    public static final String COL_EXP6_PORT4 = "exp6_port4";
    public static final String COL_EXP6_PORT5 = "exp6_port5";
    public static final String COL_EXP6_PORT6 = "exp6_port6";
    public static final String COL_EXP6_PORT7 = "exp6_port7";
    public static final String COL_EXP6_PORT8 = "exp6_port8";
    public static final String COL_EXP7_PORT1 = "exp7_port1";
    public static final String COL_EXP7_PORT2 = "exp7_port2";
    public static final String COL_EXP7_PORT3 = "exp7_port3";
    public static final String COL_EXP7_PORT4 = "exp7_port4";
    public static final String COL_EXP7_PORT5 = "exp7_port5";
    public static final String COL_EXP7_PORT6 = "exp7_port6";
    public static final String COL_EXP7_PORT7 = "exp7_port7";
    public static final String COL_EXP7_PORT8 = "exp7_port8";
    public static final String COL_EXP8_PORT1 = "exp8_port1";
    public static final String COL_EXP8_PORT2 = "exp8_port2";
    public static final String COL_EXP8_PORT3 = "exp8_port3";
    public static final String COL_EXP8_PORT4 = "exp8_port4";
    public static final String COL_EXP8_PORT5 = "exp8_port5";
    public static final String COL_EXP8_PORT6 = "exp8_port6";
    public static final String COL_EXP8_PORT7 = "exp8_port7";
    public static final String COL_EXP8_PORT8 = "exp8_port8";

    private static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME
            + " (" + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COL_CONTROLLER_ID + " INTEGER, "
            + COL_MAIN_PORT1 + " INTEGER DEFAULT 1, "
            + COL_MAIN_PORT2 + " INTEGER DEFAULT 1, "
            + COL_MAIN_PORT3 + " INTEGER DEFAULT 1, "
            + COL_MAIN_PORT4 + " INTEGER DEFAULT 1, "
            + COL_MAIN_PORT5 + " INTEGER DEFAULT 1, "
            + COL_MAIN_PORT6 + " INTEGER DEFAULT 1, "
            + COL_MAIN_PORT7 + " INTEGER DEFAULT 1, "
            + COL_MAIN_PORT8 + " INTEGER DEFAULT 1, "
            + COL_EXP1_PORT1 + " INTEGER DEFAULT 1, "
            + COL_EXP1_PORT2 + " INTEGER DEFAULT 1, "
            + COL_EXP1_PORT3 + " INTEGER DEFAULT 1, "
            + COL_EXP1_PORT4 + " INTEGER DEFAULT 1, "
            + COL_EXP1_PORT5 + " INTEGER DEFAULT 1, "
            + COL_EXP1_PORT6 + " INTEGER DEFAULT 1, "
            + COL_EXP1_PORT7 + " INTEGER DEFAULT 1, "
            + COL_EXP1_PORT8 + " INTEGER DEFAULT 1, "
            + COL_EXP2_PORT1 + " INTEGER DEFAULT 1, "
            + COL_EXP2_PORT2 + " INTEGER DEFAULT 1, "
            + COL_EXP2_PORT3 + " INTEGER DEFAULT 1, "
            + COL_EXP2_PORT4 + " INTEGER DEFAULT 1, "
            + COL_EXP2_PORT5 + " INTEGER DEFAULT 1, "
            + COL_EXP2_PORT6 + " INTEGER DEFAULT 1, "
            + COL_EXP2_PORT7 + " INTEGER DEFAULT 1, "
            + COL_EXP2_PORT8 + " INTEGER DEFAULT 1, "
            + COL_EXP3_PORT1 + " INTEGER DEFAULT 1, "
            + COL_EXP3_PORT2 + " INTEGER DEFAULT 1, "
            + COL_EXP3_PORT3 + " INTEGER DEFAULT 1, "
            + COL_EXP3_PORT4 + " INTEGER DEFAULT 1, "
            + COL_EXP3_PORT5 + " INTEGER DEFAULT 1, "
            + COL_EXP3_PORT6 + " INTEGER DEFAULT 1, "
            + COL_EXP3_PORT7 + " INTEGER DEFAULT 1, "
            + COL_EXP3_PORT8 + " INTEGER DEFAULT 1, "
            + COL_EXP4_PORT1 + " INTEGER DEFAULT 1, "
            + COL_EXP4_PORT2 + " INTEGER DEFAULT 1, "
            + COL_EXP4_PORT3 + " INTEGER DEFAULT 1, "
            + COL_EXP4_PORT4 + " INTEGER DEFAULT 1, "
            + COL_EXP4_PORT5 + " INTEGER DEFAULT 1, "
            + COL_EXP4_PORT6 + " INTEGER DEFAULT 1, "
            + COL_EXP4_PORT7 + " INTEGER DEFAULT 1, "
            + COL_EXP4_PORT8 + " INTEGER DEFAULT 1, "
            + COL_EXP5_PORT1 + " INTEGER DEFAULT 1, "
            + COL_EXP5_PORT2 + " INTEGER DEFAULT 1, "
            + COL_EXP5_PORT3 + " INTEGER DEFAULT 1, "
            + COL_EXP5_PORT4 + " INTEGER DEFAULT 1, "
            + COL_EXP5_PORT5 + " INTEGER DEFAULT 1, "
            + COL_EXP5_PORT6 + " INTEGER DEFAULT 1, "
            + COL_EXP5_PORT7 + " INTEGER DEFAULT 1, "
            + COL_EXP5_PORT8 + " INTEGER DEFAULT 1, "
            + COL_EXP6_PORT1 + " INTEGER DEFAULT 1, "
            + COL_EXP6_PORT2 + " INTEGER DEFAULT 1, "
            + COL_EXP6_PORT3 + " INTEGER DEFAULT 1, "
            + COL_EXP6_PORT4 + " INTEGER DEFAULT 1, "
            + COL_EXP6_PORT5 + " INTEGER DEFAULT 1, "
            + COL_EXP6_PORT6 + " INTEGER DEFAULT 1, "
            + COL_EXP6_PORT7 + " INTEGER DEFAULT 1, "
            + COL_EXP6_PORT8 + " INTEGER DEFAULT 1, "
            + COL_EXP7_PORT1 + " INTEGER DEFAULT 1, "
            + COL_EXP7_PORT2 + " INTEGER DEFAULT 1, "
            + COL_EXP7_PORT3 + " INTEGER DEFAULT 1, "
            + COL_EXP7_PORT4 + " INTEGER DEFAULT 1, "
            + COL_EXP7_PORT5 + " INTEGER DEFAULT 1, "
            + COL_EXP7_PORT6 + " INTEGER DEFAULT 1, "
            + COL_EXP7_PORT7 + " INTEGER DEFAULT 1, "
            + COL_EXP7_PORT8 + " INTEGER DEFAULT 1, "
            + COL_EXP8_PORT1 + " INTEGER DEFAULT 1, "
            + COL_EXP8_PORT2 + " INTEGER DEFAULT 1, "
            + COL_EXP8_PORT3 + " INTEGER DEFAULT 1, "
            + COL_EXP8_PORT4 + " INTEGER DEFAULT 1, "
            + COL_EXP8_PORT5 + " INTEGER DEFAULT 1, "
            + COL_EXP8_PORT6 + " INTEGER DEFAULT 1, "
            + COL_EXP8_PORT7 + " INTEGER DEFAULT 1, "
            + COL_EXP8_PORT8 + " INTEGER DEFAULT 1, "
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
