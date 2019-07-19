package com.aimir.schedule.task;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.aimir.dao.device.WaterMeterDao;
import com.aimir.dao.mvm.BillingDayEMDao;
import com.aimir.dao.mvm.BillingDayGMDao;
import com.aimir.dao.mvm.BillingDayWMDao;
import com.aimir.dao.mvm.BillingMonthEMDao;
import com.aimir.dao.mvm.BillingMonthGMDao;
import com.aimir.dao.mvm.BillingMonthWMDao;
import com.aimir.dao.mvm.DayEMDao;
import com.aimir.dao.mvm.DayGMDao;
import com.aimir.dao.mvm.DayHMDao;
import com.aimir.dao.mvm.DayWMDao;
import com.aimir.dao.mvm.MonthEMDao;
import com.aimir.dao.mvm.MonthGMDao;
import com.aimir.dao.mvm.MonthHMDao;
import com.aimir.dao.mvm.MonthWMDao;
import com.aimir.dao.mvm.SeasonDao;
import com.aimir.dao.system.ContractDao;
import com.aimir.dao.system.TOURateDao;
import com.aimir.dao.system.TariffEMDao;
import com.aimir.dao.system.TariffGMDao;
import com.aimir.dao.system.TariffWMCaliberDao;
import com.aimir.dao.system.TariffWMDao;
import com.aimir.model.system.Contract;
import com.aimir.util.DateTimeUtil;

@Transactional
@Deprecated
public class DailyBillingInfoSaveTask {

    protected static Log log = LogFactory.getLog(BalanceMonitorTask.class);
    private static final String SERVICE_TYPE_EM = "EM";
    private static final String SERVICE_TYPE_GM = "GM";
    private static final String SERVICE_TYPE_WM = "WM";
    private static final String SERVICE_TYPE_HM = "HM";
    private static final String SEARCH_DATE_TYPE_DAILY = "1";

    @Autowired
    ContractDao contractDao;

    @Autowired
    DayEMDao dayEmDao;

    @Autowired
    DayGMDao dayGmDao;

    @Autowired
    DayWMDao dayWmDao;

    @Autowired
    DayHMDao dayHmDao;

    @Autowired
    TariffEMDao tariffEMDao;

    @Autowired
    TariffGMDao tariffGMDao;

    @Autowired
    TariffWMDao tariffWMDao;

    @Autowired
    TariffWMDao tariffHMDao;
    
    @Autowired
    TariffWMCaliberDao tariffWMCaliberDao;

    @Autowired
    SeasonDao seasonDao;

    @Autowired
    WaterMeterDao waterMeterDao;

    @Autowired
    MonthEMDao monthEMDao;

    @Autowired
    MonthGMDao monthGmDao;

    @Autowired
    MonthWMDao monthWmDao;

    @Autowired
    MonthHMDao monthHmDao;

    @Autowired
    TOURateDao touRateDao;

    @Autowired
    BillingDayEMDao billingDayEMDao;
 
    @Autowired
    BillingDayGMDao billingDayGMDao;

    @Autowired
    BillingDayWMDao billingDayWMDao;
    
    @Autowired
    BillingMonthEMDao billingMonthEMDao;

    @Autowired
    BillingMonthGMDao billingMonthGMDao;

    @Autowired
    BillingMonthWMDao billingMonthWMDao;

    public void excute() {
        log.info("############################# Daily Billing Information Save Scheduler Start ##################################");

        // 일별 전기 요금 정보 등록
        this.saveEmBillingDayInfo();

        // 일별 가스 요금 정보 등록
//        this.saveGmBillingDayInfo();

        // 일별 수도 요금 정보 등록
        this.saveWmBillingDayInfo();

        log.info("############################# Daily Billing Information Save Scheduler End ##################################");
    }

    public void saveEmBillingDayInfo() {
 
    	// 전기 계약 정보 취득
        List<Integer> em_contractIds = this.getContractInfos(SERVICE_TYPE_EM); 

        for(Integer contract_id : em_contractIds) {
            log.info("ContractId[" + contract_id + "]");
        	Contract contract = contractDao.get(contract_id);
//        	tariffEMDao.saveEmBillingDailyWithTariffEM(contract);
        }
    }

    public void saveGmBillingDayInfo() {

		// 가스 계약 정보 취득
		List<Integer> gm_contractIds = this.getContractInfos(SERVICE_TYPE_GM);
        for(Integer contract_id : gm_contractIds) {
        	// 계약 정보 취득
        	Contract contract = contractDao.get(contract_id);
//        	tariffGMDao.saveGmBillingDayWithTariffGM(contract);
        }
    }

    private void saveWmBillingDayInfo() {

		// 수도 계약 정보 취득
		List<Integer> wm_contractIds = this.getContractInfos(SERVICE_TYPE_WM);
        for(Integer contract_id : wm_contractIds) {
        	// 계약 정보 취득
        	Contract contract = contractDao.get(contract_id);
//        	tariffWMDao.saveWMChargeUsingDailyUsage(contract);
        }
    }

    private List<Integer> getContractInfos(String serviceType){
    	List<Integer> contractIds = null;

        // 현재 일자를 취득한다.
		String today = DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMdd").substring(0,8);

		// 일별 사용량 취득 조건을 설정한다.
    	Map<String, String> conditionMap = new HashMap<String, String>();
//
		conditionMap.put("startDate", today);
		conditionMap.put("endDate", today);
		conditionMap.put("locationCondition", "");
		conditionMap.put("searchDateType", SEARCH_DATE_TYPE_DAILY); // 일별
		conditionMap.put("serviceType", serviceType);

		if("EM".equals(serviceType)) {  // 전기 
			contractIds = dayEmDao.getContractIds(conditionMap);
		} else if("GM".equals(serviceType)) { // 가스
			contractIds = dayGmDao.getContractIds(conditionMap);
		} else if("WM".equals(serviceType)) { // 수도
			contractIds = dayWmDao.getContractIds(conditionMap);
		} else if("HM".equals(serviceType)) { // 열량
			contractIds = dayHmDao.getContractIds(conditionMap);
		}

		return contractIds;
    }
}
