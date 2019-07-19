package com.aimir.dao.device.impl;

import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractHibernateGenericDao;
import com.aimir.dao.device.SNRLogDao;
import com.aimir.model.device.SNRLog;
import com.aimir.util.SQLWrapper;

@Repository(value = "snrlogDao")
public class SNRLogDaoImpl extends AbstractHibernateGenericDao<SNRLog, Integer> implements SNRLogDao {
    private static Log log = LogFactory.getLog(SNRLogDaoImpl.class);
    
    @Autowired
    protected SNRLogDaoImpl(SessionFactory sessionFactory) {
        super(SNRLog.class);
        super.setSessionFactory(sessionFactory);
    }
    
    // 주어진 모뎀 리스트와 날짜 조건을 바탕으로 snr값 조회
    public List<Object> getSnrWithPeriod(Map<String,Object> conditionMap, List deviceList){
        StringBuffer sbQuery    = new StringBuffer();
        sbQuery.append("  SELECT device_id, mx, dcu_id, snr \n") 
               .append("  FROM SNR_LOG INNER JOIN   \n")
               .append("  (SELECT device_id as dx, MAX(yyyymmdd||hhmmss) as mx \n")
               .append("  FROM SNR_LOG  \n")
               .append("  WHERE (yyyymmdd||hhmmss) >= :startDate AND (yyyymmdd||hhmmss) <= :endDate")
               .append("  AND device_id IN (:deviceList)  \n")
               .append("  GROUP BY device_id)  \n")
               .append("  ON device_id=dx AND mx=(yyyymmdd||hhmmss)  \n")
               .append("  WHERE (yyyymmdd||hhmmss) >= :startDate AND (yyyymmdd||hhmmss) <= :endDate");
        
        //SQLQuery query = getSession().createSQLQuery(sbQuery.toString());
        SQLQuery dataQueryObj = getSession().createSQLQuery(new SQLWrapper().getQuery(sbQuery.toString()));
        dataQueryObj.setString("startDate", conditionMap.get("startDate").toString());
        dataQueryObj.setString("endDate", conditionMap.get("endDate").toString());
        dataQueryObj.setParameterList("deviceList", deviceList);
        List<Object> result = dataQueryObj.list(); 
        return result;
    }
    
    // 주어진 모뎀 리스트와 날짜 조건하에서 통계값 조회
    public List<Object> getSnrStatisticWithPeriod(Map<String,Object> conditionMap, List deviceList){
        StringBuffer sbQuery    = new StringBuffer();
        sbQuery.append("  SELECT device_id, AVG(snr), MAX(snr), MIN(snr) \n") 
               .append("  FROM SNR_LOG INNER JOIN   \n")
               .append("  (SELECT device_id as dx, MAX(yyyymmdd) as mx \n")
               .append("  FROM SNR_LOG  \n")
               .append("  WHERE (yyyymmdd||hhmmss) >= :startDate AND (yyyymmdd||hhmmss) <= :endDate")
               .append("  AND device_id IN (:deviceList)  \n")
               .append("  GROUP BY device_id)  \n")
               .append("  ON device_id=dx AND mx=yyyymmdd  \n")
               .append("  WHERE (yyyymmdd||hhmmss) >= :startDate AND (yyyymmdd||hhmmss) <= :endDate")
               .append("  GROUP BY device_id  \n");
               
        
        //SQLQuery query = getSession().createSQLQuery(sbQuery.toString());
        SQLQuery dataQueryObj = getSession().createSQLQuery(new SQLWrapper().getQuery(sbQuery.toString()));
        dataQueryObj.setString("startDate", conditionMap.get("startDate").toString());
        dataQueryObj.setString("endDate", conditionMap.get("endDate").toString());
        dataQueryObj.setParameterList("deviceList", deviceList);
        List<Object> result = dataQueryObj.list(); 
        return result;
    }
    
