package las;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

public class Word_Num {
	public static void main(String[] args) throws IOException {
		String dic = "C:\\Users\\lenovo\\Desktop\\data2";
		File file = new File(dic);
		String[] filename = file.list();
		 FileInputStream in1= null; 
		String str1="";		
		int word_num=0,head_crt=0,head_rel_crt=0;
		String[] sub_str1= new String[10];	
		int sent_num=0;
		HashMap<Integer,Integer> hm = new HashMap<Integer,Integer>();
		int word_num_sent=0;
		for (int j =0; j<filename.length;j++){
			String filename1=dic+"\\"+filename[j];	
			File f1=new File(filename1);	
		    in1 = new FileInputStream(f1); 
			BufferedReader reader1 = new BufferedReader(new InputStreamReader(in1,"UTF-8"));
			
			while((str1=reader1.readLine())!=null){		
				if(!str1.equals("")){			
				sub_str1=str1.split("\t");
				if(!sub_str1[3].equals("wp")){
					word_num++;	
					word_num_sent++;
				}
				}
				else{
					
					
					if(hm.get(word_num_sent)!=null){
						hm.put(word_num_sent, hm.get(word_num_sent)+1);
					}
					else hm.put(word_num_sent, 1);
					sent_num++;
					word_num_sent=0;
				}
			}
		}	
		for(Iterator<Entry<Integer, Integer>> i=hm.entrySet().iterator();i.hasNext();){
			   Entry<Integer, Integer> entry = i.next();
			   int key = entry.getKey();
			   int value = entry.getValue();
			   System.out.println(key+"   "+value);
					   
			}
		System.out.println("共有"+sent_num+"句话");
		System.out.println("共有"+word_num+"个词");
		in1.close();
		
	}

}
