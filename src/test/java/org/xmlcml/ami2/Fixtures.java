package org.xmlcml.ami2;

import java.io.File;

public class Fixtures {

	public final static File TEST_RESOURCES_DIR    = new File("src/test/resources/");
	public final static File TEST_AMI_DIR          = new File(Fixtures.TEST_RESOURCES_DIR, "org/xmlcml/ami2");
	
	public final static File TEST_BMC_DIR          = new File(Fixtures.TEST_AMI_DIR, "bmc");
	public final static File TEST_BMC_15_1_511_QSN = new File(Fixtures.TEST_BMC_DIR, "15_1_511");

	public final static File TEST_MIXED_DIR        = new File(Fixtures.TEST_AMI_DIR, "mixed");

	public final static File TEST_PLOSONE_DIR      = new File(Fixtures.TEST_AMI_DIR, "plosone");
	public final static File TEST_PLOSONE_0115884  = new File(Fixtures.TEST_PLOSONE_DIR, "journal.pone.0115884");
	public final static File TEST_PLOSONE_SEQUENCE_0121780  = new File(Fixtures.TEST_PLOSONE_DIR, "plosjournal.pone.0121780_sequence");
	public final static File TEST_PLOSONE_MALARIA_0119475  = new File(Fixtures.TEST_PLOSONE_DIR, "journal.pone.0119475");

	public static final File EXAMPLES              = new File("examples");
	public static final File EXAMPLES_TEMP         = new File("target/examples");
	
}
