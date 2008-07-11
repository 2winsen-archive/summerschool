package demo.jdbc.dao.temp;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import demo.jdbc.dao.BookmarkDAO;
import demo.jdbc.dao.DAOException;

/**
 * <p>
 * BookmarkIdGenerator is a IdGenerator class for Bookmark.
 * </p>
 */
public class BookmarkIdGenerator implements IdGeneratorInterface {

	protected DataSource dataSource;

	private Log logger = LogFactory.getLog(BookmarkDAO.class);

	private static BookmarkIdGenerator idGen = null;

	private String genID = "SELECT seq_bookmark_id.NEXTVAL FROM dual";

	private ResultSet rs = null;

	private Statement stmt = null;

	private Connection conn = null;

	private BookmarkIdGenerator(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	/**
	 * Gets the instance of this class
	 * 
	 * @return BookmarkIdGenerator
	 */
	public static BookmarkIdGenerator getInstance(DataSource dataSource) {
		if (idGen == null) {
			idGen = new BookmarkIdGenerator(dataSource);
		}
		return idGen;
	}

	/**
	 * Returns the next id for Bookmark table or
	 * -1, if cannot get one. 
	 * 
	 * @return long - generated id
	 */
	public long getNextId() {
		long id = -1;
		try {
			conn = dataSource.getConnection();
			stmt = conn.createStatement();
			rs = stmt.executeQuery(genID);
			rs.next();
			id = rs.getLong(1);
		} catch (Exception e) {
			logger.error("Cannot generate ID", e);
			throw new DAOException("Cannot generate ID", e);
		} finally {
			try {
				rs.close();
			} catch (Exception e) {
			}
			try {
				stmt.close();
			} catch (Exception e) {
			}
			try {
				conn.close();
			} catch (Exception e) {
			}
		}
		return id;
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}
}
