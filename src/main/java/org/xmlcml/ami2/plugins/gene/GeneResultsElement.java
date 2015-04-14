package org.xmlcml.ami2.plugins.gene;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.files.ResultElement;
import org.xmlcml.files.ResultsElement;

public class GeneResultsElement extends ResultsElement {

	
	private static final Logger LOG = Logger
			.getLogger(GeneResultsElement.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	private static final String GENE = "gene";
	private List<String> nameList;

	private GeneResultsElement(String title) {
		super(title);
	}

	public GeneResultsElement() {
		this(GENE);
	}

	public List<String> getExactList() {
		if (nameList == null) {
			nameList = new ArrayList<String>();
			for (ResultElement resultElement : this) {
				GeneResultElement speciesResultElement = (GeneResultElement)resultElement;
				String name = speciesResultElement.getExact();
				nameList.add(name);
			}
		}
		return nameList;
	}

}
