/**
 * BillingDayGMDaoImpl.java Copyright NuriTelecom Limited 2011
 */

package com.aimir.dao.mvm.impl;

import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractHibernateGenericDao;
import com.aimir.dao.mvm.BillingDayGMDao;
import com.aimir.model.mvm.BillingDayGM;
import com.aimir.model.system.Contract;

/**
 * BillingDayGMDaoImpl.java Description 
 *
 * 
 * Date          Version     Author   Description
 * 2011. 5. 17.   v1.0       김상연         유효 데이터 최신 날짜 조회
 * 2011. 5. 17.   v1.1       김상연         BillingDayGM 조회 - 조건(BillingDayGM, 시작일, 종료일)
 * 2011. 5. 17.   v1.2       김상연         계약 회사 평균 조회
 * 2011. 5. 31.   v1.3       김상연        최근 데이터 조회
 * 2011. 5. 31.   v1.4       김상연        최근 전체 데이터 조회
 *
 */
@Repository(value = "billingdaygmDao")
public class BillingDayGMDaoImpl extends AbstractHibernateGenericDao<BillingDayGM, Integer> implements BillingDayGMDao {

	@SuppressWarnings("unused")
	private static Log logger = LogFactory.getLog(BillingDayGMDaoImpl.class);
    
	@Autowired
	protected BillingDayGMDaoImpl(SessionFactory sessionFactory) {
		super(BillingDayGM.class);
		super.setSessionFactory(sessionFactory);
	}

	/* (non-Javadoc)
	 * @see com.aimir.dao.mvm.BillingDayGMDao#getMaxDay(com.aimir.model.system.Contract)
	 */
	public String getMaxDay(Contract contract) {
		
		Criteria criteria = getSession().createCriteria(BillingDayGM.class);
		
		if (contract != null) {
		
			criteria.add(Restrictions.eq("contract.id", contract.getId()));
		}
		
		criteria.setProjection( Projections.projectionList().add( Projections.max("id.yyyymmdd") ) );
		
		return (criteria.list().get(0) == null ? "00000000" : criteria.list().get(0).toString());
	}

	/* (non-Javadoc)
	 * @see com.aimir.dao.mvm.BillingDayGMDao#getBillingDayGMs(com.aimir.model.mvm.BillingDayGM, java.lang.String, java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	public List<BillingDayGM> getBillingDayGMs(BillingDayGM billingDayGM,
			String startDay, String finishDay) {

		Criteria criteria = getSession().createCriteria(BillingDayGM.class);
		
		if (billingDayGM != null) {
			
			if (billingDayGM.getYyyymmdd() != null) {
				
				if ( 8 == billingDayGM.getYyyymmdd().length() ) {
					
					criteria.add(Restrictions.eq("id.yyyymmdd", billingDayGM.getYyyymmdd()));
				} else if ( 6 == billingDayGM.getYyyymmdd().length() ) {
					
					criteria.add(Restrictions.like("id.yyyymmdd", billingDayGM.getYyyymmdd() + "%"));
				}
			}
			
			if (billingDayGM.getContract() != null) {
				
				if (billingDayGM.getContract().getId() != null) {
					
					criteria.add(Restrictions.eq("contract.id", billingDayGM.getContract().getId()));
				} 
			}
		}

		if (startDay != null) {
			
			//criteria.add(Restrictions.gt("id.yyyymmdd", startDay));
			criteria.add(Restrictions.ge("id.yyyymmdd", startDay));
		}
		
		if (finishDay != null) {
			
			criteria.add(Restrictions.le("id.yyyymmdd", finishDay));
		}

		criteria.addOrder(Order.asc("id.yyyymmdd"));

		return criteria.list();
	}

	/* (non-Javadoc)
	 * @see com.aimir.dao.mvm.BillingDayGMDao#getBillingDayGMsAvg(com.aimir.model.mvm.BillingDayGM)
	 */
	@SuppressWarnings("unchecked")
	public List<Object> getBillingDayGMsAvg(BillingDayGM billingDayGM) {
		StringBuffer sbSql = new StringBuffer();
		sbSql.append(" SELECT yyyymmdd AS yyyymmdd  ")
		.append(", AVG(bill) AS bill ")
		.append(" FROM BILLING_DAY_GM ")
		.append(" WHERE yyyymmdd like '").append(billingDayGM.id.getYyyymmdd()).append("%' ")
		.append(" AND location_id = :locationId ")
		.append(" GROUP BY yyyymmdd ORDER BY yyyymmdd ");

		SQLQuery query = getSession().createSQLQuery(sbSql.toString());
		query.setInteger("locationId", billingDayGM.getLocation().getId());

		return query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
	}

	/* (non-Javadoc)
	 * @see com.aimir.dao.mvm.BillingDayGMDao#getAverageUsage(com.aimir.model.mvm.BillingDayGM)
	 */
	public Double getAverageUsage(BillingDayGM billingDayGM) {
		
		//String sqlStr = "\n select avg(billingDayGM.usage) "
		String sqlStr = "\n select avg(billingDayGM.bill) "
            + "\n from BillingDayGM billingDayGM "
            + "\n join billingDayGM.contract contract "
            + "\n where contract.supplier.id = :supplier "
            + "\n and billingDayGM.id.yyyymmdd = :someDay";

		Query query = getSession().createQuery(sqlStr);

		query.setInteger("supplier", billingDayGM.getContract().getSupplier().getId());
		query.setString("someDay", billingDayGM.getYyyymmdd());
		
		Double averageUsage = Double.parseDouble((query.list().get(0) == null ? 0 : query.list().get(0)).toString());
		
		return averageUsage;
	}

