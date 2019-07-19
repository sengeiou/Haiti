package com.aimir.dao.mvm.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractHibernateGenericDao;
import com.aimir.dao.mvm.VEEParameterDao;
import com.aimir.model.mvm.VEEParameter;
import com.aimir.util.Condition;
import com.aimir.util.StringUtil;

@Repository(value = "veeparameterDao")
public class VEEParameterDaoImpl extends AbstractHibernateGenericDao<VEEParameter, Integer> implements VEEParameterDao {

	private static Log logger = LogFactory.getLog(VEEParameterDaoImpl.class);
    
	@Autowired
	protected VEEParameterDaoImpl(SessionFactory sessionFactory) {
		super(VEEParameter.class);
		super.setSessionFactory(sessionFactory);
	}
	
	public List<VEEParameter> getVEEParameterByListCondition(Set<Condition> set) {         

        return findByConditions(set);
    }
	
	
	public List<String> getParameterNames(){
		Query query = null;
    	try {
            StringBuffer sb = new StringBuffer();
            sb.append("\n SELECT DISTINCT PARAMETER FROM VEE_PARAMETER");

            query = getSession().createSQLQuery(sb.toString());
    		
    	}catch(Exception e){
			e.printStackTrace();
		}
		
		return query.list(); 
	}
	
	public List<VEEParameter> getParameterList(String ruleType) {
		ruleType = StringUtil.nullToBlank(ruleType);
		Criteria criteria = getSession().createCriteria(VEEParameter.class);
		if (!ruleType.equals("")) {
			criteria.add(Restrictions.eq("ruleType", ruleType));
		}
		criteria.addOrder(Order.asc("localName"));
		return criteria.list();
	}
	
	public List<Object> getParameterDataList(HashMap<String, Object> hm){
		String ruleType = (String)hm.get("ruleType");
		Query query = null;
        List<Object> result = new ArrayList<Object> ();
    	try {
            StringBuffer sb = new StringBuffer();
            sb.append("\n SELECT DISTINCT LOCAL_NAME, ITEM, USE_THRESHOLD, CONDITION_ITEM, THRESHOLD_PERIOD, THRESHOLD_ITEM, THRESHOLD_CONDITION1, THRESHOLD1, THRESHOLD_CONDITION2, THRESHOLD2");
            sb.append("\n FROM VEE_PARAMETER");
            sb.append("\n WHERE RULE_TYPE =:ruleType ");
            sb.append("\n ORDER BY LOCAL_NAME");

            query = getSession().createSQLQuery(sb.toString()).setParameter("ruleType", ruleType);
            
    		List<Object> list = query.list();

    		for(int i = 0; list != null && i < list.size(); i++) {
    			Object[] object = (Object[])list.get(i);

    			String condition_item = (String)object[3];
    			String threshold_period = (String)object[4];
    			String threshold_item = (String)object[5];
    			String threshold_condition1 = (String)object[6];
    			String threshold1 = (Number)object[7] == null ? "" : ((Number)object[7]).intValue()+"";
    			//2번째 조건이 없을경우도 반영
    			String threshold_condition2 = null;
    			String threshold2			= null;
    			
    			if(object[8]!=null && ((String) object[8]).length() > 0){
	    			threshold_condition2 	= (String)object[8];
	    			threshold2 				= ((Number)object[9]).intValue()+"";
    			}
    			
    			Object[] obj = new Object[4];
    			obj[0] = object[0];
    			obj[1] = object[1];
    			obj[2] = object[2];
    			if(threshold_condition2 !=null && threshold_condition2.length() > 0){
	    			obj[3] = StringUtil.nullToBlank(condition_item) 
	    			       + StringUtil.nullToBlank(threshold_period) + " "
	    			       + StringUtil.nullToBlank(threshold_item) + " "
	    			       + StringUtil.nullToBlank(threshold_condition1) + " "
	    			       + StringUtil.nullToBlank(threshold1) + " AND "
	    			       + StringUtil.nullToBlank(threshold_period) + " "
	    			       + StringUtil.nullToBlank(threshold_item) + " "
	    			       //+ StringUtil.nullToBlank(threshold_condition1) + " "
	    			       + StringUtil.nullToBlank(threshold_condition2) + " "
	    			       + StringUtil.nullToBlank(threshold2);
    			}else{
    				obj[3] = StringUtil.nullToBlank(condition_item) 
 			       + StringUtil.nullToBlank(threshold_period) + " "
 			       + StringUtil.nullToBlank(threshold_item) + " "
 			       + StringUtil.nullToBlank(threshold_condition1) + " "
 			       + StringUtil.nullToBlank(threshold1) + " ";
    			}
    			result.add(obj);

    		} 
    		
    	}catch(Exception e){
			e.printStackTrace();
		}
		
		return result; 
	} 
	
	public List<Object> getParameterDataList(HashMap<String, Object> hm, int startRow, int pageSize){
		String ruleType = (String)hm.get("ruleType");
		Query query = null;
    	try {
            StringBuffer sb = new StringBuffer();
            sb.append("\n SELECT DISTINCT localName, item, useThreshold, condition ");
            sb.append("\n FROM VEEParameter");
            sb.append("\n WHERE ruleType =:ruleType ");
            sb.append("\n ORDER BY localName");

            query = getSession().createQuery(sb.toString()).setParameter("ruleType", ruleType);
            
    		
    	}catch(Exception e){
			e.printStackTrace();
		}
		
		return query.list(); 
	} 
}