package com.aimir.dao.mvm.impl;

import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractJpaDao;
import com.aimir.dao.mvm.MeteringDataWMDao;
import com.aimir.model.mvm.MeteringDataWM;
import com.aimir.util.Condition;

@Repository(value = "meteringdatawmDao")
public class MeteringDataWMDaoImpl extends AbstractJpaDao<MeteringDataWM, Integer> implements MeteringDataWMDao {

	private static Log logger = LogFactory.getLog(MeteringDataDaoImpl.class);
    
	public MeteringDataWMDaoImpl() {
		super(MeteringDataWM.class);
	}

    @Override
    public Class<MeteringDataWM> getPersistentClass() {
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
