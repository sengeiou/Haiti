/**
 * DayEMDaoImpl.java Copyright NuriTelecom Limited 2011
 */

package com.aimir.dao.mvm.impl;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
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
import com.aimir.dao.mvm.DayEMDao;
import com.aimir.model.mvm.DayEM;
import com.aimir.model.mvm.DayPk;
import com.aimir.util.Condition;
import com.aimir.util.Condition.Restriction;
import com.aimir.util.SQLWrapper;
import com.aimir.util.SearchCondition;
import com.aimir.util.StringUtil;

/**
 * DayEMDaoImpl.java Description 
 *
 * 
 * Date          Version     Author   Description
 * 2011. 4. 29.   v1.0       김상연         DayEM 조회 조건 (DayEM)
 * 2011. 5. 16.   v1.1       김상연         기기별 그리드 조회
 *
 */
@Repository(value = "dayemDao")
@SuppressWarnings("unchecked")
public class DayEMDaoImpl extends AbstractHibernateGenericDao<DayEM, Integer> 
        implements DayEMDao {

    private static Log logger = LogFactory.getLog(DayEMDaoImpl.class);

    
    
    @Autowired
    protected DayEMDaoImpl(SessionFactory sessionFactory) {
        super(DayEM.class);
        super.setSessionFactory(sessionFactory);
    }

    @Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
    public List<DayEM> getDayEMsByListCondition(Set<Condition> set) {

        return findByConditions(set);
    }

    @Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
    public List<Object> getDayEMsCountByListCondition(Set<Condition> set) {

        return findTotalCountByConditions(set);
    }

    // totCount를 int 형으로 retrun
    @Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
    public int getIntDayEMsCountByListCondition(Set<Condition> set) {
        Integer objVal = 0;
        Iterator it = findTotalCountByConditions(set).iterator();

        while (it.hasNext()) {
            objVal = (Integer) it.next();
        }
        return objVal;
    }
    
    @Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
    public List<Object> getTotalGroupByListCondition(Set<Condition> conditions) {
        Criteria criteria = getSession().createCriteria(DayEM.class);
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
        criteria.setProjection(Projections.projectionList().add(
                Projections.count("total")));
        return criteria.list();
    }

    @Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
    public List<Object> getDayEMsGroupByListCondition(Set<Condition> conditions) {
        Criteria criteria = getSession().createCriteria(DayEM.class);
        int first = 0;
        int max = 0;
        if (conditions != null) {

            Iterator it = conditions.iterator();
            while (it.hasNext()) {
                Condition condition = (Condition) it.next();
                Criterion addCriterion = SearchCondition
                        .getCriterion(condition);

                if (addCriterion != null) {
                    criteria.add(addCriterion);
                }

                if (condition.getRestriction() == Restriction.ORDERBY)
                    criteria.addOrder(Order.asc(condition.getField()));
                if (condition.getRestriction() == Restriction.ORDERBYDESC)
                    criteria.addOrder(Order.desc(condition.getField()));

                if (condition.getRestriction() == Restriction.FIRST)
                    first = (Integer) condition.getValue()[0];
                if (condition.getRestriction() == Restriction.MAX)
                    max = (Integer) condition.getValue()[0];

            }

        }
        criteria.setProjection(Projections.projectionList().add(
                Projections.sum("total")).add(Projections.property("meter.id"))
                .add(Projections.property("contract")).add(
                        Projections.groupProperty("contract")).add(
                        Projections.groupProperty("meter.id")));

        criteria.setFirstResult(first);
        criteria.setFetchSize(max);
        return criteria.list();
    }

    @Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
    public List<Object> getDayEMsMaxMinAvgSum(Set<Condition> conditions,
            String div) {

        Criteria criteria = getSession().createCriteria(DayEM.class);

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

        ProjectionList pjl = Projections.projectionList();

        if ("max".equals(div)) {
            pjl.add(Projections.max("total"));
        } else if ("min".equals(div)) {
            pjl.add(Projections.min("total"));
        } else if ("avg".equals(div)) {
            pjl.add(Projections.avg("total"));
        } else if ("sum".equals(div)) {
            pjl.add(Projections.sum("total"));
        } else if ("minDate".equals(div)) {
        	pjl.add(Projections.min("id.yyyymmdd"));
        }
        
        criteria.setProjection(pjl);
        return criteria.list();

    }
    
    @Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
    public List<Object> getDayEMsCount(Set<Condition> conditions,
            String div) {

        
        DetachedCriteria  criteriaSub = DetachedCriteria.forClass(DayEM.class);
        Criteria criteria = getSession().createCriteria(DayEM.class);

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

    // day의 yyyymm의 value_xx 값의 합을 리턴 : month_em에 넣을 값.
    @Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
    public String getDaySumValueByYYYYMM(DayPk daypk) {

        StringBuffer sb = new StringBuffer();
        sb.append("\n SELECT    total               ");
        sb.append("\n FROM      DayEM                                   ");
        sb.append("\n WHERE     id.yyyymmdd = :yyyymmdd                     ");
        sb.append("\n AND       id.mdevType = :mdevType                 ");
        sb.append("\n AND       id.mdevId   = :mdevId                   ");
        sb.append("\n AND       id.channel  = :channel                  ");
        sb.append("\n AND       id.dst      = :dst                      ");

        Query query = getSession().createQuery(sb.toString()).setString(
                "yyyymmdd", daypk.getYyyymmdd()).setString("mdevType",
                daypk.getMDevType().toString()).setString("mdevId",
                daypk.getMDevId()).setInteger("channel", daypk.getChannel())
                .setInteger("dst", daypk.getDst());

        return StringUtil.nullToBlank(query.uniqueResult());
    }

    @Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
    public List<Object> getDayEMsSumList(Set<Condition> conditions) {

        Criteria criteria = getSession().createCriteria(DayEM.class);

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

        criteria.setProjection(Projections.projectionList().add(
                Projections.sum("total")).add(
                Projections.groupProperty("contract.id")));

        List<Object> result = new ArrayList<Object>();
        HashMap<Object, Object> hm = new HashMap<Object, Object>();
        Iterator it = criteria.list().iterator();
        int idx = 0;
        while (it.hasNext()) {
            Object[] objVal = (Object[]) it.next();
            hm.put(((Number) objVal[1]).intValue(), ((Number) objVal[0]).doubleValue());
            result.add(hm);
            idx++;
        }

        return result;
    }

    @Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
    public long getTotalCount(Map<String, Object> condition) {

        int supplierId = (Integer) condition.get("supplierId");
        int tariffType = (Integer) condition.get("tariffType");
        String yyyymmdd = (String) condition.get("yyyymmdd");

        //logger.info("==conditions====" + condition);

        StringBuffer sb = new StringBuffer();
        sb.append("\n SELECT    COUNT(d.id.yyyymmdd) ");
        sb.append("\n FROM      DayEM d INNER JOIN d.contract c ");
        sb.append("\n WHERE     d.id.yyyymmdd = :yyyymmdd ");
        sb.append("\n AND       d.id.mdevType = :mdevType ");
        sb.append("\n AND       d.id.channel = 1  ");
        sb.append("\n AND       d.id.dst = 0 "); // TODO
        sb.append("\n AND       c.serviceTypeCode.code = :serviceType ");
        sb
                .append("\n AND     c.serviceTypeCode.id = c.tariffIndex.serviceTypeCode.id ");
        if (supplierId > 0) {
            sb.append("\n AND       c.supplier.id = :supplierId ");
            sb.append("\n AND       c.supplier.id = c.tariffIndex.supplier.id ");
        }
        if (tariffType > 0) {
            sb.append("\n AND       c.tariffIndex.id = :tariffType ");
        }

        Query query = getSession().createQuery(sb.toString()).setString(
                "serviceType",
                CommonConstants.MeterType.EnergyMeter.getServiceType())
                .setString("yyyymmdd", yyyymmdd).setString("mdevType",
                        CommonConstants.DeviceType.Meter.name());

        if (supplierId > 0) {
            query.setInteger("supplierId", supplierId);
        }
        if (tariffType > 0) {
            query.setInteger("tariffType", tariffType);
        }

        return ((Number) query.uniqueResult()).longValue();
    }

    @Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
    public List<Object> getAbnormalContractUsageEM(Map<String, Object> condition) {

        int supplierId = (Integer) condition.get("supplierId");
        int tariffType = (Integer) condition.get("tariffType");
        String yyyymmdd = (String) condition.get("yyyymmdd");

        //logger.info("==conditions====" + condition);

        StringBuffer sb = new StringBuffer();
        sb.append("\n SELECT    c.tariffIndex.id as tarifftypeId,               ");
        sb.append("\n           c.tariffIndex.name as tarifftypeName,           ");
        sb.append("\n           SUM(                                            ");
        sb.append("\n               CASE WHEN c.contractDemand < d.total THEN 1 ");
        sb.append("\n                    ELSE 0                                 ");
        sb.append("\n               END                                         ");
        sb.append("\n           ) as count                                      ");
        sb
                .append("\n FROM      DayEM d INNER JOIN d.contract c                 ");
        sb.append("\n WHERE     d.id.yyyymmdd = :yyyymmdd                       ");
        sb.append("\n AND       d.id.mdevType = :mdevType                       ");
        sb.append("\n AND       d.id.channel = 1                       ");
        sb.append("\n AND       d.id.dst = 0 "); // TODO
        sb.append("\n AND       c.serviceTypeCode.code = :serviceType           ");
        sb.append("\n AND       c.serviceTypeCode.id = c.tariffIndex.serviceTypeCode.id ");

        if (supplierId > 0) {
            sb.append("\n AND       c.supplier.id = :supplierId ");
            sb.append("\n AND       c.supplier.id = c.tariffIndex.supplier.id ");
        }
        if (tariffType > 0) {
            sb.append("\n AND       c.tariffIndex.id = :tariffType ");
        }
        sb.append("\n GROUP BY c.tariffIndex.id, c.tariffIndex.name ");

        Query query = getSession().createQuery(sb.toString()).setString(
                "serviceType",
                CommonConstants.MeterType.EnergyMeter.getServiceType())
                .setString("yyyymmdd", yyyymmdd).setString("mdevType",
                        CommonConstants.DeviceType.Meter.name());

        if (supplierId > 0) {
            query.setInteger("supplierId", supplierId);
        }
        if (tariffType > 0) {
            query.setInteger("tariffType", tariffType);
        }
        return query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP)
                .list();
    }

    @Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
    public long getAbnormalContractUsageEMTotal(Map<String, Object> condition) {

        int supplierId = (Integer) condition.get("supplierId");
        int locationId = (Integer) condition.get("locationId");
        int tariffType = (Integer) condition.get("tariffType");
        String customerName = (String) condition.get("customerName");
        String contractNo = (String) condition.get("contractNo");
        String strWattage = (String) condition.get("wattage");
        String fromDate = (String) condition.get("fromDate");
        String toDate = (String) condition.get("toDate");

        logger.info("==conditions====" + condition);

        StringBuffer sb = new StringBuffer();
        sb.append("\n SELECT  count(d.id.yyyymmdd) as  total ");
        sb.append("\n FROM    DayEM d INNER JOIN d.contract c ");
        sb.append("\n WHERE   d.id.yyyymmdd >= :fromDate ");
        sb.append("\n AND     d.id.yyyymmdd <= :toDate   ");
        sb.append("\n AND     d.id.mdevType = :mdevType  ");
        sb.append("\n AND     d.id.channel = 1 ");
        sb.append("\n AND     d.id.dst = 0 ");
        sb.append("\n AND     c.contractDemand < d.total ");
        sb.append("\n AND     c.serviceTypeCode.code = :serviceType ");
        sb
                .append("\n AND   c.serviceTypeCode.id = c.tariffIndex.serviceTypeCode.id ");

        if (supplierId > 0) {
            sb.append("\n AND     c.supplier.id = :supplierId ");
            sb.append("\n AND     c.supplier.id = c.tariffIndex.supplier.id ");
        }
        if (!"".equals(customerName)) {
            sb.append("\n AND     c.customer.name like :customerName ");
        }
        if (!"".equals(contractNo)) {
            sb.append("\n AND     c.contractNumber = :contractNo ");
        }
        if (locationId > 0) {
            sb.append("\n AND     c.location.id = :locationId ");
        }
        if (tariffType > 0) {
            sb.append("\n AND     c.tariffIndex.id = :tariffType ");
        }
        if (!"".equals(strWattage)) {
            sb.append("\n AND     c.contractDemand >= :wattage ");
        }

        Query query = getSession().createQuery(sb.toString()).setString(
                "serviceType",
                CommonConstants.MeterType.EnergyMeter.getServiceType())
                .setString("fromDate", fromDate).setString("toDate", toDate)
                .setString("mdevType", CommonConstants.DeviceType.Meter.name());

        if (supplierId > 0) {
            query.setInteger("supplierId", supplierId);
        }
        if (locationId > 0) {
            query.setInteger("locationId", locationId);
        }
        if (tariffType > 0) {
            query.setInteger("tariffType", tariffType);
        }
        if (!"".equals(customerName)) {
            query.setString("customerName", "%" + customerName + "%");
        }
        if (!"".equals(contractNo)) {
            query.setString("contractNo", contractNo);
        }
        if (!"".equals(strWattage)) {
            double wattage = Double.parseDouble(strWattage);
            query.setDouble("wattage", wattage);
        }

        return ((Number) query.uniqueResult()).longValue();
    }

    @Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
    public List<Object> getAbnormalContractUsageEMList(
            Map<String, Object> condition) {

        int supplierId = (Integer) condition.get("supplierId");
        int locationId = (Integer) condition.get("locationId");
        int tariffType = (Integer) condition.get("tariffType");
        String customerName = (String) condition.get("customerName");
        String contractNo = (String) condition.get("contractNo");
        String strWattage = (String) condition.get("wattage");
        String fromDate = (String) condition.get("fromDate");
        String toDate = (String) condition.get("toDate");

        int page = (Integer)condition.get("page");
    	int rowPerPage = (Integer)condition.get("pageSize");
        int firstPage = page * rowPerPage;

        StringBuffer sb = new StringBuffer();
        sb.append("\n SELECT  c.customer.name as customerName    ");
        sb.append("\n        ,c.contractNumber as contractNo     ");
        sb.append("\n        ,c.tariffIndex.id as tariffId       ");
        sb.append("\n        ,c.tariffIndex.name as tariffName   ");
        sb.append("\n        ,c.contractDemand as contractUsage  ");
        sb.append("\n        ,d.total as demandUsage             ");
        sb.append("\n        ,d.id.yyyymmdd as yyyymmdd          ");
        sb.append("\n FROM    DayEM d INNER JOIN d.contract c    ");
        sb.append("\n WHERE   d.id.yyyymmdd between :fromDate and :toDate        ");
        sb.append("\n AND     d.id.mdevType = :mdevType          ");
        sb.append("\n AND     d.id.channel = 1                   ");
        sb.append("\n AND     d.id.dst = 0                   ");
        sb.append("\n AND     c.contractDemand < d.total         ");
        sb.append("\n AND     c.serviceTypeCode.code = :serviceType ");
        sb.append("\n AND     c.serviceTypeCode.id = c.tariffIndex.serviceTypeCode.id ");

        if (supplierId > 0) {
            sb.append("\n AND     c.supplier.id = :supplierId ");
            sb.append("\n AND     c.supplier.id = c.tariffIndex.supplier.id ");
        }
        if (!"".equals(customerName)) {
            sb.append("\n AND     c.customer.name like :customerName ");
        }
        if (!"".equals(contractNo)) {
            sb.append("\n AND     c.contractNumber = :contractNo ");
        }
        if (locationId > 0) {
            sb.append("\n AND     c.location.id = :locationId ");
        }
        if (tariffType > 0) {
            sb.append("\n AND     c.tariffIndex.id = :tariffType ");
        }
        if (!"".equals(strWattage)) {
            sb.append("\n AND     c.contractDemand >= :wattage ");
        }
        // sb.append("\n ORDER BY d.id.yyyymmdd, c.id, c.tariffIndex.id ");

        Query query = getSession().createQuery(sb.toString()).setString(
                "serviceType",
                CommonConstants.MeterType.EnergyMeter.getServiceType())
                .setString("fromDate", fromDate).setString("toDate", toDate)
                .setString("mdevType", CommonConstants.DeviceType.Meter.name());

        if (supplierId > 0) {
            query.setInteger("supplierId", supplierId);
        }
        if (locationId > 0) {
            query.setInteger("locationId", locationId);
        }
        if (tariffType > 0) {
            query.setInteger("tariffType", tariffType);
        }
        if (!"".equals(customerName)) {
            query.setString("customerName", "%" + customerName + "%");
        }
        if (!"".equals(contractNo)) {
            query.setString("contractNo", contractNo);
        }
        if (!"".equals(strWattage)) {
            double wattage = Double.parseDouble(strWattage);
            query.setDouble("wattage", wattage);
        }

        return query.setFirstResult(firstPage).setMaxResults(rowPerPage)
                .setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
    }

    // 미터의 검침데이터 목록을 조회한다.
    @Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
    public List<DayEM> getMeteringFailureMeteringData(Map<String, Object> params) {

        String meterId = StringUtil.nullToBlank(params.get("meterId"));
        String startDate = StringUtil
                .nullToBlank(params.get("searchStartDate"));
        String endDate = StringUtil.nullToBlank(params.get("searchEndDate"));

        Query query = getSession().createQuery("FROM DayEM d "
                                + "\n WHERE d.meter.id = ? "
                                + "\n AND d.id.channel = 1 "
                                + "\n AND d.id.dst = 0 "
                                + "\n AND d.id.yyyymmdd between ? and ?");
        query.setInteger(1, Integer.parseInt(meterId));
        query.setString(2,  startDate);
        query.setString(3,  endDate);
        return query.list();
    }

    @Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
    public List<Object[]> getDayBillingChartData(
            Map<String, String> conditionMap) {

        String startDate = conditionMap.get("startDate");
        String endDate = conditionMap.get("endDate");
        String locationCondition = conditionMap.get("locationCondition");
        String searchDateType = conditionMap.get("searchDateType");
        int supplierId = Integer.parseInt(conditionMap.get("supplierId").toString());

        StringBuilder sb = new StringBuilder()
                .append(
                        " SELECT sum(d.total), d.location.name                         ")
                .append(
                        "   FROM DayEM d                                               ")
                .append(
                        "  WHERE d.id.channel = :channel                               ")
                .append(
                        "    AND d.contract.serviceTypeCode.code = :serviceTypeCode    ")
                .append(
                        "    AND d.contract.status.code = :status                      ")
        		.append(
        				"    AND d.contract.customer.supplier.id = :supplierId                      ")
        	    .append("    AND d.id.dst = 0 ");

        if ("1".equals(searchDateType)) { // 일별
            sb.append("    AND d.id.yyyymmdd = :startDate    ");
        } else if ("3".equals(searchDateType)) { // 월별
            sb.append("    AND d.id.yyyymmdd between :startDate and :endDate  ");
        }
        if (!"".equals(locationCondition)) {
            sb.append("   AND d.location.id in (" + locationCondition + ")");
        }

        sb.append("  GROUP BY d.location.name               ");

        Query query = getSession().createQuery(sb.toString());
        query.setString("channel", DefaultChannel.Usage.getCode() + "");
        query.setString("serviceTypeCode", MeterType.EnergyMeter
                .getServiceType());
        query.setString("status", ContractStatus.NORMAL.getCode());
        query.setInteger("supplierId", supplierId);
        
        if ("1".equals(searchDateType)) {
            query.setString("startDate", startDate);
        } else if ("3".equals(searchDateType)) {
            query.setString("startDate", startDate);
            query.setString("endDate", endDate);
        }

        return query.list();
    }
//QhdQHd
    @Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
    public List<Object[]> getDayBillingGridData(Map<String, String> conditionMap) {

        String startDate = conditionMap.get("startDate");
        String endDate = conditionMap.get("endDate");
        String locationCondition = conditionMap.get("locationCondition");
        String searchDateType = conditionMap.get("searchDateType");
        int supplierId = Integer.parseInt(conditionMap.get("supplierId")); 
        
        StringBuilder sb = new StringBuilder()
                .append(" SELECT sum(d.total), count(distinct d.id.mdevId),                ")
                .append(
                        "        sum(d.baseValue+d.value_00+d.value_01+d.value_02+d.value_03+d.value_04+d.value_05+d.value_06+d.value_07+d.value_08+")
                .append(
                        "            d.value_09+d.value_10+d.value_11+d.value_12+d.value_13+d.value_14+d.value_15+d.value_16+d.value_17+d.value_18+")
                .append(
                        "            d.value_19+d.value_20+d.value_21+d.value_22+d.value_23), ")
                .append(
                        "         max(d.value_00),max(d.value_01),max(d.value_02),max(d.value_03),max(d.value_04),max(d.value_05),max(d.value_06), ")
                .append(
                        "         max(d.value_07),max(d.value_08),max(d.value_09),max(d.value_10),max(d.value_11),max(d.value_12),max(d.value_13), ")
                .append(
                        "         max(d.value_14),max(d.value_15),max(d.value_16),max(d.value_17),max(d.value_18),max(d.value_19),max(d.value_20), ")
                .append(
                        "         max(d.value_21),max(d.value_22),max(d.value_23)      ")
                .append(
                        "   FROM DayEM d                                               ")
                .append(
                        "  WHERE d.id.channel = :channel                               ")
                .append(
                        "    AND d.id.dst = 0 ")
                .append(
                        "    AND d.contract.serviceTypeCode.code = :serviceTypeCode ")
                .append(
                        "    AND d.contract.status.code = :status                   ")
		        .append(
						"    AND d.contract.customer.supplier.id = :supplierId      ");
        if ("1".equals(searchDateType)) { // 일별
            sb.append("    AND d.id.yyyymmdd = :startDate    ");
        } else if ("3".equals(searchDateType)) { // 월별
            sb.append("    AND d.id.yyyymmdd between :startDate and :endDate  ");
        }
        if (!"".equals(locationCondition)) {
            sb.append("   AND d.location.id in (" + locationCondition + ")");
        }

        Query query = getSession().createQuery(sb.toString());
        query.setString("channel", DefaultChannel.Usage.getCode() + "");
        query.setString("serviceTypeCode", MeterType.EnergyMeter
                .getServiceType());
        query.setString("status", ContractStatus.NORMAL.getCode());
        query.setInteger("supplierId", supplierId);
        
        if ("1".equals(searchDateType)) {
            query.setString("startDate", startDate);
        } else if ("3".equals(searchDateType)) {
            query.setString("startDate", startDate);
            query.setString("endDate", endDate);
        }

        try {
            query.list();
        } catch (Exception e) {
            logger.error(e, e);
        }
        return query.list();
    }

    // 수요관리 차트 데이터 조회.
    @Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
    public List<Object> getDemandManagement(Map<String, Object> condition,
            String type) {

        int supplierId = (Integer) condition.get("supplierId");
        int tariffType = (Integer) condition.get("tariffType");
        int locationId = (Integer) condition.get("locationId");
        String startDate = (String) condition.get("startDate");
        String endDate = (String) condition.get("endDate");

        logger.info("==== getDemandManagement ====\n" + condition);

        StringBuffer sb = new StringBuffer();
        if (type.equals("loadFactor")) {
            sb
                    .append("\n SELECT  MAX(d.value_00) as max_00 ,AVG(d.value_00) as avg_00 ");
        } else {
            sb.append("\n SELECT  MAX(c.location.name) as location ");
            sb.append("\n        ,MAX(c.tariffIndex.name) as tariffName ");
            sb.append("\n        ,c.tariffIndex.id as tariffIndex ");
            sb
                    .append("\n        ,MAX(d.value_00) as max_00 ,AVG(d.value_00) as avg_00 ");
        }
        sb
                .append("\n        ,MAX(d.value_01) as max_01 ,AVG(d.value_01) as avg_01 ");
        sb
                .append("\n        ,MAX(d.value_02) as max_02 ,AVG(d.value_02) as avg_02 ");
        sb
                .append("\n        ,MAX(d.value_03) as max_03 ,AVG(d.value_03) as avg_03 ");
        sb
                .append("\n        ,MAX(d.value_04) as max_04 ,AVG(d.value_04) as avg_04 ");
        sb
                .append("\n        ,MAX(d.value_05) as max_05 ,AVG(d.value_05) as avg_05 ");
        sb
                .append("\n        ,MAX(d.value_06) as max_06 ,AVG(d.value_06) as avg_06 ");
        sb
                .append("\n        ,MAX(d.value_07) as max_07 ,AVG(d.value_07) as avg_07 ");
        sb
                .append("\n        ,MAX(d.value_08) as max_08 ,AVG(d.value_08) as avg_08 ");
        sb
                .append("\n        ,MAX(d.value_09) as max_09 ,AVG(d.value_09) as avg_09 ");
        sb
                .append("\n        ,MAX(d.value_10) as max_10 ,AVG(d.value_10) as avg_10 ");
        sb
                .append("\n        ,MAX(d.value_11) as max_11 ,AVG(d.value_11) as avg_11 ");
        sb
                .append("\n        ,MAX(d.value_12) as max_12 ,AVG(d.value_12) as avg_12 ");
        sb
                .append("\n        ,MAX(d.value_13) as max_13 ,AVG(d.value_13) as avg_13 ");
        sb
                .append("\n        ,MAX(d.value_14) as max_14 ,AVG(d.value_14) as avg_14 ");
        sb
                .append("\n        ,MAX(d.value_15) as max_15 ,AVG(d.value_15) as avg_15 ");
        sb
                .append("\n        ,MAX(d.value_16) as max_16 ,AVG(d.value_16) as avg_16 ");
        sb
                .append("\n        ,MAX(d.value_17) as max_17 ,AVG(d.value_17) as avg_17 ");
        sb
                .append("\n        ,MAX(d.value_18) as max_18 ,AVG(d.value_18) as avg_18 ");
        sb
                .append("\n        ,MAX(d.value_19) as max_19 ,AVG(d.value_19) as avg_19 ");
        sb
                .append("\n        ,MAX(d.value_20) as max_20 ,AVG(d.value_20) as avg_20 ");
        sb
                .append("\n        ,MAX(d.value_21) as max_21 ,AVG(d.value_21) as avg_21 ");
        sb
                .append("\n        ,MAX(d.value_22) as max_22 ,AVG(d.value_22) as avg_22 ");
        sb
                .append("\n        ,MAX(d.value_23) as max_23 ,AVG(d.value_23) as avg_23 ");
        sb.append("\n FROM    DayEM d INNER JOIN d.contract c ");
        sb.append("\n WHERE   d.id.yyyymmdd between :startDate and :endDate    ");
        sb.append("\n AND     d.id.channel = :channel         ");
        sb.append("\n AND     d.id.mdevType = :mdevType       ");
        sb.append("\n AND     d.id.dst = 0       ");
        sb.append("\n AND     c.serviceTypeCode.code = :serviceType ");
        sb
                .append("\n AND   c.serviceTypeCode.id = c.tariffIndex.serviceTypeCode.id ");
        if (supplierId > 0) {
            sb.append("\n AND     c.supplier.id = :supplierId ");
            sb.append("\n AND     c.supplier.id = c.tariffIndex.supplier.id ");
        }
        if (tariffType > 0) {
            sb.append("\n AND     c.tariffIndex.id = :tariffType ");
        }
        if (locationId > 0) {
            sb.append("\n AND     c.location.id = :locationId ");
        }
        if (!type.equals("loadFactor")) {
            sb.append("\n GROUP BY c.location.id, c.tariffIndex.id ");
        }

        Query query = getSession().createQuery(sb.toString()).setString(
                "serviceType",
                CommonConstants.MeterType.EnergyMeter.getServiceType())
                .setString("startDate", startDate)
                .setString("endDate", endDate).setInteger("channel",
                        DefaultChannel.Usage.getCode()).setString("mdevType",
                        CommonConstants.DeviceType.Meter.name());

        if (supplierId > 0) {
            query.setInteger("supplierId", supplierId);
        }
        if (tariffType > 0) {
            query.setInteger("tariffType", tariffType);
        }
        if (locationId > 0) {
            query.setInteger("locationId", locationId);
        }

        return query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP)
                .list();
    }

    // 수요관리 차트 데이터 조회.
    @Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
    public List<Object> getDemandManagementList(Map<String, Object> condition) {

        int supplierId = (Integer) condition.get("supplierId");
        int tariffType = (Integer) condition.get("tariffType");
        List<Integer> locations = ((List<Integer>) condition.get("locations"));
        String startDate = (String) condition.get("startDate");
        String endDate = (String) condition.get("endDate");

        String searchType = (String) condition.get("searchType");

        int page = (Integer) condition.get("page");
        int pageSize = CommonConstants.Paging.ROWPERPAGE.getPageNum();
        if (condition.containsKey("pageSize")) {
            pageSize = (Integer) condition.get("pageSize");
        }

        logger.info("==== condition ====\n" + condition);

        StringBuffer sb = new StringBuffer();
        sb.append("\n SELECT  MAX(c.location.parent.id) as parent ");
        sb.append("\n        ,MAX(c.location.name) as location ");
        sb.append("\n        ,MAX(c.tariffIndex.name) as tariffName ");
        sb.append("\n        ,c.tariffIndex.id as tariffIndex ");
        sb
                .append("\n        ,MAX(d.value_00) as max_00 ,AVG(d.value_00) as avg_00 ");
        sb
                .append("\n        ,MAX(d.value_01) as max_01 ,AVG(d.value_01) as avg_01 ");
        sb
                .append("\n        ,MAX(d.value_02) as max_02 ,AVG(d.value_02) as avg_02 ");
        sb
                .append("\n        ,MAX(d.value_03) as max_03 ,AVG(d.value_03) as avg_03 ");
        sb
                .append("\n        ,MAX(d.value_04) as max_04 ,AVG(d.value_04) as avg_04 ");
        sb
                .append("\n        ,MAX(d.value_05) as max_05 ,AVG(d.value_05) as avg_05 ");
        sb
                .append("\n        ,MAX(d.value_06) as max_06 ,AVG(d.value_06) as avg_06 ");
        sb
                .append("\n        ,MAX(d.value_07) as max_07 ,AVG(d.value_07) as avg_07 ");
        sb
                .append("\n        ,MAX(d.value_08) as max_08 ,AVG(d.value_08) as avg_08 ");
        sb
                .append("\n        ,MAX(d.value_09) as max_09 ,AVG(d.value_09) as avg_09 ");
        sb
                .append("\n        ,MAX(d.value_10) as max_10 ,AVG(d.value_10) as avg_10 ");
        sb
                .append("\n        ,MAX(d.value_11) as max_11 ,AVG(d.value_11) as avg_11 ");
        sb
                .append("\n        ,MAX(d.value_12) as max_12 ,AVG(d.value_12) as avg_12 ");
        sb
                .append("\n        ,MAX(d.value_13) as max_13 ,AVG(d.value_13) as avg_13 ");
        sb
                .append("\n        ,MAX(d.value_14) as max_14 ,AVG(d.value_14) as avg_14 ");
        sb
                .append("\n        ,MAX(d.value_15) as max_15 ,AVG(d.value_15) as avg_15 ");
        sb
                .append("\n        ,MAX(d.value_16) as max_16 ,AVG(d.value_16) as avg_16 ");
        sb
                .append("\n        ,MAX(d.value_17) as max_17 ,AVG(d.value_17) as avg_17 ");
        sb
                .append("\n        ,MAX(d.value_18) as max_18 ,AVG(d.value_18) as avg_18 ");
        sb
                .append("\n        ,MAX(d.value_19) as max_19 ,AVG(d.value_19) as avg_19 ");
        sb
                .append("\n        ,MAX(d.value_20) as max_20 ,AVG(d.value_20) as avg_20 ");
        sb
                .append("\n        ,MAX(d.value_21) as max_21 ,AVG(d.value_21) as avg_21 ");
        sb
                .append("\n        ,MAX(d.value_22) as max_22 ,AVG(d.value_22) as avg_22 ");
        sb
                .append("\n        ,MAX(d.value_23) as max_23 ,AVG(d.value_23) as avg_23 ");
        sb.append("\n FROM    DayEM d INNER JOIN d.contract c ");
        sb.append("\n WHERE   d.id.yyyymmdd between :startDate and :endDate    ");
        sb.append("\n AND     d.id.channel = :channel         ");
        sb.append("\n AND     d.id.mdevType = :mdevType       ");
        sb.append("\n AND     d.id.dst = 0       ");
        sb.append("\n AND     c.serviceTypeCode.code = :serviceType ");
        sb
                .append("\n AND   c.serviceTypeCode.id = c.tariffIndex.serviceTypeCode.id ");
        sb.append("\n AND     c.location.id IN (:locations) ");
        if (supplierId > 0) {
            sb.append("\n AND     c.supplier.id = :supplierId ");
            sb.append("\n AND     c.supplier.id = c.tariffIndex.supplier.id ");
        }
        if (tariffType > 0) {
            sb.append("\n AND     c.tariffIndex.id = :tariffType ");
        }
        sb.append("\n GROUP BY c.location.id, c.tariffIndex.id ");

        Query query = getSession().createQuery(sb.toString()).setString(
                "serviceType",
                CommonConstants.MeterType.EnergyMeter.getServiceType())
                .setString("startDate", startDate)
                .setString("endDate", endDate).setInteger("channel",
                        DefaultChannel.Usage.getCode()).setString("mdevType",
                        CommonConstants.DeviceType.Meter.name())
                .setParameterList("locations", locations);

        if (supplierId > 0) {
            query.setInteger("supplierId", supplierId);
        }
        if (tariffType > 0) {
            query.setInteger("tariffType", tariffType);
        }

        List<Object> list = query.setResultTransformer(
                Transformers.ALIAS_TO_ENTITY_MAP).list();

        int fromIdx = page * pageSize;
        int toIdx = (page + 1) * pageSize;
        toIdx = list.size() < toIdx ? list.size() : toIdx;

        List<Object> returnList = list.subList(fromIdx, toIdx);
        if ("search".equals(searchType)) {
            returnList.add(list.size());
        }
        return returnList;
    }

    @Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
    public List<DayEM> getElecCustomerBillingGridData(
            Map<String, String> conditionMap) {

        String startDate = conditionMap.get("startDate");
        String endDate = conditionMap.get("endDate");
        String searchDateType = conditionMap.get("searchDateType");
        String locationCondition = conditionMap.get("locationCondition");
        String tariffIndex = conditionMap.get("tariffIndex");
        String customerName = conditionMap.get("customerName");
        String contractNo = conditionMap.get("contractNo");
        String meterName = conditionMap.get("meterName");

        int page = Integer.parseInt(conditionMap.get("page"));
        int pageSize = Integer.parseInt(conditionMap.get("pageSize"));

        StringBuilder sb = new StringBuilder()
                .append("         from DayEM d                                         ")
                .append(
                        "  WHERE d.id.channel = :channel                               ")
                .append(
                        "  WHERE d.id.dst = 0                               ")
                .append(
                        "    AND d.contract.serviceTypeCode.code = :serviceTypeCode ")
                .append(
                        "    AND d.contract.status.code = :status                   ");

        if ("1".equals(searchDateType)) { // 일별
            sb.append("    AND d.id.yyyymmdd = :startDate    ");
        } else if ("3".equals(searchDateType)) { // 월별
            sb.append("    AND d.id.yyyymmdd between :startDate and :endDate  ");
        }
        if (!"".equals(locationCondition)) {
            sb.append("   AND d.location.id in (" + locationCondition + ")");
        }
        if (!"".equals(tariffIndex)) {
            sb.append("   AND d.contract.tariffIndex = :tariffIndex ");
        }
        if (!"".equals(customerName)) {
            sb.append("   AND d.contract.customer.name = :customerName ");
        }
        if (!"".equals(contractNo)) {
            sb.append("   AND d.contract.contractNumber = :contractNo ");
        }
        if (!"".equals(meterName)) {
            sb.append("   AND d.meter.mdsId = :meterName ");
        }

        Query query = getSession().createQuery(sb.toString());
        query.setString("channel", DefaultChannel.Usage.getCode() + "");
        query.setString("serviceTypeCode", MeterType.EnergyMeter
                .getServiceType());
        query.setString("status", ContractStatus.NORMAL.getCode());

        if ("1".equals(searchDateType)) {
            query.setString("startDate", startDate);
        } else if ("3".equals(searchDateType)) {
            query.setString("startDate", startDate);
            query.setString("endDate", endDate);
        }

        if (!"".equals(tariffIndex)) {
            query.setString("tariffIndex", tariffIndex);
        }
        if (!"".equals(customerName)) {
            query.setString("customerName", customerName);
        }
        if (!"".equals(contractNo)) {
            query.setString("contractNo", contractNo);
        }
        if (!"".equals(meterName)) {
            query.setString("meterName", meterName);
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
        sb.append("FROM DayEM d ");
        sb.append("WHERE d.id.channel = :channel ");
        sb.append("AND   d.id.dst = 0 ");
        sb.append("AND   d.contract.serviceTypeCode.code = :serviceTypeCode ");
        sb.append("AND   d.contract.status.code = :status ");
        sb.append("AND   d.contract.customer.supplier.id = :supplierId ");

        if ("1".equals(searchDateType)) { // 일별
            sb.append("AND d.id.yyyymmdd = :startDate ");
        } else if ("3".equals(searchDateType)) { // 월별
            sb.append("AND   d.id.yyyymmdd between :startDate and :endDate ");
        }
        if (!"".equals(locationCondition)) {
            sb.append("AND   d.location.id in (").append(locationCondition).append(") ");
        }
        if (!"".equals(tariffIndex)) {
            sb.append("AND   d.contract.tariffIndex = :tariffIndex ");
        }
        if (!"".equals(customerName)) {
            sb.append("AND   d.contract.customer.name LIKE :customerName ");
        }
        if (!"".equals(contractNo)) {
            sb.append("AND   d.contract.contractNumber LIKE :contractNo ");
        }
        if (!"".equals(meterName)) {
            sb.append("AND   d.meter.mdsId LIKE :meterName ");
        }

        Query query = getSession().createQuery(sb.toString());
        // criteria.setProjection(Projections.rowCount());

        query.setInteger("channel", DefaultChannel.Usage.getCode());
        query.setString("serviceTypeCode", MeterType.EnergyMeter.getServiceType());
        query.setString("status", ContractStatus.NORMAL.getCode());
        query.setInteger("supplierId", supplierId);
        
        if ("1".equals(searchDateType)) {
            query.setString("startDate", startDate);
        } else if ("3".equals(searchDateType)) {
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

        return ((Number) query.uniqueResult()).longValue();
    }

    /*
     * public int getTotalLoadDurationChartByListCondition(Set<Condition>
     * conditions) { DetachedCriteria criteria =
     * DetachedCriteria.forClass(DayEM.class); if(conditions != null) { Iterator
     * it = conditions.iterator(); while(it.hasNext()){ Condition condition =
     * (Condition)it.next(); Criterion addCriterion =
     * SearchCondition.getCriterion(condition);
     * 
     * if(addCriterion != null){ criteria.add(addCriterion); } }
     * 
     * } criteria.setProjection(Projections.projectionList() .add(
     * Projections.groupProperty("id.contract") ) .add(
     * Projections.groupProperty("id.meter") ) );
     * 
     * return getSession().findByCriteria(criteria).size(); }
     */
    @Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
    public List<DayEM> getDayCustomerBillingGridData(Map<String, Object> conditionMap) {

        String startDate = (String) conditionMap.get("startDate");
        String endDate = (String) conditionMap.get("endDate");
        String searchDateType = (String) conditionMap.get("searchDateType");
        String locationCondition = (String) conditionMap.get("locationCondition");
        String tariffIndex = (String) conditionMap.get("tariffIndex");
        String customerName = (String) conditionMap.get("customerName");
        String contractNo = (String) conditionMap.get("contractNo");
        String meterName = (String) conditionMap.get("meterName");
        int supplierId = Integer.parseInt(conditionMap.get("supplierId").toString());
        
        int page = Integer.parseInt((String) conditionMap.get("page"));
        int pageSize = Integer.parseInt((String) conditionMap.get("pageSize"));

        StringBuilder sb = new StringBuilder();
        sb.append("from DayEM d ");
        sb.append("WHERE d.id.channel = :channel ");
        sb.append("AND   d.id.dst = 0 ");
        sb.append("AND   d.contract.serviceTypeCode.code = :serviceTypeCode ");
        sb.append("AND   d.contract.status.code = :status ");
        sb.append("AND   d.contract.customer.supplier.id = :supplierId      ");	

        if ("1".equals(searchDateType)) { // 일별
            sb.append("AND   d.id.yyyymmdd = :startDate ");
        } else if ("3".equals(searchDateType)) { // 월별
            sb.append("AND   d.id.yyyymmdd between :startDate and :endDate ");
        }
        if (!"".equals(locationCondition)) {
            sb.append("AND   d.location.id IN (").append(locationCondition).append(") ");
        }
        if (!"".equals(tariffIndex)) {
            sb.append("AND   d.contract.tariffIndex = :tariffIndex ");
        }
        if (!"".equals(customerName)) {
            sb.append("AND   d.contract.customer.name LIKE :customerName ");
        }
        if (!"".equals(contractNo)) {
            sb.append("AND   d.contract.contractNumber LIKE :contractNo ");
        }
        if (!"".equals(meterName)) {
            sb.append("AND   d.meter.mdsId LIKE :meterName ");
        }

        Query query = getSession().createQuery(sb.toString());
        query.setInteger("channel", DefaultChannel.Usage.getCode());
        query.setString("serviceTypeCode", MeterType.EnergyMeter.getServiceType());
        query.setString("status", ContractStatus.NORMAL.getCode());
        query.setInteger("supplierId", supplierId);
        
        if ("1".equals(searchDateType)) {
            query.setString("startDate", startDate);
        } else if ("3".equals(searchDateType)) {
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

    /**
     * BEMS 전력사용량 , 탄소배출량 차트 데이터 조회.
     */
    @Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
    public List<Object> getConsumptionMonitoringManagement(
            Map<String, Object> condition) {

        logger.info("BEMS 전력사용량 , 탄소배출량  차트 데이터 조회.\n==== conditions ====\n"
                + condition);

        int supplierId = (Integer) condition.get("supplierId");

        StringBuffer sb = new StringBuffer();
        sb.append("\n SELECT c.mdsId , b.id , b.name FROM Meter c , (  ");
        sb.append("\n   SELECT id , name FROM Location WHERE parent.id IN  ");
        sb.append("\n   ( ");
        sb
                .append("\n         SELECT a.id FROM Location a WHERE a.supplier.id = :supplierId AND a.parent.id IS NULL");
        sb.append("\n   ) ");
        sb.append("\n ) AS b WHERE c.location.id = b.id ");

        Query query = getSession().createQuery(sb.toString());
        query.setInteger("supplierId", supplierId);

        return query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP)
                .list();
    }

    /**
     * BEMS 데이터 조회를 위한 supplierId를 이용하여 Root Location 정보 조회
     */
    @Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
    public List<Object> getRootLocationId(Map<String, Object> condition) {

        logger
                .info("BEMS 데이터 조회를 위한 Root Location 정보 조회\n==== conditions ====\n"
                        + condition);

        String supplierId = (String) condition.get("supplierId");

        StringBuffer sb = new StringBuffer();

        sb
                .append(" SELECT ID FROM LOCATION WHERE SUPPLIER_ID = :supplierId AND PARENT_ID IS NULL ");

        SQLQuery query = getSession().createSQLQuery(sb.toString());

        query.setInteger("supplierId", Integer.parseInt(supplierId));

        return query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP)
                .list();
    }

    /**
     * BEMS 전력사용량 , 탄소배출량 차트 데이터 조회.
     */
    @Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
    public List<Object> getConsumptionMonitoring(Map<String, Object> condition) {

        logger.info("\n==== conditions ====\n" + condition);

        String searchDateType = ObjectUtils.defaultIfNull(
                condition.get("searchDateType"),
                CommonConstants.DateType.DAILY.getCode()).toString(); // 일 , 주 ,
                                                                        // 월 ,
                                                                        // 분기
        @SuppressWarnings("unused")
        Integer supplierId = (Integer) condition.get("supplierId");
        Integer locationId = (Integer) condition.get("locationId");
        String channelType = "1"; // 탄소일 경우만 0 , 전력/온도/습도의 사용량일때는 1
        String startDate = (String) condition.get("startDate");
        String endDate = (String) condition.get("endDate");

        StringBuffer sb = new StringBuffer();

        sb
                .append("\n SELECT LL.ID AS LOCATION_ID , LL.NAME , LL.MDS_ID , D.TOTAL , D.CHANNEL ");
        sb.append("\n   FROM DAY_EM D , ( ");
        sb.append("\n       SELECT M.MDS_ID , L.ID , L.NAME FROM METER M , ( ");
        sb
                .append("\n             SELECT ID , NAME FROM LOCATION WHERE PARENT_ID = :parentId ");
        sb.append("\n       ) L WHERE M.LOCATION_ID = L.ID ");
        // sb.append("\n           AND M.METERTYPE_ID = (  ");
        // sb.append("\n                SELECT CC.id FROM code AS CC WHERE CC.parent_id = ( ");
        // sb.append("\n                    SELECT C.id FROM code AS C WHERE C.CODE = '1.3.1' ");
        // // MeterType
        // sb.append("\n                ) AND CC.CODE = '1.3.1.1' "); // EnergyMeter
        // sb.append("\n            ) ");
        sb.append("\n     ) LL WHERE D.MDEV_ID = LL.MDS_ID AND D.CHANNEL = :channelType ");
        sb.append("\n AND D.DST = 0 ");

        if (CommonConstants.DateType.DAILY.getCode().equals(searchDateType)) { // 일별
            sb.append("    AND d.yyyymmdd = :startDate    ");
            // sb.append("\n  AND D.YYYYMMDD='20100517' ");
        } else if (CommonConstants.DateType.WEEKLY.getCode().equals(
                searchDateType)
                || CommonConstants.DateType.MONTHLY.getCode().equals(
                        searchDateType)
                || CommonConstants.DateType.QUARTERLY.getCode().equals(
                        searchDateType)) { // 주/월/분기별
            sb.append("    AND d.yyyymmdd between :startDate and :endDate  ");
        }

        SQLQuery query = getSession().createSQLQuery(sb.toString());

        query.setInteger("parentId", locationId);
        query.setInteger("channelType", Integer.parseInt(channelType));

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
     * BEMS 주기에 따른 빌딩의 TOTAL 사용량 , TOTAL 탄소배출량 차트 데이터 조회.
     */
    @Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
    public List<Object> getConsumptionEmCo2SearchDayTypeTotal(
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
        // String channelType = (String)condition.get("channelType"); // 탄소일 경우만
        // 0 , 전력/온도/습도의 사용량일때는 1
        String startDate = (String) condition.get("startDate");
        String endDate = (String) condition.get("endDate");

        StringBuffer sb = new StringBuffer();

        sb
                .append("\n SELECT EM.SUPPLIER_ID , EM.TOTAL , CO2.CO2_TOTAL FROM (  ");
        sb
                .append("\n     SELECT LL.SUPPLIER_ID , SUM(D.TOTAL) TOTAL FROM DAY_EM D , ( ");
        sb
                .append("\n         SELECT M.MDS_ID , L.ID , L.NAME , L.SUPPLIER_ID FROM METER M , (  ");
        sb
                .append("\n             SELECT ID , NAME , SUPPLIER_ID FROM LOCATION WHERE PARENT_ID = :parentId ");
        sb.append("\n       ) L WHERE M.LOCATION_ID = L.ID ");
        // sb.append("\n           AND M.METERTYPE_ID = (  ");
        // sb.append("\n                SELECT CC.id FROM code AS CC WHERE CC.parent_id = ( ");
        // sb.append("\n                    SELECT C.id FROM code AS C WHERE C.CODE = '1.3.1' ");
        // // MeterType
        // sb.append("\n                ) AND CC.CODE = '1.3.1.1' "); // EnergyMeter
        // sb.append("\n            ) ");
        sb.append("\n   ) LL WHERE D.MDEV_ID = LL.MDS_ID AND D.CHANNEL=1 ");
        sb.append("\n   AND D.DST = 0 ");
        if (CommonConstants.DateType.DAILY.getCode().equals(searchDateType)) { // 일별
            sb.append("    AND D.YYYYMMDD = :startDate    ");
            // sb.append("\n  AND D.YYYYMMDD='20100517' ");
        } else if (CommonConstants.DateType.WEEKLY.getCode().equals(
                searchDateType)
                || CommonConstants.DateType.MONTHLY.getCode().equals(
                        searchDateType)
                || CommonConstants.DateType.QUARTERLY.getCode().equals(
                        searchDateType)) { // 주/월/분기별
            sb.append("    AND D.YYYYMMDD between :startDate and :endDate  ");
        }
        sb.append("\n   GROUP BY LL.SUPPLIER_ID  ");
        sb.append("\n ) EM ");
        sb.append("\n LEFT JOIN ");
        sb.append("\n ( ");
        sb
                .append("\n     SELECT LL2.SUPPLIER_ID , SUM(D2.TOTAL) AS CO2_TOTAL FROM DAY_EM D2 , ( ");
        sb
                .append("\n         SELECT M2.MDS_ID , L2.ID , L2.NAME , L2.SUPPLIER_ID FROM METER M2 , (  ");
        sb
                .append("\n             SELECT ID , NAME , SUPPLIER_ID FROM LOCATION WHERE PARENT_ID = :parentId ");
        sb.append("\n       ) L2 WHERE M2.LOCATION_ID = L2.ID ");
        // sb.append("\n           AND M2.METERTYPE_ID = (  ");
        // sb.append("\n                SELECT CC.id FROM code AS CC WHERE CC.parent_id = ( ");
        // sb.append("\n                    SELECT C.id FROM code AS C WHERE C.CODE = '1.3.1' ");
        // // MeterType
        // sb.append("\n                ) AND CC.CODE = '1.3.1.1' "); // EnergyMeter
        // sb.append("\n            ) ");
        sb.append("\n     ) LL2 WHERE D2.MDEV_ID = LL2.MDS_ID AND D2.CHANNEL=0 ");
        sb.append("\n       AND D2.DST = 0 ");
        if (CommonConstants.DateType.DAILY.getCode().equals(searchDateType)) { // 일별
            sb.append("    AND D2.YYYYMMDD = :startDate    ");
            // sb.append("\n  AND D2.YYYYMMDD='20100517' ");
        } else if (CommonConstants.DateType.WEEKLY.getCode().equals(
                searchDateType)
                || CommonConstants.DateType.MONTHLY.getCode().equals(
                        searchDateType)
                || CommonConstants.DateType.QUARTERLY.getCode().equals(
                        searchDateType)) { // 주/월/분기별
            sb.append("    AND D2.YYYYMMDD between :startDate and :endDate  ");
        }
        sb.append("\n   GROUP BY LL2.SUPPLIER_ID  ");
        sb.append("\n ) CO2 ");
        sb.append("\n ON EM.SUPPLIER_ID = CO2.SUPPLIER_ID ");

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
     * BEMS 동별 일(시간)전력사용량 , 탄소배출량 차트 데이터 조회. 기준 데이터 키로 Location 테이블의 PARENT_ID를
     * 사용.
     */
    @Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
    public List<Object> getConsumptionEmCo2DayMonitoringParentId(
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

        sb.append("\n   SELECT LL.ORDERNO , LL.ID AS LOCATION_ID , LL.NAME , LL.MDS_ID , D.CHANNEL , SUM(D.TOTAL) TOTAL FROM "+ meterType + " D , ( ");
        sb.append("\n        SELECT M.MDS_ID , L.ID , L.NAME , L.ORDERNO FROM METER M , (  ");
        sb.append("\n           SELECT ID , NAME , ORDERNO  FROM LOCATION WHERE PARENT_ID = :parentId ");
        sb.append("\n   ) L WHERE M.LOCATION_ID = L.ID ");
        sb.append("\n   ) LL WHERE D.MDEV_ID = LL.MDS_ID AND D.CHANNEL=:channel ");
        sb.append("\n     AND D.DST = 0 ");
        
        if (CommonConstants.DateType.DAILY.getCode().equals(searchDateType)) { // 일별
            sb.append("    AND D.YYYYMMDD = :startDate    ");
        } else if (CommonConstants.DateType.WEEKLY.getCode().equals(searchDateType)) { // 주/월/분기별
            sb.append("    AND D.YYYYMMDD between :startDate and :endDate  ");
        }
        sb.append("\n   GROUP BY LL.ORDERNO, LL.ID , LL.NAME ,  LL.MDS_ID , D.CHANNEL , D.MDEV_ID   ");
        // sb.append("   ORDER BY LL.ORDERNO  ASC, LL.ID DESC");

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

    @Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
    public List<Object> getConsumptionEmCo2DayValuesParentId(Map<String, Object> condition) {

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
        sb.append("\n    FROM DAY_EM D ");
        sb.append("\n    INNER JOIN (SELECT ID FROM LOCATION WHERE PARENT_ID=:parentId) L ");
        sb.append("\n    ON D.LOCATION_ID = L.ID ");
        sb.append("\n    WHERE D.CHANNEL=:channel ");
        sb.append("\n    AND D.DST = 0 ");
        sb.append("\n    AND D.YYYYMMDD = :startDate ");

        SQLQuery query = getSession().createSQLQuery(sb.toString());

        query.setInteger("parentId", locationId);
        query.setInteger("channel", channel);

        query.setString("startDate", startDate);

        return query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP)
                .list();
    }

    @Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
    public List<Object> getConsumptionEmCo2DayValuesLocationId(Map<String, Object> condition) {

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
        sb.append("\n    FROM DAY_EM D ");
        sb.append("\n    INNER JOIN (SELECT ID FROM LOCATION WHERE ID=:parentId) L ");
        sb.append("\n    ON D.LOCATION_ID = L.ID ");
        sb.append("\n    WHERE D.CHANNEL=:channel ");
        sb.append("\n    AND D.DST = 0 ");
        sb.append("\n    AND D.YYYYMMDD = :startDate ");

        SQLQuery query = getSession().createSQLQuery(sb.toString());

        query.setInteger("parentId", locationId);
        query.setInteger("channel", channel);

        query.setString("startDate", startDate);

        return query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP)
                .list();
    }
    /**
     * BEMS 동별 일(시간)전력사용량 , 탄소배출량 차트 데이터 조회. 기준 데이터 키로 Location 테이블의 Id를 사용.
     */
    @Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
    public List<Object> getConsumptionEmCo2DayMonitoringLocationId(
            Map<String, Object> condition) {

        logger
                .info("BEMS 동별   일(시간)전력사용량 , 탄소배출량  차트 데이터 조회.\n==== conditions ====\n"
                        + condition);

        String searchDateType = ObjectUtils.defaultIfNull(
                condition.get("searchDateType"),
                CommonConstants.DateType.DAILY.getCode()).toString(); // 일 , 주 ,
                                                                        // 월 ,
                                                                        // 분기
        @SuppressWarnings("unused")
        String supplierId = ObjectUtils.defaultIfNull(
                condition.get("supplierId"), "0").toString();
        String locationId = ObjectUtils.defaultIfNull(
                condition.get("locationId"), "0").toString();
        // String channelType = (String)condition.get("channelType"); // 탄소일 경우만
        // 0 , 전력/온도/습도의 사용량일때는 1
        String startDate = (String) condition.get("startDate");
        String endDate = (String) condition.get("endDate");

        StringBuffer sb = new StringBuffer();

        sb
                .append("\n SELECT EM.LOCATION_ID , EM.NAME , EM.MDS_ID , EM.TOTAL , CO2.CO2_TOTAL FROM (  ");
        sb
                .append("\n     SELECT LL.ID AS LOCATION_ID , LL.NAME , LL.MDS_ID , D.CHANNEL , SUM(D.TOTAL) TOTAL FROM DAY_EM D , ( ");
        sb
                .append("\n         SELECT M.MDS_ID , L.ID , L.NAME FROM METER M , LOCATION L ");
        sb.append("\n        WHERE M.LOCATION_ID = L.ID ");
        sb.append("\n          AND L.ID = :locationId ");
        // sb.append("\n           AND M.METERTYPE_ID = (  ");
        // sb.append("\n                SELECT CC.id FROM code AS CC WHERE CC.parent_id = ( ");
        // sb.append("\n                    SELECT C.id FROM code AS C WHERE C.CODE = '1.3.1' ");
        // // MeterType
        // sb.append("\n                ) AND CC.CODE = '1.3.1.1' "); // EnergyMeter
        // sb.append("\n            ) ");
        sb.append("\n   ) LL WHERE D.MDEV_ID = LL.MDS_ID AND D.CHANNEL=1 ");
        sb.append("\n        AND D.DST = 0 ");
        if (CommonConstants.DateType.DAILY.getCode().equals(searchDateType)) { // 일별
            sb.append("    AND D.YYYYMMDD = :startDate    ");
            // sb.append("\n  AND D.YYYYMMDD='20100517' ");
        } else if (CommonConstants.DateType.WEEKLY.getCode().equals(
                searchDateType)
                || CommonConstants.DateType.MONTHLY.getCode().equals(
                        searchDateType)
                || CommonConstants.DateType.QUARTERLY.getCode().equals(
                        searchDateType)) { // 주/월/분기별
            sb.append("    AND D.YYYYMMDD between :startDate and :endDate  ");
        }
        sb
                .append("\n     GROUP BY LL.ID , LL.NAME , LL.MDS_ID , D.CHANNEL , D.MDEV_ID  ");
        sb.append("\n ) EM ");
        sb.append("\n LEFT JOIN ");
        sb.append("\n ( ");
        sb
                .append("\n     SELECT LL2.ID AS LOCATION_ID , SUM(D2.TOTAL) AS CO2_TOTAL FROM DAY_EM D2 , ( ");
        sb
                .append("\n         SELECT M2.MDS_ID , L2.ID , L2.NAME FROM METER M2 , LOCATION L2 ");
        sb.append("\n        WHERE M2.LOCATION_ID = L2.ID ");
        sb.append("\n          AND L2.ID = :locationId ");
        // sb.append("\n           AND M2.METERTYPE_ID = (  ");
        // sb.append("\n                SELECT CC.id FROM code AS CC WHERE CC.parent_id = ( ");
        // sb.append("\n                    SELECT C.id FROM code AS C WHERE C.CODE = '1.3.1' ");
        // // MeterType
        // sb.append("\n                ) AND CC.CODE = '1.3.1.1' "); // EnergyMeter
        // sb.append("\n            ) ");
        sb.append("\n     ) LL2 WHERE D2.MDEV_ID = LL2.MDS_ID AND D2.CHANNEL=0 ");
        sb.append("\n       AND D2.DST = 0 ");
        if (CommonConstants.DateType.DAILY.getCode().equals(searchDateType)) { // 일별
            sb.append("    AND D2.YYYYMMDD = :startDate    ");
            // sb.append("\n  AND D2.YYYYMMDD='20100517' ");
        } else if (CommonConstants.DateType.WEEKLY.getCode().equals(
                searchDateType)
                || CommonConstants.DateType.MONTHLY.getCode().equals(
                        searchDateType)
                || CommonConstants.DateType.QUARTERLY.getCode().equals(
                        searchDateType)) { // 주/월/분기별
            sb.append("    AND D2.YYYYMMDD between :startDate and :endDate  ");
        }
        sb.append("\n   GROUP BY LL2.ID , D2.MDEV_ID  ");
        sb.append("\n ) CO2 ");
        sb.append("\n ON EM.LOCATION_ID = CO2.LOCATION_ID ");

        SQLQuery query = getSession().createSQLQuery(sb.toString());

        query.setInteger("locationId", Integer.parseInt(locationId));

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
     * BEMS 전력사용량 , 탄소배출량 차트 최대/최소 데이터 조회. 기준키로 location 테이블의 id를 사용 일(시간) 빌딩 전체
     * : 전력사용량/탄솝출량 의 최대 ,최소,합
     */ 
    @Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
    public List<Object> getConsumptionEmCo2MonitoringSumMinMaxLocationId(
            Map<String, Object> condition) {

        logger
                .info("일(시간) 빌딩 전체 : 전력사용량/탄솝출량 의 최대 ,최소,합\n==== conditions ====\n"
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
        String startDate = (String) condition.get("startDate");
        @SuppressWarnings("unused")
        String endDate = (String) condition.get("endDate");

        @SuppressWarnings("unused")
        String[] dataType = { "SUM" };
        
        StringBuilder sb = new StringBuilder();
        sb.append("     from DayEM d  ");
        sb.append("  WHERE d.id.channel = 1      ");
        sb.append("    AND d.id.dst = 0 ");
        sb.append("    AND d.id.yyyymmdd = :startDate    ");
        sb.append("    AND d.location.id = :locationId    ");
    
        Query query = getSession().createQuery(sb.toString());
        query.setInteger("locationId", locationId);
        query.setString("startDate", startDate);
        
        List<DayEM> list = query.list();
        
        List<Object> retList = new ArrayList<Object>();
        
        for(DayEM dayEM:list){
            HashMap<String, Object> temp = new HashMap<String,Object>();
            for(int i=0;i<24;i++){
                
                String setTime = StringUtils.leftPad(String.valueOf(i), 2,"0");
                String val ="0";
                try {
                     val = (String)ObjectUtils.defaultIfNull(BeanUtils.getProperty(dayEM, "value_"+setTime),"0");
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                }
                temp.put("EM_SUM_" + setTime, Double.parseDouble(val));
                @SuppressWarnings("unused")
                double check = Double.valueOf(ObjectUtils.defaultIfNull(
                temp.get("EM_SUM_" + setTime), "0").toString());
            }
            
            temp.put("EM_TOTAL", dayEM.getTotal());
            retList.add(temp);
        }
        
        return retList;
    }
    
    /**
     * BEMS 전력사용량 , 탄소배출량 차트 최대/최소 데이터 조회. 기준키로 location 테이블의 id를 사용 일(시간) 빌딩 전체
     * : 전력사용량/탄솝출량 의 최대 ,최소,합
     */
    @Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
    public List<Object> getConsumptionEmCo2MonitoringSumMinMaxLocationId_bck(
            Map<String, Object> condition) {

        logger
                .info("일(시간) 빌딩 전체 : 전력사용량/탄솝출량 의 최대 ,최소,합\n==== conditions ====\n"
                        + condition);

        String searchDateType = ObjectUtils.defaultIfNull(
                condition.get("searchDateType"),
                CommonConstants.DateType.DAILY.getCode()).toString(); // 일 , 주 ,
                                                                        // 월 ,
                                                                        // 분기
        @SuppressWarnings("unused")
        Integer supplierId = (Integer) condition.get("supplierId");
        Integer locationId = (Integer) condition.get("locationId");
        String startDate = (String) condition.get("startDate");
        @SuppressWarnings("unused")
        String endDate = (String) condition.get("endDate");

        String[] dataType = { "MIN", "MAX", "SUM" };

        StringBuffer sb = new StringBuffer();

        sb.append("\n SELECT EM.* , CO2.* FROM (  ");
        sb.append("\n   SELECT LL.ID AS EM_PARENT_ID  ");
        sb.append("\n        , SUM(D.TOTAL) AS EM_TOTAL  ");
        for (int i = 0; i < dataType.length; i++) {

            sb.append("\n , " + dataType[i] + "(D.VALUE_00) AS EM_"
                    + dataType[i] + "_00 , " + dataType[i]
                    + "(D.VALUE_01) AS EM_" + dataType[i] + "_01 , "
                    + dataType[i] + "(D.VALUE_02) AS EM_" + dataType[i]
                    + "_02 , " + dataType[i] + "(D.VALUE_03) AS EM_"
                    + dataType[i] + "_03 , " + dataType[i]
                    + "(D.VALUE_04) AS EM_" + dataType[i] + "_04 ");
            sb.append("\n , " + dataType[i] + "(D.VALUE_05) AS EM_"
                    + dataType[i] + "_05 , " + dataType[i]
                    + "(D.VALUE_06) AS EM_" + dataType[i] + "_06 , "
                    + dataType[i] + "(D.VALUE_07) AS EM_" + dataType[i]
                    + "_07 , " + dataType[i] + "(D.VALUE_08) AS EM_"
                    + dataType[i] + "_08 , " + dataType[i]
                    + "(D.VALUE_09) AS EM_" + dataType[i] + "_09 ");
            sb.append("\n , " + dataType[i] + "(D.VALUE_10) AS EM_"
                    + dataType[i] + "_10 , " + dataType[i]
                    + "(D.VALUE_11) AS EM_" + dataType[i] + "_11 , "
                    + dataType[i] + "(D.VALUE_12) AS EM_" + dataType[i]
                    + "_12 , " + dataType[i] + "(D.VALUE_13) AS EM_"
                    + dataType[i] + "_13 , " + dataType[i]
                    + "(D.VALUE_14) AS EM_" + dataType[i] + "_14 ");
            sb.append("\n , " + dataType[i] + "(D.VALUE_15) AS EM_"
                    + dataType[i] + "_15 , " + dataType[i]
                    + "(D.VALUE_16) AS EM_" + dataType[i] + "_16 , "
                    + dataType[i] + "(D.VALUE_17) AS EM_" + dataType[i]
                    + "_17 , " + dataType[i] + "(D.VALUE_18) AS EM_"
                    + dataType[i] + "_18 , " + dataType[i]
                    + "(D.VALUE_19) AS EM_" + dataType[i] + "_19 ");
            sb.append("\n , " + dataType[i] + "(D.VALUE_20) AS EM_"
                    + dataType[i] + "_20 , " + dataType[i]
                    + "(D.VALUE_21) AS EM_" + dataType[i] + "_21 , "
                    + dataType[i] + "(D.VALUE_22) AS EM_" + dataType[i]
                    + "_22 , " + dataType[i] + "(D.VALUE_23) AS EM_"
                    + dataType[i] + "_23 ");
        }

        sb.append("\n   FROM DAY_EM D , ( ");
        sb
                .append("\n         SELECT M.MDS_ID , L.ID , L.NAME , L.PARENT_ID FROM METER M , LOCATION L WHERE M.LOCATION_ID = L.ID AND L.ID = :locationId ");
        // sb.append("\n           AND M.METERTYPE_ID = (  ");
        // sb.append("\n                SELECT CC.id FROM code AS CC WHERE CC.parent_id = ( ");
        // sb.append("\n                    SELECT C.id FROM code AS C WHERE C.CODE = '1.3.1' ");
        // // MeterType
        // sb.append("\n                ) AND CC.CODE = '1.3.1.1' "); // EnergyMeter
        // sb.append("\n            ) ");
        sb.append("\n   )LL WHERE D.MDEV_ID = LL.MDS_ID AND D.CHANNEL=1 AND D.DST = 0 ");
        if (CommonConstants.DateType.DAILY.getCode().equals(searchDateType)) { // 일별
            sb.append("\n    AND D.YYYYMMDD = :startDate    ");
            // } else if
            // (CommonConstants.DateType.WEEKLY.getCode().equals(searchDateType)
            // ||
            // CommonConstants.DateType.MONTHLY.getCode().equals(searchDateType)
            // ||
            // CommonConstants.DateType.QUARTERLY.getCode().equals(searchDateType))
            // { // 주/월/분기별
            // sb.append("\n    AND D.YYYYMMDD >= :startDate   ");
            // sb.append("\n    AND D.YYYYMMDD <= :endDate     ");
        }
        sb.append("\n   GROUP BY LL.ID ");
        sb.append("\n ) EM ");
        sb.append("\n LEFT JOIN ");
        sb.append("\n (  ");
        sb.append("\n   SELECT LL2.ID AS CO2_PARENT_ID   ");
        sb.append("\n        , SUM(D2.TOTAL) AS CO2_TOTAL  ");
        for (int i = 0; i < dataType.length; i++) {

            sb.append("\n , " + dataType[i] + "(D2.VALUE_00) AS CO2_"
                    + dataType[i] + "_00 , " + dataType[i]
                    + "(D2.VALUE_01) AS CO2_" + dataType[i] + "_01 , "
                    + dataType[i] + "(D2.VALUE_02) AS CO2_" + dataType[i]
                    + "_02 , " + dataType[i] + "(D2.VALUE_03) AS CO2_"
                    + dataType[i] + "_03 , " + dataType[i]
                    + "(D2.VALUE_04) AS CO2_" + dataType[i] + "_04 ");
            sb.append("\n , " + dataType[i] + "(D2.VALUE_05) AS CO2_"
                    + dataType[i] + "_05 , " + dataType[i]
                    + "(D2.VALUE_06) AS CO2_" + dataType[i] + "_06 , "
                    + dataType[i] + "(D2.VALUE_07) AS CO2_" + dataType[i]
                    + "_07 , " + dataType[i] + "(D2.VALUE_08) AS CO2_"
                    + dataType[i] + "_08 , " + dataType[i]
                    + "(D2.VALUE_09) AS CO2_" + dataType[i] + "_09 ");
            sb.append("\n , " + dataType[i] + "(D2.VALUE_10) AS CO2_"
                    + dataType[i] + "_10 , " + dataType[i]
                    + "(D2.VALUE_11) AS CO2_" + dataType[i] + "_11 , "
                    + dataType[i] + "(D2.VALUE_12) AS CO2_" + dataType[i]
                    + "_12 , " + dataType[i] + "(D2.VALUE_13) AS CO2_"
                    + dataType[i] + "_13 , " + dataType[i]
                    + "(D2.VALUE_14) AS CO2_" + dataType[i] + "_14 ");
            sb.append("\n , " + dataType[i] + "(D2.VALUE_15) AS CO2_"
                    + dataType[i] + "_15 , " + dataType[i]
                    + "(D2.VALUE_16) AS CO2_" + dataType[i] + "_16 , "
                    + dataType[i] + "(D2.VALUE_17) AS CO2_" + dataType[i]
                    + "_17 , " + dataType[i] + "(D2.VALUE_18) AS CO2_"
                    + dataType[i] + "_18 , " + dataType[i]
                    + "(D2.VALUE_19) AS CO2_" + dataType[i] + "_19 ");
            sb.append("\n , " + dataType[i] + "(D2.VALUE_20) AS CO2_"
                    + dataType[i] + "_20 , " + dataType[i]
                    + "(D2.VALUE_21) AS CO2_" + dataType[i] + "_21 , "
                    + dataType[i] + "(D2.VALUE_22) AS CO2_" + dataType[i]
                    + "_22 , " + dataType[i] + "(D2.VALUE_23) AS CO2_"
                    + dataType[i] + "_23 ");
        }

        sb.append("\n   FROM DAY_EM D2 , ( ");
        sb
                .append("\n         SELECT M2.MDS_ID , L2.ID , L2.NAME , L2.PARENT_ID FROM METER M2 , LOCATION L2 WHERE M2.LOCATION_ID = L2.ID AND L2.ID= :locationId ");
        // sb.append("\n           AND M2.METERTYPE_ID = (  ");
        // sb.append("\n                SELECT CC.id FROM code AS CC WHERE CC.parent_id = ( ");
        // sb.append("\n                    SELECT C.id FROM code AS C WHERE C.CODE = '1.3.1' ");
        // // MeterType
        // sb.append("\n                ) AND CC.CODE = '1.3.1.1' "); // EnergyMeter
        // sb.append("\n            ) ");
        sb
                .append("\n     ) LL2 WHERE D2.MDEV_ID = LL2.MDS_ID AND D2.CHANNEL=0 AND D2.DST = 0 ");
        if (CommonConstants.DateType.DAILY.getCode().equals(searchDateType)) { // 일별
            sb.append("\n    AND D2.YYYYMMDD = :startDate    ");
            // } else if
            // (CommonConstants.DateType.WEEKLY.getCode().equals(searchDateType)
            // ||
            // CommonConstants.DateType.MONTHLY.getCode().equals(searchDateType)
            // ||
            // CommonConstants.DateType.QUARTERLY.getCode().equals(searchDateType))
            // { // 주/월/분기별
            // sb.append("\n    AND D2.YYYYMMDD >= :startDate   ");
            // sb.append("\n    AND D2.YYYYMMDD <= :endDate     ");
        }
        sb.append("\n   GROUP BY LL2.ID ");
        sb.append("\n ) CO2 ");
        sb.append("\n ON EM.EM_PARENT_ID = CO2.CO2_PARENT_ID");

        // logger.info("일(시간) 빌딩 전체 : 전력사용량/탄솝출량 의 최대 ,최소,합\n==== sb.toString() ====\n"+sb.toString());
        SQLQuery query = getSession().createSQLQuery(sb.toString());

        query.setInteger("locationId", locationId);

        if (CommonConstants.DateType.DAILY.getCode().equals(searchDateType)) {
            query.setString("startDate", startDate);
            // } else if
            // (CommonConstants.DateType.WEEKLY.getCode().equals(searchDateType)
            // ||
            // CommonConstants.DateType.MONTHLY.getCode().equals(searchDateType)
            // ||
            // CommonConstants.DateType.QUARTERLY.getCode().equals(searchDateType))
            // {
            // query.setString("startDate", startDate);
            // query.setString("endDate", endDate);
        }
        //logger.info(sb.toString());
        return query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP)
                .list();
    }   

    /**
     * BEMS 전력사용량 , 탄소배출량 차트 최대/최소 데이터 조회. 기준키로 location 테이블의 parent_id를 사용
     * 일(시간) 빌딩 전체 : 전력사용량/탄솝출량 의 최대 ,최소,합
     */
    @Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
    public List<Object> getConsumptionEmCo2MonitoringSumMinMaxPrentId(
            Map<String, Object> condition) {

        logger
                .info("일(시간) 빌딩 전체 : 전력사용량/탄솝출량 의 최대 ,최소,합\n==== conditions ====\n"
                        + condition);

        String searchDateType = ObjectUtils.defaultIfNull(
                condition.get("searchDateType"),
                CommonConstants.DateType.DAILY.getCode()).toString(); // 일 , 주 ,
                                                                        // 월 ,
                                                                        // 분기
        @SuppressWarnings("unused")
        Integer supplierId = (Integer) condition.get("supplierId");
        Integer locationId = (Integer) condition.get("locationId");
        String startDate = (String) condition.get("startDate");
        @SuppressWarnings("unused")
        String endDate = (String) condition.get("endDate");

        String[] dataType = { "MIN", "MAX", "SUM" };

        StringBuffer sb = new StringBuffer();

        sb.append("\n SELECT EM.* , CO2.* FROM (  ");
        sb.append("\n   SELECT LL.PARENT_ID AS EM_PARENT_ID  ");
        sb.append("\n        , SUM(D.TOTAL) AS EM_TOTAL  ");
        for (int i = 0; i < dataType.length; i++) {

            sb.append("\n , " + dataType[i] + "(D.VALUE_00) AS EM_"
                    + dataType[i] + "_00 , " + dataType[i]
                    + "(D.VALUE_01) AS EM_" + dataType[i] + "_01 , "
                    + dataType[i] + "(D.VALUE_02) AS EM_" + dataType[i]
                    + "_02 , " + dataType[i] + "(D.VALUE_03) AS EM_"
                    + dataType[i] + "_03 , " + dataType[i]
                    + "(D.VALUE_04) AS EM_" + dataType[i] + "_04 ");
            sb.append("\n , " + dataType[i] + "(D.VALUE_05) AS EM_"
                    + dataType[i] + "_05 , " + dataType[i]
                    + "(D.VALUE_06) AS EM_" + dataType[i] + "_06 , "
                    + dataType[i] + "(D.VALUE_07) AS EM_" + dataType[i]
                    + "_07 , " + dataType[i] + "(D.VALUE_08) AS EM_"
                    + dataType[i] + "_08 , " + dataType[i]
                    + "(D.VALUE_09) AS EM_" + dataType[i] + "_09 ");
            sb.append("\n , " + dataType[i] + "(D.VALUE_10) AS EM_"
                    + dataType[i] + "_10 , " + dataType[i]
                    + "(D.VALUE_11) AS EM_" + dataType[i] + "_11 , "
                    + dataType[i] + "(D.VALUE_12) AS EM_" + dataType[i]
                    + "_12 , " + dataType[i] + "(D.VALUE_13) AS EM_"
                    + dataType[i] + "_13 , " + dataType[i]
                    + "(D.VALUE_14) AS EM_" + dataType[i] + "_14 ");
            sb.append("\n , " + dataType[i] + "(D.VALUE_15) AS EM_"
                    + dataType[i] + "_15 , " + dataType[i]
                    + "(D.VALUE_16) AS EM_" + dataType[i] + "_16 , "
                    + dataType[i] + "(D.VALUE_17) AS EM_" + dataType[i]
                    + "_17 , " + dataType[i] + "(D.VALUE_18) AS EM_"
                    + dataType[i] + "_18 , " + dataType[i]
                    + "(D.VALUE_19) AS EM_" + dataType[i] + "_19 ");
            sb.append("\n , " + dataType[i] + "(D.VALUE_20) AS EM_"
                    + dataType[i] + "_20 , " + dataType[i]
                    + "(D.VALUE_21) AS EM_" + dataType[i] + "_21 , "
                    + dataType[i] + "(D.VALUE_22) AS EM_" + dataType[i]
                    + "_22 , " + dataType[i] + "(D.VALUE_23) AS EM_"
                    + dataType[i] + "_23 ");
        }

        sb.append("\n   FROM DAY_EM D , ( ");
        sb
                .append("\n         SELECT M.MDS_ID , L.ID , L.NAME , L.PARENT_ID FROM METER  M , (  ");
        sb
                .append("\n             SELECT ID , NAME , PARENT_ID FROM LOCATION WHERE PARENT_ID = :parentId ");
        sb.append("\n       ) L WHERE M.LOCATION_ID = L.ID ");
        // sb.append("\n           AND M.METERTYPE_ID = (  ");
        // sb.append("\n                SELECT CC.id FROM code AS CC WHERE CC.parent_id = ( ");
        // sb.append("\n                    SELECT C.id FROM code AS C WHERE C.CODE = '1.3.1' ");
        // // MeterType
        // sb.append("\n                ) AND CC.CODE = '1.3.1.1' "); // EnergyMeter
        // sb.append("\n            ) ");
        sb.append("\n   )LL WHERE D.MDEV_ID = LL.MDS_ID AND D.CHANNEL=1 AND D.DST = 0 ");
        if (CommonConstants.DateType.DAILY.getCode().equals(searchDateType)) { // 일별
            sb.append("\n    AND D.YYYYMMDD = :startDate    ");
            // } else if
            // (CommonConstants.DateType.WEEKLY.getCode().equals(searchDateType)
            // ||
            // CommonConstants.DateType.MONTHLY.getCode().equals(searchDateType)
            // ||
            // CommonConstants.DateType.QUARTERLY.getCode().equals(searchDateType))
            // { // 주/월/분기별
            // sb.append("\n    AND D.YYYYMMDD >= :startDate   ");
            // sb.append("\n    AND D.YYYYMMDD <= :endDate     ");
        }
        sb.append("\n   GROUP BY LL.PARENT_ID ");
        sb.append("\n ) EM ");
        sb.append("\n LEFT JOIN ");
        sb.append("\n (  ");
        sb.append("\n   SELECT LL2.PARENT_ID AS CO2_PARENT_ID   ");
        sb.append("\n        , SUM(D2.TOTAL) AS CO2_TOTAL  ");
        for (int i = 0; i < dataType.length; i++) {

            sb.append("\n , " + dataType[i] + "(D2.VALUE_00) AS CO2_"
                    + dataType[i] + "_00 , " + dataType[i]
                    + "(D2.VALUE_01) AS CO2_" + dataType[i] + "_01 , "
                    + dataType[i] + "(D2.VALUE_02) AS CO2_" + dataType[i]
                    + "_02 , " + dataType[i] + "(D2.VALUE_03) AS CO2_"
                    + dataType[i] + "_03 , " + dataType[i]
                    + "(D2.VALUE_04) AS CO2_" + dataType[i] + "_04 ");
            sb.append("\n , " + dataType[i] + "(D2.VALUE_05) AS CO2_"
                    + dataType[i] + "_05 , " + dataType[i]
                    + "(D2.VALUE_06) AS CO2_" + dataType[i] + "_06 , "
                    + dataType[i] + "(D2.VALUE_07) AS CO2_" + dataType[i]
                    + "_07 , " + dataType[i] + "(D2.VALUE_08) AS CO2_"
                    + dataType[i] + "_08 , " + dataType[i]
                    + "(D2.VALUE_09) AS CO2_" + dataType[i] + "_09 ");
            sb.append("\n , " + dataType[i] + "(D2.VALUE_10) AS CO2_"
                    + dataType[i] + "_10 , " + dataType[i]
                    + "(D2.VALUE_11) AS CO2_" + dataType[i] + "_11 , "
                    + dataType[i] + "(D2.VALUE_12) AS CO2_" + dataType[i]
                    + "_12 , " + dataType[i] + "(D2.VALUE_13) AS CO2_"
                    + dataType[i] + "_13 , " + dataType[i]
                    + "(D2.VALUE_14) AS CO2_" + dataType[i] + "_14 ");
            sb.append("\n , " + dataType[i] + "(D2.VALUE_15) AS CO2_"
                    + dataType[i] + "_15 , " + dataType[i]
                    + "(D2.VALUE_16) AS CO2_" + dataType[i] + "_16 , "
                    + dataType[i] + "(D2.VALUE_17) AS CO2_" + dataType[i]
                    + "_17 , " + dataType[i] + "(D2.VALUE_18) AS CO2_"
                    + dataType[i] + "_18 , " + dataType[i]
                    + "(D2.VALUE_19) AS CO2_" + dataType[i] + "_19 ");
            sb.append("\n , " + dataType[i] + "(D2.VALUE_20) AS CO2_"
                    + dataType[i] + "_20 , " + dataType[i]
                    + "(D2.VALUE_21) AS CO2_" + dataType[i] + "_21 , "
                    + dataType[i] + "(D2.VALUE_22) AS CO2_" + dataType[i]
                    + "_22 , " + dataType[i] + "(D2.VALUE_23) AS CO2_"
                    + dataType[i] + "_23 ");
        }

        sb.append("\n   FROM DAY_EM D2 , ( ");
        sb
                .append("\n         SELECT M2.MDS_ID , L2.ID , L2.NAME , L2.PARENT_ID FROM METER M2 , (  ");
        sb
                .append("\n             SELECT ID , NAME , PARENT_ID FROM LOCATION WHERE PARENT_ID = :parentId ");
        sb.append("\n       ) L2 WHERE M2.LOCATION_ID = L2.ID ");
        // sb.append("\n           AND M2.METERTYPE_ID = (  ");
        // sb.append("\n                SELECT CC.id FROM code AS CC WHERE CC.parent_id = ( ");
        // sb.append("\n                    SELECT C.id FROM code AS C WHERE C.CODE = '1.3.1' ");
        // // MeterType
        // sb.append("\n                ) AND CC.CODE = '1.3.1.1' "); // EnergyMeter
        // sb.append("\n            ) ");
        sb
                .append("\n     ) LL2 WHERE D2.MDEV_ID = LL2.MDS_ID AND D2.CHANNEL=0 AND D2.DST = 0 ");
        if (CommonConstants.DateType.DAILY.getCode().equals(searchDateType)) { // 일별
            sb.append("\n    AND D2.YYYYMMDD = :startDate    ");
            // } else if
            // (CommonConstants.DateType.WEEKLY.getCode().equals(searchDateType)
            // ||
            // CommonConstants.DateType.MONTHLY.getCode().equals(searchDateType)
            // ||
            // CommonConstants.DateType.QUARTERLY.getCode().equals(searchDateType))
            // { // 주/월/분기별
            // sb.append("\n    AND D2.YYYYMMDD >= :startDate   ");
            // sb.append("\n    AND D2.YYYYMMDD <= :endDate     ");
        }
        sb.append("\n   GROUP BY LL2.PARENT_ID ");
        sb.append("\n ) CO2 ");
        sb.append("\n ON EM.EM_PARENT_ID = CO2.CO2_PARENT_ID");

        
        SQLQuery query = getSession().createSQLQuery(sb.toString());

        query.setInteger("parentId", locationId);

        if (CommonConstants.DateType.DAILY.getCode().equals(searchDateType)) {
            query.setString("startDate", startDate);
            // } else if
            // (CommonConstants.DateType.WEEKLY.getCode().equals(searchDateType)
            // ||
            // CommonConstants.DateType.MONTHLY.getCode().equals(searchDateType)
            // ||
            // CommonConstants.DateType.QUARTERLY.getCode().equals(searchDateType))
            // {
            // query.setString("startDate", startDate);
            // query.setString("endDate", endDate);
        }

        return query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP)
                .list();
    }

    /**
     * BEMS 일주일 전력사용량 , 탄소배출량 차트 데이터 조회. 일주일 : 빌딩 전체 전력사용량/탄솝출량
     */
    @Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
    public List<Object> getConsumptionEmCo2WeekMonitoringLocationId(
            Map<String, Object> condition) {

        logger.info("일주일 : 빌딩 전체 전력사용량/탄솝출량 \n==== conditions ====\n"
                + condition);

        String searchDateType = ObjectUtils.defaultIfNull(
                condition.get("searchDateType"),
                CommonConstants.DateType.DAILY.getCode()).toString(); // 일 , 주 ,
                                                                        // 월 ,
                                                                        // 분기
        @SuppressWarnings("unused")
        Integer supplierId = (Integer) condition.get("supplierId");
        Integer locationId = (Integer) condition.get("locationId");
        // String channelType = (String)condition.get("channelType"); // 탄소일 경우만
        // 0 , 전력/온도/습도의 사용량일때는 1
        String startDate = (String) condition.get("startDate");
        String endDate = (String) condition.get("endDate");

        StringBuffer sb = new StringBuffer();

        sb
                .append("\n    SELECT EM.YYYYMMDD AS YYYYMMDD , EM.TOTAL AS EM_TOTAL ,  CO2.TOTAL AS CO2_TOTAL  ");
        sb.append("\n      FROM (      ");
        sb.append("\n             SELECT D.YYYYMMDD , SUM(D.TOTAL) AS TOTAL ");
        sb.append("\n               FROM DAY_EM D , (      ");
        sb.append("\n                       SELECT M.MDS_ID , L.ID , L.NAME  ");
        sb.append("\n                         FROM METER M , LOCATION L  ");
        sb.append("\n                        WHERE M.LOCATION_ID = L.ID     ");
        sb.append("\n                          AND L.ID = :locationId ");
        // sb.append("\n           AND M.METERTYPE_ID = (  ");
        // sb.append("\n                SELECT CC.id FROM code AS CC WHERE CC.parent_id = ( ");
        // sb.append("\n                    SELECT C.id FROM code AS C WHERE C.CODE = '1.3.1' ");
        // // MeterType
        // sb.append("\n                ) AND CC.CODE = '1.3.1.1' "); // EnergyMeter
        // sb.append("\n            ) ");
        sb.append("\n                    ) LL  ");
        sb.append("\n               WHERE D.MDEV_ID = LL.MDS_ID  ");
        sb.append("\n                 AND D.CHANNEL = 1  AND D.DST = 0     ");
        if (CommonConstants.DateType.WEEKLY.getCode().equals(searchDateType)
                || CommonConstants.DateType.MONTHLY.getCode().equals(
                        searchDateType)
                || CommonConstants.DateType.QUARTERLY.getCode().equals(
                        searchDateType)) { // 주/월/분기별
            sb.append("\n                 AND D.YYYYMMDD between :startDate and :endDate  ");
        }
        sb.append("\n               GROUP BY D.YYYYMMDD   ");
        sb.append("\n      ) EM ");
        sb.append("\n      LEFT JOIN ");
        sb.append("\n      ( ");
        sb.append("\n              SELECT D2.YYYYMMDD , SUM(D2.TOTAL) AS TOTAL ");
        sb.append("\n                FROM DAY_EM D2 , ( ");
        sb.append("\n                       SELECT M2.MDS_ID , L2.ID , L2.NAME  ");
        sb.append("\n                        FROM METER M2 , LOCATION L2  ");
        sb.append("\n                       WHERE M2.LOCATION_ID = L2.ID     ");
        sb.append("\n                         AND L2.ID = :locationId  ");
        // sb.append("\n           AND M2.METERTYPE_ID = (  ");
        // sb.append("\n                SELECT CC.id FROM code AS CC WHERE CC.parent_id = ( ");
        // sb.append("\n                    SELECT C.id FROM code AS C WHERE C.CODE = '1.3.1' ");
        // // MeterType
        // sb.append("\n                ) AND CC.CODE = '1.3.1.1' "); // EnergyMeter
        // sb.append("\n            ) ");
        sb.append("\n                     )LL2  ");
        sb.append("\n               WHERE D2.MDEV_ID = LL2.MDS_ID  ");
        sb.append("\n                 AND D2.CHANNEL = 0  AND D2.DST = 0     ");
        if (CommonConstants.DateType.WEEKLY.getCode().equals(searchDateType)
                || CommonConstants.DateType.MONTHLY.getCode().equals(
                        searchDateType)
                || CommonConstants.DateType.QUARTERLY.getCode().equals(
                        searchDateType)) { // 주/월/분기별
            sb.append("\n                 AND D2.YYYYMMDD between :startDate and :endDate  ");
        }
        sb.append("\n               GROUP BY D2.YYYYMMDD    ");
        sb.append("\n         ) CO2    ");
        sb.append("\n    ON EM.YYYYMMDD = CO2.YYYYMMDD ");
        // sb.append("\n   ORDER BY EM.YYYYMMDD ASC  ");

        //logger.debug(sb.toString());
        SQLQuery query = getSession().createSQLQuery(sb.toString());

        query.setInteger("locationId", locationId);

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
     * BEMS 일주일 전력사용량 , 탄소배출량 차트 데이터 조회. 일주일 : 빌딩 전체 전력사용량/탄솝출량
     */
    @Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
    public List<Object> getConsumptionEmCo2WeekMonitoringParentId(
            Map<String, Object> condition) {

        logger.info("일주일 : 빌딩 전체 전력사용량/탄솝출량 \n==== conditions ====\n"
                + condition);

        String searchDateType = ObjectUtils.defaultIfNull(
                condition.get("searchDateType"),
                CommonConstants.DateType.DAILY.getCode()).toString(); // 일 , 주 ,
                                                                        // 월 ,
                                                                        // 분기
        @SuppressWarnings("unused")
        Integer supplierId = (Integer) condition.get("supplierId");
        Integer locationId = (Integer) condition.get("locationId");
        // String channelType = (String)condition.get("channelType"); // 탄소일 경우만
        // 0 , 전력/온도/습도의 사용량일때는 1
        String startDate = (String) condition.get("startDate");
        String endDate = (String) condition.get("endDate");

        StringBuffer sb = new StringBuffer();

        sb
                .append("\n    SELECT EM.YYYYMMDD AS YYYYMMDD , EM.TOTAL AS EM_TOTAL ,  CO2.TOTAL AS CO2_TOTAL  ");
        sb.append("\n      FROM (      ");
        sb.append("\n             SELECT D.YYYYMMDD , SUM(D.TOTAL) AS TOTAL ");
        sb.append("\n               FROM DAY_EM D , (      ");
        sb.append("\n                       SELECT M.MDS_ID , L.ID , L.NAME  ");
        sb.append("\n                         FROM METER M , ( ");
        sb.append("\n                                   SELECT ID , NAME  ");
        sb.append("\n                                     FROM LOCATION  ");
        sb.append("\n                                    WHERE PARENT_ID = :parentId      ");
        sb.append("\n                              )L  ");
        sb.append("\n                        WHERE M.LOCATION_ID = L.ID     ");
        // sb.append("\n           AND M.METERTYPE_ID = (  ");
        // sb.append("\n                SELECT CC.id FROM code AS CC WHERE CC.parent_id = ( ");
        // sb.append("\n                    SELECT C.id FROM code AS C WHERE C.CODE = '1.3.1' ");
        // // MeterType
        // sb.append("\n                ) AND CC.CODE = '1.3.1.1' "); // EnergyMeter
        // sb.append("\n            ) ");
        sb.append("\n                    )LL  ");
        sb.append("\n               WHERE D.MDEV_ID = LL.MDS_ID  ");
        sb.append("\n                 AND D.CHANNEL = 1  AND D.DST = 0   ");
        if (CommonConstants.DateType.WEEKLY.getCode().equals(searchDateType)
                || CommonConstants.DateType.MONTHLY.getCode().equals(
                        searchDateType)
                || CommonConstants.DateType.QUARTERLY.getCode().equals(
                        searchDateType)) { // 주/월/분기별
            sb.append("\n                 AND D.YYYYMMDD between :startDate and :endDate  ");
        }
        sb.append("\n               GROUP BY D.YYYYMMDD   ");
        sb.append("\n      ) EM ");
        sb.append("\n      LEFT JOIN ");
        sb.append("\n      ( ");
        sb.append("\n              SELECT D2.YYYYMMDD , SUM(D2.TOTAL) AS TOTAL ");
        sb.append("\n                FROM DAY_EM D2 , ( ");
        sb.append("\n                       SELECT M2.MDS_ID , L2.ID , L2.NAME  ");
        sb.append("\n                        FROM METER M2 , ( ");
        sb.append("\n                               SELECT ID , NAME  ");
        sb.append("\n                                 FROM LOCATION  ");
        sb.append("\n                                WHERE PARENT_ID = :parentId      ");
        sb.append("\n                             )L2  ");
        sb.append("\n                       WHERE M2.LOCATION_ID = L2.ID     ");
        // sb.append("\n           AND M2.METERTYPE_ID = (  ");
        // sb.append("\n                SELECT CC.id FROM code AS CC WHERE CC.parent_id = ( ");
        // sb.append("\n                    SELECT C.id FROM code AS C WHERE C.CODE = '1.3.1' ");
        // // MeterType
        // sb.append("\n                ) AND CC.CODE = '1.3.1.1' "); // EnergyMeter
        // sb.append("\n            ) ");
        sb.append("\n                     )LL2  ");
        sb.append("\n               WHERE D2.MDEV_ID = LL2.MDS_ID  ");
        sb.append("\n                 AND D2.CHANNEL = 0 AND D2.DST = 0      ");
        if (CommonConstants.DateType.WEEKLY.getCode().equals(searchDateType)
                || CommonConstants.DateType.MONTHLY.getCode().equals(
                        searchDateType)
                || CommonConstants.DateType.QUARTERLY.getCode().equals(
                        searchDateType)) { // 주/월/분기별
            sb.append("\n                 AND D2.YYYYMMDD between :startDate and :endDate  ");
        }
        sb.append("\n               GROUP BY D2.YYYYMMDD    ");
        sb.append("\n         ) CO2    ");
        sb.append("\n    ON EM.YYYYMMDD = CO2.YYYYMMDD ");
        // sb.append("\n   ORDER BY EM.YYYYMMDD ASC  ");
        
        //logger.debug(sb.toString());
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
     * BEMS 일주일 온도 / 습도 차트 데이터 조회. 일주일 : 빌딩 전체 온도 /습도 온도는 2 , 습도는 3으로 임시로 정하여
     * 작업하였음..
     */
    @Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
    public List<Object> getConsumptionTmHmWeekMonitoring(
            Map<String, Object> condition) {

        logger.info("일주일 : 빌딩 전체 온도 /습도\n==== conditions ====\n" + condition);

        String searchDateType = ObjectUtils.defaultIfNull(
                condition.get("searchDateType"),
                CommonConstants.DateType.DAILY.getCode()).toString(); // 일 , 주 ,
                                                                        // 월 ,
                                                                        // 분기
        @SuppressWarnings("unused")
        Integer supplierId = (Integer) condition.get("supplierId");
        Integer locationId = (Integer) condition.get("locationId");
        // String channelType = (String)condition.get("channelType"); // 탄소일 경우만
        // 0 , 전력/온도/습도의 사용량일때는 1
        String startDate = (String) condition.get("startDate");
        String endDate = (String) condition.get("endDate");

        StringBuffer sb = new StringBuffer();

        sb
                .append("\n    SELECT EM.YYYYMMDD AS YYYYMMDD , EM.TOTAL AS EM_TOTAL ,  CO2.TOTAL AS CO2_TOTAL  ");
        sb.append("\n      FROM (      ");
        sb.append("\n             SELECT D.YYYYMMDD , SUM(D.TOTAL) AS TOTAL ");
        sb.append("\n               FROM DAY_EM D , (      ");
        sb.append("\n                       SELECT M.MDS_ID , L.ID , L.NAME  ");
        sb.append("\n                         FROM METER M , ( ");
        sb.append("\n                                   SELECT ID , NAME  ");
        sb.append("\n                                     FROM LOCATION  ");
        sb.append("\n                                    WHERE PARENT_ID = :parentId      ");
        sb.append("\n                              ) L  ");
        sb.append("\n                        WHERE M.LOCATION_ID = L.ID     ");
        // sb.append("\n           AND M.METERTYPE_ID = (  ");
        // sb.append("\n                SELECT CC.id FROM code AS CC WHERE CC.parent_id = ( ");
        // sb.append("\n                    SELECT C.id FROM code AS C WHERE C.CODE = '1.3.1' ");
        // // MeterType
        // sb.append("\n                ) AND CC.CODE = '1.3.1.1' "); // EnergyMeter
        // sb.append("\n            ) ");
        sb.append("\n                    ) LL  ");
        sb.append("\n               WHERE D.MDEV_ID = LL.MDS_ID  ");
        sb.append("\n                 AND D.CHANNEL = 2  AND D.DST = 0     ");
        if (CommonConstants.DateType.WEEKLY.getCode().equals(searchDateType)
                || CommonConstants.DateType.MONTHLY.getCode().equals(
                        searchDateType)
                || CommonConstants.DateType.QUARTERLY.getCode().equals(
                        searchDateType)) { // 주/월/분기별
            sb.append("\n                 AND D.YYYYMMDD between :startDate and :endDate  ");
        }
        sb.append("\n               GROUP BY D.YYYYMMDD   ");
        sb.append("\n      ) EM ");
        sb.append("\n      LEFT JOIN ");
        sb.append("\n      ( ");
        sb.append("\n              SELECT D2.YYYYMMDD , SUM(D2.TOTAL) AS TOTAL ");
        sb.append("\n                FROM DAY_EM D2 , ( ");
        sb.append("\n                       SELECT M2.MDS_ID , L2.ID , L2.NAME  ");
        sb.append("\n                        FROM METER M2 , ( ");
        sb.append("\n                               SELECT ID , NAME  ");
        sb.append("\n                                 FROM LOCATION  ");
        sb.append("\n                                WHERE PARENT_ID = :parentId      ");
        sb.append("\n                             ) L2  ");
        sb.append("\n                       WHERE M2.LOCATION_ID = L2.ID     ");
        // sb.append("\n           AND M2.METERTYPE_ID = (  ");
        // sb.append("\n                SELECT CC.id FROM code AS CC WHERE CC.parent_id = ( ");
        // sb.append("\n                    SELECT C.id FROM code AS C WHERE C.CODE = '1.3.1' ");
        // // MeterType
        // sb.append("\n                ) AND CC.CODE = '1.3.1.1' "); // EnergyMeter
        // sb.append("\n            ) ");
        sb.append("\n                     ) LL2  ");
        sb.append("\n               WHERE D2.MDEV_ID = LL2.MDS_ID  ");
        sb.append("\n                 AND D2.CHANNEL = 3  AND D2.DST = 0     ");
        if (CommonConstants.DateType.WEEKLY.getCode().equals(searchDateType)
                || CommonConstants.DateType.MONTHLY.getCode().equals(
                        searchDateType)
                || CommonConstants.DateType.QUARTERLY.getCode().equals(
                        searchDateType)) { // 주/월/분기별
            sb.append("\n                 AND D2.YYYYMMDD between :startDate and :endDate  ");
        }
        sb.append("\n               GROUP BY D2.YYYYMMDD    ");
        sb.append("\n         ) CO2    ");
        sb.append("\n    ON EM.YYYYMMDD = CO2.YYYYMMDD ");
        // sb.append("\n   ORDER BY EM.YYYYMMDD ASC  ");

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
     * BEMS 설비별 사용량 조회.
     */
    @Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
    public List<Object> getCompareFacilityDayData(Map<String, Object> condition) {

        StringBuffer sb = new StringBuffer();

        Boolean convert = (Boolean) condition.get("convert");
        sb.append("\n    SELECT                                 ");
        sb.append("\n    (SELECT TOTAL FROM DAY_EM              ");
        sb.append("\n    WHERE channel = :channel               ");
        sb.append("\n    AND YYYYMMDD = :yesterday              ");
        if (convert)
            sb.append("\n    AND MODEM_ID = :endDeviceId        ");
        else
            sb.append("\n    AND METER_ID = :endDeviceId        ");
        sb.append("\n    ) EM_YESTERDAY,                        ");

        sb.append("\n    (SELECT TOTAL FROM DAY_EM              ");
        sb.append("\n    WHERE channel = :channel               ");
        sb.append("\n    AND YYYYMMDD = :today                  ");
        if (convert)
            sb.append("\n    AND MODEM_ID = :endDeviceId        ");
        else
            sb.append("\n    AND METER_ID = :endDeviceId        ");
        sb.append("\n    ) EM_TODAY,                            ");

        sb.append("\n    (SELECT SUM(TOTAL) FROM DAY_EM         ");
        sb.append("\n    WHERE channel = :channel               ");
        sb.append("\n    AND YYYYMMDD between :lastWeekStartDate and :lastWeekEndDate    ");
        if (convert)
            sb.append("\n    AND MODEM_ID = :endDeviceId        ");
        else
            sb.append("\n    AND METER_ID = :endDeviceId        ");
        sb.append("\n    ) EM_LASTWEEK,                         ");

        sb.append("\n    (SELECT SUM(TOTAL) FROM DAY_EM         ");
        sb.append("\n    WHERE channel = :channel               ");
        sb.append("\n    AND YYYYMMDD between :weekStartDate and :weekEndDate         ");
        if (convert)
            sb.append("\n    AND MODEM_ID = :endDeviceId        ");
        else
            sb.append("\n    AND METER_ID = :endDeviceId        ");
        sb.append("\n    ) EM_WEEK,                             ");

        sb.append("\n    (SELECT TOTAL FROM DAY_GM ");
        sb.append("\n    WHERE channel = :channel               ");
        sb.append("\n    AND YYYYMMDD = :yesterday              ");
        if (convert)
            sb.append("\n    AND MODEM_ID = :endDeviceId        ");
        else
            sb.append("\n    AND METER_ID = :endDeviceId        ");
        sb.append("\n    ) GM_YESTERDAY,                        ");

        sb.append("\n    (SELECT TOTAL FROM DAY_GM              ");
        sb.append("\n    WHERE channel = :channel               ");
        sb.append("\n    AND YYYYMMDD = :today                  ");
        if (convert)
            sb.append("\n    AND MODEM_ID = :endDeviceId        ");
        else
            sb.append("\n    AND METER_ID = :endDeviceId        ");
        sb.append("\n    ) GM_TODAY,                            ");

        sb.append("\n    (SELECT SUM(TOTAL) FROM DAY_GM         ");
        sb.append("\n    WHERE channel = :channel               ");
        sb.append("\n    AND YYYYMMDD between :lastWeekStartDate and :lastWeekEndDate    ");
        if (convert)
            sb.append("\n    AND MODEM_ID = :endDeviceId        ");
        else
            sb.append("\n    AND METER_ID = :endDeviceId        ");
        sb.append("\n    ) GM_LASTWEEK,                         ");

        sb.append("\n    (SELECT SUM(TOTAL) FROM DAY_GM         ");
        sb.append("\n    WHERE channel = :channel               ");
        sb.append("\n    AND YYYYMMDD between :weekStartDate and :weekEndDate       ");
        if (convert)
            sb.append("\n    AND MODEM_ID = :endDeviceId        ");
        else
            sb.append("\n    AND METER_ID = :endDeviceId        ");
        sb.append("\n    ) GM_WEEK,                             ");

        sb.append("\n    (SELECT TOTAL FROM DAY_HM ");
        sb.append("\n    WHERE channel = :channel               ");
        sb.append("\n    AND YYYYMMDD = :yesterday              ");
        if (convert)
            sb.append("\n    AND MODEM_ID = :endDeviceId        ");
        else
            sb.append("\n    AND METER_ID = :endDeviceId        ");
        sb.append("\n    ) HM_YESTERDAY,                        ");

        sb.append("\n    (SELECT TOTAL FROM DAY_HM              ");
        sb.append("\n    WHERE channel = :channel               ");
        sb.append("\n    AND YYYYMMDD = :today                  ");
        if (convert)
            sb.append("\n    AND MODEM_ID = :endDeviceId        ");
        else
            sb.append("\n    AND METER_ID = :endDeviceId        ");
        sb.append("\n    ) HM_TODAY,                            ");

        sb.append("\n    (SELECT SUM(TOTAL) FROM DAY_HM         ");
        sb.append("\n    WHERE channel = :channel               ");
        sb.append("\n    AND YYYYMMDD between :lastWeekStartDate and :lastWeekEndDate   ");
        if (convert)
            sb.append("\n    AND MODEM_ID = :endDeviceId        ");
        else
            sb.append("\n    AND METER_ID = :endDeviceId        ");
        sb.append("\n    ) HM_LASTWEEK,                         ");

        sb.append("\n    (SELECT SUM(TOTAL) FROM DAY_HM         ");
        sb.append("\n    WHERE channel = :channel               ");
        sb.append("\n    AND YYYYMMDD between :weekStartDate and :weekEndDate        ");
        if (convert)
            sb.append("\n    AND MODEM_ID = :endDeviceId        ");
        else
            sb.append("\n    AND METER_ID = :endDeviceId        ");
        sb.append("\n    ) HM_WEEK,                             ");

        sb.append("\n    (SELECT TOTAL FROM DAY_WM ");
        sb.append("\n    WHERE channel = :channel               ");
        sb.append("\n    AND YYYYMMDD = :yesterday              ");
        if (convert)
            sb.append("\n    AND MODEM_ID = :endDeviceId        ");
        else
            sb.append("\n    AND METER_ID = :endDeviceId        ");
        sb.append("\n    ) WM_YESTERDAY,                        ");

        sb.append("\n    (SELECT TOTAL FROM DAY_WM              ");
        sb.append("\n    WHERE channel = :channel               ");
        sb.append("\n    AND YYYYMMDD = :today                  ");
        if (convert)
            sb.append("\n    AND MODEM_ID = :endDeviceId        ");
        else
            sb.append("\n    AND METER_ID = :endDeviceId        ");
        sb.append("\n    ) WM_TODAY,                            ");

        sb.append("\n    (SELECT SUM(TOTAL) FROM DAY_WM         ");
        sb.append("\n    WHERE channel = :channel               ");
        sb.append("\n    AND YYYYMMDD between :lastWeekStartDate and :lastWeekEndDate    ");
        if(convert)
            sb.append("\n    AND MODEM_ID = :endDeviceId        ");
            else
                sb.append("\n    AND METER_ID = :endDeviceId        ");
        sb.append("\n    ) WM_LASTWEEK,                         ");

        sb.append("\n    (SELECT SUM(TOTAL) FROM DAY_WM         ");
        sb.append("\n    WHERE channel = :channel               ");
        sb.append("\n    AND YYYYMMDD between :weekStartDate and :weekEndDate        ");
        if(convert)
            sb.append("\n    AND MODEM_ID = :endDeviceId        ");
            else
                sb.append("\n    AND METER_ID = :endDeviceId        ");
        sb.append("\n    ) WM_WEEK                              ");

        sb.append("\n    FROM DAY_EM                            ");
        sb.append("\n    WHERE channel=:channel                 ");
        sb.append("\n    AND YYYYMMDD between :lastWeekStartDate and :weekEndDate                    ");
        
        if(convert)
            sb.append("\n    AND MODEM_ID = :endDeviceId        ");
            else
                sb.append("\n    AND METER_ID = :endDeviceId        ");
        SQLQuery query = getSession().createSQLQuery(sb.toString());
        query.setString("yesterday", condition.get("yesterday").toString());
        query.setString("today", condition.get("today").toString());
        query.setString("channel", "1");
        query.setString("lastWeekStartDate", condition.get("lastWeekStartDate")
                .toString());
        query.setString("lastWeekEndDate", condition.get("lastWeekEndDate")
                .toString());
        query.setString("weekStartDate", condition.get("weekStartDate")
                .toString());
        query.setString("weekEndDate", condition.get("weekEndDate").toString());
        query.setInteger("endDeviceId",(Integer) condition.get("endDeviceId"));
        return query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP)
                .list();
    }

    @Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
    public List<Integer> getContractIds(Map<String, String> conditionMap) {

        String startDate = conditionMap.get("startDate");
        String endDate = conditionMap.get("endDate");
        String locationCondition = conditionMap.get("locationCondition");
        String searchDateType = conditionMap.get("searchDateType");

        StringBuilder sb = new StringBuilder()
                .append(" SELECT distinct d.contract.id            ")
                .append("   FROM DayEM d                   ")
                .append(
                        "  WHERE d.id.channel = :channel  and d.id.dst = 0                     ")
                .append(
                        "    AND d.contract.serviceTypeCode.code = :serviceTypeCode ");
                // .append(
                //        "    AND d.contract.status.code = :status                   ");

        if ("1".equals(searchDateType)) { // 일별
            sb.append("    AND d.id.yyyymmdd = :startDate    ");
        } else if ("3".equals(searchDateType)) { // 월별
            sb.append("    AND d.id.yyyymmdd between :startDate and :endDate   ");
        }
        if (!"".equals(locationCondition)) {
            sb.append("   AND d.location.id in (" + locationCondition + ")");
        }

        Query query = getSession().createQuery(sb.toString());
        query.setString("channel", DefaultChannel.Usage.getCode() + "");
        query.setString("serviceTypeCode", MeterType.EnergyMeter
                .getServiceType());
        // query.setString("status", ContractStatus.NORMAL.getCode());

        if ("1".equals(searchDateType)) {
            query.setString("startDate", startDate);
        } else if ("3".equals(searchDateType)) {
            query.setString("startDate", startDate);
            query.setString("endDate", endDate);
        }

        return query.list();
    }
    
    @Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
    public List<Object> getDayEMsByNoSended(String date,String type) {

        
        StringBuffer sb = new StringBuffer();

        
        sb.append("\n   select C.mdev_id,C.VALUE,C.FLOOR,C.BLOCK,D.code from ");
        sb.append("\n   (select A.mdev_id,A.total as VALUE,A.name as FLOOR,B.name as BLOCK from ");
        sb.append("\n   (select d.mdev_id,d.total,l.name,l.parent_id from day_em d, ");
        sb.append("\n   (select id,parent_id,name from location) l ");
        sb.append("\n   where d.yyyymmdd=:date and d.channel=1 and d.mdev_type=:type and d.LOCATION_ID is not null ");
        sb.append("\n   and d.send_result is null ");
        sb.append("\n   and d.location_id=l.id) A left outer join "); 
        sb.append("\n   (select id,parent_id,name from location) B ");
        sb.append("\n   on A.parent_id=B.id) C ");
        sb.append("\n   left outer join "); 
        sb.append("\n   (select e.serial_number,c.code from enddevice e,code c where e.CATEGORY_ID=c.id) D ");
        sb.append("\n   on C.mdev_id=D.serial_number ");
        

        SQLQuery query = getSession().createSQLQuery(sb.toString());

        query.setString("date", date);
        query.setString("type", type);
        return query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP)
                .list();
    }
    
    @Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
    public void updateSendedResult(String table,String date,DeviceType type,String mdev_id) {
        try {
          
            StringBuffer sbQuery = new StringBuffer();
            sbQuery.append(" UPDATE "+table+" SET ");
            sbQuery.append(" send_result='1'");
            
            
            sbQuery.append(" WHERE id.yyyymmdd = ? ")
                .append(" AND id.channel = ? ")
                .append(" AND id.mdevType = ? ")
                .append(" AND id.mdevId = ? ")
                .append(" AND id.dst = ? ");
        
            //log.debug(sbQuery.toString());
            
            //HQL문을 이용한 CUD를 할 경우에는 getSession().bulkUpdate() 메소드를 사용한다.
            Query query = getSession().createQuery(sbQuery.toString());
            query.setString(1, date);
            query.setInteger(2, 1);
            query.setParameter(3, type);
            query.setString(4, mdev_id);
            query.setInteger(5, 0);
            query.executeUpdate();
            /*this.getSession().bulkUpdate(sbQuery.toString(),
                new Object[] {date
                        , 1
                        , type
                        , mdev_id
                        , 0} ); */
        }
        catch (Exception e) {
           e.printStackTrace();
        }
            
    }

    /* (non-Javadoc)
     * @see com.aimir.dao.mvm.DayEMDao#getPeriodTimeUsage(int, java.lang.String, int)
     */
    @Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
    public List<DayEM> getDayEMs(DayEM dayEM) {

        Criteria criteria = getSession().createCriteria(DayEM.class);
        
        if (dayEM != null) {
            
            if (dayEM.getContract() != null) {
                
                if (dayEM.getContract().getId() != null) {
                    
                    criteria.add(Restrictions.eq("contract.id", dayEM.getContract().getId()));
                } 
            }
            
            if (dayEM.getChannel() != null) {
                
                criteria.add(Restrictions.eq("id.channel", dayEM.getChannel()));
            }
            
            if (dayEM.getYyyymmdd() != null) {
                
                
                criteria.add(Restrictions.eq("id.yyyymmdd", dayEM.getYyyymmdd()));
            }
            
            if (dayEM.getMDevType() != null) {
                
                criteria.add(Restrictions.eq("id.mdevType", dayEM.getMDevType()));
            }
        }

        return criteria.list();
    }

    /* (non-Javadoc)
     * @see com.aimir.dao.mvm.DayEMDao#getDeviceSpecificGrid(java.lang.String, int)
     */
    @Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
    public List<Map<String, Object>> getDeviceSpecificGrid(String basicDay, int contractId) {

        String sqlStr = " "
//          + "\n SELECT  B.friendlyName       AS name "
//          + "\n       , A.total              AS usage "
//          + "\n       , B.drLevel            AS levelDR "
//          + "\n       , B.drProgramMandatory AS programDR "
//          + "\n       , 0                    AS co2 "
//          + "\n   FROM  DayEM     A "
//          + "\n       , EndDevice B "
//          + "\n   LEFT "
//          + "\n  OUTER "
//          + "\n   JOIN B.categoryCode C "
//          + "\n  WHERE  A.id.channel = 1 "
//          + "\n    AND  A.id.yyyymmdd = :basicDay "
//          + "\n    AND  A.contract.id = :contractId "
//          + "\n    AND  'Y' = CASE WHEN A.id.mdevType = :endDevice "
//          + "\n                    THEN CASE WHEN A.enddevice.id = B.id "
//          + "\n                              THEN 'Y' "
//          + "\n                              ELSE 'N' END "
//          + "\n                    WHEN A.id.mdevType = :modem "
//          + "\n                    THEN CASE WHEN A.modem.id = B.modem.id "
//          + "\n                               AND C.code = :normal "
//          + "\n                              THEN 'Y' "
//          + "\n                              ELSE 'N' END "
//          + "\n                    ELSE 'N' END "
//          + "\n   ORDER BY A.total DESC ";
            + "\n SELECT  B.FRIENDLY_NAME       AS NAME "
            + "\n       , A.USAGE               AS USAGE "
            + "\n       , C.DRNAME              AS LEVELDR "
            + "\n       , B.DRPROGRAMMANDATORY  AS PROGRAMDR "
            + "\n       , A.CO2                 AS CO2 "
            + "\n   FROM  ( "
            + "\n         SELECT  A.TOTAL         AS USAGE "
            + "\n               , B.TOTAL         AS CO2 "
            + "\n               , A.MDEV_TYPE     AS MDEV_TYPE "
            + "\n               , A.MODEM_ID      AS MODEM_ID "
            + "\n               , A.ENDDEVICE_ID  AS ENDDEVICE_ID "
            + "\n           FROM  DAY_EM A "
            + "\n           LEFT "
            + "\n          OUTER "
            + "\n           JOIN  DAY_EM B "
            + "\n             ON  A.YYYYMMDD = B.YYYYMMDD "
            + "\n            AND  A.MDEV_ID = B.MDEV_ID "
            + "\n            AND  A.CONTRACT_ID = B.CONTRACT_ID "
            + "\n            AND  B.CHANNEL = :co2Channel "
            + "\n          WHERE  A.YYYYMMDD = :basicDay "
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
            + "\n  ORDER BY A.USAGE ";

//      Query query = getSession().createQuery(sqlStr);
        SQLQuery query = getSession().createSQLQuery(sqlStr);
        
        query.setInteger("contractId", contractId);
        query.setInteger("co2Channel", DefaultChannel.Co2.getCode());
        query.setInteger("usageChannel", DefaultChannel.Usage.getCode());
        query.setString("basicDay", basicDay);
        query.setString("endDevice", DeviceType.EndDevice.name());
        query.setString("modem", DeviceType.Modem.name());
        query.setString("smartConcent", HomeDeviceCategoryType.SMART_CONCENT.getCode());
        query.setString("generalAppliance", HomeDeviceCategoryType.GENERAL_APPLIANCE.getCode());
        query.setString("smartAppliance", HomeDeviceCategoryType.SMART_APPLIANCE.getCode());
//      query.setString("normal", HomeDeviceCategoryType.GENERAL_APPLIANCE.getCode());
        
        return query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
    }   


    /* (non-Javadoc)
     * @see com.aimir.dao.mvm.DayEMDao#getDayEMsAvg(com.aimir.model.mvm.DayEM)
     */
    @Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
    public List<Object> getDayEMsAvg(DayEM dayEM) {

        Criteria criteria = getSession().createCriteria(DayEM.class);
        
        if (dayEM != null) {
            
            if (dayEM.getChannel() != null) {
                
                criteria.add(Restrictions.eq("id.channel", dayEM.getChannel()));
            }

            if (dayEM.getYyyymmdd() != null) {
                
                
                criteria.add(Restrictions.eq("id.yyyymmdd", dayEM.getYyyymmdd()));
            }
            
            if (dayEM.getMDevType() != null) {
                
                criteria.add(Restrictions.eq("id.mdevType", dayEM.getMDevType()));
            }

            if (dayEM.getLocation() != null) {

                criteria.add(Restrictions.eq("location.id", dayEM.getLocation().getId()));
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
        //return criteria.setResultTransformer(Transformers.TO_LIST).list();
        return criteria.list();
    }
    
    @Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
    public Double getDayEMsUsageAvg(DayEM dayEM) {

        Criteria criteria = getSession().createCriteria(DayEM.class);
        
        if (dayEM != null) {
            
            if (dayEM.getChannel() != null) {
                
                criteria.add(Restrictions.eq("id.channel", dayEM.getChannel()));
            }
            
            if (dayEM.getYyyymmdd() != null) {
                
                
                criteria.add(Restrictions.eq("id.yyyymmdd", dayEM.getYyyymmdd()));
            }
            
            if (dayEM.getMDevType() != null) {
                
                criteria.add(Restrictions.eq("id.mdevType", dayEM.getMDevType()));
            }

            if (dayEM.getLocation() != null) {

                criteria.add(Restrictions.eq("location.id", dayEM.getLocation().getId()));
            }
        }

        ProjectionList pjl = Projections.projectionList();
        pjl.add(Projections.avg("total"));
        criteria.setProjection(pjl);

        return ((Number)(criteria.list().get(0) == null ? 0.0 : criteria.list().get(0))).doubleValue();
    }
    
    @Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
    public Double getDayEMsUsageMonthToDate(DayEM dayEM, String startDay, String endDay) {

        Criteria criteria = getSession().createCriteria(DayEM.class);
        
        if (dayEM != null) {
            
            if (dayEM.getContract() != null) {
                
                if (dayEM.getContract().getId() != null) {
                    
                    criteria.add(Restrictions.eq("contract.id", dayEM.getContract().getId()));
                } 
            }
            
            if (dayEM.getChannel() != null) {
                
                criteria.add(Restrictions.eq("id.channel", dayEM.getChannel()));
            }
            
            if (dayEM.getYyyymmdd() != null) {
                
                
                criteria.add(Restrictions.eq("id.yyyymmdd", dayEM.getYyyymmdd()));
            }
            
            if (dayEM.getMDevType() != null) {
                
                criteria.add(Restrictions.eq("id.mdevType", dayEM.getMDevType()));
            }
            
            if (startDay != null) {
                criteria.add(Restrictions.ge("id.yyyymmdd", startDay));
            }

            if (endDay != null) {
                
                criteria.add(Restrictions.le("id.yyyymmdd", endDay));
            }
        }

        criteria.setProjection( Projections.projectionList().add( Projections.sum("total") ) );
        
        return ((Number)(criteria.list().get(0) == null ? 0.0 : criteria.list().get(0))).doubleValue();
    }

    @Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
    public Map<String, Object> getLast(Integer id) {
        
        String sqlStr = " "
            + "\n select  id.yyyymmdd            as  lastDay "
            + "\n       , total                  as  total "
            + "\n   from  DayEM "
            + "\n  where  contract.id = :contractId "
            + "\n    and  id.mdevType = :mdevType "
            + "\n    and  id.channel =  1 "
            + "\n    and  id.yyyymmdd = ( select  max(id.yyyymmdd) "
            + "\n                           from  DayEM "
            + "\n                          where  contract.id = :contractId) ";

        Query query = getSession().createQuery(sqlStr);

        query.setInteger("contractId", id);
        query.setString("mdevType",CommonConstants.DeviceType.Meter.name());
        
        List<Map<String, Object>> returnList = query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
        
        return 1 == returnList.size() ? returnList.get(0) : null;
    }

    /**
     * method name : getSicEnergyUsageList<b/>
     * method Desc : SIC Load Profile 가젯에서 에너지 사용량 데이터를 조회한다. 
     *
     * @param conditionMap
     * @return
     */
    @Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
    public List<Map<String, Object>> getSicEnergyUsageList(Map<String, Object> conditionMap) {
        Integer supplierId = (Integer)conditionMap.get("supplierId");
        String searchStartDate = StringUtil.nullToBlank(conditionMap.get("searchStartDate"));
        String searchEndDate = StringUtil.nullToBlank(conditionMap.get("searchEndDate"));

        StringBuilder sb = new StringBuilder();

        sb.append("\nSELECT cd.code AS SIC_CODE, ");
        sb.append("\n       SUM(de.total) AS USAGE_SUM ");
        sb.append("\nFROM day_em de, ");
        sb.append("\n     code cd ");
        sb.append("\nWHERE de.channel = :channel ");
        sb.append("\nAND   de.mdev_type = :mdevType ");
        sb.append("\nAND   de.yyyymmdd BETWEEN :searchStartDate AND :searchEndDate ");
        sb.append("\nAND   de.supplier_id = :supplierId ");
        sb.append("\nAND   cd.code = de.sic ");
        sb.append("\nGROUP BY cd.code, cd.id ");

        SQLQuery query = getSession().createSQLQuery(new SQLWrapper().getQuery(sb.toString()));
        query.setInteger("channel", ElectricityChannel.Usage.getChannel());
        query.setString("mdevType", DeviceType.Meter.name());
        query.setString("searchStartDate", searchStartDate);
        query.setString("searchEndDate", searchEndDate);
        query.setInteger("supplierId", supplierId);

        return query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
    }

    /*
     * (non-Javadoc)
     * @see com.aimir.dao.mvm.DayEMDao#getSicIdList()
     */
    @Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
    public List<Map<String, Object>> getSicIdList()
    {
        
        
        List<Map<String, Object>> result = null;

        StringBuilder sb = new StringBuilder();

        /*
         * --sic id별에 따른 name 과 계약 건수 가져오기 and code 
         * select
         * 
         * cont.SIC_ID , cd.CODE , cd.NAME ,count(*) as cont_count
         * 
         * 
         * 
         * from contract cont, code cd where cont.SIC_ID = cd.ID group by
         * cont.SIC_ID, cd.NAME, cd.CODE
         */

        sb.append("\n SELECT cont.sicCodeId AS sicId, ");
        sb.append("\n      cont.sic.code AS sicCode, ");
        sb.append("\n       cont.sic.name AS sicName, ");
        sb.append("\n       COUNT(*) AS customerCount ");
        sb.append("\n FROM Contract cont ");
        sb.append("\n WHERE cont.sicCodeId = cont.sic.id");
        sb.append("\n GROUP BY cont.sicCodeId, cont.sic.name, cont.sic.code");

        Query query = getSession().createQuery(sb.toString());

        
        result = query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
        
        //result = query.list();

        

        return result;
    }
    
    
    
    @SuppressWarnings("unused")
    @Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
    public List<Map<String, Object>> getSicCustomerEnergyUsageList2(Map<String, Object> conditionMap, boolean isTotal) 
    {
        List<Map<String, Object>> result;
        Integer supplierId = (Integer)conditionMap.get("supplierId");
        String searchStartDate = StringUtil.nullToBlank(conditionMap.get("searchStartDate"));
        String searchEndDate = StringUtil.nullToBlank(conditionMap.get("searchEndDate"));
        String sicId = (String) conditionMap.get("sicId");
        String sicCode = (String) conditionMap.get("sicCode");

        StringBuilder sb = new StringBuilder();
        

        /*  
        
                --sic_id에 따른 에너지 소비량 가져오기 by_sic_id
                select   
                    sum( em.TOTAL) as total
                
                    
                from    day_em em 
                    where   em.CONTRACT_ID in ( select distinct cont.id as cont_id  from contract cont  where cont.SIC_ID=576) --농업및 임업
                    
                        and em.CHANNEL=1
                        and em.YYYYMMDD between '20090107' and '20120807' 
                        and em.MDEV_TYPE='Meter'
                        and em.SUPPLIER_ID=22
            
            */

        sb.append("\n SELECT  ");
        sb.append("\n       SUM(em.total) AS usageSum ");
        sb.append("\n FROM DayEM em ");
        sb.append("\n WHERE em.contractId in ( select distinct cont.id as cont_id  from Contract cont  where cont.sicCodeId=:sicId)    ");
        
        
        
        //sb.append("\n WHERE em.sic in ( select distinct cont.sic.code as sicCode  from Contract cont  where cont.sic.code=:sicCode)    ");

        
        sb.append("\n AND   em.id.channel = :channel ");
        sb.append("\n AND   em.id.channel = :channel ");
        sb.append("\n AND   em.id.yyyymmdd BETWEEN :searchStartDate AND :searchEndDate ");
        sb.append("\n AND   em.id.mdevType = :mdevType ");
        sb.append("\n AND   em.supplier.id = :supplierId ");
        sb.append("\nAND   em.sic IS NOT NULL ");
        
        
        
      //  sb.append("\nAND   ct.id = em.contract.id ");

        
        
        

        Query query = getSession().createQuery(sb.toString());
        query.setInteger("channel", ElectricityChannel.Usage.getChannel());
        query.setString("searchStartDate", searchStartDate);
        query.setString("searchEndDate", searchEndDate);
        
        //sicId
        query.setString("sicId", sicId);
        
        //sicCOde
        //query.setString("sicCode", sicCode);
        
        int channel = ElectricityChannel.Usage.getChannel();
        String mdevType = DeviceType.Meter.name();
        
        
        
        query.setString("mdevType", DeviceType.Meter.name());
        query.setInteger("supplierId", supplierId);
        
        
        result = query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();

        return result;
    }
    
    

    /**
     * method name : getSicCustomerEnergyUsageTotalSum<b/>
     * method Desc : SIC Load Profile 미니가젯의 List 의 Total 데이터를 조회한다. 
     *
     * @param conditionMap
     * @return
     */
    @Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
    public List<Map<String, Object>> getSicCustomerEnergyUsageTotalSum(Map<String, Object> conditionMap) {
        Integer supplierId = (Integer)conditionMap.get("supplierId");
        String searchStartDate = StringUtil.nullToBlank(conditionMap.get("searchStartDate"));
        String searchEndDate = StringUtil.nullToBlank(conditionMap.get("searchEndDate"));

        StringBuilder sb = new StringBuilder();

        sb.append("\nSELECT 0 AS sicId, ");
        sb.append("\n       '0' AS sicCode, ");
        sb.append("\n       'Total' AS sicName, ");
        sb.append("\n       COUNT(DISTINCT ct.id) AS customerCount, ");
        sb.append("\n       SUM(em.total) AS usageSum ");
        sb.append("\nFROM DayEM em, ");
        sb.append("\n     Contract ct ");
        sb.append("\nWHERE em.id.channel = :channel ");
        sb.append("\nAND   em.id.mdevType = :mdevType ");
        sb.append("\nAND   em.id.yyyymmdd BETWEEN :searchStartDate AND :searchEndDate ");
        sb.append("\nAND   em.supplier.id = :supplierId ");
        sb.append("\nAND   ct.id = em.contract.id ");
        sb.append("\nAND   em.sic IS NOT NULL ");

        Query query = getSession().createQuery(sb.toString());
        query.setInteger("channel", ElectricityChannel.Usage.getChannel());
        query.setString("mdevType", DeviceType.Meter.name());
        query.setString("searchStartDate", searchStartDate);
        query.setString("searchEndDate", searchEndDate);
        query.setInteger("supplierId", supplierId);

        return query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
    }

    /**
     * method name : getSicLoadProfileChartDataByDayAvg<b/>
     * method Desc : SIC Load Profile 맥스가젯의 LoadProfileChart 의 WorkingDay/Saturday/Sunday/Holiday Avg Data 를 조회한다.
     *
     * @param conditionMap
     * @return
     */
    @Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
    public List<Map<String, Object>> getSicLoadProfileChartDataByDayAvg(Map<String, Object> conditionMap) {
        Integer supplierId = (Integer)conditionMap.get("supplierId");
        String searchStartDate = StringUtil.nullToBlank(conditionMap.get("searchStartDate"));
        String searchEndDate = StringUtil.nullToBlank(conditionMap.get("searchEndDate"));
        String sicCode = StringUtil.nullToBlank(conditionMap.get("sicCode"));
        Integer dayType = (Integer)conditionMap.get("dayType");     // 0:working day, 1:saturday, 2:sunday, 3:holiday

        StringBuilder sb = new StringBuilder();

        sb.append("\nSELECT AVG(de.value_00) AS value_00, ");
        sb.append("\n       AVG(de.value_01) AS value_01, ");
        sb.append("\n       AVG(de.value_02) AS value_02, ");
        sb.append("\n       AVG(de.value_03) AS value_03, ");
        sb.append("\n       AVG(de.value_04) AS value_04, ");
        sb.append("\n       AVG(de.value_05) AS value_05, ");
        sb.append("\n       AVG(de.value_06) AS value_06, ");
        sb.append("\n       AVG(de.value_07) AS value_07, ");
        sb.append("\n       AVG(de.value_08) AS value_08, ");
        sb.append("\n       AVG(de.value_09) AS value_09, ");
        sb.append("\n       AVG(de.value_10) AS value_10, ");
        sb.append("\n       AVG(de.value_11) AS value_11, ");
        sb.append("\n       AVG(de.value_12) AS value_12, ");
        sb.append("\n       AVG(de.value_13) AS value_13, ");
        sb.append("\n       AVG(de.value_14) AS value_14, ");
        sb.append("\n       AVG(de.value_15) AS value_15, ");
        sb.append("\n       AVG(de.value_16) AS value_16, ");
        sb.append("\n       AVG(de.value_17) AS value_17, ");
        sb.append("\n       AVG(de.value_18) AS value_18, ");
        sb.append("\n       AVG(de.value_19) AS value_19, ");
        sb.append("\n       AVG(de.value_20) AS value_20, ");
        sb.append("\n       AVG(de.value_21) AS value_21, ");
        sb.append("\n       AVG(de.value_22) AS value_22, ");
        sb.append("\n       AVG(de.value_23) AS value_23 ");
        sb.append("\nFROM DayEM de ");
        sb.append("\nWHERE de.id.channel = :channel ");
        sb.append("\nAND   de.id.mdevType = :mdevType ");
        sb.append("\nAND   de.id.yyyymmdd BETWEEN :searchStartDate AND :searchEndDate ");
        sb.append("\nAND   de.supplier.id = :supplierId ");
        sb.append("\nAND   de.dayType = :dayType ");
        sb.append("\nAND   de.sic = :sicCode ");
        
        Query query = getSession().createQuery(sb.toString());
        query.setInteger("channel", ElectricityChannel.Usage.getChannel());
        query.setString("mdevType", DeviceType.Meter.name());
        query.setString("searchStartDate", searchStartDate);
        query.setString("searchEndDate", searchEndDate);
        query.setInteger("supplierId", supplierId);
        query.setInteger("dayType", dayType);
        query.setString("sicCode", sicCode);

        return query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
    }

    /**
     * method name : getSicLoadProfileChartDataByDaySum<b/>
     * method Desc : SIC Load Profile 맥스가젯의 LoadProfileChart 의 WorkingDay/Saturday/Sunday/Holiday Sum Data 를 조회한다.
     *
     * @param conditionMap
     * @return
     */
    @Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
    public List<Map<String, Object>> getSicLoadProfileChartDataByDaySum(Map<String, Object> conditionMap) {
        Integer supplierId = (Integer)conditionMap.get("supplierId");
        String searchStartDate = StringUtil.nullToBlank(conditionMap.get("searchStartDate"));
        String searchEndDate = StringUtil.nullToBlank(conditionMap.get("searchEndDate"));
        String sicCode = StringUtil.nullToBlank(conditionMap.get("sicCode"));
        Integer dayType = (Integer)conditionMap.get("dayType");     // 0:working day, 1:saturday, 2:sunday, 3:holiday

        StringBuilder sb = new StringBuilder();

        sb.append("\nSELECT SUM(de.value_00) AS value_00, ");
        sb.append("\n       SUM(de.value_01) AS value_01, ");
        sb.append("\n       SUM(de.value_02) AS value_02, ");
        sb.append("\n       SUM(de.value_03) AS value_03, ");
        sb.append("\n       SUM(de.value_04) AS value_04, ");
        sb.append("\n       SUM(de.value_05) AS value_05, ");
        sb.append("\n       SUM(de.value_06) AS value_06, ");
        sb.append("\n       SUM(de.value_07) AS value_07, ");
        sb.append("\n       SUM(de.value_08) AS value_08, ");
        sb.append("\n       SUM(de.value_09) AS value_09, ");
        sb.append("\n       SUM(de.value_10) AS value_10, ");
        sb.append("\n       SUM(de.value_11) AS value_11, ");
        sb.append("\n       SUM(de.value_12) AS value_12, ");
        sb.append("\n       SUM(de.value_13) AS value_13, ");
        sb.append("\n       SUM(de.value_14) AS value_14, ");
        sb.append("\n       SUM(de.value_15) AS value_15, ");
        sb.append("\n       SUM(de.value_16) AS value_16, ");
        sb.append("\n       SUM(de.value_17) AS value_17, ");
        sb.append("\n       SUM(de.value_18) AS value_18, ");
        sb.append("\n       SUM(de.value_19) AS value_19, ");
        sb.append("\n       SUM(de.value_20) AS value_20, ");
        sb.append("\n       SUM(de.value_21) AS value_21, ");
        sb.append("\n       SUM(de.value_22) AS value_22, ");
        sb.append("\n       SUM(de.value_23) AS value_23 ");
        sb.append("\nFROM DayEM de ");
        sb.append("\nWHERE de.id.channel = :channel ");
        sb.append("\nAND   de.id.mdevType = :mdevType ");
        sb.append("\nAND   de.id.yyyymmdd BETWEEN :searchStartDate AND :searchEndDate ");
        sb.append("\nAND   de.supplier.id = :supplierId ");
        sb.append("\nAND   de.dayType = :dayType ");
        sb.append("\nAND   de.sic = :sicCode ");
        
        Query query = getSession().createQuery(sb.toString());
        query.setInteger("channel", ElectricityChannel.Usage.getChannel());
        query.setString("mdevType", DeviceType.Meter.name());
        query.setString("searchStartDate", searchStartDate);
        query.setString("searchEndDate", searchEndDate);
        query.setInteger("supplierId", supplierId);
        query.setInteger("dayType", dayType);
        query.setString("sicCode", sicCode);

        return query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
    }

    /**
     * method name : getSicLoadProfileChartDataByDayMax<b/>
     * method Desc : SIC Load Profile 맥스가젯의 LoadProfileChart 의 PeakDay Data 를 조회한다.
     *
     * @param conditionMap
     * @return
     */
    @Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
    public List<Map<String, Object>> getSicLoadProfileChartDataByPeakDay(Map<String, Object> conditionMap) {
        Integer supplierId = (Integer)conditionMap.get("supplierId");
        String searchStartDate = StringUtil.nullToBlank(conditionMap.get("searchStartDate"));
        String searchEndDate = StringUtil.nullToBlank(conditionMap.get("searchEndDate"));
        String sicCode = StringUtil.nullToBlank(conditionMap.get("sicCode"));

        StringBuilder sb = new StringBuilder();

        sb.append("\nSELECT MAX(de.value_00) AS value_00, ");
        sb.append("\n       MAX(de.value_01) AS value_01, ");
        sb.append("\n       MAX(de.value_02) AS value_02, ");
        sb.append("\n       MAX(de.value_03) AS value_03, ");
        sb.append("\n       MAX(de.value_04) AS value_04, ");
        sb.append("\n       MAX(de.value_05) AS value_05, ");
        sb.append("\n       MAX(de.value_06) AS value_06, ");
        sb.append("\n       MAX(de.value_07) AS value_07, ");
        sb.append("\n       MAX(de.value_08) AS value_08, ");
        sb.append("\n       MAX(de.value_09) AS value_09, ");
        sb.append("\n       MAX(de.value_10) AS value_10, ");
        sb.append("\n       MAX(de.value_11) AS value_11, ");
        sb.append("\n       MAX(de.value_12) AS value_12, ");
        sb.append("\n       MAX(de.value_13) AS value_13, ");
        sb.append("\n       MAX(de.value_14) AS value_14, ");
        sb.append("\n       MAX(de.value_15) AS value_15, ");
        sb.append("\n       MAX(de.value_16) AS value_16, ");
        sb.append("\n       MAX(de.value_17) AS value_17, ");
        sb.append("\n       MAX(de.value_18) AS value_18, ");
        sb.append("\n       MAX(de.value_19) AS value_19, ");
        sb.append("\n       MAX(de.value_20) AS value_20, ");
        sb.append("\n       MAX(de.value_21) AS value_21, ");
        sb.append("\n       MAX(de.value_22) AS value_22, ");
        sb.append("\n       MAX(de.value_23) AS value_23 ");
//        sb.append("\nFROM DayEM de, ");
//        sb.append("\n     Contract ct ");
        sb.append("\nFROM DayEM de ");
        sb.append("\nWHERE de.id.channel = :channel ");
        sb.append("\nAND   de.id.mdevType = :mdevType ");
        sb.append("\nAND   de.id.yyyymmdd BETWEEN :searchStartDate AND :searchEndDate ");
        sb.append("\nAND   de.supplier.id = :supplierId ");
        sb.append("\nAND   de.sic = :sicCode ");
//        sb.append("\nAND   ct.id = de.contract.id ");
//        sb.append("\nAND   ct.sic.code = :sicCode ");

        Query query = getSession().createQuery(sb.toString());
        query.setInteger("channel", ElectricityChannel.Usage.getChannel());
        query.setString("mdevType", DeviceType.Meter.name());
        query.setString("searchStartDate", searchStartDate);
        query.setString("searchEndDate", searchEndDate);
        query.setInteger("supplierId", supplierId);
        query.setString("sicCode", sicCode);

        return query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
    }

    /**
     * method name : getSicTotalLoadProfileChartData<b/>
     * method Desc : SIC Load Profile 맥스가젯의 Total Load Profile Chart 의 Data 를 조회한다.
     *
     * @param conditionMap
     * @return
     */
    @Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
    public List<Map<String, Object>> getSicTotalLoadProfileChartData(Map<String, Object> conditionMap) {
        Integer supplierId = (Integer)conditionMap.get("supplierId");
        String searchStartDate = StringUtil.nullToBlank(conditionMap.get("searchStartDate"));
        String searchEndDate = StringUtil.nullToBlank(conditionMap.get("searchEndDate"));

        StringBuilder sb = new StringBuilder();

        sb.append("\nSELECT de.sic AS sicCode, ");
        sb.append("\n       cd.name AS sicName, ");
        sb.append("\n       SUM(de.value_00)/1000 AS value_00, ");
        sb.append("\n       SUM(de.value_01)/1000 AS value_01, ");
        sb.append("\n       SUM(de.value_02)/1000 AS value_02, ");
        sb.append("\n       SUM(de.value_03)/1000 AS value_03, ");
        sb.append("\n       SUM(de.value_04)/1000 AS value_04, ");
        sb.append("\n       SUM(de.value_05)/1000 AS value_05, ");
        sb.append("\n       SUM(de.value_06)/1000 AS value_06, ");
        sb.append("\n       SUM(de.value_07)/1000 AS value_07, ");
        sb.append("\n       SUM(de.value_08)/1000 AS value_08, ");
        sb.append("\n       SUM(de.value_09)/1000 AS value_09, ");
        sb.append("\n       SUM(de.value_10)/1000 AS value_10, ");
        sb.append("\n       SUM(de.value_11)/1000 AS value_11, ");
        sb.append("\n       SUM(de.value_12)/1000 AS value_12, ");
        sb.append("\n       SUM(de.value_13)/1000 AS value_13, ");
        sb.append("\n       SUM(de.value_14)/1000 AS value_14, ");
        sb.append("\n       SUM(de.value_15)/1000 AS value_15, ");
        sb.append("\n       SUM(de.value_16)/1000 AS value_16, ");
        sb.append("\n       SUM(de.value_17)/1000 AS value_17, ");
        sb.append("\n       SUM(de.value_18)/1000 AS value_18, ");
        sb.append("\n       SUM(de.value_19)/1000 AS value_19, ");
        sb.append("\n       SUM(de.value_20)/1000 AS value_20, ");
        sb.append("\n       SUM(de.value_21)/1000 AS value_21, ");
        sb.append("\n       SUM(de.value_22)/1000 AS value_22, ");
        sb.append("\n       SUM(de.value_23)/1000 AS value_23 ");
        sb.append("\nFROM DayEM de, ");
        sb.append("\n     Code cd ");
        sb.append("\nWHERE de.id.channel = :channel ");
        sb.append("\nAND   de.id.mdevType = :mdevType ");
        sb.append("\nAND   de.id.yyyymmdd BETWEEN :searchStartDate AND :searchEndDate ");
        sb.append("\nAND   de.supplier.id = :supplierId ");
        sb.append("\nAND   de.sic IS NOT NULL ");
//        sb.append("\nAND   de.sic != '11.A' ");
        sb.append("\nAND   cd.code = de.sic ");
        sb.append("\nGROUP BY cd.name, de.sic ");

        Query query = getSession().createQuery(sb.toString());
        query.setInteger("channel", ElectricityChannel.Usage.getChannel());
        query.setString("mdevType", DeviceType.Meter.name());
        query.setString("searchStartDate", searchStartDate);
        query.setString("searchEndDate", searchEndDate);
        query.setInteger("supplierId", supplierId);

        return query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
    }

    /**
     * method name : getMeteringSuccessCountListPerLocation<b/>
     * method Desc : 
     *
     * @param conditionMap
     * @return
     */
    @Deprecated
    @Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
    public List<Map<String, Object>> getMeteringSuccessCountListPerLocation(Map<String, Object> conditionMap) {
        Integer supplierId = (Integer)conditionMap.get("supplierId");
        String meterType = StringUtil.nullToBlank(conditionMap.get("meterType"));
        String searchStartDate = StringUtil.nullToBlank(conditionMap.get("searchStartDate"));
        String searchEndDate = StringUtil.nullToBlank(conditionMap.get("searchEndDate"));

        // 미터 타입별 미터링데이터 테이블 설정
        String meteringDataTable = CommonConstants.MeterType.valueOf(meterType).getMeteringTableName();

        StringBuilder sb = new StringBuilder();

//        sb.append("\nSELECT c.location_id AS LOC_ID, ");
//        sb.append("\n       COUNT(c.mdev_id) AS SUCCESS_CNT ");
//        sb.append("\nFROM ( ");
//        sb.append("\n    SELECT m.location_id, ");
//        sb.append("\n           d.mdev_id ");
//        sb.append("\n    FROM ").append(meteringDataTable).append(" d, ");
//        sb.append("\n         meter m, ");
//        sb.append("\n         location p ");
//        sb.append("\n    WHERE d.meter_id = m.id ");
//        sb.append("\n    AND   p.id = m.location_id ");
//        sb.append("\n    AND   m.meter = :meterType ");
//        sb.append("\n    AND   m.install_date <= :installDate ");
//        sb.append("\n    AND   m.supplier_id = :supplierId ");
//        sb.append("\n    AND   d.yyyymmddhhmmss BETWEEN :startDateTime AND :endDateTime ");
//        sb.append("\n    AND   d.mdev_type = :mdevType ");
//        sb.append("\n    GROUP BY m.location_id, d.mdev_id ");
//        sb.append("\n) c ");
//        sb.append("\nGROUP BY c.location_id ");

        sb.append("\nSELECT c.location_id AS LOC_ID, ");
        sb.append("\n       COUNT(c.mdev_id) AS SUCCESS_CNT ");
        sb.append("\nFROM ( ");
        sb.append("\n    SELECT m.location_id, ");
        sb.append("\n           d.mdev_id ");
        sb.append("\n    FROM day_em d, ");
        sb.append("\n         meter m, ");
        sb.append("\n         location p ");
        sb.append("\n    WHERE d.meter_id = m.id ");
        sb.append("\n    AND   p.id = m.location_id ");
        sb.append("\n    AND   m.meter = :meterType ");
        sb.append("\n    AND   m.install_date <= :installDate ");
        sb.append("\n    AND   m.supplier_id = :supplierId ");
        sb.append("\n    AND   d.yyyymmdd BETWEEN :startDate AND :endDate ");
        sb.append("\n    AND   d.mdev_type = :mdevType ");
        sb.append("\n    AND   d.channel = :channel ");
        sb.append("\n    GROUP BY m.location_id, d.mdev_id ");
        sb.append("\n) c ");
        sb.append("\nGROUP BY c.location_id ");

        SQLQuery query = getSession().createSQLQuery(new SQLWrapper().getQuery(sb.toString()));
        query.setString("startDate", searchStartDate);
        query.setString("endDate", searchEndDate);
//        query.setString("startDateTime", searchStartDate + "000000");
//        query.setString("endDateTime", searchEndDate + "235959");
        query.setString("installDate", searchEndDate + "235959");
        query.setString("meterType", meterType);
        query.setInteger("channel", DefaultChannel.Usage.getCode());
        query.setInteger("supplierId", supplierId);
        query.setString("mdevType", CommonConstants.DeviceType.Meter.name());

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
        if(DateType.WEEKLY.equals(dateType)) {
            return getConsumptionEmCo2WeekManualMonitoring(condition);
        }
        else {
            return new ArrayList<Object>();
        }
    }
        
    /**
     * 수검침 주간 조회
     * 
     * 기존 소스를 거의 그대로 Copy 함.
     * 
     * XXX: 추후 리팩토링이 필요하다.
     * @param condition
     * @return
     */
    private List<Object> getConsumptionEmCo2WeekManualMonitoring(Map<String, Object> condition) {

        Integer meterId = (Integer) condition.get("meterId");
        String startDate = (String) condition.get("startDate");
        String endDate = (String) condition.get("endDate");

        StringBuffer sb = new StringBuffer();

        sb.append("\n    SELECT EM.YYYYMMDD AS YYYYMMDD, EM.TOTAL AS EM_TOTAL, CO2.TOTAL AS CO2_TOTAL  ");
        sb.append("\n      FROM (      ");
        sb.append("\n             SELECT D.YYYYMMDD , SUM(D.TOTAL) AS TOTAL ");
        sb.append("\n               FROM DAY_EM D , (      ");
        sb.append("\n                       SELECT M.MDS_ID , L.ID , L.NAME  ");
        sb.append("\n                         FROM METER M , LOCATION L  ");
        sb.append("\n                        WHERE M.LOCATION_ID = L.ID     ");
        sb.append("\n                          AND M.ID = :meterId ");
        sb.append("\n                    ) LL  ");
        sb.append("\n               WHERE D.MDEV_ID = LL.MDS_ID  ");
        sb.append("\n                 AND D.CHANNEL = 1       ");
        if(startDate != null && endDate != null) {
            sb.append("\n                 AND D.YYYYMMDD between :startDate and :endDate  ");
        }
        sb.append("\n               GROUP BY D.YYYYMMDD   ");
        sb.append("\n      ) EM ");
        sb.append("\n      LEFT JOIN ");
        sb.append("\n      ( ");
        sb.append("\n              SELECT D2.YYYYMMDD , SUM(D2.TOTAL) AS TOTAL ");
        sb.append("\n                FROM DAY_EM D2 , ( ");
        sb.append("\n                       SELECT M2.MDS_ID , L2.ID , L2.NAME  ");
        sb.append("\n                        FROM METER M2 , LOCATION L2  ");
        sb.append("\n                       WHERE M2.LOCATION_ID = L2.ID     ");
        sb.append("\n                         AND M2.ID = :meterId  ");
        sb.append("\n                     ) LL2  ");
        sb.append("\n               WHERE D2.MDEV_ID = LL2.MDS_ID  ");
        sb.append("\n                 AND D2.CHANNEL = 0       ");
        if(startDate != null && endDate != null) {
            sb.append("\n                 AND D2.YYYYMMDD between :startDate and :endDate  ");
        }
        sb.append("\n               GROUP BY D2.YYYYMMDD    ");
        sb.append("\n         ) CO2    ");
        sb.append("\n    ON EM.YYYYMMDD = CO2.YYYYMMDD ");
        // sb.append("\n   ORDER BY EM.YYYYMMDD ASC  ");
        
        SQLQuery query = getSession().createSQLQuery(sb.toString());
        
        query.setInteger("meterId", meterId);
        if(startDate != null && endDate != null) {
            query.setString("startDate", startDate);
            query.setString("endDate", endDate);
        }
        
        return query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
    }
    
    @Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
    public List<Object> getConsumptionEmValueSum(int supplierId, String startDate, String endDate, int starthour, int endhour) {

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
        sb.append("\n  FROM DAY_EM");
        sb.append("\n  WHERE");
        if(startDate != null && endDate != null) {
            sb.append("\n YYYYMMDD BETWEEN :startDate AND :endDate");
        }
        sb.append("\n   AND CHANNEL = :channel ");
        sb.append("\n   AND MDEV_TYPE = :mdevType ");
        sb.append("\n   AND SUPPLIER_ID = :supplierid");
        
        SQLQuery query = getSession().createSQLQuery(sb.toString());

        if(startDate != null && startDate != null) {
            query.setString("startDate", startDate);
            query.setString("endDate", endDate);
        }
        
        query.setInteger("channel", ElectricityChannel.Usage.getChannel());
        query.setString("mdevType", DeviceType.Meter.name());
        query.setInteger("supplierid", supplierId);
//      logger.debug("sql string : "+ query.toString());
        return query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();

    }   
    
    @Override
    @Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
    public double getSumTotalUsageByCondition(Set<Condition> conditions) {
        conditions.add(
            new Condition("id.channel", new Object[]{ 1 }, null, Restriction.EQ)
        );
        List<Object> list = getSumFieldByCondition(conditions, "total");
        double sum = 0;
        if(list != null && !list.isEmpty()) {
            Object obj = list.get(0);
            sum = (obj != null) ? (Double)obj : 0;
        }
        return sum;
    }
    
    @Override
    @SuppressWarnings("unchecked")
    @Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
    public Map<String, Double> getSumUsageByCondition(Set<Condition> conditions) {
        
        Criteria criteria = getSession().createCriteria(getPersistentClass());
        conditions.add(
            new Condition("id.channel", new Object[]{ 1 }, null, Restriction.EQ)
        );
        if (conditions != null && !conditions.isEmpty()) {
            for (Condition condition : conditions) {
                Criterion addCriterion = SearchCondition.getCriterion(condition);
                if (addCriterion != null) {
                    criteria.add(addCriterion);
                }
            }
        }
        
        ProjectionList pList = Projections.projectionList();
        String field = "value_";
        for(int i=0; i < 24; i++) {         
            pList.add(Projections.sum((i < 10) ? field + "0" + i : field + i).as("H" + i));
        }
        criteria.setProjection(pList);
        return (Map<String, Double>) criteria.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).uniqueResult();
    }

    @Override
    @Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
    public List<Map<String, Object>> getDayEmsZoneUsage(Map<String, Object> conditions) {
        
        String startDate = (String) conditions.get("startDate");
        String endDate = (String) conditions.get("endDate");
        
        StringBuffer sb = new StringBuffer();
        
        sb.append("\n    SELECT SUM(TOTAL) AS TOTAL, L.NAME AS NAME");
        sb.append("\n    FROM DAY_EM D, LOCATION L  ");
        sb.append("\n    WHERE D.LOCATION_ID = L.ID ");
        sb.append("\n    AND D.YYYYMMDD >= :startDate and D.YYYYMMDD <= :endDate");
        sb.append("\n    AND D.CHANNEL = 1");
        sb.append("\n    AND L.PARENT_ID = 1");
        sb.append("\n    GROUP BY L.NAME");
        
        SQLQuery query = getSession().createSQLQuery(sb.toString());

        if(startDate != null && endDate != null) {
            query.setString("startDate", startDate);
            query.setString("endDate", endDate);
        }
        
        return  query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
    }

    @Override
    @Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
    public List<Map<String, Object>> getTotalDayEmsZoneUsage(
            Map<String, Object> conditions) {
        
        String startDate = (String) conditions.get("startDate");
        String endDate = (String) conditions.get("endDate");

        StringBuffer sb = new StringBuffer();

        sb.append("\n    SELECT SUM(TOTAL) AS TOTAL");
        sb.append("\n    FROM DAY_EM D, LOCATION L  ");
        sb.append("\n    WHERE D.LOCATION_ID = L.ID ");
        sb.append("\n    AND D.YYYYMMDD >= :startDate and D.YYYYMMDD <= :endDate");
        sb.append("\n    AND D.CHANNEL = 1");
        sb.append("\n    AND L.PARENT_ID = 1");

        SQLQuery query = getSession().createSQLQuery(sb.toString());

        if(startDate != null && endDate != null) {
            query.setString("startDate", startDate);
            query.setString("endDate", endDate);
        }

        return  query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
    }

    @Override
    @Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
    public List<Map<String, Object>> getDayEmsLocationUsage(Map<String, Object> conditions) {
        
        String startDate = (String) conditions.get("startDate");
        String endDate = (String) conditions.get("endDate");
        
        StringBuffer sb = new StringBuffer();
        
        sb.append("\n    SELECT SUM(D.TOTAL) AS TOTAL, Z.NAME AS NAME");
        sb.append("\n    FROM ENDDEVICE E, ZONE Z, DAY_EM D, METER M  ");
        sb.append("\n    WHERE E.ZONE_ID = Z.ID ");
        sb.append("\n    AND E.ID = M.ENDDEVICE_ID");
        sb.append("\n    AND M.MDS_ID = D.MDEV_ID");
        sb.append("\n    AND Z.PARENT_ID IS NOT NULL");
        sb.append("\n    AND D.YYYYMMDD BETWEEN :startDate AND :endDate");
        sb.append("\n    GROUP BY Z.NAME");
        
        SQLQuery query = getSession().createSQLQuery(sb.toString());

        if(startDate != null && endDate != null) {
            query.setString("startDate", startDate);
            query.setString("endDate", endDate);
        }
        
        return  query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
    }

    @Override
    @Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
    public List<Map<String, Object>> getTotalDayEmsLocationUsage(
            Map<String, Object> conditions) {
        
        String startDate = (String) conditions.get("startDate");
        String endDate = (String) conditions.get("endDate");

        StringBuffer sb = new StringBuffer();

        sb.append("\n    SELECT SUM(D.TOTAL) AS TOTAL");
        sb.append("\n    FROM ENDDEVICE E, ZONE Z, DAY_EM D, METER M  ");
        sb.append("\n    WHERE E.ZONE_ID = Z.ID ");
        sb.append("\n    AND E.ID = M.ENDDEVICE_ID");
        sb.append("\n    AND M.MDS_ID = D.MDEV_ID");
        sb.append("\n    AND Z.PARENT_ID IS NOT NULL");
        sb.append("\n    AND D.YYYYMMDD BETWEEN :startDate AND :endDate");
        
        SQLQuery query = getSession().createSQLQuery(sb.toString());

        if(startDate != null && endDate != null) {
            query.setString("startDate", startDate);
            query.setString("endDate", endDate);
        }

        return  query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
    }

	@Override
	@Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
	public List<Map<String, Object>> getSicCustomerEnergyUsageList(
			Map<String, Object> conditionMap, boolean isTotal)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	@Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
	public List<Map<String, Object>> getSicIdList2(
			Map<String, Object> conditionMap)
	{
		// TODO Auto-generated method stub
		return null;
	}
	
	/**
     * BEMS 동별 일(시간)전력사용량 , 탄소배출량 차트 데이터 조회. 기준 데이터 키로 Location 테이블의 PARENT_ID를
     * 사용.
     */
	@Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
    public List<Object> getConsumptionSumEmCo2DayMonitoringParentId(
            Map<String, Object> condition) {

        logger
                .info("BEMS 동별   일(시간) 에너지사용량 , 탄소배출량  차트 데이터 조회.\n==== conditions ====\n"
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

        sb.append("\n   SELECT LL.ORDERNO , LL.ID AS LOCATION_ID , LL.NAME , LL.MDS_ID , D.CHANNEL , SUM(D.TOTAL) TOTAL FROM "+ meterType + " D , ( ");
        sb.append("\n        SELECT M.MDS_ID , L.ID , L.NAME , L.ORDERNO FROM METER M , (  ");
        sb.append("\n           SELECT ID , NAME , ORDERNO  FROM LOCATION WHERE PARENT_ID = :parentId ");
        sb.append("\n   ) L WHERE M.LOCATION_ID = L.ID ");
        sb.append("\n   ) LL WHERE D.MDEV_ID = LL.MDS_ID AND D.CHANNEL=:channel ");
        
        if (CommonConstants.DateType.DAILY.getCode().equals(searchDateType)) { // 일별
            sb.append("    AND D.YYYYMMDD = :startDate    ");
        } else if (CommonConstants.DateType.WEEKLY.getCode().equals(searchDateType)) { // 주/월/분기별
            sb.append("    AND D.YYYYMMDD between :startDate and :endDate  ");
        }
        sb.append("\n   GROUP BY LL.ORDERNO, LL.ID , LL.NAME ,  LL.MDS_ID , D.CHANNEL , D.MDEV_ID   ");
        // sb.append("   ORDER BY LL.ORDERNO  ASC, LL.ID DESC");

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
	
	@Override
	@Transactional(propagation=Propagation.REQUIRES_NEW)
    public void delete(String meterId, String yyyymmdd) {
        String qstr = "DELETE from DayEM WHERE id.mdevId = :meterId AND id.yyyymmdd = :yyyymmdd";
        Query query = getSession().createQuery(qstr);
        query.setString("meterId", meterId);
        query.setString("yyyymmdd", yyyymmdd);
        query.executeUpdate();
    }

	@Override
	@Transactional(propagation=Propagation.REQUIRES_NEW)
	public void oldLPDelete(String mdsId, String bDate) {
        StringBuilder hqlBuf = new StringBuilder();
        hqlBuf.append("DELETE FROM DayEM");
        hqlBuf.append(" WHERE id.yyyymmdd <= ? ");
        hqlBuf.append(" AND id.mdevId = ? ");

        Query query = getSession().createQuery(hqlBuf.toString());
        query.setString(1, bDate);
        query.setString(2, mdsId);
        query.executeUpdate();
        // this.getSession().bulkUpdate(hqlBuf.toString(),
        //    new Object[] {bDate, mdsId} );	
	}
}