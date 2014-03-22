/*
 * Copyright (c) 2011-2013 by Curt Binder (http://curtbinder.info)
 * 
 * This work is made available under the terms of the Creative Commons
 * Attribution-NonCommercial-ShareAlike 3.0 Unported License
 * http://creativecommons.org/licenses/by-nc-sa/3.0/
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
        String a = StatusProvider.class.getCanonicalName();
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

	// Used for the UriMatcher
	private static final int CODE_LATEST = 10;
	private static final int CODE_STATUS = 11;
	private static final int CODE_STATUS_ID = 12;
	private static final int CODE_ERROR = 13;
	private static final int CODE_ERROR_ID = 14;
	private static final int CODE_NOTIFICATION = 15;
	private static final int CODE_NOTIFICATION_ID = 16;
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
			default:
				throw new IllegalArgumentException( "Unknown Update URI: "
													+ uri );
		}
		getContext().getContentResolver().notifyChange( uri, null );
		return rowsUpdated;
	}

}
