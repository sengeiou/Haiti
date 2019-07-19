package com.aimir.service.mvm.impl;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aimir.dao.device.MeterDao;
import com.aimir.dao.mvm.DayEMDao;
import com.aimir.dao.system.CodeDao;
import com.aimir.dao.system.ContractDao;
import com.aimir.dao.system.LocationDao;
import com.aimir.dao.system.SupplierDao;
import com.aimir.model.system.Code;
import com.aimir.model.system.Supplier;
import com.aimir.service.mvm.SicLoadProfileManager;
import com.aimir.util.DecimalUtil;
import com.aimir.util.StringUtil;
import com.aimir.util.TimeLocaleUtil;

/**
 * SicLoadProfileManagerImpl.java Description 
 * <p>
 * <pre>
 * Date          Version     Author   Description
 * 2012. 4. 12.  v1.0        문동규   SIC Load Profile Service Impl
 * 2012. 08. 08.  v1.5       고경준   SIC Load Profile Service Impl 수정
 * </pre>
 */
@Service(value = "sicLoadProfileManager")
public class SicLoadProfileManagerImpl implements SicLoadProfileManager {

    protected static Log log = LogFactory.getLog(SicLoadProfileManagerImpl.class);

    @Autowired
    ContractDao contractDao;

    @Autowired
    SupplierDao supplierDao;

    @Autowired
    LocationDao locationDao;
    
    @Autowired
    DayEMDao dayEMDao;

    @Autowired
    MeterDao meterDao;

