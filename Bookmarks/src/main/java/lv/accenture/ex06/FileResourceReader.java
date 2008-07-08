package lv.accenture.ex06;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class FileResourceReader {
	public static void main(String[] args) throws IOException
	{
		FileResourceReader reader = new FileResourceReader();
		List<String> lines = reader.readLines("asian.rdf");
		
		int i = 0;
		for(String line:lines){
			System.out.print("LINE[" + (++i) + "]=" + line);
		}

	}
	
	public List<String> readLines(String name) throws IOException {
		FileInputStream fis = new FileInputStream("src/main/resources/retiman/" + name);
		InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
		BufferedReader br = new BufferedReader(isr);
		List<String> result = new ArrayList<String>();
		String line = null;
		while ((line = br.readLine())!= null) {
			result.add(line);
		}
		return result;
	}
}
