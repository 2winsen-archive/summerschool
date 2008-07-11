package demo.jdbc.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import demo.jdbc.model.Bookmark;

@RunWith(Suite.class)
@Suite.SuiteClasses(value = { //BookmarkDAOTest.OracleTests.class,
		BookmarkDAOTest.PooledMySQLTests.class, 
		BookmarkDAOTest.MySQLTests.class,
		//BookmarkDAOTest.PooledOracleTests.class,
		 
		
})
public class BookmarkDAOTest {

	private static BookmarkDAO dao;

	private static Map<String, List<String>> connectionParams = new HashMap<String, List<String>>();

	@BeforeClass
	public static void onlyOnce() {
		dao = new BookmarkDAO();
		connectionParams.put("Oracle", Arrays.asList(new String[] {
				"oracle.jdbc.driver.OracleDriver",
				"jdbc:oracle:thin:@localhost:1522:orcl", "sampleuser",
				"samplepassword" }));
		connectionParams.put("MySQL", Arrays.asList(new String[] {
				"com.mysql.jdbc.Driver", "jdbc:mysql://localhost/music",
				"student", "Student007" }));
	}

	public static class OracleTests {

		protected static long startTime;

		@BeforeClass
		public static void startTimer() {
			startTime = System.currentTimeMillis();
		}

		@AfterClass
		public static void endTimer() {
			long elapsedMsec = System.currentTimeMillis() - startTime;
			System.err.println("Elapsed time for Oracle NOT-pooled is " + elapsedMsec);
		}

		protected  String getDbType() {
			return "Oracle";
		}

		protected  boolean isPooled() {
			return false;
		}

		@Before
		public void setUp() throws Exception {
			DataSourceAdapter adapter = new DataSourceAdapter();
			adapter.setPooled(isPooled());
			adapter.setDbType(getDbType());
			adapter.setConnectionParams(connectionParams);
			DataSource ds = (DataSource) adapter.getObject();
			dao.setDbType(getDbType());
			dao.setDataSource(ds);
			dao.save(new Bookmark("http://www.delfi.lv", "Delfi"));
		}

		/**
		 * Deletes everything
		 */
		@After
		public void tearDown() throws SQLException {
			Connection conn = dao.getDataSource().getConnection();
			Statement stmt = conn.createStatement();
			stmt.executeUpdate("DELETE FROM bookmark");
			stmt.close();
			conn.close();
		}

		@Test
		public void testSave() throws SQLException {
			dao.save(new Bookmark("http://www.google.com", "Google"));
			dao.save(new Bookmark("http://www.yahoo.com", "Yahoo"));
			assertEquals(3, dao.findAll().size());
		}

		@Test(expected = DAOException.class)
		public void testSaveNoTitle() {
			dao.save(new Bookmark("http://www.google.com", null));
		}

		@Test(expected = DAOException.class)
		public void testSaveNoUrl() {
			dao.save(new Bookmark(null, "Google"));
		}

		@Test
		public void testRemove() throws SQLException {
			Bookmark item1 = new Bookmark();
			item1.setUrl("http://www.google.com");
			item1.setTitle("Google");
			long id = dao.save(item1);
			// long id = item1.getId();
			assertTrue(dao.remove(id));
			assertEquals(1, dao.findAll().size());
		}

	}

	public static class MySQLTests extends OracleTests {
		@BeforeClass
		public static void startTimer() {
			startTime = System.currentTimeMillis();
		}

		@AfterClass
		public static void endTimer() {
			long elapsedMsec = System.currentTimeMillis() - startTime;
			System.err.println("Elapsed time for MySQL NOT-pooled is " + elapsedMsec);
		}
		
		@Override
		protected  String getDbType() {
			return "MySQL";
		}

		@Override
		protected  boolean isPooled() {
			return false;
		}
	}

	public static class PooledOracleTests extends OracleTests {
		@BeforeClass
		public static void startTimer() {
			startTime = System.currentTimeMillis();
		}

		@AfterClass
		public static void endTimer() {
			long elapsedMsec = System.currentTimeMillis() - startTime;
			System.err.println("Elapsed time for Oracle pooled is " + elapsedMsec);
		}
		@Override
		protected  String getDbType() {
			return "Oracle";
		}

		@Override
		protected  boolean isPooled() {
			return true;
		}
	}

	public static class PooledMySQLTests extends OracleTests {
		@BeforeClass
		public static void startTimer() {
			startTime = System.currentTimeMillis();
		}

		@AfterClass
		public static void endTimer() {
			long elapsedMsec = System.currentTimeMillis() - startTime;
			System.err.println("Elapsed time for MySQL pooled is " + elapsedMsec);
		}
		@Override
		protected  String getDbType() {
			return "MySQL";
		}

		@Override
		protected  boolean isPooled() {
			return true;
		}
	}
}
