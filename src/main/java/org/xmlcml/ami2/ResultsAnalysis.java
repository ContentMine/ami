package org.xmlcml.ami2;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
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
import org.xmlcml.cmine.util.CellCalculator;
import org.xmlcml.cmine.util.DataTablesTool;
import org.xmlcml.cmine.util.CellRenderer;
import org.xmlcml.html.HtmlTable;
import org.xmlcml.html.HtmlTd;
import org.xmlcml.html.HtmlTr;
import org.xmlcml.xml.XMLUtil;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import com.google.common.collect.Multiset.Entry;

import nu.xom.Element;

public class ResultsAnalysis implements CellCalculator {

	private static final Logger LOG = Logger.getLogger(ResultsAnalysis.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	public static final String COMMONEST = "commonest";
	public static final String COUNT = "count";
	public static final String ENTRIES = "entries";
	
	public Map<String, ProjectSnippetsTree> projectSnippetsTreeByPluginOption;
	private Set<String> cTreeNameSet;
	public List<String> cTreeNameList;
	public List<String> pluginOptionList;
	private DataTablesTool dataTablesTool;
	private String link0;
	private String link1;
	private String rowHeadingName;

	private List<String> cellContentFlags;
	
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

	public String createSnippetsTreeContents(SnippetsTree snippetsTree) {
		String ss = "";
		List<XMLSnippets> list = snippetsTree.getOrCreateSnippetsList();
		List<String> terms = createTerms(list);
		Multiset<String> multiset = HashMultiset.create();
		multiset.addAll(terms);
		LOG.trace(multiset);
		Iterable<Multiset.Entry<String>> entrys = CMineUtil.getEntriesSortedByCount(multiset);
		Iterator<Entry<String>> iterator = entrys.iterator();
		if (cellContentFlags == null) {
			ss = entrys.toString();
			ss = ss.substring(1, Math.min(50, ss.length()));
			ss = terms.size()+": "+ss;
		} else if (cellContentFlags.contains(COMMONEST)) {
			if (iterator.hasNext()) {
				ss += iterator.next().getElement();
			}
		} else if (cellContentFlags.contains(COUNT)) {
			ss = String.valueOf(multiset.size());
		} else if (cellContentFlags.contains(ENTRIES)) {
			ss = String.valueOf(multiset.entrySet().size());
		} else {
			LOG.warn("Unknown flags: "+cellContentFlags);
		}
		return ss;
	}

	private List<String> createTerms(List<XMLSnippets> list) {
		List<String> terms = new ArrayList<String>();
		for (XMLSnippets snippets : list) {
			for (int i = 0; i < snippets.getChildElements().size(); i++) {
				ResultElement resultElement = ResultElement.createResultElement(snippets.getChildElements().get(i));
				String term = resultElement.getTerm();
				terms.add(term);
			}
		}
		return terms;
	}

	public HtmlTable makeHtmlDataTable() {
		Set<String> set = this.projectSnippetsTreeByPluginOption.keySet();
		pluginOptionList = Arrays.asList(set.toArray(new String[0]));
		Collections.sort(this.pluginOptionList);
		
		List<CellRenderer> flagList = new ArrayList<CellRenderer>();
		for (String pluginOption : pluginOptionList) {
			CellRenderer cellRenderer = new CellRenderer(pluginOption);
			flagList.add(cellRenderer);
		}
		dataTablesTool.setColumnHeadingList(flagList);
		dataTablesTool.setRowHeadingList(this.getSortedCTreeNameList());
		
		HtmlTable htmlTable = dataTablesTool.createHtmlDataTable();
		htmlTable.setClassAttribute(DataTablesTool.TABLE+" "+DataTablesTool.TABLE_STRIPED+" "+DataTablesTool.TABLE_BORDERED+" "+DataTablesTool.TABLE_HOVER);
		htmlTable.setId(dataTablesTool.getId());

		return htmlTable;
	}

	public CellCalculator setLink0(String link0) {
		this.link0 = link0;
		return this;
	}

	public String getLink0() {
		return link0;
	}

	public CellCalculator setLink1(String link1) {
		this.link1 = link1;
		return this;
	}

	public String getLink1() {
		return link1;
	}

	public ResultsAnalysis setRowHeadingName(String rowHeadingName) {
		this.rowHeadingName = rowHeadingName;
		return this;
	}

	public String getRowHeadingName() {
		return this.rowHeadingName;
	}

	public DataTablesTool getDataTablesTool() {
		return dataTablesTool;
	}

	public void addCellValues(List<CellRenderer> columnHeadingList, HtmlTr htmlTr, int iRow) {
		for (int iCol = 0; iCol < columnHeadingList.size(); iCol++) {
			String ss = createCellContents(iRow, iCol);
			ss = (ss == null) ? "" : ss;
			HtmlTd htmlTd = new HtmlTd();
			htmlTd.appendChild(ss);
			htmlTr.appendChild(htmlTd);
		}
	}

	public List<String> getColumnHeadingList() {
		return pluginOptionList;
	}

	public List<String> getRowHeadingList() {
		return cTreeNameList;
	}

	public String createCellContents(int iRow, int iCol) {
		String columnHeadingx = getColumnHeadingList().get(iCol);
		ProjectSnippetsTree projectSnippetsTree = this.projectSnippetsTreeByPluginOption.get(columnHeadingx);
		String rowHeading = getRowHeadingList().get(iRow);
		SnippetsTree snippetsTree = projectSnippetsTree.getOrCreateSnippetsTreeByCTreeName().get(rowHeading);
		String contents =  (snippetsTree == null) ? null : this.createSnippetsTreeContents(snippetsTree);
		return contents;
	}

	public void setCellContentFlags(String flagString) {
		cellContentFlags = Arrays.asList(flagString.split("\\s+"));
	}






	
}
