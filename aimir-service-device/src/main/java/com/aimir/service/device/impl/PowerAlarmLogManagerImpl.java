package com.aimir.service.device.impl;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.aimir.constants.CommonConstants.LineType;
import com.aimir.dao.device.PowerAlarmLogDao;
import com.aimir.dao.system.CodeDao;
import com.aimir.dao.system.LocationDao;
import com.aimir.dao.system.SupplierDao;
import com.aimir.model.system.Supplier;
import com.aimir.service.device.PowerAlarmLogManager;
import com.aimir.util.CalendarUtil;
import com.aimir.util.DateTimeUtil;
import com.aimir.util.DecimalUtil;
import com.aimir.util.StringUtil;
import com.aimir.util.TimeLocaleUtil;
import com.aimir.util.TimeUtil;

@Service(value = "powerAlarmLogManager")
@Transactional(readOnly=false)
public class PowerAlarmLogManagerImpl implements PowerAlarmLogManager {

    protected static Log logger = LogFactory.getLog(PowerAlarmLogManagerImpl.class);

    @Autowired
    public PowerAlarmLogDao poweralarmlogDao;
    @Autowired
    public LocationDao locationDao;
    @Autowired
    public CodeDao codeDao;
    @Autowired
    public SupplierDao supplierDao;

    /**
     * MiniGadget ColumnChart 데이터 조회
     * 검색기간을 최근 일주일로 설정.
     */
    @SuppressWarnings("unchecked")
    public List<Object> getPowerAlarmLogColumnMiniChart(Map<String, Object> params) {
        params.put("searchStartDate", this.getPrevWeekDate());
        params.put("searchEndDate", this.getCurrentDate());
        params.put("dateLength", "8");

        Integer supplierId = (Integer)params.get("supplierId");
        Supplier supplier = supplierDao.get(supplierId);
        String lang = supplier.getLang().getCode_2letter();
        String country = supplier.getCountry().getCode_2letter();

        List<Object> result = new ArrayList<Object>();
        result = poweralarmlogDao.getPowerAlarmLogColumnChartData(params);

        List<Object> resultList = new ArrayList<Object>();
        HashMap<String, Object> resultMap = null;

        int index = 0;
        String searchDate = null;
        for (int i = 0; i < 7; i++) {
            resultMap = new HashMap<String, Object>();

            try {
                searchDate = DateTimeUtil.getPreDay(CalendarUtil.getCurrentDate(), (6-i)).substring(0, 8);
            } catch(ParseException e) {
                e.printStackTrace();
            }

            resultMap.put("searchDate", TimeLocaleUtil.getLocaleDate(searchDate, lang, country));
            for (int m = index; m < result.size(); m++) {
                Map<String, Object> tmp = (Map<String, Object>)result.get(m);
                if (searchDate.equals(((String)tmp.get("OPENDATE")).substring(0, 8))) {
                    if (tmp.get("TYPE") != null) {
                        resultMap.put(String.valueOf(tmp.get("TYPE")), String.valueOf(tmp.get("COUNT")));
                        resultMap.put("type" + String.valueOf(tmp.get("TYPE")), String.valueOf(tmp.get("COUNT")));
                    } else {
                        resultMap.put("open", String.valueOf(tmp.get("COUNT"))); // 미복구
                    }
                    index++;
                } else {
                    break;
                }
            }

            resultList.add(resultMap);
        }

        return resultList;
    }

