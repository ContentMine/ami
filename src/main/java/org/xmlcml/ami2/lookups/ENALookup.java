package org.xmlcml.ami2.lookups;

import java.io.IOException;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

public class ENALookup extends AbstractLookup {

	
	private static final Logger LOG = Logger.getLogger(ENALookup.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	public ENALookup() {
	}

	/**
http://eutils.ncbi.nlm.nih.gov/entrez/eutils/efetch.fcgi?db=taxonomy&term="Gorilla+gorilla"&retmode=text
	http://www.ebi.ac.uk/ena/data/view/Taxon:Gorilla%20gorilla,Taxon:Erithacus&display=xml
*/
	
	public String lookupTaxonomy(String genbankId) throws IOException {
		return null;
	}

	@Override
	public String lookup(String key) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

		
}
