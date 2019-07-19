package com.aimir.service.device.impl;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.jws.WebService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aimir.constants.CommonConstants;
import com.aimir.constants.CommonConstants.BatteryStatus;
import com.aimir.constants.CommonConstants.ModemPowerType;
import com.aimir.constants.CommonConstants.ModemType;
import com.aimir.dao.device.ModemDao;
import com.aimir.dao.system.LocationDao;
import com.aimir.dao.system.SupplierDao;
import com.aimir.model.system.Location;
import com.aimir.model.system.Supplier;
import com.aimir.service.device.BatteryLogManager;
import com.aimir.service.device.bean.BatteryLogData;
import com.aimir.util.CommonUtils2;
import com.aimir.util.DecimalUtil;
import com.aimir.util.StringUtil;
import com.aimir.util.TimeLocaleUtil;

@WebService(endpointInterface = "com.aimir.service.device.BatteryLogManager")
@Service(value="batteryLogManager")
public class BatteryLogManagerImpl implements BatteryLogManager{

    Log logger = LogFactory.getLog(BatteryLogManagerImpl.class);
    	
	@Autowired
	ModemDao modemDao;
	@Autowired
	LocationDao locationDao;
	@Autowired
	SupplierDao supplierDao;

	/**
	 * ModemType 콤보 조회
	 */
    public List<Object> getModemTypeCombo() {
        List<Object> resultList = new ArrayList<Object>();
       
        Map<String, Object> resultMap = new HashMap<String, Object>();
        
        resultMap = new HashMap<String, Object>();
        resultMap.put("id", ModemType.Repeater);
        resultMap.put("name", ModemType.Repeater.name());
        resultList.add(resultMap);
        
        resultMap = new HashMap<String, Object>();
        resultMap.put("id", ModemType.ZEU_PLS);
        resultMap.put("name", ModemType.ZEU_PLS.name());
        resultList.add(resultMap);

        return resultList;
    }

	/**
	 * PowerType 콤보 조회
	 */
    public List<Map<String, Object>> getPowerTypeCombo() {
        ModemPowerType[] types = ModemPowerType.values();

        List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
        Map<String, Object> resultMap = null;

        for (ModemPowerType type : types) {
            resultMap = new HashMap<String, Object>();
            resultMap.put("id", type.getCode());
            resultMap.put("name", type.name());
            resultList.add(resultMap);
        }
        
        Collections.sort(resultList, new Comparator<Map<String,Object>>(){

			@Override
			public int compare(Map<String, Object> o1, Map<String, Object> o2) {
				 String firstValue =  (String) o1.get("name");
				 String secondValue = (String) o2.get("name");
			    return firstValue.compareToIgnoreCase(secondValue);
			}
        	
        });
        return resultList;
    }

