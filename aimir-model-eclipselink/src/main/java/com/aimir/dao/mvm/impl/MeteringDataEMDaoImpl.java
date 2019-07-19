package com.aimir.dao.mvm.impl;

import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractJpaDao;
import com.aimir.dao.mvm.MeteringDataEMDao;
import com.aimir.model.mvm.MeteringDataEM;
import com.aimir.util.Condition;

@Repository(value = "meteringdataemDao")
public class MeteringDataEMDaoImpl extends AbstractJpaDao<MeteringDataEM, Integer> implements MeteringDataEMDao {

	private static Log logger = LogFactory.getLog(MeteringDataDaoImpl.class);
    
	public MeteringDataEMDaoImpl() {
		super(MeteringDataEM.class);
	}

    @Override
    public Class<MeteringDataEM> getPersistentClass() {
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
