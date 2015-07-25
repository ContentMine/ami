package org.xmlcml.ami2.plugins;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.Element;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.ami2.plugins.regex.CompoundRegex;
import org.xmlcml.ami2.plugins.regex.CompoundRegexList;
import org.xmlcml.ami2.plugins.regex.RegexComponent;
import org.xmlcml.cmine.args.ArgIterator;
import org.xmlcml.cmine.args.ArgumentOption;
import org.xmlcml.cmine.args.DefaultArgProcessor;
import org.xmlcml.cmine.args.ValueElement;
import org.xmlcml.cmine.args.VersionManager;
import org.xmlcml.cmine.files.CMDir;
import org.xmlcml.cmine.files.ContentProcessor;
import org.xmlcml.cmine.files.DefaultSearcher;
import org.xmlcml.cmine.files.EuclidSource;
import org.xmlcml.cmine.files.ResultsElement;
import org.xmlcml.cmine.lookup.AbstractLookup;
import org.xmlcml.norma.NormaArgProcessor;
import org.xmlcml.xml.XMLUtil;

/** 
 * Processes commandline arguments.
 * for Norma
 * 
 * @author pm286
 */
public class AMIArgProcessor extends DefaultArgProcessor {
	
	public static final Logger LOG = Logger.getLogger(AMIArgProcessor.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	private static final String AMI = "ami";
	private static final String ARG_PROCESSOR = "ArgProcessor";
	public static final String RESULTS = "results";
	protected static String RESOURCE_NAME_TOP = "/org/xmlcml/ami2";
	protected static String PLUGIN_RESOURCE = RESOURCE_NAME_TOP+"/plugins";
	private static String ARGS_RESOURCE = PLUGIN_RESOURCE+"/"+"args.xml";
	public static final VersionManager AMI_PLUGIN_VERSION_MANAGER = new VersionManager();

	protected static final String NAME = "name";
	private Integer[] contextCount = new Integer[] {98, 98};
	private List<String> params;
	
	private XPathProcessor xPathProcessor;
	private String plugin;
    Map<String,AbstractLookup> lookupInstanceByName;
	protected CompoundRegexList compoundRegexList;
	protected List<Element> regexElementList;
	protected List<? extends Element> sectionElements;
	protected List<String> lookupNames;
	
	public AMIArgProcessor() {
		super();
		readArgsResourcesIntoOptions();
	}

	protected static VersionManager getVersionManager() {
		return AMI_PLUGIN_VERSION_MANAGER;
	}

	private void readArgsResourcesIntoOptions() {
		// the default ami2 options
		super.readArgumentOptions(ARGS_RESOURCE);
		// the plugin-specific options
		super.readArgumentOptions(createPluginArgsResourceName());
	}

	public AMIArgProcessor(String[] args) {
		this();
		parseArgs(args);
	}
	
	public AMIArgProcessor(String cmd) {
		this(cmd.split("\\s+"));
	}

	protected String createPluginArgsResourceName() {
		String clazz = this.getClass().getSimpleName();
		plugin = clazz.replace(ARG_PROCESSOR, "").toLowerCase();
		String resourceName = null;
		if (plugin.equals(AMI)) {
			// no ami/ subdirectory
			resourceName = AMIArgProcessor.PLUGIN_RESOURCE + "/"+ARGS_XML;
		} else {
			resourceName = AMIArgProcessor.PLUGIN_RESOURCE + "/"+plugin+"/"+ARGS_XML;
		}
		return resourceName;
	}

	// ============= METHODS =============
	
	public void parseContext(ArgumentOption option, ArgIterator argIterator) {
		List<String> tokens = argIterator.createTokenListUpToNextNonDigitMinus(option);
		if (tokens.size() == 0) {
			throw new IllegalArgumentException("required argument/s missing");
		}
		contextCount[0] = new Integer(tokens.get(0));
		if (tokens.size() == 2) {
			contextCount[1] = new Integer(tokens.get(1));
		} else {
			contextCount[1] = contextCount[0];
		}
	}

	public void parseUpdate(ArgumentOption option, ArgIterator argIterator) {
		List<String> tokens = argIterator.createTokenListUpToNextNonDigitMinus(option);
		if (tokens.size() != 1) {
			throw new IllegalArgumentException("required single argument missing");
		}
		update = tokens.get(0);
	}

	public void parseParam(ArgumentOption option, ArgIterator argIterator) {
		setParams(argIterator.createTokenListUpToNextNonDigitMinus(option));
		for (String param : getParams()) {
//			if (!HARDCODED_PARAMS.contains(param)) {
//				LOG.debug("The parameters can be "+HARDCODED_PARAMS +"found..."+getParams()+";");
//				throw new RuntimeException("Bad param: "+param);
//			}
		}
	}

	public void parseTest(ArgumentOption option, ArgIterator argIterator) {
		List<String> tokens = argIterator.createTokenListUpToNextNonDigitMinus(option);
		LOG.debug("The test strings are..."+tokens+"; override this if you want to use your own parseTest()");
	}

	public void parseXpath(ArgumentOption option, ArgIterator argIterator) {
		List<String> tokens = argIterator.createTokenListUpToNextNonDigitMinus(option);
		if (tokens.size() == 0) {
//			LOG.debug(XPATH_OPTION).getHelp());
		} else if (tokens.size() > 1) {
			LOG.debug("Exactly one xpath required");
		} else {
			xPathProcessor = new XPathProcessor(tokens.get(0));
		}
	}

	public void parseLookup(ArgumentOption option, ArgIterator argIterator) {
		lookupNames = argIterator.getStrings(option);
		loadLookupClassesFromArgValues(option);
	}

	public void finalLookup(ArgumentOption option) {
		LOG.debug("final lookup NYI; please add code or override: names are: "+lookupNames+"; override");
	}


	// =============run=============
	@Override
	/** In AMI we can split the document by Xpath string which allows per-section searching.
	 * 
	 */
	public void runRunMethodsOnChosenArgOptions() {
		ensureSectionElements();
		super.runRunMethodsOnChosenArgOptions();
	}

	// ============output options==============

	// ==========================

	protected  void printVersion() {
		getVersionManager().printVersion();
		NormaArgProcessor.getVersionManager().printVersion();
	}

	public List<String> getParams() {
		return params;
	}

	public void setParams(List<String> params) {
		this.params = params;
	}

	public Integer[] getContextCount() {
		return contextCount;
	}

	protected List<? extends Element> ensureSectionElements() {
		if (xPathProcessor != null) {
			sectionElements = currentCMDir.extractSectionsFromScholarlyHtml(xPathProcessor.getXPath());
		} else {
			sectionElements = extractPSectionElements(currentCMDir);
		}
		return sectionElements;
	}

//	public List<? extends Element> extractSectionElements(CMDir cmDir) {
//		cmDir.ensureScholarlyHtmlElement();
//		sectionElements = HtmlP.extractSelfAndDescendantIs(cmDir.htmlElement);
//		return sectionElements;
//	}


	public String getPlugin() {
		return plugin;
	}

	private void loadLookupClassesFromArgValues(ArgumentOption option) {
		getOrCreateLookupInstanceByName();
		List<ValueElement> valueElements = option.getOrCreateValueElements();
		for (ValueElement valueElement : valueElements) {
			String name = valueElement.getName();
			String className = valueElement.getClassName();
			if (name == null || className == null) {
				LOG.error("Missing name or class: "+valueElement.toXML());
				continue;
			}
			Class<? extends AbstractLookup> lookupClass;
			try {
				lookupClass = (Class<? extends AbstractLookup>)Class.forName(className);
			} catch (ClassNotFoundException e) {
				LOG.error("Cannot find class, skipping: "+className);
				continue;
			}
			AbstractLookup lookupInstance;
			try {
				lookupInstance = (AbstractLookup) lookupClass.newInstance();
				LOG.debug("lookup "+lookupInstance);
			} catch (Exception e) {
				LOG.error("Cannot instantiate, skipping: "+lookupClass+"; "+e.getMessage());
				continue;
			}
			lookupInstanceByName.put(name, lookupInstance);
		}
	}

	Map<String, AbstractLookup> getOrCreateLookupInstanceByName() {
		if (lookupInstanceByName == null) {
			lookupInstanceByName = new HashMap<String, AbstractLookup>(); 
		}
		return lookupInstanceByName;
	}

	/** create Subclassed Searcher.
	 * 
	 * //PLUGIN
	 * 
	 * Most plugins should Override this and create a FooSearcher.
	 * 
	 * @param namedPattern may be null for non-regex-based searchers
	 * @return subclassed Plugin
	 */
	protected DefaultSearcher createSearcher(NamedPattern namedPattern) {
		AMISearcher defaultSearcher = new AMISearcher(this);
		defaultSearcher.setNamedPattern(namedPattern);
		return defaultSearcher;
	}

	protected void createSearcherList(List<String> names) {
		ensureSearcherBySearcherNameMap();
		ensureSearcherList();
		for (String name : names) {
			DefaultSearcher optionSearcher = (DefaultSearcher) searcherByNameMap.get(name);
			if (optionSearcher == null) {
				LOG.error("unknown optionType: "+name+"; skipped");
			} else {
				searcherList.add(optionSearcher);
			}
		}
	}

	private void ensureSearcherBySearcherNameMap() {
		if (searcherByNameMap == null) {
			searcherByNameMap = new HashMap<String, DefaultSearcher>();
		}
	}

	protected void searchSectionElements() {
		if (currentCMDir != null) {
			ensureSectionElements();
			for (DefaultSearcher searcher : searcherList) {
				String name = searcher.getName();
				LOG.trace("search "+name);
				ResultsElement resultsElement = searcher.search(sectionElements);
				resultsElement.lookup(lookupInstanceByName, lookupNames);
				LOG.trace("exactList "+resultsElement.getExactList());
				resultsElement.setAllResultElementNames(name);
				currentCMDir.putInContentProcessor(name, resultsElement);
			}
		}
	}
	

	protected void createAndStoreNamedSearchers(ArgumentOption option) {
		List<ValueElement> values = option.getOrCreateValueElements();
		createNamedSearchers(values);
	}

	protected void createNamedSearchers(List<ValueElement> values) {
		for (ValueElement valueElement : values) {
			try {
				if (RegexComponent.REGEX.equals(valueElement.getLocalName())) {
					NamedPattern namedPattern = NamedPattern.createFromRegexElement(valueElement);
					createSearcherAndAddToMap(namedPattern);
				} else {
					createSearcherAndAddToMap(valueElement);
				}
			} catch (Exception e) {
				LOG.error("Could not create NamedSearcher "+valueElement.getAttributeValue(NAME)+"; "+e.getCause());
				continue;
			}
		}
		LOG.trace("searchers "+searcherByNameMap);
	}

	public void createSearcherAndAddToMap(Element valueElement) {
		ensureSearcherByNameMap();
		NamedPattern namedPattern = NamedPattern.createFromValueElement(valueElement);
		if (namedPattern != null) {
			LOG.trace("added named pattern "+namedPattern);
			DefaultSearcher searcher = createSearcher(namedPattern);
			searcherByNameMap.put(namedPattern.getName(), searcher);
		}
	}

	public void createSearcherAndAddToMap(NamedPattern namedPattern) {
		if (namedPattern != null) {
			ensureSearcherByNameMap();
			LOG.trace("added named pattern "+namedPattern);
			DefaultSearcher searcher = createSearcher(namedPattern);
			searcherByNameMap.put(namedPattern.getName(), searcher);
		}
	}

	private void ensureSearcherByNameMap() {
		if (searcherByNameMap == null) {
			searcherByNameMap = new HashMap<String, DefaultSearcher>();
		}
	}

	protected void createSearcherList(ArgumentOption option, ArgIterator argIterator) {
		List<String> types = argIterator.getStrings(option);
		createSearcherList(types);
	}

	protected void createRegexElementList(ArgumentOption option, List<String> tokens) {
		List<String> regexLocations = option.processArgs(tokens).getStringValues();
		ensureRegexElementList();
		for (String regexLocation : regexLocations) {
			LOG.trace("RegexLocation "+regexLocation);
			try {
				Element rawCompoundRegex = new Builder().build(EuclidSource.getInputStream(regexLocation)).getRootElement();
				List<Element> elements = XMLUtil.getQueryElements(rawCompoundRegex, ".//*[local-name()='regex']");
				regexElementList.addAll(elements);
			} catch (Exception e) {
				LOG.error("Cannot parse regexLocation: ("+e+")"+regexLocation);
			}
		}
	}

	private void ensureRegexElementList() {
		if (regexElementList == null) {
			regexElementList = new ArrayList<Element>();
		}
	}

	protected void createCompoundRegexes(ArgumentOption option, List<String> tokens) {
		List<String> regexLocations = option.processArgs(tokens).getStringValues();
		getOrCreateCompoundRegexList();
		for (String regexLocation : regexLocations) {
			LOG.trace("RegexLocation "+regexLocation);
			try {
				CompoundRegex compoundRegex = readAndCreateCompoundRegex(EuclidSource.getInputStream(regexLocation));
				compoundRegexList.add(compoundRegex);
			} catch (Exception e) {
				LOG.error("Cannot parse regexLocation: ("+e+")"+regexLocation);
			}
			
		}
	}

	public CompoundRegexList getOrCreateCompoundRegexList() {
		if (compoundRegexList == null) {
			compoundRegexList = new CompoundRegexList();
		}
		return compoundRegexList;
	}

	/** creates a regex from InputStream if possible
	 * 	 * 
	 * @param file
	 * @param is TODO
	 * @return null if not a regex file
	 * @exception RuntimeException if cannot read/parse
	 */
	public CompoundRegex readAndCreateCompoundRegex(InputStream is) {
		Element rootElement = null;
		try {
			Document doc = new Builder().build(is);
			rootElement = doc.getRootElement();
		} catch (Exception e) {
			throw new RuntimeException("Cannot read or parse regexInputStream", e);
		}
		return new CompoundRegex(this, rootElement);
	}

	public CMDir getCurrentCMDir() {
		return currentCMDir;
	}

	protected ContentProcessor getOrCreateContentProcessor() {
		return (currentCMDir == null) ? null : currentCMDir.getOrCreateContentProcessor();
	}

//	public List<? extends Element> getSectionElementList() {
//		return sectionElementList;
//	}

//	protected void ensureSectionElements() {
//		sectionElements = extractSectionElements(currentCMDir);
//	}


}
