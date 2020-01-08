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
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;

import javax.persistence.Lob;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.AliasToEntityMapResultTransformer;
import org.hibernate.transform.AliasedTupleSubsetResultTransformer;
import org.hibernate.transform.Transformers;
import org.hibernate.type.IntegerType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.aimir.constants.CommonConstants;
import com.aimir.constants.CommonConstants.DateType;
import com.aimir.constants.CommonConstants.DefaultChannel;
import com.aimir.constants.CommonConstants.DefaultUndefinedValue;
import com.aimir.constants.CommonConstants.DeviceType;
import com.aimir.constants.CommonConstants.DistTrfmrSubstationMeterPhase;
import com.aimir.constants.CommonConstants.GroupType;
import com.aimir.constants.CommonConstants.MeterCodes;
import com.aimir.constants.CommonConstants.MeterStatus;
import com.aimir.constants.CommonConstants.MeterType;
import com.aimir.constants.CommonConstants.ModemIFType;
import com.aimir.dao.AbstractHibernateGenericDao;
import com.aimir.dao.device.MeterDao;
import com.aimir.dao.mvm.MeteringLpDao;
import com.aimir.dao.system.CodeDao;
import com.aimir.dao.system.DeviceModelDao;
import com.aimir.dao.system.SupplierDao;
import com.aimir.model.device.Meter;
import com.aimir.model.system.Code;
import com.aimir.model.system.DeviceModel;
import com.aimir.model.system.Supplier;
import com.aimir.util.CalendarUtil;
import com.aimir.util.CommonUtils2;
import com.aimir.util.DateTimeUtil;
import com.aimir.util.DecimalUtil;
import com.aimir.util.SQLWrapper;
import com.aimir.util.StringUtil;
import com.aimir.util.TimeLocaleUtil;
import com.aimir.util.TimeUtil;


@SuppressWarnings({ "unchecked", "rawtypes" })
@Repository(value = "meterDao")
public class MeterDaoImpl extends AbstractHibernateGenericDao<Meter, Integer> implements MeterDao {

	@Autowired
    CodeDao codeDao;
	
    @Autowired
    MeteringLpDao meteringlpDao;

    @Autowired
    CodeDao codedao;

    @Autowired
    SupplierDao supplierDao;
    
	@Autowired
	DeviceModelDao deviceModelDao;

    protected static Log logger = LogFactory.getLog(MeterDaoImpl.class);

    @Autowired
    protected MeterDaoImpl(SessionFactory sessionFactory) {
        super(Meter.class);
        super.setSessionFactory(sessionFactory);
    }

    @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
    public Meter get(String mdsId) {
        Meter m = findByCondition("mdsId", mdsId);
        if (m != null) {
            Hibernate.initialize(m.getMcu());
            Hibernate.initialize(m.getMeterType());
            Hibernate.initialize(m.getMeterStatus());
        }
        return m;
    }

    /**
     * 제주 실증단지에서 사용하는 11자리 미터키
     * @param installProperty
     * @return
     */
    @Transactional(readOnly=true, propagation=Propagation.REQUIRED)
    public Meter getInstallProperty(String installProperty) {
        return findByCondition("installProperty", installProperty);
    }

    // Meter 정보 저장
    public Serializable setMeter(Meter meter) {
        return getSession().save(meter);
    }

    @Transactional(readOnly=true, propagation=Propagation.REQUIRED)
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

    @Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
    public List<Object> getMetersByMcuName(String name) {

        StringBuffer query = new StringBuffer();

        query.append(" SELECT   m.id ");
        query.append(" FROM     Meter m INNER JOIN m.modem.mcu mcu  ");
        query.append(" WHERE    mcu.sysID = :vName ");

        Query _query = getSession().createQuery(query.toString());
        _query.setString("vName",  name);
        return _query.list();
    }

    @Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
    public List<Meter> getMeters(String mcuId, Integer shortId) {

        StringBuffer query = new StringBuffer();

        query.append(" SELECT   m ");
        query.append(" FROM     Meter m INNER JOIN m.modem.mcu mcu  ");
        query.append(" WHERE    mcu.sysID = :mcuId and m.shortId = :shortId ");

        Query _query = getSession().createQuery(query.toString());
        _query.setString("mcuId",  mcuId);
        _query.setInteger("shortId",  shortId);
        return _query.list();
    }

    //검침실패한 미터 목록조회
    @Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
    public Map<String,Object> getMeteringFailureMeter(Map<String,Object> params){

        int rowPerPage  = 0;
        int firstIdx    = 0;

        Map<String,Object> resultMap = new HashMap<String,Object>();

        String searchStartDate  = StringUtil.nullToBlank(params.get("searchStartDate"));
        String searchEndDate    = StringUtil.nullToBlank(params.get("searchEndDate"));
        String meterType        = StringUtil.nullToBlank(params.get("meterType"));
        List<Integer> locations = (ArrayList<Integer>)params.get("locationId");
        String customerId       = StringUtil.nullToBlank(params.get("customerId"));
        String meterId          = StringUtil.nullToBlank(params.get("meterId"));
        String mcuId            = StringUtil.nullToBlank(params.get("mcuId"));
        if(!params.get("currPage").equals("")&&params.get("currPage") != null){
            String currPage         = StringUtil.nullToBlank(params.get("currPage"));
            rowPerPage = CommonConstants.Paging.ROWPERPAGE_20.getPageNum();
            firstIdx  = Integer.parseInt(currPage) * rowPerPage;
        }
        String supplierId       = StringUtil.nullToBlank(params.get("supplierId"));

        String installDate = searchEndDate + "235959";
        String deleteDate = searchStartDate + "235959";

        // 미터 타입별 미터링데이터 테이블 설정
        String dayTable = CommonConstants.MeterType.valueOf(meterType).getDayTableName();

        StringBuffer query = new StringBuffer();
        query.append("\nSELECT contract.contract_number AS customerId ");
        query.append("\n      ,customer.name AS customerName ");
        query.append("\n      ,CASE WHEN customer.address IS NULL THEN '' ELSE customer.address END ");
        query.append("\n          CONCAT '-' CONCAT CASE WHEN customer.address1 IS NULL THEN '' ELSE customer.address1 END ");
        query.append("\n          CONCAT ' ' CONCAT CASE WHEN customer.address2 IS NULL THEN '' ELSE customer.address2 END ");
        query.append("\n          CONCAT '' CONCAT CASE WHEN customer.address3 IS NULL THEN '' ELSE customer.address3 END AS customerAddress ");
        query.append("\n      ,m.mds_id            AS mdsId ");
        query.append("\n      ,m.id                AS meterId ");
        query.append("\n      ,m.address           AS meterAddress ");
        query.append("\n      ,modem.device_serial AS modemId ");
        query.append("\n      ,mcu.sys_id        AS mcuId ");
        query.append("\n      ,m.last_read_date    AS lastReadDate ");
        query.append("\n      ,m.meter_status      AS meterStatus ");
        query.append("\n      ,m.time_diff         AS timeDiff ");
        query.append("\nFROM meter m ");
        query.append("\n     LEFT OUTER JOIN contract contract ");
        query.append("\n     ON m.id = contract.meter_id ");
        query.append("\n     LEFT OUTER JOIN customer customer ");
        query.append("\n     ON contract.customer_id = customer.id ");
        query.append("\n     LEFT OUTER JOIN modem modem ");
        query.append("\n     ON m.modem_id = modem.id ");
        query.append("\n     LEFT OUTER JOIN mcu mcu ");
        query.append("\n     ON modem.mcu_id = mcu.id ");
        query.append("\n     LEFT OUTER JOIN code c ");
        query.append("\n     ON c.id = m.meter_status ");
        query.append("\nWHERE NOT EXISTS (SELECT 'X' ");
        query.append("\n                  FROM ").append(dayTable).append(" dt ");
        query.append("\n                  WHERE dt.mdev_type = :mdevType ");
        query.append("\n                  AND   dt.mdev_id = m.mds_id ");
        query.append("\n                  AND   dt.yyyymmdd BETWEEN :searchStartDate AND :searchEndDate ");
        query.append("\n                  AND   dt.channel = :channel ");
        if(locations != null)
        	query.append("\n                  AND   dt.location_id IN (:locationId) ");
        query.append("\n                 ) ");
        query.append("\nAND   m.meter = :meterType ");
        if(locations != null)
        	query.append("\nAND   m.location_id IN (:locationId) ");
        query.append("\nAND   m.install_date <= :installDate ");
        query.append("\nAND   (c.id IS NULL ");
        query.append("\n    OR c.code != :deleteCode ");
        query.append("\n    OR (c.code = :deleteCode AND m.delete_date > :deleteDate) ");
        query.append("\n) ");

        if (!"".equals(customerId)) {
            query.append("\nAND   contract.contract_number LIKE :customerId ");
        }
        if (!"".equals(meterId)) {
            query.append("\nAND   m.mds_id LIKE  :meterId  ");
        }
        if (!"".equals(mcuId)) {
            query.append("\nAND   mcu.sys_id LIKE :mcuId ");
        }
        if (!"".equals(supplierId)) {
            query.append("\nAND   m.supplier_id = :supplierId ");
        }

        query.append("\nGROUP BY m.id, m.mds_id, contract.contract_number, customer.name, ");
        query.append("\n         customer.address, customer.address1, customer.address2, customer.address3, ");
        query.append("\n         m.address,modem.device_serial,mcu.sys_id,m.last_read_date,m.meter_status,m.time_diff ");

        // 페이징처리를 위한 전체 데이터건수 조회 쿼리
        StringBuffer countQuery = new StringBuffer();
        countQuery.append("\n SELECT COUNT(*) ");
        countQuery.append("\n FROM (  ");
        countQuery.append(query);
        countQuery.append("\n ) y ");

        // 전체건수 조회
        SQLQuery countQueryObj = getSession().createSQLQuery(new SQLWrapper().getQuery(countQuery.toString()));
        if(locations != null)
        	countQueryObj.setParameterList("locationId", locations);
        countQueryObj.setString("searchStartDate", searchStartDate);
        countQueryObj.setString("searchEndDate", searchEndDate);
        countQueryObj.setString("meterType", meterType);
        countQueryObj.setString("installDate", installDate);
        countQueryObj.setString("mdevType", CommonConstants.DeviceType.Meter.name());
        countQueryObj.setInteger("channel", DefaultChannel.Usage.getCode());
        countQueryObj.setString("deleteCode", MeterCodes.DELETE_STATUS.getCode());
        countQueryObj.setString("deleteDate", deleteDate);

        if (!"".equals(customerId)) {
            countQueryObj.setString("customerId", "%" + customerId + "%");
        }
        if (!"".equals(meterId)) {
            countQueryObj.setString("meterId", "%" + meterId + "%");
        }
        if (!"".equals(mcuId)) {
            countQueryObj.setString("mcuId", "%" + mcuId + "%");
        }
        if (!"".equals(supplierId)) {
            countQueryObj.setInteger("supplierId", Integer.parseInt(supplierId));
        }

        //logger.debug("\nNative :");
        //logger.debug(countQueryObj.getQueryString());
        Number totalCount = (Number)countQueryObj.uniqueResult();

        resultMap.put("totalCount", totalCount.toString());

        // 페이징 조회
        SQLQuery dataQueryObj = getSession().createSQLQuery(new SQLWrapper().getQuery(query.toString()));

        if(locations != null)
        	dataQueryObj.setParameterList("locationId", locations);
        dataQueryObj.setString("searchStartDate", searchStartDate);
        dataQueryObj.setString("searchEndDate", searchEndDate);
        dataQueryObj.setString("meterType", meterType);
        dataQueryObj.setString("installDate", installDate);
        dataQueryObj.setString("mdevType", CommonConstants.DeviceType.Meter.name());
        dataQueryObj.setInteger("channel", DefaultChannel.Usage.getCode());
        dataQueryObj.setString("deleteCode", MeterCodes.DELETE_STATUS.getCode());
        dataQueryObj.setString("deleteDate", deleteDate);

        if (!"".equals(customerId)) {
            dataQueryObj.setString("customerId", "%" +customerId+ "%");
        }
        if (!"".equals(meterId)) {
            dataQueryObj.setString("meterId", "%" +meterId+ "%");
        }
        if (!"".equals(mcuId)) {
            dataQueryObj.setString("mcuId", "%" + mcuId + "%");
        }
        if (!"".equals(supplierId)) {
            dataQueryObj.setInteger("supplierId", Integer.parseInt(supplierId));
        }
        if(!params.get("currPage").equals("")&&params.get("currPage") != null){
            dataQueryObj.setFirstResult(firstIdx);
            dataQueryObj.setMaxResults(rowPerPage);
        }

        //logger.debug("\nNative :");
        //logger.debug(dataQueryObj.getQueryString());

        List<Object> resultList = dataQueryObj.list();

        resultMap.put("list", resultList);

        return resultMap;
    }

    @Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
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

            List<Code> CodeMeterType = codedao.getChildCodes(Code.METER_TYPE);

            for(int j=0;j<CodeMeterType.size();j++){
                if(resultData[1].equals(CodeMeterType.get(j).getName())){
                    if(CodeMeterType.get(j).getDescr()!=null){
                        resultData[1] = CodeMeterType.get(j).getDescr().toString();
                    }else{
                        resultData[1] = resultData[1];
                    }
                }
            }

            chartSerie.put("xField", "xTag");
            chartSerie.put("yField", "value".concat(Integer.toString(i)));
            chartSerie.put("yCode", resultData[0].toString());
            chartSerie.put("displayName", resultData[1].toString());
            chartSeries.add(chartSerie);

            sbQueryWhere.append(" , SUM(CASE WHEN LOCATION_ID = " + resultData[0].toString() + " THEN 1 ELSE 0 END) AS value" + i + " \n");
        }


        // chartData
        sbQuery = new StringBuffer();

        sbQuery.append("SELECT cd.name AS xTag \n");
        sbQuery.append("     , cd.name AS xCode \n");
        sbQuery.append(sbQueryWhere);
        sbQuery.append("FROM meter mt, \n");
        sbQuery.append("     code cd \n");
        sbQuery.append("WHERE cd.id = mt.metertype_id \n");
        sbQuery.append("AND   mt.supplier_id = :supplierId \n");
        sbQuery.append("GROUP BY cd.name \n");

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

            List<Code> CodeMeterType = codedao.getChildCodes(Code.METER_TYPE);

            for(int j=0;j<CodeMeterType.size();j++){
                if(resultData[0].equals(CodeMeterType.get(j).getName())){
                    if(CodeMeterType.get(j).getDescr()!=null){
                        resultData[0] = CodeMeterType.get(j).getDescr().toString();
                    }else{
                        resultData[0] = resultData[0];
                    }
                }
            }

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

    @Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
    public List<Object> getMiniChartMeterTypeByCommStatus(Map<String, Object> condition) {
        String supplierId = StringUtil.nullToBlank(condition.get("supplierId"));
        String gridType = StringUtil.nullToBlank(condition.get("gridType"));
        List<Integer> locationIdList = (List<Integer>)condition.get("locationIdList");

        List<Object> chartData = new ArrayList<Object>();
        List<Object> chartSeries = new ArrayList<Object>();
        List<Object> result = new ArrayList<Object>();

        StringBuilder sbQuery = new StringBuilder();

        // chartSeries
        HashMap chartSerie1 = new HashMap();
        chartSerie1.put("xField", "xTag");
        chartSerie1.put("yField", "value0");
        chartSerie1.put("yCode", "0");
        chartSerie1.put("displayName", "fmtMessage00");
        chartSeries.add(chartSerie1);

        HashMap chartSerie2 = new HashMap();
        chartSerie2.put("xField", "xTag");
        chartSerie2.put("yField", "value1");
        chartSerie2.put("yCode", "1");
        chartSerie2.put("displayName", "fmtMessage24");
        chartSeries.add(chartSerie2);

        HashMap chartSerie3 = new HashMap();
        chartSerie3.put("xField", "xTag");
        chartSerie3.put("yField", "value2");
        chartSerie3.put("yCode", "2");
        chartSerie3.put("displayName", "fmtMessage48");
        chartSeries.add(chartSerie3);

        HashMap chartSerie4 = new HashMap();
        chartSerie4.put("xField", "xTag");
        chartSerie4.put("yField", "value3");
        chartSerie4.put("yCode", "3");
        chartSerie4.put("displayName", "fmtMessage99");
        chartSeries.add(chartSerie4);
        
        HashMap chartSerie5 = new HashMap();
        chartSerie5.put("xField", "xTag");
        chartSerie5.put("yField", "value4");
        chartSerie5.put("yCode", "4");
        chartSerie5.put("displayName", "CommError");
        chartSeries.add(chartSerie5);
        
        HashMap chartSerie6 = new HashMap();
        chartSerie6.put("xField", "xTag");
        chartSerie6.put("yField", "value5");
        chartSerie6.put("yCode", "5");
        chartSerie6.put("displayName", "SecurityError");
        chartSeries.add(chartSerie6);
        HashMap chartSerie7 = new HashMap();
        chartSerie7.put("xField", "xTag");
        chartSerie7.put("yField", "value6");
        chartSerie7.put("yCode", "6");
        chartSerie7.put("displayName", "PowerDown");
        chartSeries.add(chartSerie7);
        
        // chartData
        sbQuery.append("\n SELECT meter AS xTag, ");
        sbQuery.append("\n       meter AS xCode, ");
        sbQuery.append("       SUM(CASE WHEN me.LAST_READ_DATE  >= :datePre24H \n");
        sbQuery.append("                 AND NOT me.METER_STATUS = :commError \n");
        sbQuery.append("                 AND NOT me.METER_STATUS = :powerDown \n");
        sbQuery.append("                 AND NOT me.METER_STATUS = :securityError THEN 1 ELSE 0 END) AS value0 \n");
        sbQuery.append("     , SUM(CASE WHEN me.LAST_READ_DATE < :datePre24H \n");
        sbQuery.append("                 AND me.LAST_READ_DATE >= :datePre48H \n");
        sbQuery.append("                 AND NOT me.METER_STATUS = :commError \n");
        sbQuery.append("                 AND NOT me.METER_STATUS = :powerDown \n");
        sbQuery.append("                 AND NOT me.METER_STATUS = :securityError THEN 1 ELSE 0 END) AS value1 \n");
        sbQuery.append("     , SUM(CASE WHEN me.LAST_READ_DATE < :datePre48H \n");
        sbQuery.append("                 AND NOT me.METER_STATUS = :commError \n");
        sbQuery.append("                 AND NOT me.METER_STATUS = :powerDown \n");
        sbQuery.append("                 AND NOT me.METER_STATUS = :securityError THEN 1 ELSE 0 END) AS value2 \n");
        sbQuery.append("     , SUM(CASE WHEN me.LAST_READ_DATE IS NULL \n");
        sbQuery.append("                 AND NOT me.METER_STATUS = :commError \n");
        sbQuery.append("                 AND NOT me.METER_STATUS = :powerDown \n");
        sbQuery.append("                 AND NOT me.METER_STATUS = :securityError THEN 1 ELSE 0 END) AS value3 \n");
        sbQuery.append("     , SUM(CASE WHEN me.METER_STATUS = :commError THEN 1 ELSE 0 END) AS value4 \n");
        sbQuery.append("     , SUM(CASE WHEN me.METER_STATUS = :securityError THEN 1 ELSE 0 END) AS value5 \n");
        sbQuery.append("     , SUM(CASE WHEN me.METER_STATUS = :powerDown THEN 1 ELSE 0 END) AS value6 \n");
        sbQuery.append("\n FROM meter me LEFT OUTER JOIN code co");
        sbQuery.append("\n ON (me.meter_status = co.id)");
        sbQuery.append("\n WHERE me.supplier_id = :supplierId");
        sbQuery.append("\nAND (me.meter_status is null or co.code <> :deleteCode)");
        sbQuery.append("\nGROUP BY me.meter ");
        
        if (locationIdList != null && locationIdList.size() > 0) {
            sbQuery.append("\nAND   location_id IN (:locationIdList)  ");
        }
        
        List dataList = null;

        SQLQuery query = getSession().createSQLQuery(sbQuery.toString());

        String datePre24H = DateTimeUtil.calcDate(Calendar.HOUR, -24, "yyyyMMddHHmmss");
        String datePre48H = DateTimeUtil.calcDate(Calendar.HOUR, -48, "yyyyMMddHHmmss");
        int securityError = codedao.getCodeIdByCode("1.3.3.13");
        int commError = codedao.getCodeIdByCode("1.3.3.14");
        int powerDown = codedao.getCodeIdByCode("1.3.3.5");
        
        query.setString("datePre24H", datePre24H);
        query.setString("datePre48H", datePre48H);
        query.setInteger("securityError", securityError);	// 1.3.3.13
        query.setInteger("commError", commError);			// 1.3.3.14
        query.setInteger("powerDown", powerDown);			// 1.3.3.5
        query.setString("deleteCode", MeterCodes.DELETE_STATUS.getCode());

        query.setInteger("supplierId", Integer.parseInt(supplierId));
        if (locationIdList != null && locationIdList.size() > 0) {
            query.setParameterList("locationIdList", locationIdList);
        }

        dataList = query.list();

        int dataListLen = 0;
        if (dataList != null)
            dataListLen = dataList.size();

        String meterType = null;

        DecimalFormat dfMd = null;
        Supplier supplier = supplierDao.get(Integer.parseInt(supplierId));
        if(supplier != null) {
        	dfMd = DecimalUtil.getMDStyle(supplier.getMd());
        }

        for (int i = 0; i < dataListLen; i++) {

            HashMap chartDataMap = new HashMap();
            Object[] resultData = (Object[]) dataList.get(i);
            meterType = null;
            List<Code> CodeMeterType = codedao.getChildCodes(Code.METER_TYPE);

            for (int j = 0; j < CodeMeterType.size(); j++) {
                if (resultData[0].equals(CodeMeterType.get(j).getName())) {
                    if (CodeMeterType.get(j).getDescr() != null) {
//                        resultData[0] = CodeMeterType.get(j).getDescr().toString();
                        meterType = CodeMeterType.get(j).getDescr().toString();
                    } else {
//                        resultData[0] = resultData[0];
                        meterType = (String)resultData[0];
                    }
                    break;
                }
            }

            if (meterType == null) {
                meterType = DefaultUndefinedValue.UNKNOWN.getName(); 
            }

//          chartDataMap.put("xTag", resultData[0]);
            chartDataMap.put("xTag", meterType);
            chartDataMap.put("xCode", resultData[1]);

            int resultDataLen = resultData.length;
            
            if(gridType.equals("extjs")) {
	            for (int j = 2; j < resultDataLen; j++) {
	                chartDataMap.put("value".concat(Integer.toString(j - 2)), resultData[j] == null ? "0" : dfMd.format(resultData[j]));
	            }
            }else { /* mini gadget - chart Data  fusionchart 값 오류 때문에 decimal 없앰 */
            	for (int j = 2; j < resultDataLen; j++) {
	                chartDataMap.put("value".concat(Integer.toString(j - 2)), resultData[j] == null ? "0" : resultData[j]);
	            }
            }

            chartData.add(chartDataMap);
        }

        result.add(chartData);
        result.add(chartSeries);

        return result;
    }

    @Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
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
            String DisResultData = null;
            //List<String> DisResultData = new ArrayList();

            List<Code> CodeMeterType = codedao.getChildCodes(Code.METER_TYPE);

            for(int j=0;j<CodeMeterType.size();j++){
                if(resultData.equals(CodeMeterType.get(j).getName())){
                    if(CodeMeterType.get(j).getDescr()!=null){
                        DisResultData = CodeMeterType.get(j).getDescr().toString();
                    }else{
                        DisResultData = resultData;
                    }
                }
            }

            chartSerie.put("xField", "xTag");
            chartSerie.put("yField", "value".concat(Integer.toString(i)));
            chartSerie.put("yCode", resultData);
            chartSerie.put("displayName", DisResultData);


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

    @Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
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

        String datePre24H = DateTimeUtil.calcDate(Calendar.HOUR, -24, "yyyyMMddHHmmss");
        String datePre48H = DateTimeUtil.calcDate(Calendar.HOUR, -48, "yyyyMMddHHmmss");

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

    @Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
    public List<Object> getMiniChartCommStatusByMeterType(Map<String, Object> condition) {
        return getMiniChartCommStatusByMeterType(condition, null);
    }

    @Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
    public List<Object> getMiniChartCommStatusByMeterType(Map<String, Object> condition, String[] messageCommAlert) {
    	String supplierId = StringUtil.nullToBlank(condition.get("supplierId"));
        List<Integer> locationIdList = (List<Integer>)condition.get("locationIdList");

        List<Object> chartData = new ArrayList<Object>();
        List<Object> chartSeries = new ArrayList<Object>();
        List<Object> result = new ArrayList<Object>();

        StringBuilder sbQuery = new StringBuilder();
        StringBuilder sbQueryWhere = new StringBuilder();

        // chartSeries
        sbQuery.append("\nSELECT meter ");
        sbQuery.append("\nFROM meter ");
        if (locationIdList != null) {
            sbQuery.append("\nWHERE location_id IN (:locationIdList) ");
        }
        sbQuery.append("\nGROUP BY meter ");
        sbQuery.append("\nORDER BY meter ");

        List yCodeList = null;

        SQLQuery query = getSession().createSQLQuery(sbQuery.toString());
        if (locationIdList != null) {
            query.setParameterList("locationIdList", locationIdList);
        }
        yCodeList = query.list();

        int yCodeLen = 0;
        if (yCodeList != null)
            yCodeLen = yCodeList.size();

        for (int i = 0; i < yCodeLen; i++) {
            HashMap chartSerie = new HashMap();
            String resultData = (String) yCodeList.get(i);
            String DisResultData = null;
            // List<String> DisResultData = new ArrayList();

            List<Code> CodeMeterType = codedao.getChildCodes(Code.METER_TYPE);

            for (int j = 0; j < CodeMeterType.size(); j++) {
                if (resultData.equals(CodeMeterType.get(j).getName())) {
                    if (CodeMeterType.get(j).getDescr() != null) {
                        DisResultData = CodeMeterType.get(j).getDescr().toString();
                    } else {
                        DisResultData = resultData;
                    }
                }
            }
            
            if (DisResultData == null) {
                DisResultData = DefaultUndefinedValue.UNKNOWN.getName();
            }

            chartSerie.put("xField", "xTag");
            chartSerie.put("yField", "value".concat(Integer.toString(i)));
            chartSerie.put("yCode", resultData);
            chartSerie.put("displayName", DisResultData);

            chartSeries.add(chartSerie);

            sbQueryWhere.append("\n      ,SUM(CASE WHEN me.meter = '" + resultData + "' THEN 1 ELSE 0 END) AS value" + i + " ");
        }

        // chartData
        sbQuery = new StringBuilder();

        sbQuery.append("\nSELECT me.commStatus AS xTag ");
        sbQuery.append("\n      ,me.commStatus AS xCode ");
        sbQuery.append(sbQueryWhere);
        sbQuery.append("\nFROM (SELECT meter, ");
        sbQuery.append("\n             CASE WHEN last_read_date >= :datePre24H ");
        sbQuery.append("                 AND NOT me.METER_STATUS =" + codedao.getCodeIdByCode("1.3.3.14") +"\n");
        sbQuery.append("                 AND NOT me.METER_STATUS =" + codedao.getCodeIdByCode("1.3.3.5") +"\n");
        sbQuery.append("                 AND NOT me.METER_STATUS =" + codedao.getCodeIdByCode("1.3.3.13") + "THEN '0' \n");
        sbQuery.append("\n                  WHEN last_read_date <  :datePre24H ");
        sbQuery.append("\n                       AND last_read_date >= :datePre48H ");
        sbQuery.append("                 AND NOT me.METER_STATUS =" + codedao.getCodeIdByCode("1.3.3.14") +"\n");
        sbQuery.append("                 AND NOT me.METER_STATUS =" + codedao.getCodeIdByCode("1.3.3.5") +"\n");
        sbQuery.append("                 AND NOT me.METER_STATUS =" + codedao.getCodeIdByCode("1.3.3.13") + "THEN '1' \n");
        sbQuery.append("\n                  WHEN last_read_date < :datePre48H ");
        sbQuery.append("                 AND NOT me.METER_STATUS =" + codedao.getCodeIdByCode("1.3.3.14") +"\n");
        sbQuery.append("                 AND NOT me.METER_STATUS =" + codedao.getCodeIdByCode("1.3.3.5") +"\n");
        sbQuery.append("                 AND NOT me.METER_STATUS =" + codedao.getCodeIdByCode("1.3.3.13") + "THEN '2' \n");
        sbQuery.append("\n                  WHEN last_read_date IS NULL ");
        sbQuery.append("                 AND NOT me.METER_STATUS =" + codedao.getCodeIdByCode("1.3.3.14") +"\n");
        sbQuery.append("                 AND NOT me.METER_STATUS =" + codedao.getCodeIdByCode("1.3.3.5") +"\n");
        sbQuery.append("                 AND NOT me.METER_STATUS =" + codedao.getCodeIdByCode("1.3.3.13") + "THEN '3' \n");
        sbQuery.append("\n                  WHEN me.METER_STATUS =" + codedao.getCodeIdByCode("1.3.3.14") + " THEN '4' \n");
        sbQuery.append("\n                  WHEN me.METER_STATUS =" + codedao.getCodeIdByCode("1.3.3.13") + " THEN '5' \n");
        sbQuery.append("\n                  WHEN me.METER_STATUS =" + codedao.getCodeIdByCode("1.3.3.5") + " THEN '6' END as commStatus \n");
        sbQuery.append("\n      FROM meter me");
        sbQuery.append("\n  LEFT OUTER JOIN code co  ");
        sbQuery.append("\n  ON ( me.meter_status = co.ID)  ");
        sbQuery.append("\n      WHERE supplier_id = :supplierId ");
        sbQuery.append("\n  AND (co.NAME  <> 'Delete' or co.NAME  IS NULL)  ");
        if (locationIdList != null) {
            sbQuery.append("\n      AND   location_id IN (:locationIdList) ");
        }
        sbQuery.append("\n) me ");
        sbQuery.append("\nGROUP BY me.commStatus ");
        sbQuery.append("\nORDER BY me.commStatus ");

        List dataList = null;

        query = getSession().createSQLQuery(new SQLWrapper().getQuery(sbQuery.toString()));

        String datePre24H = DateTimeUtil.calcDate(Calendar.HOUR, -24, "yyyyMMddHHmmss");
        String datePre48H = DateTimeUtil.calcDate(Calendar.HOUR, -48, "yyyyMMddHHmmss");

        query.setString("datePre24H", datePre24H);
        query.setString("datePre48H", datePre48H);
        query.setInteger("supplierId", Integer.parseInt(supplierId));
        if (locationIdList != null) {
            query.setParameterList("locationIdList", locationIdList);
        }

        dataList = query.list();

        int dataListLen = 0;
        if (dataList != null)
            dataListLen = dataList.size();

        String fmtMessage00 = (messageCommAlert != null && messageCommAlert.length > 0) ? messageCommAlert[0] : "fmtMessage00";
        String fmtMessage24 = (messageCommAlert != null && messageCommAlert.length > 0) ? messageCommAlert[1] : "fmtMessage24";
        String fmtMessage48 = (messageCommAlert != null && messageCommAlert.length > 0) ? messageCommAlert[2] : "fmtMessage48";
        String fmtMessage99 = (messageCommAlert != null && messageCommAlert.length > 0) ? messageCommAlert[3] : "fmtMessage99";
        
        DecimalFormat dfMd = null;
        Supplier supplier = supplierDao.get(Integer.parseInt(supplierId));
        if(supplier != null) {
        	dfMd = DecimalUtil.getMDStyle(supplier.getMd());
        }

        for (int i = 0; i < dataListLen && i < 4; i++) {
            HashMap chartDataMap = new HashMap();
            Object[] resultData = (Object[]) dataList.get(i);

            chartDataMap.put("xCode", resultData[1].toString());
            if (chartDataMap.get("xCode").equals("0")) {
                chartDataMap.put("xTag", fmtMessage00);
            } else if (chartDataMap.get("xCode").equals("1")) {
                chartDataMap.put("xTag", fmtMessage24);
            } else if (chartDataMap.get("xCode").equals("2")) {
                chartDataMap.put("xTag", fmtMessage48);
            } else if (chartDataMap.get("xCode").equals("3")) {
                chartDataMap.put("xTag", fmtMessage99);
            } else if (chartDataMap.get("xCode").equals("4")) {
                chartDataMap.put("xTag", "CommError");
            } else if (chartDataMap.get("xCode").equals("5")) {
                chartDataMap.put("xTag", "SecurityError");
            } else if (chartDataMap.get("xCode").equals("6")) {
                chartDataMap.put("xTag", "PowerDown");
            }

            int resultDataLen = resultData.length;
            for (int j = 2; j < resultDataLen; j++) {
                chartDataMap.put("value".concat(Integer.toString(j - 2)), resultData[j]==null? "0" : dfMd.format(resultData[j]));
            }

            chartData.add(chartDataMap);
        }

        result.add(chartData);
        result.add(chartSeries);

        return result;
    }

    @Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
    public List<Object> getMiniChartCommStatusByLocation(Map<String, Object> condition, String[] arrFmtmessagecommalert)
    {
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

        String datePre24H = DateTimeUtil.calcDate(Calendar.HOUR, -24, "yyyyMMddHHmmss");
        String datePre48H = DateTimeUtil.calcDate(Calendar.HOUR, -48, "yyyyMMddHHmmss");

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
            {
                //chartDataMap.put("xTag", "fmtMessage00");
                chartDataMap.put("xTag", arrFmtmessagecommalert[0]);


            }
            else if(chartDataMap.get("xCode").equals("1"))
            {
                //chartDataMap.put("xTag", "fmtMessage24");
                chartDataMap.put("xTag", arrFmtmessagecommalert[1]);

            }
            else
            {
                //chartDataMap.put("xTag", "fmtMessage48");
                chartDataMap.put("xTag", arrFmtmessagecommalert[2]);
            }






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

    @Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
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

        String datePre24H = DateTimeUtil.calcDate(Calendar.HOUR, -24, "yyyyMMddHHmmss");
        String datePre48H = DateTimeUtil.calcDate(Calendar.HOUR, -48, "yyyyMMddHHmmss");

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
    
    @Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
    public String getMcuIdFromMdsId(String mdsId) {
    	StringBuffer query = new StringBuffer();
    	query.append("SELECT mcu.sys_id AS mcuId FROM METER me ");
    	query.append("LEFT OUTER JOIN MODEM mo ");
    	query.append("ON ( me.MODEM_ID = mo.ID) ");
    	query.append("LEFT OUTER JOIN MCU mcu ");
    	query.append("ON ( mo.MCU_ID = mcu.ID) ");
    	query.append("WHERE me.MDS_ID = :mdsId");
    	String result = null;
    	
    	SQLQuery sql = getSession().createSQLQuery(new SQLWrapper().getQuery(query.toString()));
    	sql.setString("mdsId", mdsId);
    	List resultList = sql.list();
    	
    	if (resultList != null && resultList.size() ==1 && resultList.get(0) != null) {
    		result = resultList.get(0).toString();
    	}
    	return result;
    }

    @Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
    public List<Object> getMeterSearchChart(Map<String, Object> condition){

        List<Object> gridData    = new ArrayList<Object>();
        List<Object> chartData   = new ArrayList<Object>();
        List<Object> result      = new ArrayList<Object>();
        StringBuffer sbQuery     = new StringBuffer();

        String sMeterType         = StringUtil.nullToBlank(condition.get("sMeterType"));
        String sMdsId             = StringUtil.nullToBlank(condition.get("sMdsId"));
        String sDeviceSerial      = StringUtil.nullToBlank(condition.get("sDeviceSerial"));
        String sStatus            = StringUtil.nullToBlank(condition.get("sStatus"));
        String sMeterGroup        = StringUtil.nullToBlank(condition.get("sMeterGroup"));
        String sMeterAddress      = StringUtil.nullToBlank(condition.get("sMeterAddress"));

        String sMcuId             = StringUtil.nullToBlank(condition.get("sMcuId"));
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

        String sCommState         = StringUtil.nullToBlank(condition.get("sCommState"));
        String isManualMeter      = StringUtil.nullToBlank(condition.get("isManualMeter"));
        String sCustomerId        = StringUtil.nullToBlank(condition.get("sCustomerId"));
        String sCustomerName      = StringUtil.nullToBlank(condition.get("sCustomerName"));

        String supplierId         = StringUtil.nullToBlank(condition.get("supplierId"));
        Integer page              = (Integer)condition.get("page");
        Integer limit             = (Integer)condition.get("limit");
        Integer start             = (page != null) ? ((page - 1) * limit) : 0;
        List<Integer> locationIdList = (List<Integer>)condition.get("locationIdList");
        Boolean isExcel = condition.get("isExcel") == null ? false : (Boolean)condition.get("isExcel");

        sbQuery.append("FROM METER me \n")
               .append("     LEFT OUTER JOIN MODEM mo \n")
               .append("     ON ( me.MODEM_ID = mo.ID) \n")
               .append("     LEFT OUTER JOIN MCU mcu \n")
               .append("     ON ( mo.MCU_ID  = mcu.ID) \n")
               .append("     LEFT OUTER JOIN LOCATION loc \n")
               .append("     ON ( me.LOCATION_ID = loc.ID) \n")
               .append("     LEFT OUTER JOIN devicemodel model \n")
               .append("     ON me.devicemodel_id = model.id \n")
               .append("     LEFT OUTER JOIN devicevendor vendor \n")
               .append("     ON model.devicevendor_id = vendor.id \n")

               .append("     LEFT OUTER JOIN contract contract \n")  // cont, contract 동일한 Contract 테이블이 중복으로 outer join 걸려있음
               .append("     ON me.id = contract.meter_id \n")
               .append("     LEFT OUTER JOIN customer customer \n")
               .append("     ON contract.customer_id = customer.id \n")
               .append("     LEFT OUTER JOIN CODE code ON me.METER_STATUS = code.id \n");
               if(!"".equals(sMeterGroup)) {
                   sbQuery.append("     LEFT OUTER JOIN group_member gm \n")
                   .append("     ON me.mds_id = gm.member \n")
                   .append("     LEFT OUTER JOIN aimirgroup ag \n")
                   .append("     ON gm.group_id = ag.id \n");
               }

               sbQuery.append("WHERE me.SUPPLIER_ID = :supplierId \n")
               .append("AND (code.name <> 'Delete' or code.name is null) \n");

        //sMeterGroup -> 그룹 아이디
        if (!sMeterGroup.equals("")) {
            sbQuery.append("AND gm.group_id = "+ sMeterGroup + " \n");
        }

        if (!sMeterType.equals(""))
            sbQuery.append("AND me.METER = '"+ sMeterType +"' \n");

        if (!sMdsId.equals("")) {
            sbQuery.append("AND me.mds_ID LIKE '"+ sMdsId +"%' \n");
        }

        // meter address -> customer address로 변경한다. 스파사 요청
        if (!sMeterAddress.equals("")) {
            //sbQuery.append("     AND me.address LIKE '%"+ sMeterAddress +"%'");
            sbQuery.append("     AND ( (customer.address  LIKE  '%"+ sMeterAddress +"%') ")
            .append(" 				OR (customer.address1 LIKE  '%"+ sMeterAddress +"%') ")
            .append(" 				OR (customer.address2 LIKE  '%"+ sMeterAddress +"%') ")
            .append(" 				OR (customer.address3 LIKE  '%"+ sMeterAddress +"%') ) ");
        }

        if (!sStatus.equals(""))
            sbQuery.append("AND me.METER_STATUS = "+ sStatus + " \n");

        if (!sMcuId.equals(""))
            sbQuery.append("AND mo.MCU_ID LIKE '"+ sMcuId +"%' \n");

        if (!sDeviceSerial.equals(""))
            sbQuery.append("AND mo.DEVICE_SERIAL LIKE '"+ sDeviceSerial +"%' \n");
        
        if (locationIdList != null)
            sbQuery.append("AND me.LOCATION_ID IN (:locationIdList) \n");

        if (!sMcuName.equals(""))
            sbQuery.append("AND mcu.SYS_ID LIKE '" + sMcuName+ "%' \n");

        if (!sConsumLocationId.equals(""))
            sbQuery.append("AND contract.CONTRACT_NUMBER like '"+ sConsumLocationId +"%' \n");

        if (!sVendor.equals("0") && !sVendor.equals("")) {
            sbQuery.append("AND vendor.id = "+ sVendor + " \n");
        }

        if (!sModel.equals("")) {
            sbQuery.append("AND model.id = "+ sModel + " \n");
        }

        if (!sInstallStartDate.equals(""))
            sbQuery.append("AND me.INSTALL_DATE >= '"+ sInstallStartDate +"000000' \n");

        if (!sInstallEndDate.equals(""))
            sbQuery.append("AND me.INSTALL_DATE <= '"+ sInstallEndDate +"235900' \n");

        if (sModemYN.equals("Y"))
            sbQuery.append("AND me.MODEM_ID IS NOT NULL \n");
        else if (sModemYN.equals("N"))
            sbQuery.append("AND me.MODEM_ID IS NULL \n");

        if (sCustomerYN.equals("Y"))
            sbQuery.append("AND contract.ID IS NOT NULL \n");
        else if (sCustomerYN.equals("N"))
            sbQuery.append("AND contract.ID IS NULL \n");

        if (!sLastcommStartDate.equals(""))
            sbQuery.append("AND me.LAST_READ_DATE >= '"+ sLastcommStartDate +"000000' \n");

        if (!sLastcommEndDate.equals(""))
            sbQuery.append("AND me.LAST_READ_DATE <= '"+ sLastcommEndDate +"235900' \n");

        if (sCommState.equals("0"))
            sbQuery.append("AND me.LAST_READ_DATE  >= :datePre24H \n");
        else if (sCommState.equals("1"))
            sbQuery.append("AND LAST_READ_DATE < :datePre24H AND LAST_READ_DATE >= :datePre48H \n");
        else if (sCommState.equals("2"))
            sbQuery.append("AND LAST_READ_DATE < :datePre48H \n");

        // 매뉴얼 미터 여부
        if (!isManualMeter.isEmpty()) {
            sbQuery.append("AND is_manual_meter = " + Integer.parseInt(isManualMeter) + " \n");
        }

        if (!"".equals(sCustomerId)) {
            sbQuery.append("AND customer.CUSTOMERNO like '" + sCustomerId +"%' \n");
        }
        if (!"".equals(sCustomerName)) {
            sbQuery.append("AND customer.NAME like '" + sCustomerName + "%' \n");
        }

        int dataListLen = 0;
        SQLQuery query = null;
        List dataList = null;
        String datePre24H = DateTimeUtil.calcDate(Calendar.DATE, -1, "yyyyMMddHHmmss");
        String datePre48H = DateTimeUtil.calcDate(Calendar.DATE, -2, "yyyyMMddHHmmss");

        // ChartGrid
        if (page != null || isExcel) {
            StringBuilder sbQueryGridInner = new StringBuilder();
            StringBuilder sbQueryGrid = new StringBuilder();
            StringBuilder sbQueryGridCount = new StringBuilder();
            
            sbQueryGridInner.append("SELECT CASE WHEN mcu.SYS_ID IS NULL THEN '-' ELSE mcu.SYS_ID end AS mcuSysId \n")
                       .append("     , CASE WHEN mo.MCU_ID IS NULL THEN COUNT(MODEM) ELSE COUNT(mo.MCU_ID) END AS totalCnt \n")
                       .append("     , SUM(CASE WHEN me.LAST_READ_DATE  >= :datePre24H \n")
                       .append("                 AND NOT me.METER_STATUS =" + codedao.getCodeIdByCode("1.3.3.14") +"\n")
                       .append("                 AND NOT me.METER_STATUS =" + codedao.getCodeIdByCode("1.3.3.5") +"\n")
                       .append("                 AND NOT me.METER_STATUS =" + codedao.getCodeIdByCode("1.3.3.13") + "THEN 1 ELSE 0 END) AS value0 \n")
                       .append("     , SUM(CASE WHEN me.LAST_READ_DATE < :datePre24H \n")
                       .append("                 AND me.LAST_READ_DATE >= :datePre48H \n")
                       .append("                 AND NOT me.METER_STATUS =" + codedao.getCodeIdByCode("1.3.3.14") +"\n")
                       .append("                 AND NOT me.METER_STATUS =" + codedao.getCodeIdByCode("1.3.3.5") +"\n")
                       .append("                 AND NOT me.METER_STATUS =" + codedao.getCodeIdByCode("1.3.3.13") + "THEN 1 ELSE 0 END) AS value1 \n")
                       .append("     , SUM(CASE WHEN me.LAST_READ_DATE < :datePre48H \n")
                       .append("                 AND NOT me.METER_STATUS =" + codedao.getCodeIdByCode("1.3.3.14") +"\n")
                       .append("                 AND NOT me.METER_STATUS =" + codedao.getCodeIdByCode("1.3.3.5") +"\n")
                       .append("                 AND NOT me.METER_STATUS =" + codedao.getCodeIdByCode("1.3.3.13") + "THEN 1 ELSE 0 END) AS value2 \n")
                       .append("     , SUM(CASE WHEN me.LAST_READ_DATE IS NULL \n")
                       .append("                 AND NOT me.METER_STATUS =" + codedao.getCodeIdByCode("1.3.3.14") +"\n")
                       .append("                 AND NOT me.METER_STATUS =" + codedao.getCodeIdByCode("1.3.3.5") +"\n")
                       .append("                 AND NOT me.METER_STATUS =" + codedao.getCodeIdByCode("1.3.3.13") + "THEN 1 ELSE 0 END) AS value3 \n")
                       .append("     , SUM(CASE WHEN me.METER_STATUS =" + codedao.getCodeIdByCode("1.3.3.14") + " THEN 1 ELSE 0 END) AS value4 \n")
                       .append("     , SUM(CASE WHEN me.METER_STATUS =" + codedao.getCodeIdByCode("1.3.3.13") + " THEN 1 ELSE 0 END) AS value5 \n")
                       .append("     , SUM(CASE WHEN me.METER_STATUS =" + codedao.getCodeIdByCode("1.3.3.5") + " THEN 1 ELSE 0 END) AS value6 \n")
                       .append("     , CASE WHEN mcu.SYS_ID IS NULL THEN 1 ELSE 0 end AS orderCount \n")
                       .append(sbQuery)
                       .append("GROUP BY mcu.SYS_Id, mo.MCU_ID \n");

            // Grid
            sbQueryGrid.append(sbQueryGridInner);
            sbQueryGrid.append("ORDER BY orderCount ASC, totalCnt DESC, mcu.sys_id \n");

            query = getSession().createSQLQuery(new SQLWrapper().getQuery(sbQueryGrid.toString()));
            query.setString("datePre24H", datePre24H);
            query.setString("datePre48H", datePre48H);
            query.setInteger("supplierId", Integer.parseInt(supplierId));
            
            if (locationIdList != null) {
                query.setParameterList("locationIdList", locationIdList);
            }

			if (!isExcel) {
				query.setFirstResult(start);
				query.setMaxResults(limit);
			}
            
            dataList = query.list();

            DecimalFormat dfMd = DecimalUtil.getMDStyle(supplierDao.get(Integer.parseInt(supplierId)).getMd());

            // 실제 데이터
            dataListLen = 0;
            if (dataList != null)
                dataListLen = dataList.size();

            for (int i = 0; i < dataListLen; i++) {
                HashMap gridDataMap = new HashMap();
                Object[] resultData = (Object[]) dataList.get(i);

                gridDataMap.put("no", dfMd.format(start + i + 1));
                gridDataMap.put("mcuSysID", resultData[0]);
                gridDataMap.put("value0", resultData[2] == null ? "0" : dfMd.format(resultData[2]));
                gridDataMap.put("value1", resultData[3] == null ? "0" : dfMd.format(resultData[3]));
                gridDataMap.put("value2", resultData[4] == null ? "0" : dfMd.format(resultData[4]));
                gridDataMap.put("value3", resultData[5] == null ? "0" : dfMd.format(resultData[5]));
                gridDataMap.put("value4", resultData[6] == null ? "0" : dfMd.format(resultData[6]));
                gridDataMap.put("value5", resultData[7] == null ? "0" : dfMd.format(resultData[7]));
                gridDataMap.put("value6", resultData[8] == null ? "0" : dfMd.format(resultData[8]));

                gridData.add(gridDataMap);
            }
            result.add(gridData);

            // Count
            sbQueryGridCount.append("SELECT COUNT(*) AS cnt FROM ( \n");
            sbQueryGridCount.append(sbQueryGridInner);
            sbQueryGridCount.append(") t \n");

            query = getSession().createSQLQuery(new SQLWrapper().getQuery(sbQueryGridCount.toString()));

            query.setString("datePre24H", datePre24H);
            query.setString("datePre48H", datePre48H);
            query.setInteger("supplierId", Integer.parseInt(supplierId));
            if (locationIdList != null) {
                query.setParameterList("locationIdList", locationIdList);
            }

            Number count = (Number)query.uniqueResult();
            result.add(count.intValue());
        } else {
            // Chart Data
            StringBuilder sbQueryChart = new StringBuilder();
            sbQueryChart.append("SELECT '0' AS commStatus \n")
                        .append("     , COUNT(me.ID) AS cnt \n")
                        .append(sbQuery)
                        .append("AND me.LAST_READ_DATE  >= :datePre24H \n")
                        .append("AND NOT me.METER_STATUS = " + codedao.getCodeIdByCode("1.3.3.14") + "\n")
                        .append("AND NOT me.METER_STATUS = " + codedao.getCodeIdByCode("1.3.3.13") + "\n")
                        .append("AND NOT me.METER_STATUS = " + codedao.getCodeIdByCode("1.3.3.5") + "\n")
                        .append("UNION ALL \n")

                        .append("SELECT '1' AS commStatus \n")
                        .append("     , COUNT(me.ID) AS cnt \n")
                        .append(sbQuery)
                        .append("AND me.LAST_READ_DATE < :datePre24H \n")
                        .append("AND me.LAST_READ_DATE >= :datePre48H \n")
                        .append("AND NOT me.METER_STATUS = " + codedao.getCodeIdByCode("1.3.3.14") + "\n")
                        .append("AND NOT me.METER_STATUS = " + codedao.getCodeIdByCode("1.3.3.13") + "\n")
                        .append("AND NOT me.METER_STATUS = " + codedao.getCodeIdByCode("1.3.3.5") + "\n")
                        .append("UNION ALL \n")

                        .append("SELECT '2' AS commStatus \n")
                        .append("     , COUNT(me.ID) AS cnt \n")
                        .append(sbQuery)
                        .append("AND me.LAST_READ_DATE < :datePre48H \n")
                        .append("AND NOT me.METER_STATUS = " + codedao.getCodeIdByCode("1.3.3.14") + "\n")
                        .append("AND NOT me.METER_STATUS = " + codedao.getCodeIdByCode("1.3.3.13") + "\n")
                        .append("AND NOT me.METER_STATUS = " + codedao.getCodeIdByCode("1.3.3.5") + "\n")
                        .append("UNION ALL \n")

                        .append("SELECT '3' AS commStatus \n")
                        .append("     , COUNT(me.ID) AS cnt \n")
                        .append(sbQuery)
                        .append("AND me.LAST_READ_DATE IS NULL \n")
                        .append("AND NOT me.METER_STATUS = " + codedao.getCodeIdByCode("1.3.3.14") + "\n")
                        .append("AND NOT me.METER_STATUS = " + codedao.getCodeIdByCode("1.3.3.13") + "\n")
                        .append("AND NOT me.METER_STATUS = " + codedao.getCodeIdByCode("1.3.3.5") + "\n")
                        .append("UNION ALL \n")
                        
            			.append("SELECT '4' AS commStatus \n")
            			.append("     , COUNT(me.ID) AS cnt \n")
            			.append(sbQuery)
            			.append("AND me.METER_STATUS = " + codedao.getCodeIdByCode("1.3.3.14") + "\n")
            			.append("UNION ALL \n")
            			
           				.append("SELECT '5' AS commStatus \n")
           				.append("     , COUNT(me.ID) AS cnt \n")
           				.append(sbQuery)
           				.append("AND me.METER_STATUS = " + codedao.getCodeIdByCode("1.3.3.13") + "\n")
           				.append("UNION ALL \n")
           				
           				.append("SELECT '6' AS commStatus \n")
           				.append("     , COUNT(me.ID) AS cnt \n")
           				.append(sbQuery)
           				.append("AND me.METER_STATUS = " + codedao.getCodeIdByCode("1.3.3.5") + "\n");

            query = getSession().createSQLQuery(new SQLWrapper().getQuery(sbQueryChart.toString()));

            query.setString("datePre24H", datePre24H);
            query.setString("datePre48H", datePre48H);
            query.setInteger("supplierId", Integer.parseInt(supplierId));
            if (locationIdList != null) {
                query.setParameterList("locationIdList", locationIdList);
            }

            dataList = query.list();

            // 실제 데이터
            dataListLen = 0;
            if (dataList != null)
                dataListLen = dataList.size();

            for (int i = 0; i < dataListLen; i++) {
                HashMap chartDataMap = new HashMap();
                Object[] resultData = (Object[]) dataList.get(i);

                if (resultData[0].toString().equals("0")) {
                    chartDataMap.put("label", "fmtMessage");
                    chartDataMap.put("data", resultData[1]);
                } else if (resultData[0].toString().equals("1")) {
                    chartDataMap.put("label", "fmtMessage24");
                    chartDataMap.put("data", resultData[1]);
                } else if (resultData[0].toString().equals("2")) {
                    chartDataMap.put("label", "fmtMessage48");
                    chartDataMap.put("data", resultData[1]);
                } else if (resultData[0].toString().equals("3")) {
                    chartDataMap.put("label", "fmtMessage99"); 
                    chartDataMap.put("data", resultData[1]);
                } else if (resultData[0].toString().equals("4")) {
                    chartDataMap.put("label", "CommError");
                    chartDataMap.put("data", resultData[1]);
                } else if (resultData[0].toString().equals("5")) {
                    chartDataMap.put("label", "SecurityError");
                    chartDataMap.put("data", resultData[1]);
                }else if (resultData[0].toString().equals("6")) {
                    chartDataMap.put("label", "PowerDown");
                    chartDataMap.put("data", resultData[1]);
                }
                chartData.add(chartDataMap);
            }

            result.add(chartData);
        }

        return result;
    }

    /**
     *
     * meter grid fetch from model.
     */
    @Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
    public List<Object> getMeterSearchGrid(Map<String, Object> condition){

        List<Object> gridData    = new ArrayList<Object>();
        List<Object> allGridData    = new ArrayList<Object>();
        List<Object> result      = new ArrayList<Object>();
        StringBuffer sbQuery     = new StringBuffer();

        String sMeterType         = StringUtil.nullToBlank(condition.get("sMeterType"));
        String sMdsId             = StringUtil.nullToBlank(condition.get("sMdsId"));
        String sStatus            = StringUtil.nullToBlank(condition.get("sStatus"));
        String sMeterGroup        = StringUtil.nullToBlank(condition.get("sMeterGroup"));
        String sMeterAddress      = StringUtil.nullToBlank(condition.get("sMeterAddress"));

        String sMcuId             = StringUtil.nullToBlank(condition.get("sMcuId"));
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
        String sGroupOndemandYN   = StringUtil.nullToBlank(condition.get("sGroupOndemandYN"));

        String supplierId         = StringUtil.nullToBlank(condition.get("supplierId"));

        String isManualMeter      = StringUtil.nullToBlank(condition.get("isManualMeter"));
        String limit          = StringUtil.nullToBlank(condition.get("limit"));

        String sCustomerId = StringUtil.nullToBlank(condition.get("sCustomerId"));
        String sCustomerName = StringUtil.nullToBlank(condition.get("sCustomerName"));
        
        String sHwVersion = StringUtil.nullToBlank(condition.get("sHwVersion"));
        String sFwVersion = StringUtil.nullToBlank(condition.get("sFwVersion"));
        String sGs1 = StringUtil.nullToBlank(condition.get("sGs1"));
        String sType = StringUtil.nullToBlank(condition.get("sType"));
        String fwGadget = StringUtil.nullToBlank(condition.get("fwGadget"));
        
        String sMbusSMYN = StringUtil.nullToBlank(condition.get("sMbusSMYN"));
        String sDeviceSerial = StringUtil.nullToBlank(condition.get("sDeviceSerial"));
        
        String sNotDeleted = StringUtil.nullToBlank(condition.get("sNotDeleted"));
        
        List<Integer> locationIdList = (List<Integer>)condition.get("locationIdList");

        sbQuery.append("  SELECT me.MDS_ID            AS meterMDS              		 \n")
               .append("       , me.METER             AS meterType             		 \n")
               .append("       , mcu.SYS_ID           AS mcuSysID      		         \n")
               .append("       , vendor.name          AS vendorName     		     \n")
               .append("       , model.name           AS modelName      		     \n")
               .append("       , contract.ID          AS customer               	 \n")
               .append("       , me.INSTALL_DATE      AS installDate            	 \n")
               .append("       , me.LAST_READ_DATE    AS lastCommDate          	 	 \n")
               .append("       , loc.NAME             AS locName               		 \n")
               .append("       , CASE WHEN LAST_READ_DATE >= :datePre24H THEN '0'    \n")
               .append("              WHEN LAST_READ_DATE <  :datePre24H             \n")
               .append("               AND LAST_READ_DATE >= :datePre48H THEN '1'    \n")
               .append("            WHEN LAST_READ_DATE <  :datePre48H THEN '2'      \n")
               .append("            ELSE '9'                                   		 \n")
               .append("            END               AS State                 		 \n")
               .append("       , me.ID                AS meterId               		 \n")
               .append("       , modemdevice.name     AS modemModelName       		 \n")
               .append("       , contract.contract_number AS contractNumber     	 \n")

               // 커스터머 정보 추가
               .append("       , customer.CUSTOMERNO  AS customerId          		 \n")
               .append("       , customer.NAME        AS customerName         		 \n")
               .append("       , me.INSTALL_PROPERTY  AS installProperty     		 \n")
               .append("       , co.descr 		      AS meterStatus   				 \n")
               .append("       , me.INSTALL_ID        AS installId               	 \n")
               .append("       , me.CT                AS ct                     	 \n")
               .append("       , customer.ADDRESS     AS customerAddr            	 \n")
               .append("       , customer.ADDRESS1    AS customerAddr1          	 \n")
               .append("       , customer.ADDRESS2    AS customerAddr2          	 \n")
               .append("       , customer.ADDRESS3    AS customerAddr3          	 \n")
               .append("       , me.TRANSFORMER_RATIO AS transformerRatio       	 \n")
               .append("       , me.address AS meterAddress 						 \n")
               .append("       , co.code              AS meterStatusByCode   		 \n")
               .append("       , me.SW_VERSION AS SW_VERSION   						 \n")
               .append("       , me.HW_VERSION AS HW_VERSION   						 \n")
               .append("       , mo.DEVICE_SERIAL AS modemId   						 \n")
               .append("       , loc.ID             AS locId                		 \n")
               
               .append("       , me.gs1               	AS gs1                		 \n")
               .append("       , me.MANUFACTURED_DATE	AS manufacturedDate    		 \n")

               .append("       FROM METER me            		                     \n")
               .append("       LEFT OUTER JOIN MODEM mo         	                 \n")
               .append("         ON ( me.MODEM_ID = mo.ID)           	             \n")
               .append("       LEFT OUTER JOIN MCU mcu                   	         \n")
               .append("         ON ( mo.MCU_ID = mcu.ID)                      		 \n")
               .append("       LEFT OUTER JOIN LOCATION loc                    		 \n")
               .append("         ON ( me.LOCATION_ID = loc.ID)                 		 \n")
               .append("       LEFT OUTER JOIN CODE co                    		  	 \n")
               .append("         ON ( me.meter_status = co.ID)                  	 \n")

               .append("       LEFT OUTER JOIN devicemodel model 					 \n")
               .append("       ON me.devicemodel_id = model.id 						 \n")
               .append("       LEFT OUTER JOIN devicevendor vendor 					 \n")
               .append("       ON model.devicevendor_id = vendor.id 				 \n")
               .append("       LEFT OUTER JOIN devicemodel modemdevice 				 \n")
               .append("       ON mo.devicemodel_id = modemdevice.id 				 \n")

               // add by eunmiae to get modem's model
               .append("        LEFT OUTER JOIN CONTRACT contract            		 \n")
               .append("          ON (me.ID = contract.METER_ID)         			 \n")

               //(kskim) 커스터머 정보 추가.
               .append("       LEFT OUTER JOIN CUSTOMER customer          			 \n")
               .append("       ON ( contract.CUSTOMER_ID = customer.ID )  			 \n");
               if (!sMeterGroup.equals("")) {
            	   sbQuery.append("       LEFT OUTER JOIN group_member gm 			 \n")
	               .append("       ON me.mds_id = gm.member 						 \n")
	               .append("       LEFT OUTER JOIN aimirgroup ag 					 \n")
	               .append("       ON gm.group_id = ag.id							 \n");
               }
               
               // 
               if (!sMbusSMYN.equals("")){
                   sbQuery.append("       LEFT OUTER JOIN MBUS_SLAVE_IO_MODULE  slvmo   \n")
                          .append("       ON ( me.ID = slvmo.METER_ID)                  \n");
               }
               sbQuery.append("       WHERE me.SUPPLIER_ID = :supplierId             \n");
        //sMeterGroup -> 그룹 아이디
        if (!sMeterGroup.equals("")) {
            sbQuery.append("     AND gm.group_id = "+ sMeterGroup );
        }

        if (!sMeterType.equals(""))
            sbQuery.append("     AND me.METER = '"+ sMeterType +"'");
        
        if (!sHwVersion.equals(""))
            sbQuery.append("     AND me.HW_VERSION = '"+ sHwVersion +"'"); //여기
        
        if (!sFwVersion.equals(""))
            sbQuery.append("     AND me.SW_VERSION = '"+ sFwVersion +"'");
        
        if(fwGadget.equals("Y")){
        	StringTokenizer st = new StringTokenizer(sMdsId, ", ");
            String deviceIds="";
            for(int i = 0 ; st.hasMoreTokens() ; i++){
            	deviceIds += ("(0,'" + st.nextToken() +"'),");
    		}
            if(deviceIds.contains(",")){
            	deviceIds = deviceIds.substring(0, deviceIds.length()-1);
            }
            if(!deviceIds.equals(""))
            	sbQuery.append("     AND (0, me.mds_ID) IN ("+ deviceIds + ") ");
        }else{
        	if (!sMdsId.equals("")) {
                sbQuery.append("     AND me.mds_ID LIKE '"+ sMdsId +"%'");
            }
        	if (!sDeviceSerial.equals("")) {
                sbQuery.append("     AND mo.DEVICE_SERIAL LIKE '"+ sDeviceSerial +"%'");
            }
        	
        }

        // meter address -> customer address로 변경한다. 스파사 요청
        if (!sMeterAddress.equals("")) {
            //sbQuery.append("     AND me.address LIKE '%"+ sMeterAddress +"%'");
            sbQuery.append("     AND ( (LOWER(customer.address)  LIKE  '%"+ sMeterAddress.toLowerCase() +"%') ")
            .append(" 				OR (LOWER(customer.address1) LIKE  '%"+ sMeterAddress.toLowerCase() +"%') ")
            .append(" 				OR (LOWER(customer.address2) LIKE  '%"+ sMeterAddress.toLowerCase() +"%') ")
            .append(" 				OR (LOWER(customer.address3) LIKE  '%"+ sMeterAddress.toLowerCase() +"%') ) ");
        }

        if (!sStatus.equals(""))
            sbQuery.append("     AND me.METER_STATUS = "+ sStatus);

// DELETE START SP-827
//        else{
//        	sbQuery.append("  AND (co.NAME <> 'Delete' or co.NAME IS NULL)");
//        }
// DELETE END SP-827
        
        // SP-1008 filter for deleted status
        if (!sNotDeleted.equals("")) {
        	sbQuery.append("  AND (co.NAME <> 'Delete' or co.NAME IS NULL)");
        }

        if (!sMcuId.equals(""))
            sbQuery.append("     AND mo.MCU_ID LIKE '"+ sMcuId + "%'");

        if (locationIdList != null)
            sbQuery.append("     AND me.LOCATION_ID IN (:locationIdList)");

        if(!sMcuName.equals("")){
            if(sMcuName.equals("-")){
                sbQuery.append("     AND mcu.SYS_ID IS NULL");                
            }else{
                sbQuery.append("     AND mcu.SYS_ID like '" + sMcuName+ "'");
            }
        }

        if (!sConsumLocationId.equals(""))
            sbQuery.append("     AND contract.CONTRACT_NUMBER like '"+ sConsumLocationId +"%' " );

        if (!sVendor.equals("0") && !sVendor.equals("")) {
            sbQuery.append("     AND vendor.id = "+ sVendor );
        }

        if (!sModel.equals("")) {
            sbQuery.append("     AND model.id = "+ sModel );
        }

        if (!sInstallStartDate.equals(""))
            sbQuery.append("     AND me.INSTALL_DATE >= '"+ sInstallStartDate +"000000'");

        if (!sInstallEndDate.equals(""))
            sbQuery.append("     AND me.INSTALL_DATE <= '"+ sInstallEndDate +"235959'");


        if (sModemYN.equals("Y"))
            sbQuery.append("     AND me.MODEM_ID IS NOT NULL");
        else if (sModemYN.equals("N"))
            sbQuery.append("     AND me.MODEM_ID IS NULL");

        if (sCustomerYN.equals("Y"))
            sbQuery.append("     AND contract.ID IS NOT NULL");
        else if (sCustomerYN.equals("N"))
            sbQuery.append("     AND contract.ID IS NULL");


        if (!sLastcommStartDate.equals(""))
            sbQuery.append("     AND me.LAST_READ_DATE >= '"+ sLastcommStartDate +"000000'");

        if (!sLastcommEndDate.equals(""))
            sbQuery.append("     AND me.LAST_READ_DATE <= '"+ sLastcommEndDate +"235959'");

        if (sCommState.equals("0"))
            sbQuery.append("     AND me.LAST_READ_DATE  >= :datePre24H \n");
        else if (sCommState.equals("1"))
            sbQuery.append("     AND LAST_READ_DATE < :datePre24H " +
                           "     AND LAST_READ_DATE >= :datePre48H \n");
        else if (sCommState.equals("2"))
            sbQuery.append("     AND LAST_READ_DATE < :datePre48H ");

        // 매뉴얼 미터 여부
        if (!isManualMeter.isEmpty()) {
            sbQuery.append("     AND is_manual_meter = " + Integer.parseInt(isManualMeter) + " ");
        }

        if (!"".equals(sCustomerId)) {
            sbQuery.append("    AND customer.CUSTOMERNO like '" + sCustomerId +"%'");
        }
        if (!"".equals(sCustomerName)) {
            sbQuery.append("    AND customer.NAME like '" + sCustomerName + "%'");
        }
        if (!"".equals(sGs1)) {
            sbQuery.append("    AND me.gs1 like '%" + sGs1 + "%'");
        }
        if (!"".equals(sType)) {
            if(sType.equals(ModemIFType.RF.name()))
            	sbQuery.append("    AND mo.MODEM_TYPE = 'SubGiga' ");
            else if(sType.equals(ModemIFType.Ethernet.name())){
            	sbQuery.append("    AND mo.MODEM_TYPE = 'MMIU' ");
            	sbQuery.append("    AND (mo.PROTOCOL_TYPE = 'IP' or mo.PROTOCOL_TYPE = 'LAN')");
            }else if(sType.equals(ModemIFType.MBB.name())){
            	sbQuery.append("    AND mo.MODEM_TYPE = 'MMIU' ");
            	sbQuery.append("    AND (mo.PROTOCOL_TYPE = 'SMS' or mo.PROTOCOL_TYPE = 'GPRS') ");
            }
        }
        if (!sMbusSMYN.equals("")){
            if ( sMbusSMYN.equals("Y")){
                sbQuery.append("    AND slvmo.METER_ID is not NULL ");
            }
            else {
                sbQuery.append("    AND slvmo.METER_ID is  NULL ");
            }
        }
        StringBuffer sbQueryData = new StringBuffer();
        sbQueryData.append(sbQuery);

        if (sOrder.equals("1"))
            sbQueryData.append("    ORDER BY me.LAST_READ_DATE DESC, me.MDS_ID      \n");//TEMP
            //sbQueryData.append("    ORDER BY me.MDS_ID      \n");
        else if (sOrder.equals("2"))
            sbQueryData.append("    ORDER BY me.LAST_READ_DATE, me.MDS_ID           \n");
            //sbQueryData.append("    ORDER BY me.MDS_ID      \n");
        else if (sOrder.equals("3"))
            sbQueryData.append("    ORDER BY me.INSTALL_DATE DESC, me.MDS_ID        \n");
        else if (sOrder.equals("4"))
            sbQueryData.append("    ORDER BY me.INSTALL_DATE, me.MDS_ID              \n");

        SQLQuery query = getSession().createSQLQuery(new SQLWrapper().getQuery(sbQueryData.toString()));

        String datePre24H = DateTimeUtil.calcDate(Calendar.HOUR, -24, "yyyyMMddHHmmss");
        String datePre48H = DateTimeUtil.calcDate(Calendar.HOUR, -48, "yyyyMMddHHmmss");

        query.setString("datePre24H", datePre24H);
        query.setString("datePre48H", datePre48H);
        query.setInteger("supplierId", Integer.parseInt(supplierId));
        if (locationIdList != null) {
            query.setParameterList("locationIdList", locationIdList);
        }

        // Paging
        int rowPerPage = 10;
        if (!limit.isEmpty()) {
            rowPerPage = Integer.parseInt(limit);
        }
        else {
            rowPerPage = CommonConstants.Paging.ROWPERPAGE.getPageNum();
        }

        int firstIdx  = Integer.parseInt(curPage) * rowPerPage;

        String excelList            = StringUtil.nullToBlank(condition.get("excelList"));
        if (excelList == "") {
            query.setFirstResult(firstIdx);
            query.setMaxResults(rowPerPage);
        } else {
            firstIdx = 1;
        }

        List dateList = null;
        dateList = query.list();

        List allDateList = null;
        if ("Y".equals(sGroupOndemandYN)) {
            //그룹검침시 리스트 생성
            SQLQuery query2 = getSession().createSQLQuery(new SQLWrapper().getQuery(sbQueryData.toString()));

            query2.setString("datePre24H", datePre24H);
            query2.setString("datePre48H", datePre48H);
            query2.setInteger("supplierId", Integer.parseInt(supplierId));
            if (locationIdList != null) {
                query2.setParameterList("locationIdList", locationIdList);
            }

            allDateList = query2.list();
        }

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
        if (locationIdList != null) {
            countQueryObj.setParameterList("locationIdList", locationIdList);
        }

        Number totalCount = (Number)countQueryObj.uniqueResult();

        result.add(totalCount.toString());

        // 실제 데이터
        int dataListLen = 0;
        if (dateList != null)
            dataListLen= dateList.size();

        for (int i = 0; i < dataListLen; i++) {
            HashMap chartDataMap = new HashMap();
            Object[] resultData = (Object[]) dateList.get(i);

            List<Code> CodemeterType = codedao.getChildCodes(Code.METER_TYPE);

            for (int j = 0; j < CodemeterType.size(); j++) {
                if (resultData[1].equals(CodemeterType.get(j).getName())) {
                    if (CodemeterType.get(j).getDescr() != null) {
                        resultData[1] = CodemeterType.get(j).getDescr().toString();
                    } else {
                        resultData[1] = resultData[1];
                    }
                }
            }

            chartDataMap.put("no",           totalCount.intValue() -i - firstIdx );
            chartDataMap.put("meterMds",     resultData[0]);
            chartDataMap.put("meterType",    resultData[1]);
            chartDataMap.put("mcuSysID",      resultData[2]);
            chartDataMap.put("vendorName",   resultData[3]);
            chartDataMap.put("modelName",    resultData[4]);

            if (resultData[5] != null)
                chartDataMap.put("customer",     "Y");
            else
                chartDataMap.put("customer",     "N");

            chartDataMap.put("installDate",  resultData[6]);
            chartDataMap.put("lastCommDate", resultData[7]);
            if (resultData[7] != null){
                if(Long.parseLong((resultData[7].toString())) >= Long.parseLong(datePre24H))
                    chartDataMap.put("activityStatus", "A24h");
                else if(Long.parseLong((resultData[7].toString())) < Long.parseLong(datePre24H) && Long.parseLong((resultData[7].toString())) >= Long.parseLong(datePre48H))
                    chartDataMap.put("activityStatus", "NA24h");
                else if(Long.parseLong((resultData[7].toString())) < Long.parseLong(datePre48H))
                    chartDataMap.put("activityStatus", "NA48h");
            }else{
                chartDataMap.put("activityStatus", "unknown");
            }
            chartDataMap.put("locName",      resultData[8]);
            
            if (resultData[16] != null) {
            	chartDataMap.put("commStatus", resultData[16]);
            } else {
            	chartDataMap.put("commStatus", "");
            }
           
            chartDataMap.put("meterId",      resultData[10]);
            chartDataMap.put("modemModelName",      resultData[11]);
            chartDataMap.put("contractNumber",      resultData[12]);

            //customer infomation
            chartDataMap.put("customerId", resultData[13]);
            chartDataMap.put("customerName", resultData[14]);
            chartDataMap.put("installProperty", resultData[15]);
            chartDataMap.put("installId", resultData[17]);
            chartDataMap.put("ct", resultData[18]);
            chartDataMap.put("customerAddr", (resultData[19] == null ? "" : StringUtil.nullToBlank(resultData[19]) + " ")
                    +(resultData[20] == null ? "" : StringUtil.nullToBlank(resultData[20])+" ")
                    +(resultData[21] == null ? "" : StringUtil.nullToBlank(resultData[21])+" ") 
                    +(resultData[22] == null ? "" : StringUtil.nullToBlank(resultData[22])));
            chartDataMap.put("address", StringUtil.nullToBlank(resultData[19]));
            chartDataMap.put("address1", StringUtil.nullToBlank(resultData[20]));
            chartDataMap.put("address2", StringUtil.nullToBlank(resultData[21]));
            chartDataMap.put("address3", StringUtil.nullToBlank(resultData[22]));
            chartDataMap.put("transformerRatio", resultData[23]);
            chartDataMap.put("meterAddress", resultData[24]);
            if (resultData[25] != null) {
                chartDataMap.put("commStatusByCode", resultData[25]);
            } else {
                chartDataMap.put("commStatusByCode", "");
            }
            chartDataMap.put("ver", StringUtil.nullToBlank(resultData[26]) + " / " + StringUtil.nullToBlank(resultData[27]));
            chartDataMap.put("modemId", StringUtil.nullToBlank(resultData[28]));
            chartDataMap.put("swVer", StringUtil.nullToBlank(resultData[26]));
            chartDataMap.put("hwVer", StringUtil.nullToBlank(resultData[27]));
            chartDataMap.put("locId", StringUtil.nullToBlank(resultData[29]));
            chartDataMap.put("gs1", StringUtil.nullToBlank(resultData[30]));
            chartDataMap.put("manufacturedDate", StringUtil.nullToBlank(resultData[31]));
            
            gridData.add(chartDataMap);
        }
       
        result.add(gridData);

        // 그룹검침 전체 데이터
        int dataListLen2 = 0;
        if (allDateList != null)
            dataListLen2= allDateList.size();

        for (int i = 0; i < dataListLen2; i++) {

            HashMap chartDataMap = new HashMap();
            Object[] resultData = (Object[]) allDateList.get(i);

            chartDataMap.put("no",           totalCount.intValue() -i - firstIdx );
            chartDataMap.put("meterMds",     resultData[0]);
            chartDataMap.put("meterType",    resultData[1]);
            chartDataMap.put("mcuSysID",      resultData[2]);
            chartDataMap.put("vendorName",   resultData[3]);
            chartDataMap.put("modelName",    resultData[4]);

            if (resultData[5] != null)
                chartDataMap.put("customer",     "Y");
            else
                chartDataMap.put("customer",     "N");

            chartDataMap.put("installDate",  resultData[6]);
            chartDataMap.put("lastCommDate", resultData[7]);
            chartDataMap.put("locName",      resultData[8]);

            if(resultData[16] != null){
            	chartDataMap.put("commStatus", resultData[16]);
            }else{
            	chartDataMap.put("commStatus", "");
            }
         
            chartDataMap.put("meterId",      resultData[10]);
            chartDataMap.put("modemModelName",      resultData[11]);
            chartDataMap.put("contractNumber",      resultData[12]);
            chartDataMap.put("installId",      resultData[17]);
            chartDataMap.put("ct", resultData[18]);
            chartDataMap.put("customerAddr", (resultData[19] == null ? "" : StringUtil.nullToBlank(resultData[19]) + " ")
                    +(resultData[20] == null ? "" : StringUtil.nullToBlank(resultData[20])+" ")
                    +(resultData[21] == null ? "" : StringUtil.nullToBlank(resultData[21])+" ") 
                    +(resultData[22] == null ? "" : StringUtil.nullToBlank(resultData[22])));
            chartDataMap.put("transformerRatio", resultData[23]);
            chartDataMap.put("meterAddress", resultData[24]);
            if (resultData[25] != null) {
                chartDataMap.put("commStatusByCode", resultData[25]);
            } else {
                chartDataMap.put("commStatusByCode", "");
            } 
            allGridData.add(chartDataMap);
        }

        result.add(allGridData);

        return result;
    }

    /**
     * Same as getMeterSearchGrid but return the simple information only
     * @param condition same as getMeterSearchGrid
     * @return mdsId, deviceSerial, model (3 items)
     */
    @Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
    public List<Object> getSimpleMeterSearchGrid(Map<String, Object> condition){

        List<Object> gridData    = new ArrayList<Object>();
        List<Object> result      = new ArrayList<Object>();
        StringBuffer sbQuery     = new StringBuffer();

        String sMeterType         = StringUtil.nullToBlank(condition.get("sMeterType"));
        String sMdsId             = StringUtil.nullToBlank(condition.get("sMdsId"));
        String sStatus            = StringUtil.nullToBlank(condition.get("sStatus"));
        String sMeterGroup        = StringUtil.nullToBlank(condition.get("sMeterGroup"));
        String sMeterAddress      = StringUtil.nullToBlank(condition.get("sMeterAddress"));

        String sMcuId             = StringUtil.nullToBlank(condition.get("sMcuId"));
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
        String sGroupOndemandYN   = StringUtil.nullToBlank(condition.get("sGroupOndemandYN"));

        String supplierId         = StringUtil.nullToBlank(condition.get("supplierId"));

        String isManualMeter      = StringUtil.nullToBlank(condition.get("isManualMeter"));
        String limit          = StringUtil.nullToBlank(condition.get("limit"));

        String sCustomerId = StringUtil.nullToBlank(condition.get("sCustomerId"));
        String sCustomerName = StringUtil.nullToBlank(condition.get("sCustomerName"));

        String sHwVersion = StringUtil.nullToBlank(condition.get("sHwVersion"));
        String sFwVersion = StringUtil.nullToBlank(condition.get("sFwVersion"));

        String datePre24H = DateTimeUtil.calcDate(Calendar.HOUR, -24, "yyyyMMddHHmmss");
        String datePre48H = DateTimeUtil.calcDate(Calendar.HOUR, -48, "yyyyMMddHHmmss");

        List<Integer> locationIdList = (List<Integer>)condition.get("locationIdList");

        sbQuery.append("  SELECT me.MDS_ID           AS meterMDS            \n")
                .append("       , model.name         AS modelName           \n")
                .append("       , mo.DEVICE_SERIAL   AS modemId             \n")
                .append("       , mcu.SYS_ID         AS mcuSysID            \n")
                .append("       , me.ID              AS meterId             \n")
                .append("       FROM METER me                               \n")
                .append("       LEFT OUTER JOIN MODEM mo                    \n")
                .append("         ON ( me.MODEM_ID = mo.ID)                 \n")
                .append("       LEFT OUTER JOIN MCU mcu                     \n")
                .append("         ON ( mo.MCU_ID = mcu.ID)                  \n")
                .append("       LEFT OUTER JOIN LOCATION loc                \n")
                .append("         ON ( me.LOCATION_ID = loc.ID)             \n")
                .append("       LEFT OUTER JOIN CODE co                     \n")
                .append("         ON ( me.meter_status = co.ID)             \n")
                .append("       LEFT OUTER JOIN devicemodel model           \n")
                .append("         ON me.devicemodel_id = model.id           \n")
                .append("       LEFT OUTER JOIN devicevendor vendor         \n")
                .append("         ON model.devicevendor_id = vendor.id      \n")
                .append("       LEFT OUTER JOIN devicemodel modemdevice     \n")
                .append("         ON mo.devicemodel_id = modemdevice.id     \n")
// add by eunmiae to get modem's model
                .append("        LEFT OUTER JOIN CONTRACT contract          \n")
                .append("          ON (me.ID = contract.METER_ID)           \n")
//(kskim) 커스터머 정보 추가.
                .append("       LEFT OUTER JOIN CUSTOMER customer           \n")
                .append("         ON ( contract.CUSTOMER_ID = customer.ID ) \n");
        if (!sMeterGroup.equals("")) {
        sbQuery.append("        LEFT OUTER JOIN group_member gm             \n")
                .append("         ON me.mds_id = gm.member                  \n")
                .append("       LEFT OUTER JOIN aimirgroup ag               \n")
                .append("         ON gm.group_id = ag.id                    \n");
        }
        sbQuery.append("       WHERE me.SUPPLIER_ID = :supplierId           \n");
        //sMeterGroup -> 그룹 아이디
        if (!sMeterGroup.equals("")) {
            sbQuery.append("     AND gm.group_id = "+ sMeterGroup );
        }
        if (!sMeterType.equals(""))
            sbQuery.append("     AND me.METER = '"+ sMeterType +"'");

        if (!sHwVersion.equals(""))
            sbQuery.append("     AND me.HW_VERSION = '"+ sHwVersion +"'"); //여기

        if (!sFwVersion.equals(""))
            sbQuery.append("     AND me.SW_VERSION = '"+ sFwVersion +"'");

        if (!sMdsId.equals("")) {
            sbQuery.append("     AND me.mds_ID LIKE '"+ sMdsId +"%'");
        }

        // meter address -> customer address로 변경한다. 스파사 요청
        if (!sMeterAddress.equals("")) {
            sbQuery.append("     AND ( (LOWER(customer.address)  LIKE  '%"+ sMeterAddress.toLowerCase() +"%') ")
                    .append(" 				OR (LOWER(customer.address1) LIKE  '%"+ sMeterAddress.toLowerCase() +"%') ")
                    .append(" 				OR (LOWER(customer.address2) LIKE  '%"+ sMeterAddress.toLowerCase() +"%') ")
                    .append(" 				OR (LOWER(customer.address3) LIKE  '%"+ sMeterAddress.toLowerCase() +"%') ) ");
        }

        if (!sStatus.equals(""))
            sbQuery.append("     AND me.METER_STATUS = "+ sStatus);
        else{
            sbQuery.append("  AND (co.NAME <> 'Delete' or co.NAME IS NULL)");
        }

        if (!sMcuId.equals(""))
            sbQuery.append("     AND mo.MCU_ID LIKE '"+ sMcuId + "%'");

        if (locationIdList != null)
            sbQuery.append("     AND me.LOCATION_ID IN (:locationIdList)");

        if(!sMcuName.equals("")){
            if(sMcuName.equals("-")){
                sbQuery.append("     AND mcu.SYS_ID IS NULL");
            }else{
                sbQuery.append("     AND mcu.SYS_ID like '" + sMcuName+ "%'");
            }
        }

        if (!sConsumLocationId.equals(""))
            sbQuery.append("     AND contract.CONTRACT_NUMBER like '"+ sConsumLocationId +"%' " );

        if (!sVendor.equals("0") && !sVendor.equals("")) {
            sbQuery.append("     AND vendor.id = "+ sVendor );
        }

        if (!sModel.equals("")) {
            sbQuery.append("     AND model.id = "+ sModel );
        }

        if (!sInstallStartDate.equals(""))
            sbQuery.append("     AND me.INSTALL_DATE >= '"+ sInstallStartDate +"000000'");

        if (!sInstallEndDate.equals(""))
            sbQuery.append("     AND me.INSTALL_DATE <= '"+ sInstallEndDate +"235959'");


        if (sModemYN.equals("Y"))
            sbQuery.append("     AND me.MODEM_ID IS NOT NULL");
        else if (sModemYN.equals("N"))
            sbQuery.append("     AND me.MODEM_ID IS NULL");

        if (sCustomerYN.equals("Y"))
            sbQuery.append("     AND contract.ID IS NOT NULL");
        else if (sCustomerYN.equals("N"))
            sbQuery.append("     AND contract.ID IS NULL");


        if (!sLastcommStartDate.equals(""))
            sbQuery.append("     AND me.LAST_READ_DATE >= '"+ sLastcommStartDate +"000000'");

        if (!sLastcommEndDate.equals(""))
            sbQuery.append("     AND me.LAST_READ_DATE <= '"+ sLastcommEndDate +"235959'");

        if (sCommState.equals("0"))
            sbQuery.append("     AND me.LAST_READ_DATE  >= '"+ datePre24H +"' \n");
        else if (sCommState.equals("1"))
            sbQuery.append("     AND LAST_READ_DATE < '"+ datePre24H +"' " +
                    "     AND LAST_READ_DATE >= '"+ datePre48H +"' \n");
        else if (sCommState.equals("2"))
            sbQuery.append("     AND LAST_READ_DATE < '"+ datePre48H +"' ");

        // 매뉴얼 미터 여부
        if (!isManualMeter.isEmpty()) {
            sbQuery.append("     AND is_manual_meter = " + Integer.parseInt(isManualMeter) + " ");
        }

        if (!"".equals(sCustomerId)) {
            sbQuery.append("    AND customer.CUSTOMERNO like '" + sCustomerId +"%'");
        }
        if (!"".equals(sCustomerName)) {
            sbQuery.append("    AND customer.NAME like '" + sCustomerName + "%'");
        }

        StringBuffer sbQueryData = new StringBuffer();
        sbQueryData.append(sbQuery);

        if (sOrder.equals("1"))
            sbQueryData.append("    ORDER BY me.LAST_READ_DATE DESC, me.MDS_ID      \n");
        else if (sOrder.equals("2"))
            sbQueryData.append("    ORDER BY me.LAST_READ_DATE, me.MDS_ID           \n");
        else if (sOrder.equals("3"))
            sbQueryData.append("    ORDER BY me.INSTALL_DATE DESC, me.MDS_ID        \n");
        else if (sOrder.equals("4"))
            sbQueryData.append("    ORDER BY me.INSTALL_DATE, me.MDS_ID              \n");

        SQLQuery query = getSession().createSQLQuery(new SQLWrapper().getQuery(sbQueryData.toString()));

        query.setInteger("supplierId", Integer.parseInt(supplierId));
        if (locationIdList != null) {
            query.setParameterList("locationIdList", locationIdList);
        }

        // Paging
        int rowPerPage = 10;
        if (!limit.isEmpty()) {
            rowPerPage = Integer.parseInt(limit);
        }
        else {
            rowPerPage = CommonConstants.Paging.ROWPERPAGE.getPageNum();
        }

        int firstIdx = 0;
        if (!"Y".equals(sGroupOndemandYN)){
            firstIdx  = Integer.parseInt(curPage) * rowPerPage;
            query.setFirstResult(firstIdx);
            query.setMaxResults(rowPerPage);
        }

        List dataList = null;
        dataList = query.list();

        // 전체 건수
        StringBuffer countQuery = new StringBuffer();
        countQuery.append("\n SELECT COUNT(countTotal.meterMDS) ");
        countQuery.append("\n FROM (  ");
        countQuery.append(sbQuery);
        countQuery.append("\n ) countTotal ");

        SQLQuery countQueryObj = getSession().createSQLQuery(new SQLWrapper().getQuery(countQuery.toString()));

        countQueryObj.setInteger("supplierId", Integer.parseInt(supplierId));
        if (locationIdList != null) {
            countQueryObj.setParameterList("locationIdList", locationIdList);
        }

        Number totalCount = (Number)countQueryObj.uniqueResult();
        result.add(totalCount.toString());

        // 실제 데이터
        int dataListLen = 0;
        if (dataList != null)
            dataListLen= dataList.size();

        for (int i = 0; i < dataListLen; i++) {
            HashMap chartDataMap = new HashMap();
            Object[] resultData = (Object[]) dataList.get(i);

            /*List<Code> CodemeterType = codedao.getChildCodes(Code.METER_TYPE);

            for (int j = 0; j < CodemeterType.size(); j++) {
                if (resultData[1].equals(CodemeterType.get(j).getName())) {
                    if (CodemeterType.get(j).getDescr() != null) {
                        resultData[1] = CodemeterType.get(j).getDescr().toString();
                    } else {
                        resultData[1] = resultData[1];
                    }
                }
            }*/

            chartDataMap.put("no",           totalCount.intValue() -i - firstIdx );
            chartDataMap.put("meterMds",     resultData[0]);
            chartDataMap.put("modelName",    resultData[1]);
            chartDataMap.put("modemId", StringUtil.nullToBlank(resultData[2]));
            chartDataMap.put("mcuId", StringUtil.nullToBlank(resultData[3]));
            chartDataMap.put("meterId", StringUtil.nullToBlank(resultData[4]));
            gridData.add(chartDataMap);
        }

        result.add(gridData);

        return result;
    }

    @Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
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

    @Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
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

    @Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
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

    @Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
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

    @Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
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
    @Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
    public List<Object> getMeterListByModem(Map<String, Object> condition){
        List<Object> gridData   = new ArrayList<Object>();
        List<Object> result     = new ArrayList<Object>();

        Integer modemId         = (Integer) condition.get("modemId");
        StringBuffer sbQuery    = new StringBuffer();

        sbQuery = new StringBuffer();
        sbQuery.append("  SELECT me.MDS_ID                    \n")
               .append("   FROM METER  me                     \n")
               .append("   LEFT OUTER JOIN CODE co            \n")
               .append("   ON ( me.meter_status = co.ID)      \n")
               .append("   WHERE me.MODEM_ID = " + modemId  + "\n")
               .append("   AND (co.NAME  <> 'Delete' or co.NAME  IS NULL) \n")
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
    @Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
    public List<Object> getMeterListByNotModem(Map<String, Object> condition){

        List<Object> gridData   = new ArrayList<Object>();
        List<Object> result     = new ArrayList<Object>();

        Integer supplierId      = (Integer) condition.get("supplierId");

        StringBuffer sbQuery    = new StringBuffer();

        sbQuery = new StringBuffer();
        sbQuery.append("  SELECT me.MDS_ID                  \n")
               .append("    FROM METER me                   \n")
               .append("    LEFT OUTER JOIN CODE co         \n")
               .append("    ON ( me.meter_status = co.ID)   \n")
               .append("   WHERE me.SUPPLIER_ID = :supplierId  \n")
               .append("     AND me.MODEM_ID is null           \n")
               .append("     AND (co.NAME  <> 'Delete' or co.NAME  IS NULL) \n")
               .append("   ORDER BY 1                       \n");

        SQLQuery query = getSession().createSQLQuery(sbQuery.toString());

        query.setInteger("supplierId", supplierId);

        List dataList = null;
        dataList = query.list();

        // 실제 데이터
        int dataListLen = 0;
        if(dataList != null)
            dataListLen= dataList.size();

        Supplier supplier = supplierDao.get(supplierId);
        DecimalFormat dfMd = DecimalUtil.getMDStyle(supplier.getMd());

        for(int i=0 ; i < dataListLen ; i++){

            HashMap gridDataMap = new HashMap();

            gridDataMap.put("no"          , dfMd.format(i+1) );
            gridDataMap.put("mdsId"      , dataList.get(i));


            gridData.add(gridDataMap);
        }

        result.add(gridData);


        return result;
    }

    @Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
    public List<Object> getMeterListForContract(Map<String, Object> condition){

        String mdsId            = StringUtil.nullToBlank(condition.get("mdsId"));

        StringBuffer sbQuery    = new StringBuffer();

        sbQuery = new StringBuffer();
//        sbQuery.append(" SELECT CASE WHEN me.MDS_ID = :mdsId THEN 'Y' ELSE 'N' END AS checked   \n")
//               .append("      , me.MDS_ID   AS mdsId        \n")
//               .append("      , me.address  AS address      \n")
//               .append("      , me.ID       AS meterId      \n")
        sbQuery.append(" SELECT CASE WHEN me.MDS_ID = :mdsId THEN 'Y' ELSE 'N' END AS CHECKED   \n")
               .append("      , me.MDS_ID   AS MDSID        \n")
               .append("      , me.address  AS ADDRESS      \n")
               .append("      , me.ID       AS METERID      \n")
               .append("   FROM METER me                    \n")
               .append("   LEFT OUTER JOIN CONTRACT con     \n")
               .append("    ON (me.ID = con.meter_ID)       \n")
               .append("  WHERE con.ID is null              \n")
               .append("    OR me.MDS_ID = :mdsId           \n")
               .append("  ORDER BY checked DESC, mdsId      \n");
/*        sbQuery.append(" SELECT CASE WHEN me.MDS_ID = :mdsId THEN 'Y' ELSE 'N' END AS CHECKED   \n")
        .append("      , me.MDS_ID   AS MDSID        \n")
        .append("      , me.address  AS ADDRESS      \n")
        .append("      , me.ID       AS METERID      \n")
        .append("   FROM METER me                    \n")
        .append("   LEFT OUTER JOIN CONTRACT con     \n")
        .append("    ON (me.ID = con.meter_ID)       \n")
        .append("  WHERE con.METER_ID is null        \n")
        .append("    OR me.MDS_ID = :mdsId           \n")
        .append("  ORDER BY checked DESC, mdsId      \n");*/

        SQLQuery query = getSession().createSQLQuery(sbQuery.toString());

        query.setString("mdsId", mdsId);
        return query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
    }

    @Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
    public List<Object> getMeterListForContractExtJs(Map<String, Object> condition){

        String mdsId            = StringUtil.nullToBlank(condition.get("mdsId"));
//        String address            = StringUtil.nullToBlank(condition.get("address"));
        logger.info(mdsId);
        StringBuffer sbQuery    = new StringBuffer();
        if(mdsId.equals("")){
            sbQuery.append(" select mds_id as MDSID, ADDRESS  \n")
                    .append(" from meter order by mds_id \n");
        }else{
            sbQuery.append(" select mds_id as MDSID, ADDRESS  \n")
            .append(" from meter  \n" )
            .append(" where mds_id like :mdsId           \n")
            .append(" union all   \n")
            .append(" select mds_id as MDSID, ADDRESS \n")
            .append(" from meter       \n")
            .append(" where mds_id not like :mdsId \n");
        }
        SQLQuery query = getSession().createSQLQuery(sbQuery.toString());
        if(!mdsId.equals("")){
            query.setString("mdsId", "%"+mdsId+"%");
        }
        /*
        sbQuery = new StringBuffer();
        if(mdsId.equals("") && address.equals("")){
            sbQuery.append(" select mds_id as MDSID, ADDRESS  \n")
                    .append(" from meter order by mds_id \n");
        }else{
            if(!mdsId.equals("") && address.equals("")){
                sbQuery.append(" select mds_id as MDSID, ADDRESS  \n")
                    .append(" from meter  \n" )
                    .append(" where mds_id like :mdsId           \n")
                    .append(" union all   \n")
                    .append(" select mds_id as MDSID, ADDRESS \n")
                    .append(" from meter       \n")
                    .append(" where mds_id not like :mdsId \n");
            }else if(mdsId.equals("") && !address.equals("")){
                sbQuery.append(" select mds_id as MDSID, ADDRESS  \n")
                    .append(" from meter  \n" )
                    .append(" where address like :address           \n")
                    .append(" union all   \n")
                    .append(" select mds_id as MDSID, ADDRESS \n")
                    .append(" from meter       \n")
                    .append(" where address not like :address \n");
            }else if(mdsId.equals("") && address.equals("")){
                sbQuery.append(" select mds_id as MDSID, ADDRESS  \n")
                .append(" from meter  \n" )
                .append(" where address like :address           \n")
                .append(" or mdsId like : mdsId  \n")
                .append(" union all   \n")
                .append(" select mds_id as MDSID, ADDRESS \n")
                .append(" from meter       \n")
                .append(" where address not like :address \n")
                .append(" or mdsId like : mdsId  \n");
            }
        }

        SQLQuery query = getSession().createSQLQuery(sbQuery.toString());
        if(!mdsId.equals("") && address.equals("")){
            query.setString("mdsId", "%"+mdsId+"%");
        }else if(mdsId.equals("") && !address.equals("")){
            query.setString("address", "%"+address+"%");
        }else if(mdsId.equals("") && address.equals("")){
            query.setString("mdsId", "%"+mdsId+"%");
            query.setString("address", "%"+address+"%");
        }else{

        }
        */
        return query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
    }

    /**
     * 설치일자,공급사 기준 전체 미터갯수를 조회한다.
     * @param params
     * @return
     */
    @Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
    public Integer getMeterCount(Map<String,Object> params){

        String searchStartDate = (String)params.get("searchStartDate");
        String meterType  = StringUtil.nullToBlank(params.get("meterType"));
        String supplierId = StringUtil.nullToBlank(params.get("supplierId"));

        StringBuffer sb = new StringBuffer();
        sb.append("\n SELECT COUNT(m.id) ");
        sb.append("\n FROM ").append(meterType).append(" m ");
        sb.append("\n      LEFT OUTER JOIN ");
        sb.append("\n      m.meterStatus c ");
        sb.append("\n WHERE m.installDate <= :searchStartDate ");
        if (!supplierId.isEmpty()) {
            sb.append("\n AND m.supplier.id = :supplierId ");
        }
        sb.append("\nAND   (c.id IS NULL ");
        sb.append("\n    OR c.code != :deleteCode ");
        sb.append("\n    OR (c.code = :deleteCode AND m.deleteDate > :deleteDate) ");
        sb.append("\n) ");

        Query query = getSession().createQuery(sb.toString());
        query.setString("searchStartDate", searchStartDate + CommonConstants.DefaultDate.LAST_HHMMSS.getValue());
        if (!supplierId.isEmpty()) {
            query.setInteger("supplierId", Integer.parseInt(supplierId));
        }
        query.setString("deleteCode", MeterCodes.DELETE_STATUS.getCode());
        query.setString("deleteDate", searchStartDate + "235959");

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
    @Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
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

        Integer totalCount = (Integer)params.get("totalMeterCount");
        String lpTable = CommonConstants.MeterType.valueOf(meterType).getLpTableName();

        StringBuffer sbQuery = new StringBuffer();
        sbQuery.append("\nSELECT COUNT(y.id) AS CNT ");
        sbQuery.append("\nFROM ( ");
        sbQuery.append("\n    SELECT x.id AS id ");
        sbQuery.append("\n    FROM ( ");
        sbQuery.append("\n        SELECT m.id, lp.yyyymmdd ");
        sbQuery.append("\n        FROM meter m ");
        sbQuery.append("\n             LEFT OUTER JOIN ");
        sbQuery.append("\n             ").append(lpTable).append(" lp ");
        sbQuery.append("\n             ON  lp.mdev_id = m.mds_id ");
        sbQuery.append("\n             AND lp.yyyymmddhh BETWEEN :startDate AND :endDate ");
        sbQuery.append("\n             AND lp.channel = :channel ");
        sbQuery.append("\n             LEFT OUTER JOIN ");
        sbQuery.append("\n             code c ");
        sbQuery.append("\n             ON c.id = m.meter_status ");
        sbQuery.append("\n        WHERE 1=1 ");
        sbQuery.append("\n        AND m.install_date <= :installDate ");
        if (!"".equals(supplierId)) {
            sbQuery.append("\n        AND m.supplier_id = :supplierId ");
        }
        sbQuery.append("\n        AND   (c.id IS NULL ");
        sbQuery.append("\n            OR c.code != :deleteCode ");
        sbQuery.append("\n            OR (c.code = :deleteCode AND m.delete_date > :deleteDate)) ");
        sbQuery.append("\n        GROUP BY m.id, lp.yyyymmdd ");
        sbQuery.append("\n    )x ");
        sbQuery.append("\n    WHERE x.yyyymmdd IS NOT NULL ");
        sbQuery.append("\n    GROUP BY x.id ");
        sbQuery.append("\n    HAVING COUNT(x.id) = :period ");
        sbQuery.append("\n)y ");

        SQLQuery query = getSession().createSQLQuery(sbQuery.toString());
        query.setString("startDate", searchStartDate + "00");
        query.setString("endDate", searchEndDate + "23");
        query.setString("installDate", searchStartDate + CommonConstants.DefaultDate.LAST_HHMMSS.getValue());
        query.setInteger("channel", channel);
        query.setInteger("period", period);
        if (!"".equals(supplierId)) {
            query.setInteger("supplierId", Integer.parseInt(supplierId));
        }
        query.addScalar("CNT", new IntegerType());
        query.setString("deleteCode", MeterCodes.DELETE_STATUS.getCode());
        query.setString("deleteDate", searchStartDate + "235959");

        Integer notAllMissingCount = (Integer)query.uniqueResult();

        return totalCount - notAllMissingCount;
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
    @Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
    public Integer getPatialMissingMeterCount(Map<String,Object> params){
        Integer totalCount = (Integer) params.get("totalMeterCount");
        String searchStartDate = StringUtil.nullToBlank(params.get("searchStartDate"));
        String searchEndDate = StringUtil.nullToBlank(params.get("searchEndDate"));
        String meterType = StringUtil.nullToBlank(params.get("meterType"));
        String supplierId = StringUtil.nullToBlank(params.get("supplierId"));
        Integer channel = (Integer) params.get("channel");

        String today = TimeUtil.getCurrentTimeMilli(); // yyyyMMddHHmmss
        String currDate = today.substring(0, 8);
        String currHour = today.substring(8, 10);
        Integer currMinute = Integer.parseInt(today.substring(10, 12));

        Integer missingCnt = 0;

        String lpTable = CommonConstants.MeterType.valueOf(meterType).getLpTableName();

        StringBuffer sbPastQuery = new StringBuffer();

        sbPastQuery.append("\n SELECT m.id");
        sbPastQuery.append("\n FROM (SELECT * FROM meter WHERE meter=:meterType) m LEFT OUTER JOIN ");
        sbPastQuery.append("\n      ").append(lpTable).append(" lp ");
        sbPastQuery.append("\n      ON lp.mdev_id = m.mds_id ");
        sbPastQuery.append("\n      LEFT OUTER JOIN ");
        sbPastQuery.append("\n      code c ");
        sbPastQuery.append("\n      ON c.id = m.meter_status ");
        sbPastQuery.append("\n WHERE lp.yyyymmddhh BETWEEN :searchStartDate AND :PreCurrOrEndDate ");
        sbPastQuery.append("\n AND lp.hh < '24' ");
        sbPastQuery.append("\n AND lp.channel = :channel ");
        sbPastQuery.append("\n AND m.supplier_Id = :supplierId ");
        sbPastQuery.append("\n AND m.install_date <= :installDate ");
//        sbPastQuery.append("\n AND 1 = CASE WHEN m.lp_interval = 1 THEN CASE WHEN lp.value_cnt < 60 THEN 0 ELSE 1 END ");
//        sbPastQuery.append("\n              WHEN m.lp_interval = 5 THEN CASE WHEN lp.value_cnt < 12 THEN 0 ELSE 1 END ");
//        sbPastQuery.append("\n              WHEN m.lp_interval = 10 THEN CASE WHEN lp.value_cnt < 6 THEN 0 ELSE 1 END ");
//        sbPastQuery.append("\n              WHEN m.lp_interval = 15 THEN CASE WHEN lp.value_cnt < 4 THEN 0 ELSE 1 END ");
//        sbPastQuery.append("\n              WHEN m.lp_interval = 30 THEN CASE WHEN lp.value_cnt < 2 THEN 0 ELSE 1 END ");
//        sbPastQuery.append("\n              WHEN m.lp_interval = 60 THEN CASE WHEN lp.value_cnt < 1 THEN 0 ELSE 1 END ");
//        sbPastQuery.append("\n          ELSE 0 END ");
        sbPastQuery.append("\nAND   ((m.lp_interval = 1 OR m.lp_interval = 5 ");
        sbPastQuery.append("\n     OR m.lp_interval = 10 OR m.lp_interval = 15 ");
        sbPastQuery.append("\n     OR m.lp_interval = 30 OR m.lp_interval = 60) ");
        sbPastQuery.append("\n    AND lp.value_cnt >= (60/m.lp_interval)) ");
        sbPastQuery.append("\n AND   (c.id IS NULL ");
        sbPastQuery.append("\n     OR c.code != :deleteCode ");
        sbPastQuery.append("\n     OR (c.code = :deleteCode AND m.delete_date > :deleteDate) ");
        sbPastQuery.append("\n ) ");
        sbPastQuery.append("\n GROUP BY m.id,lp.yyyymmdd ");
        sbPastQuery.append("\n HAVING COUNT(m.id) = 24 ");

        StringBuffer sbToCurrQuery = new StringBuffer();

        sbToCurrQuery.append("\n SELECT i1.id");
        sbToCurrQuery.append("\n FROM ");
        sbToCurrQuery.append("\n     (SELECT m.id");
        sbToCurrQuery.append("\n      FROM (SELECT * FROM meter WHERE meter=:meterType) m LEFT OUTER JOIN ");
        sbToCurrQuery.append("\n           ").append(lpTable).append(" lp ");
        sbToCurrQuery.append("\n           ON lp.mdev_id = m.mds_id ");
        sbToCurrQuery.append("\n           LEFT OUTER JOIN ");
        sbToCurrQuery.append("\n           code c ");
        sbToCurrQuery.append("\n           ON c.id = m.meter_status ");
        sbToCurrQuery.append("\n      WHERE lp.yyyymmddhh BETWEEN :currStartDate AND :currEndDate ");
        sbToCurrQuery.append("\n      AND lp.hh < :currHour ");
        sbToCurrQuery.append("\n      AND lp.channel = :channel ");
        sbToCurrQuery.append("\n      AND m.supplier_Id = :supplierId ");
        sbToCurrQuery.append("\n      AND m.install_date <= :installDate ");
//        sbToCurrQuery.append("\n      AND 1 = CASE WHEN m.lp_interval = 1 THEN CASE WHEN lp.value_cnt < 60 THEN 0 ELSE 1 END ");
//        sbToCurrQuery.append("\n                   WHEN m.lp_interval = 5 THEN CASE WHEN lp.value_cnt < 12 THEN 0 ELSE 1 END ");
//        sbToCurrQuery.append("\n                   WHEN m.lp_interval = 10 THEN CASE WHEN lp.value_cnt < 6 THEN 0 ELSE 1 END ");
//        sbToCurrQuery.append("\n                   WHEN m.lp_interval = 15 THEN CASE WHEN lp.value_cnt < 4 THEN 0 ELSE 1 END ");
//        sbToCurrQuery.append("\n                   WHEN m.lp_interval = 30 THEN CASE WHEN lp.value_cnt < 2 THEN 0 ELSE 1 END ");
//        sbToCurrQuery.append("\n                   WHEN m.lp_interval = 60 THEN CASE WHEN lp.value_cnt < 1 THEN 0 ELSE 1 END ");
//        sbToCurrQuery.append("\n              ELSE 0 END ");
        sbToCurrQuery.append("\n      AND   ((m.lp_interval = 1 OR m.lp_interval = 5 ");
        sbToCurrQuery.append("\n           OR m.lp_interval = 10 OR m.lp_interval = 15 ");
        sbToCurrQuery.append("\n           OR m.lp_interval = 30 OR m.lp_interval = 60) ");
        sbToCurrQuery.append("\n          AND lp.value_cnt >= (60/m.lp_interval)) ");
        sbToCurrQuery.append("\n      AND   (c.id IS NULL ");
        sbToCurrQuery.append("\n          OR c.code != :deleteCode ");
        sbToCurrQuery.append("\n          OR (c.code = :deleteCode AND m.delete_date > :deleteDate)) ");
        sbToCurrQuery.append("\n      GROUP BY m.id,lp.yyyymmdd ");
        sbToCurrQuery.append("\n      HAVING COUNT(m.id) = :hourCnt) i1 ");

        sbToCurrQuery.append("\n     INNER JOIN ");

        sbToCurrQuery.append("\n     (SELECT m.id");
        sbToCurrQuery.append("\n      FROM (SELECT * FROM meter WHERE meter=:meterType) m LEFT OUTER JOIN ");
        sbToCurrQuery.append("\n           ").append(lpTable).append(" lp ");
        sbToCurrQuery.append("\n           ON lp.mdev_id = m.mds_id ");
        sbToCurrQuery.append("\n           LEFT OUTER JOIN ");
        sbToCurrQuery.append("\n           code c ");
        sbToCurrQuery.append("\n           ON c.id = m.meter_status ");
        sbToCurrQuery.append("\n      WHERE lp.yyyymmddhh BETWEEN :currStartDate AND :currEndDate  ");
        sbToCurrQuery.append("\n      AND lp.hh = :currHour ");
        sbToCurrQuery.append("\n      AND lp.channel = :channel ");
        sbToCurrQuery.append("\n      AND m.supplier_Id = :supplierId ");
        sbToCurrQuery.append("\n      AND m.install_date <= :installDate ");
        sbToCurrQuery.append("\n      AND   (c.id IS NULL ");
        sbToCurrQuery.append("\n          OR c.code != :deleteCode ");
        sbToCurrQuery.append("\n          OR (c.code = :deleteCode AND m.delete_date > :deleteDate)) ");

        if (currMinute < 1) {
//            sbToCurrQuery.append("\n      AND 1 = CASE WHEN m.lp_interval = 1 THEN CASE WHEN 1 <= lp.value_cnt THEN 1 ELSE 0 END ");
//            sbToCurrQuery.append("\n                   WHEN m.lp_interval = 5 THEN CASE WHEN 1 <= lp.value_cnt THEN 1 ELSE 0 END ");
//            sbToCurrQuery.append("\n                   WHEN m.lp_interval = 10 THEN CASE WHEN 1 <= lp.value_cnt THEN 1 ELSE 0 END ");
//            sbToCurrQuery.append("\n                   WHEN m.lp_interval = 15 THEN CASE WHEN 1 <= lp.value_cnt THEN 1 ELSE 0 END ");
//            sbToCurrQuery.append("\n                   WHEN m.lp_interval = 30 THEN CASE WHEN 1 <= lp.value_cnt THEN 1 ELSE 0 END ");
//            sbToCurrQuery.append("\n                   WHEN m.lp_interval = 60 THEN CASE WHEN 1 <= lp.value_cnt THEN 1 ELSE 0 END ");
//            sbToCurrQuery.append("\n              ELSE 0 END) i2 ON i1.id=i2.id  ");
            sbToCurrQuery.append("\n      AND   ((m.lp_interval = 1 OR m.lp_interval = 5 ");
            sbToCurrQuery.append("\n           OR m.lp_interval = 10 OR m.lp_interval = 15 ");
            sbToCurrQuery.append("\n           OR m.lp_interval = 30 OR m.lp_interval = 60) ");
            sbToCurrQuery.append("\n          AND lp.value_cnt >= 1) ");
        } else {
//            sbToCurrQuery.append("\n      AND 1 = CASE WHEN m.lp_interval = 1 THEN CASE WHEN (:currMinute/1) < lp.value_cnt THEN 1 ELSE 0 END ");
//            sbToCurrQuery.append("\n                   WHEN m.lp_interval = 5  THEN CASE WHEN (:currMinute/5) < lp.value_cnt THEN 1 ELSE 0 END ");
//            sbToCurrQuery.append("\n                   WHEN m.lp_interval = 10 THEN CASE WHEN (:currMinute/10) < lp.value_cnt THEN 1 ELSE 0 END ");
//            sbToCurrQuery.append("\n                   WHEN m.lp_interval = 15 THEN CASE WHEN (:currMinute/15) < lp.value_cnt THEN 1 ELSE 0 END ");
//            sbToCurrQuery.append("\n                   WHEN m.lp_interval = 30 THEN CASE WHEN (:currMinute/30) < lp.value_cnt THEN 1 ELSE 0 END ");
//            sbToCurrQuery.append("\n                   WHEN m.lp_interval = 60 THEN CASE WHEN (:currMinute/60) < lp.value_cnt THEN 1 ELSE 0 END ");
//            sbToCurrQuery.append("\n              ELSE 0 END) i2 ON i1.id=i2.id ");
            sbToCurrQuery.append("\n      AND   ((m.lp_interval = 1 OR m.lp_interval = 5 ");
            sbToCurrQuery.append("\n           OR m.lp_interval = 10 OR m.lp_interval = 15 ");
            sbToCurrQuery.append("\n           OR m.lp_interval = 30 OR m.lp_interval = 60) ");
            sbToCurrQuery.append("\n          AND lp.value_cnt > (:currMinute/m.lp_interval)) ");
        }
        sbToCurrQuery.append("\n     ) i2 ON i1.id = i2.id ");

        SQLQuery query = null;

        if (Integer.parseInt(searchStartDate) < Integer.parseInt(currDate)
                && Integer.parseInt(searchEndDate) < Integer.parseInt(currDate)) {
            StringBuffer sbPastToPastQuery = new StringBuffer();

            sbPastToPastQuery.append("\n SELECT COUNT(y.id) AS CNT");
            sbPastToPastQuery.append("\n FROM (" + sbPastQuery);
            sbPastToPastQuery.append("\n  ) y");

            query = getSession().createSQLQuery(sbPastToPastQuery.toString());

            query.setString("searchStartDate", searchStartDate + "00");
            query.setString("PreCurrOrEndDate", searchEndDate + "23");

        } else {
            if (Integer.parseInt(searchStartDate) < Integer.parseInt(currDate)) {
                StringBuffer sbPastToCurrQuery = new StringBuffer();

                sbPastToCurrQuery.append("\n SELECT COUNT(y.id) AS CNT ");
                sbPastToCurrQuery.append("\n FROM ( SELECT i1.id FROM (" + sbPastQuery + ") i1");
                sbPastToCurrQuery.append("\n INNER JOIN ");
                sbPastToCurrQuery.append("\n (" + sbToCurrQuery + ") i2 ON i1.id=i2.id");
                sbPastToCurrQuery.append("\n ) y ");

                query = getSession().createSQLQuery(sbPastToCurrQuery.toString());

                query.setString("searchStartDate", searchStartDate + "00");
                query.setString("PreCurrOrEndDate", searchEndDate + "23");

            } else {
                StringBuffer sbCurrToCurrQuery = new StringBuffer();

                sbCurrToCurrQuery.append("\n SELECT COUNT(y.id) AS CNT ");
                sbCurrToCurrQuery.append("\n FROM (" + sbToCurrQuery);
                sbCurrToCurrQuery.append("\n ) y ");

                query = getSession().createSQLQuery(sbCurrToCurrQuery.toString());

            }

            query.setString("currStartDate", currDate + "00");
            query.setString("currEndDate", currDate + "23");
            query.setString("currHour", currHour);
            query.setInteger("hourCnt", Integer.parseInt(currHour));
            if (currMinute >= 1) {
                query.setInteger("currMinute", currMinute);
            }
        }

        query.setString("meterType", meterType);
        if (!"".equals(supplierId)) {
            query.setInteger("supplierId", Integer.parseInt(supplierId));
        }
        query.setString("installDate", searchEndDate + CommonConstants.DefaultDate.LAST_HHMMSS.getValue());
        query.setInteger("channel", channel);
        query.addScalar("CNT", new IntegerType());
        query.setString("deleteCode", MeterCodes.DELETE_STATUS.getCode());
        query.setString("deleteDate", searchStartDate + "235959");

        Integer successCount = (Integer) query.uniqueResult();

        Integer getAllMissingMeterCount = this.getAllMissingMeterCount(params);

        missingCnt = totalCount - (getAllMissingMeterCount + successCount);

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
    @Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
    public List<Object> getMissingMetersByHour(Map<String, Object> params) {
        String meterType = StringUtil.nullToBlank(params.get("meterType"));
        String supplierId = StringUtil.nullToBlank(params.get("supplierId"));

        String searchStartDate = (String) params.get("searchStartDate");
        Integer channel = (Integer) params.get("channel");
        Integer totalMeterCnt = (Integer) params.get("totalMeterCnt");

        String today = TimeUtil.getCurrentTimeMilli(); // yyyyMMddHHmmss
        String currDate = today.substring(0, 8);
        String currHour = today.substring(8, 10);
        Integer currMinute = Integer.parseInt(today.substring(10, 12));

        String lpTable = CommonConstants.MeterType.valueOf(meterType).getLpTableName();

        List<Object> resultList = new ArrayList<Object>();

        // query 작성
        StringBuffer sbQuery = new StringBuffer();
        sbQuery.append("\nSELECT hh AS HH, ");
        sbQuery.append("\n       COUNT(hh) AS SUCCESS_CNT ");
        sbQuery.append("\nFROM meter m ");
        sbQuery.append("\n     LEFT OUTER JOIN ");
        sbQuery.append("\n     code c ");
        sbQuery.append("\n     ON c.id = m.meter_status, ");
        sbQuery.append("\n     ").append(lpTable).append(" lp ");
        sbQuery.append("\nWHERE lp.mdev_id = m.mds_id ");
        sbQuery.append("\nAND   lp.yyyymmddhh BETWEEN :startDate AND :endDate ");
        sbQuery.append("\nAND   lp.hh < :currHour ");
        sbQuery.append("\nAND   lp.channel = :channel ");
        sbQuery.append("\nAND   m.install_date <= :installDate ");
        if (!"".equals(supplierId)) {
            sbQuery.append("\nAND   m.supplier_id = :supplierId ");
        }
        sbQuery.append("\nAND   (c.id IS NULL ");
        sbQuery.append("\n    OR c.code != :deleteCode ");
        sbQuery.append("\n    OR (c.code = :deleteCode AND m.delete_date > :deleteDate)) ");

//        sbQuery.append("\nAND   1 = CASE WHEN m.lp_interval = 1 THEN CASE WHEN lp.value_cnt < 60   THEN 0 ELSE 1 END ");
//        sbQuery.append("\n               WHEN m.lp_interval = 5 THEN CASE WHEN lp.value_cnt < 12   THEN 0 ELSE 1 END ");
//        sbQuery.append("\n               WHEN m.lp_interval = 10 THEN CASE WHEN lp.value_cnt < 6   THEN 0 ELSE 1 END ");
//        sbQuery.append("\n               WHEN m.lp_interval = 15 THEN CASE WHEN lp.value_cnt < 4   THEN 0 ELSE 1 END ");
//        sbQuery.append("\n               WHEN m.lp_interval = 30 THEN CASE WHEN lp.value_cnt < 2   THEN 0 ELSE 1 END ");
//        sbQuery.append("\n               WHEN m.lp_interval = 60 THEN CASE WHEN lp.value_cnt < 1   THEN 0 ELSE 1 END ");
//        sbQuery.append("\n          ELSE 0 END ");

        sbQuery.append("\nAND   ((m.lp_interval = 1 OR m.lp_interval = 5 ");
        sbQuery.append("\n     OR m.lp_interval = 10 OR m.lp_interval = 15 ");
        sbQuery.append("\n     OR m.lp_interval = 30 OR m.lp_interval = 60) ");
        sbQuery.append("\n    AND lp.value_cnt >= (60/m.lp_interval)) ");

        sbQuery.append("\nGROUP BY yyyymmdd,hh ");

        StringBuffer sbQueryCurrHour = new StringBuffer();
        sbQueryCurrHour.append("\nSELECT hh AS HH, ");
        sbQueryCurrHour.append("\n       COUNT(hh) AS SUCCESS_CNT ");
        sbQueryCurrHour.append("\nFROM meter m ");
        sbQueryCurrHour.append("\n     LEFT OUTER JOIN ");
        sbQueryCurrHour.append("\n     code c ");
        sbQueryCurrHour.append("\n     ON c.id = m.meter_status, ");
        sbQueryCurrHour.append("\n     ").append(lpTable).append(" lp ");
        sbQueryCurrHour.append("\nWHERE lp.mdev_id = m.mds_id ");
        sbQueryCurrHour.append("\nAND   lp.yyyymmddhh BETWEEN :startDate AND :endDate ");
        sbQueryCurrHour.append("\nAND   lp.hh = :currHour ");
        sbQueryCurrHour.append("\nAND   lp.channel = :channel ");
        sbQueryCurrHour.append("\nAND   m.install_date <= :installDate ");
        if (!"".equals(supplierId)) {
            sbQueryCurrHour.append("\nAND   m.supplier_id = :supplierId ");
        }
        sbQueryCurrHour.append("\nAND   (c.id IS NULL ");
        sbQueryCurrHour.append("\n    OR c.code != :deleteCode ");
        sbQueryCurrHour.append("\n    OR (c.code = :deleteCode AND m.delete_date > :deleteDate)) ");

        if (currMinute < 1) {
//            sbQueryCurrHour.append("\nAND   1 = CASE WHEN m.lp_interval = 1  THEN CASE WHEN 1 <= lp.value_cnt THEN 1 ELSE 0 END ");
//            sbQueryCurrHour.append("\n               WHEN m.lp_interval = 5  THEN CASE WHEN 1 <= lp.value_cnt THEN 1 ELSE 0 END ");
//            sbQueryCurrHour.append("\n               WHEN m.lp_interval = 10 THEN CASE WHEN 1 <= lp.value_cnt THEN 1 ELSE 0 END ");
//            sbQueryCurrHour.append("\n               WHEN m.lp_interval = 15 THEN CASE WHEN 1 <= lp.value_cnt THEN 1 ELSE 0 END ");
//            sbQueryCurrHour.append("\n               WHEN m.lp_interval = 30 THEN CASE WHEN 1 <= lp.value_cnt THEN 1 ELSE 0 END ");
//            sbQueryCurrHour.append("\n               WHEN m.lp_interval = 60 THEN CASE WHEN 1 <= lp.value_cnt THEN 1 ELSE 0 END ");
//            sbQueryCurrHour.append("\n          ELSE 0 END ");
            sbQueryCurrHour.append("\nAND   ((m.lp_interval = 1 OR m.lp_interval = 5 ");
            sbQueryCurrHour.append("\n     OR m.lp_interval = 10 OR m.lp_interval = 15 ");
            sbQueryCurrHour.append("\n     OR m.lp_interval = 30 OR m.lp_interval = 60) ");
            sbQueryCurrHour.append("\n    AND lp.value_cnt >= 1) ");
        } else {
//            sbQueryCurrHour.append("\nAND   CASE WHEN m.lp_interval = 1 THEN CASE WHEN (:currMinute/1) < lp.value_cnt THEN 1 ELSE 0 END ");
//            sbQueryCurrHour.append("\n           WHEN m.lp_interval = 5 THEN CASE WHEN (:currMinute/5) < lp.value_cnt THEN 1 ELSE 0 END ");
//            sbQueryCurrHour.append("\n           WHEN m.lp_interval = 10 THEN CASE WHEN (:currMinute/10) < lp.value_cnt THEN 1 ELSE 0 END ");
//            sbQueryCurrHour.append("\n           WHEN m.lp_interval = 15 THEN CASE WHEN (:currMinute/15) < lp.value_cnt THEN 1 ELSE 0 END ");
//            sbQueryCurrHour.append("\n           WHEN m.lp_interval = 30 THEN CASE WHEN (:currMinute/30) < lp.value_cnt THEN 1 ELSE 0 END ");
//            sbQueryCurrHour.append("\n           WHEN m.lp_interval = 60 THEN CASE WHEN (:currMinute/60) < lp.value_cnt THEN 1 ELSE 0 END ");
//            sbQueryCurrHour.append("\n      ELSE 0 END = 1 ");
            sbQueryCurrHour.append("\nAND   ((m.lp_interval = 1 OR m.lp_interval = 5 ");
            sbQueryCurrHour.append("\n     OR m.lp_interval = 10 OR m.lp_interval = 15 ");
            sbQueryCurrHour.append("\n     OR m.lp_interval = 30 OR m.lp_interval = 60) ");
            sbQueryCurrHour.append("\n    AND lp.value_cnt > (:currMinute/m.lp_interval)) ");
        }

        sbQueryCurrHour.append("\nGROUP BY lp.yyyymmdd, lp.hh ");

        Map<String, Object> successData = new HashMap<String, Object>();

        // 조회일자가 과거일경우 0~23시의 모든 누락건수 조회
        if (Integer.parseInt(searchStartDate) < Integer.parseInt(currDate)) {
            // 파라메터 설정
            SQLQuery query = getSession().createSQLQuery(sbQuery.toString());

            query.setString("startDate", searchStartDate + "00");
            query.setString("endDate", searchStartDate + "23");
            query.setString("installDate", searchStartDate + CommonConstants.DefaultDate.LAST_HHMMSS.getValue());
            query.setInteger("channel", channel);
            query.setString("currHour", "24");
            if (!"".equals(supplierId)) {
                query.setInteger("supplierId", Integer.parseInt(supplierId));
            }
            query.setString("deleteCode", MeterCodes.DELETE_STATUS.getCode());
            query.setString("deleteDate", searchStartDate + "235959");

            // query 결과목록
            List<Object> successDataList = query.list();
            Object[] objs = null;

            for (Object obj : successDataList) {
                objs = (Object[]) obj;
                successData.put((String) objs[0], ((Number) objs[1]).intValue());
            }
        } else {
            // 조회일자가 오늘일경우 0~ 현재시간 1시간전까지의 누락건을 조회하고
            // 현재시간의 누락건을 별도의 쿼리로 조회한다.

            // 0~ 현재시간 1시간전데이터 조회
            SQLQuery query = getSession().createSQLQuery(sbQuery.toString());
            query.setString("startDate", searchStartDate + "00");
            query.setString("endDate", searchStartDate + "23");
            query.setString("installDate", searchStartDate + CommonConstants.DefaultDate.LAST_HHMMSS.getValue());
            query.setInteger("channel", channel);
            query.setString("currHour", currHour);
            if (!"".equals(supplierId)) {
                query.setInteger("supplierId", Integer.parseInt(supplierId));
            }
            query.setString("deleteCode", MeterCodes.DELETE_STATUS.getCode());
            query.setString("deleteDate", searchStartDate + "235959");

            // query 결과목록
            List<Object> successDataList = query.list();
            Object[] objs = null;

            for (Object obj : successDataList) {
                objs = (Object[]) obj;
                successData.put((String) objs[0], ((Number) objs[1]).intValue());
            }

            // 현재시간 데이터 조회
            SQLQuery queryCurrHour = getSession().createSQLQuery(sbQueryCurrHour.toString());
            queryCurrHour.setString("startDate", searchStartDate + "00");
            queryCurrHour.setString("endDate", searchStartDate + "23");
            queryCurrHour.setString("installDate", searchStartDate + CommonConstants.DefaultDate.LAST_HHMMSS.getValue());
            queryCurrHour.setInteger("channel", channel);
            queryCurrHour.setString("currHour", currHour);
            if (currMinute >= 1) {
                queryCurrHour.setInteger("currMinute", currMinute);
            }
            if (!"".equals(supplierId)) {
                queryCurrHour.setInteger("supplierId", Integer.parseInt(supplierId));
            }
            queryCurrHour.setString("deleteCode", MeterCodes.DELETE_STATUS.getCode());
            queryCurrHour.setString("deleteDate", searchStartDate + "235959");

            // query 결과목록
            List<Object> successDataListCurrHour = queryCurrHour.list();
            for (Object obj : successDataListCurrHour) {
                objs = (Object[]) obj;
                successData.put((String) objs[0], ((Number) objs[1]).intValue());
            }
        }

        Supplier supplier = supplierDao.get(Integer.parseInt(supplierId));
        DecimalFormat dfMd = DecimalUtil.getMDStyle(supplier.getMd());
        Map<String, Object> resultMap = null;
        Integer successCount = 0;
        String hh = null;
        // 특정시간대에 모두 누락될수도 있으므로 0 ~23 시까지의 데이터는 고정
        for (int i = 0; i < 24; i++) {
            resultMap = new HashMap<String, Object>();
            resultMap.put("no", dfMd.format(i + 1));
            resultMap.put("yyyymmdd", TimeLocaleUtil.getLocaleDate(searchStartDate, supplier.getLang().getCode_2letter(),
                    supplier.getCountry().getCode_2letter()));

            hh = TimeUtil.to2Digit(i);
            resultMap.put("hh", hh);

            if (searchStartDate.equals(currDate) && i > Integer.parseInt(currHour)) { // 오늘날짜이고 현재시간 이후일경우 0으로 세팅한다.
                resultMap.put("missingCount", dfMd.format(0));
            } else {
                successCount = (successData.get(hh) == null) ? 0 : (Integer) successData.get(hh);
                resultMap.put("missingCount", dfMd.format(totalMeterCnt - successCount));
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
    @Transactional(readOnly = true, propagation = Propagation.REQUIRES_NEW)
    public List<Object> getMissingMetersByDay(Map<String, Object> params) {
        String searchStartDate = (String) params.get("searchStartDate");
        String searchEndDate = (String) params.get("searchEndDate");
        String meterType = StringUtil.nullToBlank(params.get("meterType"));
        String supplierId = StringUtil.nullToBlank(params.get("supplierId"));
        Integer channel = (Integer) params.get("channel");
        Integer totalMeterCnt = (Integer) params.get("totalMeterCnt");

        String today = TimeUtil.getCurrentTimeMilli(); // yyyyMMddHHmmss
        String currDate = today.substring(0, 8);
        String currHour = today.substring(8, 10);
        Integer currMinute = Integer.parseInt(today.substring(10, 12));

        String lpTable = CommonConstants.MeterType.valueOf(meterType).getLpTableName();

        int period = 0;
        try {
            period = TimeUtil.getDayDuration(searchStartDate, searchEndDate) + 1;
        } catch (ParseException e) {
            logger.error(e, e);
            e.printStackTrace();
        }

        List<Object> resultList = new ArrayList<Object>();

        // query 작성
        StringBuffer sbQuery = new StringBuffer();
        sbQuery.append("\nSELECT x.yyyymmdd AS YYYYMMDD, COUNT(x.yyyymmdd) AS SUCCESS_CNT ");
        sbQuery.append("\nFROM ( ");
        sbQuery.append("\n    SELECT yyyymmdd,m.id ");
        sbQuery.append("\n    FROM meter m ");
        sbQuery.append("\n         LEFT OUTER JOIN ");
        sbQuery.append("\n         code c ");
        sbQuery.append("\n         ON c.id = m.meter_status, ");
        sbQuery.append("\n         ").append(lpTable).append(" lp ");
        sbQuery.append("\n    WHERE lp.mdev_id = m.mds_id ");
        sbQuery.append("\n    AND   lp.yyyymmddhh BETWEEN :startDate AND :endDate ");
        sbQuery.append("\n    AND   lp.channel = :channel ");
        sbQuery.append("\n    AND   m.install_date <= :installDate ");
        if (!"".equals(supplierId)) {
            sbQuery.append("\n    AND   m.supplier_id = :supplierId ");
        }
        sbQuery.append("\n    AND   (c.id IS NULL ");
        sbQuery.append("\n        OR c.code != :deleteCode ");
        sbQuery.append("\n        OR (c.code = :deleteCode AND m.delete_date > :deleteDate)) ");

//        sbQuery.append("\n    AND   1 = CASE WHEN m.lp_interval = 1  THEN CASE WHEN lp.value_cnt < 60 THEN 0 ELSE 1 END ");
//        sbQuery.append("\n                   WHEN m.lp_interval = 5  THEN CASE WHEN lp.value_cnt < 12 THEN 0 ELSE 1 END ");
//        sbQuery.append("\n                   WHEN m.lp_interval = 10 THEN CASE WHEN lp.value_cnt < 6  THEN 0 ELSE 1 END ");
//        sbQuery.append("\n                   WHEN m.lp_interval = 15 THEN CASE WHEN lp.value_cnt < 4  THEN 0 ELSE 1 END ");
//        sbQuery.append("\n                   WHEN m.lp_interval = 30 THEN CASE WHEN lp.value_cnt < 2  THEN 0 ELSE 1 END ");
//        sbQuery.append("\n                   WHEN m.lp_interval = 60 THEN CASE WHEN lp.value_cnt < 1  THEN 0 ELSE 1 END ");
//        sbQuery.append("\n              ELSE 0 END ");
        sbQuery.append("\n    AND   ((m.lp_interval = 1 OR m.lp_interval = 5 ");
        sbQuery.append("\n         OR m.lp_interval = 10 OR m.lp_interval = 15 ");
        sbQuery.append("\n         OR m.lp_interval = 30 OR m.lp_interval = 60) ");
        sbQuery.append("\n        AND lp.value_cnt >= (60/m.lp_interval)) ");
        sbQuery.append("\n    GROUP BY yyyymmdd, m.id ");
        sbQuery.append("\n    HAVING COUNT(*) = 24 ");
        sbQuery.append("\n) x ");
        sbQuery.append("\nGROUP BY x.yyyymmdd ");

        // query 작성
        StringBuffer sbQueryToday = new StringBuffer();
        sbQueryToday.append("\nSELECT yyyymmdd, m.id ");
        sbQueryToday.append("\nFROM meter m ");
        sbQueryToday.append("\n     LEFT OUTER JOIN ");
        sbQueryToday.append("\n     code c ");
        sbQueryToday.append("\n     ON c.id = m.meter_status, ");
        sbQueryToday.append("\n     ").append(lpTable).append(" lp ");
        sbQueryToday.append("\nWHERE lp.mdev_id = m.mds_id ");
        sbQueryToday.append("\nAND   lp.yyyymmddhh BETWEEN :startDate AND :endDate ");
        sbQueryToday.append("\nAND   lp.hh < :currHour ");
        sbQueryToday.append("\nAND   lp.channel = :channel ");
        sbQueryToday.append("\nAND   m.install_date <= :installDate ");
        if (!"".equals(supplierId)) {
            sbQueryToday.append("\nAND   m.supplier_id = :supplierId ");
        }
        sbQueryToday.append("\nAND   (c.id IS NULL ");
        sbQueryToday.append("\n    OR c.code != :deleteCode ");
        sbQueryToday.append("\n    OR (c.code = :deleteCode AND m.delete_date > :deleteDate)) ");

//        sbQueryToday.append("\nAND   1 = CASE WHEN m.lp_interval = 1  THEN CASE WHEN lp.value_cnt < 60 THEN 0 ELSE 1 END ");
//        sbQueryToday.append("\n               WHEN m.lp_interval = 5  THEN CASE WHEN lp.value_cnt < 12 THEN 0 ELSE 1 END ");
//        sbQueryToday.append("\n               WHEN m.lp_interval = 10 THEN CASE WHEN lp.value_cnt < 6  THEN 0 ELSE 1 END ");
//        sbQueryToday.append("\n               WHEN m.lp_interval = 15 THEN CASE WHEN lp.value_cnt < 4  THEN 0 ELSE 1 END ");
//        sbQueryToday.append("\n               WHEN m.lp_interval = 30 THEN CASE WHEN lp.value_cnt < 2  THEN 0 ELSE 1 END ");
//        sbQueryToday.append("\n               WHEN m.lp_interval = 60 THEN CASE WHEN lp.value_cnt < 1  THEN 0 ELSE 1 END ");
//        sbQueryToday.append("\n          ELSE 0 END ");
        sbQueryToday.append("\nAND   ((m.lp_interval = 1 OR m.lp_interval = 5 ");
        sbQueryToday.append("\n     OR m.lp_interval = 10 OR m.lp_interval = 15 ");
        sbQueryToday.append("\n     OR m.lp_interval = 30 OR m.lp_interval = 60) ");
        sbQueryToday.append("\n    AND lp.value_cnt >= (60/m.lp_interval)) ");
        sbQueryToday.append("\nGROUP BY yyyymmdd,m.id ");
        sbQueryToday.append("\nHAVING COUNT(*) = :hourCnt ");

        // query 작성
        StringBuffer sbQueryCurrHour = new StringBuffer();
        sbQueryCurrHour.append("\nSELECT lp.yyyymmdd, m.id ");
        sbQueryCurrHour.append("\nFROM meter m ");
        sbQueryCurrHour.append("\n     LEFT OUTER JOIN ");
        sbQueryCurrHour.append("\n     code c ");
        sbQueryCurrHour.append("\n     ON c.id = m.meter_status, ");
        sbQueryCurrHour.append("\n     ").append(lpTable).append(" lp ");
        sbQueryCurrHour.append("\nWHERE lp.mdev_id = m.mds_id ");
        sbQueryCurrHour.append("\nAND   lp.yyyymmddhh BETWEEN :startDate AND :endDate ");
        sbQueryCurrHour.append("\nAND   lp.hh = :currHour ");
        sbQueryCurrHour.append("\nAND   lp.channel = :channel ");
        sbQueryCurrHour.append("\nAND   m.install_date <= :installDate ");
        if (!"".equals(supplierId)) {
            sbQueryCurrHour.append("\nAND   m.supplier_id = :supplierId ");
        }
        sbQueryCurrHour.append("\nAND   (c.id IS NULL ");
        sbQueryCurrHour.append("\n    OR c.code != :deleteCode ");
        sbQueryCurrHour.append("\n    OR (c.code = :deleteCode AND m.delete_date > :deleteDate)) ");

        if (currMinute < 1) {
//            sbQueryCurrHour.append("\nAND   1 = CASE WHEN m.lp_interval = 1 THEN CASE WHEN 1 <= lp.value_cnt THEN 1 ELSE 0 END ");
//            sbQueryCurrHour.append("\n               WHEN m.lp_interval = 5 THEN CASE WHEN 1 <= lp.value_cnt THEN 1 ELSE 0 END ");
//            sbQueryCurrHour.append("\n               WHEN m.lp_interval = 10 THEN CASE WHEN 1 <= lp.value_cnt THEN 1 ELSE 0 END ");
//            sbQueryCurrHour.append("\n               WHEN m.lp_interval = 15 THEN CASE WHEN 1 <= lp.value_cnt THEN 1 ELSE 0 END ");
//            sbQueryCurrHour.append("\n               WHEN m.lp_interval = 30 THEN CASE WHEN 1 <= lp.value_cnt THEN 1 ELSE 0 END ");
//            sbQueryCurrHour.append("\n               WHEN m.lp_interval = 60 THEN CASE WHEN 1 <= lp.value_cnt THEN 1 ELSE 0 END ");
//            sbQueryCurrHour.append("\n          ELSE 0 END ");
            sbQueryCurrHour.append("\nAND   ((m.lp_interval = 1 OR m.lp_interval = 5 ");
            sbQueryCurrHour.append("\n     OR m.lp_interval = 10 OR m.lp_interval = 15 ");
            sbQueryCurrHour.append("\n     OR m.lp_interval = 30 OR m.lp_interval = 60) ");
            sbQueryCurrHour.append("\n    AND lp.value_cnt >= 1) ");
        } else {
//            sbQueryCurrHour.append("\nAND   1 = CASE WHEN m.lp_interval = 1  THEN CASE WHEN (:currMinute/1) < lp.value_cnt  THEN 1 ELSE 0 END ");
//            sbQueryCurrHour.append("\n               WHEN m.lp_interval = 5  THEN CASE WHEN (:currMinute/5) < lp.value_cnt  THEN 1 ELSE 0 END ");
//            sbQueryCurrHour.append("\n               WHEN m.lp_interval = 10 THEN CASE WHEN (:currMinute/10) < lp.value_cnt THEN 1 ELSE 0 END ");
//            sbQueryCurrHour.append("\n               WHEN m.lp_interval = 15 THEN CASE WHEN (:currMinute/15) < lp.value_cnt THEN 1 ELSE 0 END ");
//            sbQueryCurrHour.append("\n               WHEN m.lp_interval = 30 THEN CASE WHEN (:currMinute/30) < lp.value_cnt THEN 1 ELSE 0 END ");
//            sbQueryCurrHour.append("\n               WHEN m.lp_interval = 60 THEN CASE WHEN (:currMinute/60) < lp.value_cnt THEN 1 ELSE 0 END ");
//            sbQueryCurrHour.append("\n          ELSE 0 END ");
            sbQueryCurrHour.append("\nAND   ((m.lp_interval = 1 OR m.lp_interval = 5 ");
            sbQueryCurrHour.append("\n     OR m.lp_interval = 10 OR m.lp_interval = 15 ");
            sbQueryCurrHour.append("\n     OR m.lp_interval = 30 OR m.lp_interval = 60) ");
            sbQueryCurrHour.append("\n    AND lp.value_cnt > (:currMinute/m.lp_interval)) ");
        }

        Map<String, Object> successData = new HashMap<String, Object>();

        int todayMissingCount = 0;
        if (Integer.parseInt(searchEndDate) < Integer.parseInt(currDate)) {
            // 파라메터 설정
            SQLQuery query = getSession().createSQLQuery(sbQuery.toString());
            query.setString("startDate", searchStartDate + "00");
            query.setString("endDate", searchEndDate + "23");
            query.setString("installDate", searchStartDate + CommonConstants.DefaultDate.LAST_HHMMSS.getValue());
            query.setInteger("channel", channel);
            if (!"".equals(supplierId)) {
                query.setInteger("supplierId", Integer.parseInt(supplierId));
            }
            query.setString("deleteCode", MeterCodes.DELETE_STATUS.getCode());
            query.setString("deleteDate", searchStartDate + "235959");

            // query 결과목록
            List<Map<String, Object>> successDataList = query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();

            for (Map<String, Object> obj : successDataList) {
                successData.put((String) obj.get("YYYYMMDD"), DecimalUtil.ConvertNumberToInteger(obj.get("SUCCESS_CNT")));

            }
        } else {
            // 시작을 ~ 종료일전일까지의 누락건수조회 having 절의 카운트가 24가 아닌경우조회
            SQLQuery query = getSession().createSQLQuery(sbQuery.toString());
            query.setString("startDate", searchStartDate + "00");
            query.setString("endDate", CalendarUtil.getDateWithoutFormat(searchEndDate, Calendar.DATE, -1) + "23");
            query.setString("installDate", searchStartDate + CommonConstants.DefaultDate.LAST_HHMMSS.getValue());
            query.setInteger("channel", channel);
            if (!"".equals(supplierId)) {
                query.setInteger("supplierId", Integer.parseInt(supplierId));
            }
            query.setString("deleteCode", MeterCodes.DELETE_STATUS.getCode());
            query.setString("deleteDate", searchStartDate + "235959");

            // query 결과목록 - 시작일부터 종료일전일까지의 결과
            List<Map<String, Object>> successDataList = query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();

            for (Map<String, Object> obj : successDataList) {
                successData.put((String) obj.get("YYYYMMDD"), DecimalUtil.ConvertNumberToInteger(obj.get("SUCCESS_CNT")));
            }

            // 현재일자의 현재시간 전까지의 데이터와 현재시간의 데이터를 union 하여 누락건수를 조회한다.
            StringBuffer queryTodayCurrHour = new StringBuffer();
            queryTodayCurrHour.append("\nSELECT COUNT(*) ");
            queryTodayCurrHour.append("\nFROM ( ");
            queryTodayCurrHour.append(sbQueryToday);
            queryTodayCurrHour.append("\nUNION ");
            queryTodayCurrHour.append(sbQueryCurrHour);
            queryTodayCurrHour.append("\n)x ");

            Query queryToday = getSession().createSQLQuery(queryTodayCurrHour.toString());
            queryToday.setString("startDate", searchEndDate + "00");
            queryToday.setString("endDate", searchEndDate + "23");
            queryToday.setString("installDate", searchStartDate + CommonConstants.DefaultDate.LAST_HHMMSS.getValue());
            queryToday.setString("currHour", currHour);
            queryToday.setInteger("hourCnt", Integer.parseInt(currHour));
            if (currMinute >= 1) {
                queryToday.setInteger("currMinute", currMinute);
            }
            queryToday.setInteger("channel", channel);
            if (!"".equals(supplierId)) {
                queryToday.setInteger("supplierId", Integer.parseInt(supplierId));
            }
            query.setString("deleteCode", MeterCodes.DELETE_STATUS.getCode());
            query.setString("deleteDate", searchStartDate + "235959");

            // query 결과목록 - 오늘날짜의 현재시간 전까지의 결과
            Number todaySuccessCnt = (Number) queryToday.uniqueResult();
            todayMissingCount = totalMeterCnt - todaySuccessCnt.intValue();
        }

        Supplier supplier = supplierDao.get(Integer.parseInt(supplierId));
        DecimalFormat dfMd = DecimalUtil.getMDStyle(supplier.getMd());
        String lang = supplier.getLang().getCode_2letter();
        String country = supplier.getCountry().getCode_2letter();
        Integer successCount = null;
        HashMap<String, Object> resultMap = null;
        // 특정일자에 모두 누락될수도 있으므로 시작일부터 종료일까지의 데이터는 고정
        for (int i = 0; i < period; i++) {
            String yyyymmdd = CalendarUtil.getDateWithoutFormat(searchStartDate, Calendar.DATE, i);
            resultMap = new HashMap<String, Object>();
            resultMap.put("no", dfMd.format(i + 1));
            resultMap.put("yyyymmdd", TimeLocaleUtil.getLocaleDate(yyyymmdd, lang, country));
            resultMap.put("hh", "-");

            if (yyyymmdd.equals(currDate)) { // 오늘날짜이고 현재시간 이후일경우 0으로 세팅한다.
                resultMap.put("missingCount", dfMd.format(todayMissingCount));
            } else {
                successCount = successData.get(yyyymmdd) == null ? 0 : ((Number) successData.get(yyyymmdd)).intValue();
                resultMap.put("missingCount", dfMd.format(totalMeterCnt - successCount));
            }

            resultMap.put("xField", TimeLocaleUtil.getLocaleDate(yyyymmdd));

            resultList.add(resultMap);
        }

        return resultList;
    }

    /**
     * 검침데이터(LP) 에 누락된 미터목록을 조회한다.
     * @param params
     * @return
     */
    @Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
    public List<Object> getMissingMeters(Map<String, Object> params) {
        String searchStartDate = (String) params.get("searchStartDate");
        String searchEndDate = (String) params.get("searchEndDate");
        String meterType = StringUtil.nullToBlank(params.get("meterType"));
        String supplierId = StringUtil.nullToBlank(params.get("supplierId"));
        Integer channel = (Integer) params.get("channel");
        String mdsId = StringUtil.nullToBlank(params.get("mdsId"));
        String deviceType = StringUtil.nullToBlank((String) params.get("deviceType"));
        String deviceId = StringUtil.nullToBlank((String) params.get("deviceId"));
        String sysId = StringUtil.nullToBlank((String) params.get("sysId")); // SP-677
        String today = TimeUtil.getCurrentTimeMilli(); // yyyyMMddHHmmss
        String currDate = today.substring(0, 8);
        String currHour = today.substring(8, 10);
        Integer currMinute = Integer.parseInt(today.substring(10, 12));

        String lpTable = CommonConstants.MeterType.valueOf(meterType).getLpTableName();

        int period = 0;
        try {
            period = TimeUtil.getDayDuration(searchStartDate, searchEndDate) + 1;
        } catch (ParseException e) {
            e.printStackTrace();
        }

        StringBuffer sbQueryAllPeriod = new StringBuffer();
        sbQueryAllPeriod.append("\nSELECT customer.name ");
        sbQueryAllPeriod.append("\n       ,modem.device_serial ");
        sbQueryAllPeriod.append("\n       ,mcu.sys_name AS mcuName ");
        sbQueryAllPeriod.append("\n       ,m1.mds_id ");
        sbQueryAllPeriod.append("\n       ,m1.last_read_date ");
        sbQueryAllPeriod.append("\n       ,m1.id ");
        sbQueryAllPeriod.append("\n       ,m1.lp_interval ");
        sbQueryAllPeriod.append("\nFROM meter m1 ");
        sbQueryAllPeriod.append("\n     LEFT OUTER JOIN contract contract ON m1.id = contract.meter_id ");
        sbQueryAllPeriod.append("\n     LEFT OUTER JOIN customer customer ON contract.customer_id = customer.id ");
        sbQueryAllPeriod.append("\n     LEFT OUTER JOIN modem modem ON m1.modem_id = modem.id ");
        sbQueryAllPeriod.append("\n     LEFT OUTER JOIN mcu mcu ON modem.mcu_id = mcu.id ");
        sbQueryAllPeriod.append("\n     LEFT OUTER JOIN code cd ON cd.id = m1.meter_status ");
        sbQueryAllPeriod.append("\nWHERE m1.install_date <= :installDate ");
        sbQueryAllPeriod.append("\nAND m1.meter = :meterType ");
        if (!"".equals(supplierId)) {
            sbQueryAllPeriod.append("\nAND m1.supplier_id = :supplierId ");
        }
        if (!"".equals(mdsId)) {
            sbQueryAllPeriod.append("\nAND m1.mds_id LIKE :mdsId ");
        }
        if ( !"".equals(sysId) ) {
            	sbQueryAllPeriod.append("\nAND mcu.sys_id = :sysId ");
        }
        // deviceId , deviceType 의 null 값 체크
        if (!"".equals(deviceId) && !"".equals(deviceType)) {
            if (CommonConstants.DeviceType.MCU.getCode().equals(Integer.parseInt(deviceType))) {
                sbQueryAllPeriod.append("\nAND mcu.sys_name LIKE :deviceId ");
            } else {
                sbQueryAllPeriod.append("\nAND modem.device_serial LIKE :deviceId ");
            }
        }
        sbQueryAllPeriod.append("\nAND   (cd.id IS NULL ");
        sbQueryAllPeriod.append("\n    OR cd.code != :deleteCode ");
        sbQueryAllPeriod.append("\n    OR (cd.code = :deleteCode AND m1.delete_date > :deleteDate)) ");
        sbQueryAllPeriod.append("\nAND m1.id NOT IN ( ");
        sbQueryAllPeriod.append("\n    SELECT x.id ");
        sbQueryAllPeriod.append("\n    FROM ( ");
        sbQueryAllPeriod.append("\n        SELECT m.id,lp.yyyymmdd ");
        sbQueryAllPeriod.append("\n        FROM meter m ");
        sbQueryAllPeriod.append("\n             LEFT OUTER JOIN ");
        sbQueryAllPeriod.append("\n             code c ");
        sbQueryAllPeriod.append("\n             ON c.id = m.meter_status ");
        sbQueryAllPeriod.append("\n             LEFT OUTER JOIN ");
        sbQueryAllPeriod.append("\n             ").append(lpTable).append(" lp ");
        sbQueryAllPeriod.append("\n             ON  lp.mdev_id = m.mds_id ");
        sbQueryAllPeriod.append("\n             AND lp.yyyymmddhh BETWEEN :startDate AND :endDate ");
        sbQueryAllPeriod.append("\n             AND lp.channel = :channel ");
        sbQueryAllPeriod.append("\n        WHERE m.install_date <= :installDate ");
//        sbQueryAllPeriod.append("\n        AND 1 = CASE WHEN m.lp_interval = 1 THEN CASE WHEN lp.value_cnt < 60   THEN 0 ELSE 1 END ");
//        sbQueryAllPeriod.append("\n                     WHEN m.lp_interval = 5 THEN CASE WHEN lp.value_cnt < 12   THEN 0 ELSE 1 END ");
//        sbQueryAllPeriod.append("\n                     WHEN m.lp_interval = 10 THEN CASE WHEN lp.value_cnt < 6   THEN 0 ELSE 1 END ");
//        sbQueryAllPeriod.append("\n                     WHEN m.lp_interval = 15 THEN CASE WHEN lp.value_cnt < 4   THEN 0 ELSE 1 END ");
//        sbQueryAllPeriod.append("\n                     WHEN m.lp_interval = 30 THEN CASE WHEN lp.value_cnt < 2   THEN 0 ELSE 1 END ");
//        sbQueryAllPeriod.append("\n                     WHEN m.lp_interval = 60 THEN CASE WHEN lp.value_cnt < 1   THEN 0 ELSE 1 END ");
//        sbQueryAllPeriod.append("\n                ELSE 0 end ");
        sbQueryAllPeriod.append("\n        AND   (m.lp_interval IN (1, 5, 10, 15, 30, 60) ");
        sbQueryAllPeriod.append("\n           AND lp.value_cnt >= (60/m.lp_interval)) ");
        sbQueryAllPeriod.append("\n        AND   (c.id IS NULL ");
        sbQueryAllPeriod.append("\n            OR c.code != :deleteCode ");
        sbQueryAllPeriod.append("\n            OR (c.code = :deleteCode AND m.delete_date > :deleteDate)) ");
        sbQueryAllPeriod.append("\n        GROUP BY m.id, lp.yyyymmdd ");
        sbQueryAllPeriod.append("\n        HAVING COUNT(m.id) = 24 ");
        sbQueryAllPeriod.append("\n    ) x ");
        sbQueryAllPeriod.append("\n    WHERE x.yyyymmdd IS NOT NULL ");
        sbQueryAllPeriod.append("\n    GROUP BY x.id ");
        sbQueryAllPeriod.append("\n    HAVING COUNT(x.id) = :period ");
        sbQueryAllPeriod.append("\n) ");

        StringBuffer sbQueryAllPeriodWithToday = new StringBuffer();
        sbQueryAllPeriodWithToday.append("\nSELECT customer.name ");
        sbQueryAllPeriodWithToday.append("\n       ,modem.device_serial ");
        sbQueryAllPeriodWithToday.append("\n       ,mcu.sys_name AS mcuname ");
        sbQueryAllPeriodWithToday.append("\n       ,m1.mds_id ");
        sbQueryAllPeriodWithToday.append("\n       ,m1.last_read_date ");
        sbQueryAllPeriodWithToday.append("\n       ,m1.id ");
        sbQueryAllPeriodWithToday.append("\n       ,m1.lp_interval ");
        sbQueryAllPeriodWithToday.append("\nFROM meter m1 ");
        sbQueryAllPeriodWithToday.append("\n     LEFT OUTER JOIN contract contract ON m1.id = contract.meter_id ");
        sbQueryAllPeriodWithToday.append("\n     LEFT OUTER JOIN customer customer ON contract.customer_id = customer.id ");
        sbQueryAllPeriodWithToday.append("\n     LEFT OUTER JOIN modem modem ON m1.modem_id = modem.id ");
        sbQueryAllPeriodWithToday.append("\n     LEFT OUTER JOIN mcu mcu ON modem.mcu_id = mcu.id ");
        sbQueryAllPeriodWithToday.append("\n     LEFT OUTER JOIN code cd ON cd.id = m1.meter_status ");
        sbQueryAllPeriodWithToday.append("\nWHERE m1.install_date <= :installDate ");
        sbQueryAllPeriodWithToday.append("\nAND   m1.meter = :meterType ");
        if (!"".equals(supplierId)) {
            sbQueryAllPeriodWithToday.append("\nAND   m1.supplier_id = :supplierId ");
        }
        if (!"".equals(mdsId)) {
            sbQueryAllPeriodWithToday.append("\nAND   m1.mds_id LIKE :mdsId ");
        }
        if ( !"".equals(sysId) ) {
        	sbQueryAllPeriodWithToday.append("\nAND mcu.sys_id = :sysId ");
        }
        // deviceId , deviceType 의 null 값 체크
        if (!"".equals(deviceId) && !"".equals(deviceType)) {
            if (CommonConstants.DeviceType.MCU.getCode().equals(Integer.parseInt(deviceType))) {
                sbQueryAllPeriodWithToday.append("\nAND   mcu.sys_name LIKE :deviceId ");
            } else {
                sbQueryAllPeriodWithToday.append("\nAND   modem.device_serial LIKE :deviceId ");
            }
        }
        sbQueryAllPeriodWithToday.append("\nAND   (cd.id IS NULL ");
        sbQueryAllPeriodWithToday.append("\n    OR cd.code != :deleteCode ");
        sbQueryAllPeriodWithToday.append("\n    OR (cd.code = :deleteCode AND m1.delete_date > :deleteDate)) ");
        sbQueryAllPeriodWithToday.append("\nAND   m1.id NOT IN ( ");
        sbQueryAllPeriodWithToday.append("\n    SELECT i3.id");
        sbQueryAllPeriodWithToday.append("\n    FROM ( ");
        sbQueryAllPeriodWithToday.append("\n        SELECT i1.id FROM ( ");
        sbQueryAllPeriodWithToday.append("\n            SELECT x.id ");
        sbQueryAllPeriodWithToday.append("\n            FROM ( ");
        sbQueryAllPeriodWithToday.append("\n                SELECT m.id,lp.yyyymmdd ");
        sbQueryAllPeriodWithToday.append("\n                FROM meter m ");
        sbQueryAllPeriodWithToday.append("\n                     LEFT OUTER JOIN ");
        sbQueryAllPeriodWithToday.append("\n                     code c ");
        sbQueryAllPeriodWithToday.append("\n                     ON c.id = m.meter_status ");
        sbQueryAllPeriodWithToday.append("\n                     LEFT OUTER JOIN ").append(lpTable).append(" lp ");
        sbQueryAllPeriodWithToday.append("\n                     ON lp.mdev_id = m.mds_id ");
        sbQueryAllPeriodWithToday.append("\n                     AND lp.yyyymmddhh BETWEEN :startDate AND :endDate ");
        sbQueryAllPeriodWithToday.append("\n                     AND lp.channel = :channel ");
        sbQueryAllPeriodWithToday.append("\n                WHERE m.install_date <= :installDate ");
//        sbQueryAllPeriodWithToday.append("\n        AND 1 = CASE WHEN m.lp_interval = 1 THEN CASE WHEN lp.value_cnt < 60 THEN 0 ELSE 1 END ");
//        sbQueryAllPeriodWithToday.append("\n                     WHEN m.lp_interval = 5 THEN CASE WHEN lp.value_cnt < 12 THEN 0 ELSE 1 END ");
//        sbQueryAllPeriodWithToday.append("\n                     WHEN m.lp_interval = 10 THEN CASE WHEN lp.value_cnt < 6 THEN 0 ELSE 1 END ");
//        sbQueryAllPeriodWithToday.append("\n                     WHEN m.lp_interval = 15 THEN CASE WHEN lp.value_cnt < 4 THEN 0 ELSE 1 END ");
//        sbQueryAllPeriodWithToday.append("\n                     WHEN m.lp_interval = 30 THEN CASE WHEN lp.value_cnt < 2 THEN 0 ELSE 1 END ");
//        sbQueryAllPeriodWithToday.append("\n                     WHEN m.lp_interval = 60 THEN CASE WHEN lp.value_cnt < 1 THEN 0 ELSE 1 END ");
//        sbQueryAllPeriodWithToday.append("\n                ELSE 0 end ");
        sbQueryAllPeriodWithToday.append("\n                AND   (m.lp_interval IN (1, 5, 10, 15, 30, 60) ");
        sbQueryAllPeriodWithToday.append("\n                   AND lp.value_cnt >= (60/m.lp_interval)) ");
        sbQueryAllPeriodWithToday.append("\n                AND   (c.id IS NULL ");
        sbQueryAllPeriodWithToday.append("\n                    OR c.code != :deleteCode ");
        sbQueryAllPeriodWithToday.append("\n                    OR (c.code = :deleteCode AND m.delete_date > :deleteDate)) ");
        sbQueryAllPeriodWithToday.append("\n                GROUP BY m.id,lp.yyyymmdd ");
        sbQueryAllPeriodWithToday.append("\n                HAVING COUNT(m.id) = 24 ");
        sbQueryAllPeriodWithToday.append("\n            )x ");
        sbQueryAllPeriodWithToday.append("\n            WHERE x.yyyymmdd IS NOT NULL ");
        sbQueryAllPeriodWithToday.append("\n            GROUP BY x.id ");
        sbQueryAllPeriodWithToday.append("\n            HAVING COUNT(x.id) = :period ");
        sbQueryAllPeriodWithToday.append("\n        ) i1 ");

        sbQueryAllPeriodWithToday.append("\n        INNER JOIN ");

        sbQueryAllPeriodWithToday.append("\n        ( ");
        sbQueryAllPeriodWithToday.append("\n            SELECT m.id ");
        sbQueryAllPeriodWithToday.append("\n            FROM meter m ");
        sbQueryAllPeriodWithToday.append("\n                 LEFT OUTER JOIN ");
        sbQueryAllPeriodWithToday.append("\n                 code c ");
        sbQueryAllPeriodWithToday.append("\n                 ON c.id = m.meter_status ");
        sbQueryAllPeriodWithToday.append("\n                 LEFT OUTER JOIN ").append(lpTable).append(" lp ");
        sbQueryAllPeriodWithToday.append("\n                 ON lp.mdev_id = m.mds_id ");
        sbQueryAllPeriodWithToday.append("\n                 AND lp.yyyymmddhh BETWEEN :currStartDate AND :currEndDate ");
        sbQueryAllPeriodWithToday.append("\n                 AND lp.hh < :currHour ");
        sbQueryAllPeriodWithToday.append("\n                 AND lp.channel = :channel ");
        sbQueryAllPeriodWithToday.append("\n            WHERE m.install_date <= :installDate ");
//        sbQueryAllPeriodWithToday.append("\n    AND 1 = CASE WHEN m.lp_interval = 1 THEN CASE WHEN lp.value_cnt < 60   THEN 0 ELSE 1 END ");
//        sbQueryAllPeriodWithToday.append("\n                 WHEN m.lp_interval = 5 THEN CASE WHEN lp.value_cnt < 12   THEN 0 ELSE 1 END ");
//        sbQueryAllPeriodWithToday.append("\n                 WHEN m.lp_interval = 10 THEN CASE WHEN lp.value_cnt < 6   THEN 0 ELSE 1 END ");
//        sbQueryAllPeriodWithToday.append("\n                 WHEN m.lp_interval = 15 THEN CASE WHEN lp.value_cnt < 4   THEN 0 ELSE 1 END ");
//        sbQueryAllPeriodWithToday.append("\n                 WHEN m.lp_interval = 30 THEN CASE WHEN lp.value_cnt < 2   THEN 0 ELSE 1 END ");
//        sbQueryAllPeriodWithToday.append("\n                 WHEN m.lp_interval = 60 THEN CASE WHEN lp.value_cnt < 1   THEN 0 ELSE 1 END ");
//        sbQueryAllPeriodWithToday.append("\n            ELSE 0 end ");
        sbQueryAllPeriodWithToday.append("\n            AND   (m.lp_interval IN (1, 5, 10, 15, 30, 60) ");
        sbQueryAllPeriodWithToday.append("\n               AND lp.value_cnt >= (60/m.lp_interval)) ");
        sbQueryAllPeriodWithToday.append("\n            AND   (c.id IS NULL ");
        sbQueryAllPeriodWithToday.append("\n                OR c.code != :deleteCode ");
        sbQueryAllPeriodWithToday.append("\n                OR (c.code = :deleteCode AND m.delete_date > :deleteDate)) ");
        sbQueryAllPeriodWithToday.append("\n            GROUP BY m.id, lp.yyyymmdd ");
//        sbQueryAllPeriodWithToday.append("\n            HAVING COUNT(m.id) = :currHour) i2 ON i1.id=i2.id ) i3");
        sbQueryAllPeriodWithToday.append("\n            HAVING COUNT(m.id) = :currHour ");
        sbQueryAllPeriodWithToday.append("\n        ) i2 ON i1.id = i2.id ");
        sbQueryAllPeriodWithToday.append("\n    ) i3");

        sbQueryAllPeriodWithToday.append("\n    INNER JOIN ");

        sbQueryAllPeriodWithToday.append("\n    ( ");
        sbQueryAllPeriodWithToday.append("\n        SELECT m.id ");
        sbQueryAllPeriodWithToday.append("\n        FROM meter m ");
        sbQueryAllPeriodWithToday.append("\n             LEFT OUTER JOIN ");
        sbQueryAllPeriodWithToday.append("\n             code c ");
        sbQueryAllPeriodWithToday.append("\n             ON c.id = m.meter_status ");
        sbQueryAllPeriodWithToday.append("\n             LEFT OUTER JOIN ").append(lpTable).append(" lp ");
        sbQueryAllPeriodWithToday.append("\n             ON lp.mdev_id = m.mds_id ");
        sbQueryAllPeriodWithToday.append("\n             AND lp.yyyymmddhh BETWEEN :currStartDate AND :currEndDate ");
        sbQueryAllPeriodWithToday.append("\n             AND lp.hh = :currHour ");
        sbQueryAllPeriodWithToday.append("\n             AND lp.channel = :channel ");
        sbQueryAllPeriodWithToday.append("\n        WHERE m.install_date <= :installDate ");

        if (currMinute < 1) {
//            sbQueryAllPeriodWithToday.append("\n        AND 1 = CASE WHEN m.lp_interval = 1  THEN CASE WHEN 1 <= lp.value_cnt THEN 1 ELSE 0 END ");
//            sbQueryAllPeriodWithToday.append("\n                     WHEN m.lp_interval = 5  THEN CASE WHEN 1 <= lp.value_cnt THEN 1 ELSE 0 END ");
//            sbQueryAllPeriodWithToday.append("\n                     WHEN m.lp_interval = 10 THEN CASE WHEN 1 <= lp.value_cnt THEN 1 ELSE 0 END ");
//            sbQueryAllPeriodWithToday.append("\n                     WHEN m.lp_interval = 15 THEN CASE WHEN 1 <= lp.value_cnt THEN 1 ELSE 0 END ");
//            sbQueryAllPeriodWithToday.append("\n                     WHEN m.lp_interval = 30 THEN CASE WHEN 1 <= lp.value_cnt THEN 1 ELSE 0 END ");
//            sbQueryAllPeriodWithToday.append("\n                     WHEN m.lp_interval = 60 THEN CASE WHEN 1 <= lp.value_cnt THEN 1 ELSE 0 END ");
//            sbQueryAllPeriodWithToday.append("\n                ELSE 0 END) i4 ON i3.id=i4.id ");
            sbQueryAllPeriodWithToday.append("\n        AND   (m.lp_interval IN (1, 5, 10, 15, 30, 60) ");
            sbQueryAllPeriodWithToday.append("\n           AND lp.value_cnt >= 1) ");
        } else {
//            sbQueryAllPeriodWithToday.append("\n        AND 1 = CASE WHEN m.lp_interval = 1  THEN CASE WHEN (:currMinute/1) < lp.value_cnt THEN 1 ELSE 0 END ");
//            sbQueryAllPeriodWithToday.append("\n                     WHEN m.lp_interval = 5  THEN CASE WHEN (:currMinute/5) < lp.value_cnt THEN 1 ELSE 0 END ");
//            sbQueryAllPeriodWithToday.append("\n                     WHEN m.lp_interval = 10 THEN CASE WHEN (:currMinute/10) < lp.value_cnt THEN 1 ELSE 0 END ");
//            sbQueryAllPeriodWithToday.append("\n                     WHEN m.lp_interval = 15 THEN CASE WHEN (:currMinute/15) < lp.value_cnt THEN 1 ELSE 0 END ");
//            sbQueryAllPeriodWithToday.append("\n                     WHEN m.lp_interval = 30 THEN CASE WHEN (:currMinute/30) < lp.value_cnt THEN 1 ELSE 0 END ");
//            sbQueryAllPeriodWithToday.append("\n                     WHEN m.lp_interval = 60 THEN CASE WHEN (:currMinute/60) < lp.value_cnt THEN 1 ELSE 0 END ");
//            sbQueryAllPeriodWithToday.append("\n                ELSE 0 END) i4 ON i3.id=i4.id ");
            sbQueryAllPeriodWithToday.append("\n        AND   (m.lp_interval IN (1, 5, 10, 15, 30, 60) ");
            sbQueryAllPeriodWithToday.append("\n           AND lp.value_cnt > (:currMinute/m.lp_interval)) ");
        }
        sbQueryAllPeriodWithToday.append("\n        AND   (c.id IS NULL ");
        sbQueryAllPeriodWithToday.append("\n            OR c.code != :deleteCode ");
        sbQueryAllPeriodWithToday.append("\n            OR (c.code = :deleteCode AND m.delete_date > :deleteDate)) ");
        sbQueryAllPeriodWithToday.append("\n    ) i4 ON i3.id = i4.id ");
        sbQueryAllPeriodWithToday.append("\n) ");

        StringBuffer sbQueryToday = new StringBuffer();
        sbQueryToday.append("\nSELECT customer.name ");
        sbQueryToday.append("\n       ,modem.device_serial ");
        sbQueryToday.append("\n       ,mcu.sys_name as mcuName ");
        sbQueryToday.append("\n       ,m1.mds_id ");
        sbQueryToday.append("\n       ,m1.last_read_date ");
        sbQueryToday.append("\n       ,m1.id ");
        sbQueryToday.append("\n       ,m1.lp_interval ");
        sbQueryToday.append("\nFROM meter m1 ");
        sbQueryToday.append("\n     LEFT OUTER JOIN contract contract ON m1.id = contract.meter_id ");
        sbQueryToday.append("\n     LEFT OUTER JOIN customer customer ON contract.customer_id = customer.id ");
        sbQueryToday.append("\n     LEFT OUTER JOIN modem modem ON m1.modem_id = modem.id ");
        sbQueryToday.append("\n     LEFT OUTER JOIN mcu mcu ON modem.mcu_id = mcu.id ");
        sbQueryToday.append("\n     LEFT OUTER JOIN code cd ON cd.id = m1.meter_status ");
        sbQueryToday.append("\nWHERE m1.install_date <= :installDate ");
        sbQueryToday.append("\nAND   m1.meter = :meterType ");
        if (!"".equals(supplierId)) {
            sbQueryToday.append("\nAND   m1.supplier_id = :supplierId ");
        }
        if (!"".equals(mdsId)) {
            sbQueryToday.append("\nAND   m1.mds_id LIKE :mdsId ");
        }
        if ( !"".equals(sysId) ) {
        	sbQueryToday.append("\nAND mcu.sys_id = :sysId ");
        }
        // deviceId , deviceType 의 null 값 체크
        if (!"".equals(deviceId) && !"".equals(deviceType)) {
            if (CommonConstants.DeviceType.MCU.getCode().equals(Integer.parseInt(deviceType))) {
                sbQueryToday.append("\nAND   mcu.sys_name LIKE :deviceId ");
            } else {
                sbQueryToday.append("\nAND   modem.device_serial LIKE :deviceId ");
            }
        }
        sbQueryToday.append("\nAND   (cd.id IS NULL ");
        sbQueryToday.append("\n    OR cd.code != :deleteCode ");
        sbQueryToday.append("\n    OR (cd.code = :deleteCode AND m1.delete_date > :deleteDate)) ");
        sbQueryToday.append("\nAND   m1.id NOT IN ( ");
        sbQueryToday.append("\n    SELECT i1.id ");
        sbQueryToday.append("\n    FROM ( ");
        sbQueryToday.append("\n        SELECT m.id ");
        sbQueryToday.append("\n        FROM meter m ");
        sbQueryToday.append("\n             LEFT OUTER JOIN ");
        sbQueryToday.append("\n             code c ");
        sbQueryToday.append("\n             ON c.id = m.meter_status ");
        sbQueryToday.append("\n             LEFT OUTER JOIN ").append(lpTable).append(" lp ");
        sbQueryToday.append("\n             ON lp.mdev_id = m.mds_id ");
        sbQueryToday.append("\n        AND lp.yyyymmddhh BETWEEN :currStartDate AND :currEndDate ");
        sbQueryToday.append("\n        AND lp.hh < :currHour ");
        sbQueryToday.append("\n        AND lp.channel = :channel ");
        sbQueryToday.append("\n        WHERE m.install_date <= :installDate ");
//        sbQueryToday.append("\n        AND 1 = CASE WHEN m.lp_interval = 1 THEN CASE WHEN lp.value_cnt < 60   THEN 0 ELSE 1 END ");
//        sbQueryToday.append("\n                     WHEN m.lp_interval = 5 THEN CASE WHEN lp.value_cnt < 12   THEN 0 ELSE 1 END ");
//        sbQueryToday.append("\n                     WHEN m.lp_interval = 10 THEN CASE WHEN lp.value_cnt < 6   THEN 0 ELSE 1 END ");
//        sbQueryToday.append("\n                     WHEN m.lp_interval = 15 THEN CASE WHEN lp.value_cnt < 4   THEN 0 ELSE 1 END ");
//        sbQueryToday.append("\n                     WHEN m.lp_interval = 30 THEN CASE WHEN lp.value_cnt < 2   THEN 0 ELSE 1 END ");
//        sbQueryToday.append("\n                     WHEN m.lp_interval = 60 THEN CASE WHEN lp.value_cnt < 1   THEN 0 ELSE 1 END ");
//        sbQueryToday.append("\n                ELSE 0 end ");
        sbQueryToday.append("\n        AND   (m.lp_interval IN (1, 5, 10, 15, 30, 60) ");
        sbQueryToday.append("\n           AND lp.value_cnt >= (60/m.lp_interval)) ");
        sbQueryToday.append("\n        AND   (c.id IS NULL ");
        sbQueryToday.append("\n            OR c.code != :deleteCode ");
        sbQueryToday.append("\n            OR (c.code = :deleteCode AND m.delete_date > :deleteDate)) ");
        sbQueryToday.append("\n        GROUP BY m.id,lp.yyyymmdd ");
        sbQueryToday.append("\n        HAVING COUNT(m.id) = :currHour ");
        sbQueryToday.append("\n    ) i1 ");

        sbQueryToday.append("\n    INNER JOIN ");

        sbQueryToday.append("\n    ( ");
        sbQueryToday.append("\n        SELECT m.id ");
        sbQueryToday.append("\n        FROM meter m ");
        sbQueryToday.append("\n             LEFT OUTER JOIN ");
        sbQueryToday.append("\n             code c ");
        sbQueryToday.append("\n             ON c.id = m.meter_status ");
        sbQueryToday.append("\n             LEFT OUTER JOIN ").append(lpTable).append(" lp ");
        sbQueryToday.append("\n             ON lp.mdev_id = m.mds_id ");
        sbQueryToday.append("\n             AND lp.yyyymmddhh BETWEEN :currStartDate AND :currEndDate ");
        sbQueryToday.append("\n             AND lp.hh = :currHour ");
        sbQueryToday.append("\n             AND lp.channel = :channel ");
        sbQueryToday.append("\n        WHERE m.install_date <= :installDate ");
        if (currMinute < 1) {
//            sbQueryToday.append("\n        AND 1 = CASE WHEN m.lp_interval = 1  THEN CASE WHEN 1 <= lp.value_cnt THEN 1 ELSE 0 END ");
//            sbQueryToday.append("\n                     WHEN m.lp_interval = 5  THEN CASE WHEN 1 <= lp.value_cnt THEN 1 ELSE 0 END ");
//            sbQueryToday.append("\n                     WHEN m.lp_interval = 10 THEN CASE WHEN 1 <= lp.value_cnt THEN 1 ELSE 0 END ");
//            sbQueryToday.append("\n                     WHEN m.lp_interval = 15 THEN CASE WHEN 1 <= lp.value_cnt THEN 1 ELSE 0 END ");
//            sbQueryToday.append("\n                     WHEN m.lp_interval = 30 THEN CASE WHEN 1 <= lp.value_cnt THEN 1 ELSE 0 END ");
//            sbQueryToday.append("\n                     WHEN m.lp_interval = 60 THEN CASE WHEN 1 <= lp.value_cnt THEN 1 ELSE 0 END ");
//            sbQueryToday.append("\n                ELSE 0 END) i2 ON i1.id=i2.id  ");
            sbQueryToday.append("\n        AND   (m.lp_interval IN (1, 5, 10, 15, 30, 60) ");
            sbQueryToday.append("\n           AND lp.value_cnt >= 1) ");
        } else {
//            sbQueryToday.append("\n        AND 1 = CASE WHEN m.lp_interval = 1  THEN CASE WHEN (:currMinute/1) < lp.value_cnt THEN 1 ELSE 0 END ");
//            sbQueryToday.append("\n                     WHEN m.lp_interval = 5  THEN CASE WHEN (:currMinute/5) < lp.value_cnt THEN 1 ELSE 0 END ");
//            sbQueryToday.append("\n                     WHEN m.lp_interval = 10 THEN CASE WHEN (:currMinute/10) < lp.value_cnt THEN 1 ELSE 0 END ");
//            sbQueryToday.append("\n                     WHEN m.lp_interval = 15 THEN CASE WHEN (:currMinute/15) < lp.value_cnt THEN 1 ELSE 0 END ");
//            sbQueryToday.append("\n                     WHEN m.lp_interval = 30 THEN CASE WHEN (:currMinute/30) < lp.value_cnt THEN 1 ELSE 0 END ");
//            sbQueryToday.append("\n                     WHEN m.lp_interval = 60 THEN CASE WHEN (:currMinute/60) < lp.value_cnt THEN 1 ELSE 0 END ");
//            sbQueryToday.append("\n                ELSE 0 END) i2 ON i1.id=i2.id ");
            sbQueryToday.append("\n        AND   (m.lp_interval IN (1, 5, 10, 15, 30, 60) ");
            sbQueryToday.append("\n           AND lp.value_cnt > (:currMinute/m.lp_interval)) ");
        }
        sbQueryToday.append("\n        AND   (c.id IS NULL ");
        sbQueryToday.append("\n            OR c.code != :deleteCode ");
        sbQueryToday.append("\n            OR (c.code = :deleteCode AND m.delete_date > :deleteDate)) ");
        sbQueryToday.append("\n    ) i2 ON i1.id = i2.id ");
        sbQueryToday.append("\n) ");

        Query query = null;
        if (Integer.parseInt(searchEndDate) < Integer.parseInt(currDate)) {
            // 조회종료일이 현재일자보다 이전일 경우 전체일자 조회
            query = getSession().createSQLQuery(sbQueryAllPeriod.toString());
            query.setString("startDate", searchStartDate + "00");
            query.setString("endDate", searchEndDate + "23");
            query.setString("installDate", searchStartDate + CommonConstants.DefaultDate.LAST_HHMMSS.getValue());
            query.setString("meterType", meterType);
            query.setInteger("channel", channel);
            query.setInteger("period", period);
            if (!"".equals(supplierId)) {
                query.setInteger("supplierId", Integer.parseInt(supplierId));
            }
            if (!"".equals(mdsId)) {
                query.setString("mdsId", "%" + mdsId + "%");
            }
            if (!"".equals(sysId)){
                query.setString("sysId", sysId );
            }
            if (!"".equals(deviceId)) {
                query.setString("deviceId", "%" + deviceId + "%");
            }

        } else {
            if (Integer.parseInt(searchStartDate) < Integer.parseInt(currDate)) {
                // 조회종료일이 현재일자이고 조회시작일이 현재일자 이전일경우 시작일~종료일전일,현재일자,현재시간 별로 조회
                query = getSession().createSQLQuery(sbQueryAllPeriodWithToday.toString());
                query.setString("startDate", searchStartDate + "00");
                query.setString("endDate", CalendarUtil.getDateWithoutFormat(searchEndDate, Calendar.DATE, -1) + "23");
                query.setString("installDate", searchStartDate + CommonConstants.DefaultDate.LAST_HHMMSS.getValue());
                query.setString("meterType", meterType);
                query.setString("currStartDate", currDate + "00");
                query.setString("currEndDate", currDate + "23");
                query.setString("currHour", currHour);
                if (currMinute >= 1) {
                    query.setInteger("currMinute", currMinute);
                }
                query.setInteger("channel", channel);
                query.setInteger("period", period);
                if (!"".equals(supplierId)) {
                    query.setInteger("supplierId", Integer.parseInt(supplierId));
                }
                if (!"".equals(mdsId)) {
                    query.setString("mdsId", "%" + mdsId + "%");
                }
                if (!"".equals(sysId)){
                	query.setString("sysId", sysId);
                }
                if (!"".equals(deviceId)) {
                    query.setString("deviceId", "%" + deviceId + "%");
                }
            } else {
                // 조회일자가 현재일자 하루일경우 현재날짜,현재시간만 조회
                query = getSession().createSQLQuery(sbQueryToday.toString());
                query.setString("installDate", searchStartDate + CommonConstants.DefaultDate.LAST_HHMMSS.getValue());
                query.setString("meterType", meterType);
                query.setString("currStartDate", currDate + "00");
                query.setString("currEndDate", currDate + "23");
                query.setString("currHour", currHour);
                if (currMinute >= 1) {
                    query.setInteger("currMinute", currMinute);
                }
                query.setInteger("channel", channel);
                if (!"".equals(supplierId)) {
                    query.setInteger("supplierId", Integer.parseInt(supplierId));
                }
                if (!"".equals(mdsId)) {
                    query.setString("mdsId", "%" + mdsId + "%");
                }
                if (!"".equals(sysId)){
                	query.setString("sysId", sysId);
                }
                if (!"".equals(deviceId)) {
                    query.setString("deviceId", "%" + deviceId + "%");
                }
            }
        }
        query.setString("deleteCode", MeterCodes.DELETE_STATUS.getCode());
        query.setString("deleteDate", searchStartDate + "235959");

        // query 결과목록
        List<Object> result = query.list();

        List<Object> resultList = new ArrayList<Object>();

        int i = 1;
        for (Object obj : result) {
            HashMap<String, Object> resultMap = new HashMap<String, Object>();
            Object[] objs = (Object[]) obj;

            params.put("meterId", ((Number) objs[5]).intValue());
            params.put("mdsId", (String) objs[3]);

            if (objs[6] == null) {
                params.put("lpInterval", 60); // lpInterval 값이 null일 경우 기본값(60) 입력
            } else {
                params.put("lpInterval", ((Number) objs[6]).intValue());
            }

            Map<String, Object> countMap = meteringlpDao.getMissingCountByDay(params);

            resultMap.put("no", Integer.toString(i++));
            resultMap.put("customerName", (String) objs[0]);

            if (!"".equals(deviceType)) {// deviceType의 값이 null일 경우 체크
                if (CommonConstants.DeviceType.Modem.getCode().equals(Integer.parseInt(deviceType))) {
                    resultMap.put("deviceNo", (String) objs[1]);
                } else {
                    resultMap.put("deviceNo", (String) objs[2]);
                }
            }
            resultMap.put("mdsId", (String) objs[3]);
            resultMap.put("missingCnt", countMap.get("totalCount"));
            resultMap.put("lastReadDate", (String) objs[4]);
            resultMap.put("meterId", params.get("meterId"));
            resultMap.put("lpInterval", params.get("lpInterval"));
            resultList.add(resultMap);
        }

        return resultList;
    } // method End

    /**
     * 검침데이터(LP) 에 누락된 미터목록을 조회한다.
     * @param params
     * @return
     */
    @Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
    public List<Object> getMissingMeters2(Map<String,Object> params) {
        String searchStartDate  = (String)params.get("searchStartDate");
        String searchEndDate    = (String)params.get("searchEndDate");
        String meterType        = StringUtil.nullToBlank(params.get("meterType"));
        String supplierId       = StringUtil.nullToBlank(params.get("supplierId"));
        Integer channel         = (Integer)params.get("channel");
        String mdsId            = StringUtil.nullToBlank(params.get("mdsId"));
        String deviceType       = StringUtil.nullToBlank((String)params.get("deviceType"));
        String deviceId         = StringUtil.nullToBlank((String)params.get("deviceId"));

        String today            = TimeUtil.getCurrentTimeMilli(); //yyyyMMddHHmmss
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
        sbQueryAllPeriod.append("\nSELECT customer.name ");
        sbQueryAllPeriod.append("\n       ,modem.device_serial ");
        sbQueryAllPeriod.append("\n       ,mcu.sys_name AS mcuName ");
        sbQueryAllPeriod.append("\n       ,m1.mds_id ");
        sbQueryAllPeriod.append("\n       ,m1.last_read_date ");
        sbQueryAllPeriod.append("\n       ,m1.id ");
        sbQueryAllPeriod.append("\n       ,m1.lp_interval ");
        sbQueryAllPeriod.append("\nFROM meter m1 LEFT OUTER JOIN contract contract ON m1.id = contract.meter_id ");
        sbQueryAllPeriod.append("\n              LEFT OUTER JOIN customer customer ON contract.customer_id = customer.id ");
        sbQueryAllPeriod.append("\n              LEFT OUTER JOIN modem modem ON m1.modem_id = modem.id ");
        sbQueryAllPeriod.append("\n              LEFT OUTER JOIN mcu mcu ON modem.mcu_id = mcu.id ");
        sbQueryAllPeriod.append("\nWHERE 1=1 ");
        sbQueryAllPeriod.append("\nAND m1.install_date <= :installDate ");
        sbQueryAllPeriod.append("\nAND m1.meter = :meterType ");
        if (!"".equals(supplierId)) {
            sbQueryAllPeriod.append("\nAND m1.supplier_id = :supplierId ");
        }
        if (!"".equals(mdsId)) {
            sbQueryAllPeriod.append("\nAND m1.mds_id LIKE :mdsId ");
        }
        //deviceId , deviceType 의  null 값 체크
        if (!"".equals(deviceId) && !"".equals(deviceType)) {
            if (CommonConstants.DeviceType.MCU.getCode().equals(Integer.parseInt(deviceType))) {
                sbQueryAllPeriod.append("\nAND mcu.sys_name LIKE :deviceId ");
            } else {
                sbQueryAllPeriod.append("\nAND modem.device_serial LIKE :deviceId ");
            }
        }
        sbQueryAllPeriod.append("\nAND m1.id NOT IN ( ");
        sbQueryAllPeriod.append("\n        SELECT x.id ");
        sbQueryAllPeriod.append("\n        FROM( ");
        sbQueryAllPeriod.append("\n            SELECT m.id,lp.yyyymmdd ");
//        sbQueryAllPeriod.append("\n             FROM METER m LEFT OUTER JOIN ").append(lpTable).append(" lp ON lp.METER_ID = m.ID ");
//        sbQueryAllPeriod.append("\n            FROM meter m LEFT OUTER JOIN ").append(lpTable).append(" lp ON lp.mdev_id = m.mds_id ");
//        sbQueryAllPeriod.append("\n            AND lp.yyyymmdd BETWEEN :searchStartDate AND :searchEndDate ");
//        sbQueryAllPeriod.append("\n            AND lp.channel = :channel ");
        sbQueryAllPeriod.append("\n            FROM meter m ");
        sbQueryAllPeriod.append("\n                 LEFT OUTER JOIN ");
        sbQueryAllPeriod.append("\n                 ").append(lpTable).append(" lp ");
        sbQueryAllPeriod.append("\n                 ON  lp.mdev_id = m.mds_id ");
//        sbQueryAllPeriod.append("\n                 AND lp.yyyymmdd BETWEEN :searchStartDate AND :searchEndDate ");
        sbQueryAllPeriod.append("\n                 AND lp.yyyymmddhh BETWEEN :startDate AND :endDate ");
        sbQueryAllPeriod.append("\n                 AND lp.channel = :channel ");
        sbQueryAllPeriod.append("\n            WHERE 1=1 ");
        sbQueryAllPeriod.append("\n            AND m.install_date <= :installDate ");
        sbQueryAllPeriod.append("\n            AND 1 = CASE WHEN m.lp_interval = 1 THEN CASE WHEN lp.value_cnt < 60   THEN 0 ELSE 1 END ");
        sbQueryAllPeriod.append("\n                         WHEN m.lp_interval = 5 THEN CASE WHEN lp.value_cnt < 12   THEN 0 ELSE 1 END ");
        sbQueryAllPeriod.append("\n                         WHEN m.lp_interval = 10 THEN CASE WHEN lp.value_cnt < 6   THEN 0 ELSE 1 END ");
        sbQueryAllPeriod.append("\n                         WHEN m.lp_interval = 15 THEN CASE WHEN lp.value_cnt < 4   THEN 0 ELSE 1 END ");
        sbQueryAllPeriod.append("\n                         WHEN m.lp_interval = 30 THEN CASE WHEN lp.value_cnt < 2   THEN 0 ELSE 1 END ");
        sbQueryAllPeriod.append("\n                         WHEN m.lp_interval = 60 THEN CASE WHEN lp.value_cnt < 1   THEN 0 ELSE 1 END ");
        sbQueryAllPeriod.append("\n                    ELSE 0 end ");
        sbQueryAllPeriod.append("\n            GROUP BY m.id, lp.yyyymmdd ");
        sbQueryAllPeriod.append("\n            HAVING COUNT(m.id) = 24 ");
        sbQueryAllPeriod.append("\n        )x ");
        sbQueryAllPeriod.append("\n        WHERE x.yyyymmdd IS NOT NULL ");
        sbQueryAllPeriod.append("\n        GROUP BY x.id ");
        sbQueryAllPeriod.append("\n        HAVING COUNT(x.id) = :period ");
        sbQueryAllPeriod.append("\n) ");

        StringBuffer sbQueryAllPeriodWithToday = new StringBuffer();
        sbQueryAllPeriodWithToday.append("\nSELECT customer.name ");
        sbQueryAllPeriodWithToday.append("\n       ,modem.device_serial ");
        sbQueryAllPeriodWithToday.append("\n       ,mcu.sys_name AS mcuname ");
        sbQueryAllPeriodWithToday.append("\n       ,m1.mds_id ");
        sbQueryAllPeriodWithToday.append("\n       ,m1.last_read_date ");
        sbQueryAllPeriodWithToday.append("\n       ,m1.id ");
        sbQueryAllPeriodWithToday.append("\n       ,m1.lp_interval ");
        sbQueryAllPeriodWithToday.append("\nFROM meter m1 LEFT OUTER JOIN contract contract ON m1.id = contract.meter_id ");
        sbQueryAllPeriodWithToday.append("\n              LEFT OUTER JOIN customer customer ON contract.customer_id = customer.id ");
        sbQueryAllPeriodWithToday.append("\n              LEFT OUTER JOIN modem modem ON m1.modem_id = modem.id ");
        sbQueryAllPeriodWithToday.append("\n              LEFT OUTER JOIN mcu mcu ON modem.mcu_id = mcu.id ");
        sbQueryAllPeriodWithToday.append("\nWHERE 1=1 ");
        sbQueryAllPeriodWithToday.append("\nAND   m1.install_date <= :installDate ");
        sbQueryAllPeriodWithToday.append("\nAND   m1.meter = :meterType ");


        if (!"".equals(supplierId)) {
            sbQueryAllPeriodWithToday.append("\nAND   m1.supplier_id = :supplierId ");
        }
        if (!"".equals(mdsId)) {
            sbQueryAllPeriodWithToday.append("\nAND   m1.mds_id LIKE :mdsId ");
        }
        //deviceId , deviceType 의  null 값 체크
        if (!"".equals(deviceId) && !"".equals(deviceType)) {
            if (CommonConstants.DeviceType.MCU.getCode().equals(Integer.parseInt(deviceType))) {
                sbQueryAllPeriodWithToday.append("\nAND   mcu.sys_name LIKE :deviceId ");
            } else {
                sbQueryAllPeriodWithToday.append("\nAND   modem.device_serial LIKE :deviceId ");
            }
        }

        sbQueryAllPeriodWithToday.append("\nAND   m1.id NOT IN ( SELECT i3.id ");
        sbQueryAllPeriodWithToday.append("\n    FROM (SELECT i1.id FROM (SELECT x.id ");
        sbQueryAllPeriodWithToday.append("\n    FROM ( ");
        sbQueryAllPeriodWithToday.append("\n        SELECT m.id,lp.yyyymmdd ");
//        sbQueryAllPeriodWithToday.append("\n            FROM METER m LEFT OUTER JOIN ").append(lpTable).append(" lp ON lp.METER_ID = m.ID ");
        sbQueryAllPeriodWithToday.append("\n        FROM meter m LEFT OUTER JOIN ").append(lpTable).append(" lp ON lp.mdev_id = m.mds_id ");
//        sbQueryAllPeriodWithToday.append("\n        AND lp.yyyymmdd BETWEEN :searchStartDate AND :searchEndDate ");
        sbQueryAllPeriodWithToday.append("\n        AND lp.yyyymmddhh BETWEEN :startDate AND :endDate ");
        sbQueryAllPeriodWithToday.append("\n        AND lp.channel = :channel ");
        sbQueryAllPeriodWithToday.append("\n        WHERE 1=1 ");
        sbQueryAllPeriodWithToday.append("\n        AND m.install_date <= :installDate ");
        sbQueryAllPeriodWithToday.append("\n        AND 1 = CASE WHEN m.lp_interval = 1 THEN CASE WHEN lp.value_cnt < 60   THEN 0 ELSE 1 END ");
        sbQueryAllPeriodWithToday.append("\n                     WHEN m.lp_interval = 5 THEN CASE WHEN lp.value_cnt < 12   THEN 0 ELSE 1 END ");
        sbQueryAllPeriodWithToday.append("\n                     WHEN m.lp_interval = 10 THEN CASE WHEN lp.value_cnt < 6   THEN 0 ELSE 1 END ");
        sbQueryAllPeriodWithToday.append("\n                     WHEN m.lp_interval = 15 THEN CASE WHEN lp.value_cnt < 4   THEN 0 ELSE 1 END ");
        sbQueryAllPeriodWithToday.append("\n                     WHEN m.lp_interval = 30 THEN CASE WHEN lp.value_cnt < 2   THEN 0 ELSE 1 END ");
        sbQueryAllPeriodWithToday.append("\n                     WHEN m.lp_interval = 60 THEN CASE WHEN lp.value_cnt < 1   THEN 0 ELSE 1 END ");
        sbQueryAllPeriodWithToday.append("\n                ELSE 0 end ");
        sbQueryAllPeriodWithToday.append("\n        GROUP BY m.id,lp.yyyymmdd ");
        sbQueryAllPeriodWithToday.append("\n        HAVING COUNT(m.id) = 24 ");
        sbQueryAllPeriodWithToday.append("\n    )x ");
        sbQueryAllPeriodWithToday.append("\n    WHERE x.yyyymmdd IS NOT NULL ");
        sbQueryAllPeriodWithToday.append("\n    GROUP BY x.id ");
        sbQueryAllPeriodWithToday.append("\n    HAVING COUNT(x.id) = :period) i1 ");

        sbQueryAllPeriodWithToday.append("\n    INNER JOIN ");

        sbQueryAllPeriodWithToday.append("\n    (SELECT m.id ");
//        sbQueryAllPeriodWithToday.append("\n        FROM METER m LEFT OUTER JOIN ").append(lpTable).append(" lp ON lp.METER_ID = m.ID ");
        sbQueryAllPeriodWithToday.append("\n    FROM meter m LEFT OUTER JOIN ").append(lpTable).append(" lp ON lp.mdev_id = m.mds_id ");
//        sbQueryAllPeriodWithToday.append("\n    AND lp.yyyymmdd = :currDate ");
        sbQueryAllPeriodWithToday.append("\n    AND lp.yyyymmddhh BETWEEN :currStartDate AND :currEndDate ");
        sbQueryAllPeriodWithToday.append("\n    AND lp.hh < :currHour ");
        sbQueryAllPeriodWithToday.append("\n    AND lp.channel = :channel ");
        sbQueryAllPeriodWithToday.append("\n    WHERE 1=1 ");
        sbQueryAllPeriodWithToday.append("\n    AND m.install_date <= :installDate ");
        sbQueryAllPeriodWithToday.append("\n    AND 1 = CASE WHEN m.lp_interval = 1 THEN CASE WHEN lp.value_cnt < 60   THEN 0 ELSE 1 END ");
        sbQueryAllPeriodWithToday.append("\n                 WHEN m.lp_interval = 5 THEN CASE WHEN lp.value_cnt < 12   THEN 0 ELSE 1 END ");
        sbQueryAllPeriodWithToday.append("\n                 WHEN m.lp_interval = 10 THEN CASE WHEN lp.value_cnt < 6   THEN 0 ELSE 1 END ");
        sbQueryAllPeriodWithToday.append("\n                 WHEN m.lp_interval = 15 THEN CASE WHEN lp.value_cnt < 4   THEN 0 ELSE 1 END ");
        sbQueryAllPeriodWithToday.append("\n                 WHEN m.lp_interval = 30 THEN CASE WHEN lp.value_cnt < 2   THEN 0 ELSE 1 END ");
        sbQueryAllPeriodWithToday.append("\n                 WHEN m.lp_interval = 60 THEN CASE WHEN lp.value_cnt < 1   THEN 0 ELSE 1 END ");
        sbQueryAllPeriodWithToday.append("\n            ELSE 0 end ");
        sbQueryAllPeriodWithToday.append("\n    GROUP BY m.id, lp.yyyymmdd ");
        sbQueryAllPeriodWithToday.append("\n    HAVING COUNT(m.id) = :currHour) i2 ON i1.id=i2.id) i3 ");

        sbQueryAllPeriodWithToday.append("\n    INNER JOIN ");

        sbQueryAllPeriodWithToday.append("\n    (SELECT m.id ");
//        sbQueryAllPeriodWithToday.append("\n            FROM METER m LEFT OUTER JOIN ").append(lpTable).append(" lp ON lp.METER_ID = m.ID ");
        sbQueryAllPeriodWithToday.append("\n    FROM meter m LEFT OUTER JOIN ").append(lpTable).append(" lp ON lp.mdev_id = m.mds_id ");
//        sbQueryAllPeriodWithToday.append("\n    AND lp.yyyymmdd = :currDate ");
        sbQueryAllPeriodWithToday.append("\n    AND lp.yyyymmddhh BETWEEN :currStartDate AND :currEndDate ");
        sbQueryAllPeriodWithToday.append("\n    AND lp.hh = :currHour ");
        sbQueryAllPeriodWithToday.append("\n    AND lp.channel = :channel ");
        sbQueryAllPeriodWithToday.append("\n    WHERE 1=1 ");
        sbQueryAllPeriodWithToday.append("\n    AND m.install_date <= :installDate ");
        if (currMinute < 1) {
        	sbQueryAllPeriodWithToday.append("\n        AND 1 = CASE WHEN m.lp_interval = 1  THEN CASE WHEN 1 <= lp.value_cnt THEN 1 ELSE 0 END ");
        	sbQueryAllPeriodWithToday.append("\n                     WHEN m.lp_interval = 5  THEN CASE WHEN 1 <= lp.value_cnt THEN 1 ELSE 0 END ");
        	sbQueryAllPeriodWithToday.append("\n                     WHEN m.lp_interval = 10 THEN CASE WHEN 1 <= lp.value_cnt THEN 1 ELSE 0 END ");
        	sbQueryAllPeriodWithToday.append("\n                     WHEN m.lp_interval = 15 THEN CASE WHEN 1 <= lp.value_cnt THEN 1 ELSE 0 END ");
        	sbQueryAllPeriodWithToday.append("\n                     WHEN m.lp_interval = 30 THEN CASE WHEN 1 <= lp.value_cnt THEN 1 ELSE 0 END ");
        	sbQueryAllPeriodWithToday.append("\n                     WHEN m.lp_interval = 60 THEN CASE WHEN 1 <= lp.value_cnt THEN 1 ELSE 0 END ");
        	sbQueryAllPeriodWithToday.append("\n                ELSE 0 END) i4 ON i3.id=i4.id ");
        } else {
        	sbQueryAllPeriodWithToday.append("\n        AND 1 = CASE WHEN m.lp_interval = 1  THEN CASE WHEN (:currMinute/1) < lp.value_cnt THEN 1 ELSE 0 END ");
        	sbQueryAllPeriodWithToday.append("\n                     WHEN m.lp_interval = 5  THEN CASE WHEN (:currMinute/5) < lp.value_cnt THEN 1 ELSE 0 END ");
        	sbQueryAllPeriodWithToday.append("\n                     WHEN m.lp_interval = 10 THEN CASE WHEN (:currMinute/10) < lp.value_cnt THEN 1 ELSE 0 END ");
        	sbQueryAllPeriodWithToday.append("\n                     WHEN m.lp_interval = 15 THEN CASE WHEN (:currMinute/15) < lp.value_cnt THEN 1 ELSE 0 END ");
        	sbQueryAllPeriodWithToday.append("\n                     WHEN m.lp_interval = 30 THEN CASE WHEN (:currMinute/30) < lp.value_cnt THEN 1 ELSE 0 END ");
        	sbQueryAllPeriodWithToday.append("\n                     WHEN m.lp_interval = 60 THEN CASE WHEN (:currMinute/60) < lp.value_cnt THEN 1 ELSE 0 END ");
        	sbQueryAllPeriodWithToday.append("\n                ELSE 0 END) i4 ON i3.id=i4.id");
        }

        
        sbQueryAllPeriodWithToday.append("\n) ");

        StringBuffer sbQueryToday = new StringBuffer();
        sbQueryToday.append("\nSELECT customer.name ");
        sbQueryToday.append("\n       ,modem.device_serial ");
        sbQueryToday.append("\n       ,mcu.sys_name as mcuName ");
        sbQueryToday.append("\n       ,m1.mds_id ");
        sbQueryToday.append("\n       ,m1.last_read_date ");
        sbQueryToday.append("\n       ,m1.id ");
        sbQueryToday.append("\n       ,m1.lp_interval ");
        sbQueryToday.append("\nFROM meter m1 LEFT OUTER JOIN contract contract ON m1.id = contract.meter_id ");
        sbQueryToday.append("\n              LEFT OUTER JOIN customer customer ON contract.customer_id = customer.id ");
        sbQueryToday.append("\n              LEFT OUTER JOIN modem modem ON m1.modem_id = modem.id ");
        sbQueryToday.append("\n              LEFT OUTER JOIN mcu mcu ON modem.mcu_id = mcu.id ");
        sbQueryToday.append("\nWHERE 1=1 ");
        sbQueryToday.append("\nAND   m1.install_date <= :installDate ");
        sbQueryToday.append("\nAND   m1.meter = :meterType ");
        if (!"".equals(supplierId)) {
            sbQueryToday.append("\nAND   m1.supplier_id = :supplierId ");
        }
        if (!"".equals(mdsId)) {
            sbQueryToday.append("\nAND   m1.mds_id LIKE :mdsId ");
        }
        //deviceId , deviceType 의  null 값 체크
        if (!"".equals(deviceId) && !"".equals(deviceType)) {
            if (CommonConstants.DeviceType.MCU.getCode().equals(Integer.parseInt(deviceType))) {
                sbQueryToday.append("\nAND   mcu.sys_name LIKE :deviceId ");
            } else {
                sbQueryToday.append("\nAND   modem.device_serial LIKE :deviceId ");
            }
        }
        sbQueryToday.append("\nAND   m1.id NOT IN ( ");
        sbQueryToday.append("\n        SELECT i1.id ");
        sbQueryToday.append("\n        FROM(SELECT m.id ");
//        sbQueryToday.append("\n         FROM METER m LEFT OUTER JOIN ").append(lpTable).append(" lp ON lp.METER_ID = m.ID ");
        sbQueryToday.append("\n        FROM meter m LEFT OUTER JOIN ").append(lpTable).append(" lp ON lp.mdev_id = m.mds_id ");
//        sbQueryToday.append("\n        AND lp.yyyymmdd = :currDate ");
        sbQueryToday.append("\n        AND lp.yyyymmddhh BETWEEN :currStartDate AND :currEndDate ");
        sbQueryToday.append("\n        AND lp.hh < :currHour ");
        sbQueryToday.append("\n        AND lp.channel = :channel ");
        sbQueryToday.append("\n        WHERE 1=1 ");
        sbQueryToday.append("\n        AND m.install_date <= :installDate ");
        sbQueryToday.append("\n        AND 1 = CASE WHEN m.lp_interval = 1 THEN CASE WHEN lp.value_cnt < 60   THEN 0 ELSE 1 END ");
        sbQueryToday.append("\n                     WHEN m.lp_interval = 5 THEN CASE WHEN lp.value_cnt < 12   THEN 0 ELSE 1 END ");
        sbQueryToday.append("\n                     WHEN m.lp_interval = 10 THEN CASE WHEN lp.value_cnt < 6   THEN 0 ELSE 1 END ");
        sbQueryToday.append("\n                     WHEN m.lp_interval = 15 THEN CASE WHEN lp.value_cnt < 4   THEN 0 ELSE 1 END ");
        sbQueryToday.append("\n                     WHEN m.lp_interval = 30 THEN CASE WHEN lp.value_cnt < 2   THEN 0 ELSE 1 END ");
        sbQueryToday.append("\n                     WHEN m.lp_interval = 60 THEN CASE WHEN lp.value_cnt < 1   THEN 0 ELSE 1 END ");
        sbQueryToday.append("\n                ELSE 0 end ");
        sbQueryToday.append("\n        GROUP BY m.id,lp.yyyymmdd ");
        sbQueryToday.append("\n        HAVING COUNT(m.id) = :currHour) i1 ");

        sbQueryToday.append("\n        INNER JOIN ");

        sbQueryToday.append("\n        (SELECT m.id ");
//        sbQueryToday.append("\n             FROM METER m LEFT OUTER JOIN ").append(lpTable).append(" lp ON lp.METER_ID = m.ID ");
        sbQueryToday.append("\n        FROM meter m LEFT OUTER JOIN ").append(lpTable).append(" lp ON lp.mdev_id = m.mds_id ");
//        sbQueryToday.append("\n        AND lp.yyyymmdd = :currDate ");
        sbQueryToday.append("\n        AND lp.yyyymmddhh BETWEEN :currStartDate AND :currEndDate ");
        sbQueryToday.append("\n        AND lp.hh = :currHour ");
        sbQueryToday.append("\n        AND lp.channel = :channel ");
        sbQueryToday.append("\n        WHERE 1=1 ");
        sbQueryToday.append("\n        AND m.install_date <= :installDate ");
        if (currMinute < 1) {
        	sbQueryToday.append("\n        AND 1 = CASE WHEN m.lp_interval = 1  THEN CASE WHEN 1 <= lp.value_cnt THEN 1 ELSE 0 END ");
        	sbQueryToday.append("\n                     WHEN m.lp_interval = 5  THEN CASE WHEN 1 <= lp.value_cnt THEN 1 ELSE 0 END ");
        	sbQueryToday.append("\n                     WHEN m.lp_interval = 10 THEN CASE WHEN 1 <= lp.value_cnt THEN 1 ELSE 0 END ");
        	sbQueryToday.append("\n                     WHEN m.lp_interval = 15 THEN CASE WHEN 1 <= lp.value_cnt THEN 1 ELSE 0 END ");
        	sbQueryToday.append("\n                     WHEN m.lp_interval = 30 THEN CASE WHEN 1 <= lp.value_cnt THEN 1 ELSE 0 END ");
        	sbQueryToday.append("\n                     WHEN m.lp_interval = 60 THEN CASE WHEN 1 <= lp.value_cnt THEN 1 ELSE 0 END ");
        	sbQueryToday.append("\n                ELSE 0 END) i2 ON i1.id=i2.id  ");
        } else {
        	sbQueryToday.append("\n        AND 1 = CASE WHEN m.lp_interval = 1  THEN CASE WHEN (:currMinute/1) < lp.value_cnt THEN 1 ELSE 0 END ");
        	sbQueryToday.append("\n                     WHEN m.lp_interval = 5  THEN CASE WHEN (:currMinute/5) < lp.value_cnt THEN 1 ELSE 0 END ");
        	sbQueryToday.append("\n                     WHEN m.lp_interval = 10 THEN CASE WHEN (:currMinute/10) < lp.value_cnt THEN 1 ELSE 0 END ");
        	sbQueryToday.append("\n                     WHEN m.lp_interval = 15 THEN CASE WHEN (:currMinute/15) < lp.value_cnt THEN 1 ELSE 0 END ");
        	sbQueryToday.append("\n                     WHEN m.lp_interval = 30 THEN CASE WHEN (:currMinute/30) < lp.value_cnt THEN 1 ELSE 0 END ");
        	sbQueryToday.append("\n                     WHEN m.lp_interval = 60 THEN CASE WHEN (:currMinute/60) < lp.value_cnt THEN 1 ELSE 0 END ");
        	sbQueryToday.append("\n                ELSE 0 END) i2 ON i1.id=i2.id ");
        }

        sbQueryToday.append("\n) ");

        Query query = null;
        if (Integer.parseInt(searchEndDate) < Integer.parseInt(currDate)) {
            // 조회종료일이 현재일자보다 이전일 경우 전체일자 조회
            query = getSession().createSQLQuery(sbQueryAllPeriod.toString());
//            query.setString("searchStartDate", searchStartDate);
//            query.setString("searchEndDate", searchEndDate);
            query.setString("startDate", searchStartDate + "00");
            query.setString("endDate", searchEndDate + "23");
            query.setString("installDate", searchStartDate + CommonConstants.DefaultDate.LAST_HHMMSS.getValue());
            query.setString("meterType", meterType);
            query.setInteger("channel", channel);
            query.setInteger("period", period);
            if (!"".equals(supplierId)) {
                query.setInteger("supplierId", Integer.parseInt(supplierId));
            }
            if (!"".equals(mdsId)) {
                query.setString("mdsId", "%"+ mdsId+"%");
            }
            if (!"".equals(deviceId)) {
                query.setString("deviceId", "%"+ deviceId+"%");
            }

        } else {
            if (Integer.parseInt(searchStartDate) < Integer.parseInt(currDate)) {
                // 조회종료일이 현재일자이고 조회시작일이 현재일자 이전일경우 시작일~종료일전일,현재일자,현재시간 별로 조회
                query = getSession().createSQLQuery(sbQueryAllPeriodWithToday.toString());
//                query.setString("searchStartDate", searchStartDate);
//                query.setString("searchEndDate", CalendarUtil.getDateWithoutFormat(searchEndDate,Calendar.DATE, -1));
                query.setString("startDate", searchStartDate + "00");
                query.setString("endDate", CalendarUtil.getDateWithoutFormat(searchEndDate,Calendar.DATE, -1) + "23");
                query.setString("installDate", searchStartDate + CommonConstants.DefaultDate.LAST_HHMMSS.getValue());
                query.setString("meterType", meterType);
//                query.setString("currDate", currDate);
                query.setString("currStartDate", currDate + "00");
                query.setString("currEndDate", currDate + "23");
                query.setString("currHour", currHour);
                if (currMinute >= 1) {
                    query.setInteger("currMinute", currMinute);
                }
                query.setInteger("channel", channel);
                query.setInteger("period", period);
                if (!"".equals(supplierId)) {
                    query.setInteger("supplierId", Integer.parseInt(supplierId));
                }
                if (!"".equals(mdsId)) {
                    query.setString("mdsId", "%"+ mdsId+"%");
                }
                if (!"".equals(deviceId)) {
                    query.setString("deviceId", "%"+ deviceId+"%");
                }
            } else {
                // 조회일자가 현재일자 하루일경우 현재날짜,현재시간만 조회
                query = getSession().createSQLQuery(sbQueryToday.toString());
                query.setString("installDate", searchStartDate + CommonConstants.DefaultDate.LAST_HHMMSS.getValue());
                query.setString("meterType", meterType);
//                query.setString("currDate", currDate);
                query.setString("currStartDate", currDate + "00");
                query.setString("currEndDate", currDate + "23");
                query.setString("currHour", currHour);
                if (currMinute >= 1) {
                    query.setInteger("currMinute", currMinute);
                }
                query.setInteger("channel", channel);
                if (!"".equals(supplierId)) {
                    query.setInteger("supplierId", Integer.parseInt(supplierId));
                }
                if (!"".equals(mdsId)) {
                    query.setString("mdsId", "%"+ mdsId+"%");
                }
                if (!"".equals(deviceId)) {
                    query.setString("deviceId", "%"+ deviceId+"%");
                }
            }
        }

        //@desc:쿼리에 페이징 처리
        query= CommonUtils2.addPagingForQuery2(query, params);

        // query 결과목록
        List<Object> result = query.list();

        List<Object> resultList = new ArrayList<Object>();

        int i=1;
        for (Object obj : result) {
            HashMap<String,Object> resultMap = new HashMap<String,Object>();
            Object[] objs = (Object[])obj;

            params.put("meterId", ((Number)objs[5]).intValue());
            params.put("mdsId", (String)objs[3]);

            if (objs[6] == null) {
                params.put("lpInterval",60); //lpInterval 값이 null일 경우 기본값(60) 입력
            }
            else {
                params.put("lpInterval",((Number)objs[6]).intValue());
            }

            Map<String,Object> countMap = meteringlpDao.getMissingCountByDay(params);

            resultMap.put("no",Integer.toString(i++));
            resultMap.put("customerName",(String)objs[0]);

            if (!"".equals(deviceType)) {//deviceType의 값이 null일 경우 체크
                    if (CommonConstants.DeviceType.Modem.getCode().equals(Integer.parseInt(deviceType))) {
                        resultMap.put("deviceNo",(String)objs[1]);
                    } else {
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
    } //method End

    @Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
    public String getMissingMetersTotalCnt(Map<String,Object> params)
    {
        String searchStartDate  = (String)params.get("searchStartDate");
        String searchEndDate    = (String)params.get("searchEndDate");
        String meterType        = StringUtil.nullToBlank(params.get("meterType"));
        String supplierId       = StringUtil.nullToBlank(params.get("supplierId"));
        Integer channel         = (Integer)params.get("channel");
        String mdsId            = StringUtil.nullToBlank(params.get("mdsId"));
        String deviceType       = StringUtil.nullToBlank((String)params.get("deviceType"));
        String deviceId         = StringUtil.nullToBlank((String)params.get("deviceId"));

        String today            = TimeUtil.getCurrentTimeMilli(); //yyyyMMddHHmmss
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

        // count(*)
        StringBuffer sbQueryAllPeriod = new StringBuffer();


        //SELECT count(*)
        sbQueryAllPeriod.append("\n  SELECT count(*) ");
        sbQueryAllPeriod.append("\nFROM meter m1 LEFT OUTER JOIN contract contract ON m1.id = contract.meter_id ");
        sbQueryAllPeriod.append("\n              LEFT OUTER JOIN customer customer ON contract.customer_id = customer.id ");
        sbQueryAllPeriod.append("\n              LEFT OUTER JOIN modem modem ON m1.modem_id = modem.id ");
        sbQueryAllPeriod.append("\n              LEFT OUTER JOIN mcu mcu ON modem.mcu_id = mcu.id ");
        sbQueryAllPeriod.append("\nWHERE 1=1 ");
        sbQueryAllPeriod.append("\nAND m1.install_date <= :installDate ");
        sbQueryAllPeriod.append("\nAND m1.meter = :meterType ");
        if (!"".equals(supplierId)) {
            sbQueryAllPeriod.append("\nAND m1.supplier_id = :supplierId ");
        }
        if (!"".equals(mdsId)) {
            sbQueryAllPeriod.append("\nAND m1.mds_id LIKE :mdsId ");
        }
        //deviceId , deviceType 의  null 값 체크
        if (!"".equals(deviceId) && !"".equals(deviceType)) {
            if (CommonConstants.DeviceType.MCU.getCode().equals(Integer.parseInt(deviceType))) {
                sbQueryAllPeriod.append("\nAND mcu.sys_name LIKE :deviceId ");
            } else {
                sbQueryAllPeriod.append("\nAND modem.device_serial LIKE :deviceId ");
            }
        }
        sbQueryAllPeriod.append("\nAND m1.id NOT IN ( ");
        //
        sbQueryAllPeriod.append("\n        SELECT x.id ");
        sbQueryAllPeriod.append("\n        FROM( ");
        sbQueryAllPeriod.append("\n            SELECT m.id,lp.yyyymmdd ");
//        sbQueryAllPeriod.append("\n             FROM METER m LEFT OUTER JOIN ").append(lpTable).append(" lp ON lp.METER_ID = m.ID ");
//        sbQueryAllPeriod.append("\n            FROM meter m LEFT OUTER JOIN ").append(lpTable).append(" lp ON lp.mdev_id = m.mds_id ");
//        sbQueryAllPeriod.append("\n            AND lp.yyyymmdd BETWEEN :searchStartDate AND :searchEndDate ");
//        sbQueryAllPeriod.append("\n            AND lp.channel = :channel ");
        sbQueryAllPeriod.append("\n            FROM meter m ");
        sbQueryAllPeriod.append("\n                 LEFT OUTER JOIN ");
        sbQueryAllPeriod.append("\n                 ").append(lpTable).append(" lp ");
        sbQueryAllPeriod.append("\n                 ON  lp.mdev_id = m.mds_id ");
//        sbQueryAllPeriod.append("\n                 AND lp.yyyymmdd BETWEEN :searchStartDate AND :searchEndDate ");
        sbQueryAllPeriod.append("\n                 AND lp.yyyymmddhh BETWEEN :startDate AND :endDate ");
        sbQueryAllPeriod.append("\n                 AND lp.channel = :channel ");
        sbQueryAllPeriod.append("\n            WHERE 1=1 ");
        sbQueryAllPeriod.append("\n            AND m.install_date <= :installDate ");
        sbQueryAllPeriod.append("\n            AND 1 = CASE WHEN m.lp_interval = 1 THEN CASE WHEN lp.value_cnt < 60   THEN 0 ELSE 1 END ");
        sbQueryAllPeriod.append("\n                         WHEN m.lp_interval = 5 THEN CASE WHEN lp.value_cnt < 12   THEN 0 ELSE 1 END ");
        sbQueryAllPeriod.append("\n                         WHEN m.lp_interval = 10 THEN CASE WHEN lp.value_cnt < 6   THEN 0 ELSE 1 END ");
        sbQueryAllPeriod.append("\n                         WHEN m.lp_interval = 15 THEN CASE WHEN lp.value_cnt < 4   THEN 0 ELSE 1 END ");
        sbQueryAllPeriod.append("\n                         WHEN m.lp_interval = 30 THEN CASE WHEN lp.value_cnt < 2   THEN 0 ELSE 1 END ");
        sbQueryAllPeriod.append("\n                         WHEN m.lp_interval = 60 THEN CASE WHEN lp.value_cnt < 1   THEN 0 ELSE 1 END ");
        sbQueryAllPeriod.append("\n                    ELSE 0 end ");
        sbQueryAllPeriod.append("\n            GROUP BY m.id, lp.yyyymmdd ");
        sbQueryAllPeriod.append("\n            HAVING COUNT(m.id) = 24 ");
        sbQueryAllPeriod.append("\n        )x ");
        sbQueryAllPeriod.append("\n        WHERE x.yyyymmdd IS NOT NULL ");
        sbQueryAllPeriod.append("\n        GROUP BY x.id ");
        sbQueryAllPeriod.append("\n        HAVING COUNT(x.id) = :period ");
        sbQueryAllPeriod.append("\n) ");

        StringBuffer sbQueryAllPeriodWithToday = new StringBuffer();
        sbQueryAllPeriodWithToday.append("\nSELECT count(*) ");
        sbQueryAllPeriodWithToday.append("\nFROM meter m1 LEFT OUTER JOIN contract contract ON m1.id = contract.meter_id ");
        sbQueryAllPeriodWithToday.append("\n              LEFT OUTER JOIN customer customer ON contract.customer_id = customer.id ");
        sbQueryAllPeriodWithToday.append("\n              LEFT OUTER JOIN modem modem ON m1.modem_id = modem.id ");
        sbQueryAllPeriodWithToday.append("\n              LEFT OUTER JOIN mcu mcu ON modem.mcu_id = mcu.id ");
        sbQueryAllPeriodWithToday.append("\nWHERE 1=1 ");
        sbQueryAllPeriodWithToday.append("\nAND   m1.install_date <= :installDate ");
        sbQueryAllPeriodWithToday.append("\nAND   m1.meter = :meterType ");


        if (!"".equals(supplierId)) {
            sbQueryAllPeriodWithToday.append("\nAND   m1.supplier_id = :supplierId ");
        }
        if (!"".equals(mdsId)) {
            sbQueryAllPeriodWithToday.append("\nAND   m1.mds_id LIKE :mdsId ");
        }
        //deviceId , deviceType 의  null 값 체크
        if (!"".equals(deviceId) && !"".equals(deviceType)) {
            if (CommonConstants.DeviceType.MCU.getCode().equals(Integer.parseInt(deviceType))) {
                sbQueryAllPeriodWithToday.append("\nAND   mcu.sys_name LIKE :deviceId ");
            } else {
                sbQueryAllPeriodWithToday.append("\nAND   modem.device_serial LIKE :deviceId ");
            }
        }

        sbQueryAllPeriodWithToday.append("\nAND   m1.id NOT IN ( SELECT i3.id ");
        sbQueryAllPeriodWithToday.append("\n    FROM (SELECT x.id ");
        sbQueryAllPeriodWithToday.append("\n    FROM ( ");
        sbQueryAllPeriodWithToday.append("\n        SELECT m.id,lp.yyyymmdd ");
//        sbQueryAllPeriodWithToday.append("\n            FROM METER m LEFT OUTER JOIN ").append(lpTable).append(" lp ON lp.METER_ID = m.ID ");
        sbQueryAllPeriodWithToday.append("\n        FROM meter m LEFT OUTER JOIN ").append(lpTable).append(" lp ON lp.mdev_id = m.mds_id ");
//        sbQueryAllPeriodWithToday.append("\n        AND lp.yyyymmdd BETWEEN :searchStartDate AND :searchEndDate ");
        sbQueryAllPeriodWithToday.append("\n        AND lp.yyyymmddhh BETWEEN :startDate AND :endDate ");
        sbQueryAllPeriodWithToday.append("\n        AND lp.channel = :channel ");
        sbQueryAllPeriodWithToday.append("\n        WHERE 1=1 ");
        sbQueryAllPeriodWithToday.append("\n        AND m.install_date <= :installDate ");
        sbQueryAllPeriodWithToday.append("\n        AND 1 = CASE WHEN m.lp_interval = 1 THEN CASE WHEN lp.value_cnt < 60   THEN 0 ELSE 1 END ");
        sbQueryAllPeriodWithToday.append("\n                     WHEN m.lp_interval = 5 THEN CASE WHEN lp.value_cnt < 12   THEN 0 ELSE 1 END ");
        sbQueryAllPeriodWithToday.append("\n                     WHEN m.lp_interval = 10 THEN CASE WHEN lp.value_cnt < 6   THEN 0 ELSE 1 END ");
        sbQueryAllPeriodWithToday.append("\n                     WHEN m.lp_interval = 15 THEN CASE WHEN lp.value_cnt < 4   THEN 0 ELSE 1 END ");
        sbQueryAllPeriodWithToday.append("\n                     WHEN m.lp_interval = 30 THEN CASE WHEN lp.value_cnt < 2   THEN 0 ELSE 1 END ");
        sbQueryAllPeriodWithToday.append("\n                     WHEN m.lp_interval = 60 THEN CASE WHEN lp.value_cnt < 1   THEN 0 ELSE 1 END ");
        sbQueryAllPeriodWithToday.append("\n                ELSE 0 end ");
        sbQueryAllPeriodWithToday.append("\n        GROUP BY m.id,lp.yyyymmdd ");
        sbQueryAllPeriodWithToday.append("\n        HAVING COUNT(m.id) = 24 ");
        sbQueryAllPeriodWithToday.append("\n    )x ");
        sbQueryAllPeriodWithToday.append("\n    WHERE x.yyyymmdd IS NOT NULL ");
        sbQueryAllPeriodWithToday.append("\n    GROUP BY x.id ");
        sbQueryAllPeriodWithToday.append("\n    HAVING COUNT(x.id) = :period ) i1 ");

        sbQueryAllPeriodWithToday.append("\n    INNER JOIN ");

        sbQueryAllPeriodWithToday.append("\n    (SELECT m.id ");
//        sbQueryAllPeriodWithToday.append("\n        FROM METER m LEFT OUTER JOIN ").append(lpTable).append(" lp ON lp.METER_ID = m.ID ");
        sbQueryAllPeriodWithToday.append("\n    FROM meter m LEFT OUTER JOIN ").append(lpTable).append(" lp ON lp.mdev_id = m.mds_id ");
//        sbQueryAllPeriodWithToday.append("\n    AND lp.yyyymmdd = :currDate ");
        sbQueryAllPeriodWithToday.append("\n    AND lp.yyyymmddhh BETWEEN :currStartDate AND :currEndDate ");
        sbQueryAllPeriodWithToday.append("\n    AND lp.hh < :currHour ");
        sbQueryAllPeriodWithToday.append("\n    AND lp.channel = :channel ");
        sbQueryAllPeriodWithToday.append("\n    WHERE 1=1 ");
        sbQueryAllPeriodWithToday.append("\n    AND m.install_date <= :installDate ");
        sbQueryAllPeriodWithToday.append("\n    AND 1 = CASE WHEN m.lp_interval = 1 THEN CASE WHEN lp.value_cnt < 60   THEN 0 ELSE 1 END ");
        sbQueryAllPeriodWithToday.append("\n                 WHEN m.lp_interval = 5 THEN CASE WHEN lp.value_cnt < 12   THEN 0 ELSE 1 END ");
        sbQueryAllPeriodWithToday.append("\n                 WHEN m.lp_interval = 10 THEN CASE WHEN lp.value_cnt < 6   THEN 0 ELSE 1 END ");
        sbQueryAllPeriodWithToday.append("\n                 WHEN m.lp_interval = 15 THEN CASE WHEN lp.value_cnt < 4   THEN 0 ELSE 1 END ");
        sbQueryAllPeriodWithToday.append("\n                 WHEN m.lp_interval = 30 THEN CASE WHEN lp.value_cnt < 2   THEN 0 ELSE 1 END ");
        sbQueryAllPeriodWithToday.append("\n                 WHEN m.lp_interval = 60 THEN CASE WHEN lp.value_cnt < 1   THEN 0 ELSE 1 END ");
        sbQueryAllPeriodWithToday.append("\n            ELSE 0 end ");
        sbQueryAllPeriodWithToday.append("\n    GROUP BY m.id, lp.yyyymmdd ");
        sbQueryAllPeriodWithToday.append("\n    HAVING COUNT(m.id) = :currHour) i2 ON i1.id=i2.id) i3 ");

        sbQueryAllPeriodWithToday.append("\n    INNER JOIN ");

        sbQueryAllPeriodWithToday.append("\n    (SELECT m.id ");
//        sbQueryAllPeriodWithToday.append("\n            FROM METER m LEFT OUTER JOIN ").append(lpTable).append(" lp ON lp.METER_ID = m.ID ");
        sbQueryAllPeriodWithToday.append("\n    FROM meter m LEFT OUTER JOIN ").append(lpTable).append(" lp ON lp.mdev_id = m.mds_id ");
//        sbQueryAllPeriodWithToday.append("\n    AND lp.yyyymmdd = :currDate ");
        sbQueryAllPeriodWithToday.append("\n    AND lp.yyyymmdd = :currDate ");
        sbQueryAllPeriodWithToday.append("\n    AND lp.yyyymmddhh BETWEEN :currStartDate AND :currEndDate ");
        sbQueryAllPeriodWithToday.append("\n    AND lp.hh = :currHour ");
        sbQueryAllPeriodWithToday.append("\n    AND lp.channel = :channel ");
        sbQueryAllPeriodWithToday.append("\n    WHERE 1=1 ");
        sbQueryAllPeriodWithToday.append("\n    AND m.install_date <= :installDate ");

        if (currMinute < 1) {
            sbQueryAllPeriodWithToday.append("\n    AND 1 = CASE WHEN m.lp_interval = 1 THEN CASE WHEN 1 <= lp.value_cnt THEN 0 ELSE 1 END ");
            sbQueryAllPeriodWithToday.append("\n                 WHEN m.lp_interval = 5 THEN CASE WHEN 1 <= lp.value_cnt  THEN 0 ELSE 1 END ");
            sbQueryAllPeriodWithToday.append("\n                 WHEN m.lp_interval = 10 THEN CASE WHEN 1 <= lp.value_cnt THEN 0 ELSE 1 END ");
            sbQueryAllPeriodWithToday.append("\n                 WHEN m.lp_interval = 15 THEN CASE WHEN 1 <= lp.value_cnt THEN 0 ELSE 1 END ");
            sbQueryAllPeriodWithToday.append("\n                 WHEN m.lp_interval = 30 THEN CASE WHEN 1 <= lp.value_cnt THEN 0 ELSE 1 END ");
            sbQueryAllPeriodWithToday.append("\n                 WHEN m.lp_interval = 60 THEN CASE WHEN 1 <= lp.value_cnt THEN 0 ELSE 1 END ");
            sbQueryAllPeriodWithToday.append("\n    ELSE 0 END) i4 ON i3.id=i4.id ");
        } else {
            sbQueryAllPeriodWithToday.append("\n    AND 1 = CASE WHEN m.lp_interval = 1 THEN CASE WHEN (:currMinute/1) < lp.value_cnt THEN 0 ELSE 1 END ");
            sbQueryAllPeriodWithToday.append("\n                 WHEN m.lp_interval = 5 THEN CASE WHEN (:currMinute/5) < lp.value_cnt THEN 0 ELSE 1 END ");
            sbQueryAllPeriodWithToday.append("\n                 WHEN m.lp_interval = 10 THEN CASE WHEN (:currMinute/10) < lp.value_cnt THEN 0 ELSE 1 END ");
            sbQueryAllPeriodWithToday.append("\n                 WHEN m.lp_interval = 15 THEN CASE WHEN (:currMinute/15) < lp.value_cnt THEN 0 ELSE 1 END ");
            sbQueryAllPeriodWithToday.append("\n                 WHEN m.lp_interval = 30 THEN CASE WHEN (:currMinute/30) < lp.value_cnt THEN 0 ELSE 1 END ");
            sbQueryAllPeriodWithToday.append("\n                 WHEN m.lp_interval = 60 THEN CASE WHEN (:currMinute/60) < lp.value_cnt THEN 0 ELSE 1 END ");
            sbQueryAllPeriodWithToday.append("\n    ELSE 0 END) i4 ON i3.id=i4.id ");
        }

        sbQueryAllPeriodWithToday.append("\n) ");

        StringBuffer sbQueryToday = new StringBuffer();
        sbQueryToday.append("\nSELECT customer.name ");
        sbQueryToday.append("\n       ,modem.device_serial ");
        sbQueryToday.append("\n       ,mcu.sys_name as mcuName ");
        sbQueryToday.append("\n       ,m1.mds_id ");
        sbQueryToday.append("\n       ,m1.last_read_date ");
        sbQueryToday.append("\n       ,m1.id ");
        sbQueryToday.append("\n       ,m1.lp_interval ");
        sbQueryToday.append("\nFROM meter m1 LEFT OUTER JOIN contract contract ON m1.id = contract.meter_id ");
        sbQueryToday.append("\n              LEFT OUTER JOIN customer customer ON contract.customer_id = customer.id ");
        sbQueryToday.append("\n              LEFT OUTER JOIN modem modem ON m1.modem_id = modem.id ");
        sbQueryToday.append("\n              LEFT OUTER JOIN mcu mcu ON modem.mcu_id = mcu.id ");
        sbQueryToday.append("\nWHERE 1=1 ");
        sbQueryToday.append("\nAND   m1.install_date <= :installDate ");
        sbQueryToday.append("\nAND   m1.meter = :meterType ");
        if (!"".equals(supplierId)) {
            sbQueryToday.append("\nAND   m1.supplier_id = :supplierId ");
        }
        if (!"".equals(mdsId)) {
            sbQueryToday.append("\nAND   m1.mds_id LIKE :mdsId ");
        }
        //deviceId , deviceType 의  null 값 체크
        if (!"".equals(deviceId) && !"".equals(deviceType)) {
            if (CommonConstants.DeviceType.MCU.getCode().equals(Integer.parseInt(deviceType))) {
                sbQueryToday.append("\nAND   mcu.sys_name LIKE :deviceId ");
            } else {
                sbQueryToday.append("\nAND   modem.device_serial LIKE :deviceId ");
            }
        }
        sbQueryToday.append("\nAND   m1.id NOT IN ( ");
        sbQueryToday.append("\n        SELECT i1.id FROM (SELECT m.id ");
//        sbQueryToday.append("\n         FROM METER m LEFT OUTER JOIN ").append(lpTable).append(" lp ON lp.METER_ID = m.ID ");
        sbQueryToday.append("\n        FROM meter m LEFT OUTER JOIN ").append(lpTable).append(" lp ON lp.mdev_id = m.mds_id ");
//        sbQueryToday.append("\n        AND lp.yyyymmdd = :currDate ");
        sbQueryToday.append("\n        AND lp.yyyymmddhh BETWEEN :currStartDate AND :currEndDate ");
        sbQueryToday.append("\n        AND lp.hh < :currHour ");
        sbQueryToday.append("\n        AND lp.channel = :channel ");
        sbQueryToday.append("\n        WHERE 1=1 ");
        sbQueryToday.append("\n        AND m.install_date <= :installDate ");
        sbQueryToday.append("\n        AND 1 = CASE WHEN m.lp_interval = 1 THEN CASE WHEN lp.value_cnt < 60   THEN 0 ELSE 1 END ");
        sbQueryToday.append("\n                     WHEN m.lp_interval = 5 THEN CASE WHEN lp.value_cnt < 12   THEN 0 ELSE 1 END ");
        sbQueryToday.append("\n                     WHEN m.lp_interval = 10 THEN CASE WHEN lp.value_cnt < 6   THEN 0 ELSE 1 END ");
        sbQueryToday.append("\n                     WHEN m.lp_interval = 15 THEN CASE WHEN lp.value_cnt < 4   THEN 0 ELSE 1 END ");
        sbQueryToday.append("\n                     WHEN m.lp_interval = 30 THEN CASE WHEN lp.value_cnt < 2   THEN 0 ELSE 1 END ");
        sbQueryToday.append("\n                     WHEN m.lp_interval = 60 THEN CASE WHEN lp.value_cnt < 1   THEN 0 ELSE 1 END ");
        sbQueryToday.append("\n                ELSE 0 end ");
        sbQueryToday.append("\n        GROUP BY m.id,lp.yyyymmdd ");
        sbQueryToday.append("\n        HAVING COUNT(m.id) = :currHour) i1 ");

        sbQueryToday.append("\n        INNER JOIN ");

        sbQueryToday.append("\n        (SELECT m.id ");
//        sbQueryToday.append("\n             FROM METER m LEFT OUTER JOIN ").append(lpTable).append(" lp ON lp.METER_ID = m.ID ");
        sbQueryToday.append("\n        FROM meter m LEFT OUTER JOIN ").append(lpTable).append(" lp ON lp.mdev_id = m.mds_id ");
//        sbQueryToday.append("\n        AND lp.yyyymmdd = :currDate ");
        sbQueryToday.append("\n        AND lp.yyyymmddhh BETWEEN :currStartDate AND :currEndDate ");
        sbQueryToday.append("\n        AND lp.hh = :currHour ");
        sbQueryToday.append("\n        AND lp.channel = :channel ");
        sbQueryToday.append("\n        WHERE 1=1 ");
        sbQueryToday.append("\n        AND m.install_date <= :installDate ");
        if (currMinute < 1) {
        	sbQueryToday.append("\n    AND 1 = CASE WHEN m.lp_interval = 1 THEN CASE WHEN 1 <= lp.value_cnt THEN 0 ELSE 1 END ");
        	sbQueryToday.append("\n                 WHEN m.lp_interval = 5 THEN CASE WHEN 1 <= lp.value_cnt  THEN 0 ELSE 1 END ");
        	sbQueryToday.append("\n                 WHEN m.lp_interval = 10 THEN CASE WHEN 1 <= lp.value_cnt THEN 0 ELSE 1 END ");
        	sbQueryToday.append("\n                 WHEN m.lp_interval = 15 THEN CASE WHEN 1 <= lp.value_cnt THEN 0 ELSE 1 END ");
        	sbQueryToday.append("\n                 WHEN m.lp_interval = 30 THEN CASE WHEN 1 <= lp.value_cnt THEN 0 ELSE 1 END ");
        	sbQueryToday.append("\n                 WHEN m.lp_interval = 60 THEN CASE WHEN 1 <= lp.value_cnt THEN 0 ELSE 1 END ");
        	sbQueryToday.append("\n    ELSE 0 END) i2 ON i1.id=i2.id ");
        } else {
        	sbQueryToday.append("\n    AND 1 = CASE WHEN m.lp_interval = 1 THEN CASE WHEN (:currMinute/1) < lp.value_cnt THEN 0 ELSE 1 END ");
        	sbQueryToday.append("\n                 WHEN m.lp_interval = 5 THEN CASE WHEN (:currMinute/5) < lp.value_cnt THEN 0 ELSE 1 END ");
        	sbQueryToday.append("\n                 WHEN m.lp_interval = 10 THEN CASE WHEN (:currMinute/10) < lp.value_cnt THEN 0 ELSE 1 END ");
        	sbQueryToday.append("\n                 WHEN m.lp_interval = 15 THEN CASE WHEN (:currMinute/15) < lp.value_cnt THEN 0 ELSE 1 END ");
        	sbQueryToday.append("\n                 WHEN m.lp_interval = 30 THEN CASE WHEN (:currMinute/30) < lp.value_cnt THEN 0 ELSE 1 END ");
        	sbQueryToday.append("\n                 WHEN m.lp_interval = 60 THEN CASE WHEN (:currMinute/60) < lp.value_cnt THEN 0 ELSE 1 END ");
        	sbQueryToday.append("\n    ELSE 0 END) i2 ON i1.id=i2.id ");
        }
        sbQueryToday.append("\n) ");

        Query query = null;
        if (Integer.parseInt(searchEndDate) < Integer.parseInt(currDate)) {
            // 조회종료일이 현재일자보다 이전을경우 전체일자 조회
            query = getSession().createSQLQuery(sbQueryAllPeriod.toString());
//            query.setString("searchStartDate", searchStartDate);
//            query.setString("searchEndDate", searchEndDate);
            query.setString("startDate", searchStartDate + "00");
            query.setString("endDate", searchEndDate + "23");
            query.setString("installDate", searchStartDate + CommonConstants.DefaultDate.LAST_HHMMSS.getValue());
            query.setString("meterType", meterType);
            query.setInteger("channel", channel);
            query.setInteger("period", period);
            if (!"".equals(supplierId)) {
                query.setInteger("supplierId", Integer.parseInt(supplierId));
            }
            if (!"".equals(mdsId)) {
                query.setString("mdsId", "%"+ mdsId+"%");
            }
            if (!"".equals(deviceId)) {
                query.setString("deviceId", "%"+ deviceId+"%");
            }

        } else {
            if (Integer.parseInt(searchStartDate) < Integer.parseInt(currDate)) {
                // 조회종료일이 현재일자이고 조회시작일이 현재일자 이전일경우 시작일~종료일전일,현재일자,현재시간 별로 조회
                query = getSession().createSQLQuery(sbQueryAllPeriodWithToday.toString());
//                query.setString("searchStartDate", searchStartDate);
//                query.setString("searchEndDate", CalendarUtil.getDateWithoutFormat(searchEndDate,Calendar.DATE, -1));
                query.setString("startDate", searchStartDate + "00");
                query.setString("endDate", CalendarUtil.getDateWithoutFormat(searchEndDate,Calendar.DATE, -1) + "23");
                query.setString("installDate", searchStartDate + CommonConstants.DefaultDate.LAST_HHMMSS.getValue());
                query.setString("meterType", meterType);
//                query.setString("currDate", currDate);
                query.setString("currStartDate", currDate + "00");
                query.setString("currEndDate", currDate + "23");
                query.setString("currHour", currHour);
                if (currMinute >= 1) {
                    query.setInteger("currMinute", currMinute);
                }
                query.setInteger("channel", channel);
                query.setInteger("period", period);
                if (!"".equals(supplierId)) {
                    query.setInteger("supplierId", Integer.parseInt(supplierId));
                }
                if (!"".equals(mdsId)) {
                    query.setString("mdsId", "%"+ mdsId+"%");
                }
                if (!"".equals(deviceId)) {
                    query.setString("deviceId", "%"+ deviceId+"%");
                }
            } else {
                // 조회일자가 현재일자 하루일경우 현재날짜,현재시간만 조회
                query = getSession().createSQLQuery(sbQueryToday.toString());
                query.setString("installDate", searchStartDate + CommonConstants.DefaultDate.LAST_HHMMSS.getValue());
                query.setString("meterType", meterType);
//                query.setString("currDate", currDate);
                query.setString("currStartDate", currDate + "00");
                query.setString("currEndDate", currDate + "23");
                query.setString("currHour", currHour);
                if (currMinute >= 1) {
                    query.setInteger("currMinute", currMinute);
                }
                query.setInteger("channel", channel);
                if (!"".equals(supplierId)) {
                    query.setInteger("supplierId", Integer.parseInt(supplierId));
                }
                if (!"".equals(mdsId)) {
                    query.setString("mdsId", "%"+ mdsId+"%");
                }
                if (!"".equals(deviceId)) {
                    query.setString("deviceId", "%"+ deviceId+"%");
                }
            }
        }

        // query 결과목록
        Object totalCnt = query.uniqueResult();

        return totalCnt.toString();
    } //method End

    
    /**
     * SP-414, SP-784(UPDATE)
     * @param params
     * @return
     */
    @Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
    public List<Object> getMissingMetersForRecollectByHour(Map<String, Object> params) {
        String searchStartDate = (String) params.get("searchStartDate"); //yyyymmddhh
        String searchEndDate = (String) params.get("searchEndDate"); // yyyymmddhh
        String meterType = StringUtil.nullToBlank(params.get("meterType"));
		// -> INSERT START 2018/02/15 #SP-892
        String modemType = StringUtil.nullToBlank(params.get("modemType"));
        String protocolType = StringUtil.nullToBlank(params.get("protocolType"));
		// <- INSERT END   2018/02/15 #SP-892
        String supplierId = StringUtil.nullToBlank(params.get("supplierId"));
        Integer channel = (Integer) params.get("channel");
        String mdsId = StringUtil.nullToBlank(params.get("mdsId"));
        String deviceType = StringUtil.nullToBlank((String) params.get("deviceType"));
        String deviceId = StringUtil.nullToBlank((String) params.get("deviceId"));

        String today = TimeUtil.getCurrentTimeMilli(); // yyyyMMddHHmmss
        String currHour = today.substring(8, 10);
        Integer currMinute = Integer.parseInt(today.substring(10, 12));
        //String currDate = today.substring(0, 8);
        String currYyyymmddhh = today.substring(0, 10);
        Integer page = (Integer) params.get("page");
        Integer limit = (Integer) params.get("limit");
        String lastLinkTime = StringUtil.nullToBlank((String) params.get("lastLinkTime"));
        String lastReadDate = StringUtil.nullToBlank((String) params.get("lastReadDate"));
        String sysId = StringUtil.nullToBlank((String) params.get("sysId")); // SP-677
        String locationName = StringUtil.nullToBlank(params.get("locationName")); //SP-1051
        
        if (Integer.parseInt(searchEndDate) >= Integer.parseInt(currYyyymmddhh)) {
    		int year = Integer.parseInt(today.substring(0,4));
    	    int month = Integer.parseInt(today.substring(4,6))-1;
    	    int day = Integer.parseInt(today.substring(6,8));
    	    int hour = Integer.parseInt(today.substring(8,10));
    	    Calendar cal = Calendar.getInstance();
    	    cal.set(year, month, day, hour, 0);
    	    cal.add(Calendar.HOUR, -1);
    	    String preHourYyyymmddhhmmss = DateTimeUtil.getDateString(cal.getTime());
    	    searchEndDate = preHourYyyymmddhhmmss.substring(0,10);
        }
        
        String lpTable = CommonConstants.MeterType.valueOf(meterType).getLpTableName();

        int periodhour = 0;
        try {
	        long fromtime = DateTimeUtil.getDateFromYYYYMMDDHHMMSS(searchStartDate+"0000").getTime();
	        long totime = DateTimeUtil.getDateFromYYYYMMDDHHMMSS(searchEndDate+"0000").getTime();
	        periodhour =  (int) ((totime - fromtime)/1000/60/60) + 1;
        } catch (ParseException e) {
        	logger.error ( "" + e,e);
        }
        
        StringBuffer sbQueryAllPeriod = new StringBuffer();
        sbQueryAllPeriod.append("\nSELECT mcu.sys_id ");
        sbQueryAllPeriod.append("\n       ,mcu.id AS MCU_ID ");
        sbQueryAllPeriod.append("\n       ,modem.device_serial ");
        sbQueryAllPeriod.append("\n       ,m1.mds_id ");
        sbQueryAllPeriod.append("\n       ,m1.last_read_date ");
        sbQueryAllPeriod.append("\n       ,m1.id AS METER_ID ");
        sbQueryAllPeriod.append("\n       ,modem.modem_type AS MODEM_TYPE, modem.protocol_type AS PROTOCOL_TYPE ");
        sbQueryAllPeriod.append("\nFROM meter m1 ");
        sbQueryAllPeriod.append("\n     LEFT OUTER JOIN modem modem ON m1.modem_id = modem.id ");
        sbQueryAllPeriod.append("\n     LEFT OUTER JOIN mcu mcu ON modem.mcu_id = mcu.id ");
        sbQueryAllPeriod.append("\n     LEFT OUTER JOIN code cd ON cd.id = m1.meter_status ");
        sbQueryAllPeriod.append("\n     LEFT OUTER JOIN location lo ON m1.location_id = lo.id"); //SP-1051            
        sbQueryAllPeriod.append("\nWHERE m1.install_date <= :installDate ");
        sbQueryAllPeriod.append("\nAND m1.meter = :meterType ");
		// -> INSERT START 2018/02/15 #SP-892
        if (!"".equals(modemType)) {
            sbQueryAllPeriod.append("\nAND modem.modem_type = :modemType ");
        }
        if (!"".equals(protocolType)) {
            sbQueryAllPeriod.append("\nAND modem.protocol_type = :protocolType ");
        }
		// <- INSERT END   2018/02/15 #SP-892
        if (!"".equals(supplierId)) {
            sbQueryAllPeriod.append("\nAND m1.supplier_id = :supplierId ");
        }
        if (!"".equals(mdsId)) {
            sbQueryAllPeriod.append("\nAND m1.mds_id LIKE :mdsId ");
        }
        if ( !"".equals(sysId) ) {
        	sbQueryAllPeriod.append("\nAND mcu.sys_id = :sysId ");
        }
        if ( !"".equals(lastLinkTime) ) {
        	sbQueryAllPeriod.append("\nAND modem.last_link_time >= :lastLinkTime ");
        }
        if ( !"".equals(lastReadDate) ) {
        	sbQueryAllPeriod.append("\nAND m1.last_read_date >= :lastReadDate ");
        }
        // deviceId , deviceType 의 null 값 체크
        if (!"".equals(deviceId) && !"".equals(deviceType)) {
            if (CommonConstants.DeviceType.MCU.getCode().equals(Integer.parseInt(deviceType))) {
                sbQueryAllPeriod.append("\nAND mcu.sys_name LIKE :deviceId ");
            } else {
                sbQueryAllPeriod.append("\nAND modem.device_serial LIKE :deviceId ");
            }
        }
        // INSERT START SP-1051
        if (!"".equals(locationName)) {
        	sbQueryAllPeriod.append("\nAND lo.name IN ("); 
        	sbQueryAllPeriod.append(locationName);
        	sbQueryAllPeriod.append(") ");
        }        
        // INSERT END SP-1051
        
        sbQueryAllPeriod.append("\nAND   (cd.id IS NULL ");
        sbQueryAllPeriod.append("\n    OR cd.code != :deleteCode ");
        sbQueryAllPeriod.append("\n    OR (cd.code = :deleteCode AND m1.delete_date > :deleteDate)) ");
        sbQueryAllPeriod.append("\nAND m1.id NOT IN ( ");
        sbQueryAllPeriod.append("\n    SELECT x.id ");
        sbQueryAllPeriod.append("\n    FROM ( ");
        sbQueryAllPeriod.append("\n        SELECT m.id,lp.yyyymmdd ");
        sbQueryAllPeriod.append("\n        FROM meter m ");
        sbQueryAllPeriod.append("\n             LEFT OUTER JOIN ");
        sbQueryAllPeriod.append("\n             code c ");
        sbQueryAllPeriod.append("\n             ON c.id = m.meter_status ");
        sbQueryAllPeriod.append("\n             LEFT OUTER JOIN ");
        sbQueryAllPeriod.append("\n             ").append(lpTable).append(" lp ");
        sbQueryAllPeriod.append("\n             ON  lp.mdev_id = m.mds_id ");
        sbQueryAllPeriod.append("\n             AND lp.yyyymmddhh BETWEEN :startDate AND :endDate ");
        sbQueryAllPeriod.append("\n             AND lp.channel = :channel ");
        sbQueryAllPeriod.append("\n        		AND lp.dst = 0 ");			// INSERT SP-1051                                
        sbQueryAllPeriod.append("\n        WHERE m.install_date <= :installDate ");
        sbQueryAllPeriod.append("\n        AND   (m.lp_interval IN (1, 5, 10, 15, 30, 60) ");
        sbQueryAllPeriod.append("\n           AND lp.value_cnt >= (60/m.lp_interval)) ");
        sbQueryAllPeriod.append("\n        AND   (c.id IS NULL ");
        sbQueryAllPeriod.append("\n            OR c.code != :deleteCode ");
        sbQueryAllPeriod.append("\n            OR (c.code = :deleteCode AND m.delete_date > :deleteDate)) ");

        // INSERT START SP-1051
        if (!"".equals(locationName)) {
        	sbQueryAllPeriod.append("\n    	AND   m.location_id IN (SELECT id FROM location WHERE name IN ");
        	sbQueryAllPeriod.append(locationName);
        	sbQueryAllPeriod.append(")) ");
        }
        // INSERT END SP-1051         
        
        sbQueryAllPeriod.append("\n        GROUP BY m.id, lp.yyyymmdd ");
        sbQueryAllPeriod.append("\n        HAVING COUNT(m.id) =  :periodhour");
        sbQueryAllPeriod.append("\n    ) x ");
        sbQueryAllPeriod.append("\n    WHERE x.yyyymmdd IS NOT NULL ");
        sbQueryAllPeriod.append("\n    GROUP BY x.id ");
        sbQueryAllPeriod.append("\n    HAVING COUNT(x.id) = 1 ");
        sbQueryAllPeriod.append("\n) ");


        Query query = null;
        query = getSession().createSQLQuery(sbQueryAllPeriod.toString());
        query.setString("startDate", searchStartDate );
        query.setString("endDate", searchEndDate );
        query.setString("installDate", searchStartDate + "5959");
        query.setString("meterType", meterType);
        query.setInteger("channel", channel);
 //       query.setInteger("period", period);
        query.setInteger("periodhour", periodhour);
		// -> INSERT START 2018/02/15 #SP-892
        if (!"".equals(modemType)) {
            query.setString("modemType", modemType);
        }
        if (!"".equals(protocolType)) {
            query.setString("protocolType", protocolType);
        }
		// <- INSERT END   2018/02/15 #SP-892
        if (!"".equals(supplierId)) {
        	query.setInteger("supplierId", Integer.parseInt(supplierId));
        }
        if (!"".equals(mdsId)) {
        	query.setString("mdsId", "%" + mdsId + "%");
        }
        if (!"".equals(deviceId)) {
        	query.setString("deviceId", "%" + deviceId + "%");
        }

 
        query.setString("deleteCode", MeterCodes.DELETE_STATUS.getCode());
        query.setString("deleteDate", searchStartDate + "5959");
	    if ( !"".equals(lastLinkTime) ) {
	    	query.setString("lastLinkTime", lastLinkTime);
	    }
	    if ( !"".equals(lastReadDate) ) {
	    	query.setString("lastReadDate", lastReadDate);
	    }
	    Integer start = 0;
	    if ( page != null){
	    	start = (page - 1) * limit;

	    }
    	query.setFirstResult(start.intValue());
    	if ( limit != null )
    		query.setMaxResults(limit.intValue());
    	
        // query 결과목록
        List<Object> result = query.list();

        List<Object> resultList = new ArrayList<Object>();

//        int i = 1;
//        for (Object obj : result) {
//            HashMap<String, Object> resultMap = new HashMap<String, Object>();
//            Object[] objs = (Object[]) obj;
//
////            params.put("meterId", ((Number) objs[5]).intValue());
////            params.put("mdsId", (String) objs[3]);
////
////            if (objs[6] == null) {
////                params.put("lpInterval", 60); // lpInterval 값이 null일 경우 기본값(60) 입력
////            } else {
////                params.put("lpInterval", ((Number) objs[6]).intValue());
////            }
////
////            Map<String, Object> countMap = meteringlpDao.getMissingCountByDay(params);
//
//            resultMap.put("no", Integer.toString(i++));
//            resultMap.put("customerName", (String) objs[0]);
//
//            if (!"".equals(deviceType)) {// deviceType의 값이 null일 경우 체크
//                if (CommonConstants.DeviceType.Modem.getCode().equals(Integer.parseInt(deviceType))) {
//                    resultMap.put("deviceNo", (String) objs[1]);
//                } else {
//                    resultMap.put("deviceNo", (String) objs[2]);
//                }
//            }
//            resultMap.put("mdsId", (String) objs[3]);
////            resultMap.put("missingCnt", countMap.get("totalCount"));
//            resultMap.put("lastReadDate", (String) objs[4]);
//            resultMap.put("meterId", params.get("meterId"));
//            resultMap.put("lpInterval", params.get("lpInterval"));
//            resultList.add(resultMap);
//        }
//
//        return resultList;

        for (Object obj : result) {
            HashMap<String, Object> resultMap = new HashMap<String, Object>();
            Object[] objs = (Object[]) obj;
            resultMap.put("sysId" , (String)objs[0]);
            resultMap.put("mcuId", objs[1] == null ? null : ((Number) objs[1]).intValue());
            resultMap.put("deviceSerial", (String) objs[2]);
            resultMap.put("mdsId", (String) objs[3]);
            resultMap.put("lastReadDate", (String) objs[4]);
            resultMap.put("meterId", ((Number) objs[5]).intValue());
            resultMap.put("modemType", (String)objs[6]);
            resultMap.put("protocolType", (String)objs[7]);
            resultList.add(resultMap);
        }
        return resultList;
    } // method End

    @Transactional(readOnly=true, propagation=Propagation.REQUIRED)
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

    @Transactional(readOnly=true, propagation=Propagation.REQUIRED)
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

    /**
     * method name : getMeterMapDataWithoutGpio<b/>
     * method Desc : Meter Management 맥스가젯의 위치정보 탭에서 맵정보를 조회한다.
     *
     * @param condition
     * @return
     */
    @Transactional(readOnly=true, propagation=Propagation.REQUIRED)
    public List<Meter> getMeterMapDataWithoutGpio(HashMap<String, Object> condition) {
        Criteria criteria = getSession().createCriteria(Meter.class);

        Set<String> set = condition.keySet();
        Object []hmKeys = set.toArray();
        for (int i=0; i<hmKeys.length; i++) {
            String key = (String)hmKeys[i];
            criteria.add(Restrictions.eq(key, condition.get(key)));
        }

        List<Meter> meters = (List<Meter>) criteria.list();

        return meters;
    }

    @Transactional(readOnly=true, propagation=Propagation.REQUIRED)
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
    @Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
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

    @Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
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

    @SuppressWarnings("unused")
    @Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
    public List<Object> getMeteringDataByMeterGrid(Map<String, Object> condition){

        List<Object> gridData   = new ArrayList<Object>();
        List<Object> result     = new ArrayList<Object>();

        StringBuffer sbQuery      = new StringBuffer();

        String curPage          = StringUtil.nullToBlank(condition.get("curPage"));

        Integer supplierId = Integer.parseInt(condition.get("supplierId").toString());

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
        sbQuery.append("  ORDER BY 1          desc      \n");
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

        Supplier supplier = supplierDao.get(supplierId);
        DecimalFormat dfMd = DecimalUtil.getDecimalFormat(supplier.getMd());
        
        DecimalFormat dfMddot = DecimalUtil.getMDStyle(supplier.getMd());

        for(int i=0 ; i < dataListLen ; i++){

            HashMap gridDataMap = new HashMap();
            Object[] resultData = (Object[]) dataList.get(i);


            gridDataMap.put("No"            , dfMddot.format(totalCount.intValue() -i - firstIdx ));
            gridDataMap.put("meteringDate", TimeLocaleUtil.getLocaleDate(StringUtil.nullToBlank(resultData[0]) , supplier.getLang().getCode_2letter(), supplier.getCountry().getCode_2letter()));
            gridDataMap.put("usage"         , resultData[1] == null ? "" : dfMd.format(resultData[1]));
            gridDataMap.put("co2"           , resultData[2] == null ? "" : dfMd.format(resultData[2]));

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

            /*for (MeterType _meterType : MeterType.values())
                if (_meterType.name().equals(meterType))
                    tableName = _meterType.getMonthViewName();*/

            for (MeterType _meterType : MeterType.values())
                if (_meterType.name().equals(meterType))
                    tableName = _meterType.getDayViewName();

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
                       .append("              AND VALUE_"+strI+" IS NOT NULL \n")
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
                        tableName = _meterType.getDayViewName();

            }else if(searchDateType.equals(DateType.MONTHLY.getCode())){
                columnDate = "YYYYMM";
                startDate  = searchStartDate.substring(0,6);
                endDate    = searchEndDate.substring(0,6);

                for (MeterType _meterType : MeterType.values())
                    if (_meterType.name().equals(meterType))
                        tableName = _meterType.getMonthViewName();
            }

            sbQuery.append(" SELECT "+ columnDate +"                    \n")
                   .append("      , SUM(CASE WHEN CHANNEL = 1   AND TOTAL_VALUE IS NOT NULL THEN TOTAL_VALUE ELSE 0 END) AS USAGE   \n")
                   .append("      , SUM(CASE WHEN CHANNEL = 0   AND TOTAL_VALUE IS NOT NULL THEN TOTAL_VALUE ELSE 0 END) AS CO2     \n")
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
    @Deprecated
    @Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
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
        sb.append(" ORDER BY t.mds_id ASC");

        SQLQuery query = getSession().createSQLQuery(sb.toString());
        //logger.debug(sb.toString());
        return query.setInteger("supplierId", Integer.parseInt((String)condition.get("supplierId")))
                    .list();
    }

    /**
     * method name : getMemberSelectData<b/>
     * method Desc : Group Management 가젯에서 Member 로 등록 가능한 Meter 리스트를 조회한다.
     *
     * @param conditionMap
     * @return
     */
    @Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
    public List<Object> getMemberSelectData(Map<String, Object> conditionMap) {
    	 List<Object> gridData   = new ArrayList<Object>();      
         List<Object> result     = new ArrayList<Object>();
         
        Integer supplierId = (Integer)conditionMap.get("supplierId");
        Integer groupId = (Integer)conditionMap.get("groupId");
        String subType = StringUtil.nullToBlank(conditionMap.get("subType"));
        String memberName = StringUtil.nullToBlank(conditionMap.get("memberName"));
     // 검색조건
        String sMeterType         = StringUtil.nullToBlank(conditionMap.get("sMeterType"));
        String sMdsId             = StringUtil.nullToBlank(conditionMap.get("sMdsId"));
        String sStatus            = StringUtil.nullToBlank(conditionMap.get("sStatus"));
        String sMeterGroup        = StringUtil.nullToBlank(conditionMap.get("sMeterGroup"));
        String sMeterAddress      = StringUtil.nullToBlank(conditionMap.get("sMeterAddress"));

        String sMcuId             = StringUtil.nullToBlank(conditionMap.get("sMcuId"));
        String sConsumLocationId  = StringUtil.nullToBlank(conditionMap.get("sConsumLocationId"));

        String sVendor            = StringUtil.nullToBlank(conditionMap.get("sVendor"));
        String sModel             = StringUtil.nullToBlank(conditionMap.get("sModel"));
        String sInstallStartDate  = StringUtil.nullToBlank(conditionMap.get("sInstallStartDate"));
        String sInstallEndDate    = StringUtil.nullToBlank(conditionMap.get("sInstallEndDate"));

        String sModemYN           = StringUtil.nullToBlank(conditionMap.get("sModemYN"));
        String sCustomerYN        = StringUtil.nullToBlank(conditionMap.get("sCustomerYN"));
        String sMcuName           = StringUtil.nullToBlank(conditionMap.get("sMcuName"));
        String sLastcommStartDate = StringUtil.nullToBlank(conditionMap.get("sLastcommStartDate"));
        String sLastcommEndDate   = StringUtil.nullToBlank(conditionMap.get("sLastcommEndDate"));

        String sLocationId            = StringUtil.nullToBlank(conditionMap.get("sLocationId"));
        String sOrder             = StringUtil.nullToBlank(conditionMap.get("sOrder"));
        String sCommState         = StringUtil.nullToBlank(conditionMap.get("sCommState"));
        String sGroupOndemandYN   = StringUtil.nullToBlank(conditionMap.get("sGroupOndemandYN"));
        
        //String isManualMeter      = StringUtil.nullToBlank(conditionMap.get("isManualMeter"));
        int page          = (Integer) conditionMap.get("page");
        int limit          = (Integer) conditionMap.get("limit");

        String sCustomerId = StringUtil.nullToBlank(conditionMap.get("sCustomerId"));
        String sCustomerName = StringUtil.nullToBlank(conditionMap.get("sCustomerName"));
        
        String sHwVersion = StringUtil.nullToBlank(conditionMap.get("sHwVersion"));
        String sFwVersion = StringUtil.nullToBlank(conditionMap.get("sFwVersion"));
        String sGs1 = StringUtil.nullToBlank(conditionMap.get("sGs1"));
        //List<Integer> locationIdList = (List<Integer>)conditionMap.get("locationIdList");

        //StringBuilder sbQuery = new StringBuilder();
        StringBuffer sb = new StringBuffer();

        sb.append("\nSELECT me.ID AS value, ");
        sb.append("\n       me.MDS_ID AS text, ");
        sb.append("\n       me.METER AS type, ");
        sb.append("\n       loc.NAME AS locName ");
        
        sb.append("\nFROM METER me ");
        //sb.append("\nleft outer join me.meterStatus meterStatus ");
        //sb.append("\nleft outer join me.meterType meterType "); 
        sb.append("\n       LEFT OUTER JOIN MODEM mo         	 "); //MODEM
        sb.append("\n       ON ( me.MODEM_ID = mo.ID)          	 "); 
        sb.append("\n		LEFT OUTER JOIN MCU mcu "); //MCU
        sb.append("\n		ON ( mo.MCU_ID = mcu.ID)  ");
        sb.append("\n		LEFT OUTER JOIN LOCATION loc "); //LOCATION
        sb.append("\n		ON ( me.LOCATION_ID = loc.ID) "); 
        sb.append("\n		LEFT OUTER JOIN devicemodel model "); //DEVICEMODEL MODEL
        sb.append("\n		ON me.devicemodel_id = model.id 	 "); 
        sb.append("\n		LEFT OUTER JOIN devicevendor vendor "); //DEVICEVENDOR
        sb.append("\n		ON model.devicevendor_id = vendor.id "); 
        sb.append("\n		LEFT OUTER JOIN devicemodel modemdevice "); //DEVICEMODEL MODEMDEVICE
        sb.append("\n		ON mo.devicemodel_id = modemdevice.id  "); 
        sb.append("\n		LEFT OUTER JOIN CONTRACT contract   "); //CONTRACT
        sb.append("\n		ON (me.ID = contract.METER_ID)    "); 
        sb.append("\n		LEFT OUTER JOIN CUSTOMER customer "); //CUSTOMER
        sb.append("\n		ON ( contract.CUSTOMER_ID = customer.ID )   "); 
        sb.append("\n		LEFT OUTER JOIN CODE co     "); //CODE
        sb.append("\n		ON ( me.meter_status = co.ID)   "); 
        if (!sMeterGroup.equals("")) {
        	sb.append("       LEFT OUTER JOIN group_member gm 			 \n")
	            .append("       ON me.mds_id = gm.member 						 \n")
	            .append("       LEFT OUTER JOIN aimirgroup ag 					 \n")
	            .append("       ON gm.group_id = ag.id							 \n");
        }
        sb.append("\n		 WHERE me.SUPPLIER_ID = :supplierId ");
        sb.append("\nAND (me.METER_STATUS is null OR co.code <> :deleteCode) ");

        if (!memberName.isEmpty()) {
            sb.append("\nAND   me.MDS_ID LIKE :memberName ");
        }
        
        // 검색조건
        if (!sMeterGroup.equals("")) {
            sb.append("\n    AND gm.group_id = "+ sMeterGroup );
        }

        if (!sMeterType.equals(""))
      	  sb.append("     AND me.METER = '"+ sMeterType +"'");
        
        if (!sHwVersion.equals(""))
      	  sb.append("     AND me.HW_VERSION = '"+ sHwVersion +"'"); //여기
        
        if (!sFwVersion.equals(""))
      	  sb.append("     AND me.SW_VERSION = '"+ sFwVersion +"'");

        if (!sMdsId.equals("")) 
      	  sb.append("     AND me.mds_ID LIKE '"+ sMdsId +"%'");
        
        if (!sMeterAddress.equals("")) {
            sb.append("     AND ( (LOWER(customer.address)  LIKE  '%"+ sMeterAddress.toLowerCase() +"%') ")
                .append(" 				OR (LOWER(customer.address1) LIKE  '%"+ sMeterAddress.toLowerCase() +"%') ")
                .append(" 				OR (LOWER(customer.address2) LIKE  '%"+ sMeterAddress.toLowerCase() +"%') ")
                .append(" 				OR (LOWER(customer.address3) LIKE  '%"+ sMeterAddress.toLowerCase() +"%') ) ");
       }

       if (!sStatus.equals(""))
            sb.append("     AND me.METER_STATUS = "+ sStatus);
       
       if (!sMcuId.equals(""))
      	 sb.append("     AND mo.MCU_ID LIKE '"+ sMcuId + "%'");

       if(!sLocationId.equals("")){
       	sb.append("\nAND me.LOCATION_ID = '"+ sLocationId +"' ");
       }
       /*if (locationIdList != null)
      	 sb.append("     AND me.location.id IN (:locationIdList)");*/

       if(!sMcuName.equals("")){
           if(sMcuName.equals("-")){
          	 sb.append("     AND mcu.SYS_ID IS NULL");                
           }else{
          	 sb.append("     AND mcu.SYS_ID like '" + sMcuName+ "'");
           }
       }

       if (!sConsumLocationId.equals(""))
      	 sb.append("     AND contract.CONTRACT_NUMBER like '"+ sConsumLocationId +"%' " );

       if (!sVendor.equals("0") && !sVendor.equals("")) {
      	 sb.append("     AND vendor.id = "+ sVendor );
       }

       if (!sModel.equals("")) {
      	 sb.append("     AND model.id = "+ sModel );
       }

       if (!sInstallStartDate.equals(""))
      	 sb.append("     AND me.INSTALL_DATE >= '"+ sInstallStartDate +"000000'");

       if (!sInstallEndDate.equals(""))
      	 sb.append("     AND me.INSTALL_DATE <= '"+ sInstallEndDate +"235959'");


       if (sModemYN.equals("Y"))
      	 sb.append("     AND me.MODEM_ID IS NOT NULL");
       else if (sModemYN.equals("N"))
      	 sb.append("     AND me.MODEM_ID IS NULL");

       if (sCustomerYN.equals("Y"))
      	 sb.append("     AND contract.ID IS NOT NULL");
       else if (sCustomerYN.equals("N"))
      	 sb.append("     AND contract.ID IS NULL");


       if (!sLastcommStartDate.equals(""))
      	 sb.append("     AND me.LAST_READ_DATE >= '"+ sLastcommStartDate +"000000'");

       if (!sLastcommEndDate.equals(""))
      	 sb.append("     AND me.LAST_READ_DATE <= '"+ sLastcommEndDate +"235959'");

       /*if (sCommState.equals("0"))
      	 sb.append("     AND me.LAST_READ_DATE  >= :datePre24H \n");
       else if (sCommState.equals("1"))
      	 sb.append("     AND LAST_READ_DATE < :datePre24H " +
                          "     AND LAST_READ_DATE >= :datePre48H \n");
       else if (sCommState.equals("2"))
      	 sb.append("     AND LAST_READ_DATE < :datePre48H ");*/

       // 매뉴얼 미터 여부
       /*if (!isManualMeter.isEmpty()) {
      	 sb.append("     AND is_manual_meter = " + Integer.parseInt(isManualMeter) + " ");
       }*/

       if (!"".equals(sCustomerId)) {
      	 sb.append("    AND customer.CUSTOMERNO like '" + sCustomerId +"%'");
       }
       if (!"".equals(sCustomerName)) {
      	 sb.append("    AND customer.NAME like '" + sCustomerName + "%'");
       }
       if (!"".equals(sGs1)) {
      	 sb.append("    AND me.gs1 like '%" + sGs1 + "%'");
       }
       // if(subType.isEmpty()) {
        	sb.append("\nAND   NOT EXISTS ( ");
            sb.append("\n    SELECT 'X' ");
            sb.append("\n    FROM group_member gm ");
            sb.append("\n    WHERE gm.member = me.MDS_ID ");
            sb.append("\n    AND gm.group_id = :groupId ");
        /*} else {
            sb.append("\nAND   NOT EXISTS ( ");
            sb.append("\n    SELECT 'X' ");
            sb.append("\n    FROM GroupMember gm, HomeGroup hg ");
            sb.append("\n    WHERE gm.groupId = hg.id ");
            sb.append("\n    AND gm.member = me.mdsId ");
        }*/
        
        sb.append("\n) ");
        sb.append("\nORDER BY text ");
        
        //String datePre24H = DateTimeUtil.calcDate(Calendar.HOUR, -24, "yyyyMMddHHmmss");
        //String datePre48H = DateTimeUtil.calcDate(Calendar.HOUR, -48, "yyyyMMddHHmmss");
        
        SQLQuery query = getSession().createSQLQuery(new SQLWrapper().getQuery(sb.toString()));
        query.setInteger("supplierId", supplierId);
        query.setString("deleteCode", MeterStatus.Delete.name());
        if(subType.isEmpty()) {
        	query.setInteger("groupId", groupId);
        }
        if (!memberName.isEmpty()) {
            query.setString("memberName", "%" + memberName + "%");
        }
        
        query.setFirstResult((page-1) * limit);      
        query.setMaxResults(limit);
        
        List dataList = null;
	  	dataList = query.list();
	  	
        // 전체 건수
        StringBuffer countQuery = new StringBuffer();
        countQuery.append("\n SELECT COUNT( * ) ");
        countQuery.append("\n FROM (  ");
        countQuery.append(sb);
        countQuery.append("\n ) countTotal ");
        
        SQLQuery countQueryObj = getSession().createSQLQuery(new SQLWrapper().getQuery(countQuery.toString()));
        countQueryObj.setInteger("supplierId", supplierId);
        if(subType.isEmpty()) {
        	countQueryObj.setInteger("groupId", groupId);
        }
        countQueryObj.setString("deleteCode", MeterStatus.Delete.name());
        if (!memberName.isEmpty()) {
        	countQueryObj.setString("memberName", "%" + memberName + "%");
        }
        
        Number totalCount = (Number)countQueryObj.uniqueResult();
        result.add(totalCount.toString());

        //sbQuery.append(sb);
        
        

	  		
	  	// 실제 데이터
	  	int dataListLen = 0;
	  	if(dataList != null)
	  		dataListLen= dataList.size();
	  				
	  	for (int i = 0; i < dataListLen; i++) {
	  		 HashMap chartDataMap = new HashMap();
	  		 Object[] resultData = (Object[]) dataList.get(i);
	  			
	  		 List<Code> CodemeterType = codedao.getChildCodes(Code.METER_TYPE);

	            for (int j = 0; j < CodemeterType.size(); j++) {
	                if (resultData[2].equals(CodemeterType.get(j).getName())) {
	                    if (CodemeterType.get(j).getDescr() != null) {
	                        resultData[2] = CodemeterType.get(j).getDescr().toString();
	                    } else {
	                        resultData[2] = resultData[2];
	                    }
	                }
	            }
	            
	  		 chartDataMap.put("value",           resultData[0] );
	  		 chartDataMap.put("text",      resultData[1]);                 
	  		 chartDataMap.put("type",    resultData[2]);
	  		 chartDataMap.put("locName",     resultData[3]);
	  		 gridData.add(chartDataMap);
	  	  }
	  	  result.add(gridData);
	  		
        return result;
    }
    
    @Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
    public List<Object> getMeterSupplierList() {

        StringBuffer sb = new StringBuffer();
        sb.append("select distinct(s.id) as SUPPLIER  from supplier s,meter m where s.id= m.SUPPLIER_ID");

        SQLQuery query = getSession().createSQLQuery(sb.toString());

        return query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
    }

    //SLA대상 미터는 오늘 이전에 계약이 적용된 고객의 미터이다.
    @Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
    public Integer getSLAMeterCount(String today, int supplierId) {

        StringBuffer sb = new StringBuffer();

        sb.append("\nSELECT COUNT(DISTINCT(m.id)) AS cnt ");
        sb.append("\nFROM contract c, meter m ");
        sb.append("\nWHERE c.apply_date <= :today ");
        sb.append("\nAND   c.supplier_id = :supplierId ");
        sb.append("\nAND   c.meter_id = m.id ");

        Query query = getSession().createSQLQuery(sb.toString());
        query.setString("today", today);
        query.setInteger("supplierId", supplierId);

        Integer meterCount = ((Number)query.uniqueResult()).intValue();

        return meterCount;
    }

    /**
     * method name : getEbsDtsTreeDtsMeterNodeData<b/>
     * method Desc : Energy Balance Monitoring 맥스가젯의 DTS Tree 의 Meter Node Data 를 조회한다.
     *
     * @param conditionMap
     * @return
     */
    @Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
    public List<Map<String, Object>> getEbsDtsTreeDtsMeterNodeData(Map<String, Object> conditionMap) {
        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
        String searchPreStartDate = StringUtil.nullToBlank(conditionMap.get("searchPreStartDate"));
        String searchStartDate = StringUtil.nullToBlank(conditionMap.get("searchStartDate"));
        String searchEndDate = StringUtil.nullToBlank(conditionMap.get("searchEndDate"));
        Set<Integer> dtsIds = (Set<Integer>)conditionMap.get("dtsIds");

        // 해당하는 dtsId 가 없으면 빈 array return
        if (dtsIds == null || dtsIds.size() <= 0) {
            return result;
        }

        StringBuilder sb = new StringBuilder();

        sb.append("\nSELECT mdsId AS MDS_ID, ");
        sb.append("\n       meterId AS METER_ID, ");
        sb.append("\n       dtsId AS DTS_ID, ");
        sb.append("\n       locationId AS LOCATION_ID, ");
        sb.append("\n       SUM(importEnergyTotal) AS IMPORT_ENERGY_TOTAL, ");
        sb.append("\n       SUM(importPhaseA) AS IMPORT_PHASE_A, ");
        sb.append("\n       SUM(importPhaseB) AS IMPORT_PHASE_B, ");
        sb.append("\n       SUM(importPhaseC) AS IMPORT_PHASE_C, ");
        sb.append("\n       SUM(consumeEnergyTotal) AS CONSUME_ENERGY_TOTAL, ");
        sb.append("\n       SUM(consumePhaseA) AS CONSUME_PHASE_A, ");
        sb.append("\n       SUM(consumePhaseB) AS CONSUME_PHASE_B, ");
        sb.append("\n       SUM(consumePhaseC) AS CONSUME_PHASE_C ");
        sb.append("\nFROM ( ");
        sb.append("\n        SELECT me.mds_id  AS mdsId, ");
        sb.append("\n               me.id AS meterId, ");
        sb.append("\n               dt.id AS dtsId, ");
        sb.append("\n               dt.location_id AS locationId, ");
        sb.append("\n               0 AS importEnergyTotal, ");
        sb.append("\n               0 AS importPhaseA, ");
        sb.append("\n               0 AS importPhaseB, ");
        sb.append("\n               0 AS importPhaseC, ");
        sb.append("\n               0 AS consumeEnergyTotal, ");
        sb.append("\n               0 AS consumePhaseA, ");
        sb.append("\n               0 AS consumePhaseB, ");
        sb.append("\n               0 AS consumePhaseC ");
        sb.append("\n        FROM disttrfmrsubstation dt, ");
        sb.append("\n             meter me ");
        sb.append("\n        WHERE dt.id IN (:dtsIds) ");
        sb.append("\n        AND   me.disttrfmrsubstation_id = dt.id ");
        sb.append("\n        UNION ALL ");
        sb.append("\n        SELECT mdsId, ");
        sb.append("\n               meterId, ");
        sb.append("\n               dtsId, ");
        sb.append("\n               locationId, ");
        sb.append("\n               CASE WHEN SUM(importEnergyTotal1) = 0 THEN 0 ");
        sb.append("\n                    ELSE SUM(importEnergyTotal1) - SUM(importEnergyTotal2) END AS importEnergyTotal, ");
        sb.append("\n               CASE WHEN SUM(importPhaseA1) = 0 THEN 0 ");
        sb.append("\n                    ELSE SUM(importPhaseA1) - SUM(importPhaseA2) END AS importPhaseA, ");
        sb.append("\n               CASE WHEN SUM(importPhaseB1) = 0 THEN 0 ");
        sb.append("\n                    ELSE SUM(importPhaseB1) - SUM(importPhaseB2) END AS importPhaseB, ");
        sb.append("\n               CASE WHEN SUM(importPhaseC1) = 0 THEN 0 ");
        sb.append("\n                    ELSE SUM(importPhaseC1) - SUM(importPhaseC2) END AS importPhaseC, ");
        sb.append("\n               0 AS consumeEnergyTotal, ");
        sb.append("\n               0 AS consumePhaseA, ");
        sb.append("\n               0 AS consumePhaseB, ");
        sb.append("\n               0 AS consumePhaseC ");
        sb.append("\n        FROM ( ");
        sb.append("\n                SELECT me.mds_id AS mdsId, ");
        sb.append("\n                       me.id AS meterId, ");
        sb.append("\n                       dt.id AS dtsId, ");
        sb.append("\n                       dt.location_id AS locationId, ");
        sb.append("\n                       re.activeenergyimportratetotal AS importEnergyTotal1, ");
        sb.append("\n                       re.importkwhphasea AS importPhaseA1, ");
        sb.append("\n                       re.importkwhphaseb AS importPhaseB1, ");
        sb.append("\n                       re.importkwhphasec AS importPhaseC1, ");
        sb.append("\n                       0 AS importEnergyTotal2, ");
        sb.append("\n                       0 AS importPhaseA2, ");
        sb.append("\n                       0 AS importPhaseB2, ");
        sb.append("\n                       0 AS importPhaseC2 ");
        sb.append("\n                FROM disttrfmrsubstation dt, ");
        sb.append("\n                     meter me, ");
        sb.append("\n                     realtime_billing_em re ");
        sb.append("\n                WHERE dt.id IN (:dtsIds) ");
        sb.append("\n                AND   me.disttrfmrsubstation_id = dt.id ");
        sb.append("\n                AND   re.meter_id = me.id ");
//        sb.append("\n                AND   re.mdev_id = me.mds_id ");
        sb.append("\n                AND   re.mdev_type = :mdevType ");
        sb.append("\n                AND   re.yyyymmdd = (SELECT MAX(re2.yyyymmdd) ");
        sb.append("\n                                     FROM realtime_billing_em re2 ");
        sb.append("\n                                     WHERE re2.mdev_id = re.mdev_id ");
        sb.append("\n                                     AND   re2.mdev_type = re.mdev_type ");
        sb.append("\n                                     AND   re2.yyyymmdd BETWEEN :searchStartDate AND :searchEndDate) ");
        sb.append("\n                AND   re.hhmmss = (SELECT MAX(sr.hhmmss) ");
        sb.append("\n                                   FROM realtime_billing_em sr ");
        sb.append("\n                                   WHERE sr.mdev_id = re.mdev_id ");
        sb.append("\n                                   AND   sr.mdev_type = re.mdev_type ");
        sb.append("\n                                   AND   sr.yyyymmdd = re.yyyymmdd) ");
        sb.append("\n                UNION ALL ");
        sb.append("\n                SELECT me.mds_id AS mdsId, ");
        sb.append("\n                       me.id AS meterId, ");
        sb.append("\n                       dt.id AS dtsId, ");
        sb.append("\n                       dt.location_id AS locationId, ");
        sb.append("\n                       0 AS importEnergyTotal1, ");
        sb.append("\n                       0 AS importPhaseA1, ");
        sb.append("\n                       0 AS importPhaseB1, ");
        sb.append("\n                       0 AS importPhaseC1, ");
        sb.append("\n                       re.activeenergyimportratetotal AS importEnergyTotal2, ");
        sb.append("\n                       re.importkwhphasea AS importPhaseA2, ");
        sb.append("\n                       re.importkwhphaseb AS importPhaseB2, ");
        sb.append("\n                       re.importkwhphasec AS importPhaseC2 ");
        sb.append("\n                FROM disttrfmrsubstation dt, ");
        sb.append("\n                     meter me, ");
        sb.append("\n                     realtime_billing_em re ");
        sb.append("\n                WHERE dt.id IN (:dtsIds) ");
        sb.append("\n                AND   me.disttrfmrsubstation_id = dt.id ");
        sb.append("\n                AND   re.meter_id = me.id ");
//        sb.append("\n                AND   re.mdev_id = me.mds_id ");
        sb.append("\n                AND   re.mdev_type = :mdevType ");
        sb.append("\n                AND   re.yyyymmdd = (SELECT MAX(re2.yyyymmdd) ");
        sb.append("\n                                     FROM realtime_billing_em re2 ");
        sb.append("\n                                     WHERE re2.mdev_id = re.mdev_id ");
        sb.append("\n                                     AND   re2.mdev_type = re.mdev_type ");
        sb.append("\n                                     AND   re2.yyyymmdd >= :searchPreStartDate ");
        sb.append("\n                                     AND   re2.yyyymmdd < :searchStartDate) ");
        sb.append("\n                AND   re.hhmmss = (SELECT MAX(sr.hhmmss) ");
        sb.append("\n                                   FROM realtime_billing_em sr ");
        sb.append("\n                                   WHERE sr.mdev_id = re.mdev_id ");
        sb.append("\n                                   AND   sr.mdev_type = re.mdev_type ");
        sb.append("\n                                   AND   sr.yyyymmdd = re.yyyymmdd) ");
        sb.append("\n            ) x ");
        sb.append("\n        GROUP BY locationId, mdsId, meterId, dtsId ");
        sb.append("\n        UNION ALL ");
        sb.append("\n        SELECT mdsId, ");
        sb.append("\n               meterId, ");
        sb.append("\n               dtsId, ");
        sb.append("\n               locationId, ");
        sb.append("\n               0 AS importEnergyTotal, ");
        sb.append("\n               0 AS importPhaseA, ");
        sb.append("\n               0 AS importPhaseB, ");
        sb.append("\n               0 AS importPhaseC, ");
        sb.append("\n               SUM(consumeEnergyTotal) AS consumeEnergyTotal, ");
        sb.append("\n               SUM(consumePhaseA) AS consumePhaseA, ");
        sb.append("\n               SUM(consumePhaseB) AS consumePhaseB, ");
        sb.append("\n               SUM(consumePhaseC) AS consumePhaseC ");
        sb.append("\n        FROM ( ");
        sb.append("\n                SELECT mdsId, ");
        sb.append("\n                       meterId, ");
        sb.append("\n                       contMeterId, ");
        sb.append("\n                       dtsId, ");
        sb.append("\n                       locationId, ");
        sb.append("\n                       CASE WHEN SUM(consumeEnergyTotal1) = 0 THEN 0 ");
        sb.append("\n                            ELSE SUM(consumeEnergyTotal1) - SUM(consumeEnergyTotal2) END AS consumeEnergyTotal, ");
        sb.append("\n                       CASE WHEN SUM(consumePhaseA1) = 0 THEN 0 ");
        sb.append("\n                            ELSE SUM(consumePhaseA1) - SUM(consumePhaseA2) END AS consumePhaseA, ");
        sb.append("\n                       CASE WHEN SUM(consumePhaseB1) = 0 THEN 0 ");
        sb.append("\n                            ELSE SUM(consumePhaseB1) - SUM(consumePhaseB2) END AS consumePhaseB, ");
        sb.append("\n                       CASE WHEN SUM(consumePhaseC1) = 0 THEN 0 ");
        sb.append("\n                            ELSE SUM(consumePhaseC1) - SUM(consumePhaseC2) END AS consumePhaseC ");
        sb.append("\n                FROM ( ");
        sb.append("\n                        SELECT me.mds_id AS mdsId, ");
        sb.append("\n                               me.id AS meterId, ");
        sb.append("\n                               mt.id AS contMeterId, ");
        sb.append("\n                               dt.id AS dtsId, ");
        sb.append("\n                               dt.location_id AS locationId, ");
        sb.append("\n                               be.activeenergyratetot AS consumeEnergyTotal1, ");
        sb.append("\n                               CASE WHEN mt.disttrfmrsubstationmeter_a_id IS NOT NULL THEN be.activeenergyratetot ELSE 0 END AS consumePhaseA1, ");
        sb.append("\n                               CASE WHEN mt.disttrfmrsubstationmeter_b_id IS NOT NULL THEN be.activeenergyratetot ELSE 0 END AS consumePhaseB1, ");
        sb.append("\n                               CASE WHEN mt.disttrfmrsubstationmeter_c_id IS NOT NULL THEN be.activeenergyratetot ELSE 0 END AS consumePhaseC1, ");
        sb.append("\n                               0 AS consumeEnergyTotal2, ");
        sb.append("\n                               0 AS consumePhaseA2, ");
        sb.append("\n                               0 AS consumePhaseB2, ");
        sb.append("\n                               0 AS consumePhaseC2 ");
        sb.append("\n                        FROM disttrfmrsubstation dt, ");
        sb.append("\n                             meter me, ");
        sb.append("\n                             meter mt, ");
//        sb.append("\n                             meter mt ");
//        sb.append("\n                             LEFT OUTER JOIN ");
//        sb.append("\n                             contract ct ");
//        sb.append("\n                             ON ct.meter_id = mt.id, ");
        sb.append("\n                             billing_day_em be ");
        sb.append("\n                        WHERE dt.id IN (:dtsIds) ");
        sb.append("\n                        AND   me.disttrfmrsubstation_id = dt.id ");
        sb.append("\n                        AND   (mt.disttrfmrsubstationmeter_a_id = me.id ");
        sb.append("\n                            OR mt.disttrfmrsubstationmeter_b_id = me.id ");
        sb.append("\n                            OR mt.disttrfmrsubstationmeter_c_id = me.id) ");
//        sb.append("\n                        AND   be.meter_id = mt.id ");
        sb.append("\n                        AND   be.mdev_id = mt.mds_id ");
        sb.append("\n                        AND   be.mdev_type = :mdevType ");
//        sb.append("\n                        AND   be.yyyymmdd BETWEEN :searchStartDate AND :searchEndDate ");
        sb.append("\n                        AND   be.yyyymmdd = (SELECT MAX(be2.yyyymmdd) ");
        sb.append("\n                                             FROM billing_day_em be2 ");
        sb.append("\n                                             WHERE be2.mdev_id = be.mdev_id ");
        sb.append("\n                                             AND   be2.mdev_type = be.mdev_type ");
        sb.append("\n                                             AND   be2.yyyymmdd BETWEEN :searchStartDate AND :searchEndDate) ");
        sb.append("\n                        UNION ALL ");
        sb.append("\n                        SELECT me.mds_id AS mdsId, ");
        sb.append("\n                               me.id AS meterId, ");
        sb.append("\n                               mt.id AS contMeterId, ");
        sb.append("\n                               dt.id AS dtsId, ");
        sb.append("\n                               dt.location_id AS locationId, ");
        sb.append("\n                               0 AS consumeEnergyTotal1, ");
        sb.append("\n                               0 AS consumePhaseA1, ");
        sb.append("\n                               0 AS consumePhaseB1, ");
        sb.append("\n                               0 AS consumePhaseC1, ");
        sb.append("\n                               be.activeenergyratetot AS consumeEnergyTotal2, ");
        sb.append("\n                               CASE WHEN mt.disttrfmrsubstationmeter_a_id IS NOT NULL THEN be.activeenergyratetot ELSE 0 END AS consumePhaseA2, ");
        sb.append("\n                               CASE WHEN mt.disttrfmrsubstationmeter_b_id IS NOT NULL THEN be.activeenergyratetot ELSE 0 END AS consumePhaseB2, ");
        sb.append("\n                               CASE WHEN mt.disttrfmrsubstationmeter_c_id IS NOT NULL THEN be.activeenergyratetot ELSE 0 END AS consumePhaseC2 ");
        sb.append("\n                        FROM disttrfmrsubstation dt, ");
        sb.append("\n                             meter me, ");
        sb.append("\n                             meter mt, ");
//        sb.append("\n                             meter mt ");
//        sb.append("\n                             left outer join ");
//        sb.append("\n                             contract ct ");
//        sb.append("\n                             on ct.meter_id = mt.id, ");
        sb.append("\n                             billing_day_em be ");
        sb.append("\n                        WHERE dt.id IN (:dtsIds) ");
        sb.append("\n                        AND   me.disttrfmrsubstation_id = dt.id ");
        sb.append("\n                        AND   (mt.disttrfmrsubstationmeter_a_id = me.id ");
        sb.append("\n                            OR mt.disttrfmrsubstationmeter_b_id = me.id ");
        sb.append("\n                            OR mt.disttrfmrsubstationmeter_c_id = me.id) ");
//        sb.append("\n                        AND   be.meter_id = mt.id ");
        sb.append("\n                        AND   be.mdev_id = mt.mds_id ");
        sb.append("\n                        AND   be.mdev_type = :mdevType ");
//        sb.append("\n                        AND   be.yyyymmdd >= :searchPreStartDate ");
//        sb.append("\n                        AND   be.yyyymmdd < :searchStartDate ");
        sb.append("\n                        AND   be.yyyymmdd = (SELECT MAX(be2.yyyymmdd) ");
        sb.append("\n                                             FROM billing_day_em be2 ");
        sb.append("\n                                             WHERE be2.mdev_id = be.mdev_id ");
        sb.append("\n                                             AND   be2.mdev_type = be.mdev_type ");
        sb.append("\n                                             AND   be2.yyyymmdd >= :searchPreStartDate ");
        sb.append("\n                                             AND   be2.yyyymmdd < :searchStartDate) ");
        sb.append("\n                    ) v ");
        sb.append("\n                GROUP BY locationId, mdsId, meterId, contMeterId, dtsId ");
        sb.append("\n            ) w ");
        sb.append("\n        GROUP BY locationId, mdsId, meterId, dtsId ");
        sb.append("\n    ) x ");
        sb.append("\nGROUP BY locationId, dtsId, mdsId, meterId ");

        SQLQuery query = getSession().createSQLQuery(new SQLWrapper().getQuery(sb.toString()));
        query.setString("searchPreStartDate", searchPreStartDate);
        query.setString("searchStartDate", searchStartDate);
        query.setString("searchEndDate", searchEndDate);
        query.setInteger("mdevType", DeviceType.Meter.getCode());
        query.setParameterList("dtsIds", dtsIds);

        result = query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();

        return result;
    }

    /**
     * method name : getEbsDtsTreeContractMeterNodeData<b/>
     * method Desc : Energy Balance Monitoring 맥스가젯의 DTS Tree 의 Contract Meter Node Data 를 조회한다.
     *
     * @param conditionMap
     * @return
     */
    @Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
    public List<Map<String, Object>> getEbsDtsTreeContractMeterNodeData(Map<String, Object> conditionMap) {
        List<Map<String, Object>> result;
//        Integer supplierId = (Integer)conditionMap.get("supplierId");
        Integer meterId = (Integer)conditionMap.get("meterId");
        Integer phaseId = (Integer)conditionMap.get("phaseId");
        String searchPreStartDate = StringUtil.nullToBlank(conditionMap.get("searchPreStartDate"));
        String searchStartDate = StringUtil.nullToBlank(conditionMap.get("searchStartDate"));
        String searchEndDate = StringUtil.nullToBlank(conditionMap.get("searchEndDate"));
        String dtsMeterColumnName = null;
//        List<Integer> dtsIdList = (List<Integer>)conditionMap.get("dtsIdList");
        boolean hasEnum = false;

        StringBuilder sb = new StringBuilder();

        for (DistTrfmrSubstationMeterPhase constant : DistTrfmrSubstationMeterPhase.values()) {
            if (constant.getCode() == phaseId) {
                switch(constant) {
                    case LINE_A:
                        dtsMeterColumnName = "mt.disttrfmrsubstationmeter_a_id";
                        hasEnum = true;
                        break;

                    case LINE_B:
                        dtsMeterColumnName = "mt.disttrfmrsubstationmeter_b_id";
                        hasEnum = true;
                        break;

                    case LINE_C:
                        dtsMeterColumnName = "mt.disttrfmrsubstationmeter_c_id";
                        hasEnum = true;
                        break;
                }
                break;
            }
        }

        if (!hasEnum) {
            dtsMeterColumnName = "0";      // 정확하지 않은 code 가 들어올 경우 조회안함.
        }

        sb.append("\nSELECT mdsId AS MDS_ID, ");
        sb.append("\n       contractNumber AS CONTRACT_NUMBER, ");
        sb.append("\n       contMeterId AS CONT_METER_ID, ");
        sb.append("\n       SUM(consumeEnergyTotal) AS CONSUME_ENERGY_TOTAL ");
        sb.append("\nFROM ( ");
        sb.append("\n        SELECT mt.mds_id AS mdsId, ");
        sb.append("\n               ct.contract_number AS contractNumber, ");
        sb.append("\n               mt.id AS contMeterId, ");
        sb.append("\n               0 AS consumeEnergyTotal ");
        sb.append("\n        FROM meter mt ");
        sb.append("\n             LEFT OUTER JOIN ");
        sb.append("\n             contract ct ");
        sb.append("\n             ON ct.meter_id = mt.id ");
        sb.append("\n        WHERE ").append(dtsMeterColumnName).append(" = :meterId ");  // ex.disttrfmrsubstationmeter_a_id
        sb.append("\n        UNION ALL ");
        sb.append("\n        SELECT mdsId, ");
        sb.append("\n               contractNumber, ");
        sb.append("\n               contMeterId, ");
        sb.append("\n               CASE WHEN SUM(consumeEnergyTotal1) = 0 THEN 0 ");
        sb.append("\n                    ELSE SUM(consumeEnergyTotal1) - SUM(consumeEnergyTotal2) END AS consumeEnergyTotal ");
        sb.append("\n        FROM ( ");
        sb.append("\n                SELECT mt.mds_id AS mdsId, ");
        sb.append("\n                       ct.contract_number AS contractNumber, ");
        sb.append("\n                       mt.id AS contMeterId, ");
        sb.append("\n                       be.activeenergyratetot AS consumeEnergyTotal1, ");
        sb.append("\n                       0 AS consumeEnergyTotal2 ");
        sb.append("\n                FROM meter mt ");
        sb.append("\n                     LEFT OUTER JOIN ");
        sb.append("\n                     contract ct ");
        sb.append("\n                     ON ct.meter_id = mt.id, ");
        sb.append("\n                     billing_day_em be ");
        sb.append("\n                WHERE ").append(dtsMeterColumnName).append(" = :meterId ");
//        sb.append("\n                AND   be.meter_id = mt.id ");
        sb.append("\n                AND   be.mdev_id = mt.mds_id ");
        sb.append("\n                AND   be.mdev_type = :mdevType ");
        sb.append("\n                AND   be.yyyymmdd = (SELECT MAX(be2.yyyymmdd) ");
        sb.append("\n                                     FROM billing_day_em be2 ");
        sb.append("\n                                     WHERE be2.mdev_id = be.mdev_id ");
        sb.append("\n                                     AND   be2.mdev_type = be.mdev_type ");
        sb.append("\n                                     AND   be2.yyyymmdd BETWEEN :searchStartDate AND :searchEndDate) ");
        sb.append("\n                UNION ALL ");
        sb.append("\n                SELECT mt.mds_id AS mdsId, ");
        sb.append("\n                       ct.contract_number AS contractNumber, ");
        sb.append("\n                       mt.id AS contMeterId, ");
        sb.append("\n                       0 AS consumeEnergyTotal1, ");
        sb.append("\n                       be.activeenergyratetot AS consumeEnergyTotal2 ");
        sb.append("\n                FROM meter mt ");
        sb.append("\n                     LEFT OUTER JOIN ");
        sb.append("\n                     contract ct ");
        sb.append("\n                     ON ct.meter_id = mt.id, ");
        sb.append("\n                     billing_day_em be ");
        sb.append("\n                WHERE ").append(dtsMeterColumnName).append(" = :meterId ");
//        sb.append("\n                AND   be.meter_id = mt.id ");
        sb.append("\n                AND   be.mdev_id = mt.mds_id ");
        sb.append("\n                AND   be.mdev_type = :mdevType ");
        sb.append("\n                AND   be.yyyymmdd = (SELECT MAX(be2.yyyymmdd) ");
        sb.append("\n                                     FROM billing_day_em be2 ");
        sb.append("\n                                     WHERE be2.mdev_id = be.mdev_id ");
        sb.append("\n                                     AND   be2.mdev_type = be.mdev_type ");
        sb.append("\n                                     AND   be2.yyyymmdd >= :searchPreStartDate ");
        sb.append("\n                                     AND   be2.yyyymmdd < :searchStartDate) ");
        sb.append("\n            ) v ");
        sb.append("\n        GROUP BY mdsId, contractNumber, contMeterId ");
        sb.append("\n    ) x ");
        sb.append("\nGROUP BY mdsId, contractNumber, contMeterId ");

        Query query = getSession().createSQLQuery(new SQLWrapper().getQuery(sb.toString()));

        query.setInteger("meterId", meterId);
        query.setString("searchPreStartDate", searchPreStartDate);
        query.setString("searchStartDate", searchStartDate);
        query.setString("searchEndDate", searchEndDate);
        query.setInteger("mdevType", DeviceType.Meter.getCode());
//        query.setString("serviceType", MeterType.EnergyMeter.getServiceType());

        result = query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();

        logger.info("getEbsDtsTreeContractMeterNodeData ========== " + result);
        return result;
    }

    /**
     * method name : getEbsMeterList<b/>
     * method Desc : Energy Balance Monitoring 맥스가젯의 Meter List 를 조회한다.
     *
     * @param conditionMap
     * @param isTotal
     * @return
     */
    @Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
    public List<Map<String, Object>> getEbsMeterList(Map<String, Object> conditionMap, boolean isTotal) {
        List<Map<String, Object>> result;
        Integer supplierId = (Integer)conditionMap.get("supplierId");
        Integer meterGroupId = (Integer)conditionMap.get("meterGroupId");
        Integer deviceVendorId = (Integer)conditionMap.get("deviceVendorId");
        Integer deviceModelId = (Integer)conditionMap.get("deviceModelId");
        String mdsId = StringUtil.nullToBlank(conditionMap.get("mdsId"));
        String installStartDate = StringUtil.nullToBlank(conditionMap.get("installStartDate"));
        String installEndDate = StringUtil.nullToBlank(conditionMap.get("installEndDate"));
        List<Integer> locationIdList = (List<Integer>)conditionMap.get("locationIdList");

        Integer page = (Integer)conditionMap.get("page");
        Integer limit = (Integer)conditionMap.get("limit");

        StringBuilder sb = new StringBuilder();

        sb.append("\nSELECT mt.id AS meterId, ");
        sb.append("\n       mt.mdsId AS mdsId, ");
        sb.append("\n       lo.name AS location, ");
        sb.append("\n       mt.model.name AS model, ");
        sb.append("\n       mt.installDate AS installDate, ");
        sb.append("\n       mt.meterStatus.descr AS meterStatus ");
        sb.append("\nFROM Meter mt ");
        sb.append("\n     LEFT OUTER JOIN ");
        sb.append("\n     mt.location lo ");
        sb.append("\nWHERE mt.supplier.id = :supplierId ");
        // TODO DTS 에 포함 가능한 미터 조건
        sb.append("\nAND   mt.model.name = 'A1140' ");              // 고압미터
        sb.append("\nAND   mt.meterStatus.code IN ('1.3.3.1', '1.3.3.8') ");      // 미터상태:Normal or New Registered
        sb.append("\nAND   mt.distTrfmrSubstation IS NULL ");

        if (!mdsId.isEmpty()) {
            sb.append("\nAND   mt.mdsId LIKE :mdsId ");
        }

        if (locationIdList != null && locationIdList.size() > 0) {
            sb.append("\nAND   lo.id IN (:locationIdList) ");
        }

        if (meterGroupId != null) {
            sb.append("\nAND   mt.mdsId IN (SELECT gm.member ");
            sb.append("\n                   FROM GroupMember gm, ");
            sb.append("\n                        AimirGroup ag ");
            sb.append("\n                   WHERE ag.groupType = :groupType ");
            sb.append("\n                   AND   ag.id = :meterGroupId) ");
        }

        if (!installStartDate.isEmpty()) {
            sb.append("\nAND   mt.installDate >= :installStartDate ");
        }

        if (!installEndDate.isEmpty()) {
            sb.append("\nAND   mt.installDate <= :installEndDate ");
        }

        if (deviceVendorId != null) {
            if (deviceModelId != null) {
                sb.append("\nAND   mt.model.id = :deviceModelId ");
            } else {
                sb.append("\nAND   mt.model.id IN (SELECT dm.id ");
                sb.append("\n                      FROM DeviceModel dm, ");
                sb.append("\n                           DeviceVendor dv ");
                sb.append("\n                      WHERE dv.id = :deviceVendorId) ");
            }
        }

        if (!isTotal) {
            sb.append("\nORDER BY mt.mdsId ");
        }

        Query query = getSession().createQuery(sb.toString());
        query.setInteger("supplierId", supplierId);

        if (!mdsId.isEmpty()) {
            query.setString("mdsId", "%" + mdsId + "%");
        }

        if (locationIdList != null && locationIdList.size() > 0) {
            query.setParameterList("locationIdList", locationIdList);
        }

        if (meterGroupId != null) {
            query.setString("groupType", GroupType.Meter.name());
            query.setInteger("meterGroupId", meterGroupId);
        }

        if (!installStartDate.isEmpty()) {
            query.setString("installStartDate", installStartDate);
        }

        if (!installEndDate.isEmpty()) {
            query.setString("installEndDate", installEndDate);
        }

        if (deviceVendorId != null) {
            if (deviceModelId != null) {
                query.setInteger("deviceModelId", deviceModelId);
            } else {
                query.setInteger("deviceVendorId", deviceVendorId);
            }
        }

        if (isTotal) {
            Map<String, Object> map = new HashMap<String, Object>();
            int count = 0;

            Iterator<?> itr = query.iterate();
            while(itr.hasNext()) {
                itr.next();
                count++;
            }
            map.put("total", count);
            result = new ArrayList<Map<String, Object>>();
            result.add(map);
        } else {
            query.setFirstResult((page - 1) * limit);
            query.setMaxResults(limit);
            result = query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
        }

        return result;
    }

    /**
     * Criteria 기반 미터리스트 얻기
     */
    @Override
    @Transactional(readOnly=true, propagation=Propagation.REQUIRED)
    public List<Meter> getMeterList(Map<String, Object> condition) {
        Criteria criteria = getSession().createCriteria(Meter.class);
        String meterTypeCode = StringUtil.nullToBlank(condition.get("meterTypeCode"));
        String mdsId = StringUtil.nullToBlank(condition.get("mdsId"));
        String supplierId = StringUtil.nullToZero(condition.get("supplierId"));
        String isManualMeter = StringUtil.nullToZero(condition.get("isManualMeter"));
        Integer meterStatus = (Integer)condition.get("meterStatus");

//        Integer start = 0;
//        Integer count = 10;
//        try {
//            start = (Integer) condition.get("start");
//            count = (Integer) condition.get("count");
//        }
//        catch(Exception ignore) { }

        if(!meterTypeCode.isEmpty()) {
            criteria.add(Restrictions.eq("metertype_id", meterTypeCode));
        }
        
        if(!mdsId.isEmpty()) {
            criteria.add(Restrictions.eq("mdsId", mdsId));
        }
        if(!isManualMeter.equals("0")) {
            criteria.add(Restrictions.eq("isManualMeter", Integer.parseInt(isManualMeter)));
        }
        if(!supplierId.equals("0")) {
            criteria.add(Restrictions.eq("supplier.id", Integer.parseInt(supplierId)));
        }

        criteria.add(Restrictions.or(Restrictions.ne("meterStatus.id", meterStatus),
        		Restrictions.isNull("meterStatus.id")));
        
        criteria.addOrder(Order.asc("mdsId").ignoreCase());

        return (List<Meter>) criteria.list();
    }

    /**
     * method name : getEbsContractMeterList<b/>
     * method Desc : Energy Balance Monitoring 맥스가젯의 Contract Meter List 를 조회한다.
     *
     * @param conditionMap
     * @param isTotal
     * @return
     */
    @Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
    public List<Map<String, Object>> getEbsContractMeterList(Map<String, Object> conditionMap, boolean isTotal) {
        List<Map<String, Object>> result;
        Map<String, Object> map;
        String customerId = StringUtil.nullToBlank(conditionMap.get("customerId"));
        String customerName = StringUtil.nullToBlank(conditionMap.get("customerName"));
        String contractNumber = StringUtil.nullToBlank(conditionMap.get("contractNumber"));
        String mdsId = StringUtil.nullToBlank(conditionMap.get("mdsId"));

        List<Integer> locationIdList = (List<Integer>)conditionMap.get("locationIdList");

        Integer supplierId = (Integer)conditionMap.get("supplierId");
        Integer contractGroupId = (Integer)conditionMap.get("contractGroupId");
        Integer page = (Integer)conditionMap.get("page");
        Integer limit = (Integer)conditionMap.get("limit");

        StringBuilder sb = new StringBuilder();

        if (isTotal) {
            sb.append("\nSELECT COUNT(*) AS cnt FROM ( ");
        }

        sb.append("\nSELECT mt.id AS CONT_METER_ID, ");
        sb.append("\n       mt.mds_id AS MDS_ID, ");
        sb.append("\n       ct.contract_number AS CONTRACT_NUMBER, ");
        sb.append("\n       cu.customerno AS CUSTOMER_NO, ");
        sb.append("\n       cu.name AS CUSTOMER_NAME, ");
        sb.append("\n       lo.name AS LOCATION, ");
        sb.append("\n       ta.name AS TARIFF_TYPE ");
        sb.append("\nFROM meter mt ");
        sb.append("\n     LEFT OUTER JOIN ");
        sb.append("\n     contract ct ");
        sb.append("\n     ON ct.meter_id = mt.id ");
        sb.append("\n     LEFT OUTER JOIN ");
        sb.append("\n     customer cu ");
        sb.append("\n     ON cu.id = ct.customer_id ");
        sb.append("\n     LEFT OUTER JOIN ");
        sb.append("\n     location lo ");
        sb.append("\n     ON lo.id = ct.location_id ");
        sb.append("\n     LEFT OUTER JOIN ");
        sb.append("\n     tarifftype ta ");
        sb.append("\n     ON ta.id = ct.tariffindex_id, ");
        sb.append("\n     code cd, ");
        sb.append("\n     devicemodel dm ");
        sb.append("\nWHERE mt.supplier_id = :supplierId ");
        sb.append("\nAND   mt.disttrfmrsubstation_id IS NULL ");
        sb.append("\nAND   mt.disttrfmrsubstationmeter_a_id IS NULL ");
        sb.append("\nAND   mt.disttrfmrsubstationmeter_b_id IS NULL ");
        sb.append("\nAND   mt.disttrfmrsubstationmeter_c_id IS NULL ");
        sb.append("\nAND   cd.id = mt.metertype_id ");
        sb.append("\nAND   cd.code = '1.3.1.1' ");
        sb.append("\nAND   dm.id = mt.devicemodel_id ");
        sb.append("\nAND   dm.name != 'A1140' ");        // 고압미터

        if (!customerId.isEmpty()) {
            sb.append("\nAND   cu.customerno LIKE :customerId ");
        }

        if (!customerName.isEmpty()) {
            sb.append("\nAND   cu.name LIKE :customerName ");
        }

        if (!contractNumber.isEmpty()) {
            sb.append("\nAND   ct.contract_number LIKE :contractNumber ");
        }

        if (contractGroupId != null) {
            sb.append("\nAND   ct.contract_number IN (SELECT gm.member ");
            sb.append("\n                             FROM group_member gm, ");
            sb.append("\n                                  aimirgroup ag ");
            sb.append("\n                             WHERE ag.id = :contractGroupId ");
            sb.append("\n                             AND   ag.group_type = :groupType ");
            sb.append("\n                             AND   gm.group_id = ag.id) ");
        }

        if (locationIdList != null && locationIdList.size() > 0) {
            sb.append("\nAND   ct.location_id IN (:locationIdList) ");
        }

        if (!mdsId.isEmpty()) {
            sb.append("\nAND   mt.mds_id LIKE :mdsId ");
        }

        if (!isTotal) {
            sb.append("\nORDER BY mt.mds_id, cu.name ");
        } else {
            sb.append("\n) x ");
        }

        SQLQuery query = getSession().createSQLQuery(new SQLWrapper().getQuery(sb.toString()));

        query.setInteger("supplierId", supplierId);
//        query.setString("meterType", MeterType.EnergyMeter.name());

        if (!customerId.isEmpty()) {
            query.setString("customerId", "%" + customerId + "%");
        }

        if (!customerName.isEmpty()) {
            query.setString("customerName", "%" + customerName + "%");
        }

        if (!contractNumber.isEmpty()) {
            query.setString("contractNumber", "%" + contractNumber + "%");
        }

        if (contractGroupId != null) {
            query.setString("groupType", GroupType.Contract.name());
            query.setInteger("contractGroupId", contractGroupId);
        }

        if (locationIdList != null && locationIdList.size() > 0) {
            query.setParameterList("locationIdList", locationIdList);
        }

        if (!mdsId.isEmpty()) {
            query.setString("mdsId", "%" + mdsId + "%");
        }

        if (isTotal) {
            map = new HashMap<String, Object>();
            Number count = (Number)query.uniqueResult();
            map.put("total", count.intValue());
            result = new ArrayList<Map<String, Object>>();
            result.add(map);
        } else {
            query.setFirstResult((page - 1) * limit);
            query.setMaxResults(limit);
            result = query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
        }

        return result;
    }

    /**
     * method name : getDeleteEbsContractMeterNodeListByMeter<b/>
     * method Desc : Energy Balance Monitoring 맥스가젯의 삭제하는 Meter Node 에 포함되어있는 Contract Meter List 를 조회한다.
     *
     * @param conditionMap
     * @return
     */
    @Transactional(readOnly=true, propagation=Propagation.REQUIRED)
    public List<Meter> getDeleteEbsContractMeterNodeListByMeter(Map<String, Object> conditionMap) {
        Integer supplierId = (Integer)conditionMap.get("supplierId");
        Integer meterId = (Integer)conditionMap.get("meterId");

        StringBuilder sb = new StringBuilder();

        sb.append("\nFROM Meter m ");
        sb.append("\nWHERE m.supplier.id = :supplierId ");
        sb.append("\nAND   (m.distTrfmrSubstationMeter_A.id = :meterId ");
        sb.append("\n    OR m.distTrfmrSubstationMeter_B.id = :meterId ");
        sb.append("\n    OR m.distTrfmrSubstationMeter_C.id = :meterId) ");

        Query query = getSession().createQuery(sb.toString());

        query.setInteger("supplierId", supplierId);
        query.setInteger("meterId", meterId);

        return query.list();
    }

    /**
     * method name : getEbsDtsChartConsumeData<b/>
     * method Desc : Energy Balance Monitoring 맥스가젯의 DTS Tree Chart 의 당월/전월/전년도 Consume Energy Data 를 조회한다.
     *
     * @param conditionMap
     * @return
     */
    @Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
    public List<Map<String, Object>> getEbsDtsChartConsumeData(Map<String, Object> conditionMap) {
        List<Map<String, Object>> result;
        Integer contMeterId = (Integer)conditionMap.get("contMeterId");
        Integer depth = (Integer)conditionMap.get("depth");
        String searchPreStartDate = StringUtil.nullToBlank(conditionMap.get("searchPreStartDate"));
        String searchEndDate = StringUtil.nullToBlank(conditionMap.get("searchEndDate"));
        List<Integer> contMeterIdList = (List<Integer>)conditionMap.get("contMeterIdList");

        StringBuilder sb = new StringBuilder();

//        sb.append("\nSELECT ct.id AS CONTRACT_ID, ");
//        sb.append("\n       be.yyyymmdd AS YYYYMMDD, ");
//        sb.append("\n       COALESCE(be.activeenergyratetot, 0) AS ENERGY_SUM ");
//        sb.append("\nFROM contract ct, ");
//        sb.append("\n     billing_day_em be ");
//        sb.append("\nWHERE 1=1 ");
//        if (depth == 5) {
//            sb.append("\nAND   ct.id = :contractId ");
//        } else {
//            sb.append("\nAND   ct.id IN (:contractIdList) ");
//        }
//        sb.append("\nAND   be.mdev_type = :mdevType ");
//        sb.append("\nAND   be.contract_id = ct.id ");
//        sb.append("\nAND   be.meter_id = ct.meter_id ");
//        sb.append("\nAND   be.yyyymmdd BETWEEN :searchPreStartDate AND :searchEndDate ");
//        sb.append("\nORDER BY be.yyyymmdd ");
//
//        Query query = getSession().createSQLQuery(new SQLWrapper().getQuery(sb.toString()));
//
//        query.setString("searchPreStartDate", searchPreStartDate);
//        query.setString("searchEndDate", searchEndDate);
//        query.setInteger("mdevType", DeviceType.Meter.getCode());
//
//        if (depth == 5) {
//            query.setInteger("contractId", contractId);
//        } else {
//            query.setParameterList("contractIdList", contractIdList);
//        }

        sb.append("\nSELECT mt.id AS CONT_METER_ID, ");
        sb.append("\n       be.yyyymmdd AS YYYYMMDD, ");
        sb.append("\n       COALESCE(be.activeenergyratetot, 0) AS ENERGY_SUM ");
        sb.append("\nFROM meter mt, ");
        sb.append("\n     billing_day_em be ");
        sb.append("\nWHERE 1=1 ");
        if (depth == 5) {
            sb.append("\nAND   mt.id = :contMeterId ");
        } else {
            sb.append("\nAND   mt.id IN (:contMeterIdList) ");
        }
//        sb.append("\nAND   be.contract_id = ct.id ");
//        sb.append("\nAND   be.meter_id = mt.id ");
        sb.append("\nAND   be.mdev_id = mt.mds_id ");
        sb.append("\nAND   be.mdev_type = :mdevType ");
        sb.append("\nAND   be.yyyymmdd BETWEEN :searchPreStartDate AND :searchEndDate ");
        sb.append("\nORDER BY be.yyyymmdd ");

        Query query = getSession().createSQLQuery(new SQLWrapper().getQuery(sb.toString()));

        query.setString("searchPreStartDate", searchPreStartDate);
        query.setString("searchEndDate", searchEndDate);
        query.setInteger("mdevType", DeviceType.Meter.getCode());

        if (depth == 5) {
            query.setInteger("contMeterId", contMeterId);
        } else {
            query.setParameterList("contMeterIdList", contMeterIdList);
        }

        result = query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();

        return result;
    }

    /**
     * method name : getEbsDtsChartContractMeterIds<b/>
     * method Desc : Energy Balance Monitoring 맥스가젯의 DTS Tree Chart 의 조건에 해당하는 Contract Meter ID 리스트를 조회한다.
     *
     * @param conditionMap
     * @return
     */
    @Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
    public List<Map<String, Object>> getEbsDtsChartContractMeterIds(Map<String, Object> conditionMap) {
        List<Map<String, Object>> result;
        Integer supplierId = (Integer)conditionMap.get("supplierId");
        Integer dtsId = (Integer)conditionMap.get("dtsId");
        Integer meterId = (Integer)conditionMap.get("meterId");
        Integer phaseId = (Integer)conditionMap.get("phaseId");
        Integer depth = (Integer)conditionMap.get("depth");

        StringBuilder sb = new StringBuilder();

//        sb.append("\nSELECT ct.id AS CONTRACT_ID ");
//        sb.append("\nFROM contract ct, ");
//        sb.append("\n     meter me, ");
//        sb.append("\n     code cd ");
//        sb.append("\nWHERE 1=1 ");
//
//        switch(depth) {
//            case 2: // DTS
//                sb.append("\nAND   me.disttrfmrsubstation_id = :dtsId ");
//                sb.append("\nAND   me.supplier_id = :supplierId ");
//                sb.append("\nAND   ct.supplier_id = me.supplier_id ");
//                break;
//
//            case 3: // Meter
//
//            case 4: // phase
//                sb.append("\nAND   me.id = :meterId ");
//                sb.append("\nAND   ct.supplier_id = me.supplier_id ");
//                break;
//        }
//
//        switch(depth) {
//            case 2: // DTS
//
//            case 3: // Meter
//                sb.append("\nAND   (ct.disttrfmrsubstationmeter_a_id = me.id OR ct.disttrfmrsubstationmeter_b_id = me.id OR ct.disttrfmrsubstationmeter_c_id = me.id) ");
//                break;
//
//            case 4: // phase
//                for (DistTrfmrSubstationMeterPhase obj : DistTrfmrSubstationMeterPhase.values()) {
//                    if (obj.getCode().equals(phaseId)) {
//                        switch(obj) {
//                            case LINE_A:
//                                sb.append("\nAND   ct.disttrfmrsubstationmeter_a_id = me.id ");
//                                break;
//
//                            case LINE_B:
//                                sb.append("\nAND   ct.disttrfmrsubstationmeter_b_id = me.id ");
//                                break;
//
//                            case LINE_C:
//                                sb.append("\nAND   ct.disttrfmrsubstationmeter_c_id = me.id ");
//                                break;
//                        }
//                    }
//                }
//                break;
//        }
//
//        sb.append("\nAND   ct.servicetype_id = cd.id ");
//        sb.append("\nAND   cd.code = :serviceType ");

        sb.append("\nSELECT mt.id AS CONT_METER_ID ");
        sb.append("\nFROM meter me, ");
        sb.append("\n     meter mt, ");
        sb.append("\n     code cd ");
        sb.append("\nWHERE 1=1 ");

        switch(depth) {
            case 2: // DTS
                sb.append("\nAND   me.disttrfmrsubstation_id = :dtsId ");
                sb.append("\nAND   me.supplier_id = :supplierId ");
//                sb.append("\nAND   ct.supplier_id = me.supplier_id ");
                break;

            case 3: // Meter

            case 4: // phase
                sb.append("\nAND   me.id = :meterId ");
//                sb.append("\nAND   ct.supplier_id = me.supplier_id ");
                break;
        }

        switch(depth) {
            case 2: // DTS

            case 3: // Meter
                sb.append("\nAND   (mt.disttrfmrsubstationmeter_a_id = me.id ");
                sb.append("\n    OR mt.disttrfmrsubstationmeter_b_id = me.id ");
                sb.append("\n    OR mt.disttrfmrsubstationmeter_c_id = me.id) ");
                break;

            case 4: // phase
                for (DistTrfmrSubstationMeterPhase obj : DistTrfmrSubstationMeterPhase.values()) {
                    if (obj.getCode().equals(phaseId)) {
                        switch(obj) {
                            case LINE_A:
                                sb.append("\nAND   mt.disttrfmrsubstationmeter_a_id = me.id ");
                                break;
                            case LINE_B:
                                sb.append("\nAND   mt.disttrfmrsubstationmeter_b_id = me.id ");
                                break;
                            case LINE_C:
                                sb.append("\nAND   mt.disttrfmrsubstationmeter_c_id = me.id ");
                                break;
                        }
                        break;
                    }
                }
                break;
        }

        sb.append("\nAND   mt.metertype_id = cd.id ");
        sb.append("\nAND   cd.code = '1.3.1.1' ");

        Query query = getSession().createSQLQuery(new SQLWrapper().getQuery(sb.toString()));
//        query.setString("serviceType", MeterType.EnergyMeter.getServiceType());

        switch(depth) {
            case 2: // DTS
                query.setInteger("supplierId", supplierId);
                query.setInteger("dtsId", dtsId);
                break;

            case 3: // Meter

            case 4: // phase
                query.setInteger("meterId", meterId);
                break;
        }

        result = query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
        return result;
    }

    /**
     * method name : getMeterCountListPerLocation<b/>
     * method Desc : Metering Fail 가젯
     *
     * @param conditionMap
     * @return
     */
    @Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
    public List<Map<String, Object>> getMeterCountListPerLocation(Map<String, Object> conditionMap) {
        List<Map<String, Object>> result;

        Integer supplierId = (Integer)conditionMap.get("supplierId");
        String meterType = (String)conditionMap.get("meterType");
        String searchEndDate = (String)conditionMap.get("searchEndDate");
        String searchStartDate = (String)conditionMap.get("searchStartDate");

        StringBuilder sb = new StringBuilder();

        sb.append("\nSELECT p.id AS LOC_ID, ");
        sb.append("\n       COUNT(m.id) AS METER_CNT ");
        sb.append("\nFROM location p, ");
        sb.append("\n     meter m ");
        sb.append("\n     LEFT OUTER JOIN ");
        sb.append("\n     code c ");
        sb.append("\n     ON c.id = m.meter_status ");
        sb.append("\nWHERE m.location_id = p.id ");
        sb.append("\nAND   m.meter = :meterType ");
        sb.append("\nAND   m.install_date <= :searchEndDate ");
        sb.append("\nAND   m.supplier_id = :supplierId ");
        sb.append("\nAND   (c.id IS NULL ");
        sb.append("\n    OR c.code != :deleteCode ");
        sb.append("\n    OR (c.code = :deleteCode AND m.delete_date > :deleteDate) ");
        sb.append("\n) ");
        sb.append("\nGROUP BY p.id ");

//        sb.append("\nSELECT m.location.id AS pid, ");
//        sb.append("\n       COUNT(m.id) as mcount ");
//        sb.append("\nFROM Meter m ");
//        sb.append("\nWHERE m.meterType.code = :meterType ");
//        sb.append("\nAND   m.installDate <= :endDate ");
//        sb.append("\nAND   m.supplierId = :supplierId ");
//        sb.append("\nAND   m.location.supplierId = :supplierId ");
//        sb.append("\nGROUP BY m.location.id ");

        SQLQuery query = getSession().createSQLQuery(new SQLWrapper().getQuery(sb.toString()));
        query.setString("meterType", meterType);
        query.setString("searchEndDate", searchEndDate + "235959");
        query.setInteger("supplierId", supplierId);
        query.setString("deleteCode", MeterCodes.DELETE_STATUS.getCode());
        query.setString("deleteDate", searchStartDate + "235959");

        result = query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();

        return result;
    }
    
	public Integer getActiveMeterCount(Map<String, String> condition) {
		String supplierId = condition.get("supplierId");

        StringBuffer sb = new StringBuffer();

        sb.append("\n SELECT COUNT(me.id) ");
        sb.append("   FROM METER  me                     \n");
        sb.append("   LEFT OUTER JOIN CODE co            \n");
        sb.append("   ON ( me.meter_status = co.ID)      \n");
        sb.append("\n WHERE 1=1 ");
        sb.append("   AND (co.NAME  <> 'Delete' or co.NAME  IS NULL) \n");
        if (supplierId != null) {
            sb.append("\n AND me.supplier_id = :supplierId ");
        }

        SQLQuery query = getSession().createSQLQuery(sb.toString());
        if (!supplierId.isEmpty()) {
            query.setInteger("supplierId", Integer.parseInt(supplierId));
        }

        Number totalCount = (Number)query.uniqueResult();

        return totalCount.intValue();
	}
	
	public List<Meter> getMeterByModemId(String deviceSerial) {

        StringBuffer sb = new StringBuffer();

        sb.append("\n FROM Meter  me                     ");
        sb.append("\n WHERE me.modem.deviceSerial = :modemId ");

        Query query = getSession().createQuery(sb.toString());
        query.setString("modemId", deviceSerial);

        return query.list();
	}

	@Override
	@Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
	public Integer getTotalMeterCount() {
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT COUNT(*) FROM METER me									\n");
		sb.append("LEFT OUTER JOIN CODE co ON me.METER_STATUS = co.ID				\n");
		sb.append("WHERE co.name <> 'Delete'										\n");

		SQLQuery query = getSession().createSQLQuery(sb.toString());
		Number totalCount = (Number) query.uniqueResult();

		return totalCount.intValue();
	}

	// SP-659
	@Override
	@Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
	public List<Map<String, Object>> getMeteringRate(String searchTime, String tableName) {
		logger.info("searchTime : " + searchTime + ", " + "tableName : " + tableName.toUpperCase());
		
		List<Map<String, Object>> result = null;
		List<Map<String, Object>> mbb_result = null;
		StringBuilder rf_sb = new StringBuilder();
		StringBuilder mbb_sb = new StringBuilder();

		// RF Momdem
		rf_sb.append("SELECT  MCU.SYS_ID, LOCATION.NAME, COUNT(MCU.SYS_ID) AS RELATED_METER, COUNT (METERING.MDEV_ID) AS METERING_COUNT \n");
		rf_sb.append("FROM METER \n");
		rf_sb.append("LEFT OUTER JOIN MODEM ON METER.MODEM_ID = MODEM.ID \n");
		rf_sb.append("LEFT OUTER JOIN MCU ON MODEM.MCU_ID = MCU.ID	\n");
		rf_sb.append("LEFT OUTER JOIN LOCATION ON LOCATION.ID = MCU.LOCATION_ID \n");
		rf_sb.append("LEFT OUTER JOIN ( \n");
		rf_sb.append("		SELECT MDEV_ID FROM ( \n");
		rf_sb.append("			SELECT MDEV_ID, COUNT(MDEV_ID) AS METER_COUNT FROM LP_EM \n");
		rf_sb.append("			WHERE YYYYMMDDHH BETWEEN '" + searchTime + "00" + "' AND '" + searchTime + "23" + "'\n");
		rf_sb.append("			AND	CHANNEL = 1	\n");
		rf_sb.append("			GROUP BY MDEV_ID \n");
		rf_sb.append("		) WHERE METER_COUNT = '24' \n");
		rf_sb.append(") METERING ON METER.MDS_ID = METERING.MDEV_ID \n");
		rf_sb.append("WHERE METER.MDS_ID in ( \n");
		rf_sb.append("		SELECT MDS_ID FROM " + tableName + " WHERE MODEM_TYPE = 'RF' \n");
		rf_sb.append(") \n");
		rf_sb.append("AND MCU.SYS_ID IS NOT NULL \n");
		rf_sb.append("GROUP BY MCU.SYS_ID, LOCATION.NAME \n");
		rf_sb.append("ORDER BY MCU.SYS_ID \n");
		
		SQLQuery rf_query = getSession().createSQLQuery(rf_sb.toString());
		result = rf_query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
		logger.info("=== RF Result ===\n" + result);

		// MBB Modem
		mbb_sb.append("SELECT 'MBB' as SYS_ID, LOCATION.NAME, COUNT(MODEM.DEVICE_SERIAL) AS RELATED_METER, COUNT (METERING.MDEV_ID) AS METERING_COUNT \n");
		mbb_sb.append("FROM METER \n");
		mbb_sb.append("LEFT OUTER JOIN MODEM ON METER.MODEM_ID = MODEM.ID \n");
		mbb_sb.append("LEFT OUTER JOIN LOCATION ON LOCATION.ID = MODEM.LOCATION_ID \n"); 
		mbb_sb.append("LEFT OUTER JOIN ( \n");
		mbb_sb.append("		SELECT MDEV_ID FROM ( \n");
		mbb_sb.append(" 		SELECT MDEV_ID, COUNT(MDEV_ID) AS METER_COUNT FROM LP_EM \n");
		mbb_sb.append("			WHERE YYYYMMDDHH BETWEEN '" + searchTime + "00" + "' AND '" + searchTime + "23" + "'\n");
		mbb_sb.append(" 		AND	CHANNEL = 1 \n");
		mbb_sb.append("			GROUP BY MDEV_ID \n");
		mbb_sb.append("		) WHERE METER_COUNT = '24' \n");
		mbb_sb.append(") METERING ON METER.MDS_ID = METERING.MDEV_ID \n");
		mbb_sb.append("WHERE METER.MDS_ID in ( \n");
		mbb_sb.append("		SELECT MDS_ID FROM " + tableName + " WHERE MODEM_TYPE = 'MBB' \n");
		mbb_sb.append(") \n"); 
		mbb_sb.append("GROUP BY LOCATION.NAME \n");
		mbb_sb.append("ORDER BY LOCATION.NAME \n");
		
		SQLQuery mbb_query = getSession().createSQLQuery(mbb_sb.toString());
		mbb_result = mbb_query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
		logger.info("=== MBB Result ===\n" + mbb_result);

		// rf result + mbb result (S)
		for (Map<String, Object> map : mbb_result) {
			for (Map.Entry<String, Object> entry : map.entrySet()) {
				String key = entry.getKey();
				Object value = entry.getValue();
				map.put(key, value);
			}

			result.add(map);
		}
		// rf result + mbb result (E)
		return result;
	}

	@Override
	@Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
	public List<Map<String, Object>> getMeteringRate_detail(String searchTime, String tableName, String tempTableName) {
		
		String tempValueTableName = tempTableName + "_V";
		StringBuilder dso_sb = new StringBuilder();
		StringBuilder dso_v_sb = new StringBuilder();
		
		StringBuilder rf_sb = new StringBuilder();
		StringBuilder rf_v_sb = new StringBuilder();
		StringBuilder mbb_sb = new StringBuilder();
		StringBuilder mbb_v_sb = new StringBuilder();
		
		StringBuilder set_sb = new StringBuilder();
		StringBuilder result_sb = new StringBuilder();
		
		List<Map<String, Object>> result = null;
		
		// TRUNCATE (S)
		dso_sb.append("TRUNCATE TABLE " + tempTableName + "\n");
		Query dso_query = getSession().createSQLQuery(dso_sb.toString());
		dso_query.executeUpdate();
		
		dso_v_sb.append("TRUNCATE TABLE " + tempValueTableName + "\n");
		Query dso_v_query = getSession().createSQLQuery(dso_v_sb.toString());
		dso_v_query.executeUpdate();
		// TRUNCATE (E)
		
		// SLA 대상 미터리스트 추출 후, tempTableName 테이블에 데이터 INSERT (S)
		// RF
		rf_sb.append("INSERT INTO " + tempTableName + "\n");
		rf_sb.append("SELECT METER.MDS_ID, MODEM.DEVICE_SERIAL, MCU.SYS_ID, MODEM.FW_VER, MODEM.FW_REVISION, 0, LOCATION.NAME \n");
		rf_sb.append("FROM METER \n");
		rf_sb.append("LEFT OUTER JOIN MODEM ON METER.MODEM_ID = MODEM.ID \n");
		rf_sb.append("LEFT OUTER JOIN MCU ON MODEM.MCU_ID = MCU.ID \n");
		rf_sb.append("LEFT OUTER JOIN LOCATION ON MODEM.LOCATION_ID = LOCATION.ID \n");
		rf_sb.append("WHERE METER.MDS_ID IN ( \n");
		rf_sb.append("	SELECT MDS_ID FROM " + tableName + " WHERE MODEM_TYPE = 'RF' \n");
		rf_sb.append(") \n");
		rf_sb.append("ORDER BY LOCATION.NAME \n");
		
		Query rf_query = getSession().createSQLQuery(rf_sb.toString());
		rf_query.executeUpdate();
		
		// MBB
		mbb_sb.append("INSERT INTO " + tempTableName + "\n");
		mbb_sb.append("SELECT METER.MDS_ID, MODEM.DEVICE_SERIAL, 'MBB' AS SYS_ID, MODEM.FW_VER, MODEM.FW_REVISION, 0, LOCATION.NAME \n");
		mbb_sb.append("FROM METER \n");
		mbb_sb.append("LEFT OUTER JOIN MODEM ON METER.MODEM_ID = MODEM.ID \n");
		mbb_sb.append("LEFT OUTER JOIN LOCATION ON MODEM.LOCATION_ID = LOCATION.ID \n");
		mbb_sb.append("WHERE METER.MDS_ID IN ( \n");
		mbb_sb.append("	SELECT MDS_ID FROM " + tableName + " WHERE MODEM_TYPE = 'MBB' \n");
		mbb_sb.append(") \n");
		mbb_sb.append("ORDER BY LOCATION.NAME \n");
		
		Query mbb_query = getSession().createSQLQuery(mbb_sb.toString());
		mbb_query.executeUpdate();
		// SLA 대상 미터리스트 추출 후, tempTableName 테이블에 데이터 INSERT (E)
		
		// LP_EM에 적재되어있는 SLA 대상 미터리스트의 정보를 추출 후, tempValueTableName 테이블에 데이터 INSERT (S)
		// RF
		rf_v_sb.append("INSERT INTO " + tempValueTableName + "\n");
		rf_v_sb.append("SELECT METER.MDS_ID, MODEM.DEVICE_SERIAL, MCU.SYS_ID, MODEM.FW_VER, MODEM.FW_REVISION, METERING_COUNT, LOCATION.NAME FROM ( \n");
		rf_v_sb.append("	SELECT METER.MDS_ID AS METER_ID, COUNT(MDEV_ID) AS METERING_COUNT FROM LP_EM \n");
		rf_v_sb.append("	LEFT OUTER JOIN METER ON LP_EM.MDEV_ID = METER.MDS_ID \n");
		rf_v_sb.append("	LEFT OUTER JOIN MODEM ON METER.MODEM_ID = MODEM.ID \n");
		rf_v_sb.append("	LEFT OUTER JOIN MCU ON MODEM.MCU_ID = MCU.ID \n");
		rf_v_sb.append("	WHERE YYYYMMDDHH BETWEEN '" + searchTime + "00" + "' AND '" + searchTime + "23" + "'\n");
		rf_v_sb.append("	AND CHANNEL = 1 \n");
		rf_v_sb.append("	AND MCU.SYS_ID IS NOT NULL \n");
		rf_v_sb.append("	GROUP BY METER.MDS_ID \n");
		rf_v_sb.append("	) \n");
		rf_v_sb.append("LEFT OUTER JOIN METER ON METER_ID = METER.MDS_ID \n");
		rf_v_sb.append("LEFT OUTER JOIN MODEM ON METER.MODEM_ID = MODEM.ID \n");
		rf_v_sb.append("LEFT OUTER JOIN MCU ON MODEM.MCU_ID = MCU.ID \n");
		rf_v_sb.append("LEFT OUTER JOIN LOCATION ON MODEM.LOCATION_ID = LOCATION.ID \n");
		rf_v_sb.append("WHERE METER.MDS_ID IN ( \n");
		rf_v_sb.append("	SELECT MDS_ID FROM " + tableName + " WHERE MODEM_TYPE = 'RF' \n");
		rf_v_sb.append(") \n");
		rf_v_sb.append("ORDER BY LOCATION.NAME \n");
		
		Query rf_v_query = getSession().createSQLQuery(rf_v_sb.toString());
		rf_v_query.executeUpdate();
		
		// MBB
		mbb_v_sb.append("INSERT INTO " + tempValueTableName + "\n");
		mbb_v_sb.append("SELECT METER.MDS_ID, MODEM.DEVICE_SERIAL, 'MBB' AS SYS_ID, MODEM.FW_VER, MODEM.FW_REVISION, METERING_COUNT, LOCATION.NAME FROM ( \n");
		mbb_v_sb.append("	SELECT METER.MDS_ID AS METER_ID, COUNT(MDEV_ID) AS METERING_COUNT FROM LP_EM \n");
		mbb_v_sb.append("	LEFT OUTER JOIN METER ON LP_EM.MDEV_ID = METER.MDS_ID \n");
		mbb_v_sb.append("	LEFT OUTER JOIN MODEM ON METER.MODEM_ID = MODEM.ID \n");
		mbb_v_sb.append("	WHERE YYYYMMDDHH BETWEEN '" + searchTime + "00" + "' AND '" + searchTime + "23" + "'\n");
		mbb_v_sb.append("	AND CHANNEL = 1 \n");
		mbb_v_sb.append("	GROUP BY METER.MDS_ID \n");
		mbb_v_sb.append("	) \n");
		mbb_v_sb.append("LEFT OUTER JOIN METER ON METER_ID = METER.MDS_ID \n");
		mbb_v_sb.append("LEFT OUTER JOIN MODEM ON METER.MODEM_ID = MODEM.ID \n");
		mbb_v_sb.append("LEFT OUTER JOIN LOCATION ON MODEM.LOCATION_ID = LOCATION.ID \n");
		mbb_v_sb.append("WHERE METER.MDS_ID IN ( \n");
		mbb_v_sb.append("	SELECT MDS_ID FROM " + tableName + " WHERE MODEM_TYPE = 'MBB' \n");
		mbb_v_sb.append(") \n");
		mbb_v_sb.append("ORDER BY LOCATION.NAME \n");
		// LP_EM에 적재되어있는 SLA 대상 미터리스트의 정보를 추출 후, tempValueTableName 테이블에 데이터 INSERT (E)
		
		Query mbb_v_query = getSession().createSQLQuery(mbb_v_sb.toString());
		mbb_v_query.executeUpdate();
		
		// data set (S)
		set_sb.append("UPDATE " + tempTableName + "\n");
		set_sb.append("SET " + tempTableName + ".METERING_COUNT = NVL((SELECT DISTINCT MAX(" + tempValueTableName + ".METERING_COUNT) FROM " + tempValueTableName + " \n");
		set_sb.append("WHERE " + tempTableName + ".MDS_ID = " + tempValueTableName + ".MDS_ID), " + tempTableName + ".METERING_COUNT) \n");
		
		Query set_query = getSession().createSQLQuery(set_sb.toString());
		set_query.executeUpdate();
		// data set (E)
		
		result_sb.append("SELECT DSO, MDS_ID, DEVICE_SERIAL, SYS_ID, FW_VER, FW_REVISION, METERING_COUNT FROM " + tempTableName + " ORDER BY TO_NUMBER(METERING_COUNT) \n");
		
		Query result_query = getSession().createSQLQuery(result_sb.toString());
		result = result_query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();

		logger.info("=== Metering Detail Result ===\n" + result);
		
		return result;
		
	}

	@Override
	public List<Map<String, Object>> getPilot2MeteringRate(String searchTime, String tableName) {
		// TODO Auto-generated method stub
		logger.info("PILOT2 : searchTime : " + searchTime + ", " + "tableName : " + tableName.toUpperCase());
		
		List<Map<String, Object>> p2_result = null;
		StringBuilder p2_sb = new StringBuilder();

		p2_sb.append("SELECT \n");
		p2_sb.append("	NVL(MC.SYS_ID, \n");
		p2_sb.append("  	CASE MO.MODEM_TYPE \n");
		p2_sb.append("      WHEN 'MMIU' THEN 'MBB' \n");
		p2_sb.append("      WHEN 'SubGiga' THEN 'RF' \n");
		p2_sb.append("      END \n");
		p2_sb.append("  ) AS SYS_ID, \n");
		p2_sb.append("  LOCATION.NAME AS DSO, \n");
		p2_sb.append("  COUNT(*) AS RELATED_METER, \n");
		p2_sb.append("  COUNT(NO_LOSS.MDEV_ID) AS METERING_COUNT \n");
		p2_sb.append("FROM METER ME  \n");
		p2_sb.append("	LEFT OUTER JOIN MODEM MO ON ME.MODEM_ID = MO.ID \n");
		p2_sb.append("	LEFT OUTER JOIN MCU MC ON MO.MCU_ID = MC.ID  \n");
		p2_sb.append("	LEFT OUTER JOIN LOCATION ON LOCATION.ID = ME.LOCATION_ID \n");
		p2_sb.append("	LEFT OUTER JOIN ( \n");
		p2_sb.append("		SELECT MDEV_ID FROM ( \n");
		p2_sb.append("			SELECT MDEV_ID, COUNT(MDEV_ID) AS SUB_QUERY FROM LP_EM  \n");
		p2_sb.append("			WHERE YYYYMMDDHH BETWEEN '" + searchTime + "00' AND '" + searchTime + "23' \n");
		p2_sb.append("			AND	CHANNEL=1  \n");
		p2_sb.append("			GROUP BY MDEV_ID \n");
		p2_sb.append("		) WHERE SUB_QUERY = '24' \n");
		p2_sb.append("	) NO_LOSS ON ME.MDS_ID = NO_LOSS.MDEV_ID \n");
		p2_sb.append("WHERE ME.MDS_ID IN (  \n");
		p2_sb.append("  SELECT METERID FROM dwh$msa WHERE ENABLED='Y'  \n");
		p2_sb.append("	AND MSA NOT IN (    \n");
		p2_sb.append("  	'3 - Haugaland Kraft',    \n");
		p2_sb.append("		'200'    \n");
		p2_sb.append("  )   \n");
		p2_sb.append(")  \n");
		p2_sb.append("GROUP BY \n");
		p2_sb.append("	NVL(MC.SYS_ID, CASE MO.MODEM_TYPE WHEN 'MMIU' THEN 'MBB' WHEN 'SubGiga' THEN 'RF' END ), \n");
		p2_sb.append("  LOCATION.NAME \n");
		p2_sb.append("ORDER BY \n");
		p2_sb.append("  DECODE(SYS_ID, 'MBB', 0), \n");
		p2_sb.append("  DECODE(SYS_ID, 'RF', 1), \n");
		p2_sb.append("  NAME \n");
		
		SQLQuery p2_query = getSession().createSQLQuery(p2_sb.toString());
		p2_result = p2_query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
		logger.info("=== PILOT2 Result ===\n" + p2_result);

		
		return p2_result;
	}

	@Override
	public List<Map<String, Object>> getPilot2MeteringRate_detail(String searchTime, String tableName,
			String tempTableName) {
		logger.info("getPilot2MeteringRate_detail Called");
		// TODO Auto-generated method stub
		StringBuilder p2_detail_sb = new StringBuilder();
		
		List<Map<String, Object>> result = null;

		p2_detail_sb.append("SELECT \n");
		p2_detail_sb.append("	LOCATION.NAME AS DSO, \n");
		p2_detail_sb.append("   METER.MDS_ID AS METER_ID, \n");
		p2_detail_sb.append("   MODEM.DEVICE_SERIAL, \n");
		p2_detail_sb.append("   NVL( MCU.SYS_ID,  \n");
		p2_detail_sb.append("   	CASE MODEM.MODEM_TYPE \n");
		p2_detail_sb.append("       WHEN 'MMIU' THEN 'MBB' \n");
		p2_detail_sb.append("       WHEN 'SubGiga' THEN 'RF' \n");
		p2_detail_sb.append("       END \n");
		p2_detail_sb.append("   ) AS SYS_ID, \n");
		p2_detail_sb.append("   MODEM.FW_VER, \n");
		p2_detail_sb.append("   MODEM.FW_REVISION, \n");
		p2_detail_sb.append("   COUNT(LP_EM.MDEV_ID) AS METERING_COUNT \n");
		p2_detail_sb.append("FROM METER \n");
		p2_detail_sb.append("	LEFT OUTER JOIN MODEM ON MODEM.ID = METER.MODEM_ID  \n");
		p2_detail_sb.append("	LEFT OUTER JOIN LOCATION ON LOCATION.ID = METER.LOCATION_ID  \n");
		p2_detail_sb.append("	LEFT OUTER JOIN MCU ON MODEM.MCU_ID = MCU.ID \n");
		p2_detail_sb.append("   LEFT OUTER JOIN LP_EM ON METER.MDS_ID = LP_EM.MDEV_ID \n");
		p2_detail_sb.append("   	AND LP_EM.YYYYMMDDHH BETWEEN '" + searchTime + "00' AND '" + searchTime + "23'  \n");
		p2_detail_sb.append("   	AND LP_EM.CHANNEL = 1  \n");
		p2_detail_sb.append("WHERE METER.MDS_ID IN (  \n");
		p2_detail_sb.append("   SELECT METERID FROM dwh$msa WHERE ENABLED='Y'  \n");
		p2_detail_sb.append("	AND MSA NOT IN (    \n");
		p2_detail_sb.append("   	'3 - Haugaland Kraft',    \n");
		p2_detail_sb.append("		'200'    \n");
		p2_detail_sb.append("	)   \n");
		p2_detail_sb.append(") \n");
		p2_detail_sb.append("GROUP BY LOCATION.NAME, METER.MDS_ID, MODEM.DEVICE_SERIAL, NVL( MCU.SYS_ID, CASE MODEM.MODEM_TYPE WHEN 'MMIU' THEN 'MBB' WHEN 'SubGiga' THEN 'RF' END ),  \n");
		p2_detail_sb.append("MODEM.FW_VER, MODEM.FW_REVISION \n");
		p2_detail_sb.append("ORDER BY METERING_COUNT \n");
		
		Query result_query = getSession().createSQLQuery(p2_detail_sb.toString());
		result = result_query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();

		logger.info("=== PILOT2 Metering Detail Result ===\n" + result);
		
		return result;
	}
	
	@Override
	public List<Map<String, Object>> getSmallScaleMeteringRate(String searchTime) {
		// TODO Auto-generated method stub
		logger.info("SMALL SCALE : searchTime : " + searchTime);
		
		List<Map<String, Object>> ss_result = null;
		StringBuilder ss_sb = new StringBuilder();

		ss_sb.append("SELECT \n");
		ss_sb.append("	NVL(MC.SYS_ID, \n");
		ss_sb.append("  	CASE MO.MODEM_TYPE \n");
		ss_sb.append("      WHEN 'MMIU' THEN 'MBB' \n");
		ss_sb.append("      WHEN 'SubGiga' THEN 'RF' \n");
		ss_sb.append("      ELSE 'NOT MAPPED' \n");
		ss_sb.append("      END \n");
		ss_sb.append("  ) AS SYS_ID, \n");
		ss_sb.append("  LOCATION.NAME AS DSO, \n");
		ss_sb.append("  COUNT(*) AS RELATED_METER, \n");
		ss_sb.append("  NVL(SUM( CASE WHEN LP.LP_COUNT=24 THEN 1 END ),0) AS SUCCESS_METER, \n");
		ss_sb.append("  COUNT(*) - NVL(SUM( CASE WHEN LP.LP_COUNT=24 THEN 1 END ),0) AS FAIL_METER, \n");
		ss_sb.append("  COUNT(*)*24 AS TOTAL_LP, \n");
		ss_sb.append("  NVL(SUM(LP.LP_COUNT),0) AS COLLECTED_LP \n");
		ss_sb.append("FROM METER ME  \n");
		ss_sb.append("	LEFT OUTER JOIN MODEM MO ON ME.MODEM_ID = MO.ID \n");
		ss_sb.append("	LEFT OUTER JOIN MCU MC ON MO.MCU_ID = MC.ID  \n");
		ss_sb.append("	LEFT OUTER JOIN LOCATION ON LOCATION.ID = ME.LOCATION_ID \n");
		ss_sb.append("	LEFT OUTER JOIN ( \n");
		ss_sb.append("		SELECT MDEV_ID, COUNT(MDEV_ID) AS LP_COUNT FROM LP_EM  \n");
		ss_sb.append("		WHERE YYYYMMDDHH BETWEEN '" + searchTime + "00' AND '" + searchTime + "23' \n");
		ss_sb.append("		AND	CHANNEL=1  \n");
		ss_sb.append("		GROUP BY MDEV_ID \n");
		ss_sb.append("	) LP ON ME.MDS_ID = LP.MDEV_ID \n");
		ss_sb.append("WHERE ME.PHASE = 'SMALL SCALE'  \n");
//		ss_sb.append("AND TO_NUMBER(ME.INSTALL_DATE) < ( CASE LOCATION.NAME WHEN 'BKK' THEN 20170615000000 ELSE 99999999999999 END) \n");
		ss_sb.append("GROUP BY \n");
		ss_sb.append("	NVL(MC.SYS_ID, CASE MO.MODEM_TYPE WHEN 'MMIU' THEN 'MBB' WHEN 'SubGiga' THEN 'RF' ELSE 'NOT MAPPED' END ), \n");
		ss_sb.append("  LOCATION.NAME \n");
		ss_sb.append("ORDER BY \n");
		ss_sb.append("  DECODE(SYS_ID, 'MBB', 0), \n");
		ss_sb.append("  DECODE(SYS_ID, 'RF', 1), \n");
		ss_sb.append("  DECODE(SYS_ID, 'NOT MAPPED', 2), \n");
		ss_sb.append("  DSO, \n");
		ss_sb.append("  SYS_ID \n");
		
		SQLQuery ss_query = getSession().createSQLQuery(ss_sb.toString());
		ss_result = ss_query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
		logger.info("=== SMALL SCALE Result ===\n" + ss_result);

		
		return ss_result;
	}

	@Override
	public List<Map<String, Object>> getSmallScaleMeteringRate_detail(String searchTime) {
		logger.info("getSmallScaleMeteringRate_detail Called");
		// TODO Auto-generated method stub
		StringBuilder ss_detail_sb = new StringBuilder();
		
		List<Map<String, Object>> result = null;

		ss_detail_sb.append("SELECT \n");
		ss_detail_sb.append("	LOCATION.NAME AS DSO, \n");
		ss_detail_sb.append("   METER.MDS_ID AS METER_ID, \n");
		ss_detail_sb.append("   METER.GS1 AS GS1, \n");
		ss_detail_sb.append("   MODEM.DEVICE_SERIAL, \n");
		ss_detail_sb.append("   NVL( MCU.SYS_ID,  \n");
		ss_detail_sb.append("   	CASE MODEM.MODEM_TYPE \n");
		ss_detail_sb.append("       WHEN 'MMIU' THEN 'MBB' \n");
		ss_detail_sb.append("       WHEN 'SubGiga' THEN 'RF' \n");
		ss_detail_sb.append("       ELSE 'NOT MAPPED' \n");
		ss_detail_sb.append("       END \n");
		ss_detail_sb.append("   ) AS SYS_ID, \n");
		ss_detail_sb.append("   MODEM.FW_VER, \n");
		ss_detail_sb.append("   MODEM.FW_REVISION, \n");
		ss_detail_sb.append("   COUNT(LP_EM.MDEV_ID) AS METERING_COUNT,\n");
		ss_detail_sb.append("   TO_CHAR( \n");
		ss_detail_sb.append("   SUM(  \n");
		ss_detail_sb.append("   CASE WHEN LP_EM.YYYYMMDDHH = '" + searchTime + "00' THEN 100000000000000000000000 \n");
		ss_detail_sb.append("   WHEN LP_EM.YYYYMMDDHH = '" + searchTime + "01' THEN 10000000000000000000000 \n");
		ss_detail_sb.append("   WHEN LP_EM.YYYYMMDDHH = '" + searchTime + "02' THEN 1000000000000000000000 \n");
		ss_detail_sb.append("   WHEN LP_EM.YYYYMMDDHH = '" + searchTime + "03' THEN 100000000000000000000 \n");
		ss_detail_sb.append("   WHEN LP_EM.YYYYMMDDHH = '" + searchTime + "04' THEN 10000000000000000000 \n");
		ss_detail_sb.append("   WHEN LP_EM.YYYYMMDDHH = '" + searchTime + "05' THEN 1000000000000000000 \n");
		ss_detail_sb.append("   WHEN LP_EM.YYYYMMDDHH = '" + searchTime + "06' THEN 100000000000000000 \n");
		ss_detail_sb.append("   WHEN LP_EM.YYYYMMDDHH = '" + searchTime + "07' THEN 10000000000000000 \n");
		ss_detail_sb.append("   WHEN LP_EM.YYYYMMDDHH = '" + searchTime + "08' THEN 1000000000000000 \n");
		ss_detail_sb.append("   WHEN LP_EM.YYYYMMDDHH = '" + searchTime + "09' THEN 100000000000000 \n");
		ss_detail_sb.append("   WHEN LP_EM.YYYYMMDDHH = '" + searchTime + "10' THEN 10000000000000 \n");
		ss_detail_sb.append("   WHEN LP_EM.YYYYMMDDHH = '" + searchTime + "11' THEN 1000000000000 \n");
		ss_detail_sb.append("   WHEN LP_EM.YYYYMMDDHH = '" + searchTime + "12' THEN 100000000000 \n");
		ss_detail_sb.append("   WHEN LP_EM.YYYYMMDDHH = '" + searchTime + "13' THEN 10000000000 \n");
		ss_detail_sb.append("   WHEN LP_EM.YYYYMMDDHH = '" + searchTime + "14' THEN 1000000000 \n");
		ss_detail_sb.append("   WHEN LP_EM.YYYYMMDDHH = '" + searchTime + "15' THEN 100000000 \n");
		ss_detail_sb.append("   WHEN LP_EM.YYYYMMDDHH = '" + searchTime + "16' THEN 10000000 \n");
		ss_detail_sb.append("   WHEN LP_EM.YYYYMMDDHH = '" + searchTime + "17' THEN 1000000 \n");
		ss_detail_sb.append("   WHEN LP_EM.YYYYMMDDHH = '" + searchTime + "18' THEN 100000 \n");
		ss_detail_sb.append("   WHEN LP_EM.YYYYMMDDHH = '" + searchTime + "19' THEN 10000 \n");
		ss_detail_sb.append("   WHEN LP_EM.YYYYMMDDHH = '" + searchTime + "20' THEN 1000 \n");
		ss_detail_sb.append("   WHEN LP_EM.YYYYMMDDHH = '" + searchTime + "21' THEN 100 \n");
		ss_detail_sb.append("   WHEN LP_EM.YYYYMMDDHH = '" + searchTime + "22' THEN 10 \n");
		ss_detail_sb.append("   WHEN LP_EM.YYYYMMDDHH = '" + searchTime + "23' THEN 1 \n");
		ss_detail_sb.append("   ELSE 0 END ), '000000000000000000000009') AS LP_24 \n");
		ss_detail_sb.append("FROM METER \n");
		ss_detail_sb.append("	LEFT OUTER JOIN MODEM ON MODEM.ID = METER.MODEM_ID  \n");
		ss_detail_sb.append("	LEFT OUTER JOIN LOCATION ON LOCATION.ID = METER.LOCATION_ID  \n");
		ss_detail_sb.append("	LEFT OUTER JOIN MCU ON MODEM.MCU_ID = MCU.ID \n");
		ss_detail_sb.append("   LEFT OUTER JOIN LP_EM ON METER.MDS_ID = LP_EM.MDEV_ID \n");
		ss_detail_sb.append("   	AND LP_EM.YYYYMMDDHH BETWEEN '" + searchTime + "00' AND '" + searchTime + "23'  \n");
		ss_detail_sb.append("   	AND LP_EM.CHANNEL = 1  \n");
		ss_detail_sb.append("WHERE METER.PHASE = 'SMALL SCALE'  \n");
//		ss_detail_sb.append("AND TO_NUMBER(METER.INSTALL_DATE) < ( CASE LOCATION.NAME WHEN 'BKK' THEN 20170615000000 ELSE 99999999999999 END) \n");
		ss_detail_sb.append("GROUP BY LOCATION.NAME, METER.MDS_ID, METER.GS1, MODEM.DEVICE_SERIAL, NVL( MCU.SYS_ID, CASE MODEM.MODEM_TYPE WHEN 'MMIU' THEN 'MBB' WHEN 'SubGiga' THEN 'RF' ELSE 'NOT MAPPED' END ),  \n");
		ss_detail_sb.append("MODEM.FW_VER, MODEM.FW_REVISION \n");
		ss_detail_sb.append("ORDER BY METERING_COUNT \n");
		
		Query result_query = getSession().createSQLQuery(ss_detail_sb.toString());
		result = result_query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();

		logger.info("=== SMALL SCALE Metering Detail Result ===\n" + result);
		
		return result;
	}
	
	@Override
	public List<Map<String, Object>> getSmallScaleSLAMeteringRate_detail(String searchTime) {
		logger.info("getSmallScaleMeteringRate_detail Called");
		// TODO Auto-generated method stub
		StringBuilder ss_SLA_detail_sb = new StringBuilder();
		
		List<Map<String, Object>> result = null;

		ss_SLA_detail_sb.append("SELECT \n");
		ss_SLA_detail_sb.append("    LOCATION.NAME AS DSO, \n");
		ss_SLA_detail_sb.append("    METER.MDS_ID AS METER_ID, \n");
		ss_SLA_detail_sb.append("    NVL( MODEM.DEVICE_SERIAL, '-') AS DEVICE_SERIAL, \n");
		ss_SLA_detail_sb.append("    NVL( MCU.SYS_ID, CASE MODEM.MODEM_TYPE WHEN 'MMIU' THEN 'MBB' WHEN 'SubGiga' THEN 'RF' ELSE 'NOT MAPPED' END ) AS SYS_ID, \n");
		ss_SLA_detail_sb.append("    NVL( MODEM.FW_VER, '-') AS FW_VER , \n");
		ss_SLA_detail_sb.append("    NVL( MODEM.FW_REVISION, '-') AS FW_REVISION, \n");
		ss_SLA_detail_sb.append("    NVL( SUM(CASE WHEN TO_NUMBER(LP_EM.YYYYMMDDHH) >= " + searchTime + "0100 AND TO_NUMBER(LP_EM.YYYYMMDDHH) <= " + searchTime + "0123 THEN 1 END ),0) AS \"" + searchTime + "01\",\n");
		ss_SLA_detail_sb.append("    NVL( SUM(CASE WHEN TO_NUMBER(LP_EM.YYYYMMDDHH) >= " + searchTime + "0200 AND TO_NUMBER(LP_EM.YYYYMMDDHH) <= " + searchTime + "0223 THEN 1 END ),0) AS \"" + searchTime + "02\",\n");
		ss_SLA_detail_sb.append("    NVL( SUM(CASE WHEN TO_NUMBER(LP_EM.YYYYMMDDHH) >= " + searchTime + "0300 AND TO_NUMBER(LP_EM.YYYYMMDDHH) <= " + searchTime + "0323 THEN 1 END ),0) AS \"" + searchTime + "03\",\n");
		ss_SLA_detail_sb.append("    NVL( SUM(CASE WHEN TO_NUMBER(LP_EM.YYYYMMDDHH) >= " + searchTime + "0400 AND TO_NUMBER(LP_EM.YYYYMMDDHH) <= " + searchTime + "0423 THEN 1 END ),0) AS \"" + searchTime + "04\",\n");
		ss_SLA_detail_sb.append("    NVL( SUM(CASE WHEN TO_NUMBER(LP_EM.YYYYMMDDHH) >= " + searchTime + "0500 AND TO_NUMBER(LP_EM.YYYYMMDDHH) <= " + searchTime + "0523 THEN 1 END ),0) AS \"" + searchTime + "05\",\n");
		ss_SLA_detail_sb.append("    NVL( SUM(CASE WHEN TO_NUMBER(LP_EM.YYYYMMDDHH) >= " + searchTime + "0600 AND TO_NUMBER(LP_EM.YYYYMMDDHH) <= " + searchTime + "0623 THEN 1 END ),0) AS \"" + searchTime + "06\",\n");
		ss_SLA_detail_sb.append("    NVL( SUM(CASE WHEN TO_NUMBER(LP_EM.YYYYMMDDHH) >= " + searchTime + "0700 AND TO_NUMBER(LP_EM.YYYYMMDDHH) <= " + searchTime + "0723 THEN 1 END ),0) AS \"" + searchTime + "07\",\n");
		ss_SLA_detail_sb.append("    NVL( SUM(CASE WHEN TO_NUMBER(LP_EM.YYYYMMDDHH) >= " + searchTime + "0800 AND TO_NUMBER(LP_EM.YYYYMMDDHH) <= " + searchTime + "0823 THEN 1 END ),0) AS \"" + searchTime + "08\",\n");
		ss_SLA_detail_sb.append("    NVL( SUM(CASE WHEN TO_NUMBER(LP_EM.YYYYMMDDHH) >= " + searchTime + "0900 AND TO_NUMBER(LP_EM.YYYYMMDDHH) <= " + searchTime + "0923 THEN 1 END ),0) AS \"" + searchTime + "09\",\n");
		ss_SLA_detail_sb.append("    NVL( SUM(CASE WHEN TO_NUMBER(LP_EM.YYYYMMDDHH) >= " + searchTime + "1000 AND TO_NUMBER(LP_EM.YYYYMMDDHH) <= " + searchTime + "1023 THEN 1 END ),0) AS \"" + searchTime + "10\",\n");
		ss_SLA_detail_sb.append("    NVL( SUM(CASE WHEN TO_NUMBER(LP_EM.YYYYMMDDHH) >= " + searchTime + "1100 AND TO_NUMBER(LP_EM.YYYYMMDDHH) <= " + searchTime + "1123 THEN 1 END ),0) AS \"" + searchTime + "11\",\n");
		ss_SLA_detail_sb.append("    NVL( SUM(CASE WHEN TO_NUMBER(LP_EM.YYYYMMDDHH) >= " + searchTime + "1200 AND TO_NUMBER(LP_EM.YYYYMMDDHH) <= " + searchTime + "1223 THEN 1 END ),0) AS \"" + searchTime + "12\",\n");
		ss_SLA_detail_sb.append("    NVL( SUM(CASE WHEN TO_NUMBER(LP_EM.YYYYMMDDHH) >= " + searchTime + "1300 AND TO_NUMBER(LP_EM.YYYYMMDDHH) <= " + searchTime + "1323 THEN 1 END ),0) AS \"" + searchTime + "13\",\n");
		ss_SLA_detail_sb.append("    NVL( SUM(CASE WHEN TO_NUMBER(LP_EM.YYYYMMDDHH) >= " + searchTime + "1400 AND TO_NUMBER(LP_EM.YYYYMMDDHH) <= " + searchTime + "1423 THEN 1 END ),0) AS \"" + searchTime + "14\",\n");
		ss_SLA_detail_sb.append("    NVL( SUM(CASE WHEN TO_NUMBER(LP_EM.YYYYMMDDHH) >= " + searchTime + "1500 AND TO_NUMBER(LP_EM.YYYYMMDDHH) <= " + searchTime + "1523 THEN 1 END ),0) AS \"" + searchTime + "15\",\n");
		ss_SLA_detail_sb.append("    NVL( SUM(CASE WHEN TO_NUMBER(LP_EM.YYYYMMDDHH) >= " + searchTime + "1600 AND TO_NUMBER(LP_EM.YYYYMMDDHH) <= " + searchTime + "1623 THEN 1 END ),0) AS \"" + searchTime + "16\",\n");
		ss_SLA_detail_sb.append("    NVL( SUM(CASE WHEN TO_NUMBER(LP_EM.YYYYMMDDHH) >= " + searchTime + "1700 AND TO_NUMBER(LP_EM.YYYYMMDDHH) <= " + searchTime + "1723 THEN 1 END ),0) AS \"" + searchTime + "17\",\n");
		ss_SLA_detail_sb.append("    NVL( SUM(CASE WHEN TO_NUMBER(LP_EM.YYYYMMDDHH) >= " + searchTime + "1800 AND TO_NUMBER(LP_EM.YYYYMMDDHH) <= " + searchTime + "1823 THEN 1 END ),0) AS \"" + searchTime + "18\",\n");
		ss_SLA_detail_sb.append("    NVL( SUM(CASE WHEN TO_NUMBER(LP_EM.YYYYMMDDHH) >= " + searchTime + "1900 AND TO_NUMBER(LP_EM.YYYYMMDDHH) <= " + searchTime + "1923 THEN 1 END ),0) AS \"" + searchTime + "19\",\n");
		ss_SLA_detail_sb.append("    NVL( SUM(CASE WHEN TO_NUMBER(LP_EM.YYYYMMDDHH) >= " + searchTime + "2000 AND TO_NUMBER(LP_EM.YYYYMMDDHH) <= " + searchTime + "2023 THEN 1 END ),0) AS \"" + searchTime + "20\",\n");
		ss_SLA_detail_sb.append("    NVL( SUM(CASE WHEN TO_NUMBER(LP_EM.YYYYMMDDHH) >= " + searchTime + "2100 AND TO_NUMBER(LP_EM.YYYYMMDDHH) <= " + searchTime + "2123 THEN 1 END ),0) AS \"" + searchTime + "21\",\n");
		ss_SLA_detail_sb.append("    NVL( SUM(CASE WHEN TO_NUMBER(LP_EM.YYYYMMDDHH) >= " + searchTime + "2200 AND TO_NUMBER(LP_EM.YYYYMMDDHH) <= " + searchTime + "2223 THEN 1 END ),0) AS \"" + searchTime + "22\",\n");
		ss_SLA_detail_sb.append("    NVL( SUM(CASE WHEN TO_NUMBER(LP_EM.YYYYMMDDHH) >= " + searchTime + "2300 AND TO_NUMBER(LP_EM.YYYYMMDDHH) <= " + searchTime + "2323 THEN 1 END ),0) AS \"" + searchTime + "23\",\n");
		ss_SLA_detail_sb.append("    NVL( SUM(CASE WHEN TO_NUMBER(LP_EM.YYYYMMDDHH) >= " + searchTime + "2400 AND TO_NUMBER(LP_EM.YYYYMMDDHH) <= " + searchTime + "2423 THEN 1 END ),0) AS \"" + searchTime + "24\",\n");
		ss_SLA_detail_sb.append("    NVL( SUM(CASE WHEN TO_NUMBER(LP_EM.YYYYMMDDHH) >= " + searchTime + "2500 AND TO_NUMBER(LP_EM.YYYYMMDDHH) <= " + searchTime + "2523 THEN 1 END ),0) AS \"" + searchTime + "25\",\n");
		ss_SLA_detail_sb.append("    NVL( SUM(CASE WHEN TO_NUMBER(LP_EM.YYYYMMDDHH) >= " + searchTime + "2600 AND TO_NUMBER(LP_EM.YYYYMMDDHH) <= " + searchTime + "2623 THEN 1 END ),0) AS \"" + searchTime + "26\",\n");
		ss_SLA_detail_sb.append("    NVL( SUM(CASE WHEN TO_NUMBER(LP_EM.YYYYMMDDHH) >= " + searchTime + "2700 AND TO_NUMBER(LP_EM.YYYYMMDDHH) <= " + searchTime + "2723 THEN 1 END ),0) AS \"" + searchTime + "27\",\n");
		ss_SLA_detail_sb.append("    NVL( SUM(CASE WHEN TO_NUMBER(LP_EM.YYYYMMDDHH) >= " + searchTime + "2800 AND TO_NUMBER(LP_EM.YYYYMMDDHH) <= " + searchTime + "2823 THEN 1 END ),0) AS \"" + searchTime + "28\",\n");
		ss_SLA_detail_sb.append("    NVL( SUM(CASE WHEN TO_NUMBER(LP_EM.YYYYMMDDHH) >= " + searchTime + "2900 AND TO_NUMBER(LP_EM.YYYYMMDDHH) <= " + searchTime + "2923 THEN 1 END ),0) AS \"" + searchTime + "29\",\n");
		ss_SLA_detail_sb.append("    NVL( SUM(CASE WHEN TO_NUMBER(LP_EM.YYYYMMDDHH) >= " + searchTime + "3000 AND TO_NUMBER(LP_EM.YYYYMMDDHH) <= " + searchTime + "3023 THEN 1 END ),0) AS \"" + searchTime + "30\",\n");
		ss_SLA_detail_sb.append("    NVL( SUM(CASE WHEN TO_NUMBER(LP_EM.YYYYMMDDHH) >= " + searchTime + "3100 AND TO_NUMBER(LP_EM.YYYYMMDDHH) <= " + searchTime + "3123 THEN 1 END ),0) AS \"" + searchTime + "31\"\n");
		ss_SLA_detail_sb.append("FROM METER \n");
		ss_SLA_detail_sb.append("	 LEFT OUTER JOIN MODEM ON MODEM.ID = METER.MODEM_ID  \n");
		ss_SLA_detail_sb.append("	 LEFT OUTER JOIN LOCATION ON LOCATION.ID = METER.LOCATION_ID  \n");
		ss_SLA_detail_sb.append("	 LEFT OUTER JOIN MCU ON MODEM.MCU_ID = MCU.ID \n");
		ss_SLA_detail_sb.append("    LEFT OUTER JOIN LP_EM ON METER.MDS_ID = LP_EM.MDEV_ID \n");
		ss_SLA_detail_sb.append("        AND TO_NUMBER(LP_EM.YYYYMMDDHH) >= " + searchTime + "0100  \n");
		ss_SLA_detail_sb.append("        AND LP_EM.CHANNEL = 1  \n");
		ss_SLA_detail_sb.append("WHERE METER.PHASE = 'SMALL SCALE'  \n");
//		ss_SLA_detail_sb.append("AND TO_NUMBER(METER.INSTALL_DATE) < ( CASE LOCATION.NAME WHEN 'BKK' THEN 20170615000000 ELSE 99999999999999 END) \n");
		ss_SLA_detail_sb.append("GROUP BY LOCATION.NAME, METER.MDS_ID, NVL( MODEM.DEVICE_SERIAL, '-'), NVL( MCU.SYS_ID, CASE MODEM.MODEM_TYPE WHEN 'MMIU' THEN 'MBB' WHEN 'SubGiga' THEN 'RF' ELSE 'NOT MAPPED' END ), NVL( MODEM.FW_VER, '-'), NVL( MODEM.FW_REVISION, '-')  \n");
		ss_SLA_detail_sb.append("ORDER BY DSO \n");
		
		Query result_query = getSession().createSQLQuery(ss_SLA_detail_sb.toString());
		result = result_query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();

		logger.info("=== SMALL SCALE Metering Detail Result ===\n" + result);
		
		return result;
	}
	
	@Override
	public List<Map<String, Object>> getSmallScaleSLAMeteringRate(String searchTime) {
		// TODO Auto-generated method stub
		
		List<Map<String, Object>> ss_SLA_result = null;
		StringBuilder ss_SLA_sb = new StringBuilder();

		ss_SLA_sb.append("SELECT \n");
		ss_SLA_sb.append("	NVL(MC.SYS_ID, CASE MO.MODEM_TYPE WHEN 'MMIU' THEN 'MBB' WHEN 'SubGiga' THEN 'RF' ELSE 'NOT MAPPED' END ) AS SYS_ID, \n");
		ss_SLA_sb.append("	LOCATION.NAME AS DSO, \n");
		ss_SLA_sb.append("  COUNT(DISTINCT ME.MDS_ID) AS RELATED_METER, \n");
		ss_SLA_sb.append("  COUNT(DISTINCT ME.MDS_ID)*24 AS TOTAL_LP, \n");
		ss_SLA_sb.append("  NVL(SUM(CASE WHEN TO_NUMBER(LP.YYYYMMDDHH) >= " + searchTime + "0100 AND TO_NUMBER(LP.YYYYMMDDHH) <= " + searchTime + "0123 THEN 1 END ),0) AS \"" + searchTime + "01\",\n");
		ss_SLA_sb.append("  NVL(SUM(CASE WHEN TO_NUMBER(LP.YYYYMMDDHH) >= " + searchTime + "0200 AND TO_NUMBER(LP.YYYYMMDDHH) <= " + searchTime + "0223 THEN 1 END ),0) AS \"" + searchTime + "02\",\n");
		ss_SLA_sb.append("  NVL(SUM(CASE WHEN TO_NUMBER(LP.YYYYMMDDHH) >= " + searchTime + "0300 AND TO_NUMBER(LP.YYYYMMDDHH) <= " + searchTime + "0323 THEN 1 END ),0) AS \"" + searchTime + "03\",\n");
		ss_SLA_sb.append("  NVL(SUM(CASE WHEN TO_NUMBER(LP.YYYYMMDDHH) >= " + searchTime + "0400 AND TO_NUMBER(LP.YYYYMMDDHH) <= " + searchTime + "0423 THEN 1 END ),0) AS \"" + searchTime + "04\",\n");
		ss_SLA_sb.append("  NVL(SUM(CASE WHEN TO_NUMBER(LP.YYYYMMDDHH) >= " + searchTime + "0500 AND TO_NUMBER(LP.YYYYMMDDHH) <= " + searchTime + "0523 THEN 1 END ),0) AS \"" + searchTime + "05\",\n");
		ss_SLA_sb.append("  NVL(SUM(CASE WHEN TO_NUMBER(LP.YYYYMMDDHH) >= " + searchTime + "0600 AND TO_NUMBER(LP.YYYYMMDDHH) <= " + searchTime + "0623 THEN 1 END ),0) AS \"" + searchTime + "06\",\n");
		ss_SLA_sb.append("  NVL(SUM(CASE WHEN TO_NUMBER(LP.YYYYMMDDHH) >= " + searchTime + "0700 AND TO_NUMBER(LP.YYYYMMDDHH) <= " + searchTime + "0723 THEN 1 END ),0) AS \"" + searchTime + "07\",\n");
		ss_SLA_sb.append("  NVL(SUM(CASE WHEN TO_NUMBER(LP.YYYYMMDDHH) >= " + searchTime + "0800 AND TO_NUMBER(LP.YYYYMMDDHH) <= " + searchTime + "0823 THEN 1 END ),0) AS \"" + searchTime + "08\",\n");
		ss_SLA_sb.append("  NVL(SUM(CASE WHEN TO_NUMBER(LP.YYYYMMDDHH) >= " + searchTime + "0900 AND TO_NUMBER(LP.YYYYMMDDHH) <= " + searchTime + "0923 THEN 1 END ),0) AS \"" + searchTime + "09\",\n");
		ss_SLA_sb.append("  NVL(SUM(CASE WHEN TO_NUMBER(LP.YYYYMMDDHH) >= " + searchTime + "1000 AND TO_NUMBER(LP.YYYYMMDDHH) <= " + searchTime + "1023 THEN 1 END ),0) AS \"" + searchTime + "10\",\n");
		ss_SLA_sb.append("  NVL(SUM(CASE WHEN TO_NUMBER(LP.YYYYMMDDHH) >= " + searchTime + "1100 AND TO_NUMBER(LP.YYYYMMDDHH) <= " + searchTime + "1123 THEN 1 END ),0) AS \"" + searchTime + "11\",\n");
		ss_SLA_sb.append("  NVL(SUM(CASE WHEN TO_NUMBER(LP.YYYYMMDDHH) >= " + searchTime + "1200 AND TO_NUMBER(LP.YYYYMMDDHH) <= " + searchTime + "1223 THEN 1 END ),0) AS \"" + searchTime + "12\",\n");
		ss_SLA_sb.append("  NVL(SUM(CASE WHEN TO_NUMBER(LP.YYYYMMDDHH) >= " + searchTime + "1300 AND TO_NUMBER(LP.YYYYMMDDHH) <= " + searchTime + "1323 THEN 1 END ),0) AS \"" + searchTime + "13\",\n");
		ss_SLA_sb.append("  NVL(SUM(CASE WHEN TO_NUMBER(LP.YYYYMMDDHH) >= " + searchTime + "1400 AND TO_NUMBER(LP.YYYYMMDDHH) <= " + searchTime + "1423 THEN 1 END ),0) AS \"" + searchTime + "14\",\n");
		ss_SLA_sb.append("  NVL(SUM(CASE WHEN TO_NUMBER(LP.YYYYMMDDHH) >= " + searchTime + "1500 AND TO_NUMBER(LP.YYYYMMDDHH) <= " + searchTime + "1523 THEN 1 END ),0) AS \"" + searchTime + "15\",\n");
		ss_SLA_sb.append("  NVL(SUM(CASE WHEN TO_NUMBER(LP.YYYYMMDDHH) >= " + searchTime + "1600 AND TO_NUMBER(LP.YYYYMMDDHH) <= " + searchTime + "1623 THEN 1 END ),0) AS \"" + searchTime + "16\",\n");
		ss_SLA_sb.append("  NVL(SUM(CASE WHEN TO_NUMBER(LP.YYYYMMDDHH) >= " + searchTime + "1700 AND TO_NUMBER(LP.YYYYMMDDHH) <= " + searchTime + "1723 THEN 1 END ),0) AS \"" + searchTime + "17\",\n");
		ss_SLA_sb.append("  NVL(SUM(CASE WHEN TO_NUMBER(LP.YYYYMMDDHH) >= " + searchTime + "1800 AND TO_NUMBER(LP.YYYYMMDDHH) <= " + searchTime + "1823 THEN 1 END ),0) AS \"" + searchTime + "18\",\n");
		ss_SLA_sb.append("  NVL(SUM(CASE WHEN TO_NUMBER(LP.YYYYMMDDHH) >= " + searchTime + "1900 AND TO_NUMBER(LP.YYYYMMDDHH) <= " + searchTime + "1923 THEN 1 END ),0) AS \"" + searchTime + "19\",\n");
		ss_SLA_sb.append("  NVL(SUM(CASE WHEN TO_NUMBER(LP.YYYYMMDDHH) >= " + searchTime + "2000 AND TO_NUMBER(LP.YYYYMMDDHH) <= " + searchTime + "2023 THEN 1 END ),0) AS \"" + searchTime + "20\",\n");
		ss_SLA_sb.append("  NVL(SUM(CASE WHEN TO_NUMBER(LP.YYYYMMDDHH) >= " + searchTime + "2100 AND TO_NUMBER(LP.YYYYMMDDHH) <= " + searchTime + "2123 THEN 1 END ),0) AS \"" + searchTime + "21\",\n");
		ss_SLA_sb.append("  NVL(SUM(CASE WHEN TO_NUMBER(LP.YYYYMMDDHH) >= " + searchTime + "2200 AND TO_NUMBER(LP.YYYYMMDDHH) <= " + searchTime + "2223 THEN 1 END ),0) AS \"" + searchTime + "22\",\n");
		ss_SLA_sb.append("  NVL(SUM(CASE WHEN TO_NUMBER(LP.YYYYMMDDHH) >= " + searchTime + "2300 AND TO_NUMBER(LP.YYYYMMDDHH) <= " + searchTime + "2323 THEN 1 END ),0) AS \"" + searchTime + "23\",\n");
		ss_SLA_sb.append("  NVL(SUM(CASE WHEN TO_NUMBER(LP.YYYYMMDDHH) >= " + searchTime + "2400 AND TO_NUMBER(LP.YYYYMMDDHH) <= " + searchTime + "2423 THEN 1 END ),0) AS \"" + searchTime + "24\",\n");
		ss_SLA_sb.append("  NVL(SUM(CASE WHEN TO_NUMBER(LP.YYYYMMDDHH) >= " + searchTime + "2500 AND TO_NUMBER(LP.YYYYMMDDHH) <= " + searchTime + "2523 THEN 1 END ),0) AS \"" + searchTime + "25\",\n");
		ss_SLA_sb.append("  NVL(SUM(CASE WHEN TO_NUMBER(LP.YYYYMMDDHH) >= " + searchTime + "2600 AND TO_NUMBER(LP.YYYYMMDDHH) <= " + searchTime + "2623 THEN 1 END ),0) AS \"" + searchTime + "26\",\n");
		ss_SLA_sb.append("  NVL(SUM(CASE WHEN TO_NUMBER(LP.YYYYMMDDHH) >= " + searchTime + "2700 AND TO_NUMBER(LP.YYYYMMDDHH) <= " + searchTime + "2723 THEN 1 END ),0) AS \"" + searchTime + "27\",\n");
		ss_SLA_sb.append("  NVL(SUM(CASE WHEN TO_NUMBER(LP.YYYYMMDDHH) >= " + searchTime + "2800 AND TO_NUMBER(LP.YYYYMMDDHH) <= " + searchTime + "2823 THEN 1 END ),0) AS \"" + searchTime + "28\",\n");
		ss_SLA_sb.append("  NVL(SUM(CASE WHEN TO_NUMBER(LP.YYYYMMDDHH) >= " + searchTime + "2900 AND TO_NUMBER(LP.YYYYMMDDHH) <= " + searchTime + "2923 THEN 1 END ),0) AS \"" + searchTime + "29\",\n");
		ss_SLA_sb.append("  NVL(SUM(CASE WHEN TO_NUMBER(LP.YYYYMMDDHH) >= " + searchTime + "3000 AND TO_NUMBER(LP.YYYYMMDDHH) <= " + searchTime + "3023 THEN 1 END ),0) AS \"" + searchTime + "30\",\n");
		ss_SLA_sb.append("  NVL(SUM(CASE WHEN TO_NUMBER(LP.YYYYMMDDHH) >= " + searchTime + "3100 AND TO_NUMBER(LP.YYYYMMDDHH) <= " + searchTime + "3123 THEN 1 END ),0) AS \"" + searchTime + "31\"\n");
		ss_SLA_sb.append("FROM METER ME  \n");
		ss_SLA_sb.append("	LEFT OUTER JOIN MODEM MO ON ME.MODEM_ID = MO.ID \n");
		ss_SLA_sb.append("	LEFT OUTER JOIN MCU MC ON MO.MCU_ID = MC.ID  \n");
		ss_SLA_sb.append("	LEFT OUTER JOIN LOCATION ON LOCATION.ID = ME.LOCATION_ID \n");
		ss_SLA_sb.append("	LEFT OUTER JOIN ( \n");
		ss_SLA_sb.append("		SELECT MDEV_ID, YYYYMMDDHH, COUNT(MDEV_ID) AS LP_COUNT FROM LP_EM  \n");
		ss_SLA_sb.append("		WHERE TO_NUMBER(YYYYMMDDHH) >= " + searchTime + "0100\n");
		ss_SLA_sb.append("		AND	CHANNEL=1  \n");
		ss_SLA_sb.append("		GROUP BY MDEV_ID, YYYYMMDDHH \n");
		ss_SLA_sb.append("	) LP ON ME.MDS_ID = LP.MDEV_ID \n");
		ss_SLA_sb.append("WHERE ME.PHASE = 'SMALL SCALE'  \n");
//		ss_SLA_sb.append("AND TO_NUMBER(ME.INSTALL_DATE) < ( CASE LOCATION.NAME WHEN 'BKK' THEN 20170615000000 ELSE 99999999999999 END) \n");
		ss_SLA_sb.append("GROUP BY \n");
		ss_SLA_sb.append("	NVL(MC.SYS_ID, CASE MO.MODEM_TYPE WHEN 'MMIU' THEN 'MBB' WHEN 'SubGiga' THEN 'RF' ELSE 'NOT MAPPED' END ), \n");
		ss_SLA_sb.append("  LOCATION.NAME \n");
		ss_SLA_sb.append("ORDER BY \n");
		ss_SLA_sb.append("  DECODE(SYS_ID, 'MBB', 0), \n");
		ss_SLA_sb.append("  DECODE(SYS_ID, 'RF', 1), \n");
		ss_SLA_sb.append("  DECODE(SYS_ID, 'NOT MAPPED', 2), \n");
		ss_SLA_sb.append("  DSO, \n");
		ss_SLA_sb.append("  SYS_ID \n");
		
		SQLQuery ss_SLA_query = getSession().createSQLQuery(ss_SLA_sb.toString());
		ss_SLA_result = ss_SLA_query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
		logger.info("=== SMALL SCALE Result ===\n" + ss_SLA_result);

		
		return ss_SLA_result;
	}
	/* SP-572
	 * SP-1050(UPDATE)
	 * @see com.aimir.dao.device.MeterDao#getMeterWithMCU(java.util.Map)
	 */
	@Override
    @Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
    public List<Map<String, Object>> getMeterWithMCU(Map<String, String> params)
    {
    	List<Map<String,Object> >resultList = new ArrayList<Map<String,Object>>();

        String locationId		= StringUtil.nullToBlank(params.get("locationId"));
        String supplierId      = StringUtil.nullToBlank(params.get("supplierId"));
        String mdsId            = StringUtil.nullToBlank(params.get("mdsId"));
        String msa  = StringUtil.nullToBlank(params.get("msa")); // SP-1050
        
        
        StringBuffer query = new StringBuffer();
        query.append("\n SELECT m.id AS ID, m.mds_id AS MDS_ID, ");
        query.append("\n     m.address AS ADDRESS, ");
        query.append("\n     m.gpiox AS GPIOX, m.gpioy AS GPIOY, m.gpioz AS GPIOZ ,");
        query.append("\n     loc.name AS LOCATION_NAME,");
        query.append("\n     md.device_serial AS DEVICE_SERIAL, ");
        query.append("\n     mcu.sys_id AS SYS_ID, ");
        query.append("\n     md.modem_type AS MODEM_TYPE, "); 
        query.append("\n     md.protocol_type AS MODEM_PROTOCOL, ");
        query.append("\n     m.msa AS MSA ");
        query.append("\n FROM  meter m ");
        query.append("\n LEFT OUTER JOIN modem md  ON m.modem_id = md.id  ");
        query.append("\n LEFT OUTER JOIN mcu mcu ON md.mcu_id = mcu.id ");
        query.append("\n LEFT OUTER JOIN location loc ON m.location_id = loc.id ");
        query.append("\n WHERE m.supplier_id = :supplierId ");
        query.append("\n      AND (m.meter_status is null or m.meter_status!=(select id from code where code='1.3.3.9')) ");
        query.append("\n      AND (mcu.mcu_status is null or mcu.mcu_status != (select id from code where code='1.1.4.2')) ");
        if (!"".equals(mdsId)) {
            query.append("\nAND   m.mds_id LIKE :mdsId ");
        }
        else {
            if ( !"".equals(locationId)){
                query.append("\nAND   m.location_id = :locationId ");
            }
            if ( !"".equals(msa)) {
                query.append("\nAND   m.msa = :msa ");
            }
            else {
                query.append("\nAND  ( m.msa is null or m.msa = '' ) ");
            }
        }
        SQLQuery queryObj = getSession().createSQLQuery(new SQLWrapper().getQuery(query.toString()));
        
        if (!"".equals(mdsId)) {
        	queryObj.setString("mdsId", "%" + mdsId + "%");
        }
        else {
            if (!"".equals(locationId)){
            	queryObj.setInteger("locationId", Integer.parseInt(locationId));
            }
            if (!"".equals(msa)){
            	queryObj.setString("msa", msa);
            }
        }
        queryObj.setInteger("supplierId", Integer.parseInt(supplierId));
  
        resultList = queryObj.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
        return resultList;
    }
	/* SP-572
	 * @see com.aimir.dao.device.MeterDao#getMeterMMIU(java.util.Map)
	 */
	@Override
    @Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
	public List<Map<String, Object>> getMeterMMIU(Map<String, String>params)
    {
    	List<Map<String,Object> >resultList = new ArrayList<Map<String,Object>>();

        String locationId		= StringUtil.nullToBlank(params.get("locationId"));
        String supplierId      = StringUtil.nullToBlank(params.get("supplierId"));
        String mdsId            = StringUtil.nullToBlank(params.get("mdsId"));  
        
        
        StringBuffer query = new StringBuffer();
        query.append("\n SELECT m.id AS ID, m.mds_id AS MDS_ID, ");
        query.append("\n     m.address AS ADDRESS, ");
        query.append("\n     m.gpiox AS GPIOX, m.gpioy AS GPIOY, m.gpioz AS GPIOZ ,");
        query.append("\n     loc.name AS LOCATION_NAME,");
        query.append("\n     md.device_serial AS DEVICE_SERIAL, ");
        query.append("\n     mcu.sys_id AS SYS_ID");
        query.append("\n FROM  meter m ");
        query.append("\n LEFT OUTER JOIN modem md  ON m.modem_id = md.id  ");
        query.append("\n LEFT OUTER JOIN location loc ON m.location_id = loc.id ");
        query.append("\n WHERE m.supplier_id = :supplierId ");
        query.append("\n      AND md.modem = 'MMIU' ");
        query.append("\n      AND (m.meter_status is null or m.meter_status!=(select id from code where code='1.3.3.9')) and m.gpiox is not null and m.gpioy is not null ");
        if (!"".equals(mdsId)) {
            query.append("\nAND   m.mds_id LIKE :mdsId ");
        }
        else {
        	if ( !"".equals(locationId)){
        		query.append("\nAND   m.location_id = :locationId ");
        	}
        }
        
        SQLQuery queryObj = getSession().createSQLQuery(new SQLWrapper().getQuery(query.toString()));
        
        if (!"".equals(mdsId)) {
        	queryObj.setString("mdsId", "%" + mdsId + "%");
        }
        else {
            if (!"".equals(locationId)){
            	queryObj.setInteger("locationId", Integer.parseInt(locationId));
            }
        }
        queryObj.setInteger("supplierId", Integer.parseInt(supplierId));
  
        resultList = queryObj.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
        return resultList;
    }
	
    /* SP-784
     * @see com.aimir.dao.device.MeterDao#getMissingMetersForRecollect(java.util.Map)
     */
    @Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
    public List<Object> getMissingMetersForRecollect(Map<String, Object> params) {
        String searchStartDate = (String) params.get("searchStartDate"); //yyyymmdd
        String searchEndDate = (String) params.get("searchEndDate");  //yyyymmdd
        String meterType = StringUtil.nullToBlank(params.get("meterType"));
		// -> INSERT START 2018/02/15 #SP-892
        String modemType = StringUtil.nullToBlank(params.get("modemType"));
        String protocolType = StringUtil.nullToBlank(params.get("protocolType"));
		// <- INSERT END   2018/02/15 #SP-892
        String supplierId = StringUtil.nullToBlank(params.get("supplierId"));
        Integer channel = (Integer) params.get("channel");
        String mdsId = StringUtil.nullToBlank(params.get("mdsId"));
        String deviceType = StringUtil.nullToBlank((String) params.get("deviceType"));
        String deviceId = StringUtil.nullToBlank((String) params.get("deviceId"));
        String sysId = StringUtil.nullToBlank((String) params.get("sysId")); // SP-677
        Integer page = (Integer) params.get("page");
        Integer limit = (Integer) params.get("limit");
        String lastLinkTime = StringUtil.nullToBlank((String) params.get("lastLinkTime"));
        String lastReadDate = StringUtil.nullToBlank((String) params.get("lastReadDate"));
        String today = TimeUtil.getCurrentTimeMilli(); // yyyyMMddHHmmss
        String currDate = today.substring(0, 8);
        String currHour = today.substring(8, 10);
        Integer currMinute = Integer.parseInt(today.substring(10, 12));        
        List<String> sysIdList = (List<String>) params.get("sysIdList");
        String locationName = StringUtil.nullToBlank(params.get("locationName")); //SP-1051

        String lpTable = CommonConstants.MeterType.valueOf(meterType).getLpTableName();

        int period = 0;
        try {
            period = TimeUtil.getDayDuration(searchStartDate, searchEndDate) + 1;
        } catch (ParseException e) {
            e.printStackTrace();
        }

        StringBuffer sbQueryAllPeriod = new StringBuffer();
        sbQueryAllPeriod.append("\nSELECT mcu.sys_id ");
        sbQueryAllPeriod.append("\n       ,mcu.id AS MCU_ID");
        sbQueryAllPeriod.append("\n       ,modem.device_serial ");
        sbQueryAllPeriod.append("\n       ,m1.mds_id ");
        sbQueryAllPeriod.append("\n       ,m1.last_read_date ");
        sbQueryAllPeriod.append("\n       ,m1.id  AS METER_ID");
        sbQueryAllPeriod.append("\n       ,modem.modem_type AS MODEM_TYPE, modem.protocol_type AS PROTOCOL_TYPE ");
        sbQueryAllPeriod.append("\nFROM meter m1 ");
        sbQueryAllPeriod.append("\n     LEFT OUTER JOIN modem modem ON m1.modem_id = modem.id ");
        sbQueryAllPeriod.append("\n     LEFT OUTER JOIN mcu mcu ON modem.mcu_id = mcu.id ");
        sbQueryAllPeriod.append("\n     LEFT OUTER JOIN code cd ON cd.id = m1.meter_status ");
        sbQueryAllPeriod.append("\n     LEFT OUTER JOIN location lo ON m1.location_id = lo.id "); //SP-1051
        sbQueryAllPeriod.append("\nWHERE m1.install_date <= :installDate ");
        sbQueryAllPeriod.append("\nAND m1.meter = :meterType ");
		// -> INSERT START 2018/02/15 #SP-892
        if (!"".equals(modemType)) {
            sbQueryAllPeriod.append("\nAND modem.modem_type = :modemType ");
        }
        if (!"".equals(protocolType)) {
            sbQueryAllPeriod.append("\nAND modem.protocol_type = :protocolType ");
        }
		// <- INSERT END   2018/02/15 #SP-892
        if (!"".equals(supplierId)) {
            sbQueryAllPeriod.append("\nAND m1.supplier_id = :supplierId ");
        }
        if (!"".equals(mdsId)) {
            sbQueryAllPeriod.append("\nAND m1.mds_id LIKE :mdsId ");
        }
        if ( !"".equals(sysId) ) {
            sbQueryAllPeriod.append("\nAND mcu.sys_id = :sysId ");
        }
        if (sysIdList != null &&  sysIdList.size() > 0) {
            sbQueryAllPeriod.append("\nAND mcu.sys_id IN (:sysIdList) ");
        }        
        if ( !"".equals(lastLinkTime) ) {
        	sbQueryAllPeriod.append("\nAND modem.last_link_time >= :lastLinkTime ");
        }
        if ( !"".equals(lastReadDate) ) {
        	sbQueryAllPeriod.append("\nAND m1.last_read_date >= :lastReadDate ");
        }
        
        // deviceId , deviceType 의 null 값 체크
        if (!"".equals(deviceId) && !"".equals(deviceType)) {
            if (CommonConstants.DeviceType.MCU.getCode().equals(Integer.parseInt(deviceType))) {
                sbQueryAllPeriod.append("\nAND mcu.sys_name LIKE :deviceId ");
            } else {
                sbQueryAllPeriod.append("\nAND modem.device_serial LIKE :deviceId ");
            }
        }
        
        // INSERT START SP-1051
        if (!"".equals(locationName)) {
        	sbQueryAllPeriod.append("\nAND lo.name IN ("); 
        	sbQueryAllPeriod.append(locationName);
        	sbQueryAllPeriod.append(") ");        	
        }        
        // INSERT END SP-1051
        
        sbQueryAllPeriod.append("\nAND   (cd.id IS NULL ");
        sbQueryAllPeriod.append("\n    OR cd.code != :deleteCode ");
        sbQueryAllPeriod.append("\n    OR (cd.code = :deleteCode AND m1.delete_date > :deleteDate)) ");
        sbQueryAllPeriod.append("\nAND m1.id NOT IN ( ");
        sbQueryAllPeriod.append("\n    SELECT x.id ");
        sbQueryAllPeriod.append("\n    FROM ( ");
        sbQueryAllPeriod.append("\n        SELECT m.id,lp.yyyymmdd ");
        sbQueryAllPeriod.append("\n        FROM meter m ");
        sbQueryAllPeriod.append("\n             LEFT OUTER JOIN ");
        sbQueryAllPeriod.append("\n             code c ");
        sbQueryAllPeriod.append("\n             ON c.id = m.meter_status ");
        sbQueryAllPeriod.append("\n             LEFT OUTER JOIN ");
        sbQueryAllPeriod.append("\n             ").append(lpTable).append(" lp ");
        sbQueryAllPeriod.append("\n             ON  lp.mdev_id = m.mds_id ");
        sbQueryAllPeriod.append("\n             AND lp.yyyymmddhh BETWEEN :startDate AND :endDate ");
        sbQueryAllPeriod.append("\n             AND lp.channel = :channel ");
        sbQueryAllPeriod.append("\n             AND lp.dst = 0 ");			// INSERT SP-1051        
        sbQueryAllPeriod.append("\n        WHERE m.install_date <= :installDate ");
        sbQueryAllPeriod.append("\n        AND   (m.lp_interval IN (1, 5, 10, 15, 30, 60) ");
        sbQueryAllPeriod.append("\n           AND lp.value_cnt >= (60/m.lp_interval)) ");
        sbQueryAllPeriod.append("\n        AND   (c.id IS NULL ");
        sbQueryAllPeriod.append("\n            OR c.code != :deleteCode ");
        sbQueryAllPeriod.append("\n            OR (c.code = :deleteCode AND m.delete_date > :deleteDate)) ");
        
        // INSERT START SP-1051
        if (!"".equals(locationName)) {
            sbQueryAllPeriod.append("\n    AND   m.location_id IN (SELECT id FROM location WHERE name IN (");
        	sbQueryAllPeriod.append(locationName);
        	sbQueryAllPeriod.append(")) ");
        }
        // INSERT END SP-1051
        
        sbQueryAllPeriod.append("\n        GROUP BY m.id, lp.yyyymmdd ");
        sbQueryAllPeriod.append("\n        HAVING COUNT(m.id) = 24 ");
        sbQueryAllPeriod.append("\n    ) x ");
        sbQueryAllPeriod.append("\n    WHERE x.yyyymmdd IS NOT NULL ");
        sbQueryAllPeriod.append("\n    GROUP BY x.id ");
        sbQueryAllPeriod.append("\n    HAVING COUNT(x.id) = :period ");
        sbQueryAllPeriod.append("\n) ");
/////

        StringBuffer sbQueryAllPeriodWithToday = new StringBuffer();
        sbQueryAllPeriodWithToday.append("\nSELECT mcu.sys_id ");
        sbQueryAllPeriodWithToday.append("\n       ,mcu.id AS MCU_ID ");
        sbQueryAllPeriodWithToday.append("\n       ,modem.device_serial ");
        sbQueryAllPeriodWithToday.append("\n       ,m1.mds_id ");
        sbQueryAllPeriodWithToday.append("\n       ,m1.last_read_date ");
        sbQueryAllPeriodWithToday.append("\n       ,m1.id AS METER_ID ");
        sbQueryAllPeriodWithToday.append("\n       ,modem.modem_type AS MODEM_TYPE, modem.protocol_type AS PROTOCOL_TYPE ");
        sbQueryAllPeriodWithToday.append("\nFROM meter m1 ");
        sbQueryAllPeriodWithToday.append("\n     LEFT OUTER JOIN modem modem ON m1.modem_id = modem.id ");
        sbQueryAllPeriodWithToday.append("\n     LEFT OUTER JOIN mcu mcu ON modem.mcu_id = mcu.id ");
        sbQueryAllPeriodWithToday.append("\n     LEFT OUTER JOIN code cd ON cd.id = m1.meter_status ");
        sbQueryAllPeriodWithToday.append("\n     LEFT OUTER JOIN location lo ON m1.location_id = lo.id "); //SP-1051
        sbQueryAllPeriodWithToday.append("\nWHERE m1.install_date <= :installDate ");
        sbQueryAllPeriodWithToday.append("\nAND   m1.meter = :meterType ");
		// -> INSERT START 2018/02/15 #SP-892
        if (!"".equals(modemType)) {
            sbQueryAllPeriodWithToday.append("\nAND modem.modem_type = :modemType ");
        }
        if (!"".equals(protocolType)) {
            sbQueryAllPeriodWithToday.append("\nAND modem.protocol_type = :protocolType ");
        }
		// <- INSERT END   2018/02/15 #SP-892
        if (!"".equals(supplierId)) {
            sbQueryAllPeriodWithToday.append("\nAND   m1.supplier_id = :supplierId ");
        }
        if (!"".equals(mdsId)) {
            sbQueryAllPeriodWithToday.append("\nAND   m1.mds_id LIKE :mdsId ");
        }
        if ( !"".equals(sysId) ) {
        	sbQueryAllPeriodWithToday.append("\nAND mcu.sys_id = :sysId ");
        }
        if (sysIdList != null &&  sysIdList.size() > 0) {
        	sbQueryAllPeriodWithToday.append("\nAND mcu.sys_id IN (:sysIdList) ");
        } 
	    if ( !"".equals(lastLinkTime) ) {
	    	sbQueryAllPeriodWithToday.append("\nAND modem.last_link_time >= :lastLinkTime ");
	    }
	    if ( !"".equals(lastReadDate) ) {
	    	sbQueryAllPeriodWithToday.append("\nAND m1.last_read_date >= :lastReadDate ");
	    }
	    
        // deviceId , deviceType 의 null 값 체크
        if (!"".equals(deviceId) && !"".equals(deviceType)) {
            if (CommonConstants.DeviceType.MCU.getCode().equals(Integer.parseInt(deviceType))) {
                sbQueryAllPeriodWithToday.append("\nAND   mcu.sys_name LIKE :deviceId ");
            } else {
                sbQueryAllPeriodWithToday.append("\nAND   modem.device_serial LIKE :deviceId ");
            }
        }
        // INSERT START SP-1051
        if (!"".equals(locationName)) {
        	sbQueryAllPeriodWithToday.append("\nAND lo.name IN ("); 
        	sbQueryAllPeriodWithToday.append(locationName);
        	sbQueryAllPeriodWithToday.append(") ");        	
        }        
        // INSERT END SP-1051        
        
        sbQueryAllPeriodWithToday.append("\nAND   (cd.id IS NULL ");
        sbQueryAllPeriodWithToday.append("\n    OR cd.code != :deleteCode ");
        sbQueryAllPeriodWithToday.append("\n    OR (cd.code = :deleteCode AND m1.delete_date > :deleteDate)) ");
        sbQueryAllPeriodWithToday.append("\nAND   m1.id NOT IN ( ");
        sbQueryAllPeriodWithToday.append("\n    SELECT i3.id");
        sbQueryAllPeriodWithToday.append("\n    FROM ( ");
        sbQueryAllPeriodWithToday.append("\n        SELECT i1.id FROM ( ");
        sbQueryAllPeriodWithToday.append("\n            SELECT x.id ");
        sbQueryAllPeriodWithToday.append("\n            FROM ( ");
        sbQueryAllPeriodWithToday.append("\n                SELECT m.id,lp.yyyymmdd ");
        sbQueryAllPeriodWithToday.append("\n                FROM meter m ");
        sbQueryAllPeriodWithToday.append("\n                     LEFT OUTER JOIN ");
        sbQueryAllPeriodWithToday.append("\n                     code c ");
        sbQueryAllPeriodWithToday.append("\n                     ON c.id = m.meter_status ");
        sbQueryAllPeriodWithToday.append("\n                     LEFT OUTER JOIN ").append(lpTable).append(" lp ");
        sbQueryAllPeriodWithToday.append("\n                     ON lp.mdev_id = m.mds_id ");
        sbQueryAllPeriodWithToday.append("\n                     AND lp.yyyymmddhh BETWEEN :startDate AND :endDate ");
        sbQueryAllPeriodWithToday.append("\n                     AND lp.channel = :channel ");
        sbQueryAllPeriodWithToday.append("\n             		 AND lp.dst = 0 ");			// INSERT SP-1051                
        sbQueryAllPeriodWithToday.append("\n                WHERE m.install_date <= :installDate ");
        sbQueryAllPeriodWithToday.append("\n                AND   (m.lp_interval IN (1, 5, 10, 15, 30, 60) ");
        sbQueryAllPeriodWithToday.append("\n                   AND lp.value_cnt >= (60/m.lp_interval)) ");
        sbQueryAllPeriodWithToday.append("\n                AND   (c.id IS NULL ");
        sbQueryAllPeriodWithToday.append("\n                    OR c.code != :deleteCode ");
        sbQueryAllPeriodWithToday.append("\n                    OR (c.code = :deleteCode AND m.delete_date > :deleteDate)) ");

        // INSERT START SP-1051
        if (!"".equals(locationName)) {
        	sbQueryAllPeriodWithToday.append("\n    		AND   m.location_id IN (SELECT id FROM location WHERE name IN ( ");
        	sbQueryAllPeriodWithToday.append(locationName);
        	sbQueryAllPeriodWithToday.append(")) ");
        }
        // INSERT END SP-1051        
        
        sbQueryAllPeriodWithToday.append("\n                GROUP BY m.id,lp.yyyymmdd ");
        sbQueryAllPeriodWithToday.append("\n                HAVING COUNT(m.id) = 24 ");
        sbQueryAllPeriodWithToday.append("\n            )x ");
        sbQueryAllPeriodWithToday.append("\n            WHERE x.yyyymmdd IS NOT NULL ");
        sbQueryAllPeriodWithToday.append("\n            GROUP BY x.id ");
        sbQueryAllPeriodWithToday.append("\n            HAVING COUNT(x.id) = :period ");
        sbQueryAllPeriodWithToday.append("\n        ) i1 ");

        sbQueryAllPeriodWithToday.append("\n        INNER JOIN ");

        sbQueryAllPeriodWithToday.append("\n        ( ");
        sbQueryAllPeriodWithToday.append("\n            SELECT m.id ");
        sbQueryAllPeriodWithToday.append("\n            FROM meter m ");
        sbQueryAllPeriodWithToday.append("\n                 LEFT OUTER JOIN ");
        sbQueryAllPeriodWithToday.append("\n                 code c ");
        sbQueryAllPeriodWithToday.append("\n                 ON c.id = m.meter_status ");
        sbQueryAllPeriodWithToday.append("\n                 LEFT OUTER JOIN ").append(lpTable).append(" lp ");
        sbQueryAllPeriodWithToday.append("\n                 ON lp.mdev_id = m.mds_id ");
        sbQueryAllPeriodWithToday.append("\n                 AND lp.yyyymmddhh BETWEEN :currStartDate AND :currEndDate ");
        sbQueryAllPeriodWithToday.append("\n                 AND lp.hh < :currHour ");
        sbQueryAllPeriodWithToday.append("\n                 AND lp.channel = :channel ");
        sbQueryAllPeriodWithToday.append("\n             	 AND lp.dst = 0 ");			// INSERT SP-1051                        
        sbQueryAllPeriodWithToday.append("\n            WHERE m.install_date <= :installDate ");
        sbQueryAllPeriodWithToday.append("\n            AND   (m.lp_interval IN (1, 5, 10, 15, 30, 60) ");
        sbQueryAllPeriodWithToday.append("\n               AND lp.value_cnt >= (60/m.lp_interval)) ");
        sbQueryAllPeriodWithToday.append("\n            AND   (c.id IS NULL ");
        sbQueryAllPeriodWithToday.append("\n                OR c.code != :deleteCode ");
        sbQueryAllPeriodWithToday.append("\n                OR (c.code = :deleteCode AND m.delete_date > :deleteDate)) ");

        // INSERT START SP-1051
        if (!"".equals(locationName)) {
        	sbQueryAllPeriodWithToday.append("\n    	AND   m.location_id IN (SELECT id FROM location WHERE name IN ( ");
        	sbQueryAllPeriodWithToday.append(locationName);
        	sbQueryAllPeriodWithToday.append(")) ");
        }
        // INSERT END SP-1051         
        
        sbQueryAllPeriodWithToday.append("\n            GROUP BY m.id, lp.yyyymmdd ");
//        sbQueryAllPeriodWithToday.append("\n            HAVING COUNT(m.id) = :currHour) i2 ON i1.id=i2.id ) i3");
        sbQueryAllPeriodWithToday.append("\n            HAVING COUNT(m.id) = :currHour ");
        sbQueryAllPeriodWithToday.append("\n        ) i2 ON i1.id = i2.id ");
        sbQueryAllPeriodWithToday.append("\n    ) i3");

        sbQueryAllPeriodWithToday.append("\n    INNER JOIN ");

        sbQueryAllPeriodWithToday.append("\n    ( ");
        sbQueryAllPeriodWithToday.append("\n        SELECT m.id ");
        sbQueryAllPeriodWithToday.append("\n        FROM meter m ");
        sbQueryAllPeriodWithToday.append("\n             LEFT OUTER JOIN ");
        sbQueryAllPeriodWithToday.append("\n             code c ");
        sbQueryAllPeriodWithToday.append("\n             ON c.id = m.meter_status ");
        sbQueryAllPeriodWithToday.append("\n             LEFT OUTER JOIN ").append(lpTable).append(" lp ");
        sbQueryAllPeriodWithToday.append("\n             ON lp.mdev_id = m.mds_id ");
        sbQueryAllPeriodWithToday.append("\n             AND lp.yyyymmddhh BETWEEN :currStartDate AND :currEndDate ");
        sbQueryAllPeriodWithToday.append("\n             AND lp.hh = :currHour ");
        sbQueryAllPeriodWithToday.append("\n             AND lp.channel = :channel ");
        sbQueryAllPeriodWithToday.append("\n             AND lp.dst = 0 ");			// INSERT SP-1051                        
        sbQueryAllPeriodWithToday.append("\n        WHERE m.install_date <= :installDate ");

        if (currMinute < 1) {
            sbQueryAllPeriodWithToday.append("\n        AND   (m.lp_interval IN (1, 5, 10, 15, 30, 60) ");
            sbQueryAllPeriodWithToday.append("\n           AND lp.value_cnt >= 1) ");
        } else {
            sbQueryAllPeriodWithToday.append("\n        AND   (m.lp_interval IN (1, 5, 10, 15, 30, 60) ");
            sbQueryAllPeriodWithToday.append("\n           AND lp.value_cnt > (:currMinute/m.lp_interval)) ");
        }
        sbQueryAllPeriodWithToday.append("\n        AND   (c.id IS NULL ");
        sbQueryAllPeriodWithToday.append("\n            OR c.code != :deleteCode ");
        sbQueryAllPeriodWithToday.append("\n            OR (c.code = :deleteCode AND m.delete_date > :deleteDate)) ");

        // INSERT START SP-1051
        if (!"".equals(locationName)) {
        	sbQueryAllPeriodWithToday.append("\n    AND   m.location_id IN (SELECT id FROM location WHERE name IN ( ");
        	sbQueryAllPeriodWithToday.append(locationName);
        	sbQueryAllPeriodWithToday.append(")) ");
        }
        // INSERT END SP-1051         
        
        sbQueryAllPeriodWithToday.append("\n    ) i4 ON i3.id = i4.id ");
        sbQueryAllPeriodWithToday.append("\n) ");

        StringBuffer sbQueryToday = new StringBuffer();
        sbQueryToday.append("\nSELECT mcu.sys_id ");
        sbQueryToday.append("\n       ,mcu.id AS MCU_ID ");
        sbQueryToday.append("\n       ,modem.device_serial ");
        sbQueryToday.append("\n       ,m1.mds_id ");
        sbQueryToday.append("\n       ,m1.last_read_date ");
        sbQueryToday.append("\n       ,m1.id AS METER_ID ");
        sbQueryToday.append("\n       ,modem.modem_type AS MODEM_TYPE, modem.protocol_type AS PROTOCOL_TYPE ");
        sbQueryToday.append("\nFROM meter m1 ");
        sbQueryToday.append("\n     LEFT OUTER JOIN contract contract ON m1.id = contract.meter_id ");
        sbQueryToday.append("\n     LEFT OUTER JOIN customer customer ON contract.customer_id = customer.id ");
        sbQueryToday.append("\n     LEFT OUTER JOIN modem modem ON m1.modem_id = modem.id ");
        sbQueryToday.append("\n     LEFT OUTER JOIN mcu mcu ON modem.mcu_id = mcu.id ");
        sbQueryToday.append("\n     LEFT OUTER JOIN code cd ON cd.id = m1.meter_status ");
        sbQueryToday.append("\n     LEFT OUTER JOIN location lo ON m1.location_id = lo.id "); //SP-1051
        sbQueryToday.append("\nWHERE m1.install_date <= :installDate ");
        sbQueryToday.append("\nAND   m1.meter = :meterType ");
		// -> INSERT START 2018/02/15 #SP-892
        if (!"".equals(modemType)) {
            sbQueryToday.append("\nAND modem.modem_type = :modemType ");
        }
        if (!"".equals(protocolType)) {
            sbQueryToday.append("\nAND modem.protocol_type = :protocolType ");
        }
		// <- INSERT END   2018/02/15 #SP-892
        if (!"".equals(supplierId)) {
            sbQueryToday.append("\nAND   m1.supplier_id = :supplierId ");
        }
        if (!"".equals(mdsId)) {
            sbQueryToday.append("\nAND   m1.mds_id LIKE :mdsId ");
        }
        if ( !"".equals(sysId) ) {
        	sbQueryToday.append("\nAND mcu.sys_id = :sysId ");
        }
        if (sysIdList != null &&  sysIdList.size() > 0) {
        	sbQueryToday.append("\nAND mcu.sys_id IN (:sysIdList) ");
        } 
	    if ( !"".equals(lastLinkTime) ) {
	    	sbQueryToday.append("\nAND modem.last_link_time >= :lastLinkTime ");
	    }
	    if ( !"".equals(lastReadDate) ) {
	    	sbQueryToday.append("\nAND m1.last_read_date >= :lastReadDate ");
	    }
        // deviceId , deviceType 의 null 값 체크
        if (!"".equals(deviceId) && !"".equals(deviceType)) {
            if (CommonConstants.DeviceType.MCU.getCode().equals(Integer.parseInt(deviceType))) {
                sbQueryToday.append("\nAND   mcu.sys_name LIKE :deviceId ");
            } else {
                sbQueryToday.append("\nAND   modem.device_serial LIKE :deviceId ");
            }
        }
        // INSERT START SP-1051
        if (!"".equals(locationName)) {
        	sbQueryToday.append("\nAND lo.name IN ("); 
        	sbQueryToday.append(locationName);
        	sbQueryToday.append(") ");        	
        	
        }        
        // INSERT END SP-1051          
        
        sbQueryToday.append("\nAND   (cd.id IS NULL ");
        sbQueryToday.append("\n    OR cd.code != :deleteCode ");
        sbQueryToday.append("\n    OR (cd.code = :deleteCode AND m1.delete_date > :deleteDate)) ");
        sbQueryToday.append("\nAND   m1.id NOT IN ( ");
        sbQueryToday.append("\n    SELECT i1.id ");
        sbQueryToday.append("\n    FROM ( ");
        sbQueryToday.append("\n        SELECT m.id ");
        sbQueryToday.append("\n        FROM meter m ");
        sbQueryToday.append("\n             LEFT OUTER JOIN ");
        sbQueryToday.append("\n             code c ");
        sbQueryToday.append("\n             ON c.id = m.meter_status ");
        sbQueryToday.append("\n             LEFT OUTER JOIN ").append(lpTable).append(" lp ");
        sbQueryToday.append("\n             ON lp.mdev_id = m.mds_id ");
        sbQueryToday.append("\n        AND lp.yyyymmddhh BETWEEN :currStartDate AND :currEndDate ");
        sbQueryToday.append("\n        AND lp.hh < :currHour ");
        sbQueryToday.append("\n        AND lp.channel = :channel ");
        sbQueryToday.append("\n        AND lp.dst = 0 ");			// INSERT SP-1051                        
        sbQueryToday.append("\n        WHERE m.install_date <= :installDate ");
        sbQueryToday.append("\n        AND   (m.lp_interval IN (1, 5, 10, 15, 30, 60) ");
        sbQueryToday.append("\n           AND lp.value_cnt >= (60/m.lp_interval)) ");
        sbQueryToday.append("\n        AND   (c.id IS NULL ");
        sbQueryToday.append("\n            OR c.code != :deleteCode ");
        sbQueryToday.append("\n            OR (c.code = :deleteCode AND m.delete_date > :deleteDate)) ");

        // INSERT START SP-1051
        if (!"".equals(locationName)) {
        	sbQueryToday.append("\n    AND   m.location_id IN (SELECT id FROM location WHERE name IN ( ");
        	sbQueryToday.append(locationName);
        	sbQueryToday.append(")) ");
        }
        // INSERT END SP-1051                 
        
        sbQueryToday.append("\n        GROUP BY m.id,lp.yyyymmdd ");
        sbQueryToday.append("\n        HAVING COUNT(m.id) = :currHour ");
        sbQueryToday.append("\n    ) i1 ");

        sbQueryToday.append("\n    INNER JOIN ");

        sbQueryToday.append("\n    ( ");
        sbQueryToday.append("\n        SELECT m.id ");
        sbQueryToday.append("\n        FROM meter m ");
        sbQueryToday.append("\n             LEFT OUTER JOIN ");
        sbQueryToday.append("\n             code c ");
        sbQueryToday.append("\n             ON c.id = m.meter_status ");
        sbQueryToday.append("\n             LEFT OUTER JOIN ").append(lpTable).append(" lp ");
        sbQueryToday.append("\n             ON lp.mdev_id = m.mds_id ");
        sbQueryToday.append("\n             AND lp.yyyymmddhh BETWEEN :currStartDate AND :currEndDate ");
        sbQueryToday.append("\n             AND lp.hh = :currHour ");
        sbQueryToday.append("\n             AND lp.channel = :channel ");
        sbQueryToday.append("\n        		AND lp.dst = 0 ");			// INSERT SP-1051                                
        sbQueryToday.append("\n        WHERE m.install_date <= :installDate ");
        if (currMinute < 1) {
            sbQueryToday.append("\n        AND   (m.lp_interval IN (1, 5, 10, 15, 30, 60) ");
            sbQueryToday.append("\n           AND lp.value_cnt >= 1) ");
        } else {
            sbQueryToday.append("\n        AND   (m.lp_interval IN (1, 5, 10, 15, 30, 60) ");
            sbQueryToday.append("\n           AND lp.value_cnt > (:currMinute/m.lp_interval)) ");
        }
        sbQueryToday.append("\n        AND   (c.id IS NULL ");
        sbQueryToday.append("\n            OR c.code != :deleteCode ");
        sbQueryToday.append("\n            OR (c.code = :deleteCode AND m.delete_date > :deleteDate)) ");

        // INSERT START SP-1051
        if (!"".equals(locationName)) {
        	sbQueryToday.append("\n    AND   m.location_id IN (SELECT id FROM location WHERE name IN ( ");
        	sbQueryToday.append(locationName);
        	sbQueryToday.append(")) ");
        }
        // INSERT END SP-1051                 
        
        sbQueryToday.append("\n    ) i2 ON i1.id = i2.id ");
        sbQueryToday.append("\n) ");

        Query query = null;
        if (Integer.parseInt(searchEndDate) < Integer.parseInt(currDate)) {
            // 조회종료일이 현재일자보다 이전일 경우 전체일자 조회
            query = getSession().createSQLQuery(sbQueryAllPeriod.toString());
            query.setString("startDate", searchStartDate + "00");
            query.setString("endDate", searchEndDate + "23");
            query.setString("installDate", searchStartDate + CommonConstants.DefaultDate.LAST_HHMMSS.getValue());
            query.setString("meterType", meterType);
            query.setInteger("channel", channel);
            query.setInteger("period", period);
			// -> INSERT START 2018/02/15 #SP-892
	        if (!"".equals(modemType)) {
	            query.setString("modemType", modemType);
	        }
	        if (!"".equals(protocolType)) {
	            query.setString("protocolType", protocolType);
	        }
			// <- INSERT END   2018/02/15 #SP-892
            if (!"".equals(supplierId)) {
                query.setInteger("supplierId", Integer.parseInt(supplierId));
            }
            if (!"".equals(mdsId)) {
                query.setString("mdsId", "%" + mdsId + "%");
            }
            if (!"".equals(sysId)){
                query.setString("sysId", sysId );
            }
            if (sysIdList != null &&  sysIdList.size() > 0) {
                query.setParameterList("sysIdList", sysIdList);
            }
            if (!"".equals(deviceId)) {
                query.setString("deviceId", "%" + deviceId + "%");
            }


        } else {
            if (Integer.parseInt(searchStartDate) < Integer.parseInt(currDate)) {
                // 조회종료일이 현재일자이고 조회시작일이 현재일자 이전일경우 시작일~종료일전일,현재일자,현재시간 별로 조회
                query = getSession().createSQLQuery(sbQueryAllPeriodWithToday.toString());
                query.setString("startDate", searchStartDate + "00");
                query.setString("endDate", CalendarUtil.getDateWithoutFormat(searchEndDate, Calendar.DATE, -1) + "23");
                query.setString("installDate", searchStartDate + CommonConstants.DefaultDate.LAST_HHMMSS.getValue());
                query.setString("meterType", meterType);
                query.setString("currStartDate", currDate + "00");
                query.setString("currEndDate", currDate + "23");
                query.setString("currHour", currHour);
				// -> INSERT START 2018/02/15 #SP-892
		        if (!"".equals(modemType)) {
		            query.setString("modemType", modemType);
		        }
		        if (!"".equals(protocolType)) {
		            query.setString("protocolType", protocolType);
		        }
				// <- INSERT END   2018/02/15 #SP-892
                if (currMinute >= 1) {
                    query.setInteger("currMinute", currMinute);
                }
                query.setInteger("channel", channel);
                query.setInteger("period", period);
                if (!"".equals(supplierId)) {
                    query.setInteger("supplierId", Integer.parseInt(supplierId));
                }
                if (!"".equals(mdsId)) {
                    query.setString("mdsId", "%" + mdsId + "%");
                }
                if (!"".equals(sysId)){
                	query.setString("sysId", sysId);
                }
                if (sysIdList != null &&  sysIdList.size() > 0) {
                    query.setParameterList("sysIdList", sysIdList);
                }
                if (!"".equals(deviceId)) {
                    query.setString("deviceId", "%" + deviceId + "%");
                }
            } else {
                // 조회일자가 현재일자 하루일경우 현재날짜,현재시간만 조회
                query = getSession().createSQLQuery(sbQueryToday.toString());
                query.setString("installDate", searchStartDate + CommonConstants.DefaultDate.LAST_HHMMSS.getValue());
                query.setString("meterType", meterType);
                query.setString("currStartDate", currDate + "00");
                query.setString("currEndDate", currDate + "23");
                query.setString("currHour", currHour);
				// -> INSERT START 2018/02/15 #SP-892
		        if (!"".equals(modemType)) {
		            query.setString("modemType", modemType);
		        }
		        if (!"".equals(protocolType)) {
		            query.setString("protocolType", protocolType);
		        }
				// <- INSERT END   2018/02/15 #SP-892
                if (currMinute >= 1) {
                    query.setInteger("currMinute", currMinute);
                }
                query.setInteger("channel", channel);
                if (!"".equals(supplierId)) {
                    query.setInteger("supplierId", Integer.parseInt(supplierId));
                }
                if (!"".equals(mdsId)) {
                    query.setString("mdsId", "%" + mdsId + "%");
                }
                if (!"".equals(sysId)){
                	query.setString("sysId", sysId);
                }
                if (sysIdList != null &&  sysIdList.size() > 0) {
                    query.setParameterList("sysIdList", sysIdList);
                }
                if (!"".equals(deviceId)) {
                    query.setString("deviceId", "%" + deviceId + "%");
                }
            }
        }
        query.setString("deleteCode", MeterCodes.DELETE_STATUS.getCode());
        query.setString("deleteDate", searchStartDate + "235959");
	    if ( !"".equals(lastLinkTime) ) {
	    	query.setString("lastLinkTime", lastLinkTime);
	    }
	    if ( !"".equals(lastReadDate) ) {
	    	query.setString("lastReadDate", lastReadDate);
	    }
	    Integer start = 0;
	    if ( page != null){
	    	start = (page - 1) * limit;

	    }
    	query.setFirstResult(start.intValue());
    	if ( limit != null )
    		query.setMaxResults(limit.intValue());

        //return query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
        List<Object> result = query.list();

        List<Object> resultList = new ArrayList<Object>();

        for (Object obj : result) {
            HashMap<String, Object> resultMap = new HashMap<String, Object>();
            Object[] objs = (Object[]) obj;
            resultMap.put("sysId" , (String)objs[0]);

            resultMap.put("mcuId", objs[1] == null ?  null : ((Number) objs[1]).intValue());
            resultMap.put("deviceSerial", (String) objs[2]);
            resultMap.put("mdsId", (String) objs[3]);
            resultMap.put("lastReadDate", (String) objs[4]);
            resultMap.put("meterId", ((Number) objs[5]).intValue());
            resultMap.put("modemType", (String)objs[6]);
            resultMap.put("protocolType", (String)objs[7]);
            resultList.add(resultMap);
        }
        return resultList;
    } // method End
    
    // INSERT START SP-818
    @Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
    public List<Object> getProblematicMeters(Map<String, Object> params) {
        String searchStartDate = (String) params.get("searchStartDate"); //yyyymmdd
        String searchEndDate = (String) params.get("searchEndDate");  //yyyymmdd
        String meterType = StringUtil.nullToBlank(params.get("meterType"));
        String supplierId = StringUtil.nullToBlank(params.get("supplierId"));
        String locationName = StringUtil.nullToBlank(params.get("locationName"));
        String msa = StringUtil.nullToBlank(params.get("msa"));
        Integer channel = (Integer) params.get("channel");
        String today = TimeUtil.getCurrentTimeMilli(); // yyyyMMddHHmmss
        String currDate = today.substring(0, 8);

        String lpTable = CommonConstants.MeterType.valueOf(meterType).getLpTableName();

        int period = 0;
        try {
            period = TimeUtil.getDayDuration(searchStartDate, searchEndDate) + 1;
        } catch (ParseException e) {
            e.printStackTrace();
        }

        StringBuffer sbQuery = new StringBuffer();
        
        sbQuery.append("\nSELECT mcu.sys_id ");
        sbQuery.append("\n       ,mcu.id AS MCU_ID");
        sbQuery.append("\n       ,m1.mds_id");
        sbQuery.append("\n       ,m1.last_read_date");
        sbQuery.append("\n       ,m1.id  AS METER_ID");
        sbQuery.append("\n       ,m1.msa");
        sbQuery.append("\n       ,m1.gs1");
        sbQuery.append("\n       ,m1.gpiox");
        sbQuery.append("\n       ,m1.gpioy");
        sbQuery.append("\n       ,lo.name");
        sbQuery.append("\n       ,modem.device_serial");
        sbQuery.append("\n       ,modem.modem");
        sbQuery.append("\n       ,modem.fw_ver");
        sbQuery.append("\n       ,modem.last_link_time");
        sbQuery.append("\n       ,modem.comm_state");
        sbQuery.append("\n       ,COALESCE(lptmp.total, 0)");
        sbQuery.append("\nFROM meter m1 ");
        sbQuery.append("\n     LEFT OUTER JOIN location lo ON m1.location_id = lo.id");
        sbQuery.append("\n     LEFT OUTER JOIN modem modem ON m1.modem_id = modem.id");
        sbQuery.append("\n     LEFT OUTER JOIN mcu mcu ON modem.mcu_id = mcu.id");
        sbQuery.append("\n     LEFT OUTER JOIN code cd ON cd.id = m1.meter_status");
        sbQuery.append("\n     LEFT OUTER JOIN ( ");
        sbQuery.append("\n          SELECT x.id, SUM(x.daycount) as total");
        sbQuery.append("\n          FROM ( ");
        sbQuery.append("\n               SELECT m.id, COUNT(m.id) as daycount, lp.yyyymmdd");
        sbQuery.append("\n               FROM meter m");
        sbQuery.append("\n               LEFT OUTER JOIN");
        sbQuery.append("\n               ").append(lpTable).append(" lp ");
        sbQuery.append("\n               ON  lp.mdev_id = m.mds_id");
        sbQuery.append("\n               AND lp.yyyymmddhh BETWEEN :startDate AND :endDate");
        sbQuery.append("\n               AND lp.channel = :channel");
        sbQuery.append("\n               GROUP BY m.id, lp.yyyymmdd");
        sbQuery.append("\n          ) x");
        sbQuery.append("\n          WHERE x.yyyymmdd IS NOT NULL");
        sbQuery.append("\n          GROUP BY x.id");
        sbQuery.append("\n     ) lptmp ON m1.id = lptmp.id");        
        
        sbQuery.append("\n WHERE m1.install_date <= :installDate");
        sbQuery.append("\n AND m1.meter = :meterType");
        if (!"".equals(supplierId)) {
        	sbQuery.append("\n AND m1.supplier_id = :supplierId ");
        }
        if (!"".equals(locationName)) {
        	sbQuery.append("\n AND lo.name = :locationName ");
        }
        if (!"".equals(msa)) {
        	sbQuery.append("\n AND m1.msa = :msa ");
        }
        sbQuery.append("\n AND   (cd.id IS NULL");
        sbQuery.append("\n OR cd.code != :deleteCode");
        sbQuery.append("\n OR (cd.code = :deleteCode AND m1.delete_date > :deleteDate)");
        sbQuery.append("\n )");
        sbQuery.append("\n AND m1.id NOT IN ( ");
        sbQuery.append("\n      SELECT x.id ");
        sbQuery.append("\n      FROM ( ");
        sbQuery.append("\n            SELECT m.id,lp.yyyymmdd");
        sbQuery.append("\n            FROM meter m");
        sbQuery.append("\n            LEFT OUTER JOIN code c ON c.id = m.meter_status");
        sbQuery.append("\n            LEFT OUTER JOIN LP_EM lp");
        sbQuery.append("\n            ON  lp.mdev_id = m.mds_id");
        sbQuery.append("\n            AND lp.yyyymmddhh BETWEEN :startDate AND :endDate");
        sbQuery.append("\n            AND lp.channel = :channel ");
        sbQuery.append("\n            WHERE m.install_date <= :installDate ");
        sbQuery.append("\n            AND (m.lp_interval IN (1, 5, 10, 15, 30, 60)");
        sbQuery.append("\n            AND lp.value_cnt >= (60/m.lp_interval)) ");
        sbQuery.append("\n            AND   (c.id IS NULL ");
        sbQuery.append("\n                  OR c.code != :deleteCode ");
        sbQuery.append("\n                  OR (c.code = :deleteCode AND m.delete_date > :deleteDate)) ");
        sbQuery.append("\n            GROUP BY m.id, lp.yyyymmdd");
        sbQuery.append("\n            HAVING COUNT(m.id) = 24");
        sbQuery.append("\n      ) x");
        sbQuery.append("\n      WHERE x.yyyymmdd IS NOT NULL");
        sbQuery.append("\n      GROUP BY x.id");
        sbQuery.append("\n      HAVING COUNT(x.id) = :period ");        
        sbQuery.append("\n) ");        

        Query query = null;
        if (Integer.parseInt(searchEndDate) < Integer.parseInt(currDate)) {
            query = getSession().createSQLQuery(sbQuery.toString());
            query.setString("startDate", searchStartDate + "00");
            query.setString("endDate", searchEndDate + "23");
            query.setString("installDate", searchStartDate + CommonConstants.DefaultDate.LAST_HHMMSS.getValue());
            query.setString("meterType", meterType);
            query.setInteger("channel", channel);
            query.setInteger("period", period);
           
            if (!"".equals(supplierId)) {
                query.setInteger("supplierId", Integer.parseInt(supplierId));
            }
            if (!"".equals(locationName)) {
            	query.setString("locationName", locationName);
            }
            if (!"".equals(msa)) {
            	query.setString("msa", msa);
            }
            
            
        } else {
        	return null;
        }
        query.setString("deleteCode", MeterCodes.DELETE_STATUS.getCode());
        query.setString("deleteDate", searchStartDate + "235959");

	    Integer start = 0;	    
    	query.setFirstResult(start.intValue());
    	
        List<Object> result = query.list();

//    	System.out.print(query.toString());
//        System.out.print("result count = " + result.size());

        List<Object> resultList = new ArrayList<Object>();        
        for (Object obj : result) {
            HashMap<String, Object> resultMap = new HashMap<String, Object>();
            Object[] objs = (Object[]) obj;
            resultMap.put("sysId" , objs[0] == null ?  null : (String)objs[0]);
            resultMap.put("mcuId", objs[1] == null ?  null : ((Number) objs[1]).intValue());
            resultMap.put("mdsId", objs[2] == null ?  null : (String) objs[2]);
            resultMap.put("lastReadDate", objs[3] == null ?  null :(String) objs[3]);
            resultMap.put("meterId", objs[4] == null ?  null :((Number) objs[4]).intValue());
            resultMap.put("msa", objs[5] == null ?  null :(String) objs[5]);
            resultMap.put("gs1", objs[6] == null ?  null :(String) objs[6]);
            resultMap.put("gpiox", objs[7] == null ?  null :((Number) objs[7]).floatValue());
            resultMap.put("gpioy", objs[8] == null ?  null :((Number) objs[8]).floatValue());
            resultMap.put("location", objs[9] == null ?  null :(String) objs[9]);
            resultMap.put("deviceSerial", objs[10] == null ?  null :(String) objs[10]);
            resultMap.put("modem", objs[11] == null ?  null :(String) objs[11]);
            resultMap.put("fw_ver", objs[12] == null ?  null :(String) objs[12]);
            resultMap.put("last_link_time", objs[13] == null ?  null :(String) objs[13]);
            resultMap.put("comm_state", objs[14] == null ?  null :(Number) objs[14]);
            resultMap.put("lpcount", objs[15] == null ?  null :((Number) objs[15]).intValue());            
            
            resultList.add(resultMap);
        }
        return resultList;
    }    
    // INSERT END SP-818

	@Override
	public List<Map<String, Object>> getRollOutMeteringRate(String searchTime) {
		// TODO Auto-generated method stub
		logger.info("ROLL OUT : searchTime : " + searchTime);
		
		List<Map<String, Object>> ro_result = null;
		StringBuilder ro_sb = new StringBuilder();

		ro_sb.append("SELECT \n");
		ro_sb.append("	NVL(MC.SYS_ID, \n");
		ro_sb.append("  	CASE MO.MODEM_TYPE \n");
		ro_sb.append("      WHEN 'MMIU' THEN 'MBB' \n");
		ro_sb.append("      WHEN 'SubGiga' THEN 'RF' \n");
		ro_sb.append("      ELSE 'NOT MAPPED'\n");
		ro_sb.append("      END \n");
		ro_sb.append("  ) AS DCU, \n");
		ro_sb.append("  LOCATION.NAME AS DSO,\n");
		ro_sb.append("  ME.MSA AS MSA,\n");
		ro_sb.append("  COUNT(*) AS RELATED_METER,\n");
		ro_sb.append("  NVL(SUM( CASE WHEN LP.LP_COUNT=24 THEN 1 END ),0) AS SUCCESS_METER,\n");
		ro_sb.append("  COUNT(*) - NVL(SUM( CASE WHEN LP.LP_COUNT=24 THEN 1 END ),0) AS FAIL_METER,\n");
		ro_sb.append("  COUNT(*)*24 AS TOTAL_LP,\n");
		ro_sb.append("  NVL(SUM(LP.LP_COUNT),0) AS COLLECTED_LP,\n");
		ro_sb.append("  (COUNT(*)*24) - (NVL(SUM(LP.LP_COUNT),0)) AS REMAIN_LP\n");
		ro_sb.append("FROM METER ME \n");
		ro_sb.append("	LEFT OUTER JOIN MODEM MO ON ME.MODEM_ID = MO.ID\n");
		ro_sb.append("	LEFT OUTER JOIN MCU MC ON MO.MCU_ID = MC.ID \n");
		ro_sb.append("	LEFT OUTER JOIN LOCATION ON LOCATION.ID = ME.LOCATION_ID\n");
		ro_sb.append("	LEFT OUTER JOIN (\n");
		ro_sb.append("		SELECT MDEV_ID, COUNT(MDEV_ID) AS LP_COUNT FROM LP_EM \n");
		ro_sb.append("		WHERE YYYYMMDDHH BETWEEN '" + searchTime + "00' AND '" + searchTime + "23'\n");
		ro_sb.append("		AND	CHANNEL=1 \n");
		ro_sb.append("		GROUP BY MDEV_ID\n");
		ro_sb.append("	) LP ON ME.MDS_ID = LP.MDEV_ID\n");
		ro_sb.append("WHERE ME.MSA IS NOT NULL\n");
		ro_sb.append("GROUP BY NVL(MC.SYS_ID, CASE MO.MODEM_TYPE WHEN 'MMIU' THEN 'MBB' WHEN 'SubGiga' THEN 'RF' ELSE 'NOT MAPPED' END ), LOCATION.NAME, ME.MSA \n");
		ro_sb.append("ORDER BY\n");
		ro_sb.append("  DSO,\n");
		ro_sb.append("  MSA,\n");
		ro_sb.append("  DECODE(DCU, 'MBB', 0),\n");
		ro_sb.append("  DECODE(DCU, 'RF', 1),\n");
		ro_sb.append("  DECODE(DCU, 'NOT MAPPED', 2),\n");
		ro_sb.append("  DCU\n");
		
		SQLQuery ss_query = getSession().createSQLQuery(ro_sb.toString());
		ro_result =  ss_query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
		logger.info("=== ROLL OUT Result ===\n" + ro_result);

		return ro_result;
	}

	@Lob
	@Override
	public List<Map<String, Object>> get48HourNoMeteringRate(String searchTime) {
		// TODO Auto-generated method stub
		logger.info("40 Hour No Metering : searchTime : " + searchTime);
		
		List<Map<String, Object>> result = null;
		StringBuilder sb = new StringBuilder();
		sb.append("SELECT\n");
		sb.append("    l.name dso,\n");
		sb.append("    m.msa,\n");
		sb.append("    m.mds_id as meterserial,\n");
		sb.append("    m.gs1,\n");
		sb.append("    d.device_serial eui,\n");
		sb.append("    d.HW_VER,\n");
		sb.append("    d.FW_ver,\n");
		sb.append("    d.LAST_LINK_TIME,\n");
		sb.append("    m.last_read_date last_metering_time\n");
		sb.append("FROM\n");
		sb.append("    meter m,\n");
		sb.append("    modem d,\n");
		sb.append("    LOCATION l\n");
		sb.append("WHERE m.last_read_date < '"+searchTime+"235959'\n");
		sb.append("    AND m.modem_id=d.id\n");
		sb.append("    AND l.id= m.location_id\n");
		sb.append("    AND l.name !='HES'\n");
		sb.append("    AND d.modem='MMIU'\n");
		sb.append("    AND d.LAST_LINK_TIME is not null \n");
		sb.append("    AND l.name='BKK'\n");
		sb.append("ORDER BY\n");
		sb.append("    dso,\n");
		sb.append("    m.msa,\n");
		sb.append("    d.last_link_time DESC\n");
		SQLQuery query = getSession().createSQLQuery(sb.toString());
		result = query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
		return result;
	}

	@Override
	public List<Map<String, Object>> getHLSKeyErrorMeteringRate() {
		// TODO Auto-generated method stub
		logger.info("HLS Key Error Metering");
		
		List<Map<String, Object>> result = null;
		StringBuilder sb = new StringBuilder();
		sb.append("SELECT\n");
		sb.append("    l.name dso,\n");
		sb.append("    m.msa,\n");
		sb.append("    m.mds_id as meterserial,\n");
		sb.append("    m.gs1,\n");
		sb.append("    d.device_serial eui,\n");
		sb.append("    d.LAST_LINK_TIME,\n");
		sb.append("    m.last_read_date last_metering_time,\n");
		sb.append("    e.OPENTIME event_time\n");
		sb.append("FROM\n");
		sb.append("    meter m,\n");
		sb.append("    modem d,\n");
		sb.append("    (   SELECT\n");
		sb.append("            ACTIVATORID,\n");
		sb.append("            MAX(OPENTIME) opentime\n");
		sb.append("        FROM\n");
		sb.append("            EVENTALERTLOG\n");
		sb.append("        WHERE message='Metering Fail for HLS'\n");
		sb.append("            AND ACTIVATOR_TYPE='MMIU'\n");
		sb.append("        GROUP BY\n");
		sb.append("            activatorid\n");
		sb.append("    ) e,\n");
		sb.append("    LOCATION l\n");
		sb.append("WHERE m.LAST_READ_DATE LIKE '2000%'\n");
		sb.append("    AND e.activatorid = d.device_serial\n");
		sb.append("    AND m.modem_id=d.id\n");
		sb.append("    AND l.id= m.location_id\n");
		sb.append("    AND l.name !='HES'\n");
		sb.append("ORDER BY\n");
		sb.append("    m.msa,\n");
		sb.append("    d.last_link_time");
		SQLQuery query = getSession().createSQLQuery(sb.toString());
		result = query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
		return result;
	}

	@Override
	public List<Map<String, Object>> getMeterNoResponseMeteringRate() {
		// TODO Auto-generated method stub
		logger.info("Meter No Response");
		
		List<Map<String, Object>> result = null;
		StringBuilder sb = new StringBuilder();
		sb.append("SELECT\n");
		sb.append("    l.name dso,\n");
		sb.append("    m.msa,\n");
		sb.append("    m.mds_id as meterserial,\n");
		sb.append("    m.gs1,\n");
		sb.append("    d.device_serial eui,\n");
		sb.append("    d.LAST_LINK_TIME,\n");
		sb.append("    m.last_read_date last_metering_time,\n");
		sb.append("    e.OPENTIME event_time\n");
		sb.append("FROM\n");
		sb.append("    meter m,\n");
		sb.append("    modem d,\n");
		sb.append("    (   SELECT\n");
		sb.append("            ACTIVATORID,\n");
		sb.append("            MAX(OPENTIME) opentime\n");
		sb.append("        FROM\n");
		sb.append("            EVENTALERTLOG\n");
		sb.append("        WHERE message='Metering Fail for HLS'\n");
		sb.append("            AND ACTIVATOR_TYPE='MMIU'\n");
		sb.append("        GROUP BY\n");
		sb.append("            activatorid\n");
		sb.append("    ) e,\n");
		sb.append("    LOCATION l\n");
		sb.append("WHERE e.activatorid = d.device_serial\n");
		sb.append("    AND m.modem_id=d.id\n");
		sb.append("    AND l.id= m.location_id\n");
		sb.append("    AND l.name !='HES'\n");
		sb.append("ORDER BY\n");
		sb.append("    m.msa\n");
		SQLQuery query = getSession().createSQLQuery(sb.toString());
		result = query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
		return result;
	}

	@Override
	public List<Map<String, Object>> getNoValueMeteringRate() {
		// TODO Auto-generated method stub
		logger.info("No Value");
		
		List<Map<String, Object>> result = null;
		StringBuilder sb = new StringBuilder();
		sb.append("SELECT\n");
		sb.append("    l.name dso,\n");
		sb.append("    m.msa,\n");
		sb.append("    m.mds_id as meterserial,\n");
		sb.append("    m.gs1,\n");
		sb.append("    d.device_serial eui,\n");
		sb.append("    d.HW_VER,\n");
		sb.append("    d.FW_ver,\n");
		sb.append("    d.LAST_LINK_TIME,\n");
		sb.append("    m.last_read_date last_metering_time\n");
		sb.append("FROM\n");
		sb.append("    meter m,\n");
		sb.append("    modem d,\n");
		sb.append("    LOCATION l\n");
		sb.append("WHERE (m.LAST_READ_DATE like '2000%' or m.LAST_READ_DATE is null)\n");
		sb.append("    AND m.modem_id=d.id\n");
		sb.append("    AND l.id= m.location_id\n");
		sb.append("    AND l.name !='HES'\n");
		sb.append("    AND d.modem='MMIU'\n");
		sb.append("    AND d.LAST_LINK_TIME is not null \n");
		sb.append("    AND l.name='BKK'\n");
		sb.append("ORDER BY\n");
		sb.append("    dso,\n");
		sb.append("    m.msa,\n");
		sb.append("    d.last_link_time DESC\n");
		SQLQuery query = getSession().createSQLQuery(sb.toString());
		result = query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
		return result;
	}
    
	@Override
    public List<String> getFirmwareVersionList(Map<String, Object> condition) {
		String modelId = StringUtil.nullToBlank(condition.get("modelId"));
        String deviceId = StringUtil.nullToBlank(condition.get("deviceId"));
        String fwVersion = StringUtil.nullToBlank(condition.get("fwVersion"));
        String locationId = StringUtil.nullToBlank(condition.get("locationId"));
        String dcuName = StringUtil.nullToBlank(condition.get("dcuName"));
        String sType = StringUtil.nullToBlank(condition.get("sType"));
        String installStartDate = StringUtil.nullToBlank(condition.get("installStartDate"));
        String installEndtDate = StringUtil.nullToBlank(condition.get("installEndtDate"));
        String lastCommStartDate = StringUtil.nullToBlank(condition.get("lastCommStartDate"));
        String lastCommEndDate = StringUtil.nullToBlank(condition.get("lastCommEndDate"));
        String hwVer = StringUtil.nullToBlank(condition.get("hwVer"));
        
        StringTokenizer st = new StringTokenizer(locationId, ",");
        String location="";
        for(int i = 0 ; st.hasMoreTokens() ; i++){
        	location += ("'" + st.nextToken() +"',");
		}
        if(location.contains(",")){
        	location = location.substring(0, location.length()-1);
        }
        
        st = new StringTokenizer(fwVersion, ",");
        String fwVersions="";
        for(int i = 0 ; st.hasMoreTokens() ; i++){
        	fwVersions += ("'" + st.nextToken() +"',");
		}
        if(fwVersions.contains(",")){
        	fwVersions = fwVersions.substring(0, fwVersions.length()-1);
        }
        
        st = new StringTokenizer(deviceId, ", ");
        String deviceIds="";
        for(int i = 0 ; st.hasMoreTokens() ; i++){
        	deviceIds += ("(0,'" + st.nextToken() +"'),");
		}
        if(deviceIds.contains(",")){
        	deviceIds = deviceIds.substring(0, deviceIds.length()-1);
        }
        
    	StringBuffer sbQuery = new StringBuffer()
    			.append("  SELECT Distinct me.SW_VERSION                                                                                                          \n") //변경
    			.append("  FROM METER me INNER JOIN LOCATION loc                                                                                                   \n")
    			.append("  ON me.location_id = loc.id                                                                                                      \n")
    			.append("  INNER JOIN Modem mo                                                                                                      \n")
    			.append("  ON me.MODEM_ID = mo.id                                                                                                      \n")
    			.append("  LEFT OUTER JOIN MCU mu                                                                                                      \n")
    			.append("  ON mo.MCU_ID = mu.id                                                                                                       \n")
    			.append("  LEFT OUTER JOIN CODE co                                                                                                      \n")
    			.append(" ON me.meter_status = co.ID                                                                                                        \n");
    			
    			if(!modelId.equals("") && modelId!=null)
    				sbQuery.append("  WHERE me.DEVICEMODEL_ID = :modelId \n");
    			sbQuery.append("    AND me.SW_VERSION is not null ");  //추가
    			sbQuery.append("    AND me.location_id is not null "); //추가
    			if (!"".equals(sType)) {
    	            if(sType.equals(ModemIFType.RF.name()))
    	            	sbQuery.append("    AND mo.MODEM_TYPE = 'SubGiga' ");
    	            else if(sType.equals(ModemIFType.Ethernet.name())){
    	            	sbQuery.append("    AND mo.MODEM_TYPE = 'MMIU' ");
    	            	sbQuery.append("    AND (mo.PROTOCOL_TYPE = 'IP' OR mo.PROTOCOL_TYPE = 'LAN') ");
    	            }else if(sType.equals(ModemIFType.MBB.name())){
    	            	sbQuery.append("    AND mo.MODEM_TYPE = 'MMIU' ");
    	            	//sbQuery.append("    AND mo.PROTOCOL_TYPE = 'SMS' ");
    	            	sbQuery.append("    AND (mo.PROTOCOL_TYPE = 'SMS' or mo.PROTOCOL_TYPE = 'GPRS') ");
    	            }
    	        }
    			
    			if(!deviceId.equals("") && deviceId!=null)
    				sbQuery.append("  and (0, me.MDS_ID) IN ("+deviceIds+") \n");
    			if(!fwVersion.equals("") && fwVersion!=null)
    				sbQuery.append("  and me.SW_VERSION IN ("+fwVersions+") \n");
    			if(!locationId.equals("") && locationId!=null)
    				sbQuery.append("  and loc.name IN ("+location+") \n");
    			if(!dcuName.equals("") && dcuName!=null){
    				if(dcuName.equals("-"))
    					sbQuery.append(" and mu.SYS_ID is null \n");
    				else
    					sbQuery.append("  and mu.SYS_ID =:dcuName \n");
    			}
    			
    			if(!installStartDate.equals("") && installStartDate!=null)
    				sbQuery.append("  and me.INSTALL_DATE >= '"+installStartDate +"000000'\n");
    			if(!installEndtDate.equals("") && installEndtDate!=null)
    				sbQuery.append("  and me.INSTALL_DATE <= '"+installEndtDate +"125959'\n");
    			if(!lastCommStartDate.equals("") && lastCommStartDate!=null)
    				sbQuery.append("  and me.LAST_READ_DATE >= '"+lastCommStartDate +"000000'\n");
    			if(!lastCommEndDate.equals("") && lastCommEndDate!=null)
    				sbQuery.append("  and me.LAST_READ_DATE <= '"+lastCommEndDate +"125959'\n");
    			if(!hwVer.equals("") && hwVer!=null)
    				sbQuery.append("  and me.HW_VERSION = '"+hwVer+"' \n");
    			
    			sbQuery.append(" and (co.NAME <> 'Delete' or co.NAME IS NULL) \n");
    			sbQuery.append(" ORDER BY me.SW_VERSION asc \n"); // 추가
    			
    			
    			SQLQuery query = getSession().createSQLQuery(sbQuery.toString());
    			if(!modelId.equals("") && modelId!=null)
    	        	query.setParameter("modelId", Integer.parseInt(modelId));
    			/*if(!deviceId.equals("") && deviceId!=null)
    				query.setParameter("deviceId", deviceId);*/
    			if(!dcuName.equals("") && dcuName!=null && !dcuName.equals("-"))
    				query.setParameter("dcuName", dcuName);
    			/*if(!locationId.equals("") && locationId!=null)
    				query.setParameter("location", location);*/
    			return query.list();
    }
    
    @Override
    public List<String> getDeviceList(Map<String, Object> condition) {
    	String modelId = StringUtil.nullToBlank(condition.get("modelId"));
        String versions = StringUtil.nullToBlank(condition.get("versions"));
        String deviceId = StringUtil.nullToBlank(condition.get("deviceId"));
        String fwVersion = StringUtil.nullToBlank(condition.get("fwVersion"));
        String locationId = StringUtil.nullToBlank(condition.get("locationId"));
        String dcuName = StringUtil.nullToBlank(condition.get("dcuName"));
        String sType = StringUtil.nullToBlank(condition.get("sType"));
        String installStartDate = StringUtil.nullToBlank(condition.get("installStartDate"));
        String installEndtDate = StringUtil.nullToBlank(condition.get("installEndtDate"));
        String lastCommStartDate = StringUtil.nullToBlank(condition.get("lastCommStartDate"));
        String lastCommEndDate = StringUtil.nullToBlank(condition.get("lastCommEndDate"));
        String hwVer = StringUtil.nullToBlank(condition.get("hwVer"));
        
        StringTokenizer st = new StringTokenizer(locationId, ",");
        String location="";
        for(int i = 0 ; st.hasMoreTokens() ; i++){
        	location += ("'" + st.nextToken() +"',");
		}
        if(location.contains(",")){
        	location = location.substring(0, location.length()-1);
        }
        
        st = new StringTokenizer(fwVersion, ",");
        String fwVersions="";
        for(int i = 0 ; st.hasMoreTokens() ; i++){
        	fwVersions += ("'" + st.nextToken() +"',");
		}
        if(fwVersions.contains(",")){
        	fwVersions = fwVersions.substring(0, fwVersions.length()-1);
        }
        
        st = new StringTokenizer(deviceId, ", ");
        String deviceIds="";
        for(int i = 0 ; st.hasMoreTokens() ; i++){
        	deviceIds += ("(0,'" + st.nextToken() +"'),");
		}
        if(deviceIds.contains(",")){
        	deviceIds = deviceIds.substring(0, deviceIds.length()-1);
        }
        
        st = new StringTokenizer(versions, ",");
        String version ="";
    	StringBuffer sbQuery = new StringBuffer()
    			.append("  SELECT loc.name AS DSO, loc.id, NVL(mu.SYS_ID,'-') AS DCU                                                                                                      \n");
    			for(int i = 0 ; st.hasMoreTokens() ; i++){
    				version = st.nextToken();
    				sbQuery.append("  , count(case me.SW_VERSION when '" + version + "' then 1 end) || '/' || loc.id || '/"+version+"'" + "|| '/' || mu.SYS_ID  AS VERSION"+i+"   \n");
    			}
    			sbQuery.append("  FROM METER me INNER JOIN LOCATION loc                                                                                                   \n")
    			.append("  ON me.location_id = loc.id                                                                                                      \n")
    			.append("  INNER JOIN Modem mo                                                                                                      \n")
    			.append("  ON me.MODEM_ID = mo.id                                                                                                      \n")
    			.append("  LEFT OUTER JOIN MCU mu                                                                                                      \n")
    			.append("  ON mo.MCU_ID = mu.id                                                                                                       \n")
    			.append("  LEFT OUTER JOIN CODE co                                                                                                      \n")
    			.append(" ON me.meter_status = co.ID                                                                                                        \n");
    			
    			if(!modelId.equals("") && modelId!=null)
    				sbQuery.append("  WHERE me.DEVICEMODEL_ID = :modelId \n");
    			
    			if (!"".equals(sType)) {
    	            if(sType.equals(ModemIFType.RF.name()))
    	            	sbQuery.append("    AND mo.MODEM_TYPE = 'SubGiga' ");
    	            else if(sType.equals(ModemIFType.Ethernet.name())){
    	            	sbQuery.append("    AND mo.MODEM_TYPE = 'MMIU' ");
    	            	sbQuery.append("    AND (mo.PROTOCOL_TYPE = 'IP' or mo.PROTOCOL_TYPE = 'LAN') ");
    	            }else if(sType.equals(ModemIFType.MBB.name())){
    	            	sbQuery.append("    AND mo.MODEM_TYPE = 'MMIU' ");
    	            	//sbQuery.append("    AND mo.PROTOCOL_TYPE = 'SMS' ");
    	            	sbQuery.append("    AND (mo.PROTOCOL_TYPE = 'SMS' or mo.PROTOCOL_TYPE = 'GPRS')");
    	            }
    	        }
    			
    			if(!deviceId.equals("") && deviceId!=null)
    				sbQuery.append("  and (0, me.MDS_ID) IN ("+deviceIds+") \n");
    			if(!fwVersion.equals("") && fwVersion!=null)
    				sbQuery.append("  and me.SW_VERSION  IN ("+fwVersions+") \n");
    			sbQuery.append("  and me.SW_VERSION  IS NOT NULL \n");
    			if(!locationId.equals("") && locationId!=null)
    				sbQuery.append("  and loc.name IN ("+location+") \n");
    			sbQuery.append("  and loc.name IS NOT NULL \n");
    			if(!dcuName.equals("") && dcuName!=null){
    				if(dcuName.equals("-"))
    					sbQuery.append(" and mu.SYS_ID is null \n");
    				else
    					sbQuery.append("  and mu.SYS_ID =:dcuName \n");
    			}
    			
    			if(!installStartDate.equals("") && installStartDate!=null)
    				sbQuery.append("  and me.INSTALL_DATE >= '"+installStartDate +"000000'\n");
    			if(!installEndtDate.equals("") && installEndtDate!=null)
    				sbQuery.append("  and me.INSTALL_DATE <= '"+installEndtDate +"125959'\n");
    			if(!lastCommStartDate.equals("") && lastCommStartDate!=null)
    				sbQuery.append("  and me.LAST_READ_DATE >= '"+lastCommStartDate +"000000'\n");
    			if(!lastCommEndDate.equals("") && lastCommEndDate!=null)
    				sbQuery.append("  and me.LAST_READ_DATE <= '"+lastCommEndDate +"125959'\n");
    			if(!hwVer.equals("") && hwVer!=null)
    				sbQuery.append("  and me.HW_VERSION = '"+hwVer+"' \n");
    			
    			
    			sbQuery.append(" and (co.NAME <> 'Delete' or co.NAME IS NULL) \n");
    			
    			sbQuery.append("  GROUP BY loc.name, loc.id, mu.SYS_ID                                                                                                    \n");
    			sbQuery.append("  ORDER BY loc.name, mu.SYS_ID asc nulls first                                                                                                   \n");
    			
    			SQLQuery query = getSession().createSQLQuery(sbQuery.toString());
    			if(!modelId.equals("") && modelId!=null)
    	        	query.setParameter("modelId", Integer.parseInt(modelId));
    			/*if(!deviceId.equals("") && deviceId!=null)
    				query.setParameter("deviceId", deviceId);*/
    			/*if(!fwVersion.equals("") && fwVersion!=null)
    				query.setParameter("fwVersion", fwVersion);*/
    			if(!dcuName.equals("") && dcuName!=null && !dcuName.equals("-"))
    				query.setParameter("dcuName", dcuName);
    			/*if(!locationId.equals("") && locationId!=null)
    				query.setParameter("location", location);*/
    			return query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
    }
    
    @Override
    public List<String> getTargetList(Map<String, Object> condition) {
    	String modelId = StringUtil.nullToBlank(condition.get("modelId"));
        String deviceId = StringUtil.nullToBlank(condition.get("deviceId"));
        String fwVersion = StringUtil.nullToBlank(condition.get("fwVersion"));
        String locationId = StringUtil.nullToBlank(condition.get("locationId"));
        String dcuName = StringUtil.nullToBlank(condition.get("dcuName"));
        String sType = StringUtil.nullToBlank(condition.get("sType"));
        String installStartDate = StringUtil.nullToBlank(condition.get("installStartDate"));
        String installEndtDate = StringUtil.nullToBlank(condition.get("installEndtDate"));
        String lastCommStartDate = StringUtil.nullToBlank(condition.get("lastCommStartDate"));
        String lastCommEndDate = StringUtil.nullToBlank(condition.get("lastCommEndDate"));
        String hwVer = StringUtil.nullToBlank(condition.get("hwVer"));
        
        StringTokenizer st = new StringTokenizer(locationId, ",");
        String location="";
        for(int i = 0 ; st.hasMoreTokens() ; i++){
        	location += ("'" + st.nextToken() +"',");
		}
        if(location.contains(",")){
        	location = location.substring(0, location.length()-1);
        }
        
        st = new StringTokenizer(fwVersion, ",");
        String fwVersions="";
        for(int i = 0 ; st.hasMoreTokens() ; i++){
        	fwVersions += ("'" + st.nextToken() +"',");
		}
        if(fwVersions.contains(",")){
        	fwVersions = fwVersions.substring(0, fwVersions.length()-1);
        }
        
        st = new StringTokenizer(deviceId, ", ");
        String deviceIds="";
        for(int i = 0 ; st.hasMoreTokens() ; i++){
        	deviceIds += ("'" + st.nextToken() +"',");
		}
        if(deviceIds.contains(",")){
        	deviceIds = deviceIds.substring(0, deviceIds.length()-1);
        }
        
    	StringBuffer sbQuery = new StringBuffer()
    			.append("  SELECT me.mds_id||'/'||me.location_id                                                                                                      \n")
    			.append("  FROM METER me INNER JOIN LOCATION loc                                                                                                   \n")
    			.append("  ON me.location_id = loc.id                                                                                                      \n")
    			.append("  INNER JOIN Modem mo                                                                                                      \n")
    			.append("  ON me.MODEM_ID = mo.id                                                                                                      \n")
    			.append("  LEFT OUTER JOIN MCU mu                                                                                                      \n")
    			.append("  ON mo.MCU_ID = mu.id                                                                                                       \n")
    			.append("  LEFT OUTER JOIN CODE co                                                                                                      \n")
    			.append(" ON me.meter_status = co.ID                                                                                                        \n");
    			
    			if(!modelId.equals("") && modelId!=null)
    				sbQuery.append("  WHERE me.DEVICEMODEL_ID = :modelId \n");
    			
    			if (!"".equals(sType)) {
    	            if(sType.equals(ModemIFType.RF.name()))
    	            	sbQuery.append("    AND mo.MODEM_TYPE = 'SubGiga' ");
    	            else if(sType.equals(ModemIFType.Ethernet.name())){
    	            	sbQuery.append("    AND mo.MODEM_TYPE = 'MMIU' ");
    	            	sbQuery.append("    AND mo.PROTOCOL_TYPE = 'IP' ");
    	            }else if(sType.equals(ModemIFType.MBB.name())){
    	            	sbQuery.append("    AND mo.MODEM_TYPE = 'MMIU' ");
    	            	sbQuery.append("    AND mo.PROTOCOL_TYPE = 'SMS' ");
    	            }
    	        }
    			
    			if(!deviceId.equals("") && deviceId!=null)
    				sbQuery.append("  and me.MDS_ID IN ("+deviceIds+") \n");
    			if(!fwVersion.equals("") && fwVersion!=null)
    				sbQuery.append("  and me.SW_VERSION IN ("+fwVersions+") \n");
    			sbQuery.append("  and me.SW_VERSION IS NOT NULL \n");
    			if(!locationId.equals("") && locationId!=null)
    				sbQuery.append("  and loc.name IN ("+location+") \n");
    			sbQuery.append("  and loc.name IS NOT NULL \n");
    			if(!dcuName.equals("") && dcuName!=null){
    				if(dcuName.equals("-"))
    					sbQuery.append(" and mu.SYS_ID is null \n");
    				else
    					sbQuery.append("  and mu.SYS_ID =:dcuName \n");
    			}
    			
    			if(!installStartDate.equals("") && installStartDate!=null)
    				sbQuery.append("  and me.INSTALL_DATE >= '"+installStartDate +"000000'\n");
    			if(!installEndtDate.equals("") && installEndtDate!=null)
    				sbQuery.append("  and me.INSTALL_DATE <= '"+installEndtDate +"125959'\n");
    			if(!lastCommStartDate.equals("") && lastCommStartDate!=null)
    				sbQuery.append("  and me.LAST_READ_DATE >= '"+lastCommStartDate +"000000'\n");
    			if(!lastCommEndDate.equals("") && lastCommEndDate!=null)
    				sbQuery.append("  and me.LAST_READ_DATE <= '"+lastCommEndDate +"125959'\n");
    			if(!hwVer.equals("") && hwVer!=null)
    				sbQuery.append("  and me.HW_VERSION = '"+hwVer+"' \n");
    			
    			sbQuery.append(" and (co.NAME <> 'Delete' or co.NAME IS NULL) \n");
    			sbQuery.append(" ORDER BY me.location_id");
    			
    			SQLQuery query = getSession().createSQLQuery(sbQuery.toString());
    			if(!modelId.equals("") && modelId!=null)
    	        	query.setParameter("modelId", Integer.parseInt(modelId));
    			/*if(!deviceId.equals("") && deviceId!=null)
    				query.setParameter("deviceId", deviceId);*/
    			if(!dcuName.equals("") && dcuName!=null && !dcuName.equals("-"))
    				query.setParameter("dcuName", dcuName);
    			/*if(!locationId.equals("") && locationId!=null)
    				query.setParameter("location", location);*/
    			return query.list();
    }
    
    @Override
    public List<String> getDeviceListMeter(Map<String, Object> condition) {
    	String modelId = StringUtil.nullToBlank(condition.get("modelId"));
    	String versions = StringUtil.nullToBlank(condition.get("versions"));
    	String deviceId = StringUtil.nullToBlank(condition.get("deviceId"));
    	String fwVersion = StringUtil.nullToBlank(condition.get("fwVersion"));
    	String locationId = StringUtil.nullToBlank(condition.get("locationId"));
    	String dcuName = StringUtil.nullToBlank(condition.get("dcuName"));
    	String installStartDate = StringUtil.nullToBlank(condition.get("installStartDate"));
    	String installEndtDate = StringUtil.nullToBlank(condition.get("installEndtDate"));
    	String lastCommStartDate = StringUtil.nullToBlank(condition.get("lastCommStartDate"));
    	String lastCommEndDate = StringUtil.nullToBlank(condition.get("lastCommEndDate"));
    	String hwVer = StringUtil.nullToBlank(condition.get("hwVer"));
    	Boolean chkParent = condition.get("chkParent") == null ? true : (Boolean)condition.get("chkParent");
    	String deviceModelName = "NAMR-P214SR";
    	/** DeviceModel 정보 추출 */
    	DeviceModel modemModel = deviceModelDao.findByCondition("name", deviceModelName);
    	
    	StringTokenizer st = new StringTokenizer(locationId, ",");
    	String location="";
    	
    	for(int i = 0 ; st.hasMoreTokens() ; i++){
    		location += ("'" + st.nextToken() +"',");
    	}
    	if(location.contains(",")){
    		location = location.substring(0, location.length()-1);
    	}
        //sp-1004 Devices Id (Excel Search 위한 meter ID)
	    st = new StringTokenizer(deviceId, ", ");
        String deviceIds="";
        for(int i = 0 ; st.hasMoreTokens() ; i++){
        	deviceIds += ("(0,'" + st.nextToken() +"'),");
		}
        if(deviceIds.contains(",")){
        	deviceIds = deviceIds.substring(0, deviceIds.length()-1);
        }
    	
    	StringBuffer sbQuery = new StringBuffer();
    	SQLQuery query = getSession().createSQLQuery(sbQuery.toString());
    	
    	if(!chkParent){ // fw 버전이 fwVersion인 것 모두 출력 (체크해제)
    		sbQuery.append("  select  loc.name as DSO, loc.id DSO_ID, NVL(mu.sys_id,'-') as DCU         \n");
    		sbQuery.append("  , count(me.mds_id) as METERCOUNT  \n");
    		sbQuery.append("   from meter me\n");
    		sbQuery.append("   inner join location loc											\n");
    		sbQuery.append("  on me.location_id = loc.id                                                                                                      \n");
    		sbQuery.append("  left outer join modem mo                                                                                                     \n");
    		sbQuery.append("  on me.modem_id = mo.id                                                                                                      \n");
    		sbQuery.append("  left outer join mcu mu                                                                                                     \n");
    		sbQuery.append("  on mo.mcu_id = mu.id                                                                                                      \n");
    		sbQuery.append("  left outer join devicemodel me_dm                                                                                                     \n");
    		sbQuery.append("  on me.devicemodel_id = me_dm.id                                                                                                      \n");
    		sbQuery.append("  left outer join devicemodel mo_dm                                                                                                      \n");
    		sbQuery.append("  on mo.devicemodel_id = mo_dm.id                                                                                                      \n");
    		sbQuery.append("  where me.sw_version is not null \n");
    		if(!deviceId.equals("") && deviceId!=null)
    			sbQuery.append("  and (0, me.mds_id) in ("+deviceIds+") \n");
    		sbQuery.append("  and me.sw_version = '"+ fwVersion +"'"); // Command를 내리고자 하는 미터 펌웨어 버전
    		sbQuery.append("  and me_dm.id = "+ modelId +" \n"); // Command를 내리고자 하는 미터 모델명
    		sbQuery.append("  and mo.modem_type = 'SubGiga' \n"); // SubGiga 모뎀만 출력 
    		sbQuery.append("  and me.meter_status not in (select id from code where name in ('BreakDown', 'Break Down', 'Delete', 'Repair')) \n");
    		sbQuery.append("  and me.modem_id is not null \n");
    		sbQuery.append("  and loc.name is not null \n");
    		//검색조건
    		if(!locationId.equals("") && locationId!=null)
    			sbQuery.append("  and loc.name in ("+location+") \n");
    		if(!dcuName.equals("") && dcuName!=null){
    			if(dcuName.equals("-"))
    				sbQuery.append(" and mu.sys_id is null \n");
    			else
    				sbQuery.append("  and mu.sys_id =:dcuName \n");
    		}
    		sbQuery.append("  group by loc.name, loc.id, mu.sys_id                                      \n");
    		sbQuery.append("  order by mu.sys_id asc nulls first                                                                                                   \n");
    	} else{ // fw 버전이 fwVersion이 아닌 것 중 부모가 fwVersion인 것 (체크했을때)
    		sbQuery.append(" WITH target_list as (         \n");
    		sbQuery.append("  select  me.location_id, me.mds_id, me_dm.id as metermodel_id, me_dm.name as meter_model, me.meter_status, me.sw_version , mo.modem_id as parent_modem_id,         \n");
    		sbQuery.append("  				mo.id as modem_id, mo.device_serial, mo.fw_ver, mo_dm.name as modem_model, loc.name as loc_name, loc.id as loc_id, mu.sys_id  \n");
    		sbQuery.append("   from meter me\n");
    		sbQuery.append("   inner join location loc											\n");
    		sbQuery.append("  on me.location_id = loc.id                                                                                                      \n");
    		sbQuery.append("  left outer join modem mo                                                                                                     \n");
    		sbQuery.append("  on me.modem_id = mo.id                                                                                                      \n");
    		sbQuery.append("  left outer join mcu mu                                                                                                     \n");
    		sbQuery.append("  on mo.mcu_id = mu.id                                                                                                      \n");
    		sbQuery.append("  left outer join devicemodel me_dm                                                                                                     \n");
    		sbQuery.append("  on me.devicemodel_id = me_dm.id                                                                                                      \n");
    		sbQuery.append("  left outer join devicemodel mo_dm                                                                                                      \n");
    		sbQuery.append("  on mo.devicemodel_id = mo_dm.id                                                                                                      \n");
    		sbQuery.append("  where me_dm.id = "+ modelId +" \n"); // Command를 내리고자 하는 미터 모델명
    		if(!deviceId.equals("") && deviceId!=null)
    			sbQuery.append("  and (0, me.mds_id) in ("+deviceIds+") \n");
    		sbQuery.append("  and mo.modem_type = 'SubGiga' \n"); // SubGiga 모뎀만 출력 
    		sbQuery.append("  and me.meter_status not in (select id from code where name in ('BreakDown', 'Break Down', 'Delete', 'Repair')) \n");
    		sbQuery.append("  and me.sw_version is not null \n");
    		sbQuery.append("  and me.modem_id is not null \n");
    		sbQuery.append("  and loc.name is not null \n");
    		sbQuery.append("  ) \n");
    		sbQuery.append("    select count(mds_id) METERCOUNT, loc_name as DSO,   loc_id DSO_ID,  NVL(sys_id, '-') as DCU  \n");
    		sbQuery.append("    from target_list  \n");
    		sbQuery.append("    where modem_id in ( \n");
    		sbQuery.append("        select distinct(parent_modem_id) \n");
    		sbQuery.append("        from target_list \n");
    		sbQuery.append("        where sw_version < to_number('"+fwVersion+"') \n");
    		sbQuery.append(" ) \n");
    		sbQuery.append("  and sw_version = '"+ fwVersion +"' \n");
    		//sbQuery.append("  and metermodel_id = "+ modelId +" \n");
    		// 검색조건
    		if(!locationId.equals("") && locationId!=null)
    			sbQuery.append("  and loc_name in ("+location+") \n");
    		if(!dcuName.equals("") && dcuName!=null){
    			if(dcuName.equals("-"))
    				sbQuery.append(" and sys_id is null \n");
    			else
    				sbQuery.append("  and sys_id =:dcuName \n");
    		}
    		sbQuery.append("  group by sys_id, loc_name, loc_id \n");
    		sbQuery.append("  order by sys_id asc nulls first                                                                                                   \n");
    		
    	}
    	
    	query = getSession().createSQLQuery(sbQuery.toString());
    	
    	if(!dcuName.equals("") && dcuName!=null && !dcuName.equals("-"))
    		query.setParameter("dcuName", dcuName);
    	/*if(!modelId.equals("") && modelId!=null)
    		query.setParameter("modelId", Integer.parseInt(modelId));*/
    	
    	return query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
    }
    
    @Override
    public List<String> getTargetListMeter(Map<String, Object> condition) {
    	String modelId = StringUtil.nullToBlank(condition.get("modelId"));
    	String versions = StringUtil.nullToBlank(condition.get("versions"));
    	String deviceId = StringUtil.nullToBlank(condition.get("deviceId"));
    	String fwVersion = StringUtil.nullToBlank(condition.get("fwVersion"));
    	String locationId = StringUtil.nullToBlank(condition.get("locationId"));
    	String dcuName = StringUtil.nullToBlank(condition.get("dcuName"));
    	String locationName = StringUtil.nullToBlank(condition.get("locationName"));
    	String installStartDate = StringUtil.nullToBlank(condition.get("installStartDate"));
    	String installEndtDate = StringUtil.nullToBlank(condition.get("installEndtDate"));
    	String lastCommStartDate = StringUtil.nullToBlank(condition.get("lastCommStartDate"));
    	String lastCommEndDate = StringUtil.nullToBlank(condition.get("lastCommEndDate"));
    	String hwVer = StringUtil.nullToBlank(condition.get("hwVer"));
    	Boolean chkParent = condition.get("chkParent") == null ? true : (Boolean)condition.get("chkParent");
    	Boolean dcuall = condition.get("dcuall") == null ? true : (Boolean)condition.get("dcuall");
    	String deviceModelName = "NAMR-P214SR";
    	/** DeviceModel 정보 추출 */
    	DeviceModel modemModel = deviceModelDao.findByCondition("name", deviceModelName);
    	
    	StringTokenizer st = new StringTokenizer(locationId, ",");
    	String location="";
    	for(int i = 0 ; st.hasMoreTokens() ; i++){
    		location += ("'" + st.nextToken() +"',");
    	}
    	if(location.contains(",")){
    		location = location.substring(0, location.length()-1);
    	}
    	
    	st = new StringTokenizer(fwVersion, ",");
    	String fwVersions="";
    	for(int i = 0 ; st.hasMoreTokens() ; i++){
    		fwVersions += ("'" + st.nextToken() +"',");
    	}
    	if(fwVersions.contains(",")){
    		fwVersions = fwVersions.substring(0, fwVersions.length()-1);
    	}
    	
    	st = new StringTokenizer(dcuName, ",");
    	String dcuNames="";
    	for(int i = 0 ; st.hasMoreTokens() ; i++){
    		dcuNames += ("'" + st.nextToken() +"',");
    	}
    	if(dcuNames.contains(",")){
    		dcuNames = dcuNames.substring(0, dcuNames.length()-1);
    	}

    	st = new StringTokenizer(locationName, ",");
    	String locationNames="";
    	for(int i = 0 ; st.hasMoreTokens() ; i++){
    		locationNames += ("'" + st.nextToken() +"',");
    	}
    	if(locationNames.contains(",")){
    		locationNames = locationNames.substring(0, locationNames.length()-1);
    	}
    	
    	st = new StringTokenizer(deviceId, ",");
    	String deviceIds="";
    	for(int i = 0 ; st.hasMoreTokens() ; i++){
    		deviceIds += ("'" + st.nextToken() +"',");
    	}
    	if(deviceIds.contains(",")){
    		deviceIds = deviceIds.substring(0, deviceIds.length()-1);
    	}
    	
    	StringBuffer sbQuery = new StringBuffer();

    	if(!chkParent){ // fw 버전이 fwVersion인 것 모두 출력 (체크해제)
    		sbQuery.append("  SELECT me.MDS_ID||'/'||loc.id          \n");
    	    sbQuery.append("   from meter me\n");
    	    sbQuery.append("   inner join location loc											\n");
    	    sbQuery.append("  on me.location_id = loc.id                                                                                                      \n");
    	    sbQuery.append("  left outer join modem mo                                                                                                     \n");
    	    sbQuery.append("  on me.modem_id = mo.id                                                                                                      \n");
    	    sbQuery.append("  left outer join mcu mu                                                                                                     \n");
    	    sbQuery.append("  on mo.mcu_id = mu.id                                                                                                      \n");
    	    sbQuery.append("  left outer join devicemodel me_dm                                                                                                     \n");
    	    sbQuery.append("  on me.devicemodel_id = me_dm.id                                                                                                      \n");
    	    sbQuery.append("  left outer join devicemodel mo_dm                                                                                                      \n");
    	    sbQuery.append("  on mo.devicemodel_id = mo_dm.id                                                                                                      \n");
    	    sbQuery.append("  where me.sw_version is not null \n");
    	    sbQuery.append("  and me.sw_version = '"+ fwVersion +"'"); // Command를 내리고자 하는 미터 펌웨어 버전
    	    sbQuery.append("  and me_dm.id = "+ modelId +" \n"); // Command를 내리고자 하는 미터 모델명
    	    sbQuery.append("  and mo.modem_type = 'SubGiga' \n"); // SubGiga 모뎀만 출력 
    	    sbQuery.append("  and me.meter_status not in (select id from code where name in ('BreakDown', 'Break Down', 'Delete', 'Repair')) \n");
    	    sbQuery.append("  and me.modem_id is not null \n");
    	    sbQuery.append("  and loc.name is not null \n");
    	    //조건
    	    /*if(!locationId.equals("") && locationId!=null)
    	    	sbQuery.append("  and loc.name in ("+location+") \n");*/
    	    if(!dcuall){
        		if(!dcuName.equals("") && dcuName!=null){
        			if(dcuName.equals("-"))
        				sbQuery.append(" and mu.SYS_ID is null \n");
        			else
        				sbQuery.append("  and mu.SYS_ID IN ("+dcuNames+") \n");
        		}
        	}
    	    sbQuery.append("  and loc.id IN ("+locationNames+") \n");
    	    
    	   /* if(!deviceId.equals("") && deviceId!=null)
        		sbQuery.append("  and me.MDS_ID IN ("+deviceIds+") \n");
    	    if(!fwVersion.equals("") && fwVersion!=null)
        		sbQuery.append("  and me.SW_VERSION IN ("+fwVersions+") \n");
        		sbQuery.append("  and me.SW_VERSION IS NOT NULL \n");*/
        		
    	    } else{ // fw 버전이 fwVersion이 아닌 것 중 부모가 fwVersion인 것 (체크했을때)
    	    		sbQuery.append(" WITH target_list as (         \n");
    	    		sbQuery.append("  select  me.location_id, me.mds_id, me_dm.id as metermodel_id, me_dm.name as meter_model, me.meter_status, me.sw_version , mo.modem_id as parent_modem_id,         \n");
    	    		sbQuery.append("  				mo.id as modem_id, mo.device_serial, mo.fw_ver, mo_dm.name as modem_model, loc.name as loc_name, loc.id as loc_id, mu.sys_id  \n");
    	    		sbQuery.append("   from meter me\n");
    	    		sbQuery.append("   inner join location loc											\n");
    	    		sbQuery.append("  on me.location_id = loc.id                                                                                                      \n");
    	    		sbQuery.append("  left outer join modem mo                                                                                                     \n");
    	    		sbQuery.append("  on me.modem_id = mo.id                                                                                                      \n");
    	    		sbQuery.append("  left outer join mcu mu                                                                                                     \n");
    	    		sbQuery.append("  on mo.mcu_id = mu.id                                                                                                      \n");
    	    		sbQuery.append("  left outer join devicemodel me_dm                                                                                                     \n");
    	    		sbQuery.append("  on me.devicemodel_id = me_dm.id                                                                                                      \n");
    	    		sbQuery.append("  left outer join devicemodel mo_dm                                                                                                      \n");
    	    		sbQuery.append("  on mo.devicemodel_id = mo_dm.id                                                                                                      \n");
    	    		sbQuery.append("  where me_dm.id = "+ modelId +" \n"); // Command를 내리고자 하는 미터 모델명
    	    		sbQuery.append("  and mo.modem_type = 'SubGiga' \n"); // SubGiga 모뎀만 출력 
    	    		sbQuery.append("  and me.meter_status not in (select id from code where name in ('BreakDown', 'Break Down', 'Delete', 'Repair')) \n");
    	    		sbQuery.append("  and me.sw_version is not null \n");
    	    		sbQuery.append("  and me.modem_id is not null \n");
    	    		sbQuery.append("  and loc.name is not null \n");
    	    		sbQuery.append("  ) \n");
    	    		sbQuery.append("    SELECT mds_id||'/'||loc_id   \n");
    	    		sbQuery.append("    from target_list  \n");
    	    		sbQuery.append("    where modem_id in ( \n");
    	    		sbQuery.append("        select distinct(parent_modem_id) \n");
    	    		sbQuery.append("        from target_list \n");
    	    		sbQuery.append("        where sw_version < to_number('"+fwVersion+"') \n");
    	    		sbQuery.append(" ) \n");
    	    		sbQuery.append("  and sw_version = '"+ fwVersion +"' \n");
    	    		// 조건
    	    		/*if(!locationId.equals("") && locationId!=null)
    	    			sbQuery.append("  and loc_name in ("+location+") \n");*/
    	    		if(!dcuall){
    	        		if(!dcuName.equals("") && dcuName!=null){
    	        			if(dcuName.equals("-"))
    	        				sbQuery.append(" and sys_id is null \n");
    	        			else
    	        				sbQuery.append("  and sys_id IN ("+dcuNames+") \n");
    	        		}
    	        	}
    	    		sbQuery.append("  and loc_id IN ("+locationNames+") \n");
    	    		/*if(!deviceId.equals("") && deviceId!=null)
    	        		sbQuery.append("  and mds_id IN ("+deviceIds+") \n");
    	    		if(!fwVersion.equals("") && fwVersion!=null)
    	        		sbQuery.append("  and sw_version IN ("+fwVersions+") \n");
    	        		sbQuery.append("  and sw_version IS NOT NULL \n");*/
    	        	
    	}
    	
    	SQLQuery query = getSession().createSQLQuery(sbQuery.toString());
    	
    	return query.list();
    }
    
    /* Meter Clone on,off 팝업 리스트 조회 - metersearchgrid 와 분기 */
    @Override
    public List<Object> getMeterListCloneonoff(Map<String, Object> condition) {
    	String modelId = StringUtil.nullToBlank(condition.get("sModel"));
    	//String versions = StringUtil.nullToBlank(condition.get("versions"));
    	String deviceId = StringUtil.nullToBlank(condition.get("sMdsId"));
    	String fwVersion = StringUtil.nullToBlank(condition.get("sFwVersion"));
    	String locationId = StringUtil.nullToBlank(condition.get("sLocationId"));
    	String dcuName = StringUtil.nullToBlank(condition.get("sMcuName"));
    	String installStartDate = StringUtil.nullToBlank(condition.get("sInstallStartDate"));
    	String installEndtDate = StringUtil.nullToBlank(condition.get("sInstallEndDate"));
    	String lastCommStartDate = StringUtil.nullToBlank(condition.get("sLastcommStartDate"));
    	String lastCommEndDate = StringUtil.nullToBlank(condition.get("sLastcommEndDate"));
    	String hwVer = StringUtil.nullToBlank(condition.get("sHwVersion"));
    	Boolean chkParent = condition.get("chkParent") == null ? true : (Boolean)condition.get("chkParent");
    	String deviceModelName = "NAMR-P214SR";
    	/** DeviceModel 정보 추출 */
    	DeviceModel modemModel = deviceModelDao.findByCondition("name", deviceModelName);
    	
    	StringTokenizer st = new StringTokenizer(locationId, ",");
    	String location="";
    	
    	for(int i = 0 ; st.hasMoreTokens() ; i++){
    		location += ("'" + st.nextToken() +"',");
    	}
    	if(location.contains(",")){
    		location = location.substring(0, location.length()-1);
    	}
    	
        //sp-1004 Devices Id (Excel Search 위한 meter ID)
	    st = new StringTokenizer(deviceId, ", ");
        String deviceIds="";
        for(int i = 0 ; st.hasMoreTokens() ; i++){
        	deviceIds += ("'" + st.nextToken() +"',");
		}
        if(deviceIds.contains(",")){
        	deviceIds = deviceIds.substring(0, deviceIds.length()-1);
        }
    	
    	StringBuffer sbQuery = new StringBuffer();
    	SQLQuery query = getSession().createSQLQuery(sbQuery.toString());
    	
    	if(!chkParent){ // fw 버전이 fwVersion인 것 모두 출력 (체크해제)
    		sbQuery.append("  select me.MDS_ID AS meterMds, me_dm.name AS modelName           \n");
    		sbQuery.append("   , me.LAST_READ_DATE  AS lastCommDate, me.ID  AS meterId     \n");
    		sbQuery.append("   , me.SW_VERSION AS swVer, me.HW_VERSION AS hwVer     \n");
    		sbQuery.append("   from meter me\n");
    		sbQuery.append("   inner join location loc											\n");
    		sbQuery.append("  on me.location_id = loc.id                                                                                                      \n");
    		sbQuery.append("  left outer join modem mo                                                                                                     \n");
    		sbQuery.append("  on me.modem_id = mo.id                                                                                                      \n");
    		sbQuery.append("  left outer join mcu mu                                                                                                     \n");
    		sbQuery.append("  on mo.mcu_id = mu.id                                                                                                      \n");
    		sbQuery.append("  left outer join devicemodel me_dm                                                                                                     \n");
    		sbQuery.append("  on me.devicemodel_id = me_dm.id                                                                                                      \n");
    		sbQuery.append("  left outer join devicemodel mo_dm                                                                                                      \n");
    		sbQuery.append("  on mo.devicemodel_id = mo_dm.id                                                                                                      \n");
    		sbQuery.append("  where me.sw_version is not null \n");
    		if(!deviceId.equals("") && deviceId!=null)
    			sbQuery.append("  and me.mds_id in ("+deviceIds+") \n");
    		sbQuery.append("  and me.sw_version = '"+ fwVersion +"'"); // Command를 내리고자 하는 미터 펌웨어 버전
    		sbQuery.append("  and me_dm.id = "+ modelId +" \n"); // Command를 내리고자 하는 미터 모델명
    		sbQuery.append("  and mo.modem_type = 'SubGiga' \n"); // SubGiga 모뎀만 출력 
    		sbQuery.append("  and me.meter_status not in (select id from code where name in ('BreakDown', 'Break Down', 'Delete', 'Repair')) \n");
    		sbQuery.append("  and me.modem_id is not null \n");
    		//검색조건
    		if(!locationId.equals("") && locationId!=null)
    			sbQuery.append("  and me.location_id in ("+locationId+") \n");
    		if(!dcuName.equals("") && dcuName!=null){
    			if(dcuName.equals("-"))
    				sbQuery.append(" and mu.sys_id is null \n");
    			else
    				sbQuery.append("  and mu.sys_id =:dcuName \n");
    		}
    	} else{ // fw 버전이 fwVersion이 아닌 것 중 부모가 fwVersion인 것 (체크했을때)
    		sbQuery.append(" WITH target_list as (         \n");
    		sbQuery.append("  select  me.location_id, me.mds_id, me_dm.id as metermodel_id, me_dm.name as meter_model, me.meter_status, me.sw_version , mo.modem_id as parent_modem_id,   me.last_read_date,      \n");
    		sbQuery.append("  				mo.id as modem_id, mo.device_serial, mo.fw_ver, mo_dm.name as modem_model, loc.name as loc_name, loc.id as loc_id, mu.sys_id  \n");
    		sbQuery.append("   from meter me\n");
    		sbQuery.append("   inner join location loc											\n");
    		sbQuery.append("  on me.location_id = loc.id                                                                                                      \n");
    		sbQuery.append("  left outer join modem mo                                                                                                     \n");
    		sbQuery.append("  on me.modem_id = mo.id                                                                                                      \n");
    		sbQuery.append("  left outer join mcu mu                                                                                                     \n");
    		sbQuery.append("  on mo.mcu_id = mu.id                                                                                                      \n");
    		sbQuery.append("  left outer join devicemodel me_dm                                                                                                     \n");
    		sbQuery.append("  on me.devicemodel_id = me_dm.id                                                                                                      \n");
    		sbQuery.append("  left outer join devicemodel mo_dm                                                                                                      \n");
    		sbQuery.append("  on mo.devicemodel_id = mo_dm.id                                                                                                      \n");
    		sbQuery.append("  where me_dm.id = "+ modelId +" \n"); // Command를 내리고자 하는 미터 모델명
    		if(!deviceId.equals("") && deviceId!=null)
    			sbQuery.append("  and me.mds_id in ("+deviceIds+") \n");
    		sbQuery.append("  and mo.modem_type = 'SubGiga' \n"); // SubGiga 모뎀만 출력 
    		sbQuery.append("  and me.meter_status not in (select id from code where name in ('BreakDown', 'Break Down', 'Delete', 'Repair')) \n");
    		sbQuery.append("  and me.sw_version is not null \n");
    		sbQuery.append("  and me.modem_id is not null \n");
    		sbQuery.append("  ) \n");
    		sbQuery.append("    select mds_id as meterMds, meter_model as modelName, sw_version as swVer,  last_read_date as lastCommDate \n");
    		sbQuery.append("    from target_list  \n");
    		sbQuery.append("    where modem_id in ( \n");
    		sbQuery.append("        select distinct(parent_modem_id) \n");
    		sbQuery.append("        from target_list \n");
    		sbQuery.append("        where sw_version < to_number('"+fwVersion+"') \n");
    		sbQuery.append(" ) \n");
    		sbQuery.append("  and sw_version = '"+ fwVersion +"'");
    		//sbQuery.append("  and metermodel_id = "+ modelId +" \n");
    		// 검색조건
    		if(!locationId.equals("") && locationId!=null)
    			sbQuery.append("  and loc_id in ("+locationId+") \n");
    		if(!dcuName.equals("") && dcuName!=null){
    			if(dcuName.equals("-"))
    				sbQuery.append(" and sys_id is null \n");
    			else
    				sbQuery.append("  and sys_id =:dcuName \n");
    		}
    		
    	}
    	
    	query = getSession().createSQLQuery(sbQuery.toString());
    	
    	if(!dcuName.equals("") && dcuName!=null && !dcuName.equals("-"))
    		query.setParameter("dcuName", dcuName);
    	/*if(!modelId.equals("") && modelId!=null)
    		query.setParameter("modelId", Integer.parseInt(modelId));*/
    	
    	return query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
    }
    
    /* SP-1050
     * @see com.aimir.dao.device.MeterDao#getMsaListByLocationName(java.lang.String)
     */
    @Override 
    public List<Object> getMsaListByLocationName(String locationName)
    {
        StringBuffer sbQuery = new StringBuffer();
        sbQuery.append("select distinct msa  from meter me inner join location loc on me.location_id = loc.id \n");
        sbQuery.append("      where loc.name =:locationName order by msa");
        
        SQLQuery query = getSession().createSQLQuery(sbQuery.toString());
        query.setString("locationName", locationName);
        return query.list();
    }
    @Override
    public void updateModemIdNull(int id) {
        StringBuffer sb = new StringBuffer();
        sb.append("UPDATE METER \n");
        sb.append("SET MODEM_ID =NULL \n");
        sb.append("WHERE ID = :id");
        Query query = getSession().createQuery(sb.toString());
        query.setInteger("id", id);
        query.executeUpdate();
    }

  //sp-1028
	@Override
	public List<Map<String, Object>> getValidMeterList(Map<String, Object> condition) {
		String sType = StringUtil.nullToBlank(condition.get("sType"));
		String modelId = StringUtil.nullToBlank(condition.get("modelId"));
        String deviceList = StringUtil.nullToBlank(condition.get("deviceList"));
        String supplierId = StringUtil.nullToBlank(condition.get("supplierId"));
		
        StringTokenizer st = new StringTokenizer(deviceList, ", ");
        String deviceIds="";
        for(int i = 0 ; st.hasMoreTokens() ; i++){
        	deviceIds += ("(0,'" + st.nextToken() +"'),");
		}
        if(deviceIds.contains(",")){
        	deviceIds = deviceIds.substring(0, deviceIds.length()-1);
        }
        
    	StringBuffer sbQuery = new StringBuffer()
    			.append("  SELECT me.MDS_ID DEVICE_ID, me.SW_VERSION VERSION 		\n")
    			.append("  FROM METER me INNER JOIN LOCATION loc 	\n")
    			.append("  ON me.location_id = loc.id 				\n")
    			.append("  INNER JOIN Modem mo        				\n")
    			.append("  ON me.MODEM_ID = mo.id 					\n")
    			.append("  LEFT OUTER JOIN MCU mu 					\n")
    			.append("  ON mo.MCU_ID = mu.id 					\n")
    			.append("  LEFT OUTER JOIN CODE co 					\n")
    			.append(" ON me.meter_status = co.ID   				\n");
    			
		sbQuery.append("  WHERE me.DEVICEMODEL_ID = :modelId \n");
		sbQuery.append("    AND me.SW_VERSION is not null "); // 추가
		sbQuery.append("    AND me.location_id is not null "); // 추가
		sbQuery.append("  	AND me.supplier_id = :supplierId 	\n");

		if (!"".equals(sType)) {
			if (sType.equals(ModemIFType.RF.name()))
				sbQuery.append("    AND mo.MODEM_TYPE = 'SubGiga' ");
			else if (sType.equals(ModemIFType.Ethernet.name())) {
				sbQuery.append("    AND mo.MODEM_TYPE = 'MMIU' ");
				sbQuery.append("    AND (mo.PROTOCOL_TYPE = 'IP' OR mo.PROTOCOL_TYPE = 'LAN') ");
			} else if (sType.equals(ModemIFType.MBB.name())) {
				sbQuery.append("    AND mo.MODEM_TYPE = 'MMIU' ");
				sbQuery.append("    AND (mo.PROTOCOL_TYPE = 'SMS' or mo.PROTOCOL_TYPE = 'GPRS') ");
			}
		}
		sbQuery.append("  and (0, me.MDS_ID) IN (" + deviceIds + ") 			\n");
		
		sbQuery.append(" and (co.NAME <> 'Delete' or co.NAME IS NULL) 	\n");
		sbQuery.append(" ORDER BY me.SW_VERSION asc \n"); // 추가

		SQLQuery query = getSession().createSQLQuery(sbQuery.toString());
		query.setParameter("modelId", Integer.parseInt(modelId));
		query.setParameter("supplierId", Integer.parseInt(supplierId));

		return query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
	}

	@Override
	public List<Map<String, Object>> getParentDevice(Map<String, Object> condition) {
		String modelId = StringUtil.nullToBlank(condition.get("modelId"));
    	String deviceList = StringUtil.nullToBlank(condition.get("deviceId"));
    	String fwVersion = StringUtil.nullToBlank(condition.get("fwVersion"));
    	String supplierId = StringUtil.nullToBlank(condition.get("supplierId"));
		
		StringTokenizer st = new StringTokenizer(deviceList, ", ");
		String deviceIds = "";
		for (int i = 0; st.hasMoreTokens(); i++) {
			deviceIds += ("(0,'" + st.nextToken() + "'),");
		}
		if (deviceIds.contains(",")) {
			deviceIds = deviceIds.substring(0, deviceIds.length() - 1);
		}

		StringBuffer sbQuery = new StringBuffer();
		
		sbQuery.append("\nselect meter.mds_id deviceid, modem.id, modem.device_serial, modem.fw_ver, parent_meter.mds_id parent_id, parent_meter.sw_version parent_ver " );
		sbQuery.append("\nfrom meter " );
		sbQuery.append("\n    left outer join modem " );
		sbQuery.append("\n    	on meter.modem_id = modem.id " );
		sbQuery.append("\n    left outer join meter parent_meter " );
		sbQuery.append("\n    	on modem.modem_id = parent_meter.modem_id  " );
		sbQuery.append("\nwhere (0, meter.mds_id) in  " );
		sbQuery.append("\n    ( "+ deviceIds +" ) " );
		sbQuery.append("\n    and meter.SUPPLIER_ID = :supplierId ");
		sbQuery.append("\n    and meter.DEVICEMODEL_ID = :modelId ");
		sbQuery.append("\n    and meter.sw_version < to_number('" +fwVersion+ "') " );
		sbQuery.append("\n    and modem.modem_type = 'SubGiga' "); // SubGiga 모뎀만 출력 
		sbQuery.append("\n    and meter.meter_status not in (select id from code where name in ('BreakDown', 'Break Down', 'Delete', 'Repair')) ");
		sbQuery.append("\n    and (parent_meter.sw_version < to_number('" +fwVersion+ "') or modem.modem_id is null)   " );

		SQLQuery query = getSession().createSQLQuery(sbQuery.toString());
		query.setParameter("modelId", Integer.parseInt(modelId));
		query.setParameter("supplierId", Integer.parseInt(supplierId));
//		query.setParameter("fwVersion", fwVersion);

		return query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
	}
	
    @Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
    public List<Object> getMissingMetersForRecollectSLA(Map<String, Object> params) {
        String searchStartDate = (String) params.get("searchStartDate"); //yyyymmdd
        Integer page = (Integer) params.get("page");
        Integer limit = (Integer) params.get("limit");
        String lastLinkTime = StringUtil.nullToBlank((String) params.get("lastLinkTime"));
        String locationName = StringUtil.nullToBlank(params.get("locationName"));
        Integer targetSLA30 = (Integer) params.get("targetSLA30");

        StringBuffer sbQueryAllPeriod = new StringBuffer();        
        
        sbQueryAllPeriod.append("\nSELECT * ");
        sbQueryAllPeriod.append("\nFROM ( ");
        sbQueryAllPeriod.append("\n       SELECT mcu.sys_id ");
        sbQueryAllPeriod.append("\n       ,mcu.id AS MCU_ID");
        sbQueryAllPeriod.append("\n       ,modem.device_serial ");
        sbQueryAllPeriod.append("\n       ,meter.mds_id ");
        sbQueryAllPeriod.append("\n       ,meter.last_read_date ");
        sbQueryAllPeriod.append("\n       ,meter.id  AS METER_ID");
        sbQueryAllPeriod.append("\n       ,modem.modem_type AS MODEM_TYPE, modem.protocol_type AS PROTOCOL_TYPE ");
        sbQueryAllPeriod.append("\n       ,meter.lp_interval, a.targetsla30, b.yyyymmdd ");
        sbQueryAllPeriod.append("\n       , 24*60/meter.LP_INTERVAL as expect_mv_count ");
        sbQueryAllPeriod.append("\n       , current_mv_count, last_update_date ");
        sbQueryAllPeriod.append("\n       from sla_target a ");
        sbQueryAllPeriod.append("\n       join meter meter on a.meter_id=meter.mds_id  ");
        sbQueryAllPeriod.append("\n            and meter.meter_status =(select id from code where code='1.3.3.1') ");
        sbQueryAllPeriod.append("\n       left join sla_rawdata b on a.meter_id=b.meter_id and b.yyyymmdd=:searchDate ");
        
        sbQueryAllPeriod.append("\n       LEFT OUTER JOIN modem modem ON meter.modem_id = modem.id ");
        sbQueryAllPeriod.append("\n       LEFT OUTER JOIN mcu mcu ON modem.mcu_id = mcu.id ");
        sbQueryAllPeriod.append("\n       LEFT OUTER JOIN location lo ON meter.location_id = lo.id "); 

        if (!"".equals(locationName)) {
        	sbQueryAllPeriod.append("\nWHERE lo.name IN ("); 
        	sbQueryAllPeriod.append(locationName);
        	sbQueryAllPeriod.append(") ");        	
        }        
	    if ( !"".equals(lastLinkTime) ) {
	        if (!"".equals(locationName)) {
	        	sbQueryAllPeriod.append("\nAND modem.last_link_time >= :lastLinkTime ");
	        } else {
	        	sbQueryAllPeriod.append("\nWHERE modem.last_link_time >= :lastLinkTime ");
	        }
	    }
	    
        sbQueryAllPeriod.append("\n ) ");
        
        sbQueryAllPeriod.append("\nWHERE ( targetsla30=:targetSLA30  ");
        if (targetSLA30==0) {
            sbQueryAllPeriod.append("\nOR targetsla30 IS NULL  ");
        }
        sbQueryAllPeriod.append("\n)  ");
        sbQueryAllPeriod.append("\nAND (current_mv_count is null or expect_mv_count!=current_mv_count) ");
		
        
 /////


        Query query = null;
        query = getSession().createSQLQuery(sbQueryAllPeriod.toString());
        query.setString("searchDate", searchStartDate);
        query.setInteger("targetSLA30", targetSLA30);
	    if ( !"".equals(lastLinkTime) ) {
	    	query.setString("lastLinkTime", lastLinkTime);
	    }
	    
        Integer start = 0;
	    if ( page != null){
	    	start = (page - 1) * limit;

	    }
    	query.setFirstResult(start.intValue());
    	if ( limit != null )
    		query.setMaxResults(limit.intValue());

        List<Object> result = query.list();

        List<Object> resultList = new ArrayList<Object>();

        for (Object obj : result) {
            HashMap<String, Object> resultMap = new HashMap<String, Object>();
            Object[] objs = (Object[]) obj;
            resultMap.put("sysId" , (String)objs[0]);

            resultMap.put("mcuId", objs[1] == null ?  null : ((Number) objs[1]).intValue());
            resultMap.put("deviceSerial", (String) objs[2]);
            resultMap.put("mdsId", (String) objs[3]);
            resultMap.put("lastReadDate", (String) objs[4]);
            resultMap.put("meterId", ((Number) objs[5]).intValue());
            resultMap.put("modemType", (String)objs[6]);
            resultMap.put("protocolType", (String)objs[7]);
            resultList.add(resultMap);
        }
        return resultList;
    } // method End	
    
    @Override
	@Transactional(value = "transactionManager", readOnly=true, propagation=Propagation.REQUIRED)
	public List<Map<String, Object>> getMissLpMeter(Map<String, Object> conditionMap) {
        List<Map<String, Object>> result;

        String startDate = (String)conditionMap.get("startDate");
        Integer lpChannel = (Integer)conditionMap.get("lpChannel");
        String lpType = (String)conditionMap.get("lpType");
        String codeName = (String)conditionMap.get("codeName");
        String code = (String)conditionMap.get("code");
		
        StringBuffer sb = new StringBuffer();
        	
        sb.append("\n SELECT * FROM ");
        sb.append("\n ( ");
        sb.append("\n   SELECT ");
        sb.append("\n      me.ID, me.MDS_ID, me.LP_INTERVAL, (1440/me.LP_INTERVAL) as MAXLP, NVL(lp.CNT, 0) as SAVELP ");
        sb.append("\n   FROM ");
        sb.append("\n      METER me LEFT OUTER JOIN (SELECT MDEV_ID, count(MDEV_ID) as CNT FROM LP_EM WHERE YYYYMMDDHHMISS = :startDate and CHANNEL = :lpChannel and MDEV_TYPE = :lpType GROUP BY MDEV_ID) lp ");
        sb.append("\n   ON ");
        sb.append("\n      me.mds_id = lp.mdev_id(+) ");
        sb.append("\n   WHERE ");
        sb.append("\n      me.METER_STATUS != (SELECT ID FROM CODE WHERE NAME = :codeName AND CODE = :code) ");
        sb.append("\n      and me.LP_INTERVAL is not null ");
        sb.append("\n ) WHERE MAXLP != SAVELP");
        
        //Query query = getSession().createQuery(sb.toString());
        //Query query = getSession().createSQLQuery(new SQLWrapper().getQuery(sb.toString()));
        SQLQuery query = getSession().createSQLQuery(sb.toString());
        query.setString("startDate", startDate);
        query.setInteger("lpChannel", lpChannel);
        query.setString("lpType", lpType);
        query.setString("codeName", codeName);
        query.setString("code", code);
        
        return query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();        
	}
}
