package org.xmlcml.ami2.plugins.phylotree;

import java.io.File;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.xmlcml.ami2.AMIFixtures;
import org.xmlcml.ami2.plugins.phylotree.nexml.NexmlElement;
import org.xmlcml.ami2.plugins.phylotree.nexml.NexmlNEXML;
import org.xmlcml.cmine.args.log.CMineLog;

/** analysis of CTreeLog output which might later go in argProcessor.summary actions.
 * 
 * @author pm286
 *
 */
@Ignore("long and uses foreign directories")
public class CTreeLogAnalysisTest {

	private static final Logger LOG = Logger.getLogger(CTreeLogAnalysisTest.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	public final static File BATCH1 = new File(AMIFixtures.TEST_PHYLO_DIR, "batch1/");
	public final static File IJSEM = new File("../ijsem/");
	
	@Test
	public void testReadLog() {
		File dir0 = BATCH1;
		int count = 185;
		File logFile = new File("target/phylo/"+CMineLog.CMINE_LOG);
		summarizeInLog(dir0, count, logFile);
	}

	@Test
	public void testReadLogAB500() {
		summarizeInLog(new File(IJSEM, "500A"), 502);
		summarizeInLog(new File(IJSEM, "500B"), 502);
	}
	
	@Test
	public void testExtractSpecies() {
		extractSpecies(new File(IJSEM, "500A"), 397);
//		summarizeInLog(new File(IJSEM, "500B"), 502);
	}
	
	// ===========================================
	

	private void extractSpecies(File dir, int count) {
		List<File> nexmlFiles = new ArrayList<File>(FileUtils.listFiles(dir, new String[]{"nexml.xml"}, true));
		Assert.assertEquals(count, nexmlFiles.size());
		for (File nexmlFile : nexmlFiles) {
			LOG.debug(""+nexmlFile);
			NexmlNEXML nexml = null;
			try {
				nexml = (NexmlNEXML) NexmlElement.readAndCreateNEXML(nexmlFile);
			} catch (Exception e) {
				LOG.error("could not read: "+nexmlFile+"; "+e);
			}
		}
	}

	private void summarizeInLog(File dir0, int count) {
		File logFile = new File(dir0, CMineLog.CMINE_LOG);
		summarizeInLog(dir0, count, logFile);
	}

	private void summarizeInLog(File dir0, int count, File logFile) {
		Assert.assertTrue(dir0.exists() && dir0.isDirectory());
		File[] files = dir0.listFiles();
		CMineLog cMineLog = new CMineLog(logFile);
		Assert.assertEquals(String.valueOf(dir0), count, files.length);
		cMineLog.summarizeInLog(files);
	}
}
