// INSERT SP-193
package com.aimir.dao.device.impl;

import java.util.List;
import java.util.Set;

import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.springframework.stereotype.Repository;

import com.aimir.constants.CommonConstants.ThresholdName;
import com.aimir.dao.AbstractJpaDao;
import com.aimir.dao.device.ThresholdDao;
import com.aimir.model.device.Threshold;
import com.aimir.util.Condition;

@Repository(value = "thresholdDao")
public class ThresholdDaoImpl extends AbstractJpaDao<Threshold, Integer> implements ThresholdDao {

	public ThresholdDaoImpl() {
		super(Threshold.class);
	}	
	
	@SuppressWarnings("unchecked")
	public Threshold getThresholdByname(String name) {
		StringBuffer hqlBuf = new StringBuffer();
		hqlBuf.append(" SELECT t.id, t.duration, t.limit, t.more, t.name ");
		hqlBuf.append(" FROM Threshold t");
		hqlBuf.append(" WHERE t.name = :name"); 
		hqlBuf.append(" ORDER BY t.id"); 	
		
		Query query = em.createQuery(hqlBuf.toString());

		query.setParameter("name", ThresholdName.valueOf(name));
		
		List result = query.getResultList();
		
		if (result.size() == 0) {
			return null;
		}
		else {
			Object[] resultData = (Object[]) result.get(0);
			Threshold threshold = new Threshold();
			threshold.setId(Integer.parseInt(resultData[0].toString()));
			if (resultData[1] != null)threshold.setDuration(resultData[1].toString());
			if (resultData[2] != null)threshold.setLimit(Integer.parseInt(resultData[2].toString()));
			if (resultData[3] != null)threshold.setMore(Integer.parseInt(resultData[3].toString()));
			threshold.setThresholdName(resultData[4].toString());
			return threshold;
		}
	}
	
    @Override
    public Class<Threshold> getPersistentClass() {
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
