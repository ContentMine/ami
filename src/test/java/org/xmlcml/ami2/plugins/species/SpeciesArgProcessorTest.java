package org.xmlcml.ami2.plugins.species;

import java.io.File;

import nu.xom.Builder;
import nu.xom.Element;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;
import org.xmlcml.ami2.Fixtures;
import org.xmlcml.ami2.plugins.AMIArgProcessor;
import org.xmlcml.norma.Norma;

public class SpeciesArgProcessorTest {

	
	
	private static final Logger LOG = Logger.getLogger(SpeciesArgProcessorTest.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	@Test
	public void testSpeciesArgProcessor() throws Exception {
		File newDir = new File("target/plosone/species");
		FileUtils.copyDirectory(Fixtures.TEST_PLOSONE_SEQUENCE_0121780, newDir);
		String args = "--sp.species --context 35 50 --sp.type binomial genus genussp -q "+newDir+" -i scholarly.html"; 
		AMIArgProcessor speciesArgProcessor = new SpeciesArgProcessor(args);
		speciesArgProcessor.runAndOutput();
		Assert.assertTrue("results dir: ", new File(newDir, "results").exists());
		Assert.assertTrue("species dir: ", new File(newDir, "results/species").exists());
		Assert.assertTrue("binomial dir ", new File(newDir, "results/species/binomial").exists());
		File binomialFile = new File(newDir, "results/species/binomial/results.xml");
		Assert.assertTrue("binomial file ", binomialFile.exists());
		Element binomialElement = new Builder().build(binomialFile).getRootElement();
		String binomialXml = binomialElement.toXML().replaceAll("\\s+", " ");
		binomialXml = binomialXml.substring(0,  200);
		/** mend the test
		Assert.assertEquals("binomial file ", "<results title=\"binomial\"> <result pre=\"ntimicrobial activity (assessed on \" match=\"Vibrio harveyi\" post=\" cultures) was limited in both H and WSU samples (\" />"
				+ " <result pre=\"ia genus Vibrio, including", binomialXml);
				*/
	}
	
	@Test
//	@Ignore // accesses net
	public void testSpeciesArgProcessorLookup() throws Exception {
		File newDir = new File("target/plosone/species");
		FileUtils.copyDirectory(Fixtures.TEST_PLOSONE_SEQUENCE_0121780, newDir);
		String args = "--sp.species --context 35 50 --sp.type binomial binomialsp -q "+newDir+" -i scholarly.html --lookup wikipedia"; 
		AMIArgProcessor speciesArgProcessor = new SpeciesArgProcessor(args);
		speciesArgProcessor.runAndOutput();
		File binomialFile = new File(newDir, "results/species/binomial/results.xml");
		Element binomialElement = new Builder().build(binomialFile).getRootElement();
	}
	
	
	@Test
	public void testMalariaArgProcessor() throws Exception {
		File newDir = new File("target/plosone/species/malaria");
		FileUtils.copyDirectory(Fixtures.TEST_PLOSONE_MALARIA_0119475, newDir);
		String args = "--sp.species --context 35 50 --sp.type binomial genus genussp -q "+newDir+" -i scholarly.html"; 
		AMIArgProcessor speciesArgProcessor = new SpeciesArgProcessor(args);
		speciesArgProcessor.runAndOutput();
		Assert.assertTrue("results dir: ", new File(newDir, "results").exists());
		Assert.assertTrue("species dir: ", new File(newDir, "results/species").exists());
		Assert.assertTrue("binomial dir ", new File(newDir, "results/species/binomial").exists());
		File binomialFile = new File(newDir, "results/species/binomial/results.xml");
		Assert.assertTrue("binomial file ", binomialFile.exists());
		Element binomialElement = new Builder().build(binomialFile).getRootElement();
		String binomialXml = binomialElement.toXML().replaceAll("\\s+", " ");
//		binomialXml = binomialXml.substring(0,  200);
		/** mend the test
		Assert.assertEquals("binomial file ", "<results title=\"binomial\"> <result pre=\"ntimicrobial activity (assessed on \" match=\"Vibrio harveyi\" post=\" cultures) was limited in both H and WSU samples (\" />"
				+ " <result pre=\"ia genus Vibrio, including", binomialXml);
				*/
	}
	
	@Test
	public void testMalariaArgProcessorNorma() throws Exception {
		File newDir = new File("target/plosone/species/malaria");
		FileUtils.copyDirectory(Fixtures.TEST_PLOSONE_MALARIA_0119475, newDir);
		String args = "-q "+newDir+" -i fulltext.xml -o scholarly.html -x nlm2html"; 
		Norma norma = new Norma();
		norma.run(args);
	}
	

	
}
