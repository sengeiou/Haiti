package com.aimir.dao.mvm.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

import javax.persistence.Query;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractJpaDao;
import com.aimir.dao.mvm.VEEParameterDao;
import com.aimir.model.mvm.VEEParameter;
import com.aimir.util.Condition;

@Repository(value = "veeparameterDao")
public class VEEParameterDaoImpl extends AbstractJpaDao<VEEParameter, Integer> implements VEEParameterDao {

	private static Log logger = LogFactory.getLog(VEEParameterDaoImpl.class);
    
	public VEEParameterDaoImpl() {
		super(VEEParameter.class);
	}
	
	public List<VEEParameter> getVEEParameterByListCondition(Set<Condition> set) {         

        return findByConditions(set);
    }
	
	
	public List<String> getParameterNames(){
		Query query = null;
        StringBuffer sb = new StringBuffer();
        sb.append("\n SELECT DISTINCT PARAMETER FROM VEEParameter");

        query = em.createQuery(sb.toString(), VEEParameter.class);
		
		return query.getResultList(); 
	}

    @Override
    public List<Object> getParameterDataList(HashMap<String, Object> hm) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getParameterDataList(HashMap<String, Object> hm,
            int startRow, int pageSize) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<VEEParameter> getParameterList(String ruleType) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Class<VEEParameter> getPersistentClass() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getSumFieldByCondition(Set<Condition> conditions,
            String field, String... groupBy) {
        // TODO Auto-generated method stub
        return null;
    }
}