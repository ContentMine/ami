# Search and Analysis Tutorial

Updated 20160125

Read in conjunction with
https://github.com/petermr/ami-plugin/blob/master/src/test/java/org/xmlcml/ami2/TutorialTest.java
especially `testSpeciesSequencesGeneWordsCMine()`

This demonstrates the analysis of several fairly diverse `CTree`s containing a mixture of 
* genes
* species
* sequences
* words

## files

There are 7 `CTree`s which have already been `norma`lized. `mixed` represents the `cProject`

```
mixed
├── file0
│   ├── fulltext.html
│   ├── fulltext.pdf
│   ├── fulltext.xml
│   └── scholarly.html
├── file1
│   ├── fulltext.html
│   ├── fulltext.pdf
│   ├── fulltext.xml
│   └── scholarly.html
... snipped
└── file6
    ├── fulltext.html
    ├── fulltext.pdf
    ├── fulltext.xml
    ├── results.json
    └── scholarly.html

```
To avoid cluttering the test material we copy them to `target`. (If you trust your operation, you can write directly to the `cProject`)
```
		File targetDir = new File("target/tutorial/mixed");
		CMineTestFixtures.cleanAndCopyDir(new File("src/test/resources/org/xmlcml/ami2/mixed"), targetDir);
```
All results will thus occur in `target`

## Search

The first operation is to use the specific `ArgProcessor` for these concepts, create `results.xml` and then
analyze these. Note that if there are no results, `empty.xml` is created to represent the fact it has been
searched. 
```
		LOG.debug("search for DNA Primers");
		args = "--project "+targetDir+" -i scholarly.html --sq.sequence --context 35 --sq.type dnaprimer";
		new SequenceArgProcessor(args).runAndOutput();

		LOG.debug("wordFrequencies");
		args = "--project "+targetDir+" -i scholarly.html"
				+ " --w.words wordFrequencies --w.stopwords /org/xmlcml/ami2/plugins/word/stopwords.txt ";
		new WordArgProcessor(args).runAndOutput();
		
		LOG.debug("species");
		args = "--project "+targetDir+" -i scholarly.html --sp.species --context 35 --sp.type binomial genus";
		new SpeciesArgProcessor(args).runAndOutput();
		
		LOG.debug("genes");
		args = "--project "+targetDir+" -i scholarly.html --g.gene --context 100 --g.type human";
		new GeneArgProcessor(args).runAndOutput();

```

These take about 500 ms for 7 articles - so ca 70 secs for 5 operations (DNA, words, species *2, genes) or 15 secs 
each. 

## results.xml

The operations all write to `results.xml`. Part of the output tree (with fulltext.* omitted):
```
.
├── file0
│   ├── results
│   │   ├── gene
│   │   │   └── human
│   │   │       └── results.xml
│   │   ├── sequence
│   │   │   └── dnaprimer
│   │   │       └── empty.xml
│   │   ├── species
│   │   │   ├── binomial
│   │   │   │   └── empty.xml
│   │   │   └── genus
│   │   │       └── results.xml
│   │   └── word
│   │       └── frequencies
│   │           ├── results.html
│   │           └── results.xml
│   ├── scholarly.html
│── file1
│   ├── results
│   │   ├── gene
│   │   │   └── human
│   │   │       └── empty.xml
│   │   ├── sequence
│   │   │   └── dnaprimer
│   │   │       └── empty.xml
│   │   ├── species
│   │   │   ├── binomial
│   │   │   │   └── results.xml
│   │   │   └── genus
│   │   │       └── empty.xml
│   │   └── word
│   │       └── frequencies
│   │           ├── results.html
│   │           └── results.xml
│   ├── scholarly.html
│── file2
│   ├── results
│   │   ├── gene
│   │   │   └── human
│   │   │       └── empty.xml
│   │   ├── sequence
│   │   │   └── dnaprimer
│   │   │       └── empty.xml
│   │   ├── species
│   │   │   ├── binomial
│   │   │   │   └── results.xml
│   │   │   └── genus
│   │   │       └── empty.xml
│   │   └── word
│   │       └── frequencies
│   │           ├── results.html
│   │           └── results.xml
│   ├── scholarly.html

```
Notice that trees have some tips with `results.xml` and others with `empty.xml` (of course `word` always has `results.xml`). 

## analysis

