package com.aimir.dao.device.impl;

import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractJpaDao;
import com.aimir.dao.device.VolumeCorrectorDao;
import com.aimir.model.device.VolumeCorrector;
import com.aimir.util.Condition;

@Repository(value = "volumecorrectorDao")
public class VolumeCorrectorDaoImpl extends AbstractJpaDao<VolumeCorrector, Integer> implements VolumeCorrectorDao {

    Log log = LogFactory.getLog(VolumeCorrectorDaoImpl.class);
    
	public VolumeCorrectorDaoImpl() {
		super(VolumeCorrector.class);
	}

    @Override
    public Class<VolumeCorrector> getPersistentClass() {
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