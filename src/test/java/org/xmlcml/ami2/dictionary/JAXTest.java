package org.xmlcml.ami2.dictionary;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;
import org.xmlcml.ami2.dictionary.gene.JAXDictionary;

public class JAXTest {

	private static final Logger LOG = Logger.getLogger(JAXTest.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	@Test
	public void testJAXDictionary() {
		AbstractAMIDictionary dictionary = new JAXDictionary();
		Assert.assertEquals(59844,  dictionary.size());
		Assert.assertTrue("Brca2", dictionary.contains("Brca2"));
	}

}
