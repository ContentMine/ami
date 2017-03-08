package org.xmlcml.ami2.plugins.gene;

import java.util.ArrayList;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.ami2.dictionary.DefaultAMIDictionary;
import org.xmlcml.ami2.dictionary.gene.HGNCDictionary;
import org.xmlcml.ami2.plugins.AMIArgProcessor;
import org.xmlcml.ami2.plugins.AMISearcher;
import org.xmlcml.ami2.plugins.NamedPattern;
import org.xmlcml.cproject.args.ArgIterator;
import org.xmlcml.cproject.args.ArgumentOption;
import org.xmlcml.cproject.files.ResultsElement;
import org.xmlcml.cproject.lookup.DefaultStringDictionary;

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
		this(argString.split(WHITESPACE));
	}

	// =============== METHODS ==============
	
	public void initGene(ArgumentOption option) {
		createAndStoreNamedSearchers(option);
	}

	// this will probably become deprecated
	public void parseTypes(ArgumentOption option, ArgIterator argIterator) {
		createSearcherList(option, argIterator);
	}

	public void runExtractGene(ArgumentOption option) {
		ensureDictionaryList();
		if (dictionaryList.size() > 0) {
			searcherList = new ArrayList<AMISearcher>();
			for (DefaultStringDictionary dictionary : dictionaryList) {
				GeneSearcher searcher = new GeneSearcher(this, dictionary);
				searcherList.add(searcher);
			}
		} 
		searchSectionElements();
	}

	public void outputGene(ArgumentOption option) {
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
	 * @return subclassed searcher
	 */
	@Override
	protected AMISearcher createSearcher(NamedPattern namedPattern) {
		return new GeneSearcher(this, namedPattern);
	}

	/**
	 * 
	 * @param dictionary
	 * @return subclassed searcher
	 */
	@Override
	protected AMISearcher createSearcher(DefaultStringDictionary dictionary) {
		return new GeneSearcher(this, dictionary);
	}

	@Override
	protected ResultsElement createResultsElement() {
		return new GeneResultsElement();
	}

	public DefaultAMIDictionary getOrCreateCurrentDictionary() {
		if (currentDictionary == null) {
			currentDictionary = new HGNCDictionary();
		}
		return currentDictionary;
	}




}
