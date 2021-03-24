package com.aimir.dao.mvm.impl;

import java.text.ParseException;
import java.util.ArrayList;
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
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.aimir.constants.CommonConstants;
import com.aimir.constants.CommonConstants.ChangeMeterTypeName;
import com.aimir.constants.CommonConstants.DefaultChannel;
import com.aimir.constants.CommonConstants.MeterCodes;
import com.aimir.constants.CommonConstants.MeterType;
import com.aimir.constants.CommonConstants.NativeDayTable;
import com.aimir.dao.AbstractHibernateGenericDao;
import com.aimir.dao.mvm.MeteringDayDao;
import com.aimir.model.mvm.MeteringDay;
import com.aimir.util.CalendarUtil;
import com.aimir.util.SQLWrapper;
import com.aimir.util.StringUtil;
import com.aimir.util.TimeUtil;

@Repository(value = "meteringdayDao")
@SuppressWarnings("unchecked")
public class MeteringDayDaoImpl extends AbstractHibernateGenericDao<MeteringDay, Integer> implements MeteringDayDao {

    private static Log log = LogFactory.getLog(MeteringDayDaoImpl.class);
    
    @Autowired
    protected MeteringDayDaoImpl(SessionFactory sessionFactory) {
        super(MeteringDay.class);
        super.setSessionFactory(sessionFactory);
    }
    
    @Deprecated
    public List<Object> getConsumptionRanking(Map<String,Object> condition) {

        Query query = null;
        String rankingType = null; //jhkim
        try {
            
        String meterType   = (String)condition.get("meterType");
        rankingType = (String)condition.get("rankingType"); 
        int supplierId     = (Integer)condition.get("supplierId");
        int tariffType     = (Integer)condition.get("tariffType");
        String startDate   = (String)condition.get("startDate");
        String endDate     = (String)condition.get("endDate");
        List<Integer> locations = ((List<Integer>)condition.get("locations"));

//      logger.info("\n====conditions====\n"+condition);

        String dayTable = MeterType.valueOf(meterType).getDayClassName();
                
        StringBuffer sb = new StringBuffer();
      
        sb.append("\n SELECT  MAX(c.customer.name) as customerName ");
        sb.append("\n        ,MAX(c.contractNumber) as contractNo  ");
        sb.append("\n        ,SUM(d.total) as usage                ");
        sb.append("\n FROM    ").append(dayTable).append(" d ");
        sb.append("           INNER JOIN d.contract c              ");
        sb.append("\n WHERE   d.id.mdevType = :mdevType            ");
        sb.append("\n AND     d.id.yyyymmdd >= :startDate          ");
        sb.append("\n AND     d.id.yyyymmdd <= :endDate            ");
        sb.append("\n AND     d.id.channel = 1                     ");
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
            sb.append("\n ORDER BY SUM(d.total), c.id ");           
        }
        else{           
            sb.append("\n GROUP BY c.id ");
            sb.append("\n ORDER BY SUM(d.total) DESC, c.id ");
        }

        query = getSession().createQuery(sb.toString())
                                  .setString("startDate", startDate)
                                  .setString("endDate", endDate)
                                  .setString("mdevType", CommonConstants.DeviceType.Meter.name());
        

        query.setString("serviceType", MeterType.valueOf(meterType).getServiceType());
        
        if(supplierId > 0){
            query.setInteger("supplierId", supplierId);
        }
        if( tariffType > 0 ){
            query.setInteger("tariffType", tariffType);
        }
        query.setParameterList("locations", locations);

        }catch(Exception e){
            log.error(e, e);
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
        String dateType    = (String)condition.get("dateType");
        String startDate   = (String)condition.get("startDate");
        String endDate     = (String)condition.get("endDate");
        List<Integer> locations = ((List<Integer>)condition.get("locations"));
        
//        logger.info("\n====conditions====\n"+condition);

        String dayTable = MeterType.valueOf(meterType).getDayClassName();
        
        StringBuffer sb = new StringBuffer();
      
        sb.append("\n SELECT  SUM(d.total) as usage   ");
        sb.append("\n        ,MAX(c.contractNumber) as contractNo ");
        if(dateType.equals(CommonConstants.DateType.WEEKDAILY.getCode())){
            sb.append("\n        ,'"+startDate+"' as period ");
        }
        else{
            sb.append("\n        ,'"+startDate+" ~ "+endDate+"' as period ");
        }
        sb.append("\n        ,MAX(c.customer.name) as customerName   ");
        sb.append("\n        ,MAX(c.tariffIndex.name) as tariffName  ");
        sb.append("\n        ,MAX(c.location.name) as locationName   ");
        sb.append("\n FROM    ").append(dayTable).append(" d ");
        sb.append("\n         INNER JOIN d.contract c             ");
        sb.append("\n WHERE   d.id.mdevType = :mdevType              ");
        sb.append("\n AND     d.id.yyyymmdd >= :startDate            ");
        sb.append("\n AND     d.id.yyyymmdd <= :endDate              ");
        sb.append("\n AND     d.id.channel = 1                       ");
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
            sb.append("\n ORDER BY SUM(d.total), c.id ");
        }
        else{           
            sb.append("\n GROUP BY c.id ");
            sb.append("\n ORDER BY SUM(d.total) DESC, c.id ");
        }

        Query query = getSession().createQuery(sb.toString())
                                  .setString("startDate", startDate)
                                  .setString("endDate", endDate)
                                  .setString("mdevType", CommonConstants.DeviceType.Meter.name());

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
        query.setParameterList("locations", locations);

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
        List<Integer> locationList = ((List<Integer>)conditionMap.get("locationList"));
        String dayTable = MeterType.valueOf(meterType).getDayTableName();

        StringBuffer sb = new StringBuffer();

        if (isCount) {
            sb.append("\nSELECT COUNT(*) AS cnt FROM (SELECT co.id ");
        } else {
            sb.append("\nSELECT tb.totalUsage AS \"totalUsage\", ");
            sb.append("\n       tb.contractNo AS \"contractNo\", ");
            sb.append("\n       tb.customerName AS \"customerName\", ");
            sb.append("\n       tb.tariffName AS \"tariffName\", ");
            sb.append("\n       tb.locationName AS \"locationName\", ");
            sb.append("\n       tb.mdsId AS \"mdsId\" ");
            sb.append("\nFROM ( ");
            sb.append("\nSELECT ");

            if (rankingType.equals(CommonConstants.RankingType.ZERO.getType())) {
                sb.append("0 ");
            } else {
                sb.append("SUM(da.value) ");
            }
            sb.append("AS totalUsage, ");

            sb.append("\n       co.contract_number AS contractNo, ");
            sb.append("\n       cu.name AS customerName, ");
            sb.append("\n       ta.name AS tariffName, ");
            sb.append("\n       lo.name AS locationName, ");
            sb.append("\n       da.mdev_id AS mdsId, ");
            sb.append("\n       co.id AS contractId ");
        }
        sb.append("\nFROM ").append(dayTable).append(" da, ");
        sb.append("\n     contract co, ");
        if(sysId != null && !"".equals(sysId)) {
        	sb.append("\n     meter me, ");
            sb.append("\n     modem mo, ");
        }
        sb.append("\n     customer cu, ");
        sb.append("\n     tarifftype ta, ");
        sb.append("\n     location lo, ");
        sb.append("\n     code cd ");
        sb.append("\nWHERE da.contract_id = co.id ");
        sb.append("\nAND   co.customer_id = cu.id ");
        sb.append("\nAND   co.tariffIndex_id = ta.id ");
        sb.append("\nAND   co.location_id = lo.id ");
        sb.append("\nAND   co.servicetype_id = cd.id ");
        sb.append("\nAND   da.mdev_type = :mdevType ");
        sb.append("\nAND   da.yyyymmdd BETWEEN :startDate AND :endDate ");
        sb.append("\nAND   da.channel = 1 ");
        sb.append("\nAND   cd.code = :serviceType ");
        sb.append("\nAND   co.servicetype_id = ta.servicetype_id ");
        
