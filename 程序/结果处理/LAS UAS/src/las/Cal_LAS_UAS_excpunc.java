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

public class Cal_LAS_UAS_excpunc {
	public static void main(String[] args) throws IOException {
		String filename1="C:\\Users\\lenovo\\Desktop\\out_o2_3_conll.txt";
		String filename2="C:\\Users\\lenovo\\Desktop\\test-punc.conll";
		File f1=new File(filename1);
		File f2 = new File(filename2);
	    FileInputStream in1 = new FileInputStream(f1);
	    FileInputStream in2 = new FileInputStream(f2);
		BufferedReader reader1 = new BufferedReader(new InputStreamReader(in1,"UTF-8"));
		BufferedReader reader2 = new BufferedReader(new InputStreamReader(in2,"UTF-8"));
		String str1="",str2="";		
		int word_num=0,head_crt=0,head_rel_crt=0;
		String[] sub_str1;
		String[] sub_str2;
		int sent_num=0;
		double LAS,UAS;
		int word_num_sent=0;
		int head_crt_sent=0,head_rel_crt_sent=0;
		int word_previous=0;
	//	int[] detailinfo= new int[3];
		HashMap<Integer,int[]> sent_l_word_crt = new HashMap<Integer,int[]>();//按照句子的长度存放，句子的长度，头结点词的个数，头结点和依存关系都正确的个数
		while(((str1=reader1.readLine())!=null)&&((str2=reader2.readLine())!=null)){
			
			if(!str1.equals("")){			
			sub_str1=str1.split("\t");
			if(!sub_str1[3].equals("wp")){
				word_num++;	
				word_num_sent++;
			sub_str2=str2.split("\t");			
			if(sub_str1[6].equals(sub_str2[6])){
				head_crt++;
				head_crt_sent++;
				if(sub_str1[7].equals(sub_str2[7])){
					head_rel_crt++;
					head_rel_crt_sent++;
				}
					
				
			}
			}
			}
			else 
				{
				
				if(sent_l_word_crt.get(word_num_sent)!=null)
				{
					int[] detailinfo= new int[3];
					detailinfo[0]=sent_l_word_crt.get(word_num_sent)[0]+word_num_sent;
					detailinfo[1]=sent_l_word_crt.get(word_num_sent)[1]+head_crt_sent;
					detailinfo[2]=sent_l_word_crt.get(word_num_sent)[2]+head_rel_crt_sent;
					sent_l_word_crt.put(word_num_sent, detailinfo);
				}
				else{
					int[] detailinfo= new int[3];
					detailinfo[0]=word_num_sent;
					detailinfo[1]=head_crt_sent;
					detailinfo[2]=head_rel_crt_sent;
					sent_l_word_crt.put(word_num_sent, detailinfo);
				}
					
				sent_num++;
				word_num_sent=0;
				head_crt_sent=0;
				head_rel_crt_sent=0;
				}
			
		}
		for(Iterator<Entry<Integer, int[]>> i=sent_l_word_crt.entrySet().iterator();i.hasNext();){
		   Entry<Integer, int[]> entry = i.next();
		   int key = entry.getKey();
		   int[] value = entry.getValue();
		   System.out.println(key+"   "+value[0]+"    "+value[1]+"    "+value[2]);
				   
		}
		UAS=(double)head_crt/(double)word_num;
		LAS=(double)head_rel_crt/word_num;
		System.out.println("共有"+sent_num+"句话：");
		System.out.println("不带标点符号的UAS正确率为："+UAS+"("+head_crt+"/"+word_num+")");
		System.out.println("不带标点符号的LAS正确率为："+LAS+"("+head_rel_crt+"/"+word_num+")");
		in1.close();
		in2.close();
	}

}
