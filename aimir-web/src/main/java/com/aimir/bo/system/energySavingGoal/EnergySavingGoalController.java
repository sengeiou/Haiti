/**
 * EnergySavingGoalController.java Copyright NuriTelecom Limited 2011
 */

package com.aimir.bo.system.energySavingGoal;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.owasp.esapi.ESAPI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.aimir.constants.CommonConstants.MeterType;
import com.aimir.esapi.AimirAuthenticator;
import com.aimir.esapi.AimirUser;
import com.aimir.model.system.Contract;
import com.aimir.model.system.EnergySavingTarget;
import com.aimir.model.system.Notification;
import com.aimir.model.system.Operator;
import com.aimir.model.system.OperatorContract;
import com.aimir.service.system.ContractManager;
import com.aimir.service.system.OperatorManager;
import com.aimir.service.system.energySavingGoal.EnergySavingGoalManager;
import com.aimir.service.system.membership.OperatorContractManager;
import com.aimir.util.BillDateUtil;
import com.aimir.util.CalendarUtil;
import com.aimir.util.DecimalUtil;
import com.aimir.util.TimeUtil;

/**
 * EnergySavingGoalController.java Description 
 *
 * 
 * Date          Version    Author   Description
 * 2011. 6. 3.   v1.0       김상연         에너지 절감 목표
 * 2011. 8. 1.   v1.1       은미애         인수인계후 전체적인 수정
 *
 */

@Controller("savingGoalController")
public class EnergySavingGoalController {

	@Autowired
	OperatorManager operatorManager;

	@Autowired
	OperatorContractManager operatorContractManager;

	@Autowired
    ContractManager contractManager;

	@Autowired
	EnergySavingGoalManager savingGoalManager;

    /**
     * method name : loadEnergySavingGoalEmMax
     * method Desc : 전기 에너지 절감 맥스 가젯 호출(energySavingGoalEmMax)
     *
     * @return
     */
    @RequestMapping(value="/gadget/energySavingGoal/energySavingGoalEmMax")
    public ModelAndView loadEnergySavingGoalEmMax() {
    	// 에너지원별 가젯 한개의 JSP에서 통합 관리
        ModelAndView mav = new ModelAndView("/gadget/energySavingGoal/energySavingGoalMax");

		// ESAPI.setAuthenticator((Authenticator)new AimirAuthenticator());
        AimirAuthenticator instance = (AimirAuthenticator)ESAPI.authenticator();        
        AimirUser user = (AimirUser)instance.getUserFromSession();

        mav.addObject("operatorId", String.valueOf(user.getAccountId()));
        mav.addObject("serviceType", MeterType.EnergyMeter.getServiceType());

        return mav;
    }

    /**
     * method name : loadEnergySavingGoalEmMini
     * method Desc : 전기 에너지 절감 미니 가젯 호출(energySavingGoalEmMini)
     *
     * @return
     */
    @RequestMapping(value="/gadget/energySavingGoal/energySavingGoalEmMini")
    public ModelAndView loadEnergySavingGoalEmMini() {
    	// 에너지원별 가젯 한개의 JSP에서 통합 관리
    	ModelAndView mav = new ModelAndView("/gadget/energySavingGoal/energySavingGoalMini");

    	// ESAPI.setAuthenticator((Authenticator)new AimirAuthenticator());
    	AimirAuthenticator instance = (AimirAuthenticator)ESAPI.authenticator();        
    	AimirUser user = (AimirUser)instance.getUserFromSession();

        mav.addObject("operatorId", String.valueOf(user.getAccountId()));
    	mav.addObject("serviceType", MeterType.EnergyMeter.getServiceType());

    	return mav;
    }

    /**
     * method name : loadEnergySavingGoalGmMax
     * method Desc : 가스 에너지 절감 맥스 가젯 호출(energySavingGoalEmMax)
     *
     * @return
     */
    @RequestMapping(value="/gadget/energySavingGoal/energySavingGoalGmMax")
    public ModelAndView loadEnergySavingGoalGmMax() {
    	// 에너지원별 가젯 한개의 JSP에서 통합 관리
        ModelAndView mav = new ModelAndView("/gadget/energySavingGoal/energySavingGoalMax");

    	// ESAPI.setAuthenticator((Authenticator)new AimirAuthenticator());
    	AimirAuthenticator instance = (AimirAuthenticator)ESAPI.authenticator();        
    	AimirUser user = (AimirUser)instance.getUserFromSession();

        mav.addObject("operatorId", String.valueOf(user.getAccountId()));
    	mav.addObject("serviceType", MeterType.GasMeter.getServiceType());

    	return mav;
    }

