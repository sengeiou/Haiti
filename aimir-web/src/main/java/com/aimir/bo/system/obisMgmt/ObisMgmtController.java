package com.aimir.bo.system.obisMgmt;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate4.HibernateTransactionManager;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.TransactionStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.aimir.constants.CommonConstants.DLMSDataType;
import com.aimir.constants.CommonConstants.MeterEventKind;
import com.aimir.fep.bypass.dlms.enums.Unit;
import com.aimir.fep.meter.parser.DLMSKaifaTable.DLMSVARIABLE;
import com.aimir.model.device.MeterEvent;
import com.aimir.model.system.Code;
import com.aimir.model.system.DeviceModel;
import com.aimir.model.system.OBISCode;
import com.aimir.service.device.MeterEventLogManager;
import com.aimir.service.system.CodeManager;
import com.aimir.service.system.DeviceModelManager;
import com.aimir.service.system.ObisCodeManager;
import com.aimir.service.system.RoleManager;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@Controller
public class ObisMgmtController {
	
	Log logger = LogFactory.getLog(ObisMgmtController.class);
	
    @Autowired
    HibernateTransactionManager transactionManager;
	
	@Autowired
	CodeManager codeManager;
	
	@Autowired
	RoleManager roleManager;
	
	@Autowired
	ObisCodeManager obisCodeManager;
	
	@Autowired
	DeviceModelManager modelManager;
	
	@Autowired
	MeterEventLogManager meterEventLogManager;
	
	@RequestMapping(value="/gadget/system/getObisCode.do")
	public ModelAndView getObisCode(
			@RequestParam Integer modelId,
			@RequestParam (value="obisCode", required=false) String obisCode,
			@RequestParam (value="classId", required=false) String classId,
			@RequestParam (value="attributeNo", required=false) String attributeNo) {
		ModelAndView mav = new ModelAndView("jsonView");
		
		Map<String,Object> condition = new HashMap<String,Object>();
		condition.put("modelId", modelId);
		condition.put("obisCode", obisCode);
		condition.put("classId", classId);
		condition.put("attributeNo", attributeNo);
		
		List<Map<String,Object>> resultData = obisCodeManager.getObisCodeInfo(condition);
		
		mav.addObject("result", resultData);
		mav.addObject("totalCnt", resultData.size());
		
		return mav;
	}
	
	@RequestMapping(value="/gadget/system/getObisCodeInfo.do")
	public ModelAndView getObisCodeInfo(
			@RequestParam Integer modelId,
			@RequestParam (value="obisCodeId", required=false) Long obisCodeId,
			@RequestParam (value="obisCode", required=false) String obisCode,
			@RequestParam (value="classId", required=false) String classId,
			@RequestParam (value="attributeNo", required=false) String attributeNo,
            @RequestParam(value="page", required=false) Integer page,
            @RequestParam(value="limit", required=false) Integer limit) {
		ModelAndView mav = new ModelAndView("jsonView");
		
		Map<String,Object> condition = new HashMap<String,Object>();
		condition.put("modelId", modelId);
		condition.put("obisCodeId", obisCodeId);
		condition.put("obisCode", obisCode);
		condition.put("classId", classId);
		condition.put("attributeNo", attributeNo);
		condition.put("page", page);
		condition.put("limit", limit);
		condition.put("isCount", false);
		List<Map<String,Object>> resultData = obisCodeManager.getObisCodeWithEvent(condition);
		
		condition.put("isCount", true);
		List<Map<String,Object>> totalCountData = obisCodeManager.getObisCodeWithEvent(condition);
		
		int size = 0;
		if(totalCountData.size() > 0) {
			size = Integer.parseInt(totalCountData.get(0).get("TOTALCNT").toString());
		}
		
		mav.addObject("result", resultData);
		mav.addObject("totalCnt", size);
		
		return mav;
	}
	
