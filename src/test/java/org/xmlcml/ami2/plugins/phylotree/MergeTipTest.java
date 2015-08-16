package org.xmlcml.ami2.plugins.phylotree;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import nu.xom.Element;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.Ignore;
import org.junit.Test;
import org.vafer.jdeb.shaded.compress.io.FileUtils;
import org.xmlcml.ami2.AMIFixtures;
import org.xmlcml.ami2.lookups.ENALookup;
import org.xmlcml.ami2.plugins.phylotree.nexml.NexmlNEXML;
import org.xmlcml.graphics.svg.SVGSVG;
import org.xmlcml.norma.editor.EditList;
import org.xmlcml.norma.editor.SubstitutionEditor;
import org.xmlcml.norma.image.ocr.HOCRReader;
import org.xmlcml.xml.XMLUtil;

//@Ignore("problematic in Jenkins; uncomment for testing")
public class MergeTipTest {

	private static final String PNG = ".png";

	private static final String PBM_PNG = ".pbm.png";

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
	
	private static String[] BAD_ROOTS = {
		"ijs.0.64938-0-000",
		"ijs.0.64950-0-000",
		"ijs.0.64952-0-000",
		"ijs.0.64969-0-000",
		"ijs.0.64970-0-001",
		"ijs.0.64980-0-001",
		"ijs.0.65003-0-000",
	};
	
	public final static Pattern IJSEM = Pattern.compile(""
			// probably leading garbage due to lines
//			+ "(?:([0-9]+[^~]*)~)*"
			// genus
//			+ "(?:(‘?[A-Z](?:[a-z]{2,}|[a-z]?\\.))~)"
			+ "\\s*(‘?[A-Z](?:[a-z]{2,}|[a-z]?\\.))"
			// species
			+ "\\s*([a-z]+’?)"
			// strain source
//			+ "(?:(?:(ATCC|DSM|HHQ|IFO|IMSNU|LMG|NBRC|NCDO|NCIMB|NRRL|YIM)~)?)"
			// strain
//			+ "(?:([A-Z0-9\\-]+T?)~?)"
			+ "\\s*.*\\s*"
			// ENA ID
			+ "\\(([^\\)]+)\\).*");



	@Test
//	@Ignore("too many")
	public void testConvertLabelsAndTreeAndMerge() throws Exception {
		
		for (String root : ROOTS) {
			try {
				LOG.debug(root);
				File infile = new File(AMIFixtures.TEST_PHYLO_DIR, "15goodtree/"+root+PBM_PNG);
				org.apache.commons.io.FileUtils.copyFile(infile, new File(X15GOODTREE+root+PNG));
				PhyloTreeArgProcessor phyloTreeArgProcessor = new PhyloTreeArgProcessor();
				phyloTreeArgProcessor.setSpeciesPattern(IJSEM);
				phyloTreeArgProcessor.setOutputRoot(root);
				phyloTreeArgProcessor.setOutputDir(new File("target/phylo/combined/15goodtree/"));
				if (!phyloTreeArgProcessor.mergeOCRAndPixelTree(infile)) continue; // tesseract failure
				NexmlNEXML nexml = phyloTreeArgProcessor.getNexml();
				new File(X15GOODTREE).mkdirs();
				XMLUtil.debug(nexml, new FileOutputStream(X15GOODTREE+root+".nexml.xml"), 1);
				FileUtils.write(new File(X15GOODTREE+root+".nwk"), nexml.createNewick());
				XMLUtil.debug(nexml.createSVG(), new FileOutputStream(X15GOODTREE+root+".svg"), 1);
				HOCRReader hocrReader = phyloTreeArgProcessor.getOrCreateHOCRReader();
				SVGSVG.wrapAndWriteAsSVG(hocrReader.getOrCreateSVG(), new File(X15GOODTREE+root+".words.svg"));
			} catch (Exception e) {
				LOG.debug("error in conversion");
				e.printStackTrace();
			}
			
		}
	}

	
	@Test
	public void testConvertPngToSemanticFiles() throws Exception {
		PhyloTreeArgProcessor.convertPngToHTML_SVG_NEXML_NWK(
			new File("src/test/resources/org/xmlcml/ami2/phylo/15goodtree/ijs.0.000174-0-000.pbm.png"),
			new File("target/phylo/misc"));
	}

	@Test
	@Ignore
	public void testNearlyCorrect1420() throws Exception {
		String root = "ijs.0.001420-0-000";
		readAndCombineTopsAndLabels(root, new File("target/phylo/combined/15goodtree/"));
	}

	@Test
//	@Ignore
	public void testSplitPhrases364() throws Exception {
		String root = "ijs.0.000364-0-004";
		readAndCombineTopsAndLabels(root, new File("target/phylo/combined/15goodtree/"));
	}

	@Test
	@Ignore("uses tesseract")
	public void testMerge() throws IOException, InterruptedException {
		File imageFile = new File(AMIFixtures.TEST_PHYLO_DIR, "15goodtree/ijs.0.000174-0-000.pbm.png");
		PhyloTreeArgProcessor phyloTreeArgProcessor = new PhyloTreeArgProcessor();
		if (!phyloTreeArgProcessor.mergeOCRAndPixelTree(imageFile)) return;
		NexmlNEXML nexml = phyloTreeArgProcessor.getNexml();
		XMLUtil.debug(nexml, new File("target/phylo/ijs.0.000174-0-000.xml"), 1);
	}
	
