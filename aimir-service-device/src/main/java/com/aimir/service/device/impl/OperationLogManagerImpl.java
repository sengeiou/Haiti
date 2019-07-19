package com.aimir.service.device.impl;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.aimir.dao.device.EndDeviceDao;
import com.aimir.dao.device.OperationListDao;
import com.aimir.dao.device.OperationLogDao;
import com.aimir.dao.system.SupplierDao;
import com.aimir.model.device.OperationList;
import com.aimir.model.device.OperationLog;
import com.aimir.model.device.OperationLogChartData;
import com.aimir.model.system.Code;
import com.aimir.model.system.DeviceModel;
import com.aimir.model.system.DeviceVendor;
import com.aimir.model.system.Supplier;
import com.aimir.service.device.OperationLogManager;
import com.aimir.util.CommonUtils2;
import com.aimir.util.DecimalUtil;
import com.aimir.util.ReflectionUtils;
import com.aimir.util.StringUtil;
import com.aimir.util.TimeLocaleUtil;

@Service(value = "operationLogManager")
@Transactional(readOnly=false)
public class OperationLogManagerImpl implements OperationLogManager{
	private static Log logger = LogFactory.getLog(OperationLogManagerImpl.class);
	@Autowired
	public OperationLogDao operationLogDao;

	@Autowired
	public OperationListDao operationListDao;

	@Autowired
	public SupplierDao supplierDao;

	@Autowired
	public EndDeviceDao endDeviceDao;

	@Override
	public List<OperationLogChartData> getOperationLogMiniChartData(Integer supplier) {

		return operationLogDao.getOperationLogMiniChartData(supplier);
	}

	@Override
	public List<OperationLogChartData> getAdvanceGridData(Map<String, String> conditioMap) {

		return operationLogDao.getAdvanceGridData(conditioMap);
	}

	@Override
	public List<OperationLogChartData> getColumnChartData(Map<String, String> conditioMap) {

		return operationLogDao.getColumnChartData(conditioMap);
	}

	@Override
	public List<OperationLog> getGridData(Map<String, String> conditioMap) {

		return operationLogDao.getGridData(conditioMap);
	}

	@Override
	public List<Map<String, Object>> getGridData(Map<String, String> conditioMap, String supplierId) {
		List<Map<String, Object>> result = ReflectionUtils.getDefineListToMapList(operationLogDao.getGridData(conditioMap));
		
		Supplier supplier = supplierDao.get(Integer.parseInt(supplierId));
		DecimalFormat dfMd = DecimalUtil.getMDStyle(supplier.getMd());
		String curPage = String.valueOf( conditioMap.get("page"));
        String pageSize =  String.valueOf( conditioMap.get("pageSize"));
		int count=1;
		
		
		for(Map<String, Object> data: result) {
			data.put("no", dfMd.format(CommonUtils2.makeIdxPerPage(curPage, pageSize, count)));
			data.put("openTime_org", (String)data.get("yyyymmdd") + (String)data.get("hhmmss"));
			//data.put("openTime", TimeLocaleUtil.getLocaleDate((String)data.get("yyyymmdd") + (String)data.get("hhmmss"), supplier.getLang().getCode_2letter(), supplier.getCountry().getCode_2letter()));
			data.put("openTime", TimeLocaleUtil.getLocaleDate((String)data.get("yyyymmddhhmmss"), supplier.getLang().getCode_2letter(), supplier.getCountry().getCode_2letter()));
			data.put("accomplisher", data.get("userId"));
			Code typecode =  (Code) data.get("targetTypeCode");
			data.put("targetType", (typecode != null) ? typecode.getDescr() : "");
			String operatorType = (Integer)data.get("operatorType")==0?"System":"Operator";
			data.put("accomplishmentType", operatorType);
			Code operation = (Code)data.get("operationCommandCode");
			data.put("operation", (operation != null) ? operation.getDescr() : "");
			String operationStatus = (String)data.get("errorReason");

			if(operationStatus!=null && operationStatus.equalsIgnoreCase("success")){
                data.put("operationStatus", "Success");
            }else if(operationStatus!=null && operationStatus.equalsIgnoreCase("fail")){
				data.put("operationStatus", "Fail");
			}else{
				data.put("operationStatus", operationStatus);
			}


			count++;
		}
		return result;
	}

	@Override
	public String getOperationLogMaxGridDataCount(Map<String, String> conditionMap) {
		
		return operationLogDao.getOperationLogMaxGridDataCount(conditionMap);
	}

	@Override
	public void saveOperationLog(Supplier supplier, Code targetTypeCode, String targetName, String userId, Code operationCode, Integer status, String errorReason){

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        Calendar today = Calendar.getInstance();
        String currDateTime = sdf.format(today.getTime());

		OperationLog log = new OperationLog();

		log.setOperatorType(1);//operator
		log.setOperationCommandCode(operationCode);
		log.setYyyymmdd(currDateTime.substring(0,8));
		log.setHhmmss(currDateTime.substring(8,14));
		log.setYyyymmddhhmmss(currDateTime);
		log.setDescription("");
		log.setErrorReason(errorReason);
		log.setResultSrc("");
		log.setStatus(status);
		log.setTargetName(targetName);
		log.setTargetTypeCode(targetTypeCode);
		log.setUserId(userId);
		log.setSupplier(supplier);
		//logger.debug("operation log: "+log.toString());
		operationLogDao.add(log);
	}


