package org.xmlcml.ami2;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.xmlcml.ami2.plugins.identifier.IdentifierPlugin;
import org.xmlcml.ami2.plugins.regex.RegexPlugin;
import org.xmlcml.ami2.plugins.species.SpeciesPlugin;

public class TutorialTest {

	@Test
	public void testSpecies() throws Exception {
		FileUtils.copyDirectory(new File("src/test/resources/org/xmlcml/ami2/tutorial/plos10"), new File("target/species10"));
		String args = "-q target/species10 -i scholarly.html --sp.species --context 35 50 --sp.type binomial genus genussp";
		SpeciesPlugin speciesPlugin = new SpeciesPlugin(args);
		speciesPlugin.runAndOutput();
		
	}
	
	@Test
	public void testRegex() throws Exception {
		FileUtils.copyDirectory(new File("src/test/resources/org/xmlcml/ami2/tutorial/plos10"), new File("target/regex10"));
		String args = "-q target/regex10/ -i scholarly.html --context 35 50 --r.regex regex/consort0.xml";
		RegexPlugin regexPlugin = new RegexPlugin(args);
		regexPlugin.runAndOutput();
		
	}
	
	@Test
	public void testIdentifier() throws Exception {
		FileUtils.copyDirectory(new File("src/test/resources/org/xmlcml/ami2/tutorial/plos10"), new File("target/ident10"));
		String args = "-q target/ident10/ -i scholarly.html --context 35 50 --id.identifier --id.regex regex/identifiers.xml --id.type bio.ena";
		IdentifierPlugin identifierPlugin = new IdentifierPlugin(args);
		identifierPlugin.runAndOutput();
		
	}
	
	@Test
	public void testIdentifierClin() throws Exception {
		FileUtils.copyDirectory(new File("src/test/resources/org/xmlcml/ami2/tutorial/plos10"), new File("target/clin10"));
		String args = "-q target/clin10/ -i scholarly.html --context 35 50 --id.identifier --id.regex regex/identifiers.xml --id.type clin.nct clin.isrctn";
		IdentifierPlugin identifierPlugin = new IdentifierPlugin(args);
		identifierPlugin.runAndOutput();
		
	}
}
