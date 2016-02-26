package org.xmlcml.ami2;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.Test;
import org.xmlcml.ami2.plugins.AMIArgProcessor;
import org.xmlcml.ami2.plugins.regex.RegexPluginTest;

public class AMIArgProcessorTest {

	private static final Logger LOG = Logger.getLogger(AMIArgProcessorTest.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	
	@Test
	public void testVersion() {
		AMIArgProcessor argProcessor = new AMIArgProcessor();
		argProcessor.parseArgs("--version");
	}
	
	// utility method to check first part of resultsElementList



}
