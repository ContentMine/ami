package org.xmlcml.ami2.lookups;

import java.io.IOException;
import java.net.URL;
import java.text.Normalizer.Form;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.xml.serialize.OutputFormat;
import org.xmlcml.cmine.lookup.AbstractLookup;

public class RRIDLookup extends AbstractLookup {

	
	private static final Logger LOG = Logger.getLogger(RRIDLookup.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	public RRIDLookup() {
	}

	/*
	https://scicrunch.org/resources/Antibodies/search?q=
	*/

	public String lookup(String rrid) throws IOException {
		urlString = "https://scicrunch.org/resolver/"+rrid;
//		if (outputFormat == null) {
//			urlString = "https://scicrunch.org/resources/Any/search?q="+rrid;
//		} else if (outputFormat.equals(".xml")) {
//			urlString = "https://scicrunch.org/resolver/"+rrid;
//		}
		return getResponse();
	}
	
	/**
	 * e.g. AB_570435
	 * 
	 * @param rrid
	 * @return
	 * @throws IOException
	 */
		
	public String lookupAntibody(String rrid) throws IOException {
		urlString = "https://scicrunch.org/resources/Antibodies/search?q="+rrid;
		return getResponse(url);
	}

		
}
