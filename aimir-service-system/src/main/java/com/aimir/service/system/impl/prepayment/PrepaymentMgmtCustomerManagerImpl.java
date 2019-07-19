package com.aimir.service.system.impl.prepayment;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.aimir.constants.CommonConstants.MeterType;
import com.aimir.dao.device.MeterDao;
import com.aimir.dao.mvm.BillingDayEMDao;
import com.aimir.dao.mvm.BillingDayGMDao;
import com.aimir.dao.mvm.BillingDayWMDao;
import com.aimir.dao.system.CodeDao;
import com.aimir.dao.system.ContractChangeLogDao;
import com.aimir.dao.system.ContractDao;
import com.aimir.dao.system.OperatorDao;
import com.aimir.dao.system.PrepaymentLogDao;
import com.aimir.dao.system.SupplierDao;
import com.aimir.dao.system.TariffEMDao;
import com.aimir.dao.system.TariffGMDao;
import com.aimir.dao.system.TariffWMCaliberDao;
import com.aimir.dao.system.TariffWMDao;
import com.aimir.model.system.Code;
import com.aimir.model.system.Contract;
import com.aimir.model.system.ContractChangeLog;
import com.aimir.model.system.Operator;
import com.aimir.model.system.PrepaymentAuthDevice;
import com.aimir.model.system.Supplier;
import com.aimir.service.system.prepayment.PrepaymentMgmtCustomerManager;
import com.aimir.util.CalendarUtil;
import com.aimir.util.DateTimeUtil;
import com.aimir.util.DecimalUtil;
import com.aimir.util.StringUtil;
import com.aimir.util.TimeLocaleUtil;
import com.aimir.util.TimeUtil;

@Service(value = "prepaymentMgmtCustomerManager")
public class PrepaymentMgmtCustomerManagerImpl implements PrepaymentMgmtCustomerManager {

    private static Log logger = LogFactory.getLog(PrepaymentMgmtCustomerManagerImpl.class);

    @Autowired
    ContractDao contractDao; 

    @Autowired
    ContractChangeLogDao contractChangeLogDao; 
    
    @Autowired
    PrepaymentLogDao prepaymentLogDao; 

    @Autowired
    SupplierDao supplierDao;

    @Autowired
    OperatorDao operatorDao; 

    @Autowired
    BillingDayEMDao billingDayEMDao;

    @Autowired
    BillingDayGMDao billingDayGMDao;

    @Autowired
    BillingDayWMDao billingDayWMDao;

    @Autowired
    CodeDao codeDao; 
    
    @Autowired
    MeterDao meterDao;

    @Autowired
    TariffEMDao tariffEMDao;

    @Autowired
    TariffGMDao tariffGMDao;
    
    @Autowired
    TariffWMDao tariffWMDao;
    
    @Autowired
    TariffWMCaliberDao tariffWMCaliberDao;

