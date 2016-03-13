package org.xmlcml.ami2.plugins;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.ami2.plugins.ResultsAnalysis.SummaryType;
import org.xmlcml.cmine.files.CProject;
import org.xmlcml.cmine.util.CellRenderer;
import org.xmlcml.cmine.util.DataTablesTool;
import org.xmlcml.html.HtmlHtml;
import org.xmlcml.html.HtmlTable;
import org.xmlcml.html.HtmlTd;
import org.xmlcml.norma.Norma;
import org.xmlcml.norma.biblio.json.EPMCConverter;
import org.xmlcml.xml.XMLUtil;

/** processes commandline , higher level functions
 * 
 * @author pm286
 *
 */
public class CommandProcessor {

	private static final Logger LOG = Logger.getLogger(CommandProcessor.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	private List<AMIPluginOption> pluginOptions;
	private File projectDir = null;
	private List<String> cmdList = new ArrayList<String>();
	
	private CommandProcessor() {
		
	}

	public CommandProcessor(File projectDir) {
		setProjectDir(projectDir);
	}

	private void setProjectDir(File projectDir) {
		this.projectDir = projectDir;
	}

	public void processCommands(String commandString) {
		if (commandString == null) {
			throw new RuntimeException("Null command");
		}
		processCommands(Arrays.asList(commandString.trim().split("\\s+")));
	}

	public void processCommands(List<String> cmds) {
		parseCommands(cmds);
		runCommands();
	}

	private void parseCommands(List<String> cmds) {
		pluginOptions = new ArrayList<AMIPluginOption>();
		if (cmds.size() == 0) {
			throw new RuntimeException("No commands given");
		}
		for (int i = 0; i < cmds.size(); i++) {
			String cmd = cmds.get(i);
			LOG.trace("creating pluginOption: "+cmd);
			AMIPluginOption pluginOption = AMIPluginOption.createPluginOption(cmd);
			if (pluginOption == null) {
				LOG.error("skipping unknown command: "+cmd);
			} else {
				LOG.trace(pluginOption);
				pluginOption.setProject(projectDir);
				pluginOptions.add(pluginOption);
			}
		}
	}
	
	public void runCommands() {
		runNormaIfNecessary();
		for (AMIPluginOption pluginOption : pluginOptions) {
			System.out.println("running: "+pluginOption);
			pluginOption.run();
			System.out.println("filter: "+pluginOption);
			pluginOption.runFilterResultsXMLOptions();
			System.out.println("summary: "+pluginOption);
			pluginOption.runSummaryAndCountOptions(); 
		}
		LOG.trace(pluginOptions);
	}

	public void runNormaIfNecessary() {
		if (!new CProject(projectDir).hasScholarlyHTML()) {
			String args = "-i fulltext.xml -o scholarly.html --transform nlm2html --project "+projectDir;
			LOG.debug("running NORMA "+args);
			new Norma().run(args);
		}
	}

	public void addCommand(String cmd) {
		cmdList.add(cmd);
	}

	public void setDefaultCommands(String cmds) {
		setDefaultCommands(Arrays.asList(cmds.split("\\s+")));
	}

	public void setDefaultCommands(List<String> cmds) {
		List<String> commands = new ArrayList<String>();
		boolean start = true;
		for (String cmd : cmds) {
			String command = lookup(cmd);
			if (command == null) {
				LOG.warn("abbreviation ignored: "+cmd);
				continue;
			}
			commands.add(command);
		}
		this.processCommands(commands);
	}

	private String lookup(String cmd) {
		return null;
	}
	
	public void createDataTables() throws IOException {
		if (projectDir == null) {
			throw new RuntimeException("projectDir must be set");
		}
		String project = FilenameUtils.getBaseName(projectDir.toString());
		DataTablesTool dataTablesTool = new DataTablesTool();
		dataTablesTool.setTitle(project);
		ResultsAnalysis resultsAnalysis = new ResultsAnalysis(dataTablesTool);
		resultsAnalysis.addDefaultSnippets(projectDir);
		resultsAnalysis.setRemoteLink0(EPMCConverter.HTTP_EUROPEPMC_ORG_ARTICLES);
		resultsAnalysis.setRemoteLink1("");
		resultsAnalysis.setLocalLink0("");
		resultsAnalysis.setLocalLink1(ResultsAnalysis.SCHOLARLY_HTML);
		resultsAnalysis.setRowHeadingName("EPMCID");
		for (SummaryType cellType : ResultsAnalysis.SUMMARY_TYPES) {
			resultsAnalysis.setCellContentFlag(cellType);
			HtmlTable table = resultsAnalysis.makeHtmlDataTable();
			HtmlHtml html = dataTablesTool.createHtmlWithDataTable(table);
			File outfile = new File(projectDir, cellType.toString()+"."+CProject.DATA_TABLES_HTML);
			XMLUtil.debug(html, outfile, 1);
		}
		LOG.trace(dataTablesTool.columnHeadingList);
		List<HtmlTd> footerList = new ArrayList<HtmlTd>();
		for (CellRenderer cellRenderer : dataTablesTool.columnHeadingList) {
			HtmlTd td = new HtmlTd();
			td.appendChild(cellRenderer.getHeading());
			footerList.add(td);
		}
		HtmlTd caption = new HtmlTd();
		caption.appendChild("coun-ts");
		dataTablesTool.setFooterCaption(caption);
		dataTablesTool.setFooterCells(footerList);
	}

	public static void main(String[] args) throws IOException {
		CommandProcessor commandProcessor = new CommandProcessor();
		if (args.length == 0) {
			help();
		} else {
			// first arg is projectDir
			commandProcessor.setProjectDir(new File(args[0]));
			List<String> commands = getDefaultCommands();
			if (args.length > 1) {
				commands = new ArrayList<String>(Arrays.asList(args));
				// remove projectDir
				commands.remove(0);
			}
			commandProcessor.processCommands(commands);
			commandProcessor.createDataTables();
		}
	}

	private static List<String> getDefaultCommands() {
		String[] cmds = {
				"word(frequencies)xpath:@count>20~w.stopwords:pmcstop.txt_stopwords.txt",
				"sequence(dnaprimer)",
				"gene(human)",
				"species(genus)",
				"species(binomial)"
		};
		return Arrays.asList(cmds);
	}

	private static void help() {
		System.err.println("Command processor: \n"
				+ "   cmine projectDir [command [command]...]");
	}

}
