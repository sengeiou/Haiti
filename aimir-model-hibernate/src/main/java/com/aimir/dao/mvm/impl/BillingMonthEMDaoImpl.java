/**
 * BillingMonthEMDaoImpl.java Copyright NuriTelecom Limited 2011
 */

package com.aimir.dao.mvm.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.aimir.constants.CommonConstants;
import com.aimir.dao.AbstractHibernateGenericDao;
import com.aimir.dao.mvm.BillingMonthEMDao;
import com.aimir.model.device.Meter;
import com.aimir.model.mvm.BillingMonthEM;
import com.aimir.model.system.Contract;
import com.aimir.util.SQLWrapper;
import com.aimir.util.StringUtil;

/**
 * BillingMonthEMDaoImpl.java Description 
 *
 * 
 * Date          Version     Author   Description
 * 2011. 4. 28.   v1.0       김상연         BillingMonthEM 조회 조건(BillingMonthEM)
 * 2011. 5. 04.   v1.1       김상연         해당 계약의 특정 년 별 조회
 * 2011. 5. 13.   v1.2       김상연        동일 공급사 평균 사용량 조회
 * 2011. 6. 09.   v1.3       김상연        해당 계약 최고 사용 금액 조회
 * 2011. 6. 27.   v1.4       김상연        사용 평균 비용 조회
 *
 */
@Repository(value = "billingmonthemDao")
public class BillingMonthEMDaoImpl extends AbstractHibernateGenericDao<BillingMonthEM, Integer> implements BillingMonthEMDao {

	@SuppressWarnings("unused")
	private static Log logger = LogFactory.getLog(BillingMonthEMDaoImpl.class);
    
	@Autowired
	protected BillingMonthEMDaoImpl(SessionFactory sessionFactory) {
		super(BillingMonthEM.class);
		super.setSessionFactory(sessionFactory);
	}

    /**
     * Billing Data 월별 TOU 데이터 개수 조회
     * 
     * @param conditionMap
     * @return 조회 결과
     */
//    public Long getBillingDataMonthlyCount(Map<String, Object> conditionMap) {
//
//        String startDate = StringUtil.nullToBlank(conditionMap.get("searchStartDate"));
//        String locationCondition = StringUtil.nullToBlank(conditionMap.get("locationCondition"));
//        String tariffIndex = StringUtil.nullToBlank(conditionMap.get("tariffIndexId"));
//        String customerName = StringUtil.nullToBlank(conditionMap.get("customerName"));
//        String contractNo = StringUtil.nullToBlank(conditionMap.get("contractNo"));
//        String meterId = StringUtil.nullToBlank(conditionMap.get("meterId"));
//
//        StringBuilder sb = new StringBuilder();
//        StringBuilder sbLikeParam = null;
//
//        sb.append("SELECT COUNT(*) ");
//        sb.append("FROM BillingMonthEM bill ");
//        sb.append("     LEFT OUTER JOIN bill.contract contract ");
//        sb.append("         LEFT OUTER JOIN bill.contract.customer customer ");
//        sb.append("     LEFT OUTER JOIN bill.meter meter ");
//        sb.append("WHERE bill.id.yyyymmdd = :startDate ");
//        
//        
//        if (!"".equals(locationCondition)) {
//            sb.append("AND   contract.location.id IN (").append(locationCondition).append(") ");
//        }
//        
//        if (!"".equals(tariffIndex) && !"0".equals(tariffIndex)) {
//            sb.append("AND   contract.tariffIndex.id = :tariffIndex ");
//        }
//        
//        if (!"".equals(customerName)) {
//            sb.append("AND   UPPER(customer.name) LIKE UPPER(:customerName) ");
//        }
//        
//        if (!"".equals(contractNo)) {
//            sb.append("AND   contract.contractNumber LIKE :contractNo ");
//        }
//        
//        if (!"".equals(meterId)) {
//            sb.append("AND   meter.mdsId LIKE :meterId ");
//        }
//
//        Query query = getSession().createQuery(sb.toString());
//        // criteria.setProjection(Projections.rowCount());
//
//        query.setString("startDate", startDate);
//        
//        if (!"".equals(tariffIndex) && !"0".equals(tariffIndex)) {
//            query.setString("tariffIndex", tariffIndex);
//        }
//        
//        if (!"".equals(customerName)) {
//            sbLikeParam = new StringBuilder();
//            query.setString("customerName", sbLikeParam.append('%').append(customerName).append('%').toString());
//        }
//        
//        if (!"".equals(contractNo)) {
//            sbLikeParam = new StringBuilder();
//            query.setString("contractNo", sbLikeParam.append('%').append(contractNo).append('%').toString());
//        }
//        
//        if (!"".equals(meterId)) {
//            sbLikeParam = new StringBuilder();
//            query.setString("meterId", sbLikeParam.append('%').append(meterId).append('%').toString());
//        }
//
//        return (Long) query.uniqueResult();
//    }

    /**
     * Billing Data 월별 TOU 조회
     * 
     * @param conditionMap
     * @param isCount
     * @return 조회 결과
     */
    @SuppressWarnings("unchecked")
	public List<Map<String, Object>> getBillingDataMonthly(Map<String, Object> conditionMap, boolean isCount) {

        List<Map<String, Object>> result;
        Map<String, Object> map;
        String startDate = StringUtil.nullToBlank(conditionMap.get("searchStartDate"));
        // 추가
        String endDate = StringUtil.nullToBlank(conditionMap.get("searchEndDate"));
        String locationCondition = StringUtil.nullToBlank(conditionMap.get("locationCondition"));
        String tariffIndex = StringUtil.nullToBlank(conditionMap.get("tariffIndexId"));
        String customerName = StringUtil.nullToBlank(conditionMap.get("customerName"));
        String contractNo = StringUtil.nullToBlank(conditionMap.get("contractNo"));
        String meterId = StringUtil.nullToBlank(conditionMap.get("meterId"));
        int page = Integer.parseInt(StringUtil.nullToZero(conditionMap.get("page")));
        int pageSize = Integer.parseInt(StringUtil.nullToZero(conditionMap.get("pageSize")));
        int supplierId = Integer.parseInt(conditionMap.get("supplierId").toString());

        StringBuilder sb = new StringBuilder();
        StringBuilder sbLikeParam = null;

        if (isCount) {
            sb.append("SELECT COUNT(*) ");
        } else {
            sb.append("SELECT bill.id.yyyymmdd as yyyymmdd, ");
            sb.append("       bill.id.hhmmss as hhmmss, ");
            sb.append("       bill.id.mdevType AS mdevType, ");
            sb.append("       bill.id.mdevId AS mdevId, ");
            sb.append("       contract.id AS detailContractId, ");
            sb.append("       meter.id AS detailMeterId, ");
            sb.append("       customer.name as customerName, ");
            sb.append("       contract.contractNumber as contractNo, ");
            sb.append("       meter.mdsId as meterId, ");
            sb.append("       bill.activeEnergyRateTotal as energyRateTot, ");
            sb.append("       contract.contractDemand as contractDemand, ");
            sb.append("       bill.activePowerMaxDemandRateTotal as demandRateTot, ");
            sb.append("       bill.kVah AS kVah, ");
            sb.append("       bill.maxDmdkVah1RateTotal AS maxDmdkVah, ");
            sb.append("       bill.maxDmdkVah1Rate1 AS maxDmdkVahRate1, ");
            sb.append("       bill.maxDmdkVah1Rate2 AS maxDmdkVahRate2, ");
            sb.append("       bill.maxDmdkVah1Rate3 AS maxDmdkVahRate3, ");
            sb.append("       bill.maxDmdkVah1TimeRateTotal AS maxDmdkVahTime, ");
            sb.append("       bill.maxDmdkVah1TimeRate1 AS maxDmdkVahTimeRate1, ");
            sb.append("       bill.maxDmdkVah1TimeRate2 AS maxDmdkVahTimeRate2, ");
            sb.append("       bill.maxDmdkVah1TimeRate3 AS maxDmdkVahTimeRate3, ");
            sb.append("       bill.importkWhPhaseA AS impkWhPhaseA, ");
            sb.append("       bill.importkWhPhaseB AS impkWhPhaseB, ");
            sb.append("       bill.importkWhPhaseC AS impkWhPhaseC ");
        }
        sb.append("FROM BillingMonthEM bill ");
        sb.append("     LEFT OUTER JOIN bill.contract contract ");
        sb.append("         LEFT OUTER JOIN bill.contract.customer customer ");
        sb.append("     LEFT OUTER JOIN bill.meter meter ");
        //sb.append("WHERE bill.id.yyyymmdd = :startDate ");
        sb.append("WHERE bill.id.yyyymmdd BETWEEN :startDate AND :endDate ");
        sb.append("AND bill.supplierId = :supplierId ");
        
        if (!"".equals(locationCondition)) {
            sb.append("AND   contract.location.id IN (").append(locationCondition).append(") ");
        }
        
        if (!"".equals(tariffIndex) && !"0".equals(tariffIndex)) {
            sb.append("AND   contract.tariffIndex.id = :tariffIndex ");
        }
        
        if (!"".equals(customerName)) {
            sb.append("AND   UPPER(customer.name) LIKE UPPER(:customerName) ");
        }
        
        if (!"".equals(contractNo)) {
            sb.append("AND   contract.contractNumber LIKE :contractNo ");
        }
        
        if (!"".equals(meterId)) {
            sb.append("AND   meter.mdsId LIKE :meterId ");
        }

        if (!isCount) {
            sb.append("ORDER BY bill.id.yyyymmdd DESC, bill.id.hhmmss DESC, customer.name ");
        }

        Query query = getSession().createQuery(sb.toString());
        // criteria.setProjection(Projections.rowCount());

        query.setString("startDate", startDate);
        query.setString("endDate", endDate);
        query.setInteger("supplierId", supplierId);
        
        if (!"".equals(tariffIndex) && !"0".equals(tariffIndex)) {
            query.setString("tariffIndex", tariffIndex);
        }
        
        if (!"".equals(customerName)) {
            sbLikeParam = new StringBuilder();
            query.setString("customerName", sbLikeParam.append('%').append(customerName).append('%').toString());
        }
        
        if (!"".equals(contractNo)) {
            sbLikeParam = new StringBuilder();
            query.setString("contractNo", sbLikeParam.append('%').append(contractNo).append('%').toString());
        }
        
        if (!"".equals(meterId)) {
            sbLikeParam = new StringBuilder();
            query.setString("meterId", sbLikeParam.append('%').append(meterId).append('%').toString());
        }

        if (isCount) {
            map = new HashMap<String, Object>();
            map.put("total", ((Number)query.uniqueResult()).longValue());
            result = new ArrayList<Map<String, Object>>();
            result.add(map);
        } else {
            if (pageSize > 0) {
                query.setFirstResult(page * pageSize);
                query.setMaxResults(pageSize);
            }
            result = query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
        }

        return result;
    }

