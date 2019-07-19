package com.aimir.mars.integration.bulkreading.dao;

import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractHibernateGenericDao;
import com.aimir.mars.integration.bulkreading.model.MDMBillingMonthEM;

@Repository(value = "mdmBillingMonthEMDao")
public class MDMBillingMonthEMDaoImpl extends AbstractHibernateGenericDao<MDMBillingMonthEM, Integer> implements MDMBillingMonthEMDao {
	
	protected static Log logger = LogFactory.getLog(MDMBillingMonthEMDaoImpl.class);
	
	@Autowired
	protected MDMBillingMonthEMDaoImpl(SessionFactory sessionFactory) {
		super(MDMBillingMonthEM.class);
		super.setSessionFactory(sessionFactory);
	}

	@Override
	public void delete(Map<String, Object> condition) {
		try {				
			int batchId = (int)condition.get("BATCH_ID");
			
	        StringBuffer queryBuffer = new StringBuffer();
	        
	        queryBuffer.append(" DELETE FROM MDM_BILLING_MONTH_EM \n");   
	        queryBuffer.append(" WHERE 	batch_id = :batchId \n");
	        
			SQLQuery query = getSession().createSQLQuery(queryBuffer.toString());
			
			query.setInteger("batchId", batchId);
			
			query.executeUpdate();  
        }
        catch (Exception e) {
        	logger.error(e);
        }
	}

	@Override
	public void updateTransferDate(int batchId) {
		try {		
			
	        StringBuffer queryBuffer = new StringBuffer();
	        
	        queryBuffer.append(" UPDATE MDM_BILLING_MONTH_EM SET \n");   
	        queryBuffer.append(" 	TRANSFER_DATETIME = to_char(sysdate,'yyyymmddhh24miss') \n");
	        queryBuffer.append(" WHERE 	batch_id = :batch_id \n");
	        
			SQLQuery query = getSession().createSQLQuery(queryBuffer.toString());
			
			query.setInteger("batch_id", batchId);
			query.executeUpdate();  
        }
        catch (Exception e) {
        	logger.error(e);
        }
	}

	@Override
	public void updateInitTransferDate(List<MDMBillingMonthEM> mdmList) {
		// TODO Auto-generated method stub
		
	}
}