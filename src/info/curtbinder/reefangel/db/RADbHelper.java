/*
 * Copyright (c) 2011-2013 by Curt Binder (http://curtbinder.info)
 * 
 * This work is made available under the terms of the Creative Commons
 * Attribution-NonCommercial-ShareAlike 3.0 Unported License
 * http://creativecommons.org/licenses/by-nc-sa/3.0/
 */

package info.curtbinder.reefangel.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class RADbHelper extends SQLiteOpenHelper {

	private static final String DB_NAME = "radata.db";
	private static final int DB_VERSION = 5;
	// ErrorTable added in version 5

	public RADbHelper ( Context context ) {
		super( context, DB_NAME, null, DB_VERSION );
	}

	@Override
	public void onCreate ( SQLiteDatabase db ) {
		// create the tables here
		StatusTable.onCreate( db );
		ErrorTable.onCreate( db );
	}

	@Override
	public void onDowngrade ( SQLiteDatabase db, int oldVersion, int newVersion ) {
		ErrorTable.onDowngrade( db, oldVersion, newVersion );
	}

	@Override
	public void onUpgrade ( SQLiteDatabase db, int oldVersion, int newVersion ) {
		StatusTable.onUpgrade( db, oldVersion, newVersion );
		ErrorTable.onUpgrade( db, oldVersion, newVersion );
	}
}
