package org.xmlcml.ami2.lookups;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.cmine.lookup.AbstractDictionary;
import org.xmlcml.xml.XMLUtil;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.internal.Streams;
import com.google.gson.stream.JsonWriter;

import nu.xom.Attribute;
import nu.xom.Element;

/** Human gene nomenclature from
 * ftp://ftp.ebi.ac.uk/pub/databases/genenames/new/json/hgnc_complete_set.json
 * 
 * @author pm286
 *
 */
public class HGNCDictionary extends AbstractDictionary {

	private static final Logger LOG = Logger.getLogger(HGNCDictionary.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	private final static File GENE_DIR = new File("src/main/resources/org/xmlcml/ami2/plugins/gene");
	private final static File HGNC_DIR = new File(GENE_DIR, "hgnc");
	private final static File HGNC_JSON_FILE = new File(HGNC_DIR, "hgnc_complete_set.json");
	private final static File HGNC_JSON_FILE1 = new File(HGNC_DIR, "hgnc_complete_set1.json");
	private final static File HGNC_XML_FILE = new File(HGNC_DIR, "hgnc_complete_set.xml");
	private JsonObject hgncJson;
	private int numGenes;
	private Set<String> geneIds;
	private InputStream inputStream;

	public HGNCDictionary() {
		init();
	}
	
	private void init() {
		readHGNCJson();
		readHGNCXML();
	}

	private void readHGNCXML() {
		if (!HGNC_XML_FILE.exists()) {
			readHGNCJson();
			Element dictionary = new Element("dictionary");
			dictionary.addAttribute(new Attribute("title", "hgnc"));
			for (String geneId : geneIds) {
				Element entry = new Element("entry");
				entry.appendChild(geneId);
				dictionary.appendChild(entry);
			}
			try {
				XMLUtil.debug(dictionary, HGNC_XML_FILE, 2);
			} catch (IOException e) {
				throw new RuntimeException("Cannot write geneXML file: "+HGNC_XML_FILE, e);
			}
		} else {
			Element dictionary = XMLUtil.parseQuietlyToDocument(HGNC_XML_FILE).getRootElement();
		}
	}
	
	private void readHGNCJson() {
		try {
			readFile("hgnc", new FileInputStream(HGNC_JSON_FILE));
			String resultsJsonString = IOUtils.toString(inputStream, "UTF-8");
		    JsonParser parser = new JsonParser();
		    hgncJson = (JsonObject) parser.parse(resultsJsonString);
		    try {
		        StringWriter stringWriter = new StringWriter();
		        JsonWriter jsonWriter = new JsonWriter(stringWriter);
		        jsonWriter.setLenient(true);
		        jsonWriter.setIndent("  ");
		        Streams.write(hgncJson, jsonWriter);
		        FileUtils.write(HGNC_JSON_FILE1, stringWriter.toString());
		      } catch (IOException e) {
		        throw new AssertionError(e);
		      }
		} catch (Exception e) {
			throw new RuntimeException("Cannot read HGNC file: "+HGNC_JSON_FILE, e);
		}
		
		JsonObject response = (JsonObject) hgncJson.get("response");
		numGenes = response.get("numFound").getAsInt();
		JsonArray docs = response.get("docs").getAsJsonArray();
//		int start =  response.get("start").getAsInt(); // don't know what this is (it's 0)
		createGeneIds(docs);
	}

	private void createGeneIds(JsonArray docs) {
		geneIds = new HashSet<String>();
		/**
{
    "gene_family":["Immunoglobulin-like domain containing"],
	"date_approved_reserved":"1989-06-30",
	"vega_id":"OTTHUMG00000183507",
	"locus_group":"protein-codingbgene",
	"status":"Approved",
	"_version_":1522770583154065408,
	"uuid":"550c4cad-7f6a-4f85-b872-3889c9afe302",
	"merops":"I43.950",
	"refseq_accession":["NM_130786"],
	"locus_type":"gene with protein product",
	"gene_family_id":[594],
	"cosmic":"A1BG",
	"hgnc_id":"HGNC:5",
	"rgd_id":["RGD:69417"],
	"ensembl_gene_id":"ENSG00000121410",
	"entrez_id":"1",
	"omim_id":[138670],
	"symbol":"A1BG",
	"location":"19q13.43",
	"name":"alpha-1-B glycoprotein",
	"date_modified":"2015-07-13",
	"mgd_id":["MGI:2152878"],
	"ucsc_id":"uc002qsd.4",
	"uniprot_ids":["P04217"],
	"ccds_id":["CCDS12976"],
	"pubmed_id":[2591067],
	"location_sortable":"19q13.43"
	},
		 */
		for (int i = 0; i < numGenes; i++) {
			JsonObject doc = (JsonObject) docs.get(i);
			String geneId = doc.get("symbol").getAsString();
			geneIds.add(geneId);
		}
	}

	private void debug(JsonObject jsonObject) {
		Set<Map.Entry<String, JsonElement>> set = jsonObject.entrySet();
		LOG.debug(set.size());
		Iterator<Map.Entry<String, JsonElement>> iterator = set.iterator();
		while (iterator.hasNext()) {
			Map.Entry<String , JsonElement> element = iterator.next();
			LOG.debug("IT "+element.getKey());
		}
	}

	public boolean contains(String geneSymbol) {
		return geneIds.contains(geneSymbol);
	}

	@Override
	protected void readFile(String name, InputStream is) throws IOException {
		inputStream = is;
	}

	@Override
	public List<List<String>> getTrailingWords(String key) {
		throw new RuntimeException("cannot use for HGNC");
	}


}
