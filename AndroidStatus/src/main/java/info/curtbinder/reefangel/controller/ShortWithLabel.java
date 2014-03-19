package info.curtbinder.reefangel.controller;

/*
 * Copyright (c) 2011-12 by Curt Binder (http://curtbinder.info)
 *
 * This work is made available under the terms of the 
 * Creative Commons Attribution-NonCommercial-ShareAlike 3.0 Unported License
 * http://creativecommons.org/licenses/by-nc-sa/3.0/
 */

public class ShortWithLabel {

	private short data;
	private String label;

	public ShortWithLabel () {
		data = 0;
		label = "";
	}

	public ShortWithLabel ( short d, String l ) {
		data = d;
		label = l;
	}

	public void setData ( short d ) {
		data = d;
	}

	public short getData ( ) {
		return data;
	}

	public void setLabel ( String label ) {
		this.label = label;
	}

	public String getLabel ( ) {
		return label;
	}
}
