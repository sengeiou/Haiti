package com.aimir.service.mvm.impl;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.aimir.constants.CommonConstants;
import com.aimir.dao.device.MeterDao;
import com.aimir.dao.mvm.MeteringLpDao;
import com.aimir.dao.system.SupplierDao;
import com.aimir.model.system.Supplier;
import com.aimir.service.mvm.DataGapsManager;
import com.aimir.util.CommonUtils2;
import com.aimir.util.DecimalUtil;
import com.aimir.util.StringUtil;
import com.aimir.util.TimeLocaleUtil;
import com.aimir.util.TimeUtil;

@Service(value = "dataGapsManager")
@Transactional
public class DataGapsManagerImpl implements DataGapsManager{
	
	@Autowired
	MeterDao meterDao;
	
	@Autowired
	MeteringLpDao meteringlpDao;
	
	@Autowired
	SupplierDao supplierDao;

    protected static Log logger = LogFactory.getLog(DataGapsManagerImpl.class);

	/**
	 * DataGaps Mini Gadget 에서 호출
	 * 기간별 전체미터수,LP부분누락미터수,LP전체누락미터수
	 * 일별,시간별 LP누락 미터수 목록을 조회한다.
	 * @param params
	 * @return
	 */
    public Map<String, Object> getDataGaps(Map<String, Object> params) {
        String searchStartDate = StringUtil.nullToBlank(params.get("searchStartDate"));
        String searchEndDate = StringUtil.nullToBlank(params.get("searchEndDate"));
        String dateType = StringUtil.nullToBlank(params.get("searchDateType"));

        // 날짜 포멧 M/D/YY 일경우 YYYYMMDD로 변경. 가젯 초기 로드시 날짜 포멧이 잘못 올수 있다.
        if (searchStartDate.matches("[0-9]*/[0-9]*/[0-9]*") || searchEndDate.matches("[0-9]*/[0-9]*/[0-9]*")) {
            searchStartDate = ""; // 비워놓으면 밑에서 현재 날짜로 초기화 한다.
            searchEndDate = "";
        }

        params.put("channel", 1);

        String currentDate = TimeUtil.getCurrentDateUsingFormat("yyyymmdd");
        if ("".equals(searchStartDate)) {
            params.put("searchStartDate", currentDate);
        }
        if ("".equals(searchEndDate)) {
            params.put("searchEndDate", currentDate);
        }
        if ("".equals(dateType) || CommonConstants.DateType.HOURLY.getCode().equals(dateType)) {
            dateType = CommonConstants.DateType.DAILY.getCode();
            params.put("dateType", dateType);
        }

        Integer totalMeterCount = meterDao.getMeterCount(params);
        params.put("totalMeterCount", totalMeterCount);

        Integer patialMissingMeterCount = meterDao.getPatialMissingMeterCount(params);

        Integer allMissingMeterCount = meterDao.getAllMissingMeterCount(params);

        params.put("totalMeterCnt", totalMeterCount);
        List<Object> resultList = null;
        if (CommonConstants.DateType.DAILY.getCode().equals(dateType)) {
            resultList = meterDao.getMissingMetersByHour(params);
        } else {
            resultList = meterDao.getMissingMetersByDay(params);
        }

        Map<String, Object> resultMap = new HashMap<String, Object>();
        resultMap.put("totalMeterCount", totalMeterCount);
        resultMap.put("patialMissingMeterCount", patialMissingMeterCount);
        resultMap.put("allMissingMeterCount", allMissingMeterCount);
        resultMap.put("dataGapsList", resultList);

        return resultMap;
    }

