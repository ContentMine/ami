package org.xmlcml.ami2;

import java.io.File;
import java.io.IOException;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.xmlcml.ami2.plugins.AMIArgProcessor;
import org.xmlcml.ami2.plugins.CommandProcessor;
import org.xmlcml.ami2.plugins.regex.RegexArgProcessor;
import org.xmlcml.ami2.plugins.word.WordArgProcessor;
import org.xmlcml.ami2.wordutil.WordSetWrapper;
import org.xmlcml.cproject.util.CMineTestFixtures;
import org.xmlcml.norma.NormaArgProcessor;

//@Ignore
public class LargeTests {
	
	File patentsLarge = new File("../patents/US08979");

	@Before
	public void setUp() {
		if (!patentsLarge.exists()) return; // only on PMR machine
		if (!new File(patentsLarge, "US08979000-20150317/scholarly.html").exists()) {
			String args = "-i fulltext.xml  --transform uspto2html -o scholarly.html --project "+patentsLarge;
			NormaArgProcessor argProcessor = new NormaArgProcessor(args);
		}
	}
	
	@Test
	// TESTED 2016-01-12
	@Ignore
	public void testLargeWordFrequencies() {
		if (!patentsLarge.exists()) return; // only on PMR machine
		String args = "-i scholarly.html  --w.words "+WordArgProcessor.WORD_FREQUENCIES+" --w.stopwords "+WordSetWrapper.COMMON_ENGLISH_STOPWORDS_TXT+" --project "+patentsLarge;
		AMIArgProcessor argProcessor = new WordArgProcessor(args);
		argProcessor.runAndOutput();
		AMIFixtures.checkResultsElementList(argProcessor, 1, 0, 
				"<results title=\"frequencies\">"
				+ "<result title=\"frequency\" word=\"applicant\" count=\"427\" />"
				+ "<result title=\"frequency\" word=\"citation:\" count=\"427\" />"
				+ "<result title=\"frequency\" word=\"cited\" count=\"427\" />"
				+ "<result title=\"frequency\" word=\"document-id::\" count=\"279\" />"
				+ "<result title=\"frequency\" word=\"[patcit]:\" ");
	}
	
	@Test
	// TESTED 2016-01-12
	// expensive
	@Ignore
	public void testLargeConsortRegex() {
		String args = "-i scholarly.html  --context 25 40 --r.regex regex/synbio.xml --project "+patentsLarge; 
		RegexArgProcessor argProcessor = new RegexArgProcessor(args);
		argProcessor.runAndOutput();
		AMIFixtures.checkResultsElementList(argProcessor, 1, 0, 
				"<results title=\"synbio\" />");
	}

	@Test
	// TESTED 2016-01-12
	@Ignore
	public void testLargeProject() {
		File large = new File("../patents/US08979");
		if (!large.exists()) return; // only on PMR machine
//		runNorma(large);
		// word frequencies
		String argsx = "-i scholarly.html  --w.words "+WordArgProcessor.WORD_FREQUENCIES+
				" --w.stopwords "+WordSetWrapper.COMMON_ENGLISH_STOPWORDS_TXT+" --w.case ignore --w.stem true --project "+large;
		AMIArgProcessor argProcessor = new WordArgProcessor(argsx);
		argProcessor.runAndOutput();
		AMIFixtures.checkResultsElementList(argProcessor, 1, 0, 
				"<results title=\"frequencies\"><result title=\"frequency\" word=\"applic\" count=\"453\" />"
				+ "<result title=\"frequency\" word=\"citat\" count=\"428\" />");
		}

	@Test
	// TESTED 2016-01-12
	@Ignore // PMR only
	public void testSynbio() {
		File large = new File("../patents/US08979");
		if (!large.exists()) return; // only on PMR machine
//		runNorma(large);
		String args = "-i scholarly.html --clean results/* --sr.search /org/xmlcml/ami2/plugins/synbio/synbio.xml --project "+large;
		AMIArgProcessor argProcessor = new WordArgProcessor(args);
		argProcessor.runAndOutput();
		AMIFixtures.checkResultsElementList(argProcessor, 1, 0, 
				"<results title=\"synbioPhrases\" />");
	}
	
	@Test
	// TESTED 2016-01-12
	@Ignore
	public void testSynbioStem() {
		File large = new File("../patents/US08979");
		if (!large.exists()) return; // only on PMR machine
//		runNorma(large);
		String args = "-i scholarly.html  --w.search /org/xmlcml/ami2/plugins/synbio/synbio.xml --w.stem true --project "+large;
		AMIArgProcessor argProcessor = new WordArgProcessor(args);
		argProcessor.runAndOutput();
		// the last result has no synbio
		AMIFixtures.checkResultsElementList(argProcessor, 1, 0, 
				"<results title=\"synbioPhrases\" />");
	}
	

	@Test
	@Ignore
	public void testWolbachia() throws IOException {
		runDefault("wolbachia2015");
	}

	@Test
	@Ignore // too long
	public void testCurrent() throws IOException {
//		runDefault("zika");
		runDefault("brcancer");
	}

