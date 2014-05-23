/*
 * Copyright (c) 2011-2013 by Curt Binder (http://curtbinder.info)
 * 
 * This work is made available under the terms of the Creative Commons
 * Attribution-NonCommercial-ShareAlike 3.0 Unported License
 * http://creativecommons.org/licenses/by-nc-sa/3.0/
 */

package info.curtbinder.reefangel.db;

import android.database.sqlite.SQLiteDatabase;

public class ErrorTable {

	// private static final String TAG = ErrorTable.class.getSimpleName();

	// Database constants
	public static final String TABLE_NAME = "errors";
	// columns
	public static final String COL_ID = "_id";
	public static final String COL_TIME = "time";
	public static final String COL_MESSAGE = "message";
	public static final String COL_READ = "read";

	private static final String CREATE_TABLE =
			"CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" + COL_ID
					+ " INTEGER PRIMARY KEY AUTOINCREMENT, " + COL_TIME
					+ " INTEGER, " + COL_MESSAGE + " TEXT, " + COL_READ
					+ " INTEGER);";
	private static final String DROP_TABLE = "DROP TABLE IF EXISTS "
												+ TABLE_NAME;

	public static void onCreate ( SQLiteDatabase db ) {
		db.execSQL( CREATE_TABLE );
	}

	public static void onUpgrade (
			SQLiteDatabase db,
			int oldVersion,
			int newVersion ) {
		int curVer = oldVersion;
		while ( curVer < oldVersion ) {
			curVer++;
			switch ( curVer ) {
				default:
					break;
				case 5:
					upgradeToVersion5(db);
					break;
			}
		}
	}
	
	private static void upgradeToVersion5 ( SQLiteDatabase db ) {
		// table did not exist prior to version 5
		dropTable(db);
		onCreate(db);
	}

	private static void dropTable ( SQLiteDatabase db ) {
		db.execSQL( DROP_TABLE );
	}

	public static void onDowngrade (
			SQLiteDatabase db,
			int oldVersion,
			int newVersion ) {
		int curVer = oldVersion;
		while ( curVer > newVersion ) {
			curVer--;
			switch ( curVer ) {
				default:
					break;
				case 4:
				case 3:
				case 2:
				case 1:
					// drop the table if the downgraded version is less than 5
					dropTable(db);
					break;
			}
		}
	}
}
