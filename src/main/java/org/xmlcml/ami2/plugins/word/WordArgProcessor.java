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
import org.xmlcml.cmine.args.ArgIterator;
import org.xmlcml.cmine.args.ArgumentOption;
import org.xmlcml.cmine.files.CTree;
import org.xmlcml.cmine.files.ContentProcessor;
import org.xmlcml.cmine.files.ResultsElement;
import org.xmlcml.cmine.files.ResultsElementList;
import org.xmlcml.cmine.lookup.DefaultStringDictionary;
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
	
	public final static String WORD_LENGTHS = "wordLengths";
	public final static String WORD_FREQUENCIES = "wordFrequencies";
	public final static String WORD_SEARCH = "wordSearch";
	public final static List<String> ANALYSIS_METHODS = Arrays.asList(
		new String[]{
				WORD_FREQUENCIES,
				WORD_LENGTHS,
				WORD_SEARCH
		});
	
	public final static String ABBREVIATION = "abbreviation";
	public final static String ACRONYM = "acronym";
	public final static String CAPITALIZED = "capitalized";
	public final static List<String> WORD_TYPES = Arrays.asList(
		new String[]{
				ABBREVIATION,
				ACRONYM,
				CAPITALIZED
		});
	
	public final static String PRESERVE = "preserve";
	public final static String IGNORE = "ignore";
	public final static List<String> CASE_TYPES = Arrays.asList(
		new String[]{
				IGNORE,
				ABBREVIATION,
				PRESERVE
		});

	public static final String FREQUENCIES = "frequencies";

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
	
	private List<WordSetWrapper> stopwordSetList;
	private List<String> chosenMethods = new ArrayList<String>();
	private IntRange wordLengthRange;
	private List<String> chosenWordTypes;
	protected List<String> words;
	private Boolean stemming;
	private List<String> wordCaseList = new ArrayList<String>();
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
			chosenMethods = getChosenList(ANALYSIS_METHODS, tokens);
		}
	}

	/** caseSensitive?
	 * 
	 * @param option list of methods (none gives help)
	 * @param argIterator
	 */
	public void parseCase(ArgumentOption option, ArgIterator argIterator) {
		List<String> tokens = argIterator.createTokenListUpToNextNonDigitMinus(option);
		wordCaseList = new ArrayList<String>();
		if (tokens.size() == 0) {
			wordCaseList.add(PRESERVE);
		} else {
			wordCaseList = tokens;
		}
		checkWordCaseList();
	}

	/** use stemming?
	 * 
	 * will have to use import org.apache.lucene.analysis.en.PorterStemFilter;
	 * 
	 * @param option list of methods (none gives help)
	 * @param argIterator
	 */
	public void parseStem(ArgumentOption option, ArgIterator argIterator) {
		stemming = argIterator.getBoolean(option);
	}

	public void parseStopwords(ArgumentOption option, ArgIterator argIterator) {
		List<String> stopwordLocations = argIterator.createTokenListUpToNextNonDigitMinus(option);
		addStopwords(stopwordLocations);
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
			AMISearcher wordSearcher = new WordSearcher(this, dictionary);
			searcherList.add(wordSearcher);
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
		ensureResultsByDictionary();
		ensureSearcherList();
		for (AMISearcher searcher : searcherList) {
			WordSearcher wordSearcher = (WordSearcher)searcher;
			String title = wordSearcher.getTitle();
			ResultsElement resultsElement = wordSearcher.searchWordList();
			resultsElement.setTitle(title);
			resultsByDictionary.put(title, resultsElement);
		}
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

	private void ensureResultsByDictionary() {
		if (resultsByDictionary == null) {
			resultsByDictionary = new HashMap<String, ResultsElement>();
		}
	}


	private void addStopwords(List<String> stopwordLocations) {
		ensureStopwordSetList();
		for (String stopwordLocation : stopwordLocations) {
			addStopwords(stopwordLocation);
		}
	}

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

	private void checkWordCaseList() {
		if (wordCaseList.size() == 1 && PRESERVE.equals(wordCaseList.get(0))) {
			// OK
		} else {
			for (int i = wordCaseList.size() - 1; i >= 0; i--) {
				String word = wordCaseList.get(i);
				if (wordCaseList.contains(PRESERVE) || !CASE_TYPES.contains(word)) {
					LOG.error("Removed forbidden/unknown word: "+word);
					wordCaseList.remove(i);
				}
			}
		}
	}

	private void addStopwords(String stopwordLocation) {
		ensureStopwordSetList();
		WordSetWrapper stopwordSet = WordSetWrapper.createStopwordSet(stopwordLocation);
		if (stopwordSet != null) {
			stopwordSetList.add(stopwordSet);
		}
	}
	
	private void ensureStopwordSetList() {
		if (stopwordSetList == null) {
			stopwordSetList = new ArrayList<WordSetWrapper>();
		}
	}
	
	public IntRange getWordLengthRange() {
		return wordLengthRange;
	}

	public List<WordSetWrapper> getStopwordSetList() {
		ensureStopwordSetList();
		return stopwordSetList;
	}

	public List<String> getChosenMethods() {
		return chosenMethods;
	}

	public boolean getStemming() {
		return stemming;
	}

	public List<String> getWordCaseList() {
		return wordCaseList;
	}

	public List<String> getChosenWordTypes() {
		ensureChosenWordTypes();
		return chosenWordTypes;
	}

	private void ensureChosenWordTypes() {
		if (chosenWordTypes == null) {
			chosenWordTypes = new ArrayList<String>();
		}
	}

}
