package org.xmlcml.ami2.plugins.dummy;

import java.util.List;

import nu.xom.Element;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.ami2.plugins.AMIArgProcessor;
import org.xmlcml.ami2.plugins.AMISearcher;
import org.xmlcml.ami2.plugins.NamedPattern;
import org.xmlcml.cmine.files.ResultElement;
import org.xmlcml.cmine.files.ResultsElement;
import org.xmlcml.html.HtmlP;

public class DummySearcher extends AMISearcher {

	
	public static final Logger LOG = Logger.getLogger(DummySearcher.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	public DummySearcher(AMIArgProcessor argProcessor, NamedPattern namedPattern) {
		super(argProcessor, namedPattern);
	}

	@Override 
	public String getValue(Element xomElement) {
		String xmlString = xomElement.toXML();
		return xmlString;
	}

	@Override
	public ResultsElement search(List<HtmlP> pElements) {
		ResultsElement resultsElement = new DummyResultsElement();
		for (HtmlP pElement : pElements) {
			String xmlString = getValue(pElement);
			LOG.trace(xmlString);
			List<ResultElement> resultElementList = this.search(xmlString);
			for (ResultElement resultElement : resultElementList) {
				resultsElement.appendChild(resultElement);
			}
		}
		return resultsElement;
	}

	/**
	 *  //PLUGIN
	 */
	public DummyResultElement createResultElement() {
		return new DummyResultElement();
	}

}
