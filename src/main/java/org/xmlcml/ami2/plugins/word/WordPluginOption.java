package org.xmlcml.ami2.plugins.word;

import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.ami2.plugins.AMIPluginOption;
import org.xmlcml.ami2.plugins.search.SearchArgProcessor;
import org.xmlcml.cproject.args.DefaultArgProcessor;
import org.xmlcml.cproject.util.CellRenderer;

public class WordPluginOption extends AMIPluginOption {

	private static final Logger LOG = Logger.getLogger(WordPluginOption.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	public static final String TAG = "word";

	public WordPluginOption() {
		super(TAG);
	}

	public WordPluginOption(List<String> options, List<String> flags) {
		super(TAG, options, flags);
	}

	protected void run() {
		StringBuilder commandString = createCoreCommandStringBuilder();
		commandString.append(" --w.words "+optionString);
		String sw = getOptionFlagString("w.stopwords", " ");
		commandString.append(sw);
		LOG.trace("WORD "+commandString);
		System.out.print("WS: "+projectDir+"  ");
		new WordArgProcessor(commandString.toString()).runAndOutput();
//		new SearchArgProcessor(commandString.toString()).runAndOutput();
	}

	protected String getPlugin(String plugin) {
		return plugin;
	}

//	protected String getOption(String option) {
//		String opt = option;
////		if (searchDictionary != null && !searchDictionary.trim().equals("")) {
////			String[] ss = searchDictionary.split("/");
////			String sss = ss[ss.length-1];
////			sss = sss.split("\\.")[0];
////			opt = sss;
////		}
//		return opt;
//	}

//	protected String createFilterCommandString(String option) {
//		String cmd = "--project "+projectDir;
//		String xpathFlags = createXpathQualifier();
//		cmd += " --filter file(**/"+getPlugin(plugin)+"/"+option+"/results.xml)xpath("+resultXPathBase+xpathFlags+") ";
//		cmd += " -o "+createSnippetsFilename(option)+"  ";
//		LOG.debug("runFilterResultsXMLOptions: >>>> "+cmd);
//		return cmd;
//	}
//
//	protected String createFilterCommandString(String option) {
//		String cmd = "--project "+projectDir;
//		String xpathFlags = createXpathQualifier();
//		cmd += " --filter file(**/"+getPlugin(plugin)+"/"+getOption(option)+"/results.xml)xpath("+resultXPathBase+xpathFlags+") ";
//		cmd += " -o "+createSnippetsFilename(option)+"  ";
//		DefaultArgProcessor.CM_LOG.debug("runFilterResultsXMLOptions: "+cmd);
//		System.out.print(option);
//		return cmd;
//	}

//	protected void runMatchSummaryAndCount(String option) {
//		if (dictionary == null) {
//			resultXPathAttribute = "@word";
//			super.runMatchSummaryAndCount(option);
//		} else {
//			String cmd = "--project "+projectDir+" -i "+createSnippetsFilename(dictionary)+"  "
//					+ "--xpath //result/"+resultXPathAttribute+" --summaryfile "+createCountFilename(dictionary);
//			DefaultArgProcessor.CM_LOG.debug("runMatchSummaryAndCount: "+cmd);
//			new DefaultArgProcessor(cmd).runAndOutput();
//		}
//	}
	
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
