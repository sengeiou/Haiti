package com.aimir.service.mvm.impl;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javassist.compiler.ast.CondExpr;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aimir.constants.CommonConstants;
import com.aimir.constants.CommonConstants.PeakType;
import com.aimir.dao.mvm.DayEMDao;
import com.aimir.dao.mvm.MonthEMDao;
import com.aimir.dao.system.LocationDao;
import com.aimir.dao.system.SupplierDao;
import com.aimir.dao.system.TOURateDao;
import com.aimir.model.system.Location;
import com.aimir.model.system.Supplier;
import com.aimir.service.mvm.DemandManagementManager;
import com.aimir.util.Condition;
import com.aimir.util.DecimalUtil;
import com.aimir.util.StringUtil;
import com.aimir.util.TimeLocaleUtil;

@Service(value="demandManagementManager")
public class DemandManagementManagerImpl implements DemandManagementManager{

    Log logger = LogFactory.getLog(DemandManagementManagerImpl.class);

    @Autowired
	DayEMDao dayEMDao;
    @Autowired
	MonthEMDao monthEMDao;
	@Autowired
	LocationDao locationDao;
    @Autowired
	TOURateDao touRateDao;
    @Autowired
    SupplierDao supplierDao;

	/**
	 * 수요관리 Mini Gadget 
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Object> getDemandManagement(Map<String, Object> condition) {

		String strSupplierId = (String)condition.get("supplierId");
		Integer supplierId = 0;
		if(!"".equals(StringUtil.nullToBlank(strSupplierId))){
			supplierId = Integer.parseInt(strSupplierId);
		}
		condition.put("supplierId", supplierId);

		String strTariffType = (String)condition.get("tariffType");
		Integer tariffType = 0;
		if(!"".equals(StringUtil.nullToBlank(strTariffType)) && !"0".equals(StringUtil.nullToBlank(strTariffType))){
			tariffType = Integer.parseInt(strTariffType);
		}
		condition.put("tariffType", tariffType);
		
		String strLocationId = (String)condition.get("locationId");
		Integer locationId = 0;
		if(!"".equals(StringUtil.nullToBlank(strLocationId)) && !"0".equals(StringUtil.nullToBlank(strLocationId))){
			locationId = Integer.parseInt(strLocationId);
		}
		
		List<Location> locations = null;
		if(locationId > 0){
			// 선택된 지역의 하위지역 조회
			locations = locationDao.getChildren(locationId, supplierId);
			if(locations.size() == 0){
				locations.add(locationDao.get(locationId));
			}
		}
		else{ 
			// 최상위 지역 조회
			locations = locationDao.getParents(supplierId);
		}

		Map<String, Object> peakData = null;
		List<Object> peakList = new ArrayList<Object>();
		Double maxVal = 0.0;		
		
		for(Location loc:locations){
			Double avgVal = 0.0;
			Double locationMaxVal = 0.0;
			Double loadFactor = 0.0;
			int count = 0;
			
			condition.put("locationId", loc.getId());
			peakData = getDemandManagementByLocation(condition, loc.getChildren());

			if(peakData == null){
				peakData = new HashMap<String, Object>();
			}
			else{
				if(peakData.get("offPeak") != null) {
					avgVal += (Double)peakData.get("offPeak");
					if((Double)peakData.get("offPeak")>0) count++;
					if(Double.compare((Double)peakData.get("offPeak"), locationMaxVal) > 0){
						locationMaxVal = (Double)peakData.get("offPeak");						
					}
					if(Double.compare((Double)peakData.get("offPeak"), maxVal) > 0){
						maxVal = (Double)peakData.get("offPeak");
					}
				}
				if(peakData.get("peak") != null) {
					avgVal += (Double)peakData.get("peak");
					if((Double)peakData.get("peak") > 0 ) count++;
					if(Double.compare((Double)peakData.get("peak"), locationMaxVal) > 0){
						locationMaxVal = (Double)peakData.get("peak");						
					}
					if(Double.compare((Double)peakData.get("peak"), maxVal) > 0){
						maxVal = (Double)peakData.get("peak");
					}
				}
				if(peakData.get("criticalPeak") != null) {
					avgVal += (Double)peakData.get("criticalPeak");
					if((Double)peakData.get("criticalPeak") > 0 ) count++;
					if(Double.compare((Double)peakData.get("criticalPeak"), locationMaxVal) > 0){
						locationMaxVal = (Double)peakData.get("criticalPeak");			
					}
					if(Double.compare((Double)peakData.get("criticalPeak"), maxVal) > 0){
						maxVal = (Double)peakData.get("criticalPeak");
					}
				}
			}
			
			avgVal = avgVal > 0 ? avgVal / count : 0;
			loadFactor = avgVal > 0 ? (avgVal / locationMaxVal) * 100 : 0;

			peakData.put("location", loc.getName());
			peakData.put("loadFactor", loadFactor);
			peakList.add(peakData);
		}
		
		// 리턴 데이터
		Map<String, Object> returnData = new HashMap<String, Object>();
		returnData.put("chart", peakList);
		returnData.put("maxVal", Math.ceil(maxVal));

		return returnData;
	}

	@SuppressWarnings("unchecked")
	public Map<String, Object> getDemandManagementByLocation(Map<String, Object> condition, Set<Location> locations) {

		Map<String, Object> peakData = null;

		// 하위지역이 존재할경우 재귀호출하여 조회하고
		// 하위지역이 존재하지 않을경우 지역별 검침 데이터를 생성한다.
		if(locations == null || locations.size() < 1){

			List<Object> grid = dayEMDao.getDemandManagement(condition, "");

			// 지역별 검침값 평균 구하기
			Double offPeak = 0.0;
			Double peak = 0.0;
			Double criticalPeak = 0.0;
			int count = 0;
			
			if(grid != null && grid.size() > 0) {
				
				// 계약 종별 Peak 시간대 구하기
				Map<Integer, Object> times = touRateDao.getPeakTimeZone(condition);
				List<Object> time = null;
	
				List<Object> result = new ArrayList<Object>();
				Integer tariffIndex = null;
				
				Map<String, Object> row = null;
				for(Object obj:grid){
					row = (Map<String, Object>)obj;
	
					tariffIndex = (Integer)row.get("tariffIndex");
					time = (List<Object>)times.get(tariffIndex);
					// 계약 종별에 따라 지역별 Max 값 산출. 
					result.add(getPeakDataSet(row, time));
				}
				
				for(Object obj:result){
					row = (Map<String, Object>)obj;

					// 지역별 검침값 평균 구하기
					if(row.get("offPeak") != null) 		offPeak      += (Double)row.get("offPeak");
					if(row.get("peak") != null) 		peak         += (Double)row.get("peak");
					if(row.get("criticalPeak") != null) criticalPeak += (Double)row.get("criticalPeak");
	
					count++;
				}
				
				// 마지막 지역 데이터가 for 문에서 누락되므로 여기서 put. 
				peakData = new HashMap<String, Object>();
				peakData.put("offPeak", offPeak/count);
				peakData.put("peak", peak/count);
				peakData.put("criticalPeak", criticalPeak/count);
			}
			else{
				peakData = new HashMap<String, Object>();
				peakData.put("offPeak", offPeak);
				peakData.put("peak", peak);
				peakData.put("criticalPeak", criticalPeak);
			}
		}
		else{

			Double offPeak = 0.0;
			Double peak = 0.0;
			Double criticalPeak = 0.0;
			
			for(Location loc:locations){
				// 재귀호출
				condition.put("locationId", loc.getId());
				peakData = getDemandManagementByLocation(condition, loc.getChildren());
				
				if(peakData == null){
					peakData = new HashMap<String, Object>();
				}
				else{
					if(Double.compare((Double)peakData.get("offPeak"), offPeak) > 0){
						offPeak = (Double)peakData.get("offPeak");
					}
					if(Double.compare((Double)peakData.get("peak"), peak) > 0){
						peak = (Double)peakData.get("peak");
					}
					if(Double.compare((Double)peakData.get("criticalPeak"), criticalPeak) > 0){
						criticalPeak = (Double)peakData.get("criticalPeak");
					}
				}
			}

			peakData.put("location", "");
			peakData.put("offPeak", offPeak);
			peakData.put("peak", peak);
			peakData.put("criticalPeak", criticalPeak);
		}
		
		return peakData;	
	}
	
	/**
	 * 계약 종별에 따라 지역별 Max 값 산출. 
	 * 
	 * @param row   지역별 검침 데이터 리스트
	 * @param times 계약종별 Peak 시간대 데이터
	 * @return Map<String, Object>
	 */
	@SuppressWarnings("unchecked")
	private Map<String, Object> getPeakDataSet(Map<String, Object> row, List<Object> time) {
		
		Double offPeak = null;
		Double peak = null;
		Double criticalPeak = null;
		
		Map<String, Object> peakData = null;
		String peakType = null;

		if(time != null){
			// 각 Peak 별 Max 값 구하기
			for(int i=0; i<time.size(); i++){
				peakData = (Map<String, Object>)time.get(i);
				peakType = ((PeakType)peakData.get("peakType")).name();
				
				if(peakType.equals(CommonConstants.PeakType.OFF_PEAK.name())){
					offPeak = getPeakData(row, peakData);
				}
				else if(peakType.equals(CommonConstants.PeakType.PEAK.name())){
					peak = getPeakData(row, peakData);
				}
				else if(peakType.equals(CommonConstants.PeakType.CRITICAL_PEAK.name())){
					criticalPeak = getPeakData(row, peakData);
				}
			}
		}

		Map<String, Object> result = new HashMap<String, Object>();
		result.put("location", (String)row.get("location"));
		result.put("parent", (Integer)row.get("parent"));
		result.put("tariffType", (Integer)row.get("tariffIndex"));
		result.put("tariffName", (String)row.get("tariffName"));
		result.put("offPeak", offPeak);
		result.put("peak", peak);
		result.put("criticalPeak", criticalPeak);

		return result;
	}
	
