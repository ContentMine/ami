package org.xmlcml.ami2;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.cmine.files.PluginOption;
import org.xmlcml.cmine.files.ProjectSnippetsTree;
import org.xmlcml.cmine.files.ResultElement;
import org.xmlcml.cmine.files.SnippetsTree;
import org.xmlcml.cmine.files.XMLSnippets;
import org.xmlcml.cmine.util.CMineUtil;
import org.xmlcml.html.HtmlA;
import org.xmlcml.html.HtmlTable;
import org.xmlcml.html.HtmlTbody;
import org.xmlcml.html.HtmlTd;
import org.xmlcml.html.HtmlTh;
import org.xmlcml.html.HtmlThead;
import org.xmlcml.html.HtmlTr;
import org.xmlcml.xml.XMLUtil;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;

import nu.xom.Element;

public class ResultsAnalysis {

	private static final Logger LOG = Logger.getLogger(ResultsAnalysis.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	private Map<PluginOption, ProjectSnippetsTree> projectSnippetsTreeByPluginOption;
	private Set<String> cTreeNameSet;
	private List<String> cTreeNameList;
	private List<PluginOption> pluginOptionList;
	private HtmlTbody htmlTbody;
	private HtmlThead htmlThead;
	
	public void addSnippetsFile(File xmlFile) throws FileNotFoundException {
		Element element = XMLUtil.parseQuietlyToDocument(new FileInputStream(xmlFile)).getRootElement();
		ProjectSnippetsTree projectSnippetsTree = ProjectSnippetsTree.createProjectSnippetsTree(element);
		if (projectSnippetsTree == null) {
			throw new RuntimeException("Cannot create ProjectSnippetsTree: "+xmlFile);
		}
		ensureProjectSnippetsTreeByPluginOption();
		PluginOption pluginOption = projectSnippetsTree.getPluginOption();
		if (pluginOption == null) {
			throw new RuntimeException("Null pluginOption");
		}
		if (projectSnippetsTreeByPluginOption.containsKey(pluginOption)) {
			throw new RuntimeException("Already has pluginOption: "+pluginOption);
		}
		projectSnippetsTreeByPluginOption.put(pluginOption, projectSnippetsTree);
	}
	
	private void ensureProjectSnippetsTreeByPluginOption() {
		if (projectSnippetsTreeByPluginOption == null) {
			projectSnippetsTreeByPluginOption = new HashMap<PluginOption, ProjectSnippetsTree>();
		}
	}

	public Map<PluginOption, ProjectSnippetsTree> getProjectSnippetsTreeByPluginOption() {
		return projectSnippetsTreeByPluginOption;
	}

	public Set<String> getCTreeNameSet() {
		if (cTreeNameSet == null) {
			cTreeNameSet = new HashSet<String>();
			ensureProjectSnippetsTreeByPluginOption();
			for (Map.Entry<PluginOption, ProjectSnippetsTree> entry : projectSnippetsTreeByPluginOption.entrySet()) {
				cTreeNameSet.addAll(entry.getValue().getCTreeNameList());
			}
		}
		return cTreeNameSet;
	}

	public List<String> getSortedCTreeNameList() {
		getCTreeNameSet();
		cTreeNameList = Arrays.asList(cTreeNameSet.toArray(new String[0]));
		Collections.sort(cTreeNameList);
		return cTreeNameList;
	}

	public HtmlTable makeHtmlTable() {
		getSortedCTreeNameList();
		pluginOptionList = Arrays.asList(projectSnippetsTreeByPluginOption.keySet().toArray(new PluginOption[0]));
		Collections.sort(pluginOptionList);
		
		HtmlTable htmlTable = new HtmlTable();
		htmlTable.appendChild(createHtmlHead());
		htmlTbody = new HtmlTbody();
		htmlTable.appendChild(htmlTbody);
		addRowsForCTrees();
		return htmlTable;
	}

	private HtmlThead createHtmlHead() {
		htmlThead = new HtmlThead();
		HtmlTr htmlTr = new HtmlTr();
		htmlThead.appendChild(htmlTr);
		addIDColumnHeading(htmlTr);
		addRemainingColumnHeadings(htmlTr);
		return htmlThead;
	}

	private void addRowsForCTrees() {
		for (String cTreeName : cTreeNameList) {
			HtmlTr htmlTr = new HtmlTr();
			htmlTbody.appendChild(htmlTr);
			addHyperlinkedIDCell(cTreeName, htmlTr);
			addCellValues(htmlTr, cTreeName);
		}
	}

	private void addHyperlinkedIDCell(String cTreeName, HtmlTr htmlTr) {
		HtmlTd htmlTd = new HtmlTd();
		htmlTr.appendChild(htmlTd);
		HtmlA htmlA = new HtmlA();
		htmlA.appendChild(cTreeName);
		htmlA.setHref("../../src/test/resources/org/xmlcml/ami2/zika/"+cTreeName+"/scholarly.html");
		htmlTd.appendChild(htmlA);
	}

	private void addCellValues(HtmlTr htmlTr, String cTreeName) {
		HtmlTd htmlTd;
		for (PluginOption pluginOption : pluginOptionList) {
			htmlTd = new HtmlTd();
			htmlTr.appendChild(htmlTd);
			ProjectSnippetsTree projectSnippetsTree = projectSnippetsTreeByPluginOption.get(pluginOption);
			SnippetsTree snippetsTree = projectSnippetsTree.getOrCreateSnippetsTreeByCTreeName().get(cTreeName);
			if (snippetsTree != null) {
				addSnippetsTreeContents(snippetsTree, htmlTd);
			}
		}
	}

	private void addSnippetsTreeContents(SnippetsTree snippetsTree, HtmlTd htmlTd) {
		List<XMLSnippets> list = snippetsTree.getOrCreateSnippetsList();
		List<String> terms = new ArrayList<String>();
		for (XMLSnippets snippets : list) {
			for (int i = 0; i < snippets.getChildElements().size(); i++) {
				ResultElement resultElement = ResultElement.createResultElement(snippets.getChildElements().get(i));
				String term = resultElement.getTerm();
				terms.add(term);
			}
		}
		Multiset<String> multiset = HashMultiset.create();
		multiset.addAll(terms);
//		String ss = multiset.toString();
		LOG.debug(multiset);
		Iterable<Multiset.Entry<String>> entrys = CMineUtil.getEntriesSortedByCount(multiset);
		String ss = entrys.toString();
		ss = ss.substring(1, Math.min(50, ss.length()));
		ss = terms.size()+": "+ss;
		htmlTd.appendChild(ss);
	}

	private void addIDColumnHeading(HtmlTr htmlTr) {
		HtmlTh htmlTh = new HtmlTh();
		htmlTr.appendChild(htmlTh);
		htmlTh.appendChild("EPMCID");
	}

	private void addRemainingColumnHeadings(HtmlTr htmlTr) {
		for (PluginOption pluginOption : pluginOptionList) {
			HtmlTh htmlTh = new HtmlTh();
			htmlTr.appendChild(htmlTh);
			htmlTh.appendChild(pluginOption.toString());
		}
	}

	
}
