package org.xmlcml.ami2.plugins.dummy;

import java.util.List;

import nu.xom.Element;

import org.apache.log4j.Level;
import org.xmlcml.ami2.plugins.AMIArgProcessor;
import org.xmlcml.ami2.plugins.AMISearcher;
import org.xmlcml.ami2.plugins.NamedPattern;
import org.xmlcml.cmine.files.ResultElement;
import org.xmlcml.cmine.files.ResultsElement;

public class DummySearcher extends AMISearcher {

	
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
	public ResultsElement search(List<? extends Element> elements) {
		ResultsElement resultsElement = null;
//		for (Element element : elements) {
//			String xmlString = getValue(element);
//			LOG.trace(xmlString);
//			List<ResultElement> resultElementList = this.search(xmlString);
//			addXpathAndAddtoResultsElement(element, resultsElement, resultElementList);
//		}
		return resultsElement;
	}

	/**
	 *  //PLUGIN
	 */
	public DummyResultElement createResultElement() {
		return new DummyResultElement();
	}

}
