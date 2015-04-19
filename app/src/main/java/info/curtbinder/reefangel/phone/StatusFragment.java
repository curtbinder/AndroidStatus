/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Curt Binder
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

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import info.curtbinder.reefangel.controller.Controller;
import info.curtbinder.reefangel.db.StatusProvider;
import info.curtbinder.reefangel.db.StatusTable;
import info.curtbinder.reefangel.service.MessageCommands;
import info.curtbinder.reefangel.service.UpdateService;
import info.curtbinder.reefangel.service.XMLTags;

public class StatusFragment extends Fragment {

    private static final String TAG = StatusFragment.class.getSimpleName();

    // minimum number of pages: flags, commands, status, main relay
    private static final int MIN_PAGES = 4;

    private static final int POS_START = 0;

    private static final int POS_FLAGS = POS_START;
    private static final int POS_COMMANDS = POS_START + 1;
    private static final int POS_CONTROLLER = POS_START + 2;

    // add on module pages
    private static final int POS_MODULES = POS_CONTROLLER + 10;
    private static final int POS_DIMMING = POS_MODULES;
    private static final int POS_SC_DIMMING = POS_MODULES + 1;
    private static final int POS_RADION = POS_MODULES + 2;
    private static final int POS_VORTECH = POS_MODULES + 3;
    private static final int POS_DCPUMP = POS_MODULES + 4;
    private static final int POS_AI = POS_MODULES + 5;
    private static final int POS_IO = POS_MODULES + 6;
    private static final int POS_CUSTOM = POS_MODULES + 7;

    // relay pages
    private static final int POS_MAIN_RELAY = POS_CONTROLLER + 1;
    private static final int POS_EXP1_RELAY = POS_MAIN_RELAY + 1;
    private static final int POS_EXP2_RELAY = POS_MAIN_RELAY + 2;
    private static final int POS_EXP3_RELAY = POS_MAIN_RELAY + 3;
    private static final int POS_EXP4_RELAY = POS_MAIN_RELAY + 4;
    private static final int POS_EXP5_RELAY = POS_MAIN_RELAY + 5;
    private static final int POS_EXP6_RELAY = POS_MAIN_RELAY + 6;
    private static final int POS_EXP7_RELAY = POS_MAIN_RELAY + 7;
    private static final int POS_EXP8_RELAY = POS_MAIN_RELAY + 8;

    private static final int POS_END = POS_CUSTOM + 1;

    private static final String CURRENT_POSITION = "currentPosition";
    private static int currentPosition = POS_CONTROLLER;

    // Message Receivers
    StatusReceiver receiver;
    IntentFilter filter;

    // display views
    private TextView mUpdateTime;
    private ViewPager mPager;
    private SectionsPagerAdapter mPagerAdapter;
    private Fragment[] mAppPages;
    private int[] mAppPageOrder;
    private String[] mAppPageTitles;
    private RAApplication raApp;
    private boolean fReloadPages = false;

