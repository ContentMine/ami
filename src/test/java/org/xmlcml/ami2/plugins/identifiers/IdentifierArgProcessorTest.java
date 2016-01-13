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
import org.xmlcml.ami2.AMIFixtures;
import org.xmlcml.ami2.plugins.identifier.IdentifierArgProcessor;
import org.xmlcml.ami2.plugins.identifier.IdentifierPlugin;
import org.xmlcml.cmine.args.DefaultArgProcessor;

// TESTED 2016-01-12
public class IdentifierArgProcessorTest {

	
	
	private static final Logger LOG = Logger.getLogger(IdentifierArgProcessorTest.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	@Test
	// TESTED 2016-01-12
	public void testIdentifierArgProcessorBio() throws Exception {
		File newDir = new File("target/plosone/identifier");
		FileUtils.copyDirectory(AMIFixtures.TEST_PLOSONE_SEQUENCE_0121780, newDir);
		String args = "--id.identifier --context 35 50 --id.regex regex/identifiers.xml --id.type bio.ena bio.pdb meta.orcid -q "+newDir+" -i scholarly.html"; 
		IdentifierArgProcessor identifierArgProcessor = new IdentifierArgProcessor(args);
		identifierArgProcessor.runAndOutput();
		AMIFixtures.checkResultsElementList(identifierArgProcessor, 3, 0, 
				"<results title=\"bio.ena\">"
				+ "<result pre=\"ecies. (GenBank accession numbers; \" "
				+ "exact=\"GQ906358\" "
				+ "post=\", NR102976 and KC954171), 3 Arcobacter species (NR\" "
				+ "xpath=\"/*[local-name()='html'][1]/*[local-name()='body'][1]/*[local-name()='div'][1]/*[local-name()='div'][6]/*[local-name()='div'][1]/*[local-name()="
				);
		AMIFixtures.checkResultsElementList(identifierArgProcessor, 3, 1, 
				"<results title=\"bio.pdb\" />"
				);
		AMIFixtures.checkResultsElementList(identifierArgProcessor, 3, 2, 
				"<results title=\"meta.orcid\" />"
				);

	}
	
	@Test
	@Ignore // broken due to empty.xml
	public void testIdentifiersArgProcessorTrials() throws Exception {
		// SHOWCASE
		String cmd = "-q target/examples_16_1_1/ -i scholarly.html --context 25 40 "
				+ "--id.identifier --id.regex regex/identifiers.xml --id.type clin.nct clin.isrctn";
		AMIFixtures.runStandardTestHarness(
				AMIFixtures.TEST_TRIALS_16_1_1, 
				new File("target/examples_16_1_1/"), 
				new IdentifierPlugin(),
				cmd,
				"identifier/clin.nct/", "identifier/clin.isrctn/");
	}
	
	@Test
	// TESTED 2016-01-12
	public void testIdentifiersArgProcessorTrials11() throws Exception {
		// SHOWCASE
		File newDir = new File("target/examples_16_1_1/");
		FileUtils.copyDirectory(AMIFixtures.TEST_TRIALS_16_1_1, newDir);
		String args = "-q "+newDir+" -i scholarly.html --context 25 40 "
				+ "--id.identifier --id.regex regex/identifiers.xml --id.type clin.nct clin.isrctn";
		IdentifierArgProcessor identifierArgProcessor = new IdentifierArgProcessor(args);
		identifierArgProcessor.runAndOutput();
		AMIFixtures.checkResultsElementList(identifierArgProcessor, 2, 0, 
				"<results title=\"clin.isrctn\">"
				+ "<result pre=\"Controlled Trial Number (\" exact=\"ISRCTN): 13837944\" "
				+ "post=\", UK Clinical Research Network (UKCRN) S\" "
				+ "xpath=\"/*[local-name()='html'][1]/*[local-name()='body'][1]/*[local-name()='div'][8]/*[local-name()='p'][4]\" "
				+ "name=\"clin.isrctn\" /></results>"
				);
		AMIFixtures.checkResultsElementList(identifierArgProcessor, 2, 1, 
				"<results title=\"clin.nct\" />"
				);
	}

	
	@Test
	@Ignore // accesses net
	public void testIdentifierArgProcessorLookup() throws Exception {
		File newDir = new File("target/plosone/identifier");
		FileUtils.copyDirectory(AMIFixtures.TEST_PLOSONE_SEQUENCE_0121780, newDir);
		String args = "--id.identifier --context 35 50 --id.type ena -q "+newDir+" -i scholarly.html --lookup wikipedia"; 
		IdentifierArgProcessor identifierArgProcessor = new IdentifierArgProcessor(args);
		identifierArgProcessor.runAndOutput();
		AMIFixtures.checkResultsElementList(identifierArgProcessor, 1, 0, 
				"<results title=\"mend me\">"
				);
	}
	
	@Test
	// TESTED 2016-01-12
	public void testNewIdentifierWithEna() throws Exception {
		File newDir = new File("target/plosone/identifier");
		FileUtils.copyDirectory(AMIFixtures.TEST_PLOSONE_SEQUENCE_0121780, newDir);
		String args = "--id.identifier --context 35 50 --id.regex regex/identifiers.xml --id.type bio.ena -q "+newDir+" -i scholarly.html"; 
		IdentifierArgProcessor identifierArgProcessor = new IdentifierArgProcessor(args);
		identifierArgProcessor.runAndOutput();
		AMIFixtures.checkResultsElementList(identifierArgProcessor, 1, 0, 
				"<results title=\"bio.ena\">"
				+ "<result pre=\"ecies. (GenBank accession numbers; \" exact=\"GQ906358\""
				+ " post=\", NR102976 and KC954171), 3 Arcobacter species (NR\" xpath=\"/*[local-name()='html'][1]/*[local-name()='body'][1]/*[local-name()='div'][1]/*[local-name()='div'][6]/*[local-name()='div'][1]/*[local-name()="
				);

	}
	
	@Test
	// TESTED 2016-01-12
	// we can't use examples/
	@Ignore
	public void testSeveralArticlesWithClin() throws Exception {
		File newDir = new File("target/plosone/identifiers");
		FileUtils.copyDirectory(new File("examples/"), newDir);
		String args = "--id.identifier --context 35 50 --id.regex regex/identifiers.xml --id.type clin.nct clin.isrctn -q "+newDir+" -i scholarly.html"; 
		IdentifierArgProcessor identifierArgProcessor = new IdentifierArgProcessor(args);
		identifierArgProcessor.runAndOutput();
		AMIFixtures.checkResultsElementList(identifierArgProcessor, 2, 0, 
				"<results title=\"clin.nct\" />" // empty?
				);
		AMIFixtures.checkResultsElementList(identifierArgProcessor, 2, 1, 
				"<results title=\"clin.isrctn\" />" // empty?
				);
	}
	
}
