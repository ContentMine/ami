package org.xmlcml.ami2.dictionary;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.xmlcml.ami2.dictionary.DefaultAMIDictionary;
import org.xmlcml.ami2.dictionary.gene.HGNCDictionary;

public class HGNCTest {

	private static final Logger LOG = Logger.getLogger(HGNCTest.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	@Test
	public void testHGNCDictionary() {
		LOG.debug("start");
		DefaultAMIDictionary dictionary = new HGNCDictionary();
		LOG.debug("finish");
		Assert.assertTrue("A1BG-AS1", dictionary.contains("A1BG-AS1"));
		Assert.assertFalse("A1BG-AS1x", dictionary.contains("A1BG-AS1x"));
		Assert.assertTrue("BRCA2", dictionary.contains("BRCA2"));
	}

}
