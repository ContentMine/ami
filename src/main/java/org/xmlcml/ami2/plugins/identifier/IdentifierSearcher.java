package org.xmlcml.ami2.plugins.identifier;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.ami2.plugins.AMIArgProcessor;
import org.xmlcml.ami2.plugins.AMISearcher;
import org.xmlcml.ami2.plugins.NamedPattern;

public class IdentifierSearcher extends AMISearcher {

	
	public static final Logger LOG = Logger.getLogger(IdentifierSearcher.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	public IdentifierSearcher(AMIArgProcessor argProcessor, NamedPattern namePattern) {
		super(argProcessor, namePattern);
	}


}
