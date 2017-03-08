package org.xmlcml.ami2.plugins.gene;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.cproject.files.ResultsElement;

public class GeneResultsElement extends ResultsElement {

	
	private static final Logger LOG = Logger
			.getLogger(GeneResultsElement.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	private static final String GENE = "gene";
	private GeneResultsElement(String title) {
		super(title);
	}

	public GeneResultsElement() {
		this(GENE);
	}
	
}
