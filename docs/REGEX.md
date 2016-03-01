# regex


## regex plugin

There are many plugins but we expect the Regex  Plugin to be one of the most popular. Here we create a set of expressions (terms) that we wish to search for. Here we use a small subset of the terms used in clinical trials (`consort0`):

## typical file
Here are a few from the file. Comments are inserted after each line...
```
<compoundRegex title="consort0">
```
There can be many `<regex>`es, and all are bracketed in `<compoundRegex>` ... `</compoundRegex>`. The `title0` is used to annotate the output (e.g. naming the output directory.
```
  <regex fields="stata">Stata</regex>
```
This is the simplest example. There must be an exact match with the string `Stata`. Note that this is case-sensitive by default.
```
  <regex fields="samplesize">sample size</regex>
```
This is a case-sensitive two-word phrase. At present there must be exactly one space in the target but we are working towards making it more general.
```
  <regex fields="followup">follow\-?up</regex>
```
This matches `follow-up` or `followup`. The `?` means optional. Because `-` has a special meaning, we "escape" it by a preceding backslash.
```
  <regex fields="exclude">exclud~</regex>
```
This is a special ContentMine extension. `~` is a common lexicographical device which means "any suffix", so `excludes`, `excluding`, `exclude` but not `exclusion`. [Note 1]
```
  <regex fields="withdraw">withdr[ae]w~</regex>
```
The `[xyz]` construct is common and means either x or y or z. Here it means "a" or "e" followed by "w" and then any suffix, so manages `withdrawal` `withdrew` , `withdrawing`.
```
  <regex fields="random">[Rr]andom~</regex>
```
A combination of `Random` and `random` followed by an optional suffix.
```
</compoundRegex>
```
Regexes can get very complex and unreadable, and they are also modified by the system so you are well advised to start with exact words, test wherever possible and only add new constructs when necessary. If you aren't sure, put in all the optionas explicitly , e.g. :
```
  <regex fields="random">random</regex>
  <regex fields="random">randomization)</regex>
  <regex fields="random">randomisation)</regex>
  <regex fields="random">randomised)</regex>
```


## running the examples

The examples can be run from the commandline in the Debian environment 

```
ami2-regex -q examples/http_www.trialsjournal.com_content_16_1_1 \
		-i scholarly.html \
		-o results.xml \
		--context 25 40 \
		--r.regex regex/consort0.xml
      
```

This tells the `ami2-regex` plugin to search a given directory and to search the `scholarly.html`. `--context` gives the textual context of the results - 25 characters on the left and 40 on the right. `--r.regex` selectes the regex file to use. The output (which can be configured) here has the default:

```
 WROTE examples/http_www.trialsjournal.com_content_16_1_1/results/regex/consort0/results.xml
```

## output

The Regex Plugin has searched the whole visible content of the file (including the abstract, references and publisher material).  The file ``results.xml`` looks something like:

```
<results title="consort0">
 <result pre=") database (31 participants). Six other " name0="participants" value0="participants" post="were recruited via a variety of other co"/>
 <result pre="ctive than those who were recruited via " name0="follow-up" value0="follow-up" post="telephone calls. No other demographics o"/>
 <result pre="on time and intensity invested.Only six " name0="participants" value0="participants" post="were recruited through a wide variety of"/>
 <result pre="rt. Qualitative feedback indicated that " name0="participants" value0="participants" post="had been attracted by the prospect of su"/>
 <result pre="nvite the target population, and use of " name0="follow-up" value0="follow-up" post="telephone calls to explain the study met"/>
```


It's found many examples of the word "participants" and here reports both the complete context (`flower`) with the exact match (`value0`) and the `pre` and `post` strings. This looks a little verbose, but the name could relate to a number of values and there could be several names (`name1`, `name2` ...) if there were several cpature groups (brackets)

## xpath

`ami` components are able to locate themselves within an XML or HTML document and generate the `xpath` which would point to their containing element (often a `html:p` element). Here's an example from a regex search:

```
<?xml version="1.0" encoding="UTF-8"?>
<results title="consort0">
 <result pre="-specific LBP (NSLBP), a " name0="diagnose" value0="diagnosis" post="based on exclusion of a specific cause o" 
     xpath="/html[1]/body[1]/div[9]/p[1]"/>
 <result pre="tion health, to alter or " name0="diagnose" value0="diagnose" post="the course of a health condition, or to " 
     xpath="/html[1]/body[1]/div[10]/table[1]/tbody[1]/tr[2]/td[2]/p[1]"/>
 <result pre="re proposed to describe, " name0="stratify" value0="stratify," post="and compare reports on patients with chr" 
     xpath="/html[1]/body[1]/div[10]/div[4]/p[4]"/>
...
</results>
```
Read the last as "the 4th `<p>` child of the 4th `<div>` child of the 10th `<div>` child of the 1st `<body>` of the 1st `<html>` element."


## restriction to named sections

NOT YET IMPLEMENTED

There is a very powerful language, XPath, for finding parts of XML documents. Here we restrict it to just those sections which we have tagged  (we've abbreviated ``input`` and ``regex`` to single-character mnemonics)
```
regex -i src/test/resources/org/xmlcml/ami/plosone/journal.pone.0113556.tagged.xml \
      -r regex/agriculture.xml \
      -x //*[@tag='abstract']
```

The XPath expression says "search for any section (``*``) which has a ``tag`` with the value ``abstract``". Norma, who knows how to read PLoSONE articles, has already tagged the Abstract so we don't have to worry.  Now the search will only search the abstract. The result is now only two hits and we know where they came from.

Similarly if we want Figures whose captions refer to flowers we can write:
```
      -x //*[@tag='figure']
```

Since XPath is a rich language we can also restrict searches, such as 
```
      -x //*[@tag and not(@tag='references')]
```

Read "search all tagged sections except the References". 

## enhancements

We will shortly add XPath expressions showing where any hit comes form (this is already done in the Species Plugin).

## Notes

Note1. (For the more advanced, it is a macro expanding to `(?:[^\\\\s]*\\\\p{Punct}?)` - i.e. non-capturing group of zero-or-more non-space characters followed by an optional punctuation character. This may be refined later).


