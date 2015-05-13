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

import android.app.Dialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.ContextThemeWrapper;
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

public class DialogAddNotification extends DialogFragment {

    private static final String TAG = DialogAddNotification.class.getSimpleName();

    private Spinner paramSpinner;
    private Spinner condSpinner;
    private EditText valueText;
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

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // TODO improve getting current theme
        int themeId = R.style.AlertDialogStyle;
        final ContextThemeWrapper themeWrapper = new ContextThemeWrapper(getActivity(), themeId);
        LayoutInflater inflater = getActivity().getLayoutInflater().cloneInContext(themeWrapper);
        AlertDialog.Builder builder = new AlertDialog.Builder(themeWrapper, themeId);
        View root = inflater.inflate(R.layout.dlg_add_notification, null);
        findViews(root);
        setAdapters();
        Bundle args = getArguments();
        if (args != null) {
            uri = args.getParcelable(StatusProvider.NOTIFICATION_ID_MIME_TYPE);
            id = Long.parseLong(uri.getLastPathSegment());
            loadData();
        } else {
            id = -1;
        }
        builder.setView(root);
        builder.setPositiveButton(R.string.buttonSave, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                saveNotification();
            }
        });
        if (isUpdate()) {
            builder.setTitle(getString(R.string.titleUpdateNotification));
            builder.setNegativeButton(R.string.buttonDelete, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    deleteNotification();
                }
            });
            builder.setNeutralButton(R.string.buttonCancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dismiss();
                }
            });
        } else {
            builder.setTitle(getString(R.string.titleCreateNotification));
            builder.setNegativeButton(R.string.buttonCancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dismiss();
                }
            });
        }
        return builder.create();
    }

    private void findViews(View v) {
        paramSpinner = (Spinner) v.findViewById(R.id.notifyParameterSpin);
        condSpinner = (Spinner) v.findViewById(R.id.notifyConditionSpin);
        valueText = (EditText) v.findViewById(R.id.notifyValue);
    }

    private void setAdapters() {
        ArrayAdapter<CharSequence> p = ArrayAdapter.createFromResource(getActivity(),
                R.array.deviceParameters,
                android.R.layout.simple_spinner_item);
        p.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        paramSpinner.setAdapter(p);
        ArrayAdapter<CharSequence> c = ArrayAdapter.createFromResource(getActivity(),
                R.array.notifyConditions,
                android.R.layout.simple_spinner_item);
        c.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        condSpinner.setAdapter(c);
    }

    private boolean isUpdate() {
        return (id > -1);
    }

    private void loadData() {
        String[] projection = {
                NotificationTable.COL_PARAM,
                NotificationTable.COL_CONDITION,
                NotificationTable.COL_VALUE
        };
        Cursor c = getActivity().getContentResolver().query(uri, projection, null, null, null);
        if (c != null) {
            c.moveToFirst();

            if (c.moveToFirst()) {
                paramSpinner.setSelection(c.getInt(
                        c.getColumnIndex(NotificationTable.COL_PARAM)), true);
                condSpinner.setSelection(c.getInt(
                        c.getColumnIndex(NotificationTable.COL_CONDITION)), true);
                valueText.setText(c.getString(
                        c.getColumnIndex(NotificationTable.COL_VALUE)));
            }
            c.close();
        }
    }

    protected void saveNotification() {
        String s = valueText.getText().toString();
        Log.d(TAG, "Save Notification: " + paramSpinner.getSelectedItemPosition()
                + ", " + condSpinner.getSelectedItemPosition() + ", "
                + s);
        // do error checking to ensure that there is a value
        if (TextUtils.isEmpty(s)) {
            // Empty string
            Toast.makeText(getActivity(),
                    getResources().getString(R.string.messageEmptyValue),
                    Toast.LENGTH_SHORT).show();
            return;
        }

        ContentValues v = new ContentValues();
        v.put(NotificationTable.COL_PARAM, paramSpinner.getSelectedItemPosition());
        v.put(NotificationTable.COL_CONDITION, condSpinner.getSelectedItemPosition());
        v.put(NotificationTable.COL_VALUE, valueText.getText().toString());

        if (isUpdate()) {
            // update the values in the table
            getActivity().getContentResolver()
                    .update(NotificationsFragment.NOTIFY_URI, v, NotificationTable.COL_ID + "=?",
                            new String[]{Long.toString(id)});
        } else {
            // Insert the values in the table
            getActivity().getContentResolver().insert(NotificationsFragment.NOTIFY_URI, v);
        }
    }

    protected void deleteNotification() {
        Uri deleteUri = Uri.withAppendedPath(NotificationsFragment.NOTIFY_URI,
                Long.toString(id));
        getActivity().getContentResolver().delete(deleteUri, null, null);
    }
}
