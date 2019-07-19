/**
 * BillingDayEMDaoImpl.java Copyright NuriTelecom Limited 2011
 */

package com.aimir.dao.mvm.impl;

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.Query;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractJpaDao;
import com.aimir.dao.mvm.BillingDayEMDao;
import com.aimir.model.mvm.BillingDayEM;
import com.aimir.model.system.Contract;
import com.aimir.util.Condition;
import com.aimir.util.StringUtil;

/**
 * BillingDayEMDaoImpl.java Description 
 *
 * 
 * Date          Version     Author   Description
 * 2011. 4. 26.   v1.0       김상연        해당 계약 최종 일자 조회
 * 2011. 4. 28.   v1.1       김상연        BillingDayEM 조회 조건 (BillingDayEM, 시작일, 종료일) 
 * 2011. 4. 28.   v1.2       김상연        관련 범위  내 평균 사용량 조회
 * 2011. 4. 28.   v1.3       김상연        기간  내 사용량 조회
 * 2011. 5. 13.   v1.4       김상연        동일 공급사 평균 사용량 조회
 * 2011. 5. 31.   v1.5       김상연        최근 데이터 조회
 * 2011. 5. 31.   v1.6       김상연        최근 전체 데이터 조회
 *
 */
@Repository(value = "billingdayemDao")
public class BillingDayEMDaoImpl extends AbstractJpaDao<BillingDayEM, Integer> implements BillingDayEMDao {

    @SuppressWarnings("unused")
	private static Log logger = LogFactory.getLog(BillingDayEMDaoImpl.class);
    
    public BillingDayEMDaoImpl() {
		super(BillingDayEM.class);
	}

  	public BillingDayEM getBillingDayEM(Map<String, Object> condition) {
		String mdsId = StringUtil.nullToBlank(condition.get("mdsId"));
		String yyyymmdd = StringUtil.nullToBlank(condition.get("yyyymmdd"));
		Integer mdevTypeCode = (Integer) ObjectUtils.defaultIfNull(condition.get("mdevTypeCode"), null);
		
		String sql = "select b from BillingDayEM b where id.mdevId = :mdevId and id.yyyymmdd = :yyyymmdd";
		
		if (mdevTypeCode != null)
		    sql += " and id.mdevType = :mdevType";
		
		Query query = em.createQuery(sql, BillingDayEM.class);
		
		query.setParameter("mdevId", mdsId);
		query.setParameter("yyyymmdd", yyyymmdd);
		
		if (mdevTypeCode != null)
		    query.setParameter("mdevType", mdevTypeCode);
		
		return (BillingDayEM) query.getSingleResult();
	}

    @Override
    public List<Map<String, Object>> getBillingDataDaily(
            Map<String, Object> conditionMap, boolean isCount) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Map<String, Object>> getBillingDataReportDaily(
            Map<String, Object> conditionMap) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Map<String, Object>> getBillingDetailDataDaily(
            Map<String, Object> conditionMap) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getMaxDay(Contract contract) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<BillingDayEM> getBillingDayEMs(BillingDayEM billingDayEM,
            String startDay, String finishDay) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Double getAverageUsage(Contract contract, String startDay,
            String finishDay) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Double getPeriodUsage(Contract contract, String startDay,
            String finishDay) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Double getAverageUsage(BillingDayEM billingDayEM) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Map<String, Object> getLast(Integer id) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Map<String, Object> getFirst(Integer id) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Map<String, Object> getTotal(Integer id, String lastBillDay,
            String periodDay) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getBillingDayEMsAvg(BillingDayEM billingDayEM) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Map<String, Object> getSelDate(Integer id, String selDate) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Double getMaxBill(Contract contract, String yyyymmdd) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<BillingDayEM> getPrepaymentBillingDayList(Integer contractId) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Double getAverageBill(Contract contract, String yyyymmdd) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Map<String, Object>> getChargeHistoryBillingList(
            Map<String, Object> conditionMap) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Map<String, Object>> getDayBillingInfoData() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Map<String, Object>> getNotDeliveryDayBillingInfoData() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Map<String, Object>> getReDeliveryDayBillingInfoData(
            int contractId, String yyyymmdd) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean hasActiveTotalEnergyByBillDate(int contractId,
            String yyyymmdd) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public List<Map<String, Object>> getDayBillingInfoDataBySupplierBillDate(
            int serviceTypeId, int creditType) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Map<String, Object>> getNotDeliveryDayBillingInfoDataBySupplierBillDate(
            int serviceType, int creditType) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void updateBillingSendResultFlag(boolean sendResultFlag,
            int contractId, String yyyymmdd) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public List<Map<String, Object>> getLastAccumulateBill(String mdevId) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Class<BillingDayEM> getPersistentClass() {
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