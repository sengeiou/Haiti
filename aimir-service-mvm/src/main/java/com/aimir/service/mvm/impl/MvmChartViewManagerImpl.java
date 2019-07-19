package com.aimir.service.mvm.impl;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.MathContext;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aimir.constants.CommonConstants.ChangeMeterTypeName;
import com.aimir.constants.CommonConstants.ChannelCalcMethod;
import com.aimir.constants.CommonConstants.DefaultChannel;
import com.aimir.constants.CommonConstants.DeviceType;
import com.aimir.constants.CommonConstants.ElectricityChannel;
import com.aimir.constants.CommonConstants.MeterType;
import com.aimir.dao.device.MeterDao;
import com.aimir.dao.mvm.ChannelConfigDao;
import com.aimir.dao.mvm.LpEMDao;
import com.aimir.dao.system.Co2FormulaDao;
import com.aimir.dao.system.CodeDao;
import com.aimir.dao.system.ContractDao;
import com.aimir.dao.system.CustomerDao;
import com.aimir.dao.system.SupplierDao;
import com.aimir.model.device.Meter;
import com.aimir.model.mvm.MeteringDay;
import com.aimir.model.mvm.MeteringLP;
import com.aimir.model.mvm.MeteringMonth;
import com.aimir.model.system.Co2Formula;
import com.aimir.model.system.Contract;
import com.aimir.model.system.Supplier;
import com.aimir.service.mvm.MvmChartViewManager;
import com.aimir.service.mvm.bean.ChannelInfo;
import com.aimir.service.mvm.bean.CustomerInfo;
import com.aimir.service.mvm.bean.MvmChartViewData;
import com.aimir.util.Condition;
import com.aimir.util.Condition.Restriction;
import com.aimir.util.DecimalUtil;
import com.aimir.util.SearchCalendarUtil;
import com.aimir.util.StringUtil;
import com.aimir.util.TimeLocaleUtil;
import com.aimir.util.TimeUtil;

/**
 * @author psc
 *
 */

@Service(value = "MvmChartViewManager")
public class MvmChartViewManagerImpl implements MvmChartViewManager {

    @Autowired
    CustomerDao ctmDao;

    @Autowired
    ContractDao contractDao;

    @Autowired
    LpEMDao lpEMDao;

    @Autowired
    Co2FormulaDao co2formulaDao;

    @Autowired
    CodeDao codeDao;

    @Autowired
    ChannelConfigDao channelConfigDao;

    @Autowired
    MeterDao meterDao;

    @Autowired
    SupplierDao supplierDao;

    @Autowired
    private MvmEmChartViewManagerImpl MvmEmChartViewManagerImpl;// EM관련 클래스

    @Autowired
    private MvmGmChartViewManagerImpl MvmGmChartViewManagerImpl;// GM관련 클래스

    @Autowired
    private MvmHmChartViewManagerImpl MvmHmChartViewManagerImpl;// HM관련 클래스

    @Autowired
    private MvmWmChartViewManagerImpl MvmWmChartViewManagerImpl;// WM관련 클래스

    @SuppressWarnings("unused")
    private static Log logger = LogFactory.getLog(MvmChartViewManagerImpl.class);

    /**
     * × @see
     * com.aimir.service.mvm.MvmDetailViewManager#getCustomerInfo(java.lang
     * .String)
     *
     * @Method Name : getCustomerInfo
     * @Date : 2010. 4. 8.
     * @Method 설명 :
     * @param customerNo
     *            고객정보
     * @return
     */
    @Deprecated
    public List<CustomerInfo> getCustomerInfo(String contractNo) {
        // Set<Condition> set = new HashSet<Condition>();

        List<CustomerInfo> result = new ArrayList<CustomerInfo>();
        String[] contractNoArr = contractNo.split(",");

        if (contractNo.startsWith(",") || "".equals(contractNo) || contractNo == null || contractNoArr == null) {
            return result;
        }

        if (contractNoArr != null && contractNoArr.length > 0) {
            for (int i = 0; i < contractNoArr.length; i++) {
//                logger.debug("contractNoArr =" + contractNoArr[i]);
            }
        }

        List<Object> retValue = contractDao.getContractIdWithCustomerName(contractNoArr);

        if (retValue == null) {
            return result;
        }

        Iterator<Object> it = retValue.iterator();
        while (it.hasNext()) {
            CustomerInfo custInfo = new CustomerInfo();
            Object[] obj = (Object[]) it.next();
            custInfo.setContractNo(obj[0].toString());
            custInfo.setCustomerName(obj[1].toString());
            result.add(custInfo);
        }

        return result;
    }

    /**
     * × @see
     * com.aimir.service.mvm.MvmDetailViewManager#getCustomerInfo(java.lang
     * .String)
     *
     * @Method Name : getCustomerInfo
     * @Date : 2010. 4. 8.
     * @Method 설명 :
     * @param contractNo 계약번호들
     * @param meterList 미터번호들
     * @return
     */
    public List<CustomerInfo> getCustomerInfo(String contractNo, String meterList) {
        List<CustomerInfo> result = new ArrayList<CustomerInfo>();
        String[] contractNoArr = contractNo.split(",");
        String[] meterNoArr = meterList.split(",");

        if (contractNo.startsWith(",") || "".equals(contractNo) || contractNo == null || contractNoArr == null) {
            return result;
        }

        List<Object> retValue = contractDao.getContractIdWithCustomerName(contractNoArr);

        if (retValue == null) {
            return result;
        }

        Map<String, String> contractMap = new HashMap<String, String>();
        Iterator<Object> it = retValue.iterator();

        while (it.hasNext()) {
            Object[] obj = (Object[]) it.next();
            contractMap.put(obj[0].toString(), obj[1].toString());
        }

        int len = contractNoArr.length;
        for (int i = 0; i < len; i++) {
            CustomerInfo custInfo = new CustomerInfo();
            custInfo.setContractNo(contractNoArr[i]);
            custInfo.setCustomerName(contractMap.get(contractNoArr[i]));
            custInfo.setMeterNo(meterNoArr[i]);
            result.add(custInfo);
        }
        return result;
    }

    /**
     * × @see
     * com.aimir.service.mvm.MvmDetailViewManager#getChannelInfo(java.lang
     * .String)
     *
     * @Method Name : getChannelInfo
     * @Date : 2010. 4. 8.
     * @Method 설명 : 상세화면과 달리 각 계약이 포함된 TABLE이 가지고 있는 채널을 추출
     * @param type ( 미터구분)
     * @return
     */
    public List<ChannelInfo> getChannelInfo(String type, String[] meterNos) {

        List<ChannelInfo> result = new ArrayList<ChannelInfo>();
//        List<List<ChannelInfo>> channelSet = new ArrayList<List<ChannelInfo>>();
//        List<ChannelInfo> channels = new ArrayList<ChannelInfo>();
//        List<ChannelInfo> rtnChannel = new ArrayList<ChannelInfo>();
        List<List<Map<String, String>>> channelSet = new ArrayList<List<Map<String, String>>>();
        List<Map<String, String>> channels = new ArrayList<Map<String, String>>();
        List<Map<String, String>> rtnChannel = new ArrayList<Map<String, String>>();
        Map<String, String> map = new HashMap<String, String>();

        List<List<String>> channelIdSet = new ArrayList<List<String>>();
        List<String> channelIds = new ArrayList<String>();
        List<String> rtnChannelIds = new ArrayList<String>();

        String meterType = ChangeMeterTypeName.valueOf(type).getCode();
        String tlbType = MeterType.valueOf(meterType).getLpClassName();
        Map<String, Object> hm = new HashMap<String, Object>();
//        String[] mdsIds = meterNos.split(",");
        Meter meter = null;
        List<Object> ojbList = null;
        String[] mdsIds = null;
        int deviceConfigId;

        hm.put("tlbType", tlbType);

//        List<Object> ojbList = channelConfigDao.getByList(hm);
//        if (ojbList.size() > 0) {
//            Iterator<Object> it = ojbList.iterator();
//            while (it.hasNext()) {
//                Object[] obj = (Object[]) it.next();
//                int codeId = (Integer) obj[0];
//                String codeNm = (String) obj[1];
//                String unit = (String) obj[2];
//                ChannelInfo channelInfo = new ChannelInfo();
//                channelInfo.setCodeId(String.valueOf(codeId));
//                channelInfo.setCodeName(codeNm + "(" + unit + ")");
//                result.add(channelInfo);
//            }
//        } else {// 해당 데이터가 없을경우 default 세팅
//            for (int i = 0; i < DefaultChannel.values().length; i++) {
//                String name = DefaultChannel.values()[i] + "";
//                int codeId = DefaultChannel.valueOf(name).getCode();
//                ChannelInfo channelInfo = new ChannelInfo();
//                channelInfo.setCodeId(String.valueOf(codeId));
//                channelInfo.setCodeName(name);
//                result.add(channelInfo);
//            }
//        }
        
        ////////////////////////////
        Iterator<Object> it = null;
        Object[] obj = null;
        int codeId = 0;
        String codeNm = null;
        String unit = null;
        String name = null;
        ChannelInfo channelInfo = new ChannelInfo();
        Map<String, String> channelNameMap = new HashMap<String, String>();     // channel 별 이름

        for (String meterNo : meterNos) {
            mdsIds = meterNo.split(",");

            for (String mdsId : mdsIds) {
//                channels = new ArrayList<ChannelInfo>();
//                channels = new ArrayList<Map<String, String>>();
                channelIds = new ArrayList<String>();

                meter = meterDao.findByCondition("mdsId", mdsId);
                ojbList = null;

                if (meter != null) {
                    // 미터의 devicemodel_Id가 존재할때, 존재하지 않을때는 그냥 table명으로 조회
                    if (meter.getModel() != null && meter.getModel().getId() > 0) {

                        if (meter.getModel().getDeviceConfig() != null) {
                            deviceConfigId = meter.getModel().getDeviceConfig().getId();
                            hm.put("deviceConfigId", deviceConfigId);
                            ojbList = channelConfigDao.getByList(hm);
                        }
                    }

                    if (ojbList != null && ojbList.size() > 0) {
                        it = ojbList.iterator();
                        obj = null;
                        while (it.hasNext()) {
                            obj = (Object[]) it.next();
                            codeId = (Integer) obj[0];
                            codeNm = (String) obj[1];
                            unit = (String) obj[2];
                            if (codeId != 0) {
//                                channelInfo = new ChannelInfo();
//                                channelInfo.setCodeId(String.valueOf(codeId));
//                                channelInfo.setCodeName(codeNm + "(" + unit + ")");
//                                channels.add(channelInfo);
//                                map = new HashMap<String, String>();
//                                map.put("codeId", String.valueOf(codeId));
//                                map.put("codeName", codeNm + "(" + unit + ")");
//                                channels.add(map);
                                channelNameMap.put(String.valueOf(codeId), codeNm + "(" + unit + ")");
                                channelIds.add(String.valueOf(codeId));
                            }
                        }
                    } else {// 해당 데이터가 없을경우 default 세팅
                        if (type.equals("EM")) {// 전기 default
                            for (int i = 0; i < ElectricityChannel.values().length; i++) {
                                name = ElectricityChannel.values()[i] + "";
                                codeId = ElectricityChannel.valueOf(name).getChannel();
                                if (codeId != 0) {
//                                    channelInfo = new ChannelInfo();
//                                    channelInfo.setCodeId(String.valueOf(codeId));
//                                    channelInfo.setCodeName(name);
//                                    channels.add(channelInfo);
//                                    map = new HashMap<String, String>();
//                                    map.put("codeId", String.valueOf(codeId));
//                                    map.put("codeName", name);
//                                    channels.add(map);
                                    channelNameMap.put(String.valueOf(codeId), name);
                                    channelIds.add(String.valueOf(codeId));
                                }
                            }
                        } else {// 전기 이외의 default
                            for (int i = 0; i < DefaultChannel.values().length; i++) {
                                name = DefaultChannel.values()[i] + "";
                                codeId = DefaultChannel.valueOf(name).getCode();
                                if (codeId != 0) {
//                                    channelInfo = new ChannelInfo();
//                                    channelInfo.setCodeId(String.valueOf(codeId));
//                                    channelInfo.setCodeName(name);
//                                    channels.add(channelInfo);
//                                    map = new HashMap<String, String>();
//                                    map.put("codeId", String.valueOf(codeId));
//                                    map.put("codeName", name);
//                                    channels.add(map);
                                    channelNameMap.put(String.valueOf(codeId), name);
                                    channelIds.add(String.valueOf(codeId));
                                }
                            }
                        }
                    }
                }
            }
//            channelSet.add(channels);
            channelIdSet.add(channelIds);
        }
        
//        for (int i = 0 ; i < channelSet.size() ; i++) {
//            if (i == 0) {
//                rtnChannel = channelSet.get(i);
//            } else {
//                rtnChannel.retainAll(channelSet.get(i));
//            }
//        }
        for (int i = 0 ; i < channelIdSet.size() ; i++) {
            if (i == 0) {
                rtnChannelIds = channelIdSet.get(i);
            } else {
                rtnChannelIds.retainAll(channelIdSet.get(i));
            }
        }

//        if (rtnChannel.size() > 0) {
//            for (Map<String, String> rtnMap : rtnChannel) {
//                // Channel Combo 생성 시 해당 Channel ID는 제외
//                if (rtnMap.get("codeId").equals("0") || rtnMap.get("codeId").equals("98") || rtnMap.get("codeId").equals("100")) {
//                    continue;
//                }
//                channelInfo = new ChannelInfo();
//                channelInfo.setCodeId(rtnMap.get("codeId"));
//                channelInfo.setCodeName(rtnMap.get("codeName"));
//                result.add(channelInfo);
//            }
//        }
        if (rtnChannelIds.size() > 0) {
            for (String rtn : rtnChannelIds) {
                // Channel Combo 생성 시 해당 Channel ID는 제외
                if (rtn.equals("0") || rtn.equals("98") || rtn.equals("100")) {
                    continue;
                }
                channelInfo = new ChannelInfo();
                channelInfo.setCodeId(rtn);
                channelInfo.setCodeName(channelNameMap.get(rtn));
                result.add(channelInfo);
            }
        }
        return result;
    }

    /*
     * 비교차트의 Load DurationChart Data를 생성한다.
     */
    public List<Object> getLoadDurationChartData(String[] values, String type) {
        Set<Condition> set = new HashSet<Condition>();
        String beginDate = values[0];
        String endDate = values[1];
        int channel = Integer.parseInt(values[5]);
        // String[] contract_number = values[4].split(",");
        // Integer[] custList = contractNumberToContractId(contract_number);

        // 채널선택
        Condition cdt1 = new Condition("id.channel", new Object[] { channel }, null, Restriction.EQ);
        set.add(cdt1);
        Condition cdt = new Condition("contract.id", null, null, Restriction.ORDERBY);
        set.add(cdt);
        // 조회년월일시
        if ((beginDate != null && beginDate.length() != 0) && (endDate != null && endDate.length() != 0)) {
            Condition cdt2 = new Condition("id.yyyymmddhh", new Object[] { beginDate, endDate }, null, Restriction.BETWEEN);//
            set.add(cdt2);
        }
        Condition cdt3 = new Condition("id.yyyymmddhh", null, null, Restriction.ORDERBY);
        set.add(cdt3);

        return null;
    }

