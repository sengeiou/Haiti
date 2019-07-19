package com.aimir.dao.device.impl;

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
import java.util.Set;
import java.util.StringTokenizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.aimir.constants.CommonConstants;
import com.aimir.constants.CommonConstants.GroupType;
import com.aimir.constants.CommonConstants.MeterStatus;
import com.aimir.constants.CommonConstants.ModemSleepMode;
import com.aimir.constants.CommonConstants.ModemType;
import com.aimir.dao.AbstractHibernateGenericDao;
import com.aimir.dao.device.ModemDao;
import com.aimir.dao.system.CodeDao;
import com.aimir.dao.system.DeviceModelDao;
import com.aimir.dao.system.SupplierDao;
import com.aimir.model.device.Modem;
import com.aimir.model.system.Code;
import com.aimir.model.system.DeviceModel;
import com.aimir.util.DateTimeUtil;
import com.aimir.util.DecimalUtil;
import com.aimir.util.SQLWrapper;
import com.aimir.util.StringUtil;

@SuppressWarnings("unused")
@Repository(value = "modemDao")
public class ModemDaoImpl extends AbstractHibernateGenericDao<Modem, Integer> implements ModemDao {

    Log log = LogFactory.getLog(ModemDaoImpl.class);
    
    @Autowired
    CodeDao codeDao;
    
    @Autowired
    SupplierDao supplierDao;
    
	@Autowired
	DeviceModelDao deviceModelDao;
    
    @Autowired
    protected ModemDaoImpl(SessionFactory sessionFactory) {
        super(Modem.class);
        super.setSessionFactory(sessionFactory);
    }

    @Transactional(readOnly=true, propagation=Propagation.REQUIRED)
    public Modem get(String deviceSerial) {
        
        return findByCondition("deviceSerial", deviceSerial);
    }
    
    
    // Modem 정보 저장
    public Serializable setModem(Modem modem) {
        return getSession().save(modem);
    }

    

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
    public List<Object> getMiniChartModemTypeByLocation(Map<String, Object> condition){
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
        
        SQLQuery query = getSession().createSQLQuery(sbQuery.toString());
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
            
            sbQueryWhere.append(" , SUM(CASE WHEN mo.location_ID = " + resultData[0].toString() + " THEN 1 ELSE 0 END) AS value" + i + " \n");
        }
        
            
        // chartData
        sbQuery = new StringBuffer();
        
        sbQuery.append(" SELECT mo.MODEM AS xTag             \n")
               .append("      , mo.MODEM AS xCode            \n")
               .append(sbQueryWhere)                         
               .append("   FROM MODEM mo                     \n")
               .append("   JOIN LOCATION loc                 \n")
               .append("     ON ( mo.location_ID = loc.ID)   \n")
               .append("  WHERE mo.SUPPLIER_ID = :supplierId \n")
               .append("  GROUP BY mo.MODEM                  \n");
        
        
        List dataList = null;

        query = getSession().createSQLQuery(sbQuery.toString());
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
    @Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
    public List<Object> getMiniChartModemTypeByCommStatus(Map<String, Object> condition) {
        String supplierId = StringUtil.nullToBlank(condition.get("supplierId"));

        List<Object> gridData = new ArrayList<Object>();
        List<Object> chartData = new ArrayList<Object>();
        List<Object> chartSeries = new ArrayList<Object>();
        List<Object> result = new ArrayList<Object>();

        StringBuffer sbQuery = new StringBuffer();

        // chartSeries
        HashMap<String, Object> chartSerie1 = new HashMap<String, Object>();
        chartSerie1.put("xField", "xTag");
        chartSerie1.put("yField", "value0");
        chartSerie1.put("yCode", "0");
        chartSerie1.put("displayName", "fmtMessage00");
        chartSeries.add(chartSerie1);

        HashMap<String, Object> chartSerie2 = new HashMap<String, Object>();
        chartSerie2.put("xField", "xTag");
        chartSerie2.put("yField", "value1");
        chartSerie2.put("yCode", "1");
        chartSerie2.put("displayName", "fmtMessage24");
        chartSeries.add(chartSerie2);

        HashMap<String, Object> chartSerie3 = new HashMap<String, Object>();
        chartSerie3.put("xField", "xTag");
        chartSerie3.put("yField", "value2");
        chartSerie3.put("yCode", "2");
        chartSerie3.put("displayName", "fmtMessage48");
        chartSeries.add(chartSerie3);

        HashMap<String, Object> chartSerie4 = new HashMap<String, Object>();
        chartSerie4.put("xField", "xTag");
        chartSerie4.put("yField", "value3");
        chartSerie4.put("yCode", "3");
        chartSerie4.put("displayName", "fmtMessage99");
        chartSeries.add(chartSerie4);
        
        HashMap<String, Object> chartSerie5 = new HashMap<String, Object>();
        chartSerie5.put("xField", "xTag");
        chartSerie5.put("yField", "value4");
        chartSerie5.put("yCode", "4");
        chartSerie5.put("displayName", "CommError");
        chartSeries.add(chartSerie5);
        
        HashMap<String, Object> chartSerie6 = new HashMap<String, Object>();
        chartSerie6.put("xField", "xTag");
        chartSerie6.put("yField", "value5");
        chartSerie6.put("yCode", "5");
        chartSerie6.put("displayName", "SecurityError");
        chartSeries.add(chartSerie6);

        // chartData
        sbQuery = new StringBuffer();
        sbQuery.append("\nSELECT mo.modem AS xTag ");
        sbQuery.append("\n     , mo.modem AS xCode ");
        sbQuery.append("     , SUM(CASE WHEN mo.last_link_time >= :datePre24H  \n");
        sbQuery.append("      AND   (NOT mo.MODEM_STATUS = " + codeDao.getCodeIdByCode("1.2.7.6") + "\n" );
        sbQuery.append("      AND   NOT mo.MODEM_STATUS = " + codeDao.getCodeIdByCode("1.2.7.5") +"OR mo.MODEM_STATUS IS NULL)"+" THEN 1 ELSE 0 END) AS value0 \n");
        sbQuery.append("     , SUM(CASE WHEN mo.last_link_time < :datePre24H ");
        sbQuery.append("                AND mo.last_link_time >= :datePre48H \n");
        sbQuery.append("      AND   (NOT mo.MODEM_STATUS = " + codeDao.getCodeIdByCode("1.2.7.6") + "\n" );
        sbQuery.append("      AND   NOT mo.MODEM_STATUS = " + codeDao.getCodeIdByCode("1.2.7.5") +"OR mo.MODEM_STATUS IS NULL)" +" THEN 1 ELSE 0 END) AS value1 \n");
        sbQuery.append("     , SUM(CASE WHEN mo.last_link_time < :datePre48H \n");
        sbQuery.append("      AND   (NOT mo.MODEM_STATUS = " + codeDao.getCodeIdByCode("1.2.7.6") + "\n" );
        sbQuery.append("      AND   NOT mo.MODEM_STATUS = " + codeDao.getCodeIdByCode("1.2.7.5") +"OR mo.MODEM_STATUS IS NULL)" +" THEN 1 ELSE 0 END) AS value2 \n");
        sbQuery.append("     , SUM(CASE WHEN mo.last_link_time IS NULL \n");
        sbQuery.append("      AND   (NOT mo.MODEM_STATUS = " + codeDao.getCodeIdByCode("1.2.7.6") + "\n" );
        sbQuery.append("      AND   NOT mo.MODEM_STATUS = " + codeDao.getCodeIdByCode("1.2.7.5") + "\n");
        sbQuery.append("      OR mo.MODEM_STATUS IS NULL ) THEN 1 ELSE 0 END) AS value3 \n");
        sbQuery.append("     , SUM(CASE WHEN mo.MODEM_STATUS = " + codeDao.getCodeIdByCode("1.2.7.6") + " THEN 1 ELSE 0 END) AS value4 \n");
        sbQuery.append("     , SUM(CASE WHEN mo.MODEM_STATUS = " + codeDao.getCodeIdByCode("1.2.7.5") + " THEN 1 ELSE 0 END) AS value5 \n");
        sbQuery.append("\nFROM modem mo left outer join code co");
        sbQuery.append("\nON (mo.modem_status = co.id)");
        sbQuery.append("\nWHERE mo.supplier_id = :supplierId ");
        sbQuery.append("\nAND (mo.modem_status is null or co.code <> :deleteCode)");
        sbQuery.append("\nGROUP BY mo.modem ");

        List<Object[]> dataList = null;

        SQLQuery query = getSession().createSQLQuery(sbQuery.toString());

        String datePre24H = DateTimeUtil.calcDate(Calendar.HOUR, -24, "yyyyMMddHHmmss");
        String datePre48H = DateTimeUtil.calcDate(Calendar.HOUR, -48, "yyyyMMddHHmmss");

        query.setString("datePre24H", datePre24H);
        query.setString("datePre48H", datePre48H);
        query.setInteger("supplierId", Integer.parseInt(supplierId));
        
        query.setString("deleteCode", ModemSleepMode.Delete.getCode());
        
        dataList = query.list();

        int dataListLen = 0;
        HashMap<String, Object> gridDataMap = null;
        HashMap<String, Object> chartDataMap = null;
        if (dataList != null)
            dataListLen = dataList.size();
        
        DecimalFormat dfMd = DecimalUtil.getMDStyle(supplierDao.get(Integer.parseInt(supplierId)).getMd());

        for (int i = 0; i < dataListLen; i++) {
        	gridDataMap = new HashMap<String, Object>();
            chartDataMap = new HashMap<String, Object>();
            // Object[] resultData = (Object[]) dataList.get(i);
            Object[] resultData = dataList.get(i);
            
            List<Code> codeModemType = codeDao.getChildCodes(Code.MODEM_TYPE);

            for(int j=0;j<codeModemType.size();j++){
                if(resultData[0].equals(codeModemType.get(j).getName())){
                    if(codeModemType.get(j).getDescr()!=null){
                        resultData[0] = codeModemType.get(j).getDescr().toString();
                        resultData[1] = resultData[0];
                    }else{
                        resultData[1] = resultData[0];
                    }
                }
            }

            gridDataMap.put("xTag", resultData[0]);
            gridDataMap.put("xCode", resultData[1]);
            
            int resultDataLen = resultData.length;
            for (int j = 2; j < resultDataLen; j++) {
            	/* mini gadget - chart Data  fusionchart 값 오류 때문에 decimal 없앰 */
                gridDataMap.put("value".concat(Integer.toString(j - 2)), resultData[j]);
            }

            gridData.add(gridDataMap);
        }

        result.add(gridData);
        result.add(chartSeries);

        return result;
    }
    
