package org.xmlcml.ami2.plugins;

import java.io.File;
import java.io.IOException;

import org.junit.Test;
import org.xmlcml.ami2.Fixtures;
import org.xmlcml.ami2.plugins.identifier.IdentifierPlugin;
import org.xmlcml.ami2.plugins.regex.RegexPlugin;
import org.xmlcml.ami2.plugins.sequence.SequencePlugin;
import org.xmlcml.ami2.plugins.species.SpeciesPlugin;
import org.xmlcml.ami2.plugins.word.WordPlugin;

/** collection of archetypal tests from each plugin.
 * 
 * These should always run and will use communal resources
 * 
 * @author pm286
 *
 */
public class RegressionDemoTest {

	@Test
	public void testIdentifiersArgProcessor() throws Exception {
		// SHOWCASE
		String cmd = "-q target/examples_16_1_1/ -i scholarly.html --context 25 40 "
				+ "--id.identifier --id.regex regex/identifiers.xml --id.type clin.nct clin.isrctn";
		Fixtures.runStandardTestHarness(
				new File("src/test/resources/org/xmlcml/ami2/regressiondemos/http_www.trialsjournal.com_content_16_1_1/"),
				new File("target/examples_16_1_1/"), 
				new IdentifierPlugin(),
				cmd,
				"identifier/clin.nct/", "identifier/clin.isrctn/");
	}

	/**
	cp -R src/test/resources/org/xmlcml/ami2/regressiondemos/http_www.trialsjournal.com_content_16_1_1/ temp
	ami-identifier -q target/examples_16_1_1/ -i scholarly.html --context 25 40 --id.identifier --id.type clin.nct clin.isrctn"
	
	cp -R src/test/resources/org/xmlcml/ami2/regressiondemos/bmc_trials_15_1_511/ temp
	ami-regex -q target/consort0/15_1_511_test/ -i scholarly.html --context 25 40 --r.regex regex/consort0.xml
	
	cp -R src/test/resources/org/xmlcml/ami2/regressiondemos/journal.pone.0121780/ temp
	ami-sequence --sq.sequence --context 35 50 --sq.type dna prot -q target/plosone/sequences/ -i scholarly.html
	
	cp -R src/test/resources/org/xmlcml/ami2/regressiondemos/journal.pone.0119475/ temp
	ami-species --sp.species --context 35 50 --sp.type binomial genus genussp -q target/plosone/species/malaria -i scholarly.html
	
	cp -R src/test/resources/org/xmlcml/ami2/regressiondemos/http_www.trialsjournal.com_content_16_1_1/ temp
	ami-word -q temp -i scholarly.html --context 25 40 --w.words wordLengths wordFrequencies --w.stopwords /org/xmlcml/ami2/plugins/word/stopwords.txt
*/

	@Test
	public void testRegexHarness() throws IOException {
		// SHOWCASE
		String cmd = "-q target/consort0/15_1_511_test/ -i scholarly.html --context 25 40 --r.regex regex/consort0.xml";
		
		Fixtures.runStandardTestHarness(
				new File("src/test/resources/org/xmlcml/ami2/regressiondemos/bmc_trials_15_1_511/"), 
				new File("target/consort0/15_1_511_test/"), 
				new RegexPlugin(),
				cmd,
				"regex/consort0/");
	}
	
	@Test
	public void testSequenceHarness() throws Exception {
		// SHOWCASE
		String cmd = "--sq.sequence --context 35 50 --sq.type dna prot -q target/plosone/sequences/ -i scholarly.html"; 
		Fixtures.runStandardTestHarness(
				new File("src/test/resources/org/xmlcml/ami2/regressiondemos/journal.pone.0121780/"), 
				new File("target/plosone/sequences/"), 
				new SequencePlugin(),
				cmd,
				"sequence/dna/", "sequence/prot/");
	}

	

	@Test
	public void testSpeciesHarness() throws Exception {
		// SHOWCASE
		String cmd = "--sp.species --context 35 50 --sp.type binomial genus genussp -q target/plosone/species/malaria -i scholarly.html"; 
 
		Fixtures.runStandardTestHarness(
				new File("src/test/resources/org/xmlcml/ami2/regressiondemos/journal.pone.0119475/"),
				new File("target/plosone/species/malaria"), 
				new SpeciesPlugin(),
				cmd,
				"species/binomial/", "species/genus/", "species/genussp/");
	}

	@Test
	public void testWordHarness() throws IOException {
		// SHOWCASE
		String cmd = "-q target/word/16_1_1_test/ -i scholarly.html --context 25 40 --w.words wordLengths wordFrequencies --w.stopwords /org/xmlcml/ami2/plugins/word/stopwords.txt";
		Fixtures.runStandardTestHarness(
				new File("src/test/resources/org/xmlcml/ami2/regressiondemos/http_www.trialsjournal.com_content_16_1_1"), 
				new File("target/word/16_1_1_test/"), 
				new WordPlugin(),
				cmd,
				"word/lengths/", "word/frequencies/");
		

	}


}