    // 주어진 모뎀 리스트만을 가지고 각 모뎀별 마지막 날짜와 해당 snr값 조회
    public List<Object> getSnrWithoutPeriod(Map<String,Object> conditionMap, List deviceList){
        StringBuffer sbQuery    = new StringBuffer();
        sbQuery.append("  SELECT device_id, mx, dcu_id, snr \n") 
               .append("  FROM SNR_LOG INNER JOIN   \n")
               .append("  (SELECT device_id as dx, MAX(yyyymmdd||hhmmss) as mx \n")
               .append("  FROM SNR_LOG  \n")
               .append("  WHERE device_id IN (:deviceList)  \n")
               .append("  GROUP BY device_id)  \n")
               .append("  ON device_id=dx AND mx=(yyyymmdd||hhmmss)  \n");
        
        //SQLQuery query = getSession().createSQLQuery(sbQuery.toString());
        SQLQuery dataQueryObj = getSession().createSQLQuery(new SQLWrapper().getQuery(sbQuery.toString()));
        //dataQueryObj.setString("sysId", conditionMap.get("sysId").toString());
        dataQueryObj.setParameterList("deviceList", deviceList);
        List<Object> result = dataQueryObj.list(); 
        return result;
    }
    
    // 주어진 모뎀 리스트만을 가지고 각 모뎀별 마지막 날짜의 snr 통계값 조회
    public List<Object> getSnrStatisticWithoutPeriod(Map<String,Object> conditionMap, List deviceList){
        StringBuffer sbQuery    = new StringBuffer();
        sbQuery.append("  SELECT device_id, AVG(snr), MAX(snr), MIN(snr) \n") 
               .append("  FROM SNR_LOG INNER JOIN   \n")
               .append("  (SELECT device_id as dx, MAX(yyyymmdd) as mx \n")
               .append("  FROM SNR_LOG  \n")
               .append("  WHERE device_id IN (:deviceList)  \n")
               .append("  GROUP BY device_id)  \n")
               .append("  ON device_id=dx AND mx=yyyymmdd  \n")
               .append("  GROUP BY device_id  \n"); 
        
        //SQLQuery query = getSession().createSQLQuery(sbQuery.toString());
        SQLQuery dataQueryObj = getSession().createSQLQuery(new SQLWrapper().getQuery(sbQuery.toString()));
        //dataQueryObj.setString("sysId", conditionMap.get("sysId").toString());
        dataQueryObj.setParameterList("deviceList", deviceList);
        List<Object> result = dataQueryObj.list(); 
        return result;
    }

    
    /**
     * Method Name : getLastSnrByMcu
     * Method Desc : 주어진 기간내에서 모뎀별로 가장 마지막에 올린 데이터를 조회
     * @param conditionMap
     * @return
     */
    public List<Object> getLastSnrByMcu(Map<String,Object> conditionMap){

        StringBuffer sbQuery    = new StringBuffer();
        sbQuery.append("  SELECT device_id, mx, dcu_id, snr  \n")
               .append("  FROM SNR_LOG INNER JOIN  \n")
               .append("  (SELECT device_id AS dx, MAX((yyyymmdd||hhmmss)) AS mx  \n")
               .append("  FROM SNR_LOG WHERE (yyyymmdd||hhmmss) >= :startDate AND (yyyymmdd||hhmmss) <= :endDate  \n");                 
        
        if(conditionMap.get("isPoor").equals("poor")){
            sbQuery.append("  AND snr <= -2  \n");
        }
        sbQuery.append("  AND dcu_id = :sysId GROUP BY device_id)  \n")
               .append("  ON mx=(yyyymmdd||hhmmss)  ")
               .append("  ORDER BY mx DESC");
        
        
        //SQLQuery query = getSession().createSQLQuery(sbQuery.toString());
        SQLQuery dataQueryObj = getSession().createSQLQuery(new SQLWrapper().getQuery(sbQuery.toString()));
        dataQueryObj.setString("startDate", conditionMap.get("startDate").toString());
        dataQueryObj.setString("endDate", conditionMap.get("endDate").toString());
        dataQueryObj.setString("sysId", conditionMap.get("sysId").toString());      
        
        List<Object> result = dataQueryObj.list();
        return result;
    }
    
