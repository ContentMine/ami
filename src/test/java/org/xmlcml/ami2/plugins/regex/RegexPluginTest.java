package org.xmlcml.ami2.plugins.regex;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.xmlcml.ami2.Fixtures;
import org.xmlcml.ami2.plugins.AMIPlugin;
import org.xmlcml.cmine.args.DefaultArgProcessor;
import org.xmlcml.cmine.files.CMDir;
import org.xmlcml.xml.XMLUtil;

public class RegexPluginTest {
	
	private static final Logger LOG = Logger.getLogger(RegexPluginTest.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	
	
	/** test help
	 * 
	 * desn't run anything
	 * 
	 * @throws IOException
	 */
	@Test
	@Ignore // to avoid output
	public void testSimpleTestRegexHelp() throws IOException {
		String[] args = {
				
		};
		new RegexPlugin(args);
	}
	

	
	/** test generation of conformant regexes
	 * 
	 * desn't run anything
	 * 
	 * @throws IOException
	 */
	@Test
	public void testSimpleTestRegex() throws IOException {
		String[] args = {
				// add context for 25 chars preceding and 40 post
				"--context", "25", "40", "--r.regex", "regex/common.xml",
		};
		new RegexPlugin(args);
	}
	
	@Test
	public void testRegexPlugins() throws IOException {
		CMDir cmDir = new CMDir(Fixtures.TEST_BMC_15_1_511_CMDIR);
		File normaTemp = new File("target/bmc/regex/15_1_511_test");
		cmDir.copyTo(normaTemp, true);
		String[] args = {
				"-q", normaTemp.toString(),
				"-i", "scholarly.html",
				"-o", "results.xml",
				"--context", "25", "40",
				"--r.regex" ,
			    	"regex/common.xml",
			    	"regex/figure.xml",
			    	"regex/phylotree.xml",
		};
		AMIPlugin regexPlugin = new RegexPlugin(args);
		DefaultArgProcessor argProcessor = (DefaultArgProcessor) regexPlugin.getArgProcessor();
		Assert.assertNotNull(argProcessor);
		argProcessor.runAndOutput();
	}
	
	@Test
	public void testCONSORTRegex() throws IOException {
		Fixtures.runStandardTestHarness(
				Fixtures.TEST_BMC_15_1_511_CMDIR, 
				new File("target/consort0/15_1_511_test/"), 
				new RegexPlugin(),
				"-q target/consort0/15_1_511_test/ -i scholarly.html --context 25 40 --r.regex regex/consort0.xml",
				"regex/consort0/");
	}
	
	@Test
	public void testSectioning() throws IOException {
		FileUtils.copyDirectory(Fixtures.TEST_BMC_15_1_511_CMDIR, new File("target/consort0/15_1_511_test/"));
		String cmd = "-q target/consort0/15_1_511_test/ -i scholarly.html --r.regex regex/consort0.xml";
		RegexArgProcessor argProcessor = new RegexArgProcessor(cmd);
		argProcessor.runAndOutput();
		File resultsFile = new File("target/consort0/15_1_511_test/results/regex/consort0/results.xml");
		Assert.assertEquals("results without xpath", 8,  
				XMLUtil.getQueryElements(XMLUtil.parseQuietlyToDocument(resultsFile).getRootElement(), 
						"//*[local-name()='result']").size());
		cmd = "-q target/consort0/15_1_511_test/ -i scholarly.html --xpath //*[@tagx='title']/* --r.regex regex/consort0.xml";
		argProcessor = new RegexArgProcessor(cmd);
		argProcessor.runAndOutput();
		resultsFile = new File("target/consort0/15_1_511_test/results/regex/consort0/results.xml");
		Assert.assertEquals("results with xpath", 2,  
				XMLUtil.getQueryElements(XMLUtil.parseQuietlyToDocument(resultsFile).getRootElement(), 
						"//*[local-name()='result']").size());
	}
	
	@Test
	@Ignore // not yet implemented
	public void testRegexPluginExtractNumbers() throws IOException {
		String args = "-q target/bmc/regex/15_1_511_test -i scholarly.html -o results.xml --context 25 40 --r.regex regex/consort0.xml";
		new RegexPlugin(args).runAndOutput();
	}
}
