package lv.accenture.ex08;

public class Producer implements Runnable {
	
	private int sizeOfJobs;
	private int numberOfJobs;
	private int delayBetweenJobs;
	private String producerName;

	public Producer(int sOJ,int nOJ,int dBJ,String pN) {
		sizeOfJobs = sOJ;
		numberOfJobs = nOJ;
		delayBetweenJobs = dBJ;
		producerName = pN;
	}
	
	@Override
	public void run() {
		Printer printer = Printer.getInstance();
		for(int i=0;i<numberOfJobs;i++){
			PrintJob job = new PrintJob(producerName + "#" + i, sizeOfJobs);
			
			synchronized (this) {
				while (true) {
					try {
						printer.addJob(job);
						break;
					} catch (FullQueueException exFull) {
						try {
							Thread.sleep(50);
							continue;
						} catch (InterruptedException exSleep) {
							exSleep.printStackTrace();
						}
					}
				}
			}
			System.out.println("P: Adding job " + job.getName() + " to the queue");

			try {
				Thread.sleep(delayBetweenJobs);
			} catch (InterruptedException exSleep) {
				exSleep.printStackTrace();
			}
			
		}
	}

}
