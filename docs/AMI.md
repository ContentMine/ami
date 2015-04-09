# AMI Tutorial

## Overview 

AMI is highly modular and consists of Java code, XML control, data and tests. 

 * Unless you are writing a plugin you don't need to know about the code. 
 * The XML (`args.xml`) controls what options each plugin has and how the system decides what to do and when
 * the data suports the plugin (e.g. `stopwords.txt` for `ami-word`)
 * the tests help development, but also check the correctness of the code and make sure that bugs don't creep in ("regression"). Tests are also useful in showing some of the options - e.g. for tutorials.
 
 AMI is built as a `maven` framework. This shouldn't worry you - just accept that many of the filenames are conventional
 
 


