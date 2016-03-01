package org.xmlcml.ami2;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
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
import org.xmlcml.cmine.files.CProject;
import org.xmlcml.cmine.files.PluginOption;
import org.xmlcml.cmine.files.ProjectSnippetsTree;
import org.xmlcml.cmine.files.ResultElement;
import org.xmlcml.cmine.files.SnippetsTree;
import org.xmlcml.cmine.files.XMLSnippets;
import org.xmlcml.cmine.util.CMineUtil;
import org.xmlcml.cmine.util.CellCalculator;
import org.xmlcml.cmine.util.CellRenderer;
import org.xmlcml.cmine.util.DataTablesTool;
import org.xmlcml.html.HtmlBr;
import org.xmlcml.html.HtmlDiv;
import org.xmlcml.html.HtmlElement;
import org.xmlcml.html.HtmlSpan;
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
	
	public enum CellType {
		COMMONEST("commonest"),
		COUNT("count"),
		ENTRIES("entries"),
		FULL("full");
		String type;
		private CellType(String type) {
			this.type = type;
		}
		public String toString() {
			return type;
		}
	}
	
	public static final String SCHOLARLY_HTML = "/scholarly.html";
	protected static final String SNIPPETS_XML = "snippets.xml";
	static List<CellType> CELL_TYPES = 
		Arrays.asList(new CellType[]{CellType.COMMONEST, CellType.COUNT, CellType.ENTRIES, CellType.FULL});
	
	public Map<String, ProjectSnippetsTree> projectSnippetsTreeByPluginOption;
	private Set<String> cTreeNameSet;
	public List<String> cTreeNameList;
	public List<String> pluginOptionList;
	private DataTablesTool dataTablesTool;
	private String rowHeadingName;

	@Deprecated
	private List<CellType> cellContentFlags;
	private CellType cellContentFlag;
	
	public ResultsAnalysis() {
	}
		
	public ResultsAnalysis(DataTablesTool dataTablesTool) {
		this.dataTablesTool = dataTablesTool;
		dataTablesTool.setCellCalculator(this);
	}
	
	public void addSnippetsFile(File xmlFile) {
		
		if (xmlFile == null) {
			LOG.error("Null XML file");
			return;
		}
		Element element = null;
		try {
			element = XMLUtil.parseQuietlyToDocument(new FileInputStream(xmlFile)).getRootElement();
		} catch (FileNotFoundException e) {
			LOG.error("Non-existent XML file: "+xmlFile);
			return;
		}
		ProjectSnippetsTree projectSnippetsTree = ProjectSnippetsTree.createProjectSnippetsTree(element);
		if (projectSnippetsTree == null) {
			LOG.warn("Cannot create ProjectSnippetsTree: "+xmlFile);
			return;
		}
		ensureProjectSnippetsTreeByPluginOption();
		PluginOption pluginOption = projectSnippetsTree.getPluginOption();
		if (pluginOption == null) {
			LOG.warn("Null pluginOption");
			return;
		}
		if (projectSnippetsTreeByPluginOption.containsKey(pluginOption)) {
			LOG.warn("Already has pluginOption: "+pluginOption);
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

	public HtmlElement createSnippetsTreeContents(SnippetsTree snippetsTree) {
		HtmlElement htmlElement = null;
		List<XMLSnippets> list = snippetsTree.getOrCreateSnippetsList();
		List<String> terms = createTerms(list);
		Multiset<String> multiset = createMultisetOmittingNullEntries(terms);
		Iterable<Multiset.Entry<String>> entrys = CMineUtil.getEntriesSortedByCount(multiset);
		Iterator<Entry<String>> iterator = entrys.iterator();
		if (cellContentFlag == null) {
			LOG.warn("no cell content flag");
		} else if (cellContentFlag.equals(CellType.FULL)) {
			htmlElement = new HtmlDiv();
			htmlElement.setTitle(String.valueOf(terms.size()));
			int maxCount = 4;
			for (Entry<String> entry : entrys) {
				HtmlElement td = createSpan(entry);
				htmlElement.appendChild(td);
				if (maxCount-- <= 0) {
					break;
				}
				htmlElement.appendChild(new HtmlBr());
			}
		} else if (cellContentFlag.equals(CellType.COMMONEST)) {
			if (iterator.hasNext()) {
				Entry<String> entry = iterator.next();
				htmlElement = createSpan(entry);
			}
		} else if (cellContentFlag.equals(CellType.COUNT)) {
			htmlElement = createSpan(multiset.size());
		} else if (cellContentFlag.equals(CellType.ENTRIES)) {
			htmlElement = createSpan(multiset.entrySet().size());
		} else {
			LOG.warn("Unknown flag: "+cellContentFlag);
		}
		return htmlElement;
	}

	private Multiset<String> createMultisetOmittingNullEntries(List<String> terms) {
		Multiset<String> multiset = HashMultiset.create();
		LOG.debug(terms);
		for (String term : terms) {
			if (term == null) {
				term = "NULL";
			}
			multiset.add(term);
		}
//		multiset.addAll(terms);
		LOG.debug(multiset);
		return multiset;
	}

	private HtmlElement createSpan(int size) {
		HtmlElement htmlElement;
		String ss = String.valueOf(size);
		htmlElement = new HtmlSpan();
		htmlElement.appendChild(ss);
		return htmlElement;
	}

	private HtmlElement createSpan(Entry<String> entry) {
		HtmlElement htmlSpan = new HtmlSpan();
		if (entry.getCount() == 1) {
			// omit the "1" count
			htmlSpan.appendChild(entry.getElement().toString());
		} else {
			// add the count
			htmlSpan.appendChild(entry.toString());
		}
		return htmlSpan;
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
		ensureProjectSnippetsTreeByPluginOption();
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

	public CellCalculator setLocalLink0(String link0) {
		dataTablesTool.setLocalLink0(link0);
		return this;
	}

	public CellCalculator setLocalLink1(String link1) {
		dataTablesTool.setLocalLink1(link1);
		return this;
	}

	public CellCalculator setRemoteLink0(String link0) {
		dataTablesTool.setRemoteLink0(link0);
		return this;
	}

	public CellCalculator setRemoteLink1(String link1) {
		dataTablesTool.setRemoteLink1(link1);
		return this;
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
			HtmlElement htmlElement = createCellContents(iRow, iCol);
			HtmlElement htmlTd = new HtmlTd();
			if (htmlElement != null) {
				htmlTd.appendChild(htmlElement);
			}
			htmlTr.appendChild(htmlTd);
		}
	}

	public List<String> getColumnHeadingList() {
		return pluginOptionList;
	}

	public List<String> getRowHeadingList() {
		return cTreeNameList;
	}

	public HtmlElement createCellContents(int iRow, int iCol) {
		String columnHeadingx = getColumnHeadingList().get(iCol);
		ProjectSnippetsTree projectSnippetsTree = this.projectSnippetsTreeByPluginOption.get(columnHeadingx);
		String rowHeading = getRowHeadingList().get(iRow);
		SnippetsTree snippetsTree = projectSnippetsTree.getOrCreateSnippetsTreeByCTreeName().get(rowHeading);
		HtmlElement contents =  (snippetsTree == null) ? null : this.createSnippetsTreeContents(snippetsTree);
		return contents;
	}

	public void setCellContentFlags(List<CellType> flags) {
		cellContentFlags = flags;
	}

	public void setCellContentFlag(CellType flag) {
		cellContentFlag = flag;
	}

	public void addDefaultSnippets(File projectDir) {
		File[] snippetsFiles = projectDir.listFiles(new FilenameFilter() {
			
			public boolean accept(File dir, String name) {
				return name != null && name.endsWith(SNIPPETS_XML);
			}
		});
		if (snippetsFiles != null) {
			for (File file : snippetsFiles) {
				this.addSnippetsFile(file);
			}
		}
				
//		this.addSnippetsFile(new File(projectDir, CProject.REGEX_STATISTICS_SNIPPETS_XML));
//		this.addSnippetsFile(new File(projectDir, CProject.SEQUENCE_DNAPRIMER_SNIPPETS_XML));
//		this.addSnippetsFile(new File(projectDir, CProject.GENE_HUMAN_SNIPPETS_XML));
//		this.addSnippetsFile(new File(projectDir, CProject.SPECIES_BINOMIAL_SNIPPETS_XML));
//		this.addSnippetsFile(new File(projectDir, CProject.SPECIES_GENUS_SNIPPETS_XML));
//		this.addSnippetsFile(new File(projectDir, CProject.WORD_FREQUENCIES_SNIPPETS_XML));
	}
	
}