    /**
     * MaxGadget ColumnChart 데이터 조회
     * 일별, 주별, 월별 조회
     */
    @SuppressWarnings("unchecked")
    public List<Object> getPowerAlarmLogColumnChart(Map<String, Object> params) {
        String searchDateType = StringUtil.nullToBlank(params.get("searchDateType"));
        String searchStartDate = StringUtil.nullToBlank(params.get("searchStartDate"));
        String searchEndDate = StringUtil.nullToBlank(params.get("searchEndDate"));
        Integer supplierId = (Integer)params.get("supplierId");
        Integer locationId = (Integer)params.get("location");

        if ("3".equals(searchDateType) || "4".equals(searchDateType)) {
            params.put("dateLength", "8"); // 주별, 월별 (날짜를 8자리로 그룹 YYYYMMDD)
        } else if ("1".equals(searchDateType)) {
            params.put("dateLength", "10"); // 일별 (날짜를 10자리로 그룹 YYYYMMDDHH)
        } else { // 기본 조회 조건이 없는 경우 일별조건으로 조회 (현제 날짜 기준)
            try {
                String currDate = TimeUtil.getCurrentDay();
                searchStartDate = currDate;
                searchEndDate = currDate;
            } catch(ParseException e) {
                e.printStackTrace();
            }
            params.put("searchDateType", "1");
            params.put("searchStartDate", searchStartDate);
            params.put("searchEndDate", searchEndDate);
            params.put("dateLength", "10");
        }

        // 공급지역 검색 조건 시 - 최하위 노드 값 조회 및 설정
        if (locationId != null) {
            List<Integer> locations = locationDao.getLeafLocationId(locationId, supplierId);
            params.put("locations", locations);
        }

        Supplier supplier = supplierDao.get(supplierId);
        String lang = supplier.getLang().getCode_2letter();
        String country = supplier.getCountry().getCode_2letter();

        List<Object> result = new ArrayList<Object>();
        result = poweralarmlogDao.getPowerAlarmLogColumnChartData(params);

        List<Object> resultList = new ArrayList<Object>();
        HashMap<String, Object> resultMap = null;
        if ("3".equals(searchDateType) || "4".equals(searchDateType)) { // 주별

            int index = 0;
            int searchDuration = 0;
            try {
                searchDuration = TimeUtil.getDayDuration(searchStartDate, searchEndDate) + 1;
            } catch(ParseException e) {
                e.printStackTrace();
            }

            String searchDate = "";

            for (int i = 0; i < searchDuration; i++) {
                resultMap = new HashMap<String, Object>();

                try {
                    searchDate = DateTimeUtil.getPreDay(searchStartDate, -i).substring(0, 8);
                } catch(ParseException e) {
                    e.printStackTrace();
                }

                resultMap.put("searchDate", TimeLocaleUtil.getLocaleDate(searchDate, lang, country));
                for (int m = index, len = result.size(); m < len; m++) {
                    Map<String, Object> tmp = (Map<String, Object>)result.get(m);
                    if (searchDate.equals(((String)tmp.get("OPENDATE")).substring(0, 8))) {
                        if (tmp.get("TYPE") != null) {
                            resultMap.put("type" + String.valueOf(tmp.get("TYPE")), String.valueOf(tmp.get("COUNT")));
                        } else {
                            resultMap.put("open", String.valueOf(tmp.get("COUNT"))); // 미복구
                        }
                        index++;
                    } else {
                        break;
                    }
                }

                resultList.add(resultMap);
            }
        } else {
            int index = 0;
            for (int i = 0; i < 24; i++) {
                resultMap = new HashMap<String, Object>();
                String searchDate = String.format("%02d", i);
                resultMap.put("searchDate", searchDate);

                for (int m = index, len = result.size(); m < len; m++) {
                    Map<String, Object> tmp = (Map<String, Object>)result.get(m);
                    if (searchDate.equals(((String)tmp.get("OPENDATE")).substring(8, 10))) {
                        if (tmp.get("TYPE") != null) {
                            resultMap.put("type" + String.valueOf(tmp.get("TYPE")), String.valueOf(tmp.get("COUNT")));
                        } else {
                            resultMap.put("open", String.valueOf(tmp.get("COUNT"))); // 미복구
                        }
                        index++;
                    } else {
                        break;
                    }
                }

                resultList.add(resultMap);
            }
        }

        return resultList;
    }

