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

import android.content.ContentValues;
import android.content.Intent;
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
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

import info.curtbinder.reefangel.db.NotificationTable;
import info.curtbinder.reefangel.db.StatusProvider;
import info.curtbinder.reefangel.service.MessageCommands;
import info.curtbinder.reefangel.service.UpdateService;

public class DialogOverridePwm extends DialogFragment
    implements View.OnClickListener, SeekBar.OnSeekBarChangeListener {

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

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.dlg_override_pwm, container);
        getDialog().setTitle(R.string.titleOverride);
        findViews(v);
        Bundle args = getArguments();
        if ( args != null ) {
            pwmChannel = args.getInt(CHANNEL_KEY);
            currentValue = args.getShort(VALUE_KEY);
            tvTitle.setText(args.getString(MESSAGE_KEY));
        }
        seek.setProgress(currentValue);
        updateProgressText();
        return v;
    }

    private void findViews(View v) {
        tvTitle = (TextView) v.findViewById(R.id.textOverrideTitle);
        tvValue = (TextView) v.findViewById(R.id.textOverrideValue);
        seek = (SeekBar) v.findViewById(R.id.seekOverride);
        seek.setMax(Globals.OVERRIDE_MAX_VALUE);
        seek.setOnSeekBarChangeListener(this);
        Button b = (Button) v.findViewById(R.id.buttonCancel);
        b.setOnClickListener(this);
        b = (Button) v.findViewById(R.id.buttonSetOverride);
        b.setOnClickListener(this);
        b = (Button) v.findViewById(R.id.buttonClearOverride);
        b.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch ( v.getId() ) {
            case R.id.buttonClearOverride:
                updateOverride(Globals.OVERRIDE_DISABLE);
                break;
            case R.id.buttonSetOverride:
                updateOverride(currentValue);
                break;
        }
        dismiss();
    }

    private void updateProgressText() {
        tvValue.setText(String.format(Locale.getDefault(), "%d%%", currentValue));
    }

    private void updateOverride(int value) {
        Intent i = new Intent( getActivity(), UpdateService.class );
        i.setAction( MessageCommands.OVERRIDE_SEND_INTENT );
        i.putExtra( MessageCommands.OVERRIDE_SEND_LOCATION_INT, pwmChannel );
        i.putExtra( MessageCommands.OVERRIDE_SEND_VALUE_INT, value );
        Log.d(TAG, "updateOverride: channel: " + pwmChannel + ", value: " + value);
        getActivity().startService(i);
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
