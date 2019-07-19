package com.aimir.service.mvm.impl;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aimir.constants.CommonConstants.DateType;
import com.aimir.dao.device.MbusSlaveIOModuleDao;
import com.aimir.dao.device.MeterDao;
import com.aimir.dao.mvm.MeteringDataNMDao;
import com.aimir.dao.system.CodeDao;
import com.aimir.dao.system.LocationDao;
import com.aimir.dao.system.SupplierDao;
import com.aimir.model.system.Location;
import com.aimir.model.system.Supplier;
import com.aimir.service.mvm.NetStationMonitoringManager;
import com.aimir.service.mvm.bean.MeteringFailureData;
import com.aimir.util.DecimalUtil;
import com.aimir.util.TimeLocaleUtil;

/**
 * SP-929
 * for Net-station Monitoring
 */
@Service(value = "netStationMonitoringManager")
@SuppressWarnings("unchecked")
public class NetStationMonitoringManagerImpl implements NetStationMonitoringManager {

    Log logger = LogFactory.getLog(NetStationMonitoringManagerImpl.class);
    

    
    @Autowired
    MeterDao meterDao;
    
    @Autowired
    LocationDao locationDao;

    @Autowired
    MeteringDataNMDao meteringDataNMDao;
    
    @Autowired
    CodeDao codeDao;
    
    @Autowired
    SupplierDao supplierDao;
    
    @Autowired
    MbusSlaveIOModuleDao mbusSlaveIOModuleDao;

    /**
     *
     * @param conditionMap
     * @return
     */
    public List<Map<String, Object>> getMeteringDataHourlyData(Map<String, Object> conditionMap) {
        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
        Integer supplierId = (Integer)conditionMap.get("supplierId");
        Integer page = (Integer)conditionMap.get("page");
        Integer limit = (Integer)conditionMap.get("limit");
        Integer locationId = (Integer)conditionMap.get("locationId");
        Integer permitLocationId = (Integer)conditionMap.get("permitLocationId");
        List<Integer> locationIdList = null;

        if (locationId != null) {
            locationIdList = locationDao.getChildLocationId(locationId);
            locationIdList.add(locationId);
            conditionMap.put("locationIdList", locationIdList);
        } else if (permitLocationId != null) {
            locationIdList = locationDao.getChildLocationId(permitLocationId);
            locationIdList.add(permitLocationId);
            conditionMap.put("locationIdList", locationIdList);
        }

        String searchStartDate = (String)conditionMap.get("searchStartDate");
        String searchStartHour = (String)conditionMap.get("searchStartHour");
        String searchEndDate = (String)conditionMap.get("searchEndDate");
        String searchEndHour = (String)conditionMap.get("searchEndHour");

        conditionMap.put("startDate", searchStartDate + searchStartHour + "0000");
        conditionMap.put("endDate", searchEndDate + searchEndHour + "5959");

        List<Map<String, Object>> list = meteringDataNMDao.getMeteringData(conditionMap, false);

        Map<String, Object> map = null;
        Supplier supplier = supplierDao.get(supplierId);
        String country = supplier.getCountry().getCode_2letter();
        String lang    = supplier.getLang().getCode_2letter();
        DecimalFormat mdf = DecimalUtil.getDecimalFormat(supplier.getMd());

        int num = 0;

        if (page != null && limit != null) {
            num = ((page - 1) * limit) + 1;
        } else {
            num = 1;
        }

        for (Map<String, Object> obj : list) {
            Object tmpObj = null;
            map = new HashMap<String, Object>();
            map.put("num", num++);
            String tmpStr = (String)obj.get("YYYYMMDDHHMMSS") != null ? (String)obj.get("YYYYMMDDHHMMSS") : "";
            map.put("meteringTime", TimeLocaleUtil.getLocaleDate(tmpStr, lang, country));
//       fields : ["num", "meteringTime", "degital", "analogCur","cnvAnalogCur", "analogVol", "cnvAnalogVol", "meterNo", "modemId"],

            map.put("meterNo", (String)obj.get("METER_NO"));
            map.put("modemId", (String)obj.get("MODEM_ID"));
            tmpStr = "";
            if (  obj.get("CHANNEL_1") != null ) {
                BigDecimal value = new BigDecimal(DecimalUtil.ConvertNumberToDouble(obj.get("CHANNEL_1")));
                BigDecimal r = value.setScale(0, RoundingMode.HALF_DOWN);
                tmpStr = r.toPlainString();
            }
            map.put("channel_1", tmpStr );
            map.put("channel_2", obj.get("CHANNEL_2") == null ? "" : mdf.format(DecimalUtil.ConvertNumberToDouble(obj.get("CHANNEL_2"))));
            map.put("channel_3", obj.get("CHANNEL_3") == null ? "" : mdf.format(DecimalUtil.ConvertNumberToDouble(obj.get("CHANNEL_3"))));
            map.put("channel_4", obj.get("CHANNEL_4") == null ? "" : mdf.format(DecimalUtil.ConvertNumberToDouble(obj.get("CHANNEL_4"))));
            map.put("channel_5", obj.get("CHANNEL_5") == null ? "" : mdf.format(DecimalUtil.ConvertNumberToDouble(obj.get("CHANNEL_5"))));
            result.add(map);
        }

        return result;
    }

