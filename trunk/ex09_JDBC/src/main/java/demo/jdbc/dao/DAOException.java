package demo.jdbc.dao;

@SuppressWarnings("serial")
public class DAOException extends RuntimeException {
	public DAOException(String message) {
		super(message);
	}
	
	public DAOException(String message, Throwable ex) {
		super(message, ex);
	}	
}
