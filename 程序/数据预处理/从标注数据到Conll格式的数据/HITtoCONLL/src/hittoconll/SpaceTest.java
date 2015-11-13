package hittoconll;

public class SpaceTest {
	public static void main(String[] args) {
		//String str="hello,   nice";
		//String[] split=str.split("[ ]+");
		String str="hello[ ]+nice";
		String[] split = str.split("\\[ ]+");
		System.out.println(split.length);
	}

}
