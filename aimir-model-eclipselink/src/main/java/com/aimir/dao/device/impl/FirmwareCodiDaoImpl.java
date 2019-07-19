/**
 * Copyright Nuri Telecom Corp.
 * 파일명: FirmwareCodiDaoImpl
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
import com.aimir.dao.device.FirmwareCodiDao;
import com.aimir.model.device.FirmwareBoard;
import com.aimir.model.device.FirmwareCodi;
import com.aimir.util.Condition;

@Repository(value = "firmwarecodiDao")
public class FirmwareCodiDaoImpl extends AbstractJpaDao<FirmwareCodi, Integer> implements FirmwareCodiDao {
	private static Log logger = LogFactory.getLog(FirmwareCodiDaoImpl.class);

	public FirmwareCodiDaoImpl() {
		super(FirmwareCodi.class);
	}

	public FirmwareCodi get(String firmwareId) {
		return findByCondition("firmwareId", firmwareId);
	}

    @Override
    public String getMcuCodiEquipCnt(Map<String, Object> param) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getDistriButeCodiIdCnt(Map<String, Object> param,
            String location_id, String location_name) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getdistributeCodiIdDivList(Map<String, Object> param,
            String location_id, String location_name) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getMcuCodiFirmwareList(Map<String, Object> condition) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getFirmwareMcuCodiListCNT(Map<String, Object> condition) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getDistriButeMcuCodiModelListCnt(Map<String, Object> param,
            String mcuId) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getDistriButeMcuCodiModelList(
            Map<String, Object> param, String mcuId) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> distributeCodiLocationStatus(Map<String, Object> param) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> distributeCodiTriggerIdStatus(Map<String, Object> param) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> distributeCodiLocStatusDetail(Map<String, Object> param) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void addFirmWareCodi(FirmwareCodi firmware,
            FirmwareBoard firmwareBoard) throws Exception {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void updateFirmWareCodi(FirmwareCodi firmware,
            FirmwareBoard firmwareBoard) throws Exception {
        // TODO Auto-generated method stub
        
    }

    @Override
    public List<Object> getReDistCodiList(Map<String, Object> param) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getMcuBuildByCodiFirmware(Map<String, Object> param) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Class<FirmwareCodi> getPersistentClass() {
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