	@RequestMapping(value="/gadget/system/getObisCodeGroup.do")
	public ModelAndView getObisCodeGroup(
			@RequestParam Integer modelId,
			@RequestParam (value="obisCodeId", required=false) Long obisCodeId,
			@RequestParam (value="obisCode", required=false) String obisCode,
			@RequestParam (value="classId", required=false) String classId,
			@RequestParam (value="attributeNo", required=false) String attributeNo) {
		ModelAndView mav = new ModelAndView("jsonView");
		
		Map<String,Object> condition = new HashMap<String,Object>();
		condition.put("modelId", modelId);
		condition.put("obisCodeId", obisCodeId);
		condition.put("obisCode", obisCode);
		condition.put("classId", classId);
		condition.put("attributeNo", attributeNo);
		
		List<Map<String,Object>> resultData = obisCodeManager.getObisCodeGroup(condition);
		
		mav.addObject("result", resultData);
		mav.addObject("totalCnt", resultData.size());
		
		return mav;
	}
	
	@RequestMapping(value="/gadget/system/checkDuplicate.do")
	public ModelAndView checkDuplidate(
			@RequestParam Integer modelId,
			@RequestParam String obisCode,
			@RequestParam String classId,
			@RequestParam String attributeNo,
			@RequestParam String accessRight) {
		ModelAndView mav = new ModelAndView("jsonView");
		String result = "useable";
		
		try{
			Map<String,Object> condition = new HashMap<String,Object>();
			condition.put("modelId", modelId);
			condition.put("obisCode",obisCode);
			condition.put("classId",classId);
			condition.put("attributeNo",attributeNo);
			condition.put("accessRight",accessRight);
			Integer cnt = obisCodeManager.getCheckDuplidate(condition);
			if(cnt > 0) {
				result = "duplicate";
			}
		} catch(Exception e) {
			logger.error(e,e);
			result="fail";
		}
		
		mav.addObject("result",result);
		
		return mav;
	}
	
