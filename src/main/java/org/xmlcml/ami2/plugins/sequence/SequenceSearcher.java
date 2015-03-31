package org.xmlcml.ami2.plugins.sequence;

import org.xmlcml.ami2.plugins.AMIArgProcessor;
import org.xmlcml.ami2.plugins.DefaultSearcher;
import org.xmlcml.ami2.plugins.NamedPattern;
import org.xmlcml.files.ResultElement;

public class SequenceSearcher extends DefaultSearcher {

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
