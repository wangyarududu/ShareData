package mstparser;

import java.io.*;
import gnu.trove.*;
import java.util.*;

public class DependencyPipe {

    public Alphabet dataAlphabet;
	
    public Alphabet typeAlphabet;
    public String[] types;
    public int[] typesInt;
	
    public boolean labeled = false;

    public boolean createForest;
	
    public DependencyPipe() throws IOException {
	this(true);
    }

    public DependencyPipe(boolean createForest) throws IOException {
	dataAlphabet = new Alphabet();
	typeAlphabet = new Alphabet();
	this.createForest = createForest;
    }

    public void setLabeled(String file) throws IOException {
	BufferedReader in = new BufferedReader(new FileReader(file));
	in.readLine(); in.readLine(); in.readLine();
	String line = in.readLine();
	if(line.trim().length() > 0) labeled = true;
	in.close();
    }

    public String[][] getLines(BufferedReader in) throws IOException {
	String line = in.readLine();
	String pos_line = in.readLine();
	String lab_line = labeled ? in.readLine() : pos_line;
	String deps_line = in.readLine();
	in.readLine(); // blank line

	if(line == null) return null;

	String[] toks = line.split("\t");
	String[] pos = pos_line.split("\t");
	String[] labs = lab_line.split("\t");
	String[] deps = deps_line.split("\t");
	
	String[] toks_new = new String[toks.length+1];
	String[] pos_new = new String[pos.length+1];
	String[] labs_new = new String[labs.length+1];
	String[] deps_new = new String[deps.length+1];
	toks_new[0] = "<root>";
	pos_new[0] = "<root-POS>";
	labs_new[0] = "<no-type>";
	deps_new[0] = "-1";
	for(int i = 0; i < toks.length; i++) {
	    toks_new[i+1] = normalize(toks[i]);
	    pos_new[i+1] = pos[i];
	    labs_new[i+1] = labeled ? labs[i] : "<no-type>";
	    deps_new[i+1] = deps[i];
	}
	toks = toks_new;
	pos = pos_new;
	labs = labs_new;
	deps = deps_new;
	
	String[][] result = new String[4][];
	result[0] = toks; result[1] = pos; result[2] = labs; result[3] = deps;
	return result;
    }

    public DependencyInstance createInstance(BufferedReader in) throws IOException {
	String[][] lines = getLines(in);
	if(lines == null) return null;

	String[] toks = lines[0];
	String[] pos = lines[1];
	String[] labs = lines[2];
	String[] deps = lines[3];
	
	int[] deps1 = new int[deps.length];
	for(int i = 0; i < deps.length; i++)
	    deps1[i] = Integer.parseInt(deps[i]);
	
	FeatureVector fv = createFeatureVector(toks,pos,labs,deps1);
	
	DependencyInstance pti = new DependencyInstance(toks,pos,labs,fv);
	
	String spans = "";
	for(int i = 1; i < deps.length; i++) {
	    spans += deps[i]+"|"+i+":"+typeAlphabet.lookupIndex(labs[i])+" ";
	}
	pti.actParseTree = spans.trim();
	
	return pti;
    }

    public DependencyInstance[] createInstances(String file,
						String featFileName) throws IOException {

	createAlphabet(file);

	System.out.println("Num Features: " + dataAlphabet.size());

	BufferedReader in = //new BufferedReader(new FileReader(file));
	    new BufferedReader(new InputStreamReader(new FileInputStream(file),"UTF8"));
	String[][] lines = getLines(in);
		
	LinkedList lt = new LinkedList();

	ObjectOutputStream out = createForest
	    ? new ObjectOutputStream(new FileOutputStream(featFileName))
	    : null;
		
	int num1 = 0;
	while(lines != null) {
	    System.out.println("Creating Feature Vector Instance: " + num1);
			
	    String[] toks = lines[0];
	    String[] pos = lines[1];
	    String[] labs = lines[2];
	    String[] deps = lines[3];
			
	    int[] deps1 = new int[deps.length];
	    for(int i = 0; i < deps.length; i++)
		deps1[i] = Integer.parseInt(deps[i]);

	    FeatureVector fv = createFeatureVector(toks,pos,labs,deps1);
			
	    DependencyInstance pti = new DependencyInstance(toks,pos,labs,fv);

	    String spans = "";
	    for(int i = 1; i < deps.length; i++) {
		spans += deps[i]+"|"+i+":"+typeAlphabet.lookupIndex(labs[i])+" ";
	    }
	    pti.actParseTree = spans.trim();

	    if(createForest)
		possibleFeatures(pti,out);
	    pti = null;
			
	    lt.add(new DependencyInstance(toks.length));
			
	    lines = getLines(in);
	    num1++;
	}

	closeAlphabets();
		
	DependencyInstance[] pti = new DependencyInstance[lt.size()];
	for(int i = 0; i < pti.length; i++) {
	    pti[i] = (DependencyInstance)lt.get(i);
	}

	if(createForest)
	    out.close();

	in.close();

	return pti;
		
    }

