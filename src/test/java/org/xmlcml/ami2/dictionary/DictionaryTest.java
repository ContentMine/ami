package org.xmlcml.ami2.dictionary;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;
import org.xmlcml.ami2.dictionary.gene.HGNCDictionary;

public class DictionaryTest {

	private static final Logger LOG = Logger.getLogger(DictionaryTest.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	@Test
	public void testCheckPattern() {
		DefaultAMIDictionary dictionary = new HGNCDictionary();
		Assert.assertNotNull(dictionary.getRegexString());
		dictionary.checkNonMatchingTerms();
	}
}
