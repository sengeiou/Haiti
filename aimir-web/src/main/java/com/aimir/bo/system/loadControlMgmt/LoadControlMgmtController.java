/**
 * EnergyConsumptionSearchController.java Copyright NuriTelecom Limited 2011
 */
package com.aimir.bo.system.loadControlMgmt;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.owasp.esapi.ESAPI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.aimir.bo.system.operator.LoginController;
import com.aimir.constants.CommonConstants.DemandResponseEventOptOutStatus;
import com.aimir.constants.CommonConstants.HomeDeviceCategoryType;
import com.aimir.constants.CommonConstants.MeterType;
import com.aimir.constants.CommonConstants.HomeDeviceGroupName;
import com.aimir.constants.CommonConstants.ResultStatus;
import com.aimir.esapi.AimirAuthenticator;
import com.aimir.esapi.AimirUser;
import com.aimir.model.device.EndDevice;
import com.aimir.model.device.Modem;
import com.aimir.model.system.Contract;
import com.aimir.model.system.HomeGroup;
import com.aimir.model.system.Operator;
import com.aimir.model.system.OperatorContract;
import com.aimir.model.system.Supplier;
import com.aimir.schedule.command.CmdOperationUtil;
import com.aimir.service.device.EndDeviceManager;
import com.aimir.service.device.MCUManager;
import com.aimir.service.device.ModemManager;
import com.aimir.service.device.OperationLogManager;
import com.aimir.service.system.CodeManager;
import com.aimir.service.system.ContractManager;
import com.aimir.service.system.GroupMgmtManager;
import com.aimir.service.system.OperatorManager;
import com.aimir.service.system.SupplierManager;
import com.aimir.service.system.loadControlMgmt.LoadControlMgmtManager;
import com.aimir.service.system.homeDeviceMgmt.HomeDeviceMgmtManager;
import com.aimir.service.system.membership.OperatorContractManager;
import com.aimir.support.AimirFilePath;
import com.aimir.util.CalendarUtil;
import com.aimir.util.TimeLocaleUtil;

/**
 * HomeDeviceMgmtController.java Description 
 *
 * 
 * Date          Version     Author   Description
 * 2013. 3. 19.   v1.0       goodjob  Load Control 관리 Controller       
 *
 */
@Controller
public class LoadControlMgmtController {
    private final Log log = LogFactory.getLog(LoginController.class);

    @Autowired
    OperatorContractManager operatorContractManager;
    
    @Autowired
    OperatorManager operatorManager;
	
    @Autowired
	AimirFilePath aimirFilePath;

	@Autowired
	ModemManager modemManager;
	
	@Autowired
	EndDeviceManager endDeviceManager;

	@Autowired
	CodeManager codeManager;
	
	@Autowired
	GroupMgmtManager groupMgmtManager;
	
	@Autowired
	SupplierManager supplierManager;	

	@Autowired
	MCUManager mcuManager;
	
	@Autowired
	HomeDeviceMgmtManager homeDeviceMgmtManager;
	
	@Autowired
	LoadControlMgmtManager loadControlMgmtManager;	

    @Autowired
    OperatorManager userLoginManager;

    @Autowired
    OperationLogManager operationLogManager;
    
    @Autowired
    ContractManager contractManager;
    
    @Autowired
    CmdOperationUtil cmdOperationUtil;

