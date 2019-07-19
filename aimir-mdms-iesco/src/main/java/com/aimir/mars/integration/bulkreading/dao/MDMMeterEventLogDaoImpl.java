package com.aimir.mars.integration.bulkreading.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractHibernateGenericDao;
import com.aimir.mars.integration.bulkreading.model.MDMMeterEventLog;

@Repository(value = "mdmBatchMeterEventLogDao")
public class MDMMeterEventLogDaoImpl extends AbstractHibernateGenericDao<MDMMeterEventLog, Integer> implements MDMMeterEventLogDao {
	
	protected static Log logger = LogFactory.getLog(MDMMeterEventLogDaoImpl.class);
	
	@Autowired
	protected MDMMeterEventLogDaoImpl(SessionFactory sessionFactory) {
		super(MDMMeterEventLog.class);
		super.setSessionFactory(sessionFactory);
	}
	
	@Override
	public List<Map<String, Object>> select(Map<String, Object> condition) {
		
		int batchId = (int)condition.get("BATCH_ID");
		
		StringBuffer queryBuffer = new StringBuffer();
        
        queryBuffer.append(" SELECT A.ACTIVATOR_ID, A.METEREVENT_ID, A.OPEN_TIME, A.MESSAGE, B.VALUE \n");
        queryBuffer.append(" FROM MDM_METEREVENT_LOG A \n");
        queryBuffer.append(" INNER JOIN METEREVENT B ON (A.METEREVENT_ID = B.ID) \n");   
        queryBuffer.append(" WHERE A.batch_id = :batchId \n");
        queryBuffer.append(" ORDER BY A.ACTIVATOR_ID, A.OPEN_TIME \n");
        
        SQLQuery query = getSession().createSQLQuery(queryBuffer.toString());
        
        query.setInteger("batchId", batchId);
        
        List<Object[]> obj = query.list();        
        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();        
        
        for(int i = 0, size = obj.size() ; i < size ; i++) {
        	
        	Object[] objVal = obj.get(i);
        	HashMap<String, Object> hm = new HashMap<String, Object>();
			
			hm.put("activator_id", ((String)objVal[0]));
			hm.put("meterevent_id", ((String)objVal[1]));
			hm.put("open_time", ((String)objVal[2]));
			hm.put("message", ((String)objVal[3]));			
			hm.put("value", String.valueOf(objVal[4]));
			
			result.add(hm);
		}
		
		return result;
	}
	
	@Override
	public void delete(Map<String, Object> condition) {
		
		try {				
			int batchId = (int)condition.get("BATCH_ID");
			
	        StringBuffer queryBuffer = new StringBuffer();
	        
	        queryBuffer.append(" DELETE FROM MDM_METEREVENT_LOG \n");   
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
	        
	        queryBuffer.append(" UPDATE MDM_METEREVENT_LOG SET \n");   
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
	public void updateInitTransferDate(List<MDMMeterEventLog> mdmMeterEventLogList) {
		
		for(MDMMeterEventLog mdmMeterEventLog : mdmMeterEventLogList) {
			try {				
					int batchId = mdmMeterEventLog.getBatchId();
					String activator_id = mdmMeterEventLog.getActivator_id();
					String open_time = mdmMeterEventLog.getOpen_time();
					String yyyymmdd = mdmMeterEventLog.getYyyymmdd();
					
			        StringBuffer queryBuffer = new StringBuffer();
			        
			        queryBuffer.append(" UPDATE MDM_METEREVENT_LOG SET \n");   
			        queryBuffer.append(" 	TRANSFER_DATETIME = 'error' \n");
			        queryBuffer.append(" WHERE 	batch_id = :batch_id \n");
			        queryBuffer.append(" AND 	activator_id = :activator_id \n");
			        queryBuffer.append(" AND 	open_time = :open_time \n");
			        queryBuffer.append(" AND 	yyyymmdd = :yyyymmdd \n");
			        
					SQLQuery query = getSession().createSQLQuery(queryBuffer.toString());
					
					query.setInteger("batch_id", batchId);
					query.setString("activator_id", activator_id);
					query.setString("open_time", open_time);
					query.setString("yyyymmdd", yyyymmdd);
					
					query.executeUpdate();  
				
	        }
	        catch (Exception e) {
	        	logger.error(e);
	        }			
		}		
	} // end for
	
}