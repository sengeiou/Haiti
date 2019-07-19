package com.aimir.dao.mvm.impl;

import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractJpaDao;
import com.aimir.dao.mvm.MeteringDataHMDao;
import com.aimir.model.mvm.MeteringDataHM;
import com.aimir.util.Condition;

@Repository(value = "meteringdatahmDao")
public class MeteringDataHMDaoImpl extends AbstractJpaDao<MeteringDataHM, Integer> implements MeteringDataHMDao {

	private static Log logger = LogFactory.getLog(MeteringDataDaoImpl.class);
    
	public MeteringDataHMDaoImpl() {
		super(MeteringDataHM.class);
	}

    @Override
    public Class<MeteringDataHM> getPersistentClass() {
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
