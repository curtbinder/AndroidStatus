package info.curtbinder.reefangel.widget;

/*
 * Copyright (c) 2011-12 by Curt Binder (http://curtbinder.info)
 * 
 * This work is made available under the terms of the Creative Commons
 * Attribution-NonCommercial-ShareAlike 3.0 Unported License
 * http://creativecommons.org/licenses/by-nc-sa/3.0/
 */

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BaseWidgetReceiver extends AppWidgetProvider {
	
	public static final String TAG = BaseWidgetReceiver.class.getSimpleName();

	@Override
	public void onReceive ( Context context, Intent intent ) {
		if ( intent
				.getAction()
				.equals(	"info.curtbinder.reefangel.service.UPDATE_DISPLAY_DATA" ) ) {
			Log.d( TAG, "OnReceive Update Display Data" );
		}
		updateDisplay( context );
		super.onReceive( context, intent );
	}

	@Override
	public void onUpdate (
			Context context,
			AppWidgetManager appWidgetManager,
			int[] appWidgetIds ) {
		updateDisplay( context );
	}

	protected void updateDisplay ( Context context ) {
		Log.d( TAG, "updateDisplay" );
	}
}
