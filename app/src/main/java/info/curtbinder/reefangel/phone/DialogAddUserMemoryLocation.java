/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2019 Curt Binder
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

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.view.ContextThemeWrapper;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import info.curtbinder.reefangel.db.UserMemoryLocationsTable;

import static info.curtbinder.reefangel.phone.MemoryFragment.TabUserFrag.CONFIRM_DELETE;

public class DialogAddUserMemoryLocation extends DialogFragment {

    private static final String TAG = DialogAddUserMemoryLocation.class.getSimpleName();

    private EditText name;
    private EditText location;
    private RadioButton intButton;
    private RadioButton byteButton;

    private long id;

    public DialogAddUserMemoryLocation() {

    }

    public static DialogAddUserMemoryLocation newInstance() {
        return new DialogAddUserMemoryLocation();
    }

    public static DialogAddUserMemoryLocation newInstance(MemoryFragment.TabUserFrag.MemoryData memoryData) {
        DialogAddUserMemoryLocation d = DialogAddUserMemoryLocation.newInstance();
        Bundle args = new Bundle();
        args.putLong(UserMemoryLocationsTable.COL_ID, memoryData.id);
        args.putString(UserMemoryLocationsTable.COL_NAME, memoryData.name);
        args.putInt(UserMemoryLocationsTable.COL_LOCATION, memoryData.location);
        args.putInt(UserMemoryLocationsTable.COL_TYPE, memoryData.type);
        d.setArguments(args);
        return d;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstance) {
        final int themeId = R.style.AlertDialogStyle;
        final ContextThemeWrapper themeWrapper = new ContextThemeWrapper(getActivity(), themeId);
        LayoutInflater inflater = getActivity().getLayoutInflater().cloneInContext(themeWrapper);
        AlertDialog.Builder builder = new AlertDialog.Builder(themeWrapper, themeId);
        View root = inflater.inflate(R.layout.dlg_add_user_memory, null);
        findViews(root);
        Bundle args = getArguments();
        if (args != null) {
            loadData(args);
        } else {
            id = -1;
        }
        builder.setView(root);
        builder.setPositiveButton(R.string.buttonSave, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if ( ! canSave() ) {
                    return;
                }
                Intent intent = saveUserMemoryLocation();
                getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, intent);
            }
        });
        if (isUpdate()) {
            // TODO change hardcoded update title to use a string resource
            builder.setTitle("Update Location");
            builder.setNegativeButton(R.string.buttonDelete, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                    Intent intent = new Intent();
                    Bundle extras = new Bundle();
                    extras.putLong(UserMemoryLocationsTable.COL_ID, id);
                    intent.putExtras(extras);
                    getTargetFragment().onActivityResult(getTargetRequestCode(),
                            CONFIRM_DELETE, intent);
                }
            });
            builder.setNeutralButton(R.string.buttonCancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.cancel();
                }
            });
        } else {
            // TODO change hardcoded create location title to use string resource
            builder.setTitle("Add Location");
            builder.setNegativeButton(R.string.buttonCancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.cancel();
                }
            });
        }
        return builder.create();
    }

    private boolean isUpdate() {
        return (id > -1);
    }

    private void findViews(View v) {
        name = (EditText) v.findViewById(R.id.nameText);
        location = (EditText) v.findViewById(R.id.locationText);
        intButton = (RadioButton) v.findViewById(R.id.radioButtonInt);
        byteButton = (RadioButton) v.findViewById(R.id.radioButtonByte);
        // default to BYTE being selected
        ((RadioButton)v.findViewById(R.id.radioButtonByte)).setChecked(true);
    }

    private void loadData(Bundle args) {
        id = args.getLong(UserMemoryLocationsTable.COL_ID);
        String sName = args.getString(UserMemoryLocationsTable.COL_NAME);
        int iLocation = args.getInt(UserMemoryLocationsTable.COL_LOCATION);
        int type = args.getInt(UserMemoryLocationsTable.COL_TYPE);

        // Set the values
        name.setText(sName);
        location.setText(String.valueOf(iLocation));
        if ( type == 1 ) {
            intButton.setChecked(true);
            byteButton.setChecked(false);
        } else {
            intButton.setChecked(false);
            byteButton.setChecked(true);
        }
    }

    protected boolean canSave() {
        int iLocation = Integer.parseInt(location.getText().toString());
        if (TextUtils.isEmpty(name.getText().toString())) {
            Toast.makeText(getActivity(), "Name cannot be empty", Toast.LENGTH_SHORT).show();
            return false;
        }
        // TODO check memory locations, locations can be between 1 and 1023, bytes take 1 space, 2 for int
        if (iLocation < 1) {
            Toast.makeText(getActivity(), "Location must be between 1 and 1023", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    protected Intent saveUserMemoryLocation() {
        Log.d(TAG, "Save user location");
        String sName = name.getText().toString();
        int iLocation = Integer.parseInt(location.getText().toString());
        boolean fInt = intButton.isChecked();

        Log.d(TAG, "Save: " + sName + ", " + iLocation + ", " + fInt);
        Bundle extras = new Bundle();
        extras.putLong(UserMemoryLocationsTable.COL_ID, id);
        extras.putString(UserMemoryLocationsTable.COL_NAME, sName);
        extras.putInt(UserMemoryLocationsTable.COL_LOCATION, iLocation);
        extras.putBoolean(UserMemoryLocationsTable.COL_TYPE, fInt);
        return new Intent().putExtras(extras);
    }
}
