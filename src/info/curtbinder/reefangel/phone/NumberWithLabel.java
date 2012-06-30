package info.curtbinder.reefangel.phone;

/*
 * Copyright (c) 2011-12 by Curt Binder (http://curtbinder.info)
 *
 * This work is made available under the terms of the 
 * Creative Commons Attribution-NonCommercial-ShareAlike 3.0 Unported License
 * http://creativecommons.org/licenses/by-nc-sa/3.0/
 */

public class NumberWithLabel {

	private Number data;
	private String label;
	
	public NumberWithLabel() {
		data = new Number((byte) 1);
		// TODO use strings.xml instead of hard code
		label = "T";
	}
	
	public void setTemp(int temp) {
		data.setValue(temp);
	}
	
	public String getTemp() {
		return data.toString();
	}
	
	public void setLabel(String label) {
		this.label = label;
	}
	
	public String getLabel() {
		return label;
	}
}
