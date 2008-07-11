package demo.jdbc.dao;

import java.util.List;

public interface DAOInterface {
	public long save(Object obj);

	public boolean remove(long id);

	public Object find(long id);

	public List<? extends Object> findAll();
}
