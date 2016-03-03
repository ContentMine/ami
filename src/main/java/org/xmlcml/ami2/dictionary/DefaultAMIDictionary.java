package org.xmlcml.ami2.dictionary;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.cmine.lookup.DefaultStringDictionary;
import org.xmlcml.xml.XMLUtil;

import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnel;
import com.google.common.hash.PrimitiveSink;

import nu.xom.Attribute;
import nu.xom.Element;
import nu.xom.Elements;

/** a simple collection of ids (terms) with additional names if known.
 * 
 * This may expand later depending on what resources we find.
 * 
 * Dictionaries are collections of terms that can be used for annotation or looking up
 * terms. At present they are simply:
 *     {term, (optional)Name}
 * They are originally developed in a scientifc context and a typical pair is
 *     {"ABI3BP", "ABI family member 3 binding protein"}
 *     
 * The dictionary is stored as XML:
 *  <dictionary title="hgnc">
      <entry term="A1BG" name="alpha-1-B glycoprotein"/>
      <entry term="A1BG-AS1" name="A1BG antisense RNA 1"/>
      ...
      
 * it is assumed all terms are distinct. Case matters.
 * 
 * Initially the dictionaries are formed from JSON, TSV,  text files etc., but normallised to 
 * this XML
 * 
 * uses a BloomFilter to check whether the Dictionary does NOT contain a term and then looks up
 * in Map<term, name>
 *     
 * @author pm286
 *
 */
public class DefaultAMIDictionary extends DefaultStringDictionary {

	private static final Logger LOG = Logger.getLogger(DefaultAMIDictionary.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	private static final String DICTIONARY = "dictionary";
	private static final String ENTRY = "entry";
	private static final String NAME = "name";
	private static final String REGEX = "regex";
	private static final String TERM = "term";
	public static final String TITLE = "title";
	private static final String URL = "url";
	private static final String WIKIDATA = "wikidata";

	/** later these should be read in from args.xml ...
	 * 
	 */
	protected static final File AMI_DIR        = new File("src/main/resources/org/xmlcml/ami2/plugins/");
	protected static final File DICTIONARY_DIR = new File(AMI_DIR, "dictionary");
	protected static final File GENE_DIR       = new File(AMI_DIR, "gene");
	protected static final File PLACES_DIR     = new File(AMI_DIR, "places");
	protected static final File SPECIES_DIR    = new File(AMI_DIR, "species");
	protected static final File SYNBIO_DIR     = new File(AMI_DIR, "synbio");
	protected static final String UTF_8        = "UTF-8";
	
	protected Map<DictionaryTerm, String> namesByTerm;
	protected InputStream inputStream;
	protected Element dictionaryElement;
	private Funnel<String> stringFunnel;
	private BloomFilter<String> bloomFilter;
	private String dictionarySource;
	private List<DictionaryTerm> dictionaryTermList; 
	private List<DictionaryTerm> stemmedTermList;
	private Set<String> rawTermSet; 
	
	public DefaultAMIDictionary() {
		init();
	}
	
	private void init() {
		stringFunnel = new Funnel<String>() {
			public void funnel(String person, PrimitiveSink into) {
				into.putUnencodedChars(person);
			}
		};
	}
	
	public boolean contains(String string) {
		if (string == null) { 
			return false;
		} else if (!mightContain(string)) {
			return false;
		} else {
//			Set<DictionaryTerm> termSet = namesByTerm.keySet();
			return (rawTermSet == null) ? false : rawTermSet.contains(string);
		}
	}

	private boolean mightContain(String string) {
		return bloomFilter == null ? true : bloomFilter.mightContain(string);
	}

	public int size() {
		return namesByTerm == null ? -1 : namesByTerm.size();
	}
	
	public Map<DictionaryTerm, String> getNamesByTerm() {
		return namesByTerm;
	}

//	@Override
//	protected void createFromInputStream(String dictionarySource, InputStream is) throws IOException {
//		inputStream = is;
//		this.dictionarySource = dictionarySource;
//	}

	@Override
	public List<List<String>> getTrailingWords(String key) {
		throw new RuntimeException("NYI");
	}

	protected Element createDictionaryElementFromHashMap(String title) {
		dictionaryElement = new Element(DICTIONARY);
		dictionaryElement.addAttribute(new Attribute(TITLE, title));
		
		List<DictionaryTerm> dictionaryTerms = Arrays.asList(namesByTerm.keySet().toArray(new DictionaryTerm[0]));
		Collections.sort(dictionaryTerms);
		for (DictionaryTerm dictionaryTerm : dictionaryTerms) {
			Element entry = new Element(ENTRY);
			String term = dictionaryTerm.getTermPhrase().getString();
			entry.addAttribute(new Attribute(TERM, term));
			entry.addAttribute(new Attribute(NAME, namesByTerm.get(dictionaryTerm)));
			dictionaryElement.appendChild(entry);
		}
		return dictionaryElement;
	}

	protected void writeXMLFile(File file) {
		try {
			XMLUtil.debug(dictionaryElement, file, 2);
		} catch (IOException e) {
			throw new RuntimeException("Cannot write dictionary file: "+file, e);
		}
	}

	/** stores entries in hashMap (term, name) and also creates BloomFilter.
	 * 
	 * @param file
	 * @throws FileNotFoundException 
	 */
	protected void readDictionary(File file)  {
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(file);
		} catch (FileNotFoundException e) {
			throw new RuntimeException("Cannot read dictionary file "+file);
		}
		readDictionary(fis);
		return;
	}

