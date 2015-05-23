package org.xmlcml.ami2.plugins.sequence;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
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
	
	public SequencePlugin() {
		this.argProcessor = new SequenceArgProcessor();
	}

	public SequencePlugin(String[] args) {
		super();
		this.argProcessor = new SequenceArgProcessor(args);
	}

	public SequencePlugin(String args) {
		super();
		this.argProcessor = new SequenceArgProcessor(args);
	}

	public static void main(String[] args) {
		new SequenceArgProcessor(args).runAndOutput();		
	}

}