    private void createAlphabet(String file) throws IOException {

	System.out.print("Creating Alphabet ... ");

	BufferedReader in =
	    new BufferedReader(new InputStreamReader(new FileInputStream(file),"UTF8"));
	String[][] lines = getLines(in);

	int cnt = 0;
		
	while(lines != null) {
			
	    String[] toks = lines[0];
	    String[] pos = lines[1];
	    String[] labs = lines[2];
	    String[] deps = lines[3];

	    for(int i = 0; i < labs.length; i++)
		typeAlphabet.lookupIndex(labs[i]);
			
	    int[] deps1 = new int[deps.length];
	    for(int i = 0; i < deps.length; i++) {
		deps1[i] = Integer.parseInt(deps[i]);
	    }
			
	    createFeatureVector(toks,pos,labs,deps1);
			
	    lines = getLines(in);
	    cnt++;
	}

	closeAlphabets();

	in.close();

	System.out.println("Done.");
    }
	
    public void closeAlphabets() {
	dataAlphabet.stopGrowth();
	typeAlphabet.stopGrowth();
		
	types = new String[typeAlphabet.size()];
	Object[] keys = typeAlphabet.toArray();
	for(int i = 0; i < keys.length; i++) {
	    int indx = typeAlphabet.lookupIndex(keys[i]);
	    types[indx] = (String)keys[i];
	}
				
	KBestParseForest.rootType = typeAlphabet.lookupIndex("<root-type>");
		
    }

    public String normalize(String s) {
	if(s.matches("[0-9]+|[0-9]+\\.[0-9]+|[0-9]+[0-9,]+"))
	    return "<num>";

	return s;

    }	
	