    /**
     * × @see
     * com.aimir.service.mvm.MvmChartViewManager#getSearchDataHour(java.lang
     * .String[], java.lang.String)
     *
     * @Method Name : getSearchDataHour
     * @Date : 2010. 4. 8.
     * @Method 설명 : 비교차트 시간별데이터
     * @param values
     * @param type
     * @return
     */
    public List<MvmChartViewData> getSearchDataHour(String[] values, String type) {

        List<MvmChartViewData> result = new ArrayList<MvmChartViewData>();
        HashMap<String, Object> hm = new HashMap<String, Object>();
        Set<Condition> set = new HashSet<Condition>();
        String meterType = ChangeMeterTypeName.valueOf(type).getCode();
        String tlbType = MeterType.valueOf(meterType).getLpClassName();

        String beginDate =  values[0];
        String endDate   = values[1];
        String supplierId = values[6];
        int channel         = Integer.parseInt(values[5]);

        String[] contract_number = values[4].split(",");
        String[] meterList = values[7].split(",");
        Integer[] custList = contractNumberToContractId(contract_number);

        // 채널선택
        Condition cdt1 = new Condition("id.channel", new Object[] { channel }, null, Restriction.EQ);
        set.add(cdt1);
        // 조회년월일시
        if ((beginDate != null && beginDate.length() != 0) && (endDate != null && endDate.length() != 0)) {
            Condition cdt2 = new Condition("id.yyyymmddhh", new Object[] { beginDate, endDate }, null, Restriction.BETWEEN);//
            set.add(cdt2);
        }
        Condition cdt4 = new Condition("id.mdevType", new Object[] { DeviceType.Meter }, null, Restriction.EQ);
        set.add(cdt4);
        Condition cdt = new Condition("contract.id", null, null, Restriction.ORDERBY);
        set.add(cdt);
        Condition cdt3 = new Condition("id.yyyymmddhh", null, null, Restriction.ORDERBY);
        set.add(cdt3);

        Double co2StdValue =0D;
        if ("EM".equals(type)) {
            hm = MvmEmChartViewManagerImpl.getEMSearchDataHourByMeter(set, custList, meterList);
            co2StdValue = getTypeToCo2("EM");
            hm.put("co2StdValue", co2StdValue);
            result = getHourChangRowByCol(hm, supplierId, beginDate, endDate, channel, tlbType);
        } else if ("GM".equals(type)) {
            hm = MvmGmChartViewManagerImpl.getGMSearchDataHour(set, custList);
            co2StdValue = getTypeToCo2("GM");
            hm.put("co2StdValue", co2StdValue);
            result = getHourChangRowByCol(hm, supplierId);
        } else if ("WM".equals(type)) {
            hm = MvmWmChartViewManagerImpl.getWMSearchDataHour(set, custList);
            co2StdValue = getTypeToCo2("WM");
            hm.put("co2StdValue", co2StdValue);
            result = getHourChangRowByCol(hm, supplierId);
        } else if ("HM".equals(type)) {
            hm = MvmHmChartViewManagerImpl.getHMSearchDataHour(set, custList);
            co2StdValue = getTypeToCo2("HM");
            hm.put("co2StdValue", co2StdValue);
            result = getHourChangRowByCol(hm, supplierId);
        }

        return result;
    }

    /*
     * 시간별 객체 세팅하기
     */
    public List<MvmChartViewData> getHourChangRowByCol(HashMap<String, Object> hm, String supplierId) {
        return this.getHourChangRowByCol(hm, supplierId, null, null, null, null);
    }

