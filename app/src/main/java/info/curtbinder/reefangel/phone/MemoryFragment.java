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
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import info.curtbinder.reefangel.service.MessageCommands;
import info.curtbinder.reefangel.service.RequestCommands;
import info.curtbinder.reefangel.service.UpdateService;

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

    private Spinner locationSpinner;
    private EditText locationText;
    private EditText valueText;
    private Button readButton;
    private Button writeButton;
    private RadioButton byteButton;
    private RadioButton intButton;
    private TextView tvDisabled;
    private int[] memoryLocations;
    private int[] memoryLocationsTypes;
    private boolean preLocations;
    boolean fOverridePHLimit;

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

        getUsePreLocations();
        createMessageReceiver();
        findViews(root);
        setAdapters();
        setOnClickListeners();
        memoryLocations = getResources().getIntArray( R.array.memoryLocations );
        memoryLocationsTypes = getResources().getIntArray( R.array.memoryLocationsTypes );
        setInitialValues();
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().registerReceiver(receiver, filter, Permissions.SEND_COMMAND, null);
        updateButtonsEnabled();
        fOverridePHLimit = false;
    }

    private void updateButtonsEnabled() {
        boolean fClickable = ((RAApplication) getActivity().getApplication()).raprefs.isCommunicateController();
        readButton.setEnabled(fClickable);
        writeButton.setEnabled(fClickable);
        if (fClickable) {
            tvDisabled.setVisibility(View.GONE);
        } else {
            tvDisabled.setVisibility(View.VISIBLE);
        }
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
        locationSpinner = (Spinner) root.findViewById(R.id.spinMemoryLocation);
        locationText = (EditText) root.findViewById(R.id.locationText);
        valueText = (EditText) root.findViewById(R.id.valueText);
        readButton = (Button) root.findViewById(R.id.buttonRead);
        writeButton = (Button) root.findViewById(R.id.buttonWrite);
        byteButton = (RadioButton) root.findViewById(R.id.radioButtonByte);
        intButton = (RadioButton) root.findViewById(R.id.radioButtonInt);
        tvDisabled = (TextView) root.findViewById(R.id.tvDisabled);
    }

    private void setAdapters() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter
                        .createFromResource(getActivity(),
                                R.array.memoryLocationsNames,
                                android.R.layout.simple_spinner_item );
        adapter.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item );
        locationSpinner.setAdapter( adapter );
    }

    private boolean checkValueRange ( ) {
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

    private boolean checkLocationValue ( ) {
        boolean fRet = true;
        String s = locationText.getText().toString();
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

    private void setOnClickListeners ( ) {
        // create on click listeners
        readButton.setOnClickListener( new View.OnClickListener() {
            public void onClick ( View v ) {
                if ( !checkLocationValue() ) {
                    return;
                }
                // good location, proceed
                sendMessage( false );
            }
        } );
        writeButton.setOnClickListener( new View.OnClickListener() {
            public void onClick ( View v ) {
                if ( !checkLocationValue() ) {
                    return;
                }
                if ( !checkValueRange() ) {
                    return;
                }
                // good location and value, proceed
                sendMessage( true );
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
        if ( preLocations ) {
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
        valueText.setText( value );
    }

    private void sendMessage ( boolean write ) {
        Log.d( TAG, "sendMessage" );
        Intent i = new Intent( getActivity(), UpdateService.class );
        i.setAction( MessageCommands.MEMORY_SEND_INTENT );
        String type = RequestCommands.MemoryByte;
        int value = Globals.memoryReadOnly;
        int id = R.string.messageReadingMemory;

        if ( write ) {
            value = (int) Integer.parseInt( valueText.getText().toString() );
            id = R.string.messageWritingMemory;
        }

        if ( intButton.isChecked() )
            type = RequestCommands.MemoryInt;

        i.putExtra( MessageCommands.MEMORY_SEND_TYPE_STRING, type );
        i.putExtra( MessageCommands.MEMORY_SEND_LOCATION_INT,
                (int) Integer.parseInt( locationText.getText().toString() ) );
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
                    updateValue( response );
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
}
