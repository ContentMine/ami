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
 