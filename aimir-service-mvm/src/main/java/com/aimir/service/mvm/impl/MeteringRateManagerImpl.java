package com.aimir.service.mvm.impl;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aimir.constants.CommonConstants.MeterType;
import com.aimir.dao.device.MeterDao;
import com.aimir.dao.mvm.DayEMDao;
import com.aimir.dao.mvm.DayGMDao;
import com.aimir.dao.mvm.DayHMDao;
import com.aimir.dao.mvm.DayWMDao;
import com.aimir.dao.mvm.MeteringDataDao;
import com.aimir.dao.mvm.MeteringDayDao;
import com.aimir.dao.system.LocationDao;
import com.aimir.model.system.Location;
import com.aimir.service.mvm.MeteringRateManager;
import com.aimir.service.mvm.bean.MeteringFailureData;
import com.aimir.service.mvm.bean.MeteringRateData;
import com.aimir.util.DecimalUtil;
import com.aimir.util.StringUtil;
import com.aimir.util.TimeUtil;

@Service(value = "meteringRateManager")
public class MeteringRateManagerImpl implements MeteringRateManager {

    @Autowired
    LocationDao locationDao;

    @Autowired
    MeteringDataDao meteringDataDao;

    @Autowired
    MeterDao meterDao;

    @Autowired
    DayEMDao dayEMDao;

    @Autowired
    DayGMDao dayGMDao;

    @Autowired
    DayWMDao dayWMDao;

    @Autowired
    DayHMDao dayHMDao;

    @Autowired
    MeteringDayDao meteringDayDao;

    Map<String, Object> totalData;
    Map<String, Object> successData;
    
    Log logger = LogFactory.getLog(MeteringRateManagerImpl.class);

