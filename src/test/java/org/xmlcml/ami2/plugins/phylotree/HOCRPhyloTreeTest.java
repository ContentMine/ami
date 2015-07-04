package org.xmlcml.ami2.plugins.phylotree;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;
import org.xmlcml.ami2.Fixtures;
import org.xmlcml.ami2.plugins.phylotree.nexml.NexmlEdge;
import org.xmlcml.ami2.plugins.phylotree.nexml.NexmlElement;
import org.xmlcml.ami2.plugins.phylotree.nexml.NexmlNEXML;
import org.xmlcml.ami2.plugins.phylotree.nexml.NexmlNode;
import org.xmlcml.ami2.plugins.phylotree.nexml.NexmlOtu;
import org.xmlcml.ami2.plugins.phylotree.nexml.NexmlOtus;
import org.xmlcml.ami2.plugins.phylotree.nexml.NexmlTree;
import org.xmlcml.ami2.plugins.phylotree.nexml.NexmlTrees;
import org.xmlcml.euclid.Int2;
import org.xmlcml.euclid.Real2;
import org.xmlcml.euclid.Real2Range;
import org.xmlcml.euclid.Real2Range.BoxDirection;
import org.xmlcml.euclid.RealRange;
import org.xmlcml.graphics.svg.SVGSVG;
import org.xmlcml.graphics.svg.SVGText;
import org.xmlcml.graphics.svg.text.SVGWord;
import org.xmlcml.graphics.svg.text.SVGWordBlock;
import org.xmlcml.graphics.svg.text.SVGWordLine;
import org.xmlcml.graphics.svg.text.SVGWordPage;
import org.xmlcml.graphics.svg.text.SVGWordPara;
import org.xmlcml.norma.image.ocr.HOCRReader;

/** merges results from HOCR (Tesseract) with phylotree (SVG and Nexml)
 * 
 * @author pm286
 *
 */
