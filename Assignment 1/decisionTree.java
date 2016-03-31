import java.util.*;
import java.io.*;
public class decisionTree
{
	Tree root;
	LinkedList<Tree> treeList;
	int size;
//default constructor	
	decisionTree(){
		root=null;
		size=0;
	}
//parameterized constructor	
	decisionTree(Details trainingData, boolean isVI){
		treeList = new LinkedList<Tree>();
		root=null;
		size=0;
		root=new Tree();
		root.setAttr(trainingData.getAttrNames());
		HashMap<Integer,String> temp = root.getAttr();
		root.setTgtAttr(temp.get(temp.size()-1));
		root.setClassCount(trainingData.getRecords());
		root.calcEntropy();
		root.calcVI();
		if(trainingData.getSize()==root.getClassCount(1)){
			root.setClassifier("Root");
			root.setClassValue(1);
			return;
		}
		if(trainingData.getSize()==root.getClassCount(0)){
			root.setClassifier("Root");
			root.setClassValue(0);
			return;
		}
		if(root.isAttrEmpty()){
			if(root.getClassCount(0)>root.getClassCount(1)){
				root.setClassifier("Root");
				root.setClassValue(0);
			}
			else{
				root.setClassifier("Root");
				root.setClassValue(1);
			}
			return;
		}
		root.setLeft(null);
		root.setRight(null);
		if(isVI)
			buildTree(trainingData, root,true); 
		else
			buildTree(trainingData, root,false); 
	}

	decisionTree(decisionTree tree){
		root = new Tree(tree.getRoot());
		root.setLeft(null);
		root.setRight(null);
		treeList = new LinkedList<Tree>(tree.treeList);
		copyTree(tree.getRoot(),root);
	}
	
	void buildTree(Details trainingData, Tree node, boolean isVI){
		int attr=0;
		if(node==null)
			return;
		if(node.getClassCount(0)==(node.getClassCount(0)+node.getClassCount(1))){
			node.setClassValue(0);
			return;
		}
		if(node.getClassCount(1)==(node.getClassCount(0)+node.getClassCount(1))){
			node.setClassValue(1);
			return;
		}
		if(node.isAttrEmpty()){
			if(node.getClassCount(0)>node.getClassCount(1))
				node.setClassValue(0);
			else
				node.setClassValue(1);
			return;
		}
		if(isVI)
			attr=findBest(trainingData,node,true);
		else
			attr=findBest(trainingData,node,false);
		node.setClassifier(node.getAttrValue(attr));
		if(isVI)
			node.calcVI();
		else
			node.calcEntropy();
		HashMap<Integer,String> temp=new HashMap<Integer,String>();
		temp.putAll(node.getAttr());
		Tree left=new Tree();
		left.setAttr(temp);
		left.setTgtAttr(node.getTgtAttr());
		left.setIndex(node.getIndex());
		left.setDataIndex(trainingData.getRecords(), left.getAttrValue(attr), 0);
		if(isVI)
			left.calcVI();
		else
			left.calcEntropy();
		left.removeAttr(attr);
		//Right
		Tree right=new Tree();
		right.setAttr(temp);
		right.setTgtAttr(node.getTgtAttr());
		right.setIndex(node.getIndex());
		right.setDataIndex(trainingData.getRecords(), right.getAttrValue(attr), 1);
		if(isVI)
			right.calcVI();
		else 
			right.calcEntropy();
		right.removeAttr(attr);
		
		if(left.getIndex().size()>0){
			node.setLeft(left);
			buildTree(trainingData,left,false); 
			buildTree(trainingData,left,true); 
		}
		
		if(right.getIndex().size()>0){
			node.setRight(right);
			buildTree(trainingData,right,false); 
			buildTree(trainingData,left,true); 
		}
	}
	
	void copyTree(Tree start, Tree current){
		if(start == null || current == null)
			return;
		if(start.getLeft()!=null){
			Tree newLeft = new Tree(start.getLeft());
			current.setLeft(newLeft);
		}
		if(start.getRight()!=null){
			Tree newRight = new Tree(start.getRight());
			current.setRight(newRight);
		}
		copyTree(start.getLeft(),current.getLeft());
		copyTree(start.getRight(),current.getRight());
	}
	