    /**
     * method name : loadEnergySavingGoalGmMini
     * method Desc : 가스 에너지 절감 목표 미니 가젯 호출(energySavingGoalGmMini)
     *
     * @return
     */
    @RequestMapping(value="/gadget/energySavingGoal/energySavingGoalGmMini")
    public ModelAndView loadEnergySavingGoalGmMini() {
    	// 에너지원별 가젯 한개의 JSP에서 통합 관리
    	ModelAndView mav = new ModelAndView("/gadget/energySavingGoal/energySavingGoalMini");

    	// ESAPI.setAuthenticator((Authenticator)new AimirAuthenticator());
    	AimirAuthenticator instance = (AimirAuthenticator)ESAPI.authenticator();        
    	AimirUser user = (AimirUser)instance.getUserFromSession();

        mav.addObject("operatorId", String.valueOf(user.getAccountId()));
    	mav.addObject("serviceType", MeterType.GasMeter.getServiceType());

    	return mav;
    }

    /**
     * method name : loadEnergySavingGoalWmMax
     * method Desc : 수도 에너지 절감 목표 맥스 가젯 호출(energySavingGoalWmMax)
     *
     * @return
     */
    @RequestMapping(value="/gadget/energySavingGoal/energySavingGoalWmMax")
    public ModelAndView loadEnergySavingGoalWmMax() {
    	// 에너지원별 가젯 한개의 JSP에서 통합 관리
        ModelAndView mav = new ModelAndView("/gadget/energySavingGoal/energySavingGoalMax");

    	// ESAPI.setAuthenticator((Authenticator)new AimirAuthenticator());
    	AimirAuthenticator instance = (AimirAuthenticator)ESAPI.authenticator();        
    	AimirUser user = (AimirUser)instance.getUserFromSession();

        mav.addObject("operatorId", String.valueOf(user.getAccountId()));
    	mav.addObject("serviceType", MeterType.WaterMeter.getServiceType());

    	return mav;
    }

    /**
     * method name : loadEnergySavingGoalWmMini
     * method Desc : 수도 에너지 절감 목표 미니 가젯 호출(energySavingGoalWmMini)
     *
     * @return
     */
    @RequestMapping(value="/gadget/energySavingGoal/energySavingGoalWmMini")
    public ModelAndView loadEnergySavingGoalWmMini() {
    	// 에너지원별 가젯 한개의 JSP에서 통합 관리
    	ModelAndView mav = new ModelAndView("/gadget/energySavingGoal/energySavingGoalMini");

    	// ESAPI.setAuthenticator((Authenticator)new AimirAuthenticator());
    	AimirAuthenticator instance = (AimirAuthenticator)ESAPI.authenticator();        
    	AimirUser user = (AimirUser)instance.getUserFromSession();

        mav.addObject("operatorId", String.valueOf(user.getAccountId()));
    	mav.addObject("serviceType", MeterType.WaterMeter.getServiceType());

    	return mav;
    }

    /**
     * method name : getSelect
     * method Desc : 계약 정보 취득
     *
     * @param operatorId
     * @param serviceType
     * @return
     */
    @RequestMapping(value="/gadget/energySavingGoal/getSelect")
    public ModelAndView getSelect(
    		@RequestParam("operatorId") int operatorId,
    		@RequestParam("serviceType") String serviceType) {
        ModelAndView mav = new ModelAndView("jsonView");

        Operator operator = operatorManager.getOperator(operatorId);
        List<OperatorContract> operatorContracts = operatorContractManager.getOperatorContractByOperator(operator);
        List<Map<String, Object>> contracts = new ArrayList<Map<String,Object>>();
        Map<String, Object> contract;
        
        boolean isNotService = false;
        
        // 계약정보 콤보 박스 생성을 위한 정보 설정
        for (OperatorContract operatorContract : operatorContracts) {
        	
        	if ( serviceType.equals(operatorContract.getContract().getServiceTypeCode().getCode()) 
        			&& 1 == operatorContract.getContractStatus() ) {
        		
            	contract = new HashMap<String, Object>();
        		
            	contract.put("id", operatorContract.getId());
            	// 콤보 박스 name : 명칭표시
            	contract.put("name", operatorContract.getFriendlyName());

        		contracts.add(contract);
        	} 
        }

        if(contracts.size() == 0) {
        	isNotService = true;
        }

        mav.addObject("isNotService",isNotService);
        mav.addObject("contracts", contracts);

        return mav;
   }

