/**
 * MdisMeterDaoImpl.java Copyright NuriTelecom Limited 2011
 */

package com.aimir.dao.device.impl;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractJpaDao;
import com.aimir.dao.device.MdisMeterDao;
import com.aimir.model.device.MdisMeter;
import com.aimir.util.Condition;

/**
 * MdisMeterDaoImpl.java Description 
 *
 * 
 * Date          Version     Author   Description
 * 2011. 12. 14  v1.0        문동규   MDIS Meter 모델 DaoImpl
 * 2012. 05. 10  v1.1        문동규   package 위치변경(mvm -> device)
 *
 */
@Repository(value = "mdisMeterDao")
public class MdisMeterDaoImpl extends AbstractJpaDao<MdisMeter, Integer> implements MdisMeterDao {

    public MdisMeterDaoImpl() {
        super(MdisMeter.class);
    }

    @Override
    public List<Map<String, Object>> getMdisMeterByMeterIdBulkCommand(
            List<Integer> meterIdList) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Class<MdisMeter> getPersistentClass() {
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