package com.aimir.schedule.task;

import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;

public class SqliteDao {

	private JdbcTemplate jdbcTemplate;

	public void setDataSource(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	public List<Map<String, Object>> getAtcCell(String codi) {
		StringBuffer sb = new StringBuffer();

		sb.append(" select _id,_codi ");
		sb.append(" ,latitude,nsIndicator");
		sb.append(" ,longitude,ewIndicator");
		sb.append(" ,lastDate,lastUtcTime ");
		sb.append(" ,lqi,rssi ");
		sb.append(" from atc_cell_tbl ");
		sb.append(" where _codi='"+codi+"' ");
	    System.out.println(sb
				.toString());
		List<Map<String, Object>> result = this.jdbcTemplate.queryForList(sb
				.toString());
		return result;
	}

	public List<Map<String, Object>> getAtcCodi() {

		StringBuffer sb = new StringBuffer();
		
		sb.append(" select _id, latitude ");
		sb.append(" ,nsIndicator ");
		sb.append(" ,longitude ");
		sb.append(" ,ewIndicator ");
		sb.append(" from atc_codi_tbl ");
		List<Map<String, Object>> result = this.jdbcTemplate.queryForList(sb
				.toString());
		return result;
	
	}

}