        if(sysId != null && !"".equals(sysId)) {
            sb.append("\nAND   co.meter_id = me.id ");
            sb.append("\nAND   me.modem_id = mo.id ");
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
        sb.append("\n         da.mdev_id ");

        if (rankingType.equals(CommonConstants.RankingType.ZERO.getType())) {
            sb.append("\nHAVING SUM(da.total) = 0 ");
        } else if (totalUsage != null) {
            if (rankingType.equals(CommonConstants.RankingType.BEST.getType())) {
                sb.append("\nHAVING SUM(da.total) >= :totalUsage ");
            } else {
                sb.append("\nHAVING SUM(da.total) <= :totalUsage ");
            }
        } else if (!usageRange.isEmpty()) {
            String[] condArr = usageRange.split(",", 4);

            if (condArr.length == 4
                    && ((!condArr[0].isEmpty() && !condArr[1].isEmpty()) || (!condArr[2].isEmpty() && !condArr[3].isEmpty()))) {
                StringBuilder sbCond = new StringBuilder();

                if (!condArr[0].isEmpty() && !condArr[1].isEmpty()) {
                    sbCond.append("\nHAVING SUM(da.total) ");
                    sbCond.append(condArr[1]).append(" ");
                    sbCond.append(condArr[0]).append(" ");
                }

                if (!condArr[2].isEmpty() && !condArr[3].isEmpty()) {
                    if (sbCond.length() == 0) {
                        sbCond.append("\nHAVING");
                    } else {
                        sbCond.append("\n   AND");
                    }
                    sbCond.append(" SUM(da.total) ");
                    sbCond.append(condArr[3]).append(" ");
                    sbCond.append(condArr[2]).append(" ");
                }
                sb.append(sbCond);
            }
        }

        sb.append("\n) tb ");

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

        query.setString("startDate", startDate);
        query.setString("endDate", endDate);
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
     * 검침데이터 Max가젯 용 
     */
    public List<Object> getMeteringDayMaxGadgetWeekDataList(Map<String,Object> condition) {
        SQLQuery query = null;
        
            String startDate        = (String)condition.get("startDate");
            String endDate          = (String)condition.get("endDate"); 
            
            String[] arrStdEndDate  = (String[])condition.get("arrStdEndDate");
            
            String meterType        = (String)condition.get("meterType");
            int channel             = (Integer)condition.get("channel");
            String contractNumber   = (String)condition.get("contractNumber");
            String contractGroup    = (String)condition.get("contractGroup");//그룹검색 추가
            String customerName     = (String)condition.get("customer_name");
            String mdsId            = (String)condition.get("mdsId");
            String locationId        = StringUtil.nullToBlank(condition.get("locationId"));
            List<Integer> sLocations= (List<Integer>) condition.get("sLocations");
            String tariffType        = StringUtil.nullToBlank(condition.get("tariffType"));
            String deviceType       = (String)condition.get("deviceType");
            String mdevId           = (String)condition.get("mdevId");
            String mcuId            = (String)condition.get("mcuId");
//          String customType       = (String)condition.get("customType");
            // TODO - customTypeId 변경 : Customer -> Contract 로 위치 변경, customtype_id -> sic_id 로 컬럼명 변경
            String sicId       = (String)condition.get("customType");
            
            int first               = (Integer)condition.get("first");
            int max = 0;
            if(condition.get("excel")==null)
                max                 = (Integer)condition.get("max");
            String meteringSF       = (String)condition.get("meteringSF");
            String totalFlag        = (String)condition.get("totalFlag");
            String supplierId       = (String)condition.get("supplierId");
            int j=1;
    
//          logger.info("\n====conditions====\n"+condition);
            
            String dayTable = null;
            dayTable = NativeDayTable.valueOf(meterType).getTableName();
            
            try {
                StringBuffer sb = new StringBuffer();
                
                sb.append("\n SELECT  firstCol, contractNo, customerName, sum(sumtotal) as meteringData                         ");
                sb.append("\n       , mcuNo, meterNo,  locationName, tarifftypeNm                                       ");
                sb.append("\n FROM (                                                                                                ");
                sb.append("\n SELECT CASE                                                                                   ");
                for (int i=0; i< arrStdEndDate.length; i++) {
                    
                String[] betweenDate = arrStdEndDate[i].split("@");
                sb.append("\n                   WHEN D.YYYYMMDD BETWEEN '"+betweenDate[0]+"' AND '"+betweenDate[1]+"' THEN "+j+"                ");
                j++;
            }
             
            sb.append("\n                   ELSE 0 END  firstCol                                                            "); //'검침시각'
            sb.append("\n           , contract.CONTRACT_NUMBER  contractNo                                                      "); //'계약'
            sb.append("\n           , customer.name             customerName                                                    ");//'고객명'
            sb.append("\n           , D.total                   sumtotal                                                        ");//'검침사용량'
            sb.append("\n           , D.device_Id                   mcuNo                                                           ");//'집중기번호' 
            sb.append("\n           , meter.MDS_ID              meterNo                                                         ");//'미터번호'
            sb.append("\n           , location.name             locationName                                                    ");//'지역'
            sb.append("\n           , tarifftype.name           tarifftypeNm                                                    ");
            sb.append("\n        FROM    ").append(dayTable.toString()).append(" D                                                      ");
            sb.append("\n               LEFT OUTER JOIN CONTRACT contract on  D.CONTRACT_ID = contract.ID                       ");
            sb.append("\n               LEFT OUTER JOIN METER meter on  D.meter_id = meter.ID                                   ");
            sb.append("\n               LEFT OUTER JOIN CUSTOMER customer on contract.CUSTOMER_ID = customer.ID                 ");
            sb.append("\n               LEFT OUTER JOIN MODEM modem on meter.MODEM_ID = modem.ID                                ");
            sb.append("\n               LEFT OUTER JOIN MCU mcu on modem.MCU_ID = mcu.ID                                        ");
            sb.append("\n               LEFT OUTER JOIN LOCATION location on  D.LOCATION_ID = location.id                       ");
            sb.append("\n               LEFT OUTER JOIN TARIFFTYPE tarifftype on contract.tariffIndex_id = tarifftype.id        ");
            sb.append("\n               LEFT OUTER JOIN ( SELECT * FROM GROUP_MEMBER gm )contractGroup ON contract.contract_Number = contractGroup.member ");
            sb.append("\n       WHERE D.yyyymmdd BETWEEN :startDate AND :endDate                                             ");
                        
            //그룹검색 추가
            if(contractGroup != null && contractGroup.length() >0) {
                sb.append("\n    AND contractGroup.group_id = :contractGroup    ");
            }
            
            if(supplierId != null && supplierId.length() >0) {
                sb.append("\n     AND D.SUPPLIER_ID = :supplierId                                       ");
            }
            
            if(channel > 0) {
                sb.append("\n     AND D.CHANNEL =:channel                                                                       ");
            }
            
            if(customerName != null && customerName.length() >0) {
                sb.append("\n     AND customer.name LIKE :customerName                                      ");
            }
            
            if(contractNumber != null && contractNumber.length() >0) {
                sb.append("\n     AND contract.CONTRACT_NUMBER LIKE :contractNumber                                                 ");
            }
            
            if(mdsId != null && mdsId.length() >0) {
                sb.append("\n     AND meter.MDS_ID =:mdsId                                                                      ");
            }
            
            if(sLocations != null) {
                sb.append("\n     AND LOCATION.id IN (:locationId)                                                                  ");
            } else if(locationId != null && locationId.length() >0) {
                sb.append("\n     AND LOCATION.id =:locationId                                                                  ");
            }
            
            if(tariffType != null && tariffType.length() >0) {
                 sb.append("\n    AND tarifftype.id =:tariffType                                                                ");
            }
            if(deviceType != null && deviceType.length()>0){
                sb.append("\n     AND D.mdev_type =:deviceType                                                              ");
            }
            
            if(mdevId != null && mdevId.length()>0){
                sb.append("\n     AND D.mdev_id =:mdevId                                                                ");
            }
            
            if(mcuId != null && mcuId.length()>0){
                sb.append("\n     AND D.device_id = :mcuId                                                              ");
            }

//            if (customType != null && customType.length() > 0) {
//                sb.append("\n     AND customer.customtype_id = :customType                                               ");
//            }
            // TODO - customTypeId 변경 : Customer -> Contract 로 위치 변경, customtype_id -> sic_id 로 컬럼명 변경 
            if (sicId != null && sicId.length() > 0) {
                sb.append("\n     AND contract.sic_id = :sicId ");
            }

            if("s".equals(meteringSF)) { // 성공여부
                sb.append("\n         AND D.total is not null                                                                       ");
            } else if("f".equals(meteringSF)) { //실패여부
                sb.append("\n         AND D.total is null                                                                           ");
            }else {
                // 조건 안들어감
            }
            sb.append("\n   )   A                                                                                                   ");
            sb.append("\n GROUP BY  firstCol, contractNo, customerName, mcuNo, meterNo,  locationName, tarifftypeNm ");
            // sb.append("\n ORDER BY  contractNo, firstCol                                                                        ");
            
            query = getSession().createSQLQuery(sb.toString());
            query.setString("startDate", startDate);
            query.setString("endDate", endDate);
            
            if(contractGroup != null && contractGroup.length() >0){
                query.setString("contractGroup", contractGroup);
            }
            
            if(channel > 0){
                query.setInteger("channel", channel);
            }
            
            if(supplierId != null && supplierId.length() >0) {
                query.setInteger("supplierId", Integer.parseInt(supplierId));
            }
            if(customerName != null && customerName.length() >0) {
                query.setString("customerName", "%" + customerName + "%");
            }
            
            if(contractNumber != null && contractNumber.length() >0) {
                query.setString("contractNumber", "%" + contractNumber + "%");
            }
                
            if(mdsId != null && mdsId.length() >0) {
                query.setString("mdsId", mdsId);
            }
            
            if(sLocations != null) {
                query.setParameterList("locationId", sLocations);
            } else if(locationId != null && locationId.length() >0) {
                query.setInteger("locationId", Integer.parseInt(locationId));
            }
            
            if(tariffType != null && tariffType.length() >0) {
                query.setInteger("tariffType", Integer.parseInt(tariffType));
            }
            
            if(deviceType != null && deviceType.length()>0){
                query.setString("deviceType", deviceType);
            }
            
            if(mdevId != null && mdevId.length()>0){
                query.setString("mdevId", mdevId);
            }
            
            if(mcuId != null && mcuId.length()>0){
                query.setString("mcuId", mcuId);
            }

//            if (customType != null && customType.length() > 0) {
//                query.setInteger("customType", new Integer(customType));
//            }
            // TODO - customTypeId 변경 : Customer -> Contract 로 위치 변경, customtype_id -> sic_id 로 컬럼명 변경
            if (sicId != null && sicId.length() > 0) {
                query.setInteger("sicId", new Integer(sicId));
            }


        }catch(Exception e){
            log.error(e, e);
        }
        
        if((totalFlag == null) || (!"Y".equals(totalFlag))) {
            query.setFirstResult(first);
            if(condition.get("excel")==null)
                query.setMaxResults(max);
        }

        return query.list();
    }
    
    @SuppressWarnings("unused")
    public List<Object> getMeteringDayMaxGadgetYearDataList(Map<String,Object> condition) {
        SQLQuery query = null;
        
            String startDate        = (String)condition.get("startDate");
            String endDate          = (String)condition.get("endDate");
            
            String meterType        = (String)condition.get("meterType");
            int channel             = (Integer)condition.get("channel");
            String contractNumber   = (String)condition.get("contractNumber");
            String contractGroup    = (String)condition.get("contractGroup");//그룹검색 추가
            String customerName     = (String)condition.get("customer_name");
            String mdsId            = (String)condition.get("mdsId");
            String locationId        = StringUtil.nullToBlank(condition.get("locationId"));
            List<Integer> sLocations= (List<Integer>) condition.get("sLocations");
            String tariffType        = StringUtil.nullToBlank(condition.get("tariffType"));
            String deviceType       = (String)condition.get("deviceType");
            String mdevId           = (String)condition.get("mdevId");
            String mcuId            = (String)condition.get("mcuId");
//          String customType       = (String)condition.get("customType");
            // TODO - customTypeId 변경 : Customer -> Contract 로 위치 변경, customtype_id -> sic_id 로 컬럼명 변경
            String sicId       = (String)condition.get("customType");
            
            int first               = (Integer)condition.get("first");
            int max = 0;
            if(condition.get("excel")==null)
                max                 = (Integer)condition.get("max");
            String meteringSF       = (String)condition.get("meteringSF");
            String totalFlag        = (String)condition.get("totalFlag");
            String supplierId       = (String)condition.get("supplierId");
            int j=1;
    
//          logger.info("\n====conditions====\n"+condition);
            
            String dayTable = null;
            if(meterType.equals("EM")){
                dayTable = "MONTH_EM";
            } else if(meterType.equals("GM")){
                dayTable = "MONTH_GM";
            } else if(meterType.equals("WM")){
                dayTable = "MONTH_WM";
            } else if(meterType.equals("HM")){
                dayTable = "MONTH_HM";
            }
            //dayTable = NativeDayTable.valueOf(meterType).getTableName();
            
            try {
                StringBuffer sb = new StringBuffer();
                
                sb.append("\n SELECT  contractNo, customerName, sum(sumtotal) as meteringData                           ");
                sb.append("\n       , mcuNo, meterNo,  locationName, tarifftypeNm                                       ");
                sb.append("\n FROM (                                                                                                ");
                sb.append("\n SELECT                                                                                    ");
               
            sb.append("\n            contract.CONTRACT_NUMBER   contractNo                                                      "); //'계약'
            sb.append("\n           , customer.name             customerName                                                    ");//'고객명'
            sb.append("\n           , D.total                   sumtotal                                                        ");//'검침사용량'
            sb.append("\n           , D.device_Id                   mcuNo                                                           ");//'집중기번호' 
            sb.append("\n           , meter.MDS_ID              meterNo                                                         ");//'미터번호'
            sb.append("\n           , location.name             locationName                                                    ");//'지역'
            sb.append("\n           , tarifftype.name           tarifftypeNm                                                    ");
            sb.append("\n        FROM    ").append(dayTable.toString()).append(" D                                                      ");
            sb.append("\n               LEFT OUTER JOIN CONTRACT contract on  D.CONTRACT_ID = contract.ID                       ");
            sb.append("\n               LEFT OUTER JOIN METER meter on  D.meter_id = meter.ID                                   ");
            sb.append("\n               LEFT OUTER JOIN CUSTOMER customer on contract.CUSTOMER_ID = customer.ID                 ");
            sb.append("\n               LEFT OUTER JOIN MODEM modem on meter.MODEM_ID = modem.ID                                ");
            sb.append("\n               LEFT OUTER JOIN MCU mcu on modem.MCU_ID = mcu.ID                                        ");
            sb.append("\n               LEFT OUTER JOIN LOCATION location on  D.LOCATION_ID = location.id                       ");
            sb.append("\n               LEFT OUTER JOIN TARIFFTYPE tarifftype on contract.tariffIndex_id = tarifftype.id        ");
            sb.append("\n               LEFT OUTER JOIN ( SELECT * FROM GROUP_MEMBER gm )contractGroup ON contract.contract_Number = contractGroup.member ");
            sb.append("\n       WHERE D.YYYYMM LIKE :startDate ");//BETWEEN :startDate AND :endDate    
            //그룹검색 추가
            if(contractGroup != null && contractGroup.length() >0) {
                sb.append("\n    AND contractGroup.group_id = :contractGroup    ");
            }
            
            if(supplierId != null && supplierId.length() >0) {
                sb.append("\n     AND D.SUPPLIER_ID = :supplierId                                       ");
            }
            
            if(channel > 0) {
                sb.append("\n     AND D.CHANNEL =:channel                                                                       ");
            }
            
            if(customerName != null && customerName.length() >0) {
                sb.append("\n     AND customer.name LIKE :customerName                                      ");
            }
            
            if(contractNumber != null && contractNumber.length() >0) {
                sb.append("\n     AND contract.CONTRACT_NUMBER LIKE :contractNumber                                                 ");
            }
            
            if(mdsId != null && mdsId.length() >0) {
                sb.append("\n     AND meter.MDS_ID =:mdsId                                                                      ");
            }
            
            if(sLocations != null) {
                sb.append("\n     AND LOCATION.id IN (:locationId)                                                                  ");
            } else if(locationId != null && locationId.length() >0) {
                sb.append("\n     AND LOCATION.id =:locationId                                                                  ");
            }
            
            if(tariffType != null && tariffType.length() >0) {
                 sb.append("\n    AND tarifftype.id =:tariffType                                                                ");
            }
            if(deviceType != null && deviceType.length()>0){
                sb.append("\n     AND D.mdev_type =:deviceType                                                              ");
            }
            
            if(mdevId != null && mdevId.length()>0){
                sb.append("\n     AND D.mdev_id =:mdevId                                                                ");
            }
            
            if(mcuId != null && mcuId.length()>0){
                sb.append("\n     AND D.device_id = :mcuId                                                              ");
            }

//            if (customType != null && customType.length() > 0) {
//                sb.append("\n     AND customer.customtype_id = :customType                                               ");
//            }
            // TODO - customTypeId 변경 : Customer -> Contract 로 위치 변경, customtype_id -> sic_id 로 컬럼명 변경 
            if (sicId != null && sicId.length() > 0) {
                sb.append("\n     AND contract.sic_id = :sicId ");
            }

            if("s".equals(meteringSF)) { // 성공여부
                sb.append("\n         AND D.total is not null                                                                       ");
            } else if("f".equals(meteringSF)) { //실패여부
                sb.append("\n         AND D.total is null                                                                           ");
            }else {
                // 조건 안들어감
            }
            sb.append("\n   )   A                                                                                                   ");
            sb.append("\n GROUP BY  contractNo, customerName, mcuNo, meterNo,  locationName, tarifftypeNm ");
            // sb.append("\n ORDER BY  contractNo                                                                      ");
            
            query = getSession().createSQLQuery(sb.toString());
            query.setString("startDate", startDate.substring(0, 4)+"%");
            //query.setString("endDate", endDate);
            
            if(contractGroup != null && contractGroup.length() >0){
                query.setString("contractGroup", contractGroup);
            }
            
            if(channel > 0){
                query.setInteger("channel", channel);
            }
            
            if(supplierId != null && supplierId.length() >0) {
                query.setInteger("supplierId", Integer.parseInt(supplierId));
            }
            if(customerName != null && customerName.length() >0) {
                query.setString("customerName", "%" + customerName + "%");
            }
            
            if(contractNumber != null && contractNumber.length() >0) {
                query.setString("contractNumber", "%" + contractNumber + "%");
            }
                
            if(mdsId != null && mdsId.length() >0) {
                query.setString("mdsId", mdsId);
            }
            
            if(sLocations != null) {
                query.setParameterList("locationId", sLocations);
            } else if(locationId != null && locationId.length() >0) {
                query.setInteger("locationId", Integer.parseInt(locationId));
            }
            
            if(tariffType != null && tariffType.length() >0) {
                query.setInteger("tariffType", Integer.parseInt(tariffType));
            }
            
            if(deviceType != null && deviceType.length()>0){
                query.setString("deviceType", deviceType);
            }
            
            if(mdevId != null && mdevId.length()>0){
                query.setString("mdevId", mdevId);
            }
            
            if(mcuId != null && mcuId.length()>0){
                query.setString("mcuId", mcuId);
            }

//            if (customType != null && customType.length() > 0) {
//                query.setInteger("customType", new Integer(customType));
//            }
            // TODO - customTypeId 변경 : Customer -> Contract 로 위치 변경, customtype_id -> sic_id 로 컬럼명 변경
            if (sicId != null && sicId.length() > 0) {
                query.setInteger("sicId", new Integer(sicId));
            }


        }catch(Exception e){
            log.error(e, e);
        }
        
        if((totalFlag == null) || (!"Y".equals(totalFlag))) {
            query.setFirstResult(first);
            if(condition.get("excel")==null)
                query.setMaxResults(max);
        }
        
        
        return query.list();
    }
    
    /*
     * 검침데이터 비교차트용 tot count
     */
    public int getLoadDurationChartTotalCount(Map<String,Object> condition) {
        SQLQuery query = null;
        
        String startDate        = (String)condition.get("startDate");
        String endDate          = (String)condition.get("endDate");
        
        String meterType        = (String)condition.get("meterType");
        int channel             = (Integer)condition.get("channel");
        int contractId          = (Integer)condition.get("contractId");
        
//      logger.info("\n====conditions====\n"+condition);
        
        String dayTable = null;
        dayTable = NativeDayTable.valueOf(meterType).getTableName();
        
        try {
            StringBuffer sb = new StringBuffer();
            
            sb.append("\n SELECT COUNT(TOTAL) AS CNT                            ");
            sb.append("\n FROM    ").append(dayTable).append("      ");
            sb.append("\n WHERE  CHANNEL=:channel                               ");
            sb.append("\n AND YYYYMMDD BETWEEN :startDate AND :endDate          ");
            sb.append("\n AND CONTRACT_ID = :contractId                     ");
            //sb.append("\n ORDER BY CONTRACT_ID                                    ");
            
            
            query = getSession().createSQLQuery(sb.toString());
            query.setString("startDate", startDate);
            query.setString("endDate", endDate);
            
            if(channel > 0){
                query.setInteger("channel", channel);
            }
            if(contractId > 0){
                query.setInteger("contractId", contractId);
            }
    
        }catch(Exception e){
            log.error(e, e);
        }
    
    
        return ((Number)query.list().get(0)).intValue();
        
    }
    
    /*
     * 검침데이터 비교차트용 
     */
    public List<Object> getLoadDurationChartData(Map<String,Object> condition) {
        SQLQuery query = null;
        
        String startDate        = (String)condition.get("startDate");
        String endDate          = (String)condition.get("endDate");
        
        String meterType        = (String)condition.get("meterType");
        int channel             = (Integer)condition.get("channel");
        int contractId          = (Integer)condition.get("contractId");
        int dataTotCount        = (Integer)condition.get("dataTotCount");
        
        
        
//      logger.info("\n====conditions====\n"+condition);
        
        String dayTable = null;
        dayTable = NativeDayTable.valueOf(meterType).getTableName();
        
        try {
            StringBuffer sb = new StringBuffer();
            
            sb.append("\n SELECT B.CONTRACT_NUMBER, A.TOTAL, ( (COUNT(A.TOTAL)* 100)/").append(dataTotCount).append(") AS VALUE     ");
            sb.append("\n FROM    ").append(dayTable).append(" A, CONTRACT B        ");
            sb.append("\n WHERE  A.CHANNEL=:channel                             ");
            sb.append("\n AND A.CONTRACT_ID = B.ID                              ");
            sb.append("\n AND A.YYYYMMDD BETWEEN :startDate AND :endDate            ");
            sb.append("\n AND B.ID = :contractId                                ");
            sb.append("\n GROUP BY B.CONTRACT_NUMBER, A.TOTAL                           ");
            // sb.append("\n ORDER BY B.CONTRACT_NUMBER, A.TOTAL                                   ");
            
            
            query = getSession().createSQLQuery(sb.toString());
            query.setString("startDate", startDate);
            query.setString("endDate", endDate);
            
            if(channel > 0){
                query.setInteger("channel", channel);
            }
            
            if(contractId > 0){
                query.setInteger("contractId", contractId);
            }
    
        }catch(Exception e){
            log.error(e, e);
        }
    
    
        return query.list();
        
    }
    
    /*
     * 검침데이터 OverlayChart용 
     */
    @SuppressWarnings("deprecation")
    public List<Object> getOverlayChartData(Map<String,Object> condition) {
        SQLQuery query = null;
        
        String startDate        = (String)condition.get("startDate");
        String endDate          = (String)condition.get("endDate");
        
        String meterType        = (String)condition.get("meterType");
        Integer[] arrChannel    = (Integer[])condition.get("arrChannel");
        int contractId          = (Integer)condition.get("contractId");
        
        
        
//      logger.info("\n====conditions====\n"+condition);
        
        String dayTable = null;
        dayTable = NativeDayTable.valueOf(meterType).getTableName();
        
        try {
            StringBuffer sb = new StringBuffer();
            
            sb.append("\n SELECT B.CONTRACT_NUMBER, A.YYYYMMDD, A.CHANNEL");
            for(int i=0;i< 24;i++) {
                if(i < 10) {
                    sb.append(", SUM(A.value_0").append(String.valueOf(i)).append(") as value0").append(i).append("");
                }
                else {
                    sb.append(", SUM(A.value_").append(String.valueOf(i)).append(") as value").append(i).append("");
                }
            }
            sb.append("\n FROM    ").append(dayTable).append(" A,  CONTRACT B   ");
            sb.append("\n WHERE  A.CHANNEL IN (:channel)                        ");
            sb.append("\n AND A.YYYYMMDD BETWEEN :startDate AND :endDate        ");
            sb.append("\n AND A.CONTRACT_ID = B.ID                              ");
            sb.append("\n AND B.ID = :contractId                                ");
            sb.append("\n GROUP BY B.CONTRACT_NUMBER, A.YYYYMMDD, A.CHANNEL     ");
            // sb.append("\n ORDER BY B.CONTRACT_NUMBER, A.YYYYMMDD, A.CHANNEL     ");
            
            
            query = getSession().createSQLQuery(sb.toString());
            query.setString("startDate", startDate);
            query.setString("endDate", endDate);
            
            if(arrChannel.length > 0){
                query.setParameterList("channel", arrChannel);
            }
            
            if(contractId > 0){
                query.setInteger("contractId", contractId);
            }
    
        }catch(Exception e){
            log.error(e, e);
        }
    
    
        return query.list();
        
    }

    /**
     * Overlay Chart Data (Day)
     * 
     * @param condition
     * @param contractId
     * @return
     */
    public List<Map<String, Object>> getOverlayChartDailyData(Map<String,Object> condition, Integer contractId) {
        Query query = null;
        String type = (String) condition.get("type");
        String meterType = ChangeMeterTypeName.valueOf(type).getCode();
        String beginDate = (String) condition.get("beginDate");
        String endDate = (String) condition.get("endDate");
        String time = null;
        Integer channel = new Integer((String)condition.get("channel"));
        String dayClass = MeterType.valueOf(meterType).getDayClassName();

        StringBuilder sb = new StringBuilder();

        sb.append("\nSELECT day.id.yyyymmdd AS yyyymmdd, ");

        for (int i = 0 ; i < 24 ; i++) {
            time = StringUtil.frontAppendNStr('0', Integer.toString(i), 2);
            sb.append("\n       COALESCE(day.value_").append(time).append(", 0) AS value_").append(time).append(", ");
        }
        sb.append("\n       day.contract.contractNumber AS contractNumber ");

        sb.append("\nFROM ").append(dayClass).append(" day ");
        sb.append("\nWHERE day.id.yyyymmdd BETWEEN :beginDate AND :endDate ");
        sb.append("\nAND   day.id.channel = :channel ");
        sb.append("\nAND   day.contract.id = :contractId ");
        
        // sb.append("\nORDER BY day.id.yyyymmdd ");

        query = getSession().createQuery(sb.toString());
        query.setString("beginDate", beginDate);
        query.setString("endDate", endDate);
        query.setInteger("channel", channel);
        query.setInteger("contractId", contractId);

        return query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
    }
    
    /*
     * 검침데이터 getInOffTimeChartData용 
     */
    public List<Object> getInOffTimeChartData(Map<String,Object> condition) {
        SQLQuery query = null;
//      logger.info("\n====conditions====\n"+condition);
        String startDate        = (String)condition.get("startDate");
        String endDate          = (String)condition.get("endDate");
        
        String meterType        = (String)condition.get("meterType");
        Integer[] arrChannel    = (Integer[])condition.get("arrChannel");
        int contractId          = (Integer)condition.get("contractId");
        
        List<String> ls         = (List<String>)condition.get("selectQuery");
        int listSize            = ls.size();
        
        String dayTable = null;
        dayTable = NativeDayTable.valueOf(meterType).getTableName();
        
        try {
            StringBuffer sb = new StringBuffer();
            
            sb.append("\n SELECT B.CONTRACT_NUMBER, A.YYYYMMDD, A.CHANNEL");
            
            for(int i=0;i< listSize;i++) {
                if(i==0) {
                    sb.append(", "+ls.get(i));
                }
                else {
                    sb.append("+"+ls.get(i));
                }
            }
            sb.append("\n FROM    ").append(dayTable).append(" A,  CONTRACT B   ");
            sb.append("\n WHERE  A.CHANNEL IN (:channel)                        ");
            sb.append("\n AND A.YYYYMMDD BETWEEN :startDate AND :endDate        ");
            sb.append("\n AND A.CONTRACT_ID = B.ID                              ");
            sb.append("\n AND B.ID = :contractId                                ");
            // sb.append("\n ORDER BY B.CONTRACT_NUMBER, A.YYYYMMDD, A.CHANNEL     ");
            
            
            query = getSession().createSQLQuery(sb.toString());
            query.setString("startDate", startDate);
            query.setString("endDate", endDate);
            
            if(arrChannel.length > 0){
                query.setParameterList("channel", arrChannel, StandardBasicTypes.INTEGER);
            }
            
            if(contractId > 0){
                query.setInteger("contractId", contractId);
            }
    
        }catch(Exception e){
            log.error(e, e);
        }
    
    
        return query.list();
        
    }
    
    
    /**
     * 특정날짜의 검침데이터를 조회한다.
     * 입력된 EndDeviceId,ModemId,MeterID 목록을 IN 조건으로 조회하며 각각 OR 조건으로 묶는다.
     * @param params - 
     * @param params2 - 
     * @return
     */
    public List<Object> getUsageForEndDevicesByDay(Map<String,Object> params,Map<String,Object> params2){
         
        Integer channel = (Integer)params2.get("channel");
        Integer dst = (Integer)params2.get("dst");
        String meterType = (String)params2.get("meterType");  
        
        String today = (String)params.get("today"); 
        List<Integer> endDeviceId = (List<Integer>)params.get("endDeviceId");
        List<Integer> modemId = (List<Integer>)params.get("modemId");
        List<Integer> meterId = (List<Integer>)params.get("meterId");
        
        StringBuffer sb = new StringBuffer();
        sb.append("\n SELECT SUM(day.value_00 ) AS value00 ");
        sb.append("\n       ,SUM(day.value_01 ) AS value01 ");
        sb.append("\n       ,SUM(day.value_02 ) AS value02 ");
        sb.append("\n       ,SUM(day.value_03 ) AS value03 ");
        sb.append("\n       ,SUM(day.value_04 ) AS value04 ");
        sb.append("\n       ,SUM(day.value_05 ) AS value05 ");
        sb.append("\n       ,SUM(day.value_06 ) AS value06 ");
        sb.append("\n       ,SUM(day.value_07 ) AS value07 ");
        sb.append("\n       ,SUM(day.value_08 ) AS value08 ");
        sb.append("\n       ,SUM(day.value_09 ) AS value09 ");
        sb.append("\n       ,SUM(day.value_10 ) AS value10 ");
        sb.append("\n       ,SUM(day.value_11 ) AS value11 ");
        sb.append("\n       ,SUM(day.value_12 ) AS value12 ");
        sb.append("\n       ,SUM(day.value_13 ) AS value13 ");
        sb.append("\n       ,SUM(day.value_14 ) AS value14 ");
        sb.append("\n       ,SUM(day.value_15 ) AS value15 ");
        sb.append("\n       ,SUM(day.value_16 ) AS value16 ");
        sb.append("\n       ,SUM(day.value_17 ) AS value17 ");
        sb.append("\n       ,SUM(day.value_18 ) AS value18 ");
        sb.append("\n       ,SUM(day.value_19 ) AS value19 ");
        sb.append("\n       ,SUM(day.value_20 ) AS value20 ");
        sb.append("\n       ,SUM(day.value_21 ) AS value21 ");
        sb.append("\n       ,SUM(day.value_22 ) AS value22 ");
        sb.append("\n       ,SUM(day.value_23 ) AS value23 ");
        sb.append("\n       ,SUM(day.total ) AS total ");
        sb.append("\n FROM ").append(meterType).append(" day ");
        sb.append("\n WHERE 1=1 ");
        sb.append("\n AND ( day.enddevice.id IN (:endDeviceId ) OR day.meter.id IN (:meterId) OR day.modem.id IN (:modemId) ) ");
        sb.append("\n AND day.id.yyyymmdd = :yyyymmdd ");
        sb.append("\n AND day.id.channel = :channel ");
        sb.append("\n AND day.id.dst = :dst ");
        
        Query query  = getSession().createQuery(sb.toString())
                                   .setParameterList("endDeviceId", endDeviceId)
                                   .setParameterList("meterId", meterId)
                                   .setParameterList("modemId", modemId)
                                   .setString("yyyymmdd", today)
                                   .setInteger("channel", channel)
                                   .setInteger("dst", dst);

        List<Object> resultList = query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();

        return resultList;
    }
    
    
    /**
     * 특정날짜의 검침데이터를 조회한다.
     * 입력된 EndDeviceId,ModemId,MeterID 목록을 IN 조건으로 조회하며 각각 OR 조건으로 묶는다.
     * @param params - 
     * @param params2 - 
     * @return
     */
    public List<Object> getUsageForEndDevicesBySearchDate(Map<String,Object> params,Map<String,Object> params2){
         
        Integer channel = (Integer)params2.get("channel");
        Integer dst = (Integer)params2.get("dst");
        String meterType = (String)params2.get("meterType");  
        
        String searchStartDate = (String)params.get("searchStartDate"); 
        List<Integer> endDeviceId = (List<Integer>)params.get("endDeviceId");
        List<Integer> modemId = (List<Integer>)params.get("modemId");
        List<Integer> meterId = (List<Integer>)params.get("meterId");
        
        StringBuffer sb = new StringBuffer();
        sb.append("\n SELECT SUM(day.value_00 ) AS value00 ");
        sb.append("\n       ,SUM(day.value_01 ) AS value01 ");
        sb.append("\n       ,SUM(day.value_02 ) AS value02 ");
        sb.append("\n       ,SUM(day.value_03 ) AS value03 ");
        sb.append("\n       ,SUM(day.value_04 ) AS value04 ");
        sb.append("\n       ,SUM(day.value_05 ) AS value05 ");
        sb.append("\n       ,SUM(day.value_06 ) AS value06 ");
        sb.append("\n       ,SUM(day.value_07 ) AS value07 ");
        sb.append("\n       ,SUM(day.value_08 ) AS value08 ");
        sb.append("\n       ,SUM(day.value_09 ) AS value09 ");
        sb.append("\n       ,SUM(day.value_10 ) AS value10 ");
        sb.append("\n       ,SUM(day.value_11 ) AS value11 ");
        sb.append("\n       ,SUM(day.value_12 ) AS value12 ");
        sb.append("\n       ,SUM(day.value_13 ) AS value13 ");
        sb.append("\n       ,SUM(day.value_14 ) AS value14 ");
        sb.append("\n       ,SUM(day.value_15 ) AS value15 ");
        sb.append("\n       ,SUM(day.value_16 ) AS value16 ");
        sb.append("\n       ,SUM(day.value_17 ) AS value17 ");
        sb.append("\n       ,SUM(day.value_18 ) AS value18 ");
        sb.append("\n       ,SUM(day.value_19 ) AS value19 ");
        sb.append("\n       ,SUM(day.value_20 ) AS value20 ");
        sb.append("\n       ,SUM(day.value_21 ) AS value21 ");
        sb.append("\n       ,SUM(day.value_22 ) AS value22 ");
        sb.append("\n       ,SUM(day.value_23 ) AS value23 ");
        sb.append("\n       ,SUM(day.total ) AS total ");
        sb.append("\n FROM ").append(meterType).append(" day ");
        sb.append("\n WHERE 1=1 ");
        sb.append("\n AND ( day.enddevice.id IN (:endDeviceId ) OR day.meter.id IN (:meterId) OR day.modem.id IN (:modemId) ) ");
        sb.append("\n AND day.id.yyyymmdd = :yyyymmdd ");
        sb.append("\n AND day.id.channel = :channel ");
        sb.append("\n AND day.id.dst = :dst ");
        
        Query query  = getSession().createQuery(sb.toString())
                                   .setParameterList("endDeviceId", endDeviceId)
                                   .setParameterList("meterId", meterId)
                                   .setParameterList("modemId", modemId)
                                   .setString("yyyymmdd", searchStartDate)
                                   .setInteger("channel", channel)
                                   .setInteger("dst", dst);

        List<Object> resultList = query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();

        return resultList;
    }
    
    /**
     * 특정주의 검침데이터를 조회한다.
     * 입력된 EndDeviceId,ModemId,MeterID 목록을 IN 조건으로 조회하며 각각 OR 조건으로 묶는다.
     * @param params - 
     * @param params2 - 
     * @return
     */
    public List<Object> getUsageForEndDevicesByWeek(Map<String,Object> params,Map<String,Object> params2){
         
        Integer channel = (Integer)params2.get("channel");
        Integer dst = (Integer)params2.get("dst");
        String meterType = (String)params2.get("meterType");  
        
        String currWeekStartDate = (String)params.get("currWeekStartDate"); 
        String currWeekEndDate = (String)params.get("currWeekEndDate");
        List<Integer> endDeviceId = (List<Integer>)params.get("endDeviceId");
        List<Integer> modemId = (List<Integer>)params.get("modemId");
        List<Integer> meterId = (List<Integer>)params.get("meterId");
        
        StringBuffer sb = new StringBuffer();
        sb.append("\n SELECT day.id.yyyymmdd as yyyymmdd, SUM(day.total) AS total                                                                   ");
        sb.append("\n FROM ").append(meterType).append(" day                                                                                         ");
        sb.append("\n WHERE 1=1                                                                                                 ");
        sb.append("\n AND ( day.enddevice.id IN (:endDeviceId ) OR day.meter.id IN (:meterId) OR day.modem.id IN (:modemId) )       ");
        sb.append("\n AND day.id.yyyymmdd between :startDate and :endDate                                                                           ");
        sb.append("\n AND day.id.channel = :channel                                                                                     ");
        sb.append("\n AND day.id.dst = :dst                                                                                         ");
        // sb.append("\n GROUP BY day.id.yyyymmdd ORDER BY day.id.yyyymmdd                 ");
        sb.append("\n GROUP BY day.id.yyyymmdd                 ");
        
        Query query  = getSession().createQuery(sb.toString())
                                   .setParameterList("endDeviceId", endDeviceId)
                                   .setParameterList("meterId", meterId)
                                   .setParameterList("modemId", modemId)
                                   .setString("startDate", currWeekStartDate)
                                   .setString("endDate", currWeekEndDate)
                                   .setInteger("channel", channel)
                                   .setInteger("dst", dst);

        return query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
    }
    
    /**
     * 특정년도의 검침데이터를 월별로 조회한다.
     * 입력된 EndDeviceId,ModemId,MeterID 목록을 IN 조건으로 조회하며 각각 OR 조건으로 묶는다.
     * @param params - 
     * @param params2 - 
     * @return
     */
    public List<Object> getUsageForEndDevicesByMonth(Map<String,Object> params,Map<String,Object> params2){
         
        Integer channel = (Integer)params2.get("channel");
        Integer dst = (Integer)params2.get("dst");
        String meterType = (String)params2.get("meterType");  
        
        String currYear = (String)params.get("currYear"); 
        String currYearStartMonth = currYear+"01";
        String currYearEndMonth = currYear+"12";
        List<Integer> endDeviceId = (List<Integer>)params.get("endDeviceId");
        List<Integer> modemId = (List<Integer>)params.get("modemId");
        List<Integer> meterId = (List<Integer>)params.get("meterId");
        
        StringBuffer sb = new StringBuffer();
        sb.append("\n SELECT month.id.yyyymm as yyyymm, SUM(month.total) AS total ");
        sb.append("\n       ,SUM(month.value_01 ) AS value01 ");
        sb.append("\n       ,SUM(month.value_02 ) AS value02 ");
        sb.append("\n       ,SUM(month.value_03 ) AS value03 ");
        sb.append("\n       ,SUM(month.value_04 ) AS value04 ");
        sb.append("\n       ,SUM(month.value_05 ) AS value05 ");
        sb.append("\n       ,SUM(month.value_06 ) AS value06 ");
        sb.append("\n       ,SUM(month.value_07 ) AS value07 ");
        sb.append("\n       ,SUM(month.value_08 ) AS value08 ");
        sb.append("\n       ,SUM(month.value_09 ) AS value09 ");
        sb.append("\n       ,SUM(month.value_10 ) AS value10 ");
        sb.append("\n       ,SUM(month.value_11 ) AS value11 ");
        sb.append("\n       ,SUM(month.value_12 ) AS value12 ");
        sb.append("\n       ,SUM(month.value_13 ) AS value13 ");
        sb.append("\n       ,SUM(month.value_14 ) AS value14 ");
        sb.append("\n       ,SUM(month.value_15 ) AS value15 ");
        sb.append("\n       ,SUM(month.value_16 ) AS value16 ");
        sb.append("\n       ,SUM(month.value_17 ) AS value17 ");
        sb.append("\n       ,SUM(month.value_18 ) AS value18 ");
        sb.append("\n       ,SUM(month.value_19 ) AS value19 ");
        sb.append("\n       ,SUM(month.value_20 ) AS value20 ");
        sb.append("\n       ,SUM(month.value_21 ) AS value21 ");
        sb.append("\n       ,SUM(month.value_22 ) AS value22 ");
        sb.append("\n       ,SUM(month.value_23 ) AS value23 ");
        sb.append("\n       ,SUM(month.value_24 ) AS value24 ");
        sb.append("\n       ,SUM(month.value_25 ) AS value25 ");
        sb.append("\n       ,SUM(month.value_26 ) AS value26 ");
        sb.append("\n       ,SUM(month.value_27 ) AS value27 ");
        sb.append("\n       ,SUM(month.value_28 ) AS value28 ");
        sb.append("\n       ,SUM(month.value_29 ) AS value29 ");
        sb.append("\n       ,SUM(month.value_30 ) AS value30 ");
        sb.append("\n       ,SUM(month.value_31 ) AS value31 ");
        sb.append("\n FROM ").append(meterType).append(" month ");
        sb.append("\n WHERE 1=1 ");
        sb.append("\n AND ( month.enddevice.id IN (:endDeviceId ) OR month.meter.id IN (:meterId) OR month.modem.id IN (:modemId) ) ");
        sb.append("\n AND month.id.yyyymm between :startDate and :endDate ");
        sb.append("\n AND month.id.channel = :channel ");
        sb.append("\n AND month.id.dst = :dst ");
        // sb.append("\n GROUP BY month.id.yyyymm ORDER BY month.id.yyyymm ");
        sb.append("\n GROUP BY month.id.yyyymm");
        
        Query query  = getSession().createQuery(sb.toString())
                                   .setParameterList("endDeviceId", endDeviceId)
                                   .setParameterList("meterId", meterId)
                                   .setParameterList("modemId", modemId)
                                   .setString("startDate", currYearStartMonth)
                                   .setString("endDate", currYearEndMonth)
                                   .setInteger("channel", channel)
                                   .setInteger("dst", dst);

        return query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
    }
    
    
    /**
     * 특정기간의 검침데이터를 일별로 조회한다.
     * 입력된 EndDeviceId,ModemId,MeterID 목록을 IN 조건으로 조회하며 각각 OR 조건으로 묶는다.
     * @param params - 
     * @return
     */
    public List<Object> getUsageForEndDevicesByDayPeriod(Map<String,Object> params,Map<String,Object> params2){
         
        Integer channel     = (Integer)params2.get("channel");
        Integer dst         = (Integer)params2.get("dst");
        String meterType    = (String)params2.get("meterType");  
        
        String searchStartDate      = (String)params.get("searchStartDate"); 
        String searchEndDate        = (String)params.get("searchEndDate");
        
        List<Integer> endDeviceId   = (List<Integer>)params.get("endDeviceId");
        List<Integer> modemId       = (List<Integer>)params.get("modemId");
        List<Integer> meterId       = (List<Integer>)params.get("meterId");
        
        StringBuffer sb = new StringBuffer();
        sb.append("\n SELECT day.id.yyyymmdd as yyyymmdd, SUM(day.total) AS total                                                                   ");
        sb.append("\n FROM ").append(meterType).append(" day                                                                                         ");
        sb.append("\n WHERE 1=1                                                                                                 ");
        sb.append("\n AND ( day.enddevice.id IN (:endDeviceId ) OR day.meter.id IN (:meterId) OR day.modem.id IN (:modemId) )       ");
        sb.append("\n AND day.id.yyyymmdd between :startDate and :endDate                                                                           ");
        sb.append("\n AND day.id.channel = :channel                                                                                     ");
        sb.append("\n AND day.id.dst = :dst                                                                                         ");
        // sb.append("\n GROUP BY day.id.yyyymmdd ORDER BY day.id.yyyymmdd                 ");
        sb.append("\n GROUP BY day.id.yyyymmdd                ");
        
        Query query  = getSession().createQuery(sb.toString())
                                   .setParameterList("endDeviceId", endDeviceId)
                                   .setParameterList("meterId", meterId)
                                   .setParameterList("modemId", modemId)
                                   .setString("startDate", searchStartDate)
                                   .setString("endDate", searchEndDate)
                                   .setInteger("channel", channel)
                                   .setInteger("dst", dst);

//      Map<String,Object> resultMap = new HashMap<String,Object>();;
//      Map<String,Object> tmp = null;
        List<Object> list = query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
//      for(Object obj:list){
//          tmp = new HashMap<String,Object>();
//          tmp = (Map<String,Object>)obj;
//          resultMap.put((String)tmp.get("yyyymmdd"), tmp.get("total"));
//      }
        
        return list;
    }
    
    /**
     * 특정기간의 검침데이터를 월별로 조회한다.
     * 입력된 EndDeviceId,ModemId,MeterID 목록을 IN 조건으로 조회하며 각각 OR 조건으로 묶는다.
     * @param params - 
     * @return
     */
    public List<Object> getUsageForEndDevicesByMonthPeriod(Map<String,Object> params,Map<String,Object> params2){
         
        Integer channel     = (Integer)params2.get("channel");
        Integer dst         = (Integer)params2.get("dst");
        String meterType    = (String)params2.get("meterType");  
        
        String searchStartDate      = (String)params.get("searchStartDate"); 
        String searchEndDate        = (String)params.get("searchEndDate");
        
        List<Integer> endDeviceId   = (List<Integer>)params.get("endDeviceId");
        List<Integer> modemId       = (List<Integer>)params.get("modemId");
        List<Integer> meterId       = (List<Integer>)params.get("meterId");
        
        //log.debug("params2:"+params2+":params:"+params);
        StringBuffer sb = new StringBuffer();
        sb.append("\n SELECT month.id.yyyymm as yyyymm, SUM(month.total) AS total ");
        sb.append("\n       ,SUM(month.value_01 ) AS value01 ");
        sb.append("\n       ,SUM(month.value_02 ) AS value02 ");
        sb.append("\n       ,SUM(month.value_03 ) AS value03 ");
        sb.append("\n       ,SUM(month.value_04 ) AS value04 ");
        sb.append("\n       ,SUM(month.value_05 ) AS value05 ");
        sb.append("\n       ,SUM(month.value_06 ) AS value06 ");
        sb.append("\n       ,SUM(month.value_07 ) AS value07 ");
        sb.append("\n       ,SUM(month.value_08 ) AS value08 ");
        sb.append("\n       ,SUM(month.value_09 ) AS value09 ");
        sb.append("\n       ,SUM(month.value_10 ) AS value10 ");
        sb.append("\n       ,SUM(month.value_11 ) AS value11 ");
        sb.append("\n       ,SUM(month.value_12 ) AS value12 ");
        sb.append("\n       ,SUM(month.value_13 ) AS value13 ");
        sb.append("\n       ,SUM(month.value_14 ) AS value14 ");
        sb.append("\n       ,SUM(month.value_15 ) AS value15 ");
        sb.append("\n       ,SUM(month.value_16 ) AS value16 ");
        sb.append("\n       ,SUM(month.value_17 ) AS value17 ");
        sb.append("\n       ,SUM(month.value_18 ) AS value18 ");
        sb.append("\n       ,SUM(month.value_19 ) AS value19 ");
        sb.append("\n       ,SUM(month.value_20 ) AS value20 ");
        sb.append("\n       ,SUM(month.value_21 ) AS value21 ");
        sb.append("\n       ,SUM(month.value_22 ) AS value22 ");
        sb.append("\n       ,SUM(month.value_23 ) AS value23 ");
        sb.append("\n       ,SUM(month.value_24 ) AS value24 ");
        sb.append("\n       ,SUM(month.value_25 ) AS value25 ");
        sb.append("\n       ,SUM(month.value_26 ) AS value26 ");
        sb.append("\n       ,SUM(month.value_27 ) AS value27 ");
        sb.append("\n       ,SUM(month.value_28 ) AS value28 ");
        sb.append("\n       ,SUM(month.value_29 ) AS value29 ");
        sb.append("\n       ,SUM(month.value_30 ) AS value30 ");
        sb.append("\n       ,SUM(month.value_31 ) AS value31 ");
        sb.append("\n FROM ").append(meterType).append(" month ");
        sb.append("\n WHERE 1=1 ");
        sb.append("\n AND ( month.enddevice.id IN (:endDeviceId ) OR month.meter.id IN (:meterId) OR month.modem.id IN (:modemId) ) ");
        sb.append("\n AND month.id.yyyymm between :startDate and :endDate ");
        sb.append("\n AND month.id.channel = :channel ");
        sb.append("\n AND month.id.dst = :dst ");
        // sb.append("\n GROUP BY month.id.yyyymm ORDER BY month.id.yyyymm ");
        sb.append("\n GROUP BY month.id.yyyymm");
        //log.debug("sb.toString():"+sb.toString());
        Query query  = getSession().createQuery(sb.toString())
                                   .setParameterList("endDeviceId", endDeviceId)
                                   .setParameterList("meterId", meterId)
                                   .setParameterList("modemId", modemId)
                                   .setString("startDate", searchStartDate)
                                   .setString("endDate", searchEndDate)
                                   .setInteger("channel", channel)
                                   .setInteger("dst", dst);

//      Map<String,Object> resultMap = new HashMap<String,Object>();;
//      Map<String,Object> tmp = null;
        List<Object> list = query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
//      for(Object obj:list){
//          tmp = new HashMap<String,Object>();
//          tmp = (Map<String,Object>)obj;
//          resultMap.put((String)tmp.get("yyyymm"), tmp.get("total"));
//      }
        
        return list;
    }

    public List<Object> getCustomerUsageEmDaily(String METER_TYPE, Map<String, Object> condition){

        
        String sUserId  = StringUtil.nullToBlank(condition.get("sUserId"));
        String sStart   = StringUtil.nullToBlank(condition.get("sStart"));
        String sEnd     = StringUtil.nullToBlank(condition.get("sEnd"));
        String iMdev_type   = StringUtil.nullToBlank(condition.get("iMdev_type"));
        Integer contractId   = (Integer)condition.get("contractId");
        
        String dayClassName = CommonConstants.MeterType.valueOf(METER_TYPE).getDayClassName();
        
        List<Object> result      = new ArrayList<Object>();
        
        StringBuffer sbQuery = new StringBuffer();
        
        sbQuery.append("\n SELECT  de.id.yyyymmdd as yyyymmdd "); 
        sbQuery.append("\n , de.total as usage  , '-' as price  ");
        sbQuery.append("\n FROM Contract ct, ").append(dayClassName).append(" de      ");
        sbQuery.append("\n WHERE ct.customer.id = :sUserId            ");
        sbQuery.append("\n AND de.contract.id=ct.id       ");
        sbQuery.append("\n AND de.id.mdevType = :iMdev_type         ");
        sbQuery.append("\n AND de.id.yyyymmdd between :sStart  and :sEnd  ");
        sbQuery.append("\n AND de.id.channel = 1    ");
        if (contractId != -1 && contractId != null) {
            sbQuery.append("\n AND ct.id = :contractId ");
        }
        sbQuery.append("\n ORDER BY de.id.yyyymmdd asc ");
        
        Query query = getSession().createQuery(sbQuery.toString());
        
        if(contractId != -1 && contractId != null) {
            query.setInteger("contractId", contractId);
        }
        query.setInteger("sUserId", Integer.parseInt( sUserId));
        query.setString("iMdev_type", iMdev_type);
        query.setString("sStart", sStart );
        query.setString("sEnd", sEnd );
        
        result =  query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();

        return result;
    }
    
    
    public List<Object> getCustomerCO2Daily(Map<String, Object> condition){

        
        String sUserId  = StringUtil.nullToBlank(condition.get("sUserId"));
        String sDate    = StringUtil.nullToBlank(condition.get("sDate"));
        String iMdev_type   = StringUtil.nullToBlank(condition.get("iMdev_type"));
        String METER_TYPE   = StringUtil.nullToBlank(condition.get("METER_TYPE"));
        
        String dayClassName = CommonConstants.MeterType.valueOf(METER_TYPE).getDayClassName();
        
        List<Object> result      = new ArrayList<Object>();
        
        StringBuffer sbQuery = new StringBuffer();
        
        sbQuery.append("\n SELECT  de.id.yyyymmdd as yyyymmdd "); 
        sbQuery.append("\n , de.total as co2    ");
        sbQuery.append("\n FROM Contract ct, ").append(dayClassName).append(" de      ");
        sbQuery.append("\n WHERE ct.customer.id = :sUserId       ");
        sbQuery.append("\n AND de.contract.id = ct.id       ");
        sbQuery.append("\n AND de.id.mdevType = :iMdev_type         ");
        sbQuery.append("\n AND de.id.yyyymmdd = :sDate  ");
        sbQuery.append("\n AND de.id.channel = 0    ");
        
        Query query = getSession().createQuery(sbQuery.toString());
        
        query.setInteger("sUserId", Integer.parseInt( sUserId));
        query.setString("iMdev_type", iMdev_type);
        query.setString("sDate", sDate );
        
        result =  query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();

        return result;
    }
    
    @SuppressWarnings("unused")
    public List<Object> getCustomerUsageEmMonthly(String METER_TYPE, Map<String, Object> condition){
        String sUserId  = StringUtil.nullToBlank(condition.get("sUserId"));
        String sStart   = StringUtil.nullToBlank(condition.get("sStart"));
        String sEnd     = StringUtil.nullToBlank(condition.get("sEnd"));
        String iMdev_type   = StringUtil.nullToBlank(condition.get("iMdev_type"));
        String sLastYear    = StringUtil.nullToBlank(condition.get("sLastYear"));
        Integer contractId   = (Integer)condition.get("contractId");
        
        String monthClassName = CommonConstants.MeterType.valueOf(METER_TYPE).getMonthClassName();
        
        List<Object> result      = new ArrayList<Object>();

        StringBuffer sbQuery = new StringBuffer();
        
        sbQuery.append("\n SELECT  de.id.yyyymm as yyyymmdd "); 
        sbQuery.append("\n , de.total as usage  , '-' as price");
        sbQuery.append("\n FROM Contract ct, ").append(monthClassName).append(" de        ");
        sbQuery.append("\n WHERE ct.customer.id = :sUserId       ");
        sbQuery.append("\n AND ct.id=de.contract.id      ");
        sbQuery.append("\n AND de.id.mdevType = :iMdev_type         ");
        sbQuery.append("\n AND de.id.channel = 1    ");
        sbQuery.append("\n AND de.id.yyyymm between :sStart and :sEnd   ");
        // sbQuery.append("\n ORDER BY de.id.yyyymm asc ");
        if (contractId != -1 && contractId != null) {
            sbQuery.append("\n AND ct.id = :contractId ");
        }
        
        Query query = getSession().createQuery(sbQuery.toString());
        if(contractId != -1 && contractId != null) {
            query.setInteger("contractId", contractId);   
        }
        query.setInteger("sUserId", Integer.parseInt( sUserId));
        query.setString("iMdev_type", iMdev_type);
        query.setString("sStart", sStart );
        query.setString("sEnd", sEnd );
        
        result =  query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
        
        return result;
    }
    
    public List<Object> getCustomerUsageEmYearly(String METER_TYPE, Map<String, Object> condition){
        String sUserId  = StringUtil.nullToBlank(condition.get("sUserId"));
        String sStart   = StringUtil.nullToBlank(condition.get("sStart"));
        String sEnd     = StringUtil.nullToBlank(condition.get("sEnd"));
        String iMdev_type   = StringUtil.nullToBlank(condition.get("iMdev_type"));
        Integer contractId   = (Integer)condition.get("contractId");
        
        String monthClassName = CommonConstants.MeterType.valueOf(METER_TYPE).getMonthClassName();
        
        List<Object> result      = new ArrayList<Object>();
        

        StringBuffer sbQuery = new StringBuffer();
        
        sbQuery.append("\n SELECT   de.id.yyyymm as yyyymmdd, de.total as usage, "); 
        sbQuery.append("\n '-' as price     ");
        sbQuery.append("\n FROM Contract ct, ").append(monthClassName).append(" de        ");
        sbQuery.append("\n WHERE ct.customer.id = :sUserId       ");
        sbQuery.append("\n AND ct.id= de.contract.id       ");
        sbQuery.append("\n AND de.id.mdevType = :iMdev_type         ");
        sbQuery.append("\n AND de.id.yyyymm between :sStart and :sEnd");
        sbQuery.append("\n AND de.id.channel = 1    ");
        if (contractId != -1 && contractId != null) {
            sbQuery.append("\n AND ct.id = :contractId ");
        }
        // sbQuery.append("\n ORDER BY de.id.yyyymm asc ");
        
        Query query = getSession().createQuery(sbQuery.toString());
        
        if(contractId != -1 && contractId != null) {
            query.setInteger("contractId", contractId);
        }
        query.setInteger("sUserId", Integer.parseInt( sUserId));
        query.setString("iMdev_type", iMdev_type);
        query.setString("sStart", sStart );
        query.setString("sEnd", sEnd );
        
        result =  query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
        
        return result;
    }
    
    public List<Object> getCustomerUsageEmHourly(String METER_TYPE, Map<String, Object> condition){
        String sUserId  = StringUtil.nullToBlank(condition.get("sUserId"));
        String sEnd     = StringUtil.nullToBlank(condition.get("sEnd"));
        String iMdev_type   = StringUtil.nullToBlank(condition.get("iMdev_type"));
        Integer contractId = (Integer)condition.get("contractId");
        
        String dayClassName = CommonConstants.MeterType.valueOf(METER_TYPE).getDayClassName();
        
        List<Object> result      = new ArrayList<Object>();

        StringBuffer sbQuery = new StringBuffer();

        sbQuery.append("\n SELECT ");
        sbQuery.append("\n de.value_00 as val00, ");
        sbQuery.append("\n de.value_01 as val01, ");
        sbQuery.append("\n de.value_02 as val02, ");
        sbQuery.append("\n de.value_03 as val03, ");
        sbQuery.append("\n de.value_04 as val04, ");
        sbQuery.append("\n de.value_05 as val05, ");
        sbQuery.append("\n de.value_06 as val06, ");
        sbQuery.append("\n de.value_07 as val07, ");
        sbQuery.append("\n de.value_08 as val08, ");
        sbQuery.append("\n de.value_09 as val09, ");
        sbQuery.append("\n de.value_10 as val10, ");
        sbQuery.append("\n de.value_11 as val11, ");
        sbQuery.append("\n de.value_12 as val12, ");
        sbQuery.append("\n de.value_13 as val13, ");
        sbQuery.append("\n de.value_14 as val14, ");
        sbQuery.append("\n de.value_15 as val15, ");
        sbQuery.append("\n de.value_16 as val16, ");
        sbQuery.append("\n de.value_17 as val17, ");
        sbQuery.append("\n de.value_18 as val18, ");
        sbQuery.append("\n de.value_19 as val19, ");
        sbQuery.append("\n de.value_20 as val20, ");
        sbQuery.append("\n de.value_21 as val21, ");
        sbQuery.append("\n de.value_22 as val22, ");
        sbQuery.append("\n de.value_23 as val23  ");
        sbQuery.append("\n FROM Contract ct, ").append(dayClassName).append(" de      ");
        sbQuery.append("\n WHERE ct.customer.id = :sUserId            ");
        sbQuery.append("\n AND ct.id= de.contract.id ");
        sbQuery.append("\n AND de.id.mdevType = :iMdev_type         ");
        sbQuery.append("\n AND de.id.yyyymmdd = :sEnd   ");
        sbQuery.append("\n AND de.id.channel = 1    ");
        if (contractId != -1 && contractId != null) {
            sbQuery.append("\n AND ct.id = :contractId ");
        }
        
        Query query = getSession().createQuery(sbQuery.toString());
        if(contractId != -1 && contractId != null) {
            query.setInteger("contractId", contractId);
        }
        query.setInteger("sUserId", Integer.parseInt( sUserId));
        query.setString("iMdev_type", iMdev_type);
        query.setString("sEnd", sEnd );
        
        result =  query.setResultTransformer(Transformers.TO_LIST).list();

        return result;
    }
    
    public Map<String, Object> getCustomerUsageFee(Map<String, Object> condition){
        String sUserId  = StringUtil.nullToBlank(condition.get("sUserId"));
        String sStart   = StringUtil.nullToBlank(condition.get("sStart"));
        String sEnd     = StringUtil.nullToBlank(condition.get("sEnd"));
        String iMdev_type   = StringUtil.nullToBlank(condition.get("iMdev_type"));
        String sLastYear    = StringUtil.nullToBlank(condition.get("sLastYear"));
        
//      sStart  = "201005";
//      sEnd    = "201004";
//      sLastYear   = "200905";
        
        Map<String, Object> resultMap   = new HashMap<String, Object>();

        StringBuffer sbQuery = new StringBuffer();
        
        Query query = null;
        
        sbQuery.append("\n SELECT  de.id.yyyymm as yyyymmdd "); 
        sbQuery.append("\n , de.total as usage      ");
        sbQuery.append("\n , '0' as price   ");
        sbQuery.append("\n , de.baseValue as basevalue  ");
        sbQuery.append("\n FROM Operator o , Role r, Customer c, Contract ct, Meter m, ").append(CommonConstants.MeterType.valueOf("EnergyMeter").getMonthClassName()).append(" de      ");
        sbQuery.append("\n WHERE o.id = :sUserId            ");
        sbQuery.append("\n AND o.loginId = c.loginId        ");
        sbQuery.append("\n AND o.role.id = r.id         ");
        sbQuery.append("\n AND r.customerRole = 1       ");
        sbQuery.append("\n AND c.id = ct.customer.id        ");
        sbQuery.append("\n AND m.id = ct.meter.id       ");
        sbQuery.append("\n AND de.meter.id = m.id       ");
        sbQuery.append("\n AND de.id.mdevType = :iMdev_type         ");
        sbQuery.append("\n AND de.id.yyyymm IN ( :sStart , :sEnd , :sLastYear )     ");
        sbQuery.append("\n AND de.id.channel = 1    ");
        // sbQuery.append("\n ORDER BY  de.id.yyyymm DESC      ");
        
        query = getSession().createQuery(sbQuery.toString());
        
        query.setInteger("sUserId", Integer.parseInt( sUserId));
        query.setString("iMdev_type", iMdev_type);
        query.setString("sStart", sStart );
        query.setString("sEnd", sEnd );
        query.setString("sLastYear", sLastYear );
        
        resultMap.put("usageFeeEm", query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list());
        
        query   = null;
        sbQuery = new StringBuffer();
        
        sbQuery.append("\n SELECT  de.id.yyyymm as yyyymmdd "); 
        sbQuery.append("\n , de.total as usage      ");
        sbQuery.append("\n , '0' as price   ");
        sbQuery.append("\n , de.baseValue as basevalue  ");
        sbQuery.append("\n FROM Operator o , Role r, Customer c, Contract ct, Meter m, ").append(CommonConstants.MeterType.valueOf("GasMeter").getMonthClassName()).append(" de     ");
        sbQuery.append("\n WHERE o.id = :sUserId            ");
        sbQuery.append("\n AND o.loginId = c.loginId        ");
        sbQuery.append("\n AND o.role.id = r.id         ");
        sbQuery.append("\n AND r.customerRole = 1       ");
        sbQuery.append("\n AND c.id = ct.customer.id        ");
        sbQuery.append("\n AND m.id = ct.meter.id       ");
        sbQuery.append("\n AND de.meter.id = m.id       ");
        sbQuery.append("\n AND de.id.mdevType = :iMdev_type         ");
        sbQuery.append("\n AND de.id.yyyymm IN ( :sStart , :sEnd , :sLastYear )     ");
        sbQuery.append("\n AND de.id.channel = 1    ");
        // sbQuery.append("\n ORDER BY  de.id.yyyymm DESC      ");
        
        query = getSession().createQuery(sbQuery.toString());
        
        query.setInteger("sUserId", Integer.parseInt( sUserId));
        query.setString("iMdev_type", iMdev_type);
        query.setString("sStart", sStart );
        query.setString("sEnd", sEnd );
        query.setString("sLastYear", sLastYear );
        
        resultMap.put("usageFeeGm", query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list());
        
        query   = null;
        sbQuery = new StringBuffer();
        
        sbQuery.append("\n SELECT  de.id.yyyymm as yyyymmdd "); 
        sbQuery.append("\n , de.total as usage      ");
        sbQuery.append("\n , '0' as price   ");
        sbQuery.append("\n , de.baseValue as basevalue  ");
        sbQuery.append("\n FROM Operator o , Role r, Customer c, Contract ct, Meter m, ").append(CommonConstants.MeterType.valueOf("WaterMeter").getMonthClassName()).append(" de       ");
        sbQuery.append("\n WHERE o.id = :sUserId            ");
        sbQuery.append("\n AND o.loginId = c.loginId        ");
        sbQuery.append("\n AND o.role.id = r.id         ");
        sbQuery.append("\n AND r.customerRole = 1       ");
        sbQuery.append("\n AND c.id = ct.customer.id        ");
        sbQuery.append("\n AND m.id = ct.meter.id       ");
        sbQuery.append("\n AND de.meter.id = m.id       ");
        sbQuery.append("\n AND de.id.mdevType = :iMdev_type         ");
        sbQuery.append("\n AND de.id.yyyymm IN ( :sStart , :sEnd , :sLastYear )     ");
        sbQuery.append("\n AND de.id.channel = 1    ");
        // sbQuery.append("\n ORDER BY  de.id.yyyymm DESC      ");
        
        query = getSession().createQuery(sbQuery.toString());
        
        query.setInteger("sUserId", Integer.parseInt( sUserId));
        query.setString("iMdev_type", iMdev_type);
        query.setString("sStart", sStart );
        query.setString("sEnd", sEnd );
        query.setString("sLastYear", sLastYear );
        
        resultMap.put("usageFeeWm", query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list());
        
        query   = null;
        sbQuery = null;
        
        return resultMap;
    }

    
    /*
     * 검침데이터 상세 일별 데이터
     */
    @Deprecated
    public List<Object> getDetailDaySearchData(HashMap<String, Object> condition) {
        
        Query query = null;
        try {
            
        String meterType   = (String)condition.get("meterType");
        String beginDate   = (String)condition.get("beginDate");
        String endDate     = (String)condition.get("endDate");
        Integer meterId    = (Integer)condition.get("meterId");
        List<Integer> arrChannel = (List<Integer>)condition.get("arrChannel");
        
//      logger.info("\n====conditions====\n"+condition);
        //log.debug("arrChannel size :"+arrChannel.size());

        String dayTable = MeterType.valueOf(meterType).getDayClassName();
                
        StringBuffer sb = new StringBuffer();
      
        sb.append("\n SELECT  day.id.yyyymmdd, day.id.channel, day.total ");
        sb.append("\n FROM    ").append(dayTable).append(" day");
        sb.append("\n WHERE   day.meter.id = :meterId            ");
        sb.append("\n AND     day.id.yyyymmdd >= :startDate          ");
        sb.append("\n AND     day.id.yyyymmdd <= :endDate            ");
        sb.append("\n AND     day.id.mdevType = :mdevType            ");
        if(arrChannel.size() > 0){
            sb.append("\n AND      day.id.channel IN (:channel)            ");
        }
        sb.append("\n ORDER BY day.id.yyyymmdd , day.id.channel");

        query = getSession().createQuery(sb.toString());
        query.setString("startDate", beginDate);
        query.setString("endDate", endDate);
        query.setInteger("meterId", meterId);
        query.setString("mdevType", CommonConstants.DeviceType.Meter.name());
        
        if(arrChannel.size() > 0){
            query.setParameterList("channel", arrChannel);
        }
        

        }catch(Exception e){
            log.error(e, e);
        }
        
        return query.list();
    }
    
    
    @SuppressWarnings("unused")
    public List<Object> getCalendarDetailDaySearchData(HashMap<String, Object> condition){
        Query query = null;
        try {
            
            String meterType   = (String)condition.get("meterType");
            String beginDate   = (String)condition.get("beginDate");
            String endDate     = (String)condition.get("endDate");
            Integer meterId    = (Integer)condition.get("meterId");
            List<Integer> arrChannel = (List<Integer>)condition.get("arrChannel");
            
//          logger.info("\n====conditions====\n"+condition);
            //log.debug("arrChannel size :"+arrChannel.size());
    
            String dayTable = MeterType.valueOf(meterType).getDayClassName();
                    
            StringBuffer sb = new StringBuffer();
          
            sb.append("\n SELECT day.id.yyyymmdd AS yyyymmdd  ");
            sb.append("\n       ,day.value_00  AS value_00 ");
            sb.append("\n       ,day.value_01  AS value_01 ");
            sb.append("\n       ,day.value_02  AS value_02 ");
            sb.append("\n       ,day.value_03  AS value_03 ");
            sb.append("\n       ,day.value_04  AS value_04 ");
            sb.append("\n       ,day.value_05  AS value_05 ");
            sb.append("\n       ,day.value_06  AS value_06 ");
            sb.append("\n       ,day.value_07  AS value_07 ");
            sb.append("\n       ,day.value_08  AS value_08 ");
            sb.append("\n       ,day.value_09  AS value_09 ");
            sb.append("\n       ,day.value_10  AS value_10 ");
            sb.append("\n       ,day.value_11  AS value_11 ");
            sb.append("\n       ,day.value_12  AS value_12 ");
            sb.append("\n       ,day.value_13  AS value_13 ");
            sb.append("\n       ,day.value_14  AS value_14 ");
            sb.append("\n       ,day.value_15  AS value_15 ");
            sb.append("\n       ,day.value_16  AS value_16 ");
            sb.append("\n       ,day.value_17  AS value_17 ");
            sb.append("\n       ,day.value_18  AS value_18 ");
            sb.append("\n       ,day.value_19  AS value_19 ");
            sb.append("\n       ,day.value_20  AS value_20 ");
            sb.append("\n       ,day.value_21  AS value_21 ");
            sb.append("\n       ,day.value_22  AS value_22 ");
            sb.append("\n       ,day.value_23  AS value_23 ");
            sb.append("\n FROM    ").append(dayTable).append(" day");
            sb.append("\n WHERE   day.meter.id = :meterId ");
            sb.append("\n AND     day.id.yyyymmdd >= :startDate ");
            sb.append("\n AND     day.id.yyyymmdd <= :endDate ");
            sb.append("\n AND     day.id.mdevType = :mdevType ");
            sb.append("\n AND      day.id.channel = 1 ");
            
//          if(arrChannel.size() > 0){
//              sb.append("\n AND      day.id.channel IN (:channel) ");
//          }
            sb.append("\n ORDER BY day.id.yyyymmdd , day.id.channel");
    
            query = getSession().createQuery(sb.toString());
            query.setString("startDate", beginDate);
            query.setString("endDate", endDate);
            query.setInteger("meterId", meterId);
            query.setParameter("mdevType", CommonConstants.DeviceType.Meter);
            
//          query.setParameter("mdevType", CommonConstants.DeviceType.Modem);
//          log.debug("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
//          log.debug("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
//          log.debug("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
//          log.debug("임시로 미터 타입을 Modem으로 세팅...");
//          log.debug("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
//          log.debug("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
//          log.debug("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
            
//          if(arrChannel.size() > 0){
//              query.setParameterList("channel", arrChannel, Hibernate.INTEGER);
//          }
        

        }catch(Exception e){
            //log.debug("##### MeteringDayDaoImpl.java => getCalendarDetailDaySearchData() exception~");
            log.error(e, e);
        }
        
        return query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
    }
    
    /*
     * 검침데이터 상세 최대/최소/평균/합계 일별 데이터
     */
    @Deprecated
    public List<Object> getDetailDayMaxMinAvgSumData(HashMap<String, Object> condition) {
        
        Query query = null;
        try {
            
        String meterType   = (String)condition.get("meterType");
        String beginDate   = (String)condition.get("beginDate");
        String endDate     = (String)condition.get("endDate");
        Integer meterId    = (Integer)condition.get("meterId");
        List<Integer> arrChannel = ((List<Integer>)condition.get("arrChannel"));

//      logger.info("\n====conditions====\n"+condition);

        String dayTable = MeterType.valueOf(meterType).getDayClassName();
                
        StringBuffer sb = new StringBuffer();
      
        sb.append("\n SELECT  day.id.channel, MIN(day.total) , MAX(day.total) , AVG(day.total) , SUM(day.total) " +
                " ");
        sb.append("\n FROM    ").append(dayTable).append(" day");
        sb.append("\n WHERE   day.meter.id = :meterId            ");
        sb.append("\n AND     day.id.yyyymmdd >= :startDate          ");
        sb.append("\n AND     day.id.yyyymmdd <= :endDate            ");
        sb.append("\n AND     day.id.mdevType = :mdevType            ");
        if(arrChannel.size() > 0){
            sb.append("\n AND      day.id.channel IN (:channel)            ");
        }
        sb.append("\n GROUP BY   day.id.channel            ");
        sb.append("\n ORDER BY   day.id.channel            ");

        query = getSession().createQuery(sb.toString());
        query.setString("startDate", beginDate);
        query.setString("endDate", endDate);
        query.setInteger("meterId", meterId);
        query.setString("mdevType", CommonConstants.DeviceType.Meter.name());
        
        if(arrChannel.size() > 0){
            query.setParameterList("channel", arrChannel);
        }
        

        }catch(Exception e){
            log.error(e, e);
        }
        
        return query.list();
    }
    public List<Object> getTemperatureHumidityLocation(String meterType) {
        
        StringBuffer sb = new StringBuffer();
        
        sb.append("\n SELECT lm.LOCATIONID AS LOCATIONID  ");
        sb.append("\n FROM ").append(meterType).append(" day   ");
        sb.append("\n INNER JOIN (SELECT m.MDS_ID AS MDS_ID,l.ID AS LOCATIONID FROM METER m INNER JOIN LOCATION l on l.ID = m.LOCATION_ID) lm on day.MDEV_ID =lm.MDS_ID");
        
        Query query  = getSession().createSQLQuery(sb.toString());
        
        return query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
    }


    public List<Object> getTemperatureHumidityForLocationByDay(Map<String, Object> params) {

        Integer dst = (Integer) params.get("dst");
        String meterType = (String) params.get("meterType");
        String periodType = (String) params.get("periodType");

        String startDate = (String) params.get("startDate");
        String endDate = (String) params.get("endDate");
        Integer locationId = ((Number) params.get("tLocationId")).intValue();

        StringBuffer sb = new StringBuffer();
        sb.append("\n SELECT day.yyyymmdd AS YYYYMMDD, day.maximumValue AS MAXIMUMVALUE,day.minimumValue AS MINIMUMVALUE ");
        sb.append("\n       ,day.VALUE_00  AS VALUE00 ");
        sb.append("\n       ,day.VALUE_01  AS VALUE01 ");
        sb.append("\n       ,day.VALUE_02  AS VALUE02 ");
        sb.append("\n       ,day.VALUE_03  AS VALUE03 ");
        sb.append("\n       ,day.VALUE_04  AS VALUE04 ");
        sb.append("\n       ,day.VALUE_05  AS VALUE05 ");
        sb.append("\n       ,day.VALUE_06  AS VALUE06 ");
        sb.append("\n       ,day.VALUE_07  AS VALUE07 ");
        sb.append("\n       ,day.VALUE_08  AS VALUE08 ");
        sb.append("\n       ,day.VALUE_09  AS VALUE09 ");
        sb.append("\n       ,day.VALUE_10  AS VALUE10 ");
        sb.append("\n       ,day.VALUE_11  AS VALUE11 ");
        sb.append("\n       ,day.VALUE_12  AS VALUE12 ");
        sb.append("\n       ,day.VALUE_13  AS VALUE13 ");
        sb.append("\n       ,day.VALUE_14  AS VALUE14 ");
        sb.append("\n       ,day.VALUE_15  AS VALUE15 ");
        sb.append("\n       ,day.VALUE_16  AS VALUE16 ");
        sb.append("\n       ,day.VALUE_17  AS VALUE17 ");
        sb.append("\n       ,day.VALUE_18  AS VALUE18 ");
        sb.append("\n       ,day.VALUE_19  AS VALUE19 ");
        sb.append("\n       ,day.VALUE_20  AS VALUE20 ");
        sb.append("\n       ,day.VALUE_21  AS VALUE21 ");
        sb.append("\n       ,day.VALUE_22  AS VALUE22 ");
        sb.append("\n       ,day.VALUE_23  AS VALUE23 ");
        sb.append("\n FROM ").append(meterType).append(" day ");
        sb.append("\n inner join (select m.mds_id as mds_id,l.id as locationId from meter m inner join location l on l.id = m.location_id and location_id=:locationId) lm on day.mdev_id =lm.mds_id ");
        // sb.append("\n inner join meter lm on day.mdev_id =lm.mds_id");
        sb.append("\n WHERE 1 =1 ");
        if (CommonConstants.DateType.HOURLY.getCode().equals(periodType)) {
            sb.append("\n AND day.yyyymmdd = :endDate ");
        } else {
            sb.append("\n AND day.yyyymmdd between :startDate and :endDate ");
        }
        sb.append("\n AND day.dst = :dst ");

        Query query = getSession().createSQLQuery(sb.toString());
        query.setInteger("locationId", locationId);

        if (CommonConstants.DateType.HOURLY.getCode().equals(periodType)) {
            query.setString("endDate", endDate);
        } else {
            query.setString("startDate", startDate);
            query.setString("endDate", endDate);
        }
        query.setInteger("dst", dst);

        return query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
    }

    public List<Object> getTemperatureHumidityForLocationByMonth(
            Map<String, Object> params) {
        
        Integer dst = (Integer)params.get("dst");
        String meterType = (String)params.get("meterType"); 
        
        String startDate = (String)params.get("startDate");
        String endDate = (String)params.get("endDate"); 
        Integer locationId = ((Number)params.get("tLocationId")).intValue();
    
        StringBuffer sb = new StringBuffer();
        sb.append("\n SELECT month.yyyymm AS YYYYMM ,month.maximumValue AS MAXIMUMVALUE, month.minimumValue AS MINIMUMVALUE           ");
        sb.append("\n FROM ").append(meterType).append(" month                                                             ");
        sb.append("\n inner join (select m.mds_id as mds_id,l.id as locationId from meter m inner join location l on l.id = m.location_id and location_id=:locationId) lm on month.mdev_id =lm.mds_id");
        sb.append("\n WHERE 1 =1                                                                  ");
        sb.append("\n AND month.yyyymm between :startDate and :endDate                                               ");
        sb.append("\n AND month.dst = :dst                                                                                         ");
        
        Query query  = getSession().createSQLQuery(sb.toString());
        query.setInteger("locationId", locationId);
    
        query.setInteger("dst", dst);
        query.setString("startDate", startDate);
        query.setString("endDate", endDate);
        
        
        
        return query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
    }

    
    public List<Object> getUsageForExhibitionTotalByDay(Map<String, Object> params) {
        Integer dst = (Integer)params.get("dst");
        Integer channel = (Integer)params.get("channel");
        String meterType = (String)params.get("meterType"); 
        String periodType = (String)params.get("periodType"); 
    
        String startDate = (String)params.get("startDate");
        String endDate = (String)params.get("endDate"); 

        StringBuffer sb = new StringBuffer();
        sb.append("\n SELECT day.yyyymmdd AS yyyymmdd,SUM(day.total) AS total                                            ");
        sb.append("\n       ,SUM(day.value_00)  AS value00                                                                   ");
        sb.append("\n       ,SUM(day.value_01)  AS value01                                                                    ");
        sb.append("\n       ,SUM(day.value_02)  AS value02                                                                    ");
        sb.append("\n       ,SUM(day.value_03)  AS value03                                                                    ");
        sb.append("\n       ,SUM(day.value_04)  AS value04                                                                    ");
        sb.append("\n       ,SUM(day.value_05)  AS value05                                                                    ");
        sb.append("\n       ,SUM(day.value_06)  AS value06                                                                    ");
        sb.append("\n       ,SUM(day.value_07)  AS value07                                                                    ");
        sb.append("\n       ,SUM(day.value_08)  AS value08                                                                    ");
        sb.append("\n       ,SUM(day.value_09)  AS value09                                                                    ");
        sb.append("\n       ,SUM(day.value_10)  AS value10                                                                    ");
        sb.append("\n       ,SUM(day.value_11)  AS value11                                                                    ");
        sb.append("\n       ,SUM(day.value_12)  AS value12                                                                    ");
        sb.append("\n       ,SUM(day.value_13)  AS value13                                                                    ");
        sb.append("\n       ,SUM(day.value_14)  AS value14                                                                    ");
        sb.append("\n       ,SUM(day.value_15)  AS value15                                                                    ");
        sb.append("\n       ,SUM(day.value_16)  AS value16                                                                    ");
        sb.append("\n       ,SUM(day.value_17)  AS value17                                                                    ");
        sb.append("\n       ,SUM(day.value_18)  AS value18                                                                    ");
        sb.append("\n       ,SUM(day.value_19)  AS value19                                                                    ");
        sb.append("\n       ,SUM(day.value_20)  AS value20                                                                    ");
        sb.append("\n       ,SUM(day.value_21)  AS value21                                                                    ");
        sb.append("\n       ,SUM(day.value_22)  AS value22                                                                    ");
        sb.append("\n       ,SUM(day.value_23)  AS value23                                                                    ");
        sb.append("\n FROM ").append(meterType).append(" day                                                             ");
    
        sb.append("\n inner join meter lm on day.mdev_id =lm.mds_id");
        sb.append("\n WHERE 1 =1                                                                  ");
            
        sb.append("\n AND day.dst = ?                                                                                         ");
        sb.append("\n AND day.channel = ?                                                                                         ");
        
        if (CommonConstants.DateType.HOURLY.getCode().equals(periodType)) {
        sb.append("\n AND day.yyyymmdd = ?                                                                           ");
        }else{
            sb.append("\n AND day.yyyymmdd between ? and ?                                               ");
        }
        
        sb.append("\n GROUP BY day.yyyymmdd ORDER BY day.yyyymmdd                 ");
    
        Query query  = getSession().createSQLQuery(sb.toString());
        
        query.setInteger(0, dst);
        query.setInteger(1, channel);

        if (CommonConstants.DateType.HOURLY.getCode().equals(periodType)) {
            query.setString(2, endDate);

        }else{
            query.setString(2, startDate);
            query.setString(3, endDate);
        }   

        return query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
    }
    
    public List<Object> getUsageForExhibitionTotalByMonth(Map<String, Object> params) {
        Integer dst = (Integer)params.get("dst");
        Integer channel = (Integer)params.get("channel");
        String meterType = (String)params.get("meterType"); 
        String periodType = (String)params.get("periodType"); 
    
        String startDate = (String)params.get("startDate");
        String endDate = (String)params.get("endDate"); 

        StringBuffer sb = new StringBuffer();
        sb.append("\n SELECT month.yyyymm AS yyyymm,SUM(month.total) AS total                                            ");
        sb.append("\n FROM ").append(meterType).append(" month                                                             ");
    
        sb.append("\n inner join meter lm on month.mdev_id =lm.mds_id");
        sb.append("\n WHERE 1 =1                                                                  ");
            
        sb.append("\n AND month.dst = ?                                                                                         ");
        sb.append("\n AND month.channel = ?                                                                                         ");
        
        if (CommonConstants.DateType.HOURLY.getCode().equals(periodType)) {
        sb.append("\n AND month.yyyymm = ?                                                                           ");
        }else{
            sb.append("\n AND month.yyyymm between ? and ?                                               ");
        }
        
        sb.append("\n GROUP BY month.yyyymm ORDER BY month.yyyymm                 ");
    
        Query query  = getSession().createSQLQuery(sb.toString());
        
        query.setInteger(0, dst);
        query.setInteger(1, channel);

        if (CommonConstants.DateType.HOURLY.getCode().equals(periodType)) {
            query.setString(2, endDate);

        }else{
            query.setString(2, startDate);
            query.setString(3, endDate);
        }   

        
        return query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
    }
    
    
    
    public List<Object> getUsageForLocationByDay(Map<String, Object> params) {
        Integer dst = (Integer)params.get("dst");
        Integer channel = (Integer)params.get("channel");
        String meterType = (String)params.get("meterType"); 
        String periodType = (String)params.get("periodType"); 
    
        String startDate = (String)params.get("startDate");
        String endDate = (String)params.get("endDate"); 
        Integer locationId = ((Number)params.get("locationId")).intValue();
        boolean root = (Boolean)params.get("root");
        
        Integer endDeviceId =0;

        StringBuffer sb = new StringBuffer();
        sb.append("\n SELECT day.YYYYMMDD AS YYYYMMDD,SUM(day.TOTAL) AS TOTAL                                           ");
        sb.append("\n       ,SUM(day.VALUE_00)  AS VALUE00                                                                   ");
        sb.append("\n       ,SUM(day.VALUE_01)  AS VALUE01                                                                    ");
        sb.append("\n       ,SUM(day.VALUE_02)  AS VALUE02                                                                    ");
        sb.append("\n       ,SUM(day.VALUE_03)  AS VALUE03                                                                    ");
        sb.append("\n       ,SUM(day.VALUE_04)  AS VALUE04                                                                    ");
        sb.append("\n       ,SUM(day.VALUE_05)  AS VALUE05                                                                    ");
        sb.append("\n       ,SUM(day.VALUE_06)  AS VALUE06                                                                    ");
        sb.append("\n       ,SUM(day.VALUE_07)  AS VALUE07                                                                    ");
        sb.append("\n       ,SUM(day.VALUE_08)  AS VALUE08                                                                    ");
        sb.append("\n       ,SUM(day.VALUE_09)  AS VALUE09                                                                    ");
        sb.append("\n       ,SUM(day.VALUE_10)  AS VALUE10                                                                    ");
        sb.append("\n       ,SUM(day.VALUE_11)  AS VALUE11                                                                    ");
        sb.append("\n       ,SUM(day.VALUE_12)  AS VALUE12                                                                    ");
        sb.append("\n       ,SUM(day.VALUE_13)  AS VALUE13                                                                    ");
        sb.append("\n       ,SUM(day.VALUE_14)  AS VALUE14                                                                    ");
        sb.append("\n       ,SUM(day.VALUE_15)  AS VALUE15                                                                    ");
        sb.append("\n       ,SUM(day.VALUE_16)  AS VALUE16                                                                    ");
        sb.append("\n       ,SUM(day.VALUE_17)  AS VALUE17                                                                    ");
        sb.append("\n       ,SUM(day.VALUE_18)  AS VALUE18                                                                    ");
        sb.append("\n       ,SUM(day.VALUE_19)  AS VALUE19                                                                    ");
        sb.append("\n       ,SUM(day.VALUE_20)  AS VALUE20                                                                    ");
        sb.append("\n       ,SUM(day.VALUE_21)  AS VALUE21                                                                    ");
        sb.append("\n       ,SUM(day.VALUE_22)  AS VALUE22                                                                    ");
        sb.append("\n       ,SUM(day.VALUE_23)  AS VALUE23                                                                    ");
        sb.append("\n FROM ").append(meterType).append(" day                                                             ");
        
        if(params.get("convertMeterId")!=null){
            endDeviceId = (Integer)params.get("convertMeterId");
            sb.append("\n WHERE day.meter_id= ?");
        }else{
            if(root){
            
                sb.append("\n inner join (select * from location where parent_id=?) lm on day.location_id =lm.id");
                sb.append("\n WHERE 1 =1                                                                  ");
                
            }else{
                sb.append("\n inner join (select * from location where id=?) lm on day.location_id =lm.id");
                sb.append("\n WHERE 1 =1                                                                  ");
            }
        }
        sb.append("\n AND day.dst = ?                                                                                         ");
        sb.append("\n AND day.channel = ?                                                                                         ");
        
        if (CommonConstants.DateType.HOURLY.getCode().equals(periodType)) {
        sb.append("\n AND day.yyyymmdd = ?                                                                           ");
        }else{
            sb.append("\n AND day.yyyymmdd between ? and ?                                               ");
        }
        
        sb.append("\n GROUP BY day.yyyymmdd ORDER BY day.yyyymmdd                 ");

        Query query  = getSession().createSQLQuery(sb.toString());
        if(params.get("convertMeterId")!=null){
            query.setInteger(0, endDeviceId);
        }else {
            query.setInteger(0, locationId);
        }
        query.setInteger(1, dst);
        query.setInteger(2, channel);

        if (CommonConstants.DateType.HOURLY.getCode().equals(periodType)) {
            query.setString(3, endDate);
            
        }else{
            query.setString(3, startDate);
            query.setString(4, endDate);

        }   
    
        return query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
    }

    public List<Object> getUsageForLocationByMonth(Map<String, Object> params) {
        Integer dst = (Integer)params.get("dst");
        Integer channel = (Integer)params.get("channel");
        String meterType = (String)params.get("meterType"); 
        String periodType = (String)params.get("periodType"); 
    
        String startDate = (String)params.get("startDate");
        String endDate = (String)params.get("endDate"); 
        Integer locationId = ((Number)params.get("locationId")).intValue();
        boolean root = (Boolean)params.get("root");
        Integer endDeviceId =0;

        StringBuffer sb = new StringBuffer();
        sb.append("\n SELECT month.yyyymm AS YYYYMM,SUM(month.total) AS TOTAL                                            ");
        sb.append("\n ,SUM(month.VALUE_01) AS VALUE01                                            ");
        sb.append("\n ,SUM(month.VALUE_02) AS VALUE02                                            ");
        sb.append("\n ,SUM(month.VALUE_03) AS VALUE03                                            ");
        sb.append("\n ,SUM(month.VALUE_04) AS VALUE04                                            ");
        sb.append("\n ,SUM(month.VALUE_05) AS VALUE05                                            ");
        sb.append("\n ,SUM(month.VALUE_06) AS VALUE06                                            ");
        sb.append("\n ,SUM(month.VALUE_07) AS VALUE07                                            ");
        sb.append("\n ,SUM(month.VALUE_08) AS VALUE08                                            ");
        sb.append("\n ,SUM(month.VALUE_09) AS VALUE09                                            ");
        sb.append("\n ,SUM(month.VALUE_10) AS VALUE10                                            ");
        sb.append("\n ,SUM(month.VALUE_11) AS VALUE11                                            ");
        sb.append("\n ,SUM(month.VALUE_12) AS VALUE12                                            ");
        sb.append("\n ,SUM(month.VALUE_13) AS VALUE13                                            ");
        sb.append("\n ,SUM(month.VALUE_14) AS VALUE14                                            ");
        sb.append("\n ,SUM(month.VALUE_15) AS VALUE15                                            ");
        sb.append("\n ,SUM(month.VALUE_16) AS VALUE16                                            ");
        sb.append("\n ,SUM(month.VALUE_17) AS VALUE17                                            ");
        sb.append("\n ,SUM(month.VALUE_18) AS VALUE18                                            ");
        sb.append("\n ,SUM(month.VALUE_19) AS VALUE19                                            ");
        sb.append("\n ,SUM(month.VALUE_20) AS VALUE20                                            ");
        sb.append("\n ,SUM(month.VALUE_21) AS VALUE21                                            ");
        sb.append("\n ,SUM(month.VALUE_22) AS VALUE22                                            ");
        sb.append("\n ,SUM(month.VALUE_23) AS VALUE23                                            ");
        sb.append("\n ,SUM(month.VALUE_24) AS VALUE24                                            ");
        sb.append("\n ,SUM(month.VALUE_25) AS VALUE25                                            ");
        sb.append("\n ,SUM(month.VALUE_26) AS VALUE26                                            ");
        sb.append("\n ,SUM(month.VALUE_27) AS VALUE27                                            ");
        sb.append("\n ,SUM(month.VALUE_28) AS VALUE28                                            ");
        sb.append("\n ,SUM(month.VALUE_29) AS VALUE29                                            ");
        sb.append("\n ,SUM(month.VALUE_30) AS VALUE30                                            ");
        sb.append("\n ,SUM(month.VALUE_31) AS VALUE31                                           ");
        sb.append("\n FROM ").append(meterType).append(" month                                                             ");
        if(params.get("convertMeterId")!=null){
            endDeviceId = (Integer)params.get("convertMeterId");
            sb.append("\n WHERE month.meter_id= ?");
        }else{
            if(root){

                sb.append("\n inner join (select * from location where parent_id=?) lm on month.location_id =lm.id");
                sb.append("\n WHERE 1 =1                                                                  ");
                
            }else{
                sb.append("\n inner join (select * from location where id=?) lm on month.location_id =lm.id");
                sb.append("\n WHERE 1 =1                                                                  ");
            }
        }

        
        sb.append("\n AND month.dst =?                                                                                         ");
        sb.append("\n AND month.channel = ?                                                                                        ");
        if (CommonConstants.DateType.HOURLY.getCode().equals(periodType)) {
            sb.append("\n AND month.yyyymm = ?                                                                          ");
            }else{
                sb.append("\n AND month.yyyymm between ? and ?                                               ");
            }
        sb.append("\n GROUP BY month.yyyymm ORDER BY month.yyyymm                 ");
        
        Query query  = getSession().createSQLQuery(sb.toString());
        if(params.get("convertMeterId")!=null){
            query.setInteger(0, endDeviceId);
        }else{
            query.setInteger(0, locationId);
        }
        query.setInteger(1, dst);
        query.setInteger(2, channel);

        if (CommonConstants.DateType.HOURLY.getCode().equals(periodType)) {
            
            query.setString(3, endDate);
            
        }else{
            query.setString(3, startDate);
            query.setString(4, endDate);
            
        }
    
        return query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
    }

    
    public List<Object> getUsageForSubLocationByDay(Map<String, Object> params) {
        Integer dst = (Integer)params.get("dst");
        Integer channel = (Integer)params.get("channel");
        String meterType = (String)params.get("meterType"); 
        String periodType = (String)params.get("periodType"); 
    
        String startDate = (String)params.get("startDate");
        String endDate = (String)params.get("endDate"); 
        Integer locationId = ((Number)params.get("locationId")).intValue();
        
        StringBuffer sb = new StringBuffer();
        sb.append("\n SELECT lm.name AS NAME,lm.orderNo AS ORDERNO,SUM(day.total) AS TOTAL                                        ");
        sb.append("\n FROM (SELECT total,mdev_id from ").append(meterType);
        sb.append("\n WHERE dst = :dst                                                                                         ");
        sb.append("\n AND channel = :channel                                                                                         ");
        if (CommonConstants.DateType.HOURLY.getCode().equals(periodType)) {
            sb.append("\n AND yyyymmdd = :endDate                                                                           ");
            }else{
                sb.append("\n AND yyyymmdd between :startDate and :endDate                                               ");
            }
        sb.append("\n ) day                                                             ");
        sb.append("\n right outer join (select m.mds_id as MDS_ID,l.name as NAME ,l.orderNo as ORDERNO from meter m right outer join location l on l.id = m.location_id where l.parent_id=:locationId) lm on lm.mds_id=day.mdev_id                                                                ");
        sb.append("\n WHERE 1 =1                                                                  ");
        
        // sb.append("\n GROUP BY lm.name,lm.orderNo order by lm.orderNo                 ");  
        sb.append("\n GROUP BY lm.name,lm.orderNo              ");
        Query query  = getSession().createSQLQuery(sb.toString());
        query.setInteger("locationId", locationId);
        query.setInteger("channel", channel);
        query.setInteger("dst", dst);
        if (CommonConstants.DateType.HOURLY.getCode().equals(periodType)) {
            query.setString("endDate", endDate); 
        }else{
            query.setString("startDate", startDate);
            query.setString("endDate", endDate);
        }
        
//      log.debug("@@@@@@@@@@:"+sb.toString());
//      
//      log.debug("dst:"+dst);
//      log.debug("channel:"+channel);
//      log.debug("meterType:"+meterType);
//      log.debug("periodType:"+periodType);
//      log.debug("startDate:"+startDate);
//      log.debug("endDate:"+endDate);
//      log.debug("locationId:"+locationId);
        return query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
    }



    public List<Object> getUsageForSubLocationByMonth(Map<String, Object> params) {
        Integer dst = (Integer)params.get("dst");
        Integer channel = (Integer)params.get("channel");
        String meterType = (String)params.get("meterType"); 
        
        String startDate = (String)params.get("startDate");
        String endDate = (String)params.get("endDate"); 
        Integer locationId = ((Number)params.get("locationId")).intValue();
        
        StringBuffer sb = new StringBuffer();
        
        sb.append("\n SELECT lm.name AS NAME,lm.orderNo AS ORDERNO,SUM(month.total) AS TOTAL                                        ");
        sb.append("\n FROM (SELECT total,mdev_id from ").append(meterType);
        sb.append("\n WHERE dst = :dst                                                                                         ");
        sb.append("\n AND channel = :channel                                                                                         ");
        sb.append("\n AND yyyymm between :startDate and :endDate                                               ");
        sb.append("\n ) month                                                             ");
        sb.append("\n right outer join (select m.mds_id as mds_id,l.name as NAME ,l.orderNo as ORDERNO from meter m right outer join location l on l.id = m.location_id where l.parent_id=:locationId) lm on lm.mds_id=month.mdev_id                                                                ");
        sb.append("\n WHERE 1 = 1                                                                  ");
        // sb.append("\n GROUP BY lm.name,lm.orderNo order by lm.orderNo                 "); 
        sb.append("\n GROUP BY lm.name,lm.orderNo                 ");
        Query query  = getSession().createSQLQuery(sb.toString());
        query.setInteger("locationId", locationId);
        query.setInteger("channel", channel);
        query.setInteger("dst", dst);
        query.setString("startDate", startDate);
        query.setString("endDate", endDate);
        //log.debug("@@@@@@@@@@:"+sb.toString());
        return query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
    }

    /*
     * 
     */
    public List<Object> getVEEThresholdData(HashMap<String, Object> hm) {
        
        String thresholdSelectQuery = (String)hm.get("thresholdSelectQuery");
        String thresholdAndQuery1   = (String)hm.get("thresholdAndQuery1");
        String thresholdAndQuery2   = (String)hm.get("thresholdAndQuery2");
        String tableName            = (String)hm.get("tableName");
        
        StringBuffer sb = new StringBuffer();
        sb.append("\n SELECT a.contract_id AS contractId, a.value AS value ");
        sb.append("\n FROM ( ");
        sb.append(thresholdSelectQuery);
        sb.append("\n       FROM ").append(tableName).append("");
        sb.append("\n )a ");
        sb.append("\n WHERE 1=1 ");
        sb.append(thresholdAndQuery1);
        sb.append(thresholdAndQuery2);
        
        Query query  = getSession().createQuery(sb.toString());
        
        return query.list();
    }
    
    /*
     * 
     */
    public List<Object> getVEEValidateCheckMiniDayData(HashMap<String, Object> hm) {
        
        String startDate        = (String)hm.get("startDate");
        String endDate          = (String)hm.get("endDate");
        String tableName        = (String)hm.get("tableName");
        String condition        = (String)hm.get("condition");// 테이블name
        List<Integer> arrContractId = ((List<Integer>)hm.get("arrContractId"));
        
        StringBuffer sb = new StringBuffer();
        sb.append("\n SELECT Distinct day.contract_id AS contractId ");
        sb.append("\n FROM ").append(tableName).append(" day ");
        sb.append("\n WHERE 1=1 ");
        sb.append("\n   AND day.YYYYMMDD between :startDate and :endDate ");
        if(condition != null && condition.length() > 0) {
            sb.append("\n   AND day.total "+condition);
        }
        if(arrContractId.size() > 0){
            sb.append("\n   AND day.contract_id IN (:contractId) ");
        }
        
        Query query  = getSession().createQuery(sb.toString());
        query.setString("startDate", startDate);
        query.setString("endDate", endDate);
        
        if(arrContractId.size() > 0){
            query.setParameterList("contractId", arrContractId, StandardBasicTypes.INTEGER);
        }
        
        return query.list();
    }
    
    
    public List<Map<String, Object>> getDayVEEList(String qry){
        Query query  = getSession().createQuery(qry);
        return query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
    }
    
    public List<Map<String, Object>> getDayVEEListPage(String qry, int startRow, int pageSize){
        Query query  = getSession().createQuery(qry);
        //return query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
        return query.setFirstResult(startRow)
        .setMaxResults(pageSize)
        .setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP)
        .list();
    }
    
