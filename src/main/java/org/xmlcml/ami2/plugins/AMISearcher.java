package org.xmlcml.ami2.plugins;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.ami2.dictionary.DefaultAMIDictionary;
import org.xmlcml.ami2.plugins.species.LinneanNamer;
import org.xmlcml.ami2.plugins.species.SpeciesResultsElement;
import org.xmlcml.ami2.plugins.word.WordCollectionFactory;
import org.xmlcml.cmine.args.DefaultArgProcessor;
import org.xmlcml.cmine.files.AbstractSearcher;
import org.xmlcml.cmine.files.ResultElement;
import org.xmlcml.cmine.files.ResultsElement;
import org.xmlcml.cmine.lookup.AbstractDictionary;
import org.xmlcml.cmine.lookup.AbstractLookup;
import org.xmlcml.xml.XPathGenerator;

import nu.xom.Attribute;
import nu.xom.Element;

public class AMISearcher extends AbstractSearcher {

	private static final String NOT_FOUND = "NOT_FOUND";
	public static final Logger LOG = Logger.getLogger(AMISearcher.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	private String exactMatch;
	private AbstractLookup lookup;
	private NamedPattern namedPattern;
	private AMIArgProcessor amiArgProcessor;
	public static int DEFAULT_POST_WORD_COUNT = 10;
	public static int DEFAULT_PRE_WORD_COUNT = 10;
	public static final String EXACT = "exact";
	public static final String POST = "post";
	public static final String PRE = "pre";
	protected Integer[] contextCounts;
	protected AbstractDictionary dictionary;
	public int maxPostWordCount = DEFAULT_POST_WORD_COUNT;
	public int maxPreWordCount = DEFAULT_PRE_WORD_COUNT;
	protected String name;
	public Pattern pattern;
	public List<String> stringList;
	
	static {
		LOG.setLevel(Level.DEBUG);
	}

	public AMISearcher(AMIArgProcessor argProcessor) {
		this.amiArgProcessor = argProcessor;
		contextCounts = argProcessor.getContextCount();
		if (this.amiArgProcessor == null) {
			throw new RuntimeException("null argProcessor");
		}
	}

	public AMISearcher(AMIArgProcessor argProcessor, NamedPattern namedPattern) {
		this(argProcessor);
		this.setNamedPattern(namedPattern);
	}

	public AMISearcher(AMIArgProcessor argProcessor, AbstractDictionary dictionary) {
		this(argProcessor);
		this.setDictionary(dictionary);
		this.name = dictionary.getTitle();
	}

	protected void matchAndAddPrePost(String value, Matcher matcher, ResultElement resultElement) {
		String exactMatch = matcher.group(0);
		int preEnd = matcher.start();
		int preStart = Math.max(0, preEnd - contextCounts[0]);
		int postStart = matcher.end();
		int postEnd = Math.min(value.length(), postStart + contextCounts[1]);
		resultElement.setPre(flattenHtmlInlineTags(value.substring(preStart, preEnd)));
		exactMatch = flattenHtmlInlineTags(exactMatch);
		resultElement.setExact(exactMatch);
		resultElement.setPost(flattenHtmlInlineTags(value.substring(postStart, postEnd)));
		lookupMatchAndAddLookupRefs(resultElement);
	}

	private void lookupMatchAndAddLookupRefs(ResultElement resultElement) {
		Map<String, AbstractLookup> lookupInstanceByName = amiArgProcessor.getOrCreateLookupInstanceByName();
		for (String lookupName : lookupInstanceByName.keySet()) {
			AbstractLookup lookup = lookupInstanceByName.get(lookupName);
			Map<String, String> lookupRefByMatch = lookup.getOrCreateLookupRefByMatch();
			String lookupRef = lookupRefByMatch.get(exactMatch);
			if (lookupRef == null) {
				try {
					lookupRef = lookup.lookup(exactMatch);
				} catch (IOException e) {
					LOG.debug("Cannot find match: "+exactMatch+" in "+lookupName);
				}
				lookupRef = lookupRef == null ? NOT_FOUND : lookupRef;
				lookupRefByMatch.put(exactMatch,  lookupRef);
			}
			if (!(NOT_FOUND.equals(lookupRef))) {
				resultElement.addAttribute(new Attribute(lookupName, lookupRef));
			}
		}
	}
	
	protected String flattenHtmlInlineTags(String s) {
		s = s.replaceAll("<[^>]*>", "");
		return s;
	}

	public ResultElement createResultElement(String value, Matcher matcher) {
		ResultElement resultElement = createResultElement();
		matchAndAddPrePost(value, matcher, resultElement);
		return resultElement;
	}
	
	/**
	 *  //PLUGIN
	 */
	public ResultElement createResultElement() {
		return new AMIResultElement();
	}


	protected void addXpathAndAddtoResultsElement(Element elementToSearch, ResultsElement resultsElement,
			ResultsElement resultsElementToAdd) {
		if (resultsElementToAdd == null) {
			LOG.warn("null resultsElement");
		} else {
			for (ResultElement resultElement : resultsElementToAdd) {
				resultElement.detach();
				String xpath = new XPathGenerator(elementToSearch).getXPath();
				resultsElement.setXPath(xpath);
				resultsElement.appendChild(resultElement);
			}
		}
	}
	public AMIArgProcessor getArgProcessor() {
		return amiArgProcessor;
	}

	public String getTitle() {
		return dictionary == null ? null : dictionary.getTitle();
	}

	public void setNamedPattern(NamedPattern namedPattern) {
		this.namedPattern = namedPattern; // could be null
		this.pattern = namedPattern == null ? null : namedPattern.getPattern();
		this.name = namedPattern == null ? null : namedPattern.getName();
	}

	/**
	 * 
	 * iterates over lists of lists of possible trailing words looking for first possible match of all words in order
	 * currently no proximity matching, 
	 * 
	 * [[mental,health,study],[mental,health]] matches "mental health study" but 
	 * [[mental,health],[mental,health,study]] matches "mental health" but 
	 * 
	 * [[health,study]] matches "health study" but not "health and safety study"
	 * 
	 *  This should ultimately work with stemming and lowercasing
	 * 
	 * @param trailingListList List of Lists of trailing strings
	 * @param strings tokens to match
	 * @param pos index of firstword
	 * @return
	 */
	public int canFitTrailing(List<List<String>> trailingListList, List<String> strings, int pos) {
		for (List<String> trailingList : trailingListList) {
			LOG.trace("match: "+strings.get(pos));
			boolean matched = true;
			int offset;
			for (offset = 0; offset < trailingList.size(); offset++) {
				int stringPos = pos + 1 + offset;
				if (stringPos >= strings.size()) {
					matched = false;
				} else if (!matchIncludingTrailingPunctuation(strings.get(stringPos), trailingList.get(offset))) {
					matched = false;
				}
				if (!matched) {
					LOG.trace(">> "+strings.get(stringPos));
					break;
				}
			}
			if (matched) {
				return offset;
			}
		}
		return -1;
	}

	public List<String> createExactStringList(int pos, int offset) {
		List<String> exactStringList = new ArrayList<String>();
		for (int i = pos; i <= pos + offset; i++) {
			exactStringList.add(stringList.get(i));
		}
		return exactStringList;
	}

	public List<String> createPostStringList(int pos) {
		List<String> postStringList = new ArrayList<String>();
		for (int i = pos + 1; i < Math.min(stringList.size(), pos + getMaxPostWordCount()); i++) {
			postStringList.add(stringList.get(i));
		}
		return postStringList;
	}

	public List<String> createPreStringList(int pos) {
		List<String> preStringList = new ArrayList<String>();
		for (int i = Math.max(0, pos - getMaxPreWordCount()); i < pos; i++) {
			preStringList.add(stringList.get(i));
		}
		return preStringList;
	}

	public ResultElement createResultElement(List<String> strings, int pos, int offset) {
		this.stringList = strings;
		ResultElement resultElement = new ResultElement();
		resultElement.addAttribute(new Attribute(PRE, StringUtils.join(createPreStringList(pos).iterator(), " ")));
		resultElement.addAttribute(new Attribute(EXACT, StringUtils.join(createExactStringList(pos, offset).iterator(), " ")));
		resultElement.addAttribute(new Attribute(POST, StringUtils.join(createPostStringList(pos + offset).iterator(), " ")));
		return resultElement;
	}

	protected ResultElement createResultElement(String value, AbstractDictionary dictionary) {
		throw new RuntimeException("createResultElement(dictionary) NYI");
	}

	public AbstractDictionary getDictionary() {
		return dictionary;
	}

	public int getMaxPostWordCount() {
		return maxPostWordCount;
	}

	public int getMaxPreWordCount() {
		return maxPreWordCount;
	}

	public String getName() {
		return name;
	}

	protected Pattern getPattern() {
		return pattern;
	}

	/** flatten all tags.
	 * 
	 * @param xomElement
	 * @return
	 */
	public String getValue(Element xomElement) {
		return xomElement.getValue();
	}

	public boolean matchIncludingTrailingPunctuation(String raw, String term) {
		int difflength = raw.length() - term.length();
		if (difflength < 0 || difflength > 1) {
			return false;
		} else if (raw.equals(term)) {
			return true;
		}
		// ignore trailing punctuation
		if (difflength == 1 && raw.startsWith(term)) {
			char c = raw.charAt(raw.length() - 1);
			return c == ';' || c == ',' || c == '.' || c == '!' || c== '?';
		}
		return false;
	}

	public ResultsElement search(List<? extends Element> elements, ResultsElement resultsElement) {
		for (Element element : elements) {
			ResultsElement resultsElementToAdd = this.searchXomElement(element);
//			if (resultsElementToAdd.size() > 0) {
//				String xpath = new XPathGenerator(element).getXPath();
//				resultsElementToAdd.setXPath(xpath);
//				resultsElement.transferResultElements(resultsElementToAdd);
//			}
			addXpathAndAddtoResultsElement(element, resultsElement, resultsElementToAdd);
		}
		postProcessResultsElement(resultsElement);
		markFalsePositives(resultsElement, this.getOrCreateCurrentDictionary());
		return resultsElement;
	}

//	protected void searchAndUpdateResultsElement(List<? extends Element> elements, ResultsElement resultsElement) {
//		if (elements != null) {
//			for (Element element : elements) {
//				ResultsElement resultsElementToAdd = this.searchXomElement(element);
//				addXpathAndAddtoResultsElement(element, resultsElement, resultsElementToAdd);
//			}
//			postProcessResultsElement(resultsElement);
//			markFalsePositives(resultsElement);
//		}
//	}
	

//	private DefaultAMIDictionary getOrCreateCurrentDictionary() {
//		getArgProcessor().
//		
//		// TODO Auto-generated method stub
//		return null;
//	}

	private DefaultAMIDictionary getOrCreateCurrentDictionary() {
		return this.getArgProcessor().getOrCreateCurrentDictionary();
	}

	/** create resultsElement.
	 * 
	 * May be empty if no hits
	 * 
	 * @param xomElement
	 * @return
	 */
	public ResultsElement searchXomElement(Element xomElement) {
		String value = getValue(xomElement);
		ResultsElement resultsElement = search(value); // crude to start with
		return resultsElement;
	}


	public ResultsElement search(String value) {
		ResultsElement resultsElement = null;
		if (getDictionary() != null) {
			resultsElement = searchWithDictionary(value);
		} else if (getPattern() != null) {
			resultsElement = searchWithPattern(value);
		}
		return resultsElement;
	}

	public ResultsElement searchWithDictionary(List<String> strings) {
		ResultsElement resultsElement = new ResultsElement();
		for (int pos = 0; pos < strings.size(); pos++) {
			String firstword = strings.get(pos);
			List<List<String>> trailingListList = dictionary.getTrailingWords(firstword);
			if (trailingListList != null) {
				int trailingOffset = canFitTrailing(trailingListList, strings, pos);
				if (trailingOffset != -1) {
					ResultElement resultElement = createResultElement(strings, pos, trailingOffset);
					resultsElement.appendChild(resultElement);
				}
			}
		}
		return resultsElement;
	}

	private ResultsElement searchWithDictionary(String value) {
			ResultsElement resultsElement = new ResultsElement();
			WordCollectionFactory wordCollectionFactory = amiArgProcessor.ensureWordCollectionFactory();
			List<String> stringList = wordCollectionFactory.createWordList();
	//		createWordList
	//		makeStrings();
			resultsElement = searchWithDictionary(stringList);
//			if (true) {
//				LOG.warn("searchWithDictionary NYI");
//			} else {
//				for (String string : stringList) {
//					//CHECK THIS
//					ResultElement resultElement = createResultElement(value, dictionary);
//					resultsElement.appendChild(resultElement);
//				}
//			}
			return resultsElement;
		}

	private ResultsElement searchWithPattern(String value) {
		ResultsElement resultsElement = new ResultsElement();
		Matcher matcher = getPattern().matcher(value);
		int start = 0;
		while (matcher.find(start)) {
			ResultElement resultElement = createResultElement(value, matcher);
			resultsElement.appendChild(resultElement);
			start = matcher.end();
		}
		return resultsElement;
	}


	public void setDictionary(AbstractDictionary dictionary) {
		this.dictionary = dictionary;
	}

	/** sometimes overridden by subclasses with complex terms.
	 * 
	 * default is resultsElement.getMatch(), but subclasses may need more 
	 * processing
	 * 
	 * @param resultElement
	 * @return
	 */
	protected String getDictionaryTerm(ResultElement resultElement) {
		return resultElement.getMatch();
	}

	protected void markFalsePositives(ResultsElement resultsElement, DefaultAMIDictionary dictionary) {
		if (dictionary != null && resultsElement != null) {
			for (int i = resultsElement.size() - 1; i >= 0; i--) {
				ResultElement resultElement = resultsElement.get(i);
				if (resultElement != null) {
					String term = getDictionaryTerm(resultElement);
					if (!dictionary.contains(term)) {
						LOG.debug("marking potential false positive: "+resultElement.toXML());
						resultsElement.get(i).setDictionaryCheck(dictionary, false);
					}
				}
			}
		}
	}

	/** maybe overridden by specialist subclasses
	 * 
	 * this defaults to no-op
	 * 
	 * @param resultsElement
	 */
	protected void postProcessResultsElement(ResultsElement resultsElement) {
		// no-op
	}
	


}
