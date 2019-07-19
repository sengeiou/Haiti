/**
 * HomeDeviceMgmtController.java Copyright NuriTelecom Limited 2011
 */
package com.aimir.bo.system.homeDeviceMgmt;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.owasp.esapi.ESAPI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate4.HibernateTransactionManager;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.aimir.bo.system.operator.LoginController;
import com.aimir.constants.CommonConstants.GroupType;
import com.aimir.constants.CommonConstants.HomeDeviceCategoryType;
import com.aimir.constants.CommonConstants.HomeDeviceGroupName;
import com.aimir.constants.CommonConstants.Interface;
import com.aimir.constants.CommonConstants.MeterType;
import com.aimir.constants.CommonConstants.Protocol;
import com.aimir.constants.CommonConstants.ResultStatus;
import com.aimir.esapi.AimirAuthenticator;
import com.aimir.esapi.AimirUser;
import com.aimir.model.device.ACD;
import com.aimir.model.device.EndDevice;
import com.aimir.model.device.MCU;
import com.aimir.model.device.Meter;
import com.aimir.model.device.Modem;
import com.aimir.model.system.Code;
import com.aimir.model.system.Contract;
import com.aimir.model.system.GroupMember;
import com.aimir.model.system.HomeGroup;
import com.aimir.model.system.Operator;
import com.aimir.model.system.OperatorContract;
import com.aimir.model.system.Supplier;
import com.aimir.service.device.EndDeviceManager;
import com.aimir.service.device.MCUManager;
import com.aimir.service.device.ModemManager;
import com.aimir.service.system.CodeManager;
import com.aimir.service.system.ContractManager;
import com.aimir.service.system.GroupMgmtManager;
import com.aimir.service.system.OperatorManager;
import com.aimir.service.system.SupplierManager;
import com.aimir.service.system.homeDeviceMgmt.HomeDeviceMgmtManager;
import com.aimir.service.system.membership.OperatorContractManager;
import com.aimir.support.AimirFilePath;

/**
 * HomeDeviceMgmtController.java Description 
 * 제품 정보 관리 Controller
 * 
 * Date          Version     Author   Description
 * 2011. 5. 24.   v1.0       eunmiae  초판 생성       
 *
 */

@Controller
public class HomeDeviceMgmtController {
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
	ContractManager contractManager;
	
    @Autowired
    HibernateTransactionManager transactionManager;

    @RequestMapping(value="/gadget/homeDeviceMgmt/homeDeviceMgmtMini")
    public ModelAndView homeDeviceMgmtMini() {

        ModelAndView mav = new ModelAndView("/gadget/homeDeviceMgmt/homeDeviceMgmtMini");

		// ESAPI.setAuthenticator((Authenticator)new AimirAuthenticator());
        AimirAuthenticator instance = (AimirAuthenticator)ESAPI.authenticator();        
        AimirUser user = (AimirUser)instance.getUserFromSession();
        int operatorId = (int) user.getAccountId();

        Operator operator = operatorManager.getOperator(operatorId);
        List<OperatorContract> operatorContracts = operatorContractManager.getOperatorContractByOperator(operator);
        List<Contract> contracts = new ArrayList<Contract>();

        for (OperatorContract operatorContract : operatorContracts) {

        	Contract contract = operatorContract.getContract();
        	if(contract != null) {
	        	String serviceType = contract.getServiceTypeCode().getCode();

	            // 계약정보 선택 콤보박스 생성을 위해 명칭을 일시적으로 Contract#KeyNum 속성에 저장합니다.
	            // Contract에 friendlyName속성을 추가 할지는 차후 결정(HEMS에서만 사용하는 속성이므로 굳이 Contract에 설정하지 않아도 될것같음)
	        	contract.setKeyNum(operatorContract.getFriendlyName());

	        	// 전기계약 고객의 계약정보만 취득한다.
	        	if (MeterType.EnergyMeter.getServiceType().equals(serviceType) 
	        			&& operatorContract.getContractStatus() == 1 ) {
	        		contracts.add(contract);
	        	}
        	}
        }

        if(contracts.size() == 0) {
        	mav.addObject("isNotService",true);
        	return mav;
        } else {
        	mav.addObject("isNotService",false);
        }
        
        mav.addObject("contracts", contracts);
        // 복수계의 계약정보를 소지한 고객의 경우,첫 계약의 공급사 정보를 취득한다.
        mav.addObject("supplierId", contracts.get(0).getSupplier().getId());

        // 홈그룹 정보를 취득한다.
        HomeGroup homeGroup = homeDeviceMgmtManager.getHomeGroupByMember(contracts.get(0).getContractNumber());
        
        mav.addObject("result", "success");
        mav.addObject("homeDeviceGroupList", HomeDeviceGroupName.values());
        mav.addObject("operatorId", operatorId);
        if(homeGroup == null){
        	mav.addObject("homeGroupId", "0");
        } else {
        	mav.addObject("homeGroupId", homeGroup.getId());
        }

        return mav;
    }

