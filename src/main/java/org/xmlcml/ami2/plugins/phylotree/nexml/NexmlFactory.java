package org.xmlcml.ami2.plugins.phylotree.nexml;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.diagrams.DiagramTree;
import org.xmlcml.euclid.Int2;
import org.xmlcml.image.pixel.PixelEdge;
import org.xmlcml.image.pixel.PixelEdgeList;
import org.xmlcml.image.pixel.PixelNode;
import org.xmlcml.image.pixel.PixelNodeList;

/** creates NexmlTrees.
 * 
 * @author pm286
 *
 */
public class NexmlFactory {

	
	private static final Logger LOG = Logger.getLogger(NexmlFactory.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	static final String TRUE = "true";
	private static final String TREE_ID = "T";
	private static final String OTU = "otu";

	private NexmlNEXML nexmlNEXML;
	private NexmlOtus nexmlOtus;
	private NexmlTrees nexmlTrees;
	private Map<PixelNode, NexmlNode> pixelNodeToNexmlNodeMap;
	private Map<String, NexmlNode> idToNexmlNodeMap;
	private PixelNode rootPixelNode;
	private NexmlNode rootNexmlNode;
	private List<NexmlEdge> nexmlEdgeList;

	public NexmlFactory() {
		pixelNodeToNexmlNodeMap = new HashMap<PixelNode, NexmlNode>();
		idToNexmlNodeMap = new HashMap<String, NexmlNode>();
		nexmlEdgeList = new ArrayList<NexmlEdge>();
	}
	
	/**
<nexml xmlns="http://www.nexml.org/2009" xmlns:nex="http://www.nexml.org/2009"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:svgx="http://www.xml-cml.org/schema/svgx"
	id="tree.null">
	<otus id="tax1" label="RootTaxaBlock">
		<otu id="t1" />
		...
		<otu id="t5" />
	</otus>
	<trees label="TreesBlockFromXML" id="Trees" otus="tax1">
		<tree id="tree1" label="tree1" xsi:type="FloatTree">
			<node id="N1" otu="t1" label="Luscinia" x="397.018" y="102.384"/>
			...
			<node id="N5" otu="t5" label="Sturnus" x="394.055" y="133.774"/>
			<node id="N6" label="N6" x="318.938" y="129.848"/>
			<node id="N7" label="N7" root="true" x="290.932" y="121.022"/>
			<node id="N8" label="N8" x="303.421" y="112.197"/>
			<node id="N9" label="N9" x="342.88" y="106.311"/>
			<edge id="polyline.14" label="polyline.14" source="N9" target="N1"
				svgx:length="54.138" />
			<edge id="polyline.16" label="polyline.16" source="N9" target="N2"
				svgx:length="66.062" />
				...
			<edge id="polyline.15" label="polyline.15" source="N8" target="N9"
				svgx:length="39.459" />
		</tree>
	</trees>
</nexml>
	 * @return
	 */
	public NexmlNEXML getOrCreateNexmlNEXML() {
		if (nexmlNEXML == null) {
			nexmlNEXML = new NexmlNEXML();
			nexmlOtus = new NexmlOtus();
			nexmlOtus.setLabel("RootTaxaBlock");
			nexmlNEXML.appendChild(nexmlOtus);
			nexmlTrees = new NexmlTrees();
			nexmlNEXML.appendChild(nexmlTrees);
		}
		return nexmlNEXML;
	}
	
	public NexmlTree createAndAddNexmlTree(DiagramTree diagramTree) {
		getOrCreateNexmlNEXML();
		NexmlTree nexmlTree = new NexmlTree();
		nexmlTrees.appendChild(nexmlTree);
		String treeId = TREE_ID+nexmlTrees.getOrCreateTreeList().size();
		nexmlTree.setId(treeId);
		PixelNodeList pixelNodeList = diagramTree.getGraph().getNodeList();
		if (rootPixelNode == null) {
			rootPixelNode = diagramTree.getGraph().getRootPixelNode();
		}
		addNodes(nexmlTree, pixelNodeList, rootPixelNode);
		PixelEdgeList pixelEdgeList = diagramTree.getGraph().getEdgeList();
		addEdges(nexmlTree, pixelEdgeList);
		addEdgesToNodes();
		addChildrenAndDirectionality(rootNexmlNode);
		return nexmlTree;
	}

	private void addNodes(NexmlTree nexmlTree, PixelNodeList pixelNodeList,
			PixelNode rootNode) {
		for (PixelNode pixelNode : pixelNodeList) {
			NexmlNode nexmlNode = null;
			PixelEdgeList edgeList = pixelNode.getEdges();
			if (edgeList.size() == 1) {
				nexmlNode = addTerminalNodeAsOtu(nexmlTree, pixelNode);
			} else if (edgeList.size() == 2) {
				LOG.trace("node2 remove me??: "+pixelNode.toString()+"; "+pixelNode.getEdges().size());
			} else if (edgeList.size() == 3) {
				nexmlNode = createAndAddNexmlNode(nexmlTree, pixelNode);
				LOG.trace("node3: "+pixelNode.toString()+"; "+pixelNode.getEdges().size());
			} else if (edgeList.size() >= 4) {
				nexmlNode = createAndAddNexmlNode(nexmlTree, pixelNode);
				LOG.trace("node >= 4 connections: "+pixelNode.toString()+"; "+pixelNode.getEdges().size());
			} else {
				// ???
			}
			if (nexmlNode != null) {
				pixelNodeToNexmlNodeMap.put(pixelNode, nexmlNode);
				String id = nexmlNode.getId();
				if (id != null) {
					idToNexmlNodeMap.put(id, nexmlNode);
				} else {
					LOG.debug("null id for "+nexmlNode);
				}
			}
			// have to compare coords as Ids, etc have changed
			// have reduced the offset to zero.
			if (nexmlNode != null) {
				Int2 xy0 = rootPixelNode.getInt2();
				Int2 xy1 = pixelNode.getInt2();
				LOG.trace("coords "+xy0+"; "+xy1);
				if (Int2.isEqual(xy0, xy1)) {
					nexmlNode.setRoot(TRUE);
					LOG.trace("ROOT TRUE "+nexmlNode);
					this.rootNexmlNode = nexmlNode;
				}
			}
		}
		
//		pixelNodeList.sort();
	}

	private void addEdges(NexmlTree nexmlTree, PixelEdgeList pixelEdgeList) {
		for (PixelEdge pixelEdge : pixelEdgeList) {
			NexmlEdge nexmlEdge = createAndAddNexmlEdge(nexmlTree, pixelEdge);
			nexmlEdgeList.add(nexmlEdge);
		}
	}
	
	private void addEdgesToNodes() {
		for (NexmlEdge nexmlEdge : nexmlEdgeList) {
			NexmlNode node0 = nexmlEdge.getNexmlNode(0);
			node0.addNexmlEdge(nexmlEdge);
			NexmlNode node1 = nexmlEdge.getNexmlNode(1);
			node1.addNexmlEdge(nexmlEdge);
		}
	}

	private void addChildrenAndDirectionality(NexmlNode parentNexmlNode) {
		if (parentNexmlNode == null) {
			LOG.debug("cannot use NULL NexmlNode");
			return;
		}
		String parentId = parentNexmlNode.getId();
		for (NexmlEdge nexmlEdge : parentNexmlNode.nexmlEdges) {
			String edgeSourceId = nexmlEdge.getSourceId();
			String edgeTargetId = nexmlEdge.getTargetId();
			if (edgeSourceId != null) {
				if (parentId != null && parentId.equals(edgeTargetId)) {
					parentNexmlNode.setParentNexmlNode(idToNexmlNodeMap.get(edgeSourceId));
				}
				continue;
			}
			NexmlNode childNexmlNode = nexmlEdge.getOtherNode(parentNexmlNode);
			if (childNexmlNode == null) {
				LOG.debug("null node in edge? "+parentNexmlNode);
			} else {
				nexmlEdge.setSource(parentId);
				nexmlEdge.setTarget(childNexmlNode.getId());
				addChildrenAndDirectionality(childNexmlNode);
			}
			parentNexmlNode.addChildNode(childNexmlNode);
		}
	}

	private NexmlEdge createAndAddNexmlEdge(NexmlTree nexmlTree, PixelEdge pixelEdge) {
		NexmlNode nexmlNode0 = pixelNodeToNexmlNodeMap.get(pixelEdge.getNodes().get(0));
		NexmlNode nexmlNode1 = pixelNodeToNexmlNodeMap.get(pixelEdge.getNodes().get(1));
		NexmlEdge nexmlEdge = new NexmlEdge(nexmlTree, nexmlNode0, nexmlNode1);
		nexmlTree.appendChild(nexmlEdge);
		return nexmlEdge;
	}

	private NexmlNode addTerminalNodeAsOtu(NexmlTree nexmlTree, PixelNode pixelNode) {
		NexmlNode nexmlNode = createAndAddNexmlNode(nexmlTree, pixelNode);
		NexmlOtu otu = new NexmlOtu();
		nexmlOtus.appendChild(otu);
		String otuId = OTU+nexmlOtus.getChildCount();
		otu.setId(otuId);
		nexmlNode.setOtuRef(otuId);
		return nexmlNode;
	}

	private NexmlNode createAndAddNexmlNode(NexmlTree nexmlTree, PixelNode pixelNode) {
		NexmlNode nexmlNode = new NexmlNode(nexmlTree);
		nexmlTree.appendChild(nexmlNode);
		String nodeId = "N"+nexmlTree.getId()+"."+nexmlTree.getChildCount();
		nexmlNode.setId(nodeId);
		nexmlNode.setLabel(nodeId); // could be overwritten later
		nexmlNode.setXY2(pixelNode.getReal2());
		return nexmlNode;
	}

	public void setRootPixelNode(PixelNode rootPixelNode) {
		this.rootPixelNode = rootPixelNode;
		LOG.debug("set rootPixelNode: "+rootPixelNode);
	}
}
