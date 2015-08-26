package org.xmlcml.ami2.plugins.phylotree;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.vafer.jdeb.shaded.compress.io.FilenameUtils;
import org.xmlcml.ami2.AMIFixtures;
import org.xmlcml.cmine.files.CMDir;

public class PhyloArgProcessorTest {

	
	
	private static final Logger LOG = Logger.getLogger(PhyloArgProcessorTest.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	@Test
	public void testPhyloArgProcessor() throws Exception {
		File newDir = new File("target/ijsem/phylo");
	}
	
	@Test
	@Ignore // accesses net
	public void testPhyloArgProcessorLookup() throws Exception {
	}

	
	@Test
	/** very thick lines - behaves badly in thinning. But overall works.
	 * Needs 0- and 2-connected nodes merging...
	 * 
	 * 
	 * @throws Exception
	 */
	@Ignore("requires tesseract")
	public void testPhyloHarness() throws Exception {
		CMDir cmDir = new CMDir(new File(AMIFixtures.TEST_PHYLO_DIR, "ijs_0_000174_0"));
		File normaTemp = new File("target/phylo/ijs_0_000174_0");
		cmDir.copyTo(normaTemp, true);
		String cmd = "--ph.phylo -q target/phylo/ijs_0_000174_0 -i image/000.pbm.png -o target/phylo/junk.xml"; 
		new PhyloTreeArgProcessor(cmd).runAndOutput();
	}

	@Test
	/** well behaved tree a few edge ends not joined to nodes, probably due to bad nodes.
	 * 
	 * @throws Exception
	 */
	@Ignore("requires tesseract") // uncomment later
	public void testCommandLine() throws Exception {
		String name = "ijs_0_000364_0"; String img = "003";
		CMDir cmDir = new CMDir(new File(AMIFixtures.TEST_PHYLO_DIR, name));
		File normaTemp = new File("target/phylo/"+name);
		cmDir.copyTo(normaTemp, true);
//		String cmd = "--ph.phylo -q target/phylo/"+name+" -i image/"+img+".pbm.png -o target/phylotest/"+name; 
		String cmd = "--ph.phylo -q target/phylo/"+name+" -i image/"+img+".pbm.png"; 
		new PhyloTreeArgProcessor(cmd).runAndOutput();
	}

	@Test
	/** 
	 * development of new options in ami-phylo
	 * 
	 * @throws Exception
	 */
//	@Ignore("requires tesseract")
	public void testFullCommandLine() throws Exception {
		String name = "ijs_0_000364_0"; 
		String img = "003";
		CMDir cmDir = new CMDir(new File(AMIFixtures.TEST_PHYLO_DIR, name));
		File normaTemp = new File("target/phylo/"+name);
		cmDir.copyTo(normaTemp, true);
		String cmd = "--ph.phylo -q target/phylo/"+name+
				" -i image/"+img+".pbm.png"+
				" --log"+
				" --ph.specpattern ijsemSpeciesEditor.xml"+
				" --ph.hocr.html image/"+img+".hocr.html"+
				" --ph.hocr.svg image/"+img+".hocr.svg"+
				" --ph.svg image/"+img+".svg"+
				" --ph.newick image/"+img+".nwk"+
				" --ph.nexml image/"+img+".nexml.xml"+
				"";
		new PhyloTreeArgProcessor(cmd).runAndOutput();
	}

	@Test
	/** 
	 * development of new options in ami-phylo
	 * 
	 * @throws Exception
	 */
//	@Ignore("requires tesseract")
	public void testProcessPngList() throws Exception {
//		String name = "ijs_0_000364_0_003.png";
		List<File> pngList = new ArrayList<File>(FileUtils.listFiles(new File(AMIFixtures.TEST_PHYLO_DIR, "15goodtree"), new String[]{"png"}, false));
		for (File pngFile : pngList) {
			extractTreeNewickNexml(pngFile);
		}
	}

	private void extractTreeNewickNexml(File pngFile) throws IOException {
		String baseName = FilenameUtils.getBaseName(pngFile.toString());
		String name = FilenameUtils.getName(pngFile.toString());
		File normaTempCTree = new File("target/phylo/"+baseName+"/");
		File normaImageDir = new File(normaTempCTree, "image/");
		normaImageDir.mkdirs();
		File normaImage = new File(normaImageDir, name);
		FileUtils.copyFile(pngFile, normaImage);
		
		String cmd = "--ph.phylo -q "+normaTempCTree+
				" -i image/"+name+
				" --log"+
				" --ph.specpattern ijsemSpeciesEditor.xml"+
				" --ph.hocr.html image/"+baseName+".hocr.html"+
				" --ph.hocr.svg image/"+baseName+".hocr.svg"+
				" --ph.svg image/"+baseName+".svg"+
				" --ph.newick image/"+baseName+".nwk"+
				" --ph.nexml image/"+baseName+".nexml.xml"+
				"";
		new PhyloTreeArgProcessor(cmd).runAndOutput();
	}

	@Test
//	@Ignore("tesseract")
	public void testEditLabels() throws IOException {
		File dir = AMIFixtures.TEST_PHYLO_DIR;
		String name = "ijs_0_000364_0"; 
		runExtraction(dir, name, "003");

	}

	@Test
	@Ignore("tesseract")
	public void testEditLabels1() throws IOException {
		runExtraction(AMIFixtures.TEST_PHYLO_DIR, "ijs_0_000174_0", "000");
		runExtraction(AMIFixtures.TEST_PHYLO_DIR, "ijs_0_000265_0", "000");
	}

	private void runExtraction(File dir, String name, String img) throws IOException,
			FileNotFoundException {
		File cmDirDir = new File(dir, name);
		Assert.assertTrue("file exists: "+cmDirDir, cmDirDir.exists());
		CMDir cmDir = new CMDir(cmDirDir);
		File normaTemp = new File("target/phylo/"+name);
		cmDir.copyTo(normaTemp, true);
		Assert.assertTrue("file exists: "+normaTemp, normaTemp.exists());
		String cmd = "--ph.phylo -q target/phylo/"+name+
				" -i image/"+img+".pbm.png"+
				" --log"+
				" --ph.specpattern ijsemSpeciesEditor.xml"+
				" --ph.hocr.html image/"+img+".hocr.html"+
				" --ph.hocr.svg image/"+img+".hocr.svg"+
				" --ph.nexml image/"+img+".nexml.xml"+
				"";
		PhyloTreeArgProcessor phyloTreeArgProcessor = new PhyloTreeArgProcessor(cmd);
		phyloTreeArgProcessor.runAndOutput();
	}

	


	
}
