package org.xmlcml.ami2.plugins.gene;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.Ignore;
import org.junit.Test;
import org.xmlcml.ami2.AMIFixtures;
import org.xmlcml.ami2.plugins.AMIArgProcessor;
import org.xmlcml.norma.util.NormaTestFixtures;

public class GeneArgProcessorTest {

	
	
	private static final Logger LOG = Logger.getLogger(GeneArgProcessorTest.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	static File GENE_DIR = new File(AMIFixtures.TEST_AMI_DIR, "gene/");

	
	@Test
	@Ignore // copy output files
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
	public void testGenePlos() throws Exception {
		File targetDir = new File("target/plosone/gene1/");
		NormaTestFixtures.cleanAndCopyDir(new File(AMIFixtures.TEST_PLOSONE_DIR, "journal.pone.0008887"), targetDir);
		String cmd = "--g.gene --context 35 50 --g.type human -q "+targetDir+" -i scholarly.html"; 
 
		GeneArgProcessor argProcessor = new GeneArgProcessor();
		argProcessor.parseArgs(cmd);
		argProcessor.runAndOutput();
		AMIFixtures.checkResultsElementList(argProcessor, 1, 0, 
				"<results title=\"human\"><result pre=\"the hepatocyte nuclear factor 4α ( \" exact=\"HNF4A\" post=\") gene, a well-known diabetes candidate gene not p\" xpath=\"/*[local-name()='html'][1]/*[local-name()='body'][1]/*[local-name()='div'][1]/*[local-name()='div'][7]/*[local-name()='p'][4]\""
				);
	}

	@Test
	public void testGeneDictionary() {
		File large = new File(AMIFixtures.TEST_PATENTS_DIR, "US08979");
		NormaTestFixtures.runNorma(large, "project", "uspto2html"); // writes to test dir
		String args = "-i scholarly.html --g.gene "
				+ "--c.dictionary /org/xmlcml/ami2/plugins/gene/hgnc/hgnc.xml"
				+ " --project "+large; 
		GeneArgProcessor argProcessor = new GeneArgProcessor(args);
		argProcessor.runAndOutput();
		AMIFixtures.checkResultsElementList(argProcessor, 1, 0, 
				"<results title=\"hgnc\" />"
				);

	}
	
	@Test
	@Ignore // too large and requires word processor
	public void testGeneDictionaryLarge() {
		File large = new File("../patents/US08979");
		if (!large.exists()) return; // only on PMR machine
//		WordTest.runNorma(large);
		String args = "-i scholarly.html --g.gene "
				+ "--c.dictionary /org/xmlcml/ami2/plugins/gene/hgnc/hgnc.xml"
				+ " --project "+large; 
		GeneArgProcessor argProcessor = new GeneArgProcessor(args);
		argProcessor.runAndOutput();
		AMIFixtures.checkResultsElementList(argProcessor, 1, 0, 
				"<results title=\"hgnc\">"
				);

	}
	
	/** this works when run singly.
	 * 
	 * suspect the test requires setup()
	 * 
	 * @throws Exception
	 */
	@Test
	public void testSimpleGeneArgProcessor() throws Exception {
		File newDir = new File("target/gene/simple/");
		FileUtils.copyDirectory(new File(GENE_DIR, "simple"), newDir);
		String args = "--g.gene --context 35 50 --g.type human -q "+newDir+" -i scholarly.html"; 
		AMIArgProcessor argProcessor = new GeneArgProcessor(args);
		argProcessor.runAndOutput();
		AMIFixtures.checkResultsElementList(argProcessor, 1, 0, 
				"<results title=\"human\"><result pre=\"This is \" exact=\"BRCA1\" post=\" and BRCA2, not FOOBAR.\" xpath=\"/*[local-name()='html'][1]/*[local-name()='body'][1]/*[local-name()='p'][2]\""
				);
	}




	
}
