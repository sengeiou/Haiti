package com.aimir.dao.device.impl;

import java.io.IOException;
import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.aimir.constants.CommonConstants;
import com.aimir.constants.CommonConstants.DateType;
import com.aimir.constants.CommonConstants.GroupType;
import com.aimir.constants.CommonConstants.MdisTamperingStatus;
import com.aimir.constants.CommonConstants.MeterType;
import com.aimir.dao.AbstractHibernateGenericDao;
import com.aimir.dao.device.MeterMdisDao;
import com.aimir.dao.mvm.MeteringLpDao;
import com.aimir.model.device.Meter;
import com.aimir.model.system.Code;
import com.aimir.util.CalendarUtil;
import com.aimir.util.DateTimeUtil;
import com.aimir.util.SQLWrapper;
import com.aimir.util.StringUtil;
import com.aimir.util.TimeLocaleUtil;
import com.aimir.util.TimeUtil;

/**
 * MeterMdisDaoImpl.java Description 
 * <p>
 * <pre>
 * Date          Version     Author   Description
 * 2011. 11. 22. v1.0        문동규   MDIS 관련 method 를 기존 DaoImpl(MeterDaoImpl) 에서 분리
 * </pre>
 */
@Repository(value = "meterMdisDao")
public class MeterMdisDaoImpl extends AbstractHibernateGenericDao<Meter, Integer> implements MeterMdisDao {

    @Autowired
    MeteringLpDao meteringlpDao;
    
    Log logger = LogFactory.getLog(MeterMdisDaoImpl.class);
    
    @Autowired
    protected MeterMdisDaoImpl(SessionFactory sessionFactory) {
        super(Meter.class);
        super.setSessionFactory(sessionFactory);
    }

    public Meter get(String mdsId) {
        return findByCondition("mdsId", mdsId);
    }
    
    /**
     * 제주 실증단지에서 사용하는 11자리 미터키
     * @param installProperty
     * @return
     */
    public Meter getInstallProperty(String installProperty) {
        return findByCondition("installProperty", installProperty);
    }
    
    // Meter 정보 저장
    public Serializable setMeter(Meter meter) {
        return getSession().save(meter);
    }
    
    @SuppressWarnings("unchecked")
    public Meter getMeterByModemDeviceSerial(String deviceSerial, int modemPort) {
        List meters = getSession().createCriteria(Meter.class)
        .add(Restrictions.eq("modemPort", modemPort))
        .createCriteria("modem")
        .add(Restrictions.eq("deviceSerial", deviceSerial))
        .list();
        
        if (meters.size() > 0)
            return (Meter)meters.get(0);
        else
            return null;
    }
    
    @SuppressWarnings("unchecked")
    public List<Object> getMetersByMcuName(String name) {
        
        StringBuffer query = new StringBuffer();
          
        query.append(" SELECT   m.id ");
        query.append(" FROM     Meter m INNER JOIN m.modem.mcu mcu  ");
        query.append(" WHERE    mcu.sysID = ? ");
       
        Query _query = getSession().createQuery(query.toString());
        _query.setString(1,  name);
        return _query.list();
    }
    
