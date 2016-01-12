package org.xmlcml.ami2.plugins.gene;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.Ignore;
import org.junit.Test;
import org.xmlcml.ami2.AMIFixtures;
import org.xmlcml.ami2.plugins.species.SpeciesArgProcessor;
import org.xmlcml.cmine.args.DefaultArgProcessor;

import nu.xom.Builder;
import nu.xom.Element;

public class GeneArgProcessorTest {

	
	
	private static final Logger LOG = Logger.getLogger(GeneArgProcessorTest.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	@Test
	public void testGeneHarness() throws Exception {
		// SHOWCASE
		String cmd = "--g.gene --context 35 50 --g.type human -q target/plosone/gene/ -i scholarly.html"; 
 
		AMIFixtures.runStandardTestHarness(
			new File("./src/test/resources/org/xmlcml/ami2/plosone/journal.pone.0008887/"), 
			new File("target/plosone/gene/"), 
			new GenePlugin(),
			cmd,
			"gene/human/");
	}

	@Test
	@Ignore // too large and requires word processor
	public void testGeneDictionary() {
		File large = new File("../patents/US08979");
		if (!large.exists()) return; // only on PMR machine
//		WordTest.runNorma(large);
		String args = "-i scholarly.html --g.gene "
				+ "--c.dictionary /org/xmlcml/ami2/plugins/gene/hgnc/hgnc_complete_set.xml"
				+ " --project "+large; 
		GeneArgProcessor argProcessor = new GeneArgProcessor(args);
		argProcessor.runAndOutput();
		AMIFixtures.checkResultsElementList(argProcessor, 1, 0, 
				"<results title=\"MEND ME\">"
				);

	}
	


	
}