    public List<Map<String, Object>> getDayVEEListTotal(String qry){
        Query query  = getSession().createQuery(qry);
        return query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
    }
    
    public List<Map<String, Object>> getBemsFloorUsageReductRankingDay(Map<String, Object> params){
        
        String preStartDate = (String)params.get("preStartDate");       
        String preEndDate = (String)params.get("preEndDate");
        
        String startDate = (String)params.get("startDate");     
        String endDate = (String)params.get("endDate");
        
        String periodType = (String)params.get("periodType");
        
        Integer locationId = ((Number)params.get("locationId")).intValue();
        
        StringBuffer sb = new StringBuffer();
        
        sb.append("\n select * from ( ");
        sb.append("\n select CUR.name as name,CUR.total as curTotal,PRE.total as preTotal,(PRE.total-CUR.total) as reduct ");
                if(periodType.equals(CommonConstants.DateType.HOURLY.getCode())){
                    for(int i=0;i<24;i++){
                        String hh = TimeUtil.to2Digit(i);
                        sb.append("\n ,CURVALUE"+hh);
                        sb.append("\n ,PREVALUE"+hh);
                    }   
                }
        sb.append("\n from ");
        sb.append("\n (select l.id as id,l.name as name,case when sum(d.total) is null then 0 else sum(d.total)  end  as total ");
        if(periodType.equals(CommonConstants.DateType.HOURLY.getCode())){
            for(int i=0;i<24;i++){
                String hh = TimeUtil.to2Digit(i);
                sb.append("\n ,sum(d.value_"+hh+") as CURVALUE"+hh);
            }
        }
        sb.append("\n from location l "); 
        sb.append("\n left outer join (select * ");
        sb.append("\n from day_em where yyyymmdd between :startDate and :endDate and channel=1 ) d "); 
        sb.append("\n on l.id=  location_id where l.parent_id=:locationId group by l.name,l.id) CUR ");
        sb.append("\n inner join ");
        sb.append("\n (select l.id as id,l.name as name,case when sum(d.total) is null then 0 else sum(d.total)  end  as total ");
        if(periodType.equals(CommonConstants.DateType.HOURLY.getCode())){
            for(int i=0;i<24;i++){
                String hh = TimeUtil.to2Digit(i);
                sb.append("\n ,sum(d.value_"+hh+") as PREVALUE"+hh);
            }
        }
        sb.append("\n from location l  ");
        sb.append("\n left outer join (select * from day_em where yyyymmdd between :preStartDate and :preEndDate and channel=1 ) d  ");
        sb.append("\n on l.id=  location_id where l.parent_id=:locationId group by l.name,l.id) PRE on CUR.id=PRE.id)main ");
        sb.append("\n order by main.reduct desc ");
        
        
        
        Query query  = getSession().createSQLQuery(sb.toString()); 
        query.setInteger("locationId", locationId);
        query.setString("startDate", startDate);
        query.setString("endDate", endDate);
        query.setString("preStartDate", preStartDate);
        query.setString("preEndDate", preEndDate);
        //log.debug(params+":@@@@@@@@@@:"+sb.toString());
        return query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
    }
    

    
     public List<Map<String, Object>> getBemsFloorUsageReductRankingMonth(Map<String, Object> params){
        
    
        String startDate = (String)params.get("startDate");     
        String endDate = (String)params.get("endDate");
        
        Integer locationId = ((Number)params.get("locationId")).intValue();
        
        StringBuffer sb = new StringBuffer();
        
        
        
        sb.append("\n select * from ( ");
        sb.append("\n select CUR.name as name,CUR.total as curTotal,PRE.total as preTotal,(PRE.total-CUR.total) as reduct ");
        for(int i=1;i<32;i++){
            String hh = TimeUtil.to2Digit(i);
            sb.append("\n ,CURVALUE"+hh);
            sb.append("\n ,PREVALUE"+hh);
        }   
        sb.append("\n from ");
        sb.append("\n (select l.id as id,l.name as name,case when sum(d.total) is null then 0 else sum(d.total)  end  as total ");
        for(int i=1;i<32;i++){
            String hh = TimeUtil.to2Digit(i);
            sb.append("\n ,sum(d.value_"+hh+") as CURVALUE"+hh);
        }
        sb.append("\n from location l "); 
        sb.append("\n left outer join (select * ");
        sb.append("\n from month_em where yyyymm =:endDate and channel=1 ) d "); 
        sb.append("\n on l.id=  location_id where l.parent_id=:locationId group by l.name,l.id) CUR ");
        sb.append("\n inner join ");
        sb.append("\n (select l.id as id,l.name as name,case when sum(d.total) is null then 0 else sum(d.total)  end  as total ");
        for(int i=1;i<32;i++){
            String hh = TimeUtil.to2Digit(i);
            sb.append("\n ,sum(d.value_"+hh+") as PREVALUE"+hh);
        }
        sb.append("\n from location l  ");
        sb.append("\n left outer join (select * from month_em where yyyymm =:startDate and channel=1 ) d "); 
        sb.append("\n on l.id=  location_id where l.parent_id=:locationId group by l.name,l.id) PRE on CUR.id=PRE.id)main ");
        sb.append("\n order by main.reduct desc ");
        /*
        sb.append("\n select * from ");
        sb.append("\n (select CUR.name as name,CUR.total as curTotal,PRE.total as preTotal,PRE.total-CUR.total as reduct ");
        for(int i=1;i<32;i++){
            String hh = TimeUtil.to2Digit(i);
            sb.append("\n ,CURVALUE"+hh);
            sb.append("\n ,PREVALUE"+hh);
        }       
        sb.append("\n from (select l.id as id,l.name as name,sum(total) as total ");
        for(int i=1;i<32;i++){
            String hh = TimeUtil.to2Digit(i);
            sb.append("\n ,sum(d.value_"+hh+") as CURVALUE"+hh);
        }
        sb.append("\n from month_em d inner join location l on l.id= d.location_id where l.parent_id=:locationId and yyyymm=:endDate and channel=1 group by l.name,l.id) CUR ");
        sb.append("\n inner join (select l.id as id,l.name as name,sum(total) as total ");
        for(int i=1;i<32;i++){
            String hh = TimeUtil.to2Digit(i);
            sb.append("\n ,sum(d.value_"+hh+") as PREVALUE"+hh);
        }
        sb.append("\n from month_em d inner join location l on l.id= d.location_id where l.parent_id=:locationId and yyyymm=:startDate and channel=1 group by l.name,l.id) PRE on CUR.ID=PRE.ID) main ");
        sb.append("\n order by main.reduct desc ");
        */
        Query query  = getSession().createSQLQuery(sb.toString());
        query.setInteger("locationId", locationId);
        query.setString("startDate", startDate);
        query.setString("endDate", endDate);
        //log.debug("params:"+params);
        //log.debug("@@@@@@@@@@:"+sb.toString());
        return query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
    }

