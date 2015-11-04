package org.xmlcml.ami2.plugins;

import java.io.File;
import java.io.IOException;
import java.util.List;

import nu.xom.Element;

import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Test;
import org.xmlcml.ami2.plugins.word.WordArgProcessor;

public class AMIPluginTest {

	@Test
	public void testAMIPlugin() {
		String[] args = {"regex"};
		AMIPlugin.main(args);
	}
	
	@Test
	public void testBadXPath() {
		String cmd = "-q src/test/resources/org/xmlcml/ami2/bmc/15_1_511 -i fulltext.xml --xpath //*]"; 
		try {
			new AMIArgProcessor(cmd);
			Assert.fail("Should throw Exception for bad XPath");
		} catch (Exception e) {
			// OK
		}
	}
	
	@Test
	public void testXPath() {
		String cmd = "-q src/test/resources/org/xmlcml/ami2/bmc/15_1_511 -i scholarly.html --xpath //*[@tagx='cpyrt']"; 
		AMIArgProcessor argProcessor = new AMIArgProcessor(cmd);
		argProcessor.runAndOutput();
		List<? extends Element> sectionList = argProcessor.ensureSectionElements();
		Assert.assertEquals("cpyrt section", 1, sectionList.size());
		argProcessor = new AMIArgProcessor(
				"-q src/test/resources/org/xmlcml/ami2/bmc/15_1_511 -i scholarly.html --xpath //*[@tagx='kwd']");
		argProcessor.runAndOutput();
		sectionList = argProcessor.ensureSectionElements();
		Assert.assertEquals("cpyrt section", 7, sectionList.size());
	}
	
	/** bag of words on papers translated from IEEE HTML into scholarly.
	 * @throws IOException 
	 * 
	 */
	@Test
	public void testNormaAmiIEEE() throws IOException {
		FileUtils.copyDirectory(new File("src/test/resources/org/xmlcml/ami2/ieee"), new File("target/ieee/words"));
		String cmd = ""
				+ "-q target/ieee/words"
				+ " -i scholarly.html"
				+ " --w.words wordFrequencies"
				+ " --w.stopwords /org/xmlcml/ami2/plugins/word/stopwords.txt";
		AMIArgProcessor argProcessor = new WordArgProcessor(cmd);
		argProcessor.runAndOutput();
	}
}
