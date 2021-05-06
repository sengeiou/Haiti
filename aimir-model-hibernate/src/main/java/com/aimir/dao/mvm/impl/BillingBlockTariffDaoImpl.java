/**
 * BillingDayEMDaoImpl.java Copyright NuriTelecom Limited 2011
 */

package com.aimir.dao.mvm.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.NoResultException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.aimir.constants.CommonConstants.DeviceType;
import com.aimir.dao.AbstractHibernateGenericDao;
import com.aimir.dao.mvm.BillingBlockTariffDao;
import com.aimir.model.mvm.BillingBlockTariff;
import com.aimir.model.mvm.BillingDayEM;
import com.aimir.model.system.Contract;
import com.aimir.model.system.MonthlyBillingLog;

/**
 * BillingBlockTariffDaoImpl.java Description 
 *
 */
@Repository(value = "billingblocktariffDao")
public class BillingBlockTariffDaoImpl extends AbstractHibernateGenericDao<BillingBlockTariff, Integer> implements BillingBlockTariffDao {

    @SuppressWarnings("unused")
	private static Log logger = LogFactory.getLog(BillingBlockTariffDaoImpl.class);
    
	@Autowired
	protected BillingBlockTariffDaoImpl(SessionFactory sessionFactory) {
		super(BillingBlockTariff.class);
		super.setSessionFactory(sessionFactory);
	}

	/* (non-Javadoc)
	 * @see com.aimir.dao.mvm.BillingDayEMDao#getMaxDay(com.aimir.model.system.Contract)
	 */
	public String getMaxDay(Contract contract) {
		
		Criteria criteria = getSession().createCriteria(BillingDayEM.class);
		
		if (contract != null) {
		
			criteria.add(Restrictions.eq("contract.id", contract.getId()));
		}
		
		criteria.setProjection( Projections.projectionList().add( Projections.max("id.yyyymmdd") ) );
		
		return (criteria.list().get(0) == null ? "00000000" : criteria.list().get(0).toString());
	}

	/* (non-Javadoc)
	 * @see com.aimir.dao.mvm.BillingDayEMDao#getMaxBill(com.aimir.model.system.Contract, java.lang.String)
	 */
	@Override
	public Double getMaxBill(Contract contract, String yyyymmdd) {
		
		Criteria criteria = getSession().createCriteria(BillingBlockTariff.class);
		
		if (contract != null) {
		
			criteria.add(Restrictions.eq("contract.id", contract.getId()));
		}
		
		if (yyyymmdd != null) {
			criteria.add(Restrictions.ilike("id.yyyymmdd", yyyymmdd  + "%"));
		}

		criteria.setProjection( Projections.projectionList().add( Projections.max("bill") ) );
		
		return (Double)(criteria.list().get(0) == null ? 0.0 : criteria.list().get(0));
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

        sb.append("\nFROM BillingBlockTariff b ");
        sb.append("\nWHERE b.contract.id = :contractId ");
        sb.append("\nAND   b.id.yyyymmdd > COALESCE((SELECT MAX(c.id.yyyymmdd) ");
        sb.append("                                  FROM BillingBlockTariff c ");
        sb.append("                                  WHERE c.contract.id = b.contract.id ");
        sb.append("                                  AND   c.bill IS NOT NULL), '') ");
        sb.append("\nORDER BY b.id.yyyymmdd ");

        Query query = getSession().createQuery(sb.toString());
        
        query.setInteger("contractId", contractId);

        return query.list();
    }

    @Override
    public Double getAverageBill(Contract contract, String yyyymmdd) {
        
        Criteria criteria = getSession().createCriteria(BillingBlockTariff.class);

        if (contract.getLocation() != null) {
            criteria.add(Restrictions.eq("location.id", contract.getLocation().getId()));
        }
        
        if (yyyymmdd != null) {
            criteria.add(Restrictions.ilike("id.yyyymmdd", yyyymmdd  + "%"));
        }

        criteria.setProjection( Projections.projectionList().add( Projections.avg("bill") ) );
        
        return (Double)(criteria.list().get(0) == null ? 0.0 : criteria.list().get(0));
    }

