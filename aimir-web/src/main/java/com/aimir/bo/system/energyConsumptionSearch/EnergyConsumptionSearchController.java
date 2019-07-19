/**
 * EnergyConsumptionSearchController.java Copyright NuriTelecom Limited 2011
 */

package com.aimir.bo.system.energyConsumptionSearch;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.owasp.esapi.ESAPI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.aimir.constants.CommonConstants;
import com.aimir.constants.CommonConstants.DefaultChannel;
import com.aimir.constants.CommonConstants.DeviceType;
import com.aimir.constants.CommonConstants.MeterType;
import com.aimir.esapi.AimirAuthenticator;
import com.aimir.esapi.AimirUser;
import com.aimir.model.device.EndDevice;
import com.aimir.model.mvm.BillingDayEM;
import com.aimir.model.mvm.BillingDayGM;
import com.aimir.model.mvm.BillingDayWM;
import com.aimir.model.mvm.BillingMonthEM;
import com.aimir.model.mvm.BillingMonthGM;
import com.aimir.model.mvm.BillingMonthWM;
import com.aimir.model.mvm.DayEM;
import com.aimir.model.mvm.MeteringDay;
import com.aimir.model.mvm.MeteringMonth;
import com.aimir.model.mvm.MonthEM;
import com.aimir.model.system.Contract;
import com.aimir.model.system.Operator;
import com.aimir.model.system.OperatorContract;
import com.aimir.model.system.Supplier;
import com.aimir.service.system.ContractManager;
import com.aimir.service.system.OperatorManager;
import com.aimir.service.system.SupplierManager;
import com.aimir.service.system.energyConsumptionSearch.EnergyConsumptionSearchManager;
import com.aimir.service.system.energySavingGoal.EnergySavingGoalManager;
import com.aimir.service.system.membership.OperatorContractManager;
import com.aimir.util.CalendarUtil;
import com.aimir.util.DecimalUtil;
import com.aimir.util.TimeLocaleUtil;
import com.aimir.util.BillDateUtil;

/**
 * EnergyConsumptionSearchController.java Description
 *
 *
 * Date          Version     Author   Description
 * 2011. 4. 19.   v1.0       김상연         전기 사용량 조회
 * 2011. 5. 17.   v1.1       김상연         기간별 조회 상세 정보 표시 추가
 * 2011. 5. 17.   v1.2       김상연         가스,수도 사용량 조회
 * 2011. 6. 7.    v1.3       은미애         가스,수도 사용 단위 수정 (kwh -> ㎥)
 */

@Controller
public class EnergyConsumptionSearchController {

    @Autowired
    OperatorContractManager operatorContractManager;

    @Autowired
    OperatorManager operatorManager;

    @Autowired
    ContractManager contractManager;

    @Autowired
    SupplierManager supplierManager;

    @Autowired
    EnergyConsumptionSearchManager energyConsumptionSearchManager;

    @Autowired
    EnergySavingGoalManager energySavingGoalManager;

    /**
     * method name : loadConsumptionSearchEmMini
     * method Desc : ConsumptionSearchEmMini 화면 호출
     *
     * @return
     */
    @RequestMapping(value="/gadget/energyConsumptionSearch/consumptionSearchEmMini")
    public ModelAndView loadConsumptionSearchEmMini() {

        ModelAndView mav = new ModelAndView("/gadget/energyConsumptionSearch/consumptionSearchEmMini");

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
        boolean isNotService = false;

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
        	isNotService = true;
        } else {
            mav.addObject("supplierId", contracts.get(0).getSupplier().getId());
        }

        mav.addObject("isNotService",isNotService);
        mav.addObject("contracts", contracts);

