/*
 * Copyright (c) 2011-2014 by Curt Binder (http://curtbinder.info)
 * 
 * This work is made available under the terms of the Creative Commons
 * Attribution-NonCommercial-ShareAlike 3.0 Unported License
 * http://creativecommons.org/licenses/by-nc-sa/3.0/
 */

package info.curtbinder.reefangel.service;

public class XMLReadException extends Exception {

	private static final long serialVersionUID = 1L;

	private String xmlData;
	
	public XMLReadException() {}
	
	public XMLReadException(String message) {
		super(message);
	}

	public void addXmlData(String xml) {
		this.xmlData = xml;
	}

	public String getXmlData ( ) {
		return "XML Data:\n" + xmlData;
	}
}
