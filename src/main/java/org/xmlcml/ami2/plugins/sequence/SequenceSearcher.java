package org.xmlcml.ami2.plugins.sequence;

import org.xmlcml.ami2.plugins.AMIArgProcessor;
import org.xmlcml.ami2.plugins.AMISearcher;
import org.xmlcml.ami2.plugins.NamedPattern;
import org.xmlcml.cproject.files.ResultElement;

public class SequenceSearcher extends AMISearcher {

	public SequenceSearcher(AMIArgProcessor argProcessor,NamedPattern namedPattern) {
		super(argProcessor, namedPattern);
	}
	
	/**
	 *  //PLUGIN
	 */
	@Override
	public ResultElement createResultElement() {
		return new SequenceResultElement();
	}

}
