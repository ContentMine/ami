package org.xmlcml.ami2;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.xmlcml.ami2.ResultsAnalysis.CellType;
import org.xmlcml.ami2.plugins.AMIArgProcessor;
import org.xmlcml.ami2.plugins.CommandProcessor;
import org.xmlcml.ami2.plugins.regex.RegexArgProcessor;
import org.xmlcml.ami2.plugins.word.WordArgProcessor;
import org.xmlcml.ami2.plugins.word.WordTest;
import org.xmlcml.cmine.files.CProject;
import org.xmlcml.cmine.util.CMineTestFixtures;
import org.xmlcml.cmine.util.CellRenderer;
import org.xmlcml.cmine.util.DataTablesTool;
import org.xmlcml.html.HtmlHtml;
import org.xmlcml.html.HtmlTable;
import org.xmlcml.html.HtmlTd;
import org.xmlcml.norma.NormaArgProcessor;
import org.xmlcml.norma.biblio.json.EPMCConverter;
import org.xmlcml.xml.XMLUtil;

@Ignore
public class LargeTests {
	
	File large = new File("../patents/US08979");

	@Before
	public void setUp() {
		if (!large.exists()) return; // only on PMR machine
		if (!new File(large, "US08979000-20150317/scholarly.html").exists()) {
			String args = "-i fulltext.xml  --transform uspto2html -o scholarly.html --project "+large;
			NormaArgProcessor argProcessor = new NormaArgProcessor(args);
		}
	}
	
	@Test
	// TESTED 2016-01-12
	@Ignore
	public void testLargeWordFrequencies() {
		if (!large.exists()) return; // only on PMR machine
		String args = "-i scholarly.html  --w.words "+WordArgProcessor.WORD_FREQUENCIES+" --w.stopwords "+WordTest.STOPWORDS_TXT+" --project "+large;
		WordArgProcessor argProcessor = new WordArgProcessor(args);
		argProcessor.runAndOutput();
		AMIFixtures.checkResultsElementList(argProcessor, 1, 0, 
				"<results title=\"frequencies\">"
				+ "<result title=\"frequency\" word=\"applicant\" count=\"427\" />"
				+ "<result title=\"frequency\" word=\"citation:\" count=\"427\" />"
				+ "<result title=\"frequency\" word=\"cited\" count=\"427\" />"
				+ "<result title=\"frequency\" word=\"document-id::\" count=\"279\" />"
				+ "<result title=\"frequency\" word=\"[patcit]:\" ");
	}
	
	@Test
	// TESTED 2016-01-12
	// expensive
	@Ignore
	public void testLargeConsortRegex() {
		String args = "-i scholarly.html  --context 25 40 --r.regex regex/synbio.xml --project "+large; 
		RegexArgProcessor argProcessor = new RegexArgProcessor(args);
		argProcessor.runAndOutput();
		AMIFixtures.checkResultsElementList(argProcessor, 1, 0, 
				"<results title=\"synbio\" />");
	}

	@Test
	// TESTED 2016-01-12
	@Ignore
	public void testLargeProject() {
		File large = new File("../patents/US08979");
		if (!large.exists()) return; // only on PMR machine
//		runNorma(large);
		// word frequencies
		String argsx = "-i scholarly.html  --w.words "+WordArgProcessor.WORD_FREQUENCIES+
				" --w.stopwords "+WordTest.STOPWORDS_TXT+" --w.case ignore --w.stem true --project "+large;
		AMIArgProcessor argProcessor = new WordArgProcessor(argsx);
		argProcessor.runAndOutput();
		AMIFixtures.checkResultsElementList(argProcessor, 1, 0, 
				"<results title=\"frequencies\"><result title=\"frequency\" word=\"applic\" count=\"453\" />"
				+ "<result title=\"frequency\" word=\"citat\" count=\"428\" />");
		}

	@Test
	// TESTED 2016-01-12
	public void testSynbio() {
		File large = new File("../patents/US08979");
		if (!large.exists()) return; // only on PMR machine
//		runNorma(large);
		String args = "-i scholarly.html --clean results/* --w.search /org/xmlcml/ami2/plugins/synbio/synbio.xml --project "+large;
		AMIArgProcessor argProcessor = new WordArgProcessor(args);
		argProcessor.runAndOutput();
		AMIFixtures.checkResultsElementList(argProcessor, 1, 0, 
				"<results title=\"synbioPhrases\" />");
	}
	
	@Test
	// TESTED 2016-01-12
	@Ignore
	public void testSynbioStem() {
		File large = new File("../patents/US08979");
		if (!large.exists()) return; // only on PMR machine
//		runNorma(large);
		String args = "-i scholarly.html  --w.search /org/xmlcml/ami2/plugins/synbio/synbio.xml --w.stem true --project "+large;
		AMIArgProcessor argProcessor = new WordArgProcessor(args);
		argProcessor.runAndOutput();
		// the last result has no synbio
		AMIFixtures.checkResultsElementList(argProcessor, 1, 0, 
				"<results title=\"synbioPhrases\" />");
	}
	