    /**
     * Billing Data 월별 TOU 리포트 데이터를 조회한다.
     * 
     * @param conditionMap
     * @return 조회 결과
     */
    @SuppressWarnings("unchecked")
	public List<Map<String, Object>> getBillingDataReportMonthly(Map<String, Object> conditionMap) {

        String startDate         = StringUtil.nullToBlank(conditionMap.get("searchStartDate"));
        String endDate           = StringUtil.nullToBlank(conditionMap.get("searchEndDate"));
        String locationCondition = StringUtil.nullToBlank(conditionMap.get("locationCondition"));
        String tariffIndex       = StringUtil.nullToBlank(conditionMap.get("tariffIndexId"));
        String customerName      = StringUtil.nullToBlank(conditionMap.get("customerName"));
        String contractNo        = StringUtil.nullToBlank(conditionMap.get("contractNo"));
        String meterId           = StringUtil.nullToBlank(conditionMap.get("meterId"));
        String mdevId            = StringUtil.nullToBlank(conditionMap.get("mdevId"));
        
        Integer mdevType         = null;
        if(conditionMap.get("mdevType") 		!= null)	mdevType = Integer.parseInt( conditionMap.get("mdevType").toString() );
        
        Integer detailContractId = null;
	    if(conditionMap.get("detailContractId") != null)	detailContractId = Integer.parseInt( conditionMap.get("detailContractId").toString() );
        
	    Integer detailMeterId    = null;
	    if(conditionMap.get("detailMeterId") 	!= null)	detailMeterId = Integer.parseInt( conditionMap.get("detailMeterId").toString() );

	    StringBuilder sb = new StringBuilder();

        sb.append("SELECT bld.yyyymmdd AS YYYYMMDD, ");
        sb.append("       bld.hhmmss AS HHMMSS, ");
        sb.append("       cus.name AS CUSTOMERNAME, ");
        sb.append("       con.contract_number AS CONTRACTNUMBER, ");
        sb.append("       mtr.mds_id AS METERID, ");
        sb.append("       con.contractdemand AS CONTRACTDEMAND, ");
        sb.append("       taf.name AS TARIFFTYPENAME, ");
        sb.append("       loc.name AS LOCATIONNAME, ");
        sb.append("       bld.activeenergyimportratetotal    AS ACTENGYIMPTOT, ");
        sb.append("       bld.activeenergyexportratetotal    AS ACTENGYEXPTOT, ");
        sb.append("       bld.rtvEnergyLagImpRateTot         AS RACTENGYLAGIMPTOT, ");
        sb.append("       bld.rtvEnergyLeadImpRateTot        AS RACTENGYLEADIMPTOT, ");
        sb.append("       bld.rtvEnergyLagExpRateTot         AS RACTENGYLAGEXPTOT, ");
        sb.append("       bld.rtvEnergyLeadExpRateTot        AS RACTENGYLEADEXPTOT, ");
        sb.append("       bld.activeenergyimportrate1        AS ACTENGYIMPRAT1, ");
        sb.append("       bld.activeenergyexportrate1        AS ACTENGYEXPRAT1, ");
        sb.append("       bld.reactiveenergylagimportrate1   AS RACTENGYLAGIMPRAT1, ");
        sb.append("       bld.reactiveenergyleadimportrate1  AS RACTENGYLEADIMPRAT1, ");
        sb.append("       bld.reactiveenergylagexportrate1   AS RACTENGYLAGEXPRAT1, ");
        sb.append("       bld.reactiveenergyleadexportrate1  AS RACTENGYLEADEXPRAT1, ");
        sb.append("       bld.activeenergyimportrate2        AS ACTENGYIMPRAT2, ");
        sb.append("       bld.activeenergyexportrate2        AS ACTENGYEXPRAT2, ");
        sb.append("       bld.reactiveenergylagimportrate2   AS RACTENGYLAGIMPRAT2, ");
        sb.append("       bld.reactiveenergyleadimportrate2  AS RACTENGYLEADIMPRAT2, ");
        sb.append("       bld.reactiveenergylagexportrate2   AS RACTENGYLAGEXPRAT2, ");
        sb.append("       bld.reactiveenergyleadexportrate2  AS RACTENGYLEADEXPRAT2, ");
        sb.append("       bld.activeenergyimportrate3        AS ACTENGYIMPRAT3, ");
        sb.append("       bld.activeenergyexportrate3        AS ACTENGYEXPRAT3, ");
        sb.append("       bld.reactiveenergylagimportrate3   AS RACTENGYLAGIMPRAT3, ");
        sb.append("       bld.reactiveenergyleadimportrate3  AS RACTENGYLEADIMPRAT3, ");
        sb.append("       bld.reactiveenergylagexportrate3   AS RACTENGYLAGEXPRAT3, ");
        sb.append("       bld.reactiveenergyleadexportrate3  AS RACTENGYLEADEXPRAT3, ");
        sb.append("       bld.atvPwrDmdMaxTimeImpRateTot     AS ACTDMDMXTIMEIMPTOT, ");
        sb.append("       bld.activepwrdmdmaximportratetotal AS ACTDMDMXIMPTOT, ");
        sb.append("       bld.atvPwrDmdMaxTimeExpRateTot     AS ACTDMDMXTIMEEXPTOT, ");
        sb.append("       bld.activepwrdmdmaxexportratetotal AS ACTDMDMXEXPTOT, ");
        sb.append("       bld.rtvPwrDmdMaxTimeLagImpRateTot  AS RACTDMDMXTIMELAGIMPTOT, ");
        sb.append("       bld.rtvPwrDmdMaxLagImpRateTot      AS RACTDMDMXLAGIMPTOT, ");
        sb.append("       bld.rtvPwrDmdMaxTimeLeadImpRateTot AS RACTDMDMXTIMELEADIMPTOT, ");
        sb.append("       bld.rtvPwrDmdMaxLeadImpRateTot     AS RACTDMDMXLEADIMPTOT, ");
        sb.append("       bld.rtvPwrDmdMaxTimeLagExpRateTot  AS RACTDMDMXTIMELAGEXPTOT, ");
        sb.append("       bld.rtvPwrDmdMaxLagExpRateTot      AS RACTDMDMXLAGEXPTOT, ");
        sb.append("       bld.rtvPwrDmdMaxTimeLeadExpRateTot AS RACTDMDMXTIMELEADEXPTOT, ");
        sb.append("       bld.rtvPwrDmdMaxLeadExpRateTot     AS RACTDMDMXLEADEXPTOT, ");
        sb.append("       bld.activepwrdmdmaxtimeimportrate1 AS ACTDMDMXTIMEIMPRAT1, ");
        sb.append("       bld.activepwrdmdmaximportrate1     AS ACTDMDMXIMPRAT1, ");
        sb.append("       bld.activepwrdmdmaxtimeexportrate1 AS ACTDMDMXTIMEEXPRAT1, ");
        sb.append("       bld.activepwrdmdmaxexportrate1     AS ACTDMDMXEXPRAT1, ");
        sb.append("       bld.rtvPwrDmdMaxTimeLagImpRate1    AS RACTDMDMXTIMELAGIMPRAT1, ");
        sb.append("       bld.rtvPwrDmdMaxLagImpRate1        AS RACTDMDMXLAGIMPRAT1, ");
        sb.append("       bld.rtvPwrDmdMaxTimeLeadImpRate1   AS RACTDMDMXTIMELEADIMPRAT1, ");
        sb.append("       bld.rtvPwrDmdMaxLeadImpRate1       AS RACTDMDMXLEADIMPRAT1, ");
        sb.append("       bld.rtvPwrDmdMaxTimeLagExpRate1    AS RACTDMDMXTIMELAGEXPRAT1, ");
        sb.append("       bld.rtvPwrDmdMaxLagExpRate1        AS RACTDMDMXLAGEXPRAT1, ");
        sb.append("       bld.rtvPwrDmdMaxTimeLeadExpRate1   AS RACTDMDMXTIMELEADEXPRAT1, ");
        sb.append("       bld.rtvPwrDmdMaxLeadExpRate1       AS RACTDMDMXLEADEXPRAT1, ");
        sb.append("       bld.activepwrdmdmaxtimeimportrate2 AS ACTDMDMXTIMEIMPRAT2, ");
        sb.append("       bld.activepwrdmdmaximportrate2     AS ACTDMDMXIMPRAT2, ");
        sb.append("       bld.activepwrdmdmaxtimeexportrate2 AS ACTDMDMXTIMEEXPRAT2, ");
        sb.append("       bld.activepwrdmdmaxexportrate2     AS ACTDMDMXEXPRAT2, ");
        sb.append("       bld.rtvPwrDmdMaxTimeLagImpRate2    AS RACTDMDMXTIMELAGIMPRAT2, ");
        sb.append("       bld.rtvPwrDmdMaxLagImpRate2        AS RACTDMDMXLAGIMPRAT2, ");
        sb.append("       bld.rtvPwrDmdMaxTimeLeadImpRate2   AS RACTDMDMXTIMELEADIMPRAT2, ");
        sb.append("       bld.rtvPwrDmdMaxLeadImpRate2       AS RACTDMDMXLEADIMPRAT2, ");
        sb.append("       bld.rtvPwrDmdMaxTimeLagExpRate2    AS RACTDMDMXTIMELAGEXPRAT2, ");
        sb.append("       bld.rtvPwrDmdMaxLagExpRate2        AS RACTDMDMXLAGEXPRAT2, ");
        sb.append("       bld.rtvPwrDmdMaxTimeLeadExpRate2   AS RACTDMDMXTIMELEADEXPRAT2, ");
        sb.append("       bld.rtvPwrDmdMaxLeadExpRate2       AS RACTDMDMXLEADEXPRAT2, ");
        sb.append("       bld.activepwrdmdmaxtimeimportrate3 AS ACTDMDMXTIMEIMPRAT3, ");
        sb.append("       bld.activepwrdmdmaximportrate3     AS ACTDMDMXIMPRAT3, ");
        sb.append("       bld.activepwrdmdmaxtimeexportrate3 AS ACTDMDMXTIMEEXPRAT3, ");
        sb.append("       bld.activepwrdmdmaxexportrate3     AS ACTDMDMXEXPRAT3, ");
        sb.append("       bld.rtvPwrDmdMaxTimeLagImpRate3    AS RACTDMDMXTIMELAGIMPRAT3, ");
        sb.append("       bld.rtvPwrDmdMaxLagImpRate3        AS RACTDMDMXLAGIMPRAT3, ");
        sb.append("       bld.rtvPwrDmdMaxTimeLeadImpRate3   AS RACTDMDMXTIMELEADIMPRAT3, ");
        sb.append("       bld.rtvPwrDmdMaxLeadImpRate3       AS RACTDMDMXLEADIMPRAT3, ");
        sb.append("       bld.rtvPwrDmdMaxTimeLagExpRate3    AS RACTDMDMXTIMELAGEXPRAT3, ");
        sb.append("       bld.rtvPwrDmdMaxLagExpRate3        AS RACTDMDMXLAGEXPRAT3, ");
        sb.append("       bld.rtvPwrDmdMaxTimeLeadExpRate3   AS RACTDMDMXTIMELEADEXPRAT3, ");
        sb.append("       bld.rtvPwrDmdMaxLeadExpRate3       AS RACTDMDMXLEADEXPRAT3, ");
        sb.append("       bld.cummAtvPwrDmdMaxImpRateTot     AS CUMACTDMDMXIMPTOT, ");
        sb.append("       bld.cummAtvPwrDmdMaxExpRateTot     AS CUMACTDMDMXEXPTOT, ");
        sb.append("       bld.cummRtvPwrDmdMaxLagImpRateTot  AS CUMRACTDMDMXLAGIMPTOT, ");
        sb.append("       bld.cummRtvPwrDmdMaxLeadImpRateTot AS CUMRACTDMDMXLEADIMPTOT, ");
        sb.append("       bld.cummRtvPwrDmdMaxLagExpRateTot  AS CUMRACTDMDMXLAGEXPTOT, ");
        sb.append("       bld.cummRtvPwrDmdMaxLeadExpRateTot AS CUMRACTDMDMXLEADEXPTOT, ");
        sb.append("       bld.cummactivepwrdmdmaximportrate1 AS CUMACTDMDMXIMPRAT1, ");
        sb.append("       bld.cummactivepwrdmdmaxexportrate1 AS CUMACTDMDMXEXPRAT1, ");
        sb.append("       bld.cummRtvPwrDmdMaxLagImpRate1    AS CUMRACTDMDMXLAGIMPRAT1, ");
        sb.append("       bld.cummRtvPwrDmdMaxLeadImpRate1   AS CUMRACTDMDMXLEADIMPRAT1, ");
        sb.append("       bld.cummRtvPwrDmdMaxLagExpRate1    AS CUMRACTDMDMXLAGEXPRAT1, ");
        sb.append("       bld.cummRtvPwrDmdMaxLeadExpRate1   AS CUMRACTDMDMXLEADEXPRAT1, ");
        sb.append("       bld.cummactivepwrdmdmaximportrate2 AS CUMACTDMDMXIMPRAT2, ");
        sb.append("       bld.cummactivepwrdmdmaxexportrate2 AS CUMACTDMDMXEXPRAT2, ");
        sb.append("       bld.cummRtvPwrDmdMaxLagImpRate2    AS CUMRACTDMDMXLAGIMPRAT2, ");
        sb.append("       bld.cummRtvPwrDmdMaxLeadImpRate2   AS CUMRACTDMDMXLEADIMPRAT2, ");
        sb.append("       bld.cummRtvPwrDmdMaxLagExpRate2    AS CUMRACTDMDMXLAGEXPRAT2, ");
        sb.append("       bld.cummRtvPwrDmdMaxLeadExpRate2   AS CUMRACTDMDMXLEADEXPRAT2, ");
        sb.append("       bld.cummactivepwrdmdmaximportrate3 AS CUMACTDMDMXIMPRAT3, ");
        sb.append("       bld.cummactivepwrdmdmaxexportrate3 AS CUMACTDMDMXEXPRAT3, ");
        sb.append("       bld.cummRtvPwrDmdMaxLagImpRate3    AS CUMRACTDMDMXLAGIMPRAT3, ");
        sb.append("       bld.cummRtvPwrDmdMaxLeadImpRate3   AS CUMRACTDMDMXLEADIMPRAT3, ");
        sb.append("       bld.cummRtvPwrDmdMaxLagExpRate3    AS CUMRACTDMDMXLAGEXPRAT3, ");
        sb.append("       bld.cummRtvPwrDmdMaxLeadExpRate3   AS CUMRACTDMDMXLEADEXPRAT3, ");
        
        sb.append("       bld.kVah 							 AS KVAH,						 	");
        sb.append("       bld.CummkVah1Rate1				 AS CUMMKVAH1RATE1,			 	");
        sb.append("       bld.CummkVah1Rate2				 AS CUMMKVAH1RATE2,			 	");
        sb.append("       bld.CummkVah1Rate3				 AS CUMMKVAH1RATE3,			 	");
        sb.append("       bld.CummkVah1RateTotal			 AS CUMMKVAH1RATETOTAL,		 	");
        sb.append("       bld.MaxDmdkVah1Rate1				 AS MAXDMDKVAH1RATE1,			 	");
        sb.append("       bld.MaxDmdkVah1Rate2				 AS MAXDMDKVAH1RATE2,			 	");
        sb.append("       bld.MaxDmdkVah1Rate3				 AS MAXDMDKVAH1RATE3,			 	");
        sb.append("       bld.MaxDmdkVah1RateTotal			 AS MAXDMDKVAH1RATETOTAL,		 	");
        sb.append("       bld.MaxDmdkVah1TimeRate1			 AS MAXDMDKVAH1TIMERATE1,		 	");
        sb.append("       bld.MaxDmdkVah1TimeRate2			 AS MAXDMDKVAH1TIMERATE2,		 	");
        sb.append("       bld.MaxDmdkVah1TimeRate3			 AS MAXDMDKVAH1TIMERATE3,		 	");
        sb.append("       bld.MaxDmdkVah1TimeRateTotal		 AS MAXDMDKVAH1TIMERATETOTAL	 	");
        
//        if (!"".equals(lastData)) {
//            sb.append(", ");
//            sb.append("       bll.activeenergyimportratetotal              AS LSTACTENGYIMPTOT, ");
//            sb.append("       bll.activeenergyexportratetotal              AS LSTACTENGYEXPTOT, ");
//            sb.append("       bll.reactiveenergylagimportratetotal         AS LSTRACTENGYLAGIMPTOT, ");
//            sb.append("       bll.reactiveenergyleadimportratetotal        AS LSTRACTENGYLEADIMPTOT, ");
//            sb.append("       bll.reactiveenergylagexportratetotal         AS LSTRACTENGYLAGEXPTOT, ");
//            sb.append("       bll.reactiveenergyleadexportratetotal        AS LSTRACTENGYLEADEXPTOT, ");
//            sb.append("       bll.activeenergyimportrate1                  AS LSTACTENGYIMPRAT1, ");
//            sb.append("       bll.activeenergyexportrate1                  AS LSTACTENGYEXPRAT1, ");
//            sb.append("       bll.reactiveenergylagimportrate1             AS LSTRACTENGYLAGIMPRAT1, ");
//            sb.append("       bll.reactiveenergyleadimportrate1            AS LSTRACTENGYLEADIMPRAT1, ");
//            sb.append("       bll.reactiveenergylagexportrate1             AS LSTRACTENGYLAGEXPRAT1, ");
//            sb.append("       bll.reactiveenergyleadexportrate1            AS LSTRACTENGYLEADEXPRAT1, ");
//            sb.append("       bll.activeenergyimportrate2                  AS LSTACTENGYIMPRAT2, ");
//            sb.append("       bll.activeenergyexportrate2                  AS LSTACTENGYEXPRAT2, ");
//            sb.append("       bll.reactiveenergylagimportrate2             AS LSTRACTENGYLAGIMPRAT2, ");
//            sb.append("       bll.reactiveenergyleadimportrate2            AS LSTRACTENGYLEADIMPRAT2, ");
//            sb.append("       bll.reactiveenergylagexportrate2             AS LSTRACTENGYLAGEXPRAT2, ");
//            sb.append("       bll.reactiveenergyleadexportrate2            AS LSTRACTENGYLEADEXPRAT2, ");
//            sb.append("       bll.activeenergyimportrate3                  AS LSTACTENGYIMPRAT3, ");
//            sb.append("       bll.activeenergyexportrate3                  AS LSTACTENGYEXPRAT3, ");
//            sb.append("       bll.reactiveenergylagimportrate3             AS LSTRACTENGYLAGIMPRAT3, ");
//            sb.append("       bll.reactiveenergyleadimportrate3            AS LSTRACTENGYLEADIMPRAT3, ");
//            sb.append("       bll.reactiveenergylagexportrate3             AS LSTRACTENGYLAGEXPRAT3, ");
//            sb.append("       bll.reactiveenergyleadexportrate3            AS LSTRACTENGYLEADEXPRAT3, ");
//            sb.append("       bll.activepwrdmdmaxtimeimportratetotal       AS LSTACTDMDMXTIMEIMPTOT, ");
//            sb.append("       bll.activepwrdmdmaximportratetotal           AS LSTACTDMDMXIMPTOT, ");
//            sb.append("       bll.activepwrdmdmaxtimeexportratetotal       AS LSTACTDMDMXTIMEEXPTOT, ");
//            sb.append("       bll.activepwrdmdmaxexportratetotal           AS LSTACTDMDMXEXPTOT, ");
//            sb.append("       bll.reactivepwrdmdmaxtimelagimportratetotal  AS LSTRACTDMDMXTIMELAGIMPTOT, ");
//            sb.append("       bll.reactivepwrdmdmaxlagimportratetotal      AS LSTRACTDMDMXLAGIMPTOT, ");
//            sb.append("       bll.reactivepwrdmdmaxtimeleadimportratetotal AS LSTRACTDMDMXTIMELEADIMPTOT, ");
//            sb.append("       bll.reactivepwrdmdmaxleadimportratetotal     AS LSTRACTDMDMXLEADIMPTOT, ");
//            sb.append("       bll.reactivepwrdmdmaxtimelagexportratetotal  AS LSTRACTDMDMXTIMELAGEXPTOT, ");
//            sb.append("       bll.reactivepwrdmdmaxlagexportratetotal      AS LSTRACTDMDMXLAGEXPTOT, ");
//            sb.append("       bll.reactivepwrdmdmaxtimeleadexportratetotal AS LSTRACTDMDMXTIMELEADEXPTOT, ");
//            sb.append("       bll.reactivepwrdmdmaxleadexportratetotal     AS LSTRACTDMDMXLEADEXPTOT, ");
//            sb.append("       bll.activepwrdmdmaxtimeimportrate1           AS LSTACTDMDMXTIMEIMPRAT1, ");
//            sb.append("       bll.activepwrdmdmaximportrate1               AS LSTACTDMDMXIMPRAT1, ");
//            sb.append("       bll.activepwrdmdmaxtimeexportrate1           AS LSTACTDMDMXTIMEEXPRAT1, ");
//            sb.append("       bll.activepwrdmdmaxexportrate1               AS LSTACTDMDMXEXPRAT1, ");
//            sb.append("       bll.reactivepwrdmdmaxtimelagimportrate1      AS LSTRACTDMDMXTIMELAGIMPRAT1, ");
//            sb.append("       bll.reactivepwrdmdmaxlagimportrate1          AS LSTRACTDMDMXLAGIMPRAT1, ");
//            sb.append("       bll.reactivepwrdmdmaxtimeleadimportrate1     AS LSTRACTDMDMXTIMELEADIMPRAT1, ");
//            sb.append("       bll.reactivepwrdmdmaxleadimportrate1         AS LSTRACTDMDMXLEADIMPRAT1, ");
//            sb.append("       bll.reactivepwrdmdmaxtimelagexportrate1      AS LSTRACTDMDMXTIMELAGEXPRAT1, ");
//            sb.append("       bll.reactivepwrdmdmaxlagexportrate1          AS LSTRACTDMDMXLAGEXPRAT1, ");
//            sb.append("       bll.reactivepwrdmdmaxtimeleadexportrate1     AS LSTRACTDMDMXTIMELEADEXPRAT1, ");
//            sb.append("       bll.reactivepwrdmdmaxleadexportrate1         AS LSTRACTDMDMXLEADEXPRAT1, ");
//            sb.append("       bll.activepwrdmdmaxtimeimportrate2           AS LSTACTDMDMXTIMEIMPRAT2, ");
//            sb.append("       bll.activepwrdmdmaximportrate2               AS LSTACTDMDMXIMPRAT2, ");
//            sb.append("       bll.activepwrdmdmaxtimeexportrate2           AS LSTACTDMDMXTIMEEXPRAT2, ");
//            sb.append("       bll.activepwrdmdmaxexportrate2               AS LSTACTDMDMXEXPRAT2, ");
//            sb.append("       bll.reactivepwrdmdmaxtimelagimportrate2      AS LSTRACTDMDMXTIMELAGIMPRAT2, ");
//            sb.append("       bll.reactivepwrdmdmaxlagimportrate2          AS LSTRACTDMDMXLAGIMPRAT2, ");
//            sb.append("       bll.reactivepwrdmdmaxtimeleadimportrate2     AS LSTRACTDMDMXTIMELEADIMPRAT2, ");
//            sb.append("       bll.reactivepwrdmdmaxleadimportrate2         AS LSTRACTDMDMXLEADIMPRAT2, ");
//            sb.append("       bll.reactivepwrdmdmaxtimelagexportrate2      AS LSTRACTDMDMXTIMELAGEXPRAT2, ");
//            sb.append("       bll.reactivepwrdmdmaxlagexportrate2          AS LSTRACTDMDMXLAGEXPRAT2, ");
//            sb.append("       bll.reactivepwrdmdmaxtimeleadexportrate2     AS LSTRACTDMDMXTIMELEADEXPRAT2, ");
//            sb.append("       bll.reactivepwrdmdmaxleadexportrate2         AS LSTRACTDMDMXLEADEXPRAT2, ");
//            sb.append("       bll.activepwrdmdmaxtimeimportrate3           AS LSTACTDMDMXTIMEIMPRAT3, ");
//            sb.append("       bll.activepwrdmdmaximportrate3               AS LSTACTDMDMXIMPRAT3, ");
//            sb.append("       bll.activepwrdmdmaxtimeexportrate3           AS LSTACTDMDMXTIMEEXPRAT3, ");
//            sb.append("       bll.activepwrdmdmaxexportrate3               AS LSTACTDMDMXEXPRAT3, ");
//            sb.append("       bll.reactivepwrdmdmaxtimelagimportrate3      AS LSTRACTDMDMXTIMELAGIMPRAT3, ");
//            sb.append("       bll.reactivepwrdmdmaxlagimportrate3          AS LSTRACTDMDMXLAGIMPRAT3, ");
//            sb.append("       bll.reactivepwrdmdmaxtimeleadimportrate3     AS LSTRACTDMDMXTIMELEADIMPRAT3, ");
//            sb.append("       bll.reactivepwrdmdmaxleadimportrate3         AS LSTRACTDMDMXLEADIMPRAT3, ");
//            sb.append("       bll.reactivepwrdmdmaxtimelagexportrate3      AS LSTRACTDMDMXTIMELAGEXPRAT3, ");
//            sb.append("       bll.reactivepwrdmdmaxlagexportrate3          AS LSTRACTDMDMXLAGEXPRAT3, ");
//            sb.append("       bll.reactivepwrdmdmaxtimeleadexportrate3     AS LSTRACTDMDMXTIMELEADEXPRAT3, ");
//            sb.append("       bll.reactivepwrdmdmaxleadexportrate3         AS LSTRACTDMDMXLEADEXPRAT3, ");
//            sb.append("       bll.cummactivepwrdmdmaximportratetotal       AS LSTCUMACTDMDMXIMPTOT, ");
//            sb.append("       bll.cummactivepwrdmdmaxexportratetotal       AS LSTCUMACTDMDMXEXPTOT, ");
//            sb.append("       bll.cummreactivepwrdmdmaxlagimportratetotal  AS LSTCUMRACTDMDMXLAGIMPTOT, ");
//            sb.append("       bll.cummreactivepwrdmdmaxleadimportratetotal AS LSTCUMRACTDMDMXLEADIMPTOT, ");
//            sb.append("       bll.cummreactivepwrdmdmaxlagexportratetotal  AS LSTCUMRACTDMDMXLAGEXPTOT, ");
//            sb.append("       bll.cummreactivepwrdmdmaxleadexportratetotal AS LSTCUMRACTDMDMXLEADEXPTOT, ");
//            sb.append("       bll.cummactivepwrdmdmaximportrate1           AS LSTCUMACTDMDMXIMPRAT1, ");
//            sb.append("       bll.cummactivepwrdmdmaxexportrate1           AS LSTCUMACTDMDMXEXPRAT1, ");
//            sb.append("       bll.cummreactivepwrdmdmaxlagimportrate1      AS LSTCUMRACTDMDMXLAGIMPRAT1, ");
//            sb.append("       bll.cummreactivepwrdmdmaxleadimportrate1     AS LSTCUMRACTDMDMXLEADIMPRAT1, ");
//            sb.append("       bll.cummreactivepwrdmdmaxlagexportrate1      AS LSTCUMRACTDMDMXLAGEXPRAT1, ");
//            sb.append("       bll.cummreactivepwrdmdmaxleadexportrate1     AS LSTCUMRACTDMDMXLEADEXPRAT1, ");
//            sb.append("       bll.cummactivepwrdmdmaximportrate2           AS LSTCUMACTDMDMXIMPRAT2, ");
//            sb.append("       bll.cummactivepwrdmdmaxexportrate2           AS LSTCUMACTDMDMXEXPRAT2, ");
//            sb.append("       bll.cummreactivepwrdmdmaxlagimportrate2      AS LSTCUMRACTDMDMXLAGIMPRAT2, ");
//            sb.append("       bll.cummreactivepwrdmdmaxleadimportrate2     AS LSTCUMRACTDMDMXLEADIMPRAT2, ");
//            sb.append("       bll.cummreactivepwrdmdmaxlagexportrate2      AS LSTCUMRACTDMDMXLAGEXPRAT2, ");
//            sb.append("       bll.cummreactivepwrdmdmaxleadexportrate2     AS LSTCUMRACTDMDMXLEADEXPRAT2, ");
//            sb.append("       bll.cummactivepwrdmdmaximportrate3           AS LSTCUMACTDMDMXIMPRAT3, ");
//            sb.append("       bll.cummactivepwrdmdmaxexportrate3           AS LSTCUMACTDMDMXEXPRAT3, ");
//            sb.append("       bll.cummreactivepwrdmdmaxlagimportrate3      AS LSTCUMRACTDMDMXLAGIMPRAT3, ");
//            sb.append("       bll.cummreactivepwrdmdmaxleadimportrate3     AS LSTCUMRACTDMDMXLEADIMPRAT3, ");
//            sb.append("       bll.cummreactivepwrdmdmaxlagexportrate3      AS LSTCUMRACTDMDMXLAGEXPRAT3, ");
//            sb.append("       bll.cummreactivepwrdmdmaxleadexportrate3     AS LSTCUMRACTDMDMXLEADEXPRAT3 ");
//        }

        sb.append("FROM billing_month_em bld ");

//        if (!"".equals(lastData)) {
//            sb.append("     LEFT OUTER JOIN billing_month_em bll ");
//            sb.append("     ON  bld.mdev_id = bll.mdev_id ");
////            sb.append("     AND bll.yyyymmdd = :lastStartDate ");
//            sb.append("     AND bll.yyyymmdd BETWEEN :lastStartDate AND :lastEndDate ");
//            sb.append("     AND substr(bll.yyyymmdd, 7, 2) = substr(bld.yyyymmdd, 7, 2) ");
//        }
        sb.append("     LEFT OUTER JOIN contract con ");
        sb.append("     ON bld.contract_id = con.id ");
        sb.append("     LEFT OUTER JOIN customer cus ");
        sb.append("     ON con.customer_id = cus.id ");
        sb.append("     LEFT OUTER JOIN tarifftype taf ");
        sb.append("     ON con.tariffindex_id = taf.id ");
        sb.append("     LEFT OUTER JOIN location loc ");
        sb.append("     ON con.location_id = loc.id ");
        sb.append("     LEFT OUTER JOIN meter mtr ");
        sb.append("     ON bld.meter_id = mtr.id ");
        sb.append("WHERE bld.yyyymmdd BETWEEN :curStartDate AND :curEndDate ");

        if (!mdevId.isEmpty()) {
            sb.append("AND   bld.mdev_type = :mdevType ");
            sb.append("AND   bld.mdev_id = :mdevId ");
            
            if (detailContractId != null) {
                sb.append("AND   bld.contract_id = :detailContractId ");
            } else {
                sb.append("AND   bld.contract_id IS NULL ");
            }
            if (detailMeterId != null) {
                sb.append("AND   bld.meter_id = :detailMeterId ");
            } else {
                sb.append("AND   bld.meter_id IS NULL ");
            }
        }

        if (!"".equals(locationCondition)) {
            sb.append("AND   loc.id IN (").append(locationCondition).append(") ");
        }

        if (!"".equals(tariffIndex) && !"0".equals(tariffIndex)) {
            sb.append("AND   taf.id = :tariffIndex ");
        }

        if (!"".equals(customerName)) {
            sb.append("AND   UPPER(cus.name) LIKE UPPER(:customerName) ");
        }

        if (!"".equals(contractNo)) {
            sb.append("AND   con.contract_number LIKE :contractNo ");
        }

        if (!"".equals(meterId)) {
            sb.append("AND   mtr.mds_id LIKE :meterId ");
        }

        sb.append("ORDER BY bld.yyyymmdd DESC, bld.hhmmss DESC, cus.name ");
        
        SQLQuery query = getSession().createSQLQuery(new SQLWrapper().getQuery(sb.toString()));
        query.setString("curStartDate", startDate);
        query.setString("curEndDate", endDate);

        if (!mdevId.isEmpty()) {
            query.setInteger("mdevType", mdevType);
            query.setString("mdevId", mdevId);
            
            if (detailContractId != null) {
                query.setInteger("detailContractId", detailContractId);
            }
            if (detailMeterId != null) {
                query.setInteger("detailMeterId", detailMeterId);
            }
        }

        if (!"".equals(tariffIndex) && !"0".equals(tariffIndex)) {
            query.setString("tariffIndex", tariffIndex);
        }

        if (!"".equals(customerName)) {
            query.setString("customerName", "%" + customerName + "%");
        }

        if (!"".equals(contractNo)) {
            query.setString("contractNo", "%" + contractNo + "%");
        }

        if (!"".equals(meterId)) {
            query.setString("meterId", "%" + meterId + "%");
        }

        return query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
    }

