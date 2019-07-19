/**
 * BillingDayEMDaoImpl.java Copyright NuriTelecom Limited 2011
 */

package com.aimir.dao.mvm.impl;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ObjectUtils;
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

import com.aimir.constants.CommonConstants.DeviceType;
import com.aimir.dao.AbstractHibernateGenericDao;
import com.aimir.dao.mvm.BillingDayEMDao;
import com.aimir.model.mvm.BillingDayEM;
import com.aimir.model.system.Contract;
import com.aimir.util.DateTimeUtil;
import com.aimir.util.StringUtil;
import com.aimir.util.TimeUtil;

/**
 * BillingDayEMDaoImpl.java Description 
 *
 * 
 * Date          Version     Author   Description
 * 2011. 4. 26.   v1.0       김상연         해당 계약 최종 일자 조회
 * 2011. 4. 28.   v1.1       김상연         BillingDayEM 조회 조건 (BillingDayEM, 시작일, 종료일) 
 * 2011. 4. 28.   v1.2       김상연        관련 범위  내 평균 사용량 조회
 * 2011. 4. 28.   v1.3       김상연        기간  내 사용량 조회
 * 2011. 5. 13.   v1.4       김상연        동일 공급사 평균 사용량 조회
 * 2011. 5. 31.   v1.5       김상연        최근 데이터 조회
 * 2011. 5. 31.   v1.6       김상연        최근 전체 데이터 조회
 *
 */
@Repository(value = "billingdayemDao")
public class BillingDayEMDaoImpl extends AbstractHibernateGenericDao<BillingDayEM, Integer> implements BillingDayEMDao {

    @SuppressWarnings("unused")
	private static Log logger = LogFactory.getLog(BillingDayEMDaoImpl.class);
    
	@Autowired
	protected BillingDayEMDaoImpl(SessionFactory sessionFactory) {
		super(BillingDayEM.class);
		super.setSessionFactory(sessionFactory);
	}

