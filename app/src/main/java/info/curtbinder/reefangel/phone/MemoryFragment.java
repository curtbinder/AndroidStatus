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

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import info.curtbinder.reefangel.db.StatusProvider;
import info.curtbinder.reefangel.db.UserMemoryLocationsTable;
import info.curtbinder.reefangel.service.MessageCommands;
import info.curtbinder.reefangel.service.RequestCommands;
import info.curtbinder.reefangel.service.UpdateService;

import static java.lang.Thread.sleep;

public class MemoryFragment extends Fragment {

    public static final String TAG = MemoryFragment.class.getSimpleName();

    final static int LOCATION_MIN = 0;
    final static int LOCATION_MAX = 1023;
    final static int LOCATION_START_OLD = 800;
    final static int LOCATION_START = 200;
    final static int TYPE_BYTE = 0;
    final static int TYPE_INT = 1;

    final static int PWM_MIN = 0;
    final static int PWM_MAX = 100;
    final static int HR_MIN = 0;
    final static int HR_MAX = 23;
    final static int MIN_MIN = 0;
    final static int MIN_MAX = 59;
    final static int WM_MIN = 0;
    final static int WM_MAX = 21600;
    final static int PH_MIN = 0;
    final static int PH_MAX = 1024;
    final static int TIMEOUTS_MIN = 0;
    final static int TIMEOUTS_MAX = 3600;

    public boolean preLocations;
    public boolean isController;
    private ViewPager mPager;
    private TabsPagerAdapter mPagerAdapter;

    MemoryReceiver receiver;
    IntentFilter filter;

    public static MemoryFragment newInstance(boolean fUsePre10Locations) {
        Bundle args = new Bundle();
        args.putBoolean(Globals.PRE10_LOCATIONS, fUsePre10Locations);
        MemoryFragment m = new MemoryFragment();
        m.setArguments(args);
        return m;
    }

    public MemoryFragment() {
        isController = false;
    }

    private void getUsePreLocations() {
        Bundle args = getArguments();
        preLocations = false;
        if ( args != null ) {
            preLocations = args.getBoolean(Globals.PRE10_LOCATIONS);
        }
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.frag_memory, container, false);

        isController = ((RAApplication) getActivity().getApplication()).raprefs.isCommunicateController();

        getUsePreLocations();
        createMessageReceiver();
        findViews(root);
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().registerReceiver(receiver, filter, Permissions.SEND_COMMAND, null);
        //TabMemoryInterface tab = (TabMemoryInterface) mPagerAdapter.getItem(mPager.getCurrentItem());
        //Log.d(TAG, "Resume: isController = " + isController);
        //tab.updateButtonsEnabled(isController);