    public Integer getMeteringDataHourlyDataTotalCount(Map<String, Object> conditionMap) {

        Integer supplierId = (Integer)conditionMap.get("supplierId");
        Integer page = (Integer)conditionMap.get("page");
        Integer limit = (Integer)conditionMap.get("limit");
        Integer locationId = (Integer)conditionMap.get("locationId");
        Integer permitLocationId = (Integer)conditionMap.get("permitLocationId");
        List<Integer> locationIdList = null;

        if (locationId != null) {
            locationIdList = locationDao.getChildLocationId(locationId);
            locationIdList.add(locationId);
            conditionMap.put("locationIdList", locationIdList);
        } else if (permitLocationId != null) {
            locationIdList = locationDao.getChildLocationId(permitLocationId);
            locationIdList.add(permitLocationId);
            conditionMap.put("locationIdList", locationIdList);
        }

        String searchStartDate = (String)conditionMap.get("searchStartDate");
        String searchStartHour = (String)conditionMap.get("searchStartHour");
        String searchEndDate = (String)conditionMap.get("searchEndDate");
        String searchEndHour = (String)conditionMap.get("searchEndHour");

        conditionMap.put("startDate", searchStartDate + searchStartHour + "0000");
        conditionMap.put("endDate", searchEndDate + searchEndHour + "5959");

        List<Map<String, Object>> result = meteringDataNMDao.getMeteringData(conditionMap, true);
        return (Integer)(result.get(0).get("total"));
    }
    
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> getMeteringDataDetail(Map<String, Object> conditionMap) {
        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
        Integer supplierId = (Integer)conditionMap.get("supplierId");
        String[] channelArray = ((String)conditionMap.get("channel")).split(",");

        Map<String, Object> map = null;
        Supplier supplier = supplierDao.get(supplierId);
        String country = supplier.getCountry().getCode_2letter();
        String lang = supplier.getLang().getCode_2letter();
        DecimalFormat mdf = DecimalUtil.getDecimalFormat(supplier.getMd());

        List<Map<String, Object>> list = meteringDataNMDao.getMeteringDataDetail(conditionMap);

        int row = 0;
        for (Map<String, Object> tmpMap : list) {
            map = new HashMap<String, Object>();
            map.put("id", (String)tmpMap.get("YYYYMMDDHHMMSS"));
            map.put("iconCls", "no-icon");
            map.put("meteringTime", TimeLocaleUtil.getLocaleDate((String)tmpMap.get("YYYYMMDDHHMMSS"), lang, country));
            map.put("dst", tmpMap.get("DST"));
            

            for ( int i = 0; i < channelArray.length; i++){
                String key = "CHANNEL_" + channelArray[i];
                if ( key.equals("CHANNEL_1")){ 
                    String tmpStr = "";
                   if ( tmpMap.get(key) != null ) {
                        BigDecimal value = new BigDecimal(DecimalUtil.ConvertNumberToDouble(tmpMap.get(key)));
                        BigDecimal r = value.setScale(0, RoundingMode.HALF_DOWN);
                        tmpStr = r.toPlainString();
                   }
                    map.put(key.toLowerCase(), tmpStr );
                }
                else {
                    map.put(key.toLowerCase(), tmpMap.get(key) == null ? "" : mdf.format(DecimalUtil.ConvertNumberToDouble(tmpMap.get(key))));
                }
            }
            map.put("expanded", false);
            map.put("leaf" ,true);
            result.add(map);
            row++;
        }


        return result;        
    }
//    
    public Map<String, Object> getMeteringDataDetailChartData(Map<String, Object> conditionMap) {
        Map<String, Object> result = new HashMap<String, Object>();
        List<Map<String,Object>> searchData = new ArrayList<Map<String, Object>>();
        List<Map<String,Object>> searchAddData = new ArrayList<Map<String, Object>>();
        Integer supplierId = (Integer)conditionMap.get("supplierId");
        String searchType = (String)conditionMap.get("searchType");

        Map<String, Object> map = null;
        Supplier supplier = supplierDao.get(supplierId);
        String country = supplier.getCountry().getCode_2letter();
        String lang = supplier.getLang().getCode_2letter();
        DecimalFormat mdf = DecimalUtil.getDecimalFormat(supplier.getMd());

        List<Map<String, Object>> list = meteringDataNMDao.getMeteringDataDetail(conditionMap);
        if (list == null || list.size() <= 0) {
            result.put("searchData", searchData);
            result.put("searchAddData", searchAddData);
            return result;
        }

        int di = 0;
        for (Map<String, Object> tmpMap : list) {
            String tmpDate  = (String)tmpMap.get("YYYYMMDDHHMMSS");
            String reportDate = TimeLocaleUtil.getLocaleDate(tmpDate, lang, country);
            String localDate = reportDate;
            if (DateType.HOURLY.getCode().equals(searchType)){
                localDate = TimeLocaleUtil.getLocaleTime(tmpDate, lang, country);
            }
            for ( int i = 1; i <= 5; i++ ){
                map = new HashMap<String, Object>();
                map.put("date", localDate);
                map.put("localeDate", localDate);
                map.put("channel", new Integer(i));
                map.put("reportDate", reportDate);
                String key = "CHANNEL_" + String.valueOf(i);
                map.put("value", tmpMap.get(key));
                map.put("decimalValue", tmpMap.get(key) == null ? mdf.format(0D) : mdf.format(DecimalUtil.ConvertNumberToDouble(tmpMap.get(key))));
                searchData.add(map);
            }
            di++;
        }
        result.put("searchData", searchData);
        result.put("searchAddData", searchAddData);

        return result;
    }
    

