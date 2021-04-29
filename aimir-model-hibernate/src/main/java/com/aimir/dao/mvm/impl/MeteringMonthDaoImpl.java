package com.aimir.dao.mvm.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.aimir.constants.CommonConstants;
import com.aimir.constants.CommonConstants.ChangeMeterTypeName;
import com.aimir.constants.CommonConstants.DateType;
import com.aimir.constants.CommonConstants.MeterType;
import com.aimir.dao.AbstractHibernateGenericDao;
import com.aimir.dao.mvm.MeteringMonthDao;
import com.aimir.model.mvm.MeteringMonth;
import com.aimir.util.CalendarUtil;
import com.aimir.util.SQLWrapper;
import com.aimir.util.StringUtil;

@Repository(value = "meteringmonthDao")
@SuppressWarnings("unchecked")
public class MeteringMonthDaoImpl extends AbstractHibernateGenericDao<MeteringMonth, Integer> implements MeteringMonthDao {
	
    private static Log logger = LogFactory.getLog(MeteringMonthDaoImpl.class);
	 
    @Autowired
    protected MeteringMonthDaoImpl(SessionFactory sessionFactory) {
        super(MeteringMonth.class);
        super.setSessionFactory(sessionFactory);
    }

    @SuppressWarnings("deprecation")
    public List<Object> getConsumptionRanking(Map<String,Object> condition) {

        Query query = null;
        String rankingType = null; //jhkim 
    try{
        String meterType   = (String)condition.get("meterType");
        rankingType = (String)condition.get("rankingType"); 
        int tariffType     = (Integer)condition.get("tariffType");
        int supplierId     = (Integer)condition.get("supplierId");
        String yyyymm      = ((String)condition.get("startDate")).substring(0, 6);
        List<Integer> locations = ((List<Integer>)condition.get("locations"));
        
//      logger.info("==conditions===="+condition);

        String monthTable = MeterType.valueOf(meterType).getMonthClassName();
        
        StringBuffer sb = new StringBuffer();
      
        sb.append("\n SELECT  MAX(c.customer.name) as customerName ");
        sb.append("\n        ,MAX(c.contractNumber) as contractNo  ");
        sb.append("\n        ,SUM(m.total) as usage                ");
        sb.append("\n FROM    ").append(monthTable).append(" m ");
        sb.append("\n         INNER JOIN m.contract c ");
        sb.append("\n WHERE   m.id.mdevType = :mdevType ");
        sb.append("\n AND     m.id.yyyymm = :yyyymm                ");
        sb.append("\n AND     m.id.channel = 1                     ");
        sb.append("\n AND     c.serviceTypeCode.code = :serviceType ");
        sb.append("\n AND     c.serviceTypeCode.id = c.tariffIndex.serviceTypeCode.id ");
        sb.append("\n AND     c.location.id IN (:locations) ");
        if(supplierId > 0){
            sb.append("\n AND     c.supplier.id = :supplierId            ");
            sb.append("\n AND     c.supplier.id = c.tariffIndex.supplier.id ");
        }
        if( tariffType > 0 ){
            sb.append("\n AND     c.tariffIndex.id = :tariffType ");
        }
        if( rankingType.equals(CommonConstants.RankingType.ZERO.getType()) ){
            sb.append("\n GROUP BY c.id ");
            sb.append("\n ORDER BY c.id ");
        }
        else if( rankingType.equals(CommonConstants.RankingType.WORST.getType()) ){
            sb.append("\n GROUP BY c.id ");
            sb.append("\n ORDER BY SUM(m.total), c.id ");
        }
        else{           
            sb.append("\n GROUP BY c.id ");
            sb.append("\n ORDER BY SUM(m.total) DESC, c.id ");
        }

        query = getSession().createQuery(sb.toString())
                            .setString("yyyymm", yyyymm)
                            .setString("mdevType", CommonConstants.DeviceType.Meter.name());
        
        query.setParameterList("locations", locations);
        query.setString("serviceType", MeterType.valueOf(meterType).getServiceType());
        
        if(supplierId > 0){
            query.setInteger("supplierId", supplierId);
        }
        if( tariffType > 0 ){
            query.setInteger("tariffType", tariffType);
        }

    }catch(Exception e){
        e.printStackTrace();
    }
    //jhkim 사용량 0인 미터 조건 추가 
    if(rankingType.equals("0")){
        return query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP)
                    .list();
    }
    else{
        return query.setFirstResult(CommonConstants.Paging.FIRST.getPageNum())
                    .setMaxResults(CommonConstants.Paging.ROWPERPAGE.getPageNum())
                    .setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP)
                    .list();
        }
    }

    @Deprecated
    public List<Object> getConsumptionRankingList(Map<String,Object> condition) {

        String meterType   = (String)condition.get("meterType");
        String rankingType = (String)condition.get("rankingType");
        int supplierId     = (Integer)condition.get("supplierId");
        int tariffType     = (Integer)condition.get("tariffType");
        int rankingCount   = (Integer)condition.get("rankingCount");
        String contractNo  = (String)condition.get("contractNo");
        String startDate   = (String)condition.get("startDate");
        String endDate     = (String)condition.get("endDate");
        String yyyymm      = startDate.substring(0, 6);
        List<Integer> locations = ((List<Integer>)condition.get("locations"));

//        logger.info("==conditions===="+condition);

        String monthTable = MeterType.valueOf(meterType).getMonthClassName();
        
        StringBuffer sb = new StringBuffer();
      
        sb.append("\n SELECT  SUM(m.total) as usage                  ");
        sb.append("\n        ,MAX(c.contractNumber) as contractNo    ");
        sb.append("\n        ,'"+startDate+" ~ "+endDate+"' as period ");
        sb.append("\n        ,MAX(c.customer.name) as customerName   ");
        sb.append("\n        ,MAX(c.tariffIndex.name) as tariffName  ");
        sb.append("\n        ,MAX(c.location.name) as locationName   ");
        sb.append("\n FROM    ").append(monthTable).append(" m ");
        sb.append("\n         INNER JOIN m.contract c   ");
        sb.append("\n WHERE   m.id.mdevType = :mdevType ");
        sb.append("\n AND     m.id.yyyymm = :yyyymm                  ");
        sb.append("\n AND     m.id.channel = 1                     ");
        sb.append("\n AND     c.serviceTypeCode.code = :serviceType  ");
        sb.append("\n AND     c.serviceTypeCode.id = c.tariffIndex.serviceTypeCode.id ");
        sb.append("\n AND     c.location.id IN (:locations) ");
        if(supplierId > 0){
            sb.append("\n AND     c.supplier.id = :supplierId            ");
            sb.append("\n AND     c.supplier.id = c.tariffIndex.supplier.id ");
        }
        if( tariffType > 0 ){
            sb.append("\n AND     c.tariffIndex.id = :tariffType ");
        }
        if(!"".equals(contractNo)){
            sb.append("\n AND     c.contractNumber = :contractNo ");
        }
        if( rankingType.equals(CommonConstants.RankingType.ZERO.getType()) ){
            sb.append("\n GROUP BY c.id ");
            sb.append("\n ORDER BY c.id ");
        }
        else if( rankingType.equals(CommonConstants.RankingType.WORST.getType()) ){
            sb.append("\n GROUP BY c.id ");
            sb.append("\n ORDER BY SUM(m.total), c.id ");
        }
        else{
            sb.append("\n GROUP BY c.id ");
            sb.append("\n ORDER BY SUM(m.total) DESC, c.id ");
        }

        Query query = getSession().createQuery(sb.toString())
                                  .setString("yyyymm", yyyymm)
                                  .setString("mdevType", CommonConstants.DeviceType.Meter.name());
        
        query.setParameterList("locations", locations);
        query.setString("serviceType", MeterType.valueOf(meterType).getServiceType());

        if(supplierId > 0){
            query.setInteger("supplierId", supplierId);
        }
        if( tariffType > 0 ){
            query.setInteger("tariffType", tariffType);
        }
        if(!"".equals(contractNo)){
            query.setString("contractNo", contractNo);
        }

        if(rankingCount > 0){
           query.setFirstResult(CommonConstants.Paging.FIRST.getPageNum())
                .setMaxResults(rankingCount);
        }
        
        return query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP)
                    .list();
    }

    /**
     * method name : getConsumptionRankingDataList<b/>
     * method Desc : Consumption Ranking 가젯에서 소비랭킹 리스트를 조회한다.
     *
     * @param conditionMap
     * @param isCount Count 조회 여부
     * @return
     */
    public List<Map<String, Object>> getConsumptionRankingDataList(Map<String,Object> conditionMap, boolean isCount) {
        return getConsumptionRankingDataList(conditionMap, isCount, false);
    }

    /**
     * method name : getConsumptionRankingDataList<b/>
     * method Desc : Consumption Ranking 가젯에서 소비랭킹 리스트를 조회한다.
     *
     * @param conditionMap
     * @param isCount Count 조회 여부
     * @param isAll 전체 조회 여부
     * @return
     */
    @SuppressWarnings("unused")
    public List<Map<String, Object>> getConsumptionRankingDataList(Map<String,Object> conditionMap, boolean isCount, boolean isAll) {
        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
        String meterType = (String) conditionMap.get("meterType");
        String rankingType = (String) conditionMap.get("rankingType");
        String sysId = (String) conditionMap.get("sysId");
        Integer dcuId = (Integer) conditionMap.get("dcuId");
        Integer supplierId = (Integer) conditionMap.get("supplierId");
        Integer tariffType = (Integer) conditionMap.get("tariffType");
        Integer rankingCount = (Integer) conditionMap.get("rankingCount");
        String contractNo = StringUtil.nullToBlank(conditionMap.get("contractNo"));
        String startDate = (String) conditionMap.get("startDate");
        String endDate = (String) conditionMap.get("endDate");
        String usageRange = StringUtil.nullToBlank(conditionMap.get("usageRange"));
        Double totalUsage = (Double)conditionMap.get("totalUsage");
        Integer page = (Integer)conditionMap.get("page");
        Integer limit = (Integer)conditionMap.get("limit");
        String yyyymm = startDate.substring(0, 6);
        List<Integer> locationList = ((List<Integer>)conditionMap.get("locationList"));
        String monthTable = MeterType.valueOf(meterType).getMonthTableName();

        StringBuffer sb = new StringBuffer();

        if (isCount) {
            sb.append("\nSELECT COUNT(*) AS cnt FROM (SELECT co.id, me.mdev_id AS mdsid ");
        } else {
            sb.append("\nSELECT tb.totalUsage AS \"totalUsage\", ");
            sb.append("\n       tb.totalUsage-NVL(pre.preusage,0) AS \"usage\", ");
            sb.append("\n       tb.contractNo AS \"contractNo\", ");
            sb.append("\n       tb.customerName AS \"customerName\", ");
            sb.append("\n       tb.tariffName AS \"tariffName\", ");
            sb.append("\n       tb.locationName AS \"locationName\", ");
            sb.append("\n       tb.mdsId AS \"mdsId\" ");
            sb.append("\nFROM ( ");
            sb.append("\nSELECT  ");

            if (rankingType.equals(CommonConstants.RankingType.ZERO.getType())) {
                sb.append("0 ");
            } else {
                sb.append("MAX(me.value) ");
            }

            sb.append("AS totalUsage, ");
            sb.append("\n       co.contract_number AS contractNo, ");
            sb.append("\n       cu.name AS customerName, ");
            sb.append("\n       ta.name AS tariffName, ");
            sb.append("\n       lo.name AS locationName, ");
            sb.append("\n       me.mdev_id AS mdsId, ");
            sb.append("\n       co.id AS contractId ");
        }
        sb.append("\nFROM ").append(monthTable).append(" me, ");
        sb.append("\n     contract co, ");
        if(sysId != null && !"".equals(sysId)) {
        	sb.append("\n     meter m, ");
            sb.append("\n     modem mo, ");
        }
        sb.append("\n     customer cu, ");
        sb.append("\n     tarifftype ta, ");
        sb.append("\n     location lo, ");
        sb.append("\n     code cd ");
        sb.append("\nWHERE me.contract_id = co.id ");
        sb.append("\nAND   co.customer_id = cu.id ");
        sb.append("\nAND   co.tariffIndex_id = ta.id ");
        sb.append("\nAND   co.location_id = lo.id ");
        sb.append("\nAND   co.servicetype_id = cd.id ");
        sb.append("\nAND   me.mdev_type = :mdevType ");
        sb.append("\nAND   me.yyyymm = :yyyymm ");
        sb.append("\nAND   me.channel = 1 ");
        sb.append("\nAND   cd.code = :serviceType ");
        sb.append("\nAND   co.servicetype_id = ta.servicetype_id ");

        if(sysId != null && !"".equals(sysId)) {
            sb.append("\nAND   co.meter_id = m.id ");
            sb.append("\nAND   m.modem_id = mo.id ");
            sb.append("\nAND   mo.mcu_id = :dcuId ");
        }
        
        if (locationList != null && locationList.size() > 0) {
            sb.append("\nAND   co.location_id IN (:locationList) ");
        }

        if (supplierId != null && supplierId > 0) {
            sb.append("\nAND   co.supplier_id = :supplierId ");
            sb.append("\nAND   co.supplier_id = ta.supplier_id ");
        }
        if (tariffType != null && tariffType > 0) {
            sb.append("\nAND   co.tariffindex_id = :tariffType ");
        }
        if (!contractNo.isEmpty()) {
            sb.append("\nAND   co.contract_number = :contractNo ");
        }

        sb.append("\nGROUP BY co.id, ");
        sb.append("\n         co.contract_number, ");
        sb.append("\n         cu.name, ");
        sb.append("\n         ta.name, ");
        sb.append("\n         lo.name, ");
        sb.append("\n         me.mdev_id ");

        if (rankingType.equals(CommonConstants.RankingType.ZERO.getType())) {
            sb.append("\nHAVING MAX(me.value) = 0 ");
        } else if (totalUsage != null) {
            if (rankingType.equals(CommonConstants.RankingType.BEST.getType())) {
                sb.append("\nHAVING MAX(me.value) >= :totalUsage ");
            } else {
                sb.append("\nHAVING MAX(me.value) <= :totalUsage ");
            }
        } else if (!usageRange.isEmpty()) {
            String[] condArr = usageRange.split(",", 4);

            if (condArr.length == 4
                    && ((!condArr[0].isEmpty() && !condArr[1].isEmpty()) || (!condArr[2].isEmpty() && !condArr[3].isEmpty()))) {
                StringBuilder sbCond = new StringBuilder();

                if (!condArr[0].isEmpty() && !condArr[1].isEmpty()) {
                    sbCond.append("\nHAVING MAX(me.value) ");
                    sbCond.append(condArr[1]).append(" ");
                    sbCond.append(condArr[0]).append(" ");
                }

                if (!condArr[2].isEmpty() && !condArr[3].isEmpty()) {
                    if (sbCond.length() == 0) {
                        sbCond.append("\nHAVING");
                    } else {
                        sbCond.append("\n   AND");
                    }
                    sbCond.append(" MAX(me.value) ");
                    sbCond.append(condArr[3]).append(" ");
                    sbCond.append(condArr[2]).append(" ");
                }
                sb.append(sbCond);
            }
        }

        sb.append("\n) tb ");
        
        sb.append("\nLEFT OUTER JOIN  ( ");
        sb.append("\n  	SELECT mdev_id, max(value) as preusage ");
		sb.append("\n   FROM "+ monthTable);
        sb.append("\n   WHERE channel = 1 ");
        sb.append("\n   AND YYYYMM = :yyyymm ");
        sb.append("\n   	GROUP BY mdev_id ");
        sb.append("\n) pre  ");
		sb.append("\n	ON pre.mdev_id = tb.mdsId ");

        if (!isCount) {
            sb.append("\nORDER BY ");
            if (rankingType.equals(CommonConstants.RankingType.ZERO.getType())) {
                sb.append("tb.contractId ");
            } else if (rankingType.equals(CommonConstants.RankingType.WORST.getType())) {
                sb.append("tb.totalUsage, tb.contractId ");
            } else {
                sb.append("tb.totalUsage DESC, tb.contractId ");
            }
        }

        SQLQuery query = getSession().createSQLQuery(new SQLWrapper().getQuery(sb.toString()));

        query.setString("yyyymm", yyyymm);
        query.setString("mdevType", CommonConstants.DeviceType.Meter.name());
        query.setString("serviceType", MeterType.valueOf(meterType).getServiceType());

        if(sysId != null && !"".equals(sysId)) {
            query.setInteger("dcuId", dcuId);        	
        }
        
        if (locationList != null && locationList.size() > 0) {
            query.setParameterList("locationList", locationList);
        }

        if (supplierId != null && supplierId > 0) {
            query.setInteger("supplierId", supplierId);
        }

        if (tariffType != null && tariffType > 0) {
            query.setInteger("tariffType", tariffType);
        }

        if (!contractNo.isEmpty()) {
            query.setString("contractNo", contractNo);
        }

        if (totalUsage != null && !rankingType.equals(CommonConstants.RankingType.ZERO.getType())) {
            query.setDouble("totalUsage", totalUsage);
        }

        if (isCount) {
            Map<String, Object> map = new HashMap<String, Object>();
            int totalCount = ((Number)query.uniqueResult()).intValue();

            if (rankingCount != null && rankingCount > 0 && totalCount > rankingCount) {
                totalCount = rankingCount;
            }
            map.put("total", totalCount);
            result = new ArrayList<Map<String, Object>>();
            result.add(map);
        } else {
            if (isAll) {
                if (rankingCount != null && rankingCount > 0) {
                    query.setFirstResult(0);
                    query.setMaxResults(rankingCount);
                }
            } else {
                query.setFirstResult((page - 1) * limit);

                if (rankingCount != null && rankingCount > 0 && rankingCount < (page * limit)) {
                    query.setMaxResults(rankingCount - ((page - 1) * limit));
                } else {
                    query.setMaxResults(limit);
                }
            }
            result = query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
        }

        return result;
    }

    /*
     * 검침데이터 상세 월별 데이터
     */
    @Deprecated
    public List<Object> getDetailMonthSearchData(HashMap<String, Object> condition) {
        
        Query query = null;
        try {
            
        String meterType   = (String)condition.get("meterType");
        String beginDate   = (String)condition.get("beginMonthDate");
        String endDate     = (String)condition.get("endMonthDate");
        Integer meterId    = (Integer)condition.get("meterId");
        List<Integer> arrChannel = ((List<Integer>)condition.get("arrChannel"));

//      logger.info("\n====conditions====\n"+condition);

        String momthTable = MeterType.valueOf(meterType).getMonthClassName();
                
        StringBuffer sb = new StringBuffer();
        
        
        sb.append("\n SELECT  month.id.yyyymm, month.id.channel, month.total ");
        sb.append("\n FROM    ").append(momthTable).append(" month");
        sb.append("\n WHERE   month.meter.id = :meterId            ");
        sb.append("\n AND     month.id.yyyymm >= :startDate          ");
        sb.append("\n AND     month.id.yyyymm <= :endDate            ");
        sb.append("\n AND     month.id.mdevType = :mdevType            ");
        if(arrChannel.size() > 0){
            sb.append("\n AND      month.id.channel IN (:channel)            ");
        }
        sb.append("\n ORDER BY month.id.yyyymm , month.id.channel");

        query = getSession().createQuery(sb.toString());
        query.setString("startDate", beginDate);
        query.setString("endDate", endDate);
        query.setInteger("meterId", meterId);
//      query.setInteger("mdevType", CommonConstants.DeviceType.Meter.getCode());
        query.setString("mdevType", CommonConstants.DeviceType.Meter.toString());
        
        if(arrChannel.size() > 0){
            query.setParameterList("channel", arrChannel);
        }
        

        }catch(Exception e){
            e.printStackTrace();
        }
        
        return query.list();
    }
    
    /*
     * 검침데이터 상세 최대/최소/평균/합계 일별 데이터
     */
    @Deprecated
    public List<Object> getDetailMonthMaxMinAvgSumData(HashMap<String, Object> condition) {
        
        Query query = null;
        try {
            
        String meterType   = (String)condition.get("meterType");
        String beginDate   = (String)condition.get("beginMonthDate");
        String endDate     = (String)condition.get("endMonthDate");
        Integer meterId    = (Integer)condition.get("meterId");
        List<Integer> arrChannel = ((List<Integer>)condition.get("arrChannel"));

//      logger.info("\n====conditions====\n"+condition);

        String momthTable = MeterType.valueOf(meterType).getMonthClassName();
                
        StringBuffer sb = new StringBuffer();
      
        sb.append("\n SELECT  month.id.channel, MIN(month.total) , MAX(month.total) , AVG(month.total) , SUM(month.total) " +
                " ");
        sb.append("\n FROM    ").append(momthTable).append(" month");
        sb.append("\n WHERE   month.meter.id = :meterId            ");
        sb.append("\n AND     month.id.yyyymm >= :startDate          ");
        sb.append("\n AND     month.id.yyyymm <= :endDate            ");
        sb.append("\n AND     month.id.mdevType = :mdevType            ");
        if(arrChannel.size() > 0){
            sb.append("\n AND      month.id.channel IN (:channel)            ");
        }
        sb.append("\n GROUP BY   month.id.channel            ");
        sb.append("\n ORDER BY   month.id.channel            ");

        query = getSession().createQuery(sb.toString());
        query.setString("startDate", beginDate);
        query.setString("endDate", endDate);
        query.setInteger("meterId", meterId);
        query.setString("mdevType", CommonConstants.DeviceType.Meter.name());
        
        if(arrChannel.size() > 0){
            query.setParameterList("channel", arrChannel);
        }
        

        }catch(Exception e){
            e.printStackTrace();
        }
        
        return query.list();
    }

    /**
     * Overlay Chart Data (Weekly, Monthly)
     * 
     * @param condition
     * @param contractId
     * @return
     */
    public List<Map<String, Object>> getOverlayChartMonthlyData(Map<String,Object> condition, Integer contractId) {
        Query query = null;
        String type = (String) condition.get("type");
        String meterType = ChangeMeterTypeName.valueOf(type).getCode();
        String beginDate = (String) condition.get("beginDate");
        String endDate = (String) condition.get("endDate");
        String beginMonth = beginDate.substring(0, 6);
        String endMonth = endDate.substring(0, 6);
        String date = null;
        Integer channel = new Integer((String)condition.get("channel"));
        String monthClass = MeterType.valueOf(meterType).getMonthClassName();

        StringBuilder sb = new StringBuilder();

        sb.append("\nSELECT mth.id.yyyymm AS yyyymm, ");

        for (int i = 1 ; i < 32 ; i++) {
            date = StringUtil.frontAppendNStr('0', Integer.toString(i), 2);
            sb.append("\n       COALESCE(mth.value_").append(date).append(", 0) AS value_").append(date).append(", ");
        }
        sb.append("\n       mth.contract.contractNumber AS contractNumber ");
        sb.append("\nFROM ").append(monthClass).append(" mth ");
        sb.append("\nWHERE mth.id.yyyymm BETWEEN :beginMonth AND :endMonth ");
        sb.append("\nAND   mth.id.channel = :channel ");
        sb.append("\nAND   mth.contract.id = :contractId ");

        query = getSession().createQuery(sb.toString());
        query.setString("beginMonth", beginMonth);
        query.setString("endMonth", endMonth);
        query.setInteger("channel", channel);
        query.setInteger("contractId", contractId);

        return query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
    }

    /**
     * method name : getDetailDailySearchData<b/>
     * method Desc : 검침데이터 상세 일별 데이터. Month Table 에서 일별 데이터를 추출한다.
     *
     * @param condition
     * @param isSum
     * @return
     */
    public List<Map<String, Object>> getDetailDailySearchData(Map<String, Object> condition, boolean isSum) {

        Query query = null;
        try {

            String meterType = (String) condition.get("meterType");
            String beginDate = (String) condition.get("beginMonthDate");
            String endDate = (String) condition.get("endMonthDate");
            Integer meterId = (Integer) condition.get("meterId");
            List<Integer> arrChannel = ((List<Integer>) condition.get("arrChannel"));

            String monthTable = MeterType.valueOf(meterType).getMonthClassName();

            StringBuilder sb = new StringBuilder();

            sb.append("\nSELECT mnt.id.yyyymm AS yyyymm, mnt.id.channel AS channel, ");
            sb.append("\n       mnt.value_01 AS value_01, ");
            sb.append("\n       mnt.value_02 AS value_02, ");
            sb.append("\n       mnt.value_03 AS value_03, ");
            sb.append("\n       mnt.value_04 AS value_04, ");
            sb.append("\n       mnt.value_05 AS value_05, ");
            sb.append("\n       mnt.value_06 AS value_06, ");
            sb.append("\n       mnt.value_07 AS value_07, ");
            sb.append("\n       mnt.value_08 AS value_08, ");
            sb.append("\n       mnt.value_09 AS value_09, ");
            sb.append("\n       mnt.value_10 AS value_10, ");
            sb.append("\n       mnt.value_11 AS value_11, ");
            sb.append("\n       mnt.value_12 AS value_12, ");
            sb.append("\n       mnt.value_13 AS value_13, ");
            sb.append("\n       mnt.value_14 AS value_14, ");
            sb.append("\n       mnt.value_15 AS value_15, ");
            sb.append("\n       mnt.value_16 AS value_16, ");
            sb.append("\n       mnt.value_17 AS value_17, ");
            sb.append("\n       mnt.value_18 AS value_18, ");
            sb.append("\n       mnt.value_19 AS value_19, ");
            sb.append("\n       mnt.value_20 AS value_20, ");
            sb.append("\n       mnt.value_21 AS value_21, ");
            sb.append("\n       mnt.value_22 AS value_22, ");
            sb.append("\n       mnt.value_23 AS value_23, ");
            sb.append("\n       mnt.value_24 AS value_24, ");
            sb.append("\n       mnt.value_25 AS value_25, ");
            sb.append("\n       mnt.value_26 AS value_26, ");
            sb.append("\n       mnt.value_27 AS value_27, ");
            sb.append("\n       mnt.value_28 AS value_28, ");
            sb.append("\n       mnt.value_29 AS value_29, ");
            sb.append("\n       mnt.value_30 AS value_30, ");
            sb.append("\n       mnt.value_31 AS value_31 ");
            sb.append("\nFROM ").append(monthTable).append(" mnt ");
            sb.append("\nWHERE mnt.meter.id = :meterId ");
            sb.append("\nAND   mnt.id.yyyymm BETWEEN :startMonth AND :endMonth ");
            sb.append("\nAND   mnt.id.mdevType = :mdevType ");

            if (arrChannel.size() > 0) {
                sb.append("\nAND   mnt.id.channel IN (:channel) ");
            }

            if (isSum) {
                sb.append("\nORDER BY mnt.id.channel, mnt.id.yyyymm ");
            } else {
                sb.append("\nORDER BY mnt.id.yyyymm, mnt.id.channel ");
            }

            query = getSession().createQuery(sb.toString());
            query.setString("startMonth", beginDate);
            query.setString("endMonth", endDate);
            query.setInteger("meterId", meterId);
            query.setString("mdevType", CommonConstants.DeviceType.Meter.toString());

            if (arrChannel.size() > 0) {
                query.setParameterList("channel", arrChannel);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
    }

    /**
     * method name : getDetailMonthlySearchData<b/>
     * method Desc : 검침데이터 상세 월별 데이터
     *
     * @param condition
     * @param isSum
     * @return
     */
    public List<Object> getDetailMonthlySearchData(Map<String, Object> condition, boolean isSum) {

        SQLQuery query = null;
        try {

            String meterType = (String)condition.get("meterType");
            String beginDate = (String)condition.get("beginMonthDate");
            String endDate = (String)condition.get("endMonthDate");
            String tlbType = (String)condition.get("tlbType");
            Integer meterId = (Integer) condition.get("meterId");
            List<Integer> arrChannel = ((List<Integer>) condition.get("arrChannel"));

            String monthTable = MeterType.valueOf(meterType).getMonthTableName();

            StringBuilder sb = new StringBuilder();

            if (isSum) {
                sb.append("\nSELECT CHANNEL AS CHANNEL, ");
                sb.append("\n       MIN(VALUE) AS MIN_VAL, ");
                sb.append("\n       MAX(VALUE) AS MAX_VAL, ");
                sb.append("\n       AVG(VALUE) AS AVG_VAL, ");
                sb.append("\n       SUM(VALUE) AS SUM_VAL ");
                sb.append("\nFROM ( ");
            }
            sb.append("\nSELECT yyyymm AS YYYYMM, ");
            sb.append("\n       channel AS CHANNEL, ");
            sb.append("\n       CASE WHEN ch_method = 'SUM' OR total = 0 THEN total ");
            sb.append("\n            WHEN value_cnt = 0 THEN NULL ");
            sb.append("\n            ELSE (total / value_cnt) END AS VALUE ");
            sb.append("\nFROM ( ");
            sb.append("\n    SELECT mo.yyyymm, mo.channel, ");
            sb.append("\n           mo.total, ");
            sb.append("\n           CASE WHEN mo.value_01 IS NOT NULL THEN 1 ELSE 0 END ");
            sb.append("\n           + CASE WHEN mo.value_02 IS NOT NULL THEN 1 ELSE 0 END ");
            sb.append("\n           + CASE WHEN mo.value_03 IS NOT NULL THEN 1 ELSE 0 END ");
            sb.append("\n           + CASE WHEN mo.value_04 IS NOT NULL THEN 1 ELSE 0 END ");
            sb.append("\n           + CASE WHEN mo.value_05 IS NOT NULL THEN 1 ELSE 0 END ");
            sb.append("\n           + CASE WHEN mo.value_06 IS NOT NULL THEN 1 ELSE 0 END ");
            sb.append("\n           + CASE WHEN mo.value_07 IS NOT NULL THEN 1 ELSE 0 END ");
            sb.append("\n           + CASE WHEN mo.value_08 IS NOT NULL THEN 1 ELSE 0 END ");
            sb.append("\n           + CASE WHEN mo.value_09 IS NOT NULL THEN 1 ELSE 0 END ");
            sb.append("\n           + CASE WHEN mo.value_10 IS NOT NULL THEN 1 ELSE 0 END ");
            sb.append("\n           + CASE WHEN mo.value_11 IS NOT NULL THEN 1 ELSE 0 END ");
            sb.append("\n           + CASE WHEN mo.value_12 IS NOT NULL THEN 1 ELSE 0 END ");
            sb.append("\n           + CASE WHEN mo.value_13 IS NOT NULL THEN 1 ELSE 0 END ");
            sb.append("\n           + CASE WHEN mo.value_14 IS NOT NULL THEN 1 ELSE 0 END ");
            sb.append("\n           + CASE WHEN mo.value_15 IS NOT NULL THEN 1 ELSE 0 END ");
            sb.append("\n           + CASE WHEN mo.value_16 IS NOT NULL THEN 1 ELSE 0 END ");
            sb.append("\n           + CASE WHEN mo.value_17 IS NOT NULL THEN 1 ELSE 0 END ");
            sb.append("\n           + CASE WHEN mo.value_18 IS NOT NULL THEN 1 ELSE 0 END ");
            sb.append("\n           + CASE WHEN mo.value_19 IS NOT NULL THEN 1 ELSE 0 END ");
            sb.append("\n           + CASE WHEN mo.value_20 IS NOT NULL THEN 1 ELSE 0 END ");
            sb.append("\n           + CASE WHEN mo.value_21 IS NOT NULL THEN 1 ELSE 0 END ");
            sb.append("\n           + CASE WHEN mo.value_22 IS NOT NULL THEN 1 ELSE 0 END ");
            sb.append("\n           + CASE WHEN mo.value_23 IS NOT NULL THEN 1 ELSE 0 END ");
            sb.append("\n           + CASE WHEN mo.value_24 IS NOT NULL THEN 1 ELSE 0 END ");
            sb.append("\n           + CASE WHEN mo.value_25 IS NOT NULL THEN 1 ELSE 0 END ");
            sb.append("\n           + CASE WHEN mo.value_26 IS NOT NULL THEN 1 ELSE 0 END ");
            sb.append("\n           + CASE WHEN mo.value_27 IS NOT NULL THEN 1 ELSE 0 END ");
            sb.append("\n           + CASE WHEN mo.value_28 IS NOT NULL THEN 1 ELSE 0 END ");
            sb.append("\n           + CASE WHEN mo.value_29 IS NOT NULL THEN 1 ELSE 0 END ");
            sb.append("\n           + CASE WHEN mo.value_30 IS NOT NULL THEN 1 ELSE 0 END ");
            sb.append("\n           + CASE WHEN mo.value_31 IS NOT NULL THEN 1 ELSE 0 END ");
            sb.append("\n           AS value_cnt, ");
            sb.append("\n           CASE WHEN ch.ch_method IS NULL THEN 'SUM' ELSE ch.ch_method END AS ch_method ");
            sb.append("\n    FROM ").append(monthTable).append(" mo ");
            sb.append("\n         LEFT OUTER JOIN ");
            sb.append("\n         (SELECT DISTINCT cc.channel_Index, ");
            sb.append("\n                 dc.ch_method ");
            sb.append("\n          FROM meter mt, ");
            sb.append("\n               meterconfig mc, ");
            sb.append("\n               display_channel dc, ");
            sb.append("\n               channel_config cc ");
            sb.append("\n          WHERE mt.id = :meterId ");
            sb.append("\n          AND   mc.devicemodel_fk = mt.devicemodel_id ");
            sb.append("\n          AND   cc.meterconfig_id = mc.id ");
            sb.append("\n          AND   cc.data_type = :tlbType ");
            sb.append("\n          AND   dc.id = cc.channel_id) ch ");
            sb.append("\n         on mo.channel = ch.channel_index ");
            sb.append("\n    WHERE mo.meter_id = :meterId ");
            sb.append("\n    AND   mo.yyyymm BETWEEN :startMonth AND :endMonth ");
            sb.append("\n    AND   mo.mdev_type = :mdevType ");
            if (arrChannel.size() > 0) {
                sb.append("\n    AND   mo.channel IN (:channel) ");
            }

            sb.append("\n ) x ");
            
            if (isSum) {
                sb.append("\n ) y ");
                sb.append("\nGROUP BY CHANNEL ");
            } else {
                sb.append("\nORDER BY yyyymm, channel ");
            }

            query = getSession().createSQLQuery(sb.toString());
            query.setString("startMonth", beginDate);
            query.setString("endMonth", endDate);
            query.setString("tlbType", tlbType);
            query.setInteger("meterId", meterId);
            query.setString("mdevType", CommonConstants.DeviceType.Meter.toString());

            if (arrChannel.size() > 0) {
                query.setParameterList("channel", arrChannel);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return query.list();
    }

    /**
     * method name : getMeteringDataMonthlyData<b/>
     * method Desc : Metering Data 맥스가젯에서 월별 검침데이터를 조회한다.
     *
     * @param conditionMap
     * @param isTotal
     * @return
     */
    @SuppressWarnings("deprecation")
	public List<Map<String, Object>> getMeteringDataMonthlyData(Map<String, Object> conditionMap, boolean isTotal) {
        List<Map<String, Object>> result;

        Integer supplierId = (Integer)conditionMap.get("supplierId");
        Integer tariffType = (Integer)conditionMap.get("tariffType");
        Integer sicId = (Integer)conditionMap.get("sicId");    // 임시
        Integer page = (Integer)conditionMap.get("page");
        Integer limit = (Integer)conditionMap.get("limit");
        String contractNumber = StringUtil.nullToBlank(conditionMap.get("contractNumber"));
        String customerName = StringUtil.nullToBlank(conditionMap.get("customerName"));
        String customerNumber = StringUtil.nullToBlank(conditionMap.get("customerNumber"));
        String meteringSF = StringUtil.nullToBlank(conditionMap.get("meteringSF"));
        String friendlyName = StringUtil.nullToBlank(conditionMap.get("friendlyName"));
        Integer isManualMeter = (Integer) conditionMap.get("isManualMeter");
        String startDate = StringUtil.nullToBlank(conditionMap.get("startDate"));
        String endDate = StringUtil.nullToBlank(conditionMap.get("endDate"));
        String prevStartDate = StringUtil.nullToBlank(conditionMap.get("prevStartDate"));
        String prevEndDate = StringUtil.nullToBlank(conditionMap.get("prevEndDate"));
        String mcuId = StringUtil.nullToBlank(conditionMap.get("mcuId"));
        String modemId = StringUtil.nullToBlank(conditionMap.get("modemId"));
        String deviceType = StringUtil.nullToBlank(conditionMap.get("deviceType"));
        String mdevId = StringUtil.nullToBlank(conditionMap.get("mdevId"));
        String gs1 = StringUtil.nullToBlank(conditionMap.get("gs1"));
        String contractGroup = StringUtil.nullToBlank(conditionMap.get("contractGroup"));
        String meterType = StringUtil.nullToBlank(conditionMap.get("meterType"));
        List<Integer> sicIdList = (List<Integer>)conditionMap.get("sicIdList");
        List<Integer> locationIdList = (List<Integer>)conditionMap.get("locationIdList");
        
//        String monthView = MeterType.valueOf(meterType).getMonthViewName();
        String monthTable = MeterType.valueOf(meterType).getMonthTableName();

        StringBuilder sb = new StringBuilder();

        if (isTotal) {
            sb.append("\nSELECT COUNT(SUM(mv.value)) ");
        } else {
            sb.append("\nSELECT mv.yyyymm AS YYYYMM, 						");
            sb.append("\n       mv.mdev_id AS METER_NO, 					");
            sb.append("\n       MAX(co.contract_number) AS CONTRACT_NUMBER, ");
            sb.append("\n       MAX(cu.NAME) AS CUSTOMER_NAME, 				");
            sb.append("\n       MAX(cu.customerNo) AS CUSTOMER_NUMBER, 		");
            sb.append("\n       MAX(mo.device_serial) AS MODEM_ID, 			");
            sb.append("\n       MAX(code.name) AS SIC_NAME, 				");
            sb.append("\n       MAX(me.last_metering_value) AS LAST_METERING_VALUE, ");
            sb.append("\n       MAX(mv.value) AS VALUE_MAX,					");
//            sb.append("\n       SUM(mv.value) AS VALUE_SUM,");
            sb.append("\n       MAX(me.gs1) AS GS1,");
            sb.append("\n       MAX(mv.ch_method) AS CH_METHOD, 			");	
            sb.append("\n       MAX(pre.value) AS PRE_VALUE 				");
        }
        sb.append("\nFROM ").append(monthTable).append(" mv ");
        sb.append("\nLEFT OUTER JOIN meter me ON mv.mdev_id = me.mds_id ");
        sb.append("\nLEFT OUTER JOIN modem mo ON mv.modem_id = mo.id ");
        sb.append("\nLEFT OUTER JOIN mcu mc ON mv.device_id = mc.sys_id ");
        sb.append("\nLEFT OUTER JOIN contract co ON mv.contract_id = co.id ");
        sb.append("\nLEFT OUTER JOIN customer cu ON co.customer_id = cu.id ");
        sb.append("\nLEFT OUTER JOIN code code ON co.sic_id = code.id ");
        sb.append("\nLEFT OUTER JOIN group_member gm ON gm.member = co.contract_number ");
		 /* SUM/MAX값에 따라 분기 (OPF-2583) */
        
        sb.append("\nLEFT OUTER JOIN ( 											");
        sb.append("\n    SELECT mv2.value AS VALUE,								");
        sb.append("\n           mv2.mdev_id										");
        sb.append("\n    FROM ").append(monthTable).append(" mv2 ");
        sb.append("\n    WHERE mv2.yyyymm = :prevStartDate						");
        sb.append("\n    and mv2.channel = 1 									");
        sb.append("\n) pre ON mv.mdev_id = pre.mdev_id 							");
        
        sb.append("\nWHERE mv.yyyymm BETWEEN :startDate AND :endDate ");
        sb.append("\nAND   mv.channel = 1 ");
        sb.append("\nAND   me.supplier_id = :supplierId ");

        if (!deviceType.isEmpty()) {
            sb.append("\nAND   mv.mdev_type = :deviceType ");
        }
        if (meteringSF.equals("s")) {
            sb.append("\nAND   mv.value IS NOT NULL ");
        } else {
            sb.append("\nAND   mv.value IS NULL ");
        }
        if (!customerName.isEmpty()) {
        	if(customerName.indexOf('%') == 0 || customerName.indexOf('%') == (customerName.length()-1)) { // %문자가 양 끝에 있을경우
                sb.append("\nAND   UPPER(cu.name) LIKE UPPER(:customerName) ");
        	}else {
                sb.append("\nAND   UPPER(cu.name) = UPPER(:customerName) ");
        	}
        }
        if (!customerNumber.isEmpty()) {
        	sb.append("\nAND   cu.customerNo LIKE :customerNumber ");
        }
        if (!mcuId.isEmpty()) {
        	if(mcuId.indexOf('%') == 0 || mcuId.indexOf('%') == (mcuId.length()-1)) { // %문자가 양 끝에 있을경우
            	sb.append("\nAND   mc.sys_id LIKE :mcuId ");
        	}else {
            	sb.append("\nAND   mc.sys_id = :mcuId ");
        	}
        }
        if(!modemId.isEmpty()){
        	if(modemId.indexOf('%') == 0 || modemId.indexOf('%') == (modemId.length()-1)) { // %문자가 양 끝에 있을경우
                sb.append("\nAND   mo.device_serial LIKE :modemId ");
        	}else {
                sb.append("\nAND   mo.device_serial = :modemId ");
        	}
        }
        if (!mdevId.isEmpty()) {
        	if(mdevId.indexOf('%') == 0 || mdevId.indexOf('%') == (mdevId.length()-1)) { // %문자가 양 끝에 있을경우
                sb.append("\nAND   mv.mdev_id LIKE :mdevId ");
        	}else {
                sb.append("\nAND   mv.mdev_id = :mdevId ");
        	}
        }
        if (!gs1.isEmpty()) {
        	if(gs1.indexOf('%') == 0 || gs1.indexOf('%') == (gs1.length()-1)) { // %문자가 양 끝에 있을경우
                sb.append("\nAND   me.gs1 LIKE :gs1 ");
        	}else {
                sb.append("\nAND   me.gs1 = :gs1 ");
        	}
        }
        if (!contractNumber.isEmpty()) {
        	if(contractNumber.indexOf('%') == 0 || contractNumber.indexOf('%') == (contractNumber.length()-1)) { // %문자가 양 끝에 있을경우
                sb.append("\nAND   co.contract_number LIKE :contractNumber ");
        	}else {
                sb.append("\nAND   co.contract_number = :contractNumber ");
        	}
        }

        if (!friendlyName.isEmpty()) {
            sb.append("\nAND   mv.friendly_name = :friendlyName ");
        }
        if (isManualMeter != null) {
            sb.append("\nAND   mv.is_manual_meter = :isManualMeter ");
        }
        if (sicIdList != null) {
            sb.append("\nAND   co.sic_id IN (:sicIdList) ");
        }
        if (sicId != null) {
            sb.append("\nAND   co.sic_id = :sicId ");
        }
        if (tariffType != null) {
            sb.append("\nAND   co.tariffindex_id = :tariffType ");
        }
        if (locationIdList != null) {
            sb.append("\nAND   mt.location_id IN (:locationIdList) ");
        }
        if (!contractGroup.isEmpty()) {
            sb.append("\nAND   gm.group_id = :contractGroup ");
        }
        
        sb.append("\nGROUP BY mv.YYYYMM, mv.mdev_id ");
        sb.append("\nORDER BY mv.yyyymm, mv.mdev_id ");
        
        SQLQuery query = getSession().createSQLQuery(new SQLWrapper().getQuery(sb.toString()));

        query.setString("startDate", startDate);
        query.setString("endDate", endDate);
        query.setString("prevStartDate", prevStartDate);
//        query.setString("prevEndDate", prevEndDate);
        query.setInteger("supplierId", supplierId);

        if (!deviceType.isEmpty()) {
            query.setString("deviceType", deviceType);
        }

        if (!mdevId.isEmpty()) {
            query.setString("mdevId", mdevId);
        }

        if (!gs1.isEmpty()) {
            query.setString("gs1", gs1);
        }
        // XXX: 수검침 조건
        if (!friendlyName.isEmpty()) {
            query.setString("friendlyName", friendlyName);
        }
        if (isManualMeter != null) {
            query.setInteger("isManualMeter", isManualMeter);
        }

        if (!contractNumber.isEmpty()) {
            query.setString("contractNumber", contractNumber);
        }

        if (sicIdList != null) {
            query.setParameterList("sicIdList", sicIdList);
        }

        if (sicId != null) {
            query.setInteger("sicId", sicId);
        }

        if (tariffType != null) {
            query.setInteger("tariffType", tariffType);
        }

        if (locationIdList != null) {
            query.setParameterList("locationIdList", locationIdList);
        }

        if (!contractGroup.isEmpty()) {
            query.setString("contractGroup", contractGroup);
        }

        if (!customerName.isEmpty()) {
            query.setString("customerName", customerName);
        }

        if (!customerNumber.isEmpty()) {
            query.setString("customerNumber", customerNumber+"%");
        }
        
        if (!mcuId.isEmpty()) {
            query.setString("mcuId", mcuId);
        }

        if(!modemId.isEmpty()){
        	query.setString("modemId", modemId);
        }
        
        if (isTotal) {
            Map<String, Object> map = new HashMap<String, Object>();
            int count = 0;
            count = ((Number)query.uniqueResult()).intValue();
            map.put("total", count);
            result = new ArrayList<Map<String, Object>>();
            result.add(map);
        } else {
            if (page != null && limit != null) {
                query.setFirstResult((page - 1) * limit);
                query.setMaxResults(limit);
            }
            result = query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
        }
        return result;
    }
    
    /**
     * method name : getMeteringDataMonthlyChannel2Data<b/>
     * method Desc : Metering Data 맥스가젯에서 채널2번 누적유효사용량을 조회한다. : 대성에너지 
     *
     * @param conditionMap
     * @param isTotal
     * @param isPrev
     * @return
     */
    @SuppressWarnings("unused")
    public List<Map<String, Object>> getMeteringDataMonthlyChannel2Data(Map<String, Object> conditionMap) {
        List<Map<String, Object>> result;

        Integer supplierId = (Integer)conditionMap.get("supplierId");
        Integer tariffType = (Integer)conditionMap.get("tariffType");
        Integer sicId = (Integer)conditionMap.get("sicId");    // 임시
        DateType dateType = (DateType) conditionMap.get("dateType");

        String contractNumber = StringUtil.nullToBlank(conditionMap.get("contractNumber"));
        String customerName = StringUtil.nullToBlank(conditionMap.get("customerName"));
        String meteringSF = StringUtil.nullToBlank(conditionMap.get("meteringSF"));

        String friendlyName = StringUtil.nullToBlank(conditionMap.get("friendlyName"));
        Integer isManualMeter = (Integer) conditionMap.get("isManualMeter");
        
        String startDate = null;
        String endDate = null;
        
        Integer startDetailDate = null;
        Integer endDetailDate = null;
        
        String mcuId = StringUtil.nullToBlank(conditionMap.get("mcuId"));
        String deviceType = StringUtil.nullToBlank(conditionMap.get("deviceType"));
        String mdevId = StringUtil.nullToBlank(conditionMap.get("mdevId"));
        String contractGroup = StringUtil.nullToBlank(conditionMap.get("contractGroup"));
        String meterType = StringUtil.nullToBlank(conditionMap.get("meterType"));
        String tlbType = StringUtil.nullToBlank(conditionMap.get("tlbType"));
        List<Integer> sicIdList = (List<Integer>)conditionMap.get("sicIdList");
        List<Integer> locationIdList = (List<Integer>)conditionMap.get("locationIdList");
        String MonthTable = MeterType.valueOf(meterType).getMonthTableName();

        startDate = StringUtil.nullToBlank(conditionMap.get("startDate"));
        endDate = StringUtil.nullToBlank(conditionMap.get("endDate"));
        startDetailDate = Integer.parseInt(StringUtil.nullToBlank(conditionMap.get("startDetailDate")));
        endDetailDate = Integer.parseInt(StringUtil.nullToBlank(conditionMap.get("endDetailDate")));

        StringBuilder sb = new StringBuilder();

        sb.append("\nSELECT mds_id AS METER_NO, ");
        sb.append("\n       yyyymm AS YYYYMM, ");
        sb.append("\n       accumulateValue AS ACCUMULATEVALUE ");
        sb.append("\nFROM ( ");
        sb.append("\n    SELECT mn.yyyymm, ");
        sb.append("\n           mt.mds_id, ");
        
        for (int i = endDetailDate; i >= startDetailDate; i--) {
            if(i == endDetailDate) {
                sb.append("COALESCE(mn.value_").append(StringUtil.frontAppendNStr('0', new Integer(i).toString(), 2));
            } else {
                sb.append(", mn.value_").append(StringUtil.frontAppendNStr('0', new Integer(i).toString(), 2));
            }
        }
        sb.append(", mn.value_").append(StringUtil.frontAppendNStr('0', new Integer(startDetailDate).toString(), 2)).append(", 0)");
        sb.append(" AS accumulateValue, ");
        
        sb.append("\n           (SELECT DISTINCT dc.ch_method ");
        sb.append("\n            FROM meter me, ");
        sb.append("\n                 meterconfig mf, ");
        sb.append("\n                 display_channel dc, ");
        sb.append("\n                 channel_config  cc ");
        sb.append("\n            WHERE 1=1 ");
        sb.append("\n            AND   mf.devicemodel_fk = me.devicemodel_id ");
        sb.append("\n            AND   cc.meterconfig_id = mf.id ");
        sb.append("\n            AND   cc.data_type = :tlbType ");
        sb.append("\n            AND   dc.id = cc.channel_id ");
        sb.append("\n            and   me.id = mn.meter_id ");
        sb.append("\n            AND   cc.channel_index = 1) AS ch_method ");
        sb.append("\n    FROM ").append(MonthTable).append(" mn ");
        sb.append("\n         LEFT OUTER JOIN ");
        sb.append("\n         contract co ");
        sb.append("\n         ON co.id = mn.contract_id ");
        sb.append("\n         LEFT OUTER JOIN ");
        sb.append("\n         customer cu ");
        sb.append("\n         ON cu.id = co.customer_id, ");

        if (!contractGroup.isEmpty()) {
            sb.append("\n         group_member gm, ");
        }

        sb.append("\n         meter mt ");

        if (!mcuId.isEmpty()) {
            sb.append("\n         ,modem mo ");
            sb.append("\n         ,mcu mc ");
        }

        sb.append("\n    WHERE mn.yyyymm BETWEEN :startDate AND :endDate ");

        sb.append("\n    AND   mn.channel = 2 ");
        sb.append("\n    AND   mt.id = mn.meter_id ");
        sb.append("\n    AND   mt.supplier_id = :supplierId ");
        
        if (!deviceType.isEmpty()) {
            sb.append("\n    AND   mn.mdev_type = :deviceType ");
        }

        if (meteringSF.equals("s")) {
            sb.append("\n    AND   mn.value IS NOT NULL ");
        } else {
            sb.append("\n    AND   mn.value IS NULL ");
        }

        if (!mdevId.isEmpty()) {
            sb.append("\n    AND   mt.mds_id LIKE :mdevId ");
        }

        // XXX: 수검침 조건
        if (!friendlyName.isEmpty()) {
            sb.append("\n    AND   mt.friendly_name = :friendlyName ");
        }
        if (isManualMeter != null) {
            sb.append("\n    AND   mt.is_manual_meter = :isManualMeter ");
        }

        if (!contractNumber.isEmpty()) {
            sb.append("\n    AND   co.contract_number LIKE :contractNumber ");
        }

        if (sicIdList != null) {
            sb.append("\n    AND   co.sic_id IN (:sicIdList) ");
        }

        if (sicId != null) {
            sb.append("\n    AND   co.sic_id = :sicId ");
        }

        if (tariffType != null) {
            sb.append("\n    AND   co.tariffindex_id = :tariffType ");
        }

        if (locationIdList != null) {
            sb.append("\n    AND   mt.location_id IN (:locationIdList) ");
        }

        if (!contractGroup.isEmpty()) {
            sb.append("\n    AND   gm.member = co.contract_number ");
            sb.append("\n    AND   gm.group_id = :contractGroup ");
        }

        if (!customerName.isEmpty()) {
            sb.append("\n    AND   UPPER(cu.name) LIKE UPPER(:customerName) ");
        }

        if (!mcuId.isEmpty()) {
            sb.append("\n    AND   mo.id = mt.modem_id ");
            sb.append("\n    AND   mc.id = mo.mcu_id ");
            sb.append("\n    AND   mc.sys_id = :mcuId ");
        }
       
        sb.append("\n) x ");
        // sb.append("\nORDER BY yyyymm, mds_id ");
        

        SQLQuery query = getSession().createSQLQuery(new SQLWrapper().getQuery(sb.toString()));

        query.setString("tlbType", tlbType);
        query.setString("startDate", startDate);
        query.setString("endDate", endDate);
        query.setInteger("supplierId", supplierId);

        if (!deviceType.isEmpty()) {
            query.setString("deviceType", deviceType);
        }

        if (!mdevId.isEmpty()) {
            query.setString("mdevId", "%" + mdevId + "%");
        }

        // XXX: 수검침 조건
        if (!friendlyName.isEmpty()) {
            query.setString("friendlyName", friendlyName);
        }
        if (isManualMeter != null) {
            query.setInteger("isManualMeter", isManualMeter);
        }

        if (!contractNumber.isEmpty()) {
            query.setString("contractNumber", "%" + contractNumber + "%");
        }

        if (sicIdList != null) {
            query.setParameterList("sicIdList", sicIdList);
        }

        if (sicId != null) {
            query.setInteger("sicId", sicId);
        }

        if (tariffType != null) {
            query.setInteger("tariffType", tariffType);
        }

        if (locationIdList != null) {
            query.setParameterList("locationIdList", locationIdList);
        }

        if (!contractGroup.isEmpty()) {
            query.setString("contractGroup", contractGroup);
        }

        if (!customerName.isEmpty()) {
            query.setString("customerName", "%" + customerName + "%");
        }

        if (!mcuId.isEmpty()) {
            query.setString("mcuId", mcuId);
        }

        result = query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();

        return result;
    }

    /**
     * method name : getMeteringDataYearlyData<b/>
     * method Desc : Metering Data 맥스가젯에서 연간 검침데이터를 조회한다.
     *
     * @param conditionMap
     * @param isTotal
     * @return
     */
    public List<Map<String, Object>> getMeteringDataYearlyData(Map<String, Object> conditionMap, boolean isTotal) {
        return getMeteringDataYearlyData(conditionMap, isTotal, false);
    }

    /**
     * method name : getMeteringDataYearlyData<b/>
     * method Desc : Metering Data 맥스가젯에서 연간 검침데이터를 조회한다.
     *
     * @param conditionMap
     * @param isTotal
     * @param isPrev
     * @return
     */
    @SuppressWarnings("deprecation")
	public List<Map<String, Object>> getMeteringDataYearlyData(Map<String, Object> conditionMap, boolean isTotal, boolean isPrev) {
        List<Map<String, Object>> result;

        Integer supplierId = (Integer)conditionMap.get("supplierId");
        Integer tariffType = (Integer)conditionMap.get("tariffType");
        Integer sicId = (Integer)conditionMap.get("sicId");
        Integer page = (Integer)conditionMap.get("page");
        Integer limit = (Integer)conditionMap.get("limit");

        String contractNumber = StringUtil.nullToBlank(conditionMap.get("contractNumber"));
        String customerName = StringUtil.nullToBlank(conditionMap.get("customerName"));
        String customerNumber = StringUtil.nullToBlank(conditionMap.get("customerNumber"));
        String meteringSF = StringUtil.nullToBlank(conditionMap.get("meteringSF"));

        String startDate = StringUtil.nullToBlank(conditionMap.get("startDate"));
        String endDate = StringUtil.nullToBlank(conditionMap.get("endDate"));
        String prevStartDate = StringUtil.nullToBlank(conditionMap.get("prevStartDate"));
        String prevEndDate = StringUtil.nullToBlank(conditionMap.get("prevEndDate"));

        String mcuId = StringUtil.nullToBlank(conditionMap.get("mcuId"));
        String modemId = StringUtil.nullToBlank(conditionMap.get("modemId"));
        String deviceType = StringUtil.nullToBlank(conditionMap.get("deviceType"));
        String mdevId = StringUtil.nullToBlank(conditionMap.get("mdevId"));
        String gs1 = StringUtil.nullToBlank(conditionMap.get("gs1"));
        String contractGroup = StringUtil.nullToBlank(conditionMap.get("contractGroup"));
        String meterType = StringUtil.nullToBlank(conditionMap.get("meterType"));
        List<Integer> sicIdList = (List<Integer>)conditionMap.get("sicIdList");
        List<Integer> locationIdList = (List<Integer>)conditionMap.get("locationIdList");
//        Set<String> meterNoList = (Set<String>)conditionMap.get("meterNoList");
        String monthView = MeterType.valueOf(meterType).getMonthViewName();
        String monthTable = MeterType.valueOf(meterType).getMonthTableName();

        StringBuilder sb = new StringBuilder();
        
        if (isTotal) {
            sb.append("\nSELECT COUNT(SUM(NVL(mv.value,0))) ");
        } else {
            sb.append("\nSELECT co.contract_number AS CONTRACT_NUMBER,	");
            sb.append("\n       MAX(cu.name) AS CUSTOMER_NAME, 			");
            sb.append("\n       MAX(cu.customerNo) AS CUSTOMER_NUMBER,	");
            sb.append("\n       mv.mdev_id AS METER_NO, 			");
            sb.append("\n       MAX(mo.device_serial) AS MODEM_ID, 	");
            sb.append("\n       MAX(code.name) AS SIC_NAME, 		");
            sb.append("\n       mt.gs1 AS gs1, 						");
            sb.append("\n       MAX(mv.value) AS VALUE_MAX, 		");
            sb.append("\n       SUM(mv.value) AS VALUE_SUM, 		");
            sb.append("\n       MAX(mv.ch_method) AS CH_METHOD, 	");	
            sb.append("\n       MAX(pre.value) AS PRE_VALUE 		");
        }
        sb.append("\nFROM ").append(monthTable).append(" mv 					");
        sb.append("\nLEFT OUTER JOIN meter mt ON mt.mds_id = mv.mdev_id 	");
        sb.append("\nLEFT OUTER JOIN modem mo ON mo.id = mv.modem_id 		");
        sb.append("\nLEFT OUTER JOIN mcu mc ON mc.sys_id = mv.device_id 	");
        sb.append("\nLEFT OUTER JOIN contract co ON co.id = mv.contract_id 	");
        sb.append("\nLEFT OUTER JOIN customer cu ON cu.id = co.customer_id 	");
        sb.append("\nLEFT OUTER JOIN code code ON co.sic_id = code.id 		");
        sb.append("\nLEFT OUTER JOIN group_member gm ON gm.member = co.contract_number ");
        
        sb.append("\nLEFT OUTER JOIN ( 											");
        sb.append("\n    SELECT MAX(NVL(mv2.value,0)) AS value,					");
        sb.append("\n           mv2.mdev_id 									");
        sb.append("\n    FROM ").append(monthTable).append(" mv2					");
        sb.append("\n    WHERE mv2.yyyymm = :prevStartDate 						");
        sb.append("\n    AND mv2.channel = 1 									"); 
        sb.append("\n    GROUP BY mv2.mdev_id 									"); 
        sb.append("\n) pre ON mv.mdev_id = pre.mdev_id 							");
        
        sb.append("\nWHERE mv.yyyymm BETWEEN :startDate AND :endDate ");
        sb.append("\nAND   mv.channel = 1 				");
        sb.append("\nAND   mt.supplier_id = :supplierId ");

        if (!deviceType.isEmpty()) {
            sb.append("\nAND   mv.mdev_type = :deviceType ");
        }
        if (meteringSF.equals("s")) {
            sb.append("\nAND   mv.value IS NOT NULL ");
        } else {
            sb.append("\nAND   mv.value IS NULL ");
        }
        if (!mdevId.isEmpty()) {
        	if(mdevId.indexOf('%') == 0 || mdevId.indexOf('%') == (mdevId.length()-1)) { // %문자가 양 끝에 있을경우
                sb.append("\nAND   mt.mds_id LIKE :mdevId ");
        	}else {
                sb.append("\nAND   mt.mds_id = :mdevId ");
        	}
        }
        if (!gs1.isEmpty()) {
        	if(gs1.indexOf('%') == 0 || gs1.indexOf('%') == (gs1.length()-1)) { // %문자가 양 끝에 있을경우
                sb.append("\nAND   mt.gs1 LIKE :gs1 ");
        	}else {
                sb.append("\nAND   mt.gs1 = :gs1 ");
        	}
        }
        if (!contractNumber.isEmpty()) {
        	if(contractNumber.indexOf('%') == 0 || contractNumber.indexOf('%') == (contractNumber.length()-1)) { // %문자가 양 끝에 있을경우
                sb.append("\nAND   co.contract_number LIKE :contractNumber ");
        	}else {
                sb.append("\nAND   co.contract_number = :contractNumber ");
        	}
        }
        if (!customerName.isEmpty()) {
        	if(customerName.indexOf('%') == 0 || customerName.indexOf('%') == (customerName.length()-1)) { // %문자가 양 끝에 있을경우
                sb.append("\nAND   UPPER(cu.name) LIKE UPPER(:customerName) ");
        	}else {
                sb.append("\nAND   UPPER(cu.name) = UPPER(:customerName) ");
        	}
        }
        if (!customerNumber.isEmpty()) {
        	sb.append("\nAND   cu.customerNo LIKE :customerNumber ");
        }
        if (!mcuId.isEmpty()) {
        	if(mcuId.indexOf('%') == 0 || mcuId.indexOf('%') == (mcuId.length()-1)) { // %문자가 양 끝에 있을경우
            	sb.append("\nAND   mc.sys_id LIKE :mcuId ");
        	}else {
            	sb.append("\nAND   mc.sys_id = :mcuId ");
        	}
        }
        if(!modemId.isEmpty()){
        	if(modemId.indexOf('%') == 0 || modemId.indexOf('%') == (modemId.length()-1)) { // %문자가 양 끝에 있을경우
                sb.append("\nAND   mo.device_serial LIKE :modemId ");
        	}else {
                sb.append("\nAND   mo.device_serial = :modemId ");
        	}
        }
        if (sicIdList != null) {
            sb.append("\nAND   co.sic_id IN (:sicIdList) ");
        }
        if (sicId != null) {
            sb.append("\nAND   co.sic_id = :sicId ");
        }
        if (tariffType != null) {
            sb.append("\nAND   co.tariffindex_id = :tariffType ");
        }
        if (locationIdList != null) {
            sb.append("\nAND   mt.location_id IN (:locationIdList) ");
        }
        if (!contractGroup.isEmpty()) {
            sb.append("\nAND   gm.group_id = :contractGroup ");
        }
        sb.append("\nGROUP BY co.contract_number, mv.mdev_id, mt.gs1 ");
//        sb.append("\nGROUP BY co.contract_number, cu.name, mv.mdev_id, code.name, mt.gs1 ");
        sb.append("\nORDER BY mv.mdev_id ");

        SQLQuery query = getSession().createSQLQuery(new SQLWrapper().getQuery(sb.toString()));

        query.setString("startDate", startDate);
        query.setString("endDate", endDate);
        query.setString("prevStartDate", prevStartDate);
//        query.setString("prevEndDate", prevEndDate);
        query.setInteger("supplierId", supplierId);
        
        if (!deviceType.isEmpty()) {
            query.setString("deviceType", deviceType);
        }
        if (!mdevId.isEmpty()) {
            query.setString("mdevId", mdevId);
        }
        if (!gs1.isEmpty()) {
            query.setString("gs1", gs1);
        }
        if (!contractNumber.isEmpty()) {
            query.setString("contractNumber", contractNumber);
        }
        if (sicIdList != null) {
            query.setParameterList("sicIdList", sicIdList);
        }
        if (sicId != null) {
            query.setInteger("sicId", sicId);
        }
        if (tariffType != null) {
            query.setInteger("tariffType", tariffType);
        }
        if (locationIdList != null) {
            query.setParameterList("locationIdList", locationIdList);
        }
        if (!contractGroup.isEmpty()) {
            query.setString("contractGroup", contractGroup);
        }
        if (!customerName.isEmpty()) {
            query.setString("customerName", customerName);
        }
        if (!customerNumber.isEmpty()) {
            query.setString("customerNumber", customerNumber+"%");
        }
        if (!mcuId.isEmpty()) {
            query.setString("mcuId", mcuId);
        }
        if(!modemId.isEmpty()){
        	query.setString("modemId", modemId);
        }
        
        if (isTotal) {
            Map<String, Object> map = new HashMap<String, Object>();
            int count = 0;
            count = ((Number)query.uniqueResult()).intValue();
            map.put("total", count);
            result = new ArrayList<Map<String, Object>>();
            result.add(map);
        } else {
            if (!isPrev && page != null && limit != null) {
                query.setFirstResult((page - 1) * limit);
                query.setMaxResults(limit);
            }
            result = query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
        }

        return result;
    }

    /**
     * method name : getMeteringDataDetailMonthlyData<b/>
     * method Desc : Metering Data 맥스가젯 상세화면에서 월별 검침데이터를 조회한다.
     *
     * @param conditionMap
     * @return
     */
    @SuppressWarnings("deprecation")
	public List<Map<String, Object>> getMeteringDataDetailMonthlyData(Map<String, Object> conditionMap, boolean isSum) {
        List<Map<String, Object>> result;

        Integer supplierId = (Integer)conditionMap.get("supplierId");
        String searchStartDate = StringUtil.nullToBlank(conditionMap.get("searchStartDate"));
        String searchEndDate = StringUtil.nullToBlank(conditionMap.get("searchEndDate"));
        String meterNo = StringUtil.nullToBlank(conditionMap.get("meterNo"));
        String meterType = StringUtil.nullToBlank(conditionMap.get("meterType"));
        String tlbType = StringUtil.nullToBlank(conditionMap.get("tlbType"));
        List<Integer> channelIdList = (List<Integer>)conditionMap.get("channelIdList");
        String monthView = MeterType.valueOf(meterType).getMonthViewName();
        String monthTable = MeterType.valueOf(meterType).getMonthTableName();

        StringBuilder sb = new StringBuilder();

        if (isSum) {
            sb.append("\nSELECT CHANNEL AS CHANNEL, ");
            sb.append("\n       MAX(mv.value) AS MAX_VAL, ");
            sb.append("\n       MIN(mv.value) AS MIN_VAL, ");
            sb.append("\n       AVG(mv.value) AS AVG_VAL, ");
            sb.append("\n       SUM(mv.value) AS SUM_VAL ");
        } else {
            sb.append("\nSELECT mv.yyyymm AS YYYYMM, ");
            sb.append("\n       mv.channel AS CHANNEL, ");
            sb.append("\n       mv.value AS VALUE ");
        }
        sb.append("\nFROM ").append(monthTable).append(" mv 		");
        sb.append("\nLEFT OUTER JOIN ( 							");
        sb.append("\n    SELECT DISTINCT mo.mdev_id, 			");
        sb.append("\n           mo.ch_method 					");
        sb.append("\n    FROM ").append(monthTable).append(" mo	");
        sb.append("\n    WHERE mo.yyyymm BETWEEN :startDate AND :endDate ");
        sb.append("\n	 AND   mo.mdev_id = :meterNo 			");
        sb.append("\n) x ON mv.mdev_id = x.mdev_id 				");
        sb.append("\nWHERE mv.yyyymm BETWEEN :startDate AND :endDate ");

        if (channelIdList != null) {
            sb.append("\nAND   mv.channel IN (:channelIdList) ");
        }

        sb.append("\nAND   mv.mdev_id = :meterNo ");
        sb.append("\nAND   mv.supplier_id = :supplierId ");

        if (isSum) {
            sb.append("\nGROUP BY CHANNEL ");
        } else {
            sb.append("\nORDER BY mv.yyyymm, mv.channel ");
        }

        SQLQuery query = getSession().createSQLQuery(new SQLWrapper().getQuery(sb.toString()));

//        query.setString("tlbType", tlbType);
        query.setString("startDate", searchStartDate.substring(0, 6));
        query.setString("endDate", searchEndDate.substring(0, 6));
        query.setString("meterNo", meterNo);
        query.setInteger("supplierId", supplierId);

        if (channelIdList != null) {
            query.setParameterList("channelIdList", channelIdList);
        }

        result = query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
        return result;
    }
}