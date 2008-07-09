package lv.accenture.ex08;

public class TestPrinter {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		Producer p1 = new Producer(3, 5, 200, "Fred");
		Producer p2 = new Producer(3, 5, 1000, "Simon");
		Producer p3 = new Producer(3, 25, 200, "Elizabeth");
		
		Printer printer = Printer.getInstance();
		Thread t = new Thread(printer);
		System.out.println("Print manager is starting up");
		
		Thread t1 = new Thread(p1);
		Thread t2 = new Thread(p2);
		Thread t3 = new Thread(p3);
		
		t.start();
		t1.start();
		t2.start();
		t3.start();
		
		try {
			t1.join();
			t2.join();
			t3.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		printer.halt();
		System.out.println("Print manager is halted");
	}

}
