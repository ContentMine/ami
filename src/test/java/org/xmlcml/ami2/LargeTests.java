package org.xmlcml.ami2;

import java.io.File;


import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.xmlcml.ami2.plugins.AMIArgProcessor;
import org.xmlcml.ami2.plugins.regex.RegexArgProcessor;
import org.xmlcml.ami2.plugins.word.WordArgProcessor;
import org.xmlcml.ami2.plugins.word.WordTest;
import org.xmlcml.norma.NormaArgProcessor;

//@Ignore
public class LargeTests {
	
	File large = new File("../patents/US08979");

	@Before
	public void setUp() {
		if (!large.exists()) return; // only on PMR machine
		if (!new File(large, "US08979000-20150317/scholarly.html").exists()) {
			String args = "-i fulltext.xml  --transform uspto2html -o scholarly.html --project "+large;
			NormaArgProcessor argProcessor = new NormaArgProcessor(args);
		}
	}
	
	@Test
	// TESTED 2016-01-12
	@Ignore
	public void testLargeWordFrequencies() {
		if (!large.exists()) return; // only on PMR machine
		String args = "-i scholarly.html  --w.words "+WordArgProcessor.WORD_FREQUENCIES+" --w.stopwords "+WordTest.STOPWORDS_TXT+" --project "+large;
		WordArgProcessor argProcessor = new WordArgProcessor(args);
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
		String args = "-i scholarly.html  --context 25 40 --r.regex regex/synbio.xml --project "+large; 
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
				" --w.stopwords "+WordTest.STOPWORDS_TXT+" --w.case ignore --w.stem true --project "+large;
		AMIArgProcessor argProcessor = new WordArgProcessor(argsx);
		argProcessor.runAndOutput();
		AMIFixtures.checkResultsElementList(argProcessor, 1, 0, 
				"<results title=\"frequencies\"><result title=\"frequency\" word=\"applic\" count=\"453\" />"
				+ "<result title=\"frequency\" word=\"citat\" count=\"428\" />");
		}

	@Test
	// TESTED 2016-01-12
	public void testSynbio() {
		File large = new File("../patents/US08979");
		if (!large.exists()) return; // only on PMR machine
//		runNorma(large);
		String args = "-i scholarly.html --clean results/* --w.search /org/xmlcml/ami2/plugins/synbio/synbio.xml --project "+large;
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
	

}
