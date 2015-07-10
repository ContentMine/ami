package org.xmlcml.ami2.plugins;

import java.io.IOException;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import nu.xom.Attribute;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.cmine.files.DefaultSearcher;
import org.xmlcml.cmine.files.ResultElement;
import org.xmlcml.cmine.lookup.AbstractLookup;

public class AMISearcher extends DefaultSearcher {

		private static final String NOT_FOUND = "NOT_FOUND";
	private static final Logger LOG = Logger.getLogger(AMISearcher.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	private AMIArgProcessor argProcessor;
	protected Integer[] contextCounts;
	private String exactMatch;
	private AbstractLookup lookup;
	private NamedPattern namedPattern;
	public Pattern pattern;
	
	public AMISearcher(AMIArgProcessor argProcessor) {
		this.argProcessor = argProcessor;
		contextCounts = argProcessor.getContextCount();
	}

	public AMISearcher(AMIArgProcessor argProcessor, NamedPattern namedPattern) {
		this(argProcessor);
		this.setNamedPattern(namedPattern);
	}

	public void setNamedPattern(NamedPattern namedPattern) {
		this.namedPattern = namedPattern; // could be null
		this.pattern = namedPattern == null ? null : namedPattern.getPattern();
		this.name = namedPattern == null ? null : namedPattern.getName();
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

	@Override
	public ResultElement createResultElement(String value, Matcher matcher) {
		ResultElement resultElement = createResultElement();
		matchAndAddPrePost(value, matcher, resultElement);
		return resultElement;
	}
	
	/**
	 *  //PLUGIN
	 */
	public ResultElement createResultElement() {
		return new AMIResultElement();
	}


	@Override
	protected Pattern getPattern() {
		return pattern;
	}
}
