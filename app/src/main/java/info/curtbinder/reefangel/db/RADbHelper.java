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

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class RADbHelper extends SQLiteOpenHelper {

	public static final String DB_NAME = "radata.db";
    private static final int DB_VERSION = 15;
    // Version 5 - ErrorTable added
    // Version 6 - NotificationTable added
    // Version 7 - StatusTable updated
    // Version 8 - StatusTable updated
    // Version 9 - StatusTable updated
    // Version 10 - StatusTable updated
    // Version 11 - StatusTable updated
    // Version 12 - StatusTable updated
	// Version 13 - StatusTable updated
	// Version 15 - UserMemoryLocationsTable added
	// Version 20 - LabelsTable, ControllersTable,
    //              ControllerProbesVisibilityTable, RelayEnabledPortsTable added
	//              TODO add foreign key references for ErrorTable, NotificationTable and StatusTable
    //              TODO update Status, Notification, Error tables to reference Controllers Table

	public RADbHelper ( Context context ) {
		super( context, DB_NAME, null, DB_VERSION );
	}

	@Override
	public void onCreate ( SQLiteDatabase db ) {
		// create the tables here
		StatusTable.onCreate( db );
		ErrorTable.onCreate( db );
		NotificationTable.onCreate( db );
		UserMemoryLocationsTable.onCreate(db);
		// TODO add creation of LabelsTable, ControllersTable, ControllerProbesVisibilityTable, RelayEnabledPortsTable
	}

	@Override
	public void onDowngrade ( SQLiteDatabase db, int oldVersion, int newVersion ) {
		ErrorTable.onDowngrade( db, oldVersion, newVersion );
		NotificationTable.onDowngrade( db, oldVersion, newVersion );
		UserMemoryLocationsTable.onDowngrade(db, oldVersion, newVersion);
        // TODO add deletion of LabelsTable, ControllersTable, ControllerProbesVisibilityTable, RelayEnabledPortsTable
	}

	@Override
	public void onUpgrade ( SQLiteDatabase db, int oldVersion, int newVersion ) {
		StatusTable.onUpgrade( db, oldVersion, newVersion );
		ErrorTable.onUpgrade( db, oldVersion, newVersion );
		NotificationTable.onUpgrade( db, oldVersion, newVersion );
		UserMemoryLocationsTable.onUpgrade(db, oldVersion, newVersion);
        // TODO add upgrade of LabelsTable, ControllersTable, ControllerProbesVisibilityTable, RelayEnabledPortsTable
	}
}