public class HOCRPhyloTreeTest {

	
	private static final Logger LOG = Logger.getLogger(HOCRPhyloTreeTest.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	private final static File HOCR_364_HTML = new File(Fixtures.TEST_PHYLO_DIR, "ijs_0_000364_0/image/003.pbm.png.hocr.html");
	private final static File PHYLOTREE_364_NEXML = new File(Fixtures.TEST_PHYLO_DIR, "ijs_0_000364_0/image/003.pbm.png.phylotree.nexml");
	private double maxDeltaY = 5.0;
	private Real2Range joiningBox = new Real2Range(new RealRange(0.0, 20.0), new RealRange(-5.0, 5.0));
		
	@Test
	public void testReadHOCRSVG() throws Exception {
		HOCRReader hocrReader = new HOCRReader();
		hocrReader.readHOCR(new FileInputStream(HOCR_364_HTML));
		SVGSVG svgSvg = (SVGSVG) hocrReader.getOrCreateSVG();
		//svgSvg.debug("svg");
		SVGSVG.wrapAndWriteAsSVG(svgSvg, new File("target/hocr/merge0.svg"));
		SVGWordPage svgPage = svgSvg.getSingleSVGPage();
		Assert.assertNotNull(svgPage);
		List<SVGWordBlock> svgBlockList = svgPage.getSVGBlockList();
		Assert.assertEquals("blocks", 40, svgBlockList.size());
		SVGWordBlock wordBlock2 = svgBlockList.get(2);
		List<SVGWordPara> paraList = wordBlock2.getSVGParaList();
		Assert.assertEquals("paras", 1, paraList.size());
		SVGWordPara wordPara = paraList.get(0);
		List<SVGWordLine> lineList = wordPara.getSVGLineList();
		Assert.assertEquals("line", 1, lineList.size());
		SVGWordLine line = lineList.get(0);
		List<SVGWord> wordList = line.getOrCreateSVGWordList();
		Assert.assertEquals("word", 2, wordList.size());
		SVGWord word = wordList.get(0);
		SVGText text = word.getSVGText();
		Assert.assertEquals("text", "66", text.getValue());
		List<SVGWordLine> allLineList = svgPage.getSVGLineList();
		Assert.assertEquals("lines", 70, allLineList.size());
		int i = 0;
		for (SVGWordLine wordLine : allLineList) {
			wordLine.makePhrasesFromWords();
//			System.out.println(Int2.getInt2(wordLine.getBoundingBox().getMidPoint(BoxDirection.LEFT)));
//			System.out.println((i++)+" "+wordLine.toString());
		}
		SVGWordLine line2 = allLineList.get(2);
		LOG.trace(line2.getValue());
		line2.makePhrasesFromWords();
		
	}
	
	@Test 
	public void testReadNexml() throws Exception {
		NexmlNEXML nexmlNEXML = (NexmlNEXML) NexmlElement.readAndCreateNEXML(PHYLOTREE_364_NEXML);
		Assert.assertNotNull(nexmlNEXML);
		NexmlOtus nexmlOtus = nexmlNEXML.getSingleOtusElement();
		List<NexmlOtu> nexmlOtuList = nexmlOtus.getNexmlOtuList();
		Assert.assertEquals("otus",  31, nexmlOtuList.size());
		NexmlTrees nexmlTrees = nexmlNEXML.getTreesElement();
		List<NexmlTree> nexmlTreeList = nexmlTrees.getOrCreateTreeList();
		Assert.assertEquals("trees",  1, nexmlTreeList.size());
		NexmlTree nexmlTree = nexmlTreeList.get(0);
		List<NexmlNode> nodeList = nexmlTree.getNodeListAndMap();
		Assert.assertEquals("nodes",  60, nodeList.size());
		List<NexmlEdge> edgeList = nexmlTree.getEdgeListAndMaps();
		Assert.assertEquals("edges",  59, edgeList.size());
		NexmlNode rootNexmlNode = nexmlTree.getRootNode();
		Assert.assertNotNull("root" , rootNexmlNode);
		List<NexmlNode> tipNodeList = nexmlTree.getTipNodeList();
		Assert.assertEquals("nodes",  31, tipNodeList.size());
		for (NexmlNode tipNode : tipNodeList) {
//			System.out.println(Int2.getInt2(tipNode.getXY2()));
		}
		Map<Int2, NexmlNode> tipByCoordMap = nexmlTree.getTipByCoordMap();
		Assert.assertEquals("tip map",  31, tipByCoordMap.size());
	}
	
	@Test
	public void testMerge() throws Exception {
		HOCRReader hocrReader = new HOCRReader();
		hocrReader.readHOCR(new FileInputStream(HOCR_364_HTML));
		SVGSVG svgSvg = (SVGSVG) hocrReader.getOrCreateSVG();
		List<SVGWordLine> allLineList = svgSvg.getSingleSVGPage().getSVGLineList();
		for (SVGWordLine wordLine : allLineList) {
			wordLine.makePhrasesFromWords();
		}
		Assert.assertEquals(70, allLineList.size());
		
		NexmlNEXML nexmlNEXML = (NexmlNEXML) NexmlElement.readAndCreateNEXML(PHYLOTREE_364_NEXML);
		NexmlTree nexmlTree = nexmlNEXML.getSingleTree();
		Assert.assertNotNull(nexmlTree);
		
		List<NexmlNode> tipNodeList = nexmlTree.getTipNodeList();
		Assert.assertEquals(70, allLineList.size());
		for (NexmlNode tipNode : tipNodeList) {
			Real2 tipXY2 = tipNode.getXY2();
			//System.out.println(""+tipXY2);
			List<SVGWordLine> lineList = new ArrayList<SVGWordLine>();
			for (SVGWordLine wordLine : allLineList) {
				String tipValue = null;
				Real2 lineXY2 = wordLine.getChildRectBoundingBox().getMidPoint(BoxDirection.LEFT);
				Real2 diffXY2 = lineXY2.subtract(tipXY2); 
				if (joiningBox.includes(diffXY2)) {
					//System.out.println(" ... "+diffXY2+"; "+wordLine.getSinglePhraseValue() +"; "+wordLine.getChildRectBoundingBox().getXMin());
					lineList.add(wordLine);
				}
			}
			if (lineList.size() == 1) {
				tipNode.setOtuValue(lineList.get(0).getSinglePhraseValue());
			} else if (lineList.size() > 1) {
				LOG.error("competing words for tip");
			}
		}
//		nexmlNEXML.debug();
		String newick = nexmlNEXML.createNewick();
		LOG.trace("NEWICK "+newick);
	}
	
	@Test
	public void testMerge1() throws Exception {
		HOCRReader hocrReader = new HOCRReader();
		
		hocrReader.readHOCR(new FileInputStream(HOCR_364_HTML));
		SVGSVG svgSvg = (SVGSVG) hocrReader.getOrCreateSVG();
		List<SVGWordLine> allLineList = svgSvg.getSingleSVGPage().getSVGLineList();
		for (SVGWordLine wordLine : allLineList) {
			wordLine.makePhrasesFromWords();
		}
		
		NexmlNEXML nexmlNEXML = (NexmlNEXML) NexmlElement.readAndCreateNEXML(PHYLOTREE_364_NEXML);
		NexmlTree nexmlTree = nexmlNEXML.getSingleTree();
		
		List<NexmlNode> tipNodeList = nexmlTree.getTipNodeList();
		for (NexmlNode tipNode : tipNodeList) {
			Real2 tipXY2 = tipNode.getXY2();
			List<SVGWordLine> lineList = new ArrayList<SVGWordLine>();
			for (SVGWordLine wordLine : allLineList) {
				Real2 lineXY2 = wordLine.getChildRectBoundingBox().getMidPoint(BoxDirection.LEFT);
				Real2 diffXY2 = lineXY2.subtract(tipXY2); 
				if (joiningBox.includes(diffXY2)) {
					lineList.add(wordLine);
				}
			}
			if (lineList.size() == 1) {
				tipNode.setOtuValue(lineList.get(0).getSinglePhraseValue());
			} else if (lineList.size() > 1) {
				LOG.error("competing words for tip");
			}
		}
		String newick = nexmlNEXML.createNewick();
	}
	/**
	 * words:
words:
(222,74)
(478,75)
(137,102)
(416,112)
(18,143)
(498,148)
(535,185)
(6,209)
(425,221)
(544,258)
(152,292)
(434,295)
(386,330)
(182,331)
(495,332)
(148,359)
(411,369)
(71,400)
(360,403)
(470,439)
(48,448)
(103,475)
(412,479)
(580,515)
(424,549)
(404,589)
(221,623)
(447,623)
(500,662)
(555,698)
(502,732)
(251,735)
(77,763)
(499,773)
(468,809)
(124,845)
(462,845)
(488,878)
(393,918)
(497,920)
(30,935)
(200,956)
(447,956)
(51,991)
(454,993)
(470,1030)
(95,1038)
(471,1066)
(421,1103)
(173,1104)
(483,1137)
(209,1150)
(48,1159)
(495,1174)
(84,1175)
(459,1175)
tips
>>[466,73]
>>[406,109]
>>[486,146]
>>[524,182]
>>[411,219]
>>[533,256]
>>[423,293]
>>[375,330]
>>[399,366]
>>[349,403]
>>[459,440]
>>[399,477]
>>[568,513]
>>[412,550]
>>[393,586]
>>[436,624]
>>[488,660]
>>[544,697]
>>[491,733]
>>[489,771]
>>[456,807]
>>[452,843]
>>[475,881]
>>[382,917]
>>[434,954]
>>[442,991]
>>[459,1027]
>>[461,1064]
>>[410,1101]
>>[472,1137]
>>[448,1174]





	 */
}