    @Autowired
    CodeDao codeDao;

//    /**
//     * method name : getSicCustomerEnergyUsageList<b/>
//     * method Desc : 에너지 사용량 fetch by sic code
//     *
//     * @param conditionMap
//     * @return
//     */
//    public List<Map<String, Object>> getSicCustomerEnergyUsageList(Map<String, Object> conditionMap) 
//    {
////        List<Map<String, Object>> total = new ArrayList<Map<String, Object>>();
//    	
//    	//에너지 사용량 fetch
//        List<Map<String, Object>> result = dayEMDao.getSicCustomerEnergyUsageList(conditionMap, false);
//        
//        
//
//        Supplier supplier = supplierDao.get((Integer)conditionMap.get("supplierId"));
//        DecimalFormat mdf = DecimalUtil.getDecimalFormat(supplier.getMd());
//        DecimalFormat cdf = DecimalUtil.getDecimalFormat(supplier.getCd());
//        Integer customerCount = null;
//        Double usageSum = null;
//
////        if (result != null && result.size() > 0) {
////            total = dayEMDao.getSicCustomerEnergyUsageTotalSum(conditionMap);
////            result.addAll(total);
////
////            for (Map<String, Object> obj : result) {
////                customerCount = DecimalUtil.ConvertNumberToInteger(obj.get("customerCount"));
////                usageSum = DecimalUtil.ConvertNumberToDouble(obj.get("usageSum"));
////
////                obj.put("customerCount", cdf.format(customerCount));
////                obj.put("usageSum", mdf.format(StringUtil.nullToDoubleZero(usageSum)));
////            }
////        }
//
//        BigDecimal bgCount = new BigDecimal("0");
//        BigDecimal bgSum = new BigDecimal("0");
//        
//        if (result != null && result.size() > 0) {
//            for (Map<String, Object> obj : result) {
//                customerCount = DecimalUtil.ConvertNumberToInteger(obj.get("customerCount"));
//                usageSum = DecimalUtil.ConvertNumberToDouble(obj.get("usageSum"));
//
//                bgCount = bgCount.add(new BigDecimal(customerCount));
//                bgSum = bgSum.add(new BigDecimal(usageSum));
//
//                obj.put("customerCount", cdf.format(customerCount));
//                obj.put("usageSum", mdf.format(StringUtil.nullToDoubleZero(usageSum)));
//            }
//            Map<String, Object> map = new HashMap<String, Object>();
//            map.put("sicId", 0);
//            map.put("sicCode", "0");
//            map.put("sicName", "Total");
//            map.put("customerCount", cdf.format(bgCount.intValue()));
//            map.put("usageSum", mdf.format(StringUtil.nullToDoubleZero(bgSum.doubleValue())));
//            
//            result.add(map);
//        }
//
//        
//        
//        return result;
//    }
    
    
    
//    /**
//     * 에너지 usage fetch by sic_id
//     */
//    @SuppressWarnings({ "unused", "unchecked", "rawtypes" })
//    @Deprecated
//	public List<Map<String, Object>> getSicCustomerEnergyUsageList2(Map<String, Object> conditionMap) {
//		List<Map<String, Object>> result=new ArrayList();
//		List<Map<String, Object>> result2=new ArrayList();
//
//    	String searchStartDate= (String) conditionMap.get("searchStartDate");
//    	String searchEndDate= (String) conditionMap.get("searchEndDate");
//    	Integer supplierId = (Integer)conditionMap.get("supplierId");
//    	
//    	/**
//    	 * 산업별 sic_id ,  ,  sicName	, customerCount  , code.code as sic , sicCode --> sic
//    	 * fetch 
//    	 * 
//    	 */
//    	List<Map<String, Object>> sicIdList = dayEMDao.getSicIdList();
//    	
//        DecimalFormat dfMd = DecimalUtil.getDecimalFormat(supplierDao.get(Integer.parseInt(conditionMap.get("supplierId").toString())).getMd());
//		DecimalFormat dfCd = DecimalUtil.getDecimalFormat(supplierDao.get(Integer.parseInt(conditionMap.get("supplierId").toString())).getCd());
//        
//		int customerCountTotal=0;
//		Double usageSumTotal=0.000;
//		
//    	for (int i=0; i< sicIdList.size(); i++) {
//			Map<String, Object> conditionMap2=new HashMap();
//    		
//    		Map<String, Object> sicidbean = sicIdList.get(i);
//    		
//    		String sicId = String.valueOf( sicidbean.get("sicId"));
//    		String sicName = String.valueOf( sicidbean.get("sicName"));
//    		String customerCount = String.valueOf( sicidbean.get("customerCount"));
//    		String sicCode = String.valueOf( sicidbean.get("sicCode"));
//    		
//    		//계약 고객 총계
//    		customerCountTotal += Integer.parseInt(customerCount);
//    		
//    		conditionMap2.put("sicId", sicId);
//    		conditionMap2.put("sicName", sicName);
//    		conditionMap2.put("customerCount", customerCount);
//    		conditionMap2.put("searchStartDate", searchStartDate);
//    		//searchEndDate
//    		conditionMap2.put("searchEndDate", searchEndDate);
//    		//supplierId
//    		conditionMap2.put("supplierId", supplierId);
//    		conditionMap2.put("sicCode", sicCode);
//    		
//    		/**
//    		 * 에너지 usage fetch 
//    		 */
//    		result2 = dayEMDao.getSicCustomerEnergyUsageList2(conditionMap2, false);
//    		
//    		Map<String, Object> energyusageresult =result2.get(0);
//    		
//    		//총사용량 null처리
//    		Object usageSum ="";
//    		if (  energyusageresult.get("usageSum")== null ) //||  energyusageresult.get("usageSum").equals(null))
//    			usageSum="0";
//    		else
//    			usageSum=energyusageresult.get("usageSum");
//    		
//    		//단위 포멧 적용.
//    		conditionMap2.put("usageSum", dfMd.format(Double.parseDouble(usageSum.toString())));
//    		
//    		/**
//    		 * 
//    		 * 사용량 총계 계산..
//    		 */
//    		usageSumTotal += Double.parseDouble(usageSum.toString());
//    		
//    		result.add(conditionMap2);
//    	}
//    	
//    	Map<String, Object> conditionMap3=new HashMap();
//    	
//    	/**
//    	 * 마지막 하단에 Total row 처리
//    	 */
//    	conditionMap3.put("sicName", "Total");
//		conditionMap3.put("customerCount", customerCountTotal);
//		conditionMap3.put("usageSum", dfMd.format(Double.parseDouble(usageSumTotal.toString())));
//    	
//		result.add(conditionMap3);
//        
//        return result;
//    }
    
//    /**
//     * method name : getEbsSuspectedDtsListTotalCount<b/>
//     * method Desc : Energy Balance Monitoring 미니가젯에서 Suspected Substation List 의 Total Count 를 조회한다.
//     *
//     * @param conditionMap
//     * @return
//     */
//    public Integer getEbsSuspectedDtsListTotalCount(Map<String, Object> conditionMap) 
//    {
//        List<Map<String, Object>> result = dayEMDao.getSicCustomerEnergyUsageList(conditionMap, true);
//
//        return (Integer)(result.get(0).get("total"));
//    }
    
//    public Integer getEbsSuspectedDtsListTotalCount2(Map<String, Object> conditionMap) 
//    {
//        List<Map<String, Object>> result = dayEMDao.getSicCustomerEnergyUsageList(conditionMap, true);
//        
//       // List<Map<String, Object>> result = dayEMDao.getSicCustomerEnergyUsageList2(conditionMap, true);
//
//        return (Integer)(result.get(0).get("total"));
//    }


