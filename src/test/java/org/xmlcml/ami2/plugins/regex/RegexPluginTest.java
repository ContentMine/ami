package org.xmlcml.ami2.plugins.regex;

import java.io.File;
import java.io.IOException;
import java.util.List;

import nu.xom.Element;
import nu.xom.Text;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.xmlcml.ami2.AMIFixtures;
import org.xmlcml.ami2.plugins.AMIPlugin;
import org.xmlcml.cmine.args.DefaultArgProcessor;
import org.xmlcml.cmine.files.CMDir;
import org.xmlcml.cmine.files.ResultElement;
import org.xmlcml.cmine.files.ResultsElement;
import org.xmlcml.html.HtmlA;
import org.xmlcml.html.HtmlElement;
import org.xmlcml.html.HtmlP;
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
	// BMC has unusual XML
	public void testRegexPlugins() throws IOException {
		CMDir cTree = new CMDir(AMIFixtures.TEST_BMC_15_1_511_CMDIR);
		File normaTemp = new File("target/bmc/regex/15_1_511_test");
		cTree.copyTo(normaTemp, true);
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
		File target = new File("target/consort0/15_1_511_test/");
		AMIFixtures.runStandardTestHarness(
				AMIFixtures.TEST_BMC_15_1_511_CMDIR, 
				target, 
				new RegexPlugin(),
				"-q "+target+" -i scholarly.html --context 25 40 --r.regex regex/consort0.xml",
				"regex/consort0/");
		CMDir cTree = new CMDir(target);
		// this may alter it by reparsing
		HtmlElement scholarlyHtml = cTree.ensureScholarlyHtmlElement();
//		FileUtils.write(new File("target/consort0/text.html"), );
		File resultsXml = new File(target, "results/regex/consort0/results.xml");
		annotate(scholarlyHtml, resultsXml);
		File annotatedHtml = new File(target, "results/regex/consort0/annotated.html");
		FileUtils.write(annotatedHtml, scholarlyHtml.toXML());
	}
	
	private void annotate(HtmlElement htmlElement, File resultsXml) {
		Element resultsElement0 = XMLUtil.parseQuietlyToDocument(resultsXml).getRootElement();
		ResultsElement resultsElement = ResultsElement.createResultsElement(resultsElement0);
		List<ResultElement> resultElements = resultsElement.getOrCreateResultElementList();
		for (ResultElement resultElement : resultElements) {
			String xpath = resultElement.getXPath();
			String pre = resultElement.getPre();
			String post = resultElement.getPost();
			List<Element> nodes = XMLUtil.getQueryElements(htmlElement, xpath);
			if (nodes.size() == 1) {
				Element element = nodes.get(0);
				if (element instanceof HtmlP) {
					String value = element.getValue();
					int iPre = value.indexOf(pre);
					iPre = iPre + pre.length();
					String preString = value.substring(0,  iPre);
					int iPost = value.indexOf(post);
					String postString = value.substring(iPost);
					String target = value.substring(iPre, iPost);
					for (int i = element.getChildCount() - 1; i >= 0; i--) {
						element.getChild(i).detach();
					}
					element.appendChild(new Text(preString));
					HtmlA aElement = new HtmlA();
					aElement.appendChild("["+target+"]");
					aElement.setHref("foo");
					element.appendChild(aElement);
					element.appendChild(new Text(postString));
				}
			}
		}
	}

	@Test
	public void testSectioning() throws IOException {
		FileUtils.copyDirectory(AMIFixtures.TEST_BMC_15_1_511_CMDIR, new File("target/consort0/15_1_511_test/"));
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
