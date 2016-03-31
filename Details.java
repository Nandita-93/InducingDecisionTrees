import java.util.*;
import java.io.*;
public class Details {
	HashMap<Integer,String> attr;
	int size;
	HashMap<Integer,HashMap<String,Integer>> records;
//default constructor	
	Details(){
		attr=new HashMap<Integer,String>();
		records=new HashMap<Integer, HashMap<String,Integer>>();
		size=0;
	}
	
//parameterized constructor	
	Details(String path) throws IOException{
		BufferedReader br = new BufferedReader(new FileReader(path));
		size=0;
		String l="";
		int i=0;
		attr=new HashMap<Integer,String>();
		records = new HashMap<Integer,HashMap<String,Integer>>();
		boolean first=true;
		while((l=br.readLine())!=null&&l.length()!=0){
			if(first){
				addAttrNames(l);
				first=false;
			}
			else{
				addRecords(l,i);
				size++;
				i++;
			}
		}
		br.close();
	}
	
	HashMap<Integer,HashMap<String,Integer>> getRecords(){
		return records;
	}
	
	HashMap<Integer,String> getAttrNames(){
		return attr;
	}
	
	int getSize(){
		return size;
	}

	String[] splitFile(String line){
		return line.split(",");
	}
	void addAttrNames(String line){
		String[] arr = splitFile(line);
		for(int i=0;i<arr.length;i++){
			attr.put(i, arr[i]);
		}
	}
	
	void addRecords(String line, int row){
		HashMap<String,Integer> attr1=new HashMap<String,Integer>();
		for(int j=0,i=0;i<attr.size();j=j+2,i++){
			attr1.put(attr.get(i), Character.getNumericValue(line.charAt(j)));
		}
		records.put(row, attr1);
	}
	
	
}