    public FeatureVector createFeatureVector(String[] toks,
					     String[] pos,
					     String[] posA,
					     int small,
					     int large,
					     boolean attR,
					     FeatureVector fv) {
	    
	String att = "";
	if(attR)
	    att = "RA";
	else
	    att = "LA";
		
	int dist = Math.abs(large-small);
	String distBool = "0";
	if(dist > 1)
	    distBool = "1";
	if(dist > 2)
	    distBool = "2";
	if(dist > 3)
	    distBool = "3";
	if(dist > 4)
	    distBool = "4";
	if(dist > 5)
	    distBool = "5";
	if(dist > 10)
	    distBool = "10";
		
	String attDist = "&"+att+"&"+distBool;

	String pLeft = small > 0 ? pos[small-1] : "STR";
	String pRight = large < pos.length-1 ? pos[large+1] : "END";
	String pLeftRight = small < large-1 ? pos[small+1] : "MID";
	String pRightLeft = large > small+1 ? pos[large-1] : "MID";
	String pLeftA = small > 0 ? posA[small-1] : "STR";
	String pRightA = large < pos.length-1 ? posA[large+1] : "END";
	String pLeftRightA = small < large-1 ? posA[small+1] : "MID";
	String pRightLeftA = large > small+1 ? posA[large-1] : "MID";
		
	// feature posR posMid posL
	for(int i = small+1; i < large; i++) {
	    String allPos = pos[small]+" "+pos[i]+" "+pos[large];
	    String allPosA = posA[small]+" "+posA[i]+" "+posA[large];
	    fv = add("PC="+allPos+attDist,1.0,fv);
	    fv = add("1PC="+allPos,1.0,fv);
	    fv = add("XPC="+allPosA+attDist,1.0,fv);
	    fv = add("X1PC="+allPosA,1.0,fv);
	}

	// feature posL-1 posL posR posR+1
	fv = add("PT="+pLeft+" "+pos[small]+" "+pos[large]+" "+pRight+attDist,1.0,fv);
	fv = add("PT1="+pos[small]+" "+pos[large]+" " +pRight+attDist,1.0,fv);
	fv = add("PT2="+pLeft+" "+pos[small]+" "+pos[large]+attDist,1.0,fv);
	fv = add("PT3="+pLeft+" "+pos[large]+" "+pRight+attDist,1.0,fv);
	fv = add("PT4="+pLeft+" "+pos[small]+" "+pRight+attDist,1.0,fv);
		
	fv = add("1PT="+pLeft+" "+pos[small]+" "+pos[large]+" "+pRight,1.0,fv);
	fv = add("1PT1="+pos[small]+" "+pos[large]+" " +pRight,1.0,fv);
	fv = add("1PT2="+pLeft+" "+pos[small]+" "+pos[large],1.0,fv);
	fv = add("1PT3="+pLeft+" "+pos[large]+" "+pRight,1.0,fv);
	fv = add("1PT4="+pLeft+" "+pos[small]+" "+pRight,1.0,fv);
		
	fv = add("XPT="+pLeftA+" "+posA[small]+" "+posA[large]+" "+pRightA+attDist,1.0,fv);
	fv = add("XPT1="+posA[small]+" "+posA[large]+" " +pRightA+attDist,1.0,fv);
	fv = add("XPT2="+pLeftA+" "+posA[small]+" "+posA[large]+attDist,1.0,fv);
	fv = add("XPT3="+pLeftA+" "+posA[large]+" "+pRightA+attDist,1.0,fv);
	fv = add("XPT4="+pLeftA+" "+posA[small]+" "+pRightA+attDist,1.0,fv);
		
	fv = add("X1PT="+pLeftA+" "+posA[small]+" "+posA[large]+" "+pRightA,1.0,fv);
	fv = add("X1PT1="+posA[small]+" "+posA[large]+" " +pRightA,1.0,fv);
	fv = add("X1PT2="+pLeftA+" "+posA[small]+" "+posA[large],1.0,fv);
	fv = add("X1PT3="+pLeftA+" "+posA[large]+" "+pRightA,1.0,fv);
	fv = add("X1PT4="+pLeftA+" "+posA[small]+" "+pRightA,1.0,fv);
		
	// feature posL posL+1 posR-1 posR
	fv = add("APT="+pos[small]+" "+pLeftRight+" "
		 +pRightLeft+" "+pos[large]+attDist,1.0,fv);
	fv = add("APT1="+pos[small]+" "+pRightLeft+" "+pos[large]+attDist,1.0,fv);
	fv = add("APT2="+pos[small]+" "+pLeftRight+" "+pos[large]+attDist,1.0,fv);
	fv = add("APT3="+pLeftRight+" "+pRightLeft+" "+pos[large]+attDist,1.0,fv);
	fv = add("APT4="+pos[small]+" "+pLeftRight+" "+pRightLeft+attDist,1.0,fv);

	fv = add("1APT="+pos[small]+" "+pLeftRight+" "
		 +pRightLeft+" "+pos[large],1.0,fv);
	fv = add("1APT1="+pos[small]+" "+pRightLeft+" "+pos[large],1.0,fv);
	fv = add("1APT2="+pos[small]+" "+pLeftRight+" "+pos[large],1.0,fv);
	fv = add("1APT3="+pLeftRight+" "+pRightLeft+" "+pos[large],1.0,fv);
	fv = add("1APT4="+pos[small]+" "+pLeftRight+" "+pRightLeft,1.0,fv);
		
	fv = add("XAPT="+posA[small]+" "+pLeftRightA+" "
		 +pRightLeftA+" "+posA[large]+attDist,1.0,fv);
	fv = add("XAPT1="+posA[small]+" "+pRightLeftA+" "+posA[large]+attDist,1.0,fv);
	fv = add("XAPT2="+posA[small]+" "+pLeftRightA+" "+posA[large]+attDist,1.0,fv);
	fv = add("XAPT3="+pLeftRightA+" "+pRightLeftA+" "+posA[large]+attDist,1.0,fv);
	fv = add("XAPT4="+posA[small]+" "+pLeftRightA+" "+pRightLeftA+attDist,1.0,fv);

	fv = add("X1APT="+posA[small]+" "+pLeftRightA+" "
		 +pRightLeftA+" "+posA[large],1.0,fv);
	fv = add("X1APT1="+posA[small]+" "+pRightLeftA+" "+posA[large],1.0,fv);
	fv = add("X1APT2="+posA[small]+" "+pLeftRightA+" "+posA[large],1.0,fv);
	fv = add("X1APT3="+pLeftRightA+" "+pRightLeftA+" "+posA[large],1.0,fv);
	fv = add("X1APT4="+posA[small]+" "+pLeftRightA+" "+pRightLeftA,1.0,fv);
		
	// feature posL-1 posL posR-1 posR
	// feature posL posL+1 posR posR+1
	fv = add("BPT="+pLeft+" "+pos[small]+" "+pRightLeft+" "+pos[large]+attDist,1.0,fv);
	fv = add("1BPT="+pLeft+" "+pos[small]+" "+pRightLeft+" "+pos[large],1.0,fv);
	fv = add("CPT="+pos[small]+" "+pLeftRight+" "+pos[large]+" "+pRight+attDist,1.0,fv);
	fv = add("1CPT="+pos[small]+" "+pLeftRight+" "+pos[large]+" "+pRight,1.0,fv);
		
	fv = add("XBPT="+pLeftA+" "+posA[small]+" "+pRightLeftA+" "+posA[large]+attDist,1.0,fv);
	fv = add("X1BPT="+pLeftA+" "+posA[small]+" "+pRightLeftA+" "+posA[large],1.0,fv);
	fv = add("XCPT="+posA[small]+" "+pLeftRightA+" "+posA[large]+" "+pRightA+attDist,1.0,fv);
	fv = add("X1CPT="+posA[small]+" "+pLeftRightA+" "+posA[large]+" "+pRightA,1.0,fv);

	String head = attR ? toks[small] : toks[large];
	String headP = attR ? pos[small] : pos[large];
	String child = attR ? toks[large] : toks[small];
	String childP = attR ? pos[large] : pos[small];
		
	String all = head + " " + headP + " " + child + " " + childP;
	String hPos = headP + " " + child + " " + childP;
	String cPos = head + " " + headP + " " + childP;
	String hP = headP + " " + child;
	String cP = head + " " + childP;
	String oPos = headP + " " + childP;
	String oLex = head + " " + child;

	fv = add("A="+all+attDist,1.0,fv); //this
	fv = add("B="+hPos+attDist,1.0,fv);
	fv = add("C="+cPos+attDist,1.0,fv);
	fv = add("D="+hP+attDist,1.0,fv);
	fv = add("E="+cP+attDist,1.0,fv);
	fv = add("F="+oLex+attDist,1.0,fv); //this
	fv = add("G="+oPos+attDist,1.0,fv);
	fv = add("H="+head+" "+headP+attDist,1.0,fv);
	fv = add("I="+headP+attDist,1.0,fv);
	fv = add("J="+head+attDist,1.0,fv); //this
	fv = add("K="+child+" "+childP+attDist,1.0,fv);
	fv = add("L="+childP+attDist,1.0,fv);
	fv = add("M="+child+attDist,1.0,fv); //this

	fv = add("AA="+all,1.0,fv); //this
	fv = add("BB="+hPos,1.0,fv);
	fv = add("CC="+cPos,1.0,fv);
	fv = add("DD="+hP,1.0,fv);
	fv = add("EE="+cP,1.0,fv);
	fv = add("FF="+oLex,1.0,fv); //this
	fv = add("GG="+oPos,1.0,fv);
	fv = add("HH="+head+" "+headP,1.0,fv);
	fv = add("II="+headP,1.0,fv);
	fv = add("JJ="+head,1.0,fv); //this
	fv = add("KK="+child+" "+childP,1.0,fv);
	fv = add("LL="+childP,1.0,fv);
	fv = add("MM="+child,1.0,fv); //this

	if(head.length() > 5 || child.length() > 5) {
	    int hL = head.length();
	    int cL = child.length();
		    
	    head = hL > 5 ? head.substring(0,5) : head;
	    child = cL > 5 ? child.substring(0,5) : child;
		    
	    all = head + " " + headP + " " + child + " " + childP;
	    hPos = headP + " " + child + " " + childP;
	    cPos = head + " " + headP + " " + childP;
	    hP = headP + " " + child;
	    cP = head + " " + childP;
	    oPos = headP + " " + childP;
	    oLex = head + " " + child;
	
	    fv = add("SA="+all+attDist,1.0,fv); //this
	    fv = add("SF="+oLex+attDist,1.0,fv); //this
	    fv = add("SAA="+all,1.0,fv); //this
	    fv = add("SFF="+oLex,1.0,fv); //this

	    if(cL > 5) {
		fv = add("SB="+hPos+attDist,1.0,fv);
		fv = add("SD="+hP+attDist,1.0,fv);
		fv = add("SK="+child+" "+childP+attDist,1.0,fv);
		fv = add("SM="+child+attDist,1.0,fv); //this
		fv = add("SBB="+hPos,1.0,fv);
		fv = add("SDD="+hP,1.0,fv);
		fv = add("SKK="+child+" "+childP,1.0,fv);
		fv = add("SMM="+child,1.0,fv); //this
	    }
	    if(hL > 5) {
		fv = add("SC="+cPos+attDist,1.0,fv);
		fv = add("SE="+cP+attDist,1.0,fv);
		fv = add("SH="+head+" "+headP+attDist,1.0,fv);
		fv = add("SJ="+head+attDist,1.0,fv); //this
			
		fv = add("SCC="+cPos,1.0,fv);
		fv = add("SEE="+cP,1.0,fv);
		fv = add("SHH="+head+" "+headP,1.0,fv);
		fv = add("SJJ="+head,1.0,fv); //this
	    }
	}
		
	return fv;
		
    }
	
