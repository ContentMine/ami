package org.xmlcml.ami2.plugins.word;

import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.ami2.plugins.PluginOption;
import org.xmlcml.cmine.args.DefaultArgProcessor;

public class WordPluginOption extends PluginOption {

	private static final Logger LOG = Logger.getLogger(WordPluginOption.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	public static final String TAG = "word";
	private String searchDictionary;
	private String dictionary;

	public WordPluginOption(List<String> options, List<String> flags) {
		super(TAG, options, flags);
	}

	protected void run() {
		StringBuilder commandString = createCoreCommandStringBuilder();
		commandString.append(" --w.words "+optionString);
		String sw = getOptionFlagString("w.stopwords", " ");
		commandString.append(sw);
		searchDictionary = getOptionFlagString("w.search", " ");
		if (searchDictionary != null && !searchDictionary.equals("")) {
			commandString.append(searchDictionary);
			plugin = "search";
			dictionary = getOption(null);
		}
		LOG.debug("WORD "+commandString);
		new WordArgProcessor(commandString.toString()).runAndOutput();
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

	protected String createFilterCommandString(String option) {
		String cmd = "--project "+projectDir;
		String xpathFlags = createXpathQualifier();
		option = (dictionary != null) ? dictionary : option;
		cmd += " --filter file(**/"+getPlugin(plugin)+"/"+option+"/results.xml)xpath("+resultXPathBase+xpathFlags+") ";
		cmd += " -o "+createSnippetsFilename(option)+"  ";
		LOG.debug("runFilterResultsXMLOptions: >>>> "+cmd);
		return cmd;
	}


	protected void runMatchSummaryAndCount(String option) {
		if (dictionary == null) {
			resultXPathAttribute = "@word";
			super.runMatchSummaryAndCount(option);
		} else {
			String cmd = "--project "+projectDir+" -i "+createSnippetsFilename(dictionary)+"  "
					+ "--xpath //result/"+resultXPathAttribute+" --summaryfile "+createCountFilename(dictionary);
			LOG.debug("runMatchSummaryAndCount: "+cmd);
			new DefaultArgProcessor(cmd).runAndOutput();
		}
	}

}
