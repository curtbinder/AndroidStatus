/*
 * Copyright (c) 2011-13 by Curt Binder (http://curtbinder.info)
 *
 * This work is made available under the terms of the 
 * Creative Commons Attribution-NonCommercial-ShareAlike 3.0 Unported License
 * http://creativecommons.org/licenses/by-nc-sa/3.0/
 */

package info.curtbinder.reefangel.phone.pages;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ScrollView;

public abstract class RAPage extends ScrollView {

	public RAPage ( Context context ) {
		super( context );
	}

	public RAPage ( Context context, AttributeSet attrs ) {
		super( context, attrs );
	}

	public abstract String getPageTitle ( );
}