    /**
     * Method Desc : getLastSnrByMcu에서 날짜 조건을 무시하고 마지막 데이터를 조회
     * @param conditionMap
     */
    public List<Object> getFinalSnrByMcu(Map<String,Object> conditionMap){
    
        StringBuffer sbQuery    = new StringBuffer();
        sbQuery.append("  SELECT device_id, mx, dcu_id, snr  \n")
               .append("  FROM SNR_LOG INNER JOIN  \n")
               .append("  (SELECT device_id AS dx, MAX((yyyymmdd||hhmmss)) AS mx  \n")
               .append("  FROM SNR_LOG WHERE dcu_id = :sysId    \n");
        if(conditionMap.get("isPoor").equals("poor")){
            sbQuery.append("  AND snr <= -2  \n");
        }
        sbQuery.append("  GROUP BY device_id) ON mx=(yyyymmdd||hhmmss)  ")
               .append("  ORDER BY mx DESC");
        
        
        //SQLQuery query = getSession().createSQLQuery(sbQuery.toString());
        SQLQuery dataQueryObj = getSession().createSQLQuery(new SQLWrapper().getQuery(sbQuery.toString()));
        dataQueryObj.setString("sysId", conditionMap.get("sysId").toString());
        
        List<Object> result = dataQueryObj.list();
        return result;
    }
    
    /**
     * Method Name : getStatisticsByMcu
     * Method Desc : 주어진 기간내에서 각 모뎀의 SNR 통계값을 조회
     * @param conditionMap
     * @return
     */ 
    public List<Object> getStatisticsByMcu(Map<String,Object> conditionMap){

        StringBuffer sbQuery    = new StringBuffer();
        sbQuery.append("  SELECT device_id, AVG(snr), MAX(snr), MIN(snr) \n") 
               .append("  FROM SNR_LOG  \n")
               .append("  WHERE (yyyymmdd||hhmmss) >= :startDate AND (yyyymmdd||hhmmss) <= :endDate \n")
               .append("  AND dcu_id = :sysId  \n");
          
        if(conditionMap.get("isPoor").equals("poor")){
            sbQuery.append("  AND snr <= -2  \n");
        }
        sbQuery.append("  GROUP BY device_id \n");
        
        //SQLQuery query = getSession().createSQLQuery(sbQuery.toString());
        SQLQuery dataQueryObj = getSession().createSQLQuery(new SQLWrapper().getQuery(sbQuery.toString()));
        dataQueryObj.setString("startDate", conditionMap.get("startDate").toString());
        dataQueryObj.setString("endDate", conditionMap.get("endDate").toString());
        dataQueryObj.setString("sysId", conditionMap.get("sysId").toString());
        List<Object> result = dataQueryObj.list();
        return result;
    }
    
    
    /**
     * Method Desc : 각 모뎀이 해당 집중기에서 마지막으로 SNR을 업로드한 시간과 아이디의 리스트 조회
     * @param conditionMap
     */
    public List<Object> getFinalDayByMcu(String _sysId, Boolean _isPoor){
        
        StringBuffer sbQuery    = new StringBuffer();
        sbQuery.append("  SELECT device_id, MAX(yyyymmdd) \n") 
               .append("  FROM SNR_LOG WHERE dcu_id = :sysId\n");
        if(_isPoor){
            sbQuery.append("  AND snr <= -2  \n");
        }        
        sbQuery.append("  GROUP BY device_id  \n");
        
        //SQLQuery query = getSession().createSQLQuery(sbQuery.toString());
        SQLQuery dataQueryObj = getSession().createSQLQuery(new SQLWrapper().getQuery(sbQuery.toString()));
        dataQueryObj.setString("sysId", _sysId);
        List<Object> result = dataQueryObj.list(); 
        return result;
    }
    