        // TODO check on fOverridePHLimit
        //fOverridePHLimit = false;
    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(receiver);
    }

    private void createMessageReceiver() {
        // Message receiver
        receiver = new MemoryReceiver();
        filter = new IntentFilter( MessageCommands.MEMORY_RESPONSE_INTENT );
        // filter.addAction( MessageCommands.UPDATE_STATUS_INTENT );
    }

    private void findViews(View root) {
        mPager = root.findViewById(R.id.tab_pager);
        mPagerAdapter = new TabsPagerAdapter(getChildFragmentManager());
        mPager.setAdapter(mPagerAdapter);

    }

    public boolean checkLocationValue ( String s ) {
        boolean fRet = true;
        Log.d(TAG, "Location: '" + s + "'");
        if ( s.equals( "" ) ) {
            // Empty string
            Toast.makeText( getActivity(),
                    getResources().getString( R.string.messageEmptyLocation ),
                    Toast.LENGTH_SHORT ).show();
            return false;
        }
        int v = Integer.parseInt( s );
        if ( (v < LOCATION_MIN) || (v > LOCATION_MAX) ) {
            Toast.makeText( getActivity(),
                    getResources().getString( R.string.messageInvalidLocation,
                            LOCATION_MIN, LOCATION_MAX ),
                    Toast.LENGTH_SHORT ).show();
            fRet = false;
        }
        return fRet;
    }

    public void sendMessage ( boolean write, int location, int writeValue, boolean intChecked ) {
        Log.d( TAG, "sendMessage" );
        Intent i = new Intent( getActivity(), UpdateService.class );
        i.setAction( MessageCommands.MEMORY_SEND_INTENT );
        String type = RequestCommands.MemoryByte;
        int value = Globals.memoryReadOnly;
        int id = R.string.messageReadingMemory;

        if ( write ) {
            value = writeValue;
            id = R.string.messageWritingMemory;
        }

        if ( intChecked ) {
            type = RequestCommands.MemoryInt;
        }

        i.putExtra( MessageCommands.MEMORY_SEND_TYPE_STRING, type );
        i.putExtra( MessageCommands.MEMORY_SEND_LOCATION_INT, location );
        i.putExtra( MessageCommands.MEMORY_SEND_VALUE_INT, value );
        getActivity().startService(i);
        Toast.makeText( getActivity(), getResources().getString( id ),
                Toast.LENGTH_SHORT ).show();
    }

    class MemoryReceiver extends BroadcastReceiver {

        public void onReceive ( Context context, Intent intent ) {
            String action = intent.getAction();
            if ( action.equals( MessageCommands.MEMORY_RESPONSE_INTENT ) ) {
                boolean wasWrite =
                        intent.getBooleanExtra( MessageCommands.MEMORY_RESPONSE_WRITE_BOOLEAN,
                                false );
                String response =
                        intent.getStringExtra( MessageCommands.MEMORY_RESPONSE_STRING );
                if ( wasWrite ) {
                    // do something since we wrote
                    Toast.makeText( getActivity(), response, Toast.LENGTH_LONG ).show();
                } else {
                    // do something for read
                    // Update the value on the appropriate TAB/screen
                    TabMemoryInterface tab = (TabMemoryInterface) mPagerAdapter.getItem(mPager.getCurrentItem());
                    tab.updateValue(response);
                }
                // } else if ( action.equals(
                // MessageCommands.UPDATE_STATUS_INTENT ) ) {
                // TextView s = (TextView) findViewById( R.id.statusText );
                // int id =
                // intent.getIntExtra( MessageCommands.UPDATE_STATUS_ID,
                // -1 );
                // if ( id > -1 )
                // s.setText( id );
            }
        }
    }

    private class TabsPagerAdapter extends FragmentPagerAdapter {

        public TabsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            Fragment f;
            if (i == 0) {
                f = new TabUserFrag();
            } else {
                f = new TabBuiltInFrag();
            }
            return f;
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            int string_id = R.string.tabMemory1;
            if (position == 1 ) {
                string_id = R.string.tabMemory2;
            }
            return getString(string_id);
        }
    }

    public static class TabUserFrag extends Fragment implements TabMemoryInterface {

        public static final int CONFIRM_SAVE = 1;
        public static final int CONFIRM_DELETE = 2;

        public static final Uri USER_MEMORY_URI = Uri.parse(StatusProvider.CONTENT_URI + "/" + StatusProvider.PATH_USER_MEMORY);

        private Spinner locationSpinner;
        private static EditText valueText;
        private Button readButton;
        private Button writeButton;
        private ImageButton addButton;
        private ImageButton editButton;
        private ImageButton deleteButton;
        private TextView tvDisabled;
        ArrayList<MemoryData> memoryData;
        private int locationsCount;

        // Class for UserMemory data
        class MemoryData {
            public long id;
            public String name;
            public int location;
            public int type;

            private MemoryData(long id, String name, int location, int type) {
                this.id = id;
                this.name = name;
                this.location = location;
                this.type = type;
            }
        }

        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            return inflater.inflate(R.layout.tab_usermemory, container, false);
        }

        @Override
        public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);
            findViews(view);
            reloadUserMemoryLocations();
            setOnClickListeners();
            if (((MemoryFragment)getParentFragment()).isController) {
                tvDisabled.setVisibility(View.GONE);
            } else {
                // Connect to Portal, disallow all buttons
                tvDisabled.setVisibility(View.VISIBLE);
                addButton.setEnabled(false);
                controlsEnableDisable(false);
            }
        }

        private void findViews(View root) {
            locationSpinner = (Spinner) root.findViewById(R.id.spinMemoryLocation);
//            locationText = (EditText) root.findViewById(R.id.locationText);
            valueText = (EditText) root.findViewById(R.id.valueText);
            readButton = (Button) root.findViewById(R.id.buttonRead);
            writeButton = (Button) root.findViewById(R.id.buttonWrite);
            addButton = (ImageButton) root.findViewById(R.id.buttonAdd);
            editButton = (ImageButton) root.findViewById(R.id.buttonEdit);
            deleteButton = (ImageButton) root.findViewById(R.id.buttonDelete);
//            byteButton = (RadioButton) root.findViewById(R.id.radioButtonByte);
//            intButton = (RadioButton) root.findViewById(R.id.radioButtonInt);
            tvDisabled = (TextView) root.findViewById(R.id.tvDisabled);

            // Disable these buttons initially, potentially need to remove them since there's no use for them.
//            locationText.setEnabled(false);
//            byteButton.setEnabled(false);
//            intButton.setEnabled(false);
        }

        private void reloadUserMemoryLocations() {
            getUserMemoryLocationsTypesFromDB();
            setBuiltinAdapters();
            boolean fEnable = false;
            if ( locationsCount > 0 ) {
                setInitialValues();
                fEnable = true;
//                controlsEnableDisable(true);
//            } else {
                // TODO add item to spinner that says "No Locations"
//                controlsEnableDisable(false);
            }
            controlsEnableDisable(fEnable);
        }

        private void getUserMemoryLocationsTypesFromDB() {
            // clear the memory locations
            memoryData = new ArrayList<>();

            Cursor c = getUserMemoryCursor();
            locationsCount = c.getCount();
            Log.d(TAG, "getUserMemoryCount: " + locationsCount);
            int i = 0;
            while(c.moveToNext()) {
                int loc = c.getInt(c.getColumnIndexOrThrow(UserMemoryLocationsTable.COL_LOCATION));
                int type = c.getInt(c.getColumnIndexOrThrow(UserMemoryLocationsTable.COL_TYPE));
                String name = c.getString(c.getColumnIndexOrThrow(UserMemoryLocationsTable.COL_NAME));
                long id = c.getLong(c.getColumnIndexOrThrow(UserMemoryLocationsTable.COL_ID));
                Log.d(TAG, "Fill:  " + i + ") "+ id + ", " + name + ", " + loc + ", " + type);
                memoryData.add(new MemoryData(id, name, loc, type));
                i++;
            }
        }

        public Cursor getUserMemoryCursor() {
            return getActivity().getContentResolver().query(USER_MEMORY_URI, null, null, null,
                    UserMemoryLocationsTable.COL_ID);
        }

        private void setBuiltinAdapters() {
            UserMemoryCursorAdapter adapter = new UserMemoryCursorAdapter(getActivity(), getUserMemoryCursor());
            locationSpinner.setAdapter(adapter);
        }

        private void controlsEnableDisable(boolean fEnabled) {
            // TODO determine how to disable the buttons if we are connecting to the Portal
//            if(isController) {
//                addButton.setEnabled(fEnabled);
//            }
            readButton.setEnabled(fEnabled);
            writeButton.setEnabled(fEnabled);
            locationSpinner.setEnabled(fEnabled);
            valueText.setEnabled(fEnabled);
            editButton.setEnabled(fEnabled);
            deleteButton.setEnabled(fEnabled);
        }

        private void setInitialValues ( ) {
            locationSpinner.setSelection( 0 );
        }

        private boolean checkValueRange ( ) {
            boolean fRet = true;
            String s = valueText.getText().toString();
            Log.d(TAG, "Value: '" + s + "'");
            if (s.equals("")) {
                // Empty string
                Toast.makeText(getActivity(),
                        getResources().getString(R.string.messageEmptyValue),
                        Toast.LENGTH_SHORT).show();
                return false;
            }
            int v = Integer.parseInt(s);
            int sel = locationSpinner.getSelectedItemPosition();
            Log.d(TAG, "Selection: " + sel);
            // Verify the value is within range based on what TYPE is selected
            if (memoryData.get(sel).type == 1 ) {
//            if (memoryLocationsTypes[sel] == 1) {
                if ( (v < Globals.INT_MIN) || (v > Globals.INT_MAX) ) {
                    Toast.makeText( getActivity(),
                            getResources().getString( R.string.messageInvalidRangeFormat,
                                    Globals.INT_MIN, Globals.INT_MAX ),
                            Toast.LENGTH_SHORT ).show();
                    fRet = false;
                }
            } else {
                if ( (v < Globals.BYTE_MIN) || (v > Globals.BYTE_MAX) ) {
                    Toast.makeText( getActivity(),
                            getResources().getString( R.string.messageInvalidRangeFormat,
                                    Globals.BYTE_MIN, Globals.BYTE_MAX ),
                            Toast.LENGTH_SHORT ).show();
                    fRet = false;
                }
            }
            return fRet;
        }

        @Override
        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            switch(requestCode) {
                case CONFIRM_SAVE: {
                    if ( resultCode == Activity.RESULT_OK ) {
                        // pull the data from the Intent data
                        if ( data == null ) {
                            Log.d(TAG, "No data to save");
                            return;
                        }

                        long id = data.getLongExtra(UserMemoryLocationsTable.COL_ID, -1);

                        ContentValues cv = new ContentValues();
                        cv.put(UserMemoryLocationsTable.COL_NAME,
                                data.getStringExtra(UserMemoryLocationsTable.COL_NAME));
                        cv.put(UserMemoryLocationsTable.COL_LOCATION,
                                data.getIntExtra(UserMemoryLocationsTable.COL_LOCATION, 0));
                        cv.put(UserMemoryLocationsTable.COL_TYPE,
                                data.getBooleanExtra(UserMemoryLocationsTable.COL_TYPE, false));
                        if ( id == -1 ) {
                            // Add
                            getActivity().getContentResolver().insert(USER_MEMORY_URI, cv);
                        } else {
                            // Edit
                            getActivity().getContentResolver().update(USER_MEMORY_URI, cv,
                                    UserMemoryLocationsTable.COL_ID + "=?",
                                    new String[]{Long.toString(id)});
                            // TODO consider restoring the position in the spinner
                        }
                        reloadUserMemoryLocations();
                    } else if ( resultCode == CONFIRM_DELETE ) {
                        Log.d(TAG, "Deleted, reload");
                        if (data == null) {
                            Log.d(TAG, "No data returned, unable to delete");
                            return;
                        }

                        long id = data.getLongExtra(UserMemoryLocationsTable.COL_ID, -1);
                        if ( id == -1 ) {
                            Log.d(TAG, "Given default ID value, unable to delete");
                            return;
                        }
                        Log.d(TAG, "Delete location by ID " + id + ", position: " + locationSpinner.getSelectedItemPosition());
                        Uri deleteUri = Uri.withAppendedPath(USER_MEMORY_URI, Long.toString(id));
                        getActivity().getContentResolver().delete(deleteUri, null, null);
                        reloadUserMemoryLocations();
                    }
                    break;
                }
                case CONFIRM_DELETE: {
                    if ( resultCode == Activity.RESULT_OK) {
                        Log.d(TAG, "Deleted, reload");
                        // get the currently selected item from the list
                        int index = locationSpinner.getSelectedItemPosition();
                        // get the database id from the memory list
                        long id = memoryData.get(index).id;
                        Log.d(TAG, "Delete location by Index: " + index + ", col_id: " + id);
                        Uri deleteUri = Uri.withAppendedPath(USER_MEMORY_URI, Long.toString(id));
                        getActivity().getContentResolver().delete(deleteUri, null, null);
                        reloadUserMemoryLocations();
                    }
                    break;
                }
            }
        }

        private void setOnClickListeners ( ) {
            // create on click listeners
            final MemoryFragment mf = (MemoryFragment)getParentFragment();
            readButton.setOnClickListener( new View.OnClickListener() {
                public void onClick ( View v ) {
                    // get the selected item
                    int id = locationSpinner.getSelectedItemPosition();
                    Log.d(TAG, "Read Memory: " + id + ": " + memoryData.get(id).location + ", " + memoryData.get(id).type);
                    boolean fInt = false;
                    if ( memoryData.get(id).type == 1) {
                        fInt = true;
                    }
                    mf.sendMessage(false,
                            memoryData.get(id).location,
                            -1,
                            fInt);
                }
            } );
            writeButton.setOnClickListener( new View.OnClickListener() {
                public void onClick ( View v ) {
                    if ( !checkValueRange() ) {
                        return;
                    }
                    int id = locationSpinner.getSelectedItemPosition();
                    Log.d(TAG, "Write Memory: " + id + ": " + memoryData.get(id).location + ", "
                            + memoryData.get(id).type + ", " + valueText.getText().toString());
                    // good value, proceed
                    boolean fInt = false;
                    if ( memoryData.get(id).type == 1) {
                        fInt = true;
                    }
                    mf.sendMessage(true,
                            memoryData.get(id).location,
                            (int) Integer.parseInt( valueText.getText().toString() ),
                            fInt);
                }
            } );
            locationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                public void onItemSelected(
                        AdapterView<?> parent,
                        View v,
                        int position,
                        long id) {
                    // TODO save the current selected item
//                    currentSelection = id;
                }

                public void onNothingSelected(AdapterView<?> arg0) {
                }
            });

            // Need to save the Fragment (this) for the callbacks
            final Fragment f = this;
            addButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DialogAddUserMemoryLocation d1 = DialogAddUserMemoryLocation.newInstance();
                    d1.setTargetFragment(f, CONFIRM_SAVE);
                    d1.show(getFragmentManager(), "dlgadd");
                }
            });
            editButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int id = locationSpinner.getSelectedItemPosition();
                    Log.d(TAG, "Edit: pos: " + id + ") - " + memoryData.get(id).name + ", " + memoryData.get(id).location);
                    DialogAddUserMemoryLocation d2 = DialogAddUserMemoryLocation.newInstance(memoryData.get(id));
                    d2.setTargetFragment(f, CONFIRM_SAVE);
                    d2.show(getFragmentManager(), "dlgedit");
                }
            });
            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DialogYesNo d3 = DialogYesNo.newInstance(R.string.messageDeleteCurrentUserLocation);
                    d3.setTargetFragment(f, CONFIRM_DELETE);
                    d3.show(getFragmentManager(), "dlgyesno");
                }
            });
        }

        public void updateValue ( String value ) {
            if (valueText != null) {
                valueText.setText(value);
            }
        }

        public class UserMemoryCursorAdapter extends CursorAdapter {

            public UserMemoryCursorAdapter(Context context, Cursor cursor) {
                super(context, cursor, 0);
            }

            @Override
            public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
                // inflate thew view, but don't bind data to it
                return LayoutInflater.from(context).inflate(R.layout.cursor_user_memory, viewGroup, false);
            }

            @Override
            public void bindView(View view, Context context, Cursor cursor) {
                TextView tvName = (TextView) view.findViewById(R.id.textViewMemoryName);
                TextView tvDetails = (TextView) view.findViewById(R.id.textViewMemoryDetails);

                // Get the data from the cursor
                String name = cursor.getString(cursor.getColumnIndexOrThrow(UserMemoryLocationsTable.COL_NAME));
                tvName.setText(name);

                int loc = cursor.getInt(cursor.getColumnIndexOrThrow(UserMemoryLocationsTable.COL_LOCATION));
                int type = cursor.getInt(cursor.getColumnIndexOrThrow(UserMemoryLocationsTable.COL_TYPE));
                String details = "(" + loc + ", ";
                if (type == 1) {
                    details += "int)";
                } else {
                    details += "byte)";
                }
                tvDetails.setText(details);
            }
        }
    }

    public static class TabBuiltInFrag extends Fragment implements TabMemoryInterface {

        private Spinner locationSpinner;
        private EditText locationText;
        private static EditText valueText;
        private Button readButton;
        private Button writeButton;
        private RadioButton byteButton;
        private RadioButton intButton;
        private TextView tvDisabled;
        private int[] memoryLocations;
        private int[] memoryLocationsTypes;
        boolean fOverridePHLimit;


        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            return inflater.inflate(R.layout.tab_builtinmemory, container, false);
        }

        @Override
        public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);

            findViews(view);
            setBuiltinAdapters();
            setOnClickListeners();
            updateButtonsEnabled(((MemoryFragment)getParentFragment()).isController);
            memoryLocations = getResources().getIntArray( R.array.memoryLocations );
            memoryLocationsTypes = getResources().getIntArray( R.array.memoryLocationsTypes );
            setInitialValues();
        }

        public void updateButtonsEnabled(boolean isController) {
            readButton.setEnabled(isController);
            writeButton.setEnabled(isController);
            if (isController) {
                tvDisabled.setVisibility(View.GONE);
            } else {
                tvDisabled.setVisibility(View.VISIBLE);
            }
        }

        private void findViews(View root) {
            locationSpinner = (Spinner) root.findViewById(R.id.spinMemoryLocation);
            locationText = (EditText) root.findViewById(R.id.locationText);
            valueText = (EditText) root.findViewById(R.id.valueText);
            readButton = (Button) root.findViewById(R.id.buttonRead);
            writeButton = (Button) root.findViewById(R.id.buttonWrite);
            byteButton = (RadioButton) root.findViewById(R.id.radioButtonByte);
            intButton = (RadioButton) root.findViewById(R.id.radioButtonInt);
            tvDisabled = (TextView) root.findViewById(R.id.tvDisabled);
        }

        private void setBuiltinAdapters() {
            ArrayAdapter<CharSequence> adapter = ArrayAdapter
                    .createFromResource(getActivity(),
                            R.array.memoryLocationsNames,
                            android.R.layout.simple_spinner_item );
            adapter.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item );
            locationSpinner.setAdapter( adapter );
        }

        private boolean checkValueRange ( ) {
            // TODO change this function to check the special locations against the memory VALUES instead of the array indices
            boolean fRet = true;
            String s = valueText.getText().toString();
            Log.d( TAG, "Value: '" + s + "'" );
            if ( s.equals( "" ) ) {
                // Empty string
                Toast.makeText( getActivity(),
                        getResources().getString( R.string.messageEmptyValue ),
                        Toast.LENGTH_SHORT ).show();
                return false;
            }
            int v = Integer.parseInt( s );
            int sel = locationSpinner.getSelectedItemPosition();
            Log.d( TAG, "Selection: " + sel );
            if ( intButton.isChecked() ) {
                if ( isSpecialLocation( sel, R.array.wavemakersIndex ) ) {
                    if ( (v < WM_MIN) || (v > WM_MAX) ) {
                        Toast.makeText( getActivity(),
                                getResources().getString( R.string.messageInvalidRangeFormat,
                                        WM_MIN, WM_MAX ),
                                Toast.LENGTH_SHORT ).show();
                        fRet = false;
                    }
                } else if ( isSpecialLocation( sel, R.array.phIndex ) ) {
                    if ( (v < PH_MIN) || (v > PH_MAX) ) {
                        if (fOverridePHLimit) {
                            // We are allowing the user to override the built-in safeguard for values of pH
                            // We need to make sure the value falls within the INT range before letting them
                            // write a new value to their locations
                            if ( (v < Globals.INT_MIN) || (v > Globals.INT_MAX) ) {
                                Toast.makeText( getActivity(),
                                        getResources().getString( R.string.messageInvalidRangeFormat,
                                                Globals.INT_MIN, Globals.INT_MAX ),
                                        Toast.LENGTH_SHORT ).show();
                                fRet = false;
                            }
                        } else {
                            Toast.makeText(getActivity(),
                                    getResources().getString(R.string.messageInvalidRangeFormat,
                                            PH_MIN, PH_MAX),
                                    Toast.LENGTH_SHORT).show();
                            fRet = false;
                            // Time to allow the user to override the hard coded pH limitations
                            // We will still show the warning about the value BUT we will allow the user
                            // to press the WRITE button again within 10 seconds to override the limitations
                            // as long as they are within the proper range of the INT variable type
                            fOverridePHLimit = true;
                            Handler h = new Handler();
                            h.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    fOverridePHLimit = false;
                                }
                            }, 10000);
                        }
                    }
                } else if ( isSpecialLocation( sel, R.array.timeoutIndex ) ) {
                    if ( (v < TIMEOUTS_MIN) || (v > TIMEOUTS_MAX) ) {
                        Toast.makeText( getActivity(),
                                getResources().getString( R.string.messageInvalidRangeFormat,
                                        TIMEOUTS_MIN,
                                        TIMEOUTS_MAX ),
                                Toast.LENGTH_SHORT ).show();
                        fRet = false;
                    }
                } else {
                    if ( (v < Globals.INT_MIN) || (v > Globals.INT_MAX) ) {
                        Toast.makeText( getActivity(),
                                getResources().getString( R.string.messageInvalidRangeFormat,
                                        Globals.INT_MIN, Globals.INT_MAX ),
                                Toast.LENGTH_SHORT ).show();
                        fRet = false;
                    }
                }
            } else if ( byteButton.isChecked() ) {
                if ( isSpecialLocation( sel, R.array.hourIndex ) ) {
                    if ( (v < HR_MIN) || (v > HR_MAX) ) {
                        Toast.makeText( getActivity(),
                                getResources().getString( R.string.messageInvalidRangeFormat,
                                        HR_MIN, HR_MAX ),
                                Toast.LENGTH_SHORT ).show();
                        fRet = false;
                    }
                } else if ( isSpecialLocation( sel, R.array.minuteIndex ) ) {
                    if ( (v < MIN_MIN) || (v > MIN_MAX) ) {
                        Toast.makeText( getActivity(),
                                getResources().getString( R.string.messageInvalidRangeFormat,
                                        MIN_MIN, MIN_MAX ),
                                Toast.LENGTH_SHORT ).show();
                        fRet = false;
                    }
                } else if ( isSpecialLocation( sel, R.array.pwmIndex ) ) {
                    if ( (v < PWM_MIN) || (v > PWM_MAX) ) {
                        Toast.makeText(getActivity(),
                                getResources().getString(R.string.messageInvalidRangeFormat,
                                        PWM_MIN, PWM_MAX),
                                Toast.LENGTH_SHORT).show();
                        fRet = false;
                    }
                } else {
                    if ( (v < Globals.BYTE_MIN) || (v > Globals.BYTE_MAX) ) {
                        Toast.makeText( getActivity(),
                                getResources().getString( R.string.messageInvalidRangeFormat,
                                        Globals.BYTE_MIN, Globals.BYTE_MAX ),
                                Toast.LENGTH_SHORT ).show();
                        fRet = false;
                    }
                }
            }
            return fRet;
        }

        private boolean isSpecialLocation ( int pos, int arrayID ) {
            // TODO convert to use the LOCATIONS instead of array indices
            int[] loc = getResources().getIntArray( arrayID );
            boolean fRet = false;
            for ( int i = 0; i < loc.length; i++ ) {
                if ( loc[i] == pos ) {
                    fRet = true;
                    break;
                }
            }
            return fRet;
        }

        private void setOnClickListeners ( ) {
            // create on click listeners
            final MemoryFragment mf = (MemoryFragment)getParentFragment();
            readButton.setOnClickListener( new View.OnClickListener() {
                public void onClick ( View v ) {
                    if ( !mf.checkLocationValue(locationText.getText().toString()) ) {
                        return;
                    }
                    // good location, proceed
                    mf.sendMessage(false,
                            (int) Integer.parseInt( locationText.getText().toString() ),
                            -1,
                            intButton.isChecked());
                }
            } );
            writeButton.setOnClickListener( new View.OnClickListener() {
                public void onClick ( View v ) {
                    if ( !mf.checkLocationValue(locationText.getText().toString()) ) {
                        return;
                    }
                    if ( !checkValueRange() ) {
                        return;
                    }
                    // good location and value, proceed
                    mf.sendMessage(true,
                            (int) Integer.parseInt( locationText.getText().toString() ),
                            (int) Integer.parseInt( valueText.getText().toString() ),
                            intButton.isChecked());
                }
            } );
            locationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                public void onItemSelected(
                        AdapterView<?> parent,
                        View v,
                        int position,
                        long id) {
                    setItemSelected((int) id);
                }

                public void onNothingSelected(AdapterView<?> arg0) {
                }
            });
        }

        private void setItemSelected ( int id ) {
            boolean enable = false;
            int start;
            if ( ((MemoryFragment)getParentFragment()).preLocations ) {
                start = LOCATION_START_OLD;
            } else {
                start = LOCATION_START;
            }

            if ( id > 0 ) {
                String s = String.format( "%d", start + memoryLocations[id] );
                locationText.setText( s );
            } else {
                locationText.setText( "" );
            }
            // update the radio button
            if ( memoryLocationsTypes[id] == TYPE_BYTE ) {
                byteButton.setChecked( true );
                intButton.setChecked( false );
            } else {
                byteButton.setChecked( false );
                intButton.setChecked( true );
            }

            if ( id == 0 ) {
                enable = true;
            }
            updateLocationEditability( enable );
        }

        private void setInitialValues ( ) {
            locationSpinner.setSelection( 1 );
            setItemSelected( 1 );
        }

        private void updateLocationEditability ( boolean enable ) {
            // updates the enabling/disabling of the location & radio buttons based
            // on the location drop down menu
            byteButton.setEnabled( enable );
            intButton.setEnabled( enable );
            locationText.setEnabled( enable );
            if ( enable ) {
                locationText.requestFocus();
            } else {
                valueText.requestFocus();
            }
        }

        public void updateValue ( String value ) {
            if (valueText != null) {
                valueText.setText(value);
            }
        }
    }

    public interface TabMemoryInterface {
        public void updateValue( String value );
    }
}
