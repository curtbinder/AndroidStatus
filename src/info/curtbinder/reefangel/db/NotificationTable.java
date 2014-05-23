/*
 * Copyright (c) 2011-2013 by Curt Binder (http://curtbinder.info)
 * 
 * This work is made available under the terms of the Creative Commons
 * Attribution-NonCommercial-ShareAlike 3.0 Unported License
 * http://creativecommons.org/licenses/by-nc-sa/3.0/
 */

package info.curtbinder.reefangel.db;

import android.database.sqlite.SQLiteDatabase;

public class NotificationTable {
	
	// Database constants
	public static final String TABLE_NAME = "notifications";
	// columns
	public static final String COL_ID = "_id";
	public static final String COL_PARAM = "param";
	public static final String COL_CONDITION = "condition";
	public static final String COL_VALUE = "value";

	private static final String CREATE_TABLE =
			"CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" + COL_ID
					+ " INTEGER PRIMARY KEY AUTOINCREMENT, " + COL_PARAM
					+ " INTEGER, " + COL_CONDITION + " INTEGER, " + COL_VALUE
					+ " TEXT);";
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
		while ( curVer < newVersion ) {
			curVer++;
			switch (curVer) {
				default:
					break;
				case 6:
					upgradeToVersion6(db);
					break;
			}
		}
	}
	
	private static void upgradeToVersion6 ( SQLiteDatabase db ) {
		// table did no exist prior to version 6, so just drop it as a safety precaution
		// then create it
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
				case 5:
				case 4:
				case 3:
				case 2:
				case 1:
					// drop the table if the downgraded version is less than 6
					dropTable(db);
					break;
			}
		}
	}
}
