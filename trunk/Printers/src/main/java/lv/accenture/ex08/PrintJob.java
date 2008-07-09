package lv.accenture.ex08;

public class PrintJob{
	
	private String jobName;
	private int pages;
	
	public PrintJob(String jN,int p) {
		jobName = jN;
		pages = p;
	}
	
	public String getName()
	{
		return jobName;
	}
	
	public int getPages()
	{
		return pages;
	}

}
