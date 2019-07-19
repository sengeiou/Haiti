// INSERT SP-193
package com.aimir.dao.device.impl;

import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.aimir.constants.CommonConstants;
import com.aimir.constants.CommonConstants.ThresholdName;
import com.aimir.dao.AbstractHibernateGenericDao;
import com.aimir.dao.device.ThresholdDao;
import com.aimir.model.device.Threshold;

@Repository(value = "thresholdDao")
public class ThresholdDaoImpl extends AbstractHibernateGenericDao<Threshold, Integer> implements ThresholdDao {

	private static Log log = LogFactory.getLog(Threshold.class);
	
	@Autowired
	protected ThresholdDaoImpl(SessionFactory sessionFactory) {
		super(Threshold.class);
		super.setSessionFactory(sessionFactory);
	}
	
	@SuppressWarnings("unchecked")
	public Threshold getThresholdByname(String name) {
		StringBuffer hqlBuf = new StringBuffer();
		hqlBuf.append(" SELECT t.id, t.duration, t.limit, t.more, t.name ");
		hqlBuf.append(" FROM Threshold t");
		hqlBuf.append(" WHERE t.name = :name"); 
		hqlBuf.append(" ORDER BY t.id"); 	
		
		Query query = getSession().createQuery(hqlBuf.toString());

		query.setParameter("name", ThresholdName.valueOf(name));
		
		List result = query.list();
		
		if (result.size() == 0) {
			return null;
		}
		else {
			Object[] resultData = (Object[]) result.get(0);
			Threshold threshold = new Threshold();
			threshold.setId(Integer.parseInt(resultData[0].toString()));
			if (resultData[1] != null)threshold.setDuration(resultData[1].toString());
			if (resultData[2] != null)threshold.setLimit(Integer.parseInt(resultData[2].toString()));
			if (resultData[3] != null)threshold.setMore(Integer.parseInt(resultData[3].toString()));
			threshold.setThresholdName(resultData[4].toString());
			return threshold;
		}
	}
	

}
