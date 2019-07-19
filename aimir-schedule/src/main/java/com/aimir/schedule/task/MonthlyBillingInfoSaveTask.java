package com.aimir.schedule.task;

import java.text.ParseException;
import java.util.HashMap;

import java.util.List;
import java.util.Map;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate4.HibernateTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;


import com.aimir.constants.CommonConstants.DefaultChannel;
import com.aimir.constants.CommonConstants.DeviceType;
import com.aimir.dao.device.MeterDao;
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
import com.aimir.dao.system.CodeDao;
import com.aimir.dao.system.ContractDao;
import com.aimir.dao.system.TOURateDao;
import com.aimir.dao.system.TariffEMDao;
import com.aimir.dao.system.TariffGMDao;
import com.aimir.dao.system.TariffTypeDao;
import com.aimir.dao.system.TariffWMCaliberDao;
import com.aimir.dao.system.TariffWMDao;
import com.aimir.model.device.Meter;
import com.aimir.model.mvm.BillingMonthEM;
import com.aimir.model.mvm.BillingMonthGM;
import com.aimir.model.mvm.BillingMonthWM;
import com.aimir.model.mvm.DayEM;
import com.aimir.model.mvm.DayGM;
import com.aimir.model.mvm.DayWM;
import com.aimir.model.system.Code;
import com.aimir.model.system.Contract;
import com.aimir.model.system.TariffEM;
import com.aimir.model.system.TariffGM;
import com.aimir.model.system.TariffType;
import com.aimir.model.system.TariffWM;
import com.aimir.model.system.TariffWMCaliber;
import com.aimir.util.BillDateUtil;
import com.aimir.util.DateTimeUtil;
import com.aimir.util.TimeUtil;

public class MonthlyBillingInfoSaveTask extends ScheduleTask {

    protected static Log log = LogFactory.getLog(BalanceMonitorTask.class);
    private static final String SERVICE_TYPE_EM = "EM";
    private static final String SERVICE_TYPE_GM = "GM";
    private static final String SERVICE_TYPE_WM = "WM";
    private static final String SERVICE_TYPE_HM = "HM";
    private static final String SEARCH_DATE_TYPE_MONTHLY = "3";

    @Autowired
    ContractDao contractDao;
    
    @Autowired
	MeterDao meterDao;
    
    @Autowired
    CodeDao codeDao;

    @Autowired
    DayEMDao dayEMDao;

    @Autowired
    DayGMDao dayGMDao;

    @Autowired
    DayWMDao dayWMDao;

    @Autowired
    DayHMDao dayHMDao;

    @Autowired
    TariffEMDao tariffEMDao;

    @Autowired
    TariffGMDao tariffGMDao;

    @Autowired
    TariffWMDao tariffWMDao;

    @Autowired
    TariffWMDao tariffHMDao;
    
    @Autowired
	TariffTypeDao tariffTypeDao;
    
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
    
    @Autowired
    HibernateTransactionManager transactionManager;

    @Override
    public void execute(JobExecutionContext context) {
        log.info("############################# Monthly Billing Information Save Scheduler Start ##################################");

//        System.out.println("Run Me ~");
//        // 일별 전기 요금 정보 등록
//        this.saveEmBillingDayInfo();
//
//        // 일별 가스 요금 정보 등록
//        this.saveGmBillingDayInfo();
//
//        // 일별 수도 요금 정보 등록
//        this.saveWmBillingDayInfo();

        // 월별 전기 요금 정보 등록
        this.saveEmBillingMonthInfo();

        // 월별 가스 요금 정보 등록
//        this.saveGmBillingMonthInfo();

        // 월별 수도 요금 정보 등록
        this.saveWmBillingMonthInfo();

        setSuccessResult();
        log.info("############################# Monthly Billing Information Save Scheduler End ##################################");
    }

    public void saveEmBillingMonthInfo() {

    	// 전기 계약 정보 취득
        List<Integer> em_contractIds = this.getContractInfos(SERVICE_TYPE_EM); 

        for(Integer contract_id : em_contractIds) {
        	Contract contract = contractDao.get(contract_id);
        	this.saveEmBillingMonthWithTariffEM(contract);
        }
    }

