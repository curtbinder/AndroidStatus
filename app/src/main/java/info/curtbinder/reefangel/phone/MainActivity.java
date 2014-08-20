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

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;

public class MainActivity extends ActionBarActivity
        implements android.support.v7.app.ActionBar.OnNavigationListener {

    private static final String OPENED_KEY = "OPENED_KEY";
    private static final String STATE_CHECKED = "DRAWER_CHECKED";
//    private static final String POSITION = "POSITION";
    private static final String PREVIOUS_CHECKED = "PREVIOUS";

    // do not switch selected profile when restoring the application state
    private static boolean fRestoreState = false;
    public final String TAG = MainActivity.class.getSimpleName();
    private RAApplication raApp;
    private String[] mNavTitles;
    private DrawerLayout mDrawerLayout;
    private LinearLayout mDrawer;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private boolean fAddToBackStack = false;
    private int mCurrentPosition = 0;
    private int mOldPosition = -1;
    private SharedPreferences prefs = null;
    private Boolean opened = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        raApp = (RAApplication) getApplication();

        if ( savedInstanceState != null ) {
            int pos = savedInstanceState.getInt(STATE_CHECKED, -1);
            int oldpos = savedInstanceState.getInt(PREVIOUS_CHECKED, -1);
            Log.d(TAG, "onRestoreInstanceState, cur: " + pos + " old: " + oldpos);
            mCurrentPosition = pos;
            mOldPosition = oldpos;
        }
        setupNavDrawer(savedInstanceState);
        updateContent();
        updateActionBar();

        // selectItem( 0 );
        fAddToBackStack = true;

        // launch a new thread to show the drawer on very first app launch
        new Thread(new Runnable() {
            @Override
            public void run() {
                prefs = getPreferences(MODE_PRIVATE);
                opened = prefs.getBoolean(OPENED_KEY, false);
                if (!opened) {
                    mDrawerLayout.openDrawer(mDrawer);
                }
            }
        }).start();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        Log.d(TAG, "onSaveInstanceState");
        outState.putInt(STATE_CHECKED, mDrawerList.getCheckedItemPosition());
        outState.putInt(PREVIOUS_CHECKED, mOldPosition);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
//        int pos = savedInstanceState.getInt(STATE_CHECKED, -1);
//        int oldpos = savedInstanceState.getInt(PREVIOUS_CHECKED, -1);
//
//        Log.d(TAG, "onRestoreInstanceState, cur: " + pos + " old: " + oldpos);
//        if (pos > -1) {
//            mDrawerList.setItemChecked(pos, true);
//        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        // todo add in message handlers, broadcast receivers
        fRestoreState = true;
        setNavigationList();

        // last thing we do is display the changelog if necessary
        // TODO add in a preference check for displaying changelog on app startup
        raApp.displayChangeLog(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    private void setupNavDrawer(Bundle savedInstanceState) {
        Log.d(TAG, "setupNavDrawer");
        // get the string array for the navigation items
        mNavTitles = getResources().getStringArray(R.array.nav_items);

        // locate the navigation drawer items in the layout
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawer = (LinearLayout) findViewById(R.id.drawer);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        // set a custom shadow that overlays the main content when the drawer
        // opens
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow,
                GravityCompat.START);

        // set the adapter for the navigation list view
        ArrayAdapter<String> adapter =
                new ArrayAdapter<String>(this, R.layout.drawer_list_item,
                        mNavTitles);
        mDrawerList.setAdapter(adapter);
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
        mDrawerList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        if (savedInstanceState == null) {
            mDrawerList.setItemChecked(0, true);
        } else {
            mDrawerList.setItemChecked(mCurrentPosition, true);
        }

        mDrawerToggle =
                new MyDrawerToggle(this, mDrawerLayout, R.drawable.ic_drawer,
                        R.string.drawer_open, R.string.drawer_close);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }

    private void setNavigationList() {
        // set list navigation items
        final ActionBar ab = getSupportActionBar();
        Context context = ab.getThemedContext();
        int arrayID;
        if (raApp.isAwayProfileEnabled()) {
            arrayID = R.array.profileLabels;
        } else {
            arrayID = R.array.profileLabelsHomeOnly;
        }
        ArrayAdapter<CharSequence> list =
                ArrayAdapter.createFromResource(context, arrayID,
                        R.layout.support_simple_spinner_dropdown_item);
        ab.setListNavigationCallbacks(list, this);
        ab.setSelectedNavigationItem(raApp.getSelectedProfile());
    }

    private void updateActionBar() {
        // update actionbar
        final ActionBar ab = getSupportActionBar();
        ab.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
        ab.setDisplayShowTitleEnabled(false);
        ab.setDisplayHomeAsUpEnabled(true);
        enableActionBarHomeButton();
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    private void enableActionBarHomeButton() {
        // function available in api 14 and later
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            getActionBar().setHomeButtonEnabled(true);
        }
    }

    @Override
    public boolean onNavigationItemSelected(int itemPosition, long itemId) {
        // only switch profiles when the user changes the navigation item,
        // not when the navigation list state is restored
        if (!fRestoreState) {
            raApp.setSelectedProfile(itemPosition);
        } else {
            fRestoreState = false;
        }
        return true;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // sync the toggle state after onRestoreInstanceState has occurred
        mDrawerToggle.syncState();
    }

    @SuppressLint("NewApi")
    protected void myInvalidateOptionsMenu() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            supportInvalidateOptionsMenu();
        } else {
            invalidateOptionsMenu();
        }
    }

    private void updateContent() {
        getSupportActionBar().setTitle(mNavTitles[mCurrentPosition]);
        if (mCurrentPosition != mOldPosition) {
            // update the main content by replacing fragments
            Fragment fragment;
            switch (mCurrentPosition) {
                default:
                case 0:
                    fragment = new StatusFragment();
                    break;
                case 1:
                    fragment = MemoryFragment.newInstance(raApp.raprefs.useOldPre10MemoryLocations());
                    break;
                case 2:
                    fragment = new NotificationsFragment();
                    break;
                case 3:
                    fragment = new HistoryFragment();
                    break;
                case 4:
                    fragment = new ErrorsFragment();
                    break;
                case 5:
                    fragment = new DateTimeFragment();
                    break;
            }

            FragmentTransaction ft =
                    getSupportFragmentManager().beginTransaction();

            Log.d(TAG, "Old Pos: " + mOldPosition + " New Pos: " + mCurrentPosition);
            ft.replace(R.id.content_frame, fragment);
            if (fAddToBackStack) {
                // TODO implement backstack listener in order to change/update
                // title
                ft.addToBackStack(null);
            }

            ft.commit();
            mOldPosition = mCurrentPosition;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.global, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // pass the event to ActionBarDrawerToggle, if it returns true,
        // then it has handled the app icon touch event
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        // handle the rest of the action bar items here
        switch (item.getItemId()) {
            case R.id.action_settings:
                Fragment f = getSupportFragmentManager().findFragmentById(R.id.content_frame);
                if ( f instanceof StatusFragment ) {
                    // the current fragment is the status fragment
                    Log.d(TAG, "Status Fragment is current");
                    ((StatusFragment) f).reloadPages();
                }
                startActivity(new Intent(this, SettingsActivity.class));
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // called whenever we call invalidateOptionsMenu()
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // if nav drawer is open, hide actions other than settings and help
//        boolean open = isDrawerOpen();
//        MenuItem r = menu.findItem(R.id.action_refresh);
//        if ( r != null )
//            r.setVisible(!open);
        return super.onPrepareOptionsMenu(menu);
    }

    private class MyDrawerToggle extends ActionBarDrawerToggle {

        public MyDrawerToggle(Activity activity, DrawerLayout drawerLayout,
                              int drawerImageRes,
                              int openDrawerContentDescRes,
                              int closeDrawerContentDescRes) {
            super(activity, drawerLayout, drawerImageRes,
                    openDrawerContentDescRes, closeDrawerContentDescRes);
        }

        @Override
        public void onDrawerClosed(View drawerView) {
            super.onDrawerClosed(drawerView);
            Log.d(TAG, "DrawerClosed");
            updateContent();
            myInvalidateOptionsMenu();
            if (opened != null && !opened) {
                // drawer closed for the first time ever,
                // set that it has been closed
                opened = true;
                if (prefs != null) {
                    Editor editor = prefs.edit();
                    editor.putBoolean(OPENED_KEY, true);
                    editor.commit();
                }
            }
        }

        @Override
        public void onDrawerOpened(View drawerView) {
            super.onDrawerOpened(drawerView);
            getSupportActionBar().setTitle(R.string.app_name);
            myInvalidateOptionsMenu();
        }

    }

    private class DrawerItemClickListener implements
            ListView.OnItemClickListener {

        @Override
        public void onItemClick(
                AdapterView<?> parent,
                View view,
                int position,
                long id) {
            // Perform action when a drawer item is selected
            // call parent classes function
            // selectItem( position );
            mCurrentPosition = position;
            //mDrawerLayout.closeDrawer( mDrawerList );
            mDrawerLayout.closeDrawer(mDrawer);
        }

    }

//    public boolean isDrawerOpen() {
//        boolean f = false;
//        if ( mDrawerLayout != null && mDrawerList != null ) {
//            f = mDrawerLayout.isDrawerOpen(mDrawerList);
//        }
//        return f;
//    }
}