    /**
     * method name : getSicLoadProfileChartData<b/>
     * method Desc : SIC Load Profile 맥스가젯의 Load Profile Chart Data 를 조회한다.
     *
     * @param conditionMap
     * @return
     */
    public Map<String, Object> getSicLoadProfileChartData(Map<String, Object> conditionMap) {
        Map<String, Object> result = new HashMap<String, Object>();
        
        conditionMap.put("dayType", 0);     // 0:working day
        List<Map<String, Object>> workAvgSelList = dayEMDao.getSicLoadProfileChartDataByDayAvg(conditionMap);
        List<Map<String, Object>> workSumSelList = dayEMDao.getSicLoadProfileChartDataByDaySum(conditionMap);

        conditionMap.put("dayType", 1);     // 1:saturday
        List<Map<String, Object>> satAvgSelList = dayEMDao.getSicLoadProfileChartDataByDayAvg(conditionMap);
        List<Map<String, Object>> satSumSelList = dayEMDao.getSicLoadProfileChartDataByDaySum(conditionMap);

        conditionMap.put("dayType", 2);     // 2:sunday
        List<Map<String, Object>> sunAvgSelList = dayEMDao.getSicLoadProfileChartDataByDayAvg(conditionMap);
        List<Map<String, Object>> sunSumSelList = dayEMDao.getSicLoadProfileChartDataByDaySum(conditionMap);

        conditionMap.put("dayType", 3);     // 3:holiday
        List<Map<String, Object>> holiAvgSelList = dayEMDao.getSicLoadProfileChartDataByDayAvg(conditionMap);
        List<Map<String, Object>> holiSumSelList = dayEMDao.getSicLoadProfileChartDataByDaySum(conditionMap);

        List<Map<String, Object>> peakSelList = dayEMDao.getSicLoadProfileChartDataByPeakDay(conditionMap);
        
        List<String> timeList = new ArrayList<String>();
        List<Double> workAvgList = new ArrayList<Double>();
        List<Double> satAvgList = new ArrayList<Double>();
        List<Double> sunAvgList = new ArrayList<Double>();
        List<Double> holiAvgList = new ArrayList<Double>();
        List<Double> workSumList = new ArrayList<Double>();
        List<Double> satSumList = new ArrayList<Double>();
        List<Double> sunSumList = new ArrayList<Double>();
        List<Double> holiSumList = new ArrayList<Double>();
        List<Double> peakList = new ArrayList<Double>();

        Supplier supplier = supplierDao.get((Integer)conditionMap.get("supplierId"));
        String lang = supplier.getLang().getCode_2letter();
        String country = supplier.getCountry().getCode_2letter();

        Map<String, Object> map = null;

        if (workAvgSelList.size() > 0) {
            map = workAvgSelList.get(0);

            for (int i = 0; i < 24 ; i++) {
                timeList.add(TimeLocaleUtil.getLocaleHourMinute(StringUtil.frontAppendNStr('0', Integer.toString(i), 2) + "00", lang, country));
                workAvgList.add(DecimalUtil.ConvertNumberToDouble(map.get("value_" + StringUtil.frontAppendNStr('0', Integer.toString(i), 2))));
            }
            map = null;
        }

        if (workSumSelList.size() > 0) {
            map = workSumSelList.get(0);

            for (int i = 0; i < 24 ; i++) {
                workSumList.add(DecimalUtil.ConvertNumberToDouble(map.get("value_" + StringUtil.frontAppendNStr('0', Integer.toString(i), 2))));
            }
            map = null;
        }

        if (satAvgSelList.size() > 0) {
            map = satAvgSelList.get(0);

            for (int i = 0; i < 24 ; i++) {
                satAvgList.add(DecimalUtil.ConvertNumberToDouble(map.get("value_" + StringUtil.frontAppendNStr('0', Integer.toString(i), 2))));
            }
            map = null;
        }

        if (satSumSelList.size() > 0) {
            map = satSumSelList.get(0);

            for (int i = 0; i < 24 ; i++) {
                satSumList.add(DecimalUtil.ConvertNumberToDouble(map.get("value_" + StringUtil.frontAppendNStr('0', Integer.toString(i), 2))));
            }
            map = null;
        }

        if (sunAvgSelList.size() > 0) {
            map = sunAvgSelList.get(0);

            for (int i = 0; i < 24 ; i++) {
                sunAvgList.add(DecimalUtil.ConvertNumberToDouble(map.get("value_" + StringUtil.frontAppendNStr('0', Integer.toString(i), 2))));
            }
            map = null;
        }

        if (sunSumSelList.size() > 0) {
            map = sunSumSelList.get(0);

            for (int i = 0; i < 24 ; i++) {
                sunSumList.add(DecimalUtil.ConvertNumberToDouble(map.get("value_" + StringUtil.frontAppendNStr('0', Integer.toString(i), 2))));
            }
            map = null;
        }

        if (holiAvgSelList.size() > 0) {
            map = holiAvgSelList.get(0);

            for (int i = 0; i < 24 ; i++) {
                holiAvgList.add(DecimalUtil.ConvertNumberToDouble(map.get("value_" + StringUtil.frontAppendNStr('0', Integer.toString(i), 2))));
            }
            map = null;
        }

        if (holiSumSelList.size() > 0) {
            map = holiSumSelList.get(0);

            for (int i = 0; i < 24 ; i++) {
                holiSumList.add(DecimalUtil.ConvertNumberToDouble(map.get("value_" + StringUtil.frontAppendNStr('0', Integer.toString(i), 2))));
            }
            map = null;
        }

        if (peakSelList.size() > 0) {
            map = peakSelList.get(0);

            for (int i = 0; i < 24 ; i++) {
                peakList.add(DecimalUtil.ConvertNumberToDouble(map.get("value_" + StringUtil.frontAppendNStr('0', Integer.toString(i), 2))));
            }
            map = null;
        }

        result.put("timeList", timeList);
        result.put("workAvgList", workAvgList);
        result.put("satAvgList", satAvgList);
        result.put("sunAvgList", sunAvgList);
        result.put("holiAvgList", holiAvgList);
        result.put("peakList", peakList);
        result.put("workSumList", workSumList);
        result.put("satSumList", satSumList);
        result.put("sunSumList", sunSumList);
        result.put("holiSumList", holiSumList);

        return result;
    }

