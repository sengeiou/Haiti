package com.aimir.service.mvm.impl;

import java.math.BigDecimal;
import java.math.MathContext;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aimir.constants.CommonConstants.MeteringFailure;
import com.aimir.dao.device.MeterDao;
import com.aimir.dao.mvm.DayEMDao;
import com.aimir.dao.mvm.DayGMDao;
import com.aimir.dao.mvm.DayHMDao;
import com.aimir.dao.mvm.DayWMDao;
import com.aimir.dao.mvm.MeteringDataDao;
import com.aimir.dao.mvm.MeteringDayDao;
import com.aimir.dao.system.LocationDao;
import com.aimir.dao.system.SupplierDao;
import com.aimir.model.mvm.DayEM;
import com.aimir.model.system.Location;
import com.aimir.model.system.Supplier;
import com.aimir.service.mvm.MeteringFailureManager;
import com.aimir.service.mvm.bean.FailureMeterData;
import com.aimir.service.mvm.bean.FailureMeteringData;
import com.aimir.service.mvm.bean.MeteringFailureData;
import com.aimir.util.DecimalUtil;
import com.aimir.util.StringUtil;
import com.aimir.util.TimeLocaleUtil;
import com.aimir.util.TimeUtil;

@Service(value = "meteringFailureManager")
public class MeteringFailureManagerImpl implements MeteringFailureManager{

    @SuppressWarnings("unused")
    private static Log logger = LogFactory.getLog(MeteringFailureManagerImpl.class);

    @Autowired
    LocationDao locationDao;

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
    MeteringDataDao meteringDataDao;

    @Autowired
    SupplierDao supplierDao;

    @Autowired
    MeteringDayDao meteringDayDao;

    Map<String, Object> totalData;
    Map<String, Object> successData;

