package info.curtbinder.reefangel.phone;

/*
 * Copyright (c) 2011-12 by Curt Binder (http://curtbinder.info)
 *
 * This work is made available under the terms of the 
 * Creative Commons Attribution-NonCommercial-ShareAlike 3.0 Unported License
 * http://creativecommons.org/licenses/by-nc-sa/3.0/
 */

import android.app.Activity;
//import android.content.Intent;
import android.os.Bundle;

public class BaseActivity extends Activity {
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

		// if the service isn't running, start it
		// TODO move to have this run all the time
		rapp.checkServiceRunning();
	}

}
