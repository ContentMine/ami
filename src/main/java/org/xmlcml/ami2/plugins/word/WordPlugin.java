package org.xmlcml.ami2.plugins.word;

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
public class WordPlugin extends AMIPlugin {

	private static final Logger LOG = Logger.getLogger(WordPlugin.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	
	public WordPlugin() {
		this.argProcessor = new WordArgProcessor();
	}

	public WordPlugin(String[] args) {
		super();
		this.argProcessor = new WordArgProcessor(args);
	}

	public WordPlugin(String args) {
		super();
		this.argProcessor = new WordArgProcessor(args);
	}

	public static void main(String[] args) {
		new WordArgProcessor(args).runAndOutput();		
	}


}
