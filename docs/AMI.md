# AMI Tutorial

## Quickstart

`ami` generally runs as 

a `<plugin>` with some `<pluginOptions>` reading some `<input>` and creating some `<results>`. A typical example is:
```
ami2-regex -q temp1 -i scholarly.html --context 25 40 --r.regex regex/phylotree.xml
``` 
This runs the `regex` plugin on the input file `scholarly.html` in the `temp1` contentMine directory using the option `phylotree` (a collection of regular expressions). The results will be in `results/regex/phylotree/results.xml`. The results are surrounded by 25 prefix characters and 40 postfix characters of context. (Arguments can be in any order). That's all there is to running `ami`.

However if you want to modify `ami` or write a new plugin, you need to know the organization...

## Overview 

AMI is highly modular and consists of Java code, XML control, data and tests. 

 * Unless you are writing a plugin you don't need to know about the code. 
 * The XML (`args.xml`) controls what options each plugin has and how the system decides what to do and when
 * the data suports the plugin (e.g. `stopwords.txt` for `ami-word`)
 * the tests help development, but also check the correctness of the code and make sure that bugs don't creep in ("regression"). Tests are also useful in showing some of the options - e.g. for tutorials.
 
 AMI is built as a `maven` framework. This shouldn't worry you - just accept that many of the filenames are conventional. Here's the current structure:
 
``` .
├── LICENSE
├── README.md
├── docs
```
`docs` contains documentation in `*.md` files (markdown) 
```
│   ├── AMI.md
│   ├── COCHRANE_20150316.md
│   ├── PLUGINS.md
│   ├── REGEX.md
│   ├── SEARCHING.md
│   ├── WORDS.md
...
```
Then a directory with some example files (here clinical trials).
```
├── examples
│   ├── http_www.trialsjournal.com_content_16_1_1
│   ├── http_www.trialsjournal.com_content_16_1_11
...
│   ├── http_www.trialsjournal.com_content_16_1_2
│   └── http_www.trialsjournal.com_content_16_1_3
```
`pom.xml` is a magic file that tells  Maven how to build the system automatically. It may need tweaking if we add a plugin.
```
├── pom.xml
```
There's a communal directory of CompoundRegex files (for use by `ami-regex`) and we'd expect people to edit this or add new ones.
```
├── regex
│   ├── common.xml
│   ├── consort0.xml
│   ├── figure.xml
│   ├── phylotree.xml
│   └── publication.xml
```
`src` contains the source code and supporting files
```
├── src
```
`deb` contains the file to control the `*.deb` creation.
```
│   ├── deb
```
`main` is for building the deployable system (`*.jar`, `*.deb`, etc)
```
│   ├── main
```
`test` is for testing and does not get distributed
```
│   └── test
```
`target` is temporary. It is deleted (`clean`ed) when the system is rebuilt. It contains the latest distributables and also a range of temporary files.
```
└── target
```
`target` contains the distributable executable files (these change with every build).
```
    ├── ami2-0.1-SNAPSHOT-bin.tar.gz
    ├── ami2-0.1-SNAPSHOT-bin.zip
    ├── ami2-0.1-SNAPSHOT.jar
    ├── ami2_0.1~SNAPSHOT_all.changes
    ├── ami2_0.1~SNAPSHOT_all.deb
    ├── appassembler
```
The rest only matters to a Java programmer.    
 
## the `src` trees

The overall structure is:
```
src
├── deb
│   └── control
├── main
│   ├── assembly
│   ├── java
│   └── resources
└── test
    ├── java
    └── resources
```

If you are tweaking a plugin you may need to know some of this
```
src
```
`main` is for the deployed system
```
├── main
```
it contains the source code. You shouldn't have to deal with it unless you create a plugin ...
```
│   ├── java
```
... and configuration and modification of the common data for the program (i.e. independent of a particular problem). 
```
│   └── resources
```
`test` is valuable for exploring what the program does. Also any new functionality should be test-driven (TDD) where possible. 
```
└── test
```
In an ideal world every `src/main/java` class should have a set of tests in `src/test/java`
```
    ├── java
```
and `resources` holds any data the tests need.
```    
    └── resources
```
 