	/**
	 * Battery Log Mini Gadget 조회
	 * 또는 Max Gadget 의 파이차트 조회
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Object> getBatteryLog(Map<String, Object> condition) {
		
		// Mini Gadget 조회시에만 데이터 타입 변경하여 condition에 적용
		// Max Gadget 에서 호출 시는 기 적용된 condition 이 넘어온다.
		if(condition.get("batteryVoltSign") == null){
			String strSupplierId = (String)condition.get("supplierId");
			Integer supplierId = 0;
			if(!"".equals(StringUtil.nullToBlank(strSupplierId))){
				supplierId = Integer.parseInt(strSupplierId);
			}
			condition.put("supplierId", supplierId);
			
			String strBatteryStatus = (String)condition.get("batteryStatus");
			Integer batteryStatus = null;
			if(!"".equals(StringUtil.nullToBlank(strBatteryStatus))){
				batteryStatus = Integer.parseInt(strBatteryStatus);
				condition.put("batteryStatus", batteryStatus);
			}
			else{
				condition.put("batteryStatus", 0);
			}
		}

		List<Object> grid = modemDao.getBatteryLog(condition);

		String status = null;
		long total = 0;
		long normal = 0;
		long abnormal = 0;
		long replacement = 0;
		long unknown = 0;

		// 배터리 상태 값에 따라 모뎀수 설정
		Map<String, Object> row = null;
		for(Object obj:grid){
			row = (Map<String, Object>)obj;
			total += ((Number)row.get("statusCount")).intValue();
			if(row.get("batteryStatus") != null){
				status = (String)row.get("batteryStatus");
				
				if(status.equals(BatteryStatus.Normal.name())){
					normal = ((Number)row.get("statusCount")).intValue();
				}
				else if(status.equals(BatteryStatus.Abnormal.name())){
					abnormal = ((Number)row.get("statusCount")).intValue();
				}
				else if(status.equals(BatteryStatus.Replacement.name())){
					replacement = ((Number)row.get("statusCount")).intValue();
				}
				else{
					unknown = ((Number)row.get("statusCount")).intValue();
				}
			}
			else {
				unknown = ((Number)row.get("statusCount")).intValue();
			}
		}

		Map<String, Object> result = new HashMap<String, Object>();
		result.put("total", total);
		result.put("normal", normal);
		result.put("abnormal", abnormal);
		result.put("replacement", replacement);
		result.put("unknown", unknown);

		return result;
	}

	/**
	 * Battery Log Max Gadget 조회
	 */
    public Map<String, Object> getBatteryLogList(Map<String, Object> condition) {

        Map<String, Object> result = new HashMap<String, Object>();

        String strSupplierId = (String) condition.get("supplierId");
        Integer supplierId = 0;
        if (!"".equals(StringUtil.nullToBlank(strSupplierId))) {
            supplierId = Integer.parseInt(strSupplierId);
        }
        condition.put("supplierId", supplierId);

        List<Integer> locations = null;
        if (!condition.get("meterLocation").equals("")) {
            locations = locationDao.getLeafLocationId(Integer.parseInt((String) condition.get("meterLocation")),
                    Integer.parseInt(strSupplierId));
        }

        condition.put("meterLocation", locations);
        String strBatteryStatus = (String) condition.get("batteryStatus");
        Integer batteryStatus = null;
        if (!"".equals(StringUtil.nullToBlank(strBatteryStatus))) {
            batteryStatus = Integer.parseInt(strBatteryStatus);
            condition.put("batteryStatus", batteryStatus);
        } else {
            condition.put("batteryStatus", 0);
        }

        String modemType = StringUtil.nullToBlank((String) condition.get("modemType"));
        List<String> modemTypes = new ArrayList<String>();

        // 모뎀타입을 선택하지 않았을 경우 각 모뎀타입에 대한 파이차트 데이터만 조회
        if ("".equals(modemType)) {
            modemTypes.add(CommonConstants.ModemType.ZEU_PLS.toString());
            modemTypes.add(CommonConstants.ModemType.Repeater.toString());

            condition.put("modemType", CommonConstants.ModemType.ZEU_PLS.toString());
            result.put("chart1", getBatteryLog(condition));
            condition.put("modemType", CommonConstants.ModemType.Repeater.toString());
            result.put("chart2", getBatteryLog(condition));
        }
        // 모뎀타입 선택 시 각 모뎀타입에 대한 파이차트, 컬럼차트 데이터 조회
        else {
            modemTypes.add(modemType);

            // 모뎀타입별 파이차트 데이터 조회
            result.put("chart1", getBatteryLog(condition));
            // 지역별 컬럼차트 데이터 조회
            String location = (String) condition.get("meterLocation");
            result.put("chart2", getBatteryLogByLocation(condition, modemType, supplierId, location));
        }
        condition.put("modemTypes", modemTypes);

        // Max Gadget 그리드 데이터 조회
        List<Object> resultList = modemDao.getBatteryLogList(condition);
        
        BatteryLogData data = null;
        List<BatteryLogData> dataList = new ArrayList<BatteryLogData>();

        Supplier supplier = supplierDao.get(supplierId);

        DecimalFormat df = DecimalUtil.getDecimalFormat(supplier.getMd());

        for (Object obj : resultList) {
            int i = 0;
            Object[] objs = (Object[]) obj;

            data = new BatteryLogData();
            data.setBatteryStatus(StringUtil.nullToBlank(objs[i++]));
            data.setModemId(StringUtil.nullToBlank(objs[i++]));
            data.setModemType(StringUtil.nullToBlank(objs[i++]));
            data.setModem(StringUtil.nullToBlank(objs[i++]));
            data.setPowerType(StringUtil.nullToBlank(objs[i++]));
            data.setMeterLocation(StringUtil.nullToBlank(objs[i++]));

            Object batteryVolt = objs[i++];
            if (batteryVolt == null) {
                data.setBatteryVolt("");
            } else {
                data.setBatteryVolt(df.format(batteryVolt));
            }
            Number operatingDay = (Number) objs[i++];
            data.setOperatingDay(operatingDay == null ? null : operatingDay.intValue());
            
            data.setCheckTime(TimeLocaleUtil.getLocaleDate(StringUtil.nullToBlank(objs[i++]), supplier.getLang()
                    .getCode_2letter(), supplier.getCountry().getCode_2letter()));
            Number activeTime = (Number) objs[i++];
            data.setActiveTime(activeTime == null ? null : activeTime.intValue());
            Number resetCount = (Number) objs[i++];
            data.setResetCount(resetCount == null ? null : resetCount.intValue());

            dataList.add(data);
        }
        result.put("grid", dataList);
     
        return result;
    }
    