	@RequestMapping(value="/gadget/system/saveObisCodes.do")
	public ModelAndView saveObisCodes(
			@RequestParam String saveObisArr) {
		ModelAndView mav = new ModelAndView("jsonView");
		String str = "fail";
		List<MeterEvent> saveMeterEvent = new ArrayList<MeterEvent>();
		TransactionStatus txStatus = null;
		try {
			txStatus = transactionManager.getTransaction(null);
	        JSONArray jsonArr = null;
	        if(saveObisArr == null || saveObisArr.isEmpty()) {
	        	jsonArr = new JSONArray();
	        } else {
	        	jsonArr = JSONArray.fromObject(saveObisArr);
	        }
	        List<OBISCode> saveObisList = new ArrayList<OBISCode>();
	        
	        int jsonArrSize = jsonArr.size();
	        for (int i = 0; i < jsonArrSize; i++) {
	        	OBISCode obisCode = new OBISCode();
				JSONArray subJsonArr = jsonArr.getJSONArray(i);
				for (int j = 0; j < subJsonArr.size(); j++) {
					Code meterCode = null;
					String meterEventCk = null;
					JSONObject jsonObj = subJsonArr.getJSONObject(j);
					Iterator it = jsonObj.keys();
					while(it.hasNext()) {
						String key = it.next().toString();
						if(key.equals("obisCode")) {
							obisCode.setObisCode(jsonObj.getString(key));
						}else if(key.equals("className")) {
							String className = jsonObj.getString(key);
							obisCode.setClassName("".equals(className) ? null : className);
						}else if(key.equals("classId")) {
							obisCode.setClassId(jsonObj.getString(key));
						}else if(key.equals("attributeName")) {
							String attributeName = jsonObj.getString(key);
							obisCode.setAttributeName("".equals(attributeName) ? null : attributeName);
						}else if(key.equals("attributeNo")) {
							obisCode.setAttributeNo(jsonObj.getString(key));
						}else if(key.equals("dataType")) {
							String dataType = jsonObj.getString(key);
							obisCode.setDataType("-".equals(dataType) || "".equals(dataType) || "null".equals(dataType) ? null : dataType);
						}else if(key.equals("accessRight")) {
							String access = jsonObj.getString(key);
							obisCode.setAccessRight("-".equals(access) || "".equals(access) || "null".equals(access) ? null : access);
						}else if(key.equals("descr")) {
							String descr = jsonObj.getString(key);
							obisCode.setDescr("".equals(descr) ? null : descr);
						}else if(key.equals("modelId")) {
							DeviceModel model = modelManager.getDeviceModel(Integer.parseInt(jsonObj.getString(key)));
							obisCode.setModel(model);
						}else if(key.equals("meterEvent")) {
							meterEventCk = jsonObj.getString(key);
						}else if(key.equals("meterType")) {
							meterCode = codeManager.getCode(Integer.parseInt(jsonObj.getString(key)));
						}
					}
					
					String modelName = obisCode.getModel().getName();
					String vendorName = obisCode.getModel().getDeviceVendor().getName();
					MeterEvent meterEvent = new MeterEvent();
					String kind = MeterEventKind.STE.name();
					String obisCodeStr = obisCode.getObisCode();
					
					Map<String,Object> conditionMap = new HashMap<String,Object>();
		    		conditionMap.put("id", kind+"."+vendorName+"."+modelName+"."+obisCodeStr);
		    		MeterEvent meterEventExist = meterEventLogManager.getMeterEventByCondition(conditionMap);
					if("Y".equals(meterEventCk)) {
			    		if(meterEventExist == null || meterEventExist.getId() == null) {
			    			meterEvent.setId(kind+"."+vendorName+"."+modelName+"."+obisCodeStr);
							meterEvent.setKind(kind);
							meterEvent.setDescr(obisCode.getDescr());
							meterEvent.setMeterType(meterCode.getName());
							meterEvent.setModel(modelName);
							meterEvent.setName(obisCode.getDescr());
							meterEvent.setSupport(true);
							meterEvent.setValue(obisCode.getObisCode());
							meterEvent.setVendor(vendorName);
							saveMeterEvent.add(meterEvent);
			    		} else {
			    			//기존에 있는 meterEvent로 Descr를 저장.
			    			obisCode.setDescr(meterEventExist.getName());
			    		}
			    		
					} else { 
						//기존에 있는 meterEvent로 Descr를 저장.
			    		if(meterEventExist != null && meterEventExist.getId() != null) {
			    			obisCode.setDescr(meterEventExist.getName());
			    		}
					}
					saveObisList.add(obisCode);
					
				}
			}
    		
	        obisCodeManager.add(saveObisList);
	        transactionManager.commit(txStatus);
	        
	        str = "success";
		} catch (Exception e) {
			str = "fail";
			logger.error(e,e);
			if (transactionManager != null) {
				transactionManager.rollback(txStatus);
	        }
			mav.addObject("result",str);
			return mav;
		}
		
		txStatus = null;
		try{
			txStatus = transactionManager.getTransaction(null);
			int size = saveMeterEvent.size();
	        for (int i = 0; i < size; i++) {
	        	meterEventLogManager.add(saveMeterEvent.get(i));
			}
	        transactionManager.commit(txStatus);
		} catch(Exception e) {
			str = "eventSavefail";
			logger.error(e,e);
			if (transactionManager != null) {
				try{
					transactionManager.rollback(txStatus);
				}catch(Exception e1) {
					logger.error(e1,e1);
				}
	        }
		}
		
        mav.addObject("result",str);
        
		return mav;
	}
	
