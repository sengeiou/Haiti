/**
 * DayHMDaoImpl.java Copyright NuriTelecom Limited 2012
 */

package com.aimir.dao.mvm.impl;

import java.util.ArrayList;
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
import com.aimir.dao.mvm.DayHMDao;
import com.aimir.model.mvm.DayHM;
import com.aimir.model.mvm.DayPk;
import com.aimir.util.Condition;
import com.aimir.util.SQLWrapper;
import com.aimir.util.SearchCondition;
import com.aimir.util.StringUtil;

/**
 * DayHMDaoImpl.java Description 
 *
 * 
 * Date          Version     Author   Description
 * 2012.7. 03.   v1.0       김미선         DayHM 조회 조건 (DayHM)
 *
 */
@Repository(value = "dayhmDao")
@SuppressWarnings("unchecked")
public class DayHMDaoImpl extends AbstractHibernateGenericDao<DayHM, Integer> implements DayHMDao {

	private static Log logger = LogFactory.getLog(DayHMDaoImpl.class);
    
	@Autowired
	protected DayHMDaoImpl(SessionFactory sessionFactory) {
		super(DayHM.class);
		super.setSessionFactory(sessionFactory);
	}
	
    public List<DayHM> getDayHMsByMap(Map map) {
	    
	    Criteria criteria = getSession().createCriteria(DayHM.class);
        criteria.add(Restrictions.allEq(map));

        return criteria.list();
	}
	
   public List<DayHM> getDayHMsByList(List<Map> list) {
       Criteria criteria = getSession().createCriteria(DayHM.class); 
       for(Map map : list){
           if(map.get("type").equals("eq")){
               criteria.add(Restrictions.eq((String) map.get("key"),map.get("value")));
           }else if(map.get("type").equals("like")){
               criteria.add(Restrictions.like((String) map.get("key"),map.get("value")));	               
           }else if(map.get("type").equals("in")){
               criteria.add(Restrictions.in((String) map.get("key"),(Object[]) map.get("value")));                  
           }else if(map.get("type").equals("isNull")){
               criteria.add(Restrictions.isNull((String) map.get("key")));                  
           }else if(map.get("type").equals("isNotNull")){
               criteria.add(Restrictions.isNotNull((String) map.get("key")));                  
           }
       }
       return criteria.list();
    }
   
    public List<DayHM> getDayHMsByListCondition(Set<Condition> list) {         
       
        return findByConditions(list);
    }
    
    public int getTotalGroupByListCondition(Set<Condition> conditions) {
        Criteria criteria = getSession().createCriteria(DayHM.class); 
        if(conditions != null) {                        
            Iterator it = conditions.iterator();
            while(it.hasNext()){
                Condition condition = (Condition)it.next();
                Criterion addCriterion = SearchCondition.getCriterion(condition);
                
                if(addCriterion != null){
                    criteria.add(addCriterion);
                }                                
            }
            
        }
        criteria.setProjection(Projections.projectionList()
                .add( Projections.sum("total") )
                .add( Projections.property("id.meter") )
                .add( Projections.groupProperty("id.contract") )
                .add( Projections.groupProperty("id.meter") ) ); 
                
        return criteria.list().size();
    }
    
    public List<Object> getDayHMsCountByListCondition(Set<Condition> set) {         
        
        return findTotalCountByConditions(set);
    }
    