    @RequestMapping(value="/gadget/homeDeviceMgmt/homeDeviceMgmtMax")
    public ModelAndView homeDeviceMgmtMax() {

        ModelAndView mav = new ModelAndView("/gadget/homeDeviceMgmt/homeDeviceMgmtMax");
        
		// ESAPI.setAuthenticator((Authenticator)new AimirAuthenticator());
        AimirAuthenticator instance = (AimirAuthenticator)ESAPI.authenticator();        
        AimirUser user = (AimirUser)instance.getUserFromSession();
        int operatorId = (int) user.getAccountId();

        Operator operator = operatorManager.getOperator(operatorId);
        List<OperatorContract> operatorContracts = operatorContractManager.getOperatorContractByOperator(operator);
        List<Contract> contracts = new ArrayList<Contract>();
        
        for (OperatorContract operatorContract : operatorContracts) {

        	Contract contract = operatorContract.getContract();
        	if(contract != null) {
	        	String serviceType = contract.getServiceTypeCode().getCode();
	            // 계약정보 선택 콤보박스 생성을 위해 명칭을 일시적으로 Contract#KeyNum 속성에 저장합니다.
	            // Contract에 friendlyName속성을 추가 할지는 차후 결정(HEMS에서만 사용하는 속성이므로 굳이 Contract에 설정하지 않아도 될것같음)
	        	contract.setKeyNum(operatorContract.getFriendlyName());

	        	if ( MeterType.EnergyMeter.getServiceType().equals(serviceType) 
	        			&& operatorContract.getContractStatus() == 1 ) {
	        		contracts.add(contract);
	        	}
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

        mav.addObject("result", "success");
        mav.addObject("homeDeviceGroupList", HomeDeviceGroupName.values());
        mav.addObject("operatorId", operatorId);
        if(homeGroup == null){
        	mav.addObject("homeGroupId", "0");
        } else {
        	mav.addObject("homeGroupId", homeGroup.getId());
        }
//        if(homeGroup == null){
//    		mav.addObject("status", "noData");
//        } else {
//            mav.addObject("status", "success");
//            mav.addObject("homeGroupId", homeGroup.getId());
//    		mav.addObject("homeDeviceGroupList", HomeDeviceGroupName.values());
//        }
        return mav;
    }

    /**
     * method name : getContract
     * method Desc : 계약정보 조회
     *
     * @param contractId
     * @return
     */
    @RequestMapping(value="/gadget/homeDeviceMgmt/getContract")
    public ModelAndView getContract(
    		@RequestParam("contractId") int contractId) {

    	ModelAndView mav = new ModelAndView("jsonView");

    	Contract contract = contractManager.getContract(contractId);

    	mav.addObject("contract", contract);
    	mav.addObject("address", contract.getCustomer().getAddress() == null ? contract.getCustomer().getAddress2() : contract.getCustomer().getAddress());
    	mav.addObject("meterId", contract.getMeter().getId());    	

        HomeGroup homeGroup = homeDeviceMgmtManager.getHomeGroupByMember(contract.getContractNumber());
        mav.addObject("result", "success");
        mav.addObject("homeDeviceGroupList", HomeDeviceGroupName.values());
        if(homeGroup == null){
        	mav.addObject("homeGroupId", "0");
        } else {
        	mav.addObject("homeGroupId", homeGroup.getId());
        }

    	return mav;
    }

	/**
	 * method name : getHomeDeviceGrid
	 * method Desc : 제품 조회 그리드 정보 취득
	 *
	 * @param homeGroupId : 홈그룹 아이디
	 * @param homeDeviceGroupName : 검색 조건(홈 그룹명)
	 * @param homeDeviceCategory : 검색 조건(카테고리)
	 * @return
	 */
	@RequestMapping(value="/gadget/homeDeviceMgmt/getHomeDeviceInfo")
	public ModelAndView getHomeDeviceGrid(
			@RequestParam("homeGroupId") String homeGroupId,
			@RequestParam("homeDeviceGroupName") String homeDeviceGroupName,
			@RequestParam("homeDeviceCategory") String homeDeviceCategory) {
		ModelAndView mav = new ModelAndView("jsonView");

		mav.addObject("result", homeDeviceMgmtManager.getHomeDeviceInfo(homeGroupId, homeDeviceGroupName, homeDeviceCategory));

		return mav;
	}

	/**
	 * method name : makeSelectbox
	 * method Desc : 제품 조회 탭 콤보 박스 생성
	 *
	 * @param groupId : 홈그룹 아이디
	 * @return
	 */
	@RequestMapping(value="/gadget/homeDeviceMgmt/makeSelectBox")
	public ModelAndView makeSelectbox(
    		@RequestParam("homeGroupId") String groupId) {

        ModelAndView mav = new ModelAndView("jsonView");

		mav.addObject("selectHomeDeviceGroup", homeDeviceMgmtManager.getHomeDeviceGroupSelected(groupId));
		mav.addObject("selectHomeDeviceCategory", homeDeviceMgmtManager.getHomeDeviceCategorySelected(groupId));

		return mav;
	}

	/**
	 * method name : getHomeDeviceImgName
	 * method Desc : [제품등록] 변경이미지 파일 정보 취득
	 *
	 * @return
	 */
	@RequestMapping(value="/gadget/homeDeviceMgmt/getHomeDeviceImgName")
	public ModelAndView getHomeDeviceImgName(){

		ModelAndView mav = new ModelAndView("jsonView");

		HttpServletRequest request = ESAPI.httpUtilities().getCurrentRequest();
		String contextRoot = new HttpServletRequestWrapper(request).getRealPath("/");
		String homeDeviceImgPathname = contextRoot+aimirFilePath.getHomeDevicePath();

		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		Map<String, Object> map = null;

		File fr = new File(homeDeviceImgPathname);
	    File[] filelist = fr.listFiles();
	    for (int i = 0; i < filelist.length; i++) {
	    	int idx = filelist[i].getName().toLowerCase().indexOf(".jpg");
	        if(idx > 0){
	        	map = new HashMap<String, Object>();
	        	map.put("imgName", filelist[i].getName().substring(0, idx)); // 확장자를 제외한 파일명  	
	        	map.put("imgUrl", aimirFilePath.getHomeDevicePath() + "/" + filelist[i].getName());
	    		result.add(map);
	        }
	    }

		mav.addObject("images", result);

		return mav;
	}

	/**
	 * method name : checkId
	 * method Desc : 식별아이디 중복 체크
	 *
	 * @param identificationID
	 * @param homeDeviceCategoryType
	 * @return
	 */
	@RequestMapping(value="/gadget/homeDeviceMgmt/checkId")
	public ModelAndView checkId(@RequestParam("identificationID") String identificationID,
			@RequestParam("homeDeviceCategory") String homeDeviceCategoryType) {
		String resStatus = "success";

		Code code = codeManager.getCode(Integer.parseInt(homeDeviceCategoryType));
//		// 스마트 콘센트
//		if(HomeDeviceCategoryType.SMART_CONCENT.getCode().equals(code.getCode())){
//			// ID중복 체크
//			if(modemManager.getModem(identificationID) != null){
//				resStatus = "duplicate";
//			}
//			// ID유효성 체크
//		} else { // 스마트 가전, 일반 가전일 경우
//			if(endDeviceManager.getEndDevice(identificationID) != null){
//				resStatus = "duplicate";
//			}
//		}
		ModelAndView mav = new ModelAndView("jsonView");
		mav.addObject("status", resStatus);

		return mav;
	}

	/**
	 * method name : checkId
	 * method Desc : 식별아이디 중복 체크
	 *
	 * @param identificationID
	 * @param homeDeviceCategoryType
	 * @return
	 */
	@RequestMapping(value="/gadget/homeDeviceMgmt/checkName")
	public ModelAndView checkName(
			HttpServletRequest request,
			HttpServletResponse response) {
		String resStatus = "success";
		
		ModelAndView mav = new ModelAndView("jsonView");
		String friendlyName = request.getParameter("friendlyName");
		int contractId = Integer.parseInt(request.getParameter("contractId"));

		Contract contract = contractManager.getContract(contractId);
		//Code code = codeManager.getCode(homeDeviceCategoryType);

		HomeGroup homeGroup = homeDeviceMgmtManager.getHomeGroupByMember(contract.getContractNumber());
		Set<GroupMember> groupMember = homeGroup.getMembers();

		Iterator<GroupMember> lt = groupMember.iterator();

		while(lt.hasNext()) {
			GroupMember g = lt.next();
			if(friendlyName.equals(g.getMember())) {
				resStatus = "duplicate";
				break;
			}
		}

		mav.addObject("status", resStatus);
		return mav;
	}

	/**
	 * method name : registerHomeDevice
	 * method Desc : 제품 정보 등록
	 *
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value="/gadget/homeDeviceMgmt/registerHomeDevice")
		public ModelAndView registerHomeDevice(
				HttpServletRequest request,
				HttpServletResponse response) {

		log.debug("start register Home Device");
		ModelAndView mav = new ModelAndView("jsonView");

		ResultStatus status        = ResultStatus.SUCCESS;                                         //  등록 처리 결과
		int groupKey               = 0;                                                            // 홈그룹 등록후 집중기로 부터 리턴받은 홈그룹 키

        // 제품 정보 입력 폼에서 취득한 값
		int homeGroupId 		   = Integer.parseInt(request.getParameter("homeGroupId"));        // 홈 그룹 아이디
        int homeDeviceCategoryId   = Integer.parseInt(request.getParameter("homeDeviceCategory")); // 제품 유형 아이디
        int contractId             = Integer.parseInt(request.getParameter("contractId"));         // 계약 아이디
        int operatorId             = Integer.parseInt(request.getParameter("operatorId"));         // 고객 아이디(PK)
        String homeDeviceImg       = request.getParameter("homeDeviceImg");                        // 제품 이미지 파일 명
        String friendlyName        = request.getParameter("friendlyName");                         // 제품 명칭
        String identificationID    = request.getParameter("identificationID");                     // 식별 ID
        String homeDeviceGroupName = request.getParameter("homeDeviceGroupName");                  // 제품 그룹
        
		// 트렌젝션 관리
//        TransactionStatus txStatus = null;
//        DefaultTransactionDefinition txDefine = new DefaultTransactionDefinition();
//        txDefine.setIsolationLevel(TransactionDefinition.ISOLATION_READ_UNCOMMITTED);

		try{
			HomeGroup homeGroup = null;
			
	    	EndDevice endDevice = new EndDevice();
//	        txStatus = transactionManager.getTransaction(txDefine);

			Code code = codeManager.getCode(homeDeviceCategoryId);
	    	Contract contract = contractManager.getContract(contractId);
	    	Supplier supplier = contract.getSupplier();
	    	Meter meter = contract.getMeter();
	    	Boolean isDr = contract.getCustomer().getDemandResponse();
	    	// 홈 그룹으로 등록 되어있지 않았을 경우
	    	if(homeGroupId == 0) {
	    	//if(homeGroupId.equals("0")) {
	    		// 홈그룹 등록 명령 실행
	    		homeGroup = new HomeGroup();

	    		if(meter != null && meter.getMcu() != null) {
//	    			groupKey = CmdOperationUtil.cmdGroupAdd((meter.getMcu().getId()).toString(), contract.getContractNumber());
	    			homeGroup.setHomeGroupMcu(meter.getMcu());
	    		}

	    		homeGroup.setGroupType(GroupType.HomeGroup.name());
	    		homeGroup.setGroupKey(groupKey);
	    		homeGroup.setAllUserAccess(false);
	    		homeGroup.setName(contract.getContractNumber());
	    		homeGroup.setOperator(operatorManager.getOperator(operatorId));
	    		homeGroup.setSupplier(supplier);

	    		// 홈 그룹 등록
	    		homeGroup = groupMgmtManager.saveHomeGroup(homeGroup, "C"); // Create

	    		// 등록한 홈 그룹 정보에서 ID취득
	    		homeGroupId = homeGroup.getId();
	    	} else {
				// 홈 그룹 정보 취득
		    	homeGroup = homeDeviceMgmtManager.getHomeGroupById(homeGroupId);
	    	}

	        List<Object> list = new ArrayList<Object>();
			HashMap<String, Object> hashMap = new HashMap<String, Object>();

	        // Group Member에 Home Device 등록
			hashMap.put("groupId", homeGroup.getId());
			hashMap.put("memberName", identificationID.length() == 0 ? friendlyName : identificationID);
	        list.add(hashMap);
	        groupMgmtManager.saveGroupMembers(list);

	        // 스마트 콘센트 일 경우
			if(HomeDeviceCategoryType.SMART_CONCENT.getCode().equals(code.getCode())){

		        // Modem(ACD) 등록
		        ACD acd = new ACD();
		        acd.setDeviceSerial(identificationID);
		        if(modemManager.getModem(identificationID) == null){  // 기존에 등록된
			        acd.setModemType("ACD");
			        acd.setProtocolType("ZigBee");
			        acd.setInterfaceType(Interface.IF4); //AMU	        	
			        acd.setSupplier(supplier);
			        MCU mcu = homeDeviceMgmtManager.getHomeGroupMcuByGroupId(homeGroup.getId());
					if(mcu != null) {
						log.debug("####important###" + mcu.getSysID());
						acd.setMcu(mcu);
					}
	
			        //acd.setMeter(meter);
			        //acd.setModel(model)
					
			        modemManager.insertModemACD(acd);
		        }

		        // EndDevice(스마트 콘센트) 등록
		    	endDevice.setFriendlyName(friendlyName);
		    	endDevice.setSerialNumber(identificationID);
		    	endDevice.setControllerCode(codeManager.getCodeByCode("1.10.1"));  // 1.10.1
		    	endDevice.setHomeDeviceGroupName(homeDeviceGroupName);
		    	endDevice.setHomeDeviceImgFilename(homeDeviceImg);
		    	endDevice.setSupplier(supplier);
		    	endDevice.setCategoryCode(codeManager.getCode(homeDeviceCategoryId));
		    	endDevice.setModem(modemManager.getModem(identificationID));
		    	endDevice.setLoadControl(true);

				// DR고객 인지 판단
				if(isDr == null){
			    	// DR고객이 아닐 경우, DR 거부
			    	endDevice.setDrProgramMandatory(false);						
				}else{
					 endDevice.setDrProgramMandatory(isDr);
				}

				// 제품 등록시,등록 상태를 진행중으로 셋팅
		    	endDevice.setInstallStatus(codeManager.getCodeByCode(Code.IN_PROCESS));

		    	// DR레벨 취득
		    	List<Map<String, Object>> drList = homeDeviceMgmtManager.getHomeDeviceDrLevelByCondition(homeDeviceCategoryId, "");

		    	// 제품 등록시에는 디폴트 DR레벨을 설정한다.
		    	endDevice.setDrLevel((Integer)drList.get(0).get("DRLEVEL"));

		    	endDevice.setProtocolType(Protocol.ZigBee);
			} else if(HomeDeviceCategoryType.SMART_APPLIANCE.getCode().equals(code.getCode())) {

				// 스마트 가전 일 경우
	    		endDevice.setFriendlyName(friendlyName);
	    		endDevice.setSerialNumber(identificationID);
	    		endDevice.setHomeDeviceGroupName(homeDeviceGroupName);
	    		endDevice.setHomeDeviceImgFilename(homeDeviceImg);
	    		endDevice.setSupplier(supplier);
	    		endDevice.setCategoryCode(codeManager.getCode(homeDeviceCategoryId));
	    		endDevice.setInstallStatus(codeManager.getCodeByCode(Code.COMPLETED));
			}else {

	        	// EndDevice(일반 가전) 등록
	    		endDevice.setFriendlyName(friendlyName);
	    		endDevice.setSerialNumber(identificationID.length() == 0 ? friendlyName : identificationID);
	    		endDevice.setHomeDeviceGroupName(homeDeviceGroupName);
	    		endDevice.setHomeDeviceImgFilename(homeDeviceImg);
	    		endDevice.setSupplier(supplier);
	    		endDevice.setCategoryCode(codeManager.getCode(homeDeviceCategoryId));
//	    		endDevice.setInstallStatus(codeManager.getCodeByCode(Code.COMPLETED));
			}
			// EndDevice 등록
			endDeviceManager.addEndDevice(endDevice);
			//transactionManager.commit(txStatus);

		} catch (Exception e) {
			status = ResultStatus.FAIL;
        	//transactionManager.rollback(txStatus);
	    	e.getMessage();
	    	e.printStackTrace();
		}
		mav.addObject("status", status.name());
		mav.addObject("homeGroupId", homeGroupId);
		return mav;
	}
//	public ModelAndView registerHomeDevice(
//			@RequestParam("homeGroupId") String homeGroupId,
//			@RequestParam("homeDeviceGroupName") String homeDeviceGroupName,
//			@RequestParam("homeDeviceCategory") String homeDeviceCategory,			
//			@RequestParam("identificationID") String identificationID,
//			@RequestParam("friendlyName") String friendlyName,
//			@RequestParam("homeDeviceImg") String homeDeviceImg,
//			//@RequestParam("supplierID") String supplierID,
//			@RequestParam("operatorId") int operatorId,
//			@RequestParam("contractId") int contractId) {
//
//		//  등록 처리 결과
//		String resStatus = "success";
//		int groupKey = 0;
//
//		ModelAndView mav = new ModelAndView("jsonView");
//		log.debug("start register Home Device");
//
//		// 트렌젝션 관리
//        TransactionStatus txStatus = null;
//        DefaultTransactionDefinition txDefine = new DefaultTransactionDefinition();
//        txDefine.setIsolationLevel(TransactionDefinition.ISOLATION_READ_UNCOMMITTED);
//
//		try{
//			HomeGroup homeGroup = null;
//			MCU mcu = null;
//	    	EndDevice endDevice = new EndDevice();
//	        txStatus = transactionManager.getTransaction(txDefine);
//			Code code = codeManager.getCode(Integer.parseInt(homeDeviceCategory));
//
//	    	Contract contract = contractManager.getContract(contractId);
//	    	Supplier supplier = contract.getSupplier();
//	    	Meter meter = contract.getMeter();
//	    	Boolean isDr = contract.getCustomer().getDemandResponse();
//	    	// 홈 그룹으로 등록 되지 않았을 경우
//	    	if(homeGroupId.equals("0")) {
//	    		// 홈그룹 등록 명령 실행
//	    		homeGroup = new HomeGroup();
//
//	    		if(meter != null && meter.getMcu() != null) {
//	    			groupKey = CmdOperationUtil.cmdGroupAdd((meter.getMcu().getId()).toString(), contract.getContractNumber());
//	    			homeGroup.setHomeGroupMcu(meter.getMcu());
//	    		}
//
//	    		homeGroup.setGroupType(GroupType.HomeGroup.name());
//	    		homeGroup.setGroupKey(groupKey);
//	    		homeGroup.setAllUserAccess(false);
//	    		homeGroup.setName(contract.getContractNumber());
//	    		homeGroup.setOperator(operatorManager.getOperator(operatorId));
//	    		homeGroup.setSupplier(supplier);
//
//	    		// 홈 그룹 등록
//	    		homeGroup = groupMgmtManager.saveHomeGroup(homeGroup, "C"); // Create
//
//	    		// 등록한 홈 그룹 정보에서 ID취득
//	    		homeGroupId = homeGroup.getId().toString();
//	    	} else {
//
//				// 홈 그룹 정보 취득
//		    	homeGroup = homeDeviceMgmtManager.getHomeGroupById(Integer.parseInt(homeGroupId));
//	    	}
//
//	    	mcu = homeGroup.getHomeGroupMcu();
//
//	        List<Object> list = new ArrayList<Object>();
//			HashMap<String, Object> hashMap = new HashMap<String, Object>();
//
//	        // Group Member에 Home Device 등록
//			hashMap.put("groupId", homeGroup.getId());
//			hashMap.put("memberName", identificationID.equals("") ? friendlyName : identificationID);
//	        list.add(hashMap);
//	        groupMgmtManager.saveGroupMembers(list);
//
//	        // 스마트 콘센트 일 경우
//			if(HomeDeviceCategoryType.SMART_CONCENT.getCode().equals(code.getCode())){
//
//		        // Modem(ACD) 등록
//		        ACD acd = new ACD();
//		        acd.setModemType("ACD");
//		        acd.setProtocolType("ZigBee");
//		        acd.setDeviceSerial(identificationID);
//		        acd.setInterfaceType(Interface.IF4); //AMU	        	
//		        acd.setSupplier(supplier);
//				if(mcu != null) {
//					log.debug("####important###" + mcu.getSysID());
//					acd.setMcu(mcu);
//				}
//
//		        //acd.setMeter(meter);
//		        //acd.setModel(model)
//		        modemManager.insertModemACD(acd);
//
//		        // EndDevice(스마트 콘센트) 등록
//		    	endDevice.setFriendlyName(friendlyName);
//		    	endDevice.setSerialNumber(identificationID);
//		    	endDevice.setControllerCode(codeManager.getCodeByName("ACD"));
//		    	endDevice.setHomeDeviceGroupName(homeDeviceGroupName);
//		    	endDevice.setHomeDeviceImgFilename(homeDeviceImg);
//		    	endDevice.setSupplier(supplier);
//		    	endDevice.setCategoryCode(codeManager.getCode(Integer.parseInt(homeDeviceCategory)));
//		    	endDevice.setModem(modemManager.getModem(identificationID));
//		    	endDevice.setLoadControl(true);
//
//				// DR고객 인지 판단
//				if(isDr == null){
//			    	// DR고객이 아닐 경우, DR 거부
//			    	endDevice.setDrProgramMandatory(false);						
//				}else{
//					 endDevice.setDrProgramMandatory(isDr);
//				}
//
//				// 제품 등록시,등록 상태를 진행중으로 셋팅
//		    	endDevice.setInstallStatus(codeManager.getCodeByCode(Code.IN_PROCESS));
//
//		    	// DR레벨 취득
//		    	List<Map<String, Object>> drList = homeDeviceMgmtManager.getHomeDeviceDrLevelByCondition(Integer.parseInt(homeDeviceCategory), "");
//
//		    	// 제품 등록시에는 디폴트 DR레벨을 설정한다.
//		    	endDevice.setDrLevel((Integer)drList.get(0).get("DRLEVEL"));
//		    	endDevice.setProtocolType(Protocol.ZigBee);
//			} else if(HomeDeviceCategoryType.SMART_APPLIANCE.getCode().equals(code.getCode())) {
//
//				// 스마트 가전 일 경우
//	    		endDevice.setFriendlyName(friendlyName);
//	    		endDevice.setSerialNumber(identificationID);
//	    		endDevice.setHomeDeviceGroupName(homeDeviceGroupName);
//	    		endDevice.setHomeDeviceImgFilename(homeDeviceImg);
//	    		endDevice.setSupplier(supplier);
//	    		endDevice.setCategoryCode(codeManager.getCode(Integer.parseInt(homeDeviceCategory)));
//	    		endDevice.setInstallStatus(codeManager.getCodeByCode(Code.COMPLETED));
//			}else {
//
//	        	// EndDevice(일반 가전) 등록
//	    		endDevice.setFriendlyName(friendlyName);
//	    		endDevice.setSerialNumber(identificationID.equals("") ? friendlyName : identificationID);
//	    		endDevice.setHomeDeviceGroupName(homeDeviceGroupName);
//	    		endDevice.setHomeDeviceImgFilename(homeDeviceImg);
//	    		endDevice.setSupplier(supplier);
//	    		endDevice.setCategoryCode(codeManager.getCode(Integer.parseInt(homeDeviceCategory)));
//	    		endDevice.setInstallStatus(codeManager.getCodeByCode(Code.COMPLETED));
//			}
//			// EndDevice 등록
//			endDeviceManager.addEndDevice(endDevice);
//			transactionManager.commit(txStatus);
//
//		} catch (Exception e) {
//        	resStatus = "failed";
//        	transactionManager.rollback(txStatus);
//	    	e.getMessage();
//	    	e.printStackTrace();
//		}
//		mav.addObject("status", resStatus);
//		mav.addObject("homeGroupId", homeGroupId);
//		return mav;
//	}

	/**
	 * method name : modifyHomeDevice
	 * method Desc : 제품 정보 갱신
	 *
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value="/gadget/homeDeviceMgmt/modifyHomeDevice")
	public ModelAndView modifyHomeDevice(HttpServletRequest request,
			HttpServletResponse response) {

		//  등록 처리 결과
		ResultStatus status = ResultStatus.SUCCESS;
		ModelAndView mav = new ModelAndView("jsonView");

        TransactionStatus txStatus = null;
        DefaultTransactionDefinition txDefine = new DefaultTransactionDefinition();
        txDefine.setIsolationLevel(TransactionDefinition.ISOLATION_READ_UNCOMMITTED);
        
		int endDeviceId = Integer.parseInt(request.getParameter("id"));
		int homeGroupMemberId = Integer.parseInt(request.getParameter("homeGroupMemberId"));
		int homeGroupId = Integer.parseInt(request.getParameter("homeGroupId"));

		String modemId = request.getParameter("modemId");
		String homeDeviceGroupName = request.getParameter("homeDeviceGroupName");
		String orgHomeDeviceGroupName = request.getParameter("orgHomeDeviceGroupName");
		String homeDeviceCategory = request.getParameter("orgHomeDeviceCategory");
		String identificationID = request.getParameter("identificationID");
		String orgIdentificationID = request.getParameter("orgIdentificationID");
		String friendlyName = request.getParameter("friendlyName");
		String homeDeviceImg = request.getParameter("homeDeviceImg");
		//String supplierID = request.getParameter("supplierID");

		try{
	        txStatus = transactionManager.getTransaction(txDefine);
 
			Code code = codeManager.getCode(Integer.parseInt(homeDeviceCategory));

			// HomeGroup 정보를 취득한다.
	    	HomeGroup homeGroup = homeDeviceMgmtManager.getHomeGroupById(homeGroupId);
	    	if(homeGroup == null) {
				mav.addObject("result", "notHomeGroup");
				return mav;
			}

//			// Home Group Member Update
//	        homeDeviceMgmtManager.updateGroupMember(homeGroupMemberId, identificationID.equals("") ? friendlyName : identificationID);

	        EndDevice endDevice = endDeviceManager.getEndDevice(endDeviceId);

	        // 스마트 콘센트일 경우
	        if(HomeDeviceCategoryType.SMART_CONCENT.getCode().equals(code.getCode())){

				// Home Group Member Update
		        homeDeviceMgmtManager.updateGroupMember(homeGroupMemberId, identificationID);
		        
				// 식별아이디가 변경 되었을 때는 모뎀과 엔드디바이스의 정보를 업데이트 한다.
				if(!identificationID.equals(orgIdentificationID)){
					Modem modem = modemManager.getModem(modemId);
					modem.setDeviceSerial(identificationID);
					modemManager.updateModem(modem);
				
					// 식별아이디가 변경 되었을 때는 기존의 식별아이디로 홈그룹 추가되어있는 자산을 삭제한다.
					// 홈 그룹 맴버 삭제 컴맨드 실행
					//CmdOperationUtil.cmdGroupDeleteMember(homeGroup.getHomeGroupMcu().getSysID(), homeGroup.getGroupKey(), orgIdentificationID);					
				}
				endDevice.setSerialNumber(identificationID);
				endDevice.setHomeDeviceGroupName(homeDeviceGroupName);
				endDevice.setFriendlyName(friendlyName);
				endDevice.setHomeDeviceImgFilename(homeDeviceImg);
				endDeviceManager.updateEndDevice(endDevice);

				//homeDeviceMgmtManager.updateEndDeviceInfo(Integer.parseInt(endDeviceId), homeDeviceGroupName, friendlyName);
		    	// 그룹이 변경되었을 시에는 해당 스마트콘센트로  맵핑된 가전의 모뎀 정보를 리셋한다.
		    	if (!homeDeviceGroupName.equals(orgHomeDeviceGroupName)){
					int categoryId = codeManager.getCodeIdByCode(HomeDeviceCategoryType.GENERAL_APPLIANCE.getCode());
		    		homeDeviceMgmtManager.resetMappingInfo(Integer.parseInt(modemId), categoryId);
		    	}
			}else {	// 일반 가전일 경우
				
				// Home Group Member Update
		        homeDeviceMgmtManager.updateGroupMember(homeGroupMemberId, friendlyName);

				endDevice.setSerialNumber(friendlyName);

				endDevice.setHomeDeviceGroupName(homeDeviceGroupName);
				endDevice.setFriendlyName(friendlyName);
				endDevice.setHomeDeviceImgFilename(homeDeviceImg);
				endDeviceManager.updateEndDevice(endDevice);

		    	// 그룹이 변경되었을 시에는 해당 가전에  맵핑된 모뎀정보를 리셋한다.
		    	if (!homeDeviceGroupName.equals(orgHomeDeviceGroupName)){
		    		homeDeviceMgmtManager.resetMappingInfo(endDeviceId);
		    	}
			}
			transactionManager.commit(txStatus);
		} catch (Exception e) {
			status = ResultStatus.FAIL;
        	transactionManager.rollback(txStatus);
	    	e.getMessage();
	    	e.printStackTrace();
		}
		mav.addObject("status", status.name());
		return mav;
	}

	/**
	 * method name : deleteHomeDevice
	 * method Desc : 제품 정보 삭제
	 *
	 * @param endDeviceId
	 * @param homeGroupMemberId
	 * @param modemId
	 * @param homeGroupId
	 * @param homeDeviceCategory
	 * @return
	 */
	@RequestMapping(value="/gadget/homeDeviceMgmt/deleteHomeDevice")
	public ModelAndView deleteHomeDevice(
			@RequestParam("id") String endDeviceId,
			@RequestParam("homeGroupMemberId") String homeGroupMemberId,
			@RequestParam("modemId") String modemId,
			@RequestParam("homeGroupId") String homeGroupId,
			@RequestParam("homeDeviceCategory") String homeDeviceCategory) {

		//  등록 처리 결과
		ResultStatus status = ResultStatus.SUCCESS;
		ModelAndView mav = new ModelAndView("jsonView");

        TransactionStatus txStatus = null;
        DefaultTransactionDefinition txDefine = new DefaultTransactionDefinition();
        txDefine.setIsolationLevel(TransactionDefinition.ISOLATION_READ_UNCOMMITTED);

		try{
	        txStatus = transactionManager.getTransaction(txDefine);

			Code code = codeManager.getCode(Integer.parseInt(homeDeviceCategory));

			// HomeGroup 정보를 취득한다.
	    	HomeGroup homeGroup = homeDeviceMgmtManager.getHomeGroupById(Integer.parseInt(homeGroupId));
	    	if(homeGroup == null) {
				mav.addObject("result", "notHomeGroup");
				return mav;
			}

			// Home Group Member Update
	        homeDeviceMgmtManager.delete(Integer.parseInt(homeGroupMemberId));

	        // 스마트 콘센트일 경우
			if(HomeDeviceCategoryType.SMART_CONCENT.getCode().equals(code.getCode())){
				int categoryId = codeManager.getCodeIdByCode(HomeDeviceCategoryType.GENERAL_APPLIANCE.getCode());
				// 삭제 대상 스마트 콘센트로 맵핑된 Home Device의  맵핑정보를 리셋한다.
				homeDeviceMgmtManager.resetMappingInfo(Integer.parseInt(modemId), categoryId);

		        endDeviceManager.delete(Integer.parseInt(endDeviceId));
				modemManager.deleteModem(Integer.parseInt(modemId));
				// 홈 그룹 맴버 삭제 컴맨드 실행
				//CmdOperationUtil.cmdGroupDeleteMember(homeGroup.getHomeGroupMcu().getSysID(), homeGroup.getGroupKey(), modemManager.getModem(Integer.parseInt(modemId)).getDeviceSerial());						
			}else { // 일반가전일 경우
				endDeviceManager.delete(Integer.parseInt(endDeviceId));
			}
			transactionManager.commit(txStatus);
		} catch (Exception e) {
        	transactionManager.rollback(txStatus);
        	status = ResultStatus.FAIL;
	    	e.getMessage();
	    	e.printStackTrace();
		}

		mav.addObject("status", status.name());
		return mav;
	}

	/**
	 * method name : getHomeDeviceMappingInfoLeft
	 * method Desc : 제품 맵핑정보 취득(스마트 콘센트)
	 *
	 * @param homeGroupId
	 * @return
	 */
	@RequestMapping(value="/gadget/homeDeviceMgmt/getHomeDeviceMappingInfoLeft")
	public ModelAndView getHomeDeviceMappingInfoLeft(
			@RequestParam("homeGroupId") String homeGroupId) {
		ModelAndView mav = new ModelAndView("jsonView");
		List<Map<String, Object>> resultList = null;
		List<Map<String, Object>> list = homeDeviceMgmtManager.getHomeDeviceInfo(homeGroupId, "", Integer.toString(codeManager.getCodeIdByCode(HomeDeviceCategoryType.SMART_CONCENT.getCode())));
		if (list.size() != 0){
			List<Map<String, Object>> homeGroupList = homeDeviceMgmtManager.getHomeDeviceGroupCnt(homeGroupId);
			resultList = this.getHomeDeviceInfoGroupByHomeDeviceGroupName(list, homeGroupList);
		}
		mav.addObject("result", resultList);
		return mav;
	}

	/**
	 * method name : getHomeDeviceMappingInfoRight
	 * method Desc : 제품 맵핑정보 취득(일반 가전)
	 *
	 * @param homeGroupId
	 * @return
	 */
	@RequestMapping(value="/gadget/homeDeviceMgmt/getHomeDeviceMappingInfoRight")
	public ModelAndView getHomeDeviceMappingInfoRight(
			@RequestParam("homeGroupId") String homeGroupId) {
		ModelAndView mav = new ModelAndView("jsonView");
		List<Map<String, Object>> resultList = null;
		List<Map<String, Object>> list = homeDeviceMgmtManager.getHomeDeviceInfo(homeGroupId, "", Integer.toString(codeManager.getCodeIdByCode(HomeDeviceCategoryType.GENERAL_APPLIANCE.getCode())));
		if (list.size() !=0 ){
			List<Map<String, Object>> homeGroupList = homeDeviceMgmtManager.getHomeDeviceGroupCnt(homeGroupId);
			// 제품 그룹에 맞게 제품요소를 정렬시킨다.
			resultList = this.getHomeDeviceInfoGroupByHomeDeviceGroupName(list, homeGroupList);
		}
		mav.addObject("result", resultList);
		return mav;
	}

	/**
	 * method name : getHomeDeviceInfoGroupByHomeDeviceGroupName
	 * method Desc : Home Group Name으로 그룹핑한다.
	 *               스마트 콘센트 그리드와 일반가전 그리드의 사이즈를 동일 하게 맞춘다.
	 *
	 * @param list1
	 * @param homeGroupList
	 * @return
	 */
	private List<Map<String, Object>> getHomeDeviceInfoGroupByHomeDeviceGroupName(List<Map<String, Object>> list1,List<Map<String, Object>> homeGroupList){
		List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
		Map<String, Object> resultMap = null;
		int k = 0;
		for(Map<String, Object> map : homeGroupList){
			String homeGroupName = (String)map.get("HOMEDEVICEGROUPNAME");
			int cnt = (Integer)map.get("CNT");

			for ( int i=k; i<list1.size(); i++){
				Map<String, Object> _map = list1.get(i);
				String _homeGroupName = (String)_map.get("HOMEDEVICEGROUPNAME");
				 if(homeGroupName.equals(_homeGroupName)){
					 resultList.add(_map);
					 k++;
					 cnt--;
				 }else{
					 for (int j=0; j<cnt; j++){
						 resultMap = new HashMap<String, Object>();
						 resultMap.put("ID", "");
						 resultMap.put("HOMEDEVICEGROUPNAME", homeGroupName);
						 resultMap.put("FRIENDLYNAME", "");
						 resultMap.put("DRLEVEL", "");
						 resultMap.put("HOMEDEVICEIMGFILENAME", "");
						 resultMap.put("MODEMID", "");
						 resultMap.put("CATEGORYID", "");
						 resultMap.put("SERIALNUMBER", "");
						 resultMap.put("GROUPMEMBERID", "");
						 resultMap.put("MAPPINGID", "");
						 resultMap.put("MAPPINGFRIENDLYNAME", "");
						 resultMap.put("MAPPINGIMGURL", "");
						 resultMap.put("MAPPINGDRNAME", "");
						 resultMap.put("USAGE", "");
						 resultList.add(resultMap);
					 }
					 cnt = 0;
					 break;
				 }
			}
			for(int j=0; j<cnt; j++){
				 resultMap = new HashMap<String, Object>();
				 resultMap.put("ID", "");
				 resultMap.put("HOMEDEVICEGROUPNAME", homeGroupName);
				 resultMap.put("FRIENDLYNAME", "");
				 resultMap.put("DRLEVEL", "");
				 resultMap.put("HOMEDEVICEIMGFILENAME", "");
				 resultMap.put("MODEMID", "");
				 resultMap.put("CATEGORYID", "");
				 resultMap.put("SERIALNUMBER", "");
				 resultMap.put("GROUPMEMBERID", "");
				 resultMap.put("MAPPINGID", "");
				 resultMap.put("MAPPINGFRIENDLYNAME", "");
				 resultMap.put("MAPPINGIMGURL", "");
				 resultMap.put("MAPPINGDRNAME", "");
				 resultMap.put("USAGE", "");
				 resultList.add(resultMap);
			}
		}
		return resultList;		
	}

    /**
     * method name : updateMappingInfo
     * method Desc : 맵핑 정보 변경
     *
     * @param id
     * @param modemId
     * @return
     */
    @RequestMapping(value="/gadget/homeDeviceMgmt/updateMappingInfo")
    public ModelAndView updateMappingInfo(
			@RequestParam("id") String id,
			@RequestParam("modemId") String modemId) {

        ModelAndView mav = new ModelAndView("jsonView");
        ResultStatus status = ResultStatus.SUCCESS;
        try{
        	if(id.length() == 0 || modemId.length() == 0){
        		status = ResultStatus.FAIL;
        	} else {
        		log.debug("id : " + id + "modemId : " + modemId);
        		homeDeviceMgmtManager.updateMappingInfo(Integer.parseInt(id), Integer.parseInt(modemId));
        	}
        }catch(Exception e){
        	status = ResultStatus.FAIL;
	    	e.getMessage();
	    	e.printStackTrace();
        }

        mav.addObject("status", status.name());
        return mav;
    }

    /**
     * method name : resetMappingInfo
     * method Desc : 맵핑 정보 리셋
     *
     * @param endDeviceId
     * @return
     */
    @RequestMapping(value="/gadget/homeDeviceMgmt/resetMappingInfo")
    public ModelAndView resetMappingInfo(
			@RequestParam("endDeviceId") String endDeviceId) {

    	ResultStatus status = ResultStatus.SUCCESS;
        ModelAndView mav = new ModelAndView("jsonView");
		try{
			homeDeviceMgmtManager.resetMappingInfo(Integer.parseInt(endDeviceId));
		}catch(Exception e){
			status = ResultStatus.FAIL;
	    	e.getMessage();
	    	e.printStackTrace();
        }

        mav.addObject("status", status.name());
        return mav;
    }
}
