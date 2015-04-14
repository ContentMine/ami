# Regex and identifier tutorial

Regular expressions are at the heart of several `ami` plugins: `species`, `sequence`, `identifier` and `regex`. The first two are packaged so you don't need to see the regexes and here we'll look at `identifier` and `regex`.

You should be familiar with how to run `ami-species`. Regex uses very similar commands.

## running `ami2-regex`

`ami-regex` reads in 1 or more regexes from files, or URLs. The typical format is

```
ami2-regex -q workshop/02_ami/plos_one_latest_10 -i scholarly.html --context 25 40 --r.regex workshop/02_ami/regex/consort0.xml
```
Here:
 *  `-q` is the location of the CM directories to be searched (as before).
 * `-i scholarly.html` uses the scholarly HTML
 * `--context` again sets the character environment
 * `--r.regex` gives the regex files to read from. Here we are using one developed for clinical trials. It's only got a few words (to make it friendly for a tutorial but already it's very useful)

 After running it we have:
 ```
 localhost:regex10 pm286$ ls -lt */*/*/*/results.xml | more
-rw-r--r--  1 pm286  staff   860 12 Apr 20:39 e0118692/results/regex/consort0/results.xml
-rw-r--r--  1 pm286  staff    69 12 Apr 20:39 e0118757/results/regex/consort0/results.xml
-rw-r--r--  1 pm286  staff   685 12 Apr 20:39 e0118792/results/regex/consort0/results.xml
-rw-r--r--  1 pm286  staff  2794 12 Apr 20:39 e0119090/results/regex/consort0/results.xml
-rw-r--r--  1 pm286  staff  1857 12 Apr 20:39 e0118659/results/regex/consort0/results.xml
-rw-r--r--  1 pm286  staff   259 12 Apr 20:39 e0118685/results/regex/consort0/results.xml
-rw-r--r--  1 pm286  staff   485 12 Apr 20:39 e0116215/results/regex/consort0/results.xml
-rw-r--r--  1 pm286  staff   479 12 Apr 20:39 e0116596/results/regex/consort0/results.xml
-rw-r--r--  1 pm286  staff   273 12 Apr 20:39 e0116903/results/regex/consort0/results.xml
-rw-r--r--  1 pm286  staff    69 12 Apr 20:39 e0117956/results/regex/consort0/results.xml
-rw-r--r--  1 pm286  staff  2499 12 Apr 20:39 e0115544/results/regex/consort0/results.xml
```
If we look at 0119090 online - http://journals.plos.org/plosone/article?id=10.1371/journal.pone.0119090 - we'll see it's a case-study trial! and also http://journals.plos.org/plosone/article?id=10.1371/journal.pone.00115544

### CONSORT regexes

We've only included 12 regexes. 
```
<compoundRegex title="consort0">
  <regex fields="random">([Rr]andom~)</regex>
  <regex fields="blind">[Bb]lind~</regex>
  <regex fields="followup">follow\-?up</regex>
  <regex fields="exclude">exclud~</regex>
  <regex fields="withdraw">withdr[ae]w~</regex>
  <regex fields="diagnose">(diagnos~)</regex>
  <regex fields="attrition">attrition~</regex>
  <regex fields="samplesize">sample size</regex>
  <regex fields="placebo">placebo~</regex>
  <regex fields="stata">Stata</regex>
  <regex fields="stratify">stratif~</regex>
  <regex fields="allocate">allocat~</regex>
</compoundRegex>
```
Each regex has a `fields` attribute - think of it as a unique name - and a `value` within `>...</regex>`. The value is the regex or pre-regex (something that can be truned into a regex). (The pre-regex is to make it simply for the tutorial). The simplest regex is:
```
Stata
```
which requires an exact match with "Stata" inlcuding the uppercase "S". The next simplest is:
```
sample size
```
which must also be matched exactly. Beyond that we get lexical variants and stemming (`~`) to allow for plurals, tenses, etc. In some cases we cater for sentence starts (`[Rr]` means "R" or "r"). Since both "follow-up" and "followup" might be used we have an optional hyphen (`?`).

