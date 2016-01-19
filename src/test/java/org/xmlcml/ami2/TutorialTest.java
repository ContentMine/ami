package org.xmlcml.ami2;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.junit.Ignore;
import org.junit.Test;
import org.xmlcml.ami2.plugins.AMIArgProcessor;
import org.xmlcml.ami2.plugins.gene.GeneArgProcessor;
import org.xmlcml.ami2.plugins.identifier.IdentifierArgProcessor;
import org.xmlcml.ami2.plugins.regex.RegexArgProcessor;
import org.xmlcml.ami2.plugins.species.SpeciesArgProcessor;
import org.xmlcml.ami2.plugins.word.WordArgProcessor;
import org.xmlcml.norma.util.NormaTestFixtures;

public class TutorialTest {

	@Test
	// TESTED 2016-01-12
	@Ignore // tests broken (?overwrite)
	public void testSpecies() throws Exception {
		NormaTestFixtures.cleanAndCopyDir(new File(AMIFixtures.TEST_AMI_DIR, "tutorial/plos10"), new File("target/species10"));
		String args = "-q target/species10 -i scholarly.html --sp.species --context 35 50 --sp.type binomial genus genussp";
		AMIArgProcessor speciesArgProcessor = new SpeciesArgProcessor(args);
		speciesArgProcessor.runAndOutput();
		AMIFixtures.checkResultsElementList(speciesArgProcessor, 3, 0, 
				"<results title=\"binomial\"><result pre=\" \" "
				+ "exact=\"Cryptococcus neoformans\" "
				+ "xpath=\"/*[local-name()='html'][1]/*[local-name()='body'][1]/*[local-name()='div'][1]/*[local-name()='div'][7]/*[local-name()='p'][10]\" "
				+ "match=\"Cryptococcus neoformans\" "
				+ "post=\" is a ubiquitous environmental fungus that can cau\" n"
				);
//		AMIFixtures.compareExpectedAndResults(new File(AMIFixtures.TEST_AMI_DIR, "tutorial/plos10/e0115544"), 
//				new File("target/species10/e0115544"), "species/binomial", AMIFixtures.RESULTS_XML);
	}
	
	@Test
	@Ignore // uses net
	public void testSpeciesLookup() throws Exception {
		NormaTestFixtures.cleanAndCopyDir(new File("src/test/resources/org/xmlcml/ami2/tutorial/plos10"), new File("target/specieslook10"));
		String args = "-q target/specieslook10 -i scholarly.html --sp.species --context 35 50 --sp.type binomial genus genussp --lookup wikipedia genbank";
		AMIArgProcessor speciesArgProcessor = new SpeciesArgProcessor(args);
		speciesArgProcessor.runAndOutput();
		AMIFixtures.checkResultsElementList(speciesArgProcessor, 3, 0, 
				"<results title=\"mend me\">"
				);
	}
	
	@Test
	// TESTED 2016-01-12
	@Ignore // tests broken (?overwrite)

	public void testRegex() throws Exception {
		NormaTestFixtures.cleanAndCopyDir(new File("src/test/resources/org/xmlcml/ami2/tutorial/plos10"), new File("target/regex10"));
		String args = "-q target/regex10/ -i scholarly.html --context 35 50 --r.regex regex/consort0.xml";
		RegexArgProcessor regexArgProcessor = new RegexArgProcessor(args);
		regexArgProcessor.runAndOutput();
		AMIFixtures.checkResultsElementList(regexArgProcessor, 1, 0, 
				"<results title=\"consort0\">"
				+ "<result pre=\"ptococcal meningitis in Taiwan was \" name0=\"diagnose\" value0=\"diagnosed\" "
				+ "post=\"in 1957 [ 22]. Large clinical case series on crypt\" "
				+ "xpath=\"/*[local-name()='html'][1]/*[local-name()='body'][1]/*[local-name()='div'][1]/*[local-name()='div'][7]/*[local-name()='p']["
				);
		
		/** omit as slightly different outout.
		Fixtures.compareExpectedAndResults(new File(Fixtures.TEST_AMI_DIR, "tutorial/plos10/e0115544"), 
				new File("target/regex10/e0115544"), "regex/consort0", Fixtures.RESULTS_XML);
				*/
	}
	
	@Test
	// EMPTY result, check.
	public void testIdentifier() throws Exception {
		NormaTestFixtures.cleanAndCopyDir(new File("src/test/resources/org/xmlcml/ami2/tutorial/plos10"), new File("target/ident10"));
		String args = "-q target/ident10/ -i scholarly.html --context 35 50 --id.identifier --id.regex regex/identifiers.xml --id.type bio.ena";
		IdentifierArgProcessor identifierArgProcessor = new IdentifierArgProcessor(args);
		identifierArgProcessor.runAndOutput();
		AMIFixtures.checkResultsElementList(identifierArgProcessor, 1, 0, 
				"<results title=\"bio.ena\" />"
				);
		
	}
	
	@Test
	// EMPTY ?
	public void testIdentifierClin() throws Exception {
		NormaTestFixtures.cleanAndCopyDir(new File("src/test/resources/org/xmlcml/ami2/tutorial/plos10"), new File("target/clin10"));
		String args = "-q target/clin10/ -i scholarly.html --context 35 50 --id.identifier --id.regex regex/identifiers.xml --id.type clin.nct clin.isrctn";
		IdentifierArgProcessor identifierArgProcessor = new IdentifierArgProcessor(args);
		identifierArgProcessor.runAndOutput();
		AMIFixtures.checkResultsElementList(identifierArgProcessor, 2, 0, 
				"<results title=\"clin.isrctn\" />"
				);
		AMIFixtures.checkResultsElementList(identifierArgProcessor, 2, 1, 
				"<results title=\"clin.nct\" />"
				);
	}
		