    public List<MeteringFailureData> getMbusSlaveIoModuleCountListPerLocation(Map<String, Object> conditionMap) {
        Integer supplierId = (Integer)conditionMap.get("supplierId");
        Integer locationId = (Integer)conditionMap.get("locationId");
        Integer permitLocationId = (Integer)conditionMap.get("permitLocationId");
        Integer pLocationId = null;
//        String meterType = (String)conditionMap.get("meterType");
        boolean isRoot = false;
        Boolean isParent = (Boolean)conditionMap.get("isParent");
//        MeterType constMeterType = MeterType.valueOf(meterType);
        MeteringFailureData meteringFailureData = null;
        MeteringFailureData locationData = null;
        List<MeteringFailureData> meteringFailureList = new ArrayList<MeteringFailureData>();

        // root location list
        List<Location> rootLocationList = new ArrayList<Location>();

        if (permitLocationId == null) {
            rootLocationList = locationDao.getRootLocationListBySupplier(supplierId);
        } else {
            rootLocationList.add(locationDao.get(permitLocationId));
        }

        // location list
        List<Map<String, Object>> locationList = locationDao.getLocationTreeForMeteringRate(supplierId);
        // meter count per location
        List<Map<String, Object>> meterCountList = mbusSlaveIOModuleDao.getMbusSlaveIOModuleCountListPerLocation(conditionMap);
        // metering success count per location
        List<Map<String, Object>> meteringSuccessCountList = null;

        meteringSuccessCountList = meteringDataNMDao.getMeteringSuccessCountListPerLocation(conditionMap);

        Map<String, Object> totalCountMap = new HashMap<String, Object>();
        Map<String, Object> successCountMap = new HashMap<String, Object>();

        String locKey = "LOC_ID";
        String meterKey = "METER_CNT";
        String successKey = "SUCCESS_CNT";
        
        for (Map<String, Object> obj : meterCountList) {
            Object loc = (obj.get(locKey) != null) ?  obj.get(locKey) : obj.get(locKey.toLowerCase());
            Object meter = (obj.get(meterKey) != null) ? obj.get(meterKey) : obj.get(meterKey.toLowerCase());
            totalCountMap.put("loc_" + loc.toString(), DecimalUtil.ConvertNumberToInteger(meter));
        }

        for (Map<String, Object> obj : meteringSuccessCountList) {
            Object loc = (obj.get(locKey) != null)? obj.get(locKey) : obj.get(locKey.toLowerCase()); 
            Object success = (obj.get(successKey) != null ) ? obj.get(successKey) : obj.get(successKey.toLowerCase());
            successCountMap.put("loc_" + loc.toString(), DecimalUtil.ConvertNumberToInteger(success));
        }

        if (locationId == null) {
            isRoot = true;
        } else {
            if (isParent) {
                pLocationId = locationId;
                isRoot = false;
            } else {
                if (permitLocationId != null && locationId.equals(permitLocationId)) {
                    isRoot = true;
                } else {
                    for (Map<String, Object> obj : locationList) {
                        if (locationId.equals(DecimalUtil.ConvertNumberToInteger(obj.get("C_ID")))) {
                            pLocationId = DecimalUtil.ConvertNumberToInteger(obj.get("P_ID"));
                            break;
                        }
                    }

                    if (pLocationId == null) {  // 상위지역이 없을 경우 - 최상위지역
                        isRoot = true;
                    } else {
                        isRoot = false;
                    }
                }
            }
        }

        if (isRoot) {
            for (Location loc : rootLocationList) {
                meteringFailureData = null;
                locationData = new MeteringFailureData();
                locationData.setLocationId(loc.getId().toString());
                locationData.setLocationName(loc.getName());
                locationData.setParent("");
                meteringFailureData = getChildrenMeteringData(locationList, totalCountMap, successCountMap, locationData);
                meteringFailureList.add(meteringFailureData);
            }
        } else {
            for (Map<String, Object> obj : locationList) {
                if (pLocationId.equals(DecimalUtil.ConvertNumberToInteger(obj.get("P_ID")))) {
                    meteringFailureData = null;
                    locationData = new MeteringFailureData();
                    locationData.setLocationId(obj.get("C_ID").toString());
                    locationData.setLocationName(obj.get("C_NAME").toString());
                    locationData.setParent(obj.get("P_ID").toString());
                    meteringFailureData = getChildrenMeteringData(locationList, totalCountMap, successCountMap, locationData);
                    meteringFailureList.add(meteringFailureData);
                }
            }
        }

        return meteringFailureList;
    }  

