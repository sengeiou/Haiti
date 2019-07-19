package com.aimir.service.mvm.impl;

import java.math.BigDecimal;
import java.math.MathContext;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.jws.WebService;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aimir.constants.CommonConstants;
import com.aimir.constants.CommonConstants.ChangeMeterTypeName;
import com.aimir.constants.CommonConstants.ChannelCalcMethod;
import com.aimir.constants.CommonConstants.DateType;
import com.aimir.constants.CommonConstants.DefaultChannel;
import com.aimir.constants.CommonConstants.ElectricityChannel;
import com.aimir.constants.CommonConstants.MeterType;
import com.aimir.constants.CommonConstants.PeakType;
import com.aimir.constants.CommonConstants.UsageRateDateType;
import com.aimir.dao.device.MeterDao;
import com.aimir.dao.mvm.ChannelConfigDao;
import com.aimir.dao.mvm.DayEMDao;
import com.aimir.dao.mvm.EachMeterChannelConfigDao;
import com.aimir.dao.mvm.MeteringDayDao;
import com.aimir.dao.mvm.MeteringLpDao;
import com.aimir.dao.mvm.MeteringMonthDao;
import com.aimir.dao.mvm.MonthEMDao;
import com.aimir.dao.mvm.SeasonDao;
import com.aimir.dao.system.Co2FormulaDao;
import com.aimir.dao.system.ContractDao;
import com.aimir.dao.system.CustomerDao;
import com.aimir.dao.system.LocationDao;
import com.aimir.dao.system.SupplierDao;
import com.aimir.dao.system.TOURateDao;
import com.aimir.model.device.Meter;
import com.aimir.model.mvm.Season;
import com.aimir.model.system.Co2Formula;
import com.aimir.model.system.Contract;
import com.aimir.model.system.Customer;
import com.aimir.model.system.DecimalPattern;
import com.aimir.model.system.Supplier;
import com.aimir.model.system.TOURate;
import com.aimir.model.system.TariffType;
import com.aimir.service.mvm.MvmMeterValueManager;
import com.aimir.service.mvm.MvmDetailViewManager;
import com.aimir.service.mvm.SeasonManager;
import com.aimir.service.mvm.bean.ChannelInfo;
import com.aimir.service.mvm.bean.CustomerInfo;
import com.aimir.service.mvm.bean.MvmDetailViewData;
import com.aimir.service.mvm.bean.SeasonData;
import com.aimir.util.CalendarUtil;
import com.aimir.util.Condition;
import com.aimir.util.Condition.Restriction;
import com.aimir.util.DateTimeUtil;
import com.aimir.util.DecimalUtil;
import com.aimir.util.SearchCalendarUtil;
import com.aimir.util.StringUtil;
import com.aimir.util.TimeLocaleUtil;
import com.aimir.util.TimeUtil;

/**
 * 
 * @author Jiae
 * @Desc MeterValue를 보애주기 위한 Metering Data가젯의 검색용 메소드
 * 		 mvmMeterValueMaxGadget용
 *
 */
@WebService(endpointInterface = "com.aimir.service.mvm.MvmDetailMeterValueViewManager")
@Service(value = "mvmDetailMeterValueViewManager")
public class MvmMeterValueManagerImpl implements MvmMeterValueManager {

    protected static Log logger = LogFactory.getLog(MvmMeterValueManagerImpl.class);
    
    @Autowired
    ChannelConfigDao channelConfigDao;
    
    @Autowired
    LocationDao locationDao;
    
    @Autowired
    MeterDao mtrDao;
    
    @Autowired
    MeteringLpDao meteringLpDao;
    
    @Autowired
    MeteringDayDao meteringDayDao;
    
    @Autowired
    MeteringMonthDao meteringMonthDao;
    
    @Autowired
    SupplierDao supplierDao;
    
    @Autowired
    SeasonManager seasonManager;
    
    @Autowired
    EachMeterChannelConfigDao eachMeterChannelConfigDao;
    /**
     * × @see
     * com.aimir.service.mvm.MvmDetailMeterValueViewManager#getCustomerInfo(java.lang
     * .String)
     * 
     * @Method Name : getChannelInfo
     * @Method 설명 : 채널값을 화면에 표시한다.
     * @param mdsId, type
     *            (미터아이디, 미터구분)
     * @return
     */
    public List<ChannelInfo> getChannelInfoAll(String mdsId, String type) {
    	return getChannelInfo(mdsId, type, true);
    }    
    public List<ChannelInfo> getChannelInfo(String mdsId, String type) {
    	return getChannelInfo(mdsId, type, false);
    }
    public List<ChannelInfo> getChannelInfo(String mdsId, String type, boolean showCO2) {
        
        List<ChannelInfo> result = new ArrayList<ChannelInfo>();
        String meterType = ChangeMeterTypeName.valueOf(type).getCode();
        String tlbType = MeterType.valueOf(meterType).getLpClassName();
        Map<String , Object> hm = new HashMap<String, Object>();
        hm.put("tlbType", tlbType);

        Meter meter = mtrDao.findByCondition("mdsId", mdsId);
        List<Object> ojbList = null;
        
        hm.put("mdevId", mdsId);
        ojbList = eachMeterChannelConfigDao.getByList(hm);
        if( ojbList != null && ojbList.size() > 0) {
            Iterator<Object> it = ojbList.iterator();
            int channelIndex = 1;
            while (it.hasNext()) {
                Object[] obj = (Object[]) it.next();
                int codeId = (Integer)obj[0];
                String codeNm = (String)obj[1];
                String unit = (String)obj[2];
                if(showCO2){
                    ChannelInfo channelInfo = new ChannelInfo();
                    channelInfo.setCodeId(String.valueOf(channelIndex));
                    channelInfo.setCodeName(codeNm+"["+unit+"]");
                    result.add(channelInfo);
                    channelIndex++;   	
                }else{
                    if(codeId != 0 ) {
                        ChannelInfo channelInfo = new ChannelInfo();
                        channelInfo.setCodeId(String.valueOf(channelIndex));
                        channelInfo.setCodeName(codeNm+"["+unit+"]");
                        result.add(channelInfo);
                        channelIndex++;
                    }   
                }

            }
            
            if(result != null && !result.isEmpty()){
                return result;
            }
        }
        
        if (meter != null) {
            // 미터의 devicemodel_Id가 존재할때, 존재하지 않을때는 그냥 table명으로 조회
            if(meter.getModel() != null && meter.getModel().getId() > 0 ) {
                
                if(meter.getModel().getDeviceConfig() != null){
                    int deviceConfigId = meter.getModel().getDeviceConfig().getId();
                    hm.put("deviceConfigId", deviceConfigId);
                    ojbList = channelConfigDao.getByList(hm);
                }
            }

            if( ojbList != null && ojbList.size() > 0) {
                Iterator<Object> it = ojbList.iterator();
                while (it.hasNext()) {
                    Object[] obj = (Object[]) it.next();
                    int codeId = (Integer)obj[0];
                    String codeNm = (String)obj[1];
                    String unit = (String)obj[2];
                    
                    //채널값이 1이거나 2인 경우 meter value 값을 표시한다.
                    if(codeId == 1 || codeId == 2) {
	                    ChannelInfo channelInfo = new ChannelInfo();
	                    channelInfo.setCodeId("mv"+String.valueOf(codeId));	//MeterValue
	                    channelInfo.setCodeName(codeNm+"(Meter Value)"+"["+unit+"]");
	                    result.add(channelInfo);
                    }
                    
                    if(showCO2){
                        ChannelInfo channelInfo = new ChannelInfo();
                        channelInfo.setCodeId(String.valueOf(codeId));
                        channelInfo.setCodeName(codeNm+"["+unit+"]");
                        result.add(channelInfo);
                    }else{
                      if(codeId != 0 ) {
                        ChannelInfo channelInfo = new ChannelInfo();
                        channelInfo.setCodeId(String.valueOf(codeId));
                        channelInfo.setCodeName(codeNm+"["+unit+"]");
                        result.add(channelInfo);
                      }                    	
                    }
                    

                    
                }
            }
            else {// 해당 데이터가 없을경우 default 세팅
                if(type.equals("EM")) {//전기 default
                    for(int i=0; i < ElectricityChannel.values().length; i++) {
                        String name = ElectricityChannel.values()[i]+"";
                        int codeId = ElectricityChannel.valueOf(name).getChannel();
                        
                        if(showCO2){
                            ChannelInfo channelInfo = new ChannelInfo();
                            channelInfo.setCodeId(String.valueOf(codeId));
                            channelInfo.setCodeName(name);
                            result.add(channelInfo);
                        }else{
                            if(codeId != 0 ) {
                                ChannelInfo channelInfo = new ChannelInfo();
                                channelInfo.setCodeId(String.valueOf(codeId));
                                channelInfo.setCodeName(name);
                                result.add(channelInfo);
                            }                        	
                        }
                    }
                }
                else {// 전기 이외의 default
                    for(int i=0; i < DefaultChannel.values().length; i++) {
                        String name = DefaultChannel.values()[i]+"";
                        int codeId = DefaultChannel.valueOf(name).getCode();
                        if(showCO2){
                            ChannelInfo channelInfo = new ChannelInfo();
                            channelInfo.setCodeId(String.valueOf(codeId));
                            channelInfo.setCodeName(name);
                            result.add(channelInfo);
                        }else{
                            if(codeId != 0 ) {
                                ChannelInfo channelInfo = new ChannelInfo();
                                channelInfo.setCodeId(String.valueOf(codeId));
                                channelInfo.setCodeName(name);
                                result.add(channelInfo);
                            }                        	
                        }

                    }
                }
                
            }
        }
        return result;

    }
    
