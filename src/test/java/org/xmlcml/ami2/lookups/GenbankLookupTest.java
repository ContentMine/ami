package org.xmlcml.ami2.lookups;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.Test;

public class GenbankLookupTest {

	
	private static final Logger LOG = Logger.getLogger(GenbankLookupTest.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	@Test
//	@Ignore // LOOKUP 
	public void getGenbankForId() throws Exception {
		GenbankLookup genbankLookup = new GenbankLookup();
		String result = genbankLookup.lookupTaxonomy("Mus");
		LOG.trace("result: "+result);
//		Assert.assertEquals("mouse", "(83310)", intArray.toString());
	}
	

}