Most of the words can have suffix variants. We indicate this by a tilde which we convert into a regex automatically (it's a bit frightening at first).

There's no reason why more than regex should not be used. Try

```
ami2-regex -q workshop/02_ami/plos_one_latest_10 -i scholarly.html --context 25 40 --r.regex workshop/02_ami/regex/consort0.xml  workshop/02_ami/regex/publication.xml
```

## identifiers

These are based on regexes, here in `regex/identifiers.xml`. (There could be multiple files, especially when *you* write some!). Many identifiers are based on strict rules and most can be cast into regex form. Here's the contents - don't be frightened 
```
<compoundRegex title="misc">
	<regex fields="bio.ena" url="http://www.ncbi.nlm.nih.gov/Sequin/acc.html" 
	    lookup="http://www.ncbi.nlm.nih.gov/entrez/eutils/esearch.fcgi?db=gene&amp;term=${match}GENBANK_ID" >[A-Z]{2}\d{6}|[A-Z]{1}\d{5}</regex>
	<regex fields="bio.enaprot" url="http://www.ncbi.nlm.nih.gov/Sequin/acc.html">[A-Z]{3}\d{5}</regex>
	<regex fields="bio.pdb">\w[A-Z]{3}\d</regex>
	<regex fields="meta.orcid" url="http://support.orcid.org/knowledgebase/articles/116780-structure-of-the-orcid-identifier">http://orcid\.org/(\d{4}-){3}\d{3}[\dX]</regex>
	<regex fields="clin.nct">NCT\d{8}</regex>
	<regex fields="clin.isrctn">ISRCTN.{0,20}\d{8}</regex>
</compoundRegex>
```

### ENA genbank

We'll take genome sequences as an example. The format for European Nucleotide Archive (ENA) (aka GenBank) is:
```
two capital letters followed by 6 digits
```
regex encodes this as
```
[A-Z]{2}\d{6}
```
This shorthand is useful for power-users but confusing for beginners, but there are enough people around who know...

the query looks like:
```
ami2-identifier -q workshop/02_ami/plos_one_latest_10 -i scholarly.html --context 25 40 --id.identifier --id.regex workshop/02_ami/regex/identifiers.xml --id.type bio.ena
```
Most of this is familiar. The ` --id.regex workshop/02_ami/regex/identifiers.xml` uses the file workshop/02_ami/regex/identifiers.xml (you may need to add the directory) and `--id.type bio.ena` uses only the `bio.ena` regex in there.

Results:
```
localhost:ident10 pm286$ ls -lt */*/*/*/results.xml
-rw-r--r--  1 pm286  staff  19816 12 Apr 21:36 e0116215/results/identifier/bio.ena/results.xml
-rw-r--r--  1 pm286  staff     68 12 Apr 21:36 e0116596/results/identifier/bio.ena/results.xml
-rw-r--r--  1 pm286  staff     68 12 Apr 21:36 e0116903/results/identifier/bio.ena/results.xml
-rw-r--r--  1 pm286  staff     68 12 Apr 21:36 e0117956/results/identifier/bio.ena/results.xml
-rw-r--r--  1 pm286  staff     68 12 Apr 21:36 e0118659/results/identifier/bio.ena/results.xml
-rw-r--r--  1 pm286  staff     68 12 Apr 21:36 e0118685/results/identifier/bio.ena/results.xml
-rw-r--r--  1 pm286  staff     68 12 Apr 21:36 e0118692/results/identifier/bio.ena/results.xml
-rw-r--r--  1 pm286  staff     68 12 Apr 21:36 e0118757/results/identifier/bio.ena/results.xml
-rw-r--r--  1 pm286  staff    444 12 Apr 21:36 e0118792/results/identifier/bio.ena/results.xml
-rw-r--r--  1 pm286  staff     68 12 Apr 21:36 e0119090/results/identifier/bio.ena/results.xml
-rw-r--r--  1 pm286  staff     68 12 Apr 21:36 e0115544/results/identifier/bio.ena/results.xml
```
looks like `e0116215` might be interesting!

### clinical trials

Use the clinical trial identifiers (NCT and ISCRTN) to see if any are included:
```
ami2-identifier -q workshop/02_ami/plos_one_latest_10 -i scholarly.html --context 25 40 --id.identifier --id.regex workshop/02_ami/regex/identifiers.xml --id.type clin.nct clin.iscrtn
```

## BagOfWords

A collection of words with frequency of occurrence. We remove common english words ("stopwords"). You can remove others with the
`w.stopwords` flag.

```
ami2-word -q workshop/02_ami/plos_one_latest_10 -i scholarly.html --context 35 50 --w.words wordFrequencies --w.stopwords /org/xmlcml/ami2/plugins/word/stopwords.txt

```
The results are not only in `results.xml` but also `results.html` where we have scaled the font size to represent frequency. 


