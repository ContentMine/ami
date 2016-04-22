package org.xmlcml.ami2.plugins;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.ami2.plugins.gene.GenePluginOption;
import org.xmlcml.ami2.plugins.regex.RegexPluginOption;
import org.xmlcml.ami2.plugins.search.SearchPluginOption;
import org.xmlcml.ami2.plugins.sequence.SequencePluginOption;
import org.xmlcml.ami2.plugins.species.SpeciesPluginOption;
import org.xmlcml.ami2.plugins.word.WordPluginOption;

public class AMIPluginOptionList {

	private static final Logger LOG = Logger.getLogger(AMIPluginOptionList.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	public List<AMIPluginOption> pluginOptionList;
	public final static AMIPluginOption GENE = new GenePluginOption();
	public final static AMIPluginOption REGEX = new RegexPluginOption();
	public final static AMIPluginOption SEARCH = new SearchPluginOption();
	public final static AMIPluginOption SEQUENCE = new SequencePluginOption();
	public final static AMIPluginOption SPECIES = new SpeciesPluginOption();
	public final static AMIPluginOption WORD = new WordPluginOption();
		
	
	public AMIPluginOptionList() {
		init();
	}

	private void init() {
		pluginOptionList = new ArrayList<AMIPluginOption>();
		pluginOptionList.add(GENE);
		pluginOptionList.add(REGEX);
		pluginOptionList.add(SEARCH);
		pluginOptionList.add(SEQUENCE);
		pluginOptionList.add(SPECIES);
		pluginOptionList.add(WORD);
	}

	public AMIPluginOption getPluginOption(String pluginOptionName) {
		for (AMIPluginOption pluginOption : pluginOptionList) {
			// messy - we just want the first name; this should be a PluginOption, not its name
			if (pluginOption.matches(pluginOptionName)) {
				return pluginOption;
			}
		}
		return null;
	}
}