    public FeatureVector createFeatureVector(String[] toks,
					     String[] pos,
					     String[] posA,
					     int word,
					     String type,
					     boolean attR,
					     boolean childFeatures,
					     FeatureVector fv) {
		
	if(!labeled) return fv;

	String att = "";
	if(attR)
	    att = "RA";
	else
	    att = "LA";

	att+="&"+childFeatures;
		
	String w = toks[word];
	String wP = pos[word];

	String wPm1 = word > 0 ? pos[word-1] : "STR";
	String wPp1 = word < pos.length-1 ? pos[word+1] : "END";

	fv = add("NTS1="+type+"&"+att,1.0,fv);
	fv = add("ANTS1="+type,1.0,fv);
	for(int i = 0; i < 2; i++) {
	    String suff = i < 1 ? "&"+att : "";
	    suff = "&"+type+suff;

	    fv = add("NTH="+w+" "+wP+suff,1.0,fv);
	    fv = add("NTI="+wP+suff,1.0,fv);
	    fv = add("NTIA="+wPm1+" "+wP+suff,1.0,fv);
	    fv = add("NTIB="+wP+" "+wPp1+suff,1.0,fv);
	    fv = add("NTIC="+wPm1+" "+wP+" "+wPp1+suff,1.0,fv);
	    fv = add("NTJ="+w+suff,1.0,fv); //this
			
	}
		
	return fv;
    }
	