	@RequestMapping(value="/gadget/system/updateObisCode.do")
	public ModelAndView updateObisCodes(
			@RequestParam String updateObisArr,
			@RequestParam(required=false, value="modelId") Integer modelId,
			@RequestParam(required=false, value="roleId") Integer roleId) {
		ModelAndView mav = new ModelAndView("jsonView");
		String str = "success";
		OBISCode obisCode = new OBISCode();
		OBISCode oldObisCode = null;
		MeterEvent meterEvent = null;
    	Code meterCode = null;
		String meterEventCk = null;
		try{
			JSONObject jsonObj = JSONObject.fromObject(updateObisArr);
			Iterator it = jsonObj.keys();
			while(it.hasNext()) {
				String key = it.next().toString();
				if(key.equals("ID")) {
					obisCode.setId(Long.parseLong(jsonObj.getString(key)));
				}else if(key.equals("OBISCODE")) {
					obisCode.setObisCode(jsonObj.getString(key));
				}else if(key.equals("CLASSNAME")) {
					String className = jsonObj.getString(key);
					obisCode.setClassName("".equals(className) ? null : className);
				}else if(key.equals("CLASSID")) {
					obisCode.setClassId(jsonObj.getString(key));
				}else if(key.equals("ATTRIBUTENAME")) {
					String attributeName = jsonObj.getString(key);
					obisCode.setAttributeName("".equals(attributeName) ? null : attributeName);
				}else if(key.equals("ATTRIBUTENO")) {
					obisCode.setAttributeNo(jsonObj.getString(key));
				}else if(key.equals("DATATYPE")) {
					String dataType = jsonObj.getString(key);
					obisCode.setDataType("-".equals(dataType) || "".equals(dataType) || "null".equals(dataType) ? null : dataType);
				}else if(key.equals("ACCESSRIGHT")) {
					String access = jsonObj.getString(key);
					obisCode.setAccessRight("-".equals(access) || "".equals(access) || "null".equals(access) ? null : access);
				}else if(key.equals("DESCR")) {
					String descr = jsonObj.getString(key);
					obisCode.setDescr("".equals(descr) ? null : descr);
				}else if(key.equals("MODELID")) {
					DeviceModel model = modelManager.getDeviceModel(Integer.parseInt(jsonObj.getString(key)));
					obisCode.setModel(model);
				}else if(key.equals("METEREVENT")) {
					meterEventCk = jsonObj.getString(key);
				}else if(key.equals("METERTYPE")) {
					meterCode = codeManager.getCode(Integer.parseInt(jsonObj.getString(key)));
				}
			}
    		
			oldObisCode = obisCodeManager.getObisCode(obisCode.getId());
			
    		oldObisCode.setClassName(obisCode.getClassName());
    		oldObisCode.setClassId(obisCode.getClassId());
    		oldObisCode.setAttributeName(obisCode.getAttributeName());
    		oldObisCode.setAttributeNo(obisCode.getAttributeNo());
    		oldObisCode.setDataType(obisCode.getDataType() == null ? null : obisCode.getDataType().name());
    		oldObisCode.setAccessRight(obisCode.getAccessRight());
    		oldObisCode.setDescr(obisCode.getDescr());
			obisCodeManager.update(oldObisCode);
			
			str = "success";
		}catch(Exception e) {
			str = "fail";
			logger.error(e,e);
		}
		
		try{
			Map<String,Object> conditionMap = new HashMap<String,Object>();
    		String kind = MeterEventKind.STE.name();
    		String oldVendor = oldObisCode.getModel().getDeviceVendor().getName();
    		String oldModel = oldObisCode.getModel().getName();
    		String oldValue = oldObisCode.getObisCode();
    		conditionMap.put("id", kind+"."+oldVendor+"."+oldModel+"."+oldValue);
    		meterEvent = meterEventLogManager.getMeterEventByCondition(conditionMap);
    		
    		if(meterEvent == null ) {
    			if("Y".equals(meterEventCk)) {
    				meterEvent = new MeterEvent();
    				String modelName = obisCode.getModel().getName();
    				String vendorName = obisCode.getModel().getDeviceVendor().getName();
    				String obisCodeStr = obisCode.getObisCode();

    				meterEvent.setId(kind+"."+vendorName+"."+modelName+"."+obisCodeStr);
    				meterEvent.setKind(kind);
    				meterEvent.setDescr(obisCode.getDescr());
    				meterEvent.setMeterType(meterCode.getName());
    				meterEvent.setModel(modelName);
    				meterEvent.setName(obisCode.getDescr());
    				meterEvent.setSupport(true);
    				meterEvent.setValue(obisCode.getObisCode());
    				meterEvent.setVendor(vendorName);
    				meterEventLogManager.add(meterEvent);
    				
    				Map<String,Object> condition = new HashMap<String,Object>();
    				condition.put("obisCode",obisCode.getObisCode());
    				condition.put("descr",obisCode.getDescr());
    				condition.put("modelId",modelId);
    				obisCodeManager.updateDescr(condition);
    			}
    		} else {
    			if("Y".equals(meterEventCk)) {
        			if(!meterEvent.getName().equals(obisCode.getDescr())) {
        				meterEvent.setName(obisCode.getDescr());
            			meterEvent.setDescr(obisCode.getDescr());
            			meterEventLogManager.update(meterEvent);
            			
        				Map<String,Object> condition = new HashMap<String,Object>();
        				condition.put("obisCode",obisCode.getObisCode());
        				condition.put("descr",obisCode.getDescr());
        				condition.put("modelId",modelId);
        				obisCodeManager.updateDescr(condition);
                	}
        		} else {
        			meterEventLogManager.delete(meterEvent);
        		}
    		}
    		str = "success";
		} catch(Exception e) {
			str = "meterEventFail";
			logger.error(e,e);
		}
        
        mav.addObject("result",str);
		return mav;
	}
	