    /**
     * method name : getMeteringDataDailyData<b/>
     * method Desc : Metering Data 맥스가젯에서 일별 검침데이터를 조회한다.
     *
     * @param conditionMap
     * @param isTotal
     * @return
     */
    public List<Map<String, Object>> getMeteringDataDailyData(Map<String, Object> conditionMap, boolean isTotal) {
        return getMeteringDataDailyData(conditionMap, isTotal, false);
    }

    /**
     * method name : getMeteringDataDailyData<b/>
     * method Desc : Metering Data 맥스가젯에서 일별 검침데이터를 조회한다.
     *
     * @param conditionMap
     * @param isTotal
     * @param isPrev
     * @return
     */
    public List<Map<String, Object>> getMeteringDataDailyData(Map<String, Object> conditionMap, boolean isTotal, boolean isPrev) {
        List<Map<String, Object>> result;

        Integer supplierId = (Integer)conditionMap.get("supplierId");
        Integer tariffType = (Integer)conditionMap.get("tariffType");
        Integer sicId = (Integer)conditionMap.get("sicId");
        Integer page = (Integer)conditionMap.get("page");
        Integer limit = (Integer)conditionMap.get("limit");

        String contractNumber = StringUtil.nullToBlank(conditionMap.get("contractNumber"));
        String customerName = StringUtil.nullToBlank(conditionMap.get("customerName"));
        String meteringSF = StringUtil.nullToBlank(conditionMap.get("meteringSF"));
        
        String friendlyName = StringUtil.nullToBlank(conditionMap.get("friendlyName"));
        Integer isManualMeter = (Integer) conditionMap.get("isManualMeter");

        String startDate = null;
        String endDate = null;

        String mcuId = StringUtil.nullToBlank(conditionMap.get("mcuId"));
        String deviceType = StringUtil.nullToBlank(conditionMap.get("deviceType"));
        String mdevId = StringUtil.nullToBlank(conditionMap.get("mdevId"));
        String gs1 = StringUtil.nullToBlank(conditionMap.get("gs1"));
        String modemId = StringUtil.nullToBlank(conditionMap.get("modemId"));
        String contractGroup = StringUtil.nullToBlank(conditionMap.get("contractGroup"));
        String meterType = StringUtil.nullToBlank(conditionMap.get("meterType"));
//        String tlbType = StringUtil.nullToBlank(conditionMap.get("tlbType"));
        List<Integer> sicIdList = (List<Integer>)conditionMap.get("sicIdList");
        List<Integer> locationIdList = (List<Integer>)conditionMap.get("locationIdList");
        String DayTableView = MeterType.valueOf(meterType).getDayViewName();

        if (isPrev) {
            startDate = StringUtil.nullToBlank(conditionMap.get("prevStartDate"));
            endDate = StringUtil.nullToBlank(conditionMap.get("prevEndDate"));
        } else {
            startDate = StringUtil.nullToBlank(conditionMap.get("startDate"));
            endDate = StringUtil.nullToBlank(conditionMap.get("endDate"));
        }

        StringBuilder sb = new StringBuilder();

        if (isTotal) {
            sb.append("\nSELECT COUNT(*) AS cnt ");
        }
        else {
            sb.append("\nSELECT contract_number AS CONTRACT_NUMBER, ");
            sb.append("\n       customer_name AS CUSTOMER_NAME, ");
            sb.append("\n       yyyymmdd AS YYYYMMDD, ");
            sb.append("\n       mds_id AS METER_NO, ");
            sb.append("\n       gs1 AS GS1, ");
            sb.append("\n       friendly_name AS FRIENDLY_NAME, ");
    
            if (!isPrev) {
                if (!mcuId.isEmpty()) {
                    sb.append("\n       device_serial AS MODEM_ID, ");
                } else {
                    sb.append("\n       (SELECT md.device_serial FROM modem md WHERE md.id = tb.modem_id ) AS MODEM_ID, ");
                }
            }
            sb.append("\n       (select name from code where id = tb.sic_id) as SIC_NAME, ");
            sb.append("\n       baseValue as BASEVALUE, ");
            sb.append("\n       total as VALUE ");
        }

        sb.append("\nFROM ( ");
        sb.append("\n    SELECT dy.yyyymmdd, ");
        sb.append("\n           mt.mds_id, ");
        sb.append("\n           mt.gs1, ");
        sb.append("\n           mt.friendly_name, ");
        sb.append("\n           co.contract_number, ");
        sb.append("\n           cu.name AS customer_name, ");

        if (!isPrev) {
            if (!mcuId.isEmpty()) {
                sb.append("\n           mo.device_serial, ");
            } else {
                sb.append("\n           mt.modem_id, ");
            }
        }
        sb.append("\n           co.sic_id, ");
        sb.append("\n           baseValue AS baseValue, ");
        sb.append("\n           SUM(dy.total_value) AS total ");

        sb.append("\n    FROM ").append(DayTableView).append(" dy ");
        sb.append("\n         LEFT OUTER JOIN ");
        sb.append("\n         contract co ");
        sb.append("\n         ON co.id = dy.contract_id ");
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
        
        if(!modemId.isEmpty()){
        	sb.append("\n         ,modem mo ");
        }

        sb.append("\n    WHERE dy.yyyymmdd BETWEEN :startDate AND :endDate ");
        sb.append("\n    AND   dy.channel = 1 ");
        sb.append("\n    AND   mt.id = dy.meter_id ");
        sb.append("\n    AND   mt.supplier_id = :supplierId ");

        if (!deviceType.isEmpty()) {
            sb.append("\n    AND   dy.mdev_type = :deviceType ");
        }

        if (meteringSF.equals("s")) {
            sb.append("\n    AND   dy.total_value IS NOT NULL ");
        } else {
            sb.append("\n    AND   dy.total_value IS NULL ");
        }

        if (!mdevId.isEmpty()) {
        	if(mdevId.indexOf('%') == 0 || mdevId.indexOf('%') == (mdevId.length()-1)) { // %문자가 양 끝에 있을경우
                sb.append("\n    AND   mt.mds_id LIKE :mdevId ");
        	}else {
                sb.append("\n    AND   mt.mds_id = :mdevId ");
        	}
        }
        
        if (!gs1.isEmpty()) {
        	if(gs1.indexOf('%') == 0 || gs1.indexOf('%') == (gs1.length()-1)) { // %문자가 양 끝에 있을경우
                sb.append("\n    AND   mt.gs1 LIKE :gs1 ");
        	}else {
                sb.append("\n    AND   mt.gs1 = :gs1 ");
        	}
        }

        // XXX: 수검침 조건
        if (!friendlyName.isEmpty()) {
            sb.append("\n    AND   mt.friendly_name = :friendlyName ");
        }
        if (isManualMeter != null) {
            sb.append("\n    AND   mt.is_manual_meter = :isManualMeter ");
        }

        if (!contractNumber.isEmpty()) {
        	if(contractNumber.indexOf('%') == 0 || contractNumber.indexOf('%') == (contractNumber.length()-1)) { // %문자가 양 끝에 있을경우
                sb.append("\n    AND   co.contract_number LIKE :contractNumber ");
        	}else {
                sb.append("\n    AND   co.contract_number = :contractNumber ");
        	}
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
        	if(customerName.indexOf('%') == 0 || customerName.indexOf('%') == (customerName.length()-1)) { // %문자가 양 끝에 있을경우
                sb.append("\n    AND   cu.name LIKE :customerName ");
        	}else {
                sb.append("\n    AND   cu.name = :customerName ");
        	}
        }

        if (!mcuId.isEmpty()) {
            sb.append("\n    AND   mo.id = mt.modem_id ");
            sb.append("\n    AND   mc.id = mo.mcu_id ");
        	if(mcuId.indexOf('%') == 0 || mcuId.indexOf('%') == (mcuId.length()-1)) { // %문자가 양 끝에 있을경우
            	sb.append("\n    AND   mc.sys_id LIKE :mcuId ");
        	}else {
            	sb.append("\n    AND   mc.sys_id = :mcuId ");
        	}
        }
        
        if(!modemId.isEmpty()){
            sb.append("\n    AND   mt.modem_id = mo.id ");
        	if(modemId.indexOf('%') == 0 || modemId.indexOf('%') == (modemId.length()-1)) { // %문자가 양 끝에 있을경우
                sb.append("\n    AND   mo.device_serial LIKE :modemId ");
        	}else {
                sb.append("\n    AND   mo.device_serial = :modemId ");
        	}
        }

        sb.append("\n    GROUP BY dy.yyyymmdd, ");
        sb.append("\n             mt.mds_id, ");
        sb.append("\n             mt.gs1, ");
        sb.append("\n             mt.friendly_name, ");
        sb.append("\n             co.contract_number, ");
        sb.append("\n             cu.name, ");
        sb.append("\n             co.sic_id, ");
        sb.append("\n             dy.baseValue ");

        if (!isPrev) {
            if (!mcuId.isEmpty()) {
                sb.append("\n             ,mo.device_serial ");
            } else {
                sb.append("\n             ,mt.modem_id ");
            }
        }

        sb.append("\n) tb ");
        
        if(!isTotal) {
            sb.append("\n order by contract_number, yyyymmdd asc");
        }

        SQLQuery query = getSession().createSQLQuery(new SQLWrapper().getQuery(sb.toString()));

        query.setString("startDate", startDate);
        query.setString("endDate", endDate);
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
     * A reform version of "getMeteringDataDailyData"
     * Reduce the duplicated query time.
     */
    public List<Map<String, Object>> getMeteringDataDailyData2(Map<String, Object> conditionMap, boolean isTotal) {
        List<Map<String, Object>> result;

        Integer supplierId = (Integer) conditionMap.get("supplierId");
        Integer tariffType = (Integer) conditionMap.get("tariffType");
        Integer sicId = (Integer) conditionMap.get("sicId");
        Integer page = (Integer) conditionMap.get("page");
        Integer limit = (Integer) conditionMap.get("limit");
        String contractNumber = StringUtil.nullToBlank(conditionMap.get("contractNumber"));
        String customerName = StringUtil.nullToBlank(conditionMap.get("customerName"));
        String meteringSF = StringUtil.nullToBlank(conditionMap.get("meteringSF"));
        String friendlyName = StringUtil.nullToBlank(conditionMap.get("friendlyName"));
        Integer isManualMeter = (Integer) conditionMap.get("isManualMeter");
        String mdevId = StringUtil.nullToBlank(conditionMap.get("mdevId"));
        String gs1 = StringUtil.nullToBlank(conditionMap.get("gs1"));
        String modemId = StringUtil.nullToBlank(conditionMap.get("modemId"));
        String mcuId = StringUtil.nullToBlank(conditionMap.get("mcuId"));
        String deviceType = StringUtil.nullToBlank(conditionMap.get("deviceType"));
        String meterType = StringUtil.nullToBlank(conditionMap.get("meterType"));
        String contractGroup = StringUtil.nullToBlank(conditionMap.get("contractGroup"));
        List<Integer> sicIdList = (List<Integer>) conditionMap.get("sicIdList");
        List<Integer> locationIdList = (List<Integer>) conditionMap.get("locationIdList");
        String startDate = StringUtil.nullToBlank(conditionMap.get("startDate"));
        String endDate = StringUtil.nullToBlank(conditionMap.get("endDate"));
        String prevStartDate = StringUtil.nullToBlank(conditionMap.get("prevStartDate"));
        String prevEndDate = StringUtil.nullToBlank(conditionMap.get("prevEndDate"));
        
        String dayView = MeterType.valueOf(meterType).getDayViewName();

        StringBuilder sb = new StringBuilder();

        if (isTotal) {
            sb.append("\nSELECT COUNT(*) AS cnt ");
        } else {
            sb.append("\nSELECT dv.yyyymmdd AS YYYYMMDD,");
            sb.append("\n       dv.mdev_id AS METER_NO, ");
            sb.append("\n       mt.friendly_name AS FRIENDLY_NAME, ");
            sb.append("\n       mt.gs1 AS GS1, ");
            sb.append("\n       co.contract_number AS CONTRACT_NUMBER, ");
            sb.append("\n       cu.name AS CUSTOMER_NAME, ");
            sb.append("\n       mo.device_serial AS MODEM_ID, ");
            sb.append("\n       dv.basevalue AS BASEVALUE, ");
            sb.append("\n       dv.total_value AS VALUE, ");
            sb.append("\n       pre.total_value AS PRE_VALUE ");
        }
        sb.append("\nFROM ").append(dayView).append(" dv ");
        sb.append("\nLEFT OUTER JOIN meter mt ON mt.mds_id = dv.mdev_id		");
        sb.append("\nLEFT OUTER JOIN modem mo ON mo.id = dv.modem_id		");
        sb.append("\nLEFT OUTER JOIN mcu mc ON mc.sys_id = dv.device_id		");
        sb.append("\nLEFT OUTER JOIN contract co ON co.id = dv.contract_id  ");
        sb.append("\nLEFT OUTER JOIN customer cu ON cu.id = co.customer_id  ");
        sb.append("\nLEFT OUTER JOIN code code ON co.sic_id = code.id 		");
        sb.append("\nLEFT OUTER JOIN group_member gm ON gm.member = co.contract_number ");
        sb.append("\nLEFT OUTER JOIN ( ");
        sb.append("\n    SELECT dv.total_value, ");
        sb.append("\n           dv.mdev_id ");
        sb.append("\n    FROM ").append(dayView).append(" dv ");
        sb.append("\n    WHERE dv.yyyymmdd BETWEEN :prevStartDate AND :prevEndDate ");
        sb.append("\n    AND dv.channel     = 1             "); 
        sb.append("\n) pre ON dv.mdev_id = pre.mdev_id ");
        
        sb.append("\nWHERE dv.yyyymmdd BETWEEN :startDate AND :endDate ");
        sb.append("\nAND dv.channel     = 1             "); 
        sb.append("\nAND mt.supplier_id = :supplierId   ");

        if (!deviceType.isEmpty()) {
            sb.append("\nAND   dv.mdev_type = :deviceType ");
        }

        if (meteringSF.equals("s")) {
            sb.append("\nAND dv.total_value      IS NOT NULL ");
        } else {
            sb.append("\nAND dv.total_value      IS NULL     ");
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
                sb.append("\nAND   cu.name LIKE :customerName ");
        	}else {
                sb.append("\nAND   cu.name = :customerName ");
        	}
        }
        if (!mcuId.isEmpty()) {
        	if(mcuId.indexOf('%') == 0 || mcuId.indexOf('%') == (mcuId.length()-1)) { // %문자가 양 끝에 있을경우
            	sb.append("\nAND   mc.sys_id LIKE :mcuId ");
        	}else {
            	sb.append("\nAND   mc.sys_id = :mcuId ");
        	}
        }
        if (!modemId.isEmpty()) {
        	if(modemId.indexOf('%') == 0 || modemId.indexOf('%') == (modemId.length()-1)) { // %문자가 양 끝에 있을경우
                sb.append("\nAND   mo.device_serial LIKE :modemId ");
        	}else {
                sb.append("\nAND   mo.device_serial = :modemId ");
        	}
        }
        if (!friendlyName.isEmpty()) {
            sb.append("\nAND   mt.friendly_name = :friendlyName ");
        }
        if (isManualMeter != null) {
            sb.append("\nAND   mt.is_manual_meter = :isManualMeter ");
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
        if (!isTotal) {
            sb.append("\nORDER BY dv.yyyymmdd, dv.mdev_id ");
        }

        SQLQuery query = getSession().createSQLQuery(new SQLWrapper().getQuery(sb.toString()));
        query.setString("startDate", startDate);
        query.setString("endDate", endDate);
        query.setInteger("supplierId", supplierId);
        query.setString("prevStartDate", prevStartDate);
        query.setString("prevEndDate", prevEndDate);
        if (!deviceType.isEmpty()) {
            query.setString("deviceType", deviceType);
        }
        if (!mdevId.isEmpty()) {
            query.setString("mdevId", mdevId);
        }
        if (!gs1.isEmpty()) {
            query.setString("gs1", gs1);
        }
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
//            if (!isPrev && page != null && limit != null) {
            if (page != null && limit != null) {
                query.setFirstResult((page - 1) * limit);
                query.setMaxResults(limit);
            }
            result = query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
        }
        return result;
    }