	public List<MeteringRateData> getMeteringRateListByLocationWithChild(Map<String,Object> params) {

		String meterType = (String)params.get("meterType");
		String locationId = (String)params.get("locationId");
		String locationType = (String)params.get("locationType");
		String strSupplierId = (String)params.get("supplierId");
		Integer supplierId = null;
		if(!"".equals(StringUtil.nullToBlank(strSupplierId))){
			supplierId = Integer.parseInt(strSupplierId);
		}
		String startDate = (String)params.get("searchStartDate");
		String endDate = (String)params.get("searchEndDate");
		String currentDate = TimeUtil.getCurrentDateUsingFormat("yyyymmdd");
		if("".equals(startDate)){
			startDate = currentDate;
			params.put("searchStartDate", startDate);
		}
		if("".equals(endDate)){
			endDate = currentDate;
			params.put("searchEndDate", endDate);
		}
		
		List<MeteringRateData> resMeteringRateDataList=new ArrayList<MeteringRateData>();
		
		//조회객체
		MeteringRateData reqMeteringRateData = new MeteringRateData();
		//조회 날짜 타입별로 조회시작,종료일 설정
		reqMeteringRateData.setStartDate(startDate); 
		reqMeteringRateData.setEndDate(endDate);
		//전기,가스,수도,열량 미터구분
		reqMeteringRateData.setMeterType(meterType);
		reqMeteringRateData.setSupplierId(strSupplierId);
		
		//입력된 locationId를 부모로 가지는 location 목록조회
		
		List<Location> locations = null;
		// locationId가 입력되지 않았을경우 최상위 지역 목록조회
		if(locationId == null || "".equals(locationId.trim())){
			locations = locationDao.getParents(supplierId);
		}
		else{
			// locationType '1'일경우 상위 지역목록조회 
			// 그렇지 않으면 하위 지역목록을 조회한다.
			if(locationType != null && "1".equals(locationType.trim())){
				//입력된 locationId 의 부모의 부모 locationID를 조회후 하위 지역을 조회하면됨
				//부모가 없거나, 부모의 부모가 없을경우 최상위 Location 목록을 조회한다.
				List<Integer> parentLocation = null;
				Integer parentLocationId = null;
				
				parentLocation   = locationDao.getParentId(Integer.parseInt(locationId));
				parentLocationId = parentLocation == null || parentLocation.size() < 1 ? null : parentLocation.get(0);
				
				if(parentLocationId != null){
					parentLocation   = locationDao.getParentId(parentLocationId);
					parentLocationId = parentLocation == null || parentLocation.size() < 1 ? null : parentLocation.get(0);
					
					if(parentLocationId != null){
						locations = locationDao.getChildren(parentLocationId,supplierId);
					}
					else{
						locations = locationDao.getParents(supplierId);
					}
				}
				else{
					locations = locationDao.getParents(supplierId);
				}
			}
			else{
				locations = locationDao.getChildren(Integer.parseInt(locationId),supplierId);
				
				if(locations == null || locations.size() < 1){
					
					//하위지역이 없을경우 현재 레벨의 지역을 조회한다.
					List<Integer> parentLocation = null;
					Integer parentLocationId = null;
					
					parentLocation = locationDao.getParentId(Integer.parseInt(locationId));
					parentLocationId = parentLocation==null||parentLocation.size()<1?null:parentLocation.get(0);
					
					if(parentLocationId!=null){
						locations = locationDao.getChildren(parentLocationId,supplierId);
					}else{
						locations = locationDao.getParents(supplierId);
					}
				}
			}
		}
				
		// 지역별 유효한 미터갯수 조회
		totalData = meteringDataDao.getTotalCountByLocation(params);
		// 검침 성공한 미터갯수 조회
		//successData = meteringDataDao.getSuccessCountByLocation(params);
		
		int totalCnt = 0;
		int successCnt = 0;
		for(Location location:locations){
			reqMeteringRateData.setLocationId(location.getId().toString());
			reqMeteringRateData.setLocationName(location.getName());
			
			MeteringRateData resMeteringRateData = getMeteringRateListRecur(reqMeteringRateData, location.getChildren());
			
			totalCnt = totalCnt + Integer.parseInt(resMeteringRateData.getTotalCount());
			successCnt = successCnt +  Integer.parseInt(resMeteringRateData.getSuccessCount());
			
			resMeteringRateDataList.add(resMeteringRateData);
		}
		
		// Total 검침 성공율 계산
		BigDecimal meteringRate = null;
		if( totalCnt >= successCnt && totalCnt != 0){
			meteringRate = new BigDecimal(successCnt).divide(new BigDecimal(totalCnt),MathContext.DECIMAL128).multiply(new BigDecimal(100));
			meteringRate = meteringRate.setScale(2,BigDecimal.ROUND_DOWN);//소수점 2자리 이하 제거
		}else{
			meteringRate = new BigDecimal(0);
		}
		
		// Total 라벨생성
		String label = meteringRate+"% ("+ successCnt + "/" + totalCnt+")";
		
		MeteringRateData total = new MeteringRateData();
		total.setSuccess(meteringRate.toString());
		total.setLabel(label);
		total.setLocationName("Total");

		resMeteringRateDataList.add(total);
		return resMeteringRateDataList;
	}
	