    /**
     * method name : getChildrenMeteringData<b/>
     * method Desc :
     *
     * @param locationList
     * @param totalCountMap
     * @param successCountMap
     * @param locInfo
     * @return
     */
    private MeteringFailureData getChildrenMeteringData(List<Map<String, Object>> locationList,
            Map<String, Object> totalCountMap, Map<String, Object> successCountMap, MeteringFailureData locInfo) {
        MeteringFailureData meteringFailureData = new MeteringFailureData();
        MeteringFailureData childData = new MeteringFailureData();
        MeteringFailureData locationData = null;
        List<MeteringFailureData> childrenList = new ArrayList<MeteringFailureData>();
        Integer totalCount = 0;
        Integer successCount = 0;
        Integer locId = Integer.valueOf(locInfo.getLocationId());
        String locName = locInfo.getLocationName();
        String locParentId = locInfo.getParent();
        BigDecimal bdTotal = null;
        BigDecimal bdSuccessRate = null;
        BigDecimal bdFailureRate = null;

        for (Map<String, Object> obj : locationList) {
            if (locId.equals(DecimalUtil.ConvertNumberToInteger(obj.get("P_ID")))) {
                locationData = new MeteringFailureData();
                locationData.setLocationId(obj.get("C_ID").toString());
                locationData.setLocationName(obj.get("C_NAME").toString());
                childData = getChildrenMeteringData(locationList, totalCountMap, successCountMap, locationData);

                totalCount = totalCount + Integer.valueOf(childData.getTotalCount());
                successCount = successCount + Integer.valueOf(childData.getSuccessCount());

                childrenList.add(childData);
            }
        }

        meteringFailureData = new MeteringFailureData();
        meteringFailureData.setLocationId(locId.toString());
        meteringFailureData.setLocationName(locName);
        meteringFailureData.setParent(locParentId);
        if (totalCountMap.get("loc_" + locId.toString()) != null) {
            totalCount = totalCount + (Integer)totalCountMap.get("loc_" + locId.toString());
        }
        meteringFailureData.setTotalCount(totalCount.toString());
        if (successCountMap.get("loc_" + locId.toString()) != null) {
            successCount = successCount + (Integer)successCountMap.get("loc_" + locId.toString());
        }
        meteringFailureData.setSuccessCount(successCount.toString());
        Integer failureCount = totalCount - successCount;
        meteringFailureData.setFailureCount(failureCount.toString());

        bdTotal = new BigDecimal(totalCount);
        if (totalCount == 0) {
            bdSuccessRate = new BigDecimal(0);
            bdFailureRate = new BigDecimal(0);
        } else {
            bdSuccessRate = new BigDecimal(successCount).divide(bdTotal, MathContext.DECIMAL128).multiply(new BigDecimal(100)).setScale(2,BigDecimal.ROUND_DOWN);
            bdFailureRate = new BigDecimal(100).subtract(bdSuccessRate);
        }

        meteringFailureData.setSuccessRate(bdSuccessRate.toString());
        meteringFailureData.setFailureRate(bdFailureRate.toString());
        meteringFailureData.setSuccess(bdFailureRate.toString());

        // 라벨생성
        String label = bdSuccessRate.toString() + "% (" + successCount + "/" + totalCount + ")";
        meteringFailureData.setLabel(label);
        meteringFailureData.setSuccessFailYn("0");

        if (childrenList == null || childrenList.size() == 0) {
            meteringFailureData.setChildren(null);
            meteringFailureData.setIsBranch("false");
            meteringFailureData.setLeaf(true);
            
        } else {
            meteringFailureData.setChildren(childrenList);
            meteringFailureData.setIsBranch("true");
            meteringFailureData.setLeaf(false);
        }

        return meteringFailureData;
    }
    