    /**
     * method name : getChargeHistoryBillingList
     * method Desc : 고객 선불관리 화면의 충전 이력 사용전력량을 조회한다.(계산용)
     *
     * @param conditionMap
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> getChargeHistoryBillingList(Map<String, Object> conditionMap) {
        Integer contractId = Integer.parseInt( conditionMap.get("contractId").toString() );
        String startDate = (String)conditionMap.get("startDate");
        String endDate = (String)conditionMap.get("endDate");
        StringBuilder sb = new StringBuilder();

        sb.append("\nSELECT b.id.yyyymmdd AS yyyymmdd, ");
        sb.append("\n       b.accumulateUsage AS usage, ");
        sb.append("\n       b.bill AS bill ");
        sb.append("\nFROM BillingBlockTariff b ");
        sb.append("\nWHERE b.contract.id = :contractId ");
        sb.append("\nAND   b.id.yyyymmdd >= :startDate ");
        sb.append("\nAND   b.id.yyyymmdd < :endDate ");
        sb.append("\nORDER BY b.id.yyyymmdd ");

        Query query = getSession().createQuery(sb.toString());
        query.setInteger("contractId", contractId);
        query.setString("startDate", startDate);
        query.setString("endDate", endDate);

        return query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
    }
    
    /**
     * method name : getBalanceHistoryList
     * method Desc : BalanceHistory 내역을 조회한다.
     *
     * @param conditionMap
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> getBalanceHistoryList(Map<String, Object> conditionMap, boolean isCount) {
        List<Map<String, Object>> result = null;
        Integer contractId = Integer.parseInt( conditionMap.get("contractId").toString() );
        Integer supplierId = Integer.parseInt( conditionMap.get("supplierId").toString() );
        String startDate = (String)conditionMap.get("startDate");
        String endDate = (String)conditionMap.get("endDate");
        Integer page = (Integer)conditionMap.get("page");
        Integer limit = (Integer)conditionMap.get("limit");
        
        StringBuilder sb = new StringBuilder();
        
        if(isCount) {
            sb.append("\nSELECT COUNT(b.contractId) ");
        } else {
            sb.append("\nSELECT b.id.yyyymmdd AS yyyymmdd, ");
            sb.append("\n       b.id.hhmmss AS hhmmss, ");
            sb.append("\n       b.accumulateUsage AS accUsage, ");
            sb.append("\n       b.accumulateBill AS accBill, ");
            sb.append("\n       b.activeEnergyImport AS activeImport, ");
            sb.append("\n       b.activeEnergyExport AS activeExport, ");
            sb.append("\n       b.writeDate AS writeDate, ");
            sb.append("\n       b.balance AS balance, ");
            sb.append("\n       b.usage AS usage, ");
            sb.append("\n       b.bill AS bill ");
        }

        sb.append("\nFROM BillingBlockTariff b ");
        sb.append("\nWHERE b.contract.id = :contractId ");
        sb.append("\nAND   b.writeDate >= :startDate ");
        sb.append("\nAND   b.writeDate < :endDate ");
        sb.append("\nAND   b.supplierId = :supplierId ");
        
        if(!isCount) {
            sb.append("\nORDER BY b.writeDate desc, b.id.yyyymmdd desc, b.id.hhmmss desc ");
        }

        Query query = getSession().createQuery(sb.toString());
        query.setInteger("contractId", contractId);
        query.setInteger("supplierId", supplierId);
        query.setString("startDate", startDate);
        query.setString("endDate", endDate);
        
        if (isCount) {
            Map<String,Object> map = new HashMap<String, Object>();
            map.put("total", query.uniqueResult());
            result = new ArrayList<Map<String, Object>>();
            result.add(map);
        } else if(page != null && limit != null) {
            query.setFirstResult((page - 1) * limit);
            query.setMaxResults(limit);
            result = query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
        } else {
            result = query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
        }

        return result;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Map<String, Object>> getLastAccumulateBill(String mdevId) {
        Query query = null;
        try{
            StringBuilder sb = new StringBuilder();
            sb.append("\n SELECT  em.id.yyyymmdd AS YYYYMMDD, em.id.hhmmss AS HHMMSS ");
            sb.append("\n         ,em.accumulateBill AS ACCUMULATEBILL, em.accumulateUsage AS ACCUMULATEUSAGE ");
            sb.append("\n         ,em.activeEnergy AS ACTIVEENERGY, em.reactiveEnergy AS REACTIVEENERGY ");
            sb.append("\n         ,em.activeEnergyImport AS ACTIVEENERGYIMPORT, em.activeEnergyExport AS ACTIVEENERGYEXPORT ");
            sb.append("\n         ,em.reactiveEnergyImport AS REACTIVEENERGYIMPORT, em.reactiveEnergyExport AS REACTIVEENERGYEXPORT ");
            sb.append("\n         ,em.contract.contractNumber AS CONTRACTNUMBER ");
            sb.append("\n FROM BillingBlockTariff em  ");
            sb.append("\n WHERE concat(em.id.yyyymmdd, em.id.hhmmss) = (select  max(concat(e.id.yyyymmdd, e.id.hhmmss)) ");
            sb.append("\n       from BillingBlockTariff e " );
            sb.append("\n       where e.id.mdevId = :mdevId " );
            sb.append("\n       and e.id.mdevType = :mdevType )");
            // sb.append("\n                            and     e.accumulateBill IS NOT NULL)");
            sb.append("\n and em.id.mdevId = :mdevId ");
            sb.append("\n and em.id.mdevType = :mdevType ");
            // sb.append("\n and        em.accumulateBill IS NOT NULL ");

            query = getSession().createQuery(sb.toString());
            query.setString("mdevId", mdevId);
            query.setParameter("mdevType", DeviceType.Meter);
        }
        catch(Exception e){
            logger.error(e, e);
        }
        return query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
    }
    
    @Override
    public BillingBlockTariff getBillingBlockTariff(Integer contractId, String mdevId, String yyyymmdd, String hhmmss) {
    	if(contractId == null && mdevId == null) {
    		logger.error("contractId and mdevId is null. please check value");
    		return null;
    	}
    		
    	try {
    		StringBuffer sbQuery = new StringBuffer();
    		sbQuery.append(" SELECT * FROM BILLING_BLOCK_TARIFF bbt ");
    		sbQuery.append("\n WHERE 1 = 1");
    		
    		if(contractId != null)
    			sbQuery.append("\n bbt.CONTRACT_ID = :contractId ");
    		
    		if(mdevId != null)
    			sbQuery.append("\n bbt.MDEV_ID = :mdevId ");
    		
    		if(yyyymmdd != null)
    			sbQuery.append("\n bbt.YYYYMMDD = :yyyymmdd ");
    		
    		if(hhmmss != null)
    			sbQuery.append("\n bbt.HHMMSS = :hhmmss ");
    		
    		org.hibernate.query.Query query = getSession().createNativeQuery(sbQuery.toString(), BillingBlockTariff.class);
    		if(contractId != null)
    			query.setParameter("contractId", contractId);
    		
    		if(mdevId != null)
    			query.setParameter("mdevId", mdevId);
    		
    		if(yyyymmdd != null)
    			query.setParameter("yyyymmdd", yyyymmdd);
    		
    		if(hhmmss != null)
    			query.setParameter("hhmmss", hhmmss);
    		
    		return (BillingBlockTariff)query.getSingleResult();
    		
    	} catch(NoResultException e) {
			return null;
		} catch(Exception e) {
    		logger.error(e, e);
    	}
    	
    	return null;
    }
    
    
    @Override
    public List<Map<String, Object>> getRevertBillingList() {
    	try {
    		StringBuffer buffer = new StringBuffer();
    		buffer.append(" truncate table TEMP_BILLGIN ");	
    		org.hibernate.query.Query query = getSession().createNativeQuery(buffer.toString());
    		query.executeUpdate();
    		
    		buffer = new StringBuffer();
			buffer.append(" INSERT INTO TEMP_BILLGIN ");
			buffer.append(" SELECT *  FROM  ");
			buffer.append(" ( ");
			buffer.append("     WITH TPREPAY AS  ");
			buffer.append("     ( ");
			buffer.append("         SELECT  ");
			buffer.append("             A.CONTRACT_ID, A.LASTTOKENDATE ");
			buffer.append("         FROM  ");
			buffer.append("         ( ");
			buffer.append("             SELECT  ");
			buffer.append("                 p.*, ");
			buffer.append("                 row_number() OVER (PARTITION BY p.CONTRACT_ID ORDER BY p.LASTTOKENDATE asc) AS row_idx ");
			buffer.append("             FROM  ");
			buffer.append("                 PREPAYMENTLOG_HSW p ");
			buffer.append("             WHERE ");
			buffer.append("                 1 = 1 ");
			buffer.append("                 AND p.CANCEL_DATE IS NULL  ");
			buffer.append("         )A WHERE  ");
			buffer.append("             row_idx = 1 ");
			buffer.append("     ), ");
			buffer.append("     TDAYBILLING AS  ");
			buffer.append("     ( ");
			buffer.append("         SELECT ");
			buffer.append("             * ");
			buffer.append("         FROM  ");
			buffer.append("         ( ");
			buffer.append("             SELECT  ");
			buffer.append("                 bbt.*, ");
			buffer.append("                 row_number() OVER (PARTITION BY bbt.CONTRACT_ID ORDER BY bbt.WRITEDATE desc) AS row_idx ");
			buffer.append("             FROM  ");
			buffer.append("                 BILLING_BLOCK_TARIFF_HSW bbt, TPREPAY tp ");
			buffer.append("             WHERE ");
			buffer.append("                bbt.CONTRACT_ID = tp.CONTRACT_ID ");
			buffer.append("            and bbt.WRITEDATE < tp.LASTTOKENDATE ");
			buffer.append("         ) where ");
			buffer.append("             row_idx = 1 ");
			buffer.append("     ) ");
			buffer.append("     select * from TDAYBILLING ");
			buffer.append(" ) ");
			query = getSession().createNativeQuery(buffer.toString());
			query.executeUpdate();
			
			buffer = new StringBuffer();
			buffer.append(" select  ");
			buffer.append("    a.* ");
			buffer.append(" from  ");
			buffer.append(" ( ");
			buffer.append("     SELECT 'BBT' AS TABLETYPE, 0 AS ID, bbt.CONTRACT_ID, 0 AS CHARGEDCREDIT,  bbt.MDEV_ID , bbt.BILL, 0 as PRE_BALANCE, bbt.BALANCE, bbt.YYYYMMDD,  bbt.HHMMSS , bbt.WRITEDATE FROM BILLING_BLOCK_TARIFF_HSW bbt ");
			buffer.append("     UNION ALL ");
			buffer.append("     SELECT 'PREPAY' AS TABLETYPE, ph.ID, ph.CONTRACT_ID, ph.CHARGEDCREDIT, '' AS MDEV_ID , 0 AS BILL, ph.PRE_BALANCE, ph.BALANCE, '' AS YYYYMMDD, '' AS HHMMSS, ph.LASTTOKENDATE AS WRITEDATE FROM PREPAYMENTLOG_HSW ph where ph.CANCEL_DATE IS null ");
			buffer.append(" )a, TEMP_BILLGIN tb ");
			buffer.append(" where ");
			buffer.append("     a.contract_id = tb.contract_id ");
			buffer.append("     and a.writedate >= tb.writedate ");
			buffer.append(" order by ");
			buffer.append("     a.contract_id, a.writedate, a.yyyymmdd, a.hhmmss ");
			query = getSession().createNativeQuery(buffer.toString());
			return query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();    		
    		
    	}catch(Exception e) {
    		logger.error(e, e);
    	}
    	
		return null;
		
    }

}