package org.xmlcml.ami2.plugins.identifier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import nu.xom.Element;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.ami2.plugins.AMIArgProcessor;
import org.xmlcml.ami2.plugins.NamedPattern;
import org.xmlcml.args.ArgIterator;
import org.xmlcml.args.ArgumentOption;
import org.xmlcml.euclid.IntRange;
import org.xmlcml.files.QuickscrapeNorma;
import org.xmlcml.files.ResultsElement;
import org.xmlcml.html.HtmlP;

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


}
