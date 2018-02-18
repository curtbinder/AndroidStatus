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
import android.content.Intent;
import android.support.v4.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;

/**
 * Created by binder on 2/17/18.
 */

public class DialogConfigureChart extends DialogFragment {

    private static final String TAG = DialogConfigureChart.class.getSimpleName();
    public final static int CONFIGURE_CHART = 1;

    public static final String VALUES1 = "values1";
    public static final String VALUES2 = "values2";
    public static final String VALUES3 = "values3";
    public static final String DATE1 = "date1";

    private int v1_index = 1, v2_index = 0, v3_index = 0, d1_index = 0;

    public DialogConfigureChart(){
    }

    public static DialogConfigureChart newInstance(int v1, int v2, int v3, int d1) {
        DialogConfigureChart d = new DialogConfigureChart();
        Bundle args = new Bundle();
        args.putInt(VALUES1, v1);
        args.putInt(VALUES2, v2);
        args.putInt(VALUES3, v3);
        args.putInt(DATE1, d1);
        d.setArguments(args);
        return d;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // TODO improve getting current theme
        final int themeId = R.style.AlertDialogStyle;
        final ContextThemeWrapper themeWrapper = new ContextThemeWrapper(getActivity(), themeId);
        LayoutInflater inflater = getActivity().getLayoutInflater().cloneInContext(themeWrapper);
        AlertDialog.Builder builder = new AlertDialog.Builder(themeWrapper, themeId);
        View root = inflater.inflate(R.layout.dlg_configure_chart, null);
        findViews(root);
        setAdapters();

        Bundle args = getArguments();
        if (args != null) {
            v1_index = args.getInt(VALUES1, 1);
            v2_index = args.getInt(VALUES2, 0);
            v3_index = args.getInt(VALUES3, 0);
            d1_index = args.getInt(DATE1, 0);
        }
        builder.setView(root);
        builder.setTitle(R.string.titleConfigureChart);
        builder.setPositiveButton(R.string.buttonOk, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Log.d(TAG, "OK clicked");
                saveChartSettings();
            }
        });
        builder.setNegativeButton(R.string.buttonCancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dismiss();
            }
        });
        return builder.create();
    }

    private void findViews(View root) {

    }

    private void setAdapters() {

    }

    protected void saveChartSettings() {
        // TODO copy the indices back into the variables
        v1_index = 5;
        v2_index = 3;
        v3_index = 0;
        d1_index = 9;
        Bundle args = new Bundle();
        args.putInt(VALUES1, v1_index);
        args.putInt(VALUES2, v2_index);
        args.putInt(VALUES3, v3_index);
        args.putInt(DATE1, d1_index);
//        setArguments(args);
    }
}
