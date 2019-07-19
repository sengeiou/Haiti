/**
 * Copyright Nuri Telecom Corp.
 * 파일명: FirmwareDaoImpl
 * 작성일자/작성자 : 2010.12.06 최창희
 * @see 
 * 
 *
 * 펌웨어 관리자 페이지 DAO
 * 
 * ============================================================================
 * 수정 내역
 * NO  수정일자   수정자   수정내역
 * 
 * ============================================================================
 */
package com.aimir.dao.device.impl;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.aimir.dao.AbstractJpaDao;
import com.aimir.dao.device.FirmwareDao;
import com.aimir.dao.mvm.MeteringLpDao;
import com.aimir.dao.system.CodeDao;
import com.aimir.dao.system.SupplierDao;
import com.aimir.model.device.Firmware;
import com.aimir.model.device.FirmwareCodi;
import com.aimir.model.device.FirmwareMCU;
import com.aimir.model.device.FirmwareModem;
import com.aimir.util.Condition;

@Repository(value = "firmwareDao")
public class FirmwareDaoImpl extends AbstractJpaDao<Firmware, Integer> implements FirmwareDao {
	private static Log logger = LogFactory.getLog(FirmwareDaoImpl.class);

	public FirmwareDaoImpl() {
		super(Firmware.class);
	}
	
	@Transactional(readOnly=true, propagation=Propagation.REQUIRED)
	public Firmware get(int id){
		return findByCondition("id", id);
	}
	
	@Transactional(readOnly=true, propagation=Propagation.REQUIRED)
	public Firmware getByFirmwareId(String firmwareId) {
		return findByCondition("firmwareId", firmwareId);
	}

    @Override
    public List<FirmwareMCU> getMCUFirmwareList(Map<String, Object> condition) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<FirmwareCodi> getCodiFirmwareList(Map<String, Object> condition) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<FirmwareModem> getModemFirmwareList(
            Map<String, Object> condition) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getStatisticsStr(Map<String, Object> condition) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> distributeFmStatusEqDetail(Map<String, Object> param) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getFirmwareFileMgmListCNT(Map<String, Object> condition) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getFirmwareFileMgmList(Map<String, Object> condition) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String checkExistFirmware(Map<String, Object> condition) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getTriggerListStep1CNT(Map<String, Object> condition,
            String locationStr) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getTriggerListStep1(Map<String, Object> condition,
            String locationStr) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getTriggerListStep2(Map<String, Object> condition)
            throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Class<Firmware> getPersistentClass() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getSumFieldByCondition(Set<Condition> conditions,
            String field, String... groupBy) {
        // TODO Auto-generated method stub
        return null;
    }

	@Override
	public List<Object> getFirmwareFileList(Map<String, Object> condition) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
	
}
