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

public class DialogDCPump extends DialogFragment
        implements SeekBar.OnSeekBarChangeListener {

    public static final int DCPUMP_UPPER_MODES_OFFSET = 4;
    private static final String TAG = DialogDCPump.class.getSimpleName();
    private static final String TYPE_KEY = "type_key";
    private static final String VALUE_KEY = "value_key";

    private static final int MAX_SPEED_VALUE = 100;
    private static final int MAX_DURATION_VALUE = 255;

    private int popupType;
    private int currentValue;
    private int[] locations;

    private Spinner spinner;
    private SeekBar seek;
    private TextView tvValue;

    public DialogDCPump() {
        currentValue = 0;
        popupType = Controller.DCPUMP_MODE;
        // memory locations for the types:  MODE, SPEED, DURATION, THRESHOLD
        locations = new int[]{337, 338, 339, 364};
    }

    public static DialogDCPump newInstance(int type, int value) {
        DialogDCPump d = new DialogDCPump();
        Bundle args = new Bundle();
        args.putInt(TYPE_KEY, type);
        args.putInt(VALUE_KEY, value);
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
        }
        validateArguments();
        int layoutId = R.layout.dlg_mds_seekbar;
        if (popupType == Controller.DCPUMP_MODE) {
            layoutId = R.layout.dlg_mds_spinner;
        }
        View root = inflater.inflate(layoutId, null);
        updateDialogType(root);
        builder.setTitle(R.string.titleDCPump)
                .setView(root)
                .setPositiveButton(R.string.buttonUpdate, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        updateDCPumpSettings();
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
        if ((popupType > Controller.DCPUMP_THRESHOLD) ||
                (popupType < Controller.DCPUMP_MODE)) {
            popupType = Controller.DCPUMP_MODE;
        }
        if (popupType == Controller.DCPUMP_MODE) {
            // make sure the value is within the range
            if ((currentValue < 0) ||
                    ((currentValue > 6) && (currentValue < 11)) ||
                    (currentValue > 14)) {
                currentValue = 0;
            }

            // the value being passed in will be the exact value that is stored
            // in the parameters database from the controller. we need to adjust the offset
            // here before displaying the dialog box. the offset will be adjusted again
            // before se send the value out to the controller.  see getModeValue()
            if ((currentValue > 10) && (currentValue < 15)) {
                currentValue = currentValue - DCPUMP_UPPER_MODES_OFFSET;
            }
        }
        if ((popupType == Controller.DCPUMP_SPEED) ||
                (popupType == Controller.DCPUMP_THRESHOLD)) {
            if ((currentValue > MAX_SPEED_VALUE) ||
                    (currentValue < 0)) {
                currentValue = 0;
            }
        }
        if (popupType == Controller.DCPUMP_DURATION) {
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
                popupType = Controller.DCPUMP_MODE;
            case Controller.DCPUMP_MODE:
                descriptionId = R.string.descriptionMode;
                break;
            case Controller.DCPUMP_SPEED:
                descriptionId = R.string.descriptionSpeed;
                fSpeed = true;
                break;
            case Controller.DCPUMP_DURATION:
                descriptionId = R.string.descriptionDuration;
                break;
            case Controller.DCPUMP_THRESHOLD:
                descriptionId = R.string.descriptionThreshold;
                fSpeed = true;
                break;
        }
        ((TextView) v.findViewById(R.id.textDescription)).setText(descriptionId);
        if (popupType == Controller.DCPUMP_MODE) {
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
        String[] labels = getResources().getStringArray(R.array.dcPumpModeLabels);
        for (String label : labels) {
            data.add(addData(label));
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
        if (popupType == Controller.DCPUMP_SPEED) {
            fmt = "%d%%";
        } else {
            fmt = "%d";
        }
        tvValue.setText(String.format(Locale.getDefault(), fmt, currentValue));
    }

    private int getModeValue() {
        /*
        modes 0-6 return the exact position value.
        modes 7-10 return the value + 4
         */
        int v = spinner.getSelectedItemPosition();
        if ( v > 6 ) {
            v += DCPUMP_UPPER_MODES_OFFSET;
        }
        return v;
    }

    private void updateDCPumpSettings() {
        // get the starting memory locations
        Intent i = new Intent(getActivity(), UpdateService.class);
        i.setAction(MessageCommands.MEMORY_SEND_INTENT);

        // grab the current value from the dialog
        // use the spinner for the MODES and use the seek bar for the SPEED & DURATION
        // could use the currentValue variable because it is updated each time
        int value;
        if (popupType == Controller.DCPUMP_MODE) {
            value = getModeValue();
        } else {
            value = seek.getProgress();
        }
        i.putExtra(MessageCommands.MEMORY_SEND_TYPE_STRING, RequestCommands.MemoryByte);
        i.putExtra(MessageCommands.MEMORY_SEND_LOCATION_INT, locations[popupType]);
        i.putExtra(MessageCommands.MEMORY_SEND_VALUE_INT, value);
        Log.d(TAG, "Update DC Pump:  " + RequestCommands.MemoryByte + locations[popupType] + "," + value);
        // send the memory command
        UpdateService.enqueueWork(getActivity(), i);
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
