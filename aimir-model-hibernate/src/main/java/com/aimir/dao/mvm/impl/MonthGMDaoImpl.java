package com.aimir.dao.mvm.impl;

import java.util.ArrayList;
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
import com.aimir.constants.CommonConstants.MeterType;
import com.aimir.dao.AbstractHibernateGenericDao;
import com.aimir.dao.mvm.MonthGMDao;
import com.aimir.model.mvm.MonthGM;
import com.aimir.util.Condition;
import com.aimir.util.SQLWrapper;
import com.aimir.util.SearchCondition;

@Repository(value = "monthgmDao")
public class MonthGMDaoImpl extends AbstractHibernateGenericDao<MonthGM, Integer> implements MonthGMDao {

	private static Log logger = LogFactory.getLog(MonthGMDaoImpl.class);
    
	@Autowired
	protected MonthGMDaoImpl(SessionFactory sessionFactory) {
		super(MonthGM.class);
		super.setSessionFactory(sessionFactory);
	}
    
    @SuppressWarnings("unchecked")
	public List<Object> getMonthGMsMaxMinAvgSum(Set<Condition> conditions, String div) {

		Criteria criteria = getSession().createCriteria(MonthGM.class);
		ProjectionList pjl = Projections.projectionList();
		if(conditions != null) {                                
            Iterator<Condition> it = conditions.iterator();
            while(it.hasNext()){
                Condition condition = (Condition)it.next();
                Criterion addCriterion = SearchCondition.getCriterion(condition);
                
                if(addCriterion != null){
                    criteria.add(addCriterion);
                }
            }
        }
		
		if("max".equals(div)) {
			pjl.add(Projections.max("total"));
		}
		else if("min".equals(div)) {
			pjl.add(Projections.min("total"));
		}
		else if("avg".equals(div)) {
			pjl.add(Projections.avg("total"));
		}
		else if("sum".equals(div)) {
			pjl.add(Projections.sum("total"));
		}
		else if ("minDate".equals(div)) {
			pjl.add(Projections.min("id.yyyymm"));
		}
		criteria.setProjection(pjl);
		return criteria.list();
	}
    
