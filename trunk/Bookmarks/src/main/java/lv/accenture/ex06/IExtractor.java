package lv.accenture.ex06;

import java.io.IOException;
import java.util.List;

public interface IExtractor {
	
	public List<Bookmark> extractBookmarks(String resource) throws IOException;

}
