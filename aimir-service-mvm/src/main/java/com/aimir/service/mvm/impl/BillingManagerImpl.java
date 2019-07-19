package com.aimir.service.mvm.impl;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aimir.constants.CommonConstants.DateType;
import com.aimir.constants.CommonConstants.DeviceType;
import com.aimir.dao.mvm.BillingDayEMDao;
import com.aimir.dao.mvm.BillingMonthEMDao;
import com.aimir.dao.mvm.DayEMDao;
import com.aimir.dao.mvm.DayGMDao;
import com.aimir.dao.mvm.DayHMDao;
import com.aimir.dao.mvm.DayWMDao;
import com.aimir.dao.mvm.MonthEMDao;
import com.aimir.dao.mvm.MonthGMDao;
import com.aimir.dao.mvm.MonthHMDao;
import com.aimir.dao.mvm.MonthWMDao;
import com.aimir.dao.mvm.RealTimeBillingEMDao;
import com.aimir.dao.system.ContractDao;
import com.aimir.dao.system.LocationDao;
import com.aimir.dao.system.SupplierDao;
import com.aimir.dao.system.TariffEMDao;
import com.aimir.dao.system.TariffGMDao;
import com.aimir.dao.system.TariffWMDao;
import com.aimir.model.mvm.DayEM;
import com.aimir.model.mvm.DayGM;
import com.aimir.model.mvm.DayHM;
import com.aimir.model.mvm.DayWM;
import com.aimir.model.mvm.MonthEM;
import com.aimir.model.mvm.MonthGM;
import com.aimir.model.mvm.MonthHM;
import com.aimir.model.mvm.MonthWM;
import com.aimir.model.system.Contract;
import com.aimir.model.system.Location;
import com.aimir.model.system.Supplier;
import com.aimir.service.mvm.BillingManager;
import com.aimir.util.CommonUtils2;
import com.aimir.util.DecimalUtil;
import com.aimir.util.StringUtil;
import com.aimir.util.TimeLocaleUtil;

@Service(value = "billingManager")
public class BillingManagerImpl implements BillingManager {

    @SuppressWarnings("unused")
    private static Log logger = LogFactory.getLog(BillingManagerImpl.class);

	@Autowired DayEMDao dayEmDao;
	@Autowired DayGMDao dayGmDao;
	@Autowired DayHMDao dayHmDao;
	@Autowired DayWMDao dayWmDao;
	
	@Autowired MonthEMDao monthEmDao;
	@Autowired MonthGMDao monthGmDao;
	@Autowired MonthHMDao monthHmDao;
	@Autowired MonthWMDao monthWmDao;
	
	@Autowired TariffEMDao tariffEMDao;
	@Autowired TariffGMDao tariffGMDao;
	@Autowired TariffWMDao tariffWMDao;
	@Autowired TariffWMDao tariffHMDao;
	
	@Autowired LocationDao locationDao;	
	@Autowired ContractDao contractDao;	
	
	@Autowired SupplierDao supplierDao;
	
	@Autowired BillingDayEMDao billingDayEMDao;
	@Autowired BillingMonthEMDao billingMonthEMDao;
    @Autowired RealTimeBillingEMDao realtimebillingemDao;
	
	@SuppressWarnings("unused")
	public List<Map<String, String>> getElecBillingChartData(	Map<String, String> conditionMap)
	{

		List<Object[]> dayEMs = null;

		String searchDateType = conditionMap.get("searchDateType");
		searchDateType= searchDateType.trim();
		String serviceType = conditionMap.get("serviceType");
		String chartType = conditionMap.get("chartType");
		
		String locationId= conditionMap.get("locationIds");
		
		
		StringBuffer strbuf=new StringBuffer();
		
		//로케이션 child nodes FETCH
		conditionMap.put("locationCondition",	getChildNodesInLocation(conditionMap.get("locationIds"),	conditionMap.get("supplierId"), true, strbuf ));

		if ("EM".equals(serviceType))
		{
			if ("1".equals(searchDateType) || "3".equals(searchDateType))
			{ // 일별, 주별
				dayEMs = dayEmDao.getDayBillingChartData(conditionMap);
			} 
			else if ("4".equals(searchDateType) || "7".equals(searchDateType))
			{ // 4-->월별, 분기별
				dayEMs = monthEmDao.getMonthBillingChartData(conditionMap);
			}
		} 
		else if ("GM".equals(serviceType))
		{
			if ("1".equals(searchDateType) || "3".equals(searchDateType))
			{ // 일별, 주별
				dayEMs = dayGmDao.getDayBillingChartData(conditionMap);
			} else if ("4".equals(searchDateType) || "7".equals(searchDateType))
			{ // 월별, 분기별
				dayEMs = monthGmDao.getMonthBillingChartData(conditionMap);
			}
		} else if ("HM".equals(serviceType))
		{
			if ("1".equals(searchDateType) || "3".equals(searchDateType))
			{ // 일별, 주별
				dayEMs = dayHmDao.getDayBillingChartData(conditionMap);
			} else if ("4".equals(searchDateType) || "7".equals(searchDateType))
			{ // 월별, 분기별
				dayEMs = monthHmDao.getMonthBillingChartData(conditionMap);
			}
		} else if ("WM".equals(serviceType))
		{
			if ("1".equals(searchDateType) || "3".equals(searchDateType))
			{ // 일별, 주별
				dayEMs = dayWmDao.getDayBillingChartData(conditionMap);
			} else if ("4".equals(searchDateType) || "7".equals(searchDateType))
			{ // 월별, 분기별
				dayEMs = monthWmDao.getMonthBillingChartData(conditionMap);
			}
		}

		return makeChartData(dayEMs, chartType);
	}
	
	// 지역들의 가장 하위 지역들을 구한다.
	
	
	/**
	 * 
	 * @param locationIds
	 * @param supplierId
	 * @return
	 */
	private String getLeafLocationIds(String locationIds, String supplierId) {
		
		if("".equals(locationIds)) 
			return "";
		
		String rtnVal = "";
		String[] locationIdArray = locationIds.split(",");		
		Set<Integer> locations = new HashSet<Integer>();
		List<Integer> tempList = null;
		
		for(String locationId : locationIdArray) {
			
			tempList = locationDao.getLeafLocationId(Integer.parseInt(locationId), Integer.parseInt(supplierId));
			
			for(Integer integer : tempList) {
				locations.add(integer);
			}
		}
		
		int i = 0 ;		
		for(Iterator<Integer> it = locations.iterator(); it.hasNext() ; i++) {
			
			//if(it.hasNext())
			if(i == locations.size() - 1)
				rtnVal += it.next();					
			else
				rtnVal += Integer.toString(it.next()) + ',';
		}

		return rtnVal;
	}
	
	
	
	
	
	/**
	 * @DESC Location 의 하위노드를 fetch 
	 * @param locationIds : view(jsp)에서 넘어온 location id (단일값)
	 * @param supplierId 
	 * @param firstFlag : 최초값인지 여부
	 * @param strbuf :  전체 노드 id(구분자 , ) 스트링버퍼
	 * @return 현재노드 와 현재노드의 하위노드들.
	 */
	@SuppressWarnings({ "rawtypes" })
	public String getChildNodesInLocation(String locationIds, String supplierId, boolean firstFlag, StringBuffer strbuf )
	{
		String locIds="";
		
		if ( locationIds =="" || locationIds ==null || locationIds.equals(null))
		{
			locIds= "";
		}
		else
		{
			
			//최초값인 경우.
			if ( firstFlag == true)
				strbuf.append( locationIds);
			
			
			List locList = new ArrayList();
	
			// 해당노드의 하위노드를 가지고 온다.
			locList = locationDao.getChildNodesInLocation(	Integer.parseInt(locationIds), Integer.parseInt(supplierId));

			
			int locId;
	
			for (int i = 0; i < locList.size(); i++)
			{
	
				locId = (Integer) locList.get(i);
				
				
				strbuf.append(", " + locId);
				
				//하위노드가 있는지를 검사.
				String childNodeCnt = locationDao.getChildNodesInLocationCnt(locId, Integer.parseInt(supplierId));
				
				//하위노드가 존재하면
				if ( Integer.parseInt(childNodeCnt) >0)
				{
					//재귀호출
					this.getChildNodesInLocation(Integer.toString(locId), supplierId, false, strbuf);
				}
				
				
	
			}
			locIds = strbuf.toString();
	
			
		}
		
		return locIds;
		
		
	}
	
	 // 해당 지역의 가장 하위 지역들을 구한다.
    private String getLeafLocationId(String locationId, String supplierId) {
        
        if("".equals(locationId)) return "";
        
        StringBuilder rtnVal = new StringBuilder();
        Set<Integer> locations = new HashSet<Integer>();
        List<Integer> tempList = null;
        Iterator<Integer> it = null;
        int cnt = 0 ;
        int tot = 0;
        
        tempList = locationDao.getLeafLocationId(Integer.parseInt(locationId), Integer.parseInt(supplierId));
        
        for(Integer integer : tempList) {
            locations.add(integer);
        }
        
        tot = locations.size();
        it = locations.iterator();
        
        while(it.hasNext()) {
            rtnVal.append(it.next());
            //if(it.hasNext())
            if(cnt != (tot - 1)) {
                rtnVal.append(',');
            }
            cnt++;
        }

        return rtnVal.toString();
    }
	
