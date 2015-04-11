package org.xmlcml.ami2.plugins.identifier;

import java.util.List;

import nu.xom.Element;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.ami2.plugins.AMIArgProcessor;
import org.xmlcml.ami2.plugins.DefaultSearcher;
import org.xmlcml.ami2.plugins.NamedPattern;
import org.xmlcml.ami2.plugins.regex.RegexSearcher;
import org.xmlcml.args.ArgIterator;
import org.xmlcml.args.ArgumentOption;

/** 
 * Processes commandline arguments.
 * 
 * @author pm286
 */
public class IdentifierArgProcessor extends AMIArgProcessor {
	
	
	public static final Logger LOG = Logger.getLogger(IdentifierArgProcessor.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	public IdentifierArgProcessor() {
		super();
	}

	public IdentifierArgProcessor(String[] args) {
		this();
		parseArgs(args);
	}

	public IdentifierArgProcessor(String argString) {
		this(argString.split("\\s+"));
	}

	// =============== METHODS ==============
	
	public void initIdentifiers(ArgumentOption option) {
		createAndStoreNamedSearchers(option);
	}

	public void parseRegex(ArgumentOption option, ArgIterator argIterator) {
		List<String> tokens= argIterator.createTokenListUpToNextNonDigitMinus(option);
		createRegexElementList(option, tokens); // compoundRegexList
		createSearchers();
	}

	private void createSearchers() {
		ensureSearcherList();
		for (Element regexElement : regexElementList) {
			NamedPattern namedPattern = NamedPattern.createFromRegexElement(regexElement);
			createSearcherAndAddToMap(namedPattern);
		}
		LOG.trace("MAP: "+searcherByNameMap);
	}

	public void parseTypes(ArgumentOption option, ArgIterator argIterator) {
		createSearcherList(option, argIterator);
	}

	public void runExtractIdentifiers(ArgumentOption option) {
		searchHtmlParaElements();
	}

	public void outputIdentifiers(ArgumentOption option) {
		outputResultElements(option);
	}
	
	// =============================

	protected DefaultSearcher createSearcher(NamedPattern namedPattern) {
		DefaultSearcher defaultSearcher = new DefaultSearcher(this);
		defaultSearcher.setNamedPattern(namedPattern);
		return defaultSearcher;
	}



}
