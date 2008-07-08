package lv.accenture.ex06;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexExtractor implements IExtractor {

	public List<Bookmark> extractBookmarks(String resource) throws IOException {
		List<Bookmark> result = new ArrayList<Bookmark>();
		FileResourceReader reader = new FileResourceReader();
		List<String> lines = reader.readLines(resource);
		
		boolean test = false;

		Bookmark bookmark = new Bookmark();
		
		for (int i = 0, k=0; i < lines.size(); i++) {
			
			String line = lines.get(i);
			Pattern p0 = Pattern.compile("<item rdf:about=\"([^\"]+)\">");
			Matcher m0 = p0.matcher(line); 
			if (m0.matches()) {
				//k++;
				//if(k!=1)
				//{
					//result.add(bookmark);
					//bookmark = new Bookmark();
				//}
					
				String url = m0.group(1);				
				System.err.println("url = " + url);
				
				bookmark.setUrl(url);
				bookmark.setUid(Integer.toHexString(url.hashCode()));
				
				test = true;
			}
			if(test)
			{
				//TITLE
				line = lines.get(i);
				Pattern p1 = Pattern.compile("<title>([^\"]+)</title>");
				Matcher m1 = p1.matcher(line);
				
				if (m1.matches()) {
					
					String title = m1.group(1);				
					System.err.println("title = " + title);
					
					bookmark.setTitle(title);
				}
				
				//DESCRIPTION
				Pattern p2 = Pattern.compile("<description>([^\"]+)</description>");
				Matcher m2 = p2.matcher(line);
						
				if (m2.matches()) {
					String description = m2.group(1);				
					System.err.println("description = " + description);
					
					bookmark.setDescription(description);
				}
			
				//CREATOR
				Pattern p3 = Pattern.compile("<dc:creator>(.+)</dc:creator>");
				Matcher m3 = p3.matcher(line);
						
				if (m3.matches()) {
					String creator = m3.group(1);				
					System.err.println("creator = " + creator);
					
					bookmark.setCreator(creator);
				}
				
				//YEAR				
				Pattern p4 = Pattern.compile("<dc:date>(.+)</dc:date>");
				Matcher m4 = p4.matcher(line);
										
				if (m4.matches()) {			
					String s = line.toString();
					String year = s.substring(9, 13);				
					System.err.println("year = " + year);
					
					bookmark.setYear(year);
				}
				
				//TAGS
				Pattern p5 = Pattern.compile("<dc:subject>(.*)</dc:subject>");
				Matcher m5 = p5.matcher(line);
										
				if (m5.matches()) {
					
					String s = line.toString();
					s = s.replace("<dc:subject>","");
					s = s.replace("</dc:subject>","");
					String[] arr = s.split(" ");
					
					List<String> tags = new ArrayList<String>();
					
					for(int ii=0;ii<arr.length;ii++)
						tags.add("\""+arr[ii]+"\"");
											
					System.err.print("tags = ");
					for(int ii=0;ii<tags.size();ii++)				
						System.err.print(arr[ii] + " ");
					
					bookmark.setTags(tags);
					
					result.add(bookmark);
					bookmark = new Bookmark();
				}
								
			}
		}
		//result.add(bookmark);
		return result;
	}

}
