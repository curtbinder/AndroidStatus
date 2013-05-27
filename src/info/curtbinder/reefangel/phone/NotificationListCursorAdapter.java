/*
 * Copyright (c) 2011-2013 by Curt Binder (http://curtbinder.info)
 * 
 * This work is made available under the terms of the Creative Commons
 * Attribution-NonCommercial-ShareAlike 3.0 Unported License
 * http://creativecommons.org/licenses/by-nc-sa/3.0/
 */

package info.curtbinder.reefangel.phone;

import java.util.Locale;

import info.curtbinder.reefangel.db.NotificationTable;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class NotificationListCursorAdapter extends CursorAdapter {

	private final int LAYOUT = R.layout.notificationslistitem;

	static private String[] deviceParameters;
	static private String[] notifyConditions;

	static class ViewHolder {
		TextView item;
	}

	public NotificationListCursorAdapter ( Context context, Cursor c, int flags ) {
		super( context, c, flags );
		final Resources r = context.getResources();
		deviceParameters = r.getStringArray( R.array.deviceParameters );
		notifyConditions = r.getStringArray( R.array.notifyConditions );
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
		vh.item = (TextView) v.findViewById( R.id.notification_item );
		setViews( vh, cursor );
		v.setTag( vh );
		return v;
	}

	private void setViews ( ViewHolder v, Cursor c ) {
		int param = c.getInt( c.getColumnIndex( NotificationTable.COL_PARAM ) );
		int cond =
				c.getInt( c.getColumnIndex( NotificationTable.COL_CONDITION ) );

		String s =
				String.format(	Locale.US,
								"%s %s %s",
								deviceParameters[param],
								notifyConditions[cond],
								c.getString( c
										.getColumnIndex( NotificationTable.COL_VALUE ) ) );
		v.item.setText( s );
	}
}
