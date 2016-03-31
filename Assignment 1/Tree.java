import java.util.*;
import java.io.*;

public class Tree {
	Tree left,right;
	HashMap<Integer, String> attributes;
	HashSet<Integer> index;
	double entropy, infoGain;
	double vi,infoGainVI;
	Integer classCt0, classCt1;
	String classifier, tgtAttr;
	boolean seen;
	Integer classValue;
	
//default constructor	
	Tree(){
		classValue = -1;
		entropy=0; infoGain=0;vi=0;infoGainVI=0;
		left=null;right=null;
		classCt0=0; classCt1=0; classifier="";
		tgtAttr="";
		seen=false;
		attributes=new HashMap<Integer, String>();
		index=new HashSet<Integer>();
	}
//parameterized constructor 	
	Tree(Tree node){
		this.entropy=node.entropy;
		this.vi=node.vi;
		this.infoGain=node.infoGain;
		this.infoGainVI=node.infoGainVI;
		this.attributes=new HashMap<Integer, String>(node.attributes);
		this.index=new HashSet<Integer>(node.index);
		this.classCt0=node.classCt0;
		this.classCt1=node.classCt1;
		this.classValue=node.classValue;
		this.classifier=node.classifier;
		this.tgtAttr=node.tgtAttr;
		this.seen=node.seen;
	}
	
	Tree getLeft(){
		return left;
	}
	
	Tree getRight(){
		return right;
	}
	
	void setVisited(boolean val){
		seen = val;
	}
	
	boolean isSeen(){
		return seen;
	}
	
	void setLeft(Tree node){
		left=node;
	}
	
	void setRight(Tree node){
		right=node;
	}
	
	void setEntropy(double value){
		entropy=value;
	}
	
	double getEntropy(){
		return entropy;
	}
	
	void setVI(double val){
		vi=val;
	}
	
	double getVI(){
		return vi;
	}
	
	void setInformationGain(double value){
		infoGain=value;
	}
	
	void setDataIndex(HashMap<Integer,HashMap<String,Integer>> records, String attr, int val){
		classCt0=classCt1=0;
		Iterator itr=getIndex().iterator();
		HashSet<Integer> set = new HashSet<Integer>();
		HashMap<String,Integer> hm;
		while(itr.hasNext()){
			int row=(Integer)itr.next();
			hm=new HashMap<String,Integer>(records.get(row));
			if(hm.get(attr)==val){
				set.add(row);
				if(hm.get(getTgtAttr())==0){
					classCt0++;
				}
				else
					classCt1++;
			}
		}
		
		index.clear();
		index=new HashSet<Integer>(set);
	}
	
	double getInformationGain(){
		return infoGain;
	}
	
	void setInformationGainVI(double value){
		infoGainVI=value;
	}
	
	double getInformationGainVI(){
		return infoGainVI;
	}
	
	void setIndex(HashSet<Integer> in){
		index=new HashSet<Integer>(in);
	}
	
	HashSet<Integer> getIndex(){
		return index;
	}

	void setAttr(HashMap<Integer,String> attr){
		attributes.putAll(attr);
	}
	
	HashMap<Integer,String> getAttr(){
		return attributes;
	}
	
	String getTgtAttr(){
		return tgtAttr;
	}
	
	void setTgtAttr(String a){
		tgtAttr=a;
	}
	
void setClassCount(HashMap<Integer,HashMap<String,Integer>> records){
	classCt0=classCt1=0;
	Iterator itr=records.entrySet().iterator();
	HashMap<String,Integer> inner;
	while(itr.hasNext()){
		Map.Entry pair = (Map.Entry) itr.next();
		inner = new HashMap<String,Integer>();
		int row = (Integer) pair.getKey();
		inner = (HashMap<String,Integer>)pair.getValue();
		index.add(row);
		if(inner.get(getTgtAttr())==0){
			classCt0++;
		}
		else
			classCt1++;
	}
}
	
	String getAttrValue(int key){
		return attributes.get(key);
	}
	
	boolean isAttrEmpty(){
		if((attributes.size()-1)>0)
			return false;
		else
			return true;
	}
	
	void setClassValue(int val){
		classValue = val;
	}
	
	int getClassValue(){
		return classValue;
	}
	
	void setClassifier(String clas){
		classifier=clas;
	}
	
	String getClassifier(){
		return classifier;
	}
	
	int getClassCount(int val){
		if(val==0)
			return classCt0;
		else
			return classCt1;
	}
	
	void removeAttr(int attribute){
		attributes.remove(attribute);
	}
		
	void calcEntropy(){
		int total = classCt0+classCt1;
		if(total==0)
			entropy=0;
		else{
			double p0 = (double) classCt0/total;
			double p1 = (double) classCt1/total;
			double prod0 = (p0*(log(p0,2))*-1);
			double prod1 = (p1*(log(p1,2))*-1);
			entropy=(double)prod0+prod1;
		}
	}
	
	void calcVI(){
		int total = classCt0+classCt1;
		if(total==0)
			vi=0;
		else{
			double p0=(double) classCt0/total;
			double p1=(double) classCt1/total;
			vi=(double)p0*p1;
		}
	}
	
	static double log(double val, int base){
		if(val!=0)
			return (Math.log(val) / Math.log(base));
		else
			return 0;
	}
	
	void calcInformationGain(){
		if (left==null && right ==null) 
			infoGain=1.0;
		else{
			double enLeft,enRight;
			left.calcEntropy();
			enLeft=left.getEntropy();
			right.calcEntropy();
			enRight=right.getEntropy();
			int totLeft = left.getClassCount(0)+left.getClassCount(1);
			int totRight = right.getClassCount(0)+right.getClassCount(1);
			calcEntropy();
			int total=classCt0+classCt1;
			double pLeft = (double)totLeft/total;
			double pRight = (double)totRight/total;
			double prodLeft = (double)pLeft*enLeft;
			double prodRight=(double)pRight*enRight;
			infoGain=(double)entropy-(prodLeft+prodRight);
		}
	}
	
	void calcInformationGainVI(){
		if (left==null && right ==null) 
			infoGainVI=1.0;
		else{
			double viLeft,viRight;
			left.calcVI();
			viLeft=left.getVI();
			right.calcVI();
			viRight=right.getVI();
			int totLeft = left.getClassCount(0)+left.getClassCount(1);
			int totRight = right.getClassCount(0)+right.getClassCount(1);
			calcVI();
			int total=classCt0+classCt1;
			double pLeft = (double)totLeft/total;
			double pRight = (double)totRight/total;
			double prodLeft = (double)pLeft*viLeft;
			double prodRight=(double)pRight*viRight;
			infoGainVI=(double)vi-(prodLeft+prodRight);
		}
	}
}