    /**
     * method name : getSavingTargetInfo
     * method Desc : 에너지 목표 설정 정보 취득
     *
     * @param operatorContractId 아이디
     * @param selYear 선택한 절감 목표 설정 년도
     * @param selMonth 선택한 절감 목표 설정 월
     * @return
     */
    @RequestMapping(value="/gadget/energySavingGoal/getSavingTargetInfo")
    public ModelAndView getSavingTargetInfo(
    		@RequestParam("operatorContractId") int operatorContractId
    		,@RequestParam("year") String selYear
    		,@RequestParam("month") String selMonth) {

    	ModelAndView mav = new ModelAndView("jsonView");
    	OperatorContract operatorContract = new OperatorContract();

    	operatorContract.setId(operatorContractId);

    	List<OperatorContract> operatorContracts = operatorContractManager.getOperatorContract(operatorContract);
    	
    	boolean resultStatus = false;

    	if ( 1 == operatorContracts.size() ) {

    		operatorContract = operatorContracts.get(0);
        	Contract contract = operatorContract.getContract();	        
	        
        	// 과금일 취득
        	int billDate = contract.getBillDate() == null ? 1 : contract.getBillDate();

        	String nextBillDay = selYear + selMonth + (billDate < 10 ? "0" + billDate : billDate);
        	String lastbillDay = CalendarUtil.getDate(nextBillDay, Calendar.MONTH, -1);
        	String billToMonth = CalendarUtil.getDate(nextBillDay, Calendar.DAY_OF_MONTH, -1);

            mav.addObject("contract", contract);
            mav.addObject("maxDay",  billToMonth);
            mav.addObject("savingTarget", savingGoalManager.getSavingTarget(operatorContractId, billToMonth).intValue());
            mav.addObject("maxBill", savingGoalManager.getMaxBill(contract, null).intValue());
            mav.addObject("lastMonthBill",  savingGoalManager.getLastMonthBill(contract, lastbillDay, billToMonth).intValue());
            mav.addObject("lastYearSameMonthBill", savingGoalManager.getLastYearSameMonthBill(contract, lastbillDay, billToMonth).intValue());
            // 현재 금액으로 변경한다.
            mav.addObject("forecastBill", savingGoalManager.getForecastBill(contract, lastbillDay, billToMonth).intValue());

        	resultStatus = true;
    	} 
    	
        mav.addObject("resultStatus", resultStatus);
        
    	return mav;
    }
    
    @RequestMapping(value="/gadget/energySavingGoal/getNotificationInfo")
    public ModelAndView getNotificationInfo(
    		@RequestParam("operatorContractId") int operatorContractId
    		,@RequestParam("maxDay") String maxDay) {

    	ModelAndView mav = new ModelAndView("jsonView");
    	OperatorContract operatorContract = new OperatorContract();
    	
    	operatorContract.setId(operatorContractId);
    	
    	List<OperatorContract> operatorContracts = operatorContractManager.getOperatorContract(operatorContract);
    	
    	boolean resultStatus = false;

    	if ( 1 == operatorContracts.size() ) {

    		operatorContract = operatorContracts.get(0);
 
        	EnergySavingTarget energySavingTarget = new EnergySavingTarget();
        	energySavingTarget.setCreateDate(maxDay);
        	energySavingTarget.setOperatorContract(operatorContract);
        		
        	List<EnergySavingTarget> energySavingTargets = savingGoalManager.getEnergySavingTarget(energySavingTarget);

        	if (0 < energySavingTargets.size()) {
        		
        		energySavingTarget = energySavingTargets.get(0);
        	}
	
        	Notification notification = energySavingTarget.getNotification();
        	
        	if (null != notification) {
                mav.addObject("period1", notification.getPeriod_1());
                mav.addObject("period2", notification.getPeriod_2());
                mav.addObject("period3", notification.getPeriod_3());
                mav.addObject("period4", notification.getPeriod_4());
                mav.addObject("period5", notification.getPeriod_5());
                mav.addObject("comValue", notification.getConditionValue());
        	}
        	resultStatus = true;
    	} 

        mav.addObject("resultStatus", resultStatus);
        
    	return mav;
    }

