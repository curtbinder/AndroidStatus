/*
 * Copyright (c) 2011-13 by Curt Binder (http://curtbinder.info)
 *
 * This work is made available under the terms of the 
 * Creative Commons Attribution-NonCommercial-ShareAlike 3.0 Unported License
 * http://creativecommons.org/licenses/by-nc-sa/3.0/
 */

package info.curtbinder.reefangel.phone.pages;

import info.curtbinder.reefangel.phone.OverridePopupActivity;
import info.curtbinder.reefangel.phone.Permissions;
import info.curtbinder.reefangel.service.MessageCommands;
import android.content.Context;
import android.content.Intent;
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
	
	protected void displayOverridePopup( int channel, short value ) {
		Intent i = new Intent( MessageCommands.OVERRIDE_POPUP_INTENT );
		i.putExtra( OverridePopupActivity.CHANNEL_KEY, channel );
		i.putExtra( OverridePopupActivity.VALUE_KEY, value );
		this.getContext().sendBroadcast( i, Permissions.SEND_COMMAND );
	}
}
