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

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.ArrayList;
import java.util.List;

import info.curtbinder.reefangel.db.StatusProvider;
import info.curtbinder.reefangel.db.StatusTable;

public class History2Fragment extends Fragment {

    private LineChart chart;

    public History2Fragment() {
    }

    public static History2Fragment newInstance() {
        return new History2Fragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        loadData();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.frag_history2, container, false);

        findViews(root);
        return root;
    }

    private void findViews(View root) {
        chart = (LineChart) root.findViewById(R.id.line_chart);
    }

    private void loadData() {
        // TODO query the data from the database
        Uri uri = Uri.parse(StatusProvider.CONTENT_URI + "/"
                        + StatusProvider.PATH_STATUS);
        final ContentResolver resolver = getActivity().getContentResolver();
        final String[] projection = {   StatusTable.COL_ID,
                                        StatusTable.COL_LOGDATE,
                                        StatusTable.COL_T1,
                                        StatusTable.COL_PH,
                                        StatusTable.COL_SAL};
        // query the database for the values
        Cursor c = resolver.query(uri, projection, null, null, StatusTable.COL_ID + " ASC LIMIT 25");

        // create a list of the data points for the chart
        List<Entry> t1_entries = new ArrayList<Entry>();
//        List<Entry> t2_entries = new ArrayList<Entry>();
//        List<Entry> t3_entries = new ArrayList<Entry>();
        final ArrayList<String> dates = new ArrayList<String>();
        float count = 0;

        if ( c == null ) {
            chart.setNoDataText("No data available");
            return;
        }

        float fX, fY1, fY2, fY3;
        String s;
        if ( c.moveToFirst() ) {
            do {
                // we have data
                fY1 = c.getFloat(c.getColumnIndex(StatusTable.COL_T1));
//                fY2 = c.getFloat(c.getColumnIndex(StatusTable.COL_PH));
//                fY3 = c.getFloat(c.getColumnIndex(StatusTable.COL_SAL));
                //fX = c.getFloat(c.getColumnIndex(StatusTable.COL_ID));
                fX = count++;
                s = c.getString(c.getColumnIndex(StatusTable.COL_LOGDATE));
//                Log.d("CHART", "X: " + fX + ", T1: " + fY1 + ", T2: " + fY2 + ", T3: " + fY3 + " - " + s);
                dates.add(s);
                t1_entries.add(new Entry(fX, fY1));
//                t2_entries.add(new Entry(fX, fY2));
//                t3_entries.add(new Entry(fX, fY3));
            } while(c.moveToNext());
        } else {
            // no data
            chart.setNoDataText("No data available");
        }
        c.close();


        XAxis xAxis = chart.getXAxis();
        xAxis.setDrawLabels(false);

        // Create the DataSet for the line, set the Legend label for the data and change the color
        LineDataSet ds1 = new LineDataSet(t1_entries, "T1");
//        ds1.setColor(R.color.red);
//        LineDataSet ds2 = new LineDataSet(t2_entries, "pH");
//        ds2.setColor(R.color.reefangelblue);
//        LineDataSet ds3 = new LineDataSet(t3_entries, "Sal");
//        ds3.setColor(R.color.green);

        // Create list of datasets for the chart
        List<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
        dataSets.add(ds1);
//        dataSets.add(ds2);
//        dataSets.add(ds3);

        // Create the Line Data
        LineData lineData = new LineData(dataSets);

        chart.setData(lineData); // add the line data to the chart
        chart.setVisibleXRangeMaximum(10);
        chart.invalidate(); // refresh the data
    }
}
