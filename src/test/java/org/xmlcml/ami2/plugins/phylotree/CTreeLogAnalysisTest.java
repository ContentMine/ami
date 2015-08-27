package org.xmlcml.ami2.plugins.phylotree;

import java.io.File;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;
import org.xmlcml.ami2.AMIFixtures;
import org.xmlcml.cmine.args.log.CMineLog;

/** analysis of CTreeLog output which might later go in argProcessor.summary actions.
 * 
 * @author pm286
 *
 */
public class CTreeLogAnalysisTest {

	private static final String LOG_XML = "log.xml";
	private static final Logger LOG = Logger.getLogger(CTreeLogAnalysisTest.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	public final static File BATCH1 = new File(AMIFixtures.TEST_PHYLO_DIR, "batch1/");
	public final static String CMINE_LOG = "cmine.log.xml";
	
	@Test
	public void testReadLog() {
		Assert.assertTrue(BATCH1.exists() && BATCH1.isDirectory());
		File[] files = BATCH1.listFiles();
		File logFile = new File("target/phylo/"+CMINE_LOG);
		CMineLog cMineLog = new CMineLog(logFile);
		Assert.assertEquals("batch1",  185, files.length);
		for (File dir : files) {
			if (!dir.isDirectory()) {
				if (CMINE_LOG.equals(dir.getName())) {
					continue;
				}
				throw new RuntimeException("forbidden file "+dir+"; "+dir.getName());
			}
			File logXml = new File(dir, LOG_XML);
			if (!logXml.exists()) {
				cMineLog.error("missingLogFile: "+dir);
			} else {
				cMineLog.mergeLogFile(logXml, dir, "//error", "//warn", "//info");
			}
		}
		cMineLog.removeNodes("//*/@method");
		cMineLog.removeNodes("//*/@millis");
		cMineLog.collectWithinLog("info[starts-with(@message,'deleted node')]", "deleted nodes");
		cMineLog.collectWithinLog("error[@message='ERR_BAD_SYNTAX []']", "empty tips");
		cMineLog.collectWithinLog("info[starts-with(@message,'wrote')]", "wrote files");
		cMineLog.collectWithinLog("error[starts-with(@message,'cannot delete tip')]", "Tip deletion error");
		cMineLog.collectWithinLog("error[starts-with(@message,'ERR_BAD_SYNTAX')]", "Syntax error");
		cMineLog.collectWithinLog("error[starts-with(@message,'edge target')]", "edge target==source");
		cMineLog.collectWithinLog("error[starts-with(@message,'duplicate edge')]", "duplicate edge");
		
		cMineLog.deleteFromLog("*[@message='Analyzed pixels for tree successfully']");
		cMineLog.deleteFromLog("*[starts-with(@message,'PhyloTree output to:')]");
		cMineLog.deleteFromLog("warn[@message='null nexml']");
		cMineLog.deleteFromLog("*[starts-with(@message,'invalid genus')]");
		cMineLog.removeNodes("//@millis");
		cMineLog.writeLog();
	}
}
