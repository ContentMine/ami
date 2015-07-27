package org.xmlcml.ami2.plugins.phylotree.nexml;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import nu.xom.Attribute;
import nu.xom.Element;

import org.apache.log4j.Logger;
import org.xmlcml.euclid.Int2;
import org.xmlcml.graphics.svg.SVGElement;
import org.xmlcml.graphics.svg.SVGG;
import org.xmlcml.xml.XMLUtil;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

public class NexmlTree extends NexmlElement {

	private static final String TYPE = "type";
	private final static Logger LOG = Logger.getLogger(NexmlTree.class);
	public final static String TAG = "tree";
	
	private List<NexmlNode> nodeList;
	private List<NexmlEdge> edgeList;
	private Map<String, NexmlNode> nodeByIdMap;
	private Multimap<String, NexmlEdge> edgeBySourceIdMap;
	private Multimap<String, NexmlEdge> edgeByTargetIdMap;
	private NexmlNode rootNexmlNode;
	private List<NexmlNode> rootList;
	private Set<NexmlNode> unusedNodeSet;
	private Set<NexmlNode> tipSet;
	private HashSet<NexmlNode> branchNodeSet;
	private List<NexmlNode> tipNodeList;
	private Map<Int2, NexmlNode> tipByCoordMap;

	/** constructor.
	 * 
	 */
	public NexmlTree() {
		super(TAG);
	}

	public void setType(String type) {
		this.addAttribute(new Attribute(XSI+":"+TYPE, XSI_NS, type));
	}

	public List<NexmlNode> getNodeListAndMap() {
		if (nodeList == null) {
			nodeList = new ArrayList<NexmlNode>();
			nodeByIdMap = new HashMap<String, NexmlNode>();
			List<Element> nodeElements = XMLUtil.getQueryElements(this, "./*[local-name()='node']");
			unusedNodeSet = new HashSet<NexmlNode>();
			for (Element node : nodeElements) {
				NexmlNode nexmlNode = (NexmlNode) node;
				nodeList.add(nexmlNode);
				nodeByIdMap.put(nexmlNode.getId(), nexmlNode);
				unusedNodeSet.add(nexmlNode);
			}
		}
		return nodeList;
	}
	
	public List<NexmlEdge> getEdgeListAndMaps() {
		if (edgeList == null) {
			edgeList = new ArrayList<NexmlEdge>();
			List<Element> edgeElements = XMLUtil.getQueryElements(this, "./*[local-name()='edge']");
			edgeByTargetIdMap = ArrayListMultimap.create();
			edgeBySourceIdMap = ArrayListMultimap.create();
			for (Element edge : edgeElements) {
				NexmlEdge nexmlEdge = (NexmlEdge) edge;
				edgeList.add(nexmlEdge);
				edgeBySourceIdMap.put(nexmlEdge.getSourceId(), nexmlEdge);
				edgeByTargetIdMap.put(nexmlEdge.getTargetId(), nexmlEdge);
			}
		}
		return edgeList;
	}
	
	private void addChildEdges(NexmlNode node) {
		if (!tipSet.contains(node) && !branchNodeSet.contains(node)) {
			String nodeId = node.getId();
			LOG.trace("adding id "+nodeId);
			List<NexmlEdge> childEdges = getSourceEdges(nodeId);
			if (childEdges.size() == 0) {
				tipSet.add(node);
			} else {
				branchNodeSet.add(node);
			}
			for (NexmlEdge childEdge : childEdges) {
				String childId = childEdge.getTargetId();
				NexmlNode childNode = getNode(childId);
				childNode.setParentNexmlNode(node);
				node.addChildNexmlNode(childNode);
				this.addChildEdges(childNode);
			}
		}
	}

	NexmlNode getNode(String id) {
		return nodeByIdMap.get(id);
	}

	List<NexmlEdge> getSourceEdges(String sourceId) {
		List<NexmlEdge> edgeList = new ArrayList<NexmlEdge>(edgeBySourceIdMap.get(sourceId));
		LOG.trace("E> "+sourceId+"; "+edgeList);
		return new ArrayList<NexmlEdge>(edgeBySourceIdMap.get(sourceId));
	}

	List<NexmlEdge> getTargetEdges(String target) {
		return new ArrayList<NexmlEdge>(edgeByTargetIdMap.get(target));
	}

