#-*-coding:utf-8-*- 
#! /usr/bin/python

import sys;
import codecs;
# Open File
#f = open(sys.argv[1],'rt');
f=open(sys.argv[1]);

wrds = ""; pos = ""; labs = ""; par = "";

for line in f:
    
    sent = line.split();

    if len(sent) > 0:
        wrds += sent[1] + "\t";
        pos += sent[4] + "\t";
        labs += sent[7] + "\t";
        par += sent[6] + "\t";
    else:
        print (wrds); 
        wrds = "";
        print (pos); 
        pos = "";
        print (labs); 
        labs = "";
        print (par); 
        par = "";
        print ("");

f.close();