	/**
	 * 지역별 검침 성공율 데이터를 재귀적으로 호출하여 조회한다.
	 * @param reqMeteringRateData
	 * @return MeteringRateData
	 */
	private MeteringRateData getMeteringRateListRecur(MeteringRateData reqMeteringRateData, Set<Location> locations) {

		//조회결과객체
		MeteringRateData meteringRateData = new MeteringRateData();
		meteringRateData.setLocationId(reqMeteringRateData.getLocationId());
		meteringRateData.setLocationName(reqMeteringRateData.getLocationName());
		
		List<MeteringRateData> childMeteringRateDataList = new ArrayList<MeteringRateData>();
		
		try {
			// 하위지역이 존재할경우 재귀호출하여 조회하고
			// 하위지역이 존재하지 않을경우 해당 지역의 검침율 정보를 조회한다.
			if(locations == null || locations.size() < 1){
				
				Map<String,Object> params = new HashMap<String,Object>();
				params.put("meterType", reqMeteringRateData.getMeterType());
				params.put("searchStartDate", reqMeteringRateData.getStartDate());
				params.put("searchEndDate", reqMeteringRateData.getEndDate());
				params.put("supplierId", reqMeteringRateData.getSupplierId());
				params.put("locationId", reqMeteringRateData.getLocationId());
				
					
				// 지역별 유효한 미터갯수 조회
				String totalCount   = (String)totalData.get(reqMeteringRateData.getLocationId());
				String successCount = meteringDataDao.getSuccessCountByLocation(params);//(String)successData.get(reqMeteringRateData.getLocationId());
				
				if(totalCount == null)   totalCount = "0";
				if(successCount == null) successCount = "0";
	
				// 검침율 계산
				BigDecimal meteringRate = null;
				
				// 전체미터건수가 0건 이상이면 계산 그렇지않으면 0
				if(!"0".equals(totalCount) && Integer.parseInt(totalCount) >= Integer.parseInt(successCount)){
					meteringRate = new BigDecimal(successCount).divide(new BigDecimal(totalCount),MathContext.DECIMAL128).multiply(new BigDecimal(100));
					meteringRate = meteringRate.setScale(2,BigDecimal.ROUND_DOWN);//소수점 2자리 이하 제거
				}else{
					meteringRate = new BigDecimal(0);
				}
				
				// 라벨생성
				String label = meteringRate+"% ("+ successCount + "/" + totalCount+")";
				
				meteringRateData.setTotalCount(totalCount);				//전체 건수
				meteringRateData.setSuccessCount(successCount);			//검침 성공건수
				meteringRateData.setSuccess(meteringRate.toString()); 	//검침 성공율
				meteringRateData.setLabel(label);
				meteringRateData.setChildren(null);
			}
			else{
				int totalCount = 0;
				int successCount = 0;
				
				for(Location location:locations){
					MeteringRateData req = new MeteringRateData();
					req.setLocationId	(location.getId().toString());
					req.setLocationName	(location.getName());
					req.setStartDate	(reqMeteringRateData.getStartDate());
					req.setEndDate		(reqMeteringRateData.getEndDate());
					req.setMeterType	(reqMeteringRateData.getMeterType());
					req.setSupplierId	(reqMeteringRateData.getSupplierId());
					// 재귀호출
					MeteringRateData res = getMeteringRateListRecur(req,location.getChildren());
					
					// 자식 지역의 전체,성공건수 합계
					totalCount   = totalCount   + Integer.parseInt(res.getTotalCount());
					successCount = successCount + Integer.parseInt(res.getSuccessCount());
					
					childMeteringRateDataList.add(res);
				}
	
				BigDecimal successRate = null;
				// 전체미터건수가 0건 이상이면 계산 그렇지않으면 0
				if(totalCount!=0){
					successRate = new BigDecimal(successCount).divide(new BigDecimal(totalCount),MathContext.DECIMAL128).multiply(new BigDecimal(100));
					successRate = successRate.setScale(2,BigDecimal.ROUND_DOWN);//소수점 2자리 이하 제거
				}else{
					successRate = new BigDecimal(0);
				}
				
				// 라벨생성
				String label = successRate+"% ("+ successCount + "/" + totalCount+")";
				
				meteringRateData.setTotalCount	(Integer.toString(totalCount));		//전체건수
				meteringRateData.setSuccessCount(Integer.toString(successCount));	//검침 성공건수
				meteringRateData.setSuccess		(successRate.toString()); 			//검침 성공율
				meteringRateData.setLabel		(label);
				
				// 자식 지역정보 설정
				meteringRateData.setChildren(childMeteringRateDataList);
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		
		return meteringRateData;
	}

    /**
     * method name : getMeteringSuccessRateListWithChildren<b/>
     * method Desc :
     *
     * @param conditionMap
     * @return
     */
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
        List<Map<String, Object>> meterCountList = meterDao.getMeterCountListPerLocation(conditionMap);
        List<Map<String, Object>> meteringSuccessCountList = null;


        meteringSuccessCountList = meteringDayDao.getMeteringSuccessCountListPerLocation(conditionMap);

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

        // 전체 검침실패율계산
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

        // 라벨생성
        String label = bdSuccessRate.toString() + "% (" + successCount + "/" + totalCount + ")";
        meteringTotalData.setLabel(label);
        meteringFailureList.add(meteringTotalData);

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
        meteringFailureData.setSuccess(bdSuccessRate.toString());

        // 라벨생성
        String label = bdSuccessRate.toString() + "% (" + successCount + "/" + totalCount + ")";
        meteringFailureData.setLabel(label);
        meteringFailureData.setSuccessFailYn("1");

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
}