package info.curtbinder.reefangel.db;

/*
 * Copyright (c) 2011-12 by Curt Binder (http://curtbinder.info)
 *
 * This work is made available under the terms of the 
 * Creative Commons Attribution-NonCommercial-ShareAlike 3.0 Unported License
 * http://creativecommons.org/licenses/by-nc-sa/3.0/
 */

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

public class StatusProvider extends ContentProvider {

	private static String TAG = StatusProvider.class.getSimpleName();
	private RAData data;

	private static final String CONTENT = StatusProvider.class.getPackage()
			.getName();
	private static final String PATH_LATEST = "latest";
	private static final String PATH_STATUS = "status";
	public static final Uri CONTENT_URI = Uri.parse( "content://" + CONTENT );

	public static final String LATEST_MIME_TYPE =
			"vnd.android.cursor.item/vnd." + CONTENT + "." + PATH_LATEST;
	public static final String STATUS_ID_MIME_TYPE =
			"vnd.android.cursor.item/vnd." + CONTENT + "." + PATH_STATUS;
	public static final String STATUS_MIME_TYPE =
			"vnd.android.cursor.dir/vnd." + CONTENT + "." + PATH_STATUS;
	private static final int CODE_LATEST = 1;
	private static final int CODE_STATUS = 2;
	private static final int CODE_STATUS_ID = 3;
	private static final UriMatcher sUriMatcher = new UriMatcher(
		UriMatcher.NO_MATCH );
	{
		sUriMatcher.addURI( CONTENT, PATH_LATEST, CODE_LATEST );
		sUriMatcher.addURI( CONTENT, PATH_STATUS, CODE_STATUS );
		sUriMatcher.addURI( CONTENT, PATH_STATUS + "/#", CODE_STATUS_ID );
	}

	@Override
	public int delete ( Uri arg0, String arg1, String[] arg2 ) {
		Log.d( TAG, "Delete not implemented" );
		return 0;
	}

	@Override
	public String getType ( Uri url ) {
		int match = sUriMatcher.match( url );
		switch ( match ) {
			case CODE_LATEST:
				return LATEST_MIME_TYPE;
			case CODE_STATUS:
				return STATUS_MIME_TYPE;
			case CODE_STATUS_ID:
				return STATUS_ID_MIME_TYPE;
			default:
		}
		return null;
	}

	@Override
	public Uri insert ( Uri arg0, ContentValues arg1 ) {
		Log.d( TAG, "Insert not implemented" );
		return null;
	}

	@Override
	public boolean onCreate ( ) {
		data = new RAData( getContext() );
		return false;
	}

	@Override
	public Cursor query (
			Uri uri,
			String[] projection,
			String selection,
			String[] selectionArgs,
			String sortOrder ) {
		Log.d( TAG, "Query" );
		switch ( sUriMatcher.match( uri ) ) {
			case CODE_LATEST:
				return data.getLatestData();
			case CODE_STATUS:
				return data.getAllData();
			case CODE_STATUS_ID:
				String id = uri.getLastPathSegment();
				return data.getDataById( Long.parseLong( id ) );
		}
		return null;
	}

	@Override
	public int update ( Uri arg0, ContentValues arg1, String arg2, String[] arg3 ) {
		Log.d( TAG, "Update not implemented" );
		return 0;
	}

}
