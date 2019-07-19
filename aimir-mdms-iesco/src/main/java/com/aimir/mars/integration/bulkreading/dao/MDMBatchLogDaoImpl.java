package com.aimir.mars.integration.bulkreading.dao;

import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractHibernateGenericDao;
import com.aimir.mars.integration.bulkreading.model.MDMBatchLog;

@Repository(value = "mdmBatchLogDao")
public class MDMBatchLogDaoImpl extends AbstractHibernateGenericDao<MDMBatchLog, Integer> implements MDMBatchLogDao {
	
	protected MDMBatchLogDaoImpl(SessionFactory sessionFactory) {
		super(MDMBatchLog.class);
		super.setSessionFactory(sessionFactory);
	}
	
	protected static Log logger = LogFactory.getLog(MDMBatchLogDaoImpl.class);
	
	@Override
	public void updateBatchStatus(Map<String, Object> condition) {
		
		try {	
			
			int batchId = (int)condition.get("BATCH_ID");
			String batchStatus = (String)condition.get("BATCH_STATUS");
			
	        StringBuffer queryBuffer = new StringBuffer();
	        
	        queryBuffer.append(" UPDATE MDM_BATCH_LOG SET \n");
	        queryBuffer.append(" 		 batch_status = :batchStatus \n");	    
	        queryBuffer.append(" WHERE 	batch_id = :batchId \n");	        
	        
			SQLQuery query = getSession().createSQLQuery(queryBuffer.toString());
			
			query.setString("batchStatus", batchStatus);
			query.setInteger("batchId", batchId);
			
			query.executeUpdate();  
        }
        catch (Exception e) {
        	logger.error(e);
        }
	}

	@Override
	public List<MDMBatchLog> selectBatchList(Map<String, Object> condition) {
		
		String batchStatus = (String)condition.get("batchStatus");
		String batchType = (String)condition.get("batchType");
		Integer batch_id = (Integer)condition.get("batch_id");
		String maxResult = (String)condition.get("maxResult");
		
		
		Criteria criteria = getSession().createCriteria(MDMBatchLog.class, "A");
    	
    	if(batchStatus != null && !"".equals(batchStatus)) {
    		criteria.add(Restrictions.eq("A.batchStatus", batchStatus)); 	
    	}
    	
    	if(batchType != null && !"".equals(batchType)) {
    		criteria.add(Restrictions.eq("A.batchType", batchType)); 	
    	}
    	
    	if(batch_id != null) {
    		criteria.add(Restrictions.eq("A.batch_id", batch_id)); 	
    	}
    	
		criteria.setFirstResult(0);
		criteria.setMaxResults(Integer.valueOf(maxResult));	
		
    	return criteria.list();
	}
}