    public void saveGmBillingMonthInfo() {
		// 가스 계약 정보 취득
		List<Integer> gm_contractIds = this.getContractInfos(SERVICE_TYPE_GM);
        for(Integer contract_id : gm_contractIds) {
        	// 계약 정보 취득
        	Contract contract = contractDao.get(contract_id);
        	this.saveGmBillingMonthWithTariffGM(contract);
        }
    }
 
    private void saveWmBillingMonthInfo() {

		// 수도 계약 정보 취득
		List<Integer> wm_contractIds = this.getContractInfos(SERVICE_TYPE_WM);

        for(Integer contract_id : wm_contractIds) {
        	// 계약 정보 취득
        	Contract contract = contractDao.get(contract_id);
        	this.saveWMChargeUsingMonthlyUsage(contract);       
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
		conditionMap.put("searchDateType", SEARCH_DATE_TYPE_MONTHLY); // 일별
		conditionMap.put("serviceType", serviceType);

		if("EM".equals(serviceType)) {  // 전기 
			contractIds = dayEMDao.getContractIds(conditionMap);
		} else if("GM".equals(serviceType)) { // 가스
			contractIds = dayGMDao.getContractIds(conditionMap);
		} else if("WM".equals(serviceType)) { // 수도
			contractIds = dayWMDao.getContractIds(conditionMap);
		} else if("HM".equals(serviceType)) { // 열량
			contractIds = dayHMDao.getContractIds(conditionMap);
		}

		return contractIds;
    }
    
    public Double saveEmBillingMonthWithTariffEM(Contract contract) {
    	double usage = 0d;
    	Double bill = 0d;
        TransactionStatus txStatus = null;
        DefaultTransactionDefinition txDefine = new DefaultTransactionDefinition();
        txDefine.setIsolationLevel(TransactionDefinition.ISOLATION_READ_UNCOMMITTED);
        try{
	        txStatus = transactionManager.getTransaction(txDefine);
	    	
	        // 현재 일자를 취득한다.
	        String today = DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMdd").substring(0,8);
	        // 계약에 해당하는 요금 정보 취득
			TariffType tariffType = tariffTypeDao.get(contract.getTariffIndexId()); 
	    	Integer tariffTypeCode = tariffType.getCode();
			Map<String, Object> tariffParam = new HashMap<String, Object>();

			tariffParam.put("tariffTypeCode", tariffTypeCode);
			tariffParam.put("tariffIndex", tariffType);
			tariffParam.put("searchDate", today);
	    	List<TariffEM> tariffEMList = tariffEMDao.getApplyedTariff(tariffParam);
	       
			// 과금일 취득
	        String startDate = BillDateUtil.getBillDate(contract, today, -1);
	        // Month To Date취득
	        String endDate = BillDateUtil.getMonthToDate(contract, today, 1);

	    	// 월 사용량
	    	DayEM dayEM = new DayEM();
	        dayEM.setChannel(DefaultChannel.Usage.getCode());
	        dayEM.setContract(contract);
	        dayEM.setMDevType(DeviceType.Meter.name());

	        usage = dayEMDao.getDayEMsUsageMonthToDate(dayEM, startDate, endDate);

	        // 요금 정보 취득
	    	bill = this.getEMChargeUsingMonthUsage(contract, tariffEMList, usage);

	        // Billing_Month_EM 정보 등록
	    	Meter meter = meterDao.get(contract.getMeterId());
			String mdsId = (meter == null) ? null : meter.getMdsId();

	        BillingMonthEM billingMonthEM = new BillingMonthEM();
	        billingMonthEM.setYyyymmdd(endDate);
	        billingMonthEM.setHhmmss("000000");
			billingMonthEM.setMDevType(DeviceType.Meter.name());
	        billingMonthEM.setMDevId(mdsId);
			billingMonthEM.setContract(contract);
			billingMonthEM.setSupplier(contract.getSupplier());
			billingMonthEM.setLocation(contract.getLocation());
	        billingMonthEM.setBill(bill);
	        billingMonthEM.setActiveEnergyRateTotal(usage);
	        billingMonthEM.setMeter(meter);
	        billingMonthEM.setModem((meter == null) ? null : meter.getModem());
	        billingMonthEM.setWriteDate(DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMddHHmmss"));
	        billingMonthEM.setUsageReadFromDate(startDate + "000000");
	        billingMonthEM.setUsageReadToDate(DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMddHHmmss"));
	        billingMonthEMDao.saveOrUpdate(billingMonthEM);

	        transactionManager.commit(txStatus);
		}catch(Exception e) {
			e.printStackTrace();
			transactionManager.rollback(txStatus);
		} finally {

		}
        return bill;
    }
    
    
    private Double getEMChargeUsingMonthUsage(Contract contract, List<TariffEM> tariffEMList, Double usage) {
    	Double bill = 10000d;
        // 현재 일자를 취득한다.
		String today = DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMdd").substring(0,8);

		// 과금일 취득
        String startDate = BillDateUtil.getBillDate(contract, today, -1);
        // Month To Date취득
        String endDate = BillDateUtil.getMonthToDate(contract, today, 1);

        try{
			Code code = codeDao.get(contract.getCreditTypeCodeId());
        	int duration = TimeUtil.getDayDuration(startDate, endDate); //요금계산하는 실제 사용기간	
			if(tariffEMList == null || tariffEMList.size() == 0){
				//return 0d;
			} else {
				for(TariffEM tariffEM : tariffEMList){
					if(Code.POSTPAY.equals(code.getCode())) { // 후불 요금일 경우

						if(tariffEM.getSupplySizeMin() != null || tariffEM.getSupplySizeMax() != null){
							if(tariffEM.getSupplySizeMin() != null && tariffEM.getSupplySizeMin() >= usage) {
								bill = tariffEM.getEnergyDemandCharge() == null ? 0d : tariffEM.getEnergyDemandCharge();
								if(tariffEM.getActiveEnergyCharge()!= null){
									bill += usage*tariffEM.getActiveEnergyCharge();
								}
							}
							if(tariffEM.getSupplySizeMin() != null && tariffEM.getSupplySizeMin() >= usage && tariffEM.getSupplySizeMin() < usage) {
								bill = tariffEM.getEnergyDemandCharge();
								if(tariffEM.getActiveEnergyCharge()!= null){
									bill += usage*tariffEM.getActiveEnergyCharge();
								}
							}
							if(tariffEM.getSupplySizeMax() != null && tariffEM.getSupplySizeMin() != null && tariffEM.getSupplySizeMin() < usage) {
								bill = (tariffEM.getEnergyDemandCharge() == null ? 0d : tariffEM.getEnergyDemandCharge())*duration/30;
								if(tariffEM.getActiveEnergyCharge()!= null){
									bill += usage*tariffEM.getActiveEnergyCharge();
								}
							}
						}else{
							if(tariffEM.getSeason() != null){
								String seasonStart = startDate.substring(0,4)+tariffEM.getSeason().getSmonth() + tariffEM.getSeason().getSday();
								String seasonEnd = endDate.substring(0,4)+tariffEM.getSeason().getEmonth() + tariffEM.getSeason().getEday();
								
								if(tariffEM.getPeakType() != null){
									// Billing_day_em의 요금정보를 합산한다.
									Map<String, Object> billingDayEM = billingDayEMDao.getTotal(contract.getId(), startDate, endDate);
									bill = Double.valueOf((String)billingDayEM.get("totalBill"));
								}else{
									
									if(startDate.compareTo(seasonStart) >= 0 && endDate.compareTo(seasonEnd) <= 0){
										bill = usage*(tariffEM.getActiveEnergyCharge() == null ? 0d : tariffEM.getActiveEnergyCharge());
									}
								}
							}else{
	
								if(tariffEM.getActiveEnergyCharge() != null){
									bill = usage*tariffEM.getActiveEnergyCharge();
								}
							}
							bill += (tariffEM.getEnergyDemandCharge() == null ? 0d : tariffEM.getEnergyDemandCharge())*duration/30;
						}
					} else { // 선불 요금일 경우
						bill = bill +  usage*(tariffEM.getActiveEnergyCharge() == null ? 0d : tariffEM.getActiveEnergyCharge())
								+ usage*(tariffEM.getRateRebalancingLevy() == null ? 0d : tariffEM.getRateRebalancingLevy());
					}
				}
			}
        }catch(ParseException e) {
        	e.printStackTrace();
        }
        return bill;
    }
    
    public Double saveGmBillingMonthWithTariffGM(Contract contract) {
		Double bill = 10000d;
        TransactionStatus txStatus = null;
        DefaultTransactionDefinition txDefine = new DefaultTransactionDefinition();
        txDefine.setIsolationLevel(TransactionDefinition.ISOLATION_READ_UNCOMMITTED);
        try{
		    txStatus = transactionManager.getTransaction(txDefine);

		    // 현재 일자를 취득한다.
		    String today = DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMdd").substring(0,8);
	    	// 요금 정보 취득
		    TariffType tariffType = tariffTypeDao.get(contract.getTariffIndexId());
		    Integer tariffTypeCode = tariffType.getCode();

			Map<String,Object> tariffParam = new HashMap<String,Object>();

			tariffParam.put("tariffTypeCode", tariffTypeCode);
			tariffParam.put("searchDate", today);
			tariffParam.put("seasonId", seasonDao.getSeasonByMonth(today.substring(4, 6)).getId());
	    	TariffGM tariffGM = tariffGMDao.getApplyedTariff(tariffParam);
			Double usageUnitPrice = 0d;
			Double basicRate = tariffGM==null||tariffGM.getBasicRate()==null?0.0:tariffGM.getBasicRate();
			
			// 과금일 취득
	        String startDate = BillDateUtil.getBillDate(contract, today, -1);
	        // Month To Date취득
	        String endDate = BillDateUtil.getMonthToDate(contract, today, 1);
	
	    	DayGM dayGM = new DayGM();
	    	dayGM.setChannel(DefaultChannel.Usage.getCode());
	    	dayGM.setContract(contract);
	    	dayGM.setMDevType(DeviceType.Meter.name());
	
	    	Double usage = dayGMDao.getDayGMsUsageMonthToDate(dayGM, startDate, endDate);
	    	Code code = codeDao.get(contract.getCreditTypeCodeId());
	
	        // 선후불 요금제가 동일 할 경우는 if문에 의한 분기가 필요없다. 현재는 선불요금제가 명확하지 않아서 if문에 의해 분기를 해 놓은 상태임
	    	if(Code.POSTPAY.equals(code.getCode())) { // 후불 요금일 경우
	    		usageUnitPrice = tariffGM==null||tariffGM.getUsageUnitPrice()==null?0.0:tariffGM.getUsageUnitPrice();
	    		bill = usage * usageUnitPrice  + basicRate;
	    	} else { // 선불 요금일 경우
	    		usageUnitPrice = tariffGM==null||tariffGM.getUsageUnitPrice()==null?0.0:tariffGM.getUsageUnitPrice();
	    		bill = usage * usageUnitPrice;
	    	}
	
			// 빌링 정보 등록
	    	Meter meter = meterDao.get(contract.getMeterId());
			String mdsId = (meter == null) ? null : meter.getMdsId();
			
			BillingMonthGM billingMonthGM = new BillingMonthGM();
			billingMonthGM.setYyyymmdd(endDate);
			billingMonthGM.setHhmmss("000000");
			billingMonthGM.setBill(bill);
			billingMonthGM.setUsage(usage);
			billingMonthGM.setContract(contract);
			billingMonthGM.setSupplier(contract.getSupplier());
			billingMonthGM.setLocation(contract.getLocation());
			billingMonthGM.setMDevId(mdsId);
			billingMonthGM.setMDevType(DeviceType.Meter.name());
			billingMonthGM.setMeter(meter);
			billingMonthGM.setModem((meter == null) ? null : meter.getModem());
			billingMonthGM.setWriteDate(DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMddHHmmss"));
			billingMonthGM.setUsageReadFromDate(startDate + "000000");
			billingMonthGM.setUsageReadToDate(DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMddHHmmss"));
			billingMonthGMDao.saveOrUpdate(billingMonthGM);
	        transactionManager.commit(txStatus);
		}catch(Exception e) {
			e.printStackTrace();
			transactionManager.rollback(txStatus);
		} finally {

		} 
		return bill;
	}
    
    public Double saveWMChargeUsingMonthlyUsage(Contract contract) {
		Double bill = 10000d;
        TransactionStatus txStatus = null;
        DefaultTransactionDefinition txDefine = new DefaultTransactionDefinition();
        txDefine.setIsolationLevel(TransactionDefinition.ISOLATION_READ_UNCOMMITTED);
        try{
	        txStatus = transactionManager.getTransaction(txDefine);
	   	 	// 현재 일자를 취득한다.
			String today = DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMdd").substring(0,8);
			// 과금일 취득
	        String startDate = BillDateUtil.getBillDate(contract, today, -1);
	        // Month To Date취득
	        String endDate = BillDateUtil.getMonthToDate(contract, today, 1);

			int duration = TimeUtil.getDayDuration(startDate, endDate); //요금계산하는 실제 사용기간	
	
	    	// 요금 정보 취득
			TariffType tariffType = tariffTypeDao.get(contract.getTariffIndexId()); 
			Integer tariffTypeCode = tariffType.getCode();

			Map<String,Object> tariffParam = new HashMap<String,Object>();
			tariffParam.put("tariffTypeCode", tariffTypeCode); //
			tariffParam.put("searchDate", today);
	    	List<TariffWM> tariffWMList = tariffWMDao.getApplyedTariff(tariffParam);
			// 월 사용량 취득
	    	DayWM dayWM = new DayWM();
	    	dayWM.setChannel(DefaultChannel.Usage.getCode());
	    	dayWM.setContract(contract);
	    	dayWM.setMDevType(DeviceType.Meter.name());
			Double usage = dayWMDao.getDayWMsUsageMonthToDate(dayWM, startDate, endDate);
	
			Code code = codeDao.get(contract.getCreditTypeCodeId());

			if(Code.POSTPAY.equals(code.getCode())) { // 후불 요금일 경우
				//사용량구간별 요금계산
				for(TariffWM tariffWM:tariffWMList){
					if(tariffWM.getSupplySizeMax()!=null && tariffWM.getSupplySizeMax() <= usage){
						bill = bill +tariffWM.getUsageUnitPrice() * (tariffWM.getSupplySizeMax()-tariffWM.getSupplySizeMin()); 
					}else{
						bill = bill + tariffWM.getUsageUnitPrice() * (usage - tariffWM.getSupplySizeMin());
						break;
					}
				}

				Map<String, Object> param = new HashMap<String, Object>();
				param.put("supplierId", contract.getSupplierId());
				param.put("caliber", waterMeterDao.get(contract.getMeterId()).getMeterSize());
				
				// 구경별 기본요금  			
				bill = bill + Math.round((this.getTariffWMCaliber(contract) == null || this.getTariffWMCaliber(contract).getBasicRate() == null ? 0d : this.getTariffWMCaliber(contract).getBasicRate())*duration/30);
			} else { // 선불 요금일 경우
				for(TariffWM tariffWM:tariffWMList){
						bill = bill + usage*tariffWM.getUsageUnitPrice();
				}
			}

			// 빌링 정보 등록
			Meter meter = meterDao.get(contract.getMeterId());
			String mdsId = (meter == null) ? null : meter.getMdsId();
			
			BillingMonthWM billingMonthWM = new BillingMonthWM();
			billingMonthWM.setYyyymmdd(endDate);
			billingMonthWM.setHhmmss("000000");
			billingMonthWM.setBill(bill);
			billingMonthWM.setUsage(usage);
			billingMonthWM.setContract(contract);
			billingMonthWM.setSupplier(contract.getSupplier());
			billingMonthWM.setLocation(contract.getLocation());
			billingMonthWM.setMDevId(mdsId);
			billingMonthWM.setMDevType(DeviceType.Meter.name());
			billingMonthWM.setMeter(meter);
			billingMonthWM.setModem((meter == null) ? null : meter.getModem());
			billingMonthWM.setWriteDate(DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMddHHmmss"));
			billingMonthWM.setUsageReadFromDate(startDate + "000000");
			billingMonthWM.setUsageReadToDate(DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMddHHmmss"));
			billingMonthWMDao.saveOrUpdate(billingMonthWM); 
	        transactionManager.commit(txStatus);
		}catch(Exception e) {
			e.printStackTrace();
			transactionManager.rollback(txStatus);
		} finally {
		} 
		return bill;
	}
    
    private TariffWMCaliber getTariffWMCaliber(Contract contract) {
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("supplierId", contract.getSupplierId());
		param.put("caliber", waterMeterDao.get(contract.getMeterId()).getMeterSize());
		
		// 구경별 기본요금
		return tariffWMCaliberDao.getTariffWMCaliberByCaliber(param);  
    }
	
}
