package org.xmlcml.ami2.plugins.species;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.files.ResultElement;
import org.xmlcml.files.ResultsElement;
import org.xmlcml.xml.XMLUtil;

public class SpeciesResultsElement extends ResultsElement {

	
	private static final Logger LOG = Logger
			.getLogger(SpeciesResultsElement.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	private static final String SPECIES = "species";
	private List<String> nameList;

	private SpeciesResultsElement(String title) {
		super(title);
	}

	public SpeciesResultsElement() {
		this(SPECIES);
	}

	public List<String> getExactList() {
		if (nameList == null) {
			nameList = new ArrayList<String>();
			for (ResultElement resultElement : this) {
				SpeciesResultElement speciesResultElement = (SpeciesResultElement)resultElement;
				String name = speciesResultElement.getExact();
				nameList.add(name);
			}
		}
		return nameList;
	}

	public void addMatchAttributes(List<String> matchList) {
		if (this.size() != matchList.size()) {
			throw new RuntimeException("name list wrong length ("+matchList.size()+") rather than ("+this.size()+")");
		}
		int i = 0;
		for (ResultElement resultElement : this) {
			resultElement.setMatch(matchList.get(i));
			// cosmetic - keeps attributes in natural order
			resultElement.setPost(resultElement.getPost());
			i++;
		}
	}

}
