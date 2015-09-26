package org.xmlcml.ami2.plugins.identifiers;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;
import org.xmlcml.ami2.AMIFixtures;
import org.xmlcml.ami2.lookups.RRIDLookup;
import org.xmlcml.ami2.plugins.identifier.IdentifierArgProcessor;
import org.xmlcml.cmine.args.DefaultArgProcessor;
import org.xmlcml.cmine.files.CMDir;
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

	@Test
	// SHOWCASE
	public void testAmiIdentifier() throws IOException {

	    File neuro4415html = new File(AMIFixtures.TEST_RRID_DIR, "JNEUROSCI.4415-13.2014.html");
	    File q4415 = new File(AMIFixtures.TEST_RRID_DIR, "q4415/");
	    q4415.mkdirs();
	    FileUtils.copyFile(neuro4415html, new File(q4415, "fulltext.html"));
	    String cmd = "norma -q " + q4415 + " --input fulltext.html --html htmlunit -o scholarly.html";
	    new NormaArgProcessor(cmd).runAndOutput();
	    File rridDir = new File("target/rrid/");
	    if (rridDir.exists()) rridDir.delete();
	    rridDir.mkdirs();
	    FileUtils.copyDirectory(AMIFixtures.TEST_RRID_DIR, rridDir);
	    cmd = "--id.identifier --context 35 50 --id.regex  regex/identifiers.xml --id.type rrid.ab -q "+rridDir+" -i scholarly.html"; 
	    new IdentifierArgProcessor(cmd).runAndOutput();
	    Assert.assertTrue("exists", rridDir.exists());
	    File resultsXml = new File(rridDir, "q4415/results/identifier/rrid.ab/results.xml");
	    Assert.assertTrue("results", resultsXml.exists());
	}
}