    /**
     * method name : getSicTotalLoadProfileChartData<b/>
     * method Desc : SIC Load Profile 맥스가젯의 Total Load Profile Chart Data 를 조회한다.
     *
     * @param conditionMap
     * @return
     */
    public Map<String, Object> getSicTotalLoadProfileChartData(Map<String, Object> conditionMap) {
        Map<String, Object> result = new HashMap<String, Object>();
        List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();

        List<Map<String, Object>> selList = dayEMDao.getSicTotalLoadProfileChartData(conditionMap);

        List<String> timeList = new ArrayList<String>();
        List<Double> list = new ArrayList<Double>();

        Supplier supplier = supplierDao.get((Integer)conditionMap.get("supplierId"));
        String lang = supplier.getLang().getCode_2letter();
        String country = supplier.getCountry().getCode_2letter();

        Map<String, Object> map = null;

        for (int i = 0; i < 24 ; i++) {
            timeList.add(TimeLocaleUtil.getLocaleHourMinute(StringUtil.frontAppendNStr('0', Integer.toString(i), 2) + "00", lang, country));
        }

        if (selList.size() > 0) {
            
            for (Map<String, Object> obj : selList) {
                map = new HashMap<String, Object>();
                list = new ArrayList<Double>();
                for (int i = 0; i < 24 ; i++) {
                    list.add(DecimalUtil.ConvertNumberToDouble(obj.get("value_" + StringUtil.frontAppendNStr('0', Integer.toString(i), 2))));
                }
                map.put("name", obj.get("sicName"));
                map.put("list", list);
                resultList.add(map);
            }
        }

        result.put("timeList", timeList);
        result.put("totalList", resultList);

        return result;
    }

