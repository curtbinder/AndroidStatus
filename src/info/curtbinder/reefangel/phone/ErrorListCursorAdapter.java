/*
 * Copyright (c) 2011-2013 by Curt Binder (http://curtbinder.info)
 * 
 * This work is made available under the terms of the Creative Commons
 * Attribution-NonCommercial-ShareAlike 3.0 Unported License
 * http://creativecommons.org/licenses/by-nc-sa/3.0/
 */

package info.curtbinder.reefangel.phone;

import info.curtbinder.reefangel.db.ErrorTable;
import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.support.v4.widget.CursorAdapter;

public class ErrorListCursorAdapter extends CursorAdapter {

	private final int LAYOUT = R.layout.errorslistitem;

	public ErrorListCursorAdapter ( Context context, Cursor c, int flags ) {
		super( context, c, flags );
	}

	@Override
	public void bindView ( View view, Context context, Cursor cursor ) {
		findAndSetViews( view, cursor );
	}

	@Override
	public View newView ( Context context, Cursor cursor, ViewGroup parent ) {
		final LayoutInflater inflater = LayoutInflater.from( context );
		View v = inflater.inflate( LAYOUT, parent, false );
		findAndSetViews( v, cursor );
		return v;
	}

	private void findAndSetViews ( View v, Cursor c ) {
		TextView date = (TextView) v.findViewById( R.id.error_date );
		TextView msg = (TextView) v.findViewById( R.id.error_message );
		if ( msg != null ) {
			msg.setText( c.getString( c.getColumnIndex( ErrorTable.COL_MESSAGE ) ) );
		}
		if ( date != null ) {
			date.setText( RAApplication.getFancyDate( c.getLong( c
					.getColumnIndex( ErrorTable.COL_TIME ) ) ) );
		}
	}
}
