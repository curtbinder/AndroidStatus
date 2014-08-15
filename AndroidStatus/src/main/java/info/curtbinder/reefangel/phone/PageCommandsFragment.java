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

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TableRow;
import android.widget.TextView;

import info.curtbinder.reefangel.controller.Controller;
import info.curtbinder.reefangel.service.MessageCommands;
import info.curtbinder.reefangel.service.RequestCommands;
import info.curtbinder.reefangel.service.UpdateService;

public class PageCommandsFragment extends Fragment
        implements PageRefreshInterface, View.OnClickListener {

    private static final String TAG = PageCommandsFragment.class.getSimpleName();

    private Button cmd_button;

    public static PageCommandsFragment newInstance() {
        return new PageCommandsFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.page_commands, container, false);
        // do something with the rootView
        findViews(rootView);
        return rootView;
    }

    private void findViews(View root) {
        Button b = (Button) root.findViewById( R.id.command_button_feed );
        b.setOnClickListener( this );
        b = (Button) root.findViewById( R.id.command_button_water );
        b.setOnClickListener( this );
        b = (Button) root.findViewById( R.id.command_button_exit );
        b.setOnClickListener( this );
        b = (Button) root.findViewById( R.id.command_button_reboot );
        b.setOnClickListener( this );
        b = (Button) root.findViewById( R.id.command_button_lights_on );
        b.setOnClickListener( this );
        b = (Button) root.findViewById( R.id.command_button_lights_off );
        b.setOnClickListener( this );
        b = (Button) root.findViewById( R.id.command_button_ato_clear );
        b.setOnClickListener( this );
        b = (Button) root.findViewById( R.id.command_button_overheat_clear );
        b.setOnClickListener( this );
        b = (Button) root.findViewById( R.id.command_button_calibrate_ph );
        b.setOnClickListener( this );
        b = (Button) root.findViewById( R.id.command_button_calibrate_salinity );
        b.setOnClickListener( this );
        b = (Button) root.findViewById( R.id.command_button_calibrate_water );
        b.setOnClickListener( this );
        b = (Button) root.findViewById( R.id.command_button_calibrate_orp );
        b.setOnClickListener( this );
        b = (Button) root.findViewById( R.id.command_button_calibrate_phe );
        b.setOnClickListener( this );
        cmd_button = (Button) root.findViewById( R.id.command_button_version );
        cmd_button.setOnClickListener( this );
    }

    @Override
    public void refreshData() {
        // todo not sure if there needs to be anything in here or not
        // just reload the date/time from the database?
    }

    @Override
    public void onClick ( View v ) {
        Intent i = new Intent( getActivity(), UpdateService.class );
        String s = RequestCommands.ExitMode;
        String action = MessageCommands.COMMAND_SEND_INTENT;
        String command = MessageCommands.COMMAND_SEND_STRING;
        int location = -1;
        switch ( v.getId() ) {
            case R.id.command_button_feed:
                s = RequestCommands.FeedingMode;
                break;
            case R.id.command_button_water:
                s = RequestCommands.WaterMode;
                break;
            case R.id.command_button_lights_on:
                s = RequestCommands.LightsOn;
                break;
            case R.id.command_button_lights_off:
                s = RequestCommands.LightsOff;
                break;
            case R.id.command_button_ato_clear:
                s = RequestCommands.AtoClear;
                break;
            case R.id.command_button_overheat_clear:
                s = RequestCommands.OverheatClear;
                break;
            case R.id.command_button_reboot:
                s = RequestCommands.Reboot;
                break;
            case R.id.command_button_calibrate_ph:
                location = Globals.CALIBRATE_PH;
                break;
            case R.id.command_button_calibrate_phe:
                location = Globals.CALIBRATE_PHE;
                break;
            case R.id.command_button_calibrate_orp:
                location = Globals.CALIBRATE_ORP;
                break;
            case R.id.command_button_calibrate_salinity:
                location = Globals.CALIBRATE_SALINITY;
                break;
            case R.id.command_button_calibrate_water:
                location = Globals.CALIBRATE_WATERLEVEL;
                break;
            case R.id.command_button_version:
                action = MessageCommands.VERSION_QUERY_INTENT;
                s = RequestCommands.Version;
        }
        if ( location > -1 ) {
            // location is greater than -1, which means we have a calibration command
            // command & s are ignored by the service since the command is the same
            // for all of the calibration modes
            i.putExtra(MessageCommands.CALIBRATE_SEND_LOCATION_INT, location);
            action = MessageCommands.CALIBRATE_SEND_INTENT;
        }
        i.setAction( action );
        i.putExtra( command, s );
        getActivity().startService(i);
    }

    public void setButtonVersion(String msg) {
        cmd_button.setText(msg);
    }
}
