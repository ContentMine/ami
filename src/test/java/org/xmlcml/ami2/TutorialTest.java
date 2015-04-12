package org.xmlcml.ami2;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.xmlcml.ami2.plugins.species.SpeciesPlugin;

public class TutorialTest {

	@Test
	public void testSpecies() throws Exception {
		FileUtils.copyDirectory(new File("src/test/resources/org/xmlcml/ami2/tutorial/plos10"), new File("target/species10"));
		String args = "-q target/species10 -i scholarly.html --sp.species --context 35 50 --sp.type binomial genus genussp";
		SpeciesPlugin speciesPlugin = new SpeciesPlugin(args);
		speciesPlugin.runAndOutput();
		
	}
}
