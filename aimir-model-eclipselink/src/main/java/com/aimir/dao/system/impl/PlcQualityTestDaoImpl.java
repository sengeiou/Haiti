package com.aimir.dao.system.impl;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractJpaDao;
import com.aimir.dao.system.PlcQualityTestDao;
import com.aimir.model.system.PlcQualityTest;
import com.aimir.util.Condition;

@Repository("PlcQualityTestDao")
public class PlcQualityTestDaoImpl extends AbstractJpaDao<PlcQualityTest, Integer> implements PlcQualityTestDao {

	public PlcQualityTestDaoImpl() {
	    super(PlcQualityTest.class);
	}
	
	public Integer getCount(Integer zigId) {
        StringBuffer countQuery = new StringBuffer();
        countQuery.append("\n SELECT COUNT(*) ");
        countQuery.append("\n FROM PlcQualityTestDetail pd ");
        countQuery.append("\n WHERE pd.zigId = :zigId");
        
        Query countQueryObj = getEntityManager().createQuery(countQuery.toString());
        
        countQueryObj.setParameter("zigId", zigId);

	Number count = (Number)countQueryObj.getSingleResult();
        
        return count.intValue();
	}

    @Override
    public Class<PlcQualityTest> getPersistentClass() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getSumFieldByCondition(Set<Condition> conditions,
            String field, String... groupBy) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getPlcQualityResult(Map<String, Object> condition) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public PlcQualityTest getInfoByZig(String zigId) {
        // TODO Auto-generated method stub
        return null;
    }

}