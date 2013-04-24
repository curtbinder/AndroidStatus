/*
 * Copyright (c) 2011-12 by Curt Binder (http://curtbinder.info)
 *
 * This work is made available under the terms of the 
 * Creative Commons Attribution-NonCommercial-ShareAlike 3.0 Unported License
 * http://creativecommons.org/licenses/by-nc-sa/3.0/
 */

package info.curtbinder.reefangel.phone;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TabHost;

@SuppressWarnings("deprecation")
public class MemoryTabsActivity extends TabActivity {

	protected void onCreate ( Bundle savedInstanceState ) {
		super.onCreate( savedInstanceState );

		final TabHost t = getTabHost();

		Intent i = new Intent( this, MemoryActivity.class );
		i.putExtra( Globals.PRE10_LOCATIONS, getIntent().getExtras()
				.getBoolean( Globals.PRE10_LOCATIONS, false ) );

		t.addTab( t
				.newTabSpec( getString( R.string.tabMemory1 ) )
				.setIndicator(	getString( R.string.titleMemory ),
								getResources()
										.getDrawable(	android.R.drawable.ic_menu_agenda ) )
				.setContent( i ) );

		// t.addTab( t
		// .newTabSpec( getString( R.string.tabMemory2 ) )
		// .setIndicator( getString( R.string.titleDateTime ),
		// getResources()
		// .getDrawable( android.R.drawable.ic_menu_my_calendar ) )
		// .setContent( new Intent( this, DateTimeActivity.class ) ) );
	}
}
