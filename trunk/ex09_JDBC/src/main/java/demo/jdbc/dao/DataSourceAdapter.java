package demo.jdbc.dao;

import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSource;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

public class DataSourceAdapter implements FactoryBean {

	/**
	 * e.g. "Oracle", "MySQL" or "HSQLDB".
	 */
	protected String dbType;

	protected boolean pooled;

	protected final static int POOL_INITIAL_SIZE = 2;

	protected final static int POOL_MAX_ACTIVE = 5;

	protected final static int POOL_MAX_IDLE = 2;

	protected Map<String, List<String>> connectionParams;

	public void setDbType(String dbType) {
		this.dbType = dbType;
	}

	public void setPooled(boolean pooled) {
		this.pooled = pooled;
	}

	public void setConnectionParams(Map<String, List<String>> connectionParams) {
		this.connectionParams = connectionParams;
	}

	public Object getObject() throws Exception {
		DataSource dataSource;
		List<String> params = connectionParams.get(dbType);
		if (!pooled) {
			dataSource = new DriverManagerDataSource(params.get(0), params
					.get(1), params.get(2), params.get(3));
		} else {
			dataSource = new BasicDataSource();
			((BasicDataSource) dataSource).setDriverClassName(params.get(0));
			((BasicDataSource) dataSource).setUrl(params.get(1));
			((BasicDataSource) dataSource).setUsername(params.get(2));
			((BasicDataSource) dataSource).setPassword(params.get(3));
			((BasicDataSource) dataSource).setInitialSize(POOL_INITIAL_SIZE);
			((BasicDataSource) dataSource).setMaxActive(POOL_MAX_ACTIVE);
			((BasicDataSource) dataSource).setMaxIdle(POOL_MAX_IDLE);
		}
		return dataSource;
	}

	public Class getObjectType() {
		return DataSource.class;
	}

	public boolean isSingleton() {
		return true;
	}
}