    public FeatureVector createFeatureVector(String[] toks,
					     String[] pos,
					     String[] labs,
					     int[] deps) {

	String[] posA = new String[pos.length];
	for(int i = 0; i < pos.length; i++) {
	    posA[i] = pos[i].substring(0,1);
	}
		
	FeatureVector fv = new FeatureVector(-1,-1.0,null);
	for(int i = 0; i < toks.length; i++) {
	    if(deps[i] == -1)
		continue;
	    int small = i < deps[i] ? i : deps[i];
	    int large = i > deps[i] ? i : deps[i];
	    boolean attR = i < deps[i] ? false : true;
	    fv = createFeatureVector(toks,pos,posA,small,large,attR,fv);
	    if(labeled) {
		fv = createFeatureVector(toks,pos,posA,i,labs[i],attR,true,fv);
		fv = createFeatureVector(toks,pos,posA,deps[i],labs[i],attR,false,fv);
	    }
	}
	return fv;
    }

    public FeatureVector add(String feat, double val, FeatureVector fv) {
	int num = dataAlphabet.lookupIndex(feat);
	if(num >= 0)
	    return new FeatureVector(num,val,fv);
	return fv;
    }

    public void possibleFeatures(DependencyInstance inst, ObjectOutputStream out) {
	String[] toks = inst.sentence;
	String[] pos = inst.pos;
	String[] labs = inst.labs;
		
	String[] posA = new String[pos.length];
	for(int i = 0; i < pos.length; i++) {
	    posA[i] = pos[i].substring(0,1);
	}

		
	try {

	    for(int w1 = 0; w1 < toks.length; w1++) {
		for(int w2 = w1+1; w2 < toks.length; w2++) {
					
		    for(int ph = 0; ph < 2; ph++) {						
			boolean attR = ph == 0 ? true : false;

			int childInt = attR ? w2 : w1;
			int parInt = attR ? w1 : w2;
						
			FeatureVector prodFV = createFeatureVector(toks,pos,posA,w1,w2,attR,
								   new FeatureVector(-1,-1.0,null));
								
			for(FeatureVector curr = prodFV; curr != null; curr = curr.next) {
			    if(curr.index >= 0)
				out.writeInt(curr.index);
			}
			out.writeInt(-2);
								
		    }
		}
			
	    }

	    out.writeInt(-3);

	    if(labeled) {
		for(int w1 = 0; w1 < toks.length; w1++) {
		    
		    for(int t = 0; t < types.length; t++) {
			String type = types[t];
			
			for(int ph = 0; ph < 2; ph++) {						
			    boolean attR = ph == 0 ? true : false;
			    
			    for(int ch = 0; ch < 2; ch++) {						
				boolean child = ch == 0 ? true : false;						
				
				FeatureVector prodFV = createFeatureVector(toks,pos,posA,w1,
									   type,
									   attR,child,
									   new FeatureVector(-1,-1.0,null));
				
				for(FeatureVector curr = prodFV; curr != null; curr = curr.next) {
				    if(curr.index >= 0)
					out.writeInt(curr.index);
				}
				out.writeInt(-2);
				
			    }
			}
		    }
		    
		}
		
		out.writeInt(-3);
	    }

	    for(FeatureVector curr = inst.fv; curr.next != null; curr = curr.next)
		out.writeInt(curr.index);

	    out.writeInt(-4);
	    out.writeObject(inst.sentence);
	    out.writeInt(-5);
	    out.writeObject(inst.pos);
	    out.writeInt(-6);
	    out.writeObject(inst.labs);
	    out.writeInt(-7);
	    out.writeObject(inst.actParseTree);
			
	    out.writeInt(-1);
	    out.reset();

	} catch (IOException e) {}
		
    }
	
