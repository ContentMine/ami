# Writing a plugin

To write a plugin you should be familiar with the Java language. The most reliable way is to clone the basic `org.xmlcml.ami2.plugins.simple`
class and associated files and edit them. Later we'll create a simple Plugin template-generator. We'll assume your Plugin is called `Foo`, which replaces 
`Simple` so that
 * `Simple` will be replaced by `Foo`
 * `SimplePlugin` will be replaced by `FooPlugin`
 * `SimpleArgProcessor` will be replaced by `FooArgProcessor`
 * `org.xmlcml.ami2.plugins.simple`  will be replaced by `org.xmlcml.ami2.plugins.foo`
 * 



There are 4 main subdirectory trees:

## `src/main/java`

This is the source code and MUST contain the sections `FooPlugin` and `FooArgProcessor`. 

It MAY contain some/all of `FooResultElement.java` and `FooResultsElement.java` if there is specific processing of the per-document results and and `FooResultsElementList.java` if there is summarising or gathering of results. 

It MAY contain of `FooSearcher.java` if there is a non-trivial search (i.e. more complex than a simple regex. 

It MAY also contain plugin-specific engines such as translators, 
property calculators etc and these should be distinct from `FooArgProcessor` as much as possible. Often the domain logic will be in a 
separate package or even a server.

### `src/main/java/org/xmlcml/ami2/plugins/foo/FooPlugin.java` Mandatory

Currently very small and only serves to route the arguments to `FooArgProcessor.java` and to establish the class  
`FooPlugin.main(String[])` as entry point.

### `src/main/java/org/xmlcml/ami2/plugins/foo/FooArgProcessor.java` Mandatory

This contains all the potential calls from commandline operation, implemented through `args.xml`. For example a command option `--bar` 
can be linked through `args.xml` to the method `public void doBar()` which must then be implemented in `src/main/java`. In some cases 
`doBar()` will simply set instance variables; other times it will initiate serious processing.

## `src/main/resources` Optional

This contains (static) resources for the Plugin, normally computed at startup, the most common being `args.xml`.

### `src/main/resources/org/xmlcml/ami2/plugins/foo/args.xml`

Mandatory if there are any Plugin-specific commands/options. Some options (e.g. `--input`) are inherited from `/org/xmlcml/cmine/args/args.xml` (processed by `org.xmlcml.cmine.args.DefaultArgProcessor.java` ) and others (e.g. `--context`)  inherit from  `/org/xmlcml/ami2/plugins/args.xml` (processed by `org.xmlcml.ami2.plugins.AmiArgProcessor.java`).   Each command requires an `<arg>` in `args.xml`.

## `src/test/java` Mandatory

This contains unit tests for the Plugin. These not only validate the code but also act as examples of how to run in. Some methods are marked as `// SHOWCASE` which means they exemplify the major methods of calling programmatically and running from the commandline.

## `src/test/resources`

Data files for unit tests.
