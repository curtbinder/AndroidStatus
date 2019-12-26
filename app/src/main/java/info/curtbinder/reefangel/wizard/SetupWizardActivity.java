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

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.util.LinkedList;

import info.curtbinder.reefangel.phone.MainActivity;
import info.curtbinder.reefangel.phone.R;
import info.curtbinder.reefangel.phone.RAApplication;
import info.curtbinder.reefangel.service.MessageCommands;
import info.curtbinder.reefangel.service.UpdateService;


public class SetupWizardActivity extends AppCompatActivity
        implements View.OnClickListener {

    private static final String TAG = SetupWizardActivity.class.getSimpleName();
    private static final int MAX_STEPS = 9;

    private static final int STEP_START = 0;
    private static final int STEP_DEVICE = 1;
    private static final int STEP_USERNAME = 2;
    private static final int STEP_RADDNS = 3;
    private static final int STEP_ALTDDNS = 4;
    private static final int STEP_IP = 5;
    private static final int STEP_AUTHUSER = 6;
    private static final int STEP_AUTHPASS = 7;
//    private static final int STEP_SUMMARY = 8;

    private NoSwipePager mWizardPager;
    private WizardAdapter mWizardAdapter;
    private Button btnNext;
    private Button btnPrev;
    private Fragment[] mSteps;
    private StepItem[] aStepItems;
    private LinkedList<Integer> stepsList;
    private RAApplication raApp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        raApp = (RAApplication) getApplication();
        setContentView(R.layout.activity_setup_wizard);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.titleSetupWizard);
        stepsList = new LinkedList<Integer>();
        findViews();
        createSteps();
        updateNextButton();
    }

    private void findViews() {
        btnNext = (Button) findViewById(R.id.btn_next);
        btnNext.setOnClickListener(this);
        btnPrev = (Button) findViewById(R.id.btn_prev);
        btnPrev.setOnClickListener(this);
        mWizardPager = (NoSwipePager) findViewById(R.id.pager);
        mWizardAdapter = new WizardAdapter(getSupportFragmentManager());
        mWizardPager.setAdapter(mWizardAdapter);
        mWizardPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i2) {
            }

            @Override
            public void onPageSelected(int i) {
                if (i == (MAX_STEPS - 1)) {
                    updateSummaryPageText();
                }
                updateNextButton();
            }

            @Override
            public void onPageScrollStateChanged(int i) {
            }
        });
    }

    private void createSteps() {
        aStepItems = new StepItem[]{
                new StepItem("", ""),
                new StepItem(getString(R.string.prefsCategoryDevice), getString(R.string.prefDeviceKey)),
                new StepItem(getString(R.string.labelPortalUsername), getString(R.string.prefUserIdKey)),
                new StepItem(getString(R.string.prefHostAwayTitle), getString(R.string.prefHostAwayKey)),
                new StepItem(getString(R.string.prefHostAwayTitle), getString(R.string.prefHostAwayKey)),
                new StepItem(getString(R.string.prefHostTitle), getString(R.string.prefHostKey)),
                new StepItem(getString(R.string.labelDeviceUsername), getString(R.string.prefWifiUserKey)),
                new StepItem(getString(R.string.labelDevicePassword), getString(R.string.prefWifiPasswordKey)),
                new StepItem(getString(R.string.labelSummary), ""),
        };

        mSteps = new Fragment[]{
                createMainStep(),
                createDeviceStep(),
                createUsernameStep(),
                createRADDNSStep(),
                createAltDDNSStep(),
                createDirectIpStep(),
                createDeviceAuthUsername(),
                createDeviceAuthPassword(),
                createSummaryStep(),
        };
    }

    private Fragment createMainStep() {
        return StepTextFragment.newInstance(getString(R.string.descriptionWizardMainStep));
    }

    private Fragment createDeviceStep() {
        StepTwoChoiceFragment f = StepTwoChoiceFragment.newInstance();
        f.setStepDescription(getString(R.string.descriptionWizardDeviceStep));
        f.setYesDescription(getString(R.string.prefsCategoryController));
        f.setNoDescription(getString(R.string.prefsCategoryPortal));
        f.setValueHidden(true);
        return f;
    }

    private Fragment createUsernameStep() {
        StepSingleInputFragment f = StepSingleInputFragment.newInstance();
        f.setStepDescription(getString(R.string.descriptionWizardUsernameStep));
        f.setHint(getString(R.string.prefUserIdDefault));
        return f;
    }

    private Fragment createRADDNSStep() {
        StepTwoChoiceFragment f = StepTwoChoiceFragment.newInstance();
        f.setStepDescription(getString(R.string.descriptionWizardRADNSStep));
        f.setYesHint(getString(R.string.prefProfileHomeTitle));
        return f;
    }

    private Fragment createAltDDNSStep() {
        StepTwoChoiceFragment f = StepTwoChoiceFragment.newInstance();
        f.setStepDescription(getString(R.string.descriptionWizardDNSStep));
        f.setYesHint(getString(R.string.labelExampleDNS));
        return f;
    }

    private Fragment createDirectIpStep() {
        StepTwoChoiceFragment f = StepTwoChoiceFragment.newInstance();
        f.setStepDescription(getString(R.string.descriptionWizardIPStep));
        f.setYesHint(getString(R.string.prefHostHomeDefault));
        f.setValueNumeric();
        return f;
    }

    private Fragment createDeviceAuthUsername() {
        StepTwoChoiceFragment f = StepTwoChoiceFragment.newInstance();
        f.setStepDescription(getString(R.string.descriptionWizardAuthUsernameStep));
        f.setYesHint(getString(R.string.labelUsername));
        return f;
    }

    private Fragment createDeviceAuthPassword() {
        StepSingleInputFragment f = StepSingleInputFragment.newInstance();
        f.setStepDescription(getString(R.string.descriptionWizardAuthPasswordStep));
        f.setHint(getString(R.string.labelPassword));
        f.setValueRequired(true);
        return f;
    }

    private Fragment createSummaryStep() {
        return StepSummaryFragment.newInstance();
    }

    public void updateNextButton() {
        StepBase step = (StepBase) mWizardAdapter.getItem(mWizardPager.getCurrentItem());
        boolean fEnabled = step.isNextEnabled();
        btnNext.setEnabled(fEnabled);
    }

    private void updateSummaryPageText() {
        // Final / Summary Step
        // update the description of the summary page based on the steps taken
        int size = stepsList.size() - 1;
        StepSummaryFragment step = (StepSummaryFragment) mWizardAdapter.getItem(8);
        // skip the first step when displaying the summary
        String summaryText, title;
        boolean fNoHostProvided = true;
        for (int i = size - 1; i >= 0; i--) {
            Integer v = stepsList.get(i);
            summaryText = aStepItems[v].getSummaryText();
            if (summaryText.length() == 0) {
                summaryText = aStepItems[v].getValue();
            }
            // skip displaying the step if the value is empty
            if (!(summaryText.length() == 0)) {
                title = aStepItems[v].getTitle();
                if (title.compareTo(getString(R.string.prefHostTitle)) == 0) {
                    // if we have a home_host, then we are good
                    fNoHostProvided = false;
                }
                step.updateSummary(title, summaryText);
            }
        }
        // assume the host is not missing to accommodate for the portal
        boolean fHostMissing = false;
        if (fNoHostProvided && !isDevicePortal()) {
            // no host was provided, we need to display something in the summary
            // AND we are not communicating with the portal
            step.updateSummary(getString(R.string.prefHostTitle), getString(R.string.labelMissing));
            fHostMissing = true;
        }
        step.setHostMissing(fHostMissing);
        step.updateDownloadButton(isUsernameGiven());
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.btn_next:
                navigate(true);
                break;
            case R.id.btn_prev:
                navigate(false);
                break;
        }
    }

    private void navigate(boolean fForward) {
        int position = mWizardPager.getCurrentItem();
        updateValues(position);
        if (fForward) {
            if (isLastStep(position)) {
                saveValues();
                finishAndLaunch();
            } else {
                moveStep(getNextStep(position));
            }
        } else {
            if (isFirstStep(position)) {
                // If the user is currently looking at the first step, allow the system to handle the
                // Back button. This calls finish() on this activity and pops the back stack.
                super.onBackPressed();
            } else {
                // Otherwise, select the previous step.
                moveStep(getPreviousStep());
            }
        }
    }

    private void updateValues(int position) {
        StepBase step = (StepBase) mWizardAdapter.getItem(position);
        aStepItems[position].setValue(step.getValue());
        aStepItems[position].setOptionSelected(step.getOption());
    }

    private void saveValues() {
        int size = stepsList.size() - 1;
        // skip the first step, which is the summary step
        // set the Device, since the value is empty / not set
        String sValue = "0";  // Controller
        if (isDevicePortal()) {
            sValue = "1"; // Portal
        }
        raApp.raprefs.set(aStepItems[1].getPreferenceKey(), sValue);
        for (int i = size - 1; i >= 0; i--) {
            Integer v = stepsList.get(i);
            // store it if we have a value
            if (!(aStepItems[v].getValue().length() == 0)) {
                String value = aStepItems[v].getValue();
                // If we are on the RA DynDNS step, we need to compute the hostname
                // based on the given values
                if (v == STEP_RADDNS) {
                    value = getRADynDNSHostname();
                } // otherwise, if it's an Alternate DynDNS host, just use the value entered
                raApp.raprefs.set(aStepItems[v].getPreferenceKey(), value);
            }
        }
        if (isDownloadLabelsChecked()) {
            Intent i = new Intent(this, UpdateService.class);
            i.setAction(MessageCommands.LABEL_QUERY_INTENT);
            UpdateService.enqueueWork(this, i);
        }
    }

    private void finishAndLaunch() {
        raApp.raprefs.disableFirstRun();
        Intent i = new Intent(this, MainActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(i);
        finish();
    }

    private void moveStep(int newPosition) {
        // scroll to the new page
        mWizardPager.setCurrentItem(newPosition);
        // after the page is scrolled, see onPageSelected, the next button is updated
        // update the navigation buttons textual display
        updateButtons(newPosition);
    }

    private void updateButtons(int newPosition) {
        // update the buttons
        if (isLastStep(newPosition)) {
            btnNext.setText(R.string.buttonFinish);
        } else {
            btnNext.setText(R.string.buttonNext);
        }
        if (isFirstStep(newPosition)) {
            btnPrev.setText(R.string.buttonExit);
        } else {
            btnPrev.setText(R.string.buttonPrevious);
        }
    }

    private boolean isFirstStep(int position) {
        return (position == STEP_START);
    }

    private boolean isLastStep(int position) {
        return (position == (MAX_STEPS - 1));
    }

    private int getNextStep(int currentPosition) {
        // determine the next position based on the current position
        // grab the previous page from the stack for use later
        Integer previous = stepsList.peek();
        // push the current position onto the stack
        stepsList.addFirst(currentPosition);
        int newPosition = currentPosition + 1;
        switch (currentPosition) {
            default:
                break;
            // case 0: Start step
            case STEP_DEVICE:
                // Device Step
                // Update the Username step to be required if Portal is chosen
                // or not required if the Controller is chosen
                StepSingleInputFragment step = (StepSingleInputFragment) mWizardAdapter.getItem(STEP_USERNAME);
                step.setValueRequired(isDevicePortal());
                break;
            case STEP_USERNAME:
                // Username step
                // if Portal was chosen, then we jump straight to the end
                // if Controller was chosen, we proceed based on the value given at this step
                // Controller value is 1, Portal value is 0
                if (isDevicePortal()) {
                    // Portal chosen, jump to the end
                    newPosition = MAX_STEPS - 1;
                    // set the summary text for the device to be Portal
                    aStepItems[STEP_DEVICE].setSummaryText(getString(R.string.prefsCategoryPortal));
                } else {
                    // Controller chosen, check if the username was given
                    // set the summary text for the device to be Controller
                    aStepItems[STEP_DEVICE].setSummaryText(getString(R.string.prefsCategoryController));
                    if (isUsernameGiven()) {
                        // Username was provided, so proceed to next step
                        newPosition = STEP_RADDNS;
                    } else {
                        // No username given, so skip over the next step
                        newPosition = STEP_ALTDDNS;
                    }
                }
                break;
            case STEP_RADDNS:
                // RA Dynamic DNS step
                // if they are using the RA DDNS step, we can skip the next step which
                // asks if they are using an alternate DDNS
                if (aStepItems[STEP_RADDNS].getValue().length() == 0) {
                    // Not using RA Dynamic DNS, so proceed to next step
                    newPosition = STEP_ALTDDNS;
                } else {
                    // Using RA Dynamic DNS, so skip the next step and goto the IP step
                    // Update the summary text to contain the full hostname
                    // host is:  username-subdomain.myreefangel.com
                    aStepItems[STEP_RADDNS].setSummaryText(getRADynDNSHostname());
                    newPosition = STEP_IP;
                }
                break;
            // case STEP_ALTDDNS: Alternate DDNS step
            case STEP_IP:
                // IP Address step
                if (aStepItems[STEP_IP].getValue().length() == 0) {
                    // user did not provide an IP address for the controller
                    // we need to assume that one of the Dynamic DNS hosts was provided
                    // look at previous step and set that title and preference key to be Home
                    aStepItems[previous].setTitle(getString(R.string.prefHostTitle));
                    aStepItems[previous].setPreferenceKey(getString(R.string.prefHostKey));
                } else {
                    // otherwise, the user provided an IP address and we need to set the
                    // Dynamic DNS host as the Away host
                    aStepItems[previous].setTitle(getString(R.string.prefHostAwayTitle));
                    aStepItems[previous].setPreferenceKey(getString(R.string.prefHostAwayKey));
                }
                break;
            case STEP_AUTHUSER:
                // Wifi Authentication Username step
                // if a username was entered, then we proceed to the next step and ask for
                // the password
                // otherwise, if no username given, we skip the password step and jump to the end
                if (aStepItems[STEP_AUTHUSER].getValue().length() == 0) {
                    newPosition = MAX_STEPS - 1;
                } else {
                    newPosition = STEP_AUTHPASS;
                }
                break;
            // case STEP_AUTHPASS: Wifi Authentication Password step
        }
        Log.d(TAG, "next: " + newPosition);
        return newPosition;
    }

    private int getPreviousStep() {
        // The previous step is simply stored in the stack.
        // pop the previous pages off of the stack
        Integer newPos = stepsList.removeFirst();
        Log.d(TAG, "prev: " + newPos.toString());
        return newPos;
    }

    private String getRADynDNSHostname() {
        // Gets the full dynamic dns hostname based on the user's values entered
        // host is:  username-subdomain.myreefangel.com
        return aStepItems[STEP_USERNAME].getValue() + "-" +
                aStepItems[STEP_RADDNS].getValue() + getString(R.string.labelMyReefangelDomain);
    }

    private boolean isUsernameGiven() {
        return !(aStepItems[STEP_USERNAME].getValue().length() == 0);
    }

    private boolean isDevicePortal() {
        return (aStepItems[STEP_DEVICE].getOptionSelected() == 0);
    }

    private boolean isDownloadLabelsChecked() {
        StepSummaryFragment step = (StepSummaryFragment) mWizardAdapter.getItem(MAX_STEPS - 1);
        return step.isDownloadLabelsChecked();
    }

    @Override
    public void onBackPressed() {
        navigate(false);
    }

    private class WizardAdapter extends FragmentStatePagerAdapter {

        public WizardAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            return mSteps[i];
        }

        @Override
        public int getCount() {
            return MAX_STEPS;
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }
    }
}
