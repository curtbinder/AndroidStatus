/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2013 Curt Binder
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

public class StatusTable {

	//private static final String TAG = StatusTable.class.getSimpleName();

	// Database constants
	public static final String TABLE_NAME = "params";
	// columns
	public static final String COL_ID = "_id";
	public static final String COL_T1 = "t1";
	public static final String COL_T2 = "t2";
	public static final String COL_T3 = "t3";
	public static final String COL_PH = "ph";
	public static final String COL_DP = "dp";
	public static final String COL_AP = "ap";
	public static final String COL_ATOHI = "atohi";
	public static final String COL_ATOLO = "atolow";
	public static final String COL_SAL = "sal";
	public static final String COL_ORP = "orp";
	public static final String COL_LOGDATE = "logdate";
	public static final String COL_RDATA = "rdata";
	public static final String COL_RONMASK = "ronmask";
	public static final String COL_ROFFMASK = "roffmask";
	public static final String COL_R1DATA = "r1data";
	public static final String COL_R1ONMASK = "r1onmask";
	public static final String COL_R1OFFMASK = "r1offmask";
	public static final String COL_R2DATA = "r2data";
	public static final String COL_R2ONMASK = "r2onmask";
	public static final String COL_R2OFFMASK = "r2offmask";
	public static final String COL_R3DATA = "r3data";
	public static final String COL_R3ONMASK = "r3onmask";
	public static final String COL_R3OFFMASK = "r3offmask";
	public static final String COL_R4DATA = "r4data";
	public static final String COL_R4ONMASK = "r4onmask";
	public static final String COL_R4OFFMASK = "r4offmask";
	public static final String COL_R5DATA = "r5data";
	public static final String COL_R5ONMASK = "r5onmask";
	public static final String COL_R5OFFMASK = "r5offmask";
	public static final String COL_R6DATA = "r6data";
	public static final String COL_R6ONMASK = "r6onmask";
	public static final String COL_R6OFFMASK = "r6offmask";
	public static final String COL_R7DATA = "r7data";
	public static final String COL_R7ONMASK = "r7onmask";
	public static final String COL_R7OFFMASK = "r7offmask";
	public static final String COL_R8DATA = "r8data";
	public static final String COL_R8ONMASK = "r8onmask";
	public static final String COL_R8OFFMASK = "r8offmask";
	public static final String COL_PWME0 = "pwme0";
	public static final String COL_PWME1 = "pwme1";
	public static final String COL_PWME2 = "pwme2";
	public static final String COL_PWME3 = "pwme3";
	public static final String COL_PWME4 = "pwme4";
	public static final String COL_PWME5 = "pwme5";
	public static final String COL_AIW = "aiw";
	public static final String COL_AIB = "aib";
	public static final String COL_AIRB = "airb";
	public static final String COL_RFM = "rfm";
	public static final String COL_RFS = "rfs";
	public static final String COL_RFD = "rfd";
	public static final String COL_RFW = "rfw";
	public static final String COL_RFRB = "rfrb";
	public static final String COL_RFR = "rfr";
	public static final String COL_RFG = "rfg";
	public static final String COL_RFB = "rfb";
	public static final String COL_RFI = "rfi";
	public static final String COL_IO = "io";
	public static final String COL_C0 = "c0";
	public static final String COL_C1 = "c1";
	public static final String COL_C2 = "c2";
	public static final String COL_C3 = "c3";
	public static final String COL_C4 = "c4";
	public static final String COL_C5 = "c5";
	public static final String COL_C6 = "c6";
	public static final String COL_C7 = "c7";
	public static final String COL_EM = "em";
	public static final String COL_REM = "rem";
	public static final String COL_PHE = "phe";
	public static final String COL_WL = "wl";
    public static final String COL_WL1 = "wl1";
    public static final String COL_WL2 = "wl2";
    public static final String COL_WL3 = "wl3";
    public static final String COL_WL4 = "wl4";
    public static final String COL_EM1 = "em1";
    public static final String COL_HUM = "hum";
    public static final String COL_PWMAO = "pwmao";
    public static final String COL_PWMDO = "pwmdo";
    public static final String COL_PWME0O = "pwme0o";
    public static final String COL_PWME1O = "pwme1o";
    public static final String COL_PWME2O = "pwme2o";
    public static final String COL_PWME3O = "pwme3o";
    public static final String COL_PWME4O = "pwme4o";
    public static final String COL_PWME5O = "pwme5o";
    public static final String COL_AIWO = "aiwo";
    public static final String COL_AIBO = "aibo";
    public static final String COL_AIRBO = "airbo";
    public static final String COL_RFWO = "rfwo";
    public static final String COL_RFRBO = "rfrbo";
    public static final String COL_RFRO = "rfro";
    public static final String COL_RFGO = "rfgo";
    public static final String COL_RFBO = "rfbo";
    public static final String COL_RFIO = "rfio";
    public static final String COL_AF = "af";
    public static final String COL_SF = "sf";
    public static final String COL_SCPWME0 = "scpwme0";
    public static final String COL_SCPWME0O = "scpwme0o";
    public static final String COL_SCPWME1 = "scpwme1";
    public static final String COL_SCPWME1O = "scpwme1o";
    public static final String COL_SCPWME2 = "scpwme2";
    public static final String COL_SCPWME2O = "scpwme2o";
    public static final String COL_SCPWME3 = "scpwme3";
    public static final String COL_SCPWME3O = "scpwme3o";
    public static final String COL_SCPWME4 = "scpwme4";
    public static final String COL_SCPWME4O = "scpwme4o";
    public static final String COL_SCPWME5 = "scpwme5";
    public static final String COL_SCPWME5O = "scpwme5o";
    public static final String COL_SCPWME6 = "scpwme6";
    public static final String COL_SCPWME6O = "scpwme6o";
    public static final String COL_SCPWME7 = "scpwme7";
    public static final String COL_SCPWME7O = "scpwme7o";
    public static final String COL_SCPWME8 = "scpwme8";
    public static final String COL_SCPWME8O = "scpwme8o";
    public static final String COL_SCPWME9 = "scpwme9";
    public static final String COL_SCPWME9O = "scpwme9o";
    public static final String COL_SCPWME10 = "scpwme10";
    public static final String COL_SCPWME10O = "scpwme10o";
    public static final String COL_SCPWME11 = "scpwme11";
    public static final String COL_SCPWME11O = "scpwme11o";
    public static final String COL_SCPWME12 = "scpwme12";
    public static final String COL_SCPWME12O = "scpwme12o";
    public static final String COL_SCPWME13 = "scpwme13";
    public static final String COL_SCPWME13O = "scpwme13o";
    public static final String COL_SCPWME14 = "scpwme14";
    public static final String COL_SCPWME14O = "scpwme14o";
    public static final String COL_SCPWME15 = "scpwme15";
    public static final String COL_SCPWME15O = "scpwme15o";
    public static final String COL_DCM = "dcm";
    public static final String COL_DCS = "dcs";
    public static final String COL_DCD = "dcd";
    public static final String COL_DCT = "dct";


