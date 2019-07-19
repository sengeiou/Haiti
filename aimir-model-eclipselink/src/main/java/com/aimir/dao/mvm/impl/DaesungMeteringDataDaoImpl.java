package com.aimir.dao.mvm.impl;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractJpaDao;
import com.aimir.dao.mvm.DaesungMeteringDataDao;
import com.aimir.model.mvm.DaesungMeteringData;
import com.aimir.util.Condition;

@Repository(value = "daesungMeteringdataDao")
public class DaesungMeteringDataDaoImpl extends AbstractJpaDao<DaesungMeteringData, Integer> implements
		DaesungMeteringDataDao {

	@SuppressWarnings("unused")
    private static Log logger = LogFactory.getLog(DaesungMeteringDataDaoImpl.class);

	public DaesungMeteringDataDaoImpl() {
	    super(DaesungMeteringData.class);
	}

    @Override
    public Class<DaesungMeteringData> getPersistentClass() {
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