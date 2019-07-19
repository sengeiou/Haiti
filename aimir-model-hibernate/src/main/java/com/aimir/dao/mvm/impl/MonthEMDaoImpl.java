/**
 * MonthEMDaoImpl.java Copyright NuriTelecom Limited 2011
 */

package com.aimir.dao.mvm.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.aimir.constants.CommonConstants;
import com.aimir.constants.CommonConstants.ContractStatus;
import com.aimir.constants.CommonConstants.DateType;
import com.aimir.constants.CommonConstants.DefaultChannel;
import com.aimir.constants.CommonConstants.DeviceType;
import com.aimir.constants.CommonConstants.ElectricityChannel;
import com.aimir.constants.CommonConstants.HomeDeviceCategoryType;
import com.aimir.constants.CommonConstants.MeterType;
import com.aimir.dao.AbstractHibernateGenericDao;
import com.aimir.dao.mvm.MonthEMDao;
import com.aimir.model.device.Meter;
import com.aimir.model.mvm.MonthEM;
import com.aimir.model.system.Contract;
import com.aimir.util.CalendarUtil;
import com.aimir.util.Condition;
import com.aimir.util.SQLWrapper;
import com.aimir.util.SearchCondition;
import com.aimir.util.StringUtil;

/**
 * MonthEMDaoImpl.java Description 
 *
 * 
 * Date          Version     Author   Description
 * 2011. 5. 11.   v1.0       김상연         MonthEM 조회 - 조건(MonthEM)
 * 2011. 5. 11.   v1.1       김상연         MonthEM 합계 조회 - 조건(MonthEM)
 * 2011. 5. 25.   v1.2       김상연         기기별 그리드 조회
 *
 */
