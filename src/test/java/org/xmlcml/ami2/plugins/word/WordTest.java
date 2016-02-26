package org.xmlcml.ami2.plugins.word;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.Ignore;
import org.junit.Test;
import org.xmlcml.ami2.AMIFixtures;
import org.xmlcml.ami2.plugins.AMIArgProcessor;
import org.xmlcml.cmine.util.CMineTestFixtures;


public class WordTest {

	
	public static final String STOPWORDS_TXT = "/org/xmlcml/ami2/plugins/word/stopwords.txt";
	private static final String CLINICAL_STOPWORDS_TXT = "/org/xmlcml/ami2/plugins/word/clinicaltrials200.txt";
	private static final Logger LOG = Logger.getLogger(WordTest.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	private static final String DATA_16_1_1 = 
			new File(AMIFixtures.TEST_BMC_DIR, "http_www.trialsjournal.com_content_16_1_1").toString();
	private static final String DATA_16_1_1A = 
			new File(AMIFixtures.TEST_BMC_DIR, "http_www.trialsjournal.com_content_16_1_1a").toString();
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
			"-q "+AMIFixtures.TEST_PLOSONE_0115884.toString()+
			" --w.words "+WordArgProcessor.WORD_LENGTHS+" "+WordArgProcessor.WORD_FREQUENCIES+
			" --w.stopwords "+STOPWORDS_TXT+" --w.wordlengths {2,12} --w.wordtypes acronym ";
		new WordArgProcessor(args);
	}
	
	@Test
	public void testWordsRun() {
		String args = 
			"-q "+AMIFixtures.TEST_PLOSONE_0115884.toString()+" --w.words "+WordArgProcessor.WORD_FREQUENCIES + 
			" --w.stopwords "+STOPWORDS_TXT + " --w.wordlengths {2,12} --w.wordtypes abbreviation";
		AMIArgProcessor argProcessor = new WordArgProcessor(args);
		argProcessor.runAndOutput();
		AMIFixtures.checkResultsElementList(argProcessor, 1, 0, 
				"<results title=\"frequencies\"><result title=\"frequency\" word=\"MZB\" count=\"11\" />"
				+ "<result title=\"frequency\" word=\"MVZ\" count=\"8\" />"
				+ "<result title=\"frequency\" word=\"SVL\" count=\"7\" /></results>"
				);
	}
	