	@Test
	@Ignore
	public void testArmillaria() throws IOException {
		runDefault("armillaria");
	}

	@Test
	@Ignore
	public void testMicrobiome() throws IOException {
		runDefault("microbiome");
	}

	@Test
//	@Ignore
	public void testQuoll() throws IOException {
		runDefault("quoll");
	}

	@Test
//	@Ignore
	public void testSTD() throws IOException {
		runDefault("std");
	}

	@Test
//	@Ignore
	public void testWolbachia() throws IOException {
		runDefault("wolbachia2015");
	}

	@Test
//	@Ignore
	public void testTasman() throws IOException {
		runDefault("tasman");
	}
	
	@Test
//	@Ignore
	public void testOettinger() throws IOException {
		runDefault("oettinger");
	}

	@Test
//	@Ignore
	public void testZika() throws IOException {
		runDefault("zika2");
	}

	@Test
//	@Ignore
	public void testSemipartial() throws IOException {
		runStatisticsDefault("semipartial");
	}

	@Test
//	@Ignore
	public void testTerrorism() throws IOException {
		runDefault("terrorism2015");
	}

	@Test
//	@Ignore
	public void ngfpoly() throws IOException {
		runDefault("ngfpoly");
	}

	@Test
//	@Ignore
	public void testApelin() throws IOException {
		runDefault("apelin2015");
	}

	@Test
//	@Ignore
	public void testZika10() throws IOException {
		runBioscienceDefault("zika10", new File("src/test/resources/org/xmlcml/ami2/zika10/"));
	}

	@Test
//	@Ignore
	public void testKakadu() throws IOException {
		runDefault("kakadu");
	}

	@Test
	@Ignore
	public void testAyeAye() throws IOException {
		runDefault("ayeaye");
	}
	
	private void runDefault(String project) throws IOException {
		File rawDir = new File("../projects/"+project);
		runBioscienceDefault(project, rawDir);
	}

	private void runBioscienceDefault(String project, File rawDir) throws IOException {
		File projectDir = new File("target/tutorial/"+project+"/");
		CMineTestFixtures.cleanAndCopyDir(rawDir, projectDir);
		
		CommandProcessor commandProcessor = new CommandProcessor(projectDir);
		commandProcessor.runCommands(""
				+ "species(binomial,genus) "
				+ " gene(human)"
				+ " word(frequencies)xpath:@count>20~w.stopwords:pmcstop.txt_stopwords.txt"
				+ " sequence(dnaprimer) "
				+ "");
		createDataTables(project, projectDir);
	}

	private void runStatisticsDefault(String project) throws IOException {
		File rawDir = new File("../projects/"+project);
		runStatisticsDefault(project, rawDir);
	}

	private void runStatisticsDefault(String project, File rawDir) throws IOException {
		File projectDir = new File("target/tutorial/"+project+"/");
		CMineTestFixtures.cleanAndCopyDir(rawDir, projectDir);
		
		CommandProcessor commandProcessor = new CommandProcessor(projectDir);
		commandProcessor.runCommands(""
				+ "regex(regex/statistics.xml)"
//				+ " word(frequencies)xpath:@count>20~w.stopwords:pmcstop.txt_stopwords.txt"
				+ " word(search)w.search:/org/xmlcml/ami2/plugins/statistics/statistics.xml"
				+ "");
		createDataTables(project, projectDir);
	}

	private void createDataTables(String project, File projectDir) throws IOException {
		DataTablesTool dataTablesTool = new DataTablesTool();
		dataTablesTool.setTitle(project);
		ResultsAnalysis resultsAnalysis = new ResultsAnalysis(dataTablesTool);
		resultsAnalysis.addDefaultSnippets(projectDir);
		resultsAnalysis.setRemoteLink0(EPMCConverter.HTTP_EUROPEPMC_ORG_ARTICLES);
		resultsAnalysis.setRemoteLink1("");
		resultsAnalysis.setLocalLink0("");
		resultsAnalysis.setLocalLink1(ResultsAnalysis.SCHOLARLY_HTML);
		resultsAnalysis.setRowHeadingName("EPMCID");
		for (CellType cellType : ResultsAnalysis.CELL_TYPES) {
			resultsAnalysis.setCellContentFlag(cellType);
			HtmlTable table = resultsAnalysis.makeHtmlDataTable();
			HtmlHtml html = dataTablesTool.createHtmlWithDataTable(table);
			File outfile = new File(projectDir, cellType.toString()+"."+CProject.DATA_TABLES_HTML);
			XMLUtil.debug(html, outfile, 1);
		}
		List<HtmlTd> footerList = new ArrayList<HtmlTd>();
		for (CellRenderer head : dataTablesTool.columnHeadingList) {
			HtmlTd td = new HtmlTd();
			td.appendChild(head.getValue());
			footerList.add(td);
		}
		HtmlTd caption = new HtmlTd();
		caption.appendChild("coun-ts");
		dataTablesTool.setFooterCaption(caption);
		dataTablesTool.setFooterCells(footerList);
	}



	

}
