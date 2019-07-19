package com.aimir.dao.mvm.impl;

import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractJpaDao;
import com.aimir.dao.mvm.MeteringDataVCDao;
import com.aimir.model.mvm.MeteringDataVC;
import com.aimir.util.Condition;

@Repository(value = "meteringdatavcDao")
public class MeteringDataVCDaoImpl extends AbstractJpaDao<MeteringDataVC, Integer> implements MeteringDataVCDao {

	private static Log logger = LogFactory.getLog(MeteringDataDaoImpl.class);
    
	public MeteringDataVCDaoImpl() {
		super(MeteringDataVC.class);
	}

    @Override
    public Class<MeteringDataVC> getPersistentClass() {
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
