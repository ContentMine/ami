package org.xmlcml.ami2;

import org.junit.Test;
import org.xmlcml.ami2.plugins.AMIArgProcessor;

public class AMIArgProcessorTest {

	
	@Test
	public void testVersion() {
		AMIArgProcessor argProcessor = new AMIArgProcessor();
		argProcessor.parseArgs("--version");
	}
}