    /**
     * method name : getContract
     * method Desc : 계약 정보 조회
     *
     * @param contractId
     * @return
     */
    @RequestMapping(value="/gadget/energySavingGoal/getContract")
    public ModelAndView getContract(
    		@RequestParam("operatorContractId") int operatorContractId
    		,@RequestParam("year") String selYear
    		,@RequestParam("month") String selMonth) {

    	ModelAndView mav = new ModelAndView("jsonView");
    	OperatorContract operatorContract = new OperatorContract();
    	
    	operatorContract.setId(operatorContractId);
    	
    	List<OperatorContract> operatorContracts = operatorContractManager.getOperatorContract(operatorContract);
    	
    	boolean resultStatus = false;

    	if ( 1 == operatorContracts.size() ) {

    		operatorContract = operatorContracts.get(0);
        	Contract contract = operatorContract.getContract();

        	int billDate = contract.getBillDate() == null ? 1 : contract.getBillDate();
        	String nextBillDay = selYear + (Integer.parseInt(selMonth) < 10 ? "0" + selMonth : selMonth) + (billDate < 10 ? "0" + billDate : billDate);
        	String lastBillDay = CalendarUtil.getDate(nextBillDay, Calendar.MONTH, -1);
        	String billToMonth = CalendarUtil.getDate(nextBillDay, Calendar.DAY_OF_MONTH, -1);


        	Double savingTarget = savingGoalManager.getSavingTarget(operatorContractId, billToMonth);        	
        	Double maxBill = savingGoalManager.getMaxBill(contract, null);
        	Double lastMonthBill = savingGoalManager.getLastMonthBill(contract, lastBillDay, billToMonth);
        	Double lastYearSameMonthBill = savingGoalManager.getLastYearSameMonthBill(contract, lastBillDay, billToMonth);
        	Double forecastBill = savingGoalManager.getForecastBill(contract, lastBillDay, billToMonth);

        	EnergySavingTarget energySavingTarget = new EnergySavingTarget();
        	energySavingTarget.setCreateDate(billToMonth);
        	energySavingTarget.setOperatorContract(operatorContract);

        	List<EnergySavingTarget> energySavingTargets = savingGoalManager.getEnergySavingTarget(energySavingTarget);

        	if (0 < energySavingTargets.size()) {
        		
        		energySavingTarget = energySavingTargets.get(0);
        	}
        	
        	Notification notification = energySavingTarget.getNotification();
        	
        	if (null != notification) {
                mav.addObject("period1", notification.getPeriod_1());
                mav.addObject("period2", notification.getPeriod_2());
                mav.addObject("period3", notification.getPeriod_3());
                mav.addObject("period4", notification.getPeriod_4());
                mav.addObject("period5", notification.getPeriod_5());
                mav.addObject("comValue", notification.getConditionValue());
        	}

            mav.addObject("contract", contract);
            mav.addObject("maxDay",  billToMonth);
            mav.addObject("savingTarget", savingTarget.intValue());
            mav.addObject("maxBill", maxBill.intValue());
            mav.addObject("lastMonthBill",  lastMonthBill.intValue());
            mav.addObject("lastYearSameMonthBill", lastYearSameMonthBill.intValue());
            mav.addObject("forecastBill", forecastBill.intValue());

        	resultStatus = true;
    	} 
    	
        mav.addObject("resultStatus", resultStatus);
        
    	return mav;
    }
    

    /**
     * method name : saveSavingTarget
     * method Desc : 에너지 목표 설정 정보 저장
     *
     * @param operatorContractId
     * @param savingTarget
     * @param maxDay
     * @return
     */
    @RequestMapping(value="/gadget/energySavingGoal/saveSavingTarget")
    public ModelAndView saveSavingTarget(
    		@RequestParam("operatorContractId") int operatorContractId,
    		@RequestParam("savingTarget") Double savingTarget,
    		@RequestParam("maxDay") String maxDay,
    		@RequestParam("year") String selYear,
    		@RequestParam("month") String selMonth) {

    	boolean resultStatus = false;

    	try {
    		// 처음 목표 설정 시에만, 디폴트 통보 정보 등록
    		if(savingGoalManager.saveSavingTarget(operatorContractId, savingTarget, maxDay)) {

	        	Notification notification = new Notification();
	        	notification.setPeriod_1(true);
	        	notification.setPeriod_3(true);

	        	savingGoalManager.saveNoticeTarget(operatorContractId, maxDay, notification);
    		}

    		resultStatus = true;
    	} catch (Exception e) {
    		
    		e.printStackTrace();
    	}
    	
    	ModelAndView mav = new ModelAndView("jsonView");
    	
        mav.addObject("resultStatus", resultStatus);

    	OperatorContract operatorContract = new OperatorContract();

    	operatorContract.setId(operatorContractId);

    	List<OperatorContract> operatorContracts = operatorContractManager.getOperatorContract(operatorContract);
    	
    	if (1 == operatorContracts.size()) {
    		
        	DecimalFormat dfCd = DecimalUtil.getDecimalFormat(operatorContracts.get(0).getContract().getSupplier().getCd());
        	
    		mav.addObject("savingTarget", dfCd.format(savingTarget));
    	}
        
    	return mav;
    }
    
