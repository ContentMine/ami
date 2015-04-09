# `contentMine` directories and `results` files

The `ami` plugins all use a conventional directory structure, modelled on theoutput from `quickscrape`. This used to be called a `quickscrape` directory, then `quickscrapeNorma` and now simply `contemtmine` or `cmdir` . (These names are still in flux, sorry). The flags `-q` and `qsN`, etc will become obsolete.

## contentMine directory

The commonest type of `contentMine` directory comes from the result of a `quickscrape` scrape, typically like:
```
http_www.trialsjournal.com_content_16_1_1
├── 1745-6215-16-1-1.gif
├── fulltext.html
├── fulltext.pdf
├── fulltext.xml
├── results.json
└── scholarly.html
```
The article URL creates the `contentMine` directory name. Note, however, that there is no standard naming structure. 
 * `fulltext.html` is raw HTML (and may sometimes be difficult to parse)
 * `fulltext.pdf` is the current PDF (some publishers replace a provisional one with a final one)
 * `fulltext.xml` is the XML. There may be several DTDs used. We have encountered at least: 'nlm` ('JATS`); 'BMC`; `Elsevier`; `ISO12083` ans probably several more.
 * `results.json` a summary/manifest of rhe files and metadata
 * `*.gif`, etc. Images, supplemental data, etc. We may put this in subdirectories soon.
 
 None of these are perfect for analysing so we have to create normalised, structured documents (`scholarly.html`); This is done by `norma` applying an `XSL` stylesheet to the `XML`.
 
 The `contentMine` directory is used directly by `ami` plugins. The software knows the reserved files names that are allowed  in the director and how to process them. At present only the `scholarly.html` can be input to `ami`, but in the futire we shall also allow `figure*.svg` and other data types. We usually require both a `contentMine` directory and an `input` file such as:
 ```
 ami-regex -q http_www.trialsjournal.com_content_16_1_1 -i scholarly.html --r.regex regex/consort0.xml
 ```
 where we are applying `regex/consort0.xml` to the input file (`scholarly.html`) within a given `contentMine` directory. 
 
 Note that all the resources for a given article or other document are kept within a single directory whose structure is controlled.
 
 ## results directory

 All `ami` plugins generate their results in a special subdirectory `results`. A typical structure is:
 ```
└── 15_1_511_test
    ├── fulltext.html
    ├── fulltext.pdf
    ├── fulltext.xml
    ├── results
    │   └── regex
    │       └── consort0
    │           └── results.xml
    ├── results.json
    └── scholarly.html
 ```
Here `ami-regex` has generated a `results` directory, used its name for a subdirectory (`regex`) , made a sub-subdirectory for the option (the regex name `consort0`), and then written `results.xml` within it. 

When several plugins are used , each with one or more options, we can get a structure like:
```
└── contentminedir
    ├── fulltext.html
    ├── fulltext.pdf
    ├── fulltext.xml
    ├── results
    │   └── plugin1
    │       └── option11
    │           └── results.xml
    │       └── option12
    │           └── results.xml
    │   └── plugin2
    │       └── option21
    │           └── results.xml
    │       └── option22
    │           └── results.xml
    ├── results.json
    └── scholarly.html
```
This makes it easy to hold all the results in one place, which not only avoids the chanceof losing files, but also allows comparison and composiyion between different output. For example we could compare the variation of `sequences` with `species`

## `expected` directory

`expected` subdirectory provides regression tests which make sure that the same result is created by later versions of software. It also acts as an illustrator of the types of output. So
```
└── 15_1_511_test
    ├── expected
    │   └── regex
    │       └── consort0
    │           └── results.xml
    ├── fulltext.html
    ├── fulltext.pdf
    ├── fulltext.xml
    ├── results
    │   └── regex
    │       └── consort0
    │           └── results.xml
    ├── results.json
    └── scholarly.html
```
here `expected` has the same structure and content as `results`.

## `results.xml` files


All output is in `results.xml` which are structured as a `<results>` element with `<result>` children. 

### `ami-word` and `count`s

Here is `ami-word`:
```
<?xml version="1.0" encoding="UTF-8"?>
<results title="frequencies">
 <result title="frequency" word="smoking" count="71"/>
 <result title="frequency" word="smokers" count="23"/>
 <result title="frequency" word="quit" count="20"/>
 <result title="frequency" word="researcher" count="18"/>
 <result title="frequency" word="calls" count="17"/>
 <result title="frequency" word="letter" count="16"/>
 <result title="frequency" word="approaches" count="14"/>
 <result title="frequency" word="contact" count="14"/>
 <result title="frequency" word="single" count="13"/>
 <result title="frequency" word="invitations" count="13"/>
 <result title="frequency" word="likely" count="13"/>
 <result title="frequency" word="attempt" count="12"/>
 <result title="frequency" word="minutes" count="12"/>
 <result title="frequency" word="cut" count="12"/>
 <result title="frequency" word="sent" count="12"/>
 <result title="frequency" word="reach" count="12"/>
 <result title="frequency" word="nicotine" count="11"/>
```
The `results` element has a `title` corresponding to the `option` selected (the `frequencies`) .

However different plugins will give different information, currently as follows:

### `regex`

