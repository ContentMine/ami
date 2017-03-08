package org.xmlcml.ami2.plugins.simple;

import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.ami2.plugins.AMIArgProcessor;
import org.xmlcml.cproject.args.ArgIterator;
import org.xmlcml.cproject.args.ArgumentOption;
import org.xmlcml.cproject.files.CTree;
import org.xmlcml.cproject.files.ResultElement;
import org.xmlcml.cproject.files.ResultsElement;

/** 
 * Processes commandline arguments.
 * 
 * @author pm286
 */
public class SimpleArgProcessor extends AMIArgProcessor {
	
	public static final Logger LOG = Logger.getLogger(SimpleArgProcessor.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	protected List<String> words;

	public SimpleArgProcessor() {
		super();
	}

	public SimpleArgProcessor(String[] args) {
		this();
		parseArgs(args);
	}

	public SimpleArgProcessor(String argString) {
		this(argString.split(WHITESPACE));
	}

	// =============== METHODS ==============

	public void parseSimple(ArgumentOption option, ArgIterator argIterator) {
		List<String> tokens = argIterator.createTokenListUpToNextNonDigitMinus(option);
	}
	
	public void countWords(ArgumentOption option) {
		words = currentCTree.extractWordsFromScholarlyHtml();
	}

	public void outputWordCounts(ArgumentOption option) {
		String outputFilename = getOutput();
		if (!CTree.isReservedFilename(outputFilename)) {
			throw new RuntimeException("Output is not a reserved file: "+outputFilename);
		}
		ResultsElement resultsElement = new ResultsElement();
		ResultElement resultElement = new ResultElement();
		resultElement.setValue("wordCount", String.valueOf(words.size()));
		resultsElement.appendChild(resultElement);
		getOrCreateContentProcessor().writeResults(outputFilename, resultsElement);
	}
	
	// =============================

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
