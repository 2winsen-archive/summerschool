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
			try
			{
				PrintJob job = new PrintJob(producerName, sizeOfJobs);
				printer.addJob(job);
				//Thread.sleep(delayBetweenJobs);
			}
			catch(Exception FullQueueEception)
			{
				System.err.println("Queue is FULL");
			}
			
		}
	}

}
