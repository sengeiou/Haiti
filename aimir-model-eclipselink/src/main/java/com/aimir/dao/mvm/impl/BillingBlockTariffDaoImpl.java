/**
 * BillingDayEMDaoImpl.java Copyright NuriTelecom Limited 2011
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
import com.aimir.dao.mvm.BillingBlockTariffDao;
import com.aimir.model.mvm.BillingBlockTariff;
import com.aimir.model.mvm.BillingDayEM;
import com.aimir.model.system.Contract;
import com.aimir.util.Condition;

/**
 * BillingBlockTariffDaoImpl.java Description 
 *
 */
@Repository(value = "billingblocktariffDao")
public class BillingBlockTariffDaoImpl extends AbstractJpaDao<BillingBlockTariff, Integer> 
    implements BillingBlockTariffDao {

    @SuppressWarnings("unused")
	private static Log logger = LogFactory.getLog(BillingBlockTariffDaoImpl.class);
    
    public BillingBlockTariffDaoImpl() {
		super(BillingBlockTariff.class);
	}

	/* (non-Javadoc)
	 * @see com.aimir.dao.mvm.BillingDayEMDao#getMaxDay(com.aimir.model.system.Contract)
	 */
	public String getMaxDay(Contract contract) {
		String sql = "select max(id.yyyymmdd) from Contract";
		
		if (contract != null)
		    sql += " where contract.id = :contractId";
		
		Query query = em.createQuery(sql,  String.class);
		
		if (contract != null)
		    query.setParameter("contractId", contract.getId());
		
		String yyyymmdd =  (String)query.getSingleResult();
		
		if (yyyymmdd == null)
		    return "00000000";
		else
		    return yyyymmdd;
	}

	/* (non-Javadoc)
	 * @see com.aimir.dao.mvm.BillingDayEMDao#getMaxBill(com.aimir.model.system.Contract, java.lang.String)
	 */
	@Override
	public Double getMaxBill(Contract contract, String yyyymmdd) {
		String sql = "select max(bill) from BllingBlockTariff " +
		        "where contract.id = :contractId and id.yyyymmdd like :yyyymmdd";
		Query query = em.createQuery(sql);
		query.setParameter("contractId",  contract.getId());
		query.setParameter("yyyymmdd", yyyymmdd);
		return (Double)query.getSingleResult();
	}

    /**
     * method name : getPrepaymentBillingDayList
     * method Desc : 잔액모니터링 스케줄러에서 조회하는 선불계약별 일별빌링 리스트
     *
     * @param contractId
     * @return
     */
    @SuppressWarnings("unchecked")
    @Override
    public List<BillingDayEM> getPrepaymentBillingDayList(Integer contractId) {
        StringBuilder sb = new StringBuilder();
        sb.append(" select b ");
        sb.append("\nFROM BillingBlockTariff b ");
        sb.append("\nWHERE b.contract.id = :contractId ");
        sb.append("\nAND   b.id.yyyymmdd > COALESCE((SELECT MAX(c.id.yyyymmdd) ");
        sb.append("                                  FROM BillingBlockTariff c ");
        sb.append("                                  WHERE c.contract.id = b.contract.id ");
        sb.append("                                  AND   c.bill IS NOT NULL), '') ");
        sb.append("\nORDER BY b.id.yyyymmdd ");

        Query query = em.createQuery(sb.toString(), BillingBlockTariff.class);
        
        query.setParameter("contractId", contractId);

        return query.getResultList();
    }

    @Override
    public Double getAverageBill(Contract contract, String yyyymmdd) {
        String sql = "select avg(bill) from BillingBlockTariff " +
                     "where location.id = :locationId and id.yyyymmdd = :yyyymmdd";
        Query query = em.createQuery(sql);
        query.setParameter("locationId", contract.getLocation().getId());
        query.setParameter("yyyymmdd",  yyyymmdd);
        return (Double)query.getSingleResult();
    }

    @Override
    public Class<BillingBlockTariff> getPersistentClass() {
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
    public List<Map<String, Object>> getChargeHistoryBillingList(
            Map<String, Object> conditionMap) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Map<String, Object>> getLastAccumulateBill(String mdevId) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Map<String, Object>> getBalanceHistoryList(
            Map<String, Object> conditionMap, boolean isCount) {
        // TODO Auto-generated method stub
        return null;
    }
    
    @Override
    public BillingBlockTariff getBillingBlockTariff(Integer contractId, String mdevId, String yyyymmdd, String hhmmss) { 
    	return null;
    }
    
    @Override
    public List<Map<String, Object>> getRevertBillingList() {
    	// TODO Auto-generated method stub
        return null;
    }
    
    @Override
    public BillingBlockTariff getLastBillingBlockTariff(Integer contractId, String mdevId) {
    	// TODO Auto-generated method stub
        return null;
    }
}