	/**
	 * Peak 시간대에 따라 지역별 최대 검침값을 리턴한다.
	 * 
	 * @param row 지역별 검침 데이터
	 * @param time Peak 시간대
	 * @return Double 지역별 최대 검침값
	 */
	private Double getPeakData(Map<String, Object> row, Map<String, Object> time){

		Object[] keys = row.keySet().toArray();
		
		Double maxVal = 0.0;
		Double thisVal = 0.0;

		int startTime = Integer.parseInt((String)time.get("startTime"));
		int endTime   = Integer.parseInt((String)time.get("endTime"));
		int hh = 0;
		
		// 지역별 최대 검침값 구하기
		for(int i=0; i<keys.length; i++){
			
			// "max_" 컬럼 데이터만 해당.
			if(keys[i].toString().startsWith("max_")){
				hh = Integer.parseInt(keys[i].toString().substring(4)); //00 ~ 23
				
				// 시간대에 포함될 경우 그중 최대값을 구한다.
				if(startTime <= hh || hh < endTime){
					thisVal = (Double)row.get(keys[i].toString());
					if(thisVal != null && thisVal.compareTo(maxVal) > 0){
						maxVal = thisVal;
					}
				}
			}
		}

		return maxVal;
	}
	
	@SuppressWarnings("unchecked")
	private Long getLoadFactor(List<Object> grid) {

		Object[] keys = ((Map<String, Object>)grid.get(0)).keySet().toArray();
		
		Map<String, Object> data = new HashMap<String, Object>();
		String location = null;
		Long loadFactor = null;
		
		Double thisVal = 0.0;
		Double maxVal = 0.0;
		Double avgVal = 0.0;
		Double total = 0.0;

		Map<String, Object> row = null;
		for(Object obj:grid){
			row = (Map<String, Object>)obj;
			
			// 지역별 최대 검침값, 평균 검침 구하기
			for(int i=0; i<keys.length; i++){
				
				// "max_" 컬럼 데이터만 해당.
				if(keys[i].toString().startsWith("max_")){
					thisVal = (Double)row.get(keys[i].toString());
					if(thisVal != null && thisVal.compareTo(maxVal) > 0){
						maxVal = thisVal;
					}
				}
				// "avg_" 컬럼 데이터만 해당.
				if(keys[i].toString().startsWith("avg_")){
					thisVal = (Double)row.get(keys[i].toString());
					if(thisVal != null){
						total += thisVal;
					}
				}
			}
			
			avgVal = total / 24 ;
			location = (String)row.get("location");
			loadFactor = Math.round((avgVal/maxVal) * 100);
			data.put(location, loadFactor);
			
			maxVal = 0.0;
			avgVal = 0.0;
			total = 0.0;
		}
		
		return loadFactor;
	}

	
	/**
	 * 수요관리 Max Gadget 
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Map<String, Object> getDemandManagementList(Map<String, Object> condition) {
			
		String strSupplierId = (String)condition.get("supplierId");
		Integer supplierId = 0;
		if(!"".equals(StringUtil.nullToBlank(strSupplierId))){
			supplierId = Integer.parseInt(strSupplierId);
		}
		condition.put("supplierId", supplierId);
		
		String strTariffType = (String)condition.get("tariffType");
		Integer tariffType = 0;
		if(!"".equals(StringUtil.nullToBlank(strTariffType)) && !"0".equals(StringUtil.nullToBlank(strTariffType))){
			tariffType = Integer.parseInt(strTariffType);
		}
		condition.put("tariffType", tariffType);
		
		String strLocationId = (String)condition.get("locationId");
		Integer locationId = 0;
		if(!"".equals(StringUtil.nullToBlank(strLocationId)) && !"0".equals(StringUtil.nullToBlank(strLocationId))){
			locationId = Integer.parseInt(strLocationId);
		}
		condition.put("locationId", locationId);
		List<Integer> locations = locationDao.getLeafLocationId(locationId, supplierId);
		condition.put("locations", locations);

		String searchType = (String)condition.get("searchType");

		List<Object> grid = dayEMDao.getDemandManagementList(condition);
		Integer totalCnt = null;
		if( "search".equals(searchType) ){
			totalCnt = (Integer)grid.get(grid.size()-1);
			grid.remove(grid.size()-1);
		}

		List<Object> result = new ArrayList<Object>();
		
		if(grid != null && grid.size() > 0) {
			
			// 계약 종별 Peak 시간대 구하기
			Map<Integer, Object> times = touRateDao.getPeakTimeZone(condition);
			List<Object> time = null;

			Integer tariffIndex = null;
			
			Map<String, Object> row = null;
			for(Object obj:grid){
				row = (Map<String, Object>)obj;

				tariffIndex = (Integer)row.get("tariffIndex");
				time = (List<Object>)times.get(tariffIndex);
				// 계약 종별에 따라 지역별 Max 값 산출. 
				result.add(getPeakDataSet(row, time));
			}

			// 그리드 데이터 셋팅
			Double thisVal = 0.0;
			Double maxVal = 0.0;
			Double avgVal = 0.0;
			Double loadFactor = null;
			int count = 0;
			int locId = 0;
			List<Integer> locIds = null;
			
			for(Object obj:result){
				row = (Map<String, Object>)obj;
				
				if (row.get("parent") == null) continue;
				
				locId = (Integer)row.get("parent");
				if(locId > 0){
					if(locationId != locId){
						locIds = locationDao.getParentId(locId);
						if(locIds.get(0) != null){
							row.put("location", locationDao.get(locIds.get(0)).getName());
						}
						else{
							row.put("location", locationDao.get(locId).getName());
						}
					}
					else{
						row.put("location", locationDao.get(locId).getName());
					}
				}
				if(row.get("offPeak") != null){
					thisVal = (Double)row.get("offPeak");
					if(thisVal != null){
						if(thisVal.compareTo(maxVal) > 0){
							maxVal = (Double)row.get("offPeak");
						}
						avgVal += (Double)row.get("offPeak");
						count++;
					}
				}
				if(row.get("peak") != null){
					thisVal = (Double)row.get("peak");
					if(thisVal != null){
						if(thisVal.compareTo(maxVal) > 0){
							maxVal = (Double)row.get("peak");
						}
						avgVal += (Double)row.get("peak");
						count++;
					}
				}
				if(row.get("criticalPeak") != null){
					thisVal = (Double)row.get("criticalPeak");
					if(thisVal != null){
						if(thisVal.compareTo(maxVal) > 0){
							maxVal = (Double)row.get("criticalPeak");
						}
						avgVal += (Double)row.get("criticalPeak");
						count++;
					}
				}
				
				DecimalFormat df = new DecimalFormat("###.##");
				avgVal = avgVal > 0 ? avgVal / count : 0;
				loadFactor = avgVal > 0 ? (avgVal / maxVal) * 100 : 0;
				
				row.put("avgPeak", avgVal);
				row.put("loadFactor", df.format(loadFactor));

				maxVal = 0.0;
				avgVal = 0.0;
				count = 0;
			}
		}

		// 차트 데이터 조회
		condition.put("supplierId", supplierId.toString());
		condition.put("tariffType", tariffType.toString());
		condition.put("locationId", strLocationId);
		condition.put("locations", new ArrayList<Location>());
		Map<String, Object> returnData = getDemandManagement(condition);
		
		// 리턴 데이터
		if( "search".equals(searchType) ){
			returnData.put("total", totalCnt);
		}
		
		DecimalFormat df = new DecimalFormat("########.###");
		
		int idx=1;
		for(Object obj: result) {
    		Map<String, Object> data = (HashMap)obj;
    		
    		//index
    		data.put("idx", idx);
    		
    		Object offPeak 		= data.get("offPeak");
    		Object peak 		= data.get("peak");
    		Object criticalPeak = data.get("criticalPeak");
    		Object avgPeak 		= data.get("avgPeak");
    		
    		if ( StringUtil.nullToBlank(offPeak).length() > 0)
    			data.put("offPeak", df.format(Double.parseDouble(offPeak.toString())));
    		
    		if ( StringUtil.nullToBlank(peak).length() > 0 )
    			data.put("peak", df.format(Double.parseDouble(peak.toString())));

    		if ( StringUtil.nullToBlank(criticalPeak).length() > 0 )
    			data.put("criticalPeak", df.format(Double.parseDouble(criticalPeak.toString())));
    		
    		if ( StringUtil.nullToBlank(avgPeak).length() > 0 )
    			data.put("avgPeak", df.format(Double.parseDouble(avgPeak.toString())));
    		
    		
    		idx++;
    	}

    	returnData.put("grid", result);
		
		return returnData;
	}
}