	public static void onCreate ( SQLiteDatabase db ) {
		// create parameters table
        db.execSQL( "CREATE TABLE " + TABLE_NAME + " (" + COL_ID
                + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COL_T1
                + " TEXT, " + COL_T2 + " TEXT, " + COL_T3 + " TEXT, "
                + COL_PH + " TEXT, " + COL_DP + " INTEGER, " + COL_AP
                + " INTEGER, " + COL_SAL + " TEXT, " + COL_ORP + " TEXT, "
                + COL_ATOHI + " INTEGER, " + COL_ATOLO + " INTEGER, "
                + COL_LOGDATE + " TEXT, " + COL_RDATA + " INTEGER, "
                + COL_RONMASK + " INTEGER, " + COL_ROFFMASK + " INTEGER, "
                + COL_R1DATA + " INTEGER, " + COL_R1ONMASK + " INTEGER, "
                + COL_R1OFFMASK + " INTEGER, " + COL_R2DATA + " INTEGER, "
                + COL_R2ONMASK + " INTEGER, " + COL_R2OFFMASK
                + " INTEGER, " + COL_R3DATA + " INTEGER, " + COL_R3ONMASK
                + " INTEGER, " + COL_R3OFFMASK + " INTEGER, " + COL_R4DATA
                + " INTEGER, " + COL_R4ONMASK + " INTEGER, "
                + COL_R4OFFMASK + " INTEGER, " + COL_R5DATA + " INTEGER, "
                + COL_R5ONMASK + " INTEGER, " + COL_R5OFFMASK
                + " INTEGER, " + COL_R6DATA + " INTEGER, " + COL_R6ONMASK
                + " INTEGER, " + COL_R6OFFMASK + " INTEGER, " + COL_R7DATA
                + " INTEGER, " + COL_R7ONMASK + " INTEGER, "
                + COL_R7OFFMASK + " INTEGER, " + COL_R8DATA + " INTEGER, "
                + COL_R8ONMASK + " INTEGER, " + COL_R8OFFMASK
                + " INTEGER, " + COL_PWME0 + " INTEGER, " + COL_PWME1
                + " INTEGER, " + COL_PWME2 + " INTEGER, " + COL_PWME3
                + " INTEGER, " + COL_PWME4 + " INTEGER, " + COL_PWME5
                + " INTEGER, " + COL_AIW + " INTEGER, " + COL_AIB
                + " INTEGER, " + COL_AIRB + " INTEGER, " + COL_RFM
                + " INTEGER, " + COL_RFS + " INTEGER, " + COL_RFD
                + " INTEGER, " + COL_RFW + " INTEGER, " + COL_RFRB
                + " INTEGER, " + COL_RFR + " INTEGER, " + COL_RFG
                + " INTEGER, " + COL_RFB + " INTEGER, " + COL_RFI
                + " INTEGER, " + COL_IO + " INTEGER, " + COL_C0
                + " INTEGER, " + COL_C1 + " INTEGER, " + COL_C2
                + " INTEGER, " + COL_C3 + " INTEGER, " + COL_C4
                + " INTEGER, " + COL_C5 + " INTEGER, " + COL_C6
                + " INTEGER, " + COL_C7 + " INTEGER, " + COL_EM
                + " INTEGER, " + COL_REM + " INTEGER, " + COL_PHE
                + " TEXT, " + COL_WL + " INTEGER, " + COL_WL1
                + " INTEGER, " + COL_WL2 + " INTEGER, " + COL_WL3
                + " INTEGER, " + COL_WL4 + " INTEGER, " + COL_EM1
                + " INTEGER, " + COL_HUM + " INTEGER, "
                + COL_PWMAO + " INTEGER DEFAULT 255, "
                + COL_PWMDO + " INTEGER DEFAULT 255, "
                + COL_PWME0O + " INTEGER DEFAULT 255, "
                + COL_PWME1O + " INTEGER DEFAULT 255, "
                + COL_PWME2O + " INTEGER DEFAULT 255, "
                + COL_PWME3O + " INTEGER DEFAULT 255, "
                + COL_PWME4O + " INTEGER DEFAULT 255, "
                + COL_PWME5O + " INTEGER DEFAULT 255, "
                + COL_AIWO + " INTEGER DEFAULT 255, "
                + COL_AIBO + " INTEGER DEFAULT 255, "
                + COL_AIRBO + " INTEGER DEFAULT 255, "
                + COL_RFWO + " INTEGER DEFAULT 255, "
                + COL_RFRBO + " INTEGER DEFAULT 255, "
                + COL_RFRO + " INTEGER DEFAULT 255, "
                + COL_RFGO + " INTEGER DEFAULT 255, "
                + COL_RFBO + " INTEGER DEFAULT 255, "
                + COL_RFIO + " INTEGER DEFAULT 255, "
                + COL_AF + " INTEGER DEFAULT 0, "
                + COL_SF + " INTEGER DEFAULT 0, "
                + COL_SCPWME0 + " INTEGER, "
                + COL_SCPWME0O + " INTEGER DEFAULT 255, "
                + COL_SCPWME1 + " INTEGER, "
                + COL_SCPWME1O + " INTEGER DEFAULT 255, "
                + COL_SCPWME2 + " INTEGER, "
                + COL_SCPWME2O + " INTEGER DEFAULT 255, "
                + COL_SCPWME3 + " INTEGER, "
                + COL_SCPWME3O + " INTEGER DEFAULT 255, "
                + COL_SCPWME4 + " INTEGER, "
                + COL_SCPWME4O + " INTEGER DEFAULT 255, "
                + COL_SCPWME5 + " INTEGER, "
                + COL_SCPWME5O + " INTEGER DEFAULT 255, "
                + COL_SCPWME6 + " INTEGER, "
                + COL_SCPWME6O + " INTEGER DEFAULT 255, "
                + COL_SCPWME7 + " INTEGER, "
                + COL_SCPWME7O + " INTEGER DEFAULT 255, "
                + COL_SCPWME8 + " INTEGER, "
                + COL_SCPWME8O + " INTEGER DEFAULT 255, "
                + COL_SCPWME9 + " INTEGER, "
                + COL_SCPWME9O + " INTEGER DEFAULT 255, "
                + COL_SCPWME10 + " INTEGER, "
                + COL_SCPWME10O + " INTEGER DEFAULT 255, "
                + COL_SCPWME11 + " INTEGER, "
                + COL_SCPWME11O + " INTEGER DEFAULT 255, "
                + COL_SCPWME12 + " INTEGER, "
                + COL_SCPWME12O + " INTEGER DEFAULT 255, "
                + COL_SCPWME13 + " INTEGER, "
                + COL_SCPWME13O + " INTEGER DEFAULT 255, "
                + COL_SCPWME14 + " INTEGER, "
                + COL_SCPWME14O + " INTEGER DEFAULT 255, "
                + COL_SCPWME15 + " INTEGER, "
                + COL_SCPWME15O + " INTEGER DEFAULT 255, "
                + COL_DCM + " INTEGER, "
                + COL_DCS + " INTEGER, "
                + COL_DCD + " INTEGER, "
                + COL_DCT + " INTEGER"

                + ");" );

	}

