/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Curt Binder
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

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;

/**
 * Created by binder on 4/8/15.
 */
public class PrefHeadersFragment extends  PreferenceFragment
        implements Preference.OnPreferenceClickListener {

        public interface PrefLoadFragListener {
            void loadFragment(int id);
        }
        private PrefLoadFragListener callback;

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            callback = (PrefLoadFragListener) activity;
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_main);

            Preference p = findPreference(getString(R.string.prefsCategoryProfiles));
            p.setOnPreferenceClickListener(this);
            p = findPreference(getString(R.string.prefsCategoryController));
            p.setOnPreferenceClickListener(this);
            p = findPreference(getString(R.string.prefAutoUpdateCategory));
            p.setOnPreferenceClickListener(this);
            p = findPreference(getString(R.string.prefsCategoryAdvanced));
            p.setOnPreferenceClickListener(this);
            p = findPreference(getString(R.string.prefNotificationCategory));
            p.setOnPreferenceClickListener(this);
            p = findPreference(getString(R.string.prefLoggingCategory));
            p.setOnPreferenceClickListener(this);
            p = findPreference(getString(R.string.prefsCategoryApp));
            p.setOnPreferenceClickListener(this);
        }

        @Override
        public boolean onPreferenceClick(Preference preference) {

            // TODO implement fragmentloading for other screens in preferences

            // TODO have a master/global ID for the fragments
            String key = preference.getKey();
            int id = 0;
            if (key.equals(getString(R.string.prefsCategoryProfiles))) {
                id = 1;
            } else if (key.equals(getString(R.string.prefsCategoryController))){
                id = 2;
            } else if (key.equals(getString(R.string.prefAutoUpdateCategory))){
                id = 3;
            } else if (key.equals(getString(R.string.prefsCategoryAdvanced))){
                id = 4;
            } else if (key.equals(getString(R.string.prefNotificationCategory))){
                id = 5;
            } else if (key.equals(getString(R.string.prefLoggingCategory))){
                id = 6;
            } else if (key.equals(getString(R.string.prefsCategoryApp))){
                id = 7;
            }
            callback.loadFragment(id);
            return true;
        }
}
