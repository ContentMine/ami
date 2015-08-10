package org.xmlcml.ami2.plugins.phylotree.nexml;

import java.util.List;
import java.util.regex.Pattern;

import nu.xom.Element;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.graphics.svg.SVGElement;
import org.xmlcml.graphics.svg.SVGSVG;
import org.xmlcml.xml.XMLUtil;

public class NexmlNEXML extends NexmlElement {

	private final static Logger LOG = Logger.getLogger(NexmlNEXML.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	public final static String TAG = "nexml";
	private List<NexmlNode> rootList;
	private List<NexmlTree> treeList;

	/** constructor.
	 * 
	 */
	public NexmlNEXML() {
		super(TAG);
		this.addNamespaceDeclaration(NEX, NEXML_NS);
		this.addNamespaceDeclaration(XSI, XSI_NS);
	}

	public String createNewick() {
		StringBuilder sb = new StringBuilder();
		List<NexmlTree> treeList = getTreeList();
		for (NexmlTree tree : treeList) {
			sb.append(tree.getNewick());
		}
		sb.append(";");
		return sb.toString();
	}

	private void splitTrees() {
		List<NexmlTree>  treeList = getTreeList();
		for (NexmlTree nexmlTree : treeList) {
			List<NexmlNode> rootNodes = nexmlTree.getRootList();
			if (rootNodes.size() > 1) {
				LOG.error("splitTrees not yet written");
			}
		}
	}

	private List<NexmlTree> getTreeList() {
		NexmlTrees nexmlTrees = getTreesElement();
		treeList = nexmlTrees.getOrCreateTreeList();
		return treeList;
	}

	public NexmlTrees getTreesElement() {
		List<Element> elementList = XMLUtil.getQueryElements(this, "./*[local-name()='trees']");
		return elementList.size() != 1 ? null : (NexmlTrees) elementList.get(0);
	}

	void buildTrees() {
		getTreeList();
		for (NexmlTree tree : treeList) {
			tree.buildTree();
		}
	}

	public NexmlOtus getSingleOtusElement() {
		return (NexmlOtus) XMLUtil.getSingleElement(this, "*[local-name()='"+NexmlOtus.TAG+"']");
	}

	public NexmlTree getSingleTree() {
		NexmlTrees nexmlTrees = getTreesElement();
		return (nexmlTrees == null) ? null : (nexmlTrees.size() != 1 ? null : nexmlTrees.get(0)); 
	}
	
	public SVGElement createSVG() {
		NexmlTrees trees = getTreesElement();
		if (trees != null) {
			SVGSVG svg = new SVGSVG();
			svg.appendChild(trees.createSVG());
			return svg;
		}
		return null;
	}


}
