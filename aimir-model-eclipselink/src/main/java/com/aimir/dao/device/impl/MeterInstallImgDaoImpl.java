package com.aimir.dao.device.impl;

import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractJpaDao;
import com.aimir.dao.device.MeterInstallImgDao;
import com.aimir.model.device.MeterInstallImg;
import com.aimir.util.Condition;

@Repository(value = "meterinstallimgDao")
public class MeterInstallImgDaoImpl  extends AbstractJpaDao<MeterInstallImg, Long> implements MeterInstallImgDao {

    Log logger = LogFactory.getLog(MeterInstallImgDaoImpl.class);
    
	public MeterInstallImgDaoImpl() {
		super(MeterInstallImg.class);
	}

    @Override
    public List<Object> getMeterInstallImgList(Integer meterId) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Class<MeterInstallImg> getPersistentClass() {
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
