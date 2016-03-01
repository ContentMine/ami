package org.xmlcml.ami2;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.Ignore;
import org.junit.Test;
import org.xmlcml.ami2.plugins.CommandProcessor;

public class TitleTest {

private static final Logger LOG = Logger.getLogger(TitleTest.class);

	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	@Test
	@Ignore // "NYI"
	public void testAddTitlesToRowHeadings() {
		CommandProcessor commandProcessor = AMIFixtures.createDefaultDirectoriesAndProcessor("title");
		commandProcessor.setDefaultCommands("Humgen Spec Genus Primer WordFreq");
	}
}
