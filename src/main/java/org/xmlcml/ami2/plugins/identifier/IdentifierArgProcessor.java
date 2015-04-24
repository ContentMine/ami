package org.xmlcml.ami2.plugins.identifier;

import java.util.List;

import nu.xom.Element;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.ami2.plugins.AMIArgProcessor;
import org.xmlcml.ami2.plugins.AMISearcher;
import org.xmlcml.ami2.plugins.NamedPattern;
import org.xmlcml.cmine.args.ArgIterator;
import org.xmlcml.cmine.args.ArgumentOption;
import org.xmlcml.cmine.files.DefaultSearcher;

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
		getOrCreateContentProcessor().outputResultElements(option, this);
	}
	
	// =============================

	protected DefaultSearcher createSearcher(NamedPattern namedPattern) {
		AMISearcher defaultSearcher = new AMISearcher(this);
		defaultSearcher.setNamedPattern(namedPattern);
		return defaultSearcher;
	}



}
