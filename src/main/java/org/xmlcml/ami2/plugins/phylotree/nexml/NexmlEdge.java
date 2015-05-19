package org.xmlcml.ami2.plugins.phylotree.nexml;

import nu.xom.Attribute;

import org.apache.log4j.Logger;

public class NexmlEdge extends NexmlElement {

	private final static Logger LOG = Logger.getLogger(NexmlEdge.class);
	
	public final static String TAG = "edge";
	
	private static final String TARGET = "target";
	private static final String SOURCE = "source";

	/** constructor.
	 * 
	 */
	public NexmlEdge() {
		super(TAG);
	}

	public void setTarget(String target) {
		this.addAttribute(new Attribute(TARGET, target));
	}

	public void setSource(String source) {
		this.addAttribute(new Attribute(SOURCE, source));
	}

	public String getSourceId() {
		return getAttributeValue(SOURCE);
	}

	public String getTargetId() {
		return getAttributeValue(TARGET);
	}

	@Override
	public String toString() {
		return getSourceId()+"->"+getTargetId();
	}
	
}
