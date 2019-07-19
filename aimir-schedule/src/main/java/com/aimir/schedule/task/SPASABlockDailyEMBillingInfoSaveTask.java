package com.aimir.schedule.task;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate4.HibernateTransactionManager;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.aimir.constants.CommonConstants.DefaultChannel;
import com.aimir.constants.CommonConstants.DeviceType;
import com.aimir.dao.device.EnergyMeterDao;
import com.aimir.dao.device.MeterDao;
import com.aimir.dao.mvm.BillingDayEMDao;
import com.aimir.dao.mvm.DayEMDao;
import com.aimir.dao.system.CodeDao;
import com.aimir.dao.system.ContractDao;
import com.aimir.dao.system.CustomerDao;
import com.aimir.dao.system.PrepaymentLogDao;
import com.aimir.dao.system.TariffEMDao;
import com.aimir.dao.system.TariffTypeDao;
import com.aimir.model.device.Meter;
import com.aimir.model.mvm.BillingDayEM;
import com.aimir.model.mvm.DayEM;
import com.aimir.model.system.Code;
import com.aimir.model.system.Contract;
import com.aimir.model.system.Customer;
import com.aimir.model.system.PrepaymentLog;
import com.aimir.model.system.TariffEM;
import com.aimir.model.system.TariffType;
import com.aimir.util.Condition;
import com.aimir.util.Condition.Restriction;
import com.aimir.util.DateTimeUtil;
import com.aimir.util.StringUtil;
import com.aimir.util.TimeUtil;

@Transactional
public class SPASABlockDailyEMBillingInfoSaveTask extends ScheduleTask{
	
	private static Log log = LogFactory.getLog(SPASABlockDailyEMBillingInfoSaveTask.class);
	
    private static final String SERVICE_TYPE_EM = "EM";
    private static final String SEARCH_DATE_TYPE_DAILY = "1";
	
	@Autowired
	HibernateTransactionManager transactionManager;
	
	@Autowired
	ContractDao contractDao;
	
	@Autowired
	CodeDao codeDao;

	@Autowired
	MeterDao meterDao;
	
	@Autowired
	DayEMDao dayEMDao;
	
	@Autowired
	TariffEMDao tariffEMDao;	
	
	@Autowired
	TariffTypeDao tariffTypeDao;
	
	@Autowired
	BillingDayEMDao billingDayEMDao;
	
	@Autowired
	EnergyMeterDao energyMeterDao;

	@Autowired
	PrepaymentLogDao prepaymentLogDao;
	
	@Autowired
	CustomerDao customerDao;
	
	private boolean isNowRunning = false;
	
	public void execute(JobExecutionContext context) {
		if(isNowRunning){
			log.info("########### SPASABlockDailyEMBillingInfoSaveTask is already running...");
			return;
		}
		isNowRunning = true;
		
	    log.info("########### START SPASABlockDailyEMBillingInfo ###############");
	    
        // 일별 전기 요금 정보 등록
        this.saveEmBillingDayInfo();
	    
	    log.info("########### END SPASABlockDailyEMBillingInfo ############");
	    isNowRunning = false;
	}//execute end
	 
