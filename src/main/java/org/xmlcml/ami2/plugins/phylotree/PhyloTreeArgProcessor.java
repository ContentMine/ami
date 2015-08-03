package org.xmlcml.ami2.plugins.phylotree;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.ami2.plugins.AMIArgProcessor;
import org.xmlcml.ami2.plugins.phylotree.nexml.NexmlElement;
import org.xmlcml.ami2.plugins.phylotree.nexml.NexmlFactory;
import org.xmlcml.ami2.plugins.phylotree.nexml.NexmlNEXML;
import org.xmlcml.ami2.plugins.phylotree.nexml.NexmlNode;
import org.xmlcml.ami2.plugins.phylotree.nexml.NexmlTree;
import org.xmlcml.cmine.args.ArgumentOption;
import org.xmlcml.cmine.files.CMDir;
import org.xmlcml.cmine.files.ResultsElement;
import org.xmlcml.diagrams.DiagramTree;
import org.xmlcml.diagrams.phylo.PhyloTreePixelAnalyzer;
import org.xmlcml.euclid.Real2;
import org.xmlcml.euclid.Real2Range;
import org.xmlcml.euclid.Real2Range.BoxDirection;
import org.xmlcml.euclid.RealRange;
import org.xmlcml.graphics.svg.SVGElement;
import org.xmlcml.graphics.svg.SVGSVG;
import org.xmlcml.graphics.svg.linestuff.LineMerger.MergeMethod;
import org.xmlcml.graphics.svg.text.SVGPhrase;
import org.xmlcml.html.HtmlSpan;
import org.xmlcml.image.pixel.PixelGraph;
import org.xmlcml.image.pixel.PixelNode;
import org.xmlcml.norma.image.ocr.HOCRReader;
import org.xmlcml.norma.image.ocr.ImageToHOCRConverter;
import org.xmlcml.xml.XMLUtil;

/** 
 * Processes commandline arguments.
 * 
 * @author pm286
 */
public class PhyloTreeArgProcessor extends AMIArgProcessor {
	

	private static final String TREES = "trees";
	private static final String HOCR_SVG_SUFFIX = ".pbm.png.hocr.svg";
	public static final Logger LOG = Logger.getLogger(PhyloTreeArgProcessor.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	private static Real2Range DEFAULT_HOCR_WORD_JOINING_BOX = new Real2Range(new RealRange(0.0, 20.0), new RealRange(-5.0, 5.0));
	private static final String HOCR_SUFFIX = ".pbm.png.hocr";
	private static final String HOCR_HTML_SUFFIX = ".pbm.png.hocr.html";
	private static final int DEFAULT_RETRIES_FOR_TESSERACT_EXIT = 60;
	private static final String PNG = ".png";

	private SVGXTree svgxTree;
	private HOCRReader hocrReader;
	private ImageToHOCRConverter imageToHOCRConverter;
	private Pattern speciesPattern;
	private String outputRoot;
	private File outputDir;
	private DiagramTree diagramTree;
	private PhyloTreePixelAnalyzer phyloTreePixelAnalyzer;
	private NexmlNEXML nexml;
	private Double joiningRadius = 40.0;
	private int maxPhraseLength = 4;

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
		for (String input : inputList) {
			File inputFile = new File(currentCMDir.getDirectory(), input);
			createTree(inputFile);
		}
	}
	
	public void outputMethod(ArgumentOption option) {
		LOG.error("outputMethod NYI");
	}


	public void outputResultsElement(ArgumentOption option) {
		ResultsElement resultsElement = new ResultsElement(TREES);
		LOG.debug("outputResultElement NYI "+output+"; need to add tree");
	}
	
	// =============================

	private void createTree(File inputFile) {
		String suffix = FilenameUtils.getExtension(inputFile.toString());
		svgxTree = null;
		try {
			if (CMDir.isImageSuffix(suffix)) {
				createNexmlAndTreeFromPixels(inputFile);
			} else if (CMDir.isSVG(suffix)) {
				createNexmlAndTreeFromSVG(inputFile);
			} else {
				throw new RuntimeException("Cannot process as phylotree: "+inputFile);
			}
			if (output != null) {
				File outputFile = new File(output);
				outputFile.getParentFile().mkdirs();
				XMLUtil.debug(svgxTree, new FileOutputStream(outputFile), 1);
			}
			PhyloResultsElement resultsElement = new PhyloResultsElement(getTitle());
		} catch (Exception e) {
			throw new RuntimeException("Cannot read/interpret tree: "+inputFile, e);
		}
	}

