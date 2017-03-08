package org.xmlcml.ami2.plugins.dummy;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.ami2.plugins.AMIArgProcessor;
import org.xmlcml.ami2.plugins.AMISearcher;
import org.xmlcml.ami2.plugins.NamedPattern;
import org.xmlcml.cproject.args.ArgIterator;
import org.xmlcml.cproject.args.ArgumentOption;

/** 
 * Processes commandline arguments.
 * Stores variable.
 * Adds methods called by reflection
 * 
 * @author pm286
 */
public class DummyArgProcessor extends AMIArgProcessor {
	
	
	public static final Logger LOG = Logger.getLogger(DummyArgProcessor.class);
	
	static {
		LOG.setLevel(Level.DEBUG);
	}

	/** CONSTRUCTORS - generally leave untouched */
	
	public DummyArgProcessor() {
		super();
	}

	public DummyArgProcessor(String[] args) {
		this();
		parseArgs(args);
	}

	public DummyArgProcessor(String argString) {
		this(argString.split(WHITESPACE));
	}

	// =============== METHODS ==============
	
	/**
	 * Initialises (static) variables for plugin.
	 * 
	 * Delete if not required
	 * 
	 * @param option
	 */
	public void initDummy(ArgumentOption option) {
		createAndStoreNamedSearchers(option);
	}

	/** parse option within plugin.
	 * 
	 * mots plugins will have one or more named options
	 * options are normally defined by an element in args.xml , e.g. 
	 * <arg name="foo"> 
	 * </arg>
	 * 
	 * @param option
	 * @param argIterator
	 */
	public void parseFooOption(ArgumentOption option, ArgIterator argIterator) {
	}

	public void runExtractDummy(ArgumentOption option) {
		searchSectionElements();
	}

	public void outputDummy(ArgumentOption option) {
		getOrCreateContentProcessor().outputResultElements(option.getName(), this);
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
	protected AMISearcher createSearcher(NamedPattern namedPattern) {
		return new DummySearcher(this, namedPattern);
	}



}
