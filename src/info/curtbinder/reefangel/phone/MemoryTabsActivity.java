package info.curtbinder.reefangel.phone;

/*
 * Copyright (c) 2011-12 by Curt Binder (http://curtbinder.info)
 *
 * This work is made available under the terms of the 
 * Creative Commons Attribution-NonCommercial-ShareAlike 3.0 Unported License
 * http://creativecommons.org/licenses/by-nc-sa/3.0/
 */

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TabHost;

public class MemoryTabsActivity extends TabActivity {

	@Override
	protected void onCreate ( Bundle savedInstanceState ) {
		super.onCreate( savedInstanceState );

		final TabHost t = getTabHost();

		t.addTab( t
				.newTabSpec( getString( R.string.tabMemory1 ) )
				.setIndicator(	getString( R.string.titleMemory ),
								getResources()
										.getDrawable(	android.R.drawable.ic_menu_agenda ) )
				.setContent( new Intent( this, MemoryActivity.class ) ) );
		t.addTab( t
				.newTabSpec( getString( R.string.tabMemory2 ) )
				.setIndicator(	getString( R.string.titleCommands ),
								getResources()
										.getDrawable(	android.R.drawable.ic_menu_upload ) )
				.setContent( new Intent( this, CommandsActivity.class ) ) );
	}
}