    /**
     * method name : getMeteringDataHourlyChannel2Data<b/>
     * method Desc : Metering Data 맥스가젯에서 채널2번 누적유효사용량을 조회한다. : 대성에너지 
     *
     * @param conditionMap
     * @param isTotal
     * @param isPrev
     * @return
     */
    public List<Map<String, Object>> getMeteringDataHourlyChannel2Data(Map<String, Object> conditionMap) {
        
        List<Map<String, Object>> result;

        Integer supplierId = (Integer)conditionMap.get("supplierId");
        Integer tariffType = (Integer)conditionMap.get("tariffType");
        Integer sicId = (Integer)conditionMap.get("sicId");

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
        String DayTable = MeterType.valueOf(meterType).getDayTableName();


        startDate = StringUtil.nullToBlank(conditionMap.get("startDate"));
        endDate = StringUtil.nullToBlank(conditionMap.get("endDate"));
        startDetailDate = Integer.parseInt(StringUtil.nullToBlank(conditionMap.get("startDetailDate")));
        endDetailDate = Integer.parseInt(StringUtil.nullToBlank(conditionMap.get("endDetailDate")));

        StringBuilder sb = new StringBuilder();

        sb.append("\nSELECT yyyymmdd AS YYYYMMDD, ");
        for (int i = endDetailDate; i >= startDetailDate ; i--) {
            sb.append("\n value_").append(StringUtil.frontAppendNStr('0', new Integer(i).toString(), 2))
            .append(" AS VALUE_").append(StringUtil.frontAppendNStr('0', new Integer(i).toString(), 2)).append(", ");
        }
        sb.append("\n       mds_id AS METER_NO ");
        sb.append("\nFROM ( ");
        sb.append("\n    SELECT dy.yyyymmdd, ");
        sb.append("\n           mt.mds_id, ");
        
        String numindex = null;
        for (int i = endDetailDate; i >= startDetailDate ; i--) {
            numindex = StringUtil.frontAppendNStr('0', new Integer(i).toString(), 2);
            sb.append("\n COALESCE(dy.value_").append(numindex).append(", 0)").append(" AS value_"+numindex).append(", ");
        }
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
        sb.append("\n            and   me.id = dy.meter_id ");
        sb.append("\n            AND   cc.channel_index = 1) AS ch_method ");
        sb.append("\n    FROM ").append(DayTable).append(" dy ");
        sb.append("\n         LEFT OUTER JOIN ");
        sb.append("\n         contract co ");
        sb.append("\n         ON co.id = dy.contract_id ");
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

        sb.append("\n    WHERE dy.yyyymmdd BETWEEN :startDate AND :endDate ");
        sb.append("\n    AND   dy.channel = 2 ");
        sb.append("\n    AND   mt.id = dy.meter_id ");
        sb.append("\n    AND   mt.supplier_id = :supplierId ");

        if (!deviceType.isEmpty()) {
            sb.append("\n    AND   dy.mdev_type = :deviceType ");
        }

        if (meteringSF.equals("s")) {
            sb.append("\n    AND   dy.total_value IS NOT NULL ");
        } else {
            sb.append("\n    AND   dy.total_value IS NULL ");
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
            sb.append("\n    AND   cu.name LIKE :customerName ");
        }

        if (!mcuId.isEmpty()) {
            sb.append("\n    AND   mo.id = mt.modem_id ");
            sb.append("\n    AND   mc.id = mo.mcu_id ");
            sb.append("\n    AND   mc.sys_id = :mcuId ");
        }

        sb.append("\n) x ");

        // sb.append("\nORDER BY yyyymmdd, mds_id ");
        

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
     * method name : getMeteringDataWeeklyData<b/>
     * method Desc : Metering Data 맥스가젯에서 주별 검침데이터를 조회한다.
     *
     * @param conditionMap
     * @param isTotal
     * @return
     */
    public List<Map<String, Object>> getMeteringDataWeeklyData(Map<String, Object> conditionMap, boolean isTotal) {
        return getMeteringDataWeeklyData(conditionMap, isTotal, false);
    }

    /**
     * method name : getMeteringDataWeeklyData<b/>
     * method Desc : Metering Data 맥스가젯에서 주별 검침데이터를 조회한다.
     *
     * @param conditionMap
     * @param isTotal
     * @param isPrev
     * @return
     */
    public List<Map<String, Object>> getMeteringDataWeeklyData(Map<String, Object> conditionMap, boolean isTotal, boolean isPrev) {
        List<Map<String, Object>> result;

        Integer supplierId = (Integer)conditionMap.get("supplierId");
        Integer tariffType = (Integer)conditionMap.get("tariffType");
        Integer sicId = (Integer)conditionMap.get("sicId");
        Integer page = (Integer)conditionMap.get("page");
        Integer limit = (Integer)conditionMap.get("limit");
        String contractNumber = StringUtil.nullToBlank(conditionMap.get("contractNumber"));
        String customerName = StringUtil.nullToBlank(conditionMap.get("customerName"));
        String meteringSF = StringUtil.nullToBlank(conditionMap.get("meteringSF"));
        String startDate = StringUtil.nullToBlank(conditionMap.get("startDate"));
        String endDate = StringUtil.nullToBlank(conditionMap.get("endDate"));
        String prevStartDate = StringUtil.nullToBlank(conditionMap.get("prevStartDate"));
        String prevEndDate = StringUtil.nullToBlank(conditionMap.get("prevEndDate"));
        String mdevId = StringUtil.nullToBlank(conditionMap.get("mdevId"));
        String gs1 = StringUtil.nullToBlank(conditionMap.get("gs1"));
        String modemId = StringUtil.nullToBlank(conditionMap.get("modemId"));
        String mcuId = StringUtil.nullToBlank(conditionMap.get("mcuId"));
        String deviceType = StringUtil.nullToBlank(conditionMap.get("deviceType"));
        String meterType = StringUtil.nullToBlank(conditionMap.get("meterType"));
        String contractGroup = StringUtil.nullToBlank(conditionMap.get("contractGroup"));
        List<Integer> sicIdList = (List<Integer>)conditionMap.get("sicIdList");
        List<Integer> locationIdList = (List<Integer>)conditionMap.get("locationIdList");
        
        String dayView = MeterType.valueOf(meterType).getDayViewName();

        StringBuilder sb = new StringBuilder();

        if (isTotal) {
            sb.append("\nSELECT COUNT(NVL(SUM(dv.total_value),0))		");
        } else {
            sb.append("\nSELECT dv.mdev_id AS METER_NO,			");
            sb.append("\n       co.contract_number AS CONTRACT_NUMBER,	");
            sb.append("\n       cu.name AS CUSTOMER_NAME,		");
            sb.append("\n       mo.device_serial AS MODEM_ID,	");
            sb.append("\n       code.name AS SIC_NAME,			");
            sb.append("\n       SUM(dv.total_value) AS VALUE,	");
            sb.append("\n       mt.gs1 AS GS1,	");
            sb.append("\n       pre.total_value AS PRE_VALUE	");
        }
        sb.append("\nFROM ").append(dayView).append(" dv ");
        sb.append("\nLEFT OUTER JOIN meter mt ON mt.mds_id = dv.mdev_id		");
        sb.append("\nLEFT OUTER JOIN modem mo ON mo.id = dv.modem_id		");
        sb.append("\nLEFT OUTER JOIN mcu mc ON mc.sys_id = dv.device_id		");
        sb.append("\nLEFT OUTER JOIN contract co ON co.id = dv.contract_id	");
        sb.append("\nLEFT OUTER JOIN customer cu ON cu.id = co.customer_id	");
        sb.append("\nLEFT OUTER JOIN code code ON co.sic_id = code.id		");
        sb.append("\nLEFT OUTER JOIN group_member gm ON gm.member = co.contract_number ");
        sb.append("\nLEFT OUTER JOIN ( 					"); // Prev Week join
        sb.append("\n    SELECT SUM(dv.total_value) AS TOTAL_VALUE,	");
        sb.append("\n           dv.mdev_id 				");
        sb.append("\n    FROM ").append(dayView).append(" dv ");
        sb.append("\n    WHERE dv.yyyymmdd BETWEEN :prevStartDate AND :prevEndDate ");
        sb.append("\n    AND dv.channel     = 1			"); 
        sb.append("\n    GROUP BY dv.mdev_id			"); 
        sb.append("\n) pre ON dv.mdev_id = pre.mdev_id	");
        
        sb.append("\nWHERE dv.yyyymmdd BETWEEN :startDate AND :endDate ");
        sb.append("\nAND dv.channel     = 1             "); 
        sb.append("\nAND mt.supplier_id = :supplierId   ");

        if (!deviceType.isEmpty()) {
            sb.append("\n    AND   dv.mdev_type = :deviceType ");
        }
        if (meteringSF.equals("s")) {
            sb.append("\n    AND   dv.total_value IS NOT NULL ");
        } else {
            sb.append("\n    AND   dv.total_value IS NULL ");
        }
        if (!mdevId.isEmpty()) {
        	if(mdevId.indexOf('%') == 0 || mdevId.indexOf('%') == (mdevId.length()-1)) { // %문자가 양 끝에 있을경우
                sb.append("\n    AND   mt.mds_id LIKE :mdevId ");
        	}else {
                sb.append("\n    AND   mt.mds_id = :mdevId ");
        	}
        }
        if (!gs1.isEmpty()) {
        	if(gs1.indexOf('%') == 0 || gs1.indexOf('%') == (gs1.length()-1)) { // %문자가 양 끝에 있을경우
                sb.append("\n    AND   mt.gs1 LIKE :gs1 ");
        	}else {
                sb.append("\n    AND   mt.gs1 = :gs1 ");
        	}
        }
        if (!contractNumber.isEmpty()) {
        	if(contractNumber.indexOf('%') == 0 || contractNumber.indexOf('%') == (contractNumber.length()-1)) { // %문자가 양 끝에 있을경우
                sb.append("\n    AND   co.contract_number LIKE :contractNumber ");
        	}else {
                sb.append("\n    AND   co.contract_number = :contractNumber ");
        	}
        }
        if (!customerName.isEmpty()) {
        	if(customerName.indexOf('%') == 0 || customerName.indexOf('%') == (customerName.length()-1)) { // %문자가 양 끝에 있을경우
                sb.append("\n    AND   cu.name LIKE :customerName ");
        	}else {
                sb.append("\n    AND   cu.name = :customerName ");
        	}
        }
        if (!mcuId.isEmpty()) {
        	if(mcuId.indexOf('%') == 0 || mcuId.indexOf('%') == (mcuId.length()-1)) { // %문자가 양 끝에 있을경우
            	sb.append("\n    AND   mc.sys_id LIKE :mcuId ");
        	}else {
            	sb.append("\n    AND   mc.sys_id = :mcuId ");
        	}
        }
        if(!modemId.isEmpty()){
        	if(modemId.indexOf('%') == 0 || modemId.indexOf('%') == (modemId.length()-1)) { // %문자가 양 끝에 있을경우
                sb.append("\n    AND   mo.device_serial LIKE :modemId ");
        	}else {
                sb.append("\n    AND   mo.device_serial = :modemId ");
        	}
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
            sb.append("\n    AND   gm.group_id = :contractGroup ");
        }

        sb.append("\nGROUP BY dv.mdev_id, co.contract_number, cu.name, mo.device_serial, pre.total_value, code.name, mt.gs1  ");
        if (!isTotal) {
            sb.append("\nORDER BY dv.mdev_id ");
        }

        SQLQuery query = getSession().createSQLQuery(new SQLWrapper().getQuery(sb.toString()));

        query.setString("startDate", startDate);
        query.setString("endDate", endDate);
        query.setInteger("supplierId", supplierId);
        query.setString("prevStartDate", prevStartDate);
        query.setString("prevEndDate", prevEndDate);

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
     * method name : getMeteringValueMonthlyData<b/>
     * method Desc : Metering Data 맥스가젯에서 월별 지침값을 조회한다.
     *
     * @param conditionMap
     * @param isTotal
     * @return
     */
    public List<Map<String, Object>> getMeteringValueMonthlyData(Map<String, Object> conditionMap, boolean isTotal) {
        return getMeteringValueMonthlyData(conditionMap, isTotal, false);
    }
    
    /**
     * method name : getMeteringValueMonthlyData<b/>
     * method Desc : Metering Data 맥스가젯에서 월별 지침값을 조회한다.
     *               WM/GM 만 사용 가능 ( LASTDAY 테이블이 현재 WM/GM에만 존재)
     *               rf) LASTDAY_WM, LASTDAY_GM
     *
     * @param conditionMap
     * @param isTotal
     * @param isPrev
     * @return
     */
    public List<Map<String, Object>> getMeteringValueMonthlyData(Map<String, Object> conditionMap, boolean isTotal, boolean isSub) {
        
        List<Map<String, Object>> result;

        Integer supplierId = (Integer)conditionMap.get("supplierId");
        Integer tariffType = (Integer)conditionMap.get("tariffType");
        Integer sicId = (Integer)conditionMap.get("sicId");
        Integer page = (Integer)conditionMap.get("page");
        Integer limit = (Integer)conditionMap.get("limit");

        String contractNumber = StringUtil.nullToBlank(conditionMap.get("contractNumber"));
        String customerName = StringUtil.nullToBlank(conditionMap.get("customerName"));
        String meteringSF = StringUtil.nullToBlank(conditionMap.get("meteringSF"));
        
        String friendlyName = StringUtil.nullToBlank(conditionMap.get("friendlyName"));
        Integer isManualMeter = (Integer) conditionMap.get("isManualMeter");

        String startDate = null;
        String endDate = null;

        String mcuId = StringUtil.nullToBlank(conditionMap.get("mcuId"));
        String deviceType = StringUtil.nullToBlank(conditionMap.get("deviceType"));
        String mdevId = StringUtil.nullToBlank(conditionMap.get("mdevId"));
        String modemId = StringUtil.nullToBlank(conditionMap.get("modemId"));
        String contractGroup = StringUtil.nullToBlank(conditionMap.get("contractGroup"));
        String meterType = StringUtil.nullToBlank(conditionMap.get("meterType"));
        List<Integer> sicIdList = (List<Integer>)conditionMap.get("sicIdList");
        List<Integer> locationIdList = (List<Integer>)conditionMap.get("locationIdList");
        String DayTable = MeterType.valueOf(meterType).getDayTableName();

        
        if (isSub) {
            startDate = StringUtil.nullToBlank(conditionMap.get("subStartDate"));
            endDate = StringUtil.nullToBlank(conditionMap.get("subEndDate"));
        } else {
            startDate = StringUtil.nullToBlank(conditionMap.get("startDate"));
            endDate = StringUtil.nullToBlank(conditionMap.get("endDate"));
        }

        StringBuilder sb = new StringBuilder();

        if (isTotal) {
            sb.append("\nSELECT COUNT(*) AS cnt ");
        }
        else {
            sb.append("\nSELECT contract_number AS CONTRACT_NUMBER, ");
            sb.append("\n       customer_name AS CUSTOMER_NAME, ");
            sb.append("\n       yyyymmdd AS YYYYMMDD, ");
            sb.append("\n       mds_id AS METER_NO, ");
            sb.append("\n       friendly_name AS FRIENDLY_NAME, ");
    
            if (!isSub) {
                if (!mcuId.isEmpty()) {
                    sb.append("\n       device_serial AS MODEM_ID, ");
                } else {
                    sb.append("\n       (SELECT md.device_serial FROM modem md WHERE md.id = tb.modem_id ) AS MODEM_ID, ");
                }
            }
            sb.append("\n       (select name from code where id = tb.sic_id) as SIC_NAME, ");
            sb.append("\n       baseValue as BASEVALUE, ");
            sb.append("\n       firstValue as FIRSTVALUE, ");
            sb.append("\n       total as VALUE ");
        }

        sb.append("\nFROM ( ");
        sb.append("\n    SELECT dy.yyyymmdd, ");
        sb.append("\n           mt.mds_id, ");
        sb.append("\n           mt.friendly_name, ");
        sb.append("\n           co.contract_number, ");
        sb.append("\n           cu.name AS customer_name, ");

        if (!isSub) {
            if (!mcuId.isEmpty()) {
                sb.append("\n           mo.device_serial, ");
            } else {
                sb.append("\n           mt.modem_id, ");
            }
        }
        sb.append("\n           co.sic_id, ");
        sb.append("\n           baseValue AS baseValue, ");
        sb.append("\n           lastdata.firstValue AS firstValue, ");
        sb.append("\n           SUM(dy.total_value) AS total ");

        sb.append("\n    FROM ").append(DayTable).append(" dy ");
        sb.append("\n         LEFT OUTER JOIN ");
        sb.append("\n         contract co ");
        sb.append("\n         ON co.id = dy.contract_id ");
        sb.append("\n         LEFT OUTER JOIN ");
        sb.append("\n         customer cu ");
        sb.append("\n         ON cu.id = co.customer_id, ");

        if (!contractGroup.isEmpty()) {
            sb.append("\n         group_member gm, ");
        }

        sb.append("\n         meter mt, ");
        sb.append("\n         (SELECT lastdy.mdev_id, lastdy.mdev_type, lastdy.yyyymmdd, lastdy.baseValue as firstValue from LAST").append(DayTable).append(" lastdy where lastdy.yyyymm  between :startDate and :endDate) lastData ");        
        if (!mcuId.isEmpty()) {
            sb.append("\n         ,modem mo ");
            sb.append("\n         ,mcu mc ");
        }
        
        if(!modemId.isEmpty()){
            sb.append("\n         ,modem mo ");
        }

        sb.append("\n    WHERE dy.yyyymmdd=lastData.yyyymmdd ");
        sb.append("\n    AND   dy.mdev_id = lastData.mdev_id ");
        sb.append("\n    AND   dy.mdev_type = lastData.mdev_type ");
        sb.append("\n    AND   dy.channel = 1 ");
        sb.append("\n    AND   mt.id = dy.meter_id ");
        sb.append("\n    AND   mt.supplier_id = :supplierId ");

        if (!deviceType.isEmpty()) {
            sb.append("\n    AND   dy.mdev_type = :deviceType ");
        }

        if (meteringSF.equals("s")) {
            sb.append("\n    AND   dy.total_value IS NOT NULL ");
        } else {
            sb.append("\n    AND   dy.total_value IS NULL ");
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
            sb.append("\n    AND   cu.name LIKE :customerName ");
        }

        if (!mcuId.isEmpty()) {
            sb.append("\n    AND   mo.id = mt.modem_id ");
            sb.append("\n    AND   mc.id = mo.mcu_id ");
            sb.append("\n    AND   mc.sys_id = :mcuId ");
        }
        
        if(!modemId.isEmpty()){
            sb.append("\n    AND   mt.modem_id = mo.id ");
            sb.append("\n    AND   mo.device_serial like :modemId ");
        }

        sb.append("\n    GROUP BY dy.yyyymmdd, ");
        sb.append("\n             mt.mds_id, ");
        sb.append("\n             mt.friendly_name, ");
        sb.append("\n             co.contract_number, ");
        sb.append("\n             cu.name, ");
        sb.append("\n             co.sic_id, ");
        sb.append("\n             dy.baseValue, ");
        sb.append("\n             lastdata.firstValue ");

        if (!isSub) {
            if (!mcuId.isEmpty()) {
                sb.append("\n             ,mo.device_serial ");
            } else {
                sb.append("\n             ,mt.modem_id ");
            }
        }

        sb.append("\n) tb ");

        SQLQuery query = getSession().createSQLQuery(new SQLWrapper().getQuery(sb.toString()));

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
        
        if(!modemId.isEmpty()){
            query.setString("modemId", "%" + modemId + "%");
        }

        if (isTotal) {
            Map<String, Object> map = new HashMap<String, Object>();
            int count = 0;
            count = ((Number)query.uniqueResult()).intValue();
            map.put("total", count);
            result = new ArrayList<Map<String, Object>>();
            result.add(map);
        } else {
            if (!isSub && page != null && limit != null) {
                query.setFirstResult((page - 1) * limit);
                query.setMaxResults(limit);
            }
            result = query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
        }
        return result;
    }
    
    /**
     * method name : getMeteringDataDetailDailyData<b/>
     * method Desc : Metering Data 맥스가젯 상세화면에서 일별 검침데이터를 조회한다.
     *
     * @param conditionMap
     * @return
     */
    public List<Map<String, Object>> getMeteringDataDetailDailyData(Map<String, Object> conditionMap, boolean isSum) {
        List<Map<String, Object>> result;

        Integer supplierId = (Integer)conditionMap.get("supplierId");
        String searchStartDate = StringUtil.nullToBlank(conditionMap.get("searchStartDate"));
        String searchEndDate = StringUtil.nullToBlank(conditionMap.get("searchEndDate"));
        String meterNo = StringUtil.nullToBlank(conditionMap.get("meterNo"));
        String meterType = StringUtil.nullToBlank(conditionMap.get("meterType"));
        String tlbType = StringUtil.nullToBlank(conditionMap.get("tlbType"));
        List<Integer> channelIdList = (List<Integer>)conditionMap.get("channelIdList");
        String dayView = MeterType.valueOf(meterType).getDayViewName();
        String dayTable = MeterType.valueOf(meterType).getDayTableName();

        StringBuilder sb = new StringBuilder();

        if (isSum) {
            sb.append("\nSELECT dv.channel AS CHANNEL, ");
            sb.append("\n       MAX(dv.total_value) AS MAX_VAL, ");
            sb.append("\n       MIN(dv.total_value) AS MIN_VAL, ");
            sb.append("\n       AVG(dv.total_value) AS AVG_VAL, ");
            sb.append("\n       SUM(dv.total_value) AS SUM_VAL ");
        } else {
            sb.append("\nSELECT dv.yyyymmdd AS YYYYMMDD, 		");
            sb.append("\n       dv.channel AS CHANNEL, 			");
            sb.append("\n       dv.total_value AS VALUE, 		");
            sb.append("\n       x.ch_method AS CH_METHOD 		");
        }
        sb.append("\nFROM ").append(dayView).append(" dv		");
        sb.append("\nLEFT OUTER JOIN ( 							");
        sb.append("\n    SELECT DISTINCT de.mdev_id, 			");
        sb.append("\n           de.ch_method 					");
        sb.append("\n    FROM ").append(dayTable).append(" de	");
        sb.append("\n    WHERE de.yyyymmdd BETWEEN :startDate AND :endDate ");
        sb.append("\n	 AND   de.mdev_id = :meterNo 			");
        sb.append("\n) x ON dv.mdev_id = x.mdev_id 				");
        sb.append("\nWHERE dv.yyyymmdd BETWEEN :startDate AND :endDate ");
        if (channelIdList != null) {
            sb.append("\nAND   dv.channel IN (:channelIdList) 	");
        }
        sb.append("\nAND   dv.mdev_id = :meterNo ");
        sb.append("\nAND   dv.supplier_id = :supplierId ");

        if (isSum) {
            sb.append("\nGROUP BY CHANNEL ");
        }

        SQLQuery query = getSession().createSQLQuery(new SQLWrapper().getQuery(sb.toString()));

//        query.setString("tlbType", tlbType);
        query.setString("startDate", searchStartDate);
        query.setString("endDate", searchEndDate);
        query.setString("meterNo", meterNo);
        query.setInteger("supplierId", supplierId);

        if (channelIdList != null) {
            query.setParameterList("channelIdList", channelIdList);
        }

        result = query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
        return result;
    }

    /**
     * method name : getMeteringDataDetailRatelyData<b/>
     * method Desc : Metering Data 맥스가젯 상세화면에서 Rate 별 검침데이터를 조회한다.
     *
     * @param conditionMap
     * @return
     */
    public List<Map<String, Object>> getMeteringDataDetailRatelyData(Map<String, Object> conditionMap) {
        List<Map<String, Object>> result;

        Integer supplierId = (Integer)conditionMap.get("supplierId");
        String searchStartDate = StringUtil.nullToBlank(conditionMap.get("searchStartDate"));
        String searchEndDate = StringUtil.nullToBlank(conditionMap.get("searchEndDate"));
        String meterNo = StringUtil.nullToBlank(conditionMap.get("meterNo"));
        String meterType = StringUtil.nullToBlank(conditionMap.get("meterType"));
        Integer rateChannel = (Integer)conditionMap.get("rateChannel");
        String dayTable = MeterType.valueOf(meterType).getDayTableName();

        StringBuilder sb = new StringBuilder();

        sb.append("\nSELECT da.yyyymmdd AS YYYYMMDD, ");
        sb.append("\n       da.channel AS CHANNEL ");

        for (int i = 0 ; i < 24 ; i++) {
            sb.append("       ,SUM(COALESCE(value_").append(CalendarUtil.to2Digit(i)).append(", 0)) ");
            sb.append("AS VALUE_").append(CalendarUtil.to2Digit(i)).append(" ");
        }

        sb.append("\nFROM ").append(dayTable).append(" da ");
        sb.append("\nWHERE da.yyyymmdd BETWEEN :startDate AND :endDate ");
        sb.append("\nAND   da.channel = :rateChannel ");
        sb.append("\nAND   da.mdev_id = :meterNo ");
        sb.append("\nAND   da.supplier_id = :supplierId ");
        sb.append("\nGROUP BY da.yyyymmdd, da.channel ");

        SQLQuery query = getSession().createSQLQuery(new SQLWrapper().getQuery(sb.toString()));

        query.setString("startDate", searchStartDate);
        query.setString("endDate", searchEndDate);
        query.setString("meterNo", meterNo);
        query.setInteger("supplierId", supplierId);
        query.setInteger("rateChannel", rateChannel);

        result = query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
        return result;
    }

    @Override
    public List<Object> getUsageForEndDevicesByMonthPeriodReport(
            Map<String, Object> params, Map<String, Object> params2) {
        Integer channel     = (Integer)params2.get("channel");
        Integer dst         = (Integer)params2.get("dst");
        String meterType    = (String)params2.get("meterType");  

        String searchStartDate      = (String)params.get("searchStartDate"); 
        String searchEndDate        = (String)params.get("searchEndDate");

        List<Integer> modemId       = (List<Integer>)params.get("modemId");
        List<Integer> meterId       = (List<Integer>)params.get("meterId");

        //log.debug("params2:"+params2+":params:"+params);
        StringBuffer sb = new StringBuffer();
        sb.append("\n SELECT month.id.yyyymm as yyyymm, SUM(month.total) AS total ");
        sb.append("\n       ,SUM(month.value_01 ) AS value01 ");
        sb.append("\n       ,SUM(month.value_02 ) AS value02 ");
        sb.append("\n       ,SUM(month.value_03 ) AS value03 ");
        sb.append("\n       ,SUM(month.value_04 ) AS value04 ");
        sb.append("\n       ,SUM(month.value_05 ) AS value05 ");
        sb.append("\n       ,SUM(month.value_06 ) AS value06 ");
        sb.append("\n       ,SUM(month.value_07 ) AS value07 ");
        sb.append("\n       ,SUM(month.value_08 ) AS value08 ");
        sb.append("\n       ,SUM(month.value_09 ) AS value09 ");
        sb.append("\n       ,SUM(month.value_10 ) AS value10 ");
        sb.append("\n       ,SUM(month.value_11 ) AS value11 ");
        sb.append("\n       ,SUM(month.value_12 ) AS value12 ");
        sb.append("\n       ,SUM(month.value_13 ) AS value13 ");
        sb.append("\n       ,SUM(month.value_14 ) AS value14 ");
        sb.append("\n       ,SUM(month.value_15 ) AS value15 ");
        sb.append("\n       ,SUM(month.value_16 ) AS value16 ");
        sb.append("\n       ,SUM(month.value_17 ) AS value17 ");
        sb.append("\n       ,SUM(month.value_18 ) AS value18 ");
        sb.append("\n       ,SUM(month.value_19 ) AS value19 ");
        sb.append("\n       ,SUM(month.value_20 ) AS value20 ");
        sb.append("\n       ,SUM(month.value_21 ) AS value21 ");
        sb.append("\n       ,SUM(month.value_22 ) AS value22 ");
        sb.append("\n       ,SUM(month.value_23 ) AS value23 ");
        sb.append("\n       ,SUM(month.value_24 ) AS value24 ");
        sb.append("\n       ,SUM(month.value_25 ) AS value25 ");
        sb.append("\n       ,SUM(month.value_26 ) AS value26 ");
        sb.append("\n       ,SUM(month.value_27 ) AS value27 ");
        sb.append("\n       ,SUM(month.value_28 ) AS value28 ");
        sb.append("\n       ,SUM(month.value_29 ) AS value29 ");
        sb.append("\n       ,SUM(month.value_30 ) AS value30 ");
        sb.append("\n       ,SUM(month.value_31 ) AS value31 ");
        sb.append("\n FROM ").append(meterType).append(" month ");
        sb.append("\n WHERE 1=1 ");
        sb.append("\n AND (month.meter.id IN (:meterId) OR month.modem.id IN (:modemId) ) ");
        sb.append("\n AND month.id.yyyymm between :startDate and :endDate ");
        sb.append("\n AND month.id.channel = :channel ");
        sb.append("\n AND month.id.dst = :dst ");
        sb.append("\n GROUP BY month.id.yyyymm ");
        // sb.append("\n GROUP BY month.id.yyyymm ORDER BY month.id.yyyymm ");
        //log.debug("sb.toString():"+sb.toString());
        Query query  = getSession().createQuery(sb.toString())
                .setParameterList("meterId", meterId)
                .setParameterList("modemId", modemId)
                .setString("startDate", searchStartDate)
                .setString("endDate", searchEndDate)
                .setInteger("channel", channel)
                .setInteger("dst", dst);

        //      Map<String,Object> resultMap = new HashMap<String,Object>();;
        //      Map<String,Object> tmp = null;
        List<Object> list = query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
        //      for(Object obj:list){
        //          tmp = new HashMap<String,Object>();
        //          tmp = (Map<String,Object>)obj;
        //          resultMap.put((String)tmp.get("yyyymm"), tmp.get("total"));
        //      }

        return list;
    }
    
    /**
     * method name : getDayUsage
     * method Desc : 각 DayEM, DayWM, DayGM 테이블에서 해당날짜의 사용량을 가져온다.
     *
     * @param conditionMap
     * @return
     */
    public List<Map<String, Object>> getDayUsage(Map<String, Object> conditionMap) {
        List<Map<String, Object>> result;

        String startDate = StringUtil.nullToBlank(conditionMap.get("startDate"));
        String endDate = StringUtil.nullToBlank(conditionMap.get("endDate"));
        String meterType = StringUtil.nullToBlank(conditionMap.get("meterType"));
        String channel = StringUtil.nullToBlank(conditionMap.get("channel"));
        String mdevId = StringUtil.nullToBlank(conditionMap.get("mdevId"));

        String dayTable = MeterType.valueOf(meterType).getDayTableName();

        StringBuilder sb = new StringBuilder();

        sb.append("\nSELECT da.yyyymmdd AS yyyymmdd, ");
        sb.append("\n       da.total AS usage ");
        sb.append("\nFROM ").append(dayTable).append(" da ");
        sb.append("\nWHERE da.yyyymmdd BETWEEN :startDate AND :endDate ");
        sb.append("\nAND   da.channel = :channel ");
        sb.append("\nAND   da.mdev_id = :mdevId ");
        // sb.append("\nORDER BY da.yyyymmdd ");

        SQLQuery query = getSession().createSQLQuery(new SQLWrapper().getQuery(sb.toString()));

        query.setString("startDate", startDate);
        query.setString("endDate", endDate);
        query.setString("mdevId", mdevId);
        query.setString("channel", channel);

        result = query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
        return result;
    }

    /**
     * method name : getMeteringSuccessCountListPerLocation<b/>
     * method Desc : 
     *
     * @param conditionMap
     * @return
     */
    public List<Map<String, Object>> getMeteringSuccessCountListPerLocation(Map<String, Object> conditionMap) {
        Integer supplierId = (Integer)conditionMap.get("supplierId");
        String meterType = StringUtil.nullToBlank(conditionMap.get("meterType"));
        String searchStartDate = StringUtil.nullToBlank(conditionMap.get("searchStartDate"));
        String searchEndDate = StringUtil.nullToBlank(conditionMap.get("searchEndDate"));

        // 미터 타입별 미터링데이 테이블 설정
        String dayTable = CommonConstants.MeterType.valueOf(meterType).getDayTableName();

        StringBuilder sb = new StringBuilder();

        sb.append("\nSELECT m.location_id AS LOC_ID, ");
        sb.append("\n       COUNT(DISTINCT d.mdev_id) AS SUCCESS_CNT ");
        sb.append("\nFROM ").append(dayTable).append(" d, ");
        sb.append("\n     meter m ");
        sb.append("\n     LEFT OUTER JOIN ");
        sb.append("\n     code c ");
        sb.append("\n     ON c.id = m.meter_status, ");
        sb.append("\n     location p ");
        sb.append("\nWHERE m.meter = :meterType ");
        sb.append("\nAND   m.install_date <= :installDate ");
        sb.append("\nAND   m.supplier_id = :supplierId ");
        sb.append("\nAND   m.location_id = p.id ");
        sb.append("\nAND   (c.id IS NULL ");
        sb.append("\n    OR c.code != :deleteCode ");
        sb.append("\n    OR (c.code = :deleteCode AND m.delete_date > :deleteDate) ");
        sb.append("\n) ");
        sb.append("\nAND   d.mdev_type = :mdevType ");
        sb.append("\nAND   d.mdev_id = m.mds_id ");
        sb.append("\nAND   d.yyyymmdd BETWEEN :startDate AND :endDate ");
        sb.append("\nAND   d.channel = :channel ");
        sb.append("\nGROUP BY m.location_id ");

        SQLQuery query = getSession().createSQLQuery(new SQLWrapper().getQuery(sb.toString()));
        query.setString("startDate", searchStartDate);
        query.setString("endDate", searchEndDate);
        query.setString("installDate", searchEndDate + "235959");
        query.setString("meterType", meterType);
        query.setString("mdevType", CommonConstants.DeviceType.Meter.name());
        query.setInteger("channel", DefaultChannel.Usage.getCode());
        query.setInteger("supplierId", supplierId);
        query.setString("deleteCode", MeterCodes.DELETE_STATUS.getCode());
        query.setString("deleteDate", searchStartDate + "235959");

        return query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
    }

    /**
     * method name : getSuccessCountByLocation<b/>
     * method Desc :
     *
     * @param condition
     * @return
     */
    public String getSuccessCountByLocation(Map<String, Object> condition) {
        String meterType = StringUtil.nullToBlank(condition.get("meterType"));
        String startDate = StringUtil.nullToBlank(condition.get("searchStartDate"));
        String endDate = StringUtil.nullToBlank(condition.get("searchEndDate"));
        String supplierId = StringUtil.nullToBlank(condition.get("supplierId"));
        String locationId = StringUtil.nullToBlank(condition.get("locationId"));
        String installDate = null;
        String deleteDate = startDate + "235959";

        if (endDate.isEmpty()) {
            try {
                installDate = TimeUtil.getCurrentTime();
            } catch (ParseException e) {
                log.error(e, e);
            }
        } else {
            installDate = endDate + "235959";
        }

        // 미터 타입별 미터링데이 테이블 설정
        String dayTable = CommonConstants.MeterType.valueOf(meterType).getDayTableName();

        StringBuilder sb = new StringBuilder();

        // 조회기간 내 검침내역이 한 건이라도 있으면 성공
        sb.append("\nSELECT COUNT(*) AS CNT FROM (");
        sb.append("\n    SELECT m.id ");
        sb.append("\n    FROM meter m ");
        sb.append("\n         LEFT OUTER JOIN ");
        sb.append("\n         code c ");
        sb.append("\n         ON c.id = m.meter_status, ");
        sb.append("\n         ").append(dayTable).append(" dt ");
        sb.append("\n    WHERE m.meter = :meterType ");
        sb.append("\n    AND   m.location_id = :locationId ");
        sb.append("\n    AND   m.install_date <= :installDate ");
        if (!supplierId.isEmpty()) {
            sb.append("\n    AND   m.supplier_id = :supplierId ");
        }
        sb.append("\n    AND   (c.id IS NULL ");
        sb.append("\n        OR c.code != :deleteCode ");
        sb.append("\n        OR (c.code = :deleteCode AND m.delete_date > :deleteDate) ");
        sb.append("\n    ) ");
        sb.append("\n    AND   dt.mdev_type = :mdevType ");
        sb.append("\n    AND   dt.mdev_id = m.mds_id ");
        sb.append("\n    AND   dt.yyyymmdd BETWEEN :searchStartDate AND :searchEndDate ");
        sb.append("\n    AND   dt.channel = :channel ");
//        sb.append("\n    AND   dt.location_id = m.location_id ");
        sb.append("\n    GROUP BY m.id ");
        sb.append("\n) x ");

        SQLQuery query = getSession().createSQLQuery(sb.toString());

        query.setString("meterType", meterType);
        query.setInteger("locationId", Integer.parseInt(locationId));
        query.setString("searchStartDate", startDate);
        query.setString("searchEndDate", endDate);
        query.setString("installDate", installDate);
        query.setString("mdevType", CommonConstants.DeviceType.Meter.name());
        query.setInteger("channel", DefaultChannel.Usage.getCode());
        query.setString("deleteCode", MeterCodes.DELETE_STATUS.getCode());
        query.setString("deleteDate", deleteDate);

        if (!supplierId.isEmpty()) {
            query.setInteger("supplierId", Integer.parseInt(supplierId));
        }

        String successCount = ((Number)query.uniqueResult()).toString();

        return successCount;
    }

    /**
     * method name : getFailureCountByCauses<b/>
     * method Desc : MeteringFail 가젯의 Cause1/Cause2 Count 를 조회한다.<b/>
     *               1. meteringdata_em 테이블에 없는 meter 테이블 데이터의 LAST_READ_DATE 를 검색.<b/>
     *               2. LAST_READ_DATE 의 값이 현재일과 하루이상 차이가 나면 Cause1(통신장애)<b/>
     *               3. LAST_READ_DATE 의 값이 현재일과 같으면 Cause2(포멧에러)
     *               4. 그 외 경우 ETC
     *
     * @param condition
     * @return
     */
    public Map<String, String> getFailureCountByCauses(Map<String, Object> condition) {
        Map<String, String> result = new HashMap<String, String>();
        String meterType = StringUtil.nullToBlank(condition.get("meterType"));
        String startDate = StringUtil.nullToBlank(condition.get("searchStartDate"));
        String endDate = StringUtil.nullToBlank(condition.get("searchEndDate"));
        String supplierId = StringUtil.nullToBlank(condition.get("supplierId"));
        String locationId = StringUtil.nullToBlank(condition.get("locationId"));

        String currentDate = null;
        String installDate = null;

        try {
            currentDate = TimeUtil.getCurrentTime();
        } catch (ParseException e) {
            log.error(e, e);
        }

        if (!endDate.isEmpty()) {
            installDate = endDate + "235959";
        } else {
            installDate = currentDate;
        }

        // 미터 타입별 미터링데이터 테이블 설정
        String dayTable = CommonConstants.MeterType.valueOf(meterType).getDayTableName();
        StringBuilder sb = new StringBuilder();

        sb.append("\nSELECT m.id AS ID, MAX(m.last_read_date) AS LAST_READ_DATE ");
        sb.append("\nFROM meter m ");
        sb.append("\n     LEFT OUTER JOIN ");
        sb.append("\n     code c ");
        sb.append("\n     ON c.id = m.meter_status ");
        sb.append("\nWHERE 1=1 ");
        sb.append("\nAND   NOT EXISTS (SELECT 'X' ");
        sb.append("\n                  FROM ").append(dayTable).append(" dt ");
        sb.append("\n                  WHERE dt.mdev_type = :mdevType ");
        sb.append("\n                  AND   dt.mdev_id = m.mds_id ");
        sb.append("\n                  AND   dt.yyyymmdd BETWEEN :searchStartDate AND :searchEndDate ");
        sb.append("\n                  AND   dt.channel = :channel ");
//        sb.append("\n                  AND   dt.location_id = :locationId ");
        sb.append("\n                 ) ");
        sb.append("\nAND   m.meter = :meterType ");
        sb.append("\nAND   m.location_id = :locationId ");
        sb.append("\nAND   m.install_date <= :installDate ");

        if (!"".equals(supplierId)) {
            sb.append("\nAND   m.supplier_id = :supplierId ");
        }

        sb.append("\nAND   (c.id IS NULL ");
        sb.append("\n    OR c.code != :deleteCode ");
        sb.append("\n    OR (c.code = :deleteCode AND m.delete_date > :deleteDate) ");
        sb.append("\n) ");
        sb.append("\nGROUP BY m.id ");

        SQLQuery query = getSession().createSQLQuery(sb.toString());

        query.setString("meterType", meterType);
        query.setString("searchStartDate", startDate);
        query.setString("searchEndDate", endDate);
        query.setString("installDate", installDate);
        query.setString("mdevType", CommonConstants.DeviceType.Meter.name());
        query.setInteger("channel", DefaultChannel.Usage.getCode());
        query.setInteger("locationId", Integer.parseInt(locationId));
        query.setString("deleteCode", MeterCodes.DELETE_STATUS.getCode());
        query.setString("deleteDate", startDate + "235959");

        if (!supplierId.isEmpty()) {
            query.setInteger("supplierId", Integer.parseInt(supplierId));
        }

        Integer failureCause1Count = 0;
        Integer failureCause2Count = 0;
        Integer failureCause3Count = 0;
        int period = 0;
        List<Map<String, Object>> list = query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();

        if (list != null && list.size() > 0) {
            for (Map<String, Object> map : list) {
                if (!StringUtil.nullToBlank(map.get("LAST_READ_DATE")).isEmpty()) {
                    // LAST_READ_DATE 와 현재일이 같을 경우 Count
                    if (map.get("LAST_READ_DATE").toString().substring(0, 8).equals(currentDate.substring(0, 8))) {
                        failureCause2Count++;
                    } else {
                        period = 0;
                        try {
                            period = TimeUtil.getDayDuration(map.get("LAST_READ_DATE").toString(), currentDate);
                        } catch (ParseException e) {
                            log.error(e, e);
                        }
                        // 마지막 통신 시간과 현재 시간의 차이가 24시간 이상이면 장애 Count
                        if (period >= 1) {
                            failureCause1Count++;
                        } else {
                            failureCause3Count++;
                        }
                    }
                } else {
                    // 통신 이력이 없을경우 장애 카운트
                    failureCause1Count++;
                }
            }
        }

        result.put("cause1", failureCause1Count.toString());
        result.put("cause2", failureCause2Count.toString());
        result.put("cause3", failureCause3Count.toString());

        return result;
    }

    /**
     * method name : getMeteringValueYearlyData<b/>
     * method Desc : Metering Data 맥스가젯에서 연별 지침값을 조회한다.
     *
     * @param conditionMap
     * @param isTotal
     * @return
     */
    @Override
    public List<Map<String, Object>> getMeteringValueYearlyData(Map<String, Object> conditionMap, boolean isTotal) {
        return getMeteringValueYearlyData(conditionMap, isTotal, false);
    }
       

    /**
     * method name : getMeteringValueYearlyData<b/>
     * method Desc : Metering Data 맥스가젯에서 연별 지침값을 조회한다.
     *               EM/WM/GM 만 사용 가능 ( LASTDAY 테이블이 현재 EM/WM/GM에만 존재)
     *               rf) LASTDAY_EM, LASTDAY_WM, LASTDAY_GM
     *
     * @param conditionMap
     * @param isTotal
     * @param isPrev
     * @return
     */
    @Override
    public List<Map<String, Object>> getMeteringValueYearlyData(Map<String, Object> conditionMap, boolean isTotal, boolean isPrev) {
        List<Map<String, Object>> result;

        Integer supplierId = (Integer)conditionMap.get("supplierId");
        Integer tariffType = (Integer)conditionMap.get("tariffType");
        Integer sicId = (Integer)conditionMap.get("sicId");
        Integer page = (Integer)conditionMap.get("page");
        Integer limit = (Integer)conditionMap.get("limit");

        String contractNumber = StringUtil.nullToBlank(conditionMap.get("contractNumber"));
        String customerName = StringUtil.nullToBlank(conditionMap.get("customerName"));
        String meteringSF = StringUtil.nullToBlank(conditionMap.get("meteringSF"));

        String startDate = null;
        String endDate = null;

        String mcuId = StringUtil.nullToBlank(conditionMap.get("mcuId"));
        String modemId = StringUtil.nullToBlank(conditionMap.get("modemId"));
        String deviceType = StringUtil.nullToBlank(conditionMap.get("deviceType"));
        String mdevId = StringUtil.nullToBlank(conditionMap.get("mdevId"));
        String contractGroup = StringUtil.nullToBlank(conditionMap.get("contractGroup"));
        String meterType = StringUtil.nullToBlank(conditionMap.get("meterType"));
        List<Integer> sicIdList = (List<Integer>)conditionMap.get("sicIdList");
        List<Integer> locationIdList = (List<Integer>)conditionMap.get("locationIdList");
        Set<String> meterNoList = (Set<String>)conditionMap.get("meterNoList");
        String MonthTable = MeterType.valueOf(meterType).getMonthTableName();
        String DayTable = MeterType.valueOf(meterType).getDayTableName();
        

        if (isPrev) {
            startDate = StringUtil.nullToBlank(conditionMap.get("prevStartDate"));
            endDate = StringUtil.nullToBlank(conditionMap.get("prevEndDate"));
        } else {
            startDate = StringUtil.nullToBlank(conditionMap.get("startDate"));
            endDate = StringUtil.nullToBlank(conditionMap.get("endDate"));
        }

        StringBuilder sb = new StringBuilder();
        
        if (isTotal) {
            sb.append("\nSELECT COUNT(*) FROM (");
        }
        
        sb.append("\nSELECT contract_number AS CONTRACT_NUMBER, ");
        sb.append("\n       customer_name AS CUSTOMER_NAME, ");
        sb.append("\n       mds_id AS METER_NO, ");

        if (!isPrev) {
            sb.append("\n       device_serial AS MODEM_ID, ");
        }
        
        sb.append("\n       SIC_NAME, ");
        sb.append("\n       BASEVALUE ");
//        sb.append("\n       SUM(total) AS VALUE ");
        sb.append("\nFROM ( ");
        sb.append("\n    SELECT mt.mds_id, ");
        sb.append("\n           co.contract_number, ");
        sb.append("\n           cu.name AS customer_name, ");

        if (!isPrev) {
            if (!mcuId.isEmpty()) {
                sb.append("\n           mo.device_serial, ");
            } else {
                sb.append("\n           (SELECT md.device_serial FROM modem md WHERE md.id = mt.modem_id ) AS device_serial, ");
            }
        }
        
        sb.append("\n           code.name as SIC_NAME, ");
        sb.append("\n           dy.baseValue as BASEVALUE ");
        sb.append("\n            ");
//        sb.append("\n    FROM ").append(MonthTable).append(" mn ");
        sb.append("\n    FROM ").append(DayTable).append(" dy ");
        sb.append("\n         LEFT OUTER JOIN ");
        sb.append("\n         contract co ");
        sb.append("\n         ON co.id = dy.contract_id ");
        sb.append("\n         LEFT OUTER JOIN ");
        sb.append("\n         code code ");
        sb.append("\n         ON co.sic_id = code.id ");
        sb.append("\n         LEFT OUTER JOIN ");
        sb.append("\n         customer cu ");
        sb.append("\n         ON cu.id = co.customer_id, ");

        if (!contractGroup.isEmpty()) {
            sb.append("\n         group_member gm, ");
        }

        sb.append("\n         meter mt, ");
        sb.append("(SELECT lastdy.mdev_id, lastdy.mdev_type, max(lastdy.yyyymmdd) as yyyymmdd from LAST").append(DayTable).append(" lastdy where lastdy.yyyymmdd  between :startDate and :endDate group by lastdy.mdev_id,lastdy.mdev_type) lastData");

        if (!mcuId.isEmpty()) {
            sb.append("\n         ,modem mo ");
            sb.append("\n         ,mcu mc ");
        }

        if(!modemId.isEmpty()){
            sb.append("\n         ,modem mo ");
        }
        
        sb.append("\n    WHERE dy.yyyymmdd =lastData.yyyymmdd ");
        sb.append("\n    AND   dy.mdev_id = lastData.mdev_id ");
        sb.append("\n    AND   dy.mdev_type = lastData.mdev_type ");
        sb.append("\n    AND   dy.channel = 1 ");

        if (meterNoList != null) {
            sb.append("\n    AND   dy.mdev_id IN (:meterNoList) ");
        }

        sb.append("\n    AND   mt.id = dy.meter_id ");
        sb.append("\n    AND   mt.supplier_id = :supplierId ");

        if (!deviceType.isEmpty()) {
            sb.append("\n    AND   dy.mdev_type = :deviceType ");
        }

        if (meteringSF.equals("s")) {
            sb.append("\n    AND   dy.total_value IS NOT NULL ");
        } else {
            sb.append("\n    AND   dy.total_value IS NULL ");
        }

        if (!mdevId.isEmpty()) {
            sb.append("\n    AND   mt.mds_id LIKE :mdevId ");
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
            sb.append("\n    AND   cu.name LIKE :customerName ");
        }

        if (!mcuId.isEmpty()) {
            sb.append("\n    AND   mo.id = mt.modem_id ");
            sb.append("\n    AND   mc.id = mo.mcu_id ");
            sb.append("\n    AND   mc.sys_id = :mcuId ");
        }
       
        if(!modemId.isEmpty()){
            sb.append("\n    AND   mt.modem_id = mo.id ");
            sb.append("\n    AND   mo.device_serial like :modemId ");
        }
        
        sb.append("\n) x ");
        sb.append("\nGROUP BY mds_id, contract_number, customer_name, SIC_NAME, BASEVALUE ");

        if (!isPrev) {
            sb.append(", device_serial ");
        }

        if (isTotal) {
            sb.append(") y ");
        }

        SQLQuery query = getSession().createSQLQuery(new SQLWrapper().getQuery(sb.toString()));

        query.setString("startDate", startDate);
        query.setString("endDate", endDate);
        query.setInteger("supplierId", supplierId);

        if (meterNoList != null) {
            query.setParameterList("meterNoList", meterNoList);
        }

        if (!deviceType.isEmpty()) {
            query.setString("deviceType", deviceType);
        }

        if (!mdevId.isEmpty()) {
            query.setString("mdevId", "%" + mdevId + "%");
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

        if(!modemId.isEmpty()){
            query.setString("modemId", "%" + modemId + "%");
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
     * method name : getMeteringValueDetailMonthlyData<b/>
     * method Desc : Metering Data 맥스가젯 상세화면에서 월별 지침값을 조회한다.
     *
     * @param conditionMap
     * @return
     */
    @Override
    public List<Map<String, Object>> getMeteringValueDetailMonthlyData(Map<String, Object> conditionMap, boolean isSum) {
        List<Map<String, Object>> result;

        Integer supplierId = (Integer)conditionMap.get("supplierId");
        String searchStartDate = StringUtil.nullToBlank(conditionMap.get("searchStartDate"));
        String searchEndDate = StringUtil.nullToBlank(conditionMap.get("searchEndDate"));
        String meterNo = StringUtil.nullToBlank(conditionMap.get("meterNo"));
        String meterType = StringUtil.nullToBlank(conditionMap.get("meterType"));
        String tlbType = StringUtil.nullToBlank(conditionMap.get("tlbType"));
        List<Integer> channelIdList = (List<Integer>)conditionMap.get("channelIdList");
        String dayTable = MeterType.valueOf(meterType).getDayTableName();

        StringBuilder sb = new StringBuilder();

        if (isSum) {
            sb.append("\nSELECT CHANNEL AS CHANNEL, ");
            sb.append("\n       MAX(BASEVALUE) AS MAX_BASEVAL, ");
            sb.append("\n       MIN(BASEVALUE) AS MIN_BASEVAL, ");
            sb.append("\n       MAX(VALUE) AS MAX_VAL, ");
            sb.append("\n       MIN(VALUE) AS MIN_VAL, ");
            sb.append("\n       AVG(VALUE) AS AVG_VAL, ");
            sb.append("\n       SUM(VALUE) AS SUM_VAL ");
            sb.append("\nFROM ( ");
        } else {
            sb.append("\nSELECT YYYYMMDD, ");
            sb.append("\n       CHANNEL, ");
            sb.append("\n       SUM(VALUE) AS VALUE, ");
            sb.append("\n       SUM(BASEVALUE) AS BASEVALUE ");
            sb.append("\nFROM ( ");
        }

        sb.append("\n    SELECT da.yyyymmdd AS YYYYMMDD, ");
        sb.append("\n           da.channel AS CHANNEL, ");
        sb.append("\n           da.total AS VALUE, ");
        sb.append("\n           da.baseValue AS BASEVALUE, ");
        sb.append("\n          (SELECT DISTINCT dc.ch_method ");
        sb.append("\n           FROM meter mt, ");
        sb.append("\n                meterconfig mc, ");
        sb.append("\n                display_channel dc, ");
        sb.append("\n                channel_config  cc ");
        sb.append("\n           WHERE mc.devicemodel_fk = mt.devicemodel_id ");
        sb.append("\n           AND   cc.meterconfig_id = mc.id ");
        sb.append("\n           AND   cc.data_type = :tlbType ");
        sb.append("\n           AND   dc.id = cc.channel_id ");
        sb.append("\n           AND   mt.id = da.meter_id ");
        sb.append("\n           AND   cc.channel_index = da.channel) AS CH_METHOD ");
        sb.append("\n    FROM ").append(dayTable).append(" da, ");
        sb.append("\n         (SELECT lastdy.mdev_id, lastdy.mdev_type, lastdy.yyyymmdd from LAST").append(dayTable).append(" lastdy where lastdy.yyyymmdd  between :startDate and :endDate) lastData ");
        sb.append("\n    WHERE da.yyyymmdd =lastdata.yyyymmdd ");
        sb.append("\n    AND   da.mdev_id=lastdata.mdev_id ");
        sb.append("\n    AND   da.mdev_type = lastdata.mdev_type ");
        sb.append("\n    AND   da.yyyymmdd BETWEEN :startDate AND :endDate ");
        

        if (channelIdList != null) {
            sb.append("\n    AND   da.channel IN (:channelIdList) ");
        }

        sb.append("\n    AND   da.mdev_id = :meterNo ");
        sb.append("\n    AND   da.supplier_id = :supplierId ");

        if (isSum) {
            sb.append("\n) y ");
            sb.append("\nGROUP BY CHANNEL ");
        } else {
            sb.append("\n) y ");
            sb.append("\nGROUP BY YYYYMMDD, CHANNEL ");
        }

        SQLQuery query = getSession().createSQLQuery(new SQLWrapper().getQuery(sb.toString()));

        query.setString("tlbType", tlbType);
        query.setString("startDate", searchStartDate);
        query.setString("endDate", searchEndDate);
        query.setString("meterNo", meterNo);
        query.setInteger("supplierId", supplierId);

        if (channelIdList != null) {
            query.setParameterList("channelIdList", channelIdList);
        }

        result = query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
        return result;
    }
    
	public List<Map<String, Object>> getRealTimeMeterValues(Map<String, Object> conditionMap, boolean isTotal) {
		List<Map<String, Object>> result;

		Integer supplierId = (Integer) conditionMap.get("supplierId");
		Integer page = (Integer) conditionMap.get("page");
		Integer limit = (Integer) conditionMap.get("limit");
		Integer meterId = (Integer) conditionMap.get("meterId");

		String deviceType = StringUtil.nullToBlank(conditionMap.get("deviceType"));
		String mdevId = StringUtil.nullToBlank(conditionMap.get("mdevId"));
		String startDate = StringUtil.nullToBlank(conditionMap.get("startDate"));
		String prevStartDate = StringUtil.nullToBlank(conditionMap.get("prevStartDate"));
		StringBuffer sqlBuf = new StringBuffer();
		
		if(isTotal) {
			sqlBuf.append("\nSELECT COUNT(*) AS cnt FROM ( ");
		}
		
		sqlBuf.append("SELECT        			                    											\n");
		sqlBuf.append("       metering_em.YYYYMMDDHHMMSS,                										\n");
		sqlBuf.append("       metering_em.CH1,                        											\n");
		sqlBuf.append("       metering_em.CH2,                        											\n");
		sqlBuf.append("       metering_em.CH3,                        											\n");
		sqlBuf.append("       metering_em.CH4,                        											\n");
		sqlBuf.append("       metering_em.CH5,                        											\n");
		sqlBuf.append("       metering_em.CH6,                        											\n");
		sqlBuf.append("       metering_em.CH7                        											\n");
		sqlBuf.append("FROM METERINGDATA_EM metering_em															\n");
		sqlBuf.append("WHERE  metering_em.SUPPLIER_ID = :supplierId                               				\n");
		sqlBuf.append("AND    metering_em.MDEV_ID = :mdevId                                 					\n");
		sqlBuf.append("AND    metering_em.METER_ID = :meterId                                 					\n");
		sqlBuf.append("AND    metering_em.MDEV_TYPE = :deviceType                                 				\n");
		sqlBuf.append("AND    metering_em.YYYYMMDDHHMMSS BETWEEN :startDate AND :endDate						\n"); // SP-1047
		sqlBuf.append("ORDER BY  metering_em.YYYYMMDDHHMMSS DESC												\n");

		if(isTotal) {
			sqlBuf.append("\n) totalCount");
		}
		
		SQLQuery query = getSession().createSQLQuery(sqlBuf.toString());
		query = getSession().createSQLQuery(sqlBuf.toString());
		query.setInteger("supplierId", supplierId);
		query.setString("mdevId", mdevId);
		query.setInteger("meterId", meterId);
		query.setString("deviceType", deviceType);
		query.setString("startDate", startDate+"000000");
		query.setString("endDate", startDate+"235959");
		
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
     * method name : getMeteringDataHourlyData<b/>
     * method Desc : Metering Data 맥스가젯에서 시간별 검침데이터를 조회한다.
     *
     * @param conditionMap
     * @param isTotal
     * @return
     */
    public List<Map<String, Object>> getMeteringDataHourlyData(Map<String, Object> conditionMap, boolean isTotal) {
        return getMeteringDataHourlyData(conditionMap, isTotal, false);
    }

    /**
     * method name : getMeteringDataHourlyData<b/>
     * method Desc : Metering Data 맥스가젯에서 시간별 검침데이터를 조회한다.
     *
     * @param conditionMap
     * @param isTotal
     * @param isPrev
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> getMeteringDataHourlyData(Map<String, Object> conditionMap, boolean isTotal, boolean isPrev) {
        List<Map<String, Object>> result;

        Integer supplierId = (Integer)conditionMap.get("supplierId");
        Integer tariffType = (Integer)conditionMap.get("tariffType");

        Integer sicId = (Integer)conditionMap.get("sicId");
        Integer page = (Integer)conditionMap.get("page");
        Integer limit = (Integer)conditionMap.get("limit");

        String startDate = null;
        String endDate = null;
        String startHour = StringUtil.nullToBlank(conditionMap.get("startHour"));
        String endHour = StringUtil.nullToBlank(conditionMap.get("endHour"));
        String contractNumber = StringUtil.nullToBlank(conditionMap.get("contractNumber"));
        String customerName = StringUtil.nullToBlank(conditionMap.get("customerName"));
        String mdsId = StringUtil.nullToBlank(conditionMap.get("friendlyName"));
        String meteringSF = StringUtil.nullToBlank(conditionMap.get("meteringSF"));
        String mcuId = StringUtil.nullToBlank(conditionMap.get("mcuId"));
        String modemId = StringUtil.nullToBlank(conditionMap.get("modemId"));
        String deviceType = StringUtil.nullToBlank(conditionMap.get("deviceType"));
        String mdevId = StringUtil.nullToBlank(conditionMap.get("mdevId"));
        String gs1 = StringUtil.nullToBlank(conditionMap.get("gs1"));
        String contractGroup = StringUtil.nullToBlank(conditionMap.get("contractGroup"));
        String meterType = StringUtil.nullToBlank(conditionMap.get("meterType"));
        List<Integer> sicIdList = (List<Integer>)conditionMap.get("sicIdList");
        List<Integer> locationIdList = (List<Integer>)conditionMap.get("locationIdList");
        Set<String> meterNoList = (Set<String>)conditionMap.get("meterNoList");
        String dayTable = MeterType.valueOf(meterType).getDayTableName();

        if (isPrev) {
            startDate = StringUtil.nullToBlank(conditionMap.get("prevStartDate"));
            
            endDate = StringUtil.nullToBlank(conditionMap.get("prevEndDate"));
        } else {
            startDate = StringUtil.nullToBlank(conditionMap.get("startDate"));
            endDate = StringUtil.nullToBlank(conditionMap.get("endDate"));
        }

        StringBuilder sb = new StringBuilder();

        if (isTotal) {
            sb.append("\nSELECT COUNT(*) ");
        } else {
        	sb.append("\nSELECT de.yyyymmdd||de.HH AS YYYYMMDDHH,");
        	sb.append("\n		de.dst AS DST,				");
        	sb.append("\n		de.mdev_id AS METER_NO,		");
        	sb.append("\n		mt.friendly_name AS FRIENDLY_NAME,");
        	sb.append("\n		mt.gs1 AS GS1,");
        	sb.append("\n		co.contract_number,			");
        	sb.append("\n		code.name AS SIC_NAME,		");
        	sb.append("\n		mo.device_serial AS MODEM_ID,");
        	sb.append("\n		de.value AS VALUE			");
        }
        sb.append("\nFROM ").append(dayTable).append(" de 				"); 
        sb.append("\nLEFT OUTER JOIN meter mt ON mt.mds_id = de.mdev_id ");
        sb.append("\nLEFT OUTER JOIN modem mo ON mo.id = de.modem_id 	");
        sb.append("\nLEFT OUTER JOIN mcu mc ON mc.id = mo.mcu_id	 	");
        sb.append("\nLEFT OUTER JOIN contract co ON co.id = de.contract_id	");
        sb.append("\nLEFT OUTER JOIN customer cu ON cu.id = co.customer_id	");
        sb.append("\nLEFT OUTER JOIN code code ON co.sic_id = code.id 		");
        sb.append("\nLEFT OUTER JOIN group_member gm ON gm.member = co.contract_number ");

        sb.append("\nWHERE de.yyyymmdd BETWEEN :startDate AND :endDate "); 
        sb.append("\nAND de.hh >= CASE de.yyyymmdd WHEN :startDate THEN :startHour ELSE '00' END "); 
        sb.append("\nAND de.hh <= CASE de.yyyymmdd WHEN :endDate   THEN :endHour   ELSE '23' END "); 
        sb.append("\nAND   de.channel = 1 ");
        sb.append("\nAND   mt.supplier_id = :supplierId ");


        if (!mdevId.isEmpty()) {
        	if(mdevId.indexOf('%') == 0 || mdevId.indexOf('%') == (mdevId.length()-1)) { // %문자가 양 끝에 있을경우
                sb.append("\nAND   de.mdev_id LIKE :mdevId ");
        	}else {
                sb.append("\nAND   de.mdev_id = :mdevId ");
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
                sb.append("\nAND   cu.name LIKE :customerName ");
        	}else {
                sb.append("\nAND   cu.name = :customerName ");
        	}
        }

        if (!mcuId.isEmpty()) {
        	if(mcuId.indexOf('%') == 0 || mcuId.indexOf('%') == (mcuId.length()-1)) { // %문자가 양 끝에 있을경우
            	sb.append("\nAND   mc.sys_id LIKE :mcuId ");
        	}else{
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

        if (meteringSF.equals("s")) {
            sb.append("\nAND   de.value IS NOT NULL ");
        } else {
            sb.append("\nAND   de.value IS NULL ");
        }
        
        if (meterNoList != null) {
            sb.append("\nAND   de.mdev_id IN (:meterNoList) ");
        }
        if (!mdsId.isEmpty()) {
            sb.append("\nAND   de.mdev_id = :mdsId ");
        }
        if (!deviceType.isEmpty()) {
            sb.append("\nAND   de.mdev_type = :deviceType ");
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
        if (!isTotal) {
            sb.append("\nORDER BY de.yyyymmdd, de.hh, de.mdev_id, de.dst ");
        }

        SQLQuery query = getSession().createSQLQuery(new SQLWrapper().getQuery(sb.toString()));

        query.setString("startDate", startDate);
        query.setString("endDate", endDate);
        query.setString("startHour", startHour);
        query.setString("endHour", endHour);
        query.setInteger("supplierId", supplierId);

        if (meterNoList != null) {
            query.setParameterList("meterNoList", meterNoList);
        }

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

        if (!mcuId.isEmpty()) {
            query.setString("mcuId", mcuId);
        }
        
        if(!modemId.isEmpty()){
        	query.setString("modemId", modemId);
        }

        if (!mdsId.isEmpty()) {
            query.setString("mdsId", mdsId);
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
}