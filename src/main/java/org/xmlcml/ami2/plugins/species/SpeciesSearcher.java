package org.xmlcml.ami2.plugins.species;

import java.util.List;

import org.apache.log4j.Level;
import org.xmlcml.ami2.dictionary.DefaultAMIDictionary;
import org.xmlcml.ami2.plugins.AMIArgProcessor;
import org.xmlcml.ami2.plugins.AMISearcher;
import org.xmlcml.ami2.plugins.NamedPattern;
import org.xmlcml.cmine.args.DefaultArgProcessor;
import org.xmlcml.cmine.files.ResultElement;
import org.xmlcml.cmine.files.ResultsElement;

import nu.xom.Element;

public class SpeciesSearcher extends AMISearcher {

	static {
		LOG.setLevel(Level.DEBUG);
	}

	public SpeciesSearcher(AMIArgProcessor argProcessor, NamedPattern namedPattern) {
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
	protected void postProcessResultsElement(ResultsElement resultsElement) {
		List<String> exactList = resultsElement.getExactList();
		LinneanNamer linneanNamer = new LinneanNamer();
		List<String> matchList = linneanNamer.expandAbbreviations(exactList);
		resultsElement.addMatchAttributes(matchList);
	}
	

	@Override
	public String getDictionaryTerm(ResultElement resultElement) {
		return LinneanNamer.createGenus(resultElement.getMatch());
	}

	/**
	 *  //PLUGIN
	 */
	public SpeciesResultElement createResultElement() {
		return new SpeciesResultElement();
	}

}