	private String getTitle() {
		return "dummyTitle";
	}

	public void createNexmlAndTreeFromSVG(File svgInputFile) {
		SVGElement svgElement = SVGElement.readAndCreateSVG(svgInputFile);
		SVGXTree tree1 = SVGXTree.makeTree(svgElement, 1.0, MergeMethod.TOUCHING_LINES);
		svgxTree = tree1;
		LOG.trace("tree "+svgxTree.toXML());
	}

	public NexmlNEXML createNexmlAndTreeFromPixels(File inputImageFile) throws IOException {
		if (inputImageFile != null && inputImageFile.exists()) {
			BufferedImage image = ImageIO.read(inputImageFile);
			phyloTreePixelAnalyzer = createAndConfigurePixelAnalyzer(image);
			diagramTree = phyloTreePixelAnalyzer.processImageIntoGraphsAndTree();
			if (diagramTree == null) {
				return null;
			}
			PixelNode rootPixelNode = diagramTree.getRootPixelNode();
			PixelGraph graph = diagramTree.getGraph();
			// use root node later...
			graph.tidyNodesAndEdges(5.0);
			diagramTree = new PhyloTreePixelAnalyzer().createFromGraph(graph, rootPixelNode);
			NexmlFactory nexmlFactory = new NexmlFactory();
			nexmlFactory.setRootPixelNode(rootPixelNode);
			nexmlFactory.createAndAddNexmlTree(diagramTree);
			nexml = nexmlFactory.getOrCreateNexmlNEXML();
		}
		return nexml;
	}
	
	public NexmlNEXML getNexml() {
		return nexml;
	}
	public DiagramTree getDiagramTree() {
		return diagramTree;
	}

	private PhyloTreePixelAnalyzer createAndConfigurePixelAnalyzer(BufferedImage image) {
		PhyloTreePixelAnalyzer phyloTreePixelAnalyzer = new PhyloTreePixelAnalyzer();
		phyloTreePixelAnalyzer.setSelectedIslandIndex(0);
		phyloTreePixelAnalyzer.setComputeLengths(true);
		phyloTreePixelAnalyzer.setImage(image);
		return phyloTreePixelAnalyzer;
	}

	public HOCRReader getOrCreateHOCRReader() {
		if (hocrReader == null) {
			hocrReader = new HOCRReader();
			hocrReader.setJoiningBox(DEFAULT_HOCR_WORD_JOINING_BOX);
		}
		return hocrReader;
	}

	/** matches tips to labels.
	 * 
	 * creates lists of failed tips (failedTipList) and failed labels (failedLabels).
	 * 
	 * @param wordLineList
	 * @param nexmlTree
	 */
	public void matchPhrasesToNodes(List<SVGPhrase> unusedPhraseList, NexmlTree nexmlTree) {
		List<NexmlNode> tipNodeList = nexmlTree.getOrCreateTipNodeList();
		annotateMatchedNodesAndDecrementUnmatchedLists(tipNodeList, unusedPhraseList, hocrReader.getWordJoiningBox(), null);
		List<NexmlNode> branchNodeList = nexmlTree.getOrCreateNonTipNodeList();
		annotateMatchedNodesAndDecrementUnmatchedLists(branchNodeList, unusedPhraseList, null, getJoiningRadius());
		
	}

	/** matches nodes against phrases.
	 * 
	 * Brute force algorithm - replace with Hungarian later
	 * 
	 * @param unmatchedNodeList decremented for each match
	 * @param unusedPhraseList decremented for each match
	 */
	private void annotateMatchedNodesAndDecrementUnmatchedLists(
			List<NexmlNode> unmatchedNodeList, List<SVGPhrase> unusedPhraseList, Real2Range joiningBox, Double joiningRadius) {
		List<SVGPhrase> matchedPhraseList = new ArrayList<SVGPhrase>();
		List<NexmlNode> matchedNodeList = new ArrayList<NexmlNode>();
		for (NexmlNode unmatchedNode : unmatchedNodeList) {
			Real2 tipXY2 = unmatchedNode.getXY2();
			List<SVGPhrase> phrases = this.annotateNodesWithMatchedPhrases(unusedPhraseList, tipXY2, joiningBox, joiningRadius);
			if (phrases.size() == 1) {
				String label = phrases.get(0).toString();
				if (joiningRadius != null) {
					unmatchedNode.setLabel(label);
				} else {
					unmatchedNode.setOtuValue(label);
				}
				matchedPhraseList.add(phrases.get(0));
			} else if (phrases.size() > 1) {
				LOG.error("competing words for tip");
			} else if (phrases.size() == 0) {
				LOG.trace("failed to find phrases to match node:" +unmatchedNode.getLabelString()+"("+unmatchedNode.getXY2()+")");
//				unmatchedTipList.add(unmatchedNode);
			}
		}
		unusedPhraseList.removeAll(matchedPhraseList);
		unmatchedNodeList.removeAll(matchedNodeList);
		
		if (unusedPhraseList.size() > 0) {
			LOG.trace("unmatched phrases: \n"+unusedPhraseList);
		}
		if (unmatchedNodeList.size() > 0) {
			LOG.trace("unmatched tips: \n"+unmatchedNodeList);
		}
	}
	
