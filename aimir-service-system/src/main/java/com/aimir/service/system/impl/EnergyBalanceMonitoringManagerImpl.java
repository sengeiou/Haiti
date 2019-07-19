package com.aimir.service.system.impl;

import java.math.BigDecimal;
import java.math.MathContext;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.jws.WebService;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.aimir.constants.CommonConstants.DistTrfmrSubstationMeterPhase;
import com.aimir.dao.device.DistTrfmrSubstationDao;
import com.aimir.dao.device.MeterDao;
import com.aimir.dao.system.CodeDao;
import com.aimir.dao.system.ContractDao;
import com.aimir.dao.system.LocationDao;
import com.aimir.dao.system.SupplierDao;
import com.aimir.model.device.DistTrfmrSubstation;
import com.aimir.model.device.Meter;
import com.aimir.model.system.Location;
import com.aimir.model.system.Supplier;
import com.aimir.service.system.EnergyBalanceMonitoringManager;
import com.aimir.util.CalendarUtil;
import com.aimir.util.Condition;
import com.aimir.util.Condition.Restriction;
import com.aimir.util.DecimalUtil;
import com.aimir.util.StringUtil;
import com.aimir.util.TimeLocaleUtil;
import com.aimir.util.TimeUtil;

/**
 * EnergyBalanceMonitoringManagerImpl.java Description 
 * <p>
 * <pre>
 * Date          Version     Author   Description
 * 2012. 3. 12.  v1.0        문동규   Energy Balance Monitoring Service Impl
 * </pre>
 */

@WebService(endpointInterface = "com.aimir.service.system.EnergyBalanceMonitoringManager")
@Service(value = "energyBalanceMonitoringManager")
public class EnergyBalanceMonitoringManagerImpl implements EnergyBalanceMonitoringManager {

    protected static Log log = LogFactory.getLog(EnergyBalanceMonitoringManagerImpl.class);

    @Autowired
    ContractDao contractDao;

    @Autowired
    SupplierDao supplierDao;

    @Autowired
    LocationDao locationDao;
    
    @Autowired
    DistTrfmrSubstationDao dtsDao;

    @Autowired
    MeterDao meterDao;

    @Autowired
    CodeDao codeDao;

