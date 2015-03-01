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
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import info.curtbinder.reefangel.phone.R;

/**
 * Created by binder on 2/21/15.
 */
public class StepSingleInputFragment extends StepBase {

//    public static final String TAG = StepSingleInputFragment.class.getSimpleName();

    private TextView tvDescription;
    private EditText editValue;

    private String sHint = "";
    private String sDescription = "";
    private boolean fRequired = false;

    public StepSingleInputFragment() {
    }

    public static StepSingleInputFragment newInstance() {
        return new StepSingleInputFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.frag_step_single_input, container, false);
        findViews(root);
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateDisplay();
    }

    private void findViews(View root) {
        tvDescription = (TextView) root.findViewById(R.id.textDescription);
        editValue = (EditText) root.findViewById(R.id.editValue);
        editValue.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // do nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // do nothing
            }

            @Override
            public void afterTextChanged(Editable s) {
                // update the next button appropriately
                updateNextButton();
            }
        });
    }

    private void updateDisplay() {
        editValue.setHint(sHint);
        tvDescription.setText(sDescription);
    }

    public String getValue() {
        return editValue.getText().toString();
    }

    public int getOption() {
        return -1;
    }

    public void updateNextButton() {
        SetupWizardActivity m = (SetupWizardActivity) getActivity();
        m.updateNextButton();
    }

    public boolean isNextEnabled() {
        // if the text field is required, make sure that something is entered
        // if it's not required, always say the next button is enabled
        return !fRequired || (editValue.length() > 0);
    }

    public void setValueRequired(boolean fRequired) {
        this.fRequired = fRequired;
    }

    public void setHint(String msg) {
        sHint = msg;
    }

    public void setStepDescription(String msg) {
        sDescription = msg;
    }

}