    public Map<String, Object> getDataGaps2(Map<String, Object> params) {
        // 페이지 시작을 0으로 설정.
        params = CommonUtils2.getFirstPageForExtjsGrid3(params);

        String searchStartDate = StringUtil.nullToBlank(params.get("searchStartDate"));
        String searchEndDate = StringUtil.nullToBlank(params.get("searchEndDate"));
        String dateType = StringUtil.nullToBlank(params.get("searchDateType"));

        // 날짜 포멧 M/D/YY 일경우 YYYYMMDD로 변경. 가젯 초기 로드시 날짜 포멧이 잘못 올수 있다.
        if (searchStartDate.matches("[0-9]*/[0-9]*/[0-9]*") || searchEndDate.matches("[0-9]*/[0-9]*/[0-9]*")) {
            searchStartDate = ""; // 비워놓으면 밑에서 현재 날짜로 초기화 한다.
            searchEndDate = "";
        }

        params.put("channel", 1);

        String currentDate = TimeUtil.getCurrentDateUsingFormat("yyyymmdd");
        if ("".equals(searchStartDate)) {
            params.put("searchStartDate", currentDate);
        }
        if ("".equals(searchEndDate)) {
            params.put("searchEndDate", currentDate);
        }
        if ("".equals(dateType) || CommonConstants.DateType.HOURLY.getCode().equals(dateType)) {
            dateType = CommonConstants.DateType.DAILY.getCode();
            params.put("dateType", dateType);
        }

        Integer totalMeterCount = meterDao.getMeterCount(params);
        params.put("totalMeterCount", totalMeterCount);

        Integer patialMissingMeterCount = meterDao.getPatialMissingMeterCount(params);

        Integer allMissingMeterCount = meterDao.getAllMissingMeterCount(params);

        params.put("totalMeterCnt", totalMeterCount);
        List<Object> resultList = null;

        if (CommonConstants.DateType.DAILY.getCode().equals(dateType)) {
            resultList = meterDao.getMissingMetersByHour(params);
        } else {
            resultList = meterDao.getMissingMetersByDay(params);
        }

        Map<String, Object> resultMap = new HashMap<String, Object>();
        resultMap.put("totalMeterCount", totalMeterCount);
        resultMap.put("patialMissingMeterCount", patialMissingMeterCount);
        resultMap.put("allMissingMeterCount", allMissingMeterCount);

        // 미터 리스트..
        resultMap.put("dataGapsList", resultList);

        return resultMap;
    }

	/**
	 * lp 누락된 미터 목록을 조회한다.
	 * @param params
	 * @return
	 */
	@SuppressWarnings("unchecked")
    public List<Object> getLpMissingMeters(Map<String, Object> params) {
        String searchStartDate = StringUtil.nullToBlank(params.get("searchStartDate"));
        String searchEndDate = StringUtil.nullToBlank(params.get("searchEndDate"));
        String dateType = StringUtil.nullToBlank(params.get("searchDateType"));
        String supplierId = StringUtil.nullToBlank(params.get("supplierId"));

        params.put("channel", 1);

        // 날짜 포멧 M/D/YY 일경우 YYYYMMDD로 변경. 가젯 초기 로드시 날짜 포멧이 잘못 올수 있다.
        if (searchStartDate.matches("[0-9]*/[0-9]*/[0-9]*") || searchEndDate.matches("[0-9]*/[0-9]*/[0-9]*")) {
            searchStartDate = ""; // 비워놓으면 밑에서 현재 날짜로 초기화 한다.
            searchEndDate = "";
        }

        String currentDate = TimeUtil.getCurrentDateUsingFormat("yyyymmdd");
        if ("".equals(searchStartDate)) {
            params.put("searchStartDate", currentDate);
        }
        if ("".equals(searchEndDate)) {
            params.put("searchEndDate", currentDate);
        }
        if ("".equals(dateType) || CommonConstants.DateType.HOURLY.getCode().equals(dateType)) {
            dateType = CommonConstants.DateType.DAILY.getCode();
            params.put("dateType", dateType);
        }

        // missing lp list fetch
        List<Object> missingLpList = meterDao.getMissingMeters(params);

        if (supplierId.length() > 0) {
            Supplier supplier = supplierDao.get(Integer.parseInt(supplierId));
            for (Object data : missingLpList) {
                Map<String, Object> mapData = (Map<String, Object>) data;
                mapData.put("lastReadDate", TimeLocaleUtil.getLocaleDate(StringUtil.nullToBlank(mapData.get("lastReadDate")),
                        supplier.getLang().getCode_2letter(), supplier.getCountry().getCode_2letter()));
            }
        }

        int page = Integer.parseInt(StringUtil.nullToZero(params.get("page")));
        int pageSize = CommonConstants.Paging.ROWPERPAGE_100.getPageNum();
        int subFrom = page * pageSize;
        int subTo = missingLpList.size() > (subFrom + pageSize) ? subFrom + pageSize : missingLpList.size();

        List<Object> result = new ArrayList<Object>();
        result.add(missingLpList.size());
        result.add(missingLpList.subList(subFrom, subTo));

        return result;
    }