    /**
     * MaxGadget PieChart 데이터 조회
     * Status 기준 (open: 정전지속, close: 정전복구)
     */
    @SuppressWarnings("unchecked")
    public List<Object> getPowerAlarmLogPieData(Map<String, Object> params) {
        String searchDateType = StringUtil.nullToBlank(params.get("searchDateType"));
        Integer supplierId = (Integer)params.get("supplierId");
        Integer locationId = (Integer)params.get("location");

        // 기본 조회 조건이 없는 경우 일별조건으로 조회 (현제 날짜 기준)
        if (!"1".equals(searchDateType) && !"3".equals(searchDateType) && !"4".equals(searchDateType)) {
            params.put("searchDateType", "1");
            params.put("searchStartDate", this.getCurrentDate());
            params.put("searchEndDate", this.getCurrentDate());
        }

        // 공급지역 검색 조건 시 - 최하위 노드 값 조회 및 설정
        if (locationId != null) {
            List<Integer> locations = locationDao.getLeafLocationId(locationId, supplierId);
            params.put("locations", locations);
        }

        List<Object> result = new ArrayList<Object>();
        result = poweralarmlogDao.getPowerAlarmLogPieData(params);

        List<Object> resultList = new ArrayList<Object>();
        HashMap<String, Object> resultMap = null;
        if (result.size() > 0) {
            Map<String, Object> tmp = null;
            for (Object obj : result) {
                tmp = (Map<String, Object>)obj;

                resultMap = new HashMap<String, Object>();
                if (String.valueOf(tmp.get("STATUS")).toLowerCase().equals("close")
                        || String.valueOf(tmp.get("STATUS")).toLowerCase().equals("closed")) {
                    resultMap.put("count", String.valueOf(tmp.get("COUNT")));
                    resultMap.put("type", "close");
                } else if (String.valueOf(tmp.get("STATUS")).equals("open")) {
                    resultMap.put("count", String.valueOf(tmp.get("COUNT")));
                    resultMap.put("type", "open");
                }

                resultList.add(resultMap);
            }
        } else { // 기본 PieChart를 표현하기 위한 Default Data
            resultMap = new HashMap<String, Object>();
            resultMap.put("count", "1");
            resultMap.put("type", "");
            resultList.add(resultMap);
        }

        return resultList;
    }

    /**
     * MaxGadget Grid 데이터 조회
     */
    @SuppressWarnings("unchecked")
    public List<Object> getPowerAlarmLogGridData(Map<String, Object> params) {
        String searchDateType = StringUtil.nullToBlank(params.get("searchDateType"));
        String durationDays = StringUtil.nullToBlank(params.get("durationDays"));
        Integer locationId = (Integer)params.get("location");
        Integer supplierId = (Integer)params.get("supplierId");
        Integer page = (Integer)params.get("page");
        Integer limit = (Integer)params.get("limit");
        Integer start = (page != null && limit != null) ? (page * limit) + 1 : 1;

        Supplier supplier = supplierDao.get(supplierId);
        String lang = supplier.getLang().getCode_2letter();
        String country = supplier.getCountry().getCode_2letter();

        // 기본 조회 조건이 없는 경우 일별조건으로 조회 (현제 날짜 기준)
        if (!searchDateType.equals("1") && !searchDateType.equals("3") && !searchDateType.equals("4")) {
            params.put("searchDateType", "1");
            params.put("searchStartDate", this.getCurrentDate());
            params.put("searchEndDate", this.getCurrentDate());
        }

        if (locationId != null) {
            List<Integer> locations = locationDao.getLeafLocationId(locationId, supplierId);
            params.put("locations", locations);
        }

        // 공급지역 검색 조건 시 - 최하위 노드 값 조회 및 설정
        List<Object> result = new ArrayList<Object>();
        result = poweralarmlogDao.getPowerAlarmLogListData(params);

        List<Object> resultList = new ArrayList<Object>();
        HashMap<String, Object> resultMap = null;

        Map<String, Object> tmp = null;
        int idx = 0;
        for (Object obj : result) {
            tmp = new HashMap<String, Object>();
            tmp = (Map<String, Object>)obj;

            resultMap = new HashMap<String, Object>();
            // id -> row number 변경
            resultMap.put("id", (start + (idx++)));
            resultMap.put("openTime", TimeLocaleUtil.getLocaleDate((String)tmp.get("OPENTIME"), lang, country));
            resultMap.put("closeTime", TimeLocaleUtil.getLocaleDate((String)tmp.get("CLOSETIME"), lang, country));
            resultMap.put("supplier", (tmp.get("LOCATIONNAME") == null) ? "" : String.valueOf(tmp.get("LOCATIONNAME")));
            resultMap.put("lineType", (tmp.get("LINETYPE") != null) ? LineType.valueOf((String)tmp.get("LINETYPE")).getName()
                    : "");
            resultMap.put("custName", (tmp.get("CUSTOMERNAME") == null) ? "" : (String)tmp.get("CUSTOMERNAME"));
            resultMap.put("meter", (tmp.get("MDS_ID") == null) ? "" : (String)tmp.get("MDS_ID"));
            resultMap.put("duration", this.getDurationFormat(DecimalUtil.ConvertNumberToLong(tmp.get("DURATION")), durationDays));
            resultMap.put("status", this.getPowerStatus(String.valueOf(tmp.get("STATUS"))));
            resultMap.put("message", StringUtil.nullToBlank(tmp.get("MESSAGE")));

            resultList.add(resultMap);
        }

        return resultList;
    }

