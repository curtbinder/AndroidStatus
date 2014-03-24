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

/**
 * Created by binder on 3/23/14.
 */
public class PageCommandsFragment extends Fragment
        implements PageRefreshInterface, View.OnClickListener {

    private static final String TAG = PageCommandsFragment.class.getSimpleName();


    public static PageCommandsFragment newInstance() {
        PageCommandsFragment p = new PageCommandsFragment();
        return p;
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
    }

    @Override
    public void refreshData() {
        // todo not sure if there needs to be anything in here or not
    }

    @Override
    public void onClick ( View v ) {
        Intent i = new Intent( getActivity(), UpdateService.class );
        String s = RequestCommands.ExitMode;
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
        }
        i.setAction( MessageCommands.COMMAND_SEND_INTENT );
        i.putExtra( MessageCommands.COMMAND_SEND_STRING, s );
        getActivity().startService(i);
    }
}