    /**
     * method name : getContractGrid
     * method Desc : 그리드 데이터 조회
     *
     * @param operatorContractId
     * @param maxDay
     * @return
     */
    @RequestMapping(value="/gadget/energySavingGoal/getContractGrid")
    public ModelAndView getContractGrid(
    		@RequestParam("operatorContractId") int operatorContractId,
    		@RequestParam("maxDay") String maxDay) {

    	List<Map<String, Object>> gridData = savingGoalManager.getContractGrid(operatorContractId, maxDay);

    	ModelAndView mav = new ModelAndView("jsonView");
    	mav.addObject("gridData", gridData);

    	return mav;
    }
 
    @RequestMapping(value="/gadget/energySavingGoal/getEnergySavingResultsYearComboBox")
    public ModelAndView getEnergySavingResultsYearComboBox(@RequestParam("operatorContractId") int operatorContractId) {
   	 	ModelAndView mav = new ModelAndView("jsonView");

    	String startDate = "";
    	String endDate = "";
    	String maxDay = "";

        try{
        	OperatorContract operatorContract = new OperatorContract();
        	
        	operatorContract.setId(operatorContractId);
        	
        	List<OperatorContract> operatorContracts = operatorContractManager.getOperatorContract(operatorContract);	
        
        	if ( 1 == operatorContracts.size() ) {

        		maxDay = BillDateUtil.getMonthToDate(operatorContracts.get(0).getContract(), TimeUtil.getCurrentDay(), 0);
        	}
	        
	    	List<Map<String, Object>> list = savingGoalManager.getEnergySavingResultsYearComboBox(operatorContractId, maxDay);
	    	
	    	mav.addObject("yearValues", list);

        }catch(Exception e){
        	e.printStackTrace();
        }

        mav.addObject("startDate", startDate);
        mav.addObject("endDate", endDate);
        mav.addObject("maxDay",maxDay);

        return mav;
    }

    @RequestMapping(value="/gadget/energySavingGoal/getEnergySavingResultsMonthComboBox")
    public ModelAndView getEnergySavingResultsMonthComboBox(
    		 @RequestParam("operatorContractId") int operatorContractId
    		,@RequestParam("selYear") String selYear) {
   	 	ModelAndView mav = new ModelAndView("jsonView");

    	String startDate = "";
    	String endDate = "";
    	String maxDay = "";

        try{
        	OperatorContract operatorContract = new OperatorContract();
        	
        	operatorContract.setId(operatorContractId);

        	List<OperatorContract> operatorContracts = operatorContractManager.getOperatorContract(operatorContract);	
        
        	if ( 1 == operatorContracts.size() ) {

        		maxDay = BillDateUtil.getMonthToDate(operatorContracts.get(0).getContract(), TimeUtil.getCurrentDay(), 0);
        	}
	        
	    	List<Map<String, Object>> list = savingGoalManager.getEnergySavingResultsMonthComboBox(operatorContractId, selYear);
	    	
	    	mav.addObject("monthValues", list);

        }catch(Exception e){
        	e.printStackTrace();
        }

        mav.addObject("startDate", startDate);
        mav.addObject("endDate", endDate);
        mav.addObject("maxDay",maxDay);

        return mav;
    }

