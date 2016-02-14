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
import org.xmlcml.cmine.util.DataTablesTool;
import org.xmlcml.html.HtmlTable;
import org.xmlcml.html.HtmlTbody;
import org.xmlcml.html.HtmlTd;
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
	
	public Map<String, ProjectSnippetsTree> projectSnippetsTreeByPluginOption;
	private Set<String> cTreeNameSet;
	private List<String> cTreeNameList;
	public List<String> pluginOptionList;
//	public List<String> columnHeadingList;
//	private List<String> rowHeadingList;
	private HtmlTbody htmlTbody;
	private DataTablesTool dataTablesTool;
	
	public ResultsAnalysis() {
	}
		
	public ResultsAnalysis(DataTablesTool dataTablesTool) {
		this.dataTablesTool = dataTablesTool;
	}
	
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
		projectSnippetsTreeByPluginOption.put(pluginOption.toString(), projectSnippetsTree);
	}
	
	private void ensureProjectSnippetsTreeByPluginOption() {
		if (projectSnippetsTreeByPluginOption == null) {
			projectSnippetsTreeByPluginOption = new HashMap<String, ProjectSnippetsTree>();
		}
	}

	public Map<String, ProjectSnippetsTree> getProjectSnippetsTreeByPluginOption() {
		return projectSnippetsTreeByPluginOption;
	}

	public Set<String> getCTreeNameSet() {
		if (cTreeNameSet == null) {
			cTreeNameSet = new HashSet<String>();
			ensureProjectSnippetsTreeByPluginOption();
			for (Map.Entry<String, ProjectSnippetsTree> entry : projectSnippetsTreeByPluginOption.entrySet()) {
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
		LOG.debug(multiset);
		Iterable<Multiset.Entry<String>> entrys = CMineUtil.getEntriesSortedByCount(multiset);
		String ss = entrys.toString();
		ss = ss.substring(1, Math.min(50, ss.length()));
		ss = terms.size()+": "+ss;
		htmlTd.appendChild(ss);
	}

	public HtmlTable makeDataTablesTable() {
		HtmlTable table = this.makeHtmlTable(dataTablesTool, "foo", "bar", dataTablesTool.getId());
		table.setClassAttribute(DataTablesTool.TABLE+" "+DataTablesTool.TABLE_STRIPED+" "+DataTablesTool.TABLE_BORDERED+" "+DataTablesTool.TABLE_HOVER);
		table.setId(dataTablesTool.getId());
		return table;
	}
	
	public HtmlTable makeHtmlTable(DataTablesTool dataTablesTool, String link0, String link1, String id) {
		dataTablesTool.setRowHeading(this.getSortedCTreeNameList());
		Set<String> set = this.projectSnippetsTreeByPluginOption.keySet();
		pluginOptionList = Arrays.asList(set.toArray(new String[0]));
		Collections.sort(this.pluginOptionList);
		dataTablesTool.setColumnHeadingList(pluginOptionList);
		
		HtmlTable htmlTable = new HtmlTable();
		htmlTable.appendChild(dataTablesTool.createHtmlHead(id));
		HtmlTbody htmlTbody = new HtmlTbody();
		htmlTable.appendChild(htmlTbody);
		this.addRowsForCTrees(link0, link1,  htmlTbody);
		return htmlTable;
	}
	
	private void addCellValues(HtmlTr htmlTr, String cTreeName) {
		HtmlTd htmlTd;
		List<String> columnHeadings = dataTablesTool.getColumnHeadingList();
		for (String columnHeading : columnHeadings) {
			htmlTd = new HtmlTd();
			htmlTr.appendChild(htmlTd);
			ProjectSnippetsTree projectSnippetsTree = projectSnippetsTreeByPluginOption.get(columnHeading);
			SnippetsTree snippetsTree = projectSnippetsTree.getOrCreateSnippetsTreeByCTreeName().get(cTreeName);
			if (snippetsTree != null) {
				addSnippetsTreeContents(snippetsTree, htmlTd);
			}
		}
	}
	
	public void addRowsForCTrees(String link0, String link1,  HtmlTbody htmlTbody) {
		for (String rowHeading : dataTablesTool.getRowHeadingList()) {
			HtmlTr htmlTr = new HtmlTr();
			htmlTbody.appendChild(htmlTr);
			String href = link0 + rowHeading + link1;
			dataTablesTool.addHyperlinkedIDCell(href, rowHeading, htmlTr);
			addCellValues(htmlTr, rowHeading);
		}
	}






	
}
