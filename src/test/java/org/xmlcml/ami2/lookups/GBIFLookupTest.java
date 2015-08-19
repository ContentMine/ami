package org.xmlcml.ami2.lookups;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;
import org.xmlcml.cmine.lookup.AbstractLookup;

public class GBIFLookupTest {

	
	private static final String CANONICAL_NAME_PATH = "$.results[0].canonicalName";
	public static final Logger LOG = Logger.getLogger(GBIFLookupTest.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	@Test
//	@Ignore // LOOKUP // comment out if running offline or on Jenkins
	public void testLookupCommonName() throws Exception {
		AbstractLookup gbifLookup = new GBIFLookup();
		String json = gbifLookup.lookup("Blue+whale");
		LOG.trace("result: "+json);
		new File("target/gbif/").mkdirs();
		IOUtils.write(json, new FileOutputStream("target/gbif/whale.json"));
		String canonicalName = gbifLookup.getStringForJsonPath(json, CANONICAL_NAME_PATH);
		// this test seems unstable - sometimes the subspecies is given
		Assert.assertTrue("name", canonicalName.startsWith("Balaenoptera musculus"));
	}
	

}
