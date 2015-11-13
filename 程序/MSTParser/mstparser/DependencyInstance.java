package mstparser;

public class DependencyInstance {

    public String[] sentence;
    public String[] pos;
    public String[] labs;
    public FeatureVector fv;
    public String actParseTree;
    public int length;
    
    public DependencyInstance() {}
    
    public DependencyInstance(int length) { this.length = length; }
    
    public DependencyInstance(String[] sentence, FeatureVector fv) {
	this.sentence = sentence;
	this.fv = fv;
	this.length = sentence.length;
    }
    
    public DependencyInstance(String[] sentence, String[] pos, FeatureVector fv) {
	this.sentence = sentence;
	this.pos = pos;
	this.fv = fv;
	this.length = sentence.length;
    }
    
    public DependencyInstance(String[] sentence, String[] pos, String[] labs, FeatureVector fv) {
	this.sentence = sentence;
	this.pos = pos;
	this.labs = labs;
	this.fv = fv;
	this.length = sentence.length;
    }
    
}