    /**
     * method name : saveNoticeTarget
     * method Desc : 에너지 통보 설정 정보 저장
     *
     * @param operatorContractId
     * @param maxDay
     * @param smsYn
     * @param eMailYn
     * @param period_1
     * @param period_2
     * @param period_3
     * @param period_4
     * @param period_5
     * @param smsAddress
     * @param eMailAddress
     * @param conditionValue
     * @return
     */
    @RequestMapping(value="/gadget/energySavingGoal/saveNoticeTarget")
    public ModelAndView saveNoticeTarget(
    		@RequestParam("operatorContractId") int operatorContractId,
    		@RequestParam("maxDay") String maxDay,
    		@RequestParam("period_1") boolean period_1,
    		@RequestParam("period_2") boolean period_2,
    		@RequestParam("period_3") boolean period_3,
    		@RequestParam("period_4") boolean period_4,
    		@RequestParam("period_5") boolean period_5,
    		@RequestParam("conditionValue") String conditionValue) {
    	
    	Notification notification = new Notification();

    	notification.setPeriod_1(period_1);
    	notification.setPeriod_2(period_2);
    	notification.setPeriod_3(period_3);
    	notification.setPeriod_4(period_4);
    	notification.setPeriod_5(period_5);
    	if(conditionValue.length() > 0) {
    		notification.setConditionValue(Integer.parseInt(conditionValue));
    	}

    	boolean resultStatus = true;

    	try {
    		
    		resultStatus = savingGoalManager.saveNoticeTarget(operatorContractId, maxDay, notification);
    	} catch (Exception e) {
    		
    		e.printStackTrace();
    	}
    	
    	ModelAndView mav = new ModelAndView("jsonView");

        mav.addObject("resultStatus", resultStatus);

    	return mav;
    }
    
    @RequestMapping(value="/gadget/energySavingGoal/getYear")
    public ModelAndView getYear(@RequestParam("operatorContractId") int operatorContractId) {

   	 	ModelAndView mav = new ModelAndView("jsonView");

    	String today = "";
    	String startDate = "";
    	String endDate = "";
    	String maxDay = "";

        try{
        	OperatorContract operatorContract = new OperatorContract();
        	operatorContract.setId(operatorContractId);
        	List<OperatorContract> operatorContracts = operatorContractManager.getOperatorContract(operatorContract);

        	today = TimeUtil.getCurrentDay(); // yyyymmdd
	        startDate = BillDateUtil.getBillDate(operatorContracts.get(0).getContract(), today, 0);
	        maxDay = BillDateUtil.getMonthToDate(operatorContracts.get(0).getContract(), today, 1);
	        endDate = CalendarUtil.getDate(startDate, Calendar.MONTH, 12); // 과금일로부터 12개월 후의 일자를 취득한다.

        }catch(Exception e){
        	e.printStackTrace();
        }

        mav.addObject("startDate", startDate);
        mav.addObject("endDate", endDate);
        mav.addObject("maxDay",maxDay);

        return mav;
    }

    @RequestMapping(value="/gadget/energySavingGoal/getMonth")
    public ModelAndView getMonth(@RequestParam("year") String selYear
    		                    ,@RequestParam("startDate") String startDate
    		                    ,@RequestParam("endDate") String endDate) {

   	 	ModelAndView mav = new ModelAndView("jsonView");

    	String fromMonth = "1";
    	String toMonth = "12";
        try{

        	if(selYear.equals(startDate.substring(0, 4))) {
        		fromMonth = (startDate.substring(4,6)).startsWith("0") == true ? (startDate.substring(4,6)).substring(1) : startDate.substring(4,6); 
        	} else if(selYear.equals(endDate.substring(0, 4))) {
        		toMonth = (endDate.substring(4,6)).startsWith("0") == true ? (endDate.substring(4,6)).substring(1) : endDate.substring(4,6); 
        	}
        }catch(Exception e){
        	e.printStackTrace();
        }

        mav.addObject("fromMonth", fromMonth);
        mav.addObject("toMonth", toMonth);

        return mav;
    }

    @RequestMapping(value="/gadget/energySavingGoal/getYearMonth")
    public ModelAndView getYearMonth(
            @RequestParam("year") String year,
            @RequestParam("month") String month,
            @RequestParam("addVal") String addVal,
            @RequestParam("maxDay") String maxDay,
            @RequestParam("startDate") String startDate,
            @RequestParam("endDate") String endDate) {

        StringBuffer sb = new StringBuffer();

        sb.append(year);
        sb.append(Integer.parseInt(month) < 10 ? "0" + month : month);
        sb.append("01");

        String resultDate = CalendarUtil.getDate(sb.toString(), Calendar.MONTH, Integer.parseInt(addVal));

        if(startDate.compareTo(resultDate) > 0) {
        	resultDate = startDate;
        } else if(endDate.compareTo(resultDate) < 0) {
        	resultDate = endDate;
        }

        String resYear = resultDate.substring(0,4);
        String resMonth = resultDate.substring(4,6);

        resMonth = Integer.toString(Integer.parseInt(resMonth));

        ModelAndView mav = new ModelAndView("jsonView");

        mav.addObject("year",resYear);
        mav.addObject("month",resMonth);

        return mav;
    }
}