    public List<MeteringFailureData> getMeteringSuccessRateListWithChildren(Map<String, Object> conditionMap) {
        Integer supplierId = (Integer)conditionMap.get("supplierId");
        Integer permitLocationId = (Integer)conditionMap.get("permitLocationId");
        Map<String, Object> totalCountMap = new HashMap<String, Object>();
        Map<String, Object> successCountMap = new HashMap<String, Object>();

        // root location list
        List<Location> rootLocationList = new ArrayList<Location>();

        if (permitLocationId == null) {
            rootLocationList = locationDao.getRootLocationListBySupplier(supplierId);
        } else {
            rootLocationList.add(locationDao.get(permitLocationId));
        }

        // location list
        List<Map<String, Object>> locationList = locationDao.getLocationTreeForMeteringRate(supplierId);
        // meter count per location
        List<Map<String, Object>> meterCountList = mbusSlaveIOModuleDao.getMbusSlaveIOModuleCountListPerLocation(conditionMap);
        List<Map<String, Object>> meteringSuccessCountList = null;


        meteringSuccessCountList = meteringDataNMDao.getMeteringSuccessCountListPerLocation(conditionMap);

        for (Map<String, Object> obj : meterCountList) {
            totalCountMap.put("loc_" + obj.get("LOC_ID").toString(), DecimalUtil.ConvertNumberToInteger(obj.get("METER_CNT")));
        }

        for (Map<String, Object> obj : meteringSuccessCountList) {
            successCountMap.put("loc_" + obj.get("LOC_ID").toString(), DecimalUtil.ConvertNumberToInteger(obj.get("SUCCESS_CNT")));
        }

        Integer totalCount = 0;
        Integer successCount = 0;
        BigDecimal bdTotal = null;
        BigDecimal bdSuccessRate = null;
        BigDecimal bdFailureRate = null;
        MeteringFailureData meteringFailureData = null;
        MeteringFailureData locationData = null;
        MeteringFailureData meteringTotalData = new MeteringFailureData();
        List<MeteringFailureData> meteringFailureList = new ArrayList<MeteringFailureData>();

        for (Location loc : rootLocationList) {
            meteringFailureData = null;
            locationData = new MeteringFailureData();
            locationData.setLocationId(loc.getId().toString());
            locationData.setLocationName(loc.getName());
            meteringFailureData = getChildrenMeteringData(locationList, totalCountMap, successCountMap, locationData);

            totalCount = totalCount + Integer.valueOf(meteringFailureData.getTotalCount());
            successCount = successCount + Integer.valueOf(meteringFailureData.getSuccessCount());
            meteringFailureList.add(meteringFailureData);
        }

        bdTotal = new BigDecimal(totalCount);
        if (totalCount == 0) {
            bdSuccessRate = new BigDecimal(0);
            bdFailureRate = new BigDecimal(0);
        } else {
            bdSuccessRate = new BigDecimal(successCount).divide(bdTotal, MathContext.DECIMAL128).multiply(new BigDecimal(100)).setScale(2,BigDecimal.ROUND_DOWN);
            bdFailureRate = new BigDecimal(100).subtract(bdSuccessRate);
        }

        meteringTotalData.setSuccessRate(bdSuccessRate.toString());
        meteringTotalData.setFailureRate(bdFailureRate.toString());
        meteringTotalData.setSuccess(bdSuccessRate.toString());
        meteringTotalData.setLocationName("Total");
        meteringTotalData.setChildren(null);
        meteringTotalData.setIsBranch("false");
        meteringTotalData.setLeaf(true);


        String label = bdSuccessRate.toString() + "% (" + successCount + "/" + totalCount + ")";
        meteringTotalData.setLabel(label);
        meteringFailureList.add(meteringTotalData);

        return meteringFailureList;
    }
}