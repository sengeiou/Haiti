/**
 * Copyright Nuri Telecom Corp.
 * 파일명: FirmwareModemDaoImpl
 * 작성일자/작성자 :Nuri com
 * @see 
 *
 * 펌웨어 관리자 페이지 Component
 * 
 * ============================================================================
 * 수정 내역
 * NO  수정일자           수정자             수정내역
 * 1.  2010.12.24  최창희             펌웨어 관리자 페이지 관련 함수 추가
 * 2.
 * ============================================================================
 */
package com.aimir.dao.device.impl;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractJpaDao;
import com.aimir.dao.device.FirmwareModemDao;
import com.aimir.model.device.FirmwareBoard;
import com.aimir.model.device.FirmwareModem;
import com.aimir.util.Condition;

@Repository(value = "firmwaremodemDao")
public class FirmwareModemDaoImpl extends AbstractJpaDao<FirmwareModem, Integer> implements FirmwareModemDao {
	private static Log logger = LogFactory.getLog(FirmwareModemDaoImpl.class);

	public FirmwareModemDaoImpl() {
		super(FirmwareModem.class);
	}

	public FirmwareModem get(String firmwareId) {
		return findByCondition("firmwareId", firmwareId);
	}

    @Override
    public String getModemEquipCnt(Map<String, Object> param) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getDistriButeModemIdCnt(Map<String, Object> param,
            String location_id, String location_name) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getdistributeModemIdDivList(Map<String, Object> param,
            String location_id, String location_name) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getModemFirmwareList(Map<String, Object> condition) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getFirmwareModemListCNT(Map<String, Object> condition) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getDistriButeModemModelListCnt(Map<String, Object> param,
            String mcuId) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getDistriButeModemModelList(Map<String, Object> param,
            String mcuId) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> distributeModemLocationStatus(Map<String, Object> param) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> distributeModemTriggerIdStatus(Map<String, Object> param) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> distributeModemLocStatusDetail(Map<String, Object> param) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void addFirmWareModem(FirmwareModem firmware,
            FirmwareBoard firmwareBoard) throws Exception {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void updateFirmWareModem(FirmwareModem firmware,
            FirmwareBoard firmwareBoard) throws Exception {
        // TODO Auto-generated method stub
        
    }

    @Override
    public List<Object> getReDistModemList(Map<String, Object> param) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getMcuBuildByModemFirmware(Map<String, Object> param) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Class<FirmwareModem> getPersistentClass() {
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
