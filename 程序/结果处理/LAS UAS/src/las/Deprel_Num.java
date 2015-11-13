package las;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

public class Deprel_Num {
	public static void main(String[] args) throws IOException {
		String[] rel = {"SBV","VOB","ATT","ADV","CMP","DEI","DE","DI","MT","QUN","CNJ","COO","APP","POB","SIM","LAD","RAD","VV","IC","DC","IS","HED","DUP","RPT","OM","TRL","WP"};
		String filename1="C:\\Users\\lenovo\\Desktop\\ltpdownloadout.txt";	
		File f1=new File(filename1);	
	    FileInputStream in1 = new FileInputStream(f1); 
		BufferedReader reader1 = new BufferedReader(new InputStreamReader(in1,"UTF-8"));
		String str1="";		
		String[] sub_str;
		int sent_num=0, token_num=0,j;
		int[] addnum= new int[27];
		while((str1=reader1.readLine())!=null){		
			if(!str1.equals("")){			
			sub_str=str1.split("\t");
			for(j=0;j<27;j++){
				if(sub_str[9].equals(rel[j])){
					addnum[j]++;
					break;
				}
			}  
			if(j==24) System.out.println(str1);
			if(j==27) System.out.println(sub_str[9]);
			}
			else sent_num++;
		}
		for(int aa=0;aa<27;aa++){
			System.out.println(rel[aa]+" "+addnum[aa]);
			token_num+=addnum[aa];
		}
		System.out.println(token_num);
		in1.close();
		
	}

}