    /**
     * method name : getCurrentMonthSearchCondition<b/>
     * method Desc : Energy Balance Monitoring 미니가젯의 조회월 타이틀 및 조회일자 조건을 가져온다. 
     *
     * @param supplierId
     * @return
     */
    public Map<String, Object> getCurrentMonthSearchCondition(Integer supplierId) {
        Map<String, Object> result = new HashMap<String, Object>();
        Supplier supplier = supplierDao.get(supplierId);
        String lang = supplier.getLang().getCode_2letter();
        String country = supplier.getCountry().getCode_2letter();

        String currentDate = null;
        String currentMonth = null;
        String currentMonthFormatted = null;

        try {
            currentDate = TimeUtil.getCurrentDay();
            currentMonth = currentDate.substring(0, 6);
//            currentMonth = "201202";
            
            currentMonthFormatted = TimeLocaleUtil.getLocaleDateByMediumFormat(currentMonth, lang, country);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        String startDate = currentMonth + "01";
        String endDate = currentMonth + CalendarUtil.getMonthLastDate(currentMonth.substring(0, 4), currentMonth.substring(4));
        
        result.put("currentMonth", currentMonthFormatted);
        result.put("startDate", startDate);
        result.put("endDate", endDate);
        return result;
    }

    /**
     * method name : getEbsSuspectedDtsList<b/>
     * method Desc : Energy Balance Monitoring 미니가젯에서 Suspected Substation List 를 조회한다.
     *
     * @param conditionMap
     * @return
     */
    public List<Map<String, Object>> getEbsSuspectedDtsList(Map<String, Object> conditionMap) {
        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
        List<Map<String, Object>> list = dtsDao.getEbsSuspectedDtsList(conditionMap, false);
        Map<String, Object> map = new HashMap<String, Object>();

        Supplier supplier = supplierDao.get((Integer)conditionMap.get("supplierId"));
        DecimalFormat mdf = DecimalUtil.getDecimalFormat(supplier.getMd());
        Double importEnergy = null;
        Double consumeEnergy = null;
        Double threshold = null;
        BigDecimal imp = null;
        BigDecimal thr = null;
        BigDecimal minImp = null;

        if (list != null) {
            for (Map<String, Object> obj : list) {
                map = new HashMap<String, Object>();
//                map.put("dtsName", obj.get("DTS_NAME"));
//                map.put("importEnergyTotal", mdf.format(StringUtil.nullToDoubleZero(importEnergy)));

                importEnergy = DecimalUtil.ConvertNumberToDouble(obj.get("IMPORT_ENERGY_TOTAL"));
                consumeEnergy = DecimalUtil.ConvertNumberToDouble(obj.get("CONSUME_ENERGY_TOTAL"));
                threshold = DecimalUtil.ConvertNumberToDouble(obj.get("THRESHOLD"));

                imp = new BigDecimal(importEnergy);
                thr = new BigDecimal(threshold);
                minImp = imp.subtract(imp.multiply(thr.divide(new BigDecimal(100), MathContext.DECIMAL32)));

                map.put("dtsName", obj.get("DTS_NAME") + " (" + obj.get("THRESHOLD") + "%)");
                map.put("consumeEnergyTotal", mdf.format(StringUtil.nullToDoubleZero(consumeEnergy)));
                map.put("importEnergyTotal", mdf.format(StringUtil.nullToDoubleZero(importEnergy)) + " (" + mdf.format(StringUtil.nullToDoubleZero(minImp.doubleValue())) + ")");

                result.add(map);
            }
        }
        return result;
    }

    /**
     * method name : getEbsSuspectedDtsListTotalCount<b/>
     * method Desc : Energy Balance Monitoring 미니가젯에서 Suspected Substation List 의 Total Count 를 조회한다.
     *
     * @param conditionMap
     * @return
     */
    public Integer getEbsSuspectedDtsListTotalCount(Map<String, Object> conditionMap) {
        List<Map<String, Object>> result = dtsDao.getEbsSuspectedDtsList(conditionMap, true);

        return (Integer)(result.get(0).get("total"));
    }

    /**
     * method name : getEbsDtsStateChartData<b/>
     * method Desc : Energy Balance Monitoring 미니가젯에서 Normal/Suspected Substation Count Chart Data 를 조회한다.
     *
     * @param conditionMap
     * @return
     */
    public List<Map<String, Object>> getEbsDtsStateChartData(Map<String, Object> conditionMap) {
        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
        List<Map<String, Object>> list = dtsDao.getEbsDtsStateChartData(conditionMap);
        Map<String, Object> map = new HashMap<String, Object>();

        if (list != null) {
            for (Map<String, Object> obj : list) {
                map = new HashMap<String, Object>();
                map.put("totalCount", obj.get("TOTAL_COUNT"));
                map.put("normalCount", obj.get("NORMAL_COUNT"));
                map.put("suspectedCount", obj.get("SUSPECTED_COUNT"));
                result.add(map);
            }
        }

        return result;
    }

//    /**
//     * method name : getEbsDtsTreeData<b/>
//     * method Desc : Energy Balance Monitoring 맥스가젯의 DTS Tree Data 를 조회한다.
//     *
//     * @param conditionMap
//     * @return
//     */
//    public List<Map<String, Object>> getEbsDtsTreeData(Map<String, Object> conditionMap) {
//        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
//        Integer locationId = (Integer)conditionMap.get("locationId");
//        List<Integer> locationIdList = null;
//
//        if (locationId != null) {
//            locationIdList = locationDao.getChildLocationId(locationId);
//            locationIdList.add(locationId);
//            conditionMap.put("locationIdList", locationIdList);
//        }
//
//        List<Map<String, Object>> locationList = dtsDao.getEbsDtsTreeLocationNodeData(conditionMap);
//        Map<String, Object> locationMap = null;
//
//        for (Map<String, Object> obj : locationList) {
//            locationMap = new HashMap<String, Object>();
//
//            locationMap.put("nodeName", (String)obj.get("LOCATION_NAME"));
//            locationMap.put("nodeCode", DecimalUtil.ConvertNumberToInteger(obj.get("LOCATION_ID")));
//            locationMap.put("threshold", 0);
//            locationMap.put("id", obj.get("LOCATION_ID").toString());
//            locationMap.put("impEnergy", null);
//            locationMap.put("consumeEnergy", null);
//            locationMap.put("suspectedYn", null);
//            locationMap.put("iconCls", "task-folder");
//            locationMap.put("expanded", true);
//
//            conditionMap.put("dtsLocationId", DecimalUtil.ConvertNumberToInteger(obj.get("LOCATION_ID")));
//            locationMap.put("children", getEbsDtsTreeDtsNodeData(conditionMap));
//            result.add(locationMap);
//        }
//
//        return result;
//    }

    /**
     * method name : getEbsDtsTreeData<b/>
     * method Desc : Energy Balance Monitoring 맥스가젯의 DTS Tree Data 를 조회한다.
     *
     * @param conditionMap
     * @return
     */
    public List<Map<String, Object>> getEbsDtsTreeData(Map<String, Object> conditionMap) {
        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
        Integer locationId = (Integer)conditionMap.get("locationId");
        List<Integer> locationIdList = null;
        
        if (locationId != null && !locationDao.isRoot(locationId)) {
            locationIdList = locationDao.getChildLocationId(locationId);
            locationIdList.add(locationId);
            conditionMap.put("locationIdList", locationIdList);
        }

        List<Map<String, Object>> locationList = dtsDao.getEbsDtsTreeLocationNodeData(conditionMap);
        List<Map<String, Object>> dtsList = dtsDao.getEbsDtsTreeDtsNodeData(conditionMap);

        Set<Integer> dtsIds = new HashSet<Integer>();

        for (Map<String, Object> obj : dtsList) {
            dtsIds.add(DecimalUtil.ConvertNumberToInteger(obj.get("DTS_ID")));
        }

        conditionMap.put("dtsIds", dtsIds);

        List<Map<String, Object>> meterList = meterDao.getEbsDtsTreeDtsMeterNodeData(conditionMap);
        Supplier supplier = supplierDao.get((Integer)conditionMap.get("supplierId"));
        DecimalFormat mdf = DecimalUtil.getDecimalFormat(supplier.getMd());

        Map<String, Object> locationMap = null;

        for (Map<String, Object> obj : locationList) {
            locationMap = new HashMap<String, Object>();

            locationMap.put("nodeName", (String)obj.get("LOCATION_NAME"));
            locationMap.put("nodeCode", DecimalUtil.ConvertNumberToInteger(obj.get("LOCATION_ID")));
            locationMap.put("threshold", 0);
            locationMap.put("id", obj.get("LOCATION_ID").toString());
            locationMap.put("impEnergy", "");
            locationMap.put("consumeEnergy", "");
            locationMap.put("suspectedYn", null);
            locationMap.put("iconCls", "task-folder");
            locationMap.put("expanded", true);

            conditionMap.put("dtsLocationId", DecimalUtil.ConvertNumberToInteger(obj.get("LOCATION_ID")));
            locationMap.put("children", makeLocationNodeChildren(dtsList, meterList, DecimalUtil.ConvertNumberToInteger(obj.get("LOCATION_ID")), mdf));
            result.add(locationMap);
        }

        return result;
    }

    private List<Map<String, Object>> makeLocationNodeChildren(List<Map<String, Object>> dtsList, List<Map<String, Object>> meterList, Integer locationId,
            DecimalFormat mdf) {
        List<Map<String, Object>> children = new ArrayList<Map<String, Object>>();
        Map<String, Object> map = new HashMap<String, Object>();
        Map<String, Object> dtsMap = null;
        Double importEnergy = null;
        Double consumeEnergy = null;
        Double threshold = null;
        Boolean suspectedYn = false;
        BigDecimal imp = null;
        BigDecimal thr = null;
        BigDecimal minImp = null;
        int len = dtsList.size();

        for (int i = 0; i < len; i++) {
            map = dtsList.get(i);

            if (locationId.equals(DecimalUtil.ConvertNumberToInteger(map.get("LOCATION_ID")))) {
                dtsMap = new HashMap<String, Object>();

                importEnergy = DecimalUtil.ConvertNumberToDouble(map.get("IMPORT_ENERGY_TOTAL"));
                consumeEnergy = DecimalUtil.ConvertNumberToDouble(map.get("CONSUME_ENERGY_TOTAL"));
                threshold = DecimalUtil.ConvertNumberToDouble(map.get("THRESHOLD"));
                suspectedYn = this.getSuspectedYn(importEnergy, consumeEnergy, threshold);
                imp = new BigDecimal(importEnergy);
                thr = new BigDecimal(threshold);
                minImp = imp.subtract(imp.multiply(thr.divide(new BigDecimal(100), MathContext.DECIMAL32)));

                dtsMap.put("nodeName", (String)map.get("DTS_NAME") + " (" + map.get("THRESHOLD") + "%)");
                dtsMap.put("nodeCode", DecimalUtil.ConvertNumberToInteger(map.get("DTS_ID")));
                dtsMap.put("threshold", threshold);
                dtsMap.put("id", locationId + "_" + map.get("DTS_ID").toString());
                dtsMap.put("impEnergy", mdf.format(StringUtil.nullToDoubleZero(importEnergy)) + " (" + mdf.format(StringUtil.nullToDoubleZero(minImp.doubleValue())) + ")");
                dtsMap.put("consumeEnergy", mdf.format(StringUtil.nullToDoubleZero(consumeEnergy)));
                dtsMap.put("suspectedYn", suspectedYn.toString());
                dtsMap.put("iconCls", "task-substation");
                dtsMap.put("expanded", true);
                dtsMap.put("children", makeDtsNodeChildren(meterList, DecimalUtil.ConvertNumberToInteger(map.get("DTS_ID")), suspectedYn, mdf));
                children.add(dtsMap);
                dtsList.remove(i);
                i--;
                len--;
            }
        }

        return children;
    }

    private List<Map<String, Object>> makeDtsNodeChildren(List<Map<String, Object>> meterList, Integer dtsId,
            Boolean suspectedYn, DecimalFormat mdf) {
        List<Map<String, Object>> children = new ArrayList<Map<String, Object>>();
        List<Map<String, Object>> phaseList = new ArrayList<Map<String, Object>>();
        Map<String, Object> map = new HashMap<String, Object>();
        Map<String, Object> meterMap = new HashMap<String, Object>();
        Map<String, Object> phaseMap = new HashMap<String, Object>();
        int len = meterList.size();

        for (int i = 0; i < len; i++) {
            map = meterList.get(i);

            if (dtsId.equals(DecimalUtil.ConvertNumberToInteger(map.get("DTS_ID")))) {
                meterMap = new HashMap<String, Object>();

                meterMap.put("nodeName", (String) map.get("MDS_ID"));
                meterMap.put("nodeCode", DecimalUtil.ConvertNumberToInteger(map.get("METER_ID")));
                meterMap.put("threshold", 0);
                meterMap.put("id",
                        map.get("LOCATION_ID").toString() + "_" + map.get("DTS_ID").toString() + "_"
                                + map.get("METER_ID").toString());
                meterMap.put("impEnergy", mdf.format(StringUtil.nullToDoubleZero(DecimalUtil.ConvertNumberToDouble(map
                        .get("IMPORT_ENERGY_TOTAL")))));
                meterMap.put("consumeEnergy", mdf.format(StringUtil.nullToDoubleZero(DecimalUtil.ConvertNumberToDouble(map
                        .get("CONSUME_ENERGY_TOTAL")))));
                meterMap.put("suspectedYn", suspectedYn.toString());
                meterMap.put("iconCls", "task-meter");
                meterMap.put("expanded", true);

                phaseMap = new HashMap<String, Object>();
                phaseList = new ArrayList<Map<String, Object>>();

                // Phase A
                phaseMap.put("nodeName", DistTrfmrSubstationMeterPhase.LINE_A.getName());
                phaseMap.put("nodeCode", DistTrfmrSubstationMeterPhase.LINE_A.getCode());
                phaseMap.put("threshold", 0);
                phaseMap.put("id", map.get("LOCATION_ID").toString() + "_" + map.get("DTS_ID").toString() + "_" + map.get("METER_ID").toString() + "_"
                        + DistTrfmrSubstationMeterPhase.LINE_A.getCode());
                phaseMap.put("impEnergy",
                        mdf.format(StringUtil.nullToDoubleZero(DecimalUtil.ConvertNumberToDouble(map.get("IMPORT_PHASE_A")))));
                phaseMap.put("consumeEnergy",
                        mdf.format(StringUtil.nullToDoubleZero(DecimalUtil.ConvertNumberToDouble(map.get("CONSUME_PHASE_A")))));
                phaseMap.put("suspectedYn", suspectedYn.toString());
                phaseMap.put("iconCls", "task-phase");
                phaseList.add(phaseMap);
                // Phase B
                phaseMap = new HashMap<String, Object>();
                phaseMap.put("nodeName", DistTrfmrSubstationMeterPhase.LINE_B.getName());
                phaseMap.put("nodeCode", DistTrfmrSubstationMeterPhase.LINE_B.getCode());
                phaseMap.put("threshold", 0);
                phaseMap.put("id", map.get("LOCATION_ID").toString() + "_" + map.get("DTS_ID").toString() + "_" + map.get("METER_ID").toString() + "_"
                        + DistTrfmrSubstationMeterPhase.LINE_B.getCode());
                phaseMap.put("impEnergy",
                        mdf.format(StringUtil.nullToDoubleZero(DecimalUtil.ConvertNumberToDouble(map.get("IMPORT_PHASE_B")))));
                phaseMap.put("consumeEnergy",
                        mdf.format(StringUtil.nullToDoubleZero(DecimalUtil.ConvertNumberToDouble(map.get("CONSUME_PHASE_B")))));
                phaseMap.put("suspectedYn", suspectedYn.toString());
                phaseMap.put("iconCls", "task-phase");
                phaseList.add(phaseMap);
                // Phase C
                phaseMap = new HashMap<String, Object>();
                phaseMap.put("nodeName", DistTrfmrSubstationMeterPhase.LINE_C.getName());
                phaseMap.put("nodeCode", DistTrfmrSubstationMeterPhase.LINE_C.getCode());
                phaseMap.put("threshold", 0);
                phaseMap.put("id", map.get("LOCATION_ID").toString() + "_" + map.get("DTS_ID").toString() + "_" + map.get("METER_ID").toString() + "_"
                        + DistTrfmrSubstationMeterPhase.LINE_C.getCode());
                phaseMap.put("impEnergy",
                        mdf.format(StringUtil.nullToDoubleZero(DecimalUtil.ConvertNumberToDouble(map.get("IMPORT_PHASE_C")))));
                phaseMap.put("consumeEnergy",
                        mdf.format(StringUtil.nullToDoubleZero(DecimalUtil.ConvertNumberToDouble(map.get("CONSUME_PHASE_C")))));
                phaseMap.put("suspectedYn", suspectedYn.toString());
                phaseMap.put("iconCls", "task-phase");
                phaseList.add(phaseMap);

                meterMap.put("children", phaseList);
                children.add(meterMap);

                meterList.remove(i);
                i--;
                len--;
            }
        }

        return children;
    }    

    /**
     * method name : getEbsDtsTreeContractNodeData<b/>
     * method Desc : Energy Balance Monitoring 맥스가젯의 DTS Tree 의 Contract Node Data 를 조회한다.
     *
     * @param conditionMap
     * @return
     */
    public List<Map<String, Object>> getEbsDtsTreeContractMeterNodeData(Map<String, Object> conditionMap) {
        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
        List<Map<String, Object>> list = meterDao.getEbsDtsTreeContractMeterNodeData(conditionMap);
        String node = (String)conditionMap.get("node");
        Boolean suspectedYn = (Boolean)conditionMap.get("suspectedYn");
        Supplier supplier = supplierDao.get((Integer)conditionMap.get("supplierId"));
        DecimalFormat mdf = DecimalUtil.getDecimalFormat(supplier.getMd());
        Map<String, Object> contractMap = null;

        for (Map<String, Object> obj : list) {
            contractMap = new HashMap<String, Object>();

//            contractMap.put("nodeName", (String)obj.get("MDS_ID") + ((obj.get("CONTRACT_NUMBER") != null) ? " (" + (String)obj.get("CONTRACT_NUMBER") + ")" : ""));
            contractMap.put("nodeName", (String)obj.get("MDS_ID"));
            contractMap.put("nodeCode", DecimalUtil.ConvertNumberToInteger(obj.get("CONT_METER_ID")));
            contractMap.put("threshold", 0);
            contractMap.put("id", node + "_" + obj.get("CONT_METER_ID"));
            contractMap.put("impEnergy", "");
            contractMap.put("consumeEnergy", mdf.format(StringUtil.nullToDoubleZero(DecimalUtil.ConvertNumberToDouble(obj.get("CONSUME_ENERGY_TOTAL")))));
            contractMap.put("suspectedYn", suspectedYn.toString());
            contractMap.put("iconCls", "task");
            contractMap.put("leaf", true);
            contractMap.put("mdsId", (String)obj.get("MDS_ID"));
            contractMap.put("contractNumber", ((obj.get("CONTRACT_NUMBER") != null) ? (String)obj.get("CONTRACT_NUMBER") : ""));
            result.add(contractMap);
        }

        return result;
    }

    /**
     * method name : getEbsDtsList<b/>
     * method Desc : Energy Balance Monitoring 맥스가젯의 DTS List 를 조회한다.
     *
     * @param conditionMap
     * @return
     */
    public List<Map<String, Object>> getEbsDtsList(Map<String, Object> conditionMap) {
        Integer locationId = (Integer)conditionMap.get("locationId");
        List<Integer> locationIdList = null;

        if (locationId != null) {
            locationIdList = locationDao.getChildLocationId(locationId);
            locationIdList.add(locationId);
            conditionMap.put("locationIdList", locationIdList);
        }

        List<Map<String, Object>> result = dtsDao.getEbsDtsList(conditionMap, false);
        return result;
    }

    /**
     * method name : getEbsDtsListTotalCount<b/>
     * method Desc : Energy Balance Monitoring 맥스가젯의 DTS List 의 Total Count 를 조회한다.
     *
     * @param conditionMap
     * @return
     */
    public Integer getEbsDtsListTotalCount(Map<String, Object> conditionMap) {
        Integer locationId = (Integer)conditionMap.get("locationId");
        List<Integer> locationIdList = null;

        if (locationId != null) {
            locationIdList = locationDao.getChildLocationId(locationId);
            locationIdList.add(locationId);
            conditionMap.put("locationIdList", locationIdList);
        }

        List<Map<String, Object>> result = dtsDao.getEbsDtsList(conditionMap, true);
        return (Integer)(result.get(0).get("total"));
    }

    /**
     * method name : getEbsMeterList<b/>
     * method Desc : Energy Balance Monitoring 맥스가젯의 Meter List 를 조회한다.
     *
     * @param conditionMap
     * @return
     */
    public List<Map<String, Object>> getEbsMeterList(Map<String, Object> conditionMap) {
        Integer locationId = (Integer)conditionMap.get("locationId");
        List<Integer> locationIdList = null;

        if (locationId != null) {
            locationIdList = locationDao.getChildLocationId(locationId);
            locationIdList.add(locationId);
            conditionMap.put("locationIdList", locationIdList);
        }

        List<Map<String, Object>> result = meterDao.getEbsMeterList(conditionMap, false);

        Supplier supplier = supplierDao.get((Integer)conditionMap.get("supplierId"));
        String lang = supplier.getLang().getCode_2letter();
        String country = supplier.getCountry().getCode_2letter();

        for (Map<String, Object> map : result) {
            map.put("installDate", TimeLocaleUtil.getLocaleDate((String)map.get("installDate"), lang, country));
        }

        return result;
    }

    /**
     * method name : getEbsMeterListTotalCount<b/>
     * method Desc : Energy Balance Monitoring 맥스가젯의 Meter List 의 Total Count 를 조회한다.
     *
     * @param conditionMap
     * @return
     */
    public Integer getEbsMeterListTotalCount(Map<String, Object> conditionMap) {
        Integer locationId = (Integer)conditionMap.get("locationId");
        List<Integer> locationIdList = null;

        if (locationId != null) {
            locationIdList = locationDao.getChildLocationId(locationId);
            locationIdList.add(locationId);
            conditionMap.put("locationIdList", locationIdList);
        }

        List<Map<String, Object>> result = meterDao.getEbsMeterList(conditionMap, true);
        return (Integer)(result.get(0).get("total"));
    }

    /**
     * method name : getEbsContractMeterList<b/>
     * method Desc : Energy Balance Monitoring 맥스가젯의 Contract Meter List 를 조회한다.
     *
     * @param conditionMap
     * @return
     */
    public List<Map<String, Object>> getEbsContractMeterList(Map<String, Object> conditionMap) {
        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
        Map<String, Object> map = null;
        Integer locationId = (Integer)conditionMap.get("locationId");
        List<Integer> locationIdList = null;

        if (locationId != null) {
            locationIdList = locationDao.getChildLocationId(locationId);
            locationIdList.add(locationId);
            conditionMap.put("locationIdList", locationIdList);
        }

        List<Map<String, Object>> list = meterDao.getEbsContractMeterList(conditionMap, false);

        if (list != null && list.size() > 0) {
            for (Map<String, Object> obj : list) {
                map = new HashMap<String, Object>();
                map.put("contMeterId", DecimalUtil.ConvertNumberToInteger(obj.get("CONT_METER_ID")));
                map.put("mdsId", (String)obj.get("MDS_ID"));
                map.put("contractNumber", (String)obj.get("CONTRACT_NUMBER"));
                map.put("customerNo", (String)obj.get("CUSTOMER_NO"));
                map.put("customerName", (String)obj.get("CUSTOMER_NAME"));
                map.put("location", (String)obj.get("LOCATION"));
                map.put("tariffType", (String)obj.get("TARIFF_TYPE"));
                result.add(map);
            }
        }

        return result;
    }

    /**
     * method name : getEbsContractMeterListTotalCount<b/>
     * method Desc : Energy Balance Monitoring 맥스가젯의 Contract Meter List 의 Total Count 를 조회한다.
     *
     * @param conditionMap
     * @return
     */
    public Integer getEbsContractMeterListTotalCount(Map<String, Object> conditionMap) {
        Integer locationId = (Integer)conditionMap.get("locationId");
        List<Integer> locationIdList = null;

        if (locationId != null) {
            locationIdList = locationDao.getChildLocationId(locationId);
            locationIdList.add(locationId);
            conditionMap.put("locationIdList", locationIdList);
        }

        List<Map<String, Object>> result = meterDao.getEbsContractMeterList(conditionMap, true);
        return (Integer)(result.get(0).get("total"));
    }

    /**
     * method name : getEbsDtsNameDup<b/>
     * method Desc : Energy Balance Monitoring 맥스가젯에서 DTS Name 의 중복을 체크한다.
     *
     * @param conditionMap
     * @return true : 중복
     */
    public boolean getEbsDtsNameDup(Map<String, Object> conditionMap) {
        boolean result = false;
        Integer count = dtsDao.getEbsDtsDupCount(conditionMap);
        
        if (count > 0) {    // 같은 이름 데이터 존재함
            result = true;
        }
        return result;
    }

    /**
     * method name : insertEbsDts<b/>
     * method Desc : Energy Balance Monitoring 맥스가젯에서 DTS 정보를 저장한다.
     *
     * @param conditionMap
     * @return
     */
    @Transactional
    public void insertEbsDts(Map<String, Object> conditionMap) {
        Supplier supplier = supplierDao.get((Integer)conditionMap.get("supplierId"));
        Location location = locationDao.get((Integer)conditionMap.get("locationId"));
        
        DistTrfmrSubstation dts = new DistTrfmrSubstation();
        dts.setName((String)conditionMap.get("dtsName"));
        dts.setSupplier(supplier);
        dts.setThreshold((Double)conditionMap.get("threshold"));
        dts.setLocation(location);
        dts.setAddress((String)conditionMap.get("address"));
        dts.setDescription((String)conditionMap.get("description"));
        dtsDao.add(dts);
    }

    /**
     * method name : updateEbsDtsList<b/>
     * method Desc : Energy Balance Monitoring 맥스가젯에서 DTS 리스트를 수정한다.
     *
     * @param conditionMap
     * @return
     */
    @Transactional
    public void updateEbsDtsList(Map<String, Object> conditionMap) {
        String[] dtsIds = (String[])conditionMap.get("dtsIds");
        String[] thresholds = (String[])conditionMap.get("thresholds");
        String[] addresses = (String[])conditionMap.get("addresses");
        String[] descriptions = (String[])conditionMap.get("descriptions");
        DistTrfmrSubstation dts = null;
        int len = dtsIds.length;

        for (int i = 0 ; i < len ; i++) {
            dts = dtsDao.get(Integer.valueOf(dtsIds[i]));
            dts.setThreshold(Double.valueOf(thresholds[i]));
            dts.setAddress(addresses[i]);
            dts.setDescription(descriptions[i]);
            dtsDao.update(dts);
        }
    }

    /**
     * method name : addEbsMeterNode<b/>
     * method Desc : Energy Balance Monitoring 맥스가젯에서 DTS Meter Node 를 추가한다.
     *
     * @param conditionMap
     * @return
     */
    @Transactional
    public void addEbsMeterNode(Map<String, Object> conditionMap) {
        Integer dtsId = (Integer)conditionMap.get("dtsId");
        String[] meterIds = (String[])conditionMap.get("meterIds");
        Meter meter = null;
        DistTrfmrSubstation dts = null;
        int len = meterIds.length;

        for (int i = 0 ; i < len ; i++) {
            meter = meterDao.get(Integer.valueOf(meterIds[i]));
            dts = dtsDao.get(dtsId);
            meter.setDistTrfmrSubstation(dts);
            meterDao.update(meter);
        }
    }

    /**
     * method name : addEbsContractMeterNode<b/>
     * method Desc : Energy Balance Monitoring 맥스가젯에서 Meter Phase 에 Contract Meter Node 를 추가한다.
     *
     * @param conditionMap
     * @return
     */
    @Transactional
    public void addEbsContractMeterNode(Map<String, Object> conditionMap) {
        Integer meterId = (Integer)conditionMap.get("meterId"); // dtsMeterId
        Integer phaseId = (Integer)conditionMap.get("phaseId");
        String[] contMeterIds = (String[])conditionMap.get("contMeterIds");
        Meter contMeter = null;
        Meter meter = null;
        int len = contMeterIds.length;

        for (int i = 0 ; i < len ; i++) {
            contMeter = meterDao.get(Integer.valueOf(contMeterIds[i]));
            meter = meterDao.get(meterId);

            for (DistTrfmrSubstationMeterPhase obj : DistTrfmrSubstationMeterPhase.values()) {
                if (obj.getCode().equals(phaseId)) {
                    switch(obj) {
                        case LINE_A:
                            contMeter.setDistTrfmrSubstationMeter_A(meter);
                            contMeter.setDistTrfmrSubstationMeter_B(null);
                            contMeter.setDistTrfmrSubstationMeter_C(null);
                            meterDao.update(contMeter);
                            break;

                        case LINE_B:
                            contMeter.setDistTrfmrSubstationMeter_A(null);
                            contMeter.setDistTrfmrSubstationMeter_B(meter);
                            contMeter.setDistTrfmrSubstationMeter_C(null);
                            meterDao.update(contMeter);
                            break;

                        case LINE_C:
                            contMeter.setDistTrfmrSubstationMeter_A(null);
                            contMeter.setDistTrfmrSubstationMeter_B(null);
                            contMeter.setDistTrfmrSubstationMeter_C(meter);
                            meterDao.update(contMeter);
                            break;

                    }
                    break;
                }
            }
        }
    }

    /**
     * method name : deleteEbsDtsTreeNode<b/>
     * method Desc : Energy Balance Monitoring 맥스가젯에서 DTS Tree 의 Node 를 삭제한다.
     *
     * @param conditionMap
     * @return
     */
    @Transactional
    public void deleteEbsDtsTreeNode(Map<String, Object> conditionMap) {
        String deleteNodeId = (String)conditionMap.get("deleteNodeId");
        String[] ids = deleteNodeId.split("_");
        int len = ids.length;
        
        switch(len) {
            case 2:
                // DTS 삭제
                conditionMap.put("dtsId", Integer.valueOf(ids[1]));
                deleteEbsDtsNode(conditionMap);
                break;
                
            case 3:
                // Meter 삭제
                conditionMap.put("meterId", Integer.valueOf(ids[2]));
                deleteEbsMeterNode(conditionMap);
                break;
                
            case 4:
                // Meter 상 에 포함된 contract 삭제
                conditionMap.put("meterId", Integer.valueOf(ids[2]));
                conditionMap.put("phaseId", Integer.valueOf(ids[3]));
                deleteEbsPhaseNode(conditionMap);
                break;
                
            case 5:
                // contract Meter 삭제
                deleteEbsContractMeterNode(Integer.valueOf(ids[4]));
                break;
        }
    }

    /**
     * method name : deleteEbsDtsNode<b/>
     * method Desc : 해당 DTS Node 와 하위 Node 를 삭제한다.
     *
     * @param conditionMap
     */
    @Transactional
    public void deleteEbsDtsNode(Map<String, Object> conditionMap) {
        Integer supplierId = (Integer)conditionMap.get("supplierId");
        Integer dtsId = (Integer)conditionMap.get("dtsId");
        DistTrfmrSubstation dts = dtsDao.get(dtsId);
        
        Set<Condition> set = new HashSet<Condition>();
        Condition condition = new Condition();

        condition = new Condition();
        condition.setField("supplier");
        condition.setValue(new Object[] { "s" });
        condition.setRestrict(Restriction.ALIAS);
        set.add(condition);

        condition = new Condition();
        condition.setField("distTrfmrSubstation");
        condition.setValue(new Object[] { "d" });
        condition.setRestrict(Restriction.ALIAS);
        set.add(condition);

        condition = new Condition();
        condition.setField("s.id");
        condition.setValue(new Object[] {supplierId});
        condition.setRestrict(Restriction.EQ);
        set.add(condition);

        condition = new Condition();
        condition.setField("d.id");
        condition.setValue(new Object[] {dtsId});
        condition.setRestrict(Restriction.EQ);
        set.add(condition);

        List<Meter> listMeter = meterDao.findByConditions(set);
        List<Meter> listContMeter = null;

        for (Meter meter : listMeter) {
            conditionMap.put("meterId", meter.getId());
            listContMeter = meterDao.getDeleteEbsContractMeterNodeListByMeter(conditionMap);

            for (Meter obj : listContMeter) {
                obj.setDistTrfmrSubstationMeter_A(null);
                obj.setDistTrfmrSubstationMeter_B(null);
                obj.setDistTrfmrSubstationMeter_C(null);
                meterDao.update(obj);
            }

            meter.setDistTrfmrSubstation(null);
            meterDao.update(meter);
        }

        // delete DTS
        dtsDao.delete(dts);
    }

    /**
     * method name : deleteEbsMeterNode<b/>
     * method Desc : 해당 Meter Node 와 하위 Node 를 삭제한다.
     *
     * @param conditionMap
     */
    @Transactional
    public void deleteEbsMeterNode(Map<String, Object> conditionMap) {
        Integer meterId = (Integer)conditionMap.get("meterId");

        Meter meter = meterDao.get(meterId);
        List<Meter> listContMeter = meterDao.getDeleteEbsContractMeterNodeListByMeter(conditionMap);

        for (Meter obj : listContMeter) {
            obj.setDistTrfmrSubstationMeter_A(null);
            obj.setDistTrfmrSubstationMeter_B(null);
            obj.setDistTrfmrSubstationMeter_C(null);
            meterDao.update(obj);
        }

        meter.setDistTrfmrSubstation(null);
        meterDao.update(meter);
    }

    /**
     * method name : deleteEbsPhaseNode<b/>
     * method Desc : 해당 phase Node 와 하위 Node 를 삭제한다.
     *
     * @param conditionMap
     */
    @Transactional
    public void deleteEbsPhaseNode(Map<String, Object> conditionMap) {
        Integer supplierId = (Integer)conditionMap.get("supplierId");
        Integer meterId = (Integer)conditionMap.get("meterId");
        Integer phaseId = (Integer)conditionMap.get("phaseId");
        String columnName = null;

        for (DistTrfmrSubstationMeterPhase obj : DistTrfmrSubstationMeterPhase.values()) {
            if (obj.getCode().equals(phaseId)) {
                switch(obj) {
                    case LINE_A:
                        columnName = "distTrfmrSubstationMeter_A";
                        break;

                    case LINE_B:
                        columnName = "distTrfmrSubstationMeter_B";
                        break;

                    case LINE_C:
                        columnName = "distTrfmrSubstationMeter_C";
                        break;
                }
                break;
            }
        }

        Set<Condition> set = new HashSet<Condition>();
        Condition condition = new Condition();

        condition = new Condition();
        condition.setField("supplier");
        condition.setValue(new Object[] { "s" });
        condition.setRestrict(Restriction.ALIAS);
        set.add(condition);

        condition = new Condition();
        condition.setField(columnName);
        condition.setValue(new Object[] { "m" });
        condition.setRestrict(Restriction.ALIAS);
        set.add(condition);

        condition = new Condition();
        condition.setField("s.id");
        condition.setValue(new Object[] {supplierId});
        condition.setRestrict(Restriction.EQ);
        set.add(condition);

        condition = new Condition();
        condition.setField("m.id");
        condition.setValue(new Object[] {meterId});
        condition.setRestrict(Restriction.EQ);
        set.add(condition);

        // select Contract Meter
        List<Meter> listContMeter = meterDao.findByConditions(set);

        for (Meter obj : listContMeter) {
            obj.setDistTrfmrSubstationMeter_A(null);
            obj.setDistTrfmrSubstationMeter_B(null);
            obj.setDistTrfmrSubstationMeter_C(null);
            meterDao.update(obj);
        }
    }

    /**
     * method name : deleteEbsContractMeterNode<b/>
     * method Desc : 해당 contract Node 를 삭제한다.
     *
     * @param contMeterId Meter.id
     */
    @Transactional
    public void deleteEbsContractMeterNode(Integer contMeterId) {
        Meter contMeter = meterDao.get(contMeterId);
        contMeter.setDistTrfmrSubstationMeter_A(null);
        contMeter.setDistTrfmrSubstationMeter_B(null);
        contMeter.setDistTrfmrSubstationMeter_C(null);
        meterDao.update(contMeter);
    }

    /**
     * method name : getEbsImportDtsChartData<b/>
     * method Desc : Energy Balance Monitoring 맥스가젯의 DTS Tree Chart 의 당월 Import Energy Data 를 조회한다.
     *
     * @param conditionMap
     * @return
     */
    public Map<String, Object> getEbsDtsImportChartData(Map<String, Object> conditionMap) {
        Map<String, Object> result = new HashMap<String, Object>();
        List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        List<Integer> meterIdList = new ArrayList<Integer>();
        String nodeId = (String)conditionMap.get("nodeId");
        String searchStartDate = (String)conditionMap.get("searchStartDate");
        String searchEndDate = (String)conditionMap.get("searchEndDate");
        String searchPreStartDate = null;
        Integer supplierId = (Integer)conditionMap.get("supplierId");
        Supplier supplier = supplierDao.get(supplierId);
        String country = supplier.getCountry().getCode_2letter();
        String lang    = supplier.getLang().getCode_2letter();
        DecimalFormat mdf = DecimalUtil.getDecimalFormat(supplier.getMd());

        String[] ids = nodeId.split("_");
        int depth = ids.length;
        conditionMap.put("depth", depth);

        try {
            searchPreStartDate = TimeUtil.getPreDay(searchStartDate).substring(0, 8);
            conditionMap.put("searchPreStartDate", searchPreStartDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        switch(depth) {
            case 2:
                // DTS Chart Data
                conditionMap.put("dtsId", Integer.valueOf(ids[1]));
                DistTrfmrSubstation dts = dtsDao.get(Integer.valueOf(ids[1]));
                Set<Meter> set = dts.getMeter();
                Iterator<Meter> itr = set.iterator();
                while(itr.hasNext()) {
                    meterIdList.add(itr.next().getId());
                }
                break;
                
            case 3:
                // Meter Chart Data
                conditionMap.put("dtsId", Integer.valueOf(ids[1]));
                conditionMap.put("meterId", Integer.valueOf(ids[2]));
                meterIdList.add(Integer.valueOf(ids[2]));
                break;
                
            case 4:
                // Meter 상 Chart Data
                conditionMap.put("dtsId", Integer.valueOf(ids[1]));
                conditionMap.put("meterId", Integer.valueOf(ids[2]));
                conditionMap.put("phaseId", Integer.valueOf(ids[3]));
                meterIdList.add(Integer.valueOf(ids[2]));
                break;
        }

        // 당월 Import Enegy 조회
        list = dtsDao.getEbsDtsChartImportData(conditionMap);

        Map<String, Object> listMap = new HashMap<String, Object>();
        for (Map<String, Object> obj : list) {
            listMap.put(obj.get("YYYYMMDD") + "_" + obj.get("METER_ID"), obj.get("ENERGY_SUM"));
        }

        int meterLen = meterIdList != null ? meterIdList.size() : 0;
        String conditionKey = null;
        String conditionPreKey = null;
        String conditionDate = searchStartDate;
        String conditionPreDate = searchPreStartDate;
        BigDecimal bdEnergyRate = null;
        Map<String, Object> map = null;

        try {
            for (int i = 0 ; i < 31 ; i++) {
                bdEnergyRate = new BigDecimal(0D); 

                for (int j = 0 ; j < meterLen ; j++) {
                    conditionKey = conditionDate + "_" + meterIdList.get(j);
                    conditionPreKey = conditionPreDate + "_" + meterIdList.get(j);
                    bdEnergyRate = bdEnergyRate.add(getBillingEnergyRateByDay((Number)listMap.get(conditionKey), (Number)listMap.get(conditionPreKey)));
                }

                map = new HashMap<String, Object>();

                map.put("yyyymmdd", conditionDate);
                map.put("yyyymmddFormat", TimeLocaleUtil.getLocaleDateByMediumFormat(conditionDate, lang, country));
                map.put("energySum", bdEnergyRate.doubleValue());
                map.put("energySumFormat", mdf.format(bdEnergyRate.doubleValue()));
                resultList.add(map);

                if (conditionDate.equals(searchEndDate)) {
                    // 마지막 조회일자에 도달하면 loop 문을 벗어남.
                    break;
                }
                conditionDate = TimeUtil.getAddedDay(conditionDate, 1).substring(0, 8);
                conditionPreDate = TimeUtil.getAddedDay(conditionPreDate, 1).substring(0, 8);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        
        result.put("curMonImport", resultList);
        result.putAll(getEbsDtsConsumeChartData(conditionMap));

        return result;
    }

    /**
     * method name : getEbsDtsConsumeChartData<b/>
     * method Desc : Energy Balance Monitoring 맥스가젯의 DTS Tree Chart 의 당월/전월/전년도 Consume Energy Data 를 조회한다.
     *
     * @param conditionMap
     * @return
     */
    public Map<String, Object> getEbsDtsConsumeChartData(Map<String, Object> conditionMap) {
        Map<String, Object> result = new HashMap<String, Object>();
        List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        List<Map<String, Object>> contMeterList = new ArrayList<Map<String, Object>>();
        List<Integer> contMeterIdList = new ArrayList<Integer>();
        Map<String, Object> map = null;

        String nodeId = (String)conditionMap.get("nodeId");
        String searchPreStartDate = (String)conditionMap.get("searchPreStartDate");
        String searchStartDate = (String)conditionMap.get("searchStartDate");
        String searchEndDate = (String)conditionMap.get("searchEndDate");
        Integer depth = (Integer)conditionMap.get("depth");
        Integer supplierId = (Integer)conditionMap.get("supplierId");
        Supplier supplier = supplierDao.get(supplierId);
        String country = supplier.getCountry().getCode_2letter();
        String lang    = supplier.getLang().getCode_2letter();
        DecimalFormat mdf = DecimalUtil.getDecimalFormat(supplier.getMd());

        try {
            if (searchPreStartDate == null) {
                searchPreStartDate = TimeUtil.getPreDay(searchStartDate).substring(0, 8);
              conditionMap.put("searchPreStartDate", searchPreStartDate);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (depth == null) {
            String[] ids = nodeId.split("_");
            depth = ids.length;
            conditionMap.put("depth", depth);

            switch(depth) {
                case 2:
                    // DTS Chart Data
                    conditionMap.put("dtsId", Integer.valueOf(ids[1]));
                    break;
                    
                case 3:
                    // Meter Chart Data
                    conditionMap.put("dtsId", Integer.valueOf(ids[1]));
                    conditionMap.put("meterId", Integer.valueOf(ids[2]));
                    break;

                case 4:
                    // Meter 상 Chart Data
                    conditionMap.put("dtsId", Integer.valueOf(ids[1]));
                    conditionMap.put("meterId", Integer.valueOf(ids[2]));
                    conditionMap.put("phaseId", Integer.valueOf(ids[3]));
                    break;

                case 5:
                    // Contract Meter Chart Data
                    conditionMap.put("dtsId", Integer.valueOf(ids[1]));
                    conditionMap.put("meterId", Integer.valueOf(ids[2]));
                    conditionMap.put("phaseId", Integer.valueOf(ids[3]));
                    conditionMap.put("contMeterId", Integer.valueOf(ids[4]));
                    contMeterIdList.add(Integer.valueOf(ids[4]));
                    break;
            }
        }

        // chart window title
        DistTrfmrSubstation dts = dtsDao.get((Integer)conditionMap.get("dtsId"));
        result.put("dtsLocation", (dts.getLocation() != null) ? dts.getLocation().getName() : "");
        result.put("dtsName", dts.getName());

        if (depth != 5) {
            contMeterList = meterDao.getEbsDtsChartContractMeterIds(conditionMap);
            for (Map<String, Object> obj : contMeterList) {
                contMeterIdList.add(DecimalUtil.ConvertNumberToInteger(obj.get("CONT_METER_ID")));
            }
            conditionMap.put("contMeterIdList", contMeterIdList);
        }

        int contMeterLen = contMeterIdList != null ? contMeterIdList.size() : 0;

        if (contMeterLen > 0) {
            // 당월 Consume Energy 조회
            list = meterDao.getEbsDtsChartConsumeData(conditionMap);
        }

        Map<String, Object> listMap = new HashMap<String, Object>();
        for (Map<String, Object> obj : list) {
            listMap.put(obj.get("YYYYMMDD") + "_" + obj.get("CONT_METER_ID"), obj.get("ENERGY_SUM"));
        }

        String conditionKey = null;
        String conditionPreKey = null;
        String conditionDate = searchStartDate;
        String conditionPreDate = searchPreStartDate;
        BigDecimal bdEnergyRate = null;

        try {
            for (int i = 0 ; i < 31 ; i++) {
                bdEnergyRate = new BigDecimal(0D); 

                for (int j = 0 ; j < contMeterLen ; j++) {
                    conditionKey = conditionDate + "_" + contMeterIdList.get(j);
                    conditionPreKey = conditionPreDate + "_" + contMeterIdList.get(j);
                    bdEnergyRate = bdEnergyRate.add(getBillingEnergyRateByDay((Number)listMap.get(conditionKey), (Number)listMap.get(conditionPreKey)));
                }

                map = new HashMap<String, Object>();

                map.put("yyyymmdd", conditionDate);
                map.put("yyyymmddFormat", TimeLocaleUtil.getLocaleDateByMediumFormat(conditionDate, lang, country));
                map.put("energySum", bdEnergyRate.doubleValue());
                map.put("energySumFormat", mdf.format(bdEnergyRate.doubleValue()));
                resultList.add(map);

                if (conditionDate.equals(searchEndDate)) {
                    // 마지막 조회일자에 도달하면 loop 문을 벗어남.
                    break;
                }
                conditionDate = TimeUtil.getAddedDay(conditionDate, 1).substring(0, 8);
                conditionPreDate = TimeUtil.getAddedDay(conditionPreDate, 1).substring(0, 8);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        result.put("curMonConsume", resultList);
        
        try {
            // 전월 Consume Energy 조회
            String lastMonStartDate = TimeUtil.getPreMonth(searchStartDate).substring(0, 8);
            String lastMonEndDate = lastMonStartDate.substring(0, 6)
                    + StringUtil.frontAppendNStr('0',
                            CalendarUtil.getMonthLastDate(lastMonStartDate.substring(0, 4), lastMonStartDate.substring(4, 6)), 2);

            conditionMap.put("searchStartDate", lastMonStartDate);
            conditionMap.put("searchEndDate", lastMonEndDate);

            String lastMonPreStartDate = TimeUtil.getPreDay(lastMonStartDate).substring(0, 8);
            conditionMap.put("searchPreStartDate", lastMonPreStartDate);

            list = new ArrayList<Map<String, Object>>();
            resultList = new ArrayList<Map<String, Object>>();

            if (contMeterLen > 0) {
                list = meterDao.getEbsDtsChartConsumeData(conditionMap);
            }

            listMap = new HashMap<String, Object>();
            for (Map<String, Object> obj : list) {
                listMap.put(obj.get("YYYYMMDD") + "_" + obj.get("CONT_METER_ID"), obj.get("ENERGY_SUM"));
            }

            conditionKey = null;
            conditionPreKey = null;
            conditionDate = lastMonStartDate;
            conditionPreDate = lastMonPreStartDate;
            bdEnergyRate = null;

            for (int i = 0 ; i < 31 ; i++) {
                bdEnergyRate = new BigDecimal(0D); 
                
                for (int j = 0 ; j < contMeterLen ; j++) {
                    conditionKey = conditionDate + "_" + contMeterIdList.get(j);
                    conditionPreKey = conditionPreDate + "_" + contMeterIdList.get(j);
                    bdEnergyRate = bdEnergyRate.add(getBillingEnergyRateByDay((Number)listMap.get(conditionKey), (Number)listMap.get(conditionPreKey)));
                }

                map = new HashMap<String, Object>();

                map.put("yyyymmdd", conditionDate);
                map.put("yyyymmddFormat", TimeLocaleUtil.getLocaleDateByMediumFormat(conditionDate, lang, country));
                map.put("energySum", bdEnergyRate.doubleValue());
                map.put("energySumFormat", mdf.format(bdEnergyRate.doubleValue()));
                resultList.add(map);

                if (conditionDate.equals(lastMonEndDate)) {
                    // 마지막 조회일자에 도달하면 loop 문을 벗어남.
                    break;
                }
                conditionDate = TimeUtil.getAddedDay(conditionDate, 1).substring(0, 8);
                conditionPreDate = TimeUtil.getAddedDay(conditionPreDate, 1).substring(0, 8);
            }

            result.put("lastMonConsume", resultList);

            // 전년도 Consume Energy 조회
            String lastYearStartDate = TimeUtil.getPreMonth(searchStartDate, 12).substring(0, 8);
            String lastYearEndDate = lastYearStartDate.substring(0, 6)
                    + StringUtil.frontAppendNStr('0',
                            CalendarUtil.getMonthLastDate(lastYearStartDate.substring(0, 4), lastYearStartDate.substring(4, 6)), 2);

            conditionMap.put("searchStartDate", lastYearStartDate);
            conditionMap.put("searchEndDate", lastYearEndDate);

            String lastYearPreStartDate = TimeUtil.getPreDay(lastYearStartDate).substring(0, 8);
            conditionMap.put("searchPreStartDate", lastYearPreStartDate);

            list = new ArrayList<Map<String, Object>>();
            resultList = new ArrayList<Map<String, Object>>();

            if (contMeterLen > 0) {
                list = meterDao.getEbsDtsChartConsumeData(conditionMap);
            }

            listMap = new HashMap<String, Object>();
            for (Map<String, Object> obj : list) {
                listMap.put(obj.get("YYYYMMDD") + "_" + obj.get("CONT_METER_ID"), obj.get("ENERGY_SUM"));
            }

            conditionKey = null;
            conditionPreKey = null;
            conditionDate = lastYearStartDate;
            conditionPreDate = lastYearPreStartDate;
            bdEnergyRate = null;

            for (int i = 0 ; i < 31 ; i++) {
                bdEnergyRate = new BigDecimal(0D);

                for (int j = 0 ; j < contMeterLen ; j++) {
                    conditionKey = conditionDate + "_" + contMeterIdList.get(j);
                    conditionPreKey = conditionPreDate + "_" + contMeterIdList.get(j);
                    bdEnergyRate = bdEnergyRate.add(getBillingEnergyRateByDay((Number)listMap.get(conditionKey), (Number)listMap.get(conditionPreKey)));
                }

                map = new HashMap<String, Object>();

                map.put("yyyymmdd", conditionDate);
                map.put("yyyymmddFormat", TimeLocaleUtil.getLocaleDateByMediumFormat(conditionDate, lang, country));
                map.put("energySum", bdEnergyRate.doubleValue());
                map.put("energySumFormat", mdf.format(bdEnergyRate.doubleValue()));
                resultList.add(map);

                if (conditionDate.equals(lastMonEndDate)) {
                    // 마지막 조회일자에 도달하면 loop 문을 벗어남.
                    break;
                }
                conditionDate = TimeUtil.getAddedDay(conditionDate, 1).substring(0, 8);
                conditionPreDate = TimeUtil.getAddedDay(conditionPreDate, 1).substring(0, 8);
            }

            result.put("lastYearConsume", resultList);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * method name : getSuspectedYn<b/>
     * method Desc : 에너지 도난 의심 여부를 체크한다.
     *
     * @param impEnergy
     * @param consumeEnergy
     * @param threshold
     * @return
     */
    private boolean getSuspectedYn(Double impEnergy, Double consumeEnergy, Double threshold) {
        boolean suspect = false;
        BigDecimal techLoss = new BigDecimal(threshold).divide(new BigDecimal(100), MathContext.DECIMAL32);
        BigDecimal diffRatio = new BigDecimal(getDifferenceRatio(impEnergy, consumeEnergy));

        if (diffRatio.compareTo(techLoss) > 0) {
            suspect = true;
        }

        return suspect;
    }

    /**
     * method name : getDifferenceRatio<b/>
     * method Desc : DTS 에서 공급량과 Customer 사용량의 차이값 비율을 구한다.
     *
     * @param impEnergy
     * @param consumeEnergy
     * @return
     */
    private Double getDifferenceRatio(Double impEnergy, Double consumeEnergy) {
        if (impEnergy.equals(0D)) {
            return 0D;
        } else {
            BigDecimal imp = new BigDecimal(impEnergy);
            BigDecimal con = new BigDecimal(consumeEnergy);

            BigDecimal difference = imp.subtract(con);        // 차이값
            BigDecimal diffRatio = difference.divide(imp, MathContext.DECIMAL32);    // 차이율

            return diffRatio.doubleValue();
        }
    }

    /**
     * method name : getBillingEnergyRateByDay<b/>
     * method Desc : BillingDay 당일 에너지 사용량을 계산한다.(당일 사용량 - 전일 사용량)
     *
     * @param energyRate
     * @param lastEnergyRate
     * @return
     */
    private BigDecimal getBillingEnergyRateByDay(Number energyRate, Number lastEnergyRate) {
        if (energyRate == null || new BigDecimal(energyRate.doubleValue()).equals(new BigDecimal(0D))) {
            return new BigDecimal(0D);
        } else if (lastEnergyRate == null) {
            return new BigDecimal(energyRate.doubleValue());
        }

        BigDecimal bdEnergyRatete = new BigDecimal(energyRate.doubleValue());
        BigDecimal bdLastEnergyRate = new BigDecimal(lastEnergyRate.doubleValue());
        BigDecimal result = bdEnergyRatete.subtract(bdLastEnergyRate);

        return result;
    }

    /**
     * method name : getEbsExportExcelData<b/>
     * method Desc : Energy Balance Monitoring 맥스가젯의 DTS Tree 의 Export Excel Data 를 조회한다.
     *
     * @param conditionMap
     * @return
     */
    public List<List<Object>> getEbsExportExcelData(Map<String, Object> conditionMap) {
        List<List<Object>> result = new ArrayList<List<Object>>();
        List<Map<String, Object>> list = null;
        Integer supplierId = (Integer)conditionMap.get("supplierId");
        Integer locationId = (Integer)conditionMap.get("locationId");
        
        List<Integer> locationIdList = null;

        if (locationId != null || !locationDao.isRoot(locationId)) {
            locationIdList = locationDao.getChildLocationId(locationId);
            locationIdList.add(locationId);
            conditionMap.put("locationIdList", locationIdList);
        }

        list = dtsDao.getEbsExportExcelData(conditionMap);
        
        Supplier supplier = supplierDao.get(supplierId);
        DecimalFormat mdf = DecimalUtil.getDecimalFormat(supplier.getMd());
        List<Object> tempList = null;

        BigDecimal bdImpEnergy = null;
        BigDecimal bdThreshold = null;
        BigDecimal bdTolEnergy = null;

        Integer num = 0;
        for (Map<String, Object> obj : list) {
            tempList = new ArrayList<Object>();
            num++;
            tempList.add(num);
            tempList.add(obj.get("LOCATION_NAME"));
            tempList.add(obj.get("DTS_NAME"));
            tempList.add(obj.get("THRESHOLD"));
            tempList.add(mdf.format(StringUtil.nullToDoubleZero(DecimalUtil.ConvertNumberToDouble(obj.get("IMPORT_ENERGY_TOTAL")))));
//            tempList.add(mdf.format(StringUtil.nullToDoubleZero(DecimalUtil.ConvertNumberToDouble(obj.get("TOL_ENERGY_TOTAL")))));
            bdImpEnergy = new BigDecimal(DecimalUtil.ConvertNumberToDouble(obj.get("IMPORT_ENERGY_TOTAL")));
            bdThreshold = new BigDecimal(DecimalUtil.ConvertNumberToDouble(obj.get("THRESHOLD")));
            bdTolEnergy = bdImpEnergy.multiply((new BigDecimal(100).subtract(bdThreshold)).divide(new BigDecimal(100), MathContext.DECIMAL32) );
            tempList.add(mdf.format(bdTolEnergy.doubleValue()));

            tempList.add(mdf.format(StringUtil.nullToDoubleZero(DecimalUtil.ConvertNumberToDouble(obj.get("CONSUME_ENERGY_TOTAL")))));
            result.add(tempList);
        }
        return result;
    }
}