    /**
     * method name : getMeteringValueDetailRatelyChartData<b/>
     * method Desc : Metering Data 맥스가젯 상세화면에서 Rate 별 Chart 데이터를 조회한다.
     *
     * @param conditionMap
     * @return
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> getMeteringValueDetailRatelyChartData(Map<String, Object> conditionMap) {
        List<Map<String, Object>> searchData = new ArrayList<Map<String, Object>>();
        Map<String, Object> resultMap = new HashMap<String, Object>();
        Map<String, Object> searchTotalData = new HashMap<String, Object>();
        String searchStartDate = (String)conditionMap.get("searchStartDate");
        String searchEndDate = (String)conditionMap.get("searchEndDate");
        Integer supplierId = (Integer)conditionMap.get("supplierId");
        String[] rate1Array = ((String)conditionMap.get("rate1")).split(",");
        String[] rate2Array = ((String)conditionMap.get("rate2")).split(",");
        String[] rate3Array = ((String)conditionMap.get("rate3")).split(",");
        List<Map<String, Object>> list = meteringDayDao.getMeteringDataDetailRatelyData(conditionMap);
        List<Map<String, Object>> mvList = meteringLpDao.getMeteringDataDetailLpData(conditionMap);

        if (list == null || list.size() <= 0) {
            resultMap.put("searchData", searchData);
            return resultMap;
        }

        Map<String, Object> map = null;
        Map<String, Object> mvMap = null;
        List<Map<String, Object>> objList = null;
        Map<String, Object> objMap = null;
        Map<String, Object> listMvMap = new HashMap<String, Object>();
        Map<String, Object> listMap = new HashMap<String, Object>();
        Set<String> dateSet = new LinkedHashSet<String>();
        Supplier supplier = supplierDao.get(supplierId);
        String country = supplier.getCountry().getCode_2letter();
        String lang    = supplier.getLang().getCode_2letter();
        DecimalFormat mdf = DecimalUtil.getDecimalFormat(supplier.getMd());
        Number nvalue = null;
        Number mvNvalue = null;
        BigDecimal bdRateValue = null;
        BigDecimal bdMvRateValue = null;

        List<String> rateDateTypeList = new ArrayList<String>();
        List<Integer> rateStartTimeList = new ArrayList<Integer>();
        List<Integer> rateEndTimeList = new ArrayList<Integer>();
        List<BigDecimal> rateTotalList = new ArrayList<BigDecimal>();   // rate 별 합계
        List<BigDecimal> rateMaxList = new ArrayList<BigDecimal>();   // rate 별 Meter Value Max

        rateDateTypeList.add(rate1Array[0]);
        rateDateTypeList.add(rate2Array[0]);
        rateDateTypeList.add(rate3Array[0]);
        int len = rateDateTypeList.size();

        if (rateDateTypeList.get(0).equals(UsageRateDateType.WEEK_END.getCode())) {
            rateStartTimeList.add(0);
            rateEndTimeList.add(23);
        } else {
            rateStartTimeList.add(new Integer(rate1Array[1]));
            rateEndTimeList.add(new Integer(rate1Array[2]));
        }

        if (rateDateTypeList.get(1).equals(UsageRateDateType.WEEK_END.getCode())) {
            rateStartTimeList.add(0);
            rateEndTimeList.add(23);
        } else {
            rateStartTimeList.add(new Integer(rate2Array[1]));
            rateEndTimeList.add(new Integer(rate2Array[2]));
        }

        if (rateDateTypeList.get(2).equals(UsageRateDateType.WEEK_END.getCode())) {
            rateStartTimeList.add(0);
            rateEndTimeList.add(23);
        } else {
            rateStartTimeList.add(new Integer(rate3Array[1]));
            rateEndTimeList.add(new Integer(rate3Array[2]));
        }

        int year = 0;
        int month = 0;
        int day = 0;
        Calendar cal = null;

        int duration = 0;

        try {
            duration = TimeUtil.getDayDuration(searchStartDate, searchEndDate);
        } catch (ParseException e1) {
            e1.printStackTrace();
        }

        // rate 별 합계 초기화
        for (int i = 0; i < len; i++) {
            rateTotalList.add(new BigDecimal(0));
        }
        
        // rate 별 Meter Value 최대값 초기화
        for (int i = 0; i < len; i++) {
            rateMaxList.add(new BigDecimal(0));
        }

        String tmpDate = searchStartDate;

        for (int i = 0; i < (duration + 1); i++) {
            dateSet.add(tmpDate);
            try {
                tmpDate = TimeUtil.getPreDay(tmpDate + "000000", -1).substring(0, 8);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        
        for (Map<String, Object> obj : list) {
            listMap.put((String)obj.get("YYYYMMDD"), obj);
        }

        for (Map<String, Object> obj : mvList) {
        	String yyyymmddhh = obj.get("yyyymmddhh").toString();
        	String yyyymmdd = yyyymmddhh.substring(0,8);
        	List<Map<String,Object>> tempList = (List<Map<String, Object>>) listMvMap.get(yyyymmdd);
        	if(tempList == null) {
        		tempList = new ArrayList<Map<String,Object>>();
        	}
        	Map<String,Object> tempMap = new HashMap<String,Object>();
        	tempMap.put(yyyymmddhh.substring(8,10), obj.get("value"));
        	tempList.add(tempMap);
        	
        	listMvMap.put(yyyymmdd, tempList);
        }

        for (String date : dateSet) {
            if (listMap.get(date) != null) {
                objList = (List<Map<String, Object>>) listMvMap.get(date);
                objMap = (Map<String, Object>)listMap.get(date);
                year = Integer.parseInt(date.substring(0, 4));
                month = Integer.parseInt(date.substring(4, 6))-1;
                day = Integer.parseInt(date.substring(6, 8));
                cal = Calendar.getInstance();
                cal.set(year, month, day);

                for (int i = 0; i < len; i++) {
                    map = new HashMap<String, Object>();
                    mvMap = new HashMap<String, Object>();
                    bdRateValue = new BigDecimal("0");
                    bdMvRateValue = new BigDecimal("0");

                    if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY || cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {   // 토/일요일
                        if (rateDateTypeList.get(i).equals(UsageRateDateType.WEEK_END.getCode())) {     // weekend
                            if (rateStartTimeList.get(i) > rateEndTimeList.get(i)) {
                            	for (int j = 0; j < (rateEndTimeList.get(i) + 1); j++) {
                            		nvalue = (Number)objMap.get("VALUE_" + StringUtil.frontAppendNStr('0', new Integer(j).toString(), 2));
                                    bdRateValue = bdRateValue.add(new BigDecimal(nvalue == null ? "0" : nvalue.toString()));
                                    
                            		Integer size = objList.size();
                                	for (int k = 0; k < size; k++) {
                                		Map<String,Object> tempMap = (Map<String, Object>) objList.get(k);
										if(j == k) {
											mvNvalue = (Number)tempMap.get(StringUtil.frontAppendNStr('0', new Integer(j).toString(), 2));
											bdMvRateValue = bdMvRateValue.max(new BigDecimal(mvNvalue == null ? "0" : mvNvalue.toString()));
											break;
										}
									}
                                }

                                for (int j = rateStartTimeList.get(i); j < 24; j++) {
                                	nvalue = (Number)objMap.get("VALUE_" + StringUtil.frontAppendNStr('0', new Integer(j).toString(), 2));
                                    bdRateValue = bdRateValue.add(new BigDecimal(nvalue == null ? "0" : nvalue.toString()));
                                    
                                	Integer size = objList.size();
                                	for (int k = 0; k < size; k++) {
                                		Map<String,Object> tempMap = (Map<String, Object>) objList.get(k);
										if(j == k) {
											mvNvalue = (Number)tempMap.get(StringUtil.frontAppendNStr('0', new Integer(j).toString(), 2));
											bdMvRateValue = bdMvRateValue.max(new BigDecimal(mvNvalue == null ? "0" : mvNvalue.toString()));
											break;
										}
									}
                                }

                            } else {
                                for (int j = rateStartTimeList.get(i); j < (rateEndTimeList.get(i) + 1); j++) {
                                	nvalue = (Number)objMap.get("VALUE_" + StringUtil.frontAppendNStr('0', new Integer(j).toString(), 2));
                                    bdRateValue = bdRateValue.add(new BigDecimal(nvalue == null ? "0" : nvalue.toString()));
                                    
                                	Integer size = objList.size();
                                	for (int k = 0; k < size; k++) {
                                		Map<String,Object> tempMap = (Map<String, Object>) objList.get(k);
										if(j == k) {
											mvNvalue = (Number)tempMap.get(StringUtil.frontAppendNStr('0', new Integer(j).toString(), 2));
											bdMvRateValue = bdMvRateValue.max(new BigDecimal(mvNvalue == null ? "0" : mvNvalue.toString()));
											break;
										}
									}
                                }
                            }
                        } else {
                            bdRateValue = null;
                            bdMvRateValue = null;
                        }
                    } else {            // 평일
                        if (rateDateTypeList.get(i).equals(UsageRateDateType.WEEK_END.getCode())) {
                            bdRateValue = null;
                            bdMvRateValue = null;
                        } else {        // weekday
                            if (rateStartTimeList.get(i) > rateEndTimeList.get(i)) {
                                for (int j = 0; j < (rateEndTimeList.get(i) + 1); j++) {
                                	nvalue = (Number)objMap.get("VALUE_" + StringUtil.frontAppendNStr('0', new Integer(j).toString(), 2));
                                    bdRateValue = bdRateValue.add(new BigDecimal(nvalue == null ? "0" : nvalue.toString()));
                                    
                                	Integer size = objList.size();
                                	for (int k = 0; k < size; k++) {
                                		Map<String,Object> tempMap = (Map<String, Object>) objList.get(k);
										if(j == k) {
											mvNvalue = (Number)tempMap.get(StringUtil.frontAppendNStr('0', new Integer(j).toString(), 2));
											bdMvRateValue = bdMvRateValue.max(new BigDecimal(mvNvalue == null ? "0" : mvNvalue.toString()));
											break;
										}
									}
                                }

                                for (int j = rateStartTimeList.get(i); j < 24; j++) {
                                	nvalue = (Number)objMap.get("VALUE_" + StringUtil.frontAppendNStr('0', new Integer(j).toString(), 2));
                                    bdRateValue = bdRateValue.add(new BigDecimal(nvalue == null ? "0" : nvalue.toString()));
                                    
                                	Integer size = objList.size();
                                	for (int k = 0; k < size; k++) {
                                		Map<String,Object> tempMap = (Map<String, Object>) objList.get(k);
										if(j == k) {
											mvNvalue = (Number)tempMap.get(StringUtil.frontAppendNStr('0', new Integer(j).toString(), 2));
											bdMvRateValue = bdMvRateValue.max(new BigDecimal(mvNvalue == null ? "0" : mvNvalue.toString()));
											break;
										}
									}
                                }
                            } else {
                                for (int j = rateStartTimeList.get(i); j < (rateEndTimeList.get(i) + 1); j++) {
                                	nvalue = (Number)objMap.get("VALUE_" + StringUtil.frontAppendNStr('0', new Integer(j).toString(), 2));
                                    bdRateValue = bdRateValue.add(new BigDecimal(nvalue == null ? "0" : nvalue.toString()));
                                    
                                	Integer size = objList.size();
                                	for (int k = 0; k < size; k++) {
                                		Map<String,Object> tempMap = (Map<String, Object>) objList.get(k);
										if(j == k) {
											mvNvalue = (Number)tempMap.get(StringUtil.frontAppendNStr('0', new Integer(j).toString(), 2));
											bdMvRateValue = bdMvRateValue.max(new BigDecimal(mvNvalue == null ? "0" : mvNvalue.toString()));
											break;
										}
									}
                                }
                            }
                        }
                    }

                    map.put("localeDate", TimeLocaleUtil.getLocaleDate(date, lang, country));
                    map.put("rateIndex", (i+1));
                    map.put("value", (bdRateValue == null) ? 0D : bdRateValue.doubleValue());
                    map.put("decimalValue", (bdRateValue == null) ? mdf.format(0D) : mdf.format(bdRateValue.doubleValue()));
                    
                    if (bdRateValue != null) {
                        rateTotalList.set(i, rateTotalList.get(i).add(bdRateValue));
                    }
                    searchData.add(map);
                    
                    mvMap.put("localeDate", TimeLocaleUtil.getLocaleDate(date, lang, country));
                    mvMap.put("rateIndex", "mv"+(i+1));
                    mvMap.put("value", (bdMvRateValue == null) ? 0D : bdMvRateValue.doubleValue());
                    mvMap.put("decimalValue", (bdMvRateValue == null) ? mdf.format(0D) : mdf.format(bdMvRateValue.doubleValue()));
                    if (bdMvRateValue != null) {
                        rateMaxList.set(i, rateMaxList.get(i).max(bdMvRateValue));
                    }
                    searchData.add(mvMap);
                }
            } else {
                for (int i = 0; i < len; i++) {
                    map = new HashMap<String, Object>();
                    map.put("localeDate", TimeLocaleUtil.getLocaleDate(date, lang, country));
                    map.put("rateIndex", (i+1));
                    map.put("value", 0D);
                    map.put("decimalValue", mdf.format(0D));
                    searchData.add(map);
                    
                    mvMap = new HashMap<String, Object>();
                    mvMap.put("localeDate", TimeLocaleUtil.getLocaleDate(date, lang, country));
                    mvMap.put("rateIndex", "mv"+(i+1));
                    mvMap.put("value", 0D);
                    mvMap.put("decimalValue", mdf.format(0D));
                    searchData.add(mvMap);
                }
            }
        }

        resultMap.put("searchData", searchData);

        for (int i = 0; i < rateTotalList.size(); i++) {
            searchTotalData.put("total" + i, mdf.format(rateTotalList.get(i).doubleValue()));
        }
        for (int i = 0; i < rateMaxList.size(); i++) {
            searchTotalData.put("totalmv" + i, mdf.format(rateMaxList.get(i).doubleValue()));
        }
        resultMap.put("searchTotalData", searchTotalData);
        return resultMap;
    }
    
    /**
     * method name : getMeteringValueDetailIntervalChartData<b/>
     * method Desc : Metering Data 맥스가젯 상세화면에서 Interval 별 Chart 데이터를 조회한다.
     *
     * @param conditionMap
     * @return
     */
    public Map<String, Object> getMeteringValueDetailIntervalChartData(Map<String, Object> conditionMap) {
        Map<String, Object> resultMap = new HashMap<String, Object>();
        List<Map<String, Object>> searchData = new ArrayList<Map<String, Object>>();
        List<Map<String, Object>> searchAddData = new ArrayList<Map<String, Object>>();
        Integer supplierId = (Integer)conditionMap.get("supplierId");
        String meterNo = (String)conditionMap.get("meterNo");
        String searchStartDate = (String)conditionMap.get("searchStartDate");
        String searchEndDate = (String)conditionMap.get("searchEndDate");
        String searchStartHour = (String)conditionMap.get("searchStartHour");
        String searchEndHour = (String)conditionMap.get("searchEndHour");
        String[] channelArray = ((String)conditionMap.get("channel")).split(",");
        List<Integer> channelIdList = new ArrayList<Integer>();
        List<String> channelIdStrList = new ArrayList<String>();

        for (String obj : channelArray) {
    		if(!channelIdList.contains(Integer.parseInt(obj.replaceAll("mv", "")))) {
    			channelIdList.add(Integer.parseInt(obj.replaceAll("mv", "")));
    		}
        	channelIdStrList.add(obj);
        }

        conditionMap.put("channelIdList", channelIdList);

        List<Map<String, Object>> list = meteringLpDao.getMeteringDataDetailLpData(conditionMap);

        if (list == null || list.size() <= 0) {
            resultMap.put("searchData", searchData);
            resultMap.put("searchAddData", searchAddData);
            return resultMap;
        }

        Map<String, Object> map = null;
        Map<String, Double> listMap = new HashMap<String, Double>();
        Set<String> dateSet = new LinkedHashSet<String>();
        Supplier supplier = supplierDao.get(supplierId);
        String country = supplier.getCountry().getCode_2letter();
        String lang    = supplier.getLang().getCode_2letter();
        DecimalFormat mdf = DecimalUtil.getDecimalFormat(supplier.getMd());
        Meter meter = null;
        Integer lpInterval = 0;

        BigDecimal bdTotalSumValue = null;
        BigDecimal bdTotalMaxValue = null;
        BigDecimal bdTotalMinValue = null;
        Integer intTotalCount = new Integer("0");
        BigDecimal bdValue = null;
        Double value = null;
        Double maxValue = null;
        Double minValue = null;
        Double avgValue = null;
        Double sumValue = null;

        if (meterNo != null) {
            meter = mtrDao.get(meterNo);
            lpInterval = (meter.getLpInterval() == null) ? 60 : meter.getLpInterval();
        } else {
            lpInterval = 60;
        }

        int cnt = 0;
        String tmpLocaleDate = null;
        String tmpLocaleDateHour = searchStartDate + searchStartHour;

        // 조회조건 내 모든 일자 가져오기
        for (int k = 0; k < 100; k++) {     // 무한 loop 방지
            for (int i = 0, j = 0 ; j < 60 ; i++, j = i * lpInterval) {
                dateSet.add(tmpLocaleDateHour + CalendarUtil.to2Digit(j));
            }

            if (tmpLocaleDateHour.compareTo(searchEndDate + searchEndHour) >= 0) {  // 종료일자이면 종료
                break;
            } else {
                try {
                    tmpLocaleDateHour = DateTimeUtil.getPreHour(tmpLocaleDateHour + "0000", -1).substring(0, 10);   // +1 시간 더함
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }

        BigDecimal bdCurVal = null;
        BigDecimal bdPrevVal = null;
        Double dbPrevVal = null;

        for (Map<String, Object> obj : list) {
            for (int i = 0, j = 0 ; j < 60 ; i++, j = i * lpInterval) {
                tmpLocaleDate = CalendarUtil.to2Digit(j);
                value = DecimalUtil.ConvertNumberToDouble(obj.get("value_" + tmpLocaleDate));

                if (listMap.containsKey((String)obj.get("yyyymmddhh") + tmpLocaleDate + "_" + obj.get("channel"))) {
                    dbPrevVal = listMap.get((String)obj.get("yyyymmddhh") + tmpLocaleDate + "_" + obj.get("channel"));

                    if (dbPrevVal != null) {

                        if (value != null) {
                            bdCurVal = new BigDecimal(value.toString());
                            bdPrevVal = new BigDecimal(dbPrevVal.toString());
                            value = bdCurVal.add(bdPrevVal).doubleValue();
                        } else {
                            value = dbPrevVal;
                        }
                    }
                }
                listMap.put((String)obj.get("yyyymmddhh") + tmpLocaleDate + "_" + obj.get("channel"), value);
                
                if("1".equals(obj.get("channel").toString()) || "2".equals(obj.get("channel").toString())) {
                    tmpLocaleDate = CalendarUtil.to2Digit(j);
                    value = DecimalUtil.ConvertNumberToDouble(obj.get("value"));

                    if (listMap.containsKey((String)obj.get("yyyymmddhh") + tmpLocaleDate + "_mv" + obj.get("channel"))) {
                        dbPrevVal = listMap.get((String)obj.get("yyyymmddhh") + tmpLocaleDate + "_mv" + obj.get("channel"));

                        if (dbPrevVal != null) {

                            if (value != null) {
                                bdCurVal = new BigDecimal(value.toString());
                                bdPrevVal = new BigDecimal(dbPrevVal.toString());
                                value = bdCurVal.add(bdPrevVal).doubleValue();
                            } else {
                                value = dbPrevVal;
                            }
                        }
                    }
                    listMap.put((String)obj.get("yyyymmddhh") + tmpLocaleDate + "_mv" + obj.get("channel"), value);
                }
            }
        }

        for (String lpDate : dateSet) {
            for (String ch : channelIdStrList) {
                map = new HashMap<String, Object>();
                value = listMap.get(lpDate + "_" + ch);
                bdValue = value == null ? null : new BigDecimal(value.toString());

                map.put("reportDate", TimeLocaleUtil.getLocaleDate(lpDate + "00", lang, country));
                map.put("localeDate", TimeLocaleUtil.getLocaleDate(lpDate + "00", lang, country));
                map.put("channel", ch);
                map.put("value", (value == null) ? 0D : value);
                map.put("decimalValue", (value == null) ? mdf.format(0D) : mdf.format(value));
                searchData.add(map);

                if (cnt == 0) {
                    bdTotalSumValue = (bdValue != null) ? bdValue : null;
                    bdTotalMaxValue = (bdValue != null) ? bdValue : null;
                    bdTotalMinValue = (bdValue != null) ? bdValue : null;
                    intTotalCount = (bdValue != null) ? 1 : 0;
                } else {
                    if (bdValue != null) {
                        bdTotalSumValue = (bdTotalSumValue == null) ? bdValue : bdTotalSumValue.add(bdValue);
                        bdTotalMaxValue = (bdTotalMaxValue == null) ? bdValue : bdTotalMaxValue.max(bdValue);
                        bdTotalMinValue = (bdTotalMinValue == null) ? bdValue : bdTotalMinValue.min(bdValue);
                        intTotalCount = intTotalCount + 1;
                    }
                }
                cnt++;
            }
        }

        resultMap.put("searchData", searchData);

        if (intTotalCount > 0) {
            map = new HashMap<String, Object>();

            sumValue = (bdTotalSumValue == null) ? 0D : bdTotalSumValue.doubleValue();
            avgValue = (bdTotalSumValue == null) ? 0D : bdTotalSumValue.divide(new BigDecimal(intTotalCount.toString()), MathContext.DECIMAL32).doubleValue();
            maxValue = (bdTotalMaxValue == null) ? 0D : bdTotalMaxValue.doubleValue();
            minValue = (bdTotalMinValue == null) ? 0D : bdTotalMinValue.doubleValue();

            map.put("sumValue", sumValue);
            map.put("avgValue", avgValue);
            map.put("maxValue", maxValue);
            map.put("minValue", minValue);

            map.put("sumDecimalValue", mdf.format(sumValue));
            map.put("avgDecimalValue", mdf.format(avgValue));
            map.put("maxDecimalValue", mdf.format(maxValue));
            map.put("minDecimalValue", mdf.format(minValue));

            searchAddData.add(map);
        }
        resultMap.put("searchAddData", searchAddData);

        return resultMap;
    }

    /**
     * method name : getMeteringValueDetailHourlyChartData<b/>
     * method Desc : Metering Data 맥스가젯 상세화면에서 시간별 Chart 데이터를 조회한다.
     *
     * @param conditionMap
     * @return0
     */
    public Map<String, Object> getMeteringValueDetailHourlyChartData(Map<String, Object> conditionMap) {
        Map<String, Object> result = new HashMap<String, Object>();
        List<Map<String,Object>> searchData = new ArrayList<Map<String, Object>>();
        List<Map<String,Object>> searchAddData = new ArrayList<Map<String, Object>>();
        String searchStartDate = (String)conditionMap.get("searchStartDate");
        Integer supplierId = (Integer)conditionMap.get("supplierId");
        String[] channelArray = ((String)conditionMap.get("channel")).split(",");
        List<Integer> channelIdList = new ArrayList<Integer>();
        List<String> channelIdStrList = new ArrayList<String>();

        for (String obj : channelArray) {
    		if(!channelIdList.contains(Integer.parseInt(obj.replaceAll("mv", "")))) {
    			channelIdList.add(Integer.parseInt(obj.replaceAll("mv", "")));
    		}
        	channelIdStrList.add(obj);
        }

        conditionMap.put("channelIdList", channelIdList);
        Meter meter = mtrDao.get((String)conditionMap.get("meterNo"));
        Integer lpInterval = (meter.getLpInterval() != null) ? meter.getLpInterval() : 60;

        // 검침데이터 리스트 추출
        List<Map<String, Object>> list = meteringLpDao.getMeteringDataDetailHourlyData(conditionMap, false);

        if (list == null || list.size() <= 0) {
            result.put("searchData", searchData);
            result.put("searchAddData", searchAddData);
            return result;
        }

        Map<String, Object> listMap = new HashMap<String, Object>();
        Map<String, Object> tmpMap = null;
        Double tmpValue = null;
        Double tmpMvValue = null;

        Supplier supplier = supplierDao.get(supplierId);
        DecimalFormat df = DecimalUtil.getDecimalFormat(supplier.getMd());
        String lang = supplier.getLang().getCode_2letter();
        String country = supplier.getCountry().getCode_2letter();

        BigDecimal bdTmpValue = null;
        BigDecimal bdTmpMvValue = null;
        BigDecimal bdAddValue = null;
        int avgCnt = 0;

        for (Map<String, Object> obj : list) {
            bdTmpValue = null;
            avgCnt = 0;
            for (int i = 0, j = 0 ; j < 60 ; i++, j = i * lpInterval) {
                tmpValue = DecimalUtil.ConvertNumberToDouble(obj.get("VALUE_" + CalendarUtil.to2Digit(j)));

                if (tmpValue != null) {
                    if (bdTmpValue != null) {
                        if (ChannelCalcMethod.MAX.name().equals((String)obj.get("CH_METHOD"))) {
                            bdTmpValue = bdTmpValue.max(new BigDecimal(tmpValue));
                        } else {    // SUM, AVG
                            bdTmpValue = bdTmpValue.add(new BigDecimal(tmpValue.toString()));
                        }
                    } else {
                        bdTmpValue = new BigDecimal(tmpValue.toString());
                    }
                    avgCnt++;
                }
                
                if("1".equals(obj.get("CHANNEL").toString()) || "2".equals(obj.get("CHANNEL").toString())) {
                	tmpMvValue = DecimalUtil.ConvertNumberToDouble(obj.get("VALUE"));
                	
                    if (tmpMvValue != null) {
                        if (bdTmpMvValue != null) {
                        	bdTmpMvValue = bdTmpMvValue.max(new BigDecimal(tmpMvValue));
                        } else {
                        	bdTmpMvValue = new BigDecimal(tmpMvValue.toString());
                        }
                    }
                }
            }

            if (bdTmpValue != null && ChannelCalcMethod.AVG.name().equals((String)obj.get("CH_METHOD"))) {
                if (avgCnt > 0) {
                    bdTmpValue = bdTmpValue.divide(new BigDecimal(avgCnt), MathContext.DECIMAL32);
                } else {
                    bdTmpValue = new BigDecimal("0");
                }
            }

            if (listMap.containsKey((String)obj.get("YYYYMMDDHH") + "_" + obj.get("CHANNEL"))) {
                bdAddValue = (BigDecimal)listMap.get((String)obj.get("YYYYMMDDHH") + "_" + obj.get("CHANNEL"));
                bdTmpValue = bdTmpValue.add(bdAddValue);
            }

            listMap.put((String)obj.get("YYYYMMDDHH") + "_" + obj.get("CHANNEL"), bdTmpValue);
            
            if("1".equals(obj.get("CHANNEL").toString()) || "2".equals(obj.get("CHANNEL").toString())) {
            	if(obj.get("VALUE") != null) {
            		listMap.put((String)obj.get("YYYYMMDDHH") + "_mv" + obj.get("CHANNEL"), new BigDecimal(DecimalUtil.ConvertNumberToDouble(obj.get("VALUE"))));
            	}
            }
        }

        List<BigDecimal> bdTotalSumList = new ArrayList<BigDecimal>();
        List<BigDecimal> bdTotalMaxList = new ArrayList<BigDecimal>();
        List<BigDecimal> bdTotalMinList = new ArrayList<BigDecimal>();
        List<Integer> intTotalCount = new ArrayList<Integer>();

        int chIndex = 0;
        bdTmpValue = null;

        for (int i = 0; i < 24; i++) {
            chIndex = 0;
            for (String ch : channelIdStrList) {
                tmpMap = new HashMap<String, Object>();

                tmpMap.put("date", i+"");
                tmpMap.put("localeDate", i+"");
                tmpMap.put("channel", ch);

                bdTmpValue = (BigDecimal)listMap.get(searchStartDate + CalendarUtil.to2Digit(i) + "_" + ch.toString());

                tmpMap.put("value", (bdTmpValue == null) ? 0D : bdTmpValue.doubleValue());
                tmpMap.put("decimalValue", (bdTmpValue == null) ? df.format(0D) : df.format(bdTmpValue));
                tmpMap.put("reportDate", TimeLocaleUtil.getLocaleDateHour(searchStartDate + CalendarUtil.to2Digit(i), lang, country));

                if (i == 0) {
                    if (bdTmpValue != null) {
                        bdTotalSumList.add(bdTmpValue);
                        bdTotalMaxList.add(bdTmpValue);
                        bdTotalMinList.add(bdTmpValue);
                        intTotalCount.add(1);
                    } else {
                        bdTotalSumList.add(null);
                        bdTotalMaxList.add(null);
                        bdTotalMinList.add(null);
                        intTotalCount.add(0);
                    }
                } else {
                    if (bdTmpValue != null) {
                        if (bdTotalSumList.get(chIndex) == null) {
                            bdTotalSumList.set(chIndex, bdTmpValue);
                        } else {
                            bdTotalSumList.set(chIndex, bdTotalSumList.get(chIndex).add(bdTmpValue));
                        }

                        if (bdTotalMaxList.get(chIndex) == null) {
                            bdTotalMaxList.set(chIndex, bdTmpValue);
                        } else {
                            bdTotalMaxList.set(chIndex, bdTotalMaxList.get(chIndex).max(bdTmpValue));
                        }

                        if (bdTotalMinList.get(chIndex) == null) {
                            bdTotalMinList.set(chIndex, bdTmpValue);
                        } else {
                            bdTotalMinList.set(chIndex, bdTotalMinList.get(chIndex).min(bdTmpValue));
                        }
                        intTotalCount.set(chIndex, intTotalCount.get(chIndex) + 1);
                    }
                }

                searchData.add(tmpMap);
                chIndex++;
            }
        }

        Double maxValue = null;
        Double minValue = null;
        Double avgValue = null;
        Double sumValue = null;

        if (bdTotalSumList != null && bdTotalSumList.size() > 0) {
            chIndex = 0;
            for (Integer ch : channelIdList) {
                tmpMap = new HashMap<String, Object>();

                maxValue = (bdTotalMaxList.get(chIndex) == null) ? 0D : bdTotalMaxList.get(chIndex).doubleValue();
                minValue = (bdTotalMinList.get(chIndex) == null) ? 0D : bdTotalMinList.get(chIndex).doubleValue();

                if (bdTotalSumList.get(chIndex) == null || intTotalCount.get(chIndex) == 0) {
                    avgValue = 0D;
                } else {
                    avgValue = bdTotalSumList.get(chIndex).divide(new BigDecimal(intTotalCount.get(chIndex)), MathContext.DECIMAL32).doubleValue();
                }

                sumValue = (bdTotalSumList.get(chIndex) == null) ? 0D : bdTotalSumList.get(chIndex).doubleValue();

                tmpMap.put("channel", ch);
                tmpMap.put("minValue", minValue);
                tmpMap.put("maxValue", maxValue);
                tmpMap.put("avgValue", avgValue);
                tmpMap.put("sumValue", sumValue);
                tmpMap.put("minDecimalValue", df.format(minValue));
                tmpMap.put("maxDecimalValue", df.format(maxValue));
                tmpMap.put("avgDecimalValue", df.format(avgValue));
                tmpMap.put("sumDecimalValue", df.format(sumValue));

                searchAddData.add(tmpMap);
                chIndex++;
            }
        }

        result.put("searchData", searchData);
        result.put("searchAddData", searchAddData);

        return result;
    }
    
    /**
     * method name : getMeteringValueDetailDailyChartData<b/>
     * method Desc : Metering Data 맥스가젯 상세화면에서 일별 Chart 데이터를 조회한다.
     *
     * @param conditionMap
     * @return
     */
    public Map<String, Object> getMeteringValueDetailDailyChartData(Map<String, Object> conditionMap) {
        Map<String, Object> resultMap = new HashMap<String, Object>();
        List<Map<String, Object>> searchData = new ArrayList<Map<String, Object>>();
        List<Map<String, Object>> searchAddData = new ArrayList<Map<String, Object>>();
        Integer supplierId = (Integer)conditionMap.get("supplierId");
        String searchStartDate = (String)conditionMap.get("searchStartDate");
        String searchEndDate = (String)conditionMap.get("searchEndDate");
        String[] channelArray = ((String)conditionMap.get("channel")).split(",");
        List<Integer> channelIdList = new ArrayList<Integer>();
        List<String> channelIdStrList = new ArrayList<String>();
        
        for (String obj : channelArray) {
    		if(!channelIdList.contains(Integer.parseInt(obj.replaceAll("mv", "")))) {
    			channelIdList.add(Integer.parseInt(obj.replaceAll("mv", "")));
    		}
        	channelIdStrList.add(obj);
        }

        conditionMap.put("channelIdList", channelIdList);

        List<Map<String, Object>> list = meteringDayDao.getMeteringDataDetailDailyData(conditionMap, false);

        if (list == null || list.size() <= 0) {
            resultMap.put("searchData", searchData);
            resultMap.put("searchAddData", searchAddData);
            return resultMap;
        }

        Map<String, Double> listMap = new HashMap<String, Double>();
        Set<String> dateSet = new LinkedHashSet<String>();
        Map<String, Object> map = null;
        Supplier supplier = supplierDao.get(supplierId);
        String country = supplier.getCountry().getCode_2letter();
        String lang    = supplier.getLang().getCode_2letter();
        DecimalFormat mdf = DecimalUtil.getDecimalFormat(supplier.getMd());
        Double tmpValue = null;
        Double sumValue = null;
        Double avgValue = null;
        Double maxValue = null;
        Double minValue = null;

        int duration = 0;

        try {
            duration = TimeUtil.getDayDuration(searchStartDate, searchEndDate);
        } catch (ParseException e1) {
            e1.printStackTrace();
        }

        String tmpDate = searchStartDate;
        for (int i = 0; i < (duration + 1); i++) {
            dateSet.add(tmpDate);
            try {
                tmpDate = TimeUtil.getPreDay(tmpDate + "000000", -1).substring(0, 8);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        for (Map<String, Object> obj : list) {
        	listMap.put((String)obj.get("YYYYMMDD") + "_" + obj.get("CHANNEL"), DecimalUtil.ConvertNumberToDouble(obj.get("VALUE")));

            if("1".equals(obj.get("CHANNEL").toString()) || "2".equals(obj.get("CHANNEL").toString())) {
            	listMap.put((String)obj.get("YYYYMMDD") + "_mv" + obj.get("CHANNEL"), DecimalUtil.ConvertNumberToDouble(obj.get("BASEVALUE")));
            }
        }

        for (String date : dateSet) {
            for (String ch : channelIdStrList) {
                map = new HashMap<String, Object>();
                map.put("reportDate", TimeLocaleUtil.getLocaleDate(date, lang, country));
                // map.put("localeDate", Integer.valueOf(date.substring(6, 8)));
                map.put("localeDate", TimeLocaleUtil.getLocaleDate(date, lang, country));
                map.put("channel", ch);
                tmpValue = listMap.get(date + "_" + ch);
                map.put("value", (tmpValue == null) ? 0D : tmpValue.doubleValue());
                map.put("decimalValue", (tmpValue == null) ? mdf.format(0D) : mdf.format(tmpValue.doubleValue()));
                searchData.add(map);
            }
        }

        resultMap.put("searchData", searchData);

        List<Map<String, Object>> sumList = meteringDayDao.getMeteringDataDetailDailyData(conditionMap, true);

        if (sumList != null && sumList.size() > 0) {
            for (Map<String, Object> obj : sumList) {
                if (DecimalUtil.ConvertNumberToInteger(obj.get("CHANNEL")).equals(1)) {
                    map = new HashMap<String, Object>();

                    sumValue = DecimalUtil.ConvertNumberToDouble(obj.get("SUM_VAL"));
                    avgValue = DecimalUtil.ConvertNumberToDouble(obj.get("AVG_VAL"));
                    maxValue = DecimalUtil.ConvertNumberToDouble(obj.get("MAX_VAL"));
                    minValue = DecimalUtil.ConvertNumberToDouble(obj.get("MIN_VAL"));

                    map.put("sumValue", (sumValue == null) ? 0D : sumValue);
                    map.put("avgValue", (avgValue == null) ? 0D : avgValue);
                    map.put("maxValue", (maxValue == null) ? 0D : maxValue);
                    map.put("minValue", (minValue == null) ? 0D : minValue);

                    map.put("sumDecimalValue", (sumValue == null) ? mdf.format(0D) : mdf.format(sumValue));
                    map.put("avgDecimalValue", (avgValue == null) ? mdf.format(0D) : mdf.format(avgValue));
                    map.put("maxDecimalValue", (maxValue == null) ? mdf.format(0D) : mdf.format(maxValue));
                    map.put("minDecimalValue", (minValue == null) ? mdf.format(0D) : mdf.format(minValue));

                    searchAddData.add(map);
                    break;
                }
            }
        }

        resultMap.put("searchAddData", searchAddData);

        return resultMap;
    }
    
    /**
     * method name : getMeteringValueDetailWeeklyChartData<b/>
     * method Desc : Metering Data 맥스가젯 상세화면에서 주별 검침 chart 데이터를 조회한다.
     *
     * @param conditionMap
     * @return
     */
    public Map<String, Object> getMeteringValueDetailWeeklyChartData(Map<String, Object> conditionMap) {
        Map<String, Object> resultMap = new HashMap<String, Object>();
        List<Map<String, Object>> searchData = new ArrayList<Map<String, Object>>();
        List<Map<String, Object>> searchAddData = new ArrayList<Map<String, Object>>();
        Integer supplierId = (Integer)conditionMap.get("supplierId");
        String[] channelArray = ((String)conditionMap.get("channel")).split(",");
        List<Integer> channelIdList = new ArrayList<Integer>();
        List<String> channelIdStrList = new ArrayList<String>();
        
        for (String obj : channelArray) {
    		if(!channelIdList.contains(Integer.parseInt(obj.replaceAll("mv", "")))) {
    			channelIdList.add(Integer.parseInt(obj.replaceAll("mv", "")));
    		}
            channelIdStrList.add(obj);
        }

        conditionMap.put("channelIdList", channelIdList);

        List<Map<String, Object>> list = meteringDayDao.getMeteringDataDetailDailyData(conditionMap, false);

        if (list == null || list.size() <= 0) {
            resultMap.put("searchData", searchData);
            resultMap.put("searchAddData", searchAddData);
            return resultMap;
        }

        Set<String> dateSet = new LinkedHashSet<String>();
        Map<String, Object> listMap = new HashMap<String, Object>();
        Map<String, Object> chMethodMap = new HashMap<String, Object>();
        Map<String, Object> map = null;
        Supplier supplier = supplierDao.get(supplierId);
        String country = supplier.getCountry().getCode_2letter();
        String lang    = supplier.getLang().getCode_2letter();
        DecimalFormat mdf = DecimalUtil.getDecimalFormat(supplier.getMd());
        Double value = null;    // 계산용 임시변수

        for (Map<String, Object> obj : list) {
            listMap.put((String)obj.get("YYYYMMDD") + "_" + (Number)obj.get("CHANNEL"), obj.get("VALUE"));
            dateSet.add((String)obj.get("YYYYMMDD"));
            chMethodMap.put(obj.get("CHANNEL").toString(), obj.get("CH_METHOD"));
            
            if("1".equals(obj.get("CHANNEL").toString()) || "2".equals(obj.get("CHANNEL").toString())) {
                listMap.put((String)obj.get("YYYYMMDD") + "_mv" + (Number)obj.get("CHANNEL"), obj.get("BASEVALUE"));
                dateSet.add((String)obj.get("YYYYMMDD"));
                chMethodMap.put(obj.get("CHANNEL").toString(), obj.get("CH_METHOD"));
            }
        }

        List<Map<String, String>> weeksList = new ArrayList<Map<String, String>>();
        String searchStartDate = (String)conditionMap.get("searchStartDate");
        String searchEndDate = (String)conditionMap.get("searchEndDate");
        
        String startMonth = searchStartDate.substring(0, 6);
        String endMonth = searchEndDate.substring(0, 6);
        String curMonth = null;
        Integer startWeek = CalendarUtil.getWeekOfMonth(searchStartDate);
        Integer endWeek = CalendarUtil.getWeekOfMonth(searchEndDate);
        int firstWeek = 1;
        int lastWeek = 0;

        if (startMonth.equals(endMonth)) {      // 연월이 동일한 경우
            curMonth = startMonth;

            // 해당월의 각 주에 해당하는 시작일자 종료일자를 구한다.
            for (int i = startWeek; i <= endWeek; i++) {
                weeksList.add(CalendarUtil.getDateWeekOfMonth(curMonth.substring(0, 4), curMonth.substring(4, 6), i + ""));
            }
        } else {        // 연월이 다를 경우
            for (int i = 0; i < 100; i++) {     // 무한 loop 를 피하기 위해 for 문으로 loop 회수 제한
                if (i == 0) {
                    curMonth = startMonth;
                    firstWeek = startWeek;
                } else {
                    curMonth = (CalendarUtil.getDate(curMonth + "01", Calendar.MONTH, 1)).substring(0, 6);
                    firstWeek = 1;
                }

                if (curMonth.equals(endMonth)) {
                    lastWeek = endWeek;
                } else {
                    lastWeek = Integer.parseInt(CalendarUtil.getWeekCountOfMonth(curMonth.substring(0, 4), curMonth.substring(4)));
                }

                for (int j = firstWeek; j <= lastWeek; j++) {
                    weeksList.add(CalendarUtil.getDateWeekOfMonth(curMonth.substring(0, 4), curMonth.substring(4), j + ""));
                }

                if (curMonth.equals(endMonth)) {
                    break;
                }
            }
        }

        int len = weeksList.size();
        Map<String, String> weekDatesMap = new HashMap<String, String>();
        String startDate = null;
        String endDate = null;
        String curDate = null;

        BigDecimal bdTotalSum = new BigDecimal("0");
        BigDecimal bdTotalMax = new BigDecimal("0");
        BigDecimal bdTotalMin = new BigDecimal("0");
        Integer intTotalCount = new Integer("0");
        BigDecimal bdWeekValue = null;
        int cnt = 0;

        for (int k = 0; k < len; k++) {
            map = new HashMap<String, Object>();
            weekDatesMap = new HashMap<String, String>();
            startDate = null;
            endDate = null;
            curDate = null;
            weekDatesMap = weeksList.get(k);
            startDate = weekDatesMap.get("startDate");
            endDate = weekDatesMap.get("endDate");
            curDate = startDate;
            bdWeekValue = null;
            String chMethod = null;
            cnt = 0;

            for (String obj : channelIdStrList) {
                cnt = 0;
                curDate = startDate;
                chMethod = null;
                bdWeekValue = null;

                for (int l = 0; l < 7; l++) {   // 일주일 데이터를 sum
                    value = DecimalUtil.ConvertNumberToDouble(listMap.get(curDate + "_" + obj));

                    if (l == 0) {
                        if (value != null) {
                            bdWeekValue = new BigDecimal(value.toString());
                            cnt++;
                        } else {
                            bdWeekValue = null;
                        }
                    } else {
                        if (value != null) {
                            if (bdWeekValue == null) {
                                bdWeekValue = new BigDecimal(value.toString());
                            } else {
                            	if(obj.contains("mv")) {
                            		bdWeekValue = bdWeekValue.max(new BigDecimal(value.toString()));
                            	} else {
                            		bdWeekValue = bdWeekValue.add(new BigDecimal(value.toString()));
                            	}
                            }
                            cnt++;
                        }
                    }

                    if (curDate.equals(endDate)) {
                        break;
                    } else {
                        curDate = CalendarUtil.getDate(curDate, Calendar.DAY_OF_MONTH, 1);
                    }
                }

                if (cnt > 0) {
                    if (bdWeekValue != null) {
                        chMethod = (String)chMethodMap.get(obj.toString());

                        if (ChannelCalcMethod.AVG.name().equals(chMethod)) {
                            bdWeekValue = bdWeekValue.divide(new BigDecimal(cnt+""), MathContext.DECIMAL32);
                        }
                    } else {
                        bdWeekValue = new BigDecimal("0");
                    }
                    
                    // 에너지사용량(channel=1)의 Sum/Avg/Max/Min 을 계산한다.
                    if (obj.equals(1)) {
                        bdTotalSum = bdTotalSum.add(bdWeekValue);
                        bdTotalMax = bdTotalMax.max(bdWeekValue);
                        bdTotalMin = bdTotalMin.min(bdWeekValue);
                        intTotalCount++;
                    }

                    map = new HashMap<String, Object>();
                    // ex) 2012.08 1 Week
                    map.put("reportDate", TimeLocaleUtil.getLocaleYearMonth(startDate.substring(0, 6), lang, country) + " " + CalendarUtil.getWeekOfMonth(startDate) + "Week");
                    map.put("localeDate", TimeLocaleUtil.getLocaleYearMonth(startDate.substring(0, 6), lang, country) + " " + CalendarUtil.getWeekOfMonth(startDate) + "Week");
                    map.put("channel", obj);
                    map.put("value", bdWeekValue.doubleValue());
                    map.put("decimalValue", mdf.format(bdWeekValue.doubleValue()));
                    searchData.add(map);
                } else {
                    map = new HashMap<String, Object>();
                    // ex) 2012.08 1 Week
                    map.put("reportDate", TimeLocaleUtil.getLocaleYearMonth(startDate.substring(0, 6), lang, country) + " " + CalendarUtil.getWeekOfMonth(startDate) + "Week");
                    map.put("localeDate", TimeLocaleUtil.getLocaleYearMonth(startDate.substring(0, 6), lang, country) + " " + CalendarUtil.getWeekOfMonth(startDate) + "Week");
                    map.put("channel", obj);
                    map.put("value", 0D);
                    map.put("decimalValue", mdf.format(0D));
                    searchData.add(map);
                }
            }
        }

        resultMap.put("searchData", searchData);

        if (intTotalCount > 0) {
            map = new HashMap<String, Object>();

            map.put("sumValue", bdTotalSum.doubleValue());
            map.put("avgValue", bdTotalSum.divide(new BigDecimal(intTotalCount+""), MathContext.DECIMAL32).doubleValue());
            map.put("maxValue", bdTotalMax.doubleValue());
            map.put("minValue", bdTotalMin.doubleValue());

            map.put("sumDecimalValue", mdf.format(bdTotalSum.doubleValue()));
            map.put("avgDecimalValue", mdf.format(bdTotalSum.divide(new BigDecimal(intTotalCount+""), MathContext.DECIMAL32).doubleValue()));
            map.put("maxDecimalValue", mdf.format(bdTotalMax.doubleValue()));
            map.put("minDecimalValue", mdf.format(bdTotalMin.doubleValue()));

            searchAddData.add(map);
        }
        resultMap.put("searchAddData", searchAddData);

        return resultMap;
    }

    /**
     * method name : getMeteringValueDetailMonthlyChartData<b/>
     * method Desc : Metering Data 맥스가젯 상세화면에서 월별 Chart 데이터를 조회한다.
     *
     * @param conditionMap
     * @return
     */
    public Map<String, Object> getMeteringValueDetailMonthlyChartData(Map<String, Object> conditionMap) {
        Map<String, Object> resultMap = new HashMap<String, Object>();
        List<Map<String, Object>> searchData = new ArrayList<Map<String, Object>>();
        List<Map<String, Object>> searchAddData = new ArrayList<Map<String, Object>>();
        Integer supplierId = (Integer)conditionMap.get("supplierId");
        String searchStartDate = (String)conditionMap.get("searchStartDate");
        String[] channelArray = ((String)conditionMap.get("channel")).split(",");
        List<Integer> channelIdList = new ArrayList<Integer>();
        List<String> channelIdStrList = new ArrayList<String>();

        for (String obj : channelArray) {
    		if(!channelIdList.contains(Integer.parseInt(obj.replaceAll("mv", "")))) {
    			channelIdList.add(Integer.parseInt(obj.replaceAll("mv", "")));
    		}
        	channelIdStrList.add(obj);
        }

        conditionMap.put("channelIdList", channelIdList);
        List<Map<String, Object>> list = meteringMonthDao.getMeteringDataDetailMonthlyData(conditionMap, false);
        List<Map<String, Object>> mvList = meteringDayDao.getMeteringValueDetailMonthlyData(conditionMap, false);

        if (list == null || list.size() <= 0) {
            resultMap.put("searchData", searchData);
            resultMap.put("searchAddData", searchAddData);
            return resultMap;
        }

        Map<String, Double> listMap = new HashMap<String, Double>();
        Set<String> dateSet = new LinkedHashSet<String>();
        Map<String, Object> map = null;
        Supplier supplier = supplierDao.get(supplierId);
        String country = supplier.getCountry().getCode_2letter();
        String lang    = supplier.getLang().getCode_2letter();
        DecimalFormat mdf = DecimalUtil.getDecimalFormat(supplier.getMd());
        Double tmpValue = null;
        Double sumValue = null;
        Double avgValue = null;
        Double maxValue = null;
        Double minValue = null;
        String startYear = searchStartDate.substring(0, 4);

        for (int i = 1; i <= 12; i++) {
            dateSet.add(startYear + CalendarUtil.to2Digit(i));
        }

        for (Map<String, Object> obj : list) {
            listMap.put((String)obj.get("YYYYMM") + "_" + obj.get("CHANNEL"), DecimalUtil.ConvertNumberToDouble(obj.get("VALUE")));
            if("1".equals(obj.get("CHANNEL").toString()) || "2".equals(obj.get("CHANNEL").toString())) {
            	listMap.put((String)obj.get("YYYYMM") + "_mv" + obj.get("CHANNEL"), DecimalUtil.ConvertNumberToDouble(obj.get("BASEVALUE")));
            }
        }
        
        for (Map<String, Object> obj : mvList) {
            if("1".equals(obj.get("CHANNEL").toString()) || "2".equals(obj.get("CHANNEL").toString())) {
            	String yyyymm = obj.get("YYYYMMDD").toString().substring(0,6);
            	listMap.put(yyyymm + "_mv" + obj.get("CHANNEL"), DecimalUtil.ConvertNumberToDouble(obj.get("BASEVALUE")));
            }
        }
        
        for (String date : dateSet) {
            for (String ch : channelIdStrList) {
                map = new HashMap<String, Object>();
                map.put("reportDate", TimeLocaleUtil.getLocaleYearMonth(date, lang, country));
                map.put("localeDate", Integer.valueOf(date.substring(4, 6)));
                map.put("channel", ch);
                tmpValue = listMap.get(date + "_" + ch);
                map.put("value", (tmpValue == null) ? 0D : tmpValue.doubleValue());
                map.put("decimalValue", (tmpValue == null) ? mdf.format(0D) : mdf.format(tmpValue.doubleValue()));
                searchData.add(map);
            }
        }

        resultMap.put("searchData", searchData);

        List<Map<String, Object>> sumList = meteringMonthDao.getMeteringDataDetailMonthlyData(conditionMap, true);

        if (sumList != null && sumList.size() > 0) {
            for (Map<String, Object> obj : sumList) {
                if (DecimalUtil.ConvertNumberToInteger(obj.get("CHANNEL")).equals(1)) {
                    map = new HashMap<String, Object>();

                    sumValue = DecimalUtil.ConvertNumberToDouble(obj.get("SUM_VAL"));
                    avgValue = DecimalUtil.ConvertNumberToDouble(obj.get("AVG_VAL"));
                    maxValue = DecimalUtil.ConvertNumberToDouble(obj.get("MAX_VAL"));
                    minValue = DecimalUtil.ConvertNumberToDouble(obj.get("MIN_VAL"));

                    map.put("sumValue", (sumValue == null) ? 0D : sumValue);
                    map.put("avgValue", (avgValue == null) ? 0D : avgValue);
                    map.put("maxValue", (maxValue == null) ? 0D : maxValue);
                    map.put("minValue", (minValue == null) ? 0D : minValue);

                    map.put("sumDecimalValue", (sumValue == null) ? mdf.format(0D) : mdf.format(sumValue));
                    map.put("avgDecimalValue", (avgValue == null) ? mdf.format(0D) : mdf.format(avgValue));
                    map.put("maxDecimalValue", (maxValue == null) ? mdf.format(0D) : mdf.format(maxValue));
                    map.put("minDecimalValue", (minValue == null) ? mdf.format(0D) : mdf.format(minValue));

                    searchAddData.add(map);
                    break;
                }
            }
        }

        resultMap.put("searchAddData", searchAddData);

        return resultMap;        
    }
    
    /**
     * method name : getMeteringValueDetailWeekDailyChartData<b/>
     * method Desc : Metering Data 맥스가젯 상세화면에서 요일별 Chart 데이터를 조회한다.
     *
     * @param conditionMap
     * @return
     */
    public Map<String, Object> getMeteringValueDetailWeekDailyChartData(Map<String, Object> conditionMap) {
        Map<String, Object> resultMap = new HashMap<String, Object>();
        List<Map<String, Object>> searchData = new ArrayList<Map<String, Object>>();
        List<Map<String, Object>> searchAddData = new ArrayList<Map<String, Object>>();
        String searchStartDate = (String)conditionMap.get("searchStartDate");
        Integer supplierId = (Integer)conditionMap.get("supplierId");
        String[] channelArray = ((String)conditionMap.get("channel")).split(",");
        List<Integer> channelIdList = new ArrayList<Integer>();
        List<String> channelIdStrList = new ArrayList<String>();
        
        for (String obj : channelArray) {
    		if(!channelIdList.contains(Integer.parseInt(obj.replaceAll("mv", "")))) {
    			channelIdList.add(Integer.parseInt(obj.replaceAll("mv", "")));
    		}
        	channelIdStrList.add(obj);
        }

        conditionMap.put("channelIdList", channelIdList);

        List<Map<String, Object>> list = meteringDayDao.getMeteringDataDetailDailyData(conditionMap, false);
        
        if (list == null || list.size() <= 0) {
            resultMap.put("searchData", searchData);
            resultMap.put("searchAddData", searchAddData);
            return resultMap;
        }

        Map<String, Double> listMap = new HashMap<String, Double>();
        Map<String, Object> sumListMap = new HashMap<String, Object>();
        Set<String> dateSet = new LinkedHashSet<String>();
        Map<String, Object> map = null;
        Supplier supplier = supplierDao.get(supplierId);
        String country = supplier.getCountry().getCode_2letter();
        String lang    = supplier.getLang().getCode_2letter();
        DecimalFormat mdf = DecimalUtil.getDecimalFormat(supplier.getMd());
        Double value = null;
        Double maxValue = null;
        Double minValue = null;
        Double avgValue = null;
        Double sumValue = null;

        int year = 0;
        int month = 0;
        int day = 0;
        Calendar cal = null;

        year = Integer.parseInt(searchStartDate.substring(0, 4));
        month = Integer.parseInt(searchStartDate.substring(4, 6))-1;
        day = 1;
        cal = Calendar.getInstance();
        cal.set(year, month, day);
        String startMonth = searchStartDate.substring(0, 6);

        int lastDate = cal.getActualMaximum(Calendar.DAY_OF_MONTH);

        for (int i = 1; i <= lastDate; i++) {
            dateSet.add(startMonth + CalendarUtil.to2Digit(i));
        }

        for (Map<String, Object> obj : list) {
            if("1".equals(obj.get("CHANNEL").toString()) || "2".equals(obj.get("CHANNEL").toString())) {
            	listMap.put((String)obj.get("YYYYMMDD") + "_mv" + obj.get("CHANNEL"), DecimalUtil.ConvertNumberToDouble(obj.get("BASEVALUE")));
            } else {
            	listMap.put((String)obj.get("YYYYMMDD") + "_" + obj.get("CHANNEL"), DecimalUtil.ConvertNumberToDouble(obj.get("VALUE")));
            }
        }

        Iterator<String> itr = dateSet.iterator();
        String date = null;
        String localeDate = null;
        String localeDay = null;

        while(itr.hasNext()) {
            date = itr.next();

            for (String ch : channelIdStrList) {
                map = new HashMap<String, Object>();
                value = StringUtil.nullToDoubleZero(listMap.get(date + "_" + ch));

                localeDate = TimeLocaleUtil.getLocaleDate(date, lang, country);
                localeDate = TimeLocaleUtil.getLocaleDay(localeDate, 8, lang, country);
                localeDay = TimeLocaleUtil.getLocaleWeekDayOnly(date, lang, country);

                map.put("reportDate", localeDate + " " + localeDay);
                map.put("localeDate", localeDate + " " + localeDay);
                map.put("channel", ch);
                map.put("value", value);
                map.put("decimalValue", mdf.format(value));
                searchData.add(map);
            }
        }
        resultMap.put("searchData", searchData);

        List<Map<String, Object>> sumList = meteringDayDao.getMeteringDataDetailDailyData(conditionMap, true);

        if (sumList != null && sumList.size() > 0) {
            for (Map<String, Object> obj : sumList) {
                sumListMap.put("MAX_" + (Number)obj.get("CHANNEL"), obj.get("MAX_VAL"));
                sumListMap.put("MIN_" + (Number)obj.get("CHANNEL"), obj.get("MIN_VAL"));
                sumListMap.put("AVG_" + (Number)obj.get("CHANNEL"), obj.get("AVG_VAL"));
                sumListMap.put("SUM_" + (Number)obj.get("CHANNEL"), obj.get("SUM_VAL"));
                if("1".equals(obj.get("CHANNEL").toString()) || "2".equals(obj.get("CHANNEL").toString())) {
                	sumListMap.put("MAX_mv" + (Number)obj.get("CHANNEL"), obj.get("MAX_BASEVAL"));
                    sumListMap.put("MIN_mv" + (Number)obj.get("CHANNEL"), obj.get("MIN_BASEVAL"));
                }
            }

            map = new HashMap<String, Object>();

            // 에너지사용량(channel=1)의 Sum/Avg/Max/Min 을 가져온다.
            sumValue = DecimalUtil.ConvertNumberToDouble(sumListMap.get("SUM_1"));
            avgValue = DecimalUtil.ConvertNumberToDouble(sumListMap.get("AVG_1"));
            maxValue = DecimalUtil.ConvertNumberToDouble(sumListMap.get("MAX_1"));
            minValue = DecimalUtil.ConvertNumberToDouble(sumListMap.get("MIN_1"));

            map.put("sumValue", sumValue);
            map.put("avgValue", avgValue);
            map.put("maxValue", maxValue);
            map.put("minValue", minValue);

            map.put("sumDecimalValue", mdf.format(sumValue));
            map.put("avgDecimalValue", mdf.format(avgValue));
            map.put("maxDecimalValue", mdf.format(maxValue));
            map.put("minDecimalValue", mdf.format(minValue));

            searchAddData.add(map);
        }

        resultMap.put("searchAddData", searchAddData);

        return resultMap;
    }
    
    /**
     * method name : getMeteringValueDetailSeasonalChartData<b/>
     * method Desc : Metering Data 맥스가젯 상세화면에서 계절별 chart 데이터를 조회한다.
     *
     * @param conditionMap
     * @return
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> getMeteringValueDetailSeasonalChartData(Map<String, Object> conditionMap) {
        Map<String, Object> resultMap = new HashMap<String, Object>();
        List<Map<String, Object>> searchData = new ArrayList<Map<String, Object>>();
        List<Map<String, Object>> searchAddData = new ArrayList<Map<String, Object>>();

        List<Map<String, Object>> list = null;
        List<Map<String, Object>> mvList = null;
        Integer supplierId = (Integer)conditionMap.get("supplierId");
        String[] channelArray = ((String)conditionMap.get("channel")).split(",");
        List<Integer> channelIdList = new ArrayList<Integer>();
        List<String> channelIdStrList = new ArrayList<String>();

        for (String obj : channelArray) {
    		if(!channelIdList.contains(Integer.parseInt(obj.replaceAll("mv", "")))) {
    			channelIdList.add(Integer.parseInt(obj.replaceAll("mv", "")));
    		}
        	channelIdStrList.add(obj);
        }

        conditionMap.put("channelIdList", channelIdList);

        Set<String> dateSet = new LinkedHashSet<String>();
        Map<String, Object> listMap = new HashMap<String, Object>();
        Map<String, Object> chMethodMap = new HashMap<String, Object>();
        Map<String, Object> map = null;
        Supplier supplier = supplierDao.get(supplierId);
        DecimalFormat mdf = DecimalUtil.getDecimalFormat(supplier.getMd());
        Double value = null;    // 계산용 임시변수
        Map<String, Object> seasonsListMap = null;
        List<SeasonData> seasonDataList = null;
        String searchStartDate = (String)conditionMap.get("searchStartDate");
        String searchEndDate = (String)conditionMap.get("searchEndDate");
        boolean hasDay = false;
        int sumLen = 0;     // 계절별 sum 할때 loop 회수 제한

        seasonsListMap = seasonManager.getSeasonDataListByDates(searchStartDate, searchEndDate);
        hasDay = (Boolean)seasonsListMap.get("hasDay");
        seasonDataList = (List<SeasonData>)seasonsListMap.get("seasonDataList");

        if (hasDay) {
            list = meteringDayDao.getMeteringDataDetailDailyData(conditionMap, false);
//            mvList = meteringDayDao.getMeteringValueDetailDailyData(conditionMap, false);
            sumLen = 124;   // 일별 데이터로 sum 할때 loop 제한
        } else {
            list = meteringMonthDao.getMeteringDataDetailMonthlyData(conditionMap, false);
            mvList = meteringDayDao.getMeteringValueDetailMonthlyData(conditionMap, false);
            sumLen = 4;     // 월별 데이터로 sum 할때 loop 제한
        }

        if (list == null || list.size() <= 0) {
            return resultMap;
        }

        for (Map<String, Object> obj : list) {
            if (hasDay) {
                listMap.put((String)obj.get("YYYYMMDD") + "_" + (Number)obj.get("CHANNEL"), obj.get("VALUE"));
                dateSet.add((String)obj.get("YYYYMMDD"));
            } else {
                listMap.put((String)obj.get("YYYYMM") + "_" + (Number)obj.get("CHANNEL"), obj.get("VALUE"));
                dateSet.add((String)obj.get("YYYYMM"));
            }
            chMethodMap.put(obj.get("CHANNEL").toString(), obj.get("CH_METHOD"));
        }
        
        for (Map<String, Object> obj : mvList) {
            if("1".equals(obj.get("CHANNEL").toString()) || "2".equals(obj.get("CHANNEL").toString())) {
            	if (hasDay) {
                    listMap.put((String)obj.get("YYYYMMDD") + "_mv" + (Number)obj.get("CHANNEL"), obj.get("BASEVALUE"));
                    dateSet.add((String)obj.get("YYYYMMDD"));
                } else {
                	String yyyymm = obj.get("YYYYMMDD").toString().substring(0,6);
                    listMap.put(yyyymm + "_mv" + (Number)obj.get("CHANNEL"), obj.get("BASEVALUE"));
                    dateSet.add(yyyymm);
                }
                chMethodMap.put("mv"+obj.get("CHANNEL").toString(), obj.get("CH_METHOD"));
            }
        }

        int len = seasonDataList.size();
        String startDate = null;        // season 기간 시작일자. 월별일 경우 yyyyMM, 일별일 경우 yyyyMMdd
        String endDate = null;          // season 기간 종료일자. 월별일 경우 yyyyMM, 일별일 경우 yyyyMMdd
        String curDate = null;
        BigDecimal bdTotalSum = new BigDecimal("0");
        BigDecimal bdTotalMax = new BigDecimal("0");
        BigDecimal bdTotalMin = new BigDecimal("0");
        Integer intTotalCount = new Integer("0");
        BigDecimal bdSeasonValue = null;
        int cnt = 0;
        SeasonData seasonData = null;
        String chMethod = null;

        for (int k = 0; k < len; k++) {
            seasonData = seasonDataList.get(k);
            map = new HashMap<String, Object>();
            startDate = null;
            endDate = null;
            curDate = null;
            cnt = 0;

            if (hasDay) {
                startDate = seasonData.getStartDate();
                endDate = seasonData.getEndDate();
            } else {
                startDate = seasonData.getStartDate().substring(0, 6);
                endDate = seasonData.getEndDate().substring(0, 6);
            }

            for (String obj : channelIdStrList) { // 각 채널별 sum
                cnt = 0;
                curDate = startDate;
                chMethod = null;
                bdSeasonValue = null;

                for (int l = 0; l < sumLen; l++) {   // 계절 단위로 sum
                    value = DecimalUtil.ConvertNumberToDouble(listMap.get(curDate + "_" + obj));

                    if (l == 0) {
                        if (value != null) {
                            bdSeasonValue = new BigDecimal(value.toString());
                            cnt++;
                        } else {
                            bdSeasonValue = null;
                        }
                    } else {
                        if (value != null) {
                            if (bdSeasonValue == null) {
                                bdSeasonValue = new BigDecimal(value.toString());
                            } else {
                                bdSeasonValue = bdSeasonValue.add(new BigDecimal(value.toString()));
                            }
                            
                            cnt++;
                        }
                    }

                    if (curDate.equals(endDate)) {
                        break;
                    } else {
                        if (hasDay) {
                            curDate = CalendarUtil.getDate(curDate, Calendar.DAY_OF_MONTH, 1);
                        } else {
                            curDate = CalendarUtil.getDate(curDate + "01", Calendar.MONTH, 1).substring(0, 6);
                        }
                    }
                }   // for (int l = 0; l < sumLen; l++)

                if (cnt > 0) {
                    if (bdSeasonValue != null) {
                        chMethod = (String)chMethodMap.get(obj.toString());

                        if (chMethod != null && chMethod.equals(ChannelCalcMethod.AVG.name())) {
                            bdSeasonValue = bdSeasonValue.divide(new BigDecimal(cnt+""), MathContext.DECIMAL32);
                        }
                    } else {
                        bdSeasonValue = new BigDecimal("0");
                    }

                    // 에너지사용량(channel=1)의 Sum/Avg/Max/Min 을 계산한다.
                    if (obj.equals(1)) {
                        bdTotalSum = bdTotalSum.add(bdSeasonValue);
                        bdTotalMax = bdTotalMax.max(bdSeasonValue);
                        bdTotalMin = bdTotalMin.min(bdSeasonValue);
                        intTotalCount++;
                    }

                    map = new HashMap<String, Object>();
                    // ex) 2012 Spring
                    map.put("reportDate", startDate.substring(0, 4) + " " + seasonData.getName());
                    map.put("localeDate", startDate.substring(0, 4) + " " + seasonData.getName());
                    map.put("channel", obj);
                    map.put("value", bdSeasonValue.doubleValue());
                    map.put("decimalValue", mdf.format(bdSeasonValue.doubleValue()));
                    searchData.add(map);
                } else {
                    map = new HashMap<String, Object>();
                    // ex) 2012 Spring
                    map.put("reportDate", startDate.substring(0, 4) + " " + seasonData.getName());
                    map.put("localeDate", startDate.substring(0, 4) + " " + seasonData.getName());
                    map.put("channel", obj);
                    map.put("value", 0D);
                    map.put("decimalValue", mdf.format(0D));
                    searchData.add(map);
                }
            }
        }

        resultMap.put("searchData", searchData);

        if (intTotalCount > 0) {
            map = new HashMap<String, Object>();

            map.put("sumValue", bdTotalSum.doubleValue());
            map.put("avgValue", bdTotalSum.divide(new BigDecimal(intTotalCount.toString()), MathContext.DECIMAL32).doubleValue());
            map.put("maxValue", bdTotalMax.doubleValue());
            map.put("minValue", bdTotalMin.doubleValue());

            map.put("sumDecimalValue", mdf.format(bdTotalSum.doubleValue()));
            map.put("avgDecimalValue", mdf.format(bdTotalSum.divide(new BigDecimal(intTotalCount.toString()), MathContext.DECIMAL32).doubleValue()));
            map.put("maxDecimalValue", mdf.format(bdTotalMax.doubleValue()));
            map.put("minDecimalValue", mdf.format(bdTotalMin.doubleValue()));

            searchAddData.add(map);
        }
        resultMap.put("searchAddData", searchAddData);

        return resultMap;
    }
    
    /**
     * method name : getMeteringDataDetailRatelyData<b/>
     * method Desc : Metering Data 맥스가젯 상세화면에서 Rate 별 검침데이터를 조회한다.
     *
     *  # MeteringData Gadget - Detail - Rate tab (dayType,startTime,endTime)
	 *	# dayType : weekday(Monday ~ Friday)/weekend(Saturday ~ Sunday)
	 *	# startTime 이 endTime 보다 크면 사용량 시간범위는 0 ~ endTime, startTime ~ 23 가 된다.
     * @param conditionMap
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> getMeteringValueDetailRatelyData(Map<String, Object> conditionMap) {
        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
        String searchStartDate = (String)conditionMap.get("searchStartDate");
        String searchEndDate = (String)conditionMap.get("searchEndDate");
        Integer supplierId = (Integer)conditionMap.get("supplierId");
        String[] rate1Array = ((String)conditionMap.get("rate1")).split(",");   // rate1=weekday,9,21
        String[] rate2Array = ((String)conditionMap.get("rate2")).split(",");   // rate2=weekday,22,8
        String[] rate3Array = ((String)conditionMap.get("rate3")).split(",");   // rate3=weekend,0,23
        List<Map<String, Object>> list = meteringDayDao.getMeteringDataDetailRatelyData(conditionMap);
        List<Map<String, Object>> mvList = meteringLpDao.getMeteringDataDetailLpData(conditionMap);

        if (list == null || list.size() <= 0) {
            return result;
        }

        Map<String, Object> map = null;
        List<Map<String, Object>> objList = null;
        Map<String, Object> objMap = null;
        Map<String, Object> listMvMap = new HashMap<String, Object>();
        Map<String, Object> listMap = new HashMap<String, Object>();
        Set<String> dateSet = new LinkedHashSet<String>();
        Supplier supplier = supplierDao.get(supplierId);
        String country = supplier.getCountry().getCode_2letter();
        String lang    = supplier.getLang().getCode_2letter();
        DecimalFormat mdf = DecimalUtil.getDecimalFormat(supplier.getMd());
        Number nvalue = null;
        Number mvNvalue = null;
        Double avgValue = null;
        StringBuilder sbAvgValue = null;

        List<BigDecimal> bdTotalSumList = new ArrayList<BigDecimal>();
        List<BigDecimal> bdTotalMaxList = new ArrayList<BigDecimal>();
        List<BigDecimal> bdTotalMinList = new ArrayList<BigDecimal>();
        List<BigDecimal> bdMvTotalMaxList = new ArrayList<BigDecimal>();
        List<BigDecimal> bdMvTotalMinList = new ArrayList<BigDecimal>();
        List<Integer> intTotalCount = new ArrayList<Integer>();
        BigDecimal bdRateValue = null;
        BigDecimal bdMvRateValue = null;

        List<String> rateDateTypeList = new ArrayList<String>();
        List<Integer> rateStartTimeList = new ArrayList<Integer>();
        List<Integer> rateEndTimeList = new ArrayList<Integer>();

        rateDateTypeList.add(rate1Array[0]);
        rateDateTypeList.add(rate2Array[0]);
        rateDateTypeList.add(rate3Array[0]);
        int len = rateDateTypeList.size();

        if (rateDateTypeList.get(0).equals(UsageRateDateType.WEEK_END.getCode())) {
            rateStartTimeList.add(0);
            rateEndTimeList.add(23);
        } else {
            rateStartTimeList.add(new Integer(rate1Array[1]));
            rateEndTimeList.add(new Integer(rate1Array[2]));
        }

        if (rateDateTypeList.get(1).equals(UsageRateDateType.WEEK_END.getCode())) {
            rateStartTimeList.add(0);
            rateEndTimeList.add(23);
        } else {
            rateStartTimeList.add(new Integer(rate2Array[1]));
            rateEndTimeList.add(new Integer(rate2Array[2]));
        }

        if (rateDateTypeList.get(2).equals(UsageRateDateType.WEEK_END.getCode())) {
            rateStartTimeList.add(0);
            rateEndTimeList.add(23);
        } else {
            rateStartTimeList.add(new Integer(rate3Array[1]));
            rateEndTimeList.add(new Integer(rate3Array[2]));
        }

        int cnt = 0;
        int year = 0;
        int month = 0;
        int day = 0;
        Calendar cal = null;

        int duration = 0;

        try {
            duration = TimeUtil.getDayDuration(searchStartDate, searchEndDate);
        } catch (ParseException e1) {
            e1.printStackTrace();
        }


        String tmpDate = searchStartDate;

        for (int i = 0; i <= duration; i++) {
            dateSet.add(tmpDate);
            try {
                tmpDate = TimeUtil.getPreDay(tmpDate + "000000", -1).substring(0, 8);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        
        for (Map<String, Object> obj : list) {
            listMap.put((String)obj.get("YYYYMMDD"), obj);
        }

        for (Map<String, Object> obj : mvList) {
        	String yyyymmddhh = obj.get("yyyymmddhh").toString();
        	String yyyymmdd = yyyymmddhh.substring(0,8);
        	List<Map<String,Object>> tempList = (List<Map<String, Object>>) listMvMap.get(yyyymmdd);
        	if(tempList == null) {
        		tempList = new ArrayList<Map<String,Object>>();
        	}
        	Map<String,Object> tempMap = new HashMap<String,Object>();
        	tempMap.put(yyyymmddhh.substring(8,10), obj.get("value"));
        	tempList.add(tempMap);
        	
        	listMvMap.put(yyyymmdd, tempList);
        }

        for (String date : dateSet) {
            if (listMap.get(date) != null) {
                objList = (List<Map<String, Object>>) listMvMap.get(date);
                objMap = (Map<String, Object>)listMap.get(date);
                map = new HashMap<String, Object>();
                map.put("meteringTime", TimeLocaleUtil.getLocaleDate(date, lang, country));
                map.put("meteringTimeDis", TimeLocaleUtil.getLocaleDate(date, lang, country));
                
                year = Integer.parseInt(date.substring(0, 4));
                month = Integer.parseInt(date.substring(4, 6))-1;
                day = Integer.parseInt(date.substring(6, 8));
                cal = Calendar.getInstance();
                cal.set(year, month, day);

                for (int i = 0; i < len; i++) {
                    bdRateValue = new BigDecimal("0");
                    bdMvRateValue = new BigDecimal("0");

                    if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY || cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {   // 토,일요일
                        if (rateDateTypeList.get(i).equals(UsageRateDateType.WEEK_END.getCode())) {     // weekend 적용
                            if (rateStartTimeList.get(i) > rateEndTimeList.get(i)) {    // 시작시간 > 종료시간일 경우 : 0 ~ 종료시간, 시작시간 ~ 23
                                for (int j = 0; j < (rateEndTimeList.get(i) + 1); j++) {
                                	nvalue = (Number)objMap.get("VALUE_" + CalendarUtil.to2Digit(j));
                                    bdRateValue = bdRateValue.add(new BigDecimal(nvalue == null ? "0" : nvalue.toString()));
                                	
                                	Integer size = objList.size();
                                	for (int k = 0; k < size; k++) {
                                		Map<String,Object> tempMap = (Map<String, Object>) objList.get(k);
										if(j == k) {
											mvNvalue = (Number)tempMap.get(StringUtil.frontAppendNStr('0', new Integer(j).toString(), 2));
											bdMvRateValue = bdMvRateValue.max(new BigDecimal(mvNvalue == null ? "0" : mvNvalue.toString()));
										}
									}
                                }

                                for (int j = rateStartTimeList.get(i); j < 24; j++) {
                                	nvalue = (Number)objMap.get("VALUE_" + CalendarUtil.to2Digit(j));
                                    bdRateValue = bdRateValue.add(new BigDecimal(nvalue == null ? "0" : nvalue.toString()));
                                	
                                	Integer size = objList.size();
                                	for (int k = 0; k < size; k++) {
                                		Map<String,Object> tempMap = (Map<String, Object>) objList.get(k);
										if(j == k) {
											mvNvalue = (Number)tempMap.get(StringUtil.frontAppendNStr('0', new Integer(j).toString(), 2));
											bdMvRateValue = bdMvRateValue.max(new BigDecimal(mvNvalue == null ? "0" : mvNvalue.toString()));
										}
									}
                                }
                            } else {        // 시작시간 < 종료시간일 경우 : 시작시간 ~ 종료시간
                                for (int j = rateStartTimeList.get(i); j < (rateEndTimeList.get(i) + 1); j++) {
                                	nvalue = (Number)objMap.get("VALUE_" + CalendarUtil.to2Digit(j));
                                    bdRateValue = bdRateValue.add(new BigDecimal(nvalue == null ? "0" : nvalue.toString()));
                                	
                                	Integer size = objList.size();
                                	for (int k = 0; k < size; k++) {
                                		Map<String,Object> tempMap = (Map<String, Object>) objList.get(k);
										if(j == k) {
											mvNvalue = (Number)tempMap.get(StringUtil.frontAppendNStr('0', new Integer(j).toString(), 2));
											bdMvRateValue = bdMvRateValue.max(new BigDecimal(mvNvalue == null ? "0" : mvNvalue.toString()));
										}
									}
                                }
                            }
                        } else {
                            bdRateValue = null;
                            bdMvRateValue = null;
                        }
                    } else {    // 평일일 경우
                        if (rateDateTypeList.get(i).equals(UsageRateDateType.WEEK_END.getCode())) {
                            bdRateValue = null;
                            bdMvRateValue = null;
                        } else {    // weekend 이외 적용
                            if (rateStartTimeList.get(i) > rateEndTimeList.get(i)) {    // 시작시간 > 종료시간일 경우 : 0 ~ 종료시간, 시작시간 ~ 23
                                for (int j = 0; j < (rateEndTimeList.get(i) + 1); j++) {
                                	nvalue = (Number)objMap.get("VALUE_" + CalendarUtil.to2Digit(j));
                                    bdRateValue = bdRateValue.add(new BigDecimal(nvalue == null ? "0" : nvalue.toString()));
                                	
                                	Integer size = objList.size();
                                	for (int k = 0; k < size; k++) {
                                		Map<String,Object> tempMap = (Map<String, Object>) objList.get(k);
										if(j == k) {
											mvNvalue = (Number)tempMap.get(StringUtil.frontAppendNStr('0', new Integer(j).toString(), 2));
											bdMvRateValue = bdMvRateValue.max(new BigDecimal(mvNvalue == null ? "0" : mvNvalue.toString()));
										}
									}
                                }

                                for (int j = rateStartTimeList.get(i); j < 24; j++) {
                                	nvalue = (Number)objMap.get("VALUE_" + CalendarUtil.to2Digit(j));
                                    bdRateValue = bdRateValue.add(new BigDecimal(nvalue == null ? "0" : nvalue.toString()));
                                	
                                	Integer size = objList.size();
                                	for (int k = 0; k < size; k++) {
                                		Map<String,Object> tempMap = (Map<String, Object>) objList.get(k);
										if(j == k) {
											mvNvalue = (Number)tempMap.get(StringUtil.frontAppendNStr('0', new Integer(j).toString(), 2));
											bdMvRateValue = bdMvRateValue.max(new BigDecimal(mvNvalue == null ? "0" : mvNvalue.toString()));
										}
									}
                                }
                            } else {        // 시작시간 < 종료시간일 경우 : 시작시간 ~ 종료시간
                                for (int j = rateStartTimeList.get(i); j < (rateEndTimeList.get(i) + 1); j++) {
                                	nvalue = (Number)objMap.get("VALUE_" + CalendarUtil.to2Digit(j));
                                    bdRateValue = bdRateValue.add(new BigDecimal(nvalue == null ? "0" : nvalue.toString()));
                                	
                                	Integer size = objList.size();
                                	for (int k = 0; k < size; k++) {
										Map<String,Object> tempMap = (Map<String, Object>) objList.get(k);
										if(j == k) {
											mvNvalue = (Number)tempMap.get(StringUtil.frontAppendNStr('0', new Integer(j).toString(), 2));
											bdMvRateValue = bdMvRateValue.max(new BigDecimal(mvNvalue == null ? "0" : mvNvalue.toString()));
										}
									}
                                }
                            }
                        }
                    }

                    map.put("rate_" + (i+1), (bdRateValue == null) ? "- " : mdf.format(bdRateValue.doubleValue()));
                    map.put("rate_mv" + (i+1), (bdMvRateValue == null) ? "- " : mdf.format(bdMvRateValue.doubleValue()));

                    if (cnt == 0) {
                        if (bdRateValue != null) {
                            bdTotalSumList.add(bdRateValue);
                            bdTotalMaxList.add(bdRateValue);
                            bdTotalMinList.add(bdRateValue);
                            bdMvTotalMaxList.add(bdMvRateValue);
                            bdMvTotalMinList.add(bdMvRateValue);
                            intTotalCount.add(1);
                        } else {
                            bdTotalSumList.add(null);
                            bdTotalMaxList.add(null);
                            bdTotalMinList.add(null);
                            bdMvTotalMaxList.add(null);
                            bdMvTotalMinList.add(null);
                            intTotalCount.add(0);
                        }
                    } else {
                        if (bdRateValue != null) {
                            if (bdTotalSumList.get(i) != null) {
                                bdTotalSumList.set(i, bdTotalSumList.get(i).add(bdRateValue));
                            } else {
                                bdTotalSumList.set(i, bdRateValue);
                            }
                            if (bdTotalMaxList.get(i) != null) {
                                bdTotalMaxList.set(i, bdTotalMaxList.get(i).max(bdRateValue));
                            } else {
                                bdTotalMaxList.set(i, bdRateValue);
                            }
                            if (bdTotalMinList.get(i) != null) {
                                bdTotalMinList.set(i, bdTotalMinList.get(i).min(bdRateValue));
                            } else {
                                bdTotalMinList.set(i, bdRateValue);
                            }
                            
                            if (bdMvTotalMaxList.get(i) != null) {
                                bdMvTotalMaxList.set(i, bdMvTotalMaxList.get(i).max(bdMvRateValue));
                            } else {
                                bdMvTotalMaxList.set(i, bdMvRateValue);
                            }
                            if (bdMvTotalMinList.get(i) != null) {
                                bdMvTotalMinList.set(i, bdMvTotalMinList.get(i).min(bdMvRateValue));
                            } else {
                                bdMvTotalMinList.set(i, bdMvRateValue);
                            }
                            intTotalCount.set(i, intTotalCount.get(i) + 1);
                        }
                    }
                }
                cnt++;
                result.add(map);
            } else {
                map = new HashMap<String, Object>();
                map.put("meteringTime", TimeLocaleUtil.getLocaleDate(date, lang, country));
                map.put("meteringTimeDis", TimeLocaleUtil.getLocaleDate(date, lang, country));
                for (int i = 0; i < len; i++) {
                    map.put("rate_" + (i+1), "- ");
                    map.put("rate_mv" + (i+1), "- ");
                }
                result.add(map);
            }
        }

        if (bdTotalSumList != null && bdTotalSumList.size() > 0) {
            map = new HashMap<String, Object>();
            map.put("meteringTime", conditionMap.get("msgSum"));
            map.put("id", "sum");

            for (int i = 0; i < len; i++) {
                map.put("rate_" + (i + 1), bdTotalSumList.get(i) == null ? "- " : mdf.format(bdTotalSumList.get(i).doubleValue()));
                map.put("rate_mv" + (i + 1), "- ");
            }

            result.add(map);

            map = new HashMap<String, Object>();
            map.put("meteringTime", conditionMap.get("msgAvg") + "(" + conditionMap.get("msgMax") + "/" + conditionMap.get("msgMin") + ")");
            map.put("id", "avg");

            for (int i = 0; i < len; i++) {
                if (intTotalCount.get(i) == 0 || bdTotalSumList.get(i) == null) {
                    avgValue = null;
                } else {
                    avgValue = bdTotalSumList.get(i).divide(new BigDecimal(intTotalCount.get(i)), MathContext.DECIMAL32).doubleValue();
                }

                sbAvgValue = new StringBuilder();
                sbAvgValue.append((avgValue == null) ? "- " : mdf.format(avgValue));
                sbAvgValue.append("(");
                sbAvgValue.append((bdTotalMaxList.get(i) == null) ? " - " : mdf.format(bdTotalMaxList.get(i).doubleValue()));
                sbAvgValue.append("/");
                sbAvgValue.append((bdTotalMinList.get(i) == null) ? " - " : mdf.format(bdTotalMinList.get(i).doubleValue()));
                sbAvgValue.append(")");
                map.put("rate_" + (i + 1), sbAvgValue.toString());
                
                sbAvgValue = new StringBuilder();
                sbAvgValue.append("- ");
                sbAvgValue.append("(");
                sbAvgValue.append((bdMvTotalMaxList.get(i) == null) ? " - " : mdf.format(bdMvTotalMaxList.get(i).doubleValue()));
                sbAvgValue.append("/");
                sbAvgValue.append((bdMvTotalMinList.get(i) == null) ? " - " : mdf.format(bdMvTotalMinList.get(i).doubleValue()));
                sbAvgValue.append(")");
                map.put("rate_mv" + (i + 1), sbAvgValue.toString());
            }

            result.add(map);
        }

        return result;
    }

    /**
     * method name : getMeteringValueDetailHourlyData<b/>
     * method Desc : Metering Data 맥스가젯 상세화면에서 시간별 검침데이터를 조회한다.
     *
     * @param conditionMap
     * @return
     */
    public List<Map<String, Object>> getMeteringValueDetailHourlyData(Map<String, Object> conditionMap) {
        return getMeteringValueDetailHourlyData(conditionMap, false);
    }

    /**
     * method name : getMeteringValueDetailHourlyData<b/>
     * method Desc : Metering Data 맥스가젯 상세화면에서 시간별 검침데이터를 조회한다. lpInterval 기준
     *
     * @param conditionMap
     * @param isLpInterval
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> getMeteringValueDetailHourlyData(Map<String, Object> conditionMap, boolean isLpInterval) {
        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
        Integer supplierId = (Integer)conditionMap.get("supplierId");
        String searchStartDate = (String)conditionMap.get("searchStartDate");
        String searchEndDate = (String)conditionMap.get("searchEndDate");
        String searchStartHour = (String)conditionMap.get("searchStartHour");
        String searchEndHour = (String)conditionMap.get("searchEndHour");
        String viewAll = StringUtil.nullToBlank(conditionMap.get("viewAll"));
        String[] channelArray = ((String)conditionMap.get("channel")).split(",");
        List<Integer> channelIdList = new ArrayList<Integer>();
        List<String> channelIdStrList = new ArrayList<String>();
        boolean isViewAll = false;

        if (viewAll.equals("yes")) {
            isViewAll = true;
        }

        for (String obj : channelArray) {
    		if(!channelIdList.contains(Integer.parseInt(obj.replaceAll("mv", "")))) {
    			channelIdList.add(Integer.parseInt(obj.replaceAll("mv", "")));
    		}
        	channelIdStrList.add(obj);
        }

        conditionMap.put("channelIdList", channelIdList);

        Meter meter = mtrDao.get((String)conditionMap.get("meterNo"));
        Integer lpInterval = (meter.getLpInterval() != null) ? meter.getLpInterval() : 60;

        Map<String, Object> listMap = new HashMap<String, Object>();
        Set<String> dateSet = new LinkedHashSet<String>();
        
        Set<Map<String, Object>> dateDstSet = new LinkedHashSet<Map<String, Object>>();
        
        Map<String, Double> listLpMap = new HashMap<String, Double>();
        Map<String, BigDecimal> listLpInvMap = new HashMap<String, BigDecimal>();
        Map<String, Object> map = null;
        Supplier supplier = supplierDao.get(supplierId);
        String country = supplier.getCountry().getCode_2letter();
        String lang = supplier.getLang().getCode_2letter();
        DecimalFormat mdf = DecimalUtil.getDecimalFormat(supplier.getMd());
        Double tmpValue = null;
        Double tmpMeterValue = null;
        Double tmpLpValue = null;
        Double maxValue = null;
        Double minValue = null;
        Double avgValue = null;
        Double sumValue = null;
        StringBuilder sbAvgValue = null;
        String lpmin = null; // 두자리 interval (ex. 00 / 15 / ...)

        Map<String, Object> lpMap = new HashMap<String, Object>();
        List<Map<String, Object>> lpList = new ArrayList<Map<String, Object>>();
        List<Map<String, Object>> list = meteringLpDao.getMeteringDataDetailHourlyData(conditionMap, false);
        Map<String, Object> sMap = new HashMap<String, Object>();

        for (Map<String, Object> tmpMap : list) {
            sMap.put("yyyymmddhh", tmpMap.get("YYYYMMDDHH"));
            sMap.put("dst", tmpMap.get("DST"));
            dateDstSet.add(sMap);
        }

        List<Map<String, Object>> lpAllList = null;
        BigDecimal tmpBdContVal = null;
        BigDecimal tmpBdLpVal = null;
        BigDecimal tmpBdLPMeterVal = null;
        BigDecimal tmpBdContMeterVal = null;

        if (isLpInterval || isViewAll) {
            lpAllList = meteringLpDao.getMeteringDataDetailLpData(conditionMap);

            for (Map<String, Object> obj : lpAllList) {
                for (int i = 0, j = 0 ; j < 60 ; i++, j = i * lpInterval) {
                    lpmin = CalendarUtil.to2Digit(j);
                    tmpValue = DecimalUtil.ConvertNumberToDouble(obj.get("value_" + lpmin));
                    listLpMap.put((String)obj.get("yyyymmddhh") + lpmin + "_" + obj.get("channel") + "_" + obj.get("dst"), tmpValue);
                    
                    if(((Number)obj.get("channel")).intValue() == 1 || ((Number)obj.get("channel")).intValue() == 2) {
                    	tmpMeterValue = DecimalUtil.ConvertNumberToDouble(obj.get("value"));
                    	listLpMap.put((String)obj.get("yyyymmddhh") + lpmin + "_mv" + obj.get("channel") + "_" + obj.get("dst"), tmpMeterValue);
                    }

                    if (isLpInterval) {
                        tmpBdLpVal = (obj.get("value_" + lpmin) != null) ? new BigDecimal(obj.get("value_" + lpmin).toString()) : null;

                        if (listLpInvMap.containsKey((String)obj.get("yyyymmddhh") + lpmin + "_" + obj.get("channel"))) {
                            tmpBdContVal = listLpInvMap.get((String)obj.get("yyyymmddhh") + lpmin + "_" + obj.get("channel"));
                            tmpBdLpVal = (tmpBdLpVal == null) ? tmpBdContVal : (tmpBdContVal == null) ? tmpBdLpVal : tmpBdLpVal.add(tmpBdContVal);
                        }

                        listLpInvMap.put((String)obj.get("yyyymmddhh") + lpmin + "_" + obj.get("channel"), tmpBdLpVal);
                        if(((Number)obj.get("channel")).intValue() == 1 || ((Number)obj.get("channel")).intValue() == 2) {
                        	tmpBdLPMeterVal = (obj.get("value") != null) ? new BigDecimal(obj.get("value").toString()) : null;
                             listLpInvMap.put((String)obj.get("yyyymmddhh") + "00_mv" + obj.get("channel"), tmpBdLPMeterVal);
                        }
                    }
                }
            }
        }

        if (isLpInterval) {
            String tmpLocaleDateHour = searchStartDate + searchStartHour;

            // 조회조건 내 모든 일자 가져오기
            for (int k = 0; k < 100; k++) {     // 무한 loop 방지
                dateSet.add(tmpLocaleDateHour);

                if (tmpLocaleDateHour.compareTo(searchEndDate + searchEndHour) >= 0) {  // 종료일자이면 종료
                    break;
                } else {
                    try {
                        tmpLocaleDateHour = DateTimeUtil.getPreHour(tmpLocaleDateHour + "0000", -1).substring(0, 10);   // +1 시간 더함
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            }
        } else {
            for (int i = 0; i < 24; i++) {
                dateSet.add(searchStartDate + CalendarUtil.to2Digit(i));
            }
        }

        BigDecimal bdTmpValue = null;
        BigDecimal bdTmpMeterValue = null;
        int avgCnt = 0;

        Map<String, Object> channelValueMap = new HashMap<String, Object>();
        List<Map<String, Object>> channelValueList = new ArrayList<Map<String, Object>>();
        List<List<Map<String, Object>>> dstList = new ArrayList<List<Map<String, Object>>>();
        String prevYyyymmddhh = null;
        Integer prevDst = null;

        for (Map<String, Object> obj : list) {
            bdTmpValue = null;
            bdTmpMeterValue = null;
            avgCnt = 0;

            if (prevYyyymmddhh != null) {
                if (!prevYyyymmddhh.equals((String)obj.get("YYYYMMDDHH"))) {
                    dstList.add(channelValueList);
                    channelValueList = new ArrayList<Map<String, Object>>();
                    listMap.put(prevYyyymmddhh, dstList);
                    dstList = new ArrayList<List<Map<String, Object>>>();
                } else if (!prevDst.equals(DecimalUtil.ConvertNumberToInteger(obj.get("DST")))) {
                    dstList.add(channelValueList);
                    channelValueList = new ArrayList<Map<String, Object>>();
                }
            }

            prevYyyymmddhh = (String)obj.get("YYYYMMDDHH");
            prevDst = DecimalUtil.ConvertNumberToInteger(obj.get("DST"));

            for (int i = 0, j = 0 ; j < 60 ; i++, j = i * lpInterval) {
                tmpValue = DecimalUtil.ConvertNumberToDouble(obj.get("VALUE_" + CalendarUtil.to2Digit(j)));

                if (tmpValue != null) {
                    if (bdTmpValue != null) {
                        if (ChannelCalcMethod.MAX.name().equals((String)obj.get("CH_METHOD"))) {
                            bdTmpValue = bdTmpValue.max(new BigDecimal(tmpValue));
                        } else {    // SUM, AVG
                            bdTmpValue = bdTmpValue.add(new BigDecimal(tmpValue.toString()));
                        }
                    } else {
                        bdTmpValue = new BigDecimal(tmpValue.toString());
                    }
                    avgCnt++;
                }
                
                if((((Number)obj.get("CHANNEL")).intValue() == 1 || ((Number)obj.get("CHANNEL")).intValue() == 2) && i == 0) {
                	tmpMeterValue = DecimalUtil.ConvertNumberToDouble(obj.get("VALUE"));

                    if (tmpMeterValue != null) {
                        if (bdTmpMeterValue != null) {
                            if (ChannelCalcMethod.MAX.name().equals((String)obj.get("CH_METHOD"))) {
                            	bdTmpMeterValue = bdTmpMeterValue.max(new BigDecimal(tmpMeterValue));
                            } else {    // SUM, AVG
                            	bdTmpMeterValue = bdTmpMeterValue.add(new BigDecimal(tmpMeterValue.toString()));
                            }
                        } else {
                            bdTmpMeterValue = new BigDecimal(tmpMeterValue.toString());
                        }
                        avgCnt++;
                    }
                }
            }

            if (bdTmpValue != null && ChannelCalcMethod.AVG.name().equals((String)obj.get("CH_METHOD"))) {
                if (avgCnt > 0) {
                    bdTmpValue = bdTmpValue.divide(new BigDecimal(avgCnt), MathContext.DECIMAL32);
                } else {
                    bdTmpValue = new BigDecimal("0");
                }
            }
            
            channelValueMap = new HashMap<String, Object>();
            channelValueMap.put("channel", obj.get("CHANNEL").toString());
            channelValueMap.put("dst", ((Number)obj.get("DST")).intValue());
            channelValueMap.put("value", bdTmpValue);
            
            channelValueList.add(channelValueMap);
            
            if((((Number)obj.get("CHANNEL")).intValue() == 1 || ((Number)obj.get("CHANNEL")).intValue() == 2)) {
                if (bdTmpMeterValue != null && ChannelCalcMethod.AVG.name().equals((String)obj.get("CH_METHOD"))) {
                    if (avgCnt > 0) {
                        bdTmpMeterValue = bdTmpMeterValue.divide(new BigDecimal(avgCnt), MathContext.DECIMAL32);
                    } else {
                        bdTmpMeterValue = new BigDecimal("0");
                    }
                }
            	
            	channelValueMap = new HashMap<String, Object>();
            	channelValueMap.put("channel", "mv"+obj.get("CHANNEL").toString());
            	channelValueMap.put("dst", ((Number)obj.get("DST")).intValue());
            	channelValueMap.put("value", bdTmpMeterValue);
                
                channelValueList.add(channelValueMap);
            }
        }

        if (list.size() > 0) {
            dstList.add(channelValueList);
            listMap.put(prevYyyymmddhh, dstList);
        }

        Iterator<String> itr = dateSet.iterator();
        String date = null;

        List<BigDecimal> bdTotalSumList = new ArrayList<BigDecimal>();
        List<BigDecimal> bdTotalMaxList = new ArrayList<BigDecimal>();
        List<BigDecimal> bdTotalMinList = new ArrayList<BigDecimal>();
        List<Integer> intTotalCount = new ArrayList<Integer>();

        int row = 0;
        int chIndex = 0;
        boolean checkFirstRealData = true;
        bdTmpValue = null;

        Integer tmpDst = null;
        while (itr.hasNext()) {
            date = itr.next();

            if ((List<List<Map<String, Object>>>)listMap.get(date) != null) {
                for (List<Map<String, Object>> tmpList : (List<List<Map<String, Object>>>)listMap.get(date)) {
                    map = new HashMap<String, Object>();
                    tmpDst = null;

                    map.put("meteringTime", TimeLocaleUtil.getLocaleDateHour(date, lang, country));
                    chIndex = 0;
                    for (String obj : channelIdStrList) {
                        bdTmpValue = null;

                        for (Map<String, Object> tmpMap : tmpList) {
                            if (obj.equals((String)tmpMap.get("channel"))) {
                                bdTmpValue = (BigDecimal)tmpMap.get("value");
                                tmpDst = (Integer)tmpMap.get("dst");
                            }
                        }

                        map.put("channel_" + obj, (bdTmpValue == null) ? "- " : mdf.format(bdTmpValue.doubleValue()));

                        if (!isLpInterval) {
                            if (row == 0) {
                                if (bdTmpValue != null) {
                                    bdTotalSumList.add(bdTmpValue);
                                    bdTotalMaxList.add(bdTmpValue);
                                    bdTotalMinList.add(bdTmpValue);
                                    intTotalCount.add(1);
                                } else {
                                    bdTotalSumList.add(null);
                                    bdTotalMaxList.add(null);
                                    bdTotalMinList.add(null);
                                    intTotalCount.add(0);
                                }
                            } else {
                                if (bdTmpValue != null) {
                                	if(checkFirstRealData) {
                                		bdTotalSumList.add(bdTmpValue);
                                	} else if (bdTotalSumList.get(chIndex) == null) {
                                        bdTotalSumList.set(chIndex, bdTmpValue);
                                    } else {
                                        bdTotalSumList.set(chIndex, bdTotalSumList.get(chIndex).add(bdTmpValue));
                                    }

                                	if(checkFirstRealData) {
                                		bdTotalMaxList.add(bdTmpValue);
                                	} else if (bdTotalMaxList.get(chIndex) == null) {
                                        bdTotalMaxList.set(chIndex, bdTmpValue);
                                    } else {
                                        bdTotalMaxList.set(chIndex, bdTotalMaxList.get(chIndex).max(bdTmpValue));
                                    }

                                	if(checkFirstRealData) {
                                		bdTotalMinList.add(bdTmpValue);
                                	} else if (bdTotalMinList.get(chIndex) == null) {
                                        bdTotalMinList.set(chIndex, bdTmpValue);
                                    } else {
                                        bdTotalMinList.set(chIndex, bdTotalMinList.get(chIndex).min(bdTmpValue));
                                    }
                                	if(checkFirstRealData) {
                                		intTotalCount.add(1);
                                	} else {
                                		intTotalCount.set(chIndex, intTotalCount.get(chIndex) + 1);
                                	}
                                    
                                }
                            }
                        }
                        chIndex++;
                    }
                    map.put("dst", tmpDst);
                    map.put("id", date + "_" + tmpDst);
                    map.put("iconCls", "no-icon");

                    if (isViewAll) {
                        lpList = new ArrayList<Map<String, Object>>();

                        for (int i = 0, j = 0 ; j < 60 ; i++, j = i * lpInterval) {
                            lpmin = CalendarUtil.to2Digit(j);
                            lpMap = new HashMap<String, Object>();
                            lpMap.put("id", date + lpmin);
                            lpMap.put("meteringTime", TimeLocaleUtil.getLocaleDate(date + lpmin + "00", lang, country));

                            for (Integer ch : channelIdList) {
                                tmpLpValue = listLpMap.get(date + lpmin + "_" + ch + "_" + tmpDst);
                                lpMap.put("channel_" + ch, (tmpLpValue == null) ? "- " : mdf.format(tmpLpValue));
                            }
                            lpMap.put("iconCls", "no-icon");
                            lpMap.put("leaf", true);

                            lpList.add(lpMap);
                        }

                        map.put("expanded", true);
                        map.put("children", lpList);
                    }

                    result.add(map);
                    checkFirstRealData=false;
                    row++;
                }
            } else {
                map = new HashMap<String, Object>();
                map.put("meteringTime", TimeLocaleUtil.getLocaleDateHour(date, lang, country));

                for (String obj : channelIdStrList) {
                    map.put("channel_" + obj, "- ");
                }
                map.put("dst", null);
                map.put("id", date);
                map.put("iconCls", "no-icon");

                if (isViewAll) {
                    lpList = new ArrayList<Map<String, Object>>();

                    for (int i = 0, j = 0; j < 60; i++, j = i * lpInterval) {
                        lpmin = CalendarUtil.to2Digit(j);
                        lpMap = new HashMap<String, Object>();
                        lpMap.put("id", date + lpmin);
                        lpMap.put("meteringTime", TimeLocaleUtil.getLocaleDate(date + lpmin + "00", lang, country));

                        for (String ch : channelIdStrList) {
                            lpMap.put("channel_" + ch, "- ");
                        }
                        lpMap.put("iconCls", "no-icon");
                        lpMap.put("leaf", true);

                        lpList.add(lpMap);
                    }

                    map.put("expanded", true);
                    map.put("children", lpList);
                }

                result.add(map);
                row++;
            }
        }

        BigDecimal bdTmpLpValue = null;

        if (isLpInterval) {
            itr = dateSet.iterator();
            row = 0;
            while(itr.hasNext()) {
                date = itr.next();

                for (int i = 0, j = 0 ; j < 60 ; i++, j = i * lpInterval) {
                    lpmin = CalendarUtil.to2Digit(j);
                    chIndex = 0;

                    for (String ch : channelIdStrList) {
                        bdTmpLpValue = listLpInvMap.get(date + lpmin + "_" + ch);

                        if (row == 0 && i == 0) {
                            if (bdTmpLpValue != null) {
                                bdTotalSumList.add(bdTmpLpValue);
                                bdTotalMaxList.add(bdTmpLpValue);
                                bdTotalMinList.add(bdTmpLpValue);
                                intTotalCount.add(1);
                            } else {
                                bdTotalSumList.add(null);
                                bdTotalMaxList.add(null);
                                bdTotalMinList.add(null);
                                intTotalCount.add(0);
                            }
                        } else {
                            if (bdTmpLpValue != null) {
                                if (bdTotalSumList.get(chIndex) == null) {
                                    bdTotalSumList.set(chIndex, bdTmpLpValue);
                                } else {
                                    bdTotalSumList.set(chIndex, bdTotalSumList.get(chIndex).add(bdTmpLpValue));
                                }

                                if (bdTotalMaxList.get(chIndex) == null) {
                                    bdTotalMaxList.set(chIndex, bdTmpLpValue);
                                } else {
                                    bdTotalMaxList.set(chIndex, bdTotalMaxList.get(chIndex).max(bdTmpLpValue));
                                }

                                if (bdTotalMinList.get(chIndex) == null) {
                                    bdTotalMinList.set(chIndex, bdTmpLpValue);
                                } else {
                                    bdTotalMinList.set(chIndex, bdTotalMinList.get(chIndex).min(bdTmpLpValue));
                                }
                                intTotalCount.set(chIndex, intTotalCount.get(chIndex) + 1);
                            }
                        }
                        chIndex++;
                    }
                }

                row++;
            }
        }

        // Sum/Avg
        if (bdTotalSumList != null && bdTotalSumList.size() > 0) {
            map = new HashMap<String, Object>();
            map.put("meteringTime", conditionMap.get("msgSum"));
            chIndex = 0;

            for (String ch : channelIdStrList) {
            	if(!ch.contains("mv")) {
	                sumValue = (bdTotalSumList.get(chIndex) == null) ? null : bdTotalSumList.get(chIndex).doubleValue();
	                map.put("channel_" + ch, (sumValue == null) ? "- " : mdf.format(sumValue));
	                chIndex++;
            	} else {
            		// Meter Value 의 경우 Sum값이 의미가 없다.
	                map.put("channel_" + ch, "- ");
	                chIndex++;
            	}
            }

            map.put("id", "sum");
            map.put("iconCls", "no-icon");
            map.put("leaf", true);

            result.add(map);

            map = new HashMap<String, Object>();
            map.put("meteringTime", conditionMap.get("msgAvg") + "(" + conditionMap.get("msgMax") + "/" + conditionMap.get("msgMin") + ")");

            chIndex = 0;

            for (String ch : channelIdStrList) {
                sbAvgValue = new StringBuilder();
                maxValue = (bdTotalMaxList.get(chIndex) == null) ? null : bdTotalMaxList.get(chIndex).doubleValue();
                minValue = (bdTotalMinList.get(chIndex) == null) ? null : bdTotalMinList.get(chIndex).doubleValue();

                if (bdTotalSumList.get(chIndex) == null || intTotalCount.get(chIndex) == null) {
                    avgValue = null;
                } else if (intTotalCount.get(chIndex) == 0) {
                    avgValue = 0D;
                } else {
                	if(!ch.contains("mv")) {
                		avgValue = bdTotalSumList.get(chIndex).divide(new BigDecimal(intTotalCount.get(chIndex)), MathContext.DECIMAL32).doubleValue();
                	} else {
                		avgValue = null;
                	}
                    
                }

                if (maxValue != null || minValue != null || avgValue != null) {
                    sbAvgValue.append((avgValue == null) ? "- " : mdf.format(avgValue));
                    sbAvgValue.append("(");
                    sbAvgValue.append((maxValue == null) ? " - " : mdf.format(maxValue));
                    sbAvgValue.append("/");
                    sbAvgValue.append((minValue == null) ? " - " : mdf.format(minValue));
                    sbAvgValue.append(")");
                }
                map.put("channel_" + ch, sbAvgValue.toString());
                chIndex++;
            }

            map.put("id", "avg");
            map.put("iconCls", "no-icon");
            map.put("leaf", true);

            result.add(map);
        }
        return result;        
    }
    
    /**
     * method name : getMeteringValueDetailDailyData<b/>
     * method Desc : Metering Data 맥스가젯 상세화면에서 일별 검침데이터를 조회한다.
     *
     * @param conditionMap
     * @return
     */
    public List<Map<String, Object>> getMeteringValueDetailDailyData(Map<String, Object> conditionMap) {
        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
        Integer supplierId = (Integer)conditionMap.get("supplierId");
        String searchStartDate = (String)conditionMap.get("searchStartDate");
        String searchEndDate = (String)conditionMap.get("searchEndDate");
        String[] channelArray = ((String)conditionMap.get("channel")).split(",");
        List<Integer> channelIdList = new ArrayList<Integer>();
        List<String> channelIdStrList = new ArrayList<String>();

        for (String obj : channelArray) {
    		if(!channelIdList.contains(Integer.parseInt(obj.replaceAll("mv", "")))) {
    			channelIdList.add(Integer.parseInt(obj.replaceAll("mv", "")));
    		}
            channelIdStrList.add(obj);
        }

        conditionMap.put("channelIdList", channelIdList);

        List<Map<String, Object>> list = meteringDayDao.getMeteringDataDetailDailyData(conditionMap, false);
        Map<String, Object> listMap = new HashMap<String, Object>();
        Map<String, Object> sumListMap = new HashMap<String, Object>();
        Set<String> dateSet = new LinkedHashSet<String>();
        Map<String, Object> map = null;
        Supplier supplier = supplierDao.get(supplierId);
        String country = supplier.getCountry().getCode_2letter();
        String lang    = supplier.getLang().getCode_2letter();
        DecimalFormat mdf = DecimalUtil.getDecimalFormat(supplier.getMd());
        Double value = null;
        Double maxValue = null;
        Double minValue = null;
        Double avgValue = null;
        Double sumValue = null;
        StringBuilder sbAvgValue = null;

        int duration = 0;

        try {
            duration = TimeUtil.getDayDuration(searchStartDate, searchEndDate);
        } catch (ParseException e1) {
            e1.printStackTrace();
        }

        String tmpDate = searchStartDate;

        for (int i = 0; i <= duration; i++) {
            dateSet.add(tmpDate);
            try {
                tmpDate = TimeUtil.getPreDay(tmpDate + "000000", -1).substring(0, 8);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        for (Map<String, Object> obj : list) {
            listMap.put((String)obj.get("YYYYMMDD") + "_" + (Number)obj.get("CHANNEL"), obj.get("VALUE"));
            if("1".equals(obj.get("CHANNEL").toString()) || "2".equals(obj.get("CHANNEL").toString())) {
            	listMap.put((String)obj.get("YYYYMMDD") + "_mv" + (Number)obj.get("CHANNEL"), obj.get("BASEVALUE"));
            }
            // dateSet.add((String)obj.get("YYYYMMDD"));
        }

        Iterator<String> itr = dateSet.iterator();
        String date = null;

        while(itr.hasNext()) {
            date = itr.next();
            map = new HashMap<String, Object>();

            map.put("meteringTime", TimeLocaleUtil.getLocaleDate(date, lang, country));

            for (String obj : channelIdStrList) {
                value = DecimalUtil.ConvertNumberToDouble(listMap.get(date + "_" + obj));
                map.put("channel_" + obj, (value == null) ? "- " : mdf.format(value));
            }

            result.add(map);
        }

        List<Map<String, Object>> sumList = meteringDayDao.getMeteringDataDetailDailyData(conditionMap, true);

        if (sumList != null && sumList.size() > 0) {
            for (Map<String, Object> obj : sumList) {
                sumListMap.put("MAX_" + (Number)obj.get("CHANNEL"), obj.get("MAX_VAL"));
                sumListMap.put("MIN_" + (Number)obj.get("CHANNEL"), obj.get("MIN_VAL"));
                sumListMap.put("AVG_" + (Number)obj.get("CHANNEL"), obj.get("AVG_VAL"));
                sumListMap.put("SUM_" + (Number)obj.get("CHANNEL"), obj.get("SUM_VAL"));
                if("1".equals(obj.get("CHANNEL").toString()) || "2".equals(obj.get("CHANNEL").toString())) {
                	sumListMap.put("MAX_mv" + (Number)obj.get("CHANNEL"), obj.get("MAX_BASEVAL"));
                    sumListMap.put("MIN_mv" + (Number)obj.get("CHANNEL"), obj.get("MIN_BASEVAL"));
                }
            }

            map = new HashMap<String, Object>();
            map.put("meteringTime", conditionMap.get("msgSum"));
            map.put("id", "sum");

            for (String ch : channelIdStrList) {
        		sumValue = DecimalUtil.ConvertNumberToDouble(sumListMap.get("SUM_" + ch));
                map.put("channel_" + ch, (sumValue == null) ? "- " : mdf.format(sumValue));
            }

            result.add(map);

            map = new HashMap<String, Object>();
            map.put("meteringTime", conditionMap.get("msgAvg") + "(" + conditionMap.get("msgMax") + "/" + conditionMap.get("msgMin") + ")");

            for (String ch : channelIdStrList) {
                sbAvgValue = new StringBuilder();
                maxValue = DecimalUtil.ConvertNumberToDouble(sumListMap.get("MAX_" + ch));
                minValue = DecimalUtil.ConvertNumberToDouble(sumListMap.get("MIN_" + ch));
                avgValue = DecimalUtil.ConvertNumberToDouble(sumListMap.get("AVG_" + ch));

                if (maxValue != null || minValue != null || avgValue != null) {
                    sbAvgValue.append((avgValue == null) ? "- " : mdf.format(avgValue));
                    sbAvgValue.append("(");
                    sbAvgValue.append((maxValue == null) ? " - " : mdf.format(maxValue));
                    sbAvgValue.append("/");
                    sbAvgValue.append((minValue == null) ? " - " : mdf.format(minValue));
                    sbAvgValue.append(")");
                }
                map.put("channel_" + ch, sbAvgValue.toString());
                map.put("id", "avg");
            }

            result.add(map);
        }
        return result;
    }
    
    /**
     * method name : getMeteringValueDetailWeeklyData<b/>
     * method Desc : Metering Data 맥스가젯 상세화면에서 주별 검침데이터를 조회한다.
     *
     * @param conditionMap
     * @return
     */
    public List<Map<String, Object>> getMeteringValueDetailWeeklyData(Map<String, Object> conditionMap) {
        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
        Integer supplierId = (Integer)conditionMap.get("supplierId");
        String[] channelArray = ((String)conditionMap.get("channel")).split(",");
        List<Integer> channelIdList = new ArrayList<Integer>();
        List<String> channelIdStrList = new ArrayList<String>();
        
        for (String obj : channelArray) {
    		if(!channelIdList.contains(Integer.parseInt(obj.replaceAll("mv", "")))) {
    			channelIdList.add(Integer.parseInt(obj.replaceAll("mv", "")));
    		}
        	channelIdStrList.add(obj);
        }

        conditionMap.put("channelIdList", channelIdList);

        List<Map<String, Object>> list = meteringDayDao.getMeteringDataDetailDailyData(conditionMap, false);

        if (list == null || list.size() <= 0) {
            return result;
        }

        Set<String> dateSet = new LinkedHashSet<String>();
        Map<String, Object> listMap = new HashMap<String, Object>();
        Map<String, Object> chMethodMap = new HashMap<String, Object>();
        Map<String, Object> map = null;
        Supplier supplier = supplierDao.get(supplierId);
        String country = supplier.getCountry().getCode_2letter();
        String lang    = supplier.getLang().getCode_2letter();
        DecimalFormat mdf = DecimalUtil.getDecimalFormat(supplier.getMd());
        Double value = null;    // 계산용 임시변수
        Double maxValue = null;
        Double minValue = null;
        Double avgValue = null;
        Double sumValue = null;
        StringBuilder sbAvgValue = null;

        for (Map<String, Object> obj : list) {
            listMap.put((String)obj.get("YYYYMMDD") + "_" + (Number)obj.get("CHANNEL"), obj.get("VALUE"));
            dateSet.add((String)obj.get("YYYYMMDD"));
            chMethodMap.put(obj.get("CHANNEL").toString(), obj.get("CH_METHOD"));
            
            if("1".equals(obj.get("CHANNEL").toString()) || "2".equals(obj.get("CHANNEL").toString())) {
            	listMap.put((String)obj.get("YYYYMMDD") + "_mv" + (Number)obj.get("CHANNEL"), obj.get("BASEVALUE"));
                chMethodMap.put("mv"+obj.get("CHANNEL").toString(), obj.get("CH_METHOD"));
            }
        }

        List<Map<String, String>> weeksList = new ArrayList<Map<String, String>>();
        String searchStartDate = (String)conditionMap.get("searchStartDate");
        String searchEndDate = (String)conditionMap.get("searchEndDate");

        String startMonth = searchStartDate.substring(0, 6);
        Integer startWeek = CalendarUtil.getWeekOfMonth(searchStartDate);
        String endMonth = searchEndDate.substring(0, 6);
        Integer endWeek = CalendarUtil.getWeekOfMonth(searchEndDate);
        String curMonth = null;
        int firstWeek = 1;
        int lastWeek = 0;

        if (startMonth.equals(endMonth)) {      // 년월이 동일한 경우
            curMonth = startMonth;

            // 해당월의 각 주에 해당하는 시작일자 종료일자를 구한다.
            for (int i = startWeek; i <= endWeek; i++) {
                weeksList.add(CalendarUtil.getDateWeekOfMonth(curMonth.substring(0, 4), curMonth.substring(4, 6), i + ""));
            }
        } else {        // 연월이 다를 경우
            for (int i = 0; i < 100; i++) {     // 무한 loop 를 피하기 위해 for 문으로 loop 회수 제한
                if (i == 0) {
                    curMonth = startMonth;
                    firstWeek = startWeek;
                } else {
                    curMonth = (CalendarUtil.getDate(curMonth + "01", Calendar.MONTH, 1)).substring(0, 6);
                    firstWeek = 1;
                }

                if (curMonth.equals(endMonth)) {
                    lastWeek = endWeek;
                } else {
                    lastWeek = Integer.parseInt(CalendarUtil.getWeekCountOfMonth(curMonth.substring(0, 4), curMonth.substring(4)));
                }

                for (int j = firstWeek; j <= lastWeek; j++) {
                    weeksList.add(CalendarUtil.getDateWeekOfMonth(curMonth.substring(0, 4), curMonth.substring(4), j + ""));
                }

                if (curMonth.equals(endMonth)) {
                    break;
                }
            }
        }

        int len = weeksList.size();
        Map<String, String> weekDatesMap = new HashMap<String, String>();
        String startDate = null;
        String endDate = null;
        String curDate = null;
        List<BigDecimal> bdTotalSumList = new ArrayList<BigDecimal>();
        List<BigDecimal> bdTotalMaxList = new ArrayList<BigDecimal>();
        List<BigDecimal> bdTotalMinList = new ArrayList<BigDecimal>();
        List<Integer> intTotalCount = new ArrayList<Integer>();
        List<BigDecimal> bdWeekValueList = new ArrayList<BigDecimal>();
        int chIndex = 0;
        int cnt = 0;

        for (int k = 0; k < len; k++) {
            map = new HashMap<String, Object>();
            weekDatesMap = new HashMap<String, String>();
            startDate = null;
            endDate = null;
            curDate = null;
            weekDatesMap = weeksList.get(k);
            startDate = weekDatesMap.get("startDate");
            endDate = weekDatesMap.get("endDate");
            curDate = startDate;
            bdWeekValueList = new ArrayList<BigDecimal>();
            cnt = 0;

            for (int l = 0; l < 7; l++) {   // 일주일 데이터를 sum
                chIndex = 0;

                for (String obj : channelIdStrList) {
                    value = DecimalUtil.ConvertNumberToDouble(listMap.get(curDate + "_" + obj));

                    if (l == 0) {
                        if (value != null) {
                            bdWeekValueList.add(new BigDecimal(value.toString()));
                            cnt++;
                        } else {
                            bdWeekValueList.add(null);
                        }
                    } else {
                        if (value != null) {
                            if (bdWeekValueList.get(chIndex) == null) {
                                bdWeekValueList.set(chIndex, new BigDecimal(value.toString()));
                            } else {
                                if (obj.contains("mv") || 
                                		ChannelCalcMethod.MAX.name().equals((String)chMethodMap.get(obj.toString()))) {     // MAX
                                    bdWeekValueList.set(chIndex, bdWeekValueList.get(chIndex).max(new BigDecimal(value.toString())));
                                } else {    // SUM, AVG
                                    bdWeekValueList.set(chIndex, bdWeekValueList.get(chIndex).add(new BigDecimal(value.toString())));
                                }
                            }
                            cnt++;
                        }
                    }
                    chIndex++;
                }

                if (curDate.equals(endDate)) {
                    break;
                } else {
                    curDate = CalendarUtil.getDate(curDate, Calendar.DAY_OF_MONTH, 1);
                }
            }

            chIndex = 0;

            for (String obj : channelIdStrList) {
                
                if (bdWeekValueList.get(chIndex) != null) {
                    if (ChannelCalcMethod.AVG.name().equals((String)chMethodMap.get(obj.toString()))) {     // AVG
                        bdWeekValueList.set(chIndex, bdWeekValueList.get(chIndex).divide(new BigDecimal(cnt+""), MathContext.DECIMAL32));
                    }
                }

                if (k == 0) {
                    if (bdWeekValueList.get(chIndex) != null) {
                        bdTotalSumList.add(bdWeekValueList.get(chIndex));
                        bdTotalMaxList.add(bdWeekValueList.get(chIndex));
                        bdTotalMinList.add(bdWeekValueList.get(chIndex));
                    } else {
                        bdTotalSumList.add(null);
                        bdTotalMaxList.add(null);
                        bdTotalMinList.add(null);
                    }
                } else {
                    if (bdWeekValueList.get(chIndex) != null) {
                        if (bdTotalSumList.get(chIndex) == null) {
                            bdTotalSumList.set(chIndex, bdWeekValueList.get(chIndex));
                        } else {
                            bdTotalSumList.set(chIndex, bdTotalSumList.get(chIndex).add(bdWeekValueList.get(chIndex)));
                        }

                        if (bdTotalMaxList.get(chIndex) == null) {
                            bdTotalMaxList.set(chIndex, bdWeekValueList.get(chIndex));
                        } else {
                            bdTotalMaxList.set(chIndex, bdTotalMaxList.get(chIndex).max(bdWeekValueList.get(chIndex)));
                        }

                        if (bdTotalMinList.get(chIndex) == null) {
                            bdTotalMinList.set(chIndex, bdWeekValueList.get(chIndex));
                        } else {
                            bdTotalMinList.set(chIndex, bdTotalMinList.get(chIndex).min(bdWeekValueList.get(chIndex)));
                        }
                    }
                }

                if (k == 0) {
                    if (bdWeekValueList.get(chIndex) != null) {
                        intTotalCount.add(1);
                    } else {
                        intTotalCount.add(0);
                    }
                } else {
                    if (bdWeekValueList.get(chIndex) != null) {
                        intTotalCount.set(chIndex, intTotalCount.get(chIndex) + 1);
                    }
                }

                value = (bdWeekValueList.get(chIndex) == null) ? null : bdWeekValueList.get(chIndex).doubleValue();
                map.put("channel_" + obj, (value == null) ? "- " : mdf.format(value));
                chIndex++;
            }

            map.put("meteringTime", TimeLocaleUtil.getLocaleYearMonth(startDate.substring(0, 6), lang, country) + " " + CalendarUtil.getWeekOfMonth(startDate) + "Week");
            result.add(map);
        }

        if (bdTotalSumList != null && bdTotalSumList.size() > 0) {
            map = new HashMap<String, Object>();
            map.put("meteringTime", conditionMap.get("msgSum"));
            map.put("id", "sum");

            chIndex = 0;
            for (String ch : channelIdStrList) {
            	if(!ch.contains("mv")) {
            		sumValue = (bdTotalSumList.get(chIndex) == null) ? null : bdTotalSumList.get(chIndex).doubleValue();
                    map.put("channel_" + ch, (sumValue == null) ? "- " : mdf.format(sumValue));
                    chIndex++;
            	} else {
                    map.put("channel_" + ch, "- ");
                    chIndex++;
            	}
                
            }

            result.add(map);

            map = new HashMap<String, Object>();
            map.put("meteringTime", conditionMap.get("msgAvg") + "(" + conditionMap.get("msgMax") + "/" + conditionMap.get("msgMin") + ")");
            map.put("id", "avg");

            chIndex = 0;
            for (String ch : channelIdStrList) {
                sbAvgValue = new StringBuilder();
                maxValue = (bdTotalMaxList.get(chIndex) == null) ? null : bdTotalMaxList.get(chIndex).doubleValue();
                minValue = (bdTotalMinList.get(chIndex) == null) ? null : bdTotalMinList.get(chIndex).doubleValue();

                if (bdTotalSumList.get(chIndex) == null || intTotalCount.get(chIndex) == null) {
                    avgValue = null;
                } else if (intTotalCount.get(chIndex) == 0) {
                    avgValue = 0D;
                } else {
                	if(!ch.contains("mv")) {
                		avgValue = bdTotalSumList.get(chIndex).divide(new BigDecimal(intTotalCount.get(chIndex)), MathContext.DECIMAL32).doubleValue();
                	} else {
                		avgValue = null;
                	}
                }

                if (maxValue != null || minValue != null || avgValue != null) {
                    sbAvgValue.append((avgValue == null) ? "- " : mdf.format(avgValue));
                    sbAvgValue.append("(");
                    sbAvgValue.append((maxValue == null) ? " - " : mdf.format(maxValue));
                    sbAvgValue.append("/");
                    sbAvgValue.append((minValue == null) ? " - " : mdf.format(minValue));
                    sbAvgValue.append(")");
                }
                map.put("channel_" + ch, sbAvgValue.toString());
                chIndex++;
            }
            result.add(map);
        }

        return result;
    }
    
    /**
     * method name : getMeteringValueDetailMonthlyData<b/>
     * method Desc : Metering Data 맥스가젯 상세화면에서 월별 지침값을 조회한다.
     *
     * @param conditionMap
     * @return
     */
    public List<Map<String, Object>> getMeteringValueDetailMonthlyData(Map<String, Object> conditionMap) {
        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
        Integer supplierId = (Integer)conditionMap.get("supplierId");
        String searchStartDate = (String)conditionMap.get("searchStartDate");
        String[] channelArray = ((String)conditionMap.get("channel")).split(",");
        List<Integer> channelIdList = new ArrayList<Integer>();
        List<String> channelIdStrList = new ArrayList<String>();
        for (String obj : channelArray) {
    		if(!channelIdList.contains(Integer.parseInt(obj.replaceAll("mv", "")))) {
    			channelIdList.add(Integer.parseInt(obj.replaceAll("mv", "")));
    		}
        	channelIdStrList.add(obj);
        }

        conditionMap.put("channelIdList", channelIdList);

        List<Map<String, Object>> list = meteringMonthDao.getMeteringDataDetailMonthlyData(conditionMap, false);
        List<Map<String, Object>> mvList = meteringDayDao.getMeteringValueDetailMonthlyData(conditionMap, false);
        Map<String, Object> listMap = new HashMap<String, Object>();
        Map<String, Double> mvMINMAXMap = new HashMap<String, Double>();
        Map<String, Object> sumListMap = new HashMap<String, Object>();
        Set<String> dateSet = new LinkedHashSet<String>();
        Map<String, Object> map = null;
        Supplier supplier = supplierDao.get(supplierId);
        String country = supplier.getCountry().getCode_2letter();
        String lang    = supplier.getLang().getCode_2letter();
        DecimalFormat mdf = DecimalUtil.getDecimalFormat(supplier.getMd());
        Double value = null;
        Double maxValue = null;
        Double minValue = null;
        Double avgValue = null;
        Double sumValue = null;
        StringBuilder sbAvgValue = null;
        String startYear = searchStartDate.substring(0, 4);

        for (int i = 1; i <= 12; i++) {
            dateSet.add(startYear + CalendarUtil.to2Digit(i));
        }

        for (Map<String, Object> obj : list) {
            listMap.put((String)obj.get("YYYYMM") + "_" + (Number)obj.get("CHANNEL"), obj.get("VALUE"));
        }
        
        for (Map<String, Object> obj : mvList) {
            if("1".equals(obj.get("CHANNEL").toString()) || "2".equals(obj.get("CHANNEL").toString())) {
            	String yyyymm = obj.get("YYYYMMDD").toString().substring(0, 6);
            	Double newData = DecimalUtil.ConvertNumberToDouble(obj.get("BASEVALUE"));
            	listMap.put(yyyymm + "_mv" + obj.get("CHANNEL"), newData);
            	
            	Double maxData = mvMINMAXMap.get("mv_" + obj.get("CHANNEL") + "_MAX");
            	Double minData = mvMINMAXMap.get("mv_" + obj.get("CHANNEL") + "_MIN");
            	if(maxData == null) {
            		mvMINMAXMap.put("mv_" + obj.get("CHANNEL") + "_MAX", newData);
            	} else {
            		mvMINMAXMap.put("mv_" + obj.get("CHANNEL") + "_MAX", Math.max(maxData, newData));
            	}
            	
            	if(minData == null) {
            		mvMINMAXMap.put("mv_" + obj.get("CHANNEL") + "_MIN", newData);
            	} else {
            		mvMINMAXMap.put("mv_" + obj.get("CHANNEL") + "_MIN", Math.min(minData, newData));
            	}
            }
        }

        Iterator<String> itr = dateSet.iterator();
        String date = null;

        while(itr.hasNext()) {
            date = itr.next();
            map = new HashMap<String, Object>();
            map.put("meteringTime", TimeLocaleUtil.getLocaleYearMonth(date, lang, country));

            for (String obj : channelIdStrList) {
                value = DecimalUtil.ConvertNumberToDouble(listMap.get(date + "_" + obj));
                map.put("channel_" + obj, (value == null) ? "- " : mdf.format(value));
            }

            result.add(map);
        }

        List<Map<String, Object>> sumList = meteringMonthDao.getMeteringDataDetailMonthlyData(conditionMap, true);

        if (sumList != null && sumList.size() > 0) {
            for (Map<String, Object> obj : sumList) {
                sumListMap.put("MAX_" + (Number)obj.get("CHANNEL"), obj.get("MAX_VAL"));
                sumListMap.put("MIN_" + (Number)obj.get("CHANNEL"), obj.get("MIN_VAL"));
                sumListMap.put("AVG_" + (Number)obj.get("CHANNEL"), obj.get("AVG_VAL"));
                sumListMap.put("SUM_" + (Number)obj.get("CHANNEL"), obj.get("SUM_VAL"));
                if("1".equals(obj.get("CHANNEL").toString()) || "2".equals(obj.get("CHANNEL").toString())) {
                	sumListMap.put("MAX_mv" + (Number)obj.get("CHANNEL"), mvMINMAXMap.get("mv_" + obj.get("CHANNEL") + "_MAX"));
                    sumListMap.put("MIN_mv" + (Number)obj.get("CHANNEL"), mvMINMAXMap.get("mv_" + obj.get("CHANNEL") + "_MIN"));
                }
            }

            map = new HashMap<String, Object>();
            map.put("meteringTime", conditionMap.get("msgSum"));
            map.put("id", "sum");

            for (String ch : channelIdStrList) {
                sumValue = DecimalUtil.ConvertNumberToDouble(sumListMap.get("SUM_" + ch));
                map.put("channel_" + ch, (sumValue == null) ? "- " : mdf.format(sumValue));
            }

            result.add(map);

            map = new HashMap<String, Object>();
            map.put("meteringTime", conditionMap.get("msgAvg") + "(" + conditionMap.get("msgMax") + "/" + conditionMap.get("msgMin") + ")");
            map.put("id", "avg");

            for (String ch : channelIdStrList) {
                sbAvgValue = new StringBuilder();
                maxValue = DecimalUtil.ConvertNumberToDouble(sumListMap.get("MAX_" + ch));
                minValue = DecimalUtil.ConvertNumberToDouble(sumListMap.get("MIN_" + ch));
                avgValue = DecimalUtil.ConvertNumberToDouble(sumListMap.get("AVG_" + ch));

                if (maxValue != null || minValue != null || avgValue != null) {
                    sbAvgValue.append((avgValue == null) ? "- " : mdf.format(avgValue));
                    sbAvgValue.append("(");
                    sbAvgValue.append((maxValue == null) ? " - " : mdf.format(maxValue));
                    sbAvgValue.append("/");
                    sbAvgValue.append((minValue == null) ? " - " : mdf.format(minValue));
                    sbAvgValue.append(")");
                }
                map.put("channel_" + ch, sbAvgValue.toString());
            }

            result.add(map);
        }
        return result;        
    }
    
    /**
     * method name : getMeteringValueDetailWeekDailyData<b/>
     * method Desc : Metering Data 맥스가젯 상세화면에서 요일별 검침데이터를 조회한다.
     *
     * @param conditionMap
     * @return
     */
    public List<Map<String, Object>> getMeteringValueDetailWeekDailyData(Map<String, Object> conditionMap) {
        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
        Integer supplierId = (Integer)conditionMap.get("supplierId");
        String searchStartDate = (String)conditionMap.get("searchStartDate");
        String[] channelArray = ((String)conditionMap.get("channel")).split(",");
        List<Integer> channelIdList = new ArrayList<Integer>();
        List<String> channelIdStrList = new ArrayList<String>();
        
        for (String obj : channelArray) {
    		if(!channelIdList.contains(Integer.parseInt(obj.replaceAll("mv", "")))) {
    			channelIdList.add(Integer.parseInt(obj.replaceAll("mv", "")));
    		}
        	channelIdStrList.add(obj);
        }

        conditionMap.put("channelIdList", channelIdList);

        List<Map<String, Object>> list = meteringDayDao.getMeteringDataDetailDailyData(conditionMap, false);
        Map<String, Object> listMap = new HashMap<String, Object>();
        Map<String, Object> sumListMap = new HashMap<String, Object>();
        Set<String> dateSet = new LinkedHashSet<String>();
        Map<String, Object> map = null;
        Supplier supplier = supplierDao.get(supplierId);
        String country = supplier.getCountry().getCode_2letter();
        String lang    = supplier.getLang().getCode_2letter();
        DecimalFormat mdf = DecimalUtil.getDecimalFormat(supplier.getMd());
        Double value = null;
        Double maxValue = null;
        Double minValue = null;
        Double avgValue = null;
        Double sumValue = null;
        StringBuilder sbAvgValue = null;

        int year = 0;
        int month = 0;
        int day = 0;
        Calendar cal = null;

        year = Integer.parseInt(searchStartDate.substring(0, 4));
        month = Integer.parseInt(searchStartDate.substring(4, 6))-1;
        day = 1;
        cal = Calendar.getInstance();
        cal.set(year, month, day);
        String startMonth = searchStartDate.substring(0, 6);

        int lastDate = cal.getActualMaximum(Calendar.DAY_OF_MONTH);

        for (int i = 1; i <= lastDate; i++) {
            dateSet.add(startMonth + CalendarUtil.to2Digit(i));
        }

        for (Map<String, Object> obj : list) {
            listMap.put((String)obj.get("YYYYMMDD") + "_" + (Number)obj.get("CHANNEL"), obj.get("VALUE"));
            if("1".equals(obj.get("CHANNEL").toString()) || "2".equals(obj.get("CHANNEL").toString())) {
            	listMap.put((String)obj.get("YYYYMMDD") + "_mv" + (Number)obj.get("CHANNEL"), obj.get("BASEVALUE"));
            }
        }

        Iterator<String> itr = dateSet.iterator();
        String date = null;

        while(itr.hasNext()) {
            date = itr.next();
            map = new HashMap<String, Object>();

            map.put("meteringTime", TimeLocaleUtil.getLocaleWeekDay(date, lang, country));
            
            for (String obj : channelIdStrList) {
                value = DecimalUtil.ConvertNumberToDouble(listMap.get(date + "_" + obj));
                map.put("channel_" + obj, (value == null) ? "- " : mdf.format(value));
            }

            result.add(map);
        }

        List<Map<String, Object>> sumList = meteringDayDao.getMeteringDataDetailDailyData(conditionMap, true);

        if (sumList != null && sumList.size() > 0) {
            for (Map<String, Object> obj : sumList) {
                sumListMap.put("MAX_" + (Number)obj.get("CHANNEL"), obj.get("MAX_VAL"));
                sumListMap.put("MIN_" + (Number)obj.get("CHANNEL"), obj.get("MIN_VAL"));
                sumListMap.put("AVG_" + (Number)obj.get("CHANNEL"), obj.get("AVG_VAL"));
                sumListMap.put("SUM_" + (Number)obj.get("CHANNEL"), obj.get("SUM_VAL"));
                if("1".equals(obj.get("CHANNEL").toString()) || "2".equals(obj.get("CHANNEL").toString())) {
                	sumListMap.put("MAX_mv" + (Number)obj.get("CHANNEL"), obj.get("MAX_BASEVAL"));
                    sumListMap.put("MIN_mv" + (Number)obj.get("CHANNEL"), obj.get("MIN_BASEVAL"));
                }                
            }

            map = new HashMap<String, Object>();
            map.put("meteringTime", conditionMap.get("msgSum"));
            map.put("id", "sum");

            for (String ch : channelIdStrList) {
                sumValue = DecimalUtil.ConvertNumberToDouble(sumListMap.get("SUM_" + ch));
                map.put("channel_" + ch, (sumValue == null) ? "- " : mdf.format(sumValue));
            }

            result.add(map);

            map = new HashMap<String, Object>();
            map.put("meteringTime", conditionMap.get("msgAvg") + "(" + conditionMap.get("msgMax") + "/" + conditionMap.get("msgMin") + ")");
            map.put("id", "avg");

            for (String ch : channelIdStrList) {
                sbAvgValue = new StringBuilder();
                maxValue = DecimalUtil.ConvertNumberToDouble(sumListMap.get("MAX_" + ch));
                minValue = DecimalUtil.ConvertNumberToDouble(sumListMap.get("MIN_" + ch));
                avgValue = DecimalUtil.ConvertNumberToDouble(sumListMap.get("AVG_" + ch));

                if (maxValue != null || minValue != null || avgValue != null) {
                    sbAvgValue.append((avgValue == null) ? "- " : mdf.format(avgValue));
                    sbAvgValue.append("(");
                    sbAvgValue.append((maxValue == null) ? " - " : mdf.format(maxValue));
                    sbAvgValue.append("/");
                    sbAvgValue.append((minValue == null) ? " - " : mdf.format(minValue));
                    sbAvgValue.append(")");
                }
                map.put("channel_" + ch, sbAvgValue.toString());
            }

            result.add(map);
        }
        return result;        
    }
    
    /**
     * method name : getMeteringValueDetailSeasonalData<b/>
     * method Desc : Metering Data 맥스가젯 상세화면에서 계절별 검침데이터를 조회한다.
     *
     * @param conditionMap
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> getMeteringValueDetailSeasonalData(Map<String, Object> conditionMap) {
        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
        List<Map<String, Object>> list = null;
        List<Map<String, Object>> mvList = null;
        Integer supplierId = (Integer)conditionMap.get("supplierId");
        String[] channelArray = ((String)conditionMap.get("channel")).split(",");
        List<Integer> channelIdList = new ArrayList<Integer>();
        List<String> channelIdStrList = new ArrayList<String>();

        for (String obj : channelArray) {
    		if(!channelIdList.contains(Integer.parseInt(obj.replaceAll("mv", "")))) {
    			channelIdList.add(Integer.parseInt(obj.replaceAll("mv", "")));
    		}
        	channelIdStrList.add(obj);
        }

        conditionMap.put("channelIdList", channelIdList);

        Set<String> dateSet = new LinkedHashSet<String>();
        Map<String, Object> listMap = new HashMap<String, Object>();
        Map<String, Object> chMethodMap = new HashMap<String, Object>();
        Map<String, Object> map = null;
        Supplier supplier = supplierDao.get(supplierId);
        DecimalFormat mdf = DecimalUtil.getDecimalFormat(supplier.getMd());
        Double value = null;    // 계산용 임시변수
        Double maxValue = null;
        Double minValue = null;
        Double avgValue = null;
        Double sumValue = null;
        StringBuilder sbAvgValue = null;

        Map<String, Object> seasonsListMap = null;
        List<SeasonData> seasonDataList = null;
        String searchStartDate = (String)conditionMap.get("searchStartDate");
        String searchEndDate = (String)conditionMap.get("searchEndDate");
        boolean hasDay = false;
        int sumLen = 0;     // 계절별 sum 할때 loop 회수 제한

        seasonsListMap = seasonManager.getSeasonDataListByDates(searchStartDate, searchEndDate);
        hasDay = (Boolean)seasonsListMap.get("hasDay");
        seasonDataList = (List<SeasonData>)seasonsListMap.get("seasonDataList");

        if (hasDay) {
            list = meteringDayDao.getMeteringDataDetailDailyData(conditionMap, false);
//            mvList = meteringDayDao.getMeteringValueDetailMonthlyData(conditionMap, false);
            sumLen = 124;   // 일별 데이터로 sum 할때 loop 제한
        } else {
            list = meteringMonthDao.getMeteringDataDetailMonthlyData(conditionMap, false);
            mvList = meteringDayDao.getMeteringValueDetailMonthlyData(conditionMap, false);
            sumLen = 4;     // 월별 데이터로 sum 할때 loop 제한
        }

        if (list == null || list.size() <= 0) {
            return result;
        }

        for (Map<String, Object> obj : list) {
            if (hasDay) {
                listMap.put((String)obj.get("YYYYMMDD") + "_" + (Number)obj.get("CHANNEL"), obj.get("VALUE"));
                dateSet.add((String)obj.get("YYYYMMDD"));
            } else {
                listMap.put((String)obj.get("YYYYMM") + "_" + (Number)obj.get("CHANNEL"), obj.get("VALUE"));
                dateSet.add((String)obj.get("YYYYMM"));
            }
            chMethodMap.put(obj.get("CHANNEL").toString(), obj.get("CH_METHOD"));
        }
        
        for (Map<String, Object> obj : mvList) {
            if("1".equals(obj.get("CHANNEL").toString()) || "2".equals(obj.get("CHANNEL").toString())) {
            	if (hasDay) {
                    listMap.put((String)obj.get("YYYYMMDD") + "_mv" + (Number)obj.get("CHANNEL"), obj.get("BASEVALUE"));
                    dateSet.add((String)obj.get("YYYYMMDD"));
                } else {
                	String yyyymm = obj.get("YYYYMMDD").toString().substring(0,6);
                    listMap.put(yyyymm + "_mv" + (Number)obj.get("CHANNEL"), obj.get("BASEVALUE"));
                    dateSet.add(yyyymm);
                }
                chMethodMap.put("mv"+obj.get("CHANNEL").toString(), obj.get("CH_METHOD"));
            }
        }

        int len = seasonDataList.size();
        String startDate = null;        // season 기간 시작일자. 월별일 경우 yyyyMM, 일별일 경우 yyyyMMdd
        String endDate = null;          // season 기간 종료일자. 월별일 경우 yyyyMM, 일별일 경우 yyyyMMdd
        String curDate = null;
        List<BigDecimal> bdTotalSumList = new ArrayList<BigDecimal>();
        List<BigDecimal> bdTotalMaxList = new ArrayList<BigDecimal>();
        List<BigDecimal> bdTotalMinList = new ArrayList<BigDecimal>();
        List<Integer> intTotalCount = new ArrayList<Integer>();
        List<BigDecimal> bdSeasonValueList = new ArrayList<BigDecimal>();
        int chIndex = 0;
        int cnt = 0;
        SeasonData seasonData = null;

        for (int k = 0; k < len; k++) {
            seasonData = seasonDataList.get(k);
            map = new HashMap<String, Object>();
            startDate = null;
            endDate = null;
            curDate = null;
            cnt = 0;

            if (hasDay) {
                startDate = seasonData.getStartDate();
                endDate = seasonData.getEndDate();
            } else {
                startDate = seasonData.getStartDate().substring(0, 6);
                endDate = seasonData.getEndDate().substring(0, 6);
            }
            curDate = startDate;
            bdSeasonValueList = new ArrayList<BigDecimal>();

            for (int l = 0; l < sumLen; l++) {   // 계절 단위로 sum
                chIndex = 0;

                for (String obj : channelIdStrList) { // 각 채널별 sum
                    value = DecimalUtil.ConvertNumberToDouble(listMap.get(curDate + "_" + obj));

                    if (l == 0) {
                        if (value != null) {
                            bdSeasonValueList.add(new BigDecimal(value.toString()));
                            cnt++;
                        } else {
                            bdSeasonValueList.add(null);
                        }
                    } else {
                        if (value != null) {
                            if (bdSeasonValueList.get(chIndex) == null) {
                                bdSeasonValueList.set(chIndex, new BigDecimal(value.toString()));
                            } else {
                                if (obj.contains("mv") || 
                                		ChannelCalcMethod.MAX.name().equals((String)chMethodMap.get(obj.toString()))) {     // MAX
                                    bdSeasonValueList.set(chIndex, bdSeasonValueList.get(chIndex).max(new BigDecimal(value.toString())));
                                } else {        // SUM, AVG
                                    bdSeasonValueList.set(chIndex, bdSeasonValueList.get(chIndex).add(new BigDecimal(value.toString())));
                                }
                            }
                            cnt++;
                        }
                    }
                    chIndex++;
                }

                if (curDate.equals(endDate)) {
                    break;
                } else {
                    if (hasDay) {
                        curDate = CalendarUtil.getDate(curDate, Calendar.DAY_OF_MONTH, 1);
                    } else {
                        curDate = CalendarUtil.getDate(curDate + "01", Calendar.MONTH, 1).substring(0, 6);
                    }
                }
            }

            chIndex = 0;

            for (String obj : channelIdStrList) {

                if (bdSeasonValueList.get(chIndex) != null) {
                    if (ChannelCalcMethod.AVG.name().equals((String)chMethodMap.get(obj.toString()))) {     // AVG
                        bdSeasonValueList.set(chIndex, bdSeasonValueList.get(chIndex).divide(new BigDecimal(cnt+""), MathContext.DECIMAL32));
                    }
                }

                if (k == 0) {
                    if (bdSeasonValueList.get(chIndex) != null) {
                        bdTotalSumList.add(bdSeasonValueList.get(chIndex));
                        bdTotalMaxList.add(bdSeasonValueList.get(chIndex));
                        bdTotalMinList.add(bdSeasonValueList.get(chIndex));
                        intTotalCount.add(1);
                    } else {
                        bdTotalSumList.add(null);
                        bdTotalMaxList.add(null);
                        bdTotalMinList.add(null);
                        intTotalCount.add(0);
                    }
                } else {
                    if (bdSeasonValueList.get(chIndex) != null) {
                        if (bdTotalSumList.get(chIndex) == null) {
                            bdTotalSumList.set(chIndex, bdSeasonValueList.get(chIndex));
                        } else {
                            bdTotalSumList.set(chIndex, bdTotalSumList.get(chIndex).add(bdSeasonValueList.get(chIndex)));
                        }

                        if (bdTotalMaxList.get(chIndex) == null) {
                            bdTotalMaxList.set(chIndex, bdSeasonValueList.get(chIndex));
                        } else {
                            bdTotalMaxList.set(chIndex, bdTotalMaxList.get(chIndex).max(bdSeasonValueList.get(chIndex)));
                        }

                        if (bdTotalMinList.get(chIndex) == null) {
                            bdTotalMinList.set(chIndex, bdSeasonValueList.get(chIndex));
                        } else {
                            bdTotalMinList.set(chIndex, bdTotalMinList.get(chIndex).min(bdSeasonValueList.get(chIndex)));
                        }
                        intTotalCount.set(chIndex, intTotalCount.get(chIndex) + 1);
                    }
                }

                value = (bdSeasonValueList.get(chIndex) == null) ? null : bdSeasonValueList.get(chIndex).doubleValue();
                map.put("channel_" + obj, (value == null) ? "- " : mdf.format(value));
                chIndex++;
            }

            map.put("meteringTime", startDate.substring(0, 4) + " " + seasonData.getName());
            result.add(map);
        }

        if (bdTotalSumList != null && bdTotalSumList.size() > 0) {
            map = new HashMap<String, Object>();
            map.put("meteringTime", conditionMap.get("msgSum"));
            map.put("id", "sum");

            chIndex = 0;
            for (String ch : channelIdStrList) {
            	if(ch.contains("mv")) {
            		sumValue = (bdTotalSumList.get(chIndex) == null) ? null : bdTotalSumList.get(chIndex).doubleValue();
                    map.put("channel_" + ch, (sumValue == null) ? "- " : mdf.format(sumValue));
            	} else {
                    map.put("channel_mv" + ch, "- ");
            	}
            	chIndex++;
            }

            result.add(map);

            map = new HashMap<String, Object>();
            map.put("meteringTime", conditionMap.get("msgAvg") + "(" + conditionMap.get("msgMax") + "/" + conditionMap.get("msgMin") + ")");
            map.put("id", "avg");

            chIndex = 0;
            for (String ch : channelIdStrList) {
                sbAvgValue = new StringBuilder();
                maxValue = (bdTotalMaxList.get(chIndex) == null) ? null : bdTotalMaxList.get(chIndex).doubleValue();
                minValue = (bdTotalMinList.get(chIndex) == null) ? null : bdTotalMinList.get(chIndex).doubleValue();

                if (bdTotalSumList.get(chIndex) == null || intTotalCount.get(chIndex) == null || ch.contains("mv")) {
                    avgValue = null;
                } else if (intTotalCount.get(chIndex) == 0) {
                    avgValue = 0D;
                } else {
                    avgValue = bdTotalSumList.get(chIndex).divide(new BigDecimal(intTotalCount.get(chIndex)), MathContext.DECIMAL32).doubleValue();
                }

                if (maxValue != null || minValue != null || avgValue != null) {
                    sbAvgValue.append((avgValue == null) ? "- " : mdf.format(avgValue));
                    sbAvgValue.append("(");
                    sbAvgValue.append((maxValue == null) ? " - " : mdf.format(maxValue));
                    sbAvgValue.append("/");
                    sbAvgValue.append((minValue == null) ? " - " : mdf.format(minValue));
                    sbAvgValue.append(")");
                }
                map.put("channel_" + ch, sbAvgValue.toString());
                chIndex++;
            }
            result.add(map);
        }

        return result;
    }
    
    /**
     * method name : getMeteringDataDetailLpData<b/>
     * method Desc : Metering Data 맥스가젯 상세화면에서 주기별 검침데이터를 조회한다.
     *
     * @param conditionMap
     * @return
     */
    public List<Map<String, Object>> getMeteringValueDetailLpData(Map<String, Object> conditionMap) {
        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
        Integer supplierId = (Integer)conditionMap.get("supplierId");
        Integer dst = (Integer)conditionMap.get("dst");
        String searchStartDate = (String)conditionMap.get("searchStartDate");
        String searchEndDate = (String)conditionMap.get("searchEndDate");
        String searchStartHour = StringUtil.nullToBlank(conditionMap.get("searchStartHour"));
        String searchEndHour = StringUtil.nullToBlank(conditionMap.get("searchEndHour"));
        String[] channelArray = ((String)conditionMap.get("channel")).split(",");
        List<Integer> channelIdList = new ArrayList<Integer>();
        List<String> channelIdStrList = new ArrayList<String>();

        for (String obj : channelArray) {
    		if(!channelIdList.contains(Integer.parseInt(obj.replaceAll("mv", "")))) {
    			channelIdList.add(Integer.parseInt(obj.replaceAll("mv", "")));
    		}
        	channelIdStrList.add(obj);
        }

        conditionMap.put("channelIdList", channelIdList);

        Meter meter = mtrDao.findByCondition("mdsId", (String)conditionMap.get("meterNo"));
        Integer lpInterval = (meter.getLpInterval() == null) ? 60 : meter.getLpInterval();

        List<Map<String, Object>> list = meteringLpDao.getMeteringDataDetailLpData(conditionMap);
        Map<String, Object> map = null;
        Map<String, Double> listMap = new HashMap<String, Double>();
        Set<String> dateSet = new LinkedHashSet<String>();

        Supplier supplier = supplierDao.get(supplierId);
        String country = supplier.getCountry().getCode_2letter();
        String lang    = supplier.getLang().getCode_2letter();
        DecimalFormat mdf = DecimalUtil.getDecimalFormat(supplier.getMd());
        Double tmpValue = null;
        String lpmin = null;
        String tmpLocaleDateHour = searchStartDate + searchStartHour;

        // 조회조건 내 모든 일자 가져오기
        for (int k = 0; k < 100; k++) {     // 무한 loop 방지
            for (int i = 0, j = 0 ; j < 60 ; i++, j = i * lpInterval) {
                dateSet.add(tmpLocaleDateHour + CalendarUtil.to2Digit(j));
            }

            if (tmpLocaleDateHour.compareTo(searchEndDate + searchEndHour) >= 0) {  // 종료일자이면 종료
                break;
            } else {
                try {
                    tmpLocaleDateHour = DateTimeUtil.getPreHour(tmpLocaleDateHour + "0000", -1).substring(0, 10);   // +1 시간 더함
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }

        for (Map<String, Object> obj : list) {
            for (int i = 0, j = 0 ; j < 60 ; i++, j = i * lpInterval) {
                lpmin = CalendarUtil.to2Digit(j);
                tmpValue = DecimalUtil.ConvertNumberToDouble(obj.get("value_" + lpmin));
                listMap.put((String)obj.get("yyyymmddhh") + lpmin + "_" + obj.get("channel"), tmpValue);
            }
            /*
             * Meter Value의 경우 시간별 데이터가 아니니까 이 데이터가 나오지 않는게 맞는것이라 판단.
            if("1".equals(obj.get("channel").toString()) || "2".equals(obj.get("channel").toString())) {
            	tmpValue = DecimalUtil.ConvertNumberToDouble(obj.get("value"));
                listMap.put((String)obj.get("yyyymmddhh") + "00" + "_mv" + obj.get("channel"), tmpValue);
            }*/
        }

        for (String lpDate : dateSet) {
            map = new HashMap<String, Object>();
            map.put("id", (dst != null) ? lpDate + dst.toString() : lpDate);
            map.put("meteringTime", TimeLocaleUtil.getLocaleDate(lpDate + "00", lang, country));

            for (String ch : channelIdStrList) {
                tmpValue = listMap.get(lpDate + "_" + ch);
                map.put("channel_" + ch, (tmpValue == null) ? "- " : mdf.format(tmpValue));
            }
            
            map.put("iconCls", "task");
            map.put("iconCls", "no-icon");
            map.put("leaf", true);

            result.add(map);
        }
        return result;        
    }
    
    /**
     * method name : getMeteringValueDataDailyData<b/>
     * method Desc : Metering Data 맥스가젯에서 일별 지침값을 조회한다.
     *
     * @param conditionMap
     * @return
     */
    public List<Map<String, Object>> getMeteringValueDataDailyData(Map<String, Object> conditionMap) {
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
        String searchEndDate = (String)conditionMap.get("searchEndDate");
        String searchPrevStartDate = null;

        if (page != null && limit != null) {        // paging
            conditionMap.put("startDate", searchStartDate);
        } else {        // all
            try {
                searchPrevStartDate = TimeUtil.getPreDay(searchStartDate).substring(0, 8);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            conditionMap.put("startDate", searchPrevStartDate);
        }

        conditionMap.put("endDate", searchEndDate);

        List<Map<String, Object>> list = meteringDayDao.getMeteringDataDailyData(conditionMap, false);
        List<Map<String, Object>> prevList = new ArrayList<Map<String, Object>>();
        Map<String, Object> listMap = new HashMap<String, Object>();
        Map<String, Object> map = null;

        Supplier supplier = supplierDao.get(supplierId);
        String country = supplier.getCountry().getCode_2letter();
        String lang    = supplier.getLang().getCode_2letter();
        DecimalFormat mdf = DecimalUtil.getDecimalFormat(supplier.getMd());
        Double prevValue = null;

        if (page != null && limit != null) {        // paging
            int cnt = 0;
            String prevStartDate = null;
            String prevEndDate = null;
            Map<String, Object> fstMap = null;
            Map<String, Object> lstMap = null;

            if (list != null && list.size() > 0) {
                cnt = list.size();
                fstMap = list.get(0);
                lstMap = list.get(cnt-1);
                try {
                    prevStartDate = TimeUtil.getPreDay((String)fstMap.get("YYYYMMDD")).substring(0, 8);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                prevEndDate = (String)lstMap.get("YYYYMMDD");

                conditionMap.put("prevStartDate", prevStartDate);
                conditionMap.put("prevEndDate", prevEndDate);

                prevList = meteringDayDao.getMeteringDataDailyData(conditionMap, false, true);

                for (Map<String, Object> obj : prevList) {
                    listMap.put((String)obj.get("YYYYMMDD") + "_" + (String)obj.get("METER_NO"), obj.get("BASEVALUE"));
                }
            }
        } else {        // all
            for (Map<String, Object> obj : list) {
                listMap.put((String)obj.get("YYYYMMDD") + "_" + (String)obj.get("METER_NO"), obj.get("BASEVALUE"));
            }

            if ("대성에너지".equals(supplier.getName())) {
                conditionMap.put("startDate", searchStartDate.substring(0, 6));
                conditionMap.put("endDate", searchEndDate.substring(0, 6));
                conditionMap.put("startDetailDate", searchStartDate.substring(6, 8));
                conditionMap.put("endDetailDate", searchEndDate.substring(6, 8));

                List<Map<String, Object>> accumulateList = meteringMonthDao.getMeteringDataMonthlyChannel2Data(conditionMap);

                for (Map<String, Object> obj : accumulateList) {
                    listMap.put((String)obj.get("METER_NO")+"_ACCUMULATE", obj.get("ACCUMULATEVALUE"));
                }
            }
        }

        int num = 0;

        if (page != null && limit != null) {
            num = ((page - 1) * limit) + 1;
        } else {
            num = 1;
        }

        for (Map<String, Object> obj : list) {
            // 전체조회일 경우 이전일자 데이터는 skip
            if ((page == null || limit == null) && ((String)obj.get("YYYYMMDD")).compareTo(searchPrevStartDate) == 0) {
                continue;
            }

            map = new HashMap<String, Object>();

            map.put("num", num++);
            map.put("contractNumber", (String)obj.get("CONTRACT_NUMBER"));
            map.put("customerName", (String)obj.get("CUSTOMER_NAME"));
            map.put("friendlyName", (String)obj.get("FRIENDLY_NAME"));
            map.put("meteringTime", TimeLocaleUtil.getLocaleDate((String)obj.get("YYYYMMDD"), lang, country));

            map.put("meterNo", (String)obj.get("METER_NO"));
            map.put("modemId", (String)obj.get("MODEM_ID"));
            map.put("sicName", (String)obj.get("SIC_NAME"));
            map.put("value", mdf.format(DecimalUtil.ConvertNumberToDouble(obj.get("BASEVALUE"))));

            Double accumulate = null;

            try {
                prevValue = DecimalUtil.ConvertNumberToDouble(listMap.get(TimeUtil.getPreDay((String)obj.get("YYYYMMDD")).substring(0, 8) + "_" + (String)obj.get("METER_NO")));
                if ("대성에너지".equals(supplier.getName()))
                    accumulate = DecimalUtil.ConvertNumberToDouble(listMap.get((String)obj.get("METER_NO")+"_ACCUMULATE"));
            } catch (ParseException e) {
                logger.error(e,e);
            }

            map.put("prevValue", (prevValue == null) ? "" : mdf.format(prevValue));
            if ("대성에너지".equals(supplier.getName()))
                map.put("accumulateValue", (accumulate == null) ? "" : mdf.format(accumulate));
            result.add(map);
        }

        return result;
    }
    
    /**
     * method name : getMeteringValueDataDailyDataTotalCount<b/>
     * method Desc : Metering Data 맥스가젯에서 일별 지침값의 total count 를 조회한다.
     *
     * @param conditionMap
     * @return
     */
    public Integer getMeteringValueDataDailyDataTotalCount(Map<String, Object> conditionMap) {
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

        conditionMap.put("startDate", (String)conditionMap.get("searchStartDate"));
        conditionMap.put("endDate", (String)conditionMap.get("searchEndDate"));

        List<Map<String, Object>> result = meteringDayDao.getMeteringDataDailyData(conditionMap, true);
        return (Integer)(result.get(0).get("total"));
    }
    
    /**
     * method name : getMeteringValueDataWeeklyData<b/>
     * method Desc : Metering Data 맥스가젯에서 주별 검침데이터를 조회한다.
     *
     * @param conditionMap
     * @return
     */
    public List<Map<String, Object>> getMeteringValueDataWeeklyData(Map<String, Object> conditionMap) {
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
        String searchEndDate = (String)conditionMap.get("searchEndDate");
        String searchWeek = (String)conditionMap.get("searchWeek");

        conditionMap.put("startDate", searchStartDate);
        conditionMap.put("endDate", searchEndDate);

        List<Map<String, Object>> list = meteringDayDao.getMeteringDataWeeklyData(conditionMap, false);
        List<Map<String, Object>> prevList = new ArrayList<Map<String, Object>>();
        Map<String, Object> listMap = new HashMap<String, Object>();
        Map<String, Object> map = null;

        Supplier supplier = supplierDao.get(supplierId);
        DecimalFormat mdf = DecimalUtil.getDecimalFormat(supplier.getMd());
        Double prevValue = null;
        Set<String> meterNoList = new HashSet<String>();

        if (list != null && list.size() > 0) {
            if (page != null && limit != null) {        // paging
                for (Map<String, Object> obj : list) {
                    meterNoList.add((String)obj.get("METER_NO"));
                }
                conditionMap.put("meterNoList", meterNoList);
            }

            String prevDate = null; 
            Map<String, String> prevWeek = null;

            try {
                // 조회시작일자 이전일자
                prevDate = TimeUtil.getPreDay(searchStartDate).substring(0, 8);

                // 이전일자 주차 구하기
                int weekNum = CalendarUtil.getWeekOfMonth(prevDate);

                // 주차에 해당하는 from to
                prevWeek = CalendarUtil.getDateWeekOfMonth(prevDate.substring(0, 4), prevDate.substring(4, 6), weekNum+"");
            } catch (ParseException e) {
                e.printStackTrace();
            }

            conditionMap.put("prevStartDate", prevWeek.get("startDate"));
            conditionMap.put("prevEndDate", prevWeek.get("endDate"));

            prevList = meteringDayDao.getMeteringDataWeeklyData(conditionMap, false, true);

            for (Map<String, Object> obj : prevList) {
                listMap.put((String)obj.get("METER_NO"), obj.get("BASEVALUE"));
            }

			if("대성에너지".equals(supplier.getName())) {
            	conditionMap.put("startDate", searchStartDate.substring(0, 6));
            	conditionMap.put("endDate", searchEndDate.substring(0, 6));
            	conditionMap.put("startDetailDate", searchStartDate.substring(6, 8));
            	conditionMap.put("endDetailDate", searchEndDate.substring(6, 8));
            
            	List<Map<String, Object>> accumulateList = meteringMonthDao.getMeteringDataMonthlyChannel2Data(conditionMap);
            
            	for (Map<String, Object> obj : accumulateList) {
                	listMap.put((String)obj.get("METER_NO")+"_ACCUMULATE", obj.get("ACCUMULATEVALUE"));
            	}
            }
        }

        int num = 0;

        if (page != null && limit != null) {
            num = ((page - 1) * limit) + 1;
        } else {
            num = 1;
        }

        for (Map<String, Object> obj : list) {
            map = new HashMap<String, Object>();
            Double accumulate = null;
            
            map.put("num", num++);
            map.put("contractNumber", (String)obj.get("CONTRACT_NUMBER"));
            map.put("customerName", (String)obj.get("CUSTOMER_NAME"));
            map.put("meteringTime", searchWeek + " Week");
            map.put("meterNo", (String)obj.get("METER_NO"));
            map.put("modemId", (String)obj.get("MODEM_ID"));
            map.put("sicName", (String)obj.get("SIC_NAME"));
            map.put("value", mdf.format(DecimalUtil.ConvertNumberToDouble(obj.get("BASEVALUE"))));

            prevValue = DecimalUtil.ConvertNumberToDouble(listMap.get((String)obj.get("METER_NO")));
            if("대성에너지".equals(supplier.getName())) 
            	accumulate = DecimalUtil.ConvertNumberToDouble(listMap.get((String)obj.get("METER_NO")+"_ACCUMULATE"));
            
            map.put("prevValue", (prevValue == null) ? "" : mdf.format(prevValue));
            if("대성에너지".equals(supplier.getName())) 
            	map.put("accumulateValue", (accumulate == null) ? "" : mdf.format(accumulate));
            result.add(map);
        }

        return result;
    }

    /**
     * method name : getMeteringValueDataWeeklyDataTotalCount<b/>
     * method Desc : Metering Data 맥스가젯에서 주별 지침값의 total count 를 조회한다.
     *
     * @param conditionMap
     * @return
     */
    public Integer getMeteringValueDataWeeklyDataTotalCount(Map<String, Object> conditionMap) {
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

        conditionMap.put("meterNoList", null);
        conditionMap.put("startDate", (String)conditionMap.get("searchStartDate"));
        conditionMap.put("endDate", (String)conditionMap.get("searchEndDate"));

        List<Map<String, Object>> result = meteringDayDao.getMeteringDataWeeklyData(conditionMap, true);
        return (Integer)(result.get(0).get("total"));
    }
    
    /**
     * method name : getMeteringValueDataWeekDailyData<b/>
     * method Desc : Metering Data 맥스가젯에서 요일별 지침값을 조회한다.
     *
     * @param conditionMap
     * @return
     */
    public List<Map<String, Object>> getMeteringValueDataWeekDailyData(Map<String, Object> conditionMap) {
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
        String searchEndDate = (String)conditionMap.get("searchEndDate");
        String searchPrevStartDate = null;

        if (page != null && limit != null) {        // paging
            conditionMap.put("startDate", searchStartDate);
        } else {        // all
            try {
                searchPrevStartDate = TimeUtil.getPreDay(searchStartDate).substring(0, 8);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            conditionMap.put("startDate", searchPrevStartDate);
        }

        conditionMap.put("endDate", searchEndDate);

        List<Map<String, Object>> list = meteringDayDao.getMeteringDataDailyData(conditionMap, false);
        List<Map<String, Object>> prevList = new ArrayList<Map<String, Object>>();
        Map<String, Object> listMap = new HashMap<String, Object>();
        Map<String, Object> map = null;

        Supplier supplier = supplierDao.get(supplierId);
        String country = supplier.getCountry().getCode_2letter();
        String lang    = supplier.getLang().getCode_2letter();
        DecimalFormat mdf = DecimalUtil.getDecimalFormat(supplier.getMd());
        Double prevValue = null;
        int num = 0;

        if (page != null && limit != null) {        // paging
            int cnt = 0;
            String prevStartDate = null;
            String prevEndDate = null;
            Map<String, Object> fstMap = null;
            Map<String, Object> lstMap = null;

            if (list != null && list.size() > 0) {
                cnt = list.size();
                fstMap = list.get(0);
                lstMap = list.get(cnt-1);
                try {
                    prevStartDate = TimeUtil.getPreDay((String)fstMap.get("YYYYMMDD")).substring(0, 8);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                prevEndDate = (String)lstMap.get("YYYYMMDD");

                conditionMap.put("prevStartDate", prevStartDate);
                conditionMap.put("prevEndDate", prevEndDate);

                prevList = meteringDayDao.getMeteringDataDailyData(conditionMap, false, true);

                for (Map<String, Object> obj : prevList) {
                    listMap.put((String)obj.get("YYYYMMDD") + "_" + (String)obj.get("METER_NO"), obj.get("BASEVALUE"));
                }
            }
        } else {        // all
            for (Map<String, Object> obj : list) {
                listMap.put((String)obj.get("YYYYMMDD") + "_" + (String)obj.get("METER_NO"), obj.get("BASEVALUE"));
            }
        }

        if (page != null && limit != null) {
            num = ((page - 1) * limit) + 1;
        } else {
            num = 1;
        }

        for (Map<String, Object> obj : list) {
            // 전체조회일 경우 이전일자 데이터는 skip
            if ((page == null || limit == null) && ((String)obj.get("YYYYMMDD")).compareTo(searchPrevStartDate) == 0) {
                continue;
            }

            map = new HashMap<String, Object>();

            map.put("num", num++);
            map.put("contractNumber", (String)obj.get("CONTRACT_NUMBER"));
            map.put("customerName", (String)obj.get("CUSTOMER_NAME"));
            map.put("meteringTime", TimeLocaleUtil.getLocaleWeekDay((String)obj.get("YYYYMMDD"), lang, country));
            map.put("meterNo", (String)obj.get("METER_NO"));
            map.put("modemId", (String)obj.get("MODEM_ID"));
            map.put("sicName", (String)obj.get("SIC_NAME"));       
            map.put("value", mdf.format(DecimalUtil.ConvertNumberToDouble(obj.get("BASEVALUE"))));

            try {
                prevValue = DecimalUtil.ConvertNumberToDouble(listMap.get(TimeUtil.getPreDay((String)obj.get("YYYYMMDD")).substring(0, 8) + "_" + (String)obj.get("METER_NO")));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            map.put("prevValue", (prevValue == null) ? "" : mdf.format(prevValue));
            result.add(map);
        }

        return result;
    }

    /**
     * method name : getMeteringValueDataWeekDailyDataTotalCount<b/>
     * method Desc : Metering Data 맥스가젯에서 요일별 지침값의 total count 를 조회한다.
     *
     * @param conditionMap
     * @return
     */
    public Integer getMeteringValueDataWeekDailyDataTotalCount(Map<String, Object> conditionMap) {
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

        conditionMap.put("startDate", (String)conditionMap.get("searchStartDate"));
        conditionMap.put("endDate", (String)conditionMap.get("searchEndDate"));

        List<Map<String, Object>> result = meteringDayDao.getMeteringDataDailyData(conditionMap, true);
        return (Integer)(result.get(0).get("total"));
    }
    
    /**
    * method name : getMeteringValueDataSeasonalData<b/>
    * method Desc : Metering Data 맥스가젯에서 계절별 지침값을 조회한다.
    *
    * @param conditionMap
    * @return
    */
   public List<Map<String, Object>> getMeteringValueDataSeasonalData(Map<String, Object> conditionMap) {
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
       String searchEndDate = (String)conditionMap.get("searchEndDate");
       String searchSeason = null;
       Boolean hasDay = false;     // 계절데이터에 일자 포함 여부. 있으면 일자로 조회, 없으면 월로 조회.
       Map<String, String> seasonMap = seasonManager.getSeasonPeriodByDate(searchStartDate);

       if (seasonMap != null) {
           searchSeason = seasonMap.get("seasonName");
           hasDay = Boolean.valueOf(seasonMap.get("hasDay"));
       }
       
       List<Map<String, Object>> list = null;
       
       if (hasDay) {   // 계절기간에 일자가 포함되어있을 경우 일자로 조회
           conditionMap.put("startDate", searchStartDate);
           conditionMap.put("endDate", searchEndDate);
           list = meteringDayDao.getMeteringDataWeeklyData(conditionMap, false);   // weekly 와 조회 query 가 동일하므로 재사용
       } else {        // 계절기간에 일자가 포함안되어있을 경우 월로 조회
           conditionMap.put("startDate", searchStartDate.substring(0, 6));
           conditionMap.put("endDate", searchEndDate.substring(0, 6));
           list = meteringDayDao.getMeteringValueYearlyData(conditionMap, false); // yearly 와 조회 query 가 동일하므로 재사용
       }

       List<Map<String, Object>> prevList = new ArrayList<Map<String, Object>>();
       Map<String, Object> listMap = new HashMap<String, Object>();
       Map<String, Object> map = null;

       Supplier supplier = supplierDao.get(supplierId);
       DecimalFormat mdf = DecimalUtil.getDecimalFormat(supplier.getMd());
       Double prevValue = null;
       Set<String> meterNoList = new HashSet<String>();

       if (list != null && list.size() > 0) {
           if (page != null && limit != null) {        // paging
               for (Map<String, Object> obj : list) {
                   meterNoList.add((String)obj.get("METER_NO"));
               }
               conditionMap.put("meterNoList", meterNoList);
           }

           String prevDate = null; 
           Map<String, String> prevSeasonMap = null;

           try {
               // 조회시작일자 이전일자
               prevDate = TimeUtil.getPreDay(searchStartDate).substring(0, 8);

               // 이전일자 계절주기 구하기
               prevSeasonMap = seasonManager.getSeasonPeriodByDate(prevDate);
           } catch (ParseException e) {
               e.printStackTrace();
           }

           if (hasDay) {   // 계절기간에 일자가 포함되어있을 경우 일자로 조회
               conditionMap.put("prevStartDate", prevSeasonMap.get("startDate"));
               conditionMap.put("prevEndDate", prevSeasonMap.get("endDate"));
               prevList = meteringDayDao.getMeteringDataWeeklyData(conditionMap, false, true);     // weekly 와 조회 query 가 동일하므로 재사용
           } else {        // 계절기간에 일자가 포함안되어있을 경우 월로 조회
               conditionMap.put("prevStartDate", prevSeasonMap.get("startDate").substring(0, 6));
               conditionMap.put("prevEndDate", prevSeasonMap.get("endDate").substring(0, 6));
               prevList = meteringMonthDao.getMeteringDataYearlyData(conditionMap, false, true);   // yearly 와 조회 query 가 동일하므로 재사용
           }

           for (Map<String, Object> obj : prevList) {
               listMap.put((String)obj.get("METER_NO"), obj.get("BASEVALUE"));
           }
       }

       int num = 0;
       
       if (page != null && limit != null) {
           num = ((page - 1) * limit) + 1;
       } else {
           num = 1;
       }

       for (Map<String, Object> obj : list) {
           map = new HashMap<String, Object>();

           map.put("num", num++);
           map.put("contractNumber", (String)obj.get("CONTRACT_NUMBER"));
           map.put("customerName", (String)obj.get("CUSTOMER_NAME"));
           map.put("meteringTime", searchSeason);
           map.put("meterNo", (String)obj.get("METER_NO"));
           map.put("modemId", (String)obj.get("MODEM_ID"));
           map.put("sicName", (String)obj.get("SIC_NAME"));       
           map.put("value", mdf.format(DecimalUtil.ConvertNumberToDouble(obj.get("BASEVALUE"))));

           prevValue = DecimalUtil.ConvertNumberToDouble(listMap.get((String)obj.get("METER_NO")));

           map.put("prevValue", (prevValue == null) ? "" : mdf.format(prevValue));
           result.add(map);
       }

       return result;
   }

   /**
    * method name : getMeteringValueDataSeasonalDataTotalCount<b/>
    * method Desc : Metering Data 맥스가젯에서 계절별 지침값의 total count 를 조회한다.
    *
    * @param conditionMap
    * @return
    */
   public Integer getMeteringValueDataSeasonalDataTotalCount(Map<String, Object> conditionMap) {
       Integer locationId = (Integer)conditionMap.get("locationId");
       Integer permitLocationId = (Integer)conditionMap.get("permitLocationId");
       List<Integer> locationIdList = null;
       String searchStartDate = (String)conditionMap.get("searchStartDate");
       String searchEndDate = (String)conditionMap.get("searchEndDate");

       if (locationId != null) {
           locationIdList = locationDao.getChildLocationId(locationId);
           locationIdList.add(locationId);
           conditionMap.put("locationIdList", locationIdList);
       } else if (permitLocationId != null) {
           locationIdList = locationDao.getChildLocationId(permitLocationId);
           locationIdList.add(permitLocationId);
           conditionMap.put("locationIdList", locationIdList);
       }

       conditionMap.put("meterNoList", null);
       Boolean hasDay = false;     // 계절데이터에 일자 포함 여부. 있으면 일자로 조회, 없으면 월로 조회.
       Map<String, String> seasonMap = seasonManager.getSeasonPeriodByDate(searchStartDate);
       List<Map<String, Object>> result = null;

       if (seasonMap != null) {
           hasDay = Boolean.valueOf(seasonMap.get("hasDay"));
       }

       if (hasDay) {   // 계절기간에 일자가 포함되어있을 경우 일자로 조회
           conditionMap.put("startDate", searchStartDate);
           conditionMap.put("endDate", searchEndDate);
           result = meteringDayDao.getMeteringDataWeeklyData(conditionMap, true);   // weekly 와 조회 query 가 동일하므로 재사용
       } else {        // 계절기간에 일자가 포함안되어있을 경우 월로 조회
           conditionMap.put("startDate", searchStartDate.substring(0, 6));
           conditionMap.put("endDate", searchEndDate.substring(0, 6));
           result = meteringDayDao.getMeteringValueYearlyData(conditionMap, true); // yearly 와 조회 query 가 동일하므로 재사용
       }

       return (Integer)(result.get(0).get("total"));
   }
   
   /**
    * method name : getMeteringValueDataYearlyData<b/>
    * method Desc : Metering Data 맥스가젯에서 연간 지침값을 조회한다.
    *
    * @param conditionMap
    * @return
    */
   public List<Map<String, Object>> getMeteringValueDataYearlyData(Map<String, Object> conditionMap) {
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
       String searchEndDate = (String)conditionMap.get("searchEndDate");
       String searchYear = searchStartDate.substring(0, 4);

       conditionMap.put("startDate", searchStartDate.substring(0, 6)+"01");
       conditionMap.put("endDate", searchEndDate.substring(0, 6)+"31");

       List<Map<String, Object>> list = meteringDayDao.getMeteringValueYearlyData(conditionMap, false);
       List<Map<String, Object>> prevList = new ArrayList<Map<String, Object>>();
       Map<String, Object> listMap = new HashMap<String, Object>();
       Map<String, Object> map = null;

       Supplier supplier = supplierDao.get(supplierId);
       DecimalFormat mdf = DecimalUtil.getDecimalFormat(supplier.getMd());
       Double value = null;
       Double prevValue = null;
       Set<String> meterNoList = new HashSet<String>();

       if (list != null && list.size() > 0) {
           if (page != null && limit != null) {        // paging
               for (Map<String, Object> obj : list) {
                   meterNoList.add((String)obj.get("METER_NO"));
               }
               conditionMap.put("meterNoList", meterNoList);
           }
           String prevYear = null; 

           try {
               // 조회이전 년도
               prevYear = TimeUtil.getPreMonth(searchStartDate).substring(0, 4);
           } catch (ParseException e) {
               e.printStackTrace();
           }

           conditionMap.put("prevStartDate", prevYear + "01");
           conditionMap.put("prevEndDate", prevYear + "12");

           prevList = meteringDayDao.getMeteringValueYearlyData(conditionMap, false, true);

           for (Map<String, Object> obj : prevList) {
               listMap.put((String)obj.get("METER_NO"), obj.get("BASEVALUE"));
           }
       }

       int num = 0;
       
       if (page != null && limit != null) {
           num = ((page - 1) * limit) + 1;
       } else {
           num = 1;
       }

       for (Map<String, Object> obj : list) {
           map = new HashMap<String, Object>();

           map.put("num", num++);
           map.put("contractNumber", (String)obj.get("CONTRACT_NUMBER"));
           map.put("customerName", (String)obj.get("CUSTOMER_NAME"));
           map.put("meteringTime", searchYear);
           map.put("meterNo", (String)obj.get("METER_NO"));
           map.put("modemId", (String)obj.get("MODEM_ID"));
           map.put("sicName", (String)obj.get("SIC_NAME"));       
           
           value = DecimalUtil.ConvertNumberToDouble(obj.get("BASEVALUE"));
           map.put("value", (value == null) ? "" : mdf.format(value));

           prevValue = DecimalUtil.ConvertNumberToDouble(listMap.get((String)obj.get("METER_NO")));
           map.put("prevValue", (prevValue == null) ? "" : mdf.format(prevValue));
           result.add(map);
       }

       return result;        
   }

   /**
    * method name : getMeteringValueDataYearlyDataTotalCount<b/>
    * method Desc : Metering Data 맥스가젯에서 연간 지침값의 total count 를 조회한다.
    *
    * @param conditionMap
    * @return
    */
   public Integer getMeteringValueDataYearlyDataTotalCount(Map<String, Object> conditionMap) {
       Integer locationId = (Integer)conditionMap.get("locationId");
       Integer permitLocationId = (Integer)conditionMap.get("permitLocationId");
       List<Integer> locationIdList = null;
       String searchStartDate = (String)conditionMap.get("searchStartDate");
       String searchEndDate = (String)conditionMap.get("searchEndDate");

       if (locationId != null) {
           locationIdList = locationDao.getChildLocationId(locationId);
           locationIdList.add(locationId);
           conditionMap.put("locationIdList", locationIdList);
       } else if (permitLocationId != null) {
           locationIdList = locationDao.getChildLocationId(permitLocationId);
           locationIdList.add(permitLocationId);
           conditionMap.put("locationIdList", locationIdList);
       }

       conditionMap.put("meterNoList", null);
       conditionMap.put("startDate", searchStartDate.substring(0, 6)+"01");
       conditionMap.put("endDate", searchEndDate.substring(0, 6)+"31");

       List<Map<String, Object>> result = meteringDayDao.getMeteringValueYearlyData(conditionMap, true);
       return (Integer)(result.get(0).get("total"));
   }
}