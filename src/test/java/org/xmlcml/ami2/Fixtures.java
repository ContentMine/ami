package org.xmlcml.ami2;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.xmlcml.ami2.plugins.AMIPlugin;
import org.xmlcml.cmine.args.DefaultArgProcessor;
import org.xmlcml.cmine.files.CMDir;
import org.xmlcml.xml.XMLUtil;

public class Fixtures {

	
	private static final Logger LOG = Logger.getLogger(Fixtures.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	public final static File TEST_RESOURCES_DIR    = new File("src/test/resources/");
	public final static File TEST_AMI_DIR          = new File(Fixtures.TEST_RESOURCES_DIR, "org/xmlcml/ami2");
	
	public final static File TEST_BMC_DIR          = new File(Fixtures.TEST_AMI_DIR, "bmc");
	public final static File TEST_BMC_15_1_511_CMDIR = new File(Fixtures.TEST_BMC_DIR, "15_1_511");
	public final static File TEST_TRIALS_16_1_1 = new File(Fixtures.TEST_BMC_DIR, "http_www.trialsjournal.com_content_16_1_1");
	
	public final static File TEST_GRAPHCHEM_DIR    = new File(Fixtures.TEST_AMI_DIR, "graphchem");
	public final static File TEST_GRAPHCHEM_ASPERGILLUS    = new File(Fixtures.TEST_GRAPHCHEM_DIR, "aspergillus_9");
	
	public final static File TEST_MIXED_DIR        = new File(Fixtures.TEST_AMI_DIR, "mixed");

	public final static File TEST_PLOSONE_DIR      = new File(Fixtures.TEST_AMI_DIR, "plosone");
	public final static File TEST_PLOSONE_0115884  = new File(Fixtures.TEST_PLOSONE_DIR, "journal.pone.0115884");
	public final static File TEST_PLOSONE_SEQUENCE_0121780  = new File(Fixtures.TEST_PLOSONE_DIR, "plosjournal.pone.0121780_sequence");
	public final static File TEST_PLOSONE_MALARIA_0119475  = new File(Fixtures.TEST_PLOSONE_DIR, "journal.pone.0119475");

	public final static File TEST_IJSEM_DIR      = new File(Fixtures.TEST_AMI_DIR, "ijsem");
	public final static File TEST_IJSEM_0115884  = new File(Fixtures.TEST_IJSEM_DIR, "journal.pone.0115884");
	public final static File TEST_IJSEM_SEQUENCE_0121780  = new File(Fixtures.TEST_IJSEM_DIR, "plosjournal.pone.0121780_sequence");
	public final static File TEST_IJSEM_MALARIA_0119475  = new File(Fixtures.TEST_IJSEM_DIR, "journal.pone.0119475");

	public final static File TEST_PHYLO_DIR          = new File(Fixtures.TEST_AMI_DIR, "phylo");

	public static final File EXAMPLES              = new File("examples");
	public static final File EXAMPLES_TEMP_16_1_1  = new File("target/examples_16_1_1");

	public static final String RESULTS_XML = "results.xml";
	private static final String RESULTS_DIR = "results/";
	private static final String EXPECTED_DIR = "expected/";

	/** runs tests and compares expected and actual output.
	 * 
	 * @param cmDirectory contentMine directory
	 * @param temp directory (will create)
	 * @param plugin plugin to use
	 * @param pluginAndOptions directories for output (e.g. species/binomial/)
	 * @throws IOException
	 */
	public static void runStandardTestHarness(File cmDirectory, File temp, AMIPlugin plugin, String args, String ... pluginAndOptions)
			throws IOException {
		LOG.trace("++++++++++++++++++++++   harness   +++++++++++++++++++++++");
		LOG.trace("temp exists: "+temp+"; e: "+temp.exists()+"; d "+temp.isDirectory());
		CMDir cmDir = new CMDir(cmDirectory);
		FileUtils.deleteDirectory(temp);
		cmDir.copyTo(temp, true);
		
		Assert.assertFalse("exists? "+RESULTS_XML, cmDir.hasResultsDir());
		DefaultArgProcessor argProcessor = (DefaultArgProcessor) plugin.getArgProcessor();
		argProcessor.parseArgs(args);
		argProcessor.runAndOutput();
		List<File> files = new ArrayList<File>(FileUtils.listFiles(temp, null, true));
		LOG.trace("FILES after: "+files);
		LOG.trace("==========================="+argProcessor+"=============================");
		LOG.trace("results exists? "+new File(temp,"results").exists());
		
		for (String pluginAndOption : pluginAndOptions) {
			Fixtures.compareExpectedAndResults(cmDir.getDirectory(), temp, pluginAndOption, RESULTS_XML);
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
	
}
