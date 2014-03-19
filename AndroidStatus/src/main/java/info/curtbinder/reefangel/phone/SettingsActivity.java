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
            addPreferencesFromResource(R.xml.pref_device);
            addPreferencesFromResource(R.xml.pref_relay);
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

    @TargetApi(11)
	public static class DeviceFragment extends PreferenceFragment {

		@Override
		public void onCreate ( Bundle savedInstanceState ) {
			super.onCreate( savedInstanceState );
			
			// load the preferences from an XML file
			addPreferencesFromResource(R.xml.pref_device);

		}

	}

    @TargetApi(11)
	public static class RelayFragment extends PreferenceFragment {
		
		@Override
		public void onCreate ( Bundle savedInstanceState ) {
			super.onCreate( savedInstanceState );
			
			// load the preferences from an XML file
			addPreferencesFromResource(R.xml.pref_relay);
		}
	}
	
}
