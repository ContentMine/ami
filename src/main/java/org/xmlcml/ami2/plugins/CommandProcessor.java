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
import org.xmlcml.ami2.plugins.gene.GeneArgProcessor;
import org.xmlcml.ami2.plugins.sequence.SequenceArgProcessor;
import org.xmlcml.ami2.plugins.species.SpeciesArgProcessor;
import org.xmlcml.ami2.plugins.word.WordArgProcessor;
import org.xmlcml.cmine.args.DefaultArgProcessor;
import org.xmlcml.cmine.files.CProject;
import org.xmlcml.norma.Norma;

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

	private List<PluginOption> pluginOptions;
	private File projectDir = null;
	
	private CommandProcessor() {
		
	}

	public CommandProcessor(File projectDir) {
		setProjectDir(projectDir);
	}

	private void setProjectDir(File projectDir) {
		this.projectDir = projectDir;
	}

	public void runCommands(String args) {
		parseCommands(args);
		runCommands();
	}

	public void parseCommands(String args) {
		List<String> cmds = Arrays.asList(args.split("\\s+"));
		LOG.debug("Parsing: "+args);
		pluginOptions = new ArrayList<PluginOption>();
		if (cmds.size() == 0) {
			throw new RuntimeException("No commands given");
		}
		for (int i = 0; i < cmds.size(); i++) {
			String cmd = cmds.get(i);
			LOG.trace("creating pluginOption: "+cmd);
			PluginOption pluginOption = PluginOption.createPluginOption(cmd);
			LOG.trace(pluginOption);
			pluginOption.setProject(projectDir);
			pluginOptions.add(pluginOption);
		}
	}
	
	public void runCommands() {
		runNormaIfNecessary();
		for (PluginOption pluginOption : pluginOptions) {
			LOG.debug("running: "+pluginOption);
			pluginOption.run();
			LOG.debug("filter: "+pluginOption);
			pluginOption.runFilterResultsXMLOptions();
			LOG.debug("summary: "+pluginOption);
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

}