package org.xmlcml.ami2.plugins.regex;

import java.util.List;

import nu.xom.Element;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.ami2.plugins.AMIArgProcessor;
import org.xmlcml.ami2.plugins.AMISearcher;
import org.xmlcml.ami2.plugins.MatcherResult;
import org.xmlcml.cproject.files.ResultElement;
import org.xmlcml.cproject.files.ResultsElement;

public class RegexSearcher extends AMISearcher {

	private static final Logger LOG = Logger.getLogger(RegexSearcher.class);
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
		LOG.trace("XOM "+xomValue);
		for (RegexComponent regexComponent : regexComponents) {
			LOG.trace("RGXCOMP "+regexComponent);
			MatcherResult matcherResult = regexComponent.searchWithPattern(xomValue); // crude to start with
			ResultsElement resultsElementToAdd = matcherResult.createResultsElement();
			if (resultsElementToAdd != null) {
				LOG.trace("RESELEM "+resultsElementToAdd);
				addXpathAndAddtoResultsElement(xomElement, resultsElement, resultsElementToAdd);
			}
		}
		return resultsElement;
	}

	// ===============

	
	public void debug() {
		LOG.debug(compoundRegex.getTitle()+"/"+compoundRegex.getRegexValues().size());
	}


}
