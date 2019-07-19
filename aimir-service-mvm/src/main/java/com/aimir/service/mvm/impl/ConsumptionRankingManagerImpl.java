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

import com.aimir.constants.CommonConstants;
import com.aimir.dao.device.MCUDao;
import com.aimir.dao.mvm.MeteringDayDao;
import com.aimir.dao.mvm.MeteringMonthDao;
import com.aimir.dao.system.LocationDao;
import com.aimir.dao.system.SupplierDao;
import com.aimir.model.device.MCU;
import com.aimir.model.system.Supplier;
import com.aimir.service.mvm.ConsumptionRankingManager;
import com.aimir.util.CalendarUtil;
import com.aimir.util.DecimalUtil;
import com.aimir.util.StringUtil;
import com.aimir.util.TimeLocaleUtil;

@Service(value="consumptionRankingManager")
public class ConsumptionRankingManagerImpl implements ConsumptionRankingManager{

    @SuppressWarnings("unused")
    private static Log logger = LogFactory.getLog(ConsumptionRankingManagerImpl.class);

    @Autowired
	MeteringDayDao meteringDayDao;
    
	@Autowired
	MeteringMonthDao meteringMonthDao;
	
	@Autowired
	LocationDao locationDao;
	
	@Autowired
	SupplierDao supplierDao;
	
	@Autowired
	MCUDao mcuDao;

