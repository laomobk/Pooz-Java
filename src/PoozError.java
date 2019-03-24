public class PoozError
{
	public static void cause(String type, String info){
		System.out.println(type + ":");
		System.out.println("\t" + info);
	
	}
	
	private static void exit(){
		System.exit(1);
	}
}