	/** checks relationship of nodes and edges and adds parent/child to nodes.
	 * 
	 */
	public void buildTree() {
		getNodeListAndMap();
		getEdgeListAndMaps();
		tipSet = new HashSet<NexmlNode>();
		branchNodeSet = new HashSet<NexmlNode>();
		while(unusedNodeSet.size() > 0) {
			NexmlNode nextNode = unusedNodeSet.iterator().next();
			unusedNodeSet.remove(nextNode);
			List<NexmlEdge> edges = getSourceEdges(nextNode.getId());
			if (edges.size() > 0) {
				addChildEdges(nextNode);
			}
		}
		LOG.trace(tipSet);
		LOG.trace(branchNodeSet);
		List<NexmlNode> rootNodes = getRootList();
		if (rootNodes.size() == 0) {
			LOG.error("NO Root nodes");
		} else if (rootNodes.size() == 1) {
			this.rootNexmlNode = rootNodes.get(0);
		} else {
			LOG.debug("Cannot process multiple roots ");
//			this.rootNexmlNode = rootNodes.get(0);
////			rootNodes.remove(rootNode);
//			for (int i = 1; i < rootNodes.size(); i++) {
//				NexmlTree newTree = new NexmlTree();
//				newTree.rootNexmlNode = rootNodes.get(i);
//				newTree.transferNodeAndDescendants(newTree.rootNexmlNode, nodeList);
//			}
		}
	}
	

//	private void transferNodeAndDescendants(NexmlNode rootNode,	List<NexmlNode> nodeList) {
//		this.nodeList = new ArrayList<NexmlNode>();
//		this.edgeList = new ArrayList<NexmlEdge>();
//		List<NexmlNode> descendantNodeList = rootNode.createDescendantNodes();
//		for (NexmlNode node : descendantNodeList) {
//			this.nodeList.add(node);
//		}
//		List<NexmlEdge> descendantEdgeList = rootNode.createDescendantEdges();
//		for (NexmlEdge edge : descendantEdgeList) {
//			this.edgeList.add(edge);
//		}
//	}

	public String getNewick() {
		getRootNode();
		return rootNexmlNode == null ? null : rootNexmlNode.getNewick();
	}

	public NexmlNode getRootNode() {
		if (rootNexmlNode == null) {
			LOG.error("No root Node ... looking ");
			getNodeListAndMap();
			for (NexmlNode nexmlNode : nodeList) {
				if (NexmlFactory.TRUE.equals(nexmlNode.getRootValue())) {
					rootNexmlNode = nexmlNode;
				}
			}
		}
		return rootNexmlNode;
	}

	public List<NexmlNode> getRootList() {
		if (rootList == null) {
			rootList = new ArrayList<NexmlNode>();
			for (NexmlNode node : nodeList) {
				if (node.getParentNexmlNode() == null) {
					rootList.add(node);
				}
			}
			LOG.debug("rootList "+rootList.size());
		}
		return rootList;
	}

	public List<NexmlNode> getOrCreateTipNodeList() {
		if (tipNodeList == null) {
			getNodeListAndMap();
			tipNodeList = new ArrayList<NexmlNode>();
			for (NexmlNode nexmlNode : nodeList) {
				if (nexmlNode.getOtuRef() != null) {
					tipNodeList.add(nexmlNode);
				}
			}
		}
		return tipNodeList;
	}
	
	public List<NexmlNode> getOrCreateNonTipNodeList() {
		getOrCreateTipNodeList();
		List<NexmlNode> nodeListCopy = new ArrayList<NexmlNode>(this.getNodeListAndMap());
		nodeListCopy.removeAll(tipNodeList);
		return nodeListCopy;
	}

	public Map<Int2, NexmlNode> getTipByCoordMap() {
		if (tipByCoordMap == null) {
			tipByCoordMap = new HashMap<Int2, NexmlNode>();
			getOrCreateTipNodeList();
			for (NexmlNode tipNode : tipNodeList) {
				Int2 xy2 = tipNode.getInt2();
				if (xy2 != null) {
					tipByCoordMap.put(xy2, tipNode);
				}
			}
		}
		return tipByCoordMap;
	}

	public SVGElement createSVG() {
		SVGG g = new SVGG();
		getNodeListAndMap();
		for (NexmlNode node : nodeList) {
			g.appendChild(node.createSVG());
		}
		getEdgeListAndMaps();
		for (NexmlEdge edge : edgeList) {
			g.appendChild(edge.createSVG());
		}
		return g;
	}
	
}
