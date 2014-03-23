package info.curtbinder.reefangel.phone;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

public class SettingsActivity extends PreferenceActivity {

    private static final String TAG = SettingsActivity.class.getSimpleName();

    protected Method mLoadHeaders = null;
    protected Method mHasHeaders = null;

    private RAApplication raApp;
    private RAPreferences raPrefs;

    private String[] devicesArray;
    private String[] profilesArray;

    /**
     * Checks to see if using the new v11+ way of handling PrefsFragments.
     *
     * @return Returns false pre-v11, else checks to see if using headers
     */
    public boolean isNewV11Prefs() {
        if (mHasHeaders != null && mLoadHeaders != null) {
            try {
                return (Boolean) mHasHeaders.invoke(this);
            } catch (IllegalArgumentException e) {

            } catch (IllegalAccessException e) {

            } catch (InvocationTargetException e) {

            }
        }
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // onBuildHeaders() will be called during super.onCreate()
        try {
            mLoadHeaders = getClass().getMethod("loadHeadersFromResource", int.class, List.class);
            mHasHeaders = getClass().getMethod("hasHeaders");
        } catch (NoSuchMethodException e) {

        }
        super.onCreate(savedInstanceState);

        raApp = (RAApplication) getApplication();
        raPrefs = raApp.raprefs;

        devicesArray = raApp.getResources().getStringArray(R.array.devices);
        profilesArray = raApp.getResources().getStringArray(R.array.profileLabels);

        if (!isNewV11Prefs()) {
            addPreferencesFromResource(R.xml.pref_profiles);
            addPreferencesFromResource(R.xml.pref_controller);
            addPreferencesFromResource(R.xml.pref_advanced);
            addPreferencesFromResource(R.xml.pref_notifications);
            addPreferencesFromResource(R.xml.pref_logging);
            addPreferencesFromResource(R.xml.pref_appinfo);
        }
    }

    public String getDevicesArrayValue(int index) {
        return devicesArray[index];
    }

    public String getProfilesArrayValue(int index) {
        return profilesArray[index];
    }

    public String getDisplayValue(
            int v,
            int arrayValuesId,
            int arrayDisplayId) {
        int pos = 0;
        String[] values = raApp.getResources().getStringArray(arrayValuesId);
        String[] display = raApp.getResources().getStringArray(arrayDisplayId);
        for (int i = 0; i < values.length; i++) {
            if (Integer.parseInt(values[i]) == v) {
                // found value
                pos = i;
                break;
            }
        }
        return display[pos];
    }

    public String getDisplayValueLong(
            long v,
            int arrayValuesId,
            int arrayDisplayId) {
        int pos = 0;
        String[] values = raApp.getResources().getStringArray(arrayValuesId);
        String[] display = raApp.getResources().getStringArray(arrayDisplayId);
        for (int i = 0; i < values.length; i++) {
            if (Long.parseLong(values[i]) == v) {
                // found value
                pos = i;
                break;
            }
        }
        return display[pos];
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onBuildHeaders(List<Header> target) {
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
        return PrefProfileFragment.class.getName().equals(fragmentName) ||
                ControllerFragment.class.getName().equals(fragmentName) ||
                PrefAutoUpdateFragment.class.getName().equals(fragmentName) ||
                PrefAdvancedFragment.class.getName().equals(fragmentName) ||
                PrefNotificationsFragment.class.getName().equals(fragmentName) ||
                PrefLoggingFragment.class.getName().equals(fragmentName) ||
                PrefAppFragment.class.getName().equals(fragmentName) ||
                super.isValidFragment(fragmentName);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class ControllerFragment extends PreferenceFragment {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            // load the preferences from an XML file
            addPreferencesFromResource(R.xml.pref_controller);
        }
    }
}
