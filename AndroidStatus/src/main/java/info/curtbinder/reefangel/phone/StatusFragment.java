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
import android.os.Bundle;
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

import info.curtbinder.reefangel.service.MessageCommands;
import info.curtbinder.reefangel.service.UpdateService;
import info.curtbinder.reefangel.service.XMLTags;

public class StatusFragment extends Fragment {

    public static final int PAGES = 4;
    private static final String TAG = StatusFragment.class.getSimpleName();
    // minimum number of pages: status, main relay
    private static final int MIN_PAGES = 3;
    private static final int POS_START = 0;
    private static final int POS_COMMANDS = POS_START;
    private static final int POS_CONTROLLER = POS_START + 1;
    private static final int POS_MODULES = POS_CONTROLLER + 10;
    private static final int POS_DIMMING = POS_MODULES;
    private static final int POS_RADION = POS_MODULES + 1;
    private static final int POS_VORTECH = POS_MODULES + 2;
    private static final int POS_AI = POS_MODULES + 3;
    private static final int POS_IO = POS_MODULES + 4;
    private static final int POS_CUSTOM = POS_MODULES + 5;
    private static final int POS_END = POS_CUSTOM + 1;
    private static final int POS_MAIN_RELAY = POS_CONTROLLER + 1;
    private static final int POS_EXP1_RELAY = POS_MAIN_RELAY + 1;
    private static final int POS_EXP2_RELAY = POS_MAIN_RELAY + 2;
    private static final int POS_EXP3_RELAY = POS_MAIN_RELAY + 3;
    private static final int POS_EXP4_RELAY = POS_MAIN_RELAY + 4;
    private static final int POS_EXP5_RELAY = POS_MAIN_RELAY + 5;
    private static final int POS_EXP6_RELAY = POS_MAIN_RELAY + 6;
    private static final int POS_EXP7_RELAY = POS_MAIN_RELAY + 7;
    private static final int POS_EXP8_RELAY = POS_MAIN_RELAY + 8;
    private static final String CURRENT_POSITION = "currentPosition";
    private static int currentPosition = 1;
    // Message Receivers
    StatusReceiver receiver;
    IntentFilter filter;
    // display views
    private TextView mUpdateTime;
    private ViewPager mPager;
    private SectionsPagerAdapter mPagerAdapter;
    private Fragment[] mAppPages;
    private String[] mVortechModes;

    public static StatusFragment newInstance() {
        StatusFragment f = new StatusFragment();
        return f;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        currentPosition = mPager.getCurrentItem();
        outState.putInt(CURRENT_POSITION, currentPosition);
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        View root = inflater.inflate(R.layout.frag_status, container, false);

        // Message Receiver stuff
        receiver = new StatusReceiver();
        filter = new IntentFilter(MessageCommands.UPDATE_DISPLAY_DATA_INTENT);
        filter.addAction(MessageCommands.UPDATE_STATUS_INTENT);
        filter.addAction(MessageCommands.COMMAND_RESPONSE_INTENT);
        filter.addAction(MessageCommands.ERROR_MESSAGE_INTENT);

        mUpdateTime = (TextView) root.findViewById(R.id.textUpdate);

        // set the maximum number of pages we can have
        mAppPages = new Fragment[PAGES];

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

    private void refreshPageData(int position) {
        PageRefreshInterface page = (PageRefreshInterface) mPagerAdapter.getItem(position);
        if (page != null) {
            page.refreshData();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");

        getActivity().registerReceiver(receiver, filter, Permissions.QUERY_STATUS, null);
        getActivity().registerReceiver(receiver, filter, Permissions.SEND_COMMAND, null);

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

    private class SectionsPagerAdapter extends FragmentStatePagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Log.d(TAG, "getItem: " + position);
            switch (position) {
                case 0:
                    if (mAppPages[0] == null) {
                        mAppPages[0] = PageCommandsFragment.newInstance();
                    }
                    break;
                case 1:
                    if (mAppPages[1] == null) {
                        mAppPages[1] = PageControllerFragment.newInstance();
                    }
                    break;
                case 2:
                    if (mAppPages[2] == null) {
                        mAppPages[2] = PageRelayFragment.newInstance(0);
                    }
                    break;
                case 3:
                    if (mAppPages[3] == null) {
                        mAppPages[3] = PageRelayFragment.newInstance(1);
                    }
                    break;
            }
            return mAppPages[position];
        }

        @Override
        public int getCount() {
            return PAGES;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            // this gets called before the getItem function gets called
            // todo add in other page names
            switch (position) {
                case 0:
                    return getString(R.string.titleCommands);
                case 1:
                    return getString(R.string.labelController);
                case 2:
                    return getString(R.string.prefMainRelayTitle);
                case 3:
                    return getString(R.string.prefExp1RelayTitle);
            }
            return null;
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
                int id = intent.getIntExtra(MessageCommands.UPDATE_STATUS_ID,
                        R.string.defaultStatusText);
                if (id > -1) {
                    mUpdateTime.setText(id);
                } else {
                    // we are updating with a string being sent to us
                    mUpdateTime
                            .setText(intent
                                    .getStringExtra(MessageCommands.UPDATE_STATUS_STRING));
                }
            } else if (action.equals(MessageCommands.UPDATE_DISPLAY_DATA_INTENT)) {
                // get the current fragment
                // call it's update data function
                refreshPageData(mPager.getCurrentItem());
//            } else if ( action.equals( MessageCommands.VORTECH_UPDATE_INTENT ) ) {
//            int type =
//                    intent.getIntExtra( MessageCommands.VORTECH_UPDATE_TYPE,
//                            0 );
//            Intent i =
//                    new Intent( StatusActivity.this,
//                            VortechPopupActivity.class );
//            i.putExtra( VortechPopupActivity.TYPE, type );
//            i.putExtra( Globals.PRE10_LOCATIONS,
//                    rapp.raprefs.useOldPre10MemoryLocations() );
//            startActivity( i );
//        } else if ( action.equals( MessageCommands.MEMORY_RESPONSE_INTENT ) ) {
//            String response =
//                    intent.getStringExtra( MessageCommands.MEMORY_RESPONSE_STRING );
//            if ( response.equals( XMLTags.Ok ) ) {
//                updateTime.setText( R.string.statusRefreshNeeded );
//            } else {
//                Toast.makeText(StatusActivity.this, response,
//                        Toast.LENGTH_LONG).show();
//            }
            } else if (action.equals(MessageCommands.COMMAND_RESPONSE_INTENT)) {
                String response =
                        intent.getStringExtra(MessageCommands.COMMAND_RESPONSE_STRING);
                if (response.contains(XMLTags.Ok)) {
                    mUpdateTime.setText(R.string.statusRefreshNeeded);
                } else {
                    Toast.makeText(getActivity(), response, Toast.LENGTH_LONG).show();
                }
            }
        }
    }
}
