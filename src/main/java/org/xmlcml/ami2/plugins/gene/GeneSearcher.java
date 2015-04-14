package org.xmlcml.ami2.plugins.gene;

import java.util.List;

import nu.xom.Element;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.ami2.plugins.AMIArgProcessor;
import org.xmlcml.ami2.plugins.DefaultSearcher;
import org.xmlcml.ami2.plugins.NamedPattern;
import org.xmlcml.files.ResultElement;
import org.xmlcml.files.ResultsElement;
import org.xmlcml.html.HtmlP;

public class GeneSearcher extends DefaultSearcher {

	
	public static final Logger LOG = Logger.getLogger(GeneSearcher.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	public GeneSearcher(AMIArgProcessor argProcessor, NamedPattern namedPattern) {
		super(argProcessor, namedPattern);
	}

	@Override 
	protected String getValue(Element xomElement) {
		String xmlString = xomElement.toXML();
		// this is ucky, but since we know the HTML is normalized it's probably OK
		xmlString = xomElement.toXML().replaceAll("\\s+", " ");
		// some markup is of form <i>Foo</i>. <i>bar</i>
		xmlString = xmlString.replaceAll("</i>\\.\\s+<i>", ". ");
		xmlString = xmlString.replaceAll("<span[^>]*>", "");
		xmlString = xmlString.replaceAll("</span[^>]*>", "");
		xmlString = xmlString.replaceAll("<b>", "");
		xmlString = xmlString.replaceAll("</b>", "");
		xmlString = xmlString.replaceAll("<a>", "");
		xmlString = xmlString.replaceAll("</a>", "");
		xmlString = xmlString.replaceAll("<p>", "");
		xmlString = xmlString.replaceAll("</p>", "");
		xmlString = xmlString.replaceAll("<div>", "");
		xmlString = xmlString.replaceAll("</div>", "");
		return xmlString;
	}

	@Override
	public ResultsElement search(List<HtmlP> pElements) {
		GeneResultsElement resultsElement = new GeneResultsElement();
		for (HtmlP pElement : pElements) {
			String xmlString = getValue(pElement);
			LOG.trace(xmlString);
			List<ResultElement> resultElementList = this.search(xmlString);
			for (ResultElement resultElement : resultElementList) {
				resultsElement.appendChild(resultElement);
			}
		}
		List<String> exactList = resultsElement.getExactList();
//		LinneanNamer linneanNamer = new LinneanNamer();
//		List<String> matchList = linneanNamer.expandAbbreviations(exactList);
//		LOG.trace("EXACT "+exactList+"; MATCH "+matchList);
//		resultsElement.addMatchAttributes(matchList);
		
		return resultsElement;
	}

	/**
	 *  //PLUGIN
	 */
	public GeneResultElement createResultElement() {
		return new GeneResultElement();
	}

}
