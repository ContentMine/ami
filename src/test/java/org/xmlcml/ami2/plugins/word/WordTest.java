package org.xmlcml.ami2.plugins.word;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.Ignore;
import org.junit.Test;
import org.xmlcml.ami2.Fixtures;
import org.xmlcml.cmine.args.DefaultArgProcessor;

public class WordTest {

	
	public static final String STOPWORDS_TXT = "/org/xmlcml/ami2/plugins/word/stopwords.txt";
	private static final String CLINICAL_STOPWORDS_TXT = "/org/xmlcml/ami2/plugins/word/clinicaltrials200.txt";
	private static final Logger LOG = Logger.getLogger(WordTest.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	private static final String DATA_16_1_1 = 
			new File(Fixtures.TEST_BMC_DIR, "http_www.trialsjournal.com_content_16_1_1").toString();
	private static final String DATA_16_1_1A = 
			new File(Fixtures.TEST_BMC_DIR, "http_www.trialsjournal.com_content_16_1_1a").toString();
	private static final String TEMP_16_1_1 = 
			"target/http_www.trialsjournal.com_content_16_1_1";

	private static final String EXAMPLES =  "examples";
	private static final String EXAMPLES_TEMP = "target/examplestemp";

	@Test
	@Ignore // to avoid output
	public void testWordsHelp() {
		String[] args = {};
		new WordArgProcessor(args);
	}

	@Test
	public void testWords() {
		String args = 
			"-q "+Fixtures.TEST_PLOSONE_0115884.toString()+
			" --w.words "+WordArgProcessor.WORD_LENGTHS+" "+WordArgProcessor.WORD_FREQUENCIES+
			" --w.stopwords "+STOPWORDS_TXT+" --w.wordlengths {2,12} --w.wordtypes acronym ";
		new WordArgProcessor(args);
	}
	
	@Test
	public void testWordsRun() {
		String args = 
			"-q "+Fixtures.TEST_PLOSONE_0115884.toString()+" --w.words "+WordArgProcessor.WORD_FREQUENCIES + " --w.stopwords "+STOPWORDS_TXT + " --w.wordlengths {2,12} --w.wordtypes abbreviation";
		DefaultArgProcessor argProcessor = new WordArgProcessor(args);
		argProcessor.runAndOutput();
	}
	
	@Test
	public void testSingleFile() throws IOException {
		// SHOWCASE
		String cmd = "-q target/word/16_1_1_test/ -i scholarly.html --context 25 40 --w.words wordLengths wordFrequencies --w.stopwords /org/xmlcml/ami2/plugins/word/stopwords.txt";
		Fixtures.runStandardTestHarness(
				new File(DATA_16_1_1), 
				new File("target/word/16_1_1_test/"), 
				new WordPlugin(),
				cmd,
				"word/lengths/", "word/frequencies/");

	}

	@Test
	public void testExamplesFrequencies() throws IOException {
		
		String cmd = "-q target/examplestemp1/ --w.words wordFrequencies "
				+ "--w.stopwords /org/xmlcml/ami2/plugins/word/stopwords.txt /org/xmlcml/ami2/plugins/word/clinicaltrials200.txt";		
		Fixtures.runStandardTestHarness(
				new File(DATA_16_1_1A), 
				new File("target/examplestemp1/"), 
				new WordPlugin(),
				cmd,
				"word/frequencies/");

	}

	@Test
	@Ignore
	public void testStemming() throws IOException {
		FileUtils.copyDirectory(new File(DATA_16_1_1), new File(TEMP_16_1_1));
		String args =
			"-q "+TEMP_16_1_1+
	" --w.words "+WordArgProcessor.WORD_FREQUENCIES+" --w.stopwords "+STOPWORDS_TXT+" --w.wordlengths {2,12}"+
	" --w.stem true";
		DefaultArgProcessor argProcessor = new WordArgProcessor(args);
		argProcessor.runAndOutput();
	}
	
	@Test
	public void testLowercase() throws IOException {
		FileUtils.copyDirectory(new File(DATA_16_1_1), new File(TEMP_16_1_1));
		String args = 
			"-q "+TEMP_16_1_1+" --w.words "+WordArgProcessor.WORD_FREQUENCIES+" --w.stopwords "+STOPWORDS_TXT+" --w.wordlengths {2,12} --w.case ignore";
		DefaultArgProcessor argProcessor = new WordArgProcessor(args);
		argProcessor.runAndOutput();
	}

	@Test
	public void testSummarize() throws IOException {
		if (Fixtures.EXAMPLES_TEMP_16_1_1.exists()) FileUtils.forceDelete(Fixtures.EXAMPLES_TEMP_16_1_1);
		FileUtils.copyDirectory(Fixtures.EXAMPLES, Fixtures.EXAMPLES_TEMP_16_1_1);
		String args = 
			"-q  "+Fixtures.EXAMPLES_TEMP_16_1_1.toString()+" --w.words "+WordArgProcessor.WORD_FREQUENCIES+" --w.stopwords "+STOPWORDS_TXT+" --w.case ignore --w.summary aggregate --summaryfile target/examples/";
		DefaultArgProcessor argProcessor = new WordArgProcessor(args);
		argProcessor.runAndOutput();
	}


	@Test
	public void testSummarizeDocumentFrequencies() throws IOException {
		if (Fixtures.EXAMPLES_TEMP_16_1_1.exists()) FileUtils.forceDelete(Fixtures.EXAMPLES_TEMP_16_1_1);
		FileUtils.copyDirectory(Fixtures.EXAMPLES, Fixtures.EXAMPLES_TEMP_16_1_1);
		String args = 
			"-q "+Fixtures.EXAMPLES_TEMP_16_1_1.toString()+" --w.words "+WordArgProcessor.WORD_FREQUENCIES+" --w.stopwords "+STOPWORDS_TXT+" "+CLINICAL_STOPWORDS_TXT+" --w.case ignore --w.summary booleanFrequency --summaryfile target/examples/	--w.wordcount {3,*}";
		DefaultArgProcessor argProcessor = new WordArgProcessor(args);
		argProcessor.runAndOutput();
	}

}
