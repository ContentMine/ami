package org.xmlcml.ami2.plugins.eng;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.xmlcml.ami2.plugins.word.WordArgProcessor;
import org.xmlcml.ami2.plugins.word.WordTest;
import org.xmlcml.cmine.args.DefaultArgProcessor;
import org.xmlcml.norma.Norma;

public class EngTest {

	@Test
	public void testReadIEEE() throws IOException {
		String ENG = "src/test/resources/org/xmlcml/ami2/engineering/ieee_1196402_html";
		FileUtils.copyDirectory(new File(ENG), new File("target/eng/"));
		String args = 
				"-q target/eng/ --w.words "+WordArgProcessor.WORD_FREQUENCIES +" --w.stopwords "+WordTest.STOPWORDS_TXT  ;
			DefaultArgProcessor argProcessor = new WordArgProcessor(args);
			argProcessor.runAndOutput();

	}
	
	@Test
	public void testReadIEEEPDF() throws IOException {
		String ENG = "src/test/resources/org/xmlcml/ami2/engineering/01268283";
		FileUtils.copyDirectory(new File(ENG), new File("target/eng/01268283/"));
		Norma norma = new Norma();
		String args = 
				"-q target/eng/01268283/ -i fulltext.pdf -o fulltext.txt --transform pdf2txt";
		norma.run(args);
		args = 
				"-q target/eng/01268283/ -i fulltext.txt --w.words "+WordArgProcessor.WORD_FREQUENCIES +" --w.stopwords "+WordTest.STOPWORDS_TXT  ;
			DefaultArgProcessor argProcessor = new WordArgProcessor(args);
			argProcessor.runAndOutput();

	}
	
}
