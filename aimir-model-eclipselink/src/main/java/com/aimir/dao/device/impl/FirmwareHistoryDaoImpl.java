/**
 * Copyright Nuri Telecom Corp.
 * 파일명: FirmwareHistoryDaoImpl
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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractJpaDao;
import com.aimir.dao.device.FirmwareHistoryDao;
import com.aimir.model.device.FirmwareHistory;
import com.aimir.model.device.FirmwareHistoryPk;
import com.aimir.util.Condition;

@Repository(value = "firmwarehistoryDao")
public class FirmwareHistoryDaoImpl extends AbstractJpaDao<FirmwareHistory, FirmwareHistoryPk> implements FirmwareHistoryDao {
	private static Log logger = LogFactory.getLog(FirmwareHistoryDaoImpl.class);

	protected FirmwareHistoryDaoImpl() {
		super(FirmwareHistory.class);
	}

    @Override
    public String historyCheckExistEquip(Map<String, Object> param)
            throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getTriggerHistory(Map<String, Object> param) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void insertFirmHistory(FirmwareHistory firmwareHistory)
            throws Exception {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void updateFirmHistory(FirmwareHistory firmwarehistory,
            Map<String, Object> param) throws Exception {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void updateFirmwareHistory(FirmwareHistory firmwareHistory,
            ArrayList<String> updateFirmwareHistory) throws Exception {
        // TODO Auto-generated method stub
        
    }

    @Override
    public List<Object> getScheduleCheckOTAState(String equip_kind)
            throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void updateFirmHistoryBySchedule(String sql) throws Exception {
        // TODO Auto-generated method stub
        
    }

    @Override
    public String equipTypeBytrID(Integer tr_id) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Class<FirmwareHistory> getPersistentClass() {
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
