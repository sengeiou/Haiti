package com.zaxxer.hikari;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;

/**
 * @author HO
 *
 */
public class HikariCPDataSource extends HikariDataSource {

	private final HashMap<MultiPoolKey, HikariCPDataSource> multiPool;

	public HikariCPDataSource() {
		multiPool = new HashMap<MultiPoolKey, HikariCPDataSource>();
	}

	public HikariCPDataSource(HikariConfig configuration) {
		super(configuration);
		multiPool = new HashMap<MultiPoolKey, HikariCPDataSource>();
	}

	@Override
	public Connection getConnection(String username, String password) throws SQLException {

		MultiPoolKey key = new MultiPoolKey(username, password);
		HikariCPDataSource dataSource = multiPool.get(key);

		if (dataSource == null) {
			//dataSource = new HikariDataSource();
			//dataSource.setUsername(username);
			//dataSource.setPassword(password);
			dataSource = this;
			
			this.setUsername(username);
			this.setPassword(password);
			
			multiPool.put(key, this);
		}

		return dataSource.getConnection();
	}

	private static class MultiPoolKey {
		private String username;
		private String password;

		MultiPoolKey(String username, String password) {
			this.username = username;
			this.password = password;
		}

		public int hashCode() {
			return this.password == null ? 0 : this.password.hashCode();
		}

		public boolean equals(Object obj) {
			MultiPoolKey otherKey = (MultiPoolKey) obj;
			if ((this.username != null) && (!this.username.equals(otherKey.username))) {
				return false;
			}
			if ((this.username == null) && (otherKey.username != null)) {
				return false;
			}
			if ((this.password != null) && (!this.password.equals(otherKey.password))) {
				return false;
			}
			if ((this.password == null) && (otherKey.password != null)) {
				return false;
			}

			return true;
		}
	}

}