	@Test
	public void testSingleFile() throws IOException {
		// SHOWCASE
		String cmd = "-q target/word/16_1_1_test/ -i scholarly.html --context 25 40 "
				+ "--w.words wordLengths wordFrequencies --w.stopwords /org/xmlcml/ami2/plugins/word/stopwords.txt";
		AMIFixtures.runStandardTestHarness(
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
		
		AMIFixtures.runStandardTestHarness(
				new File(DATA_16_1_1A), 
				new File("target/examplestemp1/"), 
				new WordPlugin(),
				cmd,
				"word/frequencies/");

	}

	@Test
	public void testStemming() throws IOException {
		FileUtils.copyDirectory(new File(DATA_16_1_1), new File(TEMP_16_1_1));
		String args =
			"-q "+TEMP_16_1_1+
	" --w.words "+WordArgProcessor.WORD_FREQUENCIES+" --w.stopwords "+STOPWORDS_TXT+" --w.wordlengths {2,12}"+
	" --w.stem true --w.case ignore";
		AMIArgProcessor argProcessor = new WordArgProcessor(args);
		argProcessor.runAndOutput();
		AMIFixtures.checkResultsElementList(argProcessor, 1, 0, 
				"<results title=\"frequencies\"><result title=\"frequency\" word=\"recruit\" count=\"153\" /><result title=\"frequency\" word=\"smoke\" count=\"76\" /><result title=\"frequency\" word=\"particip\" count=\"60\" /><result title=\"frequency\" word=\"invit\" count=\"59\" /><result title=\"frequency\" word=\"research\" count=\"58\" /><r");
	}
	
	@Test
	public void testLowercase() throws IOException {
		FileUtils.copyDirectory(new File(DATA_16_1_1), new File(TEMP_16_1_1));
		String args = 
			"-q "+TEMP_16_1_1+" --w.words "+WordArgProcessor.WORD_FREQUENCIES+
			" --w.stopwords "+STOPWORDS_TXT+" --w.wordlengths {2,12} --w.case ignore";
		AMIArgProcessor argProcessor = new WordArgProcessor(args);
		argProcessor.runAndOutput();
		AMIFixtures.checkResultsElementList(argProcessor, 1, 0, 
				"<results title=\"frequencies\"><result title=\"frequency\" word=\"smoking\" count=\"71\"");
	}

	@Test
	public void testSummarize() throws IOException {
		CMineTestFixtures.cleanAndCopyDir(AMIFixtures.TEST_WORD_EXAMPLES, AMIFixtures.TARGET_EXAMPLES_TEMP_16_1_1);
		String args = 
			"-q  "+AMIFixtures.TARGET_EXAMPLES_TEMP_16_1_1.toString()+" --w.words "+WordArgProcessor.WORD_FREQUENCIES+
			" --w.stopwords "+STOPWORDS_TXT+" --w.case ignore --w.summary aggregate --summaryfile target/examples/";
		AMIArgProcessor argProcessor = new WordArgProcessor(args);
		argProcessor.runAndOutput();
		AMIFixtures.checkResultsElementList(argProcessor, 1, 0, 
				"<results title=\"frequencies\"><result title=\"frequency\" word=\"smoking\" count=\"71\"");
	}


	@Test
	public void testSummarizeDocumentFrequencies() throws IOException {
		CMineTestFixtures.cleanAndCopyDir(AMIFixtures.TEST_WORD_EXAMPLES, AMIFixtures.TARGET_EXAMPLES_TEMP_16_1_1);
		String args = 
			"-q "+AMIFixtures.TARGET_EXAMPLES_TEMP_16_1_1.toString()+" --w.words "+WordArgProcessor.WORD_FREQUENCIES+" --w.stopwords "+STOPWORDS_TXT+" "+CLINICAL_STOPWORDS_TXT+" --w.case ignore --w.summary booleanFrequency --summaryfile target/examples/	--w.wordcount {3,*}";
		AMIArgProcessor argProcessor = new WordArgProcessor(args);
		argProcessor.runAndOutput();
		AMIFixtures.checkResultsElementList(argProcessor, 1, 0, "<results title=\"frequencies\"><result title=\"frequency\" word=\"smoking\" count=\"71\"");
	}

	@Test
//	@Ignore // MEND - Jenkins uses adjectives - have to sort resultsElementList
	public void testWordSearch() throws IOException {
		CMineTestFixtures.cleanAndCopyDir(AMIFixtures.TEST_WORD_EXAMPLES, AMIFixtures.TARGET_EXAMPLES_TEMP_16_1_1);
		String args = 
			"-q "+AMIFixtures.TARGET_EXAMPLES_TEMP_16_1_1.toString()+
			" --w.search searchwords/adjectives.xml searchwords/prepositions.xml --w.stem true" ;
		AMIArgProcessor argProcessor = new WordArgProcessor(args);
		argProcessor.runAndOutput();
		AMIFixtures.checkResultsElementList(argProcessor, 2, 0, 
			    "<results title=\"adjectives\"><result pre=\"19818578 Outcom criteria in smoke cessat trials: propos for a\" exact=\"common\" post=\"standard West R Hajek P Stead L Stapleton J\" /></results>"
				);
		AMIFixtures.checkResultsElementList(argProcessor, 2, 1, 
				"<results title=\"prepositions\"><result pre=\" 1745-6215-16-1 1745-6215 Methodolog Lesson learn\" exact=\"from\" post=\"recruit socioeconom disadvantag smoker into a pilot random control\" /><result pre=\"wish to quit. Method Smoker were recruit through mail invit\" exact=\"from\" post=\"three primari care pract"
				);
	}
	
	@Test
	public void testCompoundWordSearch() throws IOException {
		CMineTestFixtures.cleanAndCopyDir(AMIFixtures.TEST_WORD_EXAMPLES, AMIFixtures.TARGET_EXAMPLES_TEMP_16_1_1);
		String args = 
			"-q "+AMIFixtures.TARGET_EXAMPLES_TEMP_16_1_1.toString()+
			" --w.search searchwords/trials.xml " ;
		AMIArgProcessor argProcessor = new WordArgProcessor(args);
		argProcessor.runAndOutput();
		AMIFixtures.checkResultsElementList(argProcessor, 1, 0, 
		    "<results title=\"trials\"><result pre=\"from recruiting socioeconomically disadvantaged smokers into a pilot "
		    + "randomized controlled\" exact=\"trial\" post=\"to explore the role of Exercise Assisted Reduction then\" />"
		    + "<result pre=\"recruitment. This was done as part of a pilot two-arm\" exact=\"trial\" post=\"of t");

	}
	
	

}
