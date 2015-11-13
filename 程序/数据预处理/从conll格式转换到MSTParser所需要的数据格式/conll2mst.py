# -*- coding: UTF-8 -*-
#! /usr/bin/python
import sys
import codecs
f=open(sys.argv[1])
f2=open(sys.argv[2],'w')
wrds = ""
pos = ""
labs = ""
par = ""

for line in f:

    sent = line.split()

    if len(sent) > 0:
        wrds += sent[1] + "\t"
        pos += sent[3] + "\t"
        labs += sent[7] + "\t"
        par += sent[6] + "\t"
    else:
        print (wrds)
        f2.write(wrds+"\r\n")
        wrds = ""
        print (pos)
        f2.write(pos+"\r\n")
        pos = ""
        print (labs)
        f2.write(labs+"\r\n")
        labs = ""
        print (par)
        f2.write(par+"\r\n")
        par = ""
        print ("")
        f2.write("\r\n")
f.close()
f2.close()