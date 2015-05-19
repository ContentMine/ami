package org.xmlcml.ami2.plugins.phylotree;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.ami2.plugins.AMIPlugin;
import org.xmlcml.ami2.plugins.sequence.SequenceArgProcessor;

/** test plugin.
 * 
 * Very simple tasks for testing and tutorials.
 * 
 * @author pm286
 *
 */
public class TreePlugin extends AMIPlugin {

	private static final Logger LOG = Logger.getLogger(TreePlugin.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

//	private TreeArgProcessor argProcessor;
	
	public TreePlugin(String[] args) {
		super();
		this.argProcessor = new TreeArgProcessor(args);
	}
	
	public static void main(String[] args) {
		TreeArgProcessor argProcessor = new TreeArgProcessor(args);
		argProcessor.runAndOutput();
	}

	public TreePlugin(String args) {
		super();
		this.argProcessor = new TreeArgProcessor(args);
	}

	
	

}
