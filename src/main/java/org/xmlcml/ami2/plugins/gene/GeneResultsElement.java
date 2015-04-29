package org.xmlcml.ami2.plugins.gene;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.cmine.files.ResultsElement;

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
	
//	public List<String> getExactList() {
//		if (nameList == null) {
//			nameList = new ArrayList<String>();
//			for (ResultElement resultElement : this) {
//				String name = resultElement.getExact();
//				nameList.add(name);
//			}
//		}
//		return nameList;
//	}

}
