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
import android.support.v4.app.ListFragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;


public class FragmentListActivity extends ActionBarActivity {

	public static final String FRAG_TYPE = "type";

	public static final int HISTORY = 0;
	public static final int NOTIFICATIONS = 1;
	public static final int ERRORS = 2;
	
	private static int frag;

	public void onCreate ( Bundle savedInstanceState ) {
		super.onCreate( savedInstanceState );

		final ActionBar ab = getSupportActionBar();
		ab.setDisplayShowTitleEnabled( true );
		
		FragmentManager fm = getSupportFragmentManager();
		frag = getIntent().getIntExtra( FRAG_TYPE, HISTORY );

		// Create the list fragment and add it as our sole content.
		if ( fm.findFragmentById( android.R.id.content ) == null ) {
			ListFragment list = null;
			switch ( frag ) {
				default:
				case HISTORY:
					list = new HistoryListFragment();
					break;
				case NOTIFICATIONS:
					list = new NotificationListFragment();
					break;
				case ERRORS:
					list = new ErrorListFragment();
					break;
			}
			fm.beginTransaction().add( android.R.id.content, list ).commit();
		}
	}
	
	@Override
	protected void onResume ( ) {
		super.onResume();
		switch ( frag ) {
			default:
			case HISTORY:
				getSupportActionBar().setTitle( R.string.titleHistory );
				break;
			case NOTIFICATIONS:
				getSupportActionBar().setTitle( R.string.titleNotifications );
				break;
			case ERRORS:
				getSupportActionBar().setTitle( R.string.titleError );
				break;
		}
	}
}
