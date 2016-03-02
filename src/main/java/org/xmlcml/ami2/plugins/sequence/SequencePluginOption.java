package org.xmlcml.ami2.plugins.sequence;

import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.ami2.plugins.AMIPluginOption;

public class SequencePluginOption extends AMIPluginOption {

	private static final Logger LOG = Logger.getLogger(SequencePluginOption.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	public static final String TAG = "sequence";

	public SequencePluginOption() {
		super(TAG);
	}
	
	public SequencePluginOption(List<String> options, List<String> flags) {
		super(TAG, options, flags);
	}
	
	protected void run() {
		String cmd = "--project "+projectDir+" -i scholarly.html --sq.sequence --sq.type "+optionString;
		new SequenceArgProcessor(cmd).runAndOutput();
	}



}