    @RequestMapping(value="/gadget/loadControlMgmt/loadControlMgmtMini")
    public ModelAndView homeDeviceMgmtMini() {

        ModelAndView mav = new ModelAndView("/gadget/loadControlMgmt/loadControlMgmtMini");
        
		// ESAPI.setAuthenticator((Authenticator)new AimirAuthenticator());
        AimirAuthenticator instance = (AimirAuthenticator)ESAPI.authenticator();        
        AimirUser user = (AimirUser)instance.getUserFromSession();
        int operatorId = (int) user.getAccountId();

        Operator operator = operatorManager.getOperator(operatorId);
        List<OperatorContract> operatorContracts = operatorContractManager.getOperatorContractByOperator(operator);
        List<Contract> contracts = new ArrayList<Contract>();
    	String energyServiceType = MeterType.EnergyMeter.getServiceType();
        Contract contract;
        String serviceType;
        
        for (OperatorContract operatorContract : operatorContracts) {

        	contract = new Contract();
        	serviceType = "";

        	contract = operatorContract.getContract();
        	serviceType = contract.getServiceTypeCode().getCode();

            // 계약정보 선택 콤보박스 생성을 위해 명칭을 일시적으로 Contract#KeyNum 속성에 저장합니다.
            // Contract에 friendlyName속성을 추가 할지는 차후 결정(HEMS에서만 사용하는 속성이므로 굳이 Contract에 설정하지 않아도 될것같음)
            contract.setKeyNum(operatorContract.getFriendlyName());
        	if ( energyServiceType.equals(serviceType) 
        			&& operatorContract.getContractStatus() == 1 ) {
        		contracts.add(contract);
        	}
        }

        if(contracts.size() == 0) {
        	mav.addObject("isNotService",true);
        	return mav;
        } else {
        	mav.addObject("isNotService",false);
        }

        mav.addObject("contracts", contracts);
        mav.addObject("supplierId", contracts.get(0).getSupplier().getId());

        // 그리드 DR제어 버튼 생성을 위해서 DR Level을 카테코리별로 취득한다.
        int smartConcentId = codeManager.getCodeIdByCode(HomeDeviceCategoryType.SMART_CONCENT.getCode());
        int smartAppliacneId = codeManager.getCodeIdByCode(HomeDeviceCategoryType.SMART_APPLIANCE.getCode());


        mav.addObject("smartConcentDrList", homeDeviceMgmtManager.getHomeDeviceDrLevelByCondition(smartConcentId,""));
        mav.addObject("smartApplianceDrList", homeDeviceMgmtManager.getHomeDeviceDrLevelByCondition(smartAppliacneId,""));

        HomeGroup homeGroup = homeDeviceMgmtManager.getHomeGroupByMember(contracts.get(0).getContractNumber());
        if(homeGroup == null){
        	mav.addObject("homeGroupId", "0");
        } else {
            mav.addObject("homeGroupId", homeGroup.getId());
    		mav.addObject("homeDeviceGroupList", HomeDeviceGroupName.values());
        }

        return mav;
    }

    @RequestMapping(value="/gadget/loadControlMgmt/loadControlMgmtMax")
    public ModelAndView homeDeviceMgmtMax() {

        ModelAndView mav = new ModelAndView("/gadget/loadControlMgmt/loadControlMgmtMax");
        
		// ESAPI.setAuthenticator((Authenticator)new AimirAuthenticator());
        AimirAuthenticator instance = (AimirAuthenticator)ESAPI.authenticator();        
        AimirUser user = (AimirUser)instance.getUserFromSession();
        int operatorId = (int) user.getAccountId();

        Operator operator = operatorManager.getOperator(operatorId);
        List<OperatorContract> operatorContracts = operatorContractManager.getOperatorContractByOperator(operator);
        List<Contract> contracts = new ArrayList<Contract>();
    	String energyServiceType = MeterType.EnergyMeter.getServiceType();
        Contract contract;
        String serviceType;

        for (OperatorContract operatorContract : operatorContracts) {

        	contract = new Contract();
        	serviceType = "";

        	contract = operatorContract.getContract();
        	serviceType = contract.getServiceTypeCode().getCode();

            // 계약정보 선택 콤보박스 생성을 위해 명칭을 일시적으로 Contract#KeyNum 속성에 저장합니다.
            // Contract에 friendlyName속성을 추가 할지는 차후 결정(HEMS에서만 사용하는 속성이므로 굳이 Contract에 설정하지 않아도 될것같음)
            contract.setKeyNum(operatorContract.getFriendlyName());

        	if ( energyServiceType.equals(serviceType) 
        			&& operatorContract.getContractStatus() == 1 ) {
        		contracts.add(contract);
        	}
        }

        if(contracts.size() == 0) {
        	mav.addObject("isNotService",true);
        	return mav;
        } else {
        	mav.addObject("isNotService",false);
        }

        mav.addObject("contracts", contracts);
        mav.addObject("supplierId", contracts.get(0).getSupplier().getId());

        HomeGroup homeGroup = homeDeviceMgmtManager.getHomeGroupByMember(contracts.get(0).getContractNumber());

        if(homeGroup == null){
        	mav.addObject("homeGroupId", "0");
        } else {
            mav.addObject("homeGroupId", homeGroup.getId());
    		mav.addObject("homeDeviceGroupList", HomeDeviceGroupName.values());
        }

        return mav;
    }