    /**
     * method name : getPowerAlarmLogGridDataTotalCount<b/>
     * method Desc : Power Outage  맥스가젯에서 Grid 의 Total Count 를 조회한다.
     *
     * @param conditionMap
     * @return
     */
    public Integer getPowerAlarmLogGridDataTotalCount(Map<String, Object> conditionMap) {
        List<Object> result = poweralarmlogDao.getPowerAlarmLogListData(conditionMap, true);

        return (Integer)(result.get(0));
    }

    @SuppressWarnings("unused")
    public List<Object> getPowerAlamLogMaxData(Map<String,Object> params) {
        Map<String,Object> resultMap = new HashMap<String,Object>();
        List<Object> resultList = this.getPowerAlarmLogGridData(params);

        return resultList;
    }

    /**
     * Excel저장용
     */
    @SuppressWarnings("unused")
    public List<Object> getPowerAlamLogMaxDataExcel(Map<String,Object> params) {
        List<Object> powerAlarmLogMaxData = new ArrayList<Object>();
        return this.getPowerAlarmLogGridData(params);
    }

    /**
     * 현제 날짜 (YYYYMMDD)
     * @return
     */
    public String getCurrentDate() {
        Calendar ti = Calendar.getInstance();

        String month = "";
        String day = "";

        if((ti.get(Calendar.MONTH) + 1) < 10) month = "0" +  (ti.get(Calendar.MONTH) + 1);
        else month = "" +  (ti.get(Calendar.MONTH) + 1);

        if((ti.get(Calendar.DAY_OF_MONTH) + 1) < 10) day = "0" +  ti.get(Calendar.DAY_OF_MONTH);
        else day = "" +  ti.get(Calendar.DAY_OF_MONTH);

        return "" + ti.get(Calendar.YEAR) + month + day;
    }

    /**
     * 7일전 날짜 (YYYYMMDD)
     * @return
     */
    public String getPrevWeekDate() {
        Calendar ti = Calendar.getInstance();
        ti.add(Calendar.DAY_OF_MONTH, -6);

        String month = "";
        String day = "";

        if((ti.get(Calendar.MONTH) + 1) < 10) month = "0" +  (ti.get(Calendar.MONTH) + 1);
        else month = "" +  (ti.get(Calendar.MONTH) + 1);

        if((ti.get(Calendar.DAY_OF_MONTH) + 1) < 10) day = "0" +  ti.get(Calendar.DAY_OF_MONTH);
        else day = "" +  ti.get(Calendar.DAY_OF_MONTH);

        return "" + ti.get(Calendar.YEAR) + month + day;
    }

    /**
     * 지속시간 (초 단위 지속시간을 일/시/분/초로 변경)
     * @param sec
     * @return
     */
    private String getDurationFormat(Long duration, String durationDays) {
        if (duration == null) {
            return "";
        }

        StringBuilder sbDuration = new StringBuilder();

        long s;
        long m;
        long h;
        long d;

        s = duration % 60l;      // 초
        duration = duration / 60l;
        m = duration % 60l;      // 분
        duration = duration / 60l;
        h = duration % 24l;      // 시
        d = duration / 24l;      // 일

        sbDuration.append(d).append(durationDays).append(" ");
        sbDuration.append(String.format("%02d", h)).append(":");
        sbDuration.append(String.format("%02d", m)).append(":");
        sbDuration.append(String.format("%02d", s));
        
        return sbDuration.toString();
    }

    /**
     * Flex Grid - 날짜 Format (YYYY-MM-DD)
     * @param yyyymmddhhmmss
     * @return
     */
    public String getDateFormat(String yyyymmddhhmmss) {
        return yyyymmddhhmmss.substring(0, 4) + "-"
            + yyyymmddhhmmss.substring(4, 6) + "-"
            + yyyymmddhhmmss.substring(6, 8);
    }

    /**
     * Flex Grid -  복구여부
     * @param status
     * @return
     */
    public String getPowerStatus(String status) {
        if(status.equals("open")) {
            return "open";
        } else {
            return "close";
        }
    }
}