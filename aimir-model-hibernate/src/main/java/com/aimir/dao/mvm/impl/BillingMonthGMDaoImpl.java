/**
 * BillingMonthGMDaoImpl.java Copyright NuriTelecom Limited 2011
 */

package com.aimir.dao.mvm.impl;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractHibernateGenericDao;
import com.aimir.dao.mvm.BillingMonthGMDao;
import com.aimir.model.mvm.BillingMonthGM;
import com.aimir.model.system.Contract;

/**
 * BillingMonthGMDaoImpl.java Description 
 *
 * 
 * Date          Version     Author   Description
 * 2011. 5. 17.   v1.0       김상연         BillingMonthGM 조회 - 조건 (BillingMonthGM)
 * 2011. 5. 17.   v1.1       김상연         계약 회사 평균 조회
 * 2011. 6. 09.   v1.2       김상연         해당 계약 최고 사용 금액 조회
 * 2011. 6. 27.   v1.3       김상연        사용 평균 비용 조회
 *
 */
@Repository(value = "billingmonthgmDao")
public class BillingMonthGMDaoImpl extends AbstractHibernateGenericDao<BillingMonthGM, Integer> implements BillingMonthGMDao {

	@SuppressWarnings("unused")
	private static Log logger = LogFactory.getLog(BillingMonthGMDaoImpl.class);
    
	@Autowired
	protected BillingMonthGMDaoImpl(SessionFactory sessionFactory) {
		super(BillingMonthGM.class);
		super.setSessionFactory(sessionFactory);
	}

	/* (non-Javadoc)
	 * @see com.aimir.dao.mvm.BillingMonthGMDao#getAverageUsage(com.aimir.model.mvm.BillingMonthGM)
	 */
	public Double getAverageUsage(BillingMonthGM billingMonthGM) {
		
		String sqlStr = " "
//			+ "\n select avg(a.sumUsage) "
//		    + "\n from ( select sum(billingMonthGM.consum_usage) sumUsage "
			+ "\n select avg(a.sumBill) "
		    + "\n from ( select sum(billingMonthGM.bill) sumBill "		    
		    + "\n          from billing_month_gm billingMonthGM "
		    + "\n          join contract contract "
		    + "\n            on billingMonthGM.contract_id = contract.id "
		    + "\n         where billingMonthGM.yyyymmdd like :someMonth "
		    + "\n           and contract.supplier_id = :supplier "
		    + "\n         group by billingMonthGM.contract_id ) a ";

        SQLQuery query = getSession().createSQLQuery(sqlStr);

		query.setInteger("supplier", billingMonthGM.getContract().getSupplier().getId());
		query.setString("someMonth", billingMonthGM.getYyyymmdd() + "%");
		
		Double averageUsage = Double.parseDouble((query.list().get(0) == null ? 0.0 : query.list().get(0)).toString());
		
		return averageUsage;
	}

	/* (non-Javadoc)
	 * @see com.aimir.dao.mvm.BillingMonthGMDao#getBillingMonthGMs(com.aimir.model.mvm.BillingMonthGM)
	 */
	@SuppressWarnings("unchecked")
	public List<BillingMonthGM> getBillingMonthGMs(BillingMonthGM billingMonthGM, String startDay, String finishDay) {
		
		Criteria criteria = getSession().createCriteria(BillingMonthGM.class);
		
		if (billingMonthGM != null) {
			
			if (billingMonthGM.getContract() != null) {
				
				if (billingMonthGM.getContract().getId() != null) {
		
					criteria.add(Restrictions.eq("contract.id", billingMonthGM.getContract().getId()));
				}
			}
			
			if (billingMonthGM.getYyyymmdd() != null) {
				
				if (8 == billingMonthGM.getYyyymmdd().length()) {
					
					criteria.add(Restrictions.eq("id.yyyymmdd", billingMonthGM.getYyyymmdd()));
				} else if (6 == billingMonthGM.getYyyymmdd().length()) {
					
					criteria.add(Restrictions.like("id.yyyymmdd", billingMonthGM.getYyyymmdd() + "%"));
					criteria.addOrder(Property.forName("id.yyyymmdd").desc());
				} else if (4 == billingMonthGM.getYyyymmdd().length()) {
					
					criteria.add(Restrictions.like("id.yyyymmdd", billingMonthGM.getYyyymmdd() + "%"));
					criteria.addOrder(Property.forName("id.yyyymmdd").asc());
				}
			} else {
				
				criteria.addOrder(Property.forName("id.yyyymmdd").desc());
			}
			
			if (startDay != null) {
				
				//criteria.add(Restrictions.gt("id.yyyymmdd", startDay));
				criteria.add(Restrictions.ge("id.yyyymmdd", startDay));
			}
			
			if (finishDay != null) {
				
				criteria.add(Restrictions.le("id.yyyymmdd", finishDay));
			}
		}

		return criteria.list();
	}

