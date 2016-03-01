package org.xmlcml.ami2.plugins.sequence;

import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.ami2.plugins.PluginOption;

public class SequencePluginOption extends PluginOption {

	private static final Logger LOG = Logger.getLogger(SequencePluginOption.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	public static final String SEQUENCE = "sequence";

	public SequencePluginOption(List<String> options, List<String> flags) {
		super(SEQUENCE, options, flags);
	}
	
	protected void run() {
		String cmd = "--project "+projectDir+" -i scholarly.html --sq.sequence --sq.type "+optionString;
		new SequenceArgProcessor(cmd).runAndOutput();
	}

	// default works
//	protected void runMatchSummaryAndCount(String option) {
//		resultXPathAttribute = "@match";
//		super.runMatchSummaryAndCount(option);
//	}



}
