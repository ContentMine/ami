package org.xmlcml.ami2.plugins.regex;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.xmlcml.ami2.Fixtures;
import org.xmlcml.ami2.plugins.AMIArgProcessor;
import org.xmlcml.ami2.plugins.AMIPlugin;
import org.xmlcml.files.QuickscrapeNorma;

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
				"--context", "25", "40", "--r.regex", "regex/simpletest.xml",
		};
		new RegexPlugin(args);
	}
	
	@Test
	public void testRegexPlugins() throws IOException {
		QuickscrapeNorma qsNorma = new QuickscrapeNorma(Fixtures.TEST_BMC_15_1_511_QSN);
		File normaTemp = new File("target/bmc/regex/15_1_511_test");
		qsNorma.copyTo(normaTemp, true);
		Assert.assertFalse("results.xml", qsNorma.hasResultsXML());
		String[] args = {
				"-q", normaTemp.toString(),
				"-i", "scholarly.html",
				"-o", "results.xml",
				"--context", "25", "40",
				"--r.regex" ,
			    	"regex/common.xml",
			    	"regex/figure.xml",
			    	"regex/phylotree.xml",
			    	"regex/stemtest.xml", // this doesn't exist and LOGs an error
		};
		AMIPlugin regexPlugin = new RegexPlugin(args);
		AMIArgProcessor argProcessor = (AMIArgProcessor) regexPlugin.getArgProcessor();
		Assert.assertNotNull(argProcessor);
		argProcessor.runAndOutput();
	}
	
	@Test
	public void testCONSORTRegex() throws IOException {
		LOG.debug("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
		Fixtures.runStandardTestHarness(
				Fixtures.TEST_BMC_15_1_511_QSN, 
				new File("target/consort0/15_1_511_test/"), 
				new RegexPlugin("-q target/consort0/15_1_511_test/ -i scholarly.html --context 25 40 --r.regex regex/consort0.xml"),
				"regex/consort0/");
		LOG.debug("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
		
	}



	
	@Test
	@Ignore // not yet implemented
	public void testRegexPluginExtractNumbers() throws IOException {
		String args = "-q target/bmc/regex/15_1_511_test -i scholarly.html -o results.xml --context 25 40 --r.regex regex/consort0.xml";
		new RegexPlugin(args).runAndOutput();
	}
}
