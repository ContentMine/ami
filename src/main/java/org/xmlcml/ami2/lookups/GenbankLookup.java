package org.xmlcml.ami2.lookups;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.cmine.lookup.AbstractLookup;

public class GenbankLookup extends AbstractLookup {

	
	private static final Logger LOG = Logger.getLogger(GenbankLookup.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	public GenbankLookup() {
	}

	/*
http://eutils.ncbi.nlm.nih.gov/entrez/eutils/efetch.fcgi?db=nuccore&id=34577062,24475906&rettype=fasta&retmode=text
http://www.ncbi.nlm.nih.gov/entrez/eutils/esearch.fcgi?db=gene&term="+genbank_id+"GENBANK_ID	 */
	
	public String lookup(String genbankId) throws IOException {
		URL url = new URL("http://eutils.ncbi.nlm.nih.gov/entrez/eutils/efetch.fcgi?db=nuccore&id="+genbankId+"&retmode=xml");
		return getResponse(url);
	}

	public String lookupGenbankIds(List<String> genbankId) throws IOException {
		StringBuilder sb = new StringBuilder();
		sb.append("http://eutils.ncbi.nlm.nih.gov/entrez/eutils/efetch.fcgi?db=nuccore&id=");
		for (int i = 0; i < genbankId.size(); i++) {
			if (i > 0) sb.append(",");
			sb.append(genbankId.get(i));
		}
		sb.append("&retmode=xml");
		URL url = new URL(sb.toString());
		return getResponse(url);
	}

/**
	http://www.ebi.ac.uk/ena/data/view/Taxon:Gorilla%20gorilla,Taxon:Erithacus&display=xml
*/
	
	public String lookupTaxonomy(String genus) throws IOException {
//		http://www.ebi.ac.uk/ena/data/view/Taxon:Gorilla&display=xml
//		URL url = new URL("http://www.ebi.ac.uk/ena/data/view/Taxon:"+genus+"&retmode=xml");
		URL url = new URL("http://www.ebi.ac.uk/ena/data/view/Taxon:"+genus+"");
		return getResponse(url);
	}

	public String lookupTaxonomy(List<String> queryList) throws IOException {
		return null;
	}

	public String lookupTaxonomy(String genus, String specific) throws IOException {
		return null;
	}

		
}
