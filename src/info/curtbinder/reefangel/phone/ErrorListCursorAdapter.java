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

	// Could improve this by putting an E or a N icon at the start
	// of the line
	private final int LAYOUT = R.layout.errorslistitem;

	static class ViewHolder {
		TextView date;
		TextView msg;
	}

	public ErrorListCursorAdapter ( Context context, Cursor c, int flags ) {
		super( context, c, flags );
	}

	@Override
	public void bindView ( View view, Context context, Cursor cursor ) {
		final ViewHolder vh = (ViewHolder) view.getTag();
		setViews( vh, cursor );
	}

	@Override
	public View newView ( Context context, Cursor cursor, ViewGroup parent ) {
		final LayoutInflater inflater = LayoutInflater.from( context );
		View v = inflater.inflate( LAYOUT, parent, false );
		ViewHolder vh = new ViewHolder();
		vh.date = (TextView) v.findViewById( R.id.error_date );
		vh.msg = (TextView) v.findViewById( R.id.error_message );
		setViews( vh, cursor );
		v.setTag( vh );
		return v;
	}

	private void setViews ( ViewHolder v, Cursor c ) {
		v.msg.setText( c.getString( c.getColumnIndex( ErrorTable.COL_MESSAGE ) ) );
		v.date.setText( RAApplication.getFancyDate( c.getLong( c
				.getColumnIndex( ErrorTable.COL_TIME ) ) ) );
	}
}
