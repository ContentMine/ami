package org.xmlcml.ami2.dictionary;

import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;
import org.xmlcml.ami2.dictionary.gene.JAXDictionary;
import org.xmlcml.ami2.dictionary.species.TaxDumpGenusDictionary;

public class TaxDumpTest {

	private static final Logger LOG = Logger.getLogger(TaxDumpTest.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	@Test
	public void testTaxDumpDictionary() {
		DefaultAMIDictionary dictionary = new TaxDumpGenusDictionary();
		Assert.assertEquals(91556,  dictionary.size());
		Assert.assertTrue("Bacillus", dictionary.contains("Bacillus"));
	}

	@Test
	public void testCheckPattern() {
		DefaultAMIDictionary dictionary = new TaxDumpGenusDictionary();
		Assert.assertNotNull("missing regex", dictionary.getRegexString());
		List<DictionaryTerm> nonMatchingTerms = dictionary.checkNonMatchingTerms();
		Assert.assertEquals("non matching terms: ", 0, nonMatchingTerms.size());
	}


}
