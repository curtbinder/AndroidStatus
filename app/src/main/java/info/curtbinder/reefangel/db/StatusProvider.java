/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2012 Curt Binder
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

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

import info.curtbinder.reefangel.phone.BuildConfig;

public class StatusProvider extends ContentProvider {

	// private static String TAG = StatusProvider.class.getSimpleName();
	private RADbHelper data;

    private static final String CONTENT = buildAuthority();

    private static String buildAuthority() {
        String a = StatusProvider.class.getPackage().getName();
        if (BuildConfig.DEBUG) {
            a += ".debug";
        }
        return a;
    }

	private static final String CONTENT_MIME_TYPE = "/vnd." + CONTENT + ".";
	public static final Uri CONTENT_URI = Uri.parse( "content://" + CONTENT );

	// PATHS
	public static final String PATH_LATEST = "latest";
	public static final String PATH_STATUS = "status";
	public static final String PATH_ERROR = "error";
	public static final String PATH_NOTIFICATION = "notification";
	public static final String PATH_CONTROLLER = "controller";

	// MIME Types
	// latest item
	public static final String LATEST_MIME_TYPE =
			ContentResolver.CURSOR_ITEM_BASE_TYPE + CONTENT_MIME_TYPE
					+ PATH_LATEST;
	// status - item
	public static final String STATUS_ID_MIME_TYPE =
			ContentResolver.CURSOR_ITEM_BASE_TYPE + CONTENT_MIME_TYPE
					+ PATH_STATUS;
	// status - all items
	public static final String STATUS_MIME_TYPE =
			ContentResolver.CURSOR_DIR_BASE_TYPE + CONTENT_MIME_TYPE
					+ PATH_STATUS;
	// error - item (not sure if needed)
	public static final String ERROR_ID_MIME_TYPE =
			ContentResolver.CURSOR_ITEM_BASE_TYPE + CONTENT_MIME_TYPE
					+ PATH_ERROR;
	// error - all items
	public static final String ERROR_MIME_TYPE =
			ContentResolver.CURSOR_DIR_BASE_TYPE + CONTENT_MIME_TYPE
					+ PATH_ERROR;
	// notification - item
	public static final String NOTIFICATION_ID_MIME_TYPE =
			ContentResolver.CURSOR_ITEM_BASE_TYPE + CONTENT_MIME_TYPE
					+ PATH_NOTIFICATION;
	// notification - all items
	public static final String NOTIFICATION_MIME_TYPE =
			ContentResolver.CURSOR_DIR_BASE_TYPE + CONTENT_MIME_TYPE
					+ PATH_NOTIFICATION;
	// controller - 1 item
	public static final String CONTROLLER_ID_MIME_TYPE =
			ContentResolver.CURSOR_ITEM_BASE_TYPE + CONTENT_MIME_TYPE
					+ PATH_CONTROLLER;
	// controller - all items
	public static final String CONTROLLER_MIME_TYPE =
			ContentResolver.CURSOR_DIR_BASE_TYPE + CONTENT_MIME_TYPE
					+ PATH_CONTROLLER;

	// Used for the UriMatcher
	private static final int CODE_LATEST = 10;
	private static final int CODE_STATUS = 11;
	private static final int CODE_STATUS_ID = 12;
	private static final int CODE_ERROR = 13;
	private static final int CODE_ERROR_ID = 14;
	private static final int CODE_NOTIFICATION = 15;
	private static final int CODE_NOTIFICATION_ID = 16;
	private static final int CODE_CONTROLLER = 20;  // all controllers
	private static final int CODE_CONTROLLER_ID = 21; // individual controllers
	private static final UriMatcher sUriMatcher = new UriMatcher(
		UriMatcher.NO_MATCH );
	static {
		sUriMatcher.addURI( CONTENT, PATH_LATEST, CODE_LATEST );
		sUriMatcher.addURI( CONTENT, PATH_STATUS, CODE_STATUS );
		sUriMatcher.addURI( CONTENT, PATH_STATUS + "/#", CODE_STATUS_ID );
		sUriMatcher.addURI( CONTENT, PATH_ERROR, CODE_ERROR );
		sUriMatcher.addURI( CONTENT, PATH_ERROR + "/#", CODE_ERROR_ID );
		sUriMatcher.addURI( CONTENT, PATH_NOTIFICATION, CODE_NOTIFICATION );
		sUriMatcher.addURI( CONTENT, PATH_NOTIFICATION + "/#",
							CODE_NOTIFICATION_ID );
		sUriMatcher.addURI( CONTENT, PATH_CONTROLLER, CODE_CONTROLLER );
		sUriMatcher.addURI( CONTENT, PATH_CONTROLLER + "/#", CODE_CONTROLLER_ID );
	}

