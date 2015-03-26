package org.xmlcml.ami2.plugins.identifier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import nu.xom.Element;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.ami2.plugins.AMIArgProcessor;
import org.xmlcml.args.ArgIterator;
import org.xmlcml.args.ArgumentOption;
import org.xmlcml.euclid.IntRange;
import org.xmlcml.files.QuickscrapeNorma;
import org.xmlcml.files.ResultsElement;
import org.xmlcml.html.HtmlP;

/** 
 * Processes commandline arguments.
 * 
 * @author pm286
 */
public class IdentifierArgProcessor extends AMIArgProcessor {
	
	
	public static final Logger LOG = Logger.getLogger(IdentifierArgProcessor.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	private Map<String, IdentifierSearcher> sequenceSearcherByTypeStringMap;
	private IntRange lengthRange;
	private List<IdentifierSearcher> sequenceSearcherList;
	private Map<String, ResultsElement> resultsBySequenceTypeStringMap;

	public IdentifierArgProcessor() {
		super();
	}

	public IdentifierArgProcessor(String[] args) {
		this();
		parseArgs(args);
	}

	public IdentifierArgProcessor(String argString) {
		this(argString.split("\\s+"));
	}

	// =============== METHODS ==============
	
	public void initSequences(ArgumentOption option) {
		ensureSequenceSearcherByTypeString();
		List<Element> values = option.getOrCreateValues();
		for (Element valueElement : values) {
			try {
				createAndStoreSequenceSearchers(valueElement);
			} catch (Exception e) {
				LOG.error("Could not create SequenceSearcher "+valueElement.getAttributeValue(ArgumentOption.NAME)+"; "+e.getCause());
				continue;
			}
		}
	}

	public void parseTypes(ArgumentOption option, ArgIterator argIterator) {
		List<String> types = argIterator.getStrings(option);
		convertToSequenceSearchers(types);
	}

	public void parseLength(ArgumentOption option, ArgIterator argIterator) {
		lengthRange = argIterator.getIntRange(option);
	}

	public void runExtractSequences(ArgumentOption option) {
		List<HtmlP> pElements = extractPElements();
		resultsBySequenceTypeStringMap = new HashMap<String, ResultsElement>();
		for (IdentifierSearcher sequenceSearcher : sequenceSearcherList) {
			String type = sequenceSearcher.getType();
			ResultsElement resultsElement = sequenceSearcher.search(pElements);
			resultsBySequenceTypeStringMap.put(type, resultsElement);
		}
	}

	public void outputSequences(ArgumentOption option) {
		outputResultElements(option);
	}
	
	public void parseSummary(ArgumentOption option, ArgIterator argIterator) {
//		summaryMethods = argIterator.getStrings(option);
		LOG.debug("summary methods not yet written");
	}
	
	public void finalSummary(ArgumentOption option) {
		LOG.debug("final summary not yet written");
	}

	// =============================

	private void createAndStoreSequenceSearchers(Element valueElement) {
		String type = valueElement.getAttributeValue(NAME);
		String patternString = valueElement.getValue();
		SequenceType sequenceType = new SequenceType(type, patternString);
		Pattern pattern = null;
		try {
			pattern = Pattern.compile(patternString);
		} catch (Exception e) {
			LOG.debug("BAD REGEX: "+patternString);
			throw new RuntimeException("Bad regex", e);
		}
		IdentifierSearcher sequenceSearcher = new IdentifierSearcher(this, sequenceType, pattern);
		sequenceSearcherByTypeStringMap.put(type, sequenceSearcher);
	}

	private void ensureSequenceSearcherByTypeString() {
		if (sequenceSearcherByTypeStringMap == null) {
			sequenceSearcherByTypeStringMap = new HashMap<String, IdentifierSearcher>();
		}
	}

	private void convertToSequenceSearchers(List<String> types) {
		if (sequenceSearcherByTypeStringMap == null) {
			throw new RuntimeException("No sequences read");
		}
		ensureSequenceSearchers();
		for (String type : types) {
			IdentifierSearcher sequenceSearcher = sequenceSearcherByTypeStringMap.get(type);
			if (sequenceSearcher == null) {
				LOG.error("unknown sequenceType: "+type+"; skipped");
			} else {
				sequenceSearcherList.add(sequenceSearcher);
			}
		}
	}

	private void ensureSequenceSearchers() {
		if (sequenceSearcherList == null) {
			sequenceSearcherList = new ArrayList<IdentifierSearcher>();
		}
		if (sequenceSearcherList == null) {
			sequenceSearcherList = new ArrayList<IdentifierSearcher>();
		}
	}

	private void outputResultElements(ArgumentOption option) {
		resultsElementList = new ArrayList<ResultsElement>();
		for (IdentifierSearcher sequenceSearcher : sequenceSearcherList) {
			String type = sequenceSearcher.getSequenceType().getType();
			ResultsElement resultsElement = resultsBySequenceTypeStringMap.get(type);
			if (resultsElement != null) {
				resultsElement.setTitle(type);
				resultsElementList.add(resultsElement);
			}
		}
		currentQuickscrapeNorma.createResultsDirectoriesAndOutputResultsElement(
				option, resultsElementList, QuickscrapeNorma.RESULTS_XML);
	}


}
