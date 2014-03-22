package info.curtbinder.reefangel.phone;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;

public class SettingsActivity extends PreferenceActivity {

    protected Method mLoadHeaders = null;
    protected Method mHasHeaders = null;

    /**
     * Checks to see if using the new v11+ way of handling PrefsFragments.
     * @return Returns false pre-v11, else checks to see if using headers
     */
    public boolean isNewV11Prefs() {
        if ( mHasHeaders != null && mLoadHeaders != null ) {
            try {
                return (Boolean)mHasHeaders.invoke(this);
            } catch (IllegalArgumentException e) {

            } catch (IllegalAccessException e) {

            } catch (InvocationTargetException e) {

            }
        }
        return false;
    }
	
	@Override
	protected void onCreate ( Bundle savedInstanceState ) {
        // onBuildHeaders() will be called during super.onCreate()
        try {
            mLoadHeaders = getClass().getMethod("loadHeadersFromResource", int.class, List.class);
            mHasHeaders = getClass().getMethod("hasHeaders");
        } catch (NoSuchMethodException e) {

        }
        super.onCreate( savedInstanceState );
        if ( !isNewV11Prefs() ) {
            addPreferencesFromResource(R.xml.pref_profiles);
            addPreferencesFromResource(R.xml.pref_controller);
            addPreferencesFromResource(R.xml.pref_advanced);
            addPreferencesFromResource(R.xml.pref_notifications);
            addPreferencesFromResource(R.xml.pref_logging);
            addPreferencesFromResource(R.xml.pref_appinfo);
        }
	}
	
	@Override
	public void onBuildHeaders ( List<Header> target ) {
        try {
            mLoadHeaders.invoke(this, new Object[]{R.xml.pref_headers, target});
        } catch (IllegalArgumentException e) {

        } catch (IllegalAccessException e) {

        } catch (InvocationTargetException e) {

        }
	}

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    protected boolean isValidFragment(String fragmentName) {
        return ProfileFragment.class.getName().equals(fragmentName) ||
                ControllerFragment.class.getName().equals(fragmentName) ||
                AdvancedFragment.class.getName().equals(fragmentName) ||
                NotificationFragment.class.getName().equals(fragmentName) ||
                LoggingFragment.class.getName().equals(fragmentName) ||
                AppFragment.class.getName().equals(fragmentName) ||
                super.isValidFragment(fragmentName);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class ProfileFragment extends PreferenceFragment {

        @Override
        public void onCreate ( Bundle savedInstanceState ) {
            super.onCreate( savedInstanceState );

            // load the preferences from an XML file
            addPreferencesFromResource(R.xml.pref_profiles);
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class ControllerFragment extends PreferenceFragment {

        @Override
        public void onCreate ( Bundle savedInstanceState ) {
            super.onCreate( savedInstanceState );

            // load the preferences from an XML file
            addPreferencesFromResource(R.xml.pref_controller);
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class AdvancedFragment extends PreferenceFragment {

        @Override
        public void onCreate ( Bundle savedInstanceState ) {
            super.onCreate( savedInstanceState );

            // load the preferences from an XML file
            addPreferencesFromResource(R.xml.pref_advanced);
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class NotificationFragment extends PreferenceFragment {

        @Override
        public void onCreate ( Bundle savedInstanceState ) {
            super.onCreate( savedInstanceState );

            // load the preferences from an XML file
            addPreferencesFromResource(R.xml.pref_notifications);
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class LoggingFragment extends PreferenceFragment {

        @Override
        public void onCreate ( Bundle savedInstanceState ) {
            super.onCreate( savedInstanceState );

            // load the preferences from an XML file
            addPreferencesFromResource(R.xml.pref_logging);
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class AppFragment extends PreferenceFragment {

        @Override
        public void onCreate ( Bundle savedInstanceState ) {
            super.onCreate( savedInstanceState );

            // load the preferences from an XML file
            addPreferencesFromResource(R.xml.pref_appinfo);
        }
    }
}
