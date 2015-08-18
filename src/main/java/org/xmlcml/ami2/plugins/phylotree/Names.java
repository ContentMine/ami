package org.xmlcml.ami2.plugins.phylotree;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.FileUtils;

public class Names {

	public Names() {
		
	}
	
	public void readLines(File file) throws IOException {
		List<String> lines = FileUtils.readLines(file);
		List<NameId> nameList = new ArrayList<NameId>();
		Set<Integer> idSet = new HashSet<Integer>();
		Set<String> nameSet = new HashSet<String>();
		for (String line : lines) {
			line = line.substring(1,  line.length()-1);
			String[] parts = line.split("@");
			Integer id = Integer.parseInt(parts[0].trim());
			String name = parts[1].trim();
			if (idSet.contains(id)) System.out.println(id);
//			if (nameSet.contains(name)) System.out.println(id+" "+name);
			NameId nameId = new NameId(id, name);
			idSet.add(id);
			nameSet.add(name);
		}
		List<String> names = new ArrayList<String>(nameSet);
		Collections.sort(names);
		FileUtils.writeLines(new File("src/main/resources/org/xmlcml/ami2/plugins/phylotree/taxdump/sorted,unique.txt"), names);
	}
	
	public static void main(String[] args) throws Exception {
		Names names = new Names();
		names.readLines(new File("src/main/resources/org/xmlcml/ami2/plugins/phylotree/taxdump/names.trimmed.txt"));
	}
}
class NameId {
	
	private int id;
	private String name;

	public NameId(Integer id, String name) {
		this.id = id;
		this.name = name;
	}
}
