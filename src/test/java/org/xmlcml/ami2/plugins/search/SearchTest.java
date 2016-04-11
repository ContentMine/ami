package org.xmlcml.ami2.plugins.search;

import java.io.IOException;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.Ignore;
import org.junit.Test;
import org.xmlcml.ami2.AMIFixtures;
import org.xmlcml.ami2.plugins.AMIArgProcessor;
import org.xmlcml.cmine.util.CMineTestFixtures;

public class SearchTest {

	private static final Logger LOG = Logger.getLogger(SearchTest.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	@Test
	@Ignore // MEND - Jenkins uses adjectives - have to sort resultsElementList
		public void testWordSearch() throws IOException {
			CMineTestFixtures.cleanAndCopyDir(AMIFixtures.TEST_WORD_EXAMPLES, AMIFixtures.TARGET_EXAMPLES_TEMP_16_1_1);
//			String args = 
//					"-q "+AMIFixtures.TARGET_EXAMPLES_TEMP_16_1_1.toString()+
//					" --sr.search searchwords/adjectives.xml searchwords/prepositions.xml --w.stem true" ;
			String args = 
					"-q "+AMIFixtures.TARGET_EXAMPLES_TEMP_16_1_1.toString()+
					" --sr.search searchwords/adjectives.xml searchwords/prepositions.xml --w.stem true " ;
			AMIArgProcessor argProcessor = new SearchArgProcessor(args);
			LOG.trace("stem "+argProcessor.getStemming());
			argProcessor.runAndOutput();
//			AMIFixtures.checkResultsElementList(argProcessor, 2, 0, 
//				    "<results title=\"adjectives\"><result pre=\"19818578 Outcom criteria in smoke cessat trials: propos for a\" exact=\"common\" post=\"standard West R Hajek P Stead L Stapleton J\" /></results>"
//					);
			AMIFixtures.checkResultsElementList(argProcessor, 2, 0, 
					"<results title=\"adjectives\"><result pre=\"made a quit attempt in the previous 12 months. Another\""
					+ " exact=\"significant\" post=\"difference was that those invited through SSS were more\" />"
					+ "<result pre=\"19818578 Outcome criteria in smoking cessation trials: proposal for a\" exact=\"common\""
					+ " post=\"standard West");
			AMIFixtures.checkResultsElementList(argProcessor, 2, 1, 
					"<results title=\"prepositions\"><result pre=\" 1745-6215-16-1 1745-6215 Methodology Lessons learned\""
					+ " exact=\"from\" post=\"recruiting socioeconomically disadvantaged smokers into a pilot randomized"
					+ " controlled\" /><result pre=\"wishing to quit. Methods Smokers were recruited through mailed"
					+ " invitations\" exact");
		}

	@Test
	public void testCompoundWordSearch() throws IOException {
		CMineTestFixtures.cleanAndCopyDir(AMIFixtures.TEST_WORD_EXAMPLES, AMIFixtures.TARGET_EXAMPLES_TEMP_16_1_1);
		String args = 
			"-q "+AMIFixtures.TARGET_EXAMPLES_TEMP_16_1_1.toString()+
			" --sr.search searchwords/trials.xml " ;
		AMIArgProcessor argProcessor = new SearchArgProcessor(args);
		argProcessor.runAndOutput();
		AMIFixtures.checkResultsElementList(argProcessor, 1, 0, 
		    "<results title=\"trials\"><result pre=\"from recruiting socioeconomically disadvantaged smokers into a pilot "
		    + "randomized controlled\" exact=\"trial\" post=\"to explore the role of Exercise Assisted Reduction then\" />"
		    + "<result pre=\"recruitment. This was done as part of a pilot two-arm\" exact=\"trial\" post=\"of t");
	
	}

}