	protected void readDictionary(InputStream is) {
		dictionaryElement = XMLUtil.parseQuietlyToDocument(is).getRootElement();
		namesByTerm = new HashMap<DictionaryTerm, String>();
		rawTermSet = new HashSet<String>();
		dictionaryTermList = new ArrayList<DictionaryTerm>();
		Elements elements = dictionaryElement.getChildElements();
		bloomFilter = BloomFilter.create(stringFunnel, elements.size());
		for (int i = 0; i < elements.size(); i++) {
			Element element = elements.get(i);
			String term = element.getAttributeValue(TERM);
			DictionaryTerm dictionaryTerm = new DictionaryTerm(term);
			String name = element.getAttributeValue(NAME);
			dictionaryTerm.setName(name);
			String url = element.getAttributeValue(URL);
			dictionaryTerm.setURL(url);
			String wikidata = element.getAttributeValue(WIKIDATA);
			dictionaryTerm.setWikidata(wikidata);
			dictionaryTermList.add(dictionaryTerm);
			namesByTerm.put(dictionaryTerm, name);
			bloomFilter.put(term);
			rawTermSet.add(term);
		}
	}

	public List<DictionaryTerm> getDictionaryTermList() {
		return dictionaryTermList;
	}

	public List<DictionaryTerm> getTermsSortedBySize() {
		getDictionaryTermList();
		getMaximumTermSize();
		List<DictionaryTerm> terms = new ArrayList<DictionaryTerm>();
		if (namesByTerm != null) {
			terms = new ArrayList<DictionaryTerm>(Arrays.asList(namesByTerm.keySet().toArray(new DictionaryTerm[0])));
			Collections.sort(terms);
		}
		return terms;
	}

	public List<TermPhrase> getTermPhraseList() {
		List<TermPhrase> termPhraseList = new ArrayList<TermPhrase>();
	getDictionaryTermList();
		Iterator<DictionaryTerm> termIterator = namesByTerm.keySet().iterator();
		while (termIterator.hasNext()) {
			termPhraseList.add(termIterator.next().getTermPhrase());
		}
		return termPhraseList;
	}
	
	public int getMaximumTermSize() {
		getDictionaryTermList();
		int maxLength = 0;
		for (DictionaryTerm dictionaryTerm : dictionaryTermList) {
			if (dictionaryTerm.size() > maxLength) {
				maxLength = dictionaryTerm.size();
			}
		}
		return maxLength;
	}

	public void sortAlphabetically() {
		ensureDictionaryTerms();
		Collections.sort(dictionaryTermList);
	}

	private void ensureDictionaryTerms() {
		if (dictionaryTermList == null) {
			dictionaryTermList = new ArrayList<DictionaryTerm>();
		}
	}
	
	public List<DictionaryTerm> getStemmedList() {
		if (stemmedTermList == null) {
			if (dictionaryTermList != null) {
				stemmedTermList = new ArrayList<DictionaryTerm>();
				for (DictionaryTerm dictionaryTerm : dictionaryTermList) {
					
				}
			}
		}
		return stemmedTermList;
	}

	public static DefaultAMIDictionary createSortedDictionary(File dictionaryFile) {
		DefaultAMIDictionary dictionary = new DefaultAMIDictionary();
		dictionary.readDictionary(dictionaryFile);
		dictionary.sortAlphabetically();
		return dictionary;
	}
	
	public String toString() {
		String s = "";
		s += "dictionarySource: "+dictionarySource;
		s += "; terms: "+getTermsSortedBySize().size();
		s += "; title:  "+getTitle();
		return s;

	}
	
	public String getURL() {
		return dictionaryElement == null ? null : dictionaryElement.getAttributeValue(DefaultAMIDictionary.URL);
		
	}
	
	public String getRegexString() {
		return dictionaryElement == null ? null : dictionaryElement.getAttributeValue(DefaultAMIDictionary.REGEX);
		
	}
	
	public List<DictionaryTerm> checkNonMatchingTerms() {
		String regexString = getRegexString();
		List<DictionaryTerm> nonMatchingTermList = new ArrayList<DictionaryTerm>();
		if (regexString == null) {
			LOG.warn("cannot find regex");
		} else {
			Pattern pattern = Pattern.compile(regexString);
			if (dictionaryTermList != null) {
				for (DictionaryTerm term : dictionaryTermList) {
					String s =  term.getTermPhrase().getString();
					Matcher matcher = pattern.matcher(s);
					if (!matcher.matches()) {
						nonMatchingTermList.add(term);
					}
				}
			}
		}		
		return nonMatchingTermList;
	}
	
	public String getTitle() {
		return dictionaryElement == null ? null : dictionaryElement.getAttributeValue(DefaultAMIDictionary.TITLE);
	}

}
