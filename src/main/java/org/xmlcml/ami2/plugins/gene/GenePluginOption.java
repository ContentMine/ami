package org.xmlcml.ami2.plugins.gene;

import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.ami2.plugins.PluginOption;

public class GenePluginOption extends PluginOption {

	private static final Logger LOG = Logger.getLogger(GenePluginOption.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	public final static String GENE = "gene";
	
	public GenePluginOption(List<String> options, List<String> flags) {
		super(GENE, options, flags);
	}

	protected void run() {
		String cmd = "--project "+projectDir+" -i scholarly.html --g.gene --g.type "+optionString;
		new GeneArgProcessor(cmd).runAndOutput();
	}

	// default works
//	protected void runMatchSummaryAndCount(String option) {
//		resultXPathAttribute = "@match";
//		super.runMatchSummaryAndCount(option);
//	}

}