    @SuppressWarnings({"unchecked", "rawtypes"})   
    @Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
    public List<Object> getMiniChartLocationByModemType(Map<String, Object> condition){
        String supplierId         = StringUtil.nullToBlank(condition.get("supplierId"));
        
        List<Object> chartData   = new ArrayList<Object>();
        List<Object> chartSeries = new ArrayList<Object>();
        List<Object> result      = new ArrayList<Object>();
        
        StringBuffer sbQuery      = new StringBuffer();
        StringBuffer sbQueryWhere = new StringBuffer();
        
        
        // chartSeries
        sbQuery.append(" SELECT MODEM as yCode              \n")
               .append("      , MODEM as displayName        \n")
               .append("   FROM MODEM                       \n")
                .append("  WHERE SUPPLIER_ID = :supplierId  \n")    
               .append("  GROUP BY MODEM                    \n")
               .append("  ORDER BY MODEM                    \n");
             
        List yCodeList = null;
        
        SQLQuery query = getSession().createSQLQuery(sbQuery.toString());
        query.setInteger("supplierId", Integer.parseInt(supplierId));
        yCodeList = query.list();
        
        int yCodeLen = 0;       
        if(yCodeList != null)
            yCodeLen = yCodeList.size();
        
        for(int i = 0 ; i < yCodeLen && i < 4 ; i++){
            HashMap chartSerie = new HashMap();
            Object[] resultData = (Object[]) yCodeList.get(i);
            
            chartSerie.put("xField", "xTag");               
            chartSerie.put("yField", "value".concat(Integer.toString(i)));
            chartSerie.put("yCode", resultData[0].toString());
            chartSerie.put("displayName", resultData[1].toString());
            
            chartSeries.add(chartSerie);
            
            sbQueryWhere.append(" , SUM(CASE WHEN mo.MODEM = '" + resultData[0].toString() + "' THEN 1 ELSE 0 END) AS value" + i + " \n");
        }
        
            
        // chartData
        sbQuery = new StringBuffer();
        
        sbQuery.append(" SELECT loc.Name AS xTag     \n")
               .append("      , loc.Id   AS xCode    \n")
               .append(sbQueryWhere)
               .append("   FROM MODEM mo                     \n")
               .append("   JOIN LOCATION loc                 \n")
               .append("     ON ( mo.location_ID = loc.ID)   \n")
               .append("  WHERE mo.SUPPLIER_ID = :supplierId \n")
               .append("  GROUP BY loc.Name, loc.Id          \n");
        
        List dataList = null;

        query = getSession().createSQLQuery(sbQuery.toString());
        query.setInteger("supplierId", Integer.parseInt(supplierId));
        dataList = query.list();

        
        int dataListLen = 0;
        if(dataList != null)
            dataListLen = dataList.size();
        
        for(int i=0 ; i < dataListLen ; i++){
            
            HashMap chartDataMap = new HashMap();
            Object[] resultData = (Object[]) dataList.get(i);
            
            chartDataMap.put("xTag", resultData[0]);                
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
    @SuppressWarnings({"unchecked", "rawtypes"})
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
        sbQuery.append(" SELECT loc.Name AS xTag     \n")
               .append("      , loc.Id   AS xCode    \n")
               .append("      , SUM(CASE WHEN LAST_LINK_TIME     >= :datePre24H THEN 1 ELSE 0 END) AS value0 \n")           
               .append("      , SUM(CASE WHEN     LAST_LINK_TIME <  :datePre24H " +
                       "                      AND LAST_LINK_TIME >= :datePre48H THEN 1 ELSE 0 END) AS value1 \n")
               .append("      , SUM(CASE WHEN LAST_LINK_TIME     <  :datePre48H THEN 1 ELSE 0 END) AS value2 \n")
               .append("   FROM MODEM mo                     \n")              
               .append("   JOIN LOCATION loc                 \n")
               .append("     ON ( mo.location_ID = loc.ID)   \n")
               .append("  WHERE mo.SUPPLIER_ID = :supplierId \n")
               .append("  GROUP BY loc.Name, loc.Id          \n");
            
        List dataList = null;
         
        SQLQuery query = getSession().createSQLQuery(sbQuery.toString());

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
            
            chartDataMap.put("xTag", resultData[0]);                
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
    @Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
    public List<Object> getMiniChartCommStatusByModemType(Map<String, Object> condition) {

        String supplierId = StringUtil.nullToBlank(condition.get("supplierId"));

        List<Object> chartData = new ArrayList<Object>();
        List<Object> chartSeries = new ArrayList<Object>();
        List<Object> result = new ArrayList<Object>();

        StringBuilder sbQuery = new StringBuilder();
        StringBuilder sbQueryWhere = new StringBuilder();

        // chartSeries
        sbQuery.append("\nSELECT mo.modem AS yCode ");
        sbQuery.append("\n     , mo.modem AS displayName ");
        sbQuery.append("\nFROM modem mo left outer join code co ");
        sbQuery.append("\nON (mo.modem_status = co.id)");
        sbQuery.append("\nWHERE mo.supplier_id = :supplierId ");
        sbQuery.append("\nAND (mo.modem_status is null OR co.code <> :deleteCode) ");
        sbQuery.append("\nGROUP BY mo.modem ");
        sbQuery.append("\nORDER BY mo.modem ");

        List<Object[]> yCodeList = null;

        SQLQuery query = getSession().createSQLQuery(sbQuery.toString());
        query.setInteger("supplierId", Integer.parseInt(supplierId));
        query.setString("deleteCode", ModemSleepMode.Delete.getCode());
        yCodeList = query.list();

        int yCodeLen = 0;
        HashMap<String, Object> chartSerie = null;
        if (yCodeList != null)
            yCodeLen = yCodeList.size();

//        for (int i = 0; i < yCodeLen && i < 4; i++) {
        for (int i = 0; i < yCodeLen; i++) {
            chartSerie = new HashMap<String, Object>();
            Object[] resultData = yCodeList.get(i);

            chartSerie.put("xField", "xTag");
            chartSerie.put("yField", "value".concat(Integer.toString(i)));
            chartSerie.put("yCode", resultData[0].toString());
            chartSerie.put("displayName", resultData[1].toString());

            chartSeries.add(chartSerie);

            sbQueryWhere.append("\n     , SUM(CASE WHEN mo.modem = '").append(resultData[0].toString());
            sbQueryWhere.append("' THEN 1 ELSE 0 END) AS value").append(i).append(" ");
        }

        // chartData
        StringBuilder sbChartQuery = new StringBuilder();

        sbChartQuery.append("\nSELECT mo.commStatus AS xTag ");
        sbChartQuery.append("\n     , mo.commStatus AS xCode ");
        sbChartQuery.append(sbQueryWhere);
        sbChartQuery.append("\nFROM (SELECT m.modem ");
        sbChartQuery.append("\n           , m.id ");
        sbChartQuery.append("\n           , CASE WHEN m.last_link_time >= :datePre24H THEN '0' ");
        sbChartQuery.append("\n                  WHEN m.last_link_time < :datePre24H ");
        sbChartQuery.append("\n                   AND m.last_link_time >= :datePre48H THEN '1' ");
        sbChartQuery.append("\n                  WHEN m.last_link_time < :datePre48H THEN '2' ");
        sbChartQuery.append("\n                  ELSE '3' ");
        sbChartQuery.append("\n             END AS commStatus ");
        sbChartQuery.append("\n      FROM modem m left outer join code c ON(m.modem_status = c.id)");
        sbChartQuery.append("\n      WHERE m.supplier_id = :supplierId AND (m.modem_status is null OR c.code <> :deleteCode)) mo ");
        sbChartQuery.append("\nGROUP BY mo.commStatus ");

        List<Object[]> dataList = null;

        query = getSession().createSQLQuery(sbChartQuery.toString());

        String datePre24H = DateTimeUtil.calcDate(Calendar.HOUR, -24, "yyyyMMddHHmmss");
        String datePre48H = DateTimeUtil.calcDate(Calendar.HOUR, -48, "yyyyMMddHHmmss");

        query.setString("datePre24H", datePre24H);
        query.setString("datePre48H", datePre48H);
        query.setInteger("supplierId", Integer.parseInt(supplierId));
        query.setString("deleteCode", ModemSleepMode.Delete.getCode());
        
        dataList = query.list();

        DecimalFormat dfMd = DecimalUtil.getMDStyle(supplierDao.get(Integer.parseInt(supplierId)).getMd());

        int dataListLen = 0;
        HashMap<String, Object> chartDataMap = null;
        if (dataList != null)
            dataListLen = dataList.size();

        for (int i = 0; i < dataListLen; i++) {
            chartDataMap = new HashMap<String, Object>();
            Object[] resultData = dataList.get(i);

            chartDataMap.put("xCode", resultData[1].toString());

            if (chartDataMap.get("xCode").toString().equals("0")) {
                chartDataMap.put("xTag", "fmtMessage00");
            } else if (chartDataMap.get("xCode").toString().equals("1")) {
                chartDataMap.put("xTag", "fmtMessage24");
            } else if (chartDataMap.get("xCode").toString().equals("2")) {
                chartDataMap.put("xTag", "fmtMessage48");
            } else {
                chartDataMap.put("xTag", "fmtMessage99");
            }

            int resultDataLen = resultData.length;
            for (int j = 2; j < resultDataLen; j++) {
                 chartDataMap.put("value".concat(Integer.toString(j - 2)), dfMd.format(resultData[j]));
            }

            chartData.add(chartDataMap);
        }

        result.add(chartData);
        result.add(chartSeries);

        return result;
    }
    
    @Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
    public int deleteModemStatus(int modemId, Code code) {
        StringBuffer sb = new StringBuffer();
        sb.append("UPDATE Modem \n");
        sb.append("set modemStatus =:code \n");
        sb.append("WHERE id = :id");
        Query query = getSession().createQuery(sb.toString());
        query.setEntity("code", code);
        query.setInteger("id", modemId);
        return query.executeUpdate();
    }

    @SuppressWarnings({"unchecked", "rawtypes"})  
    @Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
    public List<Object> getMiniChartCommStatusByModemType2(Map<String, Object> condition){
        
        String supplierId         = StringUtil.nullToBlank(condition.get("supplierId"));
        
        List<Object> chartData   = new ArrayList<Object>();
        List<Object> chartSeries = new ArrayList<Object>();
        List<Object> result      = new ArrayList<Object>();
        
        StringBuffer sbQuery      = new StringBuffer();
        StringBuffer sbQueryWhere = new StringBuffer();
        
        // chartSeries
        sbQuery.append(" SELECT MODEM as yCode              \n")
               .append("      , MODEM as displayName        \n")
               .append("   FROM MODEM                       \n")    
               .append("  WHERE SUPPLIER_ID = :supplierId  \n") 
               .append("  GROUP BY MODEM                    \n")
               .append("  ORDER BY MODEM                    \n");
        
        List yCodeList = null;
        
        SQLQuery query = getSession().createSQLQuery(sbQuery.toString());
        query.setInteger("supplierId", Integer.parseInt(supplierId));
        yCodeList = query.list();
        
        int yCodeLen = 0;       
        if(yCodeList != null)
            yCodeLen = yCodeList.size();
        
        for(int i = 0 ; i < yCodeLen && i < 4 ; i++){
            HashMap chartSerie = new HashMap();
            Object[] resultData = (Object[]) yCodeList.get(i);
            
            chartSerie.put("xField", "xTag");               
            chartSerie.put("yField", "value".concat(Integer.toString(i)));
            chartSerie.put("yCode", resultData[0].toString());
            chartSerie.put("displayName", resultData[1].toString());
            
            chartSeries.add(chartSerie);
            
            sbQueryWhere.append(" , SUM(CASE WHEN mo.MODEM = '" + resultData[0].toString() + "' THEN 1 ELSE 0 END) AS value" + i + " \n");
        }
        
            
        // chartData
        sbQuery = new StringBuffer();
        
        sbQuery.append(" SELECT mo.commStatus AS xTag     \n")
               .append("      , mo.commStatus AS xCode    \n")
               .append(sbQueryWhere)
               .append("   FROM (SELECT MODEM                                                 \n")         
               .append("              , ID                                                    \n")
               .append("              , CASE WHEN LAST_LINK_TIME >= :datePre24H THEN '0'          \n")
               .append("                     WHEN     LAST_LINK_TIME <  :datePre24H               \n")
               .append("                          AND LAST_LINK_TIME >= :datePre48H THEN '1'      \n")
               .append("                     ELSE '2'                                         \n")
               .append("                 END        as commStatus                             \n")
               .append("            FROM MODEM                                                \n")
               .append("           WHERE SUPPLIER_ID = :supplierId ) mo                       \n")                  
               .append("  GROUP BY mo.commStatus                                              \n");
        
        List dataList = null;

        query = getSession().createSQLQuery(sbQuery.toString());
        
        String datePre24H = DateTimeUtil.calcDate(Calendar.HOUR, -24, "yyyyMMddHHmmss");
        String datePre48H = DateTimeUtil.calcDate(Calendar.HOUR, -48, "yyyyMMddHHmmss");        
        
        query.setString("datePre24H", datePre24H);  
        query.setString("datePre48H", datePre48H);
        query.setInteger("supplierId", Integer.parseInt(supplierId));
        
        dataList = query.list();

        
        int dataListLen = 0;
        if(dataList != null)
            dataListLen = dataList.size();
        
        for (int i = 0; i < dataListLen; i++)
        {

            HashMap chartDataMap = new HashMap();
            Object[] resultData = (Object[]) dataList.get(i);

            chartDataMap.put("xCode", resultData[1].toString());

            if (chartDataMap.get("xCode").toString().equals("0"))
                chartDataMap.put("xTag", "fmtMessage00");
            else if (chartDataMap.get("xCode").toString().equals("1"))
                chartDataMap.put("xTag", "fmtMessage24");
            else
                chartDataMap.put("xTag", "fmtMessage48");

            int resultDataLen = resultData.length;
            for (int j = 2; j < resultDataLen; j++)
            {
                chartDataMap.put("value".concat(Integer.toString(j - 2)),
                        resultData[j]);
            }

            chartData.add(chartDataMap);

        }
        
        result.add(chartData);
        result.add(chartSeries);
        
        return result;      
        
    }

    @SuppressWarnings("unchecked")
    @Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
    public List<Object> getMiniChartCommStatusByModemType(Map<String, Object> condition, String[] arrFmtmessagecommalert) {
        String supplierId = StringUtil.nullToBlank(condition.get("supplierId"));

        List<Object> chartData = new ArrayList<Object>();
        List<Object> chartSeries = new ArrayList<Object>();
        List<Object> result = new ArrayList<Object>();

        StringBuilder sbQuery = new StringBuilder();
        StringBuilder sbQueryWhere = new StringBuilder();

        // chartSeries
        sbQuery.append("\nSELECT mo.modem AS yCode ");
        sbQuery.append("\n     , mo.modem AS displayName ");
        sbQuery.append("\nFROM modem mo left outer join code co ");
        sbQuery.append("\nON mo.modem_status = co.id");
        sbQuery.append("\nWHERE mo.supplier_id = :supplierId ");
        sbQuery.append("\nAND (mo.modem_status is null OR co.code <> :deleteCode)");
        sbQuery.append("\nGROUP BY mo.modem ");
        sbQuery.append("\nORDER BY mo.modem ");

        List<Object[]> yCodeList = null;

        SQLQuery query = getSession().createSQLQuery(sbQuery.toString());
        query.setInteger("supplierId", Integer.parseInt(supplierId));
        query.setString("deleteCode", ModemSleepMode.Delete.getCode());
        yCodeList = query.list();

        int yCodeLen = 0;
        HashMap<String, Object> chartSerie = null;
        if (yCodeList != null)
            yCodeLen = yCodeList.size();

//        for (int i = 0; i < yCodeLen && i < 4; i++) {
        for (int i = 0; i < yCodeLen; i++) {
            chartSerie = new HashMap<String, Object>();
            Object[] resultData = yCodeList.get(i);

            chartSerie.put("xField", "xTag");
            chartSerie.put("yField", "value".concat(Integer.toString(i)));
            chartSerie.put("yCode", resultData[0].toString());
            chartSerie.put("displayName", resultData[1].toString());

            chartSeries.add(chartSerie);

            sbQueryWhere.append("\n     , SUM(CASE WHEN mo.modem = '");
            sbQueryWhere.append(resultData[0].toString());
            sbQueryWhere.append("' THEN 1 ELSE 0 END) AS value");
            sbQueryWhere.append(i).append(" ");
        }

        // chartData  //여기2
        StringBuilder sbChartQuery = new StringBuilder();

        sbChartQuery.append("\nSELECT mo.commStatus AS xTag ");
        sbChartQuery.append("\n     , mo.commStatus AS xCode ");
        sbChartQuery.append(sbQueryWhere);
        sbChartQuery.append("\nFROM (SELECT m.modem ");
        sbChartQuery.append("\n           , m.id ");
        sbChartQuery.append("\n           , CASE WHEN m.last_link_time >= :datePre24H ");
        sbChartQuery.append("                    AND   (NOT m.MODEM_STATUS = " + codeDao.getCodeIdByCode("1.2.7.6") + "\n" );
        sbChartQuery.append("                    AND   NOT m.MODEM_STATUS = " + codeDao.getCodeIdByCode("1.2.7.5") +"OR m.MODEM_STATUS IS NULL)"+" THEN '0' \n");
        sbChartQuery.append("\n                  WHEN m.last_link_time < :datePre24H ");
        sbChartQuery.append("\n                  AND m.last_link_time >= :datePre48H ");
        sbChartQuery.append("                    AND   (NOT m.MODEM_STATUS = " + codeDao.getCodeIdByCode("1.2.7.6") + "\n" );
        sbChartQuery.append("                    AND   NOT m.MODEM_STATUS = " + codeDao.getCodeIdByCode("1.2.7.5") +"OR m.MODEM_STATUS IS NULL)"+" THEN '1' \n");
        sbChartQuery.append("\n                  WHEN m.last_link_time < :datePre48H ");
        sbChartQuery.append("                    AND   (NOT m.MODEM_STATUS = " + codeDao.getCodeIdByCode("1.2.7.6") + "\n" );
        sbChartQuery.append("                    AND   NOT m.MODEM_STATUS = " + codeDao.getCodeIdByCode("1.2.7.5") +"OR m.MODEM_STATUS IS NULL)"+" THEN '2' \n");
        sbChartQuery.append("\n                  WHEN m.last_link_time IS NULL ");
        sbChartQuery.append("                    AND   (NOT m.MODEM_STATUS = " + codeDao.getCodeIdByCode("1.2.7.6") + "\n" );
        sbChartQuery.append("                    AND   NOT m.MODEM_STATUS = " + codeDao.getCodeIdByCode("1.2.7.5") +"OR m.MODEM_STATUS IS NULL)"+" THEN '3' \n");
        sbChartQuery.append("\n                  WHEN m.MODEM_STATUS = " + codeDao.getCodeIdByCode("1.2.7.6") + " THEN '4'\n");
        sbChartQuery.append("\n                  WHEN m.MODEM_STATUS = " + codeDao.getCodeIdByCode("1.2.7.5") + " THEN '5' \n");
        sbChartQuery.append("\n             END AS commStatus ");
        sbChartQuery.append("\n      FROM modem m left outer join code co ON (m.modem_status = co.id) ");
        sbChartQuery.append("\n      WHERE m.supplier_id = :supplierId AND (m.modem_status is null OR co.code <> :deleteCode)) mo ");
        sbChartQuery.append("\nGROUP BY mo.commStatus ");

        List<Object[]> dataList = null;

        query = getSession().createSQLQuery(sbChartQuery.toString());

        String datePre24H = DateTimeUtil.calcDate(Calendar.HOUR, -24, "yyyyMMddHHmmss");
        String datePre48H = DateTimeUtil.calcDate(Calendar.HOUR, -48, "yyyyMMddHHmmss");

        query.setString("datePre24H", datePre24H);
        query.setString("datePre48H", datePre48H);
        query.setInteger("supplierId", Integer.parseInt(supplierId));
        query.setString("deleteCode", ModemSleepMode.Delete.getCode());

        dataList = query.list();

        int dataListLen = 0;
        HashMap<String, Object> chartDataMap = null;
        if (dataList != null)
            dataListLen = dataList.size();
        
        DecimalFormat dfMd = DecimalUtil.getMDStyle(supplierDao.get(Integer.parseInt(supplierId)).getMd());

        for (int i = 0; i < dataListLen; i++) {
            chartDataMap = new HashMap<String, Object>();
            Object[] resultData = (Object[]) dataList.get(i);

            chartDataMap.put("xCode", resultData[1].toString());

            if (chartDataMap.get("xCode").toString().equals("0")) {
                chartDataMap.put("xTag", "fmtMessage00");
            } else if (chartDataMap.get("xCode").toString().equals("1")) {
                chartDataMap.put("xTag", "fmtMessage24");
            } else if (chartDataMap.get("xCode").toString().equals("2")) {
                chartDataMap.put("xTag", "fmtMessage48");
            } else if (chartDataMap.get("xCode").toString().equals("3")){
                chartDataMap.put("xTag", "fmtMessage99");
            } else if (chartDataMap.get("xCode").toString().equals("4")){
                chartDataMap.put("xTag", "CommError");
            } else if (chartDataMap.get("xCode").toString().equals("5")){
                chartDataMap.put("xTag", "SecurityError");}
            
            /*
             * 
             * @desc: 메시지 tag 처리
             */
            if (chartDataMap.get("xTag").toString().equals("fmtMessage00")) {
                chartDataMap.put("xTag", arrFmtmessagecommalert[0]);
            } else if (chartDataMap.get("xTag").toString().equals("fmtMessage24")) {
                chartDataMap.put("xTag", arrFmtmessagecommalert[1]);
            } else if (chartDataMap.get("xTag").toString().equals("fmtMessage48")) {
                chartDataMap.put("xTag", arrFmtmessagecommalert[2]);
            } else if (chartDataMap.get("xTag").toString().equals("fmtMessage99")) {
                chartDataMap.put("xTag", arrFmtmessagecommalert[3]);
            } else if (chartDataMap.get("xTag").toString().equals("fmtMessage99")) {
                chartDataMap.put("xTag", arrFmtmessagecommalert[3]);
            }

            int resultDataLen = resultData.length;
            for (int j = 2; j < resultDataLen; j++) {
                chartDataMap.put("value".concat(Integer.toString(j - 2)), dfMd.format(resultData[j]));
            }

            chartData.add(chartDataMap);
        }

        result.add(chartData);
        result.add(chartSeries);

        return result;
    }   

    @SuppressWarnings({"unchecked", "rawtypes"})
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
               .append("  AND loc.SUPPLIER_ID = :supplierId \n")
               .append("  ORDER BY loc.ID                   \n");
        
        List locList = null;
        
        SQLQuery query = getSession().createSQLQuery(sbQuery.toString());
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
            
            sbQueryWhere.append(" , SUM(CASE WHEN mo.location_Id = " + resultData[0].toString() + " THEN 1 ELSE 0 END) AS value" + i + " \n");
        }
        
            
        // chartData
        sbQuery = new StringBuffer();
        
        sbQuery.append(" SELECT mo.commStatus AS xTag     \n")
               .append("      , mo.commStatus AS xCode    \n")
               .append(sbQueryWhere)
               .append("   FROM (SELECT MODEM                                                 \n")         
               .append("              , ID                                                    \n")
               .append("              , Location_ID                                           \n")
               .append("              , CASE WHEN LAST_LINK_TIME >= :datePre24H THEN '0'          \n")
               .append("                     WHEN     LAST_LINK_TIME <  :datePre24H               \n")
               .append("                          AND LAST_LINK_TIME >= :datePre48H THEN '1'      \n")
               .append("                     ELSE '2'                                         \n")
               .append("                 END        as commStatus                             \n")             
               .append("            FROM MODEM                                                \n")
               .append("           WHERE SUPPLIER_ID = :supplierId ) mo                       \n")
              
               
               
               .append("  GROUP BY mo.commStatus                                              \n");
        
        List dataList = null;

        query = getSession().createSQLQuery(sbQuery.toString());
        
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
            
            if(chartDataMap.get("xCode").toString().equals("0"))
                chartDataMap.put("xTag", "fmtMessage00");
            else if(chartDataMap.get("xCode").toString().equals("1"))
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
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
    public List<Object> getMiniChartCommStatusByLocation(Map<String, Object> condition,String[] arrFmtmessagecommalert )
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
               .append("  AND loc.SUPPLIER_ID = :supplierId \n")
               .append("  ORDER BY loc.ID                   \n");
        
        List locList = null;
        
        SQLQuery query = getSession().createSQLQuery(sbQuery.toString());
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
            
            sbQueryWhere.append(" , SUM(CASE WHEN mo.location_Id = " + resultData[0].toString() + " THEN 1 ELSE 0 END) AS value" + i + " \n");
        }
        
            
        // chartData
        sbQuery = new StringBuffer();
        
        sbQuery.append(" SELECT mo.commStatus AS xTag     \n")
               .append("      , mo.commStatus AS xCode    \n")
               .append(sbQueryWhere)
               .append("   FROM (SELECT MODEM                                                 \n")         
               .append("              , ID                                                    \n")
               .append("              , Location_ID                                           \n")
               .append("              , CASE WHEN LAST_LINK_TIME >= :datePre24H THEN '0'          \n")
               .append("                     WHEN     LAST_LINK_TIME <  :datePre24H               \n")
               .append("                          AND LAST_LINK_TIME >= :datePre48H THEN '1'      \n")
               .append("                     ELSE '2'                                         \n")
               .append("                 END        as commStatus                             \n")             
               .append("            FROM MODEM                                                \n")
               .append("           WHERE SUPPLIER_ID = :supplierId ) mo                       \n")
              
               
               
               .append("  GROUP BY mo.commStatus                                              \n");
        
        List dataList = null;

        query = getSession().createSQLQuery(sbQuery.toString());
        
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

            
            if (chartDataMap.get("xCode").toString().equals("0"))
            {
                chartDataMap.put("xTag", "fmtMessage00");
                
            }
            else if (chartDataMap.get("xCode").toString().equals("1"))
            {
                chartDataMap.put("xTag", "fmtMessage24");
                
            }
            else
            {
                chartDataMap.put("xTag", "fmtMessage48");
                
            }
            