	int findBest(Details instance, Tree node, boolean isVI){
		double max = Double.NEGATIVE_INFINITY;
		int maxIndex=-1,key=-1;
		String s="";
		Iterator ita=sortByValues(node.getAttr()).entrySet().iterator();
		while(ita.hasNext()){
			Map.Entry pair=(Map.Entry)ita.next();
			key=(Integer)pair.getKey();
			String value = (String)pair.getValue();
			if(node.getTgtAttr()!=value){
				if(isVI){
				double a=calculateInfoGain(instance,value,node,true);
				if(a>max){
					max=a;
					maxIndex=key;
					s=value;
					}
				}
				else{
					double a=calculateInfoGain(instance,value,node,false);
					if(a>max){
						max=a;
						maxIndex=key;
						s=value;
					}
				}
			}
		}
		node.setLeft(null);
		node.setRight(null);
		if(isVI)
			node.setInformationGainVI(max);
		else
			node.setInformationGain(max);
		return maxIndex;
	}
	
	double calculateInfoGain(Details instance, String attr, Tree node, boolean isVI){
		Tree left=new Tree();
		left.setTgtAttr(node.getTgtAttr());
		left.setIndex(node.getIndex());
		left.setDataIndex(instance.getRecords(), attr, 0);
		if(isVI)
			left.calcVI();
		else
			left.calcEntropy();
		Tree right=new Tree();
		right.setTgtAttr(node.getTgtAttr());
		right.setIndex(node.getIndex());
		right.setDataIndex(instance.getRecords(), attr, 1);
		if(isVI)
			right.calcVI();
		else
			right.calcEntropy();
		node.setLeft(left);
		node.setRight(right);
		if(isVI){
			node.calcInformationGainVI();
			return node.getInformationGainVI();
		}
		else{
			node.calcInformationGain();
			return node.getInformationGain();
		}
	}
	
	int getSize(){
		return size;
	}
	
	Tree getRoot(){
		return root;
	}
	
	void printTree(Tree node,int level){
		if(node==null)
			return;
		level++;
		if(node.getLeft()!=null){
			Tree left=node.getLeft();
			int i=0;
			while(i<level){
				System.out.println("|");
				i++;
			}
			if(left.getClassValue()==-1){
				System.out.println(node.getClassifier()+"=0:");	
			}
			else{
				System.out.println(node.getClassifier()+"=0;"+left.getClassValue());
			}
			printTree(left,level);
		}
		if(node.getRight()!=null){
			Tree right=node.getRight();
			int i=0;
			while(i<level){
				System.out.println("|");
				i++;
			}
			if(right.getClassValue()==-1){
				System.out.println(node.getClassifier()+"=1:");	
			}
			else{
				System.out.println(node.getClassifier()+"=1;"+right.getClassValue());
			}
			printTree(right,level);
		}
	}
	
	int getPredictedVal(Tree node, HashMap<String,Integer> hm){
		if(node==null)
			return -1;
		if(node.getLeft()==null&&node.getRight()==null)
			return node.getClassValue();
		if(hm.get(node.getClassifier())==0){
			if(node.getLeft()!=null){
				return getPredictedVal(node.getLeft(),hm);
			}
		}
		if(hm.get(node.getClassifier())==1){
			if(node.getRight()!=null){
				return getPredictedVal(node.getRight(),hm);
			}
		}
		return -1;
	}
	
	double accuracy(Details d){
		Iterator ita=d.getRecords().entrySet().iterator();
		HashMap<String,Integer> hm;
		int counter=0, total=0;
		while(ita.hasNext()){
			Map.Entry entry=(Map.Entry) ita.next();
			hm=new HashMap<String,Integer>((HashMap<String,Integer>)entry.getValue());
			int predicted=getPredictedVal(root,hm);
			int actual=hm.get(root.getTgtAttr());
			if(predicted==actual){
				counter++;
			}
			total++;
		}
		return (double)counter/total;
	}	
	
	void initList(){
		treeList=new LinkedList<Tree>();
	}
	
	void levelOrder(LinkedList<Tree> list){
		if(list.size()==0){
			return;
		}
		LinkedList<Tree> nList = new LinkedList<Tree>();
		for(int i=0;i<list.size();i++){
			if(list.get(i).getLeft()==null && list.get(i).getRight()==null){
				continue;
			}
			else {
				if(list.get(i).getLeft()!=null)
					nList.addLast(list.get(i).getLeft());
				if(list.get(i).getRight()!=null)
					nList.addLast(list.get(i).getRight());
				treeList.addLast(list.get(i));
			}
		}
	}
	
