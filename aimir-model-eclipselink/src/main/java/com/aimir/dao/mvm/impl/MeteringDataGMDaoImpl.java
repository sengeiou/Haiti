package com.aimir.dao.mvm.impl;

import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractJpaDao;
import com.aimir.dao.mvm.MeteringDataGMDao;
import com.aimir.model.mvm.MeteringDataGM;
import com.aimir.util.Condition;

@Repository(value = "meteringdatagmDao")
public class MeteringDataGMDaoImpl extends AbstractJpaDao<MeteringDataGM, Integer> implements MeteringDataGMDao {

	private static Log logger = LogFactory.getLog(MeteringDataDaoImpl.class);
    
	public MeteringDataGMDaoImpl() {
		super(MeteringDataGM.class);
	}

    @Override
    public Class<MeteringDataGM> getPersistentClass() {
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