    public ArrayList<Map<String,Object>> getBatteryVoltageLogList(Map<String,Object> condition){
    	
    	 ArrayList<Map<String,Object>> resultList = new ArrayList<Map<String,Object>>();
    	 Map<String, Object> result;
    	 Map<String, Object> griddata = getBatteryLogList(condition);
    	 List<BatteryLogData> data = (List<BatteryLogData>) griddata.get("grid");
    	
    	 Integer supplierId = Integer.parseInt(condition.get("supplierId").toString());
    	 Supplier supplier = supplierDao.get(supplierId);
    	 
    	 String curPage = String.valueOf(condition.get("page"));
    	 String pageSize = String.valueOf(condition.get("pageSize"));
    	 
    	 DecimalFormat dfMd = DecimalUtil.getMDStyle(supplier.getMd());
    	 
    	 int cnt = 1;
    	 for(BatteryLogData batterylogdata :data){
    		 result = new HashMap<String, Object>();
    		 result.put("no", dfMd.format(CommonUtils2.makeIdxPerPage(curPage, pageSize, cnt)));
    		 result.put("batteryStatus", batterylogdata.getBatteryStatus());
    		 result.put("modemId", batterylogdata.getModemId());
    		 if("".equals(batterylogdata.getModemType())){
    			 result.put("displayData", batterylogdata.getModem());
    		 }else{
    			 result.put("displayData", batterylogdata.getModemType());
    		 }
    		 result.put("powerType", batterylogdata.getPowerType());
    		 result.put("meterLoction", batterylogdata.getMeterLocation());
    		 result.put("checkTime", batterylogdata.getCheckTime());
    		 result.put("batteryVolt", batterylogdata.getBatteryVolt());
    		 result.put("operatingDay", batterylogdata.getOperatingDay());
    		 result.put("activeTime", batterylogdata.getActiveTime());
    		 result.put("resetCount", batterylogdata.getResetCount() == null ? "" : dfMd.format(Double.parseDouble(batterylogdata.getResetCount().toString())));
    		 resultList.add(result);
    		 cnt++;
    	 }
    	 return resultList;
    }
    
    /**
     * Battery Log Max Gadget Total Count 조회
     */
    public Map<String, Object> getBatteryLogListTotalCount(Map<String, Object> condition) {

        Map<String, Object> result = new HashMap<String, Object>();

        String strSupplierId = (String)condition.get("supplierId");
        Integer supplierId = 0;
        if (!"".equals(StringUtil.nullToBlank(strSupplierId))) { 
            supplierId = Integer.parseInt(strSupplierId);
        }
        condition.put("supplierId", supplierId);

        List<Integer> locations = null;
        if (!condition.get("meterLocation").equals("")) {
            locations = locationDao.getLeafLocationId(Integer.parseInt((String) condition.get("meterLocation")),
                    Integer.parseInt(strSupplierId));
        }

        condition.put("meterLocation", locations);
        String strBatteryStatus = (String) condition.get("batteryStatus");
        Integer batteryStatus = null;
        if (!"".equals(StringUtil.nullToBlank(strBatteryStatus))) {
            batteryStatus = Integer.parseInt(strBatteryStatus);
            condition.put("batteryStatus", batteryStatus);
        } else {
            condition.put("batteryStatus", 0);
        }

        String modemType = StringUtil.nullToBlank((String) condition.get("modemType"));
        List<String> modemTypes = new ArrayList<String>();

        // 모뎀타입을 선택하지 않았을 경우 각 모뎀타입에 대한 파이차트 데이터만 조회
        if ("".equals(modemType)) {
            modemTypes.add(CommonConstants.ModemType.ZEU_PLS.toString());
            modemTypes.add(CommonConstants.ModemType.Repeater.toString());
        }
        // 모뎀타입 선택 시 각 모뎀타입에 대한 파이차트, 컬럼차트 데이터 조회
        else {
            modemTypes.add(modemType);
        }
        condition.put("modemTypes", modemTypes);

        // Max Gadget 그리드 Total Count 조회
        List<Object> resultList = modemDao.getBatteryLogList(condition, true);

        result.put("total", resultList.get(0));

        return result;
    }

