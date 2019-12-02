/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2019 Curt Binder
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
import android.os.strictmode.SqliteObjectLeakedViolation;

public class UserMemoryLocationsTable {

    public static final String TABLE_NAME = "usermemorylocations";
    // columns
    public static final String COL_ID = "_id";
    public static final String COL_NAME = "name";
    public static final String COL_LOCATION = "location";
    public static final String COL_TYPE = "type";
    public static final String COL_SORT_ORDER = "sortorder";

    public static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME
            + " (" + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COL_NAME + " TEXT, " + COL_LOCATION + " INTEGER, "
            + COL_TYPE + " INTEGER DEFAULT 0, "
            + COL_SORT_ORDER + " INTEGER);";

    private static final String DROP_TABLE = "DROP TABLE IF EXISTS "
            + TABLE_NAME;

    public static void onCreate (SQLiteDatabase db) { db.execSQL(CREATE_TABLE); }

    public static void onUpgrade (
            SQLiteDatabase db,
            int oldVersion,
            int newVersion ) {
        int curVer = oldVersion;
        while (curVer < newVersion) {
            curVer++;
            if ( curVer == 15 ) {
                upgradeToVersion15(db);
            }
        }
    }

    public static void upgradeToVersion15 (SQLiteDatabase db) {
        onCreate(db);
    }

    private static void dropTable (SQLiteDatabase db) { db.execSQL(DROP_TABLE); }

    public static void onDowngrade (
            SQLiteDatabase db,
            int oldVersion,
            int newVersion ) {
        int curVer = oldVersion;
        while ( curVer > newVersion ) {
            curVer--;
            if (curVer < 15) {
                // drop the table if downgrade is less than 15 (version table was added)
                dropTable(db);
                break;
            }
        }
    }
}
