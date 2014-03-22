package info.curtbinder.reefangel.phone;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceFragment;

/**
 * Created by binder on 3/22/14.
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class PrefAdvancedFragment extends PreferenceFragment {

    @Override
    public void onCreate ( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );

        // load the preferences from an XML file
        addPreferencesFromResource(R.xml.pref_advanced);

    }
}
