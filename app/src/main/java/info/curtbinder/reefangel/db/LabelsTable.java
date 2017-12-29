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

public class LabelsTable {

    /*
    This table contains all the labels associated with each controller.
    Prior to using this table, the labels were all stored in the Settings

    Added in DB Version 20
     */


    private static final String TAG = LabelsTable.class.getSimpleName();

    // Database constants
    public static final String TABLE_NAME = "labels";
    // columns
    public static final String COL_ID = "_id";
    public static final String COL_CONTROLLER_ID = "controller_id";
    public static final String COL_T1 = "t1";
    public static final String COL_T2 = "t2";
    public static final String COL_T3 = "t3";
    public static final String COL_PH = "ph";
    public static final String COL_AP = "ap";
    public static final String COL_DP = "dp";
    public static final String COL_AP2 = "ap2";
    public static final String COL_DP2 = "dp2";
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
    public static final String COL_PAR = "par";
    // pwm expansion
    public static final String COL_PWME0 = "pwme0";
    public static final String COL_PWME1 = "pwme1";
    public static final String COL_PWME2 = "pwme2";
    public static final String COL_PWME3 = "pwme3";
    public static final String COL_PWME4 = "pwme4";
    public static final String COL_PWME5 = "pwme5";
    // 16 channel scpwm expansion
    public static final String COL_SCPWME0 = "scpwme0";
    public static final String COL_SCPWME1 = "scpwme1";
    public static final String COL_SCPWME2 = "scpwme2";
    public static final String COL_SCPWME3 = "scpwme3";
    public static final String COL_SCPWME4 = "scpwme4";
    public static final String COL_SCPWME5 = "scpwme5";
    public static final String COL_SCPWME6 = "scpwme6";
    public static final String COL_SCPWME7 = "scpwme7";
    public static final String COL_SCPWME8 = "scpwme8";
    public static final String COL_SCPWME9 = "scpwme9";
    public static final String COL_SCPWME10 = "scpwme10";
    public static final String COL_SCPWME11 = "scpwme11";
    public static final String COL_SCPWME12 = "scpwme12";
    public static final String COL_SCPWME13 = "scpwme13";
    public static final String COL_SCPWME14 = "scpwme14";
    public static final String COL_SCPWME15 = "scpwme15";
    // i/o expansion
    public static final String COL_IO0 = "io0";
    public static final String COL_IO1 = "io1";
    public static final String COL_IO2 = "io2";
    public static final String COL_IO3 = "io3";
    public static final String COL_IO4 = "io4";
    public static final String COL_IO5 = "io5";
    // custom variables
    public static final String COL_CVAR0 = "cvar0";
    public static final String COL_CVAR1 = "cvar1";
    public static final String COL_CVAR2 = "cvar2";
    public static final String COL_CVAR3 = "cvar3";
    public static final String COL_CVAR4 = "cvar4";
    public static final String COL_CVAR5 = "cvar5";
    public static final String COL_CVAR6 = "cvar6";
    public static final String COL_CVAR7 = "cvar7";
    // main relay
    public static final String COL_MAIN_PORT1 = "main_port1";
    public static final String COL_MAIN_PORT2 = "main_port2";
    public static final String COL_MAIN_PORT3 = "main_port3";
    public static final String COL_MAIN_PORT4 = "main_port4";
    public static final String COL_MAIN_PORT5 = "main_port5";
    public static final String COL_MAIN_PORT6 = "main_port6";
    public static final String COL_MAIN_PORT7 = "main_port7";
    public static final String COL_MAIN_PORT8 = "main_port8";
    // expansion relays, 1-8
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
            + COL_T1 + " TEXT, "
            + COL_T2 + " TEXT, "
            + COL_T3 + " TEXT, "
            + COL_PH + " TEXT, "
            + COL_AP + " TEXT, "
            + COL_DP + " TEXT, "
            + COL_AP2 + " TEXT, "
            + COL_DP2 + " TEXT, "
            + COL_ATOLOW + " TEXT, "
            + COL_ATOHIGH + " TEXT, "
            + COL_SALINITY + " TEXT, "
            + COL_ORP + " TEXT, "
            + COL_PHE + " TEXT, "
            + COL_W0 + " TEXT, "
            + COL_W1 + " TEXT, "
            + COL_W2 + " TEXT, "
            + COL_W3 + " TEXT, "
            + COL_W4 + " TEXT, "
            + COL_HUMIDITY + " TEXT, "
            + COL_PAR + " TEXT, "
            + COL_PWME0 + " TEXT, "
            + COL_PWME1 + " TEXT, "
            + COL_PWME2 + " TEXT, "
            + COL_PWME3 + " TEXT, "
            + COL_PWME4 + " TEXT, "
            + COL_PWME5 + " TEXT, "
            + COL_SCPWME0 + " TEXT, "
            + COL_SCPWME1 + " TEXT, "
            + COL_SCPWME2 + " TEXT, "
            + COL_SCPWME3 + " TEXT, "
            + COL_SCPWME4 + " TEXT, "
            + COL_SCPWME5 + " TEXT, "
            + COL_SCPWME6 + " TEXT, "
            + COL_SCPWME7 + " TEXT, "
            + COL_SCPWME8 + " TEXT, "
            + COL_SCPWME9 + " TEXT, "
            + COL_SCPWME10 + " TEXT, "
            + COL_SCPWME11 + " TEXT, "
            + COL_SCPWME12 + " TEXT, "
            + COL_SCPWME13 + " TEXT, "
            + COL_SCPWME14 + " TEXT, "
            + COL_SCPWME15 + " TEXT, "
            + COL_IO0 + " TEXT, "
            + COL_IO1 + " TEXT, "
            + COL_IO2 + " TEXT, "
            + COL_IO3 + " TEXT, "
            + COL_IO4 + " TEXT, "
            + COL_IO5 + " TEXT, "
            + COL_CVAR0 + " TEXT, "
            + COL_CVAR1 + " TEXT, "
            + COL_CVAR2 + " TEXT, "
            + COL_CVAR3 + " TEXT, "
            + COL_CVAR4 + " TEXT, "
            + COL_CVAR5 + " TEXT, "
            + COL_CVAR6 + " TEXT, "
            + COL_CVAR7 + " TEXT, "
            + COL_MAIN_PORT1 + " TEXT, "
            + COL_MAIN_PORT2 + " TEXT, "
            + COL_MAIN_PORT3 + " TEXT, "
            + COL_MAIN_PORT4 + " TEXT, "
            + COL_MAIN_PORT5 + " TEXT, "
            + COL_MAIN_PORT6 + " TEXT, "
            + COL_MAIN_PORT7 + " TEXT, "
            + COL_MAIN_PORT8 + " TEXT, "
            + COL_EXP1_PORT1 + " TEXT, "
            + COL_EXP1_PORT2 + " TEXT, "
            + COL_EXP1_PORT3 + " TEXT, "
            + COL_EXP1_PORT4 + " TEXT, "
            + COL_EXP1_PORT5 + " TEXT, "
            + COL_EXP1_PORT6 + " TEXT, "
            + COL_EXP1_PORT7 + " TEXT, "
            + COL_EXP1_PORT8 + " TEXT, "
            + COL_EXP2_PORT1 + " TEXT, "
            + COL_EXP2_PORT2 + " TEXT, "
            + COL_EXP2_PORT3 + " TEXT, "
            + COL_EXP2_PORT4 + " TEXT, "
            + COL_EXP2_PORT5 + " TEXT, "
            + COL_EXP2_PORT6 + " TEXT, "
            + COL_EXP2_PORT7 + " TEXT, "
            + COL_EXP2_PORT8 + " TEXT, "
            + COL_EXP3_PORT1 + " TEXT, "
            + COL_EXP3_PORT2 + " TEXT, "
            + COL_EXP3_PORT3 + " TEXT, "
            + COL_EXP3_PORT4 + " TEXT, "
            + COL_EXP3_PORT5 + " TEXT, "
            + COL_EXP3_PORT6 + " TEXT, "
            + COL_EXP3_PORT7 + " TEXT, "
            + COL_EXP3_PORT8 + " TEXT, "
            + COL_EXP4_PORT1 + " TEXT, "
            + COL_EXP4_PORT2 + " TEXT, "
            + COL_EXP4_PORT3 + " TEXT, "
            + COL_EXP4_PORT4 + " TEXT, "
            + COL_EXP4_PORT5 + " TEXT, "
            + COL_EXP4_PORT6 + " TEXT, "
            + COL_EXP4_PORT7 + " TEXT, "
            + COL_EXP4_PORT8 + " TEXT, "
            + COL_EXP5_PORT1 + " TEXT, "
            + COL_EXP5_PORT2 + " TEXT, "
            + COL_EXP5_PORT3 + " TEXT, "
            + COL_EXP5_PORT4 + " TEXT, "
            + COL_EXP5_PORT5 + " TEXT, "
            + COL_EXP5_PORT6 + " TEXT, "
            + COL_EXP5_PORT7 + " TEXT, "
            + COL_EXP5_PORT8 + " TEXT, "
            + COL_EXP6_PORT1 + " TEXT, "
            + COL_EXP6_PORT2 + " TEXT, "
            + COL_EXP6_PORT3 + " TEXT, "
            + COL_EXP6_PORT4 + " TEXT, "
            + COL_EXP6_PORT5 + " TEXT, "
            + COL_EXP6_PORT6 + " TEXT, "
            + COL_EXP6_PORT7 + " TEXT, "
            + COL_EXP6_PORT8 + " TEXT, "
            + COL_EXP7_PORT1 + " TEXT, "
            + COL_EXP7_PORT2 + " TEXT, "
            + COL_EXP7_PORT3 + " TEXT, "
            + COL_EXP7_PORT4 + " TEXT, "
            + COL_EXP7_PORT5 + " TEXT, "
            + COL_EXP7_PORT6 + " TEXT, "
            + COL_EXP7_PORT7 + " TEXT, "
            + COL_EXP7_PORT8 + " TEXT, "
            + COL_EXP8_PORT1 + " TEXT, "
            + COL_EXP8_PORT2 + " TEXT, "
            + COL_EXP8_PORT3 + " TEXT, "
            + COL_EXP8_PORT4 + " TEXT, "
            + COL_EXP8_PORT5 + " TEXT, "
            + COL_EXP8_PORT6 + " TEXT, "
            + COL_EXP8_PORT7 + " TEXT, "
            + COL_EXP8_PORT8 + " TEXT, "
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
                case 19:
                    // drop the table if the downgraded version is less than 20
                    dropTable(db);
                    break;
            }
        }
    }

}
