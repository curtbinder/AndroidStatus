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

package info.curtbinder.reefangel.wizard;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import info.curtbinder.reefangel.phone.R;

/**
 * Created by binder on 2/21/15.
 */
public class StepSummaryFragment extends StepBase {

    public static final String TAG = StepSummaryFragment.class.getSimpleName();

    private LinearLayout layout;
    private CheckBox btnDownload;
    private boolean fHostMissing;

    public StepSummaryFragment() {
        fHostMissing = false;
    }

    public static StepSummaryFragment newInstance() {
        return new StepSummaryFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.frag_step_summary, container, false);
        layout = (LinearLayout) root.findViewById(R.id.summaryLayout);
        btnDownload = (CheckBox) root.findViewById(R.id.btn_download);
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public String getValue() {
        return "";
    }

    public int getOption() {
        return -1;
    }

    public void updateSummary(String title, String value) {
        Log.d(TAG, "Item: " + title + ": " + value);
        View v = View.inflate(getActivity().getBaseContext(), R.layout.step_summary_item, null);
        ((TextView) v.findViewById(R.id.tvTitle)).setText(title);
        ((TextView) v.findViewById(R.id.tvValue)).setText(value);
        layout.addView(v);
    }

    public void updateDownloadButton(boolean fEnabled) {
        btnDownload.setEnabled(fEnabled);
    }

    public boolean isDownloadLabelsChecked() {
        return btnDownload.isChecked();
    }

    public void setHostMissing(boolean fHostMissing) {
        this.fHostMissing = fHostMissing;
    }

    public boolean isNextEnabled() {
        return !fHostMissing;
    }
}