	private List<Map<String, String>> makeChartData(List<Object[]> objs, String chartType) {
		
		List<Map<String, String>> chartDatas = new ArrayList<Map<String, String>>();		
		Map<String, String> chartData = null;						
		
		
		if(objs != null && objs.size() > 0){
			for(Object[] obj : objs) {

				chartData = new HashMap<String, String>();
				chartData.put("locationName", obj[1] + "");
				chartData.put("kwh", obj[0] + "");			
				chartDatas.add(chartData);
			}
		}


		if(("lineChart".equals(chartType) && chartDatas.size() == 1) || chartDatas.size() == 0) {
			
			chartData = new HashMap<String, String>();
			chartData.put("locationName", "");
			chartData.put("kwh", "0");			
			chartDatas.add(0, chartData);
		}		
		
		return chartDatas;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public List<Map<String, Object>> getElecLocationBillingGridData(Map<String, String> conditionMap) {			
		
		String isAnonymous = conditionMap.get("isAnonymous");
		String supplierId = conditionMap.get("supplierId");
		String locationIds = conditionMap.get("locationIds");
		
		String[] locationIdArray = conditionMap.get("locationIds").split(",");
		
		List<Location> locations = new ArrayList<Location>();
		
		if("".equals(locationIds)) {
			if("false".equals(isAnonymous))
				locations = locationDao.getParentsBySupplierId(Integer.parseInt(supplierId));
			else if("true".equals(isAnonymous)) 
				locations = locationDao.getParents();
		} else {
			locations.add(locationDao.get(Integer.parseInt(locationIds)));
		}

		DecimalFormat dfMd = DecimalUtil.getDecimalFormat(supplierDao.get(Integer.parseInt(supplierId)).getMd());
		DecimalFormat dfCd = DecimalUtil.getDecimalFormat(supplierDao.get(Integer.parseInt(supplierId)).getCd());
		
		
		
		List makeGridDatasList= makeGridDatas(conditionMap, locations, locationIdArray);
				
		return setFormatting(makeGridDatasList, dfMd, dfCd);
	}
	
	// MD, CD 포맷 설정
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private List<Map<String, Object>> setFormatting(List<Map<String, Object>> changeList, DecimalFormat dfMd, DecimalFormat dfCd) {
		
    	for(Map<String, Object> changeMap: changeList) {
			Object usage = changeMap.get("usage");
			Object maxUsage = changeMap.get("maxUsage");
			Object usageCharge = changeMap.get("usageCharge");
			
			if ( StringUtil.nullToBlank(usage).length() > 0 )
				changeMap.put("usage", dfMd.format(Double.parseDouble(usage.toString())));
			
			if ( StringUtil.nullToBlank(maxUsage).length() > 0 )
				changeMap.put("maxUsage", dfMd.format(Double.parseDouble(maxUsage.toString())));
	
			if ( StringUtil.nullToBlank(usageCharge).length() > 0 )
				changeMap.put("usageCharge", dfCd.format(Double.parseDouble(usageCharge.toString())));
			
			if ( changeMap.containsKey("children") )
				setFormatting((List)changeMap.get("children"), dfMd, dfCd);
	    	}
    	
    	return changeList;
	}
	
	/**
	 * @desc 그리드 데이타 생성
	 * @param conditionMap
	 * @param locations
	 * @param locationIdArray
	 * @return
	 */
	private List<Map<String, Object>> makeGridDatas(
			Map<String, String> conditionMap, List<Location> locations,
			String[] locationIdArray)
	{

		List<Map<String, Object>> rtnList = new ArrayList<Map<String, Object>>();
		Map<String, Object> rtnMap = null;

		for (Location location : locations)
		{

			// if(checkLocationId(location, locationIdArray)) {

			//에너지 사용량 fetch
			rtnMap = makeGridData(conditionMap, location);

			List<Location> children = locationDao.getChildren(location.getId());

			
			//하위 노드가 존재할경우..재귀호출.
			if (children != null && children.size() > 0) {
				rtnMap.put("children",	makeGridDatas(conditionMap, children, locationIdArray));
			} else {
				rtnMap.put("leaf",	true);
			}

			rtnList.add(rtnMap);
			// }
		}

		return rtnList;
	}
	
	// 그리드 ROW 데이타 생성
	/**
	 * @desc 에너지 사용량 fetch at billing max gadget
	 * @param conditionMap
	 * @param location
	 * @return
	 */
	private Map<String, Object> makeGridData(Map<String, String> conditionMap, Location location) {
		
		String locationIds = "";
		
		if(location.getChildren() != null && location.getChildren().size() > 0) {
			
			List<Integer> idList = locationDao.getLeafLocationId(location.getId(), location.getSupplier().getId());
			
			for(int i = 0, size = idList.size() ; i < size ; i++) {
				
				if(i == size - 1)
					locationIds += (idList.get(i) + ""); 			
				else 
					locationIds += (idList.get(i) + "") + ',';			
			}			
		} else {
			locationIds = location.getId() + "";
		}
			
		conditionMap.put("locationCondition", locationIds);
		
		List<Object[]> objects = null;
		String searchDateType = conditionMap.get("searchDateType");
		searchDateType= searchDateType.trim();
		String serviceType = conditionMap.get("serviceType");		
		
		if ("EM".equals(serviceType))
		{
			if ("1".equals(searchDateType) || "3".equals(searchDateType))
			{ // 일별, 주별
				objects = dayEmDao.getDayBillingGridData(conditionMap);
			} else if ("4".equals(searchDateType) || "7".equals(searchDateType))
			{ // 월별, 분기별
				objects = monthEmDao.getMonthBillingGridData(conditionMap);
			}
		} else if ("GM".equals(serviceType))
		{
			if ("1".equals(searchDateType) || "3".equals(searchDateType))
			{ // 일별, 주별
				objects = dayGmDao.getDayBillingGridData(conditionMap);
			} else if ("4".equals(searchDateType) || "7".equals(searchDateType))
			{ // 월별, 분기별
				objects = monthGmDao.getMonthBillingGridData(conditionMap);
			}
		} else if ("HM".equals(serviceType))
		{
			if ("1".equals(searchDateType) || "3".equals(searchDateType))
			{ // 일별, 주별
				objects = dayHmDao.getDayBillingGridData(conditionMap);
			} else if ("4".equals(searchDateType) || "7".equals(searchDateType))
			{ // 월별, 분기별
				objects = monthHmDao.getMonthBillingGridData(conditionMap);
			}
		} else if ("WM".equals(serviceType))
		{
			if ("1".equals(searchDateType) || "3".equals(searchDateType))
			{ // 일별, 주별
				objects = dayWmDao.getDayBillingGridData(conditionMap);
			} else if ("4".equals(searchDateType) || "7".equals(searchDateType))
			{ // 월별, 분기별
				objects = monthWmDao.getMonthBillingGridData(conditionMap);
			}
		}

		if(objects == null || objects.size() == 0) return new HashMap<String, Object>();
		
		Object[] objectArray = objects.get(0);
		
		Map<String, Object> rtnMap = new HashMap<String, Object>();
		rtnMap.put("customerCnt", objectArray[1] + "");
		rtnMap.put("usage", StringUtil.nullToBlank(objectArray[0]));
		rtnMap.put("maxUsage", getMaxUsage(objectArray));
		rtnMap.put("locationName", location.getName());
		//rtnMap.put("usageCharge", getUsageCharge(conditionMap));
		
		return rtnMap;
	}
	
	private String getUsageCharge(Map<String, String> conditionMap) {

		String serviceType = conditionMap.get("serviceType");		
		Double totalUsageCharge = 0d ;
		Double currentUsageCharge = 0d ;
		Contract contract = null;
		Map<String, Object> paramMap = null;		
		List<Integer> contractIds = null;
		
		if("EM".equals(serviceType)) {
			contractIds = dayEmDao.getContractIds(conditionMap);
		} else if("GM".equals(serviceType)) {
			contractIds = dayGmDao.getContractIds(conditionMap);
		} else if("WM".equals(serviceType)) {
			contractIds = dayWmDao.getContractIds(conditionMap);
		} else if("HM".equals(serviceType)) {
			contractIds = dayHmDao.getContractIds(conditionMap);
		}	
		
		for(Integer contractId : contractIds) {
			
			contract = contractDao.get(contractId);
			
			paramMap = new HashMap<String, Object>();			
			paramMap.put("contract", contract);
			paramMap.put("dateType", conditionMap.get("searchDateType"));
			paramMap.put("startDate", conditionMap.get("startDate"));
			paramMap.put("endDate", conditionMap.get("endDate"));
						
			if("EM".equals(serviceType)) {
				currentUsageCharge = tariffEMDao.getUsageChargeByContract(paramMap);
			} else if("GM".equals(serviceType)) {
				currentUsageCharge = tariffGMDao.getUsageChargeByContract(paramMap);
			} else if("WM".equals(serviceType)) {
				currentUsageCharge = tariffWMDao.getUsageChargeByContract(paramMap);
			} else if("HM".equals(serviceType)) {
				currentUsageCharge = tariffHMDao.getUsageChargeByContract(paramMap);
			}					
			
			if(currentUsageCharge != null) 
				totalUsageCharge += currentUsageCharge;
		}
		
		return totalUsageCharge.toString();
	}
	
	// 최대 부하 구하기
	private String getMaxUsage(Object[] objectArray) {

		List<Double> list = new ArrayList<Double>();
		
		for(int i = 3; i <= 26; i++) {
			
			if(objectArray[i] != null) 
				list.add(Double.parseDouble(objectArray[i].toString()));
		}
				
		if(list.size() == 0) return "";
		
		return Collections.max(list) + "";
	}
	
	// 지역이 검색 되야 할 지역인지 판단
	@SuppressWarnings("unused")
	private boolean checkLocationId(Location location, String[] locationIdArray) {
		
		// 지역 선택 암것도 안했으믄 점 모든 지역 조회
		if(locationIdArray.length == 1 && "".equals(locationIdArray[0])) 
			return true;
		
		String id = Integer.toString(location.getId());
		
		for(String locationId : locationIdArray) 			
			if(id.equals(locationId)) 
				return true;
		
		return false;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public List<Map<String, String>> getCustomerBillingGridData(Map<String, Object> conditionMap) {

		String searchDateType = (String)conditionMap.get("searchDateType");
		String serviceType = (String)conditionMap.get("serviceType");
		conditionMap.put("locationCondition", getLeafLocationIds((String)conditionMap.get("locationIds"), (String)conditionMap.get("supplierId"))); 
		String page = (String)conditionMap.get("page");
		String rowPerPage = (String)conditionMap.get("pageSize");
		List<Map<String, String>> returnList = new ArrayList<Map<String, String>>();
		
		if("EM".equals(serviceType)) { 
			if("1".equals(searchDateType) || "3".equals(searchDateType))   // 일별, 주별			
				returnList = makeCustomerGridData(dayEmDao.getDayCustomerBillingGridData(conditionMap), conditionMap);						
			else if("4".equals(searchDateType) || "7".equals(searchDateType))  // 월별, 분기별		
				returnList = makeCustomerGridData(monthEmDao.getMonthCustomerBillingGridData(conditionMap), conditionMap);			
		} else if("GM".equals(serviceType)) {
			if("1".equals(searchDateType) || "3".equals(searchDateType))   // 일별, 주별			
				returnList = makeCustomerGridData(dayGmDao.getDayCustomerBillingGridData(conditionMap), conditionMap);						
			else if("4".equals(searchDateType) || "7".equals(searchDateType))  // 월별, 분기별		
				returnList = makeCustomerGridData(monthGmDao.getMonthCustomerBillingGridData(conditionMap), conditionMap);			
		} else if("HM".equals(serviceType)) {
			if("1".equals(searchDateType) || "3".equals(searchDateType))   // 일별, 주별			
				returnList = makeCustomerGridData(dayHmDao.getDayCustomerBillingGridData(conditionMap), conditionMap);						
			else if("4".equals(searchDateType) || "7".equals(searchDateType))  // 월별, 분기별		
				returnList = makeCustomerGridData(monthHmDao.getMonthCustomerBillingGridData(conditionMap), conditionMap);			
		} else if("WM".equals(serviceType)) {
			if("1".equals(searchDateType) || "3".equals(searchDateType))   // 일별, 주별			
				returnList = makeCustomerGridData(dayWmDao.getDayCustomerBillingGridData(conditionMap), conditionMap);						
			else if("4".equals(searchDateType) || "7".equals(searchDateType))  // 월별, 분기별		
				returnList = makeCustomerGridData(monthWmDao.getMonthCustomerBillingGridData(conditionMap), conditionMap);
		}
		
		DecimalFormat dfMd = DecimalUtil.getDecimalFormat(supplierDao.get(Integer.parseInt(conditionMap.get("supplierId").toString())).getMd());
		DecimalFormat dfCd = DecimalUtil.getDecimalFormat(supplierDao.get(Integer.parseInt(conditionMap.get("supplierId").toString())).getCd());
		Supplier supplier = supplierDao.get(Integer.parseInt((String)conditionMap.get("supplierId")));
		int count=1;
    	for(Object obj: returnList) {
    		Map<String, Object> data = (HashMap)obj;
    		
    		Object total = data.get("total");
    		Object usage = data.get("usage");
    		Object max = data.get("max");
//    		Object usageCharge = data.get("usageCharge");
    		data.put("rownum", Integer.toString(CommonUtils2.makeIdxPerPage(page, rowPerPage, count)));
    		if ( !StringUtil.nullToBlank(total).equals("") )
    			data.put("total", dfMd.format(Double.parseDouble(total.toString())));
    		
    		if ( !StringUtil.nullToBlank(usage).equals("") )
    			data.put("usage", dfMd.format(Double.parseDouble(usage.toString())));

    		if ( !StringUtil.nullToBlank(max).equals("") )
    			data.put("max", dfMd.format(Double.parseDouble(max.toString())));
    		
//    		if ( !StringUtil.nullToBlank(usageCharge).equals("") )
//    			data.put("usageCharge", dfCd.format(Double.parseDouble(usageCharge.toString())));
    		
            if ((DateType.DAILY.getCode()).equals(searchDateType) || (DateType.WEEKLY.getCode()).equals(searchDateType)) {   // 일별, 주별            
                data.put("yyyymmdd", TimeLocaleUtil.getLocaleDate((String)data.get("yyyymmdd"), supplier.getLang().getCode_2letter(), supplier.getCountry().getCode_2letter()));
            } else if((DateType.MONTHLY.getCode()).equals(searchDateType) || (DateType.SEASONAL.getCode()).equals(searchDateType)) {  // 월별, 분기별   
                data.put("yyyymmdd", TimeLocaleUtil.getLocaleYearMonth((String)data.get("yyyymmdd"), supplier.getLang().getCode_2letter(), supplier.getCountry().getCode_2letter()));
            }
            count++;
    	}
		return returnList;
	}

	public Long getElecCustomerBillingGridDataCount(Map<String, Object> conditionMap) {
		
		Long total = null;
		
		String searchDateType = (String)conditionMap.get("searchDateType");
		String serviceType = (String)conditionMap.get("serviceType");
		conditionMap.put("locationCondition", getLeafLocationIds((String)conditionMap.get("locationIds"), (String)conditionMap.get("supplierId"))); 
		
		if("EM".equals(serviceType)) {
			if("1".equals(searchDateType) || "3".equals(searchDateType)) {  // 일별, 주별			
				total = dayEmDao.getElecCustomerBillingGridDataCount(conditionMap);						
			} else if("4".equals(searchDateType) || "7".equals(searchDateType)) { // 월별, 분기별		
				total = monthEmDao.getElecCustomerBillingGridDataCount(conditionMap);				
			}				
		} else if("GM".equals(serviceType)) {
			if("1".equals(searchDateType) || "3".equals(searchDateType)) {  // 일별, 주별			
				total = dayGmDao.getElecCustomerBillingGridDataCount(conditionMap);						
			} else if("4".equals(searchDateType) || "7".equals(searchDateType)) { // 월별, 분기별		
				total = monthGmDao.getElecCustomerBillingGridDataCount(conditionMap);				
			}	
		} else if("HM".equals(serviceType)) {
			if("1".equals(searchDateType) || "3".equals(searchDateType)) {  // 일별, 주별			
				total = dayHmDao.getElecCustomerBillingGridDataCount(conditionMap);						
			} else if("4".equals(searchDateType) || "7".equals(searchDateType)) { // 월별, 분기별		
				total = monthHmDao.getElecCustomerBillingGridDataCount(conditionMap);				
			}	
		} else if("WM".equals(serviceType)) {
			if("1".equals(searchDateType) || "3".equals(searchDateType)) {  // 일별, 주별			
				total = dayWmDao.getElecCustomerBillingGridDataCount(conditionMap);						
			} else if("4".equals(searchDateType) || "7".equals(searchDateType)) { // 월별, 분기별		
				total = monthWmDao.getElecCustomerBillingGridDataCount(conditionMap);				
			}	
		}
		
		return total;	
	}
	
	@SuppressWarnings("rawtypes")
	private List<Map<String, String>> makeCustomerGridData(List objects, Map<String, Object> conditionMap) {
		
		List<Map<String, String>> rtnList = new ArrayList<Map<String, String>>();
		Map<String, String> rtnMap = null;
		Double usageCharge = null;
		
		DayEM dayEM = null;
		DayGM dayGM = null;
		DayHM dayHM = null;
		DayWM dayWM = null;
		
		MonthEM monthEM = null;
		MonthGM monthGM = null;
		MonthHM monthHM = null;
		MonthWM monthWM = null;

		 conditionMap.put("dateType", conditionMap.get("searchDateType"));
		 
		for(Object object : objects) {
			
			rtnMap = new HashMap<String, String>();
			
			if(object instanceof DayEM) { 

				dayEM = (DayEM)object;

				rtnMap.put("yyyymmdd", dayEM.getId().getYyyymmdd());
				rtnMap.put("customerName", dayEM.getContract().getCustomer().getName());
				rtnMap.put("contractNo", dayEM.getContract().getContractNumber());
				rtnMap.put("meterName", (dayEM.getMeter() == null) ? null : dayEM.getMeter().getMdsId());
				rtnMap.put("total", StringUtil.nullToBlank(dayEM.getTotal()) + "");
				rtnMap.put("usage", (dayEM.getTotal() + Double.valueOf(StringUtil.nullToZero(dayEM.getBaseValue()))) + "");

				rtnMap.put("max", getMaxValue(dayEM));
				
//				conditionMap.put("contract", contractDao.get(dayEM.getContract().getId()));
//				usageCharge = tariffEMDao.getUsageChargeByContract(conditionMap);
//				String sUsageCharge = "0";
//				if(usageCharge != null) sUsageCharge = usageCharge.toString();
//				rtnMap.put("usageCharge", sUsageCharge);
				
			} else if(object instanceof DayGM) { 

				dayGM = (DayGM)object;
				
				rtnMap.put("yyyymmdd", dayGM.getId().getYyyymmdd());
				rtnMap.put("customerName", dayGM.getContract().getCustomer().getName());
				rtnMap.put("contractNo", dayGM.getContract().getContractNumber());
				rtnMap.put("meterName", (dayGM.getMeter() == null) ? null : dayGM.getMeter().getMdsId());
				rtnMap.put("total", StringUtil.nullToBlank(dayGM.getTotal()) + "");
				rtnMap.put("usage", (dayGM.getTotal() + Double.valueOf(StringUtil.nullToZero(dayGM.getBaseValue()))) + "");
				rtnMap.put("max", getMaxValue(dayGM));		
				
//				conditionMap.put("contract", contractDao.get(dayGM.getContract().getId()));
//				usageCharge = tariffGMDao.getUsageChargeByContract(conditionMap);
//				String sUsageCharge = "0";
//				if(usageCharge != null) sUsageCharge = usageCharge.toString();				
//				rtnMap.put("usageCharge", sUsageCharge);	
				
			} else if(object instanceof DayHM) { 

				dayHM = (DayHM)object;
				
				rtnMap.put("yyyymmdd", dayHM.getId().getYyyymmdd());
				rtnMap.put("customerName", dayHM.getContract().getCustomer().getName());
				rtnMap.put("contractNo", dayHM.getContract().getContractNumber());
				rtnMap.put("meterName", (dayHM.getMeter() == null) ? null : dayHM.getMeter().getMdsId());
				rtnMap.put("total", StringUtil.nullToBlank(dayHM.getTotal()) + "");
				rtnMap.put("usage", (dayHM.getTotal() + Double.valueOf(StringUtil.nullToZero(dayHM.getBaseValue()))) + "");
				rtnMap.put("max", getMaxValue(dayHM));
				
			} else if(object instanceof DayWM ) { 

				dayWM = (DayWM)object;
				
				rtnMap.put("yyyymmdd", dayWM.getId().getYyyymmdd());
				rtnMap.put("customerName", dayWM.getContract().getCustomer().getName());
				rtnMap.put("contractNo", dayWM.getContract().getContractNumber());
				rtnMap.put("meterName", (dayWM.getMeter() == null) ? null : dayWM.getMeter().getMdsId());
				rtnMap.put("total", StringUtil.nullToBlank(dayWM.getTotal()) + "");
				rtnMap.put("usage", (dayWM.getTotal() + Double.valueOf(StringUtil.nullToZero(dayWM.getBaseValue()))) + "");
				rtnMap.put("max", getMaxValue(dayWM));
				
//				conditionMap.put("contract", contractDao.get(dayWM.getContract().getId()));
//				usageCharge = tariffWMDao.getUsageChargeByContract(conditionMap);
//				rtnMap.put("usageCharge", usageCharge.toString());
				
			} else if(object instanceof MonthEM) {
				
				monthEM = (MonthEM)object;

				rtnMap.put("yyyymmdd", monthEM.getId().getYyyymm());
				rtnMap.put("customerName", monthEM.getContract().getCustomer().getName());
				rtnMap.put("contractNo", monthEM.getContract().getContractNumber());
				rtnMap.put("meterName", (monthEM.getMeter() == null) ? null : monthEM.getMeter().getMdsId());			
				rtnMap.put("total", StringUtil.nullToBlank(monthEM.getTotal()) + "");
				rtnMap.put("usage", (monthEM.getTotal() + Double.valueOf(StringUtil.nullToZero(monthEM.getBaseValue()))) + "");
				rtnMap.put("max", getMaxValue(monthEM));			
				
//				conditionMap.put("contract", contractDao.get(monthEM.getContract().getId()));
//				usageCharge = tariffEMDao.getUsageChargeByContract(conditionMap);
//				String sUsageCharge = "0";
//				if(usageCharge != null) sUsageCharge = usageCharge.toString();				
//				rtnMap.put("usageCharge", sUsageCharge);	
				
			} else if(object instanceof MonthGM) {
				
				monthGM = (MonthGM)object;
				
				rtnMap.put("yyyymmdd", monthGM.getId().getYyyymm());
				rtnMap.put("customerName", monthGM.getContract().getCustomer().getName());
				rtnMap.put("contractNo", monthGM.getContract().getContractNumber());
				rtnMap.put("meterName", (monthGM.getMeter() == null) ? null : monthGM.getMeter().getMdsId());			
				rtnMap.put("total", StringUtil.nullToBlank(monthGM.getTotal()) + "");
				rtnMap.put("usage", (monthGM.getTotal() + Double.valueOf(StringUtil.nullToZero(monthGM.getBaseValue()))) + "");
				rtnMap.put("max", getMaxValue(monthGM));		
				
//				conditionMap.put("contract", contractDao.get(monthGM.getContract().getId()));
//				usageCharge = tariffGMDao.getUsageChargeByContract(conditionMap);
//				String sUsageCharge = "0";
//				if(usageCharge != null) sUsageCharge = usageCharge.toString();				
//				rtnMap.put("usageCharge", sUsageCharge);					
				
			} else if(object instanceof MonthHM) {
			
				monthHM = (MonthHM)object;
				
				rtnMap.put("yyyymmdd", monthHM.getId().getYyyymm());
				rtnMap.put("customerName", monthHM.getContract().getCustomer().getName());
				rtnMap.put("contractNo", monthHM.getContract().getContractNumber());
				rtnMap.put("meterName", (monthHM.getMeter() == null) ? null : monthHM.getMeter().getMdsId());
				rtnMap.put("total", StringUtil.nullToBlank(monthHM.getTotal()) + "");
				rtnMap.put("usage", (monthHM.getTotal() + Double.valueOf(StringUtil.nullToZero(monthHM.getBaseValue()))) + "");
				rtnMap.put("max", getMaxValue(monthHM));			
		
			} else if(object instanceof MonthWM) {
				
				monthWM = (MonthWM)object;
				
				rtnMap.put("yyyymmdd", monthWM.getId().getYyyymm());
				rtnMap.put("customerName", monthWM.getContract().getCustomer().getName());
				rtnMap.put("contractNo", monthWM.getContract().getContractNumber());
				rtnMap.put("meterName", (monthWM.getMeter() == null) ? null : monthWM.getMeter().getMdsId());
				rtnMap.put("total", StringUtil.nullToBlank(monthWM.getTotal()) + "");
				rtnMap.put("usage", (monthWM.getTotal() + Double.valueOf(StringUtil.nullToZero(monthWM.getBaseValue()))) + "");
				rtnMap.put("max", getMaxValue(monthWM));	
				
//				conditionMap.put("contract", contractDao.get(monthWM.getContract().getId()));
//				usageCharge = tariffWMDao.getUsageChargeByContract(conditionMap);
//				String sUsageCharge = "0";
//				if(usageCharge != null) sUsageCharge = usageCharge.toString();				
//				rtnMap.put("usageCharge", sUsageCharge);									
			}				
			
			rtnList.add(rtnMap);
		}
		
		return rtnList;
	}
	
	private String getMaxValue(Object object) {
		
		List<Double> list = new ArrayList<Double>();
		
		Class<?> clazz = object.getClass().getSuperclass();
		DecimalFormat df = new DecimalFormat("00");
		
		try {
			
			if(object instanceof DayEM || object instanceof DayGM || object instanceof DayHM || object instanceof DayWM ) {				
				
				for(int i = 0 ; i <= 23; i++) {
					
					Object val = clazz.getDeclaredMethod("getValue_" + df.format(i)).invoke(object);
					if(val != null && !"".equals(val.toString())){
						list.add(Double.parseDouble(val.toString()));
					}	
				}

			} else if(object instanceof MonthEM || object instanceof MonthGM || object instanceof MonthHM || object instanceof MonthWM) {
				
				for(int i = 1 ; i <= 31; i++) {
					Object val = clazz.getDeclaredMethod("getValue_" + df.format(i)).invoke(object);
					if(val != null && !"".equals(val.toString())){
						list.add(Double.parseDouble(val.toString()));
					}	
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return Collections.max(list) + "";
	}

	public List<Map<String, String>> getContractBillingChartData(Map<String, String> conditionMap) {

		List<Map<String, String>> rtnVal = null;		
		String serviceType = conditionMap.get("serviceTypeTab");
		
		if("EM".equals(serviceType)) {
			rtnVal = monthEmDao.getContractBillingChartData(conditionMap);
		} else if("GM".equals(serviceType)) {
			rtnVal = monthGmDao.getContractBillingChartData(conditionMap);
		} else if("WM".equals(serviceType)) {
			rtnVal = monthEmDao.getContractBillingChartData(conditionMap);
		} else if("HM".equals(serviceType)) {
			rtnVal = monthHmDao.getContractBillingChartData(conditionMap);
		}
		
		return rtnVal;
	}

    public Long getMeteringDataCount(Map<String, Object> conditionMap) {
        Long total = 0L;
        List<Map<String, Object>> list;

        String reportType = (String)conditionMap.get("reportType");
        conditionMap.put("locationCondition", getLeafLocationId((String)conditionMap.get("locationId"), (String)conditionMap.get("supplierId")));

        if(StringUtil.nullToBlank(reportType).equals("daily")) {  // 일간
            list = billingDayEMDao.getBillingDataDaily(conditionMap, true);
            total = (Long)list.get(0).get("total");
        } else if(StringUtil.nullToBlank(reportType).equals("monthly")) { // 월간
            list = billingMonthEMDao.getBillingDataMonthly(conditionMap, true);
            total = (Long)list.get(0).get("total");
        } else if(StringUtil.nullToBlank(reportType).equals("current")) { // Current
            list = realtimebillingemDao.getBillingDataCurrent(conditionMap, true);
            total = (Long)list.get(0).get("total");
        }

        return total;
    }

    public List<Map<String, Object>> getMeteringData(Map<String, Object> conditionMap) {

        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();

        String reportType = (String)conditionMap.get("reportType");
        String page = (String)conditionMap.get("page");
        String rowPerPage = (String)conditionMap.get("pageSize");
        Supplier supplier = supplierDao.get(Integer.parseInt((String)conditionMap.get("supplierId")));
        String lang = supplier.getLang().getCode_2letter();
        String country = supplier.getCountry().getCode_2letter();
        DecimalFormat df = DecimalUtil.getDecimalFormat(supplier.getMd());

        conditionMap.put("locationCondition", getLeafLocationId((String)conditionMap.get("locationId"), (String)conditionMap.get("supplierId")));

        if (StringUtil.nullToBlank(reportType).equals("daily")) {  // 일간
            result = billingDayEMDao.getBillingDataDaily(conditionMap, false);
        } else if (StringUtil.nullToBlank(reportType).equals("monthly")) { // 월간
            result = billingMonthEMDao.getBillingDataMonthly(conditionMap, false);
        } else if (StringUtil.nullToBlank(reportType).equals("current")) { // Current
            result = realtimebillingemDao.getBillingDataCurrent(conditionMap, false);
        }

        // 일자, 숫자 formatting
        int count=1;
        for (Map<String, Object> data: result) {
        	data.put("rownum", Integer.toString(CommonUtils2.makeIdxPerPage(String.valueOf(page), String.valueOf(rowPerPage), count)));
            data.put("dateview", TimeLocaleUtil.getLocaleDate((String)data.get("yyyymmdd") + (String)data.get("hhmmss"), supplier.getLang().getCode_2letter(), supplier.getCountry().getCode_2letter()));
            data.put("energyRateTot", df.format((StringUtil.nullToDoubleZero((Double)data.get("energyRateTot"))).doubleValue()));
            data.put("contractDemand", df.format((StringUtil.nullToDoubleZero((Double)data.get("contractDemand"))).doubleValue()));
            data.put("demandRateTot", df.format((StringUtil.nullToDoubleZero((Double)data.get("demandRateTot"))).doubleValue()));
            data.put("kVah", df.format((StringUtil.nullToDoubleZero((Double)data.get("kVah"))).doubleValue()));
            data.put("maxDmdkVah", df.format((StringUtil.nullToDoubleZero((Double)data.get("maxDmdkVah"))).doubleValue()));
            data.put("maxDmdkVahRate1", df.format((StringUtil.nullToDoubleZero((Double)data.get("maxDmdkVahRate1"))).doubleValue()));
            data.put("maxDmdkVahRate2", df.format((StringUtil.nullToDoubleZero((Double)data.get("maxDmdkVahRate2"))).doubleValue()));
            data.put("maxDmdkVahRate3", df.format((StringUtil.nullToDoubleZero((Double)data.get("maxDmdkVahRate3"))).doubleValue()));

            data.put("maxDmdkVahTime", TimeLocaleUtil.getLocaleDate((String)data.get("maxDmdkVahTime"), lang, country));
            data.put("maxDmdkVahTimeRate1", TimeLocaleUtil.getLocaleDate((String)data.get("maxDmdkVahTimeRate1"), lang, country));
            data.put("maxDmdkVahTimeRate2", TimeLocaleUtil.getLocaleDate((String)data.get("maxDmdkVahTimeRate2"), lang, country));
            data.put("maxDmdkVahTimeRate3", TimeLocaleUtil.getLocaleDate((String)data.get("maxDmdkVahTimeRate3"), lang, country));

            data.put("mdevType", ((DeviceType)data.get("mdevType") != null) ? ((DeviceType)data.get("mdevType")).getCode() : "");

            data.put("impkWhPhaseA", df.format((StringUtil.nullToDoubleZero(data.get("impkWhPhaseA")==null?0.0:(Double)data.get("impkWhPhaseA"))).doubleValue()));
            data.put("impkWhPhaseB", df.format((StringUtil.nullToDoubleZero(data.get("impkWhPhaseB")==null?0.0:(Double)data.get("impkWhPhaseB"))).doubleValue()));
            data.put("impkWhPhaseC", df.format((StringUtil.nullToDoubleZero(data.get("impkWhPhaseC")==null?0.0:(Double)data.get("impkWhPhaseC"))).doubleValue()));
            count++;
        }

        return result;
    }

    /**
     * @param conditionMap
     * @return
     */
    public List<Map<String, Object>> getMeteringDataReport(Map<String, Object> conditionMap) {
        List<Map<String, Object>> result = null;

        String reportType = StringUtil.nullToBlank(conditionMap.get("reportType"));
        String searchDate = null;
        String lastSearchDate = null;
        String lastData = StringUtil.nullToBlank(conditionMap.get("lastData"));

        Supplier supplier = supplierDao.get(Integer.parseInt((String)conditionMap.get("supplierId")));
        DecimalFormat df = DecimalUtil.getDecimalFormat(supplier.getMd());
        DecimalFormat dfc = DecimalUtil.getDecimalFormat(supplier.getCd());

        conditionMap.put("locationCondition", getLeafLocationId((String)conditionMap.get("locationId"), (String)conditionMap.get("supplierId")));

        if(reportType.equals("daily")) {  // 일간
            result = billingDayEMDao.getBillingDataReportDaily(conditionMap);
            searchDate = (String)conditionMap.get("searchStartDate");
            lastSearchDate = (String)conditionMap.get("lastStartDate");
        } else if(reportType.equals("monthly")) { // 월간
            if (!lastData.isEmpty()) {
                // 전월 데이터 포함
                result = billingMonthEMDao.getBillingDataReportMonthlyWithLastMonth(conditionMap);
            } else {
                // 전월 데이터 제외
                result = billingMonthEMDao.getBillingDataReportMonthly(conditionMap);
            }
            searchDate = ((String)conditionMap.get("searchStartDate")).substring(0, 6);
            lastSearchDate = ((String)conditionMap.get("lastStartDate")).substring(0, 6);
        } else if(reportType.equals("current")) {  // Current
            result = realtimebillingemDao.getBillingDataReportCurrent(conditionMap);
            searchDate = (String)conditionMap.get("searchStartDate");
            lastSearchDate = (String)conditionMap.get("lastStartDate");
        }

        // formatting 할 항목 key 패턴 정의
        String regTime   = ".*ACT.*TIME.*";
        String regImpTot = ".*ACT.*IMP.*TOT.*";
        String regImpRat = ".*ACT.*IMP.*RAT.*";
        String regExpTot = ".*ACT.*EXP.*TOT.*";
        String regExpRat = ".*ACT.*EXP.*RAT.*";
        String cumKvahRat = ".*CUM.*KVAH.*RAT.*";
        String maxKvahRat = ".*MAX.*KVAH1RAT.*";
        String maxKvahTime = ".*MAX.*KVAH1TIME.*";


        Pattern pTime   = Pattern.compile(regTime);
        Pattern pImpTot = Pattern.compile(regImpTot);
        Pattern pImpRat = Pattern.compile(regImpRat);
        Pattern pExpTot = Pattern.compile(regExpTot);
        Pattern pExpRat = Pattern.compile(regExpRat);
        Pattern pCumKvahRat = Pattern.compile(cumKvahRat);
        Pattern pMaxKvahRat = Pattern.compile(maxKvahRat);
        Pattern pMaxKvahTime = Pattern.compile(maxKvahTime);
        
        Matcher mTime   = null;
        Matcher mImpTot = null;
        Matcher mImpRat = null;
        Matcher mExpTot = null;
        Matcher mExpRat = null;
        Matcher mCumKvahRat = null;
        Matcher mMaxKvahRat = null;
        Matcher mMaxKvahTime = null;
        String key      = null;
        
        int rownum = 0;

        // 일자, 숫자 formatting
        for(Map<String, Object> data: result) {
            Set<String> set = data.keySet();
            Iterator<String> itr = set.iterator();

            while (itr.hasNext()) {
                key = itr.next();
                
                mTime   = pTime.matcher(key);
                mImpTot = pImpTot.matcher(key);
                mImpRat = pImpRat.matcher(key);
                mExpTot = pExpTot.matcher(key);
                mExpRat = pExpRat.matcher(key);
                mCumKvahRat 	= pCumKvahRat.matcher(key);
                mMaxKvahRat 	= pMaxKvahRat.matcher(key);
                mMaxKvahTime 	= pMaxKvahTime.matcher(key);

                if(mTime.find()) {
                    data.put(key, TimeLocaleUtil.getLocaleDate((String)data.get(key), supplier.getLang().getCode_2letter(), supplier.getCountry().getCode_2letter()));
                }else if(mImpTot.find()) {
                    data.put(key, df.format((StringUtil.nullToDoubleZero((Double)data.get(key))).doubleValue()));
                } else if(mImpRat.find()) {
                    data.put(key, df.format((StringUtil.nullToDoubleZero((Double)data.get(key))).doubleValue()));
                } else if(mExpTot.find()) {
                    data.put(key, df.format((StringUtil.nullToDoubleZero((Double)data.get(key))).doubleValue()));
                } else if(mExpRat.find()) {
                    data.put(key, df.format((StringUtil.nullToDoubleZero((Double)data.get(key))).doubleValue()));
                }else if(mCumKvahRat.find()) {
                	data.put(key, df.format((StringUtil.nullToDoubleZero((Double)data.get(key))).doubleValue()));
                }else if(mMaxKvahRat.find()) {
                	data.put(key, df.format((StringUtil.nullToDoubleZero((Double)data.get(key))).doubleValue()));
                }else if(mMaxKvahTime.find()) {
                    data.put(key, TimeLocaleUtil.getLocaleDate((String)data.get(key), supplier.getLang().getCode_2letter(), supplier.getCountry().getCode_2letter()));
                }
            }
            
            if(reportType.equals("daily") || reportType.equals("current")) {  // 일간 / Current
                data.put("searchDate", TimeLocaleUtil.getLocaleDate(searchDate, supplier.getLang().getCode_2letter(), supplier.getCountry().getCode_2letter()));
                data.put("lastSearchDate", TimeLocaleUtil.getLocaleDate(lastSearchDate, supplier.getLang().getCode_2letter(), supplier.getCountry().getCode_2letter()));

                if (!StringUtil.nullToBlank(data.get("HHMMSS")).isEmpty()) {
                    data.put("YYYYMMDD", TimeLocaleUtil.getLocaleDate((String)data.get("YYYYMMDD") + (String)data.get("HHMMSS"), supplier.getLang().getCode_2letter(), supplier.getCountry().getCode_2letter()));

                } else {
                    data.put("YYYYMMDD", TimeLocaleUtil.getLocaleDate((String)data.get("YYYYMMDD"), supplier.getLang().getCode_2letter(), supplier.getCountry().getCode_2letter()));
                }
            } else { // 월간
                data.put("searchDate", TimeLocaleUtil.getLocaleYearMonth(searchDate, supplier.getLang().getCode_2letter(), supplier.getCountry().getCode_2letter()));
                data.put("lastSearchDate", TimeLocaleUtil.getLocaleYearMonth(lastSearchDate, supplier.getLang().getCode_2letter(), supplier.getCountry().getCode_2letter()));

                if (lastData.isEmpty()) {

                    if (!StringUtil.nullToBlank(data.get("HHMMSS")).isEmpty()) {
                        data.put("YYYYMMDD", TimeLocaleUtil.getLocaleDate((String)data.get("YYYYMMDD") + (String)data.get("HHMMSS"), supplier.getLang().getCode_2letter(), supplier.getCountry().getCode_2letter()));
                    } else {
                        data.put("YYYYMMDD", TimeLocaleUtil.getLocaleDate((String)data.get("YYYYMMDD"), supplier.getLang().getCode_2letter(), supplier.getCountry().getCode_2letter()));
                    }
                } else {
                    data.put("YYYYMMDD", TimeLocaleUtil.getLocaleYearMonth((String)data.get("YYYYMMDD"), supplier.getLang().getCode_2letter(), supplier.getCountry().getCode_2letter()));
                }
            }
            data.put("CONTRACTDEMAND", dfc.format((StringUtil.nullToDoubleZero((Double)data.get("CONTRACTDEMAND"))).doubleValue())+"(kW)");
            data.put("rowNum", Integer.toString(++rownum));
            data.put("KVAH", dfc.format((StringUtil.nullToDoubleZero((Double)data.get("KVAH"))).doubleValue()));
            
        }

        return result;
    }

    /**
     * @param conditionMap
     * @return
     */
    public List<Map<String, Object>> getMeteringDetailData(Map<String, Object> conditionMap) {
        List<Map<String, Object>> result = null;

        String reportType = StringUtil.nullToBlank(conditionMap.get("reportType"));
        String lastData = StringUtil.nullToBlank(conditionMap.get("lastData"));

        Supplier supplier = supplierDao.get(Integer.parseInt((String)conditionMap.get("supplierId")));
        String lang = supplier.getLang().getCode_2letter();
        String country = supplier.getCountry().getCode_2letter();
        DecimalFormat dfc = DecimalUtil.getDecimalFormat(supplier.getCd());

        if(reportType.equals("daily")) {  // 일간
            result = billingDayEMDao.getBillingDetailDataDaily(conditionMap);
        } else if(reportType.equals("monthly")) { // 월간
            result = billingMonthEMDao.getBillingDetailDataMonthly(conditionMap);
        } else if(reportType.equals("current")) {  // Current
            result = realtimebillingemDao.getBillingDetailDataCurrent(conditionMap);
        }

        // 일자, 숫자 formatting
        for(Map<String, Object> data: result) {
            
//            if(reportType.equals("daily") || reportType.equals("current")) {  // 일간 / Current
//
//                if (!StringUtil.nullToBlank(data.get("hhmmss")).isEmpty()) {
//                    data.put("yyyymmddhhmmss", TimeLocaleUtil.getLocaleDate((String)data.get("yyyymmdd") + (String)data.get("hhmmss"), lang, country));
//
//                } else {
//                    data.put("yyyymmddhhmmss", TimeLocaleUtil.getLocaleDate((String)data.get("yyyymmdd"), lang, country));
//                }
//            } else { // 월간
//
//                if (lastData.isEmpty()) {
//
//                    if (!StringUtil.nullToBlank(data.get("hhmmss")).isEmpty()) {
//                        data.put("yyyymmddhhmmss", TimeLocaleUtil.getLocaleDate((String)data.get("yyyymmdd") + (String)data.get("hhmmss"), lang, country));
//                    } else {
//                        data.put("yyyymmddhhmmss", TimeLocaleUtil.getLocaleDate((String)data.get("yyyymmdd"), lang, country));
//                    }
//                } else {
//                    data.put("yyyymmddhhmmss", TimeLocaleUtil.getLocaleYearMonth((String)data.get("yyyymmdd"), lang, country));
//                }
//            }
//            data.put("contractDemand", dfc.format((StringUtil.nullToDoubleZero((Double)data.get("contractDemand"))).doubleValue())+"(kW)");

            if(reportType.equals("daily") || reportType.equals("current")) {  // 일간 / Current

                data.put("detailDate", TimeLocaleUtil.getLocaleDate((String)data.get("YYYYMMDD") + (String)data.get("HHMMSS"), lang, country));
                
                if (!lastData.isEmpty()) {
                    data.put("detailLastDate", TimeLocaleUtil.getLocaleDate((String)data.get("LSTYYYYMMDD") + (String)data.get("LSTHHMMSS"), lang, country));
                }
            } else { // 월간

                data.put("detailDate", TimeLocaleUtil.getLocaleDate((String)data.get("YYYYMMDD") + (String)data.get("HHMMSS"), lang, country));
                
                if (!lastData.isEmpty()) {
                    data.put("detailLastDate", TimeLocaleUtil.getLocaleYearMonth((String)data.get("LSTYYYYMMDD"), lang, country));
                }
            }
            data.put("CONTRACTDEMAND", dfc.format((StringUtil.nullToDoubleZero((Double)data.get("CONTRACTDEMAND"))).doubleValue())+"(kW)");

        }

        return result;
    }

    /**
     * method name : getMeteringDetailData<b/>
     * method Desc : TOU Report 의 상세 사용량정보를 조회한다.
     *
     * @param conditionMap
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> getMeteringDetailUsageData(Map<String, Object> conditionMap) {
        List<Map<String, Object>> result = null;
        List<Map<String, Object>> rtnList = new ArrayList<Map<String, Object>>();
        Map<String, String> msgMap = (Map<String, String>)conditionMap.get("msgMap");
        Map<String, Object> rtnMap = new HashMap<String, Object>();

        String reportType = StringUtil.nullToBlank(conditionMap.get("reportType"));
        String searchDate = null;
        String lastSearchDate = null;
        String lastData = StringUtil.nullToBlank(conditionMap.get("lastData"));

//        boolean hasLast = (!StringUtil.nullToBlank(conditionMap.get("lastData")).isEmpty());

        Supplier supplier = supplierDao.get(Integer.parseInt((String)conditionMap.get("supplierId")));
        String lang = supplier.getLang().getCode_2letter();
        String country = supplier.getCountry().getCode_2letter();
        
        DecimalFormat df = DecimalUtil.getDecimalFormat(supplier.getMd());
        DecimalFormat dfc = DecimalUtil.getDecimalFormat(supplier.getCd());

        conditionMap.put("locationCondition", getLeafLocationId((String)conditionMap.get("locationId"), (String)conditionMap.get("supplierId")));

        if(reportType.equals("daily")) {  // 일간
            result = billingDayEMDao.getBillingDataReportDaily(conditionMap);
            searchDate = (String)conditionMap.get("searchStartDate");
            lastSearchDate = (String)conditionMap.get("lastStartDate");
        } else if(reportType.equals("monthly")) { // 월간
            if (!lastData.isEmpty()) {
                // 전월 데이터 포함
                result = billingMonthEMDao.getBillingDataReportMonthlyWithLastMonth(conditionMap);
            } else {
                // 전월 데이터 제외
                result = billingMonthEMDao.getBillingDataReportMonthly(conditionMap);
            }
            searchDate = ((String)conditionMap.get("searchStartDate")).substring(0, 6);
            lastSearchDate = ((String)conditionMap.get("lastStartDate")).substring(0, 6);
        } else if(reportType.equals("current")) {  // Current
            result = realtimebillingemDao.getBillingDataReportCurrent(conditionMap);
            searchDate = (String)conditionMap.get("searchStartDate");
            lastSearchDate = (String)conditionMap.get("lastStartDate");
        }        

        // formatting 할 항목 key 패턴 정의
        String regTime   = ".*ACT.*TIME.*";
        String regImpTot = ".*ACT.*IMP.*TOT.*";
        String regImpRat = ".*ACT.*IMP.*RAT.*";
        String regExpTot = ".*ACT.*EXP.*TOT.*";
        String regExpRat = ".*ACT.*EXP.*RAT.*";
        String cumKvahRat = ".*CUM.*KVAH.*RAT.*";
        String maxKvahRat = ".*MAX.*KVAH1RAT.*";
        String maxKvahTime = ".*MAX.*KVAH1TIME.*";
        String Kvah = ".*KVAH.*";

        Pattern pTime   = Pattern.compile(regTime);
        Pattern pImpTot = Pattern.compile(regImpTot);
        Pattern pImpRat = Pattern.compile(regImpRat);
        Pattern pExpTot = Pattern.compile(regExpTot);
        Pattern pExpRat = Pattern.compile(regExpRat);
        Pattern pCumKvahRat = Pattern.compile(cumKvahRat);
        Pattern pMaxKvahRat = Pattern.compile(maxKvahRat);
        Pattern pMaxKvahTime = Pattern.compile(maxKvahTime);
        Pattern pKvah = Pattern.compile(Kvah);

        Matcher mTime   = null;
        Matcher mImpTot = null;
        Matcher mImpRat = null;
        Matcher mExpTot = null;
        Matcher mExpRat = null;
        Matcher mCumKvahRat = null;
        Matcher mMaxKvahRat = null;
        Matcher mMaxKvahTime = null;
        Matcher mKvah = null;
        String key      = null;

        int rownum = 0;

        if (result.size() <= 0) {
            return result;
        }

        // 일자, 숫자 formatting
        Map<String, Object> resultMap = (Map<String, Object>)result.get(0);

        Set<String> set = resultMap.keySet();
        Iterator<String> itr = set.iterator();

        while (itr.hasNext()) {
            key = itr.next();

            mTime   = pTime.matcher(key);
            mImpTot = pImpTot.matcher(key);
            mImpRat = pImpRat.matcher(key);
            mExpTot = pExpTot.matcher(key);
            mExpRat = pExpRat.matcher(key);

            mCumKvahRat 	= pCumKvahRat.matcher(key);
            mMaxKvahRat 	= pMaxKvahRat.matcher(key);
            mMaxKvahTime 	= pMaxKvahTime.matcher(key);
            mKvah 			= pKvah.matcher(key);

            if(mTime.find()) {
                resultMap.put(key, TimeLocaleUtil.getLocaleDate((String)resultMap.get(key), lang, country));
            }else if(mMaxKvahTime.find()) {
                resultMap.put(key, TimeLocaleUtil.getLocaleDate((String)resultMap.get(key), lang, country));
            }else if(mImpTot.find()) {
                resultMap.put(key, df.format((StringUtil.nullToDoubleZero(
                		Double.parseDouble(resultMap.get(key) == null ? "0" : resultMap.get(key).toString())))));
            } else if(mImpRat.find()) {
                resultMap.put(key, df.format((StringUtil.nullToDoubleZero(
                		Double.parseDouble(resultMap.get(key) == null ? "0" : resultMap.get(key).toString())))));
            } else if(mExpTot.find()) {
                resultMap.put(key, df.format((StringUtil.nullToDoubleZero(
                		Double.parseDouble(resultMap.get(key) == null ? "0" : resultMap.get(key).toString())))));
            } else if(mExpRat.find()) {
                resultMap.put(key, df.format((StringUtil.nullToDoubleZero(
                		Double.parseDouble(resultMap.get(key) == null ? "0" : resultMap.get(key).toString())))));
            }else if(mCumKvahRat.find()) {
                resultMap.put(key, df.format((StringUtil.nullToDoubleZero(
                		Double.parseDouble(resultMap.get(key) == null ? "0" : resultMap.get(key).toString())))));
            }else if(mMaxKvahRat.find()) {
                resultMap.put(key, df.format((StringUtil.nullToDoubleZero(
                		Double.parseDouble(resultMap.get(key) == null ? "0" : resultMap.get(key).toString())))));
            }else if(mKvah.find()) {
                resultMap.put(key, df.format((StringUtil.nullToDoubleZero(
                		Double.parseDouble(resultMap.get(key) == null ? "0" : resultMap.get(key).toString())))));
            }
        }

        if(reportType.equals("daily") || reportType.equals("current")) {  // 일간 / Current
            resultMap.put("searchDate", TimeLocaleUtil.getLocaleDate(searchDate, lang, country));
            resultMap.put("lastSearchDate", TimeLocaleUtil.getLocaleDate(lastSearchDate, lang, country));

            if (!StringUtil.nullToBlank(resultMap.get("HHMMSS")).isEmpty()) {
                resultMap.put("YYYYMMDDHHMMSS", TimeLocaleUtil.getLocaleDate((String)resultMap.get("YYYYMMDD") + (String)resultMap.get("HHMMSS"), lang, country));

            } else {
                resultMap.put("YYYYMMDDHHMMSS", TimeLocaleUtil.getLocaleDate((String)resultMap.get("YYYYMMDD"), lang, country));
            }
        } else { // 월간
            resultMap.put("searchDate", TimeLocaleUtil.getLocaleYearMonth(searchDate, lang, country));
            resultMap.put("lastSearchDate", TimeLocaleUtil.getLocaleYearMonth(lastSearchDate, lang, country));

            if (lastData.isEmpty()) {

                if (!StringUtil.nullToBlank(resultMap.get("HHMMSS")).isEmpty()) {
                    resultMap.put("YYYYMMDDHHMMSS", TimeLocaleUtil.getLocaleDate((String)resultMap.get("YYYYMMDD") + (String)resultMap.get("HHMMSS"), lang, country));
                } else {
                    resultMap.put("YYYYMMDDHHMMSS", TimeLocaleUtil.getLocaleDate((String)resultMap.get("YYYYMMDD"), lang, country));
                }
            } else {
                resultMap.put("YYYYMMDDHHMMSS", TimeLocaleUtil.getLocaleYearMonth((String)resultMap.get("YYYYMMDD"), lang, country));
            }
        }
        resultMap.put("CONTRACTDEMAND", dfc.format((StringUtil.nullToDoubleZero((Double)resultMap.get("CONTRACTDEMAND"))).doubleValue())+"(kW)");
        resultMap.put("rowNum", Integer.toString(++rownum));

        // pivotgrid data 생성
        // Total Energy - Active Import
        rtnMap = new HashMap<String, Object>();
        rtnMap.put("leftFstTitle", makeStyleMsg(msgMap.get("msgTotEnergy"), 1));
        rtnMap.put("leftSndTitle", "");
        rtnMap.put("leftTrdTitle", "");
        rtnMap.put("topTitle", makeStyleMsg(msgMap.get("msgActImp"), 1));
        rtnMap.put("value", makeStyleValue((String)resultMap.get("ACTENGYIMPTOT")));
        rtnList.add(rtnMap);

        // Total Energy - Active Export
        rtnMap = new HashMap<String, Object>();
        rtnMap.put("leftFstTitle", makeStyleMsg(msgMap.get("msgTotEnergy"), 1));
        rtnMap.put("leftSndTitle", "");
        rtnMap.put("leftTrdTitle", "");
        rtnMap.put("topTitle", makeStyleMsg(msgMap.get("msgActExp"), 2));
        rtnMap.put("value", makeStyleValue((String)resultMap.get("ACTENGYEXPTOT")));
        rtnList.add(rtnMap);

        // Total Energy - Reactive Lag Import
        rtnMap = new HashMap<String, Object>();
        rtnMap.put("leftFstTitle", makeStyleMsg(msgMap.get("msgTotEnergy"), 1));
        rtnMap.put("leftSndTitle", "");
        rtnMap.put("leftTrdTitle", "");
        rtnMap.put("topTitle", makeStyleMsg(msgMap.get("msgRactLagImp"), 3));
        rtnMap.put("value", makeStyleValue((String)resultMap.get("RACTENGYLAGIMPTOT")));
        rtnList.add(rtnMap);

        // Total Energy - Reactive Lead Import
        rtnMap = new HashMap<String, Object>();
        rtnMap.put("leftFstTitle", makeStyleMsg(msgMap.get("msgTotEnergy"), 1));
        rtnMap.put("leftSndTitle", "");
        rtnMap.put("leftTrdTitle", "");
        rtnMap.put("topTitle", makeStyleMsg(msgMap.get("msgRactLeadImp"), 4));
        rtnMap.put("value", makeStyleValue((String)resultMap.get("RACTENGYLEADIMPTOT")));
        rtnList.add(rtnMap);

        // Total Energy - Reactive Lag Export
        rtnMap = new HashMap<String, Object>();
        rtnMap.put("leftFstTitle", makeStyleMsg(msgMap.get("msgTotEnergy"), 1));
        rtnMap.put("leftSndTitle", "");
        rtnMap.put("leftTrdTitle", "");
        rtnMap.put("topTitle", makeStyleMsg(msgMap.get("msgRactLagExp"), 5));
        rtnMap.put("value", makeStyleValue((String)resultMap.get("RACTENGYLAGEXPTOT")));
        rtnList.add(rtnMap);

        // Total Energy - Reactive Lead Export
        rtnMap = new HashMap<String, Object>();
        rtnMap.put("leftFstTitle", makeStyleMsg(msgMap.get("msgTotEnergy"), 1));
        rtnMap.put("leftSndTitle", "");
        rtnMap.put("leftTrdTitle", "");
        rtnMap.put("topTitle", makeStyleMsg(msgMap.get("msgRactLeadExp"), 6));
        rtnMap.put("value", makeStyleValue((String)resultMap.get("RACTENGYLEADEXPTOT")));
        rtnList.add(rtnMap);
        
        // Total Energy - kVAh1
        rtnMap = new HashMap<String, Object>();
        rtnMap.put("leftFstTitle", makeStyleMsg(msgMap.get("msgTotEnergy"), 1));
        rtnMap.put("leftSndTitle", "");
        rtnMap.put("leftTrdTitle", "");
        rtnMap.put("topTitle", makeStyleMsg(msgMap.get("msgkVah1"), 7));
        rtnMap.put("value", makeStyleValue((String)resultMap.get("KVAH")));
        rtnList.add(rtnMap);
        // ----------------------------------------------------------------

        // Energy(Rate1) - Active Import
        rtnMap = new HashMap<String, Object>();
        rtnMap.put("leftFstTitle", makeStyleMsg(msgMap.get("msgEnergy"), 2));
        rtnMap.put("leftSndTitle", makeStyleMsg(msgMap.get("msgRate1"), 0));
        rtnMap.put("leftTrdTitle", "");
        rtnMap.put("topTitle", makeStyleMsg(msgMap.get("msgActImp"), 1));
        rtnMap.put("value", makeStyleValue((String)resultMap.get("ACTENGYIMPRAT1")));
        rtnList.add(rtnMap);

        // Energy(Rate1) - Active Export
        rtnMap = new HashMap<String, Object>();
        rtnMap.put("leftFstTitle", makeStyleMsg(msgMap.get("msgEnergy"), 2));
        rtnMap.put("leftSndTitle", makeStyleMsg(msgMap.get("msgRate1"), 0));
        rtnMap.put("leftTrdTitle", "");
        rtnMap.put("topTitle", makeStyleMsg(msgMap.get("msgActExp"), 2));
        rtnMap.put("value", makeStyleValue((String)resultMap.get("ACTENGYEXPRAT1")));
        rtnList.add(rtnMap);

        // Energy(Rate1) - Reactive Lag Import
        rtnMap = new HashMap<String, Object>();
        rtnMap.put("leftFstTitle", makeStyleMsg(msgMap.get("msgEnergy"), 2));
        rtnMap.put("leftSndTitle", makeStyleMsg(msgMap.get("msgRate1"), 0));
        rtnMap.put("leftTrdTitle", "");
        rtnMap.put("topTitle", makeStyleMsg(msgMap.get("msgRactLagImp"), 3));
        rtnMap.put("value", makeStyleValue((String)resultMap.get("RACTENGYLAGIMPRAT1")));
        rtnList.add(rtnMap);

        // Energy(Rate1) - Reactive Lead Import
        rtnMap = new HashMap<String, Object>();
        rtnMap.put("leftFstTitle", makeStyleMsg(msgMap.get("msgEnergy"), 2));
        rtnMap.put("leftSndTitle", makeStyleMsg(msgMap.get("msgRate1"), 0));
        rtnMap.put("leftTrdTitle", "");
        rtnMap.put("topTitle", makeStyleMsg(msgMap.get("msgRactLeadImp"), 4));
        rtnMap.put("value", makeStyleValue((String)resultMap.get("RACTENGYLEADIMPRAT1")));
        rtnList.add(rtnMap);

        // Energy(Rate1) - Reactive Lag Export
        rtnMap = new HashMap<String, Object>();
        rtnMap.put("leftFstTitle", makeStyleMsg(msgMap.get("msgEnergy"), 2));
        rtnMap.put("leftSndTitle", makeStyleMsg(msgMap.get("msgRate1"), 0));
        rtnMap.put("leftTrdTitle", "");
        rtnMap.put("topTitle", makeStyleMsg(msgMap.get("msgRactLagExp"), 5));
        rtnMap.put("value", makeStyleValue((String)resultMap.get("RACTENGYLAGEXPRAT1")));
        rtnList.add(rtnMap);

        // Energy(Rate1) - Reactive Lead Export
        rtnMap = new HashMap<String, Object>();
        rtnMap.put("leftFstTitle", makeStyleMsg(msgMap.get("msgEnergy"), 2));
        rtnMap.put("leftSndTitle", makeStyleMsg(msgMap.get("msgRate1"), 0));
        rtnMap.put("leftTrdTitle", "");
        rtnMap.put("topTitle", makeStyleMsg(msgMap.get("msgRactLeadExp"), 6));
        rtnMap.put("value", makeStyleValue((String)resultMap.get("RACTENGYLEADEXPRAT1")));
        rtnList.add(rtnMap);
        
        // Energy(Rate1) - kVAh1
        rtnMap = new HashMap<String, Object>();
        rtnMap.put("leftFstTitle", makeStyleMsg(msgMap.get("msgEnergy"), 2));
        rtnMap.put("leftSndTitle", makeStyleMsg(msgMap.get("msgRate1"), 0));
        rtnMap.put("leftTrdTitle", "");
        rtnMap.put("topTitle", makeStyleMsg(msgMap.get("msgkVah1"), 7));
        rtnMap.put("value", makeStyleValue("0"));
        rtnList.add(rtnMap);
        // ----------------------------------------------------------------

        // Energy(Rate2) - Active Import
        rtnMap = new HashMap<String, Object>();
        rtnMap.put("leftFstTitle", makeStyleMsg(msgMap.get("msgEnergy"), 2));
        rtnMap.put("leftSndTitle", makeStyleMsg(msgMap.get("msgRate2"), 0));
        rtnMap.put("leftTrdTitle", "");
        rtnMap.put("topTitle", makeStyleMsg(msgMap.get("msgActImp"), 1));
        rtnMap.put("value", makeStyleValue((String)resultMap.get("ACTENGYIMPRAT2")));
        rtnList.add(rtnMap);

        // Energy(Rate2) - Active Export
        rtnMap = new HashMap<String, Object>();
        rtnMap.put("leftFstTitle", makeStyleMsg(msgMap.get("msgEnergy"), 2));
        rtnMap.put("leftSndTitle", makeStyleMsg(msgMap.get("msgRate2"), 0));
        rtnMap.put("leftTrdTitle", "");
        rtnMap.put("topTitle", makeStyleMsg(msgMap.get("msgActExp"), 2));
        rtnMap.put("value", makeStyleValue((String)resultMap.get("ACTENGYEXPRAT2")));
        rtnList.add(rtnMap);

        // Energy(Rate2) - Reactive Lag Import
        rtnMap = new HashMap<String, Object>();
        rtnMap.put("leftFstTitle", makeStyleMsg(msgMap.get("msgEnergy"), 2));
        rtnMap.put("leftSndTitle", makeStyleMsg(msgMap.get("msgRate2"), 0));
        rtnMap.put("leftTrdTitle", "");
        rtnMap.put("topTitle", makeStyleMsg(msgMap.get("msgRactLagImp"), 3));
        rtnMap.put("value", makeStyleValue((String)resultMap.get("RACTENGYLAGIMPRAT2")));
        rtnList.add(rtnMap);

        // Energy(Rate2) - Reactive Lead Import
        rtnMap = new HashMap<String, Object>();
        rtnMap.put("leftFstTitle", makeStyleMsg(msgMap.get("msgEnergy"), 2));
        rtnMap.put("leftSndTitle", makeStyleMsg(msgMap.get("msgRate2"), 0));
        rtnMap.put("leftTrdTitle", "");
        rtnMap.put("topTitle", makeStyleMsg(msgMap.get("msgRactLeadImp"), 4));
        rtnMap.put("value", makeStyleValue((String)resultMap.get("RACTENGYLEADIMPRAT2")));
        rtnList.add(rtnMap);

        // Energy(Rate2) - Reactive Lag Export
        rtnMap = new HashMap<String, Object>();
        rtnMap.put("leftFstTitle", makeStyleMsg(msgMap.get("msgEnergy"), 2));
        rtnMap.put("leftSndTitle", makeStyleMsg(msgMap.get("msgRate2"), 0));
        rtnMap.put("leftTrdTitle", "");
        rtnMap.put("topTitle", makeStyleMsg(msgMap.get("msgRactLagExp"), 5));
        rtnMap.put("value", makeStyleValue((String)resultMap.get("RACTENGYLAGEXPRAT2")));
        rtnList.add(rtnMap);

        // Energy(Rate2) - Reactive Lead Export
        rtnMap = new HashMap<String, Object>();
        rtnMap.put("leftFstTitle", makeStyleMsg(msgMap.get("msgEnergy"), 2));
        rtnMap.put("leftSndTitle", makeStyleMsg(msgMap.get("msgRate2"), 0));
        rtnMap.put("leftTrdTitle", "");
        rtnMap.put("topTitle", makeStyleMsg(msgMap.get("msgRactLeadExp"), 6));
        rtnMap.put("value", makeStyleValue((String)resultMap.get("RACTENGYLEADEXPRAT2")));
        rtnList.add(rtnMap);
        
        // Energy(Rate2) - kVAh1
        rtnMap = new HashMap<String, Object>();
        rtnMap.put("leftFstTitle", makeStyleMsg(msgMap.get("msgEnergy"), 2));
        rtnMap.put("leftSndTitle", makeStyleMsg(msgMap.get("msgRate2"), 0));
        rtnMap.put("leftTrdTitle", "");
        rtnMap.put("topTitle", makeStyleMsg(msgMap.get("msgkVah1"), 7));
        rtnMap.put("value", makeStyleValue("0"));
        rtnList.add(rtnMap);
        // ----------------------------------------------------------------

        // Energy(Rate3) - Active Import
        rtnMap = new HashMap<String, Object>();
        rtnMap.put("leftFstTitle", makeStyleMsg(msgMap.get("msgEnergy"), 2));
        rtnMap.put("leftSndTitle", makeStyleMsg(msgMap.get("msgRate3"), 0));
        rtnMap.put("leftTrdTitle", "");
        rtnMap.put("topTitle", makeStyleMsg(msgMap.get("msgActImp"), 1));
        rtnMap.put("value", makeStyleValue((String)resultMap.get("ACTENGYIMPRAT3")));
        rtnList.add(rtnMap);

        // Energy(Rate3) - Active Export
        rtnMap = new HashMap<String, Object>();
        rtnMap.put("leftFstTitle", makeStyleMsg(msgMap.get("msgEnergy"), 2));
        rtnMap.put("leftSndTitle", makeStyleMsg(msgMap.get("msgRate3"), 0));
        rtnMap.put("leftTrdTitle", "");
        rtnMap.put("topTitle", makeStyleMsg(msgMap.get("msgActExp"), 2));
        rtnMap.put("value", makeStyleValue((String)resultMap.get("ACTENGYEXPRAT3")));
        rtnList.add(rtnMap);

        // Energy(Rate3) - Reactive Lag Import
        rtnMap = new HashMap<String, Object>();
        rtnMap.put("leftFstTitle", makeStyleMsg(msgMap.get("msgEnergy"), 2));
        rtnMap.put("leftSndTitle", makeStyleMsg(msgMap.get("msgRate3"), 0));
        rtnMap.put("leftTrdTitle", "");
        rtnMap.put("topTitle", makeStyleMsg(msgMap.get("msgRactLagImp"), 3));
        rtnMap.put("value", makeStyleValue((String)resultMap.get("RACTENGYLAGIMPRAT3")));
        rtnList.add(rtnMap);

        // Energy(Rate3) - Reactive Lead Import
        rtnMap = new HashMap<String, Object>();
        rtnMap.put("leftFstTitle", makeStyleMsg(msgMap.get("msgEnergy"), 2));
        rtnMap.put("leftSndTitle", makeStyleMsg(msgMap.get("msgRate3"), 0));
        rtnMap.put("leftTrdTitle", "");
        rtnMap.put("topTitle", makeStyleMsg(msgMap.get("msgRactLeadImp"), 4));
        rtnMap.put("value", makeStyleValue((String)resultMap.get("RACTENGYLEADIMPRAT3")));
        rtnList.add(rtnMap);

        // Energy(Rate3) - Reactive Lag Export
        rtnMap = new HashMap<String, Object>();
        rtnMap.put("leftFstTitle", makeStyleMsg(msgMap.get("msgEnergy"), 2));
        rtnMap.put("leftSndTitle", makeStyleMsg(msgMap.get("msgRate3"), 0));
        rtnMap.put("leftTrdTitle", "");
        rtnMap.put("topTitle", makeStyleMsg(msgMap.get("msgRactLagExp"), 5));
        rtnMap.put("value", makeStyleValue((String)resultMap.get("RACTENGYLAGEXPRAT3")));
        rtnList.add(rtnMap);

        // Energy(Rate3) - Reactive Lead Export
        rtnMap = new HashMap<String, Object>();
        rtnMap.put("leftFstTitle", makeStyleMsg(msgMap.get("msgEnergy"), 2));
        rtnMap.put("leftSndTitle", makeStyleMsg(msgMap.get("msgRate3"), 0));
        rtnMap.put("leftTrdTitle", "");
        rtnMap.put("topTitle", makeStyleMsg(msgMap.get("msgRactLeadExp"), 6));
        rtnMap.put("value", makeStyleValue((String)resultMap.get("RACTENGYLEADEXPRAT3")));
        rtnList.add(rtnMap);
        
        // Energy(Rate3) - kVAh1
        rtnMap = new HashMap<String, Object>();
        rtnMap.put("leftFstTitle", makeStyleMsg(msgMap.get("msgEnergy"), 2));
        rtnMap.put("leftSndTitle", makeStyleMsg(msgMap.get("msgRate3"), 0));
        rtnMap.put("leftTrdTitle", "");
        rtnMap.put("topTitle", makeStyleMsg(msgMap.get("msgkVah1"), 7));
        rtnMap.put("value", makeStyleValue("0"));
        rtnList.add(rtnMap);
        // ----------------------------------------------------------------

        // Total Demand and Time (Time) - Active Import
        rtnMap = new HashMap<String, Object>();
        rtnMap.put("leftFstTitle", makeStyleMsg(msgMap.get("msgTotDemandTime"), 3));
        rtnMap.put("leftSndTitle", "&nbsp;");
        rtnMap.put("leftTrdTitle", makeStyleTrdMsg("0"));
        rtnMap.put("topTitle", makeStyleMsg(msgMap.get("msgActImp"), 1));
        rtnMap.put("value", makeStyleValue((String)resultMap.get("ACTDMDMXTIMEIMPTOT")));
        rtnList.add(rtnMap);

        // Total Demand and Time (Time) - Active Export
        rtnMap = new HashMap<String, Object>();
        rtnMap.put("leftFstTitle", makeStyleMsg(msgMap.get("msgTotDemandTime"), 3));
        rtnMap.put("leftSndTitle", "&nbsp;");
        rtnMap.put("leftTrdTitle", makeStyleTrdMsg("0"));
        rtnMap.put("topTitle", makeStyleMsg(msgMap.get("msgActExp"), 2));
        rtnMap.put("value", makeStyleValue((String)resultMap.get("ACTDMDMXTIMEEXPTOT")));
        rtnList.add(rtnMap);

        // Total Demand and Time (Time) - Reactive Lag Import
        rtnMap = new HashMap<String, Object>();
        rtnMap.put("leftFstTitle", makeStyleMsg(msgMap.get("msgTotDemandTime"), 3));
        rtnMap.put("leftSndTitle", "&nbsp;");
        rtnMap.put("leftTrdTitle", makeStyleTrdMsg("0"));
        rtnMap.put("topTitle", makeStyleMsg(msgMap.get("msgRactLagImp"), 3));
        rtnMap.put("value", makeStyleValue((String)resultMap.get("RACTDMDMXTIMELAGIMPTOT")));
        rtnList.add(rtnMap);

        // Total Demand and Time (Time) - Reactive Lead Import
        rtnMap = new HashMap<String, Object>();
        rtnMap.put("leftFstTitle", makeStyleMsg(msgMap.get("msgTotDemandTime"), 3));
        rtnMap.put("leftSndTitle", "&nbsp;");
        rtnMap.put("leftTrdTitle", makeStyleTrdMsg("0"));
        rtnMap.put("topTitle", makeStyleMsg(msgMap.get("msgRactLeadImp"), 4));
        rtnMap.put("value", makeStyleValue((String)resultMap.get("RACTDMDMXTIMELEADIMPTOT")));
        rtnList.add(rtnMap);

        // Total Demand and Time (Time) - Reactive Lag Export
        rtnMap = new HashMap<String, Object>();
        rtnMap.put("leftFstTitle", makeStyleMsg(msgMap.get("msgTotDemandTime"), 3));
        rtnMap.put("leftSndTitle", "&nbsp;");
        rtnMap.put("leftTrdTitle", makeStyleTrdMsg("0"));
        rtnMap.put("topTitle", makeStyleMsg(msgMap.get("msgRactLagExp"), 5));
        rtnMap.put("value", makeStyleValue((String)resultMap.get("RACTDMDMXTIMELAGEXPTOT")));
        rtnList.add(rtnMap);

        // Total Demand and Time (Time) - Reactive Lead Export
        rtnMap = new HashMap<String, Object>();
        rtnMap.put("leftFstTitle", makeStyleMsg(msgMap.get("msgTotDemandTime"), 3));
        rtnMap.put("leftSndTitle", "&nbsp;");
        rtnMap.put("leftTrdTitle", makeStyleTrdMsg("0"));
        rtnMap.put("topTitle", makeStyleMsg(msgMap.get("msgRactLeadExp"), 6));
        rtnMap.put("value", makeStyleValue((String)resultMap.get("RACTDMDMXTIMELEADEXPTOT")));
        rtnList.add(rtnMap);

        // Total Demand and Time (Time) - kVAh1
        rtnMap = new HashMap<String, Object>();
        rtnMap.put("leftFstTitle", makeStyleMsg(msgMap.get("msgTotDemandTime"), 3));
        rtnMap.put("leftSndTitle", "&nbsp;");
        rtnMap.put("leftTrdTitle", makeStyleTrdMsg("0"));
        rtnMap.put("topTitle", makeStyleMsg(msgMap.get("msgkVah1"), 7));
        rtnMap.put("value", makeStyleValue((String)resultMap.get("MAXDMDKVAH1TIMERATETOTAL")));
        rtnList.add(rtnMap);
        // ----------------------------------------------------------------

        // Total Demand and Time - Active Import
        rtnMap = new HashMap<String, Object>();
        rtnMap.put("leftFstTitle", makeStyleMsg(msgMap.get("msgTotDemandTime"), 3));
        rtnMap.put("leftSndTitle", "&nbsp;");
        rtnMap.put("leftTrdTitle", makeStyleTrdMsg("1"));
        rtnMap.put("topTitle", makeStyleMsg(msgMap.get("msgActImp"), 1));
        rtnMap.put("value", makeStyleValue((String)resultMap.get("ACTDMDMXIMPTOT")));
        rtnList.add(rtnMap);

        // Total Demand and Time - Active Export
        rtnMap = new HashMap<String, Object>();
        rtnMap.put("leftFstTitle", makeStyleMsg(msgMap.get("msgTotDemandTime"), 3));
        rtnMap.put("leftSndTitle", "&nbsp;");
        rtnMap.put("leftTrdTitle", makeStyleTrdMsg("1"));
        rtnMap.put("topTitle", makeStyleMsg(msgMap.get("msgActExp"), 2));
        rtnMap.put("value", makeStyleValue((String)resultMap.get("ACTDMDMXEXPTOT")));
        rtnList.add(rtnMap);

        // Total Demand and Time - Reactive Lag Import
        rtnMap = new HashMap<String, Object>();
        rtnMap.put("leftFstTitle", makeStyleMsg(msgMap.get("msgTotDemandTime"), 3));
        rtnMap.put("leftSndTitle", "&nbsp;");
        rtnMap.put("leftTrdTitle", makeStyleTrdMsg("1"));
        rtnMap.put("topTitle", makeStyleMsg(msgMap.get("msgRactLagImp"), 3));
        rtnMap.put("value", makeStyleValue((String)resultMap.get("RACTDMDMXLAGIMPTOT")));
        rtnList.add(rtnMap);

        // Total Demand and Time - Reactive Lead Import
        rtnMap = new HashMap<String, Object>();
        rtnMap.put("leftFstTitle", makeStyleMsg(msgMap.get("msgTotDemandTime"), 3));
        rtnMap.put("leftSndTitle", "&nbsp;");
        rtnMap.put("leftTrdTitle", makeStyleTrdMsg("1"));
        rtnMap.put("topTitle", makeStyleMsg(msgMap.get("msgRactLeadImp"), 4));
        rtnMap.put("value", makeStyleValue((String)resultMap.get("RACTDMDMXLEADIMPTOT")));
        rtnList.add(rtnMap);

        // Total Demand and Time - Reactive Lag Export
        rtnMap = new HashMap<String, Object>();
        rtnMap.put("leftFstTitle", makeStyleMsg(msgMap.get("msgTotDemandTime"), 3));
        rtnMap.put("leftSndTitle", "&nbsp;");
        rtnMap.put("leftTrdTitle", makeStyleTrdMsg("1"));
        rtnMap.put("topTitle", makeStyleMsg(msgMap.get("msgRactLagExp"), 5));
        rtnMap.put("value", makeStyleValue((String)resultMap.get("RACTDMDMXLAGEXPTOT")));
        rtnList.add(rtnMap);

        // Total Demand and Time - Reactive Lead Export
        rtnMap = new HashMap<String, Object>();
        rtnMap.put("leftFstTitle", makeStyleMsg(msgMap.get("msgTotDemandTime"), 3));
        rtnMap.put("leftSndTitle", "&nbsp;");
        rtnMap.put("leftTrdTitle", makeStyleTrdMsg("1"));
        rtnMap.put("topTitle", makeStyleMsg(msgMap.get("msgRactLeadExp"), 6));
        rtnMap.put("value", makeStyleValue((String)resultMap.get("RACTDMDMXLEADEXPTOT")));
        rtnList.add(rtnMap);
        
        // Total Demand and Time - kVAh1
        rtnMap = new HashMap<String, Object>();
        rtnMap.put("leftFstTitle", makeStyleMsg(msgMap.get("msgTotDemandTime"), 3));
        rtnMap.put("leftSndTitle", "&nbsp;");
        rtnMap.put("leftTrdTitle", makeStyleTrdMsg("1"));
        rtnMap.put("topTitle", makeStyleMsg(msgMap.get("msgkVah1"), 7));
        rtnMap.put("value", makeStyleValue((String)resultMap.get("MAXDMDKVAH1RATETOTAL")));
        rtnList.add(rtnMap);
        // ----------------------------------------------------------------

        // Max Demand and Time(Rate1-Time) - Active Import
        rtnMap = new HashMap<String, Object>();
        rtnMap.put("leftFstTitle", makeStyleMsg(msgMap.get("msgMaxDemandTime"), 4));
        rtnMap.put("leftSndTitle", makeStyleMsg(msgMap.get("msgRate1"), 0));
        rtnMap.put("leftTrdTitle", makeStyleTrdMsg("0"));
        rtnMap.put("topTitle", makeStyleMsg(msgMap.get("msgActImp"), 1));
        rtnMap.put("value", makeStyleValue((String)resultMap.get("ACTDMDMXTIMEIMPRAT1")));
        rtnList.add(rtnMap);

        // Max Demand and Time(Rate1-Time) - Active Export
        rtnMap = new HashMap<String, Object>();
        rtnMap.put("leftFstTitle", makeStyleMsg(msgMap.get("msgMaxDemandTime"), 4));
        rtnMap.put("leftSndTitle", makeStyleMsg(msgMap.get("msgRate1"), 0));
        rtnMap.put("leftTrdTitle", makeStyleTrdMsg("0"));
        rtnMap.put("topTitle", makeStyleMsg(msgMap.get("msgActExp"), 2));
        rtnMap.put("value", makeStyleValue((String)resultMap.get("ACTDMDMXTIMEEXPRAT1")));
        rtnList.add(rtnMap);

        // Max Demand and Time(Rate1-Time) - Reactive Lag Import
        rtnMap = new HashMap<String, Object>();
        rtnMap.put("leftFstTitle", makeStyleMsg(msgMap.get("msgMaxDemandTime"), 4));
        rtnMap.put("leftSndTitle", makeStyleMsg(msgMap.get("msgRate1"), 0));
        rtnMap.put("leftTrdTitle", makeStyleTrdMsg("0"));
        rtnMap.put("topTitle", makeStyleMsg(msgMap.get("msgRactLagImp"), 3));
        rtnMap.put("value", makeStyleValue((String)resultMap.get("RACTDMDMXTIMELAGIMPRAT1")));
        rtnList.add(rtnMap);

        // Max Demand and Time(Rate1-Time) - Reactive Lead Import
        rtnMap = new HashMap<String, Object>();
        rtnMap.put("leftFstTitle", makeStyleMsg(msgMap.get("msgMaxDemandTime"), 4));
        rtnMap.put("leftSndTitle", makeStyleMsg(msgMap.get("msgRate1"), 0));
        rtnMap.put("leftTrdTitle", makeStyleTrdMsg("0"));
        rtnMap.put("topTitle", makeStyleMsg(msgMap.get("msgRactLeadImp"), 4));
        rtnMap.put("value", makeStyleValue((String)resultMap.get("RACTDMDMXTIMELEADIMPRAT1")));
        rtnList.add(rtnMap);

        // Max Demand and Time(Rate1-Time) - Reactive Lag Export
        rtnMap = new HashMap<String, Object>();
        rtnMap.put("leftFstTitle", makeStyleMsg(msgMap.get("msgMaxDemandTime"), 4));
        rtnMap.put("leftSndTitle", makeStyleMsg(msgMap.get("msgRate1"), 0));
        rtnMap.put("leftTrdTitle", makeStyleTrdMsg("0"));
        rtnMap.put("topTitle", makeStyleMsg(msgMap.get("msgRactLagExp"), 5));
        rtnMap.put("value", makeStyleValue((String)resultMap.get("RACTDMDMXTIMELAGEXPRAT1")));
        rtnList.add(rtnMap);

        // Max Demand and Time(Rate1-Time) - Reactive Lead Export
        rtnMap = new HashMap<String, Object>();
        rtnMap.put("leftFstTitle", makeStyleMsg(msgMap.get("msgMaxDemandTime"), 4));
        rtnMap.put("leftSndTitle", makeStyleMsg(msgMap.get("msgRate1"), 0));
        rtnMap.put("leftTrdTitle", makeStyleTrdMsg("0"));
        rtnMap.put("topTitle", makeStyleMsg(msgMap.get("msgRactLeadExp"), 6));
        rtnMap.put("value", makeStyleValue((String)resultMap.get("RACTDMDMXTIMELEADEXPRAT1")));
        rtnList.add(rtnMap);
        
        // Max Demand and Time(Rate1-Time) - kVAh1
        rtnMap = new HashMap<String, Object>();
        rtnMap.put("leftFstTitle", makeStyleMsg(msgMap.get("msgMaxDemandTime"), 4));
        rtnMap.put("leftSndTitle", makeStyleMsg(msgMap.get("msgRate1"), 0));
        rtnMap.put("leftTrdTitle", makeStyleTrdMsg("0"));
        rtnMap.put("topTitle", makeStyleMsg(msgMap.get("msgkVah1"), 7));
        rtnMap.put("value", makeStyleValue((String)resultMap.get("MAXDMDKVAH1TIMERATE1")));
        rtnList.add(rtnMap);
        // ----------------------------------------------------------------

        // Max Demand and Time(Rate1) - Active Import
        rtnMap = new HashMap<String, Object>();
        rtnMap.put("leftFstTitle", makeStyleMsg(msgMap.get("msgMaxDemandTime"), 4));
        rtnMap.put("leftSndTitle", makeStyleMsg(msgMap.get("msgRate1"), 0));
        rtnMap.put("leftTrdTitle", makeStyleTrdMsg("1"));
        rtnMap.put("topTitle", makeStyleMsg(msgMap.get("msgActImp"), 1));
        rtnMap.put("value", makeStyleValue((String)resultMap.get("ACTDMDMXIMPRAT1")));
        rtnList.add(rtnMap);

        // Max Demand and Time(Rate1) - Active Export
        rtnMap = new HashMap<String, Object>();
        rtnMap.put("leftFstTitle", makeStyleMsg(msgMap.get("msgMaxDemandTime"), 4));
        rtnMap.put("leftSndTitle", makeStyleMsg(msgMap.get("msgRate1"), 0));
        rtnMap.put("leftTrdTitle", makeStyleTrdMsg("1"));
        rtnMap.put("topTitle", makeStyleMsg(msgMap.get("msgActExp"), 2));
        rtnMap.put("value", makeStyleValue((String)resultMap.get("ACTDMDMXEXPRAT1")));
        rtnList.add(rtnMap);

        // Max Demand and Time(Rate1) - Reactive Lag Import
        rtnMap = new HashMap<String, Object>();
        rtnMap.put("leftFstTitle", makeStyleMsg(msgMap.get("msgMaxDemandTime"), 4));
        rtnMap.put("leftSndTitle", makeStyleMsg(msgMap.get("msgRate1"), 0));
        rtnMap.put("leftTrdTitle", makeStyleTrdMsg("1"));
        rtnMap.put("topTitle", makeStyleMsg(msgMap.get("msgRactLagImp"), 3));
        rtnMap.put("value", makeStyleValue((String)resultMap.get("RACTDMDMXLAGIMPRAT1")));
        rtnList.add(rtnMap);

        // Max Demand and Time(Rate1) - Reactive Lead Import
        rtnMap = new HashMap<String, Object>();
        rtnMap.put("leftFstTitle", makeStyleMsg(msgMap.get("msgMaxDemandTime"), 4));
        rtnMap.put("leftSndTitle", makeStyleMsg(msgMap.get("msgRate1"), 0));
        rtnMap.put("leftTrdTitle", makeStyleTrdMsg("1"));
        rtnMap.put("topTitle", makeStyleMsg(msgMap.get("msgRactLeadImp"), 4));
        rtnMap.put("value", makeStyleValue((String)resultMap.get("RACTDMDMXLEADIMPRAT1")));
        rtnList.add(rtnMap);

        // Max Demand and Time(Rate1) - Reactive Lag Export
        rtnMap = new HashMap<String, Object>();
        rtnMap.put("leftFstTitle", makeStyleMsg(msgMap.get("msgMaxDemandTime"), 4));
        rtnMap.put("leftSndTitle", makeStyleMsg(msgMap.get("msgRate1"), 0));
        rtnMap.put("leftTrdTitle", makeStyleTrdMsg("1"));
        rtnMap.put("topTitle", makeStyleMsg(msgMap.get("msgRactLagExp"), 5));
        rtnMap.put("value", makeStyleValue((String)resultMap.get("RACTDMDMXLAGEXPRAT1")));
        rtnList.add(rtnMap);

        // Max Demand and Time(Rate1) - Reactive Lead Export
        rtnMap = new HashMap<String, Object>();
        rtnMap.put("leftFstTitle", makeStyleMsg(msgMap.get("msgMaxDemandTime"), 4));
        rtnMap.put("leftSndTitle", makeStyleMsg(msgMap.get("msgRate1"), 0));
        rtnMap.put("leftTrdTitle", makeStyleTrdMsg("1"));
        rtnMap.put("topTitle", makeStyleMsg(msgMap.get("msgRactLeadExp"), 6));
        rtnMap.put("value", makeStyleValue((String)resultMap.get("RACTDMDMXLEADEXPRAT1")));
        rtnList.add(rtnMap);
        
        // Max Demand and Time(Rate1) - kVAh1
        rtnMap = new HashMap<String, Object>();
        rtnMap.put("leftFstTitle", makeStyleMsg(msgMap.get("msgMaxDemandTime"), 4));
        rtnMap.put("leftSndTitle", makeStyleMsg(msgMap.get("msgRate1"), 0));
        rtnMap.put("leftTrdTitle", makeStyleTrdMsg("1"));
        rtnMap.put("topTitle", makeStyleMsg(msgMap.get("msgkVah1"), 7));
        rtnMap.put("value", makeStyleValue((String)resultMap.get("MAXDMDKVAH1RATE1")));
        rtnList.add(rtnMap);
        // ----------------------------------------------------------------

        // Max Demand and Time(Rate2-Time) - Active Import
        rtnMap = new HashMap<String, Object>();
        rtnMap.put("leftFstTitle", makeStyleMsg(msgMap.get("msgMaxDemandTime"), 4));
        rtnMap.put("leftSndTitle", makeStyleMsg(msgMap.get("msgRate2"), 0));
        rtnMap.put("leftTrdTitle", makeStyleTrdMsg("0"));
        rtnMap.put("topTitle", makeStyleMsg(msgMap.get("msgActImp"), 1));
        rtnMap.put("value", makeStyleValue((String)resultMap.get("ACTDMDMXTIMEIMPRAT2")));
        rtnList.add(rtnMap);

        // Max Demand and Time(Rate2-Time) - Active Export
        rtnMap = new HashMap<String, Object>();
        rtnMap.put("leftFstTitle", makeStyleMsg(msgMap.get("msgMaxDemandTime"), 4));
        rtnMap.put("leftSndTitle", makeStyleMsg(msgMap.get("msgRate2"), 0));
        rtnMap.put("leftTrdTitle", makeStyleTrdMsg("0"));
        rtnMap.put("topTitle", makeStyleMsg(msgMap.get("msgActExp"), 2));
        rtnMap.put("value", makeStyleValue((String)resultMap.get("ACTDMDMXTIMEEXPRAT2")));
        rtnList.add(rtnMap);

        // Max Demand and Time(Rate2-Time) - Reactive Lag Import
        rtnMap = new HashMap<String, Object>();
        rtnMap.put("leftFstTitle", makeStyleMsg(msgMap.get("msgMaxDemandTime"), 4));
        rtnMap.put("leftSndTitle", makeStyleMsg(msgMap.get("msgRate2"), 0));
        rtnMap.put("leftTrdTitle", makeStyleTrdMsg("0"));
        rtnMap.put("topTitle", makeStyleMsg(msgMap.get("msgRactLagImp"), 3));
        rtnMap.put("value", makeStyleValue((String)resultMap.get("RACTDMDMXTIMELAGIMPRAT2")));
        rtnList.add(rtnMap);

        // Max Demand and Time(Rate2-Time) - Reactive Lead Import
        rtnMap = new HashMap<String, Object>();
        rtnMap.put("leftFstTitle", makeStyleMsg(msgMap.get("msgMaxDemandTime"), 4));
        rtnMap.put("leftSndTitle", makeStyleMsg(msgMap.get("msgRate2"), 0));
        rtnMap.put("leftTrdTitle", makeStyleTrdMsg("0"));
        rtnMap.put("topTitle", makeStyleMsg(msgMap.get("msgRactLeadImp"), 4));
        rtnMap.put("value", makeStyleValue((String)resultMap.get("RACTDMDMXTIMELEADIMPRAT2")));
        rtnList.add(rtnMap);

        // Max Demand and Time(Rate2-Time) - Reactive Lag Export
        rtnMap = new HashMap<String, Object>();
        rtnMap.put("leftFstTitle", makeStyleMsg(msgMap.get("msgMaxDemandTime"), 4));
        rtnMap.put("leftSndTitle", makeStyleMsg(msgMap.get("msgRate2"), 0));
        rtnMap.put("leftTrdTitle", makeStyleTrdMsg("0"));
        rtnMap.put("topTitle", makeStyleMsg(msgMap.get("msgRactLagExp"), 5));
        rtnMap.put("value", makeStyleValue((String)resultMap.get("RACTDMDMXTIMELAGEXPRAT2")));
        rtnList.add(rtnMap);

        // Max Demand and Time(Rate2-Time) - Reactive Lead Export
        rtnMap = new HashMap<String, Object>();
        rtnMap.put("leftFstTitle", makeStyleMsg(msgMap.get("msgMaxDemandTime"), 4));
        rtnMap.put("leftSndTitle", makeStyleMsg(msgMap.get("msgRate2"), 0));
        rtnMap.put("leftTrdTitle", makeStyleTrdMsg("0"));
        rtnMap.put("topTitle", makeStyleMsg(msgMap.get("msgRactLeadExp"), 6));
        rtnMap.put("value", makeStyleValue((String)resultMap.get("RACTDMDMXTIMELEADEXPRAT2")));
        rtnList.add(rtnMap);
        
        // Max Demand and Time(Rate2-Time) - kVAh1
        rtnMap = new HashMap<String, Object>();
        rtnMap.put("leftFstTitle", makeStyleMsg(msgMap.get("msgMaxDemandTime"), 4));
        rtnMap.put("leftSndTitle", makeStyleMsg(msgMap.get("msgRate2"), 0));
        rtnMap.put("leftTrdTitle", makeStyleTrdMsg("0"));
        rtnMap.put("topTitle", makeStyleMsg(msgMap.get("msgkVah1"), 7));
        rtnMap.put("value", makeStyleValue((String)resultMap.get("MAXDMDKVAH1TIMERATE2")));
        rtnList.add(rtnMap);
        // ----------------------------------------------------------------

        // Max Demand and Time(Rate2) - Active Import
        rtnMap = new HashMap<String, Object>();
        rtnMap.put("leftFstTitle", makeStyleMsg(msgMap.get("msgMaxDemandTime"), 4));
        rtnMap.put("leftSndTitle", makeStyleMsg(msgMap.get("msgRate2"), 0));
        rtnMap.put("leftTrdTitle", makeStyleTrdMsg("1"));
        rtnMap.put("topTitle", makeStyleMsg(msgMap.get("msgActImp"), 1));
        rtnMap.put("value", makeStyleValue((String)resultMap.get("ACTDMDMXIMPRAT2")));
        rtnList.add(rtnMap);

        // Max Demand and Time(Rate2) - Active Export
        rtnMap = new HashMap<String, Object>();
        rtnMap.put("leftFstTitle", makeStyleMsg(msgMap.get("msgMaxDemandTime"), 4));
        rtnMap.put("leftSndTitle", makeStyleMsg(msgMap.get("msgRate2"), 0));
        rtnMap.put("leftTrdTitle", makeStyleTrdMsg("1"));
        rtnMap.put("topTitle", makeStyleMsg(msgMap.get("msgActExp"), 2));
        rtnMap.put("value", makeStyleValue((String)resultMap.get("ACTDMDMXEXPRAT2")));
        rtnList.add(rtnMap);

        // Max Demand and Time(Rate2) - Reactive Lag Import
        rtnMap = new HashMap<String, Object>();
        rtnMap.put("leftFstTitle", makeStyleMsg(msgMap.get("msgMaxDemandTime"), 4));
        rtnMap.put("leftSndTitle", makeStyleMsg(msgMap.get("msgRate2"), 0));
        rtnMap.put("leftTrdTitle", makeStyleTrdMsg("1"));
        rtnMap.put("topTitle", makeStyleMsg(msgMap.get("msgRactLagImp"), 3));
        rtnMap.put("value", makeStyleValue((String)resultMap.get("RACTDMDMXLAGIMPRAT2")));
        rtnList.add(rtnMap);

        // Max Demand and Time(Rate2) - Reactive Lead Import
        rtnMap = new HashMap<String, Object>();
        rtnMap.put("leftFstTitle", makeStyleMsg(msgMap.get("msgMaxDemandTime"), 4));
        rtnMap.put("leftSndTitle", makeStyleMsg(msgMap.get("msgRate2"), 0));
        rtnMap.put("leftTrdTitle", makeStyleTrdMsg("1"));
        rtnMap.put("topTitle", makeStyleMsg(msgMap.get("msgRactLeadImp"), 4));
        rtnMap.put("value", makeStyleValue((String)resultMap.get("RACTDMDMXLEADIMPRAT2")));
        rtnList.add(rtnMap);

        // Max Demand and Time(Rate2) - Reactive Lag Export
        rtnMap = new HashMap<String, Object>();
        rtnMap.put("leftFstTitle", makeStyleMsg(msgMap.get("msgMaxDemandTime"), 4));
        rtnMap.put("leftSndTitle", makeStyleMsg(msgMap.get("msgRate2"), 0));
        rtnMap.put("leftTrdTitle", makeStyleTrdMsg("1"));
        rtnMap.put("topTitle", makeStyleMsg(msgMap.get("msgRactLagExp"), 5));
        rtnMap.put("value", makeStyleValue((String)resultMap.get("RACTDMDMXLAGEXPRAT2")));
        rtnList.add(rtnMap);

        // Max Demand and Time(Rate2) - Reactive Lead Export
        rtnMap = new HashMap<String, Object>();
        rtnMap.put("leftFstTitle", makeStyleMsg(msgMap.get("msgMaxDemandTime"), 4));
        rtnMap.put("leftSndTitle", makeStyleMsg(msgMap.get("msgRate2"), 0));
        rtnMap.put("leftTrdTitle", makeStyleTrdMsg("1"));
        rtnMap.put("topTitle", makeStyleMsg(msgMap.get("msgRactLeadExp"), 6));
        rtnMap.put("value", makeStyleValue((String)resultMap.get("RACTDMDMXLEADEXPRAT2")));
        rtnList.add(rtnMap);
        
        // Max Demand and Time(Rate2) - kVAh1
        rtnMap = new HashMap<String, Object>();
        rtnMap.put("leftFstTitle", makeStyleMsg(msgMap.get("msgMaxDemandTime"), 4));
        rtnMap.put("leftSndTitle", makeStyleMsg(msgMap.get("msgRate2"), 0));
        rtnMap.put("leftTrdTitle", makeStyleTrdMsg("1"));
        rtnMap.put("topTitle", makeStyleMsg(msgMap.get("msgkVah1"), 7));
        rtnMap.put("value", makeStyleValue((String)resultMap.get("MAXDMDKVAH1RATE2")));
        rtnList.add(rtnMap);
        // ----------------------------------------------------------------

        // Max Demand and Time(Rate3-Time) - Active Import
        rtnMap = new HashMap<String, Object>();
        rtnMap.put("leftFstTitle", makeStyleMsg(msgMap.get("msgMaxDemandTime"), 4));
        rtnMap.put("leftSndTitle", makeStyleMsg(msgMap.get("msgRate3"), 0));
        rtnMap.put("leftTrdTitle", makeStyleTrdMsg("0"));
        rtnMap.put("topTitle", makeStyleMsg(msgMap.get("msgActImp"), 1));
        rtnMap.put("value", makeStyleValue((String)resultMap.get("ACTDMDMXTIMEIMPRAT3")));
        rtnList.add(rtnMap);

        // Max Demand and Time(Rate3-Time) - Active Export
        rtnMap = new HashMap<String, Object>();
        rtnMap.put("leftFstTitle", makeStyleMsg(msgMap.get("msgMaxDemandTime"), 4));
        rtnMap.put("leftSndTitle", makeStyleMsg(msgMap.get("msgRate3"), 0));
        rtnMap.put("leftTrdTitle", makeStyleTrdMsg("0"));
        rtnMap.put("topTitle", makeStyleMsg(msgMap.get("msgActExp"), 2));
        rtnMap.put("value", makeStyleValue((String)resultMap.get("ACTDMDMXTIMEEXPRAT3")));
        rtnList.add(rtnMap);

        // Max Demand and Time(Rate3-Time) - Reactive Lag Import
        rtnMap = new HashMap<String, Object>();
        rtnMap.put("leftFstTitle", makeStyleMsg(msgMap.get("msgMaxDemandTime"), 4));
        rtnMap.put("leftSndTitle", makeStyleMsg(msgMap.get("msgRate3"), 0));
        rtnMap.put("leftTrdTitle", makeStyleTrdMsg("0"));
        rtnMap.put("topTitle", makeStyleMsg(msgMap.get("msgRactLagImp"), 3));
        rtnMap.put("value", makeStyleValue((String)resultMap.get("RACTDMDMXTIMELAGIMPRAT3")));
        rtnList.add(rtnMap);

        // Max Demand and Time(Rate3-Time) - Reactive Lead Import
        rtnMap = new HashMap<String, Object>();
        rtnMap.put("leftFstTitle", makeStyleMsg(msgMap.get("msgMaxDemandTime"), 4));
        rtnMap.put("leftSndTitle", makeStyleMsg(msgMap.get("msgRate3"), 0));
        rtnMap.put("leftTrdTitle", makeStyleTrdMsg("0"));
        rtnMap.put("topTitle", makeStyleMsg(msgMap.get("msgRactLeadImp"), 4));
        rtnMap.put("value", makeStyleValue((String)resultMap.get("RACTDMDMXTIMELEADIMPRAT3")));
        rtnList.add(rtnMap);

        // Max Demand and Time(Rate3-Time) - Reactive Lag Export
        rtnMap = new HashMap<String, Object>();
        rtnMap.put("leftFstTitle", makeStyleMsg(msgMap.get("msgMaxDemandTime"), 4));
        rtnMap.put("leftSndTitle", makeStyleMsg(msgMap.get("msgRate3"), 0));
        rtnMap.put("leftTrdTitle", makeStyleTrdMsg("0"));
        rtnMap.put("topTitle", makeStyleMsg(msgMap.get("msgRactLagExp"), 5));
        rtnMap.put("value", makeStyleValue((String)resultMap.get("RACTDMDMXTIMELAGEXPRAT3")));
        rtnList.add(rtnMap);

        // Max Demand and Time(Rate3-Time) - Reactive Lead Export
        rtnMap = new HashMap<String, Object>();
        rtnMap.put("leftFstTitle", makeStyleMsg(msgMap.get("msgMaxDemandTime"), 4));
        rtnMap.put("leftSndTitle", makeStyleMsg(msgMap.get("msgRate3"), 0));
        rtnMap.put("leftTrdTitle", makeStyleTrdMsg("0"));
        rtnMap.put("topTitle", makeStyleMsg(msgMap.get("msgRactLeadExp"), 6));
        rtnMap.put("value", makeStyleValue((String)resultMap.get("RACTDMDMXTIMELEADEXPRAT3")));
        rtnList.add(rtnMap);
        
        // Max Demand and Time(Rate3-Time) - kVAh1
        rtnMap = new HashMap<String, Object>();
        rtnMap.put("leftFstTitle", makeStyleMsg(msgMap.get("msgMaxDemandTime"), 4));
        rtnMap.put("leftSndTitle", makeStyleMsg(msgMap.get("msgRate3"), 0));
        rtnMap.put("leftTrdTitle", makeStyleTrdMsg("0"));
        rtnMap.put("topTitle", makeStyleMsg(msgMap.get("msgkVah1"), 7));
        rtnMap.put("value", makeStyleValue((String)resultMap.get("MAXDMDKVAH1TIMERATE3")));
        rtnList.add(rtnMap);
        // ----------------------------------------------------------------

        // Max Demand and Time(Rate3) - Active Import
        rtnMap = new HashMap<String, Object>();
        rtnMap.put("leftFstTitle", makeStyleMsg(msgMap.get("msgMaxDemandTime"), 4));
        rtnMap.put("leftSndTitle", makeStyleMsg(msgMap.get("msgRate3"), 0));
        rtnMap.put("leftTrdTitle", makeStyleTrdMsg("1"));
        rtnMap.put("topTitle", makeStyleMsg(msgMap.get("msgActImp"), 1));
        rtnMap.put("value", makeStyleValue((String)resultMap.get("ACTDMDMXIMPRAT3")));
        rtnList.add(rtnMap);

        // Max Demand and Time(Rate3) - Active Export
        rtnMap = new HashMap<String, Object>();
        rtnMap.put("leftFstTitle", makeStyleMsg(msgMap.get("msgMaxDemandTime"), 4));
        rtnMap.put("leftSndTitle", makeStyleMsg(msgMap.get("msgRate3"), 0));
        rtnMap.put("leftTrdTitle", makeStyleTrdMsg("1"));
        rtnMap.put("topTitle", makeStyleMsg(msgMap.get("msgActExp"), 2));
        rtnMap.put("value", makeStyleValue((String)resultMap.get("ACTDMDMXEXPRAT3")));
        rtnList.add(rtnMap);

        // Max Demand and Time(Rate3) - Reactive Lag Import
        rtnMap = new HashMap<String, Object>();
        rtnMap.put("leftFstTitle", makeStyleMsg(msgMap.get("msgMaxDemandTime"), 4));
        rtnMap.put("leftSndTitle", makeStyleMsg(msgMap.get("msgRate3"), 0));
        rtnMap.put("leftTrdTitle", makeStyleTrdMsg("1"));
        rtnMap.put("topTitle", makeStyleMsg(msgMap.get("msgRactLagImp"), 3));
        rtnMap.put("value", makeStyleValue((String)resultMap.get("RACTDMDMXLAGIMPRAT3")));
        rtnList.add(rtnMap);

        // Max Demand and Time(Rate3) - Reactive Lead Import
        rtnMap = new HashMap<String, Object>();
        rtnMap.put("leftFstTitle", makeStyleMsg(msgMap.get("msgMaxDemandTime"), 4));
        rtnMap.put("leftSndTitle", makeStyleMsg(msgMap.get("msgRate3"), 0));
        rtnMap.put("leftTrdTitle", makeStyleTrdMsg("1"));
        rtnMap.put("topTitle", makeStyleMsg(msgMap.get("msgRactLeadImp"), 4));
        rtnMap.put("value", makeStyleValue((String)resultMap.get("RACTDMDMXLEADIMPRAT3")));
        rtnList.add(rtnMap);

        // Max Demand and Time(Rate3) - Reactive Lag Export
        rtnMap = new HashMap<String, Object>();
        rtnMap.put("leftFstTitle", makeStyleMsg(msgMap.get("msgMaxDemandTime"), 4));
        rtnMap.put("leftSndTitle", makeStyleMsg(msgMap.get("msgRate3"), 0));
        rtnMap.put("leftTrdTitle", makeStyleTrdMsg("1"));
        rtnMap.put("topTitle", makeStyleMsg(msgMap.get("msgRactLagExp"), 5));
        rtnMap.put("value", makeStyleValue((String)resultMap.get("RACTDMDMXLAGEXPRAT3")));
        rtnList.add(rtnMap);

        // Max Demand and Time(Rate3) - Reactive Lead Export
        rtnMap = new HashMap<String, Object>();
        rtnMap.put("leftFstTitle", makeStyleMsg(msgMap.get("msgMaxDemandTime"), 4));
        rtnMap.put("leftSndTitle", makeStyleMsg(msgMap.get("msgRate3"), 0));
        rtnMap.put("leftTrdTitle", makeStyleTrdMsg("1"));
        rtnMap.put("topTitle", makeStyleMsg(msgMap.get("msgRactLeadExp"), 6));
        rtnMap.put("value", makeStyleValue((String)resultMap.get("RACTDMDMXLEADEXPRAT3")));
        rtnList.add(rtnMap);
        
        // Max Demand and Time(Rate3) - kVAh1
        rtnMap = new HashMap<String, Object>();
        rtnMap.put("leftFstTitle", makeStyleMsg(msgMap.get("msgMaxDemandTime"), 4));
        rtnMap.put("leftSndTitle", makeStyleMsg(msgMap.get("msgRate3"), 0));
        rtnMap.put("leftTrdTitle", makeStyleTrdMsg("1"));
        rtnMap.put("topTitle", makeStyleMsg(msgMap.get("msgkVah1"), 7));
        rtnMap.put("value", makeStyleValue((String)resultMap.get("MAXDMDKVAH1RATE3")));
        rtnList.add(rtnMap);
        // ----------------------------------------------------------------

        // Total Cumulative Demand - Active Import
        rtnMap = new HashMap<String, Object>();
        rtnMap.put("leftFstTitle", makeStyleMsg(msgMap.get("msgTotCummDemand"), 5));
        rtnMap.put("leftSndTitle", "");
        rtnMap.put("leftTrdTitle", "");
        rtnMap.put("topTitle", makeStyleMsg(msgMap.get("msgActImp"), 1));
        rtnMap.put("value", makeStyleValue((String)resultMap.get("CUMACTDMDMXIMPTOT")));
        rtnList.add(rtnMap);

        // Total Cumulative Demand - Active Export
        rtnMap = new HashMap<String, Object>();
        rtnMap.put("leftFstTitle", makeStyleMsg(msgMap.get("msgTotCummDemand"), 5));
        rtnMap.put("leftSndTitle", "");
        rtnMap.put("leftTrdTitle", "");
        rtnMap.put("topTitle", makeStyleMsg(msgMap.get("msgActExp"), 2));
        rtnMap.put("value", makeStyleValue((String)resultMap.get("CUMACTDMDMXEXPTOT")));
        rtnList.add(rtnMap);

        // Total Cumulative Demand - Reactive Lag Import
        rtnMap = new HashMap<String, Object>();
        rtnMap.put("leftFstTitle", makeStyleMsg(msgMap.get("msgTotCummDemand"), 5));
        rtnMap.put("leftSndTitle", "");
        rtnMap.put("leftTrdTitle", "");
        rtnMap.put("topTitle", makeStyleMsg(msgMap.get("msgRactLagImp"), 3));
        rtnMap.put("value", makeStyleValue((String)resultMap.get("CUMRACTDMDMXLAGIMPTOT")));
        rtnList.add(rtnMap);

        // Total Cumulative Demand - Reactive Lead Import
        rtnMap = new HashMap<String, Object>();
        rtnMap.put("leftFstTitle", makeStyleMsg(msgMap.get("msgTotCummDemand"), 5));
        rtnMap.put("leftSndTitle", "");
        rtnMap.put("leftTrdTitle", "");
        rtnMap.put("topTitle", makeStyleMsg(msgMap.get("msgRactLeadImp"), 4));
        rtnMap.put("value", makeStyleValue((String)resultMap.get("CUMRACTDMDMXLEADIMPTOT")));
        rtnList.add(rtnMap);

        // Total Cumulative Demand - Reactive Lag Export
        rtnMap = new HashMap<String, Object>();
        rtnMap.put("leftFstTitle", makeStyleMsg(msgMap.get("msgTotCummDemand"), 5));
        rtnMap.put("leftSndTitle", "");
        rtnMap.put("leftTrdTitle", "");
        rtnMap.put("topTitle", makeStyleMsg(msgMap.get("msgRactLagExp"), 5));
        rtnMap.put("value", makeStyleValue((String)resultMap.get("CUMRACTDMDMXLAGEXPTOT")));
        rtnList.add(rtnMap);

        // Total Cumulative Demand - Reactive Lead Export
        rtnMap = new HashMap<String, Object>();
        rtnMap.put("leftFstTitle", makeStyleMsg(msgMap.get("msgTotCummDemand"), 5));
        rtnMap.put("leftSndTitle", "");
        rtnMap.put("leftTrdTitle", "");
        rtnMap.put("topTitle", makeStyleMsg(msgMap.get("msgRactLeadExp"), 6));
        rtnMap.put("value", makeStyleValue((String)resultMap.get("CUMRACTDMDMXLEADEXPTOT")));
        rtnList.add(rtnMap);
        
        // Total Cumulative Demand - kVAh1
        rtnMap = new HashMap<String, Object>();
        rtnMap.put("leftFstTitle", makeStyleMsg(msgMap.get("msgTotCummDemand"), 5));
        rtnMap.put("leftSndTitle", "");
        rtnMap.put("leftTrdTitle", "");
        rtnMap.put("topTitle", makeStyleMsg(msgMap.get("msgkVah1"), 7));
        rtnMap.put("value", makeStyleValue((String)resultMap.get("CUMMKVAH1RATETOTAL")));
        rtnList.add(rtnMap);
        // ----------------------------------------------------------------

        // Cumulative Demand(Rate1) - Active Import
        rtnMap = new HashMap<String, Object>();
        rtnMap.put("leftFstTitle", makeStyleMsg(msgMap.get("msgCummDemand"), 6));
        rtnMap.put("leftSndTitle", makeStyleMsg(msgMap.get("msgRate1"), 0));
        rtnMap.put("leftTrdTitle", "");
        rtnMap.put("topTitle", makeStyleMsg(msgMap.get("msgActImp"), 1));
        rtnMap.put("value", makeStyleValue((String)resultMap.get("CUMACTDMDMXIMPRAT1")));
        rtnList.add(rtnMap);

        // Cumulative Demand(Rate1) - Active Export
        rtnMap = new HashMap<String, Object>();
        rtnMap.put("leftFstTitle", makeStyleMsg(msgMap.get("msgCummDemand"), 6));
        rtnMap.put("leftSndTitle", makeStyleMsg(msgMap.get("msgRate1"), 0));
        rtnMap.put("leftTrdTitle", "");
        rtnMap.put("topTitle", makeStyleMsg(msgMap.get("msgActExp"), 2));
        rtnMap.put("value", makeStyleValue((String)resultMap.get("CUMACTDMDMXEXPRAT1")));
        rtnList.add(rtnMap);

        // Cumulative Demand(Rate1) - Reactive Lag Import
        rtnMap = new HashMap<String, Object>();
        rtnMap.put("leftFstTitle", makeStyleMsg(msgMap.get("msgCummDemand"), 6));
        rtnMap.put("leftSndTitle", makeStyleMsg(msgMap.get("msgRate1"), 0));
        rtnMap.put("leftTrdTitle", "");
        rtnMap.put("topTitle", makeStyleMsg(msgMap.get("msgRactLagImp"), 3));
        rtnMap.put("value", makeStyleValue((String)resultMap.get("CUMRACTDMDMXLAGIMPRAT1")));
        rtnList.add(rtnMap);

        // Cumulative Demand(Rate1) - Reactive Lead Import
        rtnMap = new HashMap<String, Object>();
        rtnMap.put("leftFstTitle", makeStyleMsg(msgMap.get("msgCummDemand"), 6));
        rtnMap.put("leftSndTitle", makeStyleMsg(msgMap.get("msgRate1"), 0));
        rtnMap.put("leftTrdTitle", "");
        rtnMap.put("topTitle", makeStyleMsg(msgMap.get("msgRactLeadImp"), 4));
        rtnMap.put("value", makeStyleValue((String)resultMap.get("CUMRACTDMDMXLEADIMPRAT1")));
        rtnList.add(rtnMap);

        // Cumulative Demand(Rate1) - Reactive Lag Export
        rtnMap = new HashMap<String, Object>();
        rtnMap.put("leftFstTitle", makeStyleMsg(msgMap.get("msgCummDemand"), 6));
        rtnMap.put("leftSndTitle", makeStyleMsg(msgMap.get("msgRate1"), 0));
        rtnMap.put("leftTrdTitle", "");
        rtnMap.put("topTitle", makeStyleMsg(msgMap.get("msgRactLagExp"), 5));
        rtnMap.put("value", makeStyleValue((String)resultMap.get("CUMRACTDMDMXLAGEXPRAT1")));
        rtnList.add(rtnMap);

        // Cumulative Demand(Rate1) - Reactive Lead Export
        rtnMap = new HashMap<String, Object>();
        rtnMap.put("leftFstTitle", makeStyleMsg(msgMap.get("msgCummDemand"), 6));
        rtnMap.put("leftSndTitle", makeStyleMsg(msgMap.get("msgRate1"), 0));
        rtnMap.put("leftTrdTitle", "");
        rtnMap.put("topTitle", makeStyleMsg(msgMap.get("msgRactLeadExp"), 6));
        rtnMap.put("value", makeStyleValue((String)resultMap.get("CUMRACTDMDMXLEADEXPRAT1")));
        rtnList.add(rtnMap);
        
        // Cumulative Demand(Rate1) - kVAh1
        rtnMap = new HashMap<String, Object>();
        rtnMap.put("leftFstTitle", makeStyleMsg(msgMap.get("msgCummDemand"), 6));
        rtnMap.put("leftSndTitle", makeStyleMsg(msgMap.get("msgRate1"), 0));
        rtnMap.put("leftTrdTitle", "");
        rtnMap.put("topTitle", makeStyleMsg(msgMap.get("msgkVah1"), 7));
        rtnMap.put("value", makeStyleValue((String)resultMap.get("CUMMKVAH1RATE1")));
        rtnList.add(rtnMap);
        // ----------------------------------------------------------------

        // Cumulative Demand(Rate2) - Active Import
        rtnMap = new HashMap<String, Object>();
        rtnMap.put("leftFstTitle", makeStyleMsg(msgMap.get("msgCummDemand"), 6));
        rtnMap.put("leftSndTitle", makeStyleMsg(msgMap.get("msgRate2"), 0));
        rtnMap.put("leftTrdTitle", "");
        rtnMap.put("topTitle", makeStyleMsg(msgMap.get("msgActImp"), 1));
        rtnMap.put("value", makeStyleValue((String)resultMap.get("CUMACTDMDMXIMPRAT2")));
        rtnList.add(rtnMap);

        // Cumulative Demand(Rate2) - Active Export
        rtnMap = new HashMap<String, Object>();
        rtnMap.put("leftFstTitle", makeStyleMsg(msgMap.get("msgCummDemand"), 6));
        rtnMap.put("leftSndTitle", makeStyleMsg(msgMap.get("msgRate2"), 0));
        rtnMap.put("leftTrdTitle", "");
        rtnMap.put("topTitle", makeStyleMsg(msgMap.get("msgActExp"), 2));
        rtnMap.put("value", makeStyleValue((String)resultMap.get("CUMACTDMDMXEXPRAT2")));
        rtnList.add(rtnMap);

        // Cumulative Demand(Rate2) - Reactive Lag Import
        rtnMap = new HashMap<String, Object>();
        rtnMap.put("leftFstTitle", makeStyleMsg(msgMap.get("msgCummDemand"), 6));
        rtnMap.put("leftSndTitle", makeStyleMsg(msgMap.get("msgRate2"), 0));
        rtnMap.put("leftTrdTitle", "");
        rtnMap.put("topTitle", makeStyleMsg(msgMap.get("msgRactLagImp"), 3));
        rtnMap.put("value", makeStyleValue((String)resultMap.get("CUMRACTDMDMXLAGIMPRAT2")));
        rtnList.add(rtnMap);

        // Cumulative Demand(Rate2) - Reactive Lead Import
        rtnMap = new HashMap<String, Object>();
        rtnMap.put("leftFstTitle", makeStyleMsg(msgMap.get("msgCummDemand"), 6));
        rtnMap.put("leftSndTitle", makeStyleMsg(msgMap.get("msgRate2"), 0));
        rtnMap.put("leftTrdTitle", "");
        rtnMap.put("topTitle", makeStyleMsg(msgMap.get("msgRactLeadImp"), 4));
        rtnMap.put("value", makeStyleValue((String)resultMap.get("CUMRACTDMDMXLEADIMPRAT2")));
        rtnList.add(rtnMap);

        // Cumulative Demand(Rate2) - Reactive Lag Export
        rtnMap = new HashMap<String, Object>();
        rtnMap.put("leftFstTitle", makeStyleMsg(msgMap.get("msgCummDemand"), 6));
        rtnMap.put("leftSndTitle", makeStyleMsg(msgMap.get("msgRate2"), 0));
        rtnMap.put("leftTrdTitle", "");
        rtnMap.put("topTitle", makeStyleMsg(msgMap.get("msgRactLagExp"), 5));
        rtnMap.put("value", makeStyleValue((String)resultMap.get("CUMRACTDMDMXLAGEXPRAT2")));
        rtnList.add(rtnMap);

        // Cumulative Demand(Rate2) - Reactive Lead Export
        rtnMap = new HashMap<String, Object>();
        rtnMap.put("leftFstTitle", makeStyleMsg(msgMap.get("msgCummDemand"), 6));
        rtnMap.put("leftSndTitle", makeStyleMsg(msgMap.get("msgRate2"), 0));
        rtnMap.put("leftTrdTitle", "");
        rtnMap.put("topTitle", makeStyleMsg(msgMap.get("msgRactLeadExp"), 6));
        rtnMap.put("value", makeStyleValue((String)resultMap.get("CUMRACTDMDMXLEADEXPRAT2")));
        rtnList.add(rtnMap);
        
        // Cumulative Demand(Rate2) - kVAh1
        rtnMap = new HashMap<String, Object>();
        rtnMap.put("leftFstTitle", makeStyleMsg(msgMap.get("msgCummDemand"), 6));
        rtnMap.put("leftSndTitle", makeStyleMsg(msgMap.get("msgRate2"), 0));
        rtnMap.put("leftTrdTitle", "");
        rtnMap.put("topTitle", makeStyleMsg(msgMap.get("msgkVah1"), 7));
        rtnMap.put("value", makeStyleValue((String)resultMap.get("CUMMKVAH1RATE2")));
        rtnList.add(rtnMap);
        // ----------------------------------------------------------------

        // Cumulative Demand(Rate3) - Active Import
        rtnMap = new HashMap<String, Object>();
        rtnMap.put("leftFstTitle", makeStyleMsg(msgMap.get("msgCummDemand"), 6));
        rtnMap.put("leftSndTitle", makeStyleMsg(msgMap.get("msgRate3"), 0));
        rtnMap.put("leftTrdTitle", "");
        rtnMap.put("topTitle", makeStyleMsg(msgMap.get("msgActImp"), 1));
        rtnMap.put("value", makeStyleValue((String)resultMap.get("CUMACTDMDMXIMPRAT3")));
        rtnList.add(rtnMap);

        // Cumulative Demand(Rate3) - Active Export
        rtnMap = new HashMap<String, Object>();
        rtnMap.put("leftFstTitle", makeStyleMsg(msgMap.get("msgCummDemand"), 6));
        rtnMap.put("leftSndTitle", makeStyleMsg(msgMap.get("msgRate3"), 0));
        rtnMap.put("leftTrdTitle", "");
        rtnMap.put("topTitle", makeStyleMsg(msgMap.get("msgActExp"), 2));
        rtnMap.put("value", makeStyleValue((String)resultMap.get("CUMACTDMDMXEXPRAT3")));
        rtnList.add(rtnMap);

        // Cumulative Demand(Rate3) - Reactive Lag Import
        rtnMap = new HashMap<String, Object>();
        rtnMap.put("leftFstTitle", makeStyleMsg(msgMap.get("msgCummDemand"), 6));
        rtnMap.put("leftSndTitle", makeStyleMsg(msgMap.get("msgRate3"), 0));
        rtnMap.put("leftTrdTitle", "");
        rtnMap.put("topTitle", makeStyleMsg(msgMap.get("msgRactLagImp"), 3));
        rtnMap.put("value", makeStyleValue((String)resultMap.get("CUMRACTDMDMXLAGIMPRAT3")));
        rtnList.add(rtnMap);

        // Cumulative Demand(Rate3) - Reactive Lead Import
        rtnMap = new HashMap<String, Object>();
        rtnMap.put("leftFstTitle", makeStyleMsg(msgMap.get("msgCummDemand"), 6));
        rtnMap.put("leftSndTitle", makeStyleMsg(msgMap.get("msgRate3"), 0));
        rtnMap.put("leftTrdTitle", "");
        rtnMap.put("topTitle", makeStyleMsg(msgMap.get("msgRactLeadImp"), 4));
        rtnMap.put("value", makeStyleValue((String)resultMap.get("CUMRACTDMDMXLEADIMPRAT3")));
        rtnList.add(rtnMap);

        // Cumulative Demand(Rate3) - Reactive Lag Export
        rtnMap = new HashMap<String, Object>();
        rtnMap.put("leftFstTitle", makeStyleMsg(msgMap.get("msgCummDemand"), 6));
        rtnMap.put("leftSndTitle", makeStyleMsg(msgMap.get("msgRate3"), 0));
        rtnMap.put("leftTrdTitle", "");
        rtnMap.put("topTitle", makeStyleMsg(msgMap.get("msgRactLagExp"), 5));
        rtnMap.put("value", makeStyleValue((String)resultMap.get("CUMRACTDMDMXLAGEXPRAT3")));
        rtnList.add(rtnMap);

        // Cumulative Demand(Rate3) - Reactive Lead Export
        rtnMap = new HashMap<String, Object>();
        rtnMap.put("leftFstTitle", makeStyleMsg(msgMap.get("msgCummDemand"), 6));
        rtnMap.put("leftSndTitle", makeStyleMsg(msgMap.get("msgRate3"), 0));
        rtnMap.put("leftTrdTitle", "");
        rtnMap.put("topTitle", makeStyleMsg(msgMap.get("msgRactLeadExp"), 6));
        rtnMap.put("value", makeStyleValue((String)resultMap.get("CUMRACTDMDMXLEADEXPRAT3")));
        rtnList.add(rtnMap);

        // Cumulative Demand(Rate3) - kVAh1
        rtnMap = new HashMap<String, Object>();
        rtnMap.put("leftFstTitle", makeStyleMsg(msgMap.get("msgCummDemand"), 6));
        rtnMap.put("leftSndTitle", makeStyleMsg(msgMap.get("msgRate3"), 0));
        rtnMap.put("leftTrdTitle", "");
        rtnMap.put("topTitle", makeStyleMsg(msgMap.get("msgkVah1"), 7));
        rtnMap.put("value", makeStyleValue((String)resultMap.get("CUMMKVAH1RATE3")));
        rtnList.add(rtnMap);
        // ----------------------------------------------------------------

        return rtnList;
    }

    /**
     * method name : getMeteringDetailLastData<b/>
     * method Desc : TOU Report 의 지난일자 상세 사용량정보를 조회한다.
     *
     * @param conditionMap
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> getMeteringDetailLastUsageData(Map<String, Object> conditionMap) {
        List<Map<String, Object>> result = null;
        List<Map<String, Object>> rtnList = new ArrayList<Map<String, Object>>();
        Map<String, String> msgMap = (Map<String, String>)conditionMap.get("msgMap");
        Map<String, Object> rtnMap = new HashMap<String, Object>();

        String reportType = StringUtil.nullToBlank(conditionMap.get("reportType"));
        String searchDate = null;
        String lastSearchDate = null;
        String lastData = StringUtil.nullToBlank(conditionMap.get("lastData"));

        Supplier supplier = supplierDao.get(Integer.parseInt((String)conditionMap.get("supplierId")));
        String lang = supplier.getLang().getCode_2letter();
        String country = supplier.getCountry().getCode_2letter();
        
        DecimalFormat df = DecimalUtil.getDecimalFormat(supplier.getMd());
        DecimalFormat dfc = DecimalUtil.getDecimalFormat(supplier.getCd());

        conditionMap.put("locationCondition", getLeafLocationId((String)conditionMap.get("locationId"), (String)conditionMap.get("supplierId")));

        if(reportType.equals("daily")) {  // 일간
            result = billingDayEMDao.getBillingDataReportDaily(conditionMap);
            searchDate = (String)conditionMap.get("searchStartDate");
            lastSearchDate = (String)conditionMap.get("lastStartDate");
        } else if(reportType.equals("monthly")) { // 월간
            if (!lastData.isEmpty()) {
                // 전월 데이터 포함
                result = billingMonthEMDao.getBillingDataReportMonthlyWithLastMonth(conditionMap);
            } else {
                // 전월 데이터 제외
                result = billingMonthEMDao.getBillingDataReportMonthly(conditionMap);
            }
            searchDate = ((String)conditionMap.get("searchStartDate")).substring(0, 6);
            lastSearchDate = ((String)conditionMap.get("lastStartDate")).substring(0, 6);
        } else if(reportType.equals("current")) {  // Current
            result = realtimebillingemDao.getBillingDataReportCurrent(conditionMap);
            searchDate = (String)conditionMap.get("searchStartDate");
            lastSearchDate = (String)conditionMap.get("lastStartDate");
        }

        // formatting 할 항목 key 패턴 정의
        String regTime   = ".*ACT.*TIME.*";
        String regImpTot = ".*ACT.*IMP.*TOT.*";
        String regImpRat = ".*ACT.*IMP.*RAT.*";
        String regExpTot = ".*ACT.*EXP.*TOT.*";
        String regExpRat = ".*ACT.*EXP.*RAT.*";
        String cumKvahRat = ".*CUM.*KVAH.*RAT.*";
        String maxKvahRat = ".*MAX.*KVAH1RAT.*";
        String maxKvahTime = ".*MAX.*KVAH1TIME.*";
        String Kvah = ".*KVAH.*";

        Pattern pTime   = Pattern.compile(regTime);
        Pattern pImpTot = Pattern.compile(regImpTot);
        Pattern pImpRat = Pattern.compile(regImpRat);
        Pattern pExpTot = Pattern.compile(regExpTot);
        Pattern pExpRat = Pattern.compile(regExpRat);
        Pattern pCumKvahRat = Pattern.compile(cumKvahRat);
        Pattern pMaxKvahRat = Pattern.compile(maxKvahRat);
        Pattern pMaxKvahTime = Pattern.compile(maxKvahTime);
        Pattern pKvah = Pattern.compile(Kvah);
        
        Matcher mTime   = null;
        Matcher mImpTot = null;
        Matcher mImpRat = null;
        Matcher mExpTot = null;
        Matcher mExpRat = null;
        Matcher mCumKvahRat = null;
        Matcher mMaxKvahRat = null;
        Matcher mMaxKvahTime = null;
        Matcher mKvah = null;
        String key      = null;
        int rownum = 0;

        if (result.size() <= 0) {
            return result;
        }

        // 일자, 숫자 formatting
        Map<String, Object> resultMap = (Map<String, Object>)result.get(0);
        Set<String> set = resultMap.keySet();
        Iterator<String> itr = set.iterator();

        while (itr.hasNext()) {
            key = itr.next();

            mTime   = pTime.matcher(key);
            mImpTot = pImpTot.matcher(key);
            mImpRat = pImpRat.matcher(key);
            mExpTot = pExpTot.matcher(key);
            mExpRat = pExpRat.matcher(key);
            mCumKvahRat 	= pCumKvahRat.matcher(key);
            mMaxKvahRat 	= pMaxKvahRat.matcher(key);
            mMaxKvahTime 	= pMaxKvahTime.matcher(key);
            mKvah 			= pKvah.matcher(key);

            if(mTime.find()) {
                resultMap.put(key, TimeLocaleUtil.getLocaleDate((String)resultMap.get(key), lang, country));
            } else if(mMaxKvahTime.find()) {
                resultMap.put(key, TimeLocaleUtil.getLocaleDate((String)resultMap.get(key), lang, country));
            } else if(mImpTot.find()) {
                resultMap.put(key, df.format((StringUtil.nullToDoubleZero(
                		Double.parseDouble(resultMap.get(key) ==null ? "0" : resultMap.get(key).toString())))));
            } else if(mImpRat.find()) {
                resultMap.put(key, df.format((StringUtil.nullToDoubleZero(
                		Double.parseDouble(resultMap.get(key) ==null ? "0" : resultMap.get(key).toString())))));
            } else if(mExpTot.find()) {
                resultMap.put(key, df.format((StringUtil.nullToDoubleZero(
                		Double.parseDouble(resultMap.get(key) ==null ? "0" : resultMap.get(key).toString())))));
            } else if(mExpRat.find()) {
                resultMap.put(key, df.format((StringUtil.nullToDoubleZero(
                		Double.parseDouble(resultMap.get(key) ==null ? "0" : resultMap.get(key).toString())))));
            }else if(mCumKvahRat.find()) {
                resultMap.put(key, df.format((StringUtil.nullToDoubleZero(
                		Double.parseDouble(resultMap.get(key) ==null ? "0" : resultMap.get(key).toString())))));
            }else if(mMaxKvahRat.find()) {
                resultMap.put(key, df.format((StringUtil.nullToDoubleZero(
                		Double.parseDouble(resultMap.get(key) ==null ? "0" : resultMap.get(key).toString())))));
            }else if(mKvah.find()) {
                resultMap.put(key, df.format((StringUtil.nullToDoubleZero(
                		Double.parseDouble(resultMap.get(key) ==null ? "0" : resultMap.get(key).toString())))));
            }
        }

        if(reportType.equals("daily") || reportType.equals("current")) {  // 일간 / Current
            resultMap.put("searchDate", TimeLocaleUtil.getLocaleDate(searchDate, lang, country));
            resultMap.put("lastSearchDate", TimeLocaleUtil.getLocaleDate(lastSearchDate, lang, country));

            if (!StringUtil.nullToBlank(resultMap.get("HHMMSS")).isEmpty()) {
                resultMap.put("YYYYMMDDHHMMSS", TimeLocaleUtil.getLocaleDate((String)resultMap.get("YYYYMMDD") + (String)resultMap.get("HHMMSS"), lang, country));

            } else {
                resultMap.put("YYYYMMDDHHMMSS", TimeLocaleUtil.getLocaleDate((String)resultMap.get("YYYYMMDD"), lang, country));
            }
        } else { // 월간
            resultMap.put("searchDate", TimeLocaleUtil.getLocaleYearMonth(searchDate, lang, country));
            resultMap.put("lastSearchDate", TimeLocaleUtil.getLocaleYearMonth(lastSearchDate, lang, country));

            if (lastData.isEmpty()) {

                if (!StringUtil.nullToBlank(resultMap.get("HHMMSS")).isEmpty()) {
                    resultMap.put("YYYYMMDDHHMMSS", TimeLocaleUtil.getLocaleDate((String)resultMap.get("YYYYMMDD") + (String)resultMap.get("HHMMSS"), lang, country));
                } else {
                    resultMap.put("YYYYMMDDHHMMSS", TimeLocaleUtil.getLocaleDate((String)resultMap.get("YYYYMMDD"), lang, country));
                }
            } else {
                resultMap.put("YYYYMMDDHHMMSS", TimeLocaleUtil.getLocaleYearMonth((String)resultMap.get("YYYYMMDD"), lang, country));
            }
        }
        resultMap.put("CONTRACTDEMAND", dfc.format((StringUtil.nullToDoubleZero((Double)resultMap.get("CONTRACTDEMAND"))).doubleValue())+"(kW)");
        resultMap.put("rowNum", Integer.toString(++rownum));

        // pivotgrid data 생성
        ///////
        rtnMap = new HashMap<String, Object>();
        rtnMap.put("leftFstTitle", makeStyleMsg(msgMap.get("msgTotEnergy"), 1));
        rtnMap.put("leftSndTitle", "");
        rtnMap.put("leftTrdTitle", "");
        rtnMap.put("topTitle", makeStyleMsg(msgMap.get("msgActImp"), 8));
        rtnMap.put("value", makeStyleValue((String)resultMap.get("LSTACTENGYIMPTOT")));
        rtnList.add(rtnMap);

        ///////
        rtnMap = new HashMap<String, Object>();
        rtnMap.put("leftFstTitle", makeStyleMsg(msgMap.get("msgTotEnergy"), 1));
        rtnMap.put("leftSndTitle", "");
        rtnMap.put("leftTrdTitle", "");
        rtnMap.put("topTitle", makeStyleMsg(msgMap.get("msgActExp"), 9));
        rtnMap.put("value", makeStyleValue((String)resultMap.get("LSTACTENGYEXPTOT")));
        rtnList.add(rtnMap);

        ///////
        rtnMap = new HashMap<String, Object>();
        rtnMap.put("leftFstTitle", makeStyleMsg(msgMap.get("msgTotEnergy"), 1));
        rtnMap.put("leftSndTitle", "");
        rtnMap.put("leftTrdTitle", "");
        rtnMap.put("topTitle", makeStyleMsg(msgMap.get("msgRactLagImp"), 10));
        rtnMap.put("value", makeStyleValue((String)resultMap.get("LSTRACTENGYLAGIMPTOT")));
        rtnList.add(rtnMap);

        ///////
        rtnMap = new HashMap<String, Object>();
        rtnMap.put("leftFstTitle", makeStyleMsg(msgMap.get("msgTotEnergy"), 1));
        rtnMap.put("leftSndTitle", "");
        rtnMap.put("leftTrdTitle", "");
        rtnMap.put("topTitle", makeStyleMsg(msgMap.get("msgRactLeadImp"), 11));
        rtnMap.put("value", makeStyleValue((String)resultMap.get("LSTRACTENGYLEADIMPTOT")));
        rtnList.add(rtnMap);

        ///////
        rtnMap = new HashMap<String, Object>();
        rtnMap.put("leftFstTitle", makeStyleMsg(msgMap.get("msgTotEnergy"), 1));
        rtnMap.put("leftSndTitle", "");
        rtnMap.put("leftTrdTitle", "");
        rtnMap.put("topTitle", makeStyleMsg(msgMap.get("msgRactLagExp"), 12));
        rtnMap.put("value", makeStyleValue((String)resultMap.get("LSTRACTENGYLAGEXPTOT")));
        rtnList.add(rtnMap);

        ///////
        rtnMap = new HashMap<String, Object>();
        rtnMap.put("leftFstTitle", makeStyleMsg(msgMap.get("msgTotEnergy"), 1));
        rtnMap.put("leftSndTitle", "");
        rtnMap.put("leftTrdTitle", "");
        rtnMap.put("topTitle", makeStyleMsg(msgMap.get("msgRactLeadExp"), 13));
        rtnMap.put("value", makeStyleValue((String)resultMap.get("LSTRACTENGYLEADEXPTOT")));
        rtnList.add(rtnMap);
        
        ///////
        rtnMap = new HashMap<String, Object>();
        rtnMap.put("leftFstTitle", makeStyleMsg(msgMap.get("msgTotEnergy"), 1));
        rtnMap.put("leftSndTitle", "");
        rtnMap.put("leftTrdTitle", "");
        rtnMap.put("topTitle", makeStyleMsg(msgMap.get("msgkVah1"), 14));
        rtnMap.put("value", makeStyleValue((String)resultMap.get("LSTKVAH")));
        rtnList.add(rtnMap);
        // ----------------------------------------------------------------

        ///////
        rtnMap = new HashMap<String, Object>();
        rtnMap.put("leftFstTitle", makeStyleMsg(msgMap.get("msgEnergy"), 2));
        rtnMap.put("leftSndTitle", makeStyleMsg(msgMap.get("msgRate1"), 0));
        rtnMap.put("leftTrdTitle", "");
        rtnMap.put("topTitle", makeStyleMsg(msgMap.get("msgActImp"), 8));
        rtnMap.put("value", makeStyleValue((String)resultMap.get("LSTACTENGYIMPRAT1")));
        rtnList.add(rtnMap);

        ///////
        rtnMap = new HashMap<String, Object>();
        rtnMap.put("leftFstTitle", makeStyleMsg(msgMap.get("msgEnergy"), 2));
        rtnMap.put("leftSndTitle", makeStyleMsg(msgMap.get("msgRate1"), 0));
        rtnMap.put("leftTrdTitle", "");
        rtnMap.put("topTitle", makeStyleMsg(msgMap.get("msgActExp"), 9));
        rtnMap.put("value", makeStyleValue((String)resultMap.get("LSTACTENGYEXPRAT1")));
        rtnList.add(rtnMap);

        ///////
        rtnMap = new HashMap<String, Object>();
        rtnMap.put("leftFstTitle", makeStyleMsg(msgMap.get("msgEnergy"), 2));
        rtnMap.put("leftSndTitle", makeStyleMsg(msgMap.get("msgRate1"), 0));
        rtnMap.put("leftTrdTitle", "");
        rtnMap.put("topTitle", makeStyleMsg(msgMap.get("msgRactLagImp"), 10));
        rtnMap.put("value", makeStyleValue((String)resultMap.get("LSTRACTENGYLAGIMPRAT1")));
        rtnList.add(rtnMap);

        ///////
        rtnMap = new HashMap<String, Object>();
        rtnMap.put("leftFstTitle", makeStyleMsg(msgMap.get("msgEnergy"), 2));
        rtnMap.put("leftSndTitle", makeStyleMsg(msgMap.get("msgRate1"), 0));
        rtnMap.put("leftTrdTitle", "");
        rtnMap.put("topTitle", makeStyleMsg(msgMap.get("msgRactLeadImp"), 11));
        rtnMap.put("value", makeStyleValue((String)resultMap.get("LSTRACTENGYLEADIMPRAT1")));
        rtnList.add(rtnMap);

        ///////
        rtnMap = new HashMap<String, Object>();
        rtnMap.put("leftFstTitle", makeStyleMsg(msgMap.get("msgEnergy"), 2));
        rtnMap.put("leftSndTitle", makeStyleMsg(msgMap.get("msgRate1"), 0));
        rtnMap.put("leftTrdTitle", "");
        rtnMap.put("topTitle", makeStyleMsg(msgMap.get("msgRactLagExp"), 12));
        rtnMap.put("value", makeStyleValue((String)resultMap.get("LSTRACTENGYLAGEXPRAT1")));
        rtnList.add(rtnMap);

        ///////
        rtnMap = new HashMap<String, Object>();
        rtnMap.put("leftFstTitle", makeStyleMsg(msgMap.get("msgEnergy"), 2));
        rtnMap.put("leftSndTitle", makeStyleMsg(msgMap.get("msgRate1"), 0));
        rtnMap.put("leftTrdTitle", "");
        rtnMap.put("topTitle", makeStyleMsg(msgMap.get("msgRactLeadExp"), 13));
        rtnMap.put("value", makeStyleValue((String)resultMap.get("LSTRACTENGYLEADEXPRAT1")));
        rtnList.add(rtnMap);
        
        ///////
        rtnMap = new HashMap<String, Object>();
        rtnMap.put("leftFstTitle", makeStyleMsg(msgMap.get("msgEnergy"), 2));
        rtnMap.put("leftSndTitle", makeStyleMsg(msgMap.get("msgRate1"), 0));
        rtnMap.put("leftTrdTitle", "");
        rtnMap.put("topTitle", makeStyleMsg(msgMap.get("msgkVah1"), 14));
        rtnMap.put("value", makeStyleValue("0"));
        rtnList.add(rtnMap);
        // ----------------------------------------------------------------

        ///////
        rtnMap = new HashMap<String, Object>();
        rtnMap.put("leftFstTitle", makeStyleMsg(msgMap.get("msgEnergy"), 2));
        rtnMap.put("leftSndTitle", makeStyleMsg(msgMap.get("msgRate2"), 0));
        rtnMap.put("leftTrdTitle", "");
        rtnMap.put("topTitle", makeStyleMsg(msgMap.get("msgActImp"), 8));
        rtnMap.put("value", makeStyleValue((String)resultMap.get("LSTACTENGYIMPRAT2")));
        rtnList.add(rtnMap);

        ///////
        rtnMap = new HashMap<String, Object>();
        rtnMap.put("leftFstTitle", makeStyleMsg(msgMap.get("msgEnergy"), 2));
        rtnMap.put("leftSndTitle", makeStyleMsg(msgMap.get("msgRate2"), 0));
        rtnMap.put("leftTrdTitle", "");
        rtnMap.put("topTitle", makeStyleMsg(msgMap.get("msgActExp"), 9));
        rtnMap.put("value", makeStyleValue((String)resultMap.get("LSTACTENGYEXPRAT2")));
        rtnList.add(rtnMap);

        ///////
        rtnMap = new HashMap<String, Object>();
        rtnMap.put("leftFstTitle", makeStyleMsg(msgMap.get("msgEnergy"), 2));
        rtnMap.put("leftSndTitle", makeStyleMsg(msgMap.get("msgRate2"), 0));
        rtnMap.put("leftTrdTitle", "");
        rtnMap.put("topTitle", makeStyleMsg(msgMap.get("msgRactLagImp"), 10));
        rtnMap.put("value", makeStyleValue((String)resultMap.get("LSTRACTENGYLAGIMPRAT2")));
        rtnList.add(rtnMap);

        ///////
        rtnMap = new HashMap<String, Object>();
        rtnMap.put("leftFstTitle", makeStyleMsg(msgMap.get("msgEnergy"), 2));
        rtnMap.put("leftSndTitle", makeStyleMsg(msgMap.get("msgRate2"), 0));
        rtnMap.put("leftTrdTitle", "");
        rtnMap.put("topTitle", makeStyleMsg(msgMap.get("msgRactLeadImp"), 11));
        rtnMap.put("value", makeStyleValue((String)resultMap.get("LSTRACTENGYLEADIMPRAT2")));
        rtnList.add(rtnMap);

        ///////
        rtnMap = new HashMap<String, Object>();
        rtnMap.put("leftFstTitle", makeStyleMsg(msgMap.get("msgEnergy"), 2));
        rtnMap.put("leftSndTitle", makeStyleMsg(msgMap.get("msgRate2"), 0));
        rtnMap.put("leftTrdTitle", "");
        rtnMap.put("topTitle", makeStyleMsg(msgMap.get("msgRactLagExp"), 12));
        rtnMap.put("value", makeStyleValue((String)resultMap.get("LSTRACTENGYLAGEXPRAT2")));
        rtnList.add(rtnMap);

        ///////
        rtnMap = new HashMap<String, Object>();
        rtnMap.put("leftFstTitle", makeStyleMsg(msgMap.get("msgEnergy"), 2));
        rtnMap.put("leftSndTitle", makeStyleMsg(msgMap.get("msgRate2"), 0));
        rtnMap.put("leftTrdTitle", "");
        rtnMap.put("topTitle", makeStyleMsg(msgMap.get("msgRactLeadExp"), 13));
        rtnMap.put("value", makeStyleValue((String)resultMap.get("LSTRACTENGYLEADEXPRAT2")));
        rtnList.add(rtnMap);
        
        ///////
        rtnMap = new HashMap<String, Object>();
        rtnMap.put("leftFstTitle", makeStyleMsg(msgMap.get("msgEnergy"), 2));
        rtnMap.put("leftSndTitle", makeStyleMsg(msgMap.get("msgRate2"), 0));
        rtnMap.put("leftTrdTitle", "");
        rtnMap.put("topTitle", makeStyleMsg(msgMap.get("msgkVah1"), 14));
        rtnMap.put("value", makeStyleValue("0"));
        rtnList.add(rtnMap);
        // ----------------------------------------------------------------

        ///////
        rtnMap = new HashMap<String, Object>();
        rtnMap.put("leftFstTitle", makeStyleMsg(msgMap.get("msgEnergy"), 2));
        rtnMap.put("leftSndTitle", makeStyleMsg(msgMap.get("msgRate3"), 0));
        rtnMap.put("leftTrdTitle", "");
        rtnMap.put("topTitle", makeStyleMsg(msgMap.get("msgActImp"), 8));
        rtnMap.put("value", makeStyleValue((String)resultMap.get("LSTACTENGYIMPRAT3")));
        rtnList.add(rtnMap);

        ///////
        rtnMap = new HashMap<String, Object>();
        rtnMap.put("leftFstTitle", makeStyleMsg(msgMap.get("msgEnergy"), 2));
        rtnMap.put("leftSndTitle", makeStyleMsg(msgMap.get("msgRate3"), 0));
        rtnMap.put("leftTrdTitle", "");
        rtnMap.put("topTitle", makeStyleMsg(msgMap.get("msgActExp"), 9));
        rtnMap.put("value", makeStyleValue((String)resultMap.get("LSTACTENGYEXPRAT3")));
        rtnList.add(rtnMap);

        ///////
        rtnMap = new HashMap<String, Object>();
        rtnMap.put("leftFstTitle", makeStyleMsg(msgMap.get("msgEnergy"), 2));
        rtnMap.put("leftSndTitle", makeStyleMsg(msgMap.get("msgRate3"), 0));
        rtnMap.put("leftTrdTitle", "");
        rtnMap.put("topTitle", makeStyleMsg(msgMap.get("msgRactLagImp"), 10));
        rtnMap.put("value", makeStyleValue((String)resultMap.get("LSTRACTENGYLAGIMPRAT3")));
        rtnList.add(rtnMap);

        ///////
        rtnMap = new HashMap<String, Object>();
        rtnMap.put("leftFstTitle", makeStyleMsg(msgMap.get("msgEnergy"), 2));
        rtnMap.put("leftSndTitle", makeStyleMsg(msgMap.get("msgRate3"), 0));
        rtnMap.put("leftTrdTitle", "");
        rtnMap.put("topTitle", makeStyleMsg(msgMap.get("msgRactLeadImp"), 11));
        rtnMap.put("value", makeStyleValue((String)resultMap.get("LSTRACTENGYLEADIMPRAT3")));
        rtnList.add(rtnMap);

        ///////
        rtnMap = new HashMap<String, Object>();
        rtnMap.put("leftFstTitle", makeStyleMsg(msgMap.get("msgEnergy"), 2));
        rtnMap.put("leftSndTitle", makeStyleMsg(msgMap.get("msgRate3"), 0));
        rtnMap.put("leftTrdTitle", "");
        rtnMap.put("topTitle", makeStyleMsg(msgMap.get("msgRactLagExp"), 12));
        rtnMap.put("value", makeStyleValue((String)resultMap.get("LSTRACTENGYLAGEXPRAT3")));
        rtnList.add(rtnMap);

        ///////
        rtnMap = new HashMap<String, Object>();
        rtnMap.put("leftFstTitle", makeStyleMsg(msgMap.get("msgEnergy"), 2));
        rtnMap.put("leftSndTitle", makeStyleMsg(msgMap.get("msgRate3"), 0));
        rtnMap.put("leftTrdTitle", "");
        rtnMap.put("topTitle", makeStyleMsg(msgMap.get("msgRactLeadExp"), 13));
        rtnMap.put("value", makeStyleValue((String)resultMap.get("LSTRACTENGYLEADEXPRAT3")));
        rtnList.add(rtnMap);
        
        ///////
        rtnMap = new HashMap<String, Object>();
        rtnMap.put("leftFstTitle", makeStyleMsg(msgMap.get("msgEnergy"), 2));
        rtnMap.put("leftSndTitle", makeStyleMsg(msgMap.get("msgRate3"), 0));
        rtnMap.put("leftTrdTitle", "");
        rtnMap.put("topTitle", makeStyleMsg(msgMap.get("msgkVah1"), 14));
        rtnMap.put("value", makeStyleValue("0"));
        rtnList.add(rtnMap);
        // ----------------------------------------------------------------

        /////// Time
        rtnMap = new HashMap<String, Object>();
        rtnMap.put("leftFstTitle", makeStyleMsg(msgMap.get("msgTotDemandTime"), 3));
        rtnMap.put("leftSndTitle", "");
        rtnMap.put("leftTrdTitle", makeStyleTrdMsg("0"));
        rtnMap.put("topTitle", makeStyleMsg(msgMap.get("msgActImp"), 8));
        rtnMap.put("value", makeStyleValue((String)resultMap.get("LSTACTDMDMXTIMEIMPTOT")));
        rtnList.add(rtnMap);

        /////// Time
        rtnMap = new HashMap<String, Object>();
        rtnMap.put("leftFstTitle", makeStyleMsg(msgMap.get("msgTotDemandTime"), 3));
        rtnMap.put("leftSndTitle", "");
        rtnMap.put("leftTrdTitle", makeStyleTrdMsg("0"));
        rtnMap.put("topTitle", makeStyleMsg(msgMap.get("msgActExp"), 9));
        rtnMap.put("value", makeStyleValue((String)resultMap.get("LSTACTDMDMXTIMEEXPTOT")));
        rtnList.add(rtnMap);

        /////// Time
        rtnMap = new HashMap<String, Object>();
        rtnMap.put("leftFstTitle", makeStyleMsg(msgMap.get("msgTotDemandTime"), 3));
        rtnMap.put("leftSndTitle", "");
        rtnMap.put("leftTrdTitle", makeStyleTrdMsg("0"));
        rtnMap.put("topTitle", makeStyleMsg(msgMap.get("msgRactLagImp"), 10));
        rtnMap.put("value", makeStyleValue((String)resultMap.get("LSTRACTDMDMXTIMELAGIMPTOT")));
        rtnList.add(rtnMap);

        /////// Time
        rtnMap = new HashMap<String, Object>();
        rtnMap.put("leftFstTitle", makeStyleMsg(msgMap.get("msgTotDemandTime"), 3));
        rtnMap.put("leftSndTitle", "");
        rtnMap.put("leftTrdTitle", makeStyleTrdMsg("0"));
        rtnMap.put("topTitle", makeStyleMsg(msgMap.get("msgRactLeadImp"), 11));
        rtnMap.put("value", makeStyleValue((String)resultMap.get("LSTRACTDMDMXTIMELEADIMPTOT")));
        rtnList.add(rtnMap);

        /////// Time
        rtnMap = new HashMap<String, Object>();
        rtnMap.put("leftFstTitle", makeStyleMsg(msgMap.get("msgTotDemandTime"), 3));
        rtnMap.put("leftSndTitle", "");
        rtnMap.put("leftTrdTitle", makeStyleTrdMsg("0"));
        rtnMap.put("topTitle", makeStyleMsg(msgMap.get("msgRactLagExp"), 12));
        rtnMap.put("value", makeStyleValue((String)resultMap.get("LSTRACTDMDMXTIMELAGEXPTOT")));
        rtnList.add(rtnMap);

        /////// Time
        rtnMap = new HashMap<String, Object>();
        rtnMap.put("leftFstTitle", makeStyleMsg(msgMap.get("msgTotDemandTime"), 3));
        rtnMap.put("leftSndTitle", "");
        rtnMap.put("leftTrdTitle", makeStyleTrdMsg("0"));
        rtnMap.put("topTitle", makeStyleMsg(msgMap.get("msgRactLeadExp"), 13));
        rtnMap.put("value", makeStyleValue((String)resultMap.get("LSTRACTDMDMXTIMELEADEXPTOT")));
        rtnList.add(rtnMap);
        
        /////// Time
        rtnMap = new HashMap<String, Object>();
        rtnMap.put("leftFstTitle", makeStyleMsg(msgMap.get("msgTotDemandTime"), 3));
        rtnMap.put("leftSndTitle", "");
        rtnMap.put("leftTrdTitle", makeStyleTrdMsg("0"));
        rtnMap.put("topTitle", makeStyleMsg(msgMap.get("msgkVah1"), 14));
        rtnMap.put("value", makeStyleValue((String)resultMap.get("LSTMAXDMDKVAH1TIMERATETOTAL")));
        rtnList.add(rtnMap);
        // ----------------------------------------------------------------
        
        ///////
        rtnMap = new HashMap<String, Object>();
        rtnMap.put("leftFstTitle", makeStyleMsg(msgMap.get("msgTotDemandTime"), 3));
        rtnMap.put("leftSndTitle", "");
        rtnMap.put("leftTrdTitle", makeStyleTrdMsg("1"));
        rtnMap.put("topTitle", makeStyleMsg(msgMap.get("msgActImp"), 8));
        rtnMap.put("value", makeStyleValue((String)resultMap.get("LSTACTDMDMXIMPTOT")));
        rtnList.add(rtnMap);

        ///////
        rtnMap = new HashMap<String, Object>();
        rtnMap.put("leftFstTitle", makeStyleMsg(msgMap.get("msgTotDemandTime"), 3));
        rtnMap.put("leftSndTitle", "");
        rtnMap.put("leftTrdTitle", makeStyleTrdMsg("1"));
        rtnMap.put("topTitle", makeStyleMsg(msgMap.get("msgActExp"), 9));
        rtnMap.put("value", makeStyleValue((String)resultMap.get("LSTACTDMDMXEXPTOT")));
        rtnList.add(rtnMap);

        ///////
        rtnMap = new HashMap<String, Object>();
        rtnMap.put("leftFstTitle", makeStyleMsg(msgMap.get("msgTotDemandTime"), 3));
        rtnMap.put("leftSndTitle", "");
        rtnMap.put("leftTrdTitle", makeStyleTrdMsg("1"));
        rtnMap.put("topTitle", makeStyleMsg(msgMap.get("msgRactLagImp"), 10));
        rtnMap.put("value", makeStyleValue((String)resultMap.get("LSTRACTDMDMXLAGIMPTOT")));
        rtnList.add(rtnMap);

        ///////
        rtnMap = new HashMap<String, Object>();
        rtnMap.put("leftFstTitle", makeStyleMsg(msgMap.get("msgTotDemandTime"), 3));
        rtnMap.put("leftSndTitle", "");
        rtnMap.put("leftTrdTitle", makeStyleTrdMsg("1"));
        rtnMap.put("topTitle", makeStyleMsg(msgMap.get("msgRactLeadImp"), 11));
        rtnMap.put("value", makeStyleValue((String)resultMap.get("LSTRACTDMDMXLEADIMPTOT")));
        rtnList.add(rtnMap);

        ///////
        rtnMap = new HashMap<String, Object>();
        rtnMap.put("leftFstTitle", makeStyleMsg(msgMap.get("msgTotDemandTime"), 3));
        rtnMap.put("leftSndTitle", "");
        rtnMap.put("leftTrdTitle", makeStyleTrdMsg("1"));
        rtnMap.put("topTitle", makeStyleMsg(msgMap.get("msgRactLagExp"), 12));
        rtnMap.put("value", makeStyleValue((String)resultMap.get("LSTRACTDMDMXLAGEXPTOT")));
        rtnList.add(rtnMap);

        ///////
        rtnMap = new HashMap<String, Object>();
        rtnMap.put("leftFstTitle", makeStyleMsg(msgMap.get("msgTotDemandTime"), 3));
        rtnMap.put("leftSndTitle", "");
        rtnMap.put("leftTrdTitle", makeStyleTrdMsg("1"));
        rtnMap.put("topTitle", makeStyleMsg(msgMap.get("msgRactLeadExp"), 13));
        rtnMap.put("value", makeStyleValue((String)resultMap.get("LSTRACTDMDMXLEADEXPTOT")));
        rtnList.add(rtnMap);
        
        ///////
        rtnMap = new HashMap<String, Object>();
        rtnMap.put("leftFstTitle", makeStyleMsg(msgMap.get("msgTotDemandTime"), 3));
        rtnMap.put("leftSndTitle", "");
        rtnMap.put("leftTrdTitle", makeStyleTrdMsg("1"));
        rtnMap.put("topTitle", makeStyleMsg(msgMap.get("msgkVah1"), 14));
        rtnMap.put("value", makeStyleValue((String)resultMap.get("LSTMAXDMDKVAH1RATETOTAL")));
        rtnList.add(rtnMap);
        // ----------------------------------------------------------------

        /////// Time
        rtnMap = new HashMap<String, Object>();
        rtnMap.put("leftFstTitle", makeStyleMsg(msgMap.get("msgMaxDemandTime"), 4));
        rtnMap.put("leftSndTitle", makeStyleMsg(msgMap.get("msgRate1"), 0));
        rtnMap.put("leftTrdTitle", makeStyleTrdMsg("0"));
        rtnMap.put("topTitle", makeStyleMsg(msgMap.get("msgActImp"), 8));
        rtnMap.put("value", makeStyleValue((String)resultMap.get("LSTACTDMDMXTIMEIMPRAT1")));
        rtnList.add(rtnMap);

        /////// Time
        rtnMap = new HashMap<String, Object>();
        rtnMap.put("leftFstTitle", makeStyleMsg(msgMap.get("msgMaxDemandTime"), 4));
        rtnMap.put("leftSndTitle", makeStyleMsg(msgMap.get("msgRate1"), 0));
        rtnMap.put("leftTrdTitle", makeStyleTrdMsg("0"));
        rtnMap.put("topTitle", makeStyleMsg(msgMap.get("msgActExp"), 9));
        rtnMap.put("value", makeStyleValue((String)resultMap.get("LSTACTDMDMXTIMEEXPRAT1")));
        rtnList.add(rtnMap);

        /////// Time
        rtnMap = new HashMap<String, Object>();
        rtnMap.put("leftFstTitle", makeStyleMsg(msgMap.get("msgMaxDemandTime"), 4));
        rtnMap.put("leftSndTitle", makeStyleMsg(msgMap.get("msgRate1"), 0));
        rtnMap.put("leftTrdTitle", makeStyleTrdMsg("0"));
        rtnMap.put("topTitle", makeStyleMsg(msgMap.get("msgRactLagImp"), 10));
        rtnMap.put("value", makeStyleValue((String)resultMap.get("LSTRACTDMDMXTIMELAGIMPRAT1")));
        rtnList.add(rtnMap);

        /////// Time
        rtnMap = new HashMap<String, Object>();
        rtnMap.put("leftFstTitle", makeStyleMsg(msgMap.get("msgMaxDemandTime"), 4));
        rtnMap.put("leftSndTitle", makeStyleMsg(msgMap.get("msgRate1"), 0));
        rtnMap.put("leftTrdTitle", makeStyleTrdMsg("0"));
        rtnMap.put("topTitle", makeStyleMsg(msgMap.get("msgRactLeadImp"), 11));
        rtnMap.put("value", makeStyleValue((String)resultMap.get("LSTRACTDMDMXTIMELEADIMPRAT1")));
        rtnList.add(rtnMap);

        /////// Time
        rtnMap = new HashMap<String, Object>();
        rtnMap.put("leftFstTitle", makeStyleMsg(msgMap.get("msgMaxDemandTime"), 4));
        rtnMap.put("leftSndTitle", makeStyleMsg(msgMap.get("msgRate1"), 0));
        rtnMap.put("leftTrdTitle", makeStyleTrdMsg("0"));
        rtnMap.put("topTitle", makeStyleMsg(msgMap.get("msgRactLagExp"), 12));
        rtnMap.put("value", makeStyleValue((String)resultMap.get("LSTRACTDMDMXTIMELAGEXPRAT1")));
        rtnList.add(rtnMap);

        /////// Time
        rtnMap = new HashMap<String, Object>();
        rtnMap.put("leftFstTitle", makeStyleMsg(msgMap.get("msgMaxDemandTime"), 4));
        rtnMap.put("leftSndTitle", makeStyleMsg(msgMap.get("msgRate1"), 0));
        rtnMap.put("leftTrdTitle", makeStyleTrdMsg("0"));
        rtnMap.put("topTitle", makeStyleMsg(msgMap.get("msgRactLeadExp"), 13));
        rtnMap.put("value", makeStyleValue((String)resultMap.get("LSTRACTDMDMXTIMELEADEXPRAT1")));
        rtnList.add(rtnMap);
        
        /////// Time
        rtnMap = new HashMap<String, Object>();
        rtnMap.put("leftFstTitle", makeStyleMsg(msgMap.get("msgMaxDemandTime"), 4));
        rtnMap.put("leftSndTitle", makeStyleMsg(msgMap.get("msgRate1"), 0));
        rtnMap.put("leftTrdTitle", makeStyleTrdMsg("0"));
        rtnMap.put("topTitle", makeStyleMsg(msgMap.get("msgkVah1"), 14));
        rtnMap.put("value", makeStyleValue((String)resultMap.get("LSTMAXDMDKVAH1TIMERATE1")));
        rtnList.add(rtnMap);
        // ----------------------------------------------------------------

        ///////
        rtnMap = new HashMap<String, Object>();
        rtnMap.put("leftFstTitle", makeStyleMsg(msgMap.get("msgMaxDemandTime"), 4));
        rtnMap.put("leftSndTitle", makeStyleMsg(msgMap.get("msgRate1"), 0));
        rtnMap.put("leftTrdTitle", makeStyleTrdMsg("1"));
        rtnMap.put("topTitle", makeStyleMsg(msgMap.get("msgActImp"), 8));
        rtnMap.put("value", makeStyleValue((String)resultMap.get("LSTACTDMDMXIMPRAT1")));
        rtnList.add(rtnMap);

        ///////
        rtnMap = new HashMap<String, Object>();
        rtnMap.put("leftFstTitle", makeStyleMsg(msgMap.get("msgMaxDemandTime"), 4));
        rtnMap.put("leftSndTitle", makeStyleMsg(msgMap.get("msgRate1"), 0));
        rtnMap.put("leftTrdTitle", makeStyleTrdMsg("1"));
        rtnMap.put("topTitle", makeStyleMsg(msgMap.get("msgActExp"), 9));
        rtnMap.put("value", makeStyleValue((String)resultMap.get("LSTACTDMDMXEXPRAT1")));
        rtnList.add(rtnMap);

        ///////
        rtnMap = new HashMap<String, Object>();
        rtnMap.put("leftFstTitle", makeStyleMsg(msgMap.get("msgMaxDemandTime"), 4));
        rtnMap.put("leftSndTitle", makeStyleMsg(msgMap.get("msgRate1"), 0));
        rtnMap.put("leftTrdTitle", makeStyleTrdMsg("1"));
        rtnMap.put("topTitle", makeStyleMsg(msgMap.get("msgRactLagImp"), 10));
        rtnMap.put("value", makeStyleValue((String)resultMap.get("LSTRACTDMDMXLAGIMPRAT1")));
        rtnList.add(rtnMap);

        ///////
        rtnMap = new HashMap<String, Object>();
        rtnMap.put("leftFstTitle", makeStyleMsg(msgMap.get("msgMaxDemandTime"), 4));
        rtnMap.put("leftSndTitle", makeStyleMsg(msgMap.get("msgRate1"), 0));
        rtnMap.put("leftTrdTitle", makeStyleTrdMsg("1"));
        rtnMap.put("topTitle", makeStyleMsg(msgMap.get("msgRactLeadImp"), 11));
        rtnMap.put("value", makeStyleValue((String)resultMap.get("LSTRACTDMDMXLEADIMPRAT1")));
        rtnList.add(rtnMap);

        ///////
        rtnMap = new HashMap<String, Object>();
        rtnMap.put("leftFstTitle", makeStyleMsg(msgMap.get("msgMaxDemandTime"), 4));
        rtnMap.put("leftSndTitle", makeStyleMsg(msgMap.get("msgRate1"), 0));
        rtnMap.put("leftTrdTitle", makeStyleTrdMsg("1"));
        rtnMap.put("topTitle", makeStyleMsg(msgMap.get("msgRactLagExp"), 12));
        rtnMap.put("value", makeStyleValue((String)resultMap.get("LSTRACTDMDMXLAGEXPRAT1")));
        rtnList.add(rtnMap);

        ///////
        rtnMap = new HashMap<String, Object>();
        rtnMap.put("leftFstTitle", makeStyleMsg(msgMap.get("msgMaxDemandTime"), 4));
        rtnMap.put("leftSndTitle", makeStyleMsg(msgMap.get("msgRate1"), 0));
        rtnMap.put("leftTrdTitle", makeStyleTrdMsg("1"));
        rtnMap.put("topTitle", makeStyleMsg(msgMap.get("msgRactLeadExp"), 13));
        rtnMap.put("value", makeStyleValue((String)resultMap.get("LSTRACTDMDMXLEADEXPRAT1")));
        rtnList.add(rtnMap);
        
        ///////
        rtnMap = new HashMap<String, Object>();
        rtnMap.put("leftFstTitle", makeStyleMsg(msgMap.get("msgMaxDemandTime"), 4));
        rtnMap.put("leftSndTitle", makeStyleMsg(msgMap.get("msgRate1"), 0));
        rtnMap.put("leftTrdTitle", makeStyleTrdMsg("1"));
        rtnMap.put("topTitle", makeStyleMsg(msgMap.get("msgkVah1"), 14));
        rtnMap.put("value", makeStyleValue((String)resultMap.get("LSTMAXDMDKVAH1RATE1")));
        rtnList.add(rtnMap);
        // ----------------------------------------------------------------

        /////// Time
        rtnMap = new HashMap<String, Object>();
        rtnMap.put("leftFstTitle", makeStyleMsg(msgMap.get("msgMaxDemandTime"), 4));
        rtnMap.put("leftSndTitle", makeStyleMsg(msgMap.get("msgRate2"), 0));
        rtnMap.put("leftTrdTitle", makeStyleTrdMsg("0"));
        rtnMap.put("topTitle", makeStyleMsg(msgMap.get("msgActImp"), 8));
        rtnMap.put("value", makeStyleValue((String)resultMap.get("LSTACTDMDMXTIMEIMPRAT2")));
        rtnList.add(rtnMap);

        /////// Time
        rtnMap = new HashMap<String, Object>();
        rtnMap.put("leftFstTitle", makeStyleMsg(msgMap.get("msgMaxDemandTime"), 4));
        rtnMap.put("leftSndTitle", makeStyleMsg(msgMap.get("msgRate2"), 0));
        rtnMap.put("leftTrdTitle", makeStyleTrdMsg("0"));
        rtnMap.put("topTitle", makeStyleMsg(msgMap.get("msgActExp"), 9));
        rtnMap.put("value", makeStyleValue((String)resultMap.get("LSTACTDMDMXTIMEEXPRAT2")));
        rtnList.add(rtnMap);

        /////// Time
        rtnMap = new HashMap<String, Object>();
        rtnMap.put("leftFstTitle", makeStyleMsg(msgMap.get("msgMaxDemandTime"), 4));
        rtnMap.put("leftSndTitle", makeStyleMsg(msgMap.get("msgRate2"), 0));
        rtnMap.put("leftTrdTitle", makeStyleTrdMsg("0"));
        rtnMap.put("topTitle", makeStyleMsg(msgMap.get("msgRactLagImp"), 10));
        rtnMap.put("value", makeStyleValue((String)resultMap.get("LSTRACTDMDMXTIMELAGIMPRAT2")));
        rtnList.add(rtnMap);

        /////// Time
        rtnMap = new HashMap<String, Object>();
        rtnMap.put("leftFstTitle", makeStyleMsg(msgMap.get("msgMaxDemandTime"), 4));
        rtnMap.put("leftSndTitle", makeStyleMsg(msgMap.get("msgRate2"), 0));
        rtnMap.put("leftTrdTitle", makeStyleTrdMsg("0"));
        rtnMap.put("topTitle", makeStyleMsg(msgMap.get("msgRactLeadImp"), 11));
        rtnMap.put("value", makeStyleValue((String)resultMap.get("LSTRACTDMDMXTIMELEADIMPRAT2")));
        rtnList.add(rtnMap);

        /////// Time
        rtnMap = new HashMap<String, Object>();
        rtnMap.put("leftFstTitle", makeStyleMsg(msgMap.get("msgMaxDemandTime"), 4));
        rtnMap.put("leftSndTitle", makeStyleMsg(msgMap.get("msgRate2"), 0));
        rtnMap.put("leftTrdTitle", makeStyleTrdMsg("0"));
        rtnMap.put("topTitle", makeStyleMsg(msgMap.get("msgRactLagExp"), 12));
        rtnMap.put("value", makeStyleValue((String)resultMap.get("LSTRACTDMDMXTIMELAGEXPRAT2")));
        rtnList.add(rtnMap);

        /////// Time
        rtnMap = new HashMap<String, Object>();
        rtnMap.put("leftFstTitle", makeStyleMsg(msgMap.get("msgMaxDemandTime"), 4));
        rtnMap.put("leftSndTitle", makeStyleMsg(msgMap.get("msgRate2"), 0));
        rtnMap.put("leftTrdTitle", makeStyleTrdMsg("0"));
        rtnMap.put("topTitle", makeStyleMsg(msgMap.get("msgRactLeadExp"), 13));
        rtnMap.put("value", makeStyleValue((String)resultMap.get("LSTRACTDMDMXTIMELEADEXPRAT2")));
        rtnList.add(rtnMap);
        
        /////// Time
        rtnMap = new HashMap<String, Object>();
        rtnMap.put("leftFstTitle", makeStyleMsg(msgMap.get("msgMaxDemandTime"), 4));
        rtnMap.put("leftSndTitle", makeStyleMsg(msgMap.get("msgRate2"), 0));
        rtnMap.put("leftTrdTitle", makeStyleTrdMsg("0"));
        rtnMap.put("topTitle", makeStyleMsg(msgMap.get("msgkVah1"), 14));
        rtnMap.put("value", makeStyleValue((String)resultMap.get("LSTMAXDMDKVAH1TIMERATE2")));
        rtnList.add(rtnMap);
        // ----------------------------------------------------------------

        ///////
        rtnMap = new HashMap<String, Object>();
        rtnMap.put("leftFstTitle", makeStyleMsg(msgMap.get("msgMaxDemandTime"), 4));
        rtnMap.put("leftSndTitle", makeStyleMsg(msgMap.get("msgRate2"), 0));
        rtnMap.put("leftTrdTitle", makeStyleTrdMsg("1"));
        rtnMap.put("topTitle", makeStyleMsg(msgMap.get("msgActImp"), 8));
        rtnMap.put("value", makeStyleValue((String)resultMap.get("LSTACTDMDMXIMPRAT2")));
        rtnList.add(rtnMap);

        ///////
        rtnMap = new HashMap<String, Object>();
        rtnMap.put("leftFstTitle", makeStyleMsg(msgMap.get("msgMaxDemandTime"), 4));
        rtnMap.put("leftSndTitle", makeStyleMsg(msgMap.get("msgRate2"), 0));
        rtnMap.put("leftTrdTitle", makeStyleTrdMsg("1"));
        rtnMap.put("topTitle", makeStyleMsg(msgMap.get("msgActExp"), 9));
        rtnMap.put("value", makeStyleValue((String)resultMap.get("LSTACTDMDMXEXPRAT2")));
        rtnList.add(rtnMap);

        ///////
        rtnMap = new HashMap<String, Object>();
        rtnMap.put("leftFstTitle", makeStyleMsg(msgMap.get("msgMaxDemandTime"), 4));
        rtnMap.put("leftSndTitle", makeStyleMsg(msgMap.get("msgRate2"), 0));
        rtnMap.put("leftTrdTitle", makeStyleTrdMsg("1"));
        rtnMap.put("topTitle", makeStyleMsg(msgMap.get("msgRactLagImp"), 10));
        rtnMap.put("value", makeStyleValue((String)resultMap.get("LSTRACTDMDMXLAGIMPRAT2")));
        rtnList.add(rtnMap);

        ///////
        rtnMap = new HashMap<String, Object>();
        rtnMap.put("leftFstTitle", makeStyleMsg(msgMap.get("msgMaxDemandTime"), 4));
        rtnMap.put("leftSndTitle", makeStyleMsg(msgMap.get("msgRate2"), 0));
        rtnMap.put("leftTrdTitle", makeStyleTrdMsg("1"));
        rtnMap.put("topTitle", makeStyleMsg(msgMap.get("msgRactLeadImp"), 11));
        rtnMap.put("value", makeStyleValue((String)resultMap.get("LSTRACTDMDMXLEADIMPRAT2")));
        rtnList.add(rtnMap);

        ///////
        rtnMap = new HashMap<String, Object>();
        rtnMap.put("leftFstTitle", makeStyleMsg(msgMap.get("msgMaxDemandTime"), 4));
        rtnMap.put("leftSndTitle", makeStyleMsg(msgMap.get("msgRate2"), 0));
        rtnMap.put("leftTrdTitle", makeStyleTrdMsg("1"));
        rtnMap.put("topTitle", makeStyleMsg(msgMap.get("msgRactLagExp"), 12));
        rtnMap.put("value", makeStyleValue((String)resultMap.get("LSTRACTDMDMXLAGEXPRAT2")));
        rtnList.add(rtnMap);

        ///////
        rtnMap = new HashMap<String, Object>();
        rtnMap.put("leftFstTitle", makeStyleMsg(msgMap.get("msgMaxDemandTime"), 4));
        rtnMap.put("leftSndTitle", makeStyleMsg(msgMap.get("msgRate2"), 0));
        rtnMap.put("leftTrdTitle", makeStyleTrdMsg("1"));
        rtnMap.put("topTitle", makeStyleMsg(msgMap.get("msgRactLeadExp"), 13));
        rtnMap.put("value", makeStyleValue((String)resultMap.get("LSTRACTDMDMXLEADEXPRAT2")));
        rtnList.add(rtnMap);
        
        ///////
        rtnMap = new HashMap<String, Object>();
        rtnMap.put("leftFstTitle", makeStyleMsg(msgMap.get("msgMaxDemandTime"), 4));
        rtnMap.put("leftSndTitle", makeStyleMsg(msgMap.get("msgRate2"), 0));
        rtnMap.put("leftTrdTitle", makeStyleTrdMsg("1"));
        rtnMap.put("topTitle", makeStyleMsg(msgMap.get("msgkVah1"), 14));
        rtnMap.put("value", makeStyleValue((String)resultMap.get("LSTMAXDMDKVAH1RATE2")));
        rtnList.add(rtnMap);
        // ----------------------------------------------------------------

        /////// Time
        rtnMap = new HashMap<String, Object>();
        rtnMap.put("leftFstTitle", makeStyleMsg(msgMap.get("msgMaxDemandTime"), 4));
        rtnMap.put("leftSndTitle", makeStyleMsg(msgMap.get("msgRate3"), 0));
        rtnMap.put("leftTrdTitle", makeStyleTrdMsg("0"));
        rtnMap.put("topTitle", makeStyleMsg(msgMap.get("msgActImp"), 8));
        rtnMap.put("value", makeStyleValue((String)resultMap.get("LSTACTDMDMXTIMEIMPRAT3")));
        rtnList.add(rtnMap);

        /////// Time
        rtnMap = new HashMap<String, Object>();
        rtnMap.put("leftFstTitle", makeStyleMsg(msgMap.get("msgMaxDemandTime"), 4));
        rtnMap.put("leftSndTitle", makeStyleMsg(msgMap.get("msgRate3"), 0));
        rtnMap.put("leftTrdTitle", makeStyleTrdMsg("0"));
        rtnMap.put("topTitle", makeStyleMsg(msgMap.get("msgActExp"), 9));
        rtnMap.put("value", makeStyleValue((String)resultMap.get("LSTACTDMDMXTIMEEXPRAT3")));
        rtnList.add(rtnMap);

        /////// Time
        rtnMap = new HashMap<String, Object>();
        rtnMap.put("leftFstTitle", makeStyleMsg(msgMap.get("msgMaxDemandTime"), 4));
        rtnMap.put("leftSndTitle", makeStyleMsg(msgMap.get("msgRate3"), 0));
        rtnMap.put("leftTrdTitle", makeStyleTrdMsg("0"));
        rtnMap.put("topTitle", makeStyleMsg(msgMap.get("msgRactLagImp"), 10));
        rtnMap.put("value", makeStyleValue((String)resultMap.get("LSTRACTDMDMXTIMELAGIMPRAT3")));
        rtnList.add(rtnMap);

        /////// Time
        rtnMap = new HashMap<String, Object>();
        rtnMap.put("leftFstTitle", makeStyleMsg(msgMap.get("msgMaxDemandTime"), 4));
        rtnMap.put("leftSndTitle", makeStyleMsg(msgMap.get("msgRate3"), 0));
        rtnMap.put("leftTrdTitle", makeStyleTrdMsg("0"));
        rtnMap.put("topTitle", makeStyleMsg(msgMap.get("msgRactLeadImp"), 11));
        rtnMap.put("value", makeStyleValue((String)resultMap.get("LSTRACTDMDMXTIMELEADIMPRAT3")));
        rtnList.add(rtnMap);

        /////// Time
        rtnMap = new HashMap<String, Object>();
        rtnMap.put("leftFstTitle", makeStyleMsg(msgMap.get("msgMaxDemandTime"), 4));
        rtnMap.put("leftSndTitle", makeStyleMsg(msgMap.get("msgRate3"), 0));
        rtnMap.put("leftTrdTitle", makeStyleTrdMsg("0"));
        rtnMap.put("topTitle", makeStyleMsg(msgMap.get("msgRactLagExp"), 12));
        rtnMap.put("value", makeStyleValue((String)resultMap.get("LSTRACTDMDMXTIMELAGEXPRAT3")));
        rtnList.add(rtnMap);

        /////// Time
        rtnMap = new HashMap<String, Object>();
        rtnMap.put("leftFstTitle", makeStyleMsg(msgMap.get("msgMaxDemandTime"), 4));
        rtnMap.put("leftSndTitle", makeStyleMsg(msgMap.get("msgRate3"), 0));
        rtnMap.put("leftTrdTitle", makeStyleTrdMsg("0"));
        rtnMap.put("topTitle", makeStyleMsg(msgMap.get("msgRactLeadExp"), 13));
        rtnMap.put("value", makeStyleValue((String)resultMap.get("LSTRACTDMDMXTIMELEADEXPRAT3")));
        rtnList.add(rtnMap);
        
        /////// Time
        rtnMap = new HashMap<String, Object>();
        rtnMap.put("leftFstTitle", makeStyleMsg(msgMap.get("msgMaxDemandTime"), 4));
        rtnMap.put("leftSndTitle", makeStyleMsg(msgMap.get("msgRate3"), 0));
        rtnMap.put("leftTrdTitle", makeStyleTrdMsg("0"));
        rtnMap.put("topTitle", makeStyleMsg(msgMap.get("msgkVah1"), 14));
        rtnMap.put("value", makeStyleValue((String)resultMap.get("LSTMAXDMDKVAH1TIMERATE3")));
        rtnList.add(rtnMap);
        // ----------------------------------------------------------------

        ///////
        rtnMap = new HashMap<String, Object>();
        rtnMap.put("leftFstTitle", makeStyleMsg(msgMap.get("msgMaxDemandTime"), 4));
        rtnMap.put("leftSndTitle", makeStyleMsg(msgMap.get("msgRate3"), 0));
        rtnMap.put("leftTrdTitle", makeStyleTrdMsg("1"));
        rtnMap.put("topTitle", makeStyleMsg(msgMap.get("msgActImp"), 8));
        rtnMap.put("value", makeStyleValue((String)resultMap.get("LSTACTDMDMXIMPRAT3")));
        rtnList.add(rtnMap);

        ///////
        rtnMap = new HashMap<String, Object>();
        rtnMap.put("leftFstTitle", makeStyleMsg(msgMap.get("msgMaxDemandTime"), 4));
        rtnMap.put("leftSndTitle", makeStyleMsg(msgMap.get("msgRate3"), 0));
        rtnMap.put("leftTrdTitle", makeStyleTrdMsg("1"));
        rtnMap.put("topTitle", makeStyleMsg(msgMap.get("msgActExp"), 9));
        rtnMap.put("value", makeStyleValue((String)resultMap.get("LSTACTDMDMXEXPRAT3")));
        rtnList.add(rtnMap);

        ///////
        rtnMap = new HashMap<String, Object>();
        rtnMap.put("leftFstTitle", makeStyleMsg(msgMap.get("msgMaxDemandTime"), 4));
        rtnMap.put("leftSndTitle", makeStyleMsg(msgMap.get("msgRate3"), 0));
        rtnMap.put("leftTrdTitle", makeStyleTrdMsg("1"));
        rtnMap.put("topTitle", makeStyleMsg(msgMap.get("msgRactLagImp"), 10));
        rtnMap.put("value", makeStyleValue((String)resultMap.get("LSTRACTDMDMXLAGIMPRAT3")));
        rtnList.add(rtnMap);

        ///////
        rtnMap = new HashMap<String, Object>();
        rtnMap.put("leftFstTitle", makeStyleMsg(msgMap.get("msgMaxDemandTime"), 4));
        rtnMap.put("leftSndTitle", makeStyleMsg(msgMap.get("msgRate3"), 0));
        rtnMap.put("leftTrdTitle", makeStyleTrdMsg("1"));
        rtnMap.put("topTitle", makeStyleMsg(msgMap.get("msgRactLeadImp"), 11));
        rtnMap.put("value", makeStyleValue((String)resultMap.get("LSTRACTDMDMXLEADIMPRAT3")));
        rtnList.add(rtnMap);

        ///////
        rtnMap = new HashMap<String, Object>();
        rtnMap.put("leftFstTitle", makeStyleMsg(msgMap.get("msgMaxDemandTime"), 4));
        rtnMap.put("leftSndTitle", makeStyleMsg(msgMap.get("msgRate3"), 0));
        rtnMap.put("leftTrdTitle", makeStyleTrdMsg("1"));
        rtnMap.put("topTitle", makeStyleMsg(msgMap.get("msgRactLagExp"), 12));
        rtnMap.put("value", makeStyleValue((String)resultMap.get("LSTRACTDMDMXLAGEXPRAT3")));
        rtnList.add(rtnMap);

        ///////
        rtnMap = new HashMap<String, Object>();
        rtnMap.put("leftFstTitle", makeStyleMsg(msgMap.get("msgMaxDemandTime"), 4));
        rtnMap.put("leftSndTitle", makeStyleMsg(msgMap.get("msgRate3"), 0));
        rtnMap.put("leftTrdTitle", makeStyleTrdMsg("1"));
        rtnMap.put("topTitle", makeStyleMsg(msgMap.get("msgRactLeadExp"), 13));
        rtnMap.put("value", makeStyleValue((String)resultMap.get("LSTRACTDMDMXLEADEXPRAT3")));
        rtnList.add(rtnMap);
        
        ///////
        rtnMap = new HashMap<String, Object>();
        rtnMap.put("leftFstTitle", makeStyleMsg(msgMap.get("msgMaxDemandTime"), 4));
        rtnMap.put("leftSndTitle", makeStyleMsg(msgMap.get("msgRate3"), 0));
        rtnMap.put("leftTrdTitle", makeStyleTrdMsg("1"));
        rtnMap.put("topTitle", makeStyleMsg(msgMap.get("msgkVah1"), 14));
        rtnMap.put("value", makeStyleValue((String)resultMap.get("LSTMAXDMDKVAH1RATE3")));
        rtnList.add(rtnMap);
        // ----------------------------------------------------------------

        ///////
        rtnMap = new HashMap<String, Object>();
        rtnMap.put("leftFstTitle", makeStyleMsg(msgMap.get("msgTotCummDemand"), 5));
        rtnMap.put("leftSndTitle", "");
        rtnMap.put("leftTrdTitle", "");
        rtnMap.put("topTitle", makeStyleMsg(msgMap.get("msgActImp"), 8));
        rtnMap.put("value", makeStyleValue((String)resultMap.get("LSTCUMACTDMDMXIMPTOT")));
        rtnList.add(rtnMap);

        ///////
        rtnMap = new HashMap<String, Object>();
        rtnMap.put("leftFstTitle", makeStyleMsg(msgMap.get("msgTotCummDemand"), 5));
        rtnMap.put("leftSndTitle", "");
        rtnMap.put("leftTrdTitle", "");
        rtnMap.put("topTitle", makeStyleMsg(msgMap.get("msgActExp"), 9));
        rtnMap.put("value", makeStyleValue((String)resultMap.get("LSTCUMACTDMDMXEXPTOT")));
        rtnList.add(rtnMap);

        ///////
        rtnMap = new HashMap<String, Object>();
        rtnMap.put("leftFstTitle", makeStyleMsg(msgMap.get("msgTotCummDemand"), 5));
        rtnMap.put("leftSndTitle", "");
        rtnMap.put("leftTrdTitle", "");
        rtnMap.put("topTitle", makeStyleMsg(msgMap.get("msgRactLagImp"), 10));
        rtnMap.put("value", makeStyleValue((String)resultMap.get("LSTCUMRACTDMDMXLAGIMPTOT")));
        rtnList.add(rtnMap);

        ///////
        rtnMap = new HashMap<String, Object>();
        rtnMap.put("leftFstTitle", makeStyleMsg(msgMap.get("msgTotCummDemand"), 5));
        rtnMap.put("leftSndTitle", "");
        rtnMap.put("leftTrdTitle", "");
        rtnMap.put("topTitle", makeStyleMsg(msgMap.get("msgRactLeadImp"), 11));
        rtnMap.put("value", makeStyleValue((String)resultMap.get("LSTCUMRACTDMDMXLEADIMPTOT")));
        rtnList.add(rtnMap);

        ///////
        rtnMap = new HashMap<String, Object>();
        rtnMap.put("leftFstTitle", makeStyleMsg(msgMap.get("msgTotCummDemand"), 5));
        rtnMap.put("leftSndTitle", "");
        rtnMap.put("leftTrdTitle", "");
        rtnMap.put("topTitle", makeStyleMsg(msgMap.get("msgRactLagExp"), 12));
        rtnMap.put("value", makeStyleValue((String)resultMap.get("LSTCUMRACTDMDMXLAGEXPTOT")));
        rtnList.add(rtnMap);

        ///////
        rtnMap = new HashMap<String, Object>();
        rtnMap.put("leftFstTitle", makeStyleMsg(msgMap.get("msgTotCummDemand"), 5));
        rtnMap.put("leftSndTitle", "");
        rtnMap.put("leftTrdTitle", "");
        rtnMap.put("topTitle", makeStyleMsg(msgMap.get("msgRactLeadExp"), 13));
        rtnMap.put("value", makeStyleValue((String)resultMap.get("LSTCUMRACTDMDMXLEADEXPTOT")));
        rtnList.add(rtnMap);
        
        ///////
        rtnMap = new HashMap<String, Object>();
        rtnMap.put("leftFstTitle", makeStyleMsg(msgMap.get("msgTotCummDemand"), 5));
        rtnMap.put("leftSndTitle", "");
        rtnMap.put("leftTrdTitle", "");
        rtnMap.put("topTitle", makeStyleMsg(msgMap.get("msgkVah1"), 14));
        rtnMap.put("value", makeStyleValue((String)resultMap.get("LSTCUMMKVAH1RATETOTAL")));
        rtnList.add(rtnMap);
        // ----------------------------------------------------------------

        ///////
        rtnMap = new HashMap<String, Object>();
        rtnMap.put("leftFstTitle", makeStyleMsg(msgMap.get("msgCummDemand"), 6));
        rtnMap.put("leftSndTitle", makeStyleMsg(msgMap.get("msgRate1"), 0));
        rtnMap.put("leftTrdTitle", "");
        rtnMap.put("topTitle", makeStyleMsg(msgMap.get("msgActImp"), 8));
        rtnMap.put("value", makeStyleValue((String)resultMap.get("LSTCUMACTDMDMXIMPRAT1")));
        rtnList.add(rtnMap);

        ///////
        rtnMap = new HashMap<String, Object>();
        rtnMap.put("leftFstTitle", makeStyleMsg(msgMap.get("msgCummDemand"), 6));
        rtnMap.put("leftSndTitle", makeStyleMsg(msgMap.get("msgRate1"), 0));
        rtnMap.put("leftTrdTitle", "");
        rtnMap.put("topTitle", makeStyleMsg(msgMap.get("msgActExp"), 9));
        rtnMap.put("value", makeStyleValue((String)resultMap.get("LSTCUMACTDMDMXEXPRAT1")));
        rtnList.add(rtnMap);

        ///////
        rtnMap = new HashMap<String, Object>();
        rtnMap.put("leftFstTitle", makeStyleMsg(msgMap.get("msgCummDemand"), 6));
        rtnMap.put("leftSndTitle", makeStyleMsg(msgMap.get("msgRate1"), 0));
        rtnMap.put("leftTrdTitle", "");
        rtnMap.put("topTitle", makeStyleMsg(msgMap.get("msgRactLagImp"), 10));
        rtnMap.put("value", makeStyleValue((String)resultMap.get("LSTCUMRACTDMDMXLAGIMPRAT1")));
        rtnList.add(rtnMap);

        ///////
        rtnMap = new HashMap<String, Object>();
        rtnMap.put("leftFstTitle", makeStyleMsg(msgMap.get("msgCummDemand"), 6));
        rtnMap.put("leftSndTitle", makeStyleMsg(msgMap.get("msgRate1"), 0));
        rtnMap.put("leftTrdTitle", "");
        rtnMap.put("topTitle", makeStyleMsg(msgMap.get("msgRactLeadImp"), 11));
        rtnMap.put("value", makeStyleValue((String)resultMap.get("LSTCUMRACTDMDMXLEADIMPRAT1")));
        rtnList.add(rtnMap);

        ///////
        rtnMap = new HashMap<String, Object>();
        rtnMap.put("leftFstTitle", makeStyleMsg(msgMap.get("msgCummDemand"), 6));
        rtnMap.put("leftSndTitle", makeStyleMsg(msgMap.get("msgRate1"), 0));
        rtnMap.put("leftTrdTitle", "");
        rtnMap.put("topTitle", makeStyleMsg(msgMap.get("msgRactLagExp"), 12));
        rtnMap.put("value", makeStyleValue((String)resultMap.get("LSTCUMRACTDMDMXLAGEXPRAT1")));
        rtnList.add(rtnMap);

        ///////
        rtnMap = new HashMap<String, Object>();
        rtnMap.put("leftFstTitle", makeStyleMsg(msgMap.get("msgCummDemand"), 6));
        rtnMap.put("leftSndTitle", makeStyleMsg(msgMap.get("msgRate1"), 0));
        rtnMap.put("leftTrdTitle", "");
        rtnMap.put("topTitle", makeStyleMsg(msgMap.get("msgRactLeadExp"), 13));
        rtnMap.put("value", makeStyleValue((String)resultMap.get("LSTCUMRACTDMDMXLEADEXPRAT1")));
        rtnList.add(rtnMap);
        
        ///////
        rtnMap = new HashMap<String, Object>();
        rtnMap.put("leftFstTitle", makeStyleMsg(msgMap.get("msgCummDemand"), 6));
        rtnMap.put("leftSndTitle", makeStyleMsg(msgMap.get("msgRate1"), 0));
        rtnMap.put("leftTrdTitle", "");
        rtnMap.put("topTitle", makeStyleMsg(msgMap.get("msgkVah1"), 14));
        rtnMap.put("value", makeStyleValue((String)resultMap.get("LSTCUMMKVAH1RATE1")));
        rtnList.add(rtnMap);
        // ----------------------------------------------------------------

        ///////
        rtnMap = new HashMap<String, Object>();
        rtnMap.put("leftFstTitle", makeStyleMsg(msgMap.get("msgCummDemand"), 6));
        rtnMap.put("leftSndTitle", makeStyleMsg(msgMap.get("msgRate2"), 0));
        rtnMap.put("leftTrdTitle", "");
        rtnMap.put("topTitle", makeStyleMsg(msgMap.get("msgActImp"), 8));
        rtnMap.put("value", makeStyleValue((String)resultMap.get("LSTCUMACTDMDMXIMPRAT2")));
        rtnList.add(rtnMap);

        ///////
        rtnMap = new HashMap<String, Object>();
        rtnMap.put("leftFstTitle", makeStyleMsg(msgMap.get("msgCummDemand"), 6));
        rtnMap.put("leftSndTitle", makeStyleMsg(msgMap.get("msgRate2"), 0));
        rtnMap.put("leftTrdTitle", "");
        rtnMap.put("topTitle", makeStyleMsg(msgMap.get("msgActExp"), 9));
        rtnMap.put("value", makeStyleValue((String)resultMap.get("LSTCUMACTDMDMXEXPRAT2")));
        rtnList.add(rtnMap);

        ///////
        rtnMap = new HashMap<String, Object>();
        rtnMap.put("leftFstTitle", makeStyleMsg(msgMap.get("msgCummDemand"), 6));
        rtnMap.put("leftSndTitle", makeStyleMsg(msgMap.get("msgRate2"), 0));
        rtnMap.put("leftTrdTitle", "");
        rtnMap.put("topTitle", makeStyleMsg(msgMap.get("msgRactLagImp"), 10));
        rtnMap.put("value", makeStyleValue((String)resultMap.get("LSTCUMRACTDMDMXLAGIMPRAT2")));
        rtnList.add(rtnMap);

        ///////
        rtnMap = new HashMap<String, Object>();
        rtnMap.put("leftFstTitle", makeStyleMsg(msgMap.get("msgCummDemand"), 6));
        rtnMap.put("leftSndTitle", makeStyleMsg(msgMap.get("msgRate2"), 0));
        rtnMap.put("leftTrdTitle", "");
        rtnMap.put("topTitle", makeStyleMsg(msgMap.get("msgRactLeadImp"), 11));
        rtnMap.put("value", makeStyleValue((String)resultMap.get("LSTCUMRACTDMDMXLEADIMPRAT2")));
        rtnList.add(rtnMap);

        ///////
        rtnMap = new HashMap<String, Object>();
        rtnMap.put("leftFstTitle", makeStyleMsg(msgMap.get("msgCummDemand"), 6));
        rtnMap.put("leftSndTitle", makeStyleMsg(msgMap.get("msgRate2"), 0));
        rtnMap.put("leftTrdTitle", "");
        rtnMap.put("topTitle", makeStyleMsg(msgMap.get("msgRactLagExp"), 12));
        rtnMap.put("value", makeStyleValue((String)resultMap.get("LSTCUMRACTDMDMXLAGEXPRAT2")));
        rtnList.add(rtnMap);

        ///////
        rtnMap = new HashMap<String, Object>();
        rtnMap.put("leftFstTitle", makeStyleMsg(msgMap.get("msgCummDemand"), 6));
        rtnMap.put("leftSndTitle", makeStyleMsg(msgMap.get("msgRate2"), 0));
        rtnMap.put("leftTrdTitle", "");
        rtnMap.put("topTitle", makeStyleMsg(msgMap.get("msgRactLeadExp"), 13));
        rtnMap.put("value", makeStyleValue((String)resultMap.get("LSTCUMRACTDMDMXLEADEXPRAT2")));
        rtnList.add(rtnMap);
        
        ///////
        rtnMap = new HashMap<String, Object>();
        rtnMap.put("leftFstTitle", makeStyleMsg(msgMap.get("msgCummDemand"), 6));
        rtnMap.put("leftSndTitle", makeStyleMsg(msgMap.get("msgRate2"), 0));
        rtnMap.put("leftTrdTitle", "");
        rtnMap.put("topTitle", makeStyleMsg(msgMap.get("msgkVah1"), 14));
        rtnMap.put("value", makeStyleValue((String)resultMap.get("LSTCUMMKVAH1RATE2")));
        rtnList.add(rtnMap);
        // ----------------------------------------------------------------

        ///////
        rtnMap = new HashMap<String, Object>();
        rtnMap.put("leftFstTitle", makeStyleMsg(msgMap.get("msgCummDemand"), 6));
        rtnMap.put("leftSndTitle", makeStyleMsg(msgMap.get("msgRate3"), 0));
        rtnMap.put("leftTrdTitle", "");
        rtnMap.put("topTitle", makeStyleMsg(msgMap.get("msgActImp"), 8));
        rtnMap.put("value", makeStyleValue((String)resultMap.get("LSTCUMACTDMDMXIMPRAT3")));
        rtnList.add(rtnMap);

        ///////
        rtnMap = new HashMap<String, Object>();
        rtnMap.put("leftFstTitle", makeStyleMsg(msgMap.get("msgCummDemand"), 6));
        rtnMap.put("leftSndTitle", makeStyleMsg(msgMap.get("msgRate3"), 0));
        rtnMap.put("leftTrdTitle", "");
        rtnMap.put("topTitle", makeStyleMsg(msgMap.get("msgActExp"), 9));
        rtnMap.put("value", makeStyleValue((String)resultMap.get("LSTCUMACTDMDMXEXPRAT3")));
        rtnList.add(rtnMap);

        ///////
        rtnMap = new HashMap<String, Object>();
        rtnMap.put("leftFstTitle", makeStyleMsg(msgMap.get("msgCummDemand"), 6));
        rtnMap.put("leftSndTitle", makeStyleMsg(msgMap.get("msgRate3"), 0));
        rtnMap.put("leftTrdTitle", "");
        rtnMap.put("topTitle", makeStyleMsg(msgMap.get("msgRactLagImp"), 10));
        rtnMap.put("value", makeStyleValue((String)resultMap.get("LSTCUMRACTDMDMXLAGIMPRAT3")));
        rtnList.add(rtnMap);

        ///////
        rtnMap = new HashMap<String, Object>();
        rtnMap.put("leftFstTitle", makeStyleMsg(msgMap.get("msgCummDemand"), 6));
        rtnMap.put("leftSndTitle", makeStyleMsg(msgMap.get("msgRate3"), 0));
        rtnMap.put("leftTrdTitle", "");
        rtnMap.put("topTitle", makeStyleMsg(msgMap.get("msgRactLeadImp"), 11));
        rtnMap.put("value", makeStyleValue((String)resultMap.get("LSTCUMRACTDMDMXLEADIMPRAT3")));
        rtnList.add(rtnMap);

        ///////
        rtnMap = new HashMap<String, Object>();
        rtnMap.put("leftFstTitle", makeStyleMsg(msgMap.get("msgCummDemand"), 6));
        rtnMap.put("leftSndTitle", makeStyleMsg(msgMap.get("msgRate3"), 0));
        rtnMap.put("leftTrdTitle", "");
        rtnMap.put("topTitle", makeStyleMsg(msgMap.get("msgRactLagExp"), 12));
        rtnMap.put("value", makeStyleValue((String)resultMap.get("LSTCUMRACTDMDMXLAGEXPRAT3")));
        rtnList.add(rtnMap);

        ///////
        rtnMap = new HashMap<String, Object>();
        rtnMap.put("leftFstTitle", makeStyleMsg(msgMap.get("msgCummDemand"), 6));
        rtnMap.put("leftSndTitle", makeStyleMsg(msgMap.get("msgRate3"), 0));
        rtnMap.put("leftTrdTitle", "");
        rtnMap.put("topTitle", makeStyleMsg(msgMap.get("msgRactLeadExp"), 13));
        rtnMap.put("value", makeStyleValue((String)resultMap.get("LSTCUMRACTDMDMXLEADEXPRAT3")));
        rtnList.add(rtnMap);
        
        ///////
        rtnMap = new HashMap<String, Object>();
        rtnMap.put("leftFstTitle", makeStyleMsg(msgMap.get("msgCummDemand"), 6));
        rtnMap.put("leftSndTitle", makeStyleMsg(msgMap.get("msgRate3"), 0));
        rtnMap.put("leftTrdTitle", "");
        rtnMap.put("topTitle", makeStyleMsg(msgMap.get("msgkVah1"), 14));
        rtnMap.put("value", makeStyleValue((String)resultMap.get("LSTCUMMKVAH1RATE3")));
        rtnList.add(rtnMap);
        // ----------------------------------------------------------------

        return rtnList;
    }

    /**
     * method name : makeStyleMsg<b/>
     * method Desc : PivotGrid Header 순서 정렬
     *
     * @param msg
     * @param order
     * @return
     */
    private String makeStyleMsg(String msg, int order) {
        StringBuilder sb = new StringBuilder();
        
        if (order != 0) {
            sb.append("<span style='display:none;'>");
            sb.append(StringUtil.frontAppendNStr('0', new Integer(order).toString(), 2));
            sb.append("</span>");
        }
        sb.append("<label style='font-weight:bold;'>");
        sb.append(msg);
        sb.append("</label>");
        
        return sb.toString();
    }

    /**
     * method name : makeStyleTrdMsg<b/>
     * method Desc : PivotGrid 데이터 두줄로 표현하기위해 hidden header 생성
     *
     * @param msg
     * @return
     */
    private String makeStyleTrdMsg(String msg) {
            StringBuilder sb = new StringBuilder();
            sb.append("<font style='display:none;'>");
            sb.append(msg);
            sb.append("</font>");
            return sb.toString();
    }

    /**
     * method name : makeStyleValue<b/>
     * method Desc : PivotGrid 데이터 오른쪽정렬
     *
     * @param msg
     * @return
     */
    private String makeStyleValue(String msg) {
        if (!StringUtil.nullToBlank(msg).isEmpty()) {
            StringBuilder sb = new StringBuilder();
            sb.append("<div style='width:100%; text-align:right;'>");
            sb.append(msg);
            sb.append("</div>");
            return sb.toString();
        } else {
            return "&nbsp;";
        }
    }
}
