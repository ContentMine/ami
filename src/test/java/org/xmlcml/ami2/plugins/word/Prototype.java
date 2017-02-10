package org.xmlcml.ami2.plugins.word;

import org.junit.Test;
import org.xmlcml.ami2.plugins.AMIArgProcessor;
import org.xmlcml.ami2.plugins.AMIPlugin;

public class Prototype {

	@Test
	public void testBoWIEEE() {
		String cmd = "-q ../cproject/target/ieee/musti/Henniger/ -i fulltext.txt --w.words wordFrequencies "
				+ "--w.stopwords /org/xmlcml/ami2/wordutil/stopwords.txt "
				+ "    /org/xmlcml/ami2/plugins/word/clinicaltrials200.txt";		
		AMIArgProcessor amiArgProcessor = new WordArgProcessor();
		amiArgProcessor.parseArgs(cmd);
		amiArgProcessor.runAndOutput();
		
	}
	
}
