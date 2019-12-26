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
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.Locale;

import info.curtbinder.reefangel.service.MessageCommands;
import info.curtbinder.reefangel.service.UpdateService;

public class DialogOverridePwm extends DialogFragment
        implements SeekBar.OnSeekBarChangeListener {

    private static final String TAG = DialogOverridePwm.class.getSimpleName();
    private static final String CHANNEL_KEY = "channel_key";
    private static final String VALUE_KEY = "value_key";
    private static final String MESSAGE_KEY = "message_key";

    private TextView tvTitle;
    private TextView tvValue;
    private int pwmChannel;
    private short currentValue;
    private SeekBar seek;

    public DialogOverridePwm() {
        pwmChannel = Globals.OVERRIDE_DAYLIGHT;
        currentValue = 0;
    }

    public static DialogOverridePwm newInstance(int channel, short value, String msg) {
        DialogOverridePwm d = new DialogOverridePwm();
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
        View root = inflater.inflate(R.layout.dlg_override_pwm, null);
        findViews(root);
        Bundle args = getArguments();
        if (args != null) {
            pwmChannel = args.getInt(CHANNEL_KEY);
            currentValue = args.getShort(VALUE_KEY);
            tvTitle.setText(args.getString(MESSAGE_KEY));
        }
        seek.setProgress(currentValue);
        updateProgressText();
        builder.setTitle(R.string.titleOverride)
                .setView(root)
                .setPositiveButton(R.string.buttonUpdate, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        updateOverride(currentValue);
                    }
                })
                .setNegativeButton(R.string.buttonCancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dismiss();
                    }
                })
                .setNeutralButton(R.string.buttonClear, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        updateOverride(Globals.OVERRIDE_DISABLE);
                    }
                });
        return builder.create();
    }

    private void findViews(View v) {
        tvTitle = (TextView) v.findViewById(R.id.textOverrideTitle);
        tvValue = (TextView) v.findViewById(R.id.textOverrideValue);
        seek = (SeekBar) v.findViewById(R.id.seekOverride);
        seek.setMax(Globals.OVERRIDE_MAX_VALUE);
        seek.setOnSeekBarChangeListener(this);
    }

   private void updateProgressText() {
        tvValue.setText(String.format(Locale.getDefault(), "%d%%", currentValue));
    }

    private void updateOverride(int value) {
        Intent i = new Intent(getActivity(), UpdateService.class);
        i.setAction(MessageCommands.OVERRIDE_SEND_INTENT);
        i.putExtra(MessageCommands.OVERRIDE_SEND_LOCATION_INT, pwmChannel);
        i.putExtra(MessageCommands.OVERRIDE_SEND_VALUE_INT, value);
        Log.d(TAG, "updateOverride: channel: " + pwmChannel + ", value: " + value);
        UpdateService.enqueueWork(getActivity(), i);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        currentValue = (short) progress;
        updateProgressText();
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}
