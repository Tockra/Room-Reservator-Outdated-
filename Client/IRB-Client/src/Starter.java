public class Starter {

	public static void main(String[] args) {
		try {
			ConsoleListener cl = new ConsoleListener(args[0]);
			cl.listen();
		}
		catch(IllegalArgumentException e) {
			System.out.println(e.getMessage());
		}

	}

	public static void test() {
		
	}
	

}
