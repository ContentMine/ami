package org.xmlcml.ami2.plugins.phylotree.nexml;

import nu.xom.Attribute;

import org.apache.log4j.Logger;
import org.xmlcml.ami2.plugins.phylotree.PhyloConstants;
import org.xmlcml.norma.editor.EditList;

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

	public void annotateOtuWithEditRecord(EditList editRecord) {
		if (editRecord.size() > 0) {
			String edit = editRecord.toString();
			addAttribute(new Attribute(PhyloConstants.CM_PHYLO_PREFIX+":edit", PhyloConstants.CM_PHYLO_NS, edit));
		}
	}
}
