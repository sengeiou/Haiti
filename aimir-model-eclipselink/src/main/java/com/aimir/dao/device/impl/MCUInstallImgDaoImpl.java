package com.aimir.dao.device.impl;

import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractJpaDao;
import com.aimir.dao.device.MCUInstallImgDao;
import com.aimir.model.device.MCUInstallImg;
import com.aimir.util.Condition;

@Repository(value = "mcuinstallimgDao")
public class MCUInstallImgDaoImpl  extends AbstractJpaDao<MCUInstallImg, Long> implements MCUInstallImgDao {

    Log logger = LogFactory.getLog(MCUInstallImgDaoImpl.class);
    
	public MCUInstallImgDaoImpl() {
		super(MCUInstallImg.class);
	}

    @Override
    public Class<MCUInstallImg> getPersistentClass() {
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