	public static void onUpgrade (
			SQLiteDatabase db,
			int oldVersion,
			int newVersion ) {
        int curVer = oldVersion;
        while ( curVer < newVersion ) {
            curVer++;
            // only list the versions that there were changes made
            switch ( curVer ) {
                default:
                    break;
                case 4:
                    upgradeToVersion4(db);
                    break;
                case 7:
                    upgradeToVersion7(db);
                    break;
                case 8:
                    upgradeToVersion8(db);
                    break;
                case 9:
                    upgradeToVersion9(db);
                    break;
                case 10:
                    upgradeToVersion10(db);
                    break;
                case 11:
                    upgradeToVersion11(db);
                    break;
                case 12:
                    upgradeToVersion12(db);
                    break;
            }
        }
	}

    // no need to worry about having extra columns in the status table on downgrading
//	public static void onDowngrade ( SQLiteDatabase db,
//			int oldVersion,
//			int newVersion ) {
//		int curVer = oldVersion;
//		while ( curVer > newVersion ) {
//			curVer--;
//			switch ( curVer ) {
//				default:
//					break;
//				case 6:
//					downgradeToVersion6(db);
//					break;
//			}
//		}
//	}

    private static void upgradeToVersion4(SQLiteDatabase db) {
        // clear everything and drop the table
        db.execSQL( "DROP TABLE IF EXISTS " + TABLE_NAME );
        onCreate( db );
    }

