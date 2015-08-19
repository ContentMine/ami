package org.xmlcml.ami2.plugins.phylotree;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.xmlcml.ami2.AMIFixtures;
import org.xmlcml.ami2.lookups.TaxdumpLookup;
import org.xmlcml.ami2.lookups.TaxdumpLookup.TaxonType;
import org.xmlcml.ami2.plugins.phylotree.nexml.NexmlNEXML;
import org.xmlcml.ami2.plugins.phylotree.nexml.NexmlOtu;
import org.xmlcml.ami2.plugins.phylotree.nexml.NexmlOtus;
import org.xmlcml.cmine.files.CMDir;
import org.xmlcml.norma.editor.EditList;
import org.xmlcml.norma.editor.Extraction;
import org.xmlcml.norma.editor.SubstitutionEditor;
import org.xmlcml.xml.XMLUtil;

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
		PhyloTreePlugin phyloPlugin = new PhyloTreePlugin(cmd);
		phyloPlugin.runAndOutput();
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
		PhyloTreePlugin phyloPlugin = new PhyloTreePlugin(cmd);
		phyloPlugin.runAndOutput();
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
				" --ph.specpattern ijsem.xml"+
				" --ph.hocr.html image/"+img+".hocr.html"+
				" --ph.hocr.svg image/"+img+".hocr.svg"+
				" --ph.svg image/"+img+".svg"+
				" --ph.newick image/"+img+".nwk"+
				" --ph.nexml image/"+img+".nexml.xml"+
				"";
		PhyloTreePlugin phyloPlugin = new PhyloTreePlugin(cmd);
		phyloPlugin.runAndOutput();
	}

	@Test
	@Ignore("tesseract")
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
		PhyloTreePlugin phyloPlugin = new PhyloTreePlugin(cmd);
		PhyloTreeArgProcessor phyloTreeArgProcessor = (PhyloTreeArgProcessor)phyloPlugin.getArgProcessor();
		phyloPlugin.runAndOutput();
		NexmlNEXML nexml = phyloTreeArgProcessor.getNexml();

		SubstitutionEditor substitutionEditor = new SubstitutionEditor();
		InputStream speciesPatternInputStream = phyloTreeArgProcessor.getSpeciesPatternInputStream();
		Assert.assertNotNull("input pattern stream",speciesPatternInputStream);
		substitutionEditor.addEditor(speciesPatternInputStream);
		NexmlOtus nexmlOtus = nexml.getSingleOtusElement();
		List<NexmlOtu> otuList = nexmlOtus.getNexmlOtuList();
		TaxdumpLookup taxdumpLookup = new TaxdumpLookup();
		nexml.getSingleOtusElement().addNamespaceDeclaration(PhyloConstants.CM_PHYLO_PREFIX, PhyloConstants.CM_PHYLO_NS);
		for (NexmlOtu otu : otuList) {
			String value = otu.getValue();
			String editedValue = substitutionEditor.createEditedValueAndRecord(value);
			List<Extraction> extractionList = substitutionEditor.getExtractionList();
			phyloTreeArgProcessor.annotateOtuWithEditRecord(otu, substitutionEditor.getEditRecord());
			phyloTreeArgProcessor.annotateOtuWithExtractions(otu, extractionList);
			LOG.trace(">otu>"+otu.toXML());
//			if (substitutionEditor.validate(extractionList)) {
			int maxDelta = 4;
			if (editedValue == null) {
				LOG.debug("incorrect syntax: "+value);
			} else if (substitutionEditor.validate(editedValue)) {
				EditList editRecord = substitutionEditor.getEditRecord();
				otu.setEditRecord(editRecord.toString());
				LOG.debug("syntax OK: "+value+" => "+editedValue+((editRecord == null || editRecord.size() == 0) ? "" :"; "+editRecord));
				String genus = otu.getAttributeValue("genus", PhyloConstants.CM_PHYLO_NS);
				String species = otu.getAttributeValue("species", PhyloConstants.CM_PHYLO_NS);
				boolean changed = false;
				boolean matched = false;
				if (taxdumpLookup.isValidBinomial(genus, species)) {
					LOG.trace("Valid organism: "+genus+" "+species);
					matched = true;
				} else if (!taxdumpLookup.isValidGenus(genus)) {
					LOG.trace("invalid genus, looking for closest match: "+genus);
					List<String> closestGenusList = taxdumpLookup.getClosest(taxdumpLookup.getGenusSet(), genus, maxDelta);
					if (closestGenusList.size() > 0) {
						LOG.trace("Could this be :"+closestGenusList);
						if (closestGenusList.size() == 1) {
							genus = closestGenusList.get(0);
							changed = true;
						}
					}
				}
				if (!matched) {
					// optimize later 
					List<String> speciesList = taxdumpLookup.lookupSpeciesList(genus);
					List<String> bestSpecies = taxdumpLookup.getClosest(speciesList, species, maxDelta);
					if (bestSpecies.size() == 1) {
						species = bestSpecies.get(0);
						changed = true;
					}
				}
				LOG.trace("genus>"+genus+": "+taxdumpLookup.isValidGenus(genus));
				LOG.trace("binomial>"+genus+" "+species+": "+taxdumpLookup.isValidBinomial(genus, species));
				if (changed) {
					LOG.debug("corrected to: "+TaxdumpLookup.getBinomial(genus, species));
				}
			} else {
				LOG.debug("bad syntax "+editedValue);
			}
		}
		LOG.trace(nexml.toXML());
		new File("target/phylo").mkdirs();
		XMLUtil.debug(nexml, new FileOutputStream("target/phylo/000364.edited.nexml.xml"), 1);
	}

	


	
}