@Repository(value = "monthemDao")
@SuppressWarnings("unchecked")
@Transactional
public class MonthEMDaoImpl extends
		AbstractHibernateGenericDao<MonthEM, Integer> implements MonthEMDao {

	private static Log logger = LogFactory.getLog(MonthEMDaoImpl.class);

	@Autowired
	protected MonthEMDaoImpl(SessionFactory sessionFactory) {
		super(MonthEM.class);
		super.setSessionFactory(sessionFactory);
	}

	@Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
	public List<MonthEM> getMonthEMsByListCondition(Set<Condition> set) {

		return findByConditions(set);
	}

	@Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
	public List<Object> getMonthEMsCountByListCondition(Set<Condition> set) {

		return findTotalCountByConditions(set);
	}
	
	@Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
	public List<MonthEM> getMonthEMsByCondition(Map<String, Object> condition) {
        
        DeviceType mdevType     = (DeviceType) condition.get("mdevType");
        String mdevId           = (String) condition.get("mdevId");
        List<Integer> channelList  = (List<Integer>) condition.get("channelList");
        String  yyyymm          = (String) condition.get("yyyymm");
        Integer  dst      = (Integer) condition.get("dst");
        
        Query query = null;
        List<MonthEM> returnList = new ArrayList<MonthEM>();
        try {
            
            StringBuffer sb = new StringBuffer();
            sb.append("\nSELECT     em.id.yyyymm as yyyymm, sum(em.total) as total, ");
            sb.append("\n           sum(em.value_01) as value_01, sum(em.value_02) as value_02, sum(em.value_03) as value_03, sum(em.value_04) as value_04, ");
            sb.append("\n           sum(em.value_05) as value_05, sum(em.value_06) as value_06, sum(em.value_07) as value_07, sum(em.value_08) as value_08, ");
            sb.append("\n           sum(em.value_09) as value_09, sum(em.value_10) as value_10, sum(em.value_11) as value_11, sum(em.value_12) as value_12, ");
            sb.append("\n           sum(em.value_13) as value_13, sum(em.value_14) as value_14, sum(em.value_15) as value_15, sum(em.value_16) as value_16, ");
            sb.append("\n           sum(em.value_17) as value_17, sum(em.value_18) as value_18, sum(em.value_19) as value_19, sum(em.value_20) as value_20, ");
            sb.append("\n           sum(em.value_21) as value_21, sum(em.value_22) as value_22, sum(em.value_23) as value_23, sum(em.value_24) as value_24, ");
            sb.append("\n           sum(em.value_25) as value_25, sum(em.value_26) as value_26, sum(em.value_27) as value_27, sum(em.value_28) as value_28, ");
            sb.append("\n           sum(em.value_29) as value_29, sum(em.value_30) as value_30, sum(em.value_31) as value_31, ");
            sb.append("\n           sum(em.baseValue) as baseValue");
            sb.append("\nFROM       MonthEM em");
            sb.append("\nWHERE      em.id.mdevType = :mdevType ");
            sb.append("\nAND        em.id.mdevId = :mdevId ");
            sb.append("\nAND        em.id.yyyymm = :yyyymm ");
            sb.append("\nAND        em.id.channel in (:channelList) ");
            sb.append("\nAND        em.id.dst = :dst ");
            sb.append("\nGROUP BY   em.id.mdevId, em.id.mdevType, em.id.yyyymm ");

            
            query = getSession().createQuery(sb.toString());
            query.setParameter("mdevType", mdevType);
            query.setParameter("mdevId", mdevId);
            query.setParameter("yyyymm", yyyymm);
            query.setParameterList("channelList", channelList);
            query.setParameter("dst", dst);

            returnList = query.setResultTransformer(Transformers.aliasToBean(MonthEM.class)).list();
        } catch (Exception e) {
            // TODO: handle exception
            logger.error(e,e);
        }
        return returnList;
    }

	@Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
	public List<Object> getMonthEMsMaxMinAvgSum(Set<Condition> conditions,
			String div) {

		Criteria criteria = getSession().createCriteria(MonthEM.class);
		ProjectionList pjl = Projections.projectionList();
		if (conditions != null) {
			Iterator it = conditions.iterator();
			while (it.hasNext()) {
				Condition condition = (Condition) it.next();
				Criterion addCriterion = SearchCondition
						.getCriterion(condition);

				if (addCriterion != null) {
					criteria.add(addCriterion);
				}
			}
		}

		if ("max".equals(div)) {
			pjl.add(Projections.max("total"));
		} else if ("min".equals(div)) {
			pjl.add(Projections.min("total"));
		} else if ("avg".equals(div)) {
			pjl.add(Projections.avg("total"));
		} else if ("sum".equals(div)) {
			pjl.add(Projections.sum("total"));
		} else if ("minDate".equals(div)) {
			pjl.add(Projections.min("id.yyyymm"));
		}
		criteria.setProjection(pjl);
		return criteria.list();
	}
	
	@Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
    public List<Object> getMonthEMsCount(Set<Condition> conditions,
            String div) {

        
        DetachedCriteria  criteriaSub = DetachedCriteria.forClass(MonthEM.class);
        Criteria criteria = getSession().createCriteria(MonthEM.class);

        if (conditions != null) {
            Iterator it = conditions.iterator();
            while (it.hasNext()) {
                Condition condition = (Condition) it.next();
                Criterion addCriterion = SearchCondition
                        .getCriterion(condition);

                if (addCriterion != null) {
                	criteriaSub.add(addCriterion);
                }
            }
        }

        ProjectionList pjl = Projections.projectionList();

        if ("groupby".equals(div)) {
        	pjl.add(Projections.groupProperty("id.yyyymm"));
        }
        criteriaSub.setProjection(pjl);
        
        ProjectionList pj2 = Projections.projectionList();
        pj2.add(Projections.countDistinct("id.yyyymm"));
        
        criteria.add(Subqueries.propertyIn("id.yyyymm", criteriaSub));
        criteria.setProjection(pj2);
        
        return criteria.list();
    	
    }
	
	@Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
	public List<Map<String, Object>> getYearlyUsageTotal( List<String> yyyymm, int locationId ) {
		StringBuilder sb = new StringBuilder()
			.append("select m.id.yyyymm as YYYYMM, sum(coalesce(m.total,0)) as TOTAL ")
			.append("from MonthEM m ")
			.append("where m.id.channel = 1 ")
			.append("and m.id.dst = 0 ")
			.append("and m.id.yyyymm in (:yyyymm) ")
			.append("and m.location.id = :locationId ")
			.append("group by m.id.yyyymm order by m.id.yyyymm");
		
		Query query = getSession().createQuery(sb.toString());
		query.setParameterList("yyyymm", yyyymm);
		query.setParameter("locationId", locationId);
		return query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
	}

	@Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
	public List<Object[]> getMonthBillingChartData(
			Map<String, String> conditionMap) 
	{

		String startDate = conditionMap.get("startDate").substring(0, 6);
		String endDate = conditionMap.get("endDate").substring(0, 6);
		String locationCondition = conditionMap.get("locationCondition");
		String searchDateType = conditionMap.get("searchDateType");
		int supplierId = Integer.parseInt(conditionMap.get("supplierId").toString());

		StringBuilder sb = new StringBuilder()
				.append(" SELECT sum(m.total), m.location.name 				           ")
				.append("   FROM MonthEM m                       			           ")
				.append("  WHERE m.id.channel = :channel                               ")
				.append("    AND m.contract.serviceTypeCode.code = :serviceTypeCode ")
				.append("    AND m.contract.status.code = :status                   ")
				.append("    AND m.contract.customer.supplierId = :supplierId                   ");

		if ("4".equals(searchDateType))
		{ // 월별
			sb.append("    AND m.id.yyyymm = :startDate    ");
		} else if ("7".equals(searchDateType))
		{ // 분기별
			sb.append("    AND m.id.yyyymm >= :startDate   ");
			sb.append("    AND m.id.yyyymm <= :endDate     ");
		}
		if (!"".equals(locationCondition))
		{
			//중랑구 code 15 ,  망우동 17
			//locationCondition= "15";
			sb.append("   AND m.location.id in (" + locationCondition + ")");
		}
		else
		{
			//로케이션 id 존재
			sb.append("  ");
		}

		sb.append("  GROUP BY m.location.name               ");

		Query query = getSession().createQuery(sb.toString());
		
		String channel = DefaultChannel.Usage.getCode() + "";
		query.setString("channel",channel );
		
		String serviceTypeCode= MeterType.EnergyMeter.getServiceType();
		query.setString("serviceTypeCode",serviceTypeCode	);
		
		String status=         ContractStatus.NORMAL.getCode();
		
		query.setString("status",status );
		
		query.setInteger("supplierId", supplierId);

		if ("4".equals(searchDateType))
		{
			query.setString("startDate", startDate);
		} else if ("7".equals(searchDateType))
		{
			query.setString("startDate", startDate);
			query.setString("endDate", endDate);
		}

		return query.list();
	}

	@Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
	public List<MonthEM> getMonthCustomerBillingGridData(Map<String, Object> conditionMap) {

        String startDate = ((String) conditionMap.get("startDate")).substring(0, 6);
        String endDate = ((String) conditionMap.get("endDate")).substring(0, 6);
        String searchDateType = ((String) conditionMap.get("searchDateType"));
        String locationCondition = ((String) conditionMap.get("locationCondition"));
        String tariffIndex = conditionMap.get("tariffIndex").toString();
        String customerName = ((String) conditionMap.get("customerName"));
        String contractNo = ((String) conditionMap.get("contractNo"));
        String meterName = ((String) conditionMap.get("meterName"));
        int supplierId = Integer.parseInt(conditionMap.get("supplierId").toString());
        
        int page = Integer.parseInt((String) conditionMap.get("page"));
        int pageSize = Integer.parseInt((String) conditionMap.get("pageSize"));

        StringBuilder sb = new StringBuilder();
        sb.append("FROM MonthEM m ");
        sb.append("WHERE m.id.channel = :channel ");
        sb.append("AND   m.contract.serviceTypeCode.code = :serviceTypeCode ");
        sb.append("AND   m.contract.status.code = :status ");
        sb.append("AND   m.contract.customer.supplier.id = :supplierId      ");	

        if ("4".equals(searchDateType)) { // Monthly
            sb.append("AND   m.id.yyyymm = :startDate ");
        } else if ("7".equals(searchDateType)) { // Seasonal
            sb.append("AND   m.id.yyyymm >= :startDate ");
            sb.append("AND   m.id.yyyymm <= :endDate ");
        }
        if (!"".equals(locationCondition)) {
            sb.append("AND   m.location.id in (").append(locationCondition).append(") ");
        }
        if (!"".equals(tariffIndex)) {
            sb.append("AND   m.contract.tariffIndex = :tariffIndex ");
        }
        if (!"".equals(customerName)) {
            sb.append("AND   m.contract.customer.name LIKE :customerName ");
        }
        if (!"".equals(contractNo)) {
            sb.append("AND   m.contract.contractNumber LIKE :contractNo ");
        }
        if (!"".equals(meterName)) {
            sb.append("AND   m.meter.mdsId LIKE :meterName ");
        }

        Query query = getSession().createQuery(sb.toString());
//        query.setString("channel", DefaultChannel.Usage.getCode() + "");
        query.setInteger("channel", DefaultChannel.Usage.getCode());
        query.setString("serviceTypeCode", MeterType.EnergyMeter.getServiceType());
        query.setString("status", ContractStatus.NORMAL.getCode());
        query.setInteger("supplierId", supplierId);
        
        if ("4".equals(searchDateType)) {
            query.setString("startDate", startDate);
        } else if ("7".equals(searchDateType)) {
            query.setString("startDate", startDate);
            query.setString("endDate", endDate);
        }

        if (!"".equals(tariffIndex)) {
            query.setString("tariffIndex", tariffIndex);
        }
        if (!"".equals(customerName)) {
            query.setString("customerName", new StringBuilder().append('%').append(customerName).append('%').toString());
        }
        if (!"".equals(contractNo)) {
            query.setString("contractNo", new StringBuilder().append('%').append(contractNo).append('%').toString());
        }
        if (!"".equals(meterName)) {
            query.setString("meterName", new StringBuilder().append('%').append(meterName).append('%').toString());
        }

        int firstResult = page * pageSize;

        query.setFirstResult(firstResult);
        query.setMaxResults(pageSize);

        return query.list();
    }

    @Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
    public Long getElecCustomerBillingGridDataCount(Map<String, Object> conditionMap) {

        String startDate = (String) conditionMap.get("startDate");
        String endDate = (String) conditionMap.get("endDate");
        String searchDateType = (String) conditionMap.get("searchDateType");
        String locationCondition = (String) conditionMap.get("locationCondition");
        String tariffIndex = (String) conditionMap.get("tariffIndex");
        String customerName = (String) conditionMap.get("customerName");
        String contractNo = (String) conditionMap.get("contractNo");
        String meterName = (String) conditionMap.get("meterName");
        int supplierId = Integer.parseInt(conditionMap.get("supplierId").toString());
        
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT COUNT(*) ");
        sb.append("FROM MonthEM m ");
        sb.append("WHERE m.id.channel = :channel ");
        sb.append("AND   m.contract.serviceTypeCode.code = :serviceTypeCode ");
        sb.append("AND   m.contract.status.code = :status ");
        sb.append("AND   m.contract.customer.supplier.id = :supplierId ");
        
        if ("4".equals(searchDateType)) { // 일별
            sb.append("AND   m.id.yyyymm = :startDate ");
        } else if ("7".equals(searchDateType)) { // 월별
            sb.append("AND   m.id.yyyymm >= :startDate ");
            sb.append("AND   m.id.yyyymm <= :endDate ");
        }
        if (!"".equals(locationCondition)) {
            sb.append("AND   m.location.id in (").append(locationCondition).append(") ");
        }
        if (!"".equals(tariffIndex)) {
            sb.append("AND   m.contract.tariffIndex = :tariffIndex ");
        }
        if (!"".equals(customerName)) {
            sb.append("AND   m.contract.customer.name LIKE :customerName ");
        }
        if (!"".equals(contractNo)) {
            sb.append("AND   m.contract.contractNumber LIKE :contractNo ");
        }
        if (!"".equals(meterName)) {
            sb.append("AND   m.meter.mdsId LIKE :meterName ");
        }

        Query query = getSession().createQuery(sb.toString());
        // criteria.setProjection(Projections.rowCount());

//        query.setString("channel", DefaultChannel.Usage.getCode() + "");
        query.setInteger("channel", DefaultChannel.Usage.getCode());
        query.setString("serviceTypeCode", MeterType.EnergyMeter.getServiceType());
        query.setString("status", ContractStatus.NORMAL.getCode());
        query.setInteger("supplierId", supplierId);
        
        if ("4".equals(searchDateType)) {
            query.setString("startDate", startDate.substring(0, 6));
        } else if ("7".equals(searchDateType)) {
            query.setString("startDate", startDate.substring(0, 6));
            query.setString("endDate", endDate.substring(0, 6));
        }

        if (!"".equals(tariffIndex)) {
            query.setString("tariffIndex", tariffIndex);
        }
        if (!"".equals(customerName)) {
            query.setString("customerName", new StringBuilder().append('%').append(customerName).append('%').toString());
        }
        if (!"".equals(contractNo)) {
            query.setString("contractNo", new StringBuilder().append('%').append(contractNo).append('%').toString());
        }
        if (!"".equals(meterName)) {
            query.setString("meterName", new StringBuilder().append('%').append(meterName).append('%').toString());
        }

//        return (Long) query.uniqueResult();
        Long count = ((Number) query.uniqueResult()).longValue();
        return count;
    }

    @Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
    public List<Object[]> getMonthBillingGridData(
			Map<String, String> conditionMap) {

		String startDate = conditionMap.get("startDate").substring(0, 6);
		String endDate = conditionMap.get("endDate").substring(0, 6);
		String locationCondition = conditionMap.get("locationCondition");
		String searchDateType = conditionMap.get("searchDateType");
		int supplierId = Integer.parseInt(conditionMap.get("supplierId")); 
		
		StringBuilder sb = new StringBuilder()
				.append(" SELECT sum(m.total), count(*),                ")
				.append(
						"        sum(m.baseValue+m.value_01+m.value_02+m.value_03+m.value_04+m.value_05+m.value_06+m.value_07+m.value_08+")
				.append(
						"            m.value_09+m.value_10+m.value_11+m.value_12+m.value_13+m.value_14+m.value_15+m.value_16+m.value_17+m.value_18+")
				.append(
						"            m.value_19+m.value_20+m.value_21+m.value_22+m.value_23+m.value_24+m.value_25+m.value_26+m.value_27+m.value_28+")
				.append("            m.value_29+m.value_30+m.value_31), ")
				.append(
						"         max(m.value_01),max(m.value_02),max(m.value_03),max(m.value_04),max(m.value_05),max(m.value_06), ")
				.append(
						"         max(m.value_07),max(m.value_08),max(m.value_09),max(m.value_10),max(m.value_11),max(m.value_12),max(m.value_13), ")
				.append(
						"         max(m.value_14),max(m.value_15),max(m.value_16),max(m.value_17),max(m.value_18),max(m.value_19),max(m.value_20), ")
				.append(
						"         max(m.value_21),max(m.value_22),max(m.value_23),max(m.value_24),max(m.value_25),max(m.value_26),max(m.value_27), ")
				.append(
						"         max(m.value_28),max(m.value_29),max(m.value_30),max(m.value_31) ")
				.append(
						"   FROM MonthEM m                                          ")
				.append(
						"  WHERE m.id.channel = :channel                            ")
				.append(
						"    AND m.contract.serviceTypeCode.code = :serviceTypeCode ")
				.append(
						"    AND m.contract.status.code = :status                   ")
				.append(
						"    AND m.contract.customer.supplier.id = :supplierId      ");

		if ("4".equals(searchDateType)) {
			sb.append("    AND m.id.yyyymm = :startDate    ");
		} else if ("7".equals(searchDateType)) {
			sb.append("    AND m.id.yyyymm >= :startDate   ");
			sb.append("    AND m.id.yyyymm <= :endDate     ");
		}

		if (!"".equals(locationCondition)) {
			sb.append("   AND m.location.id in (" + locationCondition + ")");
		}

		Query query = getSession().createQuery(sb.toString());
		query.setString("channel", DefaultChannel.Usage.getCode() + "");
		query.setString("serviceTypeCode", MeterType.EnergyMeter
				.getServiceType());
		query.setString("status", ContractStatus.NORMAL.getCode());
		query.setInteger("supplierId", supplierId);
		
		if ("4".equals(searchDateType)) {
			query.setString("startDate", startDate);
		} else if ("7".equals(searchDateType)) {
			query.setString("startDate", startDate);
			query.setString("endDate", endDate);
		}

		return query.list();
	}

	/*
	 * BEMS 주기에 따른 빌딩의 TOTAL 사용량 , TOTAL 탄소배출량 차트 데이터 조회.
	 */
	@Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
	public List<Object> getConsumptionEmCo2MonthSearchDayTypeTotal2(
			Map<String, Object> condition) {

		logger
				.info("BEMS 주기에 따른 빌딩의  TOTAL 사용량 , TOTAL 탄소배출량  차트 데이터 조회.\n==== conditions ====\n"
						+ condition);

		String searchDateType = ObjectUtils.defaultIfNull(
				condition.get("searchDateType"),
				CommonConstants.DateType.DAILY.getCode()).toString(); // 일 , 주 ,
																		// 월 ,
																		// 분기
		Integer locationId = (Integer) condition.get("locationId");
		Integer channel = (Integer) condition.get("channel");

		String startDateT = ObjectUtils.defaultIfNull(
				condition.get("startDate"), "").toString();
		String startDate = "";
		if (startDateT.length() > 6) {
			startDate = startDateT.substring(0, 6);
		} else {
			startDate = startDateT;
		}
		String endDateT = ObjectUtils.defaultIfNull(condition.get("endDate"),
				"").toString();
		String endDate = "";

		if (endDateT.length() > 6) {
			endDate = endDateT.substring(0, 6);
		} else {

			endDate = endDateT;
		}
		String meterType = ObjectUtils.defaultIfNull(
				condition.get("meterType"), "").toString();

		StringBuffer sb = new StringBuffer();

		sb
				.append("\n  SELECT SUM(m.TOTAL) AS TOTAL,SUM(m.VALUE_01) AS VALUE_01,SUM(m.VALUE_02) VALUE_02,");
		sb
				.append("\n  		SUM(m.VALUE_03) AS VALUE_03,SUM(m.VALUE_04) VALUE_04,");
		sb
				.append("\n  		SUM(m.VALUE_05) AS VALUE_05,SUM(m.VALUE_06) VALUE_06,");
		sb
				.append("\n  		SUM(m.VALUE_07) AS VALUE_07,SUM(m.VALUE_08) VALUE_08,");
		sb
				.append("\n  		SUM(m.VALUE_09) AS VALUE_09,SUM(m.VALUE_10) VALUE_10,");
		sb
				.append("\n  		SUM(m.VALUE_11) AS VALUE_11,SUM(m.VALUE_12) VALUE_12,");
		sb
				.append("\n  		SUM(m.VALUE_13) AS VALUE_13,SUM(m.VALUE_14) VALUE_14,");
		sb
				.append("\n  		SUM(m.VALUE_15) AS VALUE_15,SUM(m.VALUE_16) VALUE_16,");
		sb
				.append("\n  		SUM(m.VALUE_17) AS VALUE_17,SUM(m.VALUE_18) VALUE_18,");
		sb
				.append("\n  		SUM(m.VALUE_19) AS VALUE_19,SUM(m.VALUE_20) VALUE_20,");
		sb
				.append("\n  		SUM(m.VALUE_21) AS VALUE_21,SUM(m.VALUE_22) VALUE_22,");
		sb
				.append("\n  		SUM(m.VALUE_23) AS VALUE_23,SUM(m.VALUE_24) VALUE_24,");
		sb
				.append("\n  		SUM(m.VALUE_25) AS VALUE_25,SUM(m.VALUE_26) VALUE_26,");
		sb
				.append("\n  		SUM(m.VALUE_27) AS VALUE_27,SUM(m.VALUE_28) VALUE_28,");
		sb
				.append("\n  		SUM(m.VALUE_29) AS VALUE_29,SUM(m.VALUE_30) VALUE_30,");
		sb.append("\n  		SUM(m.VALUE_31) AS VALUE_31 ");
		sb
				.append("\n from "
						+ meterType
						+ " m inner join (select id from location where parent_id=:parentId) l ");
		sb.append("\n on m.location_id=l.id where channel=:channel  ");
		if (CommonConstants.DateType.QUARTERLY.getCode().equals(searchDateType)) {
			sb.append("\n and m.yyyymm between :startDate and :endDate ");
		} else {
			sb.append("\n and m.yyyymm=:startDate ");
		}
	
		SQLQuery query = getSession().createSQLQuery(sb.toString());

		query.setInteger("parentId", locationId);
		query.setInteger("channel", channel);

		if (CommonConstants.DateType.QUARTERLY.getCode().equals(searchDateType)) {
			query.setString("startDate", startDate);
			query.setString("endDate", endDate);
		} else {
			query.setString("startDate", startDate);
		}

		return query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP)
				.list();
	}

	/*
	 * BEMS 주기에 따른 빌딩의 TOTAL 사용량 , TOTAL 탄소배출량 차트 데이터 조회.
	 */
	@Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
	public List<Object> getConsumptionEmCo2MonthSearchDayTypeTotal(
			Map<String, Object> condition) {

		logger
				.info("BEMS 주기에 따른 빌딩의  TOTAL 사용량 , TOTAL 탄소배출량  차트 데이터 조회.\n==== conditions ====\n"
						+ condition);

		String searchDateType = ObjectUtils.defaultIfNull(
				condition.get("searchDateType"),
				CommonConstants.DateType.DAILY.getCode()).toString(); // 일 , 주 ,
																		// 월 ,
																		// 분기
		Integer locationId = (Integer) condition.get("locationId");

		String startDateT = ObjectUtils.defaultIfNull(
				condition.get("startDate"), "").toString();
		String startDate = "";
		if (startDateT.length() > 6) {
			startDate = startDateT.substring(0, 6);
		} else {
			startDate = startDateT;
		}
		String endDateT = ObjectUtils.defaultIfNull(condition.get("endDate"),
				"").toString();
		String endDate = "";
		if (endDateT.length() > 6) {
			endDate = endDateT.substring(0, 6);
		} else {

			endDate = endDateT;
		}

		StringBuffer sb = new StringBuffer();

		sb
				.append("\n SELECT EM.SUPPLIER_ID , EM.TOTAL , CO2.CO2_TOTAL FROM (  ");
		sb
				.append("\n 	SELECT LL.SUPPLIER_ID , SUM(D.TOTAL) TOTAL FROM MONTH_EM D , ( ");
		sb
				.append("\n 		SELECT M.MDS_ID , L.ID , L.NAME , L.SUPPLIER_ID FROM METER M , (  ");
		sb
				.append("\n 			SELECT ID , NAME , SUPPLIER_ID FROM LOCATION WHERE PARENT_ID = :parentId ");
		sb.append("\n 		) L WHERE M.LOCATION_ID = L.ID ");
		// sb.append("\n 		   AND M.METERTYPE_ID = (  ");
		// sb.append("\n 		   		SELECT CC.id FROM code AS CC WHERE CC.parent_id = ( ");
		// sb.append("\n 		   			SELECT C.id FROM code AS C WHERE C.CODE = '1.3.1' ");
		// // MeterType
		// sb.append("\n 		   		) AND CC.CODE = '1.3.1.1' "); // EnergyMeter
		// sb.append("\n 		   	) ");
		sb.append("\n 	)LL WHERE D.MDEV_ID = LL.MDS_ID AND D.CHANNEL=1 ");
		if (CommonConstants.DateType.WEEKLY.getCode().equals(searchDateType)
				|| CommonConstants.DateType.MONTHLY.getCode().equals(
						searchDateType)
				|| CommonConstants.DateType.QUARTERLY.getCode().equals(
						searchDateType)) { // 주/월/분기별
			sb.append("    AND D.YYYYMM >= :startDate   ");
			sb.append("    AND D.YYYYMM <= :endDate     ");
		}
		sb.append("\n 	GROUP BY LL.SUPPLIER_ID  ");
		sb.append("\n ) EM ");
		sb.append("\n LEFT JOIN ");
		sb.append("\n (  ");
		sb
				.append("\n 	SELECT LL2.SUPPLIER_ID , SUM(D2.TOTAL) AS CO2_TOTAL FROM MONTH_EM D2 , ( ");
		sb
				.append("\n 		SELECT M2.MDS_ID , L2.ID , L2.NAME , L2.SUPPLIER_ID FROM METER M2 , (  ");
		sb
				.append("\n 			SELECT ID , NAME , SUPPLIER_ID FROM LOCATION WHERE PARENT_ID = :parentId ");
		sb.append("\n 		) L2 WHERE M2.LOCATION_ID = L2.ID ");
		// sb.append("\n 		   AND M2.METERTYPE_ID = (  ");
		// sb.append("\n 		   		SELECT CC2.id FROM code AS CC2 WHERE CC2.parent_id = ( ");
		// sb.append("\n 		   			SELECT C2.id FROM code AS C2 WHERE C2.CODE = '1.3.1' ");
		// // MeterType
		// sb.append("\n 		   		) AND CC2.CODE = '1.3.1.1' "); // EnergyMeter
		// sb.append("\n 		   	) ");
		sb
				.append("\n 	) LL2 WHERE D2.MDEV_ID = LL2.MDS_ID AND D2.CHANNEL=0 ");
		if (CommonConstants.DateType.WEEKLY.getCode().equals(searchDateType)
				|| CommonConstants.DateType.MONTHLY.getCode().equals(
						searchDateType)
				|| CommonConstants.DateType.QUARTERLY.getCode().equals(
						searchDateType)) { // 주/월/분기별
			sb.append("    AND D2.YYYYMM >= :startDate   ");
			sb.append("    AND D2.YYYYMM <= :endDate     ");
		}
		sb.append("\n 	GROUP BY LL2.SUPPLIER_ID  ");
		sb.append("\n ) CO2 ");
		sb.append("\n ON EM.SUPPLIER_ID = CO2.SUPPLIER_ID ");

		SQLQuery query = getSession().createSQLQuery(sb.toString());

		query.setInteger("parentId", locationId);

		if (CommonConstants.DateType.WEEKLY.getCode().equals(searchDateType)
				|| CommonConstants.DateType.MONTHLY.getCode().equals(
						searchDateType)
				|| CommonConstants.DateType.QUARTERLY.getCode().equals(
						searchDateType)) {
			query.setString("startDate", startDate);
			query.setString("endDate", endDate);
		}

		return query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP)
				.list();
	}

	/**
	 * BEMS 동별 월단위 전력사용량 , 탄소배출량 차트 데이터 조회. 기준 데이터 키로 Location 테이블의 Id를 사용.
	 */
	@Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
	public List<Object> getConsumptionEmCo2MonthMonitoringLocationId(
			Map<String, Object> condition) {

		logger
				.info("BEMS 동별   월단위 전력사용량 , 탄소배출량  차트 데이터 조회.\n==== conditions ====\n"
						+ condition);

		String searchDateType = ObjectUtils.defaultIfNull(
				condition.get("searchDateType"),
				CommonConstants.DateType.DAILY.getCode()).toString(); // 일 , 주 ,
																		// 월 ,
																		// 분기
		@SuppressWarnings("unused")
		Integer supplierId = (Integer) condition.get("supplierId");
		Integer locationId = (Integer) condition.get("locationId");
		@SuppressWarnings("unused")
		String channelType = (String) condition.get("channelType"); // 탄소일 경우만 0
																	// ,
																	// 전력/온도/습도의
																	// 사용량일때는 1

		String startDateT = ObjectUtils.defaultIfNull(
				condition.get("startDate"), "").toString();
		String startDate = "";
		if (startDateT.length() > 6) {
			startDate = startDateT.substring(0, 6);
		} else {
			startDate = startDateT;
		}

		String endDateT = ObjectUtils.defaultIfNull(condition.get("endDate"),
				"").toString();
		String endDate = "";
		if (endDateT.length() > 6) {
			endDate = endDateT.substring(0, 6);
		} else {

			endDate = endDateT;
		}

		StringBuffer sb = new StringBuffer();

		sb
				.append("\n SELECT EM.LOCATION_ID , EM.NAME , EM.MDS_ID , EM.TOTAL , CO2.CO2_TOTAL FROM (  ");
		sb
				.append("\n 	SELECT LL.ID AS LOCATION_ID , LL.NAME , LL.MDS_ID , D.CHANNEL , SUM(D.TOTAL) TOTAL FROM MONTH_EM D , ( ");
		sb
				.append("\n 		SELECT M.MDS_ID , L.ID , L.NAME FROM METER M , LOCATION L ");
		sb.append("\n 		 WHERE M.LOCATION_ID = L.ID ");
		sb.append("\n 		   AND L.ID = :locationId ");
		// sb.append("\n 		   AND M.METERTYPE_ID = (  ");
		// sb.append("\n 		   		SELECT CC.id FROM code AS CC WHERE CC.parent_id = ( ");
		// sb.append("\n 		   			SELECT C.id FROM code AS C WHERE C.CODE = '1.3.1' ");
		// // MeterType
		// sb.append("\n 		   		) AND CC.CODE = '1.3.1.1' "); // EnergyMeter
		// sb.append("\n 		   	) ");
		sb.append("\n 	) LL WHERE D.MDEV_ID = LL.MDS_ID AND D.CHANNEL=1 ");
		if (CommonConstants.DateType.WEEKLY.getCode().equals(searchDateType)
				|| CommonConstants.DateType.MONTHLY.getCode().equals(
						searchDateType)
				|| CommonConstants.DateType.QUARTERLY.getCode().equals(
						searchDateType)) { // 주/월/분기별
			sb.append("    AND D.YYYYMM >= :startDate   ");
			sb.append("    AND D.YYYYMM <= :endDate     ");
		}
		sb
				.append("\n 	GROUP BY LL.ID , LL.NAME , LL.MDS_ID , D.CHANNEL , D.MDEV_ID  ");
		sb.append("\n ) EM ");
		sb.append("\n LEFT JOIN ");
		sb.append("\n (  ");
		sb
				.append("\n 	SELECT LL2.ID AS LOCATION_ID , SUM(D2.TOTAL) AS CO2_TOTAL FROM MONTH_EM D2 , ( ");
		sb
				.append("\n 		SELECT M2.MDS_ID , L2.ID , L2.NAME FROM METER M2 , LOCATION L2 ");
		sb.append("\n 		 WHERE M2.LOCATION_ID = L2.ID ");
		sb.append("\n 		   AND L2.ID = :locationId ");
		// sb.append("\n 		   AND M2.METERTYPE_ID = (  ");
		// sb.append("\n 		   		SELECT CC2.id FROM code AS CC2 WHERE CC2.parent_id = ( ");
		// sb.append("\n 		   			SELECT C2.id FROM code AS C2 WHERE C2.CODE = '1.3.1' ");
		// // MeterType
		// sb.append("\n 		   		) AND CC2.CODE = '1.3.1.1' "); // EnergyMeter
		// sb.append("\n 		   	) ");
		sb
				.append("\n 	) LL2 WHERE D2.MDEV_ID = LL2.MDS_ID AND D2.CHANNEL=0 ");
		if (CommonConstants.DateType.WEEKLY.getCode().equals(searchDateType)
				|| CommonConstants.DateType.MONTHLY.getCode().equals(
						searchDateType)
				|| CommonConstants.DateType.QUARTERLY.getCode().equals(
						searchDateType)) { // 주/월/분기별
			sb.append("    AND D2.YYYYMM >= :startDate   ");
			sb.append("    AND D2.YYYYMM <= :endDate     ");
		}
		sb.append("\n 	GROUP BY LL2.ID , D2.MDEV_ID  ");
		sb.append("\n ) CO2 ");
		sb.append("\n ON EM.LOCATION_ID = CO2.LOCATION_ID ");

		SQLQuery query = getSession().createSQLQuery(sb.toString());

		query.setInteger("parentId", locationId);

		if (CommonConstants.DateType.WEEKLY.getCode().equals(searchDateType)
				|| CommonConstants.DateType.MONTHLY.getCode().equals(
						searchDateType)
				|| CommonConstants.DateType.QUARTERLY.getCode().equals(
						searchDateType)) {
			query.setString("startDate", startDate);
			query.setString("endDate", endDate);
		}

		return query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP)
				.list();
	}

	/**
	 * BEMS 동별 월단위 전력사용량 , 탄소배출량 차트 데이터 조회. 기준 데이터 키로 Location 테이블의 PARENT_ID를
	 * 사용.
	 */
	@Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
	public List<Object> getConsumptionEmCo2MonthMonitoringParentId(
			Map<String, Object> condition) {

		logger
				.info("BEMS 동별   월단위 전력사용량 , 탄소배출량  차트 데이터 조회.\n==== conditions ====\n"
						+ condition);

		@SuppressWarnings("unused")
		String searchDateType = ObjectUtils.defaultIfNull(
				condition.get("searchDateType"),
				CommonConstants.DateType.DAILY.getCode()).toString(); // 일 , 주 ,
																		// 월 ,
																		// 분기
		@SuppressWarnings("unused")
		Integer supplierId = (Integer) condition.get("supplierId");
		Integer locationId = (Integer) condition.get("locationId");
		Integer channel = (Integer) condition.get("channel");
		@SuppressWarnings("unused")
		String channelType = (String) condition.get("channelType"); // 탄소일 경우만 0
																	// ,
																	// 전력/온도/습도의
																	// 사용량일때는 1
		String meterType = (String) condition.get("meterType");
		String startDateT = ObjectUtils.defaultIfNull(
				condition.get("startDate"), "").toString();
		String startDate = "";
		if (startDateT.length() > 6) {
			startDate = startDateT.substring(0, 6);
		} else {
			startDate = startDateT;
		}

		String endDateT = ObjectUtils.defaultIfNull(condition.get("endDate"),
				"").toString();
		String endDate = "";
		if (endDateT.length() > 6) {
			endDate = endDateT.substring(0, 6);
		} else {

			endDate = endDateT;
		}

		StringBuffer sb = new StringBuffer();

		sb.append("\n 	SELECT LL.ORDERNO , LL.ID AS LOCATION_ID , LL.NAME , LL.MDS_ID , D.CHANNEL , SUM(D.TOTAL) TOTAL FROM "
						+ meterType + " D , ( ");
		sb.append("\n 		SELECT M.MDS_ID , L.ID , L.NAME , L.ORDERNO FROM METER M  , (  ");
		sb.append("\n 	SELECT ID , NAME , ORDERNO  FROM LOCATION WHERE PARENT_ID = :parentId ");
		sb.append("\n 		) L WHERE M.LOCATION_ID = L.ID ");
		sb.append("\n 	)LL WHERE D.MDEV_ID = LL.MDS_ID AND D.CHANNEL=:channel ");
		sb.append("    AND D.YYYYMM >= :startDate   ");
		sb.append("    AND D.YYYYMM <= :endDate     ");
		sb.append("\n 	 GROUP BY LL.ORDERNO, LL.ID , LL.NAME ,  LL.MDS_ID , D.CHANNEL , D.MDEV_ID");
		sb.append("   ORDER BY LL.ORDERNO  ASC, LL.ID DESC");
		
		SQLQuery query = getSession().createSQLQuery(sb.toString());

		query.setInteger("parentId", locationId);
		query.setInteger("channel", channel);
		query.setString("startDate", startDate);
		query.setString("endDate", endDate);

		return query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP)
				.list();
	}

	/**
	 * BEMS 전력사용량 , 탄소배출량 차트 데이터 조회. ( 월간/분기 )
	 */
	@Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
	public List<Object> getConsumptionEmCo2MonitoringLocationId(
			Map<String, Object> condition) {

		logger
				.info("BEMS 전력사용량 , 탄소배출량  차트  데이터 조회. ( 월간/분기 )\n==== conditions ====\n"
						+ condition);

		String searchDateType = ObjectUtils.defaultIfNull(
				condition.get("searchDateType"),
				CommonConstants.DateType.DAILY.getCode()).toString(); // 일 , 주 ,
																		// 월 ,
																		// 분기
		@SuppressWarnings("unused")
		Integer supplierId = (Integer) condition.get("supplierId");
		Integer locationId = (Integer) condition.get("locationId");
		String startDateT = ObjectUtils.defaultIfNull(
				condition.get("startDate"), "").toString();
		String startDate = "";

		if (startDateT.length() > 6) {
			startDate = startDateT.substring(0, 6);
		} else {
			startDate = startDateT;
		}
		String endDateT = ObjectUtils.defaultIfNull(condition.get("endDate"),
				"").toString();
		String endDate = "";
		if (endDateT.length() > 6) {
			endDate = endDateT.substring(0, 6);
		} else {

			endDate = endDateT;
		}

		StringBuffer sb = new StringBuffer();

		sb
				.append("\n SELECT EM.YYYYMM AS YYYYMM , EM.TOTAL AS EM_TOTAL ,  CO2.TOTAL AS CO2_TOTAL  FROM (  ");

		sb.append("\n 	SELECT D.YYYYMM , SUM(D.TOTAL) AS TOTAL  ");
		sb.append("\n 	FROM MONTH_EM D , ( ");
		sb
				.append("\n 		SELECT M.MDS_ID , L.ID , L.NAME , L.PARENT_ID FROM METER M , LOCATION L WHERE M.LOCATION_ID = L.ID AND L.ID = :locationId ");
		// sb.append("\n 		   AND M.METERTYPE_ID = (  ");
		// sb.append("\n 		   		SELECT CC.id FROM code AS CC WHERE CC.parent_id = ( ");
		// sb.append("\n 		   			SELECT C.id FROM code AS C WHERE C.CODE = '1.3.1' ");
		// // MeterType
		// sb.append("\n 		   		) AND CC.CODE = '1.3.1.1' "); // EnergyMeter
		// sb.append("\n 		   	) ");
		sb.append("\n 	)LL WHERE D.MDEV_ID = LL.MDS_ID AND D.CHANNEL=1 ");
		if (CommonConstants.DateType.DAILY.getCode().equals(searchDateType)) { // 일별
			sb.append("\n    AND D.YYYYMM = :startDate    ");
			// sb.append("\n  AND D.YYYYMMDD='201005' ");
		} else if (CommonConstants.DateType.WEEKLY.getCode().equals(
				searchDateType)
				|| CommonConstants.DateType.MONTHLY.getCode().equals(
						searchDateType)
				|| CommonConstants.DateType.QUARTERLY.getCode().equals(
						searchDateType)) { // 주/월/분기별
			sb.append("\n    AND D.YYYYMM >= :startDate   ");
			sb.append("\n    AND D.YYYYMM <= :endDate     ");
		}
		sb.append("\n 	GROUP BY D.YYYYMM ");
		sb.append("\n ) EM ");
		sb.append("\n LEFT JOIN ");
		sb.append("\n (  ");
		sb.append("\n 	SELECT D2.YYYYMM , SUM(D2.TOTAL) AS TOTAL ");
		sb.append("\n 	FROM MONTH_EM D2 , ( ");
		sb
				.append("\n 		SELECT M2.MDS_ID , L2.ID , L2.NAME , L2.PARENT_ID FROM METER M2 , LOCATION L2 WHERE M2.LOCATION_ID = L2.ID AND L2.ID = :locationId ");
		// sb.append("\n 		   AND M2.METERTYPE_ID = (  ");
		// sb.append("\n 		   		SELECT CC2.id FROM code AS CC2 WHERE CC2.parent_id = ( ");
		// sb.append("\n 		   			SELECT C2.id FROM code AS C2 WHERE C2.CODE = '1.3.1' ");
		// // MeterType
		// sb.append("\n 		   		) AND CC2.CODE = '1.3.1.1' "); // EnergyMeter
		// sb.append("\n 		   	) ");
		sb
				.append("\n 	)LL2 WHERE D2.MDEV_ID = LL2.MDS_ID AND D2.CHANNEL=0 ");
		if (CommonConstants.DateType.DAILY.getCode().equals(searchDateType)) { // 일별
			sb.append("\n    AND D2.YYYYMM = :startDate    ");
			// sb.append("\n  AND D.YYYYMMDD='20100517' ");
		} else if (CommonConstants.DateType.WEEKLY.getCode().equals(
				searchDateType)
				|| CommonConstants.DateType.MONTHLY.getCode().equals(
						searchDateType)
				|| CommonConstants.DateType.QUARTERLY.getCode().equals(
						searchDateType)) { // 주/월/분기별
			sb.append("\n    AND D2.YYYYMM >= :startDate   ");
			sb.append("\n    AND D2.YYYYMM <= :endDate     ");
		}
		sb.append("\n 	GROUP BY D2.YYYYMM ");
		sb.append("\n ) CO2 ");
		sb.append("\n ON EM.YYYYMM = CO2.YYYYMM");

		SQLQuery query = getSession().createSQLQuery(sb.toString());

		query.setInteger("locationId", locationId);

		if (CommonConstants.DateType.DAILY.getCode().equals(searchDateType)) {
			query.setString("startDate", startDate);
		} else if (CommonConstants.DateType.WEEKLY.getCode().equals(
				searchDateType)
				|| CommonConstants.DateType.MONTHLY.getCode().equals(
						searchDateType)
				|| CommonConstants.DateType.QUARTERLY.getCode().equals(
						searchDateType)) {
			query.setString("startDate", startDate);
			query.setString("endDate", endDate);
		}
		
		// logger.info("BEMS 전력사용량 , 탄소배출량  차트  데이터 조회. ( 월간/분기 )\n==== sb.toString() ====\n"
		// + sb.toString());
		return query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP)
				.list();
	}

	/**
	 * BEMS 전력사용량 , 탄소배출량 차트 데이터 조회. ( 월간/분기 )
	 */
	@Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
	public List<Object> getConsumptionEmCo2MonitoringParentId(
			Map<String, Object> condition) {

		logger
				.info("BEMS 전력사용량 , 탄소배출량  차트  데이터 조회. ( 월간/분기 )\n==== conditions ====\n"
						+ condition);

		String searchDateType = ObjectUtils.defaultIfNull(
				condition.get("searchDateType"),
				CommonConstants.DateType.DAILY.getCode()).toString(); // 일 , 주 ,
																		// 월 ,
																		// 분기
		@SuppressWarnings("unused")
		Integer supplierId = (Integer) condition.get("supplierId");
		Integer locationId = (Integer) condition.get("locationId");
		String startDateT = ObjectUtils.defaultIfNull(
				condition.get("startDate"), "").toString();
		String startDate = "";

		if (startDateT.length() > 6) {
			startDate = startDateT.substring(0, 6);
		} else {
			startDate = startDateT;
		}
		String endDateT = ObjectUtils.defaultIfNull(condition.get("endDate"),
				"").toString();
		String endDate = "";
		if (endDateT.length() > 6) {
			endDate = endDateT.substring(0, 6);
		} else {

			endDate = endDateT;
		}

		StringBuffer sb = new StringBuffer();

		sb
				.append("\n SELECT EM.YYYYMM AS YYYYMM , EM.TOTAL AS EM_TOTAL ,  CO2.TOTAL AS CO2_TOTAL  FROM (  ");

		sb.append("\n 	SELECT D.YYYYMM , SUM(D.TOTAL) AS TOTAL  ");
		sb.append("\n 	FROM MONTH_EM D , ( ");
		sb
				.append("\n 		SELECT M.MDS_ID , L.ID , L.NAME , L.PARENT_ID FROM METER M , (  ");
		sb
				.append("\n 			SELECT ID , NAME , PARENT_ID FROM LOCATION WHERE PARENT_ID = :parentId ");
		sb.append("\n 		) L WHERE M.LOCATION_ID = L.ID ");
		// sb.append("\n 		   AND M.METERTYPE_ID = (  ");
		// sb.append("\n 		   		SELECT CC.id FROM code AS CC WHERE CC.parent_id = ( ");
		// sb.append("\n 		   			SELECT C.id FROM code AS C WHERE C.CODE = '1.3.1' ");
		// // MeterType
		// sb.append("\n 		   		) AND CC.CODE = '1.3.1.1' "); // EnergyMeter
		// sb.append("\n 		   	) ");
		sb.append("\n 	) LL WHERE D.MDEV_ID = LL.MDS_ID AND D.CHANNEL=1 ");
		if (CommonConstants.DateType.DAILY.getCode().equals(searchDateType)) { // 일별
			sb.append("\n    AND D.YYYYMM = :startDate    ");
			// sb.append("\n  AND D.YYYYMMDD='201005' ");
		} else if (CommonConstants.DateType.WEEKLY.getCode().equals(
				searchDateType)
				|| CommonConstants.DateType.MONTHLY.getCode().equals(
						searchDateType)
				|| CommonConstants.DateType.QUARTERLY.getCode().equals(
						searchDateType)) { // 주/월/분기별
			sb.append("\n    AND D.YYYYMM >= :startDate   ");
			sb.append("\n    AND D.YYYYMM <= :endDate     ");
		}
		sb.append("\n 	GROUP BY D.YYYYMM ");
		sb.append("\n ) EM ");
		sb.append("\n LEFT JOIN ");
		sb.append("\n (  ");
		sb.append("\n 	SELECT D2.YYYYMM , SUM(D2.TOTAL) AS TOTAL ");
		sb.append("\n 	FROM MONTH_EM D2 , ( ");
		sb
				.append("\n 		SELECT M2.MDS_ID , L2.ID , L2.NAME , L2.PARENT_ID FROM METER M2 , (  ");
		sb
				.append("\n 			SELECT ID , NAME , PARENT_ID FROM LOCATION WHERE PARENT_ID = :parentId ");
		sb.append("\n 		) L2 WHERE M2.LOCATION_ID = L2.ID ");
		// sb.append("\n 		   AND M2.METERTYPE_ID = (  ");
		// sb.append("\n 		   		SELECT CC2.id FROM code AS CC2 WHERE CC2.parent_id = ( ");
		// sb.append("\n 		   			SELECT C2.id FROM code AS C2 WHERE C2.CODE = '1.3.1' ");
		// // MeterType
		// sb.append("\n 		   		) AND CC2.CODE = '1.3.1.1' "); // EnergyMeter
		// sb.append("\n 		   	) ");
		sb
				.append("\n 	)LL2 WHERE D2.MDEV_ID = LL2.MDS_ID AND D2.CHANNEL=0 ");
		if (CommonConstants.DateType.DAILY.getCode().equals(searchDateType)) { // 일별
			sb.append("\n    AND D2.YYYYMM = :startDate    ");
			// sb.append("\n  AND D.YYYYMMDD='20100517' ");
		} else if (CommonConstants.DateType.WEEKLY.getCode().equals(
				searchDateType)
				|| CommonConstants.DateType.MONTHLY.getCode().equals(
						searchDateType)
				|| CommonConstants.DateType.QUARTERLY.getCode().equals(
						searchDateType)) { // 주/월/분기별
			sb.append("\n    AND D2.YYYYMM >= :startDate   ");
			sb.append("\n    AND D2.YYYYMM <= :endDate     ");
		}
		sb.append("\n 	GROUP BY D2.YYYYMM ");
		sb.append("\n ) CO2 ");
		sb.append("\n ON EM.YYYYMM = CO2.YYYYMM");

		SQLQuery query = getSession().createSQLQuery(sb.toString());

		query.setInteger("parentId", locationId);

		if (CommonConstants.DateType.DAILY.getCode().equals(searchDateType)) {
			query.setString("startDate", startDate);
		} else if (CommonConstants.DateType.WEEKLY.getCode().equals(
				searchDateType)
				|| CommonConstants.DateType.MONTHLY.getCode().equals(
						searchDateType)
				|| CommonConstants.DateType.QUARTERLY.getCode().equals(
						searchDateType)) {
			query.setString("startDate", startDate);
			query.setString("endDate", endDate);
		}
		// logger.info("BEMS 전력사용량 , 탄소배출량  차트  데이터 조회. ( 월간/분기 )\n==== sb.toString() ====\n"
		// + sb.toString());
		
		return query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP)
				.list();
	}

	/**
	 * BEMS 온도 습도 차트 데이터 조회. ( 월간/분기 ) , channel : 2 , 3
	 */
	@Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
	public List<Object> getConsumptionTmHmMonitoring(
			Map<String, Object> condition) {

		logger.info("BEMS 온도 습도  차트 데이터 조회. ( 월간/분기 )\n==== conditions ====\n"
				+ condition);

		String searchDateType = ObjectUtils.defaultIfNull(
				condition.get("searchDateType"),
				CommonConstants.DateType.DAILY.getCode()).toString(); // 일 , 주 ,
																		// 월 ,
																		// 분기
		@SuppressWarnings("unused")
		Integer supplierId = (Integer) condition.get("supplierId");
		Integer locationId = (Integer) condition.get("locationId");
		String startDateT = ObjectUtils.defaultIfNull(
				condition.get("startDate"), "").toString();
		String startDate = "";
		if (startDateT.length() > 6) {
			startDate = startDateT.substring(0, 6);
		} else {
			startDate = startDateT;
		}
		String endDateT = ObjectUtils.defaultIfNull(condition.get("endDate"),
				"").toString();
		String endDate = "";
		if (endDateT.length() > 6) {
			endDate = endDateT.substring(0, 6);
		} else {

			endDate = endDateT;
		}

		StringBuffer sb = new StringBuffer();

		sb
				.append("\n SELECT EM.YYYYMM AS YYYYMM , EM.TOTAL AS EM_TOTAL ,  CO2.TOTAL AS CO2_TOTAL  FROM (  ");

		sb.append("\n 	SELECT D.YYYYMM , SUM(D.TOTAL) AS TOTAL  ");
		sb.append("\n 	FROM MONTH_EM D , ( ");
		sb
				.append("\n 		SELECT M.MDS_ID , L.ID , L.NAME , L.PARENT_ID FROM METER M , (  ");
		sb
				.append("\n 			SELECT ID , NAME , PARENT_ID FROM LOCATION WHERE PARENT_ID = :parentId ");
		sb.append("\n 		) L WHERE M.LOCATION_ID = L.ID ");
		// sb.append("\n 		   AND M.METERTYPE_ID = (  ");
		// sb.append("\n 		   		SELECT CC.id FROM code AS CC WHERE CC.parent_id = ( ");
		// sb.append("\n 		   			SELECT C.id FROM code AS C WHERE C.CODE = '1.3.1' ");
		// // MeterType
		// sb.append("\n 		   		) AND CC.CODE = '1.3.1.1' "); // EnergyMeter
		// sb.append("\n 		   	) ");
		sb.append("\n 	) LL WHERE D.MDEV_ID = LL.MDS_ID AND D.CHANNEL=2 ");
		if (CommonConstants.DateType.DAILY.getCode().equals(searchDateType)) { // 일별
			sb.append("\n    AND D.YYYYMM = :startDate    ");
			// sb.append("\n  AND D.YYYYMMDD='201005' ");
		} else if (CommonConstants.DateType.WEEKLY.getCode().equals(
				searchDateType)
				|| CommonConstants.DateType.MONTHLY.getCode().equals(
						searchDateType)
				|| CommonConstants.DateType.QUARTERLY.getCode().equals(
						searchDateType)) { // 주/월/분기별
			sb.append("\n    AND D.YYYYMM >= :startDate   ");
			sb.append("\n    AND D.YYYYMM <= :endDate     ");
		}
		sb.append("\n 	GROUP BY D.YYYYMM ");
		sb.append("\n ) EM ");
		sb.append("\n LEFT JOIN ");
		sb.append("\n (  ");
		sb.append("\n 	SELECT D2.YYYYMM , SUM(D2.TOTAL) AS TOTAL ");
		sb.append("\n 	FROM MONTH_EM D2 , ( ");
		sb
				.append("\n 		SELECT M2.MDS_ID , L2.ID , L2.NAME , L2.PARENT_ID FROM METER M2 , (  ");
		sb
				.append("\n 			SELECT ID , NAME , PARENT_ID FROM LOCATION WHERE PARENT_ID = :parentId ");
		sb.append("\n 		) L2 WHERE M2.LOCATION_ID = L2.ID ");
		// sb.append("\n 		   AND M2.METERTYPE_ID = (  ");
		// sb.append("\n 		   		SELECT CC2.id FROM code AS CC2 WHERE CC2.parent_id = ( ");
		// sb.append("\n 		   			SELECT C2.id FROM code AS C2 WHERE C2.CODE = '1.3.1' ");
		// // MeterType
		// sb.append("\n 		   		) AND CC2.CODE = '1.3.1.1' "); // EnergyMeter
		// sb.append("\n 		   	) ");
		sb
				.append("\n 	) LL2 WHERE D2.MDEV_ID = LL2.MDS_ID AND D2.CHANNEL=3 ");
		if (CommonConstants.DateType.DAILY.getCode().equals(searchDateType)) { // 일별
			sb.append("\n    AND D2.YYYYMM = :startDate    ");
			// sb.append("\n  AND D.YYYYMMDD='20100517' ");
		} else if (CommonConstants.DateType.WEEKLY.getCode().equals(
				searchDateType)
				|| CommonConstants.DateType.MONTHLY.getCode().equals(
						searchDateType)
				|| CommonConstants.DateType.QUARTERLY.getCode().equals(
						searchDateType)) { // 주/월/분기별
			sb.append("\n    AND D2.YYYYMM >= :startDate   ");
			sb.append("\n    AND D2.YYYYMM <= :endDate     ");
		}
		sb.append("\n 	GROUP BY D2.YYYYMM ");
		sb.append("\n ) CO2 ");
		sb.append("\n ON EM.YYYYMM = CO2.YYYYMM");

		SQLQuery query = getSession().createSQLQuery(sb.toString());

		query.setInteger("parentId", locationId);

		if (CommonConstants.DateType.DAILY.getCode().equals(searchDateType)) {
			query.setString("startDate", startDate);
		} else if (CommonConstants.DateType.WEEKLY.getCode().equals(
				searchDateType)
				|| CommonConstants.DateType.MONTHLY.getCode().equals(
						searchDateType)
				|| CommonConstants.DateType.QUARTERLY.getCode().equals(
						searchDateType)) {
			query.setString("startDate", startDate);
			query.setString("endDate", endDate);
		}

		return query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP)
				.list();
	}

	/**
	 * BEMS 설비별 사용량 조회.
	 */
	@Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
	public List<Object> getCompareFacilityMonthData(
			Map<String, Object> condition) {

		
		
		StringBuffer sb = new StringBuffer();
		Boolean convert = (Boolean) condition.get("convert");
		sb.append("\n    SELECT 				");
		sb.append("\n    (SELECT TOTAL FROM MONTH_EM 			");
		sb.append("\n  	 WHERE channel = :channel               ");
		sb.append("\n  	 AND YYYYMM = :lastMonth              	");
		if (convert)
			sb.append("\n  	 AND MODEM_ID = :endDeviceId        ");
		else
			sb.append("\n  	 AND METER_ID = :endDeviceId        ");
		sb.append("\n    ) EM_LASTMONTH,                        ");

		sb.append("\n    (SELECT TOTAL FROM MONTH_EM            ");
		sb.append("\n  	 WHERE channel = :channel               ");
		sb.append("\n  	 AND YYYYMM = :month                  	");
		if (convert)
			sb.append("\n  	 AND MODEM_ID = :endDeviceId        ");
		else
			sb.append("\n  	 AND METER_ID = :endDeviceId        ");
		sb.append("\n    ) EM_MONTH,                            ");

		sb.append("\n    (SELECT SUM(TOTAL) FROM MONTH_EM       ");
		sb.append("\n  	 WHERE channel = :channel               ");
		sb.append("\n  	 AND YYYYMM >= :lastYearStartMonth     	");
		sb.append("\n  	 AND YYYYMM <= :lastYearEndMonth       	");
		if (convert)
			sb.append("\n  	 AND MODEM_ID = :endDeviceId        ");
		else
			sb.append("\n  	 AND METER_ID = :endDeviceId        ");
		sb.append("\n    ) EM_LASTYEAR,                         ");

		sb.append("\n    (SELECT SUM(TOTAL) FROM MONTH_EM         ");
		sb.append("\n  	 WHERE channel = :channel               ");
		sb.append("\n  	 AND YYYYMM >= :yearStartMonth         	");
		sb.append("\n  	 AND YYYYMM <= :yearEndMonth           	");
		if (convert)
			sb.append("\n  	 AND MODEM_ID = :endDeviceId        ");
		else
			sb.append("\n  	 AND METER_ID = :endDeviceId        ");
		sb.append("\n    ) EM_YEAR,                             ");

		sb.append("\n    (SELECT TOTAL FROM MONTH_GM ");
		sb.append("\n  	 WHERE channel = :channel               ");
		sb.append("\n  	 AND YYYYMM = :lastMonth              	");
		if (convert)
			sb.append("\n  	 AND MODEM_ID = :endDeviceId        ");
		else
			sb.append("\n  	 AND METER_ID = :endDeviceId        ");
		sb.append("\n    ) GM_LASTMONTH,                        ");

		sb.append("\n    (SELECT TOTAL FROM MONTH_GM            ");
		sb.append("\n  	 WHERE channel = :channel               ");
		sb.append("\n  	 AND YYYYMM = :month                  	");
		if (convert)
			sb.append("\n  	 AND MODEM_ID = :endDeviceId        ");
		else
			sb.append("\n  	 AND METER_ID = :endDeviceId        ");
		sb.append("\n    ) GM_MONTH,                            ");

		sb.append("\n    (SELECT SUM(TOTAL) FROM MONTH_GM       ");
		sb.append("\n  	 WHERE channel = :channel               ");
		sb.append("\n  	 AND YYYYMM >= :lastYearStartMonth     	");
		sb.append("\n  	 AND YYYYMM <= :lastYearEndMonth       	");
		if (convert)
			sb.append("\n  	 AND MODEM_ID = :endDeviceId        ");
		else
			sb.append("\n  	 AND METER_ID = :endDeviceId        ");
		sb.append("\n    ) GM_LASTYEAR,                         ");

		sb.append("\n    (SELECT SUM(TOTAL) FROM MONTH_GM       ");
		sb.append("\n  	 WHERE channel = :channel               ");
		sb.append("\n  	 AND YYYYMM >= :yearStartMonth          ");
		sb.append("\n  	 AND YYYYMM <= :yearEndMonth            ");
		if (convert)
			sb.append("\n  	 AND MODEM_ID = :endDeviceId        ");
		else
			sb.append("\n  	 AND METER_ID = :endDeviceId        ");
		sb.append("\n    ) GM_YEAR,                             ");

		sb.append("\n    (SELECT TOTAL FROM MONTH_HM ");
		sb.append("\n  	 WHERE channel = :channel               ");
		sb.append("\n  	 AND YYYYMM = :lastMonth                ");
		if (convert)
			sb.append("\n  	 AND MODEM_ID = :endDeviceId        ");
		else
			sb.append("\n  	 AND METER_ID = :endDeviceId        ");
		sb.append("\n    ) HM_LASTMONTH,                        ");

		sb.append("\n    (SELECT TOTAL FROM MONTH_HM            ");
		sb.append("\n  	 WHERE channel = :channel               ");
		sb.append("\n  	 AND YYYYMM = :month                    ");
		if (convert)
			sb.append("\n  	 AND MODEM_ID = :endDeviceId        ");
		else
			sb.append("\n  	 AND METER_ID = :endDeviceId        ");
		sb.append("\n    ) HM_MONTH,                            ");

		sb.append("\n    (SELECT SUM(TOTAL) FROM MONTH_HM       ");
		sb.append("\n  	 WHERE channel = :channel               ");
		sb.append("\n  	 AND YYYYMM >= :lastYearStartMonth      ");
		sb.append("\n  	 AND YYYYMM <= :lastYearEndMonth        ");
		if (convert)
			sb.append("\n  	 AND MODEM_ID = :endDeviceId        ");
		else
			sb.append("\n  	 AND METER_ID = :endDeviceId        ");
		sb.append("\n    ) HM_LASTYEAR,                         ");

		sb.append("\n    (SELECT SUM(TOTAL) FROM MONTH_HM       ");
		sb.append("\n  	 WHERE channel = :channel               ");
		sb.append("\n  	 AND YYYYMM >= :yearStartMonth          ");
		sb.append("\n  	 AND YYYYMM <= :yearEndMonth            ");
		if (convert)
			sb.append("\n  	 AND MODEM_ID = :endDeviceId        ");
		else
			sb.append("\n  	 AND METER_ID = :endDeviceId        ");
		sb.append("\n    ) HM_YEAR,                             ");

		sb.append("\n    (SELECT TOTAL FROM MONTH_WM ");
		sb.append("\n  	 WHERE channel = :channel               ");
		sb.append("\n  	 AND YYYYMM = :lastMonth              ");
		if (convert)
			sb.append("\n  	 AND MODEM_ID = :endDeviceId        ");
		else
			sb.append("\n  	 AND METER_ID = :endDeviceId        ");
		sb.append("\n    ) WM_LASTMONTH,                        ");

		sb.append("\n    (SELECT TOTAL FROM MONTH_WM              ");
		sb.append("\n  	 WHERE channel = :channel               ");
		sb.append("\n  	 AND YYYYMM = :month                  ");
		if (convert)
			sb.append("\n  	 AND MODEM_ID = :endDeviceId        ");
		else
			sb.append("\n  	 AND METER_ID = :endDeviceId        ");
		sb.append("\n    ) WM_MONTH,                            ");

		sb.append("\n    (SELECT SUM(TOTAL) FROM MONTH_WM         ");
		sb.append("\n  	 WHERE channel = :channel               ");
		sb.append("\n  	 AND YYYYMM >= :lastYearStartMonth     ");
		sb.append("\n  	 AND YYYYMM <= :lastYearEndMonth       ");
		if (convert)
			sb.append("\n  	 AND MODEM_ID = :endDeviceId        ");
		else
			sb.append("\n  	 AND METER_ID = :endDeviceId        ");
		sb.append("\n    ) WM_LASTYEAR,                         ");

		sb.append("\n    (SELECT SUM(TOTAL) FROM MONTH_WM         ");
		sb.append("\n  	 WHERE channel = :channel               ");
		sb.append("\n  	 AND YYYYMM >= :yearStartMonth         ");
		sb.append("\n  	 AND YYYYMM <= :yearEndMonth           ");
		if (convert)
			sb.append("\n  	 AND MODEM_ID = :endDeviceId        ");
		else
			sb.append("\n  	 AND METER_ID = :endDeviceId        ");
		sb.append("\n    ) WM_YEAR                             ");

		sb.append("\n    FROM MONTH_EM                            ");
		sb.append("\n    WHERE channel=:channel                  ");
		sb.append("\n  	 AND YYYYMM >= :lastYearStartMonth         ");
		sb.append("\n  	 AND YYYYMM <= :yearEndMonth         ");
		
		if (convert)
			sb.append("\n  	 AND MODEM_ID = :endDeviceId        ");
		else
			sb.append("\n  	 AND METER_ID = :endDeviceId        ");
	

		SQLQuery query = getSession().createSQLQuery(sb.toString());

		query.setString("lastMonth", condition.get("lastMonth").toString());
		query.setString("month", condition.get("month").toString());
		query.setString("channel", "1");
		query.setString("lastYearStartMonth", condition.get(
				"lastYearStartMonth").toString());
		query.setString("lastYearEndMonth", condition.get("lastYearEndMonth")
				.toString());
		query.setString("yearStartMonth", condition.get("yearStartMonth")
				.toString());
		query.setString("yearEndMonth", condition.get("yearEndMonth")
				.toString());
		query.setInteger("endDeviceId", (Integer)condition.get("endDeviceId"));
		return query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP)
				.list();
	}

	@Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
	public List<Map<String, String>> getContractBillingChartData(
			Map<String, String> conditionMap) {

		String startDate = conditionMap.get("startDate").substring(0, 6);
		String endDate = conditionMap.get("endDate").substring(0, 6);
		String contractId = conditionMap.get("contractId");

		StringBuilder sb = new StringBuilder()
				.append(
						" SELECT m.id.yyyymm as DATE, m.total as TOTAL			       ")
				.append(
						"   FROM MonthEM m                       			           ")
				.append(
						"  WHERE m.id.channel = :channel                               ")
				.append(
						"    AND m.contract.serviceTypeCode.code = :serviceTypeCode    ")
				.append(
						"    AND m.contract.status.code = :status                      ")
				.append("    AND m.id.yyyymm >= :startDate   						   ")
				.append("    AND m.id.yyyymm <= :endDate     						   ")
				.append(
						"    AND m.contract.id = :contractId                           ");

		Query query = getSession().createQuery(sb.toString());
		query.setString("channel", DefaultChannel.Usage.getCode() + "");
		query.setString("serviceTypeCode", MeterType.EnergyMeter
				.getServiceType());
		query.setString("status", ContractStatus.NORMAL.getCode());
		query.setString("startDate", startDate);
		query.setString("endDate", endDate);
		query.setString("contractId", contractId);

		return query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP)
				.list();
	}

	@Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
	public List<Object> getSearchChartData(Set<Condition> conditions,
			int locationId, int endDeviceId) {

		String startDate = "";
		String endDate = "";

		StringBuilder sb = new StringBuilder();
		StringBuilder querySb = new StringBuilder();

		Iterator<Condition> cIterator = conditions.iterator();
		int i = 0;
		@SuppressWarnings("unused")
		int arrCnt = conditions.size();

		while (cIterator.hasNext()) {
			String emStr = "";
			String gmStr = "";
			String wmStr = "";
			Condition con = cIterator.next();

			if ("date".equalsIgnoreCase(con.getField())) {
				emStr = emStr
						+ "(SELECT SUM(m.TOTAL) FROM MONTH_EM m WHERE m.yyyymm >= :"
						+ con.getOperator() + "_startDate";
				emStr = emStr + " AND m.yyyymm <= :" + con.getOperator()
						+ "_endDate ";
				gmStr = gmStr
						+ "(SELECT SUM(m.MINVALUE) FROM MONTH_GM m WHERE m.yyyymm >= :"
						+ con.getOperator() + "_startDate";
				gmStr = gmStr + " AND m.yyyymm <= :" + con.getOperator()
						+ "_endDate ";
				wmStr = wmStr
						+ "(SELECT SUM(m.MINVALUE) FROM MONTH_WM m WHERE m.yyyymm >= :"
						+ con.getOperator() + "_startDate";
				wmStr = wmStr + " AND m.yyyymm <= :" + con.getOperator()
						+ "_endDate ";

			}

			if ("startEndDate".equalsIgnoreCase(con.getField())) {

				startDate = con.getValue()[0].toString();
				endDate = con.getValue()[1].toString();

			} else {
				if (locationId != -1) {
					emStr = emStr + " AND m.location_id=:location ";
					emStr = emStr + " ) " + "MAX_" + con.getOperator() + ",";
					gmStr = gmStr + " AND m.location_id=:location ";
					gmStr = gmStr + " ) " + "MIN_" + con.getOperator() + ",";
					wmStr = wmStr + " AND m.location_id=:location ";
					wmStr = wmStr + " ) " + "MIN_" + con.getOperator() + ",";
				}
				if (endDeviceId != -1) {
					emStr = emStr + " AND m.endDevice_id=:endDevice ";
					emStr = emStr + " ) " + "MAX_" + con.getOperator() + ",";
					gmStr = gmStr + " AND m.endDevice_id=:endDevice ";
					gmStr = gmStr + " ) " + "MIN_" + con.getOperator() + ",";
					wmStr = wmStr + " AND m.endDevice_id=:endDevice ";
					wmStr = wmStr + " ) " + "MIN_" + con.getOperator() + ",";
				}
			}

			sb.append(emStr + gmStr + wmStr);

			i++;

		}

		querySb.append("   SELECT ");
		querySb.append(sb.substring(0, sb.lastIndexOf(",")));

		querySb.append(" from MONTH_EM m where m.yyyymm >=:startDate ");
		querySb.append(" and m.yyyymm <=:endDate ");

		if (locationId != -1) {
			querySb.append(" and m.location_id =:location ");
		}

		if (endDeviceId != -1) {
			querySb.append(" and m.endDevice_id =:endDevice ");
		}
		SQLQuery query = getSession().createSQLQuery(querySb.toString());

		query.setString("startDate", startDate);
		query.setString("endDate", endDate);

		if (locationId != -1) {
			query.setInteger("location", locationId);
		}

		if (endDeviceId != -1) {
			query.setInteger("endDevice", endDeviceId);
		}

		Iterator<Condition> cqIterator = conditions.iterator();
		while (cqIterator.hasNext()) {
			Condition con = cqIterator.next();

			if ("date".equalsIgnoreCase(con.getField())) {
				query.setString(con.getOperator() + "_startDate", con
						.getValue()[0].toString());
				query.setString(con.getOperator() + "_endDate",
						con.getValue()[1].toString());

			}
		}

		return query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP)
				.list();
	}

	@Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
	public List<Object> getMonthToYears() {

		Criteria criteria = getSession().createCriteria(MonthEM.class);
		criteria.setProjection(Projections.groupProperty("id.yyyymm"));
		criteria.addOrder(Order.asc("id.yyyymm"));

		List<Object> resultTemp = criteria.list();
		List<Object> result = new ArrayList<Object>();

		String yyyy = "";
		for (int i = 0; i < resultTemp.size(); i++) {

			String yyyymm = resultTemp.get(i).toString();
			if (yyyymm.indexOf(yyyy) < 0 || "".equals(yyyy)) {

				yyyy = yyyymm.substring(0, 4);
				result.add(yyyy);

			}
		}

		return result;
	}
	
	@Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
	public List<Object> getEnergySavingReportMonthlyData(String[] years, int channel, Integer[] meterIds) {
		
		StringBuffer sb = new StringBuffer();
		sb.append("select SUBSTR(yyyymm, 5, 2) as MM, sum(total) as TOTAL from month_em ")
			.append("where SUBSTR(yyyymm, 1, 4) in ('" + years[0] + "'");
		for(int i=1 ; i < years.length ; i++ ) {
			sb.append(", '" + years[i] + "'");
		}
		sb.append( ") ");
		
		if(meterIds.length > 0) {
			sb.append("and meter_id in (" + meterIds[0]);
			for(int i=1 ; i < meterIds.length ; i++ ) {
				sb.append(", " + meterIds[i]);
			}
			sb.append( ") ");
		}
			
		sb.append("and channel = :channel ")
			.append("group by SUBSTR(yyyymm, 5,2) ")
			.append("order by SUBSTR(yyyymm, 5,2) asc");

		SQLQuery query = getSession().createSQLQuery(new SQLWrapper().getQuery(sb.toString()));
		query.setInteger("channel", channel);
		
		return query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
	}
	
	@Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
	public List<Object> getEnergySavingReportYearlyData(String[] years, int channel, Integer[] meterIds) {
		
		StringBuffer sb = new StringBuffer();
		sb.append("select sum(total) as TOTAL from month_em ")
			.append("where SUBSTR(yyyymm, 1, 4) in ('" + years[0] + "'");
		for(int i=1 ; i < years.length ; i++ ) {
			sb.append(", '" + years[i] + "'");
		}
		sb.append( ") ");
		
		if(meterIds.length > 0) {
			sb.append("and meter_id in (" + meterIds[0]);
			for(int i=1 ; i < meterIds.length ; i++ ) {
				sb.append(", " + meterIds[i]);
			}
			sb.append( ") ");
		}
			
		sb.append("and channel = :channel ");

		SQLQuery query = getSession().createSQLQuery(new SQLWrapper().getQuery(sb.toString()));
		query.setInteger("channel", channel);
		
		return query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
	}
	
	/**
	 * method name : getMonthEMbySupplierId
	 * method Desc : 해당 공급사의 고객에 대한 MonthEM정보만 가져오는 조건
	 * 
	 * @param params
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
	public List<MonthEM> getMonthEMbySupplierId(Map<String,Object> params){

		String yyyymm = (String)params.get("yyyymm");  
		Integer channel = (Integer)params.get("channel");
		String mdevId = (String)params.get("mdevId");
		DeviceType mdevType = (DeviceType)params.get("mdevType");  
		int supplierId = (Integer)params.get("supplierId");
		
		StringBuffer sb = new StringBuffer();

		sb.append("\n  ");
		sb.append("\n FROM MonthEM m");
		sb.append("\n WHERE m.id.mdevId = :mdevId");
		sb.append("\n AND m.id.mdevType = :mdevType");
		sb.append("\n AND m.id.yyyymm = :yyyymm");
		sb.append("\n AND m.id.channel = :channel");
		sb.append("\n AND m.contract.customer.supplier.id = :supplierId");
		
		
		Query query = getSession().createQuery(new SQLWrapper().getQuery(sb.toString()));
		query.setString("mdevId", mdevId);
		query.setString("mdevType", mdevType.toString());
		query.setString("yyyymm", yyyymm);
		query.setInteger("channel", channel);
		query.setInteger("supplierId", supplierId);
		
		return query.list();
	}

	/* (non-Javadoc)
	 * @see com.aimir.dao.mvm.MonthEMDao#getMonthEMs(com.aimir.model.mvm.MonthEM)
	 */
	@Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
	public List<MonthEM> getMonthEMs(MonthEM monthEM) {
		
		Criteria criteria = getSession().createCriteria(MonthEM.class);
		
		if (monthEM != null) {
			
			if (monthEM.getContract() != null) {
				
				if (monthEM.getContract().getId() != null) {
					
					criteria.add(Restrictions.eq("contract.id", monthEM.getContract().getId()));
				} 
			}
			
			if (monthEM.getChannel() != null) {
				
				criteria.add(Restrictions.eq("id.channel", monthEM.getChannel()));
			}
			
			if (monthEM.getYyyymm() != null) {
				
				if ( 6 == monthEM.getYyyymm().length() ) {
					
					criteria.add(Restrictions.eq("id.yyyymm", monthEM.getYyyymm()));
				} else if ( 4 == monthEM.getYyyymm().length() ) {

					criteria.add(Restrictions.like("id.yyyymm", monthEM.getYyyymm() + "%"));
					criteria.addOrder( Order.asc("id.yyyymm") );
				}
			}
			
			if (monthEM.getMDevType() != null) {
				
				criteria.add(Restrictions.eq("id.mdevType", monthEM.getMDevType()));
				
				if ( monthEM.getMDevType().name().equals(DeviceType.EndDevice.name()) ) {
					
					criteria.addOrder( Order.asc("enddevice.id") );
				} else if ( monthEM.getMDevType().name().equals(DeviceType.Modem.name()) ) {
					
					criteria.addOrder( Order.asc("modem.id") );
				}
			}
			
			if (monthEM.getEnddevice() != null) {
				
				if ( monthEM.getEnddevice().getId() != null ) {
					
					criteria.add(Restrictions.eq("enddevice.id", monthEM.getEnddevice().getId()));
				}
			}
		}

		return criteria.list();
	}


	/* (non-Javadoc)
	 * @see com.aimir.dao.mvm.MonthEMDao#getSumMonthEMs(com.aimir.model.mvm.MonthEM)
	 */
	@Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
	public List<Map<String, Object>> getSumMonthEMs(MonthEM monthEM) {

		Criteria criteria = getSession().createCriteria(MonthEM.class);
		
		if (monthEM != null) {
			
			if (monthEM.getContract() != null) {
				
				if (monthEM.getContract().getId() != null) {
					
					criteria.add(Restrictions.eq("contract.id", monthEM.getContract().getId()));
				} 
			}
			
			if (monthEM.getChannel() != null) {
				
				criteria.add(Restrictions.eq("id.channel", monthEM.getChannel()));
			}
			
			if (monthEM.getYyyymm() != null) {
				
				if ( 6 == monthEM.getYyyymm().length() ) {
				
					criteria.add(Restrictions.eq("id.yyyymm", monthEM.getYyyymm()));
				} else if ( 4 == monthEM.getYyyymm().length() ) {

					criteria.add(Restrictions.like("id.yyyymm", monthEM.getYyyymm() + "%"));
				}
			}
			
			if (monthEM.getMDevType() != null) {
				
				criteria.add(Restrictions.eq("id.mdevType", monthEM.getMDevType()));
			}
		}

		ProjectionList projectionList = Projections.projectionList();
		
		projectionList.add( Projections.sum("total"), "total" );
		
		if ( monthEM.getMDevType().name().equals(DeviceType.EndDevice.name()) ) {
			
			projectionList.add(Projections.groupProperty("enddevice"), "enddevice" );
			criteria.addOrder( Order.asc("enddevice.id") );
		} else if ( monthEM.getMDevType().name().equals(DeviceType.Modem.name()) ) {
			
			projectionList.add(Projections.groupProperty("modem"), "modem" );
			criteria.addOrder( Order.asc("modem.id") );
		}
		
		criteria.setProjection( projectionList );
		
		List<Map<String, Object>> result = criteria.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
		
		return result;
	}

	/* (non-Javadoc)
	 * @see com.aimir.dao.mvm.MonthEMDao#getDeviceSpecificGrid(java.lang.String, int)
	 */
	@Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
	public List<Map<String, Object>> getDeviceSpecificGrid(String basicDay, int contractId) {
		
		String sqlStr = " "
//			+ "\n SELECT  B.friendlyName       AS name "
//			+ "\n       , sum(A.total)         AS usage "
//			+ "\n       , B.drLevel            AS levelDR "
//			+ "\n       , B.drProgramMandatory AS programDR "
//			+ "\n       , 0                    AS co2 "
//			+ "\n   FROM  MonthEM   A "
//			+ "\n       , EndDevice B "
//			+ "\n   LEFT "
//			+ "\n  OUTER "
//			+ "\n   JOIN B.categoryCode C "
//			+ "\n  WHERE  A.id.channel = :usageChannel "
//			+ "\n    AND  A.id.yyyymm like :basicDay "
//			+ "\n    AND  A.contract.id = :contractId "
//			+ "\n    AND  'Y' = CASE WHEN A.id.mdevType = :endDevice "
//			+ "\n                    THEN CASE WHEN A.enddevice.id = B.id "
//			+ "\n                              THEN 'Y' "
//			+ "\n                              ELSE 'N' END "
//			+ "\n                    WHEN A.id.mdevType = :modem "
//			+ "\n                    THEN CASE WHEN A.modem.id = B.modem.id "
//			+ "\n                               AND C.code = :normal "
//			+ "\n                              THEN 'Y' "
//			+ "\n                              ELSE 'N' END "
//			+ "\n                    ELSE 'N' END "
//			+ "\n   GROUP BY B.friendlyName, B.drLevel, B.drProgramMandatory "
//			+ "\n   ORDER BY sum(A.total) DESC ";
			+ "\n SELECT  B.FRIENDLY_NAME       AS NAME "
			+ "\n       , SUM(A.USAGE)          AS USAGE "
			+ "\n       , C.DRNAME              AS LEVELDR "
			+ "\n       , B.DRPROGRAMMANDATORY  AS PROGRAMDR "
			+ "\n       , SUM(A.CO2)            AS CO2 "
			+ "\n   FROM  ( "
			+ "\n         SELECT  A.TOTAL         AS USAGE "
			+ "\n               , B.TOTAL         AS CO2 "
			+ "\n               , A.MDEV_TYPE     AS MDEV_TYPE "
			+ "\n               , A.MODEM_ID      AS MODEM_ID "
			+ "\n               , A.ENDDEVICE_ID  AS ENDDEVICE_ID "
			+ "\n           FROM  MONTH_EM A "
			+ "\n           LEFT "
			+ "\n          OUTER "
			+ "\n           JOIN  MONTH_EM B "
			+ "\n             ON  A.YYYYMM = B.YYYYMM "
			+ "\n            AND  A.MDEV_ID = B.MDEV_ID "
			+ "\n            AND  A.CONTRACT_ID = B.CONTRACT_ID "
			+ "\n            AND  B.CHANNEL = :co2Channel "
			+ "\n          WHERE  A.YYYYMM LIKE :basicDay "
			+ "\n            AND  A.CHANNEL = :usageChannel "
			+ "\n            AND  A.CONTRACT_ID = :contractId "
			+ "\n         ) A "
			+ "\n       , ( "
			+ "\n         SELECT  A.MODEM_ID            AS ID "
			+ "\n               , B.FRIENDLY_NAME       AS FRIENDLY_NAME "
			+ "\n               , A.CATEGORY_ID         AS CATEGORY_ID "
			+ "\n               , A.DRLEVEL             AS DRLEVEL "
			+ "\n               , A.DRPROGRAMMANDATORY  AS DRPROGRAMMANDATORY "
			+ "\n           FROM  ( "
			+ "\n                 SELECT  A.MODEM_ID "
			+ "\n                       , A.CATEGORY_ID "
			+ "\n                       , A.DRLEVEL "
			+ "\n                       , A.DRPROGRAMMANDATORY "
			+ "\n                   FROM  ENDDEVICE A "
			+ "\n                       , CODE C "
			+ "\n                  WHERE  A.CATEGORY_ID = C.ID "
			+ "\n                    AND  C.CODE = :smartConcent "
			+ "\n                 )  A "
			+ "\n           LEFT "
			+ "\n          OUTER "
			+ "\n           JOIN  ( "
			+ "\n                 SELECT  A.MODEM_ID "
			+ "\n                       , A.FRIENDLY_NAME "
			+ "\n                   FROM  ENDDEVICE A "
			+ "\n                       , CODE C "
			+ "\n                  WHERE  A.CATEGORY_ID = C.ID "
			+ "\n                    AND  C.CODE = :generalAppliance "
			+ "\n                 ) B "
			+ "\n             ON  A.MODEM_ID = B.MODEM_ID "
			+ "\n          UNION "
			+ "\n         SELECT  C.ID                  AS ID "
			+ "\n               , C.FRIENDLY_NAME       AS FRIENDLY_NAME "
			+ "\n               , C.CATEGORY_ID         AS CATEGORY_ID "
			+ "\n               , C.DRLEVEL             AS DRLEVEL "
			+ "\n               , C.DRPROGRAMMANDATORY  AS DRPROGRAMMANDATORY "
			+ "\n           FROM  ENDDEVICE C "
			+ "\n               , CODE D "
			+ "\n          WHERE  C.CATEGORY_ID = D.ID "
			+ "\n            AND  D.CODE = :smartAppliance "
			+ "\n         ) B "
			+ "\n   LEFT "
			+ "\n  OUTER "
			+ "\n   JOIN  HOME_DEVICE_DRLEVEL C "
			+ "\n     ON  B.DRLEVEL = C.DRLEVEL "
			+ "\n    AND  B.CATEGORY_ID = C.CATEGORY_ID "
			+ "\n  WHERE  'Y' = CASE WHEN A.MDEV_TYPE = :endDevice "
			+ "\n                    THEN CASE WHEN A.ENDDEVICE_ID = B.ID "
			+ "\n                              THEN 'Y' "
			+ "\n                              ELSE 'N' END "
			+ "\n                    WHEN A.MDEV_TYPE = :modem "
			+ "\n                    THEN CASE WHEN A.MODEM_ID = B.ID "
			+ "\n                              THEN 'Y' "
			+ "\n                              ELSE 'N' END "
			+ "\n                    ELSE 'N' END "
			+ "\n  GROUP BY B.FRIENDLY_NAME, C.DRNAME, B.DRPROGRAMMANDATORY "
			+ "\n  ORDER BY SUM(A.USAGE) ";

//		Query query = getSession().createQuery(sqlStr);
		SQLQuery query = getSession().createSQLQuery(sqlStr);
		
		query.setInteger("contractId", contractId);
		query.setInteger("co2Channel", DefaultChannel.Co2.getCode());
		query.setInteger("usageChannel", DefaultChannel.Usage.getCode());
		query.setString("basicDay", basicDay + "%");
		query.setString("endDevice", DeviceType.EndDevice.name());
		query.setString("modem", DeviceType.Modem.name());
		query.setString("smartConcent", HomeDeviceCategoryType.SMART_CONCENT.getCode());
		query.setString("generalAppliance", HomeDeviceCategoryType.GENERAL_APPLIANCE.getCode());
		query.setString("smartAppliance", HomeDeviceCategoryType.SMART_APPLIANCE.getCode());
//		query.setString("normal", HomeDeviceCategoryType.GENERAL_APPLIANCE.getCode());
		
		return query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
	}
	
	/**
     * 날자 타입 검색별로 수 검침결과를 반환
     * 
	 * @param condition
	 * @param dateType
	 * @return
     */
    @Override
    @Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
    public List<Object> getConsumptionEmCo2ManualMonitoring(Map<String, Object> condition, DateType dateType) {
    	if(DateType.MONTHLY.equals(dateType)) {
    		return getConsumptionEmCo2MonthlyManualMonitoring(condition);
    	}
    	else if(DateType.SEASONAL.equals(dateType)){
    		return getConsumptionEmCo2SeasonalManualMonitoring(condition);
    	}
    	else {
    		return new ArrayList<Object>();
    	}
    }
	
	/**
	 * 수검침 월간 조회
	 * 기존 소스를 거의 그대로 Copy 함.
	 * 
	 * XXX: 추후 리팩토링이 필요하다.
	 * 
	 * @param condition
	 * @return
	 * @author Yi Hanghee(javarouka@gmail.com, javarouka@nuritelecom.co.kr)
	 */
	private List<Object> getConsumptionEmCo2MonthlyManualMonitoring(Map<String, Object> condition) {
		Integer meterId = (Integer) condition.get("meterId");
		String startDate = ObjectUtils.defaultIfNull(condition.get("startDate"), "").toString();
		if (startDate.length() > 6) startDate = startDate.substring(0, 6);
		String endDate = ObjectUtils.defaultIfNull(condition.get("endDate"), "").toString();
		if (endDate.length() > 6) endDate = endDate.substring(0, 6);

		StringBuffer sb = new StringBuffer();

		sb.append("\n SELECT EM.YYYYMM AS YYYYMM, EM.TOTAL AS EM_TOTAL, CO2.TOTAL AS CO2_TOTAL  FROM (  ");
		sb.append("\n 	SELECT D.YYYYMM , SUM(D.TOTAL) AS TOTAL  ");
		sb.append("\n 	FROM MONTH_EM D , ( ");
		sb.append("\n 		SELECT M.MDS_ID , L.ID , L.NAME , L.PARENT_ID FROM METER M , LOCATION L WHERE M.LOCATION_ID = L.ID AND M.ID = :meterId ");
		sb.append("\n 	) LL WHERE D.MDEV_ID = LL.MDS_ID AND D.CHANNEL=1 ");
		if(!startDate.isEmpty() && !endDate.isEmpty()) {
			sb.append("\n    AND D.YYYYMM >= :startDate   ");
			sb.append("\n    AND D.YYYYMM <= :endDate     ");
		}
		sb.append("\n 	GROUP BY D.YYYYMM ");
		sb.append("\n ) EM ");
		sb.append("\n LEFT JOIN ");
		sb.append("\n (  ");
		sb.append("\n 	SELECT D2.YYYYMM , SUM(D2.TOTAL) AS TOTAL ");
		sb.append("\n 	FROM MONTH_EM D2 , ( ");
		sb.append("\n 		SELECT M2.MDS_ID , L2.ID , L2.NAME , L2.PARENT_ID FROM METER M2 , LOCATION L2 WHERE M2.LOCATION_ID = L2.ID AND M2.ID = :meterId ");
		sb.append("\n 	) LL2 WHERE D2.MDEV_ID = LL2.MDS_ID AND D2.CHANNEL=0 ");
		if(!startDate.isEmpty() && !endDate.isEmpty()) {
			sb.append("\n    AND D2.YYYYMM >= :startDate   ");
			sb.append("\n    AND D2.YYYYMM <= :endDate     ");
		}
		sb.append("\n 	GROUP BY D2.YYYYMM ");
		sb.append("\n ) CO2 ");
		sb.append("\n ON EM.YYYYMM = CO2.YYYYMM");

		SQLQuery query = getSession().createSQLQuery(sb.toString());
		query.setInteger("meterId", meterId);

		if(!startDate.isEmpty() && !endDate.isEmpty()) {
			query.setString("startDate", startDate);
			query.setString("endDate", endDate);
		}

		return query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
	}
	
	/**
	 * 수검침 시즌별 조회
	 * 기존 소스를 거의 그대로 Copy 함.
	 * 
	 * XXX: 추후 리팩토링이 필요하다.
	 * 
	 * @param condition
	 * @return
	 * @author Yi Hanghee(javarouka@gmail.com, javarouka@nuritelecom.co.kr)
	 */
	private List<Object> getConsumptionEmCo2SeasonalManualMonitoring(Map<String, Object> condition) {
		Integer meterId = (Integer) condition.get("meterId");
		String startDate = ObjectUtils.defaultIfNull(condition.get("startDate"), "").toString();
		if (startDate.length() > 6) startDate = startDate.substring(0, 6);
		String endDate = ObjectUtils.defaultIfNull(condition.get("endDate"), "").toString();
		if (endDate.length() > 6) endDate = endDate.substring(0, 6);

		StringBuffer sb = new StringBuffer();

		sb.append("\n SELECT EM.YYYYMM AS YYYYMM , EM.TOTAL AS EM_TOTAL ,  CO2.TOTAL AS CO2_TOTAL  FROM (  ");
		sb.append("\n 	SELECT D.YYYYMM , SUM(D.TOTAL) AS TOTAL  ");
		sb.append("\n 	FROM MONTH_EM D , ( ");
		sb.append("\n 		SELECT M.MDS_ID , L.ID , L.NAME , L.PARENT_ID FROM METER M , LOCATION L WHERE M.LOCATION_ID = L.ID AND M.ID = :meterId ");
		sb.append("\n 	)LL WHERE D.MDEV_ID = LL.MDS_ID AND D.CHANNEL=1 ");
		if(!startDate.isEmpty() && !endDate.isEmpty()) {
			sb.append("\n    AND D.YYYYMM >= :startDate   ");
			sb.append("\n    AND D.YYYYMM <= :endDate     ");
		}
		sb.append("\n 	GROUP BY D.YYYYMM ");
		sb.append("\n ) EM ");
		sb.append("\n LEFT JOIN ");
		sb.append("\n (  ");
		sb.append("\n 	SELECT D2.YYYYMM , SUM(D2.TOTAL) AS TOTAL ");
		sb.append("\n 	FROM MONTH_EM D2 , ( ");
		sb.append("\n 		SELECT M2.MDS_ID , L2.ID , L2.NAME , L2.PARENT_ID FROM METER M2 , LOCATION L2 WHERE M2.LOCATION_ID = L2.ID AND M2.ID = :meterId ");
		sb.append("\n 	) LL2 WHERE D2.MDEV_ID = LL2.MDS_ID AND D2.CHANNEL=0 ");
		if(!startDate.isEmpty() && !endDate.isEmpty()) {
			sb.append("\n    AND D2.YYYYMM >= :startDate   ");
			sb.append("\n    AND D2.YYYYMM <= :endDate     ");
		}
		sb.append("\n 	GROUP BY D2.YYYYMM ");
		sb.append("\n ) CO2 ");
		sb.append("\n ON EM.YYYYMM = CO2.YYYYMM");

		SQLQuery query = getSession().createSQLQuery(sb.toString());

		query.setInteger("meterId", meterId);

		if(!startDate.isEmpty() && !endDate.isEmpty()) {
			query.setString("startDate", startDate);
			query.setString("endDate", endDate);
		}

		return query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
	}
	
	@Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
	public List<Object> getConsumptionEmValueSum(int supplierId, String startDate, String endDate, int startday, int endday) {

		Boolean plus= true;
		logger.debug("startDate : " + startDate+", endDate : "+ endDate+", startDAY : "+ startday+", endDAY : "+ endday);
		StringBuffer sb = new StringBuffer();
		
		sb.append("\n    SELECT ");
	
		for(int hh=startday; hh<endday+1 ;hh++ ){
			sb.append("SUM(");
			sb.append("VALUE_" + (hh<10? "0":"")+hh+")");
			if(hh == endday){
				plus = false;
			}	
			if(plus){
				sb.append("+");
			}
		
		}
		sb.append("\n  AS VALUE_SUM ");
		sb.append("\n  FROM MONTH_EM");
		sb.append("\n  WHERE");
		if(startDate != null && endDate != null) {
			sb.append("\n YYYYMM BETWEEN :startDate AND :endDate");
		}
		sb.append("\n 	AND CHANNEL = :channel ");
        sb.append("\n   AND MDEV_TYPE = :mdevType ");
        sb.append("\n	AND SUPPLIER_ID = :supplierid ");
		
		SQLQuery query = getSession().createSQLQuery(sb.toString());

		if(startDate != null && startDate != null) {
			query.setString("startDate", startDate);
			query.setString("endDate", endDate);
		}
		
        query.setInteger("channel", ElectricityChannel.Usage.getChannel());
        query.setString("mdevType", DeviceType.Meter.name());
        query.setInteger("supplierid", supplierId);
		logger.debug("sql string : "+ query.toString());
		
		return query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();

	}
	
	/**
	 * method name : getMonthByMinDate
	 * method Desc : 해당 미터의 최초 MonthEM 값
	 * 
	 * @param mdevId
	 * 
	 */
	@Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
    public List<Map<String, Object>> getMonthByMinDate(String mdevId) {

		SQLQuery query = null;
		try{
			StringBuilder sb = new StringBuilder();
			sb.append("\n SELECT  	yyyymm as YYYYMM, mdev_id as MDEVID");
			sb.append("\n FROM 		month_em ");
			sb.append("\n WHERE 	yyyymm = (SELECT min(yyyymm) " +
					"						FROM month_em  " +
					"						WHERE channel = :channel AND mdev_id = :mdevId	)");
			sb.append("\n AND 		channel = :channel ");
			sb.append("\n AND 		mdev_id = :mdevId ");
			
            query = getSession().createSQLQuery(sb.toString());
			query.setString("mdevId", mdevId);
			query.setInteger("channel", ElectricityChannel.Usage.getChannel());

		}catch(Exception e){
			e.printStackTrace();
		}
		return query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
	}
	
	/**
	 * @MethodName getMonthlyUsageByContract
	 * @Date 2013. 10. 28.
	 * @param contract
	 * @param yyyymm
	 * @param channels
	 * @return
	 * @Modified
	 * @Description 특정 contract, 채널에 대하여 월간 사용이력을 구한다.
	 */
	@Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
	public List<MonthEM> getMonthlyUsageByContract(Contract contract, String yyyymm, String channels) {
		String SEPARATOR = ",";
		Query query = null;
		
		StringBuilder queryStr = new StringBuilder();
		queryStr.append("\n FROM MonthEM");
		queryStr.append("\n WHERE id.yyyymm = :yyyymm");
		queryStr.append("\n AND contract.id = :contractId");
		queryStr.append("\n AND id.channel in (:channels)");

		String[] cha = channels.split(SEPARATOR);
		List<Integer> list = new ArrayList<Integer>();
		
		for ( String ch : cha ) {
			list.add(Integer.parseInt(ch));
		}
		
		query = getSession().createQuery(queryStr.toString());
		query.setString("yyyymm", yyyymm);
		query.setInteger("contractId", contract.getId());
		query.setParameterList("channels", list.toArray());
		return query.list();
	}
	
	@Transactional(readOnly=true, propagation=Propagation.REQUIRED)
	public List<MonthEM> getMonthEMByMeter(Meter meter, String yyyymm, Integer... channels) {
		logger.debug("\n meter: " + meter.getMdsId() +
				"\n yyyymm: " + yyyymm +
				"\n channels: " + Arrays.toString(channels));
		
		Query query = null;
		StringBuilder queryStr = new StringBuilder();
		queryStr.append("\n FROM MonthEM em");
		queryStr.append("\n WHERE em.id.yyyymm = :yyyymm ");
		queryStr.append("\n AND em.id.mdevId = :meter ");
		queryStr.append("\n AND em.id.mdevType = :mdevType ");
		queryStr.append("\n AND em.id.channel in (:channels)");
		
		query = getSession().createQuery(queryStr.toString());
		query.setString("yyyymm", yyyymm);
		query.setString("meter", meter.getMdsId());
		query.setString("mdevType", CommonConstants.DeviceType.Meter.name());
		query.setParameterList("channels", channels);	
		return query.list();
	}
	
	@Transactional(readOnly=true, propagation=Propagation.REQUIRED)
	public Map<String, Object> getMonthlyAccumulatedUsageByMeter(Meter meter, String yyyymm, Integer... channels) {
		Map<String, Object> result = new HashMap<String, Object>();
		logger.debug("meter: \n" + meter.getMdsId());
		logger.debug("yyyymm: \n" + yyyymm);
		logger.debug("channels: \n" + Arrays.toString(channels));
		String end = CalendarUtil.getDate(yyyymm + "01", Calendar.MONTH, 1).substring(0, 6);
		StringBuilder queryString = new StringBuilder();
		queryString.append("\n SELECT ");
		
		for ( int i = 1 ; i <= 31 ; i++) {
			queryString.append("\n em.value_")
			.append(CalendarUtil.to2Digit(i))
			.append(" as value").append(CalendarUtil.to2Digit(i)).append(",");
		}
		
		queryString.append("\n em.writeDate as writeDate");
		queryString.append("\n FROM MonthEM em ");
		queryString.append("\n WHERE em.id.yyyymm = :yyyymm ");		
		queryString.append("\n AND em.id.mdevId = :meter ");
		queryString.append("\n AND em.id.mdevType = :mdevType ");
		queryString.append("\n AND em.id.channel in (:channels) ");
		
		Query query = getSession().createQuery(queryString.toString());
		query.setString("yyyymm", yyyymm);
		query.setString("meter", meter.getMdsId());
		query.setString("mdevType", CommonConstants.DeviceType.Meter.name());
		query.setParameterList("channels", channels);
		
		List<Map<String, Object>> startEMList = query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
		
		Double prevAccUsage = new Double(0d);
		for ( Map<String, Object> em : startEMList ) {
			for ( int i = 0 ; i < 31 ; i++ ) {
				String property = "value";
				property += CalendarUtil.to2Digit(i);
				if ( em.get(property) != null ) {
					prevAccUsage += (Double) em.get(property);
					break;
				}
			}
		}
		
		query.setString("yyyymm", end);	
		List<Map<String, Object>> endEMList = query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
		
		String writeDate = "";
		Double postAccUsage = new Double(0d);
		for ( Map<String, Object> em : endEMList ) {
			String property = "value01";
			if ( em.get(property) !=null ) {
				postAccUsage += (Double) em.get(property);
				writeDate = StringUtil.nullToBlank(em.get("writeDate"));
				break;
			}
		}
		
		if ( postAccUsage == 0d ) {
			for ( Map<String, Object> em : startEMList ) {
				for ( int i = 31 ; i > 0 ; i-- ) {
					String property = "value";
					property += CalendarUtil.to2Digit(i);
					if ( em.get(property) != null ) {
						postAccUsage += (Double) em.get(property);
						writeDate = StringUtil.nullToBlank(em.get("writeDate"));
						break;
					}
				}
			}			
		}
		
		writeDate = writeDate.equals("")? "" : writeDate.substring(0, 8);
		Double accUsage = postAccUsage - prevAccUsage;
		result.put("accUsage", accUsage);
		result.put("writeDate", writeDate);
		return result;
	}

	@Override
	@Transactional(propagation=Propagation.REQUIRES_NEW)
	public void oldLPDelete(String mdsId, String bDate) {
        StringBuilder hqlBuf = new StringBuilder();
        hqlBuf.append("DELETE FROM MonthEM");
        hqlBuf.append(" WHERE id.yyyymm <= ? ");
        hqlBuf.append(" AND id.mdevId = ? ");
        
        Query query = getSession().createQuery(hqlBuf.toString());
        query.setParameter(1, bDate);
        query.setParameter(2, mdsId);
        query.executeUpdate();
        // bulkUpdate 때문에 주석처리
        /*this.getSession().bulkUpdate(hqlBuf.toString(),
            new Object[] {bDate, mdsId} );*/
	}
}