    public static StatusFragment newInstance() {
        return new StatusFragment();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mPager != null) {
            currentPosition = mPager.getCurrentItem();
            outState.putInt(CURRENT_POSITION, currentPosition);
        }
    }

    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        currentPosition = POS_CONTROLLER;
        if (savedInstanceState != null) {
            currentPosition = savedInstanceState.getInt(CURRENT_POSITION, POS_CONTROLLER);
        }
        Log.d(TAG, "onViewStateRestored: pos: " + currentPosition);
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        raApp = (RAApplication) getActivity().getApplication();
        View root = inflater.inflate(R.layout.frag_status, container, false);

        createMessageReceiver();

        mUpdateTime = (TextView) root.findViewById(R.id.textUpdate);
        mAppPageOrder = new int[POS_END];

        createPages();
        updatePageOrder();

        // Set up the ViewPager with the sections adapter.
        mPager = (ViewPager) root.findViewById(R.id.pager);
        /* we are using Nested Fragments inside the pager adapter to allow the
         * fragment manager to manage them properly, instead of using the
		 * getSupportFragmentManager or getFragmentManager, you must use 
		 * getChildFragmentManager to allow for proper management
		 */
        mPagerAdapter = new SectionsPagerAdapter(getChildFragmentManager());
        mPager.setAdapter(mPagerAdapter);
        mPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                // example used mPagerAdapter.instantiateItem(mPager, position)
                refreshPageData(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        if (savedInstanceState != null) {
            currentPosition = savedInstanceState.getInt(CURRENT_POSITION);
        }
        // enable the options menu
        setHasOptionsMenu(true);
        return root;
    }

    private void createPages() {
        // set the maximum number of pages we can have
        mAppPages = new Fragment[POS_END];
        mAppPageTitles = new String[POS_END];
        mAppPages[POS_FLAGS] = PageFlagsFragment.newInstance();
        mAppPageTitles[POS_FLAGS] = getString(R.string.titleFlags);
        mAppPages[POS_COMMANDS] = PageCommandsFragment.newInstance();
        mAppPageTitles[POS_COMMANDS] = getString(R.string.titleCommands);
        mAppPages[POS_CONTROLLER] = PageControllerFragment.newInstance();
        mAppPageTitles[POS_CONTROLLER] = getString(R.string.labelController);
        mAppPages[POS_DIMMING] = PageDimmingFragment.newInstance();
        mAppPageTitles[POS_DIMMING] = getString(R.string.labelDimming);
        mAppPages[POS_SC_DIMMING] = PageSCDimmingFragment.newInstance();
        mAppPageTitles[POS_SC_DIMMING] = getString(R.string.labelSCDimming);
        mAppPages[POS_RADION] = PageRadionFragment.newInstance();
        mAppPageTitles[POS_RADION] = getString(R.string.labelRadion);
        mAppPages[POS_VORTECH] = PageVortechFragment.newInstance();
        mAppPageTitles[POS_VORTECH] = getString(R.string.labelVortech);
        mAppPages[POS_DCPUMP] = PageDCPumpFragment.newInstance();
        mAppPageTitles[POS_DCPUMP] = getString(R.string.labelDCPump);
        mAppPages[POS_AI] = PageAIFragment.newInstance();
        mAppPageTitles[POS_AI] = getString(R.string.labelAI);
        mAppPages[POS_IO] = PageIOFragment.newInstance();
        mAppPageTitles[POS_IO] = getString(R.string.labelIO);
        mAppPages[POS_CUSTOM] = PageCustomFragment.newInstance();
        mAppPageTitles[POS_CUSTOM] = getString(R.string.labelCustomVariables);
        for (int i = POS_MAIN_RELAY, j = 0; i < POS_EXP8_RELAY + 1; i++, j++) {
            // should be 3 through 11
            mAppPages[i] = PageRelayFragment.newInstance(j);
            mAppPageTitles[i] = getRelayPageTitle(j);
        }
    }

    private void redrawPages() {
        updatePageOrder();
        mPagerAdapter.notifyDataSetChanged();
        fReloadPages = false;
    }

    public void reloadPages() {
        // called by parent activity when entering settings
        // scroll to first page on entering settings
        mPager.setCurrentItem(POS_CONTROLLER, false);
        // force the pages to be redrawn if we enter the settings
        fReloadPages = true;
    }

    private void updatePageOrder() {
        int page, pos;
        int qty = raApp.raprefs.getExpansionRelayQuantity();
        // loop through all the possible pages
        // keep track of the pages installed compared to the total pages
        // if the module is enabled, add it to the available pages list
        // then increment the installed pages counter
        for (page = POS_START, pos = POS_START; page <= POS_END; page++) {
            switch (page) {
                // the first 4 pages are non-changeable, each case performs
                // the same action
                case POS_FLAGS:
                case POS_COMMANDS:
                case POS_CONTROLLER:
                case POS_MAIN_RELAY:
                    mAppPageOrder[pos] = page;
                    pos++;
                    break;
                case POS_DIMMING:
                    if (raApp.raprefs.getDimmingModuleEnabled()) {
                        mAppPageOrder[pos] = page;
                        pos++;
                    }
                    break;
                case POS_SC_DIMMING:
                    if (raApp.raprefs.getSCDimmingModuleEnabled()) {
                        mAppPageOrder[pos] = page;
                        pos++;
                    }
                    break;
                case POS_RADION:
                    if (raApp.raprefs.getRadionModuleEnabled()) {
                        mAppPageOrder[pos] = page;
                        pos++;
                    }
                    break;
                case POS_VORTECH:
                    if (raApp.raprefs.getVortechModuleEnabled()) {
                        mAppPageOrder[pos] = page;
                        pos++;
                    }
                    break;
                case POS_DCPUMP:
                    if (raApp.raprefs.getDCPumpModuleEnabled()) {
                        mAppPageOrder[pos] = page;
                        pos++;
                    }
                    break;
                case POS_AI:
                    if (raApp.raprefs.getAIModuleEnabled()) {
                        mAppPageOrder[pos] = page;
                        pos++;
                    }
                    break;
                case POS_IO:
                    if (raApp.raprefs.getIOModuleEnabled()) {
                        mAppPageOrder[pos] = page;
                        pos++;
                    }
                    break;
                case POS_CUSTOM:
                    if (raApp.raprefs.getCustomModuleEnabled()) {
                        mAppPageOrder[pos] = page;
                        pos++;
                    }
                    break;
                case POS_EXP1_RELAY:
                case POS_EXP2_RELAY:
                case POS_EXP3_RELAY:
                case POS_EXP4_RELAY:
                case POS_EXP5_RELAY:
                case POS_EXP6_RELAY:
                case POS_EXP7_RELAY:
                case POS_EXP8_RELAY:
                    if (qty > 0) {
                        int relay = page - POS_EXP1_RELAY;
                        if (relay < qty) {
                            mAppPageOrder[pos] = page;
                            pos++;
                        }
                    }
                    break;
            }
        }

        // fill the rest of the order array with the first position...
        // ie, zero out the rest of the array
        if (pos < POS_END) {
            for (; pos < POS_END; pos++) {
                mAppPageOrder[pos] = POS_START;
            }
        }
    }

    private String getRelayPageTitle(int relay) {
        int id;
        switch (relay) {
            default:
            case 0:
                id = R.string.prefMainRelayTitle;
                break;
            case 1:
                id = R.string.prefExp1RelayTitle;
                break;
            case 2:
                id = R.string.prefExp2RelayTitle;
                break;
            case 3:
                id = R.string.prefExp3RelayTitle;
                break;
            case 4:
                id = R.string.prefExp4RelayTitle;
                break;
            case 5:
                id = R.string.prefExp5RelayTitle;
                break;
            case 6:
                id = R.string.prefExp6RelayTitle;
                break;
            case 7:
                id = R.string.prefExp7RelayTitle;
                break;
            case 8:
                id = R.string.prefExp8RelayTitle;
                break;
        }
        return getString(id);
    }

    private void createMessageReceiver() {
        // Message Receiver stuff
        receiver = new StatusReceiver();
        filter = new IntentFilter(MessageCommands.UPDATE_DISPLAY_DATA_INTENT);
        filter.addAction(MessageCommands.UPDATE_STATUS_INTENT);
        filter.addAction(MessageCommands.COMMAND_RESPONSE_INTENT);
        filter.addAction(MessageCommands.ERROR_MESSAGE_INTENT);
        filter.addAction(MessageCommands.VORTECH_POPUP_INTENT);
        filter.addAction(MessageCommands.MEMORY_RESPONSE_INTENT);
        filter.addAction(MessageCommands.COMMAND_RESPONSE_INTENT);
        filter.addAction(MessageCommands.VERSION_RESPONSE_INTENT);
        filter.addAction(MessageCommands.OVERRIDE_RESPONSE_INTENT);
        filter.addAction(MessageCommands.OVERRIDE_POPUP_INTENT);
        filter.addAction(MessageCommands.CALIBRATE_RESPONSE_INTENT);
    }

    private void refreshPageData(int position) {
        PageRefreshInterface page = (PageRefreshInterface) mPagerAdapter.getItem(position);
        if (page != null) {
            page.refreshData();
        }
    }

    private void checkDeviceModules() {
        Cursor c = getLatestDataCursor();
        short newEM, newEM1, newREM;
        if (c.moveToFirst()) {
            newEM = c.getShort(c.getColumnIndex(StatusTable.COL_EM));
            newEM1 = c.getShort(c.getColumnIndex(StatusTable.COL_EM1));
            newREM = c.getShort(c.getColumnIndex(StatusTable.COL_REM));
        } else {
            newEM = newEM1 = newREM = 0;
        }
        c.close();

        if (checkExpansionModules(newEM) ||
                checkExpansionModules1(newEM1) ||
                checkRelayModules(newREM)) {
            // TODO do we call updateViewsAndVisibility??
            // if the modules change, redraw the pages first then navigate to the main page
            redrawPages();
            reloadPages();
        }
    }

    private boolean checkExpansionModules(short newEM) {
        boolean fReload = false;
        short oldEM = (short) raApp.raprefs.getPreviousEM();
        Log.d(TAG, "EM: Old: " + oldEM + " New: " + newEM);
        if (oldEM != newEM) {
            // expansion modules are different
            // set the flag to reload the pages
            fReload = true;
            // check which expansion modules are installed
            // set the installed modules in the preferences
            boolean f;
            f = (Controller.isAIModuleInstalled(newEM));
            Log.d(TAG, "AI: " + f);
            raApp.raprefs.set(R.string.prefExpAIEnableKey, f);
            f = (Controller.isDimmingModuleInstalled(newEM));
            Log.d(TAG, "Dimming: " + f);
            raApp.raprefs.set(R.string.prefExpDimmingEnableKey, f);
            f = (Controller.isIOModuleInstalled(newEM));
            Log.d(TAG, "IO: " + f);
            raApp.raprefs.set(R.string.prefExpIOEnableKey, f);
            f = (Controller.isORPModuleInstalled(newEM));
            Log.d(TAG, "ORP: " + f);
            raApp.raprefs.set(R.string.prefORPVisibilityKey, f);
            f = (Controller.isPHExpansionModuleInstalled(newEM));
            Log.d(TAG, "PHE: " + f);
            raApp.raprefs.set(R.string.prefPHExpVisibilityKey, f);
            f = (Controller.isRFModuleInstalled(newEM));
            Log.d(TAG, "RF: " + f);
            raApp.raprefs.set(R.string.prefExpRadionEnableKey, f);
            raApp.raprefs.set(R.string.prefExpVortechEnableKey, f);
            f = (Controller.isSalinityModuleInstalled(newEM));
            Log.d(TAG, "Salinity: " + f);
            raApp.raprefs.set(R.string.prefSalinityVisibilityKey, f);
            f = (Controller.isWaterLevelModuleInstalled(newEM));
            Log.d(TAG, "Water: " + f);
            String key;
            for (int i = 0; i < Controller.MAX_WATERLEVEL_PORTS; i++) {
                key = "wl";
                if (i > 0) key += i;
                key += "_visibility";
                raApp.raprefs.set(key, f);
            }

            // update the previous settings to the new ones after we change
            raApp.raprefs.setPreviousEM(newEM);
        }
        return fReload;
    }

    private boolean checkExpansionModules1(short newEM1) {
        boolean fReload = false;
        short oldEM1 = (short) raApp.raprefs.getPreviousEM1();
        Log.d(TAG, "EM1: Old: " + oldEM1 + " New: " + newEM1);
        if (oldEM1 != newEM1) {
            fReload = true;
            boolean f;
            f = (Controller.isHumidityModuleInstalled(newEM1));
            Log.d(TAG, "Humidity: " + f);
            raApp.raprefs.set(R.string.prefHumidityVisibilityKey, f);
            f = (Controller.isDCPumpControlModuleInstalled(newEM1));
            Log.d(TAG, "DCPump: " + f);
            raApp.raprefs.set(R.string.prefExpDCPumpEnabledKey, f);
            f = (Controller.isLeakDetectorModuleInstalled(newEM1));
            Log.d(TAG, "Leak: " + f);
            // TODO enable Leak module?? maybe it's not needed since it's a flag
            //raApp.raprefs.set(R.string.prefExpLeakDetectorEnableKey, f);

            raApp.raprefs.setPreviousEM1(newEM1);
        }

        return fReload;
    }

    private boolean checkRelayModules(short newREM) {
        boolean fReload = false;
        int oldRQty = raApp.raprefs.getExpansionRelayQuantity();
        int newRQty = Controller.getRelayExpansionModulesInstalled(newREM);
        Log.d(TAG, "Old Qty: " + oldRQty + " New Qty: " + newRQty);
        if (oldRQty != newRQty) {
            fReload = true;
            Log.d(TAG, "Relays: " + newRQty);
            raApp.raprefs.set(R.string.prefExpQtyKey, Integer.toString(newRQty));
        }
        return fReload;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");

        getActivity().registerReceiver(receiver, filter, Permissions.QUERY_STATUS, null);
        getActivity().registerReceiver(receiver, filter, Permissions.SEND_COMMAND, null);

        // Redraw pages if needed
        if (fReloadPages) {
            redrawPages();
        }

        mPager.setCurrentItem(currentPosition, false);
    }

    public void updateDisplayText(String text) {
        mUpdateTime.setText(text);
    }

    public String[] getNeverValues(int qty) {
        String[] s = new String[qty];
        for (int i = 0; i < qty; i++) {
            s[i] = getString(R.string.defaultStatusText);
        }
        return s;
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");

        getActivity().unregisterReceiver(receiver);
    }

    private void launchStatusTask() {
        Intent i = new Intent(getActivity(), UpdateService.class);
        i.setAction(MessageCommands.QUERY_STATUS_INTENT);
        getActivity().startService(i);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.frag_status, menu);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh:
                Log.d(TAG, "Refresh Data");
                launchStatusTask();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void displayOverrideDialog(int channel, short value) {
        DialogOverridePwm d = DialogOverridePwm.newInstance(channel, value,
                raApp.getPWMOverrideMessageDisplay(channel));
        d.show(getFragmentManager(), "dlgoverridepwm");
    }

    public void displayVortechDialog(int type, int value) {
        DialogVortech d = DialogVortech.newInstance(type, value,
                raApp.raprefs.useOldPre10MemoryLocations());
        d.show(getFragmentManager(), "dlgvortech");
    }

    public void displayDCPumpDialog(int type, int value) {
        DialogDCPump d= DialogDCPump.newInstance(type, value);
        d.show(getFragmentManager(), "dlgdcpump");
    }

    protected Cursor getLatestDataCursor() {
        Uri uri = Uri.parse(StatusProvider.CONTENT_URI + "/" + StatusProvider.PATH_LATEST);
        return getActivity().getContentResolver().query(uri, null, null, null,
                StatusTable.COL_ID + " DESC");
    }

    private class SectionsPagerAdapter extends FragmentStatePagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return mAppPages[mAppPageOrder[position]];
        }

        @Override
        public int getCount() {
            return MIN_PAGES + raApp.raprefs.getTotalInstalledModuleQuantity();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            // this gets called before the getItem function gets called
            return mAppPageTitles[mAppPageOrder[position]];
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

    }

    class StatusReceiver extends BroadcastReceiver {
        // private final String TAG = StatusReceiver.class.getSimpleName();

        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(MessageCommands.UPDATE_STATUS_INTENT)) {
                int id = intent.getIntExtra(MessageCommands.UPDATE_STATUS_ID, R.string.defaultStatusText);
                if (id > -1) {
                    mUpdateTime.setText(id);
                } else {
                    // we are updating with a string being sent to us
                    mUpdateTime.setText(intent
                            .getStringExtra(MessageCommands.UPDATE_STATUS_STRING));
                }
            } else if (action.equals(MessageCommands.UPDATE_DISPLAY_DATA_INTENT)) {
                // get the current fragment
                // call it's update data function
                refreshPageData(mPager.getCurrentItem());
                // only refresh after we get the command to refresh
                // otherwise, if we have this check inside the refreshPageData function,
                // we have this check being called every time the pages are changed when
                // the fragment is updating its data
                if (raApp.raprefs.isAutoUpdateModulesEnabled()) {
                    // update the screen / pages if necessary
                    checkDeviceModules();
                }
            } else if (action.equals(MessageCommands.MEMORY_RESPONSE_INTENT)) {
                // for vortech responses
                String response = intent.getStringExtra(MessageCommands.MEMORY_RESPONSE_STRING);
                displayResponse(response, -1, false);
            } else if (action.equals(MessageCommands.OVERRIDE_RESPONSE_INTENT)) {
                String response = intent.getStringExtra(MessageCommands.OVERRIDE_RESPONSE_STRING);
                displayResponse(response, -1, false);
            } else if (action.equals(MessageCommands.COMMAND_RESPONSE_INTENT)) {
                String response = intent.getStringExtra(MessageCommands.COMMAND_RESPONSE_STRING);
                displayResponse(response, -1, false);
            } else if (action.equals(MessageCommands.CALIBRATE_RESPONSE_INTENT)) {
                String response =
                        intent.getStringExtra(MessageCommands.CALIBRATE_RESPONSE_STRING);
                displayResponse(response, R.string.statusFinished, true);
            } else if (action.equals(MessageCommands.VERSION_RESPONSE_INTENT)) {
                // set the version button's text to the version of the software
                ((PageCommandsFragment) mAppPages[POS_COMMANDS]).setButtonVersion(
                        intent.getStringExtra(MessageCommands.VERSION_RESPONSE_STRING)
                );
                mUpdateTime.setText(R.string.statusFinished);
            }
        }
    }

    private void displayResponse(String response, int stringId, boolean fAlwaysToast) {
        int msgId;
        boolean fShowToast = false;
        if (stringId == -1) {
            msgId = R.string.statusRefreshNeeded;
        } else {
            msgId = stringId;
        }
        if (response.contains(XMLTags.Ok)) {
            mUpdateTime.setText(msgId);
            if (raApp.raprefs.isAutoRefreshAfterUpdate()) {
                mUpdateTime.setText(R.string.statusWaiting);
                Log.d(TAG, "AutoRefreshAfterUpdate");
                Handler h = new Handler();
                Runnable r = new Runnable() {
                    public void run() {
                        launchStatusTask();
                    }
                };
                // pause for a second before we proceed
                h.postDelayed(r, 1000);
            }
        } else {
            fShowToast = true;
        }

        if (fAlwaysToast || fShowToast) {
            Toast.makeText(getActivity(), response, Toast.LENGTH_LONG).show();
        }
    }
}