	@Test
	@Ignore // too long and PMR
	public void testDictionary() throws IOException {
		String project = "brcancer";
		File rawDir = new File("../projects/"+project);
		File projectDir = new File("target/tutorial/"+project+"/");
		CMineTestFixtures.cleanAndCopyDir(rawDir, projectDir);
		CommandProcessor commandProcessor = new CommandProcessor(projectDir);
		commandProcessor.processCommands(""
//				+ "species(binomial,genus) "
//				+ " word(search)w.search:/org/xmlcml/ami2/plugins/dictionary/inn.xml_/org/xmlcml/ami2/plugins/dictionary/cochrane.xml"
+ " word(search)w.search:/org/xmlcml/ami2/plugins/dictionary/funders.xml"
+ " word(search)w.search:/org/xmlcml/ami2/plugins/dictionary/disease.xml"
+ " word(search)w.search:/org/xmlcml/ami2/plugins/dictionary/inn.xml"
+ " word(search)w.search:/org/xmlcml/ami2/plugins/dictionary/cochrane.xml"
//+ " word(search)w.search:funders"
//+ " word(search)w.search:disease"
//+ " word(search)w.search:inn"
//+ " word(search)w.search:cochrane"
+ " gene(human)"
				+ "");
		commandProcessor.createDataTables();
		
	}

	@Test
	@Ignore //large
	public void testNano() throws IOException {
		String project = "nano";
		File rawDir = new File("../projects/"+project);
		File projectDir = new File("target/tutorial/"+project+"/");
		CMineTestFixtures.cleanAndCopyDir(rawDir, projectDir);
		CommandProcessor commandProcessor = new CommandProcessor(projectDir);
		commandProcessor.processCommands(""
//				+ "species(binomial,genus) "
//				+ " word(search)w.search:/org/xmlcml/ami2/plugins/dictionary/inn.xml_/org/xmlcml/ami2/plugins/dictionary/cochrane.xml"
+ " word(search)w.search:/org/xmlcml/ami2/plugins/dictionary/funders.xml"
+ " word(search)w.search:/org/xmlcml/ami2/plugins/dictionary/disease.xml"
+ " word(search)w.search:/org/xmlcml/ami2/plugins/dictionary/inn.xml"
+ " word(search)w.search:/org/xmlcml/ami2/plugins/dictionary/cochrane.xml"
+ " gene(human)"
				+ "");
		commandProcessor.createDataTables();
		
	}


	@Test
	@Ignore // too long
	public void testSemipartial() throws IOException {
		runStatisticsDefault("semipartial");
	}
	
	@Test
	@Ignore  // very large
	public void testTrialsLarge() throws IOException {
		String project = "trials/trialsjournal";
		File rawDir = new File("../projects/"+project);
		File projectDir = new File("target/tutorial/"+project+"/");
		CMineTestFixtures.cleanAndCopyDir(rawDir, projectDir);
		CommandProcessor commandProcessor = new CommandProcessor(projectDir);
		commandProcessor.processCommands(""
				+ "species(binomial,genus) "
//				+ "gene(human)"
+ " word(search)w.search:/org/xmlcml/ami2/plugins/dictionary/disease.xml"
+ " word(search)w.search:/org/xmlcml/ami2/plugins/dictionary/inn.xml"
+ " word(search)w.search:/org/xmlcml/ami2/plugins/dictionary/cochrane.xml"
				+ "");
		commandProcessor.createDataTables();
		
	}
		
	@Test
//	@Ignore
	public void testZika10() throws IOException {
		runBioscienceDefault("zika10", new File("src/test/resources/org/xmlcml/ami2/zika10/"));
	}

	@Test
	public void runBespokeDictionary() throws IOException {
		File projectDir = new File("target/tutorial/zika10");
		CommandProcessor commandProcessor = new CommandProcessor(projectDir);
		commandProcessor.processCommands(""
				+ "word(search)w.search:/org/xmlcml/ami2/plugins/dictionary/inn.xml"
				+ "");
		commandProcessor.createDataTables();
	}

	// =============== private support ==============

	private void runDefault(String project) throws IOException {
		File rawDir = new File("../projects/"+project);
		runBioscienceDefault(project, rawDir);
	}

	private void runBioscienceDefault(String project, File rawDir) throws IOException {
		File projectDir = new File("target/tutorial/"+project+"/");
		CMineTestFixtures.cleanAndCopyDir(rawDir, projectDir);
		
		CommandProcessor commandProcessor = new CommandProcessor(projectDir);
		commandProcessor.processCommands(""
				+ "species(binomial,genus) "
				+ " gene(human)"
				+ " word(frequencies)xpath:@count>20~w.stopwords:pmcstop.txt_stopwords.txt"
				+ " sequence(dnaprimer) "
				+ "");
		commandProcessor.createDataTables();
	}

	private void runStatisticsDefault(String project) throws IOException {
		File rawDir = new File("../projects/"+project);
		runStatisticsDefault(project, rawDir);
	}

	private void runStatisticsDefault(String project, File rawDir) throws IOException {
		File projectDir = new File("target/tutorial/"+project+"/");
		CMineTestFixtures.cleanAndCopyDir(rawDir, projectDir);
		
		CommandProcessor commandProcessor = new CommandProcessor(projectDir);
		commandProcessor.processCommands(""
				+ "regex(regex/statistics.xml)"
//				+ " word(frequencies)xpath:@count>20~w.stopwords:pmcstop.txt_stopwords.txt"
				+ " word(search)w.search:/org/xmlcml/ami2/plugins/statistics/statistics.xml"
				+ "");
		commandProcessor.createDataTables();
	}



	

}
