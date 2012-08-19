package info.curtbinder.reefangel.phone;

/*
 * Copyright (c) 2011-12 by Curt Binder (http://curtbinder.info)
 *
 * This work is made available under the terms of the 
 * Creative Commons Attribution-NonCommercial-ShareAlike 3.0 Unported License
 * http://creativecommons.org/licenses/by-nc-sa/3.0/
 */

import info.curtbinder.reefangel.controller.Number;

public class NumberWithLabel {

	private Number data;
	private String label;

	private void initValues ( byte decimalPlaces, String label ) {
		data = new Number( decimalPlaces );
		this.label = label;
	}

	public NumberWithLabel () {
		initValues( (byte) 0, "" );
	}

	public NumberWithLabel ( byte decimalPlaces ) {
		initValues( decimalPlaces, "" );
	}

	public NumberWithLabel ( byte decimalPlaces, String label ) {
		initValues( decimalPlaces, label );
	}

	public void setData ( Number data ) {
		this.data = data;
	}

	public void setData ( int value ) {
		data.setValue( value );
	}

	public String getData ( ) {
		return data.toString();
	}

	public void setLabel ( String label ) {
		this.label = label;
	}

	public String getLabel ( ) {
		return label;
	}
}
