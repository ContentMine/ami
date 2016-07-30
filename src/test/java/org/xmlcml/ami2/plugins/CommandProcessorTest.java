package org.xmlcml.ami2.plugins;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.Ignore;
import org.junit.Test;
import org.xmlcml.ami2.AMIFixtures;
import org.xmlcml.cmine.util.CMineTestFixtures;

public class CommandProcessorTest {

	private static final Logger LOG = Logger.getLogger(CommandProcessorTest.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	@Test
	public void testCommandLineSyntax() throws IOException {
		String args = "fooDir bar(plugh)";
		CommandProcessor.main(args.split("\\s+"));
	}
	
	@Test
	public void testCommandLineSearch() throws IOException {
		String project = "zika10";
		File projectDir = new File("target/tutorial1/"+project);
		File rawDir = new File(AMIFixtures.TEST_AMI_DIR, project);
		CMineTestFixtures.cleanAndCopyDir(rawDir, projectDir);
		String cmd = ""
				+ " search(tropicalVirus)"
				+ " search(inn)"
				+ " search(disease)"
	    ;
		String[] args = (projectDir+" "+cmd).split("\\s+");
		CommandProcessor.main(args);
	}

	@Test
	public void testCommandLineShort() throws IOException {
		String project = "zika10";
		File projectDir = new File("target/tutorial/"+project);
		File rawDir = new File(AMIFixtures.TEST_AMI_DIR, project);
		CMineTestFixtures.cleanAndCopyDir(rawDir, projectDir);
		String cmd = ""
		+ "word(frequencies)xpath:@count>20~w.stopwords:pmcstop.txt_stopwords.txt"
		+ " sequence(dnaprimer)"
		+ " gene(human) "
		+ " search(tropicalVirus)"
	    ;
		CommandProcessor.main((projectDir+" "+cmd).split("\\s+"));
	}

	@Test
	public void testCommandLinePreprocessor() throws IOException {
		String project = "zika10";
		File projectDir = new File("target/tutorial/"+project);
		File rawDir = new File(AMIFixtures.TEST_AMI_DIR, project);
		CMineTestFixtures.cleanAndCopyDir(rawDir, projectDir);
		String cmd = ""
		+ "w_fstop"
		+ " sq_d"
		+ " g_h"
		+ " s_tv"
		+ " s_inn"
		+ " s_nal"
		+ " s_phch"
		
	    ;
		CommandProcessor.main((projectDir+" "+cmd).split("\\s+"));
	}

	@Test
	//@Ignore
	// runs defaults
	public void testCommandLineShortEmpty() throws IOException {
		String project = "zika10";
		File projectDir = new File("target/tutorial/"+project);
		File rawDir = new File(AMIFixtures.TEST_AMI_DIR, project);
		CMineTestFixtures.cleanAndCopyDir(rawDir, projectDir);
		CommandProcessor.main(new String[]{projectDir.toString()});
	}

	@Test
	@Ignore // LONG
	public void testCommandLine() throws IOException {
		String project = "zika10";
		File projectDir = new File("target/tutorial/"+project);
		File rawDir = new File(AMIFixtures.TEST_AMI_DIR, project);
		CMineTestFixtures.cleanAndCopyDir(rawDir, projectDir);
		String cmd = "word(frequencies)xpath:@count>20~w.stopwords:pmcstop.txt_stopwords.txt"
		+ " sequence(dnaprimer)"
		+ " gene(human) "
		+ " word(search)w.search:/org/xmlcml/ami2/plugins/dictionary/tropicalVirus.xml"
	    ;
		CommandProcessor.main((projectDir+" "+cmd).split("\\s+"));
	}

	@Test
	@Ignore // as not test directory
	public void  testHindawiSampleMini() throws IOException {
		File rawDir = new File("../../hindawi/samplemini");
		File projectDir = new File("target/tutorial/hindawi/samplemini");
		CMineTestFixtures.cleanAndCopyDir(rawDir, projectDir);
		String cmd = ""
//		+ "word(frequencies)xpath:@count>20~w.stopwords:pmcstop.txt_stopwords.txt"
//		+ " sequence(dnaprimer)"
//		+ " species(binomial)"
//		+ " gene(human) "
		+ " word(search)w.search:/org/xmlcml/ami2/plugins/dictionary/disease.xml"
//		+ " word(search)w.search:/org/xmlcml/ami2/plugins/dictionary/phytochemicals2.xml"
//		+ " word(search)w.search:/org/xmlcml/ami2/plugins/dictionary/inn.xml"
	    ;
		CommandProcessor.main((projectDir+" "+cmd).split("\\s+"));
	}
		
	@Test
	@Ignore // as not test directory
	public void  testHindawiEPMC() throws IOException {
		
		File rawDir = new File("../../hindawi/epmc");
		File projectDir = new File("target/tutorial/hindawi/epmc");
		CMineTestFixtures.cleanAndCopyDir(rawDir, projectDir);
		String cmd = "word(frequencies)xpath:@count>20~w.stopwords:pmcstop.txt_stopwords.txt"
		+ " sequence(dnaprimer)"
		+ " species(binomial)"
		+ " gene(human) "
		+ " word(search)w.search:/org/xmlcml/ami2/plugins/dictionary/disease.xml"
		+ " word(search)w.search:/org/xmlcml/ami2/plugins/dictionary/phytochemicals2.xml"
		+ " word(search)w.search:/org/xmlcml/ami2/plugins/dictionary/inn.xml"
	    ;
		CommandProcessor.main((projectDir+" "+cmd).split("\\s+"));
			
	}
}