	/**
	 * 지역별로 배터리 상태별 모뎀수를 계산한다.
	 * @param modemType
	 * @param supplierId
	 * @param location
	 * @return
	 */
	private List<Object> getBatteryLogByLocation(Map<String, Object> condition, String modemType, Integer supplierId, String location) {

		int locationId = 0;
		List<Location> locations = null;
		if(location != null){
			locations = locationDao.getLocationByName(location);
			if(locations.size() > 0){
				locationId = locations.get(0).getId();
			}
		}
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
		
//		Map<String, Object> condition = new HashMap<String, Object>();
//		condition.put("modemType", modemType);
//		condition.put("supplierId", supplierId);

		List<Object> returnList = new ArrayList<Object>();
		Map<String, Object> result = null;
		
		for(Location loc:locations){
			condition.put("locationId", loc.getId());
			// getLogDataByLocation 재귀함수 호출
			result = getLogDataByLocation(condition, loc.getChildren());

			result.put("location", loc.getName());
			returnList.add(result);
		}

		// meterLocation 이 없는 경우(Unknown) 조회
		condition.put("locationId", null);
		result = getLogDataByLocation(condition, null);
		result.put("location", "Unknown");
		returnList.add(result);

		return returnList;
	}

	/**
	 * location 에 하위지역이 존재하면 가장 최하위 location 까지 재귀적으로 호출됨. 
	 * @param condition 조회조건
	 * @param locations 하위지역 Location 리스트
	 * @return
	 */
	private Map<String, Object> getLogDataByLocation(Map<String, Object> condition, Set<Location> locations){

		Map<String, Object> result = new HashMap<String, Object>();
		int normal = 0;
		int abnormal = 0;
		int replacement = 0;
		int unknown = 0;

		Object[] objs = null;
		String batteryStatus = null;
		
		// 하위지역이 존재하지 않을경우 해당 배터리상태 별 모뎀수를 계산한다.
		if(locations == null || locations.size() < 1){
			List<Object> grid = modemDao.getBatteryLogByLocation(condition);
			
			for(Object obj:grid){
				objs = (Object[])obj;
				if(objs[3] != null){
					batteryStatus = (String)objs[3];
				}
				
				if(batteryStatus != null){
					if(batteryStatus.equals(BatteryStatus.Normal.name())){
						normal++;
					}
					else if(batteryStatus.equals(BatteryStatus.Abnormal.name())){
						abnormal++;
					}
					else if(batteryStatus.equals(BatteryStatus.Replacement.name())){
						replacement++;
					}
					else{
						unknown++;
					}
				}
				else{
					unknown++;
				}
			}
			
			result.put("normal", normal);
			result.put("abnormal", abnormal);
			result.put("replacement", replacement);
			result.put("unknown", unknown);
		}
		// 하위지역이 존재할경우 재귀호출하여 조회하고 배터리상태 별 모뎀수를 합산한다.
		else{
			for(Location loc:locations){
				condition.put("locationId", loc.getId());
				result = getLogDataByLocation(condition, loc.getChildren());
				
				normal      += ((Number)result.get("normal")).intValue();
				abnormal    += ((Number)result.get("abnormal")).intValue();
				replacement += ((Number)result.get("replacement")).intValue();
				unknown     += ((Number)result.get("unknown")).intValue();
			}
			
			result.put("normal", normal);
			result.put("abnormal", abnormal);
			result.put("replacement", replacement);
			result.put("unknown", unknown);
		}
		
		return result;
	}
    
