package org.xmlcml.ami2.plugins.phylotree;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import javax.imageio.ImageIO;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.vafer.jdeb.shaded.compress.io.FilenameUtils;
import org.xmlcml.ami2.plugins.AMIArgProcessor;
import org.xmlcml.ami2.plugins.phylotree.nexml.NexmlFactory;
import org.xmlcml.ami2.plugins.phylotree.nexml.NexmlNEXML;
import org.xmlcml.cmine.args.ArgumentOption;
import org.xmlcml.cmine.files.CMDir;
import org.xmlcml.diagrams.DiagramTree;
import org.xmlcml.diagrams.phylo.PhyloTreePixelAnalyzer;
import org.xmlcml.graphics.svg.SVGElement;
import org.xmlcml.graphics.svg.SVGSVG;
import org.xmlcml.graphics.svg.linestuff.LineMerger.MergeMethod;
import org.xmlcml.image.pixel.PixelGraph;
import org.xmlcml.image.pixel.PixelNode;
import org.xmlcml.xml.XMLUtil;

/** 
 * Processes commandline arguments.
 * 
 * @author pm286
 */
public class PhyloTreeArgProcessor extends AMIArgProcessor {
	
	public static final Logger LOG = Logger.getLogger(PhyloTreeArgProcessor.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	protected List<String> words;
	private SVGXTree tree;

	public PhyloTreeArgProcessor() {
		super();
	}

	public PhyloTreeArgProcessor(String[] args) {
		this();
		parseArgs(args);
	}

	public PhyloTreeArgProcessor(String argString) {
		this(argString.split(WHITESPACE));
	}

	// =============== METHODS ==============

	public void runPhylo(ArgumentOption option) {
		for (String tree : inputList) {
			File inputFile = new File(currentCMDir.getDirectory(), tree);
			createTree(inputFile);
		}
	}
	
	public void outputMethod(ArgumentOption option) {
		LOG.error("outputMethod NYI");
	}


	public void outputResultElements(ArgumentOption option) {
		LOG.debug("outputResultElement NYI "+output+"; need to add tree");
	}
	
	// =============================

	private void createTree(File inputFile) {
		String suffix = FilenameUtils.getExtension(inputFile.toString());
		tree = null;
		try {
			if (CMDir.isImageSuffix(suffix)) {
				createNexmlAndTreeFromPixels(inputFile);
			} else if (CMDir.isSVG(suffix)) {
				createNexmlAndTreeFromSVG(inputFile);
			} else {
				throw new RuntimeException("Cannot process as phylotree: "+inputFile);
			}
			new File("target/phylotest/").mkdirs();
			XMLUtil.debug(tree, new FileOutputStream(output), 1);
		} catch (Exception e) {
			throw new RuntimeException("Cannot read/interpret tree: "+inputFile, e);
		}
	}

	private void createNexmlAndTreeFromSVG(File inputFile) {
		SVGElement svgElement = SVGElement.readAndCreateSVG(inputFile);
		SVGXTree tree1 = SVGXTree.makeTree(svgElement, 1.0, MergeMethod.TOUCHING_LINES);
		tree = tree1;
		LOG.debug("tree "+tree.toXML());
	}

	private void createNexmlAndTreeFromPixels(File inputFile) throws IOException {
		BufferedImage image = ImageIO.read(inputFile);
		PhyloTreePixelAnalyzer phyloTreePixelAnalyzer = createAndConfigurePixelAnalyzer(image);
		DiagramTree diagramTree = phyloTreePixelAnalyzer.processImageIntoGraphsAndTree();
		PixelNode rootPixelNode = diagramTree.getRootPixelNode();
		PixelGraph graph = diagramTree.getGraph();
		LOG.debug("Root pixelNode "+rootPixelNode);
		// use root node later...
		graph.tidyNodesAndEdges(5.0);
		diagramTree = new PhyloTreePixelAnalyzer().createFromGraph(graph, rootPixelNode);
		SVGSVG.wrapAndWriteAsSVG(diagramTree.getOrCreateSVG(), new File("target/phylotree/diagram.svg"));
		NexmlFactory nexmlFactory = new NexmlFactory();
		nexmlFactory.setRootPixelNode(rootPixelNode);
		nexmlFactory.createAndAddNexmlTree(diagramTree);
		NexmlNEXML nexmlNEXML = nexmlFactory.getOrCreateNexmlNEXML();
		XMLUtil.debug(nexmlNEXML, new FileOutputStream("target/phylotree/tree.nexml"), 1);
		String newick = nexmlNEXML.createNewick();
		FileUtils.write(new File("target/phylotree/tree.nwk"), newick);
		LOG.debug("NEWICK "+newick);
	}

	private PhyloTreePixelAnalyzer createAndConfigurePixelAnalyzer(BufferedImage image) {
		PhyloTreePixelAnalyzer phyloTreePixelAnalyzer = new PhyloTreePixelAnalyzer();
		phyloTreePixelAnalyzer.setSelectedIslandIndex(0);
		phyloTreePixelAnalyzer.setComputeLengths(true);
		phyloTreePixelAnalyzer.setImage(image);
		return phyloTreePixelAnalyzer;
	}

	
}
