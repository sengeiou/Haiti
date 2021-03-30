package com.aimir.dao.system.impl;

import javax.persistence.NoResultException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractHibernateGenericDao;
import com.aimir.dao.system.MonthlyBillingLogDao;
import com.aimir.model.system.MonthlyBillingLog;
import com.aimir.model.system.MonthlyBillingLogPk;

@Repository(value = "monthlyBillingLogDao")
public class MonthlyBillingLogDaoImpl extends AbstractHibernateGenericDao<MonthlyBillingLog, MonthlyBillingLogPk> implements MonthlyBillingLogDao {
	private static Log log = LogFactory.getLog(MonthlyBillingLogDaoImpl.class);
	
	@Autowired
	protected MonthlyBillingLogDaoImpl(SessionFactory sessionFactory) {
		super(MonthlyBillingLog.class);
		super.setSessionFactory(sessionFactory);
	}

	@Override
	public MonthlyBillingLog getLastMonthlyBillingLog(Integer contractId, String mdevId) {
		if(contractId == null && mdevId == null) {
			log.info("contractId and mdevId is null!!");
			return null;
		}
		
		try {
			StringBuffer sbQuery = new StringBuffer();
			sbQuery.append(" SELECT * FROM ( ");
			sbQuery.append("\n		SELECT mb.* FROM MONTHLY_BILLING_LOG mb ");
			sbQuery.append("\n		WHERE 1 = 1");
			
			if(contractId != null)
				sbQuery.append("\n		AND mb.CONTRACT_ID = :contractId");
			
			if(mdevId != null)
				sbQuery.append("\n		AND mb.MDS_ID = :mdevId");
			
			sbQuery.append("\n		ORDER BY mb.YYYYMM DESC");	
			sbQuery.append("\n		FETCH first 1 ROW ONLY");
			sbQuery.append("\n )");
			
			Query query = getSession().createNativeQuery(sbQuery.toString(), MonthlyBillingLog.class);
			if(contractId != null)
				query.setParameter("contractId", contractId);
			
			if(mdevId != null)
				query.setParameter("mdevId", mdevId);
			
			return (MonthlyBillingLog)query.getSingleResult();
		}catch(NoResultException e) {
			return null;
		}
		
	}

}
