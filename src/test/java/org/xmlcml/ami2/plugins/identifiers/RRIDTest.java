package org.xmlcml.ami2.plugins.identifiers;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.Test;
import org.xmlcml.ami2.AMIFixtures;
import org.xmlcml.ami2.lookups.GBIFLookupTest;
import org.xmlcml.ami2.lookups.RRIDLookup;
import org.xmlcml.ami2.plugins.identifier.IdentifierArgProcessor;
import org.xmlcml.cmine.args.DefaultArgProcessor;
import org.xmlcml.cmine.lookup.AbstractLookup;
import org.xmlcml.norma.NormaArgProcessor;

public class RRIDTest {

	public static final Logger LOG = Logger.getLogger(RRIDTest.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	@Test
	public void testAny() throws IOException {
		AbstractLookup rridLookup = new RRIDLookup();
		String response = rridLookup.lookup("AB_570435");
		FileUtils.write(new File("target/rrid/ab570435.html"), response);
	}
	
	@Test
	public void testAnyXML() throws IOException {
		AbstractLookup rridLookup = new RRIDLookup();
		rridLookup.setOutputFormat(".xml");
		String response = rridLookup.lookup("AB_570435");
		FileUtils.write(new File("target/rrid/ab570435.xml"), response);
	}
	
}
