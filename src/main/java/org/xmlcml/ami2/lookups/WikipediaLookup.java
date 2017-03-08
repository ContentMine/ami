package org.xmlcml.ami2.lookups;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.cproject.lookup.AbstractLookup;
import org.xmlcml.cproject.util.CMineUtil;
import org.xmlcml.euclid.IntArray;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import net.minidev.json.JSONArray;

public class WikipediaLookup extends AbstractLookup {

	private static final String DOLLAR_PROPS = "$.props.";
	private static final String STRING = "string";
	private static final String ITEMS = "items";
	private static final String WIKIDATA_GETIDS = "http://wdq.wmflabs.org/api?q=string[";
	private static final String ESC_QUOTE = "%22";
	private static final String ESC_SPACE = "%20";
	private static final String FORMAT_XML = "&format=xml";
	private static final String WIKIDATA_GET_ENTITIES = "https://www.wikidata.org/w/api.php?action=wbgetentities&ids=";
	public static final String WIKIDATA_SPECIES = "225";
	/**
Magnus Manske
	
Mar 14 (9 days ago)
		
to Peter
Hi Peter,

here are some links related to Wikipedia and Wikidata search.

To find a Wikidata item by the Latin species name (here ���Mus musculus���):
http://wdq.wmflabs.org/api?q=string[225:%22Mus%20musculus%22]

This returns a JSON structure with an item list, containing one item:
{"status":{"error":"OK","items":1,"querytime":"226ms","parsed_query":"STRING[225:'Mus musculus']"},"items":[83310]}


The item number (83310) refers to the Wikidata item; prepend a ���Q��� for that:
https://www.wikidata.org/wiki/Q83310

To get the XML for that item, use this format:
https://www.wikidata.org/w/api.php?action=wbgetentities&ids=Q83310&format=xml

You can do a search for item properties like this:
https://www.wikidata.org/w/api.php?action=wbsearchentities&search=Mus%20musculus&language=en&limit=50&format=xml

Or do a fulltext search. This works on en.wikipedia.org and www.wikidata.org alike:
https://www.wikidata.org/w/api.php?action=query&list=search&srsearch=Mus%20musculus&format=xml


You can see more details about the extensive Wikidata/Wikipedia API here:
https://www.wikidata.org/w/api.php
and for the query API:
http://wdq.wmflabs.org/api_documentation.html


Hope that helps,
Magnus
Peter Murray-Rust <pm286@cam.ac.uk>
	
Mar 14 (9 days ago)
		
	 */
	
	/**
	 * {"status":
	 *   {"error":"OK",
		 *     "items":1,
	 *     "querytime":"222ms",
	 *     "parsed_query":"STRING[225:'Mus musculus']"},
	 *   "items":[83310]
	 * }
	 */
	