	@RequestMapping(value="/gadget/loadControlMgmt/getHomeDeviceMappingInfo")
	public ModelAndView getHomeDeviceMappingInfo(
			@RequestParam("homeGroupId") String homeGroupId) {
		ModelAndView mav = new ModelAndView("jsonView");
		List<Map<String, Object>> list = loadControlMgmtManager.getHomeDeviceDRMgmtInfo(homeGroupId, "", Integer.toString(codeManager.getCodeIdByCode(HomeDeviceCategoryType.GENERAL_APPLIANCE.getCode())));

		mav.addObject("result", list);
		return mav;
	}

	@RequestMapping(value="/gadget/loadControlMgmt/getHomeDeviceDrList")
	public ModelAndView getHomeDeviceDrList() {
		ModelAndView mav = new ModelAndView("jsonView");
		int smartConcentId = codeManager.getCodeIdByCode(HomeDeviceCategoryType.SMART_CONCENT.getCode());
		mav.addObject("result", homeDeviceMgmtManager.getHomeDeviceDrLevelByCondition(smartConcentId,""));
		return mav;
	}

	@RequestMapping(value="/gadget/loadControlMgmt/runDemandResponse")
	public ModelAndView runDemandResponse(
			@RequestParam("id") String endDeviceId,
			@RequestParam("drLevel") String drLevel,
			@RequestParam("contractId") int contractId,
			@RequestParam("serialNumber") String serialNumber,
			@RequestParam("password") String password) {

		ResultStatus status = ResultStatus.SUCCESS;
		ModelAndView mav = new ModelAndView("jsonView");
		// ESAPI.setAuthenticator((Authenticator)new AimirAuthenticator());
        AimirAuthenticator instance = (AimirAuthenticator)ESAPI.authenticator();        
        AimirUser user = (AimirUser)instance.getUserFromSession();
        String loginId = user.getLoginId();
        Operator operator = null;
        String rtnStr = "";
        Modem modem = null;
        EndDevice endDevice = null;
        try {
	        operator = (Operator)userLoginManager.getOperatorByLoginId(loginId);
	    	if(operator != null && operator.getPassword().equals(instance.hashPassword(password, loginId))){

				endDevice = endDeviceManager.getEndDevice(Integer.parseInt(endDeviceId));
				modem = endDevice.getModem();

				//TODO 집중기에 endDeviceControl 커맨드가 구현되지 않았기 때문에 임시로 cmdDigitalInOut을 호출함
				//CmdOperationUtil.cmdDigitalInOut(modem.getMcu().getSysID(), modem.getDeviceSerial(), (byte)Integer.parseInt(drLevel), (byte)0xff, (byte)0x01);
				cmdOperationUtil.cmdSetEnergyLevel(modem.getMcu().getSysID(), modem.getDeviceSerial(), Integer.parseInt(drLevel));
				
				// DR레벨 변경 구현( 집중기에 자산 상태이벤트가 구현되지 않았기 때문에 임시로 작성)
				loadControlMgmtManager.updateEndDeviceDrLevel(endDevice.getId(), endDevice.getCategoryCode().getId(), Integer.parseInt(drLevel));

				/*
				CmdOperationUtil.cmdEndDeviceControl(meter.getModem().getMcu().getSysID(),
						endDeviceControl.getMRID(), endDeviceControl.getEndDeviceAsset().getMRID(),
						responseProgram.getMRID(), responseProgram.getDlcLevel().toString());
						*/
	            
//	    		endDeviceEntry endDeviceEntry = new endDeviceEntry();
//	    		endDeviceEntry.setCommDevId(new OCTET(modem.getDeviceSerial())); // 통신장비 아이디(모뎀아이디)
//	    		endDeviceEntry.setDeviceId(new OCTET(modem.getDeviceSerial())); // HAN Asset ID
//	    		//endDeviceEntry.setDeviceDescription(deviceDescription); // 장비 설명 예) 안방 TV; 
//	    		endDeviceEntry.setFirmwareVersion(new OCTET(modem.getFwRevision())); // 펌웨어 버전
//	    		endDeviceEntry.setDrLevel(new INT(Integer.parseInt(drLevel))); // drProgram level 1,2,3,6 (1,6 : ACD))
//	    		endDeviceEntry.setDrProgramMandatory(new BOOL(true));
//	    		endDeviceEntry.setDrEnable(new BOOL(true)); // 부하제어이기 때문에 true로 고정
//	    		endDeviceEntry.setLoadControl(new BOOL(endDevice.getLoadControl()));
//	    		//endDeviceEntry.setCurrentDemand(currentDemand);
//	    		endDeviceEntry.setType(new INT(4));
//	    		//endDeviceEntry.setScheduledInterval(scheduledInterval);
//	    		//endDeviceEntry.setStatus(status);
//	    		//endDeviceEntry.setUpdateDate(updateDate);
//	    		CmdOperationUtil.cmdSetDRLevel(modem.getMcu().getSysID(), endDeviceEntry);
//	    		
//	    		// DR level Control 명령
//	    		CmdOperationUtil.cmdSetDRLevel(modem.getMcu().getSysID(), endDeviceEntry);
	    		// DR 레벨 변경해야 하는지 조사
	            //user.setLastLoginTime(Calendar.getInstance().getTime());
	    	}else{
	    		status = ResultStatus.INVALID_PARAMETER;
	    		mav.addObject("status", status.name());
	    		return mav;
	    	}
		} catch (Exception e) {
			status = ResultStatus.FAIL;
			e.printStackTrace();
			rtnStr = e.toString();
		}

		// code : 8.2.10 Device Control
		// 고객의 Device 
		StringBuilder description = new StringBuilder();
		description.append("drlevel:").append(drLevel)
		.append(",drname:").append(homeDeviceMgmtManager.getHomeDeviceDrLevelByCondition(endDevice.getCategoryCode().getId(), drLevel).get(0).get("DRNAME"))
		.append(",serialNumber:").append(serialNumber);

		// 8.2.10
		Contract contract = contractManager.getContract(contractId);
		Supplier supplier = contract.getSupplier();

//		operationLogManager.saveOperationLogByCustomer(supplier, modem.getMcu().getMcuType(), modem.getDeviceSerial(),
//				loginId, codeManager.getCodeByCode("8.2.10"), status.getCode(), rtnStr, description.toString(), contract.getContractNumber());

		mav.addObject("status", status.name());
		return mav;
	}