	/**
	 * method name : getChargeInfo
	 * method Desc : 고객 선불관리 화면의 충전 정보를 조회한다.
	 *
	 * @param conditionMap
	 * @return
	 */
    @SuppressWarnings("unused")
    @Transactional(readOnly=false)
	public Map<String, Object> getChargeInfo(Map<String, Object> conditionMap) {

		Map<String, Object> result = new HashMap<String, Object>();

		// TODO 선불잔액 실시간 계산 start
        String contractNumber = StringUtil.nullToBlank(conditionMap.get("contractNumber"));
        String serviceType = StringUtil.nullToBlank(conditionMap.get("serviceType"));

        Contract contract = contractDao.findByCondition("contractNumber", contractNumber);

        //추후 필요여부에 따라 구현 필요 - DaoImpl에 있는 선불계산 관련 소스들을 스케줄러로 분리함에 따라 주석처리.
        if(serviceType.equals(MeterType.EnergyMeter.getServiceType())) {
            // Electricity
//            tariffEMDao.saveEmBillingDailyWithTariffEM(contract);
//        } else if(serviceType.equals(MeterType.GasMeter.getServiceType())) {
//            // Gas
//            tariffGMDao.saveGmBillingDayWithTariffGM(contract);
        } else if(serviceType.equals(MeterType.WaterMeter.getServiceType())) {
            // Water
//            tariffWMDao.saveWMChargeUsingDailyUsage(contract);
        }
		// 선불잔액 실시간 계산 end

		List<Map<String, Object>> list = prepaymentLogDao.getChargeInfo(conditionMap);

		if (list != null && list.size() > 0) {
		    result = list.get(0);
		} else {
		    return result;
		}

		Supplier supplier = supplierDao.get((Integer)conditionMap.get("supplierId"));
		String lang = supplier.getLang().getCode_2letter();
		String country = supplier.getCountry().getCode_2letter();
		DecimalFormat df = DecimalUtil.getDecimalFormat(supplier.getCd());

		result.put("lastTokenDateView", TimeLocaleUtil.getLocaleDateByMediumFormat(((String)result.get("lastTokenDate")).substring(0, 8), lang, country));

		if (result.get("currentCredit") == null) {
		    result.put("currentCredit", new Double(0d));
		}
		if (result.get("balance") == null) {
		    result.put("balance", new Double(0d));
		}

		result.put("currentCreditView", df.format((Double)result.get("currentCredit")));
		//result.put("chargedCreditView", df.format((Integer)result.get("chargedCredit")));
		result.put("balanceView", df.format((Double)result.get("balance")));

		// 게이지차트 임계점
		result.put("threshold1", new Double((Double)result.get("balance") * 0.2).intValue());
		result.put("threshold2", new Double((Double)result.get("balance") * 0.5).intValue());

		if (!StringUtil.nullToBlank(result.get("emergencyCreditStartTime")).isEmpty()) {
	        result.put("emergencyCreditStartTimeView", TimeLocaleUtil.getLocaleDateByMediumFormat(((String)result.get("emergencyCreditStartTime")).substring(0, 8), lang, country));

	        try {
	            String currentDateTime = TimeUtil.getCurrentTime();
	            String limitDate = CalendarUtil.getDate((String)result.get("emergencyCreditStartTime"), Calendar.DAY_OF_MONTH, (Integer)result.get("emergencyCreditMaxDuration"));
	            
	            result.put("limitDateView", TimeLocaleUtil.getLocaleDateByMediumFormat(limitDate, lang, country));
	            result.put("limitDuration", TimeUtil.getDayDuration(currentDateTime, limitDate));

	        } catch(Exception e) {
	            e.printStackTrace();
	        }
		}

		return result;
	}	

