# Phylotree tutorial

NOTE: This is still PRE-ALPHA

## Pixel input

There are two separate parts:

(Later we shall combine these, including removing trees before analysing text and vice versa).

### Tree analysis

org.xmlcml.diagrams.phylo.PhyloTreePixelAnalyzer (maybe wrapped in AMI) is run over the raw pixel diagram to create a tree, normally from the largest PixelIsland. (This needs strategy to deal with 2 or more trees and also scale bar). The output is `/ami2/src/test/resources/org/xmlcml/ami2/phylo/ijs_0_000364_0/image/003.pbm.png.phylotree.svg`. These are then converted to to `/ami2/src/test/resources/org/xmlcml/ami2/phylo/ijs_0_000364_0/image/003.pbm.png.phylotree.nexml` and `/ami2/src/test/resources/org/xmlcml/ami2/phylo/ijs_0_000364_0/image/003.pbm.png.phylotree.nwk` which are tree templtea


### Text analysis

Tesseract is used to extract text (`/ami2/src/test/resources/org/xmlcml/ami2/phylo/ijs_0_000364_0/image/003.pbm.png.hocr.html`). This is converted by `org.xmlcml.ami2.plugins.phylotree.HOCRPhyloTreeTest` (later tyo be packaged tidily) "HOCR output"
## Merging

The *.nexml and 