	/**
     * Billing Data 일별 TOU 데이터 개수 조회
     * 
     * @param conditionMap
     * @return 조회 결과
     */
//    public Long getBillingDataDailyCount(Map<String, Object> conditionMap) {
//
//        String startDate         = StringUtil.nullToBlank(conditionMap.get("searchStartDate"));
//        String locationCondition = StringUtil.nullToBlank(conditionMap.get("locationCondition"));
//        String tariffIndex       = StringUtil.nullToBlank(conditionMap.get("tariffIndexId"));
//        String customerName      = StringUtil.nullToBlank(conditionMap.get("customerName"));
//        String contractNo        = StringUtil.nullToBlank(conditionMap.get("contractNo"));
//        String meterId           = StringUtil.nullToBlank(conditionMap.get("meterId"));
//
//        StringBuilder sb = new StringBuilder();
//        StringBuilder sbLikeParam = null;
//        
//        sb.append("SELECT COUNT(*) ");
//        sb.append("FROM BillingDayEM bill ");
//        sb.append("     LEFT OUTER JOIN bill.contract contract ");
//        sb.append("         LEFT OUTER JOIN bill.contract.customer customer ");
//        sb.append("     LEFT OUTER JOIN bill.meter meter ");
//        sb.append("WHERE bill.id.yyyymmdd = :startDate ");
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
     * Billing Data 일별 TOU 조회
     * 
     * @param conditionMap
     * @param isCount
     * @return 조회 결과
     */
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> getBillingDataDaily(Map<String, Object> conditionMap, boolean isCount) {

        List<Map<String, Object>> result;
        Map<String, Object> map;
        String startDate         = StringUtil.nullToBlank(conditionMap.get("searchStartDate"));
        String locationCondition = StringUtil.nullToBlank(conditionMap.get("locationCondition"));
        String tariffIndex       = StringUtil.nullToBlank(conditionMap.get("tariffIndexId"));
        String customerName      = StringUtil.nullToBlank(conditionMap.get("customerName"));
        String contractNo        = StringUtil.nullToBlank(conditionMap.get("contractNo"));
        String meterId           = StringUtil.nullToBlank(conditionMap.get("meterId"));
        int page = Integer.parseInt(StringUtil.nullToZero(conditionMap.get("page")));
        int pageSize = Integer.parseInt(StringUtil.nullToZero(conditionMap.get("pageSize")));
        int supplierId = Integer.parseInt(conditionMap.get("supplierId").toString());
        
        StringBuilder sb = new StringBuilder();
        StringBuilder sbLikeParam = null;

        if (isCount) {
            sb.append("SELECT COUNT(*) ");
        } else {
            sb.append("SELECT bill.id.yyyymmdd AS yyyymmdd, ");
            sb.append("       bill.id.hhmmss AS hhmmss, ");
            sb.append("       bill.id.mdevType AS mdevType, ");
            sb.append("       bill.id.mdevId AS mdevId, ");
            sb.append("       contract.id AS detailContractId, ");
            sb.append("       meter.id AS detailMeterId, ");
            sb.append("       customer.name AS customerName, ");
            sb.append("       contract.contractNumber AS contractNo, ");
            sb.append("       meter.mdsId AS meterId, ");
            sb.append("       bill.activeEnergyRateTotal AS energyRateTot, ");
            sb.append("       contract.contractDemand AS contractDemand, ");
            sb.append("       bill.activePowerMaxDemandRateTotal AS demandRateTot, ");
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
        sb.append("FROM BillingDayEM bill ");
        sb.append("     LEFT OUTER JOIN bill.contract contract ");
        sb.append("         LEFT OUTER JOIN bill.contract.customer customer ");
        sb.append("     LEFT OUTER JOIN bill.meter meter ");
        sb.append("WHERE bill.id.yyyymmdd = :startDate ");
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
            sb.append("ORDER BY bill.id.hhmmss DESC, customer.name ");
        }

        Query query = getSession().createQuery(sb.toString());
        // criteria.setProjection(Projections.rowCount());

        query.setString("startDate", startDate);
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
     * Billing Data 일별 TOU 리포트 데이터 조회
     * 
     * @param conditionMap
     * @return 조회 결과
     */
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> getBillingDataReportDaily(Map<String, Object> conditionMap) {

        String startDate         = StringUtil.nullToBlank(conditionMap.get("searchStartDate"));
        String lastDate          = StringUtil.nullToBlank(conditionMap.get("lastStartDate"));
        String locationCondition = StringUtil.nullToBlank(conditionMap.get("locationCondition"));
        String tariffIndex       = StringUtil.nullToBlank(conditionMap.get("tariffIndexId"));
        String customerName      = StringUtil.nullToBlank(conditionMap.get("customerName"));
        String contractNo        = StringUtil.nullToBlank(conditionMap.get("contractNo"));
        String meterId           = StringUtil.nullToBlank(conditionMap.get("meterId"));
        String lastData          = StringUtil.nullToBlank(conditionMap.get("lastData"));
        String mdevId            = StringUtil.nullToBlank(conditionMap.get("mdevId"));
        
        Integer mdevType         = null;
        if(conditionMap.get("mdevType")         != null)    mdevType = Integer.parseInt( conditionMap.get("mdevType").toString() );
        
        Integer detailContractId = null;
        if(conditionMap.get("detailContractId") != null)    detailContractId = Integer.parseInt( conditionMap.get("detailContractId").toString() );
        
        Integer detailMeterId    = null;
        if(conditionMap.get("detailMeterId")    != null)    detailMeterId = Integer.parseInt( conditionMap.get("detailMeterId").toString() );

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
        sb.append("       bld.kVah                           AS KVAH, ");
        sb.append("       bld.CummkVah1Rate1                 AS CUMMKVAH1RATE1, ");
        sb.append("       bld.CummkVah1Rate2                 AS CUMMKVAH1RATE2, ");
        sb.append("       bld.CummkVah1Rate3                 AS CUMMKVAH1RATE3, ");
        sb.append("       bld.CummkVah1RateTotal             AS CUMMKVAH1RATETOTAL, ");
        sb.append("       bld.MaxDmdkVah1Rate1               AS MAXDMDKVAH1RATE1, ");
        sb.append("       bld.MaxDmdkVah1Rate2               AS MAXDMDKVAH1RATE2, ");
        sb.append("       bld.MaxDmdkVah1Rate3               AS MAXDMDKVAH1RATE3, ");
        sb.append("       bld.MaxDmdkVah1RateTotal           AS MAXDMDKVAH1RATETOTAL, ");
        sb.append("       bld.MaxDmdkVah1TimeRate1           AS MAXDMDKVAH1TIMERATE1, ");
        sb.append("       bld.MaxDmdkVah1TimeRate2           AS MAXDMDKVAH1TIMERATE2, ");
        sb.append("       bld.MaxDmdkVah1TimeRate3           AS MAXDMDKVAH1TIMERATE3, ");
        sb.append("       bld.MaxDmdkVah1TimeRateTotal       AS MAXDMDKVAH1TIMERATETOTAL ");
        
        if (!"".equals(lastData)) {
            sb.append(", ");
            sb.append("       bll.activeenergyimportratetotal    AS LSTACTENGYIMPTOT, ");
            sb.append("       bll.activeenergyexportratetotal    AS LSTACTENGYEXPTOT, ");
            sb.append("       bll.rtvEnergyLagImpRateTot         AS LSTRACTENGYLAGIMPTOT, ");
            sb.append("       bll.rtvEnergyLeadImpRateTot        AS LSTRACTENGYLEADIMPTOT, ");
            sb.append("       bll.rtvEnergyLagExpRateTot         AS LSTRACTENGYLAGEXPTOT, ");
            sb.append("       bll.rtvEnergyLeadExpRateTot        AS LSTRACTENGYLEADEXPTOT, ");
            sb.append("       bll.activeenergyimportrate1        AS LSTACTENGYIMPRAT1, ");
            sb.append("       bll.activeenergyexportrate1        AS LSTACTENGYEXPRAT1, ");
            sb.append("       bll.reactiveenergylagimportrate1   AS LSTRACTENGYLAGIMPRAT1, ");
            sb.append("       bll.reactiveenergyleadimportrate1  AS LSTRACTENGYLEADIMPRAT1, ");
            sb.append("       bll.reactiveenergylagexportrate1   AS LSTRACTENGYLAGEXPRAT1, ");
            sb.append("       bll.reactiveenergyleadexportrate1  AS LSTRACTENGYLEADEXPRAT1, ");
            sb.append("       bll.activeenergyimportrate2        AS LSTACTENGYIMPRAT2, ");
            sb.append("       bll.activeenergyexportrate2        AS LSTACTENGYEXPRAT2, ");
            sb.append("       bll.reactiveenergylagimportrate2   AS LSTRACTENGYLAGIMPRAT2, ");
            sb.append("       bll.reactiveenergyleadimportrate2  AS LSTRACTENGYLEADIMPRAT2, ");
            sb.append("       bll.reactiveenergylagexportrate2   AS LSTRACTENGYLAGEXPRAT2, ");
            sb.append("       bll.reactiveenergyleadexportrate2  AS LSTRACTENGYLEADEXPRAT2, ");
            sb.append("       bll.activeenergyimportrate3        AS LSTACTENGYIMPRAT3, ");
            sb.append("       bll.activeenergyexportrate3        AS LSTACTENGYEXPRAT3, ");
            sb.append("       bll.reactiveenergylagimportrate3   AS LSTRACTENGYLAGIMPRAT3, ");
            sb.append("       bll.reactiveenergyleadimportrate3  AS LSTRACTENGYLEADIMPRAT3, ");
            sb.append("       bll.reactiveenergylagexportrate3   AS LSTRACTENGYLAGEXPRAT3, ");
            sb.append("       bll.reactiveenergyleadexportrate3  AS LSTRACTENGYLEADEXPRAT3, ");
            sb.append("       bll.atvPwrDmdMaxTimeImpRateTot     AS LSTACTDMDMXTIMEIMPTOT, ");
            sb.append("       bll.activepwrdmdmaximportratetotal AS LSTACTDMDMXIMPTOT, ");
            sb.append("       bll.atvPwrDmdMaxTimeExpRateTot     AS LSTACTDMDMXTIMEEXPTOT, ");
            sb.append("       bll.activepwrdmdmaxexportratetotal AS LSTACTDMDMXEXPTOT, ");
            sb.append("       bll.rtvPwrDmdMaxTimeLagImpRateTot  AS LSTRACTDMDMXTIMELAGIMPTOT, ");
            sb.append("       bll.rtvPwrDmdMaxLagImpRateTot      AS LSTRACTDMDMXLAGIMPTOT, ");
            sb.append("       bll.rtvPwrDmdMaxTimeLeadImpRateTot AS LSTRACTDMDMXTIMELEADIMPTOT, ");
            sb.append("       bll.rtvPwrDmdMaxLeadImpRateTot     AS LSTRACTDMDMXLEADIMPTOT, ");
            sb.append("       bll.rtvPwrDmdMaxTimeLagExpRateTot  AS LSTRACTDMDMXTIMELAGEXPTOT, ");
            sb.append("       bll.rtvPwrDmdMaxLagExpRateTot      AS LSTRACTDMDMXLAGEXPTOT, ");
            sb.append("       bll.rtvPwrDmdMaxTimeLeadExpRateTot AS LSTRACTDMDMXTIMELEADEXPTOT, ");
            sb.append("       bll.rtvPwrDmdMaxLeadExpRateTot     AS LSTRACTDMDMXLEADEXPTOT, ");
            sb.append("       bll.activepwrdmdmaxtimeimportrate1 AS LSTACTDMDMXTIMEIMPRAT1, ");
            sb.append("       bll.activepwrdmdmaximportrate1     AS LSTACTDMDMXIMPRAT1, ");
            sb.append("       bll.activepwrdmdmaxtimeexportrate1 AS LSTACTDMDMXTIMEEXPRAT1, ");
            sb.append("       bll.activepwrdmdmaxexportrate1     AS LSTACTDMDMXEXPRAT1, ");
            sb.append("       bll.rtvPwrDmdMaxTimeLagImpRate1    AS LSTRACTDMDMXTIMELAGIMPRAT1, ");
            sb.append("       bll.rtvPwrDmdMaxLagImpRate1        AS LSTRACTDMDMXLAGIMPRAT1, ");
            sb.append("       bll.rtvPwrDmdMaxTimeLeadImpRate1   AS LSTRACTDMDMXTIMELEADIMPRAT1, ");
            sb.append("       bll.rtvPwrDmdMaxLeadImpRate1       AS LSTRACTDMDMXLEADIMPRAT1, ");
            sb.append("       bll.rtvPwrDmdMaxTimeLagExpRate1    AS LSTRACTDMDMXTIMELAGEXPRAT1, ");
            sb.append("       bll.rtvPwrDmdMaxLagExpRate1        AS LSTRACTDMDMXLAGEXPRAT1, ");
            sb.append("       bll.rtvPwrDmdMaxTimeLeadExpRate1   AS LSTRACTDMDMXTIMELEADEXPRAT1, ");
            sb.append("       bll.rtvPwrDmdMaxLeadExpRate1       AS LSTRACTDMDMXLEADEXPRAT1, ");
            sb.append("       bll.activepwrdmdmaxtimeimportrate2 AS LSTACTDMDMXTIMEIMPRAT2, ");
            sb.append("       bll.activepwrdmdmaximportrate2     AS LSTACTDMDMXIMPRAT2, ");
            sb.append("       bll.activepwrdmdmaxtimeexportrate2 AS LSTACTDMDMXTIMEEXPRAT2, ");
            sb.append("       bll.activepwrdmdmaxexportrate2     AS LSTACTDMDMXEXPRAT2, ");
            sb.append("       bll.rtvPwrDmdMaxTimeLagImpRate2    AS LSTRACTDMDMXTIMELAGIMPRAT2, ");
            sb.append("       bll.rtvPwrDmdMaxLagImpRate2        AS LSTRACTDMDMXLAGIMPRAT2, ");
            sb.append("       bll.rtvPwrDmdMaxTimeLeadImpRate2   AS LSTRACTDMDMXTIMELEADIMPRAT2, ");
            sb.append("       bll.rtvPwrDmdMaxLeadImpRate2       AS LSTRACTDMDMXLEADIMPRAT2, ");
            sb.append("       bll.rtvPwrDmdMaxTimeLagExpRate2    AS LSTRACTDMDMXTIMELAGEXPRAT2, ");
            sb.append("       bll.rtvPwrDmdMaxLagExpRate2        AS LSTRACTDMDMXLAGEXPRAT2, ");
            sb.append("       bll.rtvPwrDmdMaxTimeLeadExpRate2   AS LSTRACTDMDMXTIMELEADEXPRAT2, ");
            sb.append("       bll.rtvPwrDmdMaxLeadExpRate2       AS LSTRACTDMDMXLEADEXPRAT2, ");
            sb.append("       bll.activepwrdmdmaxtimeimportrate3 AS LSTACTDMDMXTIMEIMPRAT3, ");
            sb.append("       bll.activepwrdmdmaximportrate3     AS LSTACTDMDMXIMPRAT3, ");
            sb.append("       bll.activepwrdmdmaxtimeexportrate3 AS LSTACTDMDMXTIMEEXPRAT3, ");
            sb.append("       bll.activepwrdmdmaxexportrate3     AS LSTACTDMDMXEXPRAT3, ");
            sb.append("       bll.rtvPwrDmdMaxTimeLagImpRate3    AS LSTRACTDMDMXTIMELAGIMPRAT3, ");
            sb.append("       bll.rtvPwrDmdMaxLagImpRate3        AS LSTRACTDMDMXLAGIMPRAT3, ");
            sb.append("       bll.rtvPwrDmdMaxTimeLeadImpRate3   AS LSTRACTDMDMXTIMELEADIMPRAT3, ");
            sb.append("       bll.rtvPwrDmdMaxLeadImpRate3       AS LSTRACTDMDMXLEADIMPRAT3, ");
            sb.append("       bll.rtvPwrDmdMaxTimeLagExpRate3    AS LSTRACTDMDMXTIMELAGEXPRAT3, ");
            sb.append("       bll.rtvPwrDmdMaxLagExpRate3        AS LSTRACTDMDMXLAGEXPRAT3, ");
            sb.append("       bll.rtvPwrDmdMaxTimeLeadExpRate3   AS LSTRACTDMDMXTIMELEADEXPRAT3, ");
            sb.append("       bll.rtvPwrDmdMaxLeadExpRate3       AS LSTRACTDMDMXLEADEXPRAT3, ");
            sb.append("       bll.cummAtvPwrDmdMaxImpRateTot     AS LSTCUMACTDMDMXIMPTOT, ");
            sb.append("       bll.cummAtvPwrDmdMaxExpRateTot     AS LSTCUMACTDMDMXEXPTOT, ");
            sb.append("       bll.cummRtvPwrDmdMaxLagImpRateTot  AS LSTCUMRACTDMDMXLAGIMPTOT, ");
            sb.append("       bll.cummRtvPwrDmdMaxLeadImpRateTot AS LSTCUMRACTDMDMXLEADIMPTOT, ");
            sb.append("       bll.cummRtvPwrDmdMaxLagExpRateTot  AS LSTCUMRACTDMDMXLAGEXPTOT, ");
            sb.append("       bll.cummRtvPwrDmdMaxLeadExpRateTot AS LSTCUMRACTDMDMXLEADEXPTOT, ");
            sb.append("       bll.cummactivepwrdmdmaximportrate1 AS LSTCUMACTDMDMXIMPRAT1, ");
            sb.append("       bll.cummactivepwrdmdmaxexportrate1 AS LSTCUMACTDMDMXEXPRAT1, ");
            sb.append("       bll.cummRtvPwrDmdMaxLagImpRate1    AS LSTCUMRACTDMDMXLAGIMPRAT1, ");
            sb.append("       bll.cummRtvPwrDmdMaxLeadImpRate1   AS LSTCUMRACTDMDMXLEADIMPRAT1, ");
            sb.append("       bll.cummRtvPwrDmdMaxLagExpRate1    AS LSTCUMRACTDMDMXLAGEXPRAT1, ");
            sb.append("       bll.cummRtvPwrDmdMaxLeadExpRate1   AS LSTCUMRACTDMDMXLEADEXPRAT1, ");
            sb.append("       bll.cummactivepwrdmdmaximportrate2 AS LSTCUMACTDMDMXIMPRAT2, ");
            sb.append("       bll.cummactivepwrdmdmaxexportrate2 AS LSTCUMACTDMDMXEXPRAT2, ");
            sb.append("       bll.cummRtvPwrDmdMaxLagImpRate2    AS LSTCUMRACTDMDMXLAGIMPRAT2, ");
            sb.append("       bll.cummRtvPwrDmdMaxLeadImpRate2   AS LSTCUMRACTDMDMXLEADIMPRAT2, ");
            sb.append("       bll.cummRtvPwrDmdMaxLagExpRate2    AS LSTCUMRACTDMDMXLAGEXPRAT2, ");
            sb.append("       bll.cummRtvPwrDmdMaxLeadExpRate2   AS LSTCUMRACTDMDMXLEADEXPRAT2, ");
            sb.append("       bll.cummactivepwrdmdmaximportrate3 AS LSTCUMACTDMDMXIMPRAT3, ");
            sb.append("       bll.cummactivepwrdmdmaxexportrate3 AS LSTCUMACTDMDMXEXPRAT3, ");
            sb.append("       bll.cummRtvPwrDmdMaxLagImpRate3    AS LSTCUMRACTDMDMXLAGIMPRAT3, ");
            sb.append("       bll.cummRtvPwrDmdMaxLeadImpRate3   AS LSTCUMRACTDMDMXLEADIMPRAT3, ");
            sb.append("       bll.cummRtvPwrDmdMaxLagExpRate3    AS LSTCUMRACTDMDMXLAGEXPRAT3, ");
            sb.append("       bll.cummRtvPwrDmdMaxLeadExpRate3   AS LSTCUMRACTDMDMXLEADEXPRAT3, ");
            sb.append("       bll.kVah                           AS LSTKVAH, ");
            sb.append("       bll.CummkVah1Rate1                 AS LSTCUMMKVAH1RATE1, ");
            sb.append("       bll.CummkVah1Rate2                 AS LSTCUMMKVAH1RATE2, ");
            sb.append("       bll.CummkVah1Rate3                 AS LSTCUMMKVAH1RATE3, ");
            sb.append("       bll.CummkVah1RateTotal             AS LSTCUMMKVAH1RATETOTAL, ");
            sb.append("       bll.MaxDmdkVah1Rate1               AS LSTMAXDMDKVAH1RATE1, ");
            sb.append("       bll.MaxDmdkVah1Rate2               AS LSTMAXDMDKVAH1RATE2, ");
            sb.append("       bll.MaxDmdkVah1Rate3               AS LSTMAXDMDKVAH1RATE3, ");
            sb.append("       bll.MaxDmdkVah1RateTotal           AS LSTMAXDMDKVAH1RATETOTAL, ");
            sb.append("       bll.MaxDmdkVah1TimeRate1           AS LSTMAXDMDKVAH1TIMERATE1, ");
            sb.append("       bll.MaxDmdkVah1TimeRate2           AS LSTMAXDMDKVAH1TIMERATE2, ");
            sb.append("       bll.MaxDmdkVah1TimeRate3           AS LSTMAXDMDKVAH1TIMERATE3, ");
            sb.append("       bll.MaxDmdkVah1TimeRateTotal       AS LSTMAXDMDKVAH1TIMERATETOTAL ");
        }

        sb.append("FROM billing_day_em bld ");

        if (!"".equals(lastData)) {
            sb.append("     LEFT OUTER JOIN billing_day_em bll ");
            sb.append("     ON  bld.mdev_type = bll.mdev_type ");
            sb.append("     AND bld.mdev_id = bll.mdev_id ");
            sb.append("     AND COALESCE(bld.contract_id,0) = COALESCE(bll.contract_id,0) ");
            sb.append("     AND bld.meter_id = bll.meter_id ");
            sb.append("     AND bll.yyyymmdd = :lastStartDate ");
        }
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
        sb.append("WHERE bld.yyyymmdd = :curStartDate ");

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

        if (!locationCondition.isEmpty()) {
            sb.append("AND   loc.id IN (").append(locationCondition).append(") ");
        }
        
        if (!"".equals(tariffIndex) && !"0".equals(tariffIndex)) {
            sb.append("AND   taf.id = :tariffIndex ");
        }
        
        if (!customerName.isEmpty()) {
            sb.append("AND   UPPER(cus.name) LIKE UPPER(:customerName) ");
        }
        
        if (!contractNo.isEmpty()) {
            sb.append("AND   con.contract_number LIKE :contractNo ");
        }
        
        if (!meterId.isEmpty()) {
            sb.append("AND   mtr.mds_id LIKE :meterId ");
        }

        sb.append("ORDER BY bld.yyyymmdd DESC, cus.name ");

        SQLQuery query = getSession().createSQLQuery(sb.toString());
        // criteria.setProjection(Projections.rowCount());

        query.setString("curStartDate", startDate);

        if (!lastData.isEmpty()) {
            query.setString("lastStartDate", lastDate);
        }

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
        
        if (!customerName.isEmpty()) {
            query.setString("customerName", "%" + customerName + "%");
        }
        
        if (!contractNo.isEmpty()) {
            query.setString("contractNo", "%" + contractNo + "%");
        }
        
        if (!meterId.isEmpty()) {
            query.setString("meterId", "%" + meterId + "%");
        }

        return query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
    }

    /**
     * Billing Data 일별 TOU 리포트 상세데이터 조회
     * 
     * @param conditionMap
     * @return 조회 결과
     */
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> getBillingDetailDataDaily(Map<String, Object> conditionMap) {

        String startDate         = StringUtil.nullToBlank(conditionMap.get("searchStartDate"));
        String lastStartDate     = StringUtil.nullToBlank(conditionMap.get("lastStartDate"));
        String mdevId            = StringUtil.nullToBlank(conditionMap.get("mdevId"));
        String lastData = StringUtil.nullToBlank(conditionMap.get("lastData"));
        
        Integer mdevType         = null;
        if(conditionMap.get("mdevType")         != null)    mdevType = Integer.parseInt( conditionMap.get("mdevType").toString() );
        
        Integer detailContractId = null;
        if(conditionMap.get("detailContractId") != null)    detailContractId = Integer.parseInt( conditionMap.get("detailContractId").toString() );
        
        Integer detailMeterId    = null;
        if(conditionMap.get("detailMeterId")    != null)    detailMeterId = Integer.parseInt( conditionMap.get("detailMeterId").toString() );

        StringBuilder sb = new StringBuilder();

        sb.append("SELECT bld.yyyymmdd AS YYYYMMDD, ");
        sb.append("       bld.hhmmss AS HHMMSS, ");
        sb.append("       cus.name AS CUSTOMERNAME, ");
        sb.append("       con.contract_number AS CONTRACTNUMBER, ");
        sb.append("       mtr.mds_id AS METERID, ");
        sb.append("       con.contractdemand AS CONTRACTDEMAND, ");
        sb.append("       taf.name AS TARIFFTYPENAME, ");
        sb.append("       loc.name AS LOCATIONNAME ");
        if (!lastData.isEmpty()) {
            sb.append("      ,bll.yyyymmdd AS LSTYYYYMMDD, ");
            sb.append("       bll.hhmmss AS LSTHHMMSS ");
        }

        sb.append("FROM billing_day_em bld ");

        if (!lastData.isEmpty()) {
            sb.append("     LEFT OUTER JOIN billing_day_em bll ");
            sb.append("     ON  bld.mdev_type = bll.mdev_type ");
            sb.append("     AND bld.mdev_id = bll.mdev_id ");
            sb.append("     AND bld.contract_id = bll.contract_id ");
            sb.append("     AND bld.meter_id = bll.meter_id ");
            sb.append("     AND bll.yyyymmdd = :lastStartDate ");
        }
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
        sb.append("WHERE bld.yyyymmdd = :curStartDate ");
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

        SQLQuery query = getSession().createSQLQuery(sb.toString());

        if (!lastData.isEmpty()) {
            query.setString("lastStartDate", lastStartDate);
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

    @SuppressWarnings("unchecked")
    public List<BillingDayEM> getBillingDayEMs (BillingDayEM billingDayEM, String startDay, String finishDay) {
        Criteria criteria = getSession().createCriteria(BillingDayEM.class);
        
        if (billingDayEM != null) {
            
            if (billingDayEM.getId() != null) {
                
                if (billingDayEM.getId().getYyyymmdd() != null) {
                    
                    if ( 8 == billingDayEM.getId().getYyyymmdd().length() ) {
                        
                        criteria.add(Restrictions.eq("id.yyyymmdd", billingDayEM.getId().getYyyymmdd()));
                    } else if ( 6 == billingDayEM.getId().getYyyymmdd().length() ) {
                        
                        criteria.add(Restrictions.like("id.yyyymmdd", billingDayEM.getId().getYyyymmdd() + "%"));
                    }
                }
            }
            
            if (billingDayEM.getContract() != null) {
                
                if (billingDayEM.getContract().getId() != null) {
                    
                    criteria.add(Restrictions.eq("contract.id", billingDayEM.getContract().getId()));
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
     * @see com.aimir.dao.mvm.BillingDayEMDao#getBillingDayEMsAvg(com.aimir.model.mvm.BillingDayEM)
     */
    @SuppressWarnings("unchecked")
    public List<Object> getBillingDayEMsAvg (BillingDayEM billingDayEM) {
        
        StringBuffer sbSql = new StringBuffer();
        sbSql.append(" SELECT yyyymmdd AS yyyymmdd  ")
        .append(", AVG(bill) AS bill ")
        .append(" FROM BILLING_DAY_EM ")
        .append(" WHERE yyyymmdd like '").append(billingDayEM.getId().getYyyymmdd()).append("%' ")
        .append(" AND location_id = :locationId ")
        .append(" GROUP BY yyyymmdd ORDER BY yyyymmdd ");

        SQLQuery query = getSession().createSQLQuery(sbSql.toString());
        query.setInteger("locationId", billingDayEM.getLocation().getId());

        return query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
    }


    /* (non-Javadoc)
     * @see com.aimir.dao.mvm.BillingDayEMDao#getAverageUsage(com.aimir.model.system.Contract, java.lang.String, java.lang.String)
     */
    @Override
    public Double getAverageUsage(Contract contract, String startDay,
            String finishDay) {

        String sqlStr = " "
            + "\n select avg(a.sumUsage) "
            + "\n from ( select sum(billingDayEM.activeEnergyRateTot) sumUsage "
            + "\n          from billing_day_em billingDayEM "
            + "\n          join contract contract "
            + "\n            on billingDayEM.contract_id = contract.id "
            + "\n         where billingDayEM.yyyymmdd > :startDay "
            + "\n           and billingDayEM.yyyymmdd <= :finishDay "
            + "\n           and contract.supplier_id = :supplier "
            + "\n         group by billingDayEM.contract_id ) a ";

        SQLQuery query = getSession().createSQLQuery(sqlStr);

        query.setInteger("supplier", contract.getSupplier().getId());
        query.setString("startDay", startDay);
        query.setString("finishDay", finishDay);

        Double averageUsage = Double.parseDouble((query.list().get(0) == null ? 0 : query.list().get(0)).toString());

        return averageUsage;
    }

    /* (non-Javadoc)
     * @see com.aimir.dao.mvm.BillingDayEMDao#getPeriodUsage(com.aimir.model.system.Contract, java.lang.String, java.lang.String)
     */
    @Override
    public Double getPeriodUsage(Contract contract, String startDay,
            String finishDay) {

        Criteria criteria = getSession().createCriteria(BillingDayEM.class);
        
        if (contract != null) {
        
            criteria.add(Restrictions.eq("contract.id", contract.getId()));
        }

        criteria.setProjection( Projections.projectionList().add( Projections.sum("activeEnergyRateTotal") ) );
        
        Double b = Double.parseDouble((criteria.list().get(0) == null ? 0 : criteria.list().get(0)).toString());
        
        return b;
    }

    /* (non-Javadoc)
     * @see com.aimir.dao.mvm.BillingDayEMDao#getAverageUsage(com.aimir.model.mvm.BillingDayEM)
     */
    public Double getAverageUsage(BillingDayEM billingDayEM) {

        String sqlStr = "\n select avg(billingDayEM.bill) "
        //String sqlStr = "\n select avg(billingDayEM.activeEnergyRateTotal) "
            + "\n from BillingDayEM billingDayEM "
            + "\n join billingDayEM.contract contract "
            + "\n where contract.supplier.id = :supplier "
            + "\n and billingDayEM.id.yyyymmdd = :someDay";

        Query query = getSession().createQuery(sqlStr);

        query.setInteger("supplier", billingDayEM.getContract().getSupplier().getId());
        query.setString("someDay", billingDayEM.getYyyymmdd());
        
        Double averageUsage = Double.parseDouble((query.list().get(0) == null ? 0 : query.list().get(0)).toString());
        
        return averageUsage;
    }

    /* (non-Javadoc)
     * @see com.aimir.dao.mvm.BillingDayEMDao#getLast(java.lang.Integer)
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> getLast(Integer id) {
        
        String sqlStr = " "
            + "\n select  id.yyyymmdd            as  lastDay "
            + "\n       , activeEnergyRateTotal  as  usage "
            + "\n       , bill                   as  bill "
            + "\n       , usageReadToDate        as  usageReadToDate "
            + "\n   from  BillingDayEM "
            + "\n  where  contract.id = :contractId "
            + "\n    and  id.yyyymmdd = ( select  max(id.yyyymmdd) "
            + "\n                           from  BillingDayEM "
            + "\n                          where  contract.id = :contractId) ";

        Query query = getSession().createQuery(sqlStr);

        query.setInteger("contractId", id);
        
        List<Map<String, Object>> returnList = query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
        
        return 1 == returnList.size() ? returnList.get(0) : null;
    }
    
    @SuppressWarnings("unchecked")
    public Map<String, Object> getFirst(Integer id) {
        
        String sqlStr = " "
            + "\n select  id.yyyymmdd            as  firstDay "
            + "\n       , activeEnergyRateTotal  as  usage "
            + "\n       , bill                   as  bill "
            + "\n   from  BillingDayEM "
            + "\n  where  contract.id = :contractId "
            + "\n    and  id.yyyymmdd = ( select  min(id.yyyymmdd) "
            + "\n                           from  BillingDayEM "
            + "\n                          where  contract.id = :contractId) ";

        Query query = getSession().createQuery(sqlStr);

        query.setInteger("contractId", id);
        
        List<Map<String, Object>> returnList = query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
        
        return 1 == returnList.size() ? returnList.get(0) : null;
    }

    /* (non-Javadoc)
     * @see com.aimir.dao.mvm.BillingDayEMDao#getSelDate(java.lang.Integer, java.lang.String)
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> getSelDate(Integer id, String selDate) {
        
        String sqlStr = " "
            + "\n select  id.yyyymmdd            as  lastDay "
            + "\n       , activeEnergyRateTotal  as  usage "
            + "\n       , bill                   as  bill "
            + "\n   from  BillingDayEM "
            + "\n  where  contract.id = :contractId "
            + "\n    and  id.yyyymmdd = :yyyymmdd ";

        Query query = getSession().createQuery(sqlStr);

        query.setInteger("contractId", id);
        query.setString("yyyymmdd", selDate);
        
        List<Map<String, Object>> returnList = query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
        
        return 1 == returnList.size() ? returnList.get(0) : null;
    }
    
    /* (non-Javadoc)
     * @see com.aimir.dao.mvm.BillingDayEMDao#getTotal(java.lang.Integer, java.lang.String, java.lang.String)
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> getTotal(Integer id, String lastBillDay, String periodDay) {
        
        String sqlStr = " "
            + "\n select  sum(activeEnergyRateTotal) as totalUsage "
            + "\n       , sum(bill)                  as totalBill "
            + "\n   from  BillingDayEM "
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
     * @see com.aimir.dao.mvm.BillingDayEMDao#getMaxBill(com.aimir.model.system.Contract, java.lang.String)
     */
    public Double getMaxBill(Contract contract, String yyyymmdd) {
        
        Criteria criteria = getSession().createCriteria(BillingDayEM.class);
        
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
    public List<BillingDayEM> getPrepaymentBillingDayList(Integer contractId) {
        StringBuilder sb = new StringBuilder();

        sb.append("\nFROM BillingDayEM b ");
        sb.append("\nWHERE b.contract.id = :contractId ");
        sb.append("\nAND   b.id.yyyymmdd > COALESCE((SELECT MAX(c.id.yyyymmdd) ");
        sb.append("                                  FROM BillingDayEM c ");
        sb.append("                                  WHERE c.contract.id = b.contract.id ");
        sb.append("                                  AND   c.bill IS NOT NULL), '') ");
        sb.append("\nORDER BY b.id.yyyymmdd ");

        Query query = getSession().createQuery(sb.toString());
        
        query.setInteger("contractId", contractId);

        return query.list();
    }

    public Double getAverageBill(Contract contract, String yyyymmdd) {
        
        Criteria criteria = getSession().createCriteria(BillingDayEM.class);

//      if (contract != null) {
//      
//          criteria.add(Restrictions.eq("contract.id", contract.getId()));
//      }
//      
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
        Integer contractId = Integer.parseInt( conditionMap.get("contractId").toString() );
        String startDate = (String)conditionMap.get("startDate");
        String endDate = (String)conditionMap.get("endDate");
        StringBuilder sb = new StringBuilder();

        sb.append("\nSELECT b.id.yyyymmdd AS yyyymmdd, ");
        sb.append("\n       b.activeEnergyRateTotal AS usage, ");
        sb.append("\n       b.bill AS bill ");
        sb.append("\nFROM BillingDayEM b ");
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

    @SuppressWarnings("unchecked")
    @Transactional(readOnly=true)
    public List<Map<String, Object>> getDayBillingInfoData() {

        String today = TimeUtil.getCurrentTimeMilli();
//      String today = "20111005000000";
        int currentDay = Integer.parseInt(today.substring(6,8));
        SQLQuery query = null;
        try{
//          String lastMonth = TimeUtil.getPreMonth(today).substring(0, 8);

            StringBuilder sb = new StringBuilder();
            sb.append("\n SELECT " );
            sb.append("\n   preDE.TOTAL as PREVIOUSMETERREADING, currtDE.TOTAL as CURRENTMETERREADING, ");
            sb.append("\n   currtDE.ID AS ID, currtDE.MDEVID AS MDEVID,currtDE.NUMBER AS NUMBER  ");
            sb.append("\n FROM ( SELECT sum(b1.activeEnergyRateTot) as TOTAL, b1.contract_id AS ID, b1.mdev_id AS MDEVID, c.contract_number AS NUMBER ");
            sb.append("\n        FROM billing_day_em b1 join contract c on  b1.contract_id = c.id ");
            sb.append("\n        WHERE b1.yyyymmdd <= :lastMonth ");
            sb.append("\n        AND b1.supplier_id = c.supplier_id ");
            if(currentDay == 1){
                sb.append("\n    AND (c.bill_date is null OR c.bill_Date = :billDate) ");
            } else {
                 sb.append("\n   AND c.bill_Date = :billDate " );
            }
            sb.append("\n        GROUP BY b1.contract_id, b1.mdev_id, c.contract_number  ) preDE, ");
            sb.append("\n      ( SELECT sum(b2.activeEnergyRateTot) as TOTAL, b2.contract_id AS ID, b2.mdev_id AS MDEVID, MAX(b2.yyyymmdd) AS YYYYMMDD, c.contract_number AS NUMBER  ");
            sb.append("\n        FROM billing_day_em b2 join contract c on  b2.contract_id = c.id ");
            sb.append("\n        WHERE  b2.yyyymmdd <= :today  ");
            sb.append("\n        AND b2.supplier_id = c.supplier_id ");
            if(currentDay == 1){
                sb.append("\n    AND (c.bill_Date is null OR b2.bill_Date = :billDate) " );
            } else {
                 sb.append("\n   AND c.bill_Date = :billDate ");
            }
            sb.append("\n        GROUP BY b2.contract_id, b2.mdev_id, c.contract_number  ) currtDE ");
            sb.append("\n WHERE preDE.ID = currtDE.ID ");
            sb.append("\n AND   preDE.MDEVID = currtDE.MDEVID ");
            sb.append("\n AND   currtDE.YYYYMMDD = :today ");

            query = getSession().createSQLQuery(sb.toString());
            query.setString("lastMonth", TimeUtil.getPreMonth(today).substring(0, 8));
            query.setString("today", today.substring(0, 8));
            query.setInteger("billDate", currentDay);
        }catch(ParseException e){
            e.printStackTrace();
        }
    //  return query.list();
        return query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
    }

    @SuppressWarnings("unchecked")
    @Transactional(readOnly=true)
    public List<Map<String, Object>> getNotDeliveryDayBillingInfoData() {
        String today = TimeUtil.getCurrentTimeMilli();
        int currentDay = Integer.parseInt(DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMdd").substring(6,8));
        SQLQuery query = null;

        StringBuilder sb = new StringBuilder();
        sb.append("\n SELECT " );
        sb.append("\n   result.id as ID ");
        sb.append("\n FROM ( SELECT c.id, b.yyyymmdd ");
        sb.append("            FROM ( SELECT id FROM contract WHERE bill_date =:billDate) c left join billing_day_em b on c.id = b.contract_id AND b.yyyymmdd =:today) result ");
        sb.append("\n WHERE result.yyyymmdd is null ");

        query = getSession().createSQLQuery(sb.toString());
        query.setString("today", today);
        query.setInteger("billDate", currentDay);

        return query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
    }

    @SuppressWarnings("unchecked")
    @Transactional(readOnly=true)
    public List<Map<String, Object>> getReDeliveryDayBillingInfoData(int contractId, String yyyymmdd) {
        SQLQuery query = null;
        try{

            String lastMonth = TimeUtil.getPreMonth(yyyymmdd).substring(0, 8);

            StringBuilder sb = new StringBuilder();
            sb.append("\n SELECT " );
            sb.append("\n   preDE.TOTAL as PREVIOUSMETERREADING, currtDE.TOTAL as CURRENTMETERREADING, ");
            sb.append("\n   currtDE.ID AS ID, currtDE.MDEVID AS MDEVID, currtDE.YYYYMMDD AS YYYYMMDD, currtDE.NUMBER as NUMBER ");
            sb.append("\n FROM ( SELECT b1.activeEnergyRateTot as TOTAL, b1.contract_id AS ID, b1.mdev_id AS MDEVID ");
            sb.append("\n        FROM billing_day_em b1 join contract c on  b1.contract_id = c.id ");
            sb.append("\n        WHERE b1.yyyymmdd = :lastMonth ");
            sb.append("\n        AND b1.supplier_id = c.supplier_id ");
            sb.append("\n        AND b1.contract_id = :contractId ");
            sb.append("\n        GROUP BY b1.activeEnergyRateTot, b1.contract_id, b1.mdev_id ) preDE ");
            sb.append("\n        RIGHT OUTER JOIN  ");
            sb.append("\n      ( SELECT b2.activeEnergyRateTot as TOTAL, b2.contract_id AS ID, b2.mdev_id AS MDEVID, MAX(b2.yyyymmdd) AS YYYYMMDD, c.contract_number as NUMBER ");
            sb.append("\n        FROM billing_day_em b2 join contract c on  b2.contract_id = c.id ");
            sb.append("\n        WHERE  b2.yyyymmdd = :today  ");
            sb.append("\n        AND b2.supplier_id = c.supplier_id ");
            sb.append("\n        AND b2.contract_id = :contractId ");
            sb.append("\n        GROUP BY b2.activeEnergyRateTot, b2.contract_id, b2.mdev_id, c.contract_number ) currtDE ");
            sb.append("\n        ON currtDE.MDEVID = preDE.MDEVID ");
//          sb.append("\n WHERE preDE.ID = currtDE.ID ");       

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
    public boolean hasActiveTotalEnergyByBillDate(int contractId, String yyyymmdd) {

        StringBuilder sb = new StringBuilder();
        sb.append("\n SELECT " );
        sb.append("\n   contract_id ");
        sb.append("\n FROM billing_day_em ");
        sb.append("\n WHERE contract_id =:contractId ");
        sb.append("\n AND yyyymmdd =:yyyymmdd ");
        sb.append("\n AND send_result is null ");

        SQLQuery query = getSession().createSQLQuery(sb.toString());
        query.setInteger("contractId", contractId);
        query.setString("yyyymmdd", yyyymmdd);

        return query.list().size() !=0 ? true : false;
    }

    @SuppressWarnings("unchecked")
    @Transactional(readOnly=true)
    public List<Map<String, Object>> getNotDeliveryDayBillingInfoDataBySupplierBillDate(int serviceType, int creditType) {
        String today = TimeUtil.getCurrentTimeMilli();
        String currentDay = today.substring(6,8);
        SQLQuery query = null;

        StringBuilder sb = new StringBuilder();
        sb.append("\n SELECT " );
        sb.append("\n   result.id as ID ");
        sb.append("\n FROM ( SELECT c.id, b.yyyymmdd ");
        sb.append("            FROM ( SELECT c.id ");
        sb.append("                   FROM contract c, supplytype s ");     
        sb.append("                   WHERE s.billDate =:billDate ");
        sb.append("                   AND s.supplier_id = c.supplier_id ");
        sb.append("                   AND s.type_id = :serviceType ");
        sb.append("                   AND c.servicetype_id =:serviceType ");
        sb.append("                   AND c.creditType_id = :creditType ) c ");     
        sb.append("                left join billing_day_em b on c.id = b.contract_id AND b.yyyymmdd =:today ) result ");
        sb.append("\n WHERE result.yyyymmdd is null ");

        query = getSession().createSQLQuery(sb.toString());
        query.setString("today", today.substring(0, 8));
        query.setInteger("billDate", Integer.parseInt(currentDay));
        query.setInteger("serviceType", serviceType);
        query.setInteger("creditType", creditType);

        return query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
    }

    @SuppressWarnings("unchecked")
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
            sb.append("\n FROM ( SELECT b1.activeEnergyRateTot as TOTAL, b1.contract_id AS ID, b1.mdev_id AS MDEVID, c.contract_number AS NUMBER ");
            sb.append("\n        FROM supplytype s1, billing_day_em b1 join contract c on  b1.contract_id = c.id ");
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
            sb.append("\n        GROUP BY b1.activeEnergyRateTot, b1.contract_id, b1.mdev_id, c.contract_number ) preDE ");
            sb.append("\n        RIGHT OUTER JOIN  ");
            sb.append("\n      ( SELECT b2.activeEnergyRateTot as TOTAL, b2.contract_id AS ID, b2.mdev_id AS MDEVID, MAX(b2.yyyymmdd) AS YYYYMMDD, c.contract_number AS NUMBER  ");
            sb.append("\n        FROM supplytype s2, billing_day_em b2 join contract c on  b2.contract_id = c.id ");
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
            sb.append("\n        GROUP BY b2.activeEnergyRateTot, b2.contract_id, b2.mdev_id, c.contract_number ) currtDE ");
            sb.append("\n        ON currtDE.MDEVID = preDE.MDEVID ");
            sb.append("\n WHERE currtDE.YYYYMMDD = :today ");
//          sb.append("\n AND   preDE.MDEVID = currtDE.MDEVID ");
//          sb.append("\n AND   currtDE.YYYYMMDD = :today ");

            query = getSession().createSQLQuery(sb.toString());
            query.setString("lastMonth", TimeUtil.getPreMonth(today).substring(0, 8));
            query.setString("today", today.substring(0, 8));
            query.setString("billDate", String.valueOf(currentDay));
            query.setInteger("typeId", serviceTypeId);
            query.setInteger("creditType", creditType);

        }catch(ParseException e){
            e.printStackTrace();
        }
    //  return query.list();
        return query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
    }

    public void updateBillingSendResultFlag(boolean sendResultFlag, int contractId, String yyyymmdd) {

        StringBuffer sb = new StringBuffer();
        sb.append("UPDATE BillingDayEM ");
        sb.append("SET sendResult = ? ");
        sb.append("WHERE contract.id = ? ");
        sb.append("AND id.yyyymmdd = ? ");
    
        //HQL문을 이용한 CUD를 할 경우에는 getSession().bulkUpdate() 메소드를 사용한다.  
        // this.getSession().bulkUpdate(sb.toString(), new Object[] {  sendResultFlag, contractId, yyyymmdd} );
    }
    
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> getLastAccumulateBill(String mdevId) {

        Query query = null;
        try{
            StringBuilder sb = new StringBuilder();
            sb.append("\n SELECT  em.id.yyyymmdd AS YYYYMMDD, em.id.hhmmss AS HHMMSS, em.accumulateBill AS ACCUMULATEBILL, em.accumulateUsage AS ACCUMULATEUSAGE ");
            sb.append("\n FROM      BillingDayEM em");
            sb.append("\n WHERE     concat(em.id.yyyymmdd, em.id.hhmmss) = (select  max(concat(e.id.yyyymmdd, e.id.hhmmss)) ");
            sb.append("\n                           from    BillingDayEM e " );
            sb.append("\n                           where   e.id.mdevId = :mdevId   " );
            sb.append("\n                           and     e.accumulateBill IS NOT NULL)");
            sb.append("\n and       em.id.mdevId = :mdevId ");
            sb.append("\n and       em.accumulateBill IS NOT NULL ");
            
            query = getSession().createQuery(sb.toString());
            query.setString("mdevId", mdevId);

        }catch(Exception e){
            e.printStackTrace();
        }
        return query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
    }
    
    public BillingDayEM getBillingDayEM(Map<String, Object> condition) {
        String mdsId = StringUtil.nullToBlank(condition.get("mdsId"));
        String yyyymmdd = StringUtil.nullToBlank(condition.get("yyyymmdd"));
        Integer mdevTypeCode = (Integer) ObjectUtils.defaultIfNull(condition.get("mdevTypeCode"), null);
        Criteria criteria = getSession().createCriteria(BillingDayEM.class);
        DeviceType dType = DeviceType.getDeviceType(mdevTypeCode);
        criteria.add(Restrictions.eq("id.mdevId", mdsId));
        criteria.add(Restrictions.eq("id.yyyymmdd", yyyymmdd));
        if ( mdevTypeCode != null) {
            criteria.add(Restrictions.eq("id.mdevType", dType));
        }
        return (BillingDayEM) criteria.uniqueResult();
    }
}