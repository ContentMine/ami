package org.xmlcml.ami2.plugins.gene;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.ami2.plugins.AMIArgProcessor;
import org.xmlcml.ami2.plugins.DefaultSearcher;
import org.xmlcml.ami2.plugins.NamedPattern;
import org.xmlcml.args.ArgIterator;
import org.xmlcml.args.ArgumentOption;

/** 
 * Processes commandline arguments.
 * 
 * @author pm286
 */
public class GeneArgProcessor extends AMIArgProcessor {
	
	
	public static final Logger LOG = Logger.getLogger(GeneArgProcessor.class);
	
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	public GeneArgProcessor() {
		super();
	}

	public GeneArgProcessor(String[] args) {
		this();
		parseArgs(args);
	}

	public GeneArgProcessor(String argString) {
		this(argString.split("\\s+"));
	}

	// =============== METHODS ==============
	
	public void initGene(ArgumentOption option) {
		createAndStoreNamedSearchers(option);
	}

	public void parseTypes(ArgumentOption option, ArgIterator argIterator) {
		createSearcherList(option, argIterator);
	}

	public void runExtractGene(ArgumentOption option) {
		searchHtmlParaElements();
	}

	public void outputGene(ArgumentOption option) {
		outputResultElements(option);
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
		return new GeneSearcher(this, namedPattern);
	}



}
