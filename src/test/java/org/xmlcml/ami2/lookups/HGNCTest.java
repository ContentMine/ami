package org.xmlcml.ami2.lookups;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

public class HGNCTest {
	
	@Test
	@Ignore // too much heap space...
	public void testHGNCDictionary() {
		HGNCDictionary dictionary = new HGNCDictionary();
		Assert.assertTrue("A1BG-AS1", dictionary.contains("A1BG-AS1"));
		Assert.assertFalse("A1BG-AS1x", dictionary.contains("A1BG-AS1x"));
		Assert.assertTrue("BRCA2", dictionary.contains("BRCA2"));
	}

}
