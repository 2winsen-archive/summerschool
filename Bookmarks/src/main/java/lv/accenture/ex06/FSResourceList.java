package lv.accenture.ex06;

import java.io.File;
import java.io.FilenameFilter;

/**
 * RDF resource list from file system
 */
public class FSResourceList implements IResourceList {
	
	private String directory;

	public FSResourceList(String directory) {
		this.directory = directory;		
	}
	
	
	public String[] getAllResources() {
		File fDirectory = new File(directory);
		return fDirectory.list(new FilenameFilter () {

			public boolean accept(File arg0, String arg1) {
				
				return arg1.endsWith(".rdf");
			}			
		});
	}
}