    public static Map<String, Object> getFirstPageForExtjsGrid2(Map<String, Object> conditionMap) {
        // 페이징 처리를 위한 부분 추가.extjs 는 0부터 시작
        String temppage = (String) conditionMap.get("page");

        int intTemppage = Integer.parseInt(temppage);

        intTemppage = intTemppage - 1;

        conditionMap.put("page", Integer.toString(intTemppage));

        return conditionMap;
    }

	/**
	 * MissingMeters fetch manager
	 * @param params
	 * @return
	 */
    @SuppressWarnings({ "unchecked", "static-access" })
    public List<Object> getLpMissingMeters2(Map<String, Object> params) {
        String searchStartDate = StringUtil.nullToBlank(params.get("searchStartDate"));
        String searchEndDate = StringUtil.nullToBlank(params.get("searchEndDate"));
        String dateType = StringUtil.nullToBlank(params.get("searchDateType"));
        String supplierId = StringUtil.nullToBlank(params.get("supplierId"));

        // 페이징 시작값 변경

        Map<String, Object> params2 = null;

        params2 = this.getFirstPageForExtjsGrid2(params);

        params2.put("channel", 1);

        // 날시 포멧 M/D/YY 일경우 YYYYMMDD로 변경. 가젯 초기 로드시 날짜 포멧이 잘못 올수 있다.
        if (searchStartDate.matches("[0-9]*/[0-9]*/[0-9]*") || searchEndDate.matches("[0-9]*/[0-9]*/[0-9]*")) {
            searchStartDate = ""; // 비워놓으면 밑에서 현재 날짜로 초기화 한다.
            searchEndDate = "";
        }

        String currentDate = TimeUtil.getCurrentDateUsingFormat("yyyymmdd");
        if ("".equals(searchStartDate)) {
            params2.put("searchStartDate", currentDate);
        }
        if ("".equals(searchEndDate)) {
            params2.put("searchEndDate", currentDate);
        }
        if ("".equals(dateType) || CommonConstants.DateType.HOURLY.getCode().equals(dateType)) {
            dateType = CommonConstants.DateType.DAILY.getCode();
            params2.put("dateType", dateType);
        }

        // missing lp list fetch
        List<Object> missingLpList = meterDao.getMissingMeters2(params2);

        if (supplierId.length() > 0) {
            Supplier supplier = supplierDao.get(Integer.parseInt(supplierId));
            DecimalFormat dfMd = DecimalUtil.getMDStyle(supplier.getMd());
            String lang = supplier.getLang().getCode_2letter();
            String country = supplier.getCountry().getCode_2letter();
            String curPage = (String) params2.get("page");
            String pageSize = (String) params2.get("pageSize");
            int idx = 1;
            for (Object data : missingLpList) {
                Map<String, Object> mapData = (Map<String, Object>) data;
                mapData.put("lastReadDate",
                        TimeLocaleUtil.getLocaleDate(StringUtil.nullToBlank(mapData.get("lastReadDate")), lang, country));

                // 페이지에 따른 페이지 인덱스 설정.
                // 인덱스값 설정.
                mapData.put("idx", dfMd.format(CommonUtils2.makeIdxPerPage(curPage, pageSize, idx)));

                idx++;
            }
        }

        return missingLpList;
    }