	@Test
	@Ignore("uses tesseract") // change debug to trace for committal
	// FIXME deal with spaces in IDs
	public void testLookup() throws IOException, InterruptedException {
		File imageFile = new File(AMIFixtures.TEST_PHYLO_DIR, "15goodtree/ijs.0.000364-0-004.pbm.png");
		PhyloTreeArgProcessor phyloTreeArgProcessor = new PhyloTreeArgProcessor();
		if (!phyloTreeArgProcessor.mergeOCRAndPixelTree(imageFile)) return;
		NexmlNEXML nexml = phyloTreeArgProcessor.getNexml();
		List<Element> nodes = XMLUtil.getQueryElements(nexml, "//*[local-name()='otu']");
		List<String> ids = new ArrayList<String>();
		for (Element node : nodes) {
			String value = node.getValue();
//			LOG.debug(value);
			Matcher matcher = IJSEM.matcher(value);
			if (matcher.matches()) {
				String genus = matcher.group(1);
				String species = matcher.group(2);
				String id = matcher.group(3);
				LOG.debug(genus+" + "+species+" + "+id);
				ids.add(id);
			}
		}
		String s = new ENALookup().lookupGenbankIds(ids);
		LOG.debug(">"+s);
	}
	
	@Test
	public void testUnrootedEdge() throws Exception {
		File inputDir = new File(AMIFixtures.TEST_PHYLO_DIR, "problems1");
		testProblem00(inputDir, new File("target/phylo/problems1/"), "ijs.0.64938-0-000");
	}

	@Test
	public void testUnrootedEdges() {
		File inputDir = new File(AMIFixtures.TEST_PHYLO_DIR, "problems1");
		for (String root :BAD_ROOTS) {
			try {
				testProblem00(inputDir, new File("target/phylo/problems1/"), root);
			} catch (Exception e) {
				LOG.debug("ERROR: "+e);
			}
		}
	}

	@Test
	@Ignore("fix later")
	public void testProblem2() throws Exception {
		testProblem0("ijs.0.65219-0-002");
	}

	// ===========================
	
	

	private void testProblem0(String root) throws Exception {
		File inputDir = new File(AMIFixtures.TEST_PHYLO_DIR, "problems/");
		File outputDir = new File("target/phylo/problems/");
		testProblem00(inputDir, outputDir, root);
	}

	private void testProblem00(File dir, File outputDir, String root) throws Exception {
		File imageFile = new File(dir, root+".pbm.png");
		PhyloTreeArgProcessor phyloTreeArgProcessor = new PhyloTreeArgProcessor();
		if (phyloTreeArgProcessor.mergeOCRAndPixelTree(imageFile)) {
			NexmlNEXML nexml = phyloTreeArgProcessor.getNexml();
			outputDir.mkdirs();
//			FileUtils.write(new File(outputDir, root+".nwk"), nexml.createNewick());
			XMLUtil.debug(nexml.createSVG(), new File(outputDir, root+".svg"), 1);
			XMLUtil.debug(nexml, new File(outputDir, root+".xml"), 1);
		} else {
			LOG.error("failed to create tree");
		}
	}

	@Test
	public void testProblem() throws Exception {
		File imageFile = new File(AMIFixtures.TEST_PHYLO_DIR, "problems/ijs.0.65219-0-001.pbm.png");
		PhyloTreeArgProcessor phyloTreeArgProcessor = new PhyloTreeArgProcessor();
		if (phyloTreeArgProcessor.mergeOCRAndPixelTree(imageFile)) {
			NexmlNEXML nexml = phyloTreeArgProcessor.getNexml();
			XMLUtil.debug(nexml, new File("target/phylo/ijs.0.65219-0-001.xml"), 1);
		}
	}

	// =========================================
	private boolean readAndCombineTopsAndLabels(String root, File outputDir) throws IOException,
	InterruptedException, FileNotFoundException {
		File infile = new File(AMIFixtures.TEST_PHYLO_DIR, "15goodtree/"+root+PBM_PNG);
		PhyloTreeArgProcessor phyloTreeArgProcessor = new PhyloTreeArgProcessor();
		phyloTreeArgProcessor.setOutputRoot(root);
		phyloTreeArgProcessor.setOutputDir(outputDir);
		if (!phyloTreeArgProcessor.mergeOCRAndPixelTree(infile)) return false;
		NexmlNEXML nexml = phyloTreeArgProcessor.getNexml();
		new File(X15GOODTREE).mkdirs();
		XMLUtil.debug(nexml, new FileOutputStream(X15GOODTREE+root+".nexml.xml"), 1);
		FileUtils.write(new File(X15GOODTREE+root+".nwk"), nexml.createNewick());
		XMLUtil.debug(nexml.createSVG(), new FileOutputStream(X15GOODTREE+root+".svg"), 1);
		return true;
	}


}
