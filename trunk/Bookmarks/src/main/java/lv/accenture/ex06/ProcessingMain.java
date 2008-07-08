package lv.accenture.ex06;

import java.io.IOException;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.BufferedWriter;
import java.util.ArrayList;
import java.util.List;

public class ProcessingMain {
	public static void main(String[] args) throws IOException {
		IResourceList rList = new FSResourceList("src/main/resources/retiman");
		String[] resources = rList.getAllResources();
		System.out.println("total # of tags is " + resources.length);

		
		List<Bookmark> book = new ArrayList<Bookmark>();
		RegexExtractor extractor = new RegexExtractor();
		for (int i = 0; i < resources.length; i++) {

			List<Bookmark> bookmarks = extractor.extractBookmarks(resources[i]);
			book.addAll(bookmarks);
		}
		writeJSON("target/bookmarks.js", book);
		
	}
	
	public static void writeJSON(String fileName, List<Bookmark> bookmarks) throws IOException {
		  FileOutputStream fis = new FileOutputStream(fileName);
		  OutputStreamWriter osw = new OutputStreamWriter(fis, "UTF-8");
		  BufferedWriter bw = new BufferedWriter(osw);
			    
		  bw.write("{\"items\" : [\n"); // write preamble of JSON file
		  for (Bookmark b: bookmarks) {
		    bw.write(b.toString()); // output each bookmark
		  }
		  bw.write("]}"); // write closing part of JSON
		  bw.close();
		  osw.close();
		  fis.close();
		}
}
