#! /usr/bin/python

import sys;
import codecs
# Open File
f = open(sys.argv[1]);
f2=open(sys.argv[2],'w')
wrds = "";
pos = "";
labs = "";
par = "";

for line in f:

    if len(line.strip()) == 0:
        w = wrds.split(); p = pos.split(); l = labs.split(); pa = par.split();
        cnt = 1;
        for t in w:
            print str(cnt) + "\t" + t + "\t" + "_" + "\t" + p[cnt-1] + "\t" + "_" + "\t_\t" + pa[cnt-1] + "\t" + l[cnt-1]+"\t_\t_"
            f2.write(str(cnt) + "\t" + t + "\t" + "_" + "\t" + p[cnt-1] + "\t" + "_" + "\t_\t" + pa[cnt-1] + "\t" + l[cnt-1]+"\t_\t_\n")
            cnt += 1;
        print "";
        f2.write("\n");
        wrds = ""; pos = ""; labs = ""; par = "";
    elif len(wrds) == 0:
        wrds = line;
    elif len(pos) == 0:
        pos = line;
    elif len(labs) == 0:
        labs = line;
    else:
        par = line;

f.close();
f2.close();
