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
					+ " TEXT, " + COL_WL + " INTEGER " + ");" );

	}

	public static void onUpgrade (
			SQLiteDatabase db,
			int oldVersion,
			int newVersion ) {
		// no changes made in version 5
		if ( oldVersion < 4 ) {
			// initially, just drop tables and create new ones
			db.execSQL( "DROP TABLE IF EXISTS " + TABLE_NAME );
			onCreate( db );
		}
	}
}