	/* (non-Javadoc)
	 * @see com.aimir.dao.mvm.BillingDayGMDao#getLast(java.lang.Integer)
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Object> getLast(Integer id) {
		
		String sqlStr = " "
			+ "\n select  id.yyyymmdd      as  lastDay "
			+ "\n       , usage            as  usage "
			+ "\n       , bill             as  bill "
			+ "\n       , usageReadToDate  as  usageReadToDate "
			+ "\n   from  BillingDayGM "
			+ "\n  where  contract.id = :contractId "
			+ "\n    and  id.yyyymmdd = ( select  max(id.yyyymmdd) "
			+ "\n                           from  BillingDayGM "
			+ "\n                          where  contract.id = :contractId) ";

		Query query = getSession().createQuery(sqlStr);

		query.setInteger("contractId", id);
		
		List<Map<String, Object>> returnList = query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
		
		return 1 == returnList.size() ? returnList.get(0) : null;
	}

	@SuppressWarnings("unchecked")
	public Map<String, Object> getFirst(Integer id) {
		
		String sqlStr = " "
			+ "\n select  id.yyyymmdd  as  firstDay "
			+ "\n       , usage        as  usage "
			+ "\n       , bill         as  bill "
			+ "\n   from  BillingDayGM "
			+ "\n  where  contract.id = :contractId "
			+ "\n    and  id.yyyymmdd = ( select  min(id.yyyymmdd) "
			+ "\n                           from  BillingDayGM "
			+ "\n                          where  contract.id = :contractId) ";

		Query query = getSession().createQuery(sqlStr);

		query.setInteger("contractId", id);
		
		List<Map<String, Object>> returnList = query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
		
		return 1 == returnList.size() ? returnList.get(0) : null;
	}
	/* (non-Javadoc)
	 * @see com.aimir.dao.mvm.BillingDayGMDao#getSelDate(java.lang.Integer, java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Object> getSelDate(Integer id, String selDate) {
		
		String sqlStr = " "
			+ "\n select  id.yyyymmdd  as  lastDay "
			+ "\n       , usage        as  usage "
			+ "\n       , bill         as  bill "
			+ "\n   from  BillingDayGM "
			+ "\n  where  contract.id = :contractId "
			+ "\n    and  id.yyyymmdd = :yyyymmdd ";

		Query query = getSession().createQuery(sqlStr);

		query.setInteger("contractId", id);
		query.setString("yyyymmdd", selDate);
		
		List<Map<String, Object>> returnList = query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
		
		return 1 == returnList.size() ? returnList.get(0) : null;
	}

	/* (non-Javadoc)
	 * @see com.aimir.dao.mvm.BillingDayGMDao#getTotal(java.lang.Integer, java.lang.String, java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Object> getTotal(Integer id, String lastBillDay, String periodDay) {
		
		String sqlStr = " "
			+ "\n select  sum(usage) as totalUsage "
			+ "\n       , sum(bill)  as totalBill "
			+ "\n   from  BillingDayGM "
			+ "\n  where  contract.id = :contractId "
			+ "\n    and  id.yyyymmdd >= :fromDay "
			+ "\n    and  id.yyyymmdd <= :toDay ";

		Query query = getSession().createQuery(sqlStr);

		query.setInteger("contractId", id);
		query.setString("fromDay", lastBillDay);
		query.setString("toDay", periodDay);
		
		List<Map<String, Object>> returnList = query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
		
		return 1 == returnList.size() ? returnList.get(0) : null;
	}
	
	/* (non-Javadoc)
	 * @see com.aimir.dao.mvm.BillingDayGMDao#getMaxBill(com.aimir.model.system.Contract, java.lang.String)
	 */
	public Double getMaxBill(Contract contract, String yyyymmdd) {
		
		Criteria criteria = getSession().createCriteria(BillingDayGM.class);
		
		if (contract != null) {
		
			criteria.add(Restrictions.eq("contract.id", contract.getId()));
		}
		
		if (yyyymmdd != null) {
			criteria.add(Restrictions.ilike("id.yyyymmdd", yyyymmdd + "%"));
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
    public List<BillingDayGM> getPrepaymentBillingDayList(Integer contractId) {
        StringBuilder sb = new StringBuilder();

        sb.append("\nFROM BillingDayGM b ");
        sb.append("\nWHERE b.contract.id = :contractId ");
        sb.append("\nAND   b.id.yyyymmdd > COALESCE((SELECT MAX(c.id.yyyymmdd) ");
        sb.append("                                  FROM BillingDayGM c ");
        sb.append("                                  WHERE c.contract.id = b.contract.id ");
        sb.append("                                  AND   c.bill IS NOT NULL), '') ");
        sb.append("\nORDER BY b.id.yyyymmdd ");

        Query query = getSession().createQuery(sb.toString());
        
        query.setInteger("contractId", contractId);

        return query.list();
    }

    public Double getAverageBill(Contract contract, String yyyymmdd) {
        
        Criteria criteria = getSession().createCriteria(BillingDayGM.class);
        
//      if (contract != null) {
//      
//          criteria.add(Restrictions.eq("contract.id", contract.getId()));
//      }
        
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
        Integer contractId = (Integer)conditionMap.get("contractId");
        String startDate = (String)conditionMap.get("startDate");
        String endDate = (String)conditionMap.get("endDate");
        StringBuilder sb = new StringBuilder();

        sb.append("\nSELECT b.id.yyyymmdd AS yyyymmdd, ");
        sb.append("\n       b.usage AS usage, ");
        sb.append("\n       b.bill AS bill ");
        sb.append("\nFROM BillingDayGM b ");
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
}