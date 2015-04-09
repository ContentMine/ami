#!/bin/sh

# archetypal demos/regression tests for ami-plugins

# clean old directories
rm -rf temp*

echo identifiers
cp -R http_www.trialsjournal.com_content_16_1_1/ temp0
../../../../../../../target/appassembler/bin/ami2-identifier -q temp0 -i scholarly.html --context 25 40 --id.identifier --id.type clin.nct clin.isrctn

echo regex
cp -R bmc_trials_15_1_511/ temp1
../../../../../../../target/appassembler/bin/ami2-regex -q temp1 -i scholarly.html --context 25 40 --r.regex regex/consort0.xml

echo sequence
cp -R journal.pone.0121780/ temp2
../../../../../../../target/appassembler/bin/ami2-sequence --sq.sequence --context 35 50 --sq.type dna prot -q temp2 -i scholarly.html

echo species
cp -R journal.pone.0119475/ temp3
../../../../../../../target/appassembler/bin/ami2-species --sp.species --context 35 50 --sp.type binomial genus genussp -q temp3 -i scholarly.html

echo word
cp -R http_www.trialsjournal.com_content_16_1_1/ temp4
../../../../../../../target/appassembler/bin/ami2-word -q temp4 -i scholarly.html --context 25 40 --w.words wordLengths wordFrequencies --w.stopwords /org/xmlcml/ami2/plugins/word/stopwords.txt

