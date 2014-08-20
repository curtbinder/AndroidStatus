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
        while ( curVer < newVersion ) {
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
