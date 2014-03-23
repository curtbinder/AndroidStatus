/*
 * Copyright (c) 2011-2013 by Curt Binder (http://curtbinder.info)
 * 
 * This work is made available under the terms of the Creative Commons
 * Attribution-NonCommercial-ShareAlike 3.0 Unported License
 * http://creativecommons.org/licenses/by-nc-sa/3.0/
 */

package info.curtbinder.reefangel.phone;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootReceiver extends BroadcastReceiver {
    //private static final String TAG = BootReceiver.class.getSimpleName();

    public void onReceive(Context context, Intent intent) {
        //Log.d( TAG, "onReceive Boot" );
        // Start the update service
        // the function will handle starting it if necessary
        ((RAApplication) context.getApplicationContext())
                .startAutoUpdateService();
    }

}
