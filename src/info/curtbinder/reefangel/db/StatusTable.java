/*
 * Copyright (c) 2011-2013 by Curt Binder (http://curtbinder.info)
 * 
 * This work is made available under the terms of the Creative Commons
 * Attribution-NonCommercial-ShareAlike 3.0 Unported License
 * http://creativecommons.org/licenses/by-nc-sa/3.0/
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
					+ COL_SF + " INTEGER DEFAULT 0 "
					
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
}
