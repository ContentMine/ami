#!/bin/bash

cd
cd workspace/cmdev

cd euclid
mvn clean install
cd ..

cd svg
mvn clean install
cd ..

cd html
mvn clean install
cd ..

cd imageanalysis
mvn clean install
cd ..

cd pdf2svg
mvn clean install
cd ..

cd svg2xml
mvn clean install
cd ..

cd cproject
mvn clean install
cd ..

cd norma
mvn clean install
cd ..

cd diagramanalyzer
mvn clean install
cd ..

cd ami
mvn clean install
cd ..


