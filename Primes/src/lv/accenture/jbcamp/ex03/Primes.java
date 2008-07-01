package lv.accenture.jbcamp.ex03;

public class Primes {

	public static void main(String[] args) {

		if (args.length < 1) {
			System.err.println("Give one number as command-line argument");
			System.exit(0);
		}

		int nValues = Integer.parseInt(args[0]);
		outer:
		for (int i = 2; i <= nValues; i++) {

			for (int j = 2; j <= Math.sqrt(i); j++) {
				if (i % j == 0) {
					continue outer;
				}
			}		
		
			System.out.println(i);

		}

	}

}
