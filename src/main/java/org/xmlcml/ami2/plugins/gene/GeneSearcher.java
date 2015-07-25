package org.xmlcml.ami2.plugins.gene;

import java.util.List;

import nu.xom.Element;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.ami2.plugins.AMIArgProcessor;
import org.xmlcml.ami2.plugins.AMISearcher;
import org.xmlcml.ami2.plugins.NamedPattern;
import org.xmlcml.cmine.args.DefaultArgProcessor;
import org.xmlcml.cmine.files.ResultElement;
import org.xmlcml.cmine.files.ResultsElement;
import org.xmlcml.html.HtmlP;

public class GeneSearcher extends AMISearcher {

	
	public static final Logger LOG = Logger.getLogger(GeneSearcher.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	public GeneSearcher(AMIArgProcessor argProcessor, NamedPattern namedPattern) {
		super(argProcessor, namedPattern);
	}

	@Override 
	public String getValue(Element xomElement) {
		String xmlString = xomElement.toXML();
		// this is ucky, but since we know the HTML is normalized it's probably OK
		xmlString = xomElement.toXML().replaceAll(DefaultArgProcessor.WHITESPACE, " ");
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
	public ResultsElement search(List<? extends Element> elements) {
		ResultsElement resultsElement = new GeneResultsElement();
		for (Element element : elements) {
			String xmlString = getValue(element);
			LOG.trace(xmlString);
			List<ResultElement> resultElementList = this.search(xmlString);
			addXpathAndAddtoResultsElement(element, resultsElement, resultElementList);
		}
		List<String> exactList = resultsElement.getExactList();
		return resultsElement;
	}

	/**
	 *  //PLUGIN
	 */
	public GeneResultElement createResultElement() {
		return new GeneResultElement();
	}

}
