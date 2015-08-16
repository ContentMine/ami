package org.xmlcml.ami2.plugins.phylotree.nexml;

import nu.xom.Attribute;

import org.apache.log4j.Logger;

public class NexmlOtu extends NexmlElement {

	private final static Logger LOG = Logger.getLogger(NexmlOtu.class);
	public final static String TAG = "otu";

	private static final String EDIT_RECORD = "editRecord";

	/** constructor.
	 * 
	 */
	public NexmlOtu() {
		super(TAG);
	}

	public NexmlOtu(String id) {
		this();
		this.addAttribute(new Attribute("id", id));
	}

	public void setEditRecord(String editRecord) {
		this.addAttribute(new Attribute(EDIT_RECORD, editRecord));
	}

	
}
