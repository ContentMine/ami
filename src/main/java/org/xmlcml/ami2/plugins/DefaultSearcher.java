package org.xmlcml.ami2.plugins;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import nu.xom.Attribute;
import nu.xom.Element;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.ami2.lookups.AbstractLookup;
import org.xmlcml.files.ResultElement;
import org.xmlcml.files.ResultsElement;
import org.xmlcml.html.HtmlP;
import org.xmlcml.xml.XPathGenerator;

public class DefaultSearcher {

		private static final String NOT_FOUND = "NOT_FOUND";
	private static final Logger LOG = Logger.getLogger(DefaultSearcher.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	private AMIArgProcessor argProcessor;
	protected Integer[] contextCounts;
	private String exactMatch;
	private AbstractLookup lookup;
	private NamedPattern namedPattern;
	private Pattern pattern;
	private String name;

	public DefaultSearcher(AMIArgProcessor argProcessor) {
		this.argProcessor = argProcessor;
		contextCounts = argProcessor.getContextCount();
	}

	public DefaultSearcher(AMIArgProcessor argProcessor, NamedPattern namedPattern) {
		this(argProcessor);
		this.setNamedPattern(namedPattern);
	}

	public void setNamedPattern(NamedPattern namedPattern) {
		this.namedPattern = namedPattern; // could be null
		this.pattern = namedPattern == null ? null : namedPattern.getPattern();
		this.name = namedPattern == null ? null : namedPattern.getName();
	}

	/** create resultsElement.
	 * 
	 * May be empty if no hits
	 * 
	 * @param xomElement
	 * @return
	 */
	protected ResultsElement searchXomElement(Element xomElement) {
		ResultsElement resultsElement = new ResultsElement();
		String value = getValue(xomElement);
		List<ResultElement> resultElementList = search(value); // crude to start with
		for (ResultElement resultElement : resultElementList) {
			resultsElement.appendChild(resultElement);
		}
		return resultsElement;
	}

	/** flatten all tags.
	 * 
	 * @param xomElement
	 * @return
	 */
	protected String getValue(Element xomElement) {
		return xomElement.getValue();
	}

	protected List<ResultElement> search(String value) {
		List<ResultElement> resultElementList = new ArrayList<ResultElement>();
		Matcher matcher = pattern.matcher(value);
		int start = 0;
		while (matcher.find(start)) {
			ResultElement resultElement = createResultElement(value, matcher);
			resultElementList.add(resultElement);
			start = matcher.end();
		}
		return resultElementList;
	}

	protected void matchAndAddPrePost(String value, Matcher matcher,
			ResultElement resultElement) {
		String exactMatch = matcher.group(0);
		int preEnd = matcher.start();
		int preStart = Math.max(0, preEnd - contextCounts[0]);
		int postStart = matcher.end();
		int postEnd = Math.min(value.length(), postStart + contextCounts[1]);
		resultElement.setPre(flattenHtmlInlineTags(value.substring(preStart, preEnd)));
		exactMatch = flattenHtmlInlineTags(exactMatch);
		resultElement.setExact(exactMatch);
		resultElement.setPost(flattenHtmlInlineTags(value.substring(postStart, postEnd)));
		lookupMatchAndAddLookupRefs(resultElement);
	}

	private void lookupMatchAndAddLookupRefs(ResultElement resultElement) {
		Map<String, AbstractLookup> lookupInstanceByName = argProcessor.getOrCreateLookupInstanceByName();
		for (String lookupName : lookupInstanceByName.keySet()) {
			AbstractLookup lookup = lookupInstanceByName.get(lookupName);
			Map<String, String> lookupRefByMatch = lookup.getOrCreateLookupRefByMatch();
			String lookupRef = lookupRefByMatch.get(exactMatch);
			if (lookupRef == null) {
				try {
					lookupRef = lookup.lookup(exactMatch);
				} catch (IOException e) {
					LOG.debug("Cannot find match: "+exactMatch+" in "+lookupName);
				}
				lookupRef = lookupRef == null ? NOT_FOUND : lookupRef;
				lookupRefByMatch.put(exactMatch,  lookupRef);
			}
			if (!(NOT_FOUND.equals(lookupRef))) {
				resultElement.addAttribute(new Attribute(lookupName, lookupRef));
			}
		}
	}
	
	protected String flattenHtmlInlineTags(String s) {
		s = s.replaceAll("<[^>]*>", "");
		return s;
	}

	public ResultsElement search(List<HtmlP> pElements) {
		ResultsElement resultsElement = new ResultsElement();
		for (HtmlP pElement : pElements) {
			String xpath = new XPathGenerator(pElement).getXPath();
			ResultsElement subResultsElement = this.searchXomElement(pElement);
			if (subResultsElement.size() > 0) {
				subResultsElement.setXPath(xpath);
				resultsElement.transferResultElements(subResultsElement);
			}
		}
		return resultsElement;
	}

	public String getName() {
		return name;
	}

	protected ResultElement createResultElement(String value, Matcher matcher) {
		ResultElement resultElement = createResultElement();
		matchAndAddPrePost(value, matcher, resultElement);
		return resultElement;
	}

	/** 
	 * Often overridden
	 * 
	 * @return
	 */
	protected ResultElement createResultElement() {
		return new ResultElement();
	}
	
	
	
}
