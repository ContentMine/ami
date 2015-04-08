package org.xmlcml.ami2;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.Assert;
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
	public static final File EXAMPLES_TEMP_16_1_1         = new File("target/examples_16_1_1");

	public static void compareExpectedAndResults(File expectedCM, File resultsCM, String results) throws IOException {
		
		File expectedFile = new File(expectedCM, "expected/"+results);
		Assert.assertTrue(""+expectedFile, expectedFile.exists());
		File resultsFile = new File(resultsCM, "results/"+results);
		Assert.assertTrue(resultsFile.exists());
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
