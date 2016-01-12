package org.xmlcml.ami2.lookups;

import org.junit.Assert;
import org.junit.Test;
import org.xmlcml.ami2.lookups.HGNCDictionary;

public class HGNCTest {
	
	@Test
	public void testHGNCDictionary() {
		HGNCDictionary dictionary = new HGNCDictionary();
		Assert.assertTrue("A1BG-AS1", dictionary.contains("A1BG-AS1"));
		Assert.assertFalse("A1BG-AS1x", dictionary.contains("A1BG-AS1x"));
		Assert.assertTrue("BRCA2", dictionary.contains("BRCA2"));
	}

}