	/** finds lines within joining box of XY2.
	 * 
	 * Ideally wordLineList should be size==1
	 * 
	 * @param phraseList
	 * @param xy2
	 * @param joiningBox if not-null joins horizontally to node
	 * @param joiningRadius if not null joins radially (for short words)
	 * @return
	 */
	private List<SVGPhrase> annotateNodesWithMatchedPhrases(List<SVGPhrase> phraseList, Real2 xy2, Real2Range joiningBox, Double joiningRadius) {
		getOrCreateHOCRReader();
		List<SVGPhrase> matchedPhraseList = new ArrayList<SVGPhrase>();
		if (phraseList != null) {
			for (SVGPhrase phrase : phraseList) {
				Real2Range phraseBox = phrase == null ? null : phrase.getBoundingBox();
				if (phraseBox != null) {
					if (joiningBox != null) {
						Real2 phraseXY2 = phraseBox.getMidPoint(BoxDirection.LEFT);
						Real2 diffXY2 = phraseXY2.subtract(xy2); 
						if (joiningBox.includes(diffXY2)) {
							matchedPhraseList.add(phrase);
						}
					} else if (joiningRadius != null && phrase.toString().length() < maxPhraseLength) {
						double dist = xy2.getDistance(phrase.getBoundingBox().getCentroid());
						if (dist < 50) {
							LOG.trace(dist);
						}
						if (dist < joiningRadius) {
							matchedPhraseList.add(phrase);
						}
					}
				}
			}
		}
		return matchedPhraseList;
	}

	public ImageToHOCRConverter getOrCreateImageToHOCRConverter() {
		if (imageToHOCRConverter == null) {
			imageToHOCRConverter = new ImageToHOCRConverter();
			imageToHOCRConverter.setTryCount(DEFAULT_RETRIES_FOR_TESSERACT_EXIT);
		}
		return imageToHOCRConverter;
	}

	public NexmlNEXML mergeFiles(File hocrFile, File nexmlFile) throws Exception {
		getOrCreateHOCRReader();
		hocrReader.createWordLineList(hocrFile);
		NexmlNEXML nexml = (NexmlNEXML) NexmlElement.readAndCreateNEXML(nexmlFile);
		this.mergeOCRAndPixelTree(hocrReader, nexml);
		return nexml;
	}

	public boolean mergeOCRAndPixelTree(File imageFile) throws IOException, InterruptedException {
		hocrReader = createHOCRReaderAndProcess(imageFile);
		if (hocrReader == null) return false;
		NexmlNEXML nexml = this.createNexmlAndTreeFromPixels(imageFile);
		mergeOCRAndPixelTree(hocrReader, nexml);
		return true;
	}

	/**
	 * 
	 * @param hocrReader
	 * @param nexml modified by the process
	 */
	public void mergeOCRAndPixelTree(HOCRReader hocrReader, NexmlNEXML nexml) {
		if (nexml == null) {
			LOG.error("Cannot create tree");
		} else {
			NexmlTree nexmlTree = nexml.getSingleTree();
			List<SVGPhrase> unusedPhraseList = new ArrayList<SVGPhrase>(hocrReader.getOrCreatePhraseList());
			this.matchPhrasesToNodes(unusedPhraseList, nexmlTree);
		}
	}

	private HOCRReader createHOCRReaderAndProcess(File imageFile) throws IOException,
			InterruptedException, FileNotFoundException {
		ensureOutputDirectory();
		outputDir.mkdirs();
		File hocrOutfile = createHocrOutputFileDescriptor();
		getOrCreateImageToHOCRConverter();
		File htmlFile = imageToHOCRConverter.convertImageToHOCR(imageFile, hocrOutfile);
		if (htmlFile == null || !htmlFile.exists()) {
			LOG.error("cannot run tesseract");
			return null;
		} else {
			getOrCreateHOCRReader();
			hocrReader.readHOCR(new FileInputStream(htmlFile));
		}
		return hocrReader;
	}

