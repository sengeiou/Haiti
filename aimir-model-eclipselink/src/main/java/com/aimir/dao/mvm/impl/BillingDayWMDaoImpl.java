/**
 * BillingDayWMDaoImpl.java Copyright NuriTelecom Limited 2011
 */

package com.aimir.dao.mvm.impl;

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.Query;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractJpaDao;
import com.aimir.dao.mvm.BillingDayWMDao;
import com.aimir.model.mvm.BillingDayWM;
import com.aimir.model.system.Contract;
import com.aimir.util.Condition;

/**
 * BillingDayWMDaoImpl.java Description 
 *
 * 
 * Date          Version     Author   Description
 * 2011. 5. 17.   v1.0       김상연         유효 데이터 최신 날짜 조회
 * 2011. 5. 17.   v1.1       김상연        BillingDayWM 조회 - 조건(BillingDayWM, 시작일, 종료일)
 * 2011. 5. 17.   v1.2       김상연        계약 회사 평균 조회
 * 2011. 5. 31.   v1.3       김상연        최근 데이터 조회
 * 2011. 5. 31.   v1.4       김상연        최근 전체 데이터 조회
 *
 */
@Repository(value = "billingdaywmDao")
public class BillingDayWMDaoImpl extends AbstractJpaDao<BillingDayWM, Integer> implements BillingDayWMDao {

	@SuppressWarnings("unused")
	private static Log logger = LogFactory.getLog(BillingDayWMDaoImpl.class);
    
	public BillingDayWMDaoImpl() {
		super(BillingDayWM.class);
	}

	/* (non-Javadoc)
	 * @see com.aimir.dao.mvm.BillingDayWMDao#getMaxDay(com.aimir.model.system.Contract)
	 */
	public String getMaxDay(Contract contract) {
		String sql = "select max(id.yyyymmdd) from BillingDayWM where 1=1";
		
		if (contract != null) {
		    sql += " and contract.id = :contractId";
		}
		
		Query query = em.createQuery(sql, String.class);
		
		if (contract != null) {
		    query.setParameter("contractId", contract.getId());
		}
		
		return (String)query.getSingleResult();
	}

    @Override
    public List<BillingDayWM> getBillingDayWMs(BillingDayWM billingDayWM,
            String startDay, String finishDay) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Double getAverageUsage(BillingDayWM billingDayWM) {
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
    public List<Object> getBillingDayWMsAvg(BillingDayWM billingDayWM) {
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
    public List<Map<String, Object>> getDayBillingInfoDataBySupplierBillDate(
            int serviceTypeId, int creditType) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void updateBillingSendResultFlag(boolean sendResultFlag,
            int contractId, String yyyymmdd) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public boolean hasActiveTotalEnergyByBillDate(int contractId,
            String yyyymmdd) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public List<Map<String, Object>> getReDeliveryDayBillingInfoData(
            int contractId, String yyyymmdd) {
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
    public Class<BillingDayWM> getPersistentClass() {
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