	@Override
	public void updateOperation(String updateStr) {

		//item.id + "_" + item.level + ":";
		String[] valueArray = null;

		if(!"".equals(updateStr)) {

			valueArray = updateStr.split(":");
		}

		String id = null;
		String level = null;
		String[] tempArray = null;

		for(String value : valueArray) {

			tempArray = value.split("_");
			id = tempArray[0];
			level = tempArray[1];

			OperationList entity = operationListDao.get(Integer.valueOf(id));
			entity.setLevel(Integer.parseInt(level));

			operationListDao.update(entity);
		}
	}

	@Override
	public List<OperationList> getOperationGridData(int operationCodeId) {

		List<OperationList> rtnList = new ArrayList<OperationList>();
		List<OperationList> operationList = operationListDao.getOperationListByOperationCodeId(operationCodeId);

		String equipment = null;

		DeviceModel deviceModel = null;
		DeviceVendor deviceVendor = null;
		Code deviceType = null;
		Code device = null;

		for(OperationList operation : operationList) {

			deviceModel = operation.getModel();
			if(deviceModel == null) continue; // db에 모델이 음네?
			deviceVendor = deviceModel.getDeviceVendor();
			deviceType = operation.getDeviceTypeCode();
			device = deviceType.getParent().getParent();

			equipment = device.getName() + " > " + deviceType.getName() + " > " + deviceVendor.getName() + " > " + deviceModel.getName();

			operation.setEquipment(equipment);
			rtnList.add(operation);
		}

		return rtnList;
	}

	@Override
	public List<OperationList> getOperationListByConstraintId(int constraintId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void saveOperationLogByCustomer(Supplier supplier, Code targetTypeCode, String targetName, String userId, Code operationCode, Integer status, String errorReason, String description, String contractNumber){

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        Calendar today = Calendar.getInstance();
        String currDateTime = sdf.format(today.getTime());

		OperationLog log = new OperationLog();

		log.setOperatorType(3);//customer
		log.setOperationCommandCode(operationCode);
		log.setYyyymmdd(currDateTime.substring(0,8));
		log.setHhmmss(currDateTime.substring(8,14));
		log.setYyyymmddhhmmss(currDateTime); //
		log.setDescription(description);
		log.setErrorReason(errorReason);
		log.setResultSrc("");
		log.setStatus(status);
		log.setTargetName(targetName);
		log.setTargetTypeCode(targetTypeCode);
		log.setUserId(userId);
		log.setSupplier(supplier);
		log.setContractNumber(contractNumber);
		//logger.debug("operation log: "+log.toString());
		operationLogDao.add(log);
	}

	@Override
	public void deleteOperationLog(Long id) {
		operationLogDao.deleteById(id);
	}

    /**
     * method name : saveOperationLogByMeterCmd<b/>
     * method Desc : Meter Command 실행 후 Operation Log 를 저장한다.
     *
     * @param supplier
     * @param targetTypeCode
     * @param targetName
     * @param writeDate
     * @param userId
     * @param operationCode
     * @param description
     * @param contractNumber
     */
    @Override
	public void saveOperationLogByMeterCmd(Supplier supplier, Code targetTypeCode, String targetName, String writeDate,
            String userId, Code operationCode, String description, String contractNumber) {

        OperationLog log = new OperationLog();

        log.setOperatorType(1);// customer
        log.setOperationCommandCode(operationCode);
        log.setYyyymmdd(writeDate.substring(0, 8));
        log.setHhmmss(writeDate.substring(8, 14));
        log.setYyyymmddhhmmss(writeDate); //
        log.setDescription(description);
        // log.setErrorReason(errorReason);
        log.setResultSrc("");
        // log.setStatus(status);
        log.setTargetName(targetName);
        log.setTargetTypeCode(targetTypeCode);
        log.setUserId(userId);
        log.setSupplier(supplier);
        log.setContractNumber(contractNumber);
        // logger.debug("operation log: "+log.toString());
        operationLogDao.add(log);
    }

    @Override
    public List<Object> getOpeartionLogListExcel(Map<String, String> condition) {
        condition.put("excelList", "list");

        // 공급지역 검색 조건 시 - 최하위 노드 값 조회 및 설정
        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
        String supplierId = String.valueOf(condition.get("supplierId"));
        result = this.getGridData(condition, supplierId);

        List<Object> resultList = new ArrayList<Object>();
        HashMap<String,Object> resultMap = null;
        if(result.size() > 0) {
            Map<String,Object> tmp = null;
            for(Object obj:result) {
                tmp = new HashMap<String,Object>();
                tmp = (Map<String,Object>)obj;

                resultMap = new HashMap<String,Object>();
                resultMap.put("no"					, StringUtil.nullToBlank(tmp.get("no")));
                resultMap.put("openTime"			, StringUtil.nullToBlank(tmp.get("openTime")));
                resultMap.put("targetType"			, StringUtil.nullToBlank(tmp.get("targetType")));
                resultMap.put("targetName"			, StringUtil.nullToBlank(tmp.get("targetName")));
                resultMap.put("accomplishmentType"	, StringUtil.nullToBlank(tmp.get("accomplishmentType")));
                resultMap.put("accomplisher"		, StringUtil.nullToBlank(tmp.get("accomplishmentType")));
                resultMap.put("operation"			, StringUtil.nullToBlank(tmp.get("operation")));
                resultMap.put("operationStatus"		, StringUtil.nullToBlank(tmp.get("operationStatus")));
                resultMap.put("description"			, StringUtil.nullToBlank(tmp.get("description")));

                resultList.add(resultMap);
            }
        }

        return resultList;
    }
}