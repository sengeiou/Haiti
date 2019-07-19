/**
 * EnergyConsumptionMonitoringController.java Copyright NuriTelecom Limited 2011
 */

package com.aimir.bo.system.energyConsumptionMonitoring;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
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
import com.aimir.model.system.Operator;
import com.aimir.model.system.OperatorContract;
import com.aimir.model.system.Supplier;
import com.aimir.service.system.ContractManager;
import com.aimir.service.system.OperatorManager;
import com.aimir.service.system.energyConsumptionMonitoring.EnergyConsumptionMonitoringManager;
import com.aimir.service.system.energySavingGoal.EnergySavingGoalManager;
import com.aimir.service.system.membership.OperatorContractManager;
import com.aimir.util.BillDateUtil;
import com.aimir.util.DecimalUtil;
import com.aimir.util.TimeLocaleUtil;
import com.aimir.util.TimeUtil;

/**
 * EnergyConsumptionMonitoringController.java Description 
 *
 * 
 * Date          Version     Author   Description
 * 2011. 5. 30.   v1.0       김상연         에너지 사용량 모니터링 화면
 * 2011. 8. 1     v1.1       은미애         전체적 수정
 *
 */

@Controller
public class EnergyConsumptionMonitoringController {

	@Autowired
	OperatorManager operatorManager;
	
	@Autowired
	OperatorContractManager operatorContractManager;
	
	@Autowired
	ContractManager contractManager;
	
    @Autowired
    EnergyConsumptionMonitoringManager energyConsumptionMonitoringManager;
    
    @Autowired    
    EnergySavingGoalManager savingGoalManager;

    /**
     * method name : loadConsumptionMonitoringMax
     * method Desc : consumptionMonitoringMax 페이지 호출
     *
     * @return
     */
    @RequestMapping(value="/gadget/energyConsumptionMonitoring/consumptionMonitoringMax")
    public ModelAndView loadConsumptionMonitoringMax() {
    	
        ModelAndView mav = new ModelAndView("/gadget/energyConsumptionMonitoring/consumptionMonitoringMax");

		// ESAPI.setAuthenticator((Authenticator)new AimirAuthenticator());
        AimirAuthenticator instance = (AimirAuthenticator)ESAPI.authenticator();        
        AimirUser user = (AimirUser)instance.getUserFromSession();

        // 로그인 고객의 PK
        mav.addObject("operatorId", String.valueOf(user.getAccountId()));
        // 전기 타입
        mav.addObject("serviceTypeEM", MeterType.EnergyMeter.getServiceType());
        // 가스 타입
        mav.addObject("serviceTypeGM", MeterType.GasMeter.getServiceType());
        // 수도 타입
        mav.addObject("serviceTypeWM", MeterType.WaterMeter.getServiceType());
        
        return mav;
    }

    /**
     * method name : getSelect
     * method Desc : 계약 정보 콤보 박스 생성을 위한 정보 취득
     *
     * @param operatorId
     * @param serviceType
     * @return
     */
    @RequestMapping(value="/gadget/energyConsumptionMonitoring/getSelect")
    public ModelAndView getSelect(
    		@RequestParam("operatorId") int operatorId,
    		@RequestParam("serviceType") String serviceType) {

        ModelAndView mav = new ModelAndView("jsonView");

        Operator operator = operatorManager.getOperator(operatorId);
        List<OperatorContract> operatorContracts = operatorContractManager.getOperatorContractByOperator(operator);
        List<Map<String, Object>> contracts = new ArrayList<Map<String,Object>>();
        Map<String, Object> contract;

        for (OperatorContract operatorContract : operatorContracts) {

        	if ( serviceType.equals(operatorContract.getContract().getServiceTypeCode().getCode())
        			&& 1 == operatorContract.getContractStatus() ) {

            	contract = new HashMap<String, Object>();
            	contract.put("id", operatorContract.getContract().getId());
            	// 콤보 박스 name : "계약번호 (주소)" 형태로 표시
            	//contract.put("name", operatorContract.getContract().getContractNumber() + " (" + operatorContract.getContract().getCustomer().getAddress2() + ")");
        		// 콤보 박스 : 계약번호 -> 명칭으로 변경
            	contract.put("name", operatorContract.getFriendlyName());
            	contracts.add(contract);
        	}
        }

        mav.addObject("contracts", contracts);
        mav.addObject("contractCount", contracts.size());

        return mav;
   }