	@Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
    public List<Object> getMonthGMsCount(Set<Condition> conditions,
            String div) {

        
        DetachedCriteria  criteriaSub = DetachedCriteria.forClass(MonthGM.class);
        Criteria criteria = getSession().createCriteria(MonthGM.class);

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
    
    public List<MonthGM> getMonthGMsByListCondition(Set<Condition> set) {         
        
        return findByConditions(set);
    }
    
    public List<Object> getMonthGMsCountByListCondition(Set<Condition> set) {         
        
        return findTotalCountByConditions(set);
    }

    @SuppressWarnings("unchecked")
	public List<Object[]> getMonthBillingChartData(Map<String, String> conditionMap) {

		String startDate = conditionMap.get("startDate").substring(0, 6);
		String endDate = conditionMap.get("endDate").substring(0, 6);
		String locationCondition = conditionMap.get("locationCondition");
		String searchDateType = conditionMap.get("searchDateType");
		int supplierId = Integer.parseInt(conditionMap.get("supplierId").toString());
		
		StringBuilder sb = new StringBuilder()
		.append(" SELECT sum(m.total), m.location.name 				           ")
		.append("   FROM MonthGM m                       			           ")
		.append("  WHERE m.id.channel = :channel                               ")
		.append("    AND m.contract.serviceTypeCode.code = :serviceTypeCode ")
		.append("    AND m.contract.status.code = :status                   ")
		.append("    AND m.contract.customer.supplierId = :supplierId       ");
				
		if("4".equals(searchDateType)) {                // 월별	
			sb.append("    AND m.id.yyyymm = :startDate    ");
		} else if ("7".equals(searchDateType)) {        // 분기별
			sb.append("    AND m.id.yyyymm >= :startDate   ");
			sb.append("    AND m.id.yyyymm <= :endDate     ");
		}
		if(!"".equals(locationCondition)) {			
			sb.append("   AND m.location.id in (" + locationCondition + ")");
		}		
		
		sb.append("  GROUP BY m.location.name               ");

		Query query = getSession().createQuery(sb.toString());
		query.setString("channel", DefaultChannel.Usage.getCode() + "");
		query.setString("serviceTypeCode", MeterType.GasMeter.getServiceType());
		query.setString("status", ContractStatus.NORMAL.getCode());
		query.setInteger("supplierId", supplierId);
		
		if("4".equals(searchDateType)) { 
			query.setString("startDate", startDate);
		} else if ("7".equals(searchDateType)) { 
			query.setString("startDate", startDate);
			query.setString("endDate", endDate);
		}		
		
		return query.list();
	}

	@SuppressWarnings("unchecked")
	public List<Object[]> getMonthBillingGridData(Map<String, String> conditionMap) {

		String startDate = conditionMap.get("startDate").substring(0, 6);
		String endDate = conditionMap.get("endDate").substring(0, 6);
		String locationCondition = conditionMap.get("locationCondition");
		String searchDateType = conditionMap.get("searchDateType");
		int supplierId = Integer.parseInt(conditionMap.get("supplierId")); 
		
		StringBuilder sb = new StringBuilder()
		.append(" SELECT sum(m.total), count(*),                ")
		.append("        sum(m.baseValue+m.value_01+m.value_02+m.value_03+m.value_04+m.value_05+m.value_06+m.value_07+m.value_08+")
		.append("            m.value_09+m.value_10+m.value_11+m.value_12+m.value_13+m.value_14+m.value_15+m.value_16+m.value_17+m.value_18+")
		.append("            m.value_19+m.value_20+m.value_21+m.value_22+m.value_23+m.value_24+m.value_25+m.value_26+m.value_27+m.value_28+")
		.append("            m.value_29+m.value_30+m.value_31), ")
		.append("         max(m.value_01),max(m.value_02),max(m.value_03),max(m.value_04),max(m.value_05),max(m.value_06), ")
		.append("         max(m.value_07),max(m.value_08),max(m.value_09),max(m.value_10),max(m.value_11),max(m.value_12),max(m.value_13), ")
		.append("         max(m.value_14),max(m.value_15),max(m.value_16),max(m.value_17),max(m.value_18),max(m.value_19),max(m.value_20), ")
		.append("         max(m.value_21),max(m.value_22),max(m.value_23),max(m.value_24),max(m.value_25),max(m.value_26),max(m.value_27), ")
		.append("         max(m.value_28),max(m.value_29),max(m.value_30),max(m.value_31) ")
		.append("   FROM MonthGM m                                          ")
		.append("  WHERE m.id.channel = :channel                            ")
		.append("    AND m.contract.serviceTypeCode.code = :serviceTypeCode ")
		.append("    AND m.contract.status.code = :status                   ")
		.append("    AND m.contract.customer.supplier.id = :supplierId      ");
		
		if("4".equals(searchDateType)) {                // 일별	
			sb.append("    AND m.id.yyyymm = :startDate    ");
		} else if ("7".equals(searchDateType)) {        // 월별
			sb.append("    AND m.id.yyyymm >= :startDate   ");
			sb.append("    AND m.id.yyyymm <= :endDate     ");
		}
		if(!"".equals(locationCondition)) {			
			sb.append("   AND m.location.id in (" + locationCondition + ")");
		}		

		Query query = getSession().createQuery(sb.toString());
		query.setString("channel", DefaultChannel.Usage.getCode() + "");
		query.setString("serviceTypeCode", MeterType.GasMeter.getServiceType());
		query.setString("status", ContractStatus.NORMAL.getCode());
		query.setInteger("supplierId", supplierId);
		
		if("4".equals(searchDateType)) { 
			query.setString("startDate", startDate);
		} else if ("7".equals(searchDateType)) { 
			query.setString("startDate", startDate);
			query.setString("endDate", endDate);
		}		
		
		return query.list();
	}

	@SuppressWarnings("unchecked")
	public List<MonthGM> getMonthCustomerBillingGridData(Map<String, Object> conditionMap) {

		String startDate = ((String)conditionMap.get("startDate")).substring(0, 6);
		String endDate = ((String)conditionMap.get("endDate")).substring(0, 6);
		String searchDateType = (String)conditionMap.get("searchDateType");
		String locationCondition = (String)conditionMap.get("locationCondition");
		String tariffIndex = (String)conditionMap.get("tariffIndex");
		String customerName = (String)conditionMap.get("customerName");
		String contractNo = (String)conditionMap.get("contractNo");
		String meterName = (String)conditionMap.get("meterName");
		int supplierId = Integer.parseInt(conditionMap.get("supplierId").toString());
		
		int page = Integer.parseInt((String)conditionMap.get("page"));
		int pageSize = Integer.parseInt((String)conditionMap.get("pageSize"));

		StringBuilder sb = new StringBuilder()
		.append("         from MonthGM m                      				")
		.append("  WHERE m.id.channel = :channel                            ")
		.append("    AND m.contract.serviceTypeCode.code = :serviceTypeCode ")
		.append("    AND m.contract.status.code = :status                   ")
		.append("    AND m.contract.customer.supplier.id = :supplierId                   ");	
		
		if("4".equals(searchDateType)) {                // 일별	
			sb.append("    AND m.id.yyyymm = :startDate    ");
		} else if ("7".equals(searchDateType)) {        // 월별
			sb.append("    AND m.id.yyyymm >= :startDate   ");
			sb.append("    AND m.id.yyyymm <= :endDate     ");
		}		
		if(!"".equals(locationCondition)) {			
			sb.append("   AND m.location.id in (" + locationCondition + ")");
		}
		if(!"".equals(tariffIndex)) {			
			sb.append("   AND m.contract.tariffIndex = :tariffIndex ");
		}
		if(!"".equals(customerName)) {			
			sb.append("   AND m.contract.customer.name = :customerName ");
		}
		if(!"".equals(contractNo)) {			
			sb.append("   AND m.contract.contractNumber = :contractNo ");
		}	
		if(!"".equals(meterName)) {			
			sb.append("   AND m.meter.mdsId = :meterName ");
		}		
		
		Query query = getSession().createQuery(sb.toString());
		query.setString("channel", DefaultChannel.Usage.getCode() + "");
		query.setString("serviceTypeCode", MeterType.GasMeter.getServiceType());
		query.setString("status", ContractStatus.NORMAL.getCode());
		query.setInteger("supplierId", supplierId);
		if("4".equals(searchDateType)) { 
			query.setString("startDate", startDate);
		} else if ("7".equals(searchDateType)) { 
			query.setString("startDate", startDate);
			query.setString("endDate", endDate);
		}		
		
		if(!"".equals(tariffIndex)) {			
			query.setString("tariffIndex", tariffIndex);
		}
		if(!"".equals(customerName)) {			
			query.setString("customerName", customerName);
		}
		if(!"".equals(contractNo)) {			
			query.setString("contractNo", contractNo);
		}	
		if(!"".equals(meterName)) {			
			query.setString("meterName", meterName);
		}	
		
		int firstResult = page * pageSize;
		
		query.setFirstResult(firstResult);		
		query.setMaxResults(pageSize);
			
		return query.list();
	}

	public Long getElecCustomerBillingGridDataCount(Map<String, Object> conditionMap) {
		
		String startDate = (String)conditionMap.get("startDate");
		String endDate = (String)conditionMap.get("endDate");
		String searchDateType = (String)conditionMap.get("searchDateType");
		String locationCondition = (String)conditionMap.get("locationCondition");
		String tariffIndex = (String)conditionMap.get("tariffIndex");
		String customerName = (String)conditionMap.get("customerName");
		String contractNo = (String)conditionMap.get("contractNo");
		String meterName = (String)conditionMap.get("meterName");
		int supplierId = Integer.parseInt(conditionMap.get("supplierId").toString());

		StringBuilder sb = new StringBuilder()
		.append("  SELECT COUNT(*)	                      				    ")
		.append("    FROM MonthGM m                      				    ")
		.append("  WHERE m.id.channel = :channel                            ")
		.append("    AND m.contract.serviceTypeCode.code = :serviceTypeCode ")
		.append("    AND m.contract.status.code = :status                   ")
		.append("    AND m.contract.customer.supplier.id = :supplierId                   ");
		
		if("1".equals(searchDateType)) {                // 일별	
			sb.append("    AND m.id.yyyymm = :startDate    ");
		} else if ("3".equals(searchDateType)) {        // 월별
			sb.append("    AND m.id.yyyymm >= :startDate   ");
			sb.append("    AND m.id.yyyymm <= :endDate     ");
		}		
		if(!"".equals(locationCondition)) {			
			sb.append("   AND m.location.id in (" + locationCondition + ")");
		}
		if(!"".equals(tariffIndex)) {			
			sb.append("   AND m.contract.tariffIndex = :tariffIndex ");
		}
		if(!"".equals(customerName)) {			
			sb.append("   AND m.contract.customer.name = :customerName ");
		}
		if(!"".equals(contractNo)) {			
			sb.append("   AND m.contract.contractNumber = :contractNo ");
		}	
		if(!"".equals(meterName)) {			
			sb.append("   AND m.meter.mdsId = :meterName ");
		}		
		
		Query query = getSession().createQuery(sb.toString());
		//criteria.setProjection(Projections.rowCount());
		
		query.setString("channel", DefaultChannel.Usage.getCode() + "");
		query.setString("serviceTypeCode", MeterType.GasMeter.getServiceType());
		query.setString("status", ContractStatus.NORMAL.getCode());
		query.setInteger("supplierId", supplierId);
		if("1".equals(searchDateType)) { 
			query.setString("startDate", startDate);
		} else if ("3".equals(searchDateType)) { 
			query.setString("startDate", startDate);
			query.setString("endDate", endDate);
		}		
		
		if(!"".equals(tariffIndex)) {			
			query.setString("tariffIndex", tariffIndex);
		}
		if(!"".equals(customerName)) {			
			query.setString("customerName", customerName);
		}
		if(!"".equals(contractNo)) {			
			query.setString("contractNo", contractNo);
		}	
		if(!"".equals(meterName)) {			
			query.setString("meterName", meterName);
		}	
		
		return ((Number) query.uniqueResult()).longValue();
	}    
	
	/*
	 * BEMS 주기에 따른 빌딩의 TOTAL 사용량 , TOTAL 탄소배출량 차트 데이터 조회.
	 */
	public List<Object> getConsumptionGmCo2MonthSearchDayTypeTotal2(
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
	@SuppressWarnings("unchecked")
    public List<Object> getConsumptionGmCo2MonthSearchDayTypeTotal(Map<String, Object> condition) {

		logger.info("BEMS 주기에 따른 빌딩의  TOTAL 사용량 , TOTAL 탄소배출량  차트 데이터 조회.\n==== conditions ====\n" + condition);

		String searchDateType = ObjectUtils.defaultIfNull(condition.get("searchDateType"), CommonConstants.DateType.DAILY.getCode() ).toString(); // 일 , 주 , 월 , 분기
		Integer locationId = (Integer) condition.get("locationId");
		
		String startDateT = ObjectUtils.defaultIfNull(condition.get("startDate"), "").toString();
		String startDate = "";
		if (startDateT.length() > 6) {
			startDate = startDateT.substring(0, 6);
		} else {
			startDate = startDateT;
		}
		String endDateT = ObjectUtils.defaultIfNull(condition.get("endDate"),"").toString();
		String endDate = "";
		if (endDateT.length() > 6) {
			endDate = endDateT.substring(0, 6);
		} else {

			endDate = endDateT;
		}

		StringBuffer sb = new StringBuffer();

		sb.append("\n SELECT GM.SUPPLIER_ID , GM.TOTAL , CO2.CO2_TOTAL FROM (  ");
		sb.append("\n 	SELECT LL.SUPPLIER_ID , SUM(D.TOTAL) TOTAL FROM MONTH_GM D , ( ");
		sb.append("\n 		SELECT M.MDS_ID , L.ID , L.NAME , L.SUPPLIER_ID FROM METER M , (  ");
		sb.append("\n 			SELECT ID , NAME , SUPPLIER_ID FROM LOCATION WHERE PARENT_ID = :parentId ");
		sb.append("\n 		) L WHERE M.LOCATION_ID = L.ID ");
//		sb.append("\n 		   AND M.METERTYPE_ID = (  ");
//		sb.append("\n 		   		SELECT CC.id FROM code AS CC WHERE CC.parent_id = ( ");
//		sb.append("\n 		   			SELECT C.id FROM code AS C WHERE C.CODE = '1.3.1' "); // MeterType
//		sb.append("\n 		   		) AND CC.CODE = '1.3.1.3' "); // GasMeter
//		sb.append("\n 		   	) ");
		sb.append("\n 	) LL WHERE D.MDEV_ID = LL.MDS_ID AND D.CHANNEL=1 ");
		if (CommonConstants.DateType.WEEKLY.getCode().equals(searchDateType) || CommonConstants.DateType.MONTHLY.getCode().equals(searchDateType)
				|| CommonConstants.DateType.QUARTERLY.getCode().equals(searchDateType)) { // 주/월/분기별
			sb.append("    AND D.YYYYMM >= :startDate   ");
			sb.append("    AND D.YYYYMM <= :endDate     ");
		}
		sb.append("\n 	GROUP BY LL.SUPPLIER_ID  ");
		sb.append("\n ) GM ");
		sb.append("\n LEFT JOIN ");
		sb.append("\n (  ");
		sb.append("\n 	SELECT LL2.SUPPLIER_ID , SUM(D2.TOTAL) AS CO2_TOTAL FROM MONTH_GM D2 , ( ");
		sb.append("\n 		SELECT M2.MDS_ID , L2.ID , L2.NAME , L2.SUPPLIER_ID FROM METER M2 , (  ");
		sb.append("\n 			SELECT ID , NAME , SUPPLIER_ID FROM LOCATION WHERE PARENT_ID = :parentId ");
		sb.append("\n 		) L2 WHERE M2.LOCATION_ID = L2.ID ");
//		sb.append("\n 		   AND M2.METERTYPE_ID = (  ");
//		sb.append("\n 		   		SELECT CC2.id FROM code AS CC2 WHERE CC2.parent_id = ( ");
//		sb.append("\n 		   			SELECT C2.id FROM code AS C2 WHERE C2.CODE = '1.3.1' "); // MeterType
//		sb.append("\n 		   		) AND CC2.CODE = '1.3.1.3' "); // GasMeter
//		sb.append("\n 		   	) ");
		sb.append("\n 	) LL2 WHERE D2.MDEV_ID = LL2.MDS_ID AND D2.CHANNEL=0 ");
		if (CommonConstants.DateType.WEEKLY.getCode().equals(searchDateType) || CommonConstants.DateType.MONTHLY.getCode().equals(searchDateType)
				|| CommonConstants.DateType.QUARTERLY.getCode().equals(searchDateType)) { // 주/월/분기별
			sb.append("    AND D2.YYYYMM >= :startDate   ");
			sb.append("    AND D2.YYYYMM <= :endDate     ");
		}
		sb.append("\n 	GROUP BY LL2.SUPPLIER_ID  ");
		sb.append("\n ) CO2 ");
		sb.append("\n ON GM.SUPPLIER_ID = CO2.SUPPLIER_ID ");

		SQLQuery query = getSession().createSQLQuery(sb.toString());

		query.setInteger("parentId", locationId );

		if (CommonConstants.DateType.WEEKLY.getCode().equals(searchDateType) || CommonConstants.DateType.MONTHLY.getCode().equals(searchDateType) || CommonConstants.DateType.QUARTERLY.getCode().equals(searchDateType)) {
			query.setString("startDate", startDate);
			query.setString("endDate", endDate);
		}

		return query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
	}
	
	/**
	 * BEMS 동별 월단위 가스사용량 , 탄소배출량 차트 데이터 조회.
	 * 기준 데이터 키로  Location 테이블의 Id를 사용.
	 */
	@SuppressWarnings("unchecked")
    public List<Object> getConsumptionGmCo2MonthMonitoringLocationId(Map<String, Object> condition) {
		
		logger.info("BEMS 동별   월단위 가스사용량 , 탄소배출량  차트 데이터 조회.\n==== conditions ====\n" + condition);
		
		String searchDateType = ObjectUtils.defaultIfNull(	condition.get("searchDateType"), CommonConstants.DateType.DAILY.getCode() ).toString(); // 일 , 주 , 월 , 분기
		@SuppressWarnings("unused")
        Integer supplierId = (Integer)	condition.get("supplierId");
		Integer locationId = (Integer) condition.get("locationId");
        @SuppressWarnings("unused")
		String channelType = (String) condition.get("channelType"); // 탄소일 경우만 0 , 가스/온도/습도의  사용량일때는 1
		
		String startDateT = ObjectUtils.defaultIfNull(	condition.get("startDate"), "").toString();
		String startDate = "";
		if (startDateT.length() > 6) {
			startDate = startDateT.substring(0, 6);
		} else {
			startDate = startDateT;
		}
		
		String endDateT = ObjectUtils.defaultIfNull(condition.get("endDate"),"").toString();
		String endDate = "";
		if (endDateT.length() > 6) {
			endDate = endDateT.substring(0, 6);
		} else {
			
			endDate = endDateT;
		}
		
		StringBuffer sb = new StringBuffer();
		
		sb.append("\n SELECT GM.LOCATION_ID , GM.NAME , GM.MDS_ID , GM.TOTAL , CO2.CO2_TOTAL FROM (  ");
		sb.append("\n 	SELECT LL.ID AS LOCATION_ID , LL.NAME , LL.MDS_ID , D.CHANNEL , SUM(D.TOTAL) TOTAL FROM MONTH_GM D , ( ");
		sb.append("\n 		SELECT M.MDS_ID , L.ID , L.NAME FROM METER M , LOCATION L ");
		sb.append("\n 		 WHERE M.LOCATION_ID = L.ID ");
		sb.append("\n 		   AND L.ID = :locationId ");
//		sb.append("\n 		   AND M.METERTYPE_ID = (  ");
//		sb.append("\n 		   		SELECT CC.id FROM code AS CC WHERE CC.parent_id = ( ");
//		sb.append("\n 		   			SELECT C.id FROM code AS C WHERE C.CODE = '1.3.1' "); // MeterType
//		sb.append("\n 		   		) AND CC.CODE = '1.3.1.3' "); // GasMeter
//		sb.append("\n 		   	) ");
		sb.append("\n 	) LL WHERE D.MDEV_ID = LL.MDS_ID AND D.CHANNEL=1 ");
		if ( CommonConstants.DateType.QUARTERLY.getCode().equals(searchDateType)) { // 주/월/분기별
			sb.append("    AND D.YYYYMM >= :startDate   ");
			sb.append("    AND D.YYYYMM <= :endDate     ");
		}else{
			sb.append("    AND D.YYYYMM = :endDate     ");
		}
		sb.append("\n 	GROUP BY LL.ID , LL.NAME , LL.MDS_ID , D.CHANNEL , D.MDEV_ID  ");
		sb.append("\n ) GM ");
		sb.append("\n LEFT JOIN ");
		sb.append("\n (  ");
		sb.append("\n 	SELECT LL2.ID AS LOCATION_ID , SUM(D2.TOTAL) AS CO2_TOTAL FROM MONTH_GM D2 , ( ");
		sb.append("\n 		SELECT M2.MDS_ID , L2.ID , L2.NAME FROM METER AS M2 , LOCATION L2 ");
		sb.append("\n 		 WHERE M2.LOCATION_ID = L2.ID ");
		sb.append("\n 		   AND L2.ID = :locationId ");
//		sb.append("\n 		   AND M2.METERTYPE_ID = (  ");
//		sb.append("\n 		   		SELECT CC2.id FROM code AS CC2 WHERE CC2.parent_id = ( ");
//		sb.append("\n 		   			SELECT C2.id FROM code AS C2 WHERE C2.CODE = '1.3.1' "); // MeterType
//		sb.append("\n 		   		) AND CC2.CODE = '1.3.1.3' "); // GasMeter
//		sb.append("\n 		   	) ");
		sb.append("\n 	) LL2 WHERE D2.MDEV_ID = LL2.MDS_ID AND D2.CHANNEL=0 ");
		if ( CommonConstants.DateType.QUARTERLY.getCode().equals(searchDateType)) { // 주/월/분기별
			sb.append("    AND D2.YYYYMM >= :startDate   ");
			sb.append("    AND D2.YYYYMM <= :endDate     ");
		}else{
			sb.append("    AND D2.YYYYMM = :endDate     ");
		}
		sb.append("\n 	GROUP BY LL2.ID , D2.MDEV_ID  ");
		sb.append("\n ) CO2 ");
		sb.append("\n ON GM.LOCATION_ID = CO2.LOCATION_ID ");
		
		SQLQuery query = getSession().createSQLQuery(sb.toString());
		
		query.setInteger("parentId", locationId );
		
		if ( CommonConstants.DateType.QUARTERLY.getCode().equals(searchDateType)) {
			query.setString("startDate", startDate);
			query.setString("endDate", endDate);
		}else{
			query.setString("endDate", endDate);
		}
		
		return query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
	}

	/**
	 * BEMS 동별 월단위 가스사용량 , 탄소배출량 차트 데이터 조회.
	 * 기준 데이터 키로  Location 테이블의 PARENT_ID를 사용.
	 */
	@SuppressWarnings("unchecked")
    public List<Object> getConsumptionGmCo2MonthMonitoringParentId(
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
		sb.append("\n 		)L WHERE M.LOCATION_ID = L.ID ");
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
	 * BEMS 가스사용량 , 탄소배출량 차트 데이터 조회. ( 월간/분기 )
	 */
	@SuppressWarnings("unchecked")
    public List<Object> getConsumptionGmCo2MonitoringLocationId(Map<String, Object> condition) {

		logger.info("BEMS 가스사용량 , 탄소배출량  차트  데이터 조회. ( 월간/분기 )\n==== conditions ====\n" + condition);

		String searchDateType = ObjectUtils.defaultIfNull( condition.get("searchDateType") , CommonConstants.DateType.DAILY.getCode() ).toString(); // 일 , 주 , 월 , 분기
        @SuppressWarnings("unused")
		Integer supplierId = (Integer) condition.get("supplierId");
		Integer locationId = (Integer) condition.get("locationId");
		String startDateT = ObjectUtils.defaultIfNull( condition.get("startDate"), "").toString();
		String startDate = "";
		
		
		
		
		if (startDateT.length() > 6) {
			startDate = startDateT.substring(0, 6);
		} else {
			startDate = startDateT;
		}
		String endDateT = ObjectUtils.defaultIfNull(condition.get("endDate"), "").toString();
		String endDate = "";
		if (endDateT.length() > 6) {
			endDate = endDateT.substring(0, 6);
		} else {

			endDate = endDateT;
		}

		StringBuffer sb = new StringBuffer();

		sb.append("\n SELECT GM.YYYYMM AS YYYYMM , GM.TOTAL AS GM_TOTAL ,  CO2.TOTAL AS CO2_TOTAL  FROM (  ");

		sb.append("\n 	SELECT D.YYYYMM , SUM(D.TOTAL) AS TOTAL  ");
		sb.append("\n 	FROM MONTH_GM D , ( ");
		sb.append("\n 		SELECT M.MDS_ID , L.ID , L.NAME , L.PARENT_ID FROM METER M , LOCATION L WHERE M.LOCATION_ID = L.ID AND L.ID = :locationId ");
//		sb.append("\n 		   AND M.METERTYPE_ID = (  ");
//		sb.append("\n 		   		SELECT CC.id FROM code AS CC WHERE CC.parent_id = ( ");
//		sb.append("\n 		   			SELECT C.id FROM code AS C WHERE C.CODE = '1.3.1' "); // MeterType
//		sb.append("\n 		   		) AND CC.CODE = '1.3.1.3' "); // GasMeter
//		sb.append("\n 		   	) ");
		sb.append("\n 	) LL WHERE D.MDEV_ID = LL.MDS_ID AND D.CHANNEL=1 ");
		if (CommonConstants.DateType.DAILY.getCode().equals(searchDateType)) { // 일별
			sb.append("\n    AND D.YYYYMM = :startDate    ");
			// sb.append("\n  AND D.YYYYMMDD='201005' ");
		} else if (CommonConstants.DateType.WEEKLY.getCode().equals(searchDateType) || CommonConstants.DateType.MONTHLY.getCode().equals(searchDateType)
				|| CommonConstants.DateType.QUARTERLY.getCode().equals(searchDateType)) { // 주/월/분기별
			sb.append("\n    AND D.YYYYMM >= :startDate   ");
			sb.append("\n    AND D.YYYYMM <= :endDate     ");
		}
		sb.append("\n 	GROUP BY D.YYYYMM ");
		sb.append("\n ) GM ");
		sb.append("\n LEFT JOIN ");
		sb.append("\n (  ");
		sb.append("\n 	SELECT D2.YYYYMM , SUM(D2.TOTAL) AS TOTAL ");
		sb.append("\n 	FROM MONTH_GM D2 , ( ");
		sb.append("\n 		SELECT M2.MDS_ID , L2.ID , L2.NAME , L2.PARENT_ID FROM METER M2 , LOCATION L2 WHERE M2.LOCATION_ID = L2.ID AND L2.ID = :locationId ");
//		sb.append("\n 		   AND M2.METERTYPE_ID = (  ");
//		sb.append("\n 		   		SELECT CC2.id FROM code AS CC2 WHERE CC2.parent_id = ( ");
//		sb.append("\n 		   			SELECT C2.id FROM code AS C2 WHERE C2.CODE = '1.3.1' "); // MeterType
//		sb.append("\n 		   		) AND CC2.CODE = '1.3.1.3' "); // GasMeter
//		sb.append("\n 		   	) ");
		sb.append("\n 	) LL2 WHERE D2.MDEV_ID = LL2.MDS_ID AND D2.CHANNEL=0 ");
		if (CommonConstants.DateType.DAILY.getCode().equals(searchDateType)) { // 일별
			sb.append("\n    AND D2.YYYYMM = :startDate    ");
			// sb.append("\n  AND D.YYYYMMDD='20100517' ");
		} else if (CommonConstants.DateType.WEEKLY.getCode().equals(searchDateType) || CommonConstants.DateType.MONTHLY.getCode().equals(searchDateType)
				|| CommonConstants.DateType.QUARTERLY.getCode().equals(searchDateType)) { // 주/월/분기별
			sb.append("\n    AND D2.YYYYMM >= :startDate   ");
			sb.append("\n    AND D2.YYYYMM <= :endDate     ");
		}
		sb.append("\n 	GROUP BY D2.YYYYMM ");
		sb.append("\n ) CO2 ");
		sb.append("\n ON GM.YYYYMM = CO2.YYYYMM");

		SQLQuery query = getSession().createSQLQuery(sb.toString());

		query.setInteger("locationId", locationId );

		if (CommonConstants.DateType.DAILY.getCode().equals(searchDateType)) {
			query.setString("startDate", startDate);
		} else if (CommonConstants.DateType.WEEKLY.getCode().equals(searchDateType) || CommonConstants.DateType.MONTHLY.getCode().equals(searchDateType) || CommonConstants.DateType.QUARTERLY.getCode().equals(searchDateType)) {
			query.setString("startDate", startDate);
			query.setString("endDate", endDate);
		}


		return query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
	}
	
	/**
	 * BEMS 가스사용량 , 탄소배출량 차트 데이터 조회. ( 월간/분기 )
	 */
	@SuppressWarnings("unchecked")
    public List<Object> getConsumptionGmCo2MonitoringParentId(Map<String, Object> condition) {
		
		logger.info("BEMS 가스사용량 , 탄소배출량  차트  데이터 조회. ( 월간/분기 )\n==== conditions ====\n" + condition);
		
		String searchDateType = ObjectUtils.defaultIfNull( condition.get("searchDateType") , CommonConstants.DateType.DAILY.getCode() ).toString(); // 일 , 주 , 월 , 분기
        @SuppressWarnings("unused")
		Integer supplierId = (Integer) condition.get("supplierId");
		Integer locationId = (Integer) condition.get("locationId");
		String startDateT = ObjectUtils.defaultIfNull( condition.get("startDate"), "").toString();
		String startDate = "";
		
		
		
		
		if (startDateT.length() > 6) {
			startDate = startDateT.substring(0, 6);
		} else {
			startDate = startDateT;
		}
		String endDateT = ObjectUtils.defaultIfNull(condition.get("endDate"), "").toString();
		String endDate = "";
		if (endDateT.length() > 6) {
			endDate = endDateT.substring(0, 6);
		} else {
			
			endDate = endDateT;
		}
		
		StringBuffer sb = new StringBuffer();
		
		sb.append("\n SELECT GM.YYYYMM AS YYYYMM , GM.TOTAL AS GM_TOTAL ,  CO2.TOTAL AS CO2_TOTAL  FROM (  ");
		
		sb.append("\n 	SELECT D.YYYYMM , SUM(D.TOTAL) AS TOTAL  ");
		sb.append("\n 	FROM MONTH_GM D , ( ");
		sb.append("\n 		SELECT M.MDS_ID , L.ID , L.NAME , L.PARENT_ID FROM METER M , (  ");
		sb.append("\n 			SELECT ID , NAME , PARENT_ID FROM LOCATION WHERE PARENT_ID = :parentId ");
		sb.append("\n 		) L WHERE M.LOCATION_ID = L.ID ");
//		sb.append("\n 		   AND M.METERTYPE_ID = (  ");
//		sb.append("\n 		   		SELECT CC.id FROM code AS CC WHERE CC.parent_id = ( ");
//		sb.append("\n 		   			SELECT C.id FROM code AS C WHERE C.CODE = '1.3.1' "); // MeterType
//		sb.append("\n 		   		) AND CC.CODE = '1.3.1.3' "); // GasMeter
//		sb.append("\n 		   	) ");
		sb.append("\n 	) LL WHERE D.MDEV_ID = LL.MDS_ID AND D.CHANNEL=1 ");
		if (CommonConstants.DateType.DAILY.getCode().equals(searchDateType)) { // 일별
			sb.append("\n    AND D.YYYYMM = :startDate    ");
			// sb.append("\n  AND D.YYYYMMDD='201005' ");
		} else if (CommonConstants.DateType.WEEKLY.getCode().equals(searchDateType) || CommonConstants.DateType.MONTHLY.getCode().equals(searchDateType)
				|| CommonConstants.DateType.QUARTERLY.getCode().equals(searchDateType)) { // 주/월/분기별
			sb.append("\n    AND D.YYYYMM >= :startDate   ");
			sb.append("\n    AND D.YYYYMM <= :endDate     ");
		}
		sb.append("\n 	GROUP BY D.YYYYMM ");
		sb.append("\n ) GM ");
		sb.append("\n LEFT JOIN ");
		sb.append("\n (  ");
		sb.append("\n 	SELECT D2.YYYYMM , SUM(D2.TOTAL) AS TOTAL ");
		sb.append("\n 	FROM MONTH_GM D2 , ( ");
		sb.append("\n 		SELECT M2.MDS_ID , L2.ID , L2.NAME , L2.PARENT_ID FROM METER M2 , (  ");
		sb.append("\n 			SELECT ID , NAME , PARENT_ID FROM LOCATION WHERE PARENT_ID = :parentId ");
		sb.append("\n 		) L2 WHERE M2.LOCATION_ID = L2.ID ");
//		sb.append("\n 		   AND M2.METERTYPE_ID = (  ");
//		sb.append("\n 		   		SELECT CC2.id FROM code AS CC2 WHERE CC2.parent_id = ( ");
//		sb.append("\n 		   			SELECT C2.id FROM code AS C2 WHERE C2.CODE = '1.3.1' "); // MeterType
//		sb.append("\n 		   		) AND CC2.CODE = '1.3.1.3' "); // GasMeter
//		sb.append("\n 		   	) ");
		sb.append("\n 	) LL2 WHERE D2.MDEV_ID = LL2.MDS_ID AND D2.CHANNEL=0 ");
		if (CommonConstants.DateType.DAILY.getCode().equals(searchDateType)) { // 일별
			sb.append("\n    AND D2.YYYYMM = :startDate    ");
			// sb.append("\n  AND D.YYYYMMDD='20100517' ");
		} else if (CommonConstants.DateType.WEEKLY.getCode().equals(searchDateType) || CommonConstants.DateType.MONTHLY.getCode().equals(searchDateType)
				|| CommonConstants.DateType.QUARTERLY.getCode().equals(searchDateType)) { // 주/월/분기별
			sb.append("\n    AND D2.YYYYMM >= :startDate   ");
			sb.append("\n    AND D2.YYYYMM <= :endDate     ");
		}
		sb.append("\n 	GROUP BY D2.YYYYMM ");
		sb.append("\n ) CO2 ");
		sb.append("\n ON GM.YYYYMM = CO2.YYYYMM");
		
		SQLQuery query = getSession().createSQLQuery(sb.toString());
		
		query.setInteger("parentId", locationId );
		
		if (CommonConstants.DateType.DAILY.getCode().equals(searchDateType)) {
			query.setString("startDate", startDate);
		} else if (CommonConstants.DateType.WEEKLY.getCode().equals(searchDateType) || CommonConstants.DateType.MONTHLY.getCode().equals(searchDateType) || CommonConstants.DateType.QUARTERLY.getCode().equals(searchDateType)) {
			query.setString("startDate", startDate);
			query.setString("endDate", endDate);
		}
		
		return query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
	}

	/**
	 * BEMS 온도 습도 차트 데이터 조회. ( 월간/분기 ) , channel : 2 , 3
	 */
	@SuppressWarnings("unchecked")
    public List<Object> getConsumptionTmHmMonitoring(
			Map<String, Object> condition) {

		logger.info("BEMS 온도 습도  차트 데이터 조회. ( 월간/분기 )\n==== conditions ====\n"
				+ condition);

		String searchDateType = ObjectUtils.defaultIfNull(
				condition.get("searchDateType"), CommonConstants.DateType.DAILY.getCode() ).toString(); // 일 , 주 , 월 , 분기
		@SuppressWarnings("unused")
        Integer supplierId = (Integer) condition.get("supplierId");
		Integer locationId = (Integer) condition.get("locationId");
		String startDateT = ObjectUtils.defaultIfNull( condition.get("startDate"), "").toString();
		String startDate = "";
		if (startDateT.length() > 6) {
			startDate = startDateT.substring(0, 6);
		} else {
			startDate = startDateT;
		}
		String endDateT = ObjectUtils.defaultIfNull(condition.get("endDate"), "").toString();
		String endDate = "";
		if (endDateT.length() > 6) {
			endDate = endDateT.substring(0, 6);
		} else {

			endDate = endDateT;
		}

		StringBuffer sb = new StringBuffer();

		sb.append("\n SELECT GM.YYYYMM AS YYYYMM , GM.TOTAL AS GM_TOTAL ,  CO2.TOTAL AS CO2_TOTAL  FROM (  ");

		sb.append("\n 	SELECT D.YYYYMM , SUM(D.TOTAL) AS TOTAL  ");
		sb.append("\n 	FROM MONTH_GM D , ( ");
		sb.append("\n 		SELECT M.MDS_ID , L.ID , L.NAME , L.PARENT_ID FROM METER M , (  ");
		sb.append("\n 			SELECT ID , NAME , PARENT_ID FROM LOCATION WHERE PARENT_ID = :parentId ");
		sb.append("\n 		) L WHERE M.LOCATION_ID = L.ID ");
//		sb.append("\n 		   AND M.METERTYPE_ID = (  ");
//		sb.append("\n 		   		SELECT CC.id FROM code AS CC WHERE CC.parent_id = ( ");
//		sb.append("\n 		   			SELECT C.id FROM code AS C WHERE C.CODE = '1.3.1' "); // MeterType
//		sb.append("\n 		   		) AND CC.CODE = '1.3.1.3' "); // GasMeter
//		sb.append("\n 		   	) ");
		sb.append("\n 	) LL WHERE D.MDEV_ID = LL.MDS_ID AND D.CHANNEL=2 ");
		if (CommonConstants.DateType.DAILY.getCode().equals(searchDateType)) { // 일별
			sb.append("\n    AND D.YYYYMM = :startDate    ");
			// sb.append("\n  AND D.YYYYMMDD='201005' ");
		} else if (CommonConstants.DateType.WEEKLY.getCode().equals(searchDateType) || CommonConstants.DateType.MONTHLY.getCode().equals(searchDateType)
				|| CommonConstants.DateType.QUARTERLY.getCode().equals(searchDateType)) { // 주/월/분기별
			sb.append("\n    AND D.YYYYMM >= :startDate   ");
			sb.append("\n    AND D.YYYYMM <= :endDate     ");
		}
		sb.append("\n 	GROUP BY D.YYYYMM ");
		sb.append("\n ) GM ");
		sb.append("\n LEFT JOIN ");
		sb.append("\n (  ");
		sb.append("\n 	SELECT D2.YYYYMM , SUM(D2.TOTAL) AS TOTAL ");
		sb.append("\n 	FROM MONTH_GM D2 , ( ");
		sb.append("\n 		SELECT M2.MDS_ID , L2.ID , L2.NAME , L2.PARENT_ID FROM METER M2 , (  ");
		sb.append("\n 			SELECT ID , NAME , PARENT_ID FROM LOCATION WHERE PARENT_ID = :parentId ");
		sb.append("\n 		) L2 WHERE M2.LOCATION_ID = L2.ID ");
//		sb.append("\n 		   AND M2.METERTYPE_ID = (  ");
//		sb.append("\n 		   		SELECT CC2.id FROM code AS CC2 WHERE CC2.parent_id = ( ");
//		sb.append("\n 		   			SELECT C2.id FROM code AS C2 WHERE C2.CODE = '1.3.1' "); // MeterType
//		sb.append("\n 		   		) AND CC2.CODE = '1.3.1.3' "); // GasMeter
//		sb.append("\n 		   	) ");
		sb.append("\n 	)LL2 WHERE D2.MDEV_ID = LL2.MDS_ID AND D2.CHANNEL=3 ");
		if (CommonConstants.DateType.DAILY.getCode().equals(searchDateType)) { // 일별
			sb.append("\n    AND D2.YYYYMM = :startDate    ");
			// sb.append("\n  AND D.YYYYMMDD='20100517' ");
		} else if (CommonConstants.DateType.WEEKLY.getCode().equals(searchDateType) || CommonConstants.DateType.MONTHLY.getCode().equals(searchDateType)
				|| CommonConstants.DateType.QUARTERLY.getCode().equals(searchDateType)) { // 주/월/분기별
			sb.append("\n    AND D2.YYYYMM >= :startDate   ");
			sb.append("\n    AND D2.YYYYMM <= :endDate     ");
		}
		sb.append("\n 	GROUP BY D2.YYYYMM ");
		sb.append("\n ) CO2 ");
		sb.append("\n ON GM.YYYYMM = CO2.YYYYMM");

		SQLQuery query = getSession().createSQLQuery(sb.toString());

		query.setInteger("parentId", locationId );

		if (CommonConstants.DateType.DAILY.getCode().equals(searchDateType)) {
			query.setString("startDate", startDate);
		} else if (CommonConstants.DateType.WEEKLY.getCode().equals(searchDateType) || CommonConstants.DateType.MONTHLY.getCode().equals(searchDateType)
				|| CommonConstants.DateType.QUARTERLY.getCode().equals(searchDateType)) {
			query.setString("startDate", startDate);
			query.setString("endDate", endDate);
		}

		return query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
	}


	public List<Map<String, String>> getContractBillingChartData(Map<String, String> conditionMap) {

		return null;
	}

	public List<Object> getCompareFacilityMonthData(
			Map<String, Object> condition) {
		// TODO Auto-generated method stub
		return null;
	}

	public List<Object> getSearchChartData(Set<Condition> conditions,
			String location, String endDevice) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@SuppressWarnings("unchecked")
    public MonthGM getMonthGM(Map<String,Object> params){
		
		String yyyymm = (String)params.get("yyyymm");  
		Integer channel = (Integer)params.get("channel");
		Integer dst = (Integer)params.get("dst");
		DeviceType mdevType = (DeviceType)params.get("mdevType");  
		String mdevId = (String)params.get("mdevId");
		
		Criteria criteria = getSession().createCriteria(MonthGM.class);
		criteria.add(Restrictions.eq("id.mdevId", mdevId));
		criteria.add(Restrictions.eq("id.mdevType", mdevType));
		criteria.add(Restrictions.eq("id.dst", dst));
		criteria.add(Restrictions.eq("id.yyyymm", yyyymm));
		criteria.add(Restrictions.eq("id.channel",channel));
		
		List<MonthGM> list = criteria.list();
		
		return list!=null&&list.size()>0?list.get(0):null;
	}
	
	/**
	 * method name : getMonthGMbySupplierId
	 * method Desc : 해당 공급사의 고객에 대한 MonthGM정보만 가져오는 조건
	 * 
	 * @param params
	 * @return
	 */
	@SuppressWarnings("unchecked")
    public MonthGM getMonthGMbySupplierId(Map<String,Object> params){
		
		String yyyymm = (String)params.get("yyyymm");  
		Integer channel = (Integer)params.get("channel");
		Integer dst = (Integer)params.get("dst");
		DeviceType mdevType = (DeviceType)params.get("mdevType");  
		String mdevId = (String)params.get("mdevId");
		int supplierId = (Integer)params.get("supplierId");
		
		StringBuffer sb = new StringBuffer();

		sb.append("\n  ");
		sb.append("\n FROM MonthGM m");
		sb.append("\n WHERE m.id.mdevId = :mdevId");
		sb.append("\n AND m.id.mdevType = :mdevType");
		sb.append("\n AND m.id.dst = :dst");
		sb.append("\n AND m.id.yyyymm = :yyyymm");
		sb.append("\n AND m.id.channel = :channel");
		sb.append("\n AND m.contract.customer.supplier.id = :supplierId");
		
		
		Query query = getSession().createQuery(new SQLWrapper().getQuery(sb.toString()));
		query.setString("mdevId", mdevId);
		query.setString("mdevType", mdevType.toString());
		query.setInteger("dst", dst);
		query.setString("yyyymm", yyyymm);
		query.setInteger("channel", channel);
		query.setInteger("supplierId", supplierId);
		
		List<MonthGM> list = query.list();
		
		return list!=null&&list.size()>0?list.get(0):null;
	}
	
	public List<Object> getMonthToYears() {

		Criteria criteria = getSession().createCriteria( MonthGM.class );
		criteria.setProjection( Projections.groupProperty( "id.yyyymm" ) ); 
		criteria.addOrder( Order.asc("id.yyyymm" ) ); 

		List<Object> resultTemp = criteria.list();
		List<Object> result = new ArrayList<Object>();
		
		String yyyy = "";
		for( int i=0; i<resultTemp.size(); i++ ){
			
			String yyyymm = resultTemp.get(i).toString();
			if( yyyymm.indexOf( yyyy ) < 0 || "".equals( yyyy) ) {
				
				yyyy = yyyymm.substring( 0 , 4 );
				result.add( yyyy );
				
			}
		}
		
		return result;
	}
	
	public List<Object> getEnergySavingReportMonthlyData(String[] years, int channel, Integer[] meterIds) {
		
		StringBuffer sb = new StringBuffer();
		sb.append("select SUBSTR(yyyymm, 5, 2) as MM, sum(total) as TOTAL from month_gm ")
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
			.append("group by SUBSTR(yyyymm, 5, 2) ")
			.append("order by SUBSTR(yyyymm, 5, 2) asc");

		SQLQuery query = getSession().createSQLQuery(new SQLWrapper().getQuery(sb.toString()));
		query.setInteger("channel", channel);
		
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

	private List<Object> getConsumptionEmCo2SeasonalManualMonitoring(
			Map<String, Object> condition) {
		return getConsumptionEmCo2MonthlyManualMonitoring(condition);
	}

	@SuppressWarnings("unchecked")
	private List<Object> getConsumptionEmCo2MonthlyManualMonitoring(Map<String, Object> condition) {
		Integer meterId = (Integer) condition.get("meterId");
		String startDate = ObjectUtils.defaultIfNull(condition.get("startDate"), "").toString();
		if (startDate.length() > 6) startDate = startDate.substring(0, 6);
		String endDate = ObjectUtils.defaultIfNull(condition.get("endDate"), "").toString();
		if (endDate.length() > 6) endDate = endDate.substring(0, 6);

		StringBuffer sb = new StringBuffer();

		sb.append("\n SELECT GM.YYYYMM AS YYYYMM , GM.TOTAL AS GM_TOTAL ,  CO2.TOTAL AS CO2_TOTAL  FROM (  ");
		sb.append("\n 	SELECT D.YYYYMM , SUM(D.TOTAL) AS TOTAL  ");
		sb.append("\n 	FROM MONTH_GM D , ( ");
		sb.append("\n 		SELECT M.MDS_ID , L.ID , L.NAME , L.PARENT_ID FROM METER M , LOCATION L WHERE M.LOCATION_ID = L.ID AND M.ID = :meterId ");
		sb.append("\n 	) LL WHERE D.MDEV_ID = LL.MDS_ID AND D.CHANNEL=1 ");
		if(!startDate.isEmpty() && !endDate.isEmpty()) {
			sb.append("\n    AND D.YYYYMM >= :startDate   ");
			sb.append("\n    AND D.YYYYMM <= :endDate     ");
		}
		sb.append("\n 	GROUP BY D.YYYYMM ");
		sb.append("\n ) GM ");
		sb.append("\n LEFT JOIN ");
		sb.append("\n (  ");
		sb.append("\n 	SELECT D2.YYYYMM , SUM(D2.TOTAL) AS TOTAL ");
		sb.append("\n 	FROM MONTH_GM D2 , ( ");
		sb.append("\n 		SELECT M2.MDS_ID , L2.ID , L2.NAME , L2.PARENT_ID FROM METER M2 , LOCATION L2 WHERE M2.LOCATION_ID = L2.ID AND M2.ID = :meterId ");
		sb.append("\n 	) LL2 WHERE D2.MDEV_ID = LL2.MDS_ID AND D2.CHANNEL=0 ");
		if(!startDate.isEmpty() && !endDate.isEmpty()) {
			sb.append("\n    AND D2.YYYYMM >= :startDate   ");
			sb.append("\n    AND D2.YYYYMM <= :endDate     ");
		}
		sb.append("\n 	GROUP BY D2.YYYYMM ");
		sb.append("\n ) CO2 ");
		sb.append("\n ON GM.YYYYMM = CO2.YYYYMM");

		SQLQuery query = getSession().createSQLQuery(sb.toString());

		query.setInteger("meterId", meterId);

		if(!startDate.isEmpty() && !endDate.isEmpty()) {
			query.setString("startDate", startDate);
			query.setString("endDate", endDate);
		}

		return query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
	}
	public List<Object> getConsumptionGmValueSum(int supplierId ,String startDate, String endDate, int startday, int endday) {

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
		sb.append("\n  FROM MONTH_GM");
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
//		logger.debug("sql string : "+ query.toString());
		return query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();

	}
}
