/**
 * BillingMonthEMDaoImpl.java Copyright NuriTelecom Limited 2011
 */

package com.aimir.dao.mvm.impl;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractJpaDao;
import com.aimir.dao.mvm.BillingMonthEMDao;
import com.aimir.model.device.Meter;
import com.aimir.model.mvm.BillingMonthEM;
import com.aimir.model.system.Contract;
import com.aimir.util.Condition;

/**
 * BillingMonthEMDaoImpl.java Description 
 *
 * 
 * Date          Version     Author   Description
 * 2011. 4. 28.   v1.0       김상연         BillingMonthEM 조회 조건(BillingMonthEM)
 * 2011. 5. 04.   v1.1       김상연         해당 계약의 특정 년 별 조회
 * 2011. 5. 13.   v1.2       김상연        동일 공급사 평균 사용량 조회
 * 2011. 6. 09.   v1.3       김상연        해당 계약 최고 사용 금액 조회
 * 2011. 6. 27.   v1.4       김상연        사용 평균 비용 조회
 *
 */
@Repository(value = "billingmonthemDao")
public class BillingMonthEMDaoImpl extends AbstractJpaDao<BillingMonthEM, Integer> implements BillingMonthEMDao {

	@SuppressWarnings("unused")
	private static Log logger = LogFactory.getLog(BillingMonthEMDaoImpl.class);
    
	public BillingMonthEMDaoImpl() {
		super(BillingMonthEM.class);
	}

    @Override
    public List<Map<String, Object>> getBillingDataMonthly(
            Map<String, Object> conditionMap, boolean isCount) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Map<String, Object>> getBillingDataReportMonthly(
            Map<String, Object> conditionMap) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Map<String, Object>> getBillingDataReportMonthlyWithLastMonth(
            Map<String, Object> conditionMap) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Map<String, Object>> getBillingDetailDataMonthly(
            Map<String, Object> conditionMap) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<BillingMonthEM> getBillingMonthEMs(
            BillingMonthEM billingMonthEM, String startDay, String finishDay) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Map<String, Object> getBillingYearEm(BillingMonthEM billingMonthEM) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Double getAverageUsage(BillingMonthEM billingMonthEM) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Double getMaxBill(Contract contract, String yyyymmdd) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Double getAverageBill(Contract contract, String yyyymmdd) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getBillingMonthEMsAvg(BillingMonthEM billingMonthEM,
            String fromDay, String toDay) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getBillingMonthEMsComboBox(
            BillingMonthEM billingMonthEM, String fromDay, String toDay) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Map<String, Object> getCurrMonCummMaxDemandData(
            Map<String, Object> conditionMap) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public BillingMonthEM getBillingMonthEM(Meter meter, String date) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Class<BillingMonthEM> getPersistentClass() {
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