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

import android.app.Activity;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import info.curtbinder.reefangel.controller.Controller;
import info.curtbinder.reefangel.db.StatusProvider;
import info.curtbinder.reefangel.db.StatusTable;

public class HistoryGraphFragment extends Fragment {

    public static final String TAG = HistoryGraphFragment.class.getSimpleName();
    private LineChart chart;

    // There are a max of 3 values that can be charted
    private int[] valuesItemIndex = {1,0,0};
    // The date range item index
    private int dateRangeItemIndex = 0;    // Default to 1st item, which is 1 day

    String[] dataSetLabels;
    String[] dataSetValues;
    int[] dataSetPrecision;

    public HistoryGraphFragment() {
    }

    public static HistoryGraphFragment newInstance() {
        return new HistoryGraphFragment();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putIntArray("values_index", valuesItemIndex);
        outState.putInt("date_index", dateRangeItemIndex);
        Log.d(TAG, "saved: {" + valuesItemIndex[0] + ", " + valuesItemIndex[1] + ", " + valuesItemIndex[2] + ", " + dateRangeItemIndex + "}");
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if ( savedInstanceState != null ) {
            valuesItemIndex = savedInstanceState.getIntArray("values_index");
            dateRangeItemIndex = savedInstanceState.getInt("date_index");
        }
        Log.d(TAG, "activity created: {" + valuesItemIndex[0] + ", " + valuesItemIndex[1] + ", " + valuesItemIndex[2] + ", " + dateRangeItemIndex + "}");
    }

    @Override
    public void onResume() {
        super.onResume();
        displayChart();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu( true );
        dataSetLabels = getResources().getStringArray(R.array.chartDataSetNames);
        dataSetValues = getResources().getStringArray(R.array.chartDataSetValues);
        View root = inflater.inflate(R.layout.frag_history_chart, container, false);
        findViews(root);

        if ( savedInstanceState != null ) {
            valuesItemIndex = savedInstanceState.getIntArray("values_index");
            dateRangeItemIndex = savedInstanceState.getInt("date_index");
        }

        // Chart Configuration Settings
        // disable the description text
        chart.getDescription().setEnabled(false);
        // disable right axis
        chart.getAxisRight().setEnabled(false);

        return root;
    }

