package org.xmlcml.ami2.plugins.word;

import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.ami2.plugins.AMIArgProcessor;
import org.xmlcml.ami2.plugins.AMISearcher;
import org.xmlcml.ami2.plugins.regex.CompoundRegex;
import org.xmlcml.ami2.plugins.regex.RegexComponent;
import org.xmlcml.cmine.files.ResultsElement;
import org.xmlcml.cmine.lookup.AbstractDictionary;

import nu.xom.Element;

public class WordSearcher extends AMISearcher {

	
	public static final Logger LOG = Logger.getLogger(WordSearcher.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	List<RegexComponent> componentList;
	private CompoundRegex compoundRegex;
	Element resultElement;
	private List<String> searchWords;
	private ResultsElement resultsElement;
	
	public WordSearcher(AMIArgProcessor argProcessor, AbstractDictionary dictionary) {
		super(argProcessor, dictionary);
	}

	public static AMISearcher createSearcher(AMIArgProcessor argProcessor, AbstractDictionary dictionary) {
		return new WordSearcher(argProcessor, dictionary);
	}

	void setSearchWords(List<String> searchWords) {
		this.searchWords = searchWords;
	}

	// ====== args ========

	/**
	 * 
	 * @return resultsElements 
	 */
	public ResultsElement searchWordList() {
		List<String> strings = new WordCollectionFactory((WordArgProcessor)this.getArgProcessor()).createWordList();
		ResultsElement resultsElement = searchWithDictionary(strings);
		return resultsElement;
	}

	public ResultsElement getResultsElement() {
		return resultsElement;
	}

	

	// ===============
	
	public void debug() {
		LOG.debug(compoundRegex.getTitle()+"/"+compoundRegex.getRegexValues().size());
	}


}
