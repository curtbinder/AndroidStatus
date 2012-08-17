package info.curtbinder.reefangel.phone;

/*
 * Copyright (c) 2011-12 by Curt Binder (http://curtbinder.info)
 * 
 * This work is made available under the terms of the Creative Commons
 * Attribution-NonCommercial-ShareAlike 3.0 Unported License
 * http://creativecommons.org/licenses/by-nc-sa/3.0/
 */

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

public class UpdateService extends IntentService {

	private static final String TAG = UpdateService.class.getSimpleName();
	
	public UpdateService ( ) {
		super( TAG );
		Log.d( TAG, "UpdateService()" );
	}

	@Override
	protected void onHandleIntent ( Intent intent ) {
		Log.d( TAG, "onHandleIntent" );
	}

}
