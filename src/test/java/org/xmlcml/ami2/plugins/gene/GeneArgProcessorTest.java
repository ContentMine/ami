package org.xmlcml.ami2.plugins.gene;

import java.io.File;

import nu.xom.Builder;
import nu.xom.Element;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.Ignore;
import org.junit.Test;
import org.xmlcml.ami2.Fixtures;
import org.xmlcml.cmine.args.DefaultArgProcessor;

public class GeneArgProcessorTest {

	
	
	private static final Logger LOG = Logger.getLogger(GeneArgProcessorTest.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
//	@Test
//	public void testGeneArgProcessor() throws Exception {
//		File newDir = new File("target/plosone/species");
//		FileUtils.copyDirectory(Fixtures.TEST_PLOSONE_SEQUENCE_0121780, newDir);
//		String args = "--sp.species --context 35 50 --sp.type binomial genus genussp -q "+newDir+" -i scholarly.html"; 
//		AMIArgProcessor speciesArgProcessor = new GeneArgProcessor(args);
//		speciesArgProcessor.runAndOutput();
//		Assert.assertTrue("results dir: ", new File(newDir, "results").exists());
//		Assert.assertTrue("species dir: ", new File(newDir, "results/species").exists());
//		Assert.assertTrue("binomial dir ", new File(newDir, "results/species/binomial").exists());
//		File binomialFile = new File(newDir, "results/species/binomial/results.xml");
//		Assert.assertTrue("binomial file ", binomialFile.exists());
//		Element binomialElement = new Builder().build(binomialFile).getRootElement();
//		String binomialXml = binomialElement.toXML().replaceAll("\\s+", " ");
//		binomialXml = binomialXml.substring(0,  200);
//		/** mend the test
//		Assert.assertEquals("binomial file ", "<results title=\"binomial\"> <result pre=\"ntimicrobial activity (assessed on \" match=\"Vibrio harveyi\" post=\" cultures) was limited in both H and WSU samples (\" />"
//				+ " <result pre=\"ia genus Vibrio, including", binomialXml);
//				*/
//	}
	
	@Test
	@Ignore // accesses net
	public void testGeneArgProcessorLookup() throws Exception {
		File newDir = new File("target/plosone/species");
		FileUtils.copyDirectory(Fixtures.TEST_PLOSONE_SEQUENCE_0121780, newDir);
		String args = "--sp.species --context 35 50 --sp.type binomial binomialsp -q "+newDir+" -i scholarly.html --lookup wikipedia"; 
		DefaultArgProcessor speciesArgProcessor = new GeneArgProcessor(args);
		speciesArgProcessor.runAndOutput();
		File binomialFile = new File(newDir, "results/species/binomial/results.xml");
		Element binomialElement = new Builder().build(binomialFile).getRootElement();
	}
	
	
//	@Test
//	public void testMalariaArgProcessor() throws Exception {
//		File newDir = new File("target/plosone/species/malaria");
//		FileUtils.copyDirectory(Fixtures.TEST_PLOSONE_MALARIA_0119475, newDir);
//		String args = "--sp.species --context 35 50 --sp.type binomial genus genussp -q "+newDir+" -i scholarly.html"; 
//		AMIArgProcessor speciesArgProcessor = new GeneArgProcessor(args);
//		speciesArgProcessor.runAndOutput();
//		Assert.assertTrue("results dir: ", new File(newDir, "results").exists());
//		Assert.assertTrue("species dir: ", new File(newDir, "results/species").exists());
//		Assert.assertTrue("binomial dir ", new File(newDir, "results/species/binomial").exists());
//		File binomialFile = new File(newDir, "results/species/binomial/results.xml");
//		Assert.assertTrue("binomial file ", binomialFile.exists());
//		Element binomialElement = new Builder().build(binomialFile).getRootElement();
//		String binomialXml = binomialElement.toXML().replaceAll("\\s+", " ");
////		binomialXml = binomialXml.substring(0,  200);
//		/** mend the test
//		Assert.assertEquals("binomial file ", "<results title=\"binomial\"> <result pre=\"ntimicrobial activity (assessed on \" match=\"Vibrio harveyi\" post=\" cultures) was limited in both H and WSU samples (\" />"
//				+ " <result pre=\"ia genus Vibrio, including", binomialXml);
//				*/
//	}

	@Test
	public void testGeneHarness() throws Exception {
		// SHOWCASE
		String cmd = "--g.gene --context 35 50 --g.type human -q target/plosone/gene/ -i scholarly.html"; 
 
		Fixtures.runStandardTestHarness(
				new File("./src/test/resources/org/xmlcml/ami2/plosone/journal.pone.0008887/"), 
				new File("target/plosone/gene/"), 
				new GenePlugin(),
				cmd,
				"gene/human/");
	}



	
}
