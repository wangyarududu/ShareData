package hittoconll;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class CollectSent {
	public static void main(String[] args) throws IOException {
		
		String fileRootName="D:\\��һѧϰ\\���Ժ\\�����ע����\\���еı�ע���� - �������-����2\\train";
		File  file = new File(fileRootName);
		String[] fileName = file.list();
		int fileNum = fileName.length;
		String fileSaveName = fileRootName+"\\"+"�ĵ�����.txt";	
		FileOutputStream o=new FileOutputStream(fileSaveName);
		BufferedWriter out = new BufferedWriter(new OutputStreamWriter(o,"UTF-8"));
		for(int i=0; i<fileName.length;i++){
			String fileReadName= fileRootName+"\\"+fileName[i];
			BufferedReader in= null;
			try{
			in = new BufferedReader(new FileReader(fileReadName));
			}
			catch (Exception e){
				System.out.println(e.toString());
			}
			String str="";
			out.write(fileReadName+"\r\n");
			while((str=in.readLine())!=null){
				out.write(str+"\r\n");
			}
			out.write("\r\n");
			
		}
		
	}

}