	@RequestMapping(value="/gadget/loadControlMgmt/runGroupDemandResponse")
	public ModelAndView runGroupDemandResponse(
			@RequestParam("homeGroupId") String homeGroupId,
			@RequestParam("groupDrLevel") String groupDrLevel,
			@RequestParam("contractId") int contractId,
			@RequestParam("password") String password) {
		ResultStatus status = ResultStatus.SUCCESS;
		ModelAndView mav = new ModelAndView("jsonView");
		// ESAPI.setAuthenticator((Authenticator)new AimirAuthenticator());
        AimirAuthenticator instance = (AimirAuthenticator)ESAPI.authenticator();        
        AimirUser user = (AimirUser)instance.getUserFromSession();
        String loginId = user.getLoginId();
        Operator operator = null;
        Modem modem = null;
        EndDevice endDevice = null;
        String rtnStr = "";
        try {
	        operator = (Operator)userLoginManager.getOperatorByLoginId(loginId);
	    	if(operator != null && operator.getPassword().equals(instance.hashPassword(password, loginId))){
	    		List<Map<String, Object>> list = loadControlMgmtManager.getHomeDeviceDRMgmtInfo(homeGroupId, "", Integer.toString(codeManager.getCodeIdByCode(HomeDeviceCategoryType.GENERAL_APPLIANCE.getCode())));
	    		for(Map<String, Object> map : list) {
	    			int endDeviceId = (Integer)map.get("MAPPINGID");
	    			endDevice = endDeviceManager.getEndDevice(endDeviceId);
					modem = endDevice.getModem();
		    		
					//TODO 집중기에 endDeviceControl 커맨드가 구현되지 않았기 때문에 임시로 cmdDigitalInOut을 호출함
					//CmdOperationUtil.cmdDigitalInOut(modem.getMcu().getSysID(), modem.getDeviceSerial(), (byte)Integer.parseInt(groupDrLevel), (byte)0xff, (byte)0x01);	

					// DR레벨 변경 구현( 집중기에 자산 상태이벤트가 구현되지 않았기 때문에 임시로 작성)
					loadControlMgmtManager.updateEndDeviceDrLevel(endDeviceId, endDevice.getCategoryCode().getId(), Integer.parseInt(groupDrLevel));

					StringBuilder description = new StringBuilder();
					description.append("drlevel:").append(groupDrLevel)
				    .append(",drname:").append(homeDeviceMgmtManager.getHomeDeviceDrLevelByCondition(endDevice.getCategoryCode().getId(), groupDrLevel).get(0).get("DRNAME"))
					.append(",serialNumber:").append(endDevice.getSerialNumber());
					
					Contract contract = contractManager.getContract(contractId);
					Supplier supplier = contract.getSupplier();

					operationLogManager.saveOperationLogByCustomer(supplier, modem.getMcu().getMcuType(), modem.getDeviceSerial(),
							loginId, codeManager.getCodeByCode("8.2.10"), status.getCode(), rtnStr, description.toString(), contract.getContractNumber());
	    		}
	    	}else{
	    		status = ResultStatus.INVALID_PARAMETER;
	    		mav.addObject("status", status.name());
	    		return mav;
	    	}
		} catch (Exception e) {
			status = ResultStatus.FAIL;
			e.printStackTrace();
			rtnStr = e.toString();
		}

		mav.addObject("status", status.name());
		return mav;
	}

