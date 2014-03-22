package info.curtbinder.reefangel.phone;

import android.annotation.TargetApi;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;

/**
 * Created by binder on 3/22/14.
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class PrefAppFragment extends PreferenceFragment {

    @Override
    public void onCreate ( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );

        final RAApplication raApp = (RAApplication) getActivity().getApplication();

        // load the preferences from an XML file
        addPreferencesFromResource(R.xml.pref_appinfo);

        Preference changelog =
                findPreference( raApp.getString( R.string.prefChangelogKey ) );
        changelog
                .setOnPreferenceClickListener( new Preference.OnPreferenceClickListener() {
                    public boolean onPreferenceClick ( Preference preference ) {
                        Changelog.displayChangelog( getActivity() );
                        return true;
                    }
                } );
    }
}
