    by wangyaru 2015.2.6
------------------------------
ʹ���ĵ���
1.��MSTParser��·��׼���á�
2.��cmd��cmd����MSTParser�¡�������·�� C:\Users\lenovo\Desktop\MSTParser��
3.�������javac -classpath ".;lib/trove.jar" mstparser/DependencyParser.java
  �������֮�������.class�ļ���ע�⣺javac ��java�ļ������class�ֽ����ļ����ֽ����ļ������Ƕ������ļ������ǲ��ܱ�ϵͳֱ��ִ�У�������Ҫ��java���������ִ��,һ��java�ļ��������ɶ��class�����ͨ��java ����class�ļ�����
4.ѵ�����
  java -classpath ".;lib/trove.jar" -Xmx1800m mstparser.DependencyParser train train-file:data/train.ulab model-name:dep.model
������֮�󣬻���ʾ�ж��ٸ�����ʵ��������ʾ�ж��ٸ�����������ʾ�ж��ٴε�����ÿ�ε��������˶���ʵ��������һ�����������ǻ��˶���ʱ��ɡ�ȷʵ�ǻ��˶���ʱ�䣬��λ�Ǻ��롣
5.�������java -classpath ".;lib/trove.jar" -Xmx1800m mstparser.DependencyParser test model-name:dep.model test-file:data/test.ulab output-file:out.txt
output���ļ��е����ҡ�����һ��һ�е����֡�������Uedit�п����������ҵġ�DOS������ʾ����ʱ�Ĳ�����
6.��������:java -classpath ".;lib/trove.jar" -Xmx1800m mstparser.DependencyParser eval gold-file:data/test.ulab
��ʾLAS,��CA�Լ��������еĲ�������ȷ�ʼ����ʱ�����㡣����û���ṩ����������ȷ�ʡ���������ȷ�ʿ϶��Ƚϵ͡�
7.out.txt��������uedit���ܺÿ�����ʽ��Ư����
8.��MSTParser������Eclipse�� ���²��𡣲���ֻҪ��javaȫ��������ȥ�����jar���ɡ����ǵ�ĿǰΪֹ����û�����У������Ͻ���Ӧ���ǿ��Եġ����ң��о�Ӧ�ÿ��԰�troveȥ����ֻ�ǿ��ܣ�û�г��ԡ