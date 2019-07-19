package com.aimir.dao.mvm.impl;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.aimir.dao.AbstractJpaDao;
import com.aimir.dao.mvm.LpEMDao;
import com.aimir.model.device.Meter;
import com.aimir.model.mvm.DataGaps;
import com.aimir.model.mvm.LpEM;
import com.aimir.util.Condition;

@Repository(value="lpemDao")
public class LpEMDaoImpl extends AbstractJpaDao<LpEM, Integer> implements LpEMDao{
    private static Log log = LogFactory.getLog(LpEMDaoImpl.class);
    private static String EnergyMeter = "1.3.1.1";

    public LpEMDaoImpl() {
        super(LpEM.class);
    }

    @Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
    public List<LpEM> getLpEMsByListCondition(Set<Condition> set) {

        return findByConditions(set);
    }

    @Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
    public List<Object> getLpEMsCountByListCondition(Set<Condition> set) {

        return findTotalCountByConditions(set);
    }

    @Override
    public Class<LpEM> getPersistentClass() {
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
    public List<Object> getLpEMsMaxMinSumAvg(Set<Condition> conditions,
            String div) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void updateLpEM(LpEM lpem) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public List<DataGaps> search(int supplier, String tabType, String startDate,
            String endDate, int totalCount, String meter, String deviceCodeType,
            String deviceId) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getLpEMs(String mdevType, String mdevId, int dst,
            String startYYYYMMDDHH, String endYYYYMMDDHH) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getLpEMsByNoSendedACD() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getLpEMsByNoSended() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getLpEMsByNoSendedDummy(String selectedDate) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getLpEMsByNoSended(String mdevType) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getLpEMsByNoSended(String mdevType,
            String lpTableName) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void updateSendedResult(LpEM lpem) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void updateSendedResultByCondition(
            Map<String, Object> conditionMap) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public List<Object> getTotalCummulValue(String mdevId, String yyyymmdd,
            int hh, int mm, int interval) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getHourCummulValueNoSelf(String mdevId, String yyyymmdd,
            int hh, int mm, int interval) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getMinCummulValueNoSelf(String mdevId, String yyyymmdd,
            int hh, int mm, int interval) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getConsumptionEmCo2LpValuesParentId(
            Map<String, Object> condition) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getConsumptionEmLpValuesParentId(
            Map<String, Object> condition) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int getLpInterval(String mdevId) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public Double getMeterDetailInfoLpData(Map<String, Object> conditionMap,
            Integer channel) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<LpEM> getLastData(Integer meterId) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<LpEM> getLastData(String mdsId) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<LpEM> getLpEMByMeter(Meter meter, String yyyymmddhh,
            Integer... channels) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<LpEM> getProperLpEMByMeter(Meter meter, String yyyymmddhh,
            Integer... channels) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void delete(String meterId, String yyyymmdd) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void oldLPDelete(String mdsId, String bDate) {
        // TODO Auto-generated method stub
        
    }
    
    @Override
    public List<Object> getLpReportByDcuSys() 
    {
    	return null;
    }
    
    @Override
    public List<Object> getMeterReportByDcuSys() 
    {
    	return null;
    }

}