package hittoconll;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class HITtoCONLL {
	public static int connected_cyclic(String[] tokens,String [] pos, String[] deps) { 
    int ii=0;
    for(ii=0;ii<tokens.length;ii++){
    	int current = Integer.parseInt(deps[ii])-1;
    	int while_cont=0;
    	if(!pos[ii].equals("wp")){
    		while(current!=-1&&while_cont<=tokens.length+2){
        		if(current<-1||current > tokens.length-1){
        			errorNum++;
        			errorInfo="����"+errorNum+":"+"���ڵ��±�Խ�磨�������þ䣩�� �μ��� "+(ii+1)+"��";
        			//System.out.print("����"+errorNum+":"+"���ڵ��±�Խ�磨�������þ䣩�� �μ��� "+(ii+1)+"��");
            		return 0;
        		}
        			
        		if(current==ii){
        			errorNum++;
        			errorInfo="����"+errorNum+":"+"�л����������þ䣩���μ���   "+(ii+1)+"��";
        			//System.out.print("����"+errorNum+":"+"�л����������þ䣩���μ���   "+(ii+1)+"��");
        			return 0;
        		}
        		if(while_cont==tokens.length+2){
        			errorNum++;
        			errorInfo="����"+errorNum+":"+"����ͨ���������þ䣩���μ���  "+(ii+1)+" ��"+tokens[ii]+"��"+"��";
        			//System.out.print("����"+errorNum+":"+"����ͨ���������þ䣩���μ���  "+(ii+1)+" ��"+tokens[ii]+"��"+"��");
        			return 0;
        	    }
        		current = Integer.parseInt(deps[current])-1;
        		while_cont++;
        		
        }
    	}
    	
		
		
	}
    return 1;
	}	
	public static int projective_deprel(String[] tokens,  String[] pos,String[] deps,String[] rels) {
		int ii=0,jj=0, head_id=0,head_j=0,small=0,large=0;
		for( ii=0;ii<tokens.length;ii++){
			head_id = Integer.parseInt(deps[ii])-1;
			if(head_id<-1||head_id>tokens.length-1){
				errorNum++;
				errorInfo="����"+errorNum+":"+" ���ڵ��±�Խ�磨�������þ䣩���μ��� "+ (ii+1) +"��";
				//System.out.print("����"+errorNum+":"+" ���ڵ��±�Խ�磨�������þ䣩���μ��� "+ (ii+1) +"��");//λ��
				return 0;
			}
			if(!pos[ii].equals("wp")){
				small = ii> head_id ? head_id : ii;
				 large = ii<head_id ? head_id : ii;
				 for( jj = small+1;jj<=large-1;jj++){
					 head_j = Integer.parseInt(deps[jj])-1;
					if(!pos[jj].equals("wp")){
						if(head_j<small|| head_j > large){
							errorNum++;
							errorInfo="����"+errorNum+":"+"��ͶӰ���������þ䣩���μ���   "+(ii+1)+"and"+(jj+1)+"��";
							//System.out.print("����"+errorNum+":"+"��ͶӰ���������þ䣩���μ���   "+(ii+1)+"and"+(jj+1)+"��");
							return 0;
						}
					}
					
				}
			}
			 
			if(rels[ii].equals(null)){
				errorNum++;
				errorInfo="����"+errorNum+":"+"�����ϵ���������������þ䣩���μ���  "+(ii+1)+"��";
				//System.out.print("�����ϵ���������������þ䣩���μ���  "+(ii+1)+"��");
				return 0;
			}
				
		}
		return 1;     
	}

	
	

	public void Cyclic(String[] tokens, String[] pos, String[] deps,
			String[] rels) {

	}
	 static int  errorNum=0;
	 static String errorInfo="";
	public static void main(String[] args) throws IOException {
		String filename = "holdout";   //������ϵ��ļ��� train test holdout
		String fileRoot = "D:\\��һѧϰ\\���Ժ\\�����ע����\\���еı�ע���� - �������-����2\\";
		String fileRootName=fileRoot+filename;
		File  file = new File(fileRootName);
		String[] fileName = file.list();
		int fileNum = fileName.length;
		String fileSaveName = fileRoot+filename+".conll";	
		
		FileOutputStream o=new FileOutputStream(fileSaveName);
		BufferedWriter out = new BufferedWriter(new OutputStreamWriter(o,"UTF-8"));
		int instNum=0;
		
		String logfile = fileRoot+filename+"-log.txt";
		BufferedWriter logout = new BufferedWriter(new FileWriter(logfile));   //����Щ��ע���������ĵ��������־��
		
		logout.write("By Yaru Wang\r\n");
		logout.write("-------------------------------\r\n");
		
		int errorFileNum=0;
		String errorFile =""; 
		
		for(int i = 0;i<fileNum;i++){
			String fileReadName= fileRootName+"\\"+fileName[i];
			BufferedReader in= null;
			try{
			in = new BufferedReader(new FileReader(fileReadName));
			}
			catch (Exception e){
				System.out.println(e.toString());
			}
			String str="";
			String[] info=new String[3];//�ֱ�洢��һ�С��ڶ��С�������
			String[] tokens;
			String[] pos;
			String[] deps;
			String[] rels;
			int line_cnt=0;
			while((str=in.readLine())!=null){
				info[0]= str;
				info[1]=in.readLine();
				info[2]=in.readLine();
				line_cnt=line_cnt+3;
				info[0]=info[0].replaceAll("/ ", "/none ");//���û�д��Ա�ǵĻ� �͸�ֵΪnone
				String[] temp0= info[0].split("[ ]+");
				String [] temp= info[2].split("[		]+");
				int tokensNum= temp0.length;
				int depsNum = temp.length;
				tokens = new String[tokensNum];
				pos = new String[tokensNum];
				deps = new String[tokensNum];
				rels = new String[tokensNum];	
				for(int j=0;j<tokensNum;j++){
					//����token��pos
					String [] split1 = new String[2];
					split1=temp0[j].split("/");
					tokens[j]= split1[0];
					try{
					pos[j]= split1[1];
					}
					catch(Exception e){
						System.out.println(temp0[j]);
					}
				}
				for(int j=0; j<depsNum;j++){
					//����dep��rel
					int f1,f2,c1,c2,r1,r2;
					f1=temp[j].indexOf("[")+1;
					f2=temp[j].indexOf("]");
					c1=temp[j].indexOf("_[")+1+1;
					c2=temp[j].indexOf("]",c1);
					r1=temp[j].indexOf("(")+1;
					r2=temp[j].indexOf(")");
					String head="";
					String child="";
					String rel="";
					if(temp[j].equals("")){
						continue;
					}
						 head=temp[j].substring(f1,f2);
						 child=temp[j].substring(c1,c2);
						 rel=temp[j].substring(r1,r2);
					
					
					
					
					int headnum = Integer.parseInt(head);
					if(headnum==(tokensNum+1))
						headnum=0;
				    int childnum = Integer.parseInt(child);
				    deps[childnum-1]=String.valueOf(headnum);
				    rels[childnum-1]=rel;
				}
				int flag=0; //��ǰ������û�д���
				for(int j=0;j<tokensNum;j++){
					if(pos[j].equals("wp"))
					{
						deps[j]=String.valueOf(j+1);//�����ŵĸ��ڵ���-1���߱��
						rels[j]="wp";
					}
					if(pos[j].equals("ws")&&(deps[j]==null))
					{
						pos[j]="wp";                //���ڴ�����ws������ȴû�б�ע��ϵ�Ĵʣ�ֱ�Ӱ������Ϊ�����š�
						deps[j]=String.valueOf(j+1);//�����ŵĸ��ڵ��Ǳ���
						rels[j]="ws";
					}
					if((deps[j]==null)){
						flag=1;
						errorNum++;
						logout.write("����"+errorNum+":"+"�����бߵ���Ŀ����ȷ���������þ䣩,λ��Ϊ���ļ�"+fileReadName+"�еĵ�"+(line_cnt/3)+"��\r\n");
						//System.out.println("����"+errorNum+":"+"�����бߵ���Ŀ����ȷ���������þ䣩,λ��Ϊ���ļ�"+fileReadName+"�еĵ�"+(line_cnt-2)+"��");
						instNum--;
//						if(!errorFile.equals(fileReadName)){            //���û�б�ע��ɵ��ĵ�
//							if(errorFileNum==0){
//								logout.write("û�б�ע��ɵ��ĵ�\r\n");
//							}
//							
//							errorFileNum++;
//						    errorFile=fileReadName;
//							logout.write(errorFile+"\r\n");
//						}
						
					}
					else if((rels[j].equals("NO"))){
						flag=1;
						errorNum++;
						logout.write("����"+errorNum+":"+"��ϵ����û�б�ע�꣨�������þ䣩,λ��Ϊ���ļ�"+fileReadName+"�еĵ�"+(line_cnt/3)+"��\r\n");
						//System.out.println("����"+errorNum+":"+"��ϵ����û�б�ע�꣨�������þ䣩,λ��Ϊ���ļ�"+fileReadName+"�еĵ�"+(line_cnt/3)+"��");
						instNum--;
//						if(!errorFile.equals(fileReadName)){
//							if(errorFileNum==0){
//								logout.write("û�б�ע��ɵ��ĵ�\r\n");
//							}
//							
//							errorFileNum++;
//						    errorFile=fileReadName;
//							logout.write(errorFile+"\r\n");
//						}
						
					}
					//System.out.println(((j+1)+"\t"+tokens[j]+"\t"+"_"+"\t"+pos[j]+"\t"+"_"+"\t"+"_"+"\t"+deps[j]+"\t"+rels[j]+"\t"+"_"+"\t"+"_"+"\t"+"\r\n"));
					
				}
				    if(flag==0){
				    	if(connected_cyclic(tokens,pos,deps)==0){
				    		logout.write(errorInfo+"λ��Ϊ���ļ�"+fileReadName+"�еĵ�"+(line_cnt/3)+"��\r\n");
							//System.out.println( "λ��Ϊ���ļ�"+fileReadName+"�еĵ�"+(line_cnt/3)+"��" );
							instNum--;
//							if(!errorFile.equals(fileReadName)){
//								if(errorFileNum==0){
//									logout.write("û�б�ע��ɵ��ĵ�\r\n");
//								}
//								
//								errorFileNum++;
//							    errorFile=fileReadName;
//								logout.write(errorFile+"\r\n");
//							}
						}
				    	else  if(projective_deprel(tokens,pos,deps,rels)==0){
				    		logout.write(errorInfo+ "λ��Ϊ���ļ�"+fileReadName+"�еĵ�"+(line_cnt/3)+"��\r\n" );
	                    	//System.out.println( "λ��Ϊ���ļ�"+fileReadName+"�еĵ�"+(line_cnt/3)+"��" );
	                    	instNum--;
//	                    	if(!errorFile.equals(fileReadName)){
//								if(errorFileNum==0){
//									logout.write("û�б�ע��ɵ��ĵ�\r\n");
//								}
//								
//								errorFileNum++;
//							    errorFile=fileReadName;
//								logout.write(errorFile+"\r\n");
//							}
						}
				    	else
				    	{
							for(int j=0;j<tokensNum;j++){
								out.write(((j+1)+"\t"+tokens[j]+"\t"+"_"+"\t"+pos[j]+"\t"+"_"+"\t"+"_"+"\t"+deps[j]+"\t"+rels[j]+"\t"+"_"+"\t"+"_"+"\t"+"\r\n"));
							}
							out.write("\r\n");
				    	}
				    }
					
					
				
				
				
				instNum++;
			}
			in.close();
		}
		if(errorNum==0){
			logout.write("�����ļ�����ע��ȷ��\r\n");
		}
		System.out.println(instNum);          //�����ļ��ľ��Ӹ���
		logout.write("����"+instNum+"�仰");
		out.close();
		logout.close();
	}

}
