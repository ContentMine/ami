package org.xmlcml.ami2.plugins.phylotree;

import java.awt.image.BufferedImage;
import java.io.File;

import org.apache.log4j.Logger;
import org.xmlcml.diagrams.phylo.PhyloTreeAnalyzer;
import org.xmlcml.graphics.svg.SVGElement;
import org.xmlcml.graphics.svg.linestuff.LineMerger.MergeMethod;
import org.xmlcml.image.pixel.PixelComparator.ComparatorType;
import org.xmlcml.svg2xml.container.ImageContainer;

public class TreeCreator {

	// ===================Called on Visitables===================
	
	private static final Logger LOG = Logger.getLogger(TreeCreator.class);

	private ComparatorType rootPosition = ComparatorType.LEFT; // default
	
	private SVGXTree makeTree(SVGElement svg) {
		SVGXTree tree = SVGXTree.makeTree(svg, 1.0, MergeMethod.TOUCHING_LINES);
		return tree;
	}

	private SVGXTree makeTree(BufferedImage image) {
		SVGXTree tree = new SVGXTree((SVGElement)null);
		PhyloTreeAnalyzer treeAnalyzer = new PhyloTreeAnalyzer();
		treeAnalyzer.setSelectedIslandIndex(0);
		treeAnalyzer.setComputeLengths(true);
//		treeAnalyzer.setNewickFile(outputFile);
		treeAnalyzer.setImage(image);
		treeAnalyzer.processImageIntoGraphsAndTrees();
		return tree;
	}

 }