    /**
     * method name : getBalanceNotifySetting
     * method Desc : 고객 선불관리 화면의 통보설정 정보를 조회한다.
     *
     * @param conditionMap
     * @return
     */
    public Map<String, Object> getBalanceNotifySetting(Map<String, Object> conditionMap) {
        Map<String, Object> result = new HashMap<String, Object>();

        Contract contract = contractDao.findByCondition("contractNumber", (String)conditionMap.get("contractNumber"));

        if (contract == null || contract.getId() == null) {
            return result;
        }
        
        Supplier supplier = supplierDao.get((Integer)conditionMap.get("supplierId"));
        String lang = supplier.getLang().getCode_2letter();
        String country = supplier.getCountry().getCode_2letter();

        DecimalFormat idf = DecimalUtil.getIntDecimalFormat(true);
        DecimalFormat mdf = DecimalUtil.getDecimalFormat(supplier.getMd());

        result.put("contractNumber", contract.getContractNumber());
        result.put("notificationPeriod", contract.getNotificationPeriod());
        result.put("notificationInterval", contract.getNotificationInterval());
        result.put("notificationTime", contract.getNotificationTime());

        if (!StringUtil.nullToBlank(contract.getNotificationTime()).isEmpty()) {
            String notificationTimeView = TimeLocaleUtil.getLocaleHourMinute(StringUtil.frontAppendNStr('0', contract.getNotificationTime().toString(), 2)+"00", lang, country);
            result.put("notificationTimeView", notificationTimeView);
        } else {
            result.put("notificationTimeView", "");
        }

        result.put("notificationWeeklyMon", contract.getNotificationWeeklyMon());
        result.put("notificationWeeklyTue", contract.getNotificationWeeklyTue());
        result.put("notificationWeeklyWed", contract.getNotificationWeeklyWed());
        result.put("notificationWeeklyThu", contract.getNotificationWeeklyThu());
        result.put("notificationWeeklyFri", contract.getNotificationWeeklyFri());
        result.put("notificationWeeklySat", contract.getNotificationWeeklySat());
        result.put("notificationWeeklySun", contract.getNotificationWeeklySun());
        result.put("lastNotificationDate", contract.getLastNotificationDate());
        result.put("prepaymentThreshold", contract.getPrepaymentThreshold());
        
        if (contract.getPrepaymentThreshold() != null) {
            result.put("prepaymentThresholdView", idf.format(contract.getPrepaymentThreshold()));
        } else {
            result.put("prepaymentThresholdView", "0");
        }

        result.put("prepaymentPowerDelay", contract.getPrepaymentPowerDelay());

        if (contract.getPrepaymentPowerDelay() != null) {
            result.put("prepaymentPowerDelayView", mdf.format(contract.getPrepaymentPowerDelay()));
        } else {
            result.put("prepaymentPowerDelayView", mdf.format(new Double(0.0)));
        }

        result.put("emergencyCreditAutoChange", contract.getEmergencyCreditAutoChange());
        result.put("emergencyCreditStartTime", contract.getEmergencyCreditStartTime());
        result.put("emergencyCreditMaxDuration", contract.getEmergencyCreditMaxDuration());
        result.put("creditType", contract.getCreditType().getCode());
        
        Set<PrepaymentAuthDevice> devices = contract.getDevices();
        
        Iterator<PrepaymentAuthDevice> itr = devices.iterator();
        PrepaymentAuthDevice authDevice = null;
        Map<String, Object> dMap = new HashMap<String, Object>();
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

        while (itr.hasNext()) {
            authDevice = itr.next();
            dMap = new HashMap<String, Object>();

            dMap.put("id", authDevice.getId());
            dMap.put("authKey", authDevice.getAuthKey());
            dMap.put("friendlyName", authDevice.getFriendlyName());
            dMap.put("writeDate", authDevice.getWriteDate());
            list.add(dMap);
        }

        result.put("devices", list);

        return result;
    }

    /**
     * method name : getLocaleFormatAllHours
     * method Desc : Locale formatting 된 00시 ~ 23시 시간형식 리스트를 조회한다.
     *
     * @param supplierId
     * @return
     */
    public List<String> getLocaleFormatAllHours(Integer supplierId) {
        List<String> allHours = new ArrayList<String>();
        Supplier supplier = supplierDao.get(supplierId);
        String lang = supplier.getLang().getCode_2letter();
        String country = supplier.getCountry().getCode_2letter();

        for (int i = 0 ; i < 24 ; i++) {
            allHours.add(TimeLocaleUtil.getLocaleHourMinute(StringUtil.frontAppendNStr('0', Integer.toString(i), 2)+"00", lang, country));
        }
        
        return allHours;
    }

    /**
     * method name : updateBalanceNotifySetting
     * method Desc : 고객 선불관리 통보설정 정보를 저장한다.
     *
     * @param conditionMap
     */
    @Transactional
    public void updateBalanceNotifySetting(Map<String, Object> conditionMap) {

        String contractNumber = (String)conditionMap.get("contractNumber");
        Integer operatorId = (Integer)conditionMap.get("operatorId");
        Integer period = (Integer)conditionMap.get("period");
        Integer interval = (Integer)conditionMap.get("interval");
        Integer hour = (Integer)conditionMap.get("hour");
        Integer threshold = (Integer)conditionMap.get("threshold");
        Boolean mon = (Boolean)conditionMap.get("mon");
        Boolean tue = (Boolean)conditionMap.get("tue");
        Boolean wed = (Boolean)conditionMap.get("wed");
        Boolean thu = (Boolean)conditionMap.get("thu");
        Boolean fri = (Boolean)conditionMap.get("fri");
        Boolean sat = (Boolean)conditionMap.get("sat");
        Boolean sun = (Boolean)conditionMap.get("sun");

        Contract contract = contractDao.findByCondition("contractNumber", contractNumber);

        // prepaymentThreshold 변경값이 있을 경우 ContractChangeLog 에 insert
        if (!StringUtil.nullToZero(contract.getPrepaymentThreshold()).equals(StringUtil.nullToZero(threshold))) {
            Operator operator = operatorDao.getOperatorById(operatorId);
            addContractChangeLog(contract, operator, "prepaymentThreshold", contract.getPrepaymentThreshold(), threshold);
        }

        // update Contract
        contract.setNotificationPeriod(period);
        contract.setNotificationInterval(interval);
        contract.setNotificationTime(hour);
        contract.setPrepaymentThreshold(threshold);
        contract.setNotificationWeeklyMon(mon);
        contract.setNotificationWeeklyTue(tue);
        contract.setNotificationWeeklyWed(wed);
        contract.setNotificationWeeklyThu(thu);
        contract.setNotificationWeeklyFri(fri);
        contract.setNotificationWeeklySat(sat);
        contract.setNotificationWeeklySun(sun);

        contractDao.update(contract);
    }