	/* (non-Javadoc)
	 * @see com.aimir.dao.mvm.BillingMonthGMDao#getBillingMonthGMsAvg(com.aimir.model.mvm.BillingMonthGM, java.lang.String, java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	public List<Object> getBillingMonthGMsAvg(BillingMonthGM billingMonthGM, String fromDay, String toDay) {
		
		StringBuffer sbSql = new StringBuffer();
		sbSql.append(" SELECT yyyymmdd AS yyyymmdd  ")
		.append(", AVG(bill) AS bill ")
		.append(" FROM BILLING_MONTH_GM ");

		if(billingMonthGM.getYyyymmdd() != null && billingMonthGM.getYyyymmdd().length() == 4) {
			sbSql.append(" WHERE yyyymmdd like '").append(billingMonthGM.id.getYyyymmdd()).append("%' ");			
		} else {
			sbSql.append(" WHERE yyyymmdd >= '").append(fromDay).append("' ")
			.append(" AND yyyymmdd <= '").append(toDay).append("' ");
		}

		sbSql.append(" AND location_id = :locationId ")
		.append(" GROUP BY yyyymmdd ORDER BY yyyymmdd ");

		SQLQuery query = getSession().createSQLQuery(sbSql.toString());
		query.setInteger("locationId", billingMonthGM.getLocation().getId());

		return query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
	}

	/* (non-Javadoc)
	 * @see com.aimir.dao.mvm.BillingMonthGMDao#getMaxBill(com.aimir.model.system.Contract)
	 */
	public Double getMaxBill(Contract contract, String yyyymmdd) {

		Criteria criteria = getSession().createCriteria(BillingMonthGM.class);
		
		if (contract != null) {
		
			criteria.add(Restrictions.eq("contract.id", contract.getId()));
		}
		
		if (yyyymmdd != null) {
			criteria.add(Restrictions.ilike("id.yyyymmdd", yyyymmdd + "%"));
		}
		
		criteria.setProjection( Projections.projectionList().add( Projections.max("bill") ) );
		
		return (Double)(criteria.list().get(0) == null ? 0.0 : criteria.list().get(0));
	}

	/* (non-Javadoc)
	 * @see com.aimir.dao.mvm.BillingMonthGMDao#getAverageBill(com.aimir.model.system.Contract)
	 */
	public Double getAverageBill(Contract contract, String yyyymmdd) {
		
		Criteria criteria = getSession().createCriteria(BillingMonthGM.class);
		
//		if (contract != null) {
//		
//			criteria.add(Restrictions.eq("contract.id", contract.getId()));
//		}
		
		if (contract.getLocation() != null) {

			criteria.add(Restrictions.eq("location.id", contract.getLocation().getId()));
		}
		
		if (yyyymmdd != null) {
			criteria.add(Restrictions.ilike("id.yyyymmdd", yyyymmdd  + "%"));
		}

		criteria.setProjection( Projections.projectionList().add( Projections.avg("bill") ) );
		
		return (Double)(criteria.list().get(0) == null ? 0.0 : criteria.list().get(0));
	}

}