	@RequestMapping(value="/gadget/system/deleteObisCode.do")
	public ModelAndView deleteObisCodes(
			@RequestParam long obisCodeId,
			@RequestParam(required=false, value="modelId") Integer modelId) {
		ModelAndView mav = new ModelAndView("jsonView");
		String str = "fail";
		TransactionStatus txStatus = null;
        try{
        	txStatus = transactionManager.getTransaction(null);
    		Map<String,Object> condition = new HashMap<String,Object>();
    		condition.put("modelId", modelId);
    		condition.put("obisCodeId", obisCodeId);
    		OBISCode obisCode = obisCodeManager.getObisCode(obisCodeId);
    		
    		Map<String,Object> condition2 = new HashMap<String,Object>();
    		condition2.put("modelId", modelId);
    		condition2.put("obisCode", obisCode.getObisCode());
    		List<Map<String,Object>> obisList = obisCodeManager.getObisCodeInfo(condition2);
    		if(obisList != null && obisList.size() == 1) {
    			Map<String,Object> conditionMap = new HashMap<String,Object>();
        		String kind = MeterEventKind.STE.name();
        		String vendor = obisCode.getModel().getDeviceVendor().getName();
        		String model = obisCode.getModel().getName();
        		String value = obisCode.getObisCode();
        		conditionMap.put("id", kind+"."+vendor+"."+model+"."+value);
        		MeterEvent meterEvent = meterEventLogManager.getMeterEventByCondition(conditionMap);
        		if(meterEvent != null) {
        			if(meterEvent.getName().equals(obisCode.getDescr())) {
        				meterEventLogManager.delete(meterEvent);
        			}
        		}
    		}
    		
    		obisCodeManager.delete(obisCodeId);
    		
    		transactionManager.commit(txStatus);
    		str = "success";
        }catch(Exception e) {
        	str = "fail";
        	logger.error(e,e);
			if (transactionManager != null) {
				transactionManager.rollback(txStatus);
	        }
        }
        mav.addObject("result",str);
		return mav;
	}
	
	@RequestMapping(value="/gadget/system/getDataType.do")
	public ModelAndView getDataType() {
		ModelAndView mav = new ModelAndView("jsonView");
		List<Map<String,Object>> dataTypeList = new ArrayList<Map<String,Object>>();
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("code", "-");
		map.put("display", "-");
		map.put("name", "-");
		dataTypeList.add(map);
		for (DLMSDataType dataType : DLMSDataType.values()) {
			map = new HashMap<String,Object>();
			map.put("code", dataType.getCode());
			map.put("display", dataType.getName());
			map.put("name", dataType.name());
			dataTypeList.add(map);
		}
        mav.addObject("result",dataTypeList);
		return mav;
	}
	
	@RequestMapping(value="/gadget/system/getUnitForKaifa.do")
	public ModelAndView getUnit() {
		ModelAndView mav = new ModelAndView("jsonView");
		List<Map<String,Object>> unitList = new ArrayList<Map<String,Object>>();
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("code", "-");
		map.put("display", "-");
		map.put("name", "-");
		unitList.add(map);
		for (DLMSVARIABLE.UNIT unit : DLMSVARIABLE.UNIT.values()) {
			map = new HashMap<String,Object>();
			map.put("code", unit.getCode());
			map.put("display", unit.getName());
			map.put("name", unit.getName());
			unitList.add(map);
		}
        mav.addObject("result",unitList);
		return mav;
	}
}
