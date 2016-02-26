package org.xmlcml.ami2.plugins.places;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.Test;
import org.xmlcml.ami2.dictionary.places.WikidataPlacesDictionary;

public class TestWikiplaces {

	private static final Logger LOG = Logger.getLogger(TestWikiplaces.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	@Test
	public void readWikiplacesRaw() {
		new WikidataPlacesDictionary();
	}
}
