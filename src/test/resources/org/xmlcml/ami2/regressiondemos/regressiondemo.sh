#!/bin/sh
# archetypal demos/regression tests for ami-plugins

cp -R regressiondemos/http_www.trialsjournal.com_content_16_1_1/ temp
ami-identifier -q target/examples_16_1_1/ -i scholarly.html --context 25 40 --id.identifier --id.type clin.nct clin.isrctn"

cp -R regressiondemos/bmc_trials_15_1_511/ temp
ami-regex -q target/consort0/15_1_511_test/ -i scholarly.html --context 25 40 --r.regex regex/consort0.xml

cp -R regressiondemos/journal.pone.0121780/ temp
ami-sequence --sq.sequence --context 35 50 --sq.type dna prot -q target/plosone/sequences/ -i scholarly.html

cp -R regressiondemos/journal.pone.0119475/ temp
ami-species --sp.species --context 35 50 --sp.type binomial genus genussp -q target/plosone/species/malaria -i scholarly.html

cp -R regressiondemos/http_www.trialsjournal.com_content_16_1_1/ temp
ami-word -q temp -i scholarly.html --context 25 40 --w.words wordLengths wordFrequencies --w.stopwords /org/xmlcml/ami2/plugins/word/stopwords.txt

