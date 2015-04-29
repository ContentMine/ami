package org.xmlcml.ami2.plugins.species;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.ami2.plugins.AMIArgProcessor;
import org.xmlcml.ami2.plugins.NamedPattern;
import org.xmlcml.cmine.args.ArgIterator;
import org.xmlcml.cmine.args.ArgumentOption;
import org.xmlcml.cmine.files.DefaultSearcher;

/** 
 * Processes commandline arguments.
 * 
 * @author pm286
 */
public class SpeciesArgProcessor extends AMIArgProcessor {
	
	
	public static final Logger LOG = Logger.getLogger(SpeciesArgProcessor.class);
	private Boolean expandAbbreviations;
	
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	public SpeciesArgProcessor() {
		super();
	}

	public SpeciesArgProcessor(String[] args) {
		this();
		parseArgs(args);
	}

	public SpeciesArgProcessor(String argString) {
		this(argString.split("\\s+"));
	}

	// =============== METHODS ==============
	
	public void initSpecies(ArgumentOption option) {
		createAndStoreNamedSearchers(option);
	}

	public void parseTypes(ArgumentOption option, ArgIterator argIterator) {
		createSearcherList(option, argIterator);
	}

	public void parseAbbreviations(ArgumentOption option, ArgIterator argIterator) {
		expandAbbreviations = argIterator.getBoolean(option);
	}

	public void runExtractSpecies(ArgumentOption option) {
		searchHtmlParaElements();
	}

	public void outputSpecies(ArgumentOption option) {
		getOrCreateContentProcessor().outputResultElements(option, this);
	}
	
	public void parseSummary(ArgumentOption option, ArgIterator argIterator) {
//		summaryMethods = argIterator.getStrings(option);
		LOG.debug("summary methods not yet written");
	}
	
	public void finalSummary(ArgumentOption option) {
		LOG.debug("final summary not yet written");
	}

	// =============================

	/** create Subclassed Searcher.
	 * 
	 * //PLUGIN
	 * 
	 * Most plugins should Override this and create a FooSearcher.
	 * 
	 * @param namedPattern 
	 * @return subclassed Plugin
	 */
	protected DefaultSearcher createSearcher(NamedPattern namedPattern) {
		return new SpeciesSearcher(this, namedPattern);
	}



}