	public List<Map<String,Object>> getMeteringFailureRateListByLocationWithChild(Map<String,Object> params) {

		String startDate = (String)params.get("searchStartDate");
		String endDate = (String)params.get("searchEndDate");
		String meterType = (String)params.get("meterType");
		String locationId = (String)params.get("locationId");
		String locationType = (String)params.get("locationType");
		String strSupplierId = (String)params.get("supplierId");
		Integer supplierId = null;
		if(!"".equals(StringUtil.nullToBlank(strSupplierId))){
			supplierId = Integer.parseInt(strSupplierId);
		}
		String currentDate		  = TimeUtil.getCurrentDateUsingFormat("yyyymmdd");
		if("".equals(startDate)){
			startDate = currentDate;
			params.put("searchStartDate", startDate);
		}
		if("".equals(endDate)){
			endDate = currentDate;
			params.put("searchEndDate", endDate);
		}
		
		
		List<Map<String,Object>> resMeteringFailureDataList=new ArrayList<Map<String,Object>>();
		
		//조회객체
		MeteringFailureData reqMeteringFailureData = new MeteringFailureData();
		reqMeteringFailureData.setStartDate(startDate);//일,주,월,계절 인 날짜 타입별로 조회시작,종료일 설정
		reqMeteringFailureData.setEndDate(endDate);
		reqMeteringFailureData.setMeterType(meterType);//전기,가스,수도,열량 미터구분
		reqMeteringFailureData.setSupplierId(strSupplierId);
		
		//입력된 locationId를 부모로 가지는 location 목록조회
		
		List<Location> locations = null;
		// locationId가 입력되지 않았을경우 최상위 지역 목록조회
		if(locationId==null||"".equals(locationId.trim())){
			locations = locationDao.getParents(supplierId);
		}else{
			// locationType '1'일경우 상위 지역목록조회 
			// 그렇지 않으면 하위 지역목록을 조회한다.
			if(locationType != null && "1".equals(locationType.trim())){
				//입력된 locationId 의 부모의 부모 locationID를 조회후 하위 지역을 조회하면됨
				//부모가 없거나, 부모의 부모가 없을경우 최상위 Location 목록을 조회한다.
				List<Integer> parentLocation = null;
				Integer parentLocationId = null;
				
				parentLocation = locationDao.getParentId(Integer.parseInt(locationId));
				parentLocationId = parentLocation==null||parentLocation.size()<1?null:parentLocation.get(0);
				
				if(parentLocationId!=null){
					parentLocation = locationDao.getParentId(parentLocationId);
					parentLocationId = parentLocation==null||parentLocation.size()<1?null:parentLocation.get(0);
					if(parentLocationId!=null){
						locations = locationDao.getChildren(parentLocationId);
					}else{
						locations = locationDao.getParents(supplierId);
					}
				}else{
					locations = locationDao.getParents(supplierId);
				}
			}else{
				locations = locationDao.getChildren(Integer.parseInt(locationId));
				
				if(locations==null||locations.size()<1){
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
		
		int totalCnt=0;
		int failureCnt=0;
		for(Location location:locations){
			reqMeteringFailureData.setLocationId(location.getId().toString());
			reqMeteringFailureData.setLocationName(location.getName());
			
			Map<String,Object> resMeteringFailureData = getMeteringFailureRateListRecur(reqMeteringFailureData,location.getChildren());
			
			totalCnt = totalCnt + Integer.parseInt(((String)resMeteringFailureData.get("totalCount")).replace(",",""));
			failureCnt = failureCnt + Integer.parseInt(((String)resMeteringFailureData.get("successCount")).replace(",",""));
			
			resMeteringFailureDataList.add(resMeteringFailureData);
		}
		
		// 전체 검침실패율계산
		BigDecimal failureRate=null;
		
		// 전체미터건수가 0건 이상이면 계산 그렇지않으면 0
		if(totalCnt!=0){
			failureRate = new BigDecimal(failureCnt).divide(new BigDecimal(totalCnt),MathContext.DECIMAL128).multiply(new BigDecimal(100));
			failureRate = failureRate.setScale(2,BigDecimal.ROUND_DOWN);//소수점제거
		}else{
			failureRate = new BigDecimal(0);
		}
		
		Supplier supplier = supplierDao.get(supplierId);
		DecimalFormat dfMd = DecimalUtil.getMDStyle(supplier.getMd());
		
		// 라벨생성
		String label = failureRate+"% ("+ dfMd.format(failureCnt) + "/" + dfMd.format(totalCnt)+")";
		
		Map<String,Object> total = new HashMap<String,Object>();
		total.put("success",failureRate.toString());
		total.put("label",label);
		total.put("locationName","Total");
		
		resMeteringFailureDataList.add(total);
		//total.setChildren(resMeteringFailureDataList);
		
		//List<MeteringFailureData> result = new ArrayList<MeteringFailureData>();
		//result.add(total);
		return resMeteringFailureDataList;
	}

    public List<Map<String, Object>> getMeteringFailureRateListByLocation(Map<String, Object> params) {
        String startDate = StringUtil.nullToBlank(params.get("searchStartDate"));
        String endDate = StringUtil.nullToBlank(params.get("searchEndDate"));
        String meterType = StringUtil.nullToBlank(params.get("meterType"));
        String locationId = StringUtil.nullToBlank(params.get("locationId"));
        String locationType = StringUtil.nullToBlank(params.get("locationType"));
        String strSupplierId = StringUtil.nullToBlank(params.get("supplierId"));
        Integer supplierId = null;

        if (!strSupplierId.isEmpty()) {
            supplierId = Integer.valueOf(strSupplierId);
        }

        String currentDate = TimeUtil.getCurrentDateUsingFormat("yyyymmdd");
        if (startDate.isEmpty()) {
            startDate = currentDate;
            params.put("searchStartDate", startDate);
        }
        if (endDate.isEmpty()) {
            endDate = currentDate;
            params.put("searchEndDate", endDate);
        }

        List<Map<String,Object>> resMeteringFailureDataList=new ArrayList<Map<String,Object>>();

        //조회객체
        MeteringFailureData reqMeteringFailureData = new MeteringFailureData();
        reqMeteringFailureData.setStartDate(startDate);//일,주,월,계절 인 날짜 타입별로 조회시작,종료일 설정
        reqMeteringFailureData.setEndDate(endDate);
        reqMeteringFailureData.setMeterType(meterType);//전기,가스,수도,열량 미터구분
        reqMeteringFailureData.setSupplierId(strSupplierId);
        //입력된 locationId를 부모로 가지는 location 목록조회

        List<Location> locations = null;;
        // locationId가 입력되지 않았을경우 최상위 지역 목록조회
        if (locationId.trim().isEmpty()) {
            locations = locationDao.getParents(supplierId);
        } else {
            // locationType '1'일경우 상위 지역목록조회
            // 그렇지 않으면 하위 지역목록을 조회한다.
            if ("1".equals(locationType.trim())) {
                //입력된 locationId 의 부모의 부모 locationID를 조회후 하위 지역을 조회하면됨
                //부모가 없거나, 부모의 부모가 없을경우 최상위 Location 목록을 조회한다.
                List<Integer> parentLocation = null;
                Integer parentLocationId = null;

                parentLocation = locationDao.getParentId(Integer.parseInt(locationId));
                parentLocationId = (parentLocation == null || parentLocation.size() < 1) ? null : parentLocation.get(0);

                if (parentLocationId != null) {
                    parentLocation = locationDao.getParentId(parentLocationId);
                    parentLocationId = (parentLocation == null || parentLocation.size() < 1) ? null : parentLocation.get(0);
                    if (parentLocationId != null) {
                        locations = locationDao.getChildren(parentLocationId, supplierId);
                    } else {
                        locations = locationDao.getParents(supplierId);
                    }
                } else {
                    locations = locationDao.getParents(supplierId);
                }
            } else {
                locations = locationDao.getChildren(Integer.parseInt(locationId));

                if (locations == null || locations.size() < 1) {
                    //하위지역이 없을경우 현재 레벨의 지역을 조회한다.
                    List<Integer> parentLocation = null;
                    Integer parentLocationId = null;

                    parentLocation = locationDao.getParentId(Integer.parseInt(locationId));
                    parentLocationId = (parentLocation == null || parentLocation.size() < 1) ? null : parentLocation.get(0);

                    if (parentLocationId != null) {
                        locations = locationDao.getChildren(parentLocationId, supplierId);
                    } else {
                        locations = locationDao.getParents(supplierId);
                    }
                }
            }
        }

        // 지역별 유효한 미터갯수 조회
        totalData = meteringDataDao.getTotalCountByLocation(params);
        // 검침 성공한 미터갯수 조회
        //successData = meteringDataDao.getSuccessCountByLocation(params);

        for (Location location : locations) {
            reqMeteringFailureData.setLocationId(location.getId().toString());
            reqMeteringFailureData.setLocationName(location.getName());
            Map<String,Object> resMeteringFailureData = new HashMap<String,Object>();

        	resMeteringFailureData = getMeteringFailureRateListRecur(reqMeteringFailureData, location.getChildren());

            resMeteringFailureDataList.add(resMeteringFailureData);
        }

        return resMeteringFailureDataList;
    }


    /**
     * 지역별 검침실패율 데이터를 재귀적으로 호출하여 조회한다.
     * @param reqMeteringFailureData
     * @return
     */
    private Map<String,Object> getMeteringFailureRateListRecur(MeteringFailureData reqMeteringFailureData,Set<Location> locations) {

        //조회결과객체
        Map<String,Object> meteringFailureData = new HashMap<String,Object>();
        meteringFailureData.put("locationId",reqMeteringFailureData.getLocationId());
        meteringFailureData.put("locationName",reqMeteringFailureData.getLocationName());

        List<Map<String,Object>> childMeteringFailureDataList = new ArrayList<Map<String,Object>>();

        // 하위지역이 존재할경우 재귀호출 + 해당 지역의 실패율정보를 조회하고
        // 하위지역이 존재하지 않을경우 해당 지역의 실패율정보를 조회한다.


        // 지역별 유효한 미터갯수 조회
        Map<String,Object> params = new HashMap<String,Object>();
        params.put("meterType", reqMeteringFailureData.getMeterType());
        params.put("searchStartDate", reqMeteringFailureData.getStartDate());
        params.put("searchEndDate", reqMeteringFailureData.getEndDate());
        params.put("supplierId", reqMeteringFailureData.getSupplierId());
        params.put("locationId", reqMeteringFailureData.getLocationId());

        String strTotalCount = (String)totalData.get(reqMeteringFailureData.getLocationId());
        String strSuccessCnt = meteringDayDao.getSuccessCountByLocation(params);

        //jhkim

        Map<String, String> failureCauseCountMap = meteringDayDao.getFailureCountByCauses(params);
        String strFailureCause1Count = StringUtil.nullToZero(failureCauseCountMap.get("cause1"));
        String strFailureCause2Count = StringUtil.nullToZero(failureCauseCountMap.get("cause2"));
        String strFailureCause3Count = StringUtil.nullToZero(failureCauseCountMap.get("cause3"));

        if (strTotalCount == null) strTotalCount = "0";
        if (strSuccessCnt == null) strSuccessCnt = "0";

        // 검침실패한 미터갯수 조회
        String strFailureCount = Integer.toString(Integer.parseInt(strTotalCount) - Integer.parseInt(strSuccessCnt));

        // 검침실패율계산
        BigDecimal failureRate = null;

        // 전체미터건수가 0건 이상이면 계산 그렇지않으면 0
        if (!"0".equals(strTotalCount) && !strTotalCount.startsWith("-")) {
            failureRate = new BigDecimal(strFailureCount).divide(new BigDecimal(strTotalCount),MathContext.DECIMAL128).multiply(new BigDecimal(100));
            failureRate = failureRate.setScale(2,BigDecimal.ROUND_DOWN);//소수점제거
        } else {
            failureRate = new BigDecimal(0);
        }
        
        Supplier supplier = supplierDao.get(Integer.parseInt(reqMeteringFailureData.getSupplierId()));
        DecimalFormat dfMd = DecimalUtil.getMDStyle(supplier.getMd());

        if (locations == null || locations.size() < 1) {    // 최하위 노드인 경우
            // 라벨생성
            String label = failureRate+"% ("+ dfMd.format(Integer.parseInt(strFailureCount)) + "/" + dfMd.format(Integer.parseInt(strTotalCount))+")";
            meteringFailureData.put("totalCount", dfMd.format(Integer.parseInt(strTotalCount)));//전체건수
            meteringFailureData.put("successCount", dfMd.format(Integer.parseInt(strFailureCount)));//실패건수
            meteringFailureData.put("success", failureRate.toString()); //실패율
    
    
            meteringFailureData.put("label", label);
            meteringFailureData.put("successFailYn", "0");
            meteringFailureData.put("children", null);
            meteringFailureData.put("isBranch", "false");
            meteringFailureData.put("leaf", true);
            //jhkim
            meteringFailureData.put("failureCountByCause1", dfMd.format(Integer.parseInt(strFailureCause1Count)));
            meteringFailureData.put("failureCountByCause2", dfMd.format(Integer.parseInt(strFailureCause2Count)));
            meteringFailureData.put("failureCountByEtc", dfMd.format(Integer.parseInt(strFailureCause3Count)));
        } else {

            int totalCount = 0;
            int failureCount = 0;

            //jhkim
            int failureCause1Count = 0;
            int failureCause2Count = 0;
            int failureCauseEtcCount = 0;

            failureRate = null;

            for (Location location : locations) {
                MeteringFailureData req = new MeteringFailureData();
                req.setLocationId(location.getId().toString());
                req.setLocationName(location.getName());
                req.setStartDate(reqMeteringFailureData.getStartDate());
                req.setEndDate(reqMeteringFailureData.getEndDate());
                req.setMeterType(reqMeteringFailureData.getMeterType());
                req.setSupplierId(reqMeteringFailureData.getSupplierId());

                Map<String,Object> res = getMeteringFailureRateListRecur(req,location.getChildren());

                // 자식 지역의 전체,실패건수 합계
                totalCount = totalCount + Integer.parseInt(((String)res.get("totalCount")).replace(",",""));
                failureCount = failureCount + Integer.parseInt(((String)res.get("successCount")).replace(",",""));

                //jhkim
                failureCause1Count = failureCause1Count + Integer.parseInt(((String)res.get("failureCountByCause1")).replace(",",""));
                failureCause2Count = failureCause2Count + Integer.parseInt(((String)res.get("failureCountByCause2")).replace(",",""));
                failureCauseEtcCount = failureCauseEtcCount + Integer.parseInt(((String)res.get("failureCountByEtc")).replace(",",""));

                childMeteringFailureDataList.add(res);
            }

            // 하위노드 값과 현재노드 값을 합함
            totalCount = totalCount + Integer.parseInt(strTotalCount);
            failureCount = failureCount + Integer.parseInt(strFailureCount);
            failureCause1Count = failureCause1Count + Integer.parseInt(strFailureCause1Count);
            failureCause2Count = failureCause2Count + Integer.parseInt(strFailureCause2Count);
            failureCauseEtcCount = failureCauseEtcCount + Integer.parseInt(strFailureCause3Count);

            // 전체미터건수가 0건 이상이면 계산 그렇지않으면 0
            if (totalCount != 0) {
                failureRate = new BigDecimal(failureCount).divide(new BigDecimal(totalCount),MathContext.DECIMAL128).multiply(new BigDecimal(100));
                failureRate = failureRate.setScale(2,BigDecimal.ROUND_DOWN);    //소수점제거
            } else {
                failureRate = new BigDecimal(0);
            }

            // 라벨생성
            String label = failureRate+"% ("+ dfMd.format(failureCount) + "/" + dfMd.format(totalCount)+")";

            meteringFailureData.put("totalCount", dfMd.format(totalCount));        //전체건수
            meteringFailureData.put("successCount", dfMd.format(failureCount));    //실패건수
            meteringFailureData.put("success", failureRate.toString()); //실패율
            meteringFailureData.put("label", label);
            meteringFailureData.put("successFailYn", "0");

            // 자식 지역정보 설정
            meteringFailureData.put("children", childMeteringFailureDataList);
            meteringFailureData.put("isBranch", "true");

            //jhkim
            meteringFailureData.put("failureCountByCause1", dfMd.format(failureCause1Count));
            meteringFailureData.put("failureCountByCause2", dfMd.format(failureCause2Count));
            meteringFailureData.put("failureCountByEtc", dfMd.format(failureCauseEtcCount));
        }
        return meteringFailureData;
    }

    // 검침실패 미터목록을 조회한다.
    @SuppressWarnings("unchecked")
    public Map<String, Object> getMeteringFailureMeter(Map<String,Object> params) {

        String locationId = (String)params.get("locationId");

        if ("".equals(locationId)) {
            locationId = "0";
        }

        String strSupplierId = (String)params.get("supplierId");
        Integer supplierId = null;
        if (!"".equals(StringUtil.nullToBlank(strSupplierId))) {
            supplierId = Integer.parseInt(strSupplierId);
        }

        Supplier supplier = supplierDao.get(supplierId);
        String lang = supplier.getLang().getCode_2letter();
        String country = supplier.getCountry().getCode_2letter();

        // 공급지역 검색 조건 시 - 현재 노드 포함 하위 노드 값 조회 및 설정
        if (locationId != null && !locationId.trim().equals("")) {
            Integer iLocationId = Integer.valueOf(locationId);

            if (iLocationId != null) {
                List<Integer> locationIdList = null;
                
                //Location의 root가 하나밖에 없다면 루트 검색시 쿼리에서 location별로 검색하는 불필요한 동작을 하지 않도록 하기 위함
                Boolean isRoot = locationDao.isRoot(iLocationId);
                List<Integer> rootList = locationDao.getRoot();
                if(!isRoot || rootList.size() != 1) {
            		locationIdList = locationDao.getChildLocationId(iLocationId);
                    locationIdList.add(iLocationId);
                }
                
                params.put("locationId", locationIdList);
            }
        }

        Map<String, Object> resultMap = meterDao.getMeteringFailureMeter(params);

        List<FailureMeterData> meterDataList =  new ArrayList<FailureMeterData>();

        List<Object> resultList = (List<Object>)resultMap.get("list");
 
        String lastReadDate = null;

        for (Object obj : resultList) {
            int i=0;
            Object[] objs = (Object[])obj;

            FailureMeterData meterData = new FailureMeterData();
            meterData.setChecked(false);
            meterData.setCustomerId  (StringUtil.nullToBlank(objs[i++]));
            meterData.setCustomerName(StringUtil.nullToBlank(objs[i++]));
            meterData.setAddress     (StringUtil.nullToBlank(objs[i++]));
            meterData.setMdsId       (StringUtil.nullToBlank(objs[i++]));
            meterData.setMeterId     (StringUtil.nullToBlank(objs[i++]));
            meterData.setMeterAddress(StringUtil.nullToBlank(objs[i++]));
            meterData.setModemId     (StringUtil.nullToBlank(objs[i++]));
            meterData.setMcuId       (StringUtil.nullToBlank(objs[i++]));

            lastReadDate = StringUtil.nullToBlank(objs[i++]);
            meterData.setLastlastReadDate(TimeLocaleUtil.getLocaleDate(lastReadDate, lang, country));
            meterData.setMeterStatus     (StringUtil.nullToBlank(objs[i++]));
            meterData.setTimeDiff        (StringUtil.nullToBlank(objs[i++]));

            boolean isCommError = false;

            try {
                if (!StringUtil.nullToBlank(lastReadDate).isEmpty()) {
                    // 마지막 통신 시간과 현재 시간의 차이가 24시간 이상이면 통신장애
                    if (TimeUtil.getDayDuration(lastReadDate, TimeUtil.getCurrentTime()) >= 1) {
                        isCommError = true;
                    }
                } else {
                    isCommError = true;
                }
            } catch(ParseException e) {
                e.printStackTrace();
            }

            /**
             * 0 ("통신이력 없음");
             * 1 ("장기간 통신장애"); //aimir.commstateYellow
             * 2 ("검침포멧 이상");
             * 3 ("미터 교체 및 공급 중단");
             * 4 ("미터상태 이상");
             * 5 ("미터시간 이상");
             * 6 ("success");
             */
            int intMessage ;
            if (meterData.getLastlastReadDate() == "" || meterData.getLastlastReadDate() == null) {
                intMessage = MeteringFailure.NotComm.getMeteringMessage();
            }
            else if (isCommError) {
                intMessage = MeteringFailure.CommstateYellow.getMeteringMessage();
            }
            //else if(strDate.substring(0,8) != meterData.getYYYYMMDD() ){ // YYYYMMDD = null
            // Metering Fail에서는 마지막 통신 날짜와 오늘 날짜를 비교 해야만 검침 포멧 이상이 된다.
            // 그러나 현재 로직상으로 meterData.getYYYYMMDD()이 항상 null이기 때문에 "통신 이력 없음,장기간 통신장애"가 원인이 아니면
            // 항상 검침 포멧 이상이 되어 버린다.
            // 그래서 오늘 일자로 비교하도록 수정함 by eunmiae
            else if (!(lastReadDate.substring(0,8).equals(TimeUtil.getCurrentTimeMilli().substring(0, 8)))) {
                intMessage = MeteringFailure.MeteringFormatError.getMeteringMessage();
            }
            else if (meterData.getMeterStatus() != null && meterData.getMeterStatus() != "") {
                intMessage = MeteringFailure.MeterStatusError.getMeteringMessage();
            }
            else if (!"".equals(meterData.getTimeDiff()) && Integer.parseInt(meterData.getTimeDiff()) >= 0) {
                intMessage = MeteringFailure.MeterTimeError.getMeteringMessage();
            }
            else {
                intMessage = MeteringFailure.MeterTimeSucces.getMeteringMessage();
            }
            meterData.setFailureCause(Integer.toString(intMessage));
            meterDataList.add(meterData);
        }

        resultMap.put("list", meterDataList);

        return resultMap;
    }

	// 미터의 검침데이터 목록을 조회한다.
	public List<FailureMeteringData> getMeteringFailureMeteringData(Map<String,Object> params){
		
		List<DayEM> grid = dayEMDao.getMeteringFailureMeteringData(params);

		List<FailureMeteringData> failureMeteringDataList = new ArrayList<FailureMeteringData>();
		FailureMeteringData failureMeteringData = null;
		
		String strSupplierId = (String)params.get("supplierId");
		Integer supplierId = null;
		if(!"".equals(StringUtil.nullToBlank(strSupplierId))){
			supplierId = Integer.parseInt(strSupplierId);
		}
		
		Supplier supplier = supplierDao.get(supplierId);

		for(DayEM dayEM:grid){
			failureMeteringData = new FailureMeteringData();
			failureMeteringData.setMeteringDate(TimeLocaleUtil.getLocaleDate(dayEM.getYyyymmdd() , supplier.getLang().getCode_2letter(), supplier.getCountry().getCode_2letter()));
			failureMeteringData.setMeteringValue(dayEM.getTotal().toString());
			failureMeteringDataList.add(failureMeteringData);
		}
		
		return failureMeteringDataList;
	}

    /**
     * method name : getMeteringCountListPerLocation<b/>
     * method Desc :
     *
     * @param conditionMap
     * @return
     */
    public List<MeteringFailureData> getMeteringCountListPerLocation(Map<String, Object> conditionMap) {
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
        List<Map<String, Object>> meterCountList = meterDao.getMeterCountListPerLocation(conditionMap);
        // metering success count per location
        List<Map<String, Object>> meteringSuccessCountList = null;

        meteringSuccessCountList = meteringDayDao.getMeteringSuccessCountListPerLocation(conditionMap);

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
     * method name : getMeteringFailureRateListWithChildren<b/>
     * method Desc :
     *
     * @param conditionMap
     * @return
     */
    public List<MeteringFailureData> getMeteringFailureRateListWithChildren(Map<String, Object> conditionMap) {
        Integer supplierId = (Integer)conditionMap.get("supplierId");
        Map<String, Object> totalCountMap = new HashMap<String, Object>();
        Map<String, Object> successCountMap = new HashMap<String, Object>();

        // root location list
        List<Location> rootLocationList = locationDao.getRootLocationListBySupplier(supplierId);
        // location list
        List<Map<String, Object>> locationList = locationDao.getLocationTreeForMeteringRate(supplierId);
        // meter count per location
        List<Map<String, Object>> meterCountList = meterDao.getMeterCountListPerLocation(conditionMap);
        // metering success count per location
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
        Integer failureCount = 0;
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
        meteringTotalData.setSuccess(bdFailureRate.toString());
        meteringTotalData.setLocationName("Total");
        meteringTotalData.setChildren(null);
        meteringTotalData.setIsBranch("false");
        meteringTotalData.setLeaf(true);
        failureCount = totalCount - successCount;

        // 라벨생성
        String label = bdFailureRate.toString() + "% (" + failureCount + "/" + totalCount + ")";
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
        meteringFailureData.setSuccess(bdFailureRate.toString());

        // 라벨생성
        String label = bdFailureRate.toString() + "% (" + failureCount + "/" + totalCount + ")";
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
}