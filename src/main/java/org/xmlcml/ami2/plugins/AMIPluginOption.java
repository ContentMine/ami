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
import org.xmlcml.ami2.plugins.search.SearchPluginOption;
import org.xmlcml.ami2.plugins.sequence.SequencePluginOption;
import org.xmlcml.ami2.plugins.species.SpeciesPluginOption;
import org.xmlcml.ami2.plugins.word.WordPluginOption;
import org.xmlcml.cproject.args.DefaultArgProcessor;
import org.xmlcml.cproject.files.OptionFlag;
import org.xmlcml.cproject.files.PluginOption;
import org.xmlcml.cproject.util.CellRenderer;

public abstract class AMIPluginOption extends PluginOption {

	private static final String WORD = "word";
	private static final String SPECIES = "species";
	private static final String SEQUENCE = "sequence";
	private static final Logger LOG = Logger.getLogger(AMIPluginOption.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	public static Pattern COMMAND = Pattern.compile("(.*)\\((.*)\\)(.*)");
	
	public final static List<String> COMMANDS = Arrays.asList( new String[] {
	GenePluginOption.TAG,
//	IdentifierPluginOption.TAG,
	RegexPluginOption.TAG,
	SequencePluginOption.TAG,
	SpeciesPluginOption.TAG,
	WordPluginOption.TAG,
	});

	public final static String WIKIPEDIA_HREF0 = "http://en.wikipedia.org/wiki/";
	public final static String WIKIPEDIA_HREF1 = "";
	
	protected AMIPluginOption(String tag) {
		this.plugin = tag;
	}
	
	public AMIPluginOption(String plugin, List<String> options, List<String> flags) {
		this(plugin);
		this.options = options;
		this.flags = flags;
		this.optionString = StringUtil.join(options, " ");
		LOG.trace("optionString: "+optionString);
		this.resultXPathBase = "//result";
		this.resultXPathAttribute = "@exact";

	}

	/** this is where the subclassing is created.
	 * 
	 * */
	public static AMIPluginOption createPluginOption(String cmd) {
		Matcher matcher = COMMAND.matcher(cmd);
		if (cmd == null || cmd.trim().equals("")) {
			throw new RuntimeException("Null/empty command");
		} else if (!matcher.matches()) {
			throw new RuntimeException("Command found: "+cmd+" must fit: "+matcher+""
					+ "...  plugin(option1[,option2...])[_flag1[_flag2...]]");
		}
		String command = matcher.group(1);
		List<String> options = Arrays.asList(matcher.group(2).split(","));
		String flagString = matcher.group(3);
		flagString = flagString.replaceAll("_",  " ");
		List<String>flags = Arrays.asList(flagString.split("~"));
		List<OptionFlag> optionFlags = OptionFlag.createOptionFlags(flags);
		LOG.trace("option flags: "+optionFlags);
		
		AMIPluginOption pluginOption = null;
		if (false) {
		} else if (command.equals(GenePluginOption.TAG)) {
			pluginOption = new GenePluginOption(options,flags);
		} else if (command.equals(RegexPluginOption.TAG)) {
			pluginOption = new RegexPluginOption(options,flags); 
		} else if (command.equals(SearchPluginOption.TAG)) {
			pluginOption = new SearchPluginOption(options,flags); 
		} else if (command.equals(SequencePluginOption.TAG)) {
			pluginOption = new SequencePluginOption(options,flags); 
		} else if (command.equals(SpeciesPluginOption.TAG)) {
			pluginOption = new SpeciesPluginOption(options,flags);
		} else if (command.equals(WordPluginOption.TAG)) {
			pluginOption = new WordPluginOption(options,flags);
		} else {
			LOG.error("unknown command: "+command);
//			LOG.info("commands: "+COMMANDS);
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
		DefaultArgProcessor.CM_LOG.debug("filter: "+filterCommandString);
		System.out.print(option);
		new DefaultArgProcessor(filterCommandString).runAndOutput();
	}

	protected String createFilterCommandString(String option) {
		String cmd = "--project "+projectDir;
		String xpathFlags = createXpathQualifier();
		cmd += " --filter file(**/"+getPlugin(plugin)+"/"+getOption(option)+"/results.xml)xpath("+resultXPathBase+xpathFlags+") ";
		cmd += " -o "+createSnippetsFilename(option)+"  ";
		DefaultArgProcessor.CM_LOG.debug("runFilterResultsXMLOptions: "+cmd);
		System.out.print(option);
		return cmd;
	}

	protected String getPlugin(String plugin) {
		return plugin;
	}

	public String getPlugin() {
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
		DefaultArgProcessor.CM_LOG.debug("runMatchSummaryAndCount: "+cmd);
		System.out.print("C: "+option);
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
	
	public CellRenderer getNewCellRenderer() {
		CellRenderer cellRenderer = new CellRenderer(this);
		return cellRenderer;
	}

	protected boolean matches(String pluginOptionName) {
		String pluginOptionTag = pluginOptionName.split(":")[0];
		LOG.trace("TAG "+pluginOptionTag);
		return getPlugin().equals(pluginOptionTag);
	}

}
