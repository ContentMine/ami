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

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.vafer.jdeb.shaded.compress.io.FilenameUtils;
import org.xmlcml.ami2.plugins.AMIArgProcessor;
import org.xmlcml.ami2.plugins.phylotree.nexml.NexmlElement;
import org.xmlcml.ami2.plugins.phylotree.nexml.NexmlFactory;
import org.xmlcml.ami2.plugins.phylotree.nexml.NexmlNEXML;
import org.xmlcml.ami2.plugins.phylotree.nexml.NexmlNode;
import org.xmlcml.ami2.plugins.phylotree.nexml.NexmlTree;
import org.xmlcml.cmine.args.ArgumentOption;
import org.xmlcml.cmine.files.CMDir;
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
	

	private static final String HOCR_SVG_SUFFIX = ".pbm.png.hocr.svg";
	public static final Logger LOG = Logger.getLogger(PhyloTreeArgProcessor.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	private static Real2Range DEFAULT_HOCR_WORD_JOINING_BOX = new Real2Range(new RealRange(0.0, 20.0), new RealRange(-5.0, 5.0));
	private static final String HOCR_SUFFIX = ".pbm.png.hocr";
	private static final String HOCR_HTML_SUFFIX = ".pbm.png.hocr.html";
	private static final int DEFAULT_RETRIES_FOR_TESSERACT_EXIT = 30;

	private SVGXTree svgxTree;
	private HOCRReader hocrReader;
	private ImageToHOCRConverter imageToHOCRConverter;
	private Pattern speciesPattern;
	private String outputRoot;
	private File outputDir;
	private DiagramTree diagramTree;
	private PhyloTreePixelAnalyzer phyloTreePixelAnalyzer;
	private NexmlNEXML nexml;
	private List<NexmlNode> unmatchedTipList;
	private List<SVGPhrase> unusedPhraseList;

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
		} catch (Exception e) {
			throw new RuntimeException("Cannot read/interpret tree: "+inputFile, e);
		}
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

	public void addTipAndBranchLabelsToTree(List<SVGPhrase> unusedPhraseList, NexmlTree nexmlTree) {
		this.unusedPhraseList = unusedPhraseList;
		addTipAndBranchLabelsToTree(nexmlTree);
	}

	/** matches tips to labels.
	 * 
	 * creates lists of failed tips (failedTipList) and failed labels (failedLabels).
	 * 
	 * @param wordLineList
	 * @param nexmlTree
	 */
	public void addTipAndBranchLabelsToTree(NexmlTree nexmlTree) {
		List<NexmlNode> tipNodeList = nexmlTree.getTipNodeList();
		unmatchedTipList = new ArrayList<NexmlNode>();
		matchTipNodes(tipNodeList);
		if (unusedPhraseList.size() > 0) {
			LOG.trace("unmatched phrases");
//			for (SVGPhrase phrase : unusedPhraseList) {
//				System.out.print("   "+"<"+phrase.toString()+">");
//			}
//			System.out.println();
		}
		
	}

	private void matchTipNodes(List<NexmlNode> tipNodeList) {
		List<SVGPhrase> matchedPhraseList = new ArrayList<SVGPhrase>();
		for (NexmlNode tipNode : tipNodeList) {
			Real2 tipXY2 = tipNode.getXY2();
			List<SVGPhrase> phrases = this.mergeTipsCreateMatchedPhrases(unusedPhraseList, tipXY2);
			if (phrases.size() == 1) {
				tipNode.setOtuValue(phrases.get(0).toString());
				LOG.trace("match tip:" +tipNode.getLabelString()+"("+tipNode.getXY2()+")");
				matchedPhraseList.add(phrases.get(0));
			} else if (phrases.size() > 1) {
				LOG.error("competing words for tip");
			} else if (phrases.size() == 0) {
				LOG.trace("failed to find phrases to match tip:" +tipNode.getLabelString()+"("+tipNode.getXY2()+")");
				unmatchedTipList.add(tipNode);
			}
		}
		unusedPhraseList.removeAll(matchedPhraseList);
		if (unmatchedTipList.size() > 0) {
			LOG.trace("unmatched tips: \n"+unmatchedTipList);
		}
	}
	
	/** finds lines within joining box of XY2.
	 * 
	 * Ideally wordLineList should be size==1
	 * 
	 * @param phraseList
	 * @param tipXY2
	 * @return
	 */
	private List<SVGPhrase> mergeTipsCreateMatchedPhrases(List<SVGPhrase> phraseList, Real2 tipXY2) {
		getOrCreateHOCRReader();
		List<SVGPhrase> matchedPhraseList = new ArrayList<SVGPhrase>();
		if (phraseList == null) {
			LOG.warn("null phraseList");
		} else {
			LOG.trace(tipXY2+"; "+phraseList.size());
			for (SVGPhrase phrase : phraseList) {
				Real2Range phraseBox = phrase == null ? null : phrase.getBoundingBox();
				if (phraseBox != null) {
					Real2 phraseXY2 = phraseBox.getMidPoint(BoxDirection.LEFT);
					Real2 diffXY2 = phraseXY2.subtract(tipXY2); 
					LOG.trace(phraseXY2+"; "+diffXY2);
//					if (Math.abs(diffXY2.getY()) < 5) {
//						LOG.debug("NEAR Y"+phraseXY2+"; "+diffXY2);
//					}
					if (hocrReader.getWordJoiningBox().includes(diffXY2)) {
						matchedPhraseList.add(phrase);
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

	public boolean readAndCombineTreeAndTips(File imageFile) throws IOException, InterruptedException {
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
		NexmlTree nexmlTree = nexml.getSingleTree();
		unusedPhraseList = new ArrayList<SVGPhrase>(hocrReader.getOrCreatePhraseList());
		this.addTipAndBranchLabelsToTree(unusedPhraseList, nexmlTree);
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
	

	
}