    public DependencyInstance getFeatureVector(ObjectInputStream in,
					       DependencyInstance inst,
					       FeatureVector[][][] fvs,
					       double[][][] probs,
					       FeatureVector[][][][] nt_fvs,
					       double[][][][] nt_probs,
					       Parameters params) throws IOException {
	int length = inst.length;
		
	// Get production crap.		
	for(int w1 = 0; w1 < length; w1++) {
	    for(int w2 = w1+1; w2 < length; w2++) {
				
		for(int ph = 0; ph < 2; ph++) {

		    FeatureVector prodFV = new FeatureVector(-1,-1.0,null);
					
		    int indx = in.readInt();
		    while(indx != -2) {
			prodFV = new FeatureVector(indx,1.0,prodFV);
			indx = in.readInt();
		    }
					
		    double prodProb = params.getScore(prodFV);
		    fvs[w1][w2][ph] = prodFV;
		    probs[w1][w2][ph] = prodProb;
		}
	    }
			
	}
	int last = in.readInt();
	if(last != -3) { System.out.println("Error reading file."); System.exit(0); }

	if(labeled) {
	    for(int w1 = 0; w1 < length; w1++) {
		
		for(int t = 0; t < types.length; t++) {
		    String type = types[t];
		    
		    for(int ph = 0; ph < 2; ph++) {						
			
			for(int ch = 0; ch < 2; ch++) {						
			    
			    FeatureVector prodFV = new FeatureVector(-1,-1.0,null);
			    
			    int indx = in.readInt();
			    while(indx != -2) {
				prodFV = new FeatureVector(indx,1.0,prodFV);
				indx = in.readInt();
			    }
			    
			    double nt_prob = params.getScore(prodFV);
			    nt_fvs[w1][t][ph][ch] = prodFV;
			    nt_probs[w1][t][ph][ch] = nt_prob;
			    
			}
		    }
		}
		
	    }
	    last = in.readInt();
	    if(last != -3) { System.out.println("Error reading file."); System.exit(0); }
	}

	FeatureVector nfv = new FeatureVector(-1,-1.0,null);
	int next = in.readInt();
	while(next != -4) {
	    nfv = new FeatureVector(next,1.0,nfv);
	    next = in.readInt();
	}

	String[] toks = null;
	String[] pos = null;
	String[] labs = null;
	String actParseTree = null;
	try {
	    toks = (String[])in.readObject();
	    next = in.readInt();
	    pos = (String[])in.readObject();
	    next = in.readInt();
	    labs = (String[])in.readObject();
	    next = in.readInt();
	    actParseTree = (String)in.readObject();
	    next = in.readInt();
	}
	catch(ClassNotFoundException e) { System.out.println("Error reading file."); System.exit(0); }
		
	if(next != -1) { System.out.println("Error reading file."); System.exit(0); }

	DependencyInstance pti = new DependencyInstance(toks,pos,labs,nfv);
	pti.actParseTree = actParseTree;
	return pti;
		
    }
		
