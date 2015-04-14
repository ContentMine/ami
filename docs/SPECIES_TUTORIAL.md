# Species Tutorial

## Species search

We start with 10 CM directories (each with a `scholarly.html` file) from PLoS (see above) in `target/species10`. Now we'll apply the Species searcher.

```
ami2-species -q target/species10 -i scholarly.html --sp.species --context 35 50 --sp.type binomial genus genussp
```

 * `ami2-species` indicates which plugin to use.
 * `--sp.species` is required, but will be removed in future versions
 * `--context 35 50` defines the number of characters before and after the match
 * `--sp.type binomial genus genussp` the types to search for: binomial is "Parus ater", genus is "Parus" genussp is "Parus sp"
 
This takes a few seconds. The output looks like:
```
localhost:target pm286$ tree species10/
species10/
├── e0115544
│   ├── fulltext.xml
│   ├── results
│   │   └── species
│   │       ├── binomial
│   │       │   └── results.xml
│   │       ├── genus
│   │       │   └── results.xml
│   │       └── genussp
│   │           └── results.xml
│   └── scholarly.html
├── e0116215
│   ├── fulltext.xml
│   ├── results
│   │   └── species
│   │       ├── binomial
│   │       │   └── results.xml
│   │       ├── genus
│   │       │   └── results.xml
│   │       └── genussp
│   │           └── results.xml
│   └── scholarly.html
... and 8 more
```
The results are all in `results.xml` - Here's the full list:

```
localhost:species10 pm286$ ls -lt */*/*/*/results.xml | more
-rw-r--r--  1 pm286  staff   747 12 Apr 18:49 e0118685/results/species/binomial/results.xml
-rw-r--r--  1 pm286  staff    66 12 Apr 18:49 e0118685/results/species/genus/results.xml
...
-rw-r--r--  1 pm286  staff    68 12 Apr 18:49 e0118757/results/species/genussp/results.xml
-rw-r--r--  1 pm286  staff   726 12 Apr 18:49 e0118792/results/species/binomial/results.xml
-rw-r--r--  1 pm286  staff    66 12 Apr 18:49 e0118792/results/species/genus/results.xml
-rw-r--r--  1 pm286  staff    68 12 Apr 18:49 e0118792/results/species/genussp/results.xml
-rw-r--r--  1 pm286  staff  5792 12 Apr 18:49 e0119090/results/species/binomial/results.xml
-rw-r--r--  1 pm286  staff    66 12 Apr 18:49 e0119090/results/species/genus/results.xml
...
-rw-r--r--  1 pm286  staff    68 12 Apr 18:49 e0115544/results/species/genussp/results.xml
-rw-r--r--  1 pm286  staff  9807 12 Apr 18:49 e0116215/results/species/binomial/results.xml
-rw-r--r--  1 pm286  staff  1561 12 Apr 18:49 e0116215/results/species/genus/results.xml
-rw-r--r--  1 pm286  staff    68 12 Apr 18:49 e0116215/results/species/genussp/results.xml
..
-rw-r--r--  1 pm286  staff    68 12 Apr 18:49 e0116596/results/species/genussp/results.xml
-rw-r--r--  1 pm286  staff   425 12 Apr 18:49 e0116903/results/species/binomial/results.xml
-rw-r--r--  1 pm286  staff   232 12 Apr 18:49 e0116903/results/species/genus/results.xml
-rw-r--r--  1 pm286  staff    68 12 Apr 18:49 e0116903/results/species/genussp/results.xml
..
-rw-r--r--  1 pm286  staff    68 12 Apr 18:49 e0117956/results/species/genussp/results.xml
-rw-r--r--  1 pm286  staff   242 12 Apr 18:49 e0118659/results/species/binomial/results.xml
``` 
The small files (~66) are stubs with no results (but it's useful to keep them to show we got no results). The larger ones
have lots of species:

Let's look at some: 

```
e0118685/results/species/binomial/results.xml:
<results title="binomial">
 <result pre="up&gt; and IL-13 −/− mice. " exact="In vitro" match="In vitro" post=" stimulation of splenocytes with PMA + ionomycin i" name="binomial"/>
 <result pre="on with the intracellular parasite " exact="Toxoplasma gondii" match="Toxoplasma gondii" post=", likely as a consequence of reduced levels of CD8" name="binomial"/>
 <result pre="produce comparable levels of IL-4. " exact="In vivo" match="In vivo" post=", challenge of IL-13 −/− mice with schi" name="binomial"/>
 <result pre="hereas challenge with the parasite " exact="N. brasiliensis" match="N. brasiliensis" post=" had no impact on Th2 response generation or IL-4 " name="binomial"/>
</results>
```
Here we have two genuine species (Toxoplasma gondii and N. brasiliensis) and also 2 *false positives* (In vivo and In vitro). Here's a more densely populated one, `e0119090/results/species/binomial/results.xml`.
```
<results title="binomial">
 <result pre=" " exact="Cryptococcus neoformans" match="Cryptococcus neoformans" post=" is a ubiquitous environmental fungus that can cau" name="binomial"/>
 <result pre="ases are risk factors for invasive " exact="C. neoformans" match="Cryptococcus neoformans" post=" diseases. " name="binomial"/>
 <result pre=" " exact="Cryptococcus neoformans" match="Cryptococcus neoformans" post=" is a pathogenic fungus that causes life-threateni" name="binomial"/>
 <result pre="f which was reported in 1905 [ 1]. " exact="C. neoformans" match="Cryptococcus neoformans" post=" is ubiquitous [ 2]. The Centers for Disease Contr" name="binomial"/>
 <result pre="urring in sub-Saharan Africa [ 3]. " exact="C. neoformans" match="Cryptococcus neoformans" post=" infection is therefore an important global health" name="binomial"/>
```
and there are many more, all of the same species.

and just one more: (e0116215/results/species/binomial/results.xml)
```
 <result pre="alysis revealed that the genome of " exact="Klebsiella pneumoniae" match="Klebsiella pneumoniae" post=" LM21 harbors eight chromosomal CU loci belonging " name="binomial"/>
 <result pre="Isogenic usher deletion mutants of " exact="K. pneumoniae" match="Klebsiella pneumoniae" post=" LM21 were constructed for each locus and their ro" name="binomial"/>
 <result pre="animal (Intestine 407) and plant ( " exact="Arabidopsis thaliana" match="Arabidopsis thaliana" post=") cells, biofilm formation and murine intestinal c" name="binomial"/>```
```
Here we have added a heuristic to replace Abbreviations (K.) with the genus.