    /**
     * method name : getSicContEnergyUsageTreeData<b/>
     * method Desc : SIC Load Profile 가젯에서 SIC 별 에너지사용량 tree data 를 조회한다.
     *
     * @param conditionMap
     * @return
     */
    public List<Map<String, Object>> getSicContEnergyUsageTreeData(Map<String, Object> conditionMap) {
        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
        List<Map<String, Object>> childrenList = new ArrayList<Map<String, Object>>();
        Map<String, Object> tmpMap = new HashMap<String, Object>();
        Map<String, Object> tmpChildrenMap = new HashMap<String, Object>();
        Map<String, Map<String, Object>> contractMap = new HashMap<String, Map<String, Object>>();
        Map<String, Map<String, Object>> usageMap = new HashMap<String, Map<String, Object>>();

        Supplier supplier = supplierDao.get((Integer)conditionMap.get("supplierId"));
        DecimalFormat mdf = DecimalUtil.getDecimalFormat(supplier.getMd());
        DecimalFormat cdf = DecimalUtil.getDecimalFormat(supplier.getCd());
        Integer customerCount = 0;
        Double usageSum = null;

        Integer customerCountSum = 0;
        Integer customerCountTotal = 0;
        BigDecimal bdUsageSum = new BigDecimal("0");
        BigDecimal bdUsageSumTotal = new BigDecimal("0");

        // SIC Level 1 코드 조회
        List<Code> level1CodeList = codeDao.getChildCodesOrder(Code.SIC);

        // SIC Level 2 코드 조회
        List<Code> level2CodeList = codeDao.getSicChildrenCodeList();

        // SIC Code 별 contract count list
        List<Map<String, Object>> sicContractCountList = contractDao.getSicContractCountList(conditionMap);

        // SIC Code 별 에너지 사용량 sum
        List<Map<String, Object>> sicEnergyUsageList = dayEMDao.getSicEnergyUsageList(conditionMap);

        for (Map<String, Object> obj : sicContractCountList) {
            contractMap.put((String)obj.get("SIC_CODE"), obj);
        }

        for (Map<String, Object> obj : sicEnergyUsageList) {
            usageMap.put((String)obj.get("SIC_CODE"), obj);
        }

        for (Code code : level1CodeList) {
            tmpMap = new HashMap<String, Object>();
            customerCountSum = 0;
            bdUsageSum = new BigDecimal("0");

            tmpMap.put("id", code.getCode());
            tmpMap.put("sicId", code.getId());
            tmpMap.put("sicCode", code.getCode());
            tmpMap.put("sicName", code.getDescr());
            tmpMap.put("isClick", Boolean.FALSE.toString());

            childrenList = new ArrayList<Map<String, Object>>();

            for (Code children : level2CodeList) {
                if (children.getParent().getId().equals(code.getId())) {
                    tmpChildrenMap = new HashMap<String, Object>();

                    tmpChildrenMap.put("id", children.getCode());
                    tmpChildrenMap.put("sicId", children.getId());
                    tmpChildrenMap.put("sicCode", children.getCode());
                    tmpChildrenMap.put("sicName", children.getDescr());
                    tmpChildrenMap.put("isClick", Boolean.TRUE.toString());

                    if (contractMap.get(children.getCode()) != null) {
                        customerCount = DecimalUtil.ConvertNumberToInteger(contractMap.get(children.getCode()).get("CONTRACT_CNT"));

                        if (customerCount == null) {
                            customerCount = 0;
                        }

                        customerCountSum = customerCountSum + customerCount;
                        customerCountTotal = customerCountTotal + customerCount;

                        tmpChildrenMap.put("customerCount", cdf.format(customerCount));
                    } else {
                        tmpChildrenMap.put("customerCount", cdf.format(0));
                    }

                    if (usageMap.get(children.getCode()) != null) {
                        usageSum = DecimalUtil.ConvertNumberToDouble(usageMap.get(children.getCode()).get("USAGE_SUM"));

                        if (usageSum == null) {
                            usageSum = 0D;
                        }

                        bdUsageSum = bdUsageSum.add(new BigDecimal(usageSum.toString()));
                        bdUsageSumTotal = bdUsageSumTotal.add(new BigDecimal(usageSum.toString()));

                        tmpChildrenMap.put("usageSum", mdf.format(usageSum));
                    } else {
                        tmpChildrenMap.put("usageSum", mdf.format(0D));
                    }

                    tmpChildrenMap.put("iconCls", "no-icon");
                    tmpChildrenMap.put("leaf", true);
                    childrenList.add(tmpChildrenMap);
                }
            }

            if (childrenList.size() <= 0) {
                tmpMap.put("children", null);
            } else {
                tmpMap.put("children", childrenList);
            }

            tmpMap.put("customerCount", cdf.format(customerCountSum));
            tmpMap.put("usageSum", mdf.format(bdUsageSum.doubleValue()));
            tmpMap.put("iconCls", "no-icon");
            tmpMap.put("expanded", true);
            result.add(tmpMap);
        }

        // Total
        tmpMap = new HashMap<String, Object>();

        tmpMap.put("id", "0");
        tmpMap.put("sicId", 0);
        tmpMap.put("sicCode", "0");
        tmpMap.put("sicName", "Total");
        tmpMap.put("isClick", Boolean.TRUE.toString());
        tmpMap.put("customerCount", cdf.format(customerCountTotal));
        tmpMap.put("usageSum", mdf.format(bdUsageSumTotal.doubleValue()));
        tmpMap.put("iconCls", "no-icon");
        tmpMap.put("leaf", true);
        result.add(tmpMap);

        return result;
    }
}