	@SuppressWarnings("unused")
    public String getLpMissingMetersTotalCnt(Map<String, Object> params) {
        String searchStartDate = StringUtil.nullToBlank(params.get("searchStartDate"));
        String searchEndDate = StringUtil.nullToBlank(params.get("searchEndDate"));
        String dateType = StringUtil.nullToBlank(params.get("searchDateType"));
        String supplierId = StringUtil.nullToBlank(params.get("supplierId"));

        params.put("channel", 1);

        // 날시 포멧 M/D/YY 일경우 YYYYMMDD로 변경. 가젯 초기 로드시 날짜 포멧이 잘못 올수 있다.
        if (searchStartDate.matches("[0-9]*/[0-9]*/[0-9]*") || searchEndDate.matches("[0-9]*/[0-9]*/[0-9]*")) {
            searchStartDate = ""; // 비워놓으면 밑에서 현재 날짜로 초기화 한다.
            searchEndDate = "";
        }

        String currentDate = TimeUtil.getCurrentDateUsingFormat("yyyymmdd");
        if ("".equals(searchStartDate)) {
            params.put("searchStartDate", currentDate);
        }
        if ("".equals(searchEndDate)) {
            params.put("searchEndDate", currentDate);
        }
        if ("".equals(dateType) || CommonConstants.DateType.HOURLY.getCode().equals(dateType)) {
            dateType = CommonConstants.DateType.DAILY.getCode();
            params.put("dateType", dateType);
        }

        // missing lp list Count fetch
        String totalCnt = meterDao.getMissingMetersTotalCnt(params);

        return totalCnt;
    }

    @SuppressWarnings("unchecked")
    public int getLpMissingMetersListCnt(Map<String, Object> params) {
        String searchStartDate = StringUtil.nullToBlank(params.get("searchStartDate"));
        String searchEndDate = StringUtil.nullToBlank(params.get("searchEndDate"));
        String dateType = StringUtil.nullToBlank(params.get("searchDateType"));
        String supplierId = StringUtil.nullToBlank(params.get("supplierId"));

        params.put("channel", 1);

        // 날시 포멧 M/D/YY 일경우 YYYYMMDD로 변경. 가젯 초기 로드시 날짜 포멧이 잘못 올수 있다.
        if (searchStartDate.matches("[0-9]*/[0-9]*/[0-9]*") || searchEndDate.matches("[0-9]*/[0-9]*/[0-9]*")) {
            searchStartDate = ""; // 비워놓으면 밑에서 현재 날짜로 초기화 한다.
            searchEndDate = "";
        }

        String currentDate = TimeUtil.getCurrentDateUsingFormat("yyyymmdd");
        if ("".equals(searchStartDate)) {
            params.put("searchStartDate", currentDate);
        }
        if ("".equals(searchEndDate)) {
            params.put("searchEndDate", currentDate);
        }
        if ("".equals(dateType) || CommonConstants.DateType.HOURLY.getCode().equals(dateType)) {
            dateType = CommonConstants.DateType.DAILY.getCode();
            params.put("dateType", dateType);
        }

        List<Object> missingLpList = meterDao.getMissingMeters(params);

        if (supplierId.length() > 0) {
            Supplier supplier = supplierDao.get(Integer.parseInt(supplierId));
            for (Object data : missingLpList) {
                Map<String, Object> mapData = (Map<String, Object>) data;
                mapData.put("lastReadDate", TimeLocaleUtil.getLocaleDate(StringUtil.nullToBlank(mapData.get("lastReadDate")),
                        supplier.getLang().getCode_2letter(), supplier.getCountry().getCode_2letter()));
            }
        }

        int page = Integer.parseInt(StringUtil.nullToZero(params.get("page")));
        int pageSize = CommonConstants.Paging.ROWPERPAGE_100.getPageNum();
        int subFrom = page * pageSize;
        int subTo = missingLpList.size() > (subFrom + pageSize) ? subFrom + pageSize : missingLpList.size();

        List<Object> result = new ArrayList<Object>();
        result.add(missingLpList.size());
        result.add(missingLpList.subList(subFrom, subTo));

        return result.size();

    }

