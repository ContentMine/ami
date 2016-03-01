# AMI

[2015-09-23: This is the current active repository, but will move to https://github.com/ContentMine/ami/ in the near future ].

AMI provides a generic infrastructure where plugins can search, index or transform structured documents on a high-through basis. The typical input is structured, normalized, tagged XHTML, possibly containing (or linked to) SVG and PNG files. The plugins are designed to analyse text or graphics or a combination according to the discipline. 

## Historical note and obsoletion

AMI has been through 2 major revisions, and most recently has been split into two parts (a) ``Norma`` which processes legacy documents and normalizes HTML (NHTML) and (b) AMI which runs plugins over the NHTML. AMI currently processes PDF, XML, HTML, etc but these will be obsoleted in favour of the output from ``Norma``. 

## Building
```
mvn package
```

After the build, shell scripts and batch files to run plugins are in `target/appassembler/bin/`.  Further documentation is in `docs/`.

## Plugins

AMI has a plugin architecture where each problem or community has its own plugin. Examples are "species", "sequence", "regex" and soon some chemistry.

It is often straightforward to develop text-based searches, and this is accessible to most committed scientists. Graphics is always harder and requires bespoke programming. Plugins have been developed for at least:

### text targets

 * indexing text by regular expressions (``regex``).
 * Genbank IDs
 * PDB ids
 * farm-related / agronomy terms
 * chemical species (OSCAR)
 * computational phylogenetics
 * terms identifying Ebola and other haemorrhagic diseases
 
### graphical targets

 * phylogenetic trees 
 * chemical structures
 
The plugin architecture is moderately stable and it requires very little alteration to the codebase to add a new one (hopefully soon this can be done automatically by configuration files).

### file structure

The input must be a QuickscrapeNorma directory (QSNorma). This must contain `scholarly.html` which is used for 
analysis. When a plugin is run, the output is to the `results` directory, wiwth a subdirectory for each plugin
and a sub-subdirectory for each plugin option: Example:


    http_www.foo_1_2/
        fulltext.xml
        fulltext.pdf
        scholarly.html
        results/
            words/
                frequency/
                    results.xml
                lengths/
                    results.xml
            regex/
                consort0/
                    results.xml
                publication/
                    results.xml


        

