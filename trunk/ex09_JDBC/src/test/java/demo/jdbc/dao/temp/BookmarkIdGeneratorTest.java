package demo.jdbc.dao.temp;

import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import demo.jdbc.dao.DataSourceAdapter;
import demo.jdbc.dao.temp.BookmarkIdGenerator;

@RunWith(Suite.class)
@Suite.SuiteClasses(value = { BookmarkIdGeneratorTest.OracleTests.class,	
		BookmarkIdGeneratorTest.PooledOracleTests.class,
})
public class BookmarkIdGeneratorTest {

	private static Map<String, List<String>> connectionParams = new HashMap<String, List<String>>();

	private static BookmarkIdGenerator generator;

	@BeforeClass
	public static void onlyOnce() {
		connectionParams.put("Oracle", Arrays.asList(new String[] {
				"oracle.jdbc.driver.OracleDriver",
				"jdbc:oracle:thin:@localhost:1522:orcl", "sampleuser",
				"samplepassword" }));
		connectionParams.put("MySQL", Arrays.asList(new String[] {
				"com.mysql.jdbc.Driver", "jdbc:mysql://localhost/jdbc_sample",
				"sampleuser", "samplepassword" }));
	}

	public static class OracleTests {

		protected boolean isPooled() {
			return false;
		}

		@Before
		public void setUp() throws Exception {
			DataSourceAdapter adapter = new DataSourceAdapter();
			adapter.setPooled(isPooled());
			adapter.setDbType("Oracle");
			adapter.setConnectionParams(connectionParams);
			DataSource ds = (DataSource) adapter.getObject();
			generator = BookmarkIdGenerator.getInstance(ds);
		}
		
		@Test
		public void testGetNext() {
			long id1 = generator.getNextId();
			assertTrue(id1 > 0);
			long id2 = generator.getNextId();
			assertTrue(id2 == id1 + 1);
		}
	}
	
	public static class PooledOracleTests extends OracleTests {

		@Override
		protected boolean isPooled() {
			return true;
		}
	}
}