	@Override
	public boolean onCreate ( ) {
		data = new RADbHelper( getContext() );
		return ((data == null) ? false : true);
	}

	@Override
	public Cursor query (
			Uri uri,
			String[] projection,
			String selection,
			String[] selectionArgs,
			String sortOrder ) {

		String limit = null;
		String table = null;
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
		switch ( sUriMatcher.match( uri ) ) {
			case CODE_LATEST:
				// limit to the most recent entry
				limit = "1";
				table = StatusTable.TABLE_NAME;
				break;
			case CODE_STATUS:
				// no limits, returns all the data in reverse order
				// the passed in query will be the limiting factor
				table = StatusTable.TABLE_NAME;
				break;
			case CODE_STATUS_ID:
				// only get the specified id
				table = StatusTable.TABLE_NAME;
				qb.appendWhere( StatusTable.COL_ID + "="
								+ uri.getLastPathSegment() );
				break;
			case CODE_ERROR: // no limits
				table = ErrorTable.TABLE_NAME;
				break;
			case CODE_ERROR_ID:
				table = ErrorTable.TABLE_NAME;
				qb.appendWhere( ErrorTable.COL_ID + "="
								+ uri.getLastPathSegment() );
				break;
			case CODE_NOTIFICATION: // no limits
				table = NotificationTable.TABLE_NAME;
				break;
			case CODE_NOTIFICATION_ID:
				table = NotificationTable.TABLE_NAME;
				qb.appendWhere( NotificationTable.COL_ID + "="
								+ uri.getLastPathSegment() );
				break;
            case CODE_CONTROLLER: // no limits
                table = ControllersTable.TABLE_NAME;
                break;
            case CODE_CONTROLLER_ID:
                table = ControllersTable.TABLE_NAME;
                qb.appendWhere( ControllersTable.COL_CONTROLLER_ID + "="
                                + uri.getLastPathSegment() );
                break;
			default:
				throw new IllegalArgumentException( "Uknown URI: " + uri );
		}
		qb.setTables( table );
		SQLiteDatabase db = data.getWritableDatabase();
		Cursor c =
				qb.query(	db, projection, selection, selectionArgs, null,
							null,
							sortOrder, limit );
		c.setNotificationUri( getContext().getContentResolver(), uri );
		return c;
	}

	@Override
	public String getType ( Uri uri ) {
		int match = sUriMatcher.match( uri );
		switch ( match ) {
			case CODE_LATEST:
				return LATEST_MIME_TYPE;
			case CODE_STATUS:
				return STATUS_MIME_TYPE;
			case CODE_STATUS_ID:
				return STATUS_ID_MIME_TYPE;
			case CODE_ERROR:
				return ERROR_MIME_TYPE;
			case CODE_ERROR_ID:
				return ERROR_ID_MIME_TYPE;
			case CODE_NOTIFICATION:
				return NOTIFICATION_MIME_TYPE;
			case CODE_NOTIFICATION_ID:
				return NOTIFICATION_ID_MIME_TYPE;
            case CODE_CONTROLLER:
                return CONTROLLER_MIME_TYPE;
            case CODE_CONTROLLER_ID:
                return CONTROLLER_ID_MIME_TYPE;
			default:
		}
		return null;
	}

