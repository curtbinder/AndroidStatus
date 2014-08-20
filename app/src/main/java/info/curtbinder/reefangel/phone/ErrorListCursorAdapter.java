/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Curt Binder
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

package info.curtbinder.reefangel.phone;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import info.curtbinder.reefangel.db.ErrorTable;

public class ErrorListCursorAdapter extends CursorAdapter {

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
		View v = inflater.inflate( R.layout.frag_errors_item, parent, false );
		ViewHolder vh = new ViewHolder();
		vh.date = (TextView) v.findViewById( R.id.error_date );
		vh.msg = (TextView) v.findViewById( R.id.error_message );
		setViews( vh, cursor );
		v.setTag( vh );
		return v;
	}

	private void setViews ( ViewHolder v, Cursor c ) {
		v.msg.setText( c.getString( c.getColumnIndex( ErrorTable.COL_MESSAGE ) ) );
		v.date.setText( RAApplication.getFancyDate(
                c.getLong( c.getColumnIndex(ErrorTable.COL_TIME) ) ) );
	}
}
