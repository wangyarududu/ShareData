package hittoconll;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ReadConll {
	public static void main(String[] args) throws IOException {
		String filename="D:\\HIT-LTP\\project4\\ltp-3.2.0\\tools\\train\\Release\\example-train.conll";
		File f=new File(filename);
	    FileInputStream in = new FileInputStream(f);
		BufferedReader reader = new BufferedReader(new InputStreamReader(in,"UTF-8"));
		String temp="";
//		while((temp=reader.readLine())!=null){
//			System.out.println(temp);
//		}
		int i =0;
		while( i<200){
			System.out.println(reader.readLine());
			i++;
		}
		in.close();
		
	}

}