	private static final Logger LOG = Logger.getLogger(WikipediaLookup.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	public WikipediaLookup() {
	}

	@Override
	public String lookup(String speciesName) throws IOException {
		if (speciesName != null) {
			LOG.debug("WPSP: "+speciesName);
			IntArray wikidataIntArray = getWikidataIDsAsIntArray(speciesName);
			String result = wikidataIntArray.toString();
			// remove all brackets
			result = result == null ? null : result.replaceAll("[\\(\\)]", "");
			return (result == null || result.trim().equals("")) ? null : result; 
		} else {
			LOG.error("null species");
			return null;
		}
	}
	
	public IntArray getWikidataIDsAsIntArray(String speciesName) throws IOException {
		JsonElement jsonElement = this.getWikidataSpeciesJSONElement(speciesName);
		return getIdentifierArray(jsonElement, ITEMS);
	}
	
	public IntArray getWikidataIDsAsIntArray(List<String> speciesNames) throws IOException {
		JsonElement jsonElement = this.getWikidataSpeciesJSONElement(speciesNames);
		return getIdentifierArray(jsonElement, ITEMS);
	}

	public List<Integer> getWikidataIDsAsIntegerList(List<String> names, String property) throws IOException {
		List<Integer> idList = new ArrayList<Integer>();
		JsonElement jsonElement = this.getWikidataSpeciesJSONElement(names);
		if (jsonElement != null) {
			String jsonPath = DOLLAR_PROPS+property;
			JSONArray jsonArray = (JSONArray) CMineUtil.getObjectForJsonPath(jsonElement.toString(), jsonPath);
			Map<String, Integer> namesToIdMap = new HashMap<String, Integer>();
			for (int i = 0; i < jsonArray.size(); i++) {
				JSONArray jsonArray1 = (JSONArray) jsonArray.get(i);
				if (STRING.equals(jsonArray1.get(1).toString())) {
					Integer id = (Integer)jsonArray1.get(0);
					String value = (String) jsonArray1.get(2);
					namesToIdMap.put(value, id);
				}
			}
			for (String name : names) {
				idList.add(namesToIdMap.get(name));
			}
		}
		return idList;
	}

	/** creates URL to retrieve data for an id.
	 * 
	 * @param wikidataId
	 * @return
	 * @throws MalformedURLException
	 */
	public static URL createWikidataXMLURL(String wikidataId) throws MalformedURLException {
		String urlString = WIKIDATA_GET_ENTITIES;
		urlString += wikidataId;
		urlString += FORMAT_XML;
		return new URL(urlString);
	}
    
	private JsonElement getWikidataSpeciesJSONElement(String speciesName) throws IOException {
		URL url = createWikidataSpeciesLookupURL(speciesName);
		String json = this.getResponse(url);
	    JsonParser parser = new JsonParser();
	    return parser.parse(json);
	}
    
	private JsonElement getWikidataSpeciesJSONElement(List<String> names) throws IOException {
		URL url = createWikidataSpeciesLookupURL(names);
		String json = this.getResponse(url);
	    JsonParser parser = new JsonParser();
	    JsonElement element = null;
	    try {
	    	element = parser.parse(json);
	    } catch (JsonSyntaxException e) {
	    	// expected for empty returns (bug in Json)
	    }
	    return element;
	}

	/** create URL to lookup a species.
	 * 
	 * @param name
	 * @return
	 * @throws MalformedURLException
	 */
	private static URL createWikidataSpeciesLookupURL(String name) {
		URL url =  createWikidataLookupURL(WIKIDATA_SPECIES, name);
		return url;
	}
    
	/** create URL to lookup many species.
	 * 
	 * @param name
	 * @return
	 * @throws MalformedURLException
	 */
	private static URL createWikidataSpeciesLookupURL(List<String> names) {
		URL url =  createWikidataLookupURL(WIKIDATA_SPECIES, names);
		return url;
	}
    
	/** creates a search URL from a Wikipedia property and a name.
	 * 
	 * @param property (e.g. 225 for species)
	 * @param name
	 * @return
	 * @throws MalformedURLException
	 */
	public static URL createWikidataLookupURL(String property, String name) {
		name = name.replaceAll(" ", ESC_SPACE);
		String urlString = WIKIDATA_GETIDS+property+":"+ESC_QUOTE+name+ESC_QUOTE+"]";
		URL url = null;
		try {
			url = new URL(urlString);
		} catch (MalformedURLException e) {
			throw new RuntimeException("Bad url for wikidata: +url", e);
		}
		return url;
	}
	
	/** creates a search URL from a Wikipedia property and a name.
	 * 
	 * @param property (e.g. 225 for species)
	 * @param name
	 * @return
	 * @throws MalformedURLException
	 */
	public static URL createWikidataLookupURL(String property, List<String> names) {
		String urlString = WIKIDATA_GETIDS;
		int i = 0;
		for (String name : names) {
			name = name.replaceAll(" ", ESC_SPACE);
			if (i++ > 0) urlString += ",";
			urlString += property+":"+ESC_QUOTE+name+ESC_QUOTE;
		}
		urlString += "]&props="+property;
		LOG.trace("URL: "+urlString);
		URL url = null;
		try {
			url = new URL(urlString);
		} catch (MalformedURLException e) {
			throw new RuntimeException("Bad url for wikidata: +url", e);
		}
		return url;
	}
	
    /**
     * These attempted to retrieve multiple species to avoid badwidth but the id<->species map is lost.
     * 
     */
//	private IntArray getWikidataIDsAsIntArray(List<String> names) throws MalformedURLException, IOException {
//	JsonElement jsonElement = this.getWikidataJSONElement(names);
//	LOG.debug(jsonElement);
//	return getIdentifierArray(jsonElement);
//}


//	private JsonElement getWikidataJSONElement(List<String> names) throws MalformedURLException, IOException {
//	URL url = createWikidataSpeciesLookupURL(names);
//	LOG.debug(url);
//	String json = this.getString(url);
//    JsonParser parser = new JsonParser();
//    return parser.parse(json);
//}

//	public URL createWikidataMultipleXMLURL(List<String> idList) throws MalformedURLException {
//		String urlString = WIKIDATA_GET_SPECIES;
//		int i = 0;
//		for (String id : idList) {
//			if (i++ > 0) {
//				urlString += ",";
//			}
//			urlString += id;
//		}
//		urlString += FORMAT_XML;
//		return new URL(urlString);
//	}
    
//	private URL createWikidataSpeciesLookupURL(List<String> names) throws MalformedURLException {
//		return createWikidataLookupURL(WIKIDATA_SPECIES, names);
//	}
    
//	private URL createWikidataLookupURL(String property, List<String> names) throws MalformedURLException {
//		String urlString = WIKIDATA_GETIDS;
//		int i = 0;
//		for (String name : names) {
//			name = name.replaceAll(" ", ESC_SPACE);
//			if (i++ > 0) {
//				urlString += ",";
//			}
//			urlString += property+":"+ESC_QUOTE+name+ESC_QUOTE;
//		}
//		urlString += "]";
//		return new URL(urlString);
//	}
    
		
}
