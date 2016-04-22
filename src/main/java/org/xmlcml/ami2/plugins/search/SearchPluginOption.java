package org.xmlcml.ami2.plugins.search;

import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.ami2.dictionary.DefaultAMIDictionary;
import org.xmlcml.ami2.plugins.AMIArgProcessor;
import org.xmlcml.ami2.plugins.AMIPluginOption;
import org.xmlcml.cmine.args.DefaultArgProcessor;
import org.xmlcml.cmine.util.CellRenderer;

public class SearchPluginOption extends AMIPluginOption {

	private static final Logger LOG = Logger.getLogger(SearchPluginOption.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	public static final String TAG = "search";
	private String searchDictionary;
	private String dictionary;

	public SearchPluginOption() {
		super(TAG);
	}

	public SearchPluginOption(List<String> options, List<String> flags) {
		super(TAG, options, flags);
	}

	protected void run() {
		StringBuilder commandString = createCoreCommandStringBuilder();
		String sw = getOptionFlagString("w.stopwords", " ");
		commandString.append(sw);
		searchDictionary = optionString;
		if (searchDictionary == null) {
			LOG.warn("no dictionary given); no search");
			return;
		}
		commandString.append(" --sr.search");
		commandString.append(" "+AMIArgProcessor.DICTIONARY_RESOURCE+"/"+searchDictionary+".xml");
		plugin = "search";
//		commandString.append(plugin);
//		commandString.append(searchDictionary);
//		dictionary = getOption(null);
		optionString = dictionary;
		LOG.debug("SEARCH "+commandString);
		System.out.print("SR: "+projectDir+"  ");
		new SearchArgProcessor(commandString.toString()).runAndOutput();
	}

	protected String getPlugin(String plugin) {
		return plugin;
	}

	protected String getOption(String option) {
		String opt = option;
		if (searchDictionary != null && !searchDictionary.trim().equals("")) {
			String[] ss = searchDictionary.split("/");
			String sss = ss[ss.length-1];
			sss = sss.split("\\.")[0];
			opt = sss;
		}
		return opt;
	}

//	protected String createFilterCommandString(String option) {
//		String cmd = "--project "+projectDir;
//		String xpathFlags = createXpathQualifier();
//		option = dictionary;
//		cmd += " --filter file(**/"+getPlugin(plugin)+"/"+option+"/results.xml)xpath("+resultXPathBase+xpathFlags+") ";
//		cmd += " -o "+createSnippetsFilename(option)+"  ";
//		LOG.debug("runFilterResultsXMLOptions: >>>> "+cmd);
//		return cmd;
//	}


	protected void runMatchSummaryAndCount(String option) {
		if (dictionary == null) {
			resultXPathAttribute = "@word";
			super.runMatchSummaryAndCount(option);
		} else {
			String cmd = "--project "+projectDir+" -i "+createSnippetsFilename(dictionary)+"  "
					+ "--xpath //result/"+resultXPathAttribute+" --summaryfile "+createCountFilename(dictionary);
			DefaultArgProcessor.CM_LOG.debug("runMatchSummaryAndCount: "+cmd);
			new DefaultArgProcessor(cmd).runAndOutput();
		}
	}
	
	@Override
	public CellRenderer getNewCellRenderer() {
		CellRenderer cellRenderer = super.getNewCellRenderer();
		cellRenderer.setHref0(AMIPluginOption.WIKIPEDIA_HREF0);
		cellRenderer.setHref1(AMIPluginOption.WIKIPEDIA_HREF1);
		cellRenderer.setUseHrefWords(1, "_");
		return cellRenderer;
	}

//	protected boolean matches(String pluginOptionName) {
//		String pluginOptionTag0 = pluginOptionName.split(":")[0];
//		String pluginOptionTag1 = pluginOptionName.split(":")[1];
//		LOG.trace("TAG "+pluginOptionTag0+" : "+pluginOptionName);
//		boolean ok = SEARCH.equals(pluginOptionTag0) || TAG.equals(pluginOptionTag0);
//		return ok;
//	}



}