    /**
     * Method Desc : getFinalDayByMcu에서 얻은 날짜(당일)의 해당 모뎀의 통계치를 계산
     * @param conditionMap
     */
    public List<Object> getStatisticsByModem(String _sysId, Boolean _isPoor, String _modemId, String _maxDate){
        
        StringBuffer sbQuery    = new StringBuffer();
        sbQuery.append("  SELECT device_id, AVG(snr), MAX(snr), MIN(snr) \n") 
               .append("  FROM SNR_LOG WHERE dcu_id = :sysId  \n")             
               .append("  AND device_id = :modemId  \n")
               .append("  AND yyyymmdd = :maxDate  \n");
        if(_isPoor){
            sbQuery.append(" AND snr <= -2  \n");
        }
         sbQuery.append("  GROUP BY device_id  \n");
        
        //SQLQuery query = getSession().createSQLQuery(sbQuery.toString());
        SQLQuery dataQueryObj = getSession().createSQLQuery(new SQLWrapper().getQuery(sbQuery.toString()));
        dataQueryObj.setString("sysId", _sysId);
        dataQueryObj.setString("modemId", _modemId);
        dataQueryObj.setString("maxDate", _maxDate);
        List<Object> result = dataQueryObj.list(); 
        return result;
    }
    
    /**
     * Method Name : getSnrChartByModem
     * Method Desc : 주어진 기간내에서 선택된 모뎀의 SNR 데이터 조회. 차트 생성.
     * @param conditionMap
     * @return
     */
    public List<Object> getSnrChartByModem(Map<String,Object> conditionMap){
        
        StringBuffer sbQuery    = new StringBuffer();
        sbQuery.append("  SELECT (yyyymmdd||hhmmss) AS mx, snr, dcu_id FROM SNR_LOG  \n")
               .append("  WHERE (yyyymmdd||hhmmss) >= :startDate AND (yyyymmdd||hhmmss) <= :endDate  \n")
               .append("  AND device_id = :modemId  \n")
               .append("  ORDER BY yyyymmdd DESC, hhmmss DESC  \n");
        
        SQLQuery dataQueryObj = getSession().createSQLQuery(new SQLWrapper().getQuery(sbQuery.toString()));
        dataQueryObj.setString("startDate", conditionMap.get("startDate").toString());
        dataQueryObj.setString("endDate", conditionMap.get("endDate").toString());
        dataQueryObj.setString("modemId", conditionMap.get("modemId").toString());
        List<Object> result = dataQueryObj.list();
        
        return result;
    }

    /**
     * Method Name : getFinalSnrChartByModem
     * Method Desc : 주어진 기간과 상관없이 선택된 모뎀의 마지막 데이터를 조회.
     * @param conditionMap
     * @return
     */
    public List<Object> getFinalSnrChartByModem(Map<String, Object> conditionMap) {

        StringBuffer sbQuery    = new StringBuffer();
        sbQuery.append("  SELECT (yyyymmdd||hhmmss) AS mx, snr, dcu_id FROM SNR_LOG  \n")
               .append("  WHERE device_id = :modemId  \n")
               .append("  AND yyyymmdd in  \n")
               .append("  (SELECT MAX(yyyymmdd) AS mx FROM SNR_LOG WHERE device_id = :modemId)  \n")
               .append("  ORDER BY yyyymmdd DESC, hhmmss DESC  \n");
        
        SQLQuery dataQueryObj = getSession().createSQLQuery(new SQLWrapper().getQuery(sbQuery.toString()));
        dataQueryObj.setString("modemId", conditionMap.get("modemId").toString());
        List<Object> result = dataQueryObj.list();
        
        return result;
    }
}