	@SuppressWarnings("unchecked")
	@Deprecated
	public Map<String, Object> getConsumptionRanking(Map<String, Object> condition) {

		List<Object> returnGrid = new ArrayList<Object>();
		
		try {
			String dateType      = (String)condition.get("dateType");
			String rankingType   = (String)condition.get("rankingType");
			
			String strSupplierId = (String)condition.get("supplierId");
			Integer supplierId = 0;
			if(!"".equals(StringUtil.nullToBlank(strSupplierId))){
				supplierId = Integer.parseInt(strSupplierId);
			}
			condition.put("supplierId", supplierId);
			
			String strLocationId = (String)condition.get("locationId");
			Integer locationId = 0;
			if(!"".equals(StringUtil.nullToBlank(strLocationId))){
				locationId = Integer.parseInt(strLocationId);
			}
			List<Integer> locations = locationDao.getLeafLocationId(locationId, supplierId);
			condition.put("locations", locations);
			
			String strTariffType = (String)condition.get("tariffType");
			Integer tariffType = 0;
			if(!"".equals(StringUtil.nullToBlank(strTariffType))){
				tariffType = Integer.parseInt(strTariffType);
			}
			condition.put("tariffType", tariffType);
	
			List<Object> grid = null;
			if(dateType.equals(CommonConstants.DateType.MONTHLY.getCode())){
				grid = meteringMonthDao.getConsumptionRanking(condition);
			}
			else{
				grid = meteringDayDao.getConsumptionRanking(condition);
			}			
			
			String strUsage = (String)condition.get("usage");
			//2011.5.9 jhkim
			//usage type, int -> double
			double usage = 0;
			if(!"".equals(StringUtil.nullToBlank(strUsage))){
				usage = Double.parseDouble(strUsage);
			}
			
			Supplier supplier = supplierDao.get(supplierId);
			DecimalFormat df = DecimalUtil.getDecimalFormat(supplier.getMd());
			
			Map<String, Object> row = null;
			Double rowUsage = null;
			
			//jhkim 
			//사용량 0인 미터의 조회수가 10이상이면 break 추가
			int zeroCnt=0;
			int RankingCnt = 1;	//jhkim
			for(Object obj:grid){
				if(zeroCnt >10) break;
				row = (Map<String, Object>)obj;
				row.put("rankingCnt", RankingCnt); //jhkim
				RankingCnt++;
				rowUsage = (Double)row.get("usage");
				row.put("usage", df.format((Double)row.get("usage")));
				if( rankingType.equals(CommonConstants.RankingType.ZERO.getType()) ){
					if( rowUsage == 0 ){
						returnGrid.add(row);
						zeroCnt++;
					}
				}
				else{
					if( rowUsage >= usage ){
						returnGrid.add(row);
					}
				}
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}
		
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("grid", returnGrid);
		
		return result;
	}

    @SuppressWarnings("unchecked")
    @Deprecated
	public Map<String, Object> getConsumptionRankingList(Map<String, Object> condition) {

		List<Object> grid = null;
		String dateType   = (String)condition.get("dateType");
		String rankingType = (String)condition.get("rankingType");
		
		String strSupplierId = (String)condition.get("supplierId");
		Integer supplierId = 0;
		if(!"".equals(StringUtil.nullToBlank(strSupplierId))){
			supplierId = Integer.parseInt(strSupplierId);
		}
		condition.put("supplierId", supplierId);

		String strLocationId = (String)condition.get("locationId");
		Integer locationId = 0;
		if(!"".equals(StringUtil.nullToBlank(strLocationId))){
			locationId = Integer.parseInt(strLocationId);
		}
		List<Integer> locations = locationDao.getLeafLocationId(locationId, supplierId);
		condition.put("locations", locations);
		
		String strTariffType = (String)condition.get("tariffType");
		Integer tariffType = 0;
		if(!"".equals(StringUtil.nullToBlank(strTariffType))){
			tariffType = Integer.parseInt(strTariffType);
		}
		condition.put("tariffType", tariffType);

		String strRankingCount = (String)condition.get("rankingCount");
		Integer rankingCount = 0;
		if(!"".equals(StringUtil.nullToBlank(strRankingCount))){
			rankingCount = Integer.parseInt(strRankingCount);
		}
		condition.put("rankingCount", rankingCount);

		String searchType = (String)condition.get("searchType");
		
		//날짜값이 없을경우 default를 오늘 날짜로 지정
		String startDate = (String) condition.get("startDate");
		if(startDate == null || startDate.isEmpty()) {
			condition.put("startDate", CalendarUtil.getCurrentDate());
			condition.put("endDate", CalendarUtil.getCurrentDate());
		}
		
		
		if(dateType.equals(CommonConstants.DateType.MONTHLY.getCode())){
			grid = meteringMonthDao.getConsumptionRankingList(condition);
		}
		else{
			grid = meteringDayDao.getConsumptionRankingList(condition);
		}

		String strUsage = (String)condition.get("usage");
		//2011.5.9 jhkim
		//usage type, int -> double
		
		double usage = 0;
		if(!"".equals(StringUtil.nullToBlank(strUsage))){
			usage = Double.parseDouble(strUsage);
		}
		
		Supplier supplier = supplierDao.get(supplierId);
		DecimalFormat df = DecimalUtil.getDecimalFormat(supplier.getMd());
		
		List<Object> returnGrid = new ArrayList<Object>();
		Map<String, Object> row = null;
		Double rowUsage = null;
		
		int rankingCnt = 1; //jhkim
		for(Object obj:grid){
			row = (Map<String, Object>)obj;
			rowUsage = (Double)row.get("usage");
			row.put("usage", df.format((Double)row.get("usage")));
			row.put("rankingCnt", rankingCnt); //jhkim
			rankingCnt++;
			if( rankingType.equals(CommonConstants.RankingType.ZERO.getType()) ){
				if( rowUsage == 0 ){
					returnGrid.add(row);
				}
			}
			else{
				if( rowUsage >= usage ){
					returnGrid.add(row);
				}
			}
		}

		Map<String, Object> result = new HashMap<String, Object>();
		if( "search".equals(searchType) ){
			result.put("total", returnGrid.size());
		}

		int page = (Integer)condition.get("page");
		int pageSize = CommonConstants.Paging.ROWPERPAGE.getPageNum();
		if(condition.containsKey("pageSize")){
			pageSize = (Integer)condition.get("pageSize");
		}
		
		int fromIdx = page * pageSize;
		int toIdx   = (page + 1) * pageSize;//returnGrid.size() > (fromIdx + pageSize)? fromIdx + pageSize:returnGrid.size();
		toIdx = returnGrid.size() < toIdx ? returnGrid.size() : toIdx;
		if(pageSize == 0) {
			result.put("grid", returnGrid);
		} else {
//			returnGrid.add(returnGrid.subList(fromIdx, toIdx));
			result.put("grid", returnGrid.subList(fromIdx, toIdx));
		}
		
		return result;    
    }

    /**
     * method name : getConsumptionRankingData<b/>
     * method Desc : Consumption Ranking 미니가젯에서 소비랭킹 리스트를 조회한다.
     *
     * @param conditionMap
     * @return
     */
    public List<Map<String, Object>> getConsumptionRankingData(Map<String, Object> conditionMap) {
        List<Map<String, Object>> result = null;
        String dateType = (String) conditionMap.get("dateType");
        Integer supplierId = (Integer) conditionMap.get("supplierId");
        Integer locationId = (Integer) conditionMap.get("locationId");
        Integer page = (Integer) conditionMap.get("page");
        Integer limit = (Integer) conditionMap.get("limit");

        if (locationId != null) {
            List<Integer> locationList = locationDao.getLeafLocationId(locationId, supplierId);
            locationList.add(locationId);
            conditionMap.put("locationList", locationList);
        }

        // 날짜값이 없을경우 default를 오늘 날짜로 지정
        String startDate = (String) conditionMap.get("startDate");
        if (startDate == null || startDate.isEmpty()) {
            conditionMap.put("startDate", CalendarUtil.getCurrentDate());
            conditionMap.put("endDate", CalendarUtil.getCurrentDate());
        }

        if (dateType.equals(CommonConstants.DateType.MONTHLY.getCode())) {
            result = meteringMonthDao.getConsumptionRankingDataList(conditionMap, false);
        } else {
            result = meteringDayDao.getConsumptionRankingDataList(conditionMap, false);
        }

        Supplier supplier = supplierDao.get(supplierId);
        DecimalFormat df = DecimalUtil.getDecimalFormat(supplier.getMd());
        StringBuilder period = new StringBuilder();
        Integer idx = (page != null) ? (page - 1) * limit : 0;

        for (Map<String, Object> map : result) {
            map.put("rankingCnt", (idx++) + 1);
            period.delete(0, period.length());
            map.put("totalUsage", df.format(DecimalUtil.ConvertNumberToDouble(map.get("totalUsage"))));
        }

        return result;
    }

    /**
     * method name : getConsumptionRankingDataList<b/>
     * method Desc : Consumption Ranking 가젯에서 소비랭킹 리스트를 조회한다.
     *
     * @param conditionMap
     * @return
     */
    public Map<String, Object> getConsumptionRankingDataList(Map<String, Object> conditionMap) {
        return getConsumptionRankingDataList(conditionMap, false);
    }

    /**
     * method name : getConsumptionRankingDataList<b/>
     * method Desc : Consumption Ranking 가젯에서 소비랭킹 리스트를 조회한다.
     *
     * @param conditionMap
     * @param isAll 전체 조회여부
     * @return
     */
    @SuppressWarnings("unused")
    public Map<String, Object> getConsumptionRankingDataList(Map<String, Object> conditionMap, boolean isAll) {
        Map<String, Object> resultMap = new HashMap<String, Object>();
        List<Map<String, Object>> result = null;
        List<Map<String, Object>> cntResult = null;
        List<Map<String, Object>> list = null;
        String dateType = (String) conditionMap.get("dateType");
        String sysId = (String) conditionMap.get("sysId");
        Integer dcuId = -1;
        Integer supplierId = (Integer) conditionMap.get("supplierId");
        Integer locationId = (Integer) conditionMap.get("locationId");
        Integer page = (Integer) conditionMap.get("page");
        Integer limit = (Integer) conditionMap.get("limit");
        Integer rankingCount = (Integer) conditionMap.get("rankingCount");
        Integer totalCount = 0;

        if (locationId != null) {
            List<Integer> locationList = locationDao.getLeafLocationId(locationId, supplierId);
            locationList.add(locationId);
            conditionMap.put("locationList", locationList);
        }

        if(sysId != null && !"".equals(sysId)) {
        	MCU mcu = mcuDao.get(sysId);
        	if(mcu != null)
        		dcuId = mcu.getId();
        	conditionMap.put("dcuId",dcuId);
        }

        // 날짜값이 없을경우 default를 오늘 날짜로 지정
        String startDate = (String) conditionMap.get("startDate");
        if (startDate == null || startDate.isEmpty()) {
            conditionMap.put("startDate", CalendarUtil.getCurrentDate());
            conditionMap.put("endDate", CalendarUtil.getCurrentDate());
        }

        // 전체 조회
        if (isAll) {
            if (dateType.equals(CommonConstants.DateType.MONTHLY.getCode())) {
                result = meteringMonthDao.getConsumptionRankingDataList(conditionMap, false, true);
            } else {
                result = meteringDayDao.getConsumptionRankingDataList(conditionMap, false, true);
            }
            totalCount = result.size();

        } else {
            if (dateType.equals(CommonConstants.DateType.MONTHLY.getCode())) {
                result = meteringMonthDao.getConsumptionRankingDataList(conditionMap, false);
                cntResult = meteringMonthDao.getConsumptionRankingDataList(conditionMap, true);
            } else {
                result = meteringDayDao.getConsumptionRankingDataList(conditionMap, false);
                cntResult = meteringDayDao.getConsumptionRankingDataList(conditionMap, true);
            }
            totalCount = (Integer) (cntResult.get(0).get("total"));
        }

        Supplier supplier = supplierDao.get(supplierId);
        String lang = supplier.getLang().getCode_2letter();
        String country = supplier.getCountry().getCode_2letter();
        DecimalFormat df = DecimalUtil.getDecimalFormat(supplier.getMd());
        DecimalFormat dfMd = DecimalUtil.getMDStyle(supplier.getMd());
        StringBuilder period = new StringBuilder();
        Integer idx = (page != null) ? (page - 1) * limit : 0;

        for (Map<String, Object> map : result) {
            map.put("rankingCnt", dfMd.format((idx++) + 1));
            period.delete(0, period.length());
            map.put("totalUsage", df.format(DecimalUtil.ConvertNumberToDouble(map.get("totalUsage"))));

            if (dateType.equals(CommonConstants.DateType.WEEKDAILY.getCode())) {
                period.append(TimeLocaleUtil.getLocaleDate((String) conditionMap.get("startDate"), lang, country));
            } else {
                period.append(TimeLocaleUtil.getLocaleDate((String) conditionMap.get("startDate"), lang, country));
                period.append(" ~ ");
                period.append(TimeLocaleUtil.getLocaleDate((String) conditionMap.get("endDate"), lang, country));
            }

            map.put("period", period.toString());
        }

        resultMap.put("result", result);
        resultMap.put("totalCount", totalCount);
        return resultMap;
    }
}