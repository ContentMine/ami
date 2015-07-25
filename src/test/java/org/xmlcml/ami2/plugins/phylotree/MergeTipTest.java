package org.xmlcml.ami2.plugins.phylotree;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.regex.Pattern;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.Test;
import org.vafer.jdeb.shaded.compress.io.FileUtils;
import org.xmlcml.ami2.Fixtures;
import org.xmlcml.ami2.plugins.phylotree.nexml.NexmlNEXML;
import org.xmlcml.graphics.svg.SVGSVG;
import org.xmlcml.norma.image.ocr.HOCRReader;
import org.xmlcml.xml.XMLUtil;

public class MergeTipTest {

	private static final String X15GOODTREE = "target/phylo/combined/15goodtree/";

	public static final Logger LOG = Logger.getLogger(HOCRPhyloTreeTest.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	private static String[] ROOTS = {
			"ijs.0.000174-0-000",
			"ijs.0.000364-0-004",
			"ijs.0.000406-0-000",
			"ijs.0.001362-0-002",
			"ijs.0.001420-0-000",
			"ijs.0.001537-0-001",
			"ijs.0.001966-0-000",
			"ijs.0.002048-0-001",
			"ijs.0.002113-0-000",
			"ijs.0.002220-0-000",
			"ijs.0.002444-0-000",
			"ijs.0.002469-0-000",
			"ijs.0.002618-0-001",
			"ijs.0.003228-0-001",
			"ijs.0.003723-0-000",
	};
	
	public final static Pattern IJSEM = Pattern.compile("(?:([0-9]+[^~]*)~)*"
			+ "(?:(‘?[A-Z](?:[a-z]{2,}|[a-z]?\\.))~)"
			+ "(?:([a-z]+’?)~)"
			+ "(?:(?:(ATCC|DSM|HHQ|IFO|IMSNU|LMG|NBRC|NCDO|NCIMB|NRRL|YIM)~)?)"
			+ "(?:([A-Z0-9\\-]+T?)~?)"
			+ "(?:\\((.*)\\)).*");



	@Test
	public void testConvertLabelsAndTreeAndMerge() throws Exception {
		
		for (String root : ROOTS) {
			LOG.debug(root);
			File infile = new File(Fixtures.TEST_PHYLO_DIR, "15goodtree/"+root+".pbm.png");
			org.apache.commons.io.FileUtils.copyFile(infile, new File(X15GOODTREE+root+".png"));
			PhyloTreeArgProcessor phyloTreeArgProcessor = new PhyloTreeArgProcessor();
			phyloTreeArgProcessor.setSpeciesPattern(IJSEM);
			phyloTreeArgProcessor.setOutputRoot(root);
			phyloTreeArgProcessor.setOutputDir(new File("target/phylo/combined/15goodtree/"));
			phyloTreeArgProcessor.readAndCombineTreeAndTips(infile);
			NexmlNEXML nexml = phyloTreeArgProcessor.getNexml();
			new File(X15GOODTREE).mkdirs();
			XMLUtil.debug(nexml, new FileOutputStream(X15GOODTREE+root+".nexml.xml"), 1);
			FileUtils.write(new File(X15GOODTREE+root+".nwk"), nexml.createNewick());
			XMLUtil.debug(nexml.createSVG(), new FileOutputStream(X15GOODTREE+root+".svg"), 1);
			HOCRReader hocrReader = phyloTreeArgProcessor.getOrCreateHOCRReader();
			SVGSVG.wrapAndWriteAsSVG(hocrReader.getOrCreateSVG(), new File(X15GOODTREE+root+".words.svg"));
			
		}
	}
	
	@Test
	public void testNearlyCorrect1420() throws Exception {
		String root = "ijs.0.001420-0-000";
		readAndCombineTopsAndLabels(root, new File("target/phylo/combined/15goodtree/"));
	}

	@Test
	public void testSplitPhrases364() throws Exception {
		String root = "ijs.0.000364-0-004";
		readAndCombineTopsAndLabels(root, new File("target/phylo/combined/15goodtree/"));
	}

	private void readAndCombineTopsAndLabels(String root, File outputDir) throws IOException,
			InterruptedException, FileNotFoundException {
		File infile = new File(Fixtures.TEST_PHYLO_DIR, "15goodtree/"+root+".pbm.png");
		PhyloTreeArgProcessor phyloTreeArgProcessor = new PhyloTreeArgProcessor();
		phyloTreeArgProcessor.setOutputRoot(root);
		phyloTreeArgProcessor.setOutputDir(outputDir);
		phyloTreeArgProcessor.readAndCombineTreeAndTips(infile);
		NexmlNEXML nexml = phyloTreeArgProcessor.getNexml();
		new File(X15GOODTREE).mkdirs();
		XMLUtil.debug(nexml, new FileOutputStream(X15GOODTREE+root+".nexml.xml"), 1);
		FileUtils.write(new File(X15GOODTREE+root+".nwk"), nexml.createNewick());
		XMLUtil.debug(nexml.createSVG(), new FileOutputStream(X15GOODTREE+root+".svg"), 1);
	}

	@Test
	public void testMerge() throws IOException, InterruptedException {
		File imageFile = new File(Fixtures.TEST_PHYLO_DIR, "15goodtree/ijs.0.000174-0-000.pbm.png");
		PhyloTreeArgProcessor phyloTreeArgProcessor = new PhyloTreeArgProcessor();
		phyloTreeArgProcessor.readAndCombineTreeAndTips(imageFile);
		NexmlNEXML nexml = phyloTreeArgProcessor.getNexml();
		XMLUtil.debug(nexml, new FileOutputStream("target/phylo/ijs.0.000174-0-000.xml"), 1);
	}


}