	@Override
	public Uri insert ( Uri uri, ContentValues cv ) {
		SQLiteDatabase db = data.getWritableDatabase();
		long id = 0;
		String path = null;
		switch ( sUriMatcher.match( uri ) ) {
			case CODE_STATUS:
				id = db.insert( StatusTable.TABLE_NAME, null, cv );
				path = PATH_STATUS;
				break;
			case CODE_ERROR:
				id = db.insert( ErrorTable.TABLE_NAME, null, cv );
				path = PATH_ERROR;
				break;
			case CODE_NOTIFICATION:
				id = db.insert( NotificationTable.TABLE_NAME, null, cv );
				path = PATH_NOTIFICATION;
				break;
            case CODE_CONTROLLER:
                // TODO verify insertion of Controller into ControllersTable works
                id = db.insert( ControllersTable.TABLE_NAME, null, cv );
                path = PATH_CONTROLLER;
                break;
			default:
				throw new IllegalArgumentException( "Unknown URI: " + uri );
		}
		getContext().getContentResolver().notifyChange( uri, null );
		return Uri.parse( path + "/" + id );
	}

	@Override
	public int delete ( Uri uri, String selection, String[] selectionArgs ) {
		int rowsDeleted = 0;
		SQLiteDatabase db = data.getWritableDatabase();
		switch ( sUriMatcher.match( uri ) ) {
			case CODE_STATUS:
				rowsDeleted =
						db.delete(	StatusTable.TABLE_NAME, selection,
									selectionArgs );
				break;
			case CODE_STATUS_ID:
				rowsDeleted =
						db.delete(	StatusTable.TABLE_NAME, StatusTable.COL_ID
															+ "=?",
									new String[] { uri.getLastPathSegment() } );
				break;
			case CODE_ERROR:
				rowsDeleted =
						db.delete(	ErrorTable.TABLE_NAME, selection,
									selectionArgs );
				break;
			case CODE_ERROR_ID:
				rowsDeleted =
						db.delete(	ErrorTable.TABLE_NAME, ErrorTable.COL_ID
															+ "=?",
									new String[] { uri.getLastPathSegment() } );
				break;
			case CODE_NOTIFICATION:
				rowsDeleted =
						db.delete(	NotificationTable.TABLE_NAME, selection,
									selectionArgs );
				break;
			case CODE_NOTIFICATION_ID:
				rowsDeleted =
						db.delete(	NotificationTable.TABLE_NAME,
									NotificationTable.COL_ID + "=?",
									new String[] { uri.getLastPathSegment() } );
				break;
            case CODE_CONTROLLER_ID:
                // TODO verify deletion of Controller from ControllersTable works
                rowsDeleted =
                        db.delete( ControllersTable.TABLE_NAME,
                                    ControllersTable.COL_CONTROLLER_ID + "=?",
                                    new String[] { uri.getLastPathSegment() } );
                /* TODO need to delete all references in other tables to the deleted controller
                When deleting a controller, we need to delete all of the following:
                    * errors
                    * notifications
                    * status
                    * labels
                    * enabled ports
                    * probes visibility
                 related to the deleted controller
                 */
                break;
			default:
				throw new IllegalArgumentException( "Unknown URI: " + uri );
		}
		getContext().getContentResolver().notifyChange( uri, null );
		return rowsDeleted;
	}

	public int update (
			Uri uri,
			ContentValues values,
			String selection,
			String[] selectionArgs ) {
		int rowsUpdated = 0;
		SQLiteDatabase db = data.getWritableDatabase();
		switch ( sUriMatcher.match( uri ) ) {
			case CODE_ERROR:
				rowsUpdated =
						db.update(	ErrorTable.TABLE_NAME, values, selection,
									selectionArgs );
				break;
			case CODE_NOTIFICATION:
				rowsUpdated =
						db.update(	NotificationTable.TABLE_NAME, values,
									selection, selectionArgs );
				break;
            case CODE_CONTROLLER:
                // TODO verify updating of Controller in ControllersTable works
                rowsUpdated =
                        db.update( ControllersTable.TABLE_NAME, values,
                                    selection, selectionArgs);
                break;
			case CODE_STATUS_ID:
				rowsUpdated =
						db.update( StatusTable.TABLE_NAME, values,
								StatusTable.COL_ID + "=?",
								new String[] { uri.getLastPathSegment() } );
				break;
			default:
				throw new IllegalArgumentException( "Unknown Update URI: "
													+ uri );
		}
		getContext().getContentResolver().notifyChange( uri, null );
		return rowsUpdated;
	}

}
