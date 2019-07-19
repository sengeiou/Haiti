package com.aimir.dao.device.impl;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.Query;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractJpaDao;
import com.aimir.dao.device.MeterMdisDao;
import com.aimir.dao.mvm.MeteringLpDao;
import com.aimir.model.device.Meter;
import com.aimir.util.Condition;

/**
 * MeterMdisDaoImpl.java Description 
 * <p>
 * <pre>
 * Date          Version     Author   Description
 * 2011. 11. 22. v1.0        문동규   MDIS 관련 method 를 기존 DaoImpl(MeterDaoImpl) 에서 분리
 * </pre>
 */
@Repository(value = "meterMdisDao")
public class MeterMdisDaoImpl extends AbstractJpaDao<Meter, Integer> implements MeterMdisDao {

    @Autowired
    MeteringLpDao meteringlpDao;
    
    Log logger = LogFactory.getLog(MeterMdisDaoImpl.class);
    
    public MeterMdisDaoImpl() {
        super(Meter.class);
    }

    public Meter get(String mdsId) {
        return findByCondition("mdsId", mdsId);
    }
    
    /**
     * 제주 실증단지에서 사용하는 11자리 미터키
     * @param installProperty
     * @return
     */
    public Meter getInstallProperty(String installProperty) {
        return findByCondition("installProperty", installProperty);
    }
    
    // Meter 정보 저장
    public Serializable setMeter(Meter meter) {
        return add(meter);
    }
    
    @SuppressWarnings("unchecked")
    public Meter getMeterByModemDeviceSerial(String deviceSerial, int modemPort) {
        String sql = "select m from Meter m where modemPort = :modemPort and modem.deviceSerial = :deviceSerial";
        Query query = em.createQuery(sql, Meter.class);
        query.setParameter("modemPort", modemPort);
        query.setParameter("deviceSerial", deviceSerial);
        return (Meter)query.getSingleResult();
    }
    
    @SuppressWarnings("unchecked")
    public List<Object> getMetersByMcuName(String name) {
        String sql = "select id from Meter where m.modem.mcu.sysID = :mcuId";
        Query query = em.createQuery(sql, Integer.class);
        query.setParameter("mcuId", name);
        return query.getResultList();
    }

    @Override
    public Class<Meter> getPersistentClass() {
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
    public Map<String, Object> getMeteringFailureMeter(
            Map<String, Object> param) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Integer getMeterCount(Map<String, Object> params) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Integer getAllMissingMeterCount(Map<String, Object> params) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Integer getPatialMissingMeterCount(Map<String, Object> params) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getMissingMetersByHour(Map<String, Object> params) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getMissingMetersByDay(Map<String, Object> params) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getMissingMeters(Map<String, Object> params) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getMiniChartMeterTypeByLocation(
            Map<String, Object> condition) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getMiniChartMeterTypeByCommStatus(
            Map<String, Object> condition) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getMiniChartLocationByMeterType(
            Map<String, Object> condition) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getMiniChartLocationByCommStatus(
            Map<String, Object> condition) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getMiniChartCommStatusByMeterType(
            Map<String, Object> condition) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getMiniChartCommStatusByLocation(
            Map<String, Object> condition) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getMeterSearchChart(Map<String, Object> condition) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getMeterSearchGrid(Map<String, Object> condition) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getMeterLogChart(Map<String, Object> condition) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getMeterLogGrid(Map<String, Object> condition) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getMeterCommLog(Map<String, Object> condition) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getMeterOperationLog(Map<String, Object> condition) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Map<String, Object> getMeterSearchCondition() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Meter> getMeterWithGpio(HashMap<String, Object> condition) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Meter> getMeterWithoutGpio(HashMap<String, Object> condition) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Meter> getMeterHavingModem(Integer id) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getMeteringDataByMeterChart(
            Map<String, Object> condition) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getMeteringDataByMeterGrid(
            Map<String, Object> condition) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getMeterListByModem(Map<String, Object> condition) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getMeterListByNotModem(Map<String, Object> condition) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Integer getMeterVEEParamsCount(HashMap<String, Object> hm) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getMeterListForContract(Map<String, Object> condition) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getGroupMember(Map<String, Object> condition) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getMeterSupplierList() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getMeterSearchChartMdis(Map<String, Object> condition) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getMeterSearchGridMdis(Map<String, Object> condition) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Map<String, Object>> getMeterMdisExportExcelData(
            Map<String, Object> conditionMap) {
        // TODO Auto-generated method stub
        return null;
    }
}