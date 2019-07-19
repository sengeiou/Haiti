package com.aimir.dao.mvm.impl;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Hibernate;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.aimir.constants.CommonConstants;
import com.aimir.constants.CommonConstants.ChannelCalcMethod;
import com.aimir.constants.CommonConstants.MeterType;
import com.aimir.dao.AbstractHibernateGenericDao;
import com.aimir.dao.mvm.MeteringLpDao;
import com.aimir.dao.system.SupplierDao;
import com.aimir.model.mvm.MeteringLP;
import com.aimir.model.system.Supplier;
import com.aimir.util.CalendarUtil;
import com.aimir.util.DecimalUtil;
import com.aimir.util.SQLWrapper;
import com.aimir.util.StringUtil;
import com.aimir.util.TimeLocaleUtil;
import com.aimir.util.TimeUtil;

@Repository(value = "meteringlpDao")
public class MeteringLpDaoImpl extends
		AbstractHibernateGenericDao<MeteringLP, Integer> implements
		MeteringLpDao {

    @Autowired
    SupplierDao supplierDao;

    @SuppressWarnings("unused")
    private static Log logger = LogFactory.getLog(MeteringLpDaoImpl.class);

	@Autowired
	protected MeteringLpDaoImpl(SessionFactory sessionFactory) {
		super(MeteringLP.class);
		super.setSessionFactory(sessionFactory);
	}

    @SuppressWarnings({"unchecked", "unused"})
    public Map<String, Object> getMissingCountByDay(Map<String, Object> params) {
        String searchStartDate = (String) params.get("searchStartDate");
        String searchEndDate = (String) params.get("searchEndDate");
        String meterType = StringUtil.nullToBlank(params.get("meterType"));
        String supplierId = StringUtil.nullToBlank(params.get("supplierId"));
        Integer channel = (Integer) params.get("channel");
        Integer meterId = Integer.parseInt(String.valueOf(params.get("meterId")));
        String mdsId = StringUtil.nullToBlank(params.get("mdsId"));
        String deviceType = (String) params.get("deviceType");

        String today = TimeUtil.getCurrentTimeMilli(); // yyyymmddhhmmss
        String currDate = today.substring(0, 8);
        String currHour = today.substring(8, 10);
        Integer currMinute = Integer.parseInt(today.substring(10, 12));

        String lpTable = CommonConstants.MeterType.valueOf(meterType).getLpTableName();

        Supplier localeSupplier = supplierDao.get(Integer.parseInt(supplierId));

        int period = 1;
        
        try {
            period = TimeUtil.getDayDuration(searchStartDate, searchEndDate) + 1;
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (Exception e) {
            // 초기 날짜 포멧이 YYYYMMDD로 파싱되지 않는 경우 Exception 처리

            try {
                searchStartDate = TimeUtil.getCurrentDay();
                searchEndDate = TimeUtil.getCurrentDay();
            } catch (ParseException e1) {
                e1.printStackTrace();
            }
            e.printStackTrace();
        }

        int interval = 0;
        try {
            interval = Integer.parseInt(String.valueOf(params.get("lpInterval")));
            if(interval == 0) interval = 60;
        } catch(Exception e) {
            interval = 60;  // Meter.LP_Interval 값이 null 이거나 Exception 발생 시, 60으로 default 설정
        }
        int intervalCnt = 0;
        try {
            intervalCnt = (int) Math.ceil(60 / interval); // 주기 카운트
        } catch(Exception e) {
            intervalCnt = 1;
        }
        int totalCnt = intervalCnt * 24; // 하루동안의 전체 건수
        int totayTotalCnt = intervalCnt * Integer.parseInt(currHour); // 오늘날짜의
                                                                      // 현재시간
                                                                      // 이전까지의
                                                                      // 전체 건수
        int currHourTotalCnt = ((int) Math.ceil(currMinute)/ interval)+1 ; // 현재시간의 전체 건수

        if (currHourTotalCnt < 1)
            currHourTotalCnt = 1;

        // 10보다 작은 수 앞에 0 붙이기
        DecimalFormat df = new DecimalFormat("00");

        // 시작일자 ~ 종료일자(현재일자일경우 하루전일)
        StringBuffer sbQuery = new StringBuffer();
        sbQuery.append("\nSELECT lp.YYYYMMDD ");
        sbQuery.append("\n       , :totalCnt - ( sum(lp.value_cnt) ) ");
        sbQuery.append("\nFROM ").append(lpTable).append(" lp ");
        sbQuery.append("\nWHERE 1=1 ");
        if (mdsId.isEmpty()) {
            sbQuery.append("\nAND   lp.meter_id = :meterId ");
        } else {
            sbQuery.append("\nAND   lp.mdev_id = :mdsId ");
        }
        sbQuery.append("\nAND lp.channel = :channel ");
        sbQuery.append("\nAND lp.yyyymmddhh BETWEEN :startDate AND :endDate ");
        sbQuery.append("\nGROUP BY lp.meter_id, lp.yyyymmdd ");

        // 현재일자의 현재시간 전시간까지
        StringBuffer sbQueryToday = new StringBuffer();

        sbQueryToday.append("\nSELECT :totalCnt - (SUM(lp.value_cnt)) AS successCnt ");
        sbQueryToday.append("\nFROM ").append(lpTable).append(" lp ");
        sbQueryToday.append("\nWHERE 1=1 ");
        if (mdsId.isEmpty()) {
            sbQueryToday.append("\nAND   lp.meter_id = :meterId ");
        } else {
            sbQueryToday.append("\nAND   lp.mdev_id = :mdsId ");
        }
        sbQueryToday.append("\nAND lp.channel = :channel ");
        sbQueryToday.append("\nAND lp.yyyymmddhh BETWEEN :startDate AND :endDate ");
        sbQueryToday.append("\nAND lp.hh < :currHour ");
        sbQueryToday.append("\nGROUP BY lp.meter_id, lp.yyyymmdd ");

        // 현재일자의 현재시간
        StringBuffer sbQueryCurrHour = new StringBuffer();
        sbQueryCurrHour.append("\nSELECT :totalCnt - (SUM(lp.value_cnt)) ");
        sbQueryCurrHour.append("\nFROM ").append(lpTable).append(" lp ");
        sbQueryCurrHour.append("\nWHERE 1=1 ");
        if (mdsId.isEmpty()) {
            sbQueryCurrHour.append("\nAND   lp.meter_id = :meterId ");
        } else {
            sbQueryCurrHour.append("\nAND   lp.mdev_id = :mdsId ");
        }
        sbQueryCurrHour.append("\nAND   lp.channel = :channel ");
        sbQueryCurrHour.append("\nAND   lp.yyyymmddhh BETWEEN :startDate AND :endDate ");
        sbQueryCurrHour.append("\nAND   lp.hh = :currHour ");

        Integer todayMissingCount = 0;
        Map<String, Object> missingData = new HashMap<String, Object>();
        if (Integer.parseInt(searchEndDate) < Integer.parseInt(currDate)) {
            Query query = getSession().createSQLQuery(sbQuery.toString());

            if (mdsId.isEmpty()) {
                query.setInteger("meterId", meterId);
            } else {
                query.setString("mdsId", mdsId);
            }
            query.setInteger("channel", channel);
            query.setString("startDate", searchStartDate + "00");
            query.setString("endDate", searchEndDate + "23");
            query.setInteger("totalCnt", totalCnt);

            // query 결과목록 - 시작일부터 종료일전일까지의 결과
            List<Object> missingDataList = query.list();
            for (Object obj : missingDataList) {
                Object[] objs = (Object[]) obj;
                missingData.put((String) objs[0], ((Number) objs[1]).intValue());
            }
        } else {
        	//startEndDate가 오늘날짜일경우 - 오늘 전날까지 데이터 검색
            Query query = getSession().createSQLQuery(sbQuery.toString());

            if (mdsId.isEmpty()) {
                query.setInteger("meterId", meterId);
            } else {
                query.setString("mdsId", mdsId);
            }
            query.setInteger("channel", channel);
            query.setString("startDate", searchStartDate + "00");
            query.setString("endDate", CalendarUtil.getDateWithoutFormat(searchEndDate, Calendar.DATE, -1) + "23");
            query.setInteger("totalCnt", totalCnt);

            // query 결과목록 - 시작일부터 종료일전일까지의 결과
            List<Object> missingDataList = query.list();
            for (Object obj : missingDataList) {
                Object[] objs = (Object[]) obj;
                missingData.put((String) objs[0], ((Number) objs[1]).intValue());
            }

            //오늘 결과
            Query queryToday = getSession().createSQLQuery(sbQueryToday.toString());

            if (mdsId.isEmpty()) {
                queryToday.setInteger("meterId", meterId);
            } else {
                queryToday.setString("mdsId", mdsId);
            }

            queryToday.setInteger("channel", channel);
            queryToday.setString("startDate", searchEndDate + "00");
            queryToday.setString("endDate", searchEndDate + "23");
            queryToday.setInteger("totalCnt", totayTotalCnt);
            queryToday.setString("currHour", currHour);

            Number todayMissingCnt = (Number) queryToday.uniqueResult();
            if (todayMissingCnt == null)
                todayMissingCnt = totayTotalCnt;

            Query queryCurrHour = getSession().createSQLQuery(sbQueryCurrHour.toString());

            if (mdsId.isEmpty()) {
                queryCurrHour.setInteger("meterId", meterId);
            } else {
                queryCurrHour.setString("mdsId", mdsId);
            }
            queryCurrHour.setInteger("channel", channel);
            queryCurrHour.setString("startDate", searchEndDate + "00");
            queryCurrHour.setString("endDate", searchEndDate + "23");
            queryCurrHour.setInteger("totalCnt", currHourTotalCnt);
            queryCurrHour.setString("currHour", currHour);

            Number currHourMissingCnt = (Number) queryCurrHour.uniqueResult();
            if (currHourMissingCnt == null)
                currHourMissingCnt = currHourTotalCnt;

            todayMissingCount = todayMissingCnt.intValue() + currHourMissingCnt.intValue();

        }

        List<Object> resultList = new ArrayList<Object>();
        // 특정일자에 모두 누락될수도 있으므로 시작일부터 종료일까지의 데이터는 고정
        int totalCount = 0;
        Map<String, Object> map = null;
        String lang = localeSupplier.getLang().getCode_2letter();
        String country = localeSupplier.getCountry().getCode_2letter();

        for (int j = 0; j < period; j++) {
            String yyyymmdd = CalendarUtil.getDateWithoutFormat(searchStartDate, Calendar.DATE, j);
            map = new HashMap<String, Object>();
            map.put("no", j + 1);
            map.put("yyyymmdd", yyyymmdd);

            if (yyyymmdd.equals(currDate)) {// 오늘날짜이고 현재시간 이후일경우
                                            // todayMissingCount 으로 세팅한다.
                map.put("missingCount", todayMissingCount);
                totalCount = totalCount + todayMissingCount;
            } else {
                Integer missingCount = missingData.get(yyyymmdd) == null ? totalCnt : ((Number) missingData.get(yyyymmdd)).intValue();
                map.put("missingCount", missingCount);
                totalCount = totalCount + missingCount;
            }

            map.put("xField", TimeLocaleUtil.getLocaleDate(yyyymmdd, lang, country));

            resultList.add(map);
        }
        Map<String, Object> resultMap = new HashMap<String, Object>();
        resultMap.put("totalCount", Integer.toString(totalCount));
        resultMap.put("resultList", resultList);
        return resultMap;
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> getMissingCountByHour(Map<String, Object> params) {
        String searchStartDate = (String) params.get("yyyymmdd");
        String meterType = StringUtil.nullToBlank(params.get("meterType"));
        @SuppressWarnings("unused")
        String supplierId = StringUtil.nullToBlank(params.get("supplierId"));
        Integer channel = (Integer) params.get("channel");
        Integer meterId = Integer.parseInt(String.valueOf(params.get("meterId")));
//        String deviceType = (String) params.get("deviceType");
        String mdsId = StringUtil.nullToBlank(params.get("mdsId"));

        String today = TimeUtil.getCurrentTimeMilli(); // yyyymmddhhmmss
        String currDate = today.substring(0, 8);
        String currHour = today.substring(8, 10);
        Integer currMinute = Integer.parseInt(today.substring(10, 12));

        String lpTable = CommonConstants.MeterType.valueOf(meterType).getLpTableName();

        int interval = 0;
        try {
            interval = Integer.parseInt(String.valueOf(params.get("lpInterval")));
            if(interval == 0) interval = 60;
        } catch(Exception e) {
            interval = 60;  // Meter.LP_Interval 값이 null 이거나 Exception 발생 시, 60으로 default 설정
        }
        int intervalCnt = 0;
        try {
            intervalCnt = (int) Math.ceil(60 / interval); // 주기 카운트
        } catch(Exception e) {
            intervalCnt = 1;
        }
        int currHourTotalCnt = ((int) Math.ceil(currMinute)/ interval)+1; // 현재시간의 전체 건수
        if (currHourTotalCnt < 1)
            currHourTotalCnt = 1;

        // 10보다 작은 수 앞에 0 붙이기
        DecimalFormat df = new DecimalFormat("00");

        // 현재일자가 아닐경우
        StringBuffer sbQuery = new StringBuffer();
        sbQuery.append("\nSELECT lp.hh ");
        sbQuery.append("\n     , :totalCnt - (SUM(lp.value_cnt)) ");
        //for (int i = 0; i < 60; i = i + interval) {
        //  sbQuery.append(" + COUNT(lp.value_" + df.format(i) + ")"); // value_00
                                                                        // ~
                                                                        // value_59
        //}
        //sbQuery.append("\n )");
        sbQuery.append("\nFROM ").append(lpTable).append(" lp ");
//        sbQuery.append("\nWHERE lp.meter_id = :meterId ");
        sbQuery.append("\nWHERE 1=1 ");
        if (mdsId.isEmpty()) {
            sbQuery.append("\nAND   lp.meter_id = :meterId ");
        } else {
            sbQuery.append("\nAND   lp.mdev_id = :mdsId ");
        }
        sbQuery.append("\nAND   lp.channel = :channel  ");
//        sbQuery.append("\nAND   lp.yyyymmdd = :searchStartDate ");
        sbQuery.append("\nAND   lp.yyyymmddhh BETWEEN :startDate AND :endDate ");
        sbQuery.append("\nGROUP BY lp.meter_id, lp.yyyymmdd, lp.hh ");

        // 현재일자의 현재시간 전시간까지
        StringBuffer sbQueryToday = new StringBuffer();
//        sbQueryToday.append("\n SELECT lp.hh , :totalCnt - (SUM(lp.value_cnt)");
        //for (int i = 0; i < 60; i = i + interval) {
        //  sbQueryToday.append(" + count(lp.value_" + df.format(i) + ")"); // value_00
                                                                            // ~
                                                                            // value_59
        //}
//        sbQueryToday.append("\n ) AS successCnt");
        sbQueryToday.append("\nSELECT lp.hh , :totalCnt - (SUM(lp.value_cnt)) AS successCnt ");
        sbQueryToday.append("\nFROM ").append(lpTable).append(" lp ");
//        sbQueryToday.append("\nWHERE lp.METER_ID = :meterId");
        sbQueryToday.append("\nWHERE 1=1 ");
        if (mdsId.isEmpty()) {
            sbQueryToday.append("\nAND   lp.meter_id = :meterId ");
        } else {
            sbQueryToday.append("\nAND   lp.mdev_id = :mdsId ");
        }
        sbQueryToday.append("\nAND   lp.channel = :channel ");
//        sbQueryToday.append("\nAND   lp.YYYYMMDD = :searchStartDate ");
        sbQueryToday.append("\nAND   lp.yyyymmddhh BETWEEN :startDate AND :endDate ");
        sbQueryToday.append("\nAND   lp.hh < :currHour ");
        sbQueryToday.append("\nGROUP BY lp.meter_id, lp.yyyymmdd, lp.hh");

        // 현재일자의 현재시간
        StringBuffer sbQueryCurrHour = new StringBuffer();
        sbQueryCurrHour.append("\nSELECT  lp.hh,:totalCnt - lp.value_cnt ");
        //for (int i = 0; i < 60; i = i + interval) {
        //  if (i > currMinute)
        //      break;
        //  sbQueryCurrHour.append(" + count(lp.value_" + df.format(i) + ")"); // value_00
                                                                                // ~
                                                                                // value_59
        //}
//      sbQueryCurrHour.append("\n )");
        sbQueryCurrHour.append("\nFROM ").append(lpTable).append(" lp ");
//        sbQueryCurrHour.append("\nWHERE lp.meter_id = :meterId ");
        sbQueryCurrHour.append("\nWHERE 1=1 ");
        if (mdsId.isEmpty()) {
            sbQueryCurrHour.append("\nAND   lp.meter_id = :meterId ");
        } else {
            sbQueryCurrHour.append("\nAND   lp.mdev_id = :mdsId ");
        }
        sbQueryCurrHour.append("\nAND   lp.channel = :channel ");
//        sbQueryCurrHour.append("\nAND   lp.yyyymmdd = :searchStartDate ");
        sbQueryCurrHour.append("\nAND   lp.yyyymmddhh BETWEEN :startDate AND :endDate ");
        sbQueryCurrHour.append("\nAND   lp.hh = :currHour ");
        sbQueryCurrHour.append("\nGROUP BY lp.meter_id, lp.yyyymmdd, lp.hh, lp.value_cnt ");

        Map<String, Object> missingData = new HashMap<String, Object>();
        if (Integer.parseInt(searchStartDate) < Integer.parseInt(currDate)) {
            Query query = getSession().createSQLQuery(sbQuery.toString());
//            query.setInteger("meterId", meterId);
            if (mdsId.isEmpty()) {
                query.setInteger("meterId", meterId);
            } else {
                query.setString("mdsId", mdsId);
            }
            query.setInteger("channel", channel);
//            query.setString("searchStartDate", searchStartDate);
            query.setString("startDate", searchStartDate + "00");
            query.setString("endDate", searchStartDate + "23");
            query.setInteger("totalCnt", intervalCnt);

            // query 결과목록 - 시작일부터 종료일전일까지의 결과
            List<Object> missingDataList = query.list();
            for (Object obj : missingDataList) {
                Object[] objs = (Object[]) obj;
                missingData.put((String) objs[0], ((Number) objs[1]).intValue());
            }
        } else {
            Query queryToday = getSession().createSQLQuery(sbQueryToday.toString());
//            queryToday.setInteger("meterId", meterId);
            if (mdsId.isEmpty()) {
                queryToday.setInteger("meterId", meterId);
            } else {
                queryToday.setString("mdsId", mdsId);
            }
            queryToday.setInteger("channel", channel);
//            queryToday.setString("searchStartDate", searchStartDate);
            queryToday.setString("startDate", searchStartDate + "00");
            queryToday.setString("endDate", searchStartDate + "23");
            queryToday.setInteger("totalCnt", intervalCnt);
            queryToday.setString("currHour", currHour);

            // query 결과목록 - 시작일부터 종료일전일까지의 결과
            List<Object> missingDataList = queryToday.list();
            for (Object obj : missingDataList) {
                Object[] objs = (Object[]) obj;
                missingData.put((String) objs[0], ((Number) objs[1]).intValue());
            }

            Query queryCurrHour = getSession().createSQLQuery(sbQueryCurrHour.toString());
//            queryCurrHour.setInteger("meterId", meterId);
            if (mdsId.isEmpty()) {
                queryCurrHour.setInteger("meterId", meterId);
            } else {
                queryCurrHour.setString("mdsId", mdsId);
            }
            queryCurrHour.setInteger("channel", channel);
//            queryCurrHour.setString("searchStartDate", searchStartDate);
            queryCurrHour.setString("startDate", searchStartDate + "00");
            queryCurrHour.setString("endDate", searchStartDate + "23");
            queryCurrHour.setInteger("totalCnt", currHourTotalCnt);
            queryCurrHour.setString("currHour", currHour);

            List<Object> currHourMissingDataList = queryCurrHour.list();
            for (Object obj : currHourMissingDataList) {
                Object[] objs = (Object[]) obj;
                missingData.put((String) objs[0], ((Number) objs[1]).intValue());
            }
        }

        List<Object> resultList = new ArrayList<Object>();
        HashMap<String, Object> map = null;
        String hh = null;
        // 특정일자에 모두 누락될수도 있으므로 시작일부터 종료일까지의 데이터는 고정
        for (int j = 0; j < 24; j++) {
            hh = df.format(j);
            map = new HashMap<String, Object>();
            map.put("no", j + 1);
            map.put("hh", hh);

            Integer missingCount = 0;
            if (Integer.parseInt(searchStartDate) == Integer.parseInt(currDate)) {
                if (j > Integer.parseInt(currHour)) {
                    missingCount = 0;
                } else {
                    if (j == Integer.parseInt(currHour)) {
                        missingCount = missingData.get(hh) == null ? currHourTotalCnt
                                : ((Number) missingData.get(hh)).intValue();
                    } else {
                        missingCount = missingData.get(hh) == null ? intervalCnt
                                : ((Number) missingData.get(hh)).intValue();
                    }
                }
            } else {
                missingCount = missingData.get(hh) == null ? intervalCnt
                        : ((Number) missingData.get(hh)).intValue();
            }
            map.put("missingCount", missingCount);
            map.put("xField", hh);

            resultList.add(map);
        }
        Map<String, Object> resultMap = new HashMap<String, Object>();
        resultMap.put("resultList", resultList);
        return resultMap;
    }

    /*
     * 검침데이터 상세 시간별 데이터
     */
	@Deprecated
    @SuppressWarnings({"unchecked", "unused"})
    public List<Object> getDetailHourSearchData(HashMap<String, Object> condition) {

        Query query = null;
        try {

            String meterType = (String) condition.get("meterType");
            String beginDate = (String) condition.get("beginDate");
            String endDate = (String) condition.get("endDate");
            Integer meterId = (Integer) condition.get("meterId");
            Integer locationId = (Integer) condition.get("locationId");
            List<Integer> arrChannel = (List<Integer>) condition.get("arrChannel");

//            logger.info("\n====conditions====\n" + condition);
            String LpTable = MeterType.valueOf(meterType).getLpClassName();

            StringBuffer sb = new StringBuffer();

            sb.append("\n SELECT  lp.id.yyyymmddhh, lp.id.channel, ");
            sb.append("\n         CASE WHEN value_00 is not null  THEN value_00 ELSE 0 END  ");
            sb.append("\n        +CASE WHEN value_01 is not null  THEN value_01 ELSE 0 END  ");
            sb.append("\n        +CASE WHEN value_02 is not null  THEN value_02 ELSE 0 END  ");
            sb.append("\n        +CASE WHEN value_03 is not null  THEN value_03 ELSE 0 END  ");
            sb.append("\n        +CASE WHEN value_04 is not null  THEN value_04 ELSE 0 END  ");
            sb.append("\n        +CASE WHEN value_05 is not null  THEN value_05 ELSE 0 END  ");
            sb.append("\n        +CASE WHEN value_06 is not null  THEN value_06 ELSE 0 END  ");
            sb.append("\n        +CASE WHEN value_07 is not null  THEN value_07 ELSE 0 END  ");
            sb.append("\n        +CASE WHEN value_08 is not null  THEN value_08 ELSE 0 END  ");
            sb.append("\n        +CASE WHEN value_09 is not null  THEN value_09 ELSE 0 END  ");
            sb.append("\n        +CASE WHEN value_10 is not null  THEN value_10 ELSE 0 END  ");
            sb.append("\n        +CASE WHEN value_11 is not null  THEN value_11 ELSE 0 END  ");
            sb.append("\n        +CASE WHEN value_12 is not null  THEN value_12 ELSE 0 END  ");
            sb.append("\n        +CASE WHEN value_13 is not null  THEN value_13 ELSE 0 END  ");
            sb.append("\n        +CASE WHEN value_14 is not null  THEN value_14 ELSE 0 END  ");
            sb.append("\n        +CASE WHEN value_15 is not null  THEN value_15 ELSE 0 END  ");
            sb.append("\n        +CASE WHEN value_16 is not null  THEN value_16 ELSE 0 END  ");
            sb.append("\n        +CASE WHEN value_17 is not null  THEN value_17 ELSE 0 END  ");
            sb.append("\n        +CASE WHEN value_18 is not null  THEN value_18 ELSE 0 END  ");
            sb.append("\n        +CASE WHEN value_19 is not null  THEN value_19 ELSE 0 END  ");
            sb.append("\n        +CASE WHEN value_20 is not null  THEN value_20 ELSE 0 END  ");
            sb.append("\n        +CASE WHEN value_21 is not null  THEN value_21 ELSE 0 END  ");
            sb.append("\n        +CASE WHEN value_22 is not null  THEN value_22 ELSE 0 END  ");
            sb.append("\n        +CASE WHEN value_23 is not null  THEN value_23 ELSE 0 END  ");
            sb.append("\n        +CASE WHEN value_24 is not null  THEN value_24 ELSE 0 END  ");
            sb.append("\n        +CASE WHEN value_25 is not null  THEN value_25 ELSE 0 END  ");
            sb.append("\n        +CASE WHEN value_26 is not null  THEN value_26 ELSE 0 END  ");
            sb.append("\n        +CASE WHEN value_27 is not null  THEN value_27 ELSE 0 END  ");
            sb.append("\n        +CASE WHEN value_28 is not null  THEN value_28 ELSE 0 END  ");
            sb.append("\n        +CASE WHEN value_29 is not null  THEN value_29 ELSE 0 END  ");
            sb.append("\n        +CASE WHEN value_30 is not null  THEN value_30 ELSE 0 END  ");
            sb.append("\n        +CASE WHEN value_31 is not null  THEN value_31 ELSE 0 END  ");
            sb.append("\n        +CASE WHEN value_32 is not null  THEN value_32 ELSE 0 END  ");
            sb.append("\n        +CASE WHEN value_33 is not null  THEN value_33 ELSE 0 END  ");
            sb.append("\n        +CASE WHEN value_34 is not null  THEN value_34 ELSE 0 END  ");
            sb.append("\n        +CASE WHEN value_35 is not null  THEN value_35 ELSE 0 END  ");
            sb.append("\n        +CASE WHEN value_36 is not null  THEN value_36 ELSE 0 END  ");
            sb.append("\n        +CASE WHEN value_37 is not null  THEN value_37 ELSE 0 END  ");
            sb.append("\n        +CASE WHEN value_38 is not null  THEN value_38 ELSE 0 END  ");
            sb.append("\n        +CASE WHEN value_39 is not null  THEN value_39 ELSE 0 END  ");
            sb.append("\n        +CASE WHEN value_40 is not null  THEN value_40 ELSE 0 END  ");
            sb.append("\n        +CASE WHEN value_41 is not null  THEN value_41 ELSE 0 END  ");
            sb.append("\n        +CASE WHEN value_42 is not null  THEN value_42 ELSE 0 END  ");
            sb.append("\n        +CASE WHEN value_43 is not null  THEN value_43 ELSE 0 END  ");
            sb.append("\n        +CASE WHEN value_44 is not null  THEN value_44 ELSE 0 END  ");
            sb.append("\n        +CASE WHEN value_45 is not null  THEN value_45 ELSE 0 END  ");
            sb.append("\n        +CASE WHEN value_46 is not null  THEN value_46 ELSE 0 END  ");
            sb.append("\n        +CASE WHEN value_47 is not null  THEN value_47 ELSE 0 END  ");
            sb.append("\n        +CASE WHEN value_48 is not null  THEN value_48 ELSE 0 END  ");
            sb.append("\n        +CASE WHEN value_49 is not null  THEN value_49 ELSE 0 END  ");
            sb.append("\n        +CASE WHEN value_50 is not null  THEN value_50 ELSE 0 END  ");
            sb.append("\n        +CASE WHEN value_51 is not null  THEN value_51 ELSE 0 END  ");
            sb.append("\n        +CASE WHEN value_52 is not null  THEN value_52 ELSE 0 END  ");
            sb.append("\n        +CASE WHEN value_53 is not null  THEN value_53 ELSE 0 END  ");
            sb.append("\n        +CASE WHEN value_54 is not null  THEN value_54 ELSE 0 END  ");
            sb.append("\n        +CASE WHEN value_55 is not null  THEN value_55 ELSE 0 END  ");
            sb.append("\n        +CASE WHEN value_56 is not null  THEN value_56 ELSE 0 END  ");
            sb.append("\n        +CASE WHEN value_57 is not null  THEN value_57 ELSE 0 END  ");
            sb.append("\n        +CASE WHEN value_58 is not null  THEN value_58 ELSE 0 END  ");
            sb.append("\n        +CASE WHEN value_59 is not null  THEN value_59 ELSE 0 END  ");
            sb.append("\n         as value ");
            sb.append("\n FROM    ").append(LpTable).append(" lp");
            sb.append("\n WHERE   lp.meter.id = :meterId            ");
            //sb.append("\n WHERE   lp.location.id = :meterId            ");
            sb.append("\n AND     lp.id.yyyymmddhh >= :startDate          ");
            sb.append("\n AND     lp.id.yyyymmddhh <= :endDate            ");
            //sb.append("\n AND     lp.id.mdevType = :mdevType            ");

            if (arrChannel.size() > 0) {
                sb.append("\n AND      lp.id.channel IN (:channel)            ");
            }

            sb.append("\n ORDER BY lp.id.yyyymmddhh , lp.id.channel");

            query = getSession().createQuery(sb.toString());
            query.setInteger("meterId", meterId);
            query.setString("startDate", beginDate);
            query.setString("endDate", endDate);
            //query.setInteger("locationId", locationId);
            //query.setString("mdevType", CommonConstants.DeviceType.Meter.name());


            if (arrChannel.size() > 0) {
                query.setParameterList("channel", arrChannel);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return query.list();
    }

	/*
	 * 검침데이터 상세 최대/최소/평균/합계 일별 데이터
	 */
	@SuppressWarnings("unchecked")
    @Deprecated
	public List<Object> getDetailLpMaxMinAvgSumData(
			HashMap<String, Object> condition) {

		Query query = null;
		try {

			String meterType = (String) condition.get("meterType");
			String beginDate = (String) condition.get("beginDate");
			String endDate = (String) condition.get("endDate");
			Integer meterId = (Integer) condition.get("meterId");
			List<Integer> arrChannel = ((List<Integer>) condition.get("arrChannel"));

//			logger.info("\n====conditions====\n" + condition);

			String LpTable = MeterType.valueOf(meterType).getLpClassName();

			StringBuffer sb = new StringBuffer();

			sb.append("\n SELECT  lp.id.channel, ");
			sb.append("\n MIN(CASE WHEN value_00 is not null  THEN value_00 ELSE 0 END  ");
			sb.append("\n        +CASE WHEN value_01 is not null  THEN value_01 ELSE 0 END  ");
            sb.append("\n        +CASE WHEN value_02 is not null  THEN value_02 ELSE 0 END  ");
            sb.append("\n        +CASE WHEN value_03 is not null  THEN value_03 ELSE 0 END  ");
            sb.append("\n        +CASE WHEN value_04 is not null  THEN value_04 ELSE 0 END  ");
            sb.append("\n        +CASE WHEN value_05 is not null  THEN value_05 ELSE 0 END  ");
            sb.append("\n        +CASE WHEN value_06 is not null  THEN value_06 ELSE 0 END  ");
            sb.append("\n        +CASE WHEN value_07 is not null  THEN value_07 ELSE 0 END  ");
            sb.append("\n        +CASE WHEN value_08 is not null  THEN value_08 ELSE 0 END  ");
            sb.append("\n        +CASE WHEN value_09 is not null  THEN value_09 ELSE 0 END  ");
            sb.append("\n        +CASE WHEN value_10 is not null  THEN value_10 ELSE 0 END  ");
            sb.append("\n        +CASE WHEN value_11 is not null  THEN value_11 ELSE 0 END  ");
            sb.append("\n        +CASE WHEN value_12 is not null  THEN value_12 ELSE 0 END  ");
            sb.append("\n        +CASE WHEN value_13 is not null  THEN value_13 ELSE 0 END  ");
            sb.append("\n        +CASE WHEN value_14 is not null  THEN value_14 ELSE 0 END  ");
            sb.append("\n        +CASE WHEN value_15 is not null  THEN value_15 ELSE 0 END  ");
            sb.append("\n        +CASE WHEN value_16 is not null  THEN value_16 ELSE 0 END  ");
            sb.append("\n        +CASE WHEN value_17 is not null  THEN value_17 ELSE 0 END  ");
            sb.append("\n        +CASE WHEN value_18 is not null  THEN value_18 ELSE 0 END  ");
            sb.append("\n        +CASE WHEN value_19 is not null  THEN value_19 ELSE 0 END  ");
            sb.append("\n        +CASE WHEN value_20 is not null  THEN value_20 ELSE 0 END  ");
            sb.append("\n        +CASE WHEN value_21 is not null  THEN value_21 ELSE 0 END  ");
            sb.append("\n        +CASE WHEN value_22 is not null  THEN value_22 ELSE 0 END  ");
            sb.append("\n        +CASE WHEN value_23 is not null  THEN value_23 ELSE 0 END  ");
            sb.append("\n        +CASE WHEN value_24 is not null  THEN value_24 ELSE 0 END  ");
            sb.append("\n        +CASE WHEN value_25 is not null  THEN value_25 ELSE 0 END  ");
            sb.append("\n        +CASE WHEN value_26 is not null  THEN value_26 ELSE 0 END  ");
            sb.append("\n        +CASE WHEN value_27 is not null  THEN value_27 ELSE 0 END  ");
            sb.append("\n        +CASE WHEN value_28 is not null  THEN value_28 ELSE 0 END  ");
            sb.append("\n        +CASE WHEN value_29 is not null  THEN value_29 ELSE 0 END  ");
            sb.append("\n        +CASE WHEN value_30 is not null  THEN value_30 ELSE 0 END  ");
            sb.append("\n        +CASE WHEN value_31 is not null  THEN value_31 ELSE 0 END  ");
            sb.append("\n        +CASE WHEN value_32 is not null  THEN value_32 ELSE 0 END  ");
            sb.append("\n        +CASE WHEN value_33 is not null  THEN value_33 ELSE 0 END  ");
            sb.append("\n        +CASE WHEN value_34 is not null  THEN value_34 ELSE 0 END  ");
            sb.append("\n        +CASE WHEN value_35 is not null  THEN value_35 ELSE 0 END  ");
            sb.append("\n        +CASE WHEN value_36 is not null  THEN value_36 ELSE 0 END  ");
            sb.append("\n        +CASE WHEN value_37 is not null  THEN value_37 ELSE 0 END  ");
            sb.append("\n        +CASE WHEN value_38 is not null  THEN value_38 ELSE 0 END  ");
            sb.append("\n        +CASE WHEN value_39 is not null  THEN value_39 ELSE 0 END  ");
            sb.append("\n        +CASE WHEN value_40 is not null  THEN value_40 ELSE 0 END  ");
            sb.append("\n        +CASE WHEN value_41 is not null  THEN value_41 ELSE 0 END  ");
            sb.append("\n        +CASE WHEN value_42 is not null  THEN value_42 ELSE 0 END  ");
            sb.append("\n        +CASE WHEN value_43 is not null  THEN value_43 ELSE 0 END  ");
            sb.append("\n        +CASE WHEN value_44 is not null  THEN value_44 ELSE 0 END  ");
            sb.append("\n        +CASE WHEN value_45 is not null  THEN value_45 ELSE 0 END  ");
            sb.append("\n        +CASE WHEN value_46 is not null  THEN value_46 ELSE 0 END  ");
            sb.append("\n        +CASE WHEN value_47 is not null  THEN value_47 ELSE 0 END  ");
            sb.append("\n        +CASE WHEN value_48 is not null  THEN value_48 ELSE 0 END  ");
            sb.append("\n        +CASE WHEN value_49 is not null  THEN value_49 ELSE 0 END  ");
            sb.append("\n        +CASE WHEN value_50 is not null  THEN value_50 ELSE 0 END  ");
            sb.append("\n        +CASE WHEN value_51 is not null  THEN value_51 ELSE 0 END  ");
            sb.append("\n        +CASE WHEN value_52 is not null  THEN value_52 ELSE 0 END  ");
            sb.append("\n        +CASE WHEN value_53 is not null  THEN value_53 ELSE 0 END  ");
            sb.append("\n        +CASE WHEN value_54 is not null  THEN value_54 ELSE 0 END  ");
            sb.append("\n        +CASE WHEN value_55 is not null  THEN value_55 ELSE 0 END  ");
            sb.append("\n        +CASE WHEN value_56 is not null  THEN value_56 ELSE 0 END  ");
            sb.append("\n        +CASE WHEN value_57 is not null  THEN value_57 ELSE 0 END  ");
            sb.append("\n        +CASE WHEN value_58 is not null  THEN value_58 ELSE 0 END  ");
            sb.append("\n        +CASE WHEN value_59 is not null  THEN value_59 ELSE 0 END),  ");
			sb.append("\n MAX(CASE WHEN value_00 is not null  THEN value_00 ELSE 0 END  ");
			sb.append("\n        +CASE WHEN value_01 is not null  THEN value_01 ELSE 0 END  ");
            sb.append("\n        +CASE WHEN value_02 is not null  THEN value_02 ELSE 0 END  ");
            sb.append("\n        +CASE WHEN value_03 is not null  THEN value_03 ELSE 0 END  ");
            sb.append("\n        +CASE WHEN value_04 is not null  THEN value_04 ELSE 0 END  ");
            sb.append("\n        +CASE WHEN value_05 is not null  THEN value_05 ELSE 0 END  ");
            sb.append("\n        +CASE WHEN value_06 is not null  THEN value_06 ELSE 0 END  ");
            sb.append("\n        +CASE WHEN value_07 is not null  THEN value_07 ELSE 0 END  ");
            sb.append("\n        +CASE WHEN value_08 is not null  THEN value_08 ELSE 0 END  ");
            sb.append("\n        +CASE WHEN value_09 is not null  THEN value_09 ELSE 0 END  ");
            sb.append("\n        +CASE WHEN value_10 is not null  THEN value_10 ELSE 0 END  ");
            sb.append("\n        +CASE WHEN value_11 is not null  THEN value_11 ELSE 0 END  ");
            sb.append("\n        +CASE WHEN value_12 is not null  THEN value_12 ELSE 0 END  ");
            sb.append("\n        +CASE WHEN value_13 is not null  THEN value_13 ELSE 0 END  ");
            sb.append("\n        +CASE WHEN value_14 is not null  THEN value_14 ELSE 0 END  ");
            sb.append("\n        +CASE WHEN value_15 is not null  THEN value_15 ELSE 0 END  ");
            sb.append("\n        +CASE WHEN value_16 is not null  THEN value_16 ELSE 0 END  ");
            sb.append("\n        +CASE WHEN value_17 is not null  THEN value_17 ELSE 0 END  ");
            sb.append("\n        +CASE WHEN value_18 is not null  THEN value_18 ELSE 0 END  ");
            sb.append("\n        +CASE WHEN value_19 is not null  THEN value_19 ELSE 0 END  ");
            sb.append("\n        +CASE WHEN value_20 is not null  THEN value_20 ELSE 0 END  ");
            sb.append("\n        +CASE WHEN value_21 is not null  THEN value_21 ELSE 0 END  ");
            sb.append("\n        +CASE WHEN value_22 is not null  THEN value_22 ELSE 0 END  ");
            sb.append("\n        +CASE WHEN value_23 is not null  THEN value_23 ELSE 0 END  ");
            sb.append("\n        +CASE WHEN value_24 is not null  THEN value_24 ELSE 0 END  ");
            sb.append("\n        +CASE WHEN value_25 is not null  THEN value_25 ELSE 0 END  ");
            sb.append("\n        +CASE WHEN value_26 is not null  THEN value_26 ELSE 0 END  ");
            sb.append("\n        +CASE WHEN value_27 is not null  THEN value_27 ELSE 0 END  ");
            sb.append("\n        +CASE WHEN value_28 is not null  THEN value_28 ELSE 0 END  ");
            sb.append("\n        +CASE WHEN value_29 is not null  THEN value_29 ELSE 0 END  ");
            sb.append("\n        +CASE WHEN value_30 is not null  THEN value_30 ELSE 0 END  ");
            sb.append("\n        +CASE WHEN value_31 is not null  THEN value_31 ELSE 0 END  ");
            sb.append("\n        +CASE WHEN value_32 is not null  THEN value_32 ELSE 0 END  ");
            sb.append("\n        +CASE WHEN value_33 is not null  THEN value_33 ELSE 0 END  ");
            sb.append("\n        +CASE WHEN value_34 is not null  THEN value_34 ELSE 0 END  ");
            sb.append("\n        +CASE WHEN value_35 is not null  THEN value_35 ELSE 0 END  ");
            sb.append("\n        +CASE WHEN value_36 is not null  THEN value_36 ELSE 0 END  ");
            sb.append("\n        +CASE WHEN value_37 is not null  THEN value_37 ELSE 0 END  ");
            sb.append("\n        +CASE WHEN value_38 is not null  THEN value_38 ELSE 0 END  ");
            sb.append("\n        +CASE WHEN value_39 is not null  THEN value_39 ELSE 0 END  ");
            sb.append("\n        +CASE WHEN value_40 is not null  THEN value_40 ELSE 0 END  ");
            sb.append("\n        +CASE WHEN value_41 is not null  THEN value_41 ELSE 0 END  ");
            sb.append("\n        +CASE WHEN value_42 is not null  THEN value_42 ELSE 0 END  ");
            sb.append("\n        +CASE WHEN value_43 is not null  THEN value_43 ELSE 0 END  ");
            sb.append("\n        +CASE WHEN value_44 is not null  THEN value_44 ELSE 0 END  ");
            sb.append("\n        +CASE WHEN value_45 is not null  THEN value_45 ELSE 0 END  ");
            sb.append("\n        +CASE WHEN value_46 is not null  THEN value_46 ELSE 0 END  ");
            sb.append("\n        +CASE WHEN value_47 is not null  THEN value_47 ELSE 0 END  ");
            sb.append("\n        +CASE WHEN value_48 is not null  THEN value_48 ELSE 0 END  ");
            sb.append("\n        +CASE WHEN value_49 is not null  THEN value_49 ELSE 0 END  ");
            sb.append("\n        +CASE WHEN value_50 is not null  THEN value_50 ELSE 0 END  ");
            sb.append("\n        +CASE WHEN value_51 is not null  THEN value_51 ELSE 0 END  ");
            sb.append("\n        +CASE WHEN value_52 is not null  THEN value_52 ELSE 0 END  ");
            sb.append("\n        +CASE WHEN value_53 is not null  THEN value_53 ELSE 0 END  ");
            sb.append("\n        +CASE WHEN value_54 is not null  THEN value_54 ELSE 0 END  ");
            sb.append("\n        +CASE WHEN value_55 is not null  THEN value_55 ELSE 0 END  ");
            sb.append("\n        +CASE WHEN value_56 is not null  THEN value_56 ELSE 0 END  ");
            sb.append("\n        +CASE WHEN value_57 is not null  THEN value_57 ELSE 0 END  ");
            sb.append("\n        +CASE WHEN value_58 is not null  THEN value_58 ELSE 0 END  ");
            sb.append("\n        +CASE WHEN value_59 is not null  THEN value_59 ELSE 0 END),  ");
			sb.append("\n AVG(CASE WHEN value_00 is not null  THEN value_00 ELSE 0 END  ");
			sb.append("\n        +CASE WHEN value_01 is not null  THEN value_01 ELSE 0 END  ");
            sb.append("\n        +CASE WHEN value_02 is not null  THEN value_02 ELSE 0 END  ");
            sb.append("\n        +CASE WHEN value_03 is not null  THEN value_03 ELSE 0 END  ");
            sb.append("\n        +CASE WHEN value_04 is not null  THEN value_04 ELSE 0 END  ");
            sb.append("\n        +CASE WHEN value_05 is not null  THEN value_05 ELSE 0 END  ");
            sb.append("\n        +CASE WHEN value_06 is not null  THEN value_06 ELSE 0 END  ");
            sb.append("\n        +CASE WHEN value_07 is not null  THEN value_07 ELSE 0 END  ");
            sb.append("\n        +CASE WHEN value_08 is not null  THEN value_08 ELSE 0 END  ");
            sb.append("\n        +CASE WHEN value_09 is not null  THEN value_09 ELSE 0 END  ");
            sb.append("\n        +CASE WHEN value_10 is not null  THEN value_10 ELSE 0 END  ");
            sb.append("\n        +CASE WHEN value_11 is not null  THEN value_11 ELSE 0 END  ");
            sb.append("\n        +CASE WHEN value_12 is not null  THEN value_12 ELSE 0 END  ");
            sb.append("\n        +CASE WHEN value_13 is not null  THEN value_13 ELSE 0 END  ");
            sb.append("\n        +CASE WHEN value_14 is not null  THEN value_14 ELSE 0 END  ");
            sb.append("\n        +CASE WHEN value_15 is not null  THEN value_15 ELSE 0 END  ");
            sb.append("\n        +CASE WHEN value_16 is not null  THEN value_16 ELSE 0 END  ");
            sb.append("\n        +CASE WHEN value_17 is not null  THEN value_17 ELSE 0 END  ");
            sb.append("\n        +CASE WHEN value_18 is not null  THEN value_18 ELSE 0 END  ");
            sb.append("\n        +CASE WHEN value_19 is not null  THEN value_19 ELSE 0 END  ");
            sb.append("\n        +CASE WHEN value_20 is not null  THEN value_20 ELSE 0 END  ");
            sb.append("\n        +CASE WHEN value_21 is not null  THEN value_21 ELSE 0 END  ");
            sb.append("\n        +CASE WHEN value_22 is not null  THEN value_22 ELSE 0 END  ");
            sb.append("\n        +CASE WHEN value_23 is not null  THEN value_23 ELSE 0 END  ");
            sb.append("\n        +CASE WHEN value_24 is not null  THEN value_24 ELSE 0 END  ");
            sb.append("\n        +CASE WHEN value_25 is not null  THEN value_25 ELSE 0 END  ");
            sb.append("\n        +CASE WHEN value_26 is not null  THEN value_26 ELSE 0 END  ");
            sb.append("\n        +CASE WHEN value_27 is not null  THEN value_27 ELSE 0 END  ");
            sb.append("\n        +CASE WHEN value_28 is not null  THEN value_28 ELSE 0 END  ");
            sb.append("\n        +CASE WHEN value_29 is not null  THEN value_29 ELSE 0 END  ");
            sb.append("\n        +CASE WHEN value_30 is not null  THEN value_30 ELSE 0 END  ");
            sb.append("\n        +CASE WHEN value_31 is not null  THEN value_31 ELSE 0 END  ");
            sb.append("\n        +CASE WHEN value_32 is not null  THEN value_32 ELSE 0 END  ");
            sb.append("\n        +CASE WHEN value_33 is not null  THEN value_33 ELSE 0 END  ");
            sb.append("\n        +CASE WHEN value_34 is not null  THEN value_34 ELSE 0 END  ");
            sb.append("\n        +CASE WHEN value_35 is not null  THEN value_35 ELSE 0 END  ");
            sb.append("\n        +CASE WHEN value_36 is not null  THEN value_36 ELSE 0 END  ");
            sb.append("\n        +CASE WHEN value_37 is not null  THEN value_37 ELSE 0 END  ");
            sb.append("\n        +CASE WHEN value_38 is not null  THEN value_38 ELSE 0 END  ");
            sb.append("\n        +CASE WHEN value_39 is not null  THEN value_39 ELSE 0 END  ");
            sb.append("\n        +CASE WHEN value_40 is not null  THEN value_40 ELSE 0 END  ");
            sb.append("\n        +CASE WHEN value_41 is not null  THEN value_41 ELSE 0 END  ");
            sb.append("\n        +CASE WHEN value_42 is not null  THEN value_42 ELSE 0 END  ");
            sb.append("\n        +CASE WHEN value_43 is not null  THEN value_43 ELSE 0 END  ");
            sb.append("\n        +CASE WHEN value_44 is not null  THEN value_44 ELSE 0 END  ");
            sb.append("\n        +CASE WHEN value_45 is not null  THEN value_45 ELSE 0 END  ");
            sb.append("\n        +CASE WHEN value_46 is not null  THEN value_46 ELSE 0 END  ");
            sb.append("\n        +CASE WHEN value_47 is not null  THEN value_47 ELSE 0 END  ");
            sb.append("\n        +CASE WHEN value_48 is not null  THEN value_48 ELSE 0 END  ");
            sb.append("\n        +CASE WHEN value_49 is not null  THEN value_49 ELSE 0 END  ");
            sb.append("\n        +CASE WHEN value_50 is not null  THEN value_50 ELSE 0 END  ");
            sb.append("\n        +CASE WHEN value_51 is not null  THEN value_51 ELSE 0 END  ");
            sb.append("\n        +CASE WHEN value_52 is not null  THEN value_52 ELSE 0 END  ");
            sb.append("\n        +CASE WHEN value_53 is not null  THEN value_53 ELSE 0 END  ");
            sb.append("\n        +CASE WHEN value_54 is not null  THEN value_54 ELSE 0 END  ");
            sb.append("\n        +CASE WHEN value_55 is not null  THEN value_55 ELSE 0 END  ");
            sb.append("\n        +CASE WHEN value_56 is not null  THEN value_56 ELSE 0 END  ");
            sb.append("\n        +CASE WHEN value_57 is not null  THEN value_57 ELSE 0 END  ");
            sb.append("\n        +CASE WHEN value_58 is not null  THEN value_58 ELSE 0 END  ");
            sb.append("\n        +CASE WHEN value_59 is not null  THEN value_59 ELSE 0 END),  ");
			sb.append("\n SUM(CASE WHEN value_00 is not null  THEN value_00 ELSE 0 END  ");
			sb.append("\n        +CASE WHEN value_01 is not null  THEN value_01 ELSE 0 END  ");
            sb.append("\n        +CASE WHEN value_02 is not null  THEN value_02 ELSE 0 END  ");
            sb.append("\n        +CASE WHEN value_03 is not null  THEN value_03 ELSE 0 END  ");
            sb.append("\n        +CASE WHEN value_04 is not null  THEN value_04 ELSE 0 END  ");
            sb.append("\n        +CASE WHEN value_05 is not null  THEN value_05 ELSE 0 END  ");
            sb.append("\n        +CASE WHEN value_06 is not null  THEN value_06 ELSE 0 END  ");
            sb.append("\n        +CASE WHEN value_07 is not null  THEN value_07 ELSE 0 END  ");
            sb.append("\n        +CASE WHEN value_08 is not null  THEN value_08 ELSE 0 END  ");
            sb.append("\n        +CASE WHEN value_09 is not null  THEN value_09 ELSE 0 END  ");
            sb.append("\n        +CASE WHEN value_10 is not null  THEN value_10 ELSE 0 END  ");
            sb.append("\n        +CASE WHEN value_11 is not null  THEN value_11 ELSE 0 END  ");
            sb.append("\n        +CASE WHEN value_12 is not null  THEN value_12 ELSE 0 END  ");
            sb.append("\n        +CASE WHEN value_13 is not null  THEN value_13 ELSE 0 END  ");
            sb.append("\n        +CASE WHEN value_14 is not null  THEN value_14 ELSE 0 END  ");
            sb.append("\n        +CASE WHEN value_15 is not null  THEN value_15 ELSE 0 END  ");
            sb.append("\n        +CASE WHEN value_16 is not null  THEN value_16 ELSE 0 END  ");
            sb.append("\n        +CASE WHEN value_17 is not null  THEN value_17 ELSE 0 END  ");
            sb.append("\n        +CASE WHEN value_18 is not null  THEN value_18 ELSE 0 END  ");
            sb.append("\n        +CASE WHEN value_19 is not null  THEN value_19 ELSE 0 END  ");
            sb.append("\n        +CASE WHEN value_20 is not null  THEN value_20 ELSE 0 END  ");
            sb.append("\n        +CASE WHEN value_21 is not null  THEN value_21 ELSE 0 END  ");
            sb.append("\n        +CASE WHEN value_22 is not null  THEN value_22 ELSE 0 END  ");
            sb.append("\n        +CASE WHEN value_23 is not null  THEN value_23 ELSE 0 END  ");
            sb.append("\n        +CASE WHEN value_24 is not null  THEN value_24 ELSE 0 END  ");
            sb.append("\n        +CASE WHEN value_25 is not null  THEN value_25 ELSE 0 END  ");
            sb.append("\n        +CASE WHEN value_26 is not null  THEN value_26 ELSE 0 END  ");
            sb.append("\n        +CASE WHEN value_27 is not null  THEN value_27 ELSE 0 END  ");
            sb.append("\n        +CASE WHEN value_28 is not null  THEN value_28 ELSE 0 END  ");
            sb.append("\n        +CASE WHEN value_29 is not null  THEN value_29 ELSE 0 END  ");
            sb.append("\n        +CASE WHEN value_30 is not null  THEN value_30 ELSE 0 END  ");
            sb.append("\n        +CASE WHEN value_31 is not null  THEN value_31 ELSE 0 END  ");
            sb.append("\n        +CASE WHEN value_32 is not null  THEN value_32 ELSE 0 END  ");
            sb.append("\n        +CASE WHEN value_33 is not null  THEN value_33 ELSE 0 END  ");
            sb.append("\n        +CASE WHEN value_34 is not null  THEN value_34 ELSE 0 END  ");
            sb.append("\n        +CASE WHEN value_35 is not null  THEN value_35 ELSE 0 END  ");
            sb.append("\n        +CASE WHEN value_36 is not null  THEN value_36 ELSE 0 END  ");
            sb.append("\n        +CASE WHEN value_37 is not null  THEN value_37 ELSE 0 END  ");
            sb.append("\n        +CASE WHEN value_38 is not null  THEN value_38 ELSE 0 END  ");
            sb.append("\n        +CASE WHEN value_39 is not null  THEN value_39 ELSE 0 END  ");
            sb.append("\n        +CASE WHEN value_40 is not null  THEN value_40 ELSE 0 END  ");
            sb.append("\n        +CASE WHEN value_41 is not null  THEN value_41 ELSE 0 END  ");
            sb.append("\n        +CASE WHEN value_42 is not null  THEN value_42 ELSE 0 END  ");
            sb.append("\n        +CASE WHEN value_43 is not null  THEN value_43 ELSE 0 END  ");
            sb.append("\n        +CASE WHEN value_44 is not null  THEN value_44 ELSE 0 END  ");
            sb.append("\n        +CASE WHEN value_45 is not null  THEN value_45 ELSE 0 END  ");
            sb.append("\n        +CASE WHEN value_46 is not null  THEN value_46 ELSE 0 END  ");
            sb.append("\n        +CASE WHEN value_47 is not null  THEN value_47 ELSE 0 END  ");
            sb.append("\n        +CASE WHEN value_48 is not null  THEN value_48 ELSE 0 END  ");
            sb.append("\n        +CASE WHEN value_49 is not null  THEN value_49 ELSE 0 END  ");
            sb.append("\n        +CASE WHEN value_50 is not null  THEN value_50 ELSE 0 END  ");
            sb.append("\n        +CASE WHEN value_51 is not null  THEN value_51 ELSE 0 END  ");
            sb.append("\n        +CASE WHEN value_52 is not null  THEN value_52 ELSE 0 END  ");
            sb.append("\n        +CASE WHEN value_53 is not null  THEN value_53 ELSE 0 END  ");
            sb.append("\n        +CASE WHEN value_54 is not null  THEN value_54 ELSE 0 END  ");
            sb.append("\n        +CASE WHEN value_55 is not null  THEN value_55 ELSE 0 END  ");
            sb.append("\n        +CASE WHEN value_56 is not null  THEN value_56 ELSE 0 END  ");
            sb.append("\n        +CASE WHEN value_57 is not null  THEN value_57 ELSE 0 END  ");
            sb.append("\n        +CASE WHEN value_58 is not null  THEN value_58 ELSE 0 END  ");
            sb.append("\n        +CASE WHEN value_59 is not null  THEN value_59 ELSE 0 END)  ");
			sb.append("\n FROM    ").append(LpTable).append(" lp");
			sb.append("\n WHERE   lp.meter.id = :meterId            ");
			sb.append("\n AND     lp.id.yyyymmddhh >= :startDate          ");
			sb.append("\n AND     lp.id.yyyymmddhh <= :endDate            ");
			sb.append("\n AND     lp.id.mdevType = :mdevType            ");
			if (arrChannel.size() > 0) {
				sb
						.append("\n AND      lp.id.channel IN (:channel)            ");
			}
			sb.append("\n GROUP BY   lp.id.channel            ");
			sb.append("\n ORDER BY   lp.id.channel            ");

			query = getSession().createQuery(sb.toString());
			query.setString("startDate", beginDate);
			query.setString("endDate", endDate);
			query.setInteger("meterId", meterId);
			query
					.setString("mdevType", CommonConstants.DeviceType.Meter
							.name());

			if (arrChannel.size() > 0) {
				query
						.setParameterList("channel", arrChannel);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return query.list();
	}

    /*
     * 검침데이터 상세 시간별 데이터
     */
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> getDetailHourlyLPData(Map<String, Object> condition, boolean isSum) {

        SQLQuery query = null;
        try {

            String meterType = (String) condition.get("meterType");
            String beginDate = (String) condition.get("beginDate");
            String endDate = (String) condition.get("endDate");
            Integer meterId = (Integer) condition.get("meterId");
            Integer interval = (Integer) condition.get("interval");
            List<Integer> arrChannel = (List<Integer>) condition.get("arrChannel");
            List<Map<String, Object>> channelMethods = (List<Map<String, Object>>) condition.get("channelMethods");

            String LpTable = MeterType.valueOf(meterType).getLpTableName();

            int cnt = 0;
            ChannelCalcMethod chMethod = null;
            StringBuilder sb = new StringBuilder();

            if (isSum) {
                sb.append("\nSELECT ");

                if (arrChannel != null && arrChannel.size() > 0) {

                    for (Integer channel : arrChannel) {
                        sb.append("\n      MAX(CHANNEL_").append(channel).append(") AS MAX_CHANNEL_").append(channel).append(", ");
                        sb.append("\n      MIN(CHANNEL_").append(channel).append(") AS MIN_CHANNEL_").append(channel).append(", ");
                        sb.append("\n      AVG(CHANNEL_").append(channel).append(") AS AVG_CHANNEL_").append(channel).append(", ");
                        sb.append("\n      SUM(CHANNEL_").append(channel).append(") AS SUM_CHANNEL_").append(channel);

                        if (cnt != (arrChannel.size()-1)) {
                            sb.append(", ");
                        } else {
                            sb.append(" ");
                        }
                        cnt++;
                    }
                }

                sb.append("\nFROM ( ");
            }

            sb.append("\nSELECT lp.yyyymmddhh AS YYYYMMDDHH ");

            if (arrChannel != null && arrChannel.size() > 0) {
                for (Integer channel : arrChannel) {
                    chMethod = null;
                    if (channelMethods == null || channelMethods.size() == 0) {
                        chMethod = ChannelCalcMethod.SUM;
                    } else {
                        for (Map<String, Object> obj : channelMethods) {
                            if (channel.equals(DecimalUtil.ConvertNumberToInteger(obj.get("channelId")))) {
                                chMethod = (ChannelCalcMethod)obj.get("chMethod");
                                break;
                            }
                        }
                    }

                    sb.append("\n      ,SUM(CASE WHEN lp.channel = ").append(channel).append(" THEN ");

                    if (chMethod.equals(ChannelCalcMethod.AVG)) {
                        sb.append("(");
                    }

                    for (int i = 0, j = 0 ; j < 60 ; i++, j = i * interval) {
                        if (i != 0) {
                            sb.append("+ ");
                        }
                        sb.append("COALESCE(lp.value_").append(StringUtil.frontAppendNStr('0', Integer.toString(j), 2)).append(", 0) ");
                    }

                    if (chMethod.equals(ChannelCalcMethod.AVG)) {
//                        sb.append(") / " + intervalCnt + " ");
                        sb.append(") / (");
                        
                        for (int i = 0, j = 0 ; j < 60 ; i++, j = i * interval) {
                            if (i != 0) {
                                sb.append("+ ");
                            }
                            sb.append("CASE WHEN lp.value_").append(StringUtil.frontAppendNStr('0', Integer.toString(j), 2)).append(" IS NOT NULL THEN 1 ELSE 0 END ");
                        }
                        
                        sb.append(") ");
                        
                    }

                    sb.append("\n           ELSE 0 END ) AS CHANNEL_").append(channel).append(" ");
                }
            }

            sb.append("\nFROM ").append(LpTable).append(" lp ");
            sb.append("\n     ,meter mt ");
            sb.append("\nWHERE mt.id = :meterId ");
            sb.append("\nAND   lp.meter_id = mt.id ");
            sb.append("\nAND   lp.yyyymmddhh BETWEEN :startDate AND :endDate ");

            if (arrChannel.size() > 0) {
                sb.append("\nAND   lp.channel IN (:channel) ");
            }

            sb.append("\nGROUP BY lp.yyyymmddhh ");

            if (isSum) {
                sb.append("\n) tbl");
            }

            query = getSession().createSQLQuery(new SQLWrapper().getQuery(sb.toString()));
            query.setInteger("meterId", meterId);
            query.setString("startDate", beginDate);
            query.setString("endDate", endDate);
            //query.setString("mdevType", CommonConstants.DeviceType.Meter.name());

            if (arrChannel.size() > 0) {
                query.setParameterList("channel", arrChannel, StandardBasicTypes.INTEGER);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
    }

    /*
     * 검침데이터 상세 시간의 Interval 별 데이터
     */
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> getDetailHourlyLPIntervalData(Map<String, Object> condition, String searchDate) {

        SQLQuery query = null;
        try {

            String meterType = (String) condition.get("meterType");
            Integer meterId = (Integer) condition.get("meterId");
            Integer interval = (Integer) condition.get("interval");
            List<Integer> arrChannel = (condition.get("arrChannel") != null) ? (List<Integer>) condition.get("arrChannel") : new ArrayList<Integer>();

//            logger.info("\n====conditions====\n" + condition);
            String LpTable = MeterType.valueOf(meterType).getLpTableName();

            StringBuilder sb = new StringBuilder();

            sb.append("\nSELECT YYYYMMDDHHMM ");

            for (Integer channel : arrChannel) {
                sb.append("\n      ,CHANNEL_").append(channel).append(" ");
            }

            sb.append("\nFROM ( ");

            for (int i = 0, j = 0 ; j < 60 ; i++, j = i * interval) {
                if (i != 0) {
                    sb.append("\n    UNION ALL ");
                }

                sb.append("\n    SELECT lp.yyyymmddhh CONCAT '").append(StringUtil.frontAppendNStr('0', Integer.toString(j), 2)).append("00' AS YYYYMMDDHHMM ");

                for (Integer channel : arrChannel) {
                    sb.append("\n          ,SUM(CASE WHEN lp.channel = ").append(channel);
                    sb.append("\n                    THEN COALESCE(lp.value_").append(StringUtil.frontAppendNStr('0', Integer.toString(j), 2)).append(", 0) ");
                    sb.append("\n                    ELSE 0 END ) AS CHANNEL_").append(channel).append(" ");
                }

                sb.append("\n    FROM ").append(LpTable).append(" lp, ");
                sb.append("\n         meter mt ");
                sb.append("\n    WHERE lp.meter_id = mt.id ");
                sb.append("\n    AND   lp.meter_id = :meterId ");
                sb.append("\n    AND   lp.YYYYMMDDHH = :searchDate ");
                //sb.append("\n    AND   lp.mdev_type = :mdevType ");

                if (arrChannel.size() > 0) {
                    sb.append("\n    AND   lp.channel IN (:channel) ");
                }

                sb.append("\n    GROUP BY lp.yyyymmddhh ");
            }

            sb.append("\n) tbl ");

            query = getSession().createSQLQuery(new SQLWrapper().getQuery(sb.toString()));

            query.setInteger("meterId", meterId);
            query.setString("searchDate", searchDate);
            //query.setString("mdevType", CommonConstants.DeviceType.Meter.name());

            if (arrChannel.size() > 0) {
                query.setParameterList("channel", arrChannel, StandardBasicTypes.INTEGER);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
    }

////////////////////////////////////////////////////////////////////////////////

	/*
	 * 검침데이터 시간대에 따른 미터별 데이타(azbil 연동시 사용)
	 */
	@SuppressWarnings("unchecked")
    public List<Object> getLpByMeter(String lpTableName) {

		Query query = null;
		try {

			StringBuffer sb = new StringBuffer();
			sb
					.append("\n SELECT code.install_property,l.yyyymmddhh,l.mdev_id,l.value,l.value_00,value_15,value_30,value_45");
			sb
					.append("\n from "
							+ lpTableName
							+ " l inner join (SELECT  max(yyyymmddhh) as yyyymmddhh,mdev_id,install_property");
			sb.append("\n FROM    " + lpTableName + " lp");
			sb.append("\n INNER JOIN meter m ON lp.mdev_id = m.mds_id");
			sb.append("\n WHERE     lp.dst = 0");
			sb.append("\n AND     lp.channel = 1");
			sb.append("\n AND     lp.mdev_type = '"
					+ CommonConstants.DeviceType.Meter.name() + "'");
			sb.append("\n group by mdev_id,install_property) code");
			sb.append("\n on l.yyyymmddhh = code.yyyymmddhh");
			sb.append("\n where l.dst = 0");
			sb.append("\n and l.mdev_id = code.mdev_id");
			sb.append("\n AND     l.channel = 1");
			sb.append("\n AND     l.mdev_type = '"
					+ CommonConstants.DeviceType.Meter.name() + "'");

//			 logger.info("sb.toString()="+sb.toString());
			query = getSession().createSQLQuery(sb.toString());

		} catch (Exception e) {
			e.printStackTrace();
		}

		return query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP)
				.list();
	}

	/*
	 * 검침데이터 시간대에 따른 미터별 데이타(azbil 연동시 사용)
	 */
	@SuppressWarnings("unchecked")
    public List<Object> getLpByModem(String lpTableName) {
		Query query = null;
		try {

			StringBuffer sb = new StringBuffer();
			sb
					.append("\n SELECT code.installed_site_img,l.yyyymmddhh,l.value,l.value_00,value_15,value_30,value_45");
			sb
					.append("\n from "
							+ lpTableName
							+ " l inner join (SELECT  max(yyyymmddhh) as yyyymmddhh,mdev_id,installed_site_img");
			sb.append("\n FROM  " + lpTableName + " lp");
			sb.append("\n INNER JOIN modem m ON lp.mdev_id = m.device_serial");
			sb.append("\n  WHERE     lp.dst = 0");
			sb.append("\n  AND     lp.channel = 1");
			sb.append("\n  AND     lp.mdev_type = '"
					+ CommonConstants.DeviceType.Modem.name() + "'");
			sb.append("\n  group by mdev_id,installed_site_img) code");
			sb.append("\n  on l.yyyymmddhh = code.yyyymmddhh");
			sb.append("\n  where l.dst = 0");
			sb.append("\n  and l.mdev_id = code.mdev_id");
			sb.append("\n  AND     l.channel = 1");
			sb.append("\n  AND     l.mdev_type = '"
					+ CommonConstants.DeviceType.Modem.name() + "'");

			// logger.info("sb.toString()="+sb.toString());
			query = getSession().createSQLQuery(sb.toString());

		} catch (Exception e) {
			e.printStackTrace();
		}

		return query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP)
				.list();
	}

	/*
	 * 검침데이터 시간대에 따른 미터별 데이타(azbil 연동시 사용)
	 */
	@SuppressWarnings("unchecked")
    public List<Object> getTimeLpByMeter(String lpTableName, String yyyymmddhh) {

		Query query = null;
		try {

			StringBuffer sb = new StringBuffer();

			sb.append("\n select yyyymmddhh AS YYYYMMDDHH ,install_property AS INSTALL_PROPERTY, mdev_id AS MDEV_ID,  ");
			sb.append("\n 		 value AS VALUE, value_00 AS VALUE_00, value_15 AS VALUE_15, value_30 AS VALUE_30, value_45 AS VALUE_45 ");
			sb.append("\n from " + lpTableName+ " lp inner join meter m on lp.mdev_id = m.mds_id ");
			sb.append("\n where lp.yyyymmddhh ='" + yyyymmddhh
					+ "' and lp.channel=1 and lp.dst=0");
			// logger.info("sb.toString()="+sb.toString());
			query = getSession().createSQLQuery(sb.toString());

		} catch (Exception e) {
			e.printStackTrace();
		}

		return query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP)
				.list();
	}

	/*
	 * 검침데이터 시간대에 따른 미터별 데이타(azbil 연동시 사용)
	 */
	@SuppressWarnings("unchecked")
    public List<Object> getTimeLpByModem(String lpTableName, String yyyymmddhh) {
		Query query = null;
		try {

			StringBuffer sb = new StringBuffer();

			sb.append("\n select yyyymmddhh AS YYYYMMDDHH, installed_site_img AS INSTALL_SITE_IMG, mdev_id AS MDEV_ID, ");
			sb.append("\n 		 value AS VALUE, value_00 AS VALUE_00, value_15 AS VALUE_15, value_30 AS VALUE_30, value_45 AS VALUE_45 ");
			sb.append("\n from "+lpTableName+" lp inner join modem m on lp.mdev_id = m.device_serial ");
			sb.append("\n where lp.yyyymmddhh ='"+yyyymmddhh+"' and lp.channel=1 and lp.dst=0");

			// logger.info("sb.toString()="+sb.toString());
			query = getSession().createSQLQuery(sb.toString());

		} catch (Exception e) {
			e.printStackTrace();
		}

		return query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP)
				.list();
	}

	/*
	 * 검침데이터 시간대에 따른 미터별 데이타(azbil 연동시 사용)
	 */
	@SuppressWarnings("unchecked")
    public List<Object> getTimeLpValue(String lpTableName,String mdevId, String yyyymmddhh) {

		Query query = null;
		try {

			StringBuffer sb = new StringBuffer();

			sb.append("\n select yyyymmddhh,mdev_id,value,value_00,value_15,value_30,value_45 ");
			sb.append("\n from "+lpTableName+" ");
			sb.append("\n where mdev_id='"+mdevId+"' and yyyymmddhh='"+yyyymmddhh+"' and channel=1");

			query = getSession().createSQLQuery(sb.toString());

		} catch (Exception e) {
			e.printStackTrace();
		}
		List<Object> result =query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP)
		.list();
//		logger.info("getTimeLpValue:"+result);
		return result;
	}

	/*
	 * 검침데이터 시간대에 따른 미터별 데이타(azbil 연동시 사용)
	 */
	public void insertAzbilLog(String createDate,String time, String name,Integer value,Integer status) {


		try {

			StringBuffer sb = new StringBuffer();

			sb.append("\n INSERT INTO POINT (CREATE_DATE,TIMEVALUE,NAME,VALUE,STATUS) VALUES ");
			sb.append("\n ('"+createDate+"','"+time+"','"+name+"',"+value+","+status+"') ");
			getSession().createSQLQuery(sb.toString());

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

    /*
     * 검침데이터 상세 시간별 데이터
     */
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> getDetailHourData4fc(Map<String, Object> condition, boolean isSum) {

        SQLQuery query = null;
        try {

            String meterType = (String) condition.get("meterType");
            String beginDate = (String) condition.get("beginDate");
            String endDate = (String) condition.get("endDate");
            String tlbType = (String)condition.get("tlbType");
            Integer meterId = (Integer) condition.get("meterId");
//            Integer locationId = (Integer) condition.get("locationId");
            List<Integer> arrChannel = (List<Integer>) condition.get("arrChannel");

            String LpTable = MeterType.valueOf(meterType).getLpTableName();

            StringBuffer sb = new StringBuffer();

            if (isSum) {
                sb.append("\nSELECT CHANNEL AS CHANNEL, ");
                sb.append("\n       MIN(VALUE) AS MIN_VAL, ");
                sb.append("\n       MAX(VALUE) AS MAX_VAL, ");
                sb.append("\n       AVG(VALUE) AS AVG_VAL, ");
                sb.append("\n       SUM(VALUE) AS SUM_VAL ");
                sb.append("\nFROM ( ");
            }

            sb.append("\nSELECT yyyymmddhh AS YYYYMMDDHH, ");
            sb.append("\n       channel AS CHANNEL, ");
            sb.append("\n       CASE WHEN ch_method = 'SUM' OR total = 0 THEN total ");
            sb.append("\n            WHEN value_cnt = 0 THEN NULL ");
            sb.append("\n            ELSE (total / value_cnt) END AS VALUE ");
            sb.append("\nFROM ( ");
            sb.append("\n    SELECT lp.yyyymmddhh, lp.channel, ");
            sb.append("\n           COALESCE(value_00, 0) + COALESCE(value_01, 0) + COALESCE(value_02, 0) ");
            sb.append("\n           + COALESCE(value_03, 0) + COALESCE(value_04, 0) + COALESCE(value_05, 0) ");
            sb.append("\n           + COALESCE(value_06, 0) + COALESCE(value_07, 0) + COALESCE(value_08, 0) ");
            sb.append("\n           + COALESCE(value_09, 0) + COALESCE(value_10, 0) + COALESCE(value_11, 0) ");
            sb.append("\n           + COALESCE(value_12, 0) + COALESCE(value_13, 0) + COALESCE(value_14, 0) ");
            sb.append("\n           + COALESCE(value_15, 0) + COALESCE(value_16, 0) + COALESCE(value_17, 0) ");
            sb.append("\n           + COALESCE(value_18, 0) + COALESCE(value_19, 0) + COALESCE(value_20, 0) ");
            sb.append("\n           + COALESCE(value_21, 0) + COALESCE(value_22, 0) + COALESCE(value_23, 0) ");
            sb.append("\n           + COALESCE(value_24, 0) + COALESCE(value_25, 0) + COALESCE(value_26, 0) ");
            sb.append("\n           + COALESCE(value_27, 0) + COALESCE(value_28, 0) + COALESCE(value_29, 0) ");
            sb.append("\n           + COALESCE(value_30, 0) + COALESCE(value_31, 0) + COALESCE(value_32, 0) ");
            sb.append("\n           + COALESCE(value_33, 0) + COALESCE(value_34, 0) + COALESCE(value_35, 0) ");
            sb.append("\n           + COALESCE(value_36, 0) + COALESCE(value_37, 0) + COALESCE(value_38, 0) ");
            sb.append("\n           + COALESCE(value_39, 0) + COALESCE(value_40, 0) + COALESCE(value_41, 0) ");
            sb.append("\n           + COALESCE(value_42, 0) + COALESCE(value_43, 0) + COALESCE(value_44, 0) ");
            sb.append("\n           + COALESCE(value_45, 0) + COALESCE(value_46, 0) + COALESCE(value_47, 0) ");
            sb.append("\n           + COALESCE(value_48, 0) + COALESCE(value_49, 0) + COALESCE(value_50, 0) ");
            sb.append("\n           + COALESCE(value_51, 0) + COALESCE(value_52, 0) + COALESCE(value_53, 0) ");
            sb.append("\n           + COALESCE(value_54, 0) + COALESCE(value_55, 0) + COALESCE(value_56, 0) ");
            sb.append("\n           + COALESCE(value_57, 0) + COALESCE(value_58, 0) + COALESCE(value_59, 0) AS total, ");
            sb.append("\n           CASE WHEN value_00 IS NOT NULL THEN 1 ELSE 0 END ");
            sb.append("\n           + CASE WHEN value_01 IS NOT NULL THEN 1 ELSE 0 END ");
            sb.append("\n           + CASE WHEN value_02 IS NOT NULL THEN 1 ELSE 0 END ");
            sb.append("\n           + CASE WHEN value_03 IS NOT NULL THEN 1 ELSE 0 END ");
            sb.append("\n           + CASE WHEN value_04 IS NOT NULL THEN 1 ELSE 0 END ");
            sb.append("\n           + CASE WHEN value_05 IS NOT NULL THEN 1 ELSE 0 END ");
            sb.append("\n           + CASE WHEN value_06 IS NOT NULL THEN 1 ELSE 0 END ");
            sb.append("\n           + CASE WHEN value_07 IS NOT NULL THEN 1 ELSE 0 END ");
            sb.append("\n           + CASE WHEN value_08 IS NOT NULL THEN 1 ELSE 0 END ");
            sb.append("\n           + CASE WHEN value_09 IS NOT NULL THEN 1 ELSE 0 END ");
            sb.append("\n           + CASE WHEN value_10 IS NOT NULL THEN 1 ELSE 0 END ");
            sb.append("\n           + CASE WHEN value_11 IS NOT NULL THEN 1 ELSE 0 END ");
            sb.append("\n           + CASE WHEN value_12 IS NOT NULL THEN 1 ELSE 0 END ");
            sb.append("\n           + CASE WHEN value_13 IS NOT NULL THEN 1 ELSE 0 END ");
            sb.append("\n           + CASE WHEN value_14 IS NOT NULL THEN 1 ELSE 0 END ");
            sb.append("\n           + CASE WHEN value_15 IS NOT NULL THEN 1 ELSE 0 END ");
            sb.append("\n           + CASE WHEN value_16 IS NOT NULL THEN 1 ELSE 0 END ");
            sb.append("\n           + CASE WHEN value_17 IS NOT NULL THEN 1 ELSE 0 END ");
            sb.append("\n           + CASE WHEN value_18 IS NOT NULL THEN 1 ELSE 0 END ");
            sb.append("\n           + CASE WHEN value_19 IS NOT NULL THEN 1 ELSE 0 END ");
            sb.append("\n           + CASE WHEN value_20 IS NOT NULL THEN 1 ELSE 0 END ");
            sb.append("\n           + CASE WHEN value_21 IS NOT NULL THEN 1 ELSE 0 END ");
            sb.append("\n           + CASE WHEN value_22 IS NOT NULL THEN 1 ELSE 0 END ");
            sb.append("\n           + CASE WHEN value_23 IS NOT NULL THEN 1 ELSE 0 END ");
            sb.append("\n           + CASE WHEN value_24 IS NOT NULL THEN 1 ELSE 0 END ");
            sb.append("\n           + CASE WHEN value_25 IS NOT NULL THEN 1 ELSE 0 END ");
            sb.append("\n           + CASE WHEN value_26 IS NOT NULL THEN 1 ELSE 0 END ");
            sb.append("\n           + CASE WHEN value_27 IS NOT NULL THEN 1 ELSE 0 END ");
            sb.append("\n           + CASE WHEN value_28 IS NOT NULL THEN 1 ELSE 0 END ");
            sb.append("\n           + CASE WHEN value_29 IS NOT NULL THEN 1 ELSE 0 END ");
            sb.append("\n           + CASE WHEN value_30 IS NOT NULL THEN 1 ELSE 0 END ");
            sb.append("\n           + CASE WHEN value_31 IS NOT NULL THEN 1 ELSE 0 END ");
            sb.append("\n           + CASE WHEN value_32 IS NOT NULL THEN 1 ELSE 0 END ");
            sb.append("\n           + CASE WHEN value_33 IS NOT NULL THEN 1 ELSE 0 END ");
            sb.append("\n           + CASE WHEN value_34 IS NOT NULL THEN 1 ELSE 0 END ");
            sb.append("\n           + CASE WHEN value_35 IS NOT NULL THEN 1 ELSE 0 END ");
            sb.append("\n           + CASE WHEN value_36 IS NOT NULL THEN 1 ELSE 0 END ");
            sb.append("\n           + CASE WHEN value_37 IS NOT NULL THEN 1 ELSE 0 END ");
            sb.append("\n           + CASE WHEN value_38 IS NOT NULL THEN 1 ELSE 0 END ");
            sb.append("\n           + CASE WHEN value_39 IS NOT NULL THEN 1 ELSE 0 END ");
            sb.append("\n           + CASE WHEN value_40 IS NOT NULL THEN 1 ELSE 0 END ");
            sb.append("\n           + CASE WHEN value_41 IS NOT NULL THEN 1 ELSE 0 END ");
            sb.append("\n           + CASE WHEN value_42 IS NOT NULL THEN 1 ELSE 0 END ");
            sb.append("\n           + CASE WHEN value_43 IS NOT NULL THEN 1 ELSE 0 END ");
            sb.append("\n           + CASE WHEN value_44 IS NOT NULL THEN 1 ELSE 0 END ");
            sb.append("\n           + CASE WHEN value_45 IS NOT NULL THEN 1 ELSE 0 END ");
            sb.append("\n           + CASE WHEN value_46 IS NOT NULL THEN 1 ELSE 0 END ");
            sb.append("\n           + CASE WHEN value_47 IS NOT NULL THEN 1 ELSE 0 END ");
            sb.append("\n           + CASE WHEN value_48 IS NOT NULL THEN 1 ELSE 0 END ");
            sb.append("\n           + CASE WHEN value_49 IS NOT NULL THEN 1 ELSE 0 END ");
            sb.append("\n           + CASE WHEN value_50 IS NOT NULL THEN 1 ELSE 0 END ");
            sb.append("\n           + CASE WHEN value_51 IS NOT NULL THEN 1 ELSE 0 END ");
            sb.append("\n           + CASE WHEN value_52 IS NOT NULL THEN 1 ELSE 0 END ");
            sb.append("\n           + CASE WHEN value_53 IS NOT NULL THEN 1 ELSE 0 END ");
            sb.append("\n           + CASE WHEN value_54 IS NOT NULL THEN 1 ELSE 0 END ");
            sb.append("\n           + CASE WHEN value_55 IS NOT NULL THEN 1 ELSE 0 END ");
            sb.append("\n           + CASE WHEN value_56 IS NOT NULL THEN 1 ELSE 0 END ");
            sb.append("\n           + CASE WHEN value_57 IS NOT NULL THEN 1 ELSE 0 END ");
            sb.append("\n           + CASE WHEN value_58 IS NOT NULL THEN 1 ELSE 0 END ");
            sb.append("\n           + CASE WHEN value_59 IS NOT NULL THEN 1 ELSE 0 END ");
            sb.append("\n           AS value_cnt, ");
            sb.append("\n           CASE WHEN ch.ch_method IS NULL THEN 'SUM' ELSE ch.ch_method END AS ch_method ");
            sb.append("\n    FROM ").append(LpTable).append(" lp ");
            sb.append("\n         LEFT OUTER JOIN ");
            sb.append("\n         (SELECT DISTINCT cc.channel_Index, dc.ch_method ");
            sb.append("\n          FROM meter mt, ");
            sb.append("\n               meterconfig mc, ");
            sb.append("\n               display_channel dc, ");
            sb.append("\n               channel_config  cc ");
            sb.append("\n          WHERE mt.id = :meterId ");
            sb.append("\n          AND   mc.devicemodel_fk = mt.devicemodel_id ");
            sb.append("\n          AND   cc.meterconfig_id = mc.id ");
            sb.append("\n          AND   cc.data_type = :tlbType ");
            sb.append("\n          AND   dc.id = cc.channel_id) ch ");
            sb.append("\n         ON lp.channel = ch.channel_index ");
            sb.append("\n    WHERE lp.meter_id = :meterId ");
            sb.append("\n    AND   lp.yyyymmddhh BETWEEN :startDate AND :endDate ");

            if (arrChannel.size() > 0) {
                sb.append("\n    AND   lp.channel IN (:channel) ");
            }

            sb.append("\n) x ");

            if (isSum) {
                sb.append("\n ) y ");
                sb.append("\nGROUP BY CHANNEL ");
            } else {
                sb.append("\nORDER BY yyyymmddhh, channel ");
            }

            query = getSession().createSQLQuery(new SQLWrapper().getQuery(sb.toString()));
            query.setInteger("meterId", meterId);
//            query.setString("startDate", beginDate);
//            query.setString("endDate", endDate);
            query.setString("startDate", beginDate + "00");
            query.setString("endDate", endDate + "23");
            query.setString("tlbType", tlbType);

            if (arrChannel.size() > 0) {
                query.setParameterList("channel", arrChannel);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
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
        Integer interval = 15;

        String startDate = null;
        String endDate = null;
        String contractNumber = StringUtil.nullToBlank(conditionMap.get("contractNumber"));
        String customerName = StringUtil.nullToBlank(conditionMap.get("customerName"));
        String mdsId = StringUtil.nullToBlank(conditionMap.get("friendlyName"));
        String meteringSF = StringUtil.nullToBlank(conditionMap.get("meteringSF"));
        String mcuId = StringUtil.nullToBlank(conditionMap.get("mcuId"));
        String modemId = StringUtil.nullToBlank(conditionMap.get("modemId"));
        String deviceType = StringUtil.nullToBlank(conditionMap.get("deviceType"));
        String mdevId = StringUtil.nullToBlank(conditionMap.get("mdevId"));
        String contractGroup = StringUtil.nullToBlank(conditionMap.get("contractGroup"));
        String meterType = StringUtil.nullToBlank(conditionMap.get("meterType"));
        List<Integer> sicIdList = (List<Integer>)conditionMap.get("sicIdList");

        List<Integer> locationIdList = (List<Integer>)conditionMap.get("locationIdList");
        Set<String> meterNoList = (Set<String>)conditionMap.get("meterNoList");
        String LpTable = MeterType.valueOf(meterType).getLpTableName();

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
            sb.append("\nSELECT contract_number AS CONTRACT_NUMBER, ");
            sb.append("\n       customer_name AS CUSTOMER_NAME, ");
            sb.append("\n       yyyymmddhh AS YYYYMMDDHH, ");
            sb.append("\n       yyyymmdd AS YYYYMMDD, ");
            sb.append("\n       dst AS DST, ");
            sb.append("\n       hh AS HH, ");
            sb.append("\n       mds_id AS METER_NO, ");
            sb.append("\n       friendly_name AS FRIENDLY_NAME, ");
            sb.append("\n       VALUE_1 AS CHANNEL_1, ");
            sb.append("\n       VALUE_2 AS CHANNEL_2, ");
            sb.append("\n       VALUE_3 AS CHANNEL_3, ");
            sb.append("\n       VALUE_4 AS CHANNEL_4, ");

            if (!isPrev) {
                sb.append("\n       device_serial AS MODEM_ID, ");
            }

            sb.append("\n       (select name from code where id = x.sic_id) as SIC_NAME, ");
            sb.append("\n       total AS VALUE ");
            sb.append("\nFROM ( ");
            sb.append("\n    SELECT lp.yyyymmddhh, ");
            sb.append("\n           lp.yyyymmdd, ");
            sb.append("\n           lp.dst, ");
            sb.append("\n           lp.hh, ");
            sb.append("\n           mt.mds_id, ");
            sb.append("\n           mt.friendly_name, ");
            sb.append("\n           co.contract_number, ");
            sb.append("\n           cu.name AS customer_name, ");
            sb.append("\n           co.sic_id, ");
            
            if (!isPrev) {
                if (!mcuId.isEmpty()) {
                    sb.append("\n           mo.device_serial, ");
                } else {
                    sb.append("\n           (SELECT md.device_serial FROM modem md WHERE md.id = mt.modem_id ) AS device_serial, ");
                }
            }

            sb.append("\n           ");
            for (int i = 0; i < 60; i++) {
                if (i > 0) {
                    sb.append(" + ");
                }
                sb.append("COALESCE(value_").append(CalendarUtil.to2Digit(i)).append(", 0)");
            }

            sb.append(" AS total ");

            Integer numindex = 1;
            for (int i = 0; i < 60; i = i + interval) {
                sb.append("\n , COALESCE(value_").append(CalendarUtil.to2Digit(i)).append(", 0)");
                sb.append(" AS  VALUE_" + numindex);
                numindex++;
            }

        }
        sb.append("\n    FROM ").append(LpTable).append(" lp ");
        sb.append("\n         LEFT OUTER JOIN ");
        sb.append("\n         contract co ");
        sb.append("\n         ON co.id = lp.contract_id ");
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

        sb.append("\n    WHERE lp.yyyymmddhh BETWEEN :startDate AND :endDate ");
        sb.append("\n    AND   lp.channel = 1 ");

        if (meterNoList != null) {
            sb.append("\n    AND   lp.mdev_id IN (:meterNoList) ");
        }

        sb.append("\n    AND   mt.id = lp.meter_id ");
        sb.append("\n    AND   mt.supplier_id = :supplierId ");

        if (!mdsId.isEmpty()) {
            sb.append("\n    AND   mt.mds_id = :mdsId ");
        }

        if (!deviceType.isEmpty()) {
            sb.append("\n    AND   lp.mdev_type = :deviceType ");
        }

        if (meteringSF.equals("s")) {
            sb.append("\n    AND   lp.value IS NOT NULL ");
        } else {
            sb.append("\n    AND   lp.value IS NULL ");
        }

        if (!mdevId.isEmpty()) {
        	if(mdevId.indexOf('%') == 0 || mdevId.indexOf('%') == (mdevId.length()-1)) { // %문자가 양 끝에 있을경우
                sb.append("\n    AND   lp.mdev_id LIKE :mdevId ");
        	}else {
                sb.append("\n    AND   lp.mdev_id = :mdevId ");
        	}
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

        if (!isTotal) {
//            sb.append("\n    ORDER BY lp.yyyymmddhh, lp.mdev_id, lp.dst ");
            sb.append("\n) x ");
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
            query.setString("mdevId", mdevId);
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

    /**
     * method name : getMeteringDataDetailHourlyData<b/>
     * method Desc : Metering Data 맥스가젯 상세화면에서 시간별 검침데이터를 조회한다.
     *
     * @param conditionMap
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> getMeteringDataDetailHourlyData(Map<String, Object> conditionMap, boolean isSum) {
        List<Map<String, Object>> result;

        Integer supplierId = (Integer)conditionMap.get("supplierId");
        String searchStartDate = StringUtil.nullToBlank(conditionMap.get("searchStartDate"));
        String searchEndDate = StringUtil.nullToBlank(conditionMap.get("searchEndDate"));
        String searchStartHour = StringUtil.nullToBlank(conditionMap.get("searchStartHour"));
        String searchEndHour = StringUtil.nullToBlank(conditionMap.get("searchEndHour"));
        String meterNo = StringUtil.nullToBlank(conditionMap.get("meterNo"));
        String meterType = StringUtil.nullToBlank(conditionMap.get("meterType"));
        String tlbType = StringUtil.nullToBlank(conditionMap.get("tlbType"));
        List<Integer> channelIdList = (List<Integer>)conditionMap.get("channelIdList");
        String lpTable = MeterType.valueOf(meterType).getLpTableName();

        StringBuilder sb = new StringBuilder();

        sb.append("\nSELECT lp.yyyymmddhh AS YYYYMMDDHH, ");
        sb.append("\n       lp.channel AS CHANNEL, ");
        sb.append("\n       lp.dst AS DST, ");
        sb.append("\n       lp.value AS VALUE, ");

        for (int i = 0 ; i < 60 ; i++) {
            sb.append("\n       lp.value_").append(CalendarUtil.to2Digit(i));
            sb.append(" AS VALUE_").append(CalendarUtil.to2Digit(i)).append(", ");
        }

        sb.append("\n      (SELECT DISTINCT dc.ch_method ");
        sb.append("\n              FROM meter me, ");
        sb.append("\n                   meterconfig mf, ");
        sb.append("\n                   display_channel dc, ");
        sb.append("\n                   channel_config  cc ");
        sb.append("\n              WHERE 1=1 ");
        sb.append("\n              AND   mf.devicemodel_fk = me.devicemodel_id ");
        sb.append("\n              AND   cc.meterconfig_id = mf.id ");
        sb.append("\n              AND   cc.data_type = :tlbType ");
        sb.append("\n              AND   dc.id = cc.channel_id ");
        sb.append("\n              AND   me.id = lp.meter_id ");
        sb.append("\n              AND   cc.channel_index = lp.channel) AS CH_METHOD ");
        sb.append("\nFROM ").append(lpTable).append(" lp ");
        sb.append("\nWHERE lp.mdev_type = :mdevType ");
        sb.append("\nAND   lp.mdev_id = :meterNo ");
        sb.append("\nAND   lp.yyyymmddhh BETWEEN :startDate AND :endDate ");
        if (channelIdList != null) {
            sb.append("\nAND   lp.channel IN (:channelIdList) ");
        }

        sb.append("\nAND   lp.supplier_id = :supplierId ");
        sb.append("\nORDER BY lp.yyyymmddhh, lp.dst, lp.channel ");

        SQLQuery query = getSession().createSQLQuery(new SQLWrapper().getQuery(sb.toString()));

        query.setString("tlbType", tlbType);
        query.setString("startDate", (searchStartHour.isEmpty()) ? searchStartDate + "00" : searchStartDate + searchStartHour);
        query.setString("endDate", (searchEndHour.isEmpty()) ? searchEndDate + "23" : searchEndDate + searchEndHour);
        query.setString("meterNo", meterNo);
        query.setString("mdevType", CommonConstants.DeviceType.Meter.name());
        query.setInteger("supplierId", supplierId);

        if (channelIdList != null) {
            query.setParameterList("channelIdList", channelIdList);
        }

        result = query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
        return result;
    }

    /**
     * method name : getMeteringDataDetailLpData<b/>
     * method Desc : Metering Data 맥스가젯 상세화면에서 Interval 별 검침 데이터를 조회한다.
     *
     * @param conditionMap
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> getMeteringDataDetailLpData(Map<String, Object> conditionMap) {
        List<Map<String, Object>> result = null;

        Integer supplierId = (Integer)conditionMap.get("supplierId");
        Integer dst = (Integer)conditionMap.get("dst");
        String searchStartDate = StringUtil.nullToBlank(conditionMap.get("searchStartDate"));
        String searchEndDate = StringUtil.nullToBlank(conditionMap.get("searchEndDate"));
        String searchStartHour = StringUtil.nullToBlank(conditionMap.get("searchStartHour"));
        String searchEndHour = StringUtil.nullToBlank(conditionMap.get("searchEndHour"));
        String meterNo = StringUtil.nullToBlank(conditionMap.get("meterNo"));
        String meterType = StringUtil.nullToBlank(conditionMap.get("meterType"));
        List<Integer> channelIdList = (List<Integer>)conditionMap.get("channelIdList");
        String lpClass = MeterType.valueOf(meterType).getLpClassName();
        StringBuilder sb = new StringBuilder();

        sb.append("\nSELECT lp.id.yyyymmddhh AS yyyymmddhh, ");

        for (int i = 0 ; i < 60 ; i++) {
            sb.append("\n       lp.value_").append(CalendarUtil.to2Digit(i)).append(" AS value_").append(CalendarUtil.to2Digit(i)).append(", ");
        }
        sb.append("\n       lp.value AS value, ");
        sb.append("\n       lp.id.channel AS channel, ");
        sb.append("\n       lp.id.dst AS dst ");
        sb.append("\nFROM ").append(lpClass).append(" lp ");
        sb.append("\nWHERE 1=1 ");
        sb.append("\nAND   lp.id.mdevType = :mdevType ");
        sb.append("\nAND   lp.id.mdevId = :meterNo ");
        sb.append("\nAND   lp.id.yyyymmddhh BETWEEN :searchStartDate AND :searchEndDate ");

        if (dst != null) {
            sb.append("\nAND   lp.id.dst = :dst ");
        }

        if (channelIdList != null && channelIdList.size() > 0) {
            sb.append("\nAND   lp.id.channel IN (:channelIdList) ");
        }

        sb.append("\nAND   lp.supplier.id = :supplierId ");
        sb.append("\nORDER BY lp.id.yyyymmddhh, lp.id.channel ");

        Query query = getSession().createQuery(sb.toString());

        query.setString("meterNo", meterNo);
        query.setString("searchStartDate", (searchStartHour.isEmpty()) ? searchStartDate + "00" : searchStartDate + searchStartHour);
        query.setString("searchEndDate", (searchEndHour.isEmpty()) ? searchEndDate + "23" : searchEndDate + searchEndHour);
        query.setString("mdevType", CommonConstants.DeviceType.Meter.name());

        if (dst != null) {
            query.setInteger("dst", dst);
        }

        if (channelIdList != null && channelIdList.size() > 0) {
            query.setParameterList("channelIdList", channelIdList);
        }
        query.setInteger("supplierId", supplierId);

        result = query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
        return result;
    }
    
    /**
     * method name : getSgdgXam1LPData<b/>
     * method Desc : SgdgXam1테이블에 넣을 데이터들의 LP 값들을 조회해온다.
     * 
     * @return
     */
    @SuppressWarnings({ "unchecked" })
    @Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
	public List<Map<String, Object>> getSgdgXam1LPData(Map<String, Object> conditionMap) {
    	List<Map<String, Object>> result = null;

    	List<Integer> channelList = (List<Integer>) conditionMap.get("channelList");
    	boolean isSeachMinMax = (Boolean) conditionMap.get("isSeachMinMax");
    	boolean isFirst = (Boolean) conditionMap.get("isFirst");
    	String minDate = (String) conditionMap.get("minDate");
    	String maxDate = (String) conditionMap.get("maxDate");
    	
    	StringBuilder sb = new StringBuilder();
    	
    	if(isSeachMinMax) {
    		if(isFirst) {
    			sb.append("\n SELECT MIN(lpEM.YYYYMMDDHH) AS MINDATE, MAX(lpEM.YYYYMMDDHH) AS MAXDATE, COUNT(*) AS CNT");
    		} else {
    			sb.append("\n SELECT MIN(lpEM.YYYYMMDDHH) AS MINDATE");
    		}
    	} else {
	 		sb.append("\n	SELECT lpEM.YYYYMMDDHH AS YYYYMMDDHH, lpEM.CHANNEL AS CHANNEL, lpEM.mdev_id AS MDEV_ID, c.CONTRACT_NUMBER AS CUSTOMERID, lpEM.SEND_RESULT AS SEND_RESULT, m.LP_INTERVAL AS INTERVAL ");
	    	for (int i = 0; i < 60; i++) {
			if(i < 10) {
				sb.append("\n 		 ,lpEM.value_0" + i);
			} else {
				sb.append("\n 		 , lpEM.value_" + i);
			}
			sb.append(" AS VALUE" + i);
			}
    	}
 		sb.append("\n	FROM LP_EM lpEM, CONTRACT c, METER m");
 		sb.append("\n	WHERE lpEM.CONTRACT_ID = c.id");
    	sb.append("\n	AND c.METER_ID = m.id");
 		sb.append("\n	AND lpEM.VALUE_CNT*2 <> LENGTH(NVL(lpEM.SEND_RESULT,0))");
 		
 		if(isSeachMinMax) {
 			sb.append("\n	AND lpEM.CHANNEL=1");
 		} else {
 			sb.append("\n	AND lpEM.CHANNEL in (:channelList)");
 		}
 		
 		if(isFirst) {
 			sb.append("\n	AND lpEM.YYYYMMDDHH >= :minDate");
 		} else if(isSeachMinMax) {
			sb.append("\n	AND lpEM.YYYYMMDDHH BETWEEN :minDate AND :maxDate");
		} else {
			sb.append("\n	AND lpEM.YYYYMMDDHH = :minDate");
		}

    	SQLQuery query = getSession().createSQLQuery(sb.toString());
    	if(!isSeachMinMax) {
    		query.setParameterList("channelList", channelList);
    	}
    	
    	if(isFirst) {
    		query.setString("minDate", minDate);
    	} else if(isSeachMinMax) {
    		query.setString("minDate", minDate);
    		query.setString("maxDate", maxDate);
    	} else {
    		query.setString("minDate", minDate);
    	}
    	
    	result = query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();

    	return result;
    }
    
    /**
     * method name : getSgdgXam1Data<b/>
     * method Desc : SgdgXam1테이블에 넣을 데이터들을 조회해온다.
     * 
     * @return
     */
    @SuppressWarnings({ "unchecked" })
    @Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
	public List<Map<String, Object>> getSgdgXam1Data(Map<String, Object> conditionMap) {
    	List<Map<String, Object>> result = null;

    	List<Integer> channelList = (List<Integer>) conditionMap.get("channelList");
    	
    	StringBuilder sb = new StringBuilder();
    	
    	sb.append("\n	select e.*, b.* ");
    	sb.append("\n	from (								");
 		sb.append("\n			SELECT lpEM.YYYYMMDDHH AS YYYYMMDDHH, lpEM.mdev_id AS MDEV_ID, lpEM.CHANNEL AS CHANNEL, lpEM.SEND_RESULT AS SEND_RESULT, m.LP_INTERVAL AS INTERVAL, c.CONTRACT_NUMBER AS CUSTOMERID ");
    	for (int i = 0; i < 60; i++) {
		if(i < 10) {
			sb.append("\n 		 ,lpEM.value_0" + i);
		} else {
			sb.append("\n 		 , lpEM.value_" + i);
		}
		sb.append(" AS VALUE" + i);
		}
 		sb.append("\n	    	FROM LP_EM lpEM, CONTRACT c, METER m");
 		sb.append("\n	where lpEM.CONTRACT_ID = c.id");
    	sb.append("\n	and c.METER_ID = m.id");
 		sb.append("\n	and lpEM.VALUE_CNT*2 <> LENGTH(NVL(lpEM.SEND_RESULT,0))");
 		sb.append("\n	AND lpEM.MDEV_Type=:mdevType");
 		sb.append("\n	and lpEM.CHANNEL in (:channelList)) e,");
 		sb.append("\n	(SELECT em.yyyymmddhh as YYYYMMDDHH, bm.yyyymmdd AS YYYYMMDD, bm.ACTIVEPWRDMDMAXIMPORTRATETOTAL AS ACTIVEPWRDMDMAXIMPORTRATETOTAL, ");
    	sb.append("\n		bm.ATVPWRDMDMAXTIMEIMPRATETOT AS ATVPWRDMDMAXTIMEIMPRATETOT, bm.PF AS PF, bm.mdev_id AS MDEV_ID");
 		sb.append("\n	from billing_month_em bm,");
 		sb.append("\n	(SELECT lpEM.YYYYMMDDHH AS YYYYMMDDHH, lpEM.MDEV_ID AS MDEV_ID, lpEM.CHANNEL AS CHANNEL, lpEM.SEND_RESULT AS SEND_RESULT");
 		sb.append("\n	FROM LP_EM lpEM, CONTRACT c");
 		sb.append("\n	where lpEM.CONTRACT_ID = c.id");
 		sb.append("\n	and lpEM.VALUE_CNT*2 <> LENGTH(NVL(lpEM.SEND_RESULT,0))");
    	sb.append("\n	AND lpEM.MDEV_Type=:mdevType");
    	sb.append("\n	and lpEM.channel = 1) em");
    	sb.append("\n	where em.mdev_id = bm.mdev_id");
    	sb.append("\n	and   bm.yyyymmdd = case when substr(em.yyyymmddhh, 5, 2) = '12' then to_char(to_number(substr(em.yyyymmddhh, 1, 4)) + 1)"); 
    	sb.append("\n	                         else substr(em.yyyymmddhh, 1, 4) end ||");
    	sb.append("\n	                    case when substr(em.yyyymmddhh, 5, 2) = '12' then '01'"); 
    	sb.append("\n							else lpad(to_char(to_number(substr(em.yyyymmddhh, 5, 2)) + 1), 2, '0') end || '01') b");
    	sb.append("\n	where e.mdev_id = b.mdev_id");
    	sb.append("\n	and   e.yyyymmddhh = b.yyyymmddhh");
    	sb.append("\n	ORDER BY b.MDEV_ID, b.YYYYMMDDHH");

    	SQLQuery query = getSession().createSQLQuery(sb.toString());
    	query.setString("mdevType", "Meter");
    	query.setParameterList("channelList", channelList);
    	
    	result = query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();

    	return result;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
    public Map<String, Object> getMeteringLpPreData(Map<String, Object> conditionMap) {
    	Map<String, Object> returnData = new HashMap<String, Object>();

    	String mdevId = StringUtil.nullToBlank(conditionMap.get("mdevId"));
    	Integer lpInterval = (Integer) conditionMap.get("lpInterval");
    	Integer channel = (Integer) conditionMap.get("channel");
    	String yyyymmddhh = StringUtil.nullToBlank(conditionMap.get("yyyymmddhh"));

    	StringBuilder sb = new StringBuilder();
    	sb.append("\n SELECT lpEM.yyyymmddhh AS PREYYYYMMDDHH ");
    	for (int i = 0; i < 60; i=(lpInterval+i)) {
    		if(i < 10) {
    			sb.append("\n 		 , lpEM.value_0" + i);
    		} else {
    			sb.append("\n 		 , lpEM.value_" + i);
    		}
    		sb.append(" AS VALUE" + i);
		}
    	sb.append("\n FROM Lp_EM lpEM");
    	sb.append("\n WHERE lpEM.yyyymmddhh = (SELECT MAX(lp.yyyymmddhh)");
    	sb.append("\n 		  				   FROM LP_EM lp");
    	sb.append("\n 		  				   WHERE lp.YYYYMMDDHH < :yyyymmddhh AND lp.mdev_id=:mdevId and lp.CHANNEL=:channel and lp.value_cnt is not null)");
    	sb.append("\n AND	lpEM.mdev_Id = :mdevId");
    	sb.append("\n AND	lpEM.channel = :channel");

    	SQLQuery query = getSession().createSQLQuery(sb.toString());
    	query.setString("yyyymmddhh", yyyymmddhh);
    	query.setString("mdevId", mdevId);
    	query.setInteger("channel", channel);

    	List list = query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
    	if(list.size() > 0) {
    		returnData = (Map<String, Object>) list.get(0);
    	}

    	return returnData;
    }
}