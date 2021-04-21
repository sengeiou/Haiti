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
	public MonthlyBillingLog getLastMonthlyBillingLog(Integer contractId, String mdevId, String yyyymm) {
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
			
			if(yyyymm != null && !yyyymm.isEmpty())
				sbQuery.append("\n		AND mb.YYYYMM = :yyyymm");
			
			
			sbQuery.append("\n		ORDER BY mb.YYYYMM DESC");	
			sbQuery.append("\n		FETCH first 1 ROW ONLY");
			sbQuery.append("\n )");
			
			Query query = getSession().createNativeQuery(sbQuery.toString(), MonthlyBillingLog.class);
			if(contractId != null)
				query.setParameter("contractId", contractId);
			
			if(mdevId != null)
				query.setParameter("mdevId", mdevId);
			
			if(yyyymm != null && !yyyymm.isEmpty())
				query.setParameter("yyyymm", yyyymm);
			
			return (MonthlyBillingLog)query.getSingleResult();
		}catch(NoResultException e) {
			return null;
		}
		
	}

	@Override
	public int updateMonthlyUsageInfo(String mdevId, String yyyymmdd, double monthlyConsumption, double monthlyUsageBill, double activeEnergyImport, double activeEnergyExport) {
		if(mdevId == null || yyyymmdd == null)
			return -1;
		
		String yyyymm = yyyymmdd.substring(0, 6);
		
		StringBuffer sbQuery = new StringBuffer();
		sbQuery.append("\n MERGE INTO MONTHLY_BILLING_LOG mb  ");
		sbQuery.append("\n 	USING ( ");
		sbQuery.append("\n 		SELECT * FROM MONTHLY_BILLING_LOG bl ");
		sbQuery.append("\n 		WHERE bl.MDS_ID = :mdevId AND bl.YYYYMM = :yyyymm	 ");
		sbQuery.append("\n 	)t ");
		sbQuery.append("\n 	ON  ");
		sbQuery.append("\n 		(mb.MDS_ID = t.MDS_ID AND mb.YYYYMM = t.YYYYMM) ");
		sbQuery.append("\n 	WHEN MATCHED THEN ");
		sbQuery.append("\n 		UPDATE SET ");
		sbQuery.append("\n 			mb.USED_CONSUMPTION = :monthlyConsumption, ");
		sbQuery.append("\n 			mb.PAID_COST = :monthlyBill, ");
		sbQuery.append("\n 			mb.MONTHLY_COST = t.SERVICE_CHARGE + :monthlyBill, ");		
		sbQuery.append("\n 			mb.ACTIVEENERGYIMPORT = :activeEnergyImport, ");		
		sbQuery.append("\n 			mb.ACTIVEENERGYEXPORT = :activeEnergyExport ");
		
		Query query = getSession().createNativeQuery(sbQuery.toString());
		query.setParameter("yyyymm", yyyymm);
		query.setParameter("mdevId", mdevId);
		query.setParameter("monthlyConsumption", monthlyConsumption);
		query.setParameter("monthlyBill", monthlyUsageBill);
		query.setParameter("activeEnergyImport", activeEnergyImport);
		query.setParameter("activeEnergyExport", activeEnergyExport);
		
		return query.executeUpdate();
	}

}