    private static void upgradeToVersion7(SQLiteDatabase db) {
        // added in additional water level columns
        db.execSQL( "ALTER TABLE " + TABLE_NAME + " ADD COLUMN " + COL_WL1 + " INTEGER;" );
        db.execSQL( "ALTER TABLE " + TABLE_NAME + " ADD COLUMN " + COL_WL2 + " INTEGER;" );
        db.execSQL( "ALTER TABLE " + TABLE_NAME + " ADD COLUMN " + COL_WL3 + " INTEGER;" );
        db.execSQL( "ALTER TABLE " + TABLE_NAME + " ADD COLUMN " + COL_WL4 + " INTEGER;" );
    }

    private static void upgradeToVersion8(SQLiteDatabase db) {
        // added in EM1 column
        db.execSQL( "ALTER TABLE " + TABLE_NAME + " ADD COLUMN " + COL_EM1 + " INTEGER;" );
        db.execSQL( "ALTER TABLE " + TABLE_NAME + " ADD COLUMN " + COL_HUM + " INTEGER;" );
    }

    private static void upgradeToVersion9(SQLiteDatabase db) {
        // add in pwm override channels
        db.execSQL( "ALTER TABLE " + TABLE_NAME + " ADD COLUMN " + COL_PWMAO + " INTEGER DEFAULT 255;" );
        db.execSQL( "ALTER TABLE " + TABLE_NAME + " ADD COLUMN " + COL_PWMDO + " INTEGER DEFAULT 255;" );
        db.execSQL( "ALTER TABLE " + TABLE_NAME + " ADD COLUMN " + COL_PWME0O + " INTEGER DEFAULT 255;" );
        db.execSQL( "ALTER TABLE " + TABLE_NAME + " ADD COLUMN " + COL_PWME1O + " INTEGER DEFAULT 255;" );
        db.execSQL( "ALTER TABLE " + TABLE_NAME + " ADD COLUMN " + COL_PWME2O + " INTEGER DEFAULT 255;" );
        db.execSQL( "ALTER TABLE " + TABLE_NAME + " ADD COLUMN " + COL_PWME3O + " INTEGER DEFAULT 255;" );
        db.execSQL( "ALTER TABLE " + TABLE_NAME + " ADD COLUMN " + COL_PWME4O + " INTEGER DEFAULT 255;" );
        db.execSQL( "ALTER TABLE " + TABLE_NAME + " ADD COLUMN " + COL_PWME5O + " INTEGER DEFAULT 255;" );
        db.execSQL( "ALTER TABLE " + TABLE_NAME + " ADD COLUMN " + COL_AIWO + " INTEGER DEFAULT 255;" );
        db.execSQL( "ALTER TABLE " + TABLE_NAME + " ADD COLUMN " + COL_AIBO + " INTEGER DEFAULT 255;" );
        db.execSQL( "ALTER TABLE " + TABLE_NAME + " ADD COLUMN " + COL_AIRBO + " INTEGER DEFAULT 255;" );
        db.execSQL( "ALTER TABLE " + TABLE_NAME + " ADD COLUMN " + COL_RFWO + " INTEGER DEFAULT 255;" );
        db.execSQL( "ALTER TABLE " + TABLE_NAME + " ADD COLUMN " + COL_RFRBO + " INTEGER DEFAULT 255;" );
        db.execSQL( "ALTER TABLE " + TABLE_NAME + " ADD COLUMN " + COL_RFRO + " INTEGER DEFAULT 255;" );
        db.execSQL( "ALTER TABLE " + TABLE_NAME + " ADD COLUMN " + COL_RFGO + " INTEGER DEFAULT 255;" );
        db.execSQL( "ALTER TABLE " + TABLE_NAME + " ADD COLUMN " + COL_RFBO + " INTEGER DEFAULT 255;" );
        db.execSQL( "ALTER TABLE " + TABLE_NAME + " ADD COLUMN " + COL_RFIO + " INTEGER DEFAULT 255;" );
    }