    public void getFeatureVector(DependencyInstance inst,
				 FeatureVector[][][] fvs,
				 double[][][] probs,
				 FeatureVector[][][][] nt_fvs,
				 double[][][][] nt_probs, Parameters params) {

	String[] toks = inst.sentence;
	String[] pos = inst.pos;
	String[] labs = inst.labs;
		
	String[] posA = new String[pos.length];
	for(int i = 0; i < pos.length; i++) {
	    posA[i] = pos[i].substring(0,1);
	}

	// Get production crap.		
	for(int w1 = 0; w1 < toks.length; w1++) {
	    for(int w2 = w1+1; w2 < toks.length; w2++) {
				
		for(int ph = 0; ph < 2; ph++) {
		    boolean attR = ph == 0 ? true : false;
		    
		    int childInt = attR ? w2 : w1;
		    int parInt = attR ? w1 : w2;
		    
		    FeatureVector prodFV = createFeatureVector(toks,pos,posA,w1,w2,attR,
								    new FeatureVector(-1,-1.0,null));
										
		    double prodProb = params.getScore(prodFV);
		    fvs[w1][w2][ph] = prodFV;
		    probs[w1][w2][ph] = prodProb;
		}
	    }
			
	}

	if(labeled) {
	    for(int w1 = 0; w1 < toks.length; w1++) {
		
		for(int t = 0; t < types.length; t++) {
		    String type = types[t];
		    
		    for(int ph = 0; ph < 2; ph++) {						
			boolean attR = ph == 0 ? true : false;
			
			for(int ch = 0; ch < 2; ch++) {						
			    boolean child = ch == 0 ? true : false;						
			    
			    FeatureVector prodFV = createFeatureVector(toks,pos,posA,w1,
									    type,attR,child,
									    new FeatureVector(-1,-1.0,null));
			    
			    double nt_prob = params.getScore(prodFV);
			    nt_fvs[w1][t][ph][ch] = prodFV;
			    nt_probs[w1][t][ph][ch] = nt_prob;
			    
			}
		    }
		}
		
	    }
	}		
    }
		
}
