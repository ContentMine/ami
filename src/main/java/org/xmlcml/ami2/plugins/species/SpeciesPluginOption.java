package org.xmlcml.ami2.plugins.species;

import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.ami2.plugins.PluginOption;
import org.xmlcml.cmine.args.DefaultArgProcessor;

public class SpeciesPluginOption extends PluginOption {

	public static final String SPECIES = "species";
	private static final Logger LOG = Logger.getLogger(SpeciesPluginOption.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	
	public SpeciesPluginOption(List<String> options, List<String> flags) {
		super(SPECIES, options, flags);
	}

	public void run() {
		String cmd = "--project "+projectDir+" -i scholarly.html --sp.species --sp.type "+optionString;
		LOG.debug(cmd);
		new SpeciesArgProcessor(cmd).runAndOutput();
	}

	protected void runMatchSummaryAndCount(String option) {
		resultXPathAttribute = "@match";
		super.runMatchSummaryAndCount(option);
	}


}
