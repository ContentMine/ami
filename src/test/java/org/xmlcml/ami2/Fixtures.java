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
import org.xmlcml.files.QuickscrapeNorma;
import org.xmlcml.xml.XMLUtil;

public class Fixtures {

	
	private static final Logger LOG = Logger.getLogger(Fixtures.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	public final static File TEST_RESOURCES_DIR    = new File("src/test/resources/");
	public final static File TEST_AMI_DIR          = new File(Fixtures.TEST_RESOURCES_DIR, "org/xmlcml/ami2");
	
	public final static File TEST_BMC_DIR          = new File(Fixtures.TEST_AMI_DIR, "bmc");
	public final static File TEST_BMC_15_1_511_QSN = new File(Fixtures.TEST_BMC_DIR, "15_1_511");
	public final static File TEST_TRIALS_16_1_1 = new File(Fixtures.TEST_BMC_DIR, "http_www.trialsjournal.com_content_16_1_1");
	
	public final static File TEST_MIXED_DIR        = new File(Fixtures.TEST_AMI_DIR, "mixed");

	public final static File TEST_PLOSONE_DIR      = new File(Fixtures.TEST_AMI_DIR, "plosone");
	public final static File TEST_PLOSONE_0115884  = new File(Fixtures.TEST_PLOSONE_DIR, "journal.pone.0115884");
	public final static File TEST_PLOSONE_SEQUENCE_0121780  = new File(Fixtures.TEST_PLOSONE_DIR, "plosjournal.pone.0121780_sequence");
	public final static File TEST_PLOSONE_MALARIA_0119475  = new File(Fixtures.TEST_PLOSONE_DIR, "journal.pone.0119475");

	public static final File EXAMPLES              = new File("examples");
	public static final File EXAMPLES_TEMP_16_1_1  = new File("target/examples_16_1_1");

	private static final String RESULTS_XML = "results.xml";
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
		LOG.debug("++++++++++++++++++++++   harness   +++++++++++++++++++++++");
		LOG.debug("temp exists: "+temp+"; e: "+temp.exists()+"; d "+temp.isDirectory());
		QuickscrapeNorma qsNorma = new QuickscrapeNorma(cmDirectory);
		FileUtils.deleteDirectory(temp);
		qsNorma.copyTo(temp, true);
		temp.mkdirs();
		LOG.debug("temp exists: "+temp+"; e: "+temp.exists()+"; d "+temp.isDirectory());
		List<File> files = new ArrayList<File>(FileUtils.listFiles(temp, null, true));
		LOG.debug("FILES: "+files);
		
		Assert.assertFalse("exists? "+RESULTS_XML, qsNorma.hasResultsXML());
		AMIArgProcessor argProcessor = (AMIArgProcessor) plugin.getArgProcessor();
		argProcessor.parseArgs(args);
		argProcessor.runAndOutput();
		files = new ArrayList<File>(FileUtils.listFiles(temp, null, true));
		LOG.debug("FILES after: "+files);
		LOG.debug("==========================="+argProcessor+"=============================");
		LOG.debug("results exists? "+new File(temp,"results").exists());
		
		for (String pluginAndOption : pluginAndOptions) {
			Fixtures.compareExpectedAndResults(qsNorma.getDirectory(), temp, pluginAndOption + RESULTS_XML);
		}
	}

	/** compares results.xml files in expected and actual directories.
	 * 
	 * @param expectedCM cmDirectory (must contain expected/)
	 * @param resultsCM cmDiecrory (must contain results/)
	 * @param pluginAndOption e.g. "species/binomial/"
	 * @throws IOException
	 */
	public static void compareExpectedAndResults(File expectedCM, File resultsCM, String pluginAndOption) throws IOException {
		
		File expectedFile = new File(expectedCM, EXPECTED_DIR+pluginAndOption);
		Assert.assertTrue("expected file should exist : "+expectedFile, expectedFile.exists());
		File resultsFile = new File(resultsCM, RESULTS_DIR+pluginAndOption);
		Assert.assertTrue("results file should exist: "+resultsFile, resultsFile.exists());
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
