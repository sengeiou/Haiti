package com.aimir.dao.device.impl;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractJpaDao;
import com.aimir.dao.device.FirmwareMCUDao;
import com.aimir.model.device.FirmwareBoard;
import com.aimir.model.device.FirmwareMCU;
import com.aimir.util.Condition;

@Repository(value = "firmwaremcuDao")
public class FirmwareMCUDaoImpl extends AbstractJpaDao<FirmwareMCU, Integer> implements FirmwareMCUDao {
	private static Log logger = LogFactory.getLog(FirmwareMCUDaoImpl.class);

	public FirmwareMCUDaoImpl() {
		super(FirmwareMCU.class);
	}

    @Override
    public FirmwareMCU get(String firmwareId) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getDistriButeMcuIdCnt(Map<String, Object> param,
            String location_id, String location_name) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getMcuEquipCnt(Map<String, Object> param) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getdistributeMcuIdDivList(Map<String, Object> param,
            String location_id, String location_name) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getMcuFirmwareList(Map<String, Object> condition) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> distributeWriterStatus(Map<String, Object> param) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> distributeLocationStatus(Map<String, Object> param) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> distributeTriggerIdStatus(Map<String, Object> param) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getFirmwareMcuListCNT(Map<String, Object> condition) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> distributeMCULocStatusDetail(Map<String, Object> param) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void addFirmWareMCU(FirmwareMCU firmware, FirmwareBoard firmwareBoard)
            throws Exception {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void updateFirmWareMCU(FirmwareMCU firmware,
            FirmwareBoard firmwareBoard) throws Exception {
        // TODO Auto-generated method stub
        
    }

    @Override
    public List<Object> getReDistMcuList(Map<String, Object> param) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getIDbyMcuSysId(String sys_id) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Class<FirmwareMCU> getPersistentClass() {
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