    public List<Object> getDayHMsMaxMinAvgSum(Set<Condition> conditions, String div) {
		
		Criteria criteria = getSession().createCriteria(DayHM.class);
		
		 if(conditions != null) {                                
	            Iterator it = conditions.iterator();
	            while(it.hasNext()){
	                Condition condition = (Condition)it.next();
	                Criterion addCriterion = SearchCondition.getCriterion(condition);
	                
	                if(addCriterion != null){
	                    criteria.add(addCriterion);
	                }
	            }
	        }
	        
			ProjectionList pjl = Projections.projectionList();
			
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
	        	pjl.add(Projections.min("id.yyyymmdd"));
	        }
			criteria.setProjection(pjl);
			return criteria.list();
		
	}
    
    @Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
    public List<Object> getDayHMsCount(Set<Condition> conditions,
            String div) {

        
        DetachedCriteria  criteriaSub = DetachedCriteria.forClass(DayHM.class);
        Criteria criteria = getSession().createCriteria(DayHM.class);

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
        	pjl.add(Projections.groupProperty("id.yyyymmdd"));
        }
        criteriaSub.setProjection(pjl);
        
        ProjectionList pj2 = Projections.projectionList();
        pj2.add(Projections.countDistinct("id.yyyymmdd"));
        
        criteria.add(Subqueries.propertyIn("id.yyyymmdd", criteriaSub));
        criteria.setProjection(pj2);
        
        return criteria.list();
    	
    }
    
    public List<Object> getDayHMsSumList(Set<Condition> conditions) {
		
		Criteria criteria = getSession().createCriteria(DayHM.class);
		
		 if(conditions != null) {                                
	            Iterator it = conditions.iterator();
	            while(it.hasNext()){
	                Condition condition = (Condition)it.next();
	                Criterion addCriterion = SearchCondition.getCriterion(condition);
	                
	                if(addCriterion != null){
	                    criteria.add(addCriterion);
	                }
	            }
	        }
	        
			criteria.setProjection(Projections.projectionList()
	                .add( Projections.sum("total") )        
	                .add( Projections.groupProperty("id.contract.id") )
	                );
			
			List<Object> result = new ArrayList<Object>();
			HashMap<Object, Object> hm = new HashMap<Object, Object>();
			Iterator it = criteria.list().iterator();
			
			int idx =0;
			
			while (it.hasNext()) {
				Object[] objVal = (Object[]) it.next();
				hm.put(((Number)objVal[1]).intValue(), ((Number)objVal[0]).doubleValue());
				result.add(hm);
				idx++;
			}
			
			return result;
		
	}

	public List<Object[]> getDayBillingChartData(Map<String, String> conditionMap) {

		String startDate = conditionMap.get("startDate");
		String endDate = conditionMap.get("endDate");
		String locationCondition = conditionMap.get("locationCondition");
		String searchDateType = conditionMap.get("searchDateType");
		int supplierId = Integer.parseInt(conditionMap.get("supplierId").toString());
		
		StringBuilder sb = new StringBuilder()
		.append(" SELECT sum(d.total), d.location.name                         ")
		.append("   FROM DayHM d                                               ")
		.append("  WHERE d.id.channel = :channel                               ")
		.append("    AND d.contract.serviceTypeCode.code = :serviceTypeCode    ")
		.append("    AND d.contract.status.code = :status                      ")
		.append("    AND d.contract.customer.supplierId = :supplierId          ");	
		
		if("1".equals(searchDateType)) {                // 일별	
			sb.append("    AND d.id.yyyymmdd = :startDate    ");
		} else if ("3".equals(searchDateType)) {        // 월별
			sb.append("    AND d.id.yyyymmdd >= :startDate   ");
			sb.append("    AND d.id.yyyymmdd <= :endDate     ");
		}
		
		if(!"".equals(locationCondition)) {			
			sb.append("   AND d.location.id in (" + locationCondition + ")");
		}		
		
		sb.append("  GROUP BY d.location.name               ");

		Query query = getSession().createQuery(sb.toString());		
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
		
		return query.list();
	}	
	
	public List<Object[]> getDayBillingGridData(Map<String, String> conditionMap) {
		
		String startDate = conditionMap.get("startDate");
		String endDate = conditionMap.get("endDate");
		String locationCondition = conditionMap.get("locationCondition");
		String searchDateType = conditionMap.get("searchDateType");
		int supplierId = Integer.parseInt(conditionMap.get("supplierId").toString());
		
		StringBuilder sb = new StringBuilder()
		.append(" SELECT sum(d.total), count(*),                ")
		.append("        sum(d.baseValue+d.value_00+d.value_01+d.value_02+d.value_03+d.value_04+d.value_05+d.value_06+d.value_07+d.value_08+")
		.append("            d.value_09+d.value_10+d.value_11+d.value_12+d.value_13+d.value_14+d.value_15+d.value_16+d.value_17+d.value_18+")
		.append("            d.value_19+d.value_20+d.value_21+d.value_22+d.value_23), ")
		.append("         max(d.value_00),max(d.value_01),max(d.value_02),max(d.value_03),max(d.value_04),max(d.value_05),max(d.value_06), ")
		.append("         max(d.value_07),max(d.value_08),max(d.value_09),max(d.value_10),max(d.value_11),max(d.value_12),max(d.value_13), ")
		.append("         max(d.value_14),max(d.value_15),max(d.value_16),max(d.value_17),max(d.value_18),max(d.value_19),max(d.value_20), ")
		.append("         max(d.value_21),max(d.value_22),max(d.value_23)      ")		
		.append("   FROM DayHM d                                               ")
		.append("  WHERE d.id.channel = :channel                               ")
		.append("    AND d.contract.serviceTypeCode.code = :serviceTypeCode ")
		.append("    AND d.contract.status.code = :status                   ")	
		.append("    AND d.contract.customer.supplier.id = :supplierId       ");
		
		if("1".equals(searchDateType)) {                // 일별	
			sb.append("    AND d.id.yyyymmdd = :startDate    ");
		} else if ("3".equals(searchDateType)) {        // 월별
			sb.append("    AND d.id.yyyymmdd >= :startDate   ");
			sb.append("    AND d.id.yyyymmdd <= :endDate     ");
		}
		
		if(!"".equals(locationCondition)) {			
			sb.append("   AND d.location.id in (" + locationCondition + ")");
		}		

		Query query = getSession().createQuery(sb.toString());
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
		
		return query.list();
	}

	public List<DayHM> getDayCustomerBillingGridData(Map<String, Object> conditionMap) {

		String startDate = (String)conditionMap.get("startDate");
		String endDate = (String)conditionMap.get("endDate");
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
		.append("         from DayHM d                      				   ")
		.append("  WHERE d.id.channel = :channel                               ")
		.append("    AND d.contract.serviceTypeCode.code = :serviceTypeCode ")
		.append("    AND d.contract.status.code = :status                   ")
		.append("    AND d.contract.customer.supplier.id = :supplierId      ");	
		
		if("1".equals(searchDateType)) {                // 일별	
			sb.append("    AND d.id.yyyymmdd = :startDate    ");
		} else if ("3".equals(searchDateType)) {        // 월별
			sb.append("    AND d.id.yyyymmdd >= :startDate   ");
			sb.append("    AND d.id.yyyymmdd <= :endDate     ");
		}
		
		if(!"".equals(locationCondition)) {			
			sb.append("   AND d.location.id in (" + locationCondition + ")");
		}
		
		if(!"".equals(tariffIndex)) {			
			sb.append("   AND d.contract.tariffIndex = :tariffIndex ");
		}
		
		if(!"".equals(customerName)) {			
			sb.append("   AND d.contract.customer.name = :customerName ");
		}
		
		if(!"".equals(contractNo)) {			
			sb.append("   AND d.contract.contractNumber = :contractNo ");
		}
		
		if(!"".equals(meterName)) {			
			sb.append("   AND d.meter.mdsId = :meterName ");
		}		
		
		Query query = getSession().createQuery(sb.toString());
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
		.append("  SELECT COUNT(*)	                      				       ")
		.append("    FROM DayHM d                      				           ")
		.append("  WHERE d.id.channel = :channel                               ")
		.append("    AND d.contract.serviceTypeCode.code = :serviceTypeCode ")
		.append("    AND d.contract.status.code = :status                   ")	
		.append("    AND d.contract.customer.supplier.id = :supplierId       ");	
		
		if("1".equals(searchDateType)) {                // 일별	
			sb.append("    AND d.id.yyyymmdd = :startDate    ");
		} else if ("3".equals(searchDateType)) {        // 월별
			sb.append("    AND d.id.yyyymmdd >= :startDate   ");
			sb.append("    AND d.id.yyyymmdd <= :endDate     ");
		}
		
		if(!"".equals(locationCondition)) {			
			sb.append("   AND d.location.id in (" + locationCondition + ")");
		}
		
		if(!"".equals(tariffIndex)) {			
			sb.append("   AND d.contract.tariffIndex = :tariffIndex ");
		}
		
		if(!"".equals(customerName)) {			
			sb.append("   AND d.contract.customer.name = :customerName ");
		}
		
		if(!"".equals(contractNo)) {			
			sb.append("   AND d.contract.contractNumber = :contractNo ");
		}
		
		if(!"".equals(meterName)) {			
			sb.append("   AND d.meter.mdsId = :meterName ");
		}		
		
		Query query = getSession().createQuery(sb.toString());
	
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
	

    /**
     *  BEMS 열량사용량 , 탄소배출량  차트 데이터 조회.
     */
	public List<Object> getConsumptionMonitoringManagement(Map<String, Object> condition) {

        logger.info("BEMS 열량사용량 , 탄소배출량  차트 데이터 조회.\n==== conditions ====\n"+condition);
        
		int supplierId   = (Integer)condition.get("supplierId");


		StringBuffer sb = new StringBuffer();
		sb.append("\n SELECT c.mdsId , b.id , b.name FROM Meter c , (  ");
		sb.append("\n 	SELECT id , name FROM Location WHERE parent.id IN  ");
		sb.append("\n 	( ");
		sb.append("\n 		SELECT a.id FROM Location as a WHERE a.supplier.id = :supplierId AND a.parent.id IS NULL");
		sb.append("\n 	) ");
		sb.append("\n ) b WHERE c.location.id = b.id ");
		
		Query query  = getSession().createQuery(sb.toString());
		query.setInteger("supplierId", supplierId);
		
		return query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
	}
	
	/**
	 *  BEMS 데이터 조회를 위한 parentLocation정보 조회
	 */
	public List<Object> getParentIdManager(Map<String, Object> condition) {
		
		logger.info("BEMS 데이터 조회를 위한 parentLocation정보 조회\n==== conditions ====\n"+condition);

		String supplierId   = (String)condition.get("supplierId");
		
		StringBuffer sb = new StringBuffer();
		sb.append(" SELECT ID FROM LOCATION WHERE SUPPLIER_ID = :supplierId AND PARENT_ID IS NULL "); 
			
		SQLQuery query = getSession().createSQLQuery(sb.toString());	
		query.setInteger("supplierId", Integer.parseInt( supplierId) );
		
		return query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
		
	}

	
	
	
	
	
	/**
	 *  BEMS 데이터 조회를 위한 supplierId를 이용하여 Root Location 정보 조회
	 */
	public List<Object> getRootLocationId(Map<String, Object> condition) {
		
		logger.info("BEMS 데이터 조회를 위한 Root Location 정보 조회\n==== conditions ====\n"+condition);

		String supplierId   = (String)condition.get("supplierId");
		
		StringBuffer sb = new StringBuffer();
		sb.append(" SELECT ID FROM LOCATION WHERE SUPPLIER_ID = :supplierId AND PARENT_ID IS NULL "); 
		
		
		SQLQuery query = getSession().createSQLQuery(sb.toString());
		query.setInteger("supplierId", Integer.parseInt( supplierId) );
		
		return query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
	}


    /**
     *  BEMS 열량사용량 , 탄소배출량  차트 데이터 조회.
     */
	public List<Object> getConsumptionMonitoring(Map<String, Object> condition) {

        logger.info("\n==== conditions ====\n"+condition);

		String searchDateType = ObjectUtils.defaultIfNull( condition.get("searchDateType") , CommonConstants.DateType.DAILY.getCode() ).toString(); // 일 , 주 , 월 , 분기
		@SuppressWarnings("unused")
        Integer supplierId = (Integer) condition.get("supplierId");
		Integer locationId = (Integer) condition.get("locationId");
		String channelType  = "1"; // 탄소일 경우만 0 , 열량/온도/습도의 사용량일때는 1
		String startDate = (String)condition.get("startDate");  
		String endDate   = (String)condition.get("endDate"); 

		StringBuffer sb = new StringBuffer();

		sb.append("\n SELECT LL.ID AS LOCATION_ID , LL.NAME , LL.MDS_ID , D.TOTAL , D.CHANNEL ");
		sb.append("\n   FROM DAY_HM D , ( ");
		sb.append("\n 		SELECT M.MDS_ID , L.ID , L.NAME FROM METER M , ( "); 
		sb.append("\n 			SELECT ID , NAME FROM LOCATION WHERE PARENT_ID = :parentId ");
		sb.append("\n 		)  L WHERE M.LOCATION_ID = L.ID ");
		sb.append("\n 	) LL WHERE D.MDEV_ID = LL.MDS_ID AND D.CHANNEL = :channelType ");
		
		if(CommonConstants.DateType.DAILY.getCode().equals(searchDateType)) {                // 일별	
			sb.append("    AND d.yyyymmdd = :startDate    ");
		} else if (CommonConstants.DateType.WEEKLY.getCode().equals(searchDateType) || CommonConstants.DateType.MONTHLY.getCode().equals(searchDateType) || CommonConstants.DateType.QUARTERLY.getCode().equals(searchDateType)) {        // 주/월/분기별
			sb.append("    AND d.yyyymmdd >= :startDate   ");
			sb.append("    AND d.yyyymmdd <= :endDate     ");
		}
		
		SQLQuery query = getSession().createSQLQuery(sb.toString());
		
		query.setInteger("parentId", locationId );
		query.setInteger("channelType", Integer.parseInt( channelType) );
		
		if(CommonConstants.DateType.DAILY.getCode().equals(searchDateType)) { 
			query.setString("startDate", startDate);
		} else if (CommonConstants.DateType.WEEKLY.getCode().equals(searchDateType) || CommonConstants.DateType.MONTHLY.getCode().equals(searchDateType) || CommonConstants.DateType.QUARTERLY.getCode().equals(searchDateType)) { 
			query.setString("startDate", startDate);
			query.setString("endDate", endDate);
		}		
		
		return query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
		
	}
	
	/**
	 *  BEMS 주기에 따른 빌딩의  TOTAL 사용량 , TOTAL 탄소배출량  차트 데이터 조회.
	 */
	public List<Object> getConsumptionHmCo2SearchDayTypeTotal(Map<String, Object> condition) {
		
		logger.info("BEMS 주기에 따른 빌딩의  TOTAL 사용량 , TOTAL 탄소배출량  차트 데이터 조회.\n==== conditions ====\n"+condition);
		
		String searchDateType = ObjectUtils.defaultIfNull( condition.get("searchDateType") , CommonConstants.DateType.DAILY.getCode() ).toString(); // 일 , 주 , 월 , 분기
		Integer locationId = (Integer) condition.get("locationId");
		String startDate = (String)condition.get("startDate");  
		String endDate   = (String)condition.get("endDate"); 
		
		StringBuffer sb = new StringBuffer();
		
		sb.append("\n SELECT HM.SUPPLIER_ID , HM.TOTAL , CO2.CO2_TOTAL FROM (  ");
		sb.append("\n 	SELECT LL.SUPPLIER_ID , SUM(D.TOTAL) TOTAL FROM DAY_HM D , ( ");
		sb.append("\n 		SELECT M.MDS_ID , L.ID , L.NAME , L.SUPPLIER_ID FROM METER M , (  ");
		sb.append("\n 			SELECT ID , NAME , SUPPLIER_ID FROM LOCATION WHERE PARENT_ID = :parentId ");
		sb.append("\n 		) L WHERE M.LOCATION_ID = L.ID ");
		sb.append("\n 	) LL WHERE D.MDEV_ID = LL.MDS_ID AND D.CHANNEL=1 ");
		
		if(CommonConstants.DateType.DAILY.getCode().equals(searchDateType)) {                // 일별	
			sb.append("    AND D.YYYYMMDD = :startDate    ");
		} else if (CommonConstants.DateType.WEEKLY.getCode().equals(searchDateType) || CommonConstants.DateType.MONTHLY.getCode().equals(searchDateType) || CommonConstants.DateType.QUARTERLY.getCode().equals(searchDateType)) {        // 주/월/분기별
			sb.append("    AND D.YYYYMMDD >= :startDate   ");
			sb.append("    AND D.YYYYMMDD <= :endDate     ");
		}	
		
		sb.append("\n 	GROUP BY LL.SUPPLIER_ID  ");
		sb.append("\n ) HM ");
		sb.append("\n LEFT JOIN ");
		sb.append("\n ( ");
		sb.append("\n 	SELECT LL2.SUPPLIER_ID , SUM(D2.TOTAL) AS CO2_TOTAL FROM DAY_HM D2 , ( ");
		sb.append("\n 		SELECT M2.MDS_ID , L2.ID , L2.NAME , L2.SUPPLIER_ID FROM METER M2 , (  ");
		sb.append("\n 			SELECT ID , NAME , SUPPLIER_ID FROM LOCATION WHERE PARENT_ID = :parentId ");
		sb.append("\n 		)L2 WHERE M2.LOCATION_ID = L2.ID ");
		sb.append("\n 	)LL2 WHERE D2.MDEV_ID = LL2.MDS_ID AND D2.CHANNEL=0 ");
		
		if(CommonConstants.DateType.DAILY.getCode().equals(searchDateType)) {                // 일별	
			sb.append("    AND D2.YYYYMMDD = :startDate    ");
		} else if (CommonConstants.DateType.WEEKLY.getCode().equals(searchDateType) || CommonConstants.DateType.MONTHLY.getCode().equals(searchDateType) || CommonConstants.DateType.QUARTERLY.getCode().equals(searchDateType)) {        // 주/월/분기별
			sb.append("    AND D2.YYYYMMDD >= :startDate   ");
			sb.append("    AND D2.YYYYMMDD <= :endDate     ");
		}
		
		sb.append("\n 	GROUP BY LL2.SUPPLIER_ID  ");
		sb.append("\n ) CO2 ");
		sb.append("\n ON HM.SUPPLIER_ID = CO2.SUPPLIER_ID ");
		
		
		SQLQuery query = getSession().createSQLQuery(sb.toString());
		
		query.setInteger("parentId", locationId );
		
		if(CommonConstants.DateType.DAILY.getCode().equals(searchDateType)) { 
			query.setString("startDate", startDate);
		} else if (CommonConstants.DateType.WEEKLY.getCode().equals(searchDateType) || CommonConstants.DateType.MONTHLY.getCode().equals(searchDateType) || CommonConstants.DateType.QUARTERLY.getCode().equals(searchDateType)) { 
			query.setString("startDate", startDate);
			query.setString("endDate", endDate);
		}		
		return query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
	}

	public List<Object> getConsumptionHmCo2DayMonitoringParentId(
			Map<String, Object> condition) {

		logger
				.info("BEMS 동별   일(시간)수도사용량 , 탄소배출량  차트 데이터 조회.\n==== conditions ====\n"
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
		String meterType = (String) condition.get("meterType"); // 탄소일 경우만 0 ,
																// 수도/온도/습도의
																// 사용량일때는 1
		String startDate = (String) condition.get("startDate");
		String endDate = (String) condition.get("endDate");

		StringBuffer sb = new StringBuffer();

		sb.append("\n 	SELECT LL.ORDERNO , LL.ID AS LOCATION_ID , LL.NAME , LL.MDS_ID , D.CHANNEL , SUM(D.TOTAL) TOTAL FROM "+ meterType + " D , ( ");
		sb.append("\n 		 SELECT M.MDS_ID , L.ID , L.NAME , L.ORDERNO FROM METER M , (  ");
		sb.append("\n 			SELECT ID , NAME , ORDERNO  FROM LOCATION WHERE PARENT_ID = :parentId ");
		sb.append("\n 	) L WHERE M.LOCATION_ID = L.ID ");
		sb.append("\n 	)LL WHERE D.MDEV_ID = LL.MDS_ID AND D.CHANNEL=:channel ");
		
		if (CommonConstants.DateType.DAILY.getCode().equals(searchDateType)) { // 일별
			sb.append("    AND D.YYYYMMDD = :startDate    ");
		} else if (CommonConstants.DateType.WEEKLY.getCode().equals(searchDateType)) { // 주/월/분기별
			sb.append("    AND D.YYYYMMDD >= :startDate   ");
		sb.append("    AND D.YYYYMMDD <= :endDate     ");
		}
		sb.append("\n 	GROUP BY LL.ORDERNO, LL.ID , LL.NAME ,  LL.MDS_ID , D.CHANNEL , D.MDEV_ID   ");
		sb.append("   ORDER BY LL.ORDERNO  ASC, LL.ID DESC");

		SQLQuery query = getSession().createSQLQuery(sb.toString());

		query.setInteger("parentId", locationId);
		query.setInteger("channel", channel);

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
	 *  BEMS 동별   일(시간)열량사용량 , 탄소배출량  차트 데이터 조회.
	 *  기준 데이터 키로  Location 테이블의 Id를 사용.
	 */
	public List<Object> getConsumptionHmCo2DayMonitoringLocationId(Map<String, Object> condition) {
		
		logger.info("BEMS 동별   일(시간)열량사용량 , 탄소배출량  차트 데이터 조회.\n==== conditions ====\n"+condition);
		
		String searchDateType = ObjectUtils.defaultIfNull( condition.get("searchDateType") , CommonConstants.DateType.DAILY.getCode() ).toString(); // 일 , 주 , 월 , 분기
		@SuppressWarnings("unused")
        String supplierId = ObjectUtils.defaultIfNull( condition.get("supplierId") , "0").toString();
		String locationId = ObjectUtils.defaultIfNull( condition.get("locationId") , "0").toString();
		String startDate = (String)condition.get("startDate");  
		String endDate   = (String)condition.get("endDate"); 
		
		
		StringBuffer sb = new StringBuffer();
		
		
		sb.append("\n SELECT HM.LOCATION_ID , HM.NAME , HM.MDS_ID , HM.TOTAL , CO2.CO2_TOTAL FROM (  ");
		sb.append("\n 	SELECT LL.ID AS LOCATION_ID , LL.NAME , LL.MDS_ID , D.CHANNEL , SUM(D.TOTAL) TOTAL FROM DAY_HM D , ( ");
		sb.append("\n 		SELECT M.MDS_ID , L.ID , L.NAME FROM METER M , LOCATION L ");
		sb.append("\n 		 WHERE M.LOCATION_ID = L.ID ");
		sb.append("\n 		   AND L.ID = :locationId ");
		sb.append("\n 	)LL WHERE D.MDEV_ID = LL.MDS_ID AND D.CHANNEL=1 ");
		
		if(CommonConstants.DateType.DAILY.getCode().equals(searchDateType)) {                // 일별	
			sb.append("    AND D.YYYYMMDD = :startDate    ");
		} else if (CommonConstants.DateType.WEEKLY.getCode().equals(searchDateType) || CommonConstants.DateType.MONTHLY.getCode().equals(searchDateType) || CommonConstants.DateType.QUARTERLY.getCode().equals(searchDateType)) {        // 주/월/분기별
			sb.append("    AND D.YYYYMMDD >= :startDate   ");
			sb.append("    AND D.YYYYMMDD <= :endDate     ");
		}	
		
		sb.append("\n 	GROUP BY LL.ID , LL.NAME , LL.MDS_ID , D.CHANNEL , D.MDEV_ID  ");
		sb.append("\n ) HM ");
		sb.append("\n LEFT JOIN ");
		sb.append("\n ( ");
		sb.append("\n 	SELECT LL2.ID AS LOCATION_ID , SUM(D2.TOTAL) AS CO2_TOTAL FROM DAY_HM D2 , ( ");
		sb.append("\n 		SELECT M2.MDS_ID , L2.ID , L2.NAME FROM METER M2 , LOCATION L2 ");
		sb.append("\n 		 WHERE M2.LOCATION_ID = L2.ID ");
		sb.append("\n 		   AND L2.ID = :locationId ");
		sb.append("\n 	)LL2 WHERE D2.MDEV_ID = LL2.MDS_ID AND D2.CHANNEL=0 ");
		
		if(CommonConstants.DateType.DAILY.getCode().equals(searchDateType)) {                // 일별	
			sb.append("    AND D2.YYYYMMDD = :startDate    ");
		} else if (CommonConstants.DateType.WEEKLY.getCode().equals(searchDateType) || CommonConstants.DateType.MONTHLY.getCode().equals(searchDateType) || CommonConstants.DateType.QUARTERLY.getCode().equals(searchDateType)) {        // 주/월/분기별
			sb.append("    AND D2.YYYYMMDD >= :startDate   ");
			sb.append("    AND D2.YYYYMMDD <= :endDate     ");
		}
		
		sb.append("\n 	GROUP BY LL2.ID , D2.MDEV_ID  ");
		sb.append("\n ) CO2 ");
		sb.append("\n ON HM.LOCATION_ID = CO2.LOCATION_ID ");
		
		
		SQLQuery query = getSession().createSQLQuery(sb.toString());
		
		query.setInteger("locationId", Integer.parseInt( locationId) );
		
		if(CommonConstants.DateType.DAILY.getCode().equals(searchDateType)) { 
			query.setString("startDate", startDate);
		} else if (CommonConstants.DateType.WEEKLY.getCode().equals(searchDateType) || CommonConstants.DateType.MONTHLY.getCode().equals(searchDateType) || CommonConstants.DateType.QUARTERLY.getCode().equals(searchDateType)) { 
			query.setString("startDate", startDate);
			query.setString("endDate", endDate);
		}		
		
		
		return query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
	}
	
	
	/**
	 *  BEMS 열량사용량 , 탄소배출량  차트 최대/최소 데이터 조회.
	 *  기준키로 location 테이블의 id를 사용
	 *  일(시간) 빌딩 전체 : 열량사용량/탄솝출량 의 최대 ,최소,합
	 */
	public List<Object> getConsumptionHmCo2MonitoringSumMinMaxLocationId(Map<String, Object> condition) {

		logger.info("일(시간) 빌딩 전체 : 열량사용량/탄솝출량 의 최대 ,최소,합\n==== conditions ====\n"+condition);
		
		String searchDateType = ObjectUtils.defaultIfNull( condition.get("searchDateType") , CommonConstants.DateType.DAILY.getCode() ).toString(); // 일 , 주 , 월 , 분기
		@SuppressWarnings("unused")
        Integer supplierId = (Integer) condition.get("supplierId");
		Integer locationId = (Integer) condition.get("locationId");
		String startDate = (String)condition.get("startDate");  
		@SuppressWarnings("unused")
        String endDate   = (String)condition.get("endDate"); 

		String[] dataType  = {"MIN" , "MAX" , "SUM" };
		
		
		StringBuffer sb = new StringBuffer();
		
		sb.append("\n SELECT HM.* , CO2.* FROM (  ");
		sb.append("\n 	SELECT LL.ID AS HM_PARENT_ID  ");
		sb.append("\n 	     , SUM(D.TOTAL) AS HM_TOTAL  ");
		for(int i=0; i<dataType.length; i++){

			sb.append("\n , " + dataType[i] + "(D.VALUE_00) AS HM_" + dataType[i] + "_00 , " + dataType[i] + "(D.VALUE_01) AS HM_" + dataType[i] + "_01 , " + dataType[i] + "(D.VALUE_02) AS HM_" + dataType[i] + "_02 , " + dataType[i] + "(D.VALUE_03) AS HM_" + dataType[i] + "_03 , " + dataType[i] + "(D.VALUE_04) AS HM_" + dataType[i] + "_04 " );
			sb.append("\n , " + dataType[i] + "(D.VALUE_05) AS HM_" + dataType[i] + "_05 , " + dataType[i] + "(D.VALUE_06) AS HM_" + dataType[i] + "_06 , " + dataType[i] + "(D.VALUE_07) AS HM_" + dataType[i] + "_07 , " + dataType[i] + "(D.VALUE_08) AS HM_" + dataType[i] + "_08 , " + dataType[i] + "(D.VALUE_09) AS HM_" + dataType[i] + "_09 " );
			sb.append("\n , " + dataType[i] + "(D.VALUE_10) AS HM_" + dataType[i] + "_10 , " + dataType[i] + "(D.VALUE_11) AS HM_" + dataType[i] + "_11 , " + dataType[i] + "(D.VALUE_12) AS HM_" + dataType[i] + "_12 , " + dataType[i] + "(D.VALUE_13) AS HM_" + dataType[i] + "_13 , " + dataType[i] + "(D.VALUE_14) AS HM_" + dataType[i] + "_14 " );
			sb.append("\n , " + dataType[i] + "(D.VALUE_15) AS HM_" + dataType[i] + "_15 , " + dataType[i] + "(D.VALUE_16) AS HM_" + dataType[i] + "_16 , " + dataType[i] + "(D.VALUE_17) AS HM_" + dataType[i] + "_17 , " + dataType[i] + "(D.VALUE_18) AS HM_" + dataType[i] + "_18 , " + dataType[i] + "(D.VALUE_19) AS HM_" + dataType[i] + "_19 " );
			sb.append("\n , " + dataType[i] + "(D.VALUE_20) AS HM_" + dataType[i] + "_20 , " + dataType[i] + "(D.VALUE_21) AS HM_" + dataType[i] + "_21 , " + dataType[i] + "(D.VALUE_22) AS HM_" + dataType[i] + "_22 , " + dataType[i] + "(D.VALUE_23) AS HM_" + dataType[i] + "_23 " );		
		}
	
		sb.append("\n 	FROM DAY_HM D , ( ");
		sb.append("\n 		SELECT M.MDS_ID , L.ID , L.NAME , L.PARENT_ID FROM METER M , LOCATION L WHERE M.LOCATION_ID = L.ID AND L.ID = :locationId ");
		sb.append("\n 	)LL WHERE D.MDEV_ID = LL.MDS_ID AND D.CHANNEL=1 ");
		
		if(CommonConstants.DateType.DAILY.getCode().equals(searchDateType)) {                // 일별	
			sb.append("\n    AND D.YYYYMMDD = :startDate    ");
		}
		
		sb.append("\n 	GROUP BY LL.ID ");
		sb.append("\n ) HM ");
		sb.append("\n LEFT JOIN ");
		sb.append("\n (  ");
		sb.append("\n 	SELECT LL2.ID AS CO2_PARENT_ID   ");
		sb.append("\n 	     , SUM(D2.TOTAL) AS CO2_TOTAL  ");
		
		for(int i=0; i<dataType.length; i++){

			sb.append("\n , " + dataType[i] + "(D2.VALUE_00) AS CO2_" + dataType[i] + "_00 , " + dataType[i] + "(D2.VALUE_01) AS CO2_" + dataType[i] + "_01 , " + dataType[i] + "(D2.VALUE_02) AS CO2_" + dataType[i] + "_02 , " + dataType[i] + "(D2.VALUE_03) AS CO2_" + dataType[i] + "_03 , " + dataType[i] + "(D2.VALUE_04) AS CO2_" + dataType[i] + "_04 " );
			sb.append("\n , " + dataType[i] + "(D2.VALUE_05) AS CO2_" + dataType[i] + "_05 , " + dataType[i] + "(D2.VALUE_06) AS CO2_" + dataType[i] + "_06 , " + dataType[i] + "(D2.VALUE_07) AS CO2_" + dataType[i] + "_07 , " + dataType[i] + "(D2.VALUE_08) AS CO2_" + dataType[i] + "_08 , " + dataType[i] + "(D2.VALUE_09) AS CO2_" + dataType[i] + "_09 " );
			sb.append("\n , " + dataType[i] + "(D2.VALUE_10) AS CO2_" + dataType[i] + "_10 , " + dataType[i] + "(D2.VALUE_11) AS CO2_" + dataType[i] + "_11 , " + dataType[i] + "(D2.VALUE_12) AS CO2_" + dataType[i] + "_12 , " + dataType[i] + "(D2.VALUE_13) AS CO2_" + dataType[i] + "_13 , " + dataType[i] + "(D2.VALUE_14) AS CO2_" + dataType[i] + "_14 " );
			sb.append("\n , " + dataType[i] + "(D2.VALUE_15) AS CO2_" + dataType[i] + "_15 , " + dataType[i] + "(D2.VALUE_16) AS CO2_" + dataType[i] + "_16 , " + dataType[i] + "(D2.VALUE_17) AS CO2_" + dataType[i] + "_17 , " + dataType[i] + "(D2.VALUE_18) AS CO2_" + dataType[i] + "_18 , " + dataType[i] + "(D2.VALUE_19) AS CO2_" + dataType[i] + "_19 " );
			sb.append("\n , " + dataType[i] + "(D2.VALUE_20) AS CO2_" + dataType[i] + "_20 , " + dataType[i] + "(D2.VALUE_21) AS CO2_" + dataType[i] + "_21 , " + dataType[i] + "(D2.VALUE_22) AS CO2_" + dataType[i] + "_22 , " + dataType[i] + "(D2.VALUE_23) AS CO2_" + dataType[i] + "_23 " );		
		}
		
		sb.append("\n 	FROM DAY_HM D2 , ( ");
		sb.append("\n 		SELECT M2.MDS_ID , L2.ID , L2.NAME , L2.PARENT_ID FROM METER M2 , LOCATION L2 WHERE M2.LOCATION_ID = L2.ID AND L2.ID= :locationId ");
		sb.append("\n 	)LL2 WHERE D2.MDEV_ID = LL2.MDS_ID AND D2.CHANNEL=0 ");
		
		if(CommonConstants.DateType.DAILY.getCode().equals(searchDateType)) {                // 일별	
			sb.append("\n    AND D2.YYYYMMDD = :startDate    ");
		}
		
		sb.append("\n 	GROUP BY LL2.ID ");
		sb.append("\n ) CO2 ");
		sb.append("\n ON HM.HM_PARENT_ID = CO2.CO2_PARENT_ID");
				
		SQLQuery query = getSession().createSQLQuery(sb.toString());
		
		query.setInteger("locationId", locationId );
		
		if(CommonConstants.DateType.DAILY.getCode().equals(searchDateType)) { 
			query.setString("startDate", startDate);
		}		
		
		
		return query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
	}

	/**
	 *  BEMS 열량사용량 , 탄소배출량  차트 최대/최소 데이터 조회.
	 *  기준키로 location 테이블의 parent_id를 사용
	 *  일(시간) 빌딩 전체 : 열량사용량/탄솝출량 의 최대 ,최소,합
	 */
	public List<Object> getConsumptionHmCo2MonitoringSumMinMaxPrentId(Map<String, Object> condition) {

		logger.info("일(시간) 빌딩 전체 : 열량 사용량/탄솝출량 의 최대 ,최소,합\n==== conditions ====\n"+condition);
		
		String searchDateType = ObjectUtils.defaultIfNull( condition.get("searchDateType") , CommonConstants.DateType.DAILY.getCode() ).toString(); // 일 , 주 , 월 , 분기
		@SuppressWarnings("unused")
        Integer supplierId = (Integer) condition.get("supplierId");
		Integer locationId = (Integer) condition.get("locationId");
		String startDate = (String)condition.get("startDate");  
		@SuppressWarnings("unused")
        String endDate   = (String)condition.get("endDate"); 

		String[] dataType  = {"MIN" , "MAX" , "SUM" };
		
		
		StringBuffer sb = new StringBuffer();
		
		sb.append("\n SELECT HM.* , CO2.* FROM (  ");
		sb.append("\n 	SELECT LL.PARENT_ID AS HM_PARENT_ID  ");
		sb.append("\n 	     , SUM(D.TOTAL) AS HM_TOTAL  ");
		
		for(int i=0; i<dataType.length; i++){

			sb.append("\n , " + dataType[i] + "(D.VALUE_00) AS HM_" + dataType[i] + "_00 , " + dataType[i] + "(D.VALUE_01) AS HM_" + dataType[i] + "_01 , " + dataType[i] + "(D.VALUE_02) AS HM_" + dataType[i] + "_02 , " + dataType[i] + "(D.VALUE_03) AS HM_" + dataType[i] + "_03 , " + dataType[i] + "(D.VALUE_04) AS HM_" + dataType[i] + "_04 " );
			sb.append("\n , " + dataType[i] + "(D.VALUE_05) AS HM_" + dataType[i] + "_05 , " + dataType[i] + "(D.VALUE_06) AS HM_" + dataType[i] + "_06 , " + dataType[i] + "(D.VALUE_07) AS HM_" + dataType[i] + "_07 , " + dataType[i] + "(D.VALUE_08) AS HM_" + dataType[i] + "_08 , " + dataType[i] + "(D.VALUE_09) AS HM_" + dataType[i] + "_09 " );
			sb.append("\n , " + dataType[i] + "(D.VALUE_10) AS HM_" + dataType[i] + "_10 , " + dataType[i] + "(D.VALUE_11) AS HM_" + dataType[i] + "_11 , " + dataType[i] + "(D.VALUE_12) AS HM_" + dataType[i] + "_12 , " + dataType[i] + "(D.VALUE_13) AS HM_" + dataType[i] + "_13 , " + dataType[i] + "(D.VALUE_14) AS HM_" + dataType[i] + "_14 " );
			sb.append("\n , " + dataType[i] + "(D.VALUE_15) AS HM_" + dataType[i] + "_15 , " + dataType[i] + "(D.VALUE_16) AS HM_" + dataType[i] + "_16 , " + dataType[i] + "(D.VALUE_17) AS HM_" + dataType[i] + "_17 , " + dataType[i] + "(D.VALUE_18) AS HM_" + dataType[i] + "_18 , " + dataType[i] + "(D.VALUE_19) AS HM_" + dataType[i] + "_19 " );
			sb.append("\n , " + dataType[i] + "(D.VALUE_20) AS HM_" + dataType[i] + "_20 , " + dataType[i] + "(D.VALUE_21) AS HM_" + dataType[i] + "_21 , " + dataType[i] + "(D.VALUE_22) AS HM_" + dataType[i] + "_22 , " + dataType[i] + "(D.VALUE_23) AS HM_" + dataType[i] + "_23 " );		
		}
	
		sb.append("\n 	FROM DAY_HM D , ( ");
		sb.append("\n 		SELECT M.MDS_ID , L.ID , L.NAME , L.PARENT_ID FROM METER M , (  ");
		sb.append("\n 			SELECT ID , NAME , PARENT_ID FROM LOCATION WHERE PARENT_ID = :parentId ");
		sb.append("\n 		) L WHERE M.LOCATION_ID = L.ID ");
		sb.append("\n 	)LL WHERE D.MDEV_ID = LL.MDS_ID AND D.CHANNEL=1 ");
		
		if(CommonConstants.DateType.DAILY.getCode().equals(searchDateType)) {                // 일별	
			sb.append("\n    AND D.YYYYMMDD = :startDate    ");
		}
		
		sb.append("\n 	GROUP BY LL.PARENT_ID ");
		sb.append("\n ) HM ");
		sb.append("\n LEFT JOIN ");
		sb.append("\n (  ");
		sb.append("\n 	SELECT LL2.PARENT_ID AS CO2_PARENT_ID   ");
		sb.append("\n 	     , SUM(D2.TOTAL) AS CO2_TOTAL  ");
		for(int i=0; i<dataType.length; i++){

			sb.append("\n , " + dataType[i] + "(D2.VALUE_00) AS CO2_" + dataType[i] + "_00 , " + dataType[i] + "(D2.VALUE_01) AS CO2_" + dataType[i] + "_01 , " + dataType[i] + "(D2.VALUE_02) AS CO2_" + dataType[i] + "_02 , " + dataType[i] + "(D2.VALUE_03) AS CO2_" + dataType[i] + "_03 , " + dataType[i] + "(D2.VALUE_04) AS CO2_" + dataType[i] + "_04 " );
			sb.append("\n , " + dataType[i] + "(D2.VALUE_05) AS CO2_" + dataType[i] + "_05 , " + dataType[i] + "(D2.VALUE_06) AS CO2_" + dataType[i] + "_06 , " + dataType[i] + "(D2.VALUE_07) AS CO2_" + dataType[i] + "_07 , " + dataType[i] + "(D2.VALUE_08) AS CO2_" + dataType[i] + "_08 , " + dataType[i] + "(D2.VALUE_09) AS CO2_" + dataType[i] + "_09 " );
			sb.append("\n , " + dataType[i] + "(D2.VALUE_10) AS CO2_" + dataType[i] + "_10 , " + dataType[i] + "(D2.VALUE_11) AS CO2_" + dataType[i] + "_11 , " + dataType[i] + "(D2.VALUE_12) AS CO2_" + dataType[i] + "_12 , " + dataType[i] + "(D2.VALUE_13) AS CO2_" + dataType[i] + "_13 , " + dataType[i] + "(D2.VALUE_14) AS CO2_" + dataType[i] + "_14 " );
			sb.append("\n , " + dataType[i] + "(D2.VALUE_15) AS CO2_" + dataType[i] + "_15 , " + dataType[i] + "(D2.VALUE_16) AS CO2_" + dataType[i] + "_16 , " + dataType[i] + "(D2.VALUE_17) AS CO2_" + dataType[i] + "_17 , " + dataType[i] + "(D2.VALUE_18) AS CO2_" + dataType[i] + "_18 , " + dataType[i] + "(D2.VALUE_19) AS CO2_" + dataType[i] + "_19 " );
			sb.append("\n , " + dataType[i] + "(D2.VALUE_20) AS CO2_" + dataType[i] + "_20 , " + dataType[i] + "(D2.VALUE_21) AS CO2_" + dataType[i] + "_21 , " + dataType[i] + "(D2.VALUE_22) AS CO2_" + dataType[i] + "_22 , " + dataType[i] + "(D2.VALUE_23) AS CO2_" + dataType[i] + "_23 " );		
		}
		
		sb.append("\n 	FROM DAY_HM D2 , ( ");
		sb.append("\n 		SELECT M2.MDS_ID , L2.ID , L2.NAME , L2.PARENT_ID FROM METER M2 , (  ");
		sb.append("\n 			SELECT ID , NAME , PARENT_ID FROM LOCATION WHERE PARENT_ID = :parentId ");
		sb.append("\n 		) L2 WHERE M2.LOCATION_ID = L2.ID ");
		sb.append("\n 	) LL2 WHERE D2.MDEV_ID = LL2.MDS_ID AND D2.CHANNEL=0 ");
		
		if(CommonConstants.DateType.DAILY.getCode().equals(searchDateType)) {                // 일별	
			sb.append("\n    AND D2.YYYYMMDD = :startDate    ");
		}
		
		sb.append("\n 	GROUP BY LL2.PARENT_ID ");
		sb.append("\n ) CO2 ");
		sb.append("\n ON HM.HM_PARENT_ID = CO2.CO2_PARENT_ID");
				
		SQLQuery query = getSession().createSQLQuery(sb.toString());
		
		query.setInteger("parentId", locationId );
		
		if(CommonConstants.DateType.DAILY.getCode().equals(searchDateType)) { 
			query.setString("startDate", startDate);
		}		
		
		
		return query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
	}
	
	public List<Object> getConsumptionHmCo2DayValuesParentId(Map<String, Object> condition) {

        logger.info("최상위 위치별 총합\n==== conditions ====\n" + condition);

        Integer locationId = (Integer) condition.get("locationId");
        Integer channel = (Integer) condition.get("channel");
        // 탄소일 경우만 0 ,
        // 수도/온도/습도의
        // 사용량일때는 1
        
        String startDate = (String) condition.get("startDate");
        String hh0 = (String) condition.get("hh0");

        StringBuffer sb = new StringBuffer();

        sb.append("\n   SELECT SUM(D.VALUE_" + hh0 + ") AS VALUE_" + hh0 + " ");
        sb.append("\n    FROM DAY_HM D ");
        sb.append("\n    INNER JOIN (SELECT ID FROM LOCATION WHERE PARENT_ID=:parentId) L ");
        sb.append("\n    ON D.LOCATION_ID = L.ID ");
        sb.append("\n    WHERE D.CHANNEL=:channel ");
        sb.append("\n    AND D.YYYYMMDD = :startDate ");

        SQLQuery query = getSession().createSQLQuery(sb.toString());

        query.setInteger("parentId", locationId);
        query.setInteger("channel", channel);

        query.setString("startDate", startDate);

        return query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP)
                .list();
    }
	
	
	/**
	 *  BEMS 일주일 열량사용량 , 탄소배출량  차트 데이터 조회.
	 *  일주일 : 빌딩 전체 열량사용량/탄솝출량 
	 */
	public List<Object> getConsumptionHmCo2WeekMonitoringLocationId(Map<String, Object> condition) {
		
		logger.info("일주일 : 빌딩 전체 열량사용량/탄솝출량 \n==== conditions ====\n"+condition);
		
		String searchDateType = ObjectUtils.defaultIfNull( condition.get("searchDateType") , CommonConstants.DateType.DAILY.getCode() ).toString(); // 일 , 주 , 월 , 분기
		@SuppressWarnings("unused")
        Integer supplierId = (Integer) condition.get("supplierId");
		Integer locationId = (Integer) condition.get("locationId");
		String startDate = (String)condition.get("startDate");  
		String endDate   = (String)condition.get("endDate"); 
		
		
		StringBuffer sb = new StringBuffer();
		
		sb.append("\n    SELECT HM.YYYYMMDD AS YYYYMMDD , HM.TOTAL AS HM_TOTAL ,  CO2.TOTAL AS CO2_TOTAL  ");
		sb.append("\n      FROM (      ");
		sb.append("\n			  SELECT D.YYYYMMDD , SUM(D.TOTAL) AS TOTAL ");
		sb.append("\n				FROM DAY_HM D , (      ");
		sb.append("\n						SELECT M.MDS_ID , L.ID , L.NAME  ");
		sb.append("\n						  FROM METER M , LOCATION L  ");
		sb.append("\n						 WHERE M.LOCATION_ID = L.ID     ");
		sb.append("\n						   AND L.ID = :locationId ");
		sb.append("\n					 ) LL  ");
		sb.append("\n				WHERE D.MDEV_ID = LL.MDS_ID  ");
		sb.append("\n				  AND D.CHANNEL = 1       ");
		if (CommonConstants.DateType.WEEKLY.getCode().equals(searchDateType) || CommonConstants.DateType.MONTHLY.getCode().equals(searchDateType) || CommonConstants.DateType.QUARTERLY.getCode().equals(searchDateType)) {        // 주/월/분기별
			sb.append("\n    			  AND D.YYYYMMDD >= :startDate   ");
			sb.append("\n    			  AND D.YYYYMMDD <= :endDate     ");
		}	
		sb.append("\n				GROUP BY D.YYYYMMDD   ");
		sb.append("\n	   ) HM ");
		sb.append("\n	   LEFT JOIN ");
		sb.append("\n	   ( ");
		sb.append("\n			   SELECT D2.YYYYMMDD , SUM(D2.TOTAL) AS TOTAL ");
		sb.append("\n				 FROM DAY_HM D2 , ( ");
		sb.append("\n						SELECT M2.MDS_ID , L2.ID , L2.NAME  ");
		sb.append("\n						 FROM METER M2 , LOCATION L2  ");
		sb.append("\n						WHERE M2.LOCATION_ID = L2.ID     ");
		sb.append("\n						  AND L2.ID = :locationId  ");
		sb.append("\n					  ) LL2  ");
		sb.append("\n				WHERE D2.MDEV_ID = LL2.MDS_ID  ");
		sb.append("\n				  AND D2.CHANNEL = 0       ");
		if (CommonConstants.DateType.WEEKLY.getCode().equals(searchDateType) || CommonConstants.DateType.MONTHLY.getCode().equals(searchDateType) || CommonConstants.DateType.QUARTERLY.getCode().equals(searchDateType)) {        // 주/월/분기별
			sb.append("\n  				  AND D2.YYYYMMDD >= :startDate   ");
			sb.append("\n  				  AND D2.YYYYMMDD <= :endDate     ");
		}	
		sb.append("\n				GROUP BY D2.YYYYMMDD    ");
		sb.append("\n         ) CO2    ");
		sb.append("\n    ON HM.YYYYMMDD = CO2.YYYYMMDD ");
		sb.append("\n	ORDER BY HM.YYYYMMDD ASC  ");
		
		
		SQLQuery query = getSession().createSQLQuery(sb.toString());
		
		query.setInteger("locationId", locationId );
		
		if (CommonConstants.DateType.WEEKLY.getCode().equals(searchDateType) || CommonConstants.DateType.MONTHLY.getCode().equals(searchDateType) || CommonConstants.DateType.QUARTERLY.getCode().equals(searchDateType)) { 
			query.setString("startDate", startDate);
			query.setString("endDate", endDate);
		}		
		
		
		return query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
	}
	

	/**
	 *  BEMS 일주일 열량사용량 , 탄소배출량  차트 데이터 조회.
	 *  일주일 : 빌딩 전체 열량사용량/탄솝출량 
	 */
	public List<Object> getConsumptionHmCo2WeekMonitoringParentId(Map<String, Object> condition) {

		logger.info("일주일 : 빌딩 전체 열량사용량/탄솝출량 \n==== conditions ====\n"+condition);
		
		String searchDateType = ObjectUtils.defaultIfNull( condition.get("searchDateType") , CommonConstants.DateType.DAILY.getCode() ).toString(); // 일 , 주 , 월 , 분기
		@SuppressWarnings("unused")
        Integer supplierId = (Integer) condition.get("supplierId");
		Integer locationId = (Integer) condition.get("locationId");
		String startDate = (String)condition.get("startDate");  
		String endDate   = (String)condition.get("endDate"); 
		
		
		StringBuffer sb = new StringBuffer();
		
		sb.append("\n    SELECT HM.YYYYMMDD AS YYYYMMDD , HM.TOTAL AS HM_TOTAL ,  CO2.TOTAL AS CO2_TOTAL  ");
		sb.append("\n      FROM (      ");
		sb.append("\n			  SELECT D.YYYYMMDD , SUM(D.TOTAL) AS TOTAL ");
		sb.append("\n				FROM DAY_HM D , (      ");
		sb.append("\n						SELECT M.MDS_ID , L.ID , L.NAME  ");
		sb.append("\n						  FROM METER M , ( ");
		sb.append("\n									SELECT ID , NAME  ");
		sb.append("\n									  FROM LOCATION  ");
		sb.append("\n									 WHERE PARENT_ID = :parentId      ");
		sb.append("\n							   ) L  ");
		sb.append("\n						 WHERE M.LOCATION_ID = L.ID     ");
		sb.append("\n					 )LL  ");
		sb.append("\n				WHERE D.MDEV_ID = LL.MDS_ID  ");
		sb.append("\n				  AND D.CHANNEL = 1       ");
		if (CommonConstants.DateType.WEEKLY.getCode().equals(searchDateType) || CommonConstants.DateType.MONTHLY.getCode().equals(searchDateType) || CommonConstants.DateType.QUARTERLY.getCode().equals(searchDateType)) {        // 주/월/분기별
			sb.append("\n    			  AND D.YYYYMMDD >= :startDate   ");
			sb.append("\n    			  AND D.YYYYMMDD <= :endDate     ");
		}	
		sb.append("\n				GROUP BY D.YYYYMMDD   ");
		sb.append("\n	   ) HM ");
		sb.append("\n	   LEFT JOIN ");
		sb.append("\n	   ( ");
		sb.append("\n			   SELECT D2.YYYYMMDD , SUM(D2.TOTAL) AS TOTAL ");
		sb.append("\n				 FROM DAY_HM D2 , ( ");
		sb.append("\n						SELECT M2.MDS_ID , L2.ID , L2.NAME  ");
		sb.append("\n						 FROM METER  M2 , ( ");
		sb.append("\n								SELECT ID , NAME  ");
		sb.append("\n								  FROM LOCATION  ");
		sb.append("\n								 WHERE PARENT_ID = :parentId      ");
		sb.append("\n							  )L2  ");
		sb.append("\n						WHERE M2.LOCATION_ID = L2.ID     ");
		sb.append("\n					  )LL2  ");
		sb.append("\n				WHERE D2.MDEV_ID = LL2.MDS_ID  ");
		sb.append("\n				  AND D2.CHANNEL = 0       ");
		if (CommonConstants.DateType.WEEKLY.getCode().equals(searchDateType) || CommonConstants.DateType.MONTHLY.getCode().equals(searchDateType) || CommonConstants.DateType.QUARTERLY.getCode().equals(searchDateType)) {        // 주/월/분기별
			sb.append("\n  				  AND D2.YYYYMMDD >= :startDate   ");
			sb.append("\n  				  AND D2.YYYYMMDD <= :endDate     ");
		}	
		sb.append("\n				GROUP BY D2.YYYYMMDD    ");
		sb.append("\n         ) CO2    ");
		sb.append("\n    ON HM.YYYYMMDD = CO2.YYYYMMDD ");
		sb.append("\n	ORDER BY HM.YYYYMMDD ASC  ");
		
				
		SQLQuery query = getSession().createSQLQuery(sb.toString());
		
		query.setInteger("parentId", locationId );
		
		if (CommonConstants.DateType.WEEKLY.getCode().equals(searchDateType) || CommonConstants.DateType.MONTHLY.getCode().equals(searchDateType) || CommonConstants.DateType.QUARTERLY.getCode().equals(searchDateType)) { 
			query.setString("startDate", startDate);
			query.setString("endDate", endDate);
		}		
		
		
		return query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
	}
	
	/**
	 *  BEMS 일주일 온도 / 습도  차트 데이터 조회.
	 *  일주일 : 빌딩 전체 온도 /습도 
	 *  온도는 2 , 습도는 3으로 임시로 정하여 작업하였음..
	 */
	public List<Object> getConsumptionTmHmWeekMonitoring(Map<String, Object> condition) {

		logger.info("일주일 : 빌딩 전체 온도 /습도\n==== conditions ====\n"+condition);
		
		String searchDateType = ObjectUtils.defaultIfNull( condition.get("searchDateType") , CommonConstants.DateType.DAILY.getCode() ).toString(); // 일 , 주 , 월 , 분기
		@SuppressWarnings("unused")
        Integer supplierId = (Integer) condition.get("supplierId");
		Integer locationId = (Integer) condition.get("locationId");
//		String channelType  = (String)condition.get("channelType"); // 탄소일 경우만 0 , 열량/온도/습도의 사용량일때는 1
		String startDate = (String)condition.get("startDate");  
		String endDate   = (String)condition.get("endDate"); 
		
		
		StringBuffer sb = new StringBuffer();
		
		sb.append("\n    SELECT HM.YYYYMMDD AS YYYYMMDD , HM.TOTAL AS HM_TOTAL ,  CO2.TOTAL AS CO2_TOTAL  ");
		sb.append("\n      FROM (      ");
		sb.append("\n			  SELECT D.YYYYMMDD , SUM(D.TOTAL) AS TOTAL ");
		sb.append("\n				FROM DAY_HM D , (      ");
		sb.append("\n						SELECT M.MDS_ID , L.ID , L.NAME  ");
		sb.append("\n						  FROM METER M , ( ");
		sb.append("\n									SELECT ID , NAME  ");
		sb.append("\n									  FROM LOCATION  ");
		sb.append("\n									 WHERE PARENT_ID = :parentId      ");
		sb.append("\n							   ) L  ");
		sb.append("\n						 WHERE M.LOCATION_ID = L.ID     ");
		sb.append("\n					 ) LL  ");
		sb.append("\n				WHERE D.MDEV_ID = LL.MDS_ID  ");
		sb.append("\n				  AND D.CHANNEL = 2       ");
		
		if (CommonConstants.DateType.WEEKLY.getCode().equals(searchDateType) || CommonConstants.DateType.MONTHLY.getCode().equals(searchDateType) || CommonConstants.DateType.QUARTERLY.getCode().equals(searchDateType)) {        // 주/월/분기별
			sb.append("\n    			  AND D.YYYYMMDD >= :startDate   ");
			sb.append("\n    			  AND D.YYYYMMDD <= :endDate     ");
		}	
		
		sb.append("\n				GROUP BY D.YYYYMMDD   ");
		sb.append("\n	   ) HM ");
		sb.append("\n	   LEFT JOIN ");
		sb.append("\n	   ( ");
		sb.append("\n			   SELECT D2.YYYYMMDD , SUM(D2.TOTAL) AS TOTAL ");
		sb.append("\n				 FROM DAY_HM D2 , ( ");
		sb.append("\n						SELECT M2.MDS_ID , L2.ID , L2.NAME  ");
		sb.append("\n						 FROM METER M2 , ( ");
		sb.append("\n								SELECT ID , NAME  ");
		sb.append("\n								  FROM LOCATION  ");
		sb.append("\n								 WHERE PARENT_ID = :parentId      ");
		sb.append("\n							  )L2  ");
		sb.append("\n						WHERE M2.LOCATION_ID = L2.ID     ");
		sb.append("\n					  )LL2  ");
		sb.append("\n				WHERE D2.MDEV_ID = LL2.MDS_ID  ");
		sb.append("\n				  AND D2.CHANNEL = 3       ");
		
		if (CommonConstants.DateType.WEEKLY.getCode().equals(searchDateType) || CommonConstants.DateType.MONTHLY.getCode().equals(searchDateType) || CommonConstants.DateType.QUARTERLY.getCode().equals(searchDateType)) {        // 주/월/분기별
			sb.append("\n  				  AND D2.YYYYMMDD >= :startDate   ");
			sb.append("\n  				  AND D2.YYYYMMDD <= :endDate     ");
		}	
		
		sb.append("\n				GROUP BY D2.YYYYMMDD    ");
		sb.append("\n         ) CO2    ");
		sb.append("\n    ON HM.YYYYMMDD = CO2.YYYYMMDD ");
		sb.append("\n	ORDER BY HM.YYYYMMDD ASC  ");
		
		SQLQuery query = getSession().createSQLQuery(sb.toString());
		
		query.setInteger("parentId", locationId );
		
		if (CommonConstants.DateType.WEEKLY.getCode().equals(searchDateType) || CommonConstants.DateType.MONTHLY.getCode().equals(searchDateType) || CommonConstants.DateType.QUARTERLY.getCode().equals(searchDateType)) { 
			query.setString("startDate", startDate);
			query.setString("endDate", endDate);
		}		
		
		return query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
	}
	
	/**
	 * BEMS 설비별 사용량 조회.
	 */
	public List<Object> getCompareFacilityDayData(Map<String, Object> condition) {

		
		StringBuffer sb = new StringBuffer();

		sb.append("\n    SELECT    								");
		sb.append("\n    (SELECT TOTAL FROM DAY_HM 				");
		sb.append("\n  	 WHERE channel = :channel               ");
		sb.append("\n  	 AND YYYYMMDD = :yesterday              ");
		sb.append("\n  	 AND ENDDEVICE_ID = :endDeviceId        ");
		sb.append("\n    ) HM_YESTERDAY,                        ");

		sb.append("\n    (SELECT TOTAL FROM DAY_HM              ");
		sb.append("\n  	 WHERE channel = :channel               ");
		sb.append("\n  	 AND YYYYMMDD = :today                  ");
		sb.append("\n  	 AND ENDDEVICE_ID = :endDeviceId        ");
		sb.append("\n    ) HM_TODAY,                            ");

		sb.append("\n    (SELECT SUM(TOTAL) FROM DAY_HM         ");
		sb.append("\n  	 WHERE channel = :channel               ");
		sb.append("\n  	 AND YYYYMMDD >= :lastWeekStartDate     ");
		sb.append("\n  	 AND YYYYMMDD <= :lastWeekEndDate       ");
		sb.append("\n  	 AND ENDDEVICE_ID = :endDeviceId        ");
		sb.append("\n    ) HM_LASTWEEK,                         ");
		
		sb.append("\n    (SELECT SUM(TOTAL) FROM DAY_HM         ");
		sb.append("\n  	 WHERE channel = :channel               ");
		sb.append("\n  	 AND YYYYMMDD >=:weekStartDate          ");
		sb.append("\n  	 AND YYYYMMDD <= :weekEndDate           ");
		sb.append("\n  	 AND ENDDEVICE_ID = :endDeviceId        ");
		sb.append("\n    ) HM_WEEK,                             ");
		
		sb.append("\n    (SELECT TOTAL FROM DAY_HM ");
		sb.append("\n  	 WHERE channel = :channel               ");
		sb.append("\n  	 AND YYYYMMDD = :yesterday              ");
		sb.append("\n  	 AND ENDDEVICE_ID = :endDeviceId        ");
		sb.append("\n    ) HM_YESTERDAY,                        ");

		sb.append("\n    (SELECT TOTAL FROM DAY_HM              ");
		sb.append("\n  	 WHERE channel = :channel               ");
		sb.append("\n  	 AND YYYYMMDD = :today                  ");
		sb.append("\n  	 AND ENDDEVICE_ID = :endDeviceId        ");
		sb.append("\n    ) HM_TODAY,                            ");

		sb.append("\n    (SELECT SUM(TOTAL) FROM DAY_HM         ");
		sb.append("\n  	 WHERE channel = :channel               ");
		sb.append("\n  	 AND YYYYMMDD >= :lastWeekStartDate     ");
		sb.append("\n  	 AND YYYYMMDD <= :lastWeekEndDate       ");
		sb.append("\n  	 AND ENDDEVICE_ID = :endDeviceId        ");
		sb.append("\n    ) HM_LASTWEEK,                         ");
		
		sb.append("\n    (SELECT SUM(TOTAL) FROM DAY_HM         ");
		sb.append("\n  	 WHERE channel = :channel               ");
		sb.append("\n  	 AND YYYYMMDD >= :weekStartDate         ");
		sb.append("\n  	 AND YYYYMMDD <= :weekEndDate           ");
		sb.append("\n  	 AND ENDDEVICE_ID = :endDeviceId        ");
		sb.append("\n    ) HM_WEEK,                             ");
		
		sb.append("\n    (SELECT TOTAL FROM DAY_GM ");
		sb.append("\n  	 WHERE channel = :channel               ");
		sb.append("\n  	 AND YYYYMMDD = :yesterday              ");
		sb.append("\n  	 AND ENDDEVICE_ID = :endDeviceId        ");
		sb.append("\n    ) GM_YESTERDAY,                        ");

		sb.append("\n    (SELECT TOTAL FROM DAY_GM              ");
		sb.append("\n  	 WHERE channel = :channel               ");
		sb.append("\n  	 AND YYYYMMDD = :today                  ");
		sb.append("\n  	 AND ENDDEVICE_ID = :endDeviceId        ");
		sb.append("\n    ) GM_TODAY,                            ");

		sb.append("\n    (SELECT SUM(TOTAL) FROM DAY_GM         ");
		sb.append("\n  	 WHERE channel = :channel               ");
		sb.append("\n  	 AND YYYYMMDD >= :lastWeekStartDate     ");
		sb.append("\n  	 AND YYYYMMDD <= :lastWeekEndDate       ");
		sb.append("\n  	 AND ENDDEVICE_ID = :endDeviceId        ");
		sb.append("\n    ) GM_LASTWEEK,                         ");
		
		sb.append("\n    (SELECT SUM(TOTAL) FROM DAY_GM         ");
		sb.append("\n  	 WHERE channel = :channel               ");
		sb.append("\n  	 AND YYYYMMDD >= :weekStartDate         ");
		sb.append("\n  	 AND YYYYMMDD <= :weekEndDate           ");
		sb.append("\n  	 AND ENDDEVICE_ID = :endDeviceId        ");
		sb.append("\n    ) GM_WEEK,                             ");
		
		sb.append("\n    (SELECT TOTAL FROM DAY_WM ");
		sb.append("\n  	 WHERE channel = :channel               ");
		sb.append("\n  	 AND YYYYMMDD = :yesterday              ");
		sb.append("\n  	 AND ENDDEVICE_ID = :endDeviceId        ");
		sb.append("\n    ) WM_YESTERDAY,                        ");

		sb.append("\n    (SELECT TOTAL FROM DAY_WM              ");
		sb.append("\n  	 WHERE channel = :channel               ");
		sb.append("\n  	 AND YYYYMMDD = :today                  ");
		sb.append("\n  	 AND ENDDEVICE_ID = :endDeviceId        ");
		sb.append("\n    ) WM_TODAY,                            ");

		sb.append("\n    (SELECT SUM(TOTAL) FROM DAY_WM         ");
		sb.append("\n  	 WHERE channel = :channel               ");
		sb.append("\n  	 AND YYYYMMDD >= :lastWeekStartDate     ");
		sb.append("\n  	 AND YYYYMMDD <= :lastWeekEndDate       ");
		sb.append("\n  	 AND ENDDEVICE_ID = :endDeviceId        ");
		sb.append("\n    ) WM_LASTWEEK,                         ");
		
		sb.append("\n    (SELECT SUM(TOTAL) FROM DAY_WM         ");
		sb.append("\n  	 WHERE channel = :channel               ");
		sb.append("\n  	 AND YYYYMMDD >= :weekStartDate         ");
		sb.append("\n  	 AND YYYYMMDD <= :weekEndDate           ");
		sb.append("\n  	 AND ENDDEVICE_ID = :endDeviceId        ");
		sb.append("\n    ) WM_WEEK                              ");
		
		sb.append("\n    FROM DAY_HM                            ");
		sb.append("\n    WHERE channel=:channel                 ");
		sb.append("\n  	 AND YYYYMMDD >= :lastWeekStartDate     				");
		sb.append("\n  	 AND YYYYMMDD <= :weekEndDate     				");
		sb.append("\n  	 AND ENDDEVICE_ID = :endDeviceId        ");		

		SQLQuery query = getSession().createSQLQuery(sb.toString());
	    
		query.setString("yesterday",condition.get("yesterday").toString());
		query.setString("today",condition.get("today").toString());
		query.setString("channel","1");
		query.setString("lastWeekStartDate",condition.get("lastWeekStartDate").toString());
		query.setString("lastWeekEndDate",condition.get("lastWeekEndDate").toString());
		query.setString("weekStartDate",condition.get("weekStartDate").toString());
		query.setString("weekEndDate",condition.get("weekEndDate").toString());
		query.setString("endDeviceId",condition.get("endDeviceId").toString());
		return query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP)
				.list();
	}
	
	public DayHM getDayHM(Map<String,Object> params){
		
		String yyyymmdd = (String)params.get("yyyymmdd");  
		Integer channel = (Integer)params.get("channel");
		Integer dst = (Integer)params.get("dst");
		DeviceType mdevType = (DeviceType)params.get("mdevType");  
		String mdevId = (String)params.get("mdevId");
		
		Criteria criteria = getSession().createCriteria(DayHM.class);
		criteria.add(Restrictions.eq("id.mdevId", mdevId));
		criteria.add(Restrictions.eq("id.mdevType", mdevType));
		criteria.add(Restrictions.eq("id.dst", dst));
		criteria.add(Restrictions.eq("id.yyyymmdd", yyyymmdd));
		criteria.add(Restrictions.eq("id.channel",channel));
		
		List<DayHM> list = criteria.list();
		
		return list!=null&&list.size()>0?list.get(0):null;
	}
	
	public List<Integer> getContractIds(Map<String, String> conditionMap) {

		String startDate = conditionMap.get("startDate");
		String endDate = conditionMap.get("endDate");
		String locationCondition = conditionMap.get("locationCondition");
		String searchDateType = conditionMap.get("searchDateType");
		
		StringBuilder sb = new StringBuilder()
		.append(" SELECT d.contract.id            ")
		.append("   FROM DayHM d                   ")
		.append("  WHERE d.id.channel = :channel                            ")
		.append("    AND d.contract.serviceTypeCode.code = :serviceTypeCode ")
		.append("    AND d.contract.status.code = :status                   ");	
		
		if("1".equals(searchDateType)) {                // 일별	
			sb.append("    AND d.id.yyyymmdd = :startDate    ");
		} else if ("3".equals(searchDateType)) {        // 월별
			sb.append("    AND d.id.yyyymmdd >= :startDate   ");
			sb.append("    AND d.id.yyyymmdd <= :endDate     ");
		}
		if(!"".equals(locationCondition)) {			
			sb.append("   AND d.location.id in (" + locationCondition + ")");
		}		
		
		Query query = getSession().createQuery(sb.toString());
		query.setString("channel", DefaultChannel.Usage.getCode() + "");
		query.setString("serviceTypeCode", MeterType.GasMeter.getServiceType());
		query.setString("status", ContractStatus.NORMAL.getCode());
		
		if("1".equals(searchDateType)) { 
			query.setString("startDate", startDate);
		} else if ("3".equals(searchDateType)) { 
			query.setString("startDate", startDate);
			query.setString("endDate", endDate);
		}			
		
		return query.list();
	}
	
	
	public List<Object> getDayHMsByNoSended(String date) {

		StringBuffer sb = new StringBuffer();

		sb.append("\n 	select A.mdev_id,A.total as VALUE,A.name as FLOOR,B.name as BLOCK from ");
		sb.append("\n 	(select d.mdev_id,d.total,l.name,l.parent_id from day_hm d, ");
		sb.append("\n 	(select id,parent_id,name from location) l ");
		sb.append("\n 	where d.yyyymmdd=:date and d.channel=1 and d.mdev_type='Meter' and d.LOCATION_ID is not null ");
		sb.append("\n 	and d.send_result is null ");
		sb.append("\n 	and d.location_id=l.id) A left outer join "); 
		sb.append("\n 	(select id,parent_id,name from location) B ");
		sb.append("\n 	on A.parent_id=B.id ");
		
		SQLQuery query = getSession().createSQLQuery(sb.toString());
		
		query.setString("date", date);
		
		return query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP)
				.list();
	}
	
	// day의 yyyymm의 value_xx 값의 합을 리턴 : month_em에 넣을 값.
	public String getDaySumValueByYYYYMM(DayPk daypk) {

		StringBuffer sb = new StringBuffer();
		sb.append("\n SELECT	total				");
		sb.append("\n FROM      DayHM                 					");
		sb.append("\n WHERE 	id.yyyymmdd = :yyyymmdd             		");
		sb.append("\n AND		id.mdevType = :mdevType                 ");
		sb.append("\n AND		id.mdevId 	= :mdevId                   ");
		sb.append("\n AND		id.channel 	= :channel                  ");
		sb.append("\n AND		id.dst 		= :dst                  	");

		Query query = getSession().createQuery(sb.toString()).setString(
				"yyyymmdd", daypk.getYyyymmdd()).setString("mdevType",
				daypk.getMDevType().toString()).setString("mdevId",
				daypk.getMDevId()).setInteger("channel", daypk.getChannel())
				.setInteger("dst", daypk.getDst());

		return StringUtil.nullToBlank(query.uniqueResult());
	}

	/* (non-Javadoc)
	 * @see com.aimir.dao.mvm.DayHMDao#getDayHMs(com.aimir.model.mvm.DayHM)
	 */
	public List<DayHM> getDayHMs(DayHM dayHM) {
		
		Criteria criteria = getSession().createCriteria(DayHM.class);
		
		if (dayHM != null) {
			
			if (dayHM.getContract() != null) {
				
				if (dayHM.getContract().getId() != null) {
					
					criteria.add(Restrictions.eq("contract.id", dayHM.getContract().getId()));
				} 
			}
			
			if (dayHM.getChannel() != null) {
				
				criteria.add(Restrictions.eq("id.channel", dayHM.getChannel()));
			}
			
			if (dayHM.getYyyymmdd() != null) {
				
				
				criteria.add(Restrictions.eq("id.yyyymmdd", dayHM.getYyyymmdd()));
			}
			
			if (dayHM.getMDevType() != null) {
				
				criteria.add(Restrictions.eq("id.mdevType", dayHM.getMDevType()));
			}
		}

		return criteria.list();
	}
	
	public List<Object> getDayHMsAvg(DayHM dayHM) {

		Criteria criteria = getSession().createCriteria(DayHM.class);
		
		if (dayHM != null) {
			
			if (dayHM.getChannel() != null) {
				
				criteria.add(Restrictions.eq("id.channel", dayHM.getChannel()));
			}
			
			if (dayHM.getYyyymmdd() != null) {
				
				
				criteria.add(Restrictions.eq("id.yyyymmdd", dayHM.getYyyymmdd()));
			}
			
			if (dayHM.getMDevType() != null) {
				
				criteria.add(Restrictions.eq("id.mdevType", dayHM.getMDevType()));
			}

			if (dayHM.getLocation() != null) {

				criteria.add(Restrictions.eq("location.id", dayHM.getLocation().getId()));
			}
		}

		ProjectionList pjl = Projections.projectionList();
		pjl.add(Projections.avg("value_00"));
		pjl.add(Projections.avg("value_01"));
		pjl.add(Projections.avg("value_02"));
		pjl.add(Projections.avg("value_03"));
		pjl.add(Projections.avg("value_04"));
		pjl.add(Projections.avg("value_05"));
		pjl.add(Projections.avg("value_06"));
		pjl.add(Projections.avg("value_07"));
		pjl.add(Projections.avg("value_08"));
		pjl.add(Projections.avg("value_09"));
		pjl.add(Projections.avg("value_10"));
		pjl.add(Projections.avg("value_11"));
		pjl.add(Projections.avg("value_12"));
		pjl.add(Projections.avg("value_13"));
		pjl.add(Projections.avg("value_14"));
		pjl.add(Projections.avg("value_15"));
		pjl.add(Projections.avg("value_16"));
		pjl.add(Projections.avg("value_17"));
		pjl.add(Projections.avg("value_18"));
		pjl.add(Projections.avg("value_19"));
		pjl.add(Projections.avg("value_20"));
		pjl.add(Projections.avg("value_21"));
		pjl.add(Projections.avg("value_22"));
		pjl.add(Projections.avg("value_23"));

		criteria.setProjection(pjl);
		return criteria.list();
	}
	
	public Double getDayHMsUsageAvg(DayHM dayHM) {

		Criteria criteria = getSession().createCriteria(DayHM.class);
		
		if (dayHM != null) {
			
			if (dayHM.getChannel() != null) {
				
				criteria.add(Restrictions.eq("id.channel", dayHM.getChannel()));
			}
			
			if (dayHM.getYyyymmdd() != null) {
				
				
				criteria.add(Restrictions.eq("id.yyyymmdd", dayHM.getYyyymmdd()));
			}
			
			if (dayHM.getMDevType() != null) {
				
				criteria.add(Restrictions.eq("id.mdevType", dayHM.getMDevType()));
			}

			if (dayHM.getLocation() != null) {

				criteria.add(Restrictions.eq("location.id", dayHM.getLocation().getId()));
			}
		}

		ProjectionList pjl = Projections.projectionList();
		pjl.add(Projections.avg("total"));
		criteria.setProjection(pjl);

		return ((Number)(criteria.list().get(0) == null ? 0.0 : criteria.list().get(0))).doubleValue();
	}
	
	public Double getDayHMsUsageMonthToDate(DayHM dayHM, String startDay, String endDay) {
		
		Criteria criteria = getSession().createCriteria(DayHM.class);
		
		if (dayHM != null) {
			
			if (dayHM.getContract() != null) {
				
				if (dayHM.getContract().getId() != null) {
					
					criteria.add(Restrictions.eq("contract.id", dayHM.getContract().getId()));
				} 
			}
			
			if (dayHM.getChannel() != null) {
				
				criteria.add(Restrictions.eq("id.channel", dayHM.getChannel()));
			}
			
			if (dayHM.getYyyymmdd() != null) {
				
				
				criteria.add(Restrictions.eq("id.yyyymmdd", dayHM.getYyyymmdd()));
			}
			
			if (dayHM.getMDevType() != null) {
				
				criteria.add(Restrictions.eq("id.mdevType", dayHM.getMDevType()));
			}
			
			if (startDay != null) {
				criteria.add(Restrictions.ge("id.yyyymmdd", startDay));
			}

			if (endDay != null) {
				
				criteria.add(Restrictions.le("id.yyyymmdd", endDay));
			}
		}

		ProjectionList pjl = Projections.projectionList();
		pjl.add(Projections.avg("total"));
		criteria.setProjection(pjl);

		return ((Number)(criteria.list().get(0) == null ? 0.0 : criteria.list().get(0))).doubleValue();
	}
	
	public Map<String, Object> getLast(Integer id) {
		
		String sqlStr = " "
			+ "\n select  id.yyyymmdd            as  lastDay "
			+ "\n       , total                  as  total "
			+ "\n   from  DayHM "
			+ "\n  where  contract.id = :contractId "
			+ "\n    and  id.mdevType = :mdevType "
			+ "\n    and  id.channel =  1 "
			+ "\n    and  id.yyyymmdd = ( select  max(id.yyyymmdd) "
			+ "\n                           from  DayHM "
			+ "\n                          where  contract.id = :contractId) ";

		Query query = getSession().createQuery(sqlStr);

		query.setInteger("contractId", id);
		query.setString("mdevType",CommonConstants.DeviceType.Meter.name());
		
		List<Map<String, Object>> returnList = query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
		
		return 1 == returnList.size() ? returnList.get(0) : null;
	}

    /**
     * method name : getMeteringSuccessCountListPerLocation<b/>
     * method Desc : 
     *
     * @param conditionMap
     * @return
     */
	@Deprecated
    public List<Map<String, Object>> getMeteringSuccessCountListPerLocation(Map<String, Object> conditionMap) {
        Integer supplierId = (Integer)conditionMap.get("supplierId");
        String meterType = StringUtil.nullToBlank(conditionMap.get("meterType"));
        String searchStartDate = StringUtil.nullToBlank(conditionMap.get("searchStartDate"));
        String searchEndDate = StringUtil.nullToBlank(conditionMap.get("searchEndDate"));

        // 미터 타입별 미터링데이터 테이블 설정
//        String meteringDataTable = CommonConstants.MeterType.valueOf(meterType).getMeteringTableName();

        StringBuilder sb = new StringBuilder();

        sb.append("\nSELECT c.location_id AS LOC_ID, ");
        sb.append("\n       COUNT(c.mdev_id) AS SUCCESS_CNT ");
        sb.append("\nFROM ( ");
        sb.append("\n    SELECT m.location_id, ");
        sb.append("\n           d.mdev_id ");
        sb.append("\n    FROM day_hm d, ");
        sb.append("\n         meter m, ");
        sb.append("\n         location p ");
        sb.append("\n    WHERE d.meter_id = m.id ");
        sb.append("\n    AND   p.id = m.location_id ");
        sb.append("\n    AND   m.meter = :meterType ");
        sb.append("\n    AND   m.install_date <= :endDateTime ");
        sb.append("\n    AND   m.supplier_id = :supplierId ");
        sb.append("\n    AND   d.yyyymmdd BETWEEN :startDate AND :endDate ");
        sb.append("\n    AND   d.mdev_type = 'Meter' ");
        sb.append("\n    AND   d.channel = :channel ");
        sb.append("\n    GROUP BY m.location_id, d.mdev_id ");
        sb.append("\n) c ");
        sb.append("\nGROUP BY c.location_id ");

        SQLQuery query = getSession().createSQLQuery(new SQLWrapper().getQuery(sb.toString()));
        query.setString("startDate", searchStartDate);
        query.setString("endDate", searchEndDate);
        query.setString("endDateTime", searchEndDate + "235959");
        query.setString("meterType", meterType);
        query.setInteger("channel", DefaultChannel.Usage.getCode());
        query.setInteger("supplierId", supplierId);

        return query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
    }

    @Override
    public List<Object> getConsumptionEmCo2ManualMonitoring(Map<String, Object> condition, DateType dateType) {
    	if(DateType.WEEKLY.equals(dateType)) {
    		return getConsumptionEmCo2WeekManualMonitoring(condition);
    	}
    	else {
    		return new ArrayList<Object>();
    	}
    }

	private List<Object> getConsumptionEmCo2WeekManualMonitoring(Map<String, Object> condition) {
		
		Integer meterId = (Integer) condition.get("meterId");
		String startDate = (String) condition.get("startDate");
		String endDate = (String) condition.get("endDate");

		StringBuffer sb = new StringBuffer();
		
		sb.append("\n    SELECT HM.YYYYMMDD AS YYYYMMDD , HM.TOTAL AS HM_TOTAL ,  CO2.TOTAL AS CO2_TOTAL  ");
		sb.append("\n      FROM (      ");
		sb.append("\n			  SELECT D.YYYYMMDD , SUM(D.TOTAL) AS TOTAL ");
		sb.append("\n				FROM DAY_HM D , (      ");
		sb.append("\n						SELECT M.MDS_ID , L.ID , L.NAME  ");
		sb.append("\n						  FROM METER M , LOCATION L  ");
		sb.append("\n						 WHERE M.LOCATION_ID = L.ID     ");
		sb.append("\n						   AND M.ID = :meterId ");
		sb.append("\n					 ) LL  ");
		sb.append("\n				WHERE D.MDEV_ID = LL.MDS_ID  ");
		sb.append("\n				  AND D.CHANNEL = 1       ");
		if(startDate != null && endDate != null) {
			sb.append("\n    			  AND D.YYYYMMDD >= :startDate   ");
			sb.append("\n    			  AND D.YYYYMMDD <= :endDate     ");
		}	
		sb.append("\n				GROUP BY D.YYYYMMDD   ");
		sb.append("\n	   ) HM ");
		sb.append("\n	   LEFT JOIN ");
		sb.append("\n	   ( ");
		sb.append("\n			   SELECT D2.YYYYMMDD , SUM(D2.TOTAL) AS TOTAL ");
		sb.append("\n				 FROM DAY_HM D2 , ( ");
		sb.append("\n						SELECT M2.MDS_ID , L2.ID , L2.NAME  ");
		sb.append("\n						 FROM METER M2 , LOCATION L2  ");
		sb.append("\n						WHERE M2.LOCATION_ID = L2.ID     ");
		sb.append("\n						  AND M2.ID = :meterId  ");
		sb.append("\n					  )LL2  ");
		sb.append("\n				WHERE D2.MDEV_ID = LL2.MDS_ID  ");
		sb.append("\n				  AND D2.CHANNEL = 0       ");
		if(startDate != null && endDate != null) {
			sb.append("\n  				  AND D2.YYYYMMDD >= :startDate   ");
			sb.append("\n  				  AND D2.YYYYMMDD <= :endDate     ");
		}	
		sb.append("\n				GROUP BY D2.YYYYMMDD    ");
		sb.append("\n         ) CO2    ");
		sb.append("\n    ON HM.YYYYMMDD = CO2.YYYYMMDD ");
		sb.append("\n	ORDER BY HM.YYYYMMDD ASC  ");
		
		
		SQLQuery query = getSession().createSQLQuery(sb.toString());
		
		query.setInteger("meterId", meterId );
		
		if(startDate != null && endDate != null) {
			query.setString("startDate", startDate);
			query.setString("endDate", endDate);
		}		
		return query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
	}
	
	public List<Object> getConsumptionHmValueSum(int supplierId, String startDate, String endDate, int starthour, int endhour) {

		Boolean plus= true;
		logger.debug("startDate : " + startDate+", endDate : "+ endDate+", starthour : "+ starthour+", endhour : "+ endhour);
		StringBuffer sb = new StringBuffer();
		
		sb.append("\n    SELECT ");
	
		for(int hh=starthour; hh<endhour+1 ;hh++ ){
			sb.append("SUM(");
			sb.append("VALUE_" + (hh<10? "0":"")+hh+")");
			if(hh == endhour){
				plus = false;
			}	
			if(plus){
				sb.append("+");
			}
		
		}
		sb.append("\n  AS VALUE_SUM ");
		sb.append("\n  FROM DAY_HM");
		sb.append("\n  WHERE");
		if(startDate != null && endDate != null) {
			sb.append("\n YYYYMMDD BETWEEN :startDate AND :endDate");
		}
		sb.append("\n 	AND CHANNEL = :channel ");
        sb.append("\n   AND MDEV_TYPE = :mdevType ");
        sb.append("\n	AND SUPPLIER_ID = :supplierid");
		
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
