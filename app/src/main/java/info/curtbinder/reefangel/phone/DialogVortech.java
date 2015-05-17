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
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import info.curtbinder.reefangel.controller.Controller;
import info.curtbinder.reefangel.service.MessageCommands;
import info.curtbinder.reefangel.service.RequestCommands;
import info.curtbinder.reefangel.service.UpdateService;

public class DialogVortech extends DialogFragment
        implements SeekBar.OnSeekBarChangeListener {

    private static final String TAG = DialogVortech.class.getSimpleName();
    private static final String TYPE_KEY = "type_key";
    private static final String VALUE_KEY = "value_key";

    private static final int LOCATION_OFFSET = 55;
    private static final int MAX_SPEED_VALUE = 100;
    private static final int MAX_DURATION_VALUE = 255;

    private int popupType;
    private int currentValue;
    private boolean preLocations;

    private Spinner spinner;
    private SeekBar seek;
    private TextView tvValue;

    public DialogVortech() {
        preLocations = false;
        currentValue = 0;
        popupType = Controller.VORTECH_MODE;
    }

    public static DialogVortech newInstance(int type, int value, boolean preLocations) {
        DialogVortech d = new DialogVortech();
        Bundle args = new Bundle();
        args.putInt(TYPE_KEY, type);
        args.putInt(VALUE_KEY, value);
        args.putBoolean(Globals.PRE10_LOCATIONS, preLocations);
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
        Bundle args = getArguments();
        if (args != null) {
            popupType = args.getInt(TYPE_KEY);
            currentValue = args.getInt(VALUE_KEY);
            preLocations = args.getBoolean(Globals.PRE10_LOCATIONS);
        }
        validateArguments();
        int layoutId = R.layout.dlg_mds_seekbar;
        if (popupType == Controller.VORTECH_MODE) {
            layoutId = R.layout.dlg_mds_spinner;
        }
        View root = inflater.inflate(layoutId, null);
        updateDialogType(root);
        builder.setTitle(R.string.titleVortech)
                .setView(root)
                .setPositiveButton(R.string.buttonUpdate, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        updateVortechSettings();
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

    private void validateArguments() {
        // validate the input arguments
        if ((popupType > Controller.VORTECH_DURATION) ||
                (popupType < Controller.VORTECH_MODE)) {
            popupType = Controller.VORTECH_MODE;
        }
        if (popupType == Controller.VORTECH_MODE) {
            if ((currentValue < 0) ||
                    (currentValue > 11)) {
                currentValue = 0;
            }
        }
        if (popupType == Controller.VORTECH_SPEED) {
            if ((currentValue > MAX_SPEED_VALUE) ||
                    (currentValue < 0)) {
                currentValue = 0;
            }
        }
        if (popupType == Controller.VORTECH_DURATION) {
            if ((currentValue > MAX_DURATION_VALUE) ||
                    (currentValue < 0)) {
                currentValue = 0;
            }
        }
    }

    private void updateDialogType(View v) {
        // updates the type of the dialog
        // updates the description and configures the spinner
        int descriptionId;
        boolean fSpeed = false;
        switch (popupType) {
            default:
                // in case of an invalid type, default to the MODE config
                popupType = Controller.VORTECH_MODE;
            case Controller.VORTECH_MODE:
                descriptionId = R.string.descriptionMode;
                break;
            case Controller.VORTECH_SPEED:
                descriptionId = R.string.descriptionSpeed;
                fSpeed = true;
                break;
            case Controller.VORTECH_DURATION:
                descriptionId = R.string.descriptionDuration;
                break;
        }
        ((TextView) v.findViewById(R.id.textDescription)).setText(descriptionId);
        if (popupType == Controller.VORTECH_MODE) {
            spinner = (Spinner) v.findViewById(R.id.vtSpinner);
            setupSpinner();
        } else {
            seek = (SeekBar) v.findViewById(R.id.vtSeek);
            tvValue = (TextView) v.findViewById(R.id.vtSeekValue);
            setupSeekBar(fSpeed);
        }
    }

    private void setupSpinner() {
        List<Map<String, String>> data = new ArrayList<Map<String, String>>();
        String[] from = new String[]{"data"};
        int[] to = new int[]{android.R.id.text1};
        String[] labels = getResources().getStringArray(R.array.vortechModeLabels);
        // only modes 0 - 11
        for (int i = 0; i < 12; i++) {
            data.add(addData(labels[i]));
        }
        SimpleAdapter simpleAdapter = new SimpleAdapter(getActivity(), data,
                android.R.layout.simple_spinner_item, from, to);
        simpleAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(simpleAdapter);
        spinner.setSelection(currentValue);
    }

    private Map<String, String> addData(String value) {
        Map<String, String> mapList = new HashMap<String, String>();
        mapList.put("data", value);
        return mapList;
    }

    private void setupSeekBar(boolean fSpeed) {
        int max;
        if (fSpeed) {
            max = MAX_SPEED_VALUE; //100;
        } else {
            max = MAX_DURATION_VALUE; //255;
        }
        seek.setMax(max);
        seek.setProgress(currentValue);
        seek.setOnSeekBarChangeListener(this);
        updateProgressText();
    }

    private void updateProgressText() {
        String fmt;
        if (popupType == Controller.VORTECH_SPEED) {
            fmt = "%d%%";
        } else {
            fmt = "%d";
        }
        tvValue.setText(String.format(Locale.getDefault(), fmt, currentValue));
    }

    private void updateVortechSettings() {
        // get the starting memory locations
        Intent i = new Intent(getActivity(), UpdateService.class);
        i.setAction(MessageCommands.MEMORY_SEND_INTENT);
        int location;
        if (preLocations) {
            location = MemoryFragment.LOCATION_START_OLD;
        } else {
            location = MemoryFragment.LOCATION_START;
        }
        location += LOCATION_OFFSET + popupType;

        // grab the current value from the dialog
        // use the spinner for the MODES and use the seek bar for the SPEED & DURATION
        // could use the currentValue variable because it is updated each time
        int value;
        if (popupType == Controller.VORTECH_MODE) {
            value = spinner.getSelectedItemPosition();
        } else {
            value = seek.getProgress();
        }
        i.putExtra(MessageCommands.MEMORY_SEND_TYPE_STRING, RequestCommands.MemoryByte);
        i.putExtra(MessageCommands.MEMORY_SEND_LOCATION_INT, location);
        i.putExtra(MessageCommands.MEMORY_SEND_VALUE_INT, value);
        Log.d(TAG, "Update Vortech:  " + RequestCommands.MemoryByte + location + "," + value);
        // send the memory command
        getActivity().startService(i);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        currentValue = progress;
        updateProgressText();
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}