The `--analyze` flag operates on both `ctree`s and the containg `cproject`. For each `ctree` it:
 
 * lists all the `results.xml` files within the `ctree`
 * lists all the `snippets` extracted within the `results.xml`
 
 ```
 ├── file0
 │   ├── geneFiles.xml
 │   ├── geneSnippets.xml
 │   ├── genegeneSnippets.xml
 │   ├── results
 │   │   ├── gene
 │   │   │   └── human
 │   │   │       └── results.xml
 │   │   ├── sequence
 │   │   │   └── dnaprimer
 │   │   │       └── empty.xml
 │   │   ├── species
 │   │   │   ├── binomial
 │   │   │   │   └── empty.xml
 │   │   │   └── genus
 │   │   │       └── results.xml
 │   │   └── word
 │   │       └── frequencies
 │   │           ├── results.html
 │   │           └── results.xml
 │   ├── scholarly.html
 │   ├── sequenceFiles.xml
 │   ├── sequenceSnippets.xml
 │   ├── speciesFiles.xml
 │   ├── speciesSnippets.xml
 │   ├── wordFiles.xml
 │   └── wordSnippets.xml
 ├── file1
 │   ├── geneFiles.xml
 │   ├── geneSnippets.xml
 │   ├── genegeneSnippets.xml
 │   ├── results
 │   │   ├── gene
 │   │   │   └── human
 │   │   │       └── empty.xml
 │   │   ├── sequence
 │   │   │   └── dnaprimer
 │   │   │       └── empty.xml
 │   │   ├── species
 │   │   │   ├── binomial
 │   │   │   │   └── results.xml
 │   │   │   └── genus
 │   │   │       └── empty.xml
 │   │   └── word
 │   │       └── frequencies
 │   │           ├── results.html
 │   │           └── results.xml
 │   ├── scholarly.html
 │   ├── sequenceFiles.xml
 │   ├── sequenceSnippets.xml
 │   ├── speciesFiles.xml
 │   ├── speciesSnippets.xml
 │   ├── wordFiles.xml
 │   └── wordSnippets.xml
 ├── file2
 ```
 There are four `ami-plugin`s, 
 
  * `gene`
  * `sequence`
  * `species`
  * `word`
  
  and each can have a `*Files` and `*Snippets` file. The snippets can come from any XML file, but most usually either the 
  input (`fulltext.xml` or equivalent) or `results.xml`. Thus `geneFiles` lists the files under `results/gene`.
  In our example there is only `human` as a gene category but in principle there might be many different genes. 
  `species` has `binomial` and `geneus` so coulsd have up to two `results.xml` files.
  
    
### `*Files`

As an example `speciesFiles` can contain 0, 1 or 2 components:

```
file1/speciesFies.xml

<cTreeFiles cTree="target/tutorial/mixed/file0">
 <file name="target/tutorial/mixed/file0/results/species/genus/results.xml"/>
</cTreeFiles>

file3/speciesFies.xml

<cTreeFiles cTree="target/tutorial/mixed/file3">
 <file name="target/tutorial/mixed/file3/results/species/binomial/results.xml"/>
 <file name="target/tutorial/mixed/file3/results/species/genus/results.xml"/>
</cTreeFiles>
````

with no `results.xml` there is a stub:

```
file5/sequenceFiles.xml
<cTreeFiles cTree="target/tutorial/mixed/file5"/>
```

### `*Snippets`

The `<result>` elements in `results.xml` files are "snippets" o the document, usually with
the extracted entity and some characters/words of context. This can be tansferred to a `*Snippets.xml`
file. Typical file `file5/speciesSnippets.xml` (aggregated)
```
<projectSnippetsTree>
 <snippetsTree>
  <snippets file="target/tutorial/mixed/file0/results/species/genus/results.xml">
   <result pre="euronal progenitor) lineage in the " exact="Drosophila" xpath="/*[local-name()='html'][1]/*[local-name()='body'][1]/*[local-name()='div'][1]/*[local-name()='div'][11]/*[local-name()='div'][3]/*[local-name()='p'][3]" match="Drosophila" post=" brain. Loss of orthodenticle&lt;/i" name="genus"/>
   <result pre="vely simple brain of the fruit fly " exact="Drosophila" xpath="/*[local-name()='html'][1]/*[local-name()='body'][1]/*[local-name()='div'][1]/*[local-name()='div'][11]/*[local-name()='div'][3]/*[local-name()='p'][3]" match="Drosophila" post=" have been identified. Furthermore," name="genus"/>
   <result pre="identity of each neuroblast in the " exact="Drosophila" xpath="/*[local-name()='html'][1]/*[local-name()='body'][1]/*[local-name()='div'][1]/*[local-name()='div'][11]/*[local-name()='div'][3]/*[local-name()='p'][3]" match="Drosophila" post=" brain are known. These genes may a" name="genus"/>
   <result pre="xt to each other in the developing " exact="Drosophila" xpath="/*[local-name()='html'][1]/*[local-name()='body'][1]/*[local-name()='div'][1]/*[local-name()='div'][11]/*[local-name()='div'][3]/*[local-name()='p'][3]" match="Drosophila" post=" brain, produces neurons for differ" name="genus"/>
