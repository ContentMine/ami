package org.xmlcml.ami2.plugins.phylotree;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.xmlcml.ami2.AMIFixtures;
import org.xmlcml.ami2.plugins.phylotree.nexml.NexmlNEXML;
import org.xmlcml.ami2.plugins.phylotree.nexml.NexmlOtu;
import org.xmlcml.graphics.svg.SVGSVG;
import org.xmlcml.norma.editor.EditList;
import org.xmlcml.norma.editor.Extraction;
import org.xmlcml.norma.editor.SubstitutionEditor;
import org.xmlcml.norma.image.ocr.HOCRReader;
import org.xmlcml.xml.XMLUtil;

@Ignore("remove exception for development")
public class LongRunningTests {

	public static final Logger LOG = Logger.getLogger(LongRunningTests.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	public final static String SUFFIX = ".pbm.png";
	@Test
	public void testImage2Nexml() throws IOException {
		File phyloDir = new File("src/test/resources/org/xmlcml/ami2/phylo/50images/");
		Assert.assertTrue("file exists "+phyloDir, phyloDir.exists() );
		Assert.assertTrue("file is dir "+phyloDir, phyloDir.isDirectory());
		List<File> images = new ArrayList<File>(FileUtils.listFiles(phyloDir, new String[]{"png"}, false));
		for (File image : images) {
			String name = image.getName();
			name = name.substring(0,  name.length() - SUFFIX.length());
			runPhyloEditing2Nexml(phyloDir, name);
		}
}

	private void runPhyloEditing2Nexml(File phyloDir, String name) throws IOException,
			FileNotFoundException {
		LOG.debug("============="+name+"=============");
//		CMDir cmDir = new CMDir(new File(AMIFixtures.TEST_PHYLO_DIR, name));
		File normaTemp = new File("target/phylo/"+name+"/");
		FileUtils.copyFile(new File(phyloDir, name+SUFFIX), new File(normaTemp, "image/"+name+SUFFIX));
//		cmDir.copyTo(normaTemp, true);
		String cmd = "--ph.phylo -q target/phylo/"+name+
				" -i image/"+name+".pbm.png"+
				" --log"+
				" --ph.specpattern ijsemSpeciesEditor.xml"+
				" --ph.hocr.html image/"+name+".hocr.html"+
				" --ph.hocr.svg image/"+name+".hocr.svg"+
				" --ph.nexml image/"+name+".nexml.xml"+
				"";
		PhyloTreeArgProcessor phyloTreeArgProcessor = new PhyloTreeArgProcessor(cmd);
		phyloTreeArgProcessor.runAndOutput();
		NexmlNEXML nexml = phyloTreeArgProcessor.getNexml();

		SubstitutionEditor substitutionEditor = new SubstitutionEditor();
		substitutionEditor.addEditor(phyloTreeArgProcessor.getSpeciesPatternInputStream());
		List<NexmlOtu> otuList = nexml.getSingleOtusElement().getNexmlOtuList();
		nexml.getSingleOtusElement().addNamespaceDeclaration(PhyloConstants.CM_PHYLO_PREFIX, PhyloConstants.CM_PHYLO_NS);
		for (NexmlOtu otu : otuList) {
			String value = otu.getValue();
			String editedValue = substitutionEditor.createEditedValueAndRecord(value);
			List<Extraction> extractionList = substitutionEditor.getExtractionList();
			otu.annotateOtuWithEditRecord(substitutionEditor.getEditRecord());
			phyloTreeArgProcessor.annotateOtuWithExtractions(otu, extractionList);
			LOG.trace(">otu>"+otu.toXML());
//			if (substitutionEditor.validate(extractionList)) {
			if (substitutionEditor.validate(editedValue)) {
				EditList editRecord = substitutionEditor.getEditRecord();
				otu.setEditRecord(editRecord.toString());
				LOG.debug("validated: "+value+" => "+editedValue+((editRecord == null || editRecord.size() == 0) ? "" :"; "+editRecord));
			} else {
				LOG.debug("failed validate: "+value);
			}
		}
		LOG.trace(nexml.toXML());
		normaTemp.mkdirs();
		XMLUtil.debug(nexml, new FileOutputStream(new File(normaTemp, name+".edited.nexml.xml")), 1);
	}

	@Test
	//	@Ignore("too many")
		public void testConvertLabelsAndTreeAndMerge() throws Exception {
			
			for (String root : MergeTipTest.ROOTS) {
				try {
					LOG.debug(root);
					File infile = new File(AMIFixtures.TEST_PHYLO_DIR, "15goodtree/"+root+MergeTipTest.PBM_PNG);
					org.apache.commons.io.FileUtils.copyFile(infile, new File(MergeTipTest.X15GOODTREE+root+MergeTipTest.PNG));
					PhyloTreeArgProcessor phyloTreeArgProcessor = new PhyloTreeArgProcessor();
					phyloTreeArgProcessor.setSpeciesPattern(MergeTipTest.IJSEM);
					phyloTreeArgProcessor.setOutputRoot(root);
					phyloTreeArgProcessor.setOutputDir(new File("target/phylo/combined/15goodtree/"));
					if (!phyloTreeArgProcessor.mergeOCRAndPixelTree(infile)) continue; // tesseract failure
					NexmlNEXML nexml = phyloTreeArgProcessor.getNexml();
					new File(MergeTipTest.X15GOODTREE).mkdirs();
					XMLUtil.debug(nexml, new FileOutputStream(MergeTipTest.X15GOODTREE+root+".nexml.xml"), 1);
					FileUtils.write(new File(MergeTipTest.X15GOODTREE+root+".nwk"), nexml.createNewick());
					XMLUtil.debug(nexml.createSVG(), new FileOutputStream(MergeTipTest.X15GOODTREE+root+".svg"), 1);
					HOCRReader hocrReader = phyloTreeArgProcessor.getOrCreateHOCRReader();
					SVGSVG.wrapAndWriteAsSVG(hocrReader.getOrCreateSVG(), new File(MergeTipTest.X15GOODTREE+root+".words.svg"));
				} catch (Exception e) {
					LOG.debug("error in conversion");
					e.printStackTrace();
				}
				
			}
		}
}