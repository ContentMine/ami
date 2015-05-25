package org.xmlcml.ami2.plugins.phylotree.nexml;

import java.util.ArrayList;
import java.util.List;

import nu.xom.Attribute;
import nu.xom.Element;

import org.apache.log4j.Logger;
import org.xmlcml.xml.XMLUtil;

public class NexmlTrees extends NexmlElement {

	private final static Logger LOG = Logger.getLogger(NexmlTrees.class);
	public final static String TAG = "trees";
	private List<NexmlTree> treeList;

	/** constructor.
	 * 
	 */
	public NexmlTrees() {
		super(TAG);
	}

	public void setOtus(String otus) {
		this.addAttribute(new Attribute("otus", otus));
	}

	public List<NexmlTree> getOrCreateTreeList() {
		if (treeList == null) {
			List<Element> elementList = XMLUtil.getQueryElements(this, "./*[local-name()='tree']");
			treeList = new ArrayList<NexmlTree>();
			for (Element element : elementList) {
				treeList.add((NexmlTree) element);
			}
		}
		return treeList;
	}

	public int size() {
		getOrCreateTreeList();
		return treeList.size();
	}

	public NexmlTree get(int i) {
		getOrCreateTreeList();
		return treeList.get(i);
	}
	
}
