package org.xmlcml.ami2;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.xmlcml.ami2.plugins.AMIArgProcessor;
import org.xmlcml.ami2.plugins.AMIPlugin;
import org.xmlcml.ami2.plugins.CommandProcessor;
import org.xmlcml.cmine.args.DefaultArgProcessor;
import org.xmlcml.cmine.files.CTree;
import org.xmlcml.cmine.files.ContentProcessor;
import org.xmlcml.cmine.files.ResultsElementList;
import org.xmlcml.cmine.util.CMineTestFixtures;
import org.xmlcml.xml.XMLUtil;

public class AMIFixtures {

	
	private static final Logger LOG = Logger.getLogger(AMIFixtures.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	public final static File TEST_RESOURCES_DIR    = new File("src/test/resources/");
	public final static File TEST_AMI_DIR          = new File(AMIFixtures.TEST_RESOURCES_DIR, "org/xmlcml/ami2");
	public final static String TEST_AMI_RESOURCES    = "/org/xmlcml/ami2";
	
	public final static File TEST_BMC_DIR          = new File(AMIFixtures.TEST_AMI_DIR, "bmc");
	public final static File TEST_BMC_15_1_511_CMDIR = new File(AMIFixtures.TEST_BMC_DIR, "15_1_511");
	public final static File TEST_TRIALS_16_1_1 = new File(AMIFixtures.TEST_BMC_DIR, "http_www.trialsjournal.com_content_16_1_1");
	
	public final static File TEST_DICTIONARY_DIR    = new File(AMIFixtures.TEST_AMI_DIR, "dictionary");
	
	public final static File TEST_GRAPHCHEM_DIR    = new File(AMIFixtures.TEST_AMI_DIR, "graphchem");
	public final static File TEST_GRAPHCHEM_ASPERGILLUS    = new File(AMIFixtures.TEST_GRAPHCHEM_DIR, "aspergillus_9");
	
	public final static File TEST_MIXED_DIR        = new File(AMIFixtures.TEST_AMI_DIR, "mixed");

	public final static File TEST_PATENTS_DIR      = new File(AMIFixtures.TEST_AMI_DIR, "patents");

	public final static File TEST_PLOSONE_DIR      = new File(AMIFixtures.TEST_AMI_DIR, "plosone");
	public final static File TEST_PLOSONE_0115884  = new File(AMIFixtures.TEST_PLOSONE_DIR, "journal.pone.0115884");
	public final static File TEST_PLOSONE_SEQUENCE_0121780  = new File(AMIFixtures.TEST_PLOSONE_DIR, "plosjournal.pone.0121780_sequence");
	public final static File TEST_PLOSONE_MALARIA_0119475  = new File(AMIFixtures.TEST_PLOSONE_DIR, "journal.pone.0119475");

	public final static File TEST_IJSEM_DIR      = new File(AMIFixtures.TEST_AMI_DIR, "ijsem");
	public final static File TEST_IJSEM_0115884  = new File(AMIFixtures.TEST_IJSEM_DIR, "journal.pone.0115884");
	public final static File TEST_IJSEM_SEQUENCE_0121780  = new File(AMIFixtures.TEST_IJSEM_DIR, "plosjournal.pone.0121780_sequence");
	public final static File TEST_IJSEM_MALARIA_0119475  = new File(AMIFixtures.TEST_IJSEM_DIR, "journal.pone.0119475");

	public final static File TEST_PHYLO_DIR          = new File(AMIFixtures.TEST_AMI_DIR, "phylo");
	public final static File TEST_RESULTS_DIR        = new File(AMIFixtures.TEST_AMI_DIR, "results/");
	public final static File TEST_RRID_DIR           = new File(AMIFixtures.TEST_AMI_DIR, "rrid/");

	public final static File TEST_TUTORIAL_DIR       = new File(AMIFixtures.TEST_AMI_DIR, "tutorial/");

	public static final File TEST_WORD_DIR           = new File(TEST_AMI_DIR, "word");
	public static final File TEST_WORD_EXAMPLES      = new File(TEST_WORD_DIR, "examples");
	public static final File TARGET_EXAMPLES_TEMP_16_1_1  = new File("target/examples_16_1_1");

	public static final String RESULTS_XML = "results.xml";
	private static final String RESULTS_DIR = "results/";
	private static final String EXPECTED_DIR = "expected/";
	private static final String TARGET_TEST = "target/test/";


	/** runs tests and compares expected and actual output.
	 * 
	 * @param cTreeDirectory contentMine directory
	 * @param newDir directory (will create)
	 * @param plugin plugin to use
	 * @param pluginAndOptions directories for output (e.g. species/binomial/)
	 * @throws IOException
	 */
	public static void runStandardTestHarness(File cTreeDirectory, File newDir, AMIPlugin plugin, String args, String ... pluginAndOptions)
			throws IOException {
		LOG.trace("++++++++++++++++++++++   harness   +++++++++++++++++++++++");
		LOG.trace("newDir exists: "+newDir+"; e: "+newDir.exists()+"; d "+newDir.isDirectory());
		CTree cTree = new CTree(cTreeDirectory);
		if (newDir.exists()) FileUtils.deleteDirectory(newDir);
		cTree.copyTo(newDir, true);
		
		Assert.assertFalse("exists? "+RESULTS_XML, cTree.hasResultsDir());
		DefaultArgProcessor argProcessor = (DefaultArgProcessor) plugin.getArgProcessor();
		argProcessor.parseArgs(args);
		argProcessor.runAndOutput();
		List<File> files = new ArrayList<File>(FileUtils.listFiles(newDir, null, true));
		LOG.trace("FILES after: "+files);
		LOG.trace("==========================="+argProcessor+"=============================");
		LOG.trace("results exists? "+new File(newDir,"results").exists());
		
		for (String pluginAndOption : pluginAndOptions) {
			AMIFixtures.compareExpectedAndResults(cTree.getDirectory(), newDir, pluginAndOption, RESULTS_XML);
		}
	}

	/** compares results.xml files in expected and actual directories.
	 * 
	 * @param expectedCM cmDirectory (must contain expected/)
	 * @param resultsCM cmDiecrory (must contain results/)
	 * @param pluginAndOption e.g. "species/binomial/"
	 * @throws IOException
	 */
	public static void compareExpectedAndResults(File expectedCM, File resultsCM, String pluginAndOption, String testFilename) throws IOException {
		
		File expectedFile = new File(new File(new File(expectedCM, EXPECTED_DIR), pluginAndOption), testFilename);
		Assert.assertTrue("expected file should exist ("+pluginAndOption+"): "+expectedFile, expectedFile.exists());
		File resultsFile = new File(new File(new File(resultsCM, RESULTS_DIR), pluginAndOption), testFilename);
		Assert.assertTrue("results file should exist ("+pluginAndOption+"): "+resultsFile, resultsFile.exists());
		String msg = XMLUtil.equalsCanonically(
	    		expectedFile, 
	    		resultsFile,
	    		true);
		if (msg != null) {
			LOG.debug(""+expectedFile+"; "+ FileUtils.readFileToString(expectedFile));
			LOG.debug(""+resultsFile+"; "+FileUtils.readFileToString(resultsFile));
		}
	    Assert.assertNull("message: "+msg, msg);
	}
	
	

	// utility method to check first part of resultsElementList
	
	public static void checkResultsElementList(AMIArgProcessor argProcessor, int size, int elem, String start) {
		CTree currentTree = argProcessor.getCurrentCTree();
		if (currentTree == null) {
			LOG.warn("Null CTree");
			return;
		}
		ContentProcessor contentProcessor = argProcessor.getOrCreateContentProcessor();
		ResultsElementList reList = contentProcessor.getOrCreateResultsElementList();
		reList.sortByTitle();
		Assert.assertEquals(size, reList.size());
		if (elem < size) {
			String results = reList.get(elem).toXML();
			if (!results.startsWith(start)) {
				String ss = results.substring(0,  Math.min(300,  results.length()));
				// replace " apos by \"
				String sss = ss.replaceAll("\"", "\\\\\\\"");
				LOG.debug("start (escaped) \n"+sss);
				Assert.fail("results assertion failure: starts with: "+ss);
			}
		}
	}

	public static CommandProcessor createDefaultDirectoriesAndProcessor(String projectName) {
		File rawDir = new File(AMIFixtures.TEST_AMI_DIR, projectName);
		File projectDir = new File(AMIFixtures.TARGET_TEST, projectName);
		CMineTestFixtures.cleanAndCopyDir(rawDir, projectDir);
		CommandProcessor commandProcessor = new CommandProcessor(projectDir);
		LOG.debug("wrote clean copy: "+projectDir);
		return commandProcessor;
	}

	

	
}
