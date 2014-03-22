package info.curtbinder.reefangel.phone;

import android.annotation.TargetApi;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.util.Log;

/**
 * Created by binder on 3/22/14.
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class AutoUpdateFragment extends PreferenceFragment
        implements Preference.OnPreferenceChangeListener,
                    SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String TAG = AutoUpdateFragment.class.getSimpleName();

    private RAApplication raApp;
    private RAPreferences raPrefs;
    private Preference updateprofilekey;

    @Override
    public void onCreate ( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );

        raApp = (RAApplication) getActivity().getApplication();
        raPrefs = raApp.raprefs;

        // load the preferences from an XML file
        addPreferencesFromResource(R.xml.pref_autoupdate);

        updateprofilekey =
                findPreference( raApp
                        .getString( R.string.prefAutoUpdateProfileKey ) );
        updateprofilekey.setOnPreferenceChangeListener( this );
        if ( raApp.isAwayProfileEnabled() ) {
            updateAutoUpdateProfileVisibility( true );
        } else {
            updateAutoUpdateProfileVisibility( false );
        }
        updateprofilekey.setSummary( getUpdateProfileDisplay() );
        updateAutoUpdateIntervalSummary();
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    private void updateAutoUpdateIntervalSummary() {
        findPreference( raApp.getString( R.string.prefAutoUpdateIntervalKey ) )
                .setSummary( getUpdateIntervalDisplay() );
    }

    private void updateAutoUpdateProfileVisibility(boolean fVisible) {
        updateprofilekey.setEnabled(fVisible);
        updateprofilekey.setSelectable(fVisible);
    }

    private String getUpdateIntervalDisplay ( ) {
        int pos = 0;
        long value = raPrefs.getUpdateInterval();
        String[] interval =
                raApp.getResources()
                        .getStringArray( R.array.updateIntervalValues );
        String[] intervalDisplay =
                raApp.getResources().getStringArray( R.array.updateInterval );
        for ( int i = 0; i < interval.length; i++ ) {
            if ( Long.parseLong( interval[i] ) == value ) {
                // found value
                pos = i;
                break;
            }
        }
        return intervalDisplay[pos];
    }

    public String getUpdateProfileDisplay ( ) {
        return ((SettingsActivity) getActivity()).getDisplayValue(raPrefs.getUpdateProfile(),
                R.array.updateProfileValues, R.array.updateProfile);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if ( key.equals( raApp
                .getString( R.string.prefAutoUpdateIntervalKey ) ) ) {
            // when interval changes, update the repeat service
            updateAutoUpdateIntervalSummary();
            raApp.restartAutoUpdateService();
            boolean fVisible = false;
            if ( raApp.isAwayProfileEnabled()
                    && (raPrefs.getUpdateInterval() > 0) ) {
                Log.d(TAG, "enable update profile");
                fVisible = true;
            }
            updateAutoUpdateProfileVisibility( fVisible );
        } else if ( key.equals( raApp
                .getString( R.string.prefAutoUpdateProfileKey ) ) ) {
            // restart the update service if we change the update profile
            updateprofilekey.setSummary( getUpdateProfileDisplay() );
            raApp.restartAutoUpdateService();
        }
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object key) {
        return true;
    }
}
