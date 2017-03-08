package org.xmlcml.ami2.plugins.search;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.cproject.files.ResultElement;

public class SearchResultElement extends ResultElement {

	private static final Logger LOG = Logger.getLogger(SearchResultElement.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	public SearchResultElement() {
		super();
	}
	
	public SearchResultElement(String title) {
		super(title);
	}
	
	
	
	
}
