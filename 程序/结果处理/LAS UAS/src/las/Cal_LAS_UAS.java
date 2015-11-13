package las;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

public class Cal_LAS_UAS {
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
		int token_num=0,head_crt=0,head_rel_crt=0;
		String[] sub_str1;
		String[] sub_str2;
		int sent_num=0;
		double LAS,UAS;
		while(((str1=reader1.readLine())!=null)&&((str2=reader2.readLine())!=null)){
			
			if(!str1.equals("")){
			token_num++;
			sub_str1=str1.split("\t");
			sub_str2=str2.split("\t");
			if(sub_str1[6].equals(sub_str2[6])){
				head_crt++;
				if(sub_str1[7].equals(sub_str2[7]))
					head_rel_crt++;
			}
			}
			else sent_num++;
		}
		UAS=(double)head_crt/(double)token_num;
		LAS=(double)head_rel_crt/token_num;
		System.out.println("共有"+sent_num+"句话：");
		System.out.println("带标点符号的UAS正确率为："+UAS+"("+head_crt+"/"+token_num+")");
		System.out.println("带标点符号的LAS正确率为："+LAS+"("+head_rel_crt+"/"+token_num+")");
		in1.close();
		in2.close();
	}

}
