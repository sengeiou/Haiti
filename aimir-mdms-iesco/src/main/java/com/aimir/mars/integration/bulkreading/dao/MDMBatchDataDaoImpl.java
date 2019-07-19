package com.aimir.mars.integration.bulkreading.dao;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractHibernateGenericDao;
import com.aimir.mars.integration.bulkreading.model.MDMBatchLog;

@Repository(value = "mdmBatchDataDao")
public class MDMBatchDataDaoImpl extends AbstractHibernateGenericDao<MDMBatchLog, Integer> implements MDMBatchDataDao {
	
	protected static Log logger = LogFactory.getLog(MDMBatchDataDaoImpl.class);
	
	protected MDMBatchDataDaoImpl(SessionFactory sessionFactory) {
		super(MDMBatchLog.class);
		super.setSessionFactory(sessionFactory);
	}
	
	@Override
	public void procBatchLpEM() throws Exception {
		try {			
			SQLQuery query = getSession().createSQLQuery("{call PKG_MDM_BATCH_JOB.INSERT_BATCH_LP_EM()}");
			query.executeUpdate();
		} catch (Exception e) {
			logger.debug("[procBatchLpEM]", e);
		}
	}

	@Override
	public void procBatchBillingDayEM() throws Exception {
		try {			
			SQLQuery query = getSession().createSQLQuery("{call PKG_MDM_BATCH_JOB.INSERT_BATCH_BILLING_DAY_EM()}");
			query.executeUpdate();
		} catch (Exception e) {
			logger.debug("[procBatchBillingDayEM]", e);
		}
	}

	@Override
	public void procBatchBillingMonthEM() throws Exception {
		try {			
			SQLQuery query = getSession().createSQLQuery("{call PKG_MDM_BATCH_JOB.INSERT_BATCH_BILLING_MONTH_EM()}");
			query.executeUpdate();
		} catch (Exception e) {
			logger.debug("[procBatchBillingMonthEM]", e);
		}
	}

	@Override
	public void procBatchMetereventLog() throws Exception {
		try {			
			SQLQuery query = getSession().createSQLQuery("{call PKG_MDM_BATCH_JOB.INSERT_BATCH_METEREVENT_LOG()}");
			query.executeUpdate();
		} catch (Exception e) {
			logger.debug("[procBatchMetereventLog]", e);
		}
	}

	@Override
	public void procBatchDeleteData() throws Exception {
		try {			
			SQLQuery query = getSession().createSQLQuery("{call PKG_MDM_DEL_DATA.DELETE_MDM_DATA()}");
			query.executeUpdate();
		} catch (Exception e) {
			logger.debug("[procBatchDeleteData]", e);
		}
	}
}