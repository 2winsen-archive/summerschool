package demo.jdbc.model;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import com.mysql.jdbc.PreparedStatement;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;

public class Bookmark {
	
	
	public void setFileAttachment(String fileName)
	{
		
		FileInputStream fis = null;
		File file = new File("src/main/resources/" + fileName);
		PreparedStatement ps = null;
	    ByteArrayInputStream bs = null;
		
	    byte[] buf = null;
	    try {
			fis = new FileInputStream(file);
			
            try {
            	
				buf = new byte[fis.available()];
				
			} catch (IOException e) {
				e.printStackTrace();
			} 
            for (int i = 0; i< file.length(); i ++)
            {
                try {
					buf[i] = (byte) fis.read();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            }			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
        setAttachment(buf);
		
	}

	
	/**
	 * An id from database; included because of technical reasons, 
	 * not because bookmarks really need id's. 
	 */
	protected long id;
	
	/**
	 * The URL address itself as a string
	 */
	protected String url;
	
	/**
	 * The title of the resource
	 */
	protected String title;
	
	/**
	 * The fingerprint of the Web page content
	 * (used to discover different URLs that lead
	 * to identical documents)
	 */
	protected long checksum;
	
	/**
	 * The date when the resource was last changed
	 */
	protected Date lastModified;
	
	/**
	 * The content of the Web page
	 */
	protected String content;
	
	
	/**
	 * User-given description of the Web page
	 */
	protected String description;

	/**
	 * User-provided "tags" (short keywords or characteristic
	 * strings) identifying the resource. 
	 */
	
	protected List<Tag> tags;
	
	protected byte[] attachment = null;
	
	
	public byte[] getAttachment() {
		return this.attachment;
	}

	public void setAttachment(byte[] attachment) {
		this.attachment = attachment;
	}

	public Bookmark() {
	}
	
	public Bookmark(String url, String title) {
		this.url = url;
		this.title = title;

	}


	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public List<Tag> getTags() {
		return tags;
	}

	public void setTags(List<Tag> tags) {
		this.tags = tags;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public long getChecksum() {
		return checksum;
	}

	public void setChecksum(long checksum) {
		this.checksum = checksum;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Date getLastModified() {
		return lastModified;
	}

	public void setLastModified(Date lastModified) {
		this.lastModified = lastModified;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	@Override
	public String toString() {
		return "<" + url + ">";
	}
	
}
