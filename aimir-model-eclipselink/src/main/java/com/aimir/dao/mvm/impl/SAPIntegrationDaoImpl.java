package com.aimir.dao.mvm.impl;

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractJpaDao;
import com.aimir.dao.mvm.SAPIntegrationDao;
import com.aimir.model.mvm.SAPIntegrationLog;
import com.aimir.util.Condition;
import com.aimir.util.StringUtil;

@Repository(value = "SAPIntegrationDao")
public class SAPIntegrationDaoImpl extends AbstractJpaDao<SAPIntegrationLog, Integer> implements SAPIntegrationDao {

    public SAPIntegrationDaoImpl() {
		super(SAPIntegrationLog.class);
	}
	
	public List<SAPIntegrationLog> getOrgerList(Map<String, Object> condition) {
		
		String deadLine		 	= StringUtil.nullToBlank(condition.get("deadLine"));
		
		StringBuffer sb = new StringBuffer();
		sb.append(" select s ");
		sb.append("   FROM SAPIntegrationLog s 			");
		sb.append("\n WHERE  s.deadline >= :deadLine 	");
		sb.append("\n AND  s.resultState IS NULL 		");
		
		Query query = em.createQuery(sb.toString()).setParameter("deadLine", deadLine);
        
		List<SAPIntegrationLog> list = query.getResultList();
		return list;
	}

    @Override
    public List<Object> getOutBoundGridData(Map<String, Object> condition) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getInBoundGridData(Map<String, Object> condition) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getErrorLogGridData(Map<String, Object> condition) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Class<SAPIntegrationLog> getPersistentClass() {
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
