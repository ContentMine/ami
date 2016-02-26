package org.xmlcml.ami2.plugins.regex;

import java.util.List;

import nu.xom.Element;

import org.apache.log4j.Level;
import org.xmlcml.ami2.plugins.AMIArgProcessor;
import org.xmlcml.ami2.plugins.AMISearcher;
import org.xmlcml.ami2.plugins.MatcherResult;
import org.xmlcml.cmine.files.ResultElement;
import org.xmlcml.cmine.files.ResultsElement;

public class RegexSearcher extends AMISearcher {

	
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	List<RegexComponent> componentList;
	private CompoundRegex compoundRegex;
	Element resultElement;


	public RegexSearcher(AMIArgProcessor argProcessor) {
		super(argProcessor);
	}

	public static RegexSearcher createSearcher(AMIArgProcessor argProcessor) {
		return new RegexSearcher(argProcessor);
	}

	void setCompoundRegex(CompoundRegex compoundRegex) {
		this.compoundRegex = compoundRegex;
	}

	// ====== args ========

	/** specific search, iterates thorugh RegexComponents
	 * 
	 */
	public ResultsElement searchXomElement(Element xomElement) {
		List<RegexComponent> regexComponents = compoundRegex.getOrCreateRegexComponentList();
		ResultsElement resultsElement = new ResultsElement();
		String xomValue = xomElement.getValue();
		for (RegexComponent regexComponent : regexComponents) {
			MatcherResult matcherResult = regexComponent.searchWithPattern(xomValue); // crude to start with
			ResultsElement resultsElementToAdd = matcherResult.createResultsElement();
			addXpathAndAddtoResultsElement(xomElement, resultsElement, resultsElementToAdd);
		}
		return resultsElement;
	}

	// ===============

	
	public void debug() {
		LOG.debug(compoundRegex.getTitle()+"/"+compoundRegex.getRegexValues().size());
	}


}
