package com.aimir.dao.device.impl;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractHibernateGenericDao;
import com.aimir.dao.device.AsyncCommandResultDao;
import com.aimir.model.device.AsyncCommandResult;
import com.aimir.model.device.AsyncCommandResultPk;

@Repository(value = "asynccommandresultDao")
public class AsyncCommandResultDaoImpl extends AbstractHibernateGenericDao<AsyncCommandResult, AsyncCommandResultPk> implements AsyncCommandResultDao {

	@Autowired
	protected AsyncCommandResultDaoImpl(SessionFactory sessionFactory) {
		super(AsyncCommandResult.class);
		super.setSessionFactory(sessionFactory);
	}

	@Override
	public Integer getMaxNum(String mcuId, Long trId) {
		StringBuffer sb = new StringBuffer();

		sb.append("\n SELECT max(a.id.num)");
		sb.append("\n FROM AsyncCommandResult a");
		sb.append("\n WHERE a.id.mcuId=:mcuId");
		sb.append("\n AND a.id.trId=:trId");

		Query query = getSession().createQuery(sb.toString());
		query.setString("mcuId", mcuId);
		query.setLong("trId", trId);

		Number totalCount = (Number) query.uniqueResult();
		int returnData = 0;

		if (totalCount == null)
			returnData = 0;
		else {
			returnData = totalCount.intValue();
		}
		return returnData;
	}

	@Override
	public List<AsyncCommandResult> getCmdResults(String deviceSerial, long trId, String paramName) {
		Criteria criteria = getSession().createCriteria(AsyncCommandResult.class);
		criteria.add(Restrictions.eq("id.mcuId", deviceSerial));
		criteria.add(Restrictions.eq("id.trId", trId));

		if (paramName != null) {
			criteria.add(Restrictions.eq("resultType", paramName));
		}

		return criteria.list();
	}
	
	@Override
	public List<AsyncCommandResult> getCmdResults(String deviceSerial, long trId, String tr_type, String paramName) {
		Criteria criteria = getSession().createCriteria(AsyncCommandResult.class);
		criteria.add(Restrictions.eq("id.mcuId", deviceSerial));
		criteria.add(Restrictions.eq("id.trId", trId));
		criteria.add(Restrictions.eq("trType", tr_type));

		if (paramName != null) {
			criteria.add(Restrictions.eq("resultType", paramName));
		}

		return criteria.list();
	}
	
	@Override
	public String getCmdResults(String deviceSerial, long trId) {
		StringBuffer sb = new StringBuffer();

		sb.append("\n SELECT a.resultValue");
		sb.append("\n FROM AsyncCommandResult a");
		sb.append("\n WHERE a.id.mcuId=:mcuId");
		sb.append("\n AND a.id.trId=:trId");
		
		Query query = getSession().createQuery(sb.toString());
		query.setString("mcuId", deviceSerial);
		query.setLong("trId", trId);
		
		String result = (String) query.uniqueResult();
		
		return result;
	}
}