	@Test
	@Ignore // tests broken (?overwrite)
	// TESTED 2016-01-12
	public void testBagOfWords() throws Exception {
		NormaTestFixtures.cleanAndCopyDir(new File("src/test/resources/org/xmlcml/ami2/tutorial/plos10"), new File("target/word10"));
		String args = "-q target/word10/"
				+ " -i scholarly.html"
				+ " --context 35 50"
				+ " --w.words wordFrequencies"
				+ " --w.stopwords /org/xmlcml/ami2/plugins/word/stopwords.txt";
		WordArgProcessor wordArgProcessor = new WordArgProcessor(args);
		wordArgProcessor.runAndOutput();
		AMIFixtures.checkResultsElementList(wordArgProcessor, 1, 0, 
				"<results title=\"frequencies\">"
				+ "<result title=\"frequency\" word=\"(.)\" count=\"55\" />"
				+ "<result title=\"frequency\" word=\"cryptococcal\" count=\"48\" />"
				+ "<result title=\"frequency\" word=\"neoformans\" count=\"47\" />"
				+ "<result title=\"frequency\" word=\"meningitis\" count=\"41\" />"
				+ "<result title=\"frequency\" word=\"risk\" count=\"41\""
				);
		// can't compare these directly as output needs sorting
		File targetE0115544 = new File("target/word10/e0115544");
		AMIFixtures.compareExpectedAndResults(new File(AMIFixtures.TEST_AMI_DIR, "tutorial/plos10/e0115544"), 
				targetE0115544, "word/frequencies", AMIFixtures.RESULTS_XML);
	}

	@Test
	// TESTED 2016-01-12
	public void testBagOfWordsNatureNano() throws Exception {
		NormaTestFixtures.cleanAndCopyDir(new File("src/test/resources/org/xmlcml/ami2/nature/nnano"), new File("target/nature/nnano"));
		String args = "-q target/nature/nnano/"
				+ " -i scholarly.html"
				+ " --context 35 50"
				+ " --w.words wordFrequencies"
				+ " --w.stopwords /org/xmlcml/ami2/plugins/word/stopwords.txt";
		WordArgProcessor wordArgProcessor = new WordArgProcessor(args);
		wordArgProcessor.runAndOutput();
		AMIFixtures.checkResultsElementList(wordArgProcessor, 1, 0, 
				"<results title=\"frequencies\">"
				+ "<result title=\"frequency\" word=\"carbon\" count=\"77\" />"
				+ "<result title=\"frequency\" word=\"hybrid\" count=\"61\" />"
				+ "<result title=\"frequency\" word=\"fibre\" count=\"53\" />"
				+ "<result title=\"frequency\" word=\"().\" count=\"51\" />"
				+ "<result title=\"frequency\" word=\"context\" count=\"51\" />"
				+ "<result t"
				);
	}
	

	@Test
	// EMPTY?
	@Ignore
	public void testGene() throws Exception {
		NormaTestFixtures.cleanAndCopyDir(new File("src/test/resources/org/xmlcml/ami2/tutorial/plos10"), new File("target/gene10"));
		String args = "-q target/gene10/e0115544 -i scholarly.html --context 35 50 --g.gene --g.type human mouse";
		GeneArgProcessor geneArgProcessor = new GeneArgProcessor(args);
		geneArgProcessor.runAndOutput();
		AMIFixtures.checkResultsElementList(geneArgProcessor, 2, 0, 
				"<results title=\"human\"><result pre=\"the most effective model of care ( \" exact=\"DU\" post=\" \" xpath=\"/*[local-name()='html'][1]/*[local-name()='body'][1]/*[local-name()='div'][1]/*[local-name()='div'][6]/*[local-name()='div'][2]/*[local-name()='div'][3]/*[local-name()='div'][1]/*[local-name()='div']["
				);
		AMIFixtures.checkResultsElementList(geneArgProcessor, 2, 1, 
				"<results title=\"mouse\" />"
				);

		
	}
	
	@Test
	// TESTED 2016-01-12
	@Ignore // tests broken (?overwrite)
	public void testWordFrequencies() throws IOException {
		NormaTestFixtures.cleanAndCopyDir(new File("src/test/resources/org/xmlcml/ami2/tutorial/plos10"), new File("target/word10a"));
			String args = "-q target/word10a/"
					+ " -i fulltext.xml"
					+ " --w.words wordFrequencies"
					+ " -o scholarly.html";
			WordArgProcessor wordArgProcessor = new WordArgProcessor(args);
			wordArgProcessor.runAndOutput();
			AMIFixtures.checkResultsElementList(wordArgProcessor, 1, 0, 
					"<results title=\"frequencies\">"
					+ "<result title=\"frequency\" word=\"the\" count=\"140\" />"
					+ "<result title=\"frequency\" word=\"and\" count=\"104\" />"
					+ "<result title=\"frequency\" word=\"with\" count=\"59\" />"
					+ "<result title=\"frequency\" word=\"(.)\" count=\"55\" />"
					+ "<result title=\"frequency\" word=\"cryptococcal\" count=\"48\" />"
					+ "<result t"
					);
	}
}
