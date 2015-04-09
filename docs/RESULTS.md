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

 
 