package org.xmlcml.ami2.plugins.gene;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.cproject.files.ResultElement;

public class GeneResultElement extends ResultElement {

	private static final Logger LOG = Logger.getLogger(GeneResultElement.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	public GeneResultElement() {
		super();
	}
	
	public GeneResultElement(String title) {
		super(title);
	}
	
	
	
	
}
