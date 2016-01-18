package org.xmlcml.ami2.dictionary;

import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/** class to support multiword and stemmed terms.
 * 
 * @author pm286
 *
 */
public class DictionaryTerm implements Comparable<DictionaryTerm> {

	private static final Logger LOG = Logger.getLogger(DictionaryTerm.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	private TermPhrase termPhrase;
	private TermPhrase lowerCaseTermPhrase;
	private TermPhrase stemmedTermPhrase;
	private TermPhrase lowerCaseStemmedTermPhrase;
	
	public DictionaryTerm(String termString) {
		termPhrase = TermPhrase.createTermPhrase(termString);
		createStemmedAndCaseVariants(termString);
	}

	private void createStemmedAndCaseVariants(String termString) {
		stemmedTermPhrase = TermPhrase.createTermPhrase(termString);
		stemmedTermPhrase.applyPorterStemming();
		lowerCaseTermPhrase = TermPhrase.createTermPhrase(termString);
		lowerCaseTermPhrase.toLowerCase();
		lowerCaseStemmedTermPhrase = TermPhrase.createTermPhrase(termString);
		lowerCaseStemmedTermPhrase.applyPorterStemming();
		lowerCaseStemmedTermPhrase.toLowerCase();
	}
	
	public int size() {
		return termPhrase.getWords().size();
	}

	/** sort alphabetically
	 * 
	 * @param dictionaryTerm
	 * @return
	 */
	public int compareTo(DictionaryTerm dictionaryTerm) {
		return this.termPhrase.compareTo(dictionaryTerm.termPhrase);
	}

	public TermPhrase getTermPhrase() {
		return termPhrase;
	}

	public TermPhrase getLowerCaseTermPhrase() {
		return lowerCaseTermPhrase;
	}

	public TermPhrase getStemmedTermPhrase() {
		return stemmedTermPhrase;
	}

	public TermPhrase getLowerCaseStemmedTermPhrase() {
		return lowerCaseStemmedTermPhrase;
	}
	
	public String toString() {
		return termPhrase.toString();
	}

	/** match against term phrase
	 * 
	 * very crude - no hashing, just for test
	 * 
	 * @param wordList
	 * @param i
	 * @return
	 */
	public boolean match(List<String> wordList, int i) {
		boolean result = false;
		int size = this.size();
		if (i < wordList.size() - size + 1) {
			List<String> phraseWords = this.termPhrase.getWordList();
			result = true;
			for (int j = 0; j < size; j++) {
				if (!wordList.get(i + j).equals(phraseWords.get(j))) {
					result = false;
					break;
				}
			}
		}
		return result;
	}
	
}
