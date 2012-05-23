package info.curtbinder.reefangel.phone;

/*
 * Copyright (c) 2011-12 by Curt Binder (http://curtbinder.info)
 *
 * This work is made available under the terms of the 
 * Creative Commons Attribution-NonCommercial-ShareAlike 3.0 Unported License
 * http://creativecommons.org/licenses/by-nc-sa/3.0/
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class RAData {
	private static final String TAG = RAData.class.getSimpleName();

	// Database constants
	public static final String PTABLE_MAX_COUNT = "30";
	public static final String PTABLE_NAME = "params";
	// columns in params table
	public static final String PCOL_ID = "_id";
	public static final String PCOL_T1 = "t1";
	public static final String PCOL_T2 = "t2";
	public static final String PCOL_T3 = "t3";
	public static final String PCOL_PH = "ph";
	public static final String PCOL_DP = "dp";
	public static final String PCOL_AP = "ap";
	public static final String PCOL_ATOHI = "atohi";
	public static final String PCOL_ATOLO = "atolow";
	public static final String PCOL_SAL = "sal";
	public static final String PCOL_ORP = "orp";
	public static final String PCOL_LOGDATE = "logdate";
	public static final String PCOL_RDATA = "rdata";
	public static final String PCOL_RONMASK = "ronmask";
	public static final String PCOL_ROFFMASK = "roffmask";
	public static final String PCOL_R1DATA = "r1data";
	public static final String PCOL_R1ONMASK = "r1onmask";
	public static final String PCOL_R1OFFMASK = "r1offmask";
	public static final String PCOL_R2DATA = "r2data";
	public static final String PCOL_R2ONMASK = "r2onmask";
	public static final String PCOL_R2OFFMASK = "r2offmask";
	public static final String PCOL_R3DATA = "r3data";
	public static final String PCOL_R3ONMASK = "r3onmask";
	public static final String PCOL_R3OFFMASK = "r3offmask";
	public static final String PCOL_R4DATA = "r4data";
	public static final String PCOL_R4ONMASK = "r4onmask";
	public static final String PCOL_R4OFFMASK = "r4offmask";
	public static final String PCOL_R5DATA = "r5data";
	public static final String PCOL_R5ONMASK = "r5onmask";
	public static final String PCOL_R5OFFMASK = "r5offmask";
	public static final String PCOL_R6DATA = "r6data";
	public static final String PCOL_R6ONMASK = "r6onmask";
	public static final String PCOL_R6OFFMASK = "r6offmask";
	public static final String PCOL_R7DATA = "r7data";
	public static final String PCOL_R7ONMASK = "r7onmask";
	public static final String PCOL_R7OFFMASK = "r7offmask";
	public static final String PCOL_R8DATA = "r8data";
	public static final String PCOL_R8ONMASK = "r8onmask";
	public static final String PCOL_R8OFFMASK = "r8offmask";
	public static final String PCOL_PWME0 = "pwme0";
	public static final String PCOL_PWME1 = "pwme1";
	public static final String PCOL_PWME2 = "pwme2";
	public static final String PCOL_PWME3 = "pwme3";
	public static final String PCOL_PWME4 = "pwme4";
	public static final String PCOL_PWME5 = "pwme5";
	public static final String PCOL_AIW = "aiw";
	public static final String PCOL_AIB = "aib";
	public static final String PCOL_AIRB = "airb";
	public static final String PCOL_RFM = "rfm";
	public static final String PCOL_RFS = "rfs";
	public static final String PCOL_RFD = "rfd";
	public static final String PCOL_RFW = "rfw";
	public static final String PCOL_RFRB = "rfrb";
	public static final String PCOL_RFR = "rfr";
	public static final String PCOL_RFG = "rfg";
	public static final String PCOL_RFB = "rfb";
	public static final String PCOL_RFI = "rfi";
	public static final String PCOL_IO = "io";
	public static final String PCOL_C0 = "c0";
	public static final String PCOL_C1 = "c1";
	public static final String PCOL_C2 = "c2";
	public static final String PCOL_C3 = "c3";
	public static final String PCOL_C4 = "c4";
	public static final String PCOL_C5 = "c5";
	public static final String PCOL_C6 = "c6";
	public static final String PCOL_C7 = "c7";
	public static final String PCOL_EM = "em";
	public static final String PCOL_REM = "rem";

	public class DbHelper extends SQLiteOpenHelper {
		private static final String DB_NAME = "radata.db";
		private static final int DB_VERSION = 2;
		private static final String TAG = "DbHelper";

		public DbHelper ( Context context ) {
			super( context, DB_NAME, null, DB_VERSION );
		}

		@Override
		public void onCreate ( SQLiteDatabase db ) {
			// create the tables here
			createParamsTable( db );
		}

		@Override
		public void onUpgrade (
				SQLiteDatabase db,
				int oldVersion,
				int newVersion ) {
			Log.w( TAG, "Upgrading db from v" + oldVersion + " to v"
						+ newVersion + ", which will destroy all old data" );
			// initially, just drop tables and create new ones
			db.execSQL( "DROP TABLE IF EXISTS " + PTABLE_NAME );
			onCreate( db );
		}

		private void createParamsTable ( SQLiteDatabase db ) {
			// create parameters table
			db.execSQL( "CREATE TABLE " + PTABLE_NAME + " (" + PCOL_ID
						+ " INTEGER PRIMARY KEY AUTOINCREMENT, " + PCOL_T1
						+ " TEXT, " + PCOL_T2 + " TEXT, " + PCOL_T3 + " TEXT, "
						+ PCOL_PH + " TEXT, " + PCOL_DP + " TEXT, " + PCOL_AP
						+ " TEXT, " + PCOL_SAL + " TEXT, " + PCOL_ORP
						+ " TEXT, " + PCOL_ATOHI + " INTEGER, " + PCOL_ATOLO
						+ " INTEGER, " + PCOL_LOGDATE + " TEXT, " + PCOL_RDATA
						+ " INTEGER, " + PCOL_RONMASK + " INTEGER, "
						+ PCOL_ROFFMASK + " INTEGER, " + PCOL_R1DATA
						+ " INTEGER, " + PCOL_R1ONMASK + " INTEGER, "
						+ PCOL_R1OFFMASK + " INTEGER, " + PCOL_R2DATA
						+ " INTEGER, " + PCOL_R2ONMASK + " INTEGER, "
						+ PCOL_R2OFFMASK + " INTEGER, " + PCOL_R3DATA
						+ " INTEGER, " + PCOL_R3ONMASK + " INTEGER, "
						+ PCOL_R3OFFMASK + " INTEGER, " + PCOL_R4DATA
						+ " INTEGER, " + PCOL_R4ONMASK + " INTEGER, "
						+ PCOL_R4OFFMASK + " INTEGER, " + PCOL_R5DATA
						+ " INTEGER, " + PCOL_R5ONMASK + " INTEGER, "
						+ PCOL_R5OFFMASK + " INTEGER, " + PCOL_R6DATA
						+ " INTEGER, " + PCOL_R6ONMASK + " INTEGER, "
						+ PCOL_R6OFFMASK + " INTEGER, " + PCOL_R7DATA
						+ " INTEGER, " + PCOL_R7ONMASK + " INTEGER, "
						+ PCOL_R7OFFMASK + " INTEGER, " + PCOL_R8DATA
						+ " INTEGER, " + PCOL_R8ONMASK + " INTEGER, "
						+ PCOL_R8OFFMASK + " INTEGER, " + PCOL_PWME0
						+ " TEXT, " + PCOL_PWME1 + " TEXT, " + PCOL_PWME2
						+ " TEXT, " + PCOL_PWME3 + " TEXT, " + PCOL_PWME4
						+ " TEXT, " + PCOL_PWME5 + " INTEGER, " + PCOL_AIW
						+ " INTEGER, " + PCOL_AIB + " INTEGER, " + PCOL_AIRB
						+ " INTEGER, " + PCOL_RFM + " INTEGER, " + PCOL_RFS
						+ " INTEGER, " + PCOL_RFD + " INTEGER, " + PCOL_RFW
						+ " INTEGER, " + PCOL_RFRB + " INTEGER, " + PCOL_RFR
						+ " INTEGER, " + PCOL_RFG + " INTEGER, " + PCOL_RFB
						+ " INTEGER, " + PCOL_RFI + " INTEGER, " + PCOL_IO
						+ " INTEGER, " + PCOL_C0 + " INTEGER, " + PCOL_C1
						+ " INTEGER, " + PCOL_C2 + " INTEGER, " + PCOL_C3
						+ " INTEGER, " + PCOL_C4 + " INTEGER, " + PCOL_C5
						+ " INTEGER, " + PCOL_C6 + " INTEGER, " + PCOL_C7
						+ " INTEGER, " + PCOL_EM + " INTEGER, " + PCOL_REM
						+ " INTEGER " + ");" );

			// create TRIGGER for params table
			// db.execSQL( "CREATE TRIGGER prune_params_entries INSERT ON "
			// + PTABLE_NAME + " BEGIN DELETE FROM " + PTABLE_NAME
			// + " WHERE " + PCOL_ID + " NOT IN " + "(SELECT "
			// + PCOL_ID + " FROM " + PTABLE_NAME + " ORDER BY "
			// + PCOL_ID + " DESC LIMIT " + PTABLE_MAX_COUNT + ");"
			// + "END;" );
		}
	}

	private final DbHelper dbHelper;

	public RAData ( Context context ) {
		this.dbHelper = new DbHelper( context );
		Log.i( TAG, "Initialized RAData" );
	}

	public void close ( ) {
		Log.d( TAG, "Close database" );
		this.dbHelper.close();
	}

	public void insert ( ContentValues values ) {
		Log.d( TAG, "insert on " + values );
		SQLiteDatabase db = this.dbHelper.getWritableDatabase();
		try {
			db.insert( PTABLE_NAME, null, values );
		} finally {
			db.close();
		}
	}

	private Cursor getData ( String limit, String selection )
			throws SQLException {
		SQLiteDatabase db = this.dbHelper.getReadableDatabase();
		return db.query(	PTABLE_NAME, null, selection, null, null, null,
							PCOL_ID + " DESC", limit );
	}

	public Cursor getLatestData ( ) throws SQLException {
		return getData( "1", null );
	}

	public Cursor getAllData ( ) throws SQLException {
		return getData( null, null );
	}

	public Cursor getDataById ( long id ) throws SQLException {
		return getData( null, PCOL_ID + "=" + id );
	}

	// public String[] getAllColumns() {
	// // returns a string list of all the columns
	// return new String [] {
	// PCOL_ID, PCOL_T1, PCOL_T2, PCOL_T3, PCOL_PH, PCOL_DP,
	// PCOL_AP, PCOL_SAL, PCOL_ATOHI, PCOL_ATOLO,
	// PCOL_LOGDATE, PCOL_RDATA, PCOL_RONMASK, PCOL_ROFFMASK
	// };
	// }

	public void deleteData ( ) {
		SQLiteDatabase db = this.dbHelper.getWritableDatabase();
		db.delete( PTABLE_NAME, null, null );
		db.close();
	}
}
