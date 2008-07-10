package lv.accenture.ex08;

public class Printer implements Runnable {

	private Queue printQueue;
	private boolean stateIsRunning;
	private final int MAX = 10;

	private static Printer instance = new Printer();

	private Printer() {
		printQueue = new CircularQueue(MAX);
		stateIsRunning = true;
	}

	public static Printer getInstance() {
		return instance;
	}

	public void halt() {
		stateIsRunning = false;
	}

	public synchronized void addJob(PrintJob job) throws FullQueueException {
		this.notify();

		printQueue.addBack(job);
	}

	private synchronized PrintJob getJob() throws EmptyQueueException {
		if(printQueue.isEmpty()) {
			try {
				System.err.println("Waiting on a job to print");
				this.wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		PrintJob job = (PrintJob) printQueue.getFront();
		printQueue.removeFront();

		return job;
	}

	@Override
	public void run() {

		while (stateIsRunning || !printQueue.isEmpty()) {
			PrintJob curr = getJob();
			System.out.println("  C: Starting job" + " \'" + curr.getName() + "\'");
			for (int i = 0; i < curr.getPages(); i++) {
				try {
					Thread.sleep(500);
				} catch (InterruptedException exSleep) {
					exSleep.printStackTrace();
				}					
			}		
			System.out.println("  C: Completed job" + " \'" + curr.getName() + "\'");
		}
		System.err.println("P: The printer manager is halted.");

	}

}