    //검침실패한 미터 목록조회
    @SuppressWarnings("unchecked")
    public Map<String,Object> getMeteringFailureMeter(Map<String,Object> params){
        
        Map<String,Object> resultMap = new HashMap<String,Object>();
        
        String searchStartDate  = StringUtil.nullToBlank(params.get("searchStartDate")); 
        String searchEndDate    = StringUtil.nullToBlank(params.get("searchEndDate"));
        String meterType        = StringUtil.nullToBlank(params.get("meterType"));
        List<Integer> locations = (ArrayList<Integer>)params.get("locationId");
        String customerId       = StringUtil.nullToBlank(params.get("customerId"));
        String meterId          = StringUtil.nullToBlank(params.get("meterId"));
        String mcuId            = StringUtil.nullToBlank(params.get("mcuId"));
        String currPage         = StringUtil.nullToBlank(params.get("currPage"));
        String supplierId       = StringUtil.nullToBlank(params.get("supplierId"));
        
        int period=0;
        try {
            period = TimeUtil.getDayDuration(searchStartDate, searchEndDate) + 1;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        
        Integer searchPeriodCount = period;
        String currentDate        = TimeUtil.getCurrentTimeMilli();
        
        // 미터 타입별 미터링데이터 테이블 설정
        String meteringDataTable = CommonConstants.MeterType.valueOf(meterType).getMeteringTableName();
        
        int rowPerPage = CommonConstants.Paging.ROWPERPAGE_25.getPageNum();
        int firstIdx  = Integer.parseInt(currPage) * rowPerPage;
        
        StringBuffer query = new StringBuffer();
        query.append("\n SELECT  contract.CONTRACT_NUMBER       AS customerId                                       ");
        query.append("\n        ,customer.NAME      AS customerName                                                 ");
        query.append("\n        ,case when customer.ADDRESS is null then '' else customer.ADDRESS end" +
                " CONCAT '-' CONCAT case when customer.ADDRESS1 is null then '' else customer.ADDRESS1 end " +
                " CONCAT ' ' CONCAT case when customer.ADDRESS2 is null then '' else    customer.ADDRESS2 end" +
                " CONCAT '' CONCAT case when customer.ADDRESS3 is null then '' else customer.ADDRESS3 end AS customerAddress    ");
        query.append("\n        ,m.MDS_ID           AS mdsId                                                        ");
        query.append("\n        ,m.ID           AS meterId                                                          ");
        query.append("\n        ,m.ADDRESS          AS meterAddress                                                 ");
        query.append("\n        ,modem.DEVICE_SERIAL            AS modemId                                          ");
        query.append("\n        ,mcu.SYS_NAME       AS mcuId                                                        ");
        query.append("\n        ,m.LAST_READ_DATE   AS lastReadDate                                                 "); //8
        query.append("\n        ,m.METER_STATUS     AS meterStatus                                                  ");
        query.append("\n        ,m.TIME_DIFF        AS timeDiff                                                     "); //10
        query.append("\n FROM METER m LEFT OUTER JOIN ( SELECT md.METER_ID, md.YYYYMMDD                             ");
        query.append("\n                                FROM ").append(meteringDataTable).append(" md               ");
        query.append("\n                                WHERE 1=1                                                   ");
        query.append("\n                                AND md.LOCATION_ID IN (:locationId) -- 지역id                 ");
        query.append("\n                                AND md.YYYYMMDDHHMMSS BETWEEN :searchStartDate AND :searchEndDate ");
        query.append("\n                                GROUP BY md.METER_ID,md.YYYYMMDD                            ");
        query.append("\n                            ) x ON m.ID = x.METER_ID                                        ");
        query.append("\n            LEFT OUTER JOIN CONTRACT contract on m.ID = contract.METER_ID                   ");
        query.append("\n            LEFT OUTER JOIN CUSTOMER customer on contract.CUSTOMER_ID = customer.ID         ");
        query.append("\n            LEFT OUTER JOIN MODEM modem on m.MODEM_ID = modem.ID                            ");
        query.append("\n            LEFT OUTER JOIN MCU mcu on modem.MCU_ID = mcu.ID                                ");
        query.append("\n WHERE 1=1                                                                                  ");
        query.append("\n AND m.METER = :meterType                                                                   ");
        query.append("\n AND m.LOCATION_ID IN (:locationId) -- 지역id                                                 ");
        query.append("\n AND m.INSTALL_DATE <= :currentDate                                                         ");
        
        if(!"".equals(customerId)){
            query.append("\n AND contract.CONTRACT_NUMBER LIKE :customerId CONCAT '%' ");
        }
        if(!"".equals(meterId)){
            query.append("\n AND m.MDS_ID LIKE '%' CONCAT :meterId CONCAT '%' ");
        }
        if(!"".equals(mcuId)){
            query.append("\n AND (mcu.SYS_NAME LIKE '%' CONCAT :mcuId CONCAT '%' OR modem.DEVICE_SERIAL LIKE '%' CONCAT :mcuId CONCAT '%')");
        }
        if(!"".equals(supplierId)){
            query.append("\n AND m.SUPPLIER_ID = :supplierId ");
        }
        query.append("\n GROUP BY m.ID,m.MDS_ID,x.METER_ID,contract.CONTRACT_NUMBER,customer.NAME,customer.ADDRESS,customer.ADDRESS1,customer.ADDRESS2,customer.ADDRESS3,m.ADDRESS,modem.DEVICE_SERIAL,mcu.SYS_NAME,m.LAST_READ_DATE,m.METER_STATUS,m.TIME_DIFF  ");
        query.append("\n HAVING COUNT(m.ID) <> :searchPeriodCount or x.METER_ID is null  ");
            
        // 페이징처리를 위한 전체 데이터건수 조회 쿼리
        StringBuffer countQuery = new StringBuffer();
        countQuery.append("\n SELECT COUNT(*) ");
        countQuery.append("\n FROM (  ");
        countQuery.append(query);
        countQuery.append("\n ) y ");
        
        // 전체건수 조회

        SQLQuery countQueryObj = getSession().createSQLQuery(new SQLWrapper().getQuery(countQuery.toString()));
        countQueryObj.setParameterList("locationId", locations);
        countQueryObj.setString("searchStartDate", searchStartDate+"000000");
        countQueryObj.setString("searchEndDate", searchEndDate+"235959");
        countQueryObj.setString("meterType", meterType);
        countQueryObj.setString("currentDate", currentDate);
        
        if(!"".equals(customerId)){
            countQueryObj.setString("customerId", customerId);
        }
        if(!"".equals(meterId)){
            countQueryObj.setString("meterId", meterId);
        }
        if(!"".equals(mcuId)){
            countQueryObj.setString("mcuId", mcuId);
        }
        if(!"".equals(supplierId)){
            countQueryObj.setInteger("supplierId", Integer.parseInt(supplierId));
        }
        countQueryObj.setInteger("searchPeriodCount", searchPeriodCount);
        
        //logger.debug("\nNative :");
        //logger.debug(countQueryObj.getQueryString());
        Number totalCount = (Number)countQueryObj.uniqueResult();
        
        resultMap.put("totalCount", totalCount.toString());
        
        
        // 페이징 조회
        SQLQuery dataQueryObj = getSession().createSQLQuery(new SQLWrapper().getQuery(query.toString()));
        
        dataQueryObj.setParameterList("locationId", locations);
        dataQueryObj.setString("searchStartDate", searchStartDate);
        dataQueryObj.setString("searchEndDate", searchEndDate);
        dataQueryObj.setString("meterType", meterType);
        dataQueryObj.setString("currentDate", currentDate);
        if(!"".equals(customerId)){
            dataQueryObj.setString("customerId", customerId);
        }
        if(!"".equals(meterId)){
            dataQueryObj.setString("meterId", meterId);
        }
        if(!"".equals(mcuId)){
            dataQueryObj.setString("mcuId", mcuId);
        }
        if(!"".equals(supplierId)){
            dataQueryObj.setInteger("supplierId", Integer.parseInt(supplierId));
        }
        dataQueryObj.setInteger("searchPeriodCount", searchPeriodCount);
        
        dataQueryObj.setFirstResult(firstIdx);
        dataQueryObj.setMaxResults(rowPerPage);
        
        //logger.debug("\nNative :");
        //logger.debug(dataQueryObj.getQueryString());
        
        List<Object> resultList = dataQueryObj.list();
        
        resultMap.put("list", resultList);
        
        return resultMap;
    }
    

    @SuppressWarnings("unchecked")
    public List<Object> getMiniChartMeterTypeByLocation(Map<String, Object> condition){
        String supplierId         = StringUtil.nullToBlank(condition.get("supplierId"));

        List<Object> chartData   = new ArrayList<Object>();
        List<Object> chartSeries = new ArrayList<Object>();
        List<Object> result      = new ArrayList<Object>();
        
        StringBuffer sbQuery      = new StringBuffer();
        StringBuffer sbQueryWhere = new StringBuffer();
        
        // chartSeries
        sbQuery.append(" SELECT loc.ID   AS locId           \n")
               .append("      , loc.NAME AS locName         \n")
               .append("   FROM LOCATION loc                \n")
               .append("   LEFT OUTER JOIN LOCATION loc_p   \n")
               .append("     ON (loc.ID = loc_p.PARENT_ID)  \n")
               .append("  WHERE loc_p.PARENT_ID is null     \n")    
               .append("  AND loc.SUPPLIER_ID = :supplierId \n")
               .append("  ORDER BY loc.ID                   \n");
        
        List locList = null;
        
        SQLQuery query = getSession().createSQLQuery(new SQLWrapper().getQuery(sbQuery.toString()));
        query.setInteger("supplierId", Integer.parseInt(supplierId));
        locList = query.list();
        
        int locListLen = 0;     
        if(locList != null)
            locListLen = locList.size();
        
        for(int i = 0 ; i < locListLen && i < 4 ; i++){
            HashMap chartSerie = new HashMap();
            Object[] resultData = (Object[]) locList.get(i);
            
            chartSerie.put("xField", "xTag");               
            chartSerie.put("yField", "value".concat(Integer.toString(i)));
            chartSerie.put("yCode", resultData[0].toString());
            chartSerie.put("displayName", resultData[1].toString());
            
            chartSeries.add(chartSerie);
            
            sbQueryWhere.append(" , SUM(CASE WHEN LOCATION_ID = " + resultData[0].toString() + " THEN 1 ELSE 0 END) AS value" + i + " \n");
        }
        
        
        // chartData
        sbQuery = new StringBuffer();
        
        sbQuery.append(" SELECT METER AS xTag             \n")      
               .append("      , METER AS xCode            \n")
               .append(sbQueryWhere)                      
               .append("   FROM METER                     \n")
               .append(" WHERE SUPPLIER_ID = :supplierId  \n")
               .append("  GROUP BY METER                  \n");
        
        List dataList = null;
        
        query = getSession().createSQLQuery(new SQLWrapper().getQuery(sbQuery.toString()));     
        query.setInteger("supplierId", Integer.parseInt(supplierId));       
        dataList = query.list();
        
        int dataListLen = 0;
        if(dataList != null)
            dataListLen = dataList.size();
        
        for(int i=0 ; i < dataListLen ; i++){
            
            HashMap chartDataMap = new HashMap();
            Object[] resultData = (Object[]) dataList.get(i);
            
            chartDataMap.put("xTag", resultData[0]);                
            chartDataMap.put("xCode", resultData[1]);
            
            int resultDataLen = resultData.length;
            for(int j=2 ; j < resultDataLen ; j++){
                chartDataMap.put("value".concat(Integer.toString(j-2)) , resultData[j]);
            }
            
            chartData.add(chartDataMap);
            
        }
        
        result.add(chartData);
        result.add(chartSeries);
        
        return result;
        
    }
    @SuppressWarnings("unchecked")
    public List<Object> getMiniChartMeterTypeByCommStatus(Map<String, Object> condition){
        String supplierId         = StringUtil.nullToBlank(condition.get("supplierId"));
        
        List<Object> chartData   = new ArrayList<Object>();
        List<Object> chartSeries = new ArrayList<Object>();
        List<Object> result      = new ArrayList<Object>();
        
        StringBuffer sbQuery      = new StringBuffer();
        
        
        // chartSeries
        HashMap chartSerie1 = new HashMap();
            chartSerie1.put("xField", "xTag");              
            chartSerie1.put("yField", "value0");
            chartSerie1.put("yCode",  "0");
            chartSerie1.put("displayName", "fmtMessage00" );
            chartSeries.add(chartSerie1);

        HashMap chartSerie2 = new HashMap();
            chartSerie2.put("xField", "xTag");              
            chartSerie2.put("yField", "value1");
            chartSerie2.put("yCode",  "1");
            chartSerie2.put("displayName", "fmtMessage24" );
            chartSeries.add(chartSerie2);

        HashMap chartSerie3 = new HashMap();
            chartSerie3.put("xField", "xTag");              
            chartSerie3.put("yField", "value2");
            chartSerie3.put("yCode",  "2");
            chartSerie3.put("displayName", "fmtMessage48" );
            chartSeries.add(chartSerie3);
        
        
            
        // chartData
        sbQuery = new StringBuffer();
        
        sbQuery.append(" SELECT METER AS xTag     \n")
               .append("      , METER AS xCode    \n")
               .append(" , SUM(CASE WHEN LAST_READ_DATE  >= :datePre24H THEN 1 ELSE 0 END) AS value0 \n")           
               .append(" , SUM(CASE WHEN     LAST_READ_DATE < :datePre24H " +
                       "                 AND LAST_READ_DATE >= :datePre48H THEN 1 ELSE 0 END) AS value1 \n")
               .append(" , SUM(CASE WHEN LAST_READ_DATE < :datePre48H THEN 1 ELSE 0 END) AS value2 \n")
               .append("   FROM METER            \n")
               .append(" WHERE SUPPLIER_ID = :supplierId  \n")      
               .append("  GROUP BY METER         \n");
        
        List dataList = null;

        SQLQuery query = getSession().createSQLQuery(sbQuery.toString());
        
        String datePre24H = DateTimeUtil.calcDate(Calendar.HOUR, -24, "yyyyMMddhhmmss");
        String datePre48H = DateTimeUtil.calcDate(Calendar.HOUR, -48, "yyyyMMddhhmmss");
        
        query.setString("datePre24H", datePre24H);
        query.setString("datePre48H", datePre48H);
        query.setInteger("supplierId", Integer.parseInt(supplierId));
        
        dataList = query.list();

        
        int dataListLen = 0;
        if(dataList != null)
            dataListLen = dataList.size();
        
        for(int i=0 ; i < dataListLen ; i++){
            
            HashMap chartDataMap = new HashMap();
            Object[] resultData = (Object[]) dataList.get(i);
            
            chartDataMap.put("xTag", resultData[0]);                
            chartDataMap.put("xCode", resultData[1]);
            
            int resultDataLen = resultData.length;
            for(int j=2 ; j < resultDataLen ; j++){
                chartDataMap.put("value".concat(Integer.toString(j-2)) , resultData[j]);
            }
            
            chartData.add(chartDataMap);
            
        }
        
        result.add(chartData);
        result.add(chartSeries);
        
        return result;
        
    }
    
    @SuppressWarnings("unchecked")         
    public List<Object> getMiniChartLocationByMeterType(Map<String, Object> condition){
        String supplierId         = StringUtil.nullToBlank(condition.get("supplierId"));
        
        List<Object> chartData   = new ArrayList<Object>();
        List<Object> chartSeries = new ArrayList<Object>();
        List<Object> result      = new ArrayList<Object>();
        
        StringBuffer sbQuery      = new StringBuffer();
        StringBuffer sbQueryWhere = new StringBuffer();
        
        // chartSeries
        sbQuery.append(" SELECT METER       \n")
               .append("   FROM METER       \n")
               .append("  GROUP BY METER    \n")
               .append("  ORDER BY METER    \n");
               
        List yCodeList = null;
        
        SQLQuery query = getSession().createSQLQuery(sbQuery.toString());
        yCodeList = query.list();
        
        int yCodeLen = 0;       
        if(yCodeList != null)
            yCodeLen = yCodeList.size();
        
        for(int i = 0 ; i < yCodeLen ; i++){
            HashMap chartSerie = new HashMap();
            String resultData = (String) yCodeList.get(i);
            
            chartSerie.put("xField", "xTag");               
            chartSerie.put("yField", "value".concat(Integer.toString(i)));
            chartSerie.put("yCode", resultData);
            chartSerie.put("displayName", resultData);
            
            chartSeries.add(chartSerie);
            
            sbQueryWhere.append(" , SUM(CASE WHEN me.METER = '" + resultData + "' THEN 1 ELSE 0 END) AS value" + i + " \n");
        }
        
        
        // chartData
        sbQuery = new StringBuffer();
        
        sbQuery.append(" SELECT loc.NAME AS xTag              \n")      
               .append("      , loc.ID   AS xCode             \n")
               .append(sbQueryWhere)
               .append("   FROM METER me                      \n")                  
               .append("   JOIN LOCATION loc                  \n")
               .append("     ON (loc.ID = me.LOCATION_ID)     \n")
               .append("  WHERE me.SUPPLIER_ID = :supplierId  \n")             
               .append("  GROUP BY loc.NAME                   \n")
               .append("         , loc.ID                     \n")
               .append("  ORDER BY loc.ID                     \n");     
        
        List dataList = null;   
        
        query = getSession().createSQLQuery(new SQLWrapper().getQuery(sbQuery.toString()));
        query.setInteger("supplierId", Integer.parseInt(supplierId));
        dataList = query.list();
        
        int dataListLen = 0;
        if(dataList != null)
            dataListLen = dataList.size();
        
        for(int i=0 ; i < dataListLen && i < 4; i++){
            
            HashMap chartDataMap = new HashMap();
            Object[] resultData = (Object[]) dataList.get(i);
            
            chartDataMap.put("xTag", resultData[0].toString());             
            chartDataMap.put("xCode", resultData[1].toString());
            
            int resultDataLen = resultData.length;
            for(int j=2 ; j < resultDataLen ; j++){
                chartDataMap.put("value".concat(Integer.toString(j-2)) , resultData[j]);
            }
            
            chartData.add(chartDataMap);
            
        }
        
        result.add(chartData);
        result.add(chartSeries);
        
        return result;
        
    }
    @SuppressWarnings("unchecked")
    public List<Object> getMiniChartLocationByCommStatus(Map<String, Object> condition){
        String supplierId         = StringUtil.nullToBlank(condition.get("supplierId"));
        
        List<Object> chartData   = new ArrayList<Object>();
        List<Object> chartSeries = new ArrayList<Object>();
        List<Object> result      = new ArrayList<Object>();
        
        StringBuffer sbQuery      = new StringBuffer();
        
        
        // chartSeries
        HashMap chartSerie1 = new HashMap();
            chartSerie1.put("xField", "xTag");              
            chartSerie1.put("yField", "value0");
            chartSerie1.put("yCode",  "0");
            chartSerie1.put("displayName", "fmtMessage00" );
            chartSeries.add(chartSerie1);

        HashMap chartSerie2 = new HashMap();
            chartSerie2.put("xField", "xTag");              
            chartSerie2.put("yField", "value1");
            chartSerie2.put("yCode",  "1");
            chartSerie2.put("displayName", "fmtMessage24" );
            chartSeries.add(chartSerie2);

        HashMap chartSerie3 = new HashMap();
            chartSerie3.put("xField", "xTag");              
            chartSerie3.put("yField", "value2");
            chartSerie3.put("yCode",  "2");
            chartSerie3.put("displayName", "fmtMessage48" );
            chartSeries.add(chartSerie3);
        
            
        // chartData
        sbQuery = new StringBuffer();
        
        sbQuery.append(" SELECT loc.NAME AS xTag              \n")      
               .append("      , loc.ID   AS xCode             \n")
               .append(" , SUM(CASE WHEN LAST_READ_DATE  >= :datePre24H THEN 1 ELSE 0 END) AS value0    \n")            
               .append(" , SUM(CASE WHEN     LAST_READ_DATE < :datePre24H " +
                       "                 AND LAST_READ_DATE >= :datePre48H THEN 1 ELSE 0 END) AS value1 \n")
               .append(" , SUM(CASE WHEN LAST_READ_DATE < :datePre48H THEN 1 ELSE 0 END) AS value2      \n")
               .append("   FROM METER me                      \n")                  
               .append("   JOIN LOCATION loc                  \n")
               .append("     ON (loc.ID = me.LOCATION_ID)     \n")
               .append("  WHERE me.SUPPLIER_ID = :supplierId  \n")             
               .append("  GROUP BY loc.NAME                   \n")
               .append("         , loc.ID                     \n")
               .append("  ORDER BY loc.ID                     \n");
        
        List dataList = null;

        SQLQuery query = getSession().createSQLQuery(new SQLWrapper().getQuery(sbQuery.toString()));
    
        String datePre24H = DateTimeUtil.calcDate(Calendar.HOUR, -24, "yyyyMMddhhmmss");
        String datePre48H = DateTimeUtil.calcDate(Calendar.HOUR, -48, "yyyyMMddhhmmss");
        
        query.setString("datePre24H", datePre24H);
        query.setString("datePre48H", datePre48H);
        query.setInteger("supplierId", Integer.parseInt(supplierId));
        
        dataList = query.list();

        
        int dataListLen = 0;
        if(dataList != null)
            dataListLen = dataList.size();
        
        for(int i=0 ; i < dataListLen && i < 4 ; i++){
            
            HashMap chartDataMap = new HashMap();
            Object[] resultData = (Object[]) dataList.get(i);
            
            chartDataMap.put("xTag", resultData[0].toString());             
            chartDataMap.put("xCode", resultData[1].toString());
            
            int resultDataLen = resultData.length;
            for(int j=2 ; j < resultDataLen ; j++){
                chartDataMap.put("value".concat(Integer.toString(j-2)) , resultData[j]);
            }
            
            chartData.add(chartDataMap);
            
        }
        
        result.add(chartData);
        result.add(chartSeries);
        
        return result;
    }
    
    @SuppressWarnings("unchecked")                   
    public List<Object> getMiniChartCommStatusByMeterType(Map<String, Object> condition){
        String supplierId         = StringUtil.nullToBlank(condition.get("supplierId"));
        
        List<Object> chartData   = new ArrayList<Object>();
        List<Object> chartSeries = new ArrayList<Object>();
        List<Object> result      = new ArrayList<Object>();
        
        StringBuffer sbQuery      = new StringBuffer();
        StringBuffer sbQueryWhere = new StringBuffer();
        
        // chartSeries
        sbQuery.append(" SELECT METER       \n")
               .append("   FROM METER       \n")
               .append("  GROUP BY METER    \n")
               .append("  ORDER BY METER    \n");
               
        List yCodeList = null;
        
        SQLQuery query = getSession().createSQLQuery(sbQuery.toString());
        yCodeList = query.list();
        
        int yCodeLen = 0;       
        if(yCodeList != null)
            yCodeLen = yCodeList.size();
        
        for(int i = 0 ; i < yCodeLen ; i++){
            HashMap chartSerie = new HashMap();
            String resultData = (String) yCodeList.get(i);
            
            chartSerie.put("xField", "xTag");               
            chartSerie.put("yField", "value".concat(Integer.toString(i)));
            chartSerie.put("yCode", resultData);
            chartSerie.put("displayName", resultData);
            
            chartSeries.add(chartSerie);
            
                sbQueryWhere.append(" , SUM(CASE WHEN me.METER = '" + resultData + "' THEN 1 ELSE 0 END) AS value" + i + " \n");
        }
        
        // chartData
        sbQuery = new StringBuffer();
        
        sbQuery.append(" SELECT me.commStatus AS xTag                                       \n")        
               .append("      , me.commStatus AS xCode                                      \n")
               .append(sbQueryWhere)               
               .append("   FROM (SELECT METER                                               \n")           
               .append("              , CASE WHEN LAST_READ_DATE >= :datePre24H THEN '0'        \n")
               .append("                     WHEN     LAST_READ_DATE <  :datePre24H             \n")
               .append("                          AND LAST_READ_DATE >= :datePre48H THEN '1'    \n")
               .append("                     ELSE '2'                                       \n")
               .append("                 END        as commStatus                           \n")
               .append("            FROM METER                                              \n")
               .append("           WHERE SUPPLIER_ID = :supplierId ) me                     \n")
               .append("  GROUP BY me.commStatus                                            \n")               
               .append("  ORDER BY me.commStatus                                            \n");
        
        
        List dataList = null;

        query = getSession().createSQLQuery(new SQLWrapper().getQuery(sbQuery.toString()));
    
        String datePre24H = DateTimeUtil.calcDate(Calendar.HOUR, -24, "yyyyMMddhhmmss");
        String datePre48H = DateTimeUtil.calcDate(Calendar.HOUR, -48, "yyyyMMddhhmmss");
        
        query.setString("datePre24H", datePre24H);
        query.setString("datePre48H", datePre48H);
        query.setInteger("supplierId", Integer.parseInt(supplierId));
        
        dataList = query.list();
        
        int dataListLen = 0;
        if(dataList != null)
            dataListLen = dataList.size();
        
        for(int i=0 ; i < dataListLen && i < 4 ; i++){
            
            HashMap chartDataMap = new HashMap();
            Object[] resultData = (Object[]) dataList.get(i);
            
            chartDataMap.put("xCode", resultData[1].toString());
            
            if(chartDataMap.get("xCode").equals("0"))
                chartDataMap.put("xTag", "fmtMessage00");
            else if(chartDataMap.get("xCode").equals("1"))
                chartDataMap.put("xTag", "fmtMessage24");
            else
                chartDataMap.put("xTag", "fmtMessage48");
            
            
            int resultDataLen = resultData.length;
            for(int j=2 ; j < resultDataLen ; j++){
                chartDataMap.put("value".concat(Integer.toString(j-2)) , resultData[j]);
            }
            
            chartData.add(chartDataMap);
            
        }
        
        result.add(chartData);
        result.add(chartSeries);
        
        return result;      
        
    }
    @SuppressWarnings("unchecked")
    public List<Object> getMiniChartCommStatusByLocation(Map<String, Object> condition){
        String supplierId         = StringUtil.nullToBlank(condition.get("supplierId"));
        
        List<Object> chartData   = new ArrayList<Object>();
        List<Object> chartSeries = new ArrayList<Object>();
        List<Object> result      = new ArrayList<Object>();
        
        StringBuffer sbQuery      = new StringBuffer();
        StringBuffer sbQueryWhere = new StringBuffer();
        
        // chartSeries
        sbQuery.append(" SELECT loc.ID   AS locId           \n")
               .append("      , loc.NAME AS locName         \n")
               .append("   FROM LOCATION loc                \n")
               .append("   LEFT OUTER JOIN LOCATION loc_p   \n")
               .append("     ON (loc.ID = loc_p.PARENT_ID)  \n")
               .append("  WHERE loc_p.PARENT_ID is null     \n")
               .append("  ORDER BY loc.ID                   \n");
        
        List locList = null;
        
        SQLQuery query = getSession().createSQLQuery(sbQuery.toString());
        locList = query.list();
        
        int locListLen = 0;     
        if(locList != null)
            locListLen = locList.size();
        
        for(int i = 0 ; i < locListLen && i < 4 ; i++){
            HashMap chartSerie = new HashMap();
            Object[] resultData = (Object[]) locList.get(i);
            
            chartSerie.put("xField", "xTag");               
            chartSerie.put("yField", "value".concat(Integer.toString(i)));
            chartSerie.put("yCode", resultData[0].toString());
            chartSerie.put("displayName", resultData[1].toString());
            
            chartSeries.add(chartSerie);
            
            sbQueryWhere.append(" , SUM(CASE WHEN LOCATION_ID = " + resultData[0].toString() + " THEN 1 ELSE 0 END) AS value" + i + " \n");
        }
        
        // chartData
        sbQuery = new StringBuffer();
        
        sbQuery.append(" SELECT me.commStatus AS xTag                                       \n")        
               .append("      , me.commStatus AS xCode                                      \n")
               .append(sbQueryWhere)               
               .append("   FROM (SELECT LOCATION_ID                                         \n")           
               .append("              , CASE WHEN LAST_READ_DATE >= :datePre24H THEN '0'        \n")
               .append("                     WHEN     LAST_READ_DATE <  :datePre24H             \n")
               .append("                          AND LAST_READ_DATE >= :datePre48H THEN '1'    \n")
               .append("                     ELSE '2'                                       \n")
               .append("                 END        as commStatus                           \n")
               .append("            FROM METER                                              \n")
               .append("           WHERE SUPPLIER_ID = :supplierId ) me                     \n")        
               .append("  GROUP BY me.commStatus                                            \n")               
               .append("  ORDER BY me.commStatus                                            \n");
        
        
        List dataList = null;

        query = getSession().createSQLQuery(new SQLWrapper().getQuery(sbQuery.toString()));
        
        String datePre24H = DateTimeUtil.calcDate(Calendar.HOUR, -24, "yyyyMMddhhmmss");
        String datePre48H = DateTimeUtil.calcDate(Calendar.HOUR, -48, "yyyyMMddhhmmss");
        
        query.setString("datePre24H", datePre24H);
        query.setString("datePre48H", datePre48H);
        query.setInteger("supplierId", Integer.parseInt(supplierId));
        
        dataList = query.list();
        
        int dataListLen = 0;
        if(dataList != null)
            dataListLen = dataList.size();
        
        for(int i=0 ; i < dataListLen ; i++){
            
            HashMap chartDataMap = new HashMap();
            Object[] resultData = (Object[]) dataList.get(i);
            
            chartDataMap.put("xCode", resultData[1].toString());
            
            if(chartDataMap.get("xCode").equals("0"))
                chartDataMap.put("xTag", "fmtMessage00");
            else if(chartDataMap.get("xCode").equals("1"))
                chartDataMap.put("xTag", "fmtMessage24");
            else 
                chartDataMap.put("xTag", "fmtMessage48");
            
            int resultDataLen = resultData.length;
            for(int j=2 ; j < resultDataLen ; j++){
                chartDataMap.put("value".concat(Integer.toString(j-2)) , resultData[j]);
            }
            
            chartData.add(chartDataMap);
            
        }       
        
        result.add(chartData);
        result.add(chartSeries);
        
        return result;
        
    }
    
    @SuppressWarnings("unchecked")
    public List<Object> getMeterSearchChart(Map<String, Object> condition){

        List<Object> gridData    = new ArrayList<Object>();
        List<Object> chartData   = new ArrayList<Object>();
        List<Object> result      = new ArrayList<Object>();
        StringBuffer sbQuery     = new StringBuffer();

        String sMeterType         = StringUtil.nullToBlank(condition.get("sMeterType"));   
        String sMdsId             = StringUtil.nullToBlank(condition.get("sMdsId"));
        String sStatus            = StringUtil.nullToBlank(condition.get("sStatus"));

        String sMcuId             = StringUtil.nullToBlank(condition.get("sMcuId"));
        String sLocationId        = StringUtil.nullToBlank(condition.get("sLocationId"));
        String sConsumLocationId  = StringUtil.nullToBlank(condition.get("sConsumLocationId"));

        String sVendor            = StringUtil.nullToBlank(condition.get("sVendor"));
        String sModel             = StringUtil.nullToBlank(condition.get("sModel"));
        String sInstallStartDate  = StringUtil.nullToBlank(condition.get("sInstallStartDate"));
        String sInstallEndDate    = StringUtil.nullToBlank(condition.get("sInstallEndDate"));

        String sModemYN           = StringUtil.nullToBlank(condition.get("sModemYN"));
        String sCustomerYN        = StringUtil.nullToBlank(condition.get("sCustomerYN"));
        String sLastcommStartDate = StringUtil.nullToBlank(condition.get("sLastcommStartDate"));
        String sLastcommEndDate   = StringUtil.nullToBlank(condition.get("sLastcommEndDate")); 

        String supplierId         = StringUtil.nullToBlank(condition.get("supplierId"));

        sbQuery.append("       FROM METER me                                    \n")
               .append("       LEFT OUTER JOIN MODEM mo                         \n")
               .append("         ON ( me.MODEM_ID = mo.ID)                      \n")               
               .append("       LEFT OUTER JOIN MCU mcu                          \n")
               .append("         ON ( mo.MCU_ID  = mcu.ID)                      \n")               
               .append("       LEFT OUTER JOIN LOCATION loc                     \n")
               .append("         ON ( me.LOCATION_ID = loc.ID)                  \n")
               .append("       LEFT OUTER JOIN CONTRACT cont                    \n")  // cont, contract 동일한 Contract 테이블이 중복으로 outer join 걸려있음
               .append("         ON ( me.ID = cont.METER_ID)                    \n")
               .append("       LEFT OUTER JOIN (                                \n")
               .append("            SELECT model.ID    AS modelId               \n")
               .append("                 , model.NAME  AS modelName             \n")
               .append("                 , vendor.ID   AS vendorId              \n")
               .append("                 , vendor.NAME as vendorName            \n")
               .append("             FROM DEVICEMODEL model                     \n")
               .append("             LEFT OUTER JOIN DEVICEVENDOR vendor        \n")
               .append("                ON (model.DEVICEVENDOR_ID = vendor.ID)  \n")
               .append("             ) device                                   \n")
               .append("         ON (me.DEVICEMODEL_ID = device.modelId)       \n")
               .append("        LEFT OUTER JOIN CONTRACT contract               \n")  // cont, contract 동일한 Contract 테이블이 중복으로 outer join 걸려있음
               .append("          ON (me.ID = contract.METER_ID)         \n")              
               .append("       WHERE me.SUPPLIER_ID = :supplierId               \n");

        if(!sMeterType.equals(""))
            sbQuery.append("     AND me.METER = '"+ sMeterType +"'");

        if(!sMdsId.equals(""))
            sbQuery.append("     AND me.mds_ID LIKE '%"+ sMdsId +"%'");

        if(!sStatus.equals(""))
            sbQuery.append("     AND me.METER_STATUS = "+ sStatus);

        if(!sMcuId.equals(""))
            sbQuery.append("     AND mo.MCU_ID = "+ sMcuId );                 

        if(!sLocationId.equals(""))
            sbQuery.append("     AND me.LOCATION_ID IN ("+ sLocationId + ")");

        if(!sConsumLocationId.equals(""))
            sbQuery.append("     AND cont.CONTRACT_NUMBER = '"+ sConsumLocationId +"'" );

        if(!sVendor.equals("0") && !sVendor.equals(""))
            sbQuery.append("     AND device.vendorId = "+ sVendor );       

        if(!sModel.equals(""))
            sbQuery.append("     AND device.modelId = "+ sModel );
        
        if(!sInstallStartDate.equals(""))
            sbQuery.append("     AND me.INSTALL_DATE >= '"+ sInstallStartDate +"000000'");

        if(!sInstallEndDate.equals(""))
            sbQuery.append("     AND me.INSTALL_DATE <= '"+ sInstallEndDate +"235900'");
                 
        
        if(sModemYN.equals("Y"))
            sbQuery.append("     AND me.MODEM_ID IS NOT NULL");
        else if(sModemYN.equals("N"))
            sbQuery.append("     AND me.MODEM_ID IS NULL");

        if(sCustomerYN.equals("Y"))
            sbQuery.append("     AND contract.ID IS NOT NULL");
        else if(sCustomerYN.equals("N"))
            sbQuery.append("     AND contract.ID IS NULL");
                          
                   
        if(!sLastcommStartDate.equals(""))
            sbQuery.append("     AND me.LAST_READ_DATE >= '"+ sLastcommStartDate +"000000'");
        
        if(!sLastcommEndDate.equals(""))
            sbQuery.append("     AND me.LAST_READ_DATE <= '"+ sLastcommEndDate +"235900'");
        
                  
        StringBuffer sbQueryGrid = new StringBuffer();
        sbQueryGrid.append("   SELECT mcu.SYS_Id         AS mcuSysId                                                \n")
                   .append("        , COUNT(mo.MCU_ID)   AS totalCnt                                                \n")
                   .append("        , SUM(CASE WHEN me.LAST_READ_DATE  >= :datePre24H THEN 1 ELSE 0 END)    AS value0   \n")            
                   .append("        , SUM(CASE WHEN     me.LAST_READ_DATE < :datePre24H " +
                           "                        AND me.LAST_READ_DATE >= :datePre48H THEN 1 ELSE 0 END) AS value1   \n")
                   .append("        , SUM(CASE WHEN     me.LAST_READ_DATE < :datePre48H THEN 1 ELSE 0 END)  AS value2   \n")
                   .append(sbQuery)
                   .append("      AND mo.MCU_ID IS NOT NULL                                             \n")
                   .append("    GROUP BY mcu.SYS_Id                                                     \n")
                   .append("    ORDER BY totalCnt DESC                                                  \n");       
         
        SQLQuery query = getSession().createSQLQuery(new SQLWrapper().getQuery(sbQueryGrid.toString()));
    
        String datePre24H = DateTimeUtil.calcDate(Calendar.HOUR, -24, "yyyyMMddhhmmss");
        String datePre48H = DateTimeUtil.calcDate(Calendar.HOUR, -48, "yyyyMMddhhmmss");
        
        query.setString("datePre24H", datePre24H);
        query.setString("datePre48H", datePre48H);
        query.setInteger("supplierId", Integer.parseInt(supplierId));
        
        List dataList = null;
        dataList = query.list();
        
        // 실제 데이터
        int dataListLen = 0;
        if(dataList != null)
            dataListLen= dataList.size();
        
        for(int i=0 ; i < dataListLen ; i++){
            
            HashMap gridDataMap  = new HashMap();
            Object[] resultData = (Object[]) dataList.get(i);
            
            gridDataMap.put("no",        i+1);
            gridDataMap.put("mcuSysID",      resultData[0]);
            gridDataMap.put("value0",        resultData[2]);
            gridDataMap.put("value1",        resultData[3]);
            gridDataMap.put("value2",        resultData[4]);
            
            gridData.add(gridDataMap);
        }       
        result.add(gridData);
        
        // Chart Data
        StringBuffer sbQueryChart = new StringBuffer();
        
        sbQueryChart.append("   SELECT '0'                AS commStatus                 \n")
                    .append("        , COUNT(me.ID)       AS cnt                        \n")
                    .append(sbQuery)
                    .append("        AND me.LAST_READ_DATE  >= :datePre24H                  \n")                    
                    .append("   UNION ALL                                               \n")
                    
                    .append("   SELECT '1'                AS commStatus                 \n")
                    .append("        , COUNT(me.ID)       AS cnt                        \n")
                    .append(sbQuery)
                    .append("        AND me.LAST_READ_DATE < :datePre24H                    \n")            
                    .append("        AND me.LAST_READ_DATE >= :datePre48H                   \n")
                    .append("   UNION ALL                                               \n")
                    
                    .append("   SELECT '2'                AS commStatus                 \n")
                    .append("        , COUNT(me.ID)       AS cnt                        \n")
                    .append(sbQuery)
                    .append("         AND me.LAST_READ_DATE < :datePre48H               \n");           
         
        query = getSession().createSQLQuery(new SQLWrapper().getQuery(sbQueryChart.toString()));
        
        query.setString("datePre24H", datePre24H);
        query.setString("datePre48H", datePre48H);
        query.setInteger("supplierId", Integer.parseInt(supplierId));
        
        dataList = null;
        dataList = query.list();
        
        // 실제 데이터
        dataListLen = 0;
        if(dataList != null)
            dataListLen= dataList.size();
        
        for(int i=0 ; i < dataListLen ; i++){
            
            HashMap chartDataMap = new HashMap();
            Object[] resultData = (Object[]) dataList.get(i);
            
            if(resultData[0].toString().equals("0")){               
                chartDataMap.put("label", "fmtMessage");
                chartDataMap.put("data" , resultData[1]);           
            }else if(resultData[0].toString().equals("1")){             
                chartDataMap.put("label", "fmtMessage24");
                chartDataMap.put("data", resultData[1]);            
            }else if(resultData[0].toString().equals("2")){             
                chartDataMap.put("label", "fmtMessage48");
                chartDataMap.put("data", resultData[1]);
            }
                chartData.add(chartDataMap);
        }
        
        result.add(chartData);
        
        return result;
    }

    @SuppressWarnings("unchecked")
    public List<Object> getMeterSearchGrid(Map<String, Object> condition){
        
        List<Object> gridData    = new ArrayList<Object>();
        List<Object> result      = new ArrayList<Object>();
        StringBuffer sbQuery     = new StringBuffer();
        
        String sMeterType         = StringUtil.nullToBlank(condition.get("sMeterType"));   
        String sMdsId             = StringUtil.nullToBlank(condition.get("sMdsId"));
        String sStatus            = StringUtil.nullToBlank(condition.get("sStatus"));

        String sMcuId             = StringUtil.nullToBlank(condition.get("sMcuId"));
        String sLocationId        = StringUtil.nullToBlank(condition.get("sLocationId"));
        String sConsumLocationId  = StringUtil.nullToBlank(condition.get("sConsumLocationId"));
                                                   
        String sVendor            = StringUtil.nullToBlank(condition.get("sVendor"));
        String sModel             = StringUtil.nullToBlank(condition.get("sModel"));
        String sInstallStartDate  = StringUtil.nullToBlank(condition.get("sInstallStartDate"));
        String sInstallEndDate    = StringUtil.nullToBlank(condition.get("sInstallEndDate"));
                                                   
        String sModemYN           = StringUtil.nullToBlank(condition.get("sModemYN"));
        String sCustomerYN        = StringUtil.nullToBlank(condition.get("sCustomerYN"));
        String sMcuName           = StringUtil.nullToBlank(condition.get("sMcuName"));
        String sLastcommStartDate = StringUtil.nullToBlank(condition.get("sLastcommStartDate"));
        String sLastcommEndDate   = StringUtil.nullToBlank(condition.get("sLastcommEndDate")); 
        
        String curPage            = StringUtil.nullToBlank(condition.get("curPage"));
        String sOrder             = StringUtil.nullToBlank(condition.get("sOrder"));
        String sCommState         = StringUtil.nullToBlank(condition.get("sCommState"));
        
        String supplierId         = StringUtil.nullToBlank(condition.get("supplierId"));
        
        
        sbQuery.append("  SELECT me.MDS_ID            AS meterMDS               \n")
               .append("       , me.METER             AS meterType              \n")
               .append("       , mcu.SYS_ID           AS mcuSysID               \n")
               .append("       , device.vendorName    AS vendorName             \n")
               .append("       , device.modelName     AS modelName              \n")
               .append("       , contract.ID          AS customer               \n")
               .append("       , me.INSTALL_DATE      AS installDate            \n")
               .append("       , me.LAST_READ_DATE    AS lastCommDate           \n")
               .append("       , loc.NAME             AS locName                \n")
               .append("       , CASE WHEN LAST_READ_DATE >= :datePre24H THEN '0'   \n")
               .append("              WHEN LAST_READ_DATE <  :datePre24H            \n")
               .append("               AND LAST_READ_DATE >= :datePre48H THEN '1'   \n")
               .append("            WHEN LAST_READ_DATE <  :datePre48H THEN '2'     \n")
               .append("            ELSE '9'                                    \n")
               .append("            END               AS State                  \n")
               .append("       , me.ID                AS meterId                \n")
               .append("       FROM METER me                                    \n")
               .append("       LEFT OUTER JOIN MODEM mo                         \n")
               .append("         ON ( me.MODEM_ID = mo.ID)                      \n")               
               .append("       LEFT OUTER JOIN MCU mcu                          \n")
               .append("         ON ( mo.MCU_ID = mcu.ID)                       \n")               
               .append("       LEFT OUTER JOIN LOCATION loc                     \n")
               .append("         ON ( me.LOCATION_ID = loc.ID)                  \n")
               .append("       LEFT OUTER JOIN CONTRACT cont                    \n")
               .append("         ON ( me.ID = cont.METER_ID)                    \n")
               .append("       LEFT OUTER JOIN (                                \n")
               .append("            SELECT model.ID    AS modelId               \n")
               .append("                 , model.NAME  AS modelName             \n")
               .append("                 , vendor.ID   AS vendorId              \n")
               .append("                 , vendor.NAME as vendorName            \n")
               .append("             FROM DEVICEMODEL model                     \n")
               .append("             LEFT OUTER JOIN DEVICEVENDOR vendor        \n")
               .append("                ON (model.DEVICEVENDOR_ID = vendor.ID)  \n")
               .append("             ) device                                   \n")
               .append("          ON (me.DEVICEMODEL_ID = device.modelId)       \n")
               .append("        LEFT OUTER JOIN CONTRACT contract               \n")
               .append("          ON (me.ID = contract.METER_ID)         \n")              
               .append("       WHERE me.SUPPLIER_ID = :supplierId               \n");
        
        
        if(!sMeterType.equals(""))
            sbQuery.append("     AND me.METER = '"+ sMeterType +"'");
        
        if(!sMdsId.equals(""))
            sbQuery.append("     AND me.mds_ID LIKE '%"+ sMdsId +"%'");
        
        if(!sStatus.equals(""))
            sbQuery.append("     AND me.METER_STATUS = "+ sStatus);


        if(!sMcuId.equals(""))
            sbQuery.append("     AND mo.MCU_ID = "+ sMcuId );                 
        
        if(!sLocationId.trim().equals(""))
            sbQuery.append("     AND me.LOCATION_ID IN ("+ sLocationId +")");
        
        if(!sMcuName.equals(""))
            sbQuery.append("     AND mcu.SYS_ID = '" + sMcuName+ "'"); //LIKE '%"+ sMcuName +"%'"); 
        
        if(!sConsumLocationId.equals(""))
            sbQuery.append("     AND cont.CONTRACT_NUMBER = '"+ sConsumLocationId +"'" );
        
        if(!sVendor.equals("0") && !sVendor.equals(""))
            sbQuery.append("     AND device.vendorId = "+ sVendor );       

        if(!sModel.equals(""))
            sbQuery.append("     AND device.modelId = "+ sModel );
        
        if(!sInstallStartDate.equals(""))
            sbQuery.append("     AND me.INSTALL_DATE >= '"+ sInstallStartDate +"000000'");

        if(!sInstallEndDate.equals(""))
            sbQuery.append("     AND me.INSTALL_DATE <= '"+ sInstallEndDate +"235959'");
                 
        
        if(sModemYN.equals("Y"))
            sbQuery.append("     AND me.MODEM_ID IS NOT NULL");
        else if(sModemYN.equals("N"))
            sbQuery.append("     AND me.MODEM_ID IS NULL");

        if(sCustomerYN.equals("Y"))
            sbQuery.append("     AND contract.ID IS NOT NULL");
        else if(sCustomerYN.equals("N"))
            sbQuery.append("     AND contract.ID IS NULL");
                          
                   
        if(!sLastcommStartDate.equals(""))
            sbQuery.append("     AND me.LAST_READ_DATE >= '"+ sLastcommStartDate +"000000'");
        
        if(!sLastcommEndDate.equals(""))
            sbQuery.append("     AND me.LAST_READ_DATE <= '"+ sLastcommEndDate +"235959'");
        
        if(sCommState.equals("0"))
            sbQuery.append("     AND me.LAST_READ_DATE  >= :datePre24H \n");
        else if(sCommState.equals("1"))
            sbQuery.append("     AND LAST_READ_DATE < :datePre24H " +
                           "     AND LAST_READ_DATE >= :datePre48H \n");        
        else if(sCommState.equals("2"))
            sbQuery.append("     AND LAST_READ_DATE < :datePre48H ");
        
                  
        StringBuffer sbQueryData = new StringBuffer();
        sbQueryData.append(sbQuery);
        
        if(sOrder.equals("1"))
            sbQueryData.append("    ORDER BY me.LAST_READ_DATE DESC                 \n");           
        else if(sOrder.equals("2"))
            sbQueryData.append("    ORDER BY me.LAST_READ_DATE                      \n");
        else if(sOrder.equals("3"))
            sbQueryData.append("    ORDER BY me.INSTALL_DATE DESC                   \n");
        else if(sOrder.equals("4"))
            sbQueryData.append("    ORDER BY me.INSTALL_DATE                        \n");
         
        SQLQuery query = getSession().createSQLQuery(new SQLWrapper().getQuery(sbQueryData.toString()));
        
        String datePre24H = DateTimeUtil.calcDate(Calendar.HOUR, -24, "yyyyMMddhhmmss");
        String datePre48H = DateTimeUtil.calcDate(Calendar.HOUR, -48, "yyyyMMddhhmmss");
        
        query.setString("datePre24H", datePre24H);
        query.setString("datePre48H", datePre48H);
        query.setInteger("supplierId", Integer.parseInt(supplierId));
        
        // Paging
        int rowPerPage = CommonConstants.Paging.ROWPERPAGE.getPageNum();
        int firstIdx  = Integer.parseInt(curPage) * rowPerPage;
        
        query.setFirstResult(firstIdx);
        query.setMaxResults(rowPerPage);
        
        List dateList = null;
        dateList = query.list();
        
        
        // 전체 건수
        StringBuffer countQuery = new StringBuffer();
        countQuery.append("\n SELECT COUNT(countTotal.meterMDS) ");
        countQuery.append("\n FROM (  ");
        countQuery.append(sbQuery);
        countQuery.append("\n ) countTotal ");
        
        SQLQuery countQueryObj = getSession().createSQLQuery(new SQLWrapper().getQuery(countQuery.toString()));
        
        countQueryObj.setString("datePre24H", datePre24H);
        countQueryObj.setString("datePre48H", datePre48H);
        countQueryObj.setInteger("supplierId", Integer.parseInt(supplierId));
                          
        Number totalCount = (Number)countQueryObj.uniqueResult();
        
        result.add(totalCount.toString());
        
        // 실제 데이터
        int dataListLen = 0;
        if(dateList != null)
            dataListLen= dateList.size();
        
        for(int i=0 ; i < dataListLen ; i++){
            
            HashMap chartDataMap = new HashMap();
            Object[] resultData = (Object[]) dateList.get(i);
            
            chartDataMap.put("no",           totalCount.intValue() -i - firstIdx );                       
            chartDataMap.put("meterMds",     resultData[0]);                 
            chartDataMap.put("meterType",    resultData[1]);
            chartDataMap.put("mcuSysID",      resultData[2]);
            chartDataMap.put("vendorName",   resultData[3]);
            chartDataMap.put("modelName",    resultData[4]);
            
            if(resultData[5] != null)
                chartDataMap.put("customer",     "Y");
            else
                chartDataMap.put("customer",     "N");
            
            chartDataMap.put("installDate",  resultData[6]);
            chartDataMap.put("lastCommDate", resultData[7]);
            chartDataMap.put("locName",      resultData[8]);
    
            if(resultData[9].equals('0'))
                chartDataMap.put("commStatus", "fmtMessage00");
            else if(resultData[9].equals('1'))
                chartDataMap.put("commStatus", "fmtMessage24");
            else if(resultData[9].equals('2'))
                chartDataMap.put("commStatus", "fmtMessage48");
            else        
                chartDataMap.put("commStatus", "");
            
            chartDataMap.put("meterId",      resultData[10]);
            
            gridData.add(chartDataMap);
            
        }
    
        result.add(gridData);

        return result;
        
    }
    
    @SuppressWarnings("unchecked")
    public List<Object> getMeterLogChart(Map<String, Object> condition){
        
        String sMeterMds         = StringUtil.nullToBlank(condition.get("meterMds"));
        String sStartDate        = StringUtil.nullToBlank(condition.get("startDate"));
        String sEndDate          = StringUtil.nullToBlank(condition.get("endDate"));
        
        List<Object> chartData   = new ArrayList<Object>();
        List<Object> chartSeries = new ArrayList<Object>();
        List<Object> result      = new ArrayList<Object>();
        
        StringBuffer sbQuery      = new StringBuffer();
        StringBuffer sbQueryDate  = new StringBuffer();

        // 시작날짜 ~ 종료날짜 
        SimpleDateFormat dateForm = new SimpleDateFormat("yyyyMMdd");
        Date startDate = new Date();
        Date endDate   = new Date();
        
        try {
            startDate = dateForm.parse(sStartDate);
            endDate   = dateForm.parse(sEndDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        
        int diffDays = (int)( (endDate.getTime() - startDate.getTime()) /86400000);
        
        List<String> daysList = new ArrayList();

        Calendar calendar = new GregorianCalendar();
        calendar.setTime(startDate);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
         
        daysList.add(sStartDate);
        
        for(int i=0 ; i<diffDays; i++){
             calendar.add(Calendar.DATE, 1);
             daysList.add(formatter.format(calendar.getTime()));
         }
        
         
        // 종료
        
        int daysListLen = 0;        
        if(daysList != null)
            daysListLen = daysList.size();
        
        for(int i = 0 ; i < daysListLen ; i++){
            HashMap chartSerie = new HashMap();
            
            chartSerie.put("xField", "xTag");               
            chartSerie.put("yField", "value".concat(Integer.toString(i)));
            chartSerie.put("yCode",       daysList.get(i));
            chartSerie.put("displayName", daysList.get(i));
            
            chartSeries.add(chartSerie);
            
            sbQueryDate.append("SELECT '" + daysList.get(i) + "'  AS logDate  , 0 AS commLog, 0 AS updateLog, 0 AS brokenLog, 0 AS operLog FROM COMMLOG \n UNION ALL \n" );
        }
        
        // chartData
        sbQuery = new StringBuffer();
        
        sbQuery.append(" SELECT logDate                         \n")
               .append("     , SUM(commLog)                     \n")
               .append("     , SUM(updateLog)                   \n")
               .append("     , SUM(brokenLog)                   \n")
               .append("     , SUM(operLog)                     \n")
               .append("     FROM (                             \n")
               .append(sbQueryDate)            
               .append("  SELECT START_DATE  AS logDate         \n")
               .append("      , COUNT(ID) AS commLog            \n")
               .append("      , 0        AS updateLog           \n")
               .append("      , 0        AS brokenLog           \n")
               .append("      , 0        AS operLog             \n")
               .append("   FROM COMMLOG                         \n")
               .append("  WHERE START_DATE >= :sStartDate       \n")
               .append("    AND START_DATE <= :sEndDate         \n")
               .append("    AND SENDER_ID   = :sMeterMds        \n")
               .append("  GROUP BY START_DATE                   \n")
               .append("  UNION ALL                             \n")
               
               .append("  SELECT YYYYMMDD   AS logDate          \n")
               .append("      , 0        AS commLog             \n")
               .append("      , 0        AS updateLog           \n")
               .append("      , 0        AS brokenLog           \n")
               .append("      , COUNT(ID) AS operLog            \n")
               .append("   FROM OPERATION_LOG                   \n")
               .append("  WHERE YYYYMMDD   >= :sStartDate       \n")
               .append("    AND YYYYMMDD   <= :sEndDate         \n")
               .append("    AND TARGET_NAME = :sMeterMds        \n")
               .append("  GROUP BY YYYYMMDD                     \n")
               .append("  ) rowData                             \n")
               .append("  GROUP BY logDate                      \n")
               .append("  ORDER BY logDate                      \n");       
        
            
        
        SQLQuery query = getSession().createSQLQuery(new SQLWrapper().getQuery(sbQuery.toString()));
        
        query.setString("sStartDate", sStartDate);
        query.setString("sEndDate",   sEndDate);
        query.setString("sMeterMds",  sMeterMds);
        
        List dataList = null;
        dataList = query.list();
        
        int dataListLen = 0;
        if(dataList != null)
            dataListLen = dataList.size();
        
        for(int i=0 ; i < dataListLen ; i++){
            
            HashMap chartDataMap = new HashMap();
            Object[] resultData = (Object[]) dataList.get(i);
            
            chartDataMap.put("xTag",         resultData[0].toString());
            
            chartDataMap.put("commLog",      resultData[1].toString());
            chartDataMap.put("updateLog",    resultData[2].toString());
            chartDataMap.put("brokenLog",    resultData[3].toString());
            chartDataMap.put("operationLog", resultData[4].toString());
            
            chartData.add(chartDataMap);
            
        }
        
        result.add(chartData);
        
        return result;

    }
    

    @SuppressWarnings("unchecked")
    public List<Object> getMeterLogGrid(Map<String, Object> condition){
        
        List<Object> result = new ArrayList<Object>();
        String logType          = condition.get("logType").toString();
        

        // 임시데이터 생성 - charData      
        List<Object> chartData = new ArrayList<Object>();
        
        HashMap t1 = new HashMap();
        HashMap t2 = new HashMap();
        HashMap t3 = new HashMap();
        HashMap t4 = new HashMap();
        HashMap t5 = new HashMap();
        HashMap t6 = new HashMap();
        
        
        if(logType.equals("commLog")){          
            t1.put("number","1");
            t1.put("receiveTime","수신시간_1");
            t1.put("dataType","데이터유형_1");
            t1.put("status","상태_1");
            
            t2.put("number","2");
            t2.put("receiveTime","수신시간_2");
            t2.put("dataType","데이터유형_2");
            t2.put("status","상태_2");

            t3.put("number","3");
            t3.put("receiveTime","수신시간_3");
            t3.put("dataType","데이터유형_3");
            t3.put("status","상태_3");

            t4.put("number","4");
            t4.put("receiveTime","수신시간_4");
            t4.put("dataType","데이터유형_4");
            t4.put("status","상태_4");
            
            t5.put("number","5");
            t5.put("receiveTime","수신시간_5");
            t5.put("dataType","데이터유형_5");
            t5.put("status","상태_5");

            t6.put("number","6");
            t6.put("receiveTime","수신시간_6");
            t6.put("dataType","데이터유형_6");
            t6.put("status","상태_6");
        }
        
        if(logType.equals("changeHistory")){            
            t1.put("number","번호_1");
            t1.put("time","로그시각_1");
            t1.put("operator","수행자_1");
            t1.put("attribute","속성_1");
            t1.put("currentvalue","현재값_1");
            t1.put("beforevalue","이전값_1");

            t2.put("number","번호_2");
            t2.put("time","로그시각_2");
            t2.put("operator","수행자_2");
            t2.put("attribute","속성_2");
            t2.put("currentvalue","현재값_2");
            t2.put("beforevalue","이전값_2");

            t3.put("number","번호_3");
            t3.put("time","로그시각_3");
            t3.put("operator","수행자_3");
            t3.put("attribute","속성_3");
            t3.put("currentvalue","현재값_3");
            t3.put("beforevalue","이전값_3");

            t4.put("number","번호_4");
            t4.put("time","로그시각_4");
            t4.put("operator","수행자_4");
            t4.put("attribute","속성_4");
            t4.put("currentvalue","현재값_4");
            t4.put("beforevalue","이전값_4");

            t5.put("number","번호_5");
            t5.put("time","로그시각_5");
            t5.put("operator","수행자_5");
            t5.put("attribute","속성_5");
            t5.put("currentvalue","현재값_5");
            t5.put("beforevalue","이전값_5");

            t6.put("number","번호_6");
            t6.put("time","로그시각_6");
            t6.put("operator","수행자_6");
            t6.put("attribute","속성_6");
            t6.put("currentvalue","현재값_6");
            t6.put("beforevalue","이전값_6");
            
        }

        if(logType.equals("alertHistory")){         
            t1.put("majorValue","중요도_1");
            t1.put("number","번호_1");
            t1.put("message","메시지_1");
            t1.put("address","주소_1");
            t1.put("location","지역_1");
            t1.put("gmptime","발생시각_1");
            t1.put("closetime","복구시각_1");
            t1.put("duration","지속시간_1");

            t2.put("majorValue","중요도_2");
            t2.put("number","번호_2");
            t2.put("message","메시지_2");
            t2.put("address","주소_2");
            t2.put("location","지역_2");
            t2.put("gmptime","발생시각_2");
            t2.put("closetime","복구시각_2");
            t2.put("duration","지속시간_2");

            t3.put("majorValue","중요도_3");
            t3.put("number","번호_3");
            t3.put("message","메시지_3");
            t3.put("address","주소_3");
            t3.put("location","지역_3");
            t3.put("gmptime","발생시각_3");
            t3.put("closetime","복구시각_3");
            t3.put("duration","지속시간_3");

            t4.put("majorValue","중요도_4");
            t4.put("number","번호_4");
            t4.put("message","메시지_4");
            t4.put("address","주소_4");
            t4.put("location","지역_4");
            t4.put("gmptime","발생시각_4");
            t4.put("closetime","복구시각_4");
            t4.put("duration","지속시간_4");

            t5.put("majorValue","중요도_5");
            t5.put("number","번호_5");
            t5.put("message","메시지_5");
            t5.put("address","주소_5");
            t5.put("location","지역_5");
            t5.put("gmptime","발생시각_5");
            t5.put("closetime","복구시각_5");
            t5.put("duration","지속시간_5");

            t6.put("majorValue","중요도_6");
            t6.put("number","번호_6");
            t6.put("message","메시지_6");
            t6.put("address","주소_6");
            t6.put("location","지역_6");
            t6.put("gmptime","발생시각_6");
            t6.put("closetime","복구시각_6");
            t6.put("duration","지속시간_6");

    
        }

        if(logType.equals("operationLog")){
            
            t1.put("number","번호_1");
            t1.put("time","로그시각_1");
            t1.put("target.type","타겟유형_1");
            t1.put("operator","수행자_1");
            t1.put("board.running","수행자_1");
            t1.put("status","상태_1");
            t1.put("description","설명_1");

            t2.put("number","번호_2");
            t2.put("time","로그시각_2");
            t2.put("target.type","타겟유형_2");
            t2.put("operator","수행자_2");
            t2.put("board.running","수행자_2");
            t2.put("status","상태_2");
            t2.put("description","설명_2");

            t3.put("number","번호_3");
            t3.put("time","로그시각_3");
            t3.put("target.type","타겟유형_3");
            t3.put("operator","수행자_3");
            t3.put("board.running","수행자_3");
            t3.put("status","상태_3");
            t3.put("description","설명_3");

            t4.put("number","번호_4");
            t4.put("time","로그시각_4");
            t4.put("target.type","타겟유형_4");
            t4.put("operator","수행자_4");
            t4.put("board.running","수행자_4");
            t4.put("status","상태_4");
            t4.put("description","설명_4");

            t5.put("number","번호_5");
            t5.put("time","로그시각_5");
            t5.put("target.type","타겟유형_5");
            t5.put("operator","수행자_5");
            t5.put("board.running","수행자_5");
            t5.put("status","상태_5");
            t5.put("description","설명_5");

            t6.put("number","번호_6");
            t6.put("time","로그시각_6");
            t6.put("target.type","타겟유형_6");
            t6.put("operator","수행자_6");
            t6.put("board.running","수행자_6");
            t6.put("status","상태_6");
            t6.put("description","설명_6");
    
        }       
            
        chartData.add(t1);
        chartData.add(t2);
        chartData.add(t3);
        chartData.add(t4);
        chartData.add(t5);
        chartData.add(t6);

        result.add(chartData);
        
        return result;
        
    }
    
    @SuppressWarnings("unchecked")
    public Map<String, Object> getMeterSearchCondition(){
        
        HashMap result           = new HashMap();       
        StringBuffer sbQuery     = new StringBuffer();
        
        sbQuery.append("  SELECT MIN(INSTALL_DATE) AS minDate       ")
               .append("       , MAX(INSTALL_DATE) AS maxDate       ")
               .append("    FROM METER                              ");                           
        
        SQLQuery query = getSession().createSQLQuery(sbQuery.toString());
        
        List dateList = null;
        dateList = query.list();
        Map<String, String> yesterday = DateTimeUtil.calcDate(Calendar.HOUR,
                -24);
        Map<String, String> today = DateTimeUtil.calcDate(Calendar.HOUR, 0);

        String yDate = yesterday.get("date").replace("-", "");
        String tDate = today.get("date").replace("-", "");

        if (dateList != null && dateList.size() > 0) {

            Object[] resultData = (Object[]) dateList.get(0);

            if (resultData != null && resultData.length > 0) {

                if (resultData[0] != null) {
                    if (StringUtil.nullToBlank(resultData[0].toString())
                            .equals(""))
                        result.put("installMinDate", yDate);
                    else
                        result.put("installMinDate", resultData[0].toString()
                                .subSequence(0, 8));
                }
                if (resultData[1] != null) {
                    if (StringUtil.nullToBlank(resultData[1].toString())
                            .equals(""))
                        result.put("installMaxDate", tDate);
                    else
                        result.put("installMaxDate", resultData[1].toString()
                                .subSequence(0, 8));
                }
            }

        }
        if(!result.containsKey("installMinDate")){
            result.put("installMinDate", yDate);
        }
        if(!result.containsKey("installMaxDate")){
            result.put("installMaxDate", tDate);
        }

        result.put("yesterday", yDate);
        result.put("today", tDate);

        return result;  
    }
 
    @SuppressWarnings("unchecked")
    public List<Object> getMeterCommLog(Map<String, Object> condition){
        String sMeterMds         = StringUtil.nullToBlank(condition.get("sMeterMds"));
        String sStartDate        = StringUtil.nullToBlank(condition.get("startDate"));
        String sEndDate          = StringUtil.nullToBlank(condition.get("endDate"));
        String curPage           = StringUtil.nullToBlank(condition.get("curPage"));
        
        List<Object> gridData    = new ArrayList<Object>();
        List<Object> result      = new ArrayList<Object>();
        
        StringBuffer sbQuery      = new StringBuffer();
        
        sbQuery = new StringBuffer();
        
        sbQuery.append(" SELECT START_DATE_TIME              \n") 
               .append("      , OPERATION_CODE               \n")
               .append("      , COMM_RESULT                  \n")
               .append("   FROM COMMLOG                      \n")
               .append("  WHERE START_DATE >= :sStartDate    \n")
               .append("    AND START_DATE <= :sEndDate      \n")
               .append("    AND SENDER_ID  =  :sMeterMds     \n")
               .append("  ORDER BY START_DATE_TIME           \n");
        
        SQLQuery query = getSession().createSQLQuery(sbQuery.toString());
        query.setString("sStartDate",   sStartDate);
        query.setString("sEndDate",     sEndDate);
        query.setString("sMeterMds",    sMeterMds);
        
        // Paging
        int rowPerPage = CommonConstants.Paging.ROWPERPAGE.getPageNum();
        int firstIdx  = Integer.parseInt(curPage) * rowPerPage;
        
        query.setFirstResult(firstIdx);
        query.setMaxResults(rowPerPage);
        
        List dataList = null;
        dataList = query.list();
        
        // 전체 건수
        StringBuffer countQuery = new StringBuffer();
        
        countQuery.append(" SELECT COUNT(ID)                    \n")
                  .append("   FROM COMMLOG                      \n")
                  .append("  WHERE START_DATE >= :sStartDate    \n")
                  .append("    AND START_DATE <= :sEndDate      \n")
                  .append("    AND SENDER_ID  =  :sMeterMds     \n");
           
        SQLQuery countQueryObj = getSession().createSQLQuery(countQuery.toString());
        countQueryObj.setString("sStartDate",   sStartDate);
        countQueryObj.setString("sEndDate",     sEndDate);
        countQueryObj.setString("sMeterMds",    sMeterMds); 
        
        Number totalCount = (Number)countQueryObj.uniqueResult();
        
        result.add(totalCount.toString());
        
        // 실제 데이터
        int dataListLen = 0;
        if(dataList != null)
            dataListLen= dataList.size();
        
        for(int i=0 ; i < dataListLen ; i++){
            
            HashMap chartDataMap = new HashMap();
            Object[] resultData = (Object[]) dataList.get(i);
            
            chartDataMap.put("no",          totalCount.intValue() -i - firstIdx );          
            chartDataMap.put("receiveTime", resultData[0]);                 
            chartDataMap.put("dataType",    resultData[1]);
            chartDataMap.put("status",      resultData[2]);     
            
            gridData.add(chartDataMap);
            
        }
    
        result.add(gridData);
        
        return result;
    }
    
    @SuppressWarnings("unchecked")
    public List<Object> getMeterOperationLog(Map<String, Object> condition){
        String sMeterMds         = StringUtil.nullToBlank(condition.get("sMeterMds"));
        String sStartDate        = StringUtil.nullToBlank(condition.get("startDate"));
        String sEndDate          = StringUtil.nullToBlank(condition.get("endDate"));
        String curPage           = StringUtil.nullToBlank(condition.get("curPage"));
        
        List<Object> gridData    = new ArrayList<Object>();
        List<Object> result      = new ArrayList<Object>();
        
        StringBuffer sbQuery      = new StringBuffer();
        
        sbQuery = new StringBuffer();
        sbQuery.append("  SELECT YYYYMMDDHHMMSS   AS logTime      \n") 
               .append("       , code.NAME      AS targetType         \n")
               .append("       , USER_ID        AS userId             \n")
               .append("       , OPERATOR_TYPE  AS operType           \n")
               .append("       , STATUS         AS status             \n")
               .append("       , DESCRIPTION    AS des                \n")
               .append("    FROM OPERATION_LOG ol                     \n")
               .append("    LEFT OUTER JOIN CODE code                 \n")
               .append("      ON ( ol.TARGET_TYPE_CODE = code.ID)     \n")
               .append("   WHERE YYYYMMDD    >= :sStartDate           \n")
               .append("     AND YYYYMMDD    <= :sEndDate             \n")
               .append("     AND TARGET_NAME  = :sMeterMds            \n")
               .append("  ORDER BY YYYYMMDDHHMMSS DESC            \n");
        
        SQLQuery query = getSession().createSQLQuery(sbQuery.toString());
        query.setString("sStartDate",   sStartDate);
        query.setString("sEndDate",     sEndDate);
        query.setString("sMeterMds",    sMeterMds);
        
        // Paging
        int rowPerPage = CommonConstants.Paging.ROWPERPAGE.getPageNum();
        int firstIdx  = Integer.parseInt(curPage) * rowPerPage;
        
        query.setFirstResult(firstIdx);
        query.setMaxResults(rowPerPage);
        
        List dataList = null;
        dataList = query.list();
        
        // 전체 건수
        StringBuffer countQuery = new StringBuffer();
        
        countQuery.append(" SELECT COUNT(ID)                    \n")
                  .append("    FROM OPERATION_LOG ol            \n")               
                  .append("   WHERE YYYYMMDD    >= :sStartDate  \n")
                  .append("     AND YYYYMMDD    <= :sEndDate    \n")
                  .append("     AND TARGET_NAME  = :sMeterMds   \n");
           
        SQLQuery countQueryObj = getSession().createSQLQuery(countQuery.toString());
        countQueryObj.setString("sStartDate",   sStartDate);
        countQueryObj.setString("sEndDate",     sEndDate);
        countQueryObj.setString("sMeterMds",    sMeterMds); 
        
        Number totalCount = (Number)countQueryObj.uniqueResult();
        
        result.add(totalCount.toString());
        
        // 실제 데이터
        int dataListLen = 0;
        if(dataList != null)
            dataListLen= dataList.size();
        
        for(int i=0 ; i < dataListLen ; i++){
            
            HashMap chartDataMap = new HashMap();
            Object[] resultData = (Object[]) dataList.get(i);
            
            chartDataMap.put("no"          , totalCount.intValue() -i - firstIdx );         
            chartDataMap.put("logTime"     , resultData[0]);                 
            chartDataMap.put("targetType"  , resultData[1]);
            chartDataMap.put("operator"    , resultData[2]);    
            chartDataMap.put("operatorType", resultData[3]);
            chartDataMap.put("status"      , resultData[4]);
            chartDataMap.put("description" , resultData[5]);
            
            gridData.add(chartDataMap);
            
        }
    
        result.add(gridData);
        
        return result;
    }
    
    // ModemID로 할당된 meter의 목록을 조회
    
    @SuppressWarnings("unchecked")
    public List<Object> getMeterListByModem(Map<String, Object> condition){
        List<Object> gridData   = new ArrayList<Object>();
        List<Object> result     = new ArrayList<Object>();
        
        Integer modemId         = (Integer) condition.get("modemId");       
        StringBuffer sbQuery    = new StringBuffer();
        
        sbQuery = new StringBuffer();
        sbQuery.append("  SELECT MDS_ID                     \n") 
               .append("    FROM METER                      \n")
               .append("   WHERE MODEM_ID = " + modemId  + "\n")
               .append("   ORDER BY 1                       \n");
        
        SQLQuery query = getSession().createSQLQuery(sbQuery.toString());
    
        List dataList = null;
        dataList = query.list();        
        
        // 실제 데이터
        int dataListLen = 0;
        if(dataList != null)
            dataListLen= dataList.size();
        
        for(int i=0 ; i < dataListLen ; i++){
            
            HashMap gridDataMap = new HashMap();
            
            gridDataMap.put("no"          , i+1 );          
            gridDataMap.put("mdsId"      , dataList.get(i));                 
            
            
            gridData.add(gridDataMap);
        }
        
        result.add(gridData);
        
        return result;
    }
    
    // modem에 할당되지 않은 meter목록을 조회
    @SuppressWarnings("unchecked")
    public List<Object> getMeterListByNotModem(Map<String, Object> condition){
        
        List<Object> gridData   = new ArrayList<Object>();
        List<Object> result     = new ArrayList<Object>();
        
        Integer supplierId      = (Integer) condition.get("supplierId");
        
        StringBuffer sbQuery    = new StringBuffer();
        
        sbQuery = new StringBuffer();
        sbQuery.append("  SELECT MDS_ID                     \n") 
               .append("    FROM METER                      \n")
               .append("   WHERE SUPPLIER_ID = :supplierId  \n")               
               .append("     AND MODEM_ID is null           \n")
               .append("   ORDER BY 1                       \n");

        SQLQuery query = getSession().createSQLQuery(sbQuery.toString());
        
        query.setInteger("supplierId", supplierId);
    
        List dataList = null;
        dataList = query.list();        
        
        // 실제 데이터
        int dataListLen = 0;
        if(dataList != null)
            dataListLen= dataList.size();
        
        for(int i=0 ; i < dataListLen ; i++){
            
            HashMap gridDataMap = new HashMap();
            
            gridDataMap.put("no"          , i+1 );          
            gridDataMap.put("mdsId"      , dataList.get(i));                 
            
            
            gridData.add(gridDataMap);
        }
        
        result.add(gridData);
        
        
        return result;
    }
    
    
    @SuppressWarnings("unchecked")
    public List<Object> getMeterListForContract(Map<String, Object> condition){
        
        String mdsId            = StringUtil.nullToBlank(condition.get("mdsId"));
        
        StringBuffer sbQuery    = new StringBuffer();
        
        sbQuery = new StringBuffer();
        sbQuery.append(" SELECT CASE WHEN me.MDS_ID = :mdsId THEN 'Y' ELSE 'N' END AS checked   \n") 
               .append("      , me.MDS_ID   AS mdsId        \n")
               .append("      , me.address  AS address      \n")
               .append("      , me.ID       AS meterId      \n")
               .append("   FROM METER me                    \n")
               .append("   LEFT OUTER JOIN CONTRACT con     \n")
               .append("    ON (me.ID = con.meter_ID)       \n")
               .append("  WHERE con.ID is null              \n")
               .append("    OR me.MDS_ID = :mdsId           \n")
               .append("  ORDER BY checked DESC, mdsId      \n");

        SQLQuery query = getSession().createSQLQuery(sbQuery.toString());
        
        query.setString("mdsId", mdsId);
        
        return query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
    }
    
    
    
    
    
    /**
     * 설치일자,공급사 기준 전체 미터갯수를 조회한다.
     * @param params
     * @return
     */
    public Integer getMeterCount(Map<String,Object> params){
        
        String searchStartDate = (String)params.get("searchStartDate");
        String meterType  = StringUtil.nullToBlank(params.get("meterType"));
        String supplierId = StringUtil.nullToBlank(params.get("supplierId"));
        
        StringBuffer sb = new StringBuffer();
        sb.append("\n SELECT COUNT(m.id)  ");
        sb.append("\n FROM ").append(meterType).append(" m ");
        sb.append("\n WHERE 1=1 ");
        sb.append("\n AND m.installDate <= :searchStartDate     ");
        if(!"".equals(supplierId)){
            sb.append("\n AND m.supplier.id = :supplierId ");
        }

        Query query = getSession().createQuery(sb.toString());
        query.setString("searchStartDate", searchStartDate + CommonConstants.DefaultDate.LAST_HHMMSS.getValue());
        if(!"".equals(supplierId)){
            query.setInteger("supplierId", Integer.parseInt(supplierId));
        }

        Number totalCount = (Number)query.uniqueResult();
         
        return totalCount.intValue();
    }
    
    /**
     * LP_XX(전기,가스,수도,열량) 에 검침된 데이터가 특정일자가 전체 누락된
     * 미터의 갯수를 조회한다.
     * (누락건이 없는 미터수 + 부분누락된 미터수)를 조회하여 전체 미터수에서 빼서 계산한다.
     * @param params
     * @return
     */
    public Integer getAllMissingMeterCount(Map<String,Object> params){
        
        String searchStartDate  = (String)params.get("searchStartDate");
        String searchEndDate    = (String)params.get("searchEndDate");
        Integer channel         = (Integer)params.get("channel");
        String meterType        = StringUtil.nullToBlank(params.get("meterType"));
        String supplierId       = StringUtil.nullToBlank(params.get("supplierId"));
        
        int period=0;
        try {
            period = TimeUtil.getDayDuration(searchStartDate, searchEndDate) + 1;
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Integer totalCount = getMeterCount(params);
        
        String lpTable = CommonConstants.MeterType.valueOf(meterType).getLpTableName();
        
        StringBuffer sbQuery = new StringBuffer();
        sbQuery.append("\n select count(y.id)                                                        ");
        sbQuery.append("\n from (                                                                 ");
        sbQuery.append("\n select x.id as id                                                  ");
        sbQuery.append("\n from(                                                                  ");
        sbQuery.append("\n select m.id,lp.yyyymmdd                                                ");
        sbQuery.append("\n       from meter m left outer join ").append(lpTable).append(" lp on lp.meter_id = m.id      ");                            
        sbQuery.append("\n       and lp.yyyymmdd between :searchStartDate and :searchEndDate                ");
        sbQuery.append("\n       and lp.channel = :channel                                               ");
        sbQuery.append("\n where 1=1                                                              ");
        sbQuery.append("\n AND m.install_Date <= :installDate     ");
        if(!"".equals(supplierId)){
            sbQuery.append("\n AND m.supplier_id = :supplierId ");
        }
        sbQuery.append("\n group by m.id,lp.yyyymmdd                                              ");
        sbQuery.append("\n )x                                                                     ");
        sbQuery.append("\n where x.yyyymmdd is not null                                           ");
        sbQuery.append("\n group by x.id                                                          ");
        sbQuery.append("\n having count(x.id) = :period                                                  ");
        sbQuery.append("\n )y                                                                     ");

        Query query = getSession().createSQLQuery(sbQuery.toString());
        query.setString("searchStartDate", searchStartDate);
        query.setString("searchEndDate", searchEndDate);
        query.setString("installDate", searchStartDate + CommonConstants.DefaultDate.LAST_HHMMSS.getValue());
        query.setInteger("channel", channel);
        query.setInteger("period", period);
        if(!"".equals(supplierId)){
            query.setInteger("supplierId", Integer.parseInt(supplierId));
        }
        
        Number notAllMissingCount = (Number)query.uniqueResult();
         
        return totalCount - notAllMissingCount.intValue();
    }
    
    /**
     * LP_XX(전기,가스,수도,열량) 에 검침된 데이터가 부분적(특정시간,시간의 특정주기)으로 누락된
     * 미터의 갯수를 조회한다.
     * 조회기간에 현재일자가 포함되어있을경우
     * 1.조회시작일~조회종료일전일
     * 2.조회종료일,00시~ 현재시간 전시간
     * 3.조회종료일,현재시간
     * 세가지 조건으로 각각의 쿼리문을 수행한다.
     * @param params
     * @return
     */
    public Integer getPatialMissingMeterCount(Map<String,Object> params){
        
        String searchStartDate  = StringUtil.nullToBlank(params.get("searchStartDate")); 
        String searchEndDate    = StringUtil.nullToBlank(params.get("searchEndDate"));
        String meterType        = StringUtil.nullToBlank(params.get("meterType"));
        String supplierId       = StringUtil.nullToBlank(params.get("supplierId"));
        Integer channel         = (Integer)params.get("channel");
        
        String today            = TimeUtil.getCurrentTimeMilli(); //yyyymmddhhmmss
        String currDate         = today.substring(0, 8);
        String currHour         = today.substring(8, 10);
        Integer currMinute      = Integer.parseInt(today.substring(10, 12));
        
        String lpTable = CommonConstants.MeterType.valueOf(meterType).getLpTableName();
        
        StringBuffer sbQuery = new StringBuffer();
        sbQuery.append("\n select count(y.id)                                                                        ");
        sbQuery.append("\n from(                                                                                  ");
        sbQuery.append("\n select x.id as id ,count(x.id) as count                                                                  ");
        sbQuery.append("\n from (                                                                                 ");
        sbQuery.append("\n       select yyyymmdd,m.id,count(m.id) as successCnt                                      "); 
        sbQuery.append("\n       from meter m join ").append(lpTable).append(" lp on lp.meter_id = m.id                                 "); 
        sbQuery.append("\n       where lp.yyyymmdd between :searchStartDate and :searchEndDate                              ");
        sbQuery.append("\n       and lp.channel = :channel                                                               ");
        sbQuery.append("\n AND m.install_Date <= :installDate     ");
        if(!"".equals(supplierId)){
            sbQuery.append("\n AND m.supplier_id = :supplierId ");
        }
        sbQuery.append("\n       and 0 = case when m.lp_interval = 1 then case when lp.value_00 is null           "); 
        sbQuery.append("\n                          OR lp.value_01 is null                                                  "); 
        sbQuery.append("\n                          OR lp.value_02 is null                                                  "); 
        sbQuery.append("\n                          OR lp.value_03 is null                                                  "); 
        sbQuery.append("\n                          OR lp.value_04 is null                                                  "); 
        sbQuery.append("\n                          OR lp.value_05 is null                                                  "); 
        sbQuery.append("\n                          OR lp.value_06 is null                                                  "); 
        sbQuery.append("\n                          OR lp.value_07 is null                                                  "); 
        sbQuery.append("\n                          OR lp.value_08 is null                                                  "); 
        sbQuery.append("\n                          OR lp.value_09 is null                                                  "); 
        sbQuery.append("\n                          OR lp.value_10 is null                                                  "); 
        sbQuery.append("\n                          OR lp.value_11 is null                                                  "); 
        sbQuery.append("\n                          OR lp.value_12 is null                                                  "); 
        sbQuery.append("\n                          OR lp.value_13 is null                                                  "); 
        sbQuery.append("\n                          OR lp.value_14 is null                                                  "); 
        sbQuery.append("\n                          OR lp.value_15 is null                                                  "); 
        sbQuery.append("\n                          OR lp.value_16 is null                                                  "); 
        sbQuery.append("\n                          OR lp.value_17 is null                                                  "); 
        sbQuery.append("\n                          OR lp.value_18 is null                                                  "); 
        sbQuery.append("\n                          OR lp.value_19 is null                                                  "); 
        sbQuery.append("\n                          OR lp.value_20 is null                                                  "); 
        sbQuery.append("\n                          OR lp.value_21 is null                                                  "); 
        sbQuery.append("\n                          OR lp.value_22 is null                                                  "); 
        sbQuery.append("\n                          OR lp.value_23 is null                                                  "); 
        sbQuery.append("\n                          OR lp.value_24 is null                                                  "); 
        sbQuery.append("\n                          OR lp.value_25 is null                                                  "); 
        sbQuery.append("\n                          OR lp.value_26 is null                                                  "); 
        sbQuery.append("\n                          OR lp.value_27 is null                                                  "); 
        sbQuery.append("\n                          OR lp.value_28 is null                                                  "); 
        sbQuery.append("\n                          OR lp.value_29 is null                                                  "); 
        sbQuery.append("\n                          OR lp.value_30 is null                                                  "); 
        sbQuery.append("\n                          OR lp.value_31 is null                                                  "); 
        sbQuery.append("\n                          OR lp.value_32 is null                                                  "); 
        sbQuery.append("\n                          OR lp.value_33 is null                                                  "); 
        sbQuery.append("\n                          OR lp.value_34 is null                                                  "); 
        sbQuery.append("\n                          OR lp.value_35 is null                                                  "); 
        sbQuery.append("\n                          OR lp.value_36 is null                                                  "); 
        sbQuery.append("\n                          OR lp.value_37 is null                                                  "); 
        sbQuery.append("\n                          OR lp.value_38 is null                                                  "); 
        sbQuery.append("\n                          OR lp.value_39 is null                                                  "); 
        sbQuery.append("\n                          OR lp.value_40 is null                                                  "); 
        sbQuery.append("\n                          OR lp.value_41 is null                                                  "); 
        sbQuery.append("\n                          OR lp.value_42 is null                                                  "); 
        sbQuery.append("\n                          OR lp.value_43 is null                                                  "); 
        sbQuery.append("\n                          OR lp.value_44 is null                                                  "); 
        sbQuery.append("\n                          OR lp.value_45 is null                                                  "); 
        sbQuery.append("\n                          OR lp.value_46 is null                                                  "); 
        sbQuery.append("\n                          OR lp.value_47 is null                                                  "); 
        sbQuery.append("\n                          OR lp.value_48 is null                                                  "); 
        sbQuery.append("\n                          OR lp.value_49 is null                                                  "); 
        sbQuery.append("\n                          OR lp.value_50 is null                                                  "); 
        sbQuery.append("\n                          OR lp.value_51 is null                                                  "); 
        sbQuery.append("\n                          OR lp.value_52 is null                                                  "); 
        sbQuery.append("\n                          OR lp.value_53 is null                                                  "); 
        sbQuery.append("\n                          OR lp.value_54 is null                                                  "); 
        sbQuery.append("\n                          OR lp.value_55 is null                                                  "); 
        sbQuery.append("\n                          OR lp.value_56 is null                                                  "); 
        sbQuery.append("\n                          OR lp.value_57 is null                                                  "); 
        sbQuery.append("\n                          OR lp.value_58 is null                                                  "); 
        sbQuery.append("\n                          OR lp.value_59 is null then 1 else 0 end                                "); 
        sbQuery.append("\n        when m.lp_interval = 5 then case when lp.value_00 is null                       "); 
        sbQuery.append("\n                          OR lp.value_05 is null                                                  "); 
        sbQuery.append("\n                          OR lp.value_10 is null                                                  "); 
        sbQuery.append("\n                          OR lp.value_15 is null                                                  "); 
        sbQuery.append("\n                          OR lp.value_20 is null                                                  "); 
        sbQuery.append("\n                          OR lp.value_25 is null                                                  "); 
        sbQuery.append("\n                          OR lp.value_30 is null                                                  "); 
        sbQuery.append("\n                          OR lp.value_35 is null                                                  "); 
        sbQuery.append("\n                          OR lp.value_40 is null                                                  "); 
        sbQuery.append("\n                          OR lp.value_45 is null                                                  "); 
        sbQuery.append("\n                          OR lp.value_50 is null                                                  "); 
        sbQuery.append("\n                          OR lp.value_55 is null then 1 else 0 end                                "); 
        sbQuery.append("\n       when m.lp_interval = 10 then case when lp.value_00 is null                       "); 
        sbQuery.append("\n                          OR lp.value_10 is null                                                  "); 
        sbQuery.append("\n                          OR lp.value_20 is null                                                  "); 
        sbQuery.append("\n                          OR lp.value_30 is null                                                  "); 
        sbQuery.append("\n                          OR lp.value_40 is null                                                  "); 
        sbQuery.append("\n                          OR lp.value_50 is null then 1 else 0 end                                "); 
        sbQuery.append("\n       when m.lp_interval = 15 then case when lp.value_00 is null                       "); 
        sbQuery.append("\n                          OR lp.value_15 is null                                                  "); 
        sbQuery.append("\n                          OR lp.value_30 is null                                                  "); 
        sbQuery.append("\n                          OR lp.value_45 is null then 1 else 0 end                                "); 
        sbQuery.append("\n       when m.lp_interval = 30 then case when lp.value_00 is null                       "); 
        sbQuery.append("\n                      OR lp.value_30 is null then 1 else 0 end                                "); 
        sbQuery.append("\n       when m.lp_interval = 60 then case when lp.value_00 is null then 1 else 0 end     "); 
        sbQuery.append("\n       else 0 end                                                                       "); 
        sbQuery.append("\n       group by yyyymmdd,m.id                                                           "); 
        sbQuery.append("\n       having count(*) <> 24                                                            ");  
        sbQuery.append("\n       ) x                                                                              "); 
        sbQuery.append("\n       group by x.id                                                                    ");
        sbQuery.append("\n ) y                                                                                   ");
        
        
        StringBuffer sbQueryToday = new StringBuffer();
        sbQueryToday.append("\n select count(y.id)                                                                        ");
        sbQueryToday.append("\n from(                                                                                  ");
        sbQueryToday.append("\n select x.id as id,count(x.id)      as count                                                             ");
        sbQueryToday.append("\n from (                                                                                 ");
        sbQueryToday.append("\n          select yyyymmdd,m.id,count(m.id) as successCnt                                      "); 
        sbQueryToday.append("\n          from meter m join ").append(lpTable).append(" lp on lp.meter_id = m.id                                 "); 
        sbQueryToday.append("\n          where lp.yyyymmdd = :searchEndDate                              ");
        sbQueryToday.append("\n          and lp.hh < :currHour                              ");
        sbQueryToday.append("\n          and lp.channel = :channel                                                               ");
        sbQueryToday.append("\n AND m.install_Date <= :installDate     ");
        if(!"".equals(supplierId)){
            sbQueryToday.append("\n AND m.supplier_id = :supplierId ");
        }
        sbQueryToday.append("\n          and 0 = case when m.lp_interval = 1 then case when lp.value_00 is null           "); 
        sbQueryToday.append("\n                             OR lp.value_01 is null                                                  "); 
        sbQueryToday.append("\n                             OR lp.value_02 is null                                                  "); 
        sbQueryToday.append("\n                             OR lp.value_03 is null                                                  "); 
        sbQueryToday.append("\n                             OR lp.value_04 is null                                                  "); 
        sbQueryToday.append("\n                             OR lp.value_05 is null                                                  "); 
        sbQueryToday.append("\n                             OR lp.value_06 is null                                                  "); 
        sbQueryToday.append("\n                             OR lp.value_07 is null                                                  "); 
        sbQueryToday.append("\n                             OR lp.value_08 is null                                                  "); 
        sbQueryToday.append("\n                             OR lp.value_09 is null                                                  "); 
        sbQueryToday.append("\n                             OR lp.value_10 is null                                                  "); 
        sbQueryToday.append("\n                             OR lp.value_11 is null                                                  "); 
        sbQueryToday.append("\n                             OR lp.value_12 is null                                                  "); 
        sbQueryToday.append("\n                             OR lp.value_13 is null                                                  "); 
        sbQueryToday.append("\n                             OR lp.value_14 is null                                                  "); 
        sbQueryToday.append("\n                             OR lp.value_15 is null                                                  "); 
        sbQueryToday.append("\n                             OR lp.value_16 is null                                                  "); 
        sbQueryToday.append("\n                             OR lp.value_17 is null                                                  "); 
        sbQueryToday.append("\n                             OR lp.value_18 is null                                                  "); 
        sbQueryToday.append("\n                             OR lp.value_19 is null                                                  "); 
        sbQueryToday.append("\n                             OR lp.value_20 is null                                                  "); 
        sbQueryToday.append("\n                             OR lp.value_21 is null                                                  "); 
        sbQueryToday.append("\n                             OR lp.value_22 is null                                                  "); 
        sbQueryToday.append("\n                             OR lp.value_23 is null                                                  "); 
        sbQueryToday.append("\n                             OR lp.value_24 is null                                                  "); 
        sbQueryToday.append("\n                             OR lp.value_25 is null                                                  "); 
        sbQueryToday.append("\n                             OR lp.value_26 is null                                                  "); 
        sbQueryToday.append("\n                             OR lp.value_27 is null                                                  "); 
        sbQueryToday.append("\n                             OR lp.value_28 is null                                                  "); 
        sbQueryToday.append("\n                             OR lp.value_29 is null                                                  "); 
        sbQueryToday.append("\n                             OR lp.value_30 is null                                                  "); 
        sbQueryToday.append("\n                             OR lp.value_31 is null                                                  "); 
        sbQueryToday.append("\n                             OR lp.value_32 is null                                                  "); 
        sbQueryToday.append("\n                             OR lp.value_33 is null                                                  "); 
        sbQueryToday.append("\n                             OR lp.value_34 is null                                                  "); 
        sbQueryToday.append("\n                             OR lp.value_35 is null                                                  "); 
        sbQueryToday.append("\n                             OR lp.value_36 is null                                                  "); 
        sbQueryToday.append("\n                             OR lp.value_37 is null                                                  "); 
        sbQueryToday.append("\n                             OR lp.value_38 is null                                                  "); 
        sbQueryToday.append("\n                             OR lp.value_39 is null                                                  "); 
        sbQueryToday.append("\n                             OR lp.value_40 is null                                                  "); 
        sbQueryToday.append("\n                             OR lp.value_41 is null                                                  "); 
        sbQueryToday.append("\n                             OR lp.value_42 is null                                                  "); 
        sbQueryToday.append("\n                             OR lp.value_43 is null                                                  "); 
        sbQueryToday.append("\n                             OR lp.value_44 is null                                                  "); 
        sbQueryToday.append("\n                             OR lp.value_45 is null                                                  "); 
        sbQueryToday.append("\n                             OR lp.value_46 is null                                                  "); 
        sbQueryToday.append("\n                             OR lp.value_47 is null                                                  "); 
        sbQueryToday.append("\n                             OR lp.value_48 is null                                                  "); 
        sbQueryToday.append("\n                             OR lp.value_49 is null                                                  "); 
        sbQueryToday.append("\n                             OR lp.value_50 is null                                                  "); 
        sbQueryToday.append("\n                             OR lp.value_51 is null                                                  "); 
        sbQueryToday.append("\n                             OR lp.value_52 is null                                                  "); 
        sbQueryToday.append("\n                             OR lp.value_53 is null                                                  "); 
        sbQueryToday.append("\n                             OR lp.value_54 is null                                                  "); 
        sbQueryToday.append("\n                             OR lp.value_55 is null                                                  "); 
        sbQueryToday.append("\n                             OR lp.value_56 is null                                                  "); 
        sbQueryToday.append("\n                             OR lp.value_57 is null                                                  "); 
        sbQueryToday.append("\n                             OR lp.value_58 is null                                                  "); 
        sbQueryToday.append("\n                             OR lp.value_59 is null then 1 else 0 end                                "); 
        sbQueryToday.append("\n           when m.lp_interval = 5 then case when lp.value_00 is null                       "); 
        sbQueryToday.append("\n                             OR lp.value_05 is null                                                  "); 
        sbQueryToday.append("\n                             OR lp.value_10 is null                                                  "); 
        sbQueryToday.append("\n                             OR lp.value_15 is null                                                  "); 
        sbQueryToday.append("\n                             OR lp.value_20 is null                                                  "); 
        sbQueryToday.append("\n                             OR lp.value_25 is null                                                  "); 
        sbQueryToday.append("\n                             OR lp.value_30 is null                                                  "); 
        sbQueryToday.append("\n                             OR lp.value_35 is null                                                  "); 
        sbQueryToday.append("\n                             OR lp.value_40 is null                                                  "); 
        sbQueryToday.append("\n                             OR lp.value_45 is null                                                  "); 
        sbQueryToday.append("\n                             OR lp.value_50 is null                                                  "); 
        sbQueryToday.append("\n                             OR lp.value_55 is null then 1 else 0 end                                "); 
        sbQueryToday.append("\n          when m.lp_interval = 10 then case when lp.value_00 is null                       "); 
        sbQueryToday.append("\n                             OR lp.value_10 is null                                                  "); 
        sbQueryToday.append("\n                             OR lp.value_20 is null                                                  "); 
        sbQueryToday.append("\n                             OR lp.value_30 is null                                                  "); 
        sbQueryToday.append("\n                             OR lp.value_40 is null                                                  "); 
        sbQueryToday.append("\n                             OR lp.value_50 is null then 1 else 0 end                                "); 
        sbQueryToday.append("\n          when m.lp_interval = 15 then case when lp.value_00 is null                       "); 
        sbQueryToday.append("\n                             OR lp.value_15 is null                                                  "); 
        sbQueryToday.append("\n                             OR lp.value_30 is null                                                  "); 
        sbQueryToday.append("\n                             OR lp.value_45 is null then 1 else 0 end                                "); 
        sbQueryToday.append("\n          when m.lp_interval = 30 then case when lp.value_00 is null                       "); 
        sbQueryToday.append("\n                         OR lp.value_30 is null then 1 else 0 end                                "); 
        sbQueryToday.append("\n          when m.lp_interval = 60 then case when lp.value_00 is null then 1 else 0 end     "); 
        sbQueryToday.append("\n          else 0 end                                                                       "); 
        sbQueryToday.append("\n          group by yyyymmdd,m.id                                                           "); 
        sbQueryToday.append("\n          having count(*) <> :hourCnt                                                            ");  
        sbQueryToday.append("\n          ) x                                                                              "); 
        sbQueryToday.append("\n          group by x.id                                                                    ");
        sbQueryToday.append("\n ) y                                                                                   ");

        StringBuffer sbQueryCurrHour = new StringBuffer();
        sbQueryCurrHour.append("\n       select count(m.id) as successCnt                                      "); 
        sbQueryCurrHour.append("\n       from meter m join ").append(lpTable).append(" lp on lp.meter_id = m.id                                 "); 
        sbQueryCurrHour.append("\n       where lp.yyyymmdd = :searchEndDate                             ");
        sbQueryCurrHour.append("\n       and lp.hh = :currHour                              ");
        sbQueryCurrHour.append("\n       and lp.channel = :channel                                                               ");
        sbQueryCurrHour.append("\n AND m.install_Date <= :installDate     ");
        if(!"".equals(supplierId)){
            sbQueryCurrHour.append("\n AND m.supplier_id = :supplierId ");
        }
        sbQueryCurrHour.append("\n       and 1 = case when m.lp_interval = 1 then case when lp.value_00 is null           "); 
        if(currMinute >= 1)sbQueryCurrHour.append("\n                           OR lp.value_01 is null                                                  "); 
        if(currMinute >= 2)sbQueryCurrHour.append("\n                           OR lp.value_02 is null                                                  "); 
        if(currMinute >= 3)sbQueryCurrHour.append("\n                           OR lp.value_03 is null                                                  "); 
        if(currMinute >= 4)sbQueryCurrHour.append("\n                           OR lp.value_04 is null                                                  "); 
        if(currMinute >= 5)sbQueryCurrHour.append("\n                           OR lp.value_05 is null                                                  "); 
        if(currMinute >= 6)sbQueryCurrHour.append("\n                           OR lp.value_06 is null                                                  "); 
        if(currMinute >= 7)sbQueryCurrHour.append("\n                           OR lp.value_07 is null                                                  "); 
        if(currMinute >= 8)sbQueryCurrHour.append("\n                           OR lp.value_08 is null                                                  "); 
        if(currMinute >= 9)sbQueryCurrHour.append("\n                           OR lp.value_09 is null                                                  "); 
        if(currMinute >= 10)sbQueryCurrHour.append("\n                          OR lp.value_10 is null                                                  "); 
        if(currMinute >= 11)sbQueryCurrHour.append("\n                          OR lp.value_11 is null                                                  "); 
        if(currMinute >= 12)sbQueryCurrHour.append("\n                          OR lp.value_12 is null                                                  "); 
        if(currMinute >= 13)sbQueryCurrHour.append("\n                          OR lp.value_13 is null                                                  "); 
        if(currMinute >= 14)sbQueryCurrHour.append("\n                          OR lp.value_14 is null                                                  "); 
        if(currMinute >= 15)sbQueryCurrHour.append("\n                          OR lp.value_15 is null                                                  "); 
        if(currMinute >= 16)sbQueryCurrHour.append("\n                          OR lp.value_16 is null                                                  "); 
        if(currMinute >= 17)sbQueryCurrHour.append("\n                          OR lp.value_17 is null                                                  "); 
        if(currMinute >= 18)sbQueryCurrHour.append("\n                          OR lp.value_18 is null                                                  "); 
        if(currMinute >= 19)sbQueryCurrHour.append("\n                          OR lp.value_19 is null                                                  "); 
        if(currMinute >= 20)sbQueryCurrHour.append("\n                          OR lp.value_20 is null                                                  "); 
        if(currMinute >= 21)sbQueryCurrHour.append("\n                          OR lp.value_21 is null                                                  "); 
        if(currMinute >= 22)sbQueryCurrHour.append("\n                          OR lp.value_22 is null                                                  "); 
        if(currMinute >= 23)sbQueryCurrHour.append("\n                          OR lp.value_23 is null                                                  "); 
        if(currMinute >= 24)sbQueryCurrHour.append("\n                          OR lp.value_24 is null                                                  "); 
        if(currMinute >= 25)sbQueryCurrHour.append("\n                          OR lp.value_25 is null                                                  "); 
        if(currMinute >= 26)sbQueryCurrHour.append("\n                          OR lp.value_26 is null                                                  "); 
        if(currMinute >= 27)sbQueryCurrHour.append("\n                          OR lp.value_27 is null                                                  "); 
        if(currMinute >= 28)sbQueryCurrHour.append("\n                          OR lp.value_28 is null                                                  "); 
        if(currMinute >= 29)sbQueryCurrHour.append("\n                          OR lp.value_29 is null                                                  "); 
        if(currMinute >= 30)sbQueryCurrHour.append("\n                          OR lp.value_30 is null                                                  "); 
        if(currMinute >= 31)sbQueryCurrHour.append("\n                          OR lp.value_31 is null                                                  "); 
        if(currMinute >= 32)sbQueryCurrHour.append("\n                          OR lp.value_32 is null                                                  "); 
        if(currMinute >= 33)sbQueryCurrHour.append("\n                          OR lp.value_33 is null                                                  "); 
        if(currMinute >= 34)sbQueryCurrHour.append("\n                          OR lp.value_34 is null                                                  "); 
        if(currMinute >= 35)sbQueryCurrHour.append("\n                          OR lp.value_35 is null                                                  "); 
        if(currMinute >= 36)sbQueryCurrHour.append("\n                          OR lp.value_36 is null                                                  "); 
        if(currMinute >= 37)sbQueryCurrHour.append("\n                          OR lp.value_37 is null                                                  "); 
        if(currMinute >= 38)sbQueryCurrHour.append("\n                          OR lp.value_38 is null                                                  "); 
        if(currMinute >= 39)sbQueryCurrHour.append("\n                          OR lp.value_39 is null                                                  "); 
        if(currMinute >= 40)sbQueryCurrHour.append("\n                          OR lp.value_40 is null                                                  "); 
        if(currMinute >= 41)sbQueryCurrHour.append("\n                          OR lp.value_41 is null                                                  "); 
        if(currMinute >= 42)sbQueryCurrHour.append("\n                          OR lp.value_42 is null                                                  "); 
        if(currMinute >= 43)sbQueryCurrHour.append("\n                          OR lp.value_43 is null                                                  "); 
        if(currMinute >= 44)sbQueryCurrHour.append("\n                          OR lp.value_44 is null                                                  "); 
        if(currMinute >= 45)sbQueryCurrHour.append("\n                          OR lp.value_45 is null                                                  "); 
        if(currMinute >= 46)sbQueryCurrHour.append("\n                          OR lp.value_46 is null                                                  "); 
        if(currMinute >= 47)sbQueryCurrHour.append("\n                          OR lp.value_47 is null                                                  "); 
        if(currMinute >= 48)sbQueryCurrHour.append("\n                          OR lp.value_48 is null                                                  "); 
        if(currMinute >= 49)sbQueryCurrHour.append("\n                          OR lp.value_49 is null                                                  "); 
        if(currMinute >= 50)sbQueryCurrHour.append("\n                          OR lp.value_50 is null                                                  "); 
        if(currMinute >= 51)sbQueryCurrHour.append("\n                          OR lp.value_51 is null                                                  "); 
        if(currMinute >= 52)sbQueryCurrHour.append("\n                          OR lp.value_52 is null                                                  "); 
        if(currMinute >= 53)sbQueryCurrHour.append("\n                          OR lp.value_53 is null                                                  "); 
        if(currMinute >= 54)sbQueryCurrHour.append("\n                          OR lp.value_54 is null                                                  "); 
        if(currMinute >= 55)sbQueryCurrHour.append("\n                          OR lp.value_55 is null                                                  "); 
        if(currMinute >= 56)sbQueryCurrHour.append("\n                          OR lp.value_56 is null                                                  "); 
        if(currMinute >= 57)sbQueryCurrHour.append("\n                          OR lp.value_57 is null                                                  "); 
        if(currMinute >= 58)sbQueryCurrHour.append("\n                          OR lp.value_58 is null                                                  "); 
        if(currMinute >= 59)sbQueryCurrHour.append("\n                          OR lp.value_59 is null                                  "); 
        sbQueryCurrHour.append("\n                                              then 1 else 0 end                                ");
        sbQueryCurrHour.append("\n        when m.lp_interval = 5 then case when lp.value_00 is null                       "); 
        if(currMinute >= 5)sbQueryCurrHour.append("\n                           OR lp.value_05 is null                                                  "); 
        if(currMinute >= 10)sbQueryCurrHour.append("\n                          OR lp.value_10 is null                                                  "); 
        if(currMinute >= 15)sbQueryCurrHour.append("\n                          OR lp.value_15 is null                                                  "); 
        if(currMinute >= 20)sbQueryCurrHour.append("\n                          OR lp.value_20 is null                                                  "); 
        if(currMinute >= 25)sbQueryCurrHour.append("\n                          OR lp.value_25 is null                                                  "); 
        if(currMinute >= 30)sbQueryCurrHour.append("\n                          OR lp.value_30 is null                                                  "); 
        if(currMinute >= 35)sbQueryCurrHour.append("\n                          OR lp.value_35 is null                                                  "); 
        if(currMinute >= 40)sbQueryCurrHour.append("\n                          OR lp.value_40 is null                                                  "); 
        if(currMinute >= 45)sbQueryCurrHour.append("\n                          OR lp.value_45 is null                                                  "); 
        if(currMinute >= 50)sbQueryCurrHour.append("\n                          OR lp.value_50 is null                                                  "); 
        if(currMinute >= 55)sbQueryCurrHour.append("\n                          OR lp.value_55 is null                                 "); 
        sbQueryCurrHour.append("\n                                              then 1 else 0 end                                ");
        sbQueryCurrHour.append("\n       when m.lp_interval = 10 then case when lp.value_00 is null                       "); 
        if(currMinute >= 10)sbQueryCurrHour.append("\n                          OR lp.value_10 is null                                                  "); 
        if(currMinute >= 20)sbQueryCurrHour.append("\n                          OR lp.value_20 is null                                                  "); 
        if(currMinute >= 30)sbQueryCurrHour.append("\n                          OR lp.value_30 is null                                                  "); 
        if(currMinute >= 40)sbQueryCurrHour.append("\n                          OR lp.value_40 is null                                                  "); 
        if(currMinute >= 50)sbQueryCurrHour.append("\n                          OR lp.value_50 is null                                 "); 
        sbQueryCurrHour.append("\n                                              then 1 else 0 end                                ");
        sbQueryCurrHour.append("\n       when m.lp_interval = 15 then case when lp.value_00 is null                       "); 
        if(currMinute >= 15)sbQueryCurrHour.append("\n                          OR lp.value_15 is null                                                  "); 
        if(currMinute >= 30)sbQueryCurrHour.append("\n                          OR lp.value_30 is null                                                  "); 
        if(currMinute >= 45)sbQueryCurrHour.append("\n                          OR lp.value_45 is null                                 "); 
        sbQueryCurrHour.append("\n                                              then 1 else 0 end                                ");
        sbQueryCurrHour.append("\n       when m.lp_interval = 30 then case when lp.value_00 is null                       "); 
        if(currMinute >= 30)sbQueryCurrHour.append("\n                      OR lp.value_30 is null                                 "); 
        sbQueryCurrHour.append("\n                                              then 1 else 0 end                                ");
        sbQueryCurrHour.append("\n       when m.lp_interval = 60 then case when lp.value_00 is null then 1 else 0 end     "); 
        sbQueryCurrHour.append("\n       else 0 end                                                                       "); 
        
        // 종료일이 오늘날짜 이전일경우 일경우 
        Integer missingCnt = 0;
        if(Integer.parseInt(searchEndDate)<Integer.parseInt(currDate)){
            Query query = getSession().createSQLQuery(sbQuery.toString());
            query.setString("searchStartDate", searchStartDate);
            query.setString("searchEndDate", searchEndDate);
            query.setString("installDate", searchStartDate + CommonConstants.DefaultDate.LAST_HHMMSS.getValue());
            query.setInteger("channel", channel);
            if(!"".equals(supplierId)){
                query.setInteger("supplierId", Integer.parseInt(supplierId));
            }
            
            missingCnt = (Integer)query.uniqueResult();
        }else{
            // 시작을 ~ 종료일전일까지의 누락건수조회  having 절의 카운트가 24가 아닌경우조회
            
            //logger.debug(sbQuery.toString());
            Query query = getSession().createSQLQuery(sbQuery.toString());
            query.setString("searchStartDate", searchStartDate);
            query.setString("searchEndDate", CalendarUtil.getDateWithoutFormat(searchEndDate,Calendar.DATE, -1));
            query.setString("installDate", searchStartDate + CommonConstants.DefaultDate.LAST_HHMMSS.getValue());
            query.setInteger("channel", channel);
            if(!"".equals(supplierId)){
                query.setInteger("supplierId", Integer.parseInt(supplierId));
            }
            
            Number patialMisstingCount = (Number)query.uniqueResult();
            
            // 오늘날짜기준으로 누락건수조회 having 절의 카운트가 현재시간전까지의 시간수 만큼 조회
            Query queryToday = getSession().createSQLQuery(sbQueryToday.toString());
            queryToday.setString("searchEndDate", searchEndDate);
            queryToday.setString("installDate", searchStartDate + CommonConstants.DefaultDate.LAST_HHMMSS.getValue());
            queryToday.setString("currHour", currHour);
            queryToday.setInteger("channel", channel);
            queryToday.setInteger("hourCnt", Integer.parseInt(currHour));
            if(!"".equals(supplierId)){
                queryToday.setInteger("supplierId", Integer.parseInt(supplierId));
            }
            Number patialMisstingCountToday = (Number)queryToday.uniqueResult();
            
            // 현재시간의 누락건수조회
            Query queryCurrHour = getSession().createSQLQuery(sbQueryCurrHour.toString());
            queryCurrHour.setString("searchEndDate", searchEndDate);
            queryCurrHour.setString("installDate", searchStartDate + CommonConstants.DefaultDate.LAST_HHMMSS.getValue());
            queryCurrHour.setString("currHour", currHour);
            queryCurrHour.setInteger("channel", channel);
            if(!"".equals(supplierId)){
                queryCurrHour.setInteger("supplierId", Integer.parseInt(supplierId));
            }
            Number patialMisstingCountCurrHour = (Number)queryCurrHour.uniqueResult();
        
            missingCnt = patialMisstingCount.intValue() + patialMisstingCountToday.intValue() + patialMisstingCountCurrHour.intValue();
        }
       
        return missingCnt;
    }

    /**
     * 일별,시간별로 LP(전기,가스,수도,열량) 에서 누락된 미터갯수를 조회한다.
     * 조회일자가 현재일자일경우
     * 1.00시~ 현재시간 전시간
     * 2.현재시간
     * 두가지 조건으로 각각의 쿼리문을 수행한다.
     * @param params
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<Object> getMissingMetersByHour(Map<String,Object> params){
        
        String meterType        = StringUtil.nullToBlank(params.get("meterType"));
        String supplierId       = StringUtil.nullToBlank(params.get("supplierId"));
        String searchStartDate  = (String)params.get("searchStartDate");
        Integer channel         = (Integer)params.get("channel");
        Integer totalMeterCnt   = (Integer)params.get("totalMeterCnt");
        
        String today            = TimeUtil.getCurrentTimeMilli(); //yyyymmddhhmmss
        String currDate         = today.substring(0, 8);
        String currHour         = today.substring(8, 10);
        Integer currMinute      = Integer.parseInt(today.substring(10, 12));
        
        String lpTable = CommonConstants.MeterType.valueOf(meterType).getLpTableName();
        
        List<Object> resultList  = new ArrayList<Object>();
        
        // query 작성
        StringBuffer sbQuery      = new StringBuffer();
        sbQuery.append("\n select hh,count(hh) as successCnt                                     ");
        sbQuery.append("\n from meter m join ").append(lpTable).append(" lp on lp.meter_id = m.id                              ");
        sbQuery.append("\n where lp.yyyymmdd = :searchStartDate                                                ");
        sbQuery.append("\n and lp.hh < :currHour                                                ");
        sbQuery.append("\n and lp.channel = :channel                                                            ");
        sbQuery.append("\n AND m.install_Date <= :installDate     ");
        if(!"".equals(supplierId)){
            sbQuery.append("\n AND m.supplier_id = :supplierId ");
        }
        sbQuery.append("\n and 1 = case when m.lp_interval = 1 then case when lp.value_00 is null        ");
        sbQuery.append("\n                  OR lp.value_01 is null                                               ");
        sbQuery.append("\n                  OR lp.value_02 is null                                               ");
        sbQuery.append("\n                  OR lp.value_03 is null                                               ");
        sbQuery.append("\n                  OR lp.value_04 is null                                               ");
        sbQuery.append("\n                  OR lp.value_05 is null                                               ");
        sbQuery.append("\n                  OR lp.value_06 is null                                               ");
        sbQuery.append("\n                  OR lp.value_07 is null                                               ");
        sbQuery.append("\n                  OR lp.value_08 is null                                               ");
        sbQuery.append("\n                  OR lp.value_09 is null                                               ");
        sbQuery.append("\n                  OR lp.value_10 is null                                               ");
        sbQuery.append("\n                  OR lp.value_11 is null                                               ");
        sbQuery.append("\n                  OR lp.value_12 is null                                               ");
        sbQuery.append("\n                  OR lp.value_13 is null                                               ");
        sbQuery.append("\n                  OR lp.value_14 is null                                               ");
        sbQuery.append("\n                  OR lp.value_15 is null                                               ");
        sbQuery.append("\n                  OR lp.value_16 is null                                               ");
        sbQuery.append("\n                  OR lp.value_17 is null                                               ");
        sbQuery.append("\n                  OR lp.value_18 is null                                               ");
        sbQuery.append("\n                  OR lp.value_19 is null                                               ");
        sbQuery.append("\n                  OR lp.value_20 is null                                               ");
        sbQuery.append("\n                  OR lp.value_21 is null                                               ");
        sbQuery.append("\n                  OR lp.value_22 is null                                               ");
        sbQuery.append("\n                  OR lp.value_23 is null                                               ");
        sbQuery.append("\n                  OR lp.value_24 is null                                               ");
        sbQuery.append("\n                  OR lp.value_25 is null                                               ");
        sbQuery.append("\n                  OR lp.value_26 is null                                               ");
        sbQuery.append("\n                  OR lp.value_27 is null                                               ");
        sbQuery.append("\n                  OR lp.value_28 is null                                               ");
        sbQuery.append("\n                  OR lp.value_29 is null                                               ");
        sbQuery.append("\n                  OR lp.value_30 is null                                               ");
        sbQuery.append("\n                  OR lp.value_31 is null                                               ");
        sbQuery.append("\n                  OR lp.value_32 is null                                               ");
        sbQuery.append("\n                  OR lp.value_33 is null                                               ");
        sbQuery.append("\n                  OR lp.value_34 is null                                               ");
        sbQuery.append("\n                  OR lp.value_35 is null                                               ");
        sbQuery.append("\n                  OR lp.value_36 is null                                               ");
        sbQuery.append("\n                  OR lp.value_37 is null                                               ");
        sbQuery.append("\n                  OR lp.value_38 is null                                               ");
        sbQuery.append("\n                  OR lp.value_39 is null                                               ");
        sbQuery.append("\n                  OR lp.value_40 is null                                               ");
        sbQuery.append("\n                  OR lp.value_41 is null                                               ");
        sbQuery.append("\n                  OR lp.value_42 is null                                               ");
        sbQuery.append("\n                  OR lp.value_43 is null                                               ");
        sbQuery.append("\n                  OR lp.value_44 is null                                               ");
        sbQuery.append("\n                  OR lp.value_45 is null                                               ");
        sbQuery.append("\n                  OR lp.value_46 is null                                               ");
        sbQuery.append("\n                  OR lp.value_47 is null                                               ");
        sbQuery.append("\n                  OR lp.value_48 is null                                               ");
        sbQuery.append("\n                  OR lp.value_49 is null                                               ");
        sbQuery.append("\n                  OR lp.value_50 is null                                               ");
        sbQuery.append("\n                  OR lp.value_51 is null                                               ");
        sbQuery.append("\n                  OR lp.value_52 is null                                               ");
        sbQuery.append("\n                  OR lp.value_53 is null                                               ");
        sbQuery.append("\n                  OR lp.value_54 is null                                               ");
        sbQuery.append("\n                  OR lp.value_55 is null                                               ");
        sbQuery.append("\n                  OR lp.value_56 is null                                               ");
        sbQuery.append("\n                  OR lp.value_57 is null                                               ");
        sbQuery.append("\n                  OR lp.value_58 is null                                               ");
        sbQuery.append("\n                  OR lp.value_59 is null then 0 else 1 end                             ");
        sbQuery.append("\n  when m.lp_interval = 5 then case when lp.value_00 is null                    ");
        sbQuery.append("\n                  OR lp.value_05 is null                                               ");
        sbQuery.append("\n                  OR lp.value_10 is null                                               ");
        sbQuery.append("\n                  OR lp.value_15 is null                                               ");
        sbQuery.append("\n                  OR lp.value_20 is null                                               ");
        sbQuery.append("\n                  OR lp.value_25 is null                                               ");
        sbQuery.append("\n                  OR lp.value_30 is null                                               ");
        sbQuery.append("\n                  OR lp.value_35 is null                                               ");
        sbQuery.append("\n                  OR lp.value_40 is null                                               ");
        sbQuery.append("\n                  OR lp.value_45 is null                                               ");
        sbQuery.append("\n                  OR lp.value_50 is null                                               ");
        sbQuery.append("\n                  OR lp.value_55 is null then 0 else 1 end                             ");
        sbQuery.append("\n when m.lp_interval = 10 then case when lp.value_00 is null                    ");
        sbQuery.append("\n                  OR lp.value_10 is null                                               ");
        sbQuery.append("\n                  OR lp.value_20 is null                                               ");
        sbQuery.append("\n                  OR lp.value_30 is null                                               ");
        sbQuery.append("\n                  OR lp.value_40 is null                                               ");
        sbQuery.append("\n                  OR lp.value_50 is null then 0 else 1 end                             ");
        sbQuery.append("\n when m.lp_interval = 15 then case when lp.value_00 is null                    ");
        sbQuery.append("\n                  OR lp.value_15 is null                                               ");
        sbQuery.append("\n                  OR lp.value_30 is null                                               ");
        sbQuery.append("\n                  OR lp.value_45 is null then 0 else 1 end                             ");
        sbQuery.append("\n when m.lp_interval = 30 then case when lp.value_00 is null                    ");
        sbQuery.append("\n              OR lp.value_30 is null then 1 else 0 end                             ");
        sbQuery.append("\n when m.lp_interval = 60 then case when lp.value_00 is null then 0 else 1 end  ");
        sbQuery.append("\n else 0 end                                                                    ");
        sbQuery.append("\n group by yyyymmdd,hh                                                          ");
 

        StringBuffer sbQueryCurrHour      = new StringBuffer();
        sbQueryCurrHour.append("\n select hh,count(hh) as successCnt                                     ");
        sbQueryCurrHour.append("\n from meter m join ").append(lpTable).append(" lp on lp.meter_id = m.id                              ");
        sbQueryCurrHour.append("\n where lp.yyyymmdd = :searchStartDate                                                ");
        sbQueryCurrHour.append("\n and lp.hh = :currHour                                                ");
        sbQueryCurrHour.append("\n and lp.channel = :channel                                                            ");
        sbQueryCurrHour.append("\n AND m.install_Date <= :installDate     ");
        if(!"".equals(supplierId)){
            sbQueryCurrHour.append("\n AND m.supplier_id = :supplierId ");
        }
        sbQueryCurrHour.append("\n and 1 = case when m.lp_interval = 1 then case when lp.value_00 is null        ");
        if(currMinute >= 1)sbQueryCurrHour.append("\n                   OR lp.value_01 is null                                               ");
        if(currMinute >= 2)sbQueryCurrHour.append("\n                   OR lp.value_02 is null                                               ");
        if(currMinute >= 3)sbQueryCurrHour.append("\n                   OR lp.value_03 is null                                               ");
        if(currMinute >= 4)sbQueryCurrHour.append("\n                   OR lp.value_04 is null                                               ");
        if(currMinute >= 5)sbQueryCurrHour.append("\n                   OR lp.value_05 is null                                               ");
        if(currMinute >= 6)sbQueryCurrHour.append("\n                   OR lp.value_06 is null                                               ");
        if(currMinute >= 7)sbQueryCurrHour.append("\n                   OR lp.value_07 is null                                               ");
        if(currMinute >= 8)sbQueryCurrHour.append("\n                   OR lp.value_08 is null                                               ");
        if(currMinute >= 9)sbQueryCurrHour.append("\n                   OR lp.value_09 is null                                               ");
        if(currMinute >= 10)sbQueryCurrHour.append("\n                  OR lp.value_10 is null                                               ");
        if(currMinute >= 11)sbQueryCurrHour.append("\n                  OR lp.value_11 is null                                               ");
        if(currMinute >= 12)sbQueryCurrHour.append("\n                  OR lp.value_12 is null                                               ");
        if(currMinute >= 13)sbQueryCurrHour.append("\n                  OR lp.value_13 is null                                               ");
        if(currMinute >= 14)sbQueryCurrHour.append("\n                  OR lp.value_14 is null                                               ");
        if(currMinute >= 15)sbQueryCurrHour.append("\n                  OR lp.value_15 is null                                               ");
        if(currMinute >= 16)sbQueryCurrHour.append("\n                  OR lp.value_16 is null                                               ");
        if(currMinute >= 17)sbQueryCurrHour.append("\n                  OR lp.value_17 is null                                               ");
        if(currMinute >= 18)sbQueryCurrHour.append("\n                  OR lp.value_18 is null                                               ");
        if(currMinute >= 19)sbQueryCurrHour.append("\n                  OR lp.value_19 is null                                               ");
        if(currMinute >= 20)sbQueryCurrHour.append("\n                  OR lp.value_20 is null                                               ");
        if(currMinute >= 21)sbQueryCurrHour.append("\n                  OR lp.value_21 is null                                               ");
        if(currMinute >= 22)sbQueryCurrHour.append("\n                  OR lp.value_22 is null                                               ");
        if(currMinute >= 23)sbQueryCurrHour.append("\n                  OR lp.value_23 is null                                               ");
        if(currMinute >= 24)sbQueryCurrHour.append("\n                  OR lp.value_24 is null                                               ");
        if(currMinute >= 25)sbQueryCurrHour.append("\n                  OR lp.value_25 is null                                               ");
        if(currMinute >= 26)sbQueryCurrHour.append("\n                  OR lp.value_26 is null                                               ");
        if(currMinute >= 27)sbQueryCurrHour.append("\n                  OR lp.value_27 is null                                               ");
        if(currMinute >= 28)sbQueryCurrHour.append("\n                  OR lp.value_28 is null                                               ");
        if(currMinute >= 29)sbQueryCurrHour.append("\n                  OR lp.value_29 is null                                               ");
        if(currMinute >= 30)sbQueryCurrHour.append("\n                  OR lp.value_30 is null                                               ");
        if(currMinute >= 31)sbQueryCurrHour.append("\n                  OR lp.value_31 is null                                               ");
        if(currMinute >= 32)sbQueryCurrHour.append("\n                  OR lp.value_32 is null                                               ");
        if(currMinute >= 33)sbQueryCurrHour.append("\n                  OR lp.value_33 is null                                               ");
        if(currMinute >= 34)sbQueryCurrHour.append("\n                  OR lp.value_34 is null                                               ");
        if(currMinute >= 35)sbQueryCurrHour.append("\n                  OR lp.value_35 is null                                               ");
        if(currMinute >= 36)sbQueryCurrHour.append("\n                  OR lp.value_36 is null                                               ");
        if(currMinute >= 37)sbQueryCurrHour.append("\n                  OR lp.value_37 is null                                               ");
        if(currMinute >= 38)sbQueryCurrHour.append("\n                  OR lp.value_38 is null                                               ");
        if(currMinute >= 39)sbQueryCurrHour.append("\n                  OR lp.value_39 is null                                               ");
        if(currMinute >= 40)sbQueryCurrHour.append("\n                  OR lp.value_40 is null                                               ");
        if(currMinute >= 41)sbQueryCurrHour.append("\n                  OR lp.value_41 is null                                               ");
        if(currMinute >= 42)sbQueryCurrHour.append("\n                  OR lp.value_42 is null                                               ");
        if(currMinute >= 43)sbQueryCurrHour.append("\n                  OR lp.value_43 is null                                               ");
        if(currMinute >= 44)sbQueryCurrHour.append("\n                  OR lp.value_44 is null                                               ");
        if(currMinute >= 45)sbQueryCurrHour.append("\n                  OR lp.value_45 is null                                               ");
        if(currMinute >= 46)sbQueryCurrHour.append("\n                  OR lp.value_46 is null                                               ");
        if(currMinute >= 47)sbQueryCurrHour.append("\n                  OR lp.value_47 is null                                               ");
        if(currMinute >= 48)sbQueryCurrHour.append("\n                  OR lp.value_48 is null                                               ");
        if(currMinute >= 49)sbQueryCurrHour.append("\n                  OR lp.value_49 is null                                               ");
        if(currMinute >= 50)sbQueryCurrHour.append("\n                  OR lp.value_50 is null                                               ");
        if(currMinute >= 51)sbQueryCurrHour.append("\n                  OR lp.value_51 is null                                               ");
        if(currMinute >= 52)sbQueryCurrHour.append("\n                  OR lp.value_52 is null                                               ");
        if(currMinute >= 53)sbQueryCurrHour.append("\n                  OR lp.value_53 is null                                               ");
        if(currMinute >= 54)sbQueryCurrHour.append("\n                  OR lp.value_54 is null                                               ");
        if(currMinute >= 55)sbQueryCurrHour.append("\n                  OR lp.value_55 is null                                               ");
        if(currMinute >= 56)sbQueryCurrHour.append("\n                  OR lp.value_56 is null                                               ");
        if(currMinute >= 57)sbQueryCurrHour.append("\n                  OR lp.value_57 is null                                               ");
        if(currMinute >= 58)sbQueryCurrHour.append("\n                  OR lp.value_58 is null                                               ");
        if(currMinute >= 59)sbQueryCurrHour.append("\n                  OR lp.value_59 is null                              ");
        sbQueryCurrHour.append("\n                                      then 0 else 1 end                                 ");
        sbQueryCurrHour.append("\n  when m.lp_interval = 5 then case when lp.value_00 is null                    ");
        if(currMinute >= 5)sbQueryCurrHour.append("\n                   OR lp.value_05 is null                                               ");
        if(currMinute >= 10)sbQueryCurrHour.append("\n                  OR lp.value_10 is null                                               ");
        if(currMinute >= 15)sbQueryCurrHour.append("\n                  OR lp.value_15 is null                                               ");
        if(currMinute >= 20)sbQueryCurrHour.append("\n                  OR lp.value_20 is null                                               ");
        if(currMinute >= 25)sbQueryCurrHour.append("\n                  OR lp.value_25 is null                                               ");
        if(currMinute >= 30)sbQueryCurrHour.append("\n                  OR lp.value_30 is null                                               ");
        if(currMinute >= 35)sbQueryCurrHour.append("\n                  OR lp.value_35 is null                                               ");
        if(currMinute >= 40)sbQueryCurrHour.append("\n                  OR lp.value_40 is null                                               ");
        if(currMinute >= 45)sbQueryCurrHour.append("\n                  OR lp.value_45 is null                                               ");
        if(currMinute >= 50)sbQueryCurrHour.append("\n                  OR lp.value_50 is null                                               ");
        if(currMinute >= 55)sbQueryCurrHour.append("\n                  OR lp.value_55 is null                              ");
        sbQueryCurrHour.append("\n                                      then 0 else 1 end                                 ");
        sbQueryCurrHour.append("\n when m.lp_interval = 10 then case when lp.value_00 is null                    ");
        if(currMinute >= 10)sbQueryCurrHour.append("\n                  OR lp.value_10 is null                                               ");
        if(currMinute >= 20)sbQueryCurrHour.append("\n                  OR lp.value_20 is null                                               ");
        if(currMinute >= 30)sbQueryCurrHour.append("\n                  OR lp.value_30 is null                                               ");
        if(currMinute >= 40)sbQueryCurrHour.append("\n                  OR lp.value_40 is null                                               ");
        if(currMinute >= 50)sbQueryCurrHour.append("\n                  OR lp.value_50 is null                              ");
        sbQueryCurrHour.append("\n                                      then 0 else 1 end                                 ");
        sbQueryCurrHour.append("\n when m.lp_interval = 15 then case when lp.value_00 is null                    ");
        if(currMinute >= 15)sbQueryCurrHour.append("\n                  OR lp.value_15 is null                                               ");
        if(currMinute >= 30)sbQueryCurrHour.append("\n                  OR lp.value_30 is null                                               ");
        if(currMinute >= 45)sbQueryCurrHour.append("\n                  OR lp.value_45 is null                              ");
        sbQueryCurrHour.append("\n                                      then 0 else 1 end                                 ");
        sbQueryCurrHour.append("\n when m.lp_interval = 30 then case when lp.value_00 is null                    ");
        if(currMinute >= 30)sbQueryCurrHour.append("\n              OR lp.value_30 is null                              ");
        sbQueryCurrHour.append("\n                                      then 0 else 1 end                                 ");
        sbQueryCurrHour.append("\n when m.lp_interval = 60 then case when lp.value_00 is null then 0 else 1 end  ");
        sbQueryCurrHour.append("\n else 0 end                                                                    ");
        sbQueryCurrHour.append("\n group by yyyymmdd,hh                                                          ");

        
        HashMap successData = new HashMap();
        
        // 조회일자가 과거일경우 0~23시의 모든 누락건수 조회
        if(Integer.parseInt(searchStartDate)<Integer.parseInt(currDate)){
            // 파라메터 설정
            SQLQuery query = getSession().createSQLQuery(sbQuery.toString());
            
            query.setString("searchStartDate",  searchStartDate);
            query.setString("installDate", searchStartDate + CommonConstants.DefaultDate.LAST_HHMMSS.getValue());
            query.setInteger("channel",     channel);
            query.setString("currHour",     "24");
            if(!"".equals(supplierId)){
                query.setInteger("supplierId", Integer.parseInt(supplierId));
            }
            // query 결과목록
            List<Object> successDataList = query.list();
            
            for(Object obj : successDataList){
                Object[] objs = (Object[])obj;
                successData.put((String)objs[0] , ((Number)objs[1]).intValue());                 
            }
        }else{
            // 조회일자가 오늘일경우 0~ 현재시간 1시간전까지의 누락건을 조회하고
            // 현재시간의 누락건을 별도의 쿼리로 조회한다.
            
            // 0~ 현재시간 1시간전데이터 조회 
            SQLQuery query = getSession().createSQLQuery(sbQuery.toString());
            query.setString("searchStartDate",  searchStartDate);
            query.setString("installDate", searchStartDate + CommonConstants.DefaultDate.LAST_HHMMSS.getValue());
            query.setInteger("channel",     channel);
            query.setString("currHour",     currHour);
            if(!"".equals(supplierId)){
                query.setInteger("supplierId", Integer.parseInt(supplierId));
            }
            // query 결과목록
            List<Object> successDataList = query.list();
            for(Object obj : successDataList){
                Object[] objs = (Object[])obj;
                successData.put((String)objs[0] , ((Number)objs[1]).intValue());                 
            }
            
            // 현재시간 데이터 조회
            SQLQuery queryCurrHour = getSession().createSQLQuery(sbQueryCurrHour.toString());
            queryCurrHour.setString("searchStartDate",  searchStartDate);
            queryCurrHour.setString("installDate", searchStartDate + CommonConstants.DefaultDate.LAST_HHMMSS.getValue());
            queryCurrHour.setInteger("channel",     channel);
            queryCurrHour.setString("currHour",     currHour);
            if(!"".equals(supplierId)){
                queryCurrHour.setInteger("supplierId", Integer.parseInt(supplierId));
            }
            // query 결과목록
            List<Object> successDataListCurrHour = queryCurrHour.list();
            for(Object obj : successDataListCurrHour){
                Object[] objs = (Object[])obj;
                successData.put((String)objs[0] , ((Number)objs[1]).intValue());                 
            }
        }
        
        
        // 특정시간대에 모두 누락될수도 있으므로 0 ~23 시까지의 데이터는 고정
        for(int i=0;i<24;i++){
            HashMap resultMap = new HashMap();
            resultMap.put("no", i+1);
            resultMap.put("yyyymmdd", searchStartDate);
            
            String hh = TimeUtil.to2Digit(i);
            resultMap.put("hh", hh);
            
            if(searchStartDate.equals(currDate)&&i>Integer.parseInt(currHour)){//오늘날짜이고 현재시간 이후일경우 0으로 세팅한다. 
                resultMap.put("missingCount", 0);
            }else{
                Integer successCount =  successData.get(hh)==null?0:((Number)successData.get(hh)).intValue();
                resultMap.put("missingCount", totalMeterCnt - successCount);
            }
            resultMap.put("xField", hh);
            
            resultList.add(resultMap);
        }
    
        return resultList;
        
    }
    
    /**
     * 일별로 LP(전기,가스,수도,열량) 에서 누락된 미터갯수를 조회한다.
     * 
     * 조회기간에 현재일자가 포함되어있을경우
     * 1.조회시작일~조회종료일전일
     * 2.조회종료일,00시~ 현재시간 전시간
     * 3.조회종료일,현재시간
     * 세가지 조건으로 각각의 쿼리문을 수행한다.
     * @param params
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<Object> getMissingMetersByDay(Map<String,Object> params){
        
        String searchStartDate  = (String)params.get("searchStartDate");
        String searchEndDate    = (String)params.get("searchEndDate");
        String meterType        = StringUtil.nullToBlank(params.get("meterType"));
        String supplierId       = StringUtil.nullToBlank(params.get("supplierId"));
        Integer channel         = (Integer)params.get("channel");
        Integer totalMeterCnt   = (Integer)params.get("totalMeterCnt");
        
        String today            = TimeUtil.getCurrentTimeMilli(); //yyyymmddhhmmss
        String currDate         = today.substring(0, 8);
        String currHour         = today.substring(8, 10);
        Integer currMinute      = Integer.parseInt(today.substring(10, 12));
        
        String lpTable = CommonConstants.MeterType.valueOf(meterType).getLpTableName();
        
        int period=0;
        try {
            period = TimeUtil.getDayDuration(searchStartDate, searchEndDate) + 1;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        
        List<Object> resultList  = new ArrayList<Object>();
        
        // query 작성
        StringBuffer sbQuery      = new StringBuffer();
        sbQuery.append("\n select x.yyyymmdd,count(x.yyyymmdd) as successCnt                                          ");
        sbQuery.append("\n from (                                                                            ");
        sbQuery.append("\n select yyyymmdd,m.id                                       ");
        sbQuery.append("\n from meter m join ").append(lpTable).append(" lp on lp.meter_id = m.id                                  ");
        sbQuery.append("\n where lp.yyyymmdd between :searchStartDate and :searchEndDate                              ");
        sbQuery.append("\n and lp.channel = :channel                                                               ");
        sbQuery.append("\n AND m.install_Date <= :installDate     ");
        if(!"".equals(supplierId)){
            sbQuery.append("\n AND m.supplier_id = :supplierId ");
        }
        sbQuery.append("\n and 1 = case when m.lp_interval = 1 then case when lp.value_00 is null            ");
        sbQuery.append("\n                  OR lp.value_01 is null                                                   ");
        sbQuery.append("\n                  OR lp.value_02 is null                                                   ");
        sbQuery.append("\n                  OR lp.value_03 is null                                                   ");
        sbQuery.append("\n                  OR lp.value_04 is null                                                   ");
        sbQuery.append("\n                  OR lp.value_05 is null                                                   ");
        sbQuery.append("\n                  OR lp.value_06 is null                                                   ");
        sbQuery.append("\n                  OR lp.value_07 is null                                                   ");
        sbQuery.append("\n                  OR lp.value_08 is null                                                   ");
        sbQuery.append("\n                  OR lp.value_09 is null                                                   ");
        sbQuery.append("\n                  OR lp.value_10 is null                                                   ");
        sbQuery.append("\n                  OR lp.value_11 is null                                                   ");
        sbQuery.append("\n                  OR lp.value_12 is null                                                   ");
        sbQuery.append("\n                  OR lp.value_13 is null                                                   ");
        sbQuery.append("\n                  OR lp.value_14 is null                                                   ");
        sbQuery.append("\n                  OR lp.value_15 is null                                                   ");
        sbQuery.append("\n                  OR lp.value_16 is null                                                   ");
        sbQuery.append("\n                  OR lp.value_17 is null                                                   ");
        sbQuery.append("\n                  OR lp.value_18 is null                                                   ");
        sbQuery.append("\n                  OR lp.value_19 is null                                                   ");
        sbQuery.append("\n                  OR lp.value_20 is null                                                   ");
        sbQuery.append("\n                  OR lp.value_21 is null                                                   ");
        sbQuery.append("\n                  OR lp.value_22 is null                                                   ");
        sbQuery.append("\n                  OR lp.value_23 is null                                                   ");
        sbQuery.append("\n                  OR lp.value_24 is null                                                   ");
        sbQuery.append("\n                  OR lp.value_25 is null                                                   ");
        sbQuery.append("\n                  OR lp.value_26 is null                                                   ");
        sbQuery.append("\n                  OR lp.value_27 is null                                                   ");
        sbQuery.append("\n                  OR lp.value_28 is null                                                   ");
        sbQuery.append("\n                  OR lp.value_29 is null                                                   ");
        sbQuery.append("\n                  OR lp.value_30 is null                                                   ");
        sbQuery.append("\n                  OR lp.value_31 is null                                                   ");
        sbQuery.append("\n                  OR lp.value_32 is null                                                   ");
        sbQuery.append("\n                  OR lp.value_33 is null                                                   ");
        sbQuery.append("\n                  OR lp.value_34 is null                                                   ");
        sbQuery.append("\n                  OR lp.value_35 is null                                                   ");
        sbQuery.append("\n                  OR lp.value_36 is null                                                   ");
        sbQuery.append("\n                  OR lp.value_37 is null                                                   ");
        sbQuery.append("\n                  OR lp.value_38 is null                                                   ");
        sbQuery.append("\n                  OR lp.value_39 is null                                                   ");
        sbQuery.append("\n                  OR lp.value_40 is null                                                   ");
        sbQuery.append("\n                  OR lp.value_41 is null                                                   ");
        sbQuery.append("\n                  OR lp.value_42 is null                                                   ");
        sbQuery.append("\n                  OR lp.value_43 is null                                                   ");
        sbQuery.append("\n                  OR lp.value_44 is null                                                   ");
        sbQuery.append("\n                  OR lp.value_45 is null                                                   ");
        sbQuery.append("\n                  OR lp.value_46 is null                                                   ");
        sbQuery.append("\n                  OR lp.value_47 is null                                                   ");
        sbQuery.append("\n                  OR lp.value_48 is null                                                   ");
        sbQuery.append("\n                  OR lp.value_49 is null                                                   ");
        sbQuery.append("\n                  OR lp.value_50 is null                                                   ");
        sbQuery.append("\n                  OR lp.value_51 is null                                                   ");
        sbQuery.append("\n                  OR lp.value_52 is null                                                   ");
        sbQuery.append("\n                  OR lp.value_53 is null                                                   ");
        sbQuery.append("\n                  OR lp.value_54 is null                                                   ");
        sbQuery.append("\n                  OR lp.value_55 is null                                                   ");
        sbQuery.append("\n                  OR lp.value_56 is null                                                   ");
        sbQuery.append("\n                  OR lp.value_57 is null                                                   ");
        sbQuery.append("\n                  OR lp.value_58 is null                                                   ");
        sbQuery.append("\n                  OR lp.value_59 is null then 0 else 1 end                                 ");
        sbQuery.append("\n  when m.lp_interval = 5 then case when lp.value_00 is null                        ");
        sbQuery.append("\n                  OR lp.value_05 is null                                                   ");
        sbQuery.append("\n                  OR lp.value_10 is null                                                   ");
        sbQuery.append("\n                  OR lp.value_15 is null                                                   ");
        sbQuery.append("\n                  OR lp.value_20 is null                                                   ");
        sbQuery.append("\n                  OR lp.value_25 is null                                                   ");
        sbQuery.append("\n                  OR lp.value_30 is null                                                   ");
        sbQuery.append("\n                  OR lp.value_35 is null                                                   ");
        sbQuery.append("\n                  OR lp.value_40 is null                                                   ");
        sbQuery.append("\n                  OR lp.value_45 is null                                                   ");
        sbQuery.append("\n                  OR lp.value_50 is null                                                   ");
        sbQuery.append("\n                  OR lp.value_55 is null then 0 else 1 end                                 ");
        sbQuery.append("\n when m.lp_interval = 10 then case when lp.value_00 is null                        ");
        sbQuery.append("\n                  OR lp.value_10 is null                                                   ");
        sbQuery.append("\n                  OR lp.value_20 is null                                                   ");
        sbQuery.append("\n                  OR lp.value_30 is null                                                   ");
        sbQuery.append("\n                  OR lp.value_40 is null                                                   ");
        sbQuery.append("\n                  OR lp.value_50 is null then 0 else 1 end                                 ");
        sbQuery.append("\n when m.lp_interval = 15 then case when lp.value_00 is null                        ");
        sbQuery.append("\n                  OR lp.value_15 is null                                                   ");
        sbQuery.append("\n                  OR lp.value_30 is null                                                   ");
        sbQuery.append("\n                  OR lp.value_45 is null then 0 else 1 end                                 ");
        sbQuery.append("\n when m.lp_interval = 30 then case when lp.value_00 is null                        ");
        sbQuery.append("\n              OR lp.value_30 is null then 1 else 0 end                                 ");
        sbQuery.append("\n when m.lp_interval = 60 then case when lp.value_00 is null then 0 else 1 end      ");
        sbQuery.append("\n else 0 end                                                                        ");
        sbQuery.append("\n group by yyyymmdd,m.id                                                            ");
        sbQuery.append("\n having count(*) = 24                                                              ");
        sbQuery.append("\n ) x                                                                               ");
        sbQuery.append("\n group by x.yyyymmdd                                                               ");

        
        // query 작성
        StringBuffer sbQueryToday     = new StringBuffer();
        sbQueryToday.append("\n select yyyymmdd,m.id                                       ");
        sbQueryToday.append("\n from meter m join ").append(lpTable).append(" lp on lp.meter_id = m.id                                  ");
        sbQueryToday.append("\n where lp.yyyymmdd = :searchEndDate                              ");
        sbQueryToday.append("\n and lp.hh < :currHour                              ");
        sbQueryToday.append("\n and lp.channel = :channel                                                               ");
        sbQueryToday.append("\n AND m.install_Date <= :installDate     ");
        if(!"".equals(supplierId)){
            sbQueryToday.append("\n AND m.supplier_id = :supplierId ");
        }
        sbQueryToday.append("\n and 1 = case when m.lp_interval = 1 then case when lp.value_00 is null            ");
        sbQueryToday.append("\n                     OR lp.value_01 is null                                                   ");
        sbQueryToday.append("\n                     OR lp.value_02 is null                                                   ");
        sbQueryToday.append("\n                     OR lp.value_03 is null                                                   ");
        sbQueryToday.append("\n                     OR lp.value_04 is null                                                   ");
        sbQueryToday.append("\n                     OR lp.value_05 is null                                                   ");
        sbQueryToday.append("\n                     OR lp.value_06 is null                                                   ");
        sbQueryToday.append("\n                     OR lp.value_07 is null                                                   ");
        sbQueryToday.append("\n                     OR lp.value_08 is null                                                   ");
        sbQueryToday.append("\n                     OR lp.value_09 is null                                                   ");
        sbQueryToday.append("\n                     OR lp.value_10 is null                                                   ");
        sbQueryToday.append("\n                     OR lp.value_11 is null                                                   ");
        sbQueryToday.append("\n                     OR lp.value_12 is null                                                   ");
        sbQueryToday.append("\n                     OR lp.value_13 is null                                                   ");
        sbQueryToday.append("\n                     OR lp.value_14 is null                                                   ");
        sbQueryToday.append("\n                     OR lp.value_15 is null                                                   ");
        sbQueryToday.append("\n                     OR lp.value_16 is null                                                   ");
        sbQueryToday.append("\n                     OR lp.value_17 is null                                                   ");
        sbQueryToday.append("\n                     OR lp.value_18 is null                                                   ");
        sbQueryToday.append("\n                     OR lp.value_19 is null                                                   ");
        sbQueryToday.append("\n                     OR lp.value_20 is null                                                   ");
        sbQueryToday.append("\n                     OR lp.value_21 is null                                                   ");
        sbQueryToday.append("\n                     OR lp.value_22 is null                                                   ");
        sbQueryToday.append("\n                     OR lp.value_23 is null                                                   ");
        sbQueryToday.append("\n                     OR lp.value_24 is null                                                   ");
        sbQueryToday.append("\n                     OR lp.value_25 is null                                                   ");
        sbQueryToday.append("\n                     OR lp.value_26 is null                                                   ");
        sbQueryToday.append("\n                     OR lp.value_27 is null                                                   ");
        sbQueryToday.append("\n                     OR lp.value_28 is null                                                   ");
        sbQueryToday.append("\n                     OR lp.value_29 is null                                                   ");
        sbQueryToday.append("\n                     OR lp.value_30 is null                                                   ");
        sbQueryToday.append("\n                     OR lp.value_31 is null                                                   ");
        sbQueryToday.append("\n                     OR lp.value_32 is null                                                   ");
        sbQueryToday.append("\n                     OR lp.value_33 is null                                                   ");
        sbQueryToday.append("\n                     OR lp.value_34 is null                                                   ");
        sbQueryToday.append("\n                     OR lp.value_35 is null                                                   ");
        sbQueryToday.append("\n                     OR lp.value_36 is null                                                   ");
        sbQueryToday.append("\n                     OR lp.value_37 is null                                                   ");
        sbQueryToday.append("\n                     OR lp.value_38 is null                                                   ");
        sbQueryToday.append("\n                     OR lp.value_39 is null                                                   ");
        sbQueryToday.append("\n                     OR lp.value_40 is null                                                   ");
        sbQueryToday.append("\n                     OR lp.value_41 is null                                                   ");
        sbQueryToday.append("\n                     OR lp.value_42 is null                                                   ");
        sbQueryToday.append("\n                     OR lp.value_43 is null                                                   ");
        sbQueryToday.append("\n                     OR lp.value_44 is null                                                   ");
        sbQueryToday.append("\n                     OR lp.value_45 is null                                                   ");
        sbQueryToday.append("\n                     OR lp.value_46 is null                                                   ");
        sbQueryToday.append("\n                     OR lp.value_47 is null                                                   ");
        sbQueryToday.append("\n                     OR lp.value_48 is null                                                   ");
        sbQueryToday.append("\n                     OR lp.value_49 is null                                                   ");
        sbQueryToday.append("\n                     OR lp.value_50 is null                                                   ");
        sbQueryToday.append("\n                     OR lp.value_51 is null                                                   ");
        sbQueryToday.append("\n                     OR lp.value_52 is null                                                   ");
        sbQueryToday.append("\n                     OR lp.value_53 is null                                                   ");
        sbQueryToday.append("\n                     OR lp.value_54 is null                                                   ");
        sbQueryToday.append("\n                     OR lp.value_55 is null                                                   ");
        sbQueryToday.append("\n                     OR lp.value_56 is null                                                   ");
        sbQueryToday.append("\n                     OR lp.value_57 is null                                                   ");
        sbQueryToday.append("\n                     OR lp.value_58 is null                                                   ");
        sbQueryToday.append("\n                     OR lp.value_59 is null then 0 else 1 end                                 ");
        sbQueryToday.append("\n  when m.lp_interval = 5 then case when lp.value_00 is null                        ");
        sbQueryToday.append("\n                     OR lp.value_05 is null                                                   ");
        sbQueryToday.append("\n                     OR lp.value_10 is null                                                   ");
        sbQueryToday.append("\n                     OR lp.value_15 is null                                                   ");
        sbQueryToday.append("\n                     OR lp.value_20 is null                                                   ");
        sbQueryToday.append("\n                     OR lp.value_25 is null                                                   ");
        sbQueryToday.append("\n                     OR lp.value_30 is null                                                   ");
        sbQueryToday.append("\n                     OR lp.value_35 is null                                                   ");
        sbQueryToday.append("\n                     OR lp.value_40 is null                                                   ");
        sbQueryToday.append("\n                     OR lp.value_45 is null                                                   ");
        sbQueryToday.append("\n                     OR lp.value_50 is null                                                   ");
        sbQueryToday.append("\n                     OR lp.value_55 is null then 0 else 1 end                                 ");
        sbQueryToday.append("\n when m.lp_interval = 10 then case when lp.value_00 is null                        ");
        sbQueryToday.append("\n                     OR lp.value_10 is null                                                   ");
        sbQueryToday.append("\n                     OR lp.value_20 is null                                                   ");
        sbQueryToday.append("\n                     OR lp.value_30 is null                                                   ");
        sbQueryToday.append("\n                     OR lp.value_40 is null                                                   ");
        sbQueryToday.append("\n                     OR lp.value_50 is null then 0 else 1 end                                 ");
        sbQueryToday.append("\n when m.lp_interval = 15 then case when lp.value_00 is null                        ");
        sbQueryToday.append("\n                     OR lp.value_15 is null                                                   ");
        sbQueryToday.append("\n                     OR lp.value_30 is null                                                   ");
        sbQueryToday.append("\n                     OR lp.value_45 is null then 0 else 1 end                                 ");
        sbQueryToday.append("\n when m.lp_interval = 30 then case when lp.value_00 is null                        ");
        sbQueryToday.append("\n                 OR lp.value_30 is null then 1 else 0 end                                 ");
        sbQueryToday.append("\n when m.lp_interval = 60 then case when lp.value_00 is null then 0 else 1 end      ");
        sbQueryToday.append("\n else 0 end                                                                        ");
        sbQueryToday.append("\n group by yyyymmdd,m.id                                                            ");
        sbQueryToday.append("\n having count(*) = :hourCnt                                                              ");

        // query 작성
        StringBuffer sbQueryCurrHour = new StringBuffer();
        sbQueryCurrHour.append("\n       select lp.yyyymmdd,m.id                                       "); 
        sbQueryCurrHour.append("\n       from meter m join ").append(lpTable).append(" lp on lp.meter_id = m.id                                 "); 
        sbQueryCurrHour.append("\n       where lp.yyyymmdd = :searchEndDate                             ");
        sbQueryCurrHour.append("\n       and lp.hh = :currHour                              ");
        sbQueryCurrHour.append("\n       and lp.channel = :channel                                                               ");
        sbQueryCurrHour.append("\n AND m.install_Date <= :installDate     ");
        if(!"".equals(supplierId)){
            sbQueryCurrHour.append("\n AND m.supplier_id = :supplierId ");
        }
        sbQueryCurrHour.append("\n       and 1 = case when m.lp_interval = 1 then case when lp.value_00 is null           "); 
        if(currMinute >= 1)sbQueryCurrHour.append("\n                           OR lp.value_01 is null                                                  "); 
        if(currMinute >= 2)sbQueryCurrHour.append("\n                           OR lp.value_02 is null                                                  "); 
        if(currMinute >= 3)sbQueryCurrHour.append("\n                           OR lp.value_03 is null                                                  "); 
        if(currMinute >= 4)sbQueryCurrHour.append("\n                           OR lp.value_04 is null                                                  "); 
        if(currMinute >= 5)sbQueryCurrHour.append("\n                           OR lp.value_05 is null                                                  "); 
        if(currMinute >= 6)sbQueryCurrHour.append("\n                           OR lp.value_06 is null                                                  "); 
        if(currMinute >= 7)sbQueryCurrHour.append("\n                           OR lp.value_07 is null                                                  "); 
        if(currMinute >= 8)sbQueryCurrHour.append("\n                           OR lp.value_08 is null                                                  "); 
        if(currMinute >= 9)sbQueryCurrHour.append("\n                           OR lp.value_09 is null                                                  "); 
        if(currMinute >= 10)sbQueryCurrHour.append("\n                          OR lp.value_10 is null                                                  "); 
        if(currMinute >= 11)sbQueryCurrHour.append("\n                          OR lp.value_11 is null                                                  "); 
        if(currMinute >= 12)sbQueryCurrHour.append("\n                          OR lp.value_12 is null                                                  "); 
        if(currMinute >= 13)sbQueryCurrHour.append("\n                          OR lp.value_13 is null                                                  "); 
        if(currMinute >= 14)sbQueryCurrHour.append("\n                          OR lp.value_14 is null                                                  "); 
        if(currMinute >= 15)sbQueryCurrHour.append("\n                          OR lp.value_15 is null                                                  "); 
        if(currMinute >= 16)sbQueryCurrHour.append("\n                          OR lp.value_16 is null                                                  "); 
        if(currMinute >= 17)sbQueryCurrHour.append("\n                          OR lp.value_17 is null                                                  "); 
        if(currMinute >= 18)sbQueryCurrHour.append("\n                          OR lp.value_18 is null                                                  "); 
        if(currMinute >= 19)sbQueryCurrHour.append("\n                          OR lp.value_19 is null                                                  "); 
        if(currMinute >= 20)sbQueryCurrHour.append("\n                          OR lp.value_20 is null                                                  "); 
        if(currMinute >= 21)sbQueryCurrHour.append("\n                          OR lp.value_21 is null                                                  "); 
        if(currMinute >= 22)sbQueryCurrHour.append("\n                          OR lp.value_22 is null                                                  "); 
        if(currMinute >= 23)sbQueryCurrHour.append("\n                          OR lp.value_23 is null                                                  "); 
        if(currMinute >= 24)sbQueryCurrHour.append("\n                          OR lp.value_24 is null                                                  "); 
        if(currMinute >= 25)sbQueryCurrHour.append("\n                          OR lp.value_25 is null                                                  "); 
        if(currMinute >= 26)sbQueryCurrHour.append("\n                          OR lp.value_26 is null                                                  "); 
        if(currMinute >= 27)sbQueryCurrHour.append("\n                          OR lp.value_27 is null                                                  "); 
        if(currMinute >= 28)sbQueryCurrHour.append("\n                          OR lp.value_28 is null                                                  "); 
        if(currMinute >= 29)sbQueryCurrHour.append("\n                          OR lp.value_29 is null                                                  "); 
        if(currMinute >= 30)sbQueryCurrHour.append("\n                          OR lp.value_30 is null                                                  "); 
        if(currMinute >= 31)sbQueryCurrHour.append("\n                          OR lp.value_31 is null                                                  "); 
        if(currMinute >= 32)sbQueryCurrHour.append("\n                          OR lp.value_32 is null                                                  "); 
        if(currMinute >= 33)sbQueryCurrHour.append("\n                          OR lp.value_33 is null                                                  "); 
        if(currMinute >= 34)sbQueryCurrHour.append("\n                          OR lp.value_34 is null                                                  "); 
        if(currMinute >= 35)sbQueryCurrHour.append("\n                          OR lp.value_35 is null                                                  "); 
        if(currMinute >= 36)sbQueryCurrHour.append("\n                          OR lp.value_36 is null                                                  "); 
        if(currMinute >= 37)sbQueryCurrHour.append("\n                          OR lp.value_37 is null                                                  "); 
        if(currMinute >= 38)sbQueryCurrHour.append("\n                          OR lp.value_38 is null                                                  "); 
        if(currMinute >= 39)sbQueryCurrHour.append("\n                          OR lp.value_39 is null                                                  "); 
        if(currMinute >= 40)sbQueryCurrHour.append("\n                          OR lp.value_40 is null                                                  "); 
        if(currMinute >= 41)sbQueryCurrHour.append("\n                          OR lp.value_41 is null                                                  "); 
        if(currMinute >= 42)sbQueryCurrHour.append("\n                          OR lp.value_42 is null                                                  "); 
        if(currMinute >= 43)sbQueryCurrHour.append("\n                          OR lp.value_43 is null                                                  "); 
        if(currMinute >= 44)sbQueryCurrHour.append("\n                          OR lp.value_44 is null                                                  "); 
        if(currMinute >= 45)sbQueryCurrHour.append("\n                          OR lp.value_45 is null                                                  "); 
        if(currMinute >= 46)sbQueryCurrHour.append("\n                          OR lp.value_46 is null                                                  "); 
        if(currMinute >= 47)sbQueryCurrHour.append("\n                          OR lp.value_47 is null                                                  "); 
        if(currMinute >= 48)sbQueryCurrHour.append("\n                          OR lp.value_48 is null                                                  "); 
        if(currMinute >= 49)sbQueryCurrHour.append("\n                          OR lp.value_49 is null                                                  "); 
        if(currMinute >= 50)sbQueryCurrHour.append("\n                          OR lp.value_50 is null                                                  "); 
        if(currMinute >= 51)sbQueryCurrHour.append("\n                          OR lp.value_51 is null                                                  "); 
        if(currMinute >= 52)sbQueryCurrHour.append("\n                          OR lp.value_52 is null                                                  "); 
        if(currMinute >= 53)sbQueryCurrHour.append("\n                          OR lp.value_53 is null                                                  "); 
        if(currMinute >= 54)sbQueryCurrHour.append("\n                          OR lp.value_54 is null                                                  "); 
        if(currMinute >= 55)sbQueryCurrHour.append("\n                          OR lp.value_55 is null                                                  "); 
        if(currMinute >= 56)sbQueryCurrHour.append("\n                          OR lp.value_56 is null                                                  "); 
        if(currMinute >= 57)sbQueryCurrHour.append("\n                          OR lp.value_57 is null                                                  "); 
        if(currMinute >= 58)sbQueryCurrHour.append("\n                          OR lp.value_58 is null                                                  "); 
        if(currMinute >= 59)sbQueryCurrHour.append("\n                          OR lp.value_59 is null                                  "); 
        sbQueryCurrHour.append("\n                                              then 0 else 1 end                                ");
        sbQueryCurrHour.append("\n        when m.lp_interval = 5 then case when lp.value_00 is null                       "); 
        if(currMinute >= 5)sbQueryCurrHour.append("\n                           OR lp.value_05 is null                                                  "); 
        if(currMinute >= 10)sbQueryCurrHour.append("\n                          OR lp.value_10 is null                                                  "); 
        if(currMinute >= 15)sbQueryCurrHour.append("\n                          OR lp.value_15 is null                                                  "); 
        if(currMinute >= 20)sbQueryCurrHour.append("\n                          OR lp.value_20 is null                                                  "); 
        if(currMinute >= 25)sbQueryCurrHour.append("\n                          OR lp.value_25 is null                                                  "); 
        if(currMinute >= 30)sbQueryCurrHour.append("\n                          OR lp.value_30 is null                                                  "); 
        if(currMinute >= 35)sbQueryCurrHour.append("\n                          OR lp.value_35 is null                                                  "); 
        if(currMinute >= 40)sbQueryCurrHour.append("\n                          OR lp.value_40 is null                                                  "); 
        if(currMinute >= 45)sbQueryCurrHour.append("\n                          OR lp.value_45 is null                                                  "); 
        if(currMinute >= 50)sbQueryCurrHour.append("\n                          OR lp.value_50 is null                                                  "); 
        if(currMinute >= 55)sbQueryCurrHour.append("\n                          OR lp.value_55 is null                                 "); 
        sbQueryCurrHour.append("\n                                              then 0 else 1 end                                ");
        sbQueryCurrHour.append("\n       when m.lp_interval = 10 then case when lp.value_00 is null                       "); 
        if(currMinute >= 10)sbQueryCurrHour.append("\n                          OR lp.value_10 is null                                                  "); 
        if(currMinute >= 20)sbQueryCurrHour.append("\n                          OR lp.value_20 is null                                                  "); 
        if(currMinute >= 30)sbQueryCurrHour.append("\n                          OR lp.value_30 is null                                                  "); 
        if(currMinute >= 40)sbQueryCurrHour.append("\n                          OR lp.value_40 is null                                                  "); 
        if(currMinute >= 50)sbQueryCurrHour.append("\n                          OR lp.value_50 is null                                 "); 
        sbQueryCurrHour.append("\n                                              then 0 else 1 end                                ");
        sbQueryCurrHour.append("\n       when m.lp_interval = 15 then case when lp.value_00 is null                       "); 
        if(currMinute >= 15)sbQueryCurrHour.append("\n                          OR lp.value_15 is null                                                  "); 
        if(currMinute >= 30)sbQueryCurrHour.append("\n                          OR lp.value_30 is null                                                  "); 
        if(currMinute >= 45)sbQueryCurrHour.append("\n                          OR lp.value_45 is null                                 "); 
        sbQueryCurrHour.append("\n                                              then 0 else 1 end                                ");
        sbQueryCurrHour.append("\n       when m.lp_interval = 30 then case when lp.value_00 is null                       "); 
        if(currMinute >= 30)sbQueryCurrHour.append("\n                      OR lp.value_30 is null                                 "); 
        sbQueryCurrHour.append("\n                                              then 0 else 1 end                                ");
        sbQueryCurrHour.append("\n       when m.lp_interval = 60 then case when lp.value_00 is null then 0 else 1 end     "); 
        sbQueryCurrHour.append("\n       else 0 end                                                                       "); 
        
        
        HashMap successData = new HashMap();
        
        int todayMissingCount = 0;
        if(Integer.parseInt(searchEndDate)<Integer.parseInt(currDate)){
            // 파라메터 설정
            SQLQuery query = getSession().createSQLQuery(sbQuery.toString());
            query.setString("searchStartDate",  searchStartDate);
            query.setString("searchEndDate",    searchEndDate);
            query.setString("installDate", searchStartDate + CommonConstants.DefaultDate.LAST_HHMMSS.getValue());
            query.setInteger("channel",     channel);
            if(!"".equals(supplierId)){
                query.setInteger("supplierId", Integer.parseInt(supplierId));
            }
            
            // query 결과목록
            List<Object> successDataList = query.list();
            for(Object obj : successDataList){
                Object[] objs = (Object[])obj;
                successData.put((String)objs[0] , ((Number)objs[1]).intValue());                 
            }
            
        }else{
            // 시작을 ~ 종료일전일까지의 누락건수조회  having 절의 카운트가 24가 아닌경우조회
            Query query = getSession().createSQLQuery(sbQuery.toString());
            query.setString("searchStartDate", searchStartDate);
            query.setString("searchEndDate", CalendarUtil.getDateWithoutFormat(searchEndDate,Calendar.DATE, -1));
            query.setString("installDate", searchStartDate + CommonConstants.DefaultDate.LAST_HHMMSS.getValue());
            query.setInteger("channel", channel);
            if(!"".equals(supplierId)){
                query.setInteger("supplierId", Integer.parseInt(supplierId));
            }
            
            // query 결과목록 - 시작일부터 종료일전일까지의 결과
            List<Object> successDataList = query.list();
            for(Object obj : successDataList){
                Object[] objs = (Object[])obj;
                successData.put((String)objs[0] , ((Number)objs[1]).intValue());                 
            }
            

            // 현재일자의 현재시간 전까지의 데이터와 현재시간의 데이터를 union 하여 누락건수를 조회한다.
            StringBuffer queryTodayCurrHour = new StringBuffer();
            queryTodayCurrHour.append("\n select count(*)  ");
            queryTodayCurrHour.append("\n from (           ");
            queryTodayCurrHour.append(sbQueryToday);
            queryTodayCurrHour.append("\n union            ");
            queryTodayCurrHour.append(sbQueryCurrHour);
            queryTodayCurrHour.append("\n )x                ");
            
            Query queryToday = getSession().createSQLQuery(queryTodayCurrHour.toString());
            queryToday.setString("searchEndDate", searchEndDate);
            queryToday.setString("installDate", searchStartDate + CommonConstants.DefaultDate.LAST_HHMMSS.getValue());
            queryToday.setString("currHour", currHour);
            queryToday.setInteger("hourCnt", Integer.parseInt(currHour));
            queryToday.setInteger("channel", channel);
            if(!"".equals(supplierId)){
                queryToday.setInteger("supplierId", Integer.parseInt(supplierId));
            }
            
            // query 결과목록 - 오늘날짜의 현재시간 전까지의 결과
            Number todaySuccessCnt = (Number)queryToday.uniqueResult();
            todayMissingCount = totalMeterCnt - todaySuccessCnt.intValue();

        }
        
        
        // 특정일자에 모두 누락될수도 있으므로 시작일부터 종료일까지의 데이터는 고정
        for(int i=0;i<period;i++){
            String yyyymmdd = CalendarUtil.getDateWithoutFormat(searchStartDate,Calendar.DATE, i);
            HashMap resultMap = new HashMap();
            resultMap.put("no", i+1);
            resultMap.put("yyyymmdd", yyyymmdd);
            resultMap.put("hh", "-");
            
            if(yyyymmdd.equals(currDate)){//오늘날짜이고 현재시간 이후일경우 0으로 세팅한다. 
                resultMap.put("missingCount", todayMissingCount);
            }else{
                Integer successCount =  successData.get(yyyymmdd)==null?0:((Number)successData.get(yyyymmdd)).intValue();
                resultMap.put("missingCount", totalMeterCnt - successCount);
            }
            
            resultMap.put("xField",TimeLocaleUtil.getLocaleDate(yyyymmdd));
            
            resultList.add(resultMap);
        }
    
        return resultList;
        
    }
    
    /**
     * 검침데이터(LP) 에 누락된 미터목록을 조회한다.
     * @param params
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<Object> getMissingMeters(Map<String,Object> params){
        
        String searchStartDate  = (String)params.get("searchStartDate");
        String searchEndDate    = (String)params.get("searchEndDate");
        String meterType        = StringUtil.nullToBlank(params.get("meterType"));
        String supplierId       = StringUtil.nullToBlank(params.get("supplierId"));
        Integer channel         = (Integer)params.get("channel");
        String mdsId            = StringUtil.nullToBlank(params.get("mdsId"));
        String deviceType       = StringUtil.nullToBlank((String)params.get("deviceType"));
        String deviceId         = StringUtil.nullToBlank((String)params.get("deviceId"));
        
        String today            = TimeUtil.getCurrentTimeMilli(); //yyyymmddhhmmss
        String currDate         = today.substring(0, 8);
        String currHour         = today.substring(8, 10);
        Integer currMinute      = Integer.parseInt(today.substring(10, 12));
        
        String lpTable = CommonConstants.MeterType.valueOf(meterType).getLpTableName();
        
        int period=0;
        try {
            period = TimeUtil.getDayDuration(searchStartDate, searchEndDate) + 1;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        
        StringBuffer sbQueryAllPeriod = new StringBuffer();
        sbQueryAllPeriod.append("\n SELECT customer.NAME                                                                       ");
        sbQueryAllPeriod.append("\n        ,modem.DEVICE_SERIAL                                                                ");
        sbQueryAllPeriod.append("\n        ,mcu.SYS_NAME as mcuName                                                                           ");
        sbQueryAllPeriod.append("\n        ,m1.MDS_ID                                                                          ");
        sbQueryAllPeriod.append("\n        ,m1.LAST_READ_DATE                                                                  ");
        sbQueryAllPeriod.append("\n        ,m1.ID                                                                  ");
        sbQueryAllPeriod.append("\n        ,m1.LP_INTERVAL                                                                  ");
        sbQueryAllPeriod.append("\n FROM METER m1 LEFT OUTER JOIN CONTRACT contract ON m1.ID = contract.METER_ID          ");
        sbQueryAllPeriod.append("\n                  LEFT OUTER JOIN CUSTOMER customer ON contract.CUSTOMER_ID = customer.ID             ");
        sbQueryAllPeriod.append("\n                  LEFT OUTER JOIN MODEM modem ON m1.MODEM_ID = modem.ID                               ");
        sbQueryAllPeriod.append("\n                  LEFT OUTER JOIN MCU mcu ON modem.MCU_ID = mcu.ID                                    ");
        sbQueryAllPeriod.append("\n WHERE 1=1                                                                                  ");
        sbQueryAllPeriod.append("\n AND m1.INSTALL_DATE <= :installDate                                                    ");
        sbQueryAllPeriod.append("\n AND m1.METER = :meterType                                                                   ");
        if(!"".equals(supplierId)){
            sbQueryAllPeriod.append("\n AND m1.SUPPLIER_ID = :supplierId ");
        }
        if(!"".equals(mdsId)){
            sbQueryAllPeriod.append("\n AND m1.MDS_ID LIKE :mdsId ");
        }
        //deviceId , deviceType 의  null 값 체크
        if(!"".equals(deviceId) && !"".equals(deviceType)){
            if(CommonConstants.DeviceType.MCU.getCode().equals(Integer.parseInt(deviceType))){
                sbQueryAllPeriod.append("\n AND mcu.SYS_NAME LIKE :deviceId ");
            }else{
                sbQueryAllPeriod.append("\n AND modem.DEVICE_SERIAL LIKE :deviceId ");
            }
        }
        sbQueryAllPeriod.append("\n AND m1.ID NOT IN (                                                                         ");
        sbQueryAllPeriod.append("\n         SELECT x.ID                                                                             ");
        sbQueryAllPeriod.append("\n         FROM(                                                                                   ");
        sbQueryAllPeriod.append("\n             SELECT m.ID,lp.YYYYMMDD                                                               ");
        sbQueryAllPeriod.append("\n             FROM METER m LEFT OUTER JOIN ").append(lpTable).append(" lp ON lp.METER_ID = m.ID                           ");
        sbQueryAllPeriod.append("\n             AND lp.YYYYMMDD BETWEEN :searchStartDate AND :searchEndDate                                     ");
        sbQueryAllPeriod.append("\n             AND lp.CHANNEL = :channel                                                                    ");
        sbQueryAllPeriod.append("\n             WHERE 1=1                                                                             ");
        sbQueryAllPeriod.append("\n             AND m.INSTALL_DATE <= :installDate                                                ");
        sbQueryAllPeriod.append("\n             AND 1 = CASE WHEN m.LP_INTERVAL = 1 THEN CASE WHEN lp.VALUE_00 IS NULL                  ");
        sbQueryAllPeriod.append("\n                                                         OR lp.VALUE_01 IS NULL                ");
        sbQueryAllPeriod.append("\n                                                         OR lp.VALUE_02 IS NULL                ");
        sbQueryAllPeriod.append("\n                                                         OR lp.VALUE_03 IS NULL                ");
        sbQueryAllPeriod.append("\n                                                         OR lp.VALUE_04 IS NULL                ");
        sbQueryAllPeriod.append("\n                                                         OR lp.VALUE_05 IS NULL                ");
        sbQueryAllPeriod.append("\n                                                         OR lp.VALUE_06 IS NULL                ");
        sbQueryAllPeriod.append("\n                                                         OR lp.VALUE_07 IS NULL                ");
        sbQueryAllPeriod.append("\n                                                         OR lp.VALUE_08 IS NULL                ");
        sbQueryAllPeriod.append("\n                                                         OR lp.VALUE_09 IS NULL                ");
        sbQueryAllPeriod.append("\n                                                         OR lp.VALUE_10 IS NULL                ");
        sbQueryAllPeriod.append("\n                                                         OR lp.VALUE_11 IS NULL                ");
        sbQueryAllPeriod.append("\n                                                         OR lp.VALUE_12 IS NULL                ");
        sbQueryAllPeriod.append("\n                                                         OR lp.VALUE_13 IS NULL                ");
        sbQueryAllPeriod.append("\n                                                         OR lp.VALUE_14 IS NULL                ");
        sbQueryAllPeriod.append("\n                                                         OR lp.VALUE_15 IS NULL                ");
        sbQueryAllPeriod.append("\n                                                         OR lp.VALUE_16 IS NULL                ");
        sbQueryAllPeriod.append("\n                                                         OR lp.VALUE_17 IS NULL                ");
        sbQueryAllPeriod.append("\n                                                         OR lp.VALUE_18 IS NULL                ");
        sbQueryAllPeriod.append("\n                                                         OR lp.VALUE_19 IS NULL                ");
        sbQueryAllPeriod.append("\n                                                         OR lp.VALUE_20 IS NULL                ");
        sbQueryAllPeriod.append("\n                                                         OR lp.VALUE_21 IS NULL                ");
        sbQueryAllPeriod.append("\n                                                         OR lp.VALUE_22 IS NULL                ");
        sbQueryAllPeriod.append("\n                                                         OR lp.VALUE_23 IS NULL                ");
        sbQueryAllPeriod.append("\n                                                         OR lp.VALUE_24 IS NULL                ");
        sbQueryAllPeriod.append("\n                                                         OR lp.VALUE_25 IS NULL                ");
        sbQueryAllPeriod.append("\n                                                         OR lp.VALUE_26 IS NULL                ");
        sbQueryAllPeriod.append("\n                                                         OR lp.VALUE_27 IS NULL                ");
        sbQueryAllPeriod.append("\n                                                         OR lp.VALUE_28 IS NULL                ");
        sbQueryAllPeriod.append("\n                                                         OR lp.VALUE_29 IS NULL                ");
        sbQueryAllPeriod.append("\n                                                         OR lp.VALUE_30 IS NULL                ");
        sbQueryAllPeriod.append("\n                                                         OR lp.VALUE_31 IS NULL                ");
        sbQueryAllPeriod.append("\n                                                         OR lp.VALUE_32 IS NULL                ");
        sbQueryAllPeriod.append("\n                                                         OR lp.VALUE_33 IS NULL                ");
        sbQueryAllPeriod.append("\n                                                         OR lp.VALUE_34 IS NULL                ");
        sbQueryAllPeriod.append("\n                                                         OR lp.VALUE_35 IS NULL                ");
        sbQueryAllPeriod.append("\n                                                         OR lp.VALUE_36 IS NULL                ");
        sbQueryAllPeriod.append("\n                                                         OR lp.VALUE_37 IS NULL                ");
        sbQueryAllPeriod.append("\n                                                         OR lp.VALUE_38 IS NULL                ");
        sbQueryAllPeriod.append("\n                                                         OR lp.VALUE_39 IS NULL                ");
        sbQueryAllPeriod.append("\n                                                         OR lp.VALUE_40 IS NULL                ");
        sbQueryAllPeriod.append("\n                                                         OR lp.VALUE_41 IS NULL                ");
        sbQueryAllPeriod.append("\n                                                         OR lp.VALUE_42 IS NULL                ");
        sbQueryAllPeriod.append("\n                                                         OR lp.VALUE_43 IS NULL                ");
        sbQueryAllPeriod.append("\n                                                         OR lp.VALUE_44 IS NULL                ");
        sbQueryAllPeriod.append("\n                                                         OR lp.VALUE_45 IS NULL                ");
        sbQueryAllPeriod.append("\n                                                         OR lp.VALUE_46 IS NULL                ");
        sbQueryAllPeriod.append("\n                                                         OR lp.VALUE_47 IS NULL                ");
        sbQueryAllPeriod.append("\n                                                         OR lp.VALUE_48 IS NULL                ");
        sbQueryAllPeriod.append("\n                                                         OR lp.VALUE_49 IS NULL                ");
        sbQueryAllPeriod.append("\n                                                         OR lp.VALUE_50 IS NULL                ");
        sbQueryAllPeriod.append("\n                                                         OR lp.VALUE_51 IS NULL                ");
        sbQueryAllPeriod.append("\n                                                         OR lp.VALUE_52 IS NULL                ");
        sbQueryAllPeriod.append("\n                                                         OR lp.VALUE_53 IS NULL                ");
        sbQueryAllPeriod.append("\n                                                         OR lp.VALUE_54 IS NULL                ");
        sbQueryAllPeriod.append("\n                                                         OR lp.VALUE_55 IS NULL                ");
        sbQueryAllPeriod.append("\n                                                         OR lp.VALUE_56 IS NULL                ");
        sbQueryAllPeriod.append("\n                                                         OR lp.VALUE_57 IS NULL                ");
        sbQueryAllPeriod.append("\n                                                         OR lp.VALUE_58 IS NULL                ");
        sbQueryAllPeriod.append("\n                                                         OR lp.VALUE_59 IS NULL                ");
        sbQueryAllPeriod.append("\n                                                       THEN 0 ELSE 1 end                       ");
        sbQueryAllPeriod.append("\n                   WHEN m.lp_interval = 5 THEN CASE WHEN lp.VALUE_00 IS NULL                ");
        sbQueryAllPeriod.append("\n                                                         OR lp.VALUE_05 IS NULL                ");
        sbQueryAllPeriod.append("\n                                                         OR lp.VALUE_10 IS NULL                ");
        sbQueryAllPeriod.append("\n                                                         OR lp.VALUE_15 IS NULL                ");
        sbQueryAllPeriod.append("\n                                                         OR lp.VALUE_20 IS NULL                ");
        sbQueryAllPeriod.append("\n                                                         OR lp.VALUE_25 IS NULL                ");
        sbQueryAllPeriod.append("\n                                                         OR lp.VALUE_30 IS NULL                ");
        sbQueryAllPeriod.append("\n                                                         OR lp.VALUE_35 IS NULL                ");
        sbQueryAllPeriod.append("\n                                                         OR lp.VALUE_40 IS NULL                ");
        sbQueryAllPeriod.append("\n                                                         OR lp.VALUE_45 IS NULL                ");
        sbQueryAllPeriod.append("\n                                                         OR lp.VALUE_50 IS NULL                ");
        sbQueryAllPeriod.append("\n                                                         OR lp.VALUE_55 IS NULL                ");
        sbQueryAllPeriod.append("\n                                                       THEN 0 ELSE 1 end                       ");
        sbQueryAllPeriod.append("\n                   WHEN m.lp_interval = 10 THEN CASE WHEN lp.VALUE_00 IS NULL               ");
        sbQueryAllPeriod.append("\n                                                      OR lp.VALUE_10 IS NULL               ");
        sbQueryAllPeriod.append("\n                                                      OR lp.VALUE_20 IS NULL               ");
        sbQueryAllPeriod.append("\n                                                      OR lp.VALUE_30 IS NULL               ");
        sbQueryAllPeriod.append("\n                                                      OR lp.VALUE_40 IS NULL               ");
        sbQueryAllPeriod.append("\n                                                      OR lp.VALUE_50 IS NULL               ");
        sbQueryAllPeriod.append("\n                                                    THEN 0 ELSE 1 end                      ");
        sbQueryAllPeriod.append("\n                   WHEN m.lp_interval = 15 THEN CASE WHEN lp.VALUE_00 IS NULL               ");
        sbQueryAllPeriod.append("\n                                                      OR lp.VALUE_15 IS NULL               ");
        sbQueryAllPeriod.append("\n                                                      OR lp.VALUE_30 IS NULL               ");
        sbQueryAllPeriod.append("\n                                                      OR lp.VALUE_45 IS NULL               ");
        sbQueryAllPeriod.append("\n                                                    THEN 0 ELSE 1 end                      ");
        sbQueryAllPeriod.append("\n                   WHEN m.lp_interval = 30 THEN CASE WHEN lp.VALUE_00 IS NULL               ");
        sbQueryAllPeriod.append("\n                                                      OR lp.VALUE_30 IS NULL               ");
        sbQueryAllPeriod.append("\n                                                    THEN 1 ELSE 0 end                      ");
        sbQueryAllPeriod.append("\n                   WHEN m.lp_interval = 60 THEN CASE WHEN lp.VALUE_00 IS NULL               ");
        sbQueryAllPeriod.append("\n                                                     THEN 0 ELSE 1 end                      ");
        sbQueryAllPeriod.append("\n                   ELSE 0 end                                                               ");
        sbQueryAllPeriod.append("\n                 GROUP BY m.ID,lp.YYYYMMDD                                                         ");
        sbQueryAllPeriod.append("\n             HAVING COUNT(m.ID) = 24                                                               ");
        sbQueryAllPeriod.append("\n         )x                                                                                      ");
        sbQueryAllPeriod.append("\n         WHERE x.YYYYMMDD IS NOT NULL                                                            ");
        sbQueryAllPeriod.append("\n         GROUP BY x.ID                                                                           ");
        sbQueryAllPeriod.append("\n         HAVING COUNT(x.ID) = :period                                                                 ");
        sbQueryAllPeriod.append("\n         )                                                                 ");

        StringBuffer sbQueryAllPeriodWithToday = new StringBuffer();
        sbQueryAllPeriodWithToday.append("\n SELECT customer.NAME                                                                       ");
        sbQueryAllPeriodWithToday.append("\n        ,modem.DEVICE_SERIAL                                                                ");
        sbQueryAllPeriodWithToday.append("\n        ,mcu.SYS_NAME  as mcuName                                                                         ");
        sbQueryAllPeriodWithToday.append("\n        ,m1.MDS_ID                                                                          ");
        sbQueryAllPeriodWithToday.append("\n        ,m1.LAST_READ_DATE                                                                  ");
        sbQueryAllPeriodWithToday.append("\n        ,m1.ID                                                                  ");
        sbQueryAllPeriodWithToday.append("\n        ,m1.LP_INTERVAL                                                                  ");
        sbQueryAllPeriodWithToday.append("\n FROM METER m1 LEFT OUTER JOIN CONTRACT contract ON m1.ID = contract.METER_ID         ");
        sbQueryAllPeriodWithToday.append("\n                 LEFT OUTER JOIN CUSTOMER customer ON contract.CUSTOMER_ID = customer.ID             ");
        sbQueryAllPeriodWithToday.append("\n                 LEFT OUTER JOIN MODEM modem ON m1.MODEM_ID = modem.ID                               ");
        sbQueryAllPeriodWithToday.append("\n                 LEFT OUTER JOIN MCU mcu ON modem.MCU_ID = mcu.ID                                    ");
        sbQueryAllPeriodWithToday.append("\n WHERE 1=1                                                                                  ");
        sbQueryAllPeriodWithToday.append("\n AND m1.INSTALL_DATE <= :installDate                                                    ");
        sbQueryAllPeriodWithToday.append("\n AND m1.METER = :meterType                                                                  ");
        if(!"".equals(supplierId)){
            sbQueryAllPeriodWithToday.append("\n AND m1.SUPPLIER_ID = :supplierId ");
        }
        if(!"".equals(mdsId)){
            sbQueryAllPeriodWithToday.append("\n AND m1.MDS_ID LIKE :mdsId ");
        }
        //deviceId , deviceType 의  null 값 체크
        if(!"".equals(deviceId) && !"".equals(deviceType)){
            if(CommonConstants.DeviceType.MCU.getCode().equals(Integer.parseInt(deviceType))){
                sbQueryAllPeriodWithToday.append("\n AND mcu.SYS_NAME LIKE :deviceId ");
            }else{
                sbQueryAllPeriodWithToday.append("\n AND modem.DEVICE_SERIAL LIKE :deviceId ");
            }
        }

        
        sbQueryAllPeriodWithToday.append("\n AND m1.ID NOT IN (                                                                         ");
        sbQueryAllPeriodWithToday.append("\n        SELECT x.ID                                                                             ");
        sbQueryAllPeriodWithToday.append("\n        FROM(                                                                                   ");
        sbQueryAllPeriodWithToday.append("\n            SELECT m.ID,lp.YYYYMMDD                                                               ");
        sbQueryAllPeriodWithToday.append("\n            FROM METER m LEFT OUTER JOIN ").append(lpTable).append(" lp ON lp.METER_ID = m.ID                           ");
        sbQueryAllPeriodWithToday.append("\n            AND lp.YYYYMMDD BETWEEN :searchStartDate AND :searchEndDate                                     ");
        sbQueryAllPeriodWithToday.append("\n            AND lp.CHANNEL = :channel                                                                    ");
        sbQueryAllPeriodWithToday.append("\n            WHERE 1=1                                                                             ");
        sbQueryAllPeriodWithToday.append("\n            AND m.INSTALL_DATE <= :installDate                                                ");
        sbQueryAllPeriodWithToday.append("\n            AND 1 = CASE WHEN m.LP_INTERVAL = 1 THEN CASE WHEN lp.VALUE_00 IS NULL                  ");
        sbQueryAllPeriodWithToday.append("\n                                                        OR lp.VALUE_01 IS NULL                ");
        sbQueryAllPeriodWithToday.append("\n                                                        OR lp.VALUE_02 IS NULL                ");
        sbQueryAllPeriodWithToday.append("\n                                                        OR lp.VALUE_03 IS NULL                ");
        sbQueryAllPeriodWithToday.append("\n                                                        OR lp.VALUE_04 IS NULL                ");
        sbQueryAllPeriodWithToday.append("\n                                                        OR lp.VALUE_05 IS NULL                ");
        sbQueryAllPeriodWithToday.append("\n                                                        OR lp.VALUE_06 IS NULL                ");
        sbQueryAllPeriodWithToday.append("\n                                                        OR lp.VALUE_07 IS NULL                ");
        sbQueryAllPeriodWithToday.append("\n                                                        OR lp.VALUE_08 IS NULL                ");
        sbQueryAllPeriodWithToday.append("\n                                                        OR lp.VALUE_09 IS NULL                ");
        sbQueryAllPeriodWithToday.append("\n                                                        OR lp.VALUE_10 IS NULL                ");
        sbQueryAllPeriodWithToday.append("\n                                                        OR lp.VALUE_11 IS NULL                ");
        sbQueryAllPeriodWithToday.append("\n                                                        OR lp.VALUE_12 IS NULL                ");
        sbQueryAllPeriodWithToday.append("\n                                                        OR lp.VALUE_13 IS NULL                ");
        sbQueryAllPeriodWithToday.append("\n                                                        OR lp.VALUE_14 IS NULL                ");
        sbQueryAllPeriodWithToday.append("\n                                                        OR lp.VALUE_15 IS NULL                ");
        sbQueryAllPeriodWithToday.append("\n                                                        OR lp.VALUE_16 IS NULL                ");
        sbQueryAllPeriodWithToday.append("\n                                                        OR lp.VALUE_17 IS NULL                ");
        sbQueryAllPeriodWithToday.append("\n                                                        OR lp.VALUE_18 IS NULL                ");
        sbQueryAllPeriodWithToday.append("\n                                                        OR lp.VALUE_19 IS NULL                ");
        sbQueryAllPeriodWithToday.append("\n                                                        OR lp.VALUE_20 IS NULL                ");
        sbQueryAllPeriodWithToday.append("\n                                                        OR lp.VALUE_21 IS NULL                ");
        sbQueryAllPeriodWithToday.append("\n                                                        OR lp.VALUE_22 IS NULL                ");
        sbQueryAllPeriodWithToday.append("\n                                                        OR lp.VALUE_23 IS NULL                ");
        sbQueryAllPeriodWithToday.append("\n                                                        OR lp.VALUE_24 IS NULL                ");
        sbQueryAllPeriodWithToday.append("\n                                                        OR lp.VALUE_25 IS NULL                ");
        sbQueryAllPeriodWithToday.append("\n                                                        OR lp.VALUE_26 IS NULL                ");
        sbQueryAllPeriodWithToday.append("\n                                                        OR lp.VALUE_27 IS NULL                ");
        sbQueryAllPeriodWithToday.append("\n                                                        OR lp.VALUE_28 IS NULL                ");
        sbQueryAllPeriodWithToday.append("\n                                                        OR lp.VALUE_29 IS NULL                ");
        sbQueryAllPeriodWithToday.append("\n                                                        OR lp.VALUE_30 IS NULL                ");
        sbQueryAllPeriodWithToday.append("\n                                                        OR lp.VALUE_31 IS NULL                ");
        sbQueryAllPeriodWithToday.append("\n                                                        OR lp.VALUE_32 IS NULL                ");
        sbQueryAllPeriodWithToday.append("\n                                                        OR lp.VALUE_33 IS NULL                ");
        sbQueryAllPeriodWithToday.append("\n                                                        OR lp.VALUE_34 IS NULL                ");
        sbQueryAllPeriodWithToday.append("\n                                                        OR lp.VALUE_35 IS NULL                ");
        sbQueryAllPeriodWithToday.append("\n                                                        OR lp.VALUE_36 IS NULL                ");
        sbQueryAllPeriodWithToday.append("\n                                                        OR lp.VALUE_37 IS NULL                ");
        sbQueryAllPeriodWithToday.append("\n                                                        OR lp.VALUE_38 IS NULL                ");
        sbQueryAllPeriodWithToday.append("\n                                                        OR lp.VALUE_39 IS NULL                ");
        sbQueryAllPeriodWithToday.append("\n                                                        OR lp.VALUE_40 IS NULL                ");
        sbQueryAllPeriodWithToday.append("\n                                                        OR lp.VALUE_41 IS NULL                ");
        sbQueryAllPeriodWithToday.append("\n                                                        OR lp.VALUE_42 IS NULL                ");
        sbQueryAllPeriodWithToday.append("\n                                                        OR lp.VALUE_43 IS NULL                ");
        sbQueryAllPeriodWithToday.append("\n                                                        OR lp.VALUE_44 IS NULL                ");
        sbQueryAllPeriodWithToday.append("\n                                                        OR lp.VALUE_45 IS NULL                ");
        sbQueryAllPeriodWithToday.append("\n                                                        OR lp.VALUE_46 IS NULL                ");
        sbQueryAllPeriodWithToday.append("\n                                                        OR lp.VALUE_47 IS NULL                ");
        sbQueryAllPeriodWithToday.append("\n                                                        OR lp.VALUE_48 IS NULL                ");
        sbQueryAllPeriodWithToday.append("\n                                                        OR lp.VALUE_49 IS NULL                ");
        sbQueryAllPeriodWithToday.append("\n                                                        OR lp.VALUE_50 IS NULL                ");
        sbQueryAllPeriodWithToday.append("\n                                                        OR lp.VALUE_51 IS NULL                ");
        sbQueryAllPeriodWithToday.append("\n                                                        OR lp.VALUE_52 IS NULL                ");
        sbQueryAllPeriodWithToday.append("\n                                                        OR lp.VALUE_53 IS NULL                ");
        sbQueryAllPeriodWithToday.append("\n                                                        OR lp.VALUE_54 IS NULL                ");
        sbQueryAllPeriodWithToday.append("\n                                                        OR lp.VALUE_55 IS NULL                ");
        sbQueryAllPeriodWithToday.append("\n                                                        OR lp.VALUE_56 IS NULL                ");
        sbQueryAllPeriodWithToday.append("\n                                                        OR lp.VALUE_57 IS NULL                ");
        sbQueryAllPeriodWithToday.append("\n                                                        OR lp.VALUE_58 IS NULL                ");
        sbQueryAllPeriodWithToday.append("\n                                                        OR lp.VALUE_59 IS NULL                ");
        sbQueryAllPeriodWithToday.append("\n                                                      THEN 0 ELSE 1 end                       ");
        sbQueryAllPeriodWithToday.append("\n                   WHEN m.lp_interval = 5 THEN CASE WHEN lp.VALUE_00 IS NULL                ");
        sbQueryAllPeriodWithToday.append("\n                                                        OR lp.VALUE_05 IS NULL                ");
        sbQueryAllPeriodWithToday.append("\n                                                        OR lp.VALUE_10 IS NULL                ");
        sbQueryAllPeriodWithToday.append("\n                                                        OR lp.VALUE_15 IS NULL                ");
        sbQueryAllPeriodWithToday.append("\n                                                        OR lp.VALUE_20 IS NULL                ");
        sbQueryAllPeriodWithToday.append("\n                                                        OR lp.VALUE_25 IS NULL                ");
        sbQueryAllPeriodWithToday.append("\n                                                        OR lp.VALUE_30 IS NULL                ");
        sbQueryAllPeriodWithToday.append("\n                                                        OR lp.VALUE_35 IS NULL                ");
        sbQueryAllPeriodWithToday.append("\n                                                        OR lp.VALUE_40 IS NULL                ");
        sbQueryAllPeriodWithToday.append("\n                                                        OR lp.VALUE_45 IS NULL                ");
        sbQueryAllPeriodWithToday.append("\n                                                        OR lp.VALUE_50 IS NULL                ");
        sbQueryAllPeriodWithToday.append("\n                                                        OR lp.VALUE_55 IS NULL                ");
        sbQueryAllPeriodWithToday.append("\n                                                      THEN 0 ELSE 1 end                       ");
        sbQueryAllPeriodWithToday.append("\n                   WHEN m.lp_interval = 10 THEN CASE WHEN lp.VALUE_00 IS NULL               ");
        sbQueryAllPeriodWithToday.append("\n                                                     OR lp.VALUE_10 IS NULL               ");
        sbQueryAllPeriodWithToday.append("\n                                                     OR lp.VALUE_20 IS NULL               ");
        sbQueryAllPeriodWithToday.append("\n                                                     OR lp.VALUE_30 IS NULL               ");
        sbQueryAllPeriodWithToday.append("\n                                                     OR lp.VALUE_40 IS NULL               ");
        sbQueryAllPeriodWithToday.append("\n                                                     OR lp.VALUE_50 IS NULL               ");
        sbQueryAllPeriodWithToday.append("\n                                                   THEN 0 ELSE 1 end                      ");
        sbQueryAllPeriodWithToday.append("\n                   WHEN m.lp_interval = 15 THEN CASE WHEN lp.VALUE_00 IS NULL               ");
        sbQueryAllPeriodWithToday.append("\n                                                     OR lp.VALUE_15 IS NULL               ");
        sbQueryAllPeriodWithToday.append("\n                                                     OR lp.VALUE_30 IS NULL               ");
        sbQueryAllPeriodWithToday.append("\n                                                     OR lp.VALUE_45 IS NULL               ");
        sbQueryAllPeriodWithToday.append("\n                                                   THEN 0 ELSE 1 end                      ");
        sbQueryAllPeriodWithToday.append("\n                   WHEN m.lp_interval = 30 THEN CASE WHEN lp.VALUE_00 IS NULL               ");
        sbQueryAllPeriodWithToday.append("\n                                                     OR lp.VALUE_30 IS NULL               ");
        sbQueryAllPeriodWithToday.append("\n                                                   THEN 1 ELSE 0 end                      ");
        sbQueryAllPeriodWithToday.append("\n                   WHEN m.lp_interval = 60 THEN CASE WHEN lp.VALUE_00 IS NULL               ");
        sbQueryAllPeriodWithToday.append("\n                                                     THEN 0 ELSE 1 end                      ");
        sbQueryAllPeriodWithToday.append("\n                   ELSE 0 end                                                               ");
        sbQueryAllPeriodWithToday.append("\n                GROUP BY m.ID,lp.YYYYMMDD                                                         ");
        sbQueryAllPeriodWithToday.append("\n            HAVING COUNT(m.ID) = 24                                                               ");
        sbQueryAllPeriodWithToday.append("\n        )x                                                                                      ");
        sbQueryAllPeriodWithToday.append("\n        WHERE x.YYYYMMDD IS NOT NULL                                                            ");
        sbQueryAllPeriodWithToday.append("\n        GROUP BY x.ID                                                                           ");
        sbQueryAllPeriodWithToday.append("\n        HAVING COUNT(x.ID) = :period                                                                 ");
        
        sbQueryAllPeriodWithToday.append("\n        INTERSECT                                                                                  ");
        
        sbQueryAllPeriodWithToday.append("\n        SELECT m.ID                                                                           ");
        sbQueryAllPeriodWithToday.append("\n        FROM METER m LEFT OUTER JOIN ").append(lpTable).append(" lp ON lp.METER_ID = m.ID                           ");
        sbQueryAllPeriodWithToday.append("\n        AND lp.YYYYMMDD = :currDate                                     ");
        sbQueryAllPeriodWithToday.append("\n        AND lp.HH < :currHour                                     ");
        sbQueryAllPeriodWithToday.append("\n        AND lp.CHANNEL = :channel                                                                    ");
        sbQueryAllPeriodWithToday.append("\n        WHERE 1=1                                                                             ");
        sbQueryAllPeriodWithToday.append("\n        AND m.INSTALL_DATE <= :installDate                                                ");
        sbQueryAllPeriodWithToday.append("\n        AND 1 = CASE WHEN m.LP_INTERVAL = 1 THEN CASE WHEN lp.VALUE_00 IS NULL                  ");
        sbQueryAllPeriodWithToday.append("\n                                                              OR lp.VALUE_01 IS NULL                ");
        sbQueryAllPeriodWithToday.append("\n                                                              OR lp.VALUE_02 IS NULL                ");
        sbQueryAllPeriodWithToday.append("\n                                                              OR lp.VALUE_03 IS NULL                ");
        sbQueryAllPeriodWithToday.append("\n                                                              OR lp.VALUE_04 IS NULL                ");
        sbQueryAllPeriodWithToday.append("\n                                                              OR lp.VALUE_05 IS NULL                ");
        sbQueryAllPeriodWithToday.append("\n                                                              OR lp.VALUE_06 IS NULL                ");
        sbQueryAllPeriodWithToday.append("\n                                                              OR lp.VALUE_07 IS NULL                ");
        sbQueryAllPeriodWithToday.append("\n                                                              OR lp.VALUE_08 IS NULL                ");
        sbQueryAllPeriodWithToday.append("\n                                                              OR lp.VALUE_09 IS NULL                ");
        sbQueryAllPeriodWithToday.append("\n                                                              OR lp.VALUE_10 IS NULL                ");
        sbQueryAllPeriodWithToday.append("\n                                                              OR lp.VALUE_11 IS NULL                ");
        sbQueryAllPeriodWithToday.append("\n                                                              OR lp.VALUE_12 IS NULL                ");
        sbQueryAllPeriodWithToday.append("\n                                                              OR lp.VALUE_13 IS NULL                ");
        sbQueryAllPeriodWithToday.append("\n                                                              OR lp.VALUE_14 IS NULL                ");
        sbQueryAllPeriodWithToday.append("\n                                                              OR lp.VALUE_15 IS NULL                ");
        sbQueryAllPeriodWithToday.append("\n                                                              OR lp.VALUE_16 IS NULL                ");
        sbQueryAllPeriodWithToday.append("\n                                                              OR lp.VALUE_17 IS NULL                ");
        sbQueryAllPeriodWithToday.append("\n                                                              OR lp.VALUE_18 IS NULL                ");
        sbQueryAllPeriodWithToday.append("\n                                                              OR lp.VALUE_19 IS NULL                ");
        sbQueryAllPeriodWithToday.append("\n                                                              OR lp.VALUE_20 IS NULL                ");
        sbQueryAllPeriodWithToday.append("\n                                                              OR lp.VALUE_21 IS NULL                ");
        sbQueryAllPeriodWithToday.append("\n                                                              OR lp.VALUE_22 IS NULL                ");
        sbQueryAllPeriodWithToday.append("\n                                                              OR lp.VALUE_23 IS NULL                ");
        sbQueryAllPeriodWithToday.append("\n                                                              OR lp.VALUE_24 IS NULL                ");
        sbQueryAllPeriodWithToday.append("\n                                                              OR lp.VALUE_25 IS NULL                ");
        sbQueryAllPeriodWithToday.append("\n                                                              OR lp.VALUE_26 IS NULL                ");
        sbQueryAllPeriodWithToday.append("\n                                                              OR lp.VALUE_27 IS NULL                ");
        sbQueryAllPeriodWithToday.append("\n                                                              OR lp.VALUE_28 IS NULL                ");
        sbQueryAllPeriodWithToday.append("\n                                                              OR lp.VALUE_29 IS NULL                ");
        sbQueryAllPeriodWithToday.append("\n                                                              OR lp.VALUE_30 IS NULL                ");
        sbQueryAllPeriodWithToday.append("\n                                                              OR lp.VALUE_31 IS NULL                ");
        sbQueryAllPeriodWithToday.append("\n                                                              OR lp.VALUE_32 IS NULL                ");
        sbQueryAllPeriodWithToday.append("\n                                                              OR lp.VALUE_33 IS NULL                ");
        sbQueryAllPeriodWithToday.append("\n                                                              OR lp.VALUE_34 IS NULL                ");
        sbQueryAllPeriodWithToday.append("\n                                                              OR lp.VALUE_35 IS NULL                ");
        sbQueryAllPeriodWithToday.append("\n                                                              OR lp.VALUE_36 IS NULL                ");
        sbQueryAllPeriodWithToday.append("\n                                                              OR lp.VALUE_37 IS NULL                ");
        sbQueryAllPeriodWithToday.append("\n                                                              OR lp.VALUE_38 IS NULL                ");
        sbQueryAllPeriodWithToday.append("\n                                                              OR lp.VALUE_39 IS NULL                ");
        sbQueryAllPeriodWithToday.append("\n                                                              OR lp.VALUE_40 IS NULL                ");
        sbQueryAllPeriodWithToday.append("\n                                                              OR lp.VALUE_41 IS NULL                ");
        sbQueryAllPeriodWithToday.append("\n                                                              OR lp.VALUE_42 IS NULL                ");
        sbQueryAllPeriodWithToday.append("\n                                                              OR lp.VALUE_43 IS NULL                ");
        sbQueryAllPeriodWithToday.append("\n                                                              OR lp.VALUE_44 IS NULL                ");
        sbQueryAllPeriodWithToday.append("\n                                                              OR lp.VALUE_45 IS NULL                ");
        sbQueryAllPeriodWithToday.append("\n                                                              OR lp.VALUE_46 IS NULL                ");
        sbQueryAllPeriodWithToday.append("\n                                                              OR lp.VALUE_47 IS NULL                ");
        sbQueryAllPeriodWithToday.append("\n                                                              OR lp.VALUE_48 IS NULL                ");
        sbQueryAllPeriodWithToday.append("\n                                                              OR lp.VALUE_49 IS NULL                ");
        sbQueryAllPeriodWithToday.append("\n                                                              OR lp.VALUE_50 IS NULL                ");
        sbQueryAllPeriodWithToday.append("\n                                                              OR lp.VALUE_51 IS NULL                ");
        sbQueryAllPeriodWithToday.append("\n                                                              OR lp.VALUE_52 IS NULL                ");
        sbQueryAllPeriodWithToday.append("\n                                                              OR lp.VALUE_53 IS NULL                ");
        sbQueryAllPeriodWithToday.append("\n                                                              OR lp.VALUE_54 IS NULL                ");
        sbQueryAllPeriodWithToday.append("\n                                                              OR lp.VALUE_55 IS NULL                ");
        sbQueryAllPeriodWithToday.append("\n                                                              OR lp.VALUE_56 IS NULL                ");
        sbQueryAllPeriodWithToday.append("\n                                                              OR lp.VALUE_57 IS NULL                ");
        sbQueryAllPeriodWithToday.append("\n                                                              OR lp.VALUE_58 IS NULL                ");
        sbQueryAllPeriodWithToday.append("\n                                                              OR lp.VALUE_59 IS NULL                ");
        sbQueryAllPeriodWithToday.append("\n                                                            THEN 0 ELSE 1 end                       ");
        sbQueryAllPeriodWithToday.append("\n                   WHEN m.lp_interval = 5 THEN CASE WHEN lp.VALUE_00 IS NULL                ");
        sbQueryAllPeriodWithToday.append("\n                                                                OR lp.VALUE_05 IS NULL                ");
        sbQueryAllPeriodWithToday.append("\n                                                                OR lp.VALUE_10 IS NULL                ");
        sbQueryAllPeriodWithToday.append("\n                                                                OR lp.VALUE_15 IS NULL                ");
        sbQueryAllPeriodWithToday.append("\n                                                                OR lp.VALUE_20 IS NULL                ");
        sbQueryAllPeriodWithToday.append("\n                                                                OR lp.VALUE_25 IS NULL                ");
        sbQueryAllPeriodWithToday.append("\n                                                                OR lp.VALUE_30 IS NULL                ");
        sbQueryAllPeriodWithToday.append("\n                                                                OR lp.VALUE_35 IS NULL                ");
        sbQueryAllPeriodWithToday.append("\n                                                                OR lp.VALUE_40 IS NULL                ");
        sbQueryAllPeriodWithToday.append("\n                                                                OR lp.VALUE_45 IS NULL                ");
        sbQueryAllPeriodWithToday.append("\n                                                                OR lp.VALUE_50 IS NULL                ");
        sbQueryAllPeriodWithToday.append("\n                                                                OR lp.VALUE_55 IS NULL                ");
        sbQueryAllPeriodWithToday.append("\n                                                              THEN 0 ELSE 1 end                       ");
        sbQueryAllPeriodWithToday.append("\n                   WHEN m.lp_interval = 10 THEN CASE WHEN lp.VALUE_00 IS NULL               ");
        sbQueryAllPeriodWithToday.append("\n                                                               OR lp.VALUE_10 IS NULL               ");
        sbQueryAllPeriodWithToday.append("\n                                                               OR lp.VALUE_20 IS NULL               ");
        sbQueryAllPeriodWithToday.append("\n                                                               OR lp.VALUE_30 IS NULL               ");
        sbQueryAllPeriodWithToday.append("\n                                                               OR lp.VALUE_40 IS NULL               ");
        sbQueryAllPeriodWithToday.append("\n                                                               OR lp.VALUE_50 IS NULL               ");
        sbQueryAllPeriodWithToday.append("\n                                                             THEN 0 ELSE 1 end                      ");
        sbQueryAllPeriodWithToday.append("\n                   WHEN m.lp_interval = 15 THEN CASE WHEN lp.VALUE_00 IS NULL               ");
        sbQueryAllPeriodWithToday.append("\n                                                               OR lp.VALUE_15 IS NULL               ");
        sbQueryAllPeriodWithToday.append("\n                                                               OR lp.VALUE_30 IS NULL               ");
        sbQueryAllPeriodWithToday.append("\n                                                               OR lp.VALUE_45 IS NULL               ");
        sbQueryAllPeriodWithToday.append("\n                                                             THEN 0 ELSE 1 end                      ");
        sbQueryAllPeriodWithToday.append("\n                   WHEN m.lp_interval = 30 THEN CASE WHEN lp.VALUE_00 IS NULL               ");
        sbQueryAllPeriodWithToday.append("\n                                                           OR lp.VALUE_30 IS NULL               ");
        sbQueryAllPeriodWithToday.append("\n                                                         THEN 1 ELSE 0 end                      ");
        sbQueryAllPeriodWithToday.append("\n                   WHEN m.lp_interval = 60 THEN CASE WHEN lp.VALUE_00 IS NULL               ");
        sbQueryAllPeriodWithToday.append("\n                                                     THEN 0 ELSE 1 end                      ");
        sbQueryAllPeriodWithToday.append("\n                   ELSE 0 end                                                               ");
        sbQueryAllPeriodWithToday.append("\n                GROUP BY m.ID,lp.YYYYMMDD                                                         ");
        sbQueryAllPeriodWithToday.append("\n            HAVING COUNT(m.ID) = :currHour                                                               ");
        
        sbQueryAllPeriodWithToday.append("\n          INTERSECT                                                                                   ");
        
        sbQueryAllPeriodWithToday.append("\n            SELECT m.ID                                                                           ");
        sbQueryAllPeriodWithToday.append("\n            FROM METER m LEFT OUTER JOIN ").append(lpTable).append(" lp ON lp.METER_ID = m.ID                           ");
        sbQueryAllPeriodWithToday.append("\n            AND lp.YYYYMMDD = :currDate                                     ");
        sbQueryAllPeriodWithToday.append("\n            AND lp.HH = :currHour                                     ");
        sbQueryAllPeriodWithToday.append("\n            AND lp.CHANNEL = :channel                                                                    ");
        sbQueryAllPeriodWithToday.append("\n            WHERE 1=1                                                                             ");
        sbQueryAllPeriodWithToday.append("\n            AND m.INSTALL_DATE <= :installDate                                                ");
        sbQueryAllPeriodWithToday.append("\n            AND 1 = CASE WHEN m.LP_INTERVAL = 1 THEN CASE WHEN lp.VALUE_00 IS NULL                  ");
        if(currMinute >= 1)sbQueryAllPeriodWithToday.append("\n                                                               OR lp.VALUE_01 IS NULL                ");
        if(currMinute >= 2)sbQueryAllPeriodWithToday.append("\n                                                               OR lp.VALUE_02 IS NULL                ");
        if(currMinute >= 3)sbQueryAllPeriodWithToday.append("\n                                                               OR lp.VALUE_03 IS NULL                ");
        if(currMinute >= 4)sbQueryAllPeriodWithToday.append("\n                                                               OR lp.VALUE_04 IS NULL                ");
        if(currMinute >= 5)sbQueryAllPeriodWithToday.append("\n                                                               OR lp.VALUE_05 IS NULL                ");
        if(currMinute >= 6)sbQueryAllPeriodWithToday.append("\n                                                               OR lp.VALUE_06 IS NULL                ");
        if(currMinute >= 7)sbQueryAllPeriodWithToday.append("\n                                                               OR lp.VALUE_07 IS NULL                ");
        if(currMinute >= 8)sbQueryAllPeriodWithToday.append("\n                                                               OR lp.VALUE_08 IS NULL                ");
        if(currMinute >= 9)sbQueryAllPeriodWithToday.append("\n                                                               OR lp.VALUE_09 IS NULL                ");
        if(currMinute >= 10)sbQueryAllPeriodWithToday.append("\n                                                              OR lp.VALUE_10 IS NULL                ");
        if(currMinute >= 11)sbQueryAllPeriodWithToday.append("\n                                                              OR lp.VALUE_11 IS NULL                ");
        if(currMinute >= 12)sbQueryAllPeriodWithToday.append("\n                                                              OR lp.VALUE_12 IS NULL                ");
        if(currMinute >= 13)sbQueryAllPeriodWithToday.append("\n                                                              OR lp.VALUE_13 IS NULL                ");
        if(currMinute >= 14)sbQueryAllPeriodWithToday.append("\n                                                              OR lp.VALUE_14 IS NULL                ");
        if(currMinute >= 15)sbQueryAllPeriodWithToday.append("\n                                                              OR lp.VALUE_15 IS NULL                ");
        if(currMinute >= 16)sbQueryAllPeriodWithToday.append("\n                                                              OR lp.VALUE_16 IS NULL                ");
        if(currMinute >= 17)sbQueryAllPeriodWithToday.append("\n                                                              OR lp.VALUE_17 IS NULL                ");
        if(currMinute >= 18)sbQueryAllPeriodWithToday.append("\n                                                              OR lp.VALUE_18 IS NULL                ");
        if(currMinute >= 19)sbQueryAllPeriodWithToday.append("\n                                                              OR lp.VALUE_19 IS NULL                ");
        if(currMinute >= 20)sbQueryAllPeriodWithToday.append("\n                                                              OR lp.VALUE_20 IS NULL                ");
        if(currMinute >= 21)sbQueryAllPeriodWithToday.append("\n                                                              OR lp.VALUE_21 IS NULL                ");
        if(currMinute >= 22)sbQueryAllPeriodWithToday.append("\n                                                              OR lp.VALUE_22 IS NULL                ");
        if(currMinute >= 23)sbQueryAllPeriodWithToday.append("\n                                                              OR lp.VALUE_23 IS NULL                ");
        if(currMinute >= 24)sbQueryAllPeriodWithToday.append("\n                                                              OR lp.VALUE_24 IS NULL                ");
        if(currMinute >= 25)sbQueryAllPeriodWithToday.append("\n                                                              OR lp.VALUE_25 IS NULL                ");
        if(currMinute >= 26)sbQueryAllPeriodWithToday.append("\n                                                              OR lp.VALUE_26 IS NULL                ");
        if(currMinute >= 27)sbQueryAllPeriodWithToday.append("\n                                                              OR lp.VALUE_27 IS NULL                ");
        if(currMinute >= 28)sbQueryAllPeriodWithToday.append("\n                                                              OR lp.VALUE_28 IS NULL                ");
        if(currMinute >= 29)sbQueryAllPeriodWithToday.append("\n                                                              OR lp.VALUE_29 IS NULL                ");
        if(currMinute >= 30)sbQueryAllPeriodWithToday.append("\n                                                              OR lp.VALUE_30 IS NULL                ");
        if(currMinute >= 31)sbQueryAllPeriodWithToday.append("\n                                                              OR lp.VALUE_31 IS NULL                ");
        if(currMinute >= 32)sbQueryAllPeriodWithToday.append("\n                                                              OR lp.VALUE_32 IS NULL                ");
        if(currMinute >= 33)sbQueryAllPeriodWithToday.append("\n                                                              OR lp.VALUE_33 IS NULL                ");
        if(currMinute >= 34)sbQueryAllPeriodWithToday.append("\n                                                              OR lp.VALUE_34 IS NULL                ");
        if(currMinute >= 35)sbQueryAllPeriodWithToday.append("\n                                                              OR lp.VALUE_35 IS NULL                ");
        if(currMinute >= 36)sbQueryAllPeriodWithToday.append("\n                                                              OR lp.VALUE_36 IS NULL                ");
        if(currMinute >= 37)sbQueryAllPeriodWithToday.append("\n                                                              OR lp.VALUE_37 IS NULL                ");
        if(currMinute >= 38)sbQueryAllPeriodWithToday.append("\n                                                              OR lp.VALUE_38 IS NULL                ");
        if(currMinute >= 39)sbQueryAllPeriodWithToday.append("\n                                                              OR lp.VALUE_39 IS NULL                ");
        if(currMinute >= 40)sbQueryAllPeriodWithToday.append("\n                                                              OR lp.VALUE_40 IS NULL                ");
        if(currMinute >= 41)sbQueryAllPeriodWithToday.append("\n                                                              OR lp.VALUE_41 IS NULL                ");
        if(currMinute >= 42)sbQueryAllPeriodWithToday.append("\n                                                              OR lp.VALUE_42 IS NULL                ");
        if(currMinute >= 43)sbQueryAllPeriodWithToday.append("\n                                                              OR lp.VALUE_43 IS NULL                ");
        if(currMinute >= 44)sbQueryAllPeriodWithToday.append("\n                                                              OR lp.VALUE_44 IS NULL                ");
        if(currMinute >= 45)sbQueryAllPeriodWithToday.append("\n                                                              OR lp.VALUE_45 IS NULL                ");
        if(currMinute >= 46)sbQueryAllPeriodWithToday.append("\n                                                              OR lp.VALUE_46 IS NULL                ");
        if(currMinute >= 47)sbQueryAllPeriodWithToday.append("\n                                                              OR lp.VALUE_47 IS NULL                ");
        if(currMinute >= 48)sbQueryAllPeriodWithToday.append("\n                                                              OR lp.VALUE_48 IS NULL                ");
        if(currMinute >= 49)sbQueryAllPeriodWithToday.append("\n                                                              OR lp.VALUE_49 IS NULL                ");
        if(currMinute >= 50)sbQueryAllPeriodWithToday.append("\n                                                              OR lp.VALUE_50 IS NULL                ");
        if(currMinute >= 51)sbQueryAllPeriodWithToday.append("\n                                                              OR lp.VALUE_51 IS NULL                ");
        if(currMinute >= 52)sbQueryAllPeriodWithToday.append("\n                                                              OR lp.VALUE_52 IS NULL                ");
        if(currMinute >= 53)sbQueryAllPeriodWithToday.append("\n                                                              OR lp.VALUE_53 IS NULL                ");
        if(currMinute >= 54)sbQueryAllPeriodWithToday.append("\n                                                              OR lp.VALUE_54 IS NULL                ");
        if(currMinute >= 55)sbQueryAllPeriodWithToday.append("\n                                                              OR lp.VALUE_55 IS NULL                ");
        if(currMinute >= 56)sbQueryAllPeriodWithToday.append("\n                                                              OR lp.VALUE_56 IS NULL                ");
        if(currMinute >= 57)sbQueryAllPeriodWithToday.append("\n                                                              OR lp.VALUE_57 IS NULL                ");
        if(currMinute >= 58)sbQueryAllPeriodWithToday.append("\n                                                              OR lp.VALUE_58 IS NULL                ");
        if(currMinute >= 59)sbQueryAllPeriodWithToday.append("\n                                                              OR lp.VALUE_59 IS NULL                ");
        sbQueryAllPeriodWithToday.append("\n                                                            THEN 0 ELSE 1 end                       ");
        sbQueryAllPeriodWithToday.append("\n                   WHEN m.lp_interval = 5 THEN CASE WHEN lp.VALUE_00 IS NULL                ");
        if(currMinute >= 5)sbQueryAllPeriodWithToday.append("\n                                                                 OR lp.VALUE_05 IS NULL                ");
        if(currMinute >= 10)sbQueryAllPeriodWithToday.append("\n                                                                OR lp.VALUE_10 IS NULL                ");
        if(currMinute >= 15)sbQueryAllPeriodWithToday.append("\n                                                                OR lp.VALUE_15 IS NULL                ");
        if(currMinute >= 20)sbQueryAllPeriodWithToday.append("\n                                                                OR lp.VALUE_20 IS NULL                ");
        if(currMinute >= 25)sbQueryAllPeriodWithToday.append("\n                                                                OR lp.VALUE_25 IS NULL                ");
        if(currMinute >= 30)sbQueryAllPeriodWithToday.append("\n                                                                OR lp.VALUE_30 IS NULL                ");
        if(currMinute >= 35)sbQueryAllPeriodWithToday.append("\n                                                                OR lp.VALUE_35 IS NULL                ");
        if(currMinute >= 40)sbQueryAllPeriodWithToday.append("\n                                                                OR lp.VALUE_40 IS NULL                ");
        if(currMinute >= 45)sbQueryAllPeriodWithToday.append("\n                                                                OR lp.VALUE_45 IS NULL                ");
        if(currMinute >= 50)sbQueryAllPeriodWithToday.append("\n                                                                OR lp.VALUE_50 IS NULL                ");
        if(currMinute >= 55)sbQueryAllPeriodWithToday.append("\n                                                                OR lp.VALUE_55 IS NULL                ");
        sbQueryAllPeriodWithToday.append("\n                                                              THEN 0 ELSE 1 end                       ");
        sbQueryAllPeriodWithToday.append("\n                   WHEN m.lp_interval = 10 THEN CASE WHEN lp.VALUE_00 IS NULL               ");
        if(currMinute >= 10)sbQueryAllPeriodWithToday.append("\n                                                               OR lp.VALUE_10 IS NULL               ");
        if(currMinute >= 20)sbQueryAllPeriodWithToday.append("\n                                                               OR lp.VALUE_20 IS NULL               ");
        if(currMinute >= 30)sbQueryAllPeriodWithToday.append("\n                                                               OR lp.VALUE_30 IS NULL               ");
        if(currMinute >= 40)sbQueryAllPeriodWithToday.append("\n                                                               OR lp.VALUE_40 IS NULL               ");
        if(currMinute >= 50)sbQueryAllPeriodWithToday.append("\n                                                               OR lp.VALUE_50 IS NULL               ");
        sbQueryAllPeriodWithToday.append("\n                                                             THEN 0 ELSE 1 end                      ");
        sbQueryAllPeriodWithToday.append("\n                   WHEN m.lp_interval = 15 THEN CASE WHEN lp.VALUE_00 IS NULL               ");
        if(currMinute >= 15)sbQueryAllPeriodWithToday.append("\n                                                               OR lp.VALUE_15 IS NULL               ");
        if(currMinute >= 30)sbQueryAllPeriodWithToday.append("\n                                                               OR lp.VALUE_30 IS NULL               ");
        if(currMinute >= 45)sbQueryAllPeriodWithToday.append("\n                                                               OR lp.VALUE_45 IS NULL               ");
        sbQueryAllPeriodWithToday.append("\n                                                             THEN 0 ELSE 1 end                      ");
        sbQueryAllPeriodWithToday.append("\n                   WHEN m.lp_interval = 30 THEN CASE WHEN lp.VALUE_00 IS NULL               ");
        if(currMinute >= 30)sbQueryAllPeriodWithToday.append("\n                                                           OR lp.VALUE_30 IS NULL               ");
        sbQueryAllPeriodWithToday.append("\n                                                         THEN 1 ELSE 0 end                      ");
        sbQueryAllPeriodWithToday.append("\n                   WHEN m.lp_interval = 60 THEN CASE WHEN lp.VALUE_00 IS NULL               ");
        sbQueryAllPeriodWithToday.append("\n                                                     THEN 0 ELSE 1 end                      ");
        sbQueryAllPeriodWithToday.append("\n                   ELSE 0 end                                                                   ");
        sbQueryAllPeriodWithToday.append("\n            )                                                                               ");
        
    
        StringBuffer sbQueryToday = new StringBuffer();
        sbQueryToday.append("\n SELECT customer.NAME                                                                       ");
        sbQueryToday.append("\n        ,modem.DEVICE_SERIAL                                                                ");
        sbQueryToday.append("\n        ,mcu.SYS_NAME as mcuName                                                                          ");
        sbQueryToday.append("\n        ,m1.MDS_ID                                                                          ");
        sbQueryToday.append("\n        ,m1.LAST_READ_DATE                                                                  ");
        sbQueryToday.append("\n        ,m1.ID                                                                  ");
        sbQueryToday.append("\n        ,m1.LP_INTERVAL                                                                  ");
        sbQueryToday.append("\n FROM METER m1 LEFT OUTER JOIN CONTRACT contract ON m1.ID = contract.METER_ID          ");
        sbQueryToday.append("\n                  LEFT OUTER JOIN CUSTOMER customer ON contract.CUSTOMER_ID = customer.ID             ");
        sbQueryToday.append("\n                  LEFT OUTER JOIN MODEM modem ON m1.MODEM_ID = modem.ID                               ");
        sbQueryToday.append("\n                  LEFT OUTER JOIN MCU mcu ON modem.MCU_ID = mcu.ID                                    ");
        sbQueryToday.append("\n WHERE 1=1                                                                                  ");
        sbQueryToday.append("\n AND m1.INSTALL_DATE <= :installDate                                                    ");
        sbQueryToday.append("\n AND m1.METER = :meterType                                                                   ");
        if(!"".equals(supplierId)){
            sbQueryToday.append("\n AND m1.SUPPLIER_ID = :supplierId ");
        }
        if(!"".equals(mdsId)){
            sbQueryToday.append("\n AND m1.MDS_ID LIKE :mdsId ");
        }
        //deviceId , deviceType 의  null 값 체크
        if(!"".equals(deviceId) && !"".equals(deviceType)){
            if(CommonConstants.DeviceType.MCU.getCode().equals(Integer.parseInt(deviceType))){
                sbQueryToday.append("\n AND mcu.SYS_NAME LIKE :deviceId ");
            }else{
                sbQueryToday.append("\n AND modem.DEVICE_SERIAL LIKE :deviceId ");
            }
        }
        sbQueryToday.append("\n AND m1.ID NOT IN (                                                                         ");
        sbQueryToday.append("\n         SELECT m.ID                                                                           ");
        sbQueryToday.append("\n         FROM METER m LEFT OUTER JOIN ").append(lpTable).append(" lp ON lp.METER_ID = m.ID                           ");
        sbQueryToday.append("\n         AND lp.YYYYMMDD = :currDate                                     ");
        sbQueryToday.append("\n         AND lp.HH < :currHour                                     ");
        sbQueryToday.append("\n         AND lp.CHANNEL = :channel                                                                    ");
        sbQueryToday.append("\n         WHERE 1=1                                                                             ");
        sbQueryToday.append("\n         AND m.INSTALL_DATE <= :installDate                                                ");
        sbQueryToday.append("\n         AND 1 = CASE WHEN m.LP_INTERVAL = 1 THEN CASE WHEN lp.VALUE_00 IS NULL                  ");
        sbQueryToday.append("\n                                                               OR lp.VALUE_01 IS NULL                ");
        sbQueryToday.append("\n                                                               OR lp.VALUE_02 IS NULL                ");
        sbQueryToday.append("\n                                                               OR lp.VALUE_03 IS NULL                ");
        sbQueryToday.append("\n                                                               OR lp.VALUE_04 IS NULL                ");
        sbQueryToday.append("\n                                                               OR lp.VALUE_05 IS NULL                ");
        sbQueryToday.append("\n                                                               OR lp.VALUE_06 IS NULL                ");
        sbQueryToday.append("\n                                                               OR lp.VALUE_07 IS NULL                ");
        sbQueryToday.append("\n                                                               OR lp.VALUE_08 IS NULL                ");
        sbQueryToday.append("\n                                                               OR lp.VALUE_09 IS NULL                ");
        sbQueryToday.append("\n                                                               OR lp.VALUE_10 IS NULL                ");
        sbQueryToday.append("\n                                                               OR lp.VALUE_11 IS NULL                ");
        sbQueryToday.append("\n                                                               OR lp.VALUE_12 IS NULL                ");
        sbQueryToday.append("\n                                                               OR lp.VALUE_13 IS NULL                ");
        sbQueryToday.append("\n                                                               OR lp.VALUE_14 IS NULL                ");
        sbQueryToday.append("\n                                                               OR lp.VALUE_15 IS NULL                ");
        sbQueryToday.append("\n                                                               OR lp.VALUE_16 IS NULL                ");
        sbQueryToday.append("\n                                                               OR lp.VALUE_17 IS NULL                ");
        sbQueryToday.append("\n                                                               OR lp.VALUE_18 IS NULL                ");
        sbQueryToday.append("\n                                                               OR lp.VALUE_19 IS NULL                ");
        sbQueryToday.append("\n                                                               OR lp.VALUE_20 IS NULL                ");
        sbQueryToday.append("\n                                                               OR lp.VALUE_21 IS NULL                ");
        sbQueryToday.append("\n                                                               OR lp.VALUE_22 IS NULL                ");
        sbQueryToday.append("\n                                                               OR lp.VALUE_23 IS NULL                ");
        sbQueryToday.append("\n                                                               OR lp.VALUE_24 IS NULL                ");
        sbQueryToday.append("\n                                                               OR lp.VALUE_25 IS NULL                ");
        sbQueryToday.append("\n                                                               OR lp.VALUE_26 IS NULL                ");
        sbQueryToday.append("\n                                                               OR lp.VALUE_27 IS NULL                ");
        sbQueryToday.append("\n                                                               OR lp.VALUE_28 IS NULL                ");
        sbQueryToday.append("\n                                                               OR lp.VALUE_29 IS NULL                ");
        sbQueryToday.append("\n                                                               OR lp.VALUE_30 IS NULL                ");
        sbQueryToday.append("\n                                                               OR lp.VALUE_31 IS NULL                ");
        sbQueryToday.append("\n                                                               OR lp.VALUE_32 IS NULL                ");
        sbQueryToday.append("\n                                                               OR lp.VALUE_33 IS NULL                ");
        sbQueryToday.append("\n                                                               OR lp.VALUE_34 IS NULL                ");
        sbQueryToday.append("\n                                                               OR lp.VALUE_35 IS NULL                ");
        sbQueryToday.append("\n                                                               OR lp.VALUE_36 IS NULL                ");
        sbQueryToday.append("\n                                                               OR lp.VALUE_37 IS NULL                ");
        sbQueryToday.append("\n                                                               OR lp.VALUE_38 IS NULL                ");
        sbQueryToday.append("\n                                                               OR lp.VALUE_39 IS NULL                ");
        sbQueryToday.append("\n                                                               OR lp.VALUE_40 IS NULL                ");
        sbQueryToday.append("\n                                                               OR lp.VALUE_41 IS NULL                ");
        sbQueryToday.append("\n                                                               OR lp.VALUE_42 IS NULL                ");
        sbQueryToday.append("\n                                                               OR lp.VALUE_43 IS NULL                ");
        sbQueryToday.append("\n                                                               OR lp.VALUE_44 IS NULL                ");
        sbQueryToday.append("\n                                                               OR lp.VALUE_45 IS NULL                ");
        sbQueryToday.append("\n                                                               OR lp.VALUE_46 IS NULL                ");
        sbQueryToday.append("\n                                                               OR lp.VALUE_47 IS NULL                ");
        sbQueryToday.append("\n                                                               OR lp.VALUE_48 IS NULL                ");
        sbQueryToday.append("\n                                                               OR lp.VALUE_49 IS NULL                ");
        sbQueryToday.append("\n                                                               OR lp.VALUE_50 IS NULL                ");
        sbQueryToday.append("\n                                                               OR lp.VALUE_51 IS NULL                ");
        sbQueryToday.append("\n                                                               OR lp.VALUE_52 IS NULL                ");
        sbQueryToday.append("\n                                                               OR lp.VALUE_53 IS NULL                ");
        sbQueryToday.append("\n                                                               OR lp.VALUE_54 IS NULL                ");
        sbQueryToday.append("\n                                                               OR lp.VALUE_55 IS NULL                ");
        sbQueryToday.append("\n                                                               OR lp.VALUE_56 IS NULL                ");
        sbQueryToday.append("\n                                                               OR lp.VALUE_57 IS NULL                ");
        sbQueryToday.append("\n                                                               OR lp.VALUE_58 IS NULL                ");
        sbQueryToday.append("\n                                                               OR lp.VALUE_59 IS NULL                ");
        sbQueryToday.append("\n                                                             THEN 0 ELSE 1 end                       ");
        sbQueryToday.append("\n                   WHEN m.lp_interval = 5 THEN CASE WHEN lp.VALUE_00 IS NULL                ");
        sbQueryToday.append("\n                                                                 OR lp.VALUE_05 IS NULL                ");
        sbQueryToday.append("\n                                                                 OR lp.VALUE_10 IS NULL                ");
        sbQueryToday.append("\n                                                                 OR lp.VALUE_15 IS NULL                ");
        sbQueryToday.append("\n                                                                 OR lp.VALUE_20 IS NULL                ");
        sbQueryToday.append("\n                                                                 OR lp.VALUE_25 IS NULL                ");
        sbQueryToday.append("\n                                                                 OR lp.VALUE_30 IS NULL                ");
        sbQueryToday.append("\n                                                                 OR lp.VALUE_35 IS NULL                ");
        sbQueryToday.append("\n                                                                 OR lp.VALUE_40 IS NULL                ");
        sbQueryToday.append("\n                                                                 OR lp.VALUE_45 IS NULL                ");
        sbQueryToday.append("\n                                                                 OR lp.VALUE_50 IS NULL                ");
        sbQueryToday.append("\n                                                                 OR lp.VALUE_55 IS NULL                ");
        sbQueryToday.append("\n                                                               THEN 0 ELSE 1 end                       ");
        sbQueryToday.append("\n                   WHEN m.lp_interval = 10 THEN CASE WHEN lp.VALUE_00 IS NULL               ");
        sbQueryToday.append("\n                                                                OR lp.VALUE_10 IS NULL               ");
        sbQueryToday.append("\n                                                                OR lp.VALUE_20 IS NULL               ");
        sbQueryToday.append("\n                                                                OR lp.VALUE_30 IS NULL               ");
        sbQueryToday.append("\n                                                                OR lp.VALUE_40 IS NULL               ");
        sbQueryToday.append("\n                                                                OR lp.VALUE_50 IS NULL               ");
        sbQueryToday.append("\n                                                              THEN 0 ELSE 1 end                      ");
        sbQueryToday.append("\n                   WHEN m.lp_interval = 15 THEN CASE WHEN lp.VALUE_00 IS NULL               ");
        sbQueryToday.append("\n                                                                OR lp.VALUE_15 IS NULL               ");
        sbQueryToday.append("\n                                                                OR lp.VALUE_30 IS NULL               ");
        sbQueryToday.append("\n                                                                OR lp.VALUE_45 IS NULL               ");
        sbQueryToday.append("\n                                                              THEN 0 ELSE 1 end                      ");
        sbQueryToday.append("\n                   WHEN m.lp_interval = 30 THEN CASE WHEN lp.VALUE_00 IS NULL               ");
        sbQueryToday.append("\n                                                            OR lp.VALUE_30 IS NULL               ");
        sbQueryToday.append("\n                                                          THEN 1 ELSE 0 end                      ");
        sbQueryToday.append("\n                   WHEN m.lp_interval = 60 THEN CASE WHEN lp.VALUE_00 IS NULL               ");
        sbQueryToday.append("\n                                                     THEN 0 ELSE 1 end                      ");
        sbQueryToday.append("\n                   ELSE 0 end                                                               ");
        sbQueryToday.append("\n                 GROUP BY m.ID,lp.YYYYMMDD                                                         ");
        sbQueryToday.append("\n             HAVING COUNT(m.ID) = :currHour                                                               ");
        
        sbQueryToday.append("\n          INTERSECT                                                                                   ");
        
        sbQueryToday.append("\n             SELECT m.ID                                                                           ");
        sbQueryToday.append("\n             FROM METER m LEFT OUTER JOIN ").append(lpTable).append(" lp ON lp.METER_ID = m.ID                           ");
        sbQueryToday.append("\n             AND lp.YYYYMMDD = :currDate                                     ");
        sbQueryToday.append("\n             AND lp.HH = :currHour                                     ");
        sbQueryToday.append("\n             AND lp.CHANNEL = :channel                                                                    ");
        sbQueryToday.append("\n             WHERE 1=1                                                                             ");
        sbQueryToday.append("\n             AND m.INSTALL_DATE <= :installDate                                                ");
        sbQueryToday.append("\n             AND 1 = CASE WHEN m.LP_INTERVAL = 1 THEN CASE WHEN lp.VALUE_00 IS NULL                  ");
        if(currMinute >= 1)sbQueryToday.append("\n                                                                OR lp.VALUE_01 IS NULL                ");
        if(currMinute >= 2)sbQueryToday.append("\n                                                                OR lp.VALUE_02 IS NULL                ");
        if(currMinute >= 3)sbQueryToday.append("\n                                                                OR lp.VALUE_03 IS NULL                ");
        if(currMinute >= 4)sbQueryToday.append("\n                                                                OR lp.VALUE_04 IS NULL                ");
        if(currMinute >= 5)sbQueryToday.append("\n                                                                OR lp.VALUE_05 IS NULL                ");
        if(currMinute >= 6)sbQueryToday.append("\n                                                                OR lp.VALUE_06 IS NULL                ");
        if(currMinute >= 7)sbQueryToday.append("\n                                                                OR lp.VALUE_07 IS NULL                ");
        if(currMinute >= 8)sbQueryToday.append("\n                                                                OR lp.VALUE_08 IS NULL                ");
        if(currMinute >= 9)sbQueryToday.append("\n                                                                OR lp.VALUE_09 IS NULL                ");
        if(currMinute >= 10)sbQueryToday.append("\n                                                               OR lp.VALUE_10 IS NULL                ");
        if(currMinute >= 11)sbQueryToday.append("\n                                                               OR lp.VALUE_11 IS NULL                ");
        if(currMinute >= 12)sbQueryToday.append("\n                                                               OR lp.VALUE_12 IS NULL                ");
        if(currMinute >= 13)sbQueryToday.append("\n                                                               OR lp.VALUE_13 IS NULL                ");
        if(currMinute >= 14)sbQueryToday.append("\n                                                               OR lp.VALUE_14 IS NULL                ");
        if(currMinute >= 15)sbQueryToday.append("\n                                                               OR lp.VALUE_15 IS NULL                ");
        if(currMinute >= 16)sbQueryToday.append("\n                                                               OR lp.VALUE_16 IS NULL                ");
        if(currMinute >= 17)sbQueryToday.append("\n                                                               OR lp.VALUE_17 IS NULL                ");
        if(currMinute >= 18)sbQueryToday.append("\n                                                               OR lp.VALUE_18 IS NULL                ");
        if(currMinute >= 19)sbQueryToday.append("\n                                                               OR lp.VALUE_19 IS NULL                ");
        if(currMinute >= 20)sbQueryToday.append("\n                                                               OR lp.VALUE_20 IS NULL                ");
        if(currMinute >= 21)sbQueryToday.append("\n                                                               OR lp.VALUE_21 IS NULL                ");
        if(currMinute >= 22)sbQueryToday.append("\n                                                               OR lp.VALUE_22 IS NULL                ");
        if(currMinute >= 23)sbQueryToday.append("\n                                                               OR lp.VALUE_23 IS NULL                ");
        if(currMinute >= 24)sbQueryToday.append("\n                                                               OR lp.VALUE_24 IS NULL                ");
        if(currMinute >= 25)sbQueryToday.append("\n                                                               OR lp.VALUE_25 IS NULL                ");
        if(currMinute >= 26)sbQueryToday.append("\n                                                               OR lp.VALUE_26 IS NULL                ");
        if(currMinute >= 27)sbQueryToday.append("\n                                                               OR lp.VALUE_27 IS NULL                ");
        if(currMinute >= 28)sbQueryToday.append("\n                                                               OR lp.VALUE_28 IS NULL                ");
        if(currMinute >= 29)sbQueryToday.append("\n                                                               OR lp.VALUE_29 IS NULL                ");
        if(currMinute >= 30)sbQueryToday.append("\n                                                               OR lp.VALUE_30 IS NULL                ");
        if(currMinute >= 31)sbQueryToday.append("\n                                                               OR lp.VALUE_31 IS NULL                ");
        if(currMinute >= 32)sbQueryToday.append("\n                                                               OR lp.VALUE_32 IS NULL                ");
        if(currMinute >= 33)sbQueryToday.append("\n                                                               OR lp.VALUE_33 IS NULL                ");
        if(currMinute >= 34)sbQueryToday.append("\n                                                               OR lp.VALUE_34 IS NULL                ");
        if(currMinute >= 35)sbQueryToday.append("\n                                                               OR lp.VALUE_35 IS NULL                ");
        if(currMinute >= 36)sbQueryToday.append("\n                                                               OR lp.VALUE_36 IS NULL                ");
        if(currMinute >= 37)sbQueryToday.append("\n                                                               OR lp.VALUE_37 IS NULL                ");
        if(currMinute >= 38)sbQueryToday.append("\n                                                               OR lp.VALUE_38 IS NULL                ");
        if(currMinute >= 39)sbQueryToday.append("\n                                                               OR lp.VALUE_39 IS NULL                ");
        if(currMinute >= 40)sbQueryToday.append("\n                                                               OR lp.VALUE_40 IS NULL                ");
        if(currMinute >= 41)sbQueryToday.append("\n                                                               OR lp.VALUE_41 IS NULL                ");
        if(currMinute >= 42)sbQueryToday.append("\n                                                               OR lp.VALUE_42 IS NULL                ");
        if(currMinute >= 43)sbQueryToday.append("\n                                                               OR lp.VALUE_43 IS NULL                ");
        if(currMinute >= 44)sbQueryToday.append("\n                                                               OR lp.VALUE_44 IS NULL                ");
        if(currMinute >= 45)sbQueryToday.append("\n                                                               OR lp.VALUE_45 IS NULL                ");
        if(currMinute >= 46)sbQueryToday.append("\n                                                               OR lp.VALUE_46 IS NULL                ");
        if(currMinute >= 47)sbQueryToday.append("\n                                                               OR lp.VALUE_47 IS NULL                ");
        if(currMinute >= 48)sbQueryToday.append("\n                                                               OR lp.VALUE_48 IS NULL                ");
        if(currMinute >= 49)sbQueryToday.append("\n                                                               OR lp.VALUE_49 IS NULL                ");
        if(currMinute >= 50)sbQueryToday.append("\n                                                               OR lp.VALUE_50 IS NULL                ");
        if(currMinute >= 51)sbQueryToday.append("\n                                                               OR lp.VALUE_51 IS NULL                ");
        if(currMinute >= 52)sbQueryToday.append("\n                                                               OR lp.VALUE_52 IS NULL                ");
        if(currMinute >= 53)sbQueryToday.append("\n                                                               OR lp.VALUE_53 IS NULL                ");
        if(currMinute >= 54)sbQueryToday.append("\n                                                               OR lp.VALUE_54 IS NULL                ");
        if(currMinute >= 55)sbQueryToday.append("\n                                                               OR lp.VALUE_55 IS NULL                ");
        if(currMinute >= 56)sbQueryToday.append("\n                                                               OR lp.VALUE_56 IS NULL                ");
        if(currMinute >= 57)sbQueryToday.append("\n                                                               OR lp.VALUE_57 IS NULL                ");
        if(currMinute >= 58)sbQueryToday.append("\n                                                               OR lp.VALUE_58 IS NULL                ");
        if(currMinute >= 59)sbQueryToday.append("\n                                                               OR lp.VALUE_59 IS NULL                ");
        sbQueryToday.append("\n                                                             THEN 0 ELSE 1 end                       ");
        sbQueryToday.append("\n                   WHEN m.lp_interval = 5 THEN CASE WHEN lp.VALUE_00 IS NULL                ");
        if(currMinute >= 5)sbQueryToday.append("\n                                                              OR lp.VALUE_05 IS NULL                ");
        if(currMinute >= 10)sbQueryToday.append("\n                                                                 OR lp.VALUE_10 IS NULL                ");
        if(currMinute >= 15)sbQueryToday.append("\n                                                                 OR lp.VALUE_15 IS NULL                ");
        if(currMinute >= 20)sbQueryToday.append("\n                                                                 OR lp.VALUE_20 IS NULL                ");
        if(currMinute >= 25)sbQueryToday.append("\n                                                                 OR lp.VALUE_25 IS NULL                ");
        if(currMinute >= 30)sbQueryToday.append("\n                                                                 OR lp.VALUE_30 IS NULL                ");
        if(currMinute >= 35)sbQueryToday.append("\n                                                                 OR lp.VALUE_35 IS NULL                ");
        if(currMinute >= 40)sbQueryToday.append("\n                                                                 OR lp.VALUE_40 IS NULL                ");
        if(currMinute >= 45)sbQueryToday.append("\n                                                                 OR lp.VALUE_45 IS NULL                ");
        if(currMinute >= 50)sbQueryToday.append("\n                                                                 OR lp.VALUE_50 IS NULL                ");
        if(currMinute >= 55)sbQueryToday.append("\n                                                                 OR lp.VALUE_55 IS NULL                ");
        sbQueryToday.append("\n                                                               THEN 0 ELSE 1 end                       ");
        sbQueryToday.append("\n                   WHEN m.lp_interval = 10 THEN CASE WHEN lp.VALUE_00 IS NULL               ");
        if(currMinute >= 10)sbQueryToday.append("\n                                                                OR lp.VALUE_10 IS NULL               ");
        if(currMinute >= 20)sbQueryToday.append("\n                                                                OR lp.VALUE_20 IS NULL               ");
        if(currMinute >= 30)sbQueryToday.append("\n                                                                OR lp.VALUE_30 IS NULL               ");
        if(currMinute >= 40)sbQueryToday.append("\n                                                                OR lp.VALUE_40 IS NULL               ");
        if(currMinute >= 50)sbQueryToday.append("\n                                                                OR lp.VALUE_50 IS NULL               ");
        sbQueryToday.append("\n                                                              THEN 0 ELSE 1 end                      ");
        sbQueryToday.append("\n                   WHEN m.lp_interval = 15 THEN CASE WHEN lp.VALUE_00 IS NULL               ");
        if(currMinute >= 15)sbQueryToday.append("\n                                                                OR lp.VALUE_15 IS NULL               ");
        if(currMinute >= 30)sbQueryToday.append("\n                                                                OR lp.VALUE_30 IS NULL               ");
        if(currMinute >= 45)sbQueryToday.append("\n                                                                OR lp.VALUE_45 IS NULL               ");
        sbQueryToday.append("\n                                                              THEN 0 ELSE 1 end                      ");
        sbQueryToday.append("\n                   WHEN m.lp_interval = 30 THEN CASE WHEN lp.VALUE_00 IS NULL               ");
        if(currMinute >= 30)sbQueryToday.append("\n                                                            OR lp.VALUE_30 IS NULL               ");
        sbQueryToday.append("\n                                                          THEN 1 ELSE 0 end                      ");
        sbQueryToday.append("\n                   WHEN m.lp_interval = 60 THEN CASE WHEN lp.VALUE_00 IS NULL               ");
        sbQueryToday.append("\n                                                     THEN 0 ELSE 1 end                      ");
        sbQueryToday.append("\n                   ELSE 0 end                                                                    ");
        sbQueryToday.append("\n            )                                                                               ");

        
        Query query = null;
        if(Integer.parseInt(searchEndDate)<Integer.parseInt(currDate)){
            // 조회종료일이 현재일자보다 이전을경우 전체일자 조회
            query = getSession().createSQLQuery(sbQueryAllPeriod.toString());
            query.setString("searchStartDate", searchStartDate);
            query.setString("searchEndDate", searchEndDate);
            query.setString("installDate", searchStartDate + CommonConstants.DefaultDate.LAST_HHMMSS.getValue());
            query.setString("meterType", meterType);
            query.setInteger("channel", channel);
            query.setInteger("period", period);
            if(!"".equals(supplierId)){
                query.setInteger("supplierId", Integer.parseInt(supplierId));
            }
            if(!"".equals(mdsId)){
                query.setString("mdsId", "%"+ mdsId+"%");           
            }
            if(!"".equals(deviceId)){
                query.setString("deviceId", "%"+ deviceId+"%");
            }
            
        }else{
            if(Integer.parseInt(searchStartDate)<Integer.parseInt(currDate)){
                // 조회종료일이 현재일자이고 조회시작일이 현재일자 이전일경우 시작일~종료일전일,현재일자,현재시간 별로 조회
                query = getSession().createSQLQuery(sbQueryAllPeriodWithToday.toString());
                query.setString("searchStartDate", searchStartDate);
                query.setString("searchEndDate", CalendarUtil.getDateWithoutFormat(searchEndDate,Calendar.DATE, -1));
                query.setString("installDate", searchStartDate + CommonConstants.DefaultDate.LAST_HHMMSS.getValue());
                query.setString("meterType", meterType);
                query.setString("currDate", currDate);
                query.setString("currHour", currHour);
                query.setInteger("channel", channel);
                query.setInteger("period", period);
                if(!"".equals(supplierId)){
                    query.setInteger("supplierId", Integer.parseInt(supplierId));
                }
                if(!"".equals(mdsId)){
                    query.setString("mdsId", "%"+ mdsId+"%");
                }
                if(!"".equals(deviceId)){
                    query.setString("deviceId", "%"+ deviceId+"%");
                }
            }else{
                // 조회일자가 현재일자 하루일경우 현재날짜,현재시간만 조회
                query = getSession().createSQLQuery(sbQueryToday.toString());
                query.setString("installDate", searchStartDate + CommonConstants.DefaultDate.LAST_HHMMSS.getValue());
                query.setString("meterType", meterType);
                query.setString("currDate", currDate);
                query.setString("currHour", currHour);
                query.setInteger("channel", channel);
                if(!"".equals(supplierId)){
                    query.setInteger("supplierId", Integer.parseInt(supplierId));
                }
                if(!"".equals(mdsId)){
                    query.setString("mdsId", "%"+ mdsId+"%");
                }
                if(!"".equals(deviceId)){
                    query.setString("deviceId", "%"+ deviceId+"%");
                }
            }
        }
        
        // query 결과목록
        List<Object> result = query.list();

        List<Object> resultList = new ArrayList<Object>();
        
        int i=1;
        for(Object obj : result){
            HashMap<String,Object> resultMap = new HashMap<String,Object>();
            Object[] objs = (Object[])obj;
            
            params.put("meterId", ((Number)objs[5]).intValue());    
            
            if((Integer)objs[6] == null){
                params.put("lpInterval",60); //lpInterval 값이 null일 경우 기본값(60) 입력
            }
            else{
                params.put("lpInterval",((Number)objs[6]).intValue());
            }
            
            Map<String,Object> countMap = meteringlpDao.getMissingCountByDay(params);
            
            resultMap.put("no",Integer.toString(i++));    
            resultMap.put("customerName",(String)objs[0]);    
    
            if(!"".equals(deviceType)){//deviceType의 값이 null일 경우 체크
                    if(CommonConstants.DeviceType.Modem.getCode().equals(Integer.parseInt(deviceType))){
                        resultMap.put("deviceNo",(String)objs[1]); 
                    }else{
                        resultMap.put("deviceNo",(String)objs[2]); 
                    }
            }
            resultMap.put("mdsId",(String)objs[3]); 
            resultMap.put("missingCnt",countMap.get("totalCount")); 
            resultMap.put("lastReadDate",(String)objs[4]); 
            resultMap.put("meterId",params.get("meterId")); 
            resultMap.put("lpInterval",params.get("lpInterval")); 
            resultList.add(resultMap);
        }
        return resultList;
    }

    @SuppressWarnings("unchecked")
    public List<Meter> getMeterWithGpio(HashMap<String, Object> condition){
        Criteria criteria = getSession().createCriteria(Meter.class);     

        Set<String> set = condition.keySet();
        Object []hmKeys = set.toArray();
        for (int i=0; i<hmKeys.length; i++) {
            String key = (String)hmKeys[i];
            if (key.equals("mdsId")) {
                criteria.add(Restrictions.or(Restrictions.eq(key, condition.get(key)), Restrictions.like("address", "%"+condition.get(key)+"%")));
            } else {
                criteria.add(Restrictions.eq(key, condition.get(key)));
            }
        }

        // 좌표 정보가 없으면 값을 반환하지 않는다.
        criteria.add(Restrictions.isNotNull("gpioX"));  
        criteria.add(Restrictions.isNotNull("gpioY"));  
        criteria.add(Restrictions.isNotNull("gpioZ"));

        List<Meter> meters = (List<Meter>) criteria.list();
        
        return meters;
    }

    @SuppressWarnings("unchecked")
    public List<Meter> getMeterWithoutGpio(HashMap<String, Object> condition){
        Criteria criteria = getSession().createCriteria(Meter.class);     

        Set<String> set = condition.keySet();
        Object []hmKeys = set.toArray();
        for (int i=0; i<hmKeys.length; i++) {
            String key = (String)hmKeys[i];
            if (key.equals("mdsId")) {
                criteria.add(Restrictions.or(Restrictions.eq(key, condition.get(key)), Restrictions.like("address", "%"+condition.get(key)+"%")));
            } else {
                criteria.add(Restrictions.eq(key, condition.get(key)));
            }
        }

        List<Meter> meters = (List<Meter>) criteria.list();
        
        return meters;
    }

    @SuppressWarnings("unchecked")
    public List<Meter> getMeterHavingModem(Integer id){
        Criteria criteria = getSession().createCriteria(Meter.class);     
        HashMap<String, Object> condition = new HashMap<String, Object>();
        condition.put("modem.id", id);

        Set<String> set = condition.keySet();
        Object []hmKeys = set.toArray();
        for (int i=0; i<hmKeys.length; i++) {
            String key = (String)hmKeys[i];
            criteria.add(Restrictions.eq(key, condition.get(key)));
        }

        // 좌표 정보가 없으면 값을 반환하지 않는다.
        criteria.add(Restrictions.isNotNull("gpioX"));  
        criteria.add(Restrictions.isNotNull("gpioY"));  
        criteria.add(Restrictions.isNotNull("gpioZ"));

        List<Meter> meters = (List<Meter>) criteria.list();
        
        return meters;
    }
    
 // VEE에서 사용
    public Integer getMeterVEEParamsCount(HashMap<String, Object> hm) {
        Code meterTypeCode = (Code)hm.get("meterTypeCode");
        String conditionSubQuery = (String)hm.get("conditionSubQuery");
        Query query = null;
        try {
        
            StringBuffer sb = new StringBuffer();
            sb.append("\n SELECT ID     ");
            sb.append("\n FROM Meter        ");
            sb.append("\n WHERE METER.meterType.code.id = '"+meterTypeCode.getId()+"'   ");
            
            if(conditionSubQuery != null && conditionSubQuery.length() > 0)
             {
                sb.append("\n AND       "+conditionSubQuery);
             }
            query = getSession().createQuery(sb.toString());
        }catch(Exception e){
            e.printStackTrace();
        }
        return query.list().size(); 
    }
    
    
    @SuppressWarnings("unchecked")
    public List<Object> getMeteringDataByMeterChart(Map<String, Object> condition){
        
        List<Object> chartData  = new ArrayList<Object>();
        StringBuffer sbQuery    = new StringBuffer();
        
        sbQuery.append(getMeteringDataByMeterQeury(condition));
        sbQuery.append("  ORDER BY 1                \n");
        
        //logger.debug(sbQuery.toString());

        SQLQuery query = getSession().createSQLQuery(new SQLWrapper().getQuery(sbQuery.toString()));
        
        List dataList = null;       
        dataList = query.list();
        
        int dataListLen = 0;
        if(dataList != null)
            dataListLen = dataList.size();
        
        for(int i=0 ; i < dataListLen ; i++){
            
            HashMap chartDataMap = new HashMap();
            Object[] resultData = (Object[]) dataList.get(i);
            
            chartDataMap.put("meteringDate" , resultData[0].toString());
            chartDataMap.put("usage"        , resultData[1].toString());
            chartDataMap.put("co2"          , resultData[2].toString());
            
            chartData.add(chartDataMap);
        }
        
        return chartData;
    }
    
    
    @SuppressWarnings("unchecked")
    public List<Object> getMeteringDataByMeterGrid(Map<String, Object> condition){
        
        List<Object> gridData   = new ArrayList<Object>();
        List<Object> result     = new ArrayList<Object>();
        
        StringBuffer sbQuery      = new StringBuffer();
        
        String curPage          = StringUtil.nullToBlank(condition.get("curPage"));
        
        sbQuery.append(getMeteringDataByMeterQeury(condition));
        
        
        // totalCount - 시작
        StringBuffer countQuery = new StringBuffer();
        countQuery.append("\n SELECT COUNT(*) ");
        countQuery.append("\n FROM (  ");
        countQuery.append(sbQuery);
        countQuery.append("\n ) countTotal ");

        SQLQuery countQueryObj = getSession().createSQLQuery(new SQLWrapper().getQuery(countQuery.toString()));
                                  
        Number totalCount = (Number)countQueryObj.uniqueResult();
        
        result.add(totalCount.toString());
        // totalCount - 종료
        sbQuery.append("  ORDER BY 1                \n");
        SQLQuery query = getSession().createSQLQuery(new SQLWrapper().getQuery(sbQuery.toString()));
        
        // Paging 설정
        int rowPerPage = CommonConstants.Paging.ROWPERPAGE.getPageNum();
        int firstIdx  = Integer.parseInt(curPage) * rowPerPage;     
        query.setFirstResult(firstIdx);
        query.setMaxResults(rowPerPage);    
         
        
        List dataList = null;       
        dataList = query.list();
        
        int dataListLen = 0;
        if(dataList != null)
            dataListLen = dataList.size();
        
        Properties prop = new Properties();
        try {
            prop.load(getClass().getClassLoader().getResourceAsStream(
                    "messages.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        
        String patternNum = "#########.#####";
        DecimalFormat df = new DecimalFormat(patternNum);
        
        for(int i=0 ; i < dataListLen ; i++){
                
            HashMap gridDataMap = new HashMap();
            Object[] resultData = (Object[]) dataList.get(i);
            
            gridDataMap.put("No"            , totalCount.intValue() -i - firstIdx );
            gridDataMap.put("meteringDate"  , resultData[0].toString());
            gridDataMap.put("usage"         , df.format(resultData[1]));
            gridDataMap.put("co2"           , df.format(resultData[2]));
            
            gridData.add(gridDataMap);
        }
        
        result.add(gridData);
        
        return result;
    }
    
    
    private StringBuffer getMeteringDataByMeterQeury(Map<String, Object> condition){
        
        StringBuffer sbQuery      = new StringBuffer();
        
        String meterId          = StringUtil.nullToBlank(condition.get("meterId"));
        String meterType        = StringUtil.nullToBlank(condition.get("meterType"));
                                                                                         
        String searchStartDate  = StringUtil.nullToBlank(condition.get("searchStartDate"));
        String searchEndDate    = StringUtil.nullToBlank(condition.get("searchEndDate"));
        String searchStartHour  = StringUtil.nullToBlank(condition.get("searchStartHour"));
        String searchEndHour    = StringUtil.nullToBlank(condition.get("searchEndHour"));
        String searchDateType   = StringUtil.nullToBlank(condition.get("searchDateType"));
        
        String columnDate   = "";
        String tableName    = "";
        String startDate    = "";
        String endDate      = "";
        
        // 날짜컬럼명과 테이블 명 설정
        if(searchDateType.equals(DateType.HOURLY.getCode())){
            columnDate = "YYYYMMDD";
            startDate  = searchStartDate + searchStartHour;
            endDate    = searchEndDate + searchEndHour;
            
            for (MeterType _meterType : MeterType.values())
                if (_meterType.name().equals(meterType))                                
                    tableName = _meterType.getMonthTableName();
                        
            for (MeterType _meterType : MeterType.values())
                if (_meterType.name().equals(meterType))                                
                    tableName = _meterType.getDayTableName();
            
            sbQuery.append(" SELECT YYYYMMDDHH  as YYYYMMDDHH \n")
                   .append("      , SUM(USAGE) as USAGE \n")
                   .append("      , SUM(CO2) as CO2 \n")
                   .append(" FROM (             \n");
            
            
            for(int i = 0 ; i < 24 ; i++){
                
                String strI = Integer.toString(i); 
                
                if(i < 10)
                    strI = "0" + strI;
                
                if(i > 0)
                    sbQuery.append(" UNION ALL  \n");
                    
                sbQuery.append(" SELECT YYYYMMDD CONCAT '"+strI+"'  AS YYYYMMDDHH           \n")
                       .append("      , CASE WHEN CHANNEL = 1   AND VALUE_"+strI+" IS NOT NULL THEN VALUE_"+strI+" ELSE 0 END AS USAGE      \n")    
                       .append("      , CASE WHEN CHANNEL = 0   AND VALUE_"+strI+" IS NOT NULL THEN VALUE_"+strI+" ELSE 0 END AS CO2        \n")
                       .append("  FROM (  SELECT *          \n")
                       .append("            FROM " + tableName + "\n")
                       .append("           WHERE METER_ID = "+meterId+"     \n")
                       .append("              AND YYYYMMDD >= '"+searchStartDate+"' \n")
                       .append("              AND YYYYMMDD <= '"+searchEndDate+"'   \n")
                       .append("              AND CHANNEL IN (0,1)  \n")
                       .append("    ) L1                \n");
            }
            
            sbQuery.append("    )L2                 \n")
                   .append("        WHERE YYYYMMDDHH >= '"+startDate+"' \n")
                   .append("          AND YYYYMMDDHH <= '"+endDate+"'   \n")
                   .append("        GROUP BY YYYYMMDDHH                 \n");
            
            

        }else{ 

            // 일 / 월에 따른 컬럼 변경
            if(searchDateType.equals(DateType.PERIOD.getCode())){
                columnDate = "YYYYMMDD";
                startDate  = searchStartDate;
                endDate    = searchEndDate;
                
                for (MeterType _meterType : MeterType.values())
                    if (_meterType.name().equals(meterType))                                
                        tableName = _meterType.getDayTableName();
                
            }else if(searchDateType.equals(DateType.MONTHLY.getCode())){
                columnDate = "YYYYMM";
                startDate  = searchStartDate.substring(0,6);
                endDate    = searchEndDate.substring(0,6);
                
                for (MeterType _meterType : MeterType.values())
                    if (_meterType.name().equals(meterType))                                
                        tableName = _meterType.getMonthTableName();
            }
            
            
            sbQuery.append(" SELECT "+ columnDate +"                    \n")
                   .append("      , SUM(CASE WHEN CHANNEL = 1   AND TOTAL IS NOT NULL THEN TOTAL ELSE 0 END) AS USAGE   \n")
                   .append("      , SUM(CASE WHEN CHANNEL = 0   AND TOTAL IS NOT NULL THEN TOTAL ELSE 0 END) AS CO2     \n")
                   .append("   FROM "+ tableName +"                     \n")
                   .append("  WHERE METER_ID = "+ meterId +"            \n")
                   .append("    AND "+columnDate+" >= '"+startDate+"'   \n")
                   .append("    AND "+columnDate+" <= '"+endDate+"'     \n")
                   .append("          AND CHANNEL IN (0,1)  \n")
                   .append("  GROUP BY "+columnDate+"                   \n");
            
        }
        
        
        
        return sbQuery;
    }

    /**
     * 그룹 관리 중 멤버 리스트 조회
     * 
     * @param condition
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<Object> getGroupMember(Map<String, Object> condition){
        @SuppressWarnings("unused")
        String member = StringUtil.nullToBlank(condition.get("member"));
        String searchMemberType = StringUtil.nullToBlank(condition.get("searchMemberType"));

        StringBuffer sb = new StringBuffer();
        sb.append("SELECT t.id, t.mds_id, g.member ")
          .append("FROM METER t ")
          .append(" LEFT JOIN GROUP_MEMBER g ON t.mds_id = g.member ");
            
            if(GroupType.DCU.name().equals(searchMemberType)){
                
                sb.append(" LEFT OUTER JOIN MODEM modem ON t.MODEM_ID = modem.ID  ");
                sb.append(" LEFT OUTER JOIN MCU mcu ON modem.MCU_ID = mcu.ID   ");
                sb.append("WHERE mcu.SYS_ID like '%").append((String)condition.get("member")).append("%'");
            }
            else if(GroupType.Location.name().equals(searchMemberType)){
                sb.append("INNER JOIN LOCATION location  ");
                sb.append("     ON (t.LOCATION_ID = LOCATION.ID)  \n");
                sb.append("WHERE location.NAME like '%").append((String)condition.get("member")).append("%'");
            }
            else if(GroupType.Contract.name().equals(searchMemberType)){
                sb.append("INNER JOIN CONTRACT contract  ");
                sb.append("     ON (t.ID = CONTRACT.METER_ID)  \n");
                sb.append("WHERE contract.CONTRACT_NUMBER like '%").append((String)condition.get("member")).append("%'");
            }
            else if(GroupType.Modem.name().equals(searchMemberType)){               
                sb.append("   INNER JOIN MODEM modem    \n");
                sb.append("     ON (t.MODEM_ID = modem.ID)  \n");
                sb.append("WHERE modem.DEVICE_SERIAL like '%").append((String)condition.get("member")).append("%'");
            }
            else if(GroupType.Meter.name().equals(searchMemberType)){
                sb.append("WHERE t.mds_id like '%").append((String)condition.get("member")).append("%'");
            }else {
              sb.append(" WHERE 1=1 ");
            }
          sb.append(" AND t.supplier_id = :supplierId ");

        sb.append("AND t.mds_id NOT IN ( ");
            sb.append("SELECT t.mds_id ");
            sb.append("FROM METER t RIGHT JOIN GROUP_MEMBER g ON t.mds_id = g.member ");
            sb.append("WHERE t.supplier_id = :supplierId ");
        sb.append(") ");

        SQLQuery query = getSession().createSQLQuery(sb.toString());
        //logger.debug(sb.toString());
        return query.setInteger("supplierId", Integer.parseInt((String)condition.get("supplierId")))
                    .list();
    }

    public List<Object> getMeterSupplierList() {
        
        StringBuffer sb = new StringBuffer();
        sb.append("select distinct(s.id) as supplier  from supplier s,meter m where s.id= m.SUPPLIER_ID");
    
        
        SQLQuery query = getSession().createSQLQuery(sb.toString());
        
        return query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
    }

    /**
     * method name : getMeterSearchChartMdis<b/>
     * method Desc : MDIS - Meter Management 화면에서 chart 데이터를 조회한다.
     *
     * @param condition
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<Object> getMeterSearchChartMdis(Map<String, Object> condition){

        List<Object> gridData    = new ArrayList<Object>();
        List<Object> chartData   = new ArrayList<Object>();
        List<Object> result      = new ArrayList<Object>();
        StringBuffer sbQuery     = new StringBuffer();

        String sMeterType         = StringUtil.nullToBlank(condition.get("sMeterType"));
        String sMdsId             = StringUtil.nullToBlank(condition.get("sMdsId"));
        String sStatus            = StringUtil.nullToBlank(condition.get("sStatus"));

        String sCmdStatus         = StringUtil.nullToBlank(condition.get("sCmdStatus"));
        String sOperators         = StringUtil.nullToBlank(condition.get("sOperators"));
        Integer sPrepaidDeposit   = (Integer)condition.get("sPrepaidDeposit");

        String sMcuId             = StringUtil.nullToBlank(condition.get("sMcuId"));
        String sLocationId        = StringUtil.nullToBlank(condition.get("sLocationId"));
        String sConsumLocationId  = StringUtil.nullToBlank(condition.get("sConsumLocationId"));

        String sVendor            = StringUtil.nullToBlank(condition.get("sVendor"));
        String sModel             = StringUtil.nullToBlank(condition.get("sModel"));
        String sInstallStartDate  = StringUtil.nullToBlank(condition.get("sInstallStartDate"));
        String sInstallEndDate    = StringUtil.nullToBlank(condition.get("sInstallEndDate"));

        String sModemYN           = StringUtil.nullToBlank(condition.get("sModemYN"));
        String sCustomerYN        = StringUtil.nullToBlank(condition.get("sCustomerYN"));
        String sLastcommStartDate = StringUtil.nullToBlank(condition.get("sLastcommStartDate"));
        String sLastcommEndDate   = StringUtil.nullToBlank(condition.get("sLastcommEndDate"));

        String supplierId         = StringUtil.nullToBlank(condition.get("supplierId"));

        sbQuery.append("       FROM METER me                                   \n")
               .append("       LEFT OUTER JOIN MODEM mo                        \n")
               .append("         ON ( me.MODEM_ID = mo.ID)                     \n")
               .append("       LEFT OUTER JOIN MCU mcu                         \n")
               .append("         ON ( mo.MCU_ID  = mcu.ID)                     \n")
               .append("       LEFT OUTER JOIN LOCATION loc                    \n")
               .append("         ON ( me.LOCATION_ID = loc.ID)                 \n")
               .append("       LEFT OUTER JOIN CONTRACT cont                   \n")  // cont, contract 동일한 Contract 테이블이 중복으로 outer join 걸려있음
               .append("         ON ( me.ID = cont.METER_ID)                   \n")
               .append("       LEFT OUTER JOIN (                               \n")
               .append("            SELECT model.ID    AS modelId              \n")
               .append("                 , model.NAME  AS modelName            \n")
               .append("                 , vendor.ID   AS vendorId             \n")
               .append("                 , vendor.NAME as vendorName           \n")
               .append("             FROM DEVICEMODEL model                    \n")
               .append("             LEFT OUTER JOIN DEVICEVENDOR vendor       \n")
               .append("                ON (model.DEVICEVENDOR_ID = vendor.ID) \n")
               .append("             ) device                                  \n")
               .append("         ON (me.DEVICEMODEL_ID = device.modelId)       \n")
               .append("        LEFT OUTER JOIN CONTRACT contract              \n")  // cont, contract 동일한 Contract 테이블이 중복으로 outer join 걸려있음
               .append("          ON (me.ID = contract.METER_ID)         \n")
               .append("       WHERE me.SUPPLIER_ID = :supplierId              \n");

        if(!sMeterType.equals(""))
            sbQuery.append("     AND me.METER = '"+ sMeterType +"'");

        if(!sMdsId.equals(""))
            sbQuery.append("     AND me.mds_ID LIKE '%"+ sMdsId +"%'");

        if(!sStatus.equals(""))
            sbQuery.append("     AND me.METER_STATUS = "+ sStatus);

        if(sCmdStatus.equals("R")) {
//            sbQuery.append("     AND me.CONDITIONS LIKE '%RelayOff%' ");
            sbQuery.append("     AND me.SWITCH_STATUS = 0 ");
        } else if(sCmdStatus.equals("T")) {
            sbQuery.append("     AND me.CONDITIONS LIKE :conditions ");
        } else if(sCmdStatus.equals("P")) {
            if (sPrepaidDeposit != null) {
                sbQuery.append("     AND cont.PREPAYMENTPOWERDELAY ").append(sOperators).append(" ").append(sPrepaidDeposit);
            }
        }

        if(!sMcuId.equals(""))
            sbQuery.append("     AND mo.MCU_ID = "+ sMcuId );

        if(!sLocationId.equals(""))
            sbQuery.append("     AND me.LOCATION_ID IN ("+ sLocationId + ")");

        if(!sConsumLocationId.equals(""))
            sbQuery.append("     AND cont.CONTRACT_NUMBER = '"+ sConsumLocationId +"'" );

        if(!sVendor.equals("0") && !sVendor.equals(""))
            sbQuery.append("     AND device.vendorId = "+ sVendor );

        if(!sModel.equals(""))
            sbQuery.append("     AND device.modelId = "+ sModel );

        if(!sInstallStartDate.equals(""))
            sbQuery.append("     AND me.INSTALL_DATE >= '"+ sInstallStartDate +"000000'");

        if(!sInstallEndDate.equals(""))
            sbQuery.append("     AND me.INSTALL_DATE <= '"+ sInstallEndDate +"235900'");


        if(sModemYN.equals("Y"))
            sbQuery.append("     AND me.MODEM_ID IS NOT NULL");
        else if(sModemYN.equals("N"))
            sbQuery.append("     AND me.MODEM_ID IS NULL");

        if(sCustomerYN.equals("Y"))
            sbQuery.append("     AND contract.ID IS NOT NULL");
        else if(sCustomerYN.equals("N"))
            sbQuery.append("     AND contract.ID IS NULL");


        if(!sLastcommStartDate.equals(""))
            sbQuery.append("     AND me.LAST_READ_DATE >= '"+ sLastcommStartDate +"000000'");

        if(!sLastcommEndDate.equals(""))
            sbQuery.append("     AND me.LAST_READ_DATE <= '"+ sLastcommEndDate +"235900'");


        StringBuffer sbQueryGrid = new StringBuffer();
        sbQueryGrid.append("   SELECT mcu.SYS_Id AS mcuSysId \n");
        sbQueryGrid.append("        , COUNT(mo.MCU_ID) AS totalCnt \n");
        sbQueryGrid.append("        , SUM(CASE WHEN me.LAST_READ_DATE  >= :datePre24H THEN 1 ELSE 0 END)    AS value0 \n");
        sbQueryGrid.append("        , SUM(CASE WHEN     me.LAST_READ_DATE < :datePre24H ");
        sbQueryGrid.append("                        AND me.LAST_READ_DATE >= :datePre48H THEN 1 ELSE 0 END) AS value1 \n");
        sbQueryGrid.append("        , SUM(CASE WHEN     me.LAST_READ_DATE < :datePre48H THEN 1 ELSE 0 END)  AS value2 \n");
        sbQueryGrid.append(sbQuery);
        sbQueryGrid.append("      AND mo.MCU_ID IS NOT NULL \n");
        sbQueryGrid.append("    GROUP BY mcu.SYS_Id \n");
        sbQueryGrid.append("    ORDER BY totalCnt DESC \n");

        SQLQuery query = getSession().createSQLQuery(new SQLWrapper().getQuery(sbQueryGrid.toString()));

        if(sCmdStatus.equals("T")) {
            query.setString("conditions", "%" + MdisTamperingStatus.TAMPERING_ISSUED.getMessage() + "%");
        }

        String datePre24H = DateTimeUtil.calcDate(Calendar.HOUR, -24, "yyyyMMddhhmmss");
        String datePre48H = DateTimeUtil.calcDate(Calendar.HOUR, -48, "yyyyMMddhhmmss");

        query.setString("datePre24H", datePre24H);
        query.setString("datePre48H", datePre48H);
        query.setInteger("supplierId", Integer.parseInt(supplierId));

        List dataList = null;
        dataList = query.list();

        // 실제 데이터
        int dataListLen = 0;
        if(dataList != null)
            dataListLen= dataList.size();

        for(int i=0 ; i < dataListLen ; i++){

            HashMap gridDataMap  = new HashMap();
            Object[] resultData = (Object[]) dataList.get(i);

            gridDataMap.put("no",        i+1);
            gridDataMap.put("mcuSysID",      resultData[0]);
            gridDataMap.put("value0",        resultData[2]);
            gridDataMap.put("value1",        resultData[3]);
            gridDataMap.put("value2",        resultData[4]);

            gridData.add(gridDataMap);
        }
        result.add(gridData);

        // Chart Data
        StringBuffer sbQueryChart = new StringBuffer();

        sbQueryChart.append("   SELECT '0'                AS commStatus                 \n")
                    .append("        , COUNT(me.ID)       AS cnt                        \n")
                    .append(sbQuery)
                    .append("        AND me.LAST_READ_DATE  >= :datePre24H                  \n")
                    .append("   UNION ALL                                               \n")

                    .append("   SELECT '1'                AS commStatus                 \n")
                    .append("        , COUNT(me.ID)       AS cnt                        \n")
                    .append(sbQuery)
                    .append("        AND me.LAST_READ_DATE < :datePre24H                    \n")
                    .append("        AND me.LAST_READ_DATE >= :datePre48H                   \n")
                    .append("   UNION ALL                                               \n")

                    .append("   SELECT '2'                AS commStatus                 \n")
                    .append("        , COUNT(me.ID)       AS cnt                        \n")
                    .append(sbQuery)
                    .append("         AND me.LAST_READ_DATE < :datePre48H               \n");

        query = getSession().createSQLQuery(new SQLWrapper().getQuery(sbQueryChart.toString()));

        if(sCmdStatus.equals("T")) {
            query.setString("conditions", "%" + MdisTamperingStatus.TAMPERING_ISSUED.getMessage() + "%");
        }

        query.setString("datePre24H", datePre24H);
        query.setString("datePre48H", datePre48H);
        query.setInteger("supplierId", Integer.parseInt(supplierId));

        dataList = null;
        dataList = query.list();

        // 실제 데이터
        dataListLen = 0;
        if(dataList != null)
            dataListLen= dataList.size();

        for(int i=0 ; i < dataListLen ; i++){

            HashMap chartDataMap = new HashMap();
            Object[] resultData = (Object[]) dataList.get(i);

            if(resultData[0].toString().equals("0")){
                chartDataMap.put("label", "fmtMessage");
                chartDataMap.put("data" , resultData[1]);
            }else if(resultData[0].toString().equals("1")){
                chartDataMap.put("label", "fmtMessage24");
                chartDataMap.put("data", resultData[1]);
            }else if(resultData[0].toString().equals("2")){
                chartDataMap.put("label", "fmtMessage48");
                chartDataMap.put("data", resultData[1]);
            }
                chartData.add(chartDataMap);
        }

        result.add(chartData);

        return result;
    }

    /**
     * method name : getMeterSearchGridMdis<b/>
     * method Desc : MDIS - Meter Management 화면에서 미터기 정보 리스트를 조회한다.
     *
     * @param condition
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<Object> getMeterSearchGridMdis(Map<String, Object> condition){

        List<Object> gridData    = new ArrayList<Object>();
        List<Object> result      = new ArrayList<Object>();
        StringBuffer sbQuery     = new StringBuffer();

        String sMeterType         = StringUtil.nullToBlank(condition.get("sMeterType"));
        String sMdsId             = StringUtil.nullToBlank(condition.get("sMdsId"));
        String sStatus            = StringUtil.nullToBlank(condition.get("sStatus"));

        String sCmdStatus         = StringUtil.nullToBlank(condition.get("sCmdStatus"));
        String sOperators         = StringUtil.nullToBlank(condition.get("sOperators"));
        Integer sPrepaidDeposit   = (Integer)condition.get("sPrepaidDeposit");

        String sMcuId             = StringUtil.nullToBlank(condition.get("sMcuId"));
        String sLocationId        = StringUtil.nullToBlank(condition.get("sLocationId"));
        String sConsumLocationId  = StringUtil.nullToBlank(condition.get("sConsumLocationId"));

        String sVendor            = StringUtil.nullToBlank(condition.get("sVendor"));
        String sModel             = StringUtil.nullToBlank(condition.get("sModel"));
        String sInstallStartDate  = StringUtil.nullToBlank(condition.get("sInstallStartDate"));
        String sInstallEndDate    = StringUtil.nullToBlank(condition.get("sInstallEndDate"));

        String sModemYN           = StringUtil.nullToBlank(condition.get("sModemYN"));
        String sCustomerYN        = StringUtil.nullToBlank(condition.get("sCustomerYN"));
        String sMcuName           = StringUtil.nullToBlank(condition.get("sMcuName"));
        String sLastcommStartDate = StringUtil.nullToBlank(condition.get("sLastcommStartDate"));
        String sLastcommEndDate   = StringUtil.nullToBlank(condition.get("sLastcommEndDate"));

        String curPage            = StringUtil.nullToBlank(condition.get("curPage"));
        String sOrder             = StringUtil.nullToBlank(condition.get("sOrder"));
        String sCommState         = StringUtil.nullToBlank(condition.get("sCommState"));

        String supplierId         = StringUtil.nullToBlank(condition.get("supplierId"));

        sbQuery.append("  SELECT me.MDS_ID            AS meterMds               \n")
               .append("       , me.METER             AS meterType              \n")
               .append("       , mcu.SYS_ID           AS mcuSysID               \n")
               .append("       , device.vendorName    AS vendorName             \n")
               .append("       , device.modelName     AS modelName              \n")
               .append("       , contract.ID          AS customer               \n")
               .append("       , me.INSTALL_DATE      AS installDate            \n")
               .append("       , me.LAST_READ_DATE    AS lastCommDate           \n")
               .append("       , loc.NAME             AS locName                \n")
               .append("       , CASE WHEN LAST_READ_DATE >= :datePre24H THEN 0   \n")
               .append("              WHEN LAST_READ_DATE <  :datePre24H            \n")
               .append("               AND LAST_READ_DATE >= :datePre48H THEN 1   \n")
               .append("            WHEN LAST_READ_DATE <  :datePre48H THEN 2     \n")
               .append("            ELSE 9                                    \n")
               .append("            END               AS commStatus                  \n")
               .append("       , me.ID                AS meterId                \n")
               .append("       , me.SWITCH_STATUS     AS switchStatus           \n")
               .append("       , me.CONDITIONS        AS commandStatus             \n")
               .append("       , cont.PREPAYMENTPOWERDELAY AS prepaidDeposit  \n")
               .append("       , me.LAST_METERING_VALUE AS lastMeteringValue  \n")
               .append("       , me.devicemodel_id AS deviceModelId  \n")
               .append("       FROM METER me                                    \n")
               .append("       LEFT OUTER JOIN MODEM mo                         \n")
               .append("         ON ( me.MODEM_ID = mo.ID)                      \n")
               .append("       LEFT OUTER JOIN MCU mcu                          \n")
               .append("         ON ( mo.MCU_ID = mcu.ID)                       \n")
               .append("       LEFT OUTER JOIN LOCATION loc                     \n")
               .append("         ON ( me.LOCATION_ID = loc.ID)                  \n")
               .append("       LEFT OUTER JOIN CONTRACT cont                    \n")
               .append("         ON ( me.ID = cont.METER_ID)                    \n")
               .append("       LEFT OUTER JOIN (                                \n")
               .append("            SELECT model.ID    AS modelId               \n")
               .append("                 , model.NAME  AS modelName             \n")
               .append("                 , vendor.ID   AS vendorId              \n")
               .append("                 , vendor.NAME as vendorName            \n")
               .append("             FROM DEVICEMODEL model                     \n")
               .append("             LEFT OUTER JOIN DEVICEVENDOR vendor        \n")
               .append("                ON (model.DEVICEVENDOR_ID = vendor.ID)  \n")
               .append("             ) device                                   \n")
               .append("          ON (me.DEVICEMODEL_ID = device.modelId)       \n")
               .append("        LEFT OUTER JOIN CONTRACT contract               \n")
               .append("          ON (me.ID = contract.METER_ID)         \n")
               .append("       WHERE me.SUPPLIER_ID = :supplierId               \n");

        if(!sMeterType.equals(""))
            sbQuery.append("     AND me.METER = '"+ sMeterType +"'");

        if(!sMdsId.equals(""))
            sbQuery.append("     AND me.mds_ID LIKE '%"+ sMdsId +"%'");

        if(!sStatus.equals(""))
            sbQuery.append("     AND me.METER_STATUS = "+ sStatus);

        if(sCmdStatus.equals("R")) {
//            sbQuery.append("     AND me.CONDITIONS LIKE '%RelayOff%' ");
            sbQuery.append("     AND me.SWITCH_STATUS = 0 ");
        } else if(sCmdStatus.equals("T")) {
            sbQuery.append("     AND me.CONDITIONS LIKE :conditions ");
        } else if(sCmdStatus.equals("P")) {
            if (sPrepaidDeposit != null) {
                sbQuery.append("     AND cont.PREPAYMENTPOWERDELAY ").append(sOperators).append(" ").append(sPrepaidDeposit);
            }
        }

        if(!sMcuId.equals(""))
            sbQuery.append("     AND mo.MCU_ID = "+ sMcuId );

        if(!sLocationId.trim().equals(""))
            sbQuery.append("     AND me.LOCATION_ID IN ("+ sLocationId +")");

        if(!sMcuName.equals(""))
            sbQuery.append("     AND mcu.SYS_ID = '" + sMcuName+ "'"); //LIKE '%"+ sMcuName +"%'");

        if(!sConsumLocationId.equals(""))
            sbQuery.append("     AND cont.CONTRACT_NUMBER = '"+ sConsumLocationId +"'" );

        if(!sVendor.equals("0") && !sVendor.equals(""))
            sbQuery.append("     AND device.vendorId = "+ sVendor );

        if(!sModel.equals(""))
            sbQuery.append("     AND device.modelId = "+ sModel );

        if(!sInstallStartDate.equals(""))
            sbQuery.append("     AND me.INSTALL_DATE >= '"+ sInstallStartDate +"000000'");

        if(!sInstallEndDate.equals(""))
            sbQuery.append("     AND me.INSTALL_DATE <= '"+ sInstallEndDate +"235959'");

        if(sModemYN.equals("Y"))
            sbQuery.append("     AND me.MODEM_ID IS NOT NULL");
        else if(sModemYN.equals("N"))
            sbQuery.append("     AND me.MODEM_ID IS NULL");

        if(sCustomerYN.equals("Y"))
            sbQuery.append("     AND contract.ID IS NOT NULL");
        else if(sCustomerYN.equals("N"))
            sbQuery.append("     AND contract.ID IS NULL");

        if(!sLastcommStartDate.equals(""))
            sbQuery.append("     AND me.LAST_READ_DATE >= '"+ sLastcommStartDate +"000000'");

        if(!sLastcommEndDate.equals(""))
            sbQuery.append("     AND me.LAST_READ_DATE <= '"+ sLastcommEndDate +"235959'");

        if(sCommState.equals("0"))
            sbQuery.append("     AND me.LAST_READ_DATE  >= :datePre24H \n");
        else if(sCommState.equals("1"))
            sbQuery.append("     AND LAST_READ_DATE < :datePre24H " +
                           "     AND LAST_READ_DATE >= :datePre48H \n");
        else if(sCommState.equals("2"))
            sbQuery.append("     AND LAST_READ_DATE < :datePre48H ");

        StringBuffer sbQueryData = new StringBuffer();
        sbQueryData.append(sbQuery);

        if(sOrder.equals("1"))
            sbQueryData.append("    ORDER BY me.LAST_READ_DATE DESC                 \n");
        else if(sOrder.equals("2"))
            sbQueryData.append("    ORDER BY me.LAST_READ_DATE                      \n");
        else if(sOrder.equals("3"))
            sbQueryData.append("    ORDER BY me.INSTALL_DATE DESC                   \n");
        else if(sOrder.equals("4"))
            sbQueryData.append("    ORDER BY me.INSTALL_DATE                        \n");

        SQLQuery query = getSession().createSQLQuery(new SQLWrapper().getQuery(sbQueryData.toString()));

        if(sCmdStatus.equals("T")) {
           query.setString("conditions", "%" + MdisTamperingStatus.TAMPERING_ISSUED.getMessage() + "%");
        }

        String datePre24H = DateTimeUtil.calcDate(Calendar.HOUR, -24, "yyyyMMddhhmmss");
        String datePre48H = DateTimeUtil.calcDate(Calendar.HOUR, -48, "yyyyMMddhhmmss");

        query.setString("datePre24H", datePre24H);
        query.setString("datePre48H", datePre48H);
        query.setInteger("supplierId", Integer.parseInt(supplierId));

        // Paging
        int rowPerPage = CommonConstants.Paging.ROWPERPAGE.getPageNum();
        int firstIdx  = Integer.parseInt(curPage) * rowPerPage;

        query.setFirstResult(firstIdx);
        query.setMaxResults(rowPerPage);

        List dataList = query.list();

        // 전체 건수
        StringBuffer countQuery = new StringBuffer();
        countQuery.append("\n SELECT COUNT(countTotal.meterMDS) ");
        countQuery.append("\n FROM (  ");
        countQuery.append(sbQuery);
        countQuery.append("\n ) countTotal ");

        SQLQuery countQueryObj = getSession().createSQLQuery(new SQLWrapper().getQuery(countQuery.toString()));

        if(sCmdStatus.equals("T")) {
            countQueryObj.setString("conditions", "%" + MdisTamperingStatus.TAMPERING_ISSUED.getMessage() + "%");
        }

        countQueryObj.setString("datePre24H", datePre24H);
        countQueryObj.setString("datePre48H", datePre48H);
        countQueryObj.setInteger("supplierId", Integer.parseInt(supplierId));

        Number totalCount = (Number)countQueryObj.uniqueResult();

        result.add(totalCount.toString());

        int dataListLen = dataList != null ? dataList.size() : 0;
        Map<String, Object> chartDataMap = null;
        Object[] resultData = null;

        for (Object obj : dataList) {
            chartDataMap = new HashMap();
            resultData = (Object[])obj;

            chartDataMap.put("meterMds",       resultData[0]);
            chartDataMap.put("meterType",      resultData[1]);
            chartDataMap.put("mcuSysID",       resultData[2]);
            chartDataMap.put("vendorName",     resultData[3]);
            chartDataMap.put("modelName",      resultData[4]);
            chartDataMap.put("customer",       resultData[5]);
            chartDataMap.put("installDate",    resultData[6]);
            chartDataMap.put("lastCommDate",   resultData[7]);
            chartDataMap.put("locName",        resultData[8]);
            chartDataMap.put("commStatus",     resultData[9]);
            chartDataMap.put("meterId",        resultData[10]);
            chartDataMap.put("switchStatus",   resultData[11]);
            chartDataMap.put("commandStatus",  resultData[12]);
            chartDataMap.put("prepaidDeposit", resultData[13]);
            chartDataMap.put("lastMeteringValue", resultData[14]);
            chartDataMap.put("deviceModelId", resultData[15]);
            gridData.add(chartDataMap);
        }

        result.add(gridData);

        return result;
    }

    /**
     * method name : getMeterMdisExportExcelData<b/>
     * method Desc : MDIS - Meter Management 가젯의 Export Excel Data 를 조회한다.
     *
     * @param condition
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> getMeterMdisExportExcelData(Map<String, Object> conditionMap){
//        List<Object> gridData    = new ArrayList<Object>();
        List<Map<String, Object>> result      = new ArrayList<Map<String, Object>>();
//        StringBuffer sbQuery     = new StringBuffer();

        String sMeterType         = StringUtil.nullToBlank(conditionMap.get("sMeterType"));
        String sMdsId             = StringUtil.nullToBlank(conditionMap.get("sMdsId"));
        Integer sStatus            = (Integer)conditionMap.get("sStatus");

        String sCmdStatus         = StringUtil.nullToBlank(conditionMap.get("sCmdStatus"));
        String sOperators         = StringUtil.nullToBlank(conditionMap.get("sOperators"));
        Integer sPrepaidDeposit   = (Integer)conditionMap.get("sPrepaidDeposit");

        Integer sMcuId             = (Integer)conditionMap.get("sMcuId");
        Integer sLocationId        = (Integer)conditionMap.get("sLocationId");
        String sConsumLocationId  = StringUtil.nullToBlank(conditionMap.get("sConsumLocationId"));

        Integer sVendor            = (Integer)conditionMap.get("sVendor");
        Integer sModel             = (Integer)conditionMap.get("sModel");
        String sInstallStartDate  = StringUtil.nullToBlank(conditionMap.get("sInstallStartDate"));
        String sInstallEndDate    = StringUtil.nullToBlank(conditionMap.get("sInstallEndDate"));

        String sModemYN           = StringUtil.nullToBlank(conditionMap.get("sModemYN"));
        String sCustomerYN        = StringUtil.nullToBlank(conditionMap.get("sCustomerYN"));
        String sMcuName           = StringUtil.nullToBlank(conditionMap.get("sMcuName"));
        String sLastcommStartDate = StringUtil.nullToBlank(conditionMap.get("sLastcommStartDate"));
        String sLastcommEndDate   = StringUtil.nullToBlank(conditionMap.get("sLastcommEndDate"));

//        String curPage            = StringUtil.nullToBlank(conditionMap.get("curPage"));
        String sOrder = StringUtil.nullToBlank(conditionMap.get("sOrder"));
        String sCommState = StringUtil.nullToBlank(conditionMap.get("sCommState"));

        Integer supplierId = (Integer)conditionMap.get("supplierId");
        List<Integer> locationIdList = (List<Integer>)conditionMap.get("locationIdList");

//        sbQuery.append("  SELECT me.MDS_ID            AS meterMds               \n")
//               .append("       , me.METER             AS meterType              \n")
//               .append("       , mcu.SYS_ID           AS mcuSysID               \n")
//               .append("       , device.vendorName    AS vendorName             \n")
//               .append("       , device.modelName     AS modelName              \n")
//               .append("       , contract.ID          AS customer               \n")
//               .append("       , me.INSTALL_DATE      AS installDate            \n")
//               .append("       , me.LAST_READ_DATE    AS lastCommDate           \n")
//               .append("       , loc.NAME             AS locName                \n")
//               .append("       , CASE WHEN LAST_READ_DATE >= :datePre24H THEN 0 \n")
//               .append("              WHEN LAST_READ_DATE <  :datePre24H        \n")
//               .append("               AND LAST_READ_DATE >= :datePre48H THEN 1 \n")
//               .append("            WHEN LAST_READ_DATE <  :datePre48H THEN 2   \n")
//               .append("            ELSE 9                                    \n")
//               .append("            END               AS commStatus             \n")
//               .append("       , me.ID                AS meterId                \n")
//               .append("       , me.SWITCH_STATUS     AS switchStatus           \n")
//               .append("       , me.CONDITIONS        AS commandStatus          \n")
//               .append("       , cont.PREPAYMENTPOWERDELAY AS prepaidDeposit  \n")
//               .append("       , me.LAST_METERING_VALUE AS lastMeteringValue  \n")
//               .append("       , me.devicemodel_id AS deviceModelId  \n")
//               .append("       FROM METER me                                    \n")
//               .append("       LEFT OUTER JOIN MODEM mo                         \n")
//               .append("         ON ( me.MODEM_ID = mo.ID)                      \n")
//               .append("       LEFT OUTER JOIN MCU mcu                          \n")
//               .append("         ON ( mo.MCU_ID = mcu.ID)                       \n")
//               .append("       LEFT OUTER JOIN LOCATION loc                     \n")
//               .append("         ON ( me.LOCATION_ID = loc.ID)                  \n")
//               .append("       LEFT OUTER JOIN CONTRACT cont                    \n")
//               .append("         ON ( me.ID = cont.METER_ID)                    \n")
//               .append("       LEFT OUTER JOIN (                                \n")
//               .append("            SELECT model.ID    AS modelId               \n")
//               .append("                 , model.NAME  AS modelName             \n")
//               .append("                 , vendor.ID   AS vendorId              \n")
//               .append("                 , vendor.NAME as vendorName            \n")
//               .append("             FROM DEVICEMODEL model                     \n")
//               .append("             LEFT OUTER JOIN DEVICEVENDOR vendor        \n")
//               .append("                ON (model.DEVICEVENDOR_ID = vendor.ID)  \n")
//               .append("             ) device                                   \n")
//               .append("          ON (me.DEVICEMODEL_ID = device.modelId)       \n")
//               .append("        LEFT OUTER JOIN CONTRACT contract               \n")
//               .append("          ON (me.ID = contract.METER_ID)         \n")
//               .append("       WHERE me.SUPPLIER_ID = :supplierId               \n");
//
//        if(!sMeterType.equals(""))
//            sbQuery.append("     AND me.METER = '"+ sMeterType +"'");
//
//        if(!sMdsId.equals(""))
//            sbQuery.append("     AND me.mds_ID LIKE '%"+ sMdsId +"%'");
//
//        if(!sStatus.equals(""))
//            sbQuery.append("     AND me.METER_STATUS = "+ sStatus);
//
//        if(sCmdStatus.equals("R")) {
////            sbQuery.append("     AND me.CONDITIONS LIKE '%RelayOff%' ");
//            sbQuery.append("     AND me.SWITCH_STATUS = 0 ");
//        } else if(sCmdStatus.equals("T")) {
//            sbQuery.append("     AND me.CONDITIONS LIKE :conditions ");
//        } else if(sCmdStatus.equals("P")) {
//            if (sPrepaidDeposit != null) {
//                sbQuery.append("     AND cont.PREPAYMENTPOWERDELAY ").append(sOperators).append(" ").append(sPrepaidDeposit);
//            }
//        }
//
//        if(!sMcuId.equals(""))
//            sbQuery.append("     AND mo.MCU_ID = "+ sMcuId );
//
//        if(!sLocationId.trim().equals(""))
//            sbQuery.append("     AND me.LOCATION_ID IN ("+ sLocationId +")");
//
//        if(!sMcuName.equals(""))
//            sbQuery.append("     AND mcu.SYS_ID = '" + sMcuName+ "'"); //LIKE '%"+ sMcuName +"%'");
//
//        if(!sConsumLocationId.equals(""))
//            sbQuery.append("     AND cont.CONTRACT_NUMBER = '"+ sConsumLocationId +"'" );
//
//        if(!sVendor.equals("0") && !sVendor.equals(""))
//            sbQuery.append("     AND device.vendorId = "+ sVendor );
//
//        if(!sModel.equals(""))
//            sbQuery.append("     AND device.modelId = "+ sModel );
//
//        if(!sInstallStartDate.equals(""))
//            sbQuery.append("     AND me.INSTALL_DATE >= '"+ sInstallStartDate +"000000'");
//
//        if(!sInstallEndDate.equals(""))
//            sbQuery.append("     AND me.INSTALL_DATE <= '"+ sInstallEndDate +"235959'");
//
//        if(sModemYN.equals("Y"))
//            sbQuery.append("     AND me.MODEM_ID IS NOT NULL");
//        else if(sModemYN.equals("N"))
//            sbQuery.append("     AND me.MODEM_ID IS NULL");
//
//        if(sCustomerYN.equals("Y"))
//            sbQuery.append("     AND contract.ID IS NOT NULL");
//        else if(sCustomerYN.equals("N"))
//            sbQuery.append("     AND contract.ID IS NULL");
//
//        if(!sLastcommStartDate.equals(""))
//            sbQuery.append("     AND me.LAST_READ_DATE >= '"+ sLastcommStartDate +"000000'");
//
//        if(!sLastcommEndDate.equals(""))
//            sbQuery.append("     AND me.LAST_READ_DATE <= '"+ sLastcommEndDate +"235959'");
//
//        if(sCommState.equals("0"))
//            sbQuery.append("     AND me.LAST_READ_DATE  >= :datePre24H \n");
//        else if(sCommState.equals("1"))
//            sbQuery.append("     AND LAST_READ_DATE < :datePre24H " +
//                           "     AND LAST_READ_DATE >= :datePre48H \n");
//        else if(sCommState.equals("2"))
//            sbQuery.append("     AND LAST_READ_DATE < :datePre48H ");
//
//        StringBuffer sbQueryData = new StringBuffer();
//        sbQueryData.append(sbQuery);
//
//        if(sOrder.equals("1"))
//            sbQueryData.append("    ORDER BY me.LAST_READ_DATE DESC                 \n");
//        else if(sOrder.equals("2"))
//            sbQueryData.append("    ORDER BY me.LAST_READ_DATE                      \n");
//        else if(sOrder.equals("3"))
//            sbQueryData.append("    ORDER BY me.INSTALL_DATE DESC                   \n");
//        else if(sOrder.equals("4"))
//            sbQueryData.append("    ORDER BY me.INSTALL_DATE                        \n");


        StringBuilder sb = new StringBuilder();

        sb.append("\nSELECT me.mds_id AS METER_MDS, ");
        sb.append("\n       me.meter AS METER_TYPE, ");
        sb.append("\n       mcu.sys_id AS MCU_SYS_ID, ");
        sb.append("\n       device.vendorname AS VENDOR_NAME, ");
        sb.append("\n       device.modelname AS MODEL_NAME, ");
        sb.append("\n       CASE WHEN cust.id IS NULL THEN 'N' ELSE 'Y' END HAS_CUSTOMER, ");
        sb.append("\n       me.install_date AS INSTALL_DATE, ");
        sb.append("\n       me.last_read_date AS LAST_COMM_DATE, ");
        sb.append("\n       loc.name AS LOC_NAME, ");
        sb.append("\n       CASE WHEN last_read_date >= :datePre24H THEN 0 ");
        sb.append("\n            WHEN last_read_date <  :datePre24H AND last_read_date >= :datePre48H THEN 1 ");
        sb.append("\n            WHEN last_read_date <  :datePre48H THEN 2 ");
        sb.append("\n       ELSE 9 END AS COMM_STATUS, ");
        sb.append("\n       me.switch_status AS SWITCH_STATUS, ");
        sb.append("\n       me.conditions AS COMMAND_STATUS, ");
        sb.append("\n       cont.prepaymentpowerdelay AS PREPAID_DEPOSIT, ");
        sb.append("\n       me.last_metering_value AS LAST_METERING_VALUE, ");
        sb.append("\n       me.sw_version AS SW_VERSION, ");
        sb.append("\n       me.hw_version AS HW_VERSION, ");
        sb.append("\n       cust.customerno AS CUSTOMER_NO, ");
        sb.append("\n       cust.name AS CUSTOMER_NAME, ");
        sb.append("\n       sp.name AS SUPPLIER_NAME, ");
        sb.append("\n       me.lp_interval AS LP_INTERVAL, ");
        sb.append("\n       me.pulse_constant AS PULSE_CONSTANT, ");
        sb.append("\n       me.transformer_ratio AS TRANSFORMER_RATIO ");
        sb.append("\nFROM meter me ");
        sb.append("\n     LEFT OUTER JOIN modem mo ");
        sb.append("\n     ON (me.modem_id = mo.id) ");
        sb.append("\n     LEFT OUTER JOIN mcu mcu ");
        sb.append("\n     ON (mo.mcu_id = mcu.id) ");
        sb.append("\n     LEFT OUTER JOIN location loc ");
        sb.append("\n     ON (me.location_id = loc.id) ");
        sb.append("\n     LEFT OUTER JOIN contract cont ");
        sb.append("\n     ON (me.id = cont.meter_id) ");
        sb.append("\n     LEFT OUTER JOIN customer cust ");
        sb.append("\n     ON (cust.id = cont.customer_id) ");
        sb.append("\n     LEFT OUTER JOIN ( ");
        sb.append("\n              SELECT model.id AS modelid, ");
        sb.append("\n                     model.name AS modelname, ");
        sb.append("\n                     vendor.id AS vendorid, ");
        sb.append("\n                     vendor.name AS vendorname ");
        sb.append("\n               FROM devicemodel model ");
        sb.append("\n                    LEFT OUTER JOIN devicevendor vendor ");
        sb.append("\n                    ON (model.devicevendor_id = vendor.id) ");
        sb.append("\n          ) device ");
        sb.append("\n     ON (me.devicemodel_id = device.modelid) ");
        sb.append("\n     ,supplier sp ");
        sb.append("\nWHERE me.supplier_id = :supplierId ");
        sb.append("\nAND   sp.id = me.supplier_id ");

        if (!sMeterType.isEmpty()) {
            sb.append("AND   me.meter = :meterType ");
        }

        if (!sMdsId.isEmpty()) {
            sb.append("\nAND   me.mds_id LIKE :mdsId ");
        }

        if (sStatus != null) {
            sb.append("\nAND   me.meter_status = :status ");
        }

        if (sCmdStatus.equals("R")) {
            sb.append("\nAND   me.switch_status = 0 ");
        } else if(sCmdStatus.equals("T")) {
            sb.append("\nAND   me.conditions LIKE :conditions ");
        } else if(sCmdStatus.equals("P")) {
            if (!sOperators.isEmpty() && sPrepaidDeposit != null) {
                sb.append("\nAND   cont.prepaymentpowerdelay ").append(sOperators).append(" ").append(sPrepaidDeposit);
            }
        }

        if (sMcuId != null) {
            sb.append("\nAND   mo.mcu_id = :mcuId ");
        }

        if (sLocationId != null) {
            sb.append("\nAND   me.location_id IN (:locationIdList) ");
        }

        if (!sMcuName.isEmpty()) {
            sb.append("\nAND   mcu.sys_id = :mcuName "); //LIKE '%"+ sMcuName +"%'");
        }

        if (!sConsumLocationId.isEmpty()) {
            sb.append("\nAND   cont.contract_number = :consumLocationId ");
        }

        if (sVendor != null && sVendor != 0) {
            sb.append("\nAND   device.vendorId = :vendor ");
        }

        if (sModel != null) {
            sb.append("\nAND   device.modelId = :model ");
        }

        if (!sInstallStartDate.isEmpty()) {
            sb.append("\nAND   me.install_date >= :installStartDate ");
        }

        if (!sInstallEndDate.isEmpty()) {
            sb.append("\nAND   me.install_date <= :installEndDate ");
        }

        if (sModemYN.equals("Y")) {
            sb.append("\nAND   me.modem_id IS NOT NULL");
        } else if (sModemYN.equals("N")) {
            sb.append("\nAND   me.modem_id IS NULL");
        }

        if (sCustomerYN.equals("Y")) {
            sb.append("\nAND   cont.id IS NOT NULL");
        } else if (sCustomerYN.equals("N")) {
            sb.append("\nAND   cont.id IS NULL");
        }

        if (!sLastcommStartDate.isEmpty()) {
            sb.append("\nAND   me.last_read_date >= :lastCommStartDate ");
        }

        if (!sLastcommEndDate.isEmpty()) {
            sb.append("\nAND   me.last_read_date <= :lastCommEndDate ");
        }

        if (sCommState.equals("0")) {
            sb.append("\nAND me.last_read_date >= :datePre24H ");
        } else if (sCommState.equals("1")) {
            sb.append("\nAND   me.last_read_date < :datePre24H ");
            sb.append("\nAND   me.last_read_date >= :datePre48H ");
        } else if (sCommState.equals("2")) {
            sb.append("\nAND   me.last_read_date < :datePre48H ");
        }

        if (sOrder.equals("1")) {
            sb.append("\nORDER BY me.last_read_date DESC ");
        } else if (sOrder.equals("2")) {
            sb.append("\nORDER BY me.last_read_date ");
        } else if (sOrder.equals("3")) {
            sb.append("\nORDER BY me.install_date DESC ");
        } else if (sOrder.equals("4")) {
            sb.append("\nORDER BY me.install_date ");
        }

        SQLQuery query = getSession().createSQLQuery(new SQLWrapper().getQuery(sb.toString()));

//        if(sCmdStatus.equals("T")) {
//           query.setString("conditions", "%" + MdisTamperingStatus.TAMPERING_ISSUED.getMessage() + "%");
//        }

        String datePre24H = DateTimeUtil.calcDate(Calendar.HOUR, -24, "yyyyMMddhhmmss");
        String datePre48H = DateTimeUtil.calcDate(Calendar.HOUR, -48, "yyyyMMddhhmmss");

        query.setString("datePre24H", datePre24H);
        query.setString("datePre48H", datePre48H);
        query.setInteger("supplierId", supplierId);

        if (!sMeterType.isEmpty()) {
            query.setString("meterType", sMeterType);
        }

        if (!sMdsId.isEmpty()) {
            query.setString("mdsId", "%" + sMdsId + "%");
        }

        if (sStatus != null) {
            query.setInteger("status", sStatus);
        }

        if(sCmdStatus.equals("T")) {
            query.setString("conditions", "%" + MdisTamperingStatus.TAMPERING_ISSUED.getMessage() + "%");
        }

        if (sMcuId != null) {
            query.setInteger("mcuId", sMcuId);
        }

        if (sLocationId != null) {
            query.setParameterList("locationIdList", locationIdList);
        }

        if (!sMcuName.isEmpty()) {
            query.setString("mcuName", sMcuName);
        }

        if (!sConsumLocationId.isEmpty()) {
            query.setString("consumLocationId", sConsumLocationId);
        }

        if (sVendor != null && sVendor != 0) {
            query.setInteger("vendor", sVendor);
        }

        if (sModel != null) {
            query.setInteger("model", sModel);
        }

        if (!sInstallStartDate.isEmpty()) {
            query.setString("installStartDate", sInstallStartDate + "000000");
        }

        if (!sInstallEndDate.isEmpty()) {
            query.setString("installEndDate", sInstallEndDate + "235959");
        }

        if (!sLastcommStartDate.isEmpty()) {
            query.setString("lastCommStartDate", sLastcommStartDate + "000000");
        }

        if (!sLastcommEndDate.isEmpty()) {
            query.setString("lastCommEndDate", sLastcommEndDate + "235959");
        }


//        // Paging
//        int rowPerPage = CommonConstants.Paging.ROWPERPAGE.getPageNum();
//        int firstIdx  = Integer.parseInt(curPage) * rowPerPage;

//        query.setFirstResult(firstIdx);
//        query.setMaxResults(rowPerPage);

//        List dataList = query.list();
//
//        int dataListLen = dataList != null ? dataList.size() : 0;
//        Map<String, Object> chartDataMap = null;
//        Object[] resultData = null;
//
//        for (Object obj : dataList) {
//            chartDataMap = new HashMap();
//            resultData = (Object[])obj;
//
//            chartDataMap.put("meterMds",       resultData[0]);
//            chartDataMap.put("meterType",      resultData[1]);
//            chartDataMap.put("mcuSysID",       resultData[2]);
//            chartDataMap.put("vendorName",     resultData[3]);
//            chartDataMap.put("modelName",      resultData[4]);
//            chartDataMap.put("customer",       resultData[5]);
//            chartDataMap.put("installDate",    resultData[6]);
//            chartDataMap.put("lastCommDate",   resultData[7]);
//            chartDataMap.put("locName",        resultData[8]);
//            chartDataMap.put("commStatus",     resultData[9]);
//            chartDataMap.put("meterId",        resultData[10]);
//            chartDataMap.put("switchStatus",   resultData[11]);
//            chartDataMap.put("commandStatus",  resultData[12]);
//            chartDataMap.put("prepaidDeposit", resultData[13]);
//            chartDataMap.put("lastMeteringValue", resultData[14]);
//            chartDataMap.put("deviceModelId", resultData[15]);
//            gridData.add(chartDataMap);
//        }
//
//        result.add(gridData);
        result = query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
        return result;
    }
}