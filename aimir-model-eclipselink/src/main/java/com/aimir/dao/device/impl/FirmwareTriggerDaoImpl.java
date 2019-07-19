/**
 * Copyright Nuri Telecom Corp.
 * 파일명: FirmwareTriggerDaoImpl
 * 작성일자/작성자 : 2011.01.13 박연경
 * @see 
 * 
 *
 * 펌웨어 배포 이력 DAO
 * 
 * ============================================================================
 * 수정 내역
 * NO  수정일자   수정자   수정내역
 * 
 * ============================================================================
 */
package com.aimir.dao.device.impl;

import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractJpaDao;
import com.aimir.dao.device.FirmwareTriggerDao;
import com.aimir.model.device.FirmwareTrigger;
import com.aimir.util.Condition;


@Repository(value = "firmwaretriggerDao")
public class FirmwareTriggerDaoImpl extends AbstractJpaDao<FirmwareTrigger, Long> implements FirmwareTriggerDao {
	private static Log logger = LogFactory.getLog(FirmwareTriggerDaoImpl.class);

	public FirmwareTriggerDaoImpl() {
		super(FirmwareTrigger.class);
	}

    @Override
    public void createTrigger(FirmwareTrigger firmwaretrigger) throws Exception {
        // TODO Auto-generated method stub
        
    }

    @Override
    public FirmwareTrigger getFirmwareTrigger(String tr_id) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void updateTrigger(FirmwareTrigger firmwaretrigger) throws Exception {
        // TODO Auto-generated method stub
        
    }

    @Override
    public Class<FirmwareTrigger> getPersistentClass() {
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
