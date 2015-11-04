package org.xmlcml.ami2.lookups;

import java.io.File;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import nu.xom.Element;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.xmlcml.ami2.AMIFixtures;
import org.xmlcml.ami2.plugins.species.SpeciesArgProcessor;
import org.xmlcml.euclid.IntArray;
import org.xmlcml.xml.XMLUtil;

@Ignore // unless testing Lookup
public class WikipediaLookupTest {

	
	private static final Logger LOG = Logger.getLogger(WikipediaLookupTest.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	@Test
	@Ignore // LOOKUP 
	public void getWikidataIdForSpecies() throws Exception {
		WikipediaLookup wikipediaLookup = new WikipediaLookup();
		IntArray intArray = wikipediaLookup.getWikidataIDsAsIntArray("Mus musculus");
		Assert.assertEquals("mouse", "(83310)", intArray.toString());
	}
	
	@Test
//	@Ignore // LOOKUP 
	public void getWikidataXMLForID() throws Exception {
		WikipediaLookup defaultLookup = new WikipediaLookup();
		URL url = defaultLookup.createWikidataXMLURL("Q83310");
		Element element = defaultLookup.getResponseXML(url);
		String property = "P225";
		/**
        <property id="P225">
          <claim _idx="0" type="statement" id="q83310$8F57CD44-BE38-4EA6-B729-36D82E855694" rank="normal">
            <mainsnak snaktype="value" property="P225" datatype="string">
              <datavalue value="Mus musculus" type="string"/>
            </mainsnak>
		 */
		String xpath = "//*[@property='"+property+"']/datavalue/@value";
		String value = XMLUtil.getSingleValue(element, xpath);
		Assert.assertEquals("value", "Mus musculus", value);
	}
	
	/** looks up a list of species.
	 * Q140 = Panthera leo
	 * Q83310 = Mus musculus
	 * Q737838 = Gorilla gorilla
	 * keeps queries and returned IDs in sync.
	 * 
	 */
	
	@Test
	public void getWikidataIdForMultipleSpecies() throws Exception {
		WikipediaLookup defaultLookup = new WikipediaLookup();
		List<String> speciesList = Arrays.asList(new String[] {"Mus musculus", "Gorilla gorilla", "Panthera leo"});
		List<Integer> idList = defaultLookup.getWikidataIDsAsIntegerList(speciesList, WikipediaLookup.WIKIDATA_SPECIES);
		Assert.assertEquals("mouse", "[83310, 737838, 140]", idList.toString());
	}
	
	@Test
	public void getWikidataIdForMultipleSpeciesWithMissing() throws Exception {
		WikipediaLookup defaultLookup = new WikipediaLookup();
		List<String> speciesList = Arrays.asList(new String[] {"Mus musculus", "Gorilla gorilla", "Biffo boffo", "Panthera leo"});
		List<Integer> idList = defaultLookup.getWikidataIDsAsIntegerList(speciesList, WikipediaLookup.WIKIDATA_SPECIES);
		Assert.assertEquals("mouse", "[83310, 737838, null, 140]", idList.toString());
	}
	
	@Test
	public void getWikidataIdForMultipleSpeciesWithAllMissing() throws Exception {
		WikipediaLookup defaultLookup = new WikipediaLookup();
		List<String> speciesList = Arrays.asList(new String[] {"Bingo bongo", "Biffo boffo"});
		List<Integer> idList = defaultLookup.getWikidataIDsAsIntegerList(speciesList, WikipediaLookup.WIKIDATA_SPECIES);
		Assert.assertEquals("mouse", "[]", idList.toString());
	}
	
//	@Test
//	public void getWikidataXMLForMutipleID() throws Exception {
//		WikipediaLookup defaultLookup = new WikipediaLookup();
//		URL url = defaultLookup.createWikidataMultipleXMLURL(Arrays.asList(new String[]{"Q140","Q83310"}));
//		LOG.debug(url);
//		Element element = defaultLookup.getWikidataXML(url);
//		XMLUtil.debug(element, "Mus");
//		Assert.assertEquals("Q83310", 
//				"<api success=\"1\"><entities><entity pageid=\"85709\" ns=\"0\" title=\"Q83310\" lastrevid=\"194466801\" modifi",
//				element.toXML().substring(0, 100));
//	}
	
	@Test
	@Ignore // takes too long
	public void testLookup() throws Exception {
		File target = new File("target/lookup/pone_0115884");
		FileUtils.copyDirectory(new File(AMIFixtures.TEST_PLOSONE_DIR, "journal.pone.0115884/"), target);
		String cmd = "--sp.species --context 35 --sp.type binomial binomialsp "
				+ "-q "+target+" -i scholarly.html "
				+ "--lookup wikipedia";
		LOG.debug(cmd);
		SpeciesArgProcessor argProcessor = new SpeciesArgProcessor();
		argProcessor.parseArgs(cmd);
		argProcessor.runAndOutput();
	}

	
}
