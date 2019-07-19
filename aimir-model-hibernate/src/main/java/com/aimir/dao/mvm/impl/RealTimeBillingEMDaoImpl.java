package com.aimir.dao.mvm.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.aimir.constants.CommonConstants;
import com.aimir.constants.CommonConstants.DeviceType;
import com.aimir.dao.AbstractHibernateGenericDao;
import com.aimir.dao.mvm.RealTimeBillingEMDao;
import com.aimir.model.device.Meter;
import com.aimir.model.mvm.RealTimeBillingEM;
import com.aimir.util.StringUtil;

@Repository(value = "realtimebillingemDao")
public class RealTimeBillingEMDaoImpl extends AbstractHibernateGenericDao<RealTimeBillingEM, Integer> implements RealTimeBillingEMDao {

    @SuppressWarnings("unused")
    private static Log logger = LogFactory.getLog(RealTimeBillingEMDaoImpl.class);
    
    @Autowired
    protected RealTimeBillingEMDaoImpl(SessionFactory sessionFactory) {
        super(RealTimeBillingEM.class);
        super.setSessionFactory(sessionFactory);
    }

    /**
     * Billing Data Current TOU 조회
     * 
     * @param conditionMap
     * @param isCount
     * @return 조회 결과
     */
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> getBillingDataCurrent(Map<String, Object> conditionMap, boolean isCount) {

        List<Map<String, Object>> result;
        Map<String, Object> map;
        String startDate         = StringUtil.nullToBlank(conditionMap.get("searchStartDate"));
        String locationCondition = StringUtil.nullToBlank(conditionMap.get("locationCondition"));
        String tariffIndex       = StringUtil.nullToBlank(conditionMap.get("tariffIndexId"));
        String customerName      = StringUtil.nullToBlank(conditionMap.get("customerName"));
        String contractNo        = StringUtil.nullToBlank(conditionMap.get("contractNo"));
        String meterId           = StringUtil.nullToBlank(conditionMap.get("meterId"));
        DeviceType mdevType          = DeviceType.Meter;
        
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
        sb.append("FROM RealTimeBillingEM bill ");
        sb.append("     LEFT OUTER JOIN bill.contract contract ");
        sb.append("         LEFT OUTER JOIN bill.contract.customer customer ");
        sb.append("     LEFT OUTER JOIN bill.meter meter ");
        sb.append("WHERE bill.id.yyyymmdd = :startDate ");
        sb.append("AND bill.supplierId = :supplierId ");
        sb.append("AND bill.id.mdevType = :mdevType ");
        
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
        query.setInteger("supplierId", supplierId);
        query.setParameter("mdevType", mdevType);
        
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
     * Billing Data Current TOU 리포트 데이터 조회
     * 
     * @param conditionMap
     * @return 조회 결과
     */
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> getBillingDataReportCurrent(Map<String, Object> conditionMap) {

        String startDate         = StringUtil.nullToBlank(conditionMap.get("searchStartDate"));
        String locationCondition = StringUtil.nullToBlank(conditionMap.get("locationCondition"));
        String tariffIndex       = StringUtil.nullToBlank(conditionMap.get("tariffIndexId"));
        String customerName      = StringUtil.nullToBlank(conditionMap.get("customerName"));
        String contractNo        = StringUtil.nullToBlank(conditionMap.get("contractNo"));
        String meterId           = StringUtil.nullToBlank(conditionMap.get("meterId"));
        String mdevId            = StringUtil.nullToBlank(conditionMap.get("mdevId"));
        String hhmmss            = StringUtil.nullToBlank(conditionMap.get("hhmmss"));
        
        Integer mdevType         = 2;
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
        sb.append("       bld.kVah                           AS KVAH,                           ");
        sb.append("       bld.CummkVah1Rate1                 AS CUMMKVAH1RATE1,             ");
        sb.append("       bld.CummkVah1Rate2                 AS CUMMKVAH1RATE2,             ");
        sb.append("       bld.CummkVah1Rate3                 AS CUMMKVAH1RATE3,             ");
        sb.append("       bld.CummkVah1RateTotal             AS CUMMKVAH1RATETOTAL,         ");
        sb.append("       bld.MaxDmdkVah1Rate1               AS MAXDMDKVAH1RATE1,               ");
        sb.append("       bld.MaxDmdkVah1Rate2               AS MAXDMDKVAH1RATE2,               ");
        sb.append("       bld.MaxDmdkVah1Rate3               AS MAXDMDKVAH1RATE3,               ");
        sb.append("       bld.MaxDmdkVah1RateTotal           AS MAXDMDKVAH1RATETOTAL,           ");
        sb.append("       bld.MaxDmdkVah1TimeRate1           AS MAXDMDKVAH1TIMERATE1,           ");
        sb.append("       bld.MaxDmdkVah1TimeRate2           AS MAXDMDKVAH1TIMERATE2,           ");
        sb.append("       bld.MaxDmdkVah1TimeRate3           AS MAXDMDKVAH1TIMERATE3,           ");
        sb.append("       bld.MaxDmdkVah1TimeRateTotal       AS MAXDMDKVAH1TIMERATETOTAL        ");
        sb.append("FROM realtime_billing_em bld ");
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
        sb.append(" AND bld.mdev_type = :mdevType ");

        if (!mdevId.isEmpty()) {
            sb.append("AND   bld.mdev_id = :mdevId ");
            sb.append("AND   bld.hhmmss = :hhmmss ");
            
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

        SQLQuery query = getSession().createSQLQuery(sb.toString());

        query.setString("curStartDate", startDate);
        query.setInteger("mdevType", mdevType);
        
        if (!mdevId.isEmpty()) {
            query.setString("mdevId", mdevId);
            query.setString("hhmmss", hhmmss);
            
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
     * Billing Data 일별 TOU 리포트 상세데이터 조회
     * 
     * @param conditionMap
     * @return 조회 결과
     */
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> getBillingDetailDataCurrent(Map<String, Object> conditionMap) {

        String startDate         = StringUtil.nullToBlank(conditionMap.get("searchStartDate"));
        String mdevId            = StringUtil.nullToBlank(conditionMap.get("mdevId"));        
        String hhmmss            = StringUtil.nullToBlank(conditionMap.get("hhmmss"));

        Integer mdevType         = 2;
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
        sb.append("FROM realtime_billing_em bld ");
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
        sb.append("AND   bld.hhmmss = :hhmmss ");

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

        query.setString("curStartDate", startDate);
        query.setInteger("mdevType", mdevType);
        query.setString("mdevId", mdevId);
        query.setString("hhmmss", hhmmss);

        if (detailContractId != null) {
            query.setInteger("detailContractId", detailContractId);
        }
        if (detailMeterId != null) {
            query.setInteger("detailMeterId", detailMeterId);
        }

        return query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
    }
    
    @Transactional(readOnly=true, propagation=Propagation.REQUIRED)
    @SuppressWarnings("unchecked")
    @Override
    public Map<String, Object> getMonthlyMaxDemandByMeter(Meter meter,
            String yyyymm) {
        logger.debug("meter: \n" + meter.getMdsId());
        logger.debug("yyyymm: \n" + yyyymm);
        
        Map<String, Object> result = new HashMap<String, Object>();
        String startDate = yyyymm + "01";
        String endDate = yyyymm + "31";

        StringBuilder queryString = new StringBuilder();
        queryString.append("\n FROM RealTimeBillingEM em ");
        queryString.append("\n WHERE em.id.yyyymmdd between :startDate and :endDate ");
        queryString.append("\n AND em.id.mdevId = :meter ");
        queryString.append("\n AND em.id.mdevType = :mdevType ");
        queryString.append("\n ORDER BY em.maxDmdkVah1RateTotal desc ");
                                        
        Query query = getSession().createQuery(queryString.toString());
        query.setString("startDate", startDate);
        query.setString("endDate", endDate);
        query.setString("meter", meter.getMdsId());
        query.setParameter("mdevType", CommonConstants.DeviceType.Meter);
        List<RealTimeBillingEM> list = query.list();
        
        if ( list != null && list.size() > 0) {
            RealTimeBillingEM max = list.get(0);        
            result.put("maxDemand", max.getMaxDmdkVah1RateTotal());
            result.put("writeDate", max.getYyyymmdd());
        }
        return result;
    }
    
    /**
     * 지정한 날짜보다 작은 데이터중 가장 최신의 CummAtvPwrDmdMaxImpRateTot 값을 가지고 온다.
     */
    public Map<String, Object> getCummAtvPwrDmdMaxImpRateTot(Map<String, Object> conditionMap) {
        Map<String, Object> returnData = new HashMap<String, Object>();
        
        String mdevId = StringUtil.nullToBlank(conditionMap.get("mdevId"));
        String yyyymmddhhMMss = StringUtil.nullToBlank(conditionMap.get("yyyymmddhhMMss"));
        String yyyymmdd = null;
        String hhmmss = null;
        
        StringBuilder sb = new StringBuilder();
        
        sb.append("\n SELECT MAX(bld.YYYYMMDDHHMMSS) AS YYYYMMDDHHMMSS");
        sb.append("\n FROM   (SELECT CONCAT(yyyymmdd, hhmmss) as YYYYMMDDHHMMSS, mdev_Id AS MDEVID FROM RealTime_Billing_Em WHERE mdev_Id=:mdevId) bld ");
        sb.append("\n WHERE  bld.YYYYMMDDHHMMSS < :yyyymmddhhMMss AND bld.MDEVID = :mdevId") ;
        
        SQLQuery query = getSession().createSQLQuery(sb.toString());
        
        query.setString("mdevId", mdevId);
        query.setString("yyyymmddhhMMss", yyyymmddhhMMss);
        
        List<Integer> resultList = query.list();
        
        if(resultList.size() > 0 && resultList.get(0) != null) {
            yyyymmdd = String.valueOf(resultList.get(0)).substring(0,8);
            hhmmss = String.valueOf(resultList.get(0)).substring(8,14);
        }
        StringBuilder sb2 = new StringBuilder();
        sb2.append("\n SELECT em.cummAtvPwrDmdMaxImpRateTot AS CUMMATVPWRDMDMAXIMPRATETOT, em.yyyymmdd AS YYYYMMDD, em.hhmmss AS HHMMSS, em.PF AS PF");
        sb2.append("\n FROM RealTime_Billing_Em em");
        sb2.append("\n WHERE em.mdev_type = 2 and em.mdev_Id = :mdevId AND em.yyyymmdd = :yyyymmdd AND em.hhmmss = :hhmmss");
        
        SQLQuery query2 = getSession().createSQLQuery(sb2.toString());
        
        query2.setString("mdevId", mdevId);
        query2.setString("yyyymmdd", yyyymmdd);
        query2.setString("hhmmss", hhmmss);
        
        List list = query2.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
        
        if(list.size() > 0) {
            returnData = (Map<String, Object>) list.get(0);
        }
        return returnData;
    }
    
    /*
     * 
     * 최신에 해당되는 빌링 정보를 추출함
     * 
     */
public RealTimeBillingEM getNewestRealTimeBilling(String mdevId, DeviceType mdevType, String yyyymm) {
        
        StringBuffer sb = new StringBuffer();
        sb.append("\n SELECT  MAX(CONCAT(yyyymmdd,hhmmss)) AS yyyymmddhhmmss");
        sb.append("\n FROM  RealTimeBillingEM em ");
        sb.append("\n WHERE em.id.mdevId = :mdevId ");
        sb.append("\n and em.id.mdevType = :mdevType ");
        sb.append("\n and em.id.yyyymmdd like :yyyymmdd");
        
        Query query = getSession().createQuery(sb.toString());
        query.setString("mdevId", mdevId);
        query.setString("yyyymmdd", yyyymm+"%");
        query.setParameter("mdevType", mdevType);
        
        String yyyymmddhhmmss = null;
        
        if(query.list().get(0) == null || query.list().size() < 0) {
            yyyymmddhhmmss = null;
        } else {
            yyyymmddhhmmss = query.list().get(0).toString();
        }
        
        if (yyyymmddhhmmss == null) return null;
        
        StringBuffer sb2 = new StringBuffer();
        sb2.append("\n FROM  RealTimeBillingEM em ");
        sb2.append("\n WHERE em.id.mdevId = :mdevId");
        sb2.append("\n and em.id.mdevType = :mdevType ");
        sb2.append("\n   AND CONCAT(em.id.yyyymmdd, em.id.hhmmss) = :yyyymmddhhmmss");

        Query query2 = getSession().createQuery(sb2.toString());
        query2.setString("mdevId", mdevId);
        query2.setString("yyyymmddhhmmss", yyyymmddhhmmss);
        query2.setParameter("mdevType", mdevType);
        
        List<RealTimeBillingEM> billingEM = query2.list();
        
        if (billingEM != null && billingEM.size() == 1)
            return billingEM.get(0);
        else
            return null;
    }
    
    @SuppressWarnings({ "unchecked" })
    @Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
    public List<Map<String, Object>> getSgdgXam1RealTimeData(Map<String, Object> conditionMap) {
        List<Map<String, Object>> result = null;

        String minDate = (String) conditionMap.get("minDate");
        
        StringBuilder sb = new StringBuilder();
        
        sb.append("\n   SELECT CONCAT(rm.YYYYMMDD, SUBSTR(rm.HHMMSS,0,4)) AS YYYYMMDDHHMM, rm.MDEV_ID AS MDEV_ID, ");
        sb.append("\n       rm.PF AS PF, rm.ACTIVEPWRMAXDMDRATETOT AS ACTIVEPWRMAXDMDRATETOT, rm.ACTIVEPWRDMDMAXTIMERATETOT AS ACTIVEPWRDMDMAXTIMERATETOT ");
        sb.append("\n   FROM REALTIME_BILLING_EM rm, ");
        sb.append("\n   (SELECT lpEM.MDEV_ID, lpEM.YYYYMMDDHH, lpEM.YYYYMMDD  FROM LP_EM lpEM, CONTRACT c, METER m ");
        sb.append("\n   WHERE lpEM.CONTRACT_ID = c.id AND c.METER_ID = m.id AND lpEM.VALUE_CNT*2 <> LENGTH(NVL(lpEM.SEND_RESULT,0))");
        sb.append("\n   AND lpEM.CHANNEL=1 AND lpEM.YYYYMMDDHH BETWEEN :minDate AND :maxDate) con ");
        sb.append("\n   WHERE con.MDEV_ID = rm.MDEV_ID");
        sb.append("\n   AND rm.YYYYMMDD = con.YYYYMMDD");
        sb.append("\n   AND rm.HHMMSS like substr(con.YYYYMMDDHH, -2, 2) || '%'");  

        SQLQuery query = getSession().createSQLQuery(sb.toString());

        query.setString("minDate", minDate);
        query.setString("maxDate", minDate);
        
        result = query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();

        return result;
    }
    
    @Override
    @Transactional(propagation=Propagation.REQUIRES_NEW)
    public void delete(String meterId, String yyyymmdd) {
        String qstr = "DELETE from RealTimeBillingEM em WHERE em.id.mdevId = :meterId AND em.id.yyyymmdd = :yyyymmdd";
        Query query = getSession().createQuery(qstr);
        query.setString("meterId", meterId);
        query.setString("yyyymmdd", yyyymmdd);
        query.executeUpdate();
    }
}