	/** can check species from HOCR.
	 * 
	 * @param svgSvg
	 * @throws Exception
	 */
	private void checkSpecies(SVGSVG svgSvg) throws Exception {
		if (svgSvg != null) {
			matchSpecies(hocrReader);
			File resultsFile = createHocrSVGFileDescriptor();
			XMLUtil.debug(svgSvg, new FileOutputStream(resultsFile), 1);
		}
	}
	
	private void ensureOutputDirectory() {
		if (outputDir == null) {
			outputDir = new File("target/junk/"+System.currentTimeMillis()+"/");
			LOG.debug("PhyloTree output to: "+outputDir+"; suggest you setOutputDir()");
		}
	}

	private File createHocrOutputHtmlFileDescriptor() {
		ensureOutputDirectory();
		return new File(outputDir, outputRoot+HOCR_HTML_SUFFIX);
	}

	private File createHocrOutputFileDescriptor() {
		ensureOutputDirectory();
		return new File(outputDir, outputRoot+HOCR_SUFFIX);
	}

	private File createHocrSVGFileDescriptor() {
		ensureOutputDirectory();
		return new File(outputDir, outputRoot+HOCR_SVG_SUFFIX);
	}

	public void matchSpecies(HOCRReader hocrReader) {
		if (speciesPattern != null) {
			List<HtmlSpan> lines = hocrReader.getNonEmptyLines();
			for (HtmlSpan line : lines) {
				List<String> matchList = HOCRReader.matchPattern(line, speciesPattern);
				LOG.trace((matchList.size() == 0 ? "?? "+HOCRReader.getSpacedValue(line).toString() : matchList));
			}
		}
	}

	public void setSpeciesPattern(Pattern speciesPattern) {
		this.speciesPattern = speciesPattern;
	}

	public void setOutputRoot(String root) {
		this.outputRoot = root;
	}

	public void setOutputDir(File outdir) {
		this.outputDir = outdir;
	}

	public Double getJoiningRadius() {
		return joiningRadius;
	}

	public void setJoiningRadius(Double joiningRadius) {
		this.joiningRadius = joiningRadius;
	}

	public int getMaxPhraseLength() {
		return maxPhraseLength;
	}

	public void setMaxPhraseLength(int maxPhraseLength) {
		this.maxPhraseLength = maxPhraseLength;
	}

	public static void convertPngToHTML_SVG_NEXML_NWK(File infile, File outdir) 
			throws IOException, InterruptedException, FileNotFoundException {
		if (infile == null || outdir == null) {
			throw new RuntimeException("files must not be null");
		}
		if (infile.isDirectory()) {
			List<File> pngFiles = new ArrayList<File>(FileUtils.listFiles(infile, new String[]{"png"}, false));
			for (File pngFile : pngFiles) {
				String basename = FilenameUtils.getBaseName(pngFile.getAbsolutePath());
				File outputSubDir = new File(outdir, basename);
				outputSubDir.mkdirs();
				convertPng(pngFile, outputSubDir);
			}
		} else {
			convertPng(infile, outdir);
		}
	}

	private static void convertPng(File pngfile, File outdir)
			throws IOException, InterruptedException, FileNotFoundException {
		String name = pngfile.getName();
		String root = name.substring(0, name.length() - PNG.length());
		org.apache.commons.io.FileUtils.copyFile(pngfile, new File(outdir, root+PNG));
		PhyloTreeArgProcessor phyloTreeArgProcessor = new PhyloTreeArgProcessor();
		phyloTreeArgProcessor.setOutputRoot(root);
		phyloTreeArgProcessor.setOutputDir(outdir);
		if (phyloTreeArgProcessor.mergeOCRAndPixelTree(pngfile)) {
			NexmlNEXML nexml = phyloTreeArgProcessor.getNexml();
			outdir.mkdirs();
			XMLUtil.debug(nexml, new FileOutputStream(new File(outdir, root+".nexml.xml")), 1);
			FileUtils.write(new File(outdir, root+".nwk"), nexml.createNewick());
			XMLUtil.debug(nexml.createSVG(), new FileOutputStream(new File(outdir, root+".svg")), 1);
			HOCRReader hocrReader = phyloTreeArgProcessor.getOrCreateHOCRReader();
			SVGSVG.wrapAndWriteAsSVG(hocrReader.getOrCreateSVG(), new File(outdir, root+".words.svg"));
		}
	}
	

	
}