    /**
     * Max Gadget 조회후 그리드데이터 선택시 해당 모뎀의 배터리로그 상세조회
     * 
     * @param condition
     * @return
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> getBatteryLogDetailList(Map<String, Object> condition) {
        Map<String, Object> result = new HashMap<String, Object>();
        String strSupplierId = (String) condition.get("supplierId");
        Integer supplierId = 0;
        if (!"".equals(StringUtil.nullToBlank(strSupplierId))) {
            supplierId = Integer.parseInt(strSupplierId);
        }
        condition.put("supplierId", supplierId);

        List<Object> grid = modemDao.getBatteryLogDetailList(condition);
        List<Object> dataList = new ArrayList<Object>();
        Supplier supplier = supplierDao.get(supplierId);
        String lang = supplier.getLang().getCode_2letter();
        String country = supplier.getCountry().getCode_2letter();
        DecimalFormat df = DecimalUtil.getDecimalFormat(supplier.getMd());

        for (Object obj : grid) {
            Map<String, Object> map = (HashMap<String, Object>) obj;
            map.put("date", TimeLocaleUtil.getLocaleDate((String) map.get("date") + (String) map.get("time"), lang, country));
            map.put("decimalVoltCurrent", df.format(Double.parseDouble(StringUtil.nullToZero(map.get("voltCurrent")))));
            map.put("decimalBatteryVolt", df.format(Double.parseDouble(StringUtil.nullToZero(map.get("batteryVolt")))));
            map.put("decimalVoltOffset", df.format(Double.parseDouble(StringUtil.nullToZero(map.get("voltOffset")))));
            dataList.add(map);
        }

        result.put("grid", dataList);
        return result;
    }

    /**
     * Max Gadget 조회후 그리드데이터 선택시 해당 모뎀의 배터리로그 상세 데이터 Total Count 조회
     * 
     * @param condition
     * @return
     */
    public Map<String, Object> getBatteryLogDetailListTotalCount(Map<String, Object> condition) {
        Map<String, Object> result = new HashMap<String, Object>();
        String strSupplierId = (String) condition.get("supplierId");
        Integer supplierId = 0;
        if (!"".equals(StringUtil.nullToBlank(strSupplierId))) {
            supplierId = Integer.parseInt(strSupplierId);
        }
        condition.put("supplierId", supplierId);

        List<Object> list = modemDao.getBatteryLogDetailList(condition, true);
        result.put("total", list.get(0));
        return result;
    }

	public Map<String, Object> getBatteryLog(String supplierId,
			String modemType, String modemTypeName) {
		Map<String, Object> condition = new HashMap<String, Object>();
	    condition .put("supplierId", supplierId);
	    condition .put("modemType", modemType);
	    condition .put("modemTypeName", modemTypeName);
	    return getBatteryLog(condition);
	}

	public Map<String, Object> getBatteryLogByParam(String supplierId,
			String modemType, String modemTypeName) {
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("supplierId", supplierId);
		condition.put("modemType", modemType);
		condition.put("modemTypeName", modemTypeName);
		return getBatteryLog(condition);
	}

	public Map<String, Object> getBatteryLogListByParam(String supplierId,
			String modemType, String modemTypeName, String modemId,
			String powerType, String meterLocation, String batteryStatus,
			String batteryVoltSign, String batteryVolt,
			String operatingDaySign, String operatingDay) {
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("supplierId", supplierId);
		condition.put("modemType", modemType);
		condition.put("modemTypeName", modemTypeName);
		condition.put("modemId", modemId);
		condition.put("powerType", powerType);
		condition.put("meterLocation", meterLocation);
		condition.put("batteryStatus", batteryStatus);
		condition.put("batteryVoltSign", batteryVoltSign);
		condition.put("batteryVolt", batteryVolt);
		condition.put("operatingDaySign", operatingDaySign);
		condition.put("operatingDay", operatingDay);
		return getBatteryLogList(condition);
	}

	public Map<String, Object> getBatteryLogDetailListByParam(
			String supplierId, String modemType, String modemId,
			String dateType, String fromDate, String toDate) {
		Map<String, Object> condition = new HashMap<String, Object>();
	    condition .put("supplierId", supplierId);
	    condition .put("modemType", modemType);
	    condition .put("modemId", modemId);
	    condition .put("dateType", dateType);
	    condition .put("fromDate", fromDate);
	    condition .put("toDate", toDate);
	    return getBatteryLogDetailList(condition);
	}
}