	/**
	 * Excel 저장용 lp 누락된 미터 목록을 조회한다.
	 * @param params
	 * @return
	 */
    @SuppressWarnings("unchecked")
    public List<Object> getLpMissingMetersExcel(Map<String, Object> params) {
        String searchStartDate = StringUtil.nullToBlank(params.get("searchStartDate"));
        String searchEndDate = StringUtil.nullToBlank(params.get("searchEndDate"));
        String dateType = StringUtil.nullToBlank(params.get("searchDateType"));
        String supplierId = StringUtil.nullToBlank(params.get("supplierId"));

        params.put("channel", 1);

        // 날시 포멧 M/D/YY 일경우 YYYYMMDD로 변경. 가젯 초기 로드시 날짜 포멧이 잘못 올수 있다.
        if (searchStartDate.matches("[0-9]*/[0-9]*/[0-9]*") || searchEndDate.matches("[0-9]*/[0-9]*/[0-9]*")) {
            searchStartDate = ""; // 비워놓으면 밑에서 현재 날짜로 초기화 한다.
            searchEndDate = "";
        }

        String currentDate = TimeUtil.getCurrentDateUsingFormat("yyyymmdd");
        if ("".equals(searchStartDate)) {
            params.put("searchStartDate", currentDate);
        }
        if ("".equals(searchEndDate)) {
            params.put("searchEndDate", currentDate);
        }
        if ("".equals(dateType) || CommonConstants.DateType.HOURLY.getCode().equals(dateType)) {
            dateType = CommonConstants.DateType.DAILY.getCode();
            params.put("dateType", dateType);
        }

        List<Object> missingLpList = meterDao.getMissingMeters(params);

        if (supplierId.length() > 0) {
            Supplier supplier = supplierDao.get(Integer.parseInt(supplierId));
            for (Object data : missingLpList) {
                Map<String, Object> mapData = (Map<String, Object>) data;
                mapData.put("lastReadDate", TimeLocaleUtil.getLocaleDate(StringUtil.nullToBlank(mapData.get("lastReadDate")),
                        supplier.getLang().getCode_2letter(), supplier.getCountry().getCode_2letter()));
            }
        }

        return missingLpList;
    }

	/**
	 * 미터의 누락건수를 일별/시간별로 조회한다.
	 * @param params
	 * @return
	 */
	@SuppressWarnings("unchecked")
    public List<Object> getLpMissingCount(Map<String, Object> params) {
        String searchStartDate = StringUtil.nullToBlank(params.get("searchStartDate"));
        String searchEndDate = StringUtil.nullToBlank(params.get("searchEndDate"));
        String dateType = StringUtil.nullToBlank(params.get("searchDateType"));

        // 특정일자에대해서 시간별로 조회하기 위한 필드
        String yyyymmdd = StringUtil.nullToBlank(params.get("yyyymmdd"));

        params.put("channel", 1);

        String currentDate = TimeUtil.getCurrentDateUsingFormat("yyyymmdd");
        if ("".equals(searchStartDate)) {
            params.put("searchStartDate", currentDate);
        }
        if ("".equals(searchEndDate)) {
            params.put("searchEndDate", currentDate);
        }
        if ("".equals(dateType) || CommonConstants.DateType.HOURLY.getCode().equals(dateType)) {
            dateType = CommonConstants.DateType.DAILY.getCode();
            params.put("dateType", dateType);
        }
        Map<String, Object> resultMap = null;

        if (yyyymmdd != null && yyyymmdd.length() > 0) {
            resultMap = meteringlpDao.getMissingCountByHour(params);
        } else {
            resultMap = meteringlpDao.getMissingCountByDay(params);
        }
        return (List<Object>) resultMap.get("resultList");
    }
}