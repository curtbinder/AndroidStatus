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

package info.curtbinder.reefangel.phone;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Configuration;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
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
import android.widget.ListView;

import info.curtbinder.reefangel.wizard.SetupWizardActivity;

public class MainActivity extends ActionBarActivity
        implements android.support.v7.app.ActionBar.OnNavigationListener,
        FragmentManager.OnBackStackChangedListener {

//    public static final int REQUEST_EXIT = 1;
//    public static final int RESULT_EXIT = 1024;

    private static final String OPENED_KEY = "OPENED_KEY";
    private static final String STATE_CHECKED = "DRAWER_CHECKED";
    private static final String PREVIOUS_CHECKED = "PREVIOUS";

    // do not switch selected profile when restoring the application state
    private static boolean fRestoreState = false;
    public final String TAG = MainActivity.class.getSimpleName();
    private RAApplication raApp;
    private String[] mNavTitles;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private SharedPreferences prefs = null;
    private Boolean opened = null;
    private int mOldPosition = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        raApp = (RAApplication) getApplication();

        // Check for first run
        if (raApp.isFirstRun()) {
            Intent i = new Intent(this, SetupWizardActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(i);
            finish();
        }

        // Load any saved position
        int position = 0;
        if (savedInstanceState != null) {
            position = savedInstanceState.getInt(STATE_CHECKED, 0);
            Log.d(TAG, "Restore, position: " + position);
        }
        setupNavDrawer();
        updateActionBar();
        getSupportFragmentManager().addOnBackStackChangedListener(this);
        selectItem(position);

        // launch a new thread to show the drawer on very first app launch
        new Thread(new Runnable() {
            @Override
            public void run() {
                prefs = getPreferences(MODE_PRIVATE);
                opened = prefs.getBoolean(OPENED_KEY, false);
                if (!opened) {
                    mDrawerLayout.openDrawer(mDrawerList);
                }
            }
        }).start();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // get the checked item and subtract off one to get the actual position
        // the same logic applies that is used in the DrawerItemClickedListener.onItemClicked
        outState.putInt(STATE_CHECKED, mDrawerList.getCheckedItemPosition() - 1);
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

    private void setupNavDrawer() {
        // get the string array for the navigation items
        mNavTitles = getResources().getStringArray(R.array.nav_items);

        // locate the navigation drawer items in the layout
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        // set a custom shadow that overlays the main content when the drawer
        // opens
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow,
                GravityCompat.START);

        // add in the logo header
        View header = getLayoutInflater().inflate(R.layout.drawer_list_header, null);
        mDrawerList.addHeaderView(header, null, false);

        // set the adapter for the navigation list view
        ArrayAdapter<String> adapter =
                new ArrayAdapter<String>(this, R.layout.drawer_list_item,
                        mNavTitles);
        mDrawerList.setAdapter(adapter);
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        // setup the toggling for the drawer
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
        // hide the icon on the actionbar by replacing it with a transparent icon
        ab.setIcon(new ColorDrawable(getResources().getColor(android.R.color.transparent)));
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

    @Override
    public void onBackPressed() {
        /*
        When the back button is pressed, this function is called.
        If the drawer is open, check it and cancel it here.

        Calling super.onBackPressed() causes the BackStackChangeListener to be called
         */
//        Log.d(TAG, "onBackPressed");
        if (mDrawerLayout.isDrawerOpen(mDrawerList)) {
//            Log.d(TAG, "drawer open, closing");
            mDrawerLayout.closeDrawer(mDrawerList);
            return;
        }
        super.onBackPressed();
    }

    @Override
    public void onBackStackChanged() {
//        Log.d(TAG, "onBackStackChanged");
        /*
        This is called when there is a change to the backstack.
        The call is when items are added or removed from the backstack.
        Mostly just need to update the UI in this function.

        Since we are replacing all the fragments in our example, we need to check
        for an empty backstack and exit the app when it is encountered. If we do not,
        we are left with a blank screen in the activity and the last fragment is removed.
        We are then required to press back one more time to actually exit the application.
        This check combines the extra step to make it function more naturally.
         */
        FragmentManager manager = getSupportFragmentManager();
        //dumpBackStack(manager);
        int count = manager.getBackStackEntryCount();
        if (count == 0) {
//            Log.d(TAG, "empty backstack, exit");
            finish();
            return;
        }
        /*
        Our fragment backstack uses the position number in the navigation drawer as
        the fragment tag for simplicity with highlighting the item.

        uncertain how this will be handled if we have additional fragments added to the
        backstack that are not referenced in the navigation drawer
         */
        FragmentManager.BackStackEntry entry = manager.getBackStackEntryAt(count - 1);
        int last = Integer.parseInt(entry.getName());
        highlightItem(last);
    }

    /*private void dumpBackStack(FragmentManager fm) {
        // dumps the contents of the backstack in a string
        int count = fm.getBackStackEntryCount();
        // 0 based index, last item is 1 less than count
        FragmentManager.BackStackEntry e;
        String s = "";
        for (int i = count - 1; i >= 0; i--) {
            e = fm.getBackStackEntryAt(i);
            s += e.getName() + ", ";
        }
        s += "null";
        Log.d(TAG, "BS Dump (" + count + "): " + s);
    }*/

    private void updateContent(int position) {
        getSupportActionBar().setTitle(mNavTitles[position]);
        if (position != mOldPosition) {
            // update the main content by replacing fragments
            Fragment fragment;
            switch (position) {
                default:
                case 0:
                    fragment = StatusFragment.newInstance();
                    break;
                case 1:
                    fragment = MemoryFragment.newInstance(raApp.raprefs.useOldPre10MemoryLocations());
                    break;
                case 2:
                    fragment = NotificationsFragment.newInstance();
                    break;
                case 3:
                    fragment = HistoryFragment.newInstance();
                    break;
                case 4:
                    fragment = ErrorsFragment.newInstance();
                    break;
                case 5:
                    fragment = DateTimeFragment.newInstance();
                    break;
            }

            Log.d(TAG, "UpdateContent: " + position);
            FragmentTransaction ft =
                    getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_frame, fragment);
            ft.addToBackStack("" + position);
            ft.commit();
            mOldPosition = position;
        }
    }

    public void selectItem(int position) {
//        Log.d(TAG, "selectItem: " + position);
        updateContent(position);
        mDrawerLayout.closeDrawer(mDrawerList);
    }

    public void highlightItem(int position) {
//        Log.d(TAG, "highlightItem: " + position);
        // since we are using a header for the list, the first
        // item/position in the list is the header. our header is non-selectable
        // so in order for us to have the proper item in our list selected, we must
        // increase the position by 1. this same logic is applied to the
        // DrawerItemClickedListener.onItemClicked
        mDrawerList.setItemChecked(position + 1, true);
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
                if (f instanceof StatusFragment) {
                    // the current fragment is the status fragment
                    Log.d(TAG, "Status Fragment is current");
                    ((StatusFragment) f).reloadPages();
                }
                startActivity(new Intent(this, SettingsActivity.class));
//                startActivityForResult(new Intent(this, SettingsActivity.class), REQUEST_EXIT);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        Log.d(TAG, "onActivityResult");
//        if (requestCode == REQUEST_EXIT) {
//            if (resultCode == RESULT_EXIT) {
//                this.finish();
//            }
//        }
//    }

    // called whenever we call invalidateOptionsMenu()
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        /*
        This function is called after invalidateOptionsMenu is called.
        This happens when the Navigation drawer is opened and closed.
         */
        boolean open = mDrawerLayout.isDrawerOpen(mDrawerList);
        hideMenuItems(menu, open);
        return super.onPrepareOptionsMenu(menu);
    }

    private void hideMenuItems(Menu menu, boolean open) {
        // hide the menu item(s) when the drawer is open
        // Refresh button on Status page
        MenuItem mi = menu.findItem(R.id.action_refresh);
        if ( mi != null )
            mi.setVisible(!open);

        // Add button on Notification page
        mi = menu.findItem(R.id.action_add_notification);
        if ( mi != null )
            mi.setVisible(!open);
        // Delete button on Notification page
        mi = menu.findItem(R.id.action_delete_notification);
        if ( mi != null )
            mi.setVisible(!open);

        // Delete button on History and Error pages
        mi = menu.findItem(R.id.menu_delete);
        if ( mi != null )
            mi.setVisible(!open);
    }

    @SuppressLint("NewApi")
    protected void myInvalidateOptionsMenu() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            supportInvalidateOptionsMenu();
        } else {
            invalidateOptionsMenu();
        }
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
//            Log.d(TAG, "DrawerClosed");
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
//            Log.d(TAG, "onDrawerItemClick: " + position);
            // when we have a list header, it counts as a position in the list
            // the first position to be exact. so we have to decrease the
            // position by 1 to get the proper item chosen in our list
            selectItem(position - 1);
        }

    }
}