        return mav;
    }

    /**
     * method name : loadConsumptionSearchEmMax
     * method Desc : ConsumptionSearchEmMax 화면 호출
     *
     * @return
     */
    @RequestMapping(value="/gadget/energyConsumptionSearch/consumptionSearchEmMax")
    public ModelAndView loadConsumptionSearchEmMax() {

        ModelAndView mav = new ModelAndView("/gadget/energyConsumptionSearch/consumptionSearchEmMax");

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
        boolean isNotService = false;

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
        	isNotService = true;
        } else {
            mav.addObject("supplierId", contracts.get(0).getSupplier().getId());
        }

        mav.addObject("isNotService",isNotService);
        mav.addObject("contracts", contracts);

        return mav;
    }

    /**
     * method name : loadConsumptionSearchEmMini
     * method Desc : ConsumptionSearchEmMini 화면 호출
     *
     * @return
     */
    @RequestMapping(value="/gadget/energyConsumptionSearch/consumptionSearchGmMini")
    public ModelAndView loadConsumptionSearchGmMini() {

        ModelAndView mav = new ModelAndView("/gadget/energyConsumptionSearch/consumptionSearchGmMini");

        // ESAPI.setAuthenticator((Authenticator)new AimirAuthenticator());
        AimirAuthenticator instance = (AimirAuthenticator)ESAPI.authenticator();
        AimirUser user = (AimirUser)instance.getUserFromSession();
        int operatorId = (int) user.getAccountId();
        Operator operator = operatorManager.getOperator(operatorId);
        List<OperatorContract> operatorContracts = operatorContractManager.getOperatorContractByOperator(operator);
        List<Contract> contracts = new ArrayList<Contract>();
        String energyServiceType = MeterType.GasMeter.getServiceType();
        Contract contract;
        String serviceType;
        boolean isNotService = false;

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
        	isNotService = true;
        } else {
            mav.addObject("supplierId", contracts.get(0).getSupplier().getId());
        }

        mav.addObject("isNotService",isNotService);
        mav.addObject("contracts", contracts);

        return mav;
    }

    /**
     * method name : loadConsumptionSearchEmMax
     * method Desc : ConsumptionSearchEmMax 화면 호출
     *
     * @return
     */
    @RequestMapping(value="/gadget/energyConsumptionSearch/consumptionSearchGmMax")
    public ModelAndView loadConsumptionSearchGmMax() {

        ModelAndView mav = new ModelAndView("/gadget/energyConsumptionSearch/consumptionSearchGmMax");

        // ESAPI.setAuthenticator((Authenticator)new AimirAuthenticator());
        AimirAuthenticator instance = (AimirAuthenticator)ESAPI.authenticator();
        AimirUser user = (AimirUser)instance.getUserFromSession();
        int operatorId = (int) user.getAccountId();
        Operator operator = operatorManager.getOperator(operatorId);
        List<OperatorContract> operatorContracts = operatorContractManager.getOperatorContractByOperator(operator);
        List<Contract> contracts = new ArrayList<Contract>();
        String energyServiceType = MeterType.GasMeter.getServiceType();
        Contract contract;
        String serviceType;
        boolean isNotService = false;

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
        	isNotService = true;
        } else {
            mav.addObject("supplierId", contracts.get(0).getSupplier().getId());
        }

        mav.addObject("isNotService",isNotService);
        mav.addObject("contracts", contracts);

        return mav;
    }

    /**
     * method name : loadConsumptionSearchEmMini
     * method Desc : ConsumptionSearchEmMini 화면 호출
     *
     * @return
     */
    @RequestMapping(value="/gadget/energyConsumptionSearch/consumptionSearchWmMini")
    public ModelAndView loadConsumptionSearchWmMini() {

        ModelAndView mav = new ModelAndView("/gadget/energyConsumptionSearch/consumptionSearchWmMini");

        // ESAPI.setAuthenticator((Authenticator)new AimirAuthenticator());
        AimirAuthenticator instance = (AimirAuthenticator)ESAPI.authenticator();
        AimirUser user = (AimirUser)instance.getUserFromSession();
        int operatorId = (int) user.getAccountId();
        Operator operator = operatorManager.getOperator(operatorId);
        List<OperatorContract> operatorContracts = operatorContractManager.getOperatorContractByOperator(operator);
        List<Contract> contracts = new ArrayList<Contract>();
        String energyServiceType = MeterType.WaterMeter.getServiceType();
        Contract contract;
        String serviceType;
        boolean isNotService = false;

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
        	isNotService = true;
        } else {
            mav.addObject("supplierId", contracts.get(0).getSupplier().getId());
        }

        mav.addObject("isNotService",isNotService);
        mav.addObject("contracts", contracts);

        return mav;
    }

    /**
     * method name : loadConsumptionSearchEmMax
     * method Desc : ConsumptionSearchEmMax 화면 호출
     *
     * @return
     */
    @RequestMapping(value="/gadget/energyConsumptionSearch/consumptionSearchWmMax")
    public ModelAndView loadConsumptionSearchWmMax() {

        ModelAndView mav = new ModelAndView("/gadget/energyConsumptionSearch/consumptionSearchWmMax");

        // ESAPI.setAuthenticator((Authenticator)new AimirAuthenticator());
        AimirAuthenticator instance = (AimirAuthenticator)ESAPI.authenticator();
        AimirUser user = (AimirUser)instance.getUserFromSession();
        int operatorId = (int) user.getAccountId();
        Operator operator = operatorManager.getOperator(operatorId);
        List<OperatorContract> operatorContracts = operatorContractManager.getOperatorContractByOperator(operator);
        List<Contract> contracts = new ArrayList<Contract>();
        String energyServiceType = MeterType.WaterMeter.getServiceType();
        Contract contract;
        String serviceType;
        boolean isNotService = false;

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
        	isNotService = true;
        } else {
            mav.addObject("supplierId", contracts.get(0).getSupplier().getId());
        }

        mav.addObject("isNotService",isNotService);
        mav.addObject("contracts", contracts);

        return mav;
    }

    /**
     * method name : getContract
     * method Desc : 계약정보 조회
     *
     * @param contractId
     * @return
     */
    @RequestMapping(value="/gadget/energyConsumptionSearch/getContract")
    public ModelAndView getContract(
            @RequestParam("contractId") int contractId) {

        Contract contract = contractManager.getContract(contractId);

        ModelAndView mav = new ModelAndView("jsonView");

        mav.addObject("contract", contract);
//        mav.addObject("address", contract.getCustomer().getAddress() == null ? contract.getCustomer().getAddress2() : contract.getCustomer().getAddress());

//        mav.addObject("location", contract.getLocation() == null ? "" : contract.getLocation().getName());
//        mav.addObject("tariffType", contract.getTariffIndex() == null ? "" : contract.getTariffIndex().getName());
//        mav.addObject("status", contract.getStatus() == null ? "" : contract.getStatus().getName());
//        mav.addObject("date", TimeLocaleUtil.getLocaleDate(contract.getContractDate(),
//                contract.getSupplier().getLang().getCode_2letter(),
//                contract.getSupplier().getCountry().getCode_2letter()));

        return mav;
    }

    /**
     * method name : getMaxDay
     * method Desc : Data 있는 마지막 일자 조회
     *
     * @param contractId
     * @return
     */
    @RequestMapping(value="/gadget/energyConsumptionSearch/getMaxDay")
    public ModelAndView getMaxDay(
            @RequestParam("contractId") int contractId) {

        Contract contract = contractManager.getContract(contractId);

        Supplier supplier = contract.getSupplier();

        String lang    = supplier.getLang().getCode_2letter();
        String country = supplier.getCountry().getCode_2letter();

        String maxDay = energyConsumptionSearchManager.getMaxDay(contract);
        String maxDate = TimeLocaleUtil.getLocaleDateByMediumFormat(maxDay, lang, country);

        ModelAndView mav = new ModelAndView("jsonView");

        mav.addObject("maxDay", maxDay);
        mav.addObject("maxDate", maxDate);

        return mav;
    }

    /**
     * method name : getYear
     * method Desc : 마지막 일자 해당 관련 연 조회
     *
     * @param contractId
     * @param maxDay
     * @return
     */
    @RequestMapping(value="/gadget/energyConsumptionSearch/getYear")
    public ModelAndView getYear(
            @RequestParam("maxDay") String maxDay) {

        String year = CalendarUtil.getDateUsingFormat(maxDay, Calendar.YEAR, -9).substring(0, 4);
        String currYear = Integer.toString(Integer.parseInt(year)+9);

        ModelAndView mav = new ModelAndView("jsonView");

        mav.addObject("year",year);
        mav.addObject("currYear",currYear);

        return mav;
    }

    /**
     * method name : getDate
     * method Desc : 마지막 일자 해당 관련 일자 조회
     *
     * @param dateVal
     * @param addVal
     * @param supplierId
     * @return
     */
    @RequestMapping(value="/gadget/energyConsumptionSearch/getDate")
    public ModelAndView getDate(
            @RequestParam("searchDate") String dateVal,
            @RequestParam("addVal") String addVal,
            @RequestParam("supplierId") String supplierId,
            @RequestParam("maxDay") String maxDay) {

        Supplier supplier = supplierManager.getSupplier((Integer.parseInt(supplierId)));

        String country = supplier.getCountry().getCode_2letter();
        String lang    = supplier.getLang().getCode_2letter();

        if( dateVal == null || "".equals(dateVal.trim()) ){

            dateVal = CalendarUtil.getCurrentDate();
        }else{

           // dateVal = TimeLocaleUtil.getDBDate(dateVal, 8,  lang,  country);
            dateVal = TimeLocaleUtil.getDBDate(dateVal, 16,  lang,  country);
        }

        if( addVal==null || "".equals(addVal.trim()) ){

            addVal = "0";
        }

        String resultDate = CalendarUtil.getDate(dateVal, Calendar.DAY_OF_MONTH, Integer.parseInt(addVal));

        if( Integer.parseInt(maxDay) < Integer.parseInt(resultDate) ){

            resultDate = maxDay;
        }

        ModelAndView mav = new ModelAndView("jsonView");
        mav.addObject("searchDate", TimeLocaleUtil.getLocaleDateByMediumFormat(resultDate, lang, country));

        return mav;
    }

    /**
     * method name : getYearMonth
     * method Desc : 마지막 일자 해당 관련 월 조회
     *
     * @param year
     * @param month
     * @param addVal
     * @return
     */
    @RequestMapping(value="/gadget/energyConsumptionSearch/getYearMonth")
    public ModelAndView getYearMonth(
            @RequestParam("year") String year,
            @RequestParam("month") String month,
            @RequestParam("addVal") String addVal,
            @RequestParam("maxDay") String maxDay) {

        StringBuffer sb = new StringBuffer();

        sb.append(year);
        sb.append(Integer.parseInt(month) < 10 ? "0" + month : month);
        sb.append("01");

        String resultDate = CalendarUtil.getDate(sb.toString(), Calendar.MONTH, Integer.parseInt(addVal));

        if(Integer.parseInt(maxDay) < Integer.parseInt(resultDate)){

            resultDate = maxDay;
        }

        String resYear = resultDate.substring(0,4);
        String resMonth = resultDate.substring(4,6);

        resMonth = Integer.toString(Integer.parseInt(resMonth));

        ModelAndView mav = new ModelAndView("jsonView");

        mav.addObject("year",resYear);
        mav.addObject("month",resMonth);

        return mav;
    }

    /**
     * method name : getYearAddVal
     * method Desc : 마지막 일자 해당 관련 연도 조회
     *
     * @param year
     * @param addVal
     * @return
     */
    @RequestMapping(value="/gadget/energyConsumptionSearch/getYearAddVal")
    public ModelAndView getYearAddVal(
            @RequestParam("year") String year,
            @RequestParam("addVal") String addVal,
            @RequestParam("maxDay") String maxDay) {

        StringBuffer sb = new StringBuffer();

        sb.append(year);
        sb.append("01");
        sb.append("01");

        String resultDate = CalendarUtil.getDate(sb.toString(), Calendar.YEAR, Integer.parseInt(addVal));

        if(Integer.parseInt(maxDay)<Integer.parseInt(resultDate)){

            resultDate = maxDay;
        }

        String targetYear = resultDate.substring(0,4);
        String currYearRange = CalendarUtil.getDate(maxDay, Calendar.YEAR, -9).substring(0,4);
        String currYear = CalendarUtil.getDate(maxDay, Calendar.YEAR, 0).substring(0,4);

        ModelAndView mav = new ModelAndView("jsonView");

        mav.addObject("targetYear",targetYear);
        mav.addObject("year",currYearRange);
        mav.addObject("currYear",currYear);

        return mav;
    }

    /**
     * method name : getMonth
     * method Desc : 마지막 월 조회
     *
     * @param year
     * @return
     */
    @RequestMapping(value="/gadget/energyConsumptionSearch/getMonth")
    public ModelAndView getMonth(
            @RequestParam("year") String year,
            @RequestParam("maxDay") String maxDay) {

        int monthCount = 12;

        if(maxDay.substring(0, 4).equals(year)){

            monthCount = Integer.parseInt(maxDay.substring(4, 6));
        }

        ModelAndView mav = new ModelAndView("jsonView");

        mav.addObject("monthCount",monthCount);

        return mav;
    }

    /**
     * method name : getSummaryDivData
     * method Desc : 요약 정보 보기
     *
     * @param contractId
     * @return
     */
    @RequestMapping(value="/gadget/energyConsumptionSearch/getSummaryDivData")
    public ModelAndView getSummaryDivData(
            @RequestParam("contractId") int contractId) {

        Contract contract = contractManager.getContract(contractId);

        String serviceType = contract.getServiceTypeCode().getCode();

        String maxDay = energyConsumptionSearchManager.getMaxDay(contract);
        String currentMonth = maxDay.substring(0,6);
        String lastMonth = getLastMonth(currentMonth);

        Double usage = 0.0;
        Double usageFee = 0.0;
        Double co2formula =  0.0;
        Double incentive = 0.0;
//        Double lastMonthUsage =  0.0;
//        Double currentMonthUsage = 0.0;

        int lastMonthUsage =  0;
        int currentMonthUsage = 0;

        if (MeterType.EnergyMeter.getServiceType().equals(serviceType)) {

            BillingDayEM billingDayEM = energyConsumptionSearchManager.getBillingDayEm(contract, maxDay);

            usage = billingDayEM.getActiveEnergyRateTotal() == null ? 0 : billingDayEM.getActiveEnergyRateTotal();
            usageFee = billingDayEM.getBill() == null ? 0 : billingDayEM.getBill();
            co2formula = billingDayEM.getCo2Emissions() == null ? 0 : billingDayEM.getCo2Emissions();
            incentive = billingDayEM.getNewMiles() == null ? 0 : billingDayEM.getNewMiles();
        } else if (MeterType.GasMeter.getServiceType().equals(serviceType)) {

            BillingDayGM billingDayGM = energyConsumptionSearchManager.getBillingDayGm(contract, maxDay);

            usage = billingDayGM.getUsage() == null ? 0 : billingDayGM.getUsage();
            usageFee = billingDayGM.getBill() == null ? 0 : billingDayGM.getBill();
            co2formula = billingDayGM.getCo2Emissions() == null ? 0 : billingDayGM.getCo2Emissions();
            incentive = billingDayGM.getNewMiles() == null ? 0 : billingDayGM.getNewMiles();

            List<Object> datas = energyConsumptionSearchManager.getCompareBill(contractId, currentMonth, lastMonth, null);

            lastMonthUsage = (Integer)datas.get(1);
            currentMonthUsage = (Integer) datas.get(2);
        } else if (MeterType.WaterMeter.getServiceType().equals(serviceType)) {

            BillingDayWM billingDayWM = energyConsumptionSearchManager.getBillingDayWm(contract, maxDay);

            usage = billingDayWM.getUsage() == null ? 0 : billingDayWM.getUsage();
            usageFee = billingDayWM.getBill() == null ? 0 : billingDayWM.getBill();
            co2formula = billingDayWM.getCo2Emissions() == null ? 0 : billingDayWM.getCo2Emissions();
            incentive = billingDayWM.getNewMiles() == null ? 0 : billingDayWM.getNewMiles();

            List<Object> datas = energyConsumptionSearchManager.getCompareBill(contractId, currentMonth, lastMonth, null);

            lastMonthUsage = (Integer) datas.get(1);
            currentMonthUsage = (Integer) datas.get(2);
        }

        DecimalFormat dfMd = DecimalUtil.getDecimalFormat(contract.getSupplier().getMd());
        DecimalFormat dfCd = DecimalUtil.getDecimalFormat(contract.getSupplier().getCd());

        ModelAndView mav = new ModelAndView("jsonView");

        mav.addObject("maxDay", maxDay);
        mav.addObject("usage", dfMd.format(usage));
        mav.addObject("usageFee", dfCd.format(usageFee));
        mav.addObject("co2formula", dfMd.format(co2formula));
        mav.addObject("incentive", dfMd.format(incentive));
        mav.addObject("lastMonthUsage", dfCd.format(lastMonthUsage));
        mav.addObject("currentMonthUsage", dfCd.format(currentMonthUsage));
        mav.addObject("fommatDay", TimeLocaleUtil.getLocaleDateByMediumFormat(maxDay,
                contract.getSupplier().getLang().getCode_2letter(),
                contract.getSupplier().getCountry().getCode_2letter()));

        return mav;
    }

    /**
     * method name : getMonthChart
     * method Desc : 월 사용량 비교
     *
     * @return
     */
    @RequestMapping(value="gadget/energyConsumptionSearch/getMonthChart")
    public ModelAndView getMonthChart(
            HttpServletRequest request,
            HttpServletResponse response) {

        ModelAndView mav = new ModelAndView("jsonView");

        Map<String, Object> chartMap = setChartPart(request, mav);

        String[] color = (String[]) chartMap.get("color");
        String[] label = (String[]) chartMap.get("label");
        int chartType = Integer.parseInt(chartMap.get("chartType").toString());
        int contractId = Integer.parseInt(chartMap.get("contractId").toString());
        String basicDay = chartMap.get("basicDay").toString();
        String trandlineText = chartMap.get("trandlineText").toString();

        Contract contract = contractManager.getContract(contractId);

        //List<Object> datas = energyConsumptionSearchManager.getMonthBill(contract, lastDay, basicDay);
        List<Object> datas = energyConsumptionSearchManager.getMonthBill(contract, basicDay);

        // 월 사용요금 비교 Bar챠트의 라벨이 평균(label[0])일 경우, 지역명 + 평균으로 변경한다.
        label[0] = contract.getLocation().getName() + " " +label[0]; 
        setChartType(chartType, color, label, datas, mav);

        // ESAPI.setAuthenticator((Authenticator)new AimirAuthenticator());
        AimirAuthenticator instance = (AimirAuthenticator)ESAPI.authenticator();
        AimirUser user = (AimirUser)instance.getUserFromSession();
        int operatorId = (int) user.getAccountId();

        Operator operator = new Operator();

        operator.setId(operatorId);

        OperatorContract operatorContract = new OperatorContract();

        operatorContract.setOperator(operator);
        operatorContract.setContract(contract);

        List<OperatorContract> operatorContracts = operatorContractManager.getOperatorContract(operatorContract);

        Double savingTarget = 0.0;

        if (1 == operatorContracts.size()) {

            //savingTarget = energyConsumptionSearchManager.getSavingTarget(operatorContracts.get(0), basicDay);
            //String billDate = BillDateUtil.getLastBillDay(contract, basicDay, 0);
            String billDate = BillDateUtil.getBillDate(contract, basicDay, -1);
            String monthToDate = BillDateUtil.getMonthToDate(contract, basicDay, 1);
            savingTarget = energyConsumptionSearchManager.getSavingTarget(operatorContracts.get(0), billDate, monthToDate);
        }

        Map<String, Object> lineMap = new HashMap<String, Object>();
        List<Map<String, Object>> line = new ArrayList<Map<String,Object>>();
        if (0 < savingTarget) {

            DecimalFormat dfCd = DecimalUtil.getDecimalFormat(contract.getSupplier().getCd());

            lineMap.put("startvalue", savingTarget);
            lineMap.put("color", "ff0000");
           // lineMap.put("displayValue", trandlineText + "{br}(" + dfCd.format(savingTarget) + ")");
            lineMap.put("toolText", trandlineText + "{br}(" + dfCd.format(savingTarget) + ")");
            lineMap.put("displayValue", " ");

        }else {
            lineMap.put("startvalue", "");
            lineMap.put("color", "ff0000");
            lineMap.put("displayValue", "{br}");
        }
        line.add(lineMap);
        Map<String, Object> trendlines = new HashMap<String, Object>();
        trendlines.put("line", line);
        mav.addObject("trendlines", trendlines);

        return mav;
    }

	/*////////////////////////////////////////////////////////////*/
	/*       기간별 조회 탭에서 주기별 에너지 사용정보 비교 챠트  Start        */
	/*////////////////////////////////////////////////////////////*/
    /**
     * method name : getCompareTimeChart
     * method Desc : 기간 - 일 사용 요금 비교 차트
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value="/gadget/energyConsumptionSearch/getCompareTimeChart")
    public ModelAndView getCompareTimeChart(
            HttpServletRequest request,
            HttpServletResponse response) {

        ModelAndView mav = new ModelAndView("jsonView");

        Map<String, Object> chartMap = setChartPart(request, mav);

        String[] color = (String[]) chartMap.get("color");
        String[] label = (String[]) chartMap.get("label");
        int chartType = Integer.parseInt(chartMap.get("chartType").toString());
        int contractId = Integer.parseInt(chartMap.get("contractId").toString());
        String basicDay = chartMap.get("basicDay").toString();
        String yesterDay = getYesterday(basicDay);
        // 전년도 동일
        String lastYearDay = getLastYear(basicDay) + basicDay.substring(4, 8);

        List<Object> datas = energyConsumptionSearchManager.getCompareBill(contractId, basicDay, yesterDay, lastYearDay);

        setChartType(chartType, color, label, datas, mav);

        return mav;
    }

    /**
     * method name : getCompareDayChart
     * method Desc : 기간 - 월별 사용량 비교 차트
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value="/gadget/energyConsumptionSearch/getCompareDayChart")
    public ModelAndView getCompareDayChart(
            HttpServletRequest request,
            HttpServletResponse response) {

        ModelAndView mav = new ModelAndView("jsonView");

        Map<String, Object> chartMap = setChartPart(request, mav);

        String[] color = (String[]) chartMap.get("color");
        String[] label = (String[]) chartMap.get("label");
        int chartType = Integer.parseInt(chartMap.get("chartType").toString());
        int contractId = Integer.parseInt(chartMap.get("contractId").toString());
        String basicDay = chartMap.get("basicDay").toString();
        String lastMonth = getLastMonth(basicDay);

        String lastYearMonth = this.getLastYear(basicDay) + basicDay.substring(4, 6);
        List<Object> datas = energyConsumptionSearchManager.getCompareBill(contractId, basicDay, lastMonth, lastYearMonth);

        setChartType(chartType, color, label, datas, mav);

        return mav;
    }

    /**
     * method name : getCompareMonthChart
     * method Desc : 기간 - 년도별 사용량 비교 차트
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value="/gadget/energyConsumptionSearch/getCompareMonthChart")
    public ModelAndView getCompareMonthChart(
            HttpServletRequest request,
            HttpServletResponse response) {

        ModelAndView mav = new ModelAndView("jsonView");

        Map<String, Object> chartMap = setChartPart(request, mav);

        String[] color = (String[]) chartMap.get("color");
        String[] label = (String[]) chartMap.get("label");
        int chartType = Integer.parseInt(chartMap.get("chartType").toString());
        int contractId = Integer.parseInt(chartMap.get("contractId").toString());
        String basicDay = chartMap.get("basicDay").toString();
        String lastYear = getLastYear(basicDay);

        List<Object> datas = energyConsumptionSearchManager.getCompareBill(contractId, basicDay, lastYear, getLastYear(lastYear));

        setChartType(chartType, color, label, datas, mav);

        return mav;
    }
    // 기간별 조회 탭에서 주기별 에너지 사용정보 비교 챠트  End
    
	/*////////////////////////////////////////////////////////////*/
	/*    기간별 조회 탭에서 주기별 동일지역과 사용정보 비교 챠트  Start       */
	/*////////////////////////////////////////////////////////////*/
    /**
     * method name : getCompareTimeVLEDChart
     * method Desc : 기간별 조회 맥스 - 동일 지역과  일별 사용량 비교 챠트
     *
     * @param contractId
     * @param basicDay
     * @return
     */
    @RequestMapping(value="/gadget/energyConsumptionSearch/getCompareTimeVLEDChart")
    public ModelAndView getCompareTimeVLEDChart(
            @RequestParam("contractId") int contractId,
            @RequestParam("basicDay") String basicDay) {

        Contract contract = contractManager.getContract(contractId);

        String serviceType = contract.getServiceTypeCode().getCode();
        Double currentUsageFee = 0.0;
        Double averageValue = 0.0;

        if (MeterType.EnergyMeter.getServiceType().equals(serviceType)) {

            BillingDayEM currentBillingDayEM = energyConsumptionSearchManager.getBillingDayEm(contract, basicDay);

            
            //currentUsage = currentBillingDayEM.getActiveEnergyRateTotal() == null ? 0 : currentBillingDayEM.getActiveEnergyRateTotal();
            currentUsageFee = currentBillingDayEM.getBill() == null ? 0 : currentBillingDayEM.getBill();
            
            averageValue = energyConsumptionSearchManager.getPeriodDayAvgBillEm(contract, basicDay);
        } else if (MeterType.GasMeter.getServiceType().equals(serviceType)) {

            BillingDayGM currentBillingDayGM = energyConsumptionSearchManager.getBillingDayGm(contract, basicDay);

            //currentUsage = currentBillingDayGM.getUsage() == null ? 0 : currentBillingDayGM.getUsage();
            currentUsageFee = currentBillingDayGM.getBill() == null ? 0 : currentBillingDayGM.getBill();
            
            averageValue = energyConsumptionSearchManager.getPeriodDayAvgBillGm(contract, basicDay);
        } else if ((MeterType.WaterMeter.getServiceType().equals(serviceType))) {

            BillingDayWM currentBillingDayWM = energyConsumptionSearchManager.getBillingDayWm(contract, basicDay);

            //currentUsage = currentBillingDayWM.getUsage() == null ? 0 : currentBillingDayWM.getUsage();
            currentUsageFee = currentBillingDayWM.getBill() == null ? 0 : currentBillingDayWM.getBill();
            
            averageValue = energyConsumptionSearchManager.getPeriodDayAvgBillWm(contract, basicDay);
        }

        ModelAndView mav = new ModelAndView("jsonView");

        //DecimalFormat dfMd = DecimalUtil.getDecimalFormat(contract.getSupplier().getMd());
        DecimalFormat dfCd = DecimalUtil.getDecimalFormat(contract.getSupplier().getCd());

       // mav.addObject("currentUsage", dfMd.format(currentUsage));
        mav.addObject("valueNumber", currentUsageFee);
        mav.addObject("value", dfCd.format(currentUsageFee));
        mav.addObject("compareValue", averageValue);
        mav.addObject("maxValue", (currentUsageFee.compareTo(averageValue) >= 0 ? currentUsageFee : averageValue));
        mav.addObject("locationName", contract.getLocation().getName());
        mav.addObject("compareGap", currentUsageFee - averageValue >= 0 
        		                    ? "+" + dfCd.format(currentUsageFee - averageValue) 
        		                    : dfCd.format(currentUsageFee - averageValue));

        return mav;
    }

    /**
     * method name : getCompareDayVLEDChart
     * method Desc : 기간별 조회 맥스 - 동일 지역과  월별 사용량 비교 챠트
     *
     * @param contractId
     * @param basicDay
     * @return
     */
    @RequestMapping(value="/gadget/energyConsumptionSearch/getCompareDayVLEDChart")
    public ModelAndView getCompareDayVLEDChart(
            @RequestParam("contractId") int contractId,
            @RequestParam("basicDay") String basicDay) {

        Contract contract = contractManager.getContract(contractId);

        String serviceType = contract.getServiceTypeCode().getCode();

        //Double currentUsage = 0.0;
        Double currentUsageFee = 0.0;
        Double averageValue = 0.0;

        if (MeterType.EnergyMeter.getServiceType().equals(serviceType)) {

            BillingMonthEM currentBillingMonthEM = energyConsumptionSearchManager.getBillingMonthEm(contract, basicDay);
            //currentUsage = currentBillingMonthEM.getActiveEnergyRateTotal() == null ? 0 : currentBillingMonthEM.getActiveEnergyRateTotal();
            currentUsageFee = currentBillingMonthEM.getBill() == null ? 0 : currentBillingMonthEM.getBill();

            averageValue = energyConsumptionSearchManager.getPeriodMonthAvgBillEm(contract, basicDay);
        } else if (MeterType.GasMeter.getServiceType().equals(serviceType)) {

            BillingMonthGM currentBillingMonthGM = energyConsumptionSearchManager.getBillingMonthGm(contract, basicDay);
            //currentUsage = currentBillingMonthGM.getUsage() == null ? 0 : currentBillingMonthGM.getUsage();
            currentUsageFee = currentBillingMonthGM.getBill() == null ? 0 : currentBillingMonthGM.getBill();

            averageValue = energyConsumptionSearchManager.getPeriodMonthAvgBillGm(contract, basicDay);
        } else if ((MeterType.WaterMeter.getServiceType().equals(serviceType))) {

            BillingMonthWM currentBillingMonthWM = energyConsumptionSearchManager.getBillingMonthWm(contract, basicDay);
            // = currentBillingMonthWM.getUsage() == null ? 0 : currentBillingMonthWM.getUsage();
            currentUsageFee = currentBillingMonthWM.getBill() == null ? 0 : currentBillingMonthWM.getBill();

            averageValue = energyConsumptionSearchManager.getPeriodMonthAvgBillWm(contract, basicDay);
        }

        ModelAndView mav = new ModelAndView("jsonView");
        //DecimalFormat dfMd = DecimalUtil.getDecimalFormat(contract.getSupplier().getMd());
        DecimalFormat dfCd = DecimalUtil.getDecimalFormat(contract.getSupplier().getCd());
        //mav.addObject("currentUsage", dfMd.format(currentUsage));
        mav.addObject("valueNumber", currentUsageFee);
        mav.addObject("value", dfCd.format(currentUsageFee));
        mav.addObject("compareValue", averageValue);
        mav.addObject("maxValue", (currentUsageFee.compareTo(averageValue) >= 0 ? currentUsageFee : averageValue));
        mav.addObject("locationName", contract.getLocation().getName());
        mav.addObject("compareGap", currentUsageFee - averageValue >= 0 
					                ? "+" + dfCd.format(currentUsageFee - averageValue) 
					                : dfCd.format(currentUsageFee - averageValue));

        return mav;
    }

    /**
     * method name : getCompareMonthVLEDChart
     * method Desc : 기간별 조회 맥스 - 동일 지역과  년도별 사용량 비교 챠트
     *
     * @param contractId
     * @param basicDay
     * @return
     */
    @RequestMapping(value="/gadget/energyConsumptionSearch/getCompareMonthVLEDChart")
    public ModelAndView getCompareMonthVLEDChart(
            @RequestParam("contractId") int contractId,
            @RequestParam("basicDay") String basicDay) {

        ModelAndView mav = new ModelAndView("jsonView");
        Double averageValue = 0d;
        Double currentUsageFee = 0d;
        
        Contract contract = contractManager.getContract(contractId);
        Map<String, Object> currentBillingYear = energyConsumptionSearchManager.getBillingYear(contract, basicDay);
        currentUsageFee = (Double)currentBillingYear.get("bill");
        //DecimalFormat dfMd = DecimalUtil.getDecimalFormat(contract.getSupplier().getMd());
        DecimalFormat dfCd = DecimalUtil.getDecimalFormat(contract.getSupplier().getCd());

        String serviceType = contract.getServiceTypeCode().getCode();

        if (MeterType.EnergyMeter.getServiceType().equals(serviceType)) {

            averageValue = energyConsumptionSearchManager.getPeriodMonthAvgBillEm(contract, basicDay);
        } else if (MeterType.GasMeter.getServiceType().equals(serviceType)) {

            averageValue = energyConsumptionSearchManager.getPeriodMonthAvgBillGm(contract, basicDay);
        } else if ((MeterType.WaterMeter.getServiceType().equals(serviceType))) {

            averageValue = energyConsumptionSearchManager.getPeriodMonthAvgBillWm(contract, basicDay);
        }
        //mav.addObject("currentUsage", dfMd.format(currentBillingYear.get("activeEnergyRateTotal")));
        mav.addObject("valueNumber", currentUsageFee);
        mav.addObject("value", dfCd.format(currentUsageFee));
        mav.addObject("compareValue", averageValue);
        mav.addObject("maxValue", (currentUsageFee.compareTo(averageValue) >= 0 ? currentUsageFee : averageValue));
        mav.addObject("locationName", contract.getLocation().getName());
        mav.addObject("compareGap", currentUsageFee - averageValue >= 0 
					                ? "+" + dfCd.format(currentUsageFee - averageValue) 
					                : dfCd.format(currentUsageFee - averageValue));

        return mav;
    }

	/*////////////////////////////////////////////////////////////*/
	/*          기간별 조회 탭에서 주기별 사용정보 컬럼 챠트  Start          */
	/*////////////////////////////////////////////////////////////*/
    /**
     * method name : getPeriodTimeChart
     * method Desc : 기간 - 시간별 차트
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value="/gadget/energyConsumptionSearch/getPeriodTimeChart")
    public ModelAndView getPeriodTimeChart(
            HttpServletRequest request,
            HttpServletResponse response) {

        ModelAndView mav = new ModelAndView("jsonView");

        Map<String, Object> chartMap = setChartPart(request, mav);

        String[] color = (String[]) chartMap.get("color");
        String[] toolText = (String[]) chartMap.get("toolText");
        //String[] label = (String[]) chartMap.get("label");
        int contractId = Integer.parseInt(chartMap.get("contractId").toString());
        String basicDay = chartMap.get("basicDay").toString();

        Contract contract = contractManager.getContract(contractId);

        DecimalFormat dfMd = DecimalUtil.getDecimalFormat(contract.getSupplier().getMd());

        List<Object> categories = new ArrayList<Object>();
        List<Object> category = new ArrayList<Object>();
        List<Object> dataset = new ArrayList<Object>();
        List<Object> data1 = null;
        List<Object> data2 = null;
        List<Object> data3 = null;

        Map<String, Object> categoryMap;
        Map<String, Object> categoriesMap;
        Map<String, Object> datasetMap;
        Map<String, Object> dataMap;

        Double dataValue[];
        String dataToolText;
        String dataDate;

        Calendar calendar =  Calendar.getInstance();
        Locale locale = new Locale(contract.getSupplier().getLang().getCode_2letter(),
                contract.getSupplier().getCountry().getCode_2letter());
        int hour;
        //String amPm;

        for (int i = 0; i < 24; i++) {

            categoryMap = new HashMap<String, Object>();

            calendar.set(Calendar.HOUR_OF_DAY, i);

            hour = calendar.get(Calendar.HOUR);
           // amPm = new String(calendar.getDisplayName(Calendar.AM_PM, Calendar.SHORT, locale));
            categoryMap.put("label", i);

//            if (hour % 3 == 0) {
//
//                categoryMap.put("label", amPm + " " + (hour == 0 ? 12 : hour));
//            } else {
//
//                categoryMap.put("label", "");
//            }

            category.add(categoryMap);
        }

        String mdevType = DeviceType.Meter.name();

        // 지정한 일자의 시간당 평균 사용량(대상 : 동일 지역)
        Double usageAvgPerTime = energyConsumptionSearchManager.getPeriodTimeAvgUsage(contract, basicDay, 1, mdevType) / 24;
    	data1 = this.getPeriodTimeChartData(energyConsumptionSearchManager.getPeriodTimeUsage(contract, getYesterday(basicDay), 1, mdevType), chartMap);
    	data2 = this.getPeriodTimeChartData(energyConsumptionSearchManager.getPeriodTimeUsage(contract, basicDay, 1, mdevType), chartMap);
//    	data3 = this.getPeriodTimeChartDataAvg(energyConsumptionSearchManager.getPeriodTimeAvgUsage(contract, basicDay, 1, mdevType), chartMap);

//        for (MeteringDay meteringMonth : datas) {
//
//            dataValue = getDayValue24(meteringMonth);
//
//            for (int i = 0; i < dataValue.length; i++) {
//
//                dataMap = new HashMap<String, Object>();
//                dataToolText = "";
//                dataDate = "";
//
//                dataDate = TimeLocaleUtil.getLocaleDate(meteringMonth.getId().getYyyymmdd(),
//                        contract.getSupplier().getLang().getCode_2letter(),
//                        contract.getSupplier().getCountry().getCode_2letter());
//
//                calendar.set(Calendar.HOUR_OF_DAY, i);
//
//                hour = calendar.get(Calendar.HOUR);
//                amPm = new String(calendar.getDisplayName(Calendar.AM_PM, Calendar.SHORT, locale));
//
//                dataToolText = toolText[0] + " : " + dataDate + " " + amPm + " " + (hour == 0 ? 12 : hour)+ "{br}"
//                             + toolText[1] + " : " + dfMd.format(dataValue[i]) + " "
//                             + toolText[2] + "{br}";
//
//                dataMap.put("value", dataValue[i]);
//
//                if (meteringMonth.getId().getYyyymmdd().equals(basicDay)) {
//
//                    dataMap.put("color", color[0]);
//                } else {
//
//                    dataMap.put("color", color[1]);
//                }
//
//                dataMap.put("toolText", dataToolText);
//
//                data.add(dataMap);
//            }
//        }

//        category.addAll(category);

        categoriesMap = new HashMap<String, Object>();
        categoriesMap.put("category", category);   
        categories.add(categoriesMap);
//
//        datasetMap = new HashMap<String, Object>();
//        datasetMap.put("data", data);
//        dataset.add(datasetMap);
//
//        datasetMap = new HashMap<String, Object>();
//        datasetMap.put("renderas", "Line");
//        datasetMap.put("linethickness", "3");
//        datasetMap.put("data", data);
//        dataset.add(datasetMap);

        // 전일 컬럼 그래프
        datasetMap = new HashMap<String, Object>();
        datasetMap.put("seriesname", TimeLocaleUtil.getLocaleDateByMediumFormat(getYesterday(basicDay),
                                                                  contract.getSupplier().getLang().getCode_2letter(),
                                                                  contract.getSupplier().getCountry().getCode_2letter()));
        datasetMap.put("color", color[1]);
        datasetMap.put("data", data1);
        dataset.add(datasetMap);

        // 당일 컬럼 
        datasetMap = new HashMap<String, Object>();
        datasetMap.put("seriesname", TimeLocaleUtil.getLocaleDateByMediumFormat(basicDay,
                                                                  contract.getSupplier().getLang().getCode_2letter(),
                                                                  contract.getSupplier().getCountry().getCode_2letter()));
        datasetMap.put("color", color[0]);
        datasetMap.put("data", data2);
        dataset.add(datasetMap);

        Map<String, Object> lineMap = new HashMap<String, Object>();
        List<Map<String, Object>> line = new ArrayList<Map<String,Object>>();

        // 일자 : yyyy. mm. dd(로케일에 따라서 포멧 변경)
        // 지역명 평균사용량 : 사용량 kWh
        dataToolText = toolText[0] + " : " + TimeLocaleUtil.getLocaleDateByMediumFormat(basicDay,
							                contract.getSupplier().getLang().getCode_2letter(),
							                contract.getSupplier().getCountry().getCode_2letter())
					   + "{br}" 
					   + contract.getLocation().getName() + " " + toolText[3] + " : " + dfMd.format(usageAvgPerTime)  + toolText[2];
        lineMap.put("startvalue",dfMd.format(usageAvgPerTime));
        lineMap.put("color", color[2]);
        lineMap.put("thickness", "2");
        lineMap.put("showOnTop", "1");
        lineMap.put("tooltext", dataToolText);
        //lineMap.put("displayValue", contract.getLocation().getName() + "<BR> 시간평균");
        line.add(lineMap);
        Map<String, Object> trendlines = new HashMap<String, Object>();
        trendlines.put("line", line);

        mav.addObject("trendlines", trendlines);

        // 지역 평균 라인그래프
//	    datasetMap = new HashMap<String, Object>();
//	    datasetMap.put("seriesname", contract.getLocation().getName() + " 평균");
//	    datasetMap.put("renderas", "Line");
//	    datasetMap.put("linethickness", "1");
//	    datasetMap.put("color", color[2]);
//	    datasetMap.put("data", data3);
//	    dataset.add(datasetMap);

        mav.addObject("categories", categories);
        mav.addObject("dataset", dataset);

        return mav;
    }

    /**
     * method name : getPeriodDayChart
     * method Desc : 기간 - 일별 차트
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value="/gadget/energyConsumptionSearch/getPeriodDayChart")
    public ModelAndView getPeriodDayChart(
            HttpServletRequest request,
            HttpServletResponse response) {

        ModelAndView mav = new ModelAndView("jsonView");

        Map<String, Object> chartMap = setChartPart(request, mav);

        String[] color = (String[]) chartMap.get("color");
        String[] toolText = (String[]) chartMap.get("toolText");
        int contractId = Integer.parseInt(chartMap.get("contractId").toString());
        String basicDay = chartMap.get("basicDay").toString();
        String moneyText = chartMap.get("moneyText").toString();

        Contract contract = contractManager.getContract(contractId);

        String serviceType = contract.getServiceTypeCode().getCode();

        DecimalFormat dfMd = DecimalUtil.getDecimalFormat(contract.getSupplier().getMd());
        DecimalFormat dfCd = DecimalUtil.getDecimalFormat(contract.getSupplier().getCd());

        List<Object> categories = new ArrayList<Object>();
        List<Object> category = new ArrayList<Object>();
        List<Object> dataset = new ArrayList<Object>();
        Map<String, List<Object>> result1 = null;
        Map<String, List<Object>> result2 = null;
        List<Object> result3 = null;

        Map<String, Object> categoryMap;
        Map<String, Object> categoriesMap;
        Map<String, Object> datasetMap;
        Map<String, Object> dataMap;

        String dataLabel;
        Double dataValue;
        Double dataBill;
        String dataToolText;
        String dataDate;
        Double billAverage = 0d;

        if (MeterType.EnergyMeter.getServiceType().equals(serviceType)) {
    		result1 = this.getPeriodDayChartEMData(energyConsumptionSearchManager.getPeriodDayUsageEm(contract, getLastMonth(basicDay)), chartMap);
    		result2 = this.getPeriodDayChartEMData(energyConsumptionSearchManager.getPeriodDayUsageEm(contract, basicDay), chartMap);
    		billAverage = energyConsumptionSearchManager.getPeriodDayAvgBillEm(contract, basicDay);
//    		result3 = this.getPeriodDayChartDataAvg(energyConsumptionSearchManager.getPeriodDayAvgUsageEm(contract, basicDay), chartMap);

        } else if (MeterType.GasMeter.getServiceType().equals(serviceType)) {
    		result1 = this.getPeriodDayChartGMData(energyConsumptionSearchManager.getPeriodDayUsageGm(contract, getLastMonth(basicDay)), chartMap);
    		result2 = this.getPeriodDayChartGMData(energyConsumptionSearchManager.getPeriodDayUsageGm(contract, basicDay), chartMap);
    		billAverage = energyConsumptionSearchManager.getPeriodDayAvgBillGm(contract, basicDay);
//    		result3 = this.getPeriodDayChartDataAvg(energyConsumptionSearchManager.getPeriodDayAvgUsageGm(contract, basicDay), chartMap);

        } else if (MeterType.WaterMeter.getServiceType().equals(serviceType)) {
    		result1 = this.getPeriodDayChartWMData(energyConsumptionSearchManager.getPeriodDayUsageWm(contract, getLastMonth(basicDay)), chartMap);
    		result2 = this.getPeriodDayChartWMData(energyConsumptionSearchManager.getPeriodDayUsageWm(contract, basicDay), chartMap);
//    		result3 = this.getPeriodDayChartDataAvg(energyConsumptionSearchManager.getPeriodDayAvgUsageWm(contract, basicDay), chartMap);
    		billAverage = energyConsumptionSearchManager.getPeriodDayAvgBillWm(contract, basicDay);

        }

        categoriesMap = new HashMap<String, Object>();
        if(result1.get("category").size() > result2.get("category").size()) {
            categoriesMap.put("category", result1.get("category"));
        }else {
            categoriesMap.put("category", result2.get("category"));
        }
        categories.add(categoriesMap);
        
        datasetMap = new HashMap<String, Object>(); 
        datasetMap.put("seriesname", TimeLocaleUtil.getLocaleDateByMediumFormat(getLastMonth(basicDay), 
        		                                                       contract.getSupplier().getLang().getCode_2letter(),
        		                                                       contract.getSupplier().getCountry().getCode_2letter()));
        datasetMap.put("color", color[1]);
        datasetMap.put("data", result1.get("data"));       
        dataset.add(datasetMap);
        
        datasetMap = new HashMap<String, Object>(); 
        datasetMap.put("seriesname", TimeLocaleUtil.getLocaleDateByMediumFormat(basicDay, 
														               contract.getSupplier().getLang().getCode_2letter(),
														               contract.getSupplier().getCountry().getCode_2letter()));
        datasetMap.put("color", color[0]);
        datasetMap.put("data", result2.get("data"));       
        dataset.add(datasetMap);

        Map<String, Object> lineMap = new HashMap<String, Object>();
        List<Map<String, Object>> line = new ArrayList<Map<String,Object>>();

        // 일자 : yyyy. mm. dd(로케일에 따라서 포멧 변경)
        // 지역명 평균 요금 : 요금 (원)
        dataToolText = toolText[0] + " : " + TimeLocaleUtil.getLocaleDateByMediumFormat(basicDay, 
								               contract.getSupplier().getLang().getCode_2letter(),
								               contract.getSupplier().getCountry().getCode_2letter())
					   + "{br}" 
					   + contract.getLocation().getName() + " " + toolText[5] + " : " + dfCd.format(billAverage)  + moneyText;
        lineMap.put("startvalue", billAverage.intValue());
        lineMap.put("color", color[2]);
        lineMap.put("thickness", "2");
        lineMap.put("showOnTop", "1");
        lineMap.put("tooltext", dataToolText);
        line.add(lineMap);
        Map<String, Object> trendlines = new HashMap<String, Object>();
        trendlines.put("line", line);

        mav.addObject("trendlines", trendlines);

        // 지역 평균값 라인그래프
//        datasetMap = new HashMap<String, Object>();
//        datasetMap.put("seriesname", "Average ");
//        datasetMap.put("renderas", "Line");
//        datasetMap.put("linethickness", "1");
//        datasetMap.put("color", color[2]);
//        datasetMap.put("data", result3);
//        dataset.add(datasetMap);

        mav.addObject("categories", categories);
        mav.addObject("dataset", dataset);

        return mav;
    }

    /**
     * method name : getPeriodMonthChart
     * method Desc : 기간 - 월별 차트
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value="/gadget/energyConsumptionSearch/getPeriodMonthChart")
    public ModelAndView getPeriodMonthChart(
            HttpServletRequest request,
            HttpServletResponse response) {

        ModelAndView mav = new ModelAndView("jsonView");

        Map<String, Object> chartMap = setChartPart(request, mav);

        String[] color = (String[]) chartMap.get("color");
        String[] toolText = (String[]) chartMap.get("toolText");
        int contractId = Integer.parseInt(chartMap.get("contractId").toString());
        String basicDay = chartMap.get("basicDay").toString();
        String moneyText = chartMap.get("moneyText").toString();

        Contract contract = contractManager.getContract(contractId);

        String serviceType = contract.getServiceTypeCode().getCode();

        DecimalFormat dfMd = DecimalUtil.getDecimalFormat(contract.getSupplier().getMd());
        DecimalFormat dfCd = DecimalUtil.getDecimalFormat(contract.getSupplier().getCd());

        List<Object> categories = new ArrayList<Object>();
        List<Object> category = new ArrayList<Object>();
        List<Object> dataset = new ArrayList<Object>();
        Map<String, List<Object>> result1 = null;
        Map<String, List<Object>> result2 = null;
        List<Object> result3 = null;

        Map<String, Object> categoryMap;
        Map<String, Object> categoriesMap;
        Map<String, Object> datasetMap;
        Map<String, Object> dataMap;

        String dataLabel;
        Double dataValue;
        Double dataBill;
        String dataToolText;
        String dataDate;
        Double billAverage = 0d;

        if (MeterType.EnergyMeter.getServiceType().equals(serviceType)) {

    		result1 = this.getPeriodMonthChartEMData(energyConsumptionSearchManager.getPeriodMonthUsageEm(contract, getLastYear(basicDay)),chartMap);
    		result2 = this.getPeriodMonthChartEMData(energyConsumptionSearchManager.getPeriodMonthUsageEm(contract, basicDay), chartMap);
    		billAverage = energyConsumptionSearchManager.getPeriodMonthAvgBillEm(contract, basicDay);
//    		result3 = this.getPeriodDayChartDataAvg(energyConsumptionSearchManager.getPeriodMonthAvgUsageEm(contract, basicDay), chartMap);

        } else if (MeterType.GasMeter.getServiceType().equals(serviceType)) {

    		result1 = this.getPeriodMonthChartGMData(energyConsumptionSearchManager.getPeriodMonthUsageGm(contract, getLastYear(basicDay)),chartMap);
    		result2 = this.getPeriodMonthChartGMData(energyConsumptionSearchManager.getPeriodMonthUsageGm(contract, basicDay), chartMap);
    		billAverage = energyConsumptionSearchManager.getPeriodMonthAvgBillGm(contract, basicDay);
//    		result3 = this.getPeriodDayChartDataAvg(energyConsumptionSearchManager.getPeriodMonthAvgUsageGm(contract, basicDay), chartMap);
//

        } else if (MeterType.WaterMeter.getServiceType().equals(serviceType)) {

    		result1 = this.getPeriodMonthChartWMData(energyConsumptionSearchManager.getPeriodMonthUsageWm(contract, getLastYear(basicDay)),chartMap);
    		result2 = this.getPeriodMonthChartWMData(energyConsumptionSearchManager.getPeriodMonthUsageWm(contract, basicDay), chartMap);
    		billAverage = energyConsumptionSearchManager.getPeriodMonthAvgBillWm(contract, basicDay);
//    		result3 = this.getPeriodDayChartDataAvg(energyConsumptionSearchManager.getPeriodMonthAvgUsageWm(contract, basicDay), chartMap);
        }

        categoriesMap = new HashMap<String, Object>();
        if(result1.get("category").size() > result2.get("category").size()) {
            categoriesMap.put("category", result1.get("category"));
        }else {
            categoriesMap.put("category", result2.get("category"));
        }
        categories.add(categoriesMap);

        datasetMap = new HashMap<String, Object>();
        datasetMap.put("seriesname", getLastYear(basicDay));
        datasetMap.put("color", color[1]);
        datasetMap.put("data", result1.get("data"));
        dataset.add(datasetMap);
        
        datasetMap = new HashMap<String, Object>();
        datasetMap.put("seriesname", basicDay);
        datasetMap.put("color", color[0]);        
        datasetMap.put("data", result2.get("data"));
        dataset.add(datasetMap);

        Map<String, Object> lineMap = new HashMap<String, Object>();
        List<Map<String, Object>> line = new ArrayList<Map<String,Object>>();
        // 일자 : yyyy. mm. dd(로케일에 따라서 포멧 변경)
        // 지역명 평균 요금 : 요금 (원)
        dataToolText = toolText[0] + " : " + basicDay
					   + "{br}" 
					   + contract.getLocation().getName() + " " + toolText[5] + " : " + dfCd.format(billAverage)  + moneyText;
        lineMap.put("startvalue", billAverage.intValue());
        lineMap.put("color", color[2]);
        lineMap.put("thickness", "2");
        lineMap.put("showOnTop", "1");
        lineMap.put("tooltext", dataToolText);
        line.add(lineMap);
        Map<String, Object> trendlines = new HashMap<String, Object>();
        trendlines.put("line", line);

        mav.addObject("trendlines", trendlines);

        // 지역 평균값 라인그래프
//        datasetMap = new HashMap<String, Object>();
//        datasetMap.put("seriesname", "Average ");
//        datasetMap.put("renderas", "Line");
//        datasetMap.put("linethickness", "1");
//        datasetMap.put("color", color[2]);
//        datasetMap.put("data", result3);
//        dataset.add(datasetMap);

        mav.addObject("categories", categories);
        mav.addObject("dataset", dataset);

        return mav;
    }
    // 기간별 조회 탭에서 주기별 사용정보 컬럼 챠트  End 
 
	/**
	 * method name : getPeriodTimeChartData
	 * method Desc :
	 *
	 * @param datas
	 * @param chartMap
	 * @return
	 */
	@SuppressWarnings("unused")
	private  List<Object> getPeriodTimeChartData(List<MeteringDay> datas,Map<String, Object> chartMap) {

		String[] color = (String[]) chartMap.get("color");
		String[] toolText = (String[]) chartMap.get("toolText");
		int contractId = Integer.parseInt(chartMap.get("contractId").toString());
		String basicDay = chartMap.get("basicDay").toString();
    	Contract contract = contractManager.getContract(contractId);
    	DecimalFormat dfMd = DecimalUtil.getDecimalFormat(contract.getSupplier().getMd());

        List<Object> data = new ArrayList<Object>();

        Map<String, Object> dataMap;
        
        Double dataValue[];
        String dataToolText;
        String dataDate;
        
    	Calendar calendar =  Calendar.getInstance();
    	Locale locale = new Locale(contract.getSupplier().getLang().getCode_2letter(),
        		contract.getSupplier().getCountry().getCode_2letter());
    	int hour;
    	String amPm;

		for (MeteringDay meteringMonth : datas) {

        	dataValue = getDayValue24(meteringMonth);

        	for (int i = 0; i < dataValue.length; i++) {

             	dataMap = new HashMap<String, Object>();
            	dataToolText = "";
            	dataDate = "";

	        	dataDate = TimeLocaleUtil.getLocaleDateByMediumFormat(meteringMonth.getId().getYyyymmdd(),
	            		contract.getSupplier().getLang().getCode_2letter(),
	            		contract.getSupplier().getCountry().getCode_2letter());
	        	
	    		calendar.set(Calendar.HOUR_OF_DAY, i);
	    		
	    		hour = calendar.get(Calendar.HOUR);
	    		amPm = new String(calendar.getDisplayName(Calendar.AM_PM, Calendar.SHORT, locale));
	    		
	        	dataToolText = toolText[0] + " : " + dataDate + " " + amPm + " " + (hour == 0 ? 12 : hour)+ "{br}"
	        	             + toolText[1] + " : " + dfMd.format(dataValue[i]) + " "
	        	             + toolText[2] + "{br}";
	
	        	dataMap.put("value", dataValue[i]);
	        	
	        	if (meteringMonth.getId().getYyyymmdd().equals(basicDay)) {
	        		
	        		dataMap.put("color", color[0]);
	        	} else {
	        		dataMap.put("color", color[1]);
	        	}
	        	
	        	dataMap.put("toolText", dataToolText);
	            
	            data.add(dataMap);
        	}
		}
		return data;
    }

	/**
	 * method name : getPeriodTimeChartDataAvg
	 * method Desc :
	 *
	 * @param datas
	 * @param chartMap
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private  List<Object> getPeriodTimeChartDataAvg(List<Object> datas,Map<String, Object> chartMap ) {

		String[] toolText = (String[]) chartMap.get("toolText");
		int contractId = Integer.parseInt(chartMap.get("contractId").toString());
		String basicDay = chartMap.get("basicDay").toString();
    	Contract contract = contractManager.getContract(contractId);
    	DecimalFormat dfMd = DecimalUtil.getDecimalFormat(contract.getSupplier().getMd());

        List<Object> data = new ArrayList<Object>();

        Map<String, Object> dataMap;

        String dataToolText;
        String dataDate;
        
    	Calendar calendar =  Calendar.getInstance();
    	Locale locale = new Locale(contract.getSupplier().getLang().getCode_2letter(),
        		contract.getSupplier().getCountry().getCode_2letter());
    	int hour;
    	String amPm;
    	Iterator it = datas.iterator(); 
    	while(it.hasNext()) {
    		Object[] row = (Object[])it.next();
    		for(int i=0; i< row.length; i++) {

                 	dataMap = new HashMap<String, Object>();
                	dataToolText = "";
                	dataDate = "";
    
    	        	dataDate = TimeLocaleUtil.getLocaleDateByMediumFormat(basicDay,
    	            		contract.getSupplier().getLang().getCode_2letter(),
    	            		contract.getSupplier().getCountry().getCode_2letter());
    	        	
    	    		calendar.set(Calendar.HOUR_OF_DAY, i);
    	    		
    	    		hour = calendar.get(Calendar.HOUR);
    	    		amPm = new String(calendar.getDisplayName(Calendar.AM_PM, Calendar.SHORT, locale));
    	    		
    	        	dataToolText = toolText[0] + " : " + dataDate + " " + amPm + " " + (hour == 0 ? 12 : hour)+ "{br}"
    	        	             + toolText[1] + " : " + dfMd.format(row[i] == null ? 0.0 : row[i]) + " "
    	        	             + toolText[2] + "{br}";
    	
    	        	dataMap.put("value", row[i] == null ? 0.0 : row[i]);
    	        	
//    	        	if (meteringMonth.getId().getYyyymmdd().equals(basicDay)) {
//    	        		
//    	        		dataMap.put("color", color[0]);
//    	        	} else {
//    	        		
//    	        		dataMap.put("color", color[1]);
//    	        	}
    	        	
    	        	dataMap.put("toolText", dataToolText);
    	            
    	            data.add(dataMap);   			
    		}
    	}
 
		return data;
    }

	/**
	 * method name : getPeriodDayChartDataAvg
	 * method Desc :
	 *
	 * @param datas
	 * @param chartMap
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "unused" })
	private  List<Object> getPeriodDayChartDataAvg(List<Object> datas,Map<String, Object> chartMap ) {

		String[] toolText = (String[]) chartMap.get("toolText");
		int contractId = Integer.parseInt(chartMap.get("contractId").toString());
		String basicDay = chartMap.get("basicDay").toString();
		String moneyText = chartMap.get("moneyText").toString();

    	Contract contract = contractManager.getContract(contractId);
    	DecimalFormat dfMd = DecimalUtil.getDecimalFormat(contract.getSupplier().getMd());
    	DecimalFormat dfCd = DecimalUtil.getDecimalFormat(contract.getSupplier().getCd());
        List<Object> data = new ArrayList<Object>();

        Map<String, Object> dataMap;

        String dataToolText;
        String dataDate;
        
    	Calendar calendar =  Calendar.getInstance();
    	Locale locale = new Locale(contract.getSupplier().getLang().getCode_2letter(),
        		contract.getSupplier().getCountry().getCode_2letter());

    	Double dataBill; 


    	for(Object obj : datas) {
    		Map<String, Object> map = (Map)obj;

        	dataMap = new HashMap<String, Object>();
        	dataToolText = "";
        	dataDate = "";
        	dataBill = new Double(map.get("BILL") == null ? 0 : (Double)map.get("BILL"));
        	
        	dataDate = TimeLocaleUtil.getLocaleDateByMediumFormat((String)map.get("YYYYMMDD"),
            		contract.getSupplier().getLang().getCode_2letter(),
            		contract.getSupplier().getCountry().getCode_2letter());
            /* 2011. 5. 17 v1.1 HEMS 회원정보관리를 위한 항목 추가 UPDATE START eunmiae */        	
        	dataToolText = toolText[0] + " : " + dataDate + "{br}"
        	             + toolText[1] + " : " + dfCd.format(dataBill) + " " + moneyText + "{br}";
            /* 2011. 5. 17 v1.1 HEMS 회원정보관리를 위한 항목 추가 UPDATE START eunmiae */

        	dataMap.put("value", dataBill);

