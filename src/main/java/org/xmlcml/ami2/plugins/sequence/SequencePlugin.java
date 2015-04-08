package org.xmlcml.ami2.plugins.sequence;

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
public class SequencePlugin extends AMIPlugin {

	private static final Logger LOG = Logger.getLogger(SequencePlugin.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	public SequencePlugin(String[] args) {
		super();
		this.argProcessor = new SequenceArgProcessor();
		argProcessor.parseArgs(args);

	}
	
	public static void main(String[] args) {
		AMIArgProcessor argProcessor = new SequenceArgProcessor();
		argProcessor.parseArgsRunAndOutput(args);
	}


}
