package org.xmlcml.ami2.plugins.phylotree.nexml;

import nu.xom.Element;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.euclid.Real2;
import org.xmlcml.euclid.Real2Range;
import org.xmlcml.euclid.RealRange;
import org.xmlcml.euclid.Transform2;
import org.xmlcml.euclid.Vector2;
import org.xmlcml.graphics.svg.SVGG;
import org.xmlcml.graphics.svg.SVGRect;
import org.xmlcml.graphics.svg.SVGText;

public class NWKLeaf {
	
	private static final Logger LOG = Logger.getLogger(NWKLeaf.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	private NWKName name;

	public NWKLeaf(NWKName name) {
		this.name = name;
	}

	public static NWKLeaf createLeaf(StringBuilder sb) {
		NWKLeaf leaf = null;
		NWKName name = NWKName.createName(sb);
		if (name != null) {
			leaf = new NWKLeaf(name);
		}
		return leaf;
	}

	/**
	private void setLength(NWKLength length) {
		this.length = length;
	}
	*/

	public NWKName getName() {
		return name;
	}

	public String toString() {
		return "[leaf: "+String.valueOf(name)+"]";
	}

	public void createNewick(StringBuilder sb) {
		if (name != null) {
			sb.append(name.getNameString());
		}
	}

	public SVGG createSVGOld() {
		SVGG g = new SVGG();
		if (name != null) {
			String nameString = name.getValue();
			SVGText text = new SVGText(new Real2(0.0, 0.0), nameString);
			text.setFontSize(12.);
			Real2Range bbox = text.getBoundingBox();
			RealRange rr = bbox.getYRange();
			rr.extendBothEndsBy(5.);
			bbox = new Real2Range(bbox.getXRange(), rr);
			SVGRect rect = SVGRect.createFromReal2Range(bbox);
			rect.setFill("none");
			rect.setStrokeWidth(1.0);
			rect.setStroke("cyan");
			g.appendChild(rect);
			g.appendChild(text);
			g.setTransform(new Transform2(new Vector2(0.0, rect.getBoundingBox().getYRange().getRange())));
		}
		return g;
	}

	public Element createXML() {
		Element node = new Element("t");
		if (name != null) {
			node.appendChild(name.getNameString());
		}
		return node;
	}

}