	@RequestMapping(value="/gadget/loadControlMgmt/drAllAllow")
	public ModelAndView drAllAllow(
			@RequestParam("homeGroupId") String homeGroupId,
			@RequestParam("checkValue") String checkValue) {
		ResultStatus status = ResultStatus.SUCCESS;
		ModelAndView mav = new ModelAndView("jsonView");
		// ESAPI.setAuthenticator((Authenticator)new AimirAuthenticator());
        AimirAuthenticator instance = (AimirAuthenticator)ESAPI.authenticator();        
        AimirUser user = (AimirUser)instance.getUserFromSession();
        String loginId = user.getLoginId();
        Modem modem = null;
        String rtnStr = "";
        try {
    		List<Map<String, Object>> list = loadControlMgmtManager.getHomeDeviceDRMgmtInfo(homeGroupId, "", Integer.toString(codeManager.getCodeIdByCode(HomeDeviceCategoryType.GENERAL_APPLIANCE.getCode())));
    		for(Map<String, Object> map : list) {
    			int endDeviceId = (Integer)map.get("MAPPINGID");
    			EndDevice endDevice = endDeviceManager.getEndDevice(endDeviceId);
				modem = endDevice.getModem();
	    		
				// DR허가 변경
				loadControlMgmtManager.updateDrProgramMandatoryInfo(endDeviceId, checkValue);

				// DR허가 여부 변경시 집중기에 변경 정보를 내려줘야 하는지 조사
//				operationLogManager.saveOperationLog(modem.getMcu().getMcuType(), modem.getDeviceSerial(),
//						loginId, codeManager.getCodeByCode("8.2.4"), status.getCode(), rtnStr);
    		}
		} catch (Exception e) {
			status = ResultStatus.FAIL;
			e.printStackTrace();
			rtnStr = e.toString();
		}

		mav.addObject("status", status.name());
		return mav;
	}

	@RequestMapping(value="/gadget/loadControlMgmt/updateDrProgramMandatory")
	public ModelAndView updateDrProgramMandatory(
			@RequestParam("id") String endDeviceId,
			@RequestParam("drProgramMandatory") String drProgramMandatory) {
		boolean status = true;
		ModelAndView mav = new ModelAndView("jsonView");
        try {
        		loadControlMgmtManager.updateDrProgramMandatoryInfo(Integer.parseInt(endDeviceId), drProgramMandatory);
		} catch (Exception e) {
            status = false;
			e.printStackTrace();
		}
		mav.addObject("status", status);
		return mav;
	}
	