```
 <result pre="-specific LBP (NSLBP), a " name0="diagnose" value0="diagnosis" 
     post="based on exclusion of a specific cause o" xpath="/html[1]/body[1]/div[9]/p[1]"/>
 <result pre="tion health, to alter or " name0="diagnose" value0="diagnose" 
     post="the course of a health condition, or to " xpath="/html[1]/body[1]/div[10]/table[1]/tbody[1]/tr[2]/td[2]/p[1]"/>
 <result pre="re proposed to describe, " name0="stratify" value0="stratify," 
     post="and compare reports on patients with chr" xpath="/html[1]/body[1]/div[10]/div[4]/p[4]"/>
 <result pre="ble researchers were not " name0="exclude" value0="excluded" 
     post="from this list, convenience sampling was" xpath="/html[1]/body[1]/div[10]/div[7]/div[2]/p[1]"/>
     ...
```
Unlike word-counting, the regex (and manyn other plugins) locate a precise word or phrase in the document. It's very valuable to know where this is so we give two mechanisms:

### `xpath` locators

 * XPath can precisely define the element which provides the content for the match; the `xpath` value allows us to recover it.
 * the text before (`pre`) and after (`post`) the match can be used to find the actual position. This is very good for variable running text, but less good in tables. 
 
 For all matches of words and prases we provide `xpath`, `post` and `pre`
 
### names and matched values (`sequence` and `species`)

A regex can extract multiple name-value pairs  and hence we hace `name0` and `value0` (for some regexes we might have `name1` and `value1`, etc. This looks a bit clunky, but it seems necessary. For single matches, e.g. for `sequences` and `species` the result is often anonymous and we don't need an explicit `name` Here are both:
```
<?xml version="1.0" encoding="UTF-8"?>
<results title="dna">
 <result pre="used by Sweet et al, 2010; (357F) (" exact="5’-CCTACGGGAGGCAGCAG-3’" 
     post=") and (518R) (5’ATTACCGCGGCTGCTGG-3’), a segment o" xpath="/html[1]/body[1]/div[1]/div[5]/div[3]/p[1]" name="dna"/>
 <result pre="-CCTACGGGAGGCAGCAG-3’) and (518R) (" exact="5’ATTACCGCGGCTGCTGG-3’" 
     post="), a segment of the bacterial 16S rRNA gene was am" xpath="/html[1]/body[1]/div[1]/div[5]/div[3]/p[1]" name="dna"/>
 <result pre="ythell [ 18]; forward primer CilF (" exact="5’-TGGTAGTGTATTGGACWACCA-3’" 
     post=") with a 36 bp GC clamp [ 31] attached to the 5’ e" xpath="/html[1]/body[1]/div[1]/div[5]/div[4]/p[1]" name="dna"/>
 <result pre=" and the reverse primer CilDGGE-r (" exact="5’-TGAAAACATCCTTGGCAACTG-3’" 
     post="). PCR reaction mixtures were made up to 10 μl vol" xpath="/html[1]/body[1]/div[1]/div[5]/div[4]/p[1]" name="dna"/>
 <result pre="mples using the primers BrB-F-171 (" exact="5’-TCAAACCCGACTTTACGGAAG-3’" 
     post=") and BrB-R-1721 (5’-TGCAGGTTCACCTACGGAAAC-3’) [ 3" xpath="/html[1]/body[1]/div[1]/div[5]/div[4]/p[3]" name="dna"/>
 <result pre="CGACTTTACGGAAG-3’) and BrB-R-1721 (" exact="5’-TGCAGGTTCACCTACGGAAAC-3’" 
     post=") [ 34]. These primers were designed to amplify th" xpath="/html[1]/body[1]/div[1]/div[5]/div[4]/p[3]" name="dna"/>
</results>
```
Note the `pre` and `post` and `xpath` as before. `exact` is the exact sequence matched - and there is no name. 

The same is true for species:
```
<results title="binomial">
 <result pre="ntimicrobial activity (assessed on " exact="Vibrio harveyi" match="Vibrio harveyi" 
     post=" cultures) was limited in both H and WSU samples (" name="binomial"/>
 <result pre="ia genus Vibrio, including; " exact="V. harveyi" match="Vibrio harveyi" 
     post=" [ 14], V. mediterranei [ 7], V. owensii" name="binomial"/>
 <result pre="ncluding; V. harveyi [ 14], " exact="V. mediterranei" match="Vibrio mediterranei" 
     post=" [ 7], V. owensii [ 15] and V. coralliil" name="binomial"/>
 <result pre=" 14], V. mediterranei [ 7], " exact="V. owensii" match="Vibrio owensii" 
     post=" [ 15] and V. coralliilyticus [ 10]. Furthe" name="binomial"/>
```
Here we keep the `exact` but add an additional field `match` which is the "translation" of the exact match to the semantic form. As before the result is formally anonymous.

### `identifier`

Finally we have identifiers, which are really just a set of regexes, withe possibility of being looked up.
```
<results title="clin.isrctn">
 <result pre="andomized Controlled Trial Number (" exact="ISRCTN): 13837944" 
     post=", UK Clinical Research Network (UKCRN) Study ID: 8" xpath="/html[1]/body[1]/div[8]/p[4]" name="clin.isrctn"/>
</results>
```
The id scheme is encapsulated in the `title` and again we have an anonymous value.




 