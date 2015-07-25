package org.xmlcml.ami2.plugins.phylotree.nexml;

import java.util.ArrayList;
import java.util.List;

import nu.xom.Attribute;
import nu.xom.ParentNode;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.euclid.Int2;
import org.xmlcml.euclid.Real2;
import org.xmlcml.graphics.svg.SVGCircle;
import org.xmlcml.graphics.svg.SVGG;
import org.xmlcml.graphics.svg.SVGText;

public class NexmlNode extends NexmlElement {

	private static final double FONTSIZE = 20.0;

	private final static Logger LOG = Logger.getLogger(NexmlNode.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	public final static String TAG = "node";
	
	private static final String LABEL = "label";
	private static final String ROOT = "root";
	private static final String OTU = "otu";
	private static final String X = "x";
	private static final String Y = "y";
	
	public static int DECIMAL_PLACES = 0;
	
//	private List<NexmlEdge> nexmlChildEdges;
	private List<NexmlNode> childNexmlNodes;
	List<NexmlEdge> nexmlEdges;
	private NexmlTree nexmlTree;
	private NexmlNode parentNexmlNode;
	private double radius = 1.0;

	public NexmlNode() {
		super(TAG);
		nexmlEdges = new ArrayList<NexmlEdge>();
		childNexmlNodes = new ArrayList<NexmlNode>();
	}

	/** constructor from reading nexml.
	 * 
	 */
	public NexmlNode(NexmlTree nexmlTree) {
		this();
		this.nexmlTree = nexmlTree;
	}

	public void setRoot(String bool) {
		this.addAttribute(new Attribute(ROOT, bool));
	}

	public void setOtuRef(String otuId) {
		this.addAttribute(new Attribute(OTU, otuId));
	}

	public String getOtuRef() {
		return this.getAttributeValue(OTU);
	}

	public void setXY2(Real2 xy) {
		if (xy != null) {
			this.addAttribute(new Attribute(X, String.valueOf(xy.getX())));
			this.addAttribute(new Attribute(Y, String.valueOf(xy.getY())));
		}
	}

	public Real2 getXY2() {
		Real2 r2 = null;
		String x = this.getAttributeValue(X);
		String y = this.getAttributeValue(Y);
		if (x != null && y != null) {
			r2 = Real2.createFromString("("+x+","+y+")");
			r2.format(DECIMAL_PLACES);
		}
		return r2;
	}

	public boolean isRoot() {
		return getAttributeValue(ROOT) != null;
	}

//	public void addChildEdge(NexmlEdge childEdge) {
//		ensureChildEdgesAndNodes();
//		this.nexmlChildEdges.add(childEdge);
//		NexmlNode childNode = nexmlTree.getNode(childEdge.getTargetId());
//		if (childNode != null) {
//			childNode.setParentNexmlNode(this);
//			this.addChildNexmlNode(childNode);
//		}
//	}

	void addChildNexmlNode(NexmlNode childNode) {
		ensureChildEdgesAndNodes();
		childNexmlNodes.add(childNode);
	}

	void setParentNexmlNode(NexmlNode nexmlNode) {
		this.parentNexmlNode = nexmlNode;
	}
	
	NexmlNode getParentNexmlNode() {
		return parentNexmlNode;
	}

	/** this may not be the correct approach.
	 * 
	 */
	private void ensureChildEdgesAndNodes() {
		if (this.childNexmlNodes == null) {
//			this.nexmlChildEdges = new ArrayList<NexmlEdge>();
			this.childNexmlNodes = new ArrayList<NexmlNode>();
		}
	}

	public String getNewick() {
		StringBuilder sb = new StringBuilder();
		getNexmlChildNodes();
		if (childNexmlNodes.size() > 0) {
			sb.append("(");
			for (int i = 0; i < childNexmlNodes.size(); i++) {
				NexmlNode childNode = childNexmlNodes.get(i);
				sb.append(childNode.getNewick());
				if (i < childNexmlNodes.size() - 1) {
					sb.append(",");
				}
			}
			sb.append(")");
		}
		sb.append(this.getNewickLabel());
		if (parentNexmlNode != null) {
			Double distance = this.getDistance(parentNexmlNode);
			if (distance != null) {
				sb.append(":"+distance);
			}
		}
		return sb.toString();
	}

	private String getNewickLabel() {
		String label = null;
		NexmlOtu otu = getOtu();
		if (otu != null) {
			label = otu.getValue();
			label = label.replaceAll(".*\\(", "");
			label = label.replaceAll("\\)", "");
			label = label.replaceAll(" ", "");
		}
		label = (label == null) ? this.getId() : label;
		return label;
	}

	private Double getDistance(NexmlNode parentNexmlNode) {
		Double d = null;
		Real2 xy2 = this.getXY2();
		if (parentNexmlNode != null && xy2 != null) {
			Real2 parentXy2 = parentNexmlNode.getXY2();
			d = parentXy2 == null ? null : xy2.getX() - parentXy2.getX();
		}
		return d;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(getId());
		ensureChildEdgesAndNodes();
		if (childNexmlNodes.size() > 0) {
			sb.append("[");
			for (int i = 0; i < childNexmlNodes.size(); i++) {
				sb.append(childNexmlNodes.get(i).getId()+" ");
			}
			sb.append("]");
		}
		return sb.toString();
	}

	private String getParentNexmlId() {
		return parentNexmlNode == null ? null : parentNexmlNode.getId(); 
	}

//	public List<NexmlNode> createDescendantNodes() {
//		List<NexmlNode> nodes = new ArrayList<NexmlNode>();
//		addNodes(this, nodes);
//		return nodes;
//	}
//
//	public List<NexmlEdge> createDescendantEdges() {
//		List<NexmlEdge> edges = new ArrayList<NexmlEdge>();
//		addEdges(this, edges);
//		return edges;
//	}
//
	private void addNodes(NexmlNode node, List<NexmlNode> nodes) {
		List<NexmlNode> childNodes = node.getNexmlChildNodes();
		for (NexmlNode childNode : childNodes) {
			nodes.add(childNode);
			addNodes(childNode, nodes);
		}
	}

//	private void addEdges(NexmlNode node, List<NexmlEdge> edges) {
//		List<NexmlEdge> childEdges = node.getNexmlChildEdges();
//		for (NexmlEdge childEdge : childEdges) {
//			edges.add(childEdge);
//		}
//		List<NexmlNode> childNodes = node.getNexmlChildNodes();
//		for (NexmlNode childNode : childNodes) {
//			addEdges(childNode, edges);
//		}
//	}

	private List<NexmlNode> getNexmlChildNodes() {
		ensureChildEdgesAndNodes();
		return childNexmlNodes;
	}
	
//	private List<NexmlEdge> getNexmlChildEdges() {
//		ensureChildEdgesAndNodes();
//		return nexmlChildEdges;
//	}

	public String getRootValue() {
		return this.getAttributeValue(ROOT);
	}

	public void addNexmlEdge(NexmlEdge nexmlEdge) {
		if (!nexmlEdges.contains(nexmlEdge)) {
			nexmlEdges.add(nexmlEdge);
		}
	}

	public void addChildNode(NexmlNode childNexmlNode) {
		this.childNexmlNodes.add(childNexmlNode);
	}

	public Int2 getInt2() {
		return Int2.getInt2(getXY2());
	}
	
	

	public void setOtuValue(String singlePhraseValue) {
		String otuId = this.getOtuRef();
		if (otuId != null) {
			NexmlOtu otu = getOtuFromOtuRef(otuId);
			if (otu != null) {
				otu.appendChild(singlePhraseValue);
			} else {
				LOG.error("Cannot find OTU" +otuId);
			}
		}
	}

	private NexmlOtu getOtuFromOtuRef(String otuRef) {
		NexmlOtu otu = null;
		if (otuRef != null) {
			NexmlNEXML nexmlNEXML = this.getNexmlNEXML();
			NexmlOtus otus = (nexmlNEXML == null) ? null : nexmlNEXML.getSingleOtusElement();
		    otu = (otus == null) ? null : otus.getOtuById(otuRef);
		}
		return otu;
	}

	private NexmlNEXML getNexmlNEXML() {
		ParentNode parent = this.getParent();
		parent = (parent == null) ? null : parent.getParent();
		parent = (parent == null) ? null : parent.getParent();
		return (NexmlNEXML) parent;
	}
	
	public NexmlOtu getOtu() {
		return getOtuFromOtuRef(this.getOtuRef());
	}
	
	public SVGG createSVG() {
		SVGG g = null;
		if (this.getXY2() != null) {
			g = new SVGG();
			SVGCircle circle = new SVGCircle(this.getXY2(), radius);
			g.appendChild(circle);
			String label = getLabelString();
			if (label != null) {
				double fontSize = (label.startsWith("NT")) ? FONTSIZE / 2.0 : FONTSIZE;
				SVGText text = new SVGText(this.getXY2(), label);
				text.setFontSize(fontSize);
				text.setFontFamily("helvetica");
				g.appendChild(text);
			}
		}
		return g;
	}

	/** a label for the node.
	 * 
	 * tries, otu, then label, then id
	 * 
	 * @return
	 */
	public String getLabelString() {
		String label = null;
		NexmlOtu otu = getOtu();
		if (otu != null) {
			label = otu.getValue();
			label = (label == null) ? otu.getId() : label;
		} else {
			label = this.getAttributeValue(LABEL);
			label = label == null ? this.getId() : label;
		}
		return label;
	}
}
