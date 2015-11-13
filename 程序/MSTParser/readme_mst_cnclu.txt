    by wangyaru 2015.2.6
------------------------------
使用文档：
1.将MSTParser的路径准备好。
2.打开cmd，cmd到该MSTParser下。（桌面路径 C:\Users\lenovo\Desktop\MSTParser）
3.编译命令：javac -classpath ".;lib/trove.jar" mstparser/DependencyParser.java
  编译完成之后会生成.class文件（注意：javac 将java文件编译成class字节码文件，字节码文件本身是二进制文件，但是不能被系统直接执行，而是需要用java虚拟机解释执行,一个java文件可以生成多个class；最后通过java 运行class文件。）
4.训练命令：
  java -classpath ".;lib/trove.jar" -Xmx1800m mstparser.DependencyParser train train-file:data/train.ulab model-name:dep.model
运行完之后，会显示有多少个特征实例，会显示有多少个特征。会显示有多少次迭代，每次迭代处理了多少实例，还有一个参数可能是花了多少时间吧。确实是花了多少时间，单位是毫秒。
5.测试命令：java -classpath ".;lib/trove.jar" -Xmx1800m mstparser.DependencyParser test model-name:dep.model test-file:data/test.ulab output-file:out.txt
output中文件有点杂乱。不是一行一行地那种。可以再Uedit中看，不是杂乱的。DOS界面显示运行时的参数。
6.评估命令:java -classpath ".;lib/trove.jar" -Xmx1800m mstparser.DependencyParser eval gold-file:data/test.ulab
显示LAS,和CA以及各种运行的参数。正确率计算的时候带标点。作者没有提供不带标点的正确率。带标点的正确率肯定比较低。
7.out.txt的内容用uedit看很好看，格式很漂亮。
8.此MSTParser可以再Eclipse中 重新部署。部署只要把java全部拷贝过去，添加jar即可。但是到目前为止，还没有运行，道理上讲，应该是可以的。而且，感觉应该可以把trove去掉，只是可能，没有尝试。