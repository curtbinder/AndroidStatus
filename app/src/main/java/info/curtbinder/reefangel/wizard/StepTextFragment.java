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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import info.curtbinder.reefangel.phone.R;

/**
 * Created by binder on 2/21/15.
 */
public class StepTextFragment extends StepBase {

//    public static final String TAG = StepTextFragment.class.getSimpleName();

    private String sDescription;
    private TextView tvDescription;

    public StepTextFragment() {
        sDescription = "This is the default text";
    }

    public static StepTextFragment newInstance(String msg) {
        StepTextFragment f = new StepTextFragment();
        Bundle args = new Bundle();
        args.putString("msg", msg);
        f.setArguments(args);
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        Bundle args = getArguments();
        if (args != null) {
            sDescription = args.getString("msg");
        }
        View root = inflater.inflate(R.layout.frag_step_text, container, false);
        tvDescription = (TextView) root.findViewById(R.id.textDescription);
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateDisplay();
    }

    private void updateDisplay() {
//        Log.d(TAG, "updateDisplay");
        tvDescription.setText(sDescription);
    }

    public String getValue() {
        return "";
    }

    public int getOption() {
        return -1;
    }

    public boolean isNextEnabled() {
        return true;
    }
}
