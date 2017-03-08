package org.xmlcml.ami2.plugins.word;

import java.io.File;
import java.io.IOException;

import org.junit.Test;
import org.xmlcml.ami2.AMIFixtures;
import org.xmlcml.ami2.plugins.AMIArgProcessor;
import org.xmlcml.ami2.plugins.search.SearchArgProcessor;
import org.xmlcml.cproject.util.CMineTestFixtures;
import org.xmlcml.norma.util.NormaTestFixtures;

public class SynbioWordTest {

	@Test
	public void testSynbioDictionary1() throws IOException {
		File targetDir = new File("target/synbio/US08979000-20150317/");
		CMineTestFixtures.cleanAndCopyDir(new File(AMIFixtures.TEST_PATENTS_DIR, "US08979/US08979000-20150317"), targetDir);
		NormaTestFixtures.runNorma(targetDir, "project", "uspto2html"); // writes to test dir
		String args = "-i scholarly.html"
				+ " --sr.search /org/xmlcml/ami2/plugins/synbio/synbio0.xml"
				+ " --w.stem true"
				+ " --w.case ignore"    
				+ " -q "+targetDir; 
		AMIArgProcessor argProcessor = new SearchArgProcessor(args);
		argProcessor.runAndOutput();
		AMIFixtures.checkResultsElementList(argProcessor, 1, 0, 
				"<results title=\"synbio\">"
				+ "<result pre=\"hydrogen atom or function radic ha been replac with an\" exact=\"amino\" "
				+ "post=\"group; (7) ani liquid includ a substanc in which,\" />"
				+ "<result pre=\"such as surfactant, saline, saccharide, organ acid, inorgan acid, and\" "
				+ "exact=\"amino\" post=\"acid; and (16) ani liquid mixt"
				);

	}

	@Test
	public void testSynbioDictionaryMany() throws IOException {
		File targetDir = new File("target/synbio/US08979");
		CMineTestFixtures.cleanAndCopyDir(new File(AMIFixtures.TEST_PATENTS_DIR, "US08979"), targetDir);
		NormaTestFixtures.runNorma(targetDir, "project", "uspto2html"); // writes to test dir
		String args = "-i scholarly.html"
				+ " --sr.search /org/xmlcml/ami2/plugins/synbio/synbio0.xml"
				+ " --project "+targetDir; 
		AMIArgProcessor argProcessor = new SearchArgProcessor(args);
		argProcessor.runAndOutput();
		AMIFixtures.checkResultsElementList(argProcessor, 1, 0, 
				"<results title=\"synbio\" />"
				);

	}

	@Test
	public void testSynbioDictionaryManyPhrase() throws IOException {
		File targetDir = new File("target/synbio/US08979");
		CMineTestFixtures.cleanAndCopyDir(new File(AMIFixtures.TEST_PATENTS_DIR, "US08979"), targetDir);
		NormaTestFixtures.runNorma(targetDir, "project", "uspto2html"); // writes to test dir
		String args = "-i scholarly.html"
				+ " --sr.search /org/xmlcml/ami2/plugins/synbio/synbio.xml"
				+ " --project "+targetDir; 
		AMIArgProcessor argProcessor = new SearchArgProcessor(args);
		argProcessor.runAndOutput();
		AMIFixtures.checkResultsElementList(argProcessor, 1, 0, 
				"<results title=\"synbioPhrases\" />"
//				"<results title=\"synbioPhrases\">"
//				+ "<result pre=\"BACKGROUND OF THE INVENTION 1. Field of the Invention The\" exact=\"present invention\" "
//				+ "post=\"relates to a process for recycling used absorbent sanitary\" />"
//				+ "<result pre=\"sanitary products. SUMMARY OF THE INVENTION The object of the\" "
//				+ "exact=\"present invention\" p"
				);

	}

}
