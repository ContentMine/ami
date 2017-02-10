package org.xmlcml.ami2;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.Test;
import org.xmlcml.ami2.plugins.word.WordArgProcessor;
import org.xmlcml.cproject.args.DefaultArgProcessor;
import org.xmlcml.cproject.util.CMineTestFixtures;

/** summary of file contents
 * 
 * @author pm286
 *
 */
public class SummaryTest {

	private static final Logger LOG = Logger.getLogger(SummaryTest.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	@Test
	public void testSummarizeSpecies() throws IOException {
		String args;
		File targetDir = new File("target/tutorial/summary");
		CMineTestFixtures.cleanAndCopyDir(new File("src/test/resources/org/xmlcml/ami2/mixed/expected"), targetDir);
		args = "--project "+targetDir+" -i speciesSnippets.xml --xpath //result/@match --summaryfile speciesCount.xml";
		DefaultArgProcessor argProcessor = new DefaultArgProcessor(args); 
		argProcessor.runAndOutput(); 
	}
	
	@Test
	public void testSummarize() throws IOException {
		String args;
		File targetDir = new File("target/tutorial/patents/summary");
		CMineTestFixtures.cleanAndCopyDir(new File(AMIFixtures.TEST_PATENTS_DIR, "US08979"), targetDir);
		args = "--project "+targetDir+" -i scholarly.html"
				+ " --w.words wordFrequencies --w.stopwords /org/xmlcml/ami2/plugins/word/stopwords.txt ";
		new WordArgProcessor(args).runAndOutput();
		args = "--project "+targetDir+" --filter file(**/word/**/results.xml)xpath(//result[@count>20]) -o wordSnippets.xml" ;
		new DefaultArgProcessor(args).runAndOutput(); 

		/** an xpath ending in an attribute returns the value of that.
		 * in addition the software can calculate the count of a sibling @count
		 * 
		 */
		args = "--project "+targetDir+" -i wordSnippets.xml --xpath //result/@word --summaryfile wordCount.xml";
		DefaultArgProcessor argProcessor = new DefaultArgProcessor(args); 
		argProcessor.runAndOutput(); 
	}
	
}