    /**
     * method name : getBillingDataReportMonthlyWithLastMonth<b/>
     * method Desc : Billing Data 월별 TOU 리포트 데이터를 조회한다. (전월 데이터 조회 선택 시)<b/>
     *               전월 데이터와 같이 보여줄 경우 일자가 매칭이 되지 않으므로 월별로 합산해서 보여준다.
     *
     * @param conditionMap
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> getBillingDataReportMonthlyWithLastMonth(Map<String, Object> conditionMap) {

        String startDate         = StringUtil.nullToBlank(conditionMap.get("searchStartDate"));
        String endDate           = StringUtil.nullToBlank(conditionMap.get("searchEndDate"));
        String lastStartDate     = StringUtil.nullToBlank(conditionMap.get("lastStartDate"));
        String lastEndDate       = StringUtil.nullToBlank(conditionMap.get("lastEndDate"));
        String locationCondition = StringUtil.nullToBlank(conditionMap.get("locationCondition"));
        String tariffIndex       = StringUtil.nullToBlank(conditionMap.get("tariffIndexId"));
        String customerName      = StringUtil.nullToBlank(conditionMap.get("customerName"));
        String contractNo        = StringUtil.nullToBlank(conditionMap.get("contractNo"));
        String meterId           = StringUtil.nullToBlank(conditionMap.get("meterId"));
        String mdevId            = StringUtil.nullToBlank(conditionMap.get("mdevId"));
        
        Integer mdevType         = null;
        if(conditionMap.get("mdevType") 		!= null)	mdevType = Integer.parseInt( conditionMap.get("mdevType").toString() );
        
        Integer detailContractId = null;
	    if(conditionMap.get("detailContractId") != null)	detailContractId = Integer.parseInt( conditionMap.get("detailContractId").toString() );
        
	    Integer detailMeterId    = null;
	    if(conditionMap.get("detailMeterId") 	!= null)	detailMeterId = Integer.parseInt( conditionMap.get("detailMeterId").toString() );

	    StringBuilder sb = new StringBuilder();

        sb.append("\nSELECT bld.yyyymmdd AS YYYYMMDD, ");
        sb.append("\n       cus.name AS CUSTOMERNAME, ");
        sb.append("\n       con.contract_number AS CONTRACTNUMBER, ");
        sb.append("\n       mtr.mds_id AS METERID, ");
        sb.append("\n       con.contractdemand AS CONTRACTDEMAND, ");
        sb.append("\n       taf.name AS TARIFFTYPENAME, ");
        sb.append("\n       loc.name AS LOCATIONNAME, ");
        sb.append("\n       bld.actengyimptot               AS ACTENGYIMPTOT, ");
        sb.append("\n       bld.actengyexptot               AS ACTENGYEXPTOT, ");
        sb.append("\n       bld.ractengylagimptot           AS RACTENGYLAGIMPTOT, ");
        sb.append("\n       bld.ractengyleadimptot          AS RACTENGYLEADIMPTOT, ");
        sb.append("\n       bld.ractengylagexptot           AS RACTENGYLAGEXPTOT, ");
        sb.append("\n       bld.ractengyleadexptot          AS RACTENGYLEADEXPTOT, ");
        sb.append("\n       bld.actengyimprat1              AS ACTENGYIMPRAT1, ");
        sb.append("\n       bld.actengyexprat1              AS ACTENGYEXPRAT1, ");
        sb.append("\n       bld.ractengylagimprat1          AS RACTENGYLAGIMPRAT1, ");
        sb.append("\n       bld.ractengyleadimprat1         AS RACTENGYLEADIMPRAT1, ");
        sb.append("\n       bld.ractengylagexprat1          AS RACTENGYLAGEXPRAT1, ");
        sb.append("\n       bld.ractengyleadexprat1         AS RACTENGYLEADEXPRAT1, ");
        sb.append("\n       bld.actengyimprat2              AS ACTENGYIMPRAT2, ");
        sb.append("\n       bld.actengyexprat2              AS ACTENGYEXPRAT2, ");
        sb.append("\n       bld.ractengylagimprat2          AS RACTENGYLAGIMPRAT2, ");
        sb.append("\n       bld.ractengyleadimprat2         AS RACTENGYLEADIMPRAT2, ");
        sb.append("\n       bld.ractengylagexprat2          AS RACTENGYLAGEXPRAT2, ");
        sb.append("\n       bld.ractengyleadexprat2         AS RACTENGYLEADEXPRAT2, ");
        sb.append("\n       bld.actengyimprat3              AS ACTENGYIMPRAT3, ");
        sb.append("\n       bld.actengyexprat3              AS ACTENGYEXPRAT3, ");
        sb.append("\n       bld.ractengylagimprat3          AS RACTENGYLAGIMPRAT3, ");
        sb.append("\n       bld.ractengyleadimprat3         AS RACTENGYLEADIMPRAT3, ");
        sb.append("\n       bld.ractengylagexprat3          AS RACTENGYLAGEXPRAT3, ");
        sb.append("\n       bld.ractengyleadexprat3         AS RACTENGYLEADEXPRAT3, ");
        sb.append("\n       bld.actdmdmxtimeimptot          AS ACTDMDMXTIMEIMPTOT, ");
        sb.append("\n       bld.actdmdmximptot              AS ACTDMDMXIMPTOT, ");
        sb.append("\n       bld.actdmdmxtimeexptot          AS ACTDMDMXTIMEEXPTOT, ");
        sb.append("\n       bld.actdmdmxexptot              AS ACTDMDMXEXPTOT, ");
        sb.append("\n       bld.ractdmdmxtimelagimptot      AS RACTDMDMXTIMELAGIMPTOT, ");
        sb.append("\n       bld.ractdmdmxlagimptot          AS RACTDMDMXLAGIMPTOT, ");
        sb.append("\n       bld.ractdmdmxtimeleadimptot     AS RACTDMDMXTIMELEADIMPTOT, ");
        sb.append("\n       bld.ractdmdmxleadimptot         AS RACTDMDMXLEADIMPTOT, ");
        sb.append("\n       bld.ractdmdmxtimelagexptot      AS RACTDMDMXTIMELAGEXPTOT, ");
        sb.append("\n       bld.ractdmdmxlagexptot          AS RACTDMDMXLAGEXPTOT, ");
        sb.append("\n       bld.ractdmdmxtimeleadexptot     AS RACTDMDMXTIMELEADEXPTOT, ");
        sb.append("\n       bld.ractdmdmxleadexptot         AS RACTDMDMXLEADEXPTOT, ");
        sb.append("\n       bld.actdmdmxtimeimprat1         AS ACTDMDMXTIMEIMPRAT1, ");
        sb.append("\n       bld.actdmdmximprat1             AS ACTDMDMXIMPRAT1, ");
        sb.append("\n       bld.actdmdmxtimeexprat1         AS ACTDMDMXTIMEEXPRAT1, ");
        sb.append("\n       bld.actdmdmxexprat1             AS ACTDMDMXEXPRAT1, ");
        sb.append("\n       bld.ractdmdmxtimelagimprat1     AS RACTDMDMXTIMELAGIMPRAT1, ");
        sb.append("\n       bld.ractdmdmxlagimprat1         AS RACTDMDMXLAGIMPRAT1, ");
        sb.append("\n       bld.ractdmdmxtimeleadimprat1    AS RACTDMDMXTIMELEADIMPRAT1, ");
        sb.append("\n       bld.ractdmdmxleadimprat1        AS RACTDMDMXLEADIMPRAT1, ");
        sb.append("\n       bld.ractdmdmxtimelagexprat1     AS RACTDMDMXTIMELAGEXPRAT1, ");
        sb.append("\n       bld.ractdmdmxlagexprat1         AS RACTDMDMXLAGEXPRAT1, ");
        sb.append("\n       bld.ractdmdmxtimeleadexprat1    AS RACTDMDMXTIMELEADEXPRAT1, ");
        sb.append("\n       bld.ractdmdmxleadexprat1        AS RACTDMDMXLEADEXPRAT1, ");
        sb.append("\n       bld.actdmdmxtimeimprat2         AS ACTDMDMXTIMEIMPRAT2, ");
        sb.append("\n       bld.actdmdmximprat2             AS ACTDMDMXIMPRAT2, ");
        sb.append("\n       bld.actdmdmxtimeexprat2         AS ACTDMDMXTIMEEXPRAT2, ");
        sb.append("\n       bld.actdmdmxexprat2             AS ACTDMDMXEXPRAT2, ");
        sb.append("\n       bld.ractdmdmxtimelagimprat2     AS RACTDMDMXTIMELAGIMPRAT2, ");
        sb.append("\n       bld.ractdmdmxlagimprat2         AS RACTDMDMXLAGIMPRAT2, ");
        sb.append("\n       bld.ractdmdmxtimeleadimprat2    AS RACTDMDMXTIMELEADIMPRAT2, ");
        sb.append("\n       bld.ractdmdmxleadimprat2        AS RACTDMDMXLEADIMPRAT2, ");
        sb.append("\n       bld.ractdmdmxtimelagexprat2     AS RACTDMDMXTIMELAGEXPRAT2, ");
        sb.append("\n       bld.ractdmdmxlagexprat2         AS RACTDMDMXLAGEXPRAT2, ");
        sb.append("\n       bld.ractdmdmxtimeleadexprat2    AS RACTDMDMXTIMELEADEXPRAT2, ");
        sb.append("\n       bld.ractdmdmxleadexprat2        AS RACTDMDMXLEADEXPRAT2, ");
        sb.append("\n       bld.actdmdmxtimeimprat3         AS ACTDMDMXTIMEIMPRAT3, ");
        sb.append("\n       bld.actdmdmximprat3             AS ACTDMDMXIMPRAT3, ");
        sb.append("\n       bld.actdmdmxtimeexprat3         AS ACTDMDMXTIMEEXPRAT3, ");
        sb.append("\n       bld.actdmdmxexprat3             AS ACTDMDMXEXPRAT3, ");
        sb.append("\n       bld.ractdmdmxtimelagimprat3     AS RACTDMDMXTIMELAGIMPRAT3, ");
        sb.append("\n       bld.ractdmdmxlagimprat3         AS RACTDMDMXLAGIMPRAT3, ");
        sb.append("\n       bld.ractdmdmxtimeleadimprat3    AS RACTDMDMXTIMELEADIMPRAT3, ");
        sb.append("\n       bld.ractdmdmxleadimprat3        AS RACTDMDMXLEADIMPRAT3, ");
        sb.append("\n       bld.ractdmdmxtimelagexprat3     AS RACTDMDMXTIMELAGEXPRAT3, ");
        sb.append("\n       bld.ractdmdmxlagexprat3         AS RACTDMDMXLAGEXPRAT3, ");
        sb.append("\n       bld.ractdmdmxtimeleadexprat3    AS RACTDMDMXTIMELEADEXPRAT3, ");
        sb.append("\n       bld.ractdmdmxleadexprat3        AS RACTDMDMXLEADEXPRAT3, ");
        sb.append("\n       bld.cumactdmdmximptot           AS CUMACTDMDMXIMPTOT, ");
        sb.append("\n       bld.cumactdmdmxexptot           AS CUMACTDMDMXEXPTOT, ");
        sb.append("\n       bld.cumractdmdmxlagimptot       AS CUMRACTDMDMXLAGIMPTOT, ");
        sb.append("\n       bld.cumractdmdmxleadimptot      AS CUMRACTDMDMXLEADIMPTOT, ");
        sb.append("\n       bld.cumractdmdmxlagexptot       AS CUMRACTDMDMXLAGEXPTOT, ");
        sb.append("\n       bld.cumractdmdmxleadexptot      AS CUMRACTDMDMXLEADEXPTOT, ");
        sb.append("\n       bld.cumactdmdmximprat1          AS CUMACTDMDMXIMPRAT1, ");
        sb.append("\n       bld.cumactdmdmxexprat1          AS CUMACTDMDMXEXPRAT1, ");
        sb.append("\n       bld.cumractdmdmxlagimprat1      AS CUMRACTDMDMXLAGIMPRAT1, ");
        sb.append("\n       bld.cumractdmdmxleadimprat1     AS CUMRACTDMDMXLEADIMPRAT1, ");
        sb.append("\n       bld.cumractdmdmxlagexprat1      AS CUMRACTDMDMXLAGEXPRAT1, ");
        sb.append("\n       bld.cumractdmdmxleadexprat1     AS CUMRACTDMDMXLEADEXPRAT1, ");
        sb.append("\n       bld.cumactdmdmximprat2          AS CUMACTDMDMXIMPRAT2, ");
        sb.append("\n       bld.cumactdmdmxexprat2          AS CUMACTDMDMXEXPRAT2, ");
        sb.append("\n       bld.cumractdmdmxlagimprat2      AS CUMRACTDMDMXLAGIMPRAT2, ");
        sb.append("\n       bld.cumractdmdmxleadimprat2     AS CUMRACTDMDMXLEADIMPRAT2, ");
        sb.append("\n       bld.cumractdmdmxlagexprat2      AS CUMRACTDMDMXLAGEXPRAT2, ");
        sb.append("\n       bld.cumractdmdmxleadexprat2     AS CUMRACTDMDMXLEADEXPRAT2, ");
        sb.append("\n       bld.cumactdmdmximprat3          AS CUMACTDMDMXIMPRAT3, ");
        sb.append("\n       bld.cumactdmdmxexprat3          AS CUMACTDMDMXEXPRAT3, ");
        sb.append("\n       bld.cumractdmdmxlagimprat3      AS CUMRACTDMDMXLAGIMPRAT3, ");
        sb.append("\n       bld.cumractdmdmxleadimprat3     AS CUMRACTDMDMXLEADIMPRAT3, ");
        sb.append("\n       bld.cumractdmdmxlagexprat3      AS CUMRACTDMDMXLAGEXPRAT3, ");
        sb.append("\n       bld.cumractdmdmxleadexprat3     AS CUMRACTDMDMXLEADEXPRAT3, ");
        sb.append("\n       bld.kVah                        AS KVAH, ");
        sb.append("\n       bld.CummkVah1Rate1              AS CUMMKVAH1RATE1, ");
        sb.append("\n       bld.CummkVah1Rate2              AS CUMMKVAH1RATE2, ");
        sb.append("\n       bld.CummkVah1Rate3              AS CUMMKVAH1RATE3, ");
        sb.append("\n       bld.CummkVah1RateTotal          AS CUMMKVAH1RATETOTAL, ");
        sb.append("\n       bld.MaxDmdkVah1Rate1            AS MAXDMDKVAH1RATE1, ");
        sb.append("\n       bld.MaxDmdkVah1Rate2            AS MAXDMDKVAH1RATE2, ");
        sb.append("\n       bld.MaxDmdkVah1Rate3            AS MAXDMDKVAH1RATE3, ");
        sb.append("\n       bld.MaxDmdkVah1RateTotal        AS MAXDMDKVAH1RATETOTAL, ");
        sb.append("\n       bld.MaxDmdkVah1TimeRate1        AS MAXDMDKVAH1TIMERATE1, ");
        sb.append("\n       bld.MaxDmdkVah1TimeRate2        AS MAXDMDKVAH1TIMERATE2, ");
        sb.append("\n       bld.MaxDmdkVah1TimeRate3        AS MAXDMDKVAH1TIMERATE3, ");
        sb.append("\n       bld.MaxDmdkVah1TimeRateTotal    AS MAXDMDKVAH1TIMERATETOTAL, ");
        sb.append("\n       bll.lstactengyimptot            AS LSTACTENGYIMPTOT, ");
        sb.append("\n       bll.lstactengyexptot            AS LSTACTENGYEXPTOT, ");
        sb.append("\n       bll.lstractengylagimptot        AS LSTRACTENGYLAGIMPTOT, ");
        sb.append("\n       bll.lstractengyleadimptot       AS LSTRACTENGYLEADIMPTOT, ");
        sb.append("\n       bll.lstractengylagexptot        AS LSTRACTENGYLAGEXPTOT, ");
        sb.append("\n       bll.lstractengyleadexptot       AS LSTRACTENGYLEADEXPTOT, ");
        sb.append("\n       bll.lstactengyimprat1           AS LSTACTENGYIMPRAT1, ");
        sb.append("\n       bll.lstactengyexprat1           AS LSTACTENGYEXPRAT1, ");
        sb.append("\n       bll.lstractengylagimprat1       AS LSTRACTENGYLAGIMPRAT1, ");
        sb.append("\n       bll.lstractengyleadimprat1      AS LSTRACTENGYLEADIMPRAT1, ");
        sb.append("\n       bll.lstractengylagexprat1       AS LSTRACTENGYLAGEXPRAT1, ");
        sb.append("\n       bll.lstractengyleadexprat1      AS LSTRACTENGYLEADEXPRAT1, ");
        sb.append("\n       bll.lstactengyimprat2           AS LSTACTENGYIMPRAT2, ");
        sb.append("\n       bll.lstactengyexprat2           AS LSTACTENGYEXPRAT2, ");
        sb.append("\n       bll.lstractengylagimprat2       AS LSTRACTENGYLAGIMPRAT2, ");
        sb.append("\n       bll.lstractengyleadimprat2      AS LSTRACTENGYLEADIMPRAT2, ");
        sb.append("\n       bll.lstractengylagexprat2       AS LSTRACTENGYLAGEXPRAT2, ");
        sb.append("\n       bll.lstractengyleadexprat2      AS LSTRACTENGYLEADEXPRAT2, ");
        sb.append("\n       bll.lstactengyimprat3           AS LSTACTENGYIMPRAT3, ");
        sb.append("\n       bll.lstactengyexprat3           AS LSTACTENGYEXPRAT3, ");
        sb.append("\n       bll.lstractengylagimprat3       AS LSTRACTENGYLAGIMPRAT3, ");
        sb.append("\n       bll.lstractengyleadimprat3      AS LSTRACTENGYLEADIMPRAT3, ");
        sb.append("\n       bll.lstractengylagexprat3       AS LSTRACTENGYLAGEXPRAT3, ");
        sb.append("\n       bll.lstractengyleadexprat3      AS LSTRACTENGYLEADEXPRAT3, ");
        sb.append("\n       bll.lstactdmdmxtimeimptot       AS LSTACTDMDMXTIMEIMPTOT, ");
        sb.append("\n       bll.lstactdmdmximptot           AS LSTACTDMDMXIMPTOT, ");
        sb.append("\n       bll.lstactdmdmxtimeexptot       AS LSTACTDMDMXTIMEEXPTOT, ");
        sb.append("\n       bll.lstactdmdmxexptot           AS LSTACTDMDMXEXPTOT, ");
        sb.append("\n       bll.lstractdmdmxtimelagimptot   AS LSTRACTDMDMXTIMELAGIMPTOT, ");
        sb.append("\n       bll.lstractdmdmxlagimptot       AS LSTRACTDMDMXLAGIMPTOT, ");
        sb.append("\n       bll.lstractdmdmxtimeleadimptot  AS LSTRACTDMDMXTIMELEADIMPTOT, ");
        sb.append("\n       bll.lstractdmdmxleadimptot      AS LSTRACTDMDMXLEADIMPTOT, ");
        sb.append("\n       bll.lstractdmdmxtimelagexptot   AS LSTRACTDMDMXTIMELAGEXPTOT, ");
        sb.append("\n       bll.lstractdmdmxlagexptot       AS LSTRACTDMDMXLAGEXPTOT, ");
        sb.append("\n       bll.lstractdmdmxtimeleadexptot  AS LSTRACTDMDMXTIMELEADEXPTOT, ");
        sb.append("\n       bll.lstractdmdmxleadexptot      AS LSTRACTDMDMXLEADEXPTOT, ");
        sb.append("\n       bll.lstactdmdmxtimeimprat1      AS LSTACTDMDMXTIMEIMPRAT1, ");
        sb.append("\n       bll.lstactdmdmximprat1          AS LSTACTDMDMXIMPRAT1, ");
        sb.append("\n       bll.lstactdmdmxtimeexprat1      AS LSTACTDMDMXTIMEEXPRAT1, ");
        sb.append("\n       bll.lstactdmdmxexprat1          AS LSTACTDMDMXEXPRAT1, ");
        sb.append("\n       bll.lstractdmdmxtimelagimprat1  AS LSTRACTDMDMXTIMELAGIMPRAT1, ");
        sb.append("\n       bll.lstractdmdmxlagimprat1      AS LSTRACTDMDMXLAGIMPRAT1, ");
        sb.append("\n       bll.lstractdmdmxtimeleadimprat1 AS LSTRACTDMDMXTIMELEADIMPRAT1, ");
        sb.append("\n       bll.lstractdmdmxleadimprat1     AS LSTRACTDMDMXLEADIMPRAT1, ");
        sb.append("\n       bll.lstractdmdmxtimelagexprat1  AS LSTRACTDMDMXTIMELAGEXPRAT1, ");
        sb.append("\n       bll.lstractdmdmxlagexprat1      AS LSTRACTDMDMXLAGEXPRAT1, ");
        sb.append("\n       bll.lstractdmdmxtimeleadexprat1 AS LSTRACTDMDMXTIMELEADEXPRAT1, ");
        sb.append("\n       bll.lstractdmdmxleadexprat1     AS LSTRACTDMDMXLEADEXPRAT1, ");
        sb.append("\n       bll.lstactdmdmxtimeimprat2      AS LSTACTDMDMXTIMEIMPRAT2, ");
        sb.append("\n       bll.lstactdmdmximprat2          AS LSTACTDMDMXIMPRAT2, ");
        sb.append("\n       bll.lstactdmdmxtimeexprat2      AS LSTACTDMDMXTIMEEXPRAT2, ");
        sb.append("\n       bll.lstactdmdmxexprat2          AS LSTACTDMDMXEXPRAT2, ");
        sb.append("\n       bll.lstractdmdmxtimelagimprat2  AS LSTRACTDMDMXTIMELAGIMPRAT2, ");
        sb.append("\n       bll.lstractdmdmxlagimprat2      AS LSTRACTDMDMXLAGIMPRAT2, ");
        sb.append("\n       bll.lstractdmdmxtimeleadimprat2 AS LSTRACTDMDMXTIMELEADIMPRAT2, ");
        sb.append("\n       bll.lstractdmdmxleadimprat2     AS LSTRACTDMDMXLEADIMPRAT2, ");
        sb.append("\n       bll.lstractdmdmxtimelagexprat2  AS LSTRACTDMDMXTIMELAGEXPRAT2, ");
        sb.append("\n       bll.lstractdmdmxlagexprat2      AS LSTRACTDMDMXLAGEXPRAT2, ");
        sb.append("\n       bll.lstractdmdmxtimeleadexprat2 AS LSTRACTDMDMXTIMELEADEXPRAT2, ");
        sb.append("\n       bll.lstractdmdmxleadexprat2     AS LSTRACTDMDMXLEADEXPRAT2, ");
        sb.append("\n       bll.lstactdmdmxtimeimprat3      AS LSTACTDMDMXTIMEIMPRAT3, ");
        sb.append("\n       bll.lstactdmdmximprat3          AS LSTACTDMDMXIMPRAT3, ");
        sb.append("\n       bll.lstactdmdmxtimeexprat3      AS LSTACTDMDMXTIMEEXPRAT3, ");
        sb.append("\n       bll.lstactdmdmxexprat3          AS LSTACTDMDMXEXPRAT3, ");
        sb.append("\n       bll.lstractdmdmxtimelagimprat3  AS LSTRACTDMDMXTIMELAGIMPRAT3, ");
        sb.append("\n       bll.lstractdmdmxlagimprat3      AS LSTRACTDMDMXLAGIMPRAT3, ");
        sb.append("\n       bll.lstractdmdmxtimeleadimprat3 AS LSTRACTDMDMXTIMELEADIMPRAT3, ");
        sb.append("\n       bll.lstractdmdmxleadimprat3     AS LSTRACTDMDMXLEADIMPRAT3, ");
        sb.append("\n       bll.lstractdmdmxtimelagexprat3  AS LSTRACTDMDMXTIMELAGEXPRAT3, ");
        sb.append("\n       bll.lstractdmdmxlagexprat3      AS LSTRACTDMDMXLAGEXPRAT3, ");
        sb.append("\n       bll.lstractdmdmxtimeleadexprat3 AS LSTRACTDMDMXTIMELEADEXPRAT3, ");
        sb.append("\n       bll.lstractdmdmxleadexprat3     AS LSTRACTDMDMXLEADEXPRAT3, ");
        sb.append("\n       bll.lstcumactdmdmximptot        AS LSTCUMACTDMDMXIMPTOT, ");
        sb.append("\n       bll.lstcumactdmdmxexptot        AS LSTCUMACTDMDMXEXPTOT, ");
        sb.append("\n       bll.lstcumractdmdmxlagimptot    AS LSTCUMRACTDMDMXLAGIMPTOT, ");
        sb.append("\n       bll.lstcumractdmdmxleadimptot   AS LSTCUMRACTDMDMXLEADIMPTOT, ");
        sb.append("\n       bll.lstcumractdmdmxlagexptot    AS LSTCUMRACTDMDMXLAGEXPTOT, ");
        sb.append("\n       bll.lstcumractdmdmxleadexptot   AS LSTCUMRACTDMDMXLEADEXPTOT, ");
        sb.append("\n       bll.lstcumactdmdmximprat1       AS LSTCUMACTDMDMXIMPRAT1, ");
        sb.append("\n       bll.lstcumactdmdmxexprat1       AS LSTCUMACTDMDMXEXPRAT1, ");
        sb.append("\n       bll.lstcumractdmdmxlagimprat1   AS LSTCUMRACTDMDMXLAGIMPRAT1, ");
        sb.append("\n       bll.lstcumractdmdmxleadimprat1  AS LSTCUMRACTDMDMXLEADIMPRAT1, ");
        sb.append("\n       bll.lstcumractdmdmxlagexprat1   AS LSTCUMRACTDMDMXLAGEXPRAT1, ");
        sb.append("\n       bll.lstcumractdmdmxleadexprat1  AS LSTCUMRACTDMDMXLEADEXPRAT1, ");
        sb.append("\n       bll.lstcumactdmdmximprat2       AS LSTCUMACTDMDMXIMPRAT2, ");
        sb.append("\n       bll.lstcumactdmdmxexprat2       AS LSTCUMACTDMDMXEXPRAT2, ");
        sb.append("\n       bll.lstcumractdmdmxlagimprat2   AS LSTCUMRACTDMDMXLAGIMPRAT2, ");
        sb.append("\n       bll.lstcumractdmdmxleadimprat2  AS LSTCUMRACTDMDMXLEADIMPRAT2, ");
        sb.append("\n       bll.lstcumractdmdmxlagexprat2   AS LSTCUMRACTDMDMXLAGEXPRAT2, ");
        sb.append("\n       bll.lstcumractdmdmxleadexprat2  AS LSTCUMRACTDMDMXLEADEXPRAT2, ");
        sb.append("\n       bll.lstcumactdmdmximprat3       AS LSTCUMACTDMDMXIMPRAT3, ");
        sb.append("\n       bll.lstcumactdmdmxexprat3       AS LSTCUMACTDMDMXEXPRAT3, ");
        sb.append("\n       bll.lstcumractdmdmxlagimprat3   AS LSTCUMRACTDMDMXLAGIMPRAT3, ");
        sb.append("\n       bll.lstcumractdmdmxleadimprat3  AS LSTCUMRACTDMDMXLEADIMPRAT3, ");
        sb.append("\n       bll.lstcumractdmdmxlagexprat3   AS LSTCUMRACTDMDMXLAGEXPRAT3, ");
        sb.append("\n       bll.lstcumractdmdmxleadexprat3  AS LSTCUMRACTDMDMXLEADEXPRAT3, ");
        sb.append("\n       bll.lstkVah                     AS LSTKVAH, ");
        sb.append("\n       bll.lstCummkVah1Rate1           AS LSTCUMMKVAH1RATE1, ");
        sb.append("\n       bll.lstCummkVah1Rate2           AS LSTCUMMKVAH1RATE2, ");
        sb.append("\n       bll.lstCummkVah1Rate3           AS LSTCUMMKVAH1RATE3, ");
        sb.append("\n       bll.lstCummkVah1RateTotal       AS LSTCUMMKVAH1RATETOTAL, ");
        sb.append("\n       bll.lstMaxDmdkVah1Rate1         AS LSTMAXDMDKVAH1RATE1, ");
        sb.append("\n       bll.lstMaxDmdkVah1Rate2         AS LSTMAXDMDKVAH1RATE2, ");
        sb.append("\n       bll.lstMaxDmdkVah1Rate3         AS LSTMAXDMDKVAH1RATE3, ");
        sb.append("\n       bll.lstMaxDmdkVah1RateTotal     AS LSTMAXDMDKVAH1RATETOTAL, ");
        sb.append("\n       bll.lstMaxDmdkVah1TimeRate1     AS LSTMAXDMDKVAH1TIMERATE1, ");
        sb.append("\n       bll.lstMaxDmdkVah1TimeRate2     AS LSTMAXDMDKVAH1TIMERATE2, ");
        sb.append("\n       bll.lstMaxDmdkVah1TimeRate3     AS LSTMAXDMDKVAH1TIMERATE3, ");
        sb.append("\n       bll.lstMaxDmdkVah1TimeRateTotal AS LSTMAXDMDKVAH1TIMERATETOTAL ");
        sb.append("\nFROM ( ");
        sb.append("\n    SELECT SUBSTR(b.yyyymmdd, 1, 6) AS yyyymmdd, ");
        sb.append("\n           b.mdev_type, ");
        sb.append("\n           b.mdev_id, ");
        sb.append("\n           b.contract_id, ");
        sb.append("\n           b.meter_id, ");
        sb.append("\n           SUM(b.activeenergyimportratetotal)    AS actengyimptot, ");
        sb.append("\n           SUM(b.activeenergyexportratetotal)    AS actengyexptot, ");
        sb.append("\n           SUM(b.rtvEnergyLagImpRateTot)         AS ractengylagimptot, ");
        sb.append("\n           SUM(b.rtvEnergyLeadImpRateTot)        AS ractengyleadimptot, ");
        sb.append("\n           SUM(b.rtvEnergyLagExpRateTot)         AS ractengylagexptot, ");
        sb.append("\n           SUM(b.rtvEnergyLeadExpRateTot)        AS ractengyleadexptot, ");
        sb.append("\n           SUM(b.activeenergyimportrate1)        AS actengyimprat1, ");
        sb.append("\n           SUM(b.activeenergyexportrate1)        AS actengyexprat1, ");
        sb.append("\n           SUM(b.reactiveenergylagimportrate1)   AS ractengylagimprat1, ");
        sb.append("\n           SUM(b.reactiveenergyleadimportrate1)  AS ractengyleadimprat1, ");
        sb.append("\n           SUM(b.reactiveenergylagexportrate1)   AS ractengylagexprat1, ");
        sb.append("\n           SUM(b.reactiveenergyleadexportrate1)  AS ractengyleadexprat1, ");
        sb.append("\n           SUM(b.activeenergyimportrate2)        AS actengyimprat2, ");
        sb.append("\n           SUM(b.activeenergyexportrate2)        AS actengyexprat2, ");
        sb.append("\n           SUM(b.reactiveenergylagimportrate2)   AS ractengylagimprat2, ");
        sb.append("\n           SUM(b.reactiveenergyleadimportrate2)  AS ractengyleadimprat2, ");
        sb.append("\n           SUM(b.reactiveenergylagexportrate2)   AS ractengylagexprat2, ");
        sb.append("\n           SUM(b.reactiveenergyleadexportrate2)  AS ractengyleadexprat2, ");
        sb.append("\n           SUM(b.activeenergyimportrate3)        AS actengyimprat3, ");
        sb.append("\n           SUM(b.activeenergyexportrate3)        AS actengyexprat3, ");
        sb.append("\n           SUM(b.reactiveenergylagimportrate3)   AS ractengylagimprat3, ");
        sb.append("\n           SUM(b.reactiveenergyleadimportrate3)  AS ractengyleadimprat3, ");
        sb.append("\n           SUM(b.reactiveenergylagexportrate3)   AS ractengylagexprat3, ");
        sb.append("\n           SUM(b.reactiveenergyleadexportrate3)  AS ractengyleadexprat3, ");
        sb.append("\n           MAX(b.atvPwrDmdMaxTimeImpRateTot)     AS actdmdmxtimeimptot, ");
        sb.append("\n           MAX(b.activepwrdmdmaximportratetotal) AS actdmdmximptot, ");
        sb.append("\n           MAX(b.atvPwrDmdMaxTimeExpRateTot)     AS actdmdmxtimeexptot, ");
        sb.append("\n           MAX(b.activepwrdmdmaxexportratetotal) AS actdmdmxexptot, ");
        sb.append("\n           MAX(b.rtvPwrDmdMaxTimeLagImpRateTot)  AS ractdmdmxtimelagimptot, ");
        sb.append("\n           MAX(b.rtvPwrDmdMaxLagImpRateTot)      AS ractdmdmxlagimptot, ");
        sb.append("\n           MAX(b.rtvPwrDmdMaxTimeLeadImpRateTot) AS ractdmdmxtimeleadimptot, ");
        sb.append("\n           MAX(b.rtvPwrDmdMaxLeadImpRateTot)     AS ractdmdmxleadimptot, ");
        sb.append("\n           MAX(b.rtvPwrDmdMaxTimeLagExpRateTot)  AS ractdmdmxtimelagexptot, ");
        sb.append("\n           MAX(b.rtvPwrDmdMaxLagExpRateTot)      AS ractdmdmxlagexptot, ");
        sb.append("\n           MAX(b.rtvPwrDmdMaxTimeLeadExpRateTot) AS ractdmdmxtimeleadexptot, ");
        sb.append("\n           MAX(b.rtvPwrDmdMaxLeadExpRateTot)     AS ractdmdmxleadexptot, ");
        sb.append("\n           MAX(b.activepwrdmdmaxtimeimportrate1) AS actdmdmxtimeimprat1, ");
        sb.append("\n           MAX(b.activepwrdmdmaximportrate1)     AS actdmdmximprat1, ");
        sb.append("\n           MAX(b.activepwrdmdmaxtimeexportrate1) AS actdmdmxtimeexprat1, ");
        sb.append("\n           MAX(b.activepwrdmdmaxexportrate1)     AS actdmdmxexprat1, ");
        sb.append("\n           MAX(b.rtvPwrDmdMaxTimeLagImpRate1)    AS ractdmdmxtimelagimprat1, ");
        sb.append("\n           MAX(b.rtvPwrDmdMaxLagImpRate1)        AS ractdmdmxlagimprat1, ");
        sb.append("\n           MAX(b.rtvPwrDmdMaxTimeLeadImpRate1)   AS ractdmdmxtimeleadimprat1, ");
        sb.append("\n           MAX(b.rtvPwrDmdMaxLeadImpRate1)       AS ractdmdmxleadimprat1, ");
        sb.append("\n           MAX(b.rtvPwrDmdMaxTimeLagExpRate1)    AS ractdmdmxtimelagexprat1, ");
        sb.append("\n           MAX(b.rtvPwrDmdMaxLagExpRate1)        AS ractdmdmxlagexprat1, ");
        sb.append("\n           MAX(b.rtvPwrDmdMaxTimeLeadExpRate1)   AS ractdmdmxtimeleadexprat1, ");
        sb.append("\n           MAX(b.rtvPwrDmdMaxLeadExpRate1)       AS ractdmdmxleadexprat1, ");
        sb.append("\n           MAX(b.activepwrdmdmaxtimeimportrate2) AS actdmdmxtimeimprat2, ");
        sb.append("\n           MAX(b.activepwrdmdmaximportrate2)     AS actdmdmximprat2, ");
        sb.append("\n           MAX(b.activepwrdmdmaxtimeexportrate2) AS actdmdmxtimeexprat2, ");
        sb.append("\n           MAX(b.activepwrdmdmaxexportrate2)     AS actdmdmxexprat2, ");
        sb.append("\n           MAX(b.rtvPwrDmdMaxTimeLagImpRate2)    AS ractdmdmxtimelagimprat2, ");
        sb.append("\n           MAX(b.rtvPwrDmdMaxLagImpRate2)        AS ractdmdmxlagimprat2, ");
        sb.append("\n           MAX(b.rtvPwrDmdMaxTimeLeadImpRate2)   AS ractdmdmxtimeleadimprat2, ");
        sb.append("\n           MAX(b.rtvPwrDmdMaxLeadImpRate2)       AS ractdmdmxleadimprat2, ");
        sb.append("\n           MAX(b.rtvPwrDmdMaxTimeLagExpRate2)    AS ractdmdmxtimelagexprat2, ");
        sb.append("\n           MAX(b.rtvPwrDmdMaxLagExpRate2)        AS ractdmdmxlagexprat2, ");
        sb.append("\n           MAX(b.rtvPwrDmdMaxTimeLeadExpRate2)   AS ractdmdmxtimeleadexprat2, ");
        sb.append("\n           MAX(b.rtvPwrDmdMaxLeadExpRate2)       AS ractdmdmxleadexprat2, ");
        sb.append("\n           MAX(b.activepwrdmdmaxtimeimportrate3) AS actdmdmxtimeimprat3, ");
        sb.append("\n           MAX(b.activepwrdmdmaximportrate3)     AS actdmdmximprat3, ");
        sb.append("\n           MAX(b.activepwrdmdmaxtimeexportrate3) AS actdmdmxtimeexprat3, ");
        sb.append("\n           MAX(b.activepwrdmdmaxexportrate3)     AS actdmdmxexprat3, ");
        sb.append("\n           MAX(b.rtvPwrDmdMaxTimeLagImpRate3)    AS ractdmdmxtimelagimprat3, ");
        sb.append("\n           MAX(b.rtvPwrDmdMaxLagImpRate3)        AS ractdmdmxlagimprat3, ");
        sb.append("\n           MAX(b.rtvPwrDmdMaxTimeLeadImpRate3)   AS ractdmdmxtimeleadimprat3, ");
        sb.append("\n           MAX(b.rtvPwrDmdMaxLeadImpRate3)       AS ractdmdmxleadimprat3, ");
        sb.append("\n           MAX(b.rtvPwrDmdMaxTimeLagExpRate3)    AS ractdmdmxtimelagexprat3, ");
        sb.append("\n           MAX(b.rtvPwrDmdMaxLagExpRate3)        AS ractdmdmxlagexprat3, ");
        sb.append("\n           MAX(b.rtvPwrDmdMaxTimeLeadExpRate3)   AS ractdmdmxtimeleadexprat3, ");
        sb.append("\n           MAX(b.rtvPwrDmdMaxLeadExpRate3)       AS ractdmdmxleadexprat3, ");
        sb.append("\n           MAX(b.cummAtvPwrDmdMaxImpRateTot)     AS cumactdmdmximptot, ");
        sb.append("\n           MAX(b.cummAtvPwrDmdMaxExpRateTot)     AS cumactdmdmxexptot, ");
        sb.append("\n           MAX(b.cummRtvPwrDmdMaxLagImpRateTot)  AS cumractdmdmxlagimptot, ");
        sb.append("\n           MAX(b.cummRtvPwrDmdMaxLeadImpRateTot) AS cumractdmdmxleadimptot, ");
        sb.append("\n           MAX(b.cummRtvPwrDmdMaxLagExpRateTot)  AS cumractdmdmxlagexptot, ");
        sb.append("\n           MAX(b.cummRtvPwrDmdMaxLeadExpRateTot) AS cumractdmdmxleadexptot, ");
        sb.append("\n           MAX(b.cummactivepwrdmdmaximportrate1) AS cumactdmdmximprat1, ");
        sb.append("\n           MAX(b.cummactivepwrdmdmaxexportrate1) AS cumactdmdmxexprat1, ");
        sb.append("\n           MAX(b.cummRtvPwrDmdMaxLagImpRate1)    AS cumractdmdmxlagimprat1, ");
        sb.append("\n           MAX(b.cummRtvPwrDmdMaxLeadImpRate1)   AS cumractdmdmxleadimprat1, ");
        sb.append("\n           MAX(b.cummRtvPwrDmdMaxLagExpRate1)    AS cumractdmdmxlagexprat1, ");
        sb.append("\n           MAX(b.cummRtvPwrDmdMaxLeadExpRate1)   AS cumractdmdmxleadexprat1, ");
        sb.append("\n           MAX(b.cummactivepwrdmdmaximportrate2) AS cumactdmdmximprat2, ");
        sb.append("\n           MAX(b.cummactivepwrdmdmaxexportrate2) AS cumactdmdmxexprat2, ");
        sb.append("\n           MAX(b.cummRtvPwrDmdMaxLagImpRate2)    AS cumractdmdmxlagimprat2, ");
        sb.append("\n           MAX(b.cummRtvPwrDmdMaxLeadImpRate2)   AS cumractdmdmxleadimprat2, ");
        sb.append("\n           MAX(b.cummRtvPwrDmdMaxLagExpRate2)    AS cumractdmdmxlagexprat2, ");
        sb.append("\n           MAX(b.cummRtvPwrDmdMaxLeadExpRate2)   AS cumractdmdmxleadexprat2, ");
        sb.append("\n           MAX(b.cummactivepwrdmdmaximportrate3) AS cumactdmdmximprat3, ");
        sb.append("\n           MAX(b.cummactivepwrdmdmaxexportrate3) AS cumactdmdmxexprat3, ");
        sb.append("\n           MAX(b.cummRtvPwrDmdMaxLagImpRate3)    AS cumractdmdmxlagimprat3, ");
        sb.append("\n           MAX(b.cummRtvPwrDmdMaxLeadImpRate3)   AS cumractdmdmxleadimprat3, ");
        sb.append("\n           MAX(b.cummRtvPwrDmdMaxLagExpRate3)    AS cumractdmdmxlagexprat3, ");
        sb.append("\n           MAX(b.cummRtvPwrDmdMaxLeadExpRate3)   AS cumractdmdmxleadexprat3, ");
        sb.append("\n           MAX(b.kVah)                           AS kVah, ");
        sb.append("\n           MAX(b.CummkVah1Rate1)                 AS CummkVah1Rate1, ");
        sb.append("\n           MAX(b.CummkVah1Rate2)                 AS CummkVah1Rate2, ");
        sb.append("\n           MAX(b.CummkVah1Rate3)                 AS CummkVah1Rate3, ");
        sb.append("\n           MAX(b.CummkVah1RateTotal)             AS CummkVah1RateTotal, ");
        sb.append("\n           MAX(b.MaxDmdkVah1Rate1)               AS MaxDmdkVah1Rate1, ");
        sb.append("\n           MAX(b.MaxDmdkVah1Rate2)               AS MaxDmdkVah1Rate2, ");
        sb.append("\n           MAX(b.MaxDmdkVah1Rate3)               AS MaxDmdkVah1Rate3, ");
        sb.append("\n           MAX(b.MaxDmdkVah1RateTotal)           AS MaxDmdkVah1RateTotal, ");
        sb.append("\n           MAX(b.MaxDmdkVah1TimeRate1)           AS MaxDmdkVah1TimeRate1, ");
        sb.append("\n           MAX(b.MaxDmdkVah1TimeRate2)           AS MaxDmdkVah1TimeRate2, ");
        sb.append("\n           MAX(b.MaxDmdkVah1TimeRate3)           AS MaxDmdkVah1TimeRate3, ");
        sb.append("\n           MAX(b.MaxDmdkVah1TimeRateTotal)       AS MaxDmdkVah1TimeRateTotal ");
        sb.append("\n    FROM billing_month_em b ");
        sb.append("\n    WHERE b.yyyymmdd BETWEEN :curStartDate AND :curEndDate ");
        sb.append("\n    GROUP BY SUBSTR(b.yyyymmdd, 1, 6), b.mdev_type, b.mdev_id, b.contract_id, b.meter_id ");
        sb.append("\n) bld ");
        sb.append("\n      LEFT OUTER JOIN ( ");
        sb.append("\n            SELECT b2.mdev_type, ");
        sb.append("\n                   b2.mdev_id, ");
        sb.append("\n                   b2.contract_id, ");
        sb.append("\n                   b2.meter_id, ");
        sb.append("\n                   SUM(b2.activeenergyimportratetotal)    AS lstactengyimptot, ");
        sb.append("\n                   SUM(b2.activeenergyexportratetotal)    AS lstactengyexptot, ");
        sb.append("\n                   SUM(b2.rtvEnergyLagImpRateTot)         AS lstractengylagimptot, ");
        sb.append("\n                   SUM(b2.rtvEnergyLeadImpRateTot)        AS lstractengyleadimptot, ");
        sb.append("\n                   SUM(b2.rtvEnergyLagExpRateTot)         AS lstractengylagexptot, ");
        sb.append("\n                   SUM(b2.rtvEnergyLeadExpRateTot)        AS lstractengyleadexptot, ");
        sb.append("\n                   SUM(b2.activeenergyimportrate1)        AS lstactengyimprat1, ");
        sb.append("\n                   SUM(b2.activeenergyexportrate1)        AS lstactengyexprat1, ");
        sb.append("\n                   SUM(b2.reactiveenergylagimportrate1)   AS lstractengylagimprat1, ");
        sb.append("\n                   SUM(b2.reactiveenergyleadimportrate1)  AS lstractengyleadimprat1, ");
        sb.append("\n                   SUM(b2.reactiveenergylagexportrate1)   AS lstractengylagexprat1, ");
        sb.append("\n                   SUM(b2.reactiveenergyleadexportrate1)  AS lstractengyleadexprat1, ");
        sb.append("\n                   SUM(b2.activeenergyimportrate2)        AS lstactengyimprat2, ");
        sb.append("\n                   SUM(b2.activeenergyexportrate2)        AS lstactengyexprat2, ");
        sb.append("\n                   SUM(b2.reactiveenergylagimportrate2)   AS lstractengylagimprat2, ");
        sb.append("\n                   SUM(b2.reactiveenergyleadimportrate2)  AS lstractengyleadimprat2, ");
        sb.append("\n                   SUM(b2.reactiveenergylagexportrate2)   AS lstractengylagexprat2, ");
        sb.append("\n                   SUM(b2.reactiveenergyleadexportrate2)  AS lstractengyleadexprat2, ");
        sb.append("\n                   SUM(b2.activeenergyimportrate3)        AS lstactengyimprat3, ");
        sb.append("\n                   SUM(b2.activeenergyexportrate3)        AS lstactengyexprat3, ");
        sb.append("\n                   SUM(b2.reactiveenergylagimportrate3)   AS lstractengylagimprat3, ");
        sb.append("\n                   SUM(b2.reactiveenergyleadimportrate3)  AS lstractengyleadimprat3, ");
        sb.append("\n                   SUM(b2.reactiveenergylagexportrate3)   AS lstractengylagexprat3, ");
        sb.append("\n                   SUM(b2.reactiveenergyleadexportrate3)  AS lstractengyleadexprat3, ");
        sb.append("\n                   MAX(b2.atvPwrDmdMaxTimeImpRateTot)     AS lstactdmdmxtimeimptot, ");
        sb.append("\n                   MAX(b2.activepwrdmdmaximportratetotal) AS lstactdmdmximptot, ");
        sb.append("\n                   MAX(b2.atvPwrDmdMaxTimeExpRateTot)     AS lstactdmdmxtimeexptot, ");
        sb.append("\n                   MAX(b2.activepwrdmdmaxexportratetotal) AS lstactdmdmxexptot, ");
        sb.append("\n                   MAX(b2.rtvPwrDmdMaxTimeLagImpRateTot)  AS lstractdmdmxtimelagimptot, ");
        sb.append("\n                   MAX(b2.rtvPwrDmdMaxLagImpRateTot)      AS lstractdmdmxlagimptot, ");
        sb.append("\n                   MAX(b2.rtvPwrDmdMaxTimeLeadImpRateTot) AS lstractdmdmxtimeleadimptot, ");
        sb.append("\n                   MAX(b2.rtvPwrDmdMaxLeadImpRateTot)     AS lstractdmdmxleadimptot, ");
        sb.append("\n                   MAX(b2.rtvPwrDmdMaxTimeLagExpRateTot)  AS lstractdmdmxtimelagexptot, ");
        sb.append("\n                   MAX(b2.rtvPwrDmdMaxLagExpRateTot)      AS lstractdmdmxlagexptot, ");
        sb.append("\n                   MAX(b2.rtvPwrDmdMaxTimeLeadExpRateTot) AS lstractdmdmxtimeleadexptot, ");
        sb.append("\n                   MAX(b2.rtvPwrDmdMaxLeadExpRateTot)     AS lstractdmdmxleadexptot, ");
        sb.append("\n                   MAX(b2.activepwrdmdmaxtimeimportrate1) AS lstactdmdmxtimeimprat1, ");
        sb.append("\n                   MAX(b2.activepwrdmdmaximportrate1)     AS lstactdmdmximprat1, ");
        sb.append("\n                   MAX(b2.activepwrdmdmaxtimeexportrate1) AS lstactdmdmxtimeexprat1, ");
        sb.append("\n                   MAX(b2.activepwrdmdmaxexportrate1)     AS lstactdmdmxexprat1, ");
        sb.append("\n                   MAX(b2.rtvPwrDmdMaxTimeLagImpRate1)    AS lstractdmdmxtimelagimprat1, ");
        sb.append("\n                   MAX(b2.rtvPwrDmdMaxLagImpRate1)        AS lstractdmdmxlagimprat1, ");
        sb.append("\n                   MAX(b2.rtvPwrDmdMaxTimeLeadImpRate1)   AS lstractdmdmxtimeleadimprat1, ");
        sb.append("\n                   MAX(b2.rtvPwrDmdMaxLeadImpRate1)       AS lstractdmdmxleadimprat1, ");
        sb.append("\n                   MAX(b2.rtvPwrDmdMaxTimeLagExpRate1)    AS lstractdmdmxtimelagexprat1, ");
        sb.append("\n                   MAX(b2.rtvPwrDmdMaxLagExpRate1)        AS lstractdmdmxlagexprat1, ");
        sb.append("\n                   MAX(b2.rtvPwrDmdMaxTimeLeadExpRate1)   AS lstractdmdmxtimeleadexprat1, ");
        sb.append("\n                   MAX(b2.rtvPwrDmdMaxLeadExpRate1)       AS lstractdmdmxleadexprat1, ");
        sb.append("\n                   MAX(b2.activepwrdmdmaxtimeimportrate2) AS lstactdmdmxtimeimprat2, ");
        sb.append("\n                   MAX(b2.activepwrdmdmaximportrate2)     AS lstactdmdmximprat2, ");
        sb.append("\n                   MAX(b2.activepwrdmdmaxtimeexportrate2) AS lstactdmdmxtimeexprat2, ");
        sb.append("\n                   MAX(b2.activepwrdmdmaxexportrate2)     AS lstactdmdmxexprat2, ");
        sb.append("\n                   MAX(b2.rtvPwrDmdMaxTimeLagImpRate2)    AS lstractdmdmxtimelagimprat2, ");
        sb.append("\n                   MAX(b2.rtvPwrDmdMaxLagImpRate2)        AS lstractdmdmxlagimprat2, ");
        sb.append("\n                   MAX(b2.rtvPwrDmdMaxTimeLeadImpRate2)   AS lstractdmdmxtimeleadimprat2, ");
        sb.append("\n                   MAX(b2.rtvPwrDmdMaxLeadImpRate2)       AS lstractdmdmxleadimprat2, ");
        sb.append("\n                   MAX(b2.rtvPwrDmdMaxTimeLagExpRate2)    AS lstractdmdmxtimelagexprat2, ");
        sb.append("\n                   MAX(b2.rtvPwrDmdMaxLagExpRate2)        AS lstractdmdmxlagexprat2, ");
        sb.append("\n                   MAX(b2.rtvPwrDmdMaxTimeLeadExpRate2)   AS lstractdmdmxtimeleadexprat2, ");
        sb.append("\n                   MAX(b2.rtvPwrDmdMaxLeadExpRate2)       AS lstractdmdmxleadexprat2, ");
        sb.append("\n                   MAX(b2.activepwrdmdmaxtimeimportrate3) AS lstactdmdmxtimeimprat3, ");
        sb.append("\n                   MAX(b2.activepwrdmdmaximportrate3)     AS lstactdmdmximprat3, ");
        sb.append("\n                   MAX(b2.activepwrdmdmaxtimeexportrate3) AS lstactdmdmxtimeexprat3, ");
        sb.append("\n                   MAX(b2.activepwrdmdmaxexportrate3)     AS lstactdmdmxexprat3, ");
        sb.append("\n                   MAX(b2.rtvPwrDmdMaxTimeLagImpRate3)    AS lstractdmdmxtimelagimprat3, ");
        sb.append("\n                   MAX(b2.rtvPwrDmdMaxLagImpRate3)        AS lstractdmdmxlagimprat3, ");
        sb.append("\n                   MAX(b2.rtvPwrDmdMaxTimeLeadImpRate3)   AS lstractdmdmxtimeleadimprat3, ");
        sb.append("\n                   MAX(b2.rtvPwrDmdMaxLeadImpRate3)       AS lstractdmdmxleadimprat3, ");
        sb.append("\n                   MAX(b2.rtvPwrDmdMaxTimeLagExpRate3)    AS lstractdmdmxtimelagexprat3, ");
        sb.append("\n                   MAX(b2.rtvPwrDmdMaxLagExpRate3)        AS lstractdmdmxlagexprat3, ");
        sb.append("\n                   MAX(b2.rtvPwrDmdMaxTimeLeadExpRate3)   AS lstractdmdmxtimeleadexprat3, ");
        sb.append("\n                   MAX(b2.rtvPwrDmdMaxLeadExpRate3)       AS lstractdmdmxleadexprat3, ");
        sb.append("\n                   MAX(b2.cummAtvPwrDmdMaxImpRateTot)     AS lstcumactdmdmximptot, ");
        sb.append("\n                   MAX(b2.cummAtvPwrDmdMaxExpRateTot)     AS lstcumactdmdmxexptot, ");
        sb.append("\n                   MAX(b2.cummRtvPwrDmdMaxLagImpRateTot)  AS lstcumractdmdmxlagimptot, ");
        sb.append("\n                   MAX(b2.cummRtvPwrDmdMaxLeadImpRateTot) AS lstcumractdmdmxleadimptot, ");
        sb.append("\n                   MAX(b2.cummRtvPwrDmdMaxLagExpRateTot)  AS lstcumractdmdmxlagexptot, ");
        sb.append("\n                   MAX(b2.cummRtvPwrDmdMaxLeadExpRateTot) AS lstcumractdmdmxleadexptot, ");
        sb.append("\n                   MAX(b2.cummactivepwrdmdmaximportrate1) AS lstcumactdmdmximprat1, ");
        sb.append("\n                   MAX(b2.cummactivepwrdmdmaxexportrate1) AS lstcumactdmdmxexprat1, ");
        sb.append("\n                   MAX(b2.cummRtvPwrDmdMaxLagImpRate1)    AS lstcumractdmdmxlagimprat1, ");
        sb.append("\n                   MAX(b2.cummRtvPwrDmdMaxLeadImpRate1)   AS lstcumractdmdmxleadimprat1, ");
        sb.append("\n                   MAX(b2.cummRtvPwrDmdMaxLagExpRate1)    AS lstcumractdmdmxlagexprat1, ");
        sb.append("\n                   MAX(b2.cummRtvPwrDmdMaxLeadExpRate1)   AS lstcumractdmdmxleadexprat1, ");
        sb.append("\n                   MAX(b2.cummactivepwrdmdmaximportrate2) AS lstcumactdmdmximprat2, ");
        sb.append("\n                   MAX(b2.cummactivepwrdmdmaxexportrate2) AS lstcumactdmdmxexprat2, ");
        sb.append("\n                   MAX(b2.cummRtvPwrDmdMaxLagImpRate2)    AS lstcumractdmdmxlagimprat2, ");
        sb.append("\n                   MAX(b2.cummRtvPwrDmdMaxLeadImpRate2)   AS lstcumractdmdmxleadimprat2, ");
        sb.append("\n                   MAX(b2.cummRtvPwrDmdMaxLagExpRate2)    AS lstcumractdmdmxlagexprat2, ");
        sb.append("\n                   MAX(b2.cummRtvPwrDmdMaxLeadExpRate2)   AS lstcumractdmdmxleadexprat2, ");
        sb.append("\n                   MAX(b2.cummactivepwrdmdmaximportrate3) AS lstcumactdmdmximprat3, ");
        sb.append("\n                   MAX(b2.cummactivepwrdmdmaxexportrate3) AS lstcumactdmdmxexprat3, ");
        sb.append("\n                   MAX(b2.cummRtvPwrDmdMaxLagImpRate3)    AS lstcumractdmdmxlagimprat3, ");
        sb.append("\n                   MAX(b2.cummRtvPwrDmdMaxLeadImpRate3)   AS lstcumractdmdmxleadimprat3, ");
        sb.append("\n                   MAX(b2.cummRtvPwrDmdMaxLagExpRate3)    AS lstcumractdmdmxlagexprat3, ");
        sb.append("\n                   MAX(b2.cummRtvPwrDmdMaxLeadExpRate3)   AS lstcumractdmdmxleadexprat3, ");
        sb.append("\n                   MAX(b2.kVah)                           AS lstkVah, ");
        sb.append("\n                   MAX(b2.CummkVah1Rate1)                 AS lstCummkVah1Rate1, ");
        sb.append("\n                   MAX(b2.CummkVah1Rate2)                 AS lstCummkVah1Rate2, ");
        sb.append("\n                   MAX(b2.CummkVah1Rate3)                 AS lstCummkVah1Rate3, ");
        sb.append("\n                   MAX(b2.CummkVah1RateTotal)             AS lstCummkVah1RateTotal, ");
        sb.append("\n                   MAX(b2.MaxDmdkVah1Rate1)               AS lstMaxDmdkVah1Rate1, ");
        sb.append("\n                   MAX(b2.MaxDmdkVah1Rate2)               AS lstMaxDmdkVah1Rate2, ");
        sb.append("\n                   MAX(b2.MaxDmdkVah1Rate3)               AS lstMaxDmdkVah1Rate3, ");
        sb.append("\n                   MAX(b2.MaxDmdkVah1RateTotal)           AS lstMaxDmdkVah1RateTotal, ");
        sb.append("\n                   MAX(b2.MaxDmdkVah1TimeRate1)           AS lstMaxDmdkVah1TimeRate1, ");
        sb.append("\n                   MAX(b2.MaxDmdkVah1TimeRate2)           AS lstMaxDmdkVah1TimeRate2, ");
        sb.append("\n                   MAX(b2.MaxDmdkVah1TimeRate3)           AS lstMaxDmdkVah1TimeRate3, ");
        sb.append("\n                   MAX(b2.MaxDmdkVah1TimeRateTotal)       AS lstMaxDmdkVah1TimeRateTotal ");
        sb.append("\n            FROM billing_month_em b2 ");
        sb.append("\n            WHERE b2.yyyymmdd BETWEEN :lastStartDate AND :lastEndDate ");
        sb.append("\n            GROUP BY b2.mdev_type, b2.mdev_id, b2.contract_id, b2.meter_id ");
        sb.append("\n      ) bll ");
        sb.append("\n      ON  bld.mdev_type = bll.mdev_type ");
        sb.append("\n      AND bld.mdev_id = bll.mdev_id ");
        sb.append("\n      AND bld.contract_id = bll.contract_id ");
        sb.append("\n      AND bld.meter_id = bll.meter_id ");
        sb.append("\n      LEFT OUTER JOIN contract con ");
        sb.append("\n      ON bld.contract_id = con.id ");
        sb.append("\n      LEFT OUTER JOIN customer cus ");
        sb.append("\n      ON con.customer_id = cus.id ");
        sb.append("\n      LEFT OUTER JOIN tarifftype taf ");
        sb.append("\n      ON con.tariffindex_id = taf.id ");
        sb.append("\n      LEFT OUTER JOIN location loc ");
        sb.append("\n      ON con.location_id = loc.id ");
        sb.append("\n      LEFT OUTER JOIN meter mtr ");
        sb.append("\n      ON bld.meter_id = mtr.id ");
        sb.append("\nWHERE 1=1 ");

        if (!mdevId.isEmpty()) {
            sb.append("\nAND   bld.mdev_type = :mdevType ");
            sb.append("\nAND   bld.mdev_id = :mdevId ");

            if (detailContractId != null) {
                sb.append("\nAND   bld.contract_id = :detailContractId ");
            } else {
                sb.append("\nAND   bld.contract_id IS NULL ");
            }
            if (detailMeterId != null) {
                sb.append("\nAND   bld.meter_id = :detailMeterId ");
            } else {
                sb.append("\nAND   bld.meter_id IS NULL ");
            }
        }

        if (!"".equals(locationCondition)) {
            sb.append("\nAND   loc.id IN (").append(locationCondition).append(") ");
        }

        if (!"".equals(tariffIndex) && !"0".equals(tariffIndex)) {
            sb.append("\nAND   taf.id = :tariffIndex ");
        }

        if (!"".equals(customerName)) {
            sb.append("\nAND   UPPER(cus.name) LIKE UPPER(:customerName) ");
        }

        if (!"".equals(contractNo)) {
            sb.append("\nAND   con.contract_number LIKE :contractNo ");
        }

        if (!"".equals(meterId)) {
            sb.append("\nAND   mtr.mds_id LIKE :meterId ");
        }

        sb.append("\nORDER BY cus.name ");
        
        SQLQuery query = getSession().createSQLQuery(new SQLWrapper().getQuery(sb.toString()));
        query.setString("curStartDate", startDate);
        query.setString("curEndDate", endDate);
        query.setString("lastStartDate", lastStartDate);
        query.setString("lastEndDate", lastEndDate);

        if (!mdevId.isEmpty()) {
            query.setInteger("mdevType", mdevType);
            query.setString("mdevId", mdevId);
            
            if (detailContractId != null) {
                query.setInteger("detailContractId", detailContractId);
            }
            if (detailMeterId != null) {
                query.setInteger("detailMeterId", detailMeterId);
            }
        }

        if (!"".equals(tariffIndex) && !"0".equals(tariffIndex)) {
            query.setString("tariffIndex", tariffIndex);
        }

        if (!"".equals(customerName)) {
            query.setString("customerName", "%" + customerName + "%");
        }

        if (!"".equals(contractNo)) {
            query.setString("contractNo", "%" + contractNo + "%");
        }

        if (!"".equals(meterId)) {
            query.setString("meterId", "%" + meterId + "%");
        }

        return query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
    }

    /**
     * Billing Data 월별 TOU 리포트 상세데이터 조회
     * 
     * @param conditionMap
     * @return 조회 결과
     */
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> getBillingDetailDataMonthly(Map<String, Object> conditionMap) {

        String startDate         = StringUtil.nullToBlank(conditionMap.get("searchStartDate"));
        String lastStartDate     = StringUtil.nullToBlank(conditionMap.get("lastStartDate"));
        String lastEndDate     = StringUtil.nullToBlank(conditionMap.get("lastEndDate"));
        String lastData          = StringUtil.nullToBlank(conditionMap.get("lastData"));
        String mdevId            = StringUtil.nullToBlank(conditionMap.get("mdevId"));
        
        Integer mdevType         = null;
        if(conditionMap.get("mdevType") 		!= null)	mdevType = Integer.parseInt( conditionMap.get("mdevType").toString() );
        
        Integer detailContractId = null;
	    if(conditionMap.get("detailContractId") != null)	detailContractId = Integer.parseInt( conditionMap.get("detailContractId").toString() );
        
	    Integer detailMeterId    = null;
	    if(conditionMap.get("detailMeterId") 	!= null)	detailMeterId = Integer.parseInt( conditionMap.get("detailMeterId").toString() );

	    StringBuilder sb = new StringBuilder();

        sb.append("\nSELECT bld.yyyymmdd AS YYYYMMDD, ");
        sb.append("\n       bld.hhmmss AS HHMMSS, ");
        
        if (!lastData.isEmpty()) {
            sb.append("\n       bll.yyyymm AS LSTYYYYMMDD, ");
        }
        sb.append("\n       cus.name AS CUSTOMERNAME, ");
        sb.append("\n       con.contract_number AS CONTRACTNUMBER, ");
        sb.append("\n       mtr.mds_id AS METERID, ");
        sb.append("\n       con.contractdemand AS CONTRACTDEMAND, ");
        sb.append("\n       taf.name AS TARIFFTYPENAME, ");
        sb.append("\n       loc.name AS LOCATIONNAME ");
        sb.append("\nFROM billing_month_em bld ");

        if (!lastData.isEmpty()) {
            sb.append("\n      LEFT OUTER JOIN ( ");
            sb.append("\n            SELECT SUBSTR(b2.yyyymmdd, 1, 6) AS yyyymm, ");
            sb.append("\n                   b2.mdev_type, ");
            sb.append("\n                   b2.mdev_id, ");
            sb.append("\n                   b2.contract_id, ");
            sb.append("\n                   b2.meter_id ");
            sb.append("\n            FROM billing_month_em b2 ");
            sb.append("\n            WHERE b2.yyyymmdd BETWEEN :lastStartDate AND :lastEndDate ");
            sb.append("\n            GROUP BY SUBSTR(b2.yyyymmdd, 1, 6), b2.mdev_type, b2.mdev_id, b2.contract_id, b2.meter_id ");
            sb.append("\n      ) bll ");
            sb.append("\n      ON  bld.mdev_type = bll.mdev_type ");
            sb.append("\n      AND bld.mdev_id = bll.mdev_id ");
            sb.append("\n      AND bld.contract_id = bll.contract_id ");
            sb.append("\n      AND bld.meter_id = bll.meter_id ");
        }

        sb.append("\n      LEFT OUTER JOIN contract con ");
        sb.append("\n      ON bld.contract_id = con.id ");
        sb.append("\n      LEFT OUTER JOIN customer cus ");
        sb.append("\n      ON con.customer_id = cus.id ");
        sb.append("\n      LEFT OUTER JOIN tarifftype taf ");
        sb.append("\n      ON con.tariffindex_id = taf.id ");
        sb.append("\n      LEFT OUTER JOIN location loc ");
        sb.append("\n      ON con.location_id = loc.id ");
        sb.append("\n      LEFT OUTER JOIN meter mtr ");
        sb.append("\n      ON bld.meter_id = mtr.id ");

        sb.append("\nWHERE bld.yyyymmdd = :curStartDate ");
        
        sb.append("AND   bld.mdev_type = :mdevType ");
        sb.append("AND   bld.mdev_id = :mdevId ");

        if (detailContractId != null) {
            sb.append("AND   bld.contract_id = :detailContractId ");
        } else {
            sb.append("AND   bld.contract_id IS NULL ");
        }
        if (detailMeterId != null) {
            sb.append("AND   bld.meter_id = :detailMeterId ");
        } else {
            sb.append("AND   bld.meter_id IS NULL ");
        }

        SQLQuery query = getSession().createSQLQuery(new SQLWrapper().getQuery(sb.toString()));

        if (!lastData.isEmpty()) {
            query.setString("lastStartDate", lastStartDate);
            query.setString("lastEndDate", lastEndDate);
        }

        query.setString("curStartDate", startDate);
        query.setInteger("mdevType", mdevType);
        query.setString("mdevId", mdevId);

        if (detailContractId != null) {
            query.setInteger("detailContractId", detailContractId);
        }
        if (detailMeterId != null) {
            query.setInteger("detailMeterId", detailMeterId);
        }

        return query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
    }