    @Override
    public void onCreateOptionsMenu (Menu menu, MenuInflater inflater ) {
        inflater.inflate( R.menu.frag_history_chart, menu );
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case DialogConfigureChart.CONFIGURE_CHART:
                if ( resultCode == Activity.RESULT_OK ) {
                    if ( data == null ) {
                        return;
                    }
                    updateChartSettings(data);
                    displayChart();
                }
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected ( MenuItem item ) {
        switch ( item.getItemId() ) {
            case R.id.action_configure_chart:
                Log.d(TAG, "Configure chart");
                // Launch dialog to configure the data
                DialogConfigureChart d1 = DialogConfigureChart.newInstance(valuesItemIndex[0],
                        valuesItemIndex[1], valuesItemIndex[2], dateRangeItemIndex);
                d1.setTargetFragment(this, DialogConfigureChart.CONFIGURE_CHART);
                d1.show(getFragmentManager(), "dlgconfigurechart");
                break;
            case R.id.action_refresh_chart:
                // Call loadData to reload the data based on the options selected
                displayChart();
                break;
            case R.id.action_save_chart:
                Log.d(TAG, "Save chart");
                // save with 80% quality
//                chart.saveToGallery("File", 80);
                break;
            case R.id.action_display_dates:
                Log.d(TAG, "Display Dates");
                fixLogDates();
                break;
        }
        return true;
    }

    private void findViews(View root) {
        chart = (LineChart) root.findViewById(R.id.line_chart);
    }

    private void updateChartSettings(Intent data) {
        // get the indices from the dialog for the values and date range
        valuesItemIndex[0] = data.getIntExtra(DialogConfigureChart.VALUES1, 1);
        valuesItemIndex[1] = data.getIntExtra(DialogConfigureChart.VALUES2, 0);
        valuesItemIndex[2] = data.getIntExtra(DialogConfigureChart.VALUES3, 0);
        dateRangeItemIndex = data.getIntExtra(DialogConfigureChart.DATE1, 0);
        Log.d(TAG, "Values: " + valuesItemIndex[0] + ", " + valuesItemIndex[1] + ", " +
        valuesItemIndex[2] + ", " + dateRangeItemIndex);
    }

    private void displayChart() {
        final Cursor c = loadData();
        if ( c == null ) {
            chart.setNoDataText(getResources().getString(R.string.messageNoChartData));
            chart.clear();
            return;
        }
        refreshChart(c);
    }

    @Nullable
    private Cursor loadData() {
        // Sanity check, ensure a parameter is chosen for charting
        if ( !isDataSetEnabled(0) && !isDataSetEnabled(1) && !isDataSetEnabled(2) ) {
            Log.d(TAG, "No data chosen, don't bother updating or reloading");
            return null;
        }

        Uri uri = Uri.parse(StatusProvider.CONTENT_URI + "/"
                        + StatusProvider.PATH_STATUS);
        final ContentResolver resolver = getActivity().getContentResolver();
        final String[] projection = getProjectionList();
        // Add LIMIT to restrict the quantity of the results
        String sortOrder = StatusTable.COL_ID;
        final String selection = getSelectionCriteria();

        // query the database for the values
        return resolver.query(uri, projection, selection, null, sortOrder);
    }

    private void refreshChart(Cursor c) {
        // Clear the chart first, then refresh the data
        chart.clear();

        // create a list of the data points for the chart
        List<Entry> v1 = new ArrayList<Entry>();
        List<Entry> v2 = new ArrayList<Entry>();
        List<Entry> v3 = new ArrayList<Entry>();
        final ArrayList<String> dates = new ArrayList<String>();
        float count = 0;

        float fX, fY1, fY2, fY3;
        if ( c.moveToFirst() ) {
            do {
                // we have data
                fX = count++;

                if ( isDataSetEnabled(0) ) {
                    fY1 = getActualValue(c, 0);
                    v1.add(new Entry(fX, fY1));
                }
                if ( isDataSetEnabled(1) ) {
                    fY2 = getActualValue(c, 1);
                    v2.add(new Entry(fX, fY2));
                }
                if ( isDataSetEnabled(2) ) {
                    fY3 = getActualValue(c, 2);
                    v3.add(new Entry(fX, fY3));
                }

                // TODO verify is the dates need to be in ISO format or "fancy" format
                dates.add(Utils.getDisplayDate(c.getString(c.getColumnIndex(StatusTable.COL_LOGDATE))));
            } while(c.moveToNext());
        } else {
            // no data
            chart.setNoDataText("No data available");
        }
        c.close();

        // TODO Update / improve the popup window, set the background using a drawable
        // Set the MarkerView, which is the popup when the user taps on a point
        RAMarkerView mv = new RAMarkerView(getActivity().getApplicationContext(), R.layout.ra_custom_marker, dates);
        mv.setChartView(chart);
        chart.setMarker(mv);

        // TODO change the formatting to only have the decimal point for the y-axis and data points
        XAxis xAxis = chart.getXAxis();
        xAxis.setDrawLabels(false);

        // TODO verify axis formatting values

        /*
        Create the main list for the Dataset for the chart
        If the any of the 3 available datasets are available, creat a new dataset and add to list
         */
        List<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
        if (isDataSetEnabled(0)) {
            LineDataSet ds1 = new LineDataSet(v1, getDataSetLabel(0));
            ds1.setColor(Color.RED);
            ds1.setCircleColor(Color.RED);
            ds1.setCircleRadius(3f);
            ds1.setValueTextSize(10f);
            dataSets.add(ds1);
        }
        if (isDataSetEnabled(1)) {
            LineDataSet ds2 = new LineDataSet(v2, getDataSetLabel(1));
            ds2.setColor(Color.BLUE);
            ds2.setCircleColor(Color.BLUE);
            ds2.setCircleRadius(3f);
            ds2.setValueTextSize(10f);
            dataSets.add(ds2);
        }
        if (isDataSetEnabled(2)) {
            LineDataSet ds3 = new LineDataSet(v3, getDataSetLabel(2));
            ds3.setColor(Color.GREEN);
            ds3.setCircleColor(Color.GREEN);
            ds3.setCircleRadius(3f);
            ds3.setValueTextSize(10f);
            dataSets.add(ds3);
        }

        // Create the Line Data
        LineData lineData = new LineData(dataSets);

        chart.setData(lineData); // add the line data to the chart
        Legend l = chart.getLegend();
        l.setTextSize(16f);
        // TODO consider changing max range based on screen size OR user selectable
        chart.setVisibleXRangeMaximum(10);
//        chart.setKeepPositionOnRotation(true);
        chart.invalidate(); // refresh the data
    }

    private String[] getProjectionList() {
        // returns the projection list needed for the cursor based on the parameters selected
        // in the configuration dialog
        final ArrayList<String> parameters = new ArrayList<>();
        parameters.add(StatusTable.COL_ID);
        parameters.add(StatusTable.COL_LOGDATE);
        // loop through the values and get the index for each value
        for (int i = 0; i < valuesItemIndex.length; i++ ) {
            if (valuesItemIndex[i] == 0 ) {
                // skip over the None item
                continue;
            }
            // Add the item to the list
            parameters.add(dataSetValues[valuesItemIndex[i]]);
            String s = getPWMColumnOverride(valuesItemIndex[i]);
            if (!s.isEmpty() ) {
                parameters.add(s);
            }
        }
        String[] a = new String[parameters.size()];
        a = parameters.toArray(a);
        return a;
    }

    @Nullable
    private String getSelectionCriteria() {
        String s;
        Boolean fAllData = false;
        /*
        Get Current Date / Time
        Put in format that Controller uses
        Match criteria accordingly
         */
        SimpleDateFormat dft = Utils.getDefaultDateFormat();

        // Get today's date in the default format
        Date today = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(today);
        switch (dateRangeItemIndex) {
            case 0:  // 1 day
                calendar.add(Calendar.DATE, -1);
                break;
            case 1:  // 1 week
                calendar.add(Calendar.WEEK_OF_YEAR, -1);
                break;
            case 2:  // 2 weeks
                calendar.add(Calendar.WEEK_OF_YEAR, -2);
                break;
            case 3:  // 1 month
                calendar.add(Calendar.MONTH, -1);
                break;
            case 4:  // 90 days
                calendar.add(Calendar.DAY_OF_YEAR, -90);
                break;
            case 5:  // 6 months
                calendar.add(Calendar.MONTH, -6);
                break;
            case 6:  // 1 year
                calendar.add(Calendar.YEAR, -1);
                break;
            case 7:  // all data
            default:
                fAllData = true;
                break;
        }
        // If we want all data, don't bother creating any additional selection criteria
        if (fAllData) {
            return null;
        }
        // Let's create the WHERE clause
        s = "" + StatusTable.COL_LOGDATE + " >= datetime('" + dft.format(calendar.getTime()) + "')";
        Log.d(TAG, "WHERE: " + s);
        return s;
    }

    private boolean isDataSetEnabled(int index) {
        return ( valuesItemIndex[index] > 0 );
    }

    private String getDataSetLabel(int index) {
        return dataSetLabels[valuesItemIndex[index]];
    }

    private float getActualValue(Cursor c, int index) {
        float y, tmp;
        String s = "";
        y = c.getFloat(c.getColumnIndex(dataSetValues[valuesItemIndex[index]]));
        s = getPWMColumnOverride(valuesItemIndex[index]);
        if ( !s.isEmpty() ) {
            tmp = c.getFloat(c.getColumnIndex(s));
            y = Controller.getPWMValueFromOverride(y, tmp);
        }
        return y;
    }

    private String getPWMColumnOverride(int index) {
        String column = "";
        switch (index) {
            case 6:  // DP
                column = StatusTable.COL_PWMDO;
                break;
            case 7:  // AP
                column = StatusTable.COL_PWMAO;
                break;
            case 8:  // DP2
                column = StatusTable.COL_PWMD2O;
                break;
            case 9:  // AP2
                column = StatusTable.COL_PWMA2O;
                break;
        }
        return column;
    }

    private void fixLogDates() {
        final int themeId = R.style.AlertDialogStyle;
        final ContextThemeWrapper themeWrapper = new ContextThemeWrapper(getActivity(), themeId);
        AlertDialog.Builder builder = new AlertDialog.Builder(themeWrapper, themeId);
        builder.setMessage(R.string.messagePromptDateConversion)
                .setTitle(R.string.titleConvertDatesPrompt)
                .setPositiveButton(R.string.buttonYes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        DialogProgressFixDates dlg = DialogProgressFixDates.newInstance();
                        dlg.show(getFragmentManager(), "dlgprogressfixdates");
                    }
                })
                .setNegativeButton(R.string.buttonNo, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
        Dialog dlg = builder.create();
        dlg.show();
    }
}
