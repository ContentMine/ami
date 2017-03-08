package org.xmlcml.ami2.plugins.regex;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.ami2.plugins.AMIArgProcessor;
import org.xmlcml.ami2.plugins.AMISearcher;
import org.xmlcml.cproject.args.ArgIterator;
import org.xmlcml.cproject.args.ArgumentOption;
import org.xmlcml.cproject.files.ContentProcessor;
import org.xmlcml.cproject.files.ResultsElement;

/** 
 * Processes commandline arguments.
 * 
 * @author pm286
 */
public class RegexArgProcessor extends AMIArgProcessor {
	
	public static final Logger LOG = Logger.getLogger(RegexArgProcessor.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	static final String TILDE = "~";
	static final String TILDE_SUFFIX = "(?:[^\\\\s]*\\\\p{Punct}?)";
	
	private Map<String, ResultsElement> resultsByCompoundRegex;
	protected List<String> words;
	
	public RegexArgProcessor() {
		super();
		this.addVariableAndExpandReferences(TILDE, TILDE_SUFFIX);
	}

	public RegexArgProcessor(String[] args) {
		this();
		parseArgs(args);
	}

	public RegexArgProcessor(String argString) {
		this(argString.split(WHITESPACE));
	}

	// =============== METHODS ==============

	public void parseRegex(ArgumentOption option, ArgIterator argIterator) {
		List<String> tokens= argIterator.createTokenListUpToNextNonDigitMinus(option);
		createCompoundRegexes(option, tokens);
	}

	public void runRegex(ArgumentOption option) {
		runRegex();
	}

	public void outputResultElements(ArgumentOption option) {
		outputResultElements(option.getName());
	}

	// =========================

	/** create Subclassed Searcher.
	 * 
	 * //PLUGIN
	 * 
	 * Most plugins should Override this and create a FooSearcher.
	 * 
	 * @param argProcessor
	 * @param compoundRegex
	 * @return subclassed Plugin
	 */
	public AMISearcher createSearcher(AMIArgProcessor argProcessor, CompoundRegex compoundRegex) {
		RegexSearcher regexSearcher = RegexSearcher.createSearcher(argProcessor);
		regexSearcher.setCompoundRegex(compoundRegex);
		return regexSearcher;
	}


	/** .
	 * 
	 * @param name of option
	 */
	private void outputResultElements(String name) {
		ContentProcessor currentContentProcessor = currentCTree.getOrCreateContentProcessor();
		currentContentProcessor.clearResultsElementList();
		if (resultsByCompoundRegex == null) {
			LOG.warn("have not run regex (runRegex)");
			return;
		}
		for (CompoundRegex compoundRegex : compoundRegexList) {
			String regexTitle = compoundRegex.getTitle();
			ResultsElement resultsElement = resultsByCompoundRegex.get(regexTitle);
			resultsElement.setTitle(regexTitle);
			currentContentProcessor.addResultsElement(resultsElement);
		}
		currentContentProcessor.createResultsDirectoriesAndOutputResultsElement(name);
	}

	private void runRegex() {
		ensureSectionElements();
		resultsByCompoundRegex = new HashMap<String, ResultsElement>();
		for (CompoundRegex compoundRegex : compoundRegexList) {
			AMISearcher regexSearcher = createSearcher(this, compoundRegex);
			ResultsElement resultsElement = regexSearcher.search(sectionElements, createResultsElement());
			resultsByCompoundRegex.put(compoundRegex.getTitle(), resultsElement);
		}
	}

	@Override
	/** parse args and resolve their dependencies.
	 * 
	 * (don't run any argument actions)
	 * 
	 */
	public void parseArgs(String[] args) {
		super.parseArgs(args);
	}

}