	/* (non-Javadoc)
	 * @see com.aimir.dao.mvm.BillingMonthEMDao#getBillingMonthEM(com.aimir.model.mvm.BillingMonthEM)
	 */
	@SuppressWarnings("unchecked")
	public List<BillingMonthEM> getBillingMonthEMs(BillingMonthEM billingMonthEM, String startDay, String finishDay) {
		
		Criteria criteria = getSession().createCriteria(BillingMonthEM.class);
		
		if (billingMonthEM != null) {
			
			if (billingMonthEM.getContract() != null) {
				
				if (billingMonthEM.getContract().getId() != null) {
		
					criteria.add(Restrictions.eq("contract.id", billingMonthEM.getContract().getId()));
				}
			}
			
			if (billingMonthEM.getId() != null) {
				
				if (billingMonthEM.getId().getYyyymmdd() != null) {
					
					if (8 == billingMonthEM.getId().getYyyymmdd().length()) {
						
						criteria.add(Restrictions.eq("id.yyyymmdd", billingMonthEM.getId().getYyyymmdd()));
					} else if (6 == billingMonthEM.getId().getYyyymmdd().length()) {
						
						criteria.add(Restrictions.like("id.yyyymmdd", billingMonthEM.getId().getYyyymmdd() + "%"));
						criteria.addOrder(Property.forName("id.yyyymmdd").desc());
					} else if (4 == billingMonthEM.getId().getYyyymmdd().length()) {
						
						criteria.add(Restrictions.like("id.yyyymmdd", billingMonthEM.getId().getYyyymmdd() + "%"));
						criteria.addOrder(Property.forName("id.yyyymmdd").asc());
					}
				} else {
					
					criteria.addOrder(Property.forName("id.yyyymmdd").desc());
				}
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

	@SuppressWarnings("unchecked")
    public List<Object> getBillingMonthEMsComboBox(BillingMonthEM billingMonthEM, String fromDay, String toDay) {
		StringBuffer sbSql = new StringBuffer();
		sbSql.append(" SELECT yyyymmdd AS yyyymmdd  ")
		.append(" FROM BILLING_MONTH_EM ")
		.append(" WHERE contract_id = :contractId ");

		if(billingMonthEM.getYyyymmdd() != null && billingMonthEM.getYyyymmdd().length() == 4) {
			sbSql.append(" AND yyyymmdd like '").append(billingMonthEM.getId().getYyyymmdd()).append("%' ");			
		} else if(fromDay.length() != 0 ) {
			sbSql.append(" AND yyyymmdd >= '").append(fromDay).append("' ");
		} else if( toDay.length() != 0 ){
			sbSql.append(" AND yyyymmdd <= '").append(toDay).append("' ");
		}

		sbSql.append(" ORDER BY yyyymmdd ");

		SQLQuery query = getSession().createSQLQuery(sbSql.toString());
		query.setInteger("contractId", billingMonthEM.getContract().getId());

		return query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
	}

	/* (non-Javadoc)
	 * @see com.aimir.dao.mvm.BillingMonthEMDao#getBillingMonthEMsAvg(com.aimir.model.mvm.BillingMonthEM, java.lang.String, java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	public List<Object> getBillingMonthEMsAvg(BillingMonthEM billingMonthEM, String fromDay, String toDay) {
		StringBuffer sbSql = new StringBuffer();
		sbSql.append(" SELECT yyyymmdd AS yyyymmdd  ")
		.append(", AVG(bill) AS bill ")
		.append(" FROM BILLING_MONTH_EM ");
		
		if(billingMonthEM.getYyyymmdd() != null && billingMonthEM.getYyyymmdd().length() == 4) {
			sbSql.append(" WHERE yyyymmdd like '").append(billingMonthEM.getId().getYyyymmdd()).append("%' ");			
		} else {
			sbSql.append(" WHERE yyyymmdd >= '").append(fromDay).append("' ")
			.append(" AND yyyymmdd <= '").append(toDay).append("' ");
		}

		sbSql.append(" AND location_id = :locationId ")
		.append(" GROUP BY yyyymmdd ORDER BY yyyymmdd ");

		SQLQuery query = getSession().createSQLQuery(sbSql.toString());
		query.setInteger("locationId", billingMonthEM.getLocation().getId());

		return query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
	}

	/* (non-Javadoc)
	 * @see com.aimir.dao.mvm.BillingMonthEMDao#getBillingYearEm(com.aimir.model.mvm.BillingMonthEM)
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Map<String, Object> getBillingYearEm(BillingMonthEM billingMonthEM) {
		
		Criteria criteria = getSession().createCriteria(BillingMonthEM.class);
		
		if (billingMonthEM != null) {
			
			if (billingMonthEM.getContract() != null) {
				
				if (billingMonthEM.getContract().getId() != null) {
		
					criteria.add(Restrictions.eq("contract.id", billingMonthEM.getContract().getId()));
				}
			}
			
			if (billingMonthEM.getId() != null) {
				
				if (billingMonthEM.getId().getYyyymmdd() != null) {
					
					if (8 == billingMonthEM.getId().getYyyymmdd().length()) {
						
						criteria.add(Restrictions.eq("id.yyyymmdd", billingMonthEM.getId().getYyyymmdd()));
					} else if (6 == billingMonthEM.getId().getYyyymmdd().length()) {
						
						criteria.add(Restrictions.like("id.yyyymmdd", billingMonthEM.getId().getYyyymmdd() + "%"));
						criteria.addOrder(Property.forName("id.yyyymmdd").desc());
					} else if (4 == billingMonthEM.getId().getYyyymmdd().length()) {
						
						criteria.add(Restrictions.like("id.yyyymmdd", billingMonthEM.getId().getYyyymmdd() + "%"));
					}
				}
			}
		}

		criteria.setProjection( Projections.projectionList()
				.add( Projections.sum("activeEnergyRateTotal"), "activeEnergyRateTotal" )
				.add( Projections.sum("bill"), "bill" )
				.add( Projections.sum("co2Emissions"), "co2Emissions" )
				.add( Projections.sum("newMiles"), "newMiles" ));
		
		List result = criteria.list();
		
		Map<String, Object> billingYearEm = (Map<String, Object>) result.get(0);
		
		return billingYearEm;
	}

	/* (non-Javadoc)
	 * @see com.aimir.dao.mvm.BillingMonthEMDao#getAverageUsage(com.aimir.model.mvm.BillingMonthEM)
	 */
	public Double getAverageUsage(BillingMonthEM billingMonthEM) {

        String sqlStr = " "
//            + "\n select avg(a.sumUsage) "
//            + "\n from ( select sum(billingMonthEM.activeEnergyRateTot) sumUsage "
            + "\n select avg(a.sumBill) "
            + "\n from ( select sum(billingMonthEM.bill) sumBill "            
            + "\n          from billing_month_em billingMonthEM "
            + "\n          join contract contract "
            + "\n            on billingMonthEM.contract_id = contract.id "
            + "\n         where billingMonthEM.yyyymmdd like :someMonth "
            + "\n           and contract.supplier_id = :supplier "
            + "\n         group by billingMonthEM.contract_id ) a ";

        SQLQuery query = getSession().createSQLQuery(sqlStr);

		query.setInteger("supplier", billingMonthEM.getContract().getSupplier().getId());
		query.setString("someMonth", billingMonthEM.getYyyymmdd() + "%");
		
		Double averageUsage = Double.parseDouble((query.list().get(0) == null ? 0.0 : query.list().get(0)).toString());
		
		return averageUsage;
	}

	/* (non-Javadoc)
	 * @see com.aimir.dao.mvm.BillingMonthEMDao#getMaxBill(com.aimir.model.system.Contract)
	 */
	public Double getMaxBill(Contract contract, String yyyymmdd) {
		
		Criteria criteria = getSession().createCriteria(BillingMonthEM.class);
		
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
	 * @see com.aimir.dao.mvm.BillingMonthEMDao#getAverageBill(com.aimir.model.system.Contract)
	 */
	public Double getAverageBill(Contract contract, String yyyymmdd) {
		
		Criteria criteria = getSession().createCriteria(BillingMonthEM.class);
		
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

	/**
	 * 정해진 날짜의 PF, ACTIVEPWRDMDMAXIMPORTRATETOTAL, ATVPWRDMDMAXTIMEIMPRATETOT 값을 검색해온다.
	 * @param conditionMap
	 * @return
	 */
	public Map<String, Object> getCurrMonCummMaxDemandData(Map<String, Object> conditionMap) {
		Map<String, Object> returnData = new HashMap<String, Object>();
    	
    	String mdevId = StringUtil.nullToBlank(conditionMap.get("mdevId"));
    	String yyyymmdd = StringUtil.nullToBlank(conditionMap.get("yyyymmdd"));
    	
    	StringBuilder sb = new StringBuilder();
    	
    	sb.append("\n SELECT YYYYMMDD, PF, ACTIVEPWRDMDMAXIMPORTRATETOTAL, ATVPWRDMDMAXTIMEIMPRATETOT");
        sb.append("\n FROM 	Billing_Month_EM  ");
        sb.append("\n WHERE  YYYYMMDD = :yyyymmdd  AND MDEV_ID = :mdevId") ;
        
        SQLQuery query = getSession().createSQLQuery(sb.toString());
        
        query.setString("yyyymmdd", yyyymmdd);
        query.setString("mdevId", mdevId);
       
        List list = query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
    	
        if(list.size() > 0) {
        	returnData = (Map<String, Object>) list.get(0);
        }
    	return returnData;
	}
	
	@Transactional(readOnly=true, propagation=Propagation.REQUIRED)
	public BillingMonthEM getBillingMonthEM(Meter meter, String date) {
		logger.debug("\n " +
				"[getBillingMonthEM]" +
				"\n meter id: " + meter.getMdsId() +
				"\n date: " + date);
		
		Criteria criteria = getSession().createCriteria(BillingMonthEM.class);
		criteria.add(Restrictions.eq("id.yyyymmdd", date));
		criteria.add(Restrictions.eq("id.mdevId", meter.getMdsId()));
		criteria.add(Restrictions.eq("id.mdevType", CommonConstants.DeviceType.Meter));
		return (BillingMonthEM) criteria.uniqueResult();
	}
}