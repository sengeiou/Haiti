/**
 * BillingDayWMDaoImpl.java Copyright NuriTelecom Limited 2011
 */

package com.aimir.dao.mvm.impl;

import java.text.ParseException;
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
import org.springframework.transaction.annotation.Transactional;

import com.aimir.dao.AbstractHibernateGenericDao;
import com.aimir.dao.mvm.BillingDayWMDao;
import com.aimir.model.mvm.BillingDayWM;
import com.aimir.model.system.Contract;
import com.aimir.util.TimeUtil;

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
public class BillingDayWMDaoImpl extends AbstractHibernateGenericDao<BillingDayWM, Integer> implements BillingDayWMDao {

	@SuppressWarnings("unused")
	private static Log logger = LogFactory.getLog(BillingDayWMDaoImpl.class);
    
	@Autowired
	protected BillingDayWMDaoImpl(SessionFactory sessionFactory) {
		super(BillingDayWM.class);
		super.setSessionFactory(sessionFactory);
	}

	/* (non-Javadoc)
	 * @see com.aimir.dao.mvm.BillingDayWMDao#getMaxDay(com.aimir.model.system.Contract)
	 */
	public String getMaxDay(Contract contract) {
		
		Criteria criteria = getSession().createCriteria(BillingDayWM.class);
		
		if (contract != null) {
		
			criteria.add(Restrictions.eq("contract.id", contract.getId()));
		}
		
		criteria.setProjection( Projections.projectionList().add( Projections.max("id.yyyymmdd") ) );
		
		return (criteria.list().get(0) == null ? "00000000" : criteria.list().get(0).toString());
	}

