package org.xmlcml.ami2.lookups;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.vafer.jdeb.shaded.compress.io.FileUtils;
import org.xmlcml.cmine.lookup.AbstractLookup;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

/** looks up species and genus against NCBI Taxdump.
 * 
 * @author pm286
 *
 */
public class TaxdumpLookup extends AbstractLookup {

	
	private static final Logger LOG = Logger.getLogger(TaxdumpLookup.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	private final static File TAXDUMP = new File("src/main/resources/org/xmlcml/ami2/plugins/phylotree/taxdump");
	private final static File GENUS = new File(TAXDUMP, "genus.txt");
	private final static File BINOMIAL = new File(TAXDUMP, "binomial.txt");
	private Set<String> genusSet;
	private Set<String> binomialSet;
	private Multimap<String, String> speciesByGenusSet;
	
	public TaxdumpLookup() {
		setup();
	}
	
	private void setup() {
		LOG.trace("start setup");
		genusSet = readSet(GENUS);
		binomialSet = readSet(BINOMIAL);
		createSpeciesForGenusSet(binomialSet);
		LOG.trace("end setup");
	}

	private void createSpeciesForGenusSet(Set<String> binomialSet) {
		speciesByGenusSet = HashMultimap.create();
		for (String binomial : binomialSet) {
			String genus = binomial.split("\\s+")[0];
			String species = binomial.split("\\s+")[1];
			speciesByGenusSet.put(genus, species);
		}
	}

	private Set<String> readSet(File file) {
		List<String> lines = null;
		try {
			lines = FileUtils.readLines(file);
		} catch (IOException e) {
			throw new RuntimeException("Cannote read set from "+file, e);
		}
		if (lines == null || lines.size() == 0) {
			throw new RuntimeException("Empty file: "+file);
		}
		return new HashSet<String>(lines);
	}

	public boolean isValidGenus(String genus) {
		return genusSet.contains(genus);
	}

	public boolean isValidBinomial(String genus, String species)  {
		return binomialSet.contains(genus+" "+species);
	}

	public List<String> lookupSpeciesList(String genus) {
		List<String> speciesList = new ArrayList<String>(speciesByGenusSet.get(genus));
		Collections.sort(speciesList);
		return speciesList;
	}

	@Override
	// no-op (for compatibility)
	public String lookup(String key) throws IOException {
		return null;
	}


		
}
