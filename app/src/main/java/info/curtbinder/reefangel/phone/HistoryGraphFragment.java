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
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
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

import java.util.ArrayList;
import java.util.List;

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

    public HistoryGraphFragment() {
    }

    public static HistoryGraphFragment newInstance() {
        return new HistoryGraphFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if ( savedInstanceState != null ) {
            valuesItemIndex = savedInstanceState.getIntArray("values_index");
            dateRangeItemIndex = savedInstanceState.getInt("date_index");
            Log.d(TAG, "create loaded");
        }
        Log.d(TAG, "oncreate: {" + valuesItemIndex[0] + ", " + valuesItemIndex[1] + ", " + valuesItemIndex[2] + "}");
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putIntArray("values_index", valuesItemIndex);
        outState.putInt("date_index", dateRangeItemIndex);
        Log.d(TAG, "saved: {" + valuesItemIndex[0] + ", " + valuesItemIndex[1] + ", " + valuesItemIndex[2] + "}");
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if ( savedInstanceState != null ) {
            valuesItemIndex = savedInstanceState.getIntArray("values_index");
            dateRangeItemIndex = savedInstanceState.getInt("date_index");
            Log.d(TAG, "restored: {" + valuesItemIndex[0] + ", " + valuesItemIndex[1] + ", " + valuesItemIndex[2] + "}");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onresume: {" + valuesItemIndex[0] + ", " + valuesItemIndex[1] + ", " + valuesItemIndex[2] + "}");
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
//        Log.d(TAG, "Values: " + valuesItemIndex[0] + ", " + valuesItemIndex[1] + ", " +
//        valuesItemIndex[2] + ", " + dateRangeItemIndex);
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
        // TODO update the LIMIT
        String sortOrder = StatusTable.COL_ID + " ASC LIMIT 25";
        /// TODO selection criteria will limit the data

        // query the database for the values
        return resolver.query(uri, projection, null, null, sortOrder);
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

                dates.add(c.getString(c.getColumnIndex(StatusTable.COL_LOGDATE)));
            } while(c.moveToNext());
        } else {
            // no data
            chart.setNoDataText("No data available");
        }
        c.close();


        XAxis xAxis = chart.getXAxis();
        xAxis.setDrawLabels(false);

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
            dataSets.add(ds1);
        }
        if (isDataSetEnabled(1)) {
            LineDataSet ds2 = new LineDataSet(v2, getDataSetLabel(1));
            ds2.setColor(Color.BLUE);
            ds2.setCircleColor(Color.BLUE);
            ds2.setCircleRadius(3f);
            dataSets.add(ds2);
        }
        if (isDataSetEnabled(2)) {
            LineDataSet ds3 = new LineDataSet(v3, getDataSetLabel(2));
            ds3.setColor(Color.GREEN);
            ds3.setCircleColor(Color.GREEN);
            ds3.setCircleRadius(3f);
            dataSets.add(ds3);
        }

        // Create the Line Data
        LineData lineData = new LineData(dataSets);

        chart.setData(lineData); // add the line data to the chart
        Legend l = chart.getLegend();
        l.setTextSize(16f);
        // TODO consider changing max range based on screen size OR user selectable
        chart.setVisibleXRangeMaximum(10);
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
}
