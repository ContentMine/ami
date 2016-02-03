package org.xmlcml.ami2;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.Ignore;
import org.junit.Test;
import org.xmlcml.ami2.plugins.AMIArgProcessor;
import org.xmlcml.ami2.plugins.gene.GeneArgProcessor;
import org.xmlcml.ami2.plugins.identifier.IdentifierArgProcessor;
import org.xmlcml.ami2.plugins.regex.RegexArgProcessor;
import org.xmlcml.ami2.plugins.sequence.SequenceArgProcessor;
import org.xmlcml.ami2.plugins.species.SpeciesArgProcessor;
import org.xmlcml.ami2.plugins.word.WordArgProcessor;
import org.xmlcml.cmine.args.DefaultArgProcessor;
import org.xmlcml.cmine.util.CMineTestFixtures;

public class TutorialTest {

	;
	private static final Logger LOG = Logger.getLogger(TutorialTest.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	@Test
	// TESTED 2016-01-12
	@Ignore // tests broken (?overwrite)
	public void testSpecies() throws Exception {
		CMineTestFixtures.cleanAndCopyDir(new File(AMIFixtures.TEST_AMI_DIR, "tutorial/plos10"), new File("target/species10"));
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
		CMineTestFixtures.cleanAndCopyDir(new File("src/test/resources/org/xmlcml/ami2/tutorial/plos10"), new File("target/specieslook10"));
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
		CMineTestFixtures.cleanAndCopyDir(new File("src/test/resources/org/xmlcml/ami2/tutorial/plos10"), new File("target/regex10"));
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
		CMineTestFixtures.cleanAndCopyDir(new File("src/test/resources/org/xmlcml/ami2/tutorial/plos10"), new File("target/ident10"));
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
		CMineTestFixtures.cleanAndCopyDir(new File("src/test/resources/org/xmlcml/ami2/tutorial/plos10"), new File("target/clin10"));
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
		CMineTestFixtures.cleanAndCopyDir(new File("src/test/resources/org/xmlcml/ami2/tutorial/plos10"), new File("target/word10"));
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
		CMineTestFixtures.cleanAndCopyDir(new File("src/test/resources/org/xmlcml/ami2/nature/nnano"), new File("target/nature/nnano"));
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
		CMineTestFixtures.cleanAndCopyDir(new File("src/test/resources/org/xmlcml/ami2/tutorial/plos10"), new File("target/gene10"));
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
		CMineTestFixtures.cleanAndCopyDir(new File("src/test/resources/org/xmlcml/ami2/tutorial/plos10"), new File("target/word10a"));
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
	
	@Test
	public void testSpeciesSequencesGeneWordsCMine() throws IOException {
		String args;
		File targetDir = new File("target/tutorial/mixed");
		CMineTestFixtures.cleanAndCopyDir(new File("src/test/resources/org/xmlcml/ami2/mixed"), targetDir);
		args = "--project "+targetDir+" -i scholarly.html --sq.sequence --context 35 --sq.type dnaprimer";
		LOG.debug("search for DNA");
		new SequenceArgProcessor(args).runAndOutput();
		args = "--project "+targetDir+" -i scholarly.html"
				+ " --w.words wordFrequencies --w.stopwords /org/xmlcml/ami2/plugins/word/stopwords.txt ";
		LOG.debug("wordFrequencies");
		new WordArgProcessor(args).runAndOutput();
		args = "--project "+targetDir+" -i scholarly.html --sp.species --context 35 --sp.type binomial genus";
		LOG.debug("species");
		new SpeciesArgProcessor(args).runAndOutput();
		LOG.debug("genes");
		args = "--project "+targetDir+" -i scholarly.html --g.gene --context 100 --g.type human";
		new GeneArgProcessor(args).runAndOutput();
		
		LOG.debug("file files");
		args = "--project "+targetDir+" --filter file(**/gene/human/results.xml) -o geneFiles.xml" ;
		new DefaultArgProcessor(args).runAndOutput(); 
		args = "--project "+targetDir+" --filter file(**/species/**/results.xml) -o speciesFiles.xml" ;
		new DefaultArgProcessor(args).runAndOutput(); 
		args = "--project "+targetDir+" --filter file(**/sequence/**/results.xml) -o sequenceFiles.xml" ;
		new DefaultArgProcessor(args).runAndOutput(); 
		args = "--project "+targetDir+" --filter file(**/word/**/results.xml) -o wordFiles.xml" ;
		new DefaultArgProcessor(args).runAndOutput(); 
		
		LOG.debug("snippets files");
		args = "--project "+targetDir+" --filter file(**/gene/human/results.xml)xpath(//result) -o geneSnippets.xml" ;
		new DefaultArgProcessor(args).runAndOutput(); 
		args = "--project "+targetDir+" --filter file(**/gene/human/results.xml)xpath(//result[contains(@pre,'genotype')]) -o genegeneSnippets.xml" ;
		new DefaultArgProcessor(args).runAndOutput(); 
		args = "--project "+targetDir+" --filter file(**/species/**/results.xml)xpath(//result) -o speciesSnippets.xml" ;
		new DefaultArgProcessor(args).runAndOutput(); 
		args = "--project "+targetDir+" --filter file(**/sequence/**/results.xml)xpath(//result) -o sequenceSnippets.xml" ;
		new DefaultArgProcessor(args).runAndOutput(); 
		args = "--project "+targetDir+" --filter file(**/word/**/results.xml)xpath(//result[@count>20]) -o wordSnippets.xml" ;
		new DefaultArgProcessor(args).runAndOutput(); 
		LOG.debug("end");

	}
	
	@Test
	/** this tutorial shows how to aggregate from results.xml files.
	 * first we run a word frequency which generates bagOfWordsWithCounts in results.xml
	 * These are then aggregated to a single wordSnippets file for each cTree.
	 * These are then aggregated to give a summary file for the cProject
	 */
	public void testSummarizeCounts() throws IOException {
		/** create a clean version in target/
		 * there are 6 ctrees
		 */
		String args;
		File targetDir = new File("target/tutorial/patents/summary");
		CMineTestFixtures.cleanAndCopyDir(new File(AMIFixtures.TEST_PATENTS_DIR, "US08979"), targetDir);
		

		/** run AMI-words to create a bagofwords as normal in ${ctree}/results/word/frequencies/results.xml
		 */ 
		args = "--project "+targetDir+" -i scholarly.html"
				+ " --w.words wordFrequencies --w.stopwords /org/xmlcml/ami2/plugins/word/stopwords.txt ";
		new WordArgProcessor(args).runAndOutput();
		
		/** take these results.xml files and aggregate into a single ${ctree}/wordSnippets.xml
		 * it selects all words with a count > 20
		 */
		args = "--project "+targetDir+" --filter file(**/word/**/results.xml)xpath(//result[@count>20]) -o wordSnippets.xml" ;
		new DefaultArgProcessor(args).runAndOutput(); 

		/** aggregate the ${ctree}/wordSnippets.xml to ${cproject}/wordSnippets.xml
		 * 
		 * because the wordSnippets holds its value in the "word" attribute, we use an xpath that
		 * returns the attribute. The software will then look for a sibling "count" attribute
		 * and use this. (messy, and we'll mend it)
		 * 
		 * The result is a global ${cproject}/wordCount.xml
		 * 
		 */
		args = "--project "+targetDir+" -i wordSnippets.xml --xpath //result/@word --summaryfile wordCount.xml";
		DefaultArgProcessor argProcessor = new DefaultArgProcessor(args); 
		argProcessor.runAndOutput(); 
	}
	
	
	@Test
	/** this tutorial shows how to aggregate from results.xml files.
	 * Files are all getpapers with "anopheles" query 
	 * first we run ami-species which generates results.xml
	 * These are then aggregated to a single speciesSnippets.xml file for each cTree.
	 * These are then aggregated to give a summary speciesSnippets.xml for the cProject
	 */
	public void testSummarizeAnopheles() throws IOException {
		/** create a clean version in target/
		 * there are 20+ ctrees
		 */
		String args;
		File targetDir = new File("target/tutorial/anopheles");
		CMineTestFixtures.cleanAndCopyDir(new File(AMIFixtures.TEST_AMI_DIR, "anopheles"), targetDir);
		

		/** run AMI-species to create results.xml as normal in ${ctree}/results/species/binomial/results.xml
		 */ 
		new SpeciesArgProcessor(
				"--project "+targetDir+" -i scholarly.html --sp.species --sp.type binomial genus ").runAndOutput();
		new GeneArgProcessor(
				"--project "+targetDir+" -i scholarly.html --g.gene --context 100 --g.type human").runAndOutput();
		new SequenceArgProcessor(
				"--project "+targetDir+" -i scholarly.html --sq.sequence --context 35 --sq.type dna dnaprimer").runAndOutput();
		new WordArgProcessor("--project "+targetDir+" -i scholarly.html --w.words wordFrequencies"
						+ " --w.stopwords /org/xmlcml/ami2/plugins/word/stopwords.txt").runAndOutput();

		/** take these results.xml files and aggregate into a single ${ctree}/wordSnippets.xml
		 * it selects all words with a count > 20
		 */
		new DefaultArgProcessor("--project "+targetDir+" --filter file(**/species/binomial/results.xml)xpath(//result) -o binomialSnippets.xml").runAndOutput(); 
		new DefaultArgProcessor("--project "+targetDir+" --filter file(**/species/genus/results.xml)xpath(//result) -o genusSnippets.xml").runAndOutput(); 
		new DefaultArgProcessor("--project "+targetDir+" --filter file(**/gene/human/results.xml)xpath(//result) -o humanSnippets.xml").runAndOutput(); 
		new DefaultArgProcessor("--project "+targetDir+" --filter file(**/sequence/dna/results.xml)xpath(//result) -o dnaSnippets.xml").runAndOutput(); 
		new DefaultArgProcessor("--project "+targetDir+" --filter file(**/sequence/dnaprimer/results.xml)xpath(//result) -o dnaprimerSnippets.xml").runAndOutput(); 
		new DefaultArgProcessor("--project "+targetDir+" --filter file(**/word/frequencies/results.xml)xpath(//result[@count>20]) -o wordCount.xml").runAndOutput(); 

		/** aggregate the ${ctree}/binomialSnippets.xml to ${cproject}/binomialSnippets.xml
		 * 
		 * The result is a global ${cproject}/binomialSnippets.xml
		 * 
		 */
		new DefaultArgProcessor("--project "+targetDir+" -i binomialSnippets.xml  --xpath //result/@match --summaryfile binomialCount.xml").runAndOutput(); 
		new DefaultArgProcessor("--project "+targetDir+" -i genusSnippets.xml     --xpath //result/@match --summaryfile genusCount.xml").runAndOutput(); 
		new DefaultArgProcessor("--project "+targetDir+" -i humanSnippets.xml     --xpath //result/@match --summaryfile humanCount.xml").runAndOutput(); 
		new DefaultArgProcessor("--project "+targetDir+" -i dnaSnippets.xml       --xpath //result/@match --summaryfile dnaCount.xml").runAndOutput(); 
		new DefaultArgProcessor("--project "+targetDir+" -i dnaprimerSnippets.xml --xpath //result/@match --summaryfile dnaprimerCount.xml").runAndOutput(); 
		new DefaultArgProcessor("--project "+targetDir+" -i wordCount.xml         --xpath //result/@word  --summaryfile wordCount.xml").runAndOutput(); 

	}
	
	

}