    /**
     * method name : getContract
     * method Desc : 계약 정보 조회및 에너지 사용 정보 취득
     *
     * @param contractId
     * @return
     */
    @RequestMapping(value="/gadget/energyConsumptionMonitoring/getContract")
    public ModelAndView getContract(
    		@RequestParam("contractId") int contractId
    	   ,@RequestParam("operatorId") int operatorId
    	   ,@RequestParam("selDate") String selDate) {

    	ModelAndView mav = new ModelAndView("jsonView");

    	// 변수 선언 부
    	Map<String, Object> lastMap = null;
    	Map<String, Object> firstMap = null;
    	String lastDay = "";
    	String displayDate = "" ;
    	Double usage = 0.0;
    	Double bill = 0.0;
    	Double beforeUsage = 0.0;
    	Double beforeBill = 0.0;
    	Double beforeTotalUsage = 0.0;
    	Double beforeTotalBill = 0.0;
    	Double totalUsage = 0.0;
    	Double totalBill = 0.0;
    	Double yAxisMaxValue = 0d;
    	Double totalYAxisMaxValue = 0d;
    	Double monthlySavingTarget = 0d;
    	boolean hasTarget = true;

    	// 계약 정보 취득
    	Contract contract = contractManager.getContract(contractId);

    	try{

    		// 가장 최근 빌링 정보 취득
    		lastMap = energyConsumptionMonitoringManager.getLast(contract);

    		// 처음 빌링 정보 취득
    		firstMap = energyConsumptionMonitoringManager.getFirst(contract);

    		if(lastMap == null) { // Billing데이터가 존재 하지 않을 경우, 일자 : 오늘날짜, 사용량과 요금은 전부 0.0으로 설정한다.
    			lastDay = selDate.length() == 0 ? TimeUtil.getCurrentDay() : selDate;
    			displayDate = lastDay;
    		} else {
    			// My Report의 달력에서 일자를 클릭했을때
    			if(selDate.length() != 0) {
            		lastDay = selDate;
            		displayDate = selDate;
    				if( selDate.compareTo(lastMap.get("lastDay").toString()) <= 0 && selDate.compareTo(firstMap.get("firstDay").toString()) >= 0) {
	            		lastMap.clear();
	            		lastMap = energyConsumptionMonitoringManager.getSelDate(contract, selDate);
	            		if(lastMap != null) {
			    	    	usage = (Double) (lastMap.get("usage") == null ? 0.0 : lastMap.get("usage"));
			    	    	bill = (Double) (lastMap.get("bill") == null ? 0.0 : lastMap.get("bill"));
	            		}
    				}
            	} else {
	    	    	lastDay = lastMap.get("lastDay").toString();
            		displayDate = TimeUtil.getCurrentDay();
	    	    	usage = (Double) (lastMap.get("usage") == null ? 0.0 : lastMap.get("usage"));
	    	    	bill = (Double) (lastMap.get("bill") == null ? 0.0 : lastMap.get("bill"));
            	}
    		}

    		// 과금일 취득
	        String billDate = BillDateUtil.getBillDate(contract, lastDay, -1);
	        // Month To Date취득
	        String monthToDate = BillDateUtil.getMonthToDate(contract, lastDay, 1);

	    	// 전일 에너지 사용 정보 취득
	    	Map<String, Object> beforeDayMap = energyConsumptionMonitoringManager.getBeforeDayUsageInfo(contract, this.getYesterday(lastDay));
	    	if(beforeDayMap != null) { // Billing데이터가 존재 하지 않을 경우, 디폴트 셋팅
	        	beforeUsage = (Double) (beforeDayMap.get("beforeUsage") == null ? 0.0 : beforeDayMap.get("beforeUsage"));
	        	beforeBill = (Double) (beforeDayMap.get("beforeBill") == null ? 0.0 : beforeDayMap.get("beforeBill"));    		
	    	}
	        
	        // 과금일 기준 전월 에너지 사용정보 취득
	        Map<String, Object> beforeMonthMap = energyConsumptionMonitoringManager.getBeforeMonthUsageInfo(contract, lastDay);

	        if(beforeMonthMap != null) { // 전월에너지 사용정보가 없을때는 전월 정보를 0으로 설정
	            // 전월 사용량
	            beforeTotalUsage = (Double)(beforeMonthMap.get("beforeUsage") == null ? 0.0 : beforeMonthMap.get("beforeUsage"));
	            // 전월 사용요금
	            beforeTotalBill = (Double)(beforeMonthMap.get("beforeBill") == null ? 0.0 : beforeMonthMap.get("beforeBill"));        	
	        }
	        
	        // 전월 과금일 이후 ~ 현재까지 에너지 사용정보 취득
	    	Map<String, Object> totalMap = energyConsumptionMonitoringManager.getTotal(contract, billDate, monthToDate);   	
	    	if(totalMap != null) {
	        	totalUsage = (Double) (totalMap.get("totalUsage") == null ? 0.0 : totalMap.get("totalUsage"));
	        	totalBill = (Double) (totalMap.get("totalBill") == null ? 0.0 : totalMap.get("totalBill"));	
	    	}

	    	// 일 절감 목표 취득(전년도 MAX)
	    	Double dailySavingTarget = energyConsumptionMonitoringManager.getMaxBill(contract, getLastYear(TimeUtil.getCurrentDay()));

	    	// 월 절감 목표 취득
	    	OperatorContract operatorContract = new OperatorContract();
	    	operatorContract.setContract(contract);
	    	operatorContract.setOperator(operatorManager.getOperator(operatorId));
	    	List<OperatorContract> list = operatorContractManager.getOperatorContract(operatorContract);
	    	monthlySavingTarget = savingGoalManager.getSavingTarget(list.get(0).getId(), monthToDate);

	    	// 월 절감 목표 미설정시, 전년도 MAX를 타겟으로 설정한다.
	    	if(monthlySavingTarget.equals(0.0)) {
	    		hasTarget = false;
	    		monthlySavingTarget = savingGoalManager.getMaxBill(contract, getLastYear(TimeUtil.getCurrentDay()));
	    	}

	    	// 계약정보에서 공급사 정보 취득
	     	Supplier supplier = contract.getSupplier();

	     	// 공급사별 날짜, 사용량, 요금 포멧설정을 위한 정보 취득
	    	String lang    = supplier.getLang().getCode_2letter();
			String country = supplier.getCountry().getCode_2letter();
	    	DecimalFormat dfMd = DecimalUtil.getDecimalFormat(contract.getSupplier().getMd());
	    	DecimalFormat dfCd = DecimalUtil.getDecimalFormat(contract.getSupplier().getCd());

	    	// 계약정보 설정
	    	mav.addObject("contract", contract);

	    	// 과금일 설정
	    	int billingDate = contract.getBillDate() == null ? 1 : contract.getBillDate();
	    	mav.addObject("billDate", billingDate < 10 ? "0" + billingDate : billingDate);
	    	
	    	// 챠트 limit 값 취득 : target설정시, 그래프 값보다 타겟값이 클경우 그래프에 표시 되지 않는 현상이 발생 
	    	// 그래서 그래프값이 타겟보다 적을 경우 챠트 리밋값을 타겟값으로 설정하여 그래프에서 타겟정보가 보이게 함
	        if(bill < dailySavingTarget && beforeBill < dailySavingTarget ) {
	        	//yAxisMaxValue = Math.round(dailySavingTarget);
//	        	double d2 = d1 / 100;
//	        	double d3 = Math.ceil(d2);
//	        	double d4 = d3 * 100;
//	        	yAxisMaxValue = dailySavingTarget;
	        	yAxisMaxValue =  (Math.ceil(dailySavingTarget/100))*100;
	        }

	        if(totalBill < monthlySavingTarget && beforeTotalBill < monthlySavingTarget ) {
//	        	totalYAxisMaxValue = monthlySavingTarget;
	        	totalYAxisMaxValue = (Math.ceil(monthlySavingTarget/100))*100;
	        }

	        // 설정된 목표가 있는지 판단
	    	mav.addObject("target", hasTarget);

	        /***********************************************/
	        /*           텍스트 구성값 설정                                        */
	        /***********************************************/
	    	// 사용일
	        mav.addObject("lastDay", TimeLocaleUtil.getLocaleDateByMediumFormat(lastDay, lang, country));
	        // 당일 사용량
	        mav.addObject("usage", dfMd.format(usage));
	        // 당일 사용요금
	        mav.addObject("bill", dfCd.format(bill));
	        // 전일 사용량
	        mav.addObject("beforeUsage", dfMd.format(beforeUsage));
	        // 전일 사용요금
	        mav.addObject("beforeBill", dfCd.format(beforeBill));

	        // 월 사용 기간
	        mav.addObject("period", TimeLocaleUtil.getLocaleDateByMediumFormat(billDate, lang, country) + " ~ " + TimeLocaleUtil.getLocaleDateByMediumFormat(monthToDate, lang, country));
	        // 당월 사용량
	        mav.addObject("totalUsage", dfMd.format(totalUsage));
	        // 당월 사용요금
	        mav.addObject("totalBill", dfCd.format(totalBill));
	        // 전월 사용량
	        mav.addObject("beforeTotalUsage", dfMd.format(beforeTotalUsage));
	        // 전월 사용요금
	        mav.addObject("beforeTotalBill", dfCd.format(beforeTotalBill));

	        /***********************************************/
	        /*           그래프 구성값 설정                                        */
	        /***********************************************/
	        // 당일 요금
	        mav.addObject("billNumber", bill.intValue());
	        // 전일 요금
	        mav.addObject("beforeBillNumber", beforeBill.intValue());
	        // 타겟
	        mav.addObject("dailySavingTarget", dfCd.format(dailySavingTarget));
	        mav.addObject("dailySavingTargetNumber", dailySavingTarget.intValue());
	        // 챠트 Limit값 설정
	        mav.addObject("yAxisMaxValue", yAxisMaxValue.intValue());

	        // 당월 요금
	        mav.addObject("totalBillNumber", totalBill.intValue());
	        // 전월 요금
	        mav.addObject("beforeTotalBillNumber", beforeTotalBill.intValue());
	        // 타겟
	        mav.addObject("monthlySavingTarget", dfCd.format(monthlySavingTarget));
	        mav.addObject("monthlySavingTargetNumber", monthlySavingTarget.intValue());
	        // 챠트 Limit값 설정
	        mav.addObject("totalYAxisMaxValue", totalYAxisMaxValue.intValue());

	        /***********************************************/
	        /*           달력 표시값 설정                                           */
	        /***********************************************/	        
	        mav.addObject("displayDate", TimeLocaleUtil.getLocaleDateByMediumFormat(displayDate, lang, country));

    	}catch(Exception e){
    		e.printStackTrace();
    	}

    	return mav;
    }

    /**
     * method name : getYesterday
     * method Desc : 기준일자의 전일자 취득
     *
     * @param contract
     * @param someDay
     * @return
     */
    private String getYesterday(String someDay) {

        int year = Integer.parseInt(someDay.substring(0, 4));
        int month = Integer.parseInt(someDay.substring(4, 6)) - 1;
        int date = Integer.parseInt(someDay.substring(6, 8));

        Calendar calendar =  Calendar.getInstance();

        // 전일 셋팅
        calendar.set(year, month, date - 1);

        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");

        return formatter.format(calendar.getTime());
    }

    /**
     * method name : getLastYear
     * method Desc : 기준년도의 전년도 취득
     *
     * @param someYear
     * @return
     */
    private String getLastYear(String someYear) {

        int year = Integer.parseInt(someYear.substring(0, 4));
        int month = 0;
        int date = 1;

        Calendar calendar =  Calendar.getInstance();

        // 전년도 셋팅
        calendar.set(year - 1, month, date);

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy");

        return formatter.format(calendar.getTime());
    }
}
