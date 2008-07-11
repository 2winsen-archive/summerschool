package demo.jdbc.dao;

import java.sql.SQLException;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

//import com.sun.java.util.jar.pack.Attribute.Layout.Element;

import demo.jdbc.model.Bookmark;

public class JdbcMain {
	public static void main(String[] params) throws SQLException {
		ApplicationContext ctx = new ClassPathXmlApplicationContext(
				"applicationContext.xml");
		FunnyBeanClass fb = (FunnyBeanClass) ctx.getBean("funnyBean");
		System.out.println("fb = " + fb);
		BookmarkDAO dao = (BookmarkDAO) ctx.getBean("bookmarkDAO");		
		Bookmark b = new Bookmark("http://www.aldaily.com", "Arts and Letters Daily");	
		
		b.setFileAttachment("Element.gif");
		
		dao.save(b);
	}
}
