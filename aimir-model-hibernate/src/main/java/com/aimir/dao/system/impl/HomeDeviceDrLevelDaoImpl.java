package com.aimir.dao.system.impl;

import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractHibernateGenericDao;
import com.aimir.dao.system.HomeDeviceDrLevelDao;
import com.aimir.model.system.HomeDeviceDrLevel;

@Repository(value = "homedevicedrlevelDao")
public class HomeDeviceDrLevelDaoImpl extends AbstractHibernateGenericDao<HomeDeviceDrLevel, Integer>
implements HomeDeviceDrLevelDao  {
	Log logger = LogFactory.getLog(HomeDeviceDrLevelDaoImpl.class);

	@Autowired
	protected HomeDeviceDrLevelDaoImpl(SessionFactory sessionFactory) {
		super(HomeDeviceDrLevel.class);
		super.setSessionFactory(sessionFactory);
	}
	
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> getHomeDeviceDrLevelByCondition(int categoryId, String drLevel) {
		StringBuffer sbSql = new StringBuffer();
		sbSql.append(" SELECT h.drname AS drname  ")
		.append(", h.drlevel AS drlevel ")
		.append(", h.category_id AS categoryId ")
		.append(" FROM HOME_DEVICE_DRLEVEL h ")
		.append(" WHERE h.category_id = :categoryId ");
		
		if (drLevel.length() != 0 ) {
			sbSql.append(" AND h.drlevel = ").append(Integer.parseInt(drLevel)).append(" ");
		}

		sbSql.append(" ORDER BY h.drlevel ");

		SQLQuery query = getSession().createSQLQuery(sbSql.toString());
		query.setInteger("categoryId", categoryId);

		return query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();		
	}

}