//            	if (billingDayEM.getId().getYyyymmdd().substring(0, 6).equals(basicDay)) {
//            		
//            		dataMap.put("color", color[0]);
//            	} else {
//            		
//            		dataMap.put("color", color[1]);
//            	}

        	dataMap.put("toolText", dataToolText);

            data.add(dataMap);
    	}

		return data;
    }

    /**
     * method name : getDayValue24
     * method Desc :
     *
     * @param meteringMonth
     * @return
     */
    private Double[] getDayValue24(MeteringDay meteringMonth) {

        Double[] dayValues = new Double[24];

        dayValues[0] = (meteringMonth.getValue_00() == null ? 0 : meteringMonth.getValue_00());
        dayValues[1] = (meteringMonth.getValue_01() == null ? 0 : meteringMonth.getValue_01());
        dayValues[2] = (meteringMonth.getValue_02() == null ? 0 : meteringMonth.getValue_02());
        dayValues[3] = (meteringMonth.getValue_03() == null ? 0 : meteringMonth.getValue_03());
        dayValues[4] = (meteringMonth.getValue_04() == null ? 0 : meteringMonth.getValue_04());
        dayValues[5] = (meteringMonth.getValue_05() == null ? 0 : meteringMonth.getValue_05());
        dayValues[6] = (meteringMonth.getValue_06() == null ? 0 : meteringMonth.getValue_06());
        dayValues[7] = (meteringMonth.getValue_07() == null ? 0 : meteringMonth.getValue_07());
        dayValues[8] = (meteringMonth.getValue_08() == null ? 0 : meteringMonth.getValue_08());
        dayValues[9] = (meteringMonth.getValue_09() == null ? 0 : meteringMonth.getValue_09());
        dayValues[10] = (meteringMonth.getValue_10() == null ? 0 : meteringMonth.getValue_10());
        dayValues[11] = (meteringMonth.getValue_11() == null ? 0 : meteringMonth.getValue_11());
        dayValues[12] = (meteringMonth.getValue_12() == null ? 0 : meteringMonth.getValue_12());
        dayValues[13] = (meteringMonth.getValue_13() == null ? 0 : meteringMonth.getValue_13());
        dayValues[14] = (meteringMonth.getValue_14() == null ? 0 : meteringMonth.getValue_14());
        dayValues[15] = (meteringMonth.getValue_15() == null ? 0 : meteringMonth.getValue_15());
        dayValues[16] = (meteringMonth.getValue_16() == null ? 0 : meteringMonth.getValue_16());
        dayValues[17] = (meteringMonth.getValue_17() == null ? 0 : meteringMonth.getValue_17());
        dayValues[18] = (meteringMonth.getValue_18() == null ? 0 : meteringMonth.getValue_18());
        dayValues[19] = (meteringMonth.getValue_19() == null ? 0 : meteringMonth.getValue_19());
        dayValues[20] = (meteringMonth.getValue_20() == null ? 0 : meteringMonth.getValue_20());
        dayValues[21] = (meteringMonth.getValue_21() == null ? 0 : meteringMonth.getValue_21());
        dayValues[22] = (meteringMonth.getValue_22() == null ? 0 : meteringMonth.getValue_22());
        dayValues[23] = (meteringMonth.getValue_23() == null ? 0 : meteringMonth.getValue_23());

        return dayValues;
    }





	/**
	 * method name : 
	 * method Desc :
	 *
	 * @param datas
	 * @param chartMap
	 * @return
	 */
	private Map<String, List<Object>> getPeriodDayChartEMData(List<BillingDayEM> datas, Map<String, Object> chartMap) {
		
		String[] color = (String[]) chartMap.get("color");
		String[] toolText = (String[]) chartMap.get("toolText");
		int contractId = Integer.parseInt(chartMap.get("contractId").toString());
		String basicDay = chartMap.get("basicDay").toString();
		String moneyText = chartMap.get("moneyText").toString();
		
    	Contract contract = contractManager.getContract(contractId);
    	
    	DecimalFormat dfMd = DecimalUtil.getDecimalFormat(contract.getSupplier().getMd());
    	DecimalFormat dfCd = DecimalUtil.getDecimalFormat(contract.getSupplier().getCd());

        List<Object> category = new ArrayList<Object>();
        List<Object> data = new ArrayList<Object>();
        
        Map<String, Object> categoryMap;
        Map<String, Object> dataMap;
        
        String dataLabel;
        Double dataValue;
        Double dataBill;
        String dataToolText;
        String dataDate;
        
        Map<String, List<Object>> result = new HashMap<String, List<Object>>();

		for (BillingDayEM billingDayEM : datas) {
        	categoryMap = new HashMap<String, Object>();
        	dataLabel = "";
        	
        	dataLabel = billingDayEM.getId().getYyyymmdd().substring(6, 8);

//        	categoryMap.put("label", dataLabel);
        	// 01 -> 1, 02 -> 2, ,,,,,, 10 -> 10, ,,,,,, 31 -> 31
        	categoryMap.put("label", dataLabel.startsWith("0") == true ? dataLabel.substring(1) : dataLabel);

            category.add(categoryMap);

        	dataMap = new HashMap<String, Object>();
        	dataToolText = "";
        	dataDate = "";
        	dataBill = new Double(billingDayEM.getBill() == null ? 0 : billingDayEM.getBill());
        	dataValue = new Double(billingDayEM.getActiveEnergyRateTotal() == null ? 0 : billingDayEM.getActiveEnergyRateTotal());
        	
        	dataDate = TimeLocaleUtil.getLocaleDateByMediumFormat(billingDayEM.getId().getYyyymmdd(),
            		contract.getSupplier().getLang().getCode_2letter(),
            		contract.getSupplier().getCountry().getCode_2letter());
            /* 2011. 5. 17 v1.1 HEMS 회원정보관리를 위한 항목 추가 UPDATE START eunmiae */        	
        	dataToolText = toolText[0] + " : " + dataDate + "{br}"
        	             + toolText[1] + " : " + dfMd.format(dataValue) + " "
        	             + toolText[2] + "{br}"
        	             + toolText[3] + " : " + dfCd.format(dataBill) + " " + moneyText + "{br}"
                         + toolText[4];
            /* 2011. 5. 17 v1.1 HEMS 회원정보관리를 위한 항목 추가 UPDATE START eunmiae */
 
        	dataMap.put("value", dataBill);
            /* 2011. 5. 17 v1.1 HEMS 회원정보관리를 위한 항목 추가 ADD START eunmiae */
        	dataMap.put("link", "j-showDetailInfo('" + dataDate + "', '" + CommonConstants.DateType.HOURLY.getCode() + "')");
            /* 2011. 5. 17 v1.1 HEMS 회원정보관리를 위한 항목 추가 ADD END eunmiae */

        	if (billingDayEM.getId().getYyyymmdd().substring(0, 6).equals(basicDay)) {
        		
        		dataMap.put("color", color[0]);
        	} else {
        		
        		dataMap.put("color", color[1]);
        	}
        	
        	dataMap.put("toolText", dataToolText);
            
            data.add(dataMap);
		}

		result.put("category", category);
		result.put("data", data);
    	return result;
	}
	
	/**
	 * method name : 
	 * method Desc :
	 *
	 * @param datas
	 * @param chartMap
	 * @return
	 */
	private Map<String, List<Object>> getPeriodDayChartGMData(List<BillingDayGM> datas, Map<String, Object> chartMap) {
		
		String[] color = (String[]) chartMap.get("color");
		String[] toolText = (String[]) chartMap.get("toolText");
		int contractId = Integer.parseInt(chartMap.get("contractId").toString());
		String basicDay = chartMap.get("basicDay").toString();
		String moneyText = chartMap.get("moneyText").toString();
		
    	Contract contract = contractManager.getContract(contractId);

    	DecimalFormat dfMd = DecimalUtil.getDecimalFormat(contract.getSupplier().getMd());
    	DecimalFormat dfCd = DecimalUtil.getDecimalFormat(contract.getSupplier().getCd());

        List<Object> category = new ArrayList<Object>();
        List<Object> data = new ArrayList<Object>();
        
        Map<String, Object> categoryMap;
        Map<String, Object> dataMap;
        
        String dataLabel;
        Double dataValue;
        Double dataBill;
        String dataToolText;
        String dataDate;

        Map<String, List<Object>> result = new HashMap<String, List<Object>>();
		for (BillingDayGM billingDayGM : datas) {
        	categoryMap = new HashMap<String, Object>();
        	dataLabel = "";
        	
        	dataLabel = billingDayGM.getYyyymmdd().substring(6, 8);
        		
//        	categoryMap.put("label", dataLabel);
        	// 01 -> 1, 02 -> 2, ,,,,,, 10 -> 10, ,,,,,, 31 -> 31
        	categoryMap.put("label", dataLabel.startsWith("0") == true ? dataLabel.substring(1) : dataLabel);
            
            category.add(categoryMap);            
        	
        	dataMap = new HashMap<String, Object>();
        	dataToolText = "";
        	dataDate = "";
        	dataBill = new Double(billingDayGM.getBill() == null ? 0 : billingDayGM.getBill());
        	dataValue = new Double(billingDayGM.getUsage() == null ? 0 : billingDayGM.getUsage());
        	
        	dataDate = TimeLocaleUtil.getLocaleDateByMediumFormat(billingDayGM.getYyyymmdd(),
            		contract.getSupplier().getLang().getCode_2letter(),
            		contract.getSupplier().getCountry().getCode_2letter());
            /* 2011. 5. 17 v1.1 HEMS 회원정보관리를 위한 항목 추가 UPDATE START eunmiae */        	
        	dataToolText = toolText[0] + " : " + dataDate + "{br}"
        	             + toolText[1] + " : " + dfMd.format(dataValue) + " "
        	             + toolText[2] + "{br}"
        	             + toolText[3] + " : " + dfCd.format(dataBill) + " " + moneyText + "{br}"
                         + toolText[4];
            /* 2011. 5. 17 v1.1 HEMS 회원정보관리를 위한 항목 추가 UPDATE START eunmiae */
 
        	dataMap.put("value", dataBill);
            /* 2011. 5. 17 v1.1 HEMS 회원정보관리를 위한 항목 추가 ADD START eunmiae */
        	dataMap.put("link", "j-showDetailInfo('" + dataDate + "', '" + CommonConstants.DateType.HOURLY.getCode() + "')");
            /* 2011. 5. 17 v1.1 HEMS 회원정보관리를 위한 항목 추가 ADD END eunmiae */

        	if (billingDayGM.getYyyymmdd().substring(0, 6).equals(basicDay)) {
        		
        		dataMap.put("color", color[0]);
        	} else {
        		
        		dataMap.put("color", color[1]);
        	}
        	
        	dataMap.put("toolText", dataToolText);
            
            data.add(dataMap);
		}
		result.put("category", category);
		result.put("data", data);
    	return result;
	}
	
	/**
	 * method name : 
	 * method Desc :
	 *
	 * @param datas
	 * @param chartMap
	 * @return
	 */
	private Map<String, List<Object>> getPeriodDayChartWMData(List<BillingDayWM> datas, Map<String, Object> chartMap) {
		
		String[] color = (String[]) chartMap.get("color");
		String[] toolText = (String[]) chartMap.get("toolText");
		int contractId = Integer.parseInt(chartMap.get("contractId").toString());
		String basicDay = chartMap.get("basicDay").toString();
		String moneyText = chartMap.get("moneyText").toString();
		
    	Contract contract = contractManager.getContract(contractId);
    	
    	DecimalFormat dfMd = DecimalUtil.getDecimalFormat(contract.getSupplier().getMd());
    	DecimalFormat dfCd = DecimalUtil.getDecimalFormat(contract.getSupplier().getCd());

        List<Object> category = new ArrayList<Object>();
        List<Object> data = new ArrayList<Object>();
        
        Map<String, Object> categoryMap;
        Map<String, Object> dataMap;
        
        String dataLabel;
        Double dataValue;
        Double dataBill;
        String dataToolText;
        String dataDate;

        Map<String, List<Object>> result = new HashMap<String, List<Object>>();
		for (BillingDayWM billingDayWM : datas) {

        	categoryMap = new HashMap<String, Object>();
        	dataLabel = "";
        	
        	dataLabel = billingDayWM.getYyyymmdd().substring(6, 8);
        		
//        	categoryMap.put("label", dataLabel);
        	// 01 -> 1, 02 -> 2, ,,,,,, 10 -> 10, ,,,,,, 31 -> 31
        	categoryMap.put("label", dataLabel.startsWith("0") == true ? dataLabel.substring(1) : dataLabel);       	
            
            category.add(categoryMap);            
        	
        	dataMap = new HashMap<String, Object>();
        	dataToolText = "";
        	dataDate = "";
        	dataBill = new Double(billingDayWM.getBill() == null ? 0 : billingDayWM.getBill());
        	dataValue = new Double(billingDayWM.getUsage() == null ? 0 : billingDayWM.getUsage());
        	
        	dataDate = TimeLocaleUtil.getLocaleDateByMediumFormat(billingDayWM.getYyyymmdd(),
            		contract.getSupplier().getLang().getCode_2letter(),
            		contract.getSupplier().getCountry().getCode_2letter());
            /* 2011. 5. 17 v1.1 HEMS 회원정보관리를 위한 항목 추가 UPDATE START eunmiae */        	
        	dataToolText = toolText[0] + " : " + dataDate + "{br}"
        	             + toolText[1] + " : " + dfMd.format(dataValue) + " "
        	             + toolText[2] + "{br}"
        	             + toolText[3] + " : " + dfCd.format(dataBill) + " " + moneyText + "{br}"
                         + toolText[4];
            /* 2011. 5. 17 v1.1 HEMS 회원정보관리를 위한 항목 추가 UPDATE START eunmiae */
 
        	dataMap.put("value", dataBill);
            /* 2011. 5. 17 v1.1 HEMS 회원정보관리를 위한 항목 추가 ADD START eunmiae */
        	dataMap.put("link", "j-showDetailInfo('" + dataDate + "', '" + CommonConstants.DateType.HOURLY.getCode() + "')");
            /* 2011. 5. 17 v1.1 HEMS 회원정보관리를 위한 항목 추가 ADD END eunmiae */

        	if (billingDayWM.getYyyymmdd().substring(0, 6).equals(basicDay)) {
        		
        		dataMap.put("color", color[0]);
        	} else {
        		
        		dataMap.put("color", color[1]);
        	}
        	
        	dataMap.put("toolText", dataToolText);
            
            data.add(dataMap);
		}

		result.put("category", category);
		result.put("data", data);		
    	return result;
	}

	/*////////////////////////////////////////////////////////////*/
	/*       기간별 조회 탭에서 에너지 사용정보 텍스트 값 취득  Start         */
	/*////////////////////////////////////////////////////////////*/
    /**
     * method name : getPeriodTimeText
     * method Desc : 기간 - 시간별 텍스트
     *
     * @param contractId
     * @param basicDay
     * @return
     */
    @RequestMapping(value="/gadget/energyConsumptionSearch/getPeriodTimeText")
    public ModelAndView getPeriodTimeText(
            @RequestParam("contractId") int contractId,
            @RequestParam("basicDay") String basicDay) {

        Contract contract = contractManager.getContract(contractId);

        String serviceType = contract.getServiceTypeCode().getCode();

        Double lastUsage = 0.0;
        Double lastUsageFee = 0.0;
        Double lastCo2formula = 0.0;
        Double lastIncentive = 0.0;

        Double currentUsage = 0.0;
        Double currentUsageFee = 0.0;
        Double currentCo2formula = 0.0;
        Double currentIncentive = 0.0;

        if (MeterType.EnergyMeter.getServiceType().equals(serviceType)) {

            BillingDayEM lastBillingDayEM = energyConsumptionSearchManager.getBillingDayEm(contract, getYesterday(basicDay));
            BillingDayEM currentBillingDayEM = energyConsumptionSearchManager.getBillingDayEm(contract, basicDay);

            lastUsage = lastBillingDayEM.getActiveEnergyRateTotal() == null ? 0 : lastBillingDayEM.getActiveEnergyRateTotal();
            lastUsageFee = lastBillingDayEM.getBill() == null ? 0 : lastBillingDayEM.getBill();
            lastCo2formula = lastBillingDayEM.getCo2Emissions() == null ? 0 : lastBillingDayEM.getCo2Emissions();
            lastIncentive = lastBillingDayEM.getNewMiles() == null ? 0 : lastBillingDayEM.getNewMiles();

            currentUsage = currentBillingDayEM.getActiveEnergyRateTotal() == null ? 0 : currentBillingDayEM.getActiveEnergyRateTotal();
            currentUsageFee = currentBillingDayEM.getBill() == null ? 0 : currentBillingDayEM.getBill();
            currentCo2formula = currentBillingDayEM.getCo2Emissions() == null ? 0 : currentBillingDayEM.getCo2Emissions();
            currentIncentive = currentBillingDayEM.getNewMiles() == null ? 0 : currentBillingDayEM.getNewMiles();
        } else if (MeterType.GasMeter.getServiceType().equals(serviceType)) {

            BillingDayGM lastBillingDayGM = energyConsumptionSearchManager.getBillingDayGm(contract, getYesterday(basicDay));
            BillingDayGM currentBillingDayGM = energyConsumptionSearchManager.getBillingDayGm(contract, basicDay);

            lastUsage = lastBillingDayGM.getUsage() == null ? 0 : lastBillingDayGM.getUsage();
            lastUsageFee = lastBillingDayGM.getBill() == null ? 0 : lastBillingDayGM.getBill();
            lastCo2formula = lastBillingDayGM.getCo2Emissions() == null ? 0 : lastBillingDayGM.getCo2Emissions();
            lastIncentive = lastBillingDayGM.getNewMiles() == null ? 0 : lastBillingDayGM.getNewMiles();

            currentUsage = currentBillingDayGM.getUsage() == null ? 0 : currentBillingDayGM.getUsage();
            currentUsageFee = currentBillingDayGM.getBill() == null ? 0 : currentBillingDayGM.getBill();
            currentCo2formula = currentBillingDayGM.getCo2Emissions() == null ? 0 : currentBillingDayGM.getCo2Emissions();
            currentIncentive = currentBillingDayGM.getNewMiles() == null ? 0 : currentBillingDayGM.getNewMiles();
        } else if ((MeterType.WaterMeter.getServiceType().equals(serviceType))) {

            BillingDayWM lastBillingDayWM = energyConsumptionSearchManager.getBillingDayWm(contract, getYesterday(basicDay));
            BillingDayWM currentBillingDayWM = energyConsumptionSearchManager.getBillingDayWm(contract, basicDay);

            lastUsage = lastBillingDayWM.getUsage() == null ? 0 : lastBillingDayWM.getUsage();
            lastUsageFee = lastBillingDayWM.getBill() == null ? 0 : lastBillingDayWM.getBill();
            lastCo2formula = lastBillingDayWM.getCo2Emissions() == null ? 0 : lastBillingDayWM.getCo2Emissions();
            lastIncentive = lastBillingDayWM.getNewMiles() == null ? 0 : lastBillingDayWM.getNewMiles();

            currentUsage = currentBillingDayWM.getUsage() == null ? 0 : currentBillingDayWM.getUsage();
            currentUsageFee = currentBillingDayWM.getBill() == null ? 0 : currentBillingDayWM.getBill();
            currentCo2formula = currentBillingDayWM.getCo2Emissions() == null ? 0 : currentBillingDayWM.getCo2Emissions();
            currentIncentive = currentBillingDayWM.getNewMiles() == null ? 0 : currentBillingDayWM.getNewMiles();
        }

        ModelAndView mav = new ModelAndView("jsonView");

        DecimalFormat dfMd = DecimalUtil.getDecimalFormat(contract.getSupplier().getMd());
        DecimalFormat dfCd = DecimalUtil.getDecimalFormat(contract.getSupplier().getCd());

        mav.addObject("lastItem", TimeLocaleUtil.getLocaleDateByMediumFormat(getYesterday(basicDay),
                contract.getSupplier().getLang().getCode_2letter(),
                contract.getSupplier().getCountry().getCode_2letter()));
        mav.addObject("lastUsage", dfMd.format(lastUsage));
        mav.addObject("lastFee", dfCd.format(lastUsageFee));
        mav.addObject("lastCo2", dfMd.format(lastCo2formula));
        mav.addObject("lastIncentive", dfMd.format(lastIncentive));
        mav.addObject("currentItem", TimeLocaleUtil.getLocaleDateByMediumFormat(basicDay,
                contract.getSupplier().getLang().getCode_2letter(),
                contract.getSupplier().getCountry().getCode_2letter()));
        mav.addObject("currentUsage", dfMd.format(currentUsage));
        mav.addObject("currentFee", dfCd.format(currentUsageFee));
        mav.addObject("currentCo2", dfMd.format(currentCo2formula));
        mav.addObject("currentIncentive", dfMd.format(currentIncentive));

        // 전년도 동일 설정 ( 일별 사용요금 비교 챠트의 라벨 설정을 위해 )
        String lastYearDay = getLastYear(basicDay) + basicDay.substring(4, 8);
        mav.addObject("lastYearItem", TimeLocaleUtil.getLocaleDateByMediumFormat(lastYearDay,
                                          contract.getSupplier().getLang().getCode_2letter(),
                                      contract.getSupplier().getCountry().getCode_2letter()));
        return mav;
    }

    /**
     * method name : getPeriodDayText
     * method Desc : 기간 - 일별 텍스트
     *
     * @param contractId
     * @param basicDay
     * @return
     */
    @RequestMapping(value="/gadget/energyConsumptionSearch/getPeriodDayText")
    public ModelAndView getPeriodDayText(
            @RequestParam("contractId") int contractId,
            @RequestParam("basicDay") String basicDay) {

        Contract contract = contractManager.getContract(contractId);

        String serviceType = contract.getServiceTypeCode().getCode();

        Double lastUsage = 0.0;
        Double lastUsageFee = 0.0;
        Double lastCo2formula = 0.0;
        Double lastIncentive = 0.0;

        Double currentUsage = 0.0;
        Double currentUsageFee = 0.0;
        Double currentCo2formula = 0.0;
        Double currentIncentive = 0.0;

        if (MeterType.EnergyMeter.getServiceType().equals(serviceType)) {

            BillingMonthEM lastBillingMonthEM = energyConsumptionSearchManager.getBillingMonthEm(contract, getLastMonth(basicDay));
            BillingMonthEM currentBillingMonthEM = energyConsumptionSearchManager.getBillingMonthEm(contract, basicDay);

            lastUsage = lastBillingMonthEM.getActiveEnergyRateTotal() == null ? 0 : lastBillingMonthEM.getActiveEnergyRateTotal();
            lastUsageFee = lastBillingMonthEM.getBill() == null ? 0 : lastBillingMonthEM.getBill();
            lastCo2formula = lastBillingMonthEM.getCo2Emissions() == null ? 0 : lastBillingMonthEM.getCo2Emissions();
            lastIncentive = lastBillingMonthEM.getNewMiles() == null ? 0 : lastBillingMonthEM.getNewMiles();

            currentUsage = currentBillingMonthEM.getActiveEnergyRateTotal() == null ? 0 : currentBillingMonthEM.getActiveEnergyRateTotal();
            currentUsageFee = currentBillingMonthEM.getBill() == null ? 0 : currentBillingMonthEM.getBill();
            currentCo2formula = currentBillingMonthEM.getCo2Emissions() == null ? 0 : currentBillingMonthEM.getCo2Emissions();
            currentIncentive = currentBillingMonthEM.getNewMiles() == null ? 0 : currentBillingMonthEM.getNewMiles();
        } else if (MeterType.GasMeter.getServiceType().equals(serviceType)) {

            BillingMonthGM lastBillingMonthGM = energyConsumptionSearchManager.getBillingMonthGm(contract, getLastMonth(basicDay));
            BillingMonthGM currentBillingMonthGM = energyConsumptionSearchManager.getBillingMonthGm(contract, basicDay);

            lastUsage = lastBillingMonthGM.getUsage() == null ? 0 : lastBillingMonthGM.getUsage();
            lastUsageFee = lastBillingMonthGM.getBill() == null ? 0 : lastBillingMonthGM.getBill();
            lastCo2formula = lastBillingMonthGM.getCo2Emissions() == null ? 0 : lastBillingMonthGM.getCo2Emissions();
            lastIncentive = lastBillingMonthGM.getNewMiles() == null ? 0 : lastBillingMonthGM.getNewMiles();

            currentUsage = currentBillingMonthGM.getUsage() == null ? 0 : currentBillingMonthGM.getUsage();
            currentUsageFee = currentBillingMonthGM.getBill() == null ? 0 : currentBillingMonthGM.getBill();
            currentCo2formula = currentBillingMonthGM.getCo2Emissions() == null ? 0 : currentBillingMonthGM.getCo2Emissions();
            currentIncentive = currentBillingMonthGM.getNewMiles() == null ? 0 : currentBillingMonthGM.getNewMiles();
        } else if ((MeterType.WaterMeter.getServiceType().equals(serviceType))) {

            BillingMonthWM lastBillingMonthWM = energyConsumptionSearchManager.getBillingMonthWm(contract, getLastMonth(basicDay));
            BillingMonthWM currentBillingMonthWM = energyConsumptionSearchManager.getBillingMonthWm(contract, basicDay);

            lastUsage = lastBillingMonthWM.getUsage() == null ? 0 : lastBillingMonthWM.getUsage();
            lastUsageFee = lastBillingMonthWM.getBill() == null ? 0 : lastBillingMonthWM.getBill();
            lastCo2formula = lastBillingMonthWM.getCo2Emissions() == null ? 0 : lastBillingMonthWM.getCo2Emissions();
            lastIncentive = lastBillingMonthWM.getNewMiles() == null ? 0 : lastBillingMonthWM.getNewMiles();

            currentUsage = currentBillingMonthWM.getUsage() == null ? 0 : currentBillingMonthWM.getUsage();
            currentUsageFee = currentBillingMonthWM.getBill() == null ? 0 : currentBillingMonthWM.getBill();
            currentCo2formula = currentBillingMonthWM.getCo2Emissions() == null ? 0 : currentBillingMonthWM.getCo2Emissions();
            currentIncentive = currentBillingMonthWM.getNewMiles() == null ? 0 : currentBillingMonthWM.getNewMiles();
        }

        ModelAndView mav = new ModelAndView("jsonView");

        DecimalFormat dfMd = DecimalUtil.getDecimalFormat(contract.getSupplier().getMd());
        DecimalFormat dfCd = DecimalUtil.getDecimalFormat(contract.getSupplier().getCd());

        mav.addObject("lastItem", TimeLocaleUtil.getLocaleDateByMediumFormat(getLastMonth(basicDay),
                contract.getSupplier().getLang().getCode_2letter(),
                contract.getSupplier().getCountry().getCode_2letter()));
        mav.addObject("lastUsage", dfMd.format(lastUsage));
        mav.addObject("lastFee", dfCd.format(lastUsageFee));
        mav.addObject("lastCo2", dfMd.format(lastCo2formula));
        mav.addObject("lastIncentive", dfMd.format(lastIncentive));
        mav.addObject("currentItem", TimeLocaleUtil.getLocaleDateByMediumFormat(basicDay,
                contract.getSupplier().getLang().getCode_2letter(),
                contract.getSupplier().getCountry().getCode_2letter()));
        mav.addObject("currentUsage", dfMd.format(currentUsage));
        mav.addObject("currentFee", dfCd.format(currentUsageFee));
        mav.addObject("currentCo2", dfMd.format(currentCo2formula));
        mav.addObject("currentIncentive", dfMd.format(currentIncentive));

        // 전년도 동월 설정 ( 월별 사용요금 비교 챠트의 라벨 설정을 위해 )
        String lastYearMonth = this.getLastYear(basicDay) + basicDay.substring(4, 6);
        mav.addObject("lastYearItem",  TimeLocaleUtil.getLocaleDateByMediumFormat(lastYearMonth,
                                             contract.getSupplier().getLang().getCode_2letter(),
                                        contract.getSupplier().getCountry().getCode_2letter()));

        return mav;
    }

    /**
     * method name : getPeriodMonthText
     * method Desc : 기간 - 월별 텍스트
     *
     * @param contractId
     * @param basicDay
     * @return
     */
    @RequestMapping(value="/gadget/energyConsumptionSearch/getPeriodMonthText")
    public ModelAndView getPeriodMonthText(
            @RequestParam("contractId") int contractId,
            @RequestParam("basicDay") String basicDay) {

        Contract contract = contractManager.getContract(contractId);

        Map<String, Object> lastBillingYear = energyConsumptionSearchManager.getBillingYear(contract, getLastYear(basicDay));
        Map<String, Object> currentBillingYear = energyConsumptionSearchManager.getBillingYear(contract, basicDay);

        ModelAndView mav = new ModelAndView("jsonView");

        DecimalFormat dfMd = DecimalUtil.getDecimalFormat(contract.getSupplier().getMd());
        DecimalFormat dfCd = DecimalUtil.getDecimalFormat(contract.getSupplier().getCd());

        mav.addObject("lastItem", getLastYear(basicDay));
        mav.addObject("lastUsage", dfMd.format(lastBillingYear.get("activeEnergyRateTotal")));
        mav.addObject("lastFee", dfCd.format(lastBillingYear.get("bill")));
        mav.addObject("lastCo2", dfMd.format(lastBillingYear.get("co2Emissions")));
        mav.addObject("lastIncentive", dfMd.format(lastBillingYear.get("newMiles")));
        mav.addObject("currentItem", basicDay);
        mav.addObject("currentUsage", dfMd.format(currentBillingYear.get("activeEnergyRateTotal")));
        mav.addObject("currentFee", dfCd.format(currentBillingYear.get("bill")));
        mav.addObject("currentCo2", dfMd.format(currentBillingYear.get("co2Emissions")));
        mav.addObject("currentIncentive", dfMd.format(currentBillingYear.get("newMiles")));

        // 전년도 동일 설정 ( 일별 사용요금 비교 챠트의 라벨 설정을 위해 )
        String lastYear = getLastYear(getLastYear(basicDay));
        mav.addObject("lastYearItem", lastYear);

        return mav;
    } 
    // 기간별 조회 탭에서 에너지 사용정보 텍스트 값 취득  End



	/**
	 * method name : getPeriodMonthChartEMData
	 * method Desc : 기간별 조회 탭 - 주기가 월별일 경우 전기 사용량 그래프 표시 데이터 취득
	 *
	 * @param datas
	 * @param chartMap
	 * @return
	 */
	public Map<String, List<Object>> getPeriodMonthChartEMData(List<BillingMonthEM> datas, Map<String, Object> chartMap) {
		
		String[] color = (String[]) chartMap.get("color");
		String[] toolText = (String[]) chartMap.get("toolText");
		int contractId = Integer.parseInt(chartMap.get("contractId").toString());
		String basicDay = chartMap.get("basicDay").toString();
		String moneyText = chartMap.get("moneyText").toString();

    	Contract contract = contractManager.getContract(contractId);
		
    	DecimalFormat dfMd = DecimalUtil.getDecimalFormat(contract.getSupplier().getMd());
    	DecimalFormat dfCd = DecimalUtil.getDecimalFormat(contract.getSupplier().getCd());
    	
        List<Object> category = new ArrayList<Object>();
        List<Object> data = new ArrayList<Object>();
        
        Map<String, Object> categoryMap;
        Map<String, Object> dataMap;
        
        String dataLabel;
        Double dataValue;
        Double dataBill;
        String dataToolText;
        String dataDate;

        Map<String, List<Object>> result = new HashMap<String, List<Object>>();

		for (BillingMonthEM billingMonthEM : datas) {

        	categoryMap = new HashMap<String, Object>();
        	dataLabel = "";

        	dataLabel = billingMonthEM.getId().getYyyymmdd().substring(4, 6);
	
        	//categoryMap.put("label", dataLabel);
        	// 01 -> 1, 02 -> 2, ,,,,,, 10 -> 10, ,,,,,, 12 -> 12
        	categoryMap.put("label", dataLabel.startsWith("0") == true ? dataLabel.substring(1) : dataLabel);

            category.add(categoryMap);            

        	dataMap = new HashMap<String, Object>();
        	dataToolText = "";
        	dataDate = "";
        	dataBill = new Double(billingMonthEM.getBill() == null ? 0 : billingMonthEM.getBill());
        	dataValue = new Double(billingMonthEM.getActiveEnergyRateTotal() == null ? 0 : billingMonthEM.getActiveEnergyRateTotal());
        	
        	dataDate = TimeLocaleUtil.getLocaleDateByMediumFormat(billingMonthEM.getId().getYyyymmdd(),
            		contract.getSupplier().getLang().getCode_2letter(),
            		contract.getSupplier().getCountry().getCode_2letter());

            /* 2011. 5. 17 v1.1 HEMS 회원정보관리를 위한 항목 추가 UPDATE START eunmiae */         	
        	dataToolText = toolText[0] + " : " + dataDate + "{br}"
        	             + toolText[1] + " : " + dfMd.format(dataValue) + " "
        	             + toolText[2] + "{br}"
        	             + toolText[3] + " : " + dfCd.format(dataBill) + " " + moneyText + "{br}"
        	             + toolText[4];
            /* 2011. 5. 17 v1.1 HEMS 회원정보관리를 위한 항목 추가 UPDATE END eunmiae */

        	dataMap.put("value", dataBill);

            /* 2011. 5. 17 v1.1 HEMS 회원정보관리를 위한 항목 추가 ADD START eunmiae */
        	dataMap.put("link", "j-showDetailInfo('" + billingMonthEM.getId().getYyyymmdd() + "', '"+ CommonConstants.DateType.DAILY.getCode() + "')");
            /* 2011. 5. 17 v1.1 HEMS 회원정보관리를 위한 항목 추가 ADD END eunmiae */

        	if (billingMonthEM.getId().getYyyymmdd().substring(0, 4).equals(basicDay)) {
        		
        		dataMap.put("color", color[0]);
        	} else {
        		
        		dataMap.put("color", color[1]);
        	}
        	
        	dataMap.put("toolText", dataToolText);
            
            data.add(dataMap);
		}

		result.put("category", category);
		result.put("data", data);
		return result;
	}
	
	/**
	 * method name : 
	 * method Desc :
	 *
	 * @param datas
	 * @param chartMap
	 * @return
	 */
	public Map<String, List<Object>> getPeriodMonthChartGMData(List<BillingMonthGM> datas, Map<String, Object> chartMap) {
		
		String[] color = (String[]) chartMap.get("color");
		String[] toolText = (String[]) chartMap.get("toolText");
		int contractId = Integer.parseInt(chartMap.get("contractId").toString());
		String basicDay = chartMap.get("basicDay").toString();
		String moneyText = chartMap.get("moneyText").toString();
		
    	Contract contract = contractManager.getContract(contractId);
		
    	DecimalFormat dfMd = DecimalUtil.getDecimalFormat(contract.getSupplier().getMd());
    	DecimalFormat dfCd = DecimalUtil.getDecimalFormat(contract.getSupplier().getCd());

        List<Object> category = new ArrayList<Object>();
        List<Object> data = new ArrayList<Object>();
        Map<String, Object> categoryMap;
        Map<String, Object> dataMap;
        
        String dataLabel;
        Double dataValue;
        Double dataBill;
        String dataToolText;
        String dataDate;
        
        Map<String, List<Object>> result = new HashMap<String, List<Object>>();
		for (BillingMonthGM billingMonthGM : datas) {
			
        	categoryMap = new HashMap<String, Object>();
        	dataLabel = "";
        	
        	dataLabel = billingMonthGM.getYyyymmdd().substring(4, 6);
        		
//        	categoryMap.put("label", dataLabel);
        	// 01 -> 1, 02 -> 2, ,,,,,, 10 -> 10, ,,,,,, 12 -> 12
        	categoryMap.put("label", dataLabel.startsWith("0") == true ? dataLabel.substring(1) : dataLabel);        	
            
            category.add(categoryMap);            
        	
        	dataMap = new HashMap<String, Object>();
        	dataToolText = "";
        	dataDate = "";
        	dataBill = new Double(billingMonthGM.getBill() == null ? 0 : billingMonthGM.getBill());
        	dataValue = new Double(billingMonthGM.getUsage() == null ? 0 : billingMonthGM.getUsage());
        	
        	dataDate = TimeLocaleUtil.getLocaleDateByMediumFormat(billingMonthGM.getYyyymmdd(),
            		contract.getSupplier().getLang().getCode_2letter(),
            		contract.getSupplier().getCountry().getCode_2letter());

            /* 2011. 5. 17 v1.1 HGMS 회원정보관리를 위한 항목 추가 UPDATG START eunmiae */         	
        	dataToolText = toolText[0] + " : " + dataDate + "{br}"
        	             + toolText[1] + " : " + dfMd.format(dataValue) + " "
        	             + toolText[2] + "{br}"
        	             + toolText[3] + " : " + dfCd.format(dataBill) + " " + moneyText + "{br}"
        	             + toolText[4];
            /* 2011. 5. 17 v1.1 HGMS 회원정보관리를 위한 항목 추가 UPDATG GND eunmiae */

        	dataMap.put("value", dataBill);

            /* 2011. 5. 17 v1.1 HGMS 회원정보관리를 위한 항목 추가 ADD START eunmiae */
        	dataMap.put("link", "j-showDetailInfo('" + billingMonthGM.getYyyymmdd() + "', '"+ CommonConstants.DateType.DAILY.getCode() + "')");
            /* 2011. 5. 17 v1.1 HGMS 회원정보관리를 위한 항목 추가 ADD GND eunmiae */

        	if (billingMonthGM.getYyyymmdd().substring(0, 4).equals(basicDay)) {
        		
        		dataMap.put("color", color[0]);
        	} else {
        		
        		dataMap.put("color", color[1]);
        	}
        	
        	dataMap.put("toolText", dataToolText);
            
            data.add(dataMap);
		}
    	
		result.put("category", category);
		result.put("data", data);
		return result;
	}
	
	/**
	 * method name : 
	 * method Desc :
	 *
	 * @param datas
	 * @param chartMap
	 * @return
	 */
	public Map<String, List<Object>> getPeriodMonthChartWMData(List<BillingMonthWM> datas, Map<String, Object> chartMap) {
		
		String[] color = (String[]) chartMap.get("color");
		String[] toolText = (String[]) chartMap.get("toolText");
		int contractId = Integer.parseInt(chartMap.get("contractId").toString());
		String basicDay = chartMap.get("basicDay").toString();
		String moneyText = chartMap.get("moneyText").toString();
		
    	Contract contract = contractManager.getContract(contractId);
		
    	DecimalFormat dfMd = DecimalUtil.getDecimalFormat(contract.getSupplier().getMd());
    	DecimalFormat dfCd = DecimalUtil.getDecimalFormat(contract.getSupplier().getCd());
    	
        List<Object> category = new ArrayList<Object>();
        List<Object> data = new ArrayList<Object>();
        
        Map<String, Object> categoryMap;
        Map<String, Object> dataMap;
        
        String dataLabel;
        Double dataValue;
        Double dataBill;
        String dataToolText;
        String dataDate;
		
        Map<String, List<Object>> result = new HashMap<String, List<Object>>();
		for (BillingMonthWM billingMonthWM : datas) {
			
        	categoryMap = new HashMap<String, Object>();
        	dataLabel = "";
        	
        	dataLabel = billingMonthWM.getYyyymmdd().substring(4, 6);
        		
//        	categoryMap.put("label", dataLabel);
        	// 01 -> 1, 02 -> 2, ,,,,,, 10 -> 10, ,,,,,, 12 -> 12
        	categoryMap.put("label", dataLabel.startsWith("0") == true ? dataLabel.substring(1) : dataLabel);
            
            category.add(categoryMap);            
        	
        	dataMap = new HashMap<String, Object>();
        	dataToolText = "";
        	dataDate = "";
        	dataBill = new Double(billingMonthWM.getBill() == null ? 0 : billingMonthWM.getBill());
        	dataValue = new Double(billingMonthWM.getUsage() == null ? 0 : billingMonthWM.getUsage());
        	
        	dataDate = TimeLocaleUtil.getLocaleDateByMediumFormat(billingMonthWM.getYyyymmdd(),
            		contract.getSupplier().getLang().getCode_2letter(),
            		contract.getSupplier().getCountry().getCode_2letter());

            /* 2011. 5. 17 v1.1 HWMS 회원정보관리를 위한 항목 추가 UPDATW START eunmiae */         	
        	dataToolText = toolText[0] + " : " + dataDate + "{br}"
        	             + toolText[1] + " : " + dfMd.format(dataValue) + " "
        	             + toolText[2] + "{br}"
        	             + toolText[3] + " : " + dfCd.format(dataBill) + " " + moneyText + "{br}"
        	             + toolText[4];
            /* 2011. 5. 17 v1.1 HWMS 회원정보관리를 위한 항목 추가 UPDATW WND eunmiae */

        	dataMap.put("value", dataBill);

            /* 2011. 5. 17 v1.1 HWMS 회원정보관리를 위한 항목 추가 ADD START eunmiae */
        	dataMap.put("link", "j-showDetailInfo('" + billingMonthWM.getYyyymmdd() + "', '"+ CommonConstants.DateType.DAILY.getCode() + "')");
            /* 2011. 5. 17 v1.1 HWMS 회원정보관리를 위한 항목 추가 ADD WND eunmiae */

        	if (billingMonthWM.getYyyymmdd().substring(0, 4).equals(basicDay)) {
        		
        		dataMap.put("color", color[0]);
        	} else {
        		
        		dataMap.put("color", color[1]);
        	}
        	
        	dataMap.put("toolText", dataToolText);
            
            data.add(dataMap);
		}
    	
		result.put("category", category);
		result.put("data", data);
		return result;
	}
	

    /**
     * method name : getDeviceSpecificTimeChart1
     * method Desc : 기기 - 시간별 차트1
     *
     * @param request
     * @param response
     * @return
     */
    @SuppressWarnings("unchecked")
    @RequestMapping(value="/gadget/energyConsumptionSearch/getDeviceSpecificTimeChart1")
    public ModelAndView getDeviceSpecificTimeChart1(
            HttpServletRequest request,
            HttpServletResponse response) {

        ModelAndView mav = new ModelAndView("jsonView");

        Map<String, Object> chartMap = setChartPart(request, mav);

        int contractId = Integer.parseInt(chartMap.get("contractId").toString());
        String basicDay = chartMap.get("basicDay").toString();
        String[] toolText = (String[]) chartMap.get("toolText");

        String meterType = DeviceType.Meter.name();
        String modemType = DeviceType.Modem.name();
        String endDeviceType = DeviceType.EndDevice.name();

        DayEM dayEM = new DayEM();
        Contract contract = contractManager.getContract(contractId);

        dayEM.setChannel(DefaultChannel.Usage.getCode());
        dayEM.setYyyymmdd(basicDay);
        dayEM.setContract(contract);
        dayEM.setMDevType(meterType);

        List<DayEM> dayEMs = energyConsumptionSearchManager.getDayEMs(dayEM);

        dayEM.setMDevType(endDeviceType);

        dayEMs.addAll(energyConsumptionSearchManager.getDayEMs(dayEM));

        dayEM.setMDevType(modemType);

        dayEMs.addAll(energyConsumptionSearchManager.getDayEMs(dayEM));

        List<Object> data = new ArrayList<Object>();
        Map<String, Object> dataMap = null;

        if (dayEMs.size() <= 0) {

            dataMap = new HashMap<String, Object>();

            dataMap.put("value", 1);

            data.add(dataMap);
        }

        List<Object> linkeddata = new ArrayList<Object>();
        List<Object> dataLink;

        Map<String, Object> linkeddataMap = null;
        Map<String, Object> linkedchartMap = null;
        Map<String, Object> chartLinkMap = null;
        Map<String, Object> dataLinkMap = null;

        Calendar calendar =  Calendar.getInstance();

        Locale locale = new Locale(contract.getSupplier().getLang().getCode_2letter(),
                contract.getSupplier().getCountry().getCode_2letter());

        int id;
        int hour;

        String amPm;
        String name;
        String toolTextData;

        Double[] values;
        Double value;
        Double meterUsage = 0.0;
        Double endDeviceUsage = 0.0;
        Double usage = 0.0;

        for (DayEM result : dayEMs) {

            String mDevType = result.getMDevType().name();

            if (endDeviceType.equals(mDevType) || modemType.equals(mDevType)) {

                dataMap = new HashMap<String, Object>();

//                usage = result.getTotal();
                usage = getNullToDouble(result.getTotal());
                endDeviceUsage += usage;
                id = result.getEnddevice().getId();
                name = result.getEnddevice().getFriendlyName();
                toolTextData = toolText[2] + " : " + name + "{br}"
                             + toolText[3] + " : " + usage + toolText[4] + "{br}"
                             + toolText[1];

                dataMap.put("label", name);
                dataMap.put("value", usage);
               //dataMap.put("displayValue", name + "\n" + usage);
                dataMap.put("toolText", toolTextData);
                dataMap.put("link", "newchart-xml-" + id);

                data.add(dataMap);

                chartLinkMap = new HashMap<String, Object>();
                chartLinkMap.put("caption", name);
                chartLinkMap.putAll((Map<String, Object>)chartMap.get("chart"));

                dataLink = new ArrayList<Object>();

                values = new Double[24];
                values = getDayValue24(result);
                value = new Double(0);

                for (int i = 0; i < 24; i++) {

                    dataLinkMap = new HashMap<String, Object>();

//                    calendar.set(Calendar.HOUR_OF_DAY, i);
//
//                    hour = calendar.get(Calendar.HOUR);
//                    amPm = new String(calendar.getDisplayName(Calendar.AM_PM, Calendar.SHORT, locale));
//
//                    if (hour % 3 == 0) {
//
//                        dataLinkMap.put("label", amPm + " " + (hour == 0 ? 12 : hour));
//                    } else {
//
//                        dataLinkMap.put("label", "");
//                    }

                    dataLinkMap.put("label", i);
                    value += values[i];

                    dataLinkMap.put("value", value);

                    dataLink.add(dataLinkMap);
                }

                linkedchartMap = new HashMap<String, Object>();
                linkedchartMap.put("chart", chartLinkMap);
                linkedchartMap.put("data", dataLink);

                linkeddataMap = new HashMap<String, Object>();
                linkeddataMap.put("id", id);
                linkeddataMap.put("linkedchart", linkedchartMap);

                linkeddata.add(linkeddataMap);
            } else if (meterType.equals(mDevType)) {

                meterUsage = result.getTotal();
            }
        }

//        usage = meterUsage - endDeviceUsage;
        usage = getNullToDouble(meterUsage - endDeviceUsage);

        if (0 < usage) {

            dataMap = new HashMap<String, Object>();

            toolTextData = toolText[2] + " : " + toolText[0] + "{br}"
                         + toolText[3] + " : " + usage + "{br}";

            dataMap.put("label", toolText[0]);
            dataMap.put("value", usage);
            dataMap.put("displayValue", toolText[0] + "\n" + usage);
            dataMap.put("toolText", toolTextData);

            data.add(dataMap);
        }

        mav.addObject("data", data);
        mav.addObject("linkeddata", linkeddata);

        DecimalFormat dfMd = DecimalUtil.getDecimalFormat(contract.getSupplier().getMd());

        mav.addObject("total", dfMd.format(meterUsage));

        return mav;
    }

    /**
     * method name : getDeviceSpecificTimeChart2
     * method Desc : 기기 - 시간별 차트2
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value="/gadget/energyConsumptionSearch/getDeviceSpecificTimeChart2")
    public ModelAndView getDeviceSpecificTimeChart2(
            HttpServletRequest request,
            HttpServletResponse response) {

        ModelAndView mav = new ModelAndView("jsonView");

        Map<String, Object> chartMap = setChartPart(request, mav);

        int contractId = Integer.parseInt(chartMap.get("contractId").toString());
        String basicDay = chartMap.get("basicDay").toString();
        String[] toolText = (String[]) chartMap.get("toolText");

        String meterType = DeviceType.Meter.name();
        String modemType = DeviceType.Modem.name();
        String endDeviceType = DeviceType.EndDevice.name();

        Contract contract = contractManager.getContract(contractId);

        List<Object> categories = new ArrayList<Object>();
        List<Object> category = new ArrayList<Object>();
        List<Object> dataset = new ArrayList<Object>();
        List<Object> data = new ArrayList<Object>();

        Map<String, Object> categoriesMap;
        Map<String, Object> categoryMap;
        Map<String, Object> datasetMap;
        Map<String, Object> dataMap;
        String seriesname;

        Calendar calendar =  Calendar.getInstance();
        Locale locale = new Locale(contract.getSupplier().getLang().getCode_2letter(),
                contract.getSupplier().getCountry().getCode_2letter());
        int hour;
        String amPm;

        for (int i = 0; i < 24; i++) {

            categoryMap = new HashMap<String, Object>();

//            calendar.set(Calendar.HOUR_OF_DAY, i);
//
//            hour = calendar.get(Calendar.HOUR);
//            amPm = new String(calendar.getDisplayName(Calendar.AM_PM, Calendar.SHORT, locale));

//            if (hour % 3 == 0) {
//
//                categoryMap.put("label", amPm + " " + (hour == 0 ? 12 : hour));
//            } else {
//
//                categoryMap.put("label", "");
//            }
            categoryMap.put("label", i);
            category.add(categoryMap);
        }

        DayEM dayEM = new DayEM();

        dayEM.setChannel(DefaultChannel.Usage.getCode());
        dayEM.setYyyymmdd(basicDay);
        dayEM.setContract(contract);
        dayEM.setMDevType(meterType);

        List<DayEM> dayEMsMeter = energyConsumptionSearchManager.getDayEMs(dayEM);

        dayEM.setMDevType(endDeviceType);

        List<DayEM> dayEMsEndDevice = energyConsumptionSearchManager.getDayEMs(dayEM);

        dayEM.setMDevType(modemType);

        dayEMsEndDevice.addAll(energyConsumptionSearchManager.getDayEMs(dayEM));

        if (dayEMsMeter.size() <= 0) {

            for (int i = 0; i < 24; i++) {

                dataMap = new HashMap<String, Object>();
                dataMap.put("value", 0);
                data.add(dataMap);
            }

            datasetMap = new HashMap<String, Object>();

            datasetMap.put("data", data);

            dataset.add(datasetMap);
        } else {

            Double[] values;
            Double[] etcValues = getDayValue24(dayEMsMeter.get(0));

            for (DayEM result : dayEMsEndDevice) {

                if (result.getEnddevice() != null) {

                    data = new ArrayList<Object>();

                    seriesname = "";
                    seriesname = result.getEnddevice().getFriendlyName();

                    values = new Double[24];
                    values = getDayValue24(result);

                    for (int i = 0; i < 24; i ++) {

                        dataMap = new HashMap<String, Object>();
                        dataMap.put("value", values[i]);
                        data.add(dataMap);

                        etcValues[i] -= values[i];
                    }

                    datasetMap = new HashMap<String, Object>();

                    datasetMap.put("seriesname", seriesname);
                    datasetMap.put("data", data);

                    dataset.add(datasetMap);
                }
            }

            data = new ArrayList<Object>();

            seriesname = "";
            seriesname = toolText[0];

            for (int i = 0; i < 24; i ++) {

                dataMap = new HashMap<String, Object>();
                dataMap.put("value", 0 < etcValues[i] ? etcValues[i] : 0);
                data.add(dataMap);
            }

            datasetMap = new HashMap<String, Object>();

            datasetMap.put("seriesname", seriesname);
            datasetMap.put("data", data);

            dataset.add(datasetMap);
        }

        categoriesMap = new HashMap<String, Object>();

        categoriesMap.put("category", category);

        categories.add(categoriesMap);

        mav.addObject("categories", categories);
        mav.addObject("dataset", dataset);

        return mav;
    }

    /**
     * method name : getDeviceSpecificDayChart1
     * method Desc : 기기 - 일별 차트1
     *
     * @param request
     * @param response
     * @return
     */
    @SuppressWarnings("unchecked")
    @RequestMapping(value="/gadget/energyConsumptionSearch/getDeviceSpecificDayChart1")
    public ModelAndView getDeviceSpecificDayChart1(
            HttpServletRequest request,
            HttpServletResponse response) {

        ModelAndView mav = new ModelAndView("jsonView");

        Map<String, Object> chartMap = setChartPart(request, mav);

        int contractId = Integer.parseInt(chartMap.get("contractId").toString());
        String basicDay = chartMap.get("basicDay").toString();
        String[] toolText = (String[]) chartMap.get("toolText");

        String meterType = DeviceType.Meter.name();
        String modemType = DeviceType.Modem.name();
        String endDeviceType = DeviceType.EndDevice.name();

        MonthEM monthEM = new MonthEM();
        Contract contract = contractManager.getContract(contractId);

        monthEM.setChannel(DefaultChannel.Usage.getCode());
        monthEM.setYyyymm(basicDay);
        monthEM.setContract(contract);
        monthEM.setMDevType(meterType);

        List<MonthEM> monthEMs = energyConsumptionSearchManager.getMonthEMs(monthEM);

        monthEM.setMDevType(endDeviceType);

        monthEMs.addAll(energyConsumptionSearchManager.getMonthEMs(monthEM));

        monthEM.setMDevType(modemType);

        monthEMs.addAll(energyConsumptionSearchManager.getMonthEMs(monthEM));

        List<Object> data = new ArrayList<Object>();
        Map<String, Object> dataMap = null;

        if (monthEMs.size() <= 0) {

            dataMap = new HashMap<String, Object>();

            dataMap.put("value", 1);

            data.add(dataMap);
        }

        List<Object> linkeddata = new ArrayList<Object>();
        List<Object> dataLink;

        Map<String, Object> linkeddataMap = null;
        Map<String, Object> linkedchartMap = null;
        Map<String, Object> chartLinkMap = null;
        Map<String, Object> dataLinkMap = null;

        Calendar calendar =  Calendar.getInstance();

        int id;

        String name;
        String toolTextData;

        Double[] values;
        Double value;
        Double meterUsage = 0.0;
        Double endDeviceUsage = 0.0;
        Double usage = 0.0;

        Calendar calendar2 = Calendar.getInstance();
        int year = Integer.parseInt(basicDay.substring(0, 4));
        int month = Integer.parseInt(basicDay.substring(4, 6));
        calendar2.set(year, month - 1, 1);
        calendar2.set(Calendar.DAY_OF_MONTH, calendar2.getActualMaximum(Calendar.DAY_OF_MONTH));
        int maxDay;

        if (calendar.compareTo(calendar2) < 0) {

            maxDay = calendar.get(Calendar.DAY_OF_MONTH);
        } else {

            maxDay = calendar2.get(Calendar.DAY_OF_MONTH);
        }

        for (MonthEM result : monthEMs) {

            String mDevType = result.getMDevType().name();

            if (endDeviceType.equals(mDevType) || modemType.equals(mDevType)) {

                dataMap = new HashMap<String, Object>();

//                usage = result.getTotal();
                usage = getNullToDouble(result.getTotal());
                endDeviceUsage += usage;
                id = result.getEnddevice().getId();
                name = result.getEnddevice().getFriendlyName();
                toolTextData = toolText[2] + " : " + name + "{br}"
                             + toolText[3] + " : " + usage + "{br}"
                             + toolText[1];

                dataMap.put("label", name);
                dataMap.put("value", usage);
                dataMap.put("displayValue", name + "\n" + usage);
                dataMap.put("toolText", toolTextData);
                dataMap.put("link", "newchart-xml-" + id);

                data.add(dataMap);

                chartLinkMap = new HashMap<String, Object>();
                chartLinkMap.put("caption", name);
                chartLinkMap.putAll((Map<String, Object>)chartMap.get("chart"));

                dataLink = new ArrayList<Object>();

                values = new Double[31];
                values = getMonthValue31(result);
                value = new Double(0);

                for (int i = 0; i < maxDay; i++) {

                    dataLinkMap = new HashMap<String, Object>();

                    dataLinkMap.put("label", i + 1);

                    value += values[i];

                    dataLinkMap.put("value", value);

                    dataLink.add(dataLinkMap);
                }

                linkedchartMap = new HashMap<String, Object>();
                linkedchartMap.put("chart", chartLinkMap);
                linkedchartMap.put("data", dataLink);

                linkeddataMap = new HashMap<String, Object>();
                linkeddataMap.put("id", id);
                linkeddataMap.put("linkedchart", linkedchartMap);

                linkeddata.add(linkeddataMap);
            } else if (meterType.equals(mDevType)) {

                meterUsage = result.getTotal();
            }
        }

//        usage = meterUsage - endDeviceUsage;
        usage = getNullToDouble(meterUsage - endDeviceUsage);

        if (0 < usage) {

            dataMap = new HashMap<String, Object>();

            toolTextData = toolText[2] + " : " + toolText[0] + "{br}"
                         + toolText[3] + " : " + usage + "{br}";

            dataMap.put("label", toolText[0]);
            dataMap.put("value", usage);
            dataMap.put("displayValue", toolText[0] + "\n" + usage);
            dataMap.put("toolText", toolTextData);

            data.add(dataMap);
        }

        mav.addObject("data", data);
        mav.addObject("linkeddata", linkeddata);

        DecimalFormat dfMd = DecimalUtil.getDecimalFormat(contract.getSupplier().getMd());

        mav.addObject("total", dfMd.format(meterUsage));

        return mav;
    }

    /**
     * method name : 
     * method Desc :
     *
     * @param meteringMonth
     * @return
     */
    private Double[] getMonthValue31(MeteringMonth meteringMonth) {

        Double[] dayValues = new Double[31];

        dayValues[0]  = (meteringMonth.getValue_01() == null ? 0 : meteringMonth.getValue_01());
        dayValues[1]  = (meteringMonth.getValue_02() == null ? 0 : meteringMonth.getValue_02());
        dayValues[2]  = (meteringMonth.getValue_03() == null ? 0 : meteringMonth.getValue_03());
        dayValues[3]  = (meteringMonth.getValue_04() == null ? 0 : meteringMonth.getValue_04());
        dayValues[4]  = (meteringMonth.getValue_05() == null ? 0 : meteringMonth.getValue_05());
        dayValues[5]  = (meteringMonth.getValue_06() == null ? 0 : meteringMonth.getValue_06());
        dayValues[6]  = (meteringMonth.getValue_07() == null ? 0 : meteringMonth.getValue_07());
        dayValues[7]  = (meteringMonth.getValue_08() == null ? 0 : meteringMonth.getValue_08());
        dayValues[8]  = (meteringMonth.getValue_09() == null ? 0 : meteringMonth.getValue_09());
        dayValues[9]  = (meteringMonth.getValue_10() == null ? 0 : meteringMonth.getValue_10());
        dayValues[10] = (meteringMonth.getValue_11() == null ? 0 : meteringMonth.getValue_11());
        dayValues[11] = (meteringMonth.getValue_12() == null ? 0 : meteringMonth.getValue_12());
        dayValues[12] = (meteringMonth.getValue_13() == null ? 0 : meteringMonth.getValue_13());
        dayValues[13] = (meteringMonth.getValue_14() == null ? 0 : meteringMonth.getValue_14());
        dayValues[14] = (meteringMonth.getValue_15() == null ? 0 : meteringMonth.getValue_15());
        dayValues[15] = (meteringMonth.getValue_16() == null ? 0 : meteringMonth.getValue_16());
        dayValues[16] = (meteringMonth.getValue_17() == null ? 0 : meteringMonth.getValue_17());
        dayValues[17] = (meteringMonth.getValue_18() == null ? 0 : meteringMonth.getValue_18());
        dayValues[18] = (meteringMonth.getValue_19() == null ? 0 : meteringMonth.getValue_19());
        dayValues[19] = (meteringMonth.getValue_20() == null ? 0 : meteringMonth.getValue_20());
        dayValues[20] = (meteringMonth.getValue_21() == null ? 0 : meteringMonth.getValue_21());
        dayValues[21] = (meteringMonth.getValue_22() == null ? 0 : meteringMonth.getValue_22());
        dayValues[22] = (meteringMonth.getValue_23() == null ? 0 : meteringMonth.getValue_23());
        dayValues[23] = (meteringMonth.getValue_24() == null ? 0 : meteringMonth.getValue_24());
        dayValues[24] = (meteringMonth.getValue_25() == null ? 0 : meteringMonth.getValue_25());
        dayValues[25] = (meteringMonth.getValue_26() == null ? 0 : meteringMonth.getValue_26());
        dayValues[26] = (meteringMonth.getValue_27() == null ? 0 : meteringMonth.getValue_27());
        dayValues[27] = (meteringMonth.getValue_28() == null ? 0 : meteringMonth.getValue_28());
        dayValues[28] = (meteringMonth.getValue_29() == null ? 0 : meteringMonth.getValue_29());
        dayValues[29] = (meteringMonth.getValue_30() == null ? 0 : meteringMonth.getValue_30());
        dayValues[30] = (meteringMonth.getValue_31() == null ? 0 : meteringMonth.getValue_31());

        return dayValues;
    }
    /**
     * method name : getDeviceSpecificDayChart2
     * method Desc : 기기 - 일별 차트2
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value="/gadget/energyConsumptionSearch/getDeviceSpecificDayChart2")
    public ModelAndView getDeviceSpecificDayChart2(
            HttpServletRequest request,
            HttpServletResponse response) {

        ModelAndView mav = new ModelAndView("jsonView");

        Map<String, Object> chartMap = setChartPart(request, mav);

        int contractId = Integer.parseInt(chartMap.get("contractId").toString());
        String basicDay = chartMap.get("basicDay").toString();
        String[] toolText = (String[]) chartMap.get("toolText");

        String meterType = DeviceType.Meter.name();
        String modemType = DeviceType.Modem.name();
        String endDeviceType = DeviceType.EndDevice.name();

        Contract contract = contractManager.getContract(contractId);

        List<Object> categories = new ArrayList<Object>();
        List<Object> category = new ArrayList<Object>();
        List<Object> dataset = new ArrayList<Object>();
        List<Object> data = new ArrayList<Object>();

        Map<String, Object> categoriesMap;
        Map<String, Object> categoryMap;
        Map<String, Object> datasetMap;
        Map<String, Object> dataMap;
        String seriesname;

        Locale locale = new Locale(contract.getSupplier().getLang().getCode_2letter(),
                contract.getSupplier().getCountry().getCode_2letter());
        Calendar calendar = Calendar.getInstance(locale);
        Calendar calendar2 = Calendar.getInstance();
        int year = Integer.parseInt(basicDay.substring(0, 4));
        int month = Integer.parseInt(basicDay.substring(4, 6));
        calendar2.set(year, month - 1, 1);
        calendar2.set(Calendar.DAY_OF_MONTH, calendar2.getActualMaximum(Calendar.DAY_OF_MONTH));
        int maxDay;

        if (calendar.compareTo(calendar2) < 0) {

            maxDay = calendar.get(Calendar.DAY_OF_MONTH);
        } else {

            maxDay = calendar2.get(Calendar.DAY_OF_MONTH);
        }

        for (int i = 1; i <= maxDay; i++) {

            categoryMap = new HashMap<String, Object>();

            categoryMap.put("label", i);

            category.add(categoryMap);
        }

        MonthEM monthEM = new MonthEM();

        monthEM.setChannel(DefaultChannel.Usage.getCode());
        monthEM.setYyyymm(basicDay);
        monthEM.setContract(contract);
        monthEM.setMDevType(meterType);

        List<MonthEM> monthEMsMeter = energyConsumptionSearchManager.getMonthEMs(monthEM);

        monthEM.setMDevType(endDeviceType);

        List<MonthEM> monthEMsEndDevice = energyConsumptionSearchManager.getMonthEMs(monthEM);

        monthEM.setMDevType(modemType);

        monthEMsEndDevice.addAll(energyConsumptionSearchManager.getMonthEMs(monthEM));

        if (monthEMsMeter.size() <= 0) {

            for (int i = 0; i < maxDay; i++) {

                dataMap = new HashMap<String, Object>();
                dataMap.put("value", 0);
                data.add(dataMap);
            }

            datasetMap = new HashMap<String, Object>();

            datasetMap.put("data", data);

            dataset.add(datasetMap);
        } else {

            Double[] values;
            Double[] etcValues = getMonthValue31(monthEMsMeter.get(0));

            for (MonthEM result : monthEMsEndDevice) {

                if (result.getEnddevice() != null) {

                    data = new ArrayList<Object>();
                    seriesname = "";

                    seriesname = result.getEnddevice().getFriendlyName();

                    values = new Double[31];
                    values = getMonthValue31(result);

                    for (int i = 0; i < maxDay; i++) {

                        dataMap = new HashMap<String, Object>();
                        dataMap.put("value", values[i]);
                        data.add(dataMap);

                        etcValues[i] -= values[i];
                    }

                    datasetMap = new HashMap<String, Object>();

                    datasetMap.put("seriesname", seriesname);
                    datasetMap.put("data", data);

                    dataset.add(datasetMap);
                }
            }

            data = new ArrayList<Object>();

            seriesname = "";

            seriesname = toolText[0];

            for (int i = 0; i < maxDay; i++) {

                dataMap = new HashMap<String, Object>();
                dataMap.put("value", 0 < etcValues[i] ? etcValues[i] : 0);
                data.add(dataMap);
            }

            datasetMap = new HashMap<String, Object>();

            datasetMap.put("seriesname", seriesname);
            datasetMap.put("data", data);

            dataset.add(datasetMap);
        }

        categoriesMap = new HashMap<String, Object>();

        categoriesMap.put("category", category);

        categories.add(categoriesMap);

        mav.addObject("categories", categories);
        mav.addObject("dataset", dataset);

        return mav;
    }

    /**
     * method name : getDeviceSpecificMonthChart1
     * method Desc : 기기 - 월별 차트1
     *
     * @param request
     * @param response
     * @return
     */
    @SuppressWarnings("unchecked")
    @RequestMapping(value="/gadget/energyConsumptionSearch/getDeviceSpecificMonthChart1")
    public ModelAndView getDeviceSpecificMonthChart1(
            HttpServletRequest request,
            HttpServletResponse response) {

        ModelAndView mav = new ModelAndView("jsonView");

        Map<String, Object> chartMap = setChartPart(request, mav);

        int contractId = Integer.parseInt(chartMap.get("contractId").toString());
        String basicDay = chartMap.get("basicDay").toString();
        String[] toolText = (String[]) chartMap.get("toolText");

        String meterType = DeviceType.Meter.name();
        String modemType = DeviceType.Modem.name();
        String endDeviceType = DeviceType.EndDevice.name();

        MonthEM monthEM = new MonthEM();
        Contract contract = contractManager.getContract(contractId);

        monthEM.setChannel(DefaultChannel.Usage.getCode());
        monthEM.setYyyymm(basicDay);
        monthEM.setContract(contract);
        monthEM.setMDevType(meterType);

        List<Map<String, Object>> sumMonthEMs = energyConsumptionSearchManager.getSumMonthEMs(monthEM);

        monthEM.setMDevType(endDeviceType);

        sumMonthEMs.addAll(energyConsumptionSearchManager.getSumMonthEMs(monthEM));

        monthEM.setMDevType(modemType);

        sumMonthEMs.addAll(energyConsumptionSearchManager.getSumMonthEMs(monthEM));

        List<Object> data = new ArrayList<Object>();
        Map<String, Object> dataMap = null;

        if (sumMonthEMs.size() <= 0) {

            dataMap = new HashMap<String, Object>();

            dataMap.put("value", 1);

            data.add(dataMap);
        }

        List<Object> linkeddata = new ArrayList<Object>();
        List<Object> dataLink;

        Map<String, Object> linkeddataMap = null;
        Map<String, Object> linkedchartMap = null;
        Map<String, Object> chartLinkMap = null;
        Map<String, Object> dataLinkMap = null;

        int id;

        String name;
        String toolTextData;

        Double value;
        Double meterUsage = 0.0;
        Double endDeviceUsage = 0.0;
        Double usage = 0.0;

        EndDevice endDevice;

        for (Map<String, Object> result : sumMonthEMs) {

            endDevice = new EndDevice();
            endDevice = (EndDevice)result.get("enddevice");

            if (endDevice != null) {

                dataMap = new HashMap<String, Object>();

//                usage = Double.parseDouble(result.get("total").toString());
                usage = getNullToDouble(Double.parseDouble(result.get("total").toString()));
                endDeviceUsage += usage;
                id = endDevice.getId();
                name = endDevice.getFriendlyName();
                toolTextData = toolText[2] + " : " + name + "{br}"
                             + toolText[3] + " : " + usage + "{br}"
                             + toolText[1];

                dataMap.put("label", name);
                dataMap.put("value", usage);
                dataMap.put("displayValue", name + "\n" + usage);
                dataMap.put("toolText", toolTextData);
                dataMap.put("link", "newchart-xml-" + id);

                data.add(dataMap);

                chartLinkMap = new HashMap<String, Object>();
                chartLinkMap.put("caption", name);
                chartLinkMap.putAll((Map<String, Object>)chartMap.get("chart"));

                dataLink = new ArrayList<Object>();

                monthEM = new MonthEM();

                monthEM.setChannel(DefaultChannel.Usage.getCode());
                monthEM.setYyyymm(basicDay);
                monthEM.setContract(contract);
                monthEM.setEnddevice(endDevice);

                List<MonthEM> monthEMs = energyConsumptionSearchManager.getMonthEMs(monthEM);

                value = new Double(0);

                for (int i = 0; i < monthEMs.size(); i++) {

                    dataLinkMap = new HashMap<String, Object>();

                    dataLinkMap.put("label", i + 1);

                    value = new Double(value + (monthEMs.get(i).getTotal() == null ? 0 : monthEMs.get(i).getTotal()));

                    dataLinkMap.put("value", value);

                    dataLink.add(dataLinkMap);
                }

                linkedchartMap = new HashMap<String, Object>();
                linkedchartMap.put("chart", chartLinkMap);
                linkedchartMap.put("data", dataLink);

                linkeddataMap = new HashMap<String, Object>();
                linkeddataMap.put("id", id);
                linkeddataMap.put("linkedchart", linkedchartMap);

                linkeddata.add(linkeddataMap);
            } else {

                meterUsage = Double.parseDouble(result.get("total").toString());
            }
        }

//        usage = meterUsage - endDeviceUsage;
        usage = getNullToDouble(meterUsage - endDeviceUsage);

        if (0 < usage) {

            dataMap = new HashMap<String, Object>();

            toolTextData = toolText[2] + " : " + toolText[0] + "{br}"
                         + toolText[3] + " : " + usage + "{br}";

            dataMap.put("label", toolText[0]);
            dataMap.put("value", usage);
            dataMap.put("displayValue", toolText[0] + "\n" + usage);
            dataMap.put("toolText", toolTextData);

            data.add(dataMap);
        }

        mav.addObject("data", data);
        mav.addObject("linkeddata", linkeddata);

        DecimalFormat dfMd = DecimalUtil.getDecimalFormat(contract.getSupplier().getMd());

        mav.addObject("total", dfMd.format(meterUsage));

        return mav;
    }

    /**
     * method name : getDeviceSpecificMonthChart2
     * method Desc : 기기 - 월별 차트2
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value="/gadget/energyConsumptionSearch/getDeviceSpecificMonthChart2")
    public ModelAndView getDeviceSpecificMonthChart2(
            HttpServletRequest request,
            HttpServletResponse response) {

        ModelAndView mav = new ModelAndView("jsonView");

        Map<String, Object> chartMap = setChartPart(request, mav);

        int contractId = Integer.parseInt(chartMap.get("contractId").toString());
        String basicDay = chartMap.get("basicDay").toString();
        String[] toolText = (String[]) chartMap.get("toolText");

        String meterType = DeviceType.Meter.name();
        String modemType = DeviceType.Modem.name();
        String endDeviceType = DeviceType.EndDevice.name();

        MonthEM monthEM = new MonthEM();
        Contract contract = contractManager.getContract(contractId);

        monthEM.setChannel(DefaultChannel.Usage.getCode());
        monthEM.setYyyymm(basicDay);
        monthEM.setContract(contract);
        monthEM.setMDevType(meterType);

        List<MonthEM> monthEMsMeter = energyConsumptionSearchManager.getMonthEMs(monthEM);

        monthEM.setMDevType(endDeviceType);

        List<MonthEM> monthEMsEndDevice = energyConsumptionSearchManager.getMonthEMs(monthEM);

        monthEM.setMDevType(modemType);

        monthEMsEndDevice.addAll(energyConsumptionSearchManager.getMonthEMs(monthEM));

        List<Object> categories = new ArrayList<Object>();
        List<Object> category = new ArrayList<Object>();
        List<Object> dataset = new ArrayList<Object>();
        List<Object> data = new ArrayList<Object>();
        List<Object> etcData = new ArrayList<Object>();

        Map<String, Object> categoriesMap;
        Map<String, Object> categoryMap;
        Map<String, Object> datasetMap;
        Map<String, Object> dataMap;
        Map<String, Object> etcDataMap;

        if (0 >= monthEMsMeter.size()) {

            categoryMap = new HashMap<String, Object>();
            categoryMap.put("label", "");
            category.add(categoryMap);

            dataMap = new HashMap<String, Object>();
            dataMap.put("value", 0);
            data.add(dataMap);

            datasetMap = new HashMap<String, Object>();
            datasetMap.put("data", data);
            dataset.add(datasetMap);
        } else {

            Map<Integer, Double> etcUsageMap = new HashMap<Integer, Double>();

            for (MonthEM monthEMMeter : monthEMsMeter) {

                int label = Integer.parseInt(monthEMMeter.getYyyymm().substring(4,6));

                etcUsageMap.put(label, monthEMMeter.getTotal() == null ? 0.0 : monthEMMeter.getTotal());

                categoryMap = new HashMap<String, Object>();
                categoryMap.put("label", label);
                category.add(categoryMap);
            }

            String seriesname = null;

            MonthEM same;

            for (int i = 0; i < monthEMsEndDevice.size(); i++) {

                data.clear();
                seriesname = "";

                seriesname = monthEMsEndDevice.get(i).getEnddevice().getFriendlyName();

                for (MonthEM monthEMMeter : monthEMsMeter) {

                    int label = Integer.parseInt(monthEMMeter.getYyyymm().substring(4,6));

                    Double etcUsage = etcUsageMap.get(label);

                    same = new MonthEM();

                    for (int j = 0; j < monthEMsEndDevice.size(); j++) {

                        if (seriesname.equals(monthEMsEndDevice.get(j).getEnddevice().getFriendlyName())
                            && label == Integer.parseInt(monthEMsEndDevice.get(j).getYyyymm().substring(4, 6))) {

                            same = monthEMsEndDevice.get(j);
                            monthEMsEndDevice.remove(j);

                            if (i >= j) {
                                i--;
                            }

                            break;
                        }
                    }

                    dataMap = new HashMap<String, Object>();

                    Double usage = same.getTotal() == null ? 0.0 : same.getTotal();
                    dataMap.put("value", usage);

                    etcUsage -= usage;

                    etcUsageMap.put(label, etcUsage);

                    data.add(dataMap);
                }

                datasetMap = new HashMap<String, Object>();

                datasetMap.put("seriesname", seriesname);
                datasetMap.put("data", data);

                dataset.add(datasetMap);
            }

            for (MonthEM monthEMMeter : monthEMsMeter) {

                int label = Integer.parseInt(monthEMMeter.getYyyymm().substring(4,6));

                Double etcUsage = etcUsageMap.get(label);

                etcDataMap = new HashMap<String, Object>();

                etcDataMap.put("value", 0 < etcUsage ? etcUsage : 0);

                etcData.add(etcDataMap);
            }

            datasetMap = new HashMap<String, Object>();

            datasetMap.put("seriesname", toolText[0]);
            datasetMap.put("data", etcData);

            dataset.add(datasetMap);
        }

        categoriesMap = new HashMap<String, Object>();

        categoriesMap.put("category", category);

        categories.add(categoriesMap);

        mav.addObject("categories", categories);
        mav.addObject("dataset", dataset);

        return mav;
    }

    /**
     * method name : getDeviceSpecificGrid
     * method Desc : 기기 - 그리드
     *
     * @param basicDay
     * @param contractId
     * @return
     */
    @RequestMapping(value="/gadget/energyConsumptionSearch/getDeviceSpecificGrid")
    public ModelAndView getDeviceSpecificGrid(
            @RequestParam("basicDay") String basicDay,
            @RequestParam("contractId") int contractId) {

        List<Map<String, Object>> result = energyConsumptionSearchManager.getDeviceSpecificGrid(basicDay, contractId);

        ModelAndView mav = new ModelAndView("jsonView");

        mav.addObject("result", result);

        return mav;
    }

    /**
     * method name : getYesterday
     * method Desc : 어제 일자 조회
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

        calendar.set(year, month, date - 1);

        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");

        return formatter.format(calendar.getTime());
    }

    /**
     * method name : getLastMonth
     * method Desc : 기준달의 전달 구하기
     *
     * @param someDay
     * @return
     */
    private String getLastMonth(String someMonth) {

        int year = Integer.parseInt(someMonth.substring(0, 4));
        int month = Integer.parseInt(someMonth.substring(4, 6)) - 1;
        int date = 1;

        Calendar calendar =  Calendar.getInstance();

        calendar.set(year, month - 1, date);

        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMM");

        return formatter.format(calendar.getTime());
    }

    /**
     * method name : getLastYear
     * method Desc : 기준년의 전년 구하기
     *
     * @param someYear
     * @return
     */
    private String getLastYear(String someYear) {

        int year = Integer.parseInt(someYear.substring(0, 4));
        int month = 0;
        int date = 1;

        Calendar calendar =  Calendar.getInstance();

        calendar.set(year - 1, month, date);

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy");

        return formatter.format(calendar.getTime());
    }
    /**
     * method name : setChartType
     * method Desc : 차트 타입별 Data 생성 변경
     *
     * @param chartType
     * @param color
     * @param label
     * @param datas
     * @param mav
     */
    private void setChartType(int chartType, String[] color, String[] label,
            List<Object> datas, ModelAndView mav) {

        switch (chartType) {
        case 0:

            break;

        case 1:

            setMultiPart(mav, color, label, datas);

            break;

        default:
            break;
        }

    }

    /**
     * method name : getChartPart
     * method Desc : 차트 기본 정보 설정
     *
     * @param request
     * @param chart
     * @param color
     * @param chartType
     */
    @SuppressWarnings("unchecked")
    private Map<String, Object> setChartPart(
            HttpServletRequest request,
            ModelAndView mav) {

        Enumeration<String> paramKey = request.getParameterNames();

        Map<String, Object> chart = new HashMap<String, Object>();
        Map<String, Object> returnMap = new HashMap<String, Object>();

        String keyStr;
        String valueStr;
        String[] color = null;
        String[] label = null;
        String[] tooltext = null;

        while(paramKey.hasMoreElements()){

            keyStr = "";
            keyStr = paramKey.nextElement();

            if ("color[]".equals(keyStr)) {

                color = request.getParameterValues(keyStr);
            } else if ("label[]".equals(keyStr)) {

                label = request.getParameterValues(keyStr);
            } else if ("toolText[]".equals(keyStr)) {

                tooltext = request.getParameterValues(keyStr);
            } else if ("chartType".equals(keyStr)) {

                valueStr = "";
                valueStr = request.getParameter(keyStr);

                returnMap.put("chartType", valueStr);
            } else if ("basicDay".equals(keyStr)) {

                valueStr = "";
                valueStr = request.getParameter(keyStr);

                returnMap.put("basicDay", valueStr);
            } else if ("contractId".equals(keyStr)) {

                valueStr = "";
                valueStr = request.getParameter(keyStr);

                returnMap.put("contractId", valueStr);
            } else if ("moneyText".equals(keyStr)) {

                valueStr = "";
                valueStr = request.getParameter(keyStr);

                returnMap.put("moneyText", valueStr);
            } else if ("trandlineText".equals(keyStr)) {

                valueStr = "";
                valueStr = request.getParameter(keyStr);

                returnMap.put("trandlineText", valueStr);
            }   else {

                valueStr = "";
                valueStr = request.getParameter(keyStr);

                chart.put(keyStr, valueStr);
            }
        }

        mav.addObject("chart", chart);

        returnMap.put("toolText", tooltext);
        returnMap.put("label", label);
        returnMap.put("color", color);
        returnMap.put("chart", chart);

        return returnMap;
    }

    /**
     * method name : getMultiPart
     * method Desc : Multi-Series 차트 정보 설정
     *
     * @param mav
     * @param color
     * @param datas
     * @param label
     */
    private void setMultiPart(ModelAndView mav, String[] color, String[] label, List<Object> datas) {

        List<Object> categories = new ArrayList<Object>();
        List<Object> category = new ArrayList<Object>();
        List<Object> dataset = new ArrayList<Object>();
        List<Object> data = new ArrayList<Object>();

        Map<String, Object> categoryMap;
        Map<String, Object> categoriesMap;
        Map<String, Object> datasetMap;
        Map<String, Object> dataMap;
        String seriesname = "";

        for (int i = 0; i < datas.size(); i ++) {

            categoryMap = new HashMap<String, Object>();

            categoryMap.put("label", label[i]);

            category.add(categoryMap);

            dataMap = new HashMap<String, Object>();

            dataMap.put("value", datas.get(i));
            dataMap.put("color", color[i % color.length]);

            data.add(dataMap);
        }

        categoriesMap = new HashMap<String, Object>();

        categoriesMap.put("category", category);

        categories.add(categoriesMap);

        datasetMap = new HashMap<String, Object>();

        datasetMap.put("seriesname", seriesname);

        dataset.add(datasetMap);

        datasetMap = new HashMap<String, Object>();

        datasetMap.put("data", data);

        dataset.add(datasetMap);

        mav.addObject("categories", categories);
        mav.addObject("dataset", dataset);
    }

    /**
     * method name : getNullToDouble
     * method Desc : value 가 null 이면 0, 그 외는 소수 4자리 Double 로 리턴
     *
     * @param value
     * @return
     */
    private Double getNullToDouble(Double value) {
        Double result = 0D;
        String strValue = "0";
        if (value != null) {
            strValue = String.format("%.4f", value);
            result = Double.parseDouble(strValue);
        }

        return result;
    }
}
