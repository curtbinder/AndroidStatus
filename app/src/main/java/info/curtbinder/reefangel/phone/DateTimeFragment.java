/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2012 Curt Binder
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

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;

import info.curtbinder.reefangel.controller.DateTime;
import info.curtbinder.reefangel.service.MessageCommands;
import info.curtbinder.reefangel.service.UpdateService;

public class DateTimeFragment extends Fragment
        implements View.OnClickListener {

    private static final String TAG = DateTimeFragment.class.getSimpleName();

    private Button getTimeButton;
    private Button setTimeButton;
    private Button changeDateButton;
    private Button changeTimeButton;
    private Button setCustomTimeButton;
    private TextView controllerTimeText;
    private TextView customTimeText;
    private TextView customDateText;
    private DateTime dt = new DateTime();
    private boolean fCustomDateSet = false;
    private boolean fCustomTimeSet = false;

    DateTimeReceiver receiver;
    IntentFilter filter;

    public static DateTimeFragment newInstance() {
        return new DateTimeFragment();
    }

    public DateTimeFragment() {
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.frag_datetime, container, false);
        createMessageReceiver();
        findViews(root);
        setOnClickListeners();
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().registerReceiver(receiver, filter, Permissions.SEND_COMMAND, null);
    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(receiver);
    }

    private void createMessageReceiver() {
        receiver = new DateTimeReceiver();
        filter = new IntentFilter(MessageCommands.DATE_QUERY_RESPONSE_INTENT);
        filter.addAction(MessageCommands.DATE_SEND_RESPONSE_INTENT);
    }

    private void findViews(View root) {
        getTimeButton = (Button) root.findViewById(R.id.timeButtonGetControllerTime);
        setTimeButton = (Button) root.findViewById(R.id.timeButtonSetCurrentTime);
        changeTimeButton = (Button) root.findViewById(R.id.timeButtonChangeTime);
        changeDateButton = (Button) root.findViewById(R.id.timeButtonChangeDate);
        setCustomTimeButton = (Button) root.findViewById(R.id.timeButtonSetCustomTime);
        controllerTimeText = (TextView) root.findViewById(R.id.timeTextControllerTime);
        customTimeText = (TextView) root.findViewById(R.id.timeTextCustomTime);
        customDateText = (TextView) root.findViewById(R.id.timeTextCustomDate);
    }

    private void setOnClickListeners() {
        getTimeButton.setOnClickListener(this);
        setTimeButton.setOnClickListener(this);
        changeTimeButton.setOnClickListener(this);
        changeDateButton.setOnClickListener(this);
        setCustomTimeButton.setOnClickListener(this);
    }

    public void onClick(View v) {
        Intent i = new Intent(getActivity(), UpdateService.class);
        boolean fSend = false;
        switch (v.getId()) {
            case R.id.timeButtonChangeDate:
                displayChangeDateDialog();
                break;
            case R.id.timeButtonChangeTime:
                displayChangeTimeDialog();
                break;
            case R.id.timeButtonGetControllerTime:
                //Log.d( TAG, "Get Time" );
                i.setAction(MessageCommands.DATE_QUERY_INTENT);
                fSend = true;
                break;
            case R.id.timeButtonSetCurrentTime:
                //Log.d( TAG, "Set Current Time" );
                dt.setWithCurrentDateTime();
                Log.d(TAG, "DT: " + dt.getDateTimeString());
                // clear the current time on the screen
                controllerTimeText.setText("");
                i.setAction(MessageCommands.DATE_SEND_INTENT);
                i.putExtra(MessageCommands.DATE_SEND_STRING, dt.getSetCommand());
                fSend = true;
                break;
            case R.id.timeButtonSetCustomTime:
                if (!canSetCustomTime()) {
                    return;
                }
                Log.d(TAG, "Custom DT: " + dt.getDateTimeString());
                controllerTimeText.setText("");
                i.setAction(MessageCommands.DATE_SEND_INTENT);
                i.putExtra(MessageCommands.DATE_SEND_STRING, dt.getSetCommand());
                fSend = true;
                break;
            default:
                return;
        }
        if (fSend) {
            getActivity().startService(i);
        }
    }

    private void displayChangeDateDialog() {
        final Calendar c = Calendar.getInstance();
        DatePickerDialog t;
        t = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                fCustomDateSet = true;
                dt.setYear(year);
                dt.setMonth(monthOfYear);
                dt.setDay(dayOfMonth);
                customDateText.setText(dt.getDateString());
            }
        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
        t.show();
    }

    private void displayChangeTimeDialog() {
        final Calendar c = Calendar.getInstance();
        TimePickerDialog t;
        t = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                fCustomTimeSet = true;
                dt.setHour(hourOfDay);
                dt.setMinute(minute);
                customTimeText.setText(dt.getTimeString());
            }
        }, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), true);
        t.show();
    }

    private boolean canSetCustomTime() {
        // both date and time must be set in order to proceed
        if (fCustomTimeSet && fCustomDateSet) {
            return true;
        }
        return false;
    }

    class DateTimeReceiver extends BroadcastReceiver {

        public void onReceive(Context context, Intent intent) {
            if (intent.getAction()
                    .equals(MessageCommands.DATE_QUERY_RESPONSE_INTENT)) {
                controllerTimeText
                        .setText(intent
                                .getStringExtra(MessageCommands.DATE_QUERY_RESPONSE_STRING));
            } else if (intent.getAction()
                    .equals(MessageCommands.DATE_SEND_RESPONSE_INTENT)) {
                Toast.makeText(getActivity(),
                        intent.getStringExtra(MessageCommands.DATE_SEND_RESPONSE_STRING),
                        Toast.LENGTH_SHORT).show();
            }
        }

    }
}
