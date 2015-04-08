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

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import info.curtbinder.reefangel.db.NotificationTable;
import info.curtbinder.reefangel.db.StatusProvider;

public class DialogAddNotification extends DialogFragment
    implements View.OnClickListener {

    private static final String TAG = DialogAddNotification.class.getSimpleName();

    private Spinner paramSpinner;
    private Spinner condSpinner;
    private EditText valueText;
    private Button saveButton;
    private Button cancelButton;
    private Button deleteButton;
    private long id;
    private Uri uri = null;

    public DialogAddNotification() {
    }

    public static DialogAddNotification newInstance() {
        return new DialogAddNotification();
    }

    public static DialogAddNotification newInstance(Uri uri) {
        DialogAddNotification d = DialogAddNotification.newInstance();
        Bundle args = new Bundle();
        args.putParcelable(StatusProvider.NOTIFICATION_ID_MIME_TYPE, uri);
        d.setArguments(args);
        return d;
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.dlg_add_notification, container);
        findViews(v);
        setAdapters();
        Bundle args = getArguments();
        if ( args != null ) {
            uri = args.getParcelable(StatusProvider.NOTIFICATION_ID_MIME_TYPE);
            id = Long.parseLong(uri.getLastPathSegment());
            loadData();
        } else {
            id = -1;
        }
        updateDisplay();
        return v;
    }

    private void findViews(View v) {
        paramSpinner = (Spinner) v.findViewById(R.id.notifyParameterSpin);
        condSpinner = (Spinner) v.findViewById(R.id.notifyConditionSpin);
        valueText = (EditText) v.findViewById(R.id.notifyValue);
        cancelButton = (Button) v.findViewById(R.id.cancelButton);
        cancelButton.setOnClickListener(this);
        deleteButton = (Button) v.findViewById(R.id.deleteButton);
        deleteButton.setOnClickListener(this);
        saveButton = (Button) v.findViewById(R.id.saveButton);
        saveButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch ( v.getId() ) {
            case R.id.deleteButton:
                deleteNotification();
                break;
            case R.id.saveButton:
                saveNotification();
                break;
        }
        dismiss();
    }

    private void setAdapters ( ) {
        ArrayAdapter<CharSequence> p = ArrayAdapter.createFromResource(getActivity(),
                        R.array.deviceParameters,
                        android.R.layout.simple_spinner_item);
        p.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item );
        paramSpinner.setAdapter( p );
        ArrayAdapter<CharSequence> c = ArrayAdapter.createFromResource(getActivity(),
                        R.array.notifyConditions,
                        android.R.layout.simple_spinner_item);
        c.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item );
        condSpinner.setAdapter( c );
    }

    private boolean isUpdate() {
        return (id >-1);
    }

    private void updateDisplay ( ) {
        // updates the buttons at the bottom of the window
        // based on the type of window we are displaying
        // If we are updating the notification item, then we display
        // Cancel, Delete, Save
        // If we are creating a new notification item, then we display
        // Cancel, Save

        // Set the title of the window
        // If updating, display Update Notification
        // If new, display Create Notification
        if ( isUpdate() ) {
            getDialog().setTitle(getString(R.string.titleUpdateNotification));
        } else {
            deleteButton.setVisibility( View.GONE );
            getDialog().setTitle(getString(R.string.titleCreateNotification));
        }
    }

    private void loadData ( ) {
        String[] projection = {
                NotificationTable.COL_PARAM,
                NotificationTable.COL_CONDITION,
                NotificationTable.COL_VALUE
        };
        Cursor c = getActivity().getContentResolver().query(uri, projection, null, null, null);
        if ( c != null ) {
            c.moveToFirst();

            if ( c.moveToFirst() ) {
                paramSpinner.setSelection(c.getInt(
                        c.getColumnIndex(NotificationTable.COL_PARAM) ), true );
                condSpinner.setSelection(c.getInt(
                        c.getColumnIndex(NotificationTable.COL_CONDITION)), true);
                valueText.setText( c.getString(
                        c.getColumnIndex(NotificationTable.COL_VALUE) ) );
            }
            c.close();
        }
    }

    protected void saveNotification ( ) {
        String s = valueText.getText().toString();
        Log.d(TAG, "Save Notification: " + paramSpinner.getSelectedItemPosition()
                        + ", " + condSpinner.getSelectedItemPosition() + ", "
                        + s);
        // do error checking to ensure that there is a value
        if ( TextUtils.isEmpty(s) ) {
            // Empty string
            Toast.makeText( getActivity(),
                    getResources().getString(R.string.messageEmptyValue),
                    Toast.LENGTH_SHORT ).show();
            return;
        }

        ContentValues v = new ContentValues();
        v.put(NotificationTable.COL_PARAM, paramSpinner.getSelectedItemPosition());
        v.put(NotificationTable.COL_CONDITION, condSpinner.getSelectedItemPosition());
        v.put(NotificationTable.COL_VALUE, valueText.getText().toString());

        if ( isUpdate() ) {
            // update the values in the table
            getActivity().getContentResolver()
                    .update( NotificationsFragment.NOTIFY_URI, v, NotificationTable.COL_ID + "=?",
                            new String[] { Long.toString( id ) } );
        } else {
            // Insert the values in the table
            getActivity().getContentResolver().insert( NotificationsFragment.NOTIFY_URI, v );
        }
    }

    protected void deleteNotification ( ) {
        Uri deleteUri = Uri.withAppendedPath( NotificationsFragment.NOTIFY_URI,
                Long.toString( id ) );
        getActivity().getContentResolver().delete( deleteUri, null, null );
    }
}
