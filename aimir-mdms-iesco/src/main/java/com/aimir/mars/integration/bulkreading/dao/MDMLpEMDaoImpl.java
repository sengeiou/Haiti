package com.aimir.mars.integration.bulkreading.dao;

import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractHibernateGenericDao;
import com.aimir.mars.integration.bulkreading.model.MDMLpEM;

@Repository(value = "mdmLpEMDao")
public class MDMLpEMDaoImpl extends AbstractHibernateGenericDao<MDMLpEM, Integer> implements MDMLpEMDao {
	
	protected static Log logger = LogFactory.getLog(MDMLpEMDaoImpl.class);
	
	@Autowired
	protected MDMLpEMDaoImpl(SessionFactory sessionFactory) {
		super(MDMLpEM.class);
		super.setSessionFactory(sessionFactory);
	}
	
	@Override
	public void delete(Map<String, Object> condition) {
		
		try {				
			int batchId = (int)condition.get("BATCH_ID");
			
	        StringBuffer queryBuffer = new StringBuffer();
	        
	        queryBuffer.append(" DELETE FROM MDM_LP_EM \n");   
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
	        
	        queryBuffer.append(" UPDATE MDM_LP_EM SET \n");   
	        queryBuffer.append(" 	TRANSFER_DATETIME = to_char(sysdate,'yyyymmddhh24miss') \n");
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
	public void updateInitTransferDate(List<MDMLpEM> mdmLpEMList) {
		
		for(MDMLpEM mdmLpEM : mdmLpEMList) {
			
			try {
				
				int batchId = mdmLpEM.getBatchId();
				String mdevId = mdmLpEM.getMdevId();
				String yyyymmddhhmmss = mdmLpEM.getYyyymmddhhmmss();
				String mdevType = mdmLpEM.getMdevType();
				int dst = mdmLpEM.getDst();
				
		        StringBuffer queryBuffer = new StringBuffer();
		        
		        queryBuffer.append(" UPDATE MDM_LP_EM SET \n");   
		        queryBuffer.append(" 	TRANSFER_DATETIME = 'error' \n");
		        queryBuffer.append(" WHERE MDEV_ID = :mdevId and YYYYMMDDHHMMSS = :yyyymmddhhmmss and MDEV_TYPE = :mdevType and DST = :dst \n");
		        
				SQLQuery query = getSession().createSQLQuery(queryBuffer.toString());
				query.setString("mdevId", mdevId);		
				query.setString("yyyymmddhhmmss", yyyymmddhhmmss);
				query.setString("mdevType", mdevType);
				query.setInteger("dst", dst);
				
				query.executeUpdate();  
	        }
	        catch (Exception e) {
	        	logger.error(e);
	        }
		} // end for
	}

	@Override
	public List<MDMLpEM> select(Map<String, Object> condition) {
		
		String mdevId = (String)condition.get("meter_no");
		String yyyymmddhhmmss = (String)condition.get("yyyymmddhhmmss");
		
		Criteria criteria = getSession().createCriteria(MDMLpEM.class, "A");
    	
		if(mdevId != null && !"".equals(mdevId)) {
    		criteria.add(Restrictions.eq("A.mdevId", mdevId)); 	
    	}
		
		if(yyyymmddhhmmss != null && !"".equals(yyyymmddhhmmss)) {
    		criteria.add(Restrictions.eq("A.yyyymmddhhmmss", yyyymmddhhmmss)); 	
    	}
		
		criteria.addOrder(Order.desc("yyyymmddhhmmss"));
		
		criteria.setFirstResult(0);
		criteria.setMaxResults(1);
		
		List<MDMLpEM> result = (List<MDMLpEM>) criteria.list();
		
    	return result;
	}
}