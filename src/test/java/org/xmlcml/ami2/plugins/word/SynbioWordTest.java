package org.xmlcml.ami2.plugins.word;

import java.io.File;
import java.io.IOException;

import org.junit.Test;
import org.xmlcml.ami2.AMIFixtures;
import org.xmlcml.ami2.plugins.gene.GeneArgProcessor;
import org.xmlcml.norma.util.NormaTestFixtures;

public class SynbioWordTest {

	@Test
	public void testSynbioDictionary() throws IOException {
		File targetDir = new File("target/synbio");
		NormaTestFixtures.cleanAndCopyDir(new File(AMIFixtures.TEST_PATENTS_DIR, "US08979"), targetDir);
		NormaTestFixtures.runNorma(targetDir, "project", "uspto2html"); // writes to test dir
		String args = "-i scholarly.html --w.words "
				+ "--c.dictionary /org/xmlcml/ami2/plugins/synbio/synbio0.xml"
				+ " --project "+targetDir; 
		GeneArgProcessor argProcessor = new GeneArgProcessor(args);
		argProcessor.runAndOutput();
		AMIFixtures.checkResultsElementList(argProcessor, 0, 0, 
				"<results title=\"synbio\" />"
				);

	}

}
