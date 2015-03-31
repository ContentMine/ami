package org.xmlcml.ami2.plugins.identifiers;

import java.io.File;

import nu.xom.Builder;
import nu.xom.Element;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.xmlcml.ami2.Fixtures;
import org.xmlcml.ami2.plugins.AMIArgProcessor;
import org.xmlcml.ami2.plugins.identifier.IdentifierArgProcessor;

public class IdentifierArgProcessorTest {

	
	
	private static final Logger LOG = Logger.getLogger(IdentifierArgProcessorTest.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	@Test
	public void testIdentifierArgProcessor() throws Exception {
		File newDir = new File("target/plosone/identifier");
		FileUtils.copyDirectory(Fixtures.TEST_PLOSONE_SEQUENCE_0121780, newDir);
		String args = "--id.identifier --context 35 50 --id.type ena pdb orcid -q "+newDir+" -i scholarly.html"; 
		AMIArgProcessor identifierArgProcessor = new IdentifierArgProcessor(args);
		identifierArgProcessor.runAndOutput();
		Assert.assertTrue("results dir: ", new File(newDir, "results").exists());
		Assert.assertTrue("identifier dir: ", new File(newDir, "results/identifier").exists());
		Assert.assertTrue("ena dir ", new File(newDir, "results/identifier/ena").exists());
		File enaFile = new File(newDir, "results/identifier/ena/results.xml");
		Assert.assertTrue("ena file ", enaFile.exists());
		Element enaElement = new Builder().build(enaFile).getRootElement();
		String enaXml = enaElement.toXML().replaceAll("\\s+", " ");
		enaXml = enaXml.substring(0,  Math.min(enaXml.length(), 200));
		Assert.assertEquals("ena file ", "<results title=\"ena\"> <result pre=\"ecies. (GenBank accession numbers; \" match=\"GQ906358\" post=\", NR102976 and KC954171), 3 Arcobacter species (NR\" /> <result pre=\"nBank accession numbers; GQ906358, \" ", enaXml);
	}
	
	@Test
	public void testTenIdentifiersArgProcessor() throws Exception {
		File newDir = Fixtures.EXAMPLES_TEMP;
		FileUtils.copyDirectory(Fixtures.EXAMPLES, newDir);
		String args = "--id.identifier --context 35 50 --id.type clin.nct clin.isrctn clin.umin -q "+newDir+" -i scholarly.html"; 
		AMIArgProcessor identifierArgProcessor = new IdentifierArgProcessor(args);
		identifierArgProcessor.runAndOutput();
		Assert.assertTrue("results dir ("+newDir+"): ", newDir.exists());
//		File newFile = new File(newDir, "results/identifier/clin.nct/results.xml");
//		Assert.assertTrue("ena file ", newFile.exists());
//		Element newElement = new Builder().build(newFile).getRootElement();
//		String newXml = newElement.toXML().replaceAll("\\s+", " ");
//		newXml = newXml.substring(0,  Math.min(newXml.length(), 200));
//		Assert.assertEquals("ena file ", "<results title=\"ena\"> <result pre=\"ecies. (GenBank accession numbers; \" match=\"GQ906358\" post=\", NR102976 and KC954171), 3 Arcobacter species (NR\" /> <result pre=\"nBank accession numbers; GQ906358, \" ", newXml);
	}
	
	@Test
	@Ignore // accesses net
	public void testIdentifierArgProcessorLookup() throws Exception {
		File newDir = new File("target/plosone/identifier");
		FileUtils.copyDirectory(Fixtures.TEST_PLOSONE_SEQUENCE_0121780, newDir);
		String args = "--id.identifier --context 35 50 --id.type ena -q "+newDir+" -i scholarly.html --lookup wikipedia"; 
		AMIArgProcessor identifierArgProcessor = new IdentifierArgProcessor(args);
		identifierArgProcessor.runAndOutput();
		File enaFile = new File(newDir, "results/identifier/ena/results.xml");
		Element enaElement = new Builder().build(enaFile).getRootElement();
	}
	
}