    /*
     * 시간별 객체 세팅하기 - 조회일자 모두 표시
     */
    @SuppressWarnings("unchecked")
    public List<MvmChartViewData> getHourChangRowByCol(HashMap<String, Object> hm, String supplierId, String startDate,
            String endDate, Integer channelId, String tlbType) {
        List<MvmChartViewData> result = new ArrayList<MvmChartViewData>();
        List<MeteringLP> dataList = (List<MeteringLP>) hm.get("dataList");
        Integer[] contId = (Integer[]) hm.get("arrContId");
//        Double[] avgValue = (Double[]) hm.get("arrAvgValue");
//        Double[] maxValue = (Double[]) hm.get("arrMaxValue");
//        Double[] minValue = (Double[]) hm.get("arrMinValue");
//        Double[] sumValue = (Double[]) hm.get("arrSumValue");
        Double co2StdValue = (Double) hm.get("co2StdValue");

        Class<? extends MvmChartViewData> cls = null;
        Class<? extends MeteringLP> clp = null;
        String tmpYYmmDDhh = ""; // 일자 비교할 기준값
        MvmChartViewData mcvd = null;
        MeteringLP mlp = null;
        Integer lpInterval = 60;
//        List<Map<String, Object>> dataMapList = null;
//        Map<String, Object> dataMap = null;
        Class<?>[] parameterTypes = {};
        Object[] parameters = {};
        Method method = null;
        Meter meter = null;
        Map<String, Object> searchCondition = null;
        List<Object> objList = new ArrayList<Object>();
        ChannelCalcMethod chMethodConst = null;
        Map<String, Object> chMethodMap = new HashMap<String, Object>();

        // 일자 비교할 기준값 추출
        List<String> hourList = new ArrayList<String>();
        if (dataList != null && dataList.size() > 0) {
            try {
                if (startDate != null && endDate != null && startDate.length() == 10 && endDate.length() == 10) {
                    String tmpDateHour = startDate;
                    int limit = 0;

                    try {
                        limit = (TimeUtil.getDayDuration(startDate, endDate) + 1) * 24;

                        // 조회조건 내 모든 일자 가져오기
                        for (int k = 0; k < limit; k++) {
                            hourList.add(tmpDateHour);

                            if (tmpDateHour.compareTo(endDate) >= 0) {
                                break;
                            }

                            tmpDateHour = TimeUtil.getPreHour(tmpDateHour + "0000", -1).substring(0, 10);
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                } else {
                    for (int i = 0; i < dataList.size(); i++) {
                        String tmpDate = dataList.get(i).getYyyymmddhh();
                        if (i == 0) {
                            tmpYYmmDDhh = tmpDate;
                            hourList.add(dataList.get(i).getYyyymmddhh());
                        }

                        if (!tmpYYmmDDhh.equals(tmpDate)) {
                            hourList.add(dataList.get(i).getYyyymmddhh());
                        }
                        tmpYYmmDDhh = tmpDate;
                    }
                }

                Double[][] tmpValue = new Double[hourList.size()][contId.length];
                String[] firstCol = new String[hourList.size()];
                for (int tmpIdx = 0; tmpIdx < hourList.size(); tmpIdx++) {
                    firstCol[tmpIdx] = hourList.get(tmpIdx);
                }
                // 데이터 추출
                for (int i = 0; i < dataList.size(); i++) {
                    String compdate = dataList.get(i).getYyyymmddhh();
                    Integer compContID = dataList.get(i).getContract().getId();
//                    dataMap = getValuesMap(dataList.get(i));

                    meter = dataList.get(i).getMeter();
                    // lp_interval 가져오기
                    if (meter != null) {
                        lpInterval = meter.getLpInterval();
                        // lp interval 이 null 이면 60 으로 설정
                        if (lpInterval == null) {
                            lpInterval = 60;
                        }

                        if (tlbType != null) {  // channel method 가져오기
//                            channelMethod = null;
                            chMethodConst = null;

                            if (chMethodMap.containsKey(meter.getMdsId())) {
                                chMethodConst = (ChannelCalcMethod)chMethodMap.get(meter.getMdsId());
                            } else {
                                searchCondition = new HashMap<String, Object>();
                                searchCondition.put("tlbType", tlbType);
                                searchCondition.put("chmethodChannelId", channelId);
                                if (meter.getModel() != null && meter.getModel().getDeviceConfig() != null) {
                                    searchCondition.put("deviceConfigId", meter.getModel().getDeviceConfig().getId());
//                                    Map<String, Object> tmpMap = searchCondition;
                                    objList = channelConfigDao.getByList(searchCondition);

                                    if (objList.size() <= 0) {
                                        chMethodConst = null;
                                    } else {
                                        for (Object obj : objList) {
                                            Object[] arr = (Object[])obj;
                                            int codeId = (Integer)arr[0];
                                            if(codeId != 0 ) {
                                                chMethodConst = (ChannelCalcMethod)arr[3];
                                                chMethodMap.put(meter.getMdsId(), chMethodConst);
                                                break;
                                            }
                                        }
                                    }
                                } else {
                                    chMethodConst = null;
                                }
                            }
                        } else {
                            chMethodConst = null;
                        }

                    } else {
                        lpInterval = 60;
                    }

                    mlp = dataList.get(i);
                    clp = mlp.getClass();

                    BigDecimal bdValue = null;      // 임시계산용
                    Double value = null;            // 임시
                    int avgCnt = 0;

                    for (int rowIdx = 0; rowIdx < hourList.size(); rowIdx++) {
                        String stdDate = hourList.get(rowIdx);

                        for (int colIdx = 0; colIdx < contId.length; colIdx++) {
                            Integer stdConId = contId[colIdx];

                            if (compdate.equals(stdDate) && (compContID == stdConId)) {
                                bdValue = null;

                                for (int j = 0, k = 0 ; k < 60 ; j++, k = j * lpInterval) { // 주기별 데이터 조회
//                                    value = (Double)dataMap.get("value_" + StringUtil.frontAppendNStr('0', Integer.toString(k), 2));
                                    value = null;
                                    try {
                                        method = clp.getMethod("getValue_" + StringUtil.frontAppendNStr('0', Integer.toString(k), 2), parameterTypes);
                                        value = (Double)method.invoke(mlp, parameters);
                                    } catch (SecurityException e) {
                                        e.printStackTrace();
                                    } catch (IllegalArgumentException e) {
                                        e.printStackTrace();
                                    } catch (NoSuchMethodException e) {
                                        e.printStackTrace();
                                    } catch (InvocationTargetException e) {
                                        e.printStackTrace();
                                    }

                                    if (value != null) {
                                        if (bdValue == null) {
                                            bdValue = new BigDecimal(value.toString());
                                        } else if (chMethodConst != null) {
                                            //bdValue = bdValue.add(new BigDecimal(value.toString()));
                                            
                                            switch(chMethodConst) {
                                                case AVG:
                                                    avgCnt++;
                                                case SUM:
                                                    bdValue = bdValue.add(new BigDecimal(value.toString()));
                                                    break;
                                                case MAX:
                                                    bdValue = bdValue.max(new BigDecimal(value.toString()));
                                                    break;
                                            }
                                        } else {
                                            bdValue = bdValue.add(new BigDecimal(value.toString()));
                                        }
                                    }
                                }
//                                tmpValue[rowIdx][colIdx] = dataList.get(i).getValue_00();
                                tmpValue[rowIdx][colIdx] = (bdValue == null) ? null : bdValue.doubleValue();

                                if (chMethodConst != null && chMethodConst.equals(ChannelCalcMethod.AVG)) {
                                    if (avgCnt <= 0) {
                                        bdValue = null;
                                    } else {
                                        bdValue = bdValue.divide(new BigDecimal(avgCnt), MathContext.DECIMAL128);
                                    }
                                } else {
                                    tmpValue[rowIdx][colIdx] = (bdValue == null) ? null : bdValue.doubleValue();
                                }
                            }
                        }
                    }
                }

                Supplier supplier = supplierDao.get(Integer.parseInt(supplierId));
                DecimalFormat df = DecimalUtil.getDecimalFormat(supplier.getMd());

                String lang = supplier.getLang().getCode_2letter();
                String country = supplier.getCountry().getCode_2letter();

                ////
                List<BigDecimal> bdTotalSumList = new ArrayList<BigDecimal>();
                List<BigDecimal> bdTotalAvgList = new ArrayList<BigDecimal>();
                List<BigDecimal> bdTotalMaxList = new ArrayList<BigDecimal>();
                List<BigDecimal> bdTotalMinList = new ArrayList<BigDecimal>();
                List<Integer> intTotalCount = new ArrayList<Integer>();
                BigDecimal bdAvgValue = null;

                // Sum/Max/Min 계산하기
                for (int rowIdx = 0; rowIdx < hourList.size(); rowIdx++) {

                    for (int colIdx = 0; colIdx < contId.length; colIdx++) {

                        if (rowIdx == 0) {
                            if (tmpValue[rowIdx][colIdx] != null) {
                                bdTotalSumList.add(new BigDecimal(tmpValue[rowIdx][colIdx].toString()));
                                bdTotalMaxList.add(new BigDecimal(tmpValue[rowIdx][colIdx].toString()));
                                bdTotalMinList.add(new BigDecimal(tmpValue[rowIdx][colIdx].toString()));
                                intTotalCount.add(1);
                            } else {
                                bdTotalSumList.add(null);
                                bdTotalMaxList.add(null);
                                bdTotalMinList.add(null);
                                intTotalCount.add(0);
                            }
                        } else {
                            if (tmpValue[rowIdx][colIdx] != null) {
                                if (bdTotalSumList.get(colIdx) == null) {
                                    bdTotalSumList.set(colIdx, new BigDecimal(tmpValue[rowIdx][colIdx].toString()));
                                } else {
                                    bdTotalSumList.set(colIdx, bdTotalSumList.get(colIdx).add(new BigDecimal(tmpValue[rowIdx][colIdx].toString())));
                                }
                                if (bdTotalMaxList.get(colIdx) == null) {
                                    bdTotalMaxList.set(colIdx, new BigDecimal(tmpValue[rowIdx][colIdx].toString()));
                                } else {
                                    bdTotalMaxList.set(colIdx, bdTotalMaxList.get(colIdx).max(new BigDecimal(tmpValue[rowIdx][colIdx].toString())));
                                }
                                if (bdTotalMinList.get(colIdx) == null) {
                                    bdTotalMinList.set(colIdx, new BigDecimal(tmpValue[rowIdx][colIdx].toString()));
                                } else {
                                    bdTotalMinList.set(colIdx, bdTotalMinList.get(colIdx).min(new BigDecimal(tmpValue[rowIdx][colIdx].toString())));
                                }
                                intTotalCount.set(colIdx, intTotalCount.get(colIdx) + 1);
                            }
                        }
                    }

//                    for (int colIdx = 0; colIdx < contId.length; colIdx++) {
//                        if (bdTotalSumList.get(colIdx) == null) {
//                            bdAvgValue = new BigDecimal("0");
//                        } else if (intTotalCount.get(colIdx) > 0) {
//                            bdAvgValue = bdTotalSumList.get(colIdx).divide(new BigDecimal(intTotalCount.get(colIdx)), MathContext.DECIMAL32);
//                        } else {
//                            bdAvgValue = new BigDecimal("0");
//                        }
//                        cls.getField("user" + colIdx + "Avg").set(mcvd, bdAvgValue.doubleValue());
//                        cls.getField("user" + colIdx + "Max").set(mcvd, (bdTotalMaxList.get(colIdx) == null) ? 0D : bdTotalMaxList.get(colIdx).doubleValue());
////                        cls.getField("user" + colIdx + "FormatValue").set(mcvd, df.format(getNullToDouble(tmpValue[rowIdx][colIdx])));
////                        cls.getField("user" + colIdx + "FormatCo2").set(mcvd, df.format(getChangeCo2Double(co2StdValue, tmpValue[rowIdx][colIdx])));
//                    }
//                    result.add(mcvd);
                }

                // Avg 계산하기
                for (int colIdx = 0; colIdx < contId.length; colIdx++) {
                    if (bdTotalSumList.get(colIdx) == null) {
                        bdAvgValue = null;
                    } else if (intTotalCount.get(colIdx) > 0) {
                        bdAvgValue = bdTotalSumList.get(colIdx).divide(new BigDecimal(intTotalCount.get(colIdx)), MathContext.DECIMAL32);
                    } else {
                        bdAvgValue = new BigDecimal("0");
                    }
                    bdTotalAvgList.add(bdAvgValue);
                }

                // 데이터 담기
                for (int rowIdx = 0; rowIdx < hourList.size(); rowIdx++) {
                    mcvd = new MvmChartViewData();
                    cls = mcvd.getClass();
//                    cls.getField("firstCol").set(mcvd, firstCol[rowIdx]);
                    cls.getField("firstCol").set(mcvd, TimeLocaleUtil.getLocaleDateHour(firstCol[rowIdx], lang, country));

                    for (int colIdx = 0; colIdx < contId.length; colIdx++) {
                        //                      cls.getField("user" + colIdx + "Value").set(mcvd, getDoubleToStirng(tmpValue[rowIdx][colIdx]));
                        //                      cls.getField("user" + colIdx + "Co2").set(mcvd, getChangeCo2(co2StdValue, tmpValue[rowIdx][colIdx]));

                        cls.getField("user" + colIdx + "Value").set(mcvd, getNullToDouble(tmpValue[rowIdx][colIdx]));
                        cls.getField("user" + colIdx + "Co2").set(mcvd, getChangeCo2Double(co2StdValue, tmpValue[rowIdx][colIdx]));
                        cls.getField("user" + colIdx + "Avg").set(mcvd, (bdTotalAvgList.get(colIdx) == null) ? 0D : bdTotalAvgList.get(colIdx).doubleValue());
                        cls.getField("user" + colIdx + "Max").set(mcvd, (bdTotalMaxList.get(colIdx) == null) ? 0D : bdTotalMaxList.get(colIdx).doubleValue());
                        cls.getField("user" + colIdx + "DecimalValue").set(mcvd, (tmpValue[rowIdx][colIdx] == null) ? "- " : df.format(getNullToDouble(tmpValue[rowIdx][colIdx])));
                        cls.getField("user" + colIdx + "DecimalCo2").set(mcvd, (tmpValue[rowIdx][colIdx] == null) ? "- " : df.format(getChangeCo2Double(co2StdValue, tmpValue[rowIdx][colIdx])));

//                        if (colIdx == 0) {
//                            if (tmpValue[rowIdx][colIdx] != null) {
//                                bdTotalSumList.add(new BigDecimal(tmpValue[rowIdx][colIdx].toString()));
//                                bdTotalMaxList.add(new BigDecimal(tmpValue[rowIdx][colIdx].toString()));
//                                bdTotalMinList.add(new BigDecimal(tmpValue[rowIdx][colIdx].toString()));
//                                intTotalCount.add(1);
//                            } else {
//                                bdTotalSumList.add(null);
//                                bdTotalMaxList.add(null);
//                                bdTotalMinList.add(null);
//                                intTotalCount.add(0);
//                            }
//                        } else {
//                            if (tmpValue[rowIdx][colIdx] != null) {
//                                if (bdTotalSumList.get(colIdx) == null) {
//                                    bdTotalSumList.set(colIdx, new BigDecimal(tmpValue[rowIdx][colIdx].toString()));
//                                } else {
//                                    bdTotalSumList.set(colIdx, bdTotalSumList.get(colIdx).add(new BigDecimal(tmpValue[rowIdx][colIdx].toString())));
//                                }
//                                if (bdTotalMaxList.get(colIdx) == null) {
//                                    bdTotalMaxList.set(colIdx, new BigDecimal(tmpValue[rowIdx][colIdx].toString()));
//                                } else {
//                                    bdTotalMaxList.set(colIdx, bdTotalMaxList.get(colIdx).max(new BigDecimal(tmpValue[rowIdx][colIdx].toString())));
//                                }
//                                if (bdTotalMinList.get(colIdx) == null) {
//                                    bdTotalMinList.set(colIdx, new BigDecimal(tmpValue[rowIdx][colIdx].toString()));
//                                } else {
//                                    bdTotalMinList.set(colIdx, bdTotalMinList.get(colIdx).min(new BigDecimal(tmpValue[rowIdx][colIdx].toString())));
//                                }
//                                intTotalCount.set(colIdx, intTotalCount.get(colIdx) + 1);
//                            }
//                        }
                    }

//                    for (int colIdx = 0; colIdx < contId.length; colIdx++) {
////                        cls.getField("user" + colIdx + "Value").set(mcvd, df.format(getNullToDouble(tmpValue[rowIdx][colIdx])));
////                        cls.getField("user" + colIdx + "Co2").set(mcvd, df.format(getChangeCo2Double(co2StdValue, tmpValue[rowIdx][colIdx])));
////                        cls.getField("user" + colIdx + "Avg").set(mcvd, avgValue[colIdx]);
////                        cls.getField("user" + colIdx + "Max").set(mcvd, maxValue[colIdx]);
//
//                        if (bdTotalSumList.get(colIdx) == null) {
//                            bdAvgValue = new BigDecimal("0");
//                        } else if (intTotalCount.get(colIdx) > 0) {
//                            bdAvgValue = bdTotalSumList.get(colIdx).divide(new BigDecimal(intTotalCount.get(colIdx)), MathContext.DECIMAL32);
//                        } else {
//                            bdAvgValue = new BigDecimal("0");
//                        }
//                        cls.getField("user" + colIdx + "Avg").set(mcvd, bdAvgValue.doubleValue());
//                        cls.getField("user" + colIdx + "Max").set(mcvd, (bdTotalMaxList.get(colIdx) == null) ? 0D : bdTotalMaxList.get(colIdx).doubleValue());
////                        cls.getField("user" + colIdx + "FormatValue").set(mcvd, df.format(getNullToDouble(tmpValue[rowIdx][colIdx])));
////                        cls.getField("user" + colIdx + "FormatCo2").set(mcvd, df.format(getChangeCo2Double(co2StdValue, tmpValue[rowIdx][colIdx])));
//                    }
                    result.add(mcvd);
                }

                // 합계 세팅
                mcvd = new MvmChartViewData();
                mcvd.setFirstCol("Sum");
                cls = mcvd.getClass();

//                Double sum = null;

//                for (int j = 0; j < contId.length; j++) {
//                    sum = getNullToDouble(sumValue[j]);
////                    if (sumValue[j] != null) {
//                        cls.getField("user" + j + "Value").set(mcvd, df.format(sum));
//                        cls.getField("user" + j + "Co2").set(mcvd, df.format(getChangeCo2Double(co2StdValue, sum)));
////                    }
//                }
                for (int j = 0; j < contId.length; j++) {
//                    cls.getField("user" + j + "Value").set(mcvd, (bdTotalSumList.get(j) == null) ? "" : df.format(bdTotalSumList.get(j).doubleValue()));
//                    cls.getField("user" + j + "Co2").set(mcvd, (bdTotalSumList.get(j) == null) ? "" : df.format(getChangeCo2Double(co2StdValue, bdTotalSumList.get(j).doubleValue())));
                    cls.getField("user" + j + "DecimalValue").set(mcvd, (bdTotalSumList.get(j) == null) ? "" : df.format(bdTotalSumList.get(j).doubleValue()));
                    cls.getField("user" + j + "DecimalCo2").set(mcvd, (bdTotalSumList.get(j) == null) ? "" : df.format(getChangeCo2Double(co2StdValue, bdTotalSumList.get(j).doubleValue())));
                }

                result.add(mcvd);

                // 평균(최대/최소값)세팅
                mcvd = new MvmChartViewData();
                mcvd.setFirstCol("Avg(Max/Min)");
                cls = mcvd.getClass();

                StringBuilder sbVal = null;
                StringBuilder sbCo2 = null;

//                Double avg = null;
//                Double max = null;
//                Double min = null;

                for (int j = 0; j < contId.length; j++) {
                    
                    if (bdTotalSumList.get(j) == null) {
                        bdAvgValue = null;
                    } else if (intTotalCount.get(j) > 0) {
                        bdAvgValue = bdTotalSumList.get(j).divide(new BigDecimal(intTotalCount.get(j)), MathContext.DECIMAL32);
                    } else {
                        bdAvgValue = new BigDecimal("0");
                    }

//                    avg = getNullToDouble(avgValue[j]);
//                    max = getNullToDouble(maxValue[j]);
//                    min = getNullToDouble(minValue[j]);

                    sbVal = new StringBuilder();
                    sbVal.append((bdAvgValue == null) ? "" : df.format(bdAvgValue.doubleValue())).append('(');
                    sbVal.append((bdTotalMaxList.get(j) == null) ? "" : df.format(bdTotalMaxList.get(j).doubleValue())).append('/');
                    sbVal.append((bdTotalMinList.get(j) == null) ? "" : df.format(bdTotalMinList.get(j).doubleValue())).append(')');

                    sbCo2 = new StringBuilder();
                    sbCo2.append((bdAvgValue == null) ? "" : df.format(getChangeCo2Double(co2StdValue, bdAvgValue.doubleValue()))).append('(');
                    sbCo2.append((bdTotalMaxList.get(j) == null) ? "" : df.format(getChangeCo2Double(co2StdValue, bdTotalMaxList.get(j).doubleValue()))).append('/');
                    sbCo2.append((bdTotalMinList.get(j) == null) ? "" : df.format(getChangeCo2Double(co2StdValue, bdTotalMinList.get(j).doubleValue()))).append(')');

//                    cls.getField("user" + j + "Value").set(mcvd, sbVal.toString());
//                    cls.getField("user" + j + "Co2").set(mcvd, sbCo2.toString());
                    cls.getField("user" + j + "DecimalValue").set(mcvd, sbVal.toString());
                    cls.getField("user" + j + "DecimalCo2").set(mcvd, sbCo2.toString());
                }
                result.add(mcvd);

            } catch (NoSuchFieldException ne) {
                System.out.println("NoSuchFieldException " + ne.getMessage());
            }

            catch (IllegalAccessException ie) {
                System.out.println("IllegalAccessException" + ie.getMessage());
            }
        }
        return result;
    }

    /**
     * × @see
     * com.aimir.service.mvm.MvmChartViewManager#getSearchDataDay(java.lang
     * .String[], java.lang.String)
     *
     * @Method Name : getSearchDataDay
     * @Date : 2010. 4. 8.
     * @Method 설명 : 일/기간별데이터
     * @param values
     * @param type
     * @return
     */
    public List<MvmChartViewData> getSearchDataDay(String[] values, String type) {

        List<MvmChartViewData> result = new ArrayList<MvmChartViewData>();
        HashMap<String, Object> hm = new HashMap<String, Object>();
        Set<Condition> set = new HashSet<Condition>();

        String beginDate =  values[0];
        String endDate   = values[1];
        String supplierId = values[6];
        int channel         = Integer.parseInt(values[5]);
        String[] contract_number = values[4].split(",");
        Integer[] custList = contractNumberToContractId(contract_number);

        // 채널선택
        Condition cdt = new Condition("contract.id", null, null, Restriction.ORDERBY);
        set.add(cdt);
        Condition cdt1 = new Condition("id.channel", new Object[] { channel }, null, Restriction.EQ);
        set.add(cdt1);
        // 조회년월일시
        if ((beginDate != null && beginDate.length() != 0) && (endDate != null && endDate.length() != 0)) {
            Condition cdt2 = new Condition("id.yyyymmdd", new Object[] { beginDate, endDate }, null, Restriction.BETWEEN);//
            set.add(cdt2);
        }
        Condition cdt3 = new Condition("id.yyyymmdd", null, null, Restriction.ORDERBY);
        set.add(cdt3);
        Double co2StdValue =0D;
        if ("EM".equals(type)) {
            hm = MvmEmChartViewManagerImpl.getEMSearchDataDay(set, custList);
            co2StdValue = getTypeToCo2("EM");
            hm.put("co2StdValue", co2StdValue);
            result = getDayChangRowByCol(hm, supplierId, beginDate, endDate);
        }
        else if ("GM".equals(type)) {
            hm =  MvmGmChartViewManagerImpl.getGMSearchDataDay(set, custList);
            co2StdValue = getTypeToCo2("GM");
            hm.put("co2StdValue", co2StdValue);
            result = getDayChangRowByCol(hm, supplierId);
        } else if ("WM".equals(type)) {
            hm =  MvmWmChartViewManagerImpl.getWMSearchDataDay(set, custList);
            co2StdValue = getTypeToCo2("WM");
            hm.put("co2StdValue", co2StdValue);
            result = getDayChangRowByCol(hm, supplierId);
        } else if ("HM".equals(type)) {
            hm =  MvmHmChartViewManagerImpl.getHMSearchDataDay(set, custList);
            co2StdValue = getTypeToCo2("HM");
            hm.put("co2StdValue", co2StdValue);
            result = getDayChangRowByCol(hm, supplierId);
        }

        return result;
    }

    /*
     * 일자/기간별 객체 세팅하기
     */
    public List<MvmChartViewData> getDayChangRowByCol(HashMap<String, Object> hm, String supplierId) {
        return getDayChangRowByCol(hm, supplierId, null, null);
    }

    /*
     * 일자/기간별 객체 세팅하기
     */
    @SuppressWarnings("unchecked")
    public List<MvmChartViewData> getDayChangRowByCol(HashMap<String, Object> hm, String supplierId, String startDate, String endDate) {
        List<MvmChartViewData> result = new ArrayList<MvmChartViewData>();

        MvmChartViewData mcvd = null;
        Class<? extends MvmChartViewData> cls = null;

        List<MeteringDay> dataList = (List<MeteringDay>) hm.get("dataList");

        Integer[] contId = (Integer[]) hm.get("arrContId");
//        Double[] avgValue = (Double[]) hm.get("arrAvgValue");
//        Double[] maxValue = (Double[]) hm.get("arrMaxValue");
//        Double[] minValue = (Double[]) hm.get("arrMinValue");
//        Double[] sumValue = (Double[]) hm.get("arrSumValue");
        Double co2StdValue = (Double) hm.get("co2StdValue");

//        Map<String, Object> dataMap = null;

        Class<? extends MeteringDay> clas = null;
        MeteringDay mtr = null;
        Class<?>[] parameterTypes = {};
        Object[] parameters = {};
        Method method = null;
        BigDecimal bdValue = null;      // 임시계산용
        Double value = null;            // 임시

        if (dataList != null && dataList.size() > 0) {

            try {
                String[] arrStdDate = null;
                int tmpIdx = 0;

                if (startDate != null && endDate != null && startDate.length() == 8 && endDate.length() == 8) {
                    List<String> tmpList = new ArrayList<String>();
                    String tmpDate = startDate;
                    int limit = 0;

                    try {
                        limit = TimeUtil.getDayDuration(startDate, endDate) + 1;

                        // 조회조건 내 모든 일자 가져오기
                        for (int k = 0; k < limit; k++) {
                            tmpList.add(tmpDate);

                            if (tmpDate.compareTo(endDate) >= 0) {
                                break;
                            }

                            tmpDate = TimeUtil.getPreDay(tmpDate + "000000", -1).substring(0, 8);
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    arrStdDate = new String[tmpList.size()];
                    tmpList.toArray(arrStdDate);
                } else {
                    // 일자 비교할 기준값 추출
                    HashSet<String> dayHashSet = new HashSet<String>();

                    // 행을 그리기 위한 기준으로 일자를 구함
                    for (int i = 0; i < dataList.size(); i++) {
                        dayHashSet.add(dataList.get(i).getYyyymmdd());
                    }

                    Set<String> dayList = new TreeSet<String>(dayHashSet);

                    arrStdDate = new String[dayList.size()];
                    Iterator<String> itr = dayList.iterator();
                    while (itr.hasNext()) {
                        arrStdDate[tmpIdx] = itr.next();
                        tmpIdx++;
                    }
                }

                Double[][] tmpValue = new Double[arrStdDate.length][contId.length];
                String[] firstCol = new String[arrStdDate.length];

                // 데이터 추출
                for (int i = 0; i < dataList.size(); i++) {
                    String compdate = dataList.get(i).getYyyymmdd();
                    Integer compContID = dataList.get(i).getContract().getId();

                    mtr = dataList.get(i);
                    clas = mtr.getClass();

                    bdValue = null;
                    value = null;

                    for (int rowIdx = 0; rowIdx < arrStdDate.length; rowIdx++) {
                        String stdDate = arrStdDate[rowIdx];
                        firstCol[rowIdx] = arrStdDate[rowIdx];

                        for (int colIdx = 0; colIdx < contId.length; colIdx++) {
                            Integer stdConId = contId[colIdx];

                            if (compdate.equals(stdDate) && (compContID == stdConId)) {
//                                tmpValue[rowIdx][colIdx] = dataList.get(i).getValue_00();
                                bdValue = null;

//                                for (int j = 0 ; j < 24 ; j++) { // 시간별 데이터 합산
//                                    value = null;
//                                    try {
//                                        method = clas.getMethod("getValue_" + StringUtil.frontAppendNStr('0', Integer.toString(j), 2), parameterTypes);
//                                        value = (Double)method.invoke(mtr, parameters);
//                                    } catch (SecurityException e) {
//                                        e.printStackTrace();
//                                    } catch (IllegalArgumentException e) {
//                                        e.printStackTrace();
//                                    } catch (NoSuchMethodException e) {
//                                        e.printStackTrace();
//                                    } catch (InvocationTargetException e) {
//                                        e.printStackTrace();
//                                    }
//
//                                    if (value != null) {
//                                        if (bdValue == null) {
//                                            bdValue = new BigDecimal(value.toString());
//                                        } else {
//                                            bdValue = bdValue.add(new BigDecimal(value.toString()));
//                                        }
//                                    }
//                                }
                                
//                                tmpValue[rowIdx][colIdx] = (bdValue == null) ? null : bdValue.doubleValue();
                                tmpValue[rowIdx][colIdx] = (mtr.getTotal() == null) ? null : DecimalUtil.ConvertNumberToDouble(mtr.getTotal());
                            }
                        }
                    }
                }

                Supplier supplier = supplierDao.get(Integer.parseInt(supplierId));
                DecimalFormat df = DecimalUtil.getDecimalFormat(supplier.getMd());

                String lang = supplier.getLang().getCode_2letter();
                String country = supplier.getCountry().getCode_2letter();

                List<BigDecimal> bdTotalSumList = new ArrayList<BigDecimal>();
                List<BigDecimal> bdTotalAvgList = new ArrayList<BigDecimal>();
                List<BigDecimal> bdTotalMaxList = new ArrayList<BigDecimal>();
                List<BigDecimal> bdTotalMinList = new ArrayList<BigDecimal>();
                List<Integer> intTotalCount = new ArrayList<Integer>();
                BigDecimal bdAvgValue = null;

                // Sum/Max/Min 계산하기
                for (int rowIdx = 0; rowIdx < arrStdDate.length; rowIdx++) {

                    for (int colIdx = 0; colIdx < contId.length; colIdx++) {

                        if (rowIdx == 0) {
                            if (tmpValue[rowIdx][colIdx] != null) {
                                bdTotalSumList.add(new BigDecimal(tmpValue[rowIdx][colIdx].toString()));
                                bdTotalMaxList.add(new BigDecimal(tmpValue[rowIdx][colIdx].toString()));
                                bdTotalMinList.add(new BigDecimal(tmpValue[rowIdx][colIdx].toString()));
                                intTotalCount.add(1);
                            } else {
                                bdTotalSumList.add(null);
                                bdTotalMaxList.add(null);
                                bdTotalMinList.add(null);
                                intTotalCount.add(0);
                            }
                        } else {
                            if (tmpValue[rowIdx][colIdx] != null) {
                                if (bdTotalSumList.get(colIdx) == null) {
                                    bdTotalSumList.set(colIdx, new BigDecimal(tmpValue[rowIdx][colIdx].toString()));
                                } else {
                                    bdTotalSumList.set(colIdx, bdTotalSumList.get(colIdx).add(new BigDecimal(tmpValue[rowIdx][colIdx].toString())));
                                }
                                if (bdTotalMaxList.get(colIdx) == null) {
                                    bdTotalMaxList.set(colIdx, new BigDecimal(tmpValue[rowIdx][colIdx].toString()));
                                } else {
                                    bdTotalMaxList.set(colIdx, bdTotalMaxList.get(colIdx).max(new BigDecimal(tmpValue[rowIdx][colIdx].toString())));
                                }
                                if (bdTotalMinList.get(colIdx) == null) {
                                    bdTotalMinList.set(colIdx, new BigDecimal(tmpValue[rowIdx][colIdx].toString()));
                                } else {
                                    bdTotalMinList.set(colIdx, bdTotalMinList.get(colIdx).min(new BigDecimal(tmpValue[rowIdx][colIdx].toString())));
                                }
                                intTotalCount.set(colIdx, intTotalCount.get(colIdx) + 1);
                            }
                        }
                    }
                }

                // Avg 계산하기
                for (int colIdx = 0; colIdx < contId.length; colIdx++) {
                    if (bdTotalSumList.get(colIdx) == null) {
                        bdAvgValue = null;
                    } else if (intTotalCount.get(colIdx) > 0) {
                        bdAvgValue = bdTotalSumList.get(colIdx).divide(new BigDecimal(intTotalCount.get(colIdx)), MathContext.DECIMAL32);
                    } else {
                        bdAvgValue = new BigDecimal("0");
                    }
                    bdTotalAvgList.add(bdAvgValue);
                }

                // 데이터 담기
                for (int rowIdx = 0; rowIdx < arrStdDate.length; rowIdx++) {
                    mcvd = new MvmChartViewData();
                    cls = mcvd.getClass();
//                    cls.getField("firstCol").set(mcvd, firstCol[rowIdx]);
                    cls.getField("firstCol").set(mcvd, TimeLocaleUtil.getLocaleDate(firstCol[rowIdx], lang, country));

                    for (int colIdx = 0; colIdx < contId.length; colIdx++) {
//                        cls.getField("user" + colIdx + "Value").set(mcvd, df.format(getNullToDouble(tmpValue[rowIdx][colIdx])));
//                        cls.getField("user" + colIdx + "Co2").set(mcvd, df.format(getChangeCo2Double(co2StdValue, tmpValue[rowIdx][colIdx])));
//                        cls.getField("user" + colIdx + "Avg").set(mcvd, avgValue[colIdx]);
//                        cls.getField("user" + colIdx + "Max").set(mcvd, maxValue[colIdx]);
                        cls.getField("user" + colIdx + "Value").set(mcvd, (tmpValue[rowIdx][colIdx] == null) ? 0D : getNullToDouble(tmpValue[rowIdx][colIdx]));
                        cls.getField("user" + colIdx + "Co2").set(mcvd, (tmpValue[rowIdx][colIdx] == null) ? 0D : getChangeCo2Double(co2StdValue, tmpValue[rowIdx][colIdx]));
                        cls.getField("user" + colIdx + "Avg").set(mcvd, (bdTotalAvgList.get(colIdx) == null) ? 0D : bdTotalAvgList.get(colIdx).doubleValue());
                        cls.getField("user" + colIdx + "Max").set(mcvd, (bdTotalMaxList.get(colIdx) == null) ? 0D : bdTotalMaxList.get(colIdx).doubleValue());
                        cls.getField("user" + colIdx + "DecimalValue").set(mcvd, (tmpValue[rowIdx][colIdx] == null) ? "- " : df.format(getNullToDouble(tmpValue[rowIdx][colIdx])));
                        cls.getField("user" + colIdx + "DecimalCo2").set(mcvd, (tmpValue[rowIdx][colIdx] == null) ? "- " : df.format(getChangeCo2Double(co2StdValue, tmpValue[rowIdx][colIdx])));
                    }
                    result.add(mcvd);
                }

                // 합계 세팅
                mcvd = new MvmChartViewData();
//                mcvd.setFirstCol("(Total)");
                mcvd.setFirstCol("Sum");
                cls = mcvd.getClass();
//                Double sum = null;

//                for (int j = 0; j < contId.length; j++) {
//                    sum = getNullToDouble(sumValue[j]);
//
//                    // if (sumValue[j] != null) {
//                    cls.getField("user" + j + "Value").set(mcvd, df.format(sum));
//                    cls.getField("user" + j + "Co2").set(mcvd, df.format(getChangeCo2Double(co2StdValue, sum)));
//                    // }
//                }
                
                for (int j = 0; j < contId.length; j++) {
                    cls.getField("user" + j + "DecimalValue").set(mcvd, (bdTotalSumList.get(j) == null) ? "" : df.format(bdTotalSumList.get(j).doubleValue()));
                    cls.getField("user" + j + "DecimalCo2").set(mcvd, (bdTotalSumList.get(j) == null) ? "" : df.format(getChangeCo2Double(co2StdValue, bdTotalSumList.get(j).doubleValue())));
                }

                result.add(mcvd);

                // 평균(최대/최소값)세팅
                mcvd = new MvmChartViewData();
                mcvd.setFirstCol("Avg(Max/Min)");
                cls = mcvd.getClass();

                StringBuilder sbVal = null;
                StringBuilder sbCo2 = null;

//                Double avg = null;
//                Double max = null;
//                Double min = null;

                for (int j = 0; j < contId.length; j++) {
//                    avg = getNullToDouble(avgValue[j]);
//                    max = getNullToDouble(maxValue[j]);
//                    min = getNullToDouble(minValue[j]);

                    // if (avgValue[j] != null) {

                    // cls.getField("user" + j + "Value").set(mcvd,
                    // getDoubleToStirng(avg) + "(" + getDoubleToStirng(max) + "/" + getDoubleToStirng(min) + ")");
                    // cls.getField("user" + j + "Co2").set(
                    // mcvd,
                    // getChangeCo2(co2StdValue, avg) + "(" + getChangeCo2(co2StdValue, max) + "/"
                    // + getChangeCo2(co2StdValue, min) + ")");

//                    sbVal = new StringBuilder();
//                    sbVal.append(df.format(avg)).append('(');
//                    sbVal.append(df.format(max)).append('/');
//                    sbVal.append(df.format(min)).append(')');
//
//                    sbCo2 = new StringBuilder();
//                    sbCo2.append(df.format(getChangeCo2Double(co2StdValue, avg))).append('(');
//                    sbCo2.append(df.format(getChangeCo2Double(co2StdValue, max))).append('/');
//                    sbCo2.append(df.format(getChangeCo2Double(co2StdValue, min))).append(')');
//
//                    cls.getField("user" + j + "Value").set(mcvd, sbVal.toString());
//                    cls.getField("user" + j + "Co2").set(mcvd, sbCo2.toString());

                    sbVal = new StringBuilder();
                    sbVal.append((bdTotalAvgList.get(j) == null) ? "" : df.format(bdTotalAvgList.get(j).doubleValue())).append('(');
                    sbVal.append((bdTotalMaxList.get(j) == null) ? "" : df.format(bdTotalMaxList.get(j).doubleValue())).append('/');
                    sbVal.append((bdTotalMinList.get(j) == null) ? "" : df.format(bdTotalMinList.get(j).doubleValue())).append(')');

                    sbCo2 = new StringBuilder();
                    sbCo2.append((bdTotalAvgList.get(j) == null) ? "" : df.format(getChangeCo2Double(co2StdValue, bdTotalAvgList.get(j).doubleValue()))).append('(');
                    sbCo2.append((bdTotalMaxList.get(j) == null) ? "" : df.format(getChangeCo2Double(co2StdValue, bdTotalMaxList.get(j).doubleValue()))).append('/');
                    sbCo2.append((bdTotalMinList.get(j) == null) ? "" : df.format(getChangeCo2Double(co2StdValue, bdTotalMinList.get(j).doubleValue()))).append(')');

                    cls.getField("user" + j + "DecimalValue").set(mcvd, sbVal.toString());
                    cls.getField("user" + j + "DecimalCo2").set(mcvd, sbCo2.toString());

                }
                result.add(mcvd);

            } catch (NoSuchFieldException ne) {
                System.out.println("NoSuchFieldException " + ne.getMessage());
            } catch (IllegalAccessException ie) {
                System.out.println("IllegalAccessException" + ie.getMessage());
            }
        }
        return result;
    }

    /**
     * × @see
     * com.aimir.service.mvm.MvmChartViewManager#getSearchDataMonth(java.lang
     * .String[], java.lang.String)
     *
     * @Method Name : getSearchDataMonth
     * @Date : 2010. 4. 8.
     * @Method 설명 : 월별차트
     * @param values
     * @param type
     * @return
     */
    public List<MvmChartViewData> getSearchDataMonth(String[] values, String type) {
        List<MvmChartViewData> result = new ArrayList<MvmChartViewData>();
        HashMap<String, Object> hm = new HashMap<String, Object>();

//        Set<Condition> set = new HashSet<Condition>();
        Set<Condition> set = new LinkedHashSet<Condition>();

        String beginDate = values[0].substring(0, 6);
        String endDate = values[1].substring(0, 6);
        String supplierId = values[6];
        int channel = Integer.parseInt(values[5]);
        String[] contract_number = values[4].split(",");
        Integer[] custList = contractNumberToContractId(contract_number);

        // 채널선택
//        Condition cdt = new Condition("contract.id", null, null, Restriction.ORDERBY);
        Condition cdt = new Condition("id.yyyymm", null, null, Restriction.ORDERBY);
        set.add(cdt);
        Condition cdt1 = new Condition("id.channel", new Object[] { channel }, null, Restriction.EQ);
        set.add(cdt1);
        // 조회년월일시
        if ((beginDate != null && beginDate.length() != 0) && (endDate != null && endDate.length() != 0)) {
            Condition cdt2 = new Condition("id.yyyymm", new Object[] { beginDate, endDate }, null, Restriction.BETWEEN);//
            set.add(cdt2);
        }
//        Condition cdt3 = new Condition("id.yyyymm", null, null, Restriction.ORDERBY);
        Condition cdt3 = new Condition("contract.id", null, null, Restriction.ORDERBY);
        set.add(cdt3);
        Double co2StdValue = 0D;
        if ("EM".equals(type)) {
            hm = MvmEmChartViewManagerImpl.getEMSearchDataMonth(set, custList);
            co2StdValue = getTypeToCo2("EM");
            hm.put("co2StdValue", co2StdValue);
            result = getMonthChangRowByCol(hm, supplierId, beginDate, endDate);
        } else if ("GM".equals(type)) {
            hm = MvmGmChartViewManagerImpl.getGMSearchDataMonth(set, custList);
            co2StdValue = getTypeToCo2("GM");
            hm.put("co2StdValue", co2StdValue);
            result = getMonthChangRowByCol(hm, supplierId);
        } else if ("WM".equals(type)) {
            hm = MvmWmChartViewManagerImpl.getWMSearchDataMonth(set, custList);
            co2StdValue = getTypeToCo2("WM");
            hm.put("co2StdValue", co2StdValue);
            result = getMonthChangRowByCol(hm, supplierId);
        } else if ("HM".equals(type)) {
            hm = MvmHmChartViewManagerImpl.getHMSearchDataMonth(set, custList);
            co2StdValue = getTypeToCo2("HM");
            hm.put("co2StdValue", co2StdValue);
            result = getMonthChangRowByCol(hm, supplierId);
        }
        return result;
    }

    /*
     * 월별 객체 세팅하기
     */
    public List<MvmChartViewData> getMonthChangRowByCol(HashMap<String, Object> hm, String supplierId) {
        return getMonthChangRowByCol(hm, supplierId, null, null);
    }

    /*
     * 월별 객체 세팅하기 - 모든 조회일자 보여주기
     */
    @SuppressWarnings("unchecked")
    public List<MvmChartViewData> getMonthChangRowByCol(HashMap<String, Object> hm, String supplierId, String startDate, String endDate) {
        List<MvmChartViewData> result = new ArrayList<MvmChartViewData>();
        List<MeteringMonth> dataList = (List<MeteringMonth>) hm.get("dataList");

        Integer[] contId = (Integer[]) hm.get("arrContId");
//        Double[] avgValue = (Double[]) hm.get("arrAvgValue");
//        Double[] maxValue = (Double[]) hm.get("arrMaxValue");
//        Double[] minValue = (Double[]) hm.get("arrMinValue");
//        Double[] sumValue = (Double[]) hm.get("arrSumValue");
        Double co2StdValue = (Double) hm.get("co2StdValue");

        MvmChartViewData mcvd = null;
        Class<? extends MvmChartViewData> cls = null;
        String tmpYYmm = "";

        // 일자 비교할 기준값 추출
        List<String> monthList = new ArrayList<String>();
        if (dataList != null && dataList.size() > 0) {
            try {
                if (startDate != null && endDate != null) {
                    String tmpMonth = startDate;
                    int limit = 0;

                    try {
                        int sYear = Integer.parseInt(startDate.substring(0, 4));
                        int sMonth = Integer.parseInt(startDate.substring(4, 6));
                        int eYear = Integer.parseInt(endDate.substring(0, 4));
                        int eMonth = Integer.parseInt(endDate.substring(4, 6));
                        int durYear = eYear - sYear;

                        if (durYear > 0) {
                            limit = ((durYear - 1) * 12) + (12 - sMonth + 1) + eMonth;
                        } else {    // 같은 연도
                            limit = eMonth - sMonth + 1;
                        }

                        // 조회조건 내 모든 일자 가져오기
                        for (int k = 0; k < limit; k++) {
                            monthList.add(tmpMonth);

                            if (tmpMonth.compareTo(endDate) >= 0) {
                                break;
                            }

                            tmpMonth = TimeUtil.getPreMonth(tmpMonth + "01000000", -1).substring(0, 6);
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                } else {
                    for (int i = 0; i < dataList.size(); i++) {
                        String tmpDate = dataList.get(i).getYyyymm();
                        if (i == 0) {
                            tmpYYmm = tmpDate;
                            monthList.add(dataList.get(i).getYyyymm());
                        } else if (!tmpYYmm.equals(tmpDate)) {
                            monthList.add(dataList.get(i).getYyyymm());
                        }
                        tmpYYmm = tmpDate;
                    }
                }

                Double[][] tmpValue = new Double[monthList.size()][contId.length];
                String[] firstCol = new String[monthList.size()];
                for (int tmpIdx = 0; tmpIdx < monthList.size(); tmpIdx++) {
                    firstCol[tmpIdx] = monthList.get(tmpIdx);
                }
                // 데이터 추출
                for (int i = 0; i < dataList.size(); i++) {
                    String compdate = dataList.get(i).getYyyymm();
                    Integer compContID = dataList.get(i).getContract().getId();

                    for (int rowIdx = 0; rowIdx < monthList.size(); rowIdx++) {
                        String stdDate = monthList.get(rowIdx);

                        for (int colIdx = 0; colIdx < contId.length; colIdx++) {
                            Integer stdConId = contId[colIdx];

                            if (compdate.equals(stdDate) && (compContID == stdConId)) {
                                tmpValue[rowIdx][colIdx] = dataList.get(i).getTotal();
                            }
                        }
                    }
                }

                Supplier supplier = supplierDao.get(Integer.parseInt(supplierId));
                DecimalFormat df = DecimalUtil.getDecimalFormat(supplier.getMd());

                String lang = supplier.getLang().getCode_2letter();
                String country = supplier.getCountry().getCode_2letter();

                List<BigDecimal> bdTotalSumList = new ArrayList<BigDecimal>();
                List<BigDecimal> bdTotalAvgList = new ArrayList<BigDecimal>();
                List<BigDecimal> bdTotalMaxList = new ArrayList<BigDecimal>();
                List<BigDecimal> bdTotalMinList = new ArrayList<BigDecimal>();
                List<Integer> intTotalCount = new ArrayList<Integer>();
                BigDecimal bdAvgValue = null;

                // Sum/Max/Min 계산하기
                for (int rowIdx = 0; rowIdx < monthList.size(); rowIdx++) {
                    for (int colIdx = 0; colIdx < contId.length; colIdx++) {
                        if (rowIdx == 0) {
                            if (tmpValue[rowIdx][colIdx] != null) {
                                bdTotalSumList.add(new BigDecimal(tmpValue[rowIdx][colIdx].toString()));
                                bdTotalMaxList.add(new BigDecimal(tmpValue[rowIdx][colIdx].toString()));
                                bdTotalMinList.add(new BigDecimal(tmpValue[rowIdx][colIdx].toString()));
                                intTotalCount.add(1);
                            } else {
                                bdTotalSumList.add(null);
                                bdTotalMaxList.add(null);
                                bdTotalMinList.add(null);
                                intTotalCount.add(0);
                            }
                        } else {
                            if (tmpValue[rowIdx][colIdx] != null) {
                                if (bdTotalSumList.get(colIdx) == null) {
                                    bdTotalSumList.set(colIdx, new BigDecimal(tmpValue[rowIdx][colIdx].toString()));
                                } else {
                                    bdTotalSumList.set(colIdx, bdTotalSumList.get(colIdx).add(new BigDecimal(tmpValue[rowIdx][colIdx].toString())));
                                }
                                if (bdTotalMaxList.get(colIdx) == null) {
                                    bdTotalMaxList.set(colIdx, new BigDecimal(tmpValue[rowIdx][colIdx].toString()));
                                } else {
                                    bdTotalMaxList.set(colIdx, bdTotalMaxList.get(colIdx).max(new BigDecimal(tmpValue[rowIdx][colIdx].toString())));
                                }
                                if (bdTotalMinList.get(colIdx) == null) {
                                    bdTotalMinList.set(colIdx, new BigDecimal(tmpValue[rowIdx][colIdx].toString()));
                                } else {
                                    bdTotalMinList.set(colIdx, bdTotalMinList.get(colIdx).min(new BigDecimal(tmpValue[rowIdx][colIdx].toString())));
                                }
                                intTotalCount.set(colIdx, intTotalCount.get(colIdx) + 1);
                            }
                        }
                    }
                }

                // Avg 계산하기
                for (int colIdx = 0; colIdx < contId.length; colIdx++) {
                    if (bdTotalSumList.get(colIdx) == null) {
                        bdAvgValue = null;
                    } else if (intTotalCount.get(colIdx) > 0) {
                        bdAvgValue = bdTotalSumList.get(colIdx).divide(new BigDecimal(intTotalCount.get(colIdx)), MathContext.DECIMAL32);
                    } else {
                        bdAvgValue = new BigDecimal("0");
                    }
                    bdTotalAvgList.add(bdAvgValue);
                }

                // 데이터 담기
                for (int rowIdx = 0; rowIdx < monthList.size(); rowIdx++) {
                    mcvd = new MvmChartViewData();
                    cls = mcvd.getClass();

//                    cls.getField("firstCol").set(mcvd, firstCol[rowIdx]);
                    cls.getField("firstCol").set(mcvd, TimeLocaleUtil.getLocaleYearMonth(firstCol[rowIdx], lang, country));

                    for (int colIdx = 0; colIdx < contId.length; colIdx++) {
//                        cls.getField("user" + colIdx + "Value").set(mcvd, df.format(getNullToDouble(tmpValue[rowIdx][colIdx])));
//                        cls.getField("user" + colIdx + "Co2").set(mcvd, df.format(getChangeCo2Double(co2StdValue, tmpValue[rowIdx][colIdx])));
//                        cls.getField("user" + colIdx + "Avg").set(mcvd, getNullToDouble(avgValue[colIdx]));
//                        cls.getField("user" + colIdx + "Max").set(mcvd, getNullToDouble(maxValue[colIdx]));
                        cls.getField("user" + colIdx + "Value").set(mcvd, (tmpValue[rowIdx][colIdx] == null) ? 0D : getNullToDouble(tmpValue[rowIdx][colIdx]));
                        cls.getField("user" + colIdx + "Co2").set(mcvd, (tmpValue[rowIdx][colIdx] == null) ? 0D : getChangeCo2Double(co2StdValue, tmpValue[rowIdx][colIdx]));
                        cls.getField("user" + colIdx + "Avg").set(mcvd, (bdTotalAvgList.get(colIdx) == null) ? 0D : bdTotalAvgList.get(colIdx).doubleValue());
                        cls.getField("user" + colIdx + "Max").set(mcvd, (bdTotalMaxList.get(colIdx) == null) ? 0D : bdTotalMaxList.get(colIdx).doubleValue());
                        cls.getField("user" + colIdx + "DecimalValue").set(mcvd, (tmpValue[rowIdx][colIdx] == null) ? "- " : df.format(getNullToDouble(tmpValue[rowIdx][colIdx])));
                        cls.getField("user" + colIdx + "DecimalCo2").set(mcvd, (tmpValue[rowIdx][colIdx] == null) ? "- " : df.format(getChangeCo2Double(co2StdValue, tmpValue[rowIdx][colIdx])));
                    }
                    result.add(mcvd);
                }

                // 합계 세팅
                mcvd = new MvmChartViewData();
                mcvd.setFirstCol("Sum");
                cls = mcvd.getClass();
//                Double sum = null;

                for (int j = 0; j < contId.length; j++) {
//                    sum = getNullToDouble(sumValue[j]);
//
//                    cls.getField("user" + j + "Value").set(mcvd, df.format(sum));
//                    cls.getField("user" + j + "Co2").set(mcvd, df.format(getChangeCo2Double(co2StdValue, sum)));
                    cls.getField("user" + j + "DecimalValue").set(mcvd, (bdTotalSumList.get(j) == null) ? "" : df.format(bdTotalSumList.get(j).doubleValue()));
                    cls.getField("user" + j + "DecimalCo2").set(mcvd, (bdTotalSumList.get(j) == null) ? "" : df.format(getChangeCo2Double(co2StdValue, bdTotalSumList.get(j).doubleValue())));
                }
                result.add(mcvd);

                // 평균(최대/최소값)세팅
                mcvd = new MvmChartViewData();
                mcvd.setFirstCol("Avg(Max/Min)");
                cls = mcvd.getClass();

                StringBuilder sbVal = null;
                StringBuilder sbCo2 = null;

//                Double avg = null;
//                Double max = null;
//                Double min = null;

                for (int j = 0; j < contId.length; j++) {
//                    avg = getNullToDouble(avgValue[j]);
//                    max = getNullToDouble(maxValue[j]);
//                    min = getNullToDouble(minValue[j]);
//
//                    sbVal = new StringBuilder();
//                    sbVal.append(df.format(avg)).append('(');
//                    sbVal.append(df.format(max)).append('/');
//                    sbVal.append(df.format(min)).append(')');
//
//                    sbCo2 = new StringBuilder();
//                    sbCo2.append(df.format(getChangeCo2Double(co2StdValue, avg))).append('(');
//                    sbCo2.append(df.format(getChangeCo2Double(co2StdValue, max))).append('/');
//                    sbCo2.append(df.format(getChangeCo2Double(co2StdValue, min))).append(')');
//
//                    cls.getField("user" + j + "Value").set(mcvd, sbVal.toString());
//                    cls.getField("user" + j + "Co2").set(mcvd, sbCo2.toString());
                    sbVal = new StringBuilder();
                    sbVal.append((bdTotalAvgList.get(j) == null) ? "" : df.format(bdTotalAvgList.get(j).doubleValue())).append('(');
                    sbVal.append((bdTotalMaxList.get(j) == null) ? "" : df.format(bdTotalMaxList.get(j).doubleValue())).append('/');
                    sbVal.append((bdTotalMinList.get(j) == null) ? "" : df.format(bdTotalMinList.get(j).doubleValue())).append(')');

                    sbCo2 = new StringBuilder();
                    sbCo2.append((bdTotalAvgList.get(j) == null) ? "" : df.format(getChangeCo2Double(co2StdValue, bdTotalAvgList.get(j).doubleValue()))).append('(');
                    sbCo2.append((bdTotalMaxList.get(j) == null) ? "" : df.format(getChangeCo2Double(co2StdValue, bdTotalMaxList.get(j).doubleValue()))).append('/');
                    sbCo2.append((bdTotalMinList.get(j) == null) ? "" : df.format(getChangeCo2Double(co2StdValue, bdTotalMinList.get(j).doubleValue()))).append(')');

                    cls.getField("user" + j + "DecimalValue").set(mcvd, sbVal.toString());
                    cls.getField("user" + j + "DecimalCo2").set(mcvd, sbCo2.toString());
                }
                result.add(mcvd);

            } catch (NoSuchFieldException ne) {
                System.out.println("NoSuchFieldException " + ne.getMessage());
            } catch (IllegalAccessException ie) {
                System.out.println("IllegalAccessException" + ie.getMessage());
            }
        }
        return result;
    }

    /**
     * × @see
     * com.aimir.service.mvm.MvmChartViewManager#getSearchDataWeek(java.lang
     * .String[], java.lang.String)
     *
     * @Method Name : getSearchDataWeek
     * @Date : 2010. 4. 8.
     * @Method 설명 : 주별데이터
     * @param values
     * @param type
     * @return
     */
    public List<MvmChartViewData> getSearchDataWeek(String[] values, String type) {
        List<MvmChartViewData> result = new ArrayList<MvmChartViewData>();
        HashMap<String, Object> hm = new HashMap<String, Object>();
        String beginDate = values[0];
        String yyMM = beginDate.substring(0, 6);
        int channel = (StringUtil.nullToBlank(values[5]).length() == 0) ? -1 : Integer.parseInt(values[5]);
        String supplierId = values[6];

        String[] contract_number = values[4].split(",");
        Integer[] custList = contractNumberToContractId(contract_number);

        // 조회조건 세팅
        Set<Condition> set = new HashSet<Condition>();
        Condition cdt = new Condition("contract.id", null, null, Restriction.ORDERBY);
        set.add(cdt);
        Condition cdt1 = new Condition("id.channel", new Object[] { channel }, null, Restriction.EQ);
        set.add(cdt1);
        Condition cdt2 = new Condition("id.yyyymmdd", null, null, Restriction.ORDERBY);
        set.add(cdt2);
        Double co2StdValue = 0D;
        if ("EM".equals(type)) {
            hm = MvmEmChartViewManagerImpl.getEMSearchDataWeek(set, custList, yyMM);
            co2StdValue = getTypeToCo2("EM");
            hm.put("co2StdValue", co2StdValue);
            result = getSeasonAndWeekChangRowByCol(hm, supplierId);
        } else if ("GM".equals(type)) {
            hm = MvmGmChartViewManagerImpl.getGMSearchDataWeek(set, custList, yyMM);
            co2StdValue = getTypeToCo2("GM");
            hm.put("co2StdValue", co2StdValue);
            result = getSeasonAndWeekChangRowByCol(hm, supplierId);
        } else if ("WM".equals(type)) {
            hm = MvmWmChartViewManagerImpl.getWMSearchDataWeek(set, custList, yyMM);
            co2StdValue = getTypeToCo2("WM");
            hm.put("co2StdValue", co2StdValue);
            result = getSeasonAndWeekChangRowByCol(hm, supplierId);
        } else if ("HM".equals(type)) {
            hm = MvmHmChartViewManagerImpl.getHMSearchDataWeek(set, custList, yyMM);
            co2StdValue = getTypeToCo2("HM");
            hm.put("co2StdValue", co2StdValue);
            result = getSeasonAndWeekChangRowByCol(hm, supplierId);
        }
        return result;
    }

    /**
     * × @see
     * com.aimir.service.mvm.MvmChartViewManager#getSearchDataDayWeek(java
     * .lang.String[], java.lang.String)
     *
     * @Method Name : getSearchDataDayWeek
     * @Date : 2010. 4. 8.
     * @Method 설명 : 요일별데이터
     * @param values
     * @param type
     * @return
     */
    public List<MvmChartViewData> getSearchDataDayWeek(String[] values, String type) {
        List<MvmChartViewData> result = new ArrayList<MvmChartViewData>();
        HashMap<String, Object> hm = new HashMap<String, Object>();

        String beginDate = values[0];
        String endDate = values[1];
        int channel = Integer.parseInt(values[5]);
        String supplierId = values[6];

        String[] contract_number = values[4].split(",");
        Integer[] custList = contractNumberToContractId(contract_number);

        // 조회시 필요한 기준채널, 일자, 미터아이디 생성
        Set<Condition> set = new HashSet<Condition>();
        Condition cdt = new Condition("contract.id", null, null, Restriction.ORDERBY);
        set.add(cdt);
        Condition cdt1 = new Condition("id.channel", new Object[] { channel }, null, Restriction.EQ);
        set.add(cdt1);
        if ((beginDate != null && beginDate.length() != 0) && (endDate != null && endDate.length() != 0)) {
            Condition cdt2 = new Condition("id.yyyymmdd", new Object[] { beginDate, endDate }, null, Restriction.BETWEEN);
            set.add(cdt2);
        }
        Condition cdt3 = new Condition("id.yyyymmdd", null, null, Restriction.ORDERBY);
        set.add(cdt3);
        Double co2StdValue = 0D;
        if ("EM".equals(type)) {
            hm = MvmEmChartViewManagerImpl.getEMSearchDataDayWeek(set, custList);
            co2StdValue = getTypeToCo2("EM");
            hm.put("co2StdValue", co2StdValue);
            result = getDayWeekChangRowByCol(hm, supplierId);
        } else if ("GM".equals(type)) {
            hm = MvmGmChartViewManagerImpl.getGMSearchDataDayWeek(set, custList);
            co2StdValue = getTypeToCo2("GM");
            hm.put("co2StdValue", co2StdValue);
            result = getDayWeekChangRowByCol(hm, supplierId);
        } else if ("WM".equals(type)) {
            hm = MvmWmChartViewManagerImpl.getWMSearchDataDayWeek(set, custList);
            co2StdValue = getTypeToCo2("WM");
            hm.put("co2StdValue", co2StdValue);
            result = getDayWeekChangRowByCol(hm, supplierId);
        } else if ("HM".equals(type)) {
            hm = MvmHmChartViewManagerImpl.getHMSearchDataDayWeek(set, custList);
            co2StdValue = getTypeToCo2("HM");
            hm.put("co2StdValue", co2StdValue);
            result = getDayWeekChangRowByCol(hm, supplierId);
        }
        return result;
    }

    /*
     * 요일별 객체 세팅하기
     */
    @SuppressWarnings("unchecked")
    public List<MvmChartViewData> getDayWeekChangRowByCol(HashMap<String, Object> hm, String supplierId) {
        List<MvmChartViewData> result = new ArrayList<MvmChartViewData>();
        SearchCalendarUtil scu = new SearchCalendarUtil();
        String[] arrFirstName = { "Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat" };
        List<MeteringDay> dataList = (List<MeteringDay>) hm.get("dataList");
        Integer[] contId = (Integer[]) hm.get("arrContId");
//        Double[] avgValue = (Double[]) hm.get("arrAvgValue");
//        Double[] maxValue = (Double[]) hm.get("arrMaxValue");
//        Double[] minValue = (Double[]) hm.get("arrMinValue");
//        Double[] sumValue = (Double[]) hm.get("arrSumValue");
        Double co2StdValue = (Double) hm.get("co2StdValue");

        MvmChartViewData mcvd = null;
        Class<? extends MvmChartViewData> cls = null;
        // String tmpYYmmDD = ""; // 일자 비교할 기준값

        Class<? extends MeteringDay> clas = null;
        MeteringDay mtr = null;
        Class<?>[] parameterTypes = {};
        Object[] parameters = {};
        Method method = null;
        BigDecimal bdValue = null;      // 임시계산용
        Double value = null;            // 임시

        // 일자 비교할 기준값 추출
        // List<String> dayList = new ArrayList<String>();
        if (dataList != null && dataList.size() > 0) {

            try {
                // 일자 비교할 기준값 추출
                HashSet<String> dayHashSet = new HashSet<String>();

                // 행을 그리기 위한 기준으로 일자를 구함
                for (int i = 0; i < dataList.size(); i++) {
                    dayHashSet.add(dataList.get(i).getYyyymmdd());
                }

                Set<String> dayList = new TreeSet<String>(dayHashSet);

                String[] arrStdDate = new String[dayList.size()];
                Iterator<String> itr = dayList.iterator();
                int tmpIdx = 0;
                while (itr.hasNext()) {
                    arrStdDate[tmpIdx] = itr.next();
                    tmpIdx++;
                }
                Double[][] tmpValue = new Double[arrStdDate.length][contId.length];
                String[] firstCol = new String[arrStdDate.length];

                // 데이터 추출
                for (int i = 0; i < dataList.size(); i++) {
                    String compdate = dataList.get(i).getYyyymmdd();
                    Integer compContID = dataList.get(i).getContract().getId();

                    mtr = dataList.get(i);
                    clas = mtr.getClass();
                    bdValue = null;
                    value = null;

                    for (int rowIdx = 0; rowIdx < dayList.size(); rowIdx++) {
                        String stdDate = arrStdDate[rowIdx];
                        firstCol[rowIdx] = arrFirstName[scu.getDateTodayWeekNum(stdDate) - 1];

                        for (int colIdx = 0; colIdx < contId.length; colIdx++) {
                            Integer stdConId = contId[colIdx];

                            if (compdate.equals(stdDate) && (compContID == stdConId)) {
//                                tmpValue[rowIdx][colIdx] = dataList.get(i).getValue_00();
                                
                                bdValue = null;

                                for (int j = 0 ; j < 24 ; j++) { // 시간별 데이터 합산
                                    value = null;
                                    try {
                                        method = clas.getMethod("getValue_" + StringUtil.frontAppendNStr('0', Integer.toString(j), 2), parameterTypes);
                                        value = (Double)method.invoke(mtr, parameters);
                                    } catch (SecurityException e) {
                                        e.printStackTrace();
                                    } catch (IllegalArgumentException e) {
                                        e.printStackTrace();
                                    } catch (NoSuchMethodException e) {
                                        e.printStackTrace();
                                    } catch (InvocationTargetException e) {
                                        e.printStackTrace();
                                    }

                                    if (value != null) {
                                        if (bdValue == null) {
                                            bdValue = new BigDecimal(value.toString());
                                        } else {
                                            bdValue = bdValue.add(new BigDecimal(value.toString()));
                                        }
                                    }
                                }
                                tmpValue[rowIdx][colIdx] = (bdValue == null) ? null : bdValue.doubleValue();
                            }
                        }
                    }
                }

                Supplier supplier = supplierDao.get(Integer.parseInt(supplierId));
                DecimalFormat df = DecimalUtil.getDecimalFormat(supplier.getMd());

                List<BigDecimal> bdTotalSumList = new ArrayList<BigDecimal>();
                List<BigDecimal> bdTotalAvgList = new ArrayList<BigDecimal>();
                List<BigDecimal> bdTotalMaxList = new ArrayList<BigDecimal>();
                List<BigDecimal> bdTotalMinList = new ArrayList<BigDecimal>();
                List<Integer> intTotalCount = new ArrayList<Integer>();
                BigDecimal bdAvgValue = null;

                // Sum/Max/Min 계산하기
                for (int rowIdx = 0; rowIdx < dayList.size(); rowIdx++) {

                    for (int colIdx = 0; colIdx < contId.length; colIdx++) {

                        if (rowIdx == 0) {
                            if (tmpValue[rowIdx][colIdx] != null) {
                                bdTotalSumList.add(new BigDecimal(tmpValue[rowIdx][colIdx].toString()));
                                bdTotalMaxList.add(new BigDecimal(tmpValue[rowIdx][colIdx].toString()));
                                bdTotalMinList.add(new BigDecimal(tmpValue[rowIdx][colIdx].toString()));
                                intTotalCount.add(1);
                            } else {
                                bdTotalSumList.add(null);
                                bdTotalMaxList.add(null);
                                bdTotalMinList.add(null);
                                intTotalCount.add(0);
                            }
                        } else {
                            if (tmpValue[rowIdx][colIdx] != null) {
                                if (bdTotalSumList.get(colIdx) == null) {
                                    bdTotalSumList.set(colIdx, new BigDecimal(tmpValue[rowIdx][colIdx].toString()));
                                } else {
                                    bdTotalSumList.set(colIdx, bdTotalSumList.get(colIdx).add(new BigDecimal(tmpValue[rowIdx][colIdx].toString())));
                                }
                                if (bdTotalMaxList.get(colIdx) == null) {
                                    bdTotalMaxList.set(colIdx, new BigDecimal(tmpValue[rowIdx][colIdx].toString()));
                                } else {
                                    bdTotalMaxList.set(colIdx, bdTotalMaxList.get(colIdx).max(new BigDecimal(tmpValue[rowIdx][colIdx].toString())));
                                }
                                if (bdTotalMinList.get(colIdx) == null) {
                                    bdTotalMinList.set(colIdx, new BigDecimal(tmpValue[rowIdx][colIdx].toString()));
                                } else {
                                    bdTotalMinList.set(colIdx, bdTotalMinList.get(colIdx).min(new BigDecimal(tmpValue[rowIdx][colIdx].toString())));
                                }
                                intTotalCount.set(colIdx, intTotalCount.get(colIdx) + 1);
                            }
                        }
                    }
                }

                // Avg 계산하기
                for (int colIdx = 0; colIdx < contId.length; colIdx++) {
                    if (bdTotalSumList.get(colIdx) == null) {
                        bdAvgValue = null;
                    } else if (intTotalCount.get(colIdx) > 0) {
                        bdAvgValue = bdTotalSumList.get(colIdx).divide(new BigDecimal(intTotalCount.get(colIdx)), MathContext.DECIMAL32);
                    } else {
                        bdAvgValue = new BigDecimal("0");
                    }
                    bdTotalAvgList.add(bdAvgValue);
                }

                
                // 데이터 담기
                for (int rowIdx = 0; rowIdx < dayList.size(); rowIdx++) {
                    mcvd = new MvmChartViewData();
                    cls = mcvd.getClass();

                    cls.getField("firstCol").set(mcvd, firstCol[rowIdx]);
                    for (int colIdx = 0; colIdx < contId.length; colIdx++) {
//                        cls.getField("user" + colIdx + "Value").set(mcvd, df.format(getNullToDouble(tmpValue[rowIdx][colIdx])));
//                        cls.getField("user" + colIdx + "Co2").set(mcvd, df.format(getChangeCo2Double(co2StdValue, tmpValue[rowIdx][colIdx])));
//                        cls.getField("user" + colIdx + "Avg").set(mcvd, getNullToDouble(avgValue[colIdx]));
//                        cls.getField("user" + colIdx + "Max").set(mcvd, getNullToDouble(maxValue[colIdx]));

                        cls.getField("user" + colIdx + "Value").set(mcvd, (tmpValue[rowIdx][colIdx] == null) ? 0D : getNullToDouble(tmpValue[rowIdx][colIdx]));
                        cls.getField("user" + colIdx + "Co2").set(mcvd, (tmpValue[rowIdx][colIdx] == null) ? 0D : getChangeCo2Double(co2StdValue, tmpValue[rowIdx][colIdx]));
                        cls.getField("user" + colIdx + "Avg").set(mcvd, (bdTotalAvgList.get(colIdx) == null) ? 0D : bdTotalAvgList.get(colIdx).doubleValue());
                        cls.getField("user" + colIdx + "Max").set(mcvd, (bdTotalMaxList.get(colIdx) == null) ? 0D : bdTotalMaxList.get(colIdx).doubleValue());
                        cls.getField("user" + colIdx + "DecimalValue").set(mcvd, (tmpValue[rowIdx][colIdx] == null) ? "" : df.format(getNullToDouble(tmpValue[rowIdx][colIdx])));
                        cls.getField("user" + colIdx + "DecimalCo2").set(mcvd, (tmpValue[rowIdx][colIdx] == null) ? "" : df.format(getChangeCo2Double(co2StdValue, tmpValue[rowIdx][colIdx])));
                    }
                    result.add(mcvd);
                }

                // 합계 세팅
                mcvd = new MvmChartViewData();
//                mcvd.setFirstCol("(Total)");
                mcvd.setFirstCol("Sum");
                cls = mcvd.getClass();
//                Double sum = null;

//                for (int j = 0; j < contId.length; j++) {
//                    sum = getNullToDouble(sumValue[j]);
//
//                    cls.getField("user" + j + "Value").set(mcvd, df.format(sum));
//                    cls.getField("user" + j + "Co2").set(mcvd, df.format(getChangeCo2Double(co2StdValue, sum)));
//                }

                for (int j = 0; j < contId.length; j++) {
                    cls.getField("user" + j + "DecimalValue").set(mcvd, (bdTotalSumList.get(j) == null) ? "" : df.format(bdTotalSumList.get(j).doubleValue()));
                    cls.getField("user" + j + "DecimalCo2").set(mcvd, (bdTotalSumList.get(j) == null) ? "" : df.format(getChangeCo2Double(co2StdValue, bdTotalSumList.get(j).doubleValue())));
                }

                result.add(mcvd);

                // 평균(최대/최소값)세팅
                mcvd = new MvmChartViewData();
                mcvd.setFirstCol("Avg(Max/Min)");
                cls = mcvd.getClass();

                StringBuilder sbVal = null;
                StringBuilder sbCo2 = null;

//                Double avg = null;
//                Double max = null;
//                Double min = null;

                for (int j = 0; j < contId.length; j++) {
//                    avg = getNullToDouble(avgValue[j]);
//                    max = getNullToDouble(maxValue[j]);
//                    min = getNullToDouble(minValue[j]);

//                    sbVal = new StringBuilder();
//                    sbVal.append(df.format(avg)).append('(');
//                    sbVal.append(df.format(max)).append('/');
//                    sbVal.append(df.format(min)).append(')');
//
//                    sbCo2 = new StringBuilder();
//                    sbCo2.append(df.format(getChangeCo2Double(co2StdValue, avg))).append('(');
//                    sbCo2.append(df.format(getChangeCo2Double(co2StdValue, max))).append('/');
//                    sbCo2.append(df.format(getChangeCo2Double(co2StdValue, min))).append(')');
//
//                    cls.getField("user" + j + "Value").set(mcvd, sbVal.toString());
//                    cls.getField("user" + j + "Co2").set(mcvd, sbCo2.toString());

                    sbVal = new StringBuilder();
                    sbVal.append((bdTotalAvgList.get(j) == null) ? "" : df.format(bdTotalAvgList.get(j).doubleValue())).append('(');
                    sbVal.append((bdTotalMaxList.get(j) == null) ? "" : df.format(bdTotalMaxList.get(j).doubleValue())).append('/');
                    sbVal.append((bdTotalMinList.get(j) == null) ? "" : df.format(bdTotalMinList.get(j).doubleValue())).append(')');

                    sbCo2 = new StringBuilder();
                    sbCo2.append((bdTotalAvgList.get(j) == null) ? "" : df.format(getChangeCo2Double(co2StdValue, bdTotalAvgList.get(j).doubleValue()))).append('(');
                    sbCo2.append((bdTotalMaxList.get(j) == null) ? "" : df.format(getChangeCo2Double(co2StdValue, bdTotalMaxList.get(j).doubleValue()))).append('/');
                    sbCo2.append((bdTotalMinList.get(j) == null) ? "" : df.format(getChangeCo2Double(co2StdValue, bdTotalMinList.get(j).doubleValue()))).append(')');

                    cls.getField("user" + j + "DecimalValue").set(mcvd, sbVal.toString());
                    cls.getField("user" + j + "DecimalCo2").set(mcvd, sbCo2.toString());
                }
                result.add(mcvd);

            } catch (NoSuchFieldException ne) {
                System.out.println("NoSuchFieldException " + ne.getMessage());
            } catch (IllegalAccessException ie) {
                System.out.println("IllegalAccessException" + ie.getMessage());
            }
        }
        return result;
    }

    /**
     * × @see
     * com.aimir.service.mvm.MvmChartViewManager#getSearchDataSeason(java.
     * lang.String[], java.lang.String)
     *
     * @Method Name : getSearchDataSeason
     * @Date : 2010. 4. 15.
     * @Method 설명 : 계절별 데이터 추출
     * @param values
     * @param type
     * @return
     */
    public List<MvmChartViewData> getSearchDataSeason(String[] values, String type) {
        List<MvmChartViewData> result = new ArrayList<MvmChartViewData>();
        HashMap<String, Object> hm = null;
        String beginDate = values[0];
        String year = beginDate.substring(0, 4);
        int channel = Integer.parseInt(values[5]);
        String supplierId = values[6];

        String[] contract_number = values[4].split(",");
        Integer[] custList = contractNumberToContractId(contract_number);

        // 조회기준 채널, 일자, 미터아이디 설정
        Set<Condition> set = new HashSet<Condition>();
        Condition cdt = new Condition("contract.id", null, null, Restriction.ORDERBY);
        set.add(cdt);
        Condition cdt1 = new Condition("id.channel", new Object[] { channel }, null, Restriction.EQ);
        set.add(cdt1);
        Condition cdt2 = new Condition("id.yyyymmdd", null, null, Restriction.ORDERBY);
        set.add(cdt2);
        Double co2StdValue = 0D;
        if ("EM".equals(type)) {
            hm = MvmEmChartViewManagerImpl.getEMSearchDataSeason(set, custList, year);
            co2StdValue = getTypeToCo2("EM");
            hm.put("co2StdValue", co2StdValue);
            result = getSeasonAndWeekChangRowByCol(hm, supplierId);
        } else if ("GM".equals(type)) {
            hm = MvmGmChartViewManagerImpl.getGMSearchDataSeason(set, custList, year);
            co2StdValue = getTypeToCo2("GM");
            hm.put("co2StdValue", co2StdValue);
            result = getSeasonAndWeekChangRowByCol(hm, supplierId);
        } else if ("WM".equals(type)) {
            hm = MvmWmChartViewManagerImpl.getWMSearchDataSeason(set, custList, year);
            co2StdValue = getTypeToCo2("WM");
            hm.put("co2StdValue", co2StdValue);
            result = getSeasonAndWeekChangRowByCol(hm, supplierId);
        } else if ("HM".equals(type)) {
            hm = MvmHmChartViewManagerImpl.getHMSearchDataSeason(set, custList, year);
            co2StdValue = getTypeToCo2("HM");
            hm.put("co2StdValue", co2StdValue);
            result = getSeasonAndWeekChangRowByCol(hm, supplierId);
        }

        return result;
    }

    /*
     * 계절/주별 객체 세팅하기
     */
    @SuppressWarnings("unchecked")
    public List<MvmChartViewData> getSeasonAndWeekChangRowByCol(HashMap<String, Object> hm, String supplierId) {

        List<MvmChartViewData> result = new ArrayList<MvmChartViewData>();
        MvmChartViewData mcvd = null;
        Class<? extends MvmChartViewData> cls = null;

        Integer rowNum = (Integer) hm.get("rowNum");// 화면에 출력되는 row수
        List<String> firstColNm = (List<String>) hm.get("firstColNm");
        Double co2StdValue = (Double) hm.get("co2StdValue");

        Integer[] contId = (Integer[]) hm.get("contractId");
        int colNum = contId.length;// 화면에 출력되는 col수

        // 데이터 저장
        Double[][] tmpValue = new Double[rowNum.intValue()][colNum];
//        Double[] maxValue = new Double[colNum];
//        Double[] minValue = new Double[colNum];
//        Double[] sumValue = new Double[colNum];

        List<BigDecimal> bdTotalSumList = new ArrayList<BigDecimal>();
        List<BigDecimal> bdTotalAvgList = new ArrayList<BigDecimal>();
        List<BigDecimal> bdTotalMaxList = new ArrayList<BigDecimal>();
        List<BigDecimal> bdTotalMinList = new ArrayList<BigDecimal>();
        List<Integer> intTotalCount = new ArrayList<Integer>();
        BigDecimal bdAvgValue = null;

        // 사용량 구하기
        for (int i = 0; i < rowNum; i++) {
            // 데이터를 순서대로 idx 형식을 key 형태로 받아옴
            List<Object> dataList = (List<Object>) hm.get("dataList" + i);
            if (dataList != null && dataList.size() > 0) {
                for (int j = 0; j < dataList.size(); j++) {
                    HashMap<Integer, Double> dataHm = (HashMap<Integer, Double>) dataList.get(j);
                    for (int inIdx = 0; inIdx < colNum; inIdx++) {
                        if (dataHm.get(contId[inIdx]) != null) { // 조회된 리스트가 없을때
                            tmpValue[i][inIdx] = dataHm.get(contId[inIdx]);
                        } else {
                            tmpValue[i][inIdx] = 0D;
                        }
                    }
                }
            } else {
                for (int inIdx = 0; inIdx < colNum; inIdx++) {
                    tmpValue[i][inIdx] = 0D;
                }
            }
        }
        // 합계, 평균, 최대값, 최소값 구하기
        for (int j = 0; j < colNum; j++) {
            for (int i = 0; i < rowNum; i++) {
//                if (i == 0) {
//                    maxValue[j] = tmpValue[i][j];
//                    minValue[j] = tmpValue[i][j];
//                    sumValue[j] = 0D;
//                } else {
//                    if (maxValue[j].doubleValue() < tmpValue[i][j].doubleValue())
//                        maxValue[j] = tmpValue[i][j];
//                    if (minValue[j].doubleValue() > tmpValue[i][j].doubleValue())
//                        minValue[j] = tmpValue[i][j];
//                }
//                sumValue[j] = sumValue[j] + tmpValue[i][j];
                
                
                if (i == 0) {
                    if (tmpValue[i][j] != null) {
                        bdTotalSumList.add(new BigDecimal(tmpValue[i][j].toString()));
                        bdTotalMaxList.add(new BigDecimal(tmpValue[i][j].toString()));
                        bdTotalMinList.add(new BigDecimal(tmpValue[i][j].toString()));
                        intTotalCount.add(1);
                    } else {
                        bdTotalSumList.add(null);
                        bdTotalMaxList.add(null);
                        bdTotalMinList.add(null);
                        intTotalCount.add(0);
                    }
                } else {
                    if (tmpValue[i][j] != null) {
                        if (bdTotalSumList.get(j) == null) {
                            bdTotalSumList.set(j, new BigDecimal(tmpValue[i][j].toString()));
                        } else {
                            bdTotalSumList.set(j, bdTotalSumList.get(j).add(new BigDecimal(tmpValue[i][j].toString())));
                        }
                        if (bdTotalMaxList.get(j) == null) {
                            bdTotalMaxList.set(j, new BigDecimal(tmpValue[i][j].toString()));
                        } else {
                            bdTotalMaxList.set(j, bdTotalMaxList.get(j).max(new BigDecimal(tmpValue[i][j].toString())));
                        }
                        if (bdTotalMinList.get(j) == null) {
                            bdTotalMinList.set(j, new BigDecimal(tmpValue[i][j].toString()));
                        } else {
                            bdTotalMinList.set(j, bdTotalMinList.get(j).min(new BigDecimal(tmpValue[i][j].toString())));
                        }
                        intTotalCount.set(j, intTotalCount.get(j) + 1);
                    }
                }
            }
        }
        
        // Avg 계산하기
        for (int j = 0; j < colNum; j++) {
            if (bdTotalSumList.get(j) == null) {
                bdAvgValue = null;
            } else if (intTotalCount.get(j) > 0) {
                bdAvgValue = bdTotalSumList.get(j).divide(new BigDecimal(intTotalCount.get(j)), MathContext.DECIMAL32);
            } else {
                bdAvgValue = new BigDecimal("0");
            }
            bdTotalAvgList.add(bdAvgValue);
        }

        if (rowNum > 0) {
            try {
                Supplier supplier = supplierDao.get(Integer.parseInt(supplierId));
                DecimalFormat df = DecimalUtil.getDecimalFormat(supplier.getMd());

                // bean객체 세팅
                for (int i = 0; i < rowNum; i++) {
                    mcvd = new MvmChartViewData();
                    cls = mcvd.getClass();
                    cls.getField("firstCol").set(mcvd, firstColNm.get(i));

                    for (int j = 0; j < colNum; j++) {
//                        cls.getField("user" + j + "Value").set(mcvd, df.format(getNullToDouble(tmpValue[i][j])));
//                        cls.getField("user" + j + "Co2").set(mcvd, df.format(getChangeCo2Double(co2StdValue, tmpValue[i][j])));
//                        cls.getField("user" + j + "Avg").set(mcvd, getNullToDouble(sumValue[j] / colNum));
//                        cls.getField("user" + j + "Max").set(mcvd, getNullToDouble(maxValue[j]));

                        cls.getField("user" + j + "Value").set(mcvd, (tmpValue[i][j] == null) ? 0D : getNullToDouble(tmpValue[i][j]));
                        cls.getField("user" + j + "Co2").set(mcvd, (tmpValue[i][j] == null) ? 0D : getChangeCo2Double(co2StdValue, tmpValue[i][j]));
                        cls.getField("user" + j + "Avg").set(mcvd, (bdTotalAvgList.get(j) == null) ? 0D : bdTotalAvgList.get(j).doubleValue());
                        cls.getField("user" + j + "Max").set(mcvd, (bdTotalMaxList.get(j) == null) ? 0D : bdTotalMaxList.get(j).doubleValue());
                        cls.getField("user" + j + "DecimalValue").set(mcvd, (tmpValue[i][j] == null) ? "" : df.format(getNullToDouble(tmpValue[i][j])));
                        cls.getField("user" + j + "DecimalCo2").set(mcvd, (tmpValue[i][j] == null) ? "" : df.format(getChangeCo2Double(co2StdValue, tmpValue[i][j])));
                    }

                    result.add(mcvd);
                }

                // 합계 세팅
                mcvd = new MvmChartViewData();
                cls = mcvd.getClass();
//                Double sum = null;
                mcvd.setFirstCol("Sum");

                for (int j = 0; j < colNum; j++) {
//                    sum = getNullToDouble(sumValue[j]);
//                    cls.getField("user" + j + "Value").set(mcvd, df.format(sum));
//                    cls.getField("user" + j + "Co2").set(mcvd, df.format(getChangeCo2Double(co2StdValue, sum)));
                    cls.getField("user" + j + "DecimalValue").set(mcvd, (bdTotalSumList.get(j) == null) ? "" : df.format(bdTotalSumList.get(j).doubleValue()));
                    cls.getField("user" + j + "DecimalCo2").set(mcvd, (bdTotalSumList.get(j) == null) ? "" : df.format(getChangeCo2Double(co2StdValue, bdTotalSumList.get(j).doubleValue())));
                }
                result.add(mcvd);

                // 평균(최대/최소값)세팅
                mcvd = new MvmChartViewData();
                cls = mcvd.getClass();
                mcvd.setFirstCol("Avg(Max/Min)");
                
                StringBuilder sbVal = null;
                StringBuilder sbCo2 = null;

//                Double avg = null;
//                Double max = null;
//                Double min = null;

                for (int j = 0; j < colNum; j++) {
//                    avg = getNullToDouble(sumValue[j] / colNum);
//                    max = getNullToDouble(maxValue[j]);
//                    min = getNullToDouble(minValue[j]);
//
//                    sbVal = new StringBuilder();
//                    sbVal.append(df.format(avg)).append('(');
//                    sbVal.append(df.format(max)).append('/');
//                    sbVal.append(df.format(min)).append(')');
//
//                    sbCo2 = new StringBuilder();
//                    sbCo2.append(df.format(getChangeCo2Double(co2StdValue, avg))).append('(');
//                    sbCo2.append(df.format(getChangeCo2Double(co2StdValue, max))).append('/');
//                    sbCo2.append(df.format(getChangeCo2Double(co2StdValue, min))).append(')');
//                    
//                    cls.getField("user" + j + "Value").set(mcvd, sbVal.toString());
//                    cls.getField("user" + j + "Co2").set(mcvd, sbCo2.toString());
                    sbVal = new StringBuilder();
                    sbVal.append((bdTotalAvgList.get(j) == null) ? "" : df.format(bdTotalAvgList.get(j).doubleValue())).append('(');
                    sbVal.append((bdTotalMaxList.get(j) == null) ? "" : df.format(bdTotalMaxList.get(j).doubleValue())).append('/');
                    sbVal.append((bdTotalMinList.get(j) == null) ? "" : df.format(bdTotalMinList.get(j).doubleValue())).append(')');

                    sbCo2 = new StringBuilder();
                    sbCo2.append((bdTotalAvgList.get(j) == null) ? "" : df.format(getChangeCo2Double(co2StdValue, bdTotalAvgList.get(j).doubleValue()))).append('(');
                    sbCo2.append((bdTotalMaxList.get(j) == null) ? "" : df.format(getChangeCo2Double(co2StdValue, bdTotalMaxList.get(j).doubleValue()))).append('/');
                    sbCo2.append((bdTotalMinList.get(j) == null) ? "" : df.format(getChangeCo2Double(co2StdValue, bdTotalMinList.get(j).doubleValue()))).append(')');

                    cls.getField("user" + j + "DecimalValue").set(mcvd, sbVal.toString());
                    cls.getField("user" + j + "DecimalCo2").set(mcvd, sbCo2.toString());
                }
                result.add(mcvd);

            } catch (NoSuchFieldException ne) {
                System.out.println("NoSuchFieldException " + ne.getMessage());
            } catch (IllegalAccessException ie) {
                System.out.println("IllegalAccessException" + ie.getMessage());
            }
        }
        return result;
    }

    
    /**
     * Method Name : getSearchDataDayOverChart
     * Date : 2011. 7. 1
     * Method 설명 : Metering Data - Chart View - Over Chart (Daily)
     * 
     * @param values
     * @param type
     * @return
     */
    public List<MvmChartViewData> getSearchDataDayOverChart(String[] values, String type) {

        List<MvmChartViewData> result = new ArrayList<MvmChartViewData>();
        Map<String, Object> hm = new HashMap<String, Object>();
        Set<Condition> set = new HashSet<Condition>();

        String beginDate = values[0];
        String endDate = values[1];
        String supplierId = values[6];
        int channel = Integer.parseInt(values[5]);
        String[] contract_number = values[4].split(",");
        Integer[] custList = contractNumberToContractId(contract_number);

        // 채널선택
        Condition cdt1 = new Condition("id.channel", new Object[] { channel }, null, Restriction.EQ);
        set.add(cdt1);
        // 조회 년월일
        if ((beginDate != null && beginDate.length() != 0) && (endDate != null && endDate.length() != 0)) {
            Condition cdt2 = new Condition("id.yyyymmdd", new Object[] { beginDate, endDate }, null, Restriction.BETWEEN);//
            set.add(cdt2);
        }
        Condition cdt3 = new Condition("contract.id", null, null, Restriction.ORDERBY);
        set.add(cdt3);
        Condition cdt4 = new Condition("id.yyyymmdd", null, null, Restriction.ORDERBY);
        set.add(cdt4);
//        Double co2StdValue = 0D;
        if ("EM".equals(type)) {
            hm = MvmEmChartViewManagerImpl.getEMSearchDataDay(set, custList);
//            co2StdValue = getTypeToCo2("EM");
//            hm.put("co2StdValue", co2StdValue);
            result = getSearchDataDayOverChartFormat(hm, supplierId);
        } else if ("GM".equals(type)) {
            hm = MvmGmChartViewManagerImpl.getGMSearchDataDay(set, custList);
//            co2StdValue = getTypeToCo2("GM");
//            hm.put("co2StdValue", co2StdValue);
            result = getSearchDataDayOverChartFormat(hm, supplierId);
        } else if ("WM".equals(type)) {
            hm = MvmWmChartViewManagerImpl.getWMSearchDataDay(set, custList);
//            co2StdValue = getTypeToCo2("WM");
//            hm.put("co2StdValue", co2StdValue);
            result = getSearchDataDayOverChartFormat(hm, supplierId);
        } else if ("HM".equals(type)) {
            hm = MvmHmChartViewManagerImpl.getHMSearchDataDay(set, custList);
//            co2StdValue = getTypeToCo2("HM");
//            hm.put("co2StdValue", co2StdValue);
            result = getSearchDataDayOverChartFormat(hm, supplierId);
        }

        return result;
    }

    /**
     * Method Name : getSearchDataDayOverChart
     * Date : 2011. 7. 1
     * Method 설명 : Metering Data - Chart View - Over Chart (Daily)
     * 
     * @param values
     * @param type
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<MvmChartViewData> getSearchDataDayOverChartFormat(Map<String, Object> hm, String supplierId) {
        List<MvmChartViewData> result = new ArrayList<MvmChartViewData>();

        MvmChartViewData mcvd = null;
        Class<? extends MvmChartViewData> cls = null;

        List<MeteringDay> dataList = (List<MeteringDay>) hm.get("dataList");

        Integer[] contId = (Integer[]) hm.get("arrContId");
        Double[] avgValue = (Double[]) hm.get("arrAvgValue");
        Double[] maxValue = (Double[]) hm.get("arrMaxValue");
        Double[] minValue = (Double[]) hm.get("arrMinValue");
        Double[] sumValue = (Double[]) hm.get("arrSumValue");
        Double co2StdValue = (Double) hm.get("co2StdValue");

        if (dataList != null && dataList.size() > 0) {

            try {
                // 일자 비교할 기준값 추출
                HashSet<String> dayHashSet = new HashSet<String>();

                // 행을 그리기 위한 기준으로 일자를 구함
                for (int i = 0; i < dataList.size(); i++) {
                    dayHashSet.add(dataList.get(i).getYyyymmdd());
                }

                Set<String> dayList = new TreeSet<String>(dayHashSet);

                String[] arrStdDate = new String[dayList.size()];
                Iterator<String> itr = dayList.iterator();
                int tmpIdx = 0;
                while (itr.hasNext()) {
                    arrStdDate[tmpIdx] = itr.next();
                    tmpIdx++;
                }

                Double[][] tmpValue = new Double[arrStdDate.length][contId.length];
                String[] firstCol = new String[arrStdDate.length];

                // 데이터 추출
                for (int i = 0; i < dataList.size(); i++) {
                    String compdate = dataList.get(i).getYyyymmdd();
                    Integer compContID = dataList.get(i).getContract().getId();

                    for (int rowIdx = 0; rowIdx < arrStdDate.length; rowIdx++) {
                        String stdDate = arrStdDate[rowIdx];
                        firstCol[rowIdx] = arrStdDate[rowIdx];

                        for (int colIdx = 0; colIdx < contId.length; colIdx++) {
                            Integer stdConId = contId[colIdx];

                            if (compdate.equals(stdDate) && (compContID == stdConId)) {
                                tmpValue[rowIdx][colIdx] = dataList.get(i).getValue_00();
                            }
                        }
                    }
                }

                Supplier supplier = supplierDao.get(Integer.parseInt(supplierId));
                DecimalFormat df = DecimalUtil.getDecimalFormat(supplier.getMd());

                String lang = supplier.getLang().getCode_2letter();
                String country = supplier.getCountry().getCode_2letter();

                // 데이터 담기
                for (int rowIdx = 0; rowIdx < arrStdDate.length; rowIdx++) {
                    mcvd = new MvmChartViewData();
                    cls = mcvd.getClass();
//                    cls.getField("firstCol").set(mcvd, firstCol[rowIdx]);
                    cls.getField("firstCol").set(mcvd, TimeLocaleUtil.getLocaleDate(firstCol[rowIdx], lang, country));

                    for (int colIdx = 0; colIdx < contId.length; colIdx++) {
                        cls.getField("user" + colIdx + "Value").set(mcvd, df.format(getNullToDouble(tmpValue[rowIdx][colIdx])));
                        cls.getField("user" + colIdx + "Co2").set(mcvd, df.format(getChangeCo2Double(co2StdValue, tmpValue[rowIdx][colIdx])));
                        cls.getField("user" + colIdx + "Avg").set(mcvd, avgValue[colIdx]);
                        cls.getField("user" + colIdx + "Max").set(mcvd, maxValue[colIdx]);
                    }
                    result.add(mcvd);
                }

                // 평균(최대/최소값)세팅
                mcvd = new MvmChartViewData();
                mcvd.setFirstCol("Avg(Max/Min)");
                cls = mcvd.getClass();

                StringBuilder sbVal = null;
                StringBuilder sbCo2 = null;

                Double avg = null;
                Double max = null;
                Double min = null;

                for (int j = 0; j < contId.length; j++) {
                    avg = getNullToDouble(avgValue[j]);
                    max = getNullToDouble(maxValue[j]);
                    min = getNullToDouble(minValue[j]);

                    // if (avgValue[j] != null) {

                    // cls.getField("user" + j + "Value").set(mcvd,
                    // getDoubleToStirng(avg) + "(" + getDoubleToStirng(max) + "/" + getDoubleToStirng(min) + ")");
                    // cls.getField("user" + j + "Co2").set(
                    // mcvd,
                    // getChangeCo2(co2StdValue, avg) + "(" + getChangeCo2(co2StdValue, max) + "/"
                    // + getChangeCo2(co2StdValue, min) + ")");

                    sbVal = new StringBuilder();
                    sbVal.append(df.format(avg)).append('(');
                    sbVal.append(df.format(max)).append('/');
                    sbVal.append(df.format(min)).append(')');

                    sbCo2 = new StringBuilder();
                    sbCo2.append(df.format(getChangeCo2Double(co2StdValue, avg))).append('(');
                    sbCo2.append(df.format(getChangeCo2Double(co2StdValue, max))).append('/');
                    sbCo2.append(df.format(getChangeCo2Double(co2StdValue, min))).append(')');

                    cls.getField("user" + j + "Value").set(mcvd, sbVal.toString());
                    cls.getField("user" + j + "Co2").set(mcvd, sbCo2.toString());
                    // }
                }
                result.add(mcvd);

                // 합계 세팅
                mcvd = new MvmChartViewData();
                mcvd.setFirstCol("(Total)");
                cls = mcvd.getClass();
                Double sum = null;

                for (int j = 0; j < contId.length; j++) {
                    sum = getNullToDouble(sumValue[j]);

                    // if (sumValue[j] != null) {
                    cls.getField("user" + j + "Value").set(mcvd, df.format(sum));
                    cls.getField("user" + j + "Co2").set(mcvd, df.format(getChangeCo2Double(co2StdValue, sum)));
                    // }
                }
                result.add(mcvd);

            } catch (NoSuchFieldException ne) {
                System.out.println("NoSuchFieldException " + ne.getMessage());
            } catch (IllegalAccessException ie) {
                System.out.println("IllegalAccessException" + ie.getMessage());
            }
        }
        return result;
    }

    /**
     * × @see
     * com.aimir.service.mvm.MvmChartViewManager#getSearchDataMonth(java.lang
     * .String[], java.lang.String)
     *
     * @Method Name : getSearchDataMonth
     * @Date : 2010. 4. 8.
     * @Method 설명 : 월별차트
     * @param values
     * @param type
     * @return
     */
    public List<MvmChartViewData> getSearchDataMonthOverChart(String[] values, String type) {
        List<MvmChartViewData> result = new ArrayList<MvmChartViewData>();
        HashMap<String, Object> hm = new HashMap<String, Object>();

//        Set<Condition> set = new HashSet<Condition>();
        Set<Condition> set = new LinkedHashSet<Condition>();

        String beginDate = values[0].substring(0, 6);
        String endDate = values[1].substring(0, 6);
        String supplierId = values[6];
        int channel = Integer.parseInt(values[5]);
        String[] contract_number = values[4].split(",");
        Integer[] custList = contractNumberToContractId(contract_number);

        // 채널선택
//        Condition cdt = new Condition("contract.id", null, null, Restriction.ORDERBY);
        Condition cdt = new Condition("id.yyyymm", null, null, Restriction.ORDERBY);
        set.add(cdt);
        Condition cdt1 = new Condition("id.channel", new Object[] { channel }, null, Restriction.EQ);
        set.add(cdt1);
        // 조회년월일시
        if ((beginDate != null && beginDate.length() != 0) && (endDate != null && endDate.length() != 0)) {
            Condition cdt2 = new Condition("id.yyyymm", new Object[] { beginDate, endDate }, null, Restriction.BETWEEN);//
            set.add(cdt2);
        }
//        Condition cdt3 = new Condition("id.yyyymm", null, null, Restriction.ORDERBY);
        Condition cdt3 = new Condition("contract.id", null, null, Restriction.ORDERBY);
        set.add(cdt3);
        Double co2StdValue = 0D;
        if ("EM".equals(type)) {
            hm = MvmEmChartViewManagerImpl.getEMSearchDataMonth(set, custList);
            co2StdValue = getTypeToCo2("EM");
            hm.put("co2StdValue", co2StdValue);
            result = getMonthChangRowByCol(hm, supplierId);
        } else if ("GM".equals(type)) {
            hm = MvmGmChartViewManagerImpl.getGMSearchDataMonth(set, custList);
            co2StdValue = getTypeToCo2("GM");
            hm.put("co2StdValue", co2StdValue);
            result = getMonthChangRowByCol(hm, supplierId);
        } else if ("WM".equals(type)) {
            hm = MvmWmChartViewManagerImpl.getWMSearchDataMonth(set, custList);
            co2StdValue = getTypeToCo2("WM");
            hm.put("co2StdValue", co2StdValue);
            result = getMonthChangRowByCol(hm, supplierId);
        } else if ("HM".equals(type)) {
            hm = MvmHmChartViewManagerImpl.getHMSearchDataMonth(set, custList);
            co2StdValue = getTypeToCo2("HM");
            hm.put("co2StdValue", co2StdValue);
            result = getMonthChangRowByCol(hm, supplierId);
        }
        return result;
    }

    /**
     * × @see
     * com.aimir.service.mvm.MvmChartViewManager#getSearchDataWeek(java.lang
     * .String[], java.lang.String)
     *
     * @Method Name : getSearchDataWeek
     * @Date : 2010. 4. 8.
     * @Method 설명 : 주별데이터
     * @param values
     * @param type
     * @return
     */
    public List<MvmChartViewData> getSearchDataWeekOverChart(String[] values, String type) {
        List<MvmChartViewData> result = new ArrayList<MvmChartViewData>();
        HashMap<String, Object> hm = new HashMap<String, Object>();
        String beginDate = values[0];
        String yyMM = beginDate.substring(0, 6);
        int channel = (StringUtil.nullToBlank(values[5]).length() == 0) ? -1 : Integer.parseInt(values[5]);
        String supplierId = values[6];

        String[] contract_number = values[4].split(",");
        Integer[] custList = contractNumberToContractId(contract_number);

        // 조회조건 세팅
        Set<Condition> set = new HashSet<Condition>();
        Condition cdt = new Condition("contract.id", null, null, Restriction.ORDERBY);
        set.add(cdt);
        Condition cdt1 = new Condition("id.channel", new Object[] { channel }, null, Restriction.EQ);
        set.add(cdt1);
        Condition cdt2 = new Condition("id.yyyymmdd", null, null, Restriction.ORDERBY);
        set.add(cdt2);
        Double co2StdValue = 0D;
        if ("EM".equals(type)) {
            hm = MvmEmChartViewManagerImpl.getEMSearchDataWeek(set, custList, yyMM);
            co2StdValue = getTypeToCo2("EM");
            hm.put("co2StdValue", co2StdValue);
            result = getSeasonAndWeekChangRowByCol(hm, supplierId);
        } else if ("GM".equals(type)) {
            hm = MvmGmChartViewManagerImpl.getGMSearchDataWeek(set, custList, yyMM);
            co2StdValue = getTypeToCo2("GM");
            hm.put("co2StdValue", co2StdValue);
            result = getSeasonAndWeekChangRowByCol(hm, supplierId);
        } else if ("WM".equals(type)) {
            hm = MvmWmChartViewManagerImpl.getWMSearchDataWeek(set, custList, yyMM);
            co2StdValue = getTypeToCo2("WM");
            hm.put("co2StdValue", co2StdValue);
            result = getSeasonAndWeekChangRowByCol(hm, supplierId);
        } else if ("HM".equals(type)) {
            hm = MvmHmChartViewManagerImpl.getHMSearchDataWeek(set, custList, yyMM);
            co2StdValue = getTypeToCo2("HM");
            hm.put("co2StdValue", co2StdValue);
            result = getSeasonAndWeekChangRowByCol(hm, supplierId);
        }
        return result;
    }

    /**
     * × @see
     * com.aimir.service.mvm.MvmChartViewManager#getSearchDataDayWeek(java
     * .lang.String[], java.lang.String)
     *
     * @Method Name : getSearchDataDayWeek
     * @Date : 2010. 4. 8.
     * @Method 설명 : 요일별데이터
     * @param values
     * @param type
     * @return
     */
    public List<MvmChartViewData> getSearchDataDayWeekOverChart(String[] values, String type) {
        List<MvmChartViewData> result = new ArrayList<MvmChartViewData>();
        HashMap<String, Object> hm = new HashMap<String, Object>();

        String beginDate = values[0];
        String endDate = values[1];
        int channel = Integer.parseInt(values[5]);
        String supplierId = values[6];

        String[] contract_number = values[4].split(",");
        Integer[] custList = contractNumberToContractId(contract_number);

        // 조회시 필요한 기준채널, 일자, 미터아이디 생성
        Set<Condition> set = new HashSet<Condition>();
        Condition cdt = new Condition("contract.id", null, null, Restriction.ORDERBY);
        set.add(cdt);
        Condition cdt1 = new Condition("id.channel", new Object[] { channel }, null, Restriction.EQ);
        set.add(cdt1);
        if ((beginDate != null && beginDate.length() != 0) && (endDate != null && endDate.length() != 0)) {
            Condition cdt2 = new Condition("id.yyyymmdd", new Object[] { beginDate, endDate }, null, Restriction.BETWEEN);
            set.add(cdt2);
        }
        Condition cdt3 = new Condition("id.yyyymmdd", null, null, Restriction.ORDERBY);
        set.add(cdt3);
        Double co2StdValue = 0D;
        if ("EM".equals(type)) {
            hm = MvmEmChartViewManagerImpl.getEMSearchDataDayWeek(set, custList);
            co2StdValue = getTypeToCo2("EM");
            hm.put("co2StdValue", co2StdValue);
            result = getDayWeekChangRowByCol(hm, supplierId);
        } else if ("GM".equals(type)) {
            hm = MvmGmChartViewManagerImpl.getGMSearchDataDayWeek(set, custList);
            co2StdValue = getTypeToCo2("GM");
            hm.put("co2StdValue", co2StdValue);
            result = getDayWeekChangRowByCol(hm, supplierId);
        } else if ("WM".equals(type)) {
            hm = MvmWmChartViewManagerImpl.getWMSearchDataDayWeek(set, custList);
            co2StdValue = getTypeToCo2("WM");
            hm.put("co2StdValue", co2StdValue);
            result = getDayWeekChangRowByCol(hm, supplierId);
        } else if ("HM".equals(type)) {
            hm = MvmHmChartViewManagerImpl.getHMSearchDataDayWeek(set, custList);
            co2StdValue = getTypeToCo2("HM");
            hm.put("co2StdValue", co2StdValue);
            result = getDayWeekChangRowByCol(hm, supplierId);
        }
        return result;
    }

    /*
     * Double 형 데이터를 소스 4째자리까지 Stirng으로 표시
     */
    public String getDoubleToStirng(Double value) {
        Double result = 0D;
        if (value != null && value != 0) {
            result = value;
        }
        else {
            result=0D;
        }

        return String.format("%.4f", result);
    }

    /*
     * Double 형 데이터를 소스 4째자리까지 Double 형으로 표시
     */
    public Double getNullToDouble(Double value) {
        Double result = 0D;
        String strValue = "0";
        if (value != null) {
            strValue = String.format("%.4f", value);
            result = Double.parseDouble(strValue);
        }

        return result;
    }

    /*
     * co2 계산 (Double -> String)
     */
    public String getChangeCo2(Double Co2Value, Double value) {

        Double result = 0D;
        if (value == null || Co2Value == null) {
            result = 0D;
        } else {
            result = value * Co2Value;
        }

        return String.format("%.4f", result);
    }

    /*
     * co2 계산 (Double -> Double)
     */
    public Double getChangeCo2Double(Double Co2Value, Double value) {
        Double result = 0D;
        if (value == null || Co2Value == null) {
            result = 0D;
        } else {
            result = value * Co2Value;
        }

        return Double.parseDouble(String.format("%.4f", result));
    }

    /*
     * supplyTypecode를 가지고 Co2계산기준값을 가져옴
     */
    public Double getTypeToCo2(String type) {
        String meterType = ChangeMeterTypeName.valueOf(type).getCode();
        int typeId = codeDao.getCodeIdByCode(MeterType.valueOf(meterType).getServiceType());

        Co2Formula co2Formula = co2formulaDao.getCo2FormulaBySupplyType(typeId);

        if(co2Formula == null) return 0.0;
        else return  co2Formula.getCo2factor();

    }

    /*
     * contractNumber로 contract Id를 추출한다.
     */

    public Integer[] contractNumberToContractId(String [] contractNumber) {
        List<Contract> contract = new ArrayList<Contract>();
        Integer[] result = new Integer[contractNumber.length];

        if( contractNumber.length > 0) {
            Set<Condition> set = new HashSet<Condition>();
            Condition cdt1 = new Condition("contractNumber", contractNumber, null, Restriction.IN);
            set.add(cdt1);
            Condition cdt2 = new Condition("id", null, null, Restriction.ORDERBY);
            set.add(cdt2);
            contract = contractDao.getContractByListCondition(set);

            int i=0;
            Iterator<Contract> it = contract.iterator();
            while (it.hasNext()) {
                Contract ctrt = (Contract) it.next();
                result[i] = ctrt.getId();
                i++;
            }
        }
        return result;
    }
}