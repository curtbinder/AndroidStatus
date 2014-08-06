/*
 * Copyright (c) 2011-13 by Curt Binder (http://curtbinder.info)
 *
 * This work is made available under the terms of the 
 * Creative Commons Attribution-NonCommercial-ShareAlike 3.0 Unported License
 * http://creativecommons.org/licenses/by-nc-sa/3.0/
 */

package info.curtbinder.reefangel.phone;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

public class BaseActivity extends ActionBarActivity {
	RAApplication rapp;

	protected void onCreate ( Bundle savedInstanceState ) {
		super.onCreate( savedInstanceState );

		rapp = (RAApplication) getApplication();
	}

	protected void onPause ( ) {
		super.onPause();
	}

	protected void onResume ( ) {
		super.onResume();
	}

}
