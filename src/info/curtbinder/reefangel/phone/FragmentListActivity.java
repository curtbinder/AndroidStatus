/*
 * Copyright (c) 2011-2013 by Curt Binder (http://curtbinder.info)
 * 
 * This work is made available under the terms of the Creative Commons
 * Attribution-NonCommercial-ShareAlike 3.0 Unported License
 * http://creativecommons.org/licenses/by-nc-sa/3.0/
 */

package info.curtbinder.reefangel.phone;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.app.SherlockListFragment;

public class FragmentListActivity extends SherlockFragmentActivity {

	public static final String FRAG_TYPE = "type";

	public static final int HISTORY = 0;
	public static final int NOTIFICATIONS = 1;
	public static final int ERRORS = 2;

	public void onCreate ( Bundle savedInstanceState ) {
		super.onCreate( savedInstanceState );

		FragmentManager fm = getSupportFragmentManager();
		int frag = getIntent().getIntExtra( FRAG_TYPE, HISTORY );

		// Create the list fragment and add it as our sole content.
		if ( fm.findFragmentById( android.R.id.content ) == null ) {
			SherlockListFragment list = null;
			switch ( frag ) {
				default:
				case HISTORY:
					list = new HistoryListFragment();
					setTitle( R.string.titleHistory );
					break;
				case NOTIFICATIONS:
					list = new NotificationListFragment();
					setTitle( R.string.titleNotifications );
					break;
				case ERRORS:
					list = new ErrorListFragment();
					setTitle( R.string.titleError );
					break;
			}
			fm.beginTransaction().add( android.R.id.content, list ).commit();
		}
	}
}
