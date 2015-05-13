/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Curt Binder
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
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import info.curtbinder.reefangel.service.MessageCommands;
import info.curtbinder.reefangel.service.UpdateService;

public class DialogCustomVar extends DialogFragment {

    private static final String TAG = DialogCustomVar.class.getSimpleName();
    private static final String CHANNEL_KEY = "channel_key";
    private static final String VALUE_KEY = "value_key";
    private static final String MESSAGE_KEY = "message_key";

    private TextView tvTitle;
    private EditText editValue;
    private int varChannel;
    private int currentValue;

    public DialogCustomVar() {
        varChannel = 0;
        currentValue = 0;
    }

    public static DialogCustomVar newInstance(int channel, short value, String msg) {
        DialogCustomVar d = new DialogCustomVar();
        Bundle args = new Bundle();
        args.putInt(CHANNEL_KEY, channel);
        args.putShort(VALUE_KEY, value);
        args.putString(MESSAGE_KEY, msg);
        d.setArguments(args);
        return d;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // TODO improve getting current theme
        final int themeId = R.style.AlertDialogStyle;
        final ContextThemeWrapper themeWrapper = new ContextThemeWrapper(getActivity(), themeId);
        LayoutInflater inflater = getActivity().getLayoutInflater().cloneInContext(themeWrapper);
        AlertDialog.Builder builder = new AlertDialog.Builder(themeWrapper, themeId);
        View root = inflater.inflate(R.layout.dlg_custom_var, null);
        findViews(root);
        Bundle args = getArguments();
        if (args != null) {
            varChannel = args.getInt(CHANNEL_KEY);
            currentValue = args.getShort(VALUE_KEY);
            tvTitle.setText(args.getString(MESSAGE_KEY));
        }

        editValue.setText(String.format("%d", currentValue));
        builder.setView(root)
                .setTitle(R.string.labelCustomVariables)
                .setPositiveButton(R.string.buttonSet, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        buttonSetClick();
                    }
                })
                .setNegativeButton(R.string.buttonCancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dismiss();
                    }
                });
        return builder.create();
    }

    private void findViews(View v) {
        tvTitle = (TextView) v.findViewById(R.id.textCustomTitle);
        editValue = (EditText) v.findViewById(R.id.editCustomValue);
    }

    private void buttonSetClick() {
        if (!updateAndValidateValue()) {
            // invalid new value, do not proceed
            Toast.makeText(getActivity(),
                    getString(R.string.messageInvalidRangeFormat,
                            Globals.BYTE_MIN, Globals.BYTE_MAX),
                    Toast.LENGTH_SHORT).show();
            return;
        }
        updateCustomVar(currentValue);
    }

    private boolean updateAndValidateValue() {
        String s = editValue.getText().toString();
        int newValue = Integer.parseInt(s);
        boolean fValid = false;
        if ( (newValue >= Globals.BYTE_MIN) && (newValue <= Globals.BYTE_MAX) ) {
            fValid = true;
        }
        if (fValid) {
            currentValue = newValue;
        }
        return fValid;
    }

    private void updateCustomVar(int value) {
        Intent i = new Intent(getActivity(), UpdateService.class);
        i.setAction(MessageCommands.CUSTOMVAR_SEND_INTENT);
        i.putExtra(MessageCommands.CUSTOMVAR_SEND_CHANNEL_INT, varChannel);
        i.putExtra(MessageCommands.CUSTOMVAR_SEND_VALUE_INT, value);
        Log.d(TAG, "updateCustomVar: channel: " + varChannel + ", value: " + value);
        getActivity().startService(i);
    }
}
