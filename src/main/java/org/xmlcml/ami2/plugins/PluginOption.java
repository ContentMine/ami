package org.xmlcml.ami2.plugins;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.jsoup.helper.StringUtil;
import org.xmlcml.ami2.plugins.gene.GenePluginOption;
import org.xmlcml.ami2.plugins.regex.RegexPluginOption;
import org.xmlcml.ami2.plugins.sequence.SequencePluginOption;
import org.xmlcml.ami2.plugins.species.SpeciesPluginOption;
import org.xmlcml.ami2.plugins.word.WordPluginOption;
import org.xmlcml.cmine.args.DefaultArgProcessor;

public abstract class PluginOption {

	private static final String WORD = "word";
	private static final String SPECIES = "species";
	private static final String SEQUENCE = "sequence";
	private static final Logger LOG = Logger.getLogger(PluginOption.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	public static Pattern COMMAND = Pattern.compile("(.*)\\((.*)\\)(.*)");
	
	protected String plugin;
	protected List<String> options;
	protected List<String> flags;
	protected File projectDir;
	protected String optionString;
	protected String resultXPathAttribute;
	protected String resultXPathBase;
	private List<OptionFlag> optionFlags;

	public PluginOption(String plugin, List<String> options, List<String> flags) {
		this.plugin = plugin;
		this.options = options;
		this.flags = flags;
		this.optionString = StringUtil.join(options, " ");
		LOG.trace("optionString: "+optionString);
		this.resultXPathBase = "//result";
		this.resultXPathAttribute = "@exact";

	}

	public static PluginOption createPluginOption(String cmd) {
		Matcher matcher = COMMAND.matcher(cmd);
		if (!matcher.matches()) {
			throw new RuntimeException("Command must be plugin(option1[,option2...])[_flag1[_flag2...]]");
		}
		String command = matcher.group(1);
		List<String> options = Arrays.asList(matcher.group(2).split(","));
		String flagString = matcher.group(3);
		flagString = flagString.replaceAll("_",  " ");
		List<String>flags = Arrays.asList(flagString.split("~"));
		List<OptionFlag> optionFlags = OptionFlag.createOptionFlags(flags);
		
		PluginOption pluginOption = null;
		if (false) {
		} else if (command.equals(GenePluginOption.TAG)) {
			pluginOption = new GenePluginOption(options,flags);
		} else if (command.equals(RegexPluginOption.TAG)) {
			pluginOption = new RegexPluginOption(options,flags); 
		} else if (command.equals(SequencePluginOption.TAG)) {
			pluginOption = new SequencePluginOption(options,flags); 
		} else if (command.equals(SpeciesPluginOption.TAG)) {
			pluginOption = new SpeciesPluginOption(options,flags);
		} else if (command.equals(WordPluginOption.TAG)) {
			pluginOption = new WordPluginOption(options,flags);
		} else {
			LOG.error("unknown command: "+command);
		}
		if (pluginOption != null) {
			pluginOption.setOptionFlags(optionFlags);
		}
		return pluginOption;
	}

	private void setOptionFlags(List<OptionFlag> optionFlags) {
		this.optionFlags = optionFlags;
	}

	List<OptionFlag> getOptionFlags() {
		return this.optionFlags;
	}

	public void setProject(File projectDir) {
		this.projectDir = projectDir;
	}
	
	protected abstract void run();

	// create optionSnippets
	public void runFilterResultsXMLOptions() {
		for (String option : options) {
			runFilterResultsXMLOptions(option);
		}
	}
	
	private void runFilterResultsXMLOptions(String option) {
		String filterCommandString = createFilterCommandString(option);
		System.out.println("filter: "+filterCommandString);
		new DefaultArgProcessor(filterCommandString).runAndOutput();
	}

	protected String createFilterCommandString(String option) {
		String cmd = "--project "+projectDir;
		String xpathFlags = createXpathQualifier();
		cmd += " --filter file(**/"+getPlugin(plugin)+"/"+getOption(option)+"/results.xml)xpath("+resultXPathBase+xpathFlags+") ";
		cmd += " -o "+createSnippetsFilename(option)+"  ";
		System.out.println("runFilterResultsXMLOptions: "+cmd);
		return cmd;
	}

	protected String getPlugin(String plugin) {
		return plugin;
	}

	protected String createXpathQualifier() {
		String xpathFlags = getOptionFlagString("xpath", "");
		if (xpathFlags != null && !"".equals(xpathFlags)) {
			xpathFlags = "["+xpathFlags+"]";
		}
		return xpathFlags;
	}

	protected String getOptionFlagString(String key, String separator) {
		StringBuilder optionFlagString = new StringBuilder();
		List<OptionFlag> keyedOptionFlags = getKeyedOptionFlags(key);
		if (keyedOptionFlags.size() > 0) {
			if (!key.equals("xpath")) {
				optionFlagString.append(" --"+key);
			}
			for (int i = 0; i < keyedOptionFlags.size(); i++) {
				optionFlagString.append(separator);
				String ko = keyedOptionFlags.get(i).getValue();
				LOG.trace(">>>>>>>>>>>>>"+ko);
				optionFlagString.append(ko);
			}
		}
		return optionFlagString.toString();
	}

	private List<OptionFlag> getKeyedOptionFlags(String key) {
		List<OptionFlag> keyedOptionFlags = new ArrayList<OptionFlag>();
		for (OptionFlag optionFlag : optionFlags) {
			if (optionFlag.getKey().equals(key)) {
				LOG.trace("OF "+optionFlag+ " /// "+key);
				keyedOptionFlags.add(optionFlag);
			}
		}
		return keyedOptionFlags;
	}

	protected void runMatchSummaryAndCount(String option) {
		String cmd = "--project "+projectDir
				+ " -i "+createSnippetsFilename(option)
				+ " --xpath //result/"+resultXPathAttribute
				+ " --summaryfile "+createCountFilename(option)
				+ " --dffile "+createDocumentCountFilename(option)
				;
		System.out.println("runMatchSummaryAndCount: "+cmd);
		new DefaultArgProcessor(cmd).runAndOutput();
	}
	


	// analyze optionSnippets
	public void runSummaryAndCountOptions() {
		for (String option : options) {
			runMatchSummaryAndCount(option);
		}
	}

	public String toString() {
		return plugin+"("+options+")"+optionFlags;
	}

	protected String createSnippetsFilename(String option) {
		return plugin+"."+getOption(option)+".snippets.xml";
	}

	protected String createCountFilename(String option) {
		return plugin+"."+getOption(option)+".count.xml";
	}
	
	protected String createDocumentCountFilename(String option) {
		return plugin+"."+getOption(option)+".documents.xml";
	}
	
	protected String getOption(String option) {
		return option;
	}

	protected StringBuilder createCoreCommandStringBuilder() {
		StringBuilder commandStringBuilder = new StringBuilder("--project "+projectDir+" -i scholarly.html");
		commandStringBuilder.append(getOptionFlagString("context", " "));
		return commandStringBuilder;
	}

}
