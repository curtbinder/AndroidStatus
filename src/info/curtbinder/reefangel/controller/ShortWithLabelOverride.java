package info.curtbinder.reefangel.controller;

import info.curtbinder.reefangel.phone.Globals;

/*
 * Copyright (c) 2011-2014 by Curt Binder (http://curtbinder.info)
 *
 * This work is made available under the terms of the 
 * Creative Commons Attribution-NonCommercial-ShareAlike 3.0 Unported License
 * http://creativecommons.org/licenses/by-nc-sa/3.0/
 */

public class ShortWithLabelOverride extends ShortWithLabel {

	private short override;
	
	public ShortWithLabelOverride () {
		super();
		disableOverride();
	}

	public ShortWithLabelOverride ( short d, String l ) {
		super( d, l );
		disableOverride();
	}

	public void disableOverride ( ) {
		override = Globals.OVERRIDE_DISABLE;
	}
	
	public void setOverride ( short v ) {
		if ( v > Globals.OVERRIDE_MAX_VALUE ) {
			disableOverride();
		} else  {
			override = v;
		}
	}
	
	@Override
	public short getData ( ) {
		short d;
		if ( override > Globals.OVERRIDE_MAX_VALUE ) {
			// above 100% (max value), so the override is disabled
			d = super.getData();
		} else {
			// override is below max value, so return the override value
			d = override;
		}
		return d;
	}
}
