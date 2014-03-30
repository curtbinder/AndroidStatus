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
import android.content.res.Resources;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Locale;

import info.curtbinder.reefangel.db.NotificationTable;

/**
 * Created by binder on 3/23/14.
 */

public class NotificationListCursorAdapter extends CursorAdapter {

    static private String[] deviceParameters;
    static private String[] notifyConditions;
    private final int LAYOUT = R.layout.frag_notification_item;

    public NotificationListCursorAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
        final Resources r = context.getResources();
        deviceParameters = r.getStringArray(R.array.deviceParameters);
        notifyConditions = r.getStringArray(R.array.notifyConditions);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        final ViewHolder vh = (ViewHolder) view.getTag();
        setViews(vh, cursor);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        final LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(LAYOUT, parent, false);
        ViewHolder vh = new ViewHolder();
        vh.item = (TextView) v.findViewById(R.id.notification_item);
        setViews(vh, cursor);
        v.setTag(vh);
        return v;
    }

    private void setViews(ViewHolder v, Cursor c) {
        int param = c.getInt(c.getColumnIndex(NotificationTable.COL_PARAM));
        int cond = c.getInt(c.getColumnIndex(NotificationTable.COL_CONDITION));

        String s = String.format(Locale.US,
                "%s %s %s",
                deviceParameters[param],
                notifyConditions[cond],
                c.getString(c.getColumnIndex(NotificationTable.COL_VALUE)));
        v.item.setText(s);
    }

    static class ViewHolder {
        TextView item;
    }
}