            /*
             * 
             * @desc: 메시지 tag 처리
             */
            if ( chartDataMap.get("xTag").toString().equals("fmtMessage00"))
            {
                chartDataMap.put("xTag", arrFmtmessagecommalert[0]);
            }
            else if (chartDataMap.get("xTag").toString().equals("fmtMessage24"))
            {

                chartDataMap.put("xTag", arrFmtmessagecommalert[1]);
            }
            else if (chartDataMap.get("xTag").toString().equals("fmtMessage48"))
            {

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
    
    
    @SuppressWarnings({"unchecked", "rawtypes"})
    @Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
    public List<Object> getMiniGrid(Map<String, Object> condition){
        
        List<Object> gridData    = new ArrayList<Object>();
        List<Object> result     = new ArrayList<Object>();
        
        String modemType    = condition.get("modemType").toString(); 
        String loc          = condition.get("loc").toString();
        String commStatus   = condition.get("commStatus").toString();       
        String curPage      = condition.get("curPage").toString();
        
        StringBuffer sbQuery      = new StringBuffer();     
        
        sbQuery.append("   SELECT mo.ID            AS modemID                                 \n")
               .append("        , loc.name         AS locName                                 \n")
               .append("        , CASE WHEN LAST_LINK_TIME     >= :datePre24H THEN '0'            \n")
               .append("               WHEN     LAST_LINK_TIME <  :datePre24H                     \n")
               .append("                    AND LAST_LINK_TIME >= :datePre48H THEN '1'            \n")
               .append("               WHEN LAST_LINK_TIME     <  :datePre48H THEN '2'            \n")
               .append("               ELSE '9'                                               \n")
               .append("           END             AS modemStatus                             \n")
               .append("     FROM MODEM mo                                                    \n")
               .append("     JOIN LOCATION loc                                                \n")
               .append("       ON ( mo.location_ID = loc.ID)                                  \n")
               .append("    WHERE mo.SUPPLIER_ID = :supplierId                                \n")               
               .append("    WHERE 1=1                                                         \n");
        
        if(!modemType.equals(""))
            sbQuery.append("AND mo.MODEM = '"+ modemType +"' \n");
        
        if(!loc.equals(""))
            sbQuery.append("AND loc.Id = "+ loc +" \n");
        
        if(commStatus.equals("0"))
            sbQuery.append("AND mo.LAST_LINK_TIME  >= :datePre24H \n");
        else if(commStatus.equals("1"))
            sbQuery.append("AND mo.LAST_LINK_TIME < :datePre24H " +
                           "AND mo.LAST_LINK_TIME >= :datePre48H \n");
        else if(commStatus.equals("2"))
            sbQuery.append("AND (    mo.LAST_LINK_TIME < :datePre48H " +
                           "      OR mo.LAST_LINK_TIME IS NULL)");
        
        StringBuffer sbQueryData = new StringBuffer();
        sbQueryData.append(sbQuery)
                   .append("    ORDER BY mo.ID \n");        
         
        SQLQuery query = getSession().createSQLQuery(sbQueryData.toString());
        
        String datePre24H = DateTimeUtil.calcDate(Calendar.HOUR, -24, "yyyyMMddHHmmss");
        String datePre48H = DateTimeUtil.calcDate(Calendar.HOUR, -48, "yyyyMMddHHmmss");    
        
        query.setString("datePre24H", datePre24H);  
        query.setString("datePre48H", datePre48H);
        
        // Paging
        int rowPerPage = CommonConstants.Paging.ROWPERPAGE.getPageNum();
        int firstIdx  = Integer.parseInt(curPage) * rowPerPage;
        
        query.setFirstResult(firstIdx);
        query.setMaxResults(rowPerPage);
        
        List meterList = null;
        meterList = query.list();
        
        // 전체 건수
        StringBuffer countQuery = new StringBuffer();
        countQuery.append("\n SELECT COUNT(countTotal.modemID) ");
        countQuery.append("\n FROM (  ");
        countQuery.append(sbQuery);
        countQuery.append("\n ) countTotal ");
        
        SQLQuery countQueryObj = getSession().createSQLQuery(countQuery.toString());
        
        countQueryObj.setString("datePre24H", datePre24H);
        countQueryObj.setString("datePre48H", datePre48H);
        
        Number totalCount = (Number)countQueryObj.uniqueResult();
        
        result.add(totalCount.toString());
        
        
        // 실제 데이터
        int meterListLen = 0;
        if(meterList != null)
            meterListLen= meterList.size();
        
        for(int i=0 ; i < meterListLen ; i++){
            
            HashMap chartDataMap = new HashMap();
            Object[] resultData = (Object[]) meterList.get(i);
            
            chartDataMap.put("no",        totalCount.intValue() -i - firstIdx );                       
            chartDataMap.put("modemId",   resultData[0]);
            chartDataMap.put("locName",   resultData[1]);
            
            if(resultData[2].equals('0'))
                chartDataMap.put("commStatus", "fmtMessage00");
            else if(resultData[2].equals('1'))
                chartDataMap.put("commStatus", "fmtMessage24");
            else
                chartDataMap.put("commStatus", "fmtMessage48");         
            
            gridData.add(chartDataMap);
            
        }
        
        result.add(gridData);
        
        return result;
        
    }

    @SuppressWarnings("unchecked")
    @Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
    public List<Object> getModemSearchChart(Map<String, Object> condition) {

        List<Object> gridData = new ArrayList<Object>();
        List<Object> chartData = new ArrayList<Object>();
        List<Object> result = new ArrayList<Object>();
        StringBuilder sbQuery = new StringBuilder();

        String sModemType = StringUtil.nullToBlank(condition.get("sModemType"));
        String sModemId = StringUtil.nullToBlank(condition.get("sModemId"));
        String sState = StringUtil.nullToBlank(condition.get("sState"));
        String sInstallState = StringUtil.nullToBlank(condition.get("sInstallState"));

        String sMcuType = StringUtil.nullToBlank(condition.get("sMcuType"));
        String sMcuName = StringUtil.nullToBlank(condition.get("sMcuName"));
        String sModemFwVer = StringUtil.nullToBlank(condition.get("sModemFwVer"));
        String sModemSwRev = StringUtil.nullToBlank(condition.get("sModemSwRev"));
        String sModemHwVer = StringUtil.nullToBlank(condition.get("sModemHwVer"));
        String sModemStatus = StringUtil.nullToBlank(condition.get("sModemStatus"));

        String sInstallStartDate = StringUtil.nullToBlank(condition.get("sInstallStartDate"));
        String sInstallEndDate = StringUtil.nullToBlank(condition.get("sInstallEndDate"));

        String sLastcommStartDate = StringUtil.nullToBlank(condition.get("sLastcommStartDate"));
        String sLastcommEndDate = StringUtil.nullToBlank(condition.get("sLastcommEndDate"));
        String sLocationId = StringUtil.nullToBlank(condition.get("sLocationId"));
        String sModuleBuild = StringUtil.nullToBlank(condition.get("sModuleBuild"));
        
        String deleteCodeId           = StringUtil.nullToBlank(condition.get("deleteCodeId"));
        String breakDownCodeId        = StringUtil.nullToBlank(condition.get("breakDownCodeId"));
        String normalCodeId           = StringUtil.nullToBlank(condition.get("normalCodeId"));
        String repairCodeId           = StringUtil.nullToBlank(condition.get("repairCodeId"));
        String securityErrorCodeId  = StringUtil.nullToBlank(condition.get("securityErrorCodeId"));
        String commErrorCodeId      = StringUtil.nullToBlank(condition.get("commErrorCodeId"));
        
        String supplierId = StringUtil.nullToBlank(condition.get("supplierId"));
        Integer page = (Integer)condition.get("page");
        Integer limit = (Integer)condition.get("limit");
        Integer start = (page != null) ? ((page - 1) * limit) : 0;
        Boolean isExcel = condition.get("isExcel") == null ? false : (Boolean)condition.get("isExcel");

        sbQuery.append("FROM modem mo \n");
        sbQuery.append("     LEFT OUTER JOIN mcu mcu \n");
        sbQuery.append("     ON (mo.mcu_id = mcu.id) \n");
        sbQuery.append("     LEFT OUTER JOIN location loc \n");
        sbQuery.append("     ON ( mo.location_id = loc.id) \n");
        sbQuery.append("     LEFT OUTER JOIN ( \n");
        sbQuery.append("         SELECT model.id    AS modelId \n");
        sbQuery.append("              , model.name  AS modelName \n");
        sbQuery.append("              , vendor.id   AS vendorId \n");
        sbQuery.append("              , vendor.name as vendorName \n");
        sbQuery.append("         FROM devicemodel model \n");
        sbQuery.append("              LEFT OUTER JOIN devicevendor vendor \n");
        sbQuery.append("              ON (model.devicevendor_id = vendor.id) \n");
        sbQuery.append("     ) device \n");
        sbQuery.append("     ON (mo.devicemodel_id = device.modelId) \n");
        sbQuery.append("WHERE mo.supplier_id = :supplierId \n");
        
        if(!"".equals(deleteCodeId)) {  
            if(sModemStatus.isEmpty()){  // Status = all
                sbQuery.append("    AND (mo.MODEM_STATUS <>" +deleteCodeId + "OR mo.MODEM_STATUS IS NULL)" );
            }else if(!sModemStatus.isEmpty() && breakDownCodeId.equals(sModemStatus)){     // Status = BreakDown                                                     
                sbQuery.append("    AND mo.MODEM_STATUS = "+ Integer.parseInt(sModemStatus));
            }else if(!sModemStatus.isEmpty() && deleteCodeId.equals(sModemStatus)) {   // Status = Delete
                sbQuery.append("    AND mo.MODEM_STATUS = "+ Integer.parseInt(sModemStatus));
            }else if(!sModemStatus.isEmpty() && normalCodeId.equals(sModemStatus)) {   // Status = Normal
                sbQuery.append("    AND mo.MODEM_STATUS = "+ Integer.parseInt(sModemStatus));
            }else if(!sModemStatus.isEmpty() && repairCodeId.equals(sModemStatus)){        // Status = Repair                                               
                sbQuery.append("    AND mo.MODEM_STATUS = "+ Integer.parseInt(sModemStatus));
            }else if(!sModemStatus.isEmpty() && securityErrorCodeId.equals(sModemStatus)){        // Status = SecurityError                                               
                sbQuery.append("    AND mo.MODEM_STATUS = "+ Integer.parseInt(sModemStatus));
                }else if(!sModemStatus.isEmpty() && commErrorCodeId.equals(sModemStatus)){        // Status = CommError                                               
                sbQuery.append("    AND mo.MODEM_STATUS = "+ Integer.parseInt(sModemStatus));}  
         }else {
            log.info("deleteCodeId is not exist");
        }           
        
        if (!sModemType.equals(""))
            sbQuery.append("AND   mo.modem = '" + sModemType + "'");

        if (!sModemId.equals("")){
            //sbQuery.append("AND   mo.device_serial = '" + sModemId + "'");
            sbQuery.append("AND   mo.device_serial like '" + sModemId + "%'");
        }
        
        // State

        if (sInstallState.equals("Y"))
            sbQuery.append("AND   mo.install_date IS NOT NULL");
        else if (sInstallState.equals("N"))
            sbQuery.append("AND   mo.install_date IS NULL");

        if (!sMcuType.equals(""))
            sbQuery.append("AND   mcu.mcu_type = " + sMcuType);

        if (!sMcuName.equals(""))
            sbQuery.append("AND   mcu.sys_id LIKE '" + sMcuName + "%'");

        if (!sLocationId.equals(""))
            sbQuery.append("AND   mo.location_Id IN (" + sLocationId + ")");

        if (!sModemFwVer.equals(""))
            sbQuery.append("AND   mo.fw_ver = '" + sModemFwVer + "'");

        if (!sModemSwRev.equals(""))
            sbQuery.append("AND   mo.fw_revision = '" + sModemSwRev + "'");

        if (!sModemHwVer.equals(""))
            sbQuery.append("AND   mo.hw_ver = '" + sModemHwVer + "'");
        
        if (!sModuleBuild.equals(""))
            sbQuery.append("AND   mo.module_revision LIKE '" + sModuleBuild + "%'");

        if (!sInstallStartDate.equals(""))
            sbQuery.append("AND   mo.install_date >= '" + sInstallStartDate + "000000'");

        if (!sInstallEndDate.equals(""))
            sbQuery.append("AND   mo.install_date <= '" + sInstallEndDate + "235959'");

        if (!sLastcommStartDate.equals(""))
            sbQuery.append("AND   mo.last_link_time >= '" + sLastcommStartDate + "000000'");

        if (!sLastcommEndDate.equals(""))
            sbQuery.append("AND   mo.last_link_time <= '" + sLastcommEndDate + "235900'");

        SQLQuery query = null;
        List<Object> dataList = null;
        int dataListLen = 0;
        String datePre24H = DateTimeUtil.calcDate(Calendar.HOUR, -24, "yyyyMMddHHmmss");
        String datePre48H = DateTimeUtil.calcDate(Calendar.HOUR, -48, "yyyyMMddHHmmss");

        // ChartGrid
        if (page != null || isExcel) {
            StringBuilder sbQueryGridInner = new StringBuilder();
            StringBuilder sbQueryGrid = new StringBuilder();
            StringBuilder sbQueryGridCount = new StringBuilder();

            sbQueryGridInner.append("SELECT CASE WHEN mcu.sys_id IS NULL THEN '-' ELSE mcu.sys_id END AS mcuSysId \n");
            sbQueryGridInner.append("     , CASE WHEN mo.mcu_id IS NULL THEN COUNT(modem) \n");
            sbQueryGridInner.append("            ELSE COUNT(mo.mcu_id) END AS totalCnt \n");
            sbQueryGridInner.append("     , SUM(CASE WHEN mo.last_link_time >= :datePre24H  \n");
            sbQueryGridInner.append("      AND   (NOT mo.MODEM_STATUS = " + codeDao.getCodeIdByCode("1.2.7.6") + "\n" );
            sbQueryGridInner.append("      AND   NOT mo.MODEM_STATUS = " + codeDao.getCodeIdByCode("1.2.7.5") +"OR mo.MODEM_STATUS IS NULL)"+" THEN 1 ELSE 0 END) AS value0 \n");
            sbQueryGridInner.append("     , SUM(CASE WHEN mo.last_link_time < :datePre24H ");
            sbQueryGridInner.append("                AND mo.last_link_time >= :datePre48H \n");
            sbQueryGridInner.append("      AND   (NOT mo.MODEM_STATUS = " + codeDao.getCodeIdByCode("1.2.7.6") + "\n" );
            sbQueryGridInner.append("      AND   NOT mo.MODEM_STATUS = " + codeDao.getCodeIdByCode("1.2.7.5") +"OR mo.MODEM_STATUS IS NULL)" +" THEN 1 ELSE 0 END) AS value1 \n");
            sbQueryGridInner.append("     , SUM(CASE WHEN mo.last_link_time < :datePre48H \n");
            sbQueryGridInner.append("      AND   (NOT mo.MODEM_STATUS = " + codeDao.getCodeIdByCode("1.2.7.6") + "\n" );
            sbQueryGridInner.append("      AND   NOT mo.MODEM_STATUS = " + codeDao.getCodeIdByCode("1.2.7.5") +"OR mo.MODEM_STATUS IS NULL)" +" THEN 1 ELSE 0 END) AS value2 \n");
            sbQueryGridInner.append("     , SUM(CASE WHEN mo.last_link_time IS NULL \n");
            sbQueryGridInner.append("      AND   (NOT mo.MODEM_STATUS = " + codeDao.getCodeIdByCode("1.2.7.6") + "\n" );
            sbQueryGridInner.append("      AND   NOT mo.MODEM_STATUS = " + codeDao.getCodeIdByCode("1.2.7.5") + "\n");
            sbQueryGridInner.append("      OR mo.MODEM_STATUS IS NULL ) THEN 1 ELSE 0 END) AS value3 \n");
            sbQueryGridInner.append("     , SUM(CASE WHEN mo.MODEM_STATUS = " + codeDao.getCodeIdByCode("1.2.7.6") + " THEN 1 ELSE 0 END) AS value4 \n");
            sbQueryGridInner.append("     , SUM(CASE WHEN mo.MODEM_STATUS = " + codeDao.getCodeIdByCode("1.2.7.5") + " THEN 1 ELSE 0 END) AS value5 \n");
            sbQueryGridInner.append("     , CASE WHEN mcu.sys_id IS NULL THEN 1 ELSE 0 END AS orderCount \n");
            sbQueryGridInner.append(sbQuery);
            sbQueryGridInner.append("GROUP BY mcu.sys_id, mo.mcu_id \n");

            // Grid
            sbQueryGrid.append(sbQueryGridInner);
            sbQueryGrid.append("ORDER BY orderCount ASC, totalCnt DESC, mcu.sys_id \n");

            query = getSession().createSQLQuery(new SQLWrapper().getQuery(sbQueryGrid.toString()));

            query.setString("datePre24H", datePre24H);
            query.setString("datePre48H", datePre48H);
            query.setInteger("supplierId", Integer.parseInt(supplierId));

			if (!isExcel) {
				query.setFirstResult(start);
				query.setMaxResults(limit);
			}
            
            dataList = query.list();

            // 실제 데이터
            if (dataList != null)
                dataListLen = dataList.size();

            DecimalFormat dfMd = DecimalUtil.getMDStyle(supplierDao.get(Integer.parseInt(supplierId)).getMd());
            Map<String, Object> chartDataMap = null;

            for (int i = 0; i < dataListLen; i++) {
                chartDataMap = new HashMap<String, Object>();
                Object[] resultData = (Object[])dataList.get(i);

                chartDataMap.put("no", dfMd.format(start + i + 1));
                chartDataMap.put("mcuSysId", resultData[0]);
                chartDataMap.put("value0", dfMd.format(resultData[2]));
                chartDataMap.put("value1", dfMd.format(resultData[3]));
                chartDataMap.put("value2", dfMd.format(resultData[4]));
                chartDataMap.put("value3", dfMd.format(resultData[5]));
                chartDataMap.put("value4", dfMd.format(resultData[6]));
                chartDataMap.put("value5", dfMd.format(resultData[7]));

                gridData.add(chartDataMap);
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

            Number count = (Number)query.uniqueResult();
            result.add(count.intValue());
        } else {
            // Chart Data
            StringBuilder sbQueryChart = new StringBuilder();
            sbQueryChart.append("SELECT '0' AS commStatus \n");
            sbQueryChart.append("     , COUNT(mo.id) AS cnt \n");
            sbQueryChart.append(sbQuery);
            sbQueryChart.append("AND   mo.last_link_time >= :datePre24H \n");
            sbQueryChart.append("AND   (NOT mo.MODEM_STATUS = " + codeDao.getCodeIdByCode("1.2.7.6") + "\n");
            sbQueryChart.append("AND   NOT mo.MODEM_STATUS = " + codeDao.getCodeIdByCode("1.2.7.5") + "\n");
            sbQueryChart.append("OR    mo.MODEM_STATUS IS NULL) \n");
            sbQueryChart.append("UNION ALL \n");

            sbQueryChart.append("SELECT '1' AS commStatus \n");
            sbQueryChart.append("     , COUNT(mo.id) AS cnt \n");
            sbQueryChart.append(sbQuery);
            sbQueryChart.append("AND   mo.last_link_time < :datePre24H \n");
            sbQueryChart.append("AND   mo.last_link_time >= :datePre48H \n");
            sbQueryChart.append("AND   (NOT mo.MODEM_STATUS = " + codeDao.getCodeIdByCode("1.2.7.6") + "\n");
            sbQueryChart.append("AND   NOT mo.MODEM_STATUS = " + codeDao.getCodeIdByCode("1.2.7.5") + "\n");
            sbQueryChart.append("OR    mo.MODEM_STATUS IS NULL) \n");
            sbQueryChart.append("UNION ALL \n");

            sbQueryChart.append("SELECT '2' AS commStatus \n");
            sbQueryChart.append("     , COUNT(mo.id) AS cnt \n");
            sbQueryChart.append(sbQuery);
            sbQueryChart.append("AND   mo.last_link_time < :datePre48H \n");
            sbQueryChart.append("AND   (NOT mo.MODEM_STATUS = " + codeDao.getCodeIdByCode("1.2.7.6") + "\n");
            sbQueryChart.append("AND   NOT mo.MODEM_STATUS = " + codeDao.getCodeIdByCode("1.2.7.5") + "\n");
            sbQueryChart.append("OR    mo.MODEM_STATUS IS NULL) \n");
            sbQueryChart.append("UNION ALL \n");

            sbQueryChart.append("SELECT '3' AS commStatus \n");
            sbQueryChart.append("     , COUNT(mo.id) AS cnt \n");
            sbQueryChart.append(sbQuery);
            sbQueryChart.append("AND   mo.last_link_time IS NULL \n");
            sbQueryChart.append("AND   (NOT mo.MODEM_STATUS = " + codeDao.getCodeIdByCode("1.2.7.6") + "\n");
            sbQueryChart.append("AND   NOT mo.MODEM_STATUS = " + codeDao.getCodeIdByCode("1.2.7.5") + "\n");
            sbQueryChart.append("OR    mo.MODEM_STATUS IS NULL) \n");
            sbQueryChart.append("UNION ALL \n");
            
            sbQueryChart.append("SELECT '4' AS commStatus \n");
            sbQueryChart.append("     , COUNT(mo.id) AS cnt \n");
            sbQueryChart.append(sbQuery);
            sbQueryChart.append("AND   mo.MODEM_STATUS = " + codeDao.getCodeIdByCode("1.2.7.6") + "\n");
            sbQueryChart.append("UNION ALL \n");
            
            sbQueryChart.append("SELECT '5' AS commStatus \n");
            sbQueryChart.append("     , COUNT(mo.id) AS cnt \n");
            sbQueryChart.append(sbQuery);
            sbQueryChart.append("AND   mo.MODEM_STATUS = " + codeDao.getCodeIdByCode("1.2.7.5") + "\n");

            query = getSession().createSQLQuery(sbQueryChart.toString());

            query.setString("datePre24H", datePre24H);
            query.setString("datePre48H", datePre48H);
            query.setInteger("supplierId", Integer.parseInt(supplierId));

            dataList = null;
            dataList = query.list();

            // 실제 데이터
            dataListLen = 0;
            if (dataList != null)
                dataListLen = dataList.size();

            Map<String, Object> chartDataMap = null;
            for (int i = 0; i < dataListLen; i++) {
                chartDataMap = new HashMap<String, Object>();
                Object[] resultData = (Object[])dataList.get(i);

                if (resultData[0].toString().equals("0")) {
                    chartDataMap.put("label", "fmtMessage00");
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
                }

                chartData.add(chartDataMap);
            }

            result.add(chartData);
        }

        return result;
    }
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
    public List<Object> getModemSearchGrid(Map<String, Object> condition){
        
        List<Object> gridData   = new ArrayList<Object>();      
        List<Object> result     = new ArrayList<Object>();
        StringBuffer sbQuery    = new StringBuffer();
        
        String sModemType           = StringUtil.nullToBlank(condition.get("sModemType"));
        String sModemId             = StringUtil.nullToBlank(condition.get("sModemId"));
        String sState               = StringUtil.nullToBlank(condition.get("sState"));
        String sInstallState        = StringUtil.nullToBlank(condition.get("sInstallState"));
        String sMcuType             = StringUtil.nullToBlank(condition.get("sMcuType"));
        String sMcuName             = StringUtil.nullToBlank(condition.get("sMcuName"));
        String sModemFwVer          = StringUtil.nullToBlank(condition.get("sModemFwVer"));
        String sModemSwRev          = StringUtil.nullToBlank(condition.get("sModemSwRev"));
        String sModemHwVer          = StringUtil.nullToBlank(condition.get("sModemHwVer"));
        String sModemStatus         = StringUtil.nullToBlank(condition.get("sModemStatus"));
        String sInstallStartDate    = StringUtil.nullToBlank(condition.get("sInstallStartDate"));
        String sInstallEndDate      = StringUtil.nullToBlank(condition.get("sInstallEndDate"));
        String sLastcommStartDate   = StringUtil.nullToBlank(condition.get("sLastcommStartDate"));
        String sLastcommEndDate     = StringUtil.nullToBlank(condition.get("sLastcommEndDate"));
        String sLocationId          = StringUtil.nullToBlank(condition.get("sLocationId"));
        String sModuleBuild			= StringUtil.nullToBlank(condition.get("sModuleBuild"));
        String curPage              = StringUtil.nullToBlank(condition.get("curPage"));
        String sOrder               = StringUtil.nullToBlank(condition.get("sOrder"));
        String sCommState           = StringUtil.nullToBlank(condition.get("sCommState"));
        String supplierId           = StringUtil.nullToBlank(condition.get("supplierId"));
        String gridType             = StringUtil.nullToBlank(condition.get("gridType"));
        String deleteCodeId         = StringUtil.nullToBlank(condition.get("deleteCodeId"));
        String breakDownCodeId      = StringUtil.nullToBlank(condition.get("breakDownCodeId"));
        String normalCodeId         = StringUtil.nullToBlank(condition.get("normalCodeId"));
        String repairCodeId         = StringUtil.nullToBlank(condition.get("repairCodeId"));
        String securityErrorCodeId  = StringUtil.nullToBlank(condition.get("securityErrorCodeId"));
        String commErrorCodeId      = StringUtil.nullToBlank(condition.get("commErrorCodeId"));
        String modelId      		= StringUtil.nullToBlank(condition.get("modelId"));
        String purchaseOrder 		= StringUtil.nullToBlank(condition.get("purchaseOrder"));
        String protocolType 		= StringUtil.nullToBlank(condition.get("protocolType"));
        String fwGadget 			= StringUtil.nullToBlank(condition.get("fwGadget"));
        
        sbQuery.append("\nSELECT mo.device_serial  		 AS modemDeviceSerial, ");
        sbQuery.append("\n       mo.modem         		 AS modemType, ");
        sbQuery.append("\n       mcu.sys_id      		 AS mcuSysID, ");
        sbQuery.append("\n       mo.fw_ver       		 AS fwVer, ");
        sbQuery.append("\n       mo.hw_ver      		 AS hwVer, ");
        sbQuery.append("\n       CASE WHEN mo.last_link_time IS NULL THEN '' ELSE mo.last_link_time END AS lastCommDate, ");
        sbQuery.append("\n       mo.install_date  		 AS InstallDate, ");
        sbQuery.append("\n       vendor.name      		 AS vendorName, ");
        sbQuery.append("\n       model.name       		 AS modelName, ");
        sbQuery.append("\n       mo.id            		 AS id, ");
        sbQuery.append("\n       mo.device_serial  		 AS deviceSerial, ");
        sbQuery.append("\n       mo.ip_addr        		 AS ipAddr, ");
        sbQuery.append("\n       mo.mac_addr      		 AS macAddr, ");
        sbQuery.append("\n       mo.modem         		 AS modemTypeCodeName, ");
        sbQuery.append("\n       co.code           		 AS modemStatusByCode, ");
        sbQuery.append("\n       mo.PROTOCOL_TYPE        AS PROTOCOL_TYPE, ");
        sbQuery.append("\n       mo.fw_revision          AS fw_revision, ");
        sbQuery.append("\n       mo.PHONE_NUMBER         AS phone, ");
        sbQuery.append("\n       mo.location_id          AS locationId, ");
        sbQuery.append("\n       mo.GS1         		 AS gs1, ");
        sbQuery.append("\n       mo.PO         			 AS po, ");
        sbQuery.append("\n       mo.SIM_NUMBER         	 AS simNumber, ");
        sbQuery.append("\n       mo.ICC_ID         		 AS iccId, ");
        sbQuery.append("\n       mo.MANUFACTURED_DATE    AS manufacturedDate, ");
        sbQuery.append("\n       mo.sw_ver       		 AS swVer, ");
        sbQuery.append("\n       mo.IMEI         		 AS imei, ");
        sbQuery.append("\n       mo.MODULE_VERSION 		 AS moduleVersion, ");
        sbQuery.append("\n       mo.MODULE_REVISION		 AS moduleRevision ");
        sbQuery.append("\nFROM modem mo ");
        sbQuery.append("\n     LEFT OUTER JOIN mcu mcu ");
        sbQuery.append("\n     ON mo.mcu_id = mcu.id ");
        sbQuery.append("\n     LEFT OUTER JOIN devicemodel model ");
        sbQuery.append("\n     ON mo.devicemodel_id = model.id ");
        sbQuery.append("\n     LEFT OUTER JOIN devicevendor vendor ");
        sbQuery.append("\n     ON model.devicevendor_id = vendor.id ");
        sbQuery.append("\n     LEFT OUTER JOIN CODE co              "); 
        sbQuery.append("\n     ON ( mo.modem_status = co.ID)        ");
        sbQuery.append("\nWHERE mo.supplier_id = :supplierId ");
    
        if(!"".equals(deleteCodeId)) {  
            if(sModemStatus.isEmpty()){  // Status = all
                sbQuery.append("    AND (mo.MODEM_STATUS <>" +deleteCodeId + "OR mo.MODEM_STATUS IS NULL)" );
            }else if(!sModemStatus.isEmpty() && breakDownCodeId.equals(sModemStatus)){     // Status = BreakDown                                                     
                sbQuery.append("    AND mo.MODEM_STATUS = "+ Integer.parseInt(sModemStatus));
            }else if(!sModemStatus.isEmpty() && deleteCodeId.equals(sModemStatus)) {   // Status = Delete
                sbQuery.append("    AND mo.MODEM_STATUS = "+ Integer.parseInt(sModemStatus));
            }else if(!sModemStatus.isEmpty() && normalCodeId.equals(sModemStatus)) {   // Status = Normal
                sbQuery.append("    AND mo.MODEM_STATUS = "+ Integer.parseInt(sModemStatus));
            }else if(!sModemStatus.isEmpty() && repairCodeId.equals(sModemStatus)){        // Status = Repair                                               
                sbQuery.append("    AND mo.MODEM_STATUS = "+ Integer.parseInt(sModemStatus));
            }else if(!sModemStatus.isEmpty() && securityErrorCodeId.equals(sModemStatus)){        // Status = SecurityError                                               
                sbQuery.append("    AND mo.MODEM_STATUS = "+ Integer.parseInt(sModemStatus));
            }else if(!sModemStatus.isEmpty() && commErrorCodeId.equals(sModemStatus)){        // Status = CommError                                               
                sbQuery.append("    AND mo.MODEM_STATUS = "+ Integer.parseInt(sModemStatus));}
         }else {
            log.info("deleteCodeId is not exist");
        }   
        
        if(!sModemType.equals(""))
            sbQuery.append("\nAND   mo.MODEM = '"+ sModemType +"' ");
        
        if(fwGadget.equals("Y")){
        	StringTokenizer st = new StringTokenizer(sModemId, ", ");
            String deviceIds="";
            for(int i = 0 ; st.hasMoreTokens() ; i++){
            	deviceIds += ("(0,'" + st.nextToken() +"'),");
    		}
            if(deviceIds.contains(",")){
            	deviceIds = deviceIds.substring(0, deviceIds.length()-1);
            }
            if(!deviceIds.equals(""))
            	sbQuery.append("\nAND   (0, mo.DEVICE_SERIAL) IN ("+ deviceIds + ") ");
        }else{
        	if(!sModemId.equals("")){
                //sbQuery.append("\nAND   mo.DEVICE_SERIAL = '"+ sModemId + "' ");
                sbQuery.append("\nAND   mo.DEVICE_SERIAL like '"+ sModemId + "%' ");
            }
        }
        
        // State        
        
        if(sInstallState.equals("Y")) 
            sbQuery.append("\nAND   mo.INSTALL_DATE IS NOT NULL ");
        else if(sInstallState.equals("N"))
            sbQuery.append("\nAND   mo.INSTALL_DATE IS NULL ");
        
        if(!sMcuType.equals(""))
            sbQuery.append("\nAND   mcu.MCU_TYPE = "+ sMcuType + " ");
        
        if(!sMcuName.equals("")){
            if(sMcuName.equals("-")){
                sbQuery.append("\nAND   mcu.SYS_ID IS NULL");               
            }else{
                sbQuery.append("\nAND   mcu.SYS_ID = '"+ sMcuName +"' ");
            }
        }
        if(!sLocationId.equals(""))
            sbQuery.append("\nAND   mo.LOCATION_ID IN (" + sLocationId + ") ");
        
        if(!sModemFwVer.equals(""))
            sbQuery.append("\nAND   mo.FW_VER = '"+ sModemFwVer + "' ");
    
        if(!sModemSwRev.equals(""))
            sbQuery.append("\nAND   mo.FW_REVISION = '"+ sModemSwRev + "' ");       
    
        if(!sModemHwVer.equals(""))
            sbQuery.append("\nAND   mo.HW_VER = '"+ sModemHwVer + "' ");
        
        if(!sModuleBuild.equals(""))
        	sbQuery.append("\nAND   mo.MODULE_REVISION LIKE '"+sModuleBuild+"%' ");
        
        if(!purchaseOrder.equals(""))
        	sbQuery.append("\nAND   mo.PO = '"+ purchaseOrder + "' ");
        
        if(!protocolType.equals(""))
        	sbQuery.append("\nAND   mo.PROTOCOL_TYPE = '"+ protocolType + "' ");
        
        if(!modelId.equals(""))
            sbQuery.append("\nAND   model.id = '"+ modelId + "' ");
        
        if(!sInstallStartDate.equals(""))
            sbQuery.append("\nAND   mo.INSTALL_DATE >= '"+ sInstallStartDate +"000000' ");
    
        if(!sInstallEndDate.equals(""))
            sbQuery.append("\nAND   mo.INSTALL_DATE <= '"+ sInstallEndDate +"235959' ");

        if(!sLastcommStartDate.equals(""))
            sbQuery.append("\nAND   CASE WHEN mo.LAST_LINK_TIME IS NULL THEN '' ELSE mo.LAST_LINK_TIME END >= '"+ sLastcommStartDate +"000000' ");
        
        if(!sLastcommEndDate.equals(""))
            sbQuery.append("\nAND   CASE WHEN mo.LAST_LINK_TIME IS NULL THEN '' ELSE mo.LAST_LINK_TIME END <= '"+ sLastcommEndDate +"235900' ");
        
        if(sCommState.equals("0"))
            sbQuery.append("\nAND   mo.LAST_LINK_TIME >= :datePre24H ");
        else if(sCommState.equals("1"))
            sbQuery.append("\nAND   mo.LAST_LINK_TIME < :datePre24H AND mo.LAST_LINK_TIME >= :datePre48H ");
        else if(sCommState.equals("2"))
            sbQuery.append("\nAND   mo.LAST_LINK_TIME < :datePre48H ");
                  
        StringBuffer sbQueryData = new StringBuffer();
        sbQueryData.append(sbQuery);
        
        if(sOrder.equals("1") || sOrder == null)
            sbQueryData.append("\nORDER BY lastCommDate DESC, mo.DEVICE_SERIAL ASC ");            
        else if(sOrder.equals("2"))
            sbQueryData.append("\nORDER BY lastCommDate, mo.DEVICE_SERIAL ASC ");
        else if(sOrder.equals("3"))
            sbQueryData.append("\nORDER BY mo.INSTALL_DATE DESC, mo.DEVICE_SERIAL ASC ");
        else if(sOrder.equals("4"))
            sbQueryData.append("\nORDER BY mo.INSTALL_DATE, mo.DEVICE_SERIAL ASC ");      
        else if(sOrder.equals("5"))
            sbQueryData.append("\nORDER BY mo.DEVICE_SERIAL");     

        SQLQuery query = getSession().createSQLQuery(sbQueryData.toString());
        
        String datePre24H = DateTimeUtil.calcDate(Calendar.HOUR, -24, "yyyyMMddHHmmss");
        String datePre48H = DateTimeUtil.calcDate(Calendar.HOUR, -48, "yyyyMMddHHmmss");        
        
        if (sCommState.equals("0")) {
            query.setString("datePre24H", datePre24H);
        } else if (sCommState.equals("1")){
            query.setString("datePre24H", datePre24H);
            query.setString("datePre48H", datePre48H);
        } else if (sCommState.equals("2")) {
            query.setString("datePre48H", datePre48H);
        }

        query.setInteger("supplierId", Integer.parseInt(supplierId));

        int firstIdx =0;
        if (gridType.equals("extjs")) {
            String strPage =  String.valueOf(condition.get("page"));
            String strPageSize =  String.valueOf(condition.get("pageSize"));
            
            /*String strPage =  Integer.toString(intPage);
            String strPageSize = Integer.toString(intPageSize); */

            int pageCommaIndex = strPage.indexOf(".");
            int pageSizeCommaIndex = strPageSize.indexOf(".");
            int page = -1;
            int pageSize = -1;
            
            if(pageCommaIndex > -1)
                page = Integer.parseInt(strPage.substring(0, pageCommaIndex));
            else
                page = Integer.parseInt(strPage);
            
            if(pageSizeCommaIndex > -1)
                pageSize = Integer.parseInt(strPageSize.substring(0, pageSizeCommaIndex));
            else
                pageSize = Integer.parseInt(strPageSize);

            int firstResult = page * pageSize;
            
            query.setFirstResult(firstResult);      
            query.setMaxResults(pageSize);
        } else {
            // Paging
            int rowPerPage = CommonConstants.Paging.ROWPERPAGE.getPageNum();
            firstIdx  = Integer.parseInt(curPage) * rowPerPage;

            String excelList            = StringUtil.nullToBlank(condition.get("excelList"));
            if (excelList == "") {  
                query.setFirstResult(firstIdx);
                query.setMaxResults(rowPerPage);
            } else {
                firstIdx = 1;
            }
        }

        List dataList = query.list();

        // 전체 건수
		StringBuffer countQuery = new StringBuffer();
		countQuery.append("\n SELECT COUNT(countTotal.modemType) ");
		countQuery.append("\n FROM (  ");
		countQuery.append(sbQuery);
		countQuery.append("\n ) countTotal ");
        
        SQLQuery countQueryObj = getSession().createSQLQuery(countQuery.toString());
        
		if (sCommState.equals("0"))
			countQueryObj.setString("datePre24H", datePre24H);
		else if (sCommState.equals("1")) {
			countQueryObj.setString("datePre24H", datePre24H);
			countQueryObj.setString("datePre48H", datePre48H);
		} else if (sCommState.equals("2"))
			countQueryObj.setString("datePre48H", datePre48H);
        
        countQueryObj.setInteger("supplierId", Integer.parseInt(supplierId));
        Number totalCount = (Number)countQueryObj.uniqueResult();
        
        result.add(totalCount.toString());
        
        
        // 실제 데이터
        int dataListLen = 0;
        if(dataList != null)
            dataListLen= dataList.size();
        
        for (int i = 0; i < dataListLen; i++) {
            
            HashMap chartDataMap = new HashMap();
            Object[] resultData = (Object[]) dataList.get(i);
            
            List<Code> codeModemType = codeDao.getChildCodes(Code.MODEM_TYPE);

            for (int j = 0; j < codeModemType.size(); j++) {
                if (resultData[1].equals(codeModemType.get(j).getName())) {
                    if (codeModemType.get(j).getDescr() != null) {
                        resultData[1] = codeModemType.get(j).getDescr().toString();
                    } else {
                        resultData[1] = resultData[1];
                    }
                }
            }
            
            //HW Ver
            List<Code> codeHwVer = codeDao.getChildCodes(Code.MODEM_HW_VERSION);
            
            for (int j = 0; j < codeHwVer.size(); j++) {
                if (codeHwVer.get(j).getName().equals(resultData[4])) {
                    if (codeHwVer.get(j).getDescr() != null) {
                        resultData[4] = codeHwVer.get(j).getDescr().toString();
                    }
                }
            }
            
            chartDataMap.put("no",           totalCount.intValue() -i - firstIdx );                       
            chartDataMap.put("modemDeviceSerial",      resultData[0]);                 
            chartDataMap.put("modemType",    resultData[1]);
            chartDataMap.put("mcuSysId",     resultData[2]);
            chartDataMap.put("ver",          StringUtil.nullToBlank(resultData[3]) + (resultData[4] == null ? "" : " / ")+ StringUtil.nullToBlank(resultData[4]));
            chartDataMap.put("lastCommDate", resultData[5]);
            if (resultData[5] != null && !"".equals(resultData[5])){
                if(Long.parseLong((resultData[5].toString())) >= Long.parseLong(datePre24H))
                    chartDataMap.put("activityStatus", "A24h");
                else if(Long.parseLong((resultData[5].toString())) < Long.parseLong(datePre24H) && Long.parseLong((resultData[5].toString())) >= Long.parseLong(datePre48H))
                    chartDataMap.put("activityStatus", "NA24h");
                else if(Long.parseLong((resultData[5].toString())) < Long.parseLong(datePre48H))
                    chartDataMap.put("activityStatus", "NA48h");
            }else{
                chartDataMap.put("activityStatus", "unknown");
            }
            chartDataMap.put("installDate",  resultData[6]);            
            chartDataMap.put("vendorName",   resultData[7]);
            chartDataMap.put("deviceName",   resultData[8]);
            chartDataMap.put("id",           resultData[9]);
            chartDataMap.put("deviceSerial", resultData[10]);
            chartDataMap.put("ipAddr",       resultData[11]);
            chartDataMap.put("macAddr",      resultData[12]);
            chartDataMap.put("modemTypeCodeName",    resultData[13]);
            chartDataMap.put("modemStatusByCode",    resultData[14]);
            chartDataMap.put("protocolType",    resultData[15]);
            chartDataMap.put("fwVer",    resultData[3]);
            chartDataMap.put("hwVer",    resultData[4]);
            chartDataMap.put("fwRevison",    resultData[16]);
            chartDataMap.put("phone",    resultData[17]);
            // chartDataMap.put("locationId",    resultData[18]);
            chartDataMap.put("locationId",    resultData[18]);	// UPDATE SP-704 (REVIVE)
            chartDataMap.put("gs1",    resultData[19]);
            chartDataMap.put("po",    resultData[20]);
            chartDataMap.put("simNumber",    resultData[21]);
            chartDataMap.put("iccId",    resultData[22]);
            chartDataMap.put("manufacturedDate",    resultData[23]);
            chartDataMap.put("swVer",    resultData[24]);
            chartDataMap.put("imei",    resultData[25]);
            chartDataMap.put("module",    StringUtil.nullToBlank(resultData[26])+(resultData[27] == null ? "" : " / ")+StringUtil.nullToBlank(resultData[27]) );
            
            gridData.add(chartDataMap);
            
        }       
        
        result.add(gridData);
        
        return result;
    }
    
    @SuppressWarnings({"unchecked", "rawtypes"})
    @Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
    public List<Object> getModemSearchGrid2(Map<String, Object> condition){
        
        List<Object> gridData   = new ArrayList<Object>();      
        List<Object> result     = new ArrayList<Object>();
        StringBuffer sbQuery    = new StringBuffer();
        
        String sModemType           = StringUtil.nullToBlank(condition.get("sModemType"));
        String sModemId             = StringUtil.nullToBlank(condition.get("sModemId"));
        String sState               = StringUtil.nullToBlank(condition.get("sState"));
        String sInstallState        = StringUtil.nullToBlank(condition.get("sInstallState"));
        
        String sMcuType             = StringUtil.nullToBlank(condition.get("sMcuType"));
        String sMcuName             = StringUtil.nullToBlank(condition.get("sMcuName"));
        String sModemSwVer          = StringUtil.nullToBlank(condition.get("sModemSwVer"));
        String sModemSwRev          = StringUtil.nullToBlank(condition.get("sModemSwRev"));
        String sModemHwVer          = StringUtil.nullToBlank(condition.get("sModemHwVer"));
        
        String sInstallStartDate    = StringUtil.nullToBlank(condition.get("sInstallStartDate"));
        String sInstallEndDate      = StringUtil.nullToBlank(condition.get("sInstallEndDate"));
        
        String sLastcommStartDate   = StringUtil.nullToBlank(condition.get("sLastcommStartDate"));
        String sLastcommEndDate     = StringUtil.nullToBlank(condition.get("sLastcommEndDate"));
        String sLocationId          = StringUtil.nullToBlank(condition.get("sLocationId"));
        
        String curPage              = StringUtil.nullToBlank(condition.get("curPage"));
        String sOrder               = StringUtil.nullToBlank(condition.get("sOrder"));
        String sCommState           = StringUtil.nullToBlank(condition.get("sCommState"));
                                     
        String supplierId           = StringUtil.nullToBlank(condition.get("supplierId"));
    	String sModemFwVer = StringUtil.nullToBlank(condition.get("sModemFwVer"));
    	Boolean chkParent = condition.get("chkParent") == null ? true : (Boolean)condition.get("chkParent");

    	 sbQuery.append("   SELECT mo.DEVICE_SERIAL  AS modemDeviceSerial                      \n")
	         .append("        , mo.MODEM          AS modemType                              \n")
	         .append("        , mcu.SYS_ID        AS mcuSysID                               \n")
	         /*
	         .append("        , code1.NAME        AS swVer                                  \n")
	         .append("        , code2.NAME        AS hwVer                                  \n")
	         */
	         .append("        , mo.SW_VER         AS swVer                                  \n")
	         .append("        , mo.HW_VER         AS hwVer                                  \n")
	         
	         .append("        , CASE WHEN mo.LAST_LINK_TIME is null THEN '' ELSE mo.LAST_LINK_TIME END AS lastCommDate                           \n")
	         .append("        , mo.INSTALL_DATE   AS InstallDate                            \n")             
	         .append("        , device.vendorName   AS vendorName                           \n")
	         .append("        , device.modelName    AS modelName                            \n")             
	         .append("        , mo.id               AS id                                     \n")
	         .append("        , mo.DEVICE_SERIAL    AS deviceSerial                         \n")
	         .append("        , mo.IP_ADDR          AS ipAddr                               \n")
	         .append("        , mo.MAC_ADDR         AS macAddr                              \n")
	         .append("       FROM MODEM mo                                                  \n")
	         .append("  LEFT OUTER JOIN MCU mcu                                             \n")
	         .append("    ON (mo.MCU_ID = mcu.ID)                                           \n")
	         .append("  LEFT OUTER JOIN (                                                   \n")
	         .append("            SELECT model.ID    AS modelId                             \n")
	         .append("                 , model.NAME  AS modelName                           \n")
	         .append("                 , vendor.ID   AS vendorId                            \n")
	         .append("                 , vendor.NAME as vendorName                          \n")
	         .append("             FROM DEVICEMODEL model                                   \n")
	         .append("             LEFT OUTER JOIN DEVICEVENDOR vendor                      \n")
	         .append("                ON (model.DEVICEVENDOR_ID = vendor.ID)                \n")
	         .append("             ) device                                                 \n")
	         .append("          ON (mo.DEVICEMODEL_ID = device.modelId)                     \n")
	         
	         /*
	          *  SwVer / HwVer : Code -> String 변경
	         .append(" LEFT OUTER JOIN CODE code1                                           \n")
	         .append("   ON (mo.SW_VER = code1.ID)                                          \n")
	         .append(" LEFT OUTER JOIN CODE code2                                           \n")
	         .append("   ON (mo.HW_VER = code2.ID)                                          \n")
	         */
	         .append("   WHERE mo.SUPPLIER_ID = :supplierId                                 \n");
	          
        if(!sModemType.equals(""))
            sbQuery.append("     AND mo.MODEM = '"+ sModemType +"'");
        
        if(!sModemId.equals(""))
            sbQuery.append("     AND mo.DEVICE_SERIAL = '"+ sModemId + "'");
                
        // State        
        
        if(sInstallState.equals("Y")) 
            sbQuery.append("     AND mo.INSTALL_DATE IS NOT NULL");
        else if(sInstallState.equals("N"))
            sbQuery.append("     AND mo.INSTALL_DATE IS NULL");
            
                
        
        if(!sMcuType.equals(""))
            sbQuery.append("     AND mcu.MCU_TYPE = "+ sMcuType );
        
        /*if(!sMcuName.equals(""))
            sbQuery.append("     AND mcu.SYS_ID LIKE '%"+ sMcuName +"%'");*/                
        
        if(!sLocationId.equals(""))
            sbQuery.append("     AND mo.LOCATION_ID IN (" + sLocationId + ")");
        
        if(!sModemSwVer.equals(""))
            sbQuery.append("     AND mo.SW_VER = '"+ sModemSwVer + "'" );
    
        if(!sModemSwRev.equals(""))
            sbQuery.append("     AND mo.FW_REVISION = '"+ sModemSwRev + "'");       
    
        if(!sModemHwVer.equals(""))
            sbQuery.append("     AND mo.HW_VER = '"+ sModemHwVer + "'" );
        
        if(chkParent){
            sbQuery.append("     AND mo.FW_VER IS NOT NULL" );
            sbQuery.append("     AND mo.FW_VER = '"+ sModemFwVer + "'" );
            if(!sMcuName.equals("") && sMcuName!=null){
            	if(sMcuName.equals("-"))
					sbQuery.append(" AND mcu.SYS_ID is null ");
				else
	                sbQuery.append("     AND mcu.SYS_ID LIKE '%"+ sMcuName +"%'");              
            }
        }else{
        	if(!sMcuName.equals(""))
                sbQuery.append("     AND mcu.SYS_ID LIKE '%"+ sMcuName +"%'");              
        }
        if(!sInstallStartDate.equals(""))
            sbQuery.append("     AND mo.INSTALL_DATE >= '"+ sInstallStartDate +"000000'");
    
        if(!sInstallEndDate.equals(""))
            sbQuery.append("     AND mo.INSTALL_DATE <= '"+ sInstallEndDate +"235959'");

        
        if(!sLastcommStartDate.equals(""))
            sbQuery.append("     AND CASE WHEN mo.LAST_LINK_TIME is null THEN '' ELSE mo.LAST_LINK_TIME END >= '"+ sLastcommStartDate +"000000'");
        
        if(!sLastcommEndDate.equals(""))
            sbQuery.append("     AND CASE WHEN mo.LAST_LINK_TIME is null THEN '' ELSE mo.LAST_LINK_TIME END <= '"+ sLastcommEndDate +"235900'");
        
        if(sCommState.equals("0"))
            sbQuery.append("     AND mo.LAST_LINK_TIME  >= :datePre24H \n");
        else if(sCommState.equals("1"))
            sbQuery.append("     AND mo.LAST_LINK_TIME <  :datePre24H " +
                           "     AND mo.LAST_LINK_TIME >= :datePre48H \n");
        else if(sCommState.equals("2"))
            sbQuery.append("     AND mo.LAST_LINK_TIME <  :datePre48H ");
        
        
                  
        StringBuffer sbQueryData = new StringBuffer();
        sbQueryData.append(sbQuery);
        
        if(sOrder.equals("1") || sOrder == null)
            sbQueryData.append("    ORDER BY lastCommDate DESC                 \n");            
        else if(sOrder.equals("2"))
            sbQueryData.append("    ORDER BY lastCommDate                      \n");
        else if(sOrder.equals("3"))
            sbQueryData.append("    ORDER BY mo.INSTALL_DATE DESC                   \n");
        else if(sOrder.equals("4"))
            sbQueryData.append("    ORDER BY mo.INSTALL_DATE                        \n");       
        
        SQLQuery query = getSession().createSQLQuery(sbQueryData.toString());
        
        String datePre24H = DateTimeUtil.calcDate(Calendar.HOUR, -24, "yyyyMMddHHmmss");
        String datePre48H = DateTimeUtil.calcDate(Calendar.HOUR, -48, "yyyyMMddHHmmss");        
        
        if(sCommState.equals("0"))
            query.setString("datePre24H", datePre24H);
        else if(sCommState.equals("1")){
            query.setString("datePre24H", datePre24H);
            query.setString("datePre48H", datePre48H);
        }else if(sCommState.equals("2"))
            query.setString("datePre48H", datePre48H);
        
        query.setInteger("supplierId", Integer.parseInt(supplierId));
        
        // Paging
    /*  int rowPerPage = CommonConstants.Paging.ROWPERPAGE.getPageNum();
        int firstIdx  = Integer.parseInt(curPage) * rowPerPage;
        
        String excelList            = StringUtil.nullToBlank(condition.get("excelList"));
        if(excelList==""){  
            query.setFirstResult(firstIdx);
            query.setMaxResults(rowPerPage);
        }else{
            firstIdx = 1;
        }
        */
        
        int firstIdx=1;
        
        
        List dataList = null;
        dataList = query.list();
        
        // 전체 건수
        StringBuffer countQuery = new StringBuffer();
        countQuery.append("\n SELECT COUNT(countTotal.modemType) ");
        countQuery.append("\n FROM (  ");
        countQuery.append(sbQuery);
        countQuery.append("\n ) countTotal ");
        
        SQLQuery countQueryObj = getSession().createSQLQuery(countQuery.toString());
        
        if(sCommState.equals("0"))
            countQueryObj.setString("datePre24H", datePre24H);
        else if(sCommState.equals("1")){
            countQueryObj.setString("datePre24H", datePre24H);
            countQueryObj.setString("datePre48H", datePre48H);
        }else if(sCommState.equals("2"))
            countQueryObj.setString("datePre48H", datePre48H);
        
        countQueryObj.setInteger("supplierId", Integer.parseInt(supplierId));
        
        Number totalCount = (Number)countQueryObj.uniqueResult();
        
        result.add(totalCount.toString());
        
        
        // 실제 데이터
        int dataListLen = 0;
        if(dataList != null)
            dataListLen= dataList.size();
        
        for(int i=0 ; i < dataListLen ; i++){
            
            HashMap chartDataMap = new HashMap();
            Object[] resultData = (Object[]) dataList.get(i);
            
            chartDataMap.put("no",           totalCount.intValue() -i - firstIdx );                       
            chartDataMap.put("modemDeviceSerial",      resultData[0]);                 
            chartDataMap.put("modemType",    resultData[1]);
            chartDataMap.put("mcuSysId",     resultData[2]);
            chartDataMap.put("ver",          StringUtil.nullToBlank(resultData[3]) + " / " + StringUtil.nullToBlank(resultData[4]));
            chartDataMap.put("lastCommDate", resultData[5]);
            chartDataMap.put("installDate",  resultData[6]);            
            chartDataMap.put("vendorName",   resultData[7]);
            chartDataMap.put("deviceName",   resultData[8]);
            chartDataMap.put("id",           resultData[9]);
            chartDataMap.put("deviceSerial", resultData[10]);
            chartDataMap.put("ipAddr",       resultData[11]);
            chartDataMap.put("macAddr",      resultData[12]);
            gridData.add(chartDataMap);
            
        }       
        
        result.add(gridData);
        
        return result;
        
    }
    
    
    @SuppressWarnings({"unchecked", "rawtypes"})
    @Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
    public List<Object> getModemLogChart(Map<String, Object> condition){
        
        String sModemId          = StringUtil.nullToBlank(condition.get("modemId"));
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
            
            sbQueryDate.append("SELECT '" + daysList.get(i) + "'  AS logDate  , 0 AS commLog, 0 AS updateLog, 0 AS brokenLog, 0 AS operLog , 0 AS modemPowerLog FROM COMMLOG \n UNION ALL \n" );
        }
        
        // chartData
        sbQuery = new StringBuffer();
        
        sbQuery.append(" SELECT logDate                         \n")
               .append("     , SUM(commLog)                     \n")
               .append("     , SUM(updateLog)                   \n")
               .append("     , SUM(brokenLog)                   \n")
               .append("     , SUM(operLog)                     \n")
               .append("     , SUM(modemPowerLog)               \n")
               .append("     FROM (                             \n")
               .append(sbQueryDate)            
               .append("  SELECT START_DATE  AS logDate         \n")
               .append("      , COUNT(ID)    AS commLog         \n")
               .append("      , 0            AS updateLog       \n")
               .append("      , 0            AS brokenLog       \n")
               .append("      , 0            AS operLog         \n")
               .append("      , 0            AS modemPowerLog   \n")
               .append("   FROM COMMLOG                         \n")
               .append("  WHERE START_DATE >= :sStartDate       \n")
               .append("    AND START_DATE <= :sEndDate         \n")
               .append("    AND SENDER_ID   = :sModemId         \n")
               .append("  GROUP BY START_DATE                   \n")
               .append("  UNION ALL                             \n")
               
               .append("  SELECT YYYYMMDD   AS logDate          \n")
               .append("      , 0           AS commLog          \n")
               .append("      , 0           AS updateLog        \n")
               .append("      , 0           AS brokenLog        \n")
               .append("      , COUNT(ID)   AS operLog          \n")
               .append("      , 0           AS modemPowerLog    \n")
               .append("   FROM OPERATION_LOG                   \n")
               .append("  WHERE YYYYMMDD   >= :sStartDate       \n")
               .append("    AND YYYYMMDD   <= :sEndDate         \n")
               .append("    AND TARGET_NAME = :sModemId         \n")
               .append("  GROUP BY YYYYMMDD                     \n")
               .append("  UNION ALL                             \n")
               
               .append("  SELECT YYYYMMDD   AS logDate          \n")
               .append("      , 0           AS commLog          \n")
               .append("      , 0           AS updateLog        \n")
               .append("      , 0           AS brokenLog        \n")
               .append("      , 0           AS operLog          \n")
               .append("      , COUNT(DEVICE_ID)   AS modemPowerLog     \n")
               .append("   FROM MODEM_POWER_LOG                 \n")
               .append("  WHERE YYYYMMDD   >= :sStartDate       \n")
               .append("    AND YYYYMMDD   <= :sEndDate         \n")
               .append("    AND DEVICE_ID   = :sModemId         \n")
               .append("  GROUP BY YYYYMMDD                     \n")
               
               .append("  ) rowData                             \n")
               .append("  GROUP BY logDate                      \n")
               .append("  ORDER BY logDate                      \n");       
        
            
        
        SQLQuery query = getSession().createSQLQuery(sbQuery.toString());
        
        query.setString("sStartDate", sStartDate);
        query.setString("sEndDate",   sEndDate);
        query.setString("sModemId",   sModemId);
        
        List dataList = null;
        dataList = query.list();
        
        int dataListLen = 0;
        if(dataList != null)
            dataListLen = dataList.size();
        
        for(int i=0 ; i < dataListLen ; i++){
            
            HashMap chartDataMap = new HashMap();
            Object[] resultData = (Object[]) dataList.get(i);
            
            chartDataMap.put("xTag",          resultData[0].toString());
            
            chartDataMap.put("commLog",       resultData[1].toString());
            chartDataMap.put("updateLog",     resultData[2].toString());
            chartDataMap.put("brokenLog",     resultData[3].toString());
            chartDataMap.put("operationLog",  resultData[4].toString());
            chartDataMap.put("modemPowerLog", resultData[5].toString());
            
            chartData.add(chartDataMap);
            
        }
        
        result.add(chartData);
        
        return result;

    }
    
/*  모뎀가젯의 history탭 : 구현이 안되어 있는 부분이고 필요없는 기능이라 삭제
    @SuppressWarnings("unchecked")
    public List<Object> getModemLogGrid(Map<String, Object> condition){
        
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
            t1.put("logDateTime","일시_1");
            t1.put("status","상태_1");
            
            t2.put("number","2");
            t2.put("receiveTime","수신시간_2");
            t2.put("logDateTime","일시_2");
            t2.put("status","상태_2");

            t3.put("number","3");
            t3.put("receiveTime","수신시간_3");
            t3.put("logDateTime","일시_3");
            t3.put("status","상태_3");

            t4.put("number","4");
            t4.put("receiveTime","수신시간_4");
            t4.put("logDateTime","일시_4");
            t4.put("status","상태_4");
            
            t5.put("number","5");
            t5.put("receiveTime","수신시간_5");
            t5.put("logDateTime","일시_5");
            t5.put("status","상태_5");

            t6.put("number","6");
            t6.put("receiveTime","수신시간_6");
            t6.put("logDateTime","일시_6");
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
    */
    @SuppressWarnings({"unchecked", "rawtypes"})
    @Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
    public Map<String, Object> getModemSearchCondition(){
        
        HashMap result           = new HashMap();
        StringBuffer sbQuery     = new StringBuffer();
        
        sbQuery.append("  SELECT MIN(INSTALL_DATE) AS minDate       ")
               .append("       , MAX(INSTALL_DATE) AS maxDate       ")
               .append("    FROM MODEM                              ");                           
        
        SQLQuery query = getSession().createSQLQuery(sbQuery.toString());
        
        List dateList = null;
        dateList = query.list();
        
        
        Object[] resultData = (Object[]) dateList.get(0);
        
        Map<String, String> yesterday = DateTimeUtil.calcDate(Calendar.HOUR, -24);
        Map<String, String> today     = DateTimeUtil.calcDate(Calendar.HOUR, 0);
        

        String yDate = yesterday.get("date").replace("-", "/"); 
        String tDate = today.get("date").replace("-", "/");
        
        
        if(resultData[0] == null || "".equals(resultData[0]))
            result.put("installMinDate" , yDate);
        else
            result.put("installMinDate" , resultData[0].toString().subSequence(0, 4) + "/" + resultData[0].toString().subSequence(4, 6) +"/" + resultData[0].toString().subSequence(6, 8));
        
        if(resultData[1] == null || "".equals(resultData[1]))
            result.put("installMaxDate" , tDate);
        else
            result.put("installMaxDate" , resultData[1].toString().subSequence(0, 4) + "/" + resultData[1].toString().subSequence(4, 6) +"/" + resultData[1].toString().subSequence(6, 8));
            
        result.put("yesterday" , yDate);
        result.put("today" , tDate);
        
        return result;      
    }   
    
    @SuppressWarnings({"unchecked", "rawtypes"})
    @Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
    public List<Object> getModemCommLog(Map<String, Object> condition){
        String sModemId          = StringUtil.nullToBlank(condition.get("modemId"));
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
               .append("    AND SENDER_ID  =  :sModemId      \n")
               .append("  ORDER BY START_DATE_TIME           \n");
        
        SQLQuery query = getSession().createSQLQuery(sbQuery.toString());
        query.setString("sStartDate",   sStartDate);
        query.setString("sEndDate",     sEndDate);
        query.setString("sModemId",     sModemId);
        
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
                  .append("    AND SENDER_ID  =  :sModemId      \n");
           
        SQLQuery countQueryObj = getSession().createSQLQuery(countQuery.toString());
        countQueryObj.setString("sStartDate",   sStartDate);
        countQueryObj.setString("sEndDate",     sEndDate);
        countQueryObj.setString("sModemId",     sModemId);  
        
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
    
    @SuppressWarnings({"unchecked", "rawtypes"})
    @Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
    public List<Object> getModemOperationLog(Map<String, Object> condition){
        String sModemId          = StringUtil.nullToBlank(condition.get("sModemId"));
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
               .append("     AND TARGET_NAME  = :sModemId             \n")
               .append("  ORDER BY YYYYMMDDHHMMSS DESC            \n");
        
        SQLQuery query = getSession().createSQLQuery(sbQuery.toString());
        query.setString("sStartDate",   sStartDate);
        query.setString("sEndDate",     sEndDate);
        query.setString("sModemId",     sModemId);
        
        // Paging
        int rowPerPage = CommonConstants.Paging.ROWPERPAGE.getPageNum();
        int firstIdx  = Integer.parseInt(curPage) * rowPerPage;
        
        query.setFirstResult(firstIdx);
        query.setMaxResults(rowPerPage);
        
        List dataList = null;
        dataList = query.list();
        
        // 전체 건수
        StringBuffer countQuery = new StringBuffer();
        
        countQuery.append(" SELECT COUNT(ID)                   \n")
                  .append("   FROM OPERATION_LOG ol            \n")            
                  .append("  WHERE YYYYMMDD    >= :sStartDate  \n")
                  .append("    AND YYYYMMDD    <= :sEndDate    \n")
                  .append("    AND TARGET_NAME  = :sModemId    \n");
           
        SQLQuery countQueryObj = getSession().createSQLQuery(countQuery.toString());
        countQueryObj.setString("sStartDate",   sStartDate);
        countQueryObj.setString("sEndDate",     sEndDate);
        countQueryObj.setString("sModemId",     sModemId);  
        
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

    @Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
    public List<Object> getModemSerialList(Map<String, Object> condition){
        
        List<Object> result      = new ArrayList<Object>();
        
        String modemSerial       = StringUtil.nullToBlank(condition.get("modemSerial"));
        String supplierId        = StringUtil.nullToBlank(condition.get("supplierId"));
        
        StringBuffer sbQuery      = new StringBuffer();
        
        sbQuery = new StringBuffer();
        sbQuery.append("  SELECT DEVICE_SERIAL              \n") 
               .append("       , MODEM                      \n")
               .append("    FROM MODEM                      \n")
               .append("   WHERE SUPPLIER_ID = :supplierId  \n")
               .append("     AND DEVICE_SERIAL LIKE '"+ modemSerial + "%' \n")
               .append("   ORDER BY 1                       \n");
        
        SQLQuery query = getSession().createSQLQuery(sbQuery.toString());
        query.setString("supplierId",   supplierId);
    
        result.add(query.list());
        
        return result;
    }

    @SuppressWarnings("unchecked")
    @Transactional(readOnly=true, propagation=Propagation.REQUIRED)
    public List<Modem> getModemWithGpio(HashMap<String, Object> condition){
        Criteria criteria = getSession().createCriteria(Modem.class);     

        Set<String> set = condition.keySet();
        Object []hmKeys = set.toArray();
        for (int i=0; i<hmKeys.length; i++) {
            String key = (String)hmKeys[i];
            criteria.add(Restrictions.eq(key, condition.get(key)));
        }

        // 좌표 정보가 없으면 값을 반환하지 않는다.
        /*
        criteria.add(Restrictions.isNotNull("gpioX"));  
        criteria.add(Restrictions.isNotNull("gpioY"));  
        criteria.add(Restrictions.isNotNull("gpioZ"));
        */

        List<Modem> modems = (List<Modem>) criteria.list();
        
        return modems;
    }

    @SuppressWarnings("unchecked")
    @Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
    public List<Object> getBatteryLog(Map<String, Object> condition){

        List<Integer> listLocation = (List<Integer>)condition.get("meterLocation"); 
        int supplierId          = (Integer)condition.get("supplierId");
        String modemId          = StringUtil.nullToBlank((String)condition.get("modemId"));
        String modemType        = StringUtil.nullToBlank((String)condition.get("modemType"));
        String powerType        = StringUtil.nullToBlank((String)condition.get("powerType"));
//      String meterLocation    = StringUtil.nullToBlank((String)condition.get("meterLocation"));
        int batteryStatus       = (Integer)condition.get("batteryStatus");
        String batteryVoltSign  = StringUtil.nullToBlank((String)condition.get("batteryVoltSign"));
        String strBatteryVolt   = StringUtil.nullToBlank((String)condition.get("batteryVolt"));
        int batteryVolt         = "".equals(strBatteryVolt) ? 0 : Integer.parseInt(strBatteryVolt);
        String operatingDaySign = StringUtil.nullToBlank((String)condition.get("operatingDaySign"));
        String strOperatingDay  = StringUtil.nullToBlank((String)condition.get("operatingDay"));
        int operatingDay        = "".equals(strOperatingDay) ? 0 : Integer.parseInt(strOperatingDay);

        StringBuffer sb = new StringBuffer();
        sb.append("\n SELECT md.BATTERY_STATUS as batteryStatus "); 
        sb.append("\n       ,COUNT(md.ID) as statusCount ");
        sb.append("\n   FROM MODEM md LEFT OUTER JOIN METER mt ON md.ID = mt.MODEM_ID ")
                           .append(" LEFT OUTER JOIN LOCATION l ON l.ID = mt.LOCATION_ID ");
        sb.append("\n  WHERE md.MODEM = :modemType ");
        sb.append("\n    AND md.SUPPLIER_ID = :supplierId ");

        if(!"".equals(modemId)) {           
            sb.append("   AND md.DEVICE_SERIAL = :modemId ");
        }
        if(!"".equals(powerType) && !"".equals(powerType)) {
            if(!"Unknown".equals(powerType)) {
                sb.append("   AND md.POWER_TYPE = :powerType ");
            }else{
                sb.append("   AND md.POWER_TYPE IS NULL ");
            }
        }
        if(condition.get("meterLocation") != null) {            
            sb.append("   AND l.ID IN (:meterLocation) ");
        }
        if(batteryStatus > 0) {         
            sb.append("   AND md.BATTERY_STATUS = :batteryStatus ");
        }   
        if(batteryVolt != 0) {
            sb.append("   AND md.BATTERY_VOLT ").append(batteryVoltSign).append(" :batteryVolt ");
        }
        if(operatingDay != 0) {     
            sb.append("   AND md.OPERATING_DAY ").append(operatingDaySign).append(" :operatingDay ");
        }
        sb.append("\n GROUP BY md.BATTERY_STATUS ");
        
        SQLQuery query = getSession().createSQLQuery(sb.toString());
        query.setString("modemType", modemType);
        query.setInteger("supplierId", supplierId);
        
        if(!"".equals(modemId)) {           
            query.setString("modemId", modemId);
        }
        if(!"".equals(powerType) && !"All".equals(powerType) && !"Unknown".equals(powerType)) {             
            query.setString("powerType", powerType);
        }
        if(condition.get("meterLocation") != null) {                
            query.setParameterList("meterLocation", listLocation);
        }
        if(batteryStatus > 0) {             
            query.setInteger("batteryStatus", batteryStatus);
        }   
        if(batteryVolt != 0) {              
            query.setInteger("batteryVolt", batteryVolt);
        }
        if(operatingDay != 0) {             
            query.setInteger("operatingDay", operatingDay);
        }
        
        List<Object> returnList = new ArrayList<Object>();
        Map<String, Object> temp = null;

        List<Object> result = query.list();
        for(Object obj:result){
            int i=0;
            Object[] objs = (Object[])obj;
            temp = new HashMap<String, Object>();
            temp.put("batteryStatus", objs[i++]);
            temp.put("statusCount", objs[i++]);
            
            returnList.add(temp);
        }
        return returnList;
    }

    @SuppressWarnings("unchecked")
    @Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
    public List<Object> getBatteryLogByLocation(Map<String, Object> condition){

        List<Integer> listLocation = (List<Integer>)condition.get("meterLocation");
        Integer locationId      = (Integer)condition.get("locationId");
        int supplierId          = (Integer)condition.get("supplierId");
        String modemId          = StringUtil.nullToBlank((String)condition.get("modemId"));
        String modemType        = StringUtil.nullToBlank((String)condition.get("modemType"));
        String powerType        = StringUtil.nullToBlank((String)condition.get("powerType"));
//      String meterLocation    = StringUtil.nullToBlank((String)condition.get("meterLocation"));
        int batteryStatus       = (Integer)condition.get("batteryStatus");
        String batteryVoltSign  = StringUtil.nullToBlank((String)condition.get("batteryVoltSign"));
        String strBatteryVolt   = StringUtil.nullToBlank((String)condition.get("batteryVolt"));
        int batteryVolt         = "".equals(strBatteryVolt) ? 0 : Integer.parseInt(strBatteryVolt);
        String operatingDaySign = StringUtil.nullToBlank((String)condition.get("operatingDaySign"));
        String strOperatingDay  = StringUtil.nullToBlank((String)condition.get("operatingDay"));
        int operatingDay        = "".equals(strOperatingDay) ? 0 : Integer.parseInt(strOperatingDay);
        
        StringBuffer sb = new StringBuffer();
        sb.append("\n SELECT l.ID,   ") 
          .append("\n        l.NAME, ")  
          .append("\n        m.ID,   ") 
          .append("\n        m.BATTERY_STATUS ")
          .append("\n   FROM MODEM m LEFT OUTER JOIN METER t ON m.ID = t.MODEM_ID ")
                           .append(" LEFT OUTER JOIN LOCATION l ON l.ID = t.LOCATION_ID ")
          .append("\n  WHERE m.MODEM = :modemType ")
          .append("\n    AND m.SUPPLIER_ID = :supplierId ");

        if(locationId != null){
            sb.append("\n    AND t.LOCATION_ID = :locationId ");
        }else{
            sb.append("\n    AND t.LOCATION_ID IS NULL ");
        }
        if(!"".equals(modemId)) {           
            sb.append("   AND m.DEVICE_SERIAL = :modemId ");
        }
        if(!"".equals(powerType)) {
            if(!"Unknown".equals(powerType)) {
                sb.append("   AND m.POWER_TYPE = :powerType ");
            }else{
                sb.append("   AND m.POWER_TYPE IS NULL ");
            }
        }
        if(listLocation != null) {          
            sb.append("   AND l.ID IN (:meterLocation) ");
        }
        if(batteryStatus > 0) {         
            sb.append("   AND m.BATTERY_STATUS = :batteryStatus ");
        }   
        if(batteryVolt != 0) {
            sb.append("   AND m.BATTERY_VOLT ").append(batteryVoltSign).append(" :batteryVolt ");
        }
        if(operatingDay != 0) {     
            sb.append("   AND m.OPERATING_DAY ").append(operatingDaySign).append(" :operatingDay ");
        }
        
        SQLQuery query = getSession().createSQLQuery(sb.toString());
        query.setString("modemType", modemType);
        query.setInteger("supplierId", supplierId);
        if(locationId != null){
            query.setInteger("locationId", locationId);
        }
        if(!"".equals(modemId)) {           
            query.setString("modemId", modemId);
        }
        if(!"".equals(powerType) && !"Unknown".equals(powerType)) {             
            query.setString("powerType", powerType);
        }
        if(listLocation != null) {              
            query.setParameterList("meterLocation", listLocation);
        }
        if(batteryStatus > 0) {             
            query.setInteger("batteryStatus", batteryStatus);
        }   
        if(batteryVolt != 0) {              
            query.setInteger("batteryVolt", batteryVolt);
        }
        if(operatingDay != 0) {             
            query.setInteger("operatingDay", operatingDay);
        }
        
        return query.list();
    }

    @Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
    public List<Object> getBatteryLogList(Map<String, Object> condition) {
        return getBatteryLogList(condition, false);
    }

    @SuppressWarnings("unchecked")
    @Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
    public List<Object> getBatteryLogList(Map<String, Object> condition, boolean isCount) {
        List<Object> result = new ArrayList<Object>();
        int supplierId = (Integer) condition.get("supplierId");
        List<String> modemTypes = (ArrayList<String>) condition.get("modemTypes");

        List<Integer> listLocation = (List<Integer>) condition.get("meterLocation");
        String modemId = StringUtil.nullToBlank((String) condition.get("modemId"));
        String powerType = StringUtil.nullToBlank((String) condition.get("powerType"));
        // String meterLocation = StringUtil.nullToBlank((String)condition.get("meterLocation"));
        int batteryStatus = (Integer) condition.get("batteryStatus");
        String batteryVoltSign = StringUtil.nullToBlank((String) condition.get("batteryVoltSign"));
        String strBatteryVolt = StringUtil.nullToBlank((String) condition.get("batteryVolt"));
        int batteryVolt = "".equals(strBatteryVolt) ? 0 : Integer.parseInt(strBatteryVolt);
        String operatingDaySign = StringUtil.nullToBlank((String) condition.get("operatingDaySign"));
        String strOperatingDay = StringUtil.nullToBlank((String) condition.get("operatingDay"));
        int operatingDay = "".equals(strOperatingDay) ? 0 : Integer.parseInt(strOperatingDay);

/*        Integer page = (Integer) condition.get("page");
        if (page == null) {
            page = 0;
        }
        Integer pageSize = null;
        if (condition.containsKey("pageSize")) {
            pageSize = (Integer) condition.get("pageSize");
        } else {
            pageSize = CommonConstants.Paging.ROWPERPAGE.getPageNum();
        }
*/
        String strPage = String.valueOf(condition.get("page"));
        String strPageSize = null;
        if (condition.containsKey("pageSize")) {
            strPageSize = String.valueOf(condition.get("pageSize"));
        }else{
            strPageSize = String.valueOf(CommonConstants.Paging.ROWPERPAGE.getPageNum());
        }
        
        int pageCommaIndex = strPage.indexOf(".");
        int pageSizeCommaIndex = strPageSize.indexOf(".");
        int page = -1;
        int pageSize = -1;
        
        if(pageCommaIndex > -1)
            page = Integer.parseInt(strPage.substring(0, pageCommaIndex));
        else
            page = Integer.parseInt(strPage);
        
        if(pageSizeCommaIndex > -1)
            pageSize = Integer.parseInt(strPageSize.substring(0, pageSizeCommaIndex));
        else
            pageSize = Integer.parseInt(strPageSize);
        int firstResult = page * pageSize;
        
        StringBuilder sb = new StringBuilder();
        if (isCount) {
            sb.append("\nSELECT COUNT(*) AS cnt FROM ( ");
        }

        sb.append("\nSELECT m.battery_status ");
        sb.append("\n      ,m.device_serial ");
        sb.append("\n      ,m.modem_type ");
        sb.append("\n      ,m.modem ");
        sb.append("\n      ,m.power_type ");
        sb.append("\n      ,l.name ");
        sb.append("\n      ,m.battery_volt ");
        sb.append("\n      ,m.operating_day ");
        sb.append("\n      ,m.last_link_time ");
        sb.append("\n      ,m.active_time ");
        sb.append("\n      ,m.reset_count ");
        sb.append("\nFROM modem m ");
        sb.append("\n     LEFT OUTER JOIN meter t ");
        sb.append("\n     ON m.id = t.modem_id ");
        sb.append("\n     LEFT OUTER JOIN location l ");
        sb.append("\n     ON l.id = t.location_id ");
        sb.append("\nWHERE m.modem IN (:modemTypes) ");
        sb.append("\nAND   m.supplier_id = :supplierId ");

        if (!"".equals(modemId)) {
            sb.append("\nAND   m.device_serial = :modemId ");
        }
        if (!"".equals(powerType)) {
            if (!"Unknown".equals(powerType)) {
                sb.append("\nAND   m.power_type = :powerType ");
            } else {
                sb.append("\nAND   m.power_type IS NULL ");
            }
        }
        if (listLocation != null) {
            sb.append("\nAND   l.id IN (:meterLocation) ");
        }
        if (batteryStatus > 0) {
            if (batteryStatus == 4) {
                sb.append("\nAND   m.battery_status IS NULL ");
            } else {
                sb.append("\nAND   m.battery_status = :batteryStatus ");
            }
        }
        if (batteryVolt != 0) {
            sb.append("\nAND   m.battery_volt ").append(batteryVoltSign).append(" :batteryVolt ");
        }
        if (operatingDay != 0) {
            sb.append("\nAND   m.operating_day ").append(operatingDaySign).append(" :operatingDay ");
        }

        if (isCount) {
            sb.append("\n) y ");
        } else {
            sb.append("\nORDER BY m.last_link_time DESC ");
        }

        SQLQuery query = getSession().createSQLQuery(sb.toString());
        query.setParameterList("modemTypes", modemTypes);
        query.setInteger("supplierId", supplierId);

        if (!"".equals(modemId)) {
            query.setString("modemId", modemId);
        }
        if (!"".equals(powerType) && !"Unknown".equals(powerType)) {
            query.setString("powerType", powerType);
        }

        if (listLocation != null) {
            query.setParameterList("meterLocation", listLocation);
        }
        if (batteryStatus > 0 && batteryStatus != 4) {
            String strBarretyStatus = "";
            if (batteryStatus == 1) {
                strBarretyStatus = "Normal";
            } else if (batteryStatus == 2) {
                strBarretyStatus = "Abnormal";
            } else if (batteryStatus == 3) {
                strBarretyStatus = "Replacement";
            }
            query.setString("batteryStatus", strBarretyStatus);
        }
        if (batteryVolt != 0) {
            query.setInteger("batteryVolt", batteryVolt);
        }
        if (operatingDay != 0) {
            query.setInteger("operatingDay", operatingDay);
        }

        if (isCount) {
            Number count = (Number) query.uniqueResult();
            result.add(count.intValue());
        } else {
            query.setFirstResult(page * pageSize);
            query.setMaxResults(pageSize);
            result = query.list();
        }

        return result;
    }

    @Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
    public List<Object> getBatteryLogDetailList(Map<String, Object> condition) {
        return getBatteryLogDetailList(condition, false);
    }

    @SuppressWarnings("unchecked")
    @Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
    public List<Object> getBatteryLogDetailList(Map<String, Object> condition, boolean isCount) {
        List<Object> result = new ArrayList<Object>();
        int supplierId   = (Integer)condition.get("supplierId");
        String modemId = (String)condition.get("modemId");
        String modemType = (String)condition.get("modemType");
        String fromDate  = (String)condition.get("fromDate");  
        String toDate    = (String)condition.get("toDate"); 
        
        int page = (Integer)condition.get("page");
        int pageSize = 0;
        if(condition.containsKey("pageSize")){
            pageSize = (Integer)condition.get("pageSize");
        } else {
            pageSize = CommonConstants.Paging.ROWPERPAGE.getPageNum();
        }

        StringBuilder sb = new StringBuilder();
        if (isCount) {
            sb.append("\nSELECT COUNT(*) AS cnt ");
        } else {
            sb.append("\nSELECT m.id.yyyymmdd AS date, ");
            sb.append("\n       m.id.hhmmss AS time, ");
            sb.append("\n       m.batteryVolt AS batteryVolt, ");
            sb.append("\n       m.voltageCurrent AS voltCurrent, ");
            sb.append("\n       m.voltageOffset AS voltOffset ");
        }

        sb.append("\nFROM ModemPowerLog m, Modem d ");
        sb.append("\nWHERE m.id.deviceId = :modemId ");
        sb.append("\nAND   m.id.deviceType = :modemType ");
        sb.append("\nAND   m.id.deviceId = d.deviceSerial ");
        sb.append("\nAND   m.id.yyyymmdd >= :fromDate ");
        sb.append("\nAND   m.id.yyyymmdd <= :toDate ");
        sb.append("\nAND   d.supplier.id = :supplierId ");

        Query query = getSession().createQuery(sb.toString())
                                  .setString("modemId", modemId)
                                  .setString("modemType", modemType)
                                  .setString("fromDate", fromDate)
                                  .setString("toDate", toDate)
                                  .setInteger("supplierId", supplierId);

        if (isCount) {
            Number count = (Number)query.uniqueResult();
            result.add(count.intValue());
        } else {
            query.setFirstResult(page * pageSize);      
            query.setMaxResults(pageSize);
            result = query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
        }

//      return query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
        return result;
    }

    @SuppressWarnings("unchecked")
    @Transactional(readOnly=true, propagation=Propagation.REQUIRED)
    public List<Modem> getModemWithoutGpio(HashMap<String, Object> condition){
        Criteria criteria = getSession().createCriteria(Modem.class);     

        Set<String> set = condition.keySet();
        Object []hmKeys = set.toArray();
        for (int i=0; i<hmKeys.length; i++) {
            String key = (String)hmKeys[i];
            if (key.equals("deviceSerial")) {
                criteria.add(Restrictions.or(Restrictions.eq(key, condition.get(key)), Restrictions.like("address", "%"+condition.get(key)+"%")));
            } else {
                criteria.add(Restrictions.eq(key, condition.get(key)));
            }
        }

        List<Modem> modems = (List<Modem>) criteria.list();
        
        return modems;
    }

    /**
     * method name : getModemMapDataWithoutGpio<b/>
     * method Desc : Modem Management 맥스가젯의 위치정보 탭에서 맵정보를 조회한다.
     *
     * @param condition
     * @return
     */
    @SuppressWarnings("unchecked")
    @Transactional(readOnly=true, propagation=Propagation.REQUIRED)
    public List<Modem> getModemMapDataWithoutGpio(HashMap<String, Object> condition) {
        Criteria criteria = getSession().createCriteria(Modem.class);

        Set<String> set = condition.keySet();
        Object []hmKeys = set.toArray();
        for (int i = 0 ; i < hmKeys.length ; i++) {
            String key = (String)hmKeys[i];
            criteria.add(Restrictions.eq(key, condition.get(key)));
        }

        List<Modem> modems = (List<Modem>) criteria.list();

        return modems;
    }

    @SuppressWarnings("unchecked")
    @Transactional(readOnly=true, propagation=Propagation.REQUIRED)
    public List<Modem> getModemHavingMCU(Integer id){
        Criteria criteria = getSession().createCriteria(Modem.class);
        HashMap<String, Object> condition = new HashMap<String, Object>();
        condition.put("mcu.id", id);

        Set<String> set = condition.keySet();
        Object []hmKeys = set.toArray();
        for (int i=0; i<hmKeys.length; i++) {
            String key = (String)hmKeys[i];
            if (key.equals("deviceSerial")) {
                criteria.add(Restrictions.or(Restrictions.eq(key, condition.get(key)), Restrictions.like("address", "%"+condition.get(key)+"%")));
            } else {
                criteria.add(Restrictions.eq(key, condition.get(key)));
            }
        }

        // 좌표 정보가 없으면 값을 반환하지 않는다.
        criteria.add(Restrictions.isNotNull("gpioX"));  
        criteria.add(Restrictions.isNotNull("gpioY"));  
        criteria.add(Restrictions.isNotNull("gpioZ"));

        List<Modem> modems = (List<Modem>) criteria.list();
        
        return modems;
    }

    /**
     * 그룹 관리 중 멤버 리스트 조회
     * 
     * @param condition
     * @return
     */
    @SuppressWarnings("unchecked")
    @Deprecated
    @Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
    public List<Object> getGroupMember(Map<String, Object> condition){
        String member = StringUtil.nullToBlank(condition.get("member"));
        String searchMemberType = StringUtil.nullToBlank(condition.get("searchMemberType"));
        
        StringBuffer sb = new StringBuffer();
        sb.append("SELECT t.id, t.device_serial ")
          .append("FROM MODEM t ")
          .append(" LEFT JOIN GROUP_MEMBER g ON t.device_serial = g.member ");

            if(GroupType.Contract.name().equals(searchMemberType)){             
                sb.append(" LEFT OUTER JOIN METER meter ON t.ID = meter.MODEM_ID  ");
                sb.append(" LEFT OUTER JOIN CONTRACT contract ON contract.METER_ID = meter.ID   ");
                sb.append("WHERE contract.CONTRACT_NUMBER like '%").append((String)condition.get("member")).append("%'");
            }
            else if(GroupType.Location.name().equals(searchMemberType)){
                sb.append("INNER JOIN LOCATION location  ");
                sb.append("     ON (t.LOCATION_ID = LOCATION.ID)  \n");
                sb.append("WHERE location.NAME like '%").append((String)condition.get("member")).append("%'");
            }
            else if(GroupType.DCU.name().equals(searchMemberType)){
                sb.append("INNER JOIN MCU mcu  ");
                sb.append("     ON (t.MCU_ID = mcu.ID)  \n");
                sb.append("WHERE mcu.SYS_ID like '%").append((String)condition.get("member")).append("%'");
            }
            else if(GroupType.Meter.name().equals(searchMemberType)){               
                sb.append("   INNER JOIN METER meter    \n");
                sb.append("     ON (t.METER_ID = meter.ID)  \n");
                sb.append("WHERE meter.MDS_ID like '%").append((String)condition.get("member")).append("%'");
            }
            else if(GroupType.Modem.name().equals(searchMemberType)){
                sb.append("WHERE t.device_serial like '%").append((String)condition.get("member")).append("%'");
            }else {
                sb.append(" WHERE 1=1 ");
            }
              sb.append("AND t.supplier_id = :supplierId ");
        sb.append("AND t.device_serial NOT IN ( ");
            sb.append("SELECT t.device_serial ");
            sb.append("FROM MODEM t RIGHT JOIN GROUP_MEMBER g ON t.device_serial = g.member ");
            sb.append("WHERE t.supplier_id = :supplierId ");
        sb.append(") ");
        sb.append(" ORDER BY t.device_serial ASC");

        SQLQuery query = getSession().createSQLQuery(sb.toString());
        return query.setInteger("supplierId", Integer.parseInt((String)condition.get("supplierId")))
                    .list();
    }

    /**
     * method name : getMemberSelectData<b/>
     * method Desc : Group Management 가젯에서 Member 로 등록 가능한 Modem 리스트를 조회한다.
     *
     * @param conditionMap
     * @return
     */
    @SuppressWarnings("unchecked")
    @Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
    public List<Object> getMemberSelectData(Map<String, Object> conditionMap) {
    	
    	List<Object> gridData 	= new ArrayList<Object>();		
		List<Object> result		= new ArrayList<Object>();
		
        Integer supplierId = (Integer)conditionMap.get("supplierId");
        Integer groupId = (Integer)conditionMap.get("groupId");
        String memberName = StringUtil.nullToBlank(conditionMap.get("memberName"));
        String sModemType           = StringUtil.nullToBlank(conditionMap.get("sModemType"));
        String sModemId             = StringUtil.nullToBlank(conditionMap.get("sModemId"));
        String sState               = StringUtil.nullToBlank(conditionMap.get("sState"));
        String sInstallState        = StringUtil.nullToBlank(conditionMap.get("sInstallState"));
        String sMcuType             = StringUtil.nullToBlank(conditionMap.get("sMcuType"));
        String sMcuName             = StringUtil.nullToBlank(conditionMap.get("sMcuName"));
        String sModemFwVer          = StringUtil.nullToBlank(conditionMap.get("sModemFwVer"));
        String sModemSwRev          = StringUtil.nullToBlank(conditionMap.get("sModemSwRev"));
        String sModemHwVer          = StringUtil.nullToBlank(conditionMap.get("sModemHwVer"));
        String sModemStatus         = StringUtil.nullToBlank(conditionMap.get("sModemStatus"));
        String sInstallStartDate    = StringUtil.nullToBlank(conditionMap.get("sInstallStartDate"));
        String sInstallEndDate      = StringUtil.nullToBlank(conditionMap.get("sInstallEndDate"));
        String sLastcommStartDate   = StringUtil.nullToBlank(conditionMap.get("sLastcommStartDate"));
        String sLastcommEndDate     = StringUtil.nullToBlank(conditionMap.get("sLastcommEndDate"));
        String sLocationId          = StringUtil.nullToBlank(conditionMap.get("sLocationId"));
        String curPage              = StringUtil.nullToBlank(conditionMap.get("curPage"));
        String sOrder               = StringUtil.nullToBlank(conditionMap.get("sOrder"));
        String sCommState           = StringUtil.nullToBlank(conditionMap.get("sCommState"));
        //String gridType             = StringUtil.nullToBlank(conditionMap.get("gridType"));
        String deleteCodeId         = StringUtil.nullToBlank(conditionMap.get("deleteCodeId"));
        String breakDownCodeId      = StringUtil.nullToBlank(conditionMap.get("breakDownCodeId"));
        String normalCodeId         = StringUtil.nullToBlank(conditionMap.get("normalCodeId"));
        String repairCodeId         = StringUtil.nullToBlank(conditionMap.get("repairCodeId"));
        String securityErrorCodeId  = StringUtil.nullToBlank(conditionMap.get("securityErrorCodeId"));
        String commErrorCodeId      = StringUtil.nullToBlank(conditionMap.get("commErrorCodeId"));
        /*String modelId      		= StringUtil.nullToBlank(conditionMap.get("modelId"));
        String fwGadget 			= StringUtil.nullToBlank(conditionMap.get("fwGadget"));
        String purchaseOrder 		= StringUtil.nullToBlank(conditionMap.get("purchaseOrder"));*/
        String protocolType 		= StringUtil.nullToBlank(conditionMap.get("protocolType"));
        int page          = (Integer) conditionMap.get("page");
        int limit          = (Integer) conditionMap.get("limit");

        StringBuilder sb = new StringBuilder();
        StringBuilder sbQuery = new StringBuilder();

        sbQuery.append("\nSELECT mo.id AS value, ");
        sbQuery.append("\n       mo.deviceSerial AS text, ");
        sbQuery.append("\n       'Modem' AS type, ");
        sbQuery.append("\n       loc.name AS name ");
        
        sb.append("\nFROM Modem mo left outer join mo.modemStatus modemStatus");
        sb.append("\nleft outer join mo.location loc ");
        sb.append("\nleft outer join mo.mcu mcu ");
        //sb.append("\nleft outer join mo.devicemodel model ");
        //sb.append("\nleft outer join model.devicevendor vendor ");
        sb.append("\nleft outer join mo.modemStatus co ");
        sb.append("\nWHERE mo.supplier.id = :supplierId ");
        sb.append("\nAND (mo.modemStatus is null OR modemStatus.code <> :deleteCode) ");
        if (!memberName.isEmpty()) {
            sb.append("\nAND   mo.deviceSerial LIKE :memberName ");
        }
        
        //검색조건
        if(!"".equals(deleteCodeId)) {  
            if(sModemStatus.isEmpty()){  // Status = all
                sb.append("    AND (mo.modemStatus <>" +deleteCodeId + "OR mo.modemStatus IS NULL)" );
            }else if(!sModemStatus.isEmpty() && breakDownCodeId.equals(sModemStatus)){     // Status = BreakDown                                                     
                sb.append("    AND mo.modemStatus = "+ Integer.parseInt(sModemStatus));
            }else if(!sModemStatus.isEmpty() && deleteCodeId.equals(sModemStatus)) {   // Status = Delete
                sb.append("    AND mo.modemStatus = "+ Integer.parseInt(sModemStatus));
            }else if(!sModemStatus.isEmpty() && normalCodeId.equals(sModemStatus)) {   // Status = Normal
                sb.append("    AND mo.modemStatus = "+ Integer.parseInt(sModemStatus));
            }else if(!sModemStatus.isEmpty() && repairCodeId.equals(sModemStatus)){        // Status = Repair                                               
                sb.append("    AND mo.modemStatus = "+ Integer.parseInt(sModemStatus));
            }else if(!sModemStatus.isEmpty() && securityErrorCodeId.equals(sModemStatus)){        // Status = SecurityError                                               
                sb.append("    AND mo.modemStatus = "+ Integer.parseInt(sModemStatus));
            }else if(!sModemStatus.isEmpty() && commErrorCodeId.equals(sModemStatus)){        // Status = CommError                                               
                sb.append("    AND mo.modemStatus = "+ Integer.parseInt(sModemStatus));}
         }else {
            log.info("deleteCodeId is not exist");
        }   
        
        if(!sModemType.equals(""))
        	sb.append("\nAND   mo.modemType = '"+ sModemType +"' ");
        if(!sModemId.equals(""))
        	sb.append("\nAND   mo.deviceSerial like '"+ sModemId + "%' ");
        if(sInstallState.equals("Y")) 
        	sb.append("\nAND   mo.installDate IS NOT NULL ");
        else if(sInstallState.equals("N"))
        	sb.append("\nAND   mo.installDate IS NULL ");
        
        if(!sMcuType.equals(""))
        	sb.append("\nAND   mcu.mcuType = "+ sMcuType + " ");
        
        if(!sMcuName.equals("")){
            if(sMcuName.equals("-")){
            	sb.append("\nAND   mcu.sysID IS NULL");               
            }else{
            	sb.append("\nAND   mcu.sysID = '"+ sMcuName +"' ");
            }
        }
        if(!sLocationId.equals(""))
        	sb.append("\nAND   mo.location IN (" + sLocationId + ") ");
        
        if(!sModemFwVer.equals(""))
        	sb.append("\nAND   mo.fwVer = '"+ sModemFwVer + "' ");
    
        if(!sModemSwRev.equals(""))
        	sb.append("\nAND   mo.FW_REVISION = '"+ sModemSwRev + "' ");       
    
        if(!sModemHwVer.equals(""))
        	sb.append("\nAND   mo.hwVer = '"+ sModemHwVer + "' ");
        
        /*if(!purchaseOrder.equals(""))
        	sb.append("\nAND   mo.PO = '"+ purchaseOrder + "' ");*/
        
        if(!protocolType.equals(""))
        	sb.append("\nAND   mo.protocolType = '"+ protocolType + "' ");
        
        /*if(!modelId.equals(""))
            sb.append("\nAND   model.id = '"+ modelId + "' ");*/
        
        if(!sInstallStartDate.equals(""))
        	sb.append("\nAND   mo.installDate >= '"+ sInstallStartDate +"000000' ");
    
        if(!sInstallEndDate.equals(""))
        	sb.append("\nAND   mo.installDate <= '"+ sInstallEndDate +"235959' ");
        
        if(!sLastcommStartDate.equals(""))
        	sb.append("\nAND   CASE WHEN mo.lastLinkTime IS NULL THEN '' ELSE mo.lastLinkTime END >= '"+ sLastcommStartDate +"000000' ");
    
        if(!sLastcommEndDate.equals(""))
        	sb.append("\nAND   CASE WHEN mo.lastLinkTime IS NULL THEN '' ELSE mo.lastLinkTime END <= '"+ sLastcommEndDate +"235900' ");
    
        sb.append("\nAND   NOT EXISTS ( ");
        sb.append("\n    SELECT 'X' ");
        sb.append("\n    FROM GroupMember gm ");
        sb.append("\n    WHERE gm.member = mo.deviceSerial ");
        sb.append("\n    AND   gm.aimirGroup.id = :groupId ");
        sb.append("\n) ");
        sb.append("\nORDER BY mo.deviceSerial ");

     // 전체 건수
        StringBuffer countQuery = new StringBuffer();
        countQuery.append("\n SELECT COUNT( * ) ");
        countQuery.append(sb);
        
        Query countQueryObj = getSession().createQuery(countQuery.toString());
        countQueryObj.setInteger("supplierId", supplierId);
        countQueryObj.setInteger("groupId", groupId);
        countQueryObj.setString("deleteCode", ModemSleepMode.Delete.getCode());
        if (!memberName.isEmpty()) {
        	countQueryObj.setString("memberName", "%" + memberName + "%");
        }
        
        Number totalCount = (Number)countQueryObj.uniqueResult();
        result.add(totalCount.toString());

        sbQuery.append(sb);
        
        Query query = getSession().createQuery(sbQuery.toString());
        query.setInteger("supplierId", supplierId);
        query.setString("deleteCode", ModemSleepMode.Delete.getCode());
        query.setInteger("groupId", groupId);
        if (!memberName.isEmpty()) {
            query.setString("memberName", "%" + memberName + "%");
        }

        query.setFirstResult((page-1) * limit);      
        query.setMaxResults(limit);
        
        List dataList = null;
		dataList = query.list();
		
		// 실제 데이터
		int dataListLen = 0;
		if(dataList != null)
			dataListLen= dataList.size();
				
		for (int i = 0; i < dataListLen; i++) {
			HashMap chartDataMap = new HashMap();
			Object[] resultData = (Object[]) dataList.get(i);
			
			chartDataMap.put("value",           resultData[0] );
			chartDataMap.put("text",      resultData[1]);                 
			chartDataMap.put("type",    resultData[2]);
			chartDataMap.put("name",     resultData[3]);
			gridData.add(chartDataMap);
		}
		
		result.add(gridData);
		
		return result;
    }
    
    /**
     * method name : getHomeGroupMemberSelectData<b/>
     * method Desc : HomeGroup Management 가젯에서 Member 로 등록 가능한 Modem 리스트를 조회한다.
     *
     * @param conditionMap
     * @return
     */
    @SuppressWarnings("unchecked")
    @Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
    public List<Object> getHomeGroupMemberSelectData(Map<String, Object> conditionMap) {
        Integer supplierId = (Integer)conditionMap.get("supplierId");
        String subType = StringUtil.nullToBlank(conditionMap.get("subType"));
        Integer mcuId = (Integer)conditionMap.get("mcuId");
        String memberName = StringUtil.nullToBlank(conditionMap.get("memberName"));

        StringBuilder sb = new StringBuilder();

        sb.append("\nSELECT mo.id AS value, ");
        sb.append("\n       mo.deviceSerial AS text, ");
        sb.append("\n       'Modem' AS type ");
        sb.append("\nFROM Modem mo ");
        sb.append("\nWHERE mo.supplier.id = :supplierId ");
        if(ModemType.IHD.name().equals(subType)) {
            sb.append("\nAND mo.modemType = :modemType");
        }
        sb.append("\nAND mo.mcu.id = :mcuId ");
        sb.append("\nAND (mo.modemStatus is null OR mo.modemStatus.code <> :deleteCode) ");

        if (!memberName.isEmpty()) {
            sb.append("\nAND   mo.deviceSerial LIKE :memberName ");
        }

        sb.append("\nAND   NOT EXISTS ( ");
        sb.append("\n    SELECT 'X' ");
        sb.append("\n    FROM GroupMember gm ");
        sb.append("\n    WHERE gm.member = mo.deviceSerial ");
        sb.append("\n) ");
        sb.append("\nORDER BY mo.deviceSerial ");

        Query query = getSession().createQuery(sb.toString());
        query.setInteger("supplierId", supplierId);
        if(ModemType.IHD.name().equals(subType)) {
            query.setString("modemType", subType);
        }
        query.setInteger("mcuId", mcuId);
        query.setString("deleteCode", ModemSleepMode.Delete.getCode());
        if (!memberName.isEmpty()) {
            query.setString("memberName", "%" + memberName + "%");
        }

        return query.list();
    }

    // Mcu sys_id로 할당된 Modem 의 목록을 조회
    @SuppressWarnings("unchecked")
    @Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
    public List<Object> getModemListByMCUsysID(String sys_id){
        
        StringBuffer sbQuery    = new StringBuffer();
        
        sbQuery = new StringBuffer();
        sbQuery.append("  SELECT md.id                      \n") 
               .append("   FROM Modem md            \n")
               .append("   join mcu mu on(sys_id = :sys_id and mu.id =  md.mcu_id)  \n");
        
        SQLQuery query = getSession().createSQLQuery(sbQuery.toString());
        query.setString("sys_id", sys_id);
        
        return query.list();
    }
    
    // Mcu sys_id로 할당된 Modem의 Device Serial 목록을 조회
    @Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
    public List<Object> getDeviceSerialByMcu(String sys_id){
        StringBuffer sbQuery    = new StringBuffer();
        
        sbQuery = new StringBuffer();
        sbQuery.append("   SELECT md.device_serial  \n") 
               .append("   FROM Modem md            \n")
               .append("   join mcu mu on(sys_id = :sys_id and mu.id =  md.mcu_id)  \n");
        
        SQLQuery query = getSession().createSQLQuery(sbQuery.toString());
        query.setString("sys_id", sys_id);
        
        return query.list();
    }
    
    // device_serial에 할당된 Modem.id 의 목록을 조회
    @SuppressWarnings("unchecked")
    @Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
    public List<Object> getModemIdListByDevice_serial(String device_serial){
        
        StringBuffer sbQuery    = new StringBuffer();
        
        sbQuery = new StringBuffer();
        sbQuery.append("  SELECT md.id                      \n") 
               .append("   FROM Modem md            \n")
               .append("   WHERE md.device_serial =  :device_serial   \n");
        
        SQLQuery query = getSession().createSQLQuery(sbQuery.toString());
        query.setString("device_serial", device_serial);
        
        return query.list();
    }
    
    @SuppressWarnings("unchecked")
    @Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
    public List<Object> getGroupMember(String name, int supplierId){

        StringBuilder sb = new StringBuilder();
        sb.append("SELECT t.id, t.deviceSerial, t.mcu.sysID ")
          .append("FROM MODEM t, AimirGroup a, GroupMember g  ")
          .append("WHERE a.name = :name ")
          .append("AND a.id = g.aimirGroup.id ")
          .append("AND g.member = t.deviceSerial ")
          .append("AND a.supplier.id = :supplierId ");
        sb.append(") ");
        Query query = getSession().createQuery(sb.toString());
        query.setString("name", name);
        query.setInteger("supplierId", supplierId);
        
        return query.list();
    }

    /**
     * method name : getMcuConnectedDeviceList<b/>
     * method Desc :
     *
     * @param conditionMap
     * @param isCount
     * @return
     */
    @SuppressWarnings("unchecked")
    @Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
    public List<Map<String, Object>> getMcuConnectedDeviceList(Map<String, Object> conditionMap, boolean isCount) {
        List<Map<String, Object>> result = null;
        Integer mcuId = (Integer)conditionMap.get("mcuId");
        Integer page = (Integer)conditionMap.get("page");
        Integer limit = (Integer)conditionMap.get("limit");

        StringBuilder sb = new StringBuilder();

        if (isCount) {
            sb.append("\nSELECT COUNT(*) AS cnt ");
        } else {
            sb.append("\nSELECT md.deviceSerial AS deviceSerial, ");
            sb.append("\n       md.modemType AS modemType ");
        }
        sb.append("\nFROM Modem md ");
        sb.append("\nWHERE md.mcu.id = :mcuId ");

        if (!isCount) {
            sb.append("\nORDER BY md.deviceSerial ");
        }

        Query query = getSession().createQuery(sb.toString());
        query.setInteger("mcuId", mcuId);

        if (isCount) {
            Integer count = ((Number)query.uniqueResult()).intValue();
            Map<String, Object> map = new HashMap<String, Object>();
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
    
    public Integer getModemCount(Map<String, String> condition) {
        Integer supplierId = Integer.parseInt(condition.get("supplierId"));
        
        Criteria criteria = getSession().createCriteria(Modem.class);
        
        criteria.setProjection(Projections.rowCount());
        
        if (supplierId != null)
            criteria.add(Restrictions.eq("supplier.id", supplierId));
        
        return ((Number)criteria.uniqueResult()).intValue();
    }
    
    @SuppressWarnings("unchecked")
    @Transactional(readOnly=false, propagation=Propagation.REQUIRES_NEW)
    public void updateModemColumn(String modemType, String deviceSerial) {
        StringBuilder sb = new StringBuilder();
        
        if(modemType.equals("LTE")){
            sb.append("\nUPDATE MODEM ");   
            sb.append("\nSET MODEM_TYPE = :modemType , MODEM = :modemType , MD_PERIOD = 0");
            sb.append("\nWHERE DEVICE_SERIAL = :deviceSerial ");
        } else {
            sb.append("\nUPDATE MODEM ");   
            sb.append("\nSET MODEM_TYPE = :modemType , MODEM = :modemType");
            sb.append("\nWHERE DEVICE_SERIAL = :deviceSerial ");
        }
        
        Query query = getSession().createSQLQuery(sb.toString());
        query.setString("modemType", modemType);
        query.setString("deviceSerial", deviceSerial);
        
        query.executeUpdate();
    }
    
    // INSERT START SP-193
    @SuppressWarnings("unchecked")
    public List<Object[]> getModemByIp(String ip) {

        HashMap result           = new HashMap();
        StringBuffer sbQuery     = new StringBuffer();
        
        sbQuery.append("  SELECT m.MODEM_TYPE, m.DEVICE_SERIAL  ")
                .append("  FROM MODEM m                     ")
                .append("  WHERE m.IPV6_ADDRESS = :ipv6 or m.IP_ADDR = :ipv4    ");                           
    
        SQLQuery query = getSession().createSQLQuery(sbQuery.toString());
        query.setString("ipv4", ip);
        query.setString("ipv6", ip);
        
        /*
        List list = null;
        list = query.list();

        if (list.size() == 0) return null;
        
        Object[] resultData = (Object[]) list.get(0);
                    
        if(resultData[0] == null || "".equals(resultData[0]))
            result.put("modemType" , "");
        else
            result.put("modemType" , resultData[0].toString());
        
        if(resultData[1] == null || "".equals(resultData[1]))
            result.put("deviceSerial" , "");
        else
            result.put("deviceSerial" , resultData[1].toString());

        if(resultData[2] == null || "".equals(resultData[2]))
            result.put("ipv6Address" , "");
        else
            result.put("ipv6Address" , resultData[2].toString());
        
        return result;
        */
        return query.list();
    }
    
    @SuppressWarnings("unchecked")
    public String getModemIpv6ByDeviceSerial(String serial) {

        HashMap result           = new HashMap();
        StringBuffer sbQuery     = new StringBuffer();
        
        sbQuery.append("  SELECT MODEM, DEVICE_SERIAL, IPV6_ADDRESS       ")
                .append("  FROM MODEM                               ")
                .append("  WHERE DEVICE_SERIAL = :serial    ");                           
    
        SQLQuery query = getSession().createSQLQuery(sbQuery.toString());
        query.setString("serial", serial);
        
        List list = null;
        list = query.list();

        if (list.size() == 0) return null;
        
        Object[] resultData = (Object[]) list.get(0);
                    
    
        if(resultData[2] == null || "".equals(resultData[2]))
            return null;
        else
            return resultData[2].toString();
    }           
    // INSERT END SP-193
    
    @SuppressWarnings("unchecked")
    @Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
    public List<Map<String, Object>>  getModemWithMCU(Map<String, Object> condition) {

        String supplierId = StringUtil.nullToBlank(condition.get("supplierId"));
        String locationId = StringUtil.nullToBlank(condition.get("locationId"));
        String deviceSerial = StringUtil.nullToBlank(condition.get("deviceSerial"));
        String msa  = StringUtil.nullToBlank(condition.get("msa")); // SP-1050
        
        StringBuilder sbQuery = new StringBuilder();
        List<Map<String, Object>>  ret = new ArrayList<Map<String, Object>>() ;
        
        sbQuery.append("\n SELECT md.id AS ID, md.device_serial AS DEVICE_SERIAL,  ");
        sbQuery.append("\n     md.address AS ADDRESS, ");
        sbQuery.append("\n     md.gpiox as GPIOX ,md.gpioy AS GPIOY, md.gpioz  AS GPIOZ, ");
        sbQuery.append("\n     loc.name as LOCATION_NAME, ");
        sbQuery.append("\n     mcu.sys_id AS SYS_ID, ");
        sbQuery.append("\n     md.modem_type AS MODEM_TYPE, "); 
        sbQuery.append("\n     md.protocol_type AS MODEM_PROTOCOL, "); 
        sbQuery.append("\n     mt.msa AS MSA "); 
        sbQuery.append("\n     FROM modem md ");
        sbQuery.append("\n INNER JOIN meter mt ON mt.modem_id = md.id ");
        sbQuery.append("\n LEFT OUTER JOIN mcu mcu ON md.mcu_id = mcu.id ");
        sbQuery.append("\n LEFT OUTER JOIN location loc ON md.location_id = loc.id ");
        sbQuery.append("\n WHERE md.supplier_id = :supplierId ");
        sbQuery.append("\n      AND (mt.meter_status is null or mt.meter_status!=(select id from code where code='1.3.3.9'))");
        sbQuery.append("\n      AND (mcu.mcu_status is null or mcu.mcu_status != (select id from code where code='1.1.4.2'))");
 
        if (!"".equals(deviceSerial)) {
            sbQuery.append("\nAND   md.device_serial LIKE :deviceSerial ");
        }
        else {
            if (  !"".equals(locationId)){
        	    sbQuery.append("\nAND   md.location_id = :locationId ");
            }
            if ( !"".equals(msa)){
                sbQuery.append("\nAND   mt.msa = :msa ");
            }
            else {
                sbQuery.append("\nAND  ( mt.msa is null or mt.msa = '' ) ");
            }
        }
        SQLQuery query = getSession().createSQLQuery(sbQuery.toString());     
        if (!"".equals(deviceSerial)) {
        	query.setString("deviceSerial", "%" + deviceSerial + "%");
        }
        else {
        	if ( !"".equals(locationId)){
        		query.setInteger("locationId", Integer.parseInt(locationId));
        	}
        	if ( !"".equals(msa)){
        		query.setString("msa", msa);
        	}
        }  
        query.setInteger("supplierId", Integer.parseInt(supplierId));
        ret = query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
        return ret;
    }
    
    
    @Override
    public List<String> getFirmwareVersionList(Map<String, Object> condition) {
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
    			.append("  SELECT Distinct mo.FW_VER                                                                                                  \n")
    			.append("  FROM MODEM mo INNER JOIN LOCATION loc                                                                                                   \n")
    			.append("  ON mo.location_id = loc.id                                                                                                      \n")
    			.append("  LEFT OUTER JOIN MCU mu                                                                                                     \n")
    			.append("  ON mo.MCU_ID = mu.id                                                                                                      \n")
    			.append("  LEFT OUTER JOIN CODE co                                                                                                      \n")
    			.append(" ON mo.MODEM_STATUS = co.ID                                                                                                        \n");
    			if(!modelId.equals("") && modelId!=null)
    				sbQuery.append("  WHERE mo.DEVICEMODEL_ID = :modelId \n");
    			sbQuery.append("  and mo.FW_VER is not null \n");
    			sbQuery.append("  and mo.location_id is not null \n");
    			if(!deviceId.equals("") && deviceId!=null)
    				sbQuery.append("  and (0, mo.DEVICE_SERIAL) IN ("+deviceIds+") \n");
    			if(!fwVersion.equals("") && fwVersion!=null)
    				sbQuery.append("  and mo.FW_VER IN ("+fwVersions+") \n");
    			if(!locationId.equals("") && locationId!=null)
    				sbQuery.append("  and loc.name IN ("+location+") \n");
    			if(!dcuName.equals("") && dcuName!=null){
    				if(dcuName.equals("-"))
    					sbQuery.append(" and mu.SYS_ID is null \n");
    				else
    					sbQuery.append("  and mu.SYS_ID =:dcuName \n");
    			}
    			
    			if(!installStartDate.equals("") && installStartDate!=null)
    				sbQuery.append("  and mo.INSTALL_DATE >= '"+installStartDate +"000000'\n");
    			if(!installEndtDate.equals("") && installEndtDate!=null)
    				sbQuery.append("  and mo.INSTALL_DATE <= '"+installEndtDate +"125959'\n");
    			if(!lastCommStartDate.equals("") && lastCommStartDate!=null)
    				sbQuery.append("  and mo.LAST_LINK_TIME >= '"+lastCommStartDate +"000000'\n");
    			if(!lastCommEndDate.equals("") && lastCommEndDate!=null)
    				sbQuery.append("  and mo.LAST_LINK_TIME <= '"+lastCommEndDate +"125959'\n");
    			if(!hwVer.equals("") && hwVer!=null)
    				sbQuery.append("  and mo.HW_VER = '"+hwVer+"' \n");
    			
    			sbQuery.append(" and (co.NAME <> 'Delete' or co.NAME IS NULL) \n");
    			sbQuery.append(" ORDER BY mo.FW_VER asc \n"); // 추가
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
    			.append("  SELECT loc.name AS DSO, loc.id, NVL(mu.SYS_ID,'-') AS DCU                                                                                                  \n");
    			for(int i = 0 ; st.hasMoreTokens() ; i++){
    				version = st.nextToken();
    				sbQuery.append("  , count(case mo.FW_VER when '" + version + "' then 1 end) || '/' || loc.id || '/"+version+"'" + "|| '/' || mu.SYS_ID  AS VERSION"+i+"   \n");
    			}
    			sbQuery.append("  FROM MODEM mo INNER JOIN LOCATION loc                                                                                                   \n")
    			.append("  ON mo.location_id = loc.id                                                                                                      \n")
    			.append("  LEFT OUTER JOIN MCU mu                                                                                                     \n")
    			.append("  ON mo.MCU_ID = mu.id                                                                                                      \n")
    			.append("  LEFT OUTER JOIN CODE co                                                                                                      \n")
    			.append(" ON mo.MODEM_STATUS = co.ID                                                                                                        \n");
    			if(!modelId.equals("") && modelId!=null)
    				sbQuery.append("  WHERE mo.DEVICEMODEL_ID = :modelId \n");
    			if(!deviceId.equals("") && deviceId!=null)
    				sbQuery.append("  and (0, mo.DEVICE_SERIAL) IN ("+deviceIds+") \n");
    			if(!fwVersion.equals("") && fwVersion!=null)
    				sbQuery.append("  and mo.FW_VER IN ("+fwVersions+") \n");
    			sbQuery.append("  and mo.FW_VER IS NOT NULL \n");
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
    				sbQuery.append("  and mo.INSTALL_DATE >= '"+installStartDate +"000000'\n");
    			if(!installEndtDate.equals("") && installEndtDate!=null)
    				sbQuery.append("  and mo.INSTALL_DATE <= '"+installEndtDate +"235959'\n");
    			if(!lastCommStartDate.equals("") && lastCommStartDate!=null)
    				sbQuery.append("  and mo.LAST_LINK_TIME >= '"+lastCommStartDate +"000000'\n");
    			if(!lastCommEndDate.equals("") && lastCommEndDate!=null)
    				sbQuery.append("  and mo.LAST_LINK_TIME <= '"+lastCommEndDate +"235959'\n");
    			if(!hwVer.equals("") && hwVer!=null)
    				sbQuery.append("  and mo.HW_VER = '"+hwVer+"' \n");
    			
    			sbQuery.append(" and (co.NAME <> 'Delete' or co.NAME IS NULL) \n");
    			
    			
    			sbQuery.append("  GROUP BY loc.name, loc.id, mu.SYS_ID                                                                                                  \n");
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
    
    /* SP-957 Clone On,Off - Modem */
    @Override
    public List<String> getDeviceListModem(Map<String, Object> condition) {
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
        
        //sp-1004 Devices Id (Excel Search 위한 modem ID)
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
        		sbQuery.append("  , count(mo.device_serial) as MODEMCOUNT  \n");
        		sbQuery.append("   from modem mo\n");
        		sbQuery.append("   inner join location loc											\n");
        		sbQuery.append("  on mo.location_id = loc.id                                                                                                      \n");
        		sbQuery.append("  left outer join mcu mu                                                                                                     \n");
        		sbQuery.append("  on mo.mcu_id = mu.id                                                                                                      \n");
        		sbQuery.append("  left outer join code co                                                                                                      \n");
        		sbQuery.append("  on mo.modem_status = co.id                                                                                                        \n");
    			sbQuery.append("  where mo.fw_ver is not null \n");
    			if(!deviceId.equals("") && deviceId!=null)
    				sbQuery.append("  and (0, mo.DEVICE_SERIAL) in ("+deviceIds+") \n");
    			sbQuery.append("  and mo.fw_ver = '"+ fwVersion +"'");
    			sbQuery.append("  and mo.devicemodel_id = "+ modelId +" \n");
    			sbQuery.append("  and loc.name is not null \n");
    			if(!locationId.equals("") && locationId!=null)
    				sbQuery.append("  and loc.name in ("+location+") \n");
    			if(!dcuName.equals("") && dcuName!=null){
    				if(dcuName.equals("-"))
    					sbQuery.append(" and mu.sys_id is null \n");
    				else
    					sbQuery.append("  and mu.sys_id =:dcuName \n");
    			}
    			if(!modelId.equals("") && modelId!=null)
    				sbQuery.append("  and mo.devicemodel_id = :modelId \n");
    			sbQuery.append(" and (co.name <> 'Delete' or co.name is null) \n");
    			sbQuery.append("  group by loc.name, loc.id, mu.sys_id                                      \n");
    			sbQuery.append("  order by mu.sys_id asc nulls first                                                                                                   \n");
    	} else{ // fw 버전이 fwVersion이 아닌 것 중 부모가 fwVersion인 것 (체크했을때)
    		sbQuery.append("  select  loc.name AS DSO, loc.id  DSO_ID, NVL(mu.sys_id,'-') as DCU         \n");
    		sbQuery.append("  , count(mo.device_serial) as MODEMCOUNT  \n");
    		sbQuery.append("   from modem mo\n");
    		sbQuery.append("   inner join location loc											\n");
    		sbQuery.append("  on mo.location_id = loc.id                                                                                                      \n");
    		sbQuery.append("  left outer join mcu mu                                                                                                     \n");
    		sbQuery.append("  on mo.mcu_id = mu.id                                                                                                      \n");
    		sbQuery.append("  left outer join code co                                                                                                      \n");
    		sbQuery.append("  on mo.modem_status = co.id                                                                                                        \n");
			sbQuery.append(" where mo.id in ( \n");
			sbQuery.append("    select distinct(modem_id) from modem where devicemodel_id = (select id from devicemodel where name = '"+deviceModelName+"')  \n");
			if(!deviceId.equals("") && deviceId!=null)
				sbQuery.append("  		and (0, mo.device_serial) in ("+deviceIds+") \n");
			sbQuery.append("        and fw_ver is not null  \n");
			sbQuery.append("        and fw_ver != '"+ fwVersion +"'");
			sbQuery.append("        and modem_id is not null \n");
			sbQuery.append("        and (modem_status is null or modem_status not in (select id from code where name in ('BreakDown' , 'Delete', 'Repair'))) \n");
			sbQuery.append(" ) and mo.fw_ver is not null \n");
			sbQuery.append("  and mo.fw_ver = '"+ fwVersion +"'");
			sbQuery.append("  and loc.name is not null \n");
			if(!locationId.equals("") && locationId!=null)
				sbQuery.append("  and loc.name in ("+location+") \n");
			if(!dcuName.equals("") && dcuName!=null){
				if(dcuName.equals("-"))
					sbQuery.append(" and mu.sys_id is null \n");
				else
					sbQuery.append("  and mu.sys_id =:dcuName \n");
			}
			if(!modelId.equals("") && modelId!=null)
				sbQuery.append("  and mo.devicemodel_id = :modelId \n");
			sbQuery.append(" and (co.name <> 'Delete' or co.name is null) \n");
			sbQuery.append("  group by loc.name, loc.id, mu.sys_id                                      \n");
			sbQuery.append("  order by mu.sys_id asc nulls first                                                                                                   \n");
			
    	}
    	
    	query = getSession().createSQLQuery(sbQuery.toString());

    	if(!dcuName.equals("") && dcuName!=null && !dcuName.equals("-"))
			query.setParameter("dcuName", dcuName);
    	if(!modelId.equals("") && modelId!=null)
        	query.setParameter("modelId", Integer.parseInt(modelId));
    	
    	return query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
    }
    
    @Override
    public List<String> getTargetList(Map<String, Object> condition) {
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
    			.append("  SELECT mo.DEVICE_SERIAL||'/'||mo.location_id                                                                                                  \n")
    			.append("  FROM MODEM mo INNER JOIN LOCATION loc                                                                                                   \n")
    			.append("  ON mo.location_id = loc.id                                                                                                      \n")
    			.append("  LEFT OUTER JOIN MCU mu                                                                                                     \n")
    			.append("  ON mo.MCU_ID = mu.id                                                                                                      \n")
    			.append("  LEFT OUTER JOIN CODE co                                                                                                      \n")
    			.append(" ON mo.MODEM_STATUS = co.ID                                                                                                        \n");
    			if(!modelId.equals("") && modelId!=null)
    				sbQuery.append("  WHERE mo.DEVICEMODEL_ID = :modelId \n");
    			if(!deviceId.equals("") && deviceId!=null)
    				sbQuery.append("  and mo.DEVICE_SERIAL IN ("+deviceIds+") \n");
    			if(!fwVersion.equals("") && fwVersion!=null)
    				sbQuery.append("  and mo.FW_VER IN ("+fwVersions+") \n");
    			sbQuery.append("  and mo.FW_VER IS NOT NULL \n");
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
    				sbQuery.append("  and mo.INSTALL_DATE >= '"+installStartDate +"000000'\n");
    			if(!installEndtDate.equals("") && installEndtDate!=null)
    				sbQuery.append("  and mo.INSTALL_DATE <= '"+installEndtDate +"125959'\n");
    			if(!lastCommStartDate.equals("") && lastCommStartDate!=null)
    				sbQuery.append("  and mo.LAST_LINK_TIME >= '"+lastCommStartDate +"000000'\n");
    			if(!lastCommEndDate.equals("") && lastCommEndDate!=null)
    				sbQuery.append("  and mo.LAST_LINK_TIME <= '"+lastCommEndDate +"125959'\n");
    			if(!hwVer.equals("") && hwVer!=null)
    				sbQuery.append("  and mo.HW_VER = '"+hwVer+"' \n");
    			
    			sbQuery.append(" and (co.NAME <> 'Delete' or co.NAME IS NULL) \n");
    			sbQuery.append(" ORDER BY mo.location_id asc \n"); // 추가
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
    			return query.list();
    }
    
    @Override
    public List<String> getTargetListModem(Map<String, Object> condition) {
    	String modelId = StringUtil.nullToBlank(condition.get("modelId"));
        String versions = StringUtil.nullToBlank(condition.get("versions"));
        String deviceId = StringUtil.nullToBlank(condition.get("deviceId"));
        String fwVersion = StringUtil.nullToBlank(condition.get("fwVersion"));
        String locationId = StringUtil.nullToBlank(condition.get("locationId"));
        String locationName = StringUtil.nullToBlank(condition.get("locationName"));
        String dcuName = StringUtil.nullToBlank(condition.get("dcuName"));
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
        
    	StringBuffer sbQuery = new StringBuffer()
    			.append("  SELECT mo.DEVICE_SERIAL||'/'||mo.location_id                                                                                                  \n")
    			.append("  FROM MODEM mo INNER JOIN LOCATION loc                                                                                                   \n")
    			.append("  ON mo.location_id = loc.id                                                                                                      \n")
    			.append("  LEFT OUTER JOIN MCU mu                                                                                                     \n")
    			.append("  ON mo.MCU_ID = mu.id                                                                                                      \n")
    			.append("  LEFT OUTER JOIN CODE co                                                                                                      \n")
    			.append(" ON mo.MODEM_STATUS = co.ID                                                                                                        \n");
    			if(chkParent){
    				sbQuery.append(" WHERE mo.id in ( \n");
    				sbQuery.append("    select distinct(modem_id) from modem where devicemodel_id = (select id from devicemodel where name = '"+deviceModelName+"')  \n");
    				sbQuery.append("        and fw_ver is not null  \n");
    				sbQuery.append("        and fw_ver != '"+ fwVersion +"'");
    				sbQuery.append("        and modem_id is not null \n");
    				sbQuery.append("        and (modem_status is null or modem_status not in (select id from code where name in ('BreakDown' , 'Delete', 'Repair'))) \n");
    				sbQuery.append(" ) \n");
    				if(!modelId.equals("") && modelId!=null)
    					sbQuery.append("   AND mo.DEVICEMODEL_ID = :modelId \n");
    			}else{
    				if(!modelId.equals("") && modelId!=null)
    					sbQuery.append(" WHERE  mo.DEVICEMODEL_ID = :modelId \n");
    			}
    			if(!deviceId.equals("") && deviceId!=null)
    				sbQuery.append("  and mo.DEVICE_SERIAL IN ("+deviceIds+") \n");
    			if(!fwVersion.equals("") && fwVersion!=null)
    				sbQuery.append("  and mo.FW_VER IN ("+fwVersions+") \n");
    			sbQuery.append("  and mo.FW_VER IS NOT NULL \n");
    			if(!locationId.equals("") && locationId!=null)
    				sbQuery.append("  and loc.name IN ("+location+") \n");
    			sbQuery.append("  and loc.name IS NOT NULL \n");
    			if(!dcuall){
	    			if(!dcuName.equals("") && dcuName!=null){
	    				if(dcuName.equals("-"))
	    					sbQuery.append(" and mu.SYS_ID is null \n");
	    				else
	    					sbQuery.append("  and mu.SYS_ID IN ("+dcuNames+") \n");
	    			}
    			}
    			sbQuery.append("  and loc.id IN ("+locationNames+") \n");
    			if(!installStartDate.equals("") && installStartDate!=null)
    				sbQuery.append("  and mo.INSTALL_DATE >= '"+installStartDate +"000000'\n");
    			if(!installEndtDate.equals("") && installEndtDate!=null)
    				sbQuery.append("  and mo.INSTALL_DATE <= '"+installEndtDate +"125959'\n");
    			if(!lastCommStartDate.equals("") && lastCommStartDate!=null)
    				sbQuery.append("  and mo.LAST_LINK_TIME >= '"+lastCommStartDate +"000000'\n");
    			if(!lastCommEndDate.equals("") && lastCommEndDate!=null)
    				sbQuery.append("  and mo.LAST_LINK_TIME <= '"+lastCommEndDate +"125959'\n");
    			if(!hwVer.equals("") && hwVer!=null)
    				sbQuery.append("  and mo.HW_VER = '"+hwVer+"' \n");
    			
    			sbQuery.append(" and (co.NAME <> 'Delete' or co.NAME IS NULL) \n");
    			sbQuery.append(" ORDER BY mo.location_id asc \n"); // 추가
    			SQLQuery query = getSession().createSQLQuery(sbQuery.toString());
    			if(!modelId.equals("") && modelId!=null)
    	        	query.setParameter("modelId", Integer.parseInt(modelId));
    			/*if(!deviceId.equals("") && deviceId!=null)
    				query.setParameter("deviceId", deviceId);*/
    			/*if(!fwVersion.equals("") && fwVersion!=null)
    				query.setParameter("fwVersion", fwVersion);*/
    			/*if(!dcuName.equals("") && dcuName!=null && !dcuName.equals("-"))
    				query.setParameter("dcuName", dcuName);*/
    			/*if(!locationId.equals("") && locationId!=null)
    				query.setParameter("location", location);*/
    			return query.list();
    }
    
    @Override
    public List<Object> getModemList(Map<String, Object> condition){
        
    	String modelId = StringUtil.nullToBlank(condition.get("modelId"));
    	String deviceId = StringUtil.nullToBlank(condition.get("sModemId"));
    	String fwVersion = StringUtil.nullToBlank(condition.get("sModemFwVer"));
    	String locationId = StringUtil.nullToBlank(condition.get("sLocationId"));
    	String dcuName = StringUtil.nullToBlank(condition.get("sMcuName"));
    	String installStartDate = StringUtil.nullToBlank(condition.get("sInstallStartDate"));
    	String installEndtDate = StringUtil.nullToBlank(condition.get("sInstallEndDate"));
    	String lastCommStartDate = StringUtil.nullToBlank(condition.get("sLastcommStartDate"));
    	String lastCommEndDate = StringUtil.nullToBlank(condition.get("sLastcommEndDate"));
    	String hwVer = StringUtil.nullToBlank(condition.get("sModemHwVer"));
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
	    
	    //sp-1004 Devices Id (Excel Search 위한 modem ID)
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
    			sbQuery.append("  select  mo.device_serial as modemDeviceSerial      \n");
        		sbQuery.append("  ,model.name    AS deviceName  \n");
        		sbQuery.append("  ,case when mo.last_link_time is null then '' else mo.last_link_time end as lastCommDate   \n");
        		sbQuery.append("  ,mo.fw_ver    AS fwVer  \n");
        		sbQuery.append("   from modem mo\n");
        		//sbQuery.append("   inner join location loc											\n");
        		//sbQuery.append("  on mo.location_id = loc.id                                                                                                      \n");
        		sbQuery.append("  left outer join mcu mu                                                                                                     \n");
        		sbQuery.append("  on mo.mcu_id = mu.id                                                                                                      \n");
        		sbQuery.append("  left outer join code co                                                                                                      \n");
        		sbQuery.append("  on mo.modem_status = co.id                                                                                                        \n");
        		sbQuery.append("  left outer join devicemodel model                                                                                                        \n");
        		sbQuery.append("  on mo.devicemodel_id = model.id                                                                                                        \n");
        		sbQuery.append("  where mo.fw_ver is not null \n");
    			if(!deviceId.equals("") && deviceId!=null)
    				sbQuery.append("  and mo.device_serial in ("+deviceIds+") \n");
    			sbQuery.append("  and mo.fw_ver = '"+ fwVersion +"'");
    			sbQuery.append("  and mo.devicemodel_id = "+ modelId +" \n");
    			//sbQuery.append("  and loc.name is not null \n");
    			//if(!locationId.equals("") && locationId!=null)
    			//	sbQuery.append("  and loc.name in ("+location+") \n");
    			if(!locationId.equals("") && locationId!=null)
    	            sbQuery.append("     and mo.location_id in (" + locationId + ")");
    			if(!dcuName.equals("") && dcuName!=null){
    				if(dcuName.equals("-"))
    					sbQuery.append(" and mu.sys_id is null \n");
    				else
    					sbQuery.append("  and mu.sys_id =:dcuName \n");
    			}
    			if(!modelId.equals("") && modelId!=null)
    				sbQuery.append("  and mo.devicemodel_id = :modelId \n");
    			sbQuery.append(" and (co.name <> 'Delete' or co.name is null) \n");
    			
    	} else{ // fw 버전이 fwVersion이 아닌 것 중 부모가 fwVersion인 것 (체크했을때)
    		sbQuery.append("  select  mo.device_serial as modemDeviceSerial      \n");
    		sbQuery.append("  ,model.name    AS deviceName  \n");
    		sbQuery.append("  ,case when mo.last_link_time is null then '' else mo.last_link_time end as lastCommDate   \n");
    		sbQuery.append("  ,mo.fw_ver    AS fwVer  \n");
    		sbQuery.append("   from modem mo\n");
    		//sbQuery.append("   inner join location loc											\n");
    		//sbQuery.append("  on mo.location_id = loc.id                                                                                                      \n");
    		sbQuery.append("  left outer join mcu mu                                                                                                     \n");
    		sbQuery.append("  on mo.mcu_id = mu.id                                                                                                      \n");
    		sbQuery.append("  left outer join code co                                                                                                      \n");
    		sbQuery.append("  on mo.modem_status = co.id                                                                                                        \n");
    		sbQuery.append("  left outer join devicemodel model                                                                                                        \n");
    		sbQuery.append("  on mo.devicemodel_id = model.id                                                                                                        \n");
    		sbQuery.append(" where mo.id in ( \n");
			sbQuery.append("    select distinct(modem_id) from modem where devicemodel_id = (select id from devicemodel where name = '"+deviceModelName+"')  \n");
			if(!deviceId.equals("") && deviceId!=null)
				sbQuery.append("  		and mo.device_serial in ("+deviceIds+") \n");
			sbQuery.append("        and fw_ver is not null  \n");
			sbQuery.append("        and fw_ver != '"+ fwVersion +"'");
			sbQuery.append("        and modem_id is not null \n");
			sbQuery.append("        and (modem_status is null or modem_status not in (select id from code where name in ('BreakDown' , 'Delete', 'Repair'))) \n");
			sbQuery.append(" ) and mo.fw_ver is not null \n");
			sbQuery.append("  and mo.fw_ver = '"+ fwVersion +"'");
			//sbQuery.append("  and loc.name is not null \n");
			//if(!locationId.equals("") && locationId!=null)
			if(!locationId.equals("") && locationId!=null)
	            sbQuery.append("     and mo.location_id in (" + locationId + ")");
			if(!dcuName.equals("") && dcuName!=null){
				if(dcuName.equals("-"))
					sbQuery.append(" and mu.sys_id is null \n");
				else
					sbQuery.append("  and mu.sys_id =:dcuName \n");
			}
			if(!modelId.equals("") && modelId!=null)
				sbQuery.append("  and mo.devicemodel_id = :modelId \n");
			sbQuery.append(" and (co.name <> 'Delete' or co.name is null) \n");
			
    	}
    	
    	query = getSession().createSQLQuery(sbQuery.toString());

    	if(!dcuName.equals("") && dcuName!=null && !dcuName.equals("-"))
			query.setParameter("dcuName", dcuName);
    	if(!modelId.equals("") && modelId!=null)
        	query.setParameter("modelId", Integer.parseInt(modelId));
    	
    	return query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
        
    }

    //sp-1028
	@Override
	public List<Map<String, Object>> getValidModemList(Map<String, Object> condition) {
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
    			.append("  SELECT mo.DEVICE_SERIAL DEVICE_ID, mo.FW_VER VERSION	\n")
    			.append("  FROM MODEM mo INNER JOIN LOCATION loc 			\n")
    			.append("  ON mo.location_id = loc.id 						\n")
    			.append("  LEFT OUTER JOIN MCU mu 							\n")
    			.append("  ON mo.MCU_ID = mu.id 							\n")
    			.append("  LEFT OUTER JOIN CODE co 							\n")
    			.append(" ON mo.MODEM_STATUS = co.ID						\n");
    			
		sbQuery.append("  WHERE mo.DEVICEMODEL_ID = :modelId 	\n");
		sbQuery.append("  and mo.FW_VER is not null 			\n");
		sbQuery.append("  and mo.location_id is not null 		\n");
		sbQuery.append("  and (0, mo.DEVICE_SERIAL) IN (" + deviceIds + ") \n");
		sbQuery.append("  and mo.supplier_id = :supplierId 	\n");
		sbQuery.append(" and (co.NAME <> 'Delete' or co.NAME IS NULL) \n");
		sbQuery.append(" ORDER BY mo.FW_VER asc \n");

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
		
		sbQuery.append("\nselect modem.id, modem.device_serial deviceid, modem.fw_ver, parent_modem.device_serial parent_id, parent_modem.fw_ver parent_ver " );
		sbQuery.append("\nfrom modem " );
		sbQuery.append("\n    left outer join modem parent_modem " );
		sbQuery.append("\n    on modem.modem_id = parent_modem.id " );
		sbQuery.append("\nwhere (0, modem.device_serial) in  " );
		sbQuery.append("\n    ( "+ deviceIds +" ) " );
		sbQuery.append("\n    and modem.SUPPLIER_ID = :supplierId ");
		sbQuery.append("\n    and modem.DEVICEMODEL_ID = :modelId ");
		sbQuery.append("\n    and (modem.modem_status is null or modem.modem_status not in (select id from code where name in ('BreakDown' , 'Delete', 'Repair'))) ");
		sbQuery.append("\n    and modem.fw_ver != :fwVersion " );
		sbQuery.append("\n    and (parent_modem.fw_ver != :fwVersion or modem.modem_id is null)   " );


		SQLQuery query = getSession().createSQLQuery(sbQuery.toString());
		query.setParameter("modelId", Integer.parseInt(modelId));
		query.setParameter("fwVersion", fwVersion);
		query.setParameter("supplierId", Integer.parseInt(supplierId));

		return query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();

	}
    
}