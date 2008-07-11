package demo.jdbc.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import demo.jdbc.model.Bookmark;

public class BookmarkDAO implements DAOInterface {

	protected DataSource dataSource;

	protected String dbType;

	protected static Map<String, String> insertSqlStatement;

	protected static String updateSqlStatement = "UPDATE bookmark SET "
			+ "url=?, title=?, last_modified=?, "
			+ "content=?, checksum=?, description=?, attachment=? WHERE bookmark_id=?";

	protected static Map<String, String> sequenceValSqlStatement;

	private Log log = LogFactory.getLog(BookmarkDAO.class);

	private Connection conn = null;

	private PreparedStatement pstmt = null;

	private Statement stmt = null;

	private ResultSet rs = null;

	public BookmarkDAO() {
		insertSqlStatement = new HashMap<String, String>();
		insertSqlStatement.put("MySQL", "INSERT INTO bookmark "
				+ "VALUES (NULL,?,?,?,?,?,?,?)");
		insertSqlStatement.put("Oracle", "INSERT INTO bookmark "
				+ "SELECT seq_bookmark_id.nextval,?,?,?,?,?,?,? FROM dual");

		sequenceValSqlStatement = new HashMap<String, String>();
		sequenceValSqlStatement.put("MySQL", "SELECT last_insert_id()");
		sequenceValSqlStatement.put("Oracle",
				"SELECT seq_bookmark_id.currval FROM dual");
	}

	private void closeAll() {
		try {
			rs.close();
		} catch (Exception e) {
		}
		try {
			pstmt.close();
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

	/**
	 * 
	 */
	public long save(Object obj) {
		try {
			conn = dataSource.getConnection();
			Bookmark item = (Bookmark) obj;

			if (item.getId() == 0) {
				pstmt = conn.prepareStatement(insertSqlStatement.get(dbType));
			} else {
				pstmt = conn.prepareStatement(updateSqlStatement);
				pstmt.setLong(7, item.getId());
			}
			initPreparedStatement(pstmt, item);
			pstmt.executeUpdate();
			// find the database id of the inserted item
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sequenceValSqlStatement.get(dbType));
			rs.next();
			long id = rs.getLong(1);
			item.setId(id);
			log.trace("TT");
			log.debug("DD");
			log.info("INFO");
			log.warn("WARNING:...");			
			log.error("Could not save object");
			log.fatal("XXX");
			return id;
		} catch (Exception e) {
			log.error("Could not save object",e);

			throw new DAOException("Could not save object", e);
		} finally {
			closeAll();
		}
	}

	public List<Bookmark> findAll() {
		List<Bookmark> result = new ArrayList<Bookmark>();
		try {
			conn = dataSource.getConnection();
			stmt = conn.createStatement();
			rs = stmt.executeQuery("SELECT * FROM bookmark");
			while (rs.next()) {
				Bookmark item = getItemFromCursor(rs);
				result.add(item);
			}
		} catch (SQLException e) {
			log.error("Could not select all", e);
			throw new DAOException("Could not select all", e);
		} finally {
			closeAll();
		}
		return result;
	}

	public boolean remove(long id) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		try {
			conn = dataSource.getConnection();
			pstmt = conn
					.prepareStatement("DELETE FROM bookmark WHERE bookmark_id = ?");
			pstmt.setLong(1, id);
			int rows = pstmt.executeUpdate();
			// returns true, if any rows were deleted
			if (rows == 0) {
				log.warn("remove ignored - no record with id = " + id);
				return false;
			} else {
				return true;
			}
		} catch (SQLException e) {
			log.error("Could not remove item", e);
			throw new DAOException("Could not remove item", e);
		} finally {
			closeAll();
		}
	}

	public Object find(long id) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rset = null;
		try {
			conn = dataSource.getConnection();
			Bookmark result;
			pstmt = conn
					.prepareStatement("SELECT * FROM bookmark WHERE bookmark_id = ?");
			rset = pstmt.executeQuery();
			if (rset.next()) {
				result = getItemFromCursor(rset);
			} else {
				result = null;
			}
			return result;
		} catch (SQLException e) {
			log.error("Could not find item", e);
			throw new DAOException("Could not find item", e);
		} finally {
			closeAll();
		}
	}

	private void initPreparedStatement(PreparedStatement pstmt, Bookmark item)
			throws SQLException {
		if (item.getUrl() != null) {
			pstmt.setString(1, item.getUrl());
		} else {
			pstmt.setNull(1, Types.VARCHAR);
		}
		if (item.getTitle() != null) {
			pstmt.setString(2, item.getTitle());
		} else {
			pstmt.setNull(2, Types.VARCHAR);
		}
		if (item.getLastModified() != null) {
			pstmt.setDate(3, new Date(item.getLastModified().getTime()));
		} else {
			pstmt.setNull(3, Types.TIMESTAMP);
		}
		if (item.getContent() != null) {
			pstmt.setString(4, item.getContent());
		} else {
			pstmt.setNull(4, Types.CLOB);
		}
		if (item.getChecksum() != 0) {
			pstmt.setLong(5, item.getChecksum());
		} else {
			pstmt.setNull(5, Types.BIGINT);
		}
		if (item.getDescription() != null) {
			pstmt.setString(6, item.getDescription());
		} else {
			pstmt.setNull(6, Types.VARCHAR);
		}
		if (item.getAttachment() != null) {
			pstmt.setBytes(7, item.getAttachment());
		} else {
			pstmt.setNull(7, Types.BLOB);
		}
	}

	private Bookmark getItemFromCursor(ResultSet rset) {
		Bookmark item = new Bookmark();
		try {
			item.setId(rset.getLong("bookmark_id"));
			item.setUrl(rset.getString("url"));
			item.setTitle(rset.getString("title"));
			item.setLastModified(rset.getDate("last_updated"));
			item.setContent(rset.getString("content"));
			item.setChecksum(rset.getLong("checksum"));
			item.setDescription(rset.getString("description"));
		} catch (SQLException e) {
			log.error("Could not initialize bookmark from resultset", e);
			throw new DAOException(
					"Could not initialize bookmark from resultset", e);
		}
		return item;
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public void setDbType(String dbType) {
		this.dbType = dbType;
	}

	public DataSource getDataSource() {
		return dataSource;
	}

	/*
	public void commit() {
		Connection conn = null;
		try {
			conn = dataSource.getConnection();
			conn.commit();
		} catch (Exception e) {
			log.error("Cannot commit", e);
		} finally {
			try {
				conn.close();
			} catch (Exception e) {
			}
		}
	}
*/
}