    public void saveEmBillingDayInfo() {
    	 
    	// 전기 계약 정보 취득
        List<Integer> em_contractIds = this.getContractInfos(SERVICE_TYPE_EM); 

        for(Integer contract_id : em_contractIds) {
            log.info("\n ContractId[" + contract_id + "]");
        	Contract contract = contractDao.get(contract_id);
        	Code code = codeDao.get(contract.getCreditTypeCodeId());
        	if( contract.getTariffIndexId() != null ) {
        		TariffType tariffType = tariffTypeDao.get(contract.getTariffIndexId());
        		Map<String, Object> condition = new HashMap<String, Object>();
        		condition.put("tariffIndex", tariffType);
        		List<TariffEM> emList = tariffEMDao.getApplyedTariff(condition);
        		if ( emList != null && emList.size() > 0) {
		        	if(Code.PREPAYMENT.equals(code.getCode()) || Code.EMERGENCY_CREDIT.equals(code.getCode())) { // 선불 요금일 경우
		        		this.saveEmBillingDailyWithTariffEMUnitCost(contract);
		        	}
        		}
        	}
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

		// 전기 
		contractIds = dayEMDao.getContractIds(conditionMap);

		return contractIds;
    }
	
    /**
     * SPASA 선불요금에 적용되는 요금계산
     * 단가 방식으로 사용량의 요금을 계산한다.
     * 
     * @param contract
     * @return
     */
    @Transactional(rollbackFor=Exception.class, propagation=Propagation.REQUIRES_NEW)
    private Double saveEmBillingDailyWithTariffEMUnitCost(Contract contract) {
    	log.info("\n start contractId [" + contract.getId()+"]");
        Double dataValue[] = null;
        Double bill = 0d;
        // 정확한 숫자계산을 위해 BigDecimal 사용
        BigDecimal bdBill = null;                   // 사용요금
        BigDecimal bdCurBill = null;                // 기존요금
        BigDecimal bdSumBill = new BigDecimal(0d);  // 사용요금의 합
        BigDecimal bdUsage = null;                   // 사용량
        BillingDayEM _billingDayEM = null;
        String saveReadFromDateYYYYMMDDHHMMSS = null;
        String saveReadToDateYYYYMMDDHHMMSS = null;
        String newReadFromDateYYYYMMDDHHMMSS = null;    // 마지막 일자의 새로 읽을 일자시간
        Boolean isBegin = false;
        
        // 미터시리얼 번호를 취득한다.
        Meter meter = meterDao.get(contract.getMeterId());
        String mdsId = (meter == null) ? null : meter.getMdsId();

        // 계약에 해당하는 요금 정보 취득
        String today = DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMdd").substring(0,8);
        
        List<TariffEM> tariffEMList = tariffEMDao.getNewestTariff(contract, today);
        if ( tariffEMList == null || tariffEMList.isEmpty() ) {
        	log.info("null tariff contractId: [" + contract.getId() + "]");
        }
        TariffEM tariffEM = null;
        
        if (tariffEMList != null && tariffEMList.size() > 0) {
            tariffEM = (TariffEM)tariffEMList.get(0);
        }
        
        BigDecimal basicCharge = BigDecimal.valueOf(StringUtil.nullToDoubleZero(tariffEM.getServiceCharge()));
        BigDecimal demandCharge = BigDecimal.valueOf(StringUtil.nullToDoubleZero(tariffEM.getEnergyDemandCharge()));
        BigDecimal totalBasicCharge = new BigDecimal(0);
        BigDecimal totalDemandCharge = new BigDecimal(0);
        
        // 가장 최근에 갱신한 일별 빌링 정보 취득
        Map<String, Object> billingEMMap = billingDayEMDao.getLast(contract.getId());

        BillingDayEM billingDayEM = new BillingDayEM();
        billingDayEM.setContract(contract);

        // TODO - 미터기의 Element 를 조회한다. 단상/3상에 따라 요금이 달라짐. 추후 재확인
        //EnergyMeter energyMeter = (EnergyMeter)contract.getMeter();
        //EnergyMeter energyMeter = energyMeterDao.get(contract.getMeterId());
        //Code element = energyMeter.getMeterElement();
        // 코드를 체크해서 단상일 경우/3상일 경우를 분기한다. 미개발

        try{
            // 사용량 읽은 마지막 날짜 취득
            String readToDate = TimeUtil.getCurrentDay() + "000000";
            newReadFromDateYYYYMMDDHHMMSS = TimeUtil.getCurrentDay() + "000000";

            if(billingEMMap != null) { // 빌링에 정보가 없을때는 가장 최근의(오늘) DayEM으로 부터 빌링정보를 등록한다.
                // 사용량 읽은 마지막 날짜 취득
                if((String)billingEMMap.get("usageReadToDate") != null) {
                    readToDate = (String)billingEMMap.get("usageReadToDate");
                    newReadFromDateYYYYMMDDHHMMSS = TimeUtil.getPreHour(readToDate, -1);
                } else {
                    // 사용량 읽은 마지막 날짜가 null 일 경우 모두 읽은 것으로 간주함.
                    readToDate = (String)billingEMMap.get("lastDay") + "230000";
                    newReadFromDateYYYYMMDDHHMMSS = TimeUtil.getPreHour(readToDate, -1);
                    isBegin = true;
                }
            }

            // 일별 에너지 사용량 취득(빌링정보 저장한 다음 시간부터 일별 사용량을 취득한다.)
            log.info(" \n dayEM's readTodate: " + readToDate
            	+"\n newReadFromDate: " + newReadFromDateYYYYMMDDHHMMSS);
            Set<Condition> param = new HashSet<Condition>();
            param.add(new Condition("id.mdevType", new Object[]{DeviceType.Meter}, null, Restriction.EQ));
            param.add(new Condition("id.mdevId", new Object[]{mdsId}, null, Restriction.EQ));
            param.add(new Condition("id.channel", new Object[]{DefaultChannel.Usage.getCode()}, null, Restriction.EQ));
            param.add(new Condition("id.yyyymmdd", new Object[]{readToDate.substring(0,8)}, null, Restriction.GE));
            param.add(new Condition("id.yyyymmdd", new Object[]{readToDate.substring(0,8)}, null, Restriction.ORDERBYDESC));
            List<DayEM> dayEMList = dayEMDao.getDayEMsByListCondition(param);

            String readToDateYYYYMMDD = readToDate.substring(0,8);                          // 마지막 읽은 일자
            String newReadFromDateHH = newReadFromDateYYYYMMDDHHMMSS.substring(8,10);       // 마지막 읽은 날의 새로 읽을 시간
            int intNewReadFromDateHH = Integer.parseInt(newReadFromDateHH);                 // 마지막 읽은 날의 새로 읽을 시간 int
            String saveReadFromDateHH = null;   // 저장할 읽은 시작시간
            String saveReadToDateHH = "23";     // 저장할 읽은 종료시간

            boolean flg = true;

            if (dayEMList.size() != 0) {
            	log.info("\n dayEMList Size:" +  dayEMList.size() );
            	
                for ( DayEM dayEM : dayEMList ) {
                    bdUsage = new BigDecimal(0d);
                    bdBill = new BigDecimal(0d);
                    dataValue =  this.getDayValue24(dayEM);
                    billingDayEM.setYyyymmdd(dayEM.getYyyymmdd());
                    List<BillingDayEM> list_billingDayEM = billingDayEMDao.getBillingDayEMs(billingDayEM, null, null);
                    _billingDayEM = (list_billingDayEM.size() != 0) ? list_billingDayEM.get(0) : null;
                    
                    if ( _billingDayEM != null ) {
                    	bdCurBill = new BigDecimal(StringUtil.nullToDoubleZero(_billingDayEM.getBill()));
                    } else {
                    	bdCurBill = new BigDecimal(0d);
                    }
                    
                    Map<String, Object> condition = new HashMap<String, Object>();

                    condition.put("yyyymmdd", dayEM.getYyyymmdd());
                    condition.put("mdsId", mdsId);
                    condition.put("mdevTypeCode", DeviceType.Meter.getCode());
                    
                    BillingDayEM billingEM = billingDayEMDao.getBillingDayEM(condition);
                    BigDecimal maxDemand = BigDecimal.valueOf(
                    		StringUtil.nullToDoubleZero(billingEM.getActiveEnergyImportRateTotal()));
                    
                    log.info("\n maxDemand:" + maxDemand.toString());
                    
                    // 마지막 읽은 날의 남은 시간에 대한 사용량을 더한다.
                    // 예) 10일 15시까지 읽었으면 이번에는 10일 16시의 사용량부터 계산하도록 하기 위해서
                    
                    if (readToDateYYYYMMDD.equals(dayEM.getYyyymmdd())) {
                        if ( !isBegin && intNewReadFromDateHH == 0) {
                            // 새로 읽을 시간이 0 이면 skip.(마지막 읽은 시간이 23시임)
                            continue;
                        }

                        
                        for (int j = 0; j < dataValue.length; j++) {
                            if (intNewReadFromDateHH <= j) {
                                if (flg) {
                                    saveReadFromDateHH = newReadFromDateHH;
                                    flg = false;
                                }

                                bdUsage = bdUsage.add(new BigDecimal(dataValue[j]));
                                totalBasicCharge.add(basicCharge); 
                                totalDemandCharge.add(maxDemand.multiply(demandCharge));
                            }
                        }
                        flg = true;
                        log.info("\n usage:" + bdUsage.toString() 
                        		+ "\n totalBasicCharge:" + totalBasicCharge.toString() 
                        		+ "\n totalDemandCharge: " + totalDemandCharge.toString());
                        saveReadFromDateYYYYMMDDHHMMSS = dayEM.getYyyymmdd() + saveReadFromDateHH + "0000";
                    } else { // 마지막 읽은 날짜와 같지 않을 경우는 전체 사용량을 읽는다.
                        saveReadFromDateYYYYMMDDHHMMSS = dayEM.getYyyymmdd() + "000000";
                        bdUsage = bdUsage.add(new BigDecimal(dayEM.getTotal()));
                        totalBasicCharge.add(basicCharge.multiply(BigDecimal.valueOf(24)));
                        totalDemandCharge.add(maxDemand.multiply(demandCharge).multiply(BigDecimal.valueOf(24)));
                        log.info("\n usage:" + bdUsage.toString() 
                        		+ "\n totalBasicCharge:" + totalBasicCharge.toString() 
                        		+ "\n totalDemandCharge: " + totalDemandCharge.toString());
                    }

                    for (int k = dataValue.length -1 ; k >= 0 ; k--) {
                        if (dataValue[k] != 0.0) {
                            saveReadToDateHH = (String.valueOf(k).length() == 1 ? "0"+k : String.valueOf(k)); // 마지막 시간 취득
                            break;
                        }
                    }

                    saveReadToDateYYYYMMDDHHMMSS = dayEM.getYyyymmdd() + saveReadToDateHH + "0000";
                    log.info("total usage: " + bdUsage);
                    
                    // 사용요금 계산
                    if (tariffEMList != null && !tariffEMList.isEmpty()) {
                        bdBill = bdUsage.multiply(new BigDecimal( getActiveEnergyCharge( tariffEMList, bdUsage.doubleValue()) ));
                        log.info("usage Bill:" + bdBill);
                    }

                    bdSumBill = bdSumBill.add(bdBill)	// 계산된 요금을 더한다.
                    		.add(totalBasicCharge) 
                    		.add(totalDemandCharge);
                    
                    // Billing_Day_Em에 정보 등록
                    if (_billingDayEM != null) {
                        _billingDayEM.setBill(bdCurBill.add(bdBill).doubleValue());
                        _billingDayEM.setUsageReadFromDate(saveReadFromDateYYYYMMDDHHMMSS);
                        _billingDayEM.setUsageReadToDate(saveReadToDateYYYYMMDDHHMMSS);
                    } else {
                        _billingDayEM = new BillingDayEM();
                        _billingDayEM.setYyyymmdd(saveReadToDateYYYYMMDDHHMMSS.substring(0,8));
                        _billingDayEM.setHhmmss("000000");
                        _billingDayEM.setMDevType(DeviceType.Meter.name());
                        _billingDayEM.setMDevId(mdsId);
                        _billingDayEM.setContract(contract);
                        _billingDayEM.setSupplier(contract.getSupplier());
                        _billingDayEM.setLocation(contract.getLocation());
                        _billingDayEM.setBill(bdCurBill.add(bdBill).doubleValue());
                        _billingDayEM.setActiveEnergyRateTotal(bdUsage.doubleValue());
                        _billingDayEM.setMeter(meter);
                        _billingDayEM.setModem((meter == null) ? null : meter.getModem());
                        _billingDayEM.setWriteDate(DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMddHHmmss"));
                        _billingDayEM.setUsageReadFromDate(saveReadFromDateYYYYMMDDHHMMSS);
                        _billingDayEM.setUsageReadToDate(saveReadToDateYYYYMMDDHHMMSS);
                    }

                    list_billingDayEM.clear();
                    billingDayEMDao.saveOrUpdate(_billingDayEM);
                    log.info(dayEM.getYyyymmdd() + " is complete");
                }

                // contract 정보 갱신
                Code code = codeDao.get(contract.getCreditTypeCodeId());
                if(Code.PREPAYMENT.equals(code.getCode()) || Code.EMERGENCY_CREDIT.equals(code.getCode())) { // 선불 요금일 경우
                    BigDecimal bdCurCredit = (contract.getCurrentCredit() != null) ? new BigDecimal(contract.getCurrentCredit()) : new BigDecimal(0d);
                    BigDecimal bdRemCredit = bdCurCredit.subtract(bdSumBill);
                    
                    log.info("ContractId[" + contract.getId() + "] contract has been changed "
                    		+ "\n previous balance: "+ bdCurCredit.toString()
                    		+ "\n charged amount: " + bdSumBill.toString()
                    		+ "\n remind balance: " + bdRemCredit.toString());
                    
                    // 현재 잔액에서 사용요금을 차감한다.
                    contract.setCurrentCredit(bdRemCredit.doubleValue());
                    contractDao.saveOrUpdate(contract);
                    
                    Customer customer = customerDao.get(contract.getCustomerId());

                    PrepaymentLog prepaymentLog = new PrepaymentLog();
                    prepaymentLog.setUsedConsumption(bdUsage.doubleValue());
                    prepaymentLog.setBalance(contract.getCurrentCredit());
                    prepaymentLog.setChargedCredit(Double.parseDouble("0"));
                    prepaymentLog.setLastTokenDate(TimeUtil.getCurrentTime());
                    prepaymentLog.setContract(contract);
                    prepaymentLog.setCustomer(customer);
                    prepaymentLog.setUsedCost(bdSumBill.doubleValue());
                    prepaymentLog.setLocation(contract.getLocation());
                    prepaymentLog.setTariffIndex(contract.getTariffIndex());
                    prepaymentLogDao.add(prepaymentLog);
                    
                    log.info("\n log id: " + prepaymentLog.getId());
                }
            }
        } catch(Exception e) {
        	log.error(e,e);
        }
        bill = bdSumBill.doubleValue();
        return bill;
    }
    
		  /**
	     * method name : getDayValue24
	     * method Desc : EM
	     *
	     * @param meteringMonth
	     * @return
	     */
	    private Double[] getDayValue24(DayEM dayEm) {

	        Double[] dayValues = new Double[24];

	        dayValues[0] = (dayEm.getValue_00() == null ? 0 : dayEm.getValue_00());
	        dayValues[1] = (dayEm.getValue_01() == null ? 0 : dayEm.getValue_01());
	        dayValues[2] = (dayEm.getValue_02() == null ? 0 : dayEm.getValue_02());
	        dayValues[3] = (dayEm.getValue_03() == null ? 0 : dayEm.getValue_03());
	        dayValues[4] = (dayEm.getValue_04() == null ? 0 : dayEm.getValue_04());
	        dayValues[5] = (dayEm.getValue_05() == null ? 0 : dayEm.getValue_05());
	        dayValues[6] = (dayEm.getValue_06() == null ? 0 : dayEm.getValue_06());
	        dayValues[7] = (dayEm.getValue_07() == null ? 0 : dayEm.getValue_07());
	        dayValues[8] = (dayEm.getValue_08() == null ? 0 : dayEm.getValue_08());
	        dayValues[9] = (dayEm.getValue_09() == null ? 0 : dayEm.getValue_09());
	        dayValues[10] = (dayEm.getValue_10() == null ? 0 : dayEm.getValue_10());
	        dayValues[11] = (dayEm.getValue_11() == null ? 0 : dayEm.getValue_11());
	        dayValues[12] = (dayEm.getValue_12() == null ? 0 : dayEm.getValue_12());
	        dayValues[13] = (dayEm.getValue_13() == null ? 0 : dayEm.getValue_13());
	        dayValues[14] = (dayEm.getValue_14() == null ? 0 : dayEm.getValue_14());
	        dayValues[15] = (dayEm.getValue_15() == null ? 0 : dayEm.getValue_15());
	        dayValues[16] = (dayEm.getValue_16() == null ? 0 : dayEm.getValue_16());
	        dayValues[17] = (dayEm.getValue_17() == null ? 0 : dayEm.getValue_17());
	        dayValues[18] = (dayEm.getValue_18() == null ? 0 : dayEm.getValue_18());
	        dayValues[19] = (dayEm.getValue_19() == null ? 0 : dayEm.getValue_19());
	        dayValues[20] = (dayEm.getValue_20() == null ? 0 : dayEm.getValue_20());
	        dayValues[21] = (dayEm.getValue_21() == null ? 0 : dayEm.getValue_21());
	        dayValues[22] = (dayEm.getValue_22() == null ? 0 : dayEm.getValue_22());
	        dayValues[23] = (dayEm.getValue_23() == null ? 0 : dayEm.getValue_23());

	        return dayValues;
	    }
	    
	    private Double getActiveEnergyCharge(List<TariffEM> tariffEMList, Double usage) {
	    	Double ret = new Double(0d);
	    	for ( TariffEM tariffEM : tariffEMList ) {
	    		Double min = (Double) ObjectUtils.defaultIfNull(tariffEM.getSupplySizeMin(), null) ;
	    		Double max = (Double) ObjectUtils.defaultIfNull(tariffEM.getSupplySizeMax(), null);
	    		
	    		if ( min != null && min <= usage ) {
	    			if ( max != null && max > usage) {
	    				ret = tariffEM.getActiveEnergyCharge();
	    				log.info("tariff min size: " + min);
	    	    		log.info("tariff max size: " + max);
	    			} else if (max == null ) {
	    				ret = tariffEM.getActiveEnergyCharge();
	    				log.info("tariff min size: " + min);
	    	    		log.info("tariff max size: " + max);
	    			}
	    		} else if (min == null) {
	    			ret = tariffEM.getActiveEnergyCharge();
	    			log.info("tariff min size: " + min);
	        		log.info("tariff max size: " + max);
	    		}
	    	}
	    	log.info("usage :" + usage);
    		log.info("tariff active-energy-charge: " + ret );
	    	return ret;
	    }
}