...
  </snippets>
 </snippetsTree>
 <snippetsTree>
  <snippets file="target/tutorial/mixed/file1/results/species/binomial/results.xml">
   <result pre="Longevity of " exact="Rhizoprionodon terraenovae" xpath="/*[local-name()='html'][1]/*[local-name()='body'][1]/*[local-name()='div'][1]/*[local-name()='div'][11]/*[local-name()='div'][4]/*[local-name()='p'][1]" match="Rhizoprionodon terraenovae" post=" and Carcharhinus acronotus " name="binomial"/>
   <result pre="Rhizoprionodon terraenovae and " exact="Carcharhinus acronotus" xpath="/*[local-name()='html'][1]/*[local-name()='body'][1]/*[local-name()='div'][1]/*[local-name()='div'][11]/*[local-name()='div'][4]/*[local-name()='p'][1]" match="Carcharhinus acronotus" post=" in the western North Atlantic Ocea" name="binomial"/>
   <result pre="om 7.7-14.0 years (mean =10.1) for " exact="R. terraenovae" xpath="/*[local-name()='html'][1]/*[local-name()='body'][1]/*[local-name()='div'][1]/*[local-name()='div'][11]/*[local-name()='div'][4]/*[local-name()='p'][1]" match="Rhizoprionodon terraenovae" post=" and 10.9-12.8 years (mean =11.9) f" name="binomial"/>
   ...
  </snippets>
 </snippetsTree>
 <snippetsTree>
  <snippets file="target/tutorial/mixed/file2/results/species/binomial/results.xml">
   <result pre=" in cooperative tasks (marmosets ( " exact="Callithrix jacchus" xpath="/*[local-name()='html'][1]/*[local-name()='body'][1]/*[local-name()='div'][1]/*[local-name()='div'][3]/*[local-name()='p'][3]" match="Callithrix jacchus" post="): Werdenich and Huber, 2002; chimp" name="binomial"/>
   <result pre="1993; Melis et al., 2006b; rooks ( " exact="Corvus frugilegus" match="Corvus frugilegus" post="): Seed et al., 2008; Scheid and No" name="binomial"/>
  </snippets>
 </snippetsTree>
 <snippetsTree>
  <snippets file="target/tutorial/mixed/file3/results/species/binomial/results.xml">
   <result pre="vailable for Fusarium poae. " exact="F. poae" xpath="/*[local-name()='html'][1]/*[local-name()='body'][1]/*[local-name()='div'][1]/*[local-name()='div'][5]/*[local-name()='div'][2]/*[local-name()='p'][1]" match="F. poae" post=" is one of the species complexes in" name="binomial"/>
   <result pre=" organic compounds associated with " exact="F. poae" xpath="/*[local-name()='html'][1]/*[local-name()='body'][1]/*[local-name()='div'][1]/*[local-name()='div'][5]/*[local-name()='div'][2]/*[local-name()='p'][1]" match="F. poae" post=" metabolism could provide good mark" name="binomial"/>
   <result pre="he volatile profile of healthy and " exact="F. poae" xpath="/*[local-name()='html'][1]/*[local-name()='body'][1]/*[local-name()='div'][1]/*[local-name()='div'][5]/*[local-name()='div'][2]/*[local-name()='p'][1]" match="F. poae" post="-infected durum wheat kernels by SP" name="binomial"/>
   <result pre="mpounds, could be used to identify " exact="F. poae" xpath="/*[local-name()='html'][1]/*[local-name()='body'][1]/*[local-name()='div'][1]/*[local-name()='div'][5]/*[local-name()='div'][2]/*[local-name()='p'][1]" match="F. poae" post=" contamination of durum wheat grain" name="binomial"/>
   <result pre="m species responsible for FHB, " exact="F. graminearum" xpath="/*[local-name()='html'][1]/*[local-name()='body'][1]/*[local-name()='div'][1]/*[local-name()='div'][5]/*[local-name()='div'][2]/*[local-name()='p'][1]" match="F. graminearum" post=" is considered the most important, " name="binomial"/>
   ...
  </snippets>
 
```

## project aggregation

The project has 7 `ctree`s and the `*Files` and `*Snippets` files cane be combined into summary files as 
direct children of the`cProject`.

## `file` and `xpath` of `--analyze`

The `--analyze` flag currently takes one argument, `searchExpression`. This has the forms:

* `file(glob-expression)`
* `file(glob-expression)xpath(xpath-expression)`

### glob syntax

The `glob and `xpath` expressions follow the Java NIO file glob syntax and the W3C XPath 1.0 syntax 
(currently spaces can't be included). `file` selects files through wildcards. Thus:
```
**/species/**/results.{xml,html}`
```
looks for a file path which contains `species` and `results.xml` or `results.html`
```
The full syntax is in: https://docs.oracle.com/javase/7/docs/api/java/nio/file/FileSystem.html 
(section `getPathMatcher`)
  
### xpath

XPath navigates an XML document in a syntax derived from a filesystem. Example:
```
//result[contains(@pre,'genotype')]
````
finds all `<result>` elements anywhere in the document, also javing a "pre" attribute which contains the string
	"genotype"
```
<result pre="ly, the adult ALad1 lineage is labelled by the Cha7.4- Gal4 (a cholinergic neuron label) and " exact="GH146" post=" -Gal4 lines, while the adult LALv1 lineage is not ( Figure 1P , Table 1 ). " />
  <result pre="te otd −/− MARCM clones, females of the genotype FRT19A,otd " exact="YH13" post=" /FM7c or FRT19A,oc 2 /FM7c or FRT19A, otd &lt;s" />

````	
The first line is not matched, the seocnd is (contains "genotype")

Anotehr example from patents:
```
file(**/fulltext.xml)xpath(//description[heading[.='BACKGROUND']]/p[contains(.,'polymer')])
```
finds all BACKGROUND <description> sections of the `fulltext.xml` which contain"polymer"
	