    /**
     * method name : getChargeHistory
     * method Desc : 고객 선불관리 맥스가젯의 충전 이력 리스트를 조회한다.
     *
     * @param conditionMap
     * @return
     */
    public List<Map<String, Object>> getChargeHistory(Map<String, Object> conditionMap) {
        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();

        List<Map<String, Object>> list = prepaymentLogDao.getChargeHistory(conditionMap, false);
        
        int dataCount = list.size();
        Boolean isExcel = (Boolean) (conditionMap.get("isExcel") == null ? false : conditionMap.get("isExcel"));
        
        //Excel출력의 경우 아래 소스를 타지 않는다.
        if(!isExcel) {
	        try {
	            if (dataCount > 0) {
	                Supplier supplier = supplierDao.get((Integer)conditionMap.get("supplierId"));
	                String lang = supplier.getLang().getCode_2letter();
	                String country = supplier.getCountry().getCode_2letter();
	                DecimalFormat cdf = DecimalUtil.getDecimalFormat(supplier.getCd());
	                DecimalFormat mdf = DecimalUtil.getDecimalFormat(supplier.getMd());
	
	                Map<String, Object> map = new HashMap<String, Object>();
	                Double usedCost = 0D;
	                Double usedConsumption = 0D;
	                Double chargedCredit = 0d;
	                Double balance = 0d;
	                
	                for (int i = 0 ; i < dataCount ; i++) {
	                	map = list.get(i);
	                    usedCost = Double.parseDouble(map.get("usedCost") == null ? "0" : map.get("usedCost").toString());
	                    usedConsumption = Double.parseDouble(map.get("usedConsumption") == null ? "0" : map.get("usedConsumption").toString());
	                    chargedCredit = Double.parseDouble(map.get("chargedCredit") == null ? "0" : map.get("chargedCredit").toString());
	                    balance = Double.parseDouble(map.get("balance") == null ? "0" : map.get("balance").toString());
	                    
	                    map.put("lastTokenDateView", TimeLocaleUtil.getLocaleDateByMediumFormat(((String)map.get("lastTokenDate")), lang, country));
	                    
                    	map.put("chargedCreditView", cdf.format(chargedCredit));
                    	map.put("balanceView", cdf.format(balance));
                    	map.put("usedCost", cdf.format(usedCost));
                        map.put("usedConsumption", mdf.format(usedConsumption));
                        
	                    //충전된 정보일 경우는 차감된 요금과 사용량을 0으로 보여준다.
	                    if(map.get("chargedCredit") != null ) {
		                    if(!(map.get("chargedCredit").toString().equals("0.0"))) {
		                    	//충전됐을 때
		                    	map.put("usedCost", cdf.format(0.0));
		                        map.put("usedConsumption", mdf.format(0.0));
		                    } 
	                    }
	                    result.add(map);
	                }
	            }
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
        } else {
        	result = list;
        }
        return result;
    }

    /* (non-Javadoc)
     * @see com.aimir.service.system.prepayment.PrepaymentMgmtCustomerManager#getChargeHistoryForCustomer(java.util.Map)
     */
    public List<Map<String, Object>> getChargeHistoryForCustomer(Map<String, Object> conditionMap) {

    	List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
    	List<Map<String, Object>> list =  prepaymentLogDao.getChargeHistoryForCustomer(conditionMap, false);
    	
        Supplier supplier = supplierDao.get((Integer)conditionMap.get("supplierId"));
        String lang = supplier.getLang().getCode_2letter();
        String country = supplier.getCountry().getCode_2letter();
        DecimalFormat cdf = DecimalUtil.getDecimalFormat(supplier.getCd());
//        DecimalFormat mdf = DecimalUtil.getDecimalFormat(supplier.getMd());
        
    	for(Map<String, Object> map : list) {
            //map.put("lastTokenDateView", TimeLocaleUtil.getLocaleDateByMediumFormat(((String)map.get("lastTokenDate")).substring(0, 8), lang, country));
            map.put("lastTokenDateView", TimeLocaleUtil.getLocaleDateByMediumFormat(((String)map.get("lastTokenDate")), lang, country));
            //map.put("lastTokenDateView", TimeLocaleUtil.getLocaleDate(((String)map.get("lastTokenDate")), lang, country));
            map.put("chargedCreditView", cdf.format(map.get("chargedCredit") == null ? 0 : (Double)map.get("chargedCredit")));
            map.put("balanceView", cdf.format((Double)map.get("balance")));
            result.add(map);
    	}
    	
        return result;
        
    }
    /**
     * method name : getChargeHistoryTotalCount
     * method Desc : 고객 선불관리 화면의 충전 이력 리스트의 total count 를 조회한다.
     *
     * @param conditionMap
     * @return
     */
    public Integer getChargeHistoryTotalCount(Map<String, Object> conditionMap) {
        List<Map<String, Object>> result = prepaymentLogDao.getChargeHistory(conditionMap, true);
        
        return ((Long)result.get(0).get("total")).intValue();
    }

    /**
     * method name : getChargeHistoryDetailChartData
     * method Desc : 고객 선불관리 맥스가젯의 충전 이력의 상세 차트 데이터를 조회한다.
     *
     * @param conditionMap
     * @return
     */
    public List<Map<String, Object>> getChargeHistoryDetailChartData(Map<String, Object> conditionMap) {
        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
        String serviceType = (String)conditionMap.get("serviceType");

        if (serviceType.equals(MeterType.EnergyMeter.getServiceType())) {
            result = billingDayEMDao.getChargeHistoryBillingList(conditionMap);
        } else if (serviceType.equals(MeterType.GasMeter.getServiceType())) {
            result = billingDayGMDao.getChargeHistoryBillingList(conditionMap);
        } else if (serviceType.equals(MeterType.WaterMeter.getServiceType())) {
            result = billingDayWMDao.getChargeHistoryBillingList(conditionMap);
        }
        
        Supplier supplier = supplierDao.get((Integer)conditionMap.get("supplierId"));
        String lang = supplier.getLang().getCode_2letter();
        String country = supplier.getCountry().getCode_2letter();
        
        for (Map<String, Object> map : result) {
            map.put("yyyymmdd", TimeLocaleUtil.getLocaleDateByMediumFormat(((String)map.get("yyyymmdd")), lang, country));
        }

        return result;
    }

    /**
     * method name : changeEmergencyCreditMode
     * method Desc : 고객 선불관리 미니가젯에서 Credit Type 을 Emergency Credit Mode 로 전환한다.
     *
     * @param conditionMap
     */
    @Transactional
    public void changeEmergencyCreditMode(Map<String, Object> conditionMap) {

        String contractNumber = (String)conditionMap.get("contractNumber");
        Integer operatorId = (Integer)conditionMap.get("operatorId");

        Contract contract = contractDao.findByCondition("contractNumber", contractNumber);
        Code code = codeDao.getCodeIdByCodeObject("2.2.2");
//        Code code = codeDao.getCodeIdByCodeObject("2.2.0");
        String currentDate = DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMddHHmmss");

        // insert ContractChangeLog
        Operator operator = operatorDao.getOperatorById(operatorId);
        addContractChangeLog(contract, operator, "emergencyCreditStartTime", contract.getEmergencyCreditStartTime(), currentDate);
        addContractChangeLog(contract, operator, "creditType", (contract.getCreditType() != null) ? contract.getCreditType().getId() : null, (code != null) ? code.getId() : null);

        contract.setEmergencyCreditStartTime(currentDate);
        contract.setCreditType(code);

        contractDao.update(contract);
        
        // TODO - 다음 작업이 있을 경우 추가
    }

    /**
     * method name : getPrepaymentTariff
     * method Desc : 고객 선불관리 화면에서 요금단가를 조회한다.
     *
     * @param conditionMap
     * @return
     */
    public List<Map<String, Object>> getPrepaymentTariff(Map<String, Object> conditionMap){
        String serviceType = (String)conditionMap.get("serviceType"); // 미터유형(전기,가스,수도)
        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
        
        if (serviceType.equals(MeterType.EnergyMeter.getServiceType())) {
            result = tariffEMDao.getPrepaymentTariff(conditionMap);
        } else if (serviceType.equals(MeterType.GasMeter.getServiceType())) {   // TODO - 임시
            result = tariffEMDao.getPrepaymentTariff(conditionMap);
        } else if (serviceType.equals(MeterType.WaterMeter.getServiceType())) { // 임시
//            result.put("tariffWmCal", tariffWMCaliberDao.getChargeMgmtMaxDateList(conditionMap)); // -- 공급자 ID 필요...
            result = tariffEMDao.getPrepaymentTariff(conditionMap);
        }else{
            logger.info("Invalid Service Type Code.");
        }

        Supplier supplier = supplierDao.get((Integer)conditionMap.get("supplierId"));
        DecimalFormat df = DecimalUtil.getDecimalFormat(supplier.getMd());

        StringBuilder usage = new StringBuilder();
        
        for (Map<String, Object> map : result) {
            usage = new StringBuilder();
            if (!StringUtil.nullToBlank(map.get("condition1")).isEmpty() && !StringUtil.nullToBlank(map.get("supplySizeMin")).isEmpty()) {
                usage.append("u ").append(map.get("condition1")).append(" ").append(df.format((Double)map.get("supplySizeMin")));
            }

            if (usage.length() > 0 && !StringUtil.nullToBlank(map.get("condition2")).isEmpty() && !StringUtil.nullToBlank(map.get("supplySizeMax")).isEmpty()) {
                usage.append(" & ");
            }

            if (!StringUtil.nullToBlank(map.get("condition2")).isEmpty() && !StringUtil.nullToBlank(map.get("supplySizeMax")).isEmpty()) {
                usage.append("u ").append(map.get("condition2")).append(" ").append(df.format((Double)map.get("supplySizeMax")));
            }

            map.put("block", usage.toString());
            map.put("serviceCharge", df.format((Double)map.get("serviceCharge")));
            map.put("transmissionNetworkCharge", df.format((Double)map.get("transmissionNetworkCharge")));
            map.put("energyDemandCharge", df.format((Double)map.get("energyDemandCharge")));
            map.put("rateRebalancingLevy", df.format((Double)map.get("rateRebalancingLevy")));
        }
        
        return result;
    }

    /**
     * method name : addContractChangeLog
     * method Desc : ContractChangeLog 에 데이터 insert
     *
     * @param contract
     * @param operator
     * @param field
     * @param beforeValue
     * @param afterValue
     */
    private void addContractChangeLog(Contract contract, Operator operator, String field, Object beforeValue, Object afterValue) {
        // ContractChangeLog Insert
        ContractChangeLog contractChangeLog = new ContractChangeLog();

        contractChangeLog.setContract(contract);
        contractChangeLog.setCustomer(contract.getCustomer());
        contractChangeLog.setStartDatetime(DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMddHHmmss"));
        contractChangeLog.setChangeField(field);

        if (beforeValue == null) {
            contractChangeLog.setBeforeValue(null);
        } else {
            contractChangeLog.setBeforeValue(StringUtil.nullToBlank(beforeValue));
        }

        if (afterValue == null) {
            contractChangeLog.setAfterValue(null);
        } else {
            contractChangeLog.setAfterValue(StringUtil.nullToBlank(afterValue));
        }

        contractChangeLog.setOperator(operator);
        contractChangeLog.setWriteDatetime(DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMddHHmmss"));
//        contractChangeLog.setDescr(descr);

        contractChangeLogDao.add(contractChangeLog);
    }
}