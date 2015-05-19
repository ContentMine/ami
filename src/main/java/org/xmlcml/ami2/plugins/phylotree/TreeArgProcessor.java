package org.xmlcml.ami2.plugins.phylotree;

import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.ami2.plugins.AMIArgProcessor;
import org.xmlcml.cmine.args.ArgIterator;
import org.xmlcml.cmine.args.ArgumentOption;
import org.xmlcml.cmine.files.CMDir;
import org.xmlcml.cmine.files.ResultElement;
import org.xmlcml.cmine.files.ResultsElement;

/** 
 * Processes commandline arguments.
 * 
 * @author pm286
 */
public class TreeArgProcessor extends AMIArgProcessor {
	
	public static final Logger LOG = Logger.getLogger(TreeArgProcessor.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	protected List<String> words;

	public TreeArgProcessor() {
		super();
	}

	public TreeArgProcessor(String[] args) {
		this();
		parseArgs(args);
	}

	public TreeArgProcessor(String argString) {
		this(argString.split(WHITESPACE));
	}

	// =============== METHODS ==============

	public void parseSimple(ArgumentOption option, ArgIterator argIterator) {
		List<String> tokens = argIterator.createTokenListUpToNextNonDigitMinus(option);
	}
	
	// =============================

	@Override
	/** parse args and resolve their dependencies.
	 * 
	 * (don't run any argument actions)
	 * 
	 */
	public void parseArgs(String[] args) {
		super.parseArgs(args);
	}

	
}
