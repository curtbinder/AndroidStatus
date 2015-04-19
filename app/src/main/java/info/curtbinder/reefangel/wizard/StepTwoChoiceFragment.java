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
import android.text.InputType;
import android.text.TextWatcher;
import android.text.method.DigitsKeyListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import info.curtbinder.reefangel.phone.R;

/**
 * Created by binder on 2/21/15.
 */
public class StepTwoChoiceFragment extends StepBase
        implements View.OnClickListener {

//    public static final String TAG = StepTwoChoiceFragment.class.getSimpleName();

    private RadioButton radioYes;
    private RadioButton radioNo;
    private RadioGroup radioGroup;
    private TextView tvDescription;
    private EditText editValue;

    private String sYesDescription = "";
    private String sYesHint = "";
    private String sNoDescription = "";
    private String sDescription = "";
    private boolean fHideValue = false;
    private boolean fEditTypeNumeric = false;

    public StepTwoChoiceFragment() {
    }

    public static StepTwoChoiceFragment newInstance() {
        return new StepTwoChoiceFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.frag_step_two_choice, container, false);
        findViews(root);
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateDisplay();
    }

    private void findViews(View root) {
        radioYes = (RadioButton) root.findViewById(R.id.radioYes);
        radioYes.setOnClickListener(this);
        radioNo = (RadioButton) root.findViewById(R.id.radioNo);
        radioNo.setOnClickListener(this);
        radioGroup = (RadioGroup) root.findViewById(R.id.radioBox);
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

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.radioNo) {
            // clear the text box when the NO option is selected
            clearValueText();
        }
        // update the value box based on the option selected
        updateValueEnabled();
        // update the next button after choice changes
        updateNextButton();
    }

    private void updateDisplay() {
//        Log.d(TAG, "updateDisplay");
        if (!(sYesDescription.length() == 0)) {
            radioYes.setText(sYesDescription);
        }
        if (!(sNoDescription.length() == 0)) {
            radioNo.setText(sNoDescription);
        }
        editValue.setHint(sYesHint);
        if (fEditTypeNumeric) {
            editValue.setInputType(InputType.TYPE_CLASS_NUMBER);
            editValue.setKeyListener(new DigitsKeyListener().getInstance("0123456789."));
        }
        int visible = View.VISIBLE;
        if (fHideValue) {
            visible = View.GONE;
        }
        editValue.setVisibility(visible);
        tvDescription.setText(sDescription);
    }

    public String getValue() {
        return editValue.getText().toString();
    }

    public int getOption() {
        if (radioYes.isChecked()) {
            return 1;
        }
        if (radioNo.isChecked()) {
            return 0;
        }
        return -1;
    }

    public void updateNextButton() {
        SetupWizardActivity m = (SetupWizardActivity) getActivity();
        m.updateNextButton();
    }

    private void updateValueEnabled() {
        boolean fEditEnable = false;
        if (radioGroup.getCheckedRadioButtonId() == R.id.radioYes) {
            // enable the edit box because of the first radio button
            fEditEnable = true;
        }
        editValue.setEnabled(fEditEnable);
    }

    private void clearValueText() {
        editValue.setText("");
    }

    public boolean isNextEnabled() {
        int ck = radioGroup.getCheckedRadioButtonId();
        boolean fEnable = true;
        if (ck == -1) {
            // no item checked
            fEnable = false;
        } else if (ck == R.id.radioYes) {
            if (!fHideValue) {
                // if the value is not hidden, update based on it
                fEnable = (editValue.length() > 0);
            }
        }
        return fEnable;
    }

    public void setValueNumeric() {
        fEditTypeNumeric = true;
    }

    public void setYesDescription(String msg) {
        sYesDescription = msg;
    }

    public void setYesHint(String msg) {
        sYesHint = msg;
    }

    public void setNoDescription(String msg) {
        sNoDescription = msg;
    }

    public void setStepDescription(String msg) {
        sDescription = msg;
    }

    public void setValueHidden(boolean fHidden) {
        fHideValue = fHidden;
    }
}
