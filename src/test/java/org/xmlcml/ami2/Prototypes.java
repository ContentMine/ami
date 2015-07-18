package org.xmlcml.ami2;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.xmlcml.ami2.plugins.word.WordPlugin;
import org.xmlcml.norma.Norma;

public class Prototypes {

	public static void main(String[] args) throws Exception {
//		runHalThesis1();
//		runHalThesis2();
//		runHalTheses();
//		runSpanishThesis();
		runSpanishThesis1();
//		createPDFImages("journal.pone.0115884a");
	}

	private static void runHalThesis1() {
//		new Norma().run("-q examples/theses/HalThesis1 -i fulltext.pdf -o fulltext.pdf.txt --transform pdf2txt");
	}

	private static void runHalThesis2() {
		WordPlugin wordPlugin = new WordPlugin("-q examples/theses/HalThesis2 -i fulltext.pdf.txt --w.words wordFrequencies "
				+ "--w.stopwords "
				+ "     /org/xmlcml/ami2/plugins/word/stopwords.txt "
				+ "     /org/xmlcml/ami2/plugins/word/stopwords.pub.txt "
				+ "     /org/xmlcml/ami2/plugins/word/stopwords.fr.txt");
		wordPlugin.runAndOutput();
	}
	
	private static void runHalTheses() {
		
		createPDFTXT("These_Nathalie_Mitton");
		createPDFTXT("Thesis_Calligari");
		createPDFTXT("20130912_Fei_YAO");
		createPDFTXT("HalThesis2");
		createPDFTXT("smigaj");
		createPDFTXT("TH2013PEST1177");

		
		WordPlugin wordPlugin = new WordPlugin("-q "
				+ "     examples/theses/HalThesis2"
				+ "     examples/theses/These_Nathalie_Mitton"
				+ "     examples/theses/20130912_Fei_YAO"
				+ "     examples/theses/smigaj"
				+ "     examples/theses/TH2013PEST1177"
				+ "     examples/theses/Thesis_Calligari"
				+ " -i fulltext.pdf.txt --w.words wordFrequencies "
				+ " --w.stopwords "
				+ "     /org/xmlcml/ami2/plugins/word/stopwords.txt "
				+ "     /org/xmlcml/ami2/plugins/word/stopwords.pub.txt "
				+ "     /org/xmlcml/ami2/plugins/word/stopwords.fr.txt");
		wordPlugin.runAndOutput();
	}

	
	private static void runSpanishThesis() {
		createPDFTXT("tesis_alexv6.5");
		WordPlugin wordPlugin = new WordPlugin("-q "
				+ "     examples/theses/tesis_alexv6.5"
				+ " -i fulltext.pdf.txt --w.words wordFrequencies "
				+ " --w.stopwords "
				+ "     /org/xmlcml/ami2/plugins/word/stopwords.txt "
				+ "     /org/xmlcml/ami2/plugins/word/stopwords.pub.txt "
				+ "     /org/xmlcml/ami2/plugins/word/stop-words_spanish_es.txt");
		wordPlugin.runAndOutput();
		
	}

	private static void runSpanishThesis1() {
		createPDFHTML("tesis_alexv6.5");
		WordPlugin wordPlugin = new WordPlugin("-q "
				+ "     examples/theses/tesis_alexv6.5"
				+ " -i fulltext.pdf.txt --w.words wordFrequencies "
				+ " --w.stopwords "
				+ "     /org/xmlcml/ami2/plugins/word/stopwords.txt "
				+ "     /org/xmlcml/ami2/plugins/word/stopwords.pub.txt "
				+ "     /org/xmlcml/ami2/plugins/word/stop-words_spanish_es.txt");
		wordPlugin.runAndOutput();
		
	}

	private static void createPDFTXT(String name) {
		new Norma().run("-i examples/theses/"+name+".pdf -o examples/theses/");
		new Norma().run("-q examples/theses/"+name+" -i fulltext.pdf -o fulltext.pdf.txt --transform pdf2txt");
	}

	private static void createPDFHTML(String name) {
		new Norma().run("-i examples/theses/"+name+".pdf -o examples/theses1/");
		new Norma().run("-q examples/theses1/"+name+" -i fulltext.pdf -o fulltext.pdf.html --transform pdf2html");
	}
	

	/** extracts images and writes to (new) images/directory.
	 * 
	 * @param name
	 * @throws Exception
	 */
	private static void createPDFImages(String name) throws Exception {
		String cmDirName = "../norma/src/test/resources/org/xmlcml/norma/pubstyle/plosone/"+name+"/";
		String targetName = "../ami-plugin/target/imagetest/";
		FileUtils.copyDirectory(new File(cmDirName), new File(targetName));
		new Norma().run("-q "+targetName+" -i fulltext.pdf -o images/ --transform pdf2images");
	}
	
	
}
