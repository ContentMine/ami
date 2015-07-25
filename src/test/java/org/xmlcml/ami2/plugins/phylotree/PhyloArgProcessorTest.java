package org.xmlcml.ami2.plugins.phylotree;

import java.io.File;

import nu.xom.Builder;
import nu.xom.Element;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.xmlcml.ami2.Fixtures;
import org.xmlcml.ami2.plugins.phylotree.PhyloTreePlugin;
import org.xmlcml.cmine.args.DefaultArgProcessor;
import org.xmlcml.cmine.files.CMDir;

public class PhyloArgProcessorTest {

	
	
	private static final Logger LOG = Logger.getLogger(PhyloArgProcessorTest.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	@Test
	public void testPhyloArgProcessor() throws Exception {
		File newDir = new File("target/ijsem/phylo");
	}
	
	@Test
	@Ignore // accesses net
	public void testPhyloArgProcessorLookup() throws Exception {
	}

	
	@Test
	/** very thick lines - behaves badly in thinning. But overall works.
	 * Needs 0- and 2-connected nodes merging...
	 * 
	 * 
	 * @throws Exception
	 */
	@Ignore("requires tesseract")
	public void testPhyloHarness() throws Exception {
		CMDir cmDir = new CMDir(new File(Fixtures.TEST_PHYLO_DIR, "ijs_0_000174_0"));
		File normaTemp = new File("target/phylo/ijs_0_000174_0");
		cmDir.copyTo(normaTemp, true);
		String cmd = "--ph.phylo -q target/phylo/ijs_0_000174_0 -i image/000.pbm.png -o target/phylo/junk.xml"; 
		PhyloTreePlugin phyloPlugin = new PhyloTreePlugin(cmd);
		phyloPlugin.runAndOutput();
	}

	@Test
	/** a few edge ends not joined to nodes, probably due to bad nodes.
	 * 
	 * @throws Exception
	 */
	@Ignore("requires tesseract")
	public void testPhyloName() throws Exception {
//		String name = "ijs_0_000265_0"; String img = "000";
		String name = "ijs_0_000364_0"; String img = "003";
		CMDir cmDir = new CMDir(new File(Fixtures.TEST_PHYLO_DIR, name));
		File normaTemp = new File("target/phylo/"+name);
		cmDir.copyTo(normaTemp, true);
		String cmd = "--ph.phylo -q target/phylo/"+name+" -i image/"+img+".pbm.png -o target/phylotest/"+name; 
		PhyloTreePlugin phyloPlugin = new PhyloTreePlugin(cmd);
		phyloPlugin.runAndOutput();
	}

	

	
}
