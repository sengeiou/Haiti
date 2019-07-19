package com.aimir.dao.mvm.impl;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Repository;

import com.aimir.constants.CommonConstants.DeviceType;
import com.aimir.dao.AbstractJpaDao;
import com.aimir.dao.mvm.RealTimeBillingEMDao;
import com.aimir.model.device.Meter;
import com.aimir.model.mvm.RealTimeBillingEM;
import com.aimir.util.Condition;

@Repository(value = "realtimebillingemDao")
public class RealTimeBillingEMDaoImpl extends AbstractJpaDao<RealTimeBillingEM, Integer> implements RealTimeBillingEMDao {

    @SuppressWarnings("unused")
    private static Log logger = LogFactory.getLog(RealTimeBillingEMDaoImpl.class);
    
    public RealTimeBillingEMDaoImpl() {
        super(RealTimeBillingEM.class);
    }

    @Override
    public Class<RealTimeBillingEM> getPersistentClass() {
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
    public List<Map<String, Object>> getBillingDataCurrent(
            Map<String, Object> conditionMap, boolean isCount) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Map<String, Object>> getBillingDataReportCurrent(
            Map<String, Object> conditionMap) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Map<String, Object>> getBillingDetailDataCurrent(
            Map<String, Object> conditionMap) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Map<String, Object> getMonthlyMaxDemandByMeter(Meter meter,
            String yyyymm) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Map<String, Object> getCummAtvPwrDmdMaxImpRateTot(
            Map<String, Object> conditionMap) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public RealTimeBillingEM getNewestRealTimeBilling(String mdevId,
            DeviceType mdevType, String yyyymm) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Map<String, Object>> getSgdgXam1RealTimeData(
            Map<String, Object> conditionMap) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void delete(String meterId, String yyyymmdd) {
        // TODO Auto-generated method stub
        
    }

}