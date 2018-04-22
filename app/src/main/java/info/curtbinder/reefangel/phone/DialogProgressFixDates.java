/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2018 Curt Binder
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
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import info.curtbinder.reefangel.db.StatusProvider;
import info.curtbinder.reefangel.db.StatusTable;

public class DialogProgressFixDates extends DialogFragment {

    private static final String TAG = DialogProgressFixDates.class.getSimpleName();
    private ProgressBar bar;
    private TextView message;

    public DialogProgressFixDates() {
    }

    public static DialogProgressFixDates newInstance() {
        return new DialogProgressFixDates();
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart:  DialogProgressFixDates");
        AlertDialog dlg = (AlertDialog)getDialog();
        final Button cancelButton = dlg.getButton(AlertDialog.BUTTON_NEGATIVE);

        Handler h = new Handler();
        final Runnable r = new Runnable() {
            public void run() {
                fixLogDates();
                cancelButton.setText(R.string.buttonClose);
            }
        };
        h.post(r);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final int themeId = R.style.AlertDialogStyle;
        final ContextThemeWrapper themeWrapper = new ContextThemeWrapper(getActivity(), themeId);
        LayoutInflater inflater = getActivity().getLayoutInflater().cloneInContext(themeWrapper);
        AlertDialog.Builder builder = new AlertDialog.Builder(themeWrapper, themeId);
        View root = inflater.inflate(R.layout.dlg_progress_fixdates, null);
        bar = (ProgressBar) root.findViewById(R.id.progressFixDate);
        message = (TextView) root.findViewById(R.id.textFixDateMessage);
        updateMessage(getString(R.string.messageConvertingDates));
        builder.setTitle(R.string.titleFixDates)
                .setView(root)
                .setNegativeButton(R.string.buttonCancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dismiss();
                    }
                });
        return builder.create();
    }

    public void updateMessage(String msg) {
        message.setText(msg);
    }

    public void fixLogDates() {
        // Displays all the current dates in the database
        RAApplication rapp = (RAApplication)getActivity().getApplication();
        // open the conversion
        PrintWriter pw = rapp.openDateConversionFile();
        if ( pw == null ) {
            Log.d(TAG, "Unable to open conversion file for logging");
        }

        Uri uri = Uri.parse(StatusProvider.CONTENT_URI + "/"
                + StatusProvider.PATH_STATUS);
        final ContentResolver resolver = rapp.getContentResolver();

        // sqlite has no official "date" column type
        // use TEXT as ISO8601 formatted string ("YYYY-MM-DD HH:MM:SS.SSS")
        //
        // We need to do the following to convert the dates from our formatted string to the proper format,
        // so we are able to sort the dates for the history
        //   - retrieve all the dates, one at a time
        //   - examine the date to see if it is in our DEFAULT locale formatted date
        //   - if it is
        //     - parse the date & time into a universal formatted object
        //     - convert it to the ISO8601 format
        //     - update the database record with the new formatted date
        //   - if not
        //     - check if it is already in the ISO8601 format, if it is, skip it, if not just flag it

        String msg;
        DateFormat dftOld = Utils.getOldDefaultDateFormat();
        SimpleDateFormat dftProper = Utils.getDefaultDateFormat();
        String[] projection = {StatusTable.COL_ID, StatusTable.COL_LOGDATE};
        String sortOrder = StatusTable.COL_ID + " ASC";
        String formatted_date = "";
        String new_date = "";
        long id;
        Date d;

        msg = "Conversion begins at:  " + dftProper.format(Calendar.getInstance().getTime());
        rapp.logRepeatMsgDateConversion(pw, msg);

        Cursor c = resolver.query(uri, projection, null, null, sortOrder);
        if (c.moveToFirst()){
            int total_rows = c.getCount();
            msg = String.format(getString(R.string.messageParsingDates), total_rows);
            Log.d(TAG, msg);
            rapp.logRepeatMsgDateConversion(pw, msg);
            bar.setMax(total_rows);
            int counter = 0;
            do {
                counter++;
                // get the ID and formatted date
                id = c.getLong(c.getColumnIndex(StatusTable.COL_ID));
                formatted_date = c.getString(c.getColumnIndex(StatusTable.COL_LOGDATE));
                msg = String.format(getString(R.string.messageParsingDatesCount), counter, total_rows);
                bar.setProgress(counter);
                updateMessage(msg);

                // parse the date and convert it to the ISO8601 format
                try {
                    d = dftOld.parse(formatted_date);
                } catch (ParseException e) {
                    msg = "Error parsing date (" + id + "): " + formatted_date;
                    Log.d(TAG, msg);
                    rapp.logRepeatMsgDateConversion(pw, msg);
                    continue;
                }
                // create new date
                Calendar cal = Calendar.getInstance();
                cal.setTime(d);
                new_date = dftProper.format(cal.getTime());

                // display the old date and new date
                msg = "ID: " + id + ": '" + formatted_date + "' ==> '" + new_date + "'";
                Log.d(TAG, msg);
                rapp.logRepeatMsgDateConversion(pw, msg);

                // update the database
                ContentValues v = new ContentValues();
                // Store the new date in place of the old date
                v.put(StatusTable.COL_LOGDATE, new_date);
                Uri update_uri = Uri.parse(StatusProvider.CONTENT_URI + "/" + StatusProvider.PATH_STATUS + "/" + id);
                // this command handles the where in the DB from the URI
                resolver.update(update_uri, v, null, null);
                // this command handles the where in the update
                //resolver.update(uri, v, StatusTable.COL_ID + "=?", new String[]{Long.toString(id)});
            } while (c.moveToNext());
        } else {
            Log.d(TAG, "No entries found in Display Dates");
            rapp.logRepeatMsgDateConversion(pw, "No entries found in Display Dates");
        }
        c.close();

        msg = "Conversion ends at:  " + dftProper.format(Calendar.getInstance().getTime());
        rapp.logRepeatMsgDateConversion(pw, msg);

        // Close the conversion file
        rapp.closeDateConversionFile(pw);
    }
}
