package org.xmlcml.ami2.plugins.word;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.Test;
import org.xmlcml.ami2.AMIFixtures;
import org.xmlcml.cmine.util.CMineTestFixtures;
import org.xmlcml.norma.Norma;

public class RawTextTest {

	;
	private static final Logger LOG = Logger.getLogger(RawTextTest.class);

	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	@Test
	public void testSplitSentences() {
		File test = new File(AMIFixtures.TEST_AMI_DIR, "word/sentences");
		String cmd = "--ctree "+test.toString()+" -i simple.txt --w.search "
				+ "searchwords/prepositions.xml -o junk.txt";
		WordArgProcessor argProcessor = new WordArgProcessor(cmd);
		argProcessor.runAndOutput();
	}
	
	@Test
	public void testSearchCochrane() {
		File test = new File(AMIFixtures.TEST_AMI_DIR, "word/sentences/cochrane1");
		String cmd = "--ctree "+test.toString()+" -i fulltext.pdf.txt --w.search "
				+ "searchwords/cochrane.xml -o junk.txt";
		WordArgProcessor argProcessor = new WordArgProcessor(cmd);
		argProcessor.runAndOutput();
	}
	
	@Test
	public void testSearchCochraneProject() {
		File test = new File(AMIFixtures.TEST_AMI_DIR, "word/sentences");
		String cmd = "--project "+test.toString()+" -i fulltext.pdf.txt --w.search "
				+ "searchwords/cochrane.xml -o junk.txt";
		WordArgProcessor argProcessor = new WordArgProcessor(cmd);
		argProcessor.runAndOutput();
	}
	
	@Test
	public void testSearchCochranePDF() throws IOException {
		File test = new File(AMIFixtures.TEST_AMI_DIR, "word/sentences/cochrane3");
		File target = new File("target/normapdf");
		File project = new File(target, "junk");
		CMineTestFixtures.cleanAndCopyDir(test, target);
		String cmd = "-i "+target.toString()+" -e pdf -o "+project+" --ctree";
		Norma norma = new Norma();
		norma.run(cmd);
		
		cmd = "--ctree "+project.toString()+" -i fulltext.pdf --transform pdf2txt -o fulltext.pdf.txt";
		norma = new Norma();
		norma.run(cmd);
		
		cmd = "--project "+project+" -i fulltext.pdf.txt --w.search "
				+ "searchwords/cochrane.xml -o junk.txt";
		WordArgProcessor argProcessor = new WordArgProcessor(cmd);
		argProcessor.runAndOutput();
	}
	
}