	@SuppressWarnings("unchecked")
	public List<BillingDayWM> getBillingDayWMs(BillingDayWM billingDayWM,
			String startDay, String finishDay) {

		Criteria criteria = getSession().createCriteria(BillingDayWM.class);
		
		if (billingDayWM != null) {
			
			if (billingDayWM.getYyyymmdd() != null) {
				
				if ( 8 == billingDayWM.getYyyymmdd().length() ) {
					
					criteria.add(Restrictions.eq("id.yyyymmdd", billingDayWM.getYyyymmdd()));
				} else if ( 6 == billingDayWM.getYyyymmdd().length() ) {
					
					criteria.add(Restrictions.like("id.yyyymmdd", billingDayWM.getYyyymmdd() + "%"));
				}
			}
			
			if (billingDayWM.getContract() != null) {
				
				if (billingDayWM.getContract().getId() != null) {
					
					criteria.add(Restrictions.eq("contract.id", billingDayWM.getContract().getId()));
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
	 * @see com.aimir.dao.mvm.BillingDayWMDao#getBillingDayWMsAvg(com.aimir.model.mvm.BillingDayWM)
	 */
	@SuppressWarnings("unchecked")
	public List<Object> getBillingDayWMsAvg(BillingDayWM billingDayWM) {
		StringBuffer sbSql = new StringBuffer();
		sbSql.append(" SELECT yyyymmdd AS yyyymmdd  ")
		.append(", AVG(bill) AS bill ")
		.append(" FROM BILLING_DAY_WM ")
		.append(" WHERE yyyymmdd like '").append(billingDayWM.id.getYyyymmdd()).append("%' ")
		.append(" AND location_id = :locationId ")
		.append(" GROUP BY yyyymmdd ORDER BY yyyymmdd ");

		SQLQuery query = getSession().createSQLQuery(sbSql.toString());
		query.setInteger("locationId", billingDayWM.getLocation().getId());

		return query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
	}

	/* (non-Javadoc)
	 * @see com.aimir.dao.mvm.BillingDayWMDao#getAverageUsage(com.aimir.model.mvm.BillingDayWM)
	 */
	public Double getAverageUsage(BillingDayWM billingDayWM) {
		
		//String sqlStr = "\n select avg(billingDayWM.usage) "
		String sqlStr = "\n select avg(billingDayWM.bill) "			
            + "\n from BillingDayWM billingDayWM "
            + "\n join billingDayWM.contract contract "
            + "\n where contract.supplier.id = :supplier "
            + "\n and billingDayWM.id.yyyymmdd = :someDay";

		Query query = getSession().createQuery(sqlStr);

		query.setInteger("supplier", billingDayWM.getContract().getSupplier().getId());
		query.setString("someDay", billingDayWM.getYyyymmdd());
		
		Double averageUsage = Double.parseDouble((query.list().get(0) == null ? 0 : query.list().get(0)).toString());
		
		return averageUsage;
	}

	/* (non-Javadoc)
	 * @see com.aimir.dao.mvm.BillingDayWMDao#getLast(java.lang.Integer)
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Object> getLast(Integer id) {
		
		String sqlStr = " "
			+ "\n select  id.yyyymmdd     as  lastDay "
			+ "\n       , usage           as  usage "
			+ "\n       , bill            as  bill "
			+ "\n       , usageReadToDate as  usageReadToDate "
			+ "\n   from  BillingDayWM "
			+ "\n  where  contract.id = :contractId "
			+ "\n    and  id.yyyymmdd = ( select  max(id.yyyymmdd) "
			+ "\n                           from  BillingDayWM "
			+ "\n                          where  contract.id = :contractId) ";

		Query query = getSession().createQuery(sqlStr);

		query.setInteger("contractId", id);
		
		List<Map<String, Object>> returnList = query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
		
		return 1 == returnList.size() ? returnList.get(0) : null;
	}
	
	public Map<String, Object> getFirst(Integer id) {
		
		String sqlStr = " "
			+ "\n select  id.yyyymmdd  as  firstDay "
			+ "\n       , usage        as  usage "
			+ "\n       , bill         as  bill "
			+ "\n   from  BillingDayWM "
			+ "\n  where  contract.id = :contractId "
			+ "\n    and  id.yyyymmdd = ( select  min(id.yyyymmdd) "
			+ "\n                           from  BillingDayWM "
			+ "\n                          where  contract.id = :contractId) ";

		Query query = getSession().createQuery(sqlStr);

		query.setInteger("contractId", id);
		
		List<Map<String, Object>> returnList = query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
		
		return 1 == returnList.size() ? returnList.get(0) : null;
	}
	
	/* (non-Javadoc)
	 * @see com.aimir.dao.mvm.BillingDayWMDao#getSelDate(java.lang.Integer, java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Object> getSelDate(Integer id, String selDate) {
		
		String sqlStr = " "
			+ "\n select  id.yyyymmdd  as  lastDay "
			+ "\n       , usage        as  usage "
			+ "\n       , bill         as  bill "
			+ "\n   from  BillingDayWM "
			+ "\n  where  contract.id = :contractId "
			+ "\n    and  id.yyyymmdd = :yyyymmdd ";

		Query query = getSession().createQuery(sqlStr);

		query.setInteger("contractId", id);
		query.setString("yyyymmdd", selDate);

		List<Map<String, Object>> returnList = query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
		
		return 1 == returnList.size() ? returnList.get(0) : null;
	}

	/* (non-Javadoc)
	 * @see com.aimir.dao.mvm.BillingDayWMDao#getTotal(java.lang.Integer, java.lang.String, java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Object> getTotal(Integer id, String lastBillDay, String periodDay) {
	
		String sqlStr = " "
			+ "\n select  sum(usage) as totalUsage "
			+ "\n       , sum(bill)  as totalBill "
			+ "\n   from  BillingDayWM "
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
	 * @see com.aimir.dao.mvm.BillingDayWMDao#getMaxBill(com.aimir.model.system.Contract, java.lang.String)
	 */
	public Double getMaxBill(Contract contract, String yyyymmdd) {
		
		Criteria criteria = getSession().createCriteria(BillingDayWM.class);
		
		if (contract != null) {
		
			criteria.add(Restrictions.eq("contract.id", contract.getId()));
		}
		
		if (yyyymmdd != null) {
			criteria.add(Restrictions.ilike("id.yyyymmdd", yyyymmdd + "%"));
		}

		criteria.setProjection( Projections.projectionList().add( Projections.max("bill") ) );
		
		return (Double)(criteria.list().get(0) == null ? 0.0 : criteria.list().get(0));
	}

    public Double getAverageBill(Contract contract, String yyyymmdd) {
        
        Criteria criteria = getSession().createCriteria(BillingDayWM.class);
        
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
    @Transactional(readOnly=true)
    public List<Map<String, Object>> getChargeHistoryBillingList(Map<String, Object> conditionMap) {
        Integer contractId = (Integer)conditionMap.get("contractId");
        String startDate = (String)conditionMap.get("startDate");
        String endDate = (String)conditionMap.get("endDate");
        StringBuilder sb = new StringBuilder();

        sb.append("\nSELECT b.id.yyyymmdd AS yyyymmdd, ");
        sb.append("\n       b.usage AS usage, ");
        sb.append("\n       b.bill AS bill ");
        sb.append("\nFROM BillingDayWM b ");
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

	@Transactional(readOnly=true)
	public List<Map<String, Object>> getDayBillingInfoDataBySupplierBillDate(int serviceTypeId, int creditType) {

		String today = TimeUtil.getCurrentTimeMilli();
		int currentDay = Integer.parseInt(today.substring(6,8));
		SQLQuery query = null;
		try{

			StringBuilder sb = new StringBuilder();
			sb.append("\n SELECT " );
			sb.append("\n   preDE.TOTAL as PREVIOUSMETERREADING, currtDE.TOTAL as CURRENTMETERREADING, ");
			sb.append("\n   currtDE.ID AS ID, currtDE.MDEVID AS MDEVID, currtDE.NUMBER AS NUMBER,currtDE.YYYYMMDD AS YYYYMMDD ");
			sb.append("\n FROM ( SELECT b1.consum_usage as TOTAL, b1.contract_id AS ID, b1.mdev_id AS MDEVID, c.contract_number AS NUMBER ");
			sb.append("\n		 FROM supplytype s1, billing_day_wm b1 join contract c on  b1.contract_id = c.id ");
			sb.append("\n        WHERE b1.yyyymmdd = :lastMonth ");
			sb.append("\n        AND b1.supplier_id = c.supplier_id ");
			sb.append("\n        AND s1.supplier_id = c.supplier_id ");
			sb.append("\n        AND s1.type_id = :typeId ");
			sb.append("\n        AND c.creditType_id = :creditType ");
			sb.append("\n        AND c.servicetype_id = :typeId ");
			if(currentDay == 1){
				sb.append("\n    AND (s1.billDate is null OR s1.billDate = :billDate OR s1.billDate = '') ");
			} else {
				 sb.append("\n   AND s1.billDate = :billDate " );
			}
			sb.append("\n        GROUP BY b1.consum_usage, b1.contract_id, b1.mdev_id, c.contract_number ) preDE ");
			sb.append("\n        RIGHT OUTER JOIN  ");			
			sb.append("\n      ( SELECT b2.consum_usage as TOTAL, b2.contract_id AS ID, b2.mdev_id AS MDEVID, MAX(b2.yyyymmdd) AS YYYYMMDD, c.contract_number AS NUMBER  ");
			sb.append("\n		 FROM supplytype s2, billing_day_wm b2 join contract c on  b2.contract_id = c.id ");
			sb.append("\n        WHERE  b2.yyyymmdd = :today  ");
			sb.append("\n        AND b2.supplier_id = c.supplier_id ");
			sb.append("\n        AND s2.supplier_id = c.supplier_id ");
			sb.append("\n        AND s2.type_id = :typeId ");
			sb.append("\n        AND c.creditType_id = :creditType ");
			sb.append("\n        AND c.servicetype_id = :typeId ");
			sb.append("\n        AND c.delay_day is null ");	
			sb.append("\n        AND b2.send_result is null ");		
			if(currentDay == 1){
				sb.append("\n    AND (s2.billDate is null OR s2.billDate = :billDate OR s2.billDate = '') ");				
			} else {
				 sb.append("\n   AND s2.billDate = :billDate " );				 
			}
			sb.append("\n        GROUP BY b2.consum_usage, b2.contract_id, b2.mdev_id, c.contract_number ) currtDE ");
			sb.append("\n        ON currtDE.MDEVID = preDE.MDEVID ");			
			sb.append("\n WHERE currtDE.YYYYMMDD = :today ");

            query = getSession().createSQLQuery(sb.toString());
			query.setString("lastMonth", TimeUtil.getPreMonth(today).substring(0, 8));
			query.setString("today", today.substring(0, 8));
			query.setString("billDate", String.valueOf(currentDay));
			query.setInteger("typeId", serviceTypeId);
			query.setInteger("creditType", creditType);

		}catch(ParseException e){
			e.printStackTrace();
		}
	//	return query.list();
		return query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
	}

	public void updateBillingSendResultFlag(boolean sendResultFlag, int contractId, String yyyymmdd) {

		StringBuffer sb = new StringBuffer();
		sb.append("UPDATE BillingDayWM ");
		sb.append("SET sendResult = ? ");
		sb.append("WHERE contract.id = ? ");
		sb.append("AND id.yyyymmdd = ? ");

		//HQL문을 이용한 CUD를 할 경우에는 getSession().bulkUpdate() 메소드를 사용한다.	
		// this.getSession().bulkUpdate(sb.toString(), new Object[] {  sendResultFlag, contractId, yyyymmdd} );
	}

	@Transactional(readOnly=true)
	public boolean hasActiveTotalEnergyByBillDate(int contractId, String yyyymmdd) {

		StringBuilder sb = new StringBuilder();
		sb.append("\n SELECT " );
		sb.append("\n   contract_id ");
		sb.append("\n FROM billing_day_wm ");
		sb.append("\n WHERE contract_id =:contractId ");
		sb.append("\n AND yyyymmdd =:yyyymmdd ");
		sb.append("\n AND send_result is null ");

		SQLQuery query = getSession().createSQLQuery(sb.toString());
		query.setInteger("contractId", contractId);
		query.setString("yyyymmdd", yyyymmdd);

		return query.list().size() !=0 ? true : false;
	}

	@Transactional(readOnly=true)
	public List<Map<String, Object>> getReDeliveryDayBillingInfoData(int contractId, String yyyymmdd) {
		SQLQuery query = null;
		try{

			String lastMonth = TimeUtil.getPreMonth(yyyymmdd).substring(0, 8);

			StringBuilder sb = new StringBuilder();
			sb.append("\n SELECT " );
			sb.append("\n   preDE.TOTAL as PREVIOUSMETERREADING, currtDE.TOTAL as CURRENTMETERREADING, ");
			sb.append("\n   currtDE.ID AS ID, currtDE.MDEVID AS MDEVID, currtDE.YYYYMMDD AS YYYYMMDD, currtDE.NUMBER as NUMBER ");
			sb.append("\n FROM ( SELECT b1.consum_usage as TOTAL, b1.contract_id AS ID, b1.mdev_id AS MDEVID ");
			sb.append("\n		 FROM billing_day_wm b1 join contract c on  b1.contract_id = c.id ");
			sb.append("\n        WHERE b1.yyyymmdd = :lastMonth ");
			sb.append("\n        AND b1.supplier_id = c.supplier_id ");
			sb.append("\n        AND b1.contract_id = :contractId ");
			sb.append("\n        GROUP BY b1.consum_usage, b1.contract_id, b1.mdev_id ) preDE ");
			sb.append("\n        RIGHT OUTER JOIN  ");			
			sb.append("\n      ( SELECT b2.consum_usage as TOTAL, b2.contract_id AS ID, b2.mdev_id AS MDEVID, MAX(b2.yyyymmdd) AS YYYYMMDD, c.contract_number as NUMBER ");
			sb.append("\n		 FROM billing_day_wm b2 join contract c on  b2.contract_id = c.id ");
			sb.append("\n        WHERE  b2.yyyymmdd = :today  ");
			sb.append("\n        AND b2.supplier_id = c.supplier_id ");
			sb.append("\n        AND b2.contract_id = :contractId ");
			sb.append("\n        GROUP BY b2.consum_usage, b2.contract_id, b2.mdev_id, c.contract_number ) currtDE ");
			sb.append("\n        ON currtDE.MDEVID = preDE.MDEVID ");			
//			sb.append("\n WHERE preDE.ID = currtDE.ID ");		

            query = getSession().createSQLQuery(sb.toString());
			query.setInteger("contractId", contractId);
			query.setString("lastMonth", lastMonth);
			query.setString("today", yyyymmdd);

		}catch(ParseException e){
			e.printStackTrace();
		}
		return query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
	}

	@Transactional(readOnly=true)
	public List<Map<String, Object>> getNotDeliveryDayBillingInfoDataBySupplierBillDate(int serviceType, int creditType) {
		String today = TimeUtil.getCurrentTimeMilli();
		String currentDay = today.substring(6,8);
		SQLQuery query = null;

		StringBuilder sb = new StringBuilder();
		sb.append("\n SELECT " );
		sb.append("\n   result.id as ID ");
		sb.append("\n FROM ( SELECT c.id, b.yyyymmdd ");
		sb.append("            FROM ( SELECT c.id FROM contract c, supplytype s ");
		sb.append("                   WHERE s.billDate =:billDate ");
		sb.append("                   AND s.supplier_id = c.supplier_id ");
		sb.append("                   AND s.type_id = :serviceType ");
		sb.append("                   AND c.servicetype_id =:serviceType");
		sb.append("                   AND c.creditType_id = :creditType ) c  ");
		sb.append("                left join billing_day_wm b on c.id = b.contract_id AND b.yyyymmdd =:today ) result ");
		sb.append("\n WHERE result.yyyymmdd is null ");

        query = getSession().createSQLQuery(sb.toString());
		query.setString("today", today.substring(0, 8));
		query.setInteger("billDate", Integer.parseInt(currentDay));
		query.setInteger("serviceType", serviceType);
		query.setInteger("creditType", creditType);

		return query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
	}

}