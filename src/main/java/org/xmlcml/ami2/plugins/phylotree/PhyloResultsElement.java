package org.xmlcml.ami2.plugins.phylotree;

import org.xmlcml.cmine.files.ResultElement;
import org.xmlcml.cmine.files.ResultsElement;
import org.xmlcml.xml.XMLUtil;

public class PhyloResultsElement extends ResultsElement {

	public PhyloResultsElement(String title) {
		super(title);
	}

	public PhyloResultsElement(ResultsElement resultsElement) {
		if (resultsElement == null) {
			throw new RuntimeException("Null ResultsElement");
		}
		XMLUtil.copyAttributesFromTo(resultsElement, this);
		for (ResultElement resultElement : resultsElement) {
			PhyloResultElement wordResultElement = new PhyloResultElement(resultElement);
			this.appendChild(wordResultElement);
		}
	}

}
