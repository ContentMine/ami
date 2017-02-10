package org.xmlcml.ami2.plugins.word;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.ami2.plugins.AMIArgProcessor;
import org.xmlcml.ami2.plugins.AMISearcher;
import org.xmlcml.ami2.plugins.search.SearchSearcher;
import org.xmlcml.cproject.args.ArgIterator;
import org.xmlcml.cproject.args.ArgumentOption;
import org.xmlcml.cproject.files.CTree;
import org.xmlcml.cproject.files.ContentProcessor;
import org.xmlcml.cproject.files.ResultsElement;
import org.xmlcml.cproject.files.ResultsElementList;
import org.xmlcml.cproject.lookup.DefaultStringDictionary;
import org.xmlcml.euclid.IntRange;
import org.xmlcml.xml.XMLUtil;

/** 
 * Processes commandline arguments.
 * 
 * @author pm286
 */
public class WordArgProcessor extends AMIArgProcessor {
	
	
	public static final Logger LOG = Logger.getLogger(WordArgProcessor.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	public final static String FREQUENCIES = "frequencies";
	public final static String WORD_LENGTHS = "wordLengths";
	public final static String WORD_FREQUENCIES = "wordFrequencies"; // deprecated
	public final static String SEARCH = "search";					
	public final static String WORD_SEARCH = "wordSearch";			// deprecated
	public final static List<String> ANALYSIS_METHODS = Arrays.asList(
		new String[]{
				FREQUENCIES,
				WORD_FREQUENCIES,
				WORD_LENGTHS,
				SEARCH,
				WORD_SEARCH
		});
	
	private static final String TFIDF = "tfidf";
	private static final String TFIDF_XML = "tfidf.xml";
	private static final String TFIDF_HTML = "tfidf.html";
	private static final String AGGREGATE_FREQUENCY = "aggregate";
	private static final String AGGREGATE_XML = "aggregate.xml";
	private static final String AGGREGATE_HTML = "aggregate.html";
	private static final String BOOLEAN_FREQUENCY = "booleanFrequency";
	private static final String BOOLEAN_FREQUENCY_XML = "booleanFrequency.xml";
	private static final String BOOLEAN_FREQUENCY_HTML = "booleanFrequency.html";
	private static final String TFIDF_FREQUENCY = "tfidfFrequency";
	private static final String TFIDF_FREQUENCY_XML = "tfidfFrequency.xml";
	private static final String TFIDF_FREQUENCY_HTML = "tfidfFrequency.html";
	static final double MIN_FONT = 10;
	static final double MAX_FONT = 30;
	
	private List<String> chosenWordAggregationMethods = new ArrayList<String>();
	private IntRange wordLengthRange;
	protected List<String> words;
	private List<String> summaryMethods;
	WordResultsElementList frequenciesElementList;
	WordResultsElement aggregatedFrequenciesElement;
	private IntRange wordCount;
	private WordResultsElement booleanFrequencyElement;
	private Map<String, ResultsElement> resultsByDictionary;
	
	public WordArgProcessor() {
		super();
	}

	public WordArgProcessor(String args) {
		this();
		parseArgs(args);
	}

	public WordArgProcessor(String[] args) {
		this();
		parseArgs(args);
	}

	// =============== METHODS ==============

	/** select methods to use
	 * 
	 * @param option list of methods (none gives help)
	 * @param argIterator
	 */
	public void parseWords(ArgumentOption option, ArgIterator argIterator) {
		List<String> tokens = argIterator.createTokenListUpToNextNonDigitMinus(option);
		if (tokens.size() == 0) {
			helpMethods();
		} else {
			chosenWordAggregationMethods = getChosenList(ANALYSIS_METHODS, tokens);
		}
	}

	public void parseWordLengths(ArgumentOption option, ArgIterator argIterator) {
		wordLengthRange =argIterator.getIntRange(option);
		if (wordLengthRange.getMin() < 1 || wordLengthRange.getMax() < 1) {
			throw new RuntimeException("bad word lengths: "+wordLengthRange);
		}
	}

	public void parseWordTypes(ArgumentOption option, ArgIterator argIterator) {
		List<String> tokens = argIterator.createTokenListUpToNextNonDigitMinus(option);
		if (tokens.size() == 0) {
			helpWordTypes();
		} else {
			chosenWordTypes = getChosenList(WORD_TYPES, tokens);
		}
	}
	
	public void parseMinCount(ArgumentOption option, ArgIterator argIterator) {
		wordCount = argIterator.getIntRange(option);
	}
	
	public void runExtractWords(ArgumentOption option) {
		ensureWordCollectionFactory();
		wordCollectionFactory.extractWords();
	}
	
	public void parseSearch(ArgumentOption option, ArgIterator argIterator) {
		ensureSearcherList();
		List<String> dictionarySources = argIterator.createTokenListUpToNextNonDigitMinus(option);
		createAndAddDictionaries(dictionarySources);
		for (DefaultStringDictionary dictionary : this.getDictionaryList()) {
			AMISearcher wordSearcher = new SearchSearcher(this, dictionary);
			searcherList.add(wordSearcher);
			wordSearcher.setName(dictionary.getTitle());
		}
//		wordSearcher.setDictionaryList(this.getDictionaryList());
	}
	
	/** refactor output option.
	 * 
	 * @param option
	 */
//	@Deprecated 
	// this 
	public void outputWords(ArgumentOption option) {
		ContentProcessor currentContentProcessor = getOrCreateContentProcessor();
		ResultsElementList resultsElementList = currentContentProcessor.getOrCreateResultsElementList();
		for (int i = 0; i < resultsElementList.size(); i++) {
			File outputDirectory = currentContentProcessor.createResultsDirectoryAndOutputResultsElement(
					option, resultsElementList.get(i)/*, CTree.RESULTS_XML*/);
			File htmlFile = new File(outputDirectory, CTree.RESULTS_HTML);
			((WordResultsElement) resultsElementList.get(i)).writeResultsElementAsHTML(htmlFile, this);
		}
	}
	
	public void parseSummary(ArgumentOption option, ArgIterator argIterator) {
		List<String> tokens = argIterator.createTokenListUpToNextNonDigitMinus(option);
		if (tokens.size() == 0) {
			LOG.error("parseSummary needs a list of actions");
		} else {
			summaryMethods = tokens;
		}
	}
	
	public void finalSummary(ArgumentOption option) {
		WordResultsElementList frequenciesElementList = this.aggregateOverCMDirList(getPlugin(), WordArgProcessor.FREQUENCIES);
		ensureWordCollectionFactory();
		for (String method : summaryMethods) {
			runSummaryMethod(frequenciesElementList, wordCollectionFactory, method);
		}
	}

	private void runSummaryMethod(WordResultsElementList frequenciesElementList,
			WordCollectionFactory wordCollectionFactory, String method) {
		if (AGGREGATE_FREQUENCY.equals(method) && summaryFileName != null) {
			aggregatedFrequenciesElement = wordCollectionFactory.createAggregatedFrequenciesElement(frequenciesElementList);
			writeResultsElement(new File(summaryFileName, AGGREGATE_XML), aggregatedFrequenciesElement);
			aggregatedFrequenciesElement.writeResultsElementAsHTML(new File(summaryFileName, AGGREGATE_HTML), this);
		} else if (BOOLEAN_FREQUENCY.equals(method) && summaryFileName != null) {
			booleanFrequencyElement = wordCollectionFactory.createBooleanFrequencies(this, frequenciesElementList);
			writeResultsElement(new File(summaryFileName, BOOLEAN_FREQUENCY_XML), booleanFrequencyElement);
			booleanFrequencyElement.writeResultsElementAsHTML(new File(summaryFileName, BOOLEAN_FREQUENCY_HTML), this);
		} else if (TFIDF_FREQUENCY.equals(method) && summaryFileName != null) {
			WordResultsElement tfidfFrequencyElement = wordCollectionFactory.createTFIDFFrequencies(this, frequenciesElementList);
			writeResultsElement(new File(summaryFileName, TFIDF_XML), tfidfFrequencyElement);
			tfidfFrequencyElement.writeResultsElementAsHTML(new File(summaryFileName, TFIDF_HTML), this);
		}
	}

	public void runSearch(ArgumentOption option) {
		LOG.warn("moved to wordSearch");
//		ensureResultsByDictionary();
//		ensureSearcherList();
//		for (AMISearcher searcher : searcherList) {
//			SearchSearcher wordSearcher = (SearchSearcher)searcher;
//			String title = wordSearcher.getTitle();
//			ResultsElement resultsElement = wordSearcher.searchWordList();
//			resultsElement.setTitle(title);
//			resultsByDictionary.put(title, resultsElement);
//		}
	}
	
	public void outputSearch(ArgumentOption option) {
		outputResultsElements(option.getName());
	}

	private void outputResultsElements(String name) {
		ContentProcessor currentContentProcessor = currentCTree.getOrCreateContentProcessor();
		currentContentProcessor.clearResultsElementList();

		for (String title : resultsByDictionary.keySet()) {
			ResultsElement resultsElement = resultsByDictionary.get(title);
			resultsElement.setTitle(title);
			currentContentProcessor.addResultsElement(resultsElement);
		}
		currentContentProcessor.createResultsDirectoriesAndOutputResultsElement(name);
	}
	


	private static void writeResultsElement(File outputFile, ResultsElement resultsElement) {
		try {
			outputFile.getParentFile().mkdirs();
			XMLUtil.debug(resultsElement, new FileOutputStream(outputFile), 1);
		} catch (IOException e) {
			throw new RuntimeException("Cannot write file "+outputFile, e);
		}
	}
	
	// =============================

//	private void ensureResultsByDictionary() {
//		if (resultsByDictionary == null) {
//			resultsByDictionary = new HashMap<String, ResultsElement>();
//		}
//	}


	public WordResultsElementList aggregateOverCMDirList(String pluginName, String methodName) {
		WordResultsElementList resultsElementList = new WordResultsElementList();
		for (CTree cTree : cTreeList) {
			ResultsElement resultsElement = cTree.getResultsElement(pluginName, methodName);
			if (resultsElement == null) {
				LOG.error("Null results element, skipped "+cTree.getDirectory());
			} else {
				WordResultsElement wordResultsElement = new WordResultsElement(cTree.getResultsElement(pluginName, methodName));
				resultsElementList.add(wordResultsElement);
			}
		}
		return resultsElementList;
	}

	private void helpMethods() {
		System.err.println("ANALYSIS METHODS");
		for (String method : ANALYSIS_METHODS) {
			System.err.println("  "+method);
		}
	}
	
	private void helpWordTypes() {
		System.err.println("WORD TYPES");
		for (String type : WORD_TYPES) {
			System.err.println("  "+type);
		}
	}

	public IntRange getWordLengthRange() {
		return wordLengthRange;
	}

	public List<String> getChosenWordAggregationMethods() {
		return chosenWordAggregationMethods;
	}

}
