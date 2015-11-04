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
public class PhyloTreePlugin extends AMIPlugin {

	private static final Logger LOG = Logger.getLogger(PhyloTreePlugin.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	
	@Deprecated // 
	public PhyloTreePlugin(String[] args) {
		super();
		this.argProcessor = new PhyloTreeArgProcessor(args);
	}
	
	public static void main(String[] args) {
		PhyloTreeArgProcessor argProcessor = new PhyloTreeArgProcessor(args);
		argProcessor.runAndOutput();
	}

	@Deprecated
	public PhyloTreePlugin(String args) {
		super();
		this.argProcessor = new PhyloTreeArgProcessor(args);
	}

	
	

}
