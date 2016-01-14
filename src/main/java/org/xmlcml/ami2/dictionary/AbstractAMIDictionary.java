package org.xmlcml.ami2.dictionary;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.cmine.lookup.AbstractDictionary;
import org.xmlcml.xml.XMLUtil;

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
 *     
 * @author pm286
 *
 */
public class AbstractAMIDictionary extends AbstractDictionary {

	private static final Logger LOG = Logger.getLogger(AbstractAMIDictionary.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	private static final String ENTRY = "entry";
	private static final String DICTIONARY = "dictionary";
	private static final String TITLE = "title";
	private static final String NAME = "name";
	private static final String TERM = "term";
	
	protected static final File GENE_DIR = new File("src/main/resources/org/xmlcml/ami2/plugins/gene");
	
	protected Map<String, String> namesByTerm;
	protected InputStream inputStream;
	protected Element dictionary;

	@Override
	public boolean contains(String string) {
		return namesByTerm.keySet().contains(string);
	}

	public int size() {
		return namesByTerm == null ? -1 : namesByTerm.size();
	}
	public Map<String, String> getNamesByTerm() {
		return namesByTerm;
	}

	@Override
	protected void readFile(String dictionarySource, InputStream is) throws IOException {
		inputStream = is;
		// dictionarySource not used
	}

	@Override
	public List<List<String>> getTrailingWords(String key) {
		throw new RuntimeException("cannot use for genes");
	}

	protected Element createDictionaryElement(String title) {
		dictionary = new Element(DICTIONARY);
		dictionary.addAttribute(new Attribute(TITLE, title));
		
		List<String> terms = Arrays.asList(namesByTerm.keySet().toArray(new String[0]));
		Collections.sort(terms);
		for (String term : terms) {
			Element entry = new Element(ENTRY);
			entry.addAttribute(new Attribute(TERM, term));
			entry.addAttribute(new Attribute(NAME, namesByTerm.get(term)));
			dictionary.appendChild(entry);
		}
		return dictionary;
	}

	protected void writeXMLFile(File file) {
		try {
			XMLUtil.debug(dictionary, file, 2);
		} catch (IOException e) {
			throw new RuntimeException("Cannot write dictionary file: "+file, e);
		}
	}

	protected void readDictionary(File file) {
		dictionary = XMLUtil.parseQuietlyToDocument(file).getRootElement();
		namesByTerm = new HashMap<String, String>();
		Elements elements = dictionary.getChildElements();
		for (int i = 0; i < elements.size(); i++) {
			namesByTerm.put(elements.get(i).getAttributeValue(TERM), elements.get(i).getAttributeValue(NAME));
		}
	}

}