	decisionTree pruning(int L, int K, Details data){
		decisionTree bestTree=new decisionTree(this);
		double bestAccuracy=bestTree.accuracy(data);
		double accuracy=0;
		for(int i=1;i<=L;i++){
			decisionTree currentTree=new decisionTree(this);
			Random rand=new Random();
			int M=rand.nextInt(K)+1;
			for(int j=1;j<=M;j++){
				LinkedList<Tree> list=new LinkedList<Tree>();
				list.add(currentTree.getRoot());
				currentTree.initList();
				currentTree.levelOrder(list);
				int N=currentTree.treeList.size();
				Random rand1=new Random();
				int P=rand1.nextInt(N);
				//if root node, skip.
				
				if(P==0){
					continue;
				}
				if(!currentTree.treeList.get(P).seen){
					Tree node=currentTree.treeList.get(P);
				Tree newNode=new Tree();
					node.setVisited(true);
					node.setLeft(null);
					node.setRight(null);
					node.setLeft(newNode);
					if(node.getClassCount(0)>node.getClassCount(1)){
						newNode.setClassValue(0);
					}
					else{
						newNode.setClassValue(1);
					}
				}
				else{
					continue;
				}
			}
			//calculate accuracy of currentTree
			accuracy=currentTree.accuracy(data);
			if(accuracy>bestAccuracy){
				bestAccuracy=accuracy;
				bestTree=new decisionTree(currentTree);
			}
		}
	return bestTree;
	}
	
	private static HashMap sortByValues(HashMap m){
		List l = new LinkedList(m.entrySet());
		Collections.sort(l, new Comparator(){
			public int compare(Object obj1, Object obj2){
				return ((Comparable)((Map.Entry)(obj1)).getValue()).compareTo(((Map.Entry) (obj2)).getValue());
			}
		});
		
		HashMap sortedMap = new LinkedHashMap();
		for (Iterator it=l.iterator();it.hasNext();){
			Map.Entry n = (Map.Entry) it.next();
			sortedMap.put(n.getKey(),n.getValue());
		}
		return sortedMap;
	}
	
	public static void main(String[] args) throws IOException{
		
		String print="";
		String training="";
		String validation="";
		String test="";
		int L=0,K=0;
		if(args.length<6)
			System.out.println("Please input all the parameters");
		else{
			L=Integer.parseInt(args[0]);
			K=Integer.parseInt(args[1]);
			training=args[2];
			validation=args[3];
			test=args[4];
			print=args[5];
		}	
		
		System.out.println("Inducing Decision Trees");
		System.out.println("1. Information Gain Heuristic");
		Details trainSet1=new Details(training);
		decisionTree tree1=new decisionTree(trainSet1,false);
		Details valSet1=new Details(validation);
		Details testSet1 = new Details(test);
		System.out.println("Decision Tree constructed using Training Set:"+training);
		double accuracy=(double) Math.round(tree1.accuracy(testSet1)*10000)/100;
		System.out.println("Accuracy of"+test+"before pruning: "+accuracy+"%");
		decisionTree secondTree=new decisionTree(tree1);
		decisionTree prunedTree=secondTree.pruning(L,K,valSet1);
		accuracy=(double)Math.round(prunedTree.accuracy(testSet1)*10000)/100;
		System.out.println("Accuracy of pruned decision tree on Test Set: "+test+": "+accuracy+"%");
		if(print.equals("yes")){
			prunedTree.printTree(prunedTree.getRoot(),-1);
		}
		System.out.println("2. Variance Impurity Heuristic");
		Details trainSet2=new Details(training);
		decisionTree tree2=new decisionTree(trainSet2,true);
		Details valSet2=new Details(validation);
		Details testSet2 = new Details(test);
		System.out.println("Decision Tree constructed using Training Set:"+training);
		double accuracy1=(double) Math.round(tree2.accuracy(testSet2)*10000)/100;
		System.out.println("Accuracy of"+test+"before pruning:"+accuracy1+"%");
		decisionTree secondTree1=new decisionTree(tree2);
		decisionTree prunedTree1=secondTree1.pruning(L,K,valSet2);
		accuracy=(double)Math.round(prunedTree1.accuracy(testSet2)*10000)/100;
		System.out.println("Accuracy of pruned decision tree on Test Set: "+test+": "+accuracy1+"%");
		if(print.equals("yes")){
			prunedTree.printTree(prunedTree1.getRoot(),-1);
		}
	}
}
