package com.aimir.mars.integration.bulkreading.dao;

import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.aimir.constants.CommonConstants.DeviceType;
import com.aimir.dao.AbstractHibernateGenericDao;
import com.aimir.mars.integration.bulkreading.model.MDMBillingDayEM;


@Repository(value = "mdmBillingDayEMDao")
public class MDMBillingDayEMDaoImpl extends AbstractHibernateGenericDao<MDMBillingDayEM, Integer> implements MDMBillingDayEMDao {
	
	protected static Log logger = LogFactory.getLog(MDMBillingDayEMDaoImpl.class);
	
	@Autowired
	protected MDMBillingDayEMDaoImpl(SessionFactory sessionFactory) {
		super(MDMBillingDayEM.class);
		super.setSessionFactory(sessionFactory);
	}

	@Override
	public void delete(Map<String, Object> condition) {
		try {				
			int batchId = (int)condition.get("BATCH_ID");
			
	        StringBuffer queryBuffer = new StringBuffer();
	        
	        queryBuffer.append(" DELETE FROM MDM_BILLING_DAY_EM \n");   
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
	        
	        queryBuffer.append(" UPDATE MDM_BILLING_DAY_EM SET \n");   
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
	public void updateInitTransferDate(List<MDMBillingDayEM> mdmList) {
		
		for(MDMBillingDayEM mdmEM : mdmList) {
			
			try {
				
				int batchId = mdmEM.getBatchId();
				String mdevId = mdmEM.getMdevId();
				String yyyymmdd = mdmEM.getYyyymmdd();
				String hhmmss = mdmEM.getHhmmss();
				DeviceType mdevType = mdmEM.getMdevType();
				
		        StringBuffer queryBuffer = new StringBuffer();
		        
		        queryBuffer.append(" UPDATE MDM_BILLING_DAY_EM SET \n");   
		        queryBuffer.append(" 	TRANSFER_DATETIME = 'error' \n");
		        queryBuffer.append(" WHERE MDEV_ID = :mdevId and YYYYMMDD = :yyyymmdd and HHMMSS = :hhmmss and MDEV_TYPE = :mdevType \n");
		        
				SQLQuery query = getSession().createSQLQuery(queryBuffer.toString());
				query.setString("mdevId", mdevId);		
				query.setString("yyyymmdd", yyyymmdd);
				query.setString("hhmmss", hhmmss);
				query.setInteger("mdevType", mdevType.getCode());
				
				query.executeUpdate();  
	        }
	        catch (Exception e) {
	        	logger.error(e);
	        }
		} // end for
		
	}

	
}