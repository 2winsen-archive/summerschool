package demo.jdbc.dao.temp;

import demo.jdbc.dao.DAOException;


/**
 * <p>
 * IdGeneratorInterface is an interface of IdGenerator classes.
 * </p> 
 */
public interface IdGeneratorInterface {

	public long getNextId() throws DAOException;
}
