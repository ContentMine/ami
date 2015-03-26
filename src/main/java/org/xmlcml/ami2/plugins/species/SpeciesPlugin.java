package org.xmlcml.ami2.plugins.species;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.ami2.plugins.AMIArgProcessor;
import org.xmlcml.ami2.plugins.AMIPlugin;

/** test plugin.
 * 
 * Very simple tasks for testing and tutorials.
 * 
 * @author pm286
 *
 */
public class SpeciesPlugin extends AMIPlugin {

	private static final Logger LOG = Logger.getLogger(SpeciesPlugin.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	public SpeciesPlugin(String[] args) {
		super();
		this.argProcessor = new SpeciesArgProcessor();
		argProcessor.parseArgs(args);

	}
	
	public static void main(String[] args) {
		AMIArgProcessor argProcessor = new SpeciesArgProcessor();
		argProcessor.parseArgsRunAndOutput(args);
	}


}
