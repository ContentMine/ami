package org.xmlcml.ami2.plugins.search;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.ami2.plugins.AMIArgProcessor;
import org.xmlcml.ami2.plugins.AMISearcher;
import org.xmlcml.ami2.plugins.word.WordResultsElement;
import org.xmlcml.ami2.plugins.word.WordResultsElementList;
import org.xmlcml.cproject.args.ArgIterator;
import org.xmlcml.cproject.args.ArgumentOption;
import org.xmlcml.cproject.files.CTree;
import org.xmlcml.cproject.files.ContentProcessor;
import org.xmlcml.cproject.files.ResultsElement;
import org.xmlcml.cproject.files.ResultsElementList;
import org.xmlcml.cproject.lookup.DefaultStringDictionary;

/** 
 * Processes commandline arguments.
 * 
 * @author pm286
 */
public class SearchArgProcessor extends AMIArgProcessor {
	
	public static final Logger LOG = Logger.getLogger(SearchArgProcessor.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
//	public final static String SEARCH = "search";					
//	public final static String WORD_SEARCH = "wordSearch";			// deprecated
//	public final static List<String> ANALYSIS_METHODS = Arrays.asList(
//		new String[]{
//				SEARCH,
//				WORD_SEARCH
//		});
//	
//	public final static String ABBREVIATION = "abbreviation";
//	public final static String ACRONYM = "acronym";
//	public final static String CAPITALIZED = "capitalized";
//	public final static List<String> WORD_TYPES = Arrays.asList(
//		new String[]{
////				ABBREVIATION,
////				ACRONYM,
////				CAPITALIZED
//		});
//	
//	public final static String PRESERVE = "preserve";
//	public final static String IGNORE = "ignore";
//	public final static List<String> CASE_TYPES = Arrays.asList(
//		new String[]{
////				IGNORE,
////				ABBREVIATION,
////				PRESERVE
//		});

	static final double MIN_FONT = 10;
	static final double MAX_FONT = 30;
	
//	private List<WordSetWrapper> stopwordSetList;
//	private List<String> chosenMethods = new ArrayList<String>();
//	private IntRange wordLengthRange;
//	private List<String> chosenWordTypes;
//	protected List<String> words;
//	private Boolean stemming;
//	private List<String> wordCaseList = new ArrayList<String>();
//	private List<String> summaryMethods;
//	WordResultsElementList frequenciesElementList;
//	WordResultsElement aggregatedFrequenciesElement;
//	private IntRange wordCount;
//	private WordResultsElement booleanFrequencyElement;
	private Map<String, ResultsElement> resultsByDictionary;
	
	public SearchArgProcessor() {
		super();
	}

	public SearchArgProcessor(String args) {
		this();
		parseArgs(args);
	}

	public SearchArgProcessor(String[] args) {
		this();
		parseArgs(args);
	}

	// =============== METHODS ==============

//	/** select methods to use
//	 * 
//	 * @param option list of methods (none gives help)
//	 * @param argIterator
//	 */
//	public void parseWords(ArgumentOption option, ArgIterator argIterator) {
//		List<String> tokens = argIterator.createTokenListUpToNextNonDigitMinus(option);
//		if (tokens.size() == 0) {
//			helpMethods();
//		} else {
//			chosenMethods = getChosenList(ANALYSIS_METHODS, tokens);
//		}
//	}

//	/** caseSensitive?
//	 * 
//	 * @param option list of methods (none gives help)
//	 * @param argIterator
//	 */
//	public void parseCase(ArgumentOption option, ArgIterator argIterator) {
//		List<String> tokens = argIterator.createTokenListUpToNextNonDigitMinus(option);
//		wordCaseList = new ArrayList<String>();
//		if (tokens.size() == 0) {
//			wordCaseList.add(PRESERVE);
//		} else {
//			wordCaseList = tokens;
//		}
//		checkWordCaseList();
//	}

//	/** use stemming?
//	 * 
//	 * will have to use import org.apache.lucene.analysis.en.PorterStemFilter;
//	 * 
//	 * @param option list of methods (none gives help)
//	 * @param argIterator
//	 */
//	public void parseStem(ArgumentOption option, ArgIterator argIterator) {
//		stemming = argIterator.getBoolean(option);
//	}
//
//	public void parseStopwords(ArgumentOption option, ArgIterator argIterator) {
//		List<String> stopwordLocations = argIterator.createTokenListUpToNextNonDigitMinus(option);
//		addStopwords(stopwordLocations);
//	}
//
//	public void parseWordLengths(ArgumentOption option, ArgIterator argIterator) {
//		wordLengthRange =argIterator.getIntRange(option);
//		if (wordLengthRange.getMin() < 1 || wordLengthRange.getMax() < 1) {
//			throw new RuntimeException("bad word lengths: "+wordLengthRange);
//		}
//	}

//	public void parseWordTypes(ArgumentOption option, ArgIterator argIterator) {
//		List<String> tokens = argIterator.createTokenListUpToNextNonDigitMinus(option);
//		if (tokens.size() == 0) {
//			helpWordTypes();
//		} else {
//			chosenWordTypes = getChosenList(WORD_TYPES, tokens);
//		}
//	}
	
//	public void parseMinCount(ArgumentOption option, ArgIterator argIterator) {
//		wordCount = argIterator.getIntRange(option);
//	}
	
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
//			summaryMethods = tokens;
			LOG.warn("no-op");
		}
	}
	
	public void finalSummary(ArgumentOption option) {
		LOG.warn("finalSummary no-op");
	}

	public void runSearch(ArgumentOption option) {
		ensureResultsByDictionary();
		ensureSearcherList();
		for (AMISearcher searcher : searcherList) {
			SearchSearcher wordSearcher = (SearchSearcher)searcher;
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
	

//
//	private static void writeResultsElement(File outputFile, ResultsElement resultsElement) {
//		try {
//			outputFile.getParentFile().mkdirs();
//			XMLUtil.debug(resultsElement, new FileOutputStream(outputFile), 1);
//		} catch (IOException e) {
//			throw new RuntimeException("Cannot write file "+outputFile, e);
//		}
//	}
//	
	// =============================

	private void ensureResultsByDictionary() {
		if (resultsByDictionary == null) {
			resultsByDictionary = new HashMap<String, ResultsElement>();
		}
	}


//	private void addStopwords(List<String> stopwordLocations) {
//		ensureStopwordSetList();
//		for (String stopwordLocation : stopwordLocations) {
//			addStopwords(stopwordLocation);
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
//		System.err.println("ANALYSIS METHODS");
//		for (String method : ANALYSIS_METHODS) {
//			System.err.println("  "+method);
//		}
	}
	
	private void helpWordTypes() {
		System.err.println("WORD TYPES");
		for (String type : WORD_TYPES) {
			System.err.println("  "+type);
		}
	}


}
