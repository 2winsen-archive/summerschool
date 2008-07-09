package lv.accenture.ex07;

public class TestRunner {

	public static void main(String[] args) {
		HelloRunner r = new HelloRunner();
		Thread t1 = new Thread(r, "R1");
		Thread t2 = new Thread(r, "R2");
		Thread t3 = new Thread(r, "R3");
		Thread t4 = new Thread(r, "R4");
		t1.start();
		t2.start();
		t3.start();
		t4.start();
	}
}

class HelloRunner implements Runnable {
	int counter = 0;

	public void run() {

		synchronized (this) {
			while (counter < 150) { //Simultaneously if 1500
				//synchronized (this) {
					System.out.println("Hello #" + counter + " from "
							+ Thread.currentThread().getName());
	
					counter++;
				//}	
			}
		}			
	}
}
