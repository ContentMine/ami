package org.xmlcml.ami2;

import org.xmlcml.ami2.plugins.word.WordPlugin;
import org.xmlcml.norma.Norma;

public class Prototypes {

	public static void main(String[] args) {
//		runHalThesis1();
//		runHalThesis2();
		runHalTheses();
	}

	private static void runHalThesis1() {
//		new Norma().run("-q examples/theses/HalThesis1 -i fulltext.pdf -o fulltext.pdf.txt --xsl pdf2txt");
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
	

	private static void createPDFTXT(String name) {
		new Norma().run("-i examples/theses/"+name+".pdf -o examples/theses/");
		new Norma().run("-q examples/theses/"+name+" -i fulltext.pdf -o fulltext.pdf.txt --xsl pdf2txt");
	}
}