	@RequestMapping(value="/gadget/loadControlMgmt/getLoadControlHistoryMini")
	public ModelAndView getDemandResponseHistory(
			@RequestParam("contractId") int contractId) {
		ModelAndView mav = new ModelAndView("jsonView");
		HttpServletRequest request = ESAPI.httpUtilities().getCurrentRequest();

		// ESAPI.setAuthenticator((Authenticator)new AimirAuthenticator());
        AimirAuthenticator instance = (AimirAuthenticator)ESAPI.authenticator();        
        AimirUser user = (AimirUser)instance.getUserFromSession();
        String loginId = user.getLoginId();
 
		int page = Integer.parseInt(request.getParameter("page"));
		int limit = Integer.parseInt(request.getParameter("limit"));
		
		Contract contract = contractManager.getContract(contractId);
//		Contract contract = contractManager.getContractByContractNumber(contractNumber.trim());
		String lang = contract.getSupplier().getLang().getCode_2letter();
		String country = contract.getSupplier().getCountry().getCode_2letter();

		// 최근 한달의 데이터를 검색한다.
		String searchEndDate = CalendarUtil.getCurrentDate();
		String searchStartDate = CalendarUtil.getDate(searchEndDate, Calendar.MONTH, -1);

		List<Map<String, Object>> list = loadControlMgmtManager.getLoadControlHistory(loginId, contract.getContractNumber(), page, limit, searchStartDate, searchEndDate);

		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		for(Map<String, Object> map : list) {
			// DESCRIPTION항목에서 DR레벨과 시리어번호를 취득하여 리스트에 저장한다.
			if((Integer)map.get("DIV") == 1) {
				map.put("DIV", "LC");
				String[] description = ((String)map.get("DESCRIPTION")).split(",");
	
				map.put("DRLEVEL", description[0].replaceAll("drlevel:", ""));
				map.put("DRNAME", description[1].replaceAll("drname:", ""));
				map.put("SERIALNUMBER", description[2].replaceAll("serialNumber:", ""));
				EndDevice endDevice = endDeviceManager.getEndDevice(description[2].replaceAll("serialNumber:", ""));
				if(endDevice == null) {
					map.put("FRIENDLYNAME", "-");				
				}else {
					map.put("FRIENDLYNAME", endDeviceManager.getEndDevice(description[2].replaceAll("serialNumber:", "")).getFriendlyName());				
				}
	
		        switch((Integer)map.get("STATUS")){
		        	case 0 : map.put("STATUSNAME", ResultStatus.SUCCESS.name());break;
		        	case 1 : map.put("STATUSNAME", ResultStatus.FAIL.name());break;
		        	case 2 : map.put("STATUSNAME", ResultStatus.INVALID_PARAMETER.name());break;
		        	case 3 : map.put("STATUSNAME", ResultStatus.COMMUNICATION_FAIL.name());break;
		        }
		        
				map.put("OPERATIONNAME", codeManager.getCode((Integer)map.get("OPERATIONCOMMANDCODE")).getName());
				map.put("RUNDATE", TimeLocaleUtil.getLocaleDate((String)map.get("RUNDATE"),	lang, country));
			} else {
				switch((Integer)map.get("STATUS")){
		        	case 1 : map.put("STATUSNAME", DemandResponseEventOptOutStatus.Initialization.name());break;
		        	case 2 : map.put("STATUSNAME", DemandResponseEventOptOutStatus.Ongoing.name());break;
		        	case 3 : map.put("STATUSNAME", DemandResponseEventOptOutStatus.Participated.name());break;
		        	case 4 : map.put("STATUSNAME", DemandResponseEventOptOutStatus.Rejected.name());break;
		        	case 5 : map.put("STATUSNAME", DemandResponseEventOptOutStatus.Completed.name());break;
				}
				map.put("DIV", "DR");
				map.put("RUNDATE", TimeLocaleUtil.getLocaleDate((String)map.get("RUNDATE"),	lang, country));
			}
			result.add(map);
		}

		mav.addObject("result", result);
		mav.addObject("totalCount", loadControlMgmtManager.getLoadControlHistoryTotalCount(loginId, contract.getContractNumber(), searchStartDate, searchEndDate));
		return mav;
	}
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value="/gadget/loadControlMgmt/deleteDrHistory")
	public ModelAndView deleteDrHistory(HttpServletRequest request,
			HttpServletResponse response) {
		boolean status = true;
    	ModelAndView mav = new ModelAndView("jsonView");

        try {
        	Enumeration<String> paramKey = request.getParameterNames();
    		String keyStr;
    		String[] recordLC = null;
    		String[] recordDR = null;
            while(paramKey.hasMoreElements()){
            	keyStr = "";
            	keyStr = paramKey.nextElement();
            	if ("recordsDR[]".equals(keyStr)) {
            		recordDR = request.getParameterValues(keyStr);
            		for(int i=0; i<recordDR.length; i++) {
                    	loadControlMgmtManager.deleteDemandResponseEventLog(Integer.parseInt(recordDR[i]));
            		}
            	} else if ("recordsLC[]".equals(keyStr)) {
            		
            		recordLC = request.getParameterValues(keyStr);
            		for(int i=0; i<recordLC.length; i++) {
                    	operationLogManager.deleteOperationLog(Long.parseLong(recordLC[i]));
            		}
            	}           	
            }
		} catch (Exception e) {
            status = false;
			e.printStackTrace();
		}
		mav.addObject("status", status);
		return mav;
	}
	
	@RequestMapping(value="/gadget/loadControlMgmt/getLoadControlHistoryMax")
	public ModelAndView getDemandResponseHistory(
			@RequestParam("contractId") int contractId
			,@RequestParam("period") String period) {
		ModelAndView mav = new ModelAndView("jsonView");
		HttpServletRequest request = ESAPI.httpUtilities().getCurrentRequest();

		// ESAPI.setAuthenticator((Authenticator)new AimirAuthenticator());
        AimirAuthenticator instance = (AimirAuthenticator)ESAPI.authenticator();        
        AimirUser user = (AimirUser)instance.getUserFromSession();
        String loginId = user.getLoginId();
       
//        Contract contract = contractManager.getContractByContractNumber(contractNumber.trim());
		Contract contract = contractManager.getContract(contractId);
		String lang = contract.getSupplier().getLang().getCode_2letter();
		String country = contract.getSupplier().getCountry().getCode_2letter();

		// 최근 한달의 데이터를 검색한다.
		String searchEndDate = CalendarUtil.getCurrentDate();
		String searchStartDate = "";

		// 선택된 주기별로 데이터를 조회 한다.
		switch (Integer.parseInt(period)) {
		case 1: // 일주일
			searchStartDate = CalendarUtil.getDate(searchEndDate, Calendar.DATE, -7);
			break;
		case 2: // 한달
			searchStartDate = CalendarUtil.getDate(searchEndDate, Calendar.MONTH, -1);
			break;
		case 3: // 3개월
			searchStartDate = CalendarUtil.getDate(searchEndDate, Calendar.MONTH, -3);
			break;
		case 4: // 6개월
			searchStartDate = CalendarUtil.getDate(searchEndDate, Calendar.MONTH, -6);
			break;
		case 5: // 1년
			searchStartDate = CalendarUtil.getDate(searchEndDate, Calendar.YEAR, -1);
			break;
		default: // 한달
			searchStartDate = CalendarUtil.getDate(searchEndDate, Calendar.MONTH, -1);
			break;
		}

		List<Map<String, Object>> list = loadControlMgmtManager.getLoadControlHistory(loginId, contract.getContractNumber(), 1, 15, searchStartDate, searchEndDate);

		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		for(Map<String, Object> map : list) {
			if((Integer)map.get("DIV") == 1) {
				map.put("DIV", "LC");			
				// DESCRIPTION항목에서 DR레벨과 시리얼번호를 취득하여 리스트에 저장한다.
				String[] description = ((String)map.get("DESCRIPTION")).split(",");
	
				map.put("DRLEVEL", description[0].replaceAll("drlevel:", ""));
				map.put("DRNAME", description[1].replaceAll("drname:", ""));
				map.put("SERIALNUMBER", description[2].replaceAll("serialNumber:", ""));
				EndDevice endDevice = endDeviceManager.getEndDevice(description[2].replaceAll("serialNumber:", ""));
				if(endDevice == null) {
					map.put("FRIENDLYNAME", "-");				
				}else {
					map.put("FRIENDLYNAME", endDeviceManager.getEndDevice(description[2].replaceAll("serialNumber:", "")).getFriendlyName());				
				}
	
		        switch((Integer)map.get("STATUS")){
		        	case 0 : map.put("STATUSNAME", ResultStatus.SUCCESS.name());break;
		        	case 1 : map.put("STATUSNAME", ResultStatus.FAIL.name());break;
		        	case 2 : map.put("STATUSNAME", ResultStatus.INVALID_PARAMETER.name());break;
		        	case 3 : map.put("STATUSNAME", ResultStatus.COMMUNICATION_FAIL.name());break;
	            }

				map.put("OPERATIONNAME", codeManager.getCode((Integer)map.get("OPERATIONCOMMANDCODE")).getName());
				map.put("RUNDATE", TimeLocaleUtil.getLocaleDate((String)map.get("RUNDATE"),	lang, country));
			}else {
				switch((Integer)map.get("STATUS")){
		        	case 1 : map.put("STATUSNAME", DemandResponseEventOptOutStatus.Initialization.name());break;
		        	case 2 : map.put("STATUSNAME", DemandResponseEventOptOutStatus.Ongoing.name());break;
		        	case 3 : map.put("STATUSNAME", DemandResponseEventOptOutStatus.Participated.name());break;
		        	case 4 : map.put("STATUSNAME", DemandResponseEventOptOutStatus.Rejected.name());break;
		        	case 5 : map.put("STATUSNAME", DemandResponseEventOptOutStatus.Completed.name());break;
	            }

				map.put("DIV", "DR");
				map.put("RUNDATE", TimeLocaleUtil.getLocaleDate((String)map.get("RUNDATE"),	lang, country));
				map.put("NOTIFICATIONTIME", TimeLocaleUtil.getLocaleDate((String)map.get("NOTIFICATIONTIME"),	lang, country));
				map.put("ENDTIME", TimeLocaleUtil.getLocaleDate((String)map.get("ENDTIME"),	lang, country));
			}

			result.add(map);
		}

		mav.addObject("result", result);
		mav.addObject("totalCount", loadControlMgmtManager.getLoadControlHistoryTotalCount(loginId, contract.getContractNumber(), searchStartDate, searchEndDate));
		return mav;
	}	
	
	/*
	@RequestMapping(value="/gadget/loadControlMgmt/pollDrWebservice")
	public ModelAndView pollDrWebservice() {

    	ModelAndView mav = new ModelAndView("jsonView");
    	List<Map<String, Object>> gridDate = null;
        try {
        	gridDate = loadControlMgmtManager.pollDrWebService();
		} catch (Exception e) {
			e.printStackTrace();
		}
		mav.addObject("gridData", gridDate);
		return mav;
	}
	*/

	@RequestMapping(value="/gadget/loadControlMgmt/setDREventOptOutStatus")
	public ModelAndView setDREventOptOutStatus(
			@RequestParam("optOutStatus") int status, 
			@RequestParam("eventIdentifier") String eventIdentifier) {

    	ModelAndView mav = new ModelAndView("jsonView");
    	try {
			// ESAPI.setAuthenticator((Authenticator)new AimirAuthenticator());
	        AimirAuthenticator instance = (AimirAuthenticator)ESAPI.authenticator();        
	        AimirUser user = (AimirUser)instance.getUserFromSession();
	        int operatorId = (int) user.getAccountId();
	        
	        Operator operator = operatorManager.getOperator(operatorId);
	        List<OperatorContract> operatorContracts = operatorContractManager.getOperatorContractByOperator(operator);

	        for (OperatorContract operatorContract : operatorContracts) {
	        	
	            // 홈그룹 정보를 취득한다.
	            HomeGroup homeGroup = homeDeviceMgmtManager.getHomeGroupByMember(operatorContract.getContract().getContractNumber());
	            String homeGroupId = homeGroup.getId().toString();
	    		List<Map<String, Object>> list = homeDeviceMgmtManager.getHomeDeviceInfo(homeGroupId, "", Integer.toString(codeManager.getCodeIdByCode(HomeDeviceCategoryType.SMART_CONCENT.getCode())));
	    		for(Map<String, Object> map : list){
	    			int modemId = (Integer)map.get("MODEMID");
	    			Modem modem = modemManager.getModem(modemId);
		        	if ( MeterType.EnergyMeter.getServiceType().equals(operatorContract.getContract().getServiceTypeCode().getCode())
		        			&& 1 == operatorContract.getContractStatus() ) {

		        		if (modem != null && modem.getMcu() != null ) {
		        			String mcuId = modem.getMcu().getSysID();
		        			String sensorId = modem.getDeviceSerial();
		                	loadControlMgmtManager.setOptDREventOptOutStatus(status, eventIdentifier, mcuId, sensorId);
		        		}
		        	}	
	    		}
	        }

		} catch (Exception e) {
			e.printStackTrace();
		}
//		mav.addObject("gridData", gridDate);
		return mav;
	}
}