    private static void upgradeToVersion10(SQLiteDatabase db) {
        // add in alert and status flags fields
        db.execSQL( "ALTER TABLE " + TABLE_NAME + " ADD COLUMN " + COL_AF + " INTEGER DEFAULT 0;" );
        db.execSQL( "ALTER TABLE " + TABLE_NAME + " ADD COLUMN " + COL_SF + " INTEGER DEFAULT 0;" );
    }

    private static void upgradeToVersion11(SQLiteDatabase db) {
        // add in 16 channel pwm support
        db.execSQL( "ALTER TABLE " + TABLE_NAME + " ADD COLUMN " + COL_SCPWME0 + " INTEGER;" );
        db.execSQL( "ALTER TABLE " + TABLE_NAME + " ADD COLUMN " + COL_SCPWME0O + " INTEGER DEFAULT 255;" );
        db.execSQL( "ALTER TABLE " + TABLE_NAME + " ADD COLUMN " + COL_SCPWME1 + " INTEGER;" );
        db.execSQL( "ALTER TABLE " + TABLE_NAME + " ADD COLUMN " + COL_SCPWME1O + " INTEGER DEFAULT 255;" );
        db.execSQL( "ALTER TABLE " + TABLE_NAME + " ADD COLUMN " + COL_SCPWME2 + " INTEGER;" );
        db.execSQL( "ALTER TABLE " + TABLE_NAME + " ADD COLUMN " + COL_SCPWME2O + " INTEGER DEFAULT 255;" );
        db.execSQL( "ALTER TABLE " + TABLE_NAME + " ADD COLUMN " + COL_SCPWME3 + " INTEGER;" );
        db.execSQL( "ALTER TABLE " + TABLE_NAME + " ADD COLUMN " + COL_SCPWME3O + " INTEGER DEFAULT 255;" );
        db.execSQL( "ALTER TABLE " + TABLE_NAME + " ADD COLUMN " + COL_SCPWME4 + " INTEGER;" );
        db.execSQL( "ALTER TABLE " + TABLE_NAME + " ADD COLUMN " + COL_SCPWME4O + " INTEGER DEFAULT 255;" );
        db.execSQL( "ALTER TABLE " + TABLE_NAME + " ADD COLUMN " + COL_SCPWME5 + " INTEGER;" );
        db.execSQL( "ALTER TABLE " + TABLE_NAME + " ADD COLUMN " + COL_SCPWME5O + " INTEGER DEFAULT 255;" );
        db.execSQL( "ALTER TABLE " + TABLE_NAME + " ADD COLUMN " + COL_SCPWME6 + " INTEGER;" );
        db.execSQL( "ALTER TABLE " + TABLE_NAME + " ADD COLUMN " + COL_SCPWME6O + " INTEGER DEFAULT 255;" );
        db.execSQL( "ALTER TABLE " + TABLE_NAME + " ADD COLUMN " + COL_SCPWME7 + " INTEGER;" );
        db.execSQL( "ALTER TABLE " + TABLE_NAME + " ADD COLUMN " + COL_SCPWME7O + " INTEGER DEFAULT 255;" );
        db.execSQL( "ALTER TABLE " + TABLE_NAME + " ADD COLUMN " + COL_SCPWME8 + " INTEGER;" );
        db.execSQL( "ALTER TABLE " + TABLE_NAME + " ADD COLUMN " + COL_SCPWME8O + " INTEGER DEFAULT 255;" );
        db.execSQL( "ALTER TABLE " + TABLE_NAME + " ADD COLUMN " + COL_SCPWME9 + " INTEGER;" );
        db.execSQL( "ALTER TABLE " + TABLE_NAME + " ADD COLUMN " + COL_SCPWME9O + " INTEGER DEFAULT 255;" );
        db.execSQL( "ALTER TABLE " + TABLE_NAME + " ADD COLUMN " + COL_SCPWME10 + " INTEGER;" );
        db.execSQL( "ALTER TABLE " + TABLE_NAME + " ADD COLUMN " + COL_SCPWME10O + " INTEGER DEFAULT 255;" );
        db.execSQL( "ALTER TABLE " + TABLE_NAME + " ADD COLUMN " + COL_SCPWME11 + " INTEGER;" );
        db.execSQL( "ALTER TABLE " + TABLE_NAME + " ADD COLUMN " + COL_SCPWME11O + " INTEGER DEFAULT 255;" );
        db.execSQL( "ALTER TABLE " + TABLE_NAME + " ADD COLUMN " + COL_SCPWME12 + " INTEGER;" );
        db.execSQL( "ALTER TABLE " + TABLE_NAME + " ADD COLUMN " + COL_SCPWME12O + " INTEGER DEFAULT 255;" );
        db.execSQL( "ALTER TABLE " + TABLE_NAME + " ADD COLUMN " + COL_SCPWME13 + " INTEGER;" );
        db.execSQL( "ALTER TABLE " + TABLE_NAME + " ADD COLUMN " + COL_SCPWME13O + " INTEGER DEFAULT 255;" );
        db.execSQL( "ALTER TABLE " + TABLE_NAME + " ADD COLUMN " + COL_SCPWME14 + " INTEGER;" );
        db.execSQL( "ALTER TABLE " + TABLE_NAME + " ADD COLUMN " + COL_SCPWME14O + " INTEGER DEFAULT 255;" );
        db.execSQL( "ALTER TABLE " + TABLE_NAME + " ADD COLUMN " + COL_SCPWME15 + " INTEGER;" );
        db.execSQL( "ALTER TABLE " + TABLE_NAME + " ADD COLUMN " + COL_SCPWME15O + " INTEGER DEFAULT 255;" );
    }

    private static void upgradeToVersion12(SQLiteDatabase db) {
        // add in dc pump support
        db.execSQL( "ALTER TABLE " + TABLE_NAME + " ADD COLUMN " + COL_DCM + " INTEGER;" );
        db.execSQL( "ALTER TABLE " + TABLE_NAME + " ADD COLUMN " + COL_DCS + " INTEGER;" );
        db.execSQL( "ALTER TABLE " + TABLE_NAME + " ADD COLUMN " + COL_DCD + " INTEGER;" );
        db.execSQL( "ALTER TABLE " + TABLE_NAME + " ADD COLUMN " + COL_DCT + " INTEGER;" );
    }
}
