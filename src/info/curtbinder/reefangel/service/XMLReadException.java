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
