package lv.accenture.ex06;

import java.util.List;

public class Bookmark {
	
	private String url;
	private String title;
	private String description;
	
	private String uid;

	private String creator;
	private String year;
	private List<String> tags;
	
	
	public String getUrl() {
		if(url == null)
			return "";
		else
			return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getTitle() {
		if(title == null)
			return "";
		else
			return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getDescription() {
		if(description == null)
			return "";
		else
			return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getCreator() {
		if(creator == null)
			return "";
		else
			return creator;
	}
	public void setCreator(String creator) {
		this.creator = creator;
	}
	public String getYear() {
		if(year == null)
			return "";
		else
			return year;
	}
	public void setYear(String year) {
		this.year = year;
	}
	public List<String> getTags() {
		return tags;
		
	}
	public void setTags(List<String> tags) {
		this.tags = tags;
	}
	public String getUid() {
		if(uid == null)
			return "";
		else
			return uid;
	}
	public void setUid(String uid) {
		this.uid = uid;
	}
	public String toString()
	{
		String s = 
			"{" + " type : \"Bookmark\",\n"+
			"  label : "+ "\"" + getTitle() + "\",\n"+
			"  url : " + "\"" + getUrl() + "\",\n"+
			"  description : " + "\"" + getDescription() + "\",\n"+
			"  uid : " + "\"" + getUid() + "\",\n"+
			"  creator : " + "\"" + getCreator() + "\",\n"+
			"  year : " + "\"" + getYear() + "\",\n"+
			"  tags : " + getTags() + "\n"+
			"},\n";
		
		
		return s;